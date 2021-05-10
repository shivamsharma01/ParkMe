package com.android.parkme.query;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.parkme.R;
import com.android.parkme.chat.ChatFragment;
import com.android.parkme.database.DatabaseClient;
import com.android.parkme.database.Query;
import com.android.parkme.utils.Functions;
import com.android.parkme.utils.Globals;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ResolvedQueryFragment extends Fragment {
    private static final String TAG = "ResolvedQueryFragment";
    RequestQueue queue = null;
    private TextView queryNumber, dateCreateText, dateClosedText, messageText, vehicleNumber;
    private SimpleRatingBar ratingbar;
    private ImageView vehicleNumberImage;
    private Button chatButton;
    private SharedPreferences sharedpreferences;
    private Query mQuery;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_query_details_resolved, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        queryNumber = getActivity().findViewById(R.id.query_number_qd);
        messageText = getActivity().findViewById(R.id.message_text_qd);
        dateCreateText = getActivity().findViewById(R.id.close_date_value_qd);
        dateClosedText = getActivity().findViewById(R.id.close_date_value_qd);
        vehicleNumberImage = getActivity().findViewById(R.id.clicked_image_qd);
        vehicleNumber = getActivity().findViewById(R.id.vehicle_number_qd);
        ratingbar = getActivity().findViewById(R.id.ratingBar);
        queryNumber.setText(String.valueOf(getArguments().getInt(Globals.QID)));
        chatButton = getActivity().findViewById(R.id.chat_button);

        chatButton.setOnClickListener(e -> {
            Bundle bundle = new Bundle();
            ChatFragment chatFragment = new ChatFragment();
            bundle.putInt(Globals.QID, mQuery.getQid());
            bundle.putString(Globals.STATUS, getArguments().getString(Globals.STATUS));
            bundle.putInt(Globals.TO_USER_ID, getArguments().getInt(Globals.TO_USER_ID));
            chatFragment.setArguments(bundle);
            getActivity().runOnUiThread(() -> Functions.setCurrentFragment(getActivity(), chatFragment));
        });
        new RetrieveQuery().execute(getArguments().getInt(Globals.QID));
    }

    private void updateUI() {
        messageText.setText(mQuery.getMsg());
        vehicleNumber.setText(mQuery.getVid());
        dateCreateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(mQuery.getCreateTime())));
        dateClosedText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(mQuery.getCloseTime())));
        ratingbar.setRating(mQuery.getRating());
        ratingbar.setIndicator(true);
        SimpleRatingBar.AnimationBuilder builder = ratingbar.getAnimationBuilder()
                .setRatingTarget(mQuery.getRating())
                .setRepeatCount(0)
                .setInterpolator(new android.view.animation.AccelerateInterpolator(0.1f));
        if (mQuery.getRating() == 5.0) {
            ratingbar.setBorderColor(getResources().getColor(R.color.golden_stars));
            ratingbar.setFillColor(getResources().getColor(R.color.golden_stars));
        } else if (mQuery.getRating() >= 3.0) {
            ratingbar.setBorderColor(getResources().getColor(R.color.orange));
            ratingbar.setFillColor(getResources().getColor(R.color.orange));
        } else {
            ratingbar.setBorderColor(getResources().getColor(R.color.red));
            ratingbar.setFillColor(getResources().getColor(R.color.red));
        }
        builder.start();
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
