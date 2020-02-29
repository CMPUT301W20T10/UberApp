package com.cmput301w20t10.uberapp.database.dao;

import com.google.firebase.firestore.DocumentReference;

public interface RideRequestDAO {
    public RideRequestDAO createRideRequest(DocumentReference riderReference, int fareOffer);
}
