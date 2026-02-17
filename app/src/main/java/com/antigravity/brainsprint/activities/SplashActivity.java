package com.antigravity.brainsprint.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.antigravity.brainsprint.R;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logoImage = findViewById(R.id.logoImage);
        TextView logoText = findViewById(R.id.logoText);
        TextView taglineText = findViewById(R.id.taglineText);

        logoText.setText("BrainZap");

        logoImage.setAlpha(0f);
        logoImage.setScaleX(0.2f);
        logoImage.setScaleY(0.2f);

        logoText.setAlpha(0f);
        logoText.setTranslationY(40f);

        taglineText.setAlpha(0f);

        logoImage.animate()
                .alpha(1f)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(1000)
                .setInterpolator(new android.view.animation.OvershootInterpolator())
                .start();

        logoText.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(400)
                .setDuration(800)
                .start();

        taglineText.animate()
                .alpha(0.5f)
                .setStartDelay(800)
                .setDuration(800)
                .withEndAction(() -> {
                    logoText.postDelayed(() -> {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }, 1200);
                }).start();
    }
}
