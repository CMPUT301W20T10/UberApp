package com.cmput301w20t10.uberapp;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.dao.RideRequestDAO;
import com.cmput301w20t10.uberapp.database.dao.UserDAO;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.User;
import com.cmput301w20t10.uberapp.util.AssertNotNullObserver;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for ID To Model functions in DAO
 *
 * @author Allan Manuba
 * @version 1.4.1
 */
public class IDToModelTest extends BasicDAOTest {
    private static final String TAG = "Tomate";
    private static final String LOC = "Tomate: IDToModelTest: ";

    @Before
    public void initialize() {
        super.initialize();
    }

    @After
    public void cleanUp() {
        super.cleanUp();
    }

    @Test
    public void userIdToModelTest() throws InterruptedException {
        userIdToModelTestHelper(BASIC_TEST_RIDER2, BASIC_TEST_RIDER2_ID);
        userIdToModelTestHelper(BASIC_TEST_DRIVER1, BASIC_TEST_DRIVER1_ID);
    }

    private void userIdToModelTestHelper(User baseUser, String baseUserId) throws InterruptedException {
        final Object syncObject = new Object();
        AtomicReference<User> userReference = new AtomicReference<>();

        Runnable runnable = () -> {
            Observer<User> observer = new AssertNotNullObserver<User>(syncObject){
                @Override
                public void onChanged(User user) {
                    userReference.set(user);
                    super.onChanged(user);
                }
            };
            UserDAO userDAO = new UserDAO(mockDb);
            MutableLiveData<User> liveData = userDAO.getModelByID(baseUserId);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
        assertUserEquals(userReference.get(), baseUser);
    }

    @Test
    public void rideRequestIDToModelTest() throws InterruptedException {
        rideRequestIDToModelTestHelper(1);
        rideRequestIDToModelTestHelper(-1);
    }

    private void rideRequestIDToModelTestHelper(final int value) throws InterruptedException {
        RideRequest rideRequest = createRideRequest(loginAsDefaultRider());

        final Object syncObject1 = new Object();

        Runnable runnable1 = () -> {
            Observer<Boolean> observer = new AssertNotNullObserver<>(syncObject1);
            RideRequestDAO dao = new RideRequestDAO(mockDb);
            MutableLiveData<Boolean> liveData = dao.rateRide(rideRequest, value);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable1, syncObject1);

        final Object syncObject2 = new Object();
        AtomicReference<RideRequest> atomicReference = new AtomicReference<>();

        Runnable runnable2 = () -> {
            AssertNotNullObserver<RideRequest> observer = new AssertNotNullObserver<RideRequest>(syncObject2) {
                @Override
                public void onChanged(RideRequest rideRequest) {
                    super.onChanged(rideRequest);
                }
            };
            observer.setAtomicReference(atomicReference);
            RideRequestDAO otherDao = new RideRequestDAO(mockDb);
            MutableLiveData<RideRequest> liveData = otherDao.getModelByReference(rideRequest.getRideRequestReference());
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable2, syncObject2);

        RideRequest remote = atomicReference.get();
        try {
            assertEquals(remote.getRating(), rideRequest.getRating());
        } catch (AssertionError e) {
            throw e;
        }
    }
}
