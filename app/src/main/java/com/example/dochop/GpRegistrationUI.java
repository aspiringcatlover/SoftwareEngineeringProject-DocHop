package com.example.dochop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Represents the GP Registration UI Activity. It is the first stage of the GP registration process,
 * where the GP keys in his or her basic details such as name, age, gender etc.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class GpRegistrationUI extends AppCompatActivity {

    /**
     * Gp's first name, last name, age, email and contact number.
     */
    private EditText et_first_name, et_last_name, et_age, et_email, et_contact_number;

    /**
     *represents a RadioGroup reference.
     */
    private RadioGroup radioGroup;

    /**
     * Create the android UI view for gp registration.
     *
     * @param savedInstanceState Represents the savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_registration_ui);

        et_first_name = findViewById(R.id.et_first_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_age = findViewById(R.id.et_age);
        radioGroup = findViewById(R.id.rg_gender);
        et_email = findViewById(R.id.et_email);
        et_contact_number = findViewById(R.id.et_contact_number);
    }

    /**
     * Go the next stage of GP Registration through starting of a new intent.
     *
     * @param view Thie represents the view.
     */
    public void goToAccountCreation(View view) {
        String first_name = et_first_name.getText().toString();
        String last_name = et_last_name.getText().toString();
        String age = et_age.getText().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String gender = radioButton.getText().toString();
        String contact_number = et_contact_number.getText().toString();
        String email = et_email.getText().toString();

        Intent intent = new Intent(this, GpAccountCreationUI.class);
        intent.putExtra("GpDetails", new String[]{first_name, last_name, age, gender, contact_number, email});
        startActivity(intent);
    }
}