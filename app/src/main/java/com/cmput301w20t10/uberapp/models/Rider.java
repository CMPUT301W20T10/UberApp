package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

public class Rider extends User {
    public DocumentReference riderReferecnce;

    public Rider(String userName, String password, String email, String firstName, String lastName, String phoneNumber, float rating) {
        super(userName, password, email, firstName, lastName, phoneNumber, rating);
    }

    public Rider(String userName, String password, String email, String firstName, String lastName, String phoneNumber, String image) {
        super(userName, password, email, firstName, lastName, phoneNumber, image);
    }

    public Rider(RiderEntity riderEntity, UserEntity userEntity) {
        super(userEntity);
        this.riderReferecnce = riderEntity.getRiderReference();
    }
}
