package com.cmput301w20t10.uberapp.database.viewmodel;

import android.app.Application;
import android.database.DefaultDatabaseErrorHandler;

import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.database.daoimpl.DriverDAOImpl;
import com.cmput301w20t10.uberapp.database.daoimpl.RiderDAOImpl;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

public class DriverViewModel extends AndroidViewModel {
    private MutableLiveData<Rider> user;

    public DriverViewModel(@NonNull Application application) {
        super(application);
    }

    public static DriverViewModel create(@NonNull Application application) {
        return new ViewModelProvider.AndroidViewModelFactory(
                application).create(DriverViewModel.class);
    }

    public MutableLiveData<Driver> registerDriver(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                LifecycleOwner owner) {
        DriverDAO driverDAO = new DriverDAOImpl();
        return driverDAO.registerDriver(username, password, email, firstName, lastName, phoneNumber, owner);
    }
}
