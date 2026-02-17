package com.antigravity.brainsprint.challenges;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import com.antigravity.brainsprint.models.Circle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MemoryFlashChallenge extends Challenge {
    private final List<Circle> sequence = new ArrayList<>();
    private final List<Circle> userSequence = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();

    private long showDuration = 2000; // Default 2 seconds
    private long showStartTime;
    private boolean isShowing = true;

    private boolean won = false;
    private boolean lost = false;

    public MemoryFlashChallenge(int width, int height, int stage) {
        super(width, height);
        this.showDuration = Math.max(1000, 2500 - (stage * 50)); // Reduces with stage
        generateChallenge(stage);
        showStartTime = System.currentTimeMillis();
    }

    private void generateChallenge(int stage) {
        int count = 2 + (stage / 10);
        float radius = 80;
        int padding = 150;

        for (int i = 0; i < count; i++) {
            float x = padding + random.nextFloat() * (width - 2 * padding);
            float y = padding + random.nextFloat() * (height - 2 * padding);
            // In Memory Flash, they are all targets but in a specific order
            sequence.add(new Circle(x, y, radius, Color.parseColor("#FFD600"), true));
        }
    }

    @Override
    public void draw(Canvas canvas) {
        long elapsed = System.currentTimeMillis() - showStartTime;
        if (elapsed < showDuration) {
            isShowing = true;
            // Draw sequence with numbers
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            for (int i = 0; i < sequence.size(); i++) {
                Circle c = sequence.get(i);
                paint.setColor(c.color);
                canvas.drawCircle(c.x, c.y, c.radius, paint);
                paint.setColor(Color.BLACK);
                canvas.drawText(String.valueOf(i + 1), c.x, c.y + 15, paint);
            }

            // Draw "MEMORIZE!" text
            paint.setColor(Color.WHITE);
            paint.setTextSize(60);
            canvas.drawText("MEMORIZE!", width / 2f, 150, paint);
        } else {
            isShowing = false;
            // Draw empty circles (or just the ones already tapped correctly)
            for (int i = 0; i < sequence.size(); i++) {
                Circle c = sequence.get(i);
                if (userSequence.contains(c)) {
                    paint.setColor(c.color);
                } else {
                    paint.setColor(Color.LTGRAY);
                }
                canvas.drawCircle(c.x, c.y, c.radius, paint);
            }

            paint.setColor(Color.WHITE);
            paint.setTextSize(60);
            canvas.drawText("TAP IN ORDER!", width / 2f, 150, paint);
        }
    }

    @Override
    public boolean handleTouch(MotionEvent event) {
        if (isShowing || event.getAction() != MotionEvent.ACTION_DOWN)
            return false;

        float tx = event.getX();
        float ty = event.getY();

        for (Circle c : sequence) {
            if (c.isTouched(tx, ty)) {
                if (userSequence.contains(c))
                    return false;

                int nextIndex = userSequence.size();
                if (sequence.get(nextIndex) == c) {
                    userSequence.add(c);
                    if (userSequence.size() == sequence.size()) {
                        won = true;
                    }
                    return true;
                } else {
                    lost = true;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isWon() {
        return won;
    }

    @Override
    public boolean isLost() {
        return lost;
    }
}
