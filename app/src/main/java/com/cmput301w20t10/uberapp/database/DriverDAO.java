package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Driver;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Map;

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

    public DriverDAO() {}

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

    @Override
    public MutableLiveData<Boolean> saveModel(Driver driver) {
        DriverEntity driverEntity = new DriverEntity();
        driver.transferChanges(driverEntity);
        return saveEntity(driverEntity);
    }

    @Override
    protected String getCollectionName() {
        return COLLECTION;
    }

    @Override
    protected DAOBase<DriverEntity, Driver> create() {
        return new DriverDAO();
    }

    // todo: deprecate the below task or make the task shorter and not redundant
    @Override
    protected MutableLiveData<Driver> createModelFromEntity(DriverEntity driverEntity) {
        GetDriverFromReferenceTask task = new GetDriverFromReferenceTask(driverEntity.getDriverReference());
        return task.run();
    }

    @Override
    protected DriverEntity createObjectFromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(DriverEntity.class);
    }

    public LiveData<Boolean> saveModel(LifecycleOwner owner, Driver driver) {
        SaveDriverModelTask saveDriverModelTask = new SaveDriverModelTask(owner, driver);
        return saveDriverModelTask.run();
    }

    public static MutableLiveData<Driver> getDriverFromDriverReference(DocumentReference driverReference) {
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
        driver.clearDirtyFieldSet();
    }

    @Override
    public void doFirstTask() {
        updateDriverEntity();
    }

    private void updateDriverEntity() {
        DriverDAO driverDAO = new DriverDAO();
        driverDAO.saveEntity(driverEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        updateUserEntity();
                    } else {
                        Log.e(TAG, LOC + "updateDriverEntity: onChanged: ");
                        postResult(false);
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
                postResult(false);
            }
        });
    }
}

class GetDriverFromReferenceTask extends GetTaskSequencer<Driver> {
    static final String LOC = DriverDAO.LOC + "GetDriverFromReferenceTask: ";

    private final DocumentReference driverReference;
    private DriverEntity driverEntity;

    GetDriverFromReferenceTask(DocumentReference driverReference) {
        this.driverReference = driverReference;
    }

    @Override
    public void doFirstTask() {
        getDriverEntity();
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
            postResult(null);
        }
    }

    private void getUserEntity(DocumentReference userReference) {
        if (userReference != null) {

            userReference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            UserEntity userEntity = task.getResult().toObject(UserEntity.class);
                            Driver driver = new Driver(driverEntity, userEntity);
                            postResult(driver);
                        } else {
                            Log.e(TAG, LOC + "getUserEntity: onComplete: ", task.getException());
                            postResult(null);
                        }
                    });
        } else {
            Log.e(TAG, LOC + "getUserEntity: No User Entity");
            postResult(null);
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
    public void doFirstTask() {
        createDriverEntity();
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
                    postResult(null);
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
                        postResult(null);
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
                        postResult(null);
                    }
                });
    }

    private void updateUserEntity() {
        userEntity.setDriverReference(driverEntity.getDriverReference());
        userDAO.saveEntity(userEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        Driver driver = new Driver(driverEntity, userEntity);
                        postResult(driver);
                    } else {
                        Log.e(TAG, LOC + "updateUserEntity: ");
                        postResult(null);
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
    public void doFirstTask() {
        userLogin();
    }

    private void userLogin() {
        UserDAO userDAO = new UserDAO();
        userDAO.logIn(username, password)
                .observe(owner, userEntity -> {
                    if (userEntity == null || userEntity.getDriverReference() == null) {
                        Log.e(TAG, LOC + ": userLogin: Failing with username: " + username);
                        postResult(null);
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
                        driverEntity = task.getResult().toObject(DriverEntity.class);
                        convertToModel();
                    } else {
                        Log.e(TAG, LOC +"getDriverEntity: onComplete: ", task.getException());
                        postResult(null);
                    }
                });
    }

    private void convertToModel() {
        if (driverEntity == null) {
            Log.e(TAG, "convertToModel: driverEntity is null");
            postResult(null);
        } else {
            Driver driver = new Driver(driverEntity, userEntity);
            postResult(driver);
        }
    }
}