package com.qwerty.curtaincall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;

import com.qwerty.curtaincall.RehearseSettingsActivity.MyPreferenceFragment;
import com.qwerty.data.DataStorage;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/* TODO: 
 * Implement button actions
 * Read in data from Data.java to collect script data
 */

public class RehearseActivity extends Activity {
	
	/* Rehearsal settings data */
	private SharedPreferences sharedPref;
	private boolean omitMyLinesPref; // TODO incorporate
	private boolean lineFeedbackPref; // TODO incorporate
	
	/* Data about this scene's main elements */
	private String play; // The name of the play.
	private String scene; // The name of the scene.
	private LineTableRow currentLineTR; // The LineTableRow of the current line audio.
	private ArrayList<LineTableRow> lineData; // The set of line UI elements for the current scene, in order.
	private Iterator<LineTableRow> lineDataIter; // The iterator for lineData.
	
	/* Media tools */
	private boolean mIsPlaying; // TRUE if the MediaPlayer is currently playing.
	private MediaPlayer mediaPlayer; // Plays audio tracks.
	
	/* Constants */
	public static final int LINE_TEXT_SIZE = 20; // The size of the lines in the scene line display, in "scaled pixels" ("sp").
	public static final String BLANK_LINE = "_______________"; // Represents a blank (omitted) line's text.
	public static final int HIGHLIGHT_COLOR = R.color.yellow; // Color of the current (highlighted) line.
	public static final int PLAIN_COLOR = R.color.white; // Color of non-highlighted lines.
	
	
	
	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rehearse);
		
		// Gather saved preferences (from settings).
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		omitMyLinesPref = sharedPref.getBoolean("pref_omit_my_lines", true);
		lineFeedbackPref = sharedPref.getBoolean("pref_line_feedback", true);
		
		// Gather intent data from previous activity.
		Bundle extras = getIntent().getExtras();
		play = "Romeo and Juliet"; // TODO delete hard-coded, extras.getString("play");
		scene = "Act IV"; // TODO delete hard-coded, extras.getString("chunk");
		
		// Set MediaPlayer-related variables.
		mIsPlaying = false;
		mediaPlayer = new MediaPlayer();
		
		// Update the play/scene data and the corresponding display.
		setScene(play, scene);
		
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
					Toast.makeText(getApplicationContext(), "Playing...", Toast.LENGTH_SHORT).show();
					playAudio();
				} else {
					Toast.makeText(getApplicationContext(), "Paused.", Toast.LENGTH_SHORT).show();
					pauseAudio();
				}
			}
		});
		
		// Set up the scene jump drop-down menu.
		ArrayList<String> scenes = new ArrayList<String>(); // TODO DataStorage.getAllChunks(play);
		scenes.add("Act I"); // TODO delete hard-coded
		scenes.add("Act II"); // TODO delete hard-coded
		scenes.add("Act III"); // TODO delete hard-coded
		scenes.add("Act IV"); // TODO delete hard-coded
		scenes.add("Act V"); // TODO delete hard-coded
		ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_scene_jump, scenes);
		spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sceneJumpSpinner.setAdapter(spinnerArrayAdapter);
		sceneJumpSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				String newScene = (String)parent.getItemAtPosition(position);
				setScene(play, newScene);
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
	}
	
	private void playAudio() {
		// If there are no lines in this scene, do nothing.
		if (currentLineTR == null) {
			return;
		}
		
		// MediaPlayer is now going to play.
		mIsPlaying = true;
		
		// Update UI buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.pause_button));
		
		// Start the MediaPlayer.
		mediaPlayer.start();
	}
	
	private void pauseAudio() {
		// MediaPlayer is now pausing.
		mIsPlaying = false;
		
		// Update UI buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
		
		// Pause the MediaPlayer.
		mediaPlayer.pause();
	}
	
	private void stopAudio() {
		// MediaPlayer is now stopping.
		mIsPlaying = false;
		
		// Update UI buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
		
		// Stop and reset the MediaPlayer.
		mediaPlayer.stop();
		mediaPlayer.release();
		mediaPlayer.reset();
	}
	
	/* Update the screen to display the new scene. */
	private void setScene(String newPlay, String newScene) {
		// Set current play and scene.
		play = newPlay;
		scene = newScene;
		
		// Set the TextViews displaying the current play and scene titles.
		final TextView playTV = (TextView)findViewById(R.id.text_play_title);
		// final TextView sceneTV = (TextView)findViewById(R.id.text_scene_title); // TODO delete
		playTV.setText(play);
		// sceneTV.setText(scene); // TODO delete
		
		// Obtain all lines from the new scene.
		LinkedHashMap<String, String> lines = new LinkedHashMap<String, String>(); // TODO DataStorage.getAllLines(play, scene); // TODO fix DataStorage.java
		
		// Clear the scrollable view of its current lines.
		final TableLayout lineTable = (TableLayout)findViewById(R.id.view_table_lines);
		lineTable.removeAllViews();
		
		// Parameters for line UI.
		TableRow.LayoutParams lpRow = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		lpRow.setMargins(0, 10, 0, 0);
		TableRow.LayoutParams lpSpeaker = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		TableRow.LayoutParams lpLine = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
		
		// Iterate through the lines, adding them to the display.
		lineData = new ArrayList<LineTableRow>();
		Set<Entry<String, String>> linesEntrySet = lines.entrySet();
		for (Entry<String, String> line : linesEntrySet) {
			String lineName = line.getKey();
			String lineAudio = line.getValue();
			String lineSpeaker = getSpeaker(lineName) + ":";
			String lineText = "DUMMY TEXT"; // TODO use google transcriber to obtain text
			
			// Initialize a new table row.
			LineTableRow lineTR = new LineTableRow(this, lineName, lineAudio);
			lineTR.setLayoutParams(lpRow);
			
			// The first column represents the speaker (either the user or the "others").
			TextView speakerTV = new TextView(this);
			speakerTV.setLayoutParams(lpSpeaker);
			speakerTV.setText(lineSpeaker + ":");
			speakerTV.setTextSize(LINE_TEXT_SIZE);
			speakerTV.setTypeface(Typeface.DEFAULT_BOLD);
			
			// The second column represents the actual line.
			TextView lineTV = new TextView(this);
			lineTV.setLayoutParams(lpLine);
			if (lineSpeaker.equals("Me"))
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
		}
		
		// Initialize lineData's iterator.
		lineDataIter = lineData.iterator();
		LineTableRow lineTR = nextLine(); // First line of this scene (NULL if empty scene).
			
		// Reset the MediaPlayer.
		mIsPlaying = false;
		mediaPlayer.release();
		mediaPlayer = new MediaPlayer();
		
		// Add a listener to the MediaPlayer to prepare the next track upon completing the current track.
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				// Play the next line track. If this was the last line, stop playing.
				LineTableRow nextLineTR = nextLine();
				if (nextLineTR != null) {
					try {
						mediaPlayer.setDataSource(nextLineTR.getLineAudio());
						mediaPlayer.prepare();
						playAudio();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					stopAudio();
				}
			}
		});
		
		// Set the current line to the first line in the scene.
		setCurrentLine(lineTR);
	}
	
	// TODO make sure to stop MediaRecorder if it's already playing and set it to play the current line
	/* Select a line on the scene script. */
	private void setCurrentLine(LineTableRow newLineTR) {
		if (currentLineTR != null) {
			// Un-highlight the previous current line.
			currentLineTR.setBackgroundColor(PLAIN_COLOR);
			
			// Stop playing the previous audio track.
			stopAudio();
		}
		
		// Set currentLine to newLine.
		currentLineTR = newLineTR;
		
		if (currentLineTR != null) {
			// Highlight the new current line.
			currentLineTR.setBackgroundColor(HIGHLIGHT_COLOR);
			
			// Set the MediaPlayer's audio file.
			try {
				mediaPlayer.setDataSource(currentLineTR.getLineAudio());
				mediaPlayer.prepare();
				
				// If the line being played is the user's, mute it. Otherwise, play it at the normal volume.
				String lineSpeaker = getSpeaker(currentLineTR.getLineName());
				if (omitMyLinesPref && lineSpeaker.equals("Me")) {
					mediaPlayer.setVolume(0, 0);
				} else {
					mediaPlayer.setVolume(1, 1);
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/* Get the next line in lineData. If we are at the end of lineData, reset the iterator. */
	private LineTableRow nextLine() {
		LineTableRow nextLineTR;
		if (lineDataIter.hasNext()) {
			nextLineTR = lineDataIter.next();
		} else {
			nextLineTR = null;
			lineDataIter = lineData.iterator();
		}
		return nextLineTR;
	}
	
	/* A rather sloppy method to determine the speaker of a particular line. */
	private String getSpeaker(String lineName) {
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
			setCurrentLine(lineTR);
		}
	}
	
	/* Wrapper class that stores lines and their corresponding TableRows. */
	private class LineTableRow extends TableRow {
		private String lineName; // The corresponding line's name (e.g., "me_1234" or "them_5382").
		private String lineAudio; // The corresponding line's audio file path.
		
		/* It's a TableRow, but also with the corresponding line saved. */
		public LineTableRow(Context context, String newLineName, String newLineAudio) {
			super(context);
			lineName = newLineName;
			lineAudio = newLineAudio;
		}
		
		/* It's a TableRow, but also with the corresponding line saved. */
		public LineTableRow(Context context, AttributeSet attrs, String newLineName, String newLineAudio) {
			super(context);
			lineName = newLineName;
			lineAudio = newLineAudio;
		}
		
		/* Accessor methods. */
		public String getLineName() { return lineName; }
		public String getLineAudio() { return lineAudio; }
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
	        	startActivity(new Intent(RehearseActivity.this, RehearseSettingsActivity.class));
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
}

