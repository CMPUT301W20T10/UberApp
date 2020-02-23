package com.cmput301w20t10.uberapp;

/**
 * This is a data holder class intended to hold the data regarding a user. Drawbacks to this class
 *  involve the security risk of storing the password in a String format
 */
public class User {

    private String userName;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private float rating;

    public User(String userName, String password, String email, String firstName, String lastName, String phoneNumber, float rating) {
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
    }

    public String getUserName() {
        return userName;
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
