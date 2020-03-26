package com.cmput301w20t10.uberapp.models;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Driver extends User {
    private static final String LOC = "Driver: ";
    private DocumentReference driverReference;
    private List<DocumentReference> transactionList;
    private List<DocumentReference> finishedRideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int rating;

    public Driver(DocumentReference userReference,
                  DocumentReference driverReference,
                  List<DocumentReference> transactionList,
                  List<DocumentReference> finishedRideRequestList,
                  List<DocumentReference> activeRideRequestList,
                  String username,
                  String password,
                  String email,
                  String firstName,
                  String lastName,
                  String phoneNumber,
                  int rating,
                  String image) {
        super(userReference, username, password, email, firstName, lastName, phoneNumber, image);
        this.driverReference = driverReference;
        this.transactionList = transactionList;
        this.finishedRideRequestList = finishedRideRequestList;
        this.activeRideRequestList = activeRideRequestList;
        this.rating = rating;
    }

    public void addActiveRideRequest(RideRequest rideRequest) {
        activeRideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(User.Field.ACTIVE_RIDE_REQUEST_LIST);
    }


    public void deactivateRideRequest(RideRequest rideRequest) {
        activeRideRequestList.remove(rideRequest.getRideRequestReference());
        finishedRideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(User.Field.ACTIVE_RIDE_REQUEST_LIST);
        addDirtyField(User.Field.FINISHED_RIDE_REQUEST_LIST);
    }

    public void transferChanges(DriverEntity driverEntity) {
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
                case RIDER_REFERENCE:
                case BALANCE:
                    // do nothing
                    break;
                case USER_REFERENCE:
                    driverEntity.setUserReference(getUserReference());
                    break;
                case DRIVER_REFERENCE:
                    // todo: document why this is always done?
                    break;
                case TRANSACTION_LIST:
                    driverEntity.setPaymentList(getTransactionList());
                    break;
                case FINISHED_RIDE_REQUEST_LIST:
                    driverEntity.setFinishedRideRequestList(getFinishedRideRequestList());
                    break;
                case ACTIVE_RIDE_REQUEST_LIST:
                    driverEntity.setActiveRideRequestList(getActiveRideRequestList());
                    break;
                case RATING:
                    driverEntity.setRating(getRating());
                    break;
                default:
                    Log.e("", LOC +"transferChanges: Unknown field: " + dirtyField.toString());
                    break;
            }
        }

        driverEntity.setDriverReference(getDriverReference());
    }

    // region getters and setters
    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(DocumentReference driverReference) {
        addDirtyField(User.Field.DRIVER_REFERENCE);
        this.driverReference = driverReference;
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

    public void setFinishedRideRequestList(List<DocumentReference> finishedRideRequestList) {
        addDirtyField(User.Field.FINISHED_RIDE_REQUEST_LIST);
        this.finishedRideRequestList = finishedRideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }

    public void setActiveRideRequestList(List<DocumentReference> activeRideRequestList) {
        addDirtyField(User.Field.ACTIVE_RIDE_REQUEST_LIST);
        this.activeRideRequestList = activeRideRequestList;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        addDirtyField(User.Field.RATING);
        this.rating = rating;
    }

    public void incrementRating(int increment) {
        setRating(this.rating + increment);
    }
    // endregion getters and setters
}
