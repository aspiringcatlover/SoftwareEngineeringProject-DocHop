package com.example.dochop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Represents a Setting User Interface for General Practitioners(GP).
 * The user interface is only presented to GPs.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class GpSettingsUI extends AppCompatActivity {

    /**
     * The EditText fields in the gp_settings_ui.
     */
    private EditText username, password, firstName, lastName, age, email, contact;

    /**
     * The RadioButton fields in gp_settings_ui.
     */
    private RadioButton maleRadioButton, femaleRadioButton;

    /**
     * The ImageView field in gp_settings_ui.
     */
    private ImageView certImage;

    /**
     * The Uri in GPSettingsUI.
     */
    private Uri imageUri;

    /**
     * The IMAGE_PICK_CODE used in GPSettingsUI.
     */
    private static final int IMAGE_PICK_CODE = 1000;

    /**
     * The PERMISSION_CODE used in GPSettingsUI.
     */
    private static final int PERMISSION_CODE = 1001;

    /**
     * Represents the GeneralPractitioner object passed in the GPSettingsUI.
     */
    private GeneralPractitioner user;

    /**
     * Represents the toast passed in the GPSettingsUI.
     */
    private Toast toast;

    /**
     * Links the EditText fields and RadioButton fields from the gp_settings_ui xml file to the
     * GPSettingsUI java file.
     * The method retrieveUserDetails() is called to retrieve the current user of the application.
     * The attributes of the current user are loaded into the EditText fields and are displayed in
     * the user interface.
     * Checkbox showPassword is generated and assigned to the checkbox in the gp_settings_ui xml
     * file.
     * The medical certificate linked to the current GP is loaded into the certImage ImageView.
     * OnCheckedChangeListener is assigned to showPassword which toggles the visibility of the
     * password EditText field when showPassword Checkbox is checked.
     *
     * @param savedInstanceState This represents the current saved instance state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_settings_ui);

        retrieveUserDetails();

        username = findViewById(R.id.GPUsernamePrompt);
        password = findViewById(R.id.GPPasswordPrompt);
        firstName = findViewById(R.id.GPFirstNamePrompt);
        lastName = findViewById(R.id.GPLastNamePrompt);
        age = findViewById(R.id.GPAgePrompt);
        email = findViewById(R.id.GPEmailPrompt);
        contact = findViewById(R.id.GPContactPrompt);

        maleRadioButton = findViewById(R.id.GPGenderMaleButton);
        femaleRadioButton = findViewById(R.id.GPGenderFemaleButton);

        certImage = findViewById(R.id.GPMedCertPic);
        Button certButton = findViewById(R.id.GPMedCertButton);

        CheckBox showPassword = findViewById(R.id.GPShowPassword);

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

        displayGPCert();

        certButton.setOnClickListener(v -> {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                pickImageFromGallery();
            }
        });

        showPassword.setOnCheckedChangeListener((buttonView, b) -> {
            if (b) {
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });
    }

    /**
     * The details of the current user is retrieved and stored in the gp object, user.
     */
    public void retrieveUserDetails() {
        Intent intent = getIntent();
        user = (GeneralPractitioner) intent.getSerializableExtra("user");
    }

    /**
     * Loads the medical certificate linked to the current user from the database to the certImage
     * ImageView.
     */
    public void displayGPCert() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("General Practitioner certificates/" + user.getUsername() + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(bytes -> {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            certImage.setImageBitmap(bitmap);
        });
    }

    /**
     * Loads the image selected from the user gallery into the certImage ImageView.
     */
    public void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    /**
     * Checks if the user has allowed the application to access the gallery of the device it is
     * being used on.
     *
     * @param requestCode  This represents the request code.
     * @param permissions  This represents permissions.
     * @param grantResults This represents whether permission is granted.
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
     * Checks if permission has been granted before loading the image selected in the certImage
     * ImageView.
     *
     * @param requestCode This represents the requestCode.
     * @param resultCode  This represents the resultCode.
     * @param data        This represents the data.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            assert data != null;
            certImage.setImageURI(data.getData());
            imageUri = data.getData();
        }
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
                    GeneralPractitionerControl generalPractitionerControl = new GeneralPractitionerControl();
                    generalPractitionerControl.removeUser(user);
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
     * GPHomeUI subsequently.
     */
    public void saveData() {
        GeneralPractitionerControl generalPractitionerControl = new GeneralPractitionerControl();
        generalPractitionerControl.saveMedicalCertificate(this, imageUri, username.getText().toString());

        String gender;
        if (maleRadioButton.isChecked()) {
            gender = maleRadioButton.getText().toString();
        } else {
            gender = femaleRadioButton.getText().toString();
        }
        boolean medicalLicense = certImage.getDrawable() != null;

        user = generalPractitionerControl.saveUser(firstName.getText().toString(), lastName.getText().toString(),
                Integer.parseInt(age.getText().toString()), gender, contact.getText().toString(), email.getText().toString(),
                username.getText().toString(), password.getText().toString(), medicalLicense);

        Intent intent = new Intent(this, GpHomeUI.class);
        Toast.makeText(this, "Save successful", Toast.LENGTH_SHORT).show();
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Redirects the user to the WelcomeScreenUI.
     *
     * @param view This represents the view.
     */
    public void logout(View view) {
        Intent intent = new Intent(this, WelcomeScreenUI.class); // change to UI page
        Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    /**
     * Redirects the user to the PatientHomeUI.
     *
     * @param view This represents the view.
     */
    public void viewHome(View view) {
        Intent intent = new Intent(this, GpHomeUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Redirects the user to the GPHistoryUI.
     *
     * @param view This represents the view.
     */
    public void viewHistory(View view) {
        Intent intent = new Intent(this, GpHistoryUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Redirects the user to the GPSettingsUI.
     *
     * @param view This represents the view.
     */
    public void viewSettings(View view) {
    }

    /**
     * Parse the user details to the next intent.
     *
     * @param intent The represents the next intent.
     */
    public void parseUserDetails(Intent intent) {
        intent.putExtra("user", user);
    }
}