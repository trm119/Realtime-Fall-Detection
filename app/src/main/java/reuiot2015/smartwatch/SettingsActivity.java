package reuiot2015.smartwatch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final Button visible = findViewById(R.id.visible_button);
        final Button invisible = findViewById(R.id.invisible_button);

        invisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.threshold.setVisibility(View.GONE);
                MainActivity.num_steps.setVisibility(View.GONE);
                visible.setVisibility(View.VISIBLE);
            }
        });

        visible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.threshold.setVisibility(View.VISIBLE);
                MainActivity.num_steps.setVisibility(View.VISIBLE);
                invisible.setVisibility(View.GONE);
            }
        });

        BottomNavigationView bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation_menu);
        bottomNavigation.setOnNavigationItemSelectedListener(navListener);


        Menu menu = bottomNavigation.getMenu();
        MenuItem menuItem= menu.getItem(2);
        menuItem.setChecked(true);
    }

    /* Function that makes the Bottom Navigation Menu work. Depending on the choice made by the user, an action will be taken */
    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                //We're in the main activity so no action needs to be done
                case R.id.nav_home:
                    Intent nextActivity2 = new Intent(SettingsActivity.this, MainActivity.class);
                    startActivity(nextActivity2);
                    break;

                //Loads the activity profile and displays the information that was previously saved from the user
                case R.id.nav_profile:
                    Intent intent = new Intent(SettingsActivity.this, ProfileSettingsActivity.class);
                    if (MainActivity.subjectInformation != null) {
                        intent.putExtra(ProfileSettingsActivity.EXTRA_NAME, MainActivity.subjectInformation.name);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_BIRTHDATE, MainActivity.subjectInformation.birthDate);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_SEX, MainActivity.subjectInformation.sex);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_HEIGHT_FEET, MainActivity.subjectInformation.height_feet);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_HEIGHT_INCHES, MainActivity.subjectInformation.height_inches);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_WEIGHT, MainActivity.subjectInformation.weight);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_EMAIL, MainActivity.subjectInformation.email);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_PHONE, MainActivity.subjectInformation.phone);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_UUID, MainActivity.subjectInformation.uuid);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_CONTACT_NAME, MainActivity.subjectInformation.contact_name);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_CONTACT_EMAIL, MainActivity.subjectInformation.contact_email);
                        intent.putExtra(ProfileSettingsActivity.EXTRA_CONTACT_PHONE, MainActivity.subjectInformation.contact_phone);

                    }
                    startActivity(intent);
                    break;

                case R.id.nav_settings:
                    break;

            }

            return false;

        }

    };
}
