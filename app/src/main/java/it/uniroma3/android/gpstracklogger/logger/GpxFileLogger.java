package it.uniroma3.android.gpstracklogger.logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * Created by Fabio on 02/05/2015.
 */
public class GpxFileLogger {
    private File gpxFile = null;
    private Track track;

    public GpxFileLogger(File gpx, Track t) {
        this.gpxFile = gpx;
        this.track = t;
    }

    public void write() {
        try {
            if (!gpxFile.exists())
                gpxFile.createNewFile();
            FileOutputStream initialWriter = new FileOutputStream(gpxFile, true);
            BufferedOutputStream output = new BufferedOutputStream(initialWriter);

            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            xml.append("<gpx version=\"1.1\" creator=\"GPS Track Logger\" ");
            xml.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
            xml.append("xmlns=\"http://www.topografix.com/GPX/1/1\" ");
            xml.append("xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 ");
            xml.append("http://www.topografix.com/GPX/1/1/gpx.xsd\">");
            xml.append("<trk>");
            xml.append("<name>").append("<![CDATA[").append(track.getName()).append("]]>").append("</name>");
            xml.append("<trkseg>");
            xml.append(getXMLTrackSegment());
            xml.append("</trkseg></trk>").append("</gpx>");
            output.write(xml.toString().getBytes());
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getXMLTrackSegment() {
        StringBuilder trackSegment = new StringBuilder();
        for (TrackPoint point : track.getTrackPoints()) {
            trackSegment.append("<trkpt lat=\"")
                    .append(String.valueOf(point.getLatitude()))
                    .append("\" lon=\"")
                    .append(String.valueOf(point.getLongitude()))
                    .append("\">");
            if (point.hasAltitude())
                trackSegment.append("<ele>").append(String.valueOf(point.getAltitude())).append("</ele>");
            trackSegment.append("<time>").append(getISODateTime(point.getTime())).append("</time>");
            trackSegment.append("</trkpt>");
        }

        return trackSegment.toString();
    }

    private String getISODateTime(Date dateToFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'KK:mm:ss'Z'",
                Locale.ITALIAN);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(dateToFormat);
    }
}
