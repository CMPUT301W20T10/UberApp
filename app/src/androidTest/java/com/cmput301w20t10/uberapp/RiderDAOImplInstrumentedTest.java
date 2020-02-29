package com.cmput301w20t10.uberapp;

import android.content.Context;

import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.database.daoimpl.RiderDAOImpl;
import com.cmput301w20t10.uberapp.models.Rider;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class RiderDAOImplInstrumentedTest {
    @Test
    public void registerRiderTest() throws InterruptedException {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();


        assertEquals("com.cmput301w20t10.uberapp", appContext.getPackageName());

        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();
        RiderDAO riderDAO = new RiderDAOImpl();
        riderDAO.registerRider("Snom", "123", "asdfkh@ya", "Snom", "Worm", "111", null);
        Rider rider = LiveDataTestUtil.getOrAwaitValue(riderLiveData);
    }

    @Test
    public void registerRiderAsUser() {

    }
}