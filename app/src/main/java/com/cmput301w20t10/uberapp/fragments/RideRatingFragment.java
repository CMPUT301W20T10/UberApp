package com.cmput301w20t10.uberapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.activities.RideHistoryActivity;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author Alexander Laevens
 */
public class RideRatingFragment extends Fragment {
    boolean hasVoted = false;
    boolean modifiedRequest = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.rider_rating_fragment_content, container, false);
    }

    /**
     * Once the view has been created, grab the relevant data from the Database and
     * populate the UI elements.
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ImageView upButton = view.findViewById(R.id.rateUpButton);
        ImageView downButton = view.findViewById(R.id.rateDownButton);
        ImageView closeButton = view.findViewById(R.id.ratingFragmentCloseButton);

        // retrieve the passed in request
        RideRequest request = Application.getInstance().getSelectedHistoryRequest();

        if (request != null) {
            DriverDAO driverDAO = DatabaseManager.getInstance().getDriverDAO();
            RideRequestDAO rideRequestDAO = DatabaseManager.getInstance().getRideRequestDAO();

            // retrieve the driver from the request
            MutableLiveData<Driver> liveDriver = driverDAO.getDriverFromDriverReference(request.getDriverReference());
            liveDriver.observe(this, driver -> {
                if (driver != null) {
                    // Set the drivers username and rating fields
                    TextView driverRatingView = view.findViewById(R.id.sPosRate);
                    TextView driverUnameView = view.findViewById(R.id.uName);
                    driverUnameView.setText(driver.getUsername());
                    driverRatingView.setText(String.valueOf(driver.getRating()));

                    // set the drivers profile picture
                    if (driver.getImage() != "") {
                        CircleImageView profilePicture = view.findViewById(R.id.profile_image);
                        Glide.with(this)
                                .load(driver.getImage())
                                .apply(RequestOptions.circleCropTransform())
                                .into(profilePicture);
                    }

                    // prevent the user from rating multiple times on different instances of the fragment
                    if (request.getRating() != 0) {
                        hasVoted = true;
                    }

                    if (!hasVoted) {
                        // Listener waiting for thumbs up press
                        upButton.setOnClickListener(v -> {
                            if (!hasVoted) {
                                hasVoted = true; // prevent the user from re-tapping the rate button
                                modifiedRequest = true;

                                // update the rating on the request and the driver
                                driverDAO.rateDriver(driver, 1);
                                rideRequestDAO.rateRide(request, 1);
                                driverRatingView.setText(String.valueOf(driver.getRating())); // allow user to see their changes
                            } else {
                                warnCantVote();
                            }
                        });

                        // Listener waiting for thumbs down press
                        downButton.setOnClickListener(v -> {
                            if (!hasVoted) {
                                hasVoted = true; // prevent the user from re-tapping the rate button
                                modifiedRequest = true;

                                // update the rating on the request and the driver
                                driverDAO.rateDriver(driver, -1);
                                rideRequestDAO.rateRide(request, -1);
                                driverRatingView.setText(String.valueOf(driver.getRating()));  // allow user to see their changes
                            } else {
                                warnCantVote();
                            }
                        });
                    } else {
                        warnCantVote();
                    }
                } else {
                    Log.d("Testing", "No driver found on this request");
                    close();
                }
            });
        }

        // Listener waiting for the close button to be pressed
        closeButton.setOnClickListener(v -> {
            this.close();
        });
    }

    private void warnCantVote() {
        Toast toast = Toast.makeText(getContext(), "You can't vote on this request again", Toast.LENGTH_LONG);
        TextView textView = toast.getView().findViewById(android.R.id.message);
        textView.setGravity(Gravity.CENTER);
        toast.show();
    }

    /**
     * This will remove the fragment from RideHistoryActivity's backstack and close the fragment
     */
    private void close() {
        /*
         * Code from Stack Overflow used to close
         * URL of question: https://stackoverflow.com/questions/5901298/how-to-get-a-fragment-to-remove-itself-i-e-its-equivalent-of-finish
         * Asked by: PJL, https://stackoverflow.com/users/702191/pjl
         * Answered by: Manfred Moser, https://stackoverflow.com/users/136445/manfred-moser
         * URL of answer: https://stackoverflow.com/a/9387251
         */

        if (modifiedRequest) {
            RideHistoryActivity activity = ((RideHistoryActivity)getActivity());
            activity.populateHistory();
        }

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
        fragmentManager.popBackStack();
    }
}
