package com.cmput301w20t10.uberapp.database;

import android.app.Application;

import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.PaymentDAO;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.database.daoimpl.RiderDAOImpl;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;

/**
 * todo (allan): description here
 *
 * Reference: CMPUT 301 Project 1 CardioBook by Allan Manuba
 */
public class DatabaseManager  {
    //  it's volatile cause we're gonna run this in multiple threads
    // todo (allan): make a better explanation why you put volatile here
    private static volatile DatabaseManager INSTANCE = new DatabaseManager();

    @NonNull
    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    private DatabaseManager() {}

    public RiderDAO getRiderDAO() {
        return new RiderDAOImpl();
    }

    public DriverDAO getDriverDAO() {
        // todo {allan}: instantiate driver dao here
        return null;
    }

    public PaymentDAO getPaymentDAO() {
        // todo {allan}: instantiate driver dao here
        return null;
    }

    public RideRequestDAO getRideRequestDAO() {
        // todo {allan}: instantiate driver dao here
        return null;
    }

    /**
     * Retrieves a user from the firebase using the email as the given key
     * @param email - The user identification used to look up user information in the firebase
     * @return - The user object containing the appropriate data of the user from the given
     *              identifier
     */
    public User getUserData(String email) {
        // Todo (Joshua): retrieve the user data from the firebase
        return null;
    }

}
