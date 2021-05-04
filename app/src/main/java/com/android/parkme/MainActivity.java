package com.android.parkme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.android.parkme.chat.ChatRoomFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navBar = findViewById(R.id.bottomNavigationView);
        navBar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    setCurrentFragment(new PersonalDetailsFragment());
                    break;
                case R.id.home:
                    setCurrentFragment(new HomeFragment());
                    break;
                case R.id.chat:
                    setCurrentFragment(new ChatRoomFragment());
                    break;
            }
            return true;
        });
        setCurrentFragment(new HomeFragment());
    }

    private void setCurrentFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.flFragment, fragment).
                commit();
    }
}