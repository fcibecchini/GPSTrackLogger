package it.uniroma3.android.gpstracklogger.model;

import java.util.Date;

/**
 * Created by Fabio on 02/05/2015.
 */
public class TrackPoint {
    private double latitude;
    private double longitude;
    private double altitude;
    private float speed;
    private Date time;

    public TrackPoint() {

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
