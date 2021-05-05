package com.android.parkme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.parkme.utils.APIs;
import com.android.parkme.utils.Functions;
import com.android.parkme.utils.Globals;
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
import java.util.HashMap;
import java.util.Map;

public class PersonalDetailsFragment extends Fragment {
    private final String TAG = "PersonalDetailsFragment";
    private RequestQueue queue = null;
    private TextView fullName, emailId, phoneNumber, personalInformation, fullNameDetails, contactNumber, address, exit;
    private SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getActivity());
        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);

        fullName = view.findViewById(R.id.full_name);
        emailId = view.findViewById(R.id.email_id);
        phoneNumber = view.findViewById(R.id.phone_number);
        personalInformation = view.findViewById(R.id.personal_information);
        fullNameDetails = view.findViewById(R.id.full_name_details);
        contactNumber = view.findViewById(R.id.contact_number);
        address = view.findViewById(R.id.address);
        exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(v -> {
            Functions.exit(getActivity(), sharedpreferences, null);
            getActivity().finish();
        });

        String url = String.format(getActivity().getResources().getString(R.string.url).toString().concat(APIs.getDetails), sharedpreferences.getInt(Globals.ID, 0));
        JsonRequest request = new JsonRequest(Request.Method.GET, url, null, response -> setFields(response), error -> this.handleError(error));
        queue.add(request);
        return view;
    }

    private void setFields(JSONObject response) {
        try {
            JSONObject data = new JSONObject(response.get(Globals.DATA).toString());
            fullName.setText(data.get(Globals.NAME).toString());
            fullNameDetails.setText(data.get(Globals.NAME).toString());
            emailId.setText(data.get(Globals.EMAIL).toString());
            phoneNumber.setText(data.get(Globals.NUMBER).toString());
            address.setText(data.get(Globals.ADDRESS).toString());
            contactNumber.setText(data.get(Globals.NUMBER).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleError(VolleyError error) {
        Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
        Functions.exit(getActivity(), sharedpreferences, null);
        getActivity().finish();
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
                jsonResponse.put(Globals.DATA, new JSONObject(jsonString));
                jsonResponse.put(Globals.HEADERS, new JSONObject(response.headers));
                return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
            } catch (UnsupportedEncodingException e) {
                return Response.error(new ParseError(e));
            } catch (JSONException je) {
                return Response.error(new ParseError(je));
            }
        }

        @Override
        public Map<String, String> getHeaders() {
            Map<String, String> params = new HashMap<>();
            params.put(Globals.SESSION_ID, sharedpreferences.getString(Globals.SESSION_KEY, ""));
            return params;
        }
    }

}

