package com.cmput301w20t10.uberapp.models;

import com.google.firebase.firestore.DocumentReference;

public class Driver extends User {
    private DocumentReference driverId;
    private int rating2;

    @Deprecated
    public Driver(String userName,
                  String password,
                  String email,
                  String firstName,
                  String lastName,
                  String phoneNumber,
                  float rating) {
        super(userName, password, email, firstName, lastName, phoneNumber, rating);
    }

    public Driver(String userName,
                  String password,
                  String email,
                  String firstName,
                  String lastName,
                  String phoneNumber,
                  int rating) {
        super(userName, password, email, firstName, lastName, phoneNumber);
        this.rating2 = rating;
    }

    public int getRating2() {
        return rating2;
    }
}
