package it.uniroma3.android.gpstracklogger;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
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
import it.uniroma3.android.gpstracklogger.service.GPSLoggingService;
import java.util.Date;

public class GPSMainActivity extends Activity {
    private Intent serviceIntent;
    private Button start,stop,draw,load,prefs,locDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsmain);
        setDefaultSettings();
        loadSettings();
        RegisterEventBus();
        startService();
        start = (Button) findViewById(R.id.startButton);
        stop = (Button) findViewById(R.id.stopButton);
        draw = (Button) findViewById(R.id.draw);
        load = (Button) findViewById(R.id.loadtrack);
        prefs = (Button) findViewById(R.id.prefs);
        locDetails = (Button) findViewById(R.id.locdetails);
        if (!Session.isStarted()) {
            stop.setEnabled(false);
            locDetails.setEnabled(false);
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
        prefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prefClick();
            }
        });

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
            start.setEnabled(false);
            setTextViewValue(R.id.notice, "Tracking...");
            startLogging();
            Session.getController().scheduleWriting();
        }
    }

    public void stopClick() {
        if (Session.isStarted()) {
            stop.setEnabled(false);
            locDetails.setEnabled(false);
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

    public void prefClick() {
        Intent set = new Intent(this, SettingsActivity.class);
        startActivity(set);
    }

    public void detailsClick() {
        Intent det = new Intent(this, LocDetailsActivity.class);
        startActivity(det);
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
