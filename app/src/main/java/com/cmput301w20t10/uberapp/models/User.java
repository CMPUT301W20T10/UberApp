package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

/**
 * This is a data holder class intended to hold the data regarding a user. Drawbacks to this class
 *  involve the security risk of storing the password in a String format
 */
public class User {
    private DocumentReference userReference;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private int balance;
    private String image;

    // todo: deprecate
    private float rating;

    // todo: deprecate
    public User(String username,
                String password,
                String email,
                String firstName,
                String lastName,
                String phoneNumber,
                float rating) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    public User(String username,
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
    }

    public User(String username,
                String password,
                String email,
                String firstName,
                String lastName,
                String phoneNumber,
                DocumentReference userReference) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.userReference = userReference;
    }

    public User(UserEntity userEntity) {
        this.userReference = userEntity.getUserReference();
        this.username = userEntity.getUsername();
        this.password = userEntity.getPassword();
        this.email = userEntity.getEmail();
        this.firstName = userEntity.getFirstName();
        this.lastName = userEntity.getLastName();
        this.phoneNumber = userEntity.getPhoneNumber();
        this.image = userEntity.getImage();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getBalance() {
        return balance;
    }

    public float getRating() {
        return rating;
    }

    public DocumentReference getUserReference() {
        return userReference;
    }

    protected void commit() {
        // todo: save new changes to
    }
}
