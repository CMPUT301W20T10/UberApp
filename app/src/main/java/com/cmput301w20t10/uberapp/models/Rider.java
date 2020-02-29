package com.cmput301w20t10.uberapp.models;

import com.google.firebase.firestore.DocumentReference;

public class Rider extends User {
    public DocumentReference riderReferecnce;

    public Rider(String userName, String password, String email, String firstName, String lastName, String phoneNumber, float rating) {
        super(userName, password, email, firstName, lastName, phoneNumber, rating);
    }

    public Rider(String userName, String password, String email, String firstName, String lastName, String phoneNumber) {
        super(userName, password, email, firstName, lastName, phoneNumber);
    }
}
