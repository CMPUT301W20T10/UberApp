package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for Rider model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 */
public class RiderDAO extends DAOBase<RiderEntity, Rider> {
    static final String LOC = "RiderDAO: ";
    static final String COLLECTION = "riders";

    RiderDAO() {}

    /**
     * Registers a rider
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
     *     <ul><b>Non-null Rider object:</b> the Rider object's fields were successfully added to the database.</ul>
     *     <ul><b>Null:</b> Registration failed.</ul>
     * </li>
     */
    @Nullable
    public MutableLiveData<Rider> registerRider(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                String image,
                                                @NonNull LifecycleOwner owner) {
        RegisterRiderTask task = new RegisterRiderTask(username,
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
     * Attempts to log the user in as a rider.
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
     *     <ul><b>Non-null Rider object:</b> Log in was successful.</ul>
     *     <ul><b>Null:</b> Log in was unsuccessful.</ul>
     * </li>
     */
    public MutableLiveData<Rider> logInAsRider(String username, String password, LifecycleOwner owner) {
        LogInAsRiderTask task = new LogInAsRiderTask(username, password, owner);
        return task.run();
    }

    @Override
    public MutableLiveData<Boolean> saveEntity(RiderEntity riderEntity) {
        final DocumentReference reference = riderEntity.getRiderReference();
        final MutableLiveData<Boolean> liveData = new MutableLiveData<>();

        if (reference != null) {
            final Map<String, Object> dirtyFieldMap = riderEntity.getDirtyFieldMap();
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
    public MutableLiveData<Boolean> saveModel(Rider rider) {
        return null;
    }

    MutableLiveData<Rider> getRiderFromRiderReference(DocumentReference riderReference) {
        GetRiderFromReferenceTask task = new GetRiderFromReferenceTask(riderReference);
        return task.run();
    }
}

class RegisterRiderTask extends GetTaskSequencer<Rider> {
    private static final String LOC = RiderDAO.LOC + "RegisterRiderTask: ";
    private String username;
    private String password;
    private String email;
    String firstName;
    String lastName;
    String phoneNumber;
    String image;
    @NonNull LifecycleOwner owner;

    UserEntity userEntity;
    RiderEntity riderEntity;
    RiderDAO riderDAO;

    RegisterRiderTask(String username,
                      String password,
                      String email,
                      String firstName,
                      String lastName,
                      String phoneNumber,
                      String image,
                      @NonNull LifecycleOwner owner) {
        super();
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.image = image;
        this.owner = owner;
    }

    public MutableLiveData<Rider> run() {
        addRiderEntity();
        return liveData;
    }

    // task 1
    private void addRiderEntity() {
        this.riderEntity = new RiderEntity();
        db.collection(RiderDAO.COLLECTION)
            .add(riderEntity)
            .addOnSuccessListener(this::updateRiderEntity)
        .addOnFailureListener(e -> {
            Log.e(TAG, "addRiderEntity: onFailure: ", e);
            liveData.setValue(null);
        });
    }

    // task 2
    private void updateRiderEntity(DocumentReference riderReference) {
        this.riderEntity.setRiderReference(riderReference);
        this.riderDAO = new RiderDAO();
        this.riderDAO.saveEntity(this.riderEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        registerRiderAsUser();
                    } else {
                        Log.e(TAG, "updateRiderEntity: ");
                        liveData.setValue(null);
                    }
                });
    }

    // task 3
    private void registerRiderAsUser() {
        UserDAO userDAO = new UserDAO();
        userDAO.registerUser(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image)
                .observe(owner, userEntity -> {
                    if (userEntity != null) {
                        this.userEntity = userEntity;
                        receiveUserEntity();
                    } else {
                        Log.e(TAG, "registerRiderAsUser: ");
                        liveData.setValue(null);
                    }
                });
    }

    // task 4
    private void receiveUserEntity() {
        if (userEntity != null && userEntity.getUserReference() != null) {
            UserDAO userDAO = new UserDAO();
            this.userEntity.setRiderReference(riderEntity.getRiderReference());
            userDAO.saveEntity(userEntity)
                    .observe(owner, aBoolean -> {
                        if (aBoolean) {
                            updateUserEntity();
                        } else {
                            Log.e(TAG, "receiveUserEntity: ");
                            liveData.setValue(null);
                        }
                    });
        }
    }

    // task 5
    private void updateUserEntity() {
        riderEntity.setUserReference(userEntity.getUserReference());
        this.riderDAO.saveEntity(riderEntity)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        Rider rider = new Rider(riderEntity.getRiderReference(),
                                riderEntity.getTransactionList(),
                                riderEntity.getFinishedRideRequestList(),
                                riderEntity.getActiveRideRequestList(),
                                userEntity.getUsername(),
                                userEntity.getPassword(),
                                userEntity.getEmail(),
                                userEntity.getFirstName(),
                                userEntity.getLastName(),
                                userEntity.getPhoneNumber(),
                                userEntity.getImage(),
                                riderEntity.getBalance());
                        liveData.setValue(rider);
                    } else {
                        Log.e(TAG, LOC + "receiveUserEntity: ");
                        liveData.setValue(null);
                    }
                });
    }
}

class LogInAsRiderTask extends GetTaskSequencer<Rider> {
    static final String LOC = DriverDAO.LOC + "LogInAsDriverTask";

    private final String password;
    private final String username;
    private final LifecycleOwner owner;
    private UserEntity userEntity;
    private RiderEntity riderEntity;

    LogInAsRiderTask(String username, String password, LifecycleOwner owner) {
        this.owner = owner;
        this.username = username;
        this.password = password;
    }

    @Override
    public MutableLiveData<Rider> run() {
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
                        getRiderEntity();
                    }
                });
    }

    private void getRiderEntity() {
        userEntity.getRiderReference()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        convertToModel();
                        riderEntity = task.getResult().toObject(RiderEntity.class);
                        convertToModel();
                    } else {
                        Log.e(TAG, LOC +"getDriverEntity: onComplete: ", task.getException());
                        liveData.setValue(null);
                    }
                });
    }

    private void convertToModel() {
        if (riderEntity == null) {
            Log.e(TAG, "convertToModel: driverEntity is null");
            liveData.setValue(null);
        } else {
            Rider rider = new Rider(riderEntity.getRiderReference(),
                    riderEntity.getTransactionList(),
                    riderEntity.getFinishedRideRequestList(),
                    riderEntity.getActiveRideRequestList(),
                    userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.getEmail(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    userEntity.getPhoneNumber(),
                    userEntity.getImage(),
                    riderEntity.getBalance());
            liveData.setValue(rider);
        }
    }
}

class GetRiderFromReferenceTask extends GetTaskSequencer<Rider> {
    static final String LOC = "RiderDAO: GetRiderFromReferenceTask: ";
    private final DocumentReference riderReference;
    private RiderEntity riderEntity;

    GetRiderFromReferenceTask(DocumentReference riderReference) {
        this.riderReference = riderReference;
    }

    @Override
    public MutableLiveData<Rider> run() {
        getRiderEntity();
        return liveData;
    }

    private void getRiderEntity() {
        if (riderReference != null) {
            riderReference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            this.riderEntity = task.getResult().toObject(RiderEntity.class);
                            DocumentReference userReference = riderEntity.getUserReference();
                            getUserEntity(userReference);
                        } else {
                            Log.e(TAG, LOC + "getRiderEntity: onComplete: ", task.getException());
                            liveData.setValue(null);
                        }
                    });
        } else {
            Log.e(TAG, LOC + "getRiderEntity: ");
            liveData.setValue(null);
        }
    }

    private void getUserEntity(DocumentReference userReference) {
        if (userReference != null) {
            userReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserEntity userEntity = task.getResult().toObject(UserEntity.class);
                        Rider rider = new Rider(riderEntity, userEntity);
                        liveData.setValue(rider);
                    } else {
                        Log.e(TAG, LOC + "getUserEntity: onComplete: ", task.getException());
                        liveData.setValue(null);
                    }
                });
        } else {
            liveData.setValue(null);
            Log.e(TAG, LOC + "getUserEntity: No UserEntity");
        }
    }

}