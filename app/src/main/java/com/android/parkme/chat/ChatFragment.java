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
import com.android.parkme.util.Globals;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private final DateFormat simple = new SimpleDateFormat("dd-MMM HH:mm");
    private String user;
    private RecyclerView mcChatRecyclerView;
    private ChatAdapter mAdapter;
    private SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recycler, container, false);

        mcChatRecyclerView = view.findViewById(R.id.chats_recycler_view);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);

        mcChatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Chat> chats = new ArrayList<>();
        user = sharedpreferences.getString(Globals.NAME, "");
        String to = "stranger";
        chats.add(new Chat("hello0000000000000000000000000 1", user, to));
        chats.add(new Chat("hello 2", to, user));

        chats.add(new Chat("hello 1", user, to));
        chats.add(new Chat("hello 1", user, to));
        chats.add(new Chat("hello 2", to, user));

        mAdapter = new ChatAdapter(chats);
        mcChatRecyclerView.setAdapter(mAdapter);
        return view;
    }

    private class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {

        private List<Chat> mChats;

        public ChatAdapter(List<Chat> chats) {
            mChats = chats;
        }


        @Override
        public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_chat, parent, false);
            return new ChatHolder(view);
        }

        @Override
        public void onBindViewHolder(ChatHolder holder, int position) {
            Chat chat = mChats.get(position);
            holder.bind(chat);
        }

        @Override
        public int getItemCount() {
            return mChats.size();
        }

    }

    private class ChatHolder extends RecyclerView.ViewHolder {

        private Chat mChat;
        private View v;
        private TextView mTitleTextView, mMessage;

        public ChatHolder(View itemView) {
            super(itemView);
            v = itemView;
//            mTitleTextView = itemView.findViewById(R.id.box_title);
            mMessage = itemView.findViewById(R.id.box_message);
        }

        public void bind(Chat chat) {
            mChat = chat;
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
