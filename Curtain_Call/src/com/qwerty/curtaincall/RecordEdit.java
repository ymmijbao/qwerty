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

public class RecordEdit extends Activity {
	
	Button me;
	Button them;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record_edit);
		// Show the Up button in the action bar.
		setupActionBar();
		
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
			       } else if (event.getAction() == MotionEvent.ACTION_UP) {
			    	   System.out.println("me button release");
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
			       } else if (event.getAction() == MotionEvent.ACTION_UP) {
			    	   System.out.println("them button release");
			       }
			       return true;
			   }
		});

	}

}
