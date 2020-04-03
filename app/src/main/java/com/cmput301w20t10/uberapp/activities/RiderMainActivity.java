package com.cmput301w20t10.uberapp.activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.ui.AppBarConfiguration;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.Directions.FetchURL;
import com.cmput301w20t10.uberapp.Directions.TaskLoadedCallback;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.viewmodel.RiderViewModel;
import com.cmput301w20t10.uberapp.fragments.NewRideFragment;
import com.cmput301w20t10.uberapp.fragments.ViewProfileFragment;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.maps.android.SphericalUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

// todo: editable map markers


public class RiderMainActivity extends BaseActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static final String TAG = "Test" ;
    private static final int REQUEST_CODE = 101;
    SharedPref sharedPref;

    // core objects
    private AppBarConfiguration mAppBarConfiguration;
    private boolean locationPermissionGranted;
    private GoogleMap mainMap;

    // live data
    private RiderViewModel viewModel;
    private MutableLiveData<Route> routeLiveData;

    // local data
    private Route route;
    private static final float DEFAULT_ZOOM = 15f;

    private Location currentLocation;
    private LatLng currentLocLatLng;
    private RectangularBounds bounds;
    private LatLng boundNE;
    private LatLng boundSW;
    Polyline currentPolyline;

    private String startPos;
    private MarkerOptions startPin;
    private LatLng startPosLatLng;
    private Marker startMarker;
    private String destination;
    private LatLng destinationLatLng;
    private MarkerOptions destinationPin;
    private Marker destinationMarker;

    private FusedLocationProviderClient client;

    private PlacesClient placesClient;
    private AutocompleteSupportFragment autocompleteStartFragment;
    private AutocompleteSupportFragment autocompleteDestinationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client  = LocationServices.getFusedLocationProviderClient(this);

        requestLocationPermission();

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        sharedPref.setHomeActivity(this.getLocalClassName());

        setContentView(R.layout.content_rider_main);

        // map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /**
         * Initialize Places. For simplicity, the API key is hard-coded. In a production
         * environment we recommend using a secure mechanism to manage API keys.
         */
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        }

        placesClient = Places.createClient(this);

        // Initialize the AutocompleteSupportFragment.
        autocompleteStartFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_starting_point);

        autocompleteDestinationFragment = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_destination);

        // this ensures that the data are saved no matter what
        // shenanigans that the android lifecycle throws at us
        viewModel = RiderViewModel.create(getApplication());
        routeLiveData = viewModel.getCurrentRoute();
        routeLiveData.observe(this, this::onRouteChanged);

        // get local data references
        route = routeLiveData.getValue();

        // setting up listener for buttons
        Button buttonNewRide = findViewById(R.id.button_new_ride);
        buttonNewRide.setOnClickListener(view -> onClick_NewRide());

    }

    @Override
    public void onBackPressed() {
        return;
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

        if (locationPermissionGranted) {
            getCurrentLocation();
            googleMap.setMyLocationEnabled(true);
        }

        autocompleteStartingPoint();
        autocompleteDestination();
    }

    private void autocompleteStartingPoint() {
        autocompleteStartFragment.a.setTextSize(20.0f);
        autocompleteStartFragment.a.setHintTextColor(R.attr.editTextColor);
        autocompleteStartFragment.setHint("Enter Starting Point");
        autocompleteStartFragment.setLocationBias(bounds);
        autocompleteStartFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteStartFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                startPos = place.getName();
                startPosLatLng = place.getLatLng();
                startPin = new MarkerOptions()
                        .position(place.getLatLng())
                        .title(place.getName())
                        .zIndex(1.0f);
                mainMap.setOnMapLoadedCallback(() -> {
                    startMarker = mainMap.addMarker(startPin);
                    addRoute(startMarker);
                    if (destinationPin != null && startPin != null) {
                        moveCamera(startMarker, destinationMarker);
                    }else {
                        moveCameraToPoint(startMarker);
                    }
                    if (route.getDestinationPosition() != null && route.getStartingPosition() != null) {
                        new FetchURL(RiderMainActivity.this).execute(create_URL(), "driving");
                    }
                });
            }
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        View startClearButton = autocompleteStartFragment.getView().findViewById(R.id.places_autocomplete_clear_button);
        startClearButton.setOnClickListener(view -> {
            autocompleteStartFragment.setText("");
            startPos= null;
            currentPolyline.remove();
            startMarker.remove();
        });
    }


    private void autocompleteDestination() {
        autocompleteDestinationFragment.a.setTextSize(20.0f);
        autocompleteDestinationFragment.a.setHintTextColor(R.attr.editTextColor);
        autocompleteDestinationFragment.a.getText();
        autocompleteDestinationFragment.setHint("Enter Destination");
        autocompleteDestinationFragment.setLocationBias(bounds);
        autocompleteDestinationFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteDestinationFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                destination = place.getName();
                destinationLatLng = place.getLatLng();
                destinationPin = new MarkerOptions()
                        .position(place.getLatLng())
                        .title(place.getName())
                        .zIndex(1.0f);
                mainMap.setOnMapLoadedCallback(() -> {
                    destinationMarker = mainMap.addMarker(destinationPin);
                    addRoute(destinationMarker);
                    if (destinationPin != null && startPin != null) {
                        moveCamera(startMarker, destinationMarker);
                    } else {
                        moveCameraToPoint(destinationMarker);
                    }
                    if (route.getDestinationPosition() != null && route.getStartingPosition() != null) {
                        new FetchURL(RiderMainActivity.this).execute(create_URL(), "driving");
                    }
                });
            }
            @Override
            public void onError(Status status) {
                Log.i(TAG, "An error occurred: " + status);
            }
        });
        View destClearButton = autocompleteDestinationFragment.getView().findViewById(R.id.places_autocomplete_clear_button);
        destClearButton.setOnClickListener(view -> {
            autocompleteDestinationFragment.setText("");
            destination = null;
            currentPolyline.remove();
            destinationMarker.remove();
        });
    }



    private void onClick_NewRide() {
        if (startPos == null || destination == null) {
            Toast.makeText(this, "Fields cannot be empty!", Toast.LENGTH_LONG).show();
            return;
        }

        Bundle args = new Bundle();
        args.putString("StartPosition", startPos);
        args.putString("Destination", destination);
        Application.getInstance().setRoute(route);
        float[] distance = new float[1];
        Location.distanceBetween(startPosLatLng.latitude, startPosLatLng.longitude, destinationLatLng.latitude, destinationLatLng.longitude, distance);
        int priceOffer = (int) Math.round(10 + distance[0]/1000 * 1.75);
        args.putInt("offer", priceOffer);
        NewRideFragment fragment = new NewRideFragment();
        fragment.setArguments(args);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.new_ride_container, fragment);
        transaction.commit();
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
        if (location != null) {
            currentLocation = location;
            currentLocLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            boundNE = SphericalUtil.computeOffset(currentLocLatLng, 30*1000, 45);
            boundSW = SphericalUtil.computeOffset(currentLocLatLng, 30*1000, 225);
            bounds = RectangularBounds.newInstance(boundSW, boundNE);
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private String create_URL(){
        //start of rout
        String origin = "origin=" + route.getStartingPosition().latitude + "," + route.getStartingPosition().longitude;
        //end of route
        String dest = "destination=" + route.getDestinationPosition().latitude + ',' + route.getDestinationPosition().longitude;
        //mode
        String mode = "mode=driving";
        //parameter
        String parameter = origin + "&" + dest + "&" + mode;
        //output format
        String output = "json";
        //build url
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameter + "&key=" + getString(R.string.google_maps_key);
        return url;
    }

    private void moveCamera(Marker startMarker, Marker endMarker){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(startMarker.getPosition());
        builder.include(endMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mainMap.animateCamera(cu);
    }

    private void moveCameraToPoint(Marker Marker){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(Marker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        mainMap.animateCamera(cu);
    }

    private void addRoute(Marker marker){
        route.addLocation(marker);
        routeLiveData.setValue(route);
    }

    private void onRouteChanged(Route route) {
        // todo: improve RiderMainActivity.onRouteChanged
//        editTextStartingPoint.setText(route.getStartingPointString());
//        editTextDestination.setText(route.getDestinationString());
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline !=null){
            currentPolyline.remove();
        }
        currentPolyline = mainMap.addPolyline((PolylineOptions) values[0]);
    }
}
