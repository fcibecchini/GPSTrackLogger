package it.uniroma3.android.gpstracklogger.application;

import android.app.Application;

import it.uniroma3.android.gpstracklogger.model.GPSController;

/**
 * Created by Fabio on 06/05/2015.
 */
public class Session extends Application {
    private static GPSController controller;
    private static boolean isStarted;

    public static boolean isStarted() {
        return isStarted;
    }

    public static void setStarted(boolean started) {
        Session.isStarted = started;
    }

    public static GPSController getController() {
        if (controller == null)
            controller = new GPSController();
        return controller;
    }
}
