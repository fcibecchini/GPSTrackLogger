package it.uniroma3.android.gpstracklogger.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import it.uniroma3.android.gpstracklogger.model.GPSController;

/**
 * Created by Fabio on 06/05/2015.
 */
public class Session extends Application {
    private static GPSController controller;
    private static Converter converter;
    private static boolean isStarted, isConverterSet;
    private static boolean providerEnabled, providerAvailable;

    public static boolean isStarted() {
        return isStarted;
    }

    public static void setStarted(boolean started) {
        Session.isStarted = started;
    }

    public static boolean isConverterSet() {
        return isConverterSet;
    }

    public static GPSController getController() {
        if (controller == null)
            controller = new GPSController();
        return controller;
    }

    public static Converter getConverter() {
        if (converter == null)
            converter = new Converter();
        return converter;
    }

    public static void setConverter(double lon, double lat) {
        converter.setLongitude(lon);
        converter.setLatitude(lat);
        isConverterSet = true;
    }

    public static boolean isProviderEnabled() {
        return providerEnabled;
    }

    public static void setProviderEnabled(boolean providerEnabled) {
        Session.providerEnabled = providerEnabled;
    }

    public static boolean isProviderAvailable() {
        return providerAvailable;
    }

    public static void setProviderAvailable(boolean providerAvailable) {
        Session.providerAvailable = providerAvailable;
    }
}
