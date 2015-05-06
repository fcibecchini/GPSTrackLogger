package it.uniroma3.android.gpstracklogger.events;

import android.location.Location;

/**
 * Created by Fabio on 04/05/2015.
 */
public class Events {

    public static class Stop {

    }

    public static class LocationUpdate {
        public Location location;
        public LocationUpdate(Location loc) {
            this.location = loc;
        }
    }

    public static class Message {
        public int id;
        public String info;
        public Message(int id, String info){
            this.id = id;
            this.info = info;
        }

    }

    public static class Directory {
        public String directory;
        public Directory (String dir) {
            this.directory = dir;
        }
    }


}
