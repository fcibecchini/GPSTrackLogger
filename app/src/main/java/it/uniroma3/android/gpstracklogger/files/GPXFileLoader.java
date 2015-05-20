package it.uniroma3.android.gpstracklogger.files;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;
import it.uniroma3.android.gpstracklogger.model.TrackPointComparator;

/**
 * Created by Fabio on 19/05/2015.
 */
public class GPXFileLoader {
    private File gpxFile;
    private static final String ns = null;

    public GPXFileLoader(File gpx) {
        this.gpxFile = gpx;
    }

    public void loadGpxFile() {
        InputStream in = null;
        try {
            FileInputStream fileStream = new FileInputStream(gpxFile);
            in = new BufferedInputStream(fileStream);
            parse(in);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (XmlPullParserException x) {
            x.printStackTrace();
        }
        catch (ParseException p) {
            p.printStackTrace();
        }
    }

    public void parse(InputStream in) throws XmlPullParserException, IOException, ParseException{
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            readGpx(parser);
        } finally {
            in.close();
        }
    }

    private void readGpx(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        Set<TrackPoint> trackPoints = null;

        parser.require(XmlPullParser.START_TAG, ns, "gpx");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the track tag
            if (name.equals("trk")) {
                trackPoints = readTrack(parser);
                Track track = new Track(gpxFile.getName());
                track.setTrackPoints(trackPoints);
                Session.getController().addTrack(track);
            }
            //or for the waypoint tag
            else if (name.equals("wpt")) {
                TrackPoint waypoint = readWayPoint(parser);
                Session.getController().addWayPoint(waypoint);
            }
            else {
                skip(parser);
            }
        }
    }

    private Set<TrackPoint> readTrack(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        Set<TrackPoint> trackPoints = null;

        parser.require(XmlPullParser.START_TAG, ns, "trk");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the track tag
            if (name.equals("trkseg")) {
                trackPoints = readTrackSegment(parser);
            } else {
                skip(parser);
            }
        }
        return trackPoints;
    }

    private Set<TrackPoint> readTrackSegment(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        Set<TrackPoint> trackPoints = new TreeSet<>(new TrackPointComparator());

        parser.require(XmlPullParser.START_TAG, ns, "trkseg");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the track tag
            if (name.equals("trkpt")) {
                trackPoints.add(readPoint(parser));
            } else {
                skip(parser);
            }
        }
        return trackPoints;
    }

    private TrackPoint readWayPoint(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "wpt");
        TrackPoint tp = new TrackPoint();
        double latitude;
        double longitude;
        double altitude;
        float speed;
        Date time;
        String wptName;

        String tag = parser.getName();
        String latType = parser.getAttributeValue(null, "lat");
        String lonType = parser.getAttributeValue(null, "lon");
        if (tag.equals("wpt")) {
            latitude = Double.valueOf(latType);
            longitude = Double.valueOf(lonType);
            tp.setLatitude(latitude);
            tp.setLongitude(longitude);
        }

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("ele")) {
                altitude = readAltitude(parser);
                tp.setAltitude(altitude);
            } else if (name.equals("speed")) {
                speed = readSpeed(parser);
                tp.setSpeed(speed);
            } else if (name.equals("time")) {
                time = readTime(parser);
                tp.setTime(time);
            } else if (name.equals("name")) {
                wptName = readName(parser);
                tp.setName(wptName);
            } else {
                skip(parser);
            }
        }

        return tp;
    }

    private TrackPoint readPoint(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "trkpt");
        TrackPoint tp = new TrackPoint();
        double latitude;
        double longitude;
        double altitude;
        float speed;
        Date time;

        String tag = parser.getName();
        String latType = parser.getAttributeValue(null, "lat");
        String lonType = parser.getAttributeValue(null, "lon");
        if (tag.equals("trkpt")) {
            latitude = Double.valueOf(latType);
            longitude = Double.valueOf(lonType);
            tp.setLatitude(latitude);
            tp.setLongitude(longitude);
        }


        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("ele")) {
                altitude = readAltitude(parser);
                tp.setAltitude(altitude);
            } else if (name.equals("speed")) {
                speed = readSpeed(parser);
                tp.setSpeed(speed);
            } else if (name.equals("time")) {
                time = readTime(parser);
                tp.setTime(time);
            } else {
                skip(parser);
            }
        }

        return tp;
    }

    private double readAltitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ele");
        String alt = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "ele");
        return Double.valueOf(alt);
    }

    private Date readTime(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException {
        parser.require(XmlPullParser.START_TAG, ns, "time");
        String time = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "time");
        return getDateTime(time);
    }

    private float readSpeed(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "speed");
        String speed = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "speed");
        return Float.valueOf(speed);
    }

    private String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private Date getDateTime(String time) throws ParseException {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'kk:mm:ss'Z'", Locale.ENGLISH);
        Date date = format.parse(time);
        return date;
    }


}
