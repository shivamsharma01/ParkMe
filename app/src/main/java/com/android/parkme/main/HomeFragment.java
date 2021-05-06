package com.android.parkme.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.parkme.R;
import com.android.parkme.common.PersonalDetailsFragment;
import com.android.parkme.common.SettingsFragment;
import com.android.parkme.query.RaiseQueryFragment;
import com.android.parkme.query.view.ViewQueriesFragment;
import com.android.parkme.utils.Functions;
import com.android.parkme.utils.Globals;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private ImageView announcement, raiseQueryOptions, profilePic, viewQueries, settings;
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

        announcement = view.findViewById(R.id.announcement);
        viewQueries = view.findViewById(R.id.view_query);
        raiseQueryOptions = view.findViewById(R.id.raise_query_option);
        profilePic = view.findViewById(R.id.imageView);
        textView = view.findViewById(R.id.textView);
        settings = view.findViewById(R.id.settings_button);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);
        textView.setText(String.format(getActivity().getResources().getString(R.string.greet).toString(), sharedpreferences.getString(Globals.NAME, "")));
        profilePic.setOnClickListener(v -> Functions.setCurrentFragment(getActivity(), new PersonalDetailsFragment()));
        viewQueries.setOnClickListener(v -> Functions.setCurrentFragment(getActivity(), new ViewQueriesFragment()));
        raiseQueryOptions.setOnClickListener(v -> Functions.setCurrentFragment(getActivity(), new RaiseQueryFragment()));
        settings.setOnClickListener(v -> Functions.setCurrentFragment(getActivity(), new SettingsFragment()));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sharedpreferences.getString(Globals.EMAIL, "").toLowerCase().contains("shivam"))
            profilePic.setImageResource(R.drawable.img_shivam);
        else if (sharedpreferences.getString(Globals.EMAIL, "").toLowerCase().contains("akhil"))
            profilePic.setImageResource(R.drawable.img_akhil);
        else if (sharedpreferences.getString(Globals.EMAIL, "").toLowerCase().contains("shradha"))
            profilePic.setImageResource(R.drawable.img_shradha);
        else if (sharedpreferences.getString(Globals.EMAIL, "").toLowerCase().contains("akanksha"))
            profilePic.setImageResource(R.drawable.img_akanksha);
    }
}