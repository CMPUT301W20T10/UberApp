package com.cmput301w20t10.uberapp.database.entity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

public class UserEntity {
    public static final String FIELD_USERNAME = "username";
    public static final String FIELD_PASSWORD = "password";
    private static final String FIELD_EMAIL = "email";
    private static final String FIELD_FIRST_NAME = "firstName";
    private static final String FIELD_LAST_NAME = "lastName";
    private static final String FIELD_PHONE_NUMBER = "phoneNumber";

    public static final String FIELD_DRIVER_REFERENCE = "driverReference";
    public static final String FIELD_RIDER_REFERENCE = "riderReference";
    public static final String FIELD_USER_REFERENCE = "userReference";

    private static final String EMPTY_STRING_VALUE = "null";

    private DocumentReference userReference;

    private String username;
    private String email;
    private String phoneNumber;
    private String password;
    private String firstName;
    private String lastName;

    private DocumentReference driverReference;
    private DocumentReference riderReference;

    public UserEntity() {}

    public UserEntity(String username,
                      String password,
                      String email,
                      String firstName,
                      String lastName,
                      String phoneNumber) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.driverReference = null;
        this.riderReference = null;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public DocumentReference getDriverReference() {
        return driverReference;
    }

    public DocumentReference getRiderReference() {
        return riderReference;
    }

    public void setRiderReference(DocumentReference riderReference) {
        this.riderReference = riderReference;
    }

    public void setUserReference(DocumentReference userReference) {
        this.userReference = userReference;
    }

    public DocumentReference getUserReference() {
        return userReference;
    }
}