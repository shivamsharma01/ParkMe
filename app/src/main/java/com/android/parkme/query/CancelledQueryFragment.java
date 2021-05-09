package com.android.parkme.query;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
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
import com.android.parkme.database.DatabaseClient;
import com.android.parkme.database.Query;
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

public class CancelledQueryFragment extends Fragment {
    private static final String TAG = "CancelledQueryFragment";
    RequestQueue queue = null;
    private TextView queryNumber, dateCreateText, dateClosedText, messageText, vehicleNumber;
    private ImageView vehicleNumberImage;
    private SharedPreferences sharedpreferences;
    private Query mQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_query_details_cancelled, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        queryNumber = getActivity().findViewById(R.id.query_number_qd);
        messageText = getActivity().findViewById(R.id.message_text_qd);
        dateCreateText = getActivity().findViewById(R.id.create_date_value_qd);
        dateClosedText = getActivity().findViewById(R.id.close_date_value_qd);
        vehicleNumberImage = getActivity().findViewById(R.id.clicked_image_qd);
        vehicleNumber = getActivity().findViewById(R.id.vehicle_number_qd);

        queryNumber.setText(String.valueOf(getArguments().getInt(Globals.QID)));
        new RetrieveQuery().execute(getArguments().getInt(Globals.QID));

    }

    private void updateUI() {
        messageText.setText(mQuery.getMsg());
        vehicleNumber.setText(mQuery.getVid());
        dateCreateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(mQuery.getCreateTime())));
        dateClosedText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(mQuery.getCloseTime())));
        Bitmap bitmap = Functions.loadResourceFromLocalStorage(getActivity(), mQuery.getQid());
        if (bitmap == null)
            Functions.getQidImage(getActivity(), mQuery.getQid(), vehicleNumberImage);
        else
            vehicleNumberImage.setImageBitmap(bitmap);
    }

    private class RetrieveQuery extends AsyncTask<Integer, Void, Query> {

        @Override
        protected Query doInBackground(Integer... params) {
            return DatabaseClient.getInstance(getContext()).getAppDatabase().parkMeDao().getQuery(params[0]);
        }

        @Override
        protected void onPostExecute(Query query) {
            super.onPostExecute(query);
            mQuery = query;
            updateUI();
        }
    }

}
