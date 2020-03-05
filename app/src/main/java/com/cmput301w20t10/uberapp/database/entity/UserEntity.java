package com.cmput301w20t10.uberapp.database.entity;

import com.cmput301w20t10.uberapp.database.EntityModelBase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

public class UserEntity extends EntityModelBase<UserEntity.Field> {

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


    public UserEntity() {}

    public UserEntity(String username,
                      String password,
                      String email,
                      String firstName,
                      String lastName,
                      String phoneNumber,
                      String image) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.driverReference = null;
        this.riderReference = null;
    }

    // region getters and setters
    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        dirtyFieldList.add(Field.USER_REFERENCE);
        this.userReference = userReference;
    }

    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public void setDriverReference(DocumentReference driverReference) {
        dirtyFieldList.add(Field.DRIVER_REFERENCE);
        this.driverReference = driverReference;
    }

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        dirtyFieldList.add(Field.RIDER_REFERENCE);
        this.riderReference = riderReference;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        dirtyFieldList.add(Field.USERNAME);
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        dirtyFieldList.add(Field.EMAIL);
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        dirtyFieldList.add(Field.PHONE_NUMBER);
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        dirtyFieldList.add(Field.PASSWORD);
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        dirtyFieldList.add(Field.FIRST_NAME);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        dirtyFieldList.add(Field.LAST_NAME);
        this.lastName = lastName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        dirtyFieldList.add(Field.IMAGE);
        this.image = image;
    }
    // endregion getters and setters

    @Override
    @Exclude
    public Field[] getDirtyFieldList() {
        return dirtyFieldList.toArray(new Field[0]);
    }
}