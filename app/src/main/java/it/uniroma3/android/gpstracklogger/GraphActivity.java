package it.uniroma3.android.gpstracklogger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Map;

import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;


public class GraphActivity extends AppCompatActivity {
    private GraphView graph;
    private GridLabelRenderer renderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        graph = (GraphView) findViewById(R.id.graph);
        renderer = graph.getGridLabelRenderer();
        drawGraph("Time");
    }

    private Track getTrack() {
        Track t = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int position = extras.getInt("TrackPosition");
            t = Session.getController().getImportedTracks().get(position);
        }
        else {
            finish();
        }
        return t;
    }

    private void drawGraph(String axis) {
        graph.removeAllSeries();
        if (axis.equals("Time")) {
            drawAltitudeTime(getTrack());
        }
        else if (axis.equals("Distance")) {
            drawAltitudeDistance(getTrack());
        }
    }

    private void drawAltitudeTime(Track track) {
        graph.setTitle("Time - Altitude");
        renderer.setHorizontalAxisTitle("time (min)");
        drawMap(track.getAltitudePerTime());
    }

    private void drawAltitudeDistance(Track track) {
        graph.setTitle("Distance - Altitude");
        renderer.setHorizontalAxisTitle("distance (km)");
        drawMap(track.getAltitudePerDistance());
    }

    private void drawMap(Map<Double, Double> map) {
        DataPoint[] points = new DataPoint[map.size()];
        int i = 0;
        for (Double key : map.keySet()) {
            DataPoint p = new DataPoint(key, map.get(key));
            points[i] = p;
            i++;
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);
        graph.addSeries(series);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_alttime: {
                drawGraph("Time");
                return true;
            }
            case R.id.action_altdistance: {
                drawGraph("Distance");
                return true;
            }
            case R.id.action_close: {
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
