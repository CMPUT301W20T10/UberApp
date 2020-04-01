package com.cmput301w20t10.uberapp.database.dao;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for UnpairedRideListDAO model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 * @version 1.4.2
 * Add dependency injection
 * @version 1.1.1
 */
public class UnpairedRideListDAO {
    static final String COLLECTION = "unpairedRideList";
    static final String LOC = "Tomate: UnpairedRideListDAO: ";
    private final FirebaseFirestore db;

    public UnpairedRideListDAO() {
        db = FirebaseFirestore.getInstance();
    }

    public UnpairedRideListDAO(FirebaseFirestore db) {
        this.db = db;
    }

    /**
     * Adds a ride request allowing searchable ride requests for drivers
     *
     * @param requestEntity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    public Task<DocumentReference> addRideRequest(RideRequestEntity requestEntity) {
        final UnpairedRideEntity unpairedRide = new UnpairedRideEntity(requestEntity.getRideRequestReference());
        MutableLiveData<DocumentReference> liveData = new MutableLiveData<>();
        return db.collection(COLLECTION)
            .add(unpairedRide)
            .addOnSuccessListener(documentReference -> {
                requestEntity.setUnpairedReference(documentReference);
                RideRequestDAO dao = new RideRequestDAO();
                dao.saveEntity(requestEntity);
            })
            .addOnFailureListener(e -> {
                Log.e(TAG, LOC + "onFailure: ", e);
            });
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
        final GetAllUnpairedRideRequestTask task = new GetAllUnpairedRideRequestTask(db);
        return task.run();
    }

    public MutableLiveData<Boolean> cancelRideRequest(RideRequest rideRequest, LifecycleOwner owner) {
        RemoveUnpairedRideRequestTask task = RemoveUnpairedRideRequestTask.cancel(rideRequest, owner, db);
        return task.run();
    }

    public MutableLiveData<Boolean> removeRideRequest(RideRequest rideRequest, LifecycleOwner owner) {
        RemoveUnpairedRideRequestTask task = RemoveUnpairedRideRequestTask.remove(rideRequest, owner, db);
        return task.run();
    }
}

/**
 * Sequence of function required to get all unpaired ride requests
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.4.3
 * Add dependency injection
 * @version 1.1.2
 * Put additional postResults to prevent an infinite wait
 * @version 1.1.1
 */
class GetAllUnpairedRideRequestTask extends GetTaskSequencer<List<RideRequest>> {
    final static String LOC = UnpairedRideListDAO.LOC + "GetAllUnpairedRideRequestTask: ";

    private List<DocumentSnapshot> snapshotList;

    GetAllUnpairedRideRequestTask(FirebaseFirestore db) {
        super(db);
    }

    @Override
    public void doFirstTask() {
        getUnpairedCollection();
    }

    private void getUnpairedCollection() {
        final FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(UnpairedRideListDAO.COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        snapshotList = task.getResult().getDocuments();
                        convertToRideRequestList();
                    } else {
                        Log.e(TAG, LOC + "onComplete: ", task.getException());
                        postResult(null);
                    }
                });
    }

    private void convertToRideRequestList() {
        List<RideRequest> rideRequestList = new ArrayList<>();
        postResult(rideRequestList);

        for (DocumentSnapshot snapshot : snapshotList) {
            UnpairedRideEntity unpairedRideEntity = snapshot.toObject(UnpairedRideEntity.class);

            if (unpairedRideEntity == null) {
                Log.e(TAG, LOC + "convertToRideRequestList: UnpairedEntity is null");
                continue;
            }

            unpairedRideEntity.getRideRequestReference()
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot =  task.getResult();
                            RideRequestEntity rideRequestEntity = null;
                            if (documentSnapshot != null) {
                                rideRequestEntity = documentSnapshot.toObject(RideRequestEntity.class);
                            }

                            if (rideRequestEntity != null) {
                                rideRequestList.add(new RideRequest(rideRequestEntity));
                                postResult(rideRequestList);
                            } else if (documentSnapshot != null) {
                                Log.e(TAG, LOC + "convertToRideRequestList: Invalid ride request format found.");

                                Map map =  documentSnapshot.getData();
                                if (map != null) {
                                    Log.e(TAG, LOC + "convertToRideRequestList: " + map.toString());
                                } else {
                                    Log.e(TAG, "convertToRideRequestList: map is null");
                                }
                            } else {
                                Log.e(TAG, "convertToRideRequestList: result is null");
                            }
                        } else {
                            Log.e(TAG, LOC + "onComplete: ", task.getException());
                            postResult(null);
                        }
                    });
        }
    }
}


/**
 * Sequence of function required to remove a ride request from unpaired ride requests
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.6.3
 * Fix null exception when cancelling after driver and rider matched
 * @version 1.4.2
 * Add dependency injection
 * @version 1.1.1
 */
class RemoveUnpairedRideRequestTask extends GetTaskSequencer<Boolean> {
    static final String LOC = UnpairedRideListDAO.LOC + "RemoveRiderRequestTask: ";

    private final RideRequest rideRequest;
    private final LifecycleOwner owner;
    private DocumentReference documentReference;
    private final Type type;

    private enum Type {
        Cancel, // deactivates the ride request
        Remove // only remove unpaired reference
    }

    private RemoveUnpairedRideRequestTask(RideRequest rideRequest, Type type, LifecycleOwner owner, FirebaseFirestore db) {
        super(db);
        this.rideRequest = rideRequest;
        this.type = type;
        this.owner = owner;
    }

    static RemoveUnpairedRideRequestTask cancel(RideRequest rideRequest, LifecycleOwner owner, FirebaseFirestore db) {
        return new RemoveUnpairedRideRequestTask(rideRequest, Type.Cancel, owner, db);
    }

    static RemoveUnpairedRideRequestTask remove(RideRequest rideRequest, LifecycleOwner owner, FirebaseFirestore db) {
        return new RemoveUnpairedRideRequestTask(rideRequest, Type.Remove, owner, db);
    }

    @Override
    public void doFirstTask() {
        removeSelfReferenceToUnpaired();
    }

    private void removeSelfReferenceToUnpaired() {
        this.documentReference = rideRequest.getUnpairedReference();

        if (documentReference != null) {
            rideRequest.setUnpairedReference(null);
            RideRequestDAO rideRequestDAO = new RideRequestDAO();
            rideRequestDAO.saveModel(rideRequest)
            .observe(owner, aBoolean -> {
                if (aBoolean) {
                switch (type) {
                    case Cancel:
                        getRider();
                        break;
                    case Remove:
                        deleteRequest();
                        break;
                    default:
                        Log.e(TAG, "removeSelfReferenceToUnpaired: Unknown type: " + type.toString());
                        postResult(false);
                        break;
                }
            } else {
                    postResult(false);
            }
            });
        } else {
            // not in unpaired
            getRider();
        }
    }

    // region cancel
    private void getRider() {
        // remove request from active list in rider
        // put request in rider history
        rideRequest.getRiderReference()
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    RiderEntity riderEntity = documentSnapshot.toObject(RiderEntity.class);
                    deactivateRequestFromRider(riderEntity);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, LOC + "onFailure: ", e);
                    postResult(false);
                });
    }

    private void deactivateRequestFromRider(RiderEntity riderEntity) {
        if (riderEntity != null) {
            riderEntity.deactivateRideRequest(rideRequest);
            RiderDAO riderDAO = new RiderDAO();
            riderDAO.saveEntity(riderEntity)
                    .observe(owner, aBoolean -> {
                        if (aBoolean) {
                            getDriverReference();
                        } else {
                            Log.e(TAG, LOC +"deactivateRequestFromRider: ");
                            postResult(false);
                        }
                    });
        } else {
            postResult(false);
        }
    }

    private void getDriverReference() {
        // remove request from driver active
        // put request in driver history
        DocumentReference driverReference = rideRequest.getDriverReference();
        if (driverReference != null) {
            driverReference.get()
                    .addOnSuccessListener(documentSnapshot -> {
                        DriverEntity driver = documentSnapshot.toObject(DriverEntity.class);
                        deactivateRequestFromDriver(driver);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, LOC + "getDriverReference: onFailure: ", e);
                        postResult(false);
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
            driverDAO.saveEntity(driver)
                    .observe(owner, aBoolean -> {
                        if (aBoolean) {
                            deleteRequest();
                        } else {
                            Log.e(TAG, LOC + "deactivateRequestFromDriver: ");
                            postResult(false);
                        }
                    });
        } else {
            postResult(false);
        }
    }
    // endregion cancel

    private void deleteRequest() {
        if (documentReference == null) {
            postResult(true);
            return;
        }

        documentReference.delete()
        .addOnSuccessListener(aVoid -> postResult(true)).addOnFailureListener(e -> {
            postResult(true);
        })
        .addOnFailureListener(e -> {
            Log.e(TAG, "onFailure: ", e);
            postResult(false);
        });
    }


}