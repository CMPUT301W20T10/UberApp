package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.database.entity.UnpairedRideEntity;
import com.cmput301w20t10.uberapp.database.entity.UserEntity;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

public class UnpairedRideListDAO {
    private static final String COLLECTION = "unpairedRideList";

    public Task addRideRequest(RideRequestEntity entity) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final UnpairedRideEntity unpairedRide = new UnpairedRideEntity(entity.getRideRequestReference());
        return db.collection(COLLECTION)
                .add(unpairedRide);
    }

    // todo: improve readability
    public MutableLiveData<List<RideRequest>> getAllUnpairedRideRequest() {
        final MutableLiveData<List<RideRequest>> rideRequestMutableLiveData = new MutableLiveData<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<RideRequest> rideRequestList = new ArrayList<>();
                        for (DocumentSnapshot snapshot : task.getResult().getDocuments()) {
                            Log.d(TAG, "getAllUnpairedRideRequest: " + snapshot.getData().toString());
                            UnpairedRideEntity unpairedRideEntity = snapshot.toObject(UnpairedRideEntity.class);
                            unpairedRideEntity.getRideRequestReference()
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.d(TAG, "entity??: " + task1.getResult().getData().toString());
                                            RideRequestEntity rideRequestEntity = task1.getResult().toObject(RideRequestEntity.class);
                                            rideRequestList.add(new RideRequest(rideRequestEntity));
                                            rideRequestMutableLiveData.setValue(rideRequestList);
                                        } else {
                                            Log.e(TAG, "onComplete: ", task1.getException());
                                        }
                                    });
                        }
                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
                    }
                });
        return rideRequestMutableLiveData;
    }
}
