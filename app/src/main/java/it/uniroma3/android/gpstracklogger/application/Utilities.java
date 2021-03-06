/*******************************************************************************
 * This file is part of GPSTrackLogger.
 * Copyright (C) 2015  Fabio Cibecchini
 *
 * GPSTrackLogger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GPSTrackLogger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSTrackLogger.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package it.uniroma3.android.gpstracklogger.application;

import android.graphics.Color;

import java.text.NumberFormat;
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
public class Utilities {
    private static Map<String, Integer> colors;

    public static Map<String, Integer> getColors() {
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
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'",
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

    public static String formatSpeed(float speed) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        return nf.format(speed*3.6)+"km/h";
    }

    public static String formatAltitude(double altitude) {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(1);
        return nf.format(altitude)+"m";
    }

}
