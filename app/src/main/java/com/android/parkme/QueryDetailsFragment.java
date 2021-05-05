package com.android.parkme;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.android.parkme.util.Globals;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class QueryDetailsFragment extends Fragment {

    private TextView queryNumber, dateText, messageText, vehicleNumber;
    private ImageView vehicleNumberImage;
    private Button cancelButton, finishButton;
    private String queryNumberVal;
    private String statusVal, messageVal, dateTime, vehicleNumberVal;
    byte[] vehicleNumberImageBArray;
    private static final String TAG = "CancelQuery";
    int queryNumberInt;
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "ParkMe";
    RequestQueue queue = null;
    String responseBody;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query_details, container, false);
        queryNumberInt = (getArguments().getInt("queryNumber"));
        queryNumberVal = String.valueOf(queryNumberInt);
        statusVal = getArguments().getString("status");
        messageVal = getArguments().getString("message");
        dateTime = getArguments().getString("queryCreateDate");
        vehicleNumberImageBArray = getArguments().getByteArray("vehicleNumberImage");
        vehicleNumberVal = getArguments().getString("vehicleRegistrationNumber");

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        queryNumber = getActivity().findViewById(R.id.query_number_qd);
        messageText = getActivity().findViewById(R.id.message_text_qd);
        dateText = getActivity().findViewById(R.id.date_value_qd);
        vehicleNumberImage = getActivity().findViewById(R.id.clicked_image_qd);
        vehicleNumber = getActivity().findViewById(R.id.vehicle_number_qd);
        cancelButton = getActivity().findViewById(R.id.cancel_button);
        finishButton = getActivity().findViewById(R.id.finish_button);

        queryNumber.setText(queryNumberVal);
        messageText.setText(messageVal);
        dateText.setText(dateTime);
        Bitmap vehicleNumberImageBitmap = BitmapFactory.decodeByteArray(vehicleNumberImageBArray, 0, vehicleNumberImageBArray.length);
        vehicleNumberImage.setImageBitmap(vehicleNumberImageBitmap);
        vehicleNumber.setText(vehicleNumberVal);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Cancel Query")
                        .setMessage("Do you really want to Cancel the raised query?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                cancelQuery();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeFragment homeFragment = new HomeFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flFragment, homeFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private void cancelQuery() {
        if (network_check()) {
            String url = getResources().getString(R.string.url).concat("cancel-query");
            Log.i(TAG, "Cancel Query " + url);
            JSONObject cancelQueryObject = new JSONObject();
            try {
                statusVal = "Cancelled";
                String date = dateTime.split(" ")[0];
                String timestamp = dateTime.split(" ")[1];
                String dateTimeWithT = date.concat("T").concat(timestamp);
                Log.i(TAG, dateTimeWithT.toString());
                cancelQueryObject.put("status", statusVal);
                cancelQueryObject.put("qid", queryNumberInt);
                cancelQueryObject.put("vehicleRegistrationNumber", vehicleNumberVal);
                cancelQueryObject.put("message", messageVal);
                cancelQueryObject.put("queryCreateDate", dateTimeWithT);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, cancelQueryObject, response -> {
                Log.i(TAG, "Cancelled the Query");
                if (null != response) {
                    int qid = 0;
                    try {
                        qid = Integer.parseInt(response.getString("qid"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HomeFragment homeFragment = new HomeFragment();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.flFragment, homeFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }, error -> this.handleError(error)) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("session-id", sharedpreferences.getString(Globals.SESSION_KEY, ""));
                    return params;
                }};
            queue.add(request);
        } else {
            Toast.makeText(getActivity(), "Please connect to Internet", Toast.LENGTH_SHORT).show();
        }
    }
    private boolean network_check()
    {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private void handleError(VolleyError error) {
        try {
            if (error == null || error.networkResponse == null) {
                Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                return;
            }
            responseBody = new String(error.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            int status = data.getInt("status");
            String errorString = data.getString("trace");
            if (status == 409) {
                int indexStart = errorString.indexOf('^'), indexEnd = errorString.indexOf('$');
//                emailInput.setError(errorString.substring(indexStart + 1, indexEnd));
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
//                            passwordInput.setError(split[1]);
                            break;
                        case 500:
                            Toast.makeText(getActivity(), split[1], Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(getActivity(), errorString.substring(indexStart + 1, indexEnd), Toast.LENGTH_SHORT).show();
                            break;
                    }
                } else {
                    Toast.makeText(getActivity(), "An error occurred", Toast.LENGTH_SHORT).show();
                }

            }
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
    }
}
