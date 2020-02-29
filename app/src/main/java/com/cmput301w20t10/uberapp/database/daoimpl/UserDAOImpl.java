package com.cmput301w20t10.uberapp.database.daoimpl;

import android.util.Log;
import android.widget.Toast;

import com.cmput301w20t10.uberapp.database.dao.UserDAO;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class UserDAOImpl implements UserDAO {
    public static final String COLLECTION_USERS = "users";

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
    public MutableLiveData<DocumentReference> registerUser(String username,
                                                           String password,
                                                           String email,
                                                           String firstName,
                                                           String lastName,
                                                           String phoneNumber) {
        MutableLiveData<DocumentReference> userLiveData = new MutableLiveData<>();

        // todo: validate if user was already registered here

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        UserEntity userEntity = new UserEntity(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber);
        db.collection(COLLECTION_USERS)
                .add(userEntity)
                .addOnSuccessListener(documentReference -> documentReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    userLiveData.setValue(documentSnapshot.getReference());
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "onFailure: ", e);
                }))
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
}
