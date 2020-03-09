package com.cmput301w20t10.uberapp.models;

import com.google.firebase.firestore.DocumentReference;

public class RideRequest {
    private DocumentReference rideId;
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

}
