package com.example.dochop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Represents the Welcome Screen Interface for all users.
 * Allows a user to login or register a new account as a General Practitioner (GP) or Patient.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class WelcomeScreenUI extends AppCompatActivity {

    /**
     * Radio group to allow for mutually exclusive selection of GP or Patient for account creation.
     */
    private RadioGroup registerSelector;

    /**
     * Radio button to identify that user wishes to create an account as a GP.
     */
    private RadioButton doctorRadio;

    /**
     * Toast passed within this UI.
     */
    private Toast toast;

    /**
     * Starts the UI for the User to login to their account or register a new account.
     *
     * @param savedInstanceState Contains the data most recently supplied in
     *                           onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen_ui);

        registerSelector = findViewById(R.id.register_selector);
        doctorRadio = findViewById(R.id.doctor_radio);
    }

    /**
     * Starts the activity for LoginUI when the Login button is selected.
     *
     * @param view This refers to the Login button.
     */
    public void login(View view) {
        Intent intent = new Intent(this, LoginUI.class);
        startActivity(intent);
    }

    /**
     * Checks if the GP radio button or the patient radio button has been checked.
     * Starts the activity for the respective RegistrationUIs when the Register button is selected.
     * An error is displayed is no radio button is selected and the user is not allowed to continue.
     *
     * @param view This refers to the Register button.
     */
    public void register(View view) {
        if (registerSelector.getCheckedRadioButtonId() == -1) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getApplicationContext(), "Please select User type", Toast.LENGTH_SHORT);
            toast.show();
        } else if (doctorRadio.isChecked()) {
            Intent intent = new Intent(this, GpRegistrationUI.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, PatientRegistrationUI.class);
            startActivity(intent);
        }
    }
}
