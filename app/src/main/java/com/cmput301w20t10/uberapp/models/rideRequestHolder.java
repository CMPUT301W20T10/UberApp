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
    private TextView usernameText;
    private TextView distanceText;
    private TextView offerText;
    private TextView firstNameText;
    private TextView lastNameText;
    private TextView startDestText;
    private TextView endDestText;
    private TextView startEndDistText;
    private ImageView profilePicImage;
    private Button acceptButton;

    public rideRequestHolder(LinearLayout textViewWrap, TextView usernameText, TextView distanceText, TextView offerText,
                             TextView firstNameText, TextView lastNameText, TextView startDestText, TextView endDestText, TextView startEndDistText,
                             ImageView profilePicImage, Button acceptButton) {
        super();
        this.textViewWrap = textViewWrap;
        this.usernameText = usernameText;
        this.distanceText = distanceText;
        this.offerText = offerText;
        this.firstNameText = firstNameText;
        this.lastNameText = lastNameText;
        this.startDestText = startDestText;
        this.endDestText = endDestText;
        this.startEndDistText = startEndDistText;
        this.profilePicImage = profilePicImage;
        this.acceptButton = acceptButton;
    }

    public TextView getUsername() {
        return usernameText;
    }

    public void setUsername(TextView usernameText) {
        this.usernameText = usernameText;
    }

    public TextView getDistance() {
        return distanceText;
    }

    public void setDistance(TextView distanceText) {
        this.distanceText = distanceText;
    }

    public TextView getOffer() {
        return offerText;
    }

    public void setOffer(TextView offerText) {
        this.offerText = offerText;
    }

    public TextView getFirstName() {
        return firstNameText;
    }

    public void setFirstName(TextView firstNameText) {
        this.firstNameText = firstNameText;
    }

    public TextView getLastName() {
        return lastNameText;
    }

    public void setLastName(TextView lastNameText) {
        this.lastNameText = lastNameText;
    }

    public TextView getStartDest() {
        return startDestText;
    }

    public void setStartDest(TextView startDestText) {
        this.startDestText = startDestText;
    }

    public TextView getEndDest() {
        return endDestText;
    }

    public void setEndDest(TextView endDestText) {
        this.endDestText = endDestText;
    }

    public TextView getStartEndDist() {
        return startEndDistText;
    }

    public void setStartEndDist(TextView startEndDistText) {
        this.startEndDistText = startEndDistText;
    }

    public ImageView getProfilePic() {
        return profilePicImage;
    }

    public void setProfilePic(ImageView profilePicImage) {
        this.profilePicImage = profilePicImage;
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
