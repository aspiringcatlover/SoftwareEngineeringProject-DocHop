package com.example.dochop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Represents the Patient Account Creation UI Activity Class.
 * It is the UI for the final stage of the patient registration.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class PatientAccountCreationUI extends AppCompatActivity {

    /**
     * EditText containing Patient's username and password.
     */
    private EditText username, password, retypePassword;

    /**
     * This is a Toast object
     */
    private Toast toast;

    /**
     * Starts the activity and generates the UI.
     *
     * @param savedInstanceState This represents the savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_account_creation_ui);
        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        retypePassword = findViewById(R.id.et_retype_password);
    }

    /**
     * Calls the method usernameGpCheck() to check whether the Patient username has already been
     * taken.
     *
     * @param view This represents the view.
     */
    public void finishAccountCreation(View view) {
        usernameGpCheck();
    }

    /**
     * Checks whether the Patient's username already exists in the GP section of the Firebase
     * database.
     */
    public void usernameGpCheck() {
        FirebaseDatabase.getInstance().getReference().child("User").child("GeneralPractitioner").child(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    usernamePatientCheck();
                } else {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(getApplicationContext(), "Username already exists. Please select a new username. ", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Checks whether Patient's username already exists in the Patient section of the Firebase
     * database.
     */
    public void usernamePatientCheck() {
        FirebaseDatabase.getInstance().getReference().child("User").child("Patient").child(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    passwordCheck();
                } else {
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(getApplicationContext(), "Username already exists. Please select a new username. ", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Check whether the user's retyped password matches the inputted password.
     */
    public void passwordCheck() {
        if (password.getText().toString().equals(retypePassword.getText().toString()))
            saveData();
        else {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(getApplicationContext(), "Passwords do not match, please try again.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Calls upon the patient control class to save the Patient's account data into firebase.
     * It then starts the patient home UI Activity.
     */
    public void saveData() {
        String[] patientDetails = getIntent().getStringArrayExtra("PatientDetails");

        PatientControl patientControl = new PatientControl();
        assert patientDetails != null;
        Patient patient = patientControl.saveUser(patientDetails[0], patientDetails[1], Integer.valueOf(patientDetails[2]),
                patientDetails[3], patientDetails[4], patientDetails[5], 0, 0, username.getText().toString(),
                password.getText().toString(), Double.valueOf(patientDetails[6]), Double.valueOf(patientDetails[7]), patientDetails[8]);

        Intent intent = new Intent(this, PatientHomeUI.class);
        parseUserDetails(intent, patient);
        startActivity(intent);
    }

    /**
     * Put patient object into intent.
     *
     * @param intent This represents the intent.
     * @param user This represents the user object to be passed with the intent.
     */
    public void parseUserDetails(Intent intent, Patient user) {
        intent.putExtra("user", user);
    }
}
