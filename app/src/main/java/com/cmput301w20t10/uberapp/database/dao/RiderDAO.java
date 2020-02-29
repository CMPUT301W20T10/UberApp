package com.cmput301w20t10.uberapp.database.dao;

import com.cmput301w20t10.uberapp.models.Rider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

public interface RiderDAO {
    @Nullable
    MutableLiveData<Rider> getRider(String username, String password);
    @Nullable
    MutableLiveData<Rider> registerRider(String username,
                                        String password,
                                        String email,
                                        String firstName,
                                        String lastName,
                                        String phoneNumber,
                                         LifecycleOwner owner);
}
