package com.antigravity.brainsprint.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.View;
import com.antigravity.brainsprint.models.Circle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BackgroundView extends View implements Choreographer.FrameCallback {
    private final List<Circle> circles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private boolean isRunning = false;

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w <= 0 || h <= 0)
            return;
        
        synchronized(circles) {
            circles.clear();
            for (int i = 0; i < 15; i++) {
                float radius = 50 + random.nextFloat() * 100;
                float x = random.nextFloat() * w;
                float y = random.nextFloat() * h;
                float vx = (random.nextFloat() - 0.5f) * 2;
                float vy = (random.nextFloat() - 0.5f) * 2;
                int color = Color.WHITE;
                Circle c = new Circle(x, y, vx, vy, radius, color, false);
                circles.add(c);
            }
        }
        
        if (!isRunning) {
            isRunning = true;
            Choreographer.getInstance().postFrameCallback(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        synchronized(circles) {
            for (Circle c : circles) {
                paint.setAlpha(40);
                canvas.drawCircle(c.x, c.y, c.radius, paint);
            }
        }
    }

    @Override
    public void doFrame(long frameTimeNanos) {
        if (!isRunning) return;
        
        synchronized(circles) {
            for (Circle c : circles) {
                c.update(getWidth(), getHeight());
            }
        }
        invalidate();
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
    }
}
