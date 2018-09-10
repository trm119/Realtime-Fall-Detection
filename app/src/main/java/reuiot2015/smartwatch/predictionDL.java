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
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import org.tensorflow.Operation;
import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.File;

import reuiot2015.smartwatch.sensors_persistence.CSVSampleWriterDL;
import reuiot2015.smartwatch.sensors_persistence.SmartWatchValues;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.PrintWriter;
import java.util.ArrayList;

public class predictionDL {
    private final String MODEL_FILE = "file:///android_asset/"+SmartWatchValues.getModelName();
    private TensorFlowInferenceInterface inferenceInterface;
    private float[] outputs;
    private String[] outputNames;
    private boolean fall = true;        //used to keep track if any of the instances are a fall
    private static int start = 0;       //used to keep track of where in the csv file to start predicting from
    private int numFall = 0;
    private File publicDirectory;
    private PrintWriter writer;



    private final Context context;


    //Context passed from main activity so this class can access the model in the assets folder.
    //countSet is passed to the class so the prediction method knows how many new rows are in the csvfile.
    public predictionDL(Context contextt) {
        this.context = contextt;
    }


    public void predict(Context c,int countSet){
        System.out.printf("IN PREDICT DL");
        File detection_data = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),SmartWatchValues.ALBUM_NAME + "/data/prediction_data.csv");
        Log.e("predict", "Saved path is: " + detection_data.getAbsolutePath());
        publicDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SmartWatchValues.ALBUM_NAME + "/data");


        if (SmartWatchValues.inferenceInterface == null)
            SmartWatchValues.inferenceInterface = new TensorFlowInferenceInterface(c.getAssets(), MODEL_FILE);
        inferenceInterface = SmartWatchValues.inferenceInterface;
        final Operation operation = inferenceInterface.graphOperation("Output");
        final int numClasses = (int) operation.output(0).shape().size(1);

        outputs = new float[numClasses];
        outputNames = new String[] {"Output"};
        final float threshold = Float.parseFloat(SmartWatchValues.THRESHOLD_VALUE);
        final int batch_size = 1;
        final int steps = Integer.parseInt(SmartWatchValues.STEPS);
        final int num_inputs = 3;
        final int hBatchSize = 10;
        float[] sampleInputs = new float[steps*num_inputs];


        String outputAsString = "Results: ";
        for (int i = 0; i < numClasses; i++) {
            Float outputi = outputs[i];
            outputAsString = outputAsString + ", " + outputi.toString();
        }

        //Open model file
        int start_new = 0;
        try {

            DataSource source = new DataSource(detection_data.getAbsolutePath());
            Instances prediction_data = source.getDataSet();                      //getting csv to predict from

            //ArrayList<Float> CSVSampleWriter.heuristics = new ArrayList<Float>();
            
            //Log.d("fallprediction", prediction_data.toSummaryString());
            //Log.d("fallprediction", source.getDataSet().toString());      //give you whole set of data in this loop
           // Log.d("fallprediction", start + " to " + prediction_data.numInstances());
           // Log.d("fallprediction", "this many "+ countSet);

            try {
                fall = false;
                numFall = 0;
                StringBuilder sb = new StringBuilder();
                StringBuilder lengths = new StringBuilder();
                lengths.append("\n");
                //new approach to decide the start point - Boden
                start_new = CSVSampleWriterDL.mStart_new;//prediction_data.numInstances() - countSet;
                if (CSVSampleWriterDL.renewed)
                    start_new = 0;
                if (start_new < 0) { start_new = 0; }




                Log.e("prediction", "start_new is: " + start_new + ", end is " + prediction_data.numInstances());
                CSVSampleWriterDL.mStart_new = prediction_data.numInstances();
                ArrayList<Float[]> samples = new ArrayList<Float[]>();

                //For every new group of samples we receive we will predict each instance
                //for (int i = start_new; i < prediction_data.numInstances(); ++i) {
                int i = start_new;
                while (i < prediction_data.numInstances()) {
                    //Set which column will be used for prediction
                    if (prediction_data.classIndex() == -1)
                        prediction_data.setClassIndex(prediction_data.numAttributes() - 1);

                    Instance newinst = prediction_data.instance(i);
                    double[] newinstDouble = newinst.toDoubleArray();
                    Float [] tempInputs = new Float[num_inputs];
                    for (int step = 0; step < num_inputs; step++) {
                        double val = newinstDouble[step];
                        tempInputs[step] = (float)val;
                    }
                    boolean adding = false;
                    if (i >= prediction_data.numInstances()-steps+1) {
                        CSVSampleWriterDL.lastData.add(tempInputs);
                        Log.e("prediction", "Adding to make size "+ CSVSampleWriterDL.lastData.size());

                        adding = true;
                    }
                    samples.add(tempInputs);
                    //StringBuilder inputs = new StringBuilder();
                    if (samples.size() == steps || (CSVSampleWriterDL.lastData.size() > 0 && samples.size() < steps && (start_new != 0 || CSVSampleWriterDL.renewed))) {
                        Log.e("prediction", "Samples size: "+samples.size());
                        Log.e("prediction", "Last data size: "+ CSVSampleWriterDL.lastData.size());
                        try
                        {

                            sb.append("\n").append("Batch: ").append(i).append("\n");
                            if (CSVSampleWriterDL.lastData.size() > 0 && samples.size() < steps) {
                                int loopStop = CSVSampleWriterDL.lastData.size();
                                if (i >= prediction_data.numInstances()-steps+1)
                                    loopStop = steps-samples.size();
                                Log.e("prediction", "Last data after: "+loopStop);
                                for (int x = 0; x < loopStop; x++){
                                    Float [] temp = CSVSampleWriterDL.lastData.get(x);
                                    if (x != 0)
                                        sb.append("\n");
                                    for (int y = 0; y < num_inputs; y++) {
                                        sb.append(temp[y].toString()).append(",");
                                        sampleInputs[x*num_inputs+y] = temp[y];
                                    }

                                }
                                for (int x = loopStop; x < samples.size()+loopStop; x++) {
                                    Float [] temp = samples.get(x-loopStop);
                                    sb.append("\n");
                                    for (int y = 0; y < num_inputs; y++) {
                                        sb.append(temp[y].toString()).append(",");
                                        sampleInputs[x*num_inputs+y] = temp[y];
                                    }
                                }
                            }
                            else {
                                for (int x = 0; x < steps; x++) {
                                    Float[] temp = samples.get(x);
                                    if (x != 0)
                                        sb.append("\n");
                                    for (int y = 0; y < num_inputs; y++) {
                                        sb.append(temp[y].toString()).append(",");
                                        sampleInputs[x * num_inputs + y] = temp[y];

                                    }
                                }
                            }

                                //Log.e("prediction2", inputs);
                            if ((CSVSampleWriterDL.lastData.size() > 0 && i < prediction_data.numInstances()-steps) || (CSVSampleWriterDL.lastData.size() > steps-1))
                                CSVSampleWriterDL.lastData.remove(0);
                            lengths.append(sampleInputs.length).append(",");
                            inferenceInterface.feed("Input", sampleInputs, batch_size, steps, num_inputs);
                            inferenceInterface.run(outputNames, false);
                            inferenceInterface.fetch("Output", outputs);

                            CSVSampleWriterDL.heuristics.add(outputs[0]);
                            float currentHeuristic = 0;
                            for (float h: CSVSampleWriterDL.heuristics) {
                                currentHeuristic += h;
                            }
                            currentHeuristic /= (CSVSampleWriterDL.heuristics.size());
                            if (CSVSampleWriterDL.heuristics.size() == hBatchSize) {
                                sb.append("\nHeuristic: ").append(currentHeuristic).append("\n");
                            }
                            boolean fallPredicted = false;
                            if (CSVSampleWriterDL.heuristics.size() == hBatchSize)
                                Log.e("prediction", "*****Current Heuristic " + Integer.toString(i) + ": "+currentHeuristic);
                            if (currentHeuristic > threshold && CSVSampleWriterDL.heuristics.size() == hBatchSize) {
                                fallPredicted = true;
                                fall = true;
                            }
                            else if (currentHeuristic < threshold && !fall) {
                                fall = true;


                                for(int j = 0; j < 9; j++) {
                                    DialogActivity.fall_data[j] = sampleInputs[j];

                                }

                            }



                            //test if prediction was fall, if so set the final prediction to fall.
                            if (fallPredicted)
                            {
                                if (fall){
                                    Log.d("prediction", "***********************" + " Fall");

                                    final ToneGenerator tg = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100);
                                    tg.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500);
                                    tg.release();

                                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                    // Vibrate for 500 milliseconds
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        v.vibrate(VibrationEffect.createOneShot(1000,VibrationEffect.DEFAULT_AMPLITUDE));
                                    }else{
                                        //deprecated in API 26
                                        v.vibrate(1000);
                                    }

                                    if (context != null){
                                        new Handler(Looper.getMainLooper()).post(
                                                new Runnable() {
                                                    public void run() {

                                                        //   code for sending notification goes here    /

                                                        Intent intentDialog = new Intent("android.intent.action.DIA");
                                                        intentDialog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        //context.startActivity(intentDialog);

                                                    }
                                                }
                                        );
                                    }

                                    try {
                                        MainActivity.consoleStatic.setText("\n Fall. Consecutive = "+ numFall +
                                                " start = " + start_new  + " end = " + prediction_data.numInstances());
                                    } catch (Exception e){
                                        Log.d("Final Prediction", "Trouble console text \n.");
                                    }
                                    fall = false;
                                }
                                else {
                                    Log.d("***********************","NoFall");
                                    try{
                                        MainActivity.consoleStatic.setText("\n No Fall. Consecutive = "+ numFall +
                                                " start = " + start_new + " end = " + prediction_data.numInstances());
                                    }catch (Exception e){
                                        Log.d("Final Prediction", "Trouble console text \n.");
                                    }
                                }
                            }
                            if (CSVSampleWriterDL.heuristics.size() == hBatchSize) {
                                CSVSampleWriterDL.heuristics.clear();
                            }

                        } catch (Exception e) {
                            Log.e("Prediction", "stack trace:");
                            e.printStackTrace();
                        }
                        if (samples.size() == steps)
                            samples.remove(0);
                        CSVSampleWriterDL.writer3.write(sb.toString());
                        CSVSampleWriterDL.writer4.write(lengths.toString());
                        //Log.e("prediction", "Writing "+sb.toString());
                        CSVSampleWriterDL.writer4.flush();
                        CSVSampleWriterDL.writer3.flush();
                        sb.setLength(0);
                        lengths.setLength(0);
                    }


             //       Log.d("fall predict newinst",newinst.toString());
                    ++i;
                }

                if (CSVSampleWriterDL.lastData.size() > steps-1) {
                    Log.e("prediction", "Old size: "+ CSVSampleWriterDL.lastData.size());
                    Log.e("prediction","Removing samples 0 - "+(CSVSampleWriterDL.lastData.size()-steps-1));
                    for (int x = 0; x < CSVSampleWriterDL.lastData.size()-steps-1; x++)
                        CSVSampleWriterDL.lastData.remove(0);
                    Log.e("prediction", "Resulting size "+ CSVSampleWriterDL.lastData.size());
                }


            }catch (Exception e) {
                Log.e("Prediction", "Stack trace:");
                e.printStackTrace();
            }



        }catch (Exception e){
            Log.e("Prediction","Could not find data source");
        }

    }
}
