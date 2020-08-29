package com.example.dochop;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

/**
 * Represents the calculator to determine the nearest carpark to the patient.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class NearestCarparkCalculator extends AsyncTask<ArrayList<Carpark>, Void, Carpark> {

    /**
     * Request that the General Practitioner has accepted.
     */
    private Request request;

    /**
     * Constructor for NearestCarparkCalculator.
     *
     * @param request Request that the General Practitioner has accepted.
     */
    NearestCarparkCalculator(Request request) {
        this.request = request;
    }

    /**
     * Performs the calculation between all the carparks and returns the one which is nearest to the
     * patient of the accepted request.
     *
     * @param carparkArrayLists ArrayList that stores retrieved carpark objects.
     * @return Carpark object that has a location which is nearest to the patient of the accepted
     * request.
     */
    @SafeVarargs
    @Override
    protected final Carpark doInBackground(ArrayList<Carpark>... carparkArrayLists) {
        LatLng requestLatLng = new LatLng(request.getPatient().getLatitude(), request.getPatient().getLongitude());
        Carpark nearestCarpark = new Carpark();
        double nearestDistance = Double.POSITIVE_INFINITY;
        for (Carpark carpark : carparkArrayLists[0]) {
            LatLng carparkLatLng = new LatLng(Double.parseDouble(carpark.getLatitude()), Double.parseDouble(carpark.getLongitude()));
            double distance = SphericalUtil.computeDistanceBetween(carparkLatLng, requestLatLng);
            if (distance < nearestDistance) {
                nearestCarpark = carpark;
                nearestDistance = distance;
            }
        }
        return nearestCarpark;
    }
}
