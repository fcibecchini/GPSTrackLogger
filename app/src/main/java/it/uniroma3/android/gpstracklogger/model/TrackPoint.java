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

package it.uniroma3.android.gpstracklogger.model;

import android.location.Location;

import java.util.Date;

/**
 * Created by Fabio on 02/05/2015.
 */
public class TrackPoint {
    private String name;
    private double latitude;
    private double longitude;
    private double altitude;
    private float speed;
    private String desc;
    private Date time;

    public TrackPoint() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean hasAltitude() {
        return this.altitude != 0;
    }

    public boolean hasSpeed() {
        return this.speed != 0;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean hasDesc() {
        return this.desc!=null;
    }

    public float distanceTo(TrackPoint t2) {
        Location l1 = new Location("l1");
        Location l2 = new Location("l2");
        l1.setLatitude(this.getLatitude());
        l1.setLongitude(this.getLongitude());
        l2.setLatitude(t2.getLatitude());
        l2.setLongitude(t2.getLongitude());
        return l1.distanceTo(l2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrackPoint trackPoint = (TrackPoint) o;

        if (Double.compare(trackPoint.latitude, latitude) != 0) return false;
        if (Double.compare(trackPoint.longitude, longitude) != 0) return false;
        if (Double.compare(trackPoint.altitude, altitude) != 0) return false;
        if (Float.compare(trackPoint.speed, speed) != 0) return false;
        return !(time != null ? !time.equals(trackPoint.time) : trackPoint.time != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(altitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (speed != +0.0f ? Float.floatToIntBits(speed) : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        return result;
    }
}
