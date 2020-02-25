package com.cmput301w20t10.uberapp.activities;

import android.os.Bundle;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.Route;
import com.cmput301w20t10.uberapp.database.viewmodel.DriverMainViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.Button;

// todo: editable map markers

public class DriverMainActivity extends AppCompatActivity implements OnMapReadyCallback {
    // core objects
    private AppBarConfiguration mAppBarConfiguration;
    private GoogleMap mainMap;

    // live data
    private DriverMainViewModel viewModel;
    private MutableLiveData<Route> routeLiveData;

    // local data
    private Route route;

    // views
    TextInputEditText editTextStartingPoint;
    TextInputEditText editTextDestination;
    TextInputEditText editTextPriceOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();

        // on support navigate up
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

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
        viewModel = DriverMainViewModel.create(getApplication());
        routeLiveData = viewModel.getCurrentRoute();
        routeLiveData.observe(this, this::onRouteChanged);

        // get local data references
        route = routeLiveData.getValue();

        // setting up listener for buttons
        Button buttonNewRide = findViewById(R.id.button_new_ride);
        buttonNewRide.setOnClickListener(view -> onClick_NewRide());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.driver_main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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
        mainMap.setOnMapClickListener(latLng -> {
            MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("Marker somewhere");
            Marker marker = mainMap.addMarker(markerOptions);
            mainMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // todo: improve routeLiveData validation in driver main screen
            route.addLocation(marker);
            routeLiveData.setValue(route);
        });
    }

    private void onClick_NewRide() {
        // todo: implement onclick new ride
    }

    private void onRouteChanged(Route route) {
        // todo: improve DriverMainActivity.onRouteChanged
        editTextStartingPoint.setText(route.getStartingPointString());
        editTextDestination.setText(route.getDestinationString());
    }
}
