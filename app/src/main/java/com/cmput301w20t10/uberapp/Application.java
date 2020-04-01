package com.cmput301w20t10.uberapp;

import android.app.ActivityManager;
import android.content.Context;
import android.icu.text.UnicodeSet;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cmput301w20t10.uberapp.database.base.EntityBase;
import com.cmput301w20t10.uberapp.database.dao.DriverDAO;
import com.cmput301w20t10.uberapp.database.dao.RiderDAO;
import com.cmput301w20t10.uberapp.database.util.GetTaskSequencerLifecycleOwner;
import com.cmput301w20t10.uberapp.models.Driver;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.Route;
import com.cmput301w20t10.uberapp.models.Rider;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.DocumentReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.MutableLiveData;

/**
 * This singleton was made to hold the information of the current user logged into the application.
 */
public final class Application {

    private static final Application INSTANCE = new Application();

    private volatile RequestQueue requestQueue;
    private volatile User user;
    private volatile String messagingToken;
    private volatile String prevActivity;
    private volatile String activeRidePath;
    private volatile Route route;
    private volatile DocumentReference rideDocument;

    private volatile RideRequest selectedHistoryRequest;

    private Application() {
        this.user = null;
    }

    // This needs to be sync because multiple threads are active
    public static synchronized Application getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves the current user data
     *
     * @return The current user, if no user has been set, null is returned
     */
    public User getCurrentUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessagingToken() {
        return messagingToken;
    }

    public void setMessagingToken(String messagingToken) {
        this.messagingToken = messagingToken;
    }

    public String getPrevActivity() {
        return prevActivity;
    }

    public void setPrevActivity(String prevActivity) {
        this.prevActivity = prevActivity;
    }

    public String getActiveRidePath() {
        return activeRidePath;
    }

    public void setActiveRidePath(String activeRidePath) {
        this.activeRidePath = activeRidePath;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public DocumentReference getRideDocument() {
        return rideDocument;
    }

    public void setRideDocument(DocumentReference rideDocument) {
        this.rideDocument = rideDocument;
    }

    public void setSelectedHistoryRequest(RideRequest request) {
        selectedHistoryRequest = request;
    }

    public RideRequest getSelectedHistoryRequest() {
        return selectedHistoryRequest;
    }

    public synchronized boolean isInBackground() {
        ActivityManager.RunningAppProcessInfo process = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(process);
        return process.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
    }

    // Todo(Joshua): This should be moved to FCMSender
    private RequestQueue getRequestQueue(Context context) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    // Todo(Joshua): This should be moved to FCMSender
    public <T> void addToRequestQueue(Context context, Request<T> req) {
        getRequestQueue(context).add(req);
    }

    /**
     * If you're unsure whether Application holds the latest data,
     * use this function to fetch the latest data and update the User object in Application.
     * <p>
     * We're mocking a lifecycle owner inside because we want to get this update even if the activity
     * or other view that called it was already destroyed.
     *
     * @return      MutableLiveData<User> that may update once with either:
     * <ul>
     *     <li>User object that you may upcast to Rider or Driver</li>
     *     <li>null which would mean a connection loss, no Firestore cache, or some other error</li>
     * </ul>
     */
    public MutableLiveData<User> getLatestUserData() {
        MutableLiveData<User> userData = new MutableLiveData<>();
        GetTaskSequencerLifecycleOwner owner = new GetTaskSequencerLifecycleOwner();

        if (user instanceof Rider) {
            MutableLiveData<Rider> riderData = getLatestRiderData(owner);
            assert riderData != null; // We know it's a rider anyway
            riderData.observe(owner, rider -> {
                setUser(rider);
                userData.setValue(rider);
                owner.callEvent(Lifecycle.Event.ON_DESTROY);
            });
        } else if (user instanceof Driver) {
            MutableLiveData<Driver> driverData = getLatestDriverData(owner);
            assert driverData != null; // We know it's a driver anyway
            driverData.observe(owner, driver -> {
                setUser(driver);
                userData.setValue(driver);
                owner.callEvent(Lifecycle.Event.ON_DESTROY);
            });
        } else {
            Log.e("Tomate", "Application: getLatestUserData: Invalid subclass of user: " + user.getClass().toString());
            userData.setValue(null);
            owner.callEvent(Lifecycle.Event.ON_DESTROY);
        }

        return userData;
    }


    @Nullable
    private MutableLiveData<Driver> getLatestDriverData(LifecycleOwner owner) {
        MutableLiveData<Driver> liveData = null;

        if (this.user instanceof Driver) {
            Driver driver = (Driver) user;
            DriverDAO riderDAO = new DriverDAO();
            liveData = riderDAO.getModelByReference(driver.getDriverReference());
            liveData.observe(owner, latestRider -> this.user = latestRider);
        } else {
            Log.e("Tomate", "Application: getLatestDriverData: Cannot upcast user to Driver: ID: " + user.getUserReference().getPath());
        }

        return liveData;
    }

    @Nullable
    private MutableLiveData<Rider> getLatestRiderData(LifecycleOwner owner) {
         MutableLiveData<Rider> liveData = null;

         if (this.user instanceof Rider) {
             Rider rider = (Rider) user;
             RiderDAO riderDAO = new RiderDAO();
             liveData = riderDAO.getModelByReference(rider.getRiderReference());
             liveData.observe(owner, latestRider -> this.user = latestRider);
         } else {
             Log.e("Tomate", "Application: getLatestRiderData: Cannot upcast user to Rider: ID: " + user.getUserReference().getPath());
         }

         return liveData;
    }

}
