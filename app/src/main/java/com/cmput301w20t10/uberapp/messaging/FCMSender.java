package com.cmput301w20t10.uberapp.messaging;

import android.content.Context;

import com.android.volley.toolbox.JsonObjectRequest;
import com.cmput301w20t10.uberapp.Application;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class FCMSender {

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "key=AAAAwlDBNpc:APA91bHKUp02-ivB2w20PdXXKTCyBYVWs7rAFiKfF84oO42IhGoebxPsH6mF57bwis5JH6Lo355hIHPYOKeLeG5NcmdnswqIyUfXRBXibA1JOGtsJk9C5XD9LFMkShXG8ZHBLLx_vq_F";
    private static final String CONTENT_TYPE = "application/json";


    /**
     * Sends a message to the given user identified by their context notifying that the ride has been completed
     *
     * @param context - The application context
     * @param destinationToken - The user identificator
     */
    public static void composeMessage(@NotNull final Context context, String destinationToken) {
        JSONObject message = new JSONObject();
        try {
            message.put("to", destinationToken);
            JSONObject notification = new JSONObject();
            notification.put("body", "You have reached your destination!");
            notification.put("title", "Ride complete!");
            message.put("notification", notification);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendMessage(context, message);
    }

    /**
     * Sends the message through a http post using Volley API
     *
     * @param context - The application context
     * @param notification - The notification to be sent
     */
    private static void sendMessage(@NotNull final Context context, JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                (response) -> {
                }, (error) -> {
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", SERVER_KEY);
                params.put("Content-Type", CONTENT_TYPE);
                return params;
            }
        };
        Application.getInstance().addToRequestQueue(context, jsonObjectRequest);

    }

}
