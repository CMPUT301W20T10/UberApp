package com.cmput301w20t10.uberapp.database.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * A class that mocks Android's lifecycle forcing some operations to finish through even
 * if the activity of origin was already destroyed.
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
public class GetTaskSequencerLifecycleOwner implements LifecycleOwner {
    private LifecycleRegistry lifecycleRegistry;

    public GetTaskSequencerLifecycleOwner() {
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    public void callEvent(Lifecycle.Event event) {
        lifecycleRegistry.handleLifecycleEvent(event);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }
}
