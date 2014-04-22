package com.qwerty.curtaincall;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
	
	// Status of activity
	private boolean isPlaying;
	
	// Audio player
	private AudioTrack audioTrack;
	private byte[] audioBuffer;
	private MediaPlayer mediaPlayer;
	
	// Data about this UI's elements
	private String playTitle; // TODO replace
	private String sceneTitle; // TODO replace
	private Scene scene;
	private Recitable currentLine;
	
	// Constants
	public static final int SAMPLE_RATE = 8000; // sample rate of AudioTrack
	public static final int LINE_TEXT_SIZE = 20; // the size of the lines in the scene line display, in "scaled pixels" (sp)

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rehearse);
		
		// Gather saved preferences (from Settings).
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		omitMyLinesPref = sharedPref.getBoolean("pref_omit_my_lines", true);
		lineFeedbackPref = sharedPref.getBoolean("pref_line_feedback", true);
		
		// Activity status.
		isPlaying = false;
		
		// Set up AudioTrack
		/*
		int minBufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, 
                AudioFormat.ENCODING_PCM_16BIT);
		audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_OUT_STEREO, 
             AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
		audioBuffer = new byte[minBufferSize];
		*/
		
		// UI: Set up title and subtitle of play/scene.
		final TextView playTitleText = (TextView)findViewById(R.id.text_play_title);
		final TextView sceneTitleText = (TextView)findViewById(R.id.text_scene_title);
		
		Intent i = getIntent();
		playTitle = "Romeo and Juliet"; // TODO i.getStringExtra("play");
		sceneTitle = "Act IV"; // TODO i.getStringExtra("scene");
		
		playTitleText.setText(playTitle);
		sceneTitleText.setText(sceneTitle);
		
		// UI: Set up scrollable scene line display.
		scene = new Scene(sceneTitle); // TODO remove scene hard-coding
		/*
		InputStream audio1 = getResources().openRawResource(R.raw.prince1mp3);
		InputStream audio2 = getResources().openRawResource(R.raw.montague1mp3);
		InputStream audio3 = getResources().openRawResource(R.raw.prince2mp3);
		*/
		int audio1 = R.raw.prince1mp3;
		int audio2 = R.raw.montague1mp3;
		int audio3 = R.raw.prince2mp3;
		scene.addLine(false, "Come, Montague, for thou art early up / To see thy son and heir now early down.", audio1);
		scene.addLine(true, "Alas, my liege, my wife is dead tonight. Grief of my son’s exile hath stopped her breath. What further woe conspires against mine age?", audio2);
		scene.addLine(false, "Look, and thou shalt see.", audio3);
		
		populateSceneLineDisplay(scene);
		
		// UI: Set up buttons.
		final ImageButton playButton = (ImageButton)findViewById(R.id.button_play);
		final Button previousSceneButton = (Button)findViewById(R.id.button_previous_scene);
		final Button nextSceneButton = (Button)findViewById(R.id.button_next_scene);
		
		playButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// start the playback at the selected line (beginning of scene by default)
				if (!isPlaying) {
					// TODO edit the for-loop and define a method for Scene class that runs a thread that plays audio
					// scene.playAudio();
					
					playAudio(scene.nextLine());
					
					// Toast.makeText(getApplicationContext(), "Playing...", Toast.LENGTH_SHORT).show();
				} else {
					
					// TODO stop playing audio
					// scene.stopAudio();
					pauseAudio();
					// Toast.makeText(getApplicationContext(), "Stopped.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		previousSceneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// go to the previous scene
				
				// 1. Determine the preceding scene, based on the current scene's play/script
				// 2. Call displayScene() to update screen with new scene's data
				
				stopAudio();
				
				// TODO refactor
				isPlaying = false;
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
		
		nextSceneButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// go to the next scene
				
				// 1. Determine the following scene, based on the current scene's play/script
				// 2. Call displayScene() to update screen with new scene's data
				
				stopAudio();
				
				// TODO refactor
				isPlaying = false;
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

	private void pauseAudio() {
		isPlaying = false;
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
	
	private void stopAudio() {
		isPlaying = false;
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
		// update scene title
		final TextView sceneTitleText = (TextView)findViewById(R.id.text_scene_title);
		sceneTitleText.setText(scene.getName());
		// update script lines and audio files
		populateSceneLineDisplay(scene);
	}
	
	/* Update the scrollable view containing the scene lines. */
	public void populateSceneLineDisplay(Scene scene) {
		final TableLayout lineTable = (TableLayout)findViewById(R.id.view_table_lines);
		lineTable.removeAllViews();
		
		TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, 10, 0);
		TableRow.LayoutParams lp2 = new TableRow.LayoutParams(600, TableRow.LayoutParams.WRAP_CONTENT);
		for (Recitable line : scene.getLines()) {
			TableRow lineRow = new TableRow(this);
			lineRow.setLayoutParams(lp);
			
			TextView speakerText = new TextView(this);
			speakerText.setLayoutParams(lp);
			speakerText.setText(line.getSpeaker() + ":");
			speakerText.setTextSize(LINE_TEXT_SIZE);
			speakerText.setTypeface(Typeface.DEFAULT_BOLD);
			
			TextView lineText = new TextView(this);
			lineText.setLayoutParams(lp2);
			if (line.getIsMyLine()) {
				lineText.setText("_________________");
			} else {
				lineText.setText(line.getLineText());
			}
			lineText.setTextSize(LINE_TEXT_SIZE);
			
			lineRow.addView(speakerText);
			lineRow.addView(lineText);
			
			line.setTableRow(lineRow); // save this table row in the Recitable
			
			lineTable.addView(lineRow);
		}
		
		// Highlight the current line (the first line by default).
		setCurrentLine(scene.getFirstLine());
	}
	
	// Select a line on the scene script.
	private void setCurrentLine(Recitable line) {
		if (currentLine != null) {
			TableRow currentLineRow = currentLine.getLineTableRow();
			currentLineRow.setBackgroundColor(getResources().getColor(R.color.transparent));
		}
		currentLine = line;
		if (currentLine != null) {
			TableRow newLineRow = currentLine.getLineTableRow();
			newLineRow.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}
	
	/* Play audio. */
	public void playAudio(Recitable line) {
		isPlaying = true;
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
		if (omitMyLinesPref && line.getIsMyLine()) {
			mediaPlayer.setVolume(0, 0);
		}
		mediaPlayer.start();
	}

	/* OnClickListener used for interacting with the script lines. */
	private class OnLineClickListener implements View.OnClickListener {
		public void onClick(View v) {
			// TODO v.setBackgroundColor(getResources().getColor(R.color.yellow));
		}
	}
	
	/* Represents a scene (i.e., chunk), which consists of a collection of lines. */
	public class Scene {
		private String name; // the name of this scene
		private ArrayList<Recitable> lines; // the set of lines that comprise this scene
		private int lineIndex; // the index of the current line
		
		public Scene() {
			name = "UNNAMED SCENE";
			lines = new ArrayList<Recitable>();
			lineIndex = 0;
		}
		
		public Scene(String n) {
			name = n;
			lines = new ArrayList<Recitable>();
			lineIndex = 0;
		}
		
		/* Adds a line to the end of the scene. */
		public void addLine(boolean myLine, String text, int audio) {
			Recitable newLine = new Recitable(myLine, text, audio);
			lines.add(newLine);
		}
		
		/* Plays the scene's lines, starting from the current line. */
		public void playAudio() {
			// TODO: Make sure audio tracks play one by one. Also make sure audio starts from current line.
			
			/*
			for (Recitable line : scene.getLines()) {
				InputStream audio = line.getLineAudio();
				int audioByte;
				try {
					while ((audioByte = audio.read(audioBuffer)) != -1) {
						audioTrack.write(audioBuffer, 0, audioByte);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					audio.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (omitMyLinesPref) { // if we are to omit our lines, mute them
					audioTrack.setStereoVolume(0, 0);
				} else { // otherwise, set them to their default volume
					audioTrack.setStereoVolume(1.0f, 1.0f);
				}
				audioTrack.play();
			}
			*/
		}
		
		/* Stops playing the scene's lines. */
		public void stopAudio() {
			// TODO
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
			setCurrentLine(line);
			return line;
		}
		
		/* Reset the line index to 0. */
		public void resetLineIndex() {
			lineIndex = 0;
		}
		
		public String getName() { return name; }
		public ArrayList<Recitable> getLines() { return lines; }
	}
	
	/* Represents a continuous line. */
	public class Recitable {
		private boolean isMyLine; // true if the user's line, false if others' lines
		private String lineText; // the line, as text
		private int lineAudio; // the line, as audio
		private TableRow lineTableRow; // the line, as a UI element
		
		/* Creates a new scene line. */
		public Recitable(boolean myLine, String text, int audio) {
			isMyLine = myLine;
			lineText = text;
			lineAudio = audio;
		}
		
		/* Assign the provided TableRow to this line, which it corresponds to. */
		public void setTableRow(TableRow tableRow) {
			lineTableRow = tableRow;
		}
		
		/* Returns "You" if the line is the user's; otherwise returns "Them". */
		public String getSpeaker() {
			if (isMyLine)
				return "You";
			else
				return "Them";
		}
		
		/* Accessor methods. */
		public boolean getIsMyLine() { return isMyLine; }
		public String getLineText() { return lineText; }
		public int getLineAudio() { return lineAudio; }
		public TableRow getLineTableRow() { return lineTableRow; }
	}
}

