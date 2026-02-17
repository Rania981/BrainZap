package com.antigravity.brainsprint.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.MotionEvent;
import android.view.View;
import com.antigravity.brainsprint.challenges.Challenge;
import com.antigravity.brainsprint.challenges.ColorReflexChallenge;
import com.antigravity.brainsprint.challenges.SwipeDirectionChallenge;
import com.antigravity.brainsprint.challenges.MemoryFlashChallenge;
import com.antigravity.brainsprint.challenges.QuickMathChallenge;
import com.antigravity.brainsprint.utils.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends View implements Choreographer.FrameCallback {
    private Challenge currentChallenge;
    private long startTime;
    private long pauseTime;
    private boolean isRunning = false;
    private boolean isPaused = false;
    private GameListener listener;

    private final Paint timerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint scorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Particle> particles = new ArrayList<>();

    // Announcement fields
    private String announcementText = "";
    private long announcementStartTime = 0;
    private int announcementColor = Color.WHITE;

    private static class Particle {
        float x, y, vx, vy, life = 1.0f;
        int color;

        Particle(float x, float y, float vx, float vy, int color) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.color = color;
        }
    }

    private int score = 0;
    private int stage = 1;
    private String challengeName = "";

    // Pop effect management
    private float popX, popY, popRadius;
    private int popColor;
    private float popScale = 1.0f;
    private int popAlpha = 0;

    public interface GameListener {
        void onGameOver(int score);
        void onChallengeSuccess();
        void onCorrectTap(float x, float y);
    }

    public GameView(Context context) {
        super(context);
        init();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        timerPaint.setStyle(Paint.Style.FILL);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(64);
        scorePaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setListener(GameListener listener) {
        this.listener = listener;
    }

    public void startChallenge(int stage, int currentScore) {
        // Detect level up
        int oldLevel = 1 + (this.stage - 1) / Constants.STAGES_PER_LEVEL;
        int newLevel = 1 + (stage - 1) / Constants.STAGES_PER_LEVEL;
        if (newLevel > oldLevel && stage > 1) {
            announcementText = "LEVEL " + newLevel + "!";
            announcementStartTime = System.currentTimeMillis();
            announcementColor = Color.parseColor("#FFD600"); // Yellow
        }

        this.stage = stage;
        this.score = currentScore;
        post(() -> {
            if (getWidth() == 0 || getHeight() == 0) {
                postDelayed(() -> startChallenge(stage, currentScore), 100);
                return;
            }
            Random r = new Random();
            int type = r.nextInt(4);
            if (type == 0) {
                currentChallenge = new ColorReflexChallenge(getWidth(), getHeight(), stage);
                challengeName = "REFLEX TEST";
            } else if (type == 1) {
                currentChallenge = new SwipeDirectionChallenge(getWidth(), getHeight(), stage);
                challengeName = "SWIPE SPRINT";
            } else if (type == 2) {
                currentChallenge = new MemoryFlashChallenge(getWidth(), getHeight(), stage);
                challengeName = "MEMORY FLASH";
            } else {
                currentChallenge = new QuickMathChallenge(getWidth(), getHeight(), stage);
                challengeName = "QUICK BRAIN";
            }
            startTime = System.currentTimeMillis();
            isRunning = true;
            Choreographer.getInstance().postFrameCallback(this);
            invalidate();
        });
    }

    private float shakeIntensity = 0;
    private final Random random = new Random();

    public void triggerShake(float intensity) {
        this.shakeIntensity = intensity;
    }

    public boolean togglePause() {
        isPaused = !isPaused;
        if (isPaused) {
            pauseTime = System.currentTimeMillis();
        } else {
            startTime += (System.currentTimeMillis() - pauseTime);
            Choreographer.getInstance().postFrameCallback(this);
        }
        return isPaused;
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (!isRunning || isPaused)
            return;

        if (shakeIntensity > 0) {
            shakeIntensity *= 0.9f;
            if (shakeIntensity < 0.5f)
                shakeIntensity = 0;
        }

        long elapsed = System.currentTimeMillis() - startTime;
        long duration = Math.max(Constants.MIN_CHALLENGE_DURATION_MS,
                Constants.BASE_CHALLENGE_DURATION_MS - (stage * Constants.DURATION_REDUCTION_PER_LEVEL));

        if (elapsed > duration) {
            isRunning = false;
            if (listener != null)
                listener.onGameOver(score);
            return;
        }

        if (currentChallenge != null) {
            if (currentChallenge.isWon()) {
                isRunning = false;
                if (listener != null)
                    listener.onChallengeSuccess();
                return;
            } else if (currentChallenge.isLost()) {
                isRunning = false;
                if (listener != null)
                    listener.onGameOver(score);
                return;
            }
        }

        invalidate();
        Choreographer.getInstance().postFrameCallback(this);
    }

    private boolean showPerfectBonus = false;
    private long perfectBonusStartTime = 0;

    public void triggerPerfectBonus() {
        showPerfectBonus = true;
        perfectBonusStartTime = System.currentTimeMillis();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currentChallenge == null)
            return;

        canvas.save();
        if (shakeIntensity > 0) {
            canvas.translate((random.nextFloat() - 0.5f) * shakeIntensity,
                    (random.nextFloat() - 0.5f) * shakeIntensity);
        }

        // Draw background
        canvas.drawColor(Color.parseColor("#1A1A1A"));

        // Draw challenge
        currentChallenge.draw(canvas);

        canvas.restore();

        // Draw timer bar
        long elapsed = System.currentTimeMillis() - startTime;
        long duration = Math.max(Constants.MIN_CHALLENGE_DURATION_MS,
                Constants.BASE_CHALLENGE_DURATION_MS - (stage * Constants.DURATION_REDUCTION_PER_LEVEL));

        float progress = 1.0f - (float) elapsed / duration;

        int timerColor;
        boolean pulse = false;
        if (progress > 0.6f) {
            timerColor = Color.parseColor("#FFD600"); // Yellow
        } else if (progress > 0.2f) {
            timerColor = Color.parseColor("#FFEE58"); // Light Yellow
        } else {
            timerColor = Color.parseColor("#FF1744"); // Error Red
            pulse = (elapsed / 100) % 2 == 0;
        }

        timerPaint.setColor(timerColor);
        if (pulse) {
            timerPaint.setAlpha(128);
        } else {
            timerPaint.setAlpha(255);
        }

        canvas.drawRect(0, 0, getWidth() * Math.max(0, progress), 20, timerPaint);

        // Draw Score and Level
        scorePaint.setColor(Color.WHITE);
        scorePaint.setAlpha(255);
        scorePaint.setFakeBoldText(false);
        scorePaint.setShadowLayer(0, 0, 0, 0);
        scorePaint.setTextSize(64);
        int level = 1 + (stage - 1) / Constants.STAGES_PER_LEVEL;
        canvas.drawText("LEVEL " + level + " | SCORE: " + score, getWidth() / 2f, 100, scorePaint);

        // Draw Remaining Time
        float secondsLeft = Math.max(0, (duration - elapsed) / 1000f);
        scorePaint.setTextSize(40);
        canvas.drawText(String.format("%.1fs", secondsLeft), getWidth() - 100, 100, scorePaint);

        // Draw Level Up Announcement
        long announcementElapsed = System.currentTimeMillis() - announcementStartTime;
        if (announcementElapsed < 1500) {
            float alpha = 1.0f - (float) announcementElapsed / 1500f;
            scorePaint.setColor(announcementColor);
            scorePaint.setAlpha((int) (alpha * 255));
            scorePaint.setTextSize(100 + (float) announcementElapsed / 10f); // Grow text
            canvas.drawText(announcementText, getWidth() / 2f, getHeight() / 2f - 300, scorePaint);
            scorePaint.setAlpha(255);
            invalidate();
        }

        // Draw Perfect Bonus
        if (showPerfectBonus && System.currentTimeMillis() - perfectBonusStartTime < 1000) {
            scorePaint.setColor(Color.parseColor("#FFD600"));
            scorePaint.setFakeBoldText(true);
            scorePaint.setShadowLayer(20, 0, 0, Color.WHITE);
            scorePaint.setTextSize(80);
            canvas.drawText("PERFECT!", getWidth() / 2f, 200, scorePaint);
        } else if (System.currentTimeMillis() - perfectBonusStartTime > 1200) {
            showPerfectBonus = false;
        }

        // Draw Challenge Title Overlay (First 1 second)
        if (elapsed < 1000) {
            float alpha = 1.0f - (float) elapsed / 1000f;
            scorePaint.setColor(Color.WHITE);
            scorePaint.setAlpha((int) (alpha * 255));
            scorePaint.setTextSize(100);
            canvas.drawText(challengeName, getWidth() / 2f, getHeight() / 2f, scorePaint);
            scorePaint.setTextSize(64); // Reset
            scorePaint.setAlpha(255);
        }

        // Draw Pop Effect
        if (popAlpha > 0) {
            timerPaint.setColor(popColor);
            timerPaint.setAlpha(popAlpha);
            canvas.drawCircle(popX, popY, popRadius * popScale, timerPaint);
            popScale += 0.1f;
            popAlpha -= 25;
            if (popAlpha < 0)
                popAlpha = 0;
            invalidate(); // Continue animating pop
        }

        // Draw Particles
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.x += p.vx;
            p.y += p.vy;
            p.life -= 0.05f;
            if (p.life <= 0) {
                particles.remove(i);
            } else {
                timerPaint.setColor(p.color);
                timerPaint.setAlpha((int) (p.life * 255));
                canvas.drawCircle(p.x, p.y, 8, timerPaint);
            }
        }
        if (!particles.isEmpty())
            invalidate();
    }

    public void triggerPop(float x, float y, float radius, int color) {
        this.popX = x;
        this.popY = y;
        this.popRadius = radius;
        this.popColor = color;
        this.popScale = 1.0f;
        this.popAlpha = 200;

        // Create particles
        for (int i = 0; i < 8; i++) {
            float angle = (float) (i * Math.PI * 2 / 8);
            float speed = 5 + random.nextFloat() * 5;
            particles.add(new Particle(x, y, (float) Math.cos(angle) * speed, (float) Math.sin(angle) * speed, color));
        }

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isRunning || currentChallenge == null)
            return true;

        // Input lock check
        if (System.currentTimeMillis() - startTime < Constants.INPUT_LOCK_DURATION) {
            return true;
        }

        // Pass ALL touch events to the challenge so it can track movement/swipes
        if (currentChallenge.handleTouch(event)) {
            // Trigger feedback only on meaningful actions like Down or Up
            if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP) {
                if (listener != null)
                    listener.onCorrectTap(event.getX(), event.getY());
            }
            invalidate();
            return true;
        }
        return true;
    }
}
