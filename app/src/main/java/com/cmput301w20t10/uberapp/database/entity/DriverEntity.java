package com.cmput301w20t10.uberapp.database.entity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class DriverEntity {
    private DocumentReference driverReference;
    public int rating;
    public List<DocumentReference> paymentListReference;

    public DriverEntity() {
        this.rating = 0;
        this.paymentListReference = new ArrayList<>();
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
}
