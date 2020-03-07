package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

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
    private static final String COLLECTION = "riders";

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
        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();
        RiderEntity riderEntity = new RiderEntity();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // todo: check if rider was already registered

        db.collection(COLLECTION)
                .add(riderEntity)
                .addOnSuccessListener(riderReference -> {
                    riderEntity.setRiderReference(riderReference);
                    this.save(riderEntity);

                    registerRiderAsUser(
                            riderLiveData,
                            riderEntity,
                            riderLiveData,
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
        return riderLiveData;
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
        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();

        UserDAO userDAO = new UserDAO();
        userDAO.logIn(username, password)
                .observe(owner, userEntity -> {
                    if (userEntity == null || userEntity.getRiderReference() == null) {
                        riderLiveData.setValue(null);
                    } else {
                        // this gets rider using user reference
                        userEntity.getRiderReference()
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        RiderEntity riderEntity = task.getResult()
                                                .toObject(RiderEntity.class);
                                        Rider rider = new Rider(riderEntity, userEntity);
                                        riderLiveData.setValue(rider);
                                    } else {
                                        riderLiveData.setValue(null);
                                    }
                                });
                    }
                });

        return riderLiveData;
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
    public Task save(final RiderEntity riderEntity) {
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

    public Task save(Rider rider) {
        final DocumentReference reference = rider.getRiderReference();
        Task task = null;
        Log.d(TAG, "save: "+ reference.getPath());
        Log.d(TAG, "save: " + Arrays.toString(rider.getDirtyFieldSet()));

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (Rider.Field field:
                    rider.getDirtyFieldSet()) {
                Object value = null;

                switch (field) {
                    case PAYMENT_LIST:
                        value = rider.getPaymentList();
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
        if (riderReference != null) {
            MutableLiveData<Rider> liveData = new MutableLiveData<>();

            riderReference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            RiderEntity riderEntity = task.getResult().toObject(RiderEntity.class);
                            DocumentReference userReference = riderEntity.getUserReference();
                            userReference.get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            UserEntity userEntity = task1.getResult().toObject(UserEntity.class);
                                            Rider rider = new Rider(riderEntity, userEntity);
                                            liveData.setValue(rider);
                                        } else {
                                            Log.e(TAG, "onComplete: ", task1.getException());
                                            liveData.setValue(null);
                                        }
                                    });
                        } else {
                            Log.e(TAG, "onComplete: ", task.getException());
                            liveData.setValue(null);
                        }
                    });

            return liveData;
        } else {
            return null;
        }
    }
}
