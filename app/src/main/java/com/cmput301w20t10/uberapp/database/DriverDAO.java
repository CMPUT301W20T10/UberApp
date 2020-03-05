package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.Driver;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

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
                    driverEntity.setDriverReference(driverReference);
                    save(driverEntity);
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
                        userEntity.setDriverReference(driverEntity.getDriverReference());
                        userDAO.save(userEntity);
                        Driver driver = new Driver(driverEntity, userEntity);
                        driverLiveData.setValue(driver);
                    }
                });
    }

    private Task save(DriverEntity driverEntity) {
        final DocumentReference reference = driverEntity.getDriverReference();
        Task task = null;

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (DriverEntity.Field field:
                    driverEntity.getDirtyFieldList()) {
                Object value = null;

                switch (field) {
                    case DRIVER_REFERENCE:
                        value = driverEntity.getDriverReference();
                        break;
                    case RATING:
                        value = driverEntity.getRating();
                        break;
                    case PAYMENT_LIST:
                        value = driverEntity.getPaymentList();
                        break;
                    case FINISHED_RIDE_REQUEST_LIST:
                        value = driverEntity.getActiveRideRequestList();
                        break;
                    case ACTIVE_RIDE_REQUEST_LIST:
                        break;
                    default:
                        break;
                }

                if (value != null) {
                    dirtyPairMap.put(field.toString(), value);
                }
            }

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }
        }

        return task;
    }
}
