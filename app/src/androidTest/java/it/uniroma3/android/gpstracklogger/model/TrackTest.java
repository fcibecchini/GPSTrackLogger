package it.uniroma3.android.gpstracklogger.model;

import android.location.Location;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Created by Fabio on 05/05/2015.
 */
public class TrackTest extends AndroidTestCase {
    Track track;

    public void setUp() throws Exception {
        track = new Track();
    }

    @SmallTest
    public void testAddTrackPoint() {
        assertEquals(0, track.getTrackPoints().size());
        Location location = new Location("TEST");
        location.setLatitude(50.033);
        location.setLongitude(20.322);
        location.setAltitude(90);
        assertTrue(track.addTrackPoint(location));
        assertEquals(1, track.getTrackPoints().size());
        Location loc2 = new Location("TEST");
        loc2.setLatitude(54.033);
        loc2.setLongitude(30.322);
        loc2.setAltitude(94);
        assertTrue(track.addTrackPoint(loc2));
        assertEquals(2, track.getTrackPoints().size());
    }
}
