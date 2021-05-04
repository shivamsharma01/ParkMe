package com.android.parkme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.parkme.chat.ChatRoomFragment;
import com.android.parkme.util.Functions;
import com.android.parkme.util.Globals;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ImageView chat, raiseQueryOptions, profilePic, settings;
    private TextView textView;
    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        chat = view.findViewById(R.id.chat_option);
        raiseQueryOptions = view.findViewById(R.id.raise_query_option);
        profilePic = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        settings = view.findViewById(R.id.settings_button);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        profilePic.setOnClickListener(v -> Functions.openFragment(new PersonalDetailsFragment(), getActivity()));
        textView.setText(String.format(getActivity().getResources().getString(R.string.greet).toString(), sharedpreferences.getString(Globals.NAME, "")));
        chat.setOnClickListener(v -> Functions.openFragment(new ChatRoomFragment(), getActivity()));
        raiseQueryOptions.setOnClickListener(v -> Functions.openFragment(new RaiseQueryFragment(), getActivity()));
        settings.setOnClickListener(v -> Functions.openFragment(new SettingsFragment(), getActivity()));
        return view;
    }

}