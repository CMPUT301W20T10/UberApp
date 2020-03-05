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

    public LatLng getStartingPosition() {
        if (markerList.size() > 0) {
            return markerList.get(0).getPosition();
        } else {
            return  null;
        }
    }

    public LatLng getDestination() {
        if (markerList.size() > 1) {
            return markerList.get(1).getPosition();
        } else {
            return null;
        }
    }

    // todo: add function that converts into something that the database understands
}
