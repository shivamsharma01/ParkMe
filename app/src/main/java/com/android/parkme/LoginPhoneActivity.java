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

import org.json.JSONException;
import org.json.JSONObject;

public class LoginPhoneActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final String MyPREFERENCES = "ParkMe";
    private static final String sessionKey = "sessionKey";
    private static final String id = "id";
    private static final String sid = "sid";
    private static final String email = "email";
    private static final String name = "fullname";
    private static final String number = "number";
    final String doLogin = "login-phone";
    Button login;
    RequestQueue queue = null;
    private SharedPreferences sharedpreferences;
    private TextView phoneInput, passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone);
        queue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        login = findViewById(R.id.login_button);
        phoneInput = findViewById(R.id.login_phone_value);
        passwordInput = findViewById(R.id.login_password_value);

        login.setOnClickListener(v -> loginRequest());
    }

    private void loginRequest() {
        Log.i(TAG, "Authenticating login at " + getResources().getString(R.string.url).concat(doLogin));
        JSONObject loginObject = new JSONObject();
        try {
            loginObject.put("number", phoneInput.getText().toString());
            loginObject.put("password", passwordInput.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(
                getResources().getString(R.string.url).concat(doLogin),
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
            editor.putString(name, response.getString(name));
            editor.putString(email, response.getString(email));
            editor.putString(number, response.getString(number));
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