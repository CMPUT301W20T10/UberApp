package com.cmput301w20t10.uberapp.database.daoimpl;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.dao.UserDAO;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class UserDAOImpl implements UserDAO {
    public static final String COLLECTION_USERS = "users";

    public interface Callback {
        public void onRiderRegistered();
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
    @Override
    public MutableLiveData<UserEntity> registerUser(String username,
                                                    String password,
                                                    String email,
                                                    String firstName,
                                                    String lastName,
                                                    String phoneNumber) {
        MutableLiveData<UserEntity> userLiveData = new MutableLiveData<>();

        // todo: validate if user was already registered here

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
                        documentReference -> {
                            if (documentReference != null) {
                                UserEntity updatedUser = userLiveData.getValue();
                                updatedUser.setUserReference(documentReference);
                                userLiveData.setValue(updatedUser);
                            }
                        }
                )
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: ", e));

        return userLiveData;
    }

    @Override
    public void registerRider(DocumentReference riderReference,
                              DocumentReference userReference) {
        userReference.update(UserEntity.FIELD_RIDER_REFERENCE, riderReference)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: Registered rider"))
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: ", e));
    }

    @Override
    public void registerDriver(DocumentReference driverReference, DocumentReference userReference) {
        userReference.update(UserEntity.FIELD_DRIVER_REFERENCE, driverReference)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "onSuccess: Registered driver"))
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: ", e));
    }
}
