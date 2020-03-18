package com.cmput301w20t10.uberapp.models;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Rider extends User {
    private static final String LOC = "Rider: ";
    private DocumentReference riderReference;
    private List<DocumentReference> transactionList;
    private List<DocumentReference> finishedRideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private float balance;

    public Rider(DocumentReference userReference,
                 DocumentReference riderReference,
                 List<DocumentReference> transactionList,
                 List<DocumentReference> finishedRideRequestList,
                 List<DocumentReference> activeRideRequestList,
                 String username,
                 String password,
                 String email,
                 String firstName,
                 String lastName,
                 String phoneNumber,
                 String image,
                 float balance) {
        super(userReference, username, password, email, firstName, lastName, phoneNumber, image);
        this.riderReference = riderReference;
        this.transactionList = transactionList;
        this.finishedRideRequestList = finishedRideRequestList;
        this.activeRideRequestList = activeRideRequestList;
        this.balance = balance;
    }

    public Rider(RiderEntity riderEntity, UserEntity userEntity) {
        super(userEntity);
        this.riderReference = riderEntity.getRiderReference();
        this.transactionList = riderEntity.getTransactionList();
        this.finishedRideRequestList = riderEntity.getFinishedRideRequestList();
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
        addDirtyField(User.Field.ACTIVE_RIDE_REQUEST_LIST);
    }

    /**
     * Immediately save after using
     *
     * @param rideRequest
     */
    public void deactivateRideRequest(RideRequest rideRequest) {
        activeRideRequestList.remove(rideRequest.getRideRequestReference());
        finishedRideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(User.Field.ACTIVE_RIDE_REQUEST_LIST);
        addDirtyField(Field.FINISHED_RIDE_REQUEST_LIST);
    }

    public void transferChanges(RiderEntity riderEntity) {
        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case USERNAME:
                case PASSWORD:
                case EMAIL:
                case FIRST_NAME:
                case LAST_NAME:
                case PHONE_NUMBER:
                case IMAGE:
                case DRIVER_REFERENCE:
                case RATING:
                    // do nothing
                    break;
                case USER_REFERENCE:
                    riderEntity.setUserReference(getUserReference());
                    break;
                case RIDER_REFERENCE:
                    riderEntity.setRiderReference(getRiderReference());
                    break;
                case TRANSACTION_LIST:
                    riderEntity.setPaymentList(getTransactionList());
                    break;
                case FINISHED_RIDE_REQUEST_LIST:
                    riderEntity.setFinishedRideRequestList(getFinishedRideRequestList());
                    break;
                case ACTIVE_RIDE_REQUEST_LIST:
                    riderEntity.setActiveRideRequestList(getActiveRideRequestList());
                    break;
                case BALANCE:
                    riderEntity.setBalance(getBalance());
                    break;
                default:
                    Log.e("", LOC + "transferChanges: Unknown field: " + dirtyField.toString());
                    break;
            }
        }
    }

    // region getters and setters

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        addDirtyField(User.Field.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }

    public List<DocumentReference> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<DocumentReference> transactionList) {
        addDirtyField(User.Field.TRANSACTION_LIST);
        this.transactionList = transactionList;
    }

    public List<DocumentReference> getFinishedRideRequestList() {
        return finishedRideRequestList;
    }

    public void setFinishedRideRequestList(List<DocumentReference> rideRequestList) {
        addDirtyField(Field.FINISHED_RIDE_REQUEST_LIST);
        this.finishedRideRequestList = rideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }

    public void setActiveRideRequestList(List<DocumentReference> activeRideRequestList) {
        addDirtyField(User.Field.ACTIVE_RIDE_REQUEST_LIST);
        this.activeRideRequestList = activeRideRequestList;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        addDirtyField(User.Field.BALANCE);
        this.balance = balance;
    }
    // endregion getters and setters
}
