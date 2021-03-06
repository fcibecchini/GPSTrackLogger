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

/**
 * Created by Fabio on 02/05/2015.
 */

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class Track {
    private String name;
    private String path;
    private Set<TrackPoint> trackPoints;

    public Track() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy kk:mm:ss");
        this.name = sdf.format(new Date());
        this.trackPoints = new TreeSet<>(new TrackPointComparator());
    }

    public Track(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Set<TrackPoint> getTrackPoints(){
        return this.trackPoints;
    }

    public void setTrackPoints(Set<TrackPoint> tp) {
        this.trackPoints = tp;
    }

    public int size() {
        return this.trackPoints.size();
    }

    public boolean addTrackPoint(Location loc) {
        TrackPoint trackPoint = new TrackPoint();
        Date timestamp = new Date(loc.getTime());
        trackPoint.setTime(timestamp);
        trackPoint.setLatitude(loc.getLatitude());
        trackPoint.setLongitude(loc.getLongitude());
        if (loc.hasAltitude())
            trackPoint.setAltitude(loc.getAltitude());
        if (loc.hasSpeed())
            trackPoint.setSpeed(loc.getSpeed());
        return this.trackPoints.add(trackPoint);
    }

    public Map<Double, Double> getAltitudePerTime() {
        Map<Double, Double> map = new TreeMap<>();
        TreeSet<TrackPoint> tps = (TreeSet<TrackPoint>) trackPoints;
        double initTime = (tps.first().getTime().getTime()/1000)/60;
        double tpTime, dTime;
        for (TrackPoint tp : tps) {
            tpTime = (tp.getTime().getTime()/1000)/60;
            dTime = (tpTime - initTime);
            map.put(dTime, tp.getAltitude());
        }
        return map;
    }

    public Map<Double, Double> getAltitudePerDistance() {
        Map<Double, Double> map = new TreeMap<>();
        Iterator<TrackPoint> it = trackPoints.iterator();
        TrackPoint t1 = it.next();
        double distance = 0;
        map.put(distance, t1.getAltitude());
        while (it.hasNext()) {
            TrackPoint t2 = it.next();
            distance+=(t1.distanceTo(t2)/1000);
            map.put(distance, t2.getAltitude());
            t1 = t2;
        }
        return map;
    }

    public int elapsedTime(TrackPoint tp, long fixedTime) {
        TreeSet<TrackPoint> list = (TreeSet<TrackPoint>) getTrackPoints();
        long first = list.first().getTime().getTime();
        long current = tp.getTime().getTime();
        int time = (int) (current - first)/1000;
        if (time >= fixedTime)
            return time;
        return 0;
    }

    public int elapsedDistance(TrackPoint tp, int fixedDistance) {
        float distance = 0;
        boolean found = false;
        Iterator<TrackPoint> it = this.trackPoints.iterator();
        TrackPoint t1 = it.next();
        while (it.hasNext() && !found) {
            TrackPoint t2 = it.next();
            distance+=t1.distanceTo(t2);
            t1 = t2;
            if (t2.equals(tp))
                found = true;
        }
        if (distance >= fixedDistance)
            return (int)distance;
        return 0;
    }

    public int getTotalDistance() {
        float totalDistance = 0;
        Iterator<TrackPoint> it = this.trackPoints.iterator();
        TrackPoint t1 = it.next();
        while (it.hasNext()) {
            TrackPoint t2 = it.next();
            totalDistance+=t1.distanceTo(t2);
            t1 = t2;
        }
        return (int) totalDistance;
    }

    public long getTotalTime() {
        TreeSet<TrackPoint> list = (TreeSet<TrackPoint>) getTrackPoints();
        long first = list.first().getTime().getTime();
        long last = list.last().getTime().getTime();
        return (last - first)/1000;
    }

    public void setReturn() {
        TreeSet<TrackPoint> tPoints = (TreeSet<TrackPoint>) getTrackPoints();
        TrackPoint tp = tPoints.last();
        tp.setDesc("Return");
    }

    public TrackPoint[] getBorderPoints() {
        TrackPoint[] tps = new TrackPoint[3];
        TreeSet<TrackPoint> list = (TreeSet<TrackPoint>) getTrackPoints();
        TrackPoint maxtp1 = maxDistanceFrom(list.first());
        tps[0] = maxtp1;
        TrackPoint maxtp2 = maxDistanceFrom(maxtp1);
        tps[2] = maxtp2;
        float maxDistance = maxtp1.distanceTo(maxtp2);
        for (TrackPoint tp : list) {
            float distance = maxtp2.distanceTo(tp);
            if (distance >= (maxDistance/2)) {
                tps[1] = tp;
                return tps;
            }
        }
        return tps;
    }

    public TrackPoint maxDistanceFrom(TrackPoint start) {
        TrackPoint maxtp = start;
        float maxDistance = 0;
        for (TrackPoint tp : getTrackPoints()) {
            float distance = start.distanceTo(tp);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxtp = tp;
            }
        }
        return maxtp;
    }

    public int totalClimb() {
        int totalClimb = 0;
        Iterator<TrackPoint> it = trackPoints.iterator();
        TrackPoint tp1 = it.next();
        while (it.hasNext()) {
            TrackPoint tp2 = it.next();
            double alt = tp2.getAltitude()-tp1.getAltitude();
            if (alt > 0) {
                totalClimb+=alt;
            }
            tp1 = tp2;
        }
        return totalClimb;
    }

    public Map<TrackPoint, Double> getSpeedPerPoint() {
        Map<TrackPoint, Double> map = new TreeMap<>(new TrackPointComparator());
        Iterator<TrackPoint> it = trackPoints.iterator();
        TrackPoint t1 = it.next();
        map.put(t1, 0.0);
        double distance, time, speed;
        while (it.hasNext()) {
            TrackPoint t2 = it.next();
            distance = (t1.distanceTo(t2)/1000);
            time = (double) (t2.getTime().getTime() - t1.getTime().getTime()) / (1000*3600);
            speed = distance/time;
            map.put(t2, speed);
            t1 = t2;
        }
        return map;
    }

    public Track removeErrorSpeed(float speed) {
        TreeMap<TrackPoint, Double> speedPerPoint = (TreeMap<TrackPoint,Double>) getSpeedPerPoint();
        Set<TrackPoint> newPoints = new TreeSet<>(new TrackPointComparator());
        Iterator<TrackPoint> it = trackPoints.iterator();
        TrackPoint t1 = it.next();
        newPoints.add(t1);
        TrackPoint t2 = it.next();
        TrackPoint t3 = null;
        double speed1, speed2, speed3=0, ds1, ds2=0;
        while (it.hasNext()) {
            t3 = it.next();
            speed1 = speedPerPoint.get(t1);
            speed2 = speedPerPoint.get(t2);
            speed3 = speedPerPoint.get(t3);
            ds1 = Math.abs(speed2 - speed1);
            ds2 = Math.abs(speed3 - speed2);
            if ((ds1<15 && ds2<15) || speed2<=speed)
                newPoints.add(t2);
            t1 = t2;
            t2 = t3;
        }
        if (t3 != null && (speed3<=speed || ds2<15))
            newPoints.add(t3);
        Track newTrack = new Track();
        newTrack.setName("fixed "+this.getName().replace(".gpx", ""));
        newTrack.setTrackPoints(newPoints);
        return newTrack;
    }

    public boolean isEmpty() {
        return this.trackPoints.isEmpty();
    }

    public boolean canDraw() {
        return this.trackPoints.size()>1;
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
