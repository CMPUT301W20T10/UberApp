package com.cmput301w20t10.uberapp.models;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentReference;
import com.google.gson.annotations.SerializedName;

public class RideRequestListContent implements Comparable<RideRequestListContent> {
//    @SerializedName("1")
    private String username;
//    @SerializedName("2")
    private Float distance;
//    @SerializedName("3")
    private int offer;
//    @SerializedName("4")
    private String firstName;
//    @SerializedName("5")
    private String lastName;
//    @SerializedName("6")
    private LatLng startDest;
//    @SerializedName("7")
    private LatLng endDest;
//    @SerializedName("8")
    private String imageURL;
//    @SerializedName("9")
    private DocumentReference rideRequestReference;
//    @SerializedName("10")
    private DocumentReference unpairedReference;
//    @SerializedName("11")
    private int collapsedHeight;
//    @SerializedName("12")
    private int currentHeight;
//    @SerializedName("13")
    private int expandedHeight;
//    @SerializedName("14")
    private boolean isOpen;
//    @SerializedName("15")
    private rideRequestHolder holder;


    public RideRequestListContent(String username, Float distance, int offer, String imageURL,
                                  String firstName, String lastName, LatLng startDest, LatLng endDest,
                                  DocumentReference rideRequestReference, DocumentReference unpairedReference,
                                  int collapsedHeight, int currentHeight, int expandedHeight) {
        this.username = username;
        this.distance = distance;
        this.offer = offer;
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDest = startDest;
        this.endDest = endDest;
        this.imageURL = imageURL;
        this.rideRequestReference = rideRequestReference;
        this.unpairedReference = unpairedReference;
        this.collapsedHeight = collapsedHeight;
        this.currentHeight = currentHeight;
        this.expandedHeight = expandedHeight;
        this.isOpen = false;
    }

    public RideRequestListContent(String username, Float distance, int offer, String imageURL,
                                  String firstName, String lastName, LatLng startDest, LatLng endDest) {
        this.username = username;
        this.distance = distance;
        this.offer = offer;
        this.firstName = firstName;
        this.lastName = lastName;
        this.startDest = startDest;
        this.endDest = endDest;
        this.imageURL = imageURL;
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

    public int getOffer() {
        return offer;
    }

    public void setOffer(int offer) {
        this.offer = offer;
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

    public LatLng getStartDest() {
        return startDest;
    }

    public void setStartDest(LatLng startDest) {
        this.startDest = startDest;
    }

    public LatLng getEndDest() {
        return endDest;
    }

    public void setEndDest(LatLng endDest) {
        this.endDest = endDest;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public DocumentReference getRideRequestReference() {
        return rideRequestReference;
    }

    public void setRideRequestReference(DocumentReference rideRequestReference) {
        this.rideRequestReference = rideRequestReference;
    }

    public DocumentReference getUnpairedReference() {
        return unpairedReference;
    }

    public void setUnpairedReference(DocumentReference unpairedReference) {
        this.unpairedReference = unpairedReference;
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

    public void setExpandedHeight(int expandedHeight) { this.expandedHeight = expandedHeight; }

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

    public int compareTo(RideRequestListContent rideRequest) {
        return distance.compareTo(rideRequest.distance);
    }


}
