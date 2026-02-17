package com.antigravity.brainsprint.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import com.antigravity.brainsprint.R;
import com.antigravity.brainsprint.managers.ScoreManager;
import com.antigravity.brainsprint.managers.SoundManager;
import com.antigravity.brainsprint.views.GameView;

public class GameActivity extends AppCompatActivity implements GameView.GameListener {
    private GameView gameView;
    private ScoreManager scoreManager;
    private SoundManager soundManager;
    private int stage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        scoreManager = new ScoreManager(this);
        soundManager = new SoundManager(this);

        gameView = findViewById(R.id.gameView);
        gameView.setListener(this);

        findViewById(R.id.pauseButton).setOnClickListener(v -> togglePause());
        findViewById(R.id.resumeButton).setOnClickListener(v -> togglePause());
        findViewById(R.id.exitButton).setOnClickListener(v -> finish());

        startNewChallenge();
    }

    private void togglePause() {
        boolean isPaused = gameView.togglePause();
        findViewById(R.id.pauseOverlay).setVisibility(isPaused ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void startNewChallenge() {
        gameView.startChallenge(stage, scoreManager.getCurrentScore());
    }

    @Override
    public void onGameOver(int score) {
        soundManager.playFailure();
        soundManager.vibrateFailure();
        gameView.triggerShake(50f);
        scoreManager.updateHighScore();

        // Wait a bit for shake before switching screen
        gameView.postDelayed(() -> {
            Intent intent = new Intent(this, GameOverActivity.class);
            intent.putExtra("SCORE", score);
            startActivity(intent);
            finish();
        }, 500);
    }

    @Override
    public void onChallengeSuccess() {
        soundManager.playSuccess();
        scoreManager.incrementScore();
        stage++;
        startNewChallenge();
    }

    @Override
    public void onCorrectTap(float x, float y) {
        soundManager.vibrateSuccess();
        gameView.triggerShake(10f);
        gameView.triggerPop(x, y, 100, Color.parseColor("#FFD600"));
    }
}
