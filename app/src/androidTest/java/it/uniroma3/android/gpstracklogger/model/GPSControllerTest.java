package it.uniroma3.android.gpstracklogger.model;

import android.location.Location;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import java.io.File;

import it.uniroma3.android.gpstracklogger.logger.GpxFileLogger;

/**
 * Created by Fabio on 05/05/2015.
 */
public class GPSControllerTest extends AndroidTestCase {
    private GPSController controller = new GPSController();

    public void setUp() throws Exception {

    }

    @SmallTest
    public void testAddTrackPoint() {
        Location location = new Location("TEST");
        location.setLatitude(50.033);
        location.setLongitude(20.322);
        location.setAltitude(90);
        assertTrue(controller.addTrackPoint(location));
        int pointsNumber = controller.getCurrentTrack().getTrackPoints().size();
        assertEquals(1, pointsNumber);
    }

}
