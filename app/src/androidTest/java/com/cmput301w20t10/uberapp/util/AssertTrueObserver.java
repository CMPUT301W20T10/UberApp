package com.cmput301w20t10.uberapp.util;

import com.cmput301w20t10.uberapp.util.AssertObserverBase;

public class AssertTrueObserver extends AssertObserverBase<Boolean> {
    public AssertTrueObserver(Object syncObject) {
        super(syncObject);
    }

    @Override
    public void callAssertion(Boolean aBoolean) {
        assert aBoolean;
    }
}
