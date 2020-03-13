package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Rider extends User {
    private DocumentReference riderReference;
    private List<DocumentReference> transactionList;
    private List<DocumentReference> rideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int balance;

    public Rider(String userName, String password, String email, String firstName, String lastName, String phoneNumber, String image) {
        super(userName, password, email, firstName, lastName, phoneNumber, image);
    }

    public Rider(RiderEntity riderEntity, UserEntity userEntity) {
        super(userEntity);
        this.riderReference = riderEntity.getRiderReference();
        this.transactionList = riderEntity.getPaymentList();
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
        addDirtyField(EnumField.ACTIVE_RIDE_REQUEST_LIST);
    }

    /**
     * Immediately save after using
     *
     * @param rideRequest
     */
    public void deactivateRideRequest(RideRequest rideRequest) {
        activeRideRequestList.remove(rideRequest.getRideRequestReference());
        rideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(EnumField.ACTIVE_RIDE_REQUEST_LIST);
        addDirtyField(EnumField.RIDE_REQUEST_LIST);
    }

    // region getters and setters

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        addDirtyField(EnumField.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }

    public List<DocumentReference> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<DocumentReference> transactionList) {
        addDirtyField(EnumField.TRANSACTION_LIST);
        this.transactionList = transactionList;
    }

    public List<DocumentReference> getRideRequestList() {
        return rideRequestList;
    }

    public void setRideRequestList(List<DocumentReference> rideRequestList) {
        addDirtyField(EnumField.RIDE_REQUEST_LIST);
        this.rideRequestList = rideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }

    public void setActiveRideRequestList(List<DocumentReference> activeRideRequestList) {
        addDirtyField(EnumField.ACTIVE_RIDE_REQUEST_LIST);
        this.activeRideRequestList = activeRideRequestList;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        addDirtyField(EnumField.BALANCE);
        this.balance = balance;
    }
    // endregion getters and setters
}
