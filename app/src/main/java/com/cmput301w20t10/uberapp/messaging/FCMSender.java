package com.cmput301w20t10.uberapp.messaging;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

public class FCMSender {

    public static void sendMessage(String destinationToken) {
        RemoteMessage message = new RemoteMessage.Builder(destinationToken)
                .addData("header", "Ride complete")
                .addData("body", "Your ride has been completed!")
                .build();

        FirebaseMessaging.getInstance().send(message);
    }

}
