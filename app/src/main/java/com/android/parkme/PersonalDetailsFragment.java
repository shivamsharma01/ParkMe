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
    private TextView fullName, emailId, phoneNumber, personalInformation, fullNameDetails, contactNumber, address, exit;
    private SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        setFields();
        return view;
    }

    private void setFields() {
        fullName.setText(sharedpreferences.getString(Globals.NAME, ""));
        fullNameDetails.setText(sharedpreferences.getString(Globals.NAME, ""));
        emailId.setText(sharedpreferences.getString(Globals.EMAIL, ""));
        phoneNumber.setText(String.valueOf(sharedpreferences.getLong(Globals.NUMBER, 0)));
        address.setText(sharedpreferences.getString(Globals.ADDRESS, ""));
        contactNumber.setText(String.valueOf(sharedpreferences.getLong(Globals.NUMBER, 0)));
    }

}

