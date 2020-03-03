package com.cmput301w20t10.uberapp.database.dao;

import com.cmput301w20t10.uberapp.models.Driver;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public interface DriverDAO {
    @Nullable
    MutableLiveData<Driver> registerDriver(String username,
                                          String password,
                                          String email,
                                          String firstName,
                                          String lastName,
                                          String phoneNumber,
                                          LifecycleOwner owner);

    LiveData<Driver> logInAsDriver(String username, String password, LifecycleOwner owner);
}
