package it.uniroma3.android.gpstracklogger.model;

/**
 * Created by Fabio on 02/05/2015.
 */

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class Track {
    private String name;
    private List<TrackPoint> trackPoints;

    public Track() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        this.name = sdf.format(new Date());
        this.trackPoints = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public List<TrackPoint> getTrackPoints(){
        return this.trackPoints;
    }

    public boolean addTrackPoint(Location loc) {
        TrackPoint trackPoint = new TrackPoint();
        Date timestamp = new Date(loc.getTime());
        String time = getISODateTime(timestamp);
        trackPoint.setTime(time);
        trackPoint.setLatitude(loc.getLatitude());
        trackPoint.setLongitude(loc.getLongitude());
        if (loc.hasAltitude())
            trackPoint.setAltitude(loc.getAltitude());
        if (loc.hasSpeed())
            trackPoint.setSpeed(loc.getSpeed());
        return this.trackPoints.add(trackPoint);
    }

    private String getISODateTime(Date dateToFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                Locale.ITALY);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(dateToFormat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Track track = (Track) o;

        if (name != null ? !name.equals(track.name) : track.name != null) return false;
        return !(trackPoints != null ? !trackPoints.equals(track.trackPoints) : track.trackPoints != null);

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (trackPoints != null ? trackPoints.hashCode() : 0);
        return result;
    }
}
