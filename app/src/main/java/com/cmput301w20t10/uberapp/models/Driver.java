package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

import java.util.List;

public class Driver extends User {
    private DocumentReference driverReference;
    private List<DocumentReference> transactionList;
    private List<DocumentReference> rideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int rating;

    public Driver(String userName,
                  String password,
                  String email,
                  String firstName,
                  String lastName,
                  String phoneNumber,
                  int rating,
                  String image) {
        super(userName, password, email, firstName, lastName, phoneNumber, image);
        this.rating = rating;
    }

    public Driver(DriverEntity driverEntity, UserEntity userEntity) {
        super(userEntity);
        this.driverReference = driverEntity.getDriverReference();
        this.rating = driverEntity.getRating();
        this.transactionList = driverEntity.getPaymentList();
        this.rideRequestList = driverEntity.getFinishedRideRequestList();
        this.activeRideRequestList = driverEntity.getActiveRideRequestList();

    }

    public void addActiveRideRequest(RideRequest rideRequest) {
        activeRideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
    }

    // region getters and setters
    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(DocumentReference driverReference) {
        addDirtyField(Field.DRIVER_REFERENCE);
        this.driverReference = driverReference;
    }

    public List<DocumentReference> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<DocumentReference> transactionList) {
        addDirtyField(Field.TRANSACTION_LIST);
        this.transactionList = transactionList;
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

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        addDirtyField(Field.RATING);
        this.rating = rating;
    }
    // endregion getters and setters
}
