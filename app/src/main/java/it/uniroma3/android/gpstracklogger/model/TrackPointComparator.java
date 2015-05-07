package it.uniroma3.android.gpstracklogger.model;

import java.util.Comparator;

/**
 * Created by Fabio on 07/05/2015.
 */
public class TrackPointComparator implements Comparator<TrackPoint> {

    public int compare(TrackPoint p1, TrackPoint p2) {
        return p1.getTime().compareTo(p2.getTime());
    }
}
