package it.uniroma3.android.gpstracklogger.application;

import android.app.Application;
import android.graphics.Color;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by Fabio on 29/05/2015.
 */
public class Utilities extends Application {
    private static Map<String, Integer> colors;

    public static Map getColors() {
        if (colors == null) {
            colors = new HashMap<>();
            colors.put("current0A", Color.BLACK);
            colors.put("current0B", Color.GRAY);

            colors.put("loaded1A", Color.BLUE); //blu
            colors.put("loaded1B", Color.rgb(51, 204, 255)); //azzurro

            colors.put("loaded2A", Color.RED); //rosso
            colors.put("loaded2B", Color.rgb(255, 128, 0)); //arancione

            colors.put("loaded3A", Color.rgb(255, 105, 180)); //rosa
            colors.put("loaded3B", Color.rgb(143, 0, 255)); //viola

            colors.put("loaded4A", Color.rgb(0, 165, 80)); //verde scuro
            colors.put("loaded4B", Color.rgb(0, 153, 0)); //verde chiaro

            colors.put("loaded5A", Color.rgb(150, 75, 0)); //marrone
            colors.put("loaded5B", Color.rgb(245, 245, 220)); //beige

            colors.put("loaded6A", Color.rgb(207, 181, 59)); //oro
            colors.put("loaded6B", Color.rgb(255, 216, 0)); //giallo

            colors.put("waypoint", Color.RED);
        }
        return colors;
    }

    public static String getISODateTime(Date dateToFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'KK:mm:ss'Z'",
                Locale.ITALIAN);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(dateToFormat);
    }

    public static String getFormattedTime(long seconds, boolean precisione) {
        String format;
        long hours = TimeUnit.SECONDS.toHours(seconds);
        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        if (precisione)
           format = String.format("%02dh:%02dm:%02ds",
                   hours,
                   minutes - TimeUnit.HOURS.toMinutes(hours),
                   seconds - TimeUnit.MINUTES.toSeconds(minutes));
        else
            format = String.format("%02d:%02d",
                    hours,
                    minutes - TimeUnit.HOURS.toMinutes(hours));

        return format;
    }

    public static String getFormattedDistance(int distance, boolean decimal) {
        if (distance >= 1000) {
            if (decimal)
                return ((double)distance / 1000) + "km";
            return (distance / 1000) + "km";
        } else {
            return distance + "m";
        }
    }

    public static String parseDecimalDegrees(double decimalDegrees) {
        int degrees = (int) decimalDegrees;
        double minutes = (decimalDegrees % 1) * 60;
        double seconds = (minutes % 1) * 60;
        String sDegrees = String.valueOf(degrees)+"° ";
        String sMinutes = String.valueOf((int)minutes)+"' ";
        String sSeconds = String.valueOf((int)seconds)+"'' ";
        return sDegrees + sMinutes + sSeconds;
    }

    public static String parseLatitude(double latitude) {
        String lat = parseDecimalDegrees(Math.abs(latitude));
        if (latitude>0)
            lat+="N";
        else
            lat+="S";
        return lat;
    }

    public static String parseLongitude(double longitude) {
        String lon = parseDecimalDegrees(Math.abs(longitude));
        if (longitude>0)
            lon+="E";
        else
            lon+="W";
        return lon;
    }

}
