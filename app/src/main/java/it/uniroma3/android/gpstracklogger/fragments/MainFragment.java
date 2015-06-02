package it.uniroma3.android.gpstracklogger.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import it.uniroma3.android.gpstracklogger.R;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.events.Events;
import it.uniroma3.android.gpstracklogger.model.Track;

/**
 * Created by Fabio on 02/06/2015.
 */
public class MainFragment extends Fragment {
    private Button start,stop;
    RelativeLayout rlayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rlayout = (RelativeLayout) inflater.inflate(R.layout.fragment_main, container, false);
        loadButtons();
        return rlayout;
    }

    public void startClick() {
        if (!Session.isStarted()) {
            start.setEnabled(false);
            stop.setEnabled(true);
            setTextViewValue(R.id.notice, "Tracking...");
            startLogging();
            Session.getController().scheduleWriting();
        }
    }

    public void stopClick() {
        if (Session.isStarted()) {
            if (!Session.getController().getCurrentTrack().isEmpty())
                showTripInfo();
            start.setEnabled(true);
            stop.setEnabled(false);
            Session.setReturning(false);
            Session.getController().stopWriting();
            stopLogging();
        }
    }

    private void showTripInfo() {
        Track current = Session.getController().getCurrentTrack();
        new AlertDialog.Builder(getActivity())
                .setTitle("Trip Info")
                .setMessage("Distanza percorsa: "+current.getStringTotalDistance()+"\n"+
                        "Tempo trascorso: "+current.getStringTime())
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void startLogging() {
        EventBus.getDefault().post(new Events.Start());
    }

    private void stopLogging() {
        EventBus.getDefault().post(new Events.Stop());
    }

    private void setTextViewValue(int textViewId, String value) {
        TextView textView = (TextView) rlayout.findViewById(textViewId);
        textView.setText(value);
    }

    private void loadButtons() {
        start = (Button) rlayout.findViewById(R.id.startButton);
        stop = (Button) rlayout.findViewById(R.id.stopButton);
        Button showTracks = (Button) rlayout.findViewById(R.id.unloadtrack);
        start.setEnabled(!Session.isStarted());
        stop.setEnabled(Session.isStarted());
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startClick();
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopClick();
            }
        });
        showTracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackListClick();
            }
        });
    }

    public void trackListClick() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ImportedFileFragment())
                .commit();
    }

}
