package com.cmput301w20t10.uberapp;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.LoginRegisterDAO;
import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.dao.TransactionDAO;
import com.cmput301w20t10.uberapp.database.dao.UnpairedRideListDAO;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.cmput301w20t10.uberapp.models.Transaction;
import com.cmput301w20t10.uberapp.util.AssertNotNullObserver;
import com.cmput301w20t10.uberapp.util.AssertTrueObserver;
import com.cmput301w20t10.uberapp.util.DatabaseTestBase;
import com.google.firebase.firestore.GeoPoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the basic functionalities of all DAOs
 *
 * @author Allan Manuba
 * @version 1.4.1
 */
public class BasicDAOTest extends DatabaseTestBase {
    private static final String TAG = "Tomate: ";

    @Before
    public void initialize() {
        super.initialize();
    }

    @After
    public void cleanUp() {
        super.cleanUp();
    }

    @Test
    public void registerAsRiderTest() throws InterruptedException {
        LoginRegisterDAO loginRegisterDAO = new LoginRegisterDAO(mockDb);

        // get data
        final Object syncObject = new Object();
        final AtomicReference<Rider> riderAtomicReference = new AtomicReference<>();
        final Rider rider = REGISTER_TEST_RIDER1;

        Runnable runnable = () -> {
            Observer<Rider> observer = new AssertNotNullObserver<Rider>(syncObject) {
                @Override
                public void onChanged(Rider rider) {
                    riderAtomicReference.set(rider);
                    super.onChanged(rider);
                }
            };
            MutableLiveData<Rider> liveData = loginRegisterDAO
                    .registerRider(rider.getUsername(),
                            rider.getPassword(),
                            rider.getEmail(),
                            rider.getFirstName(),
                            rider.getLastName(),
                            rider.getPhoneNumber(),
                            rider.getImage(),
                            mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        Rider burnerRider = riderAtomicReference.get();
        addUsersToCleanUp(burnerRider);
        assertRiderEquals(burnerRider, rider);
    }

    // todo: improve to match current set up
    @Test
    public void registerAsDriverTest() throws InterruptedException {
        LoginRegisterDAO loginRegisterDAO = new LoginRegisterDAO(mockDb);

        // get data
        final Object syncObject = new Object();
        AtomicReference<Driver> atomicReference = new AtomicReference<>();

        Runnable runnable = () -> {
            Observer<Driver> observer = new AssertNotNullObserver<Driver>(syncObject){
                @Override
                public void onChanged(Driver driver) {
                    atomicReference.set(driver);
                    super.onChanged(driver);
                }
            };
            MutableLiveData<Driver> liveData = loginRegisterDAO
                    .registerDriver("CharlieTest2",
                            "2:00",
                            "email",
                            "Full",
                            "Pineapple",
                            "200",
                            "picture",
                            mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        Driver driver = atomicReference.get();
        addUsersToCleanUp(driver);
    }

    /**
     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
     * https://androidoverride.wordpress.com/2017/05/27/android-working-with-live-data-and-custom-life-cycle-owners/
     */
    @Test
    public void loginAsRiderTest() throws InterruptedException {
        loginAsDefaultRider();
    }

    private Rider logInAsRider(Rider rider) throws InterruptedException {
        // get data
        final Object syncObject = new Object();
        AtomicReference<Rider> riderAtomicReference = new AtomicReference<>();

        Runnable runnable = () -> {
            Observer<Rider> observer = new AssertNotNullObserver<Rider>(syncObject) {
                @Override
                public void onChanged(Rider rider) {
                    assertNotNull(rider);
                    riderAtomicReference.set(rider);
                    super.onChanged(rider);
                }
            };
            LoginRegisterDAO loginRegisterDAO = new LoginRegisterDAO(mockDb);
            MutableLiveData<Rider> liveData = loginRegisterDAO
                    .logInAsRider(rider.getUsername(),
                            rider.getPassword(),
                            mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        return riderAtomicReference.get();
    }

    protected Rider loginAsDefaultRider() throws InterruptedException {
        return logInAsRider(BASIC_TEST_RIDER2);
    }

    @Test
    public void loginAsDriverTest() throws InterruptedException {
        loginAsDriver();
    }

    protected Driver loginAsDriver() throws InterruptedException {
        // get data
        final Object syncObject = new Object();
        AtomicReference<Driver> atomicReference = new AtomicReference<>();



        Runnable runnable = () -> {
            Observer<Driver> observer = new AssertNotNullObserver<Driver>(syncObject) {
                @Override
                public void onChanged(Driver driver) {
                    assertNotNull(driver);
                    atomicReference.set(driver);
                    super.onChanged(driver);
                }
            };
            LoginRegisterDAO loginRegisterDAO = new LoginRegisterDAO(mockDb);
            MutableLiveData<Driver> liveData = loginRegisterDAO
                    .logInAsDriver("Charlie",
                            "2:00",
                            mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
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

    //@Test
    public void createRideRequestForHepCat() throws InterruptedException {
        // Initialize
        Rider rider = logInAsRider(BASIC_TEST_RIDER1);
        assertNotNull(rider);
        AtomicReference<RideRequest> rideRequestAtomicReference = new AtomicReference<>();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<RideRequest> observer = new AssertNotNullObserver<RideRequest>(syncObject) {
                @Override
                public void onChanged(RideRequest rideRequest) {
                    rideRequestAtomicReference.set(rideRequest);
                    super.onChanged(rideRequest);
                }
            };
            Route route = new Route(new GeoPoint(0,0), new GeoPoint(10, 10));
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<RideRequest> liveData = dao.createRideRequest(rider, route, 10, mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    public RideRequest createRideRequest() throws InterruptedException {
        // Initialize
        Rider rider = loginAsDefaultRider();
        assertNotNull(rider);
        AtomicReference<RideRequest> rideRequestAtomicReference = new AtomicReference<>();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<RideRequest> observer = new AssertNotNullObserver<RideRequest>(syncObject) {
                @Override
                public void onChanged(RideRequest rideRequest) {
                    rideRequestAtomicReference.set(rideRequest);
                    super.onChanged(rideRequest);
                }
            };
            Route route = new Route(new GeoPoint(0,0), new GeoPoint(10, 10));
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<RideRequest> liveData = dao.createRideRequest(rider, route, 10, mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
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
            Observer<List<RideRequest>> observer = new AssertNotNullObserver<List<RideRequest>>(syncObject) {
                @Override
                public void onChanged(List<RideRequest> rideRequests) {
                    if (rideRequests.size() > 0) {
                        super.onChanged(rideRequests);
                    }
                }
            };
            UnpairedRideListDAO dao = new UnpairedRideListDAO(mockDb);
            MutableLiveData<List<RideRequest>> liveData = dao.getAllUnpairedRideRequest();
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

//    /**
//     * Reference: medium.com/android-development-by-danylo/simple-way-to-test-asynchronous-actions-in-android-service-asynctask-thread-rxjava-etc-d43b0402e005
//     */
    @Test
    public void getAllActiveRideRequestForRiderTest() throws InterruptedException {
        // Initialize
        Rider rider = loginAsDefaultRider();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<List<RideRequest>> observer = new AssertNotNullObserver<List<RideRequest>>(syncObject) {
                @Override
                public void onChanged(List<RideRequest> rideRequests) {
                    if (rideRequests.size() >= 1) {
                        super.onChanged(rideRequests);
                    }
                }
            };
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<List<RideRequest>> liveData = dao.getAllActiveRideRequest(rider);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    @Test
    public void cancelRideByRiderTest() throws InterruptedException {
        // Initialize
        RideRequest rideRequest = createRideRequest();

        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Boolean> observer = new AssertTrueObserver(syncObject);
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<Boolean> liveData = dao.cancelRequest(rideRequest, mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    @Test
    public void getRiderFromRequestTest() throws InterruptedException {
        // Initialize
        RideRequest rideRequest = createRideRequest();

        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Rider> observer = new AssertNotNullObserver<Rider>(syncObject);
            RideRequestDAO dao = new RideRequestDAO();
            MutableLiveData<Rider> liveData = dao.getRiderForRequest(rideRequest);
            liveData.observe(mainLifecycleOwner, observer);
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
            Observer<Boolean> observer = new AssertNotNullObserver<Boolean>(syncObject);
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<Boolean> liveData = dao.acceptRequest(rideRequest, driver, mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        // todo: check if in active rides

        // todo: check if references a valid rider

        // todo: check if references self
        return rideRequest;
    }

    @Test
    public void cancelRequestAfterDriverAcceptTest()  throws InterruptedException {
        RideRequest rideRequest = driverAcceptsRequest();

        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Boolean> observer = new AssertTrueObserver(syncObject);
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<Boolean> liveData = dao.cancelRequest(rideRequest, mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }

    @Test
    public void getAllActiveRideRequestForDriverTest() throws InterruptedException {
        driverAcceptsRequestTest();
        driverAcceptsRequestTest();
        Driver driver = loginAsDriver();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<List<RideRequest>> observer = new AssertNotNullObserver<List<RideRequest>>(syncObject) {
                @Override
                public void onChanged(List<RideRequest> rideRequests) {
                    if (rideRequests.size() > 1) {
                        super.onChanged(rideRequests);
                    }
                }
            };
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<List<RideRequest>> liveData = dao.getAllActiveRideRequest(driver);
            liveData.observe(mainLifecycleOwner, observer);
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
        Rider rider = loginAsDefaultRider();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Boolean> observer = new AssertNotNullObserver<Boolean>(syncObject);
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<Boolean> liveData = dao.acceptRideFromDriver(rideRequest, rider, mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        // todo: check if in active rides

        // todo: check if references a valid rider

        // todo: check if references self
        return rideRequest;
    }

    @Test
    public void riderConfirmCompletionTest() throws InterruptedException {
        riderConfirmCompletion();
    }

    public RideRequest riderConfirmCompletion() throws InterruptedException {
        RideRequest rideRequest = riderAcceptsDriver();

        Rider rider = loginAsDefaultRider();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Boolean> observer = new AssertNotNullObserver<Boolean>(syncObject);
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<Boolean> liveData = dao.confirmRideCompletion(rideRequest, rider, mainLifecycleOwner);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        return rideRequest;
    }

    @Test
    public void createTransactionTest() throws InterruptedException {
        RideRequest rideRequest = riderConfirmCompletion();

        // todo: change to get user instead of logging in
        Rider rider = loginAsDefaultRider();
        Driver driver = loginAsDriver();
        AtomicReference<Transaction> transactionAtomicReference = new AtomicReference<>();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Transaction> observer = new AssertNotNullObserver<Transaction>(syncObject) {
                @Override
                public void onChanged(Transaction transaction) {
                    transactionAtomicReference.set(transaction);
                    super.onChanged(transaction);
                }
            };
            TransactionDAO dao = new TransactionDAO(mockDb);
            MutableLiveData<Transaction> liveData = dao.createTransaction(mainLifecycleOwner, rideRequest, 1250);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        Transaction transaction = transactionAtomicReference.get();
        Log.d(TAG, "createTransactionTest: Rider: " + rider.getRiderReference().getPath());
        Log.d(TAG, "createTransactionTest: Driver: " + driver.getDriverReference().getPath());
        Log.d(TAG, "createTransactionTest: RideRequest: " + rideRequest.getRideRequestReference().getPath());
        Log.d(TAG, "createTransactionTest: RideRequest: " + transaction.getTransactionReference().getPath());
    }

    @Test
    public void riderRateDriverTest() throws InterruptedException {
        Driver driver = loginAsDriver();

        // get data
        final Object syncObject = new Object();

        Runnable runnable = () -> {
            Observer<Boolean> observer = new AssertNotNullObserver<Boolean>(syncObject) {
                @Override
                public void onChanged(Boolean aBoolean) {
                    assertTrue(aBoolean);
                    super.onChanged(aBoolean);
                }
            };
            DriverDAO driverDAO = new DriverDAO(mockDb);
            int increment = 1;
            MutableLiveData<Boolean> liveData = driverDAO.rateDriver(driver, increment);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
    }
}
