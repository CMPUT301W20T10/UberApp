package com.cmput301w20t10.uberapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.Directions.FetchURL;
import com.cmput301w20t10.uberapp.Directions.TaskLoadedCallback;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.fragments.ViewProfileFragment;
import com.cmput301w20t10.uberapp.messaging.FCMSender;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.RideRequestListContent;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.Locale;

public class DriverAcceptedActivity extends BaseActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final int REQUEST_CODE = 101;

    SharedPref sharedPref;

    String riderUsername;

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

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        sharedPref.setHomeActivity(this.getLocalClassName());

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
        ImageView riderPictureButton = findViewById(R.id.profile_button);
        Button cancelButton = findViewById(R.id.cancel_request_button);
        cancelButton.setVisibility(View.VISIBLE);
        TextView tapProfileHint = findViewById(R.id.tap_profile_hint);
        tapProfileHint.setVisibility(View.VISIBLE);

        if (hasNetwork()) {
            String activeRideRequest = Application.getInstance().getActiveRidePath();

            DocumentReference rideRequestReference = db.document(activeRideRequest);

            rideRequestReference.get().addOnSuccessListener(rideRequestSnapshot -> {
                GeoPoint startGeoPoint = (GeoPoint) rideRequestSnapshot.get("startingPosition");
                LatLng startLatLng = new LatLng(startGeoPoint.getLatitude(), startGeoPoint.getLongitude());
                startDest.setText(getAddress(startLatLng));
                GeoPoint endGeoPoint = (GeoPoint) rideRequestSnapshot.get("destination");
                LatLng endLatLng = new LatLng(endGeoPoint.getLatitude(), endGeoPoint.getLongitude());
                endDest.setText(getAddress(endLatLng));

                float[] currentStartDistance = new float[1];
                Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(),
                        startLatLng.latitude, startLatLng.longitude, currentStartDistance);
                distance.setText(String.format("%.2fkm", currentStartDistance[0] / 1000));

                dropPins("Start Destination", startLatLng, "End Destination", endLatLng);
                new FetchURL(this).execute(createUrl(startPin.getPosition(), endPin.getPosition()), "driving");

                float[] startEndDist = new float[1];
                Location.distanceBetween(startLatLng.latitude, startLatLng.longitude,
                        endLatLng.latitude, endLatLng.longitude, startEndDist);
                startEndDistance.setText(String.format("%.2fkm", startEndDist[0] / 1000));

                long offerLong = (long) rideRequestSnapshot.get("fareOffer");
                int fareOffer = (int) offerLong;

                offer.setText("Offer: $" + String.format("%d", fareOffer));

                DocumentReference riderReference = (DocumentReference) rideRequestSnapshot.get("riderReference");
                riderReference.get().addOnSuccessListener(riderSnapshot -> {
                    DocumentReference userReference = (DocumentReference) riderSnapshot.get("userReference");
                    userReference.get().addOnSuccessListener(userSnapshot -> {
                        riderUsername = userSnapshot.get("username").toString();
                        username.setText(riderUsername);
                        firstName.setText(userSnapshot.get("firstName").toString());
                        lastName.setText(userSnapshot.get("lastName").toString());
                        /*if (userSnapshot.get("image") != "") {
                            Glide.with(this)
                                    .load(userSnapshot.get("image"))
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(riderPictureButton);
                        }*/
                        riderPictureButton.setImageResource(android.R.color.transparent);
                    });
                });
            });

            riderPictureButton.setOnClickListener(view -> db.collection("users")
                    .whereEqualTo("username", riderUsername)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot userSnapshot = task.getResult();
                            String userID = userSnapshot.getDocuments().get(0).getId();
                            ViewProfileFragment.newInstance(userID, riderUsername).show(getSupportFragmentManager(), "User");
                        }
                    })
            );

            cancelButton.setOnClickListener(view -> {
                RideRequestDAO dao = new RideRequestDAO();
                MutableLiveData<RideRequest> liveData = dao.getModelByReference(rideRequestReference);
                liveData.observe(this, rideRequest -> {
                    if (rideRequest != null) {
                        dao.cancelRequest(rideRequest, this);
                        Intent intent = new Intent(this, DriverMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            });
        } else {
            RideRequestListContent rideRequest = sharedPref.loadRideRequest();

        }
    }

    public boolean hasNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
    }

    @Override
    public void onBackPressed() {
        return;
    }
    /**
     * Called when the driver presses the "Ride complete" button and transition the driver to the
     * QR scan activity
     *
     * @param view
     */
    public void onDonePressed(View view) {
        // Todo(Joshua): Send notification to Rider

        RideRequestDAO dao = new RideRequestDAO();
        String[] tokens = Application.getInstance().getActiveRidePath().split("/");
        dao.getModelByID(tokens[tokens.length - 1]).observe(this, rideRequest -> {
            if(rideRequest != null) {
                RiderDAO riderDao = new RiderDAO();
                riderDao.getModelByReference(rideRequest.getRiderReference()).observe(this, rider-> {
                    if(rider != null) {
                        String token = rider.getFCMToken();
                        FCMSender.composeMessage(getApplicationContext(), token);
                    } else {
                        // Failed to get the rider info
                        Toast.makeText(getApplicationContext(), "Failed to find rider", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                // Failed to get the ride request info
                Toast.makeText(getApplicationContext(), "Failed to find ride info", Toast.LENGTH_LONG).show();
            }
        });

        Intent intent = new Intent(getApplicationContext(), QRScanActivity.class);
        startActivity(intent);
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

        if (sharedPref.loadNightModeState()) {
            boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.NightMap)));
            if (!success) {
                Log.e("Bad Style", "Style parsing failed.");
            }
        }

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

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            currentLocation = location;
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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
