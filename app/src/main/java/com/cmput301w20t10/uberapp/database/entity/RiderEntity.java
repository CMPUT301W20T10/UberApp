package com.cmput301w20t10.uberapp.database.entity;

import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.cmput301w20t10.uberapp.database.entity.RiderEntity.*;

/**
 * Entity representation for Rider model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class RiderEntity extends EntityBase<Field> {
    private DocumentReference userReference;
    private DocumentReference riderReference;
    private List<DocumentReference> transactionList;
    private List<DocumentReference> rideRequestList;
    private List<DocumentReference> activeRideRequestList;
    private int balance;

    enum Field {
        RIDER_REFERENCE ("riderReference"),
        USER_REFERENCE ("userReference"),
        PAYMENT_LIST ("transactionList"),
        FINISHED_RIDE_REQUEST_LIST("finishedRideRequestList"),
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
        transactionList = new ArrayList<>();
        rideRequestList = new ArrayList<>();
        activeRideRequestList = new ArrayList<>();
    }

    @Override
    @Exclude
    public Map<String, Object> getDirtyFieldMap() {
        Map<String, Object> dirtyFieldMap = new HashMap<>();

        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case RIDER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getRiderReference());
                    break;
                case USER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getUserReference());
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
                case BALANCE:
                    dirtyFieldMap.put(dirtyField.toString(), getBalance());
                    break;
                default:
                    Log.e(TAG, "getDirtyFieldMap: Unknown field: " + dirtyField.toString());
                    break;
            }
        }

        return dirtyFieldMap;
    }

    public void deactivateRideRequest(RideRequest rideRequest) {
        activeRideRequestList.remove(rideRequest.getRideRequestReference());
        rideRequestList.add(rideRequest.getRideRequestReference());
        addDirtyField(Field.ACTIVE_RIDE_REQUEST_LIST);
        addDirtyField(Field.FINISHED_RIDE_REQUEST_LIST);
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

    public List<DocumentReference> getTransactionList() {
        return transactionList;
    }

    public void setPaymentList(List<DocumentReference> paymentReferenceList) {
        addDirtyField(Field.PAYMENT_LIST);
        this.transactionList = paymentReferenceList;
    }

    public List<DocumentReference> getFinishedRideRequestList() {
        return rideRequestList;
    }

    public void setFinishedRideRequestList(List<DocumentReference> rideRequestList) {
        addDirtyField(Field.FINISHED_RIDE_REQUEST_LIST);
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