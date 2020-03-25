package com.cmput301w20t10.uberapp.models;

import com.cmput301w20t10.uberapp.database.base.EntityModelBase;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;

/**
 * This is a data holder class intended to hold the data regarding a user. Drawbacks to this class
 * involve the security risk of storing the password in a String format
 */
public class User extends EntityModelBase<EnumField> {
    private DocumentReference userReference;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String FCMToken;

    private int balance;

    // todo: deprecate
    @Deprecated
    private float rating;

    public User() {
    }

    // todo: deprecate
    @Deprecated
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

    private String image;

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

    @Override
    @Exclude
    public EnumField[] getDirtyFieldSet() {
        return dirtyFieldSet.toArray(new EnumField[0]);
    }

    // region setter and getter
    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        addDirtyField(EnumField.USER_REFERENCE);
        this.userReference = userReference;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        addDirtyField(EnumField.USERNAME);
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        addDirtyField(EnumField.PASSWORD);
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        addDirtyField(EnumField.EMAIL);
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        addDirtyField(EnumField.FIRST_NAME);
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        addDirtyField(EnumField.LAST_NAME);
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        addDirtyField(EnumField.PHONE_NUMBER);
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        addDirtyField(EnumField.IMAGE);
        this.image = image;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        addDirtyField(EnumField.FCM_TOKEN);
        this.FCMToken = FCMToken;
    }
    // endregion setter and getter
}
