package com.android.parkme.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.android.parkme.R;
import com.android.parkme.util.Globals;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

public class MessagingService extends FirebaseMessagingService {
    private SharedPreferences sharedpreferences;
    private static final String TAG = "MessagingService";

    public void onMessageReceived(RemoteMessage message) {
        System.out.println(message.getFrom());
        Map<String, String> m = message.getData();
        if ("push".equals(m.get("type"))) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_identity_card)
                    .setTicker("Hearty365")
                    .setContentTitle(m.get("title"))
                    .setContentText(m.get("msg"));

            notificationManager.notify(/*notification id*/1, notificationBuilder.build());
        } else if("chat".equals(m.get("type"))) {
            for(Map.Entry<String,String> kv: m.entrySet()) {
                System.out.println(kv.getKey());
                System.out.println(kv.getValue());
            }
        }
    }

    public void onNewToken(String token) {
        sharedpreferences = getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Globals.TOKEN, token);
        editor.commit();
        Log.i(TAG, "new Token generated");
        Log.i(TAG, token);
    }
}
