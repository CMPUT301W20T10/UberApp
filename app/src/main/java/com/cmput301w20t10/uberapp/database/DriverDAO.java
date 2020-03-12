package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for Driver model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 */
public class DriverDAO {
    private static final String COLLECTION_DRIVERS = "drivers";
    final static String LOC = "DriverDAO: ";

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
        // todo: improve code here

        MutableLiveData<Driver> driverLiveData = new MutableLiveData<>();
        DriverEntity driverEntity = new DriverEntity();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // todo: check if rider was already registered

        db.collection(COLLECTION_DRIVERS)
                .add(driverEntity)
                .addOnSuccessListener(driverReference -> {
                    driverEntity.setDriverReference(driverReference);
                    save(driverEntity);
                    registerDriverAsUser(
                            driverLiveData,
                            driverEntity,
                            username,
                            password,
                            email,
                            firstName,
                            lastName,
                            phoneNumber,
                            image,
                            owner);
                })
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: Error adding document", e));
        return driverLiveData;
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
        MutableLiveData<Driver> driverLiveData = new MutableLiveData<>();

        UserDAO userDAO = new UserDAO();
        userDAO.logIn(username, password)
                .observe(owner, userEntity -> {
                    if (userEntity == null || userEntity.getDriverReference() == null) {
                        driverLiveData.setValue(null);
                    } else {
                        userEntity.getDriverReference()
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        DriverEntity driverEntity = task.getResult()
                                                .toObject(DriverEntity.class);
                                        Driver driver = new Driver(driverEntity, userEntity);
                                        driverLiveData.setValue(driver);
                                    } else {
                                        driverLiveData.setValue(null);
                                    }
                                });
                    }
                });

        return driverLiveData;
    }

    /**
     * Helper function for registerDriver
     *
     * @param driverLiveData
     * @param driverEntity
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @param image
     * @param owner
     */
    private void registerDriverAsUser(MutableLiveData<Driver> driverLiveData,
                                      DriverEntity driverEntity,
                                      String username,
                                      String password,
                                      String email,
                                      String firstName,
                                      String lastName,
                                      String phoneNumber,
                                      String image,
                                      @NonNull LifecycleOwner owner){
        // create user then set driver
        UserDAO userDAO = new UserDAO();
        userDAO.registerUser(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image)
                .observe(owner, userEntity -> {
                    if (userEntity != null && userEntity.getUserReference() != null) {
                        userEntity.setDriverReference(driverEntity.getDriverReference());
                        userDAO.saveEntity(userEntity);
                        Driver driver = new Driver(driverEntity, userEntity);
                        driverLiveData.setValue(driver);
                    }
                });
    }

    /**
     * Saves changes in driverEntity
     * @param driverEntity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    public Task<Void> save(DriverEntity driverEntity) {
        final DocumentReference reference = driverEntity.getDriverReference();
        Task task = null;

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (DriverEntity.Field field:
                    driverEntity.getDirtyFieldSet()) {
                Object value = null;

                switch (field) {
                    case USER_REFERENCE:
                        value = driverEntity.getUserReference();
                        break;
                    case DRIVER_REFERENCE:
                        value = driverEntity.getDriverReference();
                        break;
                    case RATING:
                        value = driverEntity.getRating();
                        break;
                    case PAYMENT_LIST:
                        value = driverEntity.getPaymentList();
                        break;
                    case FINISHED_RIDE_REQUEST_LIST:
                        value = driverEntity.getFinishedRideRequestList();
                        break;
                    case ACTIVE_RIDE_REQUEST_LIST:
                        value = driverEntity.getActiveRideRequestList();
                        break;
                    default:
                        break;
                }

                if (value != null) {
                    dirtyPairMap.put(field.toString(), value);
                }
            }

            driverEntity.clearDirtyStateSet();

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }
        }

        return task;
    }

    public MutableLiveData<Boolean> saveModel(Driver driver) {
        SaveModelTask saveModelTask = new SaveModelTask(driver);
        return saveModelTask.run();
    }

    public MutableLiveData<Driver> getDriverFromDriverReference(DocumentReference driverReference) {
        return null;
    }
}

class SaveModelTask extends GetTaskSequencer<Boolean> {
    final static String LOC = "Tomate: DriverDAO: SaveModel: ";
    private final Driver driver;
    private final DriverEntity driverEntity;
    private final UserEntity userEntity;

    SaveModelTask(Driver driver) {
        this.driver = driver;
        driverEntity = new DriverEntity(driver);
        userEntity = new UserEntity(driver);
        driver.clearDirtyStateSet();
    }

    @Override
    public MutableLiveData<Boolean> run() {
        updateDriverEntity();
        return liveData;
    }

    private void updateDriverEntity() {
        DriverDAO driverDAO = new DriverDAO();
        Task driverSaveTask = driverDAO.save(driverEntity);
        if (driverSaveTask != null) {
            driverSaveTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, LOC + "updateDriverEntity: ");
                    updateUserEntity();
                } else {
                    Log.e(TAG, LOC + "updateDriverEntity: onComplete: ", task.getException());
                    liveData.setValue(false);
                }
            });
        } else {
            liveData.setValue(true);
        }
    }

    private void updateUserEntity() {
        UserDAO userDAO = new UserDAO();
        Task saveUserTask = userDAO.saveEntity(userEntity);
        if (saveUserTask != null) {
            saveUserTask.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d(TAG, LOC + "updateUserEntity: ");
                    liveData.setValue(true);
                } else {
                    Log.e(TAG, LOC + "updateUserEntity: onComplete: ", task.getException());
                    liveData.setValue(false);
                }
            });
        } else {
            liveData.setValue(true);
        }
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
                            Driver driver = new Driver(driverEntity, userEntity);
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