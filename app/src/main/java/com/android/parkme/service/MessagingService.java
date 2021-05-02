package com.android.parkme.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.parkme.util.Globals;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    private SharedPreferences sharedpreferences;
    private static final String TAG = "MessagingService";

    public void onMessageReceived(RemoteMessage message) {
        //Log.i(TAG, message.getNotification().getBody());
        Log.i(TAG, message.getData().get("Key-1"));
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
