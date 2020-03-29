package com.cmput301w20t10.uberapp.models;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.ModelBase;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.cmput301w20t10.uberapp.models.User.*;

/**
 * This is a data holder class intended to hold the data regarding a user. Drawbacks to this class
 * involve the security risk of storing the password in a String format
 */
public class User extends ModelBase<Field, UserEntity> {
    private static final String LOC = "Tomate: User: ";
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

    private String image;

    /**
     * Cannot make this private because of Generics
     */
    enum Field {
        USER_REFERENCE("userReference"),
        USERNAME("username"),
        PASSWORD("password"),
        EMAIL("email"),
        FIRST_NAME("firstName"),
        LAST_NAME("lastName"),
        PHONE_NUMBER("phoneNumber"),
        IMAGE("image"),
        FCM_TOKEN("FCMToken"),
        // shadowed
        DRIVER_REFERENCE("driverReference"),
        RIDER_REFERENCE("riderReference"),
        // children fields
        TRANSACTION_LIST("paymentList"),
        FINISHED_RIDE_REQUEST_LIST("rideRequest"),
        ACTIVE_RIDE_REQUEST_LIST("activeRideRequestList"),
        // driver field
        RATING("rating"),
        // rider field
        BALANCE("balance");

        private String stringValue;

        Field(String fieldName) {
            this.stringValue = fieldName;
        }

        public String toString() {
            return stringValue;
        }
    }

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

    public User(DocumentReference userReference,
                String username,
                String password,
                String email,
                String firstName,
                String lastName,
                String phoneNumber,
                String image) {
        this.userReference = userReference;
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
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
        this.FCMToken = userEntity.getFCMToken();
    }

    @Override
    public void transferChanges(UserEntity userEntity) {
        for (Field dirtyField :
                dirtyFieldSet) {
            switch (dirtyField) {
                case TRANSACTION_LIST:
                case FINISHED_RIDE_REQUEST_LIST:
                case ACTIVE_RIDE_REQUEST_LIST:
                case RATING:
                case BALANCE:
                    // do nothing
                    break;
                case FCM_TOKEN:
                    userEntity.setFCMToken(getFCMToken());
                    break;
                case DRIVER_REFERENCE:
                    if (this instanceof Driver) {
                        Driver driver = (Driver) this;
                        userEntity.setDriverReference(driver.getDriverReference());
                    } else {
                        userEntity.setDriverReference(null);
                    }
                    break;
                case RIDER_REFERENCE:
                    if (this instanceof Rider) {
                        Rider rider = (Rider) this;
                        userEntity.setRiderReference(rider.getRiderReference());
                    } else {
                        userEntity.setUserReference(null);
                    }
                    break;
                case USER_REFERENCE:
                    // always true for the sake of main reference
                    // todo: document why this is done
                    break;
                case USERNAME:
                    userEntity.setUsername(getUsername());
                    break;
                case PASSWORD:
                    userEntity.setPassword(getPassword());
                    break;
                case EMAIL:
                    userEntity.setEmail(getEmail());
                    break;
                case FIRST_NAME:
                    userEntity.setFirstName(getFirstName());
                    break;
                case LAST_NAME:
                    userEntity.setLastName(getLastName());
                    break;
                case PHONE_NUMBER:
                    userEntity.setPhoneNumber(getPhoneNumber());
                    break;
                case IMAGE:
                    userEntity.setImage(getImage());
                    break;
                default:
                    Log.e(TAG, LOC + "transferChanges: Unknown field: " + dirtyField.toString());
                    break;
            }

            userEntity.setUserReference(getUserReference());
        }
    }

    // region setter and getter
    public DocumentReference getUserReference() {
        return userReference;
    }

    public void setUserReference(DocumentReference userReference) {
        addDirtyField(Field.USER_REFERENCE);
        this.userReference = userReference;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        addDirtyField(Field.USERNAME);
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        addDirtyField(Field.PASSWORD);
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        addDirtyField(Field.EMAIL);
        this.email = email;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        addDirtyField(Field.PHONE_NUMBER);
        this.phoneNumber = phoneNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        addDirtyField(Field.IMAGE);
        this.image = image;
    }

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        addDirtyField(Field.FCM_TOKEN);
        this.FCMToken = FCMToken;
    }
    // endregion setter and getter
}
