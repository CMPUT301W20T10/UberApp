package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

class UserDAO {
    private static final String COLLECTION_USERS = "users";

    /**
     *
     * @param username
     * @param password
     * @return
     */
    MutableLiveData<UserEntity> logIn(String username, String password) {
        MutableLiveData<UserEntity> userLiveData = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_USERS)
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
        db.collection(COLLECTION_USERS)
                .add(userEntity)
                .addOnSuccessListener(
                        userReference -> {
                            if (userReference != null) {
                                userEntity.setUserReference(userReference);
                                UserDAO.this.save(userEntity);
                                userLiveData.setValue(userEntity);
                            }
                        }
                )
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: ", e));

        return userLiveData;
    }

    public Task save(final UserEntity userEntity) {
        final DocumentReference reference = userEntity.getUserReference();
        Task task = null;

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (UserEntity.Field field:
                 userEntity.getDirtyFieldList()) {
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

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }
        }

        return task;
    }
}
