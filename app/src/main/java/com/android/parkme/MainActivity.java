package com.android.parkme;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.parkme.chat.ChatRoomFragment;
import com.android.parkme.utils.Functions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BottomNavigationView navBar;
    private String fragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    Functions.setCurrentFragment(this, new PersonalDetailsFragment());
                    break;
                case R.id.home:
                    Functions.setCurrentFragment(this, new HomeFragment());
                    break;
                case R.id.chat:
                    Functions.setCurrentFragment(this, new ChatRoomFragment());
                    break;
            }
            return true;
        });

        Functions.setCurrentFragment(this, new HomeFragment());
    }

//    private void setCurrentFragment(Fragment fragment) {
//        Log.i(TAG, "setCurrentFragment");
//        fragmentManager.beginTransaction().
//                replace(R.id.flFragment, fragment).
//                commit();
//    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Log.i(TAG, ""+fragmentManager.getBackStackEntryCount());
        if(fragmentManager.getBackStackEntryCount() == 1) {
            fragmentManager.popBackStack();
            finish();
        } else {
            Log.i(TAG, "super.onBackPressed()");
            super.onBackPressed();
        }
    }

}