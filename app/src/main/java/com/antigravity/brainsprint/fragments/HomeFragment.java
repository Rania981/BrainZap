package com.antigravity.brainsprint.fragments;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.antigravity.brainsprint.R;
import com.antigravity.brainsprint.activities.GameActivity;
import com.antigravity.brainsprint.managers.ScoreManager;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ScoreManager scoreManager = new ScoreManager(requireContext());
        TextView highScoreText = view.findViewById(R.id.highScoreText);
        highScoreText.setText("Best Run: " + scoreManager.getHighScore());

        View startButton = view.findViewById(R.id.startButton);
        View title = view.findViewById(R.id.title);

        // Entrance animation
        title.setAlpha(0f);
        title.setTranslationY(-50f);
        title.animate().alpha(1f).translationY(0f).setDuration(1000).start();

        // Pulsing Start Button using ObjectAnimator
        ObjectAnimator pulseAnimator = ObjectAnimator.ofPropertyValuesHolder(
                startButton,
                PropertyValuesHolder.ofFloat("scaleX", 0.95f, 1.05f),
                PropertyValuesHolder.ofFloat("scaleY", 0.95f, 1.05f));
        pulseAnimator.setDuration(1000);
        pulseAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        pulseAnimator.setRepeatMode(ObjectAnimator.REVERSE);
        pulseAnimator.start();

        startButton.setOnClickListener(v -> {
            try {
                new com.antigravity.brainsprint.managers.SoundManager(requireContext()).playSuccess();
            } catch (Exception ignored) {
            }
            startActivity(new Intent(getActivity(), GameActivity.class));
        });

        return view;
    }
}
