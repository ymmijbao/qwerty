package com.qwerty.curtaincall;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.TaskStackBuilder;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerty.data.DataStorage;

public class RecordEdit extends Activity {
	
	private RelativeLayout mainLayout;
	private LinearLayout scrollLinLayout;
	
	Button me;
	Button them;
	Button save, cancel;
	ImageButton rec;

	int lineIndex = 1;
	int isRecording;
	int recButtonEnabled;
	int meDepressed=0xffebae5d;
	int meUnpressed=0xfffaebd7;
	int themDepressed=0xffe04e0f;
	int themUnpressed=0xfff8b294;
	
	private MediaPlayer mediaPlayer;
	
	String playName, chunkName, value;
	boolean myLine;
	
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit);
		// Show the Up button in the action bar.
		setupActionBar();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// assign the layouts to the private vars
		mainLayout = (RelativeLayout) findViewById(R.id.recordEditLayout);
		scrollLinLayout = (LinearLayout) findViewById(R.id.recordEditLinearLayout);
		
		// grab the intent
		Intent intent = getIntent();
		playName = intent.getExtras().getString("play");
		chunkName = intent.getExtras().getString("chunk");
		
		// Set up the listeners
		addListenerOnMeButton();
		addListenerOnThemButton();
		addListenerOnRecordButton();
		addListenerOnSaveButton();
		addListenerOnCancelButton();
		
		// Set up audio recorder
		
		/*
		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecording.3gp";
		Log.d("RECORDEDIT", outputFile + " is the output file");
		myAudioRecorder.setOutputFile(outputFile);
		Log.d("RECORDEDIT", "SUCCESSFULLY SET OUTPUTFILE TO AUDIORECORDER");
		*/
		
		// Retrieve all existing lines in the chunk and display them
		//displayExistingLines();
	}

	
	private void startRecording(){
		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
//		try {
//	         myAudioRecorder.prepare();
//	      } catch (IllegalStateException e) {
//	         // TODO Auto-generated catch block
//	         e.printStackTrace();
//	      } catch (IOException e) {
//	         // TODO Auto-generated catch block
//	         e.printStackTrace();
//	      }
		if (myLine){
			outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CurtainCall/myLine" + System.currentTimeMillis() + ".3gp";
		} else {
			outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CurtainCall/theirLine" + System.currentTimeMillis()+ ".3gp";
		}
		System.out.println("OutputFile: "+ outputFile);
		myAudioRecorder.setOutputFile(outputFile);
		try {
	         myAudioRecorder.prepare();
	         myAudioRecorder.start();
	      } catch (IllegalStateException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      } catch (IOException e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
	}
	
	private void stopRecording(){
		try {
			myAudioRecorder.stop();			
		} catch(RuntimeException stopException) {
			Log.d("STOPBUG", "stopException thrown");
			Toast toast = Toast.makeText(getApplicationContext(), "I didn't quite catch that, please slow down.", Toast.LENGTH_LONG);
			toast.show();
			myAudioRecorder.release();
			myAudioRecorder = null;
			isRecording = 0;
			rec.setImageResource(R.drawable.record_button_gray);
			recButtonEnabled = 0;
			me.setBackgroundColor(meUnpressed);
			them.setBackgroundColor(themUnpressed);
			DataStorage.deleteLine(playName, chunkName, lineIndex - 1);
			return;
		}
		myAudioRecorder.release();
		myAudioRecorder = null;
		final Button newLine = new Button(RecordEdit.this);
		if (myLine){
			int result = DataStorage.addLine(playName, chunkName, outputFile, "me");
			Log.d("RECORDEDIT", "attempting to add button for output: " + outputFile);
			Log.d("RECORDEDIT", "success?: " + result);
			newLine.setBackgroundColor(0xfffaebd7);
			value = lineIndex + ": My Line";
		} else {
			int result = DataStorage.addLine(playName, chunkName, outputFile, "them");
			Log.d("RECORDEDIT", "attempting to add button for output: " + outputFile);
			Log.d("RECORDEDIT", "success?: " + result);
			newLine.setBackgroundColor(0xfff8b294);
			value = lineIndex + ": Other Line";
		}
		lineIndex++;
		newLine.setGravity(Gravity.LEFT);
		final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.setMargins(0, 10, 0, 0);
		newLine.setText(value);
		final String tempFile = outputFile;
		//clickable newLine buttons
		newLine.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				try {
					playAudio(tempFile);
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
		});
		
		scrollLinLayout.addView(newLine, layoutParams);
		final ScrollView scroll = (ScrollView) findViewById(R.id.recordEditScrollView);
		scroll.post(new Runnable() {            
			@Override
			public void run() {
				scroll.fullScroll(View.FOCUS_DOWN);              
			}
		});
			// DEBUG
			/*
   		LinkedHashMap<String, String> lines = DataStorage.getAllLines(playName, chunkName);
   		Set<Entry<String, String>> lineEntries = lines.entrySet();
   		for (Entry<String, String> lineEntry : lineEntries) {
   			Log.d("RECORDEDIT", lineEntry.getKey() + "..." + lineEntry.getValue());
   		}
			 */		
	}
	
	
	/**Play audio for clicked lines*/
	private void playAudio(String file) throws IllegalArgumentException,   
	   SecurityException, IllegalStateException, IOException{
	   MediaPlayer m = new MediaPlayer();
	   m.setDataSource(file);
	   m.prepare();
	   m.start();
	   Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
	}
	
	
	/** Display all existing lines in the chunk. */
	private void displayExistingLines() {
		LinkedHashMap<String, String> lines = DataStorage.getAllLines(playName, chunkName);
		Set<Entry<String, String>> lineEntries = lines.entrySet();
		for (Entry<String, String> lineEntry : lineEntries) {
			String lineName = lineEntry.getKey();
			String lineAudio = lineEntry.getValue();
			
			final Button newLine = new Button(RecordEdit.this);
			if (lineName.charAt(0) == 'm'){
				DataStorage.addLine(playName, chunkName, outputFile, "me");
				newLine.setBackgroundColor(0xfffaebd7);
				value = lineIndex + ": My Line";
			} else {
				DataStorage.addLine(playName, chunkName, outputFile, "them");
				newLine.setBackgroundColor(0xfff8b294);
				value = lineIndex + ": Other Line";
			}
			lineIndex++;
			newLine.setGravity(Gravity.LEFT);
			final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			layoutParams.setMargins(0, 10, 0, 0);
			newLine.setText(value);
			scrollLinLayout.addView(newLine, layoutParams);
		}
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.record_edit, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:			
			Intent upIntent = NavUtils.getParentActivityIntent(this);
			upIntent.putExtra("play", playName);
			upIntent.putExtra("chunk", chunkName);
			
	        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
	            TaskStackBuilder.create(this).addNextIntentWithParentStack(upIntent).startActivities();
	        } else {
	            NavUtils.navigateUpTo(this, upIntent);
	        }
	        return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void addListenerOnSaveButton() {
		save = (Button) findViewById(R.id.saveRecordingButton);
		save.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				Intent intent = new Intent(RecordEdit.this, RehearseActivity.class);
				intent.putExtra("play", playName);
				intent.putExtra("chunk", chunkName);
				startActivity(intent);
			}
		});
	}
	public void addListenerOnCancelButton(){
		Log.d("listener", "listener set up called");
		cancel = (Button) findViewById(R.id.cancelRecordingButton);
		cancel.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v){
				AlertDialog.Builder alert = new AlertDialog.Builder(RecordEdit.this);
				alert.setTitle("Cancel Recording");
				alert.setMessage("Are you sure you want to cancel? Your latest recordings will be deleted.");
				alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			    	
			        public void onClick(DialogInterface dialog, int whichButton) {
			            dialog.cancel();
			            Intent intent = new Intent(RecordEdit.this, ChunkSelector.class);
						intent.putExtra("play", playName);
						startActivity(intent);
			            //delete recordings
			            //go back to chunk selector
			        }
			    });

			    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
			    	
			        public void onClick(DialogInterface dialog, int whichButton) {
			            dialog.cancel();
			        }
			    });
			    
			    alert.show(); 
				
			}
		});
	}
	
	public void addListenerOnRecordButton() {
		rec = (ImageButton) findViewById(R.id.recordButton);
		recButtonEnabled=0;
		
		rec.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (recButtonEnabled == 0){
					Toast toast = Toast.makeText(getApplicationContext(), "Select 'My Line' or 'Other Line' to record", 2700);
					toast.setGravity(Gravity.CENTER, 0, 0);
					TextView viewtext = (TextView) toast.getView().findViewById(android.R.id.message);
					if( viewtext!=null) viewtext.setGravity(Gravity.CENTER);
					toast.show();
				} else {
					if(isRecording == 0){
						isRecording = 1;
						startRecording();
						rec.setImageResource(R.drawable.stop_button);
					} else if (isRecording ==1){
						isRecording = 0;
						stopRecording();
//						myAudioRecorder.release();
						rec.setImageResource(R.drawable.record_button_gray);
						recButtonEnabled = 0;
						me.setBackgroundColor(meUnpressed);
						them.setBackgroundColor(themUnpressed);
					}
				}
			}
		});
	}
	
	public void addListenerOnMeButton() {
		
		me = (Button) findViewById(R.id.meButton);

		me.setOnClickListener(new OnClickListener() {
			   public void onClick(View v) {
				   them.setBackgroundColor(themUnpressed);
				   me.setBackgroundResource(R.drawable.me_button_highlighted);
		    	   if(isRecording==0){
					   recButtonEnabled = 1;
					   rec.setImageResource(R.drawable.record_button_red);
					   myLine=true;
				   } else {
					   if (!myLine){
						   stopRecording();
						   myLine=true;
						   startRecording();
					   }
				   }
			   }
		});

	}
	
	public void addListenerOnThemButton() {
		
		them = (Button) findViewById(R.id.themButton);

		them.setOnClickListener(new OnClickListener() {
			   @Override
			   public void onClick(View v) {
				   me.setBackgroundColor(meUnpressed);
				   them.setBackgroundResource(R.drawable.them_button_highlighted);
				   if(isRecording==0){
					   recButtonEnabled = 1;
					   rec.setImageResource(R.drawable.record_button_red);
					   myLine=false;
				   } else {
					   if (myLine){
						   stopRecording();
						   myLine=false;
						   startRecording();
					   }
				   } 
			   }
		});

	}
}
