package com.android.parkme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PersonalDetailsFragment extends Fragment {
    ImageView profilePic;
    final String TAG = "PersonalDetailsFragment";
    RequestQueue queue = null;
    String url ="http://192.168.43.17:8081/parkme/getDetails?id=%1$s&sid=%2$s";
    StringRequest stringRequest;
    TextView full_name, email_id, phone_number, personal_information, full_name_details, contact_number, address;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        queue = Volley.newRequestQueue(getActivity());
        View view = inflater.inflate(R.layout.fragment_personal_details, container, false);
        full_name = view.findViewById(R.id.full_name);
        email_id = view.findViewById(R.id.email_id);
        phone_number = view.findViewById(R.id.phone_number);
        personal_information = view.findViewById(R.id.personal_information);
        full_name_details = view.findViewById(R.id.full_name_details);
        contact_number = view.findViewById(R.id.contact_number);
        address = view.findViewById(R.id.address);

        url = String.format(url, 1, 1);
        stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    setFields(response);
                }, error -> {
            Log.i(TAG, error.getLocalizedMessage());
        });
        new GetDetails().execute();
        return view;
    }

    private void setFields(String response) {
        Log.i(TAG, "CAlled set");
        full_name.setText("YOYOYOYO");
        JSONObject obj = null;
        try {
            // , personal_information, full_name_details, ,
            obj = new JSONObject(response);
            full_name.setText(obj.get("fullname").toString());
            full_name_details.setText(obj.get("fullname").toString());
            email_id.setText(obj.get("email").toString());
            phone_number.setText(obj.get("number").toString());
            address.setText(obj.get("address").toString());
            contact_number.setText(obj.get("number").toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class GetDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            queue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getContext(),"Saved",Toast.LENGTH_SHORT);
        }
    }
}

