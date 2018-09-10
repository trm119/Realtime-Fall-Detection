package reuiot2015.smartwatch.sensors_persistence;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;
import java.util.HashMap;

/** Some default persistence values and methods for use by SmartWatch. */
public class SmartWatchValues {

    // Default to DL

    // In both DL and NB
    private static String MODEL_NAME = "frozen_Model_Best_0.3thresh_45steps.pb";
    public static  String ALBUM_NAME = "SmartWatch Samples";
    public static  int TRIGGER = 125; //Number of data per prediction. Related to prediction interval.
    public static  int WINDOW_SIZE = 750; //unit: millisecond
    public static  float SAMPLE_FREQUENCY= 31.25f; //unit: (time/second)
    public static  int DATA_PER_WINDOW= 45; //How many data are compared at once
    public static  int REWRITE_INTERVAL= 10; //How many rounds of predictions for rewriting of default.csv to happen
    public static  int OVERLAP_RADIUS = 3;
    public static  int THRESHOLD_LOW= 3; //Thresholds for heuristic function
    public static  int THRESHOLD_HIGH = 50;

    // Unique to DL
    public static String THRESHOLD_VALUE = "0.3f";
    public static String STEPS = "45";
    public static TensorFlowInferenceInterface inferenceInterface = null;




    public static void setModelName(String name) {
        if (isNBorSVM(name)) {
            MODEL_NAME = name;
            ALBUM_NAME = "SmartWatch Samples";
            TRIGGER = 100;
            WINDOW_SIZE = 750;
            SAMPLE_FREQUENCY = 31.25f;
            DATA_PER_WINDOW = (int)(WINDOW_SIZE/(1000 / SAMPLE_FREQUENCY));
            REWRITE_INTERVAL = 10;
            OVERLAP_RADIUS = 3;
            THRESHOLD_LOW = 3;
            THRESHOLD_HIGH = 50;

            THRESHOLD_VALUE = null;
            STEPS = null;
            inferenceInterface = null;
        } else {
            MODEL_NAME = name;
            ALBUM_NAME = "SmartWatch Samples";
            TRIGGER = 125;
            WINDOW_SIZE = 750;
            SAMPLE_FREQUENCY = 31.25f;
            DATA_PER_WINDOW = 45;
            REWRITE_INTERVAL = 10;
            OVERLAP_RADIUS = 3;
            THRESHOLD_LOW = 3;
            THRESHOLD_HIGH = 50;

            THRESHOLD_VALUE = "0.3f";
            STEPS = "45";
            inferenceInterface = null;
        }
    }

    public static String getModelName() {
        return MODEL_NAME;
    }

    private static boolean isNBorSVM(String name) {
        System.out.print("IN IS NB OR SVM " );
        System.out.println(name.contains("naivebayes") || name.contains("svm"));
        return name.contains("naivebayes") || name.contains("svm");
    }

    public static boolean isNBorSVM() {
        return isNBorSVM(MODEL_NAME);
    }


}
