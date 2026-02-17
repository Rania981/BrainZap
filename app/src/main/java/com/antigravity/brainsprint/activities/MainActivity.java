package com.antigravity.brainsprint.activities;

import android.os.Bundle;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.antigravity.brainsprint.R;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout bgContainer = findViewById(R.id.backgroundContainer);
        bgContainer.addView(new com.antigravity.brainsprint.views.BackgroundView(this, null));

        com.google.android.material.bottomnavigation.BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            androidx.fragment.app.Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectedFragment = new com.antigravity.brainsprint.fragments.HomeFragment();
            } else if (id == R.id.nav_history) {
                selectedFragment = new com.antigravity.brainsprint.fragments.HistoryFragment();
            } else if (id == R.id.nav_settings) {
                selectedFragment = new com.antigravity.brainsprint.fragments.SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, selectedFragment)
                    .commit();
            }
            return true;
        });

        // Set default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, new com.antigravity.brainsprint.fragments.HomeFragment())
                .commit();
        }
    }
}
