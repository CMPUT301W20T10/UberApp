package com.cmput301w20t10.uberapp.database.entity;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

import androidx.annotation.NonNull;

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
    private DocumentReference unpairedReference;

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
        UNPAIRED_REFERENCE ("unpairedReference"),
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

    public RideRequestEntity(@NonNull Rider rider, Route route, int fareOffer) {
        this.riderReference = rider.getRiderReference();
        LatLng latLngStart = route.getStartingPosition();
        LatLng latLngDestination = route.getDestination();
        startingPosition = new GeoPoint(latLngStart.latitude, latLngStart.longitude);
        destination = new GeoPoint(latLngDestination.latitude, latLngDestination.longitude);
        this.state = 0;
        this.fareOffer = fareOffer;
        this.timestamp = new Timestamp(new Date());
    }

    public RideRequestEntity(RideRequest model) {
        // get dirty fields
        for (RideRequest.Field otherField :
                model.getDirtyFieldSet()) {
            switch (otherField) {
                case DRIVER_REFERENCE:
                    addDirtyField(Field.DRIVER_REFERENCE);
                    break;
                case RIDER_REFERENCE:
                    addDirtyField(Field.RIDER_REFERENCE);
                    break;
                case PAYMENT_REFERENCE:
                    addDirtyField(Field.PAYMENT_REFERENCE);
                    break;
                case RIDE_REQUEST_REFERENCE:
                    addDirtyField(Field.RIDE_REQUEST_REFERENCE);
                    break;
                case ROUTE:
                    addDirtyField(Field.STARTING_POSITION);
                    addDirtyField(Field.DESTINATION);
                    break;
                case STATE:
                    addDirtyField(Field.STATE);
                    break;
                case TIMESTAMP:
                    addDirtyField(Field.TIMESTAMP);
                    break;
                case FARE_OFFER:
                    addDirtyField(Field.FARE_OFFER);
                    break;
            }
        }
        model.clearDirtyStateSet();

        // set all fields
        this.rideRequestReference = model.getRideRequestReference();
        this.driverReference = model.getDriverReference();
        this.riderReference = model.getRiderReference();
        this.paymentReference = model.getPaymentReference();

        LatLng latLngStart = model.getRoute().getStartingPosition();
        LatLng latLngDest = model.getRoute().getDestination();

        startingPosition = new GeoPoint(latLngStart.latitude, latLngStart.longitude);
        destination = new GeoPoint(latLngDest.latitude, latLngDest.longitude);

        state = model.getState().ordinal();
        fareOffer = model.getFareOffer();
        timestamp = new Timestamp(model.getTimestamp());
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

    public DocumentReference getUnpairedReference() {
        return unpairedReference;
    }

    public void setUnpairedReference(DocumentReference unpairedReference) {
        Log.e(TAG, "setUnpairedReference: 4");
        addDirtyField(Field.UNPAIRED_REFERENCE);
        this.unpairedReference = unpairedReference;
    }
    // endregion getters and setters
}
