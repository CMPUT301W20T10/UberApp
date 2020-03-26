package com.cmput301w20t10.uberapp.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.util.DatabaseLogger;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.DocumentReference;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class DatabaseTestBase {
    protected static final Rider FAKE_DUCK_RIDER = new Rider(null,
            null,
            null,
            null,
            null,
            "DuckOnTheWall",
            "Can'tHackMe",
            "milkchoco@yahoo.com",
            "Duck",
            "Beak",
            "123456789",
            "None.jpeg",
            0f);

    protected Context mainContext;
    protected LifecycleOwnerMock mainLifecycleOwner;
    protected DatabaseManager databaseManager;
    protected Handler handler;

    private List<User> userList;

    protected void initialize() {
        mainContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mainLifecycleOwner = new LifecycleOwnerMock();
        databaseManager = DatabaseManager.getInstance();
        handler = new Handler(Looper.getMainLooper());
        userList = new ArrayList<>();
    }

    protected void cleanUp() {
        for (User user :
                userList) {
            DocumentReference userReference = user.getUserReference();
            if (userReference != null) {
                userReference.delete();
            } else {
                DatabaseLogger.error(new Exception(),
                        "User reference is null for " + user.getUsername(),
                        null);
            }

            if (user instanceof Rider) {
                Rider rider = (Rider) user;
                DocumentReference riderReference = rider.getRiderReference();
                if (riderReference != null) {
                    riderReference.delete();
                } else {
                    DatabaseLogger.error(new Exception(),
                            "Rider reference is null for " + rider.getUsername(),
                            null);
                }
            }

            if (user instanceof Driver) {
                Driver driver = (Driver) user;
                DocumentReference driverReference = driver.getDriverReference();
                if (driverReference != null) {
                    driverReference.delete();
                } else {
                    DatabaseLogger.error(new Exception(),
                            "Driver reference is null for " + driver.getUsername(),
                            null);
                }
            }
        }
    }

    protected void addUsersToCleanUp(User user) {
        userList.add(user);
    }

    protected void assertRiderEquals(Rider real, Rider fake) {
        assertNotNull(real.getRiderReference());
        assertNotNull(real.getTransactionList());
        assertNotNull(real.getFinishedRideRequestList());
        assertNotNull(real.getActiveRideRequestList());

        assertEquals(Float.compare(real.getBalance(), fake.getBalance()), 0);
        assertUserEquals(real, fake);
    }

    private void assertUserEquals(User real, User fake) {
        assertNotNull(real.getUserReference());
        assertEquals(real.getUsername(), fake.getUsername());
        assertEquals(real.getPassword(), fake.getPassword());
        assertEquals(real.getEmail(), fake.getEmail());
        assertEquals(real.getFirstName(), fake.getFirstName());
        assertEquals(real.getLastName(), fake.getLastName());
        assertEquals(real.getPhoneNumber(), fake.getPhoneNumber());
    }

    /**
     * Pauses the thread to wait for results
     *
     * Reference:
     *  Website title: Simple way to test asynchronous actions in Android: Service, AsyncTask, Thread, RxJava etc.
     *  Author: Danylo Volokh
     *  Link: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     *
     * todo: put a timer which stops the loop just in case
     * @author Allan Manuba
     * @version 1.0.1
     */
    protected void liveDataObserver(@NonNull Runnable runnable,
                                    final Object syncObject) throws InterruptedException {
        handler.post(runnable);

        synchronized (syncObject) {
            syncObject.wait();
        }
    }
}
