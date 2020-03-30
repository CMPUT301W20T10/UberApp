package com.cmput301w20t10.uberapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.Directions.FetchURL;
import com.cmput301w20t10.uberapp.Directions.TaskLoadedCallback;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.dao.UnpairedRideListDAO;
import com.cmput301w20t10.uberapp.fragments.ViewProfileFragment;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RequestList;
import com.cmput301w20t10.uberapp.models.ResizeAnimation;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DriverMainActivity extends BaseActivity implements OnMapReadyCallback, TaskLoadedCallback {

    private static final String LAST_LOCATION_KEY = "location";
    private static final String CAMERA_DIRECTION_KEY = "camera_direction";
    private static final String USER_REFERENCE = "userReference";
    private static final String USERNAME = "username";
    private static final String IMAGE = "image";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    private static final int REQUEST_CODE = 101;
    private GoogleMap mainMap;
    private Location currentLocation;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient client;

    Polyline currentPolyline;

    private MarkerOptions startPin;
    private MarkerOptions endPin;

    SharedPref sharedPref;

    ListView requestList;
    ArrayAdapter<RideRequestListContent> requestAdapter;
    ArrayList<RideRequestListContent> requestDataList;

    private FirebaseFirestore db;

    private boolean accordion = true;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = LocationServices.getFusedLocationProviderClient(this);

        requestLocationPermission();

        db = FirebaseFirestore.getInstance();

        // Retrieve location and camera direction from savedInstanceState
        if (savedInstanceState != null) {
            currentLocation = savedInstanceState.getParcelable(LAST_LOCATION_KEY);
            CameraPosition cameraPosition = savedInstanceState.getParcelable(CAMERA_DIRECTION_KEY);
        }

        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        final int collapsedHeight = 320;
        final int expandedHeight = height / 2;

        sharedPref = new SharedPref(this);
        System.out.println("SHARED: " + sharedPref);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        setContentView(R.layout.content_driver_main);

        // map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        System.out.println("NAME: " + this.getLocalClassName());

        requestList = findViewById(R.id.ride_request_list);
        requestDataList = new ArrayList<>();

        UnpairedRideListDAO dao = new UnpairedRideListDAO();
        MutableLiveData<List<RideRequest>> liveRideRequest = dao.getAllUnpairedRideRequest();
        AtomicInteger counter = new AtomicInteger(0);
        liveRideRequest.observe(this, rideRequests -> {
            if (!rideRequests.isEmpty()) {
                DocumentReference rideRequestReference = rideRequests.get(counter.getAndAdd(0)).getRideRequestReference();
                DocumentReference unpairedReference = rideRequests.get(counter.getAndAdd(0)).getUnpairedReference();
                float offer = rideRequests.get(counter.getAndAdd(0)).getFareOffer();
                LatLng startDest = rideRequests.get(counter.getAndAdd(0)).getRoute().getStartingPosition();
                LatLng endDest = rideRequests.get(counter.getAndAdd(0)).getRoute().getDestinationPosition();
                String riderPath = rideRequests.get(counter.getAndAdd(1)).getRiderReference().getPath();
                DocumentReference riderReference = db.document(riderPath);
                riderReference.get().addOnSuccessListener(riderSnapshot -> {
                    if (riderSnapshot.exists()) {
                        DocumentReference userReference = (DocumentReference) riderSnapshot.get(USER_REFERENCE);
                        userReference.get().addOnSuccessListener(userSnapshot -> {
                            if (userSnapshot.exists()) {
                                String username = (String) userSnapshot.get(USERNAME);
                                String imageURL = (String) userSnapshot.get(IMAGE);
                                String firstName = (String) userSnapshot.get(FIRST_NAME);
                                String lastName = (String) userSnapshot.get(LAST_NAME);
                                float[] distance = new float[1];
                                Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), startDest.latitude, startDest.longitude, distance);
                                RideRequestListContent rideRequest = new RideRequestListContent(username, distance[0] / 1000, offer,
                                        imageURL, firstName, lastName, startDest, endDest,
                                        rideRequestReference, unpairedReference);
                                rideRequest.setCollapsedHeight(collapsedHeight);
                                rideRequest.setCurrentHeight(collapsedHeight);
                                rideRequest.setExpandedHeight(expandedHeight);
                                requestDataList.add(rideRequest);
                                Collections.sort(requestDataList);
                                requestAdapter = new RequestList(DriverMainActivity.this, requestDataList);
                                requestList.setAdapter(requestAdapter);
                            }
                        });
                    }
                });
            }
        });

        requestList.setOnItemClickListener((adapterView, view, i, l) -> {
            toggle(view, i);
            final RideRequestListContent rideRequestContent = (RideRequestListContent) adapterView.getItemAtPosition(i);
            LatLng startDest = rideRequestContent.getStartDest();
            LatLng endDest = rideRequestContent.getEndDest();
            dropPins("Start Destination", startDest, "End Destination",  endDest);
            new FetchURL(DriverMainActivity.this).execute(createUrl(startPin.getPosition(), endPin.getPosition()), "driving");
            Button acceptButton = view.findViewById(R.id.accept_request_button);

            acceptButton.setOnClickListener(view1 -> {
                Driver driver = (Driver) Application.getInstance().getCurrentUser();
                RideRequestDAO rideRequestDAO = new RideRequestDAO();
                MutableLiveData<RideRequest> liveData = rideRequestDAO.getModelByReference(rideRequestContent.getRideRequestReference());
                liveData.observe(this, rideRequest -> {
                    if (rideRequest != null) {
                        rideRequestDAO.acceptRequest(rideRequest, driver, this);
                    }
                });
            });

            ImageButton riderPictureButton = view.findViewById(R.id.profile_picture);
            riderPictureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    db.collection("users")
                            .whereEqualTo("username", rideRequestContent.getUsername())
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    QuerySnapshot userSnapshot = task.getResult();
                                    System.out.println("MACA10: " + userSnapshot.getDocuments().get(0).getId());
                                    String userID = userSnapshot.getDocuments().get(0).getId();
                                    String username = rideRequestContent.getUsername();
                                    ViewProfileFragment.newInstance(userID, username) .show(getSupportFragmentManager(),"User");
                                }
                            });
                }
            });

            // For message passing, the driver must subscribe to a topic
            FirebaseMessaging.getInstance().subscribeToTopic(Application.getInstance().getCurrentUser().getUsername())
                    .addOnCompleteListener((task) -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(DriverMainActivity.this, "Successfully subscribed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(DriverMainActivity.this, "Failed to subscribe", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
}

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (mainMap != null) {
            savedInstanceState.putParcelable(CAMERA_DIRECTION_KEY, mainMap.getCameraPosition());
            savedInstanceState.putParcelable(LAST_LOCATION_KEY, currentLocation);
            super.onSaveInstanceState(savedInstanceState);
        }
    }

    /*
     * toggle() is code from Stack overflow used to toggle expansion of listview item
     * URL of question: https://stackoverflow.com/questions/12522348
     * Asked by: Ethan Allen, https://stackoverflow.com/users/546509/ethan-allen
     * Answered by: Leonardo Cardoso, https://stackoverflow.com/users/1255990/leonardo-cardoso
     * URL of answer: https://stackoverflow.com/a/22160822
     */
    private void toggle(View view, final int position) {
        RideRequestListContent rideRequest = requestDataList.get(position);
        rideRequest.getHolder().setTextViewWrap((LinearLayout) view);
        TextView tapProfileHint = view.findViewById(R.id.tap_profile_hint);

        int fromHeight = 0;
        int toHeight = 0;

        if (rideRequest.isOpen()) {
            fromHeight = rideRequest.getExpandedHeight();
            toHeight = rideRequest.getCollapsedHeight();
            tapProfileHint.setVisibility(View.INVISIBLE);
        } else {
            fromHeight = rideRequest.getCollapsedHeight();
            toHeight = rideRequest.getExpandedHeight();
            tapProfileHint.setVisibility(View.VISIBLE);

            // This closes all item before the selected one opens
            if (accordion) {
                closeAll();
            }
        }
        toggleAnimation(rideRequest, position, fromHeight, toHeight, true);
    }

    /*
     * closeAll() is code from Stack overflow used to collapse all listview items
     * URL of question: https://stackoverflow.com/questions/12522348
     * Asked by: Ethan Allen, https://stackoverflow.com/users/546509/ethan-allen
     * Answered by: Leonardo Cardoso, https://stackoverflow.com/users/1255990/leonardo-cardoso
     * URL of answer: https://stackoverflow.com/a/22160822
     */
    private void closeAll() {
        int i = 0;
        for (RideRequestListContent rideRequest : requestDataList) {
            if (rideRequest.isOpen()) {
                toggleAnimation(rideRequest, i, rideRequest.getExpandedHeight(), rideRequest.getCollapsedHeight(), false);
            }
            i++;
        }
    }

    /*
     * toggleAnimation() is code from Stack overflow used to display expand/collapse animation
     * URL of question: https://stackoverflow.com/questions/12522348
     * Asked by: Ethan Allen, https://stackoverflow.com/users/546509/ethan-allen
     * Answered by: Leonardo Cardoso, https://stackoverflow.com/users/1255990/leonardo-cardoso
     * URL of answer: https://stackoverflow.com/a/22160822
     */
    private void toggleAnimation(final RideRequestListContent rideRequest, final int position, final int fromHeight, final int toHeight, final boolean goToItem) {
        ResizeAnimation resizeAnimation = new ResizeAnimation(requestAdapter, rideRequest, 0, fromHeight, 0, toHeight);
        resizeAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rideRequest.setOpen(!rideRequest.isOpen());
                rideRequest.setCurrentHeight(toHeight);
                requestAdapter.notifyDataSetChanged();
                if (goToItem)
                    goToItem(position);
            }
        });
        rideRequest.getHolder().getTextViewWrap().startAnimation(resizeAnimation);
    }

    /*
     * goToItem() is code from Stack overflow used to move listview to expanded rideRequest
     * URL of question: https://stackoverflow.com/questions/12522348
     * Asked by: Ethan Allen, https://stackoverflow.com/users/546509/ethan-allen
     * Answered by: Leonardo Cardoso, https://stackoverflow.com/users/1255990/leonardo-cardoso
     * URL of answer: https://stackoverflow.com/a/22160822
     */
    private void goToItem(final int position) {
        requestList.post(new Runnable() {
            @Override
            public void run() {
                try {
                    requestList.smoothScrollToPosition(position);
                } catch (Exception e) {
                    requestList.setSelection(position);
                }
            }
        });
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
                if (mainMap != null) {
                    currentLocation = task.getResult();
                    mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 13));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))      // Sets the center of the map to location user
                            .zoom(17)                   // Sets the zoom
                            .build();                   // Creates a CameraPosition from the builder
                    mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
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
