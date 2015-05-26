package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;

import de.greenrobot.event.EventBus;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.events.Events;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;


public class LocDetailsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loc_details);
        setDisplay();
    }

    private void setDisplay() {
        if (Session.isLocationChanged())
            displayProviderInfo();
        TreeSet<TrackPoint> trackPoints = null;
        if (Session.getController().getCurrentTrack() != null)
            trackPoints = (TreeSet) Session.getController().getCurrentTrack().getTrackPoints();
        if (!trackPoints.isEmpty())
            displayLocationInfo(trackPoints.last());
    }

    private void displayProviderInfo() {
        setTextViewValue(R.id.available, String.valueOf(Session.isProviderAvailable()));
        setTextViewValue(R.id.enabled, String.valueOf(Session.isProviderEnabled()));
    }

    private void displayLocationInfo(TrackPoint location) {
        Date timestamp = location.getTime();
        setTextViewValue(R.id.timestamp, timestamp.toString());
        double latitude = location.getLatitude();
        setTextViewValue(R.id.latitude, String.valueOf(latitude));
        double longitude = location.getLongitude();
        setTextViewValue(R.id.longitude, String.valueOf(longitude));
        if (location.hasAltitude()) {
            double altitude = location.getAltitude();
            setTextViewValue(R.id.altitude, String.valueOf(altitude));
        }
        if (location.hasSpeed()) {
            float speed = location.getSpeed();
            setTextViewValue(R.id.speed, String.valueOf(speed));
        }
    }

    private void setTextViewValue(int textViewId, String value) {
        TextView textView = (TextView) findViewById(textViewId);
        textView.setText(value);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDisplay();
    }
}
