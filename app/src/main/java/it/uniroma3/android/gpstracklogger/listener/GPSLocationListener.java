package it.uniroma3.android.gpstracklogger.listener;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;

import it.uniroma3.android.gpstracklogger.R;
import it.uniroma3.android.gpstracklogger.service.GPSLoggingService;

/**
 * Created by Fabio on 04/05/2015.
 */
public class GPSLocationListener implements LocationListener {
    private GPSLoggingService gpsLoggingService;

    public GPSLocationListener(GPSLoggingService service) {
        this.gpsLoggingService = service;
    }

    @Override
    public void onLocationChanged(Location location) {
        try {
            if (location != null) {
                gpsLoggingService.sendMessage(R.id.provider, "GPS");
                gpsLoggingService.sendMessage(R.id.enabled, "TRUE");
                gpsLoggingService.sendMessage(R.id.available, "TRUE");
                gpsLoggingService.onLocationChanged(location);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if (status == LocationProvider.AVAILABLE)
            gpsLoggingService.sendMessage(R.id.available, "TRUE");
        else {
            gpsLoggingService.sendMessage(R.id.available, "FALSE");
            gpsLoggingService.restartGPSManager();
        }
    }

    @Override
    public void onProviderEnabled(String provider) {
        gpsLoggingService.sendMessage(R.id.enabled, "TRUE");
        gpsLoggingService.restartGPSManager();
    }

    @Override
    public void onProviderDisabled(String provider) {
        gpsLoggingService.sendMessage(R.id.enabled, "FALSE");
        gpsLoggingService.restartGPSManager();
    }

}
