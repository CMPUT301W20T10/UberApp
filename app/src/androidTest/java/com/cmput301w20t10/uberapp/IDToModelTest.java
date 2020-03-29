package com.cmput301w20t10.uberapp;

import com.cmput301w20t10.uberapp.database.UserDAO;
import com.cmput301w20t10.uberapp.models.User;
import com.cmput301w20t10.uberapp.util.AssertNotNullObserver;
import com.cmput301w20t10.uberapp.util.DatabaseTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

public class IDToModelTest extends DatabaseTestBase {
    private static final String TAG = "Tomate";

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
        userIdToModelTestHelper(BASIC_TEST_RIDER1, BASIC_TEST_RIDER1_ID);
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
            UserDAO userDAO = new UserDAO();
            MutableLiveData<User> liveData = userDAO.getModelByID(baseUserId);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);
        assertUserEquals(userReference.get(), baseUser);
    }

    
}
