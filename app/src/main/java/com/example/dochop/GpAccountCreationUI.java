package com.example.dochop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Represents the GP Account Creation activity.
 * It is the 2nd stage of the GP registration process, where the GP will choose their username and
 * password.
 * GP will also upload his or her medical certificate.
 * All GP information will be saved into the firebase.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class GpAccountCreationUI extends AppCompatActivity {

    /**
     * ImageView containing GP's medical certificate.
     */
    private ImageView gpCert;

    /**
     * Uri reference to the image.
     */
    private Uri imageUri;

    /**
     * This constant represents the image pick code
     */
    private static final int IMAGE_PICK_CODE = 1000;

    /**
     * This constant represents the permission code.
     */
    private static final int PERMISSION_CODE = 1001;

    /**
     * EditText containing GP's username and password.
     */
    private EditText username, password, retryPassword;

    /**
     * This is a reference to a toast object
     */
    private Toast toast;

    /**
     * Main method that creates the UI view for GP Registration.
     *
     * @param savedInstanceState This represents the savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_account_creation_ui);
        gpCert = findViewById(R.id.iv_gp_reg_cert);
        username = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        retryPassword = findViewById(R.id.et_retype_password);
        Button uploadButton = findViewById(R.id.button_gp_reg_cert);
        uploadButton.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        });
    }

    /**
     * Creates a new intent that allows the GP to choose their medical certificate from their
     * gallery to upload.
     */
    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    /**
     * Executes the pickImageFromGallery method if permissions are granted by the user.
     *
     * @param requestCode This represents the requestCode needed.
     * @param permissions This represents the permissions needed.
     * @param grantResults This represents the result granted.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Sets the image file that the GP uploaded into a URI.
     *
     * @param requestCode This represents the request code.
     * @param resultCode This represents the result code.
     * @param data This represents the data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            assert data != null;
            gpCert.setImageURI(data.getData());
            imageUri = data.getData();
        }
    }

    /**
     * Calls usernameGpCheck() to check whether username has already been taken.
     *
     * @param view This represents the view.
     */
    public void finishAccountCreation(View view) {
        usernameGpCheck();
    }

    /**
     * Checks whether GP's username already exists in the GP section of the Firebase database.
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
     * Checks whether GP's username already exists in the Patient section of the Firebase database.
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
     * Check if the reentered password matches the password entered.
     */
    public void passwordCheck() {
        if (password.getText().toString().equals(retryPassword.getText().toString()))
            saveData();
        else {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(this, "Passwords do not match, please try again.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Saves the GP's information and pushes it into the Firebase database.
     * Also starts the GP home screen.
     */
    public void saveData() {
        String[] gpDetails = getIntent().getStringArrayExtra("GpDetails");

        GeneralPractitionerControl generalPractitionerControl = new GeneralPractitionerControl();
        boolean medicalLicense = gpCert.getDrawable() != null;
        generalPractitionerControl.saveMedicalCertificate(this, imageUri, username.getText().toString());
        assert gpDetails != null;
        GeneralPractitioner gp = generalPractitionerControl.saveUser(gpDetails[0], gpDetails[1], Integer.parseInt(gpDetails[2]), gpDetails[3],
                gpDetails[4], gpDetails[5], username.getText().toString(), password.getText().toString(), medicalLicense);

        Toast toast = Toast.makeText(getApplicationContext(), "Sign Up Completed!", Toast.LENGTH_SHORT);
        toast.show();

        Intent intent = new Intent(this, GpHomeUI.class);
        parseUserDetails(intent, gp);
        startActivity(intent);
    }

    /**
     * Parses the user details to the next intent.
     *
     * @param intent This represents the intent.
     * @param user This represents the user object to be passed with the intent.
     */
    public void parseUserDetails(Intent intent, GeneralPractitioner user) {
        intent.putExtra("user", user);
    }
}
