package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.EntityModelBase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class DriverEntity extends EntityModelBase<DriverEntity.Field> {
    public static final String DRIVER_REFERENCE = "driverReference";

    private DocumentReference driverReference;
    private List<DocumentReference> paymentList;
    private List<DocumentReference> finishedRideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int rating;

    public enum Field {
        DRIVER_REFERENCE ("driverReference"),
        RATING ("rating"),
        PAYMENT_LIST ("paymentList"),
        FINISHED_RIDE_REQUEST_LIST ("finishedRideRequestList"),
        ACTIVE_RIDE_REQUEST_LIST ("activeRideRequestList");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        public String toString() {
            return stringValue;
        }
    };

    public DriverEntity() {
        this.rating = 0;
        this.paymentList = new ArrayList<>();
        this.finishedRideRequestList = new ArrayList<>();
        this.activeRideRequestList = new ArrayList<>();
    }

    @Override
    @Exclude
    public Field[] getDirtyFieldList() {
        return dirtyFieldList.toArray(new Field[0]);
    }

    // region setters

    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(DocumentReference driverReference) {
        this.dirtyFieldList.add(Field.DRIVER_REFERENCE);
        this.driverReference = driverReference;
    }

    public List<DocumentReference> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<DocumentReference> paymentList) {
        this.dirtyFieldList.add(Field.PAYMENT_LIST);
        this.paymentList = paymentList;
    }

    public List<DocumentReference> getFinishedRideRequestList() {
        return finishedRideRequestList;
    }

    public void setFinishedRideRequestList(List<DocumentReference> finishedRideRequestList) {
        this.dirtyFieldList.add(Field.FINISHED_RIDE_REQUEST_LIST);
        this.finishedRideRequestList = finishedRideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }

    public void setActiveRideRequestList(List<DocumentReference> activeRideRequestList) {
        this.dirtyFieldList.add(Field.ACTIVE_RIDE_REQUEST_LIST);
        this.activeRideRequestList = activeRideRequestList;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.dirtyFieldList.add(Field.RATING);
        this.rating = rating;
    }
    // endregion setters

}
