package com.cmput301w20t10.uberapp.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.cmput301w20t10.uberapp.R;
import com.google.android.gms.maps.model.LatLng;

public class RequestList extends ArrayAdapter<RideRequestListContent> {

    private ArrayList<RideRequestListContent> rideRequests;
    private Context context;

    public RequestList(Context context, ArrayList<RideRequestListContent> rideRequests) {
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

        RideRequestListContent rideRequest = rideRequests.get(position);

        if (view == null){
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = vi.inflate(R.layout.ride_request_content,null);

            LinearLayout textViewWrap = view.findViewById(R.id.text_wrap);
            TextView username = view.findViewById(R.id.request_username);
            TextView distance = view.findViewById(R.id.request_distance);
            TextView offer = view.findViewById(R.id.request_offer);
            TextView firstName = view.findViewById(R.id.request_first_name);
            TextView lastName = view.findViewById(R.id.request_last_name);
            TextView startDest = view.findViewById(R.id.request_start_dest);
            TextView endDest = view.findViewById(R.id.request_end_dest);
            TextView startEndDist = view.findViewById(R.id.start_end_distance);
            ImageView profilePicture = view.findViewById(R.id.profile_picture);
            Button acceptButton = view.findViewById(R.id.accept_request_button);

            holder = new rideRequestHolder(textViewWrap, username, distance, offer, firstName, lastName,
                    startDest, endDest, startEndDist ,profilePicture, acceptButton);
            holder.setTextViewWrap(textViewWrap);
        } else {
            holder = (rideRequestHolder) view.getTag();
        }

        holder.getTextViewWrap().setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, rideRequest.getCurrentHeight()));

        Geocoder geocoder;
        Address startAddress = null;
        Address endAddress = null;
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            startAddress = geocoder.getFromLocation(rideRequest.getStartDest().latitude, rideRequest.getStartDest().longitude, 1)
                    .get(0);
            endAddress = geocoder.getFromLocation(rideRequest.getEndDest().latitude, rideRequest.getEndDest().longitude, 1)
                    .get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.getUsername().setText(rideRequest.getUsername());
        holder.getDistance().setText(String.format("%.2f", rideRequest.getDistance()) + "km away");
        holder.getOffer().setText("Offer: $" + String.format("%.2f", rideRequest.getOffer()));
        holder.getFirstName().setText(rideRequest.getFirstName());
        holder.getLastName().setText(rideRequest.getLastName());

        LatLng startDest = rideRequest.getStartDest();
        LatLng endDest = rideRequest.getEndDest();
        float[] startEndDist = new float[1];
        Location.distanceBetween(startDest.latitude,startDest.longitude,
                endDest.latitude,endDest.longitude, startEndDist);
        holder.getStartEndDist().setText(String.format("%.2fkm", startEndDist[0]/1000));

        String startAddressLine = startAddress.getAddressLine(0);
        String startKnownName = startAddress.getFeatureName();
        String[] startAddressLineArr = startAddressLine.split("[\\s,]");
        String[] startKnownNameArr =startKnownName.split("[\\s,]");
        boolean startsEqual = true;
        for (int i=0; i < startKnownNameArr.length; i++) {
            if (!startKnownNameArr[i].equals(startAddressLineArr[i])) {
                startsEqual = false;
                break;
            }
        }
        if (startsEqual) {
            holder.getStartDest().setText(startAddressLine);
        } else {
            holder.getStartDest().setText(startKnownName + ", "  + startAddressLine);
        }

        String endAddressLine = endAddress.getAddressLine(0);
        String endKnownName = endAddress.getFeatureName();
        String[] endAddressLineArr = endAddressLine.split("[\\s,]");
        String[] endKnownNameArr =endKnownName.split("[\\s,]");
        boolean endsEqual = true;
        for (int i=0; i < endKnownNameArr.length; i++) {
            if (!endKnownNameArr[i].equals(endAddressLineArr[i])) {
                endsEqual = false;
                break;
            }
        }
        if (endsEqual) {
            holder.getEndDest().setText(endAddressLine);
        } else {
            holder.getEndDest().setText(endKnownName + ", "  + endAddressLine);
        }

        Glide.with(view)
                .load(rideRequest.getImageURL())
                .into(holder.getProfilePic());

        view.setTag(holder);

        rideRequest.setHolder(holder);

        return view;
    }
}
