package reuiot2015.smartwatch.sensors_persistence;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.IllegalFormatException;

import reuiot2015.smartwatch.MyApplication;
import reuiot2015.smartwatch.predictionDL;
import reuiot2015.smartwatch.predictionNB;

import static reuiot2015.smartwatch.MainActivity.flagFallCollect;

/** Stores samples collected from a Collector into a local file.
 *
 * @author Mario A. Gutierrez (mag262@txstate.edu)
 */
public class CSVSampleWriterNB extends CSVSampleWriter {
    //Set this to true if you're collecting new data for a new model instead of predicting
    private boolean collectNewDataMode = false;

    //skip the first few null data to prevent crash
    private static boolean skipNull = false;

    //count how many prediction rounds has been made
    private int count_round = 0;

    private PrintWriter writer;
    static private boolean header = true;

    private String filename_rewrite = "";
    private File publicDirectory;
    public CSVSampleWriterNB(String[] header, String filename) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            // Get the public directory to store to.
            publicDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), SmartWatchValues.ALBUM_NAME + "/data");
            Log.d("CSVSampleWriter", "Save path is: " + publicDirectory.getAbsolutePath());

            // Get filename for reopen/rewrite the file - Boden
            filename_rewrite = filename;


            if (publicDirectory.mkdirs()) Log.d("CSVSampleWriter", "Created file structure.");
            else Log.d("CSVSampleWriter", "Using existing file structure, or failed to create.");

            try {
                // Open a writer to write to the sample file.
                writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(publicDirectory, filename))));

                // Artificially call this method to write the sample header to file.
                receiveAccumulatedSamples(new String[][] { header });
            } catch (IOException e) {  Log.e("CSVSampleWriter", e.getMessage()); }
        } else {
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
            synchronized (samples) {
                if (header == true){
                    sb.append("resultant,").append("cvfast,").append("smax,").append("smin,").append("outcome").append("\n");
                    // .append("resultant phone,")
                    writer.write(sb.toString()); // Write the formatted samples title to file.
                    sb.setLength(0);}
                header = false;  // keep from writing the header multiple times
                boolean l = true; // in the initial csv file both possible outcomes must be present
                // in the correct order as predicted. in this case notfall first and fall second. see below
                count = 0;     // can't start predicting until after 3 samples have been collected

                for (String[] sample : samples) {
                    Log.e("CSVSampleWriter", "(each sample) c = " + c);
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

                        double[] ax = new double[totalWidth];     //acceleration x axis
                        double[] ay = new double[totalWidth];     //acceleration y axis
                        double[] az = new double[totalWidth];     //acceleration z axis
                        for (int i = 0; i < totalWidth; i++)
                        {
                            if ((count >= totalWidth) && skipNull == false)
                            {
                                // Indexes 0 1 2 means accelerometer from the phone. Indexes 3 4 5 means accelerometer from the watch.
                                // see SensorService to check the order of sensors
                                if (!isEmptyString(samples[count - totalWidth + i][3]))
                                    ax[i] = Double.parseDouble(samples[count - totalWidth + i][3]);
                                if (!isEmptyString(samples[count - totalWidth + i][4]))
                                    ay[i] = Double.parseDouble(samples[count - totalWidth + i][4]);
                                if (!isEmptyString(samples[count - totalWidth + i][5]))
                                    az[i] = Double.parseDouble(samples[count - totalWidth + i][5]);
                            }
                        }

                        double cvfast = calculatecvfastM(ax,ay,az, totalWidth);
                        double currentresultant = resultantM(ax[halfWidth],ay[halfWidth],az[halfWidth]);  //need resultant for current and last two

                        double smin = sminM(ax,ay,az, totalWidth);
                        double smax = smaxM(ax,ay,az, totalWidth);
                        sb.append(String.valueOf(currentresultant)).append(",").append(String.valueOf(cvfast)).append(",").append(String.valueOf(smax)).append(",").append(String.valueOf(smin)).append(",");

                        if (collectNewDataMode == false)
                        {
                            if (l == true){
                                sb.append("notfall"/*sample[last]*/).append("\n");
                            } else {
                                sb.append("fall"/*sample[last]*/).append("\n");
                            }

                        } else {
                            if (flagFallCollect == true)
                            {
                                Log.d("Testing", "---------------------------------------------------------------------------------------DETECT CHANGE here");
                                sb.append("fall"/*sample[last]*/).append("\n");
                            } else {
                                sb.append("notfall"/*sample[last]*/).append("\n");
                            }
                        }

                        writer.write(sb.toString()); // Write the formatted samples to file.
                        Log.d("******", sb.toString());
                        Log.e("CSVSampleWriter", "(write) c = " + c);
                        sb.setLength(0); // Clear contents of builder.
                        l = !l;   //alternate outcome to make sure prediction is working correctly.
                    }
                    ++count;
                    c = count;
                }
            }

            writer.flush();
            MyApplication m = new MyApplication();
            if (count > 30) {
                Log.e("CSVSampleWriter", "(sent to predict) c = " + c);

                predictionNB d = new predictionNB(m.getAppContext());   //create prediction instance
                d.predict(m.getAppContext(), c);                    //call prediction function.


                // Replace default.csv when ever count_round gets big
                count_round++;

                if (count_round > SmartWatchValues.REWRITE_INTERVAL){
                    count_round = 0;
                    release();
                    header = true;
                    writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(publicDirectory, filename_rewrite))));
                    Log.e("CSVSampleWriter", "Renew default.csv file.");
                }
            }

            skipNull = false;

            return true;
        } catch (IllegalFormatException | NullPointerException e) {
            Log.e("CSVSampleWriter", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Closes the output stream to the file. */
    public void release() {
        if (writer != null) this.writer.close();
    }


    //takes the arrays of accelerations and returns the sqrt of the max-min of all
    public static double calculatecvfastM(double[] ax, double[] ay, double[] az, int length){

        double axmax = ax[0];
        for (int i = 0; i < length; i++)
        {
            axmax = Math.max(axmax,ax[i]);
        }
        double aymax = ay[0];
        for (int i = 0; i < length; i++)
        {
            aymax = Math.max(aymax,ay[i]);
        }
        double azmax = az[0];
        for (int i = 0; i < length; i++)
        {
            azmax = Math.max(azmax,az[i]);
        }

        double axmin = ax[0];
        for (int i = 0; i < length; i++)
        {
            axmin = Math.min(axmin,ax[i]);
        }
        double aymin = ay[0];
        for (int i = 0; i < length; i++)
        {
            aymin = Math.min(aymin,ay[i]);
        }
        double azmin = az[0];
        for (int i = 0; i < length; i++)
        {
            azmin = Math.min(azmin,az[i]);
        }

        return Math.sqrt(((axmax-axmin)*(axmax-axmin))+((aymax-aymin)*(aymax-aymin))+((azmax-azmin)*(azmax-azmin)));
    }


    public static double resultantM(double x, double y, double z){
        return Math.sqrt((x*x)+(y*y)+(z*z));
    }

    //finds min of all resultants
    public static double sminM(double[] ax, double[] ay, double[] az, int length){
        double smin = resultantM(ax[0], ay[0], az[0]);
        for (int i = 0; i < length; i++)
        {
            smin = Math.min(smin, resultantM(ax[i], ay[i], az[i]));
        }
        return smin;
    }

    //finds max resultant of all resultants
    public static double smaxM(double[] ax, double[] ay, double[] az, int length){
        double smax = resultantM(ax[0], ay[0], az[0]);
        for (int i = 0; i < length; i++)
        {
            smax = Math.max(smax, resultantM(ax[i], ay[i], az[i]));
        }
        return smax;
    }


    public static boolean isEmptyString(String text) {
        return (text == null || text.trim().equals("null") || text.trim()
                .length() <= 0);
    }


    /*****************************relevant****************************/
}
