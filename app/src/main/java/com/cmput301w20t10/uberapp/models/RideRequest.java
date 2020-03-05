package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.google.firebase.firestore.DocumentReference;

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
        Cancelled,
        RideCompleted,
        TransactionFinished
    }

    public RideRequest(RideRequestEntity entity) {
        this.riderReference = entity.getRiderReference();
        this.route = new Route(entity.getStartingPosition(), entity.getDestination());
        this.state = State.values()[entity.getState()];
        this.fareOffer = entity.getFareOffer();
    }
}
