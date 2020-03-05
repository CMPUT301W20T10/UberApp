package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

class RideRequestDAO {
    private static final String RIDE_REQUESTS = "rideRequests";

    public MutableLiveData<RideRequest> createRideRequest(Rider rider, Route route, int fareOffer) {
        MutableLiveData<RideRequest> rideRequestMutableLiveData = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RideRequestEntity entity = new RideRequestEntity(rider, route, fareOffer);
        db.collection(RIDE_REQUESTS)
                .add(entity)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference rideRequestReference) {
                        rideRequestReference.update(
                                RideRequestEntity.FIELD_RIDE_REQUEST_REFERENCE,
                                rideRequestReference);
                        entity.setRideRequestReference(rideRequestReference);
                        RideRequest model = new RideRequest(entity);
                        rideRequestMutableLiveData.setValue(model);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });

        return rideRequestMutableLiveData;
    }
}
