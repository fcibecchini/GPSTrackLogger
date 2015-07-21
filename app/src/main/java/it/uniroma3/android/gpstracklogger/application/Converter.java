/*******************************************************************************
 * This file is part of GPSTrackLogger.
 * Copyright (C) 2015  Fabio Cibecchini
 *
 * GPSTrackLogger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GPSTrackLogger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSTrackLogger.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package it.uniroma3.android.gpstracklogger.application;

import android.graphics.Point;
import android.location.Location;

import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * Created by Fabio on 17/05/2015.
 */
public class Converter {
    private double longitude;
    private double latitude;
    private double scala;

    public Converter() {
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
