/*******************************************************************************
 * This file is part of GPSTrackLogger.
 * Copyright (C) 2015  Fabio Cibecchini
 *
 * GPSTrackLogger is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GPSTrackLogger is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with GPSTrackLogger.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

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

import it.uniroma3.android.gpstracklogger.R;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.application.Utilities;
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
            TextView notice = (TextView) rlayout.findViewById(R.id.notice);
            notice.setText("Tracking...");
            Session.getController().startLogging();
        }
    }

    public void stopClick() {
        if (Session.isStarted()) {
            if (!Session.getController().isCurrentTrackEmpty())
                showTripInfo();
            start.setEnabled(true);
            stop.setEnabled(false);
            Session.setReturning(false);
            Session.getController().stopLogging();
        }
    }

    private void showTripInfo() {
        Track current = Session.getController().getCurrentTrack();
        new AlertDialog.Builder(getActivity())
                .setTitle("Trip Info")
                .setMessage("Distanza percorsa: "+ Utilities.getFormattedDistance(current.getTotalDistance(), true)+"\n"+
                        "Tempo trascorso: "+Utilities.getFormattedTime(current.getTotalTime(), true))
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
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
                .addToBackStack("")
                .commit();
    }

}
