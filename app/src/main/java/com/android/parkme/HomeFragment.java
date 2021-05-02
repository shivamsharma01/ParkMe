package com.android.parkme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.parkme.chat.ChatRoomFragment;
import com.android.parkme.util.Globals;
import com.google.firebase.messaging.FirebaseMessaging;

public class HomeFragment extends Fragment {
    ImageView chat, raiseQueryOptions, profilePic, settings;
    TextView textView;
    private SharedPreferences sharedpreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        chat = view.findViewById(R.id.chat_option);
        raiseQueryOptions = view.findViewById(R.id.raise_query_option);
        chat.setOnClickListener(v -> openFragment(new ChatRoomFragment()));
        raiseQueryOptions.setOnClickListener(v -> openFragment(new RaiseQueryFragment()));
        profilePic = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        textView.setText(String.format(getActivity().getResources().getString(R.string.greet).toString(), sharedpreferences.getString(Globals.NAME, "")));
        profilePic.setOnClickListener(v -> openFragment(new PersonalDetailsFragment()));
        settings = view.findViewById(R.id.settings_button);
        settings.setOnClickListener(v -> openFragment(new SettingsFragment()));
        return view;
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}