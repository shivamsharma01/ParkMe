package com.android.parkme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LoginActivity extends AppCompatActivity {
    private SharedPreferences sharedpreferences;
    private static final String TAG = "LoginActivity" ;
    private static final String MyPREFERENCES = "ParkMe" ;
    private static final String sessionKey = "sessionKey";
    private static final String id = "id";
    private static final String sid = "sid";
    private TextInputLayout email, password;
    Button login, loginUsingPhone;
    RequestQueue queue = null;
    final String doLogin = "login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);
        login = findViewById(R.id.login_button);
        loginUsingPhone = findViewById(R.id.login_sign_in_with_the_phone_number);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);

        login.setOnClickListener(v -> {
            loginRequest();
        });
    }

    private void loginRequest() {
        Log.i(TAG, "Authenticating login at "+getResources().getString(R.string.url).toString().concat(doLogin));
        JSONObject loginObject = new JSONObject();
        try {
            loginObject.put("email", email.getEditText().getText().toString());
            loginObject.put("password", password.getEditText().getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                getResources().getString(R.string.url).toString().concat(doLogin),
                loginObject,
                response -> {
                    Log.i(TAG, "Authentication Success");
                    if (null != response) {
                        storeFields(response);
                        onSuccess();
                    }
                }, this::handleError);
        queue.add(request);
    }

    private void handleError(VolleyError volleyError) {
        Log.i(TAG, "Authentication Failure");
    }
    private void storeFields(JSONObject response) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        try {
            editor.putString(sessionKey, response.getString(sessionKey));
            editor.putString(id, response.getString(id));
            editor.putString(sid, response.getString(sid));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onSuccess() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }
}