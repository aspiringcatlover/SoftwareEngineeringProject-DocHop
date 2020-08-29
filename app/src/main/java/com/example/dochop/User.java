package com.example.dochop;

import java.io.Serializable;

/**
 * Represents a User object.
 * There can be one or many User objects.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */

public class User implements Serializable {

    /**
     * The first name of the User.
     */
    private String firstName;

    /**
     * The last name of the User.
     */
    private String lastName;

    /**
     * The age of the User.
     */
    private int age;

    /**
     * The gender of the User.
     */
    private String gender;

    /**
     * The phone number of the User.
     */
    private String phoneNumber;

    /**
     * The email of the User.
     */
    private String email;

    /**
     * The latitude of the User location.
     */
    private double latitude;

    /**
     * The longitude of the User location.
     */
    private double longitude;

    /**
     * The username used by the User.
     */
    private String username;

    /**
     * The password used by the User.
     */
    private String password;

    /**
     * Creates a new User.
     */
    public User() {
    }

    /**
     * Creates a new user with the following parameters.
     *
     * @param firstName   This represents the new first name.
     * @param lastName    This represents the new last name.
     * @param age         This represents the new age.
     * @param gender      This represents the new gender.
     * @param phoneNumber This represents the new phone number.
     * @param email       This represents the new email.
     * @param latitude    This represents the new latitude.
     * @param longitude   This represents the new longitude.
     * @param username    This represents the new username.
     * @param password    This represents the new password.
     */
    public User(String firstName, String lastName, int age, String gender, String phoneNumber, String email, double latitude, double longitude, String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the first name of this User.
     *
     * @return this User's first name.
     */
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * Changes the first name of this User.
     *
     * @param firstName this User's new first name.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the last name of this User.
     *
     * @return this User's last name.
     */
    public String getLastName() {
        return this.lastName;
    }

    /**
     * Changes the last name of this User.
     *
     * @param lastName this User's new last name.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the age of this User.
     *
     * @return this User's age.
     */
    public int getAge() {
        return this.age;
    }

    /**
     * Changes the age of this User.
     *
     * @param age this User's new age.
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Gets the gender of this User.
     *
     * @return this User's gender.
     */
    public String getGender() {
        return this.gender;
    }

    /**
     * Changes the gender of this User.
     *
     * @param gender this User's new gender.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Gets the phone number of this User.
     *
     * @return this User's phone number.
     */
    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Changes the phone number of this User.
     *
     * @param phoneNumber this User's new phone number.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the email of this User.
     *
     * @return this User's email.
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Changes the email of this User.
     *
     * @param email this User's new email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the latitude of this User's location.
     *
     * @return this User's location latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Changes the latitude of this User's location.
     *
     * @param latitude this User's location latitude.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of this User's location.
     *
     * @return this User's location longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Changes the longitude of this User's location.
     *
     * @param longitude this User's location longitude.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the username of this User.
     *
     * @return this User's username.
     */
    public String getUsername() {
        return this.username;
    }

    /**
     * Changes the username of this User.
     *
     * @param username this User's new username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of this User.
     *
     * @return this User's password.
     */
    public String getPassword() {
        return this.password;
    }

    /**
     * Changes the password of this User.
     *
     * @param password this User's new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}


