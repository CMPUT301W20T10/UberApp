package com.cmput301w20t10.uberapp.models;

import java.util.Date;

import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.database.base.ModelBase;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import static com.cmput301w20t10.uberapp.models.RideRequest.*;


/**
 * WILL LEARN TO USE - Kevin
 */
public class RideRequest extends ModelBase<Field, RideRequestEntity> {
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

    enum Field {
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

    public RideRequest(RideRequestEntity entity) {
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
    public void transferChanges(RideRequestEntity entity) {
        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case DRIVER_REFERENCE:
                    entity.setDriverReference(getDriverReference());
                    break;
                case RIDER_REFERENCE:
                    entity.setRiderReference(getRiderReference());
                    break;
                case TRANSACTION_REFERENCE:
                    entity.setRiderReference(getTransactionReference());
                    break;
                case RIDE_REQUEST_REFERENCE:
                    // todo: document why this is neeeded
                    break;
                case ROUTE:
                    Route route = getRoute();
                    LatLng latLngStartingPosition = route.getStartingPosition();
                    LatLng latLngDestination = route.getDestinationPosition();
                    GeoPoint startingPosition = new GeoPoint(latLngStartingPosition.latitude,
                            latLngStartingPosition.longitude);
                    GeoPoint destination = new GeoPoint(latLngDestination.latitude,
                            latLngDestination.longitude);
                    entity.setStartingPosition(startingPosition);
                    entity.setDestination(destination);
                    break;
                case STATE:
                    entity.setState(getState().ordinal());
                    break;
                case TIMESTAMP:
                    entity.setTimestamp(new Timestamp(getTimestamp()));
                    break;
                case UNPAIRED_REFERENCE:
                    entity.setUnpairedReference(getUnpairedReference());
                    break;
                case FARE_OFFER:
                    entity.setFareOffer(getFareOffer());
                    break;
            }
        }

        entity.setRideRequestReference(getRideRequestReference());
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
