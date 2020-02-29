package com.cmput301w20t10.uberapp.database.viewmodel;

import android.app.Application;

import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.database.dao.UserDAO;
import com.cmput301w20t10.uberapp.database.daoimpl.RiderDAOImpl;
import com.cmput301w20t10.uberapp.database.daoimpl.UserDAOImpl;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

public class RiderMainViewModel extends AndroidViewModel {
    private MutableLiveData<Route> currentRoute;
    private MutableLiveData<Rider> rider;

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

    public MutableLiveData<Rider> getRider() {
        if (rider == null) {
            rider = new MutableLiveData<>();
        }

        return rider;
    }

    public MutableLiveData<Rider> registerRider(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                LifecycleOwner owner) {
        if (rider == null) {
            rider = new MutableLiveData<>();
        }

        RiderDAO riderDAO = new RiderDAOImpl();
        return riderDAO.registerRider(username, password, email, firstName, lastName, phoneNumber, owner);
    }
}
