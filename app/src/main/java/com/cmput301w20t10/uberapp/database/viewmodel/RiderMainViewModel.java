package com.cmput301w20t10.uberapp.database.viewmodel;

import android.app.Application;

import com.cmput301w20t10.uberapp.models.Route;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

public class RiderMainViewModel extends AndroidViewModel {
    private MutableLiveData<Route> currentRoute;

    public RiderMainViewModel(@NonNull Application application) {
        super(application);
    }

    public static RiderMainViewModel create(@NonNull Application application) {
        return new ViewModelProvider.AndroidViewModelFactory(
                application).create(RiderMainViewModel.class);
    }

    public MutableLiveData<Route> getCurrentRoute() {
        if (currentRoute == null) {
            currentRoute = new MutableLiveData<>();
            currentRoute.setValue(new Route());
        }

        return currentRoute;
    }
}
