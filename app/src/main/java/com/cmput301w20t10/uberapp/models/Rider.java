package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Rider extends User {
    private DocumentReference riderReference;
    private List<DocumentReference> paymentReferenceList;
    private List<DocumentReference> finishedRideRequestList;
    private List<DocumentReference> activeRideRequestList;

    public Rider(String userName, String password, String email, String firstName, String lastName, String phoneNumber, float rating) {
        super(userName, password, email, firstName, lastName, phoneNumber, rating);
    }

    public Rider(String userName, String password, String email, String firstName, String lastName, String phoneNumber, String image) {
        super(userName, password, email, firstName, lastName, phoneNumber, image);
    }

    public Rider(RiderEntity riderEntity, UserEntity userEntity) {
        super(userEntity);
        this.riderReference = riderEntity.getRiderReference();
        this.paymentReferenceList = riderEntity.getPaymentList();
        this.finishedRideRequestList = riderEntity.getRideRequestList();
        this.activeRideRequestList = riderEntity.getActiveRideRequestList();
    }

    public List<DocumentReference> getPaymentReferenceList() {
        return paymentReferenceList;
    }

    public List<DocumentReference> getFinishedRideRequestList() {
        return finishedRideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void addActiveRequest(RideRequest rideRequest) {

    }
}
