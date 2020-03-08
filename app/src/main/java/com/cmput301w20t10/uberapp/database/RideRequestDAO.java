package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for RideRequestEntity model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 */
public class RideRequestDAO extends DAOBase<RideRequestEntity> {
    static final String COLLECTION = "rideRequests";

    /**
     * Create a ride request
     *
     * @param rider
     * @param route
     * @param fareOffer
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
     *     <ul><b>Non-null RideRequestEntity object:</b> the RideRequestEntity object's fields were successfully added to the database.</ul>
     *     <ul><b>Null:</b> Registration failed.</ul>
     * </li>
     */
    public MutableLiveData<RideRequest> createRideRequest(@NonNull Rider rider,
                                                          Route route,
                                                          int fareOffer) {
        CreateRideRequestTask task = new CreateRideRequestTask(rider, route, fareOffer);
        return task.run();
    }

    public MutableLiveData<List<RideRequest>> getAllActiveRideRequest(Rider rider) {
        MutableLiveData<List<RideRequest>> mutableLiveData = new MutableLiveData<>();
        List<RideRequest> rideList = new ArrayList<>();
        mutableLiveData.setValue(rideList);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (DocumentReference reference :
                rider.getActiveRideRequestList()) {
            reference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            RideRequestEntity rideRequestEntity = task.getResult().toObject(RideRequestEntity.class);
                            rideList.add(new RideRequest(rideRequestEntity));
                            mutableLiveData.setValue(rideList);
                        } else {
                            Log.e(TAG, "onComplete: ", task.getException());
                        }
                    });
        }

        return mutableLiveData;
    }

    /**
     * Save changes in RideRequestEntity
     * @param entity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    @Override
    public Task<Void> saveEntity(final RideRequestEntity entity) {
        final DocumentReference reference = entity.getRideRequestReference();
        Task<Void> task = null;

        if (reference != null) {
            Log.e(TAG, "saveEntity: 1 : " + Arrays.toString(entity.getDirtyFieldSet()));
            final Map<String, Object> dirtyPairMap = new HashMap<>();

            for (RideRequestEntity.Field field:
                    entity.getDirtyFieldSet()) {
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
                    case UNPAIRED_REFERENCE:
                        Log.e(TAG, "saveEntity: 3");
                        value = entity.getUnpairedReference();
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

            entity.clearDirtyStateSet();

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }
        }

        return task;
    }

    public Task saveModel(final RideRequest model) {
        return saveEntity(new RideRequestEntity(model));
    }

    public Task cancelRequest(final RideRequest rideRequest) {
        rideRequest.setState(RideRequest.State.Cancelled);

        // remove request from active list in system
        // remove request from active list in rider
        // put request in rider history
        // remove request from driver active
        // put request in driver history
        UnpairedRideListDAO unpairedRideListDAO = new UnpairedRideListDAO();
        unpairedRideListDAO.removeRiderRequest(rideRequest);

        return saveModel(rideRequest);
    }

    public MutableLiveData<Rider> getRiderForRequest(final RideRequest rideRequest) {
        RiderDAO riderDAO = new RiderDAO();
        return riderDAO.getRiderFromRiderReference(rideRequest.getRiderReference());
    }
}

class CreateRideRequestTask extends GetTaskSequencer<RideRequest> {
    private final int fareOffer;
    private final Route route;
    private final Rider rider;
    private RideRequestEntity requestEntity;

    CreateRideRequestTask(@NonNull Rider rider,
                          Route route,
                          int fareOffer) {
        this.rider = rider;
        this.route = route;
        this.fareOffer = fareOffer;
    }

    @Override
    public MutableLiveData<RideRequest> run() {
        addRequestEntity();
        return liveData;
    }

    private void addRequestEntity() {
        this.requestEntity = new RideRequestEntity(rider, route, fareOffer);
        db.collection(RideRequestDAO.COLLECTION)
                .add(requestEntity)
                .addOnSuccessListener(rideRequestReference -> {
                    requestEntity.setRideRequestReference(rideRequestReference);
                    requestEntity.setRiderReference(rider.getRiderReference());
                    updateRideRequest();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onFailure: ", e);
                    liveData.setValue(null);
                });
    }

    private void updateRideRequest() {
        RideRequestDAO rideRequestDAO = new RideRequestDAO();
        rideRequestDAO.saveEntity(requestEntity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addToUnpairedRideList();
                    } else {
                        Log.e(TAG, "updateRideRequest: ", task.getException());
                        liveData.setValue(null);
                    }
                });
    }

    private void addToUnpairedRideList() {
        // add to active rides
        UnpairedRideListDAO unpairedRideListDAO = new UnpairedRideListDAO();
        unpairedRideListDAO.addRideRequest(requestEntity)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addReferenceToRider();
                    } else {
                        Log.e(TAG, "addToUnpairedRideList: ", task.getException());
                        liveData.setValue(null);
                    }
                });
    }

    private void addReferenceToRider() {
        RideRequest model = new RideRequest(requestEntity);
        rider.addActiveRequest(model);
        RiderDAO riderDAO = new RiderDAO();
        riderDAO.save(rider)
        .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveData.setValue(model);
            } else {
                Log.e(TAG, "onComplete: ", task.getException());
                liveData.setValue(null);
            }
        });
    }
}