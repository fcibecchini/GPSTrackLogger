package it.uniroma3.android.gpstracklogger;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import it.uniroma3.android.gpstracklogger.application.AppSettings;
import it.uniroma3.android.gpstracklogger.application.Session;
import it.uniroma3.android.gpstracklogger.application.Utilities;
import it.uniroma3.android.gpstracklogger.fragments.LoadFileFragment;
import it.uniroma3.android.gpstracklogger.fragments.LocDetailsFragment;
import it.uniroma3.android.gpstracklogger.fragments.MainFragment;
import it.uniroma3.android.gpstracklogger.model.Track;
import it.uniroma3.android.gpstracklogger.service.GPSLoggingService;

public class GPSMainActivity extends AppCompatActivity {
    private Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gpsmain);
        setDefaultSettings();
        loadSettings();
        startService();
        loadFragment();
        Session.getController().registerEventBus();
    }

    private void loadFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        MainFragment fragment = new MainFragment();
        fragmentTransaction.add(R.id.container, fragment);
        fragmentTransaction.commit();
    }

    private void setDefaultSettings() {
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    private void loadSettings() {
        AppSettings.loadSettings(this);
    }

    public void drawClick() {
        Intent draw = new Intent(this, StartDrawActivity.class);
        startActivity(draw);
    }

    public void loadClick() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new LoadFileFragment())
                .commit();
    }

    public void prefClick() {
        Intent set = new Intent(this, SettingsActivity.class);
        startActivity(set);
    }

    public void detailsClick() {
        if (Session.isStarted()) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new LocDetailsFragment())
                    .commit();
        }
        else
            alertSessionState("Not tracking...");
    }

    public void mainClick() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment())
                .commit();
    }

    public void returnClick() {
        if (Session.isStarted() && !Session.isReturning()) {
            new AlertDialog.Builder(this)
                    .setTitle("Conferma")
                    .setMessage("conferma ritorno?")
                    .setPositiveButton("Sì", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Session.getController().setReturn()) {
                                showTripInfo();
                                Session.setReturning(true);
                            }
                            else {
                                alertSessionState("No trackpoints...");
                            }
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
        else
            alertSessionState("Not tracking...");
    }

    private void alertSessionState(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Stato sessione")
                .setMessage(message)
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void compassClick() {
        View checkBoxView = View.inflate(this, R.layout.checkbok, null);
        CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
        checkBox.setChecked(Session.isCompass());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Session.isCompass())
                    Session.setCompass(false);
                else
                    Session.setCompass(true);
            }
        });
        checkBox.setText("Abilita bussola");

        new AlertDialog.Builder(this)
                .setTitle("Modalità Bussola")
                .setView(checkBoxView)
                .setCancelable(false)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void showTripInfo() {
        Track current = Session.getController().getCurrentTrack();
        new AlertDialog.Builder(this)
                .setTitle("Trip Info")
                .setMessage("Distanza percorsa: " + Utilities.getFormattedDistance(current.getTotalDistance(), true) + "\n" +
                        "Tempo trascorso: " + Utilities.getFormattedTime(current.getTotalTime(), true))
                .setCancelable(true)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show();
    }

    private void startService() {
        serviceIntent = new Intent(this, GPSLoggingService.class);
        startService(serviceIntent);
    }

    private void stopService() {
        try {
            stopService(serviceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_gpsmain, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_map: {
                drawClick();
                return true;
            }
            case R.id.action_settings: {
                prefClick();
                return true;
            }
            case R.id.action_main: {
                mainClick();
                return true;
            }
            case R.id.action_compass: {
                compassClick();
                return true;
            }
            case R.id.action_return: {
                returnClick();
                return true;
            }
            case R.id.action_details: {
                detailsClick();
                return true;
            }
            case R.id.action_load: {
                loadClick();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        stopService();
        Session.getController().unregisterEventBus();
        super.onDestroy();
    }
}
