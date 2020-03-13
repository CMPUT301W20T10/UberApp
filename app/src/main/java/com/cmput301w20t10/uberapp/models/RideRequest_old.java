package com.cmput301w20t10.uberapp.models;


import android.widget.ImageView;

import com.google.firebase.firestore.DocumentReference;

public class RideRequest_old implements Comparable<RideRequest_old> {
    private String username;
    private Float distance;
    private Float offer;
    private String firstName;
    private String lastName;
    private ImageView profilePic;
    private DocumentReference userReference;

    private int collapsedHeight, currentHeight, expandedHeight;
    private boolean isOpen;
    private rideRequestHolder holder;

    public RideRequest_old(String username, Float distance, Float offer, DocumentReference userReference, String firstName, String lastName,
                           int collapsedHeight, int currentHeight, int expandedHeight) {
        this.username = username;
        this.distance = distance;
        this.offer = offer;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userReference = userReference;
        this.collapsedHeight = collapsedHeight;
        this.currentHeight = currentHeight;
        this.expandedHeight = expandedHeight;
        this.isOpen = false;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Float getDistance() {
        return distance;
    }

    public void setDistance(Float distance) {
        this.distance = distance;
    }

    public Float getOffer() {
        return offer;
    }

    public void setOffer(Float offer) {
        this.offer = offer;
    }

    public ImageView getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(ImageView profilePic) {
        this.profilePic = profilePic;
    }

    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        this.userReference = userReference;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCollapsedHeight() {
        return collapsedHeight;
    }

    public void setCollapsedHeight(int collapsedHeight) {
        this.collapsedHeight = collapsedHeight;
    }

    public int getCurrentHeight() {
        return currentHeight;
    }

    public void setCurrentHeight(int currentHeight) {
        this.currentHeight = currentHeight;
    }

    public int getExpandedHeight() {
        return expandedHeight;
    }

    public void setExpandedHeight(int expandedHeight) {
        this.expandedHeight = expandedHeight;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public rideRequestHolder getHolder() {
        return holder;
    }

    public void setHolder(rideRequestHolder holder) {
        this.holder = holder;
    }

    public int compareTo(RideRequest_old rideRequest) {
        return distance.compareTo(rideRequest.distance);
    }
}
