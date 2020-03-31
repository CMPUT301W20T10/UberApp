package com.cmput301w20t10.uberapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.fragments.RideRatingFragment;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.cmput301w20t10.uberapp.util.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class RideHistoryActivity extends BaseActivity {
    private ListView historyListView;
    private RadioGroup toggle;
    private boolean displayActives = true;
    private volatile List<RideRequest> historyList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        setContentView(R.layout.activity_ride_history);

        historyListView = findViewById(R.id.historyList);

        populateHistory(displayActives); // pull the inital list

        // set up update button listener to refresh list
        Button updateButton = findViewById(R.id.history_update_button);
        updateButton.setOnClickListener(v -> {
            populateHistory(displayActives);
        });

        // set up toggle listener to update list
        toggle = findViewById(R.id.active_old_toggle);
        toggle.setOnCheckedChangeListener(((group, checkedId) -> {
            if (checkedId == R.id.active_button) {
                displayActives = true;
            } else {
                displayActives = false;
            }

            populateHistory(displayActives);
        }));
    }

    private void updateView() {
        HistoryAdapter adapter = new HistoryAdapter(this, Glide.with(this), historyList);
        historyListView.setAdapter(adapter);
        historyListView.setOnItemClickListener((adapterView, view, i, l) -> {
            Application.getInstance().setSelectedHistoryRequest(historyList.get(i));

            FragmentManager fragManager = getSupportFragmentManager();
            FragmentTransaction fragTransaction = fragManager.beginTransaction();
            RideRatingFragment rateFrag = new RideRatingFragment();
            fragTransaction.add(R.id.rating_container, rateFrag);
            fragTransaction.commit();

            populateHistory(displayActives); // refresh list
        });
    }

    private void populateHistory(boolean active) {
        User user = Application.getInstance().getCurrentUser();
        historyList = new ArrayList<RideRequest>();
        RideRequestDAO rrDAO = DatabaseManager.getInstance().getRideRequestDAO();

        if (user instanceof Rider) {
            Rider rider = (Rider) user;
            Log.d("Testing", "Get ride history of rider: " + rider.getUsername());

            MutableLiveData<List<RideRequest>> liveRides;
            if (active) {
                Log.d("Testing", "Retrieving active rides");
                liveRides = rrDAO.getAllActiveRideRequest(rider);
            } else {
                Log.d("Testing", "Retrieving historic rides");
                liveRides = rrDAO.getRideHistory(rider);
            }

            liveRides.observe(this, list -> {
                if (list != null) {
                    Log.d("Testing", "Ride history length: " + list.size());
                    historyList.addAll(list); // Append to end
                } else {
                    Log.d("Testing", "Past Rides NULL");
                }
                updateView();
            });

        } else {
            // TODO: Handle driver
        }
    }
}
