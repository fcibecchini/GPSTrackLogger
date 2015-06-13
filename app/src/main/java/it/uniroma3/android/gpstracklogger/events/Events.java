package it.uniroma3.android.gpstracklogger.events;

import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * Created by Fabio on 04/05/2015.
 */
public class Events {

    public static class Start {}

    public static class Stop {}

    public static class LoadTrack {
        public Track track;

        public LoadTrack(Track track) {
            this.track = track;
        }
    }

    public static class LoadWayPoint {
        public TrackPoint trackPoint;

        public LoadWayPoint(TrackPoint trackPoint) {
            this.trackPoint = trackPoint;
        }
    }

}
