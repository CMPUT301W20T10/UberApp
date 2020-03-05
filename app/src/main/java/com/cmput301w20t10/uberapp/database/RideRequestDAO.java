package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class RideRequestDAO extends DAOBase<RideRequestEntity> {
    private static final String RIDE_REQUESTS = "rideRequests";

    public MutableLiveData<RideRequest> createRideRequest(Rider rider, Route route, int fareOffer) {
        MutableLiveData<RideRequest> rideRequestMutableLiveData = new MutableLiveData<>();
        Log.d(TAG, "createRideRequest: Here");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RideRequestEntity entity = new RideRequestEntity(rider, route, fareOffer);
        db.collection(RIDE_REQUESTS)
                .add(entity)
                .addOnSuccessListener(rideRequestReference -> {
                    rideRequestReference.update(
                            RideRequestEntity.FIELD_RIDE_REQUEST_REFERENCE,
                            rideRequestReference);
                    entity.setRideRequestReference(rideRequestReference);
                    save(entity);
                    RideRequest model = new RideRequest(entity);
                    rideRequestMutableLiveData.setValue(model);
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });

        return rideRequestMutableLiveData;
    }

    @Override
    public Task save(final RideRequestEntity entity) {
        final DocumentReference reference = entity.getRideRequestReference();
        Task task = null;

        if (reference != null) {
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (RideRequestEntity.Field field:
                    entity.getDirtyFieldList()) {
                Object value = null;

                switch (field) {
                    case RIDE_REQUEST_REFERENCE:
                        value = entity.getRideRequestReference();
                        break;
                    case DRIVER_REFERENCE:
                        value = entity.getDriverReference();
                        break;
                    case RIDER_REFERENCE:
                        value = entity.getRiderReference();
                        break;
                    case PAYMENT_REFERENCE:
                        value = entity.getPaymentReference();
                        break;
                    case STARTING_POSITION:
                        value = entity.getStartingPosition();
                        break;
                    case DESTINATION:
                        value = entity.getDestination();
                        break;
                    case STATE:
                        value = entity.getState();
                        break;
                    case FARE_OFFER:
                        value = entity.getFareOffer();
                        break;
                    case TIMESTAMP:
                        value = entity.getTimestamp();
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
