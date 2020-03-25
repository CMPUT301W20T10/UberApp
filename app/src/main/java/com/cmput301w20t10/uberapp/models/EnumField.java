package com.cmput301w20t10.uberapp.models;

public enum EnumField {

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
    RIDE_REQUEST_LIST("rideRequest"),
    ACTIVE_RIDE_REQUEST_LIST("activeRideRequestList"),
    // driver field
    RATING("rating"),
    // rider field
    BALANCE("balance");

    private String stringValue;

    EnumField(String fieldName) {
        this.stringValue = fieldName;
    }

    public String toString() {
        return stringValue;
    }


}
