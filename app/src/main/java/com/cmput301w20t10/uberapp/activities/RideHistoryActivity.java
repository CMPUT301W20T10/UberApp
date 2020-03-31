package com.cmput301w20t10.uberapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

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
    private volatile List<RideRequest> historyList;
    private RideRequestDAO rrDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        setContentView(R.layout.activity_ride_history);

        historyListView = findViewById(R.id.historyList);

        rrDAO = DatabaseManager.getInstance().getRideRequestDAO();

        populateHistory();

        Button updateButton = findViewById(R.id.history_update_button);
        updateButton.setOnClickListener(v -> {
            populateHistory();
        });
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

            populateHistory(); // refresh list
        });
    }

    private void populateHistory() {
        User user = Application.getInstance().getCurrentUser();
        historyList = new ArrayList<RideRequest>();

        if (user instanceof Rider) {
            Rider rider = (Rider) user;
            Log.d("Testing", "Get ride history of rider: " + rider.getUsername());

            MutableLiveData<List<RideRequest>> liveHistory = rrDAO.getRideHistory(rider);
            liveHistory.observe(this, list -> {
                if (list != null) {
                    Log.d("Testing", "Ride history length: " + list.size());
                    historyList.addAll(list); // Append to end
                    updateView();
                } else {
                    Log.d("Testing", "Past Rides NULL");
                }
            });


            MutableLiveData<List<RideRequest>> liveActive = rrDAO.getAllActiveRideRequest(rider);
            liveActive.observe(this, actives -> {
                if (actives != null) {
                    Log.d("Testing", "Active rides length: " + actives.size());
                    historyList.addAll(0, actives); // append to beginning
                    updateView();
                } else {
                    Log.d("Testing", "Active Rides NULL");
                }


            });

        } else {
            // TODO: Handle driver
        }
    }
}
