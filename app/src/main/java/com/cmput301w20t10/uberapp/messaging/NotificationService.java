package com.cmput301w20t10.uberapp.messaging;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.cmput301w20t10.uberapp.R;

import org.jetbrains.annotations.NotNull;

/**
 * A utility class for creating and sending notifications
 *
 * @author Joshua Mayer
 * @version 1.0.0
 */
public class NotificationService {

    /**
     * Creates and deploys a notification
     *
     * @param messageTitle  - The title of the notification
     * @param messageBody   - The message body of the notification
     * @param senderContext - The context of the activity sending the notification
     * @param targetClass   - The Activity to send the user to when the notification is tapped
     */
    public static void sendNotification(String messageTitle, String messageBody, @NotNull Context senderContext, Class<? extends Activity> targetClass) {
        // Create the intent for when the user taps on the notification
        Intent intent = new Intent(senderContext, targetClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Set the intent to wait for a specific event
        PendingIntent pendingIntent = PendingIntent.getActivity(senderContext, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // Determine what channel to send the notification through
        String channelID = senderContext.getString(R.string.default_notification_channel_id);

        // Add a notification sound
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        // Build the notification
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(senderContext, channelID)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)   // Set the notification icon (In the top bar)
                .setContentTitle(messageTitle)                      // Set the title
                .setContentText(messageBody)                        // Set the body text
                .setAutoCancel(true)                                // Determine whether the notification will clear itself when the user taps on it
                .setSound(defaultSoundUri)                          // Set the notification sound
                .setContentIntent(pendingIntent);                   // Set the intent to be called when the user taps the notification

        // Retrieve the notification manager to send the notification
        NotificationManager notificationManager = (NotificationManager) senderContext.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            throw new IllegalStateException("Unable to get system services from context");
        }

        // Android changed the requirements to send a notification as of Android Oreo
        // Therefore if the user is running Oreo or later, we need to follow the new protocol
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // As of Oreo, we must define the notification channel we send it through
            NotificationChannel channel = new NotificationChannel(channelID, "CHANNEL HUMAN READABLE TITLE",
                    NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notifBuilder.build());

    }

}
