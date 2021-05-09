package com.android.parkme.query.view;

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

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import com.android.parkme.chat.ChatFragment;
import com.android.parkme.database.DatabaseClient;
import com.android.parkme.database.Query;
import com.android.parkme.utils.Functions;
import com.android.parkme.utils.Globals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ReceivedQueryFragment extends Fragment {
    private static final String TAG = "ReceivedFragment";
    private final DateFormat simple = new SimpleDateFormat("MMM dd");
    private RecyclerView mcQueryRecyclerView;
    private QueryAdapter mAdapter;
    private SharedPreferences sharedpreferences;
    private List<Query> mQueries = new ArrayList<>();
    private int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query_recycler, container, false);

        mcQueryRecyclerView = view.findViewById(R.id.query_recycler_view);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);

        mcQueryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new QueryAdapter(mQueries);
        mcQueryRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        id = sharedpreferences.getInt(Globals.ID, 0);
        new RetrieveQuery().execute(id);
    }

    class QueryAdapter extends RecyclerView.Adapter<QueryHolder> {

        private List<Query> mQueries;

        public QueryAdapter(List<Query> queries) {
            mQueries = queries;
        }


        @Override
        public QueryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_query_view_received, parent, false);
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
        private TextView mNameTextView, mDateTextView, mStatusTextView;
        private ImageView userPicImageView;

        public QueryHolder(View itemView) {
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
            mDateTextView.setText(Functions.parseDateText(simple.format(query.getCreateTime())));
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
        }

        @Override
        public void onClick(View v) {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            Bundle bundle = new Bundle();
            bundle.putInt(Globals.QID, mQuery.getQid());
            bundle.putInt(Globals.TO_USER_ID, mQuery.getFromId());
            ChatFragment chatFragment = new ChatFragment();
            chatFragment.setArguments(bundle);
            fragmentTransaction.replace(R.id.flFragment, chatFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

    private class RetrieveQuery extends AsyncTask<Integer, Void, List<Query>> {

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
