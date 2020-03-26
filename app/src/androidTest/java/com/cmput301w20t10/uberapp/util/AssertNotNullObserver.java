package com.cmput301w20t10.uberapp.util;

import androidx.lifecycle.Observer;

import static org.junit.Assert.assertNotNull;

public class AssertNotNullObserver<Result> implements Observer<Result> {
    private final Object syncObject;

    public AssertNotNullObserver(Object syncObject) {
        this.syncObject = syncObject;
    }

    @Override
    public void onChanged(Result result) {
        assertNotNull(result);

        synchronized (syncObject) {
            syncObject.notify();
        }
    }
}
