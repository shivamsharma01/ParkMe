package com.android.parkme.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import com.android.parkme.database.Query;
import com.android.parkme.util.Globals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RaisedFragment extends Fragment {
    private final DateFormat simple = new SimpleDateFormat("MMM dd");
    private String user;
    private RecyclerView mcQueryRecyclerView;
    private QueryAdapter mAdapter;
    private SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        mcQueryRecyclerView = view.findViewById(R.id.chats_recycler_view);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);

        mcQueryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Query> queries = new ArrayList<>();
        user = sharedpreferences.getString(Globals.NAME, "");
        String to = "stranger";
        queries.add(new Query("Resolved", to, user, System.currentTimeMillis() - 20 * 24 * 60 * 60 * 1000));
        queries.add(new Query("Unresolved", to, user, System.currentTimeMillis() - 40 * 24 * 60 * 60 * 1000));


        mAdapter = new QueryAdapter(queries);
        mcQueryRecyclerView.setAdapter(mAdapter);
        return view;
    }


    class QueryAdapter extends RecyclerView.Adapter<QueryHolder> {

        private List<Query> mQueries;

        public QueryAdapter(List<Query> queries) {
            mQueries = queries;
        }


        @Override
        public QueryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_query_view, parent, false);
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

        public QueryHolder(View itemView) {
            super(itemView);
            v = itemView;
            itemView.setOnClickListener(this);
            mNameTextView = itemView.findViewById(R.id.query_name);
            mDateTextView = itemView.findViewById(R.id.query_date);
            mStatusTextView = itemView.findViewById(R.id.query_status);
        }

        public void bind(Query query) {
            mQuery = query;
            mNameTextView.setText(query.getTo());
            mNameTextView.setPaintFlags(mNameTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mDateTextView.setText(simple.format(query.getTime()));
            mStatusTextView.setText(query.getStatus());
            if ("resolved".equals(query.getStatus().toLowerCase()))
                mStatusTextView.setTextColor(Color.GREEN);
            else
                mStatusTextView.setTextColor(Color.RED);
        }

        @Override
        public void onClick(View v) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.flFragment, new ChatFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
    }

}
