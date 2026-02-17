package com.antigravity.brainsprint.challenges;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import java.util.Random;

public class QuickMathChallenge extends Challenge {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private long startTime;

    private int op1, op2;
    private String operator;
    private int result;
    private int wrongResult;

    private final boolean leftIsCorrect;

    private boolean answered = false;
    private boolean correct = false;

    public QuickMathChallenge(int width, int height, int stage) {
        super(width, height);
        this.startTime = System.currentTimeMillis();

        int level = 1 + (stage - 1) / 5;
        int maxOp = 10 + (level * 5);

        // Operation types: 0:Add, 1:Sub, 2:Mult, 3:Div, 4:Sqrt
        int type;
        if (level >= 4) type = random.nextInt(5);
        else if (level >= 3) type = random.nextInt(4);
        else if (level >= 2) type = random.nextInt(3);
        else type = random.nextInt(2);

        generateQuestion(type, maxOp);

        // Improved wrong answer generation
        do {
            int offset = (random.nextInt(5) + 1) * (random.nextBoolean() ? 1 : -1);
            this.wrongResult = result + offset;
        } while (wrongResult == result || wrongResult < 0);

        this.leftIsCorrect = random.nextBoolean();
        initPaint();
    }

    private void generateQuestion(int type, int maxOp) {
        switch (type) {
            case 0: // Addition
                op1 = random.nextInt(maxOp) + 1;
                op2 = random.nextInt(maxOp) + 1;
                operator = " + ";
                result = op1 + op2;
                break;
            case 1: // Subtraction
                op1 = random.nextInt(maxOp) + 10;
                op2 = random.nextInt(op1 - 1) + 1;
                operator = " - ";
                result = op1 - op2;
                break;
            case 2: // Multiplication
                op1 = random.nextInt(10) + 2;
                op2 = random.nextInt(10) + 2;
                operator = " × ";
                result = op1 * op2;
                break;
            case 3: // Division
                op2 = random.nextInt(9) + 2; // Divisor
                result = random.nextInt(10) + 2; // Quotient
                op1 = op2 * result; // Dividend
                operator = " ÷ ";
                break;
            case 4: // Square Root
                result = random.nextInt(11) + 2; // 2 to 12
                op1 = result * result;
                operator = "√";
                op2 = -1; // Not used
                break;
        }
    }

    private void initPaint() {
        paint.setTextSize(80);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setFakeBoldText(true);
    }

    @Override
    public void draw(Canvas canvas) {
        paint.setColor(Color.WHITE);
        String equation;
        if (op2 == -1) equation = operator + op1 + " = ?";
        else equation = op1 + operator + op2 + " = ?";
        
        canvas.drawText(equation, width / 2f, height / 2f - 100, paint);

        // Draw Options
        float leftX = width / 4f;
        float rightX = width * 3 / 4f;
        float centerY = height / 2f + 150;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#333333"));
        canvas.drawRoundRect(leftX - 140, centerY - 100, leftX + 140, centerY + 100, 30, 30, paint);
        canvas.drawRoundRect(rightX - 140, centerY - 100, rightX + 140, centerY + 100, 30, 30, paint);

        paint.setColor(Color.parseColor("#FFD600"));
        paint.setTextSize(100);
        canvas.drawText(String.valueOf(leftIsCorrect ? result : wrongResult), leftX, centerY + 35, paint);
        canvas.drawText(String.valueOf(leftIsCorrect ? wrongResult : result), rightX, centerY + 35, paint);
    }

    @Override
    public boolean handleTouch(MotionEvent event) {
        if (answered || event.getAction() != MotionEvent.ACTION_DOWN) return false;

        float tx = event.getX();
        float ty = event.getY();
        float leftX = width / 4f;
        float rightX = width * 3 / 4f;
        float centerY = height / 2f + 150;

        if (ty > centerY - 100 && ty < centerY + 100) {
            if (tx > leftX - 140 && tx < leftX + 140) {
                answered = true;
                correct = leftIsCorrect;
                return true;
            } else if (tx > rightX - 140 && tx < rightX + 140) {
                answered = true;
                correct = !leftIsCorrect;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isWon() { return answered && correct; }
    @Override
    public boolean isLost() { return answered && !correct; }
}
