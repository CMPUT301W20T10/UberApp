package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.models.RideRequest2;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

/**
 * Entity representation for Rider model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class RiderEntity extends EntityModelBase<RiderEntity.Field> {
    private DocumentReference userReference;
    private DocumentReference riderReference;
    private List<DocumentReference> paymentReferenceList;
    private List<DocumentReference> rideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int balance;

    public enum Field {
        RIDER_REFERENCE ("riderReference"),
        USER_REFERENCE ("userReference"),
        PAYMENT_LIST ("paymentList"),
        RIDE_REQUEST_LIST("rideRequestList"),
        ACTIVE_RIDE_REQUEST_LIST ("activeRideRequestList"),
        BALANCE ("balance");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        @NonNull
        public String toString() {
            return stringValue;
        }
    }

    public RiderEntity() {
        paymentReferenceList = new ArrayList<>();
        rideRequestList = new ArrayList<>();
        activeRideRequestList = new ArrayList<>();
    }

    @Override
    @Exclude
    public Field[] getDirtyFieldSet() {
        return this.dirtyFieldSet.toArray(new Field[0]);
    }

    public void deactivateRideRequest(RideRequest2 rideRequest) {
        activeRideRequestList.remove(rideRequest.getRideRequestReference());
        rideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
        addDirtyField(Field.RIDE_REQUEST_LIST);
    }

    // region getters and setters
    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        addDirtyField(Field.USER_REFERENCE);
        this.userReference = userReference;
    }

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        addDirtyField(Field.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }

    public List<DocumentReference> getPaymentList() {
        return paymentReferenceList;
    }

    public void setPaymentList(List<DocumentReference> paymentReferenceList) {
        addDirtyField(Field.PAYMENT_LIST);
        this.paymentReferenceList = paymentReferenceList;
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