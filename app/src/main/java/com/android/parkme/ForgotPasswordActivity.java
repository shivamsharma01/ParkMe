package com.android.parkme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    Button submit;
    EditText email;
    RequestQueue queue = null;
    final String forgotPassword = "forgot-password";
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "ParkMe" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        submit = findViewById(R.id.submit_button);
        email = findViewById(R.id.fpassword_email_value);
        submit.setOnClickListener(v->fpassword_module());
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        queue = Volley.newRequestQueue(this);
    }

    private void fpassword_module()
    {
        if (email.getText().toString().equals("")) {
            email.setError("This is a mandatory field.");
            return;
        }
        JSONObject obj = new JSONObject();
        try {
            obj.put("email",  email.getText().toString());
            String url = getResources().getString(R.string.url).concat(forgotPassword);
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
    }

    private void login() {
        Toast.makeText(this, "Password sent to "+email.getText().toString()+". Please login again.", Toast.LENGTH_SHORT);
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
            int status = data.getInt("status");
            if (status == 409) {
                String errorString = data.getString("trace");
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
                email.setError(errorString.substring(indexStart+1, indexEnd));
            } else if (status == 404) {
                String errorString = data.getString("trace");
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
                email.setError(errorString.substring(indexStart+1, indexEnd));
            } else
                Toast.makeText(this, "An error Occurred", Toast.LENGTH_SHORT);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

}