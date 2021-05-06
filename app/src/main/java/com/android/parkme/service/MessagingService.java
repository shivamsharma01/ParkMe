package com.android.parkme.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.android.parkme.MainActivity;
import com.android.parkme.R;
import com.android.parkme.database.Chat;
import com.android.parkme.database.DatabaseClient;
import com.android.parkme.database.Query;
import com.android.parkme.utils.Globals;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class MessagingService extends FirebaseMessagingService {
    public static final BehaviorSubject<Object> subject = BehaviorSubject.create();
    private static final String TAG = "MessagingService";
    private SharedPreferences sharedpreferences;

    public void onMessageReceived(RemoteMessage message) {
        Map<String, String> m = message.getData();
        if (Globals.NOTIFICATION_TEST.equals(m.get(Globals.NOTIFICATION_TYPE))) {
            Log.i(TAG, "message received");
            Toast.makeText(getApplicationContext(), "message received", Toast.LENGTH_SHORT).show();
        }
        sharedpreferences = getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        if (Globals.NOTIFICATION_PUSH.equals(m.get(Globals.NOTIFICATION_TYPE))) {
            Query query = null;
            for (Map.Entry<String, String> eS : m.entrySet())
                System.out.println(eS.getKey() + " : " + eS.getValue());

            query = new Query(Integer.parseInt(m.get(Globals.QID)),
                    m.get(Globals.STATUS),
                    m.get(Globals.FROM_USER_NAME),
                    Integer.parseInt(m.get(Globals.FROM_USER_ID)),
                    sharedpreferences.getString(Globals.NAME, ""),
                    sharedpreferences.getInt(Globals.ID, 0),
                    Long.parseLong(m.get(Globals.TIME)),
                    -1f);

            saveQuery(query);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(Globals.NOTIFICATION_CHANNEL_ID, "My Notifications",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Globals.CHAT_MESSAGE, m.get(Globals.CHAT_MESSAGE));
            intent.putExtra(Globals.DATE, m.get(Globals.DATE));
            intent.putExtra(Globals.QID, m.get(Globals.QID));

            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, Globals.NOTIFICATION_CHANNEL_ID);

            notificationBuilder.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_identity_card)
                    .setTicker("Hearty365")
                    .setContentTitle(m.get(Globals.TITLE))
                    .setContentText(m.get(Globals.CHAT_MESSAGE))
                    .setContentIntent(pendingIntent);
            notificationManager.notify(/*notification id*/1, notificationBuilder.build());
        } else if (Globals.NOTIFICATION_CHAT.equals(m.get(Globals.NOTIFICATION_TYPE))) {
            Chat chat = new Chat(
                    Integer.parseInt(m.get(Globals.QID)),
                    Integer.parseInt(m.get(Globals.FROM_USER_ID)),
                    sharedpreferences.getInt(Globals.ID, 0),
                    Long.parseLong(m.get(Globals.TIME)),
                    m.get(Globals.CHAT_MESSAGE));
            chat.setStatus(1);
            saveChat(chat);
        }
    }

    public void onNewToken(String token) {
        Log.i(TAG, "new Token generated");
        Log.i(TAG, token);
        sharedpreferences = getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Globals.TOKEN, token);
        editor.commit();
    }

    private void saveChat(Chat chat) {
        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().parkMeDao().insert(chat);
        subject.onNext(chat);
    }
    private void saveQuery(Query query) {
        DatabaseClient.getInstance(getApplicationContext()).getAppDatabase().parkMeDao().insert(query);
    }

}
