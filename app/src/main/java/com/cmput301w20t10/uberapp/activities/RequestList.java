package com.cmput301w20t10.uberapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301w20t10.uberapp.R;

public class RequestList extends ArrayAdapter<RideRequest> {

    private ArrayList<RideRequest> rideRequests;
    private Context context;

    public RequestList(Context context, ArrayList<RideRequest> rideRequests) {
        super(context,0,rideRequests);
        this.rideRequests = rideRequests;
        this.context = context;
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null){
            view = LayoutInflater.from(context).inflate(R.layout.ride_request_content,parent,false);
        }

        RideRequest rideRequest = rideRequests.get(position);

        TextView username = view.findViewById(R.id.request_username);
        TextView distance = view.findViewById(R.id.request_distance);
        TextView offer = view.findViewById(R.id.request_offer);

        username.setText(rideRequest.getUsername());
        distance.setText(String.format("%.2f", rideRequest.getDistance()) +"km away");
        offer.setText("Offer: $" + String.format("%.2f", rideRequest.getOffer()));

        return view;
    }
}
