package com.cmput301w20t10.uberapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.DocumentReference;

import java.math.BigDecimal;

public class NewRideFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.new_ride_fragment, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        EditText startEditText = view.findViewById(R.id.new_ride_start_dest);
        EditText endEditText = view.findViewById(R.id.new_ride_end_dest);
        EditText offerText = view.findViewById(R.id.new_ride_offer);
        Button confirmButton = view.findViewById(R.id.new_ride_confirm);
        ImageButton cancelButton = view.findViewById(R.id.new_ride_cancel);

        String startPos = getArguments().getString("StartPosition");
        String destination = getArguments().getString("Destination");
        int priceOffer = getArguments().getInt("offer");

        startEditText.setText(startPos);
        endEditText.setText(destination);
        offerText.setText(String.format("%d", priceOffer));

        confirmButton.setOnClickListener(v -> {
            double newOffer = Double.parseDouble(offerText.getText().toString());
            if (newOffer < priceOffer) {
                Toast toast = Toast.makeText(getContext(), String.format("Price offer cannot be lower than %d", priceOffer), Toast.LENGTH_LONG);
                TextView textView = toast.getView().findViewById(android.R.id.message);
                textView.setGravity(Gravity.CENTER);
                toast.show();
                return;
            }
            //get user
            DatabaseManager db = DatabaseManager.getInstance();
            RideRequestDAO dao = db.getRideRequestDAO();
            User user = Application.getInstance().getCurrentUser();
            //pass data
            if (user instanceof Rider){
                Log.d("Test", "if condition passed");
                Rider rider = (Rider) user;
                int offerCents = (int) newOffer*100;
                MutableLiveData<RideRequest> createdRequest = dao.createRideRequest(rider,Application.getInstance().getRoute(),offerCents,this);
                this.close();

            }
            else{
                Log.d("Test", "if condition did not pass");
            }

        });

        cancelButton.setOnClickListener(v -> {
            this.close();
        });
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
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
        fragmentManager.popBackStack();
    }
}
