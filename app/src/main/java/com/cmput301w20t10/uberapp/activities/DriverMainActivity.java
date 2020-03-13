package com.cmput301w20t10.uberapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.RequestList;
import com.cmput301w20t10.uberapp.models.ResizeAnimation;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class DriverMainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mainMap;
    private Location currentLocation;
    private FusedLocationProviderClient client;

    ListView requestList;
    ArrayAdapter<RideRequest> requestAdapter;
    ArrayList<RideRequest> requestDataList;

    FirebaseFirestore db;

    private boolean accordion = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_driver_main);

        Display display = this.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;
        final int collapsedHeight = 320;
        final int expandedHeight = height / 2;

        // map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ArrayList<LatLng> arrayPoints = new ArrayList<LatLng>();

        requestList = findViewById(R.id.ride_request_list);
        requestDataList = new ArrayList<>();

        /*
         * Used code from YouTube video to ask location permission and get current location
         * YouTube video posted by "Android Coding"
         * Title: How to Show Current Location On Map in Android Studio | CurrentLocation | Android Coding
         * URL: https://www.youtube.com/watch?v=boyyLhXAZAQ
         */
        client = LocationServices.getFusedLocationProviderClient(this);
        // get last know location of device
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    //System.out.println(currentLocation.getLatitude() + " " + currentLocation.getLongitude());
                }
            }
        });

        db = FirebaseFirestore.getInstance();
        final CollectionReference unpairedRequestsReference = db.collection("unpairedRideList");
        unpairedRequestsReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference requestReference = (DocumentReference) document.get("rideRequestReference");
                        requestReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot rideRequestSnapshot) {
                                if (rideRequestSnapshot.exists()) {
                                    DocumentReference ridersReference = (DocumentReference) rideRequestSnapshot.get("riderReference");
                                    GeoPoint geoPoint = rideRequestSnapshot.getGeoPoint("destination");
                                    double lat = geoPoint.getLatitude();
                                    double lng = geoPoint.getLongitude();
                                    float[] distance = new float[1];
                                    Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), lat, lng, distance);
                                    float offer = (long) rideRequestSnapshot.get("fareOffer");
                                    ridersReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot riderSnapshot) {
                                            if (riderSnapshot.exists()) {
                                                DocumentReference usersReference = (DocumentReference) riderSnapshot.get("userReference");
                                                usersReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot usersSnapshot) {
                                                        if (usersSnapshot.exists()) {
                                                            String username = (String) usersSnapshot.get("username");
                                                            String firstName = (String) usersSnapshot.get("firstName");
                                                            String lastName = (String) usersSnapshot.get("lastName");
                                                            requestDataList.add((new RideRequest(username, distance[0] / 1000, offer, usersReference, firstName, lastName,
                                                                    collapsedHeight, collapsedHeight, expandedHeight)));
                                                            Collections.sort(requestDataList);
                                                            requestAdapter = new RequestList(DriverMainActivity.this, requestDataList);
                                                            requestList.setAdapter(requestAdapter);
                                                        } else {
                                                            Toast.makeText(DriverMainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            } else {
                                                Toast.makeText(DriverMainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                } else {
                                    Toast.makeText(DriverMainActivity.this, "Document does not exist", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                } else {
                    Log.d("thing2", "Error getting Documents", task.getException());
                }
            }
        });

        requestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                toggle(view, i);
            }
        });
    }

    public void onPicturePressed(View view) {
        /**
         * GOTO USER PROFILE
         */
    }

    /*
     * toggle() is code from Stack overflow used to toggle expansion of listview item
     * URL of question: https://stackoverflow.com/questions/12522348
     * Asked by: Ethan Allen, https://stackoverflow.com/users/546509/ethan-allen
     * Answered by: Leonardo Cardoso, https://stackoverflow.com/users/1255990/leonardo-cardoso
     */
    private void toggle(View view, final int position) {
        RideRequest rideRequest = requestDataList.get(position);
        rideRequest.getHolder().setTextViewWrap((LinearLayout) view);

        int fromHeight = 0;
        int toHeight = 0;

        if (rideRequest.isOpen()) {
            fromHeight = rideRequest.getExpandedHeight();
            toHeight = rideRequest.getCollapsedHeight();
        } else {
            fromHeight = rideRequest.getCollapsedHeight();
            toHeight = rideRequest.getExpandedHeight();

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
     */
    private void closeAll() {
        int i = 0;
        for (RideRequest rideRequest : requestDataList) {
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
     */
    private void toggleAnimation(final RideRequest rideRequest, final int position, final int fromHeight, final int toHeight, final boolean goToItem) {
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

        googleMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

        if (location != null) {
            mainMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mainMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }
}
