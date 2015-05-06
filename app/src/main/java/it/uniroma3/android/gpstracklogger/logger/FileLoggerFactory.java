package it.uniroma3.android.gpstracklogger.logger;

import android.content.Context;
import android.location.Location;

import java.io.File;

import it.uniroma3.android.gpstracklogger.model.Track;

/**
 * Created by Fabio on 04/05/2015.
 */
public class FileLoggerFactory {

    public static GpxFileLogger getLogger(Context context, Track track) {
        File gpxFileFolder = new File(context.getFilesDir().toString());
        if (!gpxFileFolder.exists()) {
            gpxFileFolder.mkdirs();
        }
        File gpxFile = new File(gpxFileFolder, track.getName().replace(":","-") + ".gpx");
        GpxFileLogger logger = new GpxFileLogger(gpxFile, track);
        return logger;



    }


    public static void write(Context context, Track track) {
        GpxFileLogger logger = getLogger(context, track);
        logger.write();
    }

}
