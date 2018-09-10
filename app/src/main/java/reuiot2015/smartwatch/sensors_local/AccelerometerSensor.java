package reuiot2015.smartwatch.sensors_local;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import reuiot2015.smartwatch.sensors.Sensor;
import reuiot2015.smartwatch.sensors.Types;

/**
 * " Conceptually, this sensor provides you with acceleration data according to the following relationship:
 *  linear acceleration = acceleration - acceleration due to gravity. "
 *
 *  https://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-linear
 */
public class AccelerometerSensor extends Sensor {
    private SensorManager manager;
    private Context context;

    private android.hardware.Sensor sensor;
    private boolean isSensorRegistered = false;
    private int samplingRate;

    private SensorEventListener listener = new SensorEventListener() {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
                //Log.d("Accelerometer", "sensorEvent: " + sensorEvent.toString());
            update(
                    sensorEvent.values[0], // Acceleration along x-axis (excluding gravity) m/s^2
                    sensorEvent.values[1], // Acceleration along y-axis (excluding gravity) m/s^2
                    sensorEvent.values[2]  // Acceleration along z-axis (excluding gravity) m/s^2
            );
        }

        @Override
        public void onAccuracyChanged(android.hardware.Sensor sensor, int i) {}
    };

    /** Constructs an instance of Sensor.
     *
     * @param context The Android context object.
     * @param samplingRate The sampling rate (SensorManager.SENSOR_DELAY_*)
     */
    public AccelerometerSensor(Context context, int samplingRate) {
        super(
                "linear_accelerometer",
                new String[] {"x", "y", "z"},
                new Types[] {Types.Float, Types.Float, Types.Float}
        );

        this.manager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.context = context;
        this.samplingRate = samplingRate;
    }

    public void subscribe() {
        // If the sensor is already registered, unsubscribe first.
        if (isSensorRegistered) unsubscribe();

        // Get instance of Android sensor object.
        sensor = this.manager.getDefaultSensor(android.hardware.Sensor.TYPE_LINEAR_ACCELERATION);

        // Register the event listener.
        isSensorRegistered = this.manager.registerListener(listener, sensor, samplingRate, samplingRate + 100);
    }

    public void unsubscribe() {
        if (isSensorRegistered && sensor != null) {
            this.manager.unregisterListener(listener);
        }

        isSensorRegistered = false;
    }

    public void flush()
    {
        //manager.flush(listener);
    }
}
