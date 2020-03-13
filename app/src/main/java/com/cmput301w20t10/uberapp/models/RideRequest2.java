package com.cmput301w20t10.uberapp.models;

import java.util.Date;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.google.firebase.firestore.DocumentReference;


/**
 * WILL LEARN TO USE - Kevin
 */
public class RideRequest2 extends EntityModelBase<RideRequest2.Field> {
    private DocumentReference driverReference;
    private DocumentReference riderReference;
    private DocumentReference transactionReference;
    private DocumentReference unpairedReference;

    private DocumentReference rideRequestReference;
    private Route route;
    private State state;
    private int fareOffer;
    private Date timestamp;

    public enum State {
        Active,
        DriverFound,
        RiderAccepted,
        RideCompleted,
        TransactionFinished,
        Cancelled
    }

    public enum Field {
        DRIVER_REFERENCE ("driverReference"),
        RIDER_REFERENCE ("riderReference"),
        TRANSACTION_REFERENCE("transactionReference"),
        RIDE_REQUEST_REFERENCE ("rideRequestReference"),
        ROUTE ("route"),
        STATE ("state"),
        TIMESTAMP ("timestamp"),
        UNPAIRED_REFERENCE ("unpairedReference"),
        FARE_OFFER ("fareOffer");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        public String toString() {
            return stringValue;
        }
    }

    public RideRequest2(RideRequestEntity entity) {
        this.driverReference = entity.getDriverReference();
        this.riderReference = entity.getRiderReference();
        this.transactionReference = entity.getTransactionReference();
        this.rideRequestReference = entity.getRideRequestReference();
        this.route = new Route(entity.getStartingPosition(), entity.getDestination());
        this.state = State.values()[entity.getState()];
        this.timestamp = entity.getTimestamp().toDate();
        this.fareOffer = entity.getFareOffer();
        this.unpairedReference = entity.getUnpairedReference();
    }

    @Override
    public Field[] getDirtyFieldSet() {
        return dirtyFieldSet.toArray(new Field[0]);
    }

    // region getters and setters
    public DocumentReference getRideRequestReference() {
        return rideRequestReference;
    }

    public void setRideRequestReference(DocumentReference rideRequestReference) {
        addDirtyField(Field.RIDE_REQUEST_REFERENCE);
        this.rideRequestReference = rideRequestReference;
    }

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        addDirtyField(Field.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }
    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(DocumentReference driverReference) {
        addDirtyField(Field.DRIVER_REFERENCE);
        this.driverReference = driverReference;
    }

    public DocumentReference getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(DocumentReference transactionReference) {
        addDirtyField(Field.TRANSACTION_REFERENCE);
        this.transactionReference = transactionReference;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        addDirtyField(Field.ROUTE);
        this.route = route;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        addDirtyField(Field.STATE);
        this.state = state;
    }

    public int getFareOffer() {
        return fareOffer;
    }

    public void setFareOffer(int fareOffer) {
        addDirtyField(Field.FARE_OFFER);
        this.fareOffer = fareOffer;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        addDirtyField(Field.TIMESTAMP);
        this.timestamp = timestamp;
    }

    public DocumentReference getUnpairedReference() {
        return unpairedReference;
    }

    public void setUnpairedReference(DocumentReference unpairedReference) {
        addDirtyField(Field.UNPAIRED_REFERENCE);
        this.unpairedReference = unpairedReference;
    }
    // endregion getters and setters

}
