package com.antigravity.brainsprint.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.media.AudioManager;
import android.media.ToneGenerator;
import com.antigravity.brainsprint.utils.Constants;

public class SoundManager {
    private final Vibrator vibrator;
    private final ToneGenerator toneGenerator;
    private final SharedPreferences prefs;

    public SoundManager(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        ToneGenerator temp = null;
        try {
            temp = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
        } catch (Exception e) {
            temp = null;
        }
        this.toneGenerator = temp;
        prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    private boolean isSoundEnabled() {
        return prefs.getBoolean("SOUND_ENABLED", true);
    }

    private boolean isHapticEnabled() {
        return prefs.getBoolean("HAPTIC_ENABLED", true);
    }

    private int getTheme() {
        return prefs.getInt(Constants.KEY_SOUND_THEME, 0);
    }

    public void playTouchFeedback() {
        if (isSoundEnabled() && toneGenerator != null) {
            toneGenerator.startTone(ToneGenerator.TONE_SUP_PIP, 40);
        }
        vibrate(15);
    }

    public void playTick() {
        if (isSoundEnabled() && toneGenerator != null)
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 30);
    }

    public void playSuccess() {
        if (isSoundEnabled() && toneGenerator != null) {
            int theme = getTheme();
            new Thread(() -> {
                try {
                    switch (theme) {
                        case 0: // Modern (Zap) - Signature Zap
                            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 50);
                            Thread.sleep(60);
                            toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 120);
                            break;
                        case 1: // Retro - Arpeggio Level Clear
                            int[] retro = {ToneGenerator.TONE_DTMF_4, ToneGenerator.TONE_DTMF_6, ToneGenerator.TONE_DTMF_8};
                            for (int n : retro) { toneGenerator.startTone(n, 60); Thread.sleep(80); }
                            break;
                        case 2: // Minimal - Discrete Click
                            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 40);
                            break;
                        case 3: // Crystal - High Resonant Triad
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_A, 80);
                            Thread.sleep(100);
                            toneGenerator.startTone(ToneGenerator.TONE_DTMF_C, 150);
                            break;
                        case 4: // Techno - Cyber Buzz
                            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 150);
                            break;
                        case 5: // Energy - Ascending Power-up
                            int[] energy = {ToneGenerator.TONE_DTMF_1, ToneGenerator.TONE_DTMF_5, ToneGenerator.TONE_DTMF_9, ToneGenerator.TONE_DTMF_D};
                            for (int n : energy) { toneGenerator.startTone(n, 50); Thread.sleep(60); }
                            break;
                        case 6: // Sci-Fi - Warp Warble
                            toneGenerator.startTone(ToneGenerator.TONE_SUP_RADIO_ACK, 100);
                            Thread.sleep(80);
                            toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
                            break;
                        case 7: // Nature - Organic Pulse
                            toneGenerator.startTone(ToneGenerator.TONE_SUP_DIAL, 40);
                            Thread.sleep(150);
                            toneGenerator.startTone(ToneGenerator.TONE_SUP_DIAL, 40);
                            break;
                        case 8: // Drum Kit - Double Snare
                            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 50);
                            Thread.sleep(50);
                            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP2, 50);
                            break;
                        case 9: // Glitch - Digital Texture
                            toneGenerator.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 40);
                            Thread.sleep(40);
                            toneGenerator.startTone(ToneGenerator.TONE_CDMA_INTERCEPT, 60);
                            break;
                    }
                } catch (InterruptedException ignored) {}
            }).start();
        }
        vibrateSuccess();
    }

    public void playFailure() {
        if (isSoundEnabled() && toneGenerator != null) {
            new Thread(() -> {
                try {
                    // "Oops" Falling Pitch: 4-note descending sequence
                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_6, 80);
                    Thread.sleep(90);
                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_5, 80);
                    Thread.sleep(90);
                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_4, 80);
                    Thread.sleep(90);
                    toneGenerator.startTone(ToneGenerator.TONE_DTMF_1, 300);
                } catch (InterruptedException ignored) {}
            }).start();
        }
        vibrateFailure();
    }

    public void playLevelUp() {
        if (isSoundEnabled() && toneGenerator != null) {
            new Thread(() -> {
                try {
                    int[] tones = { ToneGenerator.TONE_DTMF_2, ToneGenerator.TONE_DTMF_4, 
                                   ToneGenerator.TONE_DTMF_6, ToneGenerator.TONE_DTMF_8 };
                    for (int tone : tones) {
                        toneGenerator.startTone(tone, 100);
                        Thread.sleep(100);
                    }
                } catch (InterruptedException ignored) {}
            }).start();
        }
    }

    public void vibrateSuccess() {
        vibrate(new long[] { 0, 40, 20, 40 }, -1);
    }

    public void vibrateFailure() {
        vibrate(new long[] { 0, 100, 50, 300 }, -1);
    }

    private void vibrate(long duration) {
        if (vibrator != null && isHapticEnabled() && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(duration);
            }
        }
    }

    private void vibrate(long[] pattern, int repeat) {
        if (vibrator != null && isHapticEnabled() && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, repeat));
            } else {
                vibrator.vibrate(pattern, repeat);
            }
        }
    }

    public void cleanup() {
        if (toneGenerator != null) {
            toneGenerator.stopTone();
            toneGenerator.release();
        }
    }
}
