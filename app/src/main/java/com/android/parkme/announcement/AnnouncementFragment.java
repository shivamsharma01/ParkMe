package com.android.parkme.announcement;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import com.android.parkme.chat.ChatFragment;
import com.android.parkme.database.Query;
import com.android.parkme.main.HomeFragment;
import com.android.parkme.query.view.RaisedQueryFragment;
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
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import com.yarolegovich.discretescrollview.DiscreteScrollView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnouncementFragment extends Fragment {
    private static final String TAG = "AnnouncementFragment";
    private final DateFormat simple = new SimpleDateFormat("MMM dd");
    RequestQueue queue = null;
    private View view;
    private SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_announcement, container, false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);

        DiscreteScrollView scrollView = view.findViewById(R.id.picker);
        List<Query> list = new ArrayList<>();
        list.add(new Query(1, "unresolved", "Shivam", 1, "Preeti", 2, new Date().getTime(), 1));
        list.add(new Query(2, "unresolved", "Amit", 1, "Preeti", 2, new Date().getTime(), 2));
        list.add(new Query(3, "unresolved", "Mom", 1, "Preeti", 4, new Date().getTime(), 3));
        list.add(new Query(4, "unresolved", "Preeti", 1, "Shivam", 1, new Date().getTime(), 4));
        list.add(new Query(5, "unresolved", "Preeti", 1, "Aditi", 3, new Date().getTime(), 5));
        scrollView.setAdapter(new QueryAdapter(list));

        //queryNumber = getActivity().findViewById(R.id.query_number_qd);

        //queryNumber.setText(String.valueOf(getArguments().getInt(Globals.QID)));

        //dateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(getArguments().getLong(Globals.QUERY_CREATE_DATE))));

    }
    class QueryAdapter extends RecyclerView.Adapter<QueryHolder> {

        private List<Query> mQueries;

        public QueryAdapter(List<Query> queries) {
            mQueries = queries;
        }


        @Override
        public QueryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_query_view_raised, parent, false);
            return new QueryHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QueryHolder holder, int position) {
            if (position == 0) {
                holder.v.setPadding(0, 200, 0, 0);
            }
            Query query = mQueries.get(position);
            holder.bind(query);
        }

        @Override
        public int getItemCount() {
            return mQueries.size();
        }
    }

    class QueryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Query mQuery;
        private View v;
        private ImageView userPicImageView;
        private TextView mNameTextView, mDateTextView, mStatusTextView;
        private SimpleRatingBar ratingbar;

        public QueryHolder(View itemView) {
            super(itemView);
            v = itemView;
            itemView.setOnClickListener(this);
            mNameTextView = itemView.findViewById(R.id.query_name);
            mDateTextView = itemView.findViewById(R.id.query_date);
            mStatusTextView = itemView.findViewById(R.id.query_status);
            userPicImageView = itemView.findViewById(R.id.user_pic);
            ratingbar = itemView.findViewById(R.id.ratingBar);
        }

        public void bind(Query query) {
            mQuery = query;
            mNameTextView.setText(query.getToName());
            mNameTextView.setPaintFlags(mNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mDateTextView.setText(Functions.parseDateText(simple.format(query.getTime())));
            mStatusTextView.setText(query.getStatus());
            if (query.getFromName().toLowerCase().contains("shivam"))
                userPicImageView.setImageResource(R.drawable.img_shivam);
            else if (query.getFromName().toLowerCase().contains("akhil"))
                userPicImageView.setImageResource(R.drawable.img_akhil);
            else if (query.getFromName().toLowerCase().contains("shradha"))
                userPicImageView.setImageResource(R.drawable.img_shradha);
            else if (query.getFromName().toLowerCase().contains("akanksha"))
                userPicImageView.setImageResource(R.drawable.img_akanksha);
            if ("resolved".equals(query.getStatus().toLowerCase()))
                mStatusTextView.setTextColor(Color.GREEN);
            else
                mStatusTextView.setTextColor(Color.RED);

            if (query.getRating() < 0)
                ratingbar.setVisibility(View.GONE);
            else {
                ratingbar.setIndicator(true);
                SimpleRatingBar.AnimationBuilder builder = ratingbar.getAnimationBuilder()
                        .setRatingTarget(query.getRating())
                        .setRepeatCount(0)
                        .setInterpolator(new android.view.animation.AccelerateInterpolator(0.1f));
                if (query.getRating() == 5.0) {
                    ratingbar.setBorderColor(getResources().getColor(R.color.golden_stars));
                    ratingbar.setFillColor(getResources().getColor(R.color.golden_stars));
                } else if (query.getRating() >= 3.0) {
                    ratingbar.setBorderColor(getResources().getColor(R.color.orange));
                    ratingbar.setFillColor(getResources().getColor(R.color.orange));
                } else {
                    ratingbar.setBorderColor(getResources().getColor(R.color.red));
                    ratingbar.setFillColor(getResources().getColor(R.color.red));
                }
                builder.start();
            }
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putInt(Globals.QID, mQuery.getQid());
            bundle.putInt(Globals.TO_USER_ID, mQuery.getToId());
            bundle.putString(Globals.STATUS, mQuery.getStatus());
            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.flFragment, chatFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

    }
}
