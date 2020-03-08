package com.cmput301w20t10.uberapp;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.database.LoginRegisterDAO;
import com.cmput301w20t10.uberapp.database.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.UnpairedRideListDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ThrowOnExtraProperties;

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

import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class BasicDAOTest {
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
    /*
    @Test
    public void registerAsRiderTest() throws InterruptedException {
        LoginRegisterDAO loginRegisterDAO = databaseManager.getLoginRegisterDAO();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Rider> observer = new AssertNullObserver<Rider>(syncObject);
            MutableLiveData<Rider> liveData = loginRegisterDAO
                    .registerRider("OneOClock2",
                            "1:00",
                            "email",
                            "Hungry",
                            "Snail2",
                            "100",
                            "image",
                            lifecycleOwner);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    @Test
    public void registerAsDriverTest() throws InterruptedException {
        LoginRegisterDAO loginRegisterDAO = databaseManager.getLoginRegisterDAO();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Driver> observer = new AssertNullObserver<Driver>(syncObject);
            MutableLiveData<Driver> liveData = loginRegisterDAO
                    .registerDriver("TwoOClock",
                            "2:00",
                            "email",
                            "Full",
                            "Pineapple",
                            "200",
                            "picture",
                            lifecycleOwner);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }
*/
    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     * https://androidoverride.wordpress.com/2017/05/27/android-working-with-live-data-and-custom-life-cycle-owners/
     */
    @Test
    public void loginAsRiderTest() throws InterruptedException {
        loginAsRider();
    }

    private Rider loginAsRider() throws InterruptedException {
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

    @Test
    public void loginAsDriverTest() throws InterruptedException {
        loginAsDriver();
    }

    private Driver loginAsDriver() throws InterruptedException {
        // get data
        final Object syncObject = new Object();
        AtomicReference<Driver> atomicReference = new AtomicReference<>();

        Runnable runnable = () -> {
            Observer<Driver> observer = new AssertNullObserver<Driver>(syncObject) {
                @Override
                public void onChanged(Driver driver) {
                    super.onChanged(driver);
                    atomicReference.set(driver);
                }
            };
            LoginRegisterDAO loginRegisterDAO = databaseManager.getLoginRegisterDAO();
            MutableLiveData<Driver> liveData = loginRegisterDAO
                    .logInAsDriver("TwoOClock",
                            "2:00",
                            lifecycleOwner);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        return atomicReference.get();
    }

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     */
    @Test
    public void createRideRequestTest() throws InterruptedException {
        createRideRequest();
    }

    private RideRequest createRideRequest() throws InterruptedException {
        // Initialize
        Rider rider = loginAsRider();
        assertNotNull(rider);
        AtomicReference<RideRequest> rideRequestAtomicReference = new AtomicReference<>();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<RideRequest> observer = new AssertNullObserver<RideRequest>(syncObject) {
                @Override
                public void onChanged(RideRequest rideRequest) {
                    rideRequestAtomicReference.set(rideRequest);
                    super.onChanged(rideRequest);
                }
            };
            Route route = new Route(new GeoPoint(0,0), new GeoPoint(10, 10));
            RideRequestDAO dao = databaseManager.getRideRequestDAO();
            MutableLiveData<RideRequest> liveData = dao.createRideRequest(rider, route, 10);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        // todo: check if in active rides

        // todo: check if references a valid rider

        // todo: check if references self
        return rideRequestAtomicReference.get();
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
                    if (rideRequests.size() > 0) {
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
    @Test
    public void getAllActiveRideRequestForRiderTest() throws InterruptedException {
        // Initialize
        Rider rider = loginAsRider();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<List<RideRequest>> observer = new AssertNullObserver<List<RideRequest>>(syncObject) {
                @Override
                public void onChanged(List<RideRequest> rideRequests) {
                    if (rideRequests.size() > 1) {
                        super.onChanged(rideRequests);
                    }
                }
            };
            RideRequestDAO dao = databaseManager.getRideRequestDAO();
            MutableLiveData<List<RideRequest>> liveData = dao.getAllActiveRideRequest(rider);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    @Test
    public void cancelRideByRiderTest() throws InterruptedException {
        // Initialize
        RideRequest rideRequest = createRideRequest();

        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Boolean> observer = new AssertNullObserver<Boolean>(syncObject) {
                @Override
                public void onChanged(Boolean aBoolean) {
                    super.onChanged(aBoolean);
                    assert aBoolean;
                }
            };
            RideRequestDAO dao = new RideRequestDAO();
            MutableLiveData<Boolean> liveData = dao.cancelRequest(rideRequest, lifecycleOwner);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    @Test
    public void getRiderFromRequestTest() throws InterruptedException {
        // Initialize
        RideRequest rideRequest = createRideRequest();

        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Rider> observer = new AssertNullObserver<Rider>(syncObject);
            RideRequestDAO dao = new RideRequestDAO();
            MutableLiveData<Rider> liveData = dao.getRiderForRequest(rideRequest);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    @Test
    public void driverAcceptsRequestTest() throws InterruptedException {
        driverAcceptsRequest();
    }

    public RideRequest driverAcceptsRequest() throws InterruptedException {
        Driver driver = loginAsDriver();
        RideRequest rideRequest = createRideRequest();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Boolean> observer = new AssertNullObserver<Boolean>(syncObject);
            RideRequestDAO dao = databaseManager.getRideRequestDAO();
            MutableLiveData<Boolean> liveData = dao.acceptRequest(rideRequest, driver, lifecycleOwner);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        // todo: check if in active rides

        // todo: check if references a valid rider

        // todo: check if references self
        return rideRequest;
    }

    @Test
    public void getAllActiveRideRequestForDriverTest() throws InterruptedException {
        driverAcceptsRequestTest();
        driverAcceptsRequestTest();
        Driver driver = loginAsDriver();
        assertNotNull(driver);

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<List<RideRequest>> observer = new AssertNullObserver<List<RideRequest>>(syncObject) {
                @Override
                public void onChanged(List<RideRequest> rideRequests) {
                    if (rideRequests.size() > 1) {
                        super.onChanged(rideRequests);
                    }
                }
            };
            RideRequestDAO dao = databaseManager.getRideRequestDAO();
            MutableLiveData<List<RideRequest>> liveData = dao.getAllActiveRideRequest(driver);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    public void estimateFareTest() {
        // todo: estimate fare test
    }

    @Test
    public void riderAcceptsDriverTest() throws InterruptedException {
        riderAcceptsDriver();
    }

    public RideRequest riderAcceptsDriver() throws InterruptedException {
        RideRequest rideRequest = driverAcceptsRequest();
        Rider rider = loginAsRider();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Boolean> observer = new AssertNullObserver<Boolean>(syncObject);
            RideRequestDAO dao = databaseManager.getRideRequestDAO();
            MutableLiveData<Boolean> liveData = dao.acceptRideFromDriver(rideRequest, rider);
            liveData.observe(lifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        // todo: check if in active rides

        // todo: check if references a valid rider

        // todo: check if references self
        return rideRequest;
    }

    public void riderConfirmCompletionTest() {
        // todo: riderConfirmCompletionTest
    }

    public void generateQRCodeTest() {
        // todo: generateQRCodeTest
    }

    public void scanQRCodeTest() {
        // todo
    }

    public void riderRateDriverTest() {
        // todo: riderRateDriverTest
    }
}
