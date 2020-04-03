package com.cmput301w20t10.uberapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.Directions.FetchURL;
import com.cmput301w20t10.uberapp.Directions.TaskLoadedCallback;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
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
import java.util.List;
import java.util.Locale;

public class DriverAcceptedActivity extends BaseActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final int REQUEST_CODE = 101;

    SharedPref sharedPref;

    String riderUsername;

    private FirebaseFirestore db;

    private GoogleMap mainMap;
    private Location currentLocation;
    private LatLng currentLatLng;
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
        Button finishButton = findViewById(R.id.finish_request_button);
        finishButton.setVisibility(View.VISIBLE);
        Button cancelButton = findViewById(R.id.cancel_request_button);
        cancelButton.setVisibility(View.VISIBLE);
        TextView tapProfileHint = findViewById(R.id.tap_profile_hint);
        tapProfileHint.setVisibility(View.VISIBLE);

        if (hasNetwork()) {
            String activeRideID = Application.getInstance().getActiveRideID();

            RideRequestDAO dao = new RideRequestDAO();
            dao.getModelByID(activeRideID).observe(this, rideRequest -> {
                if(rideRequest != null) {
                    RiderDAO riderDao = new RiderDAO();

                    LatLng startLatLng = rideRequest.getRoute().getStartingPosition();
                    startDest.setText(getAddress(startLatLng));
                    LatLng endLatLng = rideRequest.getRoute().getDestinationPosition();
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

                    double offerDec = ((double) rideRequest.getFareOffer()) /100;
                    offer.setText("Offer: $" + String.format("%.2f", offerDec));

                    riderDao.getModelByReference(rideRequest.getRiderReference()).observe(this, rider -> {
                        if (rider != null) {
                            riderUsername = rider.getUsername();
                            username.setText(riderUsername);
                            firstName.setText(rider.getFirstName());
                            lastName.setText(rider.getLastName());
                            if (rider.getImage() != "") {
                            Glide.with(this)
                                    .load(rider.getImage())
                                    .apply(RequestOptions.circleCropTransform())
                                    .into(riderPictureButton);
                            }
                        }
                    });
                }
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

            finishButton.setOnClickListener(view -> onDonePressed(view));

            cancelButton.setOnClickListener(view -> {
                dao.getModelByID(activeRideID).observe(this, rideRequest -> {
                    if (rideRequest != null) {
                        dao.cancelRequest(rideRequest, this);
                        Runnable r = () -> goBack();
                        Handler myHandler = new Handler();
                        myHandler.postDelayed(r, 1500);
                    }
                });
            });
        } else {
            RideRequestListContent rideRequest = sharedPref.loadRideRequest();
            username.setText(rideRequest.getUsername());
            distance.setText(String.format("%.2fkm", rideRequest.getDistance()));
            offer.setText(String.format("Offer: $%d", rideRequest.getOffer()));
            firstName.setText(rideRequest.getFirstName());
            lastName.setText(rideRequest.getLastName());
            LatLng startLatLng = rideRequest.getStartDest();
            startDest.setText(getAddress(startLatLng));
            LatLng endLatLng = rideRequest.getEndDest();
            endDest.setText(getAddress(endLatLng));
            float[] startEndDist = new float[1];
            Location.distanceBetween(startLatLng.latitude, startLatLng.longitude,
                    endLatLng.latitude, endLatLng.longitude, startEndDist);
            startEndDistance.setText(String.format("%.2fkm", startEndDist[0] / 1000));

//            dropPins("Start Destination", startLatLng, "End Destination", endLatLng);
//            new FetchURL(this).execute(createUrl(startPin.getPosition(), endPin.getPosition()), "driving");

            cancelButton.setOnClickListener(view -> {
                Toast.makeText(getApplicationContext(), "Internet required to cancel", Toast.LENGTH_LONG).show();
            });

            finishButton.setOnClickListener(view -> {
                Toast.makeText(getApplicationContext(), "Internet required to finish", Toast.LENGTH_LONG).show();
            });

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
        dao.getModelByID(Application.getInstance().getActiveRideID()).observe(this, rideRequest -> {
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
        finish();
    }

    private void goBack() {
        Intent intent = new Intent(this, DriverMainActivity.class);
        Application.getInstance().setPrevActivity(this.getLocalClassName());
        startActivity(intent);
        finish();
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
        if (locationPermissionGranted && hasNetwork()) {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(() -> {
                mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 18f));
                return true;
            });
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
        }
    }


    private void moveCamera(Marker startMarker, Marker endMarker){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(startMarker.getPosition());
        builder.include(endMarker.getPosition());
        builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

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
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_marker_start_48dp));
        endPin = new MarkerOptions()
                .position(endDest)
                .title(endTitle)
                .icon(bitmapDescriptorFromVector(this, R.drawable.ic_marker_destination_48dp));

        mainMap.clear();

        Marker startMarker = mainMap.addMarker(startPin);
        Marker endMarker = mainMap.addMarker(endPin);

        moveCamera(startMarker, endMarker);
    }

    /*
     * Code from Stack Overflow to convert Vector to Bitmap
     * URL to question: https://stackoverflow.com/questions/42365658/custom-marker-in-google-maps-in-android-with-vector-asset-icon
     * Asked by: Shuddh, https://stackoverflow.com/users/3345454/shuddh
     * Answered by: Leo Droidcoder, https://stackoverflow.com/users/5730321/leo-droidcoder
     * URL to answer: https://stackoverflow.com/a/45564994
     */
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, 80, 80);
        Bitmap bitmap = Bitmap.createBitmap(80, 80, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline !=null){
            currentPolyline.remove();
        }
        currentPolyline = mainMap.addPolyline((PolylineOptions) values[0]);
    }

}
