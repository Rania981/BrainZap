package com.antigravity.brainsprint.fragments;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.antigravity.brainsprint.R;
import com.antigravity.brainsprint.managers.ScoreManager;
import java.util.List;

public class HistoryFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        ScoreManager scoreManager = new ScoreManager(requireContext());
        LinearLayout historyContainer = view.findViewById(R.id.historyContainer);

        TextView lifetimeScoreText = view.findViewById(R.id.lifetimeScoreText);
        TextView gamesPlayedText = view.findViewById(R.id.gamesPlayedText);

        lifetimeScoreText.setText(String.valueOf(scoreManager.getLifetimeScore()));
        gamesPlayedText.setText(String.valueOf(scoreManager.getGamesPlayed()));

        List<Integer> scores = scoreManager.getScoreHistory();

        if (scores.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("NO RUNS RECORDED YET");
            empty.setTextColor(getResources().getColor(R.color.white_muted));
            empty.setTextSize(14);
            empty.setGravity(Gravity.CENTER);
            empty.setAlpha(0.5f);
            empty.setLetterSpacing(0.2f);
            historyContainer.addView(empty);
        } else {
            for (int i = 0; i < scores.size(); i++) {
                View card = LayoutInflater.from(requireContext()).inflate(R.layout.item_history, historyContainer,
                        false);
                TextView rankText = card.findViewById(R.id.rankText);
                TextView scoreText = card.findViewById(R.id.scoreValueText);

                rankText.setText(String.format("%02d", i + 1));
                scoreText.setText(String.valueOf(scores.get(i)));

                if (i == 0) {
                    rankText.setTextColor(getResources().getColor(R.color.primary_yellow));
                    scoreText.setTextColor(getResources().getColor(R.color.primary_yellow));
                }

                historyContainer.addView(card);
            }
        }

        return view;
    }
}
