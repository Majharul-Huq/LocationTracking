package com.example.locationtracking;

import android.content.Context;
import android.location.Location;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.util.Date;

public class LocationUtil {

    static final String KEY_REQUESTING_LOCATION_UPDATES = "requesting_location_updates";

    public static boolean requestingLocationUpdates(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(KEY_REQUESTING_LOCATION_UPDATES, false);
    }

    static void setRequestingLocationUpdates(Context context, boolean requestingLocationUpdates) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(KEY_REQUESTING_LOCATION_UPDATES, requestingLocationUpdates).apply();

    }

    public static String getLocationText(Location location) {
        return location == null ? "Unknown location" : "(" + location.getLatitude() + ", " + location.getLongitude() + ")";
    }

    static String getLocationTitle(Context context) {
        return context.getString(R.string.location_updated, DateFormat.getTimeInstance().format(new Date()));
    }

}
