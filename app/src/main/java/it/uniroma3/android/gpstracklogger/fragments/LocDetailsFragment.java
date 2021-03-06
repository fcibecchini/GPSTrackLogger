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

package it.uniroma3.android.gpstracklogger.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.TreeSet;

import it.uniroma3.android.gpstracklogger.R;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.application.Utilities;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;


public class LocDetailsFragment extends Fragment {
    RelativeLayout rlayout;
    private Track current;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rlayout = (RelativeLayout) inflater.inflate(R.layout.fragment_loc_details, container, false);
        current = Session.getController().getCurrentTrack();
        setDisplay();
        return rlayout;
    }

    private void setDisplay() {
        if (Session.isLocationChanged())
            displayProviderInfo();
        TreeSet<TrackPoint> trackPoints = null;
        if (current != null)
            trackPoints = (TreeSet<TrackPoint>) current.getTrackPoints();
        if (trackPoints!=null && !trackPoints.isEmpty()) {
            setTextViewValue(R.id.currentdist, Utilities.getFormattedDistance(current.getTotalDistance(), true));
            setTextViewValue(R.id.currenttime, Utilities.getFormattedTime(current.getTotalTime(), true));
            displayLocationInfo(trackPoints.last());
        }
    }

    private void displayProviderInfo() {
        setTextViewValue(R.id.available, String.valueOf(Session.isProviderAvailable()));
        setTextViewValue(R.id.enabled, String.valueOf(Session.isProviderEnabled()));
    }

    private void displayLocationInfo(TrackPoint tp) {
        setTextViewValue(R.id.latitude, Utilities.parseLatitude(tp.getLatitude()));
        setTextViewValue(R.id.longitude, Utilities.parseLongitude(tp.getLongitude()));
        if (tp.hasAltitude()) {
            int altitude = (int) tp.getAltitude();
            setTextViewValue(R.id.altitude, Utilities.formatAltitude(altitude));
        }
        else {
            setTextViewValue(R.id.altitude, "N/A");
        }
        if (tp.hasSpeed()) {
            float speed = tp.getSpeed();
            setTextViewValue(R.id.speed, Utilities.formatSpeed(speed));
        }
        else {
            setTextViewValue(R.id.speed, "N/A");
        }
    }

    private void setTextViewValue(int textViewId, String value) {
        TextView textView = (TextView) rlayout.findViewById(textViewId);
        textView.setText(value);
    }

    @Override
    public void onResume() {
        super.onResume();
        setDisplay();
    }
}
