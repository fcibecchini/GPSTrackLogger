package it.uniroma3.android.gpstracklogger.model;

import android.location.Location;

import it.uniroma3.android.gpstracklogger.logger.FileLoggerFactory;

/**
 * Created by Fabio on 02/05/2015.
 */
public class GPSController {
    private Track currentTrack;

    public GPSController() {
        this.currentTrack = new Track();
    }

    public Track getCurrentTrack() {
        return this.currentTrack;
    }

    public boolean addTrackPoint(Location loc) {
        return this.currentTrack.addTrackPoint(loc);
    }

    public void writeToFile() {
        FileLoggerFactory.write(this.currentTrack);
    }

}
