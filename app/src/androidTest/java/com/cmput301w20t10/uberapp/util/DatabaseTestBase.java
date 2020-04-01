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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Base class for all database testing classes
 *
 * @author Allan Manuba
 * @version 1.9.1
 */
public class DatabaseTestBase {
    // region fake accounts
    protected static final Rider REGISTER_TEST_RIDER1 = new Rider(null,
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
            0);

    protected static final Driver REGISTER_TEST_DRIVER1 = new Driver(null,
            null,
            null,
            null,
            null,
            "PenguinHighway",
            "BritaWaterFilter",
            "sundog@gmail.com",
            "Penguin",
            "Highway",
            "126290876",
            0,
            "ilovejpg.jpg");

    // reserved for Alex's debugging
    protected static final String BASIC_TEST_RIDER1_ID = "DnD1XshbWPj6szVlzdqG";
    protected static final Rider BASIC_TEST_RIDER1 = new Rider(null,
            null,
            null,
            null,
            null,
            "HepCat",
            "1:00",
            "inlovewithaghost@outlook.com",
            "Hepburn",
            "Catastrophy",
            "232323232",
            "great.png",
            0);

    protected static final String BASIC_TEST_RIDER2_ID = "VdiHZjofV3ilOkt8qPqz";
    protected static final Rider BASIC_TEST_RIDER2 = new Rider(null,
            null,
            null,
            null,
            null,
            "HepCat999",
            "1:00",
            "inlovewithaghost@outlook.com",
            "Hepburn",
            "Catastrophy",
            "232323232",
            "great.png",
            0);

    protected static final String BASIC_TEST_DRIVER1_ID = "HFDqU1A9t4CHtGaY234f";
    protected static final Driver BASIC_TEST_DRIVER1 = new Driver(null,
            null,
            null,
            null,
            null,
            "Charlie",
            "2:00",
            "jazzy.com",
            "Charlie",
            "Sport",
            "126290876",
            0,
            "ilovejpg.jpg");
    // endregion fake accounts

    protected FirebaseFirestore mockDb;

    protected LifecycleOwnerMock mainLifecycleOwner;
    protected DatabaseManager databaseManager;
    protected Handler handler;

    private List<User> userList;

    protected void initialize() {
        mockDb = FirebaseFirestore.getInstance();
        mainLifecycleOwner = new LifecycleOwnerMock();
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

        assertEquals(real.getBalance(), fake.getBalance());
        assertUserEquals(real, fake);
    }

    protected void assertUserEquals(User real, User fake) {
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
