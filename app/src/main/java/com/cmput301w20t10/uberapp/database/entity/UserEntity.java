package com.cmput301w20t10.uberapp.database.entity;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.EnumField;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.cmput301w20t10.uberapp.database.entity.UserEntity.*;

/**
 * Entity representation for Driver model.
 * @see EntityBase
 *
 * @author Allan Manuba
 * @version 1.0.0
 */
public class UserEntity extends EntityBase<Field> {
    // region Fields
    /**
     * Fields
     * @version 1.0.0
     */
    private static final String LOC = "Tomate: UserEntity: ";

    public enum Field {
        USERNAME ("username"),
        PASSWORD ("password"),
        EMAIL ("email"),
        FIRST_NAME ("firstName"),
        LAST_NAME ("lastName"),
        PHONE_NUMBER ("phoneNumber"),
        DRIVER_REFERENCE ("driverReference"),
        RIDER_REFERENCE ("riderReference"),
        USER_REFERENCE ("userReference"),
        IMAGE ("image");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        public String toString() {
            return stringValue;
        }
    }

    private DocumentReference userReference;
    private DocumentReference driverReference;
    private DocumentReference riderReference;

    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;
    private String image;
    // endregion fields

    // region Constructors
    /**
     * Constructors
     * @version 1.0.0
     */

    /**
     * For Firestore deserialization
     */
    public UserEntity() { super(); }

    public UserEntity(String username,
                      String password,
                      String email,
                      String firstName,
                      String lastName,
                      String phoneNumber,
                      String image) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.driverReference = null;
        this.riderReference = null;
        this.userReference = null;
    }

    // todo: check out if still needed
    public UserEntity(DocumentReference userReference,
                      DocumentReference driverReference,
                      DocumentReference riderReference,
                      String username,
                      String password,
                      String email,
                      String firstName,
                      String lastName,
                      String phoneNumber,
                      String image) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.driverReference = driverReference;
        this.riderReference = riderReference;
        this.userReference = userReference;
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
        Log.d(TAG, LOC + "getDirtyFieldMap: Here!");
        Log.d(TAG, LOC + "getDirtyFieldMap: Set: " + dirtyFieldSet.toString());
        HashMap<String, Object> dirtyFieldMap = new HashMap<>();
        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case USERNAME:
                    dirtyFieldMap.put(dirtyField.toString(), getUsername());
                    break;
                case PASSWORD:
                    dirtyFieldMap.put(dirtyField.toString(), getPassword());
                    break;
                case EMAIL:
                    dirtyFieldMap.put(dirtyField.toString(), getEmail());
                    break;
                case FIRST_NAME:
                    dirtyFieldMap.put(dirtyField.toString(), getFirstName());
                    break;
                case LAST_NAME:
                    dirtyFieldMap.put(dirtyField.toString(), getLastName());
                    break;
                case PHONE_NUMBER:
                    dirtyFieldMap.put(dirtyField.toString(), getPhoneNumber());
                    break;
                case DRIVER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getDriverReference());
                    break;
                case RIDER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getRiderReference());
                    break;
                case USER_REFERENCE:
                    dirtyFieldMap.put(dirtyField.toString(), getUserReference());
                    break;
                case IMAGE:
                    dirtyFieldMap.put(dirtyField.toString(), getImage());
                    break;
                default:
                    Log.e(TAG, LOC + "getDirtyFieldMap: Unknown field: " + dirtyField.toString());
                    break;
            }
        }
        Log.d(TAG, LOC + "getDirtyFieldMap: End!");
        Log.d(TAG, LOC + "getDirtyFieldMap: " + dirtyFieldMap.toString());
        return  dirtyFieldMap;
    }

    // region getters and setters
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

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        addDirtyField(Field.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        addDirtyField(Field.USERNAME);
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        addDirtyField(Field.EMAIL);
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        addDirtyField(Field.PHONE_NUMBER);
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        addDirtyField(Field.PASSWORD);
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        addDirtyField(Field.FIRST_NAME);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        addDirtyField(Field.LAST_NAME);
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        addDirtyField(Field.IMAGE);
        this.image = image;
    }
    // endregion getters and setters
}