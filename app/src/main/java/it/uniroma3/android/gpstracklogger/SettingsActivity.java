package it.uniroma3.android.gpstracklogger;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import it.uniroma3.android.gpstracklogger.prefs.SettingsFragment;


public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

}
