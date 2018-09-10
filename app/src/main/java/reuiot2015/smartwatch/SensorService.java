package reuiot2015.smartwatch;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Button;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import reuiot2015.smartwatch.sensors.Sensor;
import reuiot2015.smartwatch.sensors.Types;
import reuiot2015.smartwatch.sensors_local.ButtonTouchSensor;
import reuiot2015.smartwatch.sensors_local.AccelerometerSensor;
import reuiot2015.smartwatch.sensors_local.LocationSensor;
import reuiot2015.smartwatch.sensors_msband.MSBandLinker;
import reuiot2015.smartwatch.sensors.Collector;
import reuiot2015.smartwatch.sensors_persistence.CSVSampleWriter;
import reuiot2015.smartwatch.sensors_persistence.CSVSampleWriterDL;
import reuiot2015.smartwatch.sensors_persistence.CSVSampleWriterNB;
import reuiot2015.smartwatch.sensors_persistence.SampleAccumulator;
import reuiot2015.smartwatch.sensors_persistence.SmartWatchValues;

/** This service maintains connections with sensors and does the data collection.
 *
 * @author Mario A. Gutierrez (mag262@txstate.edu)
 */
public class SensorService extends Service {
    private final static int ONGOING_NOTIFICATION_ID = 0x1;

    // Specific to Service methodology.
    private final IBinder binder = new LocalBinder();
    private final static boolean allowRebind = true; // Allow activities to unbind and re-bind.

    // Used for monitoring of the connection status.
    private final Set<ConnectionStatusListener> connectionStatusListeners = Collections.synchronizedSet(new HashSet<ConnectionStatusListener>());

    // Subject Information
    private SubjectInformation subjectInformation;
    private float currentBac = 0.0f;

    // These are specific to the sensors and application.
    private final MSBandLinker linker = new MSBandLinker(SensorService.this);
    private final Collector collector = new Collector(SmartWatchValues.SAMPLE_FREQUENCY); //was 31.25f in experiments
    private AccelerometerSensor androidAccelerometerSensor;
    private LocationSensor locationSensor;
    private ButtonTouchSensor isDrinkingSensor;

    // This is used to maintain connection with the sensors.
    private SensorCollectorThread thread = null;

    private final Object collectionMonitor = new Object();

    @Override
    public void onCreate() {

        // Set up a notification to keep the service in the foreground.
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Smart Watch")
                .setContentText("The sensor service is running.");

        // This is the intent to launch the main view.
        Intent notificationIntent = new Intent(this, MainActivity.class);

        // Make it a PendingIntent to grant higher permission to launch the Activity.
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendingIntent);

        // Set the service in the foreground with the created notification.
        startForeground(ONGOING_NOTIFICATION_ID, builder.build());

        this.androidAccelerometerSensor = new AccelerometerSensor(SensorService.this, SensorManager.SENSOR_DELAY_GAME);
        this.androidAccelerometerSensor.setAveraging(3, Sensor.AverageWeighting.LINEAR);
        //**this.locationSensor = new LocationSensor(SensorService.this);
        //**this.isDrinkingSensor = new ButtonTouchSensor("is_drinking");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Keep running until explicitly stopped (restart if killed).
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder; // Used as an interface to the service.
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return allowRebind;
    }

    @Override
    public void onDestroy() {
        stopCollection();
        linker.unsubscribe();
        linker.disconnect();
    }

    /** Checks if the collection thread is running.
     *
     * @return True if the collection thread is running, false otherwise.
     */
    public boolean isCollecting() {
        return this.thread != null && this.thread.isCollecting();
    }

    /** Starts collection if not running. */
    public void startCollection() {
        synchronized (collectionMonitor) { // Make sure someone doesn't try to stop collection.
            if (this.thread == null) {
                this.thread = new SensorCollectorThread();
                this.thread.start();

            }
        }
    }

    /** Stops collection if running. */
    public void stopCollection() {
        synchronized (collectionMonitor) { // Make sure someone doesn't try to start collection.
            if (this.thread != null) {
                this.thread.stopCollection();
                this.thread = null;
            }
        }
    }

    /** Registers a ConnectionStatusListener for monitoring connection status changes.
     *
     * @param listener The listener to add.
     */
    public void addConnectionStatusListener(ConnectionStatusListener listener) {
        this.connectionStatusListeners.add(listener);
    }

    /** Unregisters a ConnectionStatusListener.
     *
     * @param listener The listener to remove.
     */
    public void removeConnectionStatusListener(ConnectionStatusListener listener) {
        this.connectionStatusListeners.remove(listener);
    }

    /** Registers a CollectorSampleListener for monitoring collected samples.
     *
     * @param listener The listener to add.
     */
    public void addCollectorSampleListener(Collector.SampleListener listener) {
        this.collector.addListener(listener);
    }

    /** Unregisters a ConnectionStatusListener.
     *
     * @param listener The listener to remove.
     */
    public void removeCollectorSampleListener(Collector.SampleListener listener) {
        this.collector.removeListener(listener);
    }

    public void setIsDrinkingButton(Button button) {
        //***this.isDrinkingSensor.setButton(button);
        //***if (this.thread != null && this.thread.isCollecting()) this.isDrinkingSensor.subscribe();
    }

    /** Sets the subject information to use.
     *
     * @param information The subject information object.
     */
    public void setSubjectInformation(SubjectInformation information) {
        this.subjectInformation = information;
    }

    /** Sets the current BAC of the subject.
     *
     * @param bac The current BAC of the subject.
     */
    public void setCurrentBac(float bac) { this.currentBac = bac; }

    /** Grabs the current BAC of the subject.
     *
     * @return The current BAC of the subject.
     */
    public float getCurrentBac() { return this.currentBac; }

    /** Generates a list of names for each column in a sample of data.
     *
     * @param additional Additional dimensions to include.
     * @return The generated sample header.
     */
    public String[] generateSampleHeader(String... additional) {
        Sensor.SensorMetaData[] meta = this.collector.getMeta();
        int headerSize = additional.length; for (Sensor.SensorMetaData m : meta) headerSize += m.getDimension();
        String[] result = new String[headerSize];

        // Set the header names for the sensors.
        int i = 0; for (Sensor.SensorMetaData m : meta) for (int j = 0; j < m.getDimension(); ++j)
            result[i++] = m.getMainLabel() + "_" + m.getDimensionLabel(j);

        // Set the additional header names.
        for (String s : additional) result[i++] = s;

        return result;
    }

    /** Generates a sample given data from a collected sample.
     *
     * @param data The sensor sample data.
     * @param meta The respective sensor meta data.
     * @param additional Additional values to use.
     *
     * @return The generated sample string.
     */
    public String[] generateSampleString(Object[] data, Sensor.SensorMetaData[] meta, String... additional) {
        String[] result = new String[data.length + additional.length];

        // Set the sensor values (convert to string by appropriate type).
        int i = 0; for (Sensor.SensorMetaData m : meta)
            for (Types t :  m.getDimensionTypes()) { result[i] = t.asString(data[i]); ++i; }

        // Set the additional values.
        for (String s : additional) result[i++] = s;

        return result;
    }

    /** Grabs the SubjectInformation object in use.
     *
     * @return The current SubjectInformation object.
     */
    public SubjectInformation getSubjectInformation() {
        return this.subjectInformation;
    }

    /** Used to establish heart rate consent with the MS Band.
     *
     * @param activity The activity to use.
     */
    public void setConsentActivity(Activity activity) {
        this.linker.setConsentActivity(activity);
    }

    public class LocalBinder extends Binder {
        public SensorService getService() {
            return SensorService.this;
        }
    }

    /** This thread creates and attempts to maintain connection with MS Band smartwatch. */
    private class SensorCollectorThread extends Thread {
        boolean collecting = true;

        @Override
        public void run() {
            // Notify that collection is starting.
            for (ConnectionStatusListener l : connectionStatusListeners) l.onCollectionStarting();

            // Begin the main loop.
            while (!Thread.interrupted() && this.collecting) {
                // Try to connect to smartwatch until connected.
                while (!linker.connect()) {
                    try { Thread.sleep(1000);
                    } catch (InterruptedException e) { /** Nothing to do here. */ }
                }

                // Add the sensors to the data collector.
                collector.addSensor(androidAccelerometerSensor);
                collector.addSensors(linker);
                //***collector.addSensor(locationSensor);
                collector.addSensor(isDrinkingSensor);

                // Subscribe the sensors for monitoring.
                // TODO: subscription should be in collector.
                linker.subscribe();
                androidAccelerometerSensor.subscribe();
                //***locationSensor.subscribe();
                //***isDrinkingSensor.subscribe();

                // Begin collection from sensors.
                collector.begin();

                // Notify that collection was started.
                for (ConnectionStatusListener l : connectionStatusListeners) l.onCollectionStarted();

                String[] header = generateSampleHeader("timestamp", "bac_observed");

                // Construct a CSVSampleWriter to use for saving the samples to .csv file.
                final CSVSampleWriter csvSampleWriter;
                if (SmartWatchValues.isNBorSVM()) {
                     csvSampleWriter = new CSVSampleWriterNB(
                            header, "prediction_data.csv"
                    );
                } else {
                    csvSampleWriter = new CSVSampleWriterDL(
                            header, "prediction_data.csv"
                    );
                }


                // Setup the sample accumulator.
                SampleAccumulator.StorageConfig storageConfig = new SampleAccumulator.StorageConfig(
                        SampleAccumulator.StorageConfig.TriggerType.NUM_SAMPLES, SmartWatchValues.TRIGGER
                );
                final SampleAccumulator sampleAccumulator = new SampleAccumulator(storageConfig);
                sampleAccumulator.addSampleAccumulationListener(csvSampleWriter);
                sampleAccumulator.start();

                // Set a Collector.SampleListener to send new samples to the SampleAccumulator.
                Collector.SampleListener sampleListener = new Collector.SampleListener() {
                    @Override
                    public void onSampleReceived(Object[] sample, Sensor.SensorMetaData[] metaData, long timestamp) {
                        sampleAccumulator.enqueueSample(generateSampleString(sample, metaData, timestamp+"", getCurrentBac()+""));
                    }
                };
                collector.addListener(sampleListener);

                // Observe the linker connection.
                try {
                    while (linker.isConnected()) Thread.sleep(2000);
                    // If the while loop is exited, the connection was disrupted; notify status listeners.
                    for (ConnectionStatusListener l : connectionStatusListeners) l.onCollectionDisrupted();
                } catch (InterruptedException e) { /** Nothing to do here. */ }

                while (this.collecting) { androidAccelerometerSensor.flush();
                    /* Nothing to do here, just waiting. */ }

                sampleAccumulator.stopStorage(); // Kill the saving of the samples.
                csvSampleWriter.release();

                collector.removeListener(sampleListener);
                collector.clearSensors(); // Clear the sensors from the collector.
                androidAccelerometerSensor.unsubscribe();
                //locationSensor.unsubscribe(); // TODO: should be part of collector.
                //isDrinkingSensor.unsubscribe();
            }

            // Notify that collection has stopped.
            for (ConnectionStatusListener l : connectionStatusListeners) l.onCollectionStopped();
        }

        /** Checks if the thread is currently trying to collect data.
         *
         * @return True if collection thread is running, false otherwise.
         */
        public boolean isCollecting() {
            return this.collecting;
        }

        /** Stops the collection thread. */
        public void stopCollection() {
            this.collecting = false;
            interrupt();
        }
    }

    /** Used to monitor changes in the status of the collection thread. */
    public interface ConnectionStatusListener {
        /** Called when the collection thread is starting. */
        void onCollectionStarting();

        /** Called when the collection has begun. */
        void onCollectionStarted();

        /** Called if the collection is disrupted, because of a dropped connection or something. */
        void onCollectionDisrupted();

        /** Called when the collection has stopped. */
        void onCollectionStopped();
    }

    /** An immutable encapsulation of a subject's profile information. */
    public static class SubjectInformation {
        public final String name;
        public final String birthDate;
        public final String sex;
        public final int height_feet;
        public final int height_inches;
        public final int weight;
        public final String email;
        public final String phone;
        public final String uuid;

        public final String contact_name;
        public final String contact_email;
        public final String contact_phone;

        private SubjectInformation(Builder builder) {
            this.name = builder.name;
            this.birthDate = builder.dateBirth;
            this.sex = builder.sex;
            this.height_feet = builder.height_feet;
            this.height_inches = builder.height_inches;
            this.weight = builder.weight;
            this.email = builder.email;
            this.phone = builder.phone;
            this.uuid = builder.uuid;

            this.contact_name = builder.contact_name;
            this.contact_email = builder.contact_email;
            this.contact_phone = builder.contact_phone;
        }

        public void exportToDB(String uuid, String name, String dateBirth, String sex, int height_feet, int height_inches, int weight, String email, String phone, String contact_name, String contact_email, String contact_phone, SQLiteDatabase db) {


            SQLiteDatabase sqLiteDatabase = MainActivity.myDataBase.getWritableDatabase();
            MainActivity.dbManager.insertUserProfile(uuid, name, dateBirth, sex, height_feet, height_inches, weight, email, phone);
            MainActivity.dbManager.insertEmergencyContacts(contact_name, contact_email, contact_phone, uuid);

        }


        public static SubjectInformation importFromDB(String name) {

            Builder builder = new Builder();

            Cursor userCursor = MainActivity.dbManager.fetchUserInfo();
            Cursor emergencyCursor = MainActivity.dbManager.fetchEmergencyContacts();
            userCursor.moveToFirst();
            emergencyCursor.moveToFirst();

            do {

                if(name.equals(userCursor.getString(0))){

                    do {

                        if (name.equals(emergencyCursor.getString(3))) {


                            builder.setUUID(userCursor.getString(0));
                            builder.setName(userCursor.getString(1));
                            builder.setDateBirth(userCursor.getString(2));
                            builder.setSex(userCursor.getString(3));
                            builder.setHeightFeet(userCursor.getInt(4));
                            builder.setHeightInches(userCursor.getInt(5));
                            builder.setWeight(userCursor.getInt(6));
                            builder.setEmail(userCursor.getString(7));
                            builder.setPhone(userCursor.getString(8));
                            builder.setContactName(emergencyCursor.getString(0));
                            builder.setContactEmail(emergencyCursor.getString(1));
                            builder.setContactPhone(emergencyCursor.getString(2));
                            return builder.build();

                        }

                    } while (emergencyCursor.moveToNext());


                }

            }while (userCursor.moveToNext() && emergencyCursor.moveToNext());

            return null;

        }

        @Override
        public String toString() {
            return String.format("%s %s %s %d %d %d %s %s %s %s %s %s", name, birthDate, sex, height_feet, height_inches, weight, email, phone, uuid.substring(0, 8), contact_name, contact_email, contact_phone);
        }

        public static class Builder {

            private String name;
            private String dateBirth;
            private String sex;
            private int height_feet;
            private int height_inches;
            private int weight;
            private String email;
            private String phone;
            private String uuid;

            private String contact_name;
            private String contact_email;
            private String contact_phone;

            /** Sets the name.
             *
             * @param name The name of the subject.
             *
             * @return This builder.
             */
            public Builder setName(String name) { this.name = name; return this; }


            /** Gets the name of the subject.
             */
            public String getName() { return this.name; }


            /** Sets the birth date of the subject .
             *
             * @param dateBirth The birth date of the subject.
             *
             * @return This builder.
             */
            public Builder setDateBirth(String dateBirth) { this.dateBirth = dateBirth; return this; }


            /** Gets the name of the subject.
             */
            public String getDateBirth() { return this.dateBirth; }


            /** Sets the sex.
             *
             * @param sex The biological sex of the subject.
             *
             * @return This builder.
             */
            public Builder setSex(String sex) { this.sex = sex; return this; }


            /** Gets the name of the subject.
             */
            public String getSex() { return this.sex; }



            /** Sets the feet part of the height.
             *
             * @param height_feet The feet part of the height of the subject.
             *
             * @return This builder.
             */
            public Builder setHeightFeet(int height_feet) { this.height_feet = height_feet; return this; }



            /** Sets the inches part of the height.
             *
             * @param height_inches The inches part of the height of the subject.
             *
             * @return This builder.
             */
            public Builder setHeightInches(int height_inches) { this.height_inches = height_inches; return this; }



            /** Sets the weight of the subject.
             *
             * @param weight The weight of the subject.
             *
             * @return This builder.
             */
            public Builder setWeight(int weight) { this.weight = weight; return this; }



            /** Sets the email of the subject.
             *
             * @param email The email of the subject.
             *
             * @return This builder.
             */
            public Builder setEmail(String email) { this.email = email; return this; }



            /** Sets the phone of the subject.
             *
             * @param phone The phone number of the subject.
             *
             * @return This builder.
             */
            public Builder setPhone(String phone) { this.phone = phone; return this; }




            public Builder setUUID(String uuid) { this.uuid = uuid; return this; }

            /** Constructs a new SubjectInformation object from the given information.
             *
             * @return The new SubjectInformation object.
             */

            public SubjectInformation build() {
                return new SubjectInformation(this);
            }



            /** Sets the name of the emergency contact.
             *
             * @param contact_name The name of the emergency contact.
             *
             * @return This builder.
             */
            public Builder setContactName(String contact_name) { this.contact_name = contact_name; return this; }



            /** Gets the name of the emergency contact.
             */
            public String getContactName() { return this.contact_name; }



            /** Sets the email of the emergency contact.
             *
             * @param contact_email The email of the emergency contact.
             *
             * @return This builder.
             */
            public Builder setContactEmail(String contact_email) { this.contact_email = contact_email; return this; }



            /** Gets the email of the subject.
             */
            public String getContactEmail() { return this.contact_email; }


            /** Sets the phone of the emergency contact.
             *
             * @param contact_phone The phone number of the emergency contact .
             *
             * @return This builder.
             */
            public Builder setContactPhone(String contact_phone) { this.contact_phone = contact_phone; return this; }



            /** Gets the phone of the emergency contact.
             */
            public String getContactPhone() { return this.contact_phone; }
        }
    }
}