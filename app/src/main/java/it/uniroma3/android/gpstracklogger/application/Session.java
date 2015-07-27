/*******************************************************************************
 * This file is part of GPSTrackLogger.
 * Copyright (C) 2015  Fabio Cibecchini
 *
 * GPSTrackLogger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GPSTrackLogger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSTrackLogger.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package it.uniroma3.android.gpstracklogger.application;

import android.app.Application;
import android.location.Location;

import it.uniroma3.android.gpstracklogger.model.GPSController;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * Created by Fabio on 06/05/2015.
 */
public class Session extends Application {
    private static GPSController controller;
    private static Location currentLocation;
    private static Converter converter;
    private static boolean started, converterSet, compass, returning, controllerRegistered;
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

    public static boolean isReturning() {
        return returning;
    }

    public static void setReturning(boolean returning) {
        Session.returning = returning;
    }

    public static boolean isControllerRegistered() {
        return controllerRegistered;
    }

    public static void setControllerRegistered(boolean controllerRegistered) {
        Session.controllerRegistered = controllerRegistered;
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }

    public static void setCurrentLocation(Location currentLocation) {
        Session.currentLocation = currentLocation;
    }
}
