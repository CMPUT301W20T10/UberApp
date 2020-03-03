package com.cmput301w20t10.uberapp.database.entity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RiderEntity {
    public static final String FIELD_PAYMENT_REFERENCE_LIST = "paymentReferenceList";
    public static final String FIELD_RIDER_REFERENCE = "riderReference";

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

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference documentReference) {
        this.riderReference = documentReference;
    }
}