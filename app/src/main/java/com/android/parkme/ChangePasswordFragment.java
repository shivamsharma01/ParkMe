package com.android.parkme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

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
import java.util.HashMap;
import java.util.Map;

public class ChangePasswordFragment extends Fragment {
    private static final String MyPREFERENCES = "ParkMe";
    private static final String sessionKey = "sessionKey";
    private static final String email = "email";
    final String TAG = "ChangePasswordFragment";
    final String changePassword = "confirm-password";
    RequestQueue queue = null;
    Button csubmit;
    EditText emailText, old_p, new_p, new_p_c;
    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        queue = Volley.newRequestQueue(getActivity());
        csubmit = view.findViewById(R.id.cpassword_button);
        emailText = view.findViewById(R.id.cpassword_email_value);
        emailText.setText(sharedpreferences.getString(email, ""));
        emailText.setEnabled(false);
        old_p = view.findViewById(R.id.cpassword_old_value);
        new_p = view.findViewById(R.id.cpassword_new_value);
        new_p_c = view.findViewById(R.id.cpassword_new_confirm_value);
        csubmit.setOnClickListener(v -> onSubmit());
        return view;
    }

    private void onSubmit() {
        if (old_p.getText().toString().equals("") || new_p.getText().toString().equals("") || new_p_c.getText().toString().equals("")) {
            Toast.makeText(getContext(), "All Fields are mandatory", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!new_p.getText().toString().equals(new_p_c.getText().toString())) {
            Toast.makeText(getContext(), "Passwords Should Match", Toast.LENGTH_SHORT).show();
            return;
        }
        String url = getActivity().getResources().getString(R.string.url).concat(changePassword);
        JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, getJsonObject(), response -> {
            Toast.makeText(getContext(), "Password updated successfully", Toast.LENGTH_SHORT).show();
        }, error -> this.handleError(error)) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("session-id", sharedpreferences.getString(sessionKey, ""));
                return params;
            }

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
    }

    private JSONObject getJsonObject() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("email", emailText.getText().toString());
            jsonObject.put("oldPassword", old_p.getText().toString());
            jsonObject.put("newPassword", new_p.getText().toString());
            jsonObject.put("newConfirmPassword", new_p_c.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void handleError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            setError(new JSONObject(responseBody));
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setError(JSONObject data) {
        try {
            int status = data.getInt("status");
            String trace = data.get("trace").toString();
            switch (status) {
                case 403:
                    exit();
                    break;
                case 409:
                    emailText.setError(trace.substring(trace.indexOf("^") + 1, trace.indexOf("$")));
                    break;
                default:
                    int start = trace.indexOf("^"), end = trace.indexOf("$");
                    if (start != -1 && end != -1) {
                        String[] split = trace.substring(start + 1, end).split(":");
                        status = Integer.parseInt(split[0]);
                        switch (status) {
                            case 411:
                                new_p.setError(split[1]);
                                new_p_c.setError(split[1]);
                                break;
                            case 412:
                            case 4132:
                                new_p.setError(split[1]);
                                break;
                            default:
                                old_p.setError(split[1]);
                                break;
                        }
                    } else
                        Toast.makeText(getContext(), "An error occurred", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void exit() {
        Toast.makeText(getContext(), "Session has ended. Please login again.", Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}