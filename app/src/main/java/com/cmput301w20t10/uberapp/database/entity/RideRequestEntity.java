package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.maps.model.LatLng;

public class RideRequestEntity {
    public static final String FIELD_RIDE_REQUEST_REFERENCE = "rideRequestId";

    private DocumentReference rideRequestReference;
    private DocumentReference driverReference;
    private DocumentReference riderReference;
    private DocumentReference paymentReference;

    private LatLng startingPosition;
    private LatLng destination;
    private int state;
    private int fareOffer;

    public RideRequestEntity(Rider rider, Route route, int fareOffer) {
        this.rideRequestReference = rider.getRiderReference();
        startingPosition = route.getStartingPosition();
        destination = route.getDestination();
        this.state = 0;
        this.fareOffer = fareOffer;
    }

    public DocumentReference getRideRequestReference() {
        return rideRequestReference;
    }

    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public DocumentReference getPaymentReference() {
        return paymentReference;
    }

    public LatLng getStartingPosition() {
        return startingPosition;
    }

    public LatLng getDestination() {
        return destination;
    }

    public int getState() {
        return state;
    }

    public int getFareOffer() {
        return fareOffer;
    }

    public void setRideRequestReference(DocumentReference rideRequestReference) {
        this.rideRequestReference = rideRequestReference;
    }
}
