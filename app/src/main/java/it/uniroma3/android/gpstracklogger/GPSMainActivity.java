package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.events.Events;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.service.GPSLoggingService;

public class GPSMainActivity extends Activity {
    private Intent serviceIntent;
    private Button start,stop,locDetails,ret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsmain);
        setDefaultSettings();
        loadSettings();
        RegisterEventBus();
        startService();
        loadButtons();
    }

    private void RegisterEventBus() {
        EventBus.getDefault().register(this);
    }

    private void UnregisterEventBus(){
        try {
            EventBus.getDefault().unregister(this);
        } catch (Throwable t){
            //this may crash if registration did not go through. just be safe
        }
    }

    private void setDefaultSettings() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void loadSettings() {
        AppSettings.loadSettings(this);
    }

    public void startClick() {
        if (!Session.isStarted()) {
            stop.setEnabled(true);
            locDetails.setEnabled(true);
            ret.setEnabled(true);
            start.setEnabled(false);
            setTextViewValue(R.id.notice, "Tracking...");
            startLogging();
            Session.getController().scheduleWriting();
        }
    }

    public void stopClick() {
        if (Session.isStarted()) {
            if (!Session.getController().getCurrentTrack().isEmpty())
                showTripInfo();
            stop.setEnabled(false);
            locDetails.setEnabled(false);
            ret.setEnabled(false);
            start.setEnabled(true);
            Session.getController().stopWriting();
            stopLogging();
        }
    }

    public void drawClick() {
        Intent draw = new Intent(this, StartDrawActivity.class);
        startActivity(draw);
    }

    public void loadClick() {
        Intent load = new Intent(this, LoadFileActivity.class);
        startActivity(load);
    }

    public void unloadClick() {
        Intent unload = new Intent(this, ImportedFileActivity.class);
        startActivity(unload);
    }

    public void prefClick() {
        Intent set = new Intent(this, SettingsActivity.class);
        startActivity(set);
    }

    public void detailsClick() {
        Intent det = new Intent(this, LocDetailsActivity.class);
        startActivity(det);
    }

    public void returnClick() {
        if (Session.getController().setReturn()) {
            ret.setEnabled(false);
            showTripInfo();
        }
    }

    public void compassClick() {
        if (!Session.isCompass())
            Session.setCompass(true);
        else
            Session.setCompass(false);
    }

    private void showTripInfo() {
        Track current = Session.getController().getCurrentTrack();
        new AlertDialog.Builder(this)
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

    public void onEventMainThread(Events.Directory dir) {
        setTextViewValue(R.id.notice, "Saved in:" + dir.directory);
    }

    private void setTextViewValue(int textViewId, String value) {
        TextView textView = (TextView) findViewById(textViewId);
        textView.setText(value);
    }

    private void startService() {
        serviceIntent = new Intent(this, GPSLoggingService.class);
        startService(serviceIntent);
    }

    private void stopService() {
        try {
            stopService(serviceIntent);
        } catch (Exception e) {
            setTextViewValue(R.id.notice, "could not stop service");
        }
    }

    private void loadButtons() {
        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);
        Button draw = (Button) findViewById(R.id.draw);
        Button load = (Button) findViewById(R.id.loadtrack);
        Button unload = (Button) findViewById(R.id.unloadtrack);
        Button prefs = (Button) findViewById(R.id.prefs);
        locDetails = (Button) findViewById(R.id.locdetails);
        ret = (Button) findViewById(R.id.return1);
        Button compass = (Button) findViewById(R.id.compass);
        if (!Session.isStarted()) {
            stop.setEnabled(false);
            locDetails.setEnabled(false);
            ret.setEnabled(false);
        }
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
        locDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailsClick();
            }
        });
        ret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnClick();
            }
        });
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawClick();
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadClick();
            }
        });
        unload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unloadClick();
            }
        });
        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compassClick();
            }
        });
        prefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefClick();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_gpsmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService();
        UnregisterEventBus();
        super.onDestroy();
    }
}
