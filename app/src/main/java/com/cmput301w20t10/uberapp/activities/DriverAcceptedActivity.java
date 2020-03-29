package com.cmput301w20t10.uberapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.Directions.FetchURL;
import com.cmput301w20t10.uberapp.Directions.TaskLoadedCallback;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.Locale;

public class DriverAcceptedActivity extends BaseActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final String LAST_LOCATION_KEY = "location";
    private static final String CAMERA_DIRECTION_KEY = "camera_direction";
    private static final int REQUEST_CODE = 101;

    SharedPref sharedPref;

    private FirebaseFirestore db;

    private GoogleMap mainMap;
    private Location currentLocation;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient client;

    Polyline currentPolyline;

    private MarkerOptions startPin;
    private MarkerOptions endPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = LocationServices.getFusedLocationProviderClient(this);

        requestLocationPermission();

        // Retrieve location and camera direction from savedInstanceState
        if (savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
            CameraPosition cameraPosition = savedInstanceState.getParcelable(CAMERA_DIRECTION_KEY);
        }

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        setContentView(R.layout.driver_accepted);

        db = FirebaseFirestore.getInstance();

        // map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TextView username = findViewById(R.id.request_username);
        TextView distance = findViewById(R.id.request_distance);
        TextView offer = findViewById(R.id.request_offer);
        TextView firstName = findViewById(R.id.request_first_name);
        TextView lastName = findViewById(R.id.request_last_name);
        TextView startDest = findViewById(R.id.request_start_dest);
        TextView endDest = findViewById(R.id.request_end_dest);
        TextView startEndDistance = findViewById(R.id.start_end_distance);
        ImageView profilePicture = findViewById(R.id.profile_picture);
        Button cancelButton = findViewById(R.id.cancel_request_button);
        cancelButton.setVisibility(View.VISIBLE);

        String activeRideRequest = getIntent().getStringExtra("ACTIVE");

        db.document(activeRideRequest).get().addOnSuccessListener(rideRequestSnapshot -> {
            GeoPoint startGeoPoint = (GeoPoint) rideRequestSnapshot.get("startingPosition");
            LatLng startLatLng = new LatLng(startGeoPoint.getLatitude(), startGeoPoint.getLongitude());
            startDest.setText(getAddress(startLatLng));
            GeoPoint endGeoPoint = (GeoPoint) rideRequestSnapshot.get("destination");
            LatLng endLatLng = new LatLng(endGeoPoint.getLatitude(), endGeoPoint.getLongitude());
            endDest.setText(getAddress(endLatLng));

            dropPins("Start Destination", startLatLng, "End Destination",  endLatLng);
            new FetchURL(this).execute(createUrl(startPin.getPosition(), endPin.getPosition()), "driving");

            float[] currentStartDistance = new float[1];
            Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                    startLatLng.latitude, startLatLng.longitude, currentStartDistance);
            distance.setText(String.format("%.2fkm", currentStartDistance[0]/1000));

            float[] startEndDist = new float[1];
            Location.distanceBetween(startLatLng.latitude,startLatLng.longitude,
                    endLatLng.latitude,endLatLng.longitude, startEndDist);
            startEndDistance.setText(String.format("%.2fkm", startEndDist[0]/1000));

            float fareOffer = (long) rideRequestSnapshot.get("fareOffer");
            offer.setText("Offer: $" + String.format("%.2f", fareOffer));

            DocumentReference riderReference =  (DocumentReference) rideRequestSnapshot.get("riderReference");
            System.out.println("PATH: " + riderReference.getPath());
            riderReference.get().addOnSuccessListener(riderSnapshot -> {
                DocumentReference userReference =  (DocumentReference) riderSnapshot.get("userReference");
                userReference.get().addOnSuccessListener(userSnapshot -> {
                    username.setText(userSnapshot.get("username").toString());
                    firstName.setText(userSnapshot.get("firstName").toString());
                    lastName.setText(userSnapshot.get("lastName").toString());
                    Glide.with(this)
                            .load(userSnapshot.get("image"))
                            .into(profilePicture);
                });
            });

        });
    }

    public String getAddress(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String addressLine = "";
        try {
            double latitude = latLng.latitude;
            double longitude = latLng.longitude;
            Address address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
            addressLine = address.getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            Log.e("Index Error: ", "IndexOutOfBoundsException");
        }

        return addressLine;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            getCurrentLocation();
        } else {
            Toast.makeText(getApplicationContext(), "Location permission required", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            finish();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mainMap = googleMap;
        getCurrentLocation();
        if (locationPermissionGranted) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private String createUrl(LatLng startDest, LatLng endDest) {
        // Start of route
        String origin = "origin=" + currentLocation.getLatitude() + "," + currentLocation.getLongitude();
        // End of route
        String dest = "destination=" + endDest.latitude + "," + endDest.longitude;
        // Mode
        String mode = "mode=driving";
        // Waypoint
        String waypoint = "waypoints=optimize:true|" + startDest.latitude + "," + startDest.longitude + "|";
        // parameters
        String parameters = origin + "&" + waypoint + "&" + dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url
        return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
    }

    private void getCurrentLocation() {
        /*
         * Used code from YouTube video to ask location permission and get current location
         * YouTube video posted by "Android Coding"
         * Title: How to Show Current Location On Map in Android Studio | CurrentLocation | Android Coding
         * URL: https://www.youtube.com/watch?v=boyyLhXAZAQ
         */
        // get last know location of device
        client.getLastLocation().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                currentLocation = task.getResult();
                LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(currentLatLng)      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .build();                   // Creates a CameraPosition from the builder
                mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    private void moveCamera(Marker startMarker, Marker endMarker){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(startMarker.getPosition());
        builder.include(endMarker.getPosition());

        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,
                padding);
        mainMap.moveCamera(cu);
        mainMap.animateCamera(cu);

    }

    private void dropPins(String startTitle, LatLng startDest, String endTitle, LatLng endDest) {
        startPin = new MarkerOptions()
                .position(startDest)
                .title(startTitle)
                .icon(BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                                BitmapFactory.decodeResource(getResources(), R.mipmap.startpos), 30, 30, false)));
        endPin = new MarkerOptions()
                .position(endDest)
                .title(endTitle)
                .icon(BitmapDescriptorFactory.fromBitmap(
                        Bitmap.createScaledBitmap(
                                BitmapFactory.decodeResource(getResources(), R.mipmap.destination), 30, 30, false)));

        mainMap.clear();

        Marker startMarker = mainMap.addMarker(startPin);
        Marker endMarker = mainMap.addMarker(endPin);

        moveCamera(startMarker, endMarker);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline !=null){
            currentPolyline.remove();
        }
        currentPolyline = mainMap.addPolyline((PolylineOptions) values[0]);
    }

}