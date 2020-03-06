package com.cmput301w20t10.uberapp.database;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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
        Log.d(TAG, "createRideRequest: Here");

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        RideRequestEntity entity = new RideRequestEntity(rider, route, fareOffer);
        db.collection(COLLECTION)
                .add(entity)
                .addOnSuccessListener(rideRequestReference -> {
                    rideRequestReference.update(
                            RideRequestEntity.FIELD_RIDE_REQUEST_REFERENCE,
                            rideRequestReference);
                    entity.setRideRequestReference(rideRequestReference);
                    save(entity);
                    RideRequest model = new RideRequest(entity);

                    // add to active rides
                    UnpairedRideListDAO unpairedRideListDAO = new UnpairedRideListDAO();
                    unpairedRideListDAO.addRideRequest(entity);

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

    /**
     * Save changes in RideRequestEntity
     * @param entity
     * @return
     * Returns a Task object that can be observed whether it is successful or not.
     */
    @Override
    public Task save(final RideRequestEntity entity) {
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

            if (dirtyPairMap.size() > 0) {
                task = reference.update(dirtyPairMap);
            }
        }

        return task;
    }
}
