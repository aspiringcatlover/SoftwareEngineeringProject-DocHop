package com.example.dochop;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Represents the controller class for Patients.
 * Acts as an interface between the Patient User Interfaces (UIs) and the Patient Object.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class PatientControl {

    /**
     * The database reference for Patient objects.
     */
    private DatabaseReference fireBase = FirebaseDatabase.getInstance().getReference().child("User").child("Patient");

    /**
     * Saves the information of the Patient into the corresponding Patient object in the database.
     * Returns the updated user.
     *
     * @param firstName      The first name of the Patient object.
     * @param lastName       The last name of the Patient object.
     * @param age            The age of the Patient object.
     * @param gender         The gender of the Patient object.
     * @param phoneNumber    The phone number of the Patient object.
     * @param email          The email of the Patient object.
     * @param latitude       The latitude coordinate of the Patient object.
     * @param longitude      The longitude coordinate of the Patient object.
     * @param username       The username of the Patient object.
     * @param password       The password of the Patient object.
     * @param height         The height of the Patient object.
     * @param weight         The weight of the Patient object.
     * @param medicalHistory The medicalHistory of the Patient object.
     * @return returns the updated Patient object after the data has been saved.
     */
    Patient saveUser(String firstName, String lastName, int age, String gender, String phoneNumber, String email, double latitude, double longitude,
                     String username, String password, double height, double weight, String medicalHistory) {
        Patient updatedUser = new Patient(firstName, lastName, age, gender, phoneNumber, email, latitude, longitude, username, password, height, weight, medicalHistory);
        fireBase.child(updatedUser.getUsername()).setValue(updatedUser);
        return updatedUser;
    }

    /**
     * Updates the location of the Patient.
     *
     * @param patient   This represents the Patient whose location is being updated.
     * @param latitude  This represents the latitude coordinate to be updated.
     * @param longitude This represents the longitude coordinate to be updated.
     * @return returns the updated Patient object.
     */
    Patient updateLocation(Patient patient, double latitude, double longitude) {
        fireBase.child(patient.getUsername()).child("latitude").setValue(latitude);
        fireBase.child(patient.getUsername()).child("longitude").setValue(longitude);
        patient.setLatitude(latitude);
        patient.setLongitude(longitude);
        return patient;
    }

    /**
     * Removes the current Patient from the database.
     *
     * @param patient This represents the current Patient.
     */
    void removeUser(Patient patient) {
        fireBase.child(patient.getUsername()).removeValue();
    }
}
