package com.cmput301w20t10.uberapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.Directions.FetchURL;
import com.cmput301w20t10.uberapp.Directions.TaskLoadedCallback;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DriverMainActivity extends BaseActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private static final String TAG = "DriverTest" ;

    // Strings
    private static final String USER_REFERENCE = "userReference";
    private static final String USERNAME = "username";
    private static final String IMAGE = "image";
    private static final String FIRST_NAME = "firstName";
    private static final String LAST_NAME = "lastName";

    Driver driver;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kevin's todo list
        // TODO: TRY TO IMPLEMENT OFFLINE BEHAVIOUR

        /*
         * This section checks whether the driver already has an active ride request.
         * If yes, then changes to DriverAcceptedActivity
         */
        Application.getInstance().getLatestUserData().observe(this, user -> {
            if (user != null) {
                RideRequestDAO rrDAO = DatabaseManager.getInstance().getRideRequestDAO();
                driver = (Driver) user;
                MutableLiveData<List<RideRequest>> liveRides = rrDAO.getAllActiveRideRequest(driver);
                liveRides.observe(this, list -> {
                    if (list != null) {
                        System.out.println("RIDE: " + driver.getActiveRideRequestList().get(0).getPath());
                        Intent intent = new Intent(this, DriverAcceptedActivity.class);
                        Application.getInstance().setActiveRideID(list.get(0).getRideRequestReference().getId());
                        Application.getInstance().setPrevActivity(this.getLocalClassName());
                        startActivity(intent);
                    }
                });
            }
        });
//        }

        client = LocationServices.getFusedLocationProviderClient(this);

        requestLocationPermission();

        db = FirebaseFirestore.getInstance();

        /*
         * This section finds the heights used to expand and collapse the listview items
         */
        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        final int collapsedHeight = 320;
        final int expandedHeight = height / 2;

        // Sets the activity to dark theme if true saved in shared pref
        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState()) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        sharedPref.setHomeActivity(this.getLocalClassName());

        setContentView(R.layout.content_driver_main);

        // map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        requestList = findViewById(R.id.ride_request_list);
        requestDataList = new ArrayList<>();

        /*
         * This section finds all ride requests that have been requested by riders but not yet accepted
         * Uses RideRequestListContent to store the necessary information
         *
         */
        UnpairedRideListDAO dao = new UnpairedRideListDAO();
        MutableLiveData<List<RideRequest>> liveRideRequest = dao.getAllUnpairedRideRequest();
        AtomicInteger counter = new AtomicInteger(0);
        liveRideRequest.observe(this, rideRequests -> {
            if (!rideRequests.isEmpty()) {
                DocumentReference rideRequestReference = rideRequests.get(counter.getAndAdd(0)).getRideRequestReference();
                DocumentReference unpairedReference = rideRequests.get(counter.getAndAdd(0)).getUnpairedReference();
                int offerInCents = rideRequests.get(counter.getAndAdd(0)).getFareOffer();
                Log.d(TAG,"Offer: " + offerInCents/100);
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
                                RideRequestListContent rideRequest = new RideRequestListContent(username, distance[0] / 1000, offerInCents,
                                        imageURL, firstName, lastName, startDest, endDest, rideRequestReference,
                                        unpairedReference, collapsedHeight, collapsedHeight,expandedHeight);
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

        // OnClickListener for listview items
        requestList.setOnItemClickListener((adapterView, view, i, l) -> {
            toggle(view, i);
            final RideRequestListContent rideRequestContent = (RideRequestListContent) adapterView.getItemAtPosition(i);
            LatLng startDest = rideRequestContent.getStartDest();
            LatLng endDest = rideRequestContent.getEndDest();
            dropPins("Start Destination", startDest, "End Destination",  endDest);
            new FetchURL(DriverMainActivity.this).execute(createUrl(startPin.getPosition(), endPin.getPosition()), "driving");

            /*
             * Accept button that is revealed after clicking list item
             * Transitions to DriverAcceptedActivity
             */
            Button acceptButton = view.findViewById(R.id.accept_request_button);
            acceptButton.setOnClickListener(acceptView -> {
                RideRequestDAO rideRequestDAO = new RideRequestDAO();
                MutableLiveData<RideRequest> liveData = rideRequestDAO.getModelByReference(rideRequestContent.getRideRequestReference());
                liveData.observe(this, rideRequest -> {
                    if (rideRequest != null) {
                        rideRequestDAO.acceptRequest(rideRequest, driver, this);
                        sharedPref.setRideRequest(rideRequestContent);
                        Intent intent = new Intent(this, DriverAcceptedActivity.class);
                        Application.getInstance().setActiveRideID(rideRequest.getRideRequestReference().getId());
                        Application.getInstance().setPrevActivity(this.getLocalClassName());
                        startActivity(intent);
                        finish();
                    }
                });
            });

            /*
             * Image button for the profile picture.
             * On click shows a fragment displaying the corresponding rider's contact information
             */
            ImageButton riderPictureButton = view.findViewById(R.id.profile_button);
            riderPictureButton.setOnClickListener(pictureView -> db.collection("users")
                    .whereEqualTo("username", rideRequestContent.getUsername())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            QuerySnapshot userSnapshot = task.getResult();
                            String userID = userSnapshot.getDocuments().get(0).getId();
                            String username = rideRequestContent.getUsername();
                            ViewProfileFragment.newInstance(userID, username) .show(getSupportFragmentManager(),"User");
                        }
                    })
            );

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
    public void onBackPressed() {
        return;
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

        if (sharedPref.loadNightModeState()) {
            boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources().getString(R.string.NightMap)));
            if (!success) {
                Log.e("Bad Style", "Style parsing failed.");
            }
        }

        if (locationPermissionGranted) {
            getCurrentLocation();
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mainMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
                    return false;
                }
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
            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(currentLatLng)      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    /*
     * Move camera and zoom level to fit start and end pin as well as current location
     */
    private void moveCamera(Marker startMarker, Marker endMarker){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include(startMarker.getPosition());
        builder.include(endMarker.getPosition());
        builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

        LatLngBounds bounds = builder.build();
        int padding = 100; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mainMap.moveCamera(cu);
        mainMap.animateCamera(cu);

    }

    /*
     * Drop custom pins on the map
     * Pins for start and end destinations
     */
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
