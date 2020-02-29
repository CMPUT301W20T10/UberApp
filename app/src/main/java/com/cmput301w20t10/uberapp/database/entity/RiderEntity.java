package com.cmput301w20t10.uberapp.database.entity;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.List;

public class RiderEntity {
    public static final String FIELD_PAYMENT_REFERENCE_LIST = "paymentReferenceList";

    private DocumentReference riderReference;
    private List<DocumentReference> paymentReferenceList;

    public RiderEntity() {
        paymentReferenceList = new ArrayList<>();
    }

    public RiderEntity(List<DocumentReference> paymentReferenceList) {
        this.paymentReferenceList = paymentReferenceList;
    }

    public List<DocumentReference> getPaymentReferenceList() {
        return paymentReferenceList;
    }
}