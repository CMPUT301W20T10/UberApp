package com.cmput301w20t10.uberapp.Directions;
import  com.cmput301w20t10.uberapp.activities.RiderMainActivity;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.IOException;

public class TaskRequestDirections extends AsyncTask <String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        String responseString = "";
        try {
            responseString = RiderMainActivity.requestDirection(strings[0]);
        }catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
