package com.cmput301w20t10.uberapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.PaymentDAO;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static android.content.ContentValues.TAG;

/**
 *
 */
public class DatabaseManager  {
    private static final String PREF_FILE_KEY =
            "com.cmput201w20t10.uberapp.database.DatabaseManager.DB_PREFERENCE_FILE_KEY";
    private static final String PREF_DB_STATE = "db_state";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_PASSWORD = "password";

    //  it's volatile cause we're gonna run this in multiple threads
    // todo (allan): make a better explanation why you put volatile here
    private static volatile DatabaseManager INSTANCE = new DatabaseManager();
    private State state = State.LOGGED_OUT;
    private Rider rider = null;
    private Driver driver = null;

    public enum State {
        LOGGED_OUT,
        DRIVER,
        RIDER
    }

    /**
     * Minimum privilege
     * @return
     */
    @NonNull
    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    /**
     * Allows access to use riderDAO
     * @param context
     * @return
     */
    @Nullable
    public static DatabaseManager getInstance(Context context) {
        if (INSTANCE.verify(context)) {
            return INSTANCE;
        } else {
            return null;
        }
    }

    private DatabaseManager() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
    }

    private boolean verify(Context context) {
        SharedPreferences preferences = context
                .getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE);
        boolean isLoggedIn = State.LOGGED_OUT.ordinal() !=
                preferences.getInt(PREF_DB_STATE, State.LOGGED_OUT.ordinal());
        // todo: verification
        return isLoggedIn;
    }

    public RiderDAO getRiderDAO() {
        if (state != State.LOGGED_OUT) {
            return new RiderDAOImpl();
        } else {
            return null;
        }
    }

    public DriverDAO getDriverDAO() {
        if (state != State.LOGGED_OUT) {
            return new DriverDAOImpl();
        } else {
            return null;
        }
    }

    public PaymentDAO getPaymentDAO() {
        // todo {allan}: instantiate driver dao here
        return null;
    }

    public RideRequestDAO getRideRequestDAO() {
        // todo {allan}: instantiate driver dao here
        return null;
    }

    public MutableLiveData<Rider> logInAsRider(String username,
                                               String password,
                                               LifecycleOwner owner) {
        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();
        new RiderDAOImpl().logInAsRider(username, password, owner)
            .observe(owner, rider -> {
                state = State.RIDER;
                this.driver = null;
                this.rider = rider;
                riderLiveData.setValue(rider);
            });
        return riderLiveData;
    }

    public MutableLiveData<Rider> registerRider(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                LifecycleOwner owner) {
         RiderDAO riderDAO = new RiderDAOImpl();
         return riderDAO.registerRider(username,
                 password,
                 email,
                 firstName,
                 lastName,
                 phoneNumber,
                 owner);
    }

    public MutableLiveData<Driver> logInAsDriver(String username,
                                               String password,
                                               LifecycleOwner owner) {
        MutableLiveData<Driver> driverLiveData = new MutableLiveData<>();
        new DriverDAOImpl().logInAsDriver(username, password, owner)
                .observe(owner, driver -> {
                    state = State.DRIVER;
                    this.driver = driver;
                    this.rider = null;
                    driverLiveData.setValue(driver);
                });
        return driverLiveData;
    }

    public MutableLiveData<Driver> registerDriver(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                LifecycleOwner owner) {
        DriverDAO driverDAO = new DriverDAOImpl();
        return driverDAO.registerDriver(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                owner);
    }
}
