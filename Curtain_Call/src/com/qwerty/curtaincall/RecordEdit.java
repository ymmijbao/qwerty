package com.qwerty.curtaincall;

import java.io.IOException;
import java.util.HashMap;
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
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.qwerty.data.DataStorage;

public class RecordEdit extends Activity implements OnClickListener {
	
	HashMap<Integer, String> hash = new HashMap<Integer, String>(); // used to map tags to recording name strings
	
	private RelativeLayout mainLayout;
	private LinearLayout scrollLinLayout;
	
	ImageButton me, them;
	Button save, cancel;

	int lineIndex = 1;
	
	private MediaPlayer mediaPlayer;
	
	
	String playName, chunkName, value;
	boolean myLineRec, themLineRec;
	
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	
	private GestureDetector gestureDetector;
	private View viewTouched; // This keeps track of which Button you selected
	View.OnTouchListener gestureListener;

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
		addListenerOnSaveButton();
		addListenerOnCancelButton();
		
		// swipe to delete functionality
		gestureDetector = new GestureDetector(RecordEdit.this, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
		        
			public boolean onTouch(View v, MotionEvent event) {
				viewTouched = v; // So I kept track of which Vie
				gestureDetector.onTouchEvent(event);
			    return false;
			}
		};
		
		// Set up audio recorder
		
		/*
		outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecording.3gp";
		Log.d("RECORDEDIT", outputFile + " is the output file");
		myAudioRecorder.setOutputFile(outputFile);
		Log.d("RECORDEDIT", "SUCCESSFULLY SET OUTPUTFILE TO AUDIORECORDER");
		*/
		
		// Retrieve all existing lines in the chunk and display them
		displayExistingLines();
	}

	
	private void startRecording(){
		myAudioRecorder = new MediaRecorder();
		myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);

		if (myLineRec){
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
			DataStorage.deleteLine(playName, chunkName, lineIndex - 1);
			return;
		}
		myAudioRecorder.release();
		myAudioRecorder = null;
		final Button newLine = new Button(RecordEdit.this);
		if (myLineRec){
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
		hash.put(new Integer(lineIndex - 1), outputFile);
		newLine.setTag(new Integer(lineIndex - 1));
		lineIndex++;
		newLine.setGravity(Gravity.LEFT);
		newLine.setOnTouchListener(gestureListener);
		final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.setMargins(0, 10, 0, 0);
		newLine.setText(value);
		
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
	   int duration = m.getDuration();
	   int loopIterations = duration / 2000 + 1;
	   int lineNumber = (Integer) viewTouched.getTag() + 1;
	   for (int i=0; i < loopIterations; i++)
	   {
		   Toast toast = Toast.makeText(getApplicationContext(), "Playing line " + Integer.toString(lineNumber), Toast.LENGTH_SHORT);
		   toast.show();
	   }
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
			hash.put(new Integer(lineIndex - 1), lineAudio);
			newLine.setTag(lineIndex - 1);
			newLine.setOnTouchListener(gestureListener);
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
				Intent intent = new Intent(RecordEdit.this, ChunkSelector.class);
				intent.putExtra("play", playName);
				startActivity(intent);
				
				/*// TODO Implement Cancel feature.
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
			    */
				
			}
		});
	}
	

	
	public void addListenerOnMeButton() {
		
		me = (ImageButton) findViewById(R.id.meButton);

		me.setOnClickListener(new OnClickListener() {
			   public void onClick(View v) {
				   if (themLineRec){ //other line straight to my line
					   them.setImageResource(R.drawable.other_line_record_button);
					   me.setImageResource(R.drawable.my_line_stop_button);
					   stopRecording();
					   myLineRec=true;
					   themLineRec=false;
					   startRecording();
				   } else if (myLineRec) { //stop my line recording
					   me.setImageResource(R.drawable.my_line_record_button);
					   stopRecording();
					   myLineRec=false;
				   } else { //no recording started
					   myLineRec=true;
					   me.setImageResource(R.drawable.my_line_stop_button);
					   startRecording();
				   }
			   }
		});

	}
	
	public void addListenerOnThemButton() {
		
		them = (ImageButton) findViewById(R.id.themButton);

		them.setOnClickListener(new OnClickListener() {
			   @Override
			   public void onClick(View v) {
				   if (myLineRec){ //my line straight to other line
					   me.setImageResource(R.drawable.my_line_record_button);
					   them.setImageResource(R.drawable.other_line_stop_button);
					   stopRecording();
					   myLineRec=false;
					   themLineRec=true;
					   startRecording();
				   } else if (themLineRec) { //stop them line recording
					   them.setImageResource(R.drawable.other_line_record_button);
					   stopRecording();
					   themLineRec=false;
				   } else { //no recording started
					   themLineRec=true;
					   them.setImageResource(R.drawable.other_line_stop_button);
					   startRecording();
				   }
			   }
		});

	}
	
	public class MyGestureDetector extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DIST = 100;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 180;
		
		@Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {            	
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;

                if (e2.getX() - e1.getX() > SWIPE_MIN_DIST && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	/** Adding a Alert Dialog to ask the user if he/she really wants to delete the play **/
    			    AlertDialog.Builder alert = new AlertDialog.Builder(RecordEdit.this);
    			    alert.setMessage("Permanently delete recording?");
    			    
    			    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    			    	public void onClick(DialogInterface dialog, int whichButton) {
    	                	final LinearLayout layout = (LinearLayout) findViewById(R.id.recordEditLinearLayout);
    	                	final Animation animation = AnimationUtils.loadAnimation(RecordEdit.this, android.R.anim.slide_out_right); 
    	                    viewTouched.startAnimation(animation);
    	                    Handler handle = new Handler();
    	                    handle.postDelayed(new Runnable() {
    	               
    	                      @Override
    	                        public void run() {
    	                    	  	if (viewTouched != null) {
    	                    	  		DataStorage.deleteLine(playName, chunkName, (Integer) viewTouched.getTag());
    	                    	  		layout.removeView(viewTouched);
    	                    	  		viewTouched = null;
    	                    	  	}
    	                            animation.cancel();
    	                        }
    	                    }, 300);
    			        }
    			    });

    			    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    			        public void onClick(DialogInterface dialog, int whichButton) {
    			            dialog.cancel();
    			        }
    			    });
    			    
    			    alert.show();    	
                } 
            } catch (Exception e) {
                // Do nothing
            }
            
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
        	return true;
        }
        
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
        	try {
        		String tempFile = hash.get((Integer) viewTouched.getTag());
				playAudio(tempFile);
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
        	return true;
        }
    }

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
