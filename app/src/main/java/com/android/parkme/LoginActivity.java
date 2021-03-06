package com.android.parkme;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    Button login, loginUsingPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = findViewById(R.id.login_button);
        loginUsingPhone = findViewById(R.id.login_sign_in_with_the_phone_number);
        login.setOnClickListener(v -> {
            onSuccess();
        });
    }

    // on successful login application navigates to the home screen
    private void onSuccess() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        // we don't want to display login screen again on pressing the back button
        finish();
    }
}