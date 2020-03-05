package com.cmput301w20t10.uberapp.database;

import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.database.entity.UnpairedRideEntity;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.lifecycle.MutableLiveData;

public class UnpairedRideListDAO {
    private static final String COLLECTION = "unpairedRideList";

    public Task addRideRequest(RideRequestEntity entity) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final UnpairedRideEntity unpairedRide = new UnpairedRideEntity(entity.getRideRequestReference());
        return db.collection(COLLECTION)
                .add(unpairedRide);
    }

    public MutableLiveData<RideRequest> getAllUnpairedRideRequest() {
        return null;
    }
}
