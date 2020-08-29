package com.example.dochop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Represents the Patient Registration UI Activity Class.
 * This class creates the UI for patient registration process.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class PatientRegistrationUI extends AppCompatActivity {

    /**
     * EditText fields that stores the user inputted details such as name,age and height.
     */
    private EditText et_first_name, et_last_name, et_age, et_height, et_weight, et_email, et_contact_number, et_medical_history;

    /**
     *This represents a RadioGroup reference.
     */
    private RadioGroup radioGroup;

    /**
     * Create the android UI view for patient registration.
     *
     * @param savedInstanceState This represents the savedInstanceState.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_registration_ui);

        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_age = findViewById(R.id.et_age);
        radioGroup = findViewById(R.id.rg_gender);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        et_email = findViewById(R.id.et_email);
        et_contact_number = findViewById(R.id.et_contact_number);
        et_medical_history = findViewById(R.id.et_medical_history);
    }

    /**
     * Go to the next activity PatientAccountCreationUI, which is the next stage in patient
     * registration.
     *
     * @param view This represents the view.
     */
    public void goToAccountCreation(View view) {
        String first_name = et_first_name.getText().toString();
        String last_name = et_last_name.getText().toString();
        String age = et_age.getText().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String gender = radioButton.getText().toString();
        String height = et_height.getText().toString();
        String weight = et_weight.getText().toString();
        String email = et_email.getText().toString();
        String contact_number = et_contact_number.getText().toString();
        String medical_history = et_medical_history.getText().toString();

        Intent intent = new Intent(this, PatientAccountCreationUI.class);
        intent.putExtra("PatientDetails", new String[]{first_name, last_name, age, gender, contact_number, email, height, weight, medical_history});
        startActivity(intent);
    }
}
