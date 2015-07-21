package it.uniroma3.android.gpstracklogger.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.uniroma3.android.gpstracklogger.GraphActivity;
import it.uniroma3.android.gpstracklogger.R;
import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.application.Utilities;
import it.uniroma3.android.gpstracklogger.model.Track;

/**
 * Created by Fabio on 31/05/2015.
 */
public class AdapterTrack extends ArrayAdapter<Track> {
    private Activity activity;
    private List<Track> lTrack;
    private static LayoutInflater inflater = null;
    private String speed;

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
        public ImageButton unloadtrack;
        public Button errorPoints;
        public ImageButton share;
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
                holder.unloadtrack = (ImageButton) vi.findViewById(R.id.unloadtrack);
                holder.errorPoints = (Button) vi.findViewById(R.id.errorpoints);
                holder.share = (ImageButton) vi.findViewById(R.id.share);

                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }
            holder.trackname.setText(lTrack.get(position).getName().replace(".gpx", ""));
            holder.trackdistance.setText(Utilities.getFormattedDistance(lTrack.get(position).getTotalDistance(), true));
            String time = Utilities.getFormattedTime(lTrack.get(position).getTotalTime(), true);
            String formatted = "("+time+")";
            holder.tracktime.setText(formatted);
            holder.trackelevationchange.setText("Total Climb: "+Utilities.getFormattedDistance(lTrack.get(position).totalClimb(), false));

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

            holder.errorPoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeErrors(position);
                }
            });

            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendMail(position);
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

    private void sendMail(int position) {
        Track track = Session.getController().getImportedTracks().get(position);
        final Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("*/*");
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Traccia: " + track.getName());

        ArrayList<Uri> chosenFile = new ArrayList<>();
        String path = track.getPath();
        File file = new File(path);
        Uri uri = null;
        if (path.contains(AppSettings.getDirectory()))
            uri = FileProvider.getUriForFile(activity, "it.uniroma3.android.gpstracklogger.GPSMainActivity", file);
        else
            uri = Uri.fromFile(file);

        chosenFile.add(uri);
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, chosenFile);
        activity.startActivity(Intent.createChooser(intent, "Condividi via..."));
    }

    private void removeErrors(int position) {
        final int pos = position;
        final EditText inputSpeed = new EditText(activity);
        inputSpeed.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(activity)
            .setTitle("Speed Limit (km/h)")
            .setView(inputSpeed)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String speed = inputSpeed.getText().toString();
                    if (speed != null && !speed.equals("")) {
                        Session.getController().removeErrorSpeed(pos, Float.valueOf(speed));
                        Toast.makeText(activity, "Traccia salvata", Toast.LENGTH_SHORT).show();
                    }
                    dialog.cancel();
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .show();
    }

}
