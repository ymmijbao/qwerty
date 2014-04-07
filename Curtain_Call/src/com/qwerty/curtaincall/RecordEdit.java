package com.qwerty.curtaincall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class RecordEdit extends Activity {
	
	private RelativeLayout mainLayout;
	private LinearLayout scrollLinLayout;
	
	Button me;
	Button them;
	String[] myLines;
	String[] theirLines;
	int myLineIndex;
	int theirLineIndex;

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
		
		// instantiate stub lines
		myLines = new String[6]; // Sampson
		theirLines = new String[6]; // Gregory
		myLines[0] = "Gregory, o' my word, we'll not carry coals.";
		theirLines[0] = "No, for then we should be colliers.";
		myLines[1] = "I mean, an we be in choler, we'll draw.";
		theirLines[1] = "Ay, while you live, draw your neck out o' the collar.";
		myLines[2] = "I strike quickly, being moved.";
		theirLines[2] = "But thou art not quickly moved to strike.";
		myLines[3] = "A dog of the house of Montague moves me.";
		theirLines[3] = "To move is to stir; and to be valiant is to stand: therefore, if thou art moved, thou runn'st away.";
		myLines[4] = "A dog of that house shall move me to stand: I will take the wall of any man or maid of Montague's.";
		theirLines[4] = "That shows thee a weak slave; for the weakest goes to the wall.";
		myLines[5] = "True; and therefore women, being the weaker vessels, are ever thrust to the wall: therefore I will push Montague's men from the wall, and thrust his maids to the wall.";
		theirLines[5] = "The quarrel is between our masters and us their men.";
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
	
	public void addListenerOnMeButton() {
		
		me = (Button) findViewById(R.id.meButton);

		me.setOnTouchListener(new OnTouchListener() {
			   @Override
			   public boolean onTouch(View v, MotionEvent event) {
			       if(event.getAction() == MotionEvent.ACTION_DOWN) {
			    	   System.out.println("me button pressed down");
			    	   Toast.makeText(getApplicationContext(), "Recording your line...", Toast.LENGTH_SHORT).show();
			       } else if (event.getAction() == MotionEvent.ACTION_UP) {
			    	   System.out.println("me button release");
			    	   Toast.makeText(getApplicationContext(), "Line saved.", Toast.LENGTH_SHORT).show();
			    	   final Button newLine = new Button(RecordEdit.this);
			    	   newLine.setBackgroundColor(0xfffaebd7);
			    	   final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			    	   layoutParams.setMargins(0, 10, 0, 0);
			    	   String value = "Sampson: " + myLines[myLineIndex%6];
			    	   myLineIndex++;
			           newLine.setText(value);
			           scrollLinLayout.addView(newLine, layoutParams);
			       }
			       return true;
			   }
		});

	}
	
	public void addListenerOnThemButton() {
		
		them = (Button) findViewById(R.id.themButton);

		them.setOnTouchListener(new OnTouchListener() {
			   @Override
			   public boolean onTouch(View v, MotionEvent event) {
			       if(event.getAction() == MotionEvent.ACTION_DOWN) {
			    	   System.out.println("them button pressed down");
			    	   Toast.makeText(getApplicationContext(), "Recording their line...", Toast.LENGTH_SHORT).show();
			       } else if (event.getAction() == MotionEvent.ACTION_UP) {
			    	   System.out.println("them button release");
			    	   Toast.makeText(getApplicationContext(), "Line saved.", Toast.LENGTH_SHORT).show();
			    	   final Button newLine = new Button(RecordEdit.this);
			    	   newLine.setBackgroundColor(0xfffaebd7);
			    	   final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			    	   layoutParams.setMargins(0, 10, 0, 0);
			    	   String value = "Gregory: " + theirLines[theirLineIndex%6];
			    	   theirLineIndex++;
			    	   newLine.setText(value);
			    	   scrollLinLayout.addView(newLine, layoutParams);
			       }
			       return true;
			   }
		});

	}

}
