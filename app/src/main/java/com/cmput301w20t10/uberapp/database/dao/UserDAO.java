package com.cmput301w20t10.uberapp.database.dao;

import com.google.firebase.firestore.DocumentReference;

import androidx.lifecycle.MutableLiveData;

public interface UserDAO {
    MutableLiveData<DocumentReference> registerUser(String username,
                                             String password,
                                             String email,
                                             String firstName,
                                             String lastName,
                                             String phoneNumber);
    void registerRider(DocumentReference riderReference,
                       DocumentReference userReference);
}
