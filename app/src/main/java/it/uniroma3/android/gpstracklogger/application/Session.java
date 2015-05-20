package it.uniroma3.android.gpstracklogger.application;

import android.app.Application;

import it.uniroma3.android.gpstracklogger.model.GPSController;

/**
 * Created by Fabio on 06/05/2015.
 */
public class Session extends Application {
    private static GPSController controller;
    private static Converter converter;
    private static boolean isStarted, isConverterSet;

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

    public static void setConverter(double lon, double lat, double scala) {
        converter.setLongitude(lon);
        converter.setLatitude(lat);
        converter.setScala(scala);
        isConverterSet = true;
    }

}
