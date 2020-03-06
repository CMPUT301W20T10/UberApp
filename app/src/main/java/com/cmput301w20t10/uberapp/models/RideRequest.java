package com.cmput301w20t10.uberapp.models;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.google.firebase.firestore.DocumentReference;

import static android.content.ContentValues.TAG;

public class RideRequest {
    private DocumentReference driverReference;
    private DocumentReference riderReference;
    private DocumentReference paymentReference;

    private DocumentReference rideRequestReference;
    private Route route;
    private State state;
    private int fareOffer;

    public enum State {
        Active,
        DriverFound,
        RiderAccepted,
        RideCompleted,
        TransactionFinished,
        Cancelled
    }

    public RideRequest(RideRequestEntity entity) {
        this.driverReference = entity.getDriverReference();
        this.riderReference = entity.getRiderReference();
        this.paymentReference = entity.getPaymentReference();
        this.rideRequestReference = entity.getRideRequestReference();
        this.route = new Route(entity.getStartingPosition(), entity.getDestination());
        this.state = State.values()[entity.getState()];
        this.fareOffer = entity.getFareOffer();
    }

    public DocumentReference getRideRequestReference() {
        return rideRequestReference;
    }

    public void setRideRequestReference(DocumentReference rideRequestReference) {
        this.rideRequestReference = rideRequestReference;
    }

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        this.riderReference = riderReference;
    }

}
