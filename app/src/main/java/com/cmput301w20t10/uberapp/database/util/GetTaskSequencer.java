package com.cmput301w20t10.uberapp.database.util;

import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.MutableLiveData;

public abstract class GetTaskSequencer<T> {
    protected MutableLiveData<T> liveData;
    protected FirebaseFirestore db;

    public GetTaskSequencer() {
        db = FirebaseFirestore.getInstance();
        liveData = new MutableLiveData<T>();
    }

    public abstract  MutableLiveData<T> run();
}
