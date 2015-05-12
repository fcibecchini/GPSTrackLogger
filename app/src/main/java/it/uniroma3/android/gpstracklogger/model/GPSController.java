package it.uniroma3.android.gpstracklogger.model;

import android.location.Location;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import it.uniroma3.android.gpstracklogger.logger.FileLoggerFactory;
import it.uniroma3.android.gpstracklogger.logger.GpxFileLogger;

/**
 * Created by Fabio on 02/05/2015.
 */
public class GPSController {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private Track currentTrack;

    public Track getCurrentTrack() {
        return this.currentTrack;
    }

    public boolean addTrackPoint(Location loc) {
        if (this.currentTrack == null)
            this.currentTrack = new Track();
        return this.currentTrack.addTrackPoint(loc);
    }

    public void scheduleWriting() {
        GpxFileLogger logger = FileLoggerFactory.getLogger(this.currentTrack);
        scheduler.scheduleAtFixedRate(logger, 30, 30, TimeUnit.SECONDS);
    }

    public void writeToFile(boolean stop) {
        FileLoggerFactory.write(this.currentTrack);
        if (stop)
            this.currentTrack = null;
    }

}
