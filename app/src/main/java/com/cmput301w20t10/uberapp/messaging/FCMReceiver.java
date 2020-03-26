package com.cmput301w20t10.uberapp.messaging;

import android.util.Log;

import com.cmput301w20t10.uberapp.Application;
import com.cmput301w20t10.uberapp.activities.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * A class to receive Firebase Cloud Messaging (FCM) messages and to handle them
 *
 * @author Joshua Mayer
 */
public class FCMReceiver extends FirebaseMessagingService {

    @Override
    public void onNewToken(String token) {
        Log.d("UBER FCM", "Refreshed token: " + token);
        Application.getInstance().setMessagingToken(token);
        // Todo(Joshua): Save the user's token in the database
    }


    /**
     * Called when a message is received
     *
     * @param remoteMessage - Object representing the message received from Firebase Cloud Messaging
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        /* There are two types of messages, data and notification messages.
         *
         * Data Messages: Are handled here in onMessageReceived whether the app is in the foreground
         * or background. Data messages are the type traditionally used with GCM.
         *
         * Notification Messages: are only received here in onMessageReceived whe the app is in the
         * foreground. When the app is in the background, an automatically generated notification is
         * displayed.
         *
         * When the user taps on the the notification, they are bought back to the app. Messages
         * containing both notification and data payloads are treated as notification messages. The
         * Firebase console always sends notification messages.
         *
         * https://firebase.google.com/docs/cloud-messaging/concept-options
         *
         */

        Log.d("UBER FCM", "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d("UBER FCM", "Message data payload: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d("UBER FCM", "Message notification body: " + remoteMessage.getNotification().getBody());
            NotificationService.sendNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody(), getApplicationContext(), LoginActivity.class);
        }


    }

}
