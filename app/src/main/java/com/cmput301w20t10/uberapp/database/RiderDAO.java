package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.models.Rider;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class RiderDAO {
    public static final String COLLECTION_RIDERS = "riders";

    RiderDAO() {}

    @Nullable
    public MutableLiveData<Rider> registerRider(String username,
                                                String password,
                                                String email,
                                                String firstName,
                                                String lastName,
                                                String phoneNumber,
                                                String image,
                                                @NonNull LifecycleOwner owner) {
        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();
        RiderEntity riderEntity = new RiderEntity();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // todo: check if rider was already registered

        db.collection(COLLECTION_RIDERS)
                .add(riderEntity)
                .addOnSuccessListener(riderReference -> {
                    riderReference.update(RiderEntity.FIELD_RIDER_REFERENCE, riderReference);
                    riderEntity.setRiderReference(riderReference);
                    this.save(riderEntity);

                    registerRiderAsUser(
                            riderLiveData,
                            riderEntity,
                            riderLiveData,
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
        return riderLiveData;
    }

    public MutableLiveData<Rider> logInAsRider(String username, String password, LifecycleOwner owner) {
        MutableLiveData<Rider> riderLiveData = new MutableLiveData<>();

        UserDAO userDAO = new UserDAO();
        userDAO.logIn(username, password)
                .observe(owner, userEntity -> {
                    if (userEntity == null || userEntity.getRiderReference() == null) {
                        riderLiveData.setValue(null);
                    } else {
                        userEntity.getRiderReference()
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        RiderEntity riderEntity = task.getResult()
                                                .toObject(RiderEntity.class);
                                        Rider rider = new Rider(riderEntity, userEntity);
                                        riderLiveData.setValue(rider);
                                    } else {
                                        riderLiveData.setValue(null);
                                    }
                                });
                    }
                });

        return riderLiveData;
    }

    private void registerRiderAsUser(MutableLiveData<Rider> riderLiveDate,
                                     RiderEntity riderEntity,
                                     MutableLiveData<Rider> riderLiveData,
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
                        userEntity.setRiderReference(riderEntity.getRiderReference());
                        userDAO.save(userEntity);
                        Rider rider = new Rider(riderEntity, userEntity);
                        riderLiveData.setValue(rider);
                    }
                });
    }

    private Task save(final RiderEntity riderEntity) {
        final DocumentReference reference = riderEntity.getRiderReference();
        Task task = null;

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (RiderEntity.Field field:
                    riderEntity.getDirtyFieldList()) {
                Object value = null;

                switch (field) {
                    case RIDER_REFERENCE:
                        value = riderEntity.getRiderReference();
                        break;
                    case PAYMENT_LIST:
                        value = riderEntity.getPaymentList();
                        break;
                    case RIDE_REQUEST_LIST:
                        value = riderEntity.getRideRequestList();
                        break;
                    case ACTIVE_RIDE_REQUEST_LIST:
                        value = riderEntity.getActiveRideRequestList();
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
