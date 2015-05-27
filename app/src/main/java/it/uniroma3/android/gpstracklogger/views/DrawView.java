package it.uniroma3.android.gpstracklogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;
import java.util.List;

import it.uniroma3.android.gpstracklogger.application.Converter;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;


public class DrawView extends View {
    private Track current;
    private List<Track> imported;
    private List<TrackPoint> waypoints;
    private Converter converter;
    Paint paint = new Paint();
    private float xc, yc;
    private final int[] colors = {-16777216,-16776961,-12303292,
            -7829368,-16711936,-3355444,-65281,-65536};

    public DrawView(Context context) {
        super(context);
        paint.setStrokeWidth(4);
        paint.setTextSize(30);
        converter = Session.getConverter();
        current = Session.getController().getCurrentTrack();
        imported = Session.getController().getImportedTracks();
        waypoints = Session.getController().getWaypoints();
    }

    public void setScala(double factor) {
        double scala = converter.getScala()*factor;
        converter.setScala(scala);
    }

    public void setConversion(float dx1, float dy1) {
        int x1 = (int) (dx1-xc);
        int y1 = (int)((dy1-yc)*-1);
        converter.update(new Point(x1, y1));
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
                i = (i==colors.length-1) ? 1 : i+1;
            }
        }
        drawWayPoints(canvas, waypoints);
        canvas.restore();
    }

    private void initDraw(Canvas canvas) {
        xc = canvas.getWidth() / 2;
        yc = canvas.getHeight() / 2;
        canvas.translate(xc, yc);
        canvas.scale(1, -1);
    }

    private void drawTrack(Canvas canvas, Track track, int color) {
        if (track != null) {
            if (track.getTrackPoints().size() > 1) {
                paint.setColor(colors[color]);
                Iterator<TrackPoint> iterator = track.getTrackPoints().iterator();
                TrackPoint t1 = iterator.next();
                TrackPoint t2 = iterator.next();
                if (!Session.isConverterSet()) {
                    Session.setConverter(t1.getLongitude(), t1.getLatitude());
                }
                Point p0 = converter.getPixel(t1);
                Point p1 = converter.getPixel(t2);
                canvas.drawCircle(p0.x, p0.y, 5, paint);
                canvas.drawLine(p0.x, p0.y, p1.x, p1.y, paint);
                canvas.drawCircle(p1.x, p1.y, 5, paint);
                while (iterator.hasNext()) {
                    Point p2 = converter.getPixel(iterator.next());
                    canvas.drawCircle(p1.x, p1.y, 5, paint);
                    canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                    canvas.drawCircle(p2.x, p2.y, 5, paint);
                    p1 = p2;
                }
            }
        }
    }

    private void drawWayPoints(Canvas canvas, List<TrackPoint> waypoints) {
        if (!waypoints.isEmpty() && Session.isConverterSet()) {
            paint.setColor(colors[7]);
            for (TrackPoint p : waypoints) {
                Point p1 = converter.getPixel(p);
                canvas.save();
                canvas.drawCircle(p1.x, p1.y, 5, paint);
                canvas.scale(1, -1, p1.x, p1.y);
                canvas.drawText(p.getName(), p1.x, p1.y, paint);
                canvas.restore();
            }
        }
    }

    public void fit() {
        TrackPoint max = max();
        if (max != null) {
            Point p = converter.getPixel(max);
            int x = Math.abs(p.x);
            int y = Math.abs(p.y);
            while (x < xc && y < yc) {
                setScala(2);
                p = converter.getPixel(max);
                x = Math.abs(p.x);
                y = Math.abs(p.y);
            }
            while (x > xc || y > yc) {
                setScala(0.5);
                p = converter.getPixel(max);
                x = Math.abs(p.x);
                y = Math.abs(p.y);
            }
        }
    }

    public TrackPoint max() {
        Track track;
        if (current != null)
            track = current;
        else if (!imported.isEmpty())
            track = imported.get(0);
        else
            return null;
        Iterator<TrackPoint> it = track.getTrackPoints().iterator();
        TrackPoint t1 = it.next();
        Session.setConverter(t1.getLongitude(), t1.getLatitude());
        TrackPoint t2 = it.next();
        TrackPoint maxtp = t2;
        float[] result = new float[1];
        Location.distanceBetween(t1.getLatitude(), t1.getLongitude(), t2.getLatitude(), t2.getLongitude(), result);
        float max = result[0];
        while (it.hasNext()) {
            TrackPoint tp = it.next();
            Location.distanceBetween(t1.getLatitude(), t1.getLongitude(), tp.getLatitude(), tp.getLongitude(), result);
            if (result[0] > max) {
                max = result[0];
                maxtp = tp;
            }
        }
        return maxtp;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                final float x = event.getX();
                final float y = event.getY();
                setConversion(x,y);
            }
            break;
        }
        invalidate();
        return true;
    }
}
