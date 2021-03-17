package com.android.parkme;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SettingsFragment extends Fragment {
    private SharedPreferences sharedpreferences;
    private static final String MyPREFERENCES = "ParkMe" ;
    Button logout_button;
    TextView contact_authority, about, change_password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedpreferences = getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        contact_authority = view.findViewById(R.id.textView4);
        about = view.findViewById(R.id.textView6);
        logout_button = view.findViewById(R.id.logout_button);
        change_password = view.findViewById(R.id.textView3);

        contact_authority.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    String temp = "tel:+919567485768";
                    intent.setData(Uri.parse(temp));
                    startActivity(intent);
        });
        logout_button.setOnClickListener(v ->exit());
        about.setOnClickListener(v -> openFragment(new AboutFragment()));
        change_password.setOnClickListener(v -> openFragment(new ChangePassword()));
        return view;
    }

    private void exit() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    private void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flFragment, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}