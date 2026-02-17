package com.antigravity.brainsprint.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatActivity;
import com.antigravity.brainsprint.R;
import com.antigravity.brainsprint.utils.Constants;

public class SettingsActivity extends AppCompatActivity {
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        Switch soundSwitch = findViewById(R.id.soundSwitch);
        Switch hapticSwitch = findViewById(R.id.hapticSwitch);

        soundSwitch.setChecked(prefs.getBoolean("SOUND_ENABLED", true));
        hapticSwitch.setChecked(prefs.getBoolean("HAPTIC_ENABLED", true));

        soundSwitch.setOnCheckedChangeListener((btn, isChecked) -> 
            prefs.edit().putBoolean("SOUND_ENABLED", isChecked).apply());
            
        hapticSwitch.setOnCheckedChangeListener((btn, isChecked) -> 
            prefs.edit().putBoolean("HAPTIC_ENABLED", isChecked).apply());

        findViewById(R.id.backButton).setOnClickListener(v -> finish());
    }
}
