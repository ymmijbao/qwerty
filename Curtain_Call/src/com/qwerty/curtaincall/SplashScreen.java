package com.qwerty.curtaincall;



import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SplashScreen extends Activity {
	
	Button stub;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		addListenerOnButton();
		
		Button playSelectorButton = (Button) findViewById(R.id.button2);
		playSelectorButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(SplashScreen.this, PlaySelector.class);
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
	
	public void addListenerOnButton() {
		
		stub = (Button) findViewById(R.id.button1);

		stub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				System.out.println("button clicked");
				Intent myIntent = new Intent(SplashScreen.this, RecordEdit.class);
				myIntent.putExtra("key", "WHODUNIT"); //Optional parameters
				SplashScreen.this.startActivity(myIntent);
			}

		});

	}

}
