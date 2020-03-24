package com.cmput301w20t10.uberapp.activities;


import android.location.Address;
import android.location.Geocoder;
import android.content.Intent;
import android.os.Bundle;

import com.cmput301w20t10.uberapp.Directions.TaskLoadedCallback;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.Route;
import com.cmput301w20t10.uberapp.database.viewmodel.RiderViewModel;
import com.cmput301w20t10.uberapp.Directions.FetchURL;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import android.util.Log;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// todo: editable map markers


public class RiderMainActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static final String TAG = "Test" ;
    // core objects
    private AppBarConfiguration mAppBarConfiguration;
    private GoogleMap mainMap;

    // live data
    private RiderViewModel viewModel;
    private MutableLiveData<Route> routeLiveData;

    // local data
    private Route route;

    // views
    TextInputEditText editTextStartingPoint;
    TextInputEditText editTextDestination;
    TextInputEditText editTextPriceOffer;

    private static final float DEFAULT_ZOOM = 15f;

    LatLngBounds.Builder builder = new LatLngBounds.Builder();

    Polyline currentPolyline;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_rider_main);

        // map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // get reference for the destination and starting point texts
        editTextStartingPoint = findViewById(R.id.text_starting_point);
        editTextDestination = findViewById(R.id.text_destination);
        editTextPriceOffer = findViewById(R.id.text_price_offer);


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

        // Add listener
        /*
        mainMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng);
            Marker marker = mainMap.addMarker(markerOptions);
            mainMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // todo: improve routeLiveData validation in rider main screen
            route.addLocation(marker);
            routeLiveData.setValue(route);
        });
        */
    }

    private void onClick_NewRide() {
        // todo: implement onclick new ride
        String startingpoint = editTextStartingPoint.getText().toString();
        String destination = editTextDestination.getText().toString();
        String priceOffer = editTextPriceOffer.getText().toString();

        Geocoder geocoder = new Geocoder(RiderMainActivity.this);
        List<Address> startingPointList = new ArrayList<>();
        List<Address> destinationList = new ArrayList<>();

        try{
            startingPointList = geocoder.getFromLocationName(startingpoint, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException on start address: "+ e.getMessage());
        }

        try{
            destinationList = geocoder.getFromLocationName(destination, 1);
        }catch (IOException e){
            Log.e(TAG, "geoLocate: IOException on destination address: "+ e.getMessage());
        }

        if (startingPointList.size() > 0){
            Address startingAdddress = startingPointList.get(0);
            Log.d(TAG, "geoLocate: found a location: " + startingAdddress.toString());
            //drop pin at sdtarting position
            dropPin(startingAdddress.getAddressLine(0), new LatLng( startingAdddress.getLatitude(), startingAdddress.getLongitude()));
        }

        if (destinationList.size() > 0){
            Address destinationAddress = destinationList.get(0);
            Log.d(TAG, "geoLocate: found a location: " + destinationAddress.toString());

            //move camera to destination and drop pin
            //moveCamera(new LatLng( destinationAddress.getLatitude(), destinationAddress.getLongitude()), DEFAULT_ZOOM);
            dropPin(destinationAddress.getAddressLine(0), new LatLng( destinationAddress.getLatitude(), destinationAddress.getLongitude()));
        }

        //this part would draw a route if direction API was enabled.. figuring out another way//
        String url = create_URL();
        new FetchURL(RiderMainActivity.this).execute(url, "driving");
    }

    private String create_URL(){
        //start of rout
        String origin = "origin=" + route.getStartingPosition().latitude + "," + route.getDestinationPosition().longitude;
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

    public static String requestDirection(String reqURL) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqURL);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            //get the response result
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine()) !=null){
                stringBuffer.append(line);
            }

            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if (inputStream !=null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return  responseString;
    }

    private void moveCamera(MarkerOptions pin){
        //Log.d(TAG, "moveCamers: moving the camera to: lat " + latLng.latitude + ", lng: " + latLng.longitude);
        //mainMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        builder.include(pin.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        mainMap.animateCamera(cu);


    }

    private void dropPin(String title, LatLng latLng){
        MarkerOptions pin = new MarkerOptions()
                .position(latLng)
                .title(title);
        Marker marker =mainMap.addMarker(pin);

        route.addLocation(marker);
        routeLiveData.setValue(route);
        moveCamera(pin);
    }

    private void onRouteChanged(Route route) {
        // todo: improve RiderMainActivity.onRouteChanged
        editTextStartingPoint.setText(route.getStartingPointString());
        editTextDestination.setText(route.getDestinationString());
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline !=null){
            currentPolyline.remove();
        }
        currentPolyline = mainMap.addPolyline((PolylineOptions) values[0]);
    }
}
