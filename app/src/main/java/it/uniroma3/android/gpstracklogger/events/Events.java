package it.uniroma3.android.gpstracklogger.events;

import android.location.Location;

/**
 * Created by Fabio on 04/05/2015.
 */
public class Events {

    public static class Start {}

    public static class Stop {}

    public static class Directory {
        public String directory;
        public Directory (String dir) {
            this.directory = dir;
        }
    }


}
