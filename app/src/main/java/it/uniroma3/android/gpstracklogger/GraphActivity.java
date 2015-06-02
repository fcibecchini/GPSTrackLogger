package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Map;

import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;


public class GraphActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int position = extras.getInt("TrackPosition");
            drawGraph(Session.getController().getImportedTracks().get(position));
        }
    }

    private void drawGraph(Track track) {
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle("Time - Altitude");
        GridLabelRenderer renderer = graph.getGridLabelRenderer();
        renderer.setHorizontalAxisTitle("time (min)");
        renderer.setVerticalAxisTitle("altitude (m)");
        Map<Double, Double> map = track.getAltitudePerTime();
        DataPoint[] points = new DataPoint[map.size()];
        int i = 0;
        for (Double time : map.keySet()) {
            DataPoint p = new DataPoint(time, map.get(time));
            points[i] = p;
            i++;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.addSeries(series);
    }
}
