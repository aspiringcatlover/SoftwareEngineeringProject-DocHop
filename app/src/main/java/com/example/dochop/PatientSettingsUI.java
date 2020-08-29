package com.example.dochop;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Represents a Setting User Interface for Patients.
 * The user interface is only presented to patients.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class PatientSettingsUI extends AppCompatActivity {

    /**
     * The EditText fields in the patient_settings_ui.
     */
    private EditText username, password, firstName, lastName, age, height, weight, email, contact, medical;

    /**
     * The RadioButton fields in patient_settings_ui.
     */
    private RadioButton maleRadioButton, femaleRadioButton;

    /**
     * Represents the Patient object passed in the PatientSettingsUI.
     */
    private Patient user;

    /**
     * Represents the toast passed in the patient_settings_ui.
     */
    private Toast toast;

    /**
     * Links the EditText fields and RadioButton fields from the patient_settings_ui xml file to the
     * PatientSettingsUI java file.
     * The method retrieveUserDetails() is called to retrieve the current user of the application.
     * The attributes of the current user are loaded into the EditText fields and are displayed in
     * the user interface.
     * Checkbox showPassword is generated and assigned to the checkbox in the patient_settings_ui
     * xml file.
     * OnCheckedChangeListener is assigned to showPassword which toggles the visibility of the
     * password EditText field when showPassword Checkbox is checked.
     *
     * @param savedInstanceState This represents the current saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_settings_ui);

        retrieveUserDetails();

        username = findViewById(R.id.PatientUsernamePrompt);
        password = findViewById(R.id.PatientPasswordPrompt);
        firstName = findViewById(R.id.PatientFirstNamePrompt);
        lastName = findViewById(R.id.PatientLastNamePrompt);
        age = findViewById(R.id.PatientAgePrompt);
        email = findViewById(R.id.PatientEmailPrompt);
        contact = findViewById(R.id.PatientContactPrompt);

        maleRadioButton = findViewById(R.id.PatientGenderMaleButton);
        femaleRadioButton = findViewById(R.id.PatientGenderFemaleButton);

        height = findViewById(R.id.PatientHeightPrompt);
        weight = findViewById(R.id.PatientWeightPrompt);
        medical = findViewById(R.id.PatientMedHistPrompt);

        CheckBox showPassword = findViewById(R.id.PatientShowPassword);

        username.setText(user.getUsername());
        password.setText(user.getPassword());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        age.setText(String.valueOf(user.getAge()));
        email.setText(user.getEmail());
        contact.setText(user.getPhoneNumber());

        if (user.getGender().equals("Male")) {
            maleRadioButton.setChecked(true);
            femaleRadioButton.setChecked(false);
        } else {
            maleRadioButton.setChecked(false);
            femaleRadioButton.setChecked(true);
        }

        height.setText(String.valueOf(user.getHeight()));
        weight.setText(String.valueOf(user.getWeight()));
        medical.setText(user.getMedicalHistory());

        showPassword.setOnCheckedChangeListener((buttonView, b) -> {
            if (b) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    /**
     * The details of the current user is retrieved and stored in the patient object, user.
     */
    public void retrieveUserDetails() {
        Intent intent = getIntent();
        user = (Patient) intent.getSerializableExtra("user");
    }

    /**
     * Redirects the user to the WelcomeScreenUI.
     *
     * @param view This represents the view.
     */
    public void logout(View view) {
        Intent intent = new Intent(this, WelcomeScreenUI.class);
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT);
        toast.show();
        startActivity(intent);
    }

    /**
     * Checks whether the new username to be saved already exists in the database by calling the
     * method usernameGpCheck().
     * If the username is unique, the method saveData() is called to save the new settings of the
     * current user.
     *
     * @param view This represents the view.
     */
    public void save(View view) {
        String oldUsername = user.getUsername();
        String newUsername = username.getText().toString();
        if (!oldUsername.equals(newUsername)) {
            usernameGpCheck();
        } else {
            saveData();
        }
    }

    /**
     * Checks whether the username to be saved is unique.
     * The usernames of general practitioners stored in the database are checked first.
     * If no general practitioners have the username, the method usernamePatientCheck() is called to
     * check the usernames of the patients stored in the database.
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
     * Checks whether any existing patients in the database has the same username as the username to
     * be stored.
     */
    public void usernamePatientCheck() {
        FirebaseDatabase.getInstance().getReference().child("User").child("Patient").child(username.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    PatientControl patientControl = new PatientControl();
                    patientControl.removeUser(user);
                    saveData();
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
     * Saves the current attributes of the patient into the database and redirects the user to the
     * PatientHomeUI subsequently.
     */
    public void saveData() {
        String gender;
        if (maleRadioButton.isChecked()) {
            gender = maleRadioButton.getText().toString();
        } else {
            gender = femaleRadioButton.getText().toString();
        }

        PatientControl patientControl = new PatientControl();
        user = patientControl.saveUser(firstName.getText().toString(), lastName.getText().toString(),
                Integer.parseInt(age.getText().toString()), gender, contact.getText().toString(), email.getText().toString(),
                user.getLatitude(), user.getLongitude(), username.getText().toString(), password.getText().toString(),
                Float.parseFloat(height.getText().toString()), Float.parseFloat(weight.getText().toString()), medical.getText().toString());

        Intent intent = new Intent(this, PatientHomeUI.class);
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT);
        toast.show();
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Redirects the user to the PatientHomeUI.
     *
     * @param view This represents the view.
     */
    public void viewHome(View view) {
        Intent intent = new Intent(this, PatientHomeUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Redirects the user to the PatientHistoryUI.
     *
     * @param view This represents the view.
     */
    public void viewHistory(View view) {
        Intent intent = new Intent(this, PatientHistoryUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Redirects the user to the PatientSettingsUI.
     *
     * @param view This represents the view.
     */
    public void viewSettings(View view) {
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
