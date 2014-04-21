package com.qwerty.curtaincall;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import com.qwerty.curtaincall.RehearseSettingsActivity.MyPreferenceFragment;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/* TODO: 
 * Implement button actions
 * Read in data from Data.java to collect script data
 * Make lines draggable?
 */

public class RehearseActivity extends Activity {
	/* Rehearsal settings data */
	private SharedPreferences sharedPref;
	private boolean omitMyLinesPref;
	private boolean lineFeedbackPref;
	
	/* Data about this UI's elements */
	private Play play;
	private Scene scene;
	private RecitableUI currentLine;
	
	/* Media tools */
	private MediaPlayer mediaPlayer;
	// TODO http://stackoverflow.com/questions/17742477/mediaplayer-progress-update-to-seekbar-not-smooth

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rehearse);
		
		// Gather saved preferences (from settings).
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		omitMyLinesPref = sharedPref.getBoolean("pref_omit_my_lines", true);
		lineFeedbackPref = sharedPref.getBoolean("pref_line_feedback", true);
		
		// UI: Set up title and subtitle of play/scene.
		final TextView playTitleText = (TextView)findViewById(R.id.text_play_title);
		final TextView sceneTitleText = (TextView)findViewById(R.id.text_scene_title);
		
		// TODO Intent i = getIntent();
		Play samplePlay = new Play("Romeo and Juliet"); // TODO delete hard-coded, i.getStringExtra("play");
		Scene sampleScene = new Scene("Act IV"); // TODO delete hard-coded, i.getStringExtra("scene");
		String playTitle = samplePlay.getName();
		String sceneTitle = sampleScene.getName();
		
		playTitleText.setText(playTitle);
		sceneTitleText.setText(sceneTitle);
		
		// UI: Set up the scene line display.
		int audio1 = R.raw.prince1mp3; // TODO delete hard-coded
		int audio2 = R.raw.montague1mp3; // TODO delete hard-coded
		int audio3 = R.raw.prince2mp3; // TODO delete hard-coded
		scene.addLine(false, "Come, Montague, for thou art early up / To see thy son and heir now early down.", audio1); // TODO delete hard-coded
		scene.addLine(true, "Alas, my liege, my wife is dead tonight. Grief of my son’s exile hath stopped her breath. What further woe conspires against mine age?", audio2); // TODO delete hard-coded
		scene.addLine(false, "Look, and thou shalt see.", audio3); // TODO delete hard-coded
		populateSceneLineDisplay(scene);
		
		// UI: Set up buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		final Button previousSceneButton = (Button)findViewById(R.id.button_previous_scene);
		final Button nextSceneButton = (Button)findViewById(R.id.button_next_scene);
		
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// start the playback at the selected line (beginning of scene by default)
				if (mediaPlayer != null) { // TODO debug
					playAudio(null);
				} else {
					pauseAudio();
				}
			}
		});
		
		// TODO
		previousSceneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopAudio();
				
				// TODO refactor
				// isPlaying = false;
				final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
				playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
				
				Scene newScene;
				if (scene.getName().equals("Act IV")) {
					newScene = new Scene("Act III"); // TODO get previous scene based on SCENE
					/*
					InputStream audio1 = getResources().openRawResource(R.raw.prince1mp3);
					InputStream audio2 = getResources().openRawResource(R.raw.prince1mp3);
					InputStream audio3 = getResources().openRawResource(R.raw.prince1mp3);
					InputStream audio4 = getResources().openRawResource(R.raw.prince1mp3);
					*/
					int audio1 = R.raw.prince1mp3;
					int audio2 = R.raw.montague1mp3;
					int audio3 = R.raw.prince2mp3;
					int audio4 = R.raw.prince1mp3;
					newScene.addLine(false, "You just want one word with one of us? Put it together with something else. Make it a word and a blow.", audio1);
					newScene.addLine(true, "You’ll find me ready enough to do that, sir, if you give me a reason.", audio2);
					newScene.addLine(false, "Can’t you find a reason without my giving you one?", audio3);
					newScene.addLine(true, "Mercutio, you hang out with Romeo.", audio4);
					setUpScene(newScene);
				} else if (scene.getName().equals("Act V")) {
					newScene = new Scene("Act IV"); // TODO get previous scene based on SCENE
					/*
					InputStream audio1 = getResources().openRawResource(R.raw.prince1mp3);
					InputStream audio2 = getResources().openRawResource(R.raw.montague1mp3);
					InputStream audio3 = getResources().openRawResource(R.raw.prince2mp3);
					*/
					int audio1 = R.raw.prince1mp3;
					int audio2 = R.raw.montague1mp3;
					int audio3 = R.raw.prince2mp3;
					newScene.addLine(false, "Come, Montague, for thou art early up / To see thy son and heir now early down.", audio1);
					newScene.addLine(true, "Alas, my liege, my wife is dead tonight. Grief of my son’s exile hath stopped her breath. What further woe conspires against mine age?", audio2);
					newScene.addLine(false, "Look, and thou shalt see.", audio3);
					setUpScene(newScene);
				}
			}
		});
		
		// TODO
		nextSceneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// go to the next scene
				
				// 1. Determine the following scene, based on the current scene's play/script
				// 2. Call displayScene() to update screen with new scene's data
				
				stopAudio();
				
				// TODO refactor
				// isPlaying = false;
				final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
				playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
				
				Scene newScene;
				if (scene.getName().equals("Act III")) {
					newScene = new Scene("Act IV"); // TODO get previous scene based on SCENE
					/*
					InputStream audio1 = getResources().openRawResource(R.raw.prince1mp3);
					InputStream audio2 = getResources().openRawResource(R.raw.montague1mp3);
					InputStream audio3 = getResources().openRawResource(R.raw.prince2mp3);
					*/
					int audio1 = R.raw.prince1mp3;
					int audio2 = R.raw.montague1mp3;
					int audio3 = R.raw.prince2mp3;
					newScene.addLine(false, "Come, Montague, for thou art early up / To see thy son and heir now early down.", audio1);
					newScene.addLine(true, "Alas, my liege, my wife is dead tonight. Grief of my son’s exile hath stopped her breath. What further woe conspires against mine age?", audio2);
					newScene.addLine(false, "Look, and thou shalt see.", audio3);
					setUpScene(newScene);
				} else if (scene.getName().equals("Act IV")) {
					newScene = new Scene("Act V"); // TODO get previous scene based on SCENE
					/*
					InputStream audio1 = getResources().openRawResource(R.raw.prince1mp3);
					InputStream audio2 = getResources().openRawResource(R.raw.prince1mp3);
					InputStream audio3 = getResources().openRawResource(R.raw.prince1mp3);
					*/
					int audio1 = R.raw.prince1mp3;
					int audio2 = R.raw.montague1mp3;
					int audio3 = R.raw.prince2mp3;
					newScene.addLine(false, "Have you come to make confession to this father?", audio1);
					newScene.addLine(true, "If I answered that question, I’d be making confession to you.", audio2);
					newScene.addLine(false, "Don’t deny to him that you love me.", audio3);
					setUpScene(newScene);
				}
			}
		});
	}
	
	// TODO
	private void playAudio(Recitable line) {
		// isPlaying = true;
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.pause_button));
		
		if (line == null) {
			scene.resetLineIndex();
			line = scene.nextLine();
		}
		MediaPlayer m = MediaPlayer.create(this, line.getLineAudio());
		mediaPlayer = m;
		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
			public void onCompletion(MediaPlayer mp) {
				mp.stop();
				mp.release();
				Recitable nextLine = scene.nextLine();
				if (nextLine != null) {
					playAudio(nextLine);
				} else {
					stopAudio();
				}
			}
		});
		if (omitMyLinesPref && line.isMyLine()) {
			mediaPlayer.setVolume(0, 0);
		}
		mediaPlayer.start();
	}
	
	// TODO
	private void pauseAudio() {
		// isPlaying = false;
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
		if (mediaPlayer != null) {
			try {
				mediaPlayer.pause();
			} catch (IllegalStateException e) {
				// TODO
			}
		}
	}
	
	// TODO
	private void stopAudio() {
		// isPlaying = false;
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		playButton.setBackground(getResources().getDrawable(R.drawable.play_button));
		if (mediaPlayer != null) {
			try {
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			} catch (IllegalStateException e) {
				// TODO
			}
		}
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
	
	/* Update the screen to display the new scene. */
	private void setUpScene(Scene newScene) {
		scene = newScene;
		final TextView sceneTitleText = (TextView)findViewById(R.id.text_scene_title);
		sceneTitleText.setText(scene.getName());
		populateSceneLineDisplay(scene);
	}
	
	/* Update the scrollable view containing the scene lines. */
	public void populateSceneLineDisplay(Scene scene) {
		final TableLayout lineTable = (TableLayout)findViewById(R.id.view_table_lines);
		lineTable.removeAllViews();
		
		ArrayList<Recitable> lines = scene.getLines();
		
		if (lines.isEmpty()) {
			setCurrentLine(null);
		} else {
			// Highlight the current line (the first line by default).
			RecitableUI lineUI = new RecitableUI(this, scene.getFirstLine());
			lineTable.addView(lineUI.getLineTableRow());
			setCurrentLine(lineUI);
			
			// Display the rest of the lines.
			for (int i = 1; i < lines.size(); i++) {
				lineUI = new RecitableUI(this, lines.get(i));
				lineTable.addView(lineUI.getLineTableRow());
			}
		}
	}
	
	// Select a line on the scene script.
	private void setCurrentLine(RecitableUI lineUI) {
		// unhighlight the previous current line
		if (currentLine != null)
			currentLine.unhighlight();
		currentLine = lineUI;
		// highlight the new current line
		if (currentLine != null)
			currentLine.highlight();
	}

	/* OnClickListener used for interacting with the script lines. */
	private class OnLineClickListener implements View.OnClickListener {
		public void onClick(View v) {
			// setCurrentLine();
		}
	}
	
	/* Represents a play, which consists of a collection of scenes. */
	public class Play {
		private String name; // the name of this play
		private ArrayList<Scene> scenes; // the set of scenes (chunks) that comprise this scene
		
		public Play() {
			name = "UNNAMED PLAY";
			scenes = new ArrayList<Scene>();
		}
		
		public Play(String title) {
			name = title;
			scenes = new ArrayList<Scene>();
		}
		
		/* Accessor methods */
		public String getName() { return name; }
		public ArrayList<Scene> getScenes() { return scenes; }
	}
	
	/* TODO Represents a scene (i.e., chunk), which consists of a collection of lines. */
	public class Scene {
		private String name; // the name of this scene
		private ArrayList<Recitable> lines; // the set of lines that comprise this scene
		private int lineIndex; // the index of the current line TODO refactor
		
		public Scene() {
			name = "UNNAMED SCENE";
			lines = new ArrayList<Recitable>();
			lineIndex = 0; // TODO refactor
		}
		
		public Scene(String title) {
			name = title;
			lines = new ArrayList<Recitable>();
			lineIndex = 0; // TODO refactor
		}
		
		/* Adds a line to the end of the scene. */
		public void addLine(boolean myLine, String text, int audio) {
			Recitable newLine = new Recitable(myLine, text, audio);
			lines.add(newLine);
		}
		
		/* Returns the first line of the scene. */
		public Recitable getFirstLine() {
			if (lines.isEmpty()) {
				return null;
			}
			return lines.get(0);
		}
		
		/* Get the next line in the script. */
		public Recitable nextLine() {
			if (lineIndex >= lines.size()) {
				return null;
			}
			Recitable line = lines.get(lineIndex++);
			// setCurrentLine(line); // TODO
			return line;
		}
		
		/* Reset the line index to 0. */
		public void resetLineIndex() {
			lineIndex = 0;
		}
		
		public String getName() { return name; }
		public ArrayList<Recitable> getLines() { return lines; }
	}
	
	/* A wrapper class that groups a Recitable (line) with a TableRow UI element for a particular context. */
	private class RecitableUI {
		private Context context;
		private Recitable line;
		private TableRow lineTableRow;
		
		public static final int LINE_TEXT_SIZE = 20; // the size of the lines in the scene line display, in "scaled pixels" (sp)
		public static final String BLANK_LINE = "_______________"; // represents a blank (omitted) line's text
		public static final int HIGHLIGHT_COLOR = R.color.yellow;
		public static final int PLAIN_COLOR = R.color.white;
		
		public RecitableUI(Context c, Recitable r) {
			context = c;
			line = r;
			
			lineTableRow = new TableRow(c);
			
			// The first column represents the speaker (either the user or the "others")
			TextView speakerText = new TextView(c);
			speakerText.setText(line.getSpeaker() + ":");
			speakerText.setTextSize(LINE_TEXT_SIZE);
			speakerText.setTypeface(Typeface.DEFAULT_BOLD);
			
			// The second column represents the actual line
			TextView lineText = new TextView(c);
			if (line.isMyLine())
				lineText.setText(BLANK_LINE);
			else
				lineText.setText(line.getLineText());
			lineText.setTextSize(LINE_TEXT_SIZE);
			
			lineTableRow.addView(speakerText);
			lineTableRow.addView(lineText);
			
			// Make this row clickable
			lineTableRow.setOnClickListener(new OnLineClickListener());
		}
		
		public RecitableUI(Context c, Recitable r, TableRow.LayoutParams lpRow,
				TableRow.LayoutParams lpSpeaker, TableRow.LayoutParams lpLine) {
			context = c;
			line = r;
			
			lineTableRow = new TableRow(c);
			lineTableRow.setLayoutParams(lpRow);
			
			// The first column represents the speaker (either the user or the "others")
			TextView speakerText = new TextView(c);
			speakerText.setLayoutParams(lpSpeaker);
			speakerText.setText(line.getSpeaker() + ":");
			speakerText.setTextSize(LINE_TEXT_SIZE);
			speakerText.setTypeface(Typeface.DEFAULT_BOLD);
			
			// The second column represents the actual line
			TextView lineText = new TextView(c);
			lineText.setLayoutParams(lpLine);
			if (line.isMyLine())
				lineText.setText(BLANK_LINE);
			else
				lineText.setText(line.getLineText());
			lineText.setTextSize(LINE_TEXT_SIZE);
			
			lineTableRow.addView(speakerText);
			lineTableRow.addView(lineText);
			
			// Make this row clickable
			lineTableRow.setOnClickListener(new OnLineClickListener());
		}
		
		/* Highlight this line. */
		public void highlight() {
			lineTableRow.setBackgroundColor(getResources().getColor(HIGHLIGHT_COLOR));
		}
		
		/* Un-highlight this line. */
		public void unhighlight() {
			lineTableRow.setBackgroundColor(getResources().getColor(PLAIN_COLOR));
		}
		
		/* Accessor methods. */
		public Context getContext() { return context; }
		public Recitable getLine() { return line; }
		public TableRow getLineTableRow() { return lineTableRow; }
	}
	
	/* Represents a continuous line in a script. */
	public class Recitable {
		private boolean isMyLine; // true if the user's line, false if others' lines
		private String lineText; // the line, as text
		private int lineAudio; // the line, as audio
		
		/* Creates a new scene line. */
		public Recitable(boolean myLine, String text, int audio) {
			isMyLine = myLine;
			lineText = text;
			lineAudio = audio;
		}
		
		/* Returns "You" if the line is the user's; otherwise returns "Them". */
		public String getSpeaker() {
			return isMyLine ? "You" : "Them";
		}
		
		/* Accessor methods. */
		public boolean isMyLine() { return isMyLine; }
		public String getLineText() { return lineText; }
		public int getLineAudio() { return lineAudio; }
	}
}

