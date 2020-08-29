package com.example.dochop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Represents the History User Interface for GPs.
 * Retrieves data from firebase to populate the history of patients for a GP.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class GpHistoryUI extends AppCompatActivity {

    /**
     * RecyclerView object for the scrolling capability of the interface.
     */
    RecyclerView recyclerView;

    /**
     * The reference to the firebase database.
     */
    DatabaseReference firebase;

    /**
     * GeneralPractitioner object to store user details.
     */
    GeneralPractitioner user;

    /**
     * Links the activity_gp_history_ui xml file to the GpHistoryUI java file.
     * Creation of array of Requests sampleRequest to store all Request objects from firebase.
     * Creation of recyclerView to link the recycler view item in the activity_gp_history_ui xml
     * file to the GpHistoryUI java file.
     * Creation of firebase variable using the database reference for Request objects.
     *
     * @param savedInstanceState This represents the current saved Instance State.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_history_ui);
        final ArrayList<Request> sampleRequest = new ArrayList<>();
        recyclerView = findViewById(R.id.gphistoryrecyclerView);
        retrieveUserDetails();

        firebase = FirebaseDatabase.getInstance().getReference().child("Request").child("RequestList");
        firebase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot CompleteRequest : dataSnapshot.getChildren()) {
                    Request request = CompleteRequest.getValue(Request.class);
                    assert request != null;
                    if (user.getUsername().equals(request.getGp().getUsername()) && request.getStatus().equals("Completed")) {
                        sampleRequest.add(request);
                    }
                }
                GpHistoryAdapter myAdapter = new GpHistoryAdapter(getApplicationContext(), sampleRequest);
                recyclerView.setAdapter(myAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Retrieves the user details from the previous intent.
     */
    public void retrieveUserDetails() {
        Intent intent = getIntent();
        user = (GeneralPractitioner) intent.getSerializableExtra("user");
    }

    /**
     * Starts the activity for GPHomeUI using its intent when the home button is selected.
     *
     * @param view This refers to Home button located at the bottom of the interface.
     */
    public void viewHome(View view) {
        Intent intent = new Intent(this, GpHomeUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Starts the activity for GPHistoryUI when the history button is selected.
     *
     * @param view This refers to History button located at the bottom of the interface.
     */
    public void viewHistory(View view) {
    }

    /**
     * Starts the activity for GPSettingsUI using its intent when the settings button is selected.
     *
     * @param view This refers to Settings button located at the bottom of the interface.
     */
    public void viewSettings(View view) {
        Intent intent = new Intent(this, GpSettingsUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Parses the user details to the next intent.
     *
     * @param intent The represents the next intent.
     */
    public void parseUserDetails(Intent intent) {
        intent.putExtra("user", user);
    }
}
