package com.cmput301w20t10.uberapp.database.entity;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.cmput301w20t10.uberapp.database.entity.UnpairedRideEntity.*;

/**
 * Entity representation for UnpairedRideEntity model.
 * @see EntityBase
 *
 * todo: add timestamp
 * @author Allan Manuba
 * @version 1.0.0
 */
public class UnpairedRideEntity extends EntityBase<Field> {
    // region Fields
    /**
     * Fields
     * @version 1.0.0
     */

    private static final String LOC = "UnpairedRideEntity: ";
    private DocumentReference rideRequestReference;

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
    // endregion Fields

    // region Constructors
    /**
     * Constructors
     * @version 1.0.0
     */

    /**
     * Don't remove. This is required during deserialization.
     */
    public UnpairedRideEntity() {}

    public UnpairedRideEntity(DocumentReference rideRequestReference) {
        this.rideRequestReference = rideRequestReference;
    }
    // endregion Constructors

    /**
     * @see EntityBase#addDirtyField(Object)
     *
     * @return a map that can be used to update a Firestore reference
     *
     * @author Allan Manuba
     * @version 1.0.0
     */
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
                default:
                    Log.e(TAG, LOC + "getDirtyFieldMap: Unknown field: " + dirtyField.toString());
                    break;
            }
        }
        return dirtyFieldMap;
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
