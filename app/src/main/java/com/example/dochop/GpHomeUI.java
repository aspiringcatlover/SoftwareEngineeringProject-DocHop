package com.example.dochop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Represents the Home User Interface for General Practitioners (GPs).
 * Allows a GP to view, accept and handle a Patient's health care request.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class GpHomeUI extends AppCompatActivity implements OnMapReadyCallback, RequestListAdapter.ListItemClickListener {

    /**
     * GeneralPractitioner object to store user details.
     */
    private GeneralPractitioner user;

    /**
     * Request ID of the request that the GP is currently viewing or handling.
     */
    private int requestId;

    /**
     * Boolean value to identify if GP has currently accepted a request.
     */
    private boolean acceptedRequest = false;

    /**
     * Database reference for request objects.
     */
    private DatabaseReference fireBase = FirebaseDatabase.getInstance().getReference().child("Request").child("RequestList");

    /**
     * Database listener to listen for request updates within the database reference.
     */
    private ValueEventListener requestListListener;

    /**
     * ArrayList to store pending request objects.
     */
    private ArrayList<Request> requestArrayList = new ArrayList<>();

    /**
     * ArrayList to store retrieved carpark objects.
     */
    private ArrayList<Carpark> carparkArrayList = new ArrayList<>();

    /**
     * Fragment to display the Google Map.
     */
    private SupportMapFragment mapFragment;

    /**
     * location API in Google Play services that provides the location information.
     */
    private FusedLocationProviderClient fusedLocationClient;

    /**
     * Google Map used to display the GP's current location and the locations of all the pending
     * requests.
     */
    private GoogleMap googleMap;

    /**
     * Contains quality of service parameters for requests to the FusedLocationProviderClient.
     */
    private LocationRequest locationRequest;

    /**
     * Marker of the GP's current location, which will be displayed on the Google Map.
     */
    private Marker currentLocationMarker;

    /**
     * ArrayList to store the Markers of every request, which will be displayed on the Google
     * Map.
     */
    private ArrayList<Marker> requestMarkers = new ArrayList<>();

    /**
     * Unique request code to identify a location permission request.
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    /**
     * Various TextViews to be populated with the personalised Welcome Message for the GP, the
     * number of currently pending (available) requests and the individual request details.
     */
    private TextView userWelcomeMessage, numAvailableCases, requestDetails;

    /**
     * RecyclerView to be populated with a list of all the currently pending (available)
     * requests.
     */
    private RecyclerView requestsList;

    /**
     * Populates the RecyclerView with a list of all the currently pending (available) requests.
     */
    private RequestListAdapter adapter;

    /**
     * Various Buttons to facilitate the GP through the navigation of the application.
     */
    private Button acceptRequest, rejectRequest, arrived, completeRequest, viewHome, viewHistory, viewSettings;

    /**
     * InputStream used to parse all the carpark information from the carpark_info.csv file.
     */
    public static InputStream csvStream;

    /**
     * Toast passed within this UI.
     */
    private Toast toast;

    /**
     * Starts the UI for the GP to interact with.
     * User data is retrieved from the previous intent, location provider (GPS) is checked if it is
     * turned on, and various views, fragments and ArrayLists are instantiated with relevant data.
     *
     * @param savedInstanceState Contains the data most recently supplied in
     *                           onSaveInstanceState(Bundle). Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gp_home_ui);

        retrieveUserDetails();
        checkLocationAvailability();

        userWelcomeMessage = findViewById(R.id.user_welcome_message);
        userWelcomeMessage.setText(MessageFormat.format("Welcome {0}", user.getFirstName()));
        numAvailableCases = findViewById(R.id.num_available_cases);
        viewHome = findViewById(R.id.view_home);
        viewHistory = findViewById(R.id.view_history);
        viewSettings = findViewById(R.id.view_settings);

        csvStream = getResources().openRawResource(R.raw.carpark_info);

        requestDetails = findViewById(R.id.request_details);
        acceptRequest = findViewById(R.id.accept_request);
        rejectRequest = findViewById(R.id.reject_request);

        arrived = findViewById(R.id.arrived);

        completeRequest = findViewById(R.id.complete_request);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        carparkArrayList = retrieveCarparkInfo();
        retrieveRequests();
    }

    /**
     * Retrieves the user details from the previous intent.
     */
    public void retrieveUserDetails() {
        Intent intent = getIntent();
        user = (GeneralPractitioner) intent.getSerializableExtra("user");
    }

    /**
     * Retrieves an ArrayList of carparks with lots currently available from the CarparkAPI.
     *
     * @return ArrayList of carparks with lots currently available.
     */
    public ArrayList<Carpark> retrieveCarparkInfo() {
        ArrayList<Carpark> carparkArrayList = null;
        CarparkAPI process = new CarparkAPI();
        process.execute();
        try {
            carparkArrayList = process.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return carparkArrayList;
    }

    /**
     * Retrieves an ArrayList of currently pending requests and populates the requestsList
     * RecyclerView with data from the ArrayList.
     * Google Map fragment is also populated with individual Markers for all the requests in the
     * ArrayList.
     */
    public void retrieveRequests() {
        fireBase.addValueEventListener(requestListListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                RequestControl requestControl = new RequestControl();
                requestArrayList = requestControl.fetchPendingRequests(dataSnapshot);
                numAvailableCases.setText(MessageFormat.format("{0} available cases", requestArrayList.size()));

                requestsList = findViewById(R.id.requests_list);
                requestsList.setHasFixedSize(true);
                requestsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new RequestListAdapter(requestArrayList, GpHomeUI.this, getApplicationContext());
                requestsList.setAdapter(adapter);

                populateMap(requestArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    /**
     * Populates Google Map fragment with individual Markers for all the requests in the ArrayList
     * storing all the pending requests.
     *
     * @param requestArrayList ArrayList to store pending request objects.
     */
    public void populateMap(ArrayList<Request> requestArrayList) {
        if (requestMarkers.size() != 0) {
            googleMap.clear();
            requestMarkers.clear();
        }
        for (Request request : requestArrayList) {
            LatLng latLng = new LatLng(request.getPatient().getLatitude(), request.getPatient().getLongitude());
            Marker requestMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Request " + request.getId() + ": " + request.getPatient().getFirstName()));
            requestMarkers.add(requestMarker);
        }
    }

    /**
     * Displays additional information of the request which was selected in the RecyclerView.
     * Moves camera on the Google Map fragment to the current location of the request.
     *
     * @param clickedItemIndex Index of the TextView that was clicked in the RecyclerView.
     */
    @Override
    public void onListItemClick(int clickedItemIndex) {
        activateIndividualRequestView();

        Request request = requestArrayList.get(clickedItemIndex);
        Patient requestPatient = request.getPatient();

        showMoreRequestDetails(request, requestPatient);

        LatLng latLng = new LatLng(requestPatient.getLatitude(), requestPatient.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        requestId = request.getId();
    }

    /**
     * Changes visibility of various Views to allow additional information of selected request to be
     * displayed clearly.
     */
    public void activateIndividualRequestView() {
        requestsList.setVisibility(View.INVISIBLE);
        requestDetails.setVisibility(View.VISIBLE);
        acceptRequest.setVisibility(View.VISIBLE);
        rejectRequest.setVisibility(View.VISIBLE);
    }

    /**
     * Populates requestDetails TextView with additional information of selected request.
     *
     * @param request        Request details of the selected request.
     * @param requestPatient Patient details of the selected request.
     */
    public void showMoreRequestDetails(Request request, Patient requestPatient) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> requestAddress = new ArrayList<>();
        try {
            requestAddress = geocoder.getFromLocation(requestPatient.getLatitude(), requestPatient.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String patientFirstName = String.valueOf(requestPatient.getFirstName());
        String patientLastName = String.valueOf(requestPatient.getLastName());
        String patientFullName = patientFirstName + " " + patientLastName;

        String patientGender = String.valueOf(requestPatient.getGender());
        String patientAge = String.valueOf(requestPatient.getAge());
        String patientHeight = String.valueOf(requestPatient.getHeight());
        String patientWeight = String.valueOf(requestPatient.getWeight());
        String requestPatientDetails = patientGender + ", " + patientAge + ", " + patientHeight + "cm, " + patientWeight + "kg";
        requestDetails.setText(MessageFormat.format("{0}\n{1}\n{2}\nMedical History: {3}\nSymptoms: {4}", requestAddress.get(0).getAddressLine(0), patientFullName, requestPatientDetails, requestPatient.getMedicalHistory(), request.getSymptoms()));
    }

    /**
     * Allows a GP to accept a currently pending request.
     * Details of the accepted request will be updated with relevant information and stored in the
     * Firebase database.
     * Travel directions to the patient's nearest carpark and subsequently, the patient's location
     * will then be displayed on the Google Map fragment.
     *
     * @param view This refers to the Accept button located within the requestDetails TextView.
     */
    public void acceptRequest(View view) {
        fireBase.removeEventListener(requestListListener);
        fireBase.child(String.valueOf(requestId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Request request = dataSnapshot.getValue(Request.class);
                RequestControl requestControl = new RequestControl();
                assert request != null;
                requestControl.acceptRequest(request, user);
                acceptedRequest = true;
                nearestCarparkDisplay(request);
                activateAcceptedView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Allows a GP to reject a currently pending request and view all pending requests again.
     *
     * @param view This refers to the Reject button located within the requestDetails TextView.
     */
    public void rejectRequest(View view) {
        recreate();
    }

    /**
     * Calculates the nearest carpark to the patient and displays travel directions for the GP in
     * the Google Map fragment.
     *
     * @param request Request details of the accepted request.
     */
    public void nearestCarparkDisplay(Request request) {
        NearestCarparkCalculator nearestCarparkCalculator = new NearestCarparkCalculator(request);
        nearestCarparkCalculator.execute(carparkArrayList);

        Carpark nearestCarpark = null;
        try {
            nearestCarpark = nearestCarparkCalculator.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        assert nearestCarpark != null;
        System.out.println("2:" + nearestCarpark.getCarparkNumber());
        displayTravelDirections(nearestCarpark);
    }

    /**
     * Displays travel directions for the GP from their current location, to the nearest carpark
     * calculated for them, and finally to the patient.
     *
     * @param nearestCarpark Carpark details of the nearest carpark to the patient.
     */
    public void displayTravelDirections(Carpark nearestCarpark) {
        googleMap.clear();
        requestMarkers.clear();
        fireBase.child(String.valueOf(requestId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                double gpLatitude = user.getLatitude();
                double gpLongitude = user.getLongitude();
                double patientLatitude = (double) dataSnapshot.child("patient").child("latitude").getValue();
                double patientLongitude = (double) dataSnapshot.child("patient").child("longitude").getValue();
                double carparkLatitude = Double.parseDouble(nearestCarpark.getLatitude());
                double carparkLongitude = Double.parseDouble(nearestCarpark.getLongitude());
                String url = getUrl(gpLatitude, gpLongitude, patientLatitude, patientLongitude, carparkLatitude, carparkLongitude);

                LatLng gpLatLng = new LatLng(gpLatitude, gpLongitude);
                LatLng patientLatLng = new LatLng(patientLatitude, patientLongitude);
                LatLng carparkLatLng = new LatLng(carparkLatitude, carparkLongitude);

                currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(gpLatLng).title("Current Location"));
                googleMap.addMarker(new MarkerOptions().position(patientLatLng).title("Patient: " + dataSnapshot.child("patient").child("firstName").getValue()));
                googleMap.addMarker(new MarkerOptions().position(carparkLatLng).title("Nearest carpark"));

                DirectionsAPI directionsAPI = new DirectionsAPI(googleMap);
                directionsAPI.execute(url);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Constructs a Google Directions API request in the form of a URL.
     *
     * @param originLatitude       Latitude of the GP's current location.
     * @param originLongitude      Longitude of the GP's current location.
     * @param destinationLatitude  Latitude of the nearest carpark's location.
     * @param destinationLongitude Longitude of the nearest carpark's location.
     * @param carparkLatitude      Latitude of the patient's location.
     * @param carparkLongitude     Longitude of the patient's location.
     * @return String implementation of the URL for the Google Directions API request.
     */
    public String getUrl(double originLatitude, double originLongitude, double destinationLatitude, double destinationLongitude, double carparkLatitude, double carparkLongitude) {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=" + originLatitude + "," + originLongitude +
                "&destination=" + destinationLatitude + "," + destinationLongitude + "&waypoints=" + carparkLatitude + "," + carparkLongitude +
                "&key=AIzaSyDJKlCwMT387I4t8K8Nb6omZ6WiwtSqe-s";
    }

    /**
     * Changes visibility of various Views to allow GP to update their arrival.
     */
    public void activateAcceptedView() {
        userWelcomeMessage.setVisibility(View.GONE);
        numAvailableCases.setVisibility(View.GONE);
        viewHome.setVisibility(View.INVISIBLE);
        viewHistory.setVisibility(View.INVISIBLE);
        viewSettings.setVisibility(View.INVISIBLE);
        acceptRequest.setVisibility(View.GONE);
        rejectRequest.setVisibility(View.GONE);
        arrived.setVisibility(View.VISIBLE);
    }

    /**
     * Updates request details with GP's arrival.
     *
     * @param view This refers to the Arrived button located within the requestDetails TextView.
     */
    public void arrived(View view) {
        fireBase.child(String.valueOf(requestId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Request request = dataSnapshot.getValue(Request.class);
                RequestControl requestControl = new RequestControl();
                assert request != null;
                requestControl.setArrived(request);
                activateArrivedView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Changes visibility of various Views to allow GP to complete the request once the request has
     * been handled.
     */
    public void activateArrivedView() {
        requestDetails.setVisibility(View.GONE);
        arrived.setVisibility(View.GONE);
        Objects.requireNonNull(mapFragment.getView()).setVisibility(View.INVISIBLE);
        completeRequest.setVisibility(View.VISIBLE);
    }

    /**
     * Updates request to be completed once the GP has handled the request.
     * Returns GP to the initial home screen to view and select another request to handle.
     *
     * @param view This refers to the Complete Request button made visible once the GP has arrived.
     */
    public void completeRequest(View view) {
        Context context = this;
        fireBase.child(String.valueOf(requestId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Request request = dataSnapshot.getValue(Request.class);
                RequestControl requestControl = new RequestControl();
                assert request != null;
                requestControl.completeRequest(request);
                if (toast != null) {
                    toast.cancel();
                }
                toast = Toast.makeText(context, "Request completed.", Toast.LENGTH_LONG);
                toast.show();
                recreate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Starts the activity for GPHomeUI using its intent when the home button is selected.
     *
     * @param view This refers to Home button located at the bottom of the interface.
     */
    public void viewHome(View view) {
    }

    /**
     * Starts the activity for GPHistoryUI when the history button is selected.
     *
     * @param view This refers to History button located at the bottom of the interface.
     */
    public void viewHistory(View view) {
        Intent intent = new Intent(this, GpHistoryUI.class);
        parseUserDetails(intent);
        startActivity(intent);
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

    /**
     * Checks if the GP's location service (GPS) has been turned on.
     * An error is thrown and the GP is not allowed to continue until their location service has
     * been turned on.
     */
    public void checkLocationAvailability() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = LayoutInflater.from(this);
            @SuppressLint("InflateParams") final View locationDialogView = inflater.inflate(R.layout.dialog_location, null);
            AlertDialog locationDialog = builder.setView(locationDialogView)
                    .setTitle(R.string.location_not_found)
                    .setPositiveButton(R.string.retry, (dialog, which) -> checkLocationAvailability())
                    .create();
            locationDialog.show();
        }
    }

    /**
     * Activates requests for location updates on the GP's device.
     * If location permission has not been granted from the device, a request for permission is sent
     * to the GP for approval first.
     *
     * @param googleMap Google Map used to display the GP's current location and the locations of
     *                  all the pending requests.
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
     * Activates requests for location updates on the GP's device once location permission has been
     * granted.
     * If permission has not been granted, a toast is shown and the GP is not allowed to continue
     * until the location permission has been granted.
     *
     * @param requestCode  Represents the request code passed when a permission is requested.
     * @param permissions  Represents the requested permission.
     * @param grantResults Represents the results of the permission request.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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

        /**
         * Updates GP's current location on Google Map each time the device location changes.
         *
         * @param locationResult Represents a geographic location result from the
         *                       FusedLocationProviderClient.
         */
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            Location location = locationResult.getLastLocation();

            GeneralPractitionerControl generalPractitionerControl = new GeneralPractitionerControl();
            user = generalPractitionerControl.updateLocation(user, location.getLatitude(), location.getLongitude());

            if (acceptedRequest) {
                RequestControl requestControl = new RequestControl();
                requestControl.updateUser(user, requestId);
            }

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