package com.android.parkme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalDetailsFragment extends Fragment {

    final String TAG = "PersonalDetailsFragment";
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "ParkMe" ;
    private static final String sessionKey = "sessionKey";
    private static final String id = "id";
    private static final String sid = "sid";
    RequestQueue queue = null;
    final String getDetails = "getDetails?id=%1$s&sid=%2$s";
    TextView full_name, email_id, phone_number, personal_information, full_name_details, contact_number, address, exit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getActivity());
        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        full_name = view.findViewById(R.id.full_name);
        email_id = view.findViewById(R.id.email_id);
        phone_number = view.findViewById(R.id.phone_number);
        personal_information = view.findViewById(R.id.personal_information);
        full_name_details = view.findViewById(R.id.full_name_details);
        contact_number = view.findViewById(R.id.contact_number);
        address = view.findViewById(R.id.address);
        exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(v -> exit());

        String url = String.format(getActivity().getResources().getString(R.string.url).toString().concat(getDetails), sharedpreferences.getString(id, ""), sharedpreferences.getString(sid, ""));
        JsonRequest request = new JsonRequest(Request.Method.GET, url, null, response -> setFields(response), error -> this.handleError(error));
        queue.add(request);
        return view;
    }

    private void setFields(JSONObject response) {
        try {
            JSONObject data = new JSONObject(response.get("data").toString());
            full_name.setText(data.get("fullname").toString());
            full_name_details.setText(data.get("fullname").toString());
            email_id.setText(data.get("email").toString());
            phone_number.setText(data.get("number").toString());
            address.setText(data.get("address").toString());
            contact_number.setText(data.get("number").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void exit() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    private void handleError(VolleyError error) {
        try {
            String responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            int status = data.getInt("status");
            if (status == 403)
                exit();
            String trace = data.getString("trace");
            Pattern pattern = Pattern.compile("##.*##");
            Matcher matcher = pattern.matcher(trace);
            if (matcher.find())
                setError(matcher.group());
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void setError(String group) {
        String[] response = group.substring(2, group.length()-2).split(",");
        Integer errorCode = Integer.parseInt(response[0].split(":")[1]);
        switch (errorCode) {
            case 101:
                break;
        }
    }

    private class JsonRequest extends JsonObjectRequest {
        public JsonRequest(int method, String url, JSONObject jsonRequest, Response.Listener
                <JSONObject> listener, Response.ErrorListener errorListener) {
            super(method, url, jsonRequest, listener, errorListener);
        }

        @Override
        protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
            try {
                String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("data", new JSONObject(jsonString));
                jsonResponse.put("headers", new JSONObject(response.headers));
                return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String>  params = new HashMap<>();
            params.put("session-id", sharedpreferences.getString(sessionKey, ""));
            return params;
        }
    }

}

