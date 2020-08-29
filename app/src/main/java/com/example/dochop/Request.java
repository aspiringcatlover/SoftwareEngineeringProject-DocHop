package com.example.dochop;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents a Request object.
 * There can be one or many Request objects.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class Request implements Serializable {

    /**
     * The id of the Request.
     */
    private int id;

    /**
     * The Patient object that made the request.
     */
    private Patient patient;

    /**
     * The symptoms that the Patient have.
     */
    private String symptoms;

    /**
     * Whether this request is handled by a General Practitioner or an ambulance.
     */
    private boolean isGP;

    /**
     * The General Practitioner object that handled the request.
     */
    private GeneralPractitioner gp;

    /**
     * The amount of time elapsed since the request was made.
     */
    private String acceptanceTime;

    /**
     * The arrival time of the General Practitioner.
     */
    private String arrivalTime;

    /**
     * The time of request completion.
     */
    private String completionTime;

    /**
     * The date that the request object was created.
     */
    private String date;

    /**
     * The status of the request.
     */
    private String status;


    /**
     * The state of request status available.
     */
    public enum Status {
        Pending, Accepted, Arrived, Completed
    }

    /**
     * Creates a new request object.
     */
    public Request() {
    }

    /**
     * Creates a new request with the following parameters.
     *
     * @param id       This represents the new id of the request.
     * @param patient  This represents the new patient that made the request.
     * @param symptoms This represents the new symptoms that the patient is having.
     */
    public Request(int id, Patient patient, String symptoms) {
        this.id = id;
        this.patient = patient;
        this.symptoms = symptoms;
        isGP = true;
        date = LocalDate.now().toString();
        status = Status.Pending.toString();
    }

    /**
     * Gets the id of this request.
     *
     * @return This request's id.
     */
    public int getId() {
        return id;
    }

    /**
     * Changes the id of this request.
     *
     * @param id This request's new id.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the patient object that made this request.
     *
     * @return This request's patient object.
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Changes the patient object of this request.
     *
     * @param patient This request's patient object.
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Gets the patient symptoms.
     *
     * @return The symptoms of the patient who made this request.
     */
    public String getSymptoms() {
        return symptoms;
    }

    /**
     * Changes the patient symptoms.
     *
     * @param symptoms The new symptoms of the patient who made this request.
     */
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    /**
     * Gets how this request is being handled.
     *
     * @return true if request is handled by a gp and false if handled by ambulance.
     */
    public boolean getIsGP() {
        return isGP;
    }

    /**
     * Changes how this request is being handled.
     *
     * @param GP true if request is handled by a gp and false if handled by ambulance.
     */
    public void setIsGP(boolean GP) {
        isGP = GP;
    }

    /**
     * Gets the general practitioner object.
     *
     * @return The general practitioner who handled this request.
     */
    public GeneralPractitioner getGp() {
        return gp;
    }

    /**
     * Changes the general practitioner who handled this request.
     *
     * @param gp The general practitioner who handled this request.
     */
    public void setGp(GeneralPractitioner gp) {
        this.gp = gp;
    }

    /**
     * Gets the acceptance time of this request.
     *
     * @return This request's acceptance time.
     */
    public String getAcceptanceTime() {
        return acceptanceTime;
    }

    /**
     * Changes the acceptance time of this request.
     */
    void setAcceptanceTime() {
        this.acceptanceTime = LocalTime.now().toString(); //android method to get local time
    }

    /**
     * Changes the  acceptance time of this request.
     *
     * @param acceptanceTime This request's new acceptance time.
     */
    public void setAcceptanceTime(String acceptanceTime) {
        this.acceptanceTime = acceptanceTime;
    }

    /**
     * Gets the arrival time of the general practitioner.
     *
     * @return This request's general practitioner's arrival time.
     */
    public String getArrivalTime() {
        return arrivalTime;
    }

    /**
     * Changes the arrival time of the general practitioner.
     */
    void setArrivalTime() {
        this.arrivalTime = LocalTime.now().toString(); //android method to get local time
    }

    /**
     * Changes the arrival time of the general practitioner.
     *
     * @param arrivalTime This request's general practitioner's new arrival time.
     */
    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    /**
     * Gets the completion time of the request.
     *
     * @return This request's completion time.
     */
    public String getCompletionTime() {
        return completionTime;
    }

    /**
     * Changes the completion time of the request.
     */
    void setCompletionTime() {
        this.completionTime = LocalTime.now().toString(); //android method to get local time
    }

    /**
     * Changes the completion time of the request.
     *
     * @param completionTime This request's new completion time.
     */
    public void setCompletionTime(String completionTime) {
        this.completionTime = completionTime;
    }

    /**
     * Gets the date of the request creation.
     *
     * @return This request's date of creation.
     */
    public String getDate() {
        return date;
    }

    /**
     * Changes the date of the request creation.
     */
    public void setDate() {
        this.date = LocalDate.now().toString(); //android method to get date
    }

    /**
     * Changes the date of the request creation.
     *
     * @param date This request's new date of creation.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * Gets the status of the request.
     *
     * @return This request's status.
     */
    public String getStatus() {
        return status;
    }

    /**
     * This sets the status by a choice of 1-4.
     *
     * @param choice This request's new status.
     */
    void setStatusByChoice(int choice) {
        switch (choice) {
            case 1:
                status = Status.Pending.toString();
                break;
            case 2:
                status = Status.Accepted.toString();
                break;
            case 3:
                status = Status.Arrived.toString();
                break;
            case 4:
                status = Status.Completed.toString();
                break;
        }
    }

    /**
     * Changes the status of the request.
     *
     * @param status This request's new status.
     */
    public void setStatus(String status) {
        this.status = status;
    }
}
