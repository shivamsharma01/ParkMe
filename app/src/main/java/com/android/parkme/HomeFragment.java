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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {
    ImageView raiseQueryOptions, profilePic, settings;
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "ParkMe";
    private static final String sessionKey = "sessionKey";
    private static final String name = "fullname";
    TextView textView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        raiseQueryOptions = view.findViewById(R.id.raise_query_option);
        raiseQueryOptions.setOnClickListener(v -> openFragment(new RaiseQueryFragment()));
        profilePic = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        textView.setText(String.format(getActivity().getResources().getString(R.string.greet).toString(), sharedpreferences.getString(name, "")));
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