package com.cmput301w20t10.uberapp.database.entity;

import com.google.firebase.firestore.DocumentReference;

public class PaymentEntity {
    public DocumentReference riderId;
    public DocumentReference driverId;
    public int value;
}
