package com.cmput301w20t10.uberapp.activities;

import android.widget.Button;
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
    private Button acceptButton;

    public rideRequestHolder(LinearLayout textViewWrap, TextView username, TextView distance, TextView offer, TextView firstName, TextView lastName, Button acceptButton) {
        super();
        this.textViewWrap = textViewWrap;
        this.username = username;
        this.distance = distance;
        this.offer = offer;
        this.firstName = firstName;
        this.lastName = lastName;
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
