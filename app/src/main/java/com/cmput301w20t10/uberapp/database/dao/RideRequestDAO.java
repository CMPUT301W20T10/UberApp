package com.cmput301w20t10.uberapp.database.dao;

import android.util.Log;

import com.cmput301w20t10.uberapp.database.base.DAOBase;
import com.cmput301w20t10.uberapp.database.base.ModelBase;
import com.cmput301w20t10.uberapp.database.entity.RideRequestEntity;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencer;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.Route;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import static android.content.ContentValues.TAG;

/**
 * Data Access Object (DAO) for RideRequestEntity model.
 * DAO contains specific operations that are concerned with the model they are associated with.
 *
 * @author Allan Manuba
 * @version 1.1.2
 * Change the update interval for GetAllRideRequest and make a generic Task class to process this
 *
 * @version 1.1.1
 */
public class RideRequestDAO extends DAOBase<RideRequestEntity, RideRequest> {
    static final String COLLECTION = "rideRequests";
    static final String LOC = "Tomate: RideRequestDAO: ";

    public RideRequestDAO() {}

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
                                                          int fareOffer,
                                                          LifecycleOwner owner) {
        CreateRideRequestTask task = new CreateRideRequestTask(rider, route, fareOffer, owner);
        return task.run();
    }

    /**
     * @param rider
     * @return
     */
    public MutableLiveData<List<RideRequest>> getAllActiveRideRequest(Rider rider) {
        GetAllActiveRideRequestTask task = new GetAllActiveRideRequestTask(rider);
        return task.run();
    }

    /**
     * @see DAOBase#saveModel(ModelBase)
     *
     * @param rideRequest
     * @return
     */
    @Override
    public MutableLiveData<Boolean> saveModel(RideRequest rideRequest) {
        RideRequestEntity entity = new RideRequestEntity();
        rideRequest.transferChanges(entity);
        return saveEntity(entity);
    }

    @Override
    protected String getCollectionName() {
        return COLLECTION;
    }

    @Override
    protected DAOBase<RideRequestEntity, RideRequest> create() {
        return new RideRequestDAO();
    }

    /**
     *
     * @param rideRequestEntity
     * @return
     */
    @Override
    protected MutableLiveData<RideRequest> createModelFromEntity(RideRequestEntity rideRequestEntity) {
        MutableLiveData<RideRequest> liveData = new MutableLiveData<>();
        liveData.setValue(new RideRequest(rideRequestEntity));
        return liveData;
    }

    @Override
    protected RideRequestEntity createObjectFromSnapshot(DocumentSnapshot snapshot) {
        return snapshot.toObject(RideRequestEntity.class);
    }

    public MutableLiveData<Boolean> cancelRequest(final RideRequest rideRequest,
                                                  final LifecycleOwner owner) {
        CancelRideRequestTask task = new CancelRideRequestTask(rideRequest, owner);
        return task.run();
    }

    public MutableLiveData<Rider> getRiderForRequest(final RideRequest rideRequest) {
        RiderDAO riderDAO = new RiderDAO();
        return riderDAO.getRiderFromRiderReference(rideRequest.getRiderReference());
    }

    public MutableLiveData<Boolean> acceptRequest(RideRequest rideRequest, Driver driver, LifecycleOwner owner) {
        DriverAcceptRequestTask task = new DriverAcceptRequestTask(rideRequest, driver, owner);
        return task.run();
    }

    /**
     * Gets all active ride request from rider.<p>
     * Usage:
     * <pre>
     RideRequestDAO dao = new RideRequestDAO();
     MutableLiveData<List<RideRequest>> liveData = dao.getAllActiveRideRequest(rider);
     liveData.observe(mainLifecycleOwner, rideList -> {
         // getAllActive sends an empty list knowing there's an internet connection
         // then updates for every item, increasing the size of the list
         // during these updates, when we lose connection, we get null instead
         if (rideList != null && rideList.size() >= 1) {
            RideRequest rideRequest = rideList.get(0);

            // do stuff with ride request
         } else if (rideList == null) {
            // no internet connection
         }
     });
     * </pre>
     *
     * @param   driver
     * @return  MutableLiveData which returns a List<RideRequest> or null when there's an error
     */
    public MutableLiveData<List<RideRequest>> getAllActiveRideRequest(Driver driver) {
        GetAllActiveRideRequestTask task = new GetAllActiveRideRequestTask(driver);
        return task.run();
    }

    /**
     * Updates the needed data to acknowledge that the driver has accepted the ride
     *
     * @param rideRequest
     * @param rider
     * @param owner
     * @return MutableLiveData which returns a Boolean which indicates whether the task was successful
     */
    public MutableLiveData<Boolean> acceptRideFromDriver(RideRequest rideRequest, Rider rider, LifecycleOwner owner) {
        RiderAcceptsRideFromDriverTask task = new RiderAcceptsRideFromDriverTask(rideRequest, rider, owner);
        return task.run();
    }

    /**
     * Updates the needed data to acknowledge that the ride was completed
     *
     * @param rideRequest
     * @param rider
     * @param owner
     * @return MutableLiveData which returns a Boolean indicating whether the task was successful
     */
    public MutableLiveData<Boolean> confirmRideCompletion(RideRequest rideRequest, Rider rider, LifecycleOwner owner) {
        RiderConfirmsCompletionTask task = new RiderConfirmsCompletionTask(rider, rideRequest, owner);
        return task.run();
    }
}

/**
 * Sequence of functions required to get all active ride requests
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
class GetAllActiveRideRequestTask extends GetTaskSequencer<List<RideRequest>> {
    private final static String LOC = RideRequestDAO.LOC + "GetAllActiveRideRequestTask: ";

    private final int finalSize;
    private final List<DocumentReference> referenceList;
    private final List<RideRequest> rideRequestList;

    GetAllActiveRideRequestTask(Rider rider) {
        this.referenceList = rider.getActiveRideRequestList();
        this.finalSize = this.referenceList.size();
        this.rideRequestList = new ArrayList<>();
    }

    GetAllActiveRideRequestTask(Driver driver) {
        this.referenceList = driver.getActiveRideRequestList();
        this.finalSize = this.referenceList.size();
        this.rideRequestList = new ArrayList<>();
    }

    @Override
    public void doFirstTask() {
        for (DocumentReference reference : referenceList) {
            reference.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            RideRequestEntity rideRequestEntity = task.getResult().toObject(RideRequestEntity.class);

                            if (rideRequestEntity != null) {
                                addRideRequest(rideRequestEntity);
                            } else {
                                Log.e(TAG, LOC + "onComplete: ", task.getException());
                                postResult(null);
                            }
                        } else {
                            Log.e(TAG, LOC + "onComplete: ", task.getException());
                            postResult(null);
                        }
                    });
        }
    }

    // todo put a timeout here to prevent issues later on
    private void addRideRequest(RideRequestEntity rideRequestEntity) {
        rideRequestList.add(new RideRequest(rideRequestEntity));
        if (rideRequestList.size() == finalSize) {
            postResult(rideRequestList);
        }
    }
}

/**
 * Sequence of functions required to create a ride request
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
class CreateRideRequestTask extends GetTaskSequencer<RideRequest> {
    private final static String LOC = RideRequestDAO.LOC + "CreateRideRequestTask: ";

    private final int fareOffer;
    private final Route route;
    private final Rider rider;
    private final LifecycleOwner owner;
    private RideRequestEntity requestEntity;

    CreateRideRequestTask(@NonNull Rider rider,
                          Route route,
                          int fareOffer,
                          LifecycleOwner owner) {
        this.rider = rider;
        this.route = route;
        this.fareOffer = fareOffer;
        this.owner = owner;
    }

    @Override
    public void doFirstTask() {
        addRequestEntity();
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
                    postResult(null);
                });
    }

    private void updateRideRequest() {
        RideRequestDAO rideRequestDAO = new RideRequestDAO();
        rideRequestDAO.saveEntity(requestEntity)
                .observe(owner, aBoolean -> {
                    if(aBoolean) {
                        addToUnpairedRideList();
                    } else {
                        Log.e(TAG, LOC + "updateRideRequest: ");
                        postResult(null);
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
                        Log.e(TAG, LOC + "addToUnpairedRideList: ", task.getException());
                        postResult(null);
                    }
                });
    }

    private void addReferenceToRider() {
        RideRequest rideRequestModel = new RideRequest(requestEntity);
        rider.addActiveRequest(rideRequestModel);
        RiderDAO riderDAO = new RiderDAO();
        riderDAO.saveModel(rider)
                .observe(owner, aBoolean -> {
                        if (aBoolean) {
                            postResult(rideRequestModel);
                        } else {
                            Log.e(TAG, LOC + "addReferenceToRider: ");
                            postResult(null);
                        }
                });
    }
}

/**
 * Sequence of functions required to cancel a ride request
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
class CancelRideRequestTask extends GetTaskSequencer<Boolean> {
    static final String LOC = "Tomate: RideRequestDAO: CancelRideRequestTask: ";
    private final RideRequest rideRequest;
    private final LifecycleOwner owner;

    CancelRideRequestTask(RideRequest rideRequest, LifecycleOwner owner) {
        this.rideRequest = rideRequest;
        this.owner = owner;
    }

    @Override
    public void doFirstTask() {
        removeRequestFromUnpaired();
    }

    private void removeRequestFromUnpaired() {
        rideRequest.setState(RideRequest.State.Cancelled);

        // remove request from active list in system
        // remove request from active list in rider
        // put request in rider history
        // remove request from driver active
        // put request in driver history
        UnpairedRideListDAO unpairedRideListDAO = new UnpairedRideListDAO();
        unpairedRideListDAO.cancelRideRequest(rideRequest, owner)
                .observe(owner, aBoolean -> {
                    if (aBoolean) {
                        updateRideRequest();
                    } else {
                        Log.e(TAG, LOC + "removeRequestFromUnpaired: Failure to remove rider request");
                        postResult(false);
                    }
                });
    }

    private void updateRideRequest() {
        RideRequestDAO rideRequestDAO = new RideRequestDAO();
        rideRequestDAO.saveModel(rideRequest)
        .observe(owner, aBoolean -> {
            if (!aBoolean) {
                Log.e(TAG, LOC + "updateRideRequest: ");
            }
            postResult(aBoolean);
        });
    }
}

/**
 * Sequence of functions required to acknowledge that a driver has accepted a request
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
class DriverAcceptRequestTask extends GetTaskSequencer<Boolean> {
    final static String LOC = "Tomate: RideRequestDAO: DriverAcceptRequestTask: ";
    private final RideRequest rideRequest;
    private final Driver driver;
    private final LifecycleOwner owner;

    DriverAcceptRequestTask(RideRequest rideRequest, Driver driver, LifecycleOwner owner) {
        this.rideRequest = rideRequest;
        this.driver = driver;
        this.owner = owner;
    }

    @Override
    public void doFirstTask() {
        changeRideRequestStatus();
    }

    private void changeRideRequestStatus() {
        rideRequest.setState(RideRequest.State.DriverFound);
        rideRequest.setDriverReference(driver.getDriverReference());
        RideRequestDAO rideRequestDAO = new RideRequestDAO();
        rideRequestDAO.saveModel(rideRequest)
                .observe(owner, aBoolean -> {
                        if (aBoolean) {
                            updateDriver();
                        } else {
                            Log.e(TAG, LOC + "changeRideRequestStatus: ");
                        }
                });
    }

    private void updateDriver() {
        driver.addActiveRideRequest(rideRequest);
        DriverDAO driverDAO = new DriverDAO();
        driverDAO.saveModel(driver)
            .observe(owner, aBoolean -> {
                if (aBoolean) {
                    Log.d(TAG, LOC + "updateDriver: ");
                    removeRequestFromUnpaired();
                } else {
                    Log.e(TAG, LOC + "updateDriver: onChanged: ");
                    postResult(false);
                }
            });
    }

    private void removeRequestFromUnpaired() {
        UnpairedRideListDAO unpairedRideListDAO = new UnpairedRideListDAO();
        unpairedRideListDAO.removeRideRequest(rideRequest, owner)
        .observe(owner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Log.d(TAG, LOC + "onChanged: ");
                    postResult(true);
                } else {
                    Log.e(TAG, LOC + "removeRequestFromUnpaired: onChanged: ");
                    postResult(false);
                }
            }
        });
    }
}

/**
 * Sequence of functions required to acknowledge that a driver has accepted a request
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
class RiderAcceptsRideFromDriverTask extends GetTaskSequencer<Boolean> {
    final static String LOC = "Tomate: RideRequestDAO: RiderAcceptsRideFromDriverTask: ";

    private final RideRequest rideRequest;
    private final Rider rider;
    private final LifecycleOwner owner;

    RiderAcceptsRideFromDriverTask(RideRequest rideRequest, Rider rider, LifecycleOwner owner) {
        this.rideRequest = rideRequest;
        this.rider = rider;
        this.owner = owner;
    }

    @Override
    public void doFirstTask() {
        acceptRideRequest();
    }

    private void acceptRideRequest() {
        // validate if right rider
        if (rideRequest.getRiderReference().getPath().equals(rideRequest.getRiderReference().getPath())) {
            rideRequest.setState(RideRequest.State.RiderAccepted);
            RideRequestDAO rideRequestDAO = new RideRequestDAO();
            rideRequestDAO.saveModel(rideRequest)
                    .observe(owner, aBoolean -> {
                        if (!aBoolean) {
                            Log.e(TAG, LOC + "onComplete: ");
                        }
                        postResult(aBoolean);
                    });
        } else {
            Log.w(TAG, "acceptRideRequest: Wrong rider");
            postResult(false);
        }
    }
}

/**
 * Sequence of functions required to acknowledge that a rider has confirmed ride completion
 * @see GetTaskSequencer
 *
 * @author Allan Manuba
 * @version 1.1.1
 */
class RiderConfirmsCompletionTask extends GetTaskSequencer<Boolean> {
    final static String LOC = "Tomate: RideRequestDAO: RiderConfirmsCompletionTask: ";
    private final RideRequest rideRequest;
    private final Rider rider;
    private final LifecycleOwner owner;

    RiderConfirmsCompletionTask(Rider rider, RideRequest rideRequest, LifecycleOwner owner) {
        this.rider = rider;
        this.rideRequest = rideRequest;
        this.owner = owner;
    }

    @Override
    public void doFirstTask() {
        updateRideRequest();
    }

    private void updateRideRequest() {
        if (rider.getRiderReference().getPath().equals(rideRequest.getRiderReference().getPath())) {
            rideRequest.setState(RideRequest.State.RideCompleted);
            RideRequestDAO rideRequestDAO = new RideRequestDAO();
            rideRequestDAO.saveModel(rideRequest)
                    .observe(owner, aBoolean -> {
                        if (!aBoolean) {
                            Log.e(TAG, LOC + "updateRideRequest: onComplete: ");
                        }
                        postResult(aBoolean);
                    });
        } else {
            Log.w(TAG, LOC + "updateRideRequest: Wrong rider");
            postResult(false);
        }
    }
}