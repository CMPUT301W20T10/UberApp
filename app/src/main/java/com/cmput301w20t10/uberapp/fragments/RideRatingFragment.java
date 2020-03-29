package com.cmput301w20t10.uberapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.cmput301w20t10.uberapp.R;

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

        upButton.setOnClickListener(v -> {
            if (!hasVoted) {
                hasVoted = true;
                counter++;
                rideDesc.setText(String.valueOf(counter));
            }
        });

        downButton.setOnClickListener(v -> {
            if (!hasVoted) {
                hasVoted = true;
                counter--;
                rideDesc.setText(String.valueOf(counter));
            }
        });

        // https://stackoverflow.com/questions/5901298/how-to-get-a-fragment-to-remove-itself-i-e-its-equivalent-of-finish
        closeButton.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        });
    }

}
