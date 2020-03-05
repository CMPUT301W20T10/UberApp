package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class RiderEntity extends EntityModelBase<RiderEntity.Field> {
    public enum Field {
        RIDER_REFERENCE ("riderReference"),
        PAYMENT_LIST ("paymentList"),
        RIDE_REQUEST_LIST("rideRequestList"),
        ACTIVE_RIDE_REQUEST_LIST ("activeRideRequestList");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        @NonNull
        public String toString() {
            return stringValue;
        }
    }

    private DocumentReference riderReference;
    private List<DocumentReference> paymentReferenceList;
    private List<DocumentReference> rideRequestList;
    private List<DocumentReference> activeRideRequestList;

    public RiderEntity() {
        paymentReferenceList = new ArrayList<>();
        rideRequestList = new ArrayList<>();
        activeRideRequestList = new ArrayList<>();
    }

    public RiderEntity(List<DocumentReference> paymentReferenceList) {
        this.paymentReferenceList = paymentReferenceList;
    }

    @Override
    @Exclude
    public Field[] getDirtyFieldList() {
        return this.dirtyFieldList.toArray(new Field[0]);
    }

    // region getters and setters

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        this.dirtyFieldList.add(Field.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }

    public List<DocumentReference> getPaymentList() {
        return paymentReferenceList;
    }

    public void setPaymentList(List<DocumentReference> paymentReferenceList) {
        this.dirtyFieldList.add(Field.PAYMENT_LIST);
        this.paymentReferenceList = paymentReferenceList;
    }

    public List<DocumentReference> getRideRequestList() {
        return rideRequestList;
    }

    public void setRideRequestList(List<DocumentReference> rideRequestList) {
        this.dirtyFieldList.add(Field.RIDE_REQUEST_LIST);
        this.rideRequestList = rideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }

    public void setActiveRideRequestList(List<DocumentReference> activeRideRequestList) {
        this.dirtyFieldList.add(Field.ACTIVE_RIDE_REQUEST_LIST);
        this.activeRideRequestList = activeRideRequestList;
    }

    // endregion getters and setters

}