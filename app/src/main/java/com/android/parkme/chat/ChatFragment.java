package com.android.parkme.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import com.android.parkme.database.Chat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {
    private static final String MyPREFERENCES = "ParkMe";
    private static final String name = "fullname";
    private String user;
    private RecyclerView mcChatRecyclerView;
    private ChatAdapter mAdapter;
    private SharedPreferences sharedpreferences;
    private final DateFormat simple = new SimpleDateFormat("dd-MMM HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mcChatRecyclerView = view.findViewById(R.id.chats_recycler_view);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        mcChatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        List<Chat> chats = new ArrayList<>();
        user = sharedpreferences.getString(name, "");
        String to = "stranger";
        chats.add(new Chat("hello 1", user, to));
        chats.add(new Chat("hello 2", to, user));

        chats.add(new Chat("hello 1", user, to));
        chats.add(new Chat("hello 1", user, to));
        chats.add(new Chat("hello 2", to, user));

        mAdapter = new ChatAdapter(chats);
        mcChatRecyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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
        private TextView mNameTextView;
        private TextView mRollNo;

        public ChatHolder(View itemView) {
            super(itemView);
            v = itemView;
            mNameTextView = itemView.findViewById(R.id.student_name);
            mRollNo = itemView.findViewById(R.id.student_rollno);
        }

        public void bind(Chat chat) {
            mChat = chat;
            mNameTextView.setText(chat.getFrom()+" ("+ simple.format(System.currentTimeMillis())+")");
            if (chat.getFrom().equals(user))
                v.setBackgroundColor(Color.CYAN);
            else
                v.setBackgroundColor(Color.MAGENTA);
        }

    }
}
