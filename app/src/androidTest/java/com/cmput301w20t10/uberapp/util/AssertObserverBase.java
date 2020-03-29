package com.cmput301w20t10.uberapp.util;

import java.util.concurrent.atomic.AtomicReference;

import androidx.lifecycle.Observer;

import static org.junit.Assert.assertNotNull;

public abstract class AssertObserverBase<Result> implements Observer<Result> {
    private final Object syncObject;
    private AtomicReference<Result> atomicReference;

    public AssertObserverBase(Object syncObject) {
        this.syncObject = syncObject;
    }

    public void onChanged(Result result) {
        if (atomicReference != null) {
            atomicReference.set(result);
        }

        callAssertion(result);

        synchronized (syncObject) {
            syncObject.notify();
        }
    }

    public abstract void callAssertion(Result result);

    public void setAtomicReference(AtomicReference<Result> atomicReference) {
        this.atomicReference = atomicReference;
    }
}
