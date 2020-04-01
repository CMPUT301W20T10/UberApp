package com.cmput301w20t10.uberapp.util;

import android.os.Handler;

import java.util.concurrent.atomic.AtomicReference;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

/**
 * Create a better live data tester to remove boilerplate code in testing
 *
 * @param <Result>
 *
 * @author Allan Manuba
 * @version 1.7.1
 */
public abstract class LiveDataTester<Result> {
    private final Handler handler;
    private final AtomicReference<Result> atomicReference;
    private final Runnable runnable;
    private final Object syncObject;
    private final AssertObserverBase observer;
    private final LifecycleOwner owner;

    protected LiveDataTester(Handler handler,
                             AtomicReference<Result> atomicReference,
                             AssertObserverBase<Result> observer,
                             LifecycleOwner owner) {
        this.observer = observer;
        this.handler = handler;
        this.atomicReference = atomicReference;
        this.owner = owner;

        if (this.atomicReference != null) {
            this.observer.setAtomicReference(this.atomicReference);
        }

        this.runnable = () -> {
            MutableLiveData<Result> liveData = doInMainLoop();
            liveData.observe(owner, observer);
        };

        this.syncObject = new Object();
        this.observer.setSyncObject(syncObject);
    }

    protected abstract MutableLiveData<Result> doInMainLoop();

    public void run() throws InterruptedException {
        handler.post(this.runnable);

        synchronized (syncObject) {
            syncObject.wait();
        }
    }
}
