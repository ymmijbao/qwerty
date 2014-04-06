package com.qwerty.curtaincall;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class PlaySelector extends Activity implements OnClickListener {
	private RelativeLayout mainLayout;
	private LinearLayout scrollLinLayout;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_selector);
		
		mainLayout = (RelativeLayout) findViewById(R.id.playSelectorLayout);
		scrollLinLayout = (LinearLayout) findViewById(R.id.scrollViewLinLayout);
		
		gestureDetector = new GestureDetector(getApplicationContext(), new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        
        Button addNewPlayButton = (Button) findViewById(R.id.add_new_play);
        addNewPlayButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Button newPlay = new Button(PlaySelector.this);
				newPlay.setBackgroundColor(Color.RED);
				
				
			    AlertDialog.Builder alert = new AlertDialog.Builder(PlaySelector.this);
			    final EditText input = new EditText(PlaySelector.this);
			    alert.setView(input);
			    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            String value = input.getText().toString().trim();
			            newPlay.setText(value);
			            scrollLinLayout.addView(newPlay);
			        }
			    });

			    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            dialog.cancel();
			        }
			    });
			    
			    alert.show(); 
			}
		});
	}
	
	public class MyGestureDetector extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DIST = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
		private static final int SWIPE_THRESHOLD = 100;
		
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) return false;

                if (e1.getX() - e2.getX() > SWIPE_MIN_DIST && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	Toast.makeText(PlaySelector.this, "Left SWIPE", Toast.LENGTH_SHORT).show();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DIST && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	Toast.makeText(PlaySelector.this, "Right SWIPE", Toast.LENGTH_SHORT).show();
                	RelativeLayout layout = (RelativeLayout) findViewById(R.id.playSelectorLayout);
                    TextView test = (TextView) findViewById(R.id.textView1);
                    layout.removeView(test);
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
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}
}
