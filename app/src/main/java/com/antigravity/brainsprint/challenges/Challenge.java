package com.antigravity.brainsprint.challenges;

import android.graphics.Canvas;
import android.view.MotionEvent;

public abstract class Challenge {
    protected int width;
    protected int height;
    protected boolean isCompleted = false;
    protected boolean isFailed = false;

    public Challenge(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract void draw(Canvas canvas);

    public abstract boolean handleTouch(MotionEvent event);

    public abstract boolean isWon();

    public abstract boolean isLost();

    public boolean isFinished() {
        return isWon() || isLost();
    }
}
