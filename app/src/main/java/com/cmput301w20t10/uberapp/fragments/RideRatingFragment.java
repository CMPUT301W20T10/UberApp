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
import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

/**
 * @author Alexander Laevens
 */
public class RideRatingFragment extends Fragment {
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

        //DocumentReference dr = Application.getInstance().getCurrentRideDocument();

        RideRequest request = Application.getInstance().getSelectedHistoryRequest();
        Log.d("Testing", "Request with fare: " + String.valueOf(request.getFareOffer()));

        if (request != null) {
            DriverDAO driverDAO = DatabaseManager.getInstance().getDriverDAO();
            RideRequestDAO rideRequestDAO = DatabaseManager.getInstance().getRideRequestDAO();
            MutableLiveData<Driver> liveDriver = driverDAO.getDriverFromDriverReference(request.getDriverReference());

            liveDriver.observe(this, driver -> {
                if (driver != null) {
                    Log.d("Testing", "Driver: " + driver.getUsername());

                    TextView driverRatingView = view.findViewById(R.id.sPosRate);
                    TextView driverUnameView = view.findViewById(R.id.uName);
                    driverUnameView.setText(driver.getUsername());
                    driverRatingView.setText(String.valueOf(driver.getRating()));

//                    Log.d("Testing", "Rated Yet?: " + String.valueOf(request.isRated()));

                    if (!hasVoted) {
                        upButton.setOnClickListener(v -> {
                            if (!hasVoted) {
                                hasVoted = true;
                                driverDAO.rateDriver(driver, 1);
                                rideRequestDAO.rateRide(request);
                                close();
                            }
                        });

                        downButton.setOnClickListener(v -> {
                            if (!hasVoted) {
                                hasVoted = true;
                                driverDAO.rateDriver(driver, -1);
                                rideRequestDAO.rateRide(request);
                                close();
                            }
                        });
                    }
                } else {
                    Log.d("Testing", "No driver found on this request");
                    close();
                }
            });
        }

        closeButton.setOnClickListener(v -> {
            this.close();
        });
    }

    private void close() {
        // https://stackoverflow.com/questions/5901298/how-to-get-a-fragment-to-remove-itself-i-e-its-equivalent-of-finish
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
    }
}
