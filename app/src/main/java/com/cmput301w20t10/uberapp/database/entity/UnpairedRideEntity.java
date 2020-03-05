package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

public class UnpairedRideEntity extends EntityModelBase<UnpairedRideEntity.Field> {
    private DocumentReference rideRequestReference;

    public UnpairedRideEntity(DocumentReference rideRequestReference) {
        this.rideRequestReference = rideRequestReference;
    }

    public enum Field {
        RIDE_REQUEST_REFERENCE ("rideRequestReference");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        public String toString() {
            return stringValue;
        }
    }

    @Override
    @Exclude
    public Field[] getDirtyFieldList() {
        return this.dirtyFieldList.toArray(new Field[0]);
    }

    public DocumentReference getRideRequestReference() {
        return rideRequestReference;
    }

    public void setRideRequestReference(DocumentReference rideRequestReference) {
        this.rideRequestReference = rideRequestReference;
    }
}
