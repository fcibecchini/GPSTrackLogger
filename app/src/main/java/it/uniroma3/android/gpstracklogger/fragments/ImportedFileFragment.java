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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.uniroma3.android.gpstracklogger.R;
import it.uniroma3.android.gpstracklogger.adapters.AdapterTrack;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;


public class ImportedFileFragment extends Fragment {
    private List<Track> tracks;
    ListView listView;
    RelativeLayout rlayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rlayout = (RelativeLayout) inflater.inflate(R.layout.fragment_file_imported, container, false);
        listView = (ListView) rlayout.findViewById(R.id.list);
        loadImportedTracks();
        final AdapterTrack adapter = new AdapterTrack(getActivity(), 0, tracks);
        listView.setAdapter(adapter);
        return rlayout;
    }

    private void loadImportedTracks() {
        tracks = new ArrayList<>();
        tracks.addAll(Session.getController().getImportedTracks());
        if (tracks.isEmpty()) {
            Toast.makeText(getActivity().getApplicationContext(), "Menu -> \"Carica tracce\" per caricare le tracce registrate", Toast.LENGTH_LONG).show();
        }
    }

}
