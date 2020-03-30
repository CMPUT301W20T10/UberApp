package com.cmput301w20t10.uberapp.models;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
 * class rideRequestHolder is code from Stack overflow
 * URL of question: https://stackoverflow.com/questions/12522348
 * Asked by: Ethan Allen, https://stackoverflow.com/users/546509/ethan-allen
 * Answered by: Leonardo Cardoso, https://stackoverflow.com/users/1255990/leonardo-cardoso
 */
public class rideRequestHolder {
    private LinearLayout textViewWrap;
    private TextView username;
    private TextView distance;
    private TextView offer;
    private TextView firstName;
    private TextView lastName;
    private TextView startDest;
    private TextView endDest;
    private TextView startEndDist;
    private ImageView profilePic;
    private Button acceptButton;

    public rideRequestHolder(LinearLayout textViewWrap, TextView username, TextView distance, TextView offer,
                             TextView firstName, TextView lastName, TextView startDest, TextView endDest, TextView startEndDist,
                             ImageView profilePic, Button acceptButton) {
        super();
        this.textViewWrap = textViewWrap;
        this.username = username;
        this.distance = distance;
        this.offer = offer;
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDest = startDest;
        this.endDest = endDest;
        this.startEndDist = startEndDist;
        this.profilePic = profilePic;
        this.acceptButton = acceptButton;
    }

    public TextView getUsername() {
        return username;
    }

    public void setUsername(TextView username) {
        this.username = username;
    }

    public TextView getDistance() {
        return distance;
    }

    public void setDistance(TextView distance) {
        this.distance = distance;
    }

    public TextView getOffer() {
        return offer;
    }

    public void setOffer(TextView offer) {
        this.offer = offer;
    }

    public TextView getFirstName() {
        return firstName;
    }

    public void setFirstName(TextView firstName) {
        this.firstName = firstName;
    }

    public TextView getLastName() {
        return lastName;
    }

    public void setLastName(TextView lastName) {
        this.lastName = lastName;
    }

    public TextView getStartDest() {
        return startDest;
    }

    public void setStartDest(TextView startDest) {
        this.startDest = startDest;
    }

    public TextView getEndDest() {
        return endDest;
    }

    public void setEndDest(TextView endDest) {
        this.endDest = endDest;
    }

    public TextView getStartEndDist() {
        return startEndDist;
    }

    public void setStartEndDist(TextView startEndDist) {
        this.startEndDist = startEndDist;
    }

    public ImageView getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(ImageView profilePic) {
        this.profilePic = profilePic;
    }

    public Button getAcceptButton() {
        return acceptButton;
    }

    public void setAcceptButton(Button acceptButton) {
        this.acceptButton = acceptButton;
    }

    public LinearLayout getTextViewWrap() {
        return textViewWrap;
    }

    public void setTextViewWrap(LinearLayout textViewWrap) {
        this.textViewWrap = textViewWrap;
    }
}
