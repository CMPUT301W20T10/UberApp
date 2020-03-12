package com.cmput301w20t10.uberapp.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * Decides what Data Access Object (DAO) can be access at the time
 *
 * @author Allan Manuba
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
    private AccessLevel accessLevel = AccessLevel.LOGGED_OUT;
    private Rider rider = null;
    private Driver driver = null;

    public enum AccessLevel {
        LOGGED_OUT,
        DRIVER,
        RIDER
    }

    /**
     * If the app was opened fresh, then it would seem like that the user's logged out even
     * if they're logged in. Current logged in state can also be ambiguous if this was used, allowing
     * unauthorized access to some DAO's.
     * Always call verify after getting the instance, or use getInstance(Context, LifecycleOwner)
     * @see #getInstance(Context, LifecycleOwner)
     *
     * @return
     * Returns a DatabaseManager instance without verification.
     */
    @NonNull
    public static DatabaseManager getInstance() {
        return INSTANCE;
    }

    /**
     * If the app was opened fresh, it would verify if the user was logged in previously.
     *
     * @param context
     * @param owner
     * @return
     * Returns a MutableLiveData<DatabaseManager> which should be observed.
     * <ul>
     *     <li>If the verification succeeds, the observer receives a DatabaseManager instance with the right access level</li>
     *     <li>If the verification does not succeed, the observer receives a null object</li>
     * </ul>
     */
    @Nullable
    public static MutableLiveData<DatabaseManager> getInstance(Context context, LifecycleOwner owner) {
        MutableLiveData<DatabaseManager> databaseLiveData = new MutableLiveData<>();

        INSTANCE.verify(context, owner)
                .observe(owner, isVerified -> {
                    if (isVerified) {
                        databaseLiveData.setValue(INSTANCE);
                    } else {
                        databaseLiveData.setValue(null);
                    }
                });

        return databaseLiveData;
    }

    private DatabaseManager() {
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.setFirestoreSettings(settings);
    }

    /**
     * Verifies the access level of the database manager
     *
     * @param context
     * @param owner
     * @return
     */
    private MutableLiveData<Boolean> verify(Context context, LifecycleOwner owner) {
        MutableLiveData<Boolean> boolLiveData = new MutableLiveData<>();

        SharedPreferences preferences = context
                .getSharedPreferences(PREF_FILE_KEY, Context.MODE_PRIVATE);
        accessLevel = AccessLevel.values()[preferences.getInt(PREF_DB_STATE, AccessLevel.LOGGED_OUT.ordinal())];
        boolean isLoggedIn = accessLevel != AccessLevel.LOGGED_OUT;
        String username = isLoggedIn ? preferences.getString(PREF_USERNAME, "") : "";
        String password = isLoggedIn ? preferences.getString(PREF_PASSWORD, "") : "";

        switch (accessLevel) {
            case LOGGED_OUT:
                INSTANCE.updateUser(null);
                break;
            case DRIVER:
                rider = null;
                DriverDAO driverDAO = new DriverDAO();
                driverDAO.logInAsDriver(username, password, owner)
                        .observe(owner, driver -> {
                            INSTANCE.updateUser(driver);
                            boolLiveData.setValue(INSTANCE.getAccessLevel() == AccessLevel.DRIVER);
                        });
                break;
            case RIDER:
                RiderDAO riderDAO = new RiderDAO();
                riderDAO.logInAsRider(username, password, owner)
                        .observe(owner, rider -> {
                            INSTANCE.updateUser(rider);
                            boolLiveData.setValue(INSTANCE.getAccessLevel() == AccessLevel.RIDER);
                        });
                break;
            default:
                Log.e(TAG, "verify: Unknown case: " + accessLevel.toString());
                INSTANCE.updateUser(null);
                break;
        }

        return boolLiveData;
    }

    /**
     * Gets the access level.
     * <ul>
     *     <li><b>LoggedOut:</b> Can only access RegisterLogInDAO</li>
     *     <li><b>Driver:</b> Logged in as driver</li>
     *     <li><b>Rider:</b> Logged in as rider</li>
     * </ul>
     *
     * @return
     */
    private AccessLevel getAccessLevel() {
        return accessLevel;
    }

    /**
     * Updates the current user.
     * This will control access to DAO's
     * For example:
     * <ul>
     *     <li>If logged out, only LogInRegisterDAO can be accessed</li>
     *     <li>If driver is logged in, only driver related DAO's can be accessed</li>
     *     <li>If rider is logged in, only rider related DAO's can be accessed</li>
     * </ul>
     *
     * @param user  Rider model or Driver model who is currently logged in
     *              If null, sets the access level to LoggedOut
     */
    public <U extends User> void updateUser(@Nullable U user) {
        if (user == null) {
            accessLevel = AccessLevel.LOGGED_OUT;
            rider = null;
            driver = null;
        } else if (user instanceof Rider) {
            accessLevel = AccessLevel.RIDER;
            this.driver = null;
            this.rider = (Rider) user;
        } else if (user instanceof Driver) {
            accessLevel = AccessLevel.DRIVER;
            this.driver = (Driver) user;
            this.rider = null;
        } else {
            Log.e(TAG, "updateUser: Unknown user type");
        }
    }

    // region DAO getters
    public RiderDAO getRiderDAO() {
        if (accessLevel != AccessLevel.LOGGED_OUT) {
            return new RiderDAO();
        } else {
            return null;
        }
    }

    public DriverDAO getDriverDAO() {
        if (accessLevel != AccessLevel.LOGGED_OUT) {
            return new DriverDAO();
        } else {
            return null;
        }
    }

    public TransactionDAO getTransactionDAO() {
        return new TransactionDAO();
    }

    public RideRequestDAO getRideRequestDAO() {
        // todo: do verifications here
        return new RideRequestDAO();
    }

    public LoginRegisterDAO getLoginRegisterDAO() {
        return new LoginRegisterDAO();
    }

    public UnpairedRideListDAO getUnpairedRideListDAO() {
        return new UnpairedRideListDAO();
    }
    // endregion DAO getters

    // region deprecated
    /**
     * @deprecated
     * logInAsRider was replaced by LogInRegisterDAO.logInAsRider.
     * Here's how to replace your code:
     * <p>
     *
     *  <pre>
     *      // for consistency with other methods
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      LoginRegisterDAO dao = db.getLoginRegisterDAO();
     *      MutableLiveData<Rider> rider = dao.logInAsRider(...);
     *      rider.observe(this, rider -> {...});
     *  </pre>
     * @see LoginRegisterDAO#logInAsRider(String, String, LifecycleOwner)
     */
    @Deprecated
    public MutableLiveData<Rider> logInAsRider(String username,
                                               String password,
                                               LifecycleOwner owner) {
        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();
        new RiderDAO().logInAsRider(username, password, owner)
            .observe(owner, rider -> {
                accessLevel = AccessLevel.RIDER;
                this.driver = null;
                this.rider = rider;
                riderLiveData.setValue(rider);
            });
        return riderLiveData;
    }

    /**
     * @deprecated
     * RegisterRider was replaced by LogInRegisterDAO.RegisterRider.
     * Here's how to replace your code:
     * <p>
     *
     *  <pre>
     *      // for consistency with other methods
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      LoginRegisterDAO dao = db.getLoginRegisterDAO();
     *      MutableLiveData<Rider> rider = dao.RegisterRider(...);
     *      rider.observe(this, rider -> {...});
     *  </pre>
     * @see LoginRegisterDAO#registerRider(String, String, String, String, String, String, String, LifecycleOwner)
     */
    @Deprecated
    public MutableLiveData<Rider> registerRider(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                LifecycleOwner owner) {
         RiderDAO riderDAO = new RiderDAO();
         return riderDAO.registerRider(username,
                 password,
                 email,
                 firstName,
                 lastName,
                 phoneNumber,
                 "",
                 owner);
    }

    /**
     * @deprecated
     * logInAsDriver was replaced by LogInRegisterDAO.logInAsDriver.
     * Here's how to replace your code:
     * <p>
     *
     *  <pre>
     *      // for consistency with other methods
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      LoginRegisterDAO dao = db.getLoginRegisterDAO();
     *      MutableLiveData<Driver> driver = dao.logInAsDriver(...);
     *      driver.observe(this, driver -> {...});
     *  </pre>
     * @see LoginRegisterDAO#logInAsDriver(String, String, LifecycleOwner)
     */
    @Deprecated
    public MutableLiveData<Driver> logInAsDriver(String username,
                                               String password,
                                               LifecycleOwner owner) {
        MutableLiveData<Driver> driverLiveData = new MutableLiveData<>();
        new DriverDAO().logInAsDriver(username, password, owner)
                .observe(owner, driver -> {
                    accessLevel = AccessLevel.DRIVER;
                    this.driver = driver;
                    this.rider = null;
                    driverLiveData.setValue(driver);
                });
        return driverLiveData;
    }

    /**
     * @deprecated
     * registerDriver was replaced by LogInRegisterDAO.logInAsDriver.
     * Here's how to replace your code:
     * <p>
     *
     *  <pre>
     *      // for consistency with other methods
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      LoginRegisterDAO dao = db.getLoginRegisterDAO();
     *      MutableLiveData<Driver> driver = dao.registerDriver(...);
     *      driver.observe(this, driver -> {...});
     *  </pre>
     * @see LoginRegisterDAO#registerRider(String, String, String, String, String, String, String, LifecycleOwner)
     */
    @Deprecated
    public MutableLiveData<Driver> registerDriver(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                LifecycleOwner owner) {
        DriverDAO driverDAO = new DriverDAO();
        return driverDAO.registerDriver(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                "",
                owner);
    }
    // endregion deprecated
}
