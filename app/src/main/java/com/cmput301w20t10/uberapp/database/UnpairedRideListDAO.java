package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.entity.DriverEntity;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.database.entity.RiderEntity;
import com.cmput301w20t10.uberapp.database.entity.UnpairedRideEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.google.android.gms.tasks.OnFailureListener;
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
    static final String COLLECTION = "unpairedRideList";

    /**
     * Adds a ride request allowing searchable ride requests for drivers
     *
     * @param requestEntity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    public Task<DocumentReference> addRideRequest(RideRequestEntity requestEntity) {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        final UnpairedRideEntity unpairedRide = new UnpairedRideEntity(requestEntity.getRideRequestReference());
        MutableLiveData<DocumentReference> liveData = new MutableLiveData<>();
        return db.collection(COLLECTION)
            .add(unpairedRide)
            .addOnSuccessListener(documentReference -> {
                requestEntity.setUnpairedReference(documentReference);
                RideRequestDAO dao = new RideRequestDAO();
                dao.saveEntity(requestEntity);
            })
            .addOnFailureListener(e -> Log.e(TAG, "onFailure: ", e));
    }

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
        final GetAllUnpairedRideRequestTask task = new GetAllUnpairedRideRequestTask();
        return task.run();
    }

    public MutableLiveData<Boolean> cancelRideRequest(RideRequest rideRequest) {
        CancelUnpairedRideRequestTask task = new CancelUnpairedRideRequestTask(rideRequest, CancelUnpairedRideRequestTask.Type.Cancel);
        return task.run();
    }

    public MutableLiveData<Boolean> removeRideRequest(RideRequest rideRequest) {
        CancelUnpairedRideRequestTask task = new CancelUnpairedRideRequestTask(rideRequest, CancelUnpairedRideRequestTask.Type.Remove);
        return task.run();
    }
}

class GetAllUnpairedRideRequestTask extends GetTaskSequencer<List<RideRequest>> {
    private List<DocumentSnapshot> snapshotList;

    @Override
    public MutableLiveData<List<RideRequest>> run() {
        getUnpairedCollection();
        return liveData;
    }

    private void getUnpairedCollection() {
        final MutableLiveData<List<RideRequest>> rideRequestMutableLiveData = new MutableLiveData<>();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(UnpairedRideListDAO.COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        snapshotList = task.getResult().getDocuments();
                        convertToRideRequestList();
                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
                        liveData.setValue(null);
                    }
                });
    }

    private void convertToRideRequestList() {
        List<RideRequest> rideRequestList = new ArrayList<>();
        liveData.setValue(rideRequestList);

        for (DocumentSnapshot snapshot : snapshotList) {
            UnpairedRideEntity unpairedRideEntity = snapshot.toObject(UnpairedRideEntity.class);

            if (unpairedRideEntity == null) {
                Log.e(TAG, "convertToRideRequestList: UnpairedEntity is null");
                continue;
            }

            unpairedRideEntity.getRideRequestReference()
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            RideRequestEntity rideRequestEntity = task.getResult().toObject(RideRequestEntity.class);
                            rideRequestList.add(new RideRequest(rideRequestEntity));
                            liveData.setValue(rideRequestList);
                        } else {
                            Log.e(TAG, "onComplete: ", task.getException());
                        }
                    });
        }
    }
}

class CancelUnpairedRideRequestTask extends GetTaskSequencer<Boolean> {
    static final String LOC = "Tomate: UnpairedRideListDAO: RemoveRiderRequestTask: ";

    private final RideRequest rideRequest;
    private DocumentReference documentReference;
    private final Type type;

    public enum Type {
        Cancel, // deactivates the ride request
        Remove // only remove unpaired reference
    }

    CancelUnpairedRideRequestTask(RideRequest rideRequest, Type type) {
        this.rideRequest = rideRequest;
        this.type = type;
    }

    @Override
    public MutableLiveData<Boolean> run() {
        removeSelfReferenceToUnpaired();
        return liveData;
    }

    private void removeSelfReferenceToUnpaired() {
        this.documentReference = rideRequest.getUnpairedReference();

        if (documentReference != null) {
            rideRequest.setUnpairedReference(null);
            RideRequestDAO rideRequestDAO = new RideRequestDAO();
            Task<Void> rideRequestTask = rideRequestDAO.saveModel(rideRequest);
            if (rideRequestTask != null) {
                rideRequestTask.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        switch (type) {
                            case Cancel:
                                getRider();
                                break;
                            case Remove:
                                deleteRequest();
                                break;
                            default:
                                Log.e(TAG, "removeSelfReferenceToUnpaired: Unknown type: " + type.toString());
                                liveData.setValue(false);
                                break;
                        }
                    } else {
                        liveData.setValue(false);
                    }
                });
            } else {
                deleteRequest();
            }
        } else {
            liveData.setValue(false);
        }
    }

    // region cancel
    private void getRider() {
        // remove request from active list in rider
        // put request in rider history
        rideRequest.getRiderReference()
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Log.d(TAG, LOC + "deactivateRequestFromRider: onSuccess:");
                    RiderEntity riderEntity = documentSnapshot.toObject(RiderEntity.class);
                    deactivateRequestFromRider(riderEntity);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, LOC + "onFailure: ", e);
                    liveData.setValue(false);
                });
    }

    private void deactivateRequestFromRider(RiderEntity riderEntity) {
        if (riderEntity != null) {
            riderEntity.deactivateRideRequest(rideRequest);
            RiderDAO riderDAO = new RiderDAO();
            riderDAO.save(riderEntity)
            .addOnSuccessListener(aVoid -> {
                Log.d(TAG, LOC + "receiveRider: onSuccess: ");
                getDriverReference();
            });
        } else {
            liveData.setValue(false);
        }
    }

    private void getDriverReference() {
        // remove request from driver active
        // put request in driver history
        Log.d(TAG, LOC + "getDriverReference: We are here");
        DocumentReference driverReference = rideRequest.getDriverReference();
        if (driverReference != null) {
            driverReference.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Log.d(TAG, LOC + "getDriverReference: ");
                        DriverEntity driver = documentSnapshot.toObject(DriverEntity.class);
                        deactivateRequestFromDriver(driver);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, LOC + "getDriverReference: onFailure: ", e);
                        liveData.setValue(false);
                    });
        } else {
            Log.i(TAG, LOC + "getDriverReference: null driverReference or no driver");
            deleteRequest();
        }
    }

    private void deactivateRequestFromDriver(DriverEntity driver) {
        if (driver != null) {
            driver.deactivateRideRequest(rideRequest);
            DriverDAO driverDAO = new DriverDAO();
            driverDAO.save(driver)
            .addOnSuccessListener(o -> {
                Log.d(TAG, LOC + "deactivateRequestFromDriver: onSuccess");
                deleteRequest();
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, "onFailure: ", e);
                liveData.setValue(false);
            });
        } else {
            liveData.setValue(false);
        }
    }
    // endregion cancel

    private void deleteRequest() {
        documentReference.delete()
        .addOnSuccessListener(aVoid -> liveData.setValue(true)).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, LOC + "deleteRequest: onFailure: ");
                liveData.setValue(false);
            }
        });
    }


}