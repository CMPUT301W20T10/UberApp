package com.cmput301w20t10.uberapp.Directions;

  /*
     * fetchURL is code from GITHUB used to grab URL for route drawing
     * URL of master: https://github.com/Vysh01/android-maps-directions
     * Author: Vysh01, https://github.com/Vysh01
     * Website: https://github.com/
     */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
/*
 * FetchURL is code from GITHUB  used to grab URL for route drawing
 * URL of master: https://github.com/Vysh01/android-maps-directions
 * Author: Vysh01, https://github.com/Vysh01
 * Website: https://github.com/
 * URL of class: https://github.com/Vysh01/android-maps-directions/blob/master/app/src/main/java/com/thecodecity/mapsdirection/directionhelpers/FetchURL.java
 */

public class FetchURL extends AsyncTask<String, Void, String> {
    Context mContext;
    String directionMode = "driving";

    public FetchURL(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    protected String doInBackground(String... strings) {
        // For storing data from web service
        String ResponseString = "";
        directionMode = strings[1];
        try {
            // Fetching the data from web service
            ResponseString = downloadUrl(strings[0]);
            Log.d("mylog", "Background task data " + ResponseString);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return  ResponseString;
    }
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        PointsParser parserTask = new PointsParser(mContext, directionMode);
        // Invokes the thread for parsing the JSON data
        parserTask.execute(s);
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();
            // Connecting to url
            urlConnection.connect();
            // Reading data from url
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("mylog", "Downloaded URL: " + data);
            br.close();
        } catch (Exception e) {
            Log.d("mylog", "Exception downloading URL: " + e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
}