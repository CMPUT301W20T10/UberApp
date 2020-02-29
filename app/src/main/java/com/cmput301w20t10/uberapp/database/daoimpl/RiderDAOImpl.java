package com.cmput301w20t10.uberapp.database.daoimpl;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.database.dao.UserDAO;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static android.content.ContentValues.TAG;

public class RiderDAOImpl implements RiderDAO {
    public static final String COLLECTION_RIDERS = "riders";

    @Override
    @Nullable
    public MutableLiveData<Rider> getRider(String username, String password) {
        return null;
    }

    @Nullable
    @Override
    public MutableLiveData<Rider> registerRider(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                @NonNull LifecycleOwner owner) {
        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();
        RiderEntity riderEntity = new RiderEntity();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_RIDERS)
                .add(riderEntity)
                .addOnSuccessListener(documentReference -> registerRiderAsUser(
                        documentReference,
                        riderLiveData,
                        username,
                        password,
                        email,
                        firstName,
                        lastName,
                        phoneNumber,
                        owner))
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: Error adding document", e));
        return riderLiveData;
    }

    private void registerRiderAsUser(DocumentReference riderReference,
                                     MutableLiveData<Rider> riderLiveData,
                                     String username,
                                     String password,
                                     String email,
                                     String firstName,
                                     String lastName,
                                     String phoneNumber,
                                     @NonNull LifecycleOwner owner){
        // create user then set driver
        UserDAO userDAO = new UserDAOImpl();
        userDAO.registerUser(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber)
        .observe(owner, userReference -> {
            if (userReference != null) {
                userDAO.registerRider(riderReference, userReference);
            }
        });
    }
}
