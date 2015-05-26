package it.uniroma3.android.gpstracklogger.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Fabio on 26/05/2015.
 */
public class AppSettings extends Application {
    private static int minTime, minDistance, initialDelay, period;
    private static SharedPreferences preferences;
    private static boolean latitudeBar, longitudeBar;

    public static int getMinTime() {
        return minTime;
    }

    public static void setMinTime(int minTime) {
        AppSettings.minTime = minTime;
    }

    public static int getMinDistance() {
        return minDistance;
    }

    public static void setMinDistance(int minDistance) {
        AppSettings.minDistance = minDistance;
    }

    public static int getInitialDelay() {
        return initialDelay;
    }

    public static void setInitialDelay(int initialDelay) {
        AppSettings.initialDelay = initialDelay;
    }

    public static int getPeriod() {
        return period;
    }

    public static void setPeriod(int period) {
        AppSettings.period = period;
    }

    public static boolean isLatitudeBar() {
        return latitudeBar;
    }

    public static void setLatitudeBar(boolean latitudeBar) {
        AppSettings.latitudeBar = latitudeBar;
    }

    public static boolean isLongitudeBar() {
        return longitudeBar;
    }

    public static void setLongitudeBar(boolean longitudeBar) {
        AppSettings.longitudeBar = longitudeBar;
    }

    public static void loadSettings(Context context) {
        if (preferences == null) {
            preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        setMinTime(Integer.valueOf(preferences.getString("minTime", "60")));
        setMinDistance(Integer.valueOf(preferences.getString("minDistance", "10")));
        setInitialDelay(Integer.valueOf(preferences.getString("initialDelay", "10")));
        setPeriod(Integer.valueOf(preferences.getString("period", "10")));
        setLatitudeBar(preferences.getBoolean("latitudeBar", true));
        setLongitudeBar(preferences.getBoolean("longitudeBar", true));
    }

}
