package com.antigravity.brainsprint.challenges;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import com.antigravity.brainsprint.models.Circle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorReflexChallenge extends Challenge {
    private final List<Circle> circles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private float pulseScale = 1.0f;
    private long startTime;

    private int redsLeft = 0;
    private boolean touchedBlue = false;

    public ColorReflexChallenge(int width, int height, int stage) {
        super(width, height);
        this.startTime = System.currentTimeMillis();
        generateChallenge(stage);
    }

    private void generateChallenge(int stage) {
        int redCount = 2 + (stage / 5);
        int blueCount = 1 + (stage / 10);
        float radius = Math.max(50, 100 - (stage * 2));

        redsLeft = redCount;

        for (int i = 0; i < redCount; i++) {
            addCircle(radius, Color.parseColor("#FFD600"), true, stage);
        }
        for (int i = 0; i < blueCount; i++) {
            addCircle(radius, Color.parseColor("#FFFFFF"), false, stage);
        }
    }

    private void addCircle(float radius, int color, boolean isTarget, int stage) {
        int padding = 100;
        int attempts = 0;
        while (attempts < 50) {
            float x = padding + random.nextFloat() * (width - 2 * padding);
            float y = padding + random.nextFloat() * (height - 2 * padding);

            float vx = 0;
            float vy = 0;
            if (stage >= 11) {
                float speed = 2 + (stage / 10f);
                vx = (random.nextFloat() - 0.5f) * speed;
                vy = (random.nextFloat() - 0.5f) * speed;
            }

            boolean overlap = false;
            for (Circle existing : circles) {
                float dx = existing.x - x;
                float dy = existing.y - y;
                if (Math.sqrt(dx * dx + dy * dy) < (existing.radius + radius + 20)) {
                    overlap = true;
                    break;
                }
            }

            if (!overlap) {
                circles.add(new Circle(x, y, vx, vy, radius, color, isTarget));
                return;
            }
            attempts++;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        long elapsed = System.currentTimeMillis() - startTime;
        pulseScale = 1.0f + 0.1f * (float) Math.sin(elapsed / 150.0);

        for (Circle circle : circles) {
            circle.update(width, height);
            paint.setColor(circle.color);
            float r = circle.isTarget ? circle.radius * pulseScale : circle.radius;
            canvas.drawCircle(circle.x, circle.y, r, paint);
        }
    }

    @Override
    public boolean handleTouch(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN)
            return false;

        float tx = event.getX();
        float ty = event.getY();

        for (int i = circles.size() - 1; i >= 0; i--) {
            Circle c = circles.get(i);
            if (c.isTouched(tx, ty)) {
                if (c.isTarget) {
                    circles.remove(i);
                    redsLeft--;
                    return true;
                } else {
                    touchedBlue = true;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isWon() {
        return redsLeft == 0;
    }

    @Override
    public boolean isLost() {
        return touchedBlue;
    }
}
