package com.cmput301w20t10.uberapp.database.viewmodel;

import android.app.Application;

import com.cmput301w20t10.uberapp.database.DatabaseManager;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

public class DriverRegistrationViewModel extends AndroidViewModel {
    private MutableLiveData<Rider> user;

    public DriverRegistrationViewModel(@NonNull Application application) {
        super(application);
    }

    public static DriverRegistrationViewModel create(@NonNull Application application) {
        return new ViewModelProvider.AndroidViewModelFactory(
                application).create(DriverRegistrationViewModel.class);
    }

    public MutableLiveData<Driver> registerDriver(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                LifecycleOwner owner) {
        return DatabaseManager.getInstance().registerDriver(
                username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                owner
        );
    }
}
