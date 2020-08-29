package com.example.dochop;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Represents the controller class for Request.
 * Acts as an interface between the Patient and GP User Interfaces (UIs) and the Request Object.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class RequestControl {

    /**
     * The database reference for Request objects.
     */
    private DatabaseReference fireBase = FirebaseDatabase.getInstance().getReference().child("Request");

    /**
     * Generates a new request by a patient.
     *
     * @param nextRequestId The ID of request that is going to the generated.
     * @param patient       The patient object that represents the patient making the request.
     * @param symptoms      The symptoms of the patient that is making the request.
     */
    void makeRequest(int nextRequestId, Patient patient, String symptoms) {
        Request request = new Request(nextRequestId, patient, symptoms);
        //send to fireBase: Requests -> Pending. Each request has ID of request as "key"
        fireBase.child("RequestList").child(String.valueOf(request.getId())).setValue(request);
        nextRequestId++;
        fireBase.child("nextRequestId").setValue(nextRequestId);
    }

    /**
     * Updates the patient info of an existing request.
     *
     * @param patient   Info of the patient that the request will be updated to.
     * @param requestId ID of the target request to be updated.
     */
    void updateUser(Patient patient, int requestId) {
        fireBase.child("RequestList").child(String.valueOf(requestId)).child("patient").setValue(patient);
    }

    /**
     * Converts an existing request to an ambulance request, by setting the boolean variable isGP of
     * a request to false.
     *
     * @param request Request that is to be converted into ambulance request.
     */
    void updateRequestToAmbulance(Request request) {
        request.setIsGP(false);
        request.setAcceptanceTime();
        request.setArrivalTime();
        request.setCompletionTime();
        request.setStatusByChoice(4);
        fireBase.child("RequestList").child(String.valueOf(request.getId())).setValue(request);
    }

    /**
     * Cancels a request by removing it from the firebase database.
     *
     * @param request Request object to be removed.
     */
    void cancelRequest(Request request) {
        fireBase.child("RequestList").child(String.valueOf(request.getId())).removeValue();
    }

    /**
     * Fetches an ArrayList of pending requests in the database.
     *
     * @param dataSnapshot This represents the current data within firebase reference.
     * @return ArrayList containing pending requests.
     */
    ArrayList<Request> fetchPendingRequests(DataSnapshot dataSnapshot) {
        ArrayList<Request> requestArrayList = new ArrayList<>();
        for (DataSnapshot pendingRequest : dataSnapshot.getChildren()) {
            Request request = pendingRequest.getValue(Request.class);
            assert request != null;
            if (request.getStatus().equals(Request.Status.Pending.toString())) {
                requestArrayList.add(request);
            }
        }
        return requestArrayList;
    }

    /**
     * Sets a pending request to an accepted request.
     *
     * @param request Request object that will have its status changed from pending to accepted.
     * @param gp      Info of the GP that accepted the request.
     */
    void acceptRequest(Request request, GeneralPractitioner gp) {
        request.setGp(gp);
        request.setAcceptanceTime();
        request.setStatusByChoice(2);
        fireBase.child("RequestList").child(String.valueOf(request.getId())).setValue(request);
    }

    /**
     * Updates the GP information of a request.
     *
     * @param generalPractitioner GP info that the request will be updated to.
     * @param requestId           ID of the target request to be updated.
     */
    void updateUser(GeneralPractitioner generalPractitioner, int requestId) {
        fireBase.child("RequestList").child(String.valueOf(requestId)).child("gp").setValue(generalPractitioner);
    }

    /**
     * Sets the status of a request to 'arrived'.
     *
     * @param request Request object to be updated.
     */
    void setArrived(Request request) {
        request.setArrivalTime();
        request.setStatusByChoice(3);
        fireBase.child("RequestList").child(String.valueOf(request.getId())).setValue(request);
    }

    /**
     * Sets the status of a request to 'completed'.
     *
     * @param request Request object to be updated.
     */
    void completeRequest(Request request) {
        request.setCompletionTime();
        request.setStatusByChoice(4);
        fireBase.child("RequestList").child(String.valueOf(request.getId())).setValue(request);
    }
}

