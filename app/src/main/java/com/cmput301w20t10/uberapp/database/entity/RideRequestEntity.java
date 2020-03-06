package com.cmput301w20t10.uberapp.database.entity;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import static android.content.ContentValues.TAG;


/**
 * Entity representation for RiderRequest model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class RideRequestEntity extends EntityModelBase<RideRequestEntity.Field> {
    public static final String FIELD_RIDE_REQUEST_REFERENCE = "rideRequestId";

    private DocumentReference rideRequestReference;
    private DocumentReference driverReference;
    private DocumentReference riderReference;
    private DocumentReference paymentReference;

    private GeoPoint startingPosition;
    private GeoPoint destination;
    private int state;
    private int fareOffer;
    private Timestamp timestamp;

    public enum Field {
        RIDE_REQUEST_REFERENCE ("rideRequestReference"),
        DRIVER_REFERENCE ("driverReference"),
        RIDER_REFERENCE ("riderReference"),
        PAYMENT_REFERENCE ("paymentReference"),
        STARTING_POSITION ("startingPosition"),
        DESTINATION ("destination"),
        STATE ("state"),
        FARE_OFFER ("fareOffer"),
        TIMESTAMP ("timestamp");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        public String toString() {
            return stringValue;
        }
    }

    /**
     * Required for deserializing
     */
    public RideRequestEntity() {}

    public RideRequestEntity(Rider rider, Route route, int fareOffer) {
        this.rideRequestReference = rider.getRiderReference();
        LatLng latLngStart = route.getStartingPosition();
        LatLng latLngDestination = route.getDestination();
        startingPosition = new GeoPoint(latLngStart.latitude, latLngStart.longitude);
        destination = new GeoPoint(latLngDestination.latitude, latLngDestination.longitude);
        this.state = 0;
        this.fareOffer = fareOffer;
    }

    @Override
    @Exclude
    public Field[] getDirtyFieldSet() {
        Log.d(TAG, "getDirtyFieldSet: save: " + dirtyFieldSet.toString());
        return this.dirtyFieldSet.toArray(new Field[0]);
    }

    // region getters and setters
    public DocumentReference getRideRequestReference() {
        return rideRequestReference;
    }

    public void setRideRequestReference(DocumentReference rideRequestReference) {
        addDirtyField(Field.RIDE_REQUEST_REFERENCE);
        this.rideRequestReference = rideRequestReference;
    }

    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(DocumentReference driverReference) {
        addDirtyField(Field.DRIVER_REFERENCE);
        this.driverReference = driverReference;
    }

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        addDirtyField(Field.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }

    public DocumentReference getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(DocumentReference paymentReference) {
        addDirtyField(Field.PAYMENT_REFERENCE);
        this.paymentReference = paymentReference;
    }

    public GeoPoint getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(GeoPoint startingPosition) {
        addDirtyField(Field.STARTING_POSITION);
        this.startingPosition = startingPosition;
    }

    public GeoPoint getDestination() {
        return destination;
    }

    public void setDestination(GeoPoint destination) {
        addDirtyField(Field.DESTINATION);
        this.destination = destination;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
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

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        addDirtyField(Field.TIMESTAMP);
        this.timestamp = timestamp;
    }
    // endregion getters and setters
}
