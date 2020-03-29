package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.EnumField;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for User model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 */
public class UserDAO extends DAOBase<UserEntity> {
    private static final String COLLECTION = "users";
    final static String LOC = "Tomate: UserDAO: ";

    /**
     * Log the user in
     *
     * @param username
     * @param password
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
     *     <ul><b>Non-null UserEntity object:</b> Log in was successful.</ul>
     *     <ul><b>Null:</b> Log in failed.</ul>
     * </li>
     */
    MutableLiveData<UserEntity> logIn(String username, String password) {
        MutableLiveData<UserEntity> userLiveData = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        System.out.println("Username: " + username);
        db.collection(COLLECTION)
                .whereEqualTo(UserEntity.Field.USERNAME.toString(), username)
                .whereEqualTo(UserEntity.Field.PASSWORD.toString(), password)
                .get()
                .addOnCompleteListener(task -> {
                    System.out.println("Task: " + task.getResult().getDocuments().get(0).get("userReference"));
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            userLiveData.setValue(null);
                        } else {
                            if (task.getResult().size() > 1) {
                                Log.w(TAG, "onComplete: This should not happen\nMore than one account found");
                            }

                            DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                            UserEntity userEntity = snapshot.toObject(UserEntity.class);
                            userLiveData.setValue(userEntity);
                        }
                    } else {
                        Log.d(TAG, "onComplete: ", task.getException());
                    }
                });

        return userLiveData;
    }

    /**
     * Don't call this on main thread
     *
     * @param username
     * @param password
     * @param email
     * @param firstName
     * @param lastName
     * @param phoneNumber
     * @return  null if user already registered
     */
    public MutableLiveData<UserEntity> registerUser(String username,
                                                    String password,
                                                    String email,
                                                    String firstName,
                                                    String lastName,
                                                    String phoneNumber,
                                                    String image) {
        MutableLiveData<UserEntity> userLiveData = new MutableLiveData<>();

        // todo: validate if user was already registered

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserEntity userEntity = new UserEntity(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image);
        db.collection(COLLECTION)
                .add(userEntity)
                .addOnSuccessListener(
                        userReference -> {
                            if (userReference != null) {
                                userEntity.setUserReference(userReference);
                                UserDAO.this.saveEntity(userEntity);
                                userLiveData.setValue(userEntity);
                            } else {
                                Log.e(TAG, LOC + "registerUser: Failed to add user");
                            }
                        }
                )
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: ", e));

        return userLiveData;
    }

    /**
     * Saves changes in UserEntity
     *
     * @param userEntity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    @Override
    public Task<Void> saveEntity(final UserEntity userEntity) {
        final DocumentReference reference = userEntity.getUserReference();
        Task task = null;

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (UserEntity.Field field:
                 userEntity.getDirtyFieldSet()) {
                Object value = null;

                switch (field) {
                    case USERNAME:
                        value = userEntity.getUsername();
                        break;
                    case PASSWORD:
                        value = userEntity.getPassword();
                        break;
                    case EMAIL:
                        value = userEntity.getEmail();
                        break;
                    case FIRST_NAME:
                        value = userEntity.getFirstName();
                        break;
                    case LAST_NAME:
                        value = userEntity.getLastName();
                        break;
                    case PHONE_NUMBER:
                        value = userEntity.getPhoneNumber();
                        break;
                    case DRIVER_REFERENCE:
                        value = userEntity.getDriverReference();
                        break;
                    case RIDER_REFERENCE:
                        value = userEntity.getRiderReference();
                        break;
                    case USER_REFERENCE:
                        value = userEntity.getUserReference();
                        break;
                    case IMAGE:
                        value = userEntity.getImage();
                        break;
                    default:
                        break;
                }

                if (value != null) {
                    dirtyPairMap.put(field.toString(), value);
                }
            }

            userEntity.clearDirtyStateSet();

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }
        }

        return task;
    }

    public Task saveModel(User model,
                          DocumentReference riderReference,
                          DocumentReference driverReference) {
        final DocumentReference reference = model.getUserReference();
        Task task = null;

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (EnumField field :
                    model.getDirtyFieldSet()) {
                Object value = null;

                switch (field) {
                    case USERNAME:
                        value = model.getUsername();
                        break;
                    case PASSWORD:
                        value = model.getPassword();
                        break;
                    case EMAIL:
                        value = model.getEmail();
                        break;
                    case FIRST_NAME:
                        value = model.getFirstName();
                        break;
                    case LAST_NAME:
                        value = model.getLastName();
                        break;
                    case PHONE_NUMBER:
                        value = model.getPhoneNumber();
                        break;
                    case DRIVER_REFERENCE:
                        value = driverReference;
                        break;
                    case RIDER_REFERENCE:
                        value = riderReference;
                        break;
                    case USER_REFERENCE:
                        value = model.getUserReference();
                        break;
                    case IMAGE:
                        value = model.getImage();
                        break;
                    default:
                        break;
                }

                if (value != null) {
                    dirtyPairMap.put(field.toString(), value);
                }
            }

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }

            model.clearDirtyStateSet();
        }

        return task;
    }
}
