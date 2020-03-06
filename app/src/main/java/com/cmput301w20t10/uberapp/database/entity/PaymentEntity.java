package com.cmput301w20t10.uberapp.database.entity;

import com.google.firebase.firestore.DocumentReference;

/**
 * Entity representation for Payment model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class PaymentEntity {
    public DocumentReference riderId;
    public DocumentReference driverId;
    public int value;
}
