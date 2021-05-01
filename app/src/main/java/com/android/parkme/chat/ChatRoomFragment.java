package com.android.parkme.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import com.android.parkme.database.Chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatRoomFragment extends Fragment {
    private static final String MyPREFERENCES = "ParkMe";
    private static final String name = "fullname";
    private String user;
    private RecyclerView mcQueryRecyclerView;
    private QueryAdapter mAdapter;
    private SharedPreferences sharedpreferences;

    private final DateFormat simple = new SimpleDateFormat("dd-MMM HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);


        mcQueryRecyclerView = view.findViewById(R.id.chats_recycler_view);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mcQueryRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Chat> chats = new ArrayList<>();
        user = sharedpreferences.getString(name, "");
        String to = "stranger";
        chats.add(new Chat("hello0000000000000000000000000 1", user, to));
        chats.add(new Chat("hello 2", to, user));

        chats.add(new Chat("hello 1", user, to));
        chats.add(new Chat("hello 1", user, to));
        chats.add(new Chat("hello 2", to, user));

        mAdapter = new QueryAdapter(chats);
        mcQueryRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private class QueryAdapter extends RecyclerView.Adapter<QueryHolder> {

        private List<Chat> mQueries;

        public QueryAdapter(List<Chat> queries) {
            mQueries = queries;
        }


        @Override
        public QueryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_query_view, parent, false);
            return new QueryHolder(view);
        }

        @Override
        public void onBindViewHolder(QueryHolder holder, int position) {
            Chat chat = mQueries.get(position);
            holder.bind(chat);
        }

        @Override
        public int getItemCount() {
            return mQueries.size();
        }

    }

    private class QueryHolder extends RecyclerView.ViewHolder {

        private Chat mQuery;
        private View v;
        private TextView mTitleTextView, mMessage;

        public QueryHolder(View itemView) {
            super(itemView);
            v = itemView;
            mMessage = itemView.findViewById(R.id.box_message);
        }

        public void bind(Chat chat) {
            mQuery = chat;
            //mTitleTextView.setText(chat.getFrom()+" ("+ simple.format(System.currentTimeMillis())+")");
            mMessage.setText(chat.getMsg());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            RelativeLayout rl = v.findViewById(R.id.rl_holder);
            CardView cv = rl.findViewById(R.id.cardView);
            LinearLayout ll = cv.findViewById(R.id.holder);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cv.getLayoutParams();
            if (chat.getFrom().equals(user)) {
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.ALIGN_PARENT_RIGHT);
                cv.setLayoutParams(lp);
                ll.setBackgroundColor(Color.CYAN);
            } else {
                lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.ALIGN_LEFT);
                cv.setLayoutParams(lp);
                ll.setBackgroundColor(Color.MAGENTA);
            }
        }

    }

}