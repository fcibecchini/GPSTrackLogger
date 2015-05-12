package it.uniroma3.android.gpstracklogger.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.Display;
import android.view.View;

import java.util.Iterator;

import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * TODO: document your custom view class.
 */
public class DrawView extends View {
    private final double radius = 6378137;
    private double currentLatitude;
    private double dimensioniCasella = 0.0001; // 10 m
    private double scalaLongitudine = 0.001; // 100 m
    private double scalaLatitudine = 0.002; // 200 m
    Paint paint = new Paint();
    private Track track;

    public DrawView(Context context, Track t, double latitude) {
        super(context);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(4);
        track = t;
        currentLatitude = latitude;
    }

    @Override
    public void onDraw(Canvas canvas) {
        Display mdisp = getDisplay();
        Point mdispSize = new Point();
        mdisp.getSize(mdispSize);
        int maxX = mdispSize.x;
        int maxY = mdispSize.y;
        int xC = maxX/2;
        int yC = maxY/2;
        int casellaX = maxX/10;
        int casellaY = maxY/20;
        int x = xC;
        int y = yC;
        Iterator<TrackPoint> iterator = track.getTrackPoints().iterator();
        TrackPoint p1 = iterator.next();
        TrackPoint p2 = iterator.next();
        double distanzaY = Math.abs(p1.getLatitude() - p2.getLatitude());
        if (distanzaY >= dimensioniCasella) {
            int incremento = (int) (distanzaY * 10000);
            int y1 = incremento * casellaY;
            if (p2.getLatitude() > p1.getLatitude()) {
                //decrementa la Y
                y = y - y1;
            }
            else {
                //incrementa la Y
                y = y + y1;
            }
        }
        double distanzaX = Math.abs(p1.getLongitude() - p2.getLongitude());
        if (distanzaX >= dimensioniCasella) {
            int incremento = (int) (distanzaX * 10000);
            int x1 = incremento * casellaX;
            if (p2.getLongitude() > p1.getLongitude())
                x = x + x1;
            else
                x = x - x1;
        }
        canvas.drawLine(xC, yC, x, y, paint);
    }

    /* // gradi di longitudine dell'asse X
    private double getSizeX(double latitude) {
        double distancePerLongitude = (Math.PI / 180) * radius * Math.cos(latitude);
        return sizeXMetres / distancePerLongitude;
    }

    private int getX(int x, int maxX, TrackPoint p1, TrackPoint p2) {
        double distanceX = p1.getLongitude() - p2.getLongitude();
        if (Math.abs(distanceX) >= degreesX()) {
            int newX = pixelX(maxX) * (int) (distanceX/ degreesX());
            if (p1.getLongitude() > p2.getLongitude())
                x -= newX;
            else
                x += newX;
        }
        return x;
    }

    private int getY(int y, int maxY, TrackPoint p1, TrackPoint p2) {
        double distanceY = p1.getLatitude() - p2.getLatitude();
        if (Math.abs(distanceY) >= degreesY()) {
            int newY = pixelY(maxY) * (int) (distanceY/ degreesY());
            if (p1.getLongitude() > p2.getLongitude())
                y += newY;
            else
                y -= newY;
        }
        return y;
    }

    //metri di distanza corrispondenti alla differenza di due longitudini
    private double longToMetres(double longitude) {
        return (sizeXMetres * longitude) / sizeX;
    }

    //metri di distanza corrispondenti alla differenza di due latitudini
    private double latToMetres(double latitude) {
        return (sizeYMetres * latitude) / sizeY;
    }

    //pixel di una casella dell'asse X
    private int pixelX(int width) {
        return width/10;
    }

    //pixel di una casella dell'asse Y
    private int pixelY(int height) {
        return height/20;
    }


    private double getDistance(TrackPoint p1, TrackPoint p2) {
        double lat1 = Math.toRadians(p1.getLatitude());
        double lat2 = Math.toRadians(p2.getLatitude());
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLong = Math.toRadians(p2.getLongitude() - p1.getLongitude());

        double a = (Math.sin(deltaLat/2) * Math.sin(deltaLat/2)) +
                (Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLong/2) * Math.sin(deltaLong/2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return radius * c;
    }

    private double getScale() {
        double distance = 0;
        Iterator<TrackPoint> it = track.getTrackPoints().iterator();
        TrackPoint p1 = it.next();
        TrackPoint p2 = null;
        while (it.hasNext()) {
            p2 = it.next();
        }
        return getDistance(p1, p2);
    }
    */

}
