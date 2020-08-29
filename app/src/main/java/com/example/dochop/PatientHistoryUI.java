package com.example.dochop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

/**
 * Represents the Tabbed History User Interface activity for Patients.
 * There are 2 fragments in this UI.
 * First fragment contains history of GPs of a patient.
 * Second fragment contains history of ambulance calls of a patient.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class PatientHistoryUI extends AppCompatActivity {

    /**
     * Layout manager that allows the user to flip left and right through the 2 tabs 'DOCTOR' and
     * 'AMBULANCE".
     */
    private ViewPager viewPager;

    /**
     * Object that provides the adapter to populate tabs('DOCTOR' and 'AMBULANCE') inside of a
     * ViewPager.
     */
    public PageAdapter pagerAdapter;

    /**
     * Creation of the user object to obtain user details.
     */
    Patient user;

    /**
     * Links the activity_patient_history_ui xml file to the PatientHistoryUI java file.
     * Creates a Bundle object to pass user details from this activity to the 2 fragments.
     * Links the tabLayout layout to the tabLayout object.
     * Links the 2 TabItems to the two tabs in the TabLayout.
     * Links the viewPager Layout to viewPager.
     * Links the appropriate adapter to populate the view inside the tab window of the history UI.
     * Gets the appropriate tab position based on user selection and sets the adapter to the
     * selected tab.
     *
     * @param savedInstanceState This represents the current saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_history_ui);

        //retrieve user object
        retrieveUserDetails();

        //put user object into bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), getIntent().getExtras());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) {
                    pagerAdapter.notifyDataSetChanged();
                } else if (tab.getPosition() == 1) {
                    pagerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    /**
     * Retrieves the user details.
     */
    public void retrieveUserDetails() {
        Intent intent = getIntent();
        user = (Patient) intent.getSerializableExtra("user");
    }

    /**
     * Starts the activity for PatientHomeUI using its intent when the home button is selected.
     *
     * @param view This refers to Home button located at the bottom of the interface
     *             found in activity_patient_history_ui xml file.
     */
    public void viewHome(View view) {
        Intent intent = new Intent(this, PatientHomeUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Starts the activity for PatientHistoryUI when the history button is selected.
     *
     * @param view This refers to History button located at the bottom of the interface
     *             found in activity_gp_history_ui xml file.
     */
    public void viewHistory(View view) {
    }

    /**
     * Starts the activity for PatientSettingsUI using its intent when the settings button is
     * selected.
     *
     * @param view This refers to Settings button located at the bottom of the interface
     *             found in activity_patient_history_ui xml file.
     */
    public void viewSettings(View view) {
        Intent intent = new Intent(this, PatientSettingsUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Parses the user details to the next intent.
     *
     * @param intent The represents the next intent.
     */
    public void parseUserDetails(Intent intent) {
        intent.putExtra("user", user);
    }
}

