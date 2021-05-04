package com.android.parkme.chat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import com.android.parkme.database.Chat;
import com.android.parkme.database.DatabaseClient;
import com.android.parkme.service.MessagingService;
import com.android.parkme.util.APIs;
import com.android.parkme.util.Functions;
import com.android.parkme.util.Globals;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.disposables.Disposable;

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";
    private final DateFormat simple = new SimpleDateFormat("dd-MMM HH:mm");
    private int userId, toId, qid;
    private RecyclerView mcChatRecyclerView;
    private ChatAdapter mAdapter;
    private SharedPreferences sharedpreferences;
    private Button sendMessageButton;
    private EditText mMessage;
    private Disposable x;
    private RequestQueue queue = null;
    private List<Chat> chats;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_recycler, container, false);

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        mcChatRecyclerView = view.findViewById(R.id.chats_recycler_view);

        sendMessageButton = view.findViewById(R.id.button_gchat_send);
        mMessage = view.findViewById(R.id.edit_gchat_message);
        mcChatRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        userId = sharedpreferences.getInt(Globals.ID, 0);
        qid = getArguments().getInt(Globals.QID);
        toId = getArguments().getInt(Globals.TO_USER_ID);

        sendMessageButton.setOnClickListener(view1 -> {
            if (Functions.networkCheck(getContext())) {
                String message = mMessage.getText().toString().trim();
                if (!message.equals("")) {
                    Chat chat = new Chat(qid, userId, toId, new Date().getTime(), message);
                    new SaveChat().execute(chat);
                    mMessage.setText("");
                }
            } else {
                Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        new GetChats().execute();
        x = MessagingService.subject.subscribe(chat -> {
            chats.add((Chat) chat);
            mAdapter.notifyItemInserted(chats.size() - 1);
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        x.dispose();
    }

    private void pushChat(Chat chat) {
        if (Functions.networkCheck(getContext())) {
            String url = getResources().getString(R.string.url).concat(APIs.sendChat);
            Log.i(TAG, "Send Chat Query " + url);
            JSONObject requestObject = new JSONObject();
            try {
                requestObject.put(Globals.QID, qid);
                requestObject.put(Globals.TIME, chat.getTime());
                requestObject.put(Globals.FROM_USER_ID, userId);
                requestObject.put(Globals.TO_USER_ID, toId);
                requestObject.put(Globals.CHAT_MESSAGE, chat.getMsg());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonRequest request = new JsonObjectRequest(Request.Method.POST, url, requestObject, response -> {
                try {
                    boolean status = Boolean.parseBoolean(response.getString(Globals.STATUS));
                    System.out.println(status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }, error -> this.handleError(error)) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put(Globals.SESSION_ID, sharedpreferences.getString(Globals.SESSION_KEY, ""));
                    return params;
                }
            };
            queue.add(request);
        } else {
            Toast.makeText(getActivity(), "Please connect to the Internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleError(VolleyError error) {
        Toast.makeText(getActivity(), "Server Down", Toast.LENGTH_SHORT).show();
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
        private TextView mMessage;

        public ChatHolder(View itemView) {
            super(itemView);
            v = itemView;
            mMessage = itemView.findViewById(R.id.box_message);
        }

        public void bind(Chat chat) {
            mChat = chat;
            mMessage.setText(chat.getMsg());
            RelativeLayout rl = v.findViewById(R.id.rl_holder);
            CardView cv = rl.findViewById(R.id.cardView);
            LinearLayout ll = cv.findViewById(R.id.holder);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cv.getLayoutParams();
            if (chat.getFrom() == userId) {
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

    private class GetChats extends AsyncTask<Void, Void, List<Chat>> {

        @Override
        protected List<Chat> doInBackground(Void... params) {
            return DatabaseClient.getInstance(getActivity()).getAppDatabase().parkMeDao().getChatForQueryID(qid);
        }

        @Override
        protected void onPostExecute(List<Chat> chats) {
            super.onPostExecute(chats);
            ChatFragment.this.chats = chats;
            mAdapter = new ChatAdapter(chats);
            mcChatRecyclerView.setAdapter(mAdapter);
        }
    }

    private class SaveChat extends AsyncTask<Chat, Void, Chat> {

        @Override
        protected Chat doInBackground(Chat... params) {
            DatabaseClient.getInstance(getActivity()).getAppDatabase().parkMeDao().insert(params[0]);
            return params[0];
        }

        @Override
        protected void onPostExecute(Chat chat) {
            super.onPostExecute(chat);
            chats.add(chat);
            mAdapter.notifyItemInserted(chats.size() - 1);
            pushChat(chat);
        }
    }
}
