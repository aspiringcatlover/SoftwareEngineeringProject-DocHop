package com.example.dochop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.MessageFormat;
import java.util.Objects;

/**
 * Represents the Home User Interface for Patients.
 * Allows a Patient to create a request and view its progress.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class PatientHomeUI extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * Patient object to store user details.
     */
    private Patient user;

    /**
     * Request ID of the request that the Patient has created.
     */
    private int requestId;

    /**
     * Database reference for request objects.
     */
    private DatabaseReference fireBase = FirebaseDatabase.getInstance().getReference().child("Request");

    /**
     * Fragment to display the Google Map.
     */
    private SupportMapFragment mapFragment;

    /**
     * location API in Google Play services that provides the location information.
     */
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Google Map used to display both the Patient's and the handling General Practitioner's (GP)
     * current location.
     */
    private GoogleMap googleMap;

    /**
     * Contains quality of service parameters for requests to the FusedLocationProviderClient.
     */
    private LocationRequest locationRequest;

    /**
     * Marker of the Patient's and the handling GP's current location, which will be displayed on
     * the Google Map.
     */
    private Marker currentLocationMarker, gpMarker;

    /**
     * Unique request code to identify a location permission request.
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Various TextViews to be populated with the personalised Welcome Message for the Patient, and
     * other update messages for the Patient's request tracking.
     */
    private TextView userWelcomeMessage, searchMessage, acceptanceMessage, waitForCompletionMessage;

    /**
     * Various Buttons to facilitate the GP through the navigation of the application.
     */
    private Button requestGP, cancelSearch, viewHome, viewHistory, viewSettings;

    /**
     * Toast passed within this UI.
     */
    private Toast toast;

    /**
     * Countdown timer to query if Patient wishes to dial an ambulance instead.
     */
    private CountDownTimer timer;
    //TODO update timer for ambulance to 8 minutes

    /**
     * Countdown timer's duration (in minutes).
     */
    private final int TIMER_FOR_AMBULANCE = 1;

    /**
     * Pop-up alert dialog to query if Patient wishes to dial an ambulance instead, once timer is up.
     */
    private AlertDialog ambulanceDialog;

    /**
     * Starts the UI for the Patient to interact with.
     * User data is retrieved from the previous intent, and various views and fragments are
     * instantiated with relevant data.
     *
     * @param savedInstanceState Contains the data most recently supplied in
     *                           onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_home_ui);

        retrieveUserDetails();

        userWelcomeMessage = findViewById(R.id.user_welcome_message);
        requestGP = findViewById(R.id.request_gp);
        viewHome = findViewById(R.id.view_home);
        viewHistory = findViewById(R.id.view_history);
        viewSettings = findViewById(R.id.view_settings);

        searchMessage = findViewById(R.id.search_message);
        cancelSearch = findViewById(R.id.cancel_search);

        acceptanceMessage = findViewById(R.id.acceptance_message);

        waitForCompletionMessage = findViewById(R.id.wait_for_completion_message);

        userWelcomeMessage.setText(MessageFormat.format("Welcome {0}", user.getFirstName()));

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        Objects.requireNonNull(mapFragment.getView()).setVisibility(View.INVISIBLE);
    }

    /**
     * Retrieves the user details from the previous intent.
     */
    public void retrieveUserDetails() {
        Intent intent = getIntent();
        user = (Patient) intent.getSerializableExtra("user");
    }

    /**
     * Allows Patient to begin creating a request for GPs.
     * Location provider (GPS) is checked if it is turned on.
     * User is only allowed to proceed to enter their symptoms if GPS is turned on.
     *
     * @param view This refers to the Request Doctor button.
     */
    public void requestGP(View view) {
        boolean haveLocation = checkLocationAvailability();
        if (!haveLocation)
            return;
        getSymptoms();
    }

    /**
     * Allows Patient to input their symptoms for storage within the request and for the GP to view.
     */
    public void getSymptoms() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View symptomsDialogView = inflater.inflate(R.layout.dialog_symptoms, null);
        AlertDialog symptomsDialog = builder.setView(symptomsDialogView).setTitle(R.string.dialog_symptoms).setPositiveButton(R.string.request, (dialog, which) -> {
            EditText symptomsInput = symptomsDialogView.findViewById(R.id.symptoms_input);
            String symptoms = symptomsInput.getText().toString();
            createRequest(symptoms);
        }).setNegativeButton(R.string.cancel, (dialog, which) -> cancelRequest()).create();
        symptomsDialog.show();
    }

    /**
     * Creates a request and stores it within the Firebase database.
     * A timer is activated to query if the Patient wishes to dial an ambulance instead if the
     * request takes too long to accept.
     * The Patient then waits for the GP to accept their request.
     *
     * @param symptoms This refers to a Patient's inputted symptoms.
     */
    public void createRequest(String symptoms) {
        RequestControl requestControl = new RequestControl();
        fireBase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int nextRequestId = Integer.parseInt(Objects.requireNonNull(dataSnapshot.child("nextRequestId").getValue()).toString());
                requestControl.makeRequest(nextRequestId, user, symptoms);
                requestId = nextRequestId;
                activateMapView();
                startTimer();
                listenForGpAccept();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Changes visibility of various Views to allow Patient to view Google Maps and track their
     * request.
     */
    public void activateMapView() {
        userWelcomeMessage.setVisibility(View.GONE);
        requestGP.setVisibility(View.GONE);
        viewHome.setVisibility(View.INVISIBLE);
        viewHistory.setVisibility(View.INVISIBLE);
        viewSettings.setVisibility(View.INVISIBLE);
        Objects.requireNonNull(mapFragment.getView()).setVisibility(View.VISIBLE);
        searchMessage.setVisibility(View.VISIBLE);
        cancelSearch.setVisibility(View.VISIBLE);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mapFragment.getMapAsync(this);
    }

    /**
     * Listens for a GP accepting their request.
     * Once an accept has been received, the request status is updated in the Firebase database and
     * the handling GP's location is displayed on the Google Map.
     */
    public void listenForGpAccept() {
        fireBase.child("RequestList").child(String.valueOf(requestId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.child("status").getValue(), "Accepted")) {
                    timer.cancel();
                    fireBase.child("RequestList").child(String.valueOf(requestId)).removeEventListener(this);
                    activateGpAcceptedView();
                    listenForGpLocation();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Changes visibility of various Views to allow Patient to view the current location of their
     * handling GP.
     */
    public void activateGpAcceptedView() {
        searchMessage.setVisibility(View.GONE);
        cancelSearch.setVisibility(View.GONE);
        acceptanceMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Listens for the handling GP arriving at the Patient's location.
     * If the GP has not arrived, the Google Map will constantly update with the GP's current
     * location.
     * Once the GP has arrived, the request status is updated in the Firebase database and an alert
     * dialog is generated to alert the Patient.
     */
    public void listenForGpLocation() {
        fireBase.child("RequestList").child(String.valueOf(requestId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.child("status").getValue(), "Arrived")) {
                    fireBase.child("RequestList").child(String.valueOf(requestId)).removeEventListener(this);
                    activateGpArrivedDialog();
                } else {
                    LatLng gpLatLng = new LatLng(((double) dataSnapshot.child("gp").child("latitude").getValue()), (double) dataSnapshot.child("gp").child("longitude").getValue());
                    if (gpMarker == null) {
                        gpMarker = googleMap.addMarker(new MarkerOptions().position(gpLatLng).title("General Practitioner's Location"));
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gpLatLng, 15));
                    } else {
                        gpMarker.setPosition(gpLatLng);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Generates the alert dialog to alert the Patient of the GP's arrival.
     */
    public void activateGpArrivedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View gpArrivedDialogView = inflater.inflate(R.layout.dialog_gp_arrived, null);
        AlertDialog gpArrivedDialog = builder.setView(gpArrivedDialogView).setTitle(R.string.dialog_gp_arrived).setPositiveButton(R.string.got_it, (dialog, which) -> listenForGpCompletion(this)).create();
        gpArrivedDialog.show();
    }

    /**
     * Changes visibility of various Views to update Patient to wait for GP to complete the request.
     */
    public void activateGpArrivedView() {
        assert mapFragment != null;
        Objects.requireNonNull(mapFragment.getView()).setVisibility(View.GONE);
        acceptanceMessage.setVisibility(View.GONE);
        waitForCompletionMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Listens for the handling GP completing the request at their end.
     * Once the request has been completed, the request status is updated in the Firebase database.
     * Returns Patient to the initial home screen to create another request.
     *
     * @param context This refers to the current application's context.
     */
    public void listenForGpCompletion(Context context) {
        activateGpArrivedView();
        fireBase.child("RequestList").child(String.valueOf(requestId)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (Objects.equals(dataSnapshot.child("status").getValue(), "Completed")) {
                    fireBase.child("RequestList").child(String.valueOf(requestId)).removeEventListener(this);
                    if (toast != null) {
                        toast.cancel();
                    }
                    toast = Toast.makeText(context, "Request completed.", Toast.LENGTH_LONG);
                    toast.show();
                    recreate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Starts the countdown timer to query if Patient wishes to dial an ambulance instead.
     * Once the timer is up, the query will pop-up for the Patient to give their input and the timer
     * is reset.
     */
    public void startTimer() {
        timer = new CountDownTimer(TIMER_FOR_AMBULANCE * 60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                requestAmbulance();
                this.start();
            }
        };
        timer.start();
    }

    /**
     * Displays an alert dialog to query if Patient wishes to dial an ambulance instead.
     * If the Patient selects Yes, the Patient will continue to dial an ambulance and the request
     * will be updated accordingly in the Firebase database.
     * If the Patient selects No, the Patient will continue searching for a GP.
     */
    public void requestAmbulance() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View ambulanceDialogView = inflater.inflate(R.layout.dialog_ambulance, null);
        if (ambulanceDialog != null) {
            ambulanceDialog.cancel();
        }
        ambulanceDialog = builder.setView(ambulanceDialogView).setTitle("8 minutes has passed. Do you want to call the ambulance?").setPositiveButton(R.string.yes, (dialog, which) -> {
            if (fusedLocationClient != null)
                fusedLocationClient.removeLocationUpdates(locationCallback);
            callAmbulance();
            RequestControl requestControl = new RequestControl();
            fireBase.child("RequestList").child(String.valueOf(requestId)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Request request = dataSnapshot.getValue(Request.class);
                    assert request != null;
                    requestControl.updateRequestToAmbulance(request);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            recreate();
        }).setNegativeButton(R.string.no, (dialog, which) -> {
        }).create();
        ambulanceDialog.show();
    }

    /**
     * Starts the activity for to dial an ambulance.
     */
    public void callAmbulance() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:1777"));
        startActivity(intent);
    }

    /**
     * Cancels the Patient's request.
     *
     * @param view This refers to Cancel button.
     */
    public void cancelRequest(View view) {
        cancelRequest();
    }

    /**
     * Cancels the Patient's request.
     * The request will be removed from the Firebase database.
     * Returns Patient to the initial home screen to create another request.
     */
    public void cancelRequest() {
        if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
        if (requestId != 0) {
            RequestControl requestControl = new RequestControl();
            fireBase.child("RequestList").child(String.valueOf(requestId)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Request request = dataSnapshot.getValue(Request.class);
                    assert request != null;
                    requestControl.cancelRequest(request);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, "Request cancelled.", Toast.LENGTH_SHORT);
        toast.show();
        recreate();
    }

    /**
     * Starts the activity for PatientHomeUI using its intent when the home button is selected.
     *
     * @param view This refers to Home button located at the bottom of the interface.
     */
    public void viewHome(View view) {
    }

    /**
     * Starts the activity for PatientHistoryUI when the history button is selected.
     *
     * @param view This refers to History button located at the bottom of the interface.
     */
    public void viewHistory(View view) {
        Intent intent = new Intent(this, PatientHistoryUI.class);
        parseUserDetails(intent);
        startActivity(intent);
    }

    /**
     * Starts the activity for PatientSettingsUI using its intent when the settings button is selected.
     *
     * @param view This refers to Settings button located at the bottom of the interface.
     */
    public void viewSettings(View view) {
        Intent intent = new Intent(this, PatientSettingsUI.class);
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

    /**
     * Checks if the Patient's location service (GPS) has been turned on.
     * An error is thrown if the GPS is not turned on.
     */
    public boolean checkLocationAvailability() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (toast != null) {
                toast.cancel();
            }
            toast = Toast.makeText(this, "Location service not active. Please turn on your GPS and try again.", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    /**
     * Activates requests for location updates on the Patient's device.
     * If location permission has not been granted from the device, a request for permission is sent
     * to the Patient for approval first.
     *
     * @param googleMap Google Map used to display the Patient's and the handling GP's current
     *                  location.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
            this.googleMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Activates requests for location updates on the Patient's device once location permission has been
     * granted.
     * If permission has not been granted, a toast is shown and the Patient is not allowed to continue
     * until the location permission has been granted.
     *
     * @param requestCode  Represents the request code passed when a permission is requested.
     * @param permissions  Represents the requested permission.
     * @param grantResults Represents the results of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    googleMap.setMyLocationEnabled(true);
                }
            } else {
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(this, "Location permission not granted. Please allow location permission and try again.", Toast.LENGTH_SHORT);
                toast.show();
                recreate();
            }
        }
    }

    /**
     * Receives notifications from FusedLocationProviderClient when device location has changed.
     */
    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            Location location = locationResult.getLastLocation();

            PatientControl patientControl = new PatientControl();
            user = patientControl.updateLocation(user, location.getLatitude(), location.getLongitude());

            RequestControl requestControl = new RequestControl();
            requestControl.updateUser(user, requestId);

            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            if (currentLocationMarker == null) {
                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            } else {
                currentLocationMarker.setPosition(latLng);
            }
        }
    };
}
