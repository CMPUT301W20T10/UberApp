package com.cmput301w20t10.uberapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cmput301w20t10.uberapp.activities.LoginActivity;

import java.io.File;

public class LogOut {

    public static void clearRestart(Context context){
        // delete all data
        clearApplicationData(context);

        /*
         * Piece of code used to restart the app
         * URL of question: https://stackoverflow.com/questions/6609414/how-do-i-programmatically-restart-an-android-app
         * Asked by: Stuck, https://stackoverflow.com/users/386201/stuck
         * Answered by: Oleg Koshkin, https://stackoverflow.com/users/960643/oleg-koshkin
         * URL of answer: https://stackoverflow.com/a/17166729
         */
        Intent mStartActivity = new Intent(context, LoginActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1, mPendingIntent);
        System.exit(0);
    }

    /*
     * clearApplicationData and deleteDir taken from blog to delete local data
     * URL: http://www.indappz.com/2014/12/clear-app-data-programmatically-in.html
     * Blog: IndAppz - Life remains, Until you live
     * Title: Clear the app data programmatically in android - Dec 18, 2014
     * Author: Ramasamy Raviudaiyar Vignaraj
     */
    private static void clearApplicationData(Context c) {
        File cache = c.getCacheDir();
        File appDir = new File(cache.getParent());
        if(appDir.exists()){
            String[] children = appDir.list();
            for(String s : children){
                if(!s.equals("lib")){
                    deleteDir(new File(appDir, s));
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s +" DELETED ");
                }
            }
        }
    }
    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
