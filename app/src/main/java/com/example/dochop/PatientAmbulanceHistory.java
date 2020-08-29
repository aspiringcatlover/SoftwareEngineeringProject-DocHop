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
 * Represents the Ambulance History User Interface fragment for patients.
 * Retrieves data from firebase to populate the history of ambulance calls of a patient.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class PatientAmbulanceHistory extends Fragment {

    /**
     * Creation of a recycler view object for the scrolling capability of the interface.
     */
    private RecyclerView recyclerView;

    /**
     * Creation of the Patient object to obtain user details.
     */
    private Patient user;

    /**
     * Required empty public constructor for PatientAmbulanceHistory.
     * All subclasses of Fragment must include a public no-argument constructor.
     * The framework will often re-instantiate a fragment class when needed, in particular during
     * state restore, and needs to be able to find this constructor to instantiate it.
     */
    public PatientAmbulanceHistory() {
        // Required empty public constructor
    }

    /**
     * Links the fragment_user_ambulance_history xml file to this class (PatientAmbulanceHistory).
     * Creation of array of Requests requests to store all Request objects from firebase.
     * Creation of recyclerView to link the recycler view item in the
     * fragment_user_ambulance_history xml file to the PatientAmbulanceHistory java file.
     * Creation of firebase variable using the database reference for Request objects.
     *
     * @param inflater           Instantiates fragment_user_ambulance_history XML file into the
     *                           PatientAmbulanceHistory fragment view.
     * @param container          The parent view that this PatientAmbulanceHistory fragment will be
     *                           attached to.
     * @param savedInstanceState This represents the current saved Instance State.
     * @return PatientAmbulanceHistory view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        assert getArguments() != null;
        user = (Patient) getArguments().getSerializable("user");

        final ArrayList<Request> ambulanceRequests = new ArrayList<>();
        final Context thisContext = container.getContext();
        View view = inflater.inflate(R.layout.fragment_user_ambulance_history, container, false);
        recyclerView = view.findViewById(R.id.userambulancehistoryrecycler);

        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child("Request").child("RequestList");
        firebase.addValueEventListener(new ValueEventListener() {

            /**
             * Retrieves data from firebase reference and populates the interface view.
             * Matches the details of each patient of each ambulance request with the credentials of
             * currently logged in user.
             * The final array of relevant Request objects is passed into
             * patientAmbulanceHistoryAdapter to display data on the interface.
             * @param dataSnapshot This represents the current data within firebase reference.
             */
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ambulanceRequest : dataSnapshot.getChildren()) {
                    Request request = ambulanceRequest.getValue(Request.class);
                    assert request != null;
                    if ((user.getUsername().equals(request.getPatient().getUsername())) && !request.getIsGP()) {
                        ambulanceRequests.add(request);
                    }
                }
                PatientAmbulanceHistoryAdapter patientAmbulanceHistoryAdapter = new PatientAmbulanceHistoryAdapter(thisContext, ambulanceRequests);
                recyclerView.setAdapter(patientAmbulanceHistoryAdapter);
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
