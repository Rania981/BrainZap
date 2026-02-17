package com.antigravity.brainsprint.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.antigravity.brainsprint.R;
import com.antigravity.brainsprint.managers.ScoreManager;
import com.antigravity.brainsprint.utils.Constants;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences prefs = requireContext().getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);

        Switch soundSwitch = view.findViewById(R.id.soundSwitch);
        Switch hapticSwitch = view.findViewById(R.id.hapticSwitch);
        View soundThemeContainer = view.findViewById(R.id.soundThemeContainer);
        Spinner soundThemeSpinner = view.findViewById(R.id.soundThemeSpinner);

        // Sound Switch Logic
        boolean soundEnabled = prefs.getBoolean("SOUND_ENABLED", true);
        soundSwitch.setChecked(soundEnabled);
        soundThemeContainer.setVisibility(soundEnabled ? View.VISIBLE : View.GONE);

        soundSwitch.setOnCheckedChangeListener((btn, isChecked) -> {
            prefs.edit().putBoolean("SOUND_ENABLED", isChecked).apply();
            soundThemeContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Haptic Switch Logic
        hapticSwitch.setChecked(prefs.getBoolean("HAPTIC_ENABLED", true));
        hapticSwitch.setOnCheckedChangeListener((btn, isChecked) -> 
            prefs.edit().putBoolean("HAPTIC_ENABLED", isChecked).apply());

        // Sound Theme Spinner Logic
        String[] themes = {"Modern", "Retro 8-Bit", "Minimal Click", "Crystal", "Techno", 
                           "Orchestral", "Sci-Fi", "Nature", "Drum Kit", "Classic Arcade"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, themes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        soundThemeSpinner.setAdapter(adapter);

        int savedThemeIndex = prefs.getInt(Constants.KEY_SOUND_THEME, 0);
        soundThemeSpinner.setSelection(savedThemeIndex);

        soundThemeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                prefs.edit().putInt(Constants.KEY_SOUND_THEME, position).apply();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Reset Button
        ScoreManager scoreManager = new ScoreManager(requireContext());
        view.findViewById(R.id.resetButton).setOnClickListener(v -> {
            scoreManager.resetAllStats();
            Toast.makeText(getContext(), "PROGRESS RESET", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
