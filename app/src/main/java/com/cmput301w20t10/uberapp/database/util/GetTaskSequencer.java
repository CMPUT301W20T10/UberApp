package com.cmput301w20t10.uberapp.database.util;

import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.MutableLiveData;

/**
 * Base class for tasks that are operated using Firestore's sequence of tasks.
 * Declutters nesting caused by observing or listening to Firestore tasks
 * to do another subsequent task.
 *
 * @param <Result>   Object type of the live data to be observed
 *
 * @author Allan Manuba
 * @version 1.0.2
 * Add lifecycle handler
 *
 * @version 1.0.1
 */
public abstract class GetTaskSequencer<Result> {
    protected FirebaseFirestore db;
    private MutableLiveData<Result> liveData;
    private GetTaskSequencerLifecycleOwner lifecycleOwner;

    public GetTaskSequencer() {
        lifecycleOwner = new GetTaskSequencerLifecycleOwner();
        db = FirebaseFirestore.getInstance();
        liveData = new MutableLiveData<>();
    }

    public abstract void doFirstTask();

    /**
     * This is the only function that you should call after instantiating
     * GetTaskSequencer subclasses.
     *
     * @return  Live data to observed
     *
     * @author Allan Manuba
     * @version 1.0.2
     */
    public MutableLiveData<Result> run() {
        doFirstTask();
        return liveData;
    }

    protected void postResult(Result result) {
        liveData.setValue(result);
        lifecycleOwner.callEvent(Lifecycle.Event.ON_DESTROY);
    }
}
