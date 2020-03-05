package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Driver extends User {
    private DocumentReference driverReference;
    private List<DocumentReference> paymentReferenceList;
    private List<DocumentReference> finishedRideRequestList;
    private List<DocumentReference> activeRideRequestList;
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
                  int rating,
                  String image) {
        super(userName, password, email, firstName, lastName, phoneNumber, image);
        this.rating2 = rating;
    }

    public Driver(DriverEntity driverEntity, UserEntity userEntity) {
        super(userEntity);
        this.driverReference = driverEntity.getDriverReference();
        this.rating2 = driverEntity.getRating();
    }

    public int getRating2() {
        return rating2;
    }
}
