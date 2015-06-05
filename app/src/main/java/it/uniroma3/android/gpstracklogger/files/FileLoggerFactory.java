package it.uniroma3.android.gpstracklogger.files;

import java.io.File;

import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.model.Track;

/**
 * Created by Fabio on 04/05/2015.
 */
public class FileLoggerFactory {
    private static int countFile = 0;
    private static String directory = AppSettings.getDirectory();

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
        return logger;
    }

    public static GPXFileLoader getLoader(String gpxFileName) {
        File gpx = new File(directory, gpxFileName);
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
