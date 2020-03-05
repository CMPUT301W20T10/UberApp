package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.models.Driver;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class DriverDAO {
    public static final String COLLECTION_DRIVERS = "drivers";

    DriverDAO() {}

    @Nullable
    public MutableLiveData<Driver> registerDriver(String username,
                                                  String password,
                                                  String email,
                                                  String firstName,
                                                  String lastName,
                                                  String phoneNumber,
                                                  String image,
                                                  LifecycleOwner owner) {

        MutableLiveData<Driver> driverLiveData = new MutableLiveData<>();
        DriverEntity driverEntity = new DriverEntity();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // todo: check if rider was already registered

        db.collection(COLLECTION_DRIVERS)
                .add(driverEntity)
                .addOnSuccessListener(driverReference -> {
                    driverReference.update(DriverEntity.DRIVER_REFERENCE, driverReference);
                    driverEntity.setDriverReference(driverReference);
                    registerDriverAsUser(
                            driverLiveData,
                            driverEntity,
                            username,
                            password,
                            email,
                            firstName,
                            lastName,
                            phoneNumber,
                            image,
                            owner);
                })
                .addOnFailureListener(e -> Log.w(TAG, "onFailure: Error adding document", e));
        return driverLiveData;
    }

    public LiveData<Driver> logInAsDriver(String username, String password, LifecycleOwner owner) {
        MutableLiveData<Driver> driverLiveData = new MutableLiveData<>();

        UserDAO userDAO = new UserDAO();
        userDAO.logIn(username, password)
                .observe(owner, userEntity -> {
                    if (userEntity == null || userEntity.getDriverReference() == null) {
                        driverLiveData.setValue(null);
                    } else {
                        userEntity.getDriverReference()
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        DriverEntity driverEntity = task.getResult()
                                                .toObject(DriverEntity.class);
                                        Driver driver = new Driver(driverEntity, userEntity);
                                        driverLiveData.setValue(driver);
                                    } else {
                                        driverLiveData.setValue(null);
                                    }
                                });
                    }
                });

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
                                      String image,
                                      @NonNull LifecycleOwner owner){
        // create user then set driver
        UserDAO userDAO = new UserDAO();
        userDAO.registerUser(username,
                password,
                email,
                firstName,
                lastName,
                phoneNumber,
                image)
                .observe(owner, userEntity -> {
                    if (userEntity != null && userEntity.getUserReference() != null) {
                        userDAO.registerDriver(driverEntity.getDriverReference(), userEntity.getUserReference());
                        Driver driver = new Driver(driverEntity, userEntity);
                        driverLiveData.setValue(driver);
                    }
                });
    }
}
