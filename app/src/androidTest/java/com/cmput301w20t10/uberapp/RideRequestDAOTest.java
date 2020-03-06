package com.cmput301w20t10.uberapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.LoginRegisterDAO;
import com.cmput301w20t10.uberapp.database.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicReference;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class RideRequestDAOTest {

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     */
    @Test
    public void createRideRequest() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create mock lifecycle owner
        LifecycleOwnerMock lifecycleOwnerMock = new LifecycleOwnerMock();

        // Set up database manager
        DatabaseManager databaseManager = DatabaseManager.getInstance();

        // Initialize ride request DAO
        RideRequestDAO dao = databaseManager.getRideRequestDAO();

        // set up input
        RiderEntity riderEntity = new RiderEntity();
        UserEntity userEntity = new UserEntity();
        Rider rider = new Rider(riderEntity, userEntity);
        Route route = new Route(new GeoPoint(0,0), new GeoPoint(10, 10));

        // get data
        final Object syncObject = new Object();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Log.d(TAG, "run: Testing");
            dao.createRideRequest(rider, route, 10)
                    .observe(lifecycleOwnerMock, rideRequest -> {
                        assertNotNull(rideRequest);

                        synchronized (syncObject) {
                            syncObject.notify();
                        }
                    });
        });


        // wait
        synchronized (syncObject) {
            syncObject.wait();
        }

        Thread.sleep(2000);

        // todo: check if in active rides
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // todo: check if references a valid rider

        // todo: check if references self
    }

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     */
    @Test
    public void getAllUnpairedRideRequestTest() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create mock lifecycle owner
        LifecycleOwnerMock lifecycleOwnerMock = new LifecycleOwnerMock();

        // Set up database manager
        DatabaseManager databaseManager = DatabaseManager.getInstance();

        // Initialize ride request DAO
        RideRequestDAO dao = databaseManager.getRideRequestDAO();

        // set up input
        RiderEntity riderEntity = new RiderEntity();
        UserEntity userEntity = new UserEntity();
        Rider rider = new Rider(riderEntity, userEntity);
        Route route = new Route(new GeoPoint(0,0), new GeoPoint(10, 10));

        // get data
        final Object syncObject = new Object();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            final int count = 5;
            dao.getUnpairedRideRequest()
                    .observe(lifecycleOwnerMock, rideRequestList -> {
                        assertNotNull(rideRequestList);

                        if (rideRequestList.size() == count) {
                            synchronized (syncObject) {
                                syncObject.notify();
                            }
                        }
                    });
        });


        // wait
        synchronized (syncObject) {
            // todo: add time limit
            syncObject.wait();
        }
    }

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     */
    @Test
    public void getAllActiveRideRequestTest() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create mock lifecycle owner
        LifecycleOwnerMock lifecycleOwnerMock = new LifecycleOwnerMock();

        // Set up database manager
        DatabaseManager databaseManager = DatabaseManager.getInstance();

        // Initialize ride request DAO
        LoginRegisterDAO dao = databaseManager.getLoginRegisterDAO();

        // set up input
        RiderEntity riderEntity = new RiderEntity();
        UserEntity userEntity = new UserEntity();
        AtomicReference<Rider> rider = new AtomicReference<>(new Rider(riderEntity, userEntity));
        Route route = new Route();

        // get data
        final Object syncObject = new Object();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            Log.d(TAG, "run: getAllActiveRideRequestTest: ");
            dao.logInAsRider("RyanBowler", "Password1", lifecycleOwnerMock)
                    .observe(lifecycleOwnerMock, rider1 -> {
                        assertNotNull(rider1);
                        rider.set(rider1);

                        synchronized (syncObject) {
                            syncObject.notify();
                        }
                    });
        });

        // wait
        synchronized (syncObject) {
            syncObject.wait();
        }

        // create ride request
        RideRequestDAO rideRequestDAO = new RideRequestDAO();
        handler.post(() -> {
            Log.d(TAG, "run: Testing");
            rideRequestDAO.createRideRequest(rider.get(), route, 10)
                    .observe(lifecycleOwnerMock, rideRequest -> {
                        assertNotNull(rideRequest);
                        Log.d(TAG, "run: rider1: " + rideRequest.getRiderReference().getPath());

                        synchronized (syncObject) {
                            syncObject.notify();
                        }
                    });
        });


        // wait
        synchronized (syncObject) {
            syncObject.wait();
        }

        // create get all active ride request
        handler.post(() -> {
            Log.d(TAG, "run: Testing");
            rideRequestDAO.getAllActiveRideRequest(rider.get())
                    .observe(lifecycleOwnerMock, rideRequestList -> {
                        assertNotNull(rideRequestList);
                        Log.d(TAG, "run: getAllActiveRideRequestTest: " + rideRequestList.toString());
                        Log.d(TAG, "run: getAllActiveRideRequestTest: " + rider.get().getRiderReference());

                        if (rideRequestList.size() > 0) {
                            Log.d(TAG, "getAllActiveRideRequestTest: Success!");
                            Log.d(TAG, "getAllActiveRideRequestTest: " + rideRequestList.get(0).getRideRequestReference().getPath());
                            synchronized (syncObject) {
                                syncObject.notify();
                            }
                        }
                    });
        });


        // wait
        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     * https://androidoverride.wordpress.com/2017/05/27/android-working-with-live-data-and-custom-life-cycle-owners/
     */
    @Test
    public void loginAsRiderTest() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        // Create mock lifecycle owner
        LifecycleOwnerMock lifecycleOwnerMock = new LifecycleOwnerMock();

        // Set up database manager
        DatabaseManager databaseManager = DatabaseManager.getInstance();

        // Initialize ride request DAO
        LoginRegisterDAO dao = databaseManager.getLoginRegisterDAO();

        // set up input
        RiderEntity riderEntity = new RiderEntity();
        UserEntity userEntity = new UserEntity();
        Rider rider = new Rider(riderEntity, userEntity);
        Route route = new Route();

        // get data
        final Object syncObject = new Object();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            dao.logInAsRider("RyanBowler", "Password1", lifecycleOwnerMock)
                .observe(lifecycleOwnerMock, rider1 -> {
                    assertNotNull(rider1);

                    synchronized (syncObject) {
                        syncObject.notify();
                    }
                });
        });

        // wait
        synchronized (syncObject) {
            syncObject.wait();
        }
    }
}
