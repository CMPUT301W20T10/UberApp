package com.cmput301w20t10.uberapp.database.entity;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.EnumField;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Entity representation for Driver model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class DriverEntity extends EntityModelBase<DriverEntity.Field> {
    public static final String DRIVER_REFERENCE = "driverReference";

    private DocumentReference userReference;
    private DocumentReference driverReference;
    private List<DocumentReference> paymentList;
    private List<DocumentReference> finishedRideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int rating;

    public enum Field {
        USER_REFERENCE ("userReference"),
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

    public DriverEntity(Driver driver) {
        this.userReference = driver.getUserReference();
        this.driverReference = driver.getDriverReference();
        this.paymentList = driver.getTransactionList();
        this.finishedRideRequestList = driver.getRideRequestList();
        this.activeRideRequestList = driver.getActiveRideRequestList();
        this.rating = driver.getRating();

        for (EnumField dirtyField :
                driver.getDirtyFieldSet()) {
            switch (dirtyField) {
                case USERNAME:
                case PASSWORD:
                case EMAIL:
                case FIRST_NAME:
                case LAST_NAME:
                case PHONE_NUMBER:
                case IMAGE:
                case RIDER_REFERENCE:
                case BALANCE:
                    // do nothing
                    break;
                case USER_REFERENCE:
                    addDirtyField(Field.USER_REFERENCE);
                    break;
                case DRIVER_REFERENCE:
                    addDirtyField(Field.DRIVER_REFERENCE);
                    break;
                case TRANSACTION_LIST:
                    addDirtyField(Field.PAYMENT_LIST);
                    break;
                case RIDE_REQUEST_LIST:
                    addDirtyField(Field.FINISHED_RIDE_REQUEST_LIST);
                    break;
                case ACTIVE_RIDE_REQUEST_LIST:
                    addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
                    break;
                case RATING:
                    addDirtyField(Field.RATING);
                    break;
                default:
                    Log.w(TAG, "DriverEntity: Constructor Unknown field: " + dirtyField.toString());
                    break;
            }
        }
    }

    @Override
    @Exclude
    public Field[] getDirtyFieldSet() {
        return dirtyFieldSet.toArray(new Field[0]);
    }

    public void deactivateRideRequest(RideRequest rideRequest) {
        activeRideRequestList.remove(rideRequest.getRideRequestReference());
        finishedRideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
        addDirtyField(Field.FINISHED_RIDE_REQUEST_LIST);
    }

    // region setters
    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        addDirtyField(Field.USER_REFERENCE);
        this.userReference = userReference;
    }

    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(DocumentReference driverReference) {
        addDirtyField(Field.DRIVER_REFERENCE);
        this.driverReference = driverReference;
    }

    public List<DocumentReference> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List<DocumentReference> paymentList) {
        addDirtyField(Field.PAYMENT_LIST);
        this.paymentList = paymentList;
    }

    public List<DocumentReference> getFinishedRideRequestList() {
        return finishedRideRequestList;
    }

    public void setFinishedRideRequestList(List<DocumentReference> finishedRideRequestList) {
        addDirtyField(Field.FINISHED_RIDE_REQUEST_LIST);
        this.finishedRideRequestList = finishedRideRequestList;
    }

    public List<DocumentReference> getActiveRideRequestList() {
        return activeRideRequestList;
    }

    public void setActiveRideRequestList(List<DocumentReference> activeRideRequestList) {
        addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
        this.activeRideRequestList = activeRideRequestList;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        addDirtyField(Field.RATING);
        this.rating = rating;
    }
    // endregion setters

}
