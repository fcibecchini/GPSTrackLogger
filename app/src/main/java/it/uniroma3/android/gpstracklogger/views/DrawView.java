package it.uniroma3.android.gpstracklogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

import java.util.Iterator;
import java.util.List;

import it.uniroma3.android.gpstracklogger.application.Converter;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;


public class DrawView extends View {
    private boolean set;
    private Track current;
    private List<Track> imported;
    private List<TrackPoint> waypoints;
    private Converter converter;
    private double scala = 100000; // 100 m
    Paint paint = new Paint();
    private float scaleFactor;
    private float xc, yc;
    private final int[] colors = {-16777216,-16776961,-16711681,
            -12303292,-7829368,-16711936,-3355444,-65281,-65536,-256};

    public DrawView(Context context) {
        super(context);
        paint.setStrokeWidth(4);
        paint.setTextSize(30);
        scaleFactor = 1f;
        converter = Session.getConverter();
        current = Session.getController().getCurrentTrack();
        imported = Session.getController().getImportedTracks();
        waypoints = Session.getController().getWaypoints();
    }

    public void setScaleFactor(float scale) {
        this.scaleFactor = scale;
    }

    public float getScaleFactor() {
        return this.scaleFactor;
    }

    public void translateCanvas(float x, float y) {
        xc+=x/10;
        yc+=y/10;
    }

    public void setZoomCanvas(float x, float y) {
        xc+=x;
        yc+=y;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        initDraw(canvas);
        drawTrack(canvas, current, 0);
        if (!imported.isEmpty()) {
            int i = 1;
            for (Track track : imported) {
                drawTrack(canvas, track, i);
                i = (i==colors.length) ? 1 : i+1;
            }
        }
        drawWayPoints(canvas, waypoints);
        canvas.restore();
    }

    private void initDraw(Canvas canvas) {
        if (!set) {
            xc = canvas.getWidth()/2;
            yc = canvas.getHeight() / 2;
            set = true;
        }
        canvas.translate(xc, yc);
        canvas.scale(scaleFactor, -scaleFactor);
    }

    private void drawTrack(Canvas canvas, Track track, int color) {
        if (track.getTrackPoints().size() > 1) {
            paint.setColor(colors[color]);
            Iterator<TrackPoint> iterator = track.getTrackPoints().iterator();
            TrackPoint t1 = iterator.next();
            TrackPoint t2 = iterator.next();
            if (!Session.isConverterSet()) {
                Session.setConverter(t1.getLongitude(), t1.getLatitude(), scala);
            }
            Point p0 = converter.getPixel(t1);
            Point p1 = converter.getPixel(t2);
            canvas.drawCircle(p0.x, p0.y, 10, paint);
            canvas.drawLine(p0.x, p0.y, p1.x, p1.y, paint);
            canvas.drawCircle(p1.x, p1.y, 10, paint);
            while (iterator.hasNext()) {
                Point p2 = converter.getPixel(iterator.next());
                canvas.drawCircle(p1.x, p1.y, 10, paint);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                canvas.drawCircle(p2.x, p2.y, 10, paint);
                p1 = p2;
            }
        }
    }

    private void drawWayPoints(Canvas canvas, List<TrackPoint> waypoints) {
        if (!waypoints.isEmpty() && Session.isConverterSet()) {
            for (TrackPoint p : waypoints) {
                Point p1 = converter.getPixel(p);
                canvas.save();
                canvas.drawCircle(p1.x, p1.y, 10, paint);
                canvas.scale(scaleFactor, -scaleFactor, p1.x, p1.y);
                canvas.drawText(p.getName(), p1.x, p1.y, paint);
                canvas.restore();
            }
        }
    }
}
