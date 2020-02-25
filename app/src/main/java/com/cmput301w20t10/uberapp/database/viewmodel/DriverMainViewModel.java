package com.cmput301w20t10.uberapp.database.viewmodel;

import android.app.Application;

import com.cmput301w20t10.uberapp.Route;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

public class DriverMainViewModel extends AndroidViewModel {
    private MutableLiveData<Route> currentRoute;

    public DriverMainViewModel(@NonNull Application application) {
        super(application);
    }

    public static DriverMainViewModel create(@NonNull Application application) {
        return new ViewModelProvider.AndroidViewModelFactory(
                application).create(DriverMainViewModel.class);
    }

    public MutableLiveData<Route> getCurrentRoute() {
        if (currentRoute == null) {
            currentRoute = new MutableLiveData<>();
            currentRoute.setValue(new Route());
        }

        return currentRoute;
    }
}
