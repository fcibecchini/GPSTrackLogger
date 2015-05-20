package it.uniroma3.android.gpstracklogger.application;

import android.graphics.Point;

import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * Created by Fabio on 17/05/2015.
 */
public class Converter {
    private double longitude;
    private double latitude;
    private double scala;

    public Converter(double lon, double lat, double s) {
        this.longitude = lon;
        this.latitude = lat;
        this.scala = s;
    }

    public Point getPixel(TrackPoint tp) {
        int x = (int) ((tp.getLongitude() - longitude) * scala);
        int y = (int) ((tp.getLatitude() - latitude) * scala);
        return new Point(x, y);
    }
}
