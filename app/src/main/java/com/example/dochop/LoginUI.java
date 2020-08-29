package com.example.dochop;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * Represents a Login User Interface for Users.
 * The user interface is presented to both patients and GPs.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class LoginUI extends AppCompatActivity {

    /**
     * The EditText fields in the activity_login_ui.
     */
    private EditText username, password;

    /**
     * The reference to the firebase database.
     */
    private DatabaseReference reference;

    /**
     * The string storing the value of the user password in the database.
     */
    private String dataPassword;

    /**
     * The string representing the key of the child of the current database reference.
     */
    private String child;

    /**
     * Represents the toast passed in the patient_settings_ui.
     */
    private Toast toast;

    /**
     * Links the EditText fields and from the activity_login_ui xml file to the LoginUI java file.
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
        setContentView(R.layout.activity_login_ui);

        username = findViewById(R.id.username);
        password = findViewById(R.id.loginPassword);
        CheckBox showPassword = findViewById(R.id.LoginShowPassword);

        showPassword.setOnCheckedChangeListener((buttonView, b) -> {
            if (b) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    /**
     * Checks if the current login fields are empty.
     * If the fields are not empty, a check is executed to check if the credentials exist in the
     * database.
     *
     * @param view This represents the view.
     */
    public void login(View view) {
        if (username.getText().toString().equals("") || password.getText().toString().equals("")) {
            empty();
            return;
        }
        String primaryKey = username.getText().toString().trim();
        PatientCheck(primaryKey);
    }

    /**
     * Generates a toast indicating that the login fields are to be filled.
     */
    public void empty() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(getApplicationContext(), "Please fill up the required fields", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Checks if the patient credentials exist in the firebase database.
     * The string primaryKey is used as an identifier and checks the patient objects in the database.
     * If the identifier does not match any patient objects, the general practitioner objects are
     * checked using the method GeneralPractitionerCheck(primaryKey).
     *
     * @param primaryKey This represents the String primaryKey used to identify the user in the
     *                   database.
     */
    public void PatientCheck(String primaryKey) {
        reference = FirebaseDatabase.getInstance().getReference().child("User").child("Patient");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : Objects.requireNonNull(dataSnapshot).getChildren()) {
                    child = Objects.requireNonNull(ds.getKey()).trim();
                    if (child.equals(primaryKey)) {
                        dataPassword = Objects.requireNonNull(dataSnapshot.child(primaryKey).child("password").getValue()).toString().trim();
                        if (password.getText().toString().trim().equals(dataPassword)) {
                            Patient user = ds.getValue(Patient.class);
                            PatientNext(user);
                            return;
                        } else {
                            invalid();
                        }
                    }
                }
                GeneralPractitionerCheck(primaryKey);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Checks if the primaryKey identifier matches the credentials of any general practitioner
     * objects.
     *
     * @param primaryKey This represents the String primaryKey used to identify the user in the
     *                   database.
     */
    public void GeneralPractitionerCheck(String primaryKey) {
        reference = FirebaseDatabase.getInstance().getReference().child("User").child("GeneralPractitioner");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : Objects.requireNonNull(dataSnapshot).getChildren()) {
                    child = Objects.requireNonNull(ds.getKey()).trim();
                    if (child.equals(primaryKey)) {
                        dataPassword = Objects.requireNonNull(dataSnapshot.child(primaryKey).child("password").getValue()).toString().trim();
                        if (password.getText().toString().trim().equals(dataPassword)) {
                            GeneralPractitioner user = ds.getValue(GeneralPractitioner.class);
                            GeneralPractitionerNext(user);
                            return;
                        } else {
                            invalid();
                        }
                    }
                }
                invalid();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Redirects the user to the PatientHomeUI if the credentials match that of a patient object.
     *
     * @param user This represents the current user.
     */
    public void PatientNext(Patient user) {
        Intent intent = new Intent(this, PatientHomeUI.class);
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT);
        toast.show();
        parseUserDetails(intent, user);
        startActivity(intent);
    }

    /**
     * Redirects the user to the GPHomeUI if the credentials match that of a general practitioner
     * object.
     *
     * @param user This represents the current user.
     */
    public void GeneralPractitionerNext(GeneralPractitioner user) {
        Intent intent = new Intent(this, GpHomeUI.class);
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT);
        toast.show();
        parseUserDetails(intent, user);
        startActivity(intent);
    }

    /**
     * Generates a toast indicating that the login credentials entered are invalid.
     */
    public void invalid() {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Parse the user details to the next intent.
     *
     * @param intent The represents the next intent.
     */
    public void parseUserDetails(Intent intent, User user) {
        intent.putExtra("user", user);
    }
}