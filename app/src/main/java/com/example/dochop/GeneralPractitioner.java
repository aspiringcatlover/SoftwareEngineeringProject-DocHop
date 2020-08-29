package com.example.dochop;

import java.io.Serializable;

/**
 * Represents a General Practitioner object.
 * There can be one or many General Practitioner objects.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class GeneralPractitioner extends User implements Serializable {

    /**
     * The status of the general practitioner's medical license, where true means it exists and
     * false means it does not exist.
     */
    private boolean medicalLicense;

    /**
     * Creates a new general practitioner object.
     */
    public GeneralPractitioner() {
    }

    /**
     * Creates a new general practitioner object with the following parameters.
     *
     * @param firstName      This represents the general practitioner's first name.
     * @param lastName       This represents the general practitioner's last name.
     * @param age            This represents the general practitioner's age.
     * @param gender         This represents the general practitioner's gender.
     * @param phoneNumber    This represents the general practitioner's phone number.
     * @param email          This represents the general practitioner's email.
     * @param latitude       This represents the latitude of the general practitioner's current
     *                       location.
     * @param longitude      This represents the longitude of the general practitioner's current
     *                       location.
     * @param username       This represents the username of the general practitioner's account.
     * @param password       This represents the password of the general practitioner's account.
     * @param medicalLicense This represents the status of the general practitioner's medical
     *                       license.
     */
    public GeneralPractitioner(String firstName, String lastName, int age, String gender, String phoneNumber, String email, double latitude, double longitude,
                               String username, String password, boolean medicalLicense) {

        super(firstName, lastName, age, gender, phoneNumber, email, latitude, longitude, username, password);
        this.medicalLicense = medicalLicense;
    }

    /**
     * Gets the status of this general practitioner's medical license.
     *
     * @return this general practitioner's medical license status.
     */
    public boolean getMedicalLicense() {
        return this.medicalLicense;
    }

    /**
     * Changes the status of this general practitioner's medical license.
     *
     * @param medicalLicense this general practitioner's medical license status.
     */
    public void setMedicalLicense(boolean medicalLicense) {
        this.medicalLicense = medicalLicense;
    }
}

