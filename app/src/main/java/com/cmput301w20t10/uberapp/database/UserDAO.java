package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.base.ModelBase;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.EnumField;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for User model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 */
class UserDAO extends DAOBase<UserEntity, User> {
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
    // todo: improve
    LiveData<UserEntity> logIn(String username, String password) {
        MutableLiveData<UserEntity> userLiveData = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .whereEqualTo(UserEntity.Field.USERNAME.toString(), username)
                .whereEqualTo(UserEntity.Field.PASSWORD.toString(), password)
                .get()
                .addOnCompleteListener(task -> {
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
    // todo: improve
    public LiveData<UserEntity> registerUser(String username,
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
    public MutableLiveData<Boolean> saveEntity(final UserEntity userEntity) {
        MutableLiveData<Boolean> liveData = new MutableLiveData<>();
        DocumentReference userReference = userEntity.getUserReference();

        if (userReference != null) {
            Map<String, Object> fieldMap = userEntity.getDirtyFieldMap();
            userEntity.clearDirtyStateSet();
            userReference.update(fieldMap)
                    .addOnCompleteListener(task -> {
                        boolean isSuccessful = task.isSuccessful();
                        if (!isSuccessful) {
                            Log.e(TAG, LOC + "saveEntity: onComplete: ", task.getException());
                        }
                        liveData.setValue(isSuccessful);
                    });
        } else {
            liveData.setValue(false);
        }

        return liveData;
    }

    @Override
    public MutableLiveData<Boolean> saveModel(User model) {
        final DocumentReference reference = model.getUserReference();

        if (reference != null) {
            UserEntity userEntity = new UserEntity();
            model.transferChanges(userEntity);
            return saveEntity(userEntity);
        } else {
            Log.e(TAG, LOC + "saveModel: Reference is null");
            MutableLiveData<Boolean> liveData = new MutableLiveData<>();
            liveData.setValue(false);
            return liveData;
        }
    }
}
