package com.example.dochop;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Represents the controller class for General Practitioners (GPs).
 * Acts as an interface between the GP User Interfaces (UIs) and the GP Object.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class GeneralPractitionerControl {

    /**
     * The database reference for general practitioner objects.
     */
    private DatabaseReference fireBase = FirebaseDatabase.getInstance().getReference().child("User").child("GeneralPractitioner");

    /**
     * The database storage reference for general practitioner objects.
     */
    private StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("General Practitioner certificates");

    /**
     * Saves the information of the GP into the corresponding GP object in the database, before
     * returning the updated user.
     *
     * @param firstName      The first name of the GP object.
     * @param lastName       The last name of the GP object.
     * @param age            The age of the GP object.
     * @param gender         The gender of the GP object.
     * @param phoneNumber    The phone number of the GP object.
     * @param email          The email of the GP object.
     * @param username       The username of the GP object.
     * @param password       The password of the GP object.
     * @param medicalLicense The status of the medical license of the GP object.
     * @return returns the updated GP object after the data has been saved.
     */
    GeneralPractitioner saveUser(String firstName, String lastName, int age, String gender, String phoneNumber, String email,
                                 String username, String password, boolean medicalLicense) {
        GeneralPractitioner updatedUser = new GeneralPractitioner(firstName, lastName, age, gender, phoneNumber, email, 0, 0, username, password, medicalLicense);
        fireBase.child(updatedUser.getUsername()).setValue(updatedUser);
        return updatedUser;
    }

    /**
     * Updates and saves the medical certificate of the corresponding GP into the database.
     *
     * @param context  This represents the application's context.
     * @param imageUri This represents the imageUri of the medical certificate to be updated.
     * @param username This represents the username of the GP.
     */
    void saveMedicalCertificate(Context context, Uri imageUri, String username) {
        if (imageUri != null) {
            fileRef.child(username + "." + getFileExtension(context, imageUri)).putFile(imageUri);
        }
    }

    /**
     * Obtains the file extension for the medical certificate for the database.
     *
     * @param context  This represents the application's context.
     * @param imageUri This represents the imageUri of the image.
     * @return returns the file extension.
     */
    private String getFileExtension(Context context, Uri imageUri) {
        ContentResolver contentResolver = context.getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    /**
     * Updates the location of the GP.
     *
     * @param gp        This represents the GP whose location is being updated.
     * @param latitude  This represents the latitude coordinate to be updated.
     * @param longitude This represents the longitude coordinate to be updated.
     * @return returns the updated GP object.
     */
    GeneralPractitioner updateLocation(GeneralPractitioner gp, double latitude, double longitude) {
        fireBase.child(gp.getUsername()).child("latitude").setValue(latitude);
        fireBase.child(gp.getUsername()).child("longitude").setValue(longitude);
        gp.setLatitude(latitude);
        gp.setLongitude(longitude);
        return gp;
    }

    /**
     * Removes the current GP from the database.
     *
     * @param gp This represents the current GP.
     */
    void removeUser(GeneralPractitioner gp) {
        fireBase.child(gp.getUsername()).removeValue();
    }
}
