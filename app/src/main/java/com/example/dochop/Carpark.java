package com.example.dochop;

/**
 * Represents a Carpark object.
 * There can be one or many Carpark objects.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class Carpark {

    /**
     * The carpark number of the carpark.
     */
    private String carparkNumber;

    /**
     * The latitude of the carpark's location.
     */
    private String latitude;

    /**
     * The longitude of the carpark's location.
     */
    private String longitude;

    /**
     * The amount of lots available in the carpark.
     */
    private String lotsAvailable;

    /**
     * Creates a new carpark object.
     */
    Carpark() {

    }

    /**
     * Gets the carpark number of this carpark.
     *
     * @return this carpark's carpark number.
     */
    String getCarparkNumber() {
        return carparkNumber;
    }

    /**
     * Changes the carpark number of this carpark.
     *
     * @param carparkNumber this carpark's new carpark number.
     */
    void setCarparkNumber(String carparkNumber) {
        this.carparkNumber = carparkNumber;
    }

    /**
     * Gets the latitude of this carpark's location.
     *
     * @return this carpark's location latitude.
     */
    public String getLatitude() {
        return latitude;
    }

    /**
     * Changes the latitude of this carpark's location.
     *
     * @param latitude this carpark's location latitude.
     */
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    /**
     * Gets the longitude of this carpark's location.
     *
     * @return this carpark's location longitude.
     */
    public String getLongitude() {
        return longitude;
    }

    /**
     * Changes the longitude of this carpark's location.
     *
     * @param longitude this carpark's location longitude.
     */
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the amount of available lots in this carpark.
     *
     * @return this carpark's amount of available lots.
     */
    public String getLotsAvailable() {
        return lotsAvailable;
    }

    /**
     * Changes the amount of available lots in this carpark.
     *
     * @param lotsAvailable this carpark's amount of available lots.
     */
    void setLotsAvailable(String lotsAvailable) {
        this.lotsAvailable = lotsAvailable;
    }
}
