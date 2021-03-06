package com.cmput301w20t10.uberapp.activities;

import android.content.Context;
import android.content.SharedPreferences;

import com.cmput301w20t10.uberapp.models.RideRequestListContent;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

public class SharedPref {
    SharedPreferences mySharedPref;

    public SharedPref(Context context) {
        mySharedPref = context.getSharedPreferences("sharedPreferences",Context.MODE_PRIVATE);
    }

    public void setUsername(String username) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putString("Username", username);
        editor.commit();
    }

    public String loadUsername() {
        return mySharedPref.getString("Username", "");
    }

    public void setPassword(String password) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putString("Password", password);
        editor.commit();
    }

    public String loadPassword() {
        return mySharedPref.getString("Password", "");
    }

    public void setUserType(String userType) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putString("UserType", userType);
        editor.commit();
    }

    public String loadUserType() {
        return mySharedPref.getString("UserType", "");
    }

    public void setHomeActivity(String homeActivity) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putString("HomeActivity", homeActivity);
        editor.commit();
    }

    public String loadHomeActivity() {
        return mySharedPref.getString("HomeActivity", "");
    }

    public void setNightModeState(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("NightMode", state);
        editor.commit();
    }

    public Boolean loadNightModeState() {
        return mySharedPref.getBoolean("NightMode", false);
    }

    public void setRememberMe(Boolean state) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.putBoolean("RememberMe", state);
        editor.apply();
    }

    public boolean loadRememberMeState() {
        return mySharedPref.getBoolean("RememberMe", false);
    }

    public void setRideRequest(RideRequestListContent oldRideRequestContent) {
        SharedPreferences.Editor editor = mySharedPref.edit();
        Gson gson = new Gson();
        RideRequestListContent rideRequestContent = new RideRequestListContent(
                oldRideRequestContent.getUsername(),
                oldRideRequestContent.getDistance(),
                oldRideRequestContent.getOffer(),
                oldRideRequestContent.getImageURL(),
                oldRideRequestContent.getFirstName(),
                oldRideRequestContent.getLastName(),
                oldRideRequestContent.getStartDest(),
                oldRideRequestContent.getEndDest());
        String json = gson.toJson(rideRequestContent);
        editor.putString("RideRequest", json);
        editor.commit();
    }

    public RideRequestListContent loadRideRequest() {
        Gson gson = new Gson();
        String json = mySharedPref.getString("RideRequest", "");
        return gson.fromJson(json, RideRequestListContent.class);
    }

    public void eraseContents() {
        Boolean nightModeState = mySharedPref.getBoolean("NightMode", false);
        SharedPreferences.Editor editor = mySharedPref.edit();
        editor.clear();
        editor.putBoolean("NightMode", nightModeState);
        editor.putBoolean("RememberMe", false);
        editor.apply();
        System.out.println("ERASED");
    }
}
