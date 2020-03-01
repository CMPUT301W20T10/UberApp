package com.cmput301w20t10.uberapp.database.daoimpl;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.UserDAO;
import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.models.Driver;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class DriverDAOImpl implements DriverDAO {
    public static final String COLLECTION_DRIVERS = "drivers";

    @Override
    public MutableLiveData<Driver> getDriver(String username, String password) {
        return null;
    }

    @Nullable
    @Override
    public MutableLiveData<Driver> registerDriver(String username,
                                                  String password,
                                                  String email,
                                                  String firstName,
                                                  String lastName,
                                                  String phoneNumber,
                                                  LifecycleOwner owner) {

        MutableLiveData<Driver> driverLiveData = new MutableLiveData<>();
        DriverEntity driverEntity = new DriverEntity();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION_DRIVERS)
                .add(driverEntity)
                .addOnSuccessListener(documentReference -> {
                    driverEntity.setDriverReference(documentReference);
                    registerDriverAsUser(
                            driverLiveData,
                            driverEntity,
                            username,
                            password,
                            email,
                            firstName,
                            lastName,
                            phoneNumber,
                            owner);
                })
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: Error adding document", e));
        return driverLiveData;
    }


    private void registerDriverAsUser(MutableLiveData<Driver> driverLiveData,
                                      DriverEntity driverEntity,
                                      String username,
                                      String password,
                                      String email,
                                      String firstName,
                                      String lastName,
                                      String phoneNumber,
                                      @NonNull LifecycleOwner owner){
        // create user then set driver
        UserDAO userDAO = new UserDAOImpl();
        userDAO.registerUser(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber)
                .observe(owner, userEntity -> {
                    if (userEntity != null && userEntity.getUserReference() != null) {
                        userDAO.registerDriver(driverEntity.getDriverReference(), userEntity.getUserReference());
                        Driver driver = new Driver(driverEntity, userEntity);
                        driverLiveData.setValue(driver);
                    }
                });
    }
}
