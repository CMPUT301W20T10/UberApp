package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for Rider model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 */
public class RiderDAO {
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
        LoginRiderTask task = new LoginRiderTask(username, password, owner);
        return task.run();
    }

    private void registerRiderAsUser(MutableLiveData<Rider> riderLiveDate,
                                     RiderEntity riderEntity,
                                     MutableLiveData<Rider> riderLiveData,
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
                        userEntity.setRiderReference(riderEntity.getRiderReference());
                        userDAO.saveEntity(userEntity);

                        riderEntity.setUserReference(userEntity.getUserReference());
                        save(riderEntity);
                        Rider rider = new Rider(riderEntity, userEntity);
                        riderLiveData.setValue(rider);
                    }
                });
    }

    /**
     * Saves changes in RiderEntity
     * @param riderEntity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    public Task<Void> save(final RiderEntity riderEntity) {
        final DocumentReference reference = riderEntity.getRiderReference();
        Task task = null;

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (RiderEntity.Field field:
                    riderEntity.getDirtyFieldSet()) {
                Object value = null;

                switch (field) {
                    case RIDER_REFERENCE:
                        value = riderEntity.getRiderReference();
                        break;
                    case USER_REFERENCE:
                        value = riderEntity.getUserReference();
                        break;
                    case PAYMENT_LIST:
                        value = riderEntity.getPaymentList();
                        break;
                    case RIDE_REQUEST_LIST:
                        value = riderEntity.getRideRequestList();
                        break;
                    case ACTIVE_RIDE_REQUEST_LIST:
                        value = riderEntity.getActiveRideRequestList();
                        break;
                    case BALANCE:
                        value = riderEntity.getBalance();
                        break;
                    default:
                        break;
                }

                if (value != null) {
                    dirtyPairMap.put(field.toString(), value);
                }
            }

            riderEntity.clearDirtyStateSet();

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }
        }

        return task;
    }

    // todo: improve
    public Task<Void> save(Rider rider) {
        final DocumentReference reference = rider.getRiderReference();
        Task<Void> task = null;
        Log.d(TAG, "save: "+ reference.getPath());
        Log.d(TAG, "save: " + Arrays.toString(rider.getDirtyFieldSet()));

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (Rider.Field field:
                    rider.getDirtyFieldSet()) {
                Object value = null;

                switch (field) {
                    case TRANSACTION_LIST:
                        value = rider.getTransactionList();
                        break;
                    case RIDE_REQUEST_LIST:
                        value = rider.getRideRequestList();
                        break;
                    case ACTIVE_RIDE_REQUEST_LIST:
                        Log.d(TAG, "save: In RiderDAO at ACTIVE RIDE");
                        value = rider.getActiveRideRequestList();
                        break;
                    case RIDER_REFERENCE:
                        value = rider.getRiderReference();
                        break;
                    case BALANCE:
                        value = rider.getBalance();
                        break;
                    default:
                        break;
                }

                if (value != null) {
                    dirtyPairMap.put(field.toString(), value);
                }
            }

            if (dirtyPairMap.size() > 0) {
                Log.d(TAG, "save: Here: " + reference.getPath());
                task = reference.update(dirtyPairMap);
            }

            // call user task
            UserDAO userDAO = new UserDAO();
            userDAO.saveModel(rider, reference, null);

            rider.clearDirtyStateSet();
        }

        return task;
    }

    MutableLiveData<Rider> getRiderFromRiderReference(DocumentReference riderReference) {
        GetRiderFromReferenceTask task = new GetRiderFromReferenceTask(riderReference);
        return task.run();
    }
}

class RegisterRiderTask extends GetTaskSequencer<Rider> {
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
            .addOnSuccessListener(this::updateRiderEntity);
    }

    // task 2
    private void updateRiderEntity(DocumentReference riderReference) {
        this.riderEntity.setRiderReference(riderReference);
        this.riderDAO = new RiderDAO();
        this.riderDAO.save(this.riderEntity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        registerRiderAsUser();
                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
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
                    this.userEntity = userEntity;
                    receiveUserEntity();
                });
    }

    // task 4
    private void receiveUserEntity(){
        if (userEntity != null && userEntity.getUserReference() != null) {
            UserDAO userDAO = new UserDAO();
            this.userEntity.setRiderReference(riderEntity.getRiderReference());
            userDAO.saveEntity(userEntity)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    updateUserEntity();
                } else {
                    Log.e(TAG, "onComplete: ", task.getException());
                }
            });
        } else {
            // todo: improve error message
            Log.e(TAG, "receiveUserEntity: ");
        }
    }

    // task 5
    private void updateUserEntity() {
        riderEntity.setUserReference(userEntity.getUserReference());
        this.riderDAO.save(riderEntity)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateLiveData();
                } else {
                    Log.e(TAG, "onComplete: ", task.getException());
                }
            }
        });
    }

    // task 6
    private void updateLiveData() {
        Rider rider = new Rider(riderEntity, userEntity);
        liveData.setValue(rider);
    }
}

class LoginRiderTask extends GetTaskSequencer<Rider> {
    String username;
    String password;
    LifecycleOwner owner;
    private UserEntity userEntity;

    LoginRiderTask(String username, String password, LifecycleOwner owner) {
        this.username = username;
        this.password = password;
        this.owner = owner;
    }

    @Override
    public MutableLiveData<Rider> run() {
        attemptLogin();
        return liveData;
    }

    // task 1
    private void attemptLogin() {
        UserDAO userDAO = new UserDAO();
        userDAO.logIn(username, password)
                .observe(owner, userEntity -> {
                    if (userEntity == null || userEntity.getRiderReference() == null) {
                        liveData.setValue(null);
                    } else {
                        this.userEntity = userEntity;
                        getRiderEntity();
                    }
                });
    }

    // task 2
    private void getRiderEntity() {
        // this gets rider using user reference
        userEntity.getRiderReference()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        RiderEntity riderEntity = task.getResult()
                                .toObject(RiderEntity.class);
                        Rider rider = new Rider(riderEntity, userEntity);
                        liveData.setValue(rider);
                    } else {
                        liveData.setValue(null);
                    }
                });
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