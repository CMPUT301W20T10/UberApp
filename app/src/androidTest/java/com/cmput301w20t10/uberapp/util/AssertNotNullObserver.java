package com.cmput301w20t10.uberapp.util;

import androidx.lifecycle.Observer;

import static org.junit.Assert.assertNotNull;

public class AssertNotNullObserver<Result> extends AssertObserverBase<Result> {
    public AssertNotNullObserver(Object syncObject) {
        super(syncObject);
    }

    @Override
    public void callAssertion(Result result) {
        assertNotNull(result);
    }
}
