package com.android.parkme;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView navbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navbar = findViewById(R.id.bottomNavigationView);
        navbar.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.profile:
                    setCurrentFragment(new PersonalDetailsFragment());
                    break;
                case R.id.home:
                    setCurrentFragment(new HomeFragment());
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