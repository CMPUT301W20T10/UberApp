package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
                .whereEqualTo(UserEntity.FIELD_USERNAME, username)
                .whereEqualTo(UserEntity.FIELD_PASSWORD, password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                                                    String phoneNumber) {
        MutableLiveData<UserEntity> userLiveData = new MutableLiveData<>();

        // todo: validate if user was already registered

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserEntity userEntity = new UserEntity(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber);
        userLiveData.setValue(userEntity);
        db.collection(COLLECTION_USERS)
                .add(userEntity)
                .addOnSuccessListener(
                        userReference -> {
                            if (userReference != null) {
                                userReference.update(UserEntity.FIELD_USER_REFERENCE, userReference);
                                UserEntity updatedUser = userLiveData.getValue();
                                updatedUser.setUserReference(userReference);
                                userLiveData.setValue(updatedUser);
                            }
                        }
                )
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: ", e));

        return userLiveData;
    }

    public void registerRider(DocumentReference riderReference,
                              DocumentReference userReference) {
        userReference.update(UserEntity.FIELD_RIDER_REFERENCE, riderReference)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: Registered rider"))
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: ", e));
    }

    public void registerDriver(DocumentReference driverReference, DocumentReference userReference) {
        userReference.update(UserEntity.FIELD_DRIVER_REFERENCE, driverReference)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: Registered driver"))
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: ", e));
    }
}
