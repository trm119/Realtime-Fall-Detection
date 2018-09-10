package reuiot2015.smartwatch.sensors_persistence;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.IllegalFormatException;

import reuiot2015.smartwatch.MainActivity;
import reuiot2015.smartwatch.predictionDL;
import reuiot2015.smartwatch.MyApplication;

import java.lang.*;

/** Stores samples collected from a Collector into a local file.
 *
 * @author Mario A. Gutierrez (mag262@txstate.edu)
 */
public class CSVSampleWriterDL extends CSVSampleWriter {
    //Set this to true if you're collecting new data for a new model instead of predicting
    private boolean collectNewDataMode = false;

    //skip the first few null data to prevent crash
    private static boolean skipNull = false;

    //count how many prediction rounds has been made
    private int count_round = 0;
    private PrintWriter writer;
    private PrintWriter writer2;
    public static PrintWriter writer4;
    public static PrintWriter writer3;
    public static int mStart_new = 0;
    static private boolean header = true;
    public static boolean renewed = false;
    public static ArrayList<Float> heuristics = new ArrayList<Float>();
    public static ArrayList<Float []> lastData = new ArrayList<Float []>();

    private String filename_rewrite = "";
    private File publicDirectory;
    SQLiteDatabase sqLiteDatabase = MainActivity.myDataBase.getWritableDatabase();

    public CSVSampleWriterDL(String[] header, String filename) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

            //Get the public directory to store to
            publicDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SmartWatchValues.ALBUM_NAME + "/data");
            Log.e("CSVSampleWriter", "Saved path is: " + publicDirectory.getAbsolutePath());

            //Get filename for reopen/rewrite the file - Boden
            filename_rewrite = filename;
            String filename_write = "saved_data.csv";
            String fname_heuristic = "saved_heuristics.csv";
            if (publicDirectory.mkdirs())
                Log.d("CSVSampleWriter", "Created file structure.");
            else
                Log.d("CSVSampleWriter", "Using existing file structure or failed to create.");

            try {
                //Open a writer to write to the sample file
                writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(publicDirectory, filename))));
                writer2 = new PrintWriter(new BufferedWriter(new FileWriter(new File(publicDirectory, filename_write))));
                writer3 = new PrintWriter(new BufferedWriter(new FileWriter(new File(publicDirectory, fname_heuristic))));
                writer4 = new PrintWriter(new BufferedWriter(new FileWriter(new File(publicDirectory, "Lengths.csv"))));
                //Artificially call this method to write the sample header to file
                receiveAccumulatedSamples(new String[][] { header });


            } catch (IOException e) {  Log.e("CSVSampleWriter", e.getMessage()); }

        }

        else {
            Log.d("CSVSampleWriter", "External media is not mounted.");
        }
    }

/*****************************relevant****************************/
    @Override
    public boolean receiveAccumulatedSamples(String[][] samples) {
            try {
                int c = 0;
                int count;
                int totalWidth = SmartWatchValues.DATA_PER_WINDOW;    //how many data are compared at once
                int halfWidth = totalWidth / 2;
                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                StringBuilder sb3 = new StringBuilder();
                String uuid = MainActivity.subjectInformation.uuid;
                SQLiteDatabase sqLiteDatabase = MainActivity.myDataBase.getWritableDatabase();

                synchronized (samples) {

                    if (header){
                        sb.append("ACC_X,").append("ACC_Y,").append("ACC_Z,").append("\n");
                        writer.write(sb.toString());    //Write the formatted samples title to file.
                        sb.setLength(0);}

                    header = false;         //Avoids writing the header multiple times


                    count = 0;              //Can't start predicting until after 3 samples have been collected
                    int sampleNum = 0;
                    String d = "";
                    for (int a = 0; a < samples.length; a++) {
                        d += samples[a][3];
                        d += ", ";
                        d += samples[a][4];
                        d += ", ";
                        d += samples[a][5];
                        d += "\n";
                    }
                    //Log.e("CSVSampleWriter2", d);
                    for (String[] sample : samples) {

                        Long time_long = System.currentTimeMillis()/1000;
                        String time_string  = time_long.toString();
                        int time_int = Integer.parseInt(time_string);

                        //Log.e("CSVSampleWriter", "(each sample) c = " + c);



                        if (count > totalWidth) {

                            double axPhone = 0;     //acceleration x axis
                            double ayPhone = 0;     //acceleration y axis
                            double azPhone = 0;     //acceleration z axis

                            if (!isEmptyString(samples[count - totalWidth][0]))
                                axPhone = Double.parseDouble(samples[count - totalWidth][0]);
                            if (!isEmptyString(samples[count - totalWidth][1]))
                                ayPhone = Double.parseDouble(samples[count - totalWidth][1]);
                            if (!isEmptyString(samples[count - totalWidth][2]))
                                azPhone = Double.parseDouble(samples[count - totalWidth][2]);

                            float[] ax = new float[totalWidth];     //acceleration x axis
                            float[] ay = new float[totalWidth];     //acceleration y axis
                            float[] az = new float[totalWidth];     //acceleration z axis
                            for (int i = 0; i < totalWidth; i++)
                            {
                                if ((count >= totalWidth) && skipNull == false)
                                {
                                    //Indexes 0 1 2 means accelerometer from the phone. Indexes 3 4 5 means accelerometer from the watch.
                                    //see SensorService to check the order of sensors

                                    if (!isEmptyString(samples[count - totalWidth + i][3]))
                                        ax[i] = Float.parseFloat(samples[count - totalWidth + i][3]);
                                    if (!isEmptyString(samples[count - totalWidth + i][4]))
                                        ay[i] = Float.parseFloat(samples[count - totalWidth + i][4]);
                                    if (!isEmptyString(samples[count - totalWidth + i][5]))
                                        az[i] = Float.parseFloat(samples[count - totalWidth + i][5]);
                                    if (sampleNum == 0) {
                                        //sb.append(String.valueOf(ax[i])).append(",").append(String.valueOf(ay[i])).append(",").append(String.valueOf(az[i])).append("\n");
                                        //sb3.append(String.valueOf(ax[i])).append(",").append(String.valueOf(ay[i])).append(",").append(String.valueOf(az[i])).append("\n");
                                        for (int j = 0; j < sample.length - 1; ++j) {
                                            if (j >= 3 && j <= 5) {
                                                if (j != 5)
                                                    sb.append(sample[j]).append(",");
                                                else
                                                    sb.append(sample[j]).append("\n");
                                            }
                                            sb2.append(sample[j]).append(", ");
                                        }
                                        int last = sample.length - 1;
                                        sb2.append(sample[last]).append("\n");
                                        MainActivity.dbManager.insertRawData(MainActivity.subjectInformation.uuid, Float.parseFloat(samples[count - totalWidth + i][3]), Float.parseFloat(samples[count - totalWidth + i][4]), Float.parseFloat(samples[count - totalWidth + i][5]), Float.parseFloat(samples[count - totalWidth + i][6]), Float.parseFloat(samples[count - totalWidth + i][7]), Float.parseFloat(samples[count - totalWidth + i][8]), Float.parseFloat(samples[count - totalWidth + i][9]), Float.parseFloat(samples[count - totalWidth + i][10]),  Float.parseFloat(samples[count - totalWidth + i][11]), Float.parseFloat(samples[count - totalWidth + i][12]), Float.parseFloat(samples[count - totalWidth + i][13]), Integer.parseInt(samples[count - totalWidth + i][14]), samples[count - totalWidth + i][15], samples[count - totalWidth + i][16], samples[count - totalWidth + i][17], Integer.parseInt(samples[count - totalWidth + i][18]), Float.parseFloat(samples[count - totalWidth + i][19]), samples[count - totalWidth + i][20], samples[count - totalWidth + i][21], Integer.parseInt(samples[count - totalWidth + i][22]), samples[count - totalWidth + i][23]);
                                    }
                                }
                            }

                            if (sampleNum != 0) {
                                for (int j = 0; j < sample.length - 1; ++j) {
                                    if (j >= 3 && j <= 5) {
                                        if (j != 5)
                                            sb.append(sample[j]).append(",");
                                        else
                                            sb.append(sample[j]).append("\n");
                                    }
                                    sb2.append(sample[j]).append(", ");
                                }
                                int last = sample.length - 1;
                                sb2.append(sample[last]).append("\n");
                                //sb.append(String.valueOf(ax[totalWidth-1])).append(",").append(String.valueOf(ay[totalWidth-1])).append(",").append(String.valueOf(az[totalWidth-1])).append("\n");
                                //sb3.append(String.valueOf(ax[totalWidth-1])).append(",").append(String.valueOf(ay[totalWidth-1])).append(",").append(String.valueOf(az[totalWidth-1])).append("\n");
                                MainActivity.dbManager.insertRawData(MainActivity.subjectInformation.uuid, Float.parseFloat(samples[count - 1][3]), Float.parseFloat(samples[count-1][4]), Float.parseFloat(samples[count-1][5]), Float.parseFloat(samples[count-1][6]), Float.parseFloat(samples[count-1][7]), Float.parseFloat(samples[count-1][8]), Float.parseFloat(samples[count-1][9]), Float.parseFloat(samples[count-1][10]), Float.parseFloat(samples[count-1][11]), Float.parseFloat(samples[count-1][12]), Float.parseFloat(samples[count-1][13]), Integer.parseInt(samples[count-1][14]), samples[count-1][15], samples[count-1][16], samples[count-1][17], Integer.parseInt(samples[count-1][18]), Float.parseFloat(samples[count-1][19]), samples[count-1][20], samples[count-1][21], Integer.parseInt(samples[count-1][22]), samples[count-1][23]);

                            }

                            //Log.e("CSVSampleWriter2", sb.toString());

                            writer.write(sb.toString());    //Write the formatted samples to file
                            writer2.write(sb2.toString());
                            //writer4.write(sb3.toString());
                            sb.setLength(0);    //Clear contents of builder
                            sb2.setLength(0);
                            sb3.setLength(0);
                            sampleNum++;
                        }

                        ++count;
                        c = count;

                    }
                }


                writer.flush();
                writer2.flush();
                //writer4.flush();
                MyApplication m = new MyApplication();
                if (count > totalWidth-1) {


                    Log.e("CSVSampleWriter", "(sent to predict) c = " + c);

                    predictionDL fall_prediction = new predictionDL(m.getAppContext());                 //create prediction instance
                    fall_prediction.predict(m.getAppContext(), c);                                                //call prediction function


                    // Replace prediction_data.csv when ever count_round gets big
                    count_round++;

                    if (count_round > SmartWatchValues.REWRITE_INTERVAL){
                        renewed = true;
                        count_round = 0;
                        release();
                        header = true;
                        writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(publicDirectory, filename_rewrite))));
                        Log.e("CSVSampleWriter", "Renew prediction_data.csv file.");
                    }
                    else {
                        renewed = false;
                    }
                }

                skipNull = false;
                return true;

            }

            catch (IllegalFormatException | NullPointerException e) {
                Log.e("CSVSampleWriterERROR", e.getMessage());
            }

            catch (Exception e) {
                e.printStackTrace();
            }

        return false;
    }

    //Closes the output stream to the file
    public void release() {
        if (writer != null) this.writer.close();
    }


    //takes the arrays of accelerations and returns the sqrt of the max-min of all


    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
    }

    /*****************************relevant****************************/
}
