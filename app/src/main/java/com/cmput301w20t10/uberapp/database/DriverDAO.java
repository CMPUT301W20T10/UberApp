package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for Driver model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 */
public class DriverDAO extends DAOBase<DriverEntity, Driver> {
    static final String COLLECTION = "drivers";
    final static String LOC = "Tomate: DriverDAO: ";

    DriverDAO() {}

    /**
     * Registers a driver.
     *
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param image
     * @param owner
     * @return
     * Returns a MutableLiveData object. To observe a MutableLiveData object:
     *
     * <pre>
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      DAO dao = db.getDAO();
     *      MutableLiveData<Model> liveData = dao.getModel(...);
     *      liveData.observe(this, model -> {
     *          // receive model inside here
     *      });
     * </pre>
     *
     * When observed, the object may receive model as the following:
     * <li>
     *     <ul><b>Non-null Driver object:</b> the Driver object's fields were successfully added to the database.</ul>
     *     <ul><b>Null:</b> Registration failed.</ul>
     * </li>
     */
    @Nullable
    public MutableLiveData<Driver> registerDriver(String username,
                                                  String password,
                                                  String email,
                                                  String firstName,
                                                  String lastName,
                                                  String phoneNumber,
                                                  String image,
                                                  LifecycleOwner owner) {
        RegisterDriverTask task = new RegisterDriverTask(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image,
                owner);
        return task.run();
    }

    /**
     * Attempts to log the user in as a driver.
     *
     * @param username
     * @param password
     * @param owner
     * @return
     * Returns a MutableLiveData object. To observe a MutableLiveData object:
     *
     * <pre>
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      DAO dao = db.getDAO();
     *      MutableLiveData<Model> liveData = dao.getModel(...);
     *      liveData.observe(this, model -> {
     *          // receive model inside here
     *      });
     * </pre>
     *
     * When observed, the object may receive model as the following:
     * <li>
     *     <ul><b>Non-null Driver object:</b> Log in was successful.</ul>
     *     <ul><b>Null:</b> Log in was unsuccessful.</ul>
     * </li>
     */
    public LiveData<Driver> logInAsDriver(String username, String password, LifecycleOwner owner) {
        LogInAsDriverTask task = new LogInAsDriverTask(owner, username, password);
        return task.run();
    }

    /**
     * Saves changes in driverEntity
     * @param driverEntity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    public MutableLiveData<Boolean> saveEntity(final DriverEntity driverEntity) {
        final DocumentReference reference = driverEntity.getDriverReference();
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        if (reference != null) {
            final Map<String, Object> dirtyFieldMap = driverEntity.getDirtyFieldMap();
            reference.update(dirtyFieldMap)
            .addOnCompleteListener(task -> {
                final boolean isSuccessful = task.isSuccessful();
                if (isSuccessful) {
                    Log.e(TAG, "saveEntity: ", task.getException());
                }
                liveData.setValue(isSuccessful);
            });
        } else {
            Log.e(TAG, LOC + "saveEntity: Reference is null");
            liveData.setValue(false);
        }

        return liveData;
    }

    @Override
    public MutableLiveData<Boolean> saveModel(Driver driver) {
        return null;
    }

    public LiveData<Boolean> saveModel(LifecycleOwner owner, Driver driver) {
        SaveDriverModelTask saveDriverModelTask = new SaveDriverModelTask(owner, driver);
        return saveDriverModelTask.run();
    }

    public MutableLiveData<Driver> getDriverFromDriverReference(DocumentReference driverReference) {
        GetDriverFromReferenceTask task = new GetDriverFromReferenceTask(driverReference);
        return task.run();
    }

    public MutableLiveData<Boolean> rateDriver(Driver driver, int increment) {
        driver.incrementRating(increment);
        return saveModel(driver);
    }
}

class SaveDriverModelTask extends GetTaskSequencer<Boolean> {
    final static String LOC = "Tomate: DriverDAO: SaveModel: ";
    private final DriverEntity driverEntity;
    private final UserEntity userEntity;
    private final LifecycleOwner owner;
    private final Driver driver;

    SaveDriverModelTask(LifecycleOwner owner, Driver driver) {
        this.owner = owner;
        this.driver = driver;

        driverEntity = new DriverEntity();
        userEntity = new UserEntity();
        driver.transferChanges(driverEntity);
        driver.clearDirtyStateSet();
    }

    @Override
    public MutableLiveData<Boolean> run() {
        updateDriverEntity();
        return liveData;
    }

    private void updateDriverEntity() {
        DriverDAO driverDAO = new DriverDAO();
        driverDAO.saveEntity(driverEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        updateUserEntity();
                    } else {
                        Log.e(TAG, LOC + "updateDriverEntity: onChanged: ");
                        liveData.setValue(false);
                    }
                });
    }

    private void updateUserEntity() {
        UserDAO userDAO = new UserDAO();
        userDAO.saveModel(driver)
        .observe(owner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (!aBoolean) {
                    Log.e(TAG, LOC +"onChanged: ");
                }
                liveData.setValue(false);
            }
        });
    }
}

class GetDriverFromReferenceTask extends GetTaskSequencer<Driver> {
    static final String LOC = DriverDAO.LOC + "GetDriverFromReferenceTask: ";

    private final DocumentReference driverReference;
    private DriverEntity driverEntity;

    GetDriverFromReferenceTask(DocumentReference drivereference) {
        this.driverReference = drivereference;
    }

    @Override
    public MutableLiveData<Driver> run() {
        getDriverEntity();
        return liveData;
    }

    private void getDriverEntity() {
        if (driverReference != null) {
            driverReference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            this.driverEntity = task.getResult().toObject(DriverEntity.class);
                            DocumentReference userReference = driverEntity.getUserReference();
                            getUserEntity(userReference);
                        } else {
                            Log.e(TAG, LOC + "getDriverEntity: ", task.getException());
                        }
                    });
        } else {
            Log.e(TAG, LOC + "getDriverEntity: reference null");
            liveData.setValue(null);
        }
    }

    private void getUserEntity(DocumentReference userReference) {
        if (userReference != null) {

            userReference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserEntity userEntity = task.getResult().toObject(UserEntity.class);
                            Driver driver = new Driver(driverEntity.getDriverReference(),
                                    driverEntity.getTransactionList(),
                                    driverEntity.getFinishedRideRequestList(),
                                    driverEntity.getActiveRideRequestList(),
                                    userEntity.getUsername(),
                                    userEntity.getPassword(),
                                    userEntity.getEmail(),
                                    userEntity.getFirstName(),
                                    userEntity.getLastName(),
                                    userEntity.getPhoneNumber(),
                                    driverEntity.getRating(),
                                    userEntity.getImage());
                            liveData.setValue(driver);
                        } else {
                            Log.e(TAG, LOC + "getUserEntity: onComplete: ", task.getException());
                            liveData.setValue(null);
                        }
                    });
        } else {
            Log.e(TAG, LOC + "getUserEntity: No User Entity");
            liveData.setValue(null);
        }
    }
}

class RegisterDriverTask extends GetTaskSequencer<Driver> {
    static final String LOC = "Tomate: RegisterDriverTask: ";

    private final String username;
    private final String password;
    private final String email;
    private final String firstName;
    private final String lastName;
    private final String phoneNumber;
    private final String image;
    private final LifecycleOwner owner;

    private DriverEntity driverEntity;
    private UserEntity userEntity;

    UserDAO userDAO;

    public RegisterDriverTask(String username,
                              String password,
                              String email,
                              String firstName,
                              String lastName,
                              String phoneNumber,
                              String image,
                              LifecycleOwner owner) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.owner = owner;
    }

    @Override
    public MutableLiveData<Driver> run() {
        createDriverEntity();
        return liveData;
    }

    private void createDriverEntity() {
        driverEntity = new DriverEntity();
        db.collection(DriverDAO.COLLECTION)
                .add(driverEntity)
                .addOnSuccessListener(documentReference -> {
                    driverEntity.setDriverReference(documentReference);
                    createUser();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, LOC + "createDriverEntity: onFailure: ", e);
                    liveData.setValue(null);
                });

    }

    private void createUser() {
        userDAO = new UserDAO();
        userDAO.registerUser(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image)
                .observe(owner, userEntity -> {
                    if (userEntity != null) {
                        RegisterDriverTask.this.userEntity = userEntity;
                        updateDriverEntity();
                    } else {
                        Log.e(TAG, LOC + "createUser: ");
                        liveData.setValue(null);
                    }
                });
    }

    private void updateDriverEntity() {
        driverEntity.setUserReference(userEntity.getUserReference());
        DriverDAO driverDAO = new DriverDAO();
        driverDAO.saveEntity(driverEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        updateUserEntity();
                    } else {
                        Log.e(TAG, LOC + "updateDriverEntity: onChanged: ");
                        liveData.setValue(null);
                    }
                });
    }

    private void updateUserEntity() {
        userEntity.setDriverReference(driverEntity.getDriverReference());
        userDAO.saveEntity(userEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        Driver driver = new Driver(driverEntity.getDriverReference(),
                                driverEntity.getTransactionList(),
                                driverEntity.getFinishedRideRequestList(),
                                driverEntity.getActiveRideRequestList(),
                                userEntity.getUsername(),
                                userEntity.getPassword(),
                                userEntity.getEmail(),
                                userEntity.getFirstName(),
                                userEntity.getLastName(),
                                userEntity.getPhoneNumber(),
                                driverEntity.getRating(),
                                userEntity.getImage());
                        liveData.setValue(driver);
                    } else {
                        Log.e(TAG, LOC + "updateUserEntity: ");
                        liveData.setValue(null);
                    }
                });
    }
}

class LogInAsDriverTask extends GetTaskSequencer<Driver> {
    static final String LOC = DriverDAO.LOC + "LogInAsDriverTask";

    private final String password;
    private final String username;
    private final LifecycleOwner owner;
    private UserEntity userEntity;
    private DriverEntity driverEntity;

    LogInAsDriverTask(LifecycleOwner owner, String username, String password) {
        this.owner = owner;
        this.username = username;
        this.password = password;
    }

    @Override
    public MutableLiveData<Driver> run() {
        userLogin();
        return liveData;
    }

    private void userLogin() {
        UserDAO userDAO = new UserDAO();
        userDAO.logIn(username, password)
                .observe(owner, userEntity -> {
                    if (userEntity == null || userEntity.getDriverReference() == null) {
                        Log.e(TAG, LOC + "userLogin: ");
                        liveData.setValue(null);
                    } else {
                        this.userEntity = userEntity;
                        getDriverEntity();
                    }
                });
    }

    private void getDriverEntity() {
        userEntity.getDriverReference()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        convertToModel();
                        driverEntity = task.getResult().toObject(DriverEntity.class);
                        convertToModel();
                    } else {
                        Log.e(TAG, LOC +"getDriverEntity: onComplete: ", task.getException());
                        liveData.setValue(null);
                    }
                });
    }

    private void convertToModel() {
        if (driverEntity == null) {
            Log.e(TAG, "convertToModel: driverEntity is null");
            liveData.setValue(null);
        } else {
            Driver driver = new Driver(driverEntity.getDriverReference(),
                    driverEntity.getTransactionList(),
                    driverEntity.getFinishedRideRequestList(),
                    driverEntity.getActiveRideRequestList(),
                    userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.getEmail(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.getPhoneNumber(),
                    driverEntity.getRating(),
                    userEntity.getImage());
            liveData.setValue(driver);
        }
    }
}