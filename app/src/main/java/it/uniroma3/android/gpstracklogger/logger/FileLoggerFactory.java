package it.uniroma3.android.gpstracklogger.logger;

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

    public static GpxFileLogger getLogger(Track track) {
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
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

    public static void write(Track track) {
        GpxFileLogger logger = getLogger(track);
        logger.write();
    }
}
