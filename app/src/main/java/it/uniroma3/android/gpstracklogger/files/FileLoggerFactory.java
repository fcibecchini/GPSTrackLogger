package it.uniroma3.android.gpstracklogger.files;

import android.os.Environment;

import java.io.File;

import de.greenrobot.event.EventBus;
import it.uniroma3.android.gpstracklogger.events.Events;
import it.uniroma3.android.gpstracklogger.model.Track;

/**
 * Created by Fabio on 04/05/2015.
 */
public class FileLoggerFactory {
    private static int countFile = 0;
    private static String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();

    public static GpxFileLogger getLogger(Track track) {
        File gpxFileFolder = new File(directory);
        if (!gpxFileFolder.exists()) {
            gpxFileFolder.mkdirs();
        }
        String fileName = track.getName().replace(":","-");
        File gpxFile = new File(gpxFileFolder, fileName + ".gpx");
        if (gpxFile.exists()) {
            countFile++;
            String rename = "(" + String.valueOf(countFile) + ")";
            gpxFile = new File(gpxFileFolder, fileName + rename + ".gpx");
        }
        else {
            countFile = 0;
        }
        GpxFileLogger logger = new GpxFileLogger(gpxFile, track);
        EventBus.getDefault().post(new Events.Directory(directory));
        return logger;
    }

    public static GPXFileLoader getLoader(String gpxFileName) {
        File gpx = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString(), gpxFileName);
        return new GPXFileLoader(gpx);
    }

    public static void loadGpxFile(String gpxFileName) {
        GPXFileLoader loader = getLoader(gpxFileName);
        loader.loadGpxFile();
    }

    public static void write(Track track) {
        GpxFileLogger logger = getLogger(track);
        logger.write();
    }
}
