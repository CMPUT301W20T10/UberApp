package com.cmput301w20t10.uberapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.viewmodel.RiderViewModel;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DriverMainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private AppBarConfiguration mAppBarConfiguration;
    private GoogleMap mainMap;
    private Location currentLocation;
    private LatLng thing;
    private FusedLocationProviderClient client;
    private static final int REQUEST_CODE = 101;

    ListView requestList;
    ArrayAdapter<RideRequest> requestAdapter;
    ArrayList<RideRequest> requestDataList;

    FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_driver_main);

//        Toolbar toolbar = findViewById(R.id.toolbar_driver);
//        setSupportActionBar(toolbar);
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        NavigationView navigationView = findViewById(R.id.nav_view);

        requestList = findViewById(R.id.ride_request_list);

        String []usernames = {"Kevin","Jason","Dano","Michael","Lucas"};
        Float []distances = {3.5f,5f,22f,0.6f,395f};
        Float []offers = {4f, 6.2f, 59f, 0.2f, 952.56f};

        requestDataList = new ArrayList<>();
        client = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(DriverMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }
        client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    System.out.println(currentLocation.getLatitude() + " " + currentLocation.getLongitude());
                }

            }
        });


        db = FirebaseFirestore.getInstance();
        final CollectionReference unpairedRequestsReference = db.collection("unpairedRideList");
        unpairedRequestsReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final int[] counter = {1};
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference requestReference = (DocumentReference) document.get("rideRequestReference");
                        requestReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot rideRequestSnapshot) {
                                //System.out.println("Document data: " + documentSnapshot.get("fareOffer"));
                                //System.out.println("Document data: " + String.format("%s: %s" ,docRef.getId(), documentSnapshot.get("fareOffer")));
                                //System.out.println(("counter: " + counter[0]));
                                if (rideRequestSnapshot.exists()) {
                                    DocumentReference ridersReference = (DocumentReference) rideRequestSnapshot.get("riderReference");
                                    GeoPoint geoPoint = rideRequestSnapshot.getGeoPoint("destination");
                                    double lat = geoPoint.getLatitude();
                                    double lng = geoPoint.getLongitude();
                                    LatLng latLng = new LatLng(lat, lng);
                                    //float distance = (long) rideRequestSnapshot.get("fareOffer");
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
                                                            //System.out.println("Document data: " + usersSnapshot.get("firstName"));
                                                            //counter[0] = counter[0] + 1;
                                                            String username = (String) usersSnapshot.get("username");
                                                            //System.out.println(currentLocation.distanceTo(latLng).getClass().getName());
                                                            //System.out.println("Document data: " + username + " " + String.format("%.2f", distance[0]/1000) + " "+ offer);
                                                            requestDataList.add((new RideRequest(username, distance[0]/1000, offer)));
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



                        //Log.d("thing", document.get("rideRequestReference").toString());
                    }
                } else {
                    Log.d("thing2", "Error getting Documents", task.getException());
                }
            }
        });




        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        mAppBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
//                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
//                .setDrawerLayout(drawer)
//                .build();

        // on support navigate up
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.rider_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }

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
        });
    }
}
