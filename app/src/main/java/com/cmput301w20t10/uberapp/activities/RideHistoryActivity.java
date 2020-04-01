package com.cmput301w20t10.uberapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.fragments.RideRatingFragment;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.cmput301w20t10.uberapp.util.HistoryAdapter;

import java.util.ArrayList;
import java.util.List;

import static android.net.wifi.rtt.CivicLocationKeys.LOC;

/**
 * @author Alexander Laevens
 */
public class RideHistoryActivity extends BaseActivity {
    private final static String LOC = "Tomate: RideHistoryActivity: ";

    private ListView historyListView;
    private RadioGroup toggle;
    private boolean displayActives = true;
    private volatile List<RideRequest> historyList;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPref = new SharedPref(this);
        if (sharedPref.loadNightModeState() == true) {
            setTheme(R.style.DarkTheme);
        } else { setTheme(R.style.AppTheme); }

        setContentView(R.layout.activity_ride_history);

        // setting up the list view and its adapter
        historyListView = findViewById(R.id.historyList);
        historyList = new ArrayList<>();
        adapter = new HistoryAdapter(this,
                Glide.with(this),
                historyList,
                Application.getInstance().getCurrentUser(),
                displayActives);
        historyListView.setAdapter(adapter);
        populateHistory(displayActives); // pull the initial list

        // set up update button listener to refresh list
        Button updateButton = findViewById(R.id.history_update_button);
        updateButton.setOnClickListener(v -> {
            Log.d("Tomate", LOC + "onCreate: ");
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

        // https://stackoverflow.com/questions/31555545/android-which-method-is-called-when-fragment-is-pop-out-from-backstack
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment frag = getSupportFragmentManager().findFragmentById(R.id.rating_container);
            if (frag == null) {
                Log.d("Testing", "Repopulate");
                populateHistory(displayActives);
            }
        });


    }

    /**
     * Refreshes the ListView content once the historyList has been populated
     */
    private void updateView(boolean active) {
        User user = Application.getInstance().getCurrentUser();

        if (user instanceof Rider) { // only riders can rate drivers
            historyListView.setOnItemClickListener((adapterView, view, i, l) -> {
                // store selected ride request to pass to fragment
                Application.getInstance().setSelectedHistoryRequest(historyList.get(i));

                // start rating fragment
                FragmentManager fragManager = getSupportFragmentManager();
                FragmentTransaction fragTransaction = fragManager.beginTransaction();
                RideRatingFragment rateFrag = new RideRatingFragment();
                fragTransaction.add(R.id.rating_container, rateFrag);
                fragTransaction.addToBackStack(null);
                fragTransaction.commit();
            });
        }
    }

    /**
     * Populates historyList with ride requests
     *
     * @param active
     *      If true: lists active requests, If false: lists finished requests
     */
    private void populateHistory(boolean active) {
        /*
         * Fix for list not updating:
         * This is where things got wonky. History list and the list in the adapter are pointing
         * to the same reference but invoking historyList = new ArrayList<>() creates a new object.
         * The list in the adapter remains to be referring to the thing which historyList lost.
         * clear() would remove the items in place. adapter.notifyDataSetChanged() would update the list.
         * Later in the code, I would set history list in adapter even if they were still referring to the
         * same object because we don't know where bugs may sprung up.
         */
        historyList.clear();
        adapter.notifyDataSetChanged(); // clear first so if there isn't any rides the screen is clear

        Application application = Application.getInstance();
        application.getLatestUserData(this)
                .observe(this, user -> {
                    if (user != null) {
                        populateHistoryHelper(user, active);
                    } else {
                        Log.e("Tomate", LOC + "populateHistory: User is null");
                    }
                });
    }

    /**
     * Reduce nesting in populateHistory
     * @param user
     * @param active
     */
    private void populateHistoryHelper(User user, boolean active) {
        // you have to fetch the latest updates, the user object is outdated here
        if (user == null) {
            Log.e("Tomate", LOC + "populateHistoryHelper: User is null");
            return;
        }

        MutableLiveData<List<RideRequest>> liveRides;
        RideRequestDAO rrDAO = DatabaseManager.getInstance().getRideRequestDAO();

        if (user instanceof Rider) {
            Rider rider = (Rider) user;
            if (active) {
                liveRides = rrDAO.getAllActiveRideRequest(rider);
            } else {
                liveRides = rrDAO.getRideHistory(rider);
            }
        } else {
            Driver driver = (Driver) user;
            if (active) {
                liveRides = rrDAO.getAllActiveRideRequest(driver);
            } else {
                liveRides = rrDAO.getRideHistory(driver);
            }
        }

        liveRides.observe(this, list -> {
            if (list != null) {
                historyList.clear();
                historyList.addAll(list);
                adapter.setData(list);
                adapter.notifyDataSetChanged();
            } else {
                Log.d("Testing", "Past Rides NULL");
            }
            updateView(active);
        });
    }
}
