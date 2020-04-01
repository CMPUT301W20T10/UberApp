package com.cmput301w20t10.uberapp;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.cmput301w20t10.uberapp.models.RideRequest;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.firestore.DocumentReference;

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

}
