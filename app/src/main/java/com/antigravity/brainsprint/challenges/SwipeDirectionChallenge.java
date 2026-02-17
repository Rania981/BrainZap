package com.antigravity.brainsprint.challenges;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import java.util.Random;

public class SwipeDirectionChallenge extends Challenge {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();

    // 0: Up, 1: Right, 2: Down, 3: Left
    private final int direction;
    private final String[] directionText = { "SWIPE UP", "SWIPE RIGHT", "SWIPE DOWN", "SWIPE LEFT" };

    private float startX, startY;
    private boolean swipedCorrectly = false;
    private boolean swipedIncorrectly = false;
    private static final float SWIPE_THRESHOLD = 80;

    public SwipeDirectionChallenge(int width, int height, int stage) {
        super(width, height);
        this.direction = random.nextInt(4);
        initPaint();
    }

    private void initPaint() {
        paint.setTextSize(60);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        canvas.drawText(directionText[direction], width / 2f, height / 2f - 100, paint);

        // Draw an arrow for clarity
        drawArrow(canvas, width / 2f, height / 2f + 100, direction);
    }

    private void drawArrow(Canvas canvas, float cx, float cy, int dir) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#FFD600")); // Yellow

        Path path = new Path();
        float size = 80;

        canvas.save();
        canvas.translate(cx, cy);
        canvas.rotate(dir * 90 - 90); // Rotate based on direction (compensating for default pointing right)

        // Default arrow pointing Right
        path.moveTo(-size, 0);
        path.lineTo(size, 0);
        path.lineTo(size - 30, -30);
        path.moveTo(size, 0);
        path.lineTo(size - 30, 30);

        canvas.drawPath(path, paint);
        canvas.restore();
    }

    @Override
    public boolean handleTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                return false; // Don't award point yet
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                float dx = endX - startX;
                float dy = endY - startY;

                if (Math.abs(dx) > SWIPE_THRESHOLD || Math.abs(dy) > SWIPE_THRESHOLD) {
                    int detectedDir = -1;
                    if (Math.abs(dx) > Math.abs(dy)) {
                        detectedDir = dx > 0 ? 1 : 3; // Right or Left
                    } else {
                        detectedDir = dy > 0 ? 2 : 0; // Down or Up
                    }

                    if (detectedDir == direction) {
                        swipedCorrectly = true;
                        return true; // Correct swipe! Award point.
                    } else {
                        swipedIncorrectly = true;
                        return false;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean isWon() {
        return swipedCorrectly;
    }

    @Override
    public boolean isLost() {
        return swipedIncorrectly;
    }
}
