package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.views.DrawView;


public class StartDrawActivity extends Activity {
    private DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Track track = Session.getController().getCurrentTrack();
        double latitude = track.getTrackPoints().iterator().next().getLatitude();
        drawView = new DrawView(this, track, latitude);
        drawView.setBackgroundColor(Color.WHITE);
        setContentView(drawView);
    }
}
