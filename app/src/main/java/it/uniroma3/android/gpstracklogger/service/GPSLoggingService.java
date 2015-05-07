package it.uniroma3.android.gpstracklogger.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;

import de.greenrobot.event.EventBus;
import it.uniroma3.android.gpstracklogger.events.Events;
import it.uniroma3.android.gpstracklogger.helpers.Session;
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
        RegisterEventBus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startGPSManager();
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        UnregisterEventBus();
        super.onDestroy();
    }

    private void RegisterEventBus() {
        EventBus.getDefault().register(this);
    }

    private void UnregisterEventBus(){
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
    }

    public void restartGPSManager() {
        saveTrack(false);
        stopGPSManager();
        startGPSManager();
    }


    public void startGPSManager() {
        if (gpsLocationListener == null) {
            gpsLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            gpsLocationListener = new GPSLocationListener(this);
        }
        gpsLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpsLocationListener);
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
            EventBus.getDefault().post(new Events.LocationUpdate(location));
        }
    }

    public void sendMessage(int id, String message) {
        EventBus.getDefault().post(new Events.Message(id, message));
    }

    public void onEvent(Events.Stop stop) {
        stopLogging();
    }

    public void onEvent(Events.Start start) {
        startLogging();
    }

    private void startLogging() {
        Session.setStarted(true);
        startGPSManager();
    }

    private void stopLogging() {
        Session.setStarted(false);
        stopGPSManager();
        saveTrack(true);
    }

    private void saveTrack(boolean stop) {
        Session.getController().writeToFile(stop);
    }
}
