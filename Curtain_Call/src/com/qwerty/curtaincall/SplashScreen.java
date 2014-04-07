package com.qwerty.curtaincall;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class SplashScreen extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		
		Button playSelectorButton = (Button) findViewById(R.id.button2);
		playSelectorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(SplashScreen.this, PlaySelector.class);
				SplashScreen.this.startActivity(myIntent);	
			}
		});
		
		Button rehearseSelectorButton = (Button) findViewById(R.id.button3);
		rehearseSelectorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(SplashScreen.this, RehearseActivity.class);
				SplashScreen.this.startActivity(myIntent);	
			}
		});
		
		Button recordEditButton = (Button) findViewById(R.id.button1);
		recordEditButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(SplashScreen.this, RecordEdit.class);
				SplashScreen.this.startActivity(myIntent);	
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

}
