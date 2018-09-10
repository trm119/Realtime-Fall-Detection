package reuiot2015.smartwatch;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import java.util.Locale;
import javax.mail.Session;
import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import android.os.AsyncTask;

import reuiot2015.smartwatch.sensors.Sensor;

public class DialogActivity extends AppCompatActivity implements OnClickListener {

    //Coralys Cubero Rivera
    Button need_help_button, okay_button, fell_okay;
    Session session = null;
    ProgressDialog progress_dialog = null;
    Context context = null;
    String recipient, subject, textMessage;
    public static double [] fall_data = new double[9];


    //Variables needed to create the timer shown when the fall prompt appears
    private TextView countDownText;                             //Displays the numbers
    private CountDownTimer mcountDownTimer;                     //Actual timer
    private static final long START_IN_MILLIS = 30000;          //30 seconds
    private long timeLeftInMilliseconds = START_IN_MILLIS;      //The time left since the start of the timer

    SQLiteDatabase sqLiteDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);
        this.setFinishOnTouchOutside(true);

        context = this;

        countDownText = findViewById(R.id.countdown);
        okay_button = findViewById(R.id.button_dialog_negative);
        need_help_button = findViewById(R.id.button_dialog_positive);
        fell_okay = findViewById(R.id.fell_okay);


        need_help_button.setOnClickListener(this);

    }


    //Whenever the fall prompt starts, the count down for help will start
    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
        MainActivity.dbManager.open();

    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.dbManager.close();
    }

    //Coralys Cubero
    //Function that takes action when the "NEED HELP" button is pressed
    public void onClick(View view) {
        /*String uuid_db = MainActivity.subjectInformation.uuid;
        sqLiteDatabase = MainActivity.myDataBase.getWritableDatabase();
        Cursor cursor1 = MainActivity.dbManager.fetchFallData(fall_data[0], fall_data[1], fall_data[2]);
        Cursor cursor2 = MainActivity.dbManager.fetchFallData(fall_data[3], fall_data[4], fall_data[5]);
        Cursor cursor3 = MainActivity.dbManager.fetchFallData(fall_data[6], fall_data[7], fall_data[8]);

        cursor1.moveToFirst();
        cursor2.moveToFirst();
        cursor3.moveToFirst();

        String id1 = cursor1.getString(0);
        String id3 = cursor3.getString(0);

        int id_start = Integer.parseInt(id1) - 5;
        int id_end = Integer.parseInt(id3) + 102;

        Cursor cursor4 = MainActivity.dbManager.fetchSpecificFall(id_start, id_end);
        cursor4.moveToFirst();

        Random rand = new Random();
        int rand_num = rand.nextInt(100000);
        String fall_id = "FALL_"+String.valueOf(rand_num);

        while(cursor4.moveToNext()){
            MainActivity.dbManager.insertTruePositives(fall_id, MainActivity.subjectInformation.uuid, Float.parseFloat(cursor4.getString(2)), Float.parseFloat(cursor4.getString(3)), Float.parseFloat(cursor4.getString(4)));
        }


      //sendEmail();
      */
        finish();

    }

    public void FellOkay(View view) {
        /*String uuid_db = MainActivity.subjectInformation.uuid;
        sqLiteDatabase = MainActivity.myDataBase.getWritableDatabase();
        Cursor cursor1 = MainActivity.dbManager.fetchFallData(fall_data[0], fall_data[1], fall_data[2]);
        Cursor cursor2 = MainActivity.dbManager.fetchFallData(fall_data[3], fall_data[4], fall_data[5]);
        Cursor cursor3 = MainActivity.dbManager.fetchFallData(fall_data[6], fall_data[7], fall_data[8]);

        cursor1.moveToFirst();
        cursor2.moveToFirst();
        cursor3.moveToFirst();

        String id1 = cursor1.getString(0);
        String id3 = cursor3.getString(0);

        int id_start = Integer.parseInt(id1) - 5;
        int id_end = Integer.parseInt(id3) + 102;

        Cursor cursor4 = MainActivity.dbManager.fetchSpecificFall(id_start, id_end);
        cursor4.moveToFirst();

        Random rand = new Random();
        int rand_num = rand.nextInt(100000);
        String fall_id = "FALL_"+String.valueOf(rand_num);

        while(cursor4.moveToNext()){
            MainActivity.dbManager.insertTruePositives(fall_id, MainActivity.subjectInformation.uuid, Float.parseFloat(cursor4.getString(2)), Float.parseFloat(cursor4.getString(3)), Float.parseFloat(cursor4.getString(4)));
        }
        */

        finish();

    }


    //Function that takes action when the "I'M OKAY" button has been pressed
    public void ButtonDialogNegative(View view) {

       /* String uuid_db = MainActivity.subjectInformation.uuid;
        sqLiteDatabase = MainActivity.myDataBase.getWritableDatabase();
        Cursor cursor1 = MainActivity.dbManager.fetchFallData(fall_data[0], fall_data[1], fall_data[2]);
        Cursor cursor2 = MainActivity.dbManager.fetchFallData(fall_data[3], fall_data[4], fall_data[5]);
        Cursor cursor3 = MainActivity.dbManager.fetchFallData(fall_data[6], fall_data[7], fall_data[8]);

        cursor1.moveToFirst();
        cursor2.moveToFirst();
        cursor3.moveToFirst();


        String id1 = cursor1.getString(0);
        String id3 = cursor3.getString(0);

        int id_start = Integer.parseInt(id1) - 5;
        int id_end = Integer.parseInt(id3) + 102;

        Cursor cursor4 = MainActivity.dbManager.fetchSpecificFall(id_start, id_end);
        cursor4.moveToFirst();

        Random rand = new Random();
        int rand_num = rand.nextInt(100000);
        String fall_id = "FALL_"+String.valueOf(rand_num);

        while(cursor4.moveToNext()){
            MainActivity.dbManager.insertFalsePositives(fall_id, MainActivity.subjectInformation.uuid, Float.parseFloat(cursor4.getString(2)), Float.parseFloat(cursor4.getString(3)), Float.parseFloat(cursor4.getString(4)));
        }

        */

        finish();
    }

    //Coralys Cubero
    //CountDown Timer
    private void startTimer() {
        mcountDownTimer = new CountDownTimer(timeLeftInMilliseconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliseconds = millisUntilFinished;
                int seconds = (int) (timeLeftInMilliseconds / 1000) % 60;

                if (seconds == 1) {
                    //sendEmail();
                }


                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d", seconds);
                countDownText.setText(timeLeftFormatted);


            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void sendEmail() {
        recipient = MainActivity.subjectInformation.contact_email;
        Log.d("NEED HELP", recipient);
        textMessage = "I've fallen and I need help!";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("fall.detection.reu.2018@gmail.com", "fallR3U2018!");
            }
        });

        progress_dialog = ProgressDialog.show(context, "", "Sending Mail...", true);

        RetreiveFeedTask task = new RetreiveFeedTask();
        task.execute();

        finish();
    }


    class RetreiveFeedTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try{
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("testfrom354@gmail.com"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
                message.setSubject(subject);
                message.setContent(textMessage, "text/html; charset=utf-8");
                Transport.send(message);
            } catch(MessagingException e) {
                e.printStackTrace();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progress_dialog.dismiss();
            Toast.makeText(getApplicationContext(), "Message sent", Toast.LENGTH_LONG).show();
        }
    }


}
