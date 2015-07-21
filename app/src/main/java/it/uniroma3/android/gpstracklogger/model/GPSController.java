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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.events.Events;
import it.uniroma3.android.gpstracklogger.files.FileLoggerFactory;

/**
 * Created by Fabio on 02/05/2015.
 */
public class GPSController {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Runnable writer;
    private ScheduledFuture<?> writerHandle;
    private Track currentTrack;
    private List<Track> importedTracks;
    private List<TrackPoint> waypoints;

    public GPSController() {
        this.importedTracks = new ArrayList<>();
        this.waypoints = new ArrayList<>();
        this.writer = new Runnable() {
            public void run() { writeToFile(false); }
        };
    }

    public void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    public void unregisterEventBus(){
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
    }

    public void setCurrentTrack() {
        this.currentTrack = new Track();
    }

    public Track getCurrentTrack() {
        return this.currentTrack;
    }

    public boolean isCurrentTrackEmpty() {
        return this.currentTrack.isEmpty();
    }

    public boolean addTrackPoint(Location loc) {
        if (this.currentTrack == null)
            this.currentTrack = new Track();
        return this.currentTrack.addTrackPoint(loc);
    }

    public boolean addTrack(Track track) {
        return this.importedTracks.add(track);
    }

    public List<Track> getImportedTracks() {
        return this.importedTracks;
    }

    public boolean removeImportedTrack(String name) {
        for (Track t : this.importedTracks) {
            if (t.getName().equals(name)) {
                return this.importedTracks.remove(t);
            }
        }
        return false;
    }

    public Track getImportedTrack(String name) {
        for (Track t : this.importedTracks) {
            if (t.getName().equals(name)) {
                return t;
            }
        }
        return null;
    }

    public List<TrackPoint> getWaypoints() {
        return this.waypoints;
    }

    public boolean addWayPoint(TrackPoint point) {
        return this.waypoints.add(point);
    }

    public void removeWayPoints() {
        this.waypoints.clear();
    }

    public void scheduleWriting() {
        writerHandle = scheduler.scheduleAtFixedRate(writer, AppSettings.getPeriod(), AppSettings.getPeriod(), TimeUnit.MINUTES);
    }

    public void stopWriting() {
        writerHandle.cancel(true);
    }

    public void writeToFile(boolean stop) {
        FileLoggerFactory.write(this.currentTrack);
        if (stop)
            this.currentTrack = null;
    }

    public void loadTrack(String fileName) {
        FileLoggerFactory.loadGpxFile(fileName);
    }

    public boolean setReturn() {
        if (!this.currentTrack.isEmpty() && this.currentTrack.size()>1) {
            this.currentTrack.setReturn();
            return true;
        }
        return false;
    }

    public void startLogging() {
        EventBus.getDefault().post(new Events.Start());
        scheduleWriting();
    }

    public void stopLogging() {
        stopWriting();
        EventBus.getDefault().post(new Events.Stop());
    }

    public void removeErrorSpeed(int position, float speed) {
        Track track = importedTracks.get(position);
        Track newTrack = track.removeErrorSpeed(speed);
        FileLoggerFactory.write(newTrack);
    }

    public void onEvent(Events.LoadTrack loadTrack) {
        addTrack(loadTrack.track);
    }

    public void onEvent(Events.LoadWayPoint loadWayPoint) {
        addWayPoint(loadWayPoint.trackPoint);
    }

}
