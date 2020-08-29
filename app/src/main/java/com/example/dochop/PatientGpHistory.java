package com.example.dochop;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Represents the GP Request History User Interface fragment for patients.
 * Retrieves data from firebase to populate the history of GP requests of a patient.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class PatientGpHistory extends Fragment {

    /**
     * Creation of a recycler view object for the scrolling capability of the interface.
     */
    private RecyclerView recyclerView;

    /**
     * Creation of the GeneralPractitioner object to obtain user details.
     */
    private Patient user;

    /**
     * Required empty public constructor for PatientGPHistory.
     * All subclasses of Fragment must include a public no-argument constructor.
     * The framework will often re-instantiate a fragment class when needed, in particular during
     * state restore, and needs to be able to find this constructor to instantiate it.
     * If the no-argument constructor is not available, a runtime exception will occur in some cases
     * during state restore.
     */
    public PatientGpHistory() {

    }

    /**
     * Links the fragment_user_gp_history xml file to this class (PatientGPHistory).
     * Creation of array of Requests requests to store all Request objects from firebase.
     * Creation of recyclerView to link the recycler view item in the
     * fragment_user_ambulance_history xml
     * file to the PatientGPHistory java file.
     * Creation of firebase variable using the database reference for Request objects.
     *
     * @param inflater           Instantiates fragment_user_gp_history XML file into the
     *                           PatientGPHistory fragment view.
     * @param container          The parent view that this PatientGPHistory fragment will be
     *                           attached to.
     * @param savedInstanceState This represents the current saved instance state.
     * @return PatientGPHistory view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert getArguments() != null;
        user = (Patient) getArguments().getSerializable("user");

        final ArrayList<Request> requests = new ArrayList<>();
        final Context thisContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_user_gp_history, container, false);
        recyclerView = view.findViewById(R.id.userdoctorhistoryrecycler);

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("Request").child("RequestList");
        firebase.addValueEventListener(new ValueEventListener() {

            /**
             * Retrieves data from firebase reference and populates the interface view.
             * Matches the details of each patient of each GP request with the credentials of
             * currently logged in user.
             * The final array of relevant Request objects is passed into
             * patientAmbulanceHistoryAdapter to display data on the interface.
             * @param dataSnapshot This represents the current data within firebase reference.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot completedRequest : dataSnapshot.getChildren()) {
                    Request request = completedRequest.getValue(Request.class);
                    assert request != null;
                    if (user.getUsername().equals(request.getPatient().getUsername()) && request.getStatus().equals("Completed") && request.getIsGP()) {
                        requests.add(request);
                    }
                }
                PatientGpHistoryAdapter patientGPHistoryAdapter = new PatientGpHistoryAdapter(thisContext, requests);
                recyclerView.setAdapter(patientGPHistoryAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(thisContext));
            }

            /**
             * This method is called when there is a database error when accessing firebase.
             * @param databaseError This represents a database error when accessing firebase.
             */
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        return view;
    }
}
