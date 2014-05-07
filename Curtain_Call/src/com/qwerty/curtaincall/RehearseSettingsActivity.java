package com.qwerty.curtaincall;

import android.app.TaskStackBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

/* SOURCES
 * Using PreferenceFragments: http://stackoverflow.com/questions/6822319/what-to-use-instead-of-addpreferencesfromresource-in-a-preferenceactivity
 */

public class RehearseSettingsActivity extends PreferenceActivity {
	public final static String TAG = "RehearseSettingsActivity";
	
	private String play;
	private String scene;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        
        // Gather intent data from RehearseActivity.
 		Bundle extras = getIntent().getExtras();
 		play = extras.getString("play");
 		scene = extras.getString("chunk");
    }
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    getMenuInflater().inflate(R.menu.activity_settings, menu);
	    return true;
	}
	*/

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent upIntent = NavUtils.getParentActivityIntent(this);
		upIntent.putExtra("play", play);
		upIntent.putExtra("chunk", scene);
		
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
        } else {
            NavUtils.navigateUpTo(this, upIntent);
        }
        
		/*
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
		*/
        
        return true;
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
