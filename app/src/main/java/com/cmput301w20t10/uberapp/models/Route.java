package com.cmput301w20t10.uberapp.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;

public class Route {
    private ArrayList<Marker> markerList = new ArrayList<>();
    private LatLng startingPosition;
    private LatLng destination;

    public Route() {
        destination = new LatLng(0,0);
        startingPosition = new LatLng(0,0);
    }

    public Route(LatLng startingPosition, LatLng destination) {
        this.startingPosition = startingPosition;
        this.destination = destination;
    }

    public Route(GeoPoint startingPosition, GeoPoint destination) {
        if (startingPosition != null) {
            this.startingPosition = new LatLng(startingPosition.getLatitude(), startingPosition.getLongitude());
        }

        if (startingPosition != null) {
            this.destination = new LatLng(destination.getLatitude(), destination.getLongitude());
        }
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
            return startingPosition.toString();
        }
    }

    public String getDestinationString() {
        if (markerList.size() >= 2 && markerList.get(1) != null) {
            return markerList.get(1).getPosition().toString().replace("lat/lng: ", "");
        } else {
            return destination.toString();
        }
    }

    public LatLng getStartingPosition() {
        if (markerList.size() > 0) {
            return markerList.get(0).getPosition();
        } else {
            return startingPosition;
        }
    }

    public LatLng getDestination() {
        if (markerList.size() > 1) {
            return markerList.get(1).getPosition();
        } else {
            return destination;
        }
    }

    // todo: add function that converts into something that the database understands
}
