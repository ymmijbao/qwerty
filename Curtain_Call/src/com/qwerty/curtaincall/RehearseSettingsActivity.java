package com.qwerty.curtaincall;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;

/* SOURCES
 * Using PreferenceFragments: http://stackoverflow.com/questions/6822319/what-to-use-instead-of-addpreferencesfromresource-in-a-preferenceactivity
 */

public class RehearseSettingsActivity extends PreferenceActivity {
	public final static String TAG = "RehearseSettingsActivity";

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        
        Log.d(TAG, "Created RehearseSettingsActivity");
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
        
        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Unregister the listener whenever a key changes
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }
        
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        	if (key.equals("pref_omit_my_lines")) {
        		Log.d(TAG, "Toggled setting: pref_omit_my_lines");
        	} else if (key.equals("pref_line_feedback")) {
        		Log.d(TAG, "Toggled setting: pref_line_feedback");
        	} else {
        		Log.d(TAG, "Could not determine changed preference.");
        	}
        }
    }
    
}
