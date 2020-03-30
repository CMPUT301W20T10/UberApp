package com.cmput301w20t10.uberapp.activities;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.fragments.RideRatingFragment;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.cmput301w20t10.uberapp.util.HistoryAdapter;
import com.cmput301w20t10.uberapp.util.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

public class RideHistoryActivity extends BaseActivity {
    private ListView historyList;
    private SearchAdapter searchAdapter;
    private RideRequestDAO rrDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        setContentView(R.layout.activity_ride_history);

        historyList = findViewById(R.id.historyList);

        rrDAO = DatabaseManager.getInstance().getRideRequestDAO();

        populateHistory();
    }

    private void populateHistory() {
        User user = Application.getInstance().getCurrentUser();
        if (user instanceof Rider) {
            Rider rider = (Rider) user;
            Log.d("Testing", "Get ride history of rider: " + rider.getUsername());

            MutableLiveData<List<RideRequest>> liveHistory = rrDAO.getRideHistory(rider);
            liveHistory.observe(this, list -> {
                if (list != null) {
                    Log.d("Testing", "Ride history length: " + String.valueOf(list.size()));
                    HistoryAdapter adapter = new HistoryAdapter(this, list);
                    historyList.setAdapter(adapter);

                    historyList.setOnItemClickListener((adapterView, view, i, l) -> {
                        Application.getInstance().setSelectedHistoryRequest(list.get(i));

                        FragmentManager fragManager = getSupportFragmentManager();
                        FragmentTransaction fragTransaction = fragManager.beginTransaction();
                        RideRatingFragment rateFrag = new RideRatingFragment();
                        fragTransaction.add(R.id.rating_container, rateFrag);
                        fragTransaction.commit();
                    });
                }
            });

        } else {
            // TODO: Handle driver
        }
    }
}
