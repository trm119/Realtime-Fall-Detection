package reuiot2015.smartwatch;

/**
 * Created by Brock on 7/6/2016.
 *
 * The prediction class is used to predict rather or not someone has fallen. It uses weka and libsvm
 * to predict. Groupings of samples are predicted at one time to make sure every instance is classified.
 *
 */

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.InputStream;

import reuiot2015.smartwatch.sensors_persistence.SmartWatchValues;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class predictionNB {
    private boolean fall = false;  //used to keep track if any of the instances are a fall.
    private static int start = 0;  //used to keep track of where in the csv file to start predicting from
    private int inarow =  0;
    private int inarow_zero = 0;
    private int numFall = 0;

    private final Context context;
    private final String MODEL_FILE = SmartWatchValues.getModelName();


    //Context passed from main activity so this class can access the model in the assets folder.
    //countSet is passed to the class so the prediction method knows how many new rows are in the csvfile.
    public predictionNB(Context contextt) {
        this.context = contextt;
    }


    public void predict(Context c,int countSet){
        System.out.printf("IN PREDICT NB");
        File drinkingdata = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                SmartWatchValues.ALBUM_NAME + "/data/prediction_data.csv");// + "/default.csv");
        Log.d("fallprediction", "Save path is: " + drinkingdata.getAbsolutePath());

        //New structures -boden
        AssetManager am = c.getAssets();
        InputStream is = null;
        Classifier newsvm = null;
        int start_new = 0;
        int start_overlap = 0;
        int end_overlap = 0;
        int overlap_radius = SmartWatchValues.OVERLAP_RADIUS;    //The amount of data for overlap prediction is radius *2

        //Open model file
        try {
            is = am.open(MODEL_FILE);
            newsvm = (Classifier) weka.core.SerializationHelper.read(is);
        } catch (Exception e) {
            Log.d("Testing Prediction", "testing fail!!!!!!!!!!!!");
        }

        try {
            DataSource source = new DataSource(drinkingdata.getAbsolutePath());
            Instances predictiondata = source.getDataSet();                      //getting csv to predict from
            Log.d("fallprediction", predictiondata.toSummaryString());
            //Log.d("fallprediction", source.getDataSet().toString());      //give you whole set of data in this loop
            Log.d("fallprediction", start + " to " + predictiondata.numInstances());
            Log.d("fallprediction", "this many "+ countSet);

            try {
                fall = false;
                numFall = 0;

                //new approach to decide the start point - Boden
                start_new = predictiondata.numInstances() - countSet + 30;
                if (start_new < 0) { start_new = 0; }
                //Log.e("prediction", "start = " + start);
                Log.e("prediction", "start_new is " + start_new + " ,end  is " + predictiondata.numInstances());

                //for every new group of samples we receive we will predict each instance
                for (int i = start_new; i < predictiondata.numInstances(); ++i) {
                    //set which column will be used for prediction
                    if (predictiondata.classIndex() == -1)
                        predictiondata.setClassIndex(predictiondata.numAttributes() - 1);

                    Instance newinst = predictiondata.instance(i);
                    //       Log.d("fall predict newinst",newinst.toString());
                    try
                    {
                        if (c == null)
                            Log.d("fallprediction", "not getting context");
                        if (is == null)
                            Log.d("fallprediction", "not opening model");
                        if (is == null)
                            Log.d("fallprediction", "not reading in model");
                     /*   else
                            Log.d("fallprediction",newsvm.toString());*/

                        //predict instance
                        double prediction = newsvm.classifyInstance(newinst);
                        if (is == null)
                            Log.d("fallprediction", "having trouble predicting");

                        //get string representation of prediction
                        String predictionoutput = predictiondata.classAttribute().value((int) prediction);
                        if (predictionoutput.equals(""))
                            Log.d("prediction", "trouble converting to string");
                        Log.d("prediction", "*****This was predicted:  " + predictionoutput);

                        //test if prediction was fall, if so set the final prediction to fall.
                        if (predictionoutput.equals("fall") || predictionoutput.equals(" fall") || predictionoutput.equals("fall "))
                        {
                            ++inarow;
                            if ( (inarow >= 3 && inarow <= 50) && (i == predictiondata.numInstances() - 1) ) {
                                fall = true;
                                inarow_zero = 0;
                                numFall = inarow;
                                Log.d("prediction -Normal loop", "----------------------------------------------------------------------------Fall detected at " + i);
                            }
                        } else {
                            if (inarow >= 3 && inarow <= 50) {
                                if (inarow_zero < 2) {
                                    inarow_zero++;
                                }
                                else if (i == predictiondata.numInstances() - 1) {
                                    fall = true;
                                    inarow_zero = 0;
                                    numFall = inarow;
                                    inarow = 0;
                                    Log.d("prediction -Normal loop", "----------------------------------------------------------------------------Fall detected at " + i);
                                } else {
                                    fall = true;
                                    inarow_zero = 0;
                                    numFall = inarow;
                                    inarow = 0;
                                    Log.d("prediction -Normal loop", "----------------------------------------------------------------------------Fall detected at " + i);
                                }
                            } else {
                                inarow_zero = 0;
                                inarow = 0;
                            }
                        }

                    } catch (Exception e) {
                        Log.d("Prediction", "could not find model");
                    }
                }


                // Overlap prediction
                inarow =  0;
                inarow_zero = 0;

                if (start_new - overlap_radius > 0)
                {
                    start_overlap = start_new - overlap_radius;
                }
                end_overlap = start_new + overlap_radius;

                Log.d("prediction -Overlap", "start the second for loop");
                Log.d("prediction -Overlap", "From " + Integer.toString(start_overlap) +
                        " to " + Integer.toString(predictiondata.numInstances()));

                for (int i = start_overlap; i < end_overlap; ++i) {
                    //set which column will be used for prediction
                    if (predictiondata.classIndex() == -1)
                        predictiondata.setClassIndex(predictiondata.numAttributes() - 1);
                    Instance newinst = predictiondata.instance(i);
                    try
                    {
                        if (c == null)
                            Log.d("fallprediction", "not getting context");
                        if (is == null)
                            Log.d("fallprediction", "not opening model");
                        if (is == null)
                            Log.d("fallprediction", "not reading in model");

                        //predict instance
                        double prediction = newsvm.classifyInstance(newinst);
                        if (is == null)
                            Log.d("fallprediction", "having trouble predicting");
                        //get string representation of prediction
                        String predictionoutput = predictiondata.classAttribute().value((int) prediction);
                        if (predictionoutput.equals(""))
                            Log.d("fallprediction", "trouble converting to string");
                        Log.d("prediction -ovlp","*****This was predicted: " + predictionoutput);



                        //heuristic function
                        //test if prediction was fall, if so set the final prediction to fall.
                        if (predictionoutput.equals("fall") || predictionoutput.equals(" fall") || predictionoutput.equals("fall "))
                        {
                            ++inarow;
                            if ( (inarow >= SmartWatchValues.THRESHOLD_LOW && inarow <= SmartWatchValues.THRESHOLD_HIGH) && (i == predictiondata.numInstances() - 1) )
                            {
                                fall = true;
                                inarow_zero = 0;
                                numFall = inarow;
                                Log.d("prediction -Overlap lop", "----------------------------------------------------------------------------Fall detected at " + i);
                            }
                        }
                        else
                        {
                            if (inarow >= SmartWatchValues.THRESHOLD_LOW && inarow <= SmartWatchValues.THRESHOLD_HIGH)
                            {
                                if (inarow_zero < 2)
                                {
                                    inarow_zero++;
                                }
                                else
                                {
                                    fall = true;
                                    inarow_zero = 0;
                                    numFall = inarow;
                                    inarow = 0;
                                    Log.d("prediction -Overlap lop", "----------------------------------------------------------------------------Fall detected at " + i);
                                }
                            }
                            else
                            {
                                inarow_zero = 0;
                                inarow = 0;
                            }
                        }

                    } catch (Exception e) {
                        Log.d("Prediction", "could not find model");
                    }
                }
            }catch (Exception e) {
                Log.d("Prediction", "found source, trouble working with it.");
            }


            if (fall){
                Log.d("prediction", "***********************" + " Fall");

                final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
                tg.release();

                if (context != null){
                    new Handler(Looper.getMainLooper()).post(
                            new Runnable() {
                                public void run() {

                                    /*   code for sending notification goes here    **/


                                    Intent intentDialog = new Intent("android.intent.action.DIA");
                                    intentDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(intentDialog);
                                }
                            }
                    );
                }

                try {
                    MainActivity.consoleStatic.setText("\n Fall. Consecutive = "+ numFall +
                            " start = " + start_new  + " end = " + predictiondata.numInstances() +
                            " (" + start_overlap + " to " + end_overlap + ")" );
                } catch (Exception e){
                    Log.d("Final Prediction", "Trouble console text \n.");
                }
            }
            else {
                Log.d("***********************","NoFall");
                try{
                    MainActivity.consoleStatic.setText("\n No Fall. Consecutive = "+ numFall +
                            " start = " + start_new + " end = " + predictiondata.numInstances() +
                            " (" + start_overlap + " to " + end_overlap + ")"  );
                }catch (Exception e){
                    Log.d("Final Prediction", "Trouble console text \n.");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("Prediction","could not find data source" + drinkingdata.getAbsolutePath());
        }
    }
}
