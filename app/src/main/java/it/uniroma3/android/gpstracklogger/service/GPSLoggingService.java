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

package it.uniroma3.android.gpstracklogger.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

import de.greenrobot.event.EventBus;
import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.events.Events;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.listener.GPSLocationListener;

/**
 * Created by Fabio on 03/05/2015.
 */
public class GPSLoggingService extends Service {
    protected LocationManager gpsLocationManager;
    private GPSLocationListener gpsLocationListener;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerEventBus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Session.getController();
        startGPSManager();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        unregisterEventBus();
        super.onDestroy();
    }

    private void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unregisterEventBus(){
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
    }

    public void restartGPSManager() {
        stopGPSManager();
        startGPSManager();
    }


    public void startGPSManager() {
        if (gpsLocationListener == null) {
            gpsLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            gpsLocationListener = new GPSLocationListener(this);
        }
        if (Session.isStarted())
            gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, AppSettings.getMinTime()*1000, AppSettings.getMinDistance(), gpsLocationListener);
    }

    public void stopGPSManager() {
        if (gpsLocationListener != null) {
            gpsLocationManager.removeUpdates(gpsLocationListener);
            gpsLocationListener = null;
        }
    }

    public void onLocationChanged(Location location) {
        if (Session.isStarted()) {
            Session.getController().addTrackPoint(location);
            Session.setLocationChanged(true);
        }
    }

    public void gpsEnabled(boolean enabled) {
        Session.setProviderEnabled(enabled);
    }

    public void gpsAvailable(boolean available) {
        Session.setProviderAvailable(available);
    }

    public void onEvent(Events.Stop stop) {
        stopLogging();
    }

    public void onEvent(Events.Start start) {
        startLogging();
    }

    public void onEvent(Events.RestartGPS restart) {
        restartGPSManager();
    }

    private void startLogging() {
        Session.setStarted(true);
        Session.getController().setCurrentTrack();
        startGPSManager();
    }

    private void stopLogging() {
        Session.setStarted(false);
        Session.setLocationChanged(false);
        stopGPSManager();
        saveTrack(true);
    }

    private void saveTrack(boolean stop) {
        Session.getController().writeToFile(stop);
    }
}
