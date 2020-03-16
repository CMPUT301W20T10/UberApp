package com.cmput301w20t10.uberapp.database.util;

import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.MutableLiveData;

/**
 * Base class for tasks that are operated using Firestore's sequence of tasks.
 * Declutters nesting caused by observing or listening to Firestore tasks
 * to do another subsequent task.
 *
 * @param <T>   T is the Object type of the live data to be observed
 *
 * @author Allan Manuba
 * @version 1.0.0
 */
public abstract class GetTaskSequencer<T> {
    protected MutableLiveData<T> liveData;
    protected FirebaseFirestore db;

    public GetTaskSequencer() {
        db = FirebaseFirestore.getInstance();
        liveData = new MutableLiveData<T>();
    }

    public abstract MutableLiveData<T> run();
}
