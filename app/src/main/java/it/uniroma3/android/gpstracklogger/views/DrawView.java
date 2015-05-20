package it.uniroma3.android.gpstracklogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.View;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import it.uniroma3.android.gpstracklogger.application.Converter;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * TODO: document your custom view class.
 */
public class DrawView extends View {
    private Track current;
    private List<Track> imported;
    private List<TrackPoint> waypoints;
    private Converter converter;
    private double scala = 1000000; // 10 m
    Paint paint = new Paint();
    private float scaleFactor;
    private int xc, yc;

    public DrawView(Context context) {
        super(context);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        paint.setTextSize(30);
        scaleFactor = 1f;
        current = Session.getController().getCurrentTrack();
        imported = Session.getController().getImportedTracks();
        waypoints = Session.getController().getWaypoints();
    }

    public void setScaleFactor(float scale) {
        this.scaleFactor = scale;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        xc = canvas.getWidth()/2;
        yc = canvas.getHeight()/2;
        canvas.translate(xc, yc);
        canvas.scale(scaleFactor, -scaleFactor);
        if (Session.isStarted()) {
            Set<TrackPoint> currentPoints = current.getTrackPoints();
            if (currentPoints.size() > 1) {
                Iterator<TrackPoint> iterator = currentPoints.iterator();
                TrackPoint t1 = iterator.next();
                if (converter == null)
                    converter = new Converter(t1.getLongitude(), t1.getLatitude(), scala);
                TrackPoint t2 = iterator.next();
                Point p1 = converter.getPixel(t2);
                canvas.drawLine(0, 0, p1.x, p1.y, paint);
                while (iterator.hasNext()) {
                    Point p2 = converter.getPixel(iterator.next());
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                    p1 = p2;
                }
            }
        }
        if (!imported.isEmpty()) {
            for (Track track : imported) {
                if (!track.getTrackPoints().isEmpty()) {
                    Iterator<TrackPoint> iterator = track.getTrackPoints().iterator();
                    TrackPoint t1 = iterator.next();
                    if (converter == null)
                        converter = new Converter(t1.getLongitude(), t1.getLatitude(), scala);
                    TrackPoint t2 = iterator.next();
                    Point p1 = converter.getPixel(t2);
                    canvas.drawLine(0, 0, p1.x, p1.y, paint);
                    while (iterator.hasNext()) {
                        Point p2 = converter.getPixel(iterator.next());
                        canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                        p1 = p2;
                    }
                }
            }
        }

        if (!waypoints.isEmpty() && converter != null) {
            for (TrackPoint p : waypoints) {
                Point p1 = converter.getPixel(p);
                canvas.save();
                canvas.drawCircle(p1.x, p1.y, 10, paint);
                canvas.scale(scaleFactor, -scaleFactor, p1.x, p1.y);
                canvas.drawText(p.getName(), p1.x, p1.y, paint);
                canvas.restore();
            }
        }

        canvas.restore();
    }
}
