package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UnpairedRideEntity;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for UnpairedRideListDAO model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 */
public class UnpairedRideListDAO {
    private static final String COLLECTION = "unpairedRideList";

    /**
     * Adds a ride request allowing searchable ride requests for drivers
     *
     * @param entity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    public Task addRideRequest(RideRequestEntity entity) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final UnpairedRideEntity unpairedRide = new UnpairedRideEntity(entity.getRideRequestReference());
        return db.collection(COLLECTION)
                .add(unpairedRide)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        entity.setUnpairedReference(documentReference);
                        RideRequestDAO dao = new RideRequestDAO();
                        dao.saveEntity(entity);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure: ", e);
                    }
                });
    }

    // todo: improve readability

    /**
     * Gives back all unpaired ride requests
     *
     * @return
     * Returns a MutableLiveData object. To observe a MutableLiveData object:
     *
     * <pre>
     *      DatabaseManager db = DatabaseManager.getInstance();
     *      DAO dao = db.getDAO();
     *      MutableLiveData<Model> liveData = dao.getModel(...);
     *      liveData.observe(this, model -> {
     *          // receive model inside here
     *      });
     * </pre>
     *
     * When observed, the object may receive model as the following:
     * <li>
     *     <ul><b>Non-null List<RideRequest> object:</b> Search was successful.</ul>
     *     <ul><b>Null:</b> Search was unsuccessful.</ul>
     * </li>
     */
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

    public Task removeRiderRequest(RideRequest rideRequest) {
        DocumentReference documentReference = rideRequest.getUnpairedReference();

        if (documentReference != null) {
            rideRequest.setUnpairedReference(null);
            RideRequestDAO rideRequestDAO = new RideRequestDAO();
            rideRequestDAO.saveModel(rideRequest);

            // remove request from active list in rider
            // put request in rider history
            rideRequest.getRiderReference()
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        RiderEntity riderEntity = documentSnapshot.toObject(RiderEntity.class);
                        riderEntity.deactivateRideRequest(rideRequest);
                        RiderDAO riderDAO = new RiderDAO();
                        riderDAO.save(riderEntity);
                    })
                    .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));

            // remove request from driver active
            // put request in driver history
            DocumentReference driverReference = rideRequest.getDriverReference();
            if (driverReference != null) {
                driverReference.get()
                        .addOnSuccessListener(documentSnapshot -> {
                            DriverEntity driver = documentSnapshot.toObject(DriverEntity.class);
                            driver.deactivateRideRequest(rideRequest);
                            DriverDAO driverDAO = new DriverDAO();
                            driverDAO.save(driver);
                        })
                        .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
            }

            // remove request from active list in system
            return documentReference.delete();
        } else {
            return null;
        }
    }
}
