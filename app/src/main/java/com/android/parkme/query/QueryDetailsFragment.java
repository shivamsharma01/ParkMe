package com.android.parkme.query;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.parkme.R;
import com.android.parkme.main.HomeFragment;
import com.android.parkme.utils.APIs;
import com.android.parkme.utils.ErrorHandler;
import com.android.parkme.utils.ErrorResponse;
import com.android.parkme.utils.Functions;
import com.android.parkme.utils.Globals;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class QueryDetailsFragment extends Fragment {
    private static final String TAG = "QueryDetailsFragment";
    RequestQueue queue = null;
    private TextView queryNumber, dateText, messageText, vehicleNumber;
    private ImageView vehicleNumberImage;
    private Button cancelButton, finishButton;
    private SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_query_details, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        queryNumber = getActivity().findViewById(R.id.query_number_qd);
        messageText = getActivity().findViewById(R.id.message_text_qd);
        dateText = getActivity().findViewById(R.id.date_value_qd);
        vehicleNumberImage = getActivity().findViewById(R.id.clicked_image_qd);
        vehicleNumber = getActivity().findViewById(R.id.vehicle_number_qd);
        cancelButton = getActivity().findViewById(R.id.cancel_button);
        finishButton = getActivity().findViewById(R.id.finish_button);

        queryNumber.setText(String.valueOf(getArguments().getInt(Globals.QID)));
        messageText.setText(getArguments().getString(Globals.MESSAGE));
        vehicleNumber.setText(getArguments().getString(Globals.VEHICLE_REGISTRATION_NUMBER));

        dateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(getArguments().getLong(Globals.QUERY_CREATE_DATE))));

        byte[] vehicleNumberImageBArray = getArguments().getByteArray(Globals.VEHICLE_IMAGE_NUMBER);
        Bitmap vehicleNumberImageBitmap = BitmapFactory.decodeByteArray(vehicleNumberImageBArray, 0, vehicleNumberImageBArray.length);

        vehicleNumberImage.setImageBitmap(vehicleNumberImageBitmap);

        cancelButton.setOnClickListener(v -> new AlertDialog.Builder(getActivity())
                .setTitle("Cancel Query")
                .setMessage("Do you really want to Cancel the raised query?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> cancelQuery())
                .setNegativeButton(android.R.string.no, null).show());
        finishButton.setOnClickListener(v -> Functions.setCurrentFragment(getActivity(), new HomeFragment()));
    }

    private void cancelQuery() {
        if (Functions.networkCheck(getActivity())) {
            String url = getResources().getString(R.string.url).concat(APIs.cancelQuery);
            Log.i(TAG, "Cancel Query " + url);
            JSONObject cancelQueryObject = new JSONObject();
            try {
                cancelQueryObject.put("status", Globals.QUERY_CANCEL_STATUS);
                cancelQueryObject.put("qid", String.valueOf(getArguments().getInt(Globals.QID)));
                JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, cancelQueryObject, response -> Functions.setCurrentFragment(getActivity(), new HomeFragment()), this::handleError) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> params = new HashMap<>();
                        params.put("session-id", sharedpreferences.getString(Globals.SESSION_KEY, ""));
                        return params;
                    }
                };
                queue.add(request);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else
            Functions.showToast(getActivity(), "Please connect to Internet");
    }

    private void handleError(VolleyError error) {
        ErrorResponse errorResponse = ErrorHandler.parseAndGetErrorLogin(error);
        if (errorResponse.getStatusCode() == 0 || errorResponse.getStatusCode() == 5000)
            Toast.makeText(getActivity(), errorResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
        else {
            Functions.exit(getActivity(), sharedpreferences, null);
            getActivity().finish();
        }
    }
}
