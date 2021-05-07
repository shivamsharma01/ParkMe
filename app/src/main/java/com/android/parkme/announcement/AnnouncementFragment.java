package com.android.parkme.announcement;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.android.parkme.R;
import com.android.parkme.chat.ChatFragment;
import com.android.parkme.database.Announcement;
import com.android.parkme.database.Query;
import com.android.parkme.utils.Functions;
import com.android.parkme.utils.Globals;
import com.android.volley.RequestQueue;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recycler.coverflow.CoverFlowLayoutManger;
import recycler.coverflow.RecyclerCoverFlow;

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

        RecyclerCoverFlow mList = (RecyclerCoverFlow) view.findViewById(R.id.list);
        //        mList.setFlatFlow(true); //平面滚动

        List<Announcement> list = new ArrayList<>();
        list.add(new Announcement(new Date().getTime()- 20*24*60*60*1000, "This is message 1"));
        list.add(new Announcement(new Date().getTime()- 10*24*60*60*1000, "This is message 2"));
        list.add(new Announcement(new Date().getTime()- 30*24*60*60*1000, "This is message 3"));
        list.add(new Announcement(new Date().getTime()+ 20*24*60*60*1000, "This is message 4"));
        list.add(new Announcement(new Date().getTime()+ 2*24*60*60*1000, "This is message 5"));
        list.add(new Announcement(new Date().getTime()+ 5*24*60*60*1000, "This is message 6"));
        list.add(new Announcement(new Date().getTime()- 50*24*60*60*1000, "This is message 7"));
        list.add(new Announcement(new Date().getTime()+ 50*24*60*60*1000, "This is message 8"));


        mList.setAdapter(new AnnouncementAdapter(list));
        mList.setOnItemSelectedListener(position -> ((TextView)view.findViewById(R.id.index)).setText((position+1)+"/"+mList.getLayoutManager().getItemCount()));

        //queryNumber = getActivity().findViewById(R.id.query_number_qd);

        //queryNumber.setText(String.valueOf(getArguments().getInt(Globals.QID)));

        //dateText.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(getArguments().getLong(Globals.QUERY_CREATE_DATE))));

    }
    class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementHolder> {

        private List<Announcement> mAnnouncement;

        public AnnouncementAdapter(List<Announcement> announcement) {
            mAnnouncement = announcement;
        }


        @Override
        public AnnouncementHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_item_announcement, parent, false);
            return new AnnouncementHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AnnouncementHolder holder, int position) {
            if (position == 0) {
                holder.v.setPadding(0, 200, 0, 0);
            }
            Announcement announcement = mAnnouncement.get(position);
            holder.bind(announcement);
        }

        @Override
        public int getItemCount() {
            return mAnnouncement.size();
        }
    }

    class AnnouncementHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Announcement mAnnouncement;
        private View v;
        private TextView messageTextView, mDateTextView;

        public AnnouncementHolder(View itemView) {
            super(itemView);
            v = itemView;
            itemView.setOnClickListener(this);
            messageTextView = itemView.findViewById(R.id.message);
            mDateTextView = itemView.findViewById(R.id.query_date);
        }

        public void bind(Announcement announcement) {
            mAnnouncement= announcement;
            messageTextView.setText(announcement.getMessage());
            messageTextView.setPaintFlags(messageTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            mDateTextView.setText(Functions.parseDateText(simple.format(announcement.getTime())));
            }

        @Override
        public void onClick(View v) {

        }

    }
}
