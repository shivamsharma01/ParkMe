package com.android.parkme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.parkme.util.APIs;
import com.android.parkme.util.Globals;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    Button login, loginUsingPhone;
    RequestQueue queue = null;
    TextView forgotPassword;
    TextInputEditText emailInput, passwordInput;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        queue = Volley.newRequestQueue(this);
        sharedpreferences = getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        login = findViewById(R.id.login_button);
        loginUsingPhone = findViewById(R.id.login_sign_in_with_the_phone_number);
        forgotPassword = findViewById(R.id.f_password_text);
        emailInput = findViewById(R.id.login_email_value);
        passwordInput = findViewById(R.id.login_password_value);

        login.setOnClickListener(v -> loginRequest());
        forgotPassword.setOnClickListener(v -> goto_fpassword());
        loginUsingPhone.setOnClickListener(v -> goto_phonelogin());
    }

    private void goto_fpassword() {
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }

    private void goto_phonelogin() {
        Intent intent = new Intent(getApplicationContext(), LoginPhoneActivity.class);
        startActivity(intent);
    }

    private void loginRequest() {
        if (network_check()) {
            Log.i(TAG, "Authenticating login at " + getResources().getString(R.string.url).toString().concat(APIs.doLogin));
            JSONObject loginObject = new JSONObject();
            try {
                loginObject.put("email", emailInput.getText().toString());
                loginObject.put("password", passwordInput.getText().toString());
                loginObject.put("token", sharedpreferences.getString(Globals.TOKEN, null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest request = new JsonObjectRequest(
                    getResources().getString(R.string.url).toString().concat(APIs.doLogin),
                    loginObject,
                    response -> {
                        Log.i(TAG, "Authentication Success");
                        if (null != response) {
                            storeFields(response);
                            onSuccess();
                        }
                    }, this::handleError);
            queue.add(request);
        } else {
            Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean network_check() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void handleError(VolleyError error) {
        try {
            if (error == null || error.networkResponse == null) {
                Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            int status = data.getInt("status");
            String errorString = data.getString("trace");
            if (status == 409) {
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
                emailInput.setError(errorString.substring(indexStart + 1, indexEnd));
            } else {
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
                if (indexStart != -1 && indexEnd != -1) {
                    String[] split = errorString.substring(indexStart + 1, indexEnd).split(":");
                    status = Integer.parseInt(split[0]);
                    switch (status) {
                        case 410:
                        case 411:
                        case 412:
                        case 413:
                            passwordInput.setError(split[1]);
                            break;
                        case 500:
                            Toast.makeText(this, split[1], Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(this, errorString.substring(indexStart + 1, indexEnd), Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void storeFields(JSONObject response) {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        try {
            editor.putString(Globals.SESSION_KEY, response.getString(Globals.SESSION_KEY));
            editor.putString(Globals.ID, response.getString(Globals.ID));
            editor.putString(Globals.NAME, response.getString(Globals.NAME));
            editor.putString(Globals.EMAIL, response.getString(Globals.EMAIL));
            editor.putString(Globals.NUMBER, response.getString(Globals.NUMBER));
            editor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void onSuccess() {
        Log.i(TAG, "Login Successful... redirecting to the main page");
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        finish();
    }
}