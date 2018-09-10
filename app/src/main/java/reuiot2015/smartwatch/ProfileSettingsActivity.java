package reuiot2015.smartwatch;

import android.app.DatePickerDialog;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.DatePicker;
import android.graphics.drawable.ColorDrawable;
import android.graphics.Color;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.UUID;
import java.util.Calendar;


public class ProfileSettingsActivity extends AppCompatActivity {

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    BottomNavigationView bottomNavigation;

    private ScrollView scrollView;

    public static final int REQUEST_PROFILE = 0x1;

    // Define keys for profile data extras.
    public static final String EXTRA_NAME = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_NAME";
    public static final String EXTRA_BIRTHDATE = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_BIRTHDATE";
    public static final String EXTRA_SEX = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_SEX";
    public static final String EXTRA_HEIGHT_FEET = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_HEIGHT_FEET";
    public static final String EXTRA_HEIGHT_INCHES = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_HEIGHT_INCHES";
    public static final String EXTRA_WEIGHT = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_WEIGHT";
    public static final String EXTRA_EMAIL = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_EMAIL";
    public static final String EXTRA_PHONE = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_PHONE";
    public static final String EXTRA_UUID = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_UUID";

    public static final String EXTRA_CONTACT_NAME = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_CONTACT_NAME";
    public static final String EXTRA_CONTACT_EMAIL = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_CONTACT_EMAIL";
    public static final String EXTRA_CONTACT_PHONE = "reuiot.smartwatch.ProfileSettingsActivity.EXTRA_CONTACT_PHONE";


    private final static UUID DEFAULT_UUID = UUID.fromString("1c0d4e24-0062-40fc-9dca-4cb8ac11257d");

    private EditText name, height_feet, height_inches, weight, email, phone, uuid, contact_name, contact_email, contact_phone;
    private RadioButton male, female;
    private String sex;

    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onStart() {
        super.onStart();
        MainActivity.dbManager.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MainActivity.dbManager.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_settings);

        mDisplayDate = (TextView) findViewById(R.id.tvDate);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);

        //Created a date picker so it is easier for the user to state their birth date.
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        ProfileSettingsActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        //Once the user picked a date, let's set it in the profile
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d("Profile Settings", "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };


        // Link up the views.
        name = (EditText) findViewById(R.id.edit_name);
        height_feet = (EditText) findViewById(R.id.edit_feet);
        height_inches = (EditText) findViewById(R.id.edit_inches);
        weight = (EditText) findViewById(R.id.edit_weight);
        email = (EditText) findViewById(R.id.edit_email);
        phone = (EditText) findViewById(R.id.edit_phone);
        uuid = (EditText) findViewById(R.id.edit_user_id);
        male = (RadioButton) findViewById(R.id.radio_button_male);
        female = (RadioButton) findViewById(R.id.radio_button_female);

        contact_name = (EditText) findViewById(R.id.edit_contact_name);
        contact_email = (EditText) findViewById(R.id.edit_contact_email);
        contact_phone = (EditText) findViewById(R.id.edit_contact_phone);


        // Fill out the profile information from previous settings.
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = (String) extras.get(EXTRA_NAME);
            if (name != null) this.name.setText(name);

            String birthDate = (String) extras.get(EXTRA_BIRTHDATE);

            String sex = (String) extras.get(EXTRA_SEX);
            if (sex.equalsIgnoreCase("MALE")) male.setChecked(true);
            else if (sex.equalsIgnoreCase("FEMALE")) female.setChecked(true);

            int height_feet = (int) extras.get(EXTRA_HEIGHT_FEET);
            if (height_feet != 0) this.height_feet.setText(height_feet + "");

            int height_inches = (int) extras.get(EXTRA_HEIGHT_INCHES);
            if (height_inches != 0) this.height_inches.setText(height_inches + "");

            int weight = (int) extras.get(EXTRA_WEIGHT);
            if (weight != 0) this.weight.setText(weight + "");

            String email = (String) extras.get(EXTRA_EMAIL);
            if (email != null) this.email.setText(email);

            String phone = (String) extras.get(EXTRA_PHONE);
            if (phone != null) this.phone.setText(phone);

            String uuid = (String) extras.get(EXTRA_UUID);
            if (uuid != null) this.uuid.setText(uuid);
            else {
                this.uuid.setText(DEFAULT_UUID.toString());

            }

            String contact_name = (String) extras.get(EXTRA_CONTACT_NAME);
            if (contact_name != null) this.contact_name.setText(contact_name);

            String contact_email = (String) extras.get(EXTRA_CONTACT_EMAIL);
            if (contact_email != null) this.contact_email.setText(contact_email);

            String contact_phone = (String) extras.get(EXTRA_CONTACT_PHONE);
            if (contact_phone != null) this.contact_phone.setText(contact_phone);

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }

        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation_menu);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);

        Menu menu = bottomNavigation.getMenu();
        MenuItem menuItem= menu.getItem(1);
        menuItem.setChecked(true);

        //Let's hide the keyboard whenever the user goes into the profile activity, it will appear if they actually want to write something.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /* Function that makes the Bottom Navigation Menu work. Depending on the choice made by the user, an action will be taken */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                //Loads the main activity - which is the main page of the application
                case R.id.nav_home:
                    Intent nextActivity = new Intent(ProfileSettingsActivity.this, MainActivity.class);
                    startActivity(nextActivity);
                    break;

                //We are in the profile activity so there's nothing to be done
                case R.id.nav_profile:
                    break;

                case R.id.nav_settings:
                Intent nextActivity2 = new Intent(ProfileSettingsActivity.this, SettingsActivity.class);
                startActivity(nextActivity2);
                break;

            }

            return false;

        }

    };


    public void onSaveProfile(View v) {

        if (male.isChecked())
            sex = "MALE";
        else
            sex = "FEMALE";

        String uuid_db = uuid.getText().toString();
        String name_db = name.getText().toString();
        String birthDate_db = mDisplayDate.getText().toString();
        String sex_db = sex;

        int height_feet_db = Integer.parseInt(height_feet.getText().toString());
        int height_inches_db = Integer.parseInt(height_inches.getText().toString());
        int weight_db = Integer.parseInt(weight.getText().toString());

        String email_db = email.getText().toString();
        String phone_db = phone.getText().toString();
        String contact_name_db = contact_name.getText().toString();
        String contact_email_db = contact_email.getText().toString();
        String contact_phone_db = contact_phone.getText().toString();


        sqLiteDatabase = MainActivity.myDataBase.getWritableDatabase();
        MainActivity.dbManager.insertUserProfile(uuid_db, name_db, birthDate_db, sex_db, height_feet_db, height_inches_db, weight_db, email_db, phone_db);
        MainActivity.dbManager.insertEmergencyContacts(contact_name_db, contact_email_db, contact_phone_db, uuid_db);
        Toast.makeText(ProfileSettingsActivity.this, "Profile inserted in DATABASE!", Toast.LENGTH_SHORT).show();

        Intent data = new Intent(); // Store data in an Intent.

        data.putExtra(EXTRA_NAME, name.getText().toString());
        data.putExtra(EXTRA_BIRTHDATE, mDisplayDate.getText().toString());
        data.putExtra(EXTRA_SEX, (male.isChecked()) ? "MALE" : (female.isChecked()) ? "FEMALE" : "UNSPECIFIED");
        data.putExtra(EXTRA_HEIGHT_FEET, (height_feet.length() != 0) ? Integer.parseInt(height_feet.getText().toString()): 0);
        data.putExtra(EXTRA_HEIGHT_INCHES, (height_inches.length() != 0) ? Integer.parseInt(height_inches.getText().toString()): 0);
        data.putExtra(EXTRA_WEIGHT, (weight.length() != 0) ? Integer.parseInt(weight.getText().toString()): 0);
        data.putExtra(EXTRA_EMAIL, email.getText().toString());
        data.putExtra(EXTRA_PHONE, phone.getText().toString());
        data.putExtra(EXTRA_UUID, uuid.getText().toString());
        data.putExtra(EXTRA_CONTACT_NAME, contact_name.getText().toString());
        data.putExtra(EXTRA_CONTACT_EMAIL, contact_email.getText().toString());
        data.putExtra(EXTRA_CONTACT_PHONE, contact_phone.getText().toString());
        setResult(RESULT_OK, data);

        // Hide soft keyboard.
        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(v.getWindowToken(), 0);

        finish(); // Kill the activity.

    }

    /** Generates a new UUID for the profile.
     *
     * @param v The related View.
     */
    public void generateUUID(View v) {
        uuid.setText(UUID.randomUUID().toString());
    }



    /** Sets the profile UUID to default.
     *
     * @param v The related View.
     */
    public void defaultUUID(View v) {
        uuid.setText(DEFAULT_UUID.toString());
    }

}
