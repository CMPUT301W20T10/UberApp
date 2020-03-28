package com.cmput301w20t10.uberapp.messaging;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class FCMSender {

    private static final String FCM_API = "https://fcm.googleapis.com/fcm/send";
    private static final String SERVER_KEY = "key=" + "";
    private static final String CONTENT_TYPE = "application/json";


    private void sendNotification(Context context, JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                response -> Log.d("UBER FCM", "onResponse: " + response.toString()),
                (error) -> {
                    Toast.makeText(context, "Request error", Toast.LENGTH_LONG).show();
                    Log.e("UBER FCM", "onErrorResponse: Didn't work");
                });
    }


}
