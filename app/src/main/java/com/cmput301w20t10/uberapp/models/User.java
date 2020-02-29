package com.cmput301w20t10.uberapp.models;

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
                String phoneNumber) {
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

    public float getRating() {
        return rating;
    }
}
