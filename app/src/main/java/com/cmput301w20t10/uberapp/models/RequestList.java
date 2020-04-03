package com.cmput301w20t10.uberapp.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
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
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cmput301w20t10.uberapp.R;
import com.google.android.gms.maps.model.LatLng;

public class RequestList extends ArrayAdapter<RideRequestListContent> {

    private ArrayList<RideRequestListContent> rideRequests;
    private Context context;
    private boolean addressesEqual;
    private String addressLine;
    private String addressKnownName;


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
        rideRequestHolder holder;

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
            acceptButton.setVisibility(View.VISIBLE);

            holder = new rideRequestHolder(textViewWrap, username, distance, offer, firstName, lastName,
                    startDest, endDest, startEndDist ,profilePicture, acceptButton);
            holder.setTextViewWrap(textViewWrap);
        } else {
            holder = (rideRequestHolder) view.getTag();
        }

        holder.getTextViewWrap().setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, rideRequest.getCurrentHeight()));

        getAddress(rideRequest.getStartDest().latitude, rideRequest.getStartDest().longitude);
        if (addressesEqual) {
            holder.getStartDest().setText(addressLine);
        } else {
            holder.getStartDest().setText(addressKnownName + ", "  + addressLine);
        }

        getAddress(rideRequest.getEndDest().latitude, rideRequest.getEndDest().longitude);
        if (addressesEqual) {
            holder.getEndDest().setText(addressLine);
        } else {
            holder.getEndDest().setText(addressKnownName + ", "  + addressLine);
        }

        holder.getUsername().setText(rideRequest.getUsername());
        holder.getDistance().setText(String.format("%.2f", rideRequest.getDistance()) + "km away");

        double offerDec = ((double)rideRequest.getOffer()) / 100;
        holder.getOffer().setText("Offer: $" + String.format("%.2f", offerDec));
        holder.getFirstName().setText(rideRequest.getFirstName());
        holder.getLastName().setText(rideRequest.getLastName());

        LatLng startDest = rideRequest.getStartDest();
        LatLng endDest = rideRequest.getEndDest();
        float[] startEndDist = new float[1];
        Location.distanceBetween(startDest.latitude,startDest.longitude,
                endDest.latitude,endDest.longitude, startEndDist);
        holder.getStartEndDist().setText(String.format("%.2fkm", startEndDist[0]/1000));

        if (rideRequest.getImageURL() != "") {
            Glide.with(view)
                    .load(rideRequest.getImageURL())
//                    .placeholder(imageView.getDrawable())
//                    .apply(RequestOptions.circleCropTransform())
                    .into(holder.getProfilePic());
        } else {
            holder.getProfilePic().setImageResource(R.drawable.ic_user_24dp);
        }

        view.setTag(holder);

        rideRequest.setHolder(holder);

        return view;
    }
    public void getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        addressesEqual = true;
        try {
            Address address = geocoder.getFromLocation(latitude, longitude, 1).get(0);
            addressLine = address.getAddressLine(0);
            addressKnownName = address.getFeatureName();
            String[] addressLineArr = addressLine.split("[\\s,]");
            String[] knownNameArr = addressKnownName.split("[\\s,]");
            for (int i=0; i < knownNameArr.length; i++) {
                if (!knownNameArr[i].equals(addressLineArr[i])) {
                    addressesEqual = false;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IndexOutOfBoundsException e) {
            Log.e("Index Error: ", "IndexOutOfBoundsException");
        }
    }
}
