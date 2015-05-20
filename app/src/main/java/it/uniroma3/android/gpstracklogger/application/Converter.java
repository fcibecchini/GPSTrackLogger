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

    public Converter() {}

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getScala() {
        return scala;
    }

    public void setScala(double scala) {
        this.scala = scala;
    }

    public Point getPixel(TrackPoint tp) {
        int x = (int) ((tp.getLongitude() - longitude) * scala);
        int y = (int) ((tp.getLatitude() - latitude) * scala);
        return new Point(x, y);
    }
}
