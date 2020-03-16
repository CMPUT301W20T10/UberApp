package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.cmput301w20t10.uberapp.database.entity.DriverEntity.*;

/**
 * Entity representation for Driver model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class DriverEntity extends EntityBase<Field> {
    public static final String DRIVER_REFERENCE = "driverReference";

    private DocumentReference userReference;
    private DocumentReference driverReference;
    private List<DocumentReference> paymentList;
    private List<DocumentReference> finishedRideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int rating;

    enum Field {
        USER_REFERENCE ("userReference"),
        DRIVER_REFERENCE ("driverReference"),
        RATING ("rating"),
        PAYMENT_LIST ("transactionList"),
        FINISHED_RIDE_REQUEST_LIST ("finishedRideRequestList"),
        ACTIVE_RIDE_REQUEST_LIST ("activeRideRequestList");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        @Override
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

    public DriverEntity(DocumentReference userReference,
                        DocumentReference driverReference,
                        List<DocumentReference> paymentList,
                        List<DocumentReference> finishedRideRequestList,
                        List<DocumentReference> activeRideRequestList) {
        this.userReference = userReference;
        this.driverReference = driverReference;
        this.paymentList = paymentList;
        this.finishedRideRequestList = finishedRideRequestList;
        this.activeRideRequestList = activeRideRequestList;
    }

    @Override
    @Exclude
    public Map<String, Object> getDirtyFieldMap() {
        Map<String, Object> dirtyFieldMap = new HashMap<>();

        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case USER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getUserReference());
                    break;
                case DRIVER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getDriverReference());
                    break;
                case RATING:
                    dirtyFieldMap.put(dirtyField.toString(), getRating());
                    break;
                case PAYMENT_LIST:
                    dirtyFieldMap.put(dirtyField.toString(), getTransactionList());
                    break;
                case FINISHED_RIDE_REQUEST_LIST:
                    dirtyFieldMap.put(dirtyField.toString(), getFinishedRideRequestList());
                    break;
                case ACTIVE_RIDE_REQUEST_LIST:
                    dirtyFieldMap.put(dirtyField.toString(), getActiveRideRequestList());
                    break;
            }
        }

        return dirtyFieldMap;
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

    public List<DocumentReference> getTransactionList() {
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
