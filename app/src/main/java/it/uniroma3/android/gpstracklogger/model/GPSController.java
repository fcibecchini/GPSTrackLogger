package it.uniroma3.android.gpstracklogger.model;

import android.location.Location;

import it.uniroma3.android.gpstracklogger.logger.FileLoggerFactory;

/**
 * Created by Fabio on 02/05/2015.
 */
public class GPSController {
    private Track currentTrack;

    public Track getCurrentTrack() {
        return this.currentTrack;
    }

    public boolean addTrackPoint(Location loc) {
        if (this.currentTrack == null)
            this.currentTrack = new Track();
        return this.currentTrack.addTrackPoint(loc);
    }

    public void writeToFile(boolean stop) {
        FileLoggerFactory.write(this.currentTrack);
        if (stop)
            this.currentTrack = null;
    }

}
