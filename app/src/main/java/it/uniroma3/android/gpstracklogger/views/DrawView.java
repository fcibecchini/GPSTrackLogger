package it.uniroma3.android.gpstracklogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.application.Converter;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.application.Utilities;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;


public class DrawView extends View {
    private Track current;
    private List<Track> imported;
    private List<TrackPoint> waypoints;
    private Converter converter;
    private float xc, yc;
    private int fixedTime;
    private int fixedDistance;
    private float azimuth;
    private boolean compass;
    Paint paint = new Paint();
    Paint textPaint = new Paint();
    private Map<String, Integer> colors;

    public DrawView(Context context) {
        super(context);
        setPaint();
        converter = Session.getConverter();
        current = Session.getController().getCurrentTrack();
        imported = Session.getController().getImportedTracks();
        waypoints = Session.getController().getWaypoints();
        fixedTime = AppSettings.getFixedTime();
        fixedDistance = AppSettings.getFixedDistance();
        colors = Utilities.getColors();
        compass = Session.isCompass();
    }

    private void setPaint() {
        paint.setAntiAlias(true);
        paint.setStrokeWidth(4);
        textPaint.setTextSize(30);
        textPaint.setAntiAlias(true);
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

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        initDraw(canvas);
        if (current != null)
            drawTrack(canvas, current, "current", 0);
        if (!imported.isEmpty()) {
            int i = 1;
            for (Track track : imported) {
                drawTrack(canvas, track, "loaded", i);
                i = (i<7) ? i+1 : 1;
            }
        }
        if (!waypoints.isEmpty())
            drawWayPoints(canvas, waypoints);
        canvas.restore();
    }

    private void initDraw(Canvas canvas) {
        xc = canvas.getWidth() / 2;
        yc = canvas.getHeight() / 2;
        canvas.translate(xc, yc);
        canvas.scale(1, -1);
        if (compass) {
            canvas.rotate(azimuth);
        }
    }

    private void drawTrack(Canvas canvas, Track track, String color, int i) {
        if (track.canDraw()) {
            textPaint.setTextAlign(Paint.Align.RIGHT);
            int timeCount = 1;
            int distanceCount = 1;
            TreeSet<TrackPoint> tps = (TreeSet) track.getTrackPoints();
            paint.setColor(colors.get(color+i+"A"));
            Iterator<TrackPoint> it = tps.iterator();
            TrackPoint t1 = it.next();
            if (color.equals("current") && compass) {
                Session.setConverter(tps.last().getLongitude(),tps.last().getLatitude());
            }
            else if (!Session.isConverterSet() && !compass) {
                Session.setConverter(t1.getLongitude(),t1.getLatitude());
                Session.setConverterSet(true);
            }
            Point p1 = converter.getPixel(t1);
            while (it.hasNext()) {
                TrackPoint t2 = it.next();
                Point p2 = converter.getPixel(t2);
                timeCount = showFixedParameters(canvas,track,t2,p2,fixedTime,timeCount);
                distanceCount = showFixedParameters(canvas,track,t2,p2,fixedDistance,distanceCount);
                canvas.drawCircle(p1.x, p1.y, 5, paint);
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint);
                canvas.drawCircle(p2.x, p2.y, 5, paint);
                if (t2.hasDesc()) {
                    if (t2.getDesc().equals("Return")) {
                        paint.setColor(colors.get(color+i+"B"));
                        textPaint.setTextAlign(Paint.Align.LEFT);
                    }
                }
                p1 = p2;
            }
        }
    }

    private int showFixedParameters(Canvas canvas, Track track, TrackPoint tp, Point p, int parameter, int count) {
        if (parameter!=0) {
            int value = 0;
            String svalue = null;
            if (parameter==fixedTime) {
                value = track.elapsedTime(tp, parameter * count);
                svalue = Utilities.getFormattedTime(value, false).substring(0,5);
            }
            else if (parameter==fixedDistance) {
                value = track.elapsedDistance(tp, parameter * count);
                boolean decimal = false;
                if (parameter<1000)
                    decimal = true;
                svalue = Utilities.getFormattedDistance(value, decimal);
            }
            if (value != 0 && svalue!=null) {
                drawText(canvas, p.x, p.y, 5, svalue);
                while (value>=parameter*(count+1)) {
                    count++;
                }
                count++;
            }
        }
        return count;
    }

    private void drawText(Canvas canvas, int x, int y, int margin, String text) {
        if (converter.getScala()>=1) {
            textPaint.setColor(paint.getColor());
            canvas.save();
            Rect rect = new Rect();
            textPaint.getTextBounds(text, 0, text.length(), rect);
            canvas.scale(1, -1, x, y);
            if (textPaint.getTextAlign().equals(Paint.Align.RIGHT))
                x-=margin;
            else
                x+=margin;
            canvas.drawText(text, x, y-rect.height()/2, textPaint);
            canvas.restore();
        }
    }

    private void drawWayPoints(Canvas canvas, List<TrackPoint> waypoints) {
        paint.setColor(colors.get("waypoint"));
        textPaint.setTextAlign(Paint.Align.RIGHT);
        for (TrackPoint tp : waypoints) {
            Point p1 = converter.getPixel(tp);
            canvas.drawCircle(p1.x, p1.y, 5, paint);
            drawText(canvas, p1.x, p1.y, 10, tp.getName());
        }
    }

    public void fit() {
        Track track;
        if (current != null  && !current.isEmpty())
            track = current;
        else if (!imported.isEmpty())
            track = imported.get(0);
        else
            return;
        TrackPoint[] fandm = track.getBorderPoints();
        converter.setLongitude(fandm[1].getLongitude());
        converter.setLatitude(fandm[1].getLatitude());
        TrackPoint maxtp1 = fandm[0];
        TrackPoint maxtp2 = fandm[2];
        Point p1 = converter.getPixel(maxtp1);
        Point p2 = converter.getPixel(maxtp2);
        int x1 = Math.abs(p1.x);
        int y1 = Math.abs(p1.y);
        int x2 = Math.abs(p2.x);
        int y2 = Math.abs(p2.y);
        while (x1<xc && y1<yc && x2<xc && y2<yc) {
            setScala(1.5);
            p1 = converter.getPixel(maxtp1);
            p2 = converter.getPixel(maxtp2);
            x1 = Math.abs(p1.x);
            y1 = Math.abs(p1.y);
            x2 = Math.abs(p2.x);
            y2 = Math.abs(p2.y);
        }
        while (x1>xc || y1>yc || x2>xc || y2>yc) {
            setScala(1/1.5);
            p1 = converter.getPixel(maxtp1);
            p2 = converter.getPixel(maxtp2);
            x1 = Math.abs(p1.x);
            y1 = Math.abs(p1.y);
            x2 = Math.abs(p2.x);
            y2 = Math.abs(p2.y);
        }
    }

    public void restoreScale() {
        converter.setScala(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!Session.isCompass()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    setConversion(event.getX(), event.getY());
                }
                break;
            }
            invalidate();
        }
        return true;
    }
}
