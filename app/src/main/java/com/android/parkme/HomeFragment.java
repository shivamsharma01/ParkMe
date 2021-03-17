package com.android.parkme;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeFragment extends Fragment {
    ImageView raiseQueryOptions, profilePic, settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        raiseQueryOptions = view.findViewById(R.id.raise_query_option);
        raiseQueryOptions.setOnClickListener(v -> openFragment(new RaiseQueryFragment()));
        profilePic = view.findViewById(R.id.imageView);
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