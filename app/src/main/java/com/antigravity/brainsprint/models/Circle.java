package com.antigravity.brainsprint.models;

import android.graphics.Color;

public class Circle {
    public float x;
    public float y;
    public float vx; // Velocity X
    public float vy; // Velocity Y
    public float radius;
    public int color;
    public boolean isTarget;

    public Circle(float x, float y, float radius, int color, boolean isTarget) {
        this(x, y, 0, 0, radius, color, isTarget);
    }

    public Circle(float x, float y, float vx, float vy, float radius, int color, boolean isTarget) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.radius = radius;
        this.color = color;
        this.isTarget = isTarget;
    }

    public void update(int width, int height) {
        x += vx;
        y += vy;

        // Bounce off walls
        if (x - radius < 0 || x + radius > width)
            vx = -vx;
        if (y - radius < 0 || y + radius > height)
            vy = -vy;
    }

    public boolean isTouched(float touchX, float touchY) {
        float dx = x - touchX;
        float dy = y - touchY;
        // Add a 20dp fuzzy buffer for better responsiveness
        float touchRadius = radius + 40;
        return (dx * dx + dy * dy) <= (touchRadius * touchRadius);
    }
}
