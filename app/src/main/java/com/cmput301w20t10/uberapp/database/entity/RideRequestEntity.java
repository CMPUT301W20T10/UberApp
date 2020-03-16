package com.cmput301w20t10.uberapp.database.entity;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import static android.content.ContentValues.TAG;
import static com.cmput301w20t10.uberapp.database.entity.RideRequestEntity.*;


/**
 * Entity representation for RiderRequest model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class RideRequestEntity extends EntityBase<Field> {
    public static final String FIELD_RIDE_REQUEST_REFERENCE = "rideRequestId";
    private static final String LOC = "RideRequestEntity";

    private DocumentReference rideRequestReference;
    private DocumentReference driverReference;
    private DocumentReference riderReference;
    private DocumentReference transactionReference;
    private DocumentReference unpairedReference;

    private GeoPoint startingPosition;
    private GeoPoint destination;
    private int state;
    private int fareOffer;
    private Timestamp timestamp;

    enum Field {
        RIDE_REQUEST_REFERENCE ("rideRequestReference"),
        DRIVER_REFERENCE ("driverReference"),
        RIDER_REFERENCE ("riderReference"),
        TRANSACTION_REFERENCE("transactionReference"),
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

        @Override
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
        LatLng latLngDestination = route.getDestinationPosition();
        startingPosition = new GeoPoint(latLngStart.latitude, latLngStart.longitude);
        destination = new GeoPoint(latLngDestination.latitude, latLngDestination.longitude);
        this.state = 0;
        this.fareOffer = fareOffer;
        this.timestamp = new Timestamp(new Date());
    }

    @Override
    @Exclude
    public Map<String, Object> getDirtyFieldMap() {
        Map<String, Object> dirtyFieldMap = new HashMap<>();
        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case RIDE_REQUEST_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getRideRequestReference());
                    break;
                case DRIVER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getDriverReference());
                    break;
                case RIDER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getRiderReference());
                    break;
                case TRANSACTION_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getTransactionReference());
                    break;
                case STARTING_POSITION:
                    dirtyFieldMap.put(dirtyField.toString(), getStartingPosition());
                    break;
                case DESTINATION:
                    dirtyFieldMap.put(dirtyField.toString(), getDestination());
                    break;
                case STATE:
                    dirtyFieldMap.put(dirtyField.toString(), getState());
                    break;
                case FARE_OFFER:
                    dirtyFieldMap.put(dirtyField.toString(), getFareOffer());
                    break;
                case UNPAIRED_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getUnpairedReference());
                    break;
                case TIMESTAMP:
                    dirtyFieldMap.put(dirtyField.toString(), getTimestamp());
                    break;
                default:
                    Log.e(TAG, LOC + "getDirtyFieldMap: Unknown field: " + dirtyField.toString());
                    break;
            }
        }
        return dirtyFieldMap;
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

    public DocumentReference getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(DocumentReference transactionReference) {
        addDirtyField(Field.TRANSACTION_REFERENCE);
        this.transactionReference = transactionReference;
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
