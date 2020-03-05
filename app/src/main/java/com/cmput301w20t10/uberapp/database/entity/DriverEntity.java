package com.cmput301w20t10.uberapp.database.entity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class DriverEntity {
    public static final String DRIVER_REFERENCE = "driverReference";

    private DocumentReference driverReference;
    public int rating;
    public List<DocumentReference> paymentListReference;
    private List<DocumentReference> finishedRideRequestList;
    private List<DocumentReference> activeRideRequestList;

    public DriverEntity() {
        this.rating = 0;
        this.paymentListReference = new ArrayList<>();
        this.finishedRideRequestList = new ArrayList<>();
        this.activeRideRequestList = new ArrayList<>();
    }

    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(DocumentReference driverReference) {
        this.driverReference = driverReference;
    }

    public int getRating() {
        return rating;
    }

    public List<DocumentReference> getPaymentListReference() {
        return paymentListReference;
    }

    public List<DocumentReference> getFinishedRideRequestList() {
        return finishedRideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }
}
