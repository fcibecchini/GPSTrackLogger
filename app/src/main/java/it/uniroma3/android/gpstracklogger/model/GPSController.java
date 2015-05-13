package it.uniroma3.android.gpstracklogger.model;

import android.location.Location;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.uniroma3.android.gpstracklogger.logger.FileLoggerFactory;

/**
 * Created by Fabio on 02/05/2015.
 */
public class GPSController {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Track currentTrack;

    public GPSController() {this.currentTrack = new Track();}

    public Track getCurrentTrack() {
        return this.currentTrack;
    }

    public boolean addTrackPoint(Location loc) {
        if (this.currentTrack == null)
            this.currentTrack = new Track();
        return this.currentTrack.addTrackPoint(loc);
    }

    public void scheduleWriting() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                writeToFile(false);
            }
        }, 20, 20, TimeUnit.SECONDS);
    }

    public void writeToFile(boolean stop) {
        FileLoggerFactory.write(this.currentTrack);
        if (stop)
            this.currentTrack = null;
    }

}
