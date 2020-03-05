package com.cmput301w20t10.uberapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;

public class Route {
    private ArrayList<Marker> markerList = new ArrayList<>();
    private ArrayList<LatLng> endPoints = new ArrayList<>();

    public Route() {}

    public Route(LatLng startingPosition, LatLng destination) {
        endPoints.add(startingPosition);
        endPoints.add(destination);
    }

    public void addLocation(Marker marker) {
        markerList.add(marker);
        if (markerList.size() > 2) {
            Marker oldMarker = markerList.remove(0);
            oldMarker.remove();
        }
    }

    public String getStartingPointString() {
        if (markerList.size() >= 1 && markerList.get(0) != null) {
            return markerList.get(0).getPosition().toString().replace("lat/lng: ", "");
        } else {
            return "";
        }
    }

    public String getDestinationString() {
        if (markerList.size() >= 2 && markerList.get(1) != null) {
            return markerList.get(1).getPosition().toString().replace("lat/lng: ", "");
        } else {
            return "";
        }
    }

    // todo: put validations here
    public LatLng getStartingPosition() {
        return markerList.get(0).getPosition();
    }

    public LatLng getDestination() {
        return markerList.get(1).getPosition();
    }

    // todo: add function that converts into something that the database understands
}
