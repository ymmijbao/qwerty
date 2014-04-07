package com.qwerty.curtaincall;

import java.util.ArrayList;

import com.qwerty.curtaincall.RehearseSettingsActivity.MyPreferenceFragment;

import android.media.AudioTrack;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/* TODO: 
 * Implement button actions
 * Read in data from Data.java to collect script data
 * Make lines draggable?
 */

public class RehearseActivity extends Activity {
	// Rehearsal settings data.
	private SharedPreferences sharedPref;
	private boolean omitMyLinesPref;
	private boolean lineFeedbackPref;
	
	// Data about this UI's elements
	private String playTitle;
	private String sceneTitle;
	
	// Status of activity
	private boolean isPlaying;
	private TextView currentLine;
	private AudioTrack currentLineAudio;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rehearse);
		
		/* Create the scrolling script
			* Select line by clicking it
			* Scroll through script by dragging it
		*/
		/* Create the action buttons
			* Play
			* Edit
			* Previous Scene/Act
			* Next Scene/Act
	    */
		
		// Gather saved preferences.
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		omitMyLinesPref = sharedPref.getBoolean("pref_omit_my_lines", true);
		lineFeedbackPref = sharedPref.getBoolean("pref_line_feedback", true);
		
		// Activity status.
		Intent i = getIntent();
		playTitle = "Romeo and Juliet"; // TODO i.getStringExtra("play");
		sceneTitle = "Act V"; // TODO i.getStringExtra("scene");
		isPlaying = false;
		currentLine = null; // TODO DataStorage.getScene(sceneTitle).getLines().get(0);
		
		// UI view elements.
		final TextView playTitleText = (TextView)findViewById(R.id.text_play_title);
		final TextView sceneTitleText = (TextView)findViewById(R.id.text_scene_title);
		
		final TextView currentLineText = (TextView)findViewById(R.id.text_line1_line); // TODO get line text and highlight the first one
		currentLineText.setBackgroundColor(getResources().getColor(R.color.yellow));
		
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		final Button previousSceneButton = (Button)findViewById(R.id.button_previous_scene);
		final Button nextSceneButton = (Button)findViewById(R.id.button_next_scene);
		
		playTitleText.setText(playTitle);
		sceneTitleText.setText(sceneTitle);
		
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// start the playback at the selected line (beginning of scene by default)
				if (!isPlaying) {
					playButton.setBackground(getResources().getDrawable(R.drawable.pause_button));
					// start playing
					ArrayList<AudioTrack> lines = new ArrayList<AudioTrack>(); // TODO
					for (AudioTrack line : lines) {
						if (omitMyLinesPref) { // if we are to omit our lines, mute them
							line.setStereoVolume(0, 0);
						}
						line.play();
					}
					isPlaying = true;
					Toast.makeText(getApplicationContext(), "Playing...", Toast.LENGTH_SHORT).show();
				} else {
					playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
					// stop playing
					
					isPlaying = false;
					Toast.makeText(getApplicationContext(), "Stopped.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		previousSceneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// go to the previous scene
				
				// 1. Determine the preceding scene, based on the current scene's play/script
				// 2. Call displayScene() to update screen with new scene's data
				
				sceneTitle = "FAKE PREVIOUS"; // TODO get previous scene based on SCENE
				displayScene(sceneTitle);
			}
		});
		
		nextSceneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// go to the next scene
				
				// 1. Determine the following scene, based on the current scene's play/script
				// 2. Call displayScene() to update screen with new scene's data
				
				sceneTitle = "DUMMY NEXT"; // TODO get previous scene based on SCENE
				displayScene(sceneTitle);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rehearse, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.action_settings:
	            optionSettings();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	// Open the settings menu.
	private void optionSettings() {
		startActivity(new Intent(RehearseActivity.this, RehearseSettingsActivity.class));
	}
	
	// Select a line on the scene script.
	private void selectLine(TableRow line) {
		TableRow currentLine = null;
		currentLine.setBackgroundColor(getResources().getColor(R.color.white));
		currentLine = line;
		line.setBackgroundColor(getResources().getColor(R.color.yellow));
	}
	
	/* Update the screen to display the new scene. */
	private void displayScene(String scene) {
		// update scene title
		final TextView sceneTitleText = (TextView)findViewById(R.id.text_scene_title);
		sceneTitleText.setText(scene);
		// update script lines and audio files
		// TODO
	}

	private class OnLineClickListener implements View.OnClickListener {
		public void onClick(View v) {
			v.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}
}
