package com.cmput301w20t10.uberapp.database.dao;

import com.cmput301w20t10.uberapp.models.Payment;

public interface PaymentDAO {
    public Payment createPayment(String riderId, String rideRequest, int value);
}
