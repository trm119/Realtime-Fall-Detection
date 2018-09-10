package reuiot2015.smartwatch;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import reuiot2015.smartwatch.sensors.Collector;
import reuiot2015.smartwatch.sensors.Sensor;
import reuiot2015.smartwatch.sensors.Types;
import reuiot2015.smartwatch.sensors_persistence.SmartWatchValues;


public class MainActivity extends AppCompatActivity implements Collector.SampleListener, CollectionStoppedFragment.OnCollectionStartButtonClick, CollectionStartedFragment.OnCollectionStopButtonClick, ProfileSelectFragment.ProfileSelectionListener, ModelSelectFragment.ModelSelectionListener {

    private TextView console;   //Fragment in the screen that receives messages from the different actions taken in the application

    // Coralys Cubero
    BottomNavigationView bottomNavigation;      //Bottom menu that enables the user to go through the different pages
    private TextView safer_message;              //Message in the main activity that says 'Allow us to keep you safer'
    private TextView help_message;
    private TextView detection_on;
    private TextView detection_off;
    public static DatabaseHelper myDataBase;
    public static DBManager dbManager;
    public static String uuid;
    public static SQLiteDatabase db;
    public Button upload_button;

    public static EditText threshold;
    public static EditText num_steps;
    //experimental try for console -boden
    static TextView consoleStatic;
    public static volatile boolean flagFallCollect; // for collecting new data only

    //IP address for Production Server
    public static final String Server_IP = "eil.cs.txstate.edu";

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... urls) {

            try{

                URL url = new URL(urls[0]); //Here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("data_1", urls[1]);
                postDataParams.put("data_2", urls[2]);
                postDataParams.put("data_3", urls[3]);

                Log.e("doInBackground", "PostDataParams are: " + postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);         //15 seconds
                conn.setConnectTimeout(15000);      //15 seconds
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream connection_writer = conn.getOutputStream();       //Returns an output stream that writes to this connection

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection_writer, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                connection_writer.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {

                    BufferedReader connection_reader  = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer("");
                    String line = " ";

                    while((line = connection_reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }

                    connection_reader.close();
                    return sb.toString();

                }
                else {
                    Log.d("doInBackground", "Connection response code was: NOT OKAY" );
                }

            }
            catch(Exception e){
                Log.e("doInBackground", "There was an error: " + e.getMessage());
            }

            return "Datasent";

        }

        @Override
        protected void onPostExecute(String result) {

        }
    }


    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else {
                result.append("&");

            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append(" = ");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }

        return result.toString();
    }

    private boolean isNetworkConnected() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connec.getActiveNetworkInfo();

        if(activeNetwork != null){
            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){

                Toast.makeText(this, " Connected to WIFI", Toast.LENGTH_LONG).show();
                return true;
            }
        }

        else {

            Toast.makeText(this, " Not Connected to WIFI", Toast.LENGTH_LONG).show();
            return false;
        }

        return false;

    }


    public void uploadData(View v) {

        int time_interval = 180;

        Cursor raw_cursor = MainActivity.dbManager.fetchRawData();
        raw_cursor.moveToFirst();
        int first_timestamp = raw_cursor.getInt(22);
        int time_range = first_timestamp + time_interval;

        Cursor upload;

        //Read text from file
        StringBuilder raw_data = new StringBuilder();

        boolean checkInternet = isNetworkConnected();
        Toast.makeText(this, "Internet Status : " + checkInternet, Toast.LENGTH_LONG).show();

        if (!checkInternet) {
            Log.d("Upload Data", "No Internet Access... Please try again after sometime.");

        } else {

            try {

                while (raw_cursor.moveToNext()) {

                    if (time_range >= raw_cursor.getInt(22)) {
                        raw_data.append(raw_cursor.getString(0)).append(", ").append(raw_cursor.getString(1)).append(", ").append(raw_cursor.getString(2)).append(", ").append(raw_cursor.getString(3)).append(", ").append(raw_cursor.getString(4)).append(", ").append(raw_cursor.getString(5)).append(", ").append(raw_cursor.getString(6)).append(", ").append(raw_cursor.getString(7)).append(", ").append(raw_cursor.getString(8)).append(", ").append(raw_cursor.getString(9)).append(", ").append(raw_cursor.getString(10)).append(", ").append(raw_cursor.getString(11)).append(", ").append(raw_cursor.getString(12)).append(", ").append(raw_cursor.getString(13)).append(", ").append(raw_cursor.getString(14)).append(", ").append(raw_cursor.getString(15)).append(", ").append(raw_cursor.getString(16)).append(", ").append(raw_cursor.getString(17)).append(", ").append(raw_cursor.getString(18)).append(", ").append(raw_cursor.getString(19)).append(", ").append(raw_cursor.getString(20)).append(", ").append(raw_cursor.getString(21)).append(raw_cursor.getString(22)).append("\n");

                    } else {
                        time_range += 180;
                        raw_data.append(raw_cursor.getString(0)).append(", ").append(raw_cursor.getString(1)).append(", ").append(raw_cursor.getString(2)).append(", ").append(raw_cursor.getString(3)).append(", ").append(raw_cursor.getString(4)).append(", ").append(raw_cursor.getString(5)).append(", ").append(raw_cursor.getString(6)).append(", ").append(raw_cursor.getString(7)).append(", ").append(raw_cursor.getString(8)).append(", ").append(raw_cursor.getString(9)).append(", ").append(raw_cursor.getString(10)).append(", ").append(raw_cursor.getString(11)).append(", ").append(raw_cursor.getString(12)).append(", ").append(raw_cursor.getString(13)).append(", ").append(raw_cursor.getString(14)).append(", ").append(raw_cursor.getString(15)).append(", ").append(raw_cursor.getString(16)).append(", ").append(raw_cursor.getString(17)).append(", ").append(raw_cursor.getString(18)).append(", ").append(raw_cursor.getString(19)).append(", ").append(raw_cursor.getString(20)).append(", ").append(raw_cursor.getString(21)).append(raw_cursor.getString(22)).append("\n");

                    }
                }

                String raw_data_string = raw_data.toString();

                new SendPostRequest().execute("http://" + Server_IP + "/IOT/insertRawData.php", raw_data_string, "Raw_Data", subjectInformation.uuid);
                raw_data.setLength(0);

            } catch (Exception e) {
                Log.e("Upload Error", "There was an error uploading the raw data: " + e.getMessage());
            }

        }
    }


    private final static DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("EEE,  MM.dd.yy,  HH:mm:ss:SSS  zzz");
    private final static DateFormat FILENAME_SUFFIX = new SimpleDateFormat("_yy-MM-dd_HH-mm");


    private Sensor.SensorMetaData[] sensorMetaData;
    private Object[] sample;
    private final Object sampleLock = new Object();

    private final String[] consoleMessages = new String[5];
    private int consoleIndex = 0;

    public static SensorService.SubjectInformation subjectInformation;

    private SensorService.ConnectionStatusListener connectionStatusListener = new SensorService.ConnectionStatusListener() {
        @Override
        public void onCollectionStarting() {
            SmartWatchValues.STEPS = num_steps.getText().toString();
            SmartWatchValues.THRESHOLD_VALUE = threshold.getText().toString();
            updateConsole("Collection is starting...");
        }

        @Override
        public void onCollectionStarted() {
            updateConsole("Collection has started.");


            // Swap collection controls when collection has begun.
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_collection_controls, new CollectionStartedFragment())
                    .commit();

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_messages, new HelpMessageFragment())
                    .commit();

        }

        @Override
        public void onCollectionDisrupted() {
            updateConsole("Collection was disrupted.");
        }

        @Override
        public void onCollectionStopped() {
            updateConsole("Collection has stopped.");


            // Swap collection controls when collection has halted.
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_collection_controls, new CollectionStoppedFragment())
                    .commit();

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.home_messages, new SaferMessageFragment())
                    .commit();
        }
    };


    private SensorService service;
    private boolean bound = false;

    // Monitors the connection to the service.
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            SensorService.LocalBinder serviceBinder = (SensorService.LocalBinder) binder;
            service = serviceBinder.getService();
            bound = true;
            updateConsole("Bound to SensorService.");

            if (subjectInformation != null) service.setSubjectInformation(subjectInformation);
            else subjectInformation = service.getSubjectInformation();

            service.setConsentActivity(MainActivity.this);

            // Monitor the collected samples.
            service.addCollectorSampleListener(MainActivity.this);

            // Monitor the connection status of the service.
            service.addConnectionStatusListener(connectionStatusListener);

            // Make sure collection controls properly reflect status.
            if (service.isCollecting()) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_collection_controls, new CollectionStartedFragment())
                        .commit();
            } else {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container_collection_controls, new CollectionStoppedFragment())
                        .commit();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
            updateConsole("Unexpectedly lost connection with SensorService.");
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        consoleStatic = (TextView) findViewById(R.id.console);

        myDataBase = new DatabaseHelper(this);
        upload_button = (Button) findViewById(R.id.upload_button);


        //Let's make our items visible and so that we can interact with them
        this.safer_message = (TextView) findViewById(R.id.safer_message);
        this.help_message = (TextView) findViewById(R.id.help_message);
        this.console = (TextView) findViewById(R.id.console);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation_menu);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);

        threshold = (EditText) findViewById(R.id.threshold_value);
        num_steps = (EditText) findViewById(R.id.steps);

        Menu menu = bottomNavigation.getMenu();
        MenuItem menuItem= menu.getItem(0);
        menuItem.setChecked(true);


        // Initialize console strings.
        for (int i = 0; i < this.consoleMessages.length; ++i) this.consoleMessages[i] = "";

        // Set initial controls.
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container_collection_controls, new CollectionStoppedFragment())
                .commit();

        getFragmentManager()
                .beginTransaction()
                .add(R.id.home_messages, new SaferMessageFragment())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Bind to the SensorService
        Intent intent = new Intent(this, SensorService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        dbManager = new DBManager(getApplicationContext());
        dbManager.open();


    }


    /* Function that makes the Bottom Navigation Menu work. Depending on the choice made by the user, an action will be taken */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                //We're in the main activity so no action needs to be done
                case R.id.nav_home:
                    break;

                //Loads the activity profile and displays the information that was previously saved from the user
                case R.id.nav_profile:
                    Intent intent = new Intent(MainActivity.this, ProfileSettingsActivity.class);
                    if (subjectInformation != null) {
                        intent.putExtra(ProfileSettingsActivity.EXTRA_NAME, subjectInformation.name);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_BIRTHDATE, subjectInformation.birthDate);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_SEX, subjectInformation.sex);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_HEIGHT_FEET, subjectInformation.height_feet);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_HEIGHT_INCHES, subjectInformation.height_inches);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_WEIGHT, subjectInformation.weight);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_EMAIL, subjectInformation.email);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_PHONE, subjectInformation.phone);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_UUID, subjectInformation.uuid);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_CONTACT_NAME, subjectInformation.contact_name);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_CONTACT_EMAIL, subjectInformation.contact_email);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_CONTACT_PHONE, subjectInformation.contact_phone);

                    }
                    startActivity(intent);
                    break;

               case R.id.nav_settings:
                    Intent nextActivity2 = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(nextActivity2);
                    break;

            }

            return false;

        }

    };

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the SensorService.
        unbindService(serviceConnection);
        dbManager.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save_data) {
            if (bound && service != null) {
                SensorService.SubjectInformation subInfo = service.getSubjectInformation();

                if (subInfo == null)
                    updateConsole("Must set subject information first!");
                else {
                    File publicDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SmartWatchValues.ALBUM_NAME + "/data");
                    File current = new File(publicDirectory, "default.csv");
                    File future = new File(publicDirectory, subInfo.uuid.substring(0, 8) + FILENAME_SUFFIX.format(new Date(System.currentTimeMillis())) + ".csv");

                    if (current.renameTo(future)) updateConsole("Saved to " + future.getName());
                    else updateConsole("Failed to save new file.");
                }

            } else updateConsole("Cannot get information from service!");

        } else if (id == R.id.action_load_profile) {

            Cursor cursor = dbManager.fetchUserInfo();

            if (cursor == null)
                Toast.makeText(this, "There are no profiles saved.", Toast.LENGTH_SHORT).show();

            else {

                int i = 0;
                String[] names = new String[cursor.getCount()];
                while (cursor.moveToNext()) {

                    SensorService.SubjectInformation subjectInformation = new SensorService.SubjectInformation.Builder()
                            .setUUID(cursor.getString(0))
                            .setName(cursor.getString(1))
                            .setDateBirth(cursor.getString(2))
                            .setSex(cursor.getString(3))
                            .setHeightFeet(cursor.getInt(4))
                            .setHeightInches(cursor.getInt(5))
                            .setWeight(cursor.getInt(6))
                            .setEmail(cursor.getString(7))
                            .setPhone(cursor.getString(8))
                            .build();
                    names[i] = subjectInformation.uuid;
                    i += 1;
                }

                ProfileSelectFragment dialog = new ProfileSelectFragment();
                dialog.setProfiles(names);
                dialog.show(getFragmentManager(), "ProfileSelectFragment");
            }




        } else if (id == R.id.model_select) {

            String [] model_names = {"fallnaivebayesmodel.model",
                    "fallsvmmodel.model",
                    "frozen_tfdroidRNN12_138.pb",
                    "frozen_RNN_ModelFullADL_30label_45s_1-30n_50e.pb",
                    "frozen_RNN_ModelFullADL_30label_45s_1-25n_80e.pb",
                    "frozen_Model_Best_0.3thresh_45steps.pb",
            "frozen_RNN_ModelFullADL_25label_52s_1-20n_1.0kp_150e.pb",
            "frozen_RNN_ModelFullADL_25label_40s_1-30n_1.0kp_40e.pb",
            "frozen_RNN_ModelFullADL_25label_40s_1-30n_1.0kp_50e.pb",
            "frozen_RNN_ModelFullADL_25label_35s_1-25n_1.0kp_30e.pb",
            "frozen_RNN_ModelFullADL_25label_35s_1-25n_1.0kp_60e.pb",
            "frozen_RNN_ModelFullADL_25label_35s_1-25n_1.0kp_130e.pb",
            "frozen_RNN_ModelFullADL_25label_35s_1-25n_1.0kp_200e.pb",
            "frozen_RNN_ModelFullADL_25label_45s_1-20n_1.0kp_130e.pb",
            "frozen_RNN_ModelFullADL_25label_45s_1-20n_1.0kp_190e.pb",
            "frozen_RNN_ModelFullADL_25label_55s_1-25n_0.95kp_470e.pb",
            "frozen_RNN_ModelFullADL_25label_42s_1-20n_1.0kp_490e.pb",
            "frozen_RNN_ModelFullADL_25label_42s_1-25n_0.95kp_490e.pb",
            "frozen_RNN_ModelFullADL_25label_65s_1-20n_0.95kp_400e.pb",
            "frozen_RNN_ModelFullADL_25label_90s_1-20n_1.0kp_490e.pb",
            "frozen_RNN_ModelFullADL_25label_32s_1-25n_1.0kp_490e.pb"};

            ModelSelectFragment dialog = new ModelSelectFragment();
            dialog.setProfiles(model_names);
            dialog.show(getFragmentManager(), "ModelSelectFragment");
        }


        else if (id == R.id.action_exit) {
            stopService(new Intent(this, SensorService.class));
            dbManager.close();
            finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (service != null) {
            service.removeConnectionStatusListener(connectionStatusListener);
            service.removeCollectorSampleListener(this);
        }

        super.onDestroy();
    }

    /**
     * Updates the console with a message.
     *
     * @param message The message to post.
     */
    private void updateConsole(String message) {
        synchronized (this.consoleMessages) {
            this.consoleIndex = ++this.consoleIndex % this.consoleMessages.length;
            this.consoleMessages[this.consoleIndex] = (message != null) ? message + "\n" : "\n";
            final StringBuilder sb = new StringBuilder();
            for (int i = this.consoleMessages.length - 1; i >= 0; --i)
                sb.append(this.consoleMessages[(this.consoleMessages.length + this.consoleIndex - i) % this.consoleMessages.length]);
            final String s = sb.toString();
            this.console.post(new Runnable() {
                @Override
                public void run() {
                    console.setText(s);
                }
            });
        }
    }


    /* static function for adjust console */
    public static void updateConsoleStatic(String str) {
        consoleStatic.setText(str);

    }

    @Override
    public void onCollectionStartButtonClick(Button button) {
        if (!bound) updateConsole("Not bound to service.");
        else {
            if (service != null && !service.isCollecting()) {
                if (service.getSubjectInformation() == null) {
                    updateConsole("Must set profile information first!");
                } else {
                    service.startCollection();


                }
            } else
                updateConsole("Service interface is missing!");
        }
    }

    @Override
    public void onCollectionStopButtonClick(Button button) {
        if (!bound) updateConsole("Not bound to service.");
        else {
            if (service != null && service.isCollecting()) {
                service.stopCollection();
            } else
                updateConsole("Service interface is missing!");
        }
    }


    @Override
    public void onSampleReceived(Object[] sample, Sensor.SensorMetaData[] metaData, final long timestamp) {
        synchronized (sampleLock) {
            this.sensorMetaData = metaData;
            this.sample = sample;
        }

    }

    @Override
    public void profileSelected(String name) {
        this.subjectInformation = SensorService.SubjectInformation.importFromDB(name);
        if (bound && service != null) {
            service.setSubjectInformation(this.subjectInformation);

            updateConsole(name + " loaded. Service updated.");
        } else {
            updateConsole(name + " loaded. Will update service on bind.");
        }

    }

    public void modelSelected(String name) {
        SmartWatchValues.setModelName(name);
        updateConsole(name + "loaded.");
    }
}

