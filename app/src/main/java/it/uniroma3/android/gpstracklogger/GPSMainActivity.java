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

package it.uniroma3.android.gpstracklogger;

import android.app.AlertDialog;
import android.app.Fragment;
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
        if (!Session.isControllerRegistered()) {
            Session.getController().registerEventBus();
            Session.setControllerRegistered(true);
        }
    }

    private void loadFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;
        String fragmentName;
        if (fragmentManager.getBackStackEntryCount() == 0) {
            fragment = new MainFragment();
            fragmentName = "Main";
        }
        else {
            fragmentName = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName();
            fragment = fragmentManager.findFragmentByTag(fragmentName);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, fragmentName);
        if (fragmentManager.getBackStackEntryCount() == 0)
            fragmentTransaction.addToBackStack(fragmentName).commit();
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
                .replace(R.id.container, new LoadFileFragment(), "Load")
                .addToBackStack("Load")
                .commit();
    }

    public void prefClick() {
        Intent set = new Intent(this, SettingsActivity.class);
        startActivity(set);
    }

    public void detailsClick() {
        if (Session.isStarted()) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, new LocDetailsFragment(), "Details")
                    .addToBackStack("Details")
                    .commit();
        }
        else
            alertSessionState("Not tracking...");
    }

    public void mainClick() {
        getFragmentManager().beginTransaction()
                .replace(R.id.container, new MainFragment(), "Main")
                .addToBackStack("Main")
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
        if (!Session.isStarted()) {
            try {
                stopService(serviceIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
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
    protected void onStart() {
        super.onStart();
        startService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService();
    }

    @Override
    protected void onPause() {
        stopService();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        stopService();
        Session.getController().unregisterEventBus();
        Session.setControllerRegistered(false);
        super.onDestroy();
    }
}
