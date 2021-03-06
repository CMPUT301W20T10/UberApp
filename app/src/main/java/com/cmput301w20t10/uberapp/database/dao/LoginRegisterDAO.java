package com.cmput301w20t10.uberapp.database.dao;

import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

/**
 * Data Access Object (DAO) for all login and registration related functions.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 * @version 1.4.2
 * Add dependency injection
 *
 * @version 1.1.1
 */
public class LoginRegisterDAO {
    private final FirebaseFirestore db;

    public LoginRegisterDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public LoginRegisterDAO(FirebaseFirestore db) {
        this.db  = db;
    }

    //region rider related
    /**
     * Logs the user in as a rider and checks if the given credentials are valid.
     * <p>
     * Sample code:
     * <p>
     * <pre>
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      LoginRegisterDAO dao = db.getLoginRegisterDAO();
     *      MutableLiveData<Rider> rider = dao.logInAsRider(...);
     *      rider.observe(this, rider -> {...});
     * </pre>
     *
     * @param username
     * @param password
     * @param owner
     * @return
     * Returns a MutableLiveData<Rider> object which should be observed.
     * <ul>
     *     <li>If the credentials are valid, the observer receives a Rider object</li>
     *     <li>Otherwise, the observer receives null</li>
     * </ul>
     */
    public MutableLiveData<Rider> logInAsRider(String username,
                                               String password,
                                               LifecycleOwner owner) {
        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();
        new RiderDAO(db).logInAsRider(username, password, owner)
                .observe(owner, rider -> {
                    riderLiveData.setValue(rider);
                    DatabaseManager.getInstance().updateUser(rider);
                });
        return riderLiveData;
    }

    /**
     * Registers the user as a rider.
     * <p>
     * Sample code:
     * <p>
     *  <pre>
     *      // for consistency with other methods
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      LoginRegisterDAO dao = db.getLoginRegisterDAO();
     *      MutableLiveData<Rider> rider = dao.RegisterRider(...);
     *      rider.observe(this, rider -> {...});
     *  </pre>
     *
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param owner
     * @return
     * Returns a MutableLiveData<Rider> object which should be observed.
     * <ul>
     *     <li>If the credentials are valid, the observer receives a Rider object</li>
     *     <li>Otherwise, the observer receives null</li>
     * </ul>
     */
    public MutableLiveData<Rider> registerRider(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                String image,
                                                LifecycleOwner owner) {
        RiderDAO riderDAO = new RiderDAO(db);
        return riderDAO.registerRider(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image,
                owner);
    }
    //endregion driver related

    // region driver related
    /**
     * Logs the user in as a driver and checks if the given credentials are valid.
     * <p>
     * Sample code:
     * <p>
     * <pre>
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      LoginRegisterDAO dao = db.getLoginRegisterDAO();
     *      MutableLiveData<Driver> driver = dao.logInAsRider(...);
     *      rider.observe(this, rider -> {...});
     * </pre>
     *
     * @param username
     * @param password
     * @param owner
     * @return
     * Returns a MutableLiveData<Rider> object which should be observed.
     * <ul>
     *     <li>If the credentials are valid, the observer receives a Rider object</li>
     *     <li>Otherwise, the observer receives null</li>
     * </ul>
     */
    public MutableLiveData<Driver> logInAsDriver(String username,
                                                 String password,
                                                 LifecycleOwner owner) {
        MutableLiveData<Driver> driverLiveData = new MutableLiveData<>();
        new DriverDAO(db).logInAsDriver(username, password, owner)
                .observe(owner, driver -> {
                    driverLiveData.setValue(driver);
                    DatabaseManager.getInstance().updateUser(driver);
                });
        return driverLiveData;
    }

    /**
     * Registers the user as a driver and checks if the given credentials are valid.
     * <p>
     * Sample code:
     * <p>
     * <pre>
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      LoginRegisterDAO dao = db.getLoginRegisterDAO();
     *      MutableLiveData<Driver> driver = dao.registerDriver(...);
     *      registerDriver.observe(this, driver -> {...});
     * </pre>
     *
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param owner
     * @return
     * Returns a MutableLiveData<Rider> object which should be observed.
     * <ul>
     *     <li>If the credentials are valid, the observer receives a Rider object</li>
     *     <li>Otherwise, the observer receives null</li>
     * </ul>
     */
    public MutableLiveData<Driver> registerDriver(String username,
                                                  String password,
                                                  String email,
                                                  String firstName,
                                                  String lastName,
                                                  String phoneNumber,
                                                  String image,
                                                  LifecycleOwner owner) {
        DriverDAO driverDAO = new DriverDAO(db);
        return driverDAO.registerDriver(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image,
                owner);
    }
    // endregion
}
