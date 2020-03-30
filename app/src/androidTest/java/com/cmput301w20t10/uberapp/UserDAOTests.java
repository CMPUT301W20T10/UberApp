package com.cmput301w20t10.uberapp;

import com.cmput301w20t10.uberapp.database.dao.UserDAO;
import com.cmput301w20t10.uberapp.models.User;
import com.cmput301w20t10.uberapp.util.AssertTrueObserver;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class UserDAOTests extends BasicDAOTest {
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
    public void saveModelTest() throws InterruptedException {
        User user = loginAsDriver();
        final String newFCMToken = "Potati";

        final Object syncObject = new Object();

        Runnable runnable = () -> {
            AssertTrueObserver observer = new AssertTrueObserver(syncObject);
            UserDAO userDao = new UserDAO();
            user.setFCMToken(newFCMToken);
            MutableLiveData<Boolean> liveData = userDao.saveModel(user);
            liveData.observe(mainLifecycleOwner, observer);
        };

        liveDataObserver(runnable, syncObject);

        assertEquals(newFCMToken, user.getFCMToken());
    }
}
