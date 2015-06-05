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
            trackPoints = (TreeSet) current.getTrackPoints();
        if (!trackPoints.isEmpty()) {
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
            double altitude = tp.getAltitude();
            setTextViewValue(R.id.altitude, String.valueOf(altitude));
        }
        if (tp.hasSpeed()) {
            float speed = tp.getSpeed();
            setTextViewValue(R.id.speed, String.valueOf(speed));
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
