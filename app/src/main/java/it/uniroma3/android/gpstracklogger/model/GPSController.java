package it.uniroma3.android.gpstracklogger.model;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.files.FileLoggerFactory;

/**
 * Created by Fabio on 02/05/2015.
 */
public class GPSController {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Runnable writer;
    private ScheduledFuture<?> writerHandle;
    private Track currentTrack;
    private List<Track> importedTracks;
    private List<TrackPoint> waypoints;

    public GPSController() {
        this.importedTracks = new ArrayList<>();
        this.waypoints = new ArrayList<>();
        this.writer = new Runnable() {
            public void run() { writeToFile(false); }
        };
    }

    public void setCurrentTrack() {
        this.currentTrack = new Track();
    }

    public Track getCurrentTrack() {
        return this.currentTrack;
    }

    public boolean addTrackPoint(Location loc) {
        if (this.currentTrack == null)
            this.currentTrack = new Track();
        return this.currentTrack.addTrackPoint(loc);
    }

    public boolean addTrack(Track track) {
        return this.importedTracks.add(track);
    }

    public List<Track> getImportedTracks() {
        return this.importedTracks;
    }

    public List<TrackPoint> getWaypoints() {
        return this.waypoints;
    }

    public boolean addWayPoint(TrackPoint point) {
        return this.waypoints.add(point);
    }

    public void scheduleWriting() {
        writerHandle = scheduler.scheduleAtFixedRate(writer, AppSettings.getInitialDelay(), AppSettings.getPeriod(), TimeUnit.MINUTES);
    }

    public void stopWriting() {
        writerHandle.cancel(true);
    }

    public void writeToFile(boolean stop) {
        FileLoggerFactory.write(this.currentTrack);
        if (stop)
            this.currentTrack = null;
    }

}
