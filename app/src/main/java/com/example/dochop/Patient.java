package com.example.dochop;

import java.io.Serializable;

/**
 * Represents a Patient object.
 * There can be one or many Patient objects.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class Patient extends User implements Serializable {

    /**
     * The height of the Patient.
     */
    private double height;

    /**
     * The weight of the Patient.
     */
    private double weight;

    /**
     * The medical history of the Patient.
     */
    private String medicalHistory;

    /**
     * Creates a new Patient.
     */
    public Patient() {
    }

    /**
     * Creates a new Patient with the following parameters
     *
     * @param firstName      This represents the new first name.
     * @param lastName       This represents the new last name.
     * @param age            This represents the new age.
     * @param gender         This represents the new gender.
     * @param phoneNumber    This represents the new phone number.
     * @param email          This represents the new email.
     * @param latitude       This represents the new latitude.
     * @param longitude      This represents the new longitude.
     * @param username       This represents the new username.
     * @param password       This represents the new password.
     * @param height         This represents the new height.
     * @param weight         This represents the new weight.
     * @param medicalHistory This represents the new medicalHistory.
     */
    public Patient(String firstName, String lastName, int age, String gender, String phoneNumber, String email, double latitude, double longitude,
                   String username, String password, double height, double weight, String medicalHistory) {

        super(firstName, lastName, age, gender, phoneNumber, email, latitude, longitude, username, password);
        this.height = height;
        this.weight = weight;
        this.medicalHistory = medicalHistory;
    }

    /**
     * Gets the height of this Patient.
     *
     * @return this Patient's height.
     */
    public double getHeight() {
        return this.height;
    }

    /**
     * Changes the height of this Patient.
     *
     * @param height this Patient new height.
     */
    public void setHeight(double height) {
        this.height = height;
    }

    /**
     * Gets the weight of this Patient.
     *
     * @return this Patient's weight.
     */
    double getWeight() {
        return this.weight;
    }

    /**
     * Changes the weight of this Patient.
     *
     * @param weight this Patient new weight.
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Gets the medical history of this Patient.
     *
     * @return this Patient's medical history.
     */
    String getMedicalHistory() {
        return this.medicalHistory;
    }

    /**
     * Changes the medical history of this Patient.
     *
     * @param medicalHistory this Patient new medical history.
     */
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
}
