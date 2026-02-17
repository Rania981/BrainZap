package com.antigravity.brainsprint.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.antigravity.brainsprint.R;
import com.antigravity.brainsprint.managers.ScoreManager;
import com.antigravity.brainsprint.managers.SoundManager;

public class GameOverActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        ScoreManager scoreManager = new ScoreManager(this);
        SoundManager soundManager = new SoundManager(this);
        int score = getIntent().getIntExtra("SCORE", 0);

        TextView finalScoreText = findViewById(R.id.finalScoreText);
        finalScoreText.setText("Score: " + score);

        TextView bestScoreText = findViewById(R.id.bestScoreText);
        TextView encouragementText = findViewById(R.id.encouragementText);
        int best = scoreManager.getHighScore();

        if (score >= best && score > 0) {
            bestScoreText.setText("NEW BEST!");
            bestScoreText.setTextColor(getResources().getColor(R.color.primary_yellow));
            bestScoreText.setAlpha(1.0f);
            encouragementText.setText("Incredible performance! You're a natural!");

            // Celebration animation
            bestScoreText.setScaleX(0f);
            bestScoreText.setScaleY(0f);
            bestScoreText.animate().scaleX(1.5f).scaleY(1.5f).setDuration(500)
                    .withEndAction(() -> bestScoreText.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start())
                    .start();

            soundManager.playLevelUp();
        } else {
            bestScoreText.setText("Best: " + best);
            if (score <= 5) {
                encouragementText.setText("OOPS! Nice try! Keep practicing!");
            } else if (score <= 15) {
                encouragementText.setText("OOPS! Great effort! Getting faster!");
            } else {
                encouragementText.setText("OOPS! You're a real brain zapper!");
            }
            // Play the "Oops" sound when it's not a new high score
            soundManager.playFailure();
        }

        findViewById(R.id.backgroundContainer).post(() -> {
            ((android.widget.FrameLayout) findViewById(R.id.backgroundContainer))
                    .addView(new com.antigravity.brainsprint.views.BackgroundView(this, null));
        });

        findViewById(R.id.retryButton).setOnClickListener(v -> {
            startActivity(new Intent(GameOverActivity.this, GameActivity.class));
            finish();
        });

        findViewById(R.id.mainMenuButton).setOnClickListener(v -> finish());
    }
}
