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
            Toast.makeText(getActivity().getApplicationContext(), "No data loaded...", Toast.LENGTH_SHORT).show();
        }
    }

}
