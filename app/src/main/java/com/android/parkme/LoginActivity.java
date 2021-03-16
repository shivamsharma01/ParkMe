package com.android.parkme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "ParkMe" ;
    private static final String sessionKey = "sessionKey";

    Button login, loginUsingPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login_button);
        loginUsingPhone = findViewById(R.id.login_sign_in_with_the_phone_number);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        login.setOnClickListener(v -> onSuccess());
    }
    private void onSuccess() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(sessionKey, "lalala");
        editor.commit();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }
}