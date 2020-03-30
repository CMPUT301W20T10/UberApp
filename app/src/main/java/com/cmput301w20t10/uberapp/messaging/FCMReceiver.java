package com.cmput301w20t10.uberapp.messaging;

import android.util.Log;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.activities.LoginActivity;
import com.cmput301w20t10.uberapp.models.User;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * A class to receive Firebase Cloud Messaging (FCM) messages and to handle them
 *
 * @author Joshua Mayer
 */
public class FCMReceiver extends FirebaseMessagingService {

    // This is initially set to null, meaning it will ignore messages received
    private static volatile OnMessageReceivedCallback onMessageReceivedCallback = null;

    /**
     * Determines what to do when a message is received. This can be set manually or ignored if
     * no function is set
     * @param callback - The function to be called when a message is received
     */
    public static synchronized void setOnMessageReceivedCallback(OnMessageReceivedCallback callback) {
        onMessageReceivedCallback = callback;
    }

    /**
     * When the token is renewed, we need to update it
     *
     * @param token - the new updated token
     */
    @Override
    public void onNewToken(String token) {
        Log.d("UBER FCM", "Refreshed token: " + token);
        Application.getInstance().setMessagingToken(token);
        User user = Application.getInstance().getCurrentUser();
        user.setFCMToken(token);
        Application.getInstance().setUser(user);
    }


    /**
     * Called when a message is received. If the app is running in the background, a push notification
     * is sent to the user
     *
     * @param remoteMessage - Object representing the message received from Firebase Cloud Messaging
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /*
         * When a message is received, it is handled here. Here is where you would specify
         * where the user is sent when the message is received.
         */
        // Todo(Joshua): Determine if the application is in the background or not
        if (remoteMessage.getNotification() != null) {
            if(onMessageReceivedCallback != null) {
                onMessageReceivedCallback.onMessageReceived();
            }
        }


    }

    public interface OnMessageReceivedCallback {
        void onMessageReceived();
    }

}
