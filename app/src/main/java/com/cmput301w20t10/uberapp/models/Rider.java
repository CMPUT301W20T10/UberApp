package com.cmput301w20t10.uberapp.models;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

import static android.content.ContentValues.TAG;

public class Rider extends User {
    private DocumentReference riderReference;
    private List<DocumentReference> paymentList;
    private List<DocumentReference> rideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int balance;

    public Rider(String userName, String password, String email, String firstName, String lastName, String phoneNumber, String image) {
        super(userName, password, email, firstName, lastName, phoneNumber, image);
    }

    public Rider(RiderEntity riderEntity, UserEntity userEntity) {
        super(userEntity);
        this.riderReference = riderEntity.getRiderReference();
        this.paymentList = riderEntity.getPaymentList();
        this.rideRequestList = riderEntity.getRideRequestList();
        this.activeRideRequestList = riderEntity.getActiveRideRequestList();
        this.balance = riderEntity.getBalance();
    }

    /**
     * Note: Immediately save after using
     *
     * @param rideRequest
     */
    public void addActiveRequest(RideRequest rideRequest) {
        activeRideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
    }

    /**
     * Immediately save after using
     *
     * @param rideRequest
     */
    public void deactivateRideRequest(RideRequest rideRequest) {
        activeRideRequestList.remove(rideRequest.getRideRequestReference());
        rideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
        addDirtyField(Field.RIDE_REQUEST_LIST);
    }

    // region getters and setters

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        addDirtyField(Field.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }

    public List<DocumentReference> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<DocumentReference> paymentList) {
        addDirtyField(Field.PAYMENT_LIST);
        this.paymentList = paymentList;
    }

    public List<DocumentReference> getRideRequestList() {
        return rideRequestList;
    }

    public void setRideRequestList(List<DocumentReference> rideRequestList) {
        addDirtyField(Field.RIDE_REQUEST_LIST);
        this.rideRequestList = rideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }

    public void setActiveRideRequestList(List<DocumentReference> activeRideRequestList) {
        addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
        this.activeRideRequestList = activeRideRequestList;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        addDirtyField(Field.BALANCE);
        this.balance = balance;
    }
    // endregion getters and setters
}
