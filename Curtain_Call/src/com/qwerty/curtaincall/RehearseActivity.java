package com.qwerty.curtaincall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;

import com.qwerty.curtaincall.RehearseSettingsActivity.MyPreferenceFragment;
import com.qwerty.data.DataStorage;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.support.v4.app.NavUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/* TODO: 
 * Read in data from Data.java to collect script data
 * WAKE_LOCK permission to prevent dimming
 * scroll.post(new Runnable() {            

    @Override

    public void run() {

    scroll.fullScroll(View.FOCUS_DOWN);              

    }
 */

public class RehearseActivity extends Activity {
	
	/* Rehearsal settings data */
	private SharedPreferences sharedPref;
	private boolean omitMyLinesPref;
	// private boolean lineFeedbackPref; // TODO incorporate
	private MyOnSharedPreferenceChangeListener sharedPrefListener;
	
	/* Data about this scene's main elements */
	private String play; // The name of the play.
	private String scene; // The name of the scene.
	private LineTableRow currentLineTR; // The LineTableRow of the current line audio.
	private ArrayList<LineTableRow> lineData; // The set of line UI elements for the current scene, in order.
	
	/* Media tools */
	private boolean mIsPlaying; // TRUE if the MediaPlayer is currently playing.
	private MediaPlayer mediaPlayer; // Plays audio tracks.
	
	/* Constants */
	public static final int LINE_TEXT_SIZE = 20; // The size of the lines in the scene line display, in "scaled pixels" ("sp").
	public static final String BLANK_LINE = "_______________"; // Represents a blank (omitted) line's text.
	public static final int HIGHLIGHT_COLOR = R.color.yellow; // Color of the current (highlighted) line.
	public static final int PLAIN_COLOR = R.color.transparent; // Color of non-highlighted lines.
	
	
	
	
	/*
	@Override
	protected void onResume() {
		super.onResume();
		
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		omitMyLinesPref = sharedPref.getBoolean("pref_omit_my_lines", true);
		// lineFeedbackPref = prefs.getBoolean("pref_line_feedback", true);
		updateLineDisplay();
	}
	*/
	@Override
	protected void onResume() {
	    super.onResume();
	    sharedPref.unregisterOnSharedPreferenceChangeListener(sharedPrefListener);
	}
	
	@Override
	protected void onPause() {
	    super.onPause();
	    sharedPref.registerOnSharedPreferenceChangeListener(sharedPrefListener);
	}
	
	/* Updates UI whenever settings are changed. */
	private class MyOnSharedPreferenceChangeListener implements OnSharedPreferenceChangeListener {
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
			omitMyLinesPref = prefs.getBoolean("pref_omit_my_lines", true);
			// lineFeedbackPref = prefs.getBoolean("pref_line_feedback", true);
			updateLineDisplay();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rehearse);
		
		// Gather saved preferences (from settings).
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		omitMyLinesPref = sharedPref.getBoolean("pref_omit_my_lines", true);
		// lineFeedbackPref = sharedPref.getBoolean("pref_line_feedback", true);
		sharedPrefListener = new MyOnSharedPreferenceChangeListener();
		// sharedPref.registerOnSharedPreferenceChangeListener(sharedPrefListener);
		
		// Gather intent data from previous activity.
		Bundle extras = getIntent().getExtras();
		play = extras.getString("play");
		scene = extras.getString("chunk");
		if (play == null) { // TODO remove (debugging)
			Log.d("REHEARSEACTIVITY", "play is null");
		}
		
		// Set MediaPlayer-related variables.
		mIsPlaying = false;
		mediaPlayer = new MediaPlayer();
		
		// UI: Set the TextViews displaying the current play title.
		final TextView playTV = (TextView)findViewById(R.id.text_play_title);
		playTV.setText(play);
		
		// UI: Set up buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		final Spinner sceneJumpSpinner = (Spinner)findViewById(R.id.spinner_scene_jump);
		final ImageButton previousSceneButton = (ImageButton)findViewById(R.id.button_previous_scene);
		final ImageButton nextSceneButton = (ImageButton)findViewById(R.id.button_next_scene);
		
		// Set up the play/pause button.
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Start the playback at the selected line (beginning of scene by default).
				if (!mIsPlaying) {
					playAudio();
				} else {
					pauseAudio();
				}
			}
		});
		
		// Set up the scene jump drop-down menu.
		ArrayList<String> scenes = DataStorage.getAllChunks(play);
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_scene_jump, scenes);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sceneJumpSpinner.setAdapter(spinnerArrayAdapter);
		int defaultScenePosition = spinnerArrayAdapter.getPosition(scene); // Set the default scene in the spinner.
		sceneJumpSpinner.setSelection(defaultScenePosition);
		sceneJumpSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if (currentLineTR != null) {
					stopAudio();
				}
				String newScene = (String)parent.getItemAtPosition(position);
				setScene(newScene);
			}
			
			public void onNothingSelected(AdapterView<?> parent) {
				// Do nothing.
			}
		});
		
		// Set up the previous and next buttons.
		previousSceneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentIndex = sceneJumpSpinner.getSelectedItemPosition();
				if (currentIndex > 0) {
					sceneJumpSpinner.setSelection(currentIndex - 1);
				}
			}
		});
		
		nextSceneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int currentIndex = sceneJumpSpinner.getSelectedItemPosition();
				if (currentIndex < sceneJumpSpinner.getCount() - 1) {
					sceneJumpSpinner.setSelection(currentIndex + 1);
				}
			}
		});
		
		// Update the play/scene data and the corresponding display.
		setScene(scene);
	}
	
	private void playAudio() {
		// If no selected line, do nothing.
		if (currentLineTR == null) {
			if (!lineData.isEmpty())
				setCurrentLine(lineData.get(0));
			else
				return;
		}
		
		// MediaPlayer is now going to play.
		mIsPlaying = true;
		
		// Update UI buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.pause_button));
		
		// Start the MediaPlayer.
		Toast.makeText(getApplicationContext(), "Playing...", Toast.LENGTH_SHORT).show();
		mediaPlayer.start();
	}
	
	private void pauseAudio() {
		// MediaPlayer is now pausing.
		mIsPlaying = false;
		
		// Update UI buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
		
		// Pause the MediaPlayer.
		if (currentLineTR != null) {
			if (mediaPlayer.isPlaying()) {
				Toast.makeText(getApplicationContext(), "Paused.", Toast.LENGTH_SHORT).show();
				mediaPlayer.pause();
			}
		}
	}
	
	private void stopAudio() {
		// MediaPlayer is now stopping.
		mIsPlaying = false;
		
		// Update UI buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
		
		// Stop the MediaPlayer.
		if (currentLineTR != null) {
			if (mediaPlayer.isPlaying()) {
				Toast.makeText(getApplicationContext(), "Stopped.", Toast.LENGTH_SHORT).show();
				mediaPlayer.stop();
				mediaPlayer.release();
			}
		}
	}
	
	/* Update the screen to display the new scene. */
	private void setScene(String newScene) {
		// Reset the MediaPlayer.
		setCurrentLine(null);
		
		// Set current play and scene.
		scene = newScene;
		
		// Obtain all lines from the new scene.
		LinkedHashMap<String, String> lines = DataStorage.getAllLines(play, scene);
		
		// Clear the scrollable view of its current lines.
		final TableLayout lineTable = (TableLayout)findViewById(R.id.view_table_lines);
		lineTable.removeAllViews();
		
		// Parameters for line UI.
		TableRow.LayoutParams lpRow = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		lpRow.setMargins(0, 10, 0, 0);
		
		// Iterate through the lines, adding them to the display.
		lineData = new ArrayList<LineTableRow>();
		Set<Entry<String, String>> linesEntrySet = lines.entrySet();
		int counter = 0;
		for (Entry<String, String> line : linesEntrySet) {
			String lineName = line.getKey();
			String lineAudio = line.getValue();
			String lineSpeaker = getSpeaker(lineName);
			String lineText = "Line " + (counter + 1); // TODO use google transcriber to obtain text
			
			// Initialize a new table row.
			LineTableRow lineTR = new LineTableRow(this, lineName, lineAudio, counter);
			lineTR.setLayoutParams(lpRow);
			lineTR.setPadding(5, 5, 5, 5);
			
			// The first column represents the speaker (either the user or the "others").
			TextView speakerTV = new TextView(this);
			speakerTV.setText(lineSpeaker + ":  ");
			speakerTV.setTextSize(LINE_TEXT_SIZE);
			speakerTV.setTypeface(Typeface.DEFAULT_BOLD);
			
			// The second column represents the actual line.
			TextView lineTV = new TextView(this);
			if (lineSpeaker.equals("Me") && omitMyLinesPref)
				lineTV.setText(BLANK_LINE);
			else
				lineTV.setText(lineText);
			lineTV.setTextSize(LINE_TEXT_SIZE);
			
			// Add the above two components into the line table row.
			lineTR.addView(speakerTV);
			lineTR.addView(lineTV);
			
			// Make this line table row click-able.
			lineTR.setOnClickListener(new OnLineClickListener());
			
			// Display this line table row in the ScrollView.
			lineTable.addView(lineTR);
			
			// Add information about this line to the lineData collection.
			lineData.add(lineTR);
			
			counter++;
		}
		
		// If the list of lines isn't empty, set the default line to the first line.
		if (!lineData.isEmpty()) {
			setCurrentLine(lineData.get(0));
		} else {
			setCurrentLine(null);
		}
	}
	
	/* Update how the lines are displayed (blanked out, etc.) */
	private void updateLineDisplay() {
		// Toast.makeText(getApplicationContext(), "Calling updateLineDisplay()", Toast.LENGTH_SHORT).show(); // TODO delete (debugging)
		final TableLayout lineTable = (TableLayout)findViewById(R.id.view_table_lines);
		for (int i = 0; i < lineTable.getChildCount(); i++) {
			LineTableRow lineTR = (LineTableRow)(lineTable.getChildAt(i));
			String lineSpeaker = getSpeaker(lineTR.getLineName());
			String lineText = "Line " + (lineTR.getLineIndex() + 1);
			TextView lineTV = (TextView)(lineTR.getChildAt(1));
			if (lineSpeaker.equals("Me") && omitMyLinesPref) {
				lineTV.setText(BLANK_LINE);
			}
			else {
				lineTV.setText(lineText);
			}
		}
	}
	
	/* Select a line on the scene script. */
	private void setCurrentLine(LineTableRow newLineTR) {
		// Un-highlight the old current line.
		if (currentLineTR != null) {
			currentLineTR.setBackgroundColor(getResources().getColor(PLAIN_COLOR));
			mediaPlayer.release(); // TODO MEDIAPLAYER
		}
		
		// Set currentLine to newLine.
		currentLineTR = newLineTR;
		
		// Highlight the new current line.
		if (currentLineTR != null) {
			currentLineTR.setBackgroundColor(getResources().getColor(HIGHLIGHT_COLOR));
			mediaPlayer = createMediaPlayer(currentLineTR);
			
			final ScrollView scroll = (ScrollView) findViewById(R.id.view_lines);
			scroll.scrollTo(0, (int)currentLineTR.getY() - 50);
		} else {
			mediaPlayer = null;
		}
	}
	
	/* Get the next line in lineData. If we are at the end of lineData, reset the iterator. */
	private LineTableRow getNextLine() {
		if (lineData.isEmpty()) {
			return null;
		} else if (currentLineTR == null) {
			return lineData.get(0);
		} else {
			int index = currentLineTR.getLineIndex() + 1;
			if (index >= lineData.size())
				return null;
			else
				return lineData.get(index);
		}
	}
	
	/* Return an initialized MediaPlayer. */
	private MediaPlayer createMediaPlayer(LineTableRow line) {
		String lineName = line.getLineName();
		String lineAudio = line.getLineAudio();
		MediaPlayer player;
		try {
			player = new MediaPlayer();
			player.setDataSource(lineAudio);
			player.prepare();
			if (getSpeaker(lineName).equals("Me") && omitMyLinesPref)
				player.setVolume(0, 0);
			else
				player.setVolume(1, 1);
			player.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
					LineTableRow nextLineTR = getNextLine();
					if (nextLineTR != null) {
						setCurrentLine(nextLineTR);
						mediaPlayer.start();
					} else {
						stopAudio();
						setCurrentLine(lineData.get(0)); // would have to lineData.isEmpty() check normally
					}
				}
			});
			return player;
		} catch (IllegalArgumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IllegalStateException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/* A rather sloppy method to determine the speaker of a particular line. */
	private String getSpeaker(String lineName) {
		// Log.d("REHEARSEACTIVITY", "getting speaker with name: " + lineName);
		if (lineName.charAt(0) == 'm') {
			return "Me";
		} else {
			return "Them";
		}
	}
	
	/* OnClickListener used for interacting with the script lines. */
	private class OnLineClickListener implements View.OnClickListener {
		public void onClick(View v) {
			LineTableRow lineTR = (LineTableRow)v;
			stopAudio();
			setCurrentLine(lineTR);
		}
	}
	
	/* Wrapper class that stores lines and their corresponding TableRows. */
	private class LineTableRow extends TableRow {
		private String lineName; // The corresponding line's name (e.g., "me_1234" or "them_5382").
		private String lineAudio; // The corresponding line's audio file path.
		private int lineIndex; // The corresponding line's index in the list of line.
		
		/* It's a TableRow, but also with the corresponding line saved. */
		public LineTableRow(Context context, String newLineName, String newLineAudio, int newLineIndex) {
			super(context);
			lineName = newLineName;
			lineAudio = newLineAudio;
			lineIndex = newLineIndex;
		}
		
		/* It's a TableRow, but also with the corresponding line saved. */
		public LineTableRow(Context context, AttributeSet attrs, String newLineName, String newLineAudio, int newLineIndex) {
			super(context);
			lineName = newLineName;
			lineAudio = newLineAudio;
			lineIndex = newLineIndex;
		}
		
		/* Accessor methods. */
		public String getLineName() { return lineName; }
		public String getLineAudio() { return lineAudio; }
		public int getLineIndex() { return lineIndex; }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rehearse, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			stopAudio();
			
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			upIntent.putExtra("play", play);
			upIntent.putExtra("chunk", scene);
			
	        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	            TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
	        } else {
	            NavUtils.navigateUpTo(this, upIntent);
	        }
	        return true;
		case R.id.action_settings: // TODO make sure going back from options screen is not buggy
			pauseAudio();
			
			Intent settingsIntent = new Intent(RehearseActivity.this, RehearseSettingsActivity.class);
			settingsIntent.putExtra("play", play);
			settingsIntent.putExtra("chunk", scene);
			
			startActivity(settingsIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}

