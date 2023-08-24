package com.example.locationtracking.util;

import android.content.Context;
import android.content.SharedPreferences;

public class UserDataManager {

    private static final String KEY_ACCESS_TOKEN = "KEY_ACCESS_TOKEN";
    private static final String KEY_EMPLOYEE_ID = "KEY_EMPLOYEE_ID";
    private static final String KEY_PASSWORD = "KEY_PASSWORD";
    private static final String KEY_NAME = "KEY_NAME";
    private static final String KEY_DESIGNATION = "KEY_DESIGNATION";
    private static final String KEY_PHOTO_URL = "KEY_PHOTO_URL";

    private static final String KEY_LOGIN_REMEMBER = "KEY_LOGIN_REMEMBER";

    private static String getPrefName(String packageName) {
        return "tracker-data";
    }

    private static SharedPreferences getPrefs(Context context) {
        String prefName = getPrefName(context.getPackageName());
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
    }

    public static String getKeyAccessToken(Context context) {
        return getPrefs(context).getString(KEY_ACCESS_TOKEN, null);
    }

    public static void setKeyAccessToken(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_ACCESS_TOKEN, input);
        editor.commit();
    }

    public static String getKeyEmployeeId(Context context) {
        return getPrefs(context).getString(KEY_EMPLOYEE_ID, null);
    }

    public static void setKeyEmployeeId(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_EMPLOYEE_ID, input);
        editor.commit();
    }

    public static String getKeyPassword(Context context) {
        return getPrefs(context).getString(KEY_PASSWORD, null);
    }

    public static void setKeyPassword(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_PASSWORD, input);
        editor.commit();
    }

    public static String getKeyName(Context context) {
        return getPrefs(context).getString(KEY_NAME, null);
    }

    public static void setKeyName(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_NAME, input);
        editor.commit();
    }

    public static String getKeyDesignation(Context context) {
        return getPrefs(context).getString(KEY_DESIGNATION, null);
    }

    public static void setKeyDesignation(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_DESIGNATION, input);
        editor.commit();
    }

    public static String getKeyPhotoUrl(Context context) {
        return getPrefs(context).getString(KEY_PHOTO_URL, null);
    }

    public static void setKeyPhotoUrl(Context context, String input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putString(KEY_PHOTO_URL, input);
        editor.commit();
    }

    public static boolean getKeyLoginRemember(Context context) {
        return getPrefs(context).getBoolean(KEY_LOGIN_REMEMBER, false);
    }

    public static void setKeyLoginRemember(Context context, boolean input) {
        SharedPreferences.Editor editor = getPrefs(context).edit();
        editor.putBoolean(KEY_LOGIN_REMEMBER, input);
        editor.commit();
    }
}
