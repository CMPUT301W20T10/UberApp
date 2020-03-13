package com.cmput301w20t10.uberapp.activities;

import com.google.android.gms.maps.model.LatLng;

public class RideRequest {
    private String username;
    private Float distance;
    private Float offer;

    RideRequest(String username, Float distance, Float offer) {
        this.username = username;
        this.distance = distance;
        this.offer = offer;
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
}
