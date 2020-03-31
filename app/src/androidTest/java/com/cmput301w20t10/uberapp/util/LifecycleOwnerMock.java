package com.cmput301w20t10.uberapp.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;

/**
 * Mocks Android's LifecycleOwner
 *
 * Reference:
 *  Website Title: Android – Working with live data and custom life cycle owners.
 *  Author: Sushobh Nadiger
 *  Link: androidoverride.wordpress.com/2017/05/27/android-working-with-live-data-and-custom-life-cycle-owners/
 *
 * @author Allan Manuba
 * @version 1.0.1
 */
public class LifecycleOwnerMock implements LifecycleOwner {
    private LifecycleRegistry lifecycleRegistry;

    public LifecycleOwnerMock() {
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