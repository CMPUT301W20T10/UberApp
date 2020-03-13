package com.cmput301w20t10.uberapp;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;

public class LifecycleMock extends Lifecycle {
    private State state = State.INITIALIZED;

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public void addObserver(@NonNull LifecycleObserver observer) {

    }

    @Override
    public void removeObserver(@NonNull LifecycleObserver observer) {

    }

    @NonNull
    @Override
    public State getCurrentState() {
        return state;
    }
}
