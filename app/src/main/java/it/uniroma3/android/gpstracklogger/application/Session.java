package it.uniroma3.android.gpstracklogger.application;

import android.app.Application;

import java.util.List;

import it.uniroma3.android.gpstracklogger.model.GPSController;

/**
 * Created by Fabio on 06/05/2015.
 */
public class Session extends Application {
    private static GPSController controller;
    private static Converter converter;
    private static boolean started, converterSet, compass;
    private static boolean providerEnabled, providerAvailable, locationChanged;

    public static boolean isStarted() {
        return started;
    }

    public static void setStarted(boolean started) {
        Session.started = started;
    }

    public static boolean isConverterSet() {
        return converterSet;
    }

    public static void setConverterSet(boolean converterSet) {
        Session.converterSet = converterSet;
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

    public static boolean isLocationChanged() {
        return locationChanged;
    }

    public static void setLocationChanged(boolean locationChanged) {
        Session.locationChanged = locationChanged;
    }

    public static boolean isCompass() {
        return compass;
    }

    public static void setCompass(boolean compass) {
        Session.compass = compass;
    }

}
