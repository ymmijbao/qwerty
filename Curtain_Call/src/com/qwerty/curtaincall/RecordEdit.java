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
		String value = intent.getStringExtra("key"); //if it's a string you stored.
		System.out.println(value);
		
		// Set up the listeners
		addListenerOnMeButton();
		addListenerOnThemButton();
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
			    	   String value = "Me: I am taking a selfie Romeo! Why are you so needy?";
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
			    	   String value = "Romeo: where art Juliet? Thy Juliet hast afk?";
			    	   newLine.setText(value);
			    	   scrollLinLayout.addView(newLine, layoutParams);
			       }
			       return true;
			   }
		});

	}

}
