package com.example.myapplication;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

public class VibrateUtil {

    public static Vibrator vibrator;

    // 开始振动milliseconds毫秒
    public static void startVibration(Context context, long milliseconds) {
        try {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                Log.d("Vibration", "Starting vibration");
                VibrationEffect effect = VibrationEffect.createOneShot(milliseconds, VibrationEffect.DEFAULT_AMPLITUDE);
                vibrator.vibrate(effect);
            }
        } catch (Exception e) {
            Log.e("VibrateUtil", "Error starting vibration", e);
        }
    }

    // 停止振动
    public static void stopVibration() {
        try {
            if (vibrator != null) {
                Log.d("Vibration", "Stopping vibration");
                vibrator.cancel();
            }
        } catch (Exception e) {
            Log.e("VibrateUtil", "Error stopping vibration", e);
        }
    }

    // 自定义振动
    public static void startVibration(Context context, long[] pattern, int repeat) {
        try {
            if (vibrator == null) {
                vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }
            if (vibrator != null && vibrator.hasVibrator()) {
                VibrationEffect effect = VibrationEffect.createWaveform(pattern, repeat);
                vibrator.vibrate(effect);
            }
            Log.d("Vibration", "Starting custom vibration");
        } catch (Exception e) {
            Log.e("VibrateUtil", "Error starting custom vibration", e);
        }
    }
}