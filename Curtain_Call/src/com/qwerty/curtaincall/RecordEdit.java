package com.qwerty.curtaincall;

import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class RecordEdit extends Activity implements RecognitionListener {
	
	private RelativeLayout mainLayout;
	private LinearLayout scrollLinLayout;
	
	Button me;
	Button them;
	ImageButton rec;

	int isRecording;
	int recButtonEnabled;
	int meDepressed=0xffebae5d;
	int meUnpressed=0xfffaebd7;
	int themDepressed=0xffe04e0f;
	int themUnpressed=0xfff8b294;
	
	boolean isMePressed = false;
	boolean isThemPressed = false;
	
	private SpeechRecognizer mSpeechRecognizer;
	private ImageButton mListenButton;
	private TextView mResultText;
	private MediaRecorder myAudioRecorder;
	private String outputFile = null;
	//private Button start,stop,play;
	
	int linesRecorded = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// assign the layouts to the private vars
		mainLayout = (RelativeLayout) findViewById(R.id.recordEditLayout);
		scrollLinLayout = (LinearLayout) findViewById(R.id.recordEditLinearLayout);
		
		// grab the intent
		Intent intent = getIntent();
		
		// Set up the listeners
		addListenerOnMeButton();
		addListenerOnThemButton();
		//addListenerOnRecordButton();
		
		
		// sound part
	    outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecording.3gp";;
		
	    myAudioRecorder = new MediaRecorder();
	    myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
	    myAudioRecorder.setOutputFile(outputFile);

		mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
		mSpeechRecognizer.setRecognitionListener(this);
		mListenButton = (ImageButton)findViewById(R.id.recordButton);
		//mResultText = (TextView)findViewById(R.id.resultText);

//		mListenButton.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//				i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,  RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//				i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak!");
//				i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.qwerty.curtaincall");
//				mSpeechRecognizer.startListening(i);
//
//			}
//		});	
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
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
//	public void addListenerOnRecordButton() {
//		rec = (ImageButton) findViewById(R.id.recordButton);
//		recButtonEnabled=0;
//		
//		rec.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if (recButtonEnabled == 0){
//					Toast toast = Toast.makeText(getApplicationContext(), "Select 'My Line' or 'Other Line' to record", 2700);
//					toast.setGravity(Gravity.CENTER, 0, 0);
//					TextView viewtext = (TextView) toast.getView().findViewById(android.R.id.message);
//					if( viewtext!=null) viewtext.setGravity(Gravity.CENTER);
//					toast.show();
//				} else {
//					if(isRecording == 0){
//						isRecording = 1;
//		//					startRecording();
//						rec.setImageResource(R.drawable.stop_button);
//					} else if (isRecording ==1){
//						isRecording = 0;
//		//					stopRecording();
//						rec.setImageResource(R.drawable.record_button_gray);
//						recButtonEnabled = 0;
//						me.setBackgroundColor(meUnpressed);
//						them.setBackgroundColor(themUnpressed);
//					}
//				}
//			}
//		});
//	}
	
	public void addListenerOnMeButton() {
		
		me = (Button) findViewById(R.id.meButton);

		me.setOnClickListener(new OnClickListener() {
			   public void onClick(View v) {
//				   me.setBackgroundColor(meDepressed);
				   them.setBackgroundColor(themUnpressed);
				   me.setBackgroundResource(R.drawable.me_button_highlighted);
				   rec = (ImageButton) findViewById(R.id.recordButton);
				   rec.setImageResource(R.drawable.record_button_red);
//		    	   if(isRecording==0){
//					   recButtonEnabled = 1;
//					   rec.setImageResource(R.drawable.record_button_red);
//				   }
		    	   System.out.println("me button clicked");
		    	   isMePressed = true;
		    	   isThemPressed = false;
					Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,  RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak!");
					i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.qwerty.curtaincall");
					mSpeechRecognizer.startListening(i);
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
				   rec = (ImageButton) findViewById(R.id.recordButton);
				   rec.setImageResource(R.drawable.record_button_red);
//				   if(isRecording==0){
//					   recButtonEnabled = 1;
//					   rec.setImageResource(R.drawable.record_button_red);
//				   }
				   System.out.println("them button clicked");
		    	   isMePressed = false;
		    	   isThemPressed = true;
		    	   
					Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
					i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,  RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
					i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak!");
					i.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.qwerty.curtaincall");
					mSpeechRecognizer.startListening(i);
			   }
		});

	}

	@Override
	public void onBeginningOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onBufferReceived(byte[] arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(int arg0, Bundle arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPartialResults(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReadyForSpeech(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResults(Bundle arg0) {
		// TODO Auto-generated method stub
		ArrayList<String> speechResults = arg0.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		if (speechResults.size() > 0) {
			//mResultText.setText(speechResults.get(0));
    	   final Button newLine = new Button(RecordEdit.this);
    	   rec = (ImageButton) findViewById(R.id.recordButton);
    	   rec.setImageResource(R.drawable.record_button_gray);
    	   if (isMePressed) {
    		   newLine.setBackgroundColor(0xfffaebd7);
    	   }
    	   if (isThemPressed) {
    		   newLine.setBackgroundColor(0xfff8b294);
    	   }
    	   final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    	   layoutParams.setMargins(0, 10, 0, 0);
    	   String value = speechResults.get(0);
    	   newLine.setText(value);
    	   scrollLinLayout.addView(newLine, layoutParams);
    	   final ScrollView scroll = (ScrollView) findViewById(R.id.recordEditScrollView);
    	   scroll.post(new Runnable() {            
    		    @Override
    		    public void run() {
    		           scroll.fullScroll(View.FOCUS_DOWN);              
    		    }
    		});
		} else {
			System.out.println("Could not detect speech!");
		}	
	}

	@Override
	public void onRmsChanged(float arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void play(View view) throws IllegalArgumentException,
	                           SecurityException, IllegalStateException, IOException {
	   MediaPlayer m = new MediaPlayer();
	   m.setDataSource(outputFile);
	   m.prepare();
	   m.start();
	   Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
	}
	
   public void stop(View view){
	   myAudioRecorder.stop();
	   myAudioRecorder.release();
	   myAudioRecorder  = null;
	   //stop.setEnabled(false);
	   //play.setEnabled(true);
	   Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
   }
   
	public void start(View view){
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
      //start.setEnabled(false);
      //stop.setEnabled(true);
      Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();
	}

}
