package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.uniroma3.android.gpstracklogger.adapters.AdapterTrack;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;


public class ImportedFileActivity extends Activity {
    private List<Track> tracks;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_imported);
        listView = (ListView) findViewById(R.id.list);
        loadImportedTracks();
        final AdapterTrack adapter = new AdapterTrack(this, 0, tracks);
        listView.setAdapter(adapter);
    }

    private void loadImportedTracks() {
        tracks = new ArrayList<>();
        tracks.addAll(Session.getController().getImportedTracks());
        if (tracks.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No data loaded...", Toast.LENGTH_SHORT).show();
        }
    }

}
