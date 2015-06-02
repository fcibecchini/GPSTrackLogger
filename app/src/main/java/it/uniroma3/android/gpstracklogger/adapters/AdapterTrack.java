package it.uniroma3.android.gpstracklogger.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import it.uniroma3.android.gpstracklogger.GraphActivity;
import it.uniroma3.android.gpstracklogger.R;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;

/**
 * Created by Fabio on 31/05/2015.
 */
public class AdapterTrack extends ArrayAdapter<Track> {
    private Activity activity;
    private List<Track> lTrack;
    private static LayoutInflater inflater = null;

    public AdapterTrack(Activity activity, int textViewResourceId,  List<Track> tracks) {
        super(activity, textViewResourceId, tracks);
        try {
            this.activity = activity;
            this.lTrack = tracks;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        } catch (Exception e) {

        }
    }

    public int getCount() {
        return lTrack.size();
    }

    public Track getItem(Track position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public static class ViewHolder {
        public TextView trackname;
        public TextView trackdistance;
        public TextView tracktime;
        public TextView trackelevationchange;
        public Button showchart;
        public Button unloadtrack;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.trackview, null);
                holder = new ViewHolder();

                holder.trackname = (TextView) vi.findViewById(R.id.trackname);
                holder.trackdistance = (TextView) vi.findViewById(R.id.trackdistance);
                holder.tracktime = (TextView) vi.findViewById(R.id.tracktime);
                holder.trackelevationchange = (TextView) vi.findViewById(R.id.trackelevationchange);
                holder.showchart = (Button) vi.findViewById(R.id.showchart);
                holder.unloadtrack = (Button) vi.findViewById(R.id.unloadtrack);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            holder.trackname.setText(lTrack.get(position).getName().replace(".gpx", ""));
            holder.trackdistance.setText(lTrack.get(position).getStringTotalDistance());
            String time = lTrack.get(position).getStringTime();
            String formatted = "("+time.substring(0,2)+"h "+time.substring(3, 5)+"m)";
            holder.tracktime.setText(formatted);
            holder.trackelevationchange.setText("Total Climb: "+lTrack.get(position).stringTotalClimb());

            holder.unloadtrack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    unloadTrack(position);
                }
            });

            holder.showchart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    drawGraph(position);
                }
            });

        } catch (Exception e) {


        }
        return vi;
    }

    private void drawGraph(int position) {
        Intent intent = new Intent(activity.getApplicationContext(), GraphActivity.class);
        intent.putExtra("TrackPosition", position);
        activity.startActivity(intent);
    }

    private void unloadTrack(int position) {
        Session.getController().removeImportedTrack(lTrack.get(position).getName());
        lTrack.remove(position);
        notifyDataSetChanged();
        checkStatus();
    }

    private void checkStatus() {
        if (Session.getController().getCurrentTrack() == null &&
                Session.getController().getImportedTracks().isEmpty()) {
            Session.setConverterSet(false);
            Session.getController().removeWayPoints();
        }
    }

}
