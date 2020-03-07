package com.cmput301w20t10.uberapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.LoginRegisterDAO;
import com.cmput301w20t10.uberapp.database.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.UnpairedRideListDAO;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UnpairedRideEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class RideRequestDAOTest {
    private Context appContext;
    private LifecycleOwnerMock lifecycleOwner;
    private DatabaseManager databaseManager;
    private Handler handler;
    private Rider testRider;

    @Before
    public void initialize() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        lifecycleOwner = new LifecycleOwnerMock();
        databaseManager = DatabaseManager.getInstance();
        handler = new Handler(Looper.getMainLooper());
    }

    private <T> void liveDataObserver(@NonNull Runnable runnable,
                                      final Object syncObject) throws InterruptedException {
        handler.post(runnable);

        synchronized (syncObject) {
            syncObject.wait();
        }
    }

    private class AssertNullObserver<T> implements Observer<T> {
        private final Object syncObject;

        AssertNullObserver(Object syncObject) {
            this.syncObject = syncObject;
        }

        @Override
        public void onChanged(T t) {
            assertNotNull(t);

            synchronized (syncObject) {
                syncObject.notify();
            }
        }
    }

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     * https://androidoverride.wordpress.com/2017/05/27/android-working-with-live-data-and-custom-life-cycle-owners/
     */
    // todo: move to other test
    // todo: fix register to not return null
    /*@Test
    public void registerAsRiderTest() throws InterruptedException {
        LoginRegisterDAO loginRegisterDAO = databaseManager.getLoginRegisterDAO();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Rider> observer = new AssertNullObserver<Rider>(syncObject) {
                @Override
                public void onChanged(Rider rider) {
                    if (rider != null) {
                        synchronized (syncObject) {
                            syncObject.notify();
                        }
                    }
                }
            };
            MutableLiveData<Rider> liveData = loginRegisterDAO
                    .registerRider("OneOClock",
                            "1:00",
                            "email",
                            "Hungry",
                            "Snail",
                            "100",
                            "image",
                            lifecycleOwner);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }*/

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     * https://androidoverride.wordpress.com/2017/05/27/android-working-with-live-data-and-custom-life-cycle-owners/
     */
    @Test
    public void loginAsRiderTest() throws InterruptedException {
        logInAsRider();
    }

    private Rider logInAsRider() throws InterruptedException {
        // get data
        final Object syncObject = new Object();
        AtomicReference<Rider> riderAtomicReference = new AtomicReference<>();

        Runnable runnable = () -> {
            Observer<Rider> observer = new AssertNullObserver<Rider>(syncObject) {
                @Override
                public void onChanged(Rider rider) {
                    super.onChanged(rider);
                    riderAtomicReference.set(rider);
                }
            };
            LoginRegisterDAO loginRegisterDAO = databaseManager.getLoginRegisterDAO();
            MutableLiveData<Rider> liveData = loginRegisterDAO
                    .logInAsRider("OneOClock",
                            "1:00",
                            lifecycleOwner);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        return riderAtomicReference.get();
    }

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     */
    @Test
    public void createRideRequest() throws InterruptedException {
        // Initialize
        Rider rider = logInAsRider();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<RideRequest> observer = new AssertNullObserver<RideRequest>(syncObject);
            Route route = new Route(new GeoPoint(0,0), new GeoPoint(10, 10));
            RideRequestDAO dao = databaseManager.getRideRequestDAO();
            MutableLiveData<RideRequest> liveData = dao.createRideRequest(rider, route, 10);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        // todo: check if in active rides

        // todo: check if references a valid rider

        // todo: check if references self
    }

//    /**
//     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
//     */
    @Test
    public void getAllUnpairedRideRequestTest() throws InterruptedException {
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<List<RideRequest>> observer = new AssertNullObserver<List<RideRequest>>(syncObject) {
                @Override
                public void onChanged(List<RideRequest> rideRequests) {
                    if (rideRequests.size() > 3) {
                        super.onChanged(rideRequests);
                    }
                }
            };
            UnpairedRideListDAO dao = new UnpairedRideListDAO();
            MutableLiveData<List<RideRequest>> liveData = dao.getAllUnpairedRideRequest();
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }
    
//    /**
//     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
//     */
//    @Test
//    public void getAllActiveRideRequestTest() throws InterruptedException {
//        // Context of the app under test.
//        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
//
//        // Create mock lifecycle owner
//        LifecycleOwnerMock lifecycleOwnerMock = new LifecycleOwnerMock();
//
//        // Set up database manager
//        DatabaseManager databaseManager = DatabaseManager.getInstance();
//
//        // Initialize ride request DAO
//        LoginRegisterDAO dao = databaseManager.getLoginRegisterDAO();
//
//        // set up input
//        RiderEntity riderEntity = new RiderEntity();
//        UserEntity userEntity = new UserEntity();
//        AtomicReference<Rider> rider = new AtomicReference<>(new Rider(riderEntity, userEntity));
//        Route route = new Route();
//
//        // get data
//        final Object syncObject = new Object();
//
//        Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(() -> {
//            dao.logInAsRider("RyanBowler", "Password1", lifecycleOwnerMock)
//                    .observe(lifecycleOwnerMock, rider1 -> {
//                        assertNotNull(rider1);
//                        rider.set(rider1);
//
//                        synchronized (syncObject) {
//                            syncObject.notify();
//                        }
//                    });
//        });
//
//        // wait
//        synchronized (syncObject) {
//            syncObject.wait();
//        }
//
//        // create ride request
//        RideRequestDAO rideRequestDAO = new RideRequestDAO();
//        handler.post(() -> {
//            rideRequestDAO.createRideRequest(rider.get(), route, 10)
//                    .observe(lifecycleOwnerMock, rideRequest -> {
//                        assertNotNull(rideRequest);
//
//                        synchronized (syncObject) {
//                            syncObject.notify();
//                        }
//                    });
//        });
//
//
//        // wait
//        synchronized (syncObject) {
//            syncObject.wait();
//        }
//
//        // create get all active ride request
//        handler.post(() -> {
//            rideRequestDAO.getAllActiveRideRequest(rider.get())
//                    .observe(lifecycleOwnerMock, rideRequestList -> {
//                        assertNotNull(rideRequestList);
//
//                        if (rideRequestList.size() > 0) {
//                            synchronized (syncObject) {
//                                syncObject.notify();
//                            }
//                        }
//                    });
//        });
//
//        // wait
//        synchronized (syncObject) {
//            syncObject.wait();
//        }
//    }


}
