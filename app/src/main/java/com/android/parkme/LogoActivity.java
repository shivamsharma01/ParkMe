package com.android.parkme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.android.parkme.util.Globals;

public class LogoActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        sharedpreferences = getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        getSupportActionBar().hide();

        final Runnable r = () -> changeActivity();
        new Handler().postDelayed(r, 3000);
    }

    private void changeActivity() {
        Intent i = null;
        String s1 = sharedpreferences.getString(Globals.SESSION_KEY, null);
        if (s1 == null)
            i = new Intent(this, LoginActivity.class);
        else
            i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}