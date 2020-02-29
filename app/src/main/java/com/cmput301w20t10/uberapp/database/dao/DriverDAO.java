package com.cmput301w20t10.uberapp.database.dao;

import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.User;

import androidx.lifecycle.MutableLiveData;

public interface DriverDAO {
    public MutableLiveData<Driver> getDriver(String username, String password);
}
