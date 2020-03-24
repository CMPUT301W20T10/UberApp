package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

/**
 * Entity representation for UnpairedRideEntity model.
 * Entity objects are the one-to-one representation of objects from the database.
 *
 * @author Allan Manuba
 */
public class UnpairedRideEntity extends EntityModelBase<UnpairedRideEntity.Field> {
    private DocumentReference rideRequestReference;

    /**
     * Don't remove. This is required during deserialization.
     */
    public UnpairedRideEntity() {}

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
    public Field[] getDirtyFieldSet() {
        return this.dirtyFieldSet.toArray(new Field[0]);
    }

    // region setter and getter
    public DocumentReference getRideRequestReference() {
        return rideRequestReference;
    }

    public void setRideRequestReference(DocumentReference rideRequestReference) {
        addDirtyField(Field.RIDE_REQUEST_REFERENCE);
        this.rideRequestReference = rideRequestReference;
    }
    // endregion
}
