package com.cmput301w20t10.uberapp.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cmput301w20t10.uberapp.R;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.rideRequestHolder;


public class RequestList extends ArrayAdapter<RideRequest> {

    private ArrayList<RideRequest> rideRequests;
    private Context context;

    public RequestList(Context context, ArrayList<RideRequest> rideRequests) {
        super(context,0,rideRequests);
        this.rideRequests = rideRequests;
        this.context = context;
    }

    /*
     * Some code from Stack Overflow was used in getView()
     * URL of question: https://stackoverflow.com/questions/12522348
     * Asked by: Ethan Allen, https://stackoverflow.com/users/546509/ethan-allen
     * Answered by: Leonardo Cardoso, https://stackoverflow.com/users/1255990/leonardo-cardoso
     */
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        rideRequestHolder holder = null;

        RideRequest rideRequest = rideRequests.get(position);

        if (view == null){
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.ride_request_content,null);

            LinearLayout textViewWrap = (LinearLayout) view.findViewById(R.id.text_wrap);
            TextView username = view.findViewById(R.id.request_username);
            TextView distance = view.findViewById(R.id.request_distance);
            TextView offer = view.findViewById(R.id.request_offer);
            TextView firstName = view.findViewById(R.id.request_first_name);
            TextView lastName = view.findViewById(R.id.request_last_name);
            Button acceptButton = view.findViewById(R.id.accept_request_button);


            holder = new rideRequestHolder(textViewWrap, username, distance, offer, firstName, lastName, acceptButton);
            holder.setTextViewWrap(textViewWrap);
        } else {
            holder = (rideRequestHolder) view.getTag();
        }

        holder.getTextViewWrap().setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, rideRequest.getCurrentHeight()));

        holder.getUsername().setText(rideRequest.getUsername());
        holder.getDistance().setText(String.format("%.2f", rideRequest.getDistance()) +"km away");
        holder.getOffer().setText("Offer: $" + String.format("%.2f", rideRequest.getOffer()));
        holder.getFirstName().setText(rideRequest.getFirstName());
        holder.getLastName().setText(rideRequest.getLastName());

        view.setTag(holder);

        rideRequest.setHolder(holder);

        return view;
    }
//        username.setText(rideRequest.getUsername());
//        distance.setText(String.format("%.2f", rideRequest.getDistance()) +"km away");
//        offer.setText("Offer: $" + String.format("%.2f", rideRequest.getOffer()));
//
//        return view;
}
