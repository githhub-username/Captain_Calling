package com.kartavya.captaincalling;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Random;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // Extract notification details

        Log.d("FirebaseMessaging", "Message received: " + remoteMessage.getData());
        String title = Objects.requireNonNull(remoteMessage.getNotification()).getTitle();
        String body = remoteMessage.getNotification().getBody();

        // Play notification sound
        playNotificationSound();

        // Vibrate the device
        vibrateDevice();

        // Create and show the notification

        Log.d("FirebaseMessaging", "Notification Title: " + title);
        Log.d("FirebaseMessaging", "Notification Body: " + body);

        showNotification(title, body);
    }

    private void playNotificationSound() {
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notificationSound);
        r.play();
    }

    private void vibrateDevice() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            long[] pattern = {100, 300, 300, 300};
            vibrator.vibrate(pattern, -1);
        }
    }

    private void showNotification(String title, String body) {
        int resourceImage = getResources().getIdentifier("ic_notification_icon", "drawable", getPackageName());
        if (resourceImage == 0) {
            resourceImage = R.drawable.logo_main; // Set a default icon resource
        }

        // Use a constant for the channel ID
        String channelId = "Your_channel_id";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(resourceImage)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Set the notification click action
        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("Id", "Your_extra_data");
        Log.d("FirebaseMessaging", "Before PendingIntent creation");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Log.d("FirebaseMessaging", "After PendingIntent creation");
        builder.setContentIntent(pendingIntent);

        // Get the NotificationManager
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Ensure NotificationManager is not null
        if (mNotificationManager != null) {
            // Create notification channel (for Android Oreo and above)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
            }

            // Use a constant notification ID or a unique identifier related to the content
            int notificationId = 1; // Adjust this based on your requirements

            // Show the notification
            mNotificationManager.notify(notificationId, builder.build());
        }
    }
}
