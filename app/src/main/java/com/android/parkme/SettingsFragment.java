package com.android.parkme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.parkme.util.Functions;
import com.android.parkme.util.Globals;

public class SettingsFragment extends Fragment {
    private static final String TAG = "SettingsFragment", TEMP_NUM = "tel:+919567485768";
    private static final int REQUEST_CODE = 5;
    private Button logoutButton;
    private TextView contactAuthority, about, changePassword;
    private SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedpreferences = getActivity().getSharedPreferences(Globals.PREFERENCES, Context.MODE_PRIVATE);

        contactAuthority = view.findViewById(R.id.textView4);
        about = view.findViewById(R.id.textView6);
        logoutButton = view.findViewById(R.id.logout_button);
        changePassword = view.findViewById(R.id.textView3);

        contactAuthority.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE);
            } else {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(TEMP_NUM));
                startActivity(intent);
            }
        });
        logoutButton.setOnClickListener(v -> exit());
        about.setOnClickListener(v -> Functions.openFragment(new AboutFragment(), getActivity()));
        changePassword.setOnClickListener(v -> Functions.openFragment(new ChangePasswordFragment(), getActivity()));
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(TEMP_NUM));
                startActivity(intent);
            }
        }
    }

    private void exit() {
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }
}