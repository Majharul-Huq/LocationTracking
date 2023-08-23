package com.example.locationtracking;

import android.content.Context;
import android.os.Vibrator;
import android.view.Gravity;
import android.widget.Toast;

public class AppToast {

    public static void showLongToast(Context context, String message) {
        Toast.makeText(context,message, Toast.LENGTH_LONG).show();
    }

    public static void showShortToast(Context context, String message) {
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show();
    }

    public static void showCenterToast(Context context, String message) {
        Toast toast = Toast.makeText(context,message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showCenterToastWithVibrate(Context context, String message) {
        showCenterToast(context,message);
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(500);
    }

    public static void showCenterLongToast(Context context, String message) {
        Toast toast = Toast.makeText(context,message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
