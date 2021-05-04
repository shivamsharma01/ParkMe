package com.android.parkme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.parkme.util.APIs;
import com.android.parkme.util.Functions;
import com.android.parkme.util.Globals;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ForgotPasswordActivity";
    private Button submit;
    private EditText email;
    private RequestQueue queue = null;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        submit = findViewById(R.id.submit_button);
        email = findViewById(R.id.fpassword_email_value);
        submit.setOnClickListener(v -> passwordModule());
        sharedpreferences = getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);
    }

    private void passwordModule() {
        if (Functions.networkCheck(getApplicationContext())) {
            if (email.getText().toString().equals("")) {
                email.setError("This is a mandatory field.");
                return;
            }
            JSONObject obj = new JSONObject();
            try {
                obj.put(Globals.EMAIL, email.getText().toString());
                String url = getResources().getString(R.string.url).concat(APIs.forgotPassword);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, obj, response -> login(), (VolleyError error) -> {
                    handleError(error);
                }) {
                    @Override
                    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                        try {
                            String jsonString = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                            JSONObject result = null;
                            if (jsonString != null && jsonString.length() > 0)
                                result = new JSONObject(jsonString);
                            return Response.success(result,
                                    HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        } catch (JSONException je) {
                            return Response.error(new ParseError(je));
                        }
                    }
                };
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {
        Toast.makeText(this, "Password sent to " + email.getText().toString() + ". Please login again.", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }


    private void handleError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            int status = data.getInt(Globals.STATUS);
            if (status == 409) {
                String errorString = data.getString(Globals.TRACE);
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
                email.setError(errorString.substring(indexStart + 1, indexEnd));
            } else if (status == 404) {
                String errorString = data.getString(Globals.TRACE);
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
                email.setError(errorString.substring(indexStart + 1, indexEnd));
            } else
                Toast.makeText(this, getResources().getString(R.string.gen_error), Toast.LENGTH_SHORT).show();
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

}