package com.cmput301w20t10.uberapp.database;

import android.media.DeniedByServerException;
import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
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
    private static final String COLLECTION = "rideRequests";

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
    public MutableLiveData<RideRequest> createRideRequest(Rider rider,
                                                          Route route,
                                                          int fareOffer) {
        MutableLiveData<RideRequest> rideRequestMutableLiveData = new MutableLiveData<>();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RideRequestEntity entity = new RideRequestEntity(rider, route, fareOffer);
        db.collection(COLLECTION)
                .add(entity)
                .addOnSuccessListener(rideRequestReference -> {
                    rideRequestReference.update(
                            RideRequestEntity.FIELD_RIDE_REQUEST_REFERENCE,
                            rideRequestReference);
                    entity.setRideRequestReference(rideRequestReference);
                    entity.setRiderReference(rider.getRiderReference());
                    saveEntity(entity);
                    RideRequest model = new RideRequest(entity);
                    Log.d(TAG, "createRideRequest: Rider: " + model.getRiderReference().getPath());
                    Log.d(TAG, "createRideRequest: Request: " + model.getRideRequestReference().getPath());

                    // add to active rides
                    UnpairedRideListDAO unpairedRideListDAO = new UnpairedRideListDAO();
                    unpairedRideListDAO.addRideRequest(entity);

                    // add reference to rider
                    rider.addActiveRequest(model);
                    RiderDAO riderDAO = new RiderDAO();
                    riderDAO.save(rider);
                    Log.d(TAG, "createRideRequest: Request in active list: " + rider.getActiveRideRequestList().get(0).getPath());

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

    public MutableLiveData<List<RideRequest>> getUnpairedRideRequest() {
        Log.d(TAG, "getUnpairedRideRequest: run: corn");
        MutableLiveData<List<RideRequest>> mutableLiveData = new MutableLiveData<>();
        List<RideRequest> rideList = new ArrayList<>();
        mutableLiveData.setValue(rideList);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(COLLECTION)
                .whereEqualTo(RideRequestEntity.Field.STATE.toString(), RideRequest.State.Active.ordinal())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Log.d(TAG, "getUnpairedRideRequest: run: " + snapshot.getData().toString());
                            RideRequestEntity entity = snapshot.toObject(RideRequestEntity.class);
                            RideRequest model = new RideRequest(entity);
                            rideList.add(model);
                            mutableLiveData.setValue(rideList);
                        }
                    } else {
                        Log.e(TAG, "onComplete: ", task.getException());
                    }
                });

        return mutableLiveData;
    }

    // todo: fix
    public MutableLiveData<List<RideRequest>> getAllActiveRideRequest(Rider rider) {
        MutableLiveData<List<RideRequest>> mutableLiveData = new MutableLiveData<>();
        List<RideRequest> rideList = new ArrayList<>();
        mutableLiveData.setValue(rideList);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        for (DocumentReference reference :
                rider.getActiveRideRequestList()) {
            reference.get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                RideRequestEntity rideRequestEntity = task.getResult().toObject(RideRequestEntity.class);
                                rideList.add(new RideRequest(rideRequestEntity));
                                mutableLiveData.setValue(rideList);
                            } else {
                                Log.e(TAG, "onComplete: ", task.getException());
                            }
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
    public Task saveEntity(final RideRequestEntity entity) {
        final DocumentReference reference = entity.getRideRequestReference();
        Task task = null;

        if (reference != null) {
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
}
