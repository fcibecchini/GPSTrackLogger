package it.uniroma3.android.gpstracklogger.application;

import android.graphics.Point;
import android.location.Location;

import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * Created by Fabio on 17/05/2015.
 */
public class Converter {
    private double longitude;
    private double latitude;
    private double scala;

    public Converter() {
        //this.scala = 200000; // 50 metri ogni 100 px
        this.scala = 1; // 1 metro/px
    }

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

/*    public Point getPixel(TrackPoint tp) {
        int x = (int) ((tp.getLongitude() - longitude) * scala);
        int y = (int) ((tp.getLatitude() - latitude) * scala);
        return new Point(x, y);
    }*/

    public Point getPixel(TrackPoint tp) {
        float[] result = new float[1];
        Location.distanceBetween(latitude, longitude, latitude, tp.getLongitude(), result);
        int x = (int) (result[0]*scala);
        if (longitude > tp.getLongitude())
            x*=-1;
        Location.distanceBetween(latitude, longitude, tp.getLatitude(), longitude, result);
        int y = (int) (result[0]*scala);
        if (latitude > tp.getLatitude())
            y*=-1;
        return new Point(x, y);
    }

    public void update(Point p) {
        TrackPoint newCentre = getTrackPoint(p);
        setLatitude(newCentre.getLatitude());
        setLongitude(newCentre.getLongitude());
    }

    public TrackPoint getTrackPoint(Point p) {
        double degreeLatLenght = 111111;
        double degreeLonLenght = degreeLatLenght * Math.cos(Math.toRadians(latitude));
        double lon = p.x/(degreeLonLenght*scala);
        double lat = p.y/(degreeLatLenght*scala);
        TrackPoint tp = new TrackPoint();
        tp.setLatitude(latitude+lat);
        tp.setLongitude(longitude+lon);
        return tp;
    }
}
