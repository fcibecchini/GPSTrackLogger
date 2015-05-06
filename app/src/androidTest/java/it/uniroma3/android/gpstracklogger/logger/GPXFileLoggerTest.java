package it.uniroma3.android.gpstracklogger.logger;

import android.location.Location;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.io.File;

import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.model.TrackPoint;

/**
 * Created by Fabio on 05/05/2015.
 */
public class GPXFileLoggerTest extends AndroidTestCase {
    private Track track;
    private File file;
    private GpxFileLogger fileLogger;


    public void setUp() throws Exception {
        track = new Track();
        file = new File("test");
        fileLogger = new GpxFileLogger(file, track);

    }

    @SmallTest
    public void testGetXMLTrackSegmentNoPoints() {
        String actual = fileLogger.getXMLTrackSegment();
        String expected = "";
        assertEquals(expected, actual);
    }

    @SmallTest
         public void testGetXMLTrackSegmentSinglePoint() {
        Location location = new Location("TEST");
        location.setLatitude(50.033);
        location.setLongitude(20.322);
        location.setAltitude(90);
        assertTrue(track.addTrackPoint(location));
        TrackPoint point = track.getTrackPoints().get(0);
        point.setTime("2015-05-05T00:00:01Z");
        String actual = fileLogger.getXMLTrackSegment();
        String expected = "<trkpt lat=\"50.033\" lon=\"20.322\"><ele>90.0</ele><time>2015-05-05T00:00:01Z</time></trkpt>";
        assertEquals(expected, actual);
    }

    @SmallTest
    public void testGetXMLTrackSegmentMultiplePoints() {
        Location loc1 = new Location("TEST");
        loc1.setLatitude(50.033);
        loc1.setLongitude(20.322);
        loc1.setAltitude(90);
        Location loc2 = new Location("TEST");
        loc2.setLatitude(52.033);
        loc2.setLongitude(22.322);
        loc2.setAltitude(92);
        assertTrue(track.addTrackPoint(loc1));
        assertTrue(track.addTrackPoint(loc2));
        TrackPoint point1 = track.getTrackPoints().get(0);
        point1.setTime("2015-05-05T00:01:00Z");
        TrackPoint point2 = track.getTrackPoints().get(1);
        point2.setTime("2015-05-05T00:02:00Z");
        String actual = fileLogger.getXMLTrackSegment();
        String expected = "<trkpt lat=\"50.033\" lon=\"20.322\"><ele>90.0</ele><time>2015-05-05T00:01:00Z</time></trkpt>" +
                "<trkpt lat=\"52.033\" lon=\"22.322\"><ele>92.0</ele><time>2015-05-05T00:02:00Z</time></trkpt>";
        assertEquals(expected, actual);
    }



}
