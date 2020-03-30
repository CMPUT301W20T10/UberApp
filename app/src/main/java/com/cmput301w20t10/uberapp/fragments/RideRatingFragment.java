package com.cmput301w20t10.uberapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.DriverDAO;
import com.cmput301w20t10.uberapp.database.RideRequestDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;

import java.util.List;

/**
 * @author Alexander Laevens
 */
public class RideRatingFragment extends Fragment {
    int counter = 0;
    boolean hasVoted = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rider_rating_fragment_content, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView upButton = view.findViewById(R.id.rateUpButton);
        ImageView downButton = view.findViewById(R.id.rateDownButton);
        ImageView closeButton = view.findViewById(R.id.ratingFragmentCloseButton);
        TextView rideDesc = view.findViewById(R.id.ratingFragmentRideDescription);

        Log.d("Testing", "Fragment view created");

        User user = Application.getInstance().getCurrentUser();
        Rider rider;
        if (user instanceof Rider) {
            rider = (Rider) user;
            Log.d("Testing", "Look for active rides of rider: "+rider.getUsername());
            DatabaseManager db = DatabaseManager.getInstance();
            RideRequestDAO rideRequestDAO = db.getRideRequestDAO();
            DriverDAO driverDAO = db.getDriverDAO();

            MutableLiveData<List<RideRequest>> liveData = rideRequestDAO.getAllActiveRideRequest(rider);
            liveData.observe(this, rideList -> {
                if (rideList == null) {
                    Log.d("Testing", "Ride List is null :(");
                } else {
                    Log.d("Testing", "Ride list contains " + String.valueOf(rideList.size()) + " Rides");
                }
                if (rideList != null && rideList.size() >= 1) {
                    RideRequest rideRequest = rideList.get(0);

                    MutableLiveData<Driver> liveDriver = driverDAO.getDriverFromDriverReference(rideRequest.getDriverReference());
                    liveDriver.observe(this, driver -> {
                        TextView driverRatingView = view.findViewById(R.id.sPosRate);
                        TextView driverUnameView = view.findViewById(R.id.uName);
                        driverUnameView.setText(driver.getUsername());
                        driverRatingView.setText(String.valueOf(driver.getRating()));

                        upButton.setOnClickListener(v -> {
                            if (!hasVoted) {
                                hasVoted = true;
                                driverDAO.rateDriver(driver, 1);
                            }
                        });

                        downButton.setOnClickListener(v -> {
                            if (!hasVoted) {
                                hasVoted = true;
                                driverDAO.rateDriver(driver, -1);
                            }
                        });
                    });


                } else if (rideList != null && rideList.size() == 0) {
                    Log.d("Testing", "No active rides");
                    this.close();
                } else if (rideList == null) {
                    // no internet connection
                    this.close();
                }
            });
        } else {
            Log.d("Testing", "User not rider");
            this.close();
        }

        // https://stackoverflow.com/questions/5901298/how-to-get-a-fragment-to-remove-itself-i-e-its-equivalent-of-finish
        closeButton.setOnClickListener(v -> {
            this.close();
        });
    }

    private void close() {
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
