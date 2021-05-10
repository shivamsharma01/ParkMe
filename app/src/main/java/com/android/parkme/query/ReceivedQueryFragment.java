package com.android.parkme.query;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import com.android.parkme.chat.ChatFragment;
import com.android.parkme.database.DatabaseClient;
import com.android.parkme.database.Query;
import com.android.parkme.utils.Functions;
import com.android.parkme.utils.Globals;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReceivedQueryFragment extends Fragment {
    private static final String TAG = "ReceivedQueryFragment";
    private final DateFormat simple = new SimpleDateFormat("MMM dd");
    private RecyclerView mcQueryRecyclerView;
    private QueryAdapter mAdapter;
    private SharedPreferences sharedpreferences;
    private List<Query> mQueries;
    private int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query_recycler, container, false);

        mcQueryRecyclerView = view.findViewById(R.id.query_recycler_view);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);

        mcQueryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mQueries = new ArrayList<>();
        mAdapter = new QueryAdapter(mQueries);
        mcQueryRecyclerView.setAdapter(mAdapter);
        id = sharedpreferences.getInt(Globals.ID, 0);
        new RetrieveQueries().execute(id);
        return view;
    }

    class QueryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Query> mQueries;

        public QueryAdapter(List<Query> queries) {
            mQueries = queries;
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // for against both unresolved and cancelled look the same
            if (viewType == Globals.VIEW_TYPE_UNRESOLVED)
                return new QueryUnresolvedHolder(LayoutInflater.from(getActivity()).inflate(R.layout.list_item_query_view_cancelled, parent, false));
            else
                return new QueryClosedHolder(LayoutInflater.from(getActivity()).inflate(R.layout.list_item_query_view_closed, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position == 0) {
                holder.itemView.setPadding(0, 200, 0, 0);
            }
            if (holder instanceof QueryUnresolvedHolder)
                ((QueryUnresolvedHolder) holder).bind(mQueries.get(position));
            else
                ((QueryClosedHolder) holder).bind(mQueries.get(position));
        }

        @Override
        public int getItemViewType(int position) {
            if (mQueries.get(position).getStatus().toLowerCase().equals("closed"))
                return Globals.VIEW_TYPE_CLOSED;
            else
                return Globals.VIEW_TYPE_UNRESOLVED;
        }

        @Override
        public int getItemCount() {
            return mQueries.size();
        }
    }

    class QueryUnresolvedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Query mQuery;
        private View v;
        private ImageView userPicImageView;
        private TextView mNameTextView, mDateTextView, mStatusTextView;

        public QueryUnresolvedHolder(View itemView) {
            super(itemView);
            v = itemView;
            itemView.setOnClickListener(this);
            mNameTextView = itemView.findViewById(R.id.query_name);
            mDateTextView = itemView.findViewById(R.id.query_date);
            mStatusTextView = itemView.findViewById(R.id.query_status);
            userPicImageView = itemView.findViewById(R.id.user_pic);
        }

        public void bind(Query query) {
            mQuery = query;
            mNameTextView.setText(query.getFromName());
            mNameTextView.setPaintFlags(mNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mDateTextView.setText(Functions.parseDateText(simple.format(new Date(query.getCreateTime()))));
            mStatusTextView.setText(query.getStatus());
            if (query.getFromName().toLowerCase().contains("shivam"))
                userPicImageView.setImageResource(R.drawable.img_shivam);
            else if (query.getFromName().toLowerCase().contains("akhil"))
                userPicImageView.setImageResource(R.drawable.img_akhil);
            else if (query.getFromName().toLowerCase().contains("shradha"))
                userPicImageView.setImageResource(R.drawable.img_shradha);
            else if (query.getFromName().toLowerCase().contains("akanksha"))
                userPicImageView.setImageResource(R.drawable.img_akanksha);
            if (query.getStatus().toLowerCase().equals("cancelled"))
                mStatusTextView.setTextColor(Color.GREEN);
            else
                mStatusTextView.setTextColor(Color.RED);
        }

        @Override
        public void onClick(View v) {
            CancelledQueryFragment cancelledQueryFragment = new CancelledQueryFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Globals.QID, mQuery.getQid());
            bundle.putString(Globals.STATUS, mQuery.getStatus());
            bundle.putInt(Globals.TO_USER_ID, mQuery.getFromId());
            cancelledQueryFragment.setArguments(bundle);
            Functions.setCurrentFragment(getActivity(), cancelledQueryFragment);
        }
    }

    class QueryClosedHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Query mQuery;
        private View v;
        private ImageView userPicImageView;
        private TextView mNameTextView, mDateTextView, mStatusTextView;
        private SimpleRatingBar ratingbar;

        public QueryClosedHolder(View itemView) {
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
            mNameTextView.setText(query.getFromName());
            mNameTextView.setPaintFlags(mNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mDateTextView.setText(Functions.parseDateText(simple.format(new Date(query.getCreateTime()))));
            mStatusTextView.setText(query.getStatus());
            if (query.getFromName().toLowerCase().contains("shivam"))
                userPicImageView.setImageResource(R.drawable.img_shivam);
            else if (query.getFromName().toLowerCase().contains("akhil"))
                userPicImageView.setImageResource(R.drawable.img_akhil);
            else if (query.getFromName().toLowerCase().contains("shradha"))
                userPicImageView.setImageResource(R.drawable.img_shradha);
            else if (query.getFromName().toLowerCase().contains("akanksha"))
                userPicImageView.setImageResource(R.drawable.img_akanksha);
            mStatusTextView.setTextColor(Color.GREEN);

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


        @Override
        public void onClick(View v) {
            ResolvedQueryFragment resolvedQueryFragment = new ResolvedQueryFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Globals.QID, mQuery.getQid());
            bundle.putString(Globals.STATUS, mQuery.getStatus());
            bundle.putInt(Globals.TO_USER_ID, mQuery.getFromId());
            resolvedQueryFragment.setArguments(bundle);
            Functions.setCurrentFragment(getActivity(), resolvedQueryFragment);
        }

    }

    private class RetrieveQueries extends AsyncTask<Integer, Void, List<Query>> {

        @Override
        protected List<Query> doInBackground(Integer... params) {
            return DatabaseClient.getInstance(getContext()).getAppDatabase().parkMeDao().raisedAgainstMe(params[0]);
        }

        @Override
        protected void onPostExecute(List<Query> queries) {
            super.onPostExecute(queries);
            mQueries.addAll(queries);
            mAdapter.notifyDataSetChanged();
        }
    }
}