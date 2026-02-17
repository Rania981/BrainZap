package com.antigravity.brainsprint.managers;

import android.content.Context;
import android.content.SharedPreferences;
import com.antigravity.brainsprint.utils.Constants;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private final SharedPreferences prefs;
    private int currentScore = 0;
    private ScoreChangeListener listener;

    public interface ScoreChangeListener {
        void onScoreChanged(int newScore);

        void onHighScoreAchieved(int highScore);
    }

    public ScoreManager(Context context) {
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void setScoreChangeListener(ScoreChangeListener listener) {
        this.listener = listener;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public void incrementScore() {
        currentScore++;
        if (listener != null) {
            listener.onScoreChanged(currentScore);
        }
    }

    public void resetScore() {
        currentScore = 0;
        if (listener != null) {
            listener.onScoreChanged(currentScore);
        }
    }

    public int getHighScore() {
        return prefs.getInt(Constants.KEY_HIGH_SCORE, 0);
    }

    public synchronized boolean updateHighScore() {
        int highScore = getHighScore();
        boolean isNew = false;

        // Update lifetime stats
        int totalScore = prefs.getInt(Constants.KEY_LIFETIME_SCORE, 0);
        int gamesPlayed = prefs.getInt(Constants.KEY_GAMES_PLAYED, 0);

        prefs.edit()
                .putInt(Constants.KEY_LIFETIME_SCORE, totalScore + currentScore)
                .putInt(Constants.KEY_GAMES_PLAYED, gamesPlayed + 1)
                .apply();

        if (currentScore > highScore) {
            prefs.edit().putInt(Constants.KEY_HIGH_SCORE, currentScore).apply();
            isNew = true;
            if (listener != null) {
                listener.onHighScoreAchieved(currentScore);
            }
        }
        addScoreToHistory(currentScore);
        return isNew;
    }

    public int getLifetimeScore() {
        return prefs.getInt(Constants.KEY_LIFETIME_SCORE, 0);
    }

    public int getGamesPlayed() {
        return prefs.getInt(Constants.KEY_GAMES_PLAYED, 0);
    }

    public void resetAllStats() {
        prefs.edit()
                .remove(Constants.KEY_HIGH_SCORE)
                .remove(Constants.KEY_LIFETIME_SCORE)
                .remove(Constants.KEY_GAMES_PLAYED)
                .remove(Constants.KEY_SCORE_HISTORY)
                .apply();
    }

    private void addScoreToHistory(int score) {
        if (score <= 0)
            return;
        String history = prefs.getString(Constants.KEY_SCORE_HISTORY, "");
        List<Integer> scores = new ArrayList<>();
        if (!history.isEmpty()) {
            for (String s : history.split(",")) {
                try {
                    scores.add(Integer.parseInt(s.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        scores.add(score);
        Collections.sort(scores, Collections.reverseOrder());

        if (scores.size() > 10) {
            scores = scores.subList(0, 10);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scores.size(); i++) {
            sb.append(scores.get(i));
            if (i < scores.size() - 1)
                sb.append(",");
        }
        prefs.edit().putString(Constants.KEY_SCORE_HISTORY, sb.toString()).apply();
    }

    public List<Integer> getScoreHistory() {
        String history = prefs.getString(Constants.KEY_SCORE_HISTORY, "");
        List<Integer> scores = new ArrayList<>();
        if (!history.isEmpty()) {
            for (String s : history.split(",")) {
                try {
                    scores.add(Integer.parseInt(s.trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return scores;
    }
}
