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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import de.greenrobot.event.EventBus;
import it.uniroma3.android.gpstracklogger.GPSMainActivity;
import it.uniroma3.android.gpstracklogger.R;
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
    private NotificationManager notificationManager;
    private static int NOTIFICATION_ID = 294071;
    private NotificationCompat.Builder nfc = null;

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
        if (Session.isStarted()) {
            startGPSManager();
        }
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
            Session.setCurrentLocation(location);
            Session.setLocationChanged(true);
            showNotification();
        }
    }

    private void removeNotification() {
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private void showNotification() {
        Intent contentIntent = new Intent(this, GPSMainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(contentIntent);
        PendingIntent pending = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        String contentText = getString(R.string.running);
        long notificationTime = System.currentTimeMillis();

        if (Session.isLocationChanged()) {
            NumberFormat nf = new DecimalFormat("###.#####");
            Location last = Session.getCurrentLocation();
            contentText = "Lat" + ": " + nf.format(last.getLatitude()) + ", "
                    + "Lon" + ": " + nf.format(last.getLongitude());

            notificationTime = Session.getCurrentLocation().getTime();
        }

        if (nfc == null) {
            nfc = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_gps_fixed_black_18dp)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentTitle(contentText)
                    .setOngoing(true)
                    .setContentIntent(pending);
        }

        nfc.setContentTitle(contentText);
        nfc.setContentText(getString(R.string.app_name));
        nfc.setWhen(notificationTime);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, nfc.build());
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
        try {
            startForeground(NOTIFICATION_ID, new Notification());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        showNotification();
        startGPSManager();
    }

    private void stopLogging() {
        Session.setStarted(false);
        Session.setLocationChanged(false);
        stopForeground(true);
        removeNotification();
        stopGPSManager();
        saveTrack(true);
    }

    private void saveTrack(boolean stop) {
        Session.getController().writeToFile(stop);
    }
}
