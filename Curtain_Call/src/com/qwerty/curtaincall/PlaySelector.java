package com.qwerty.curtaincall;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PlaySelector extends Activity implements OnClickListener {
	private RelativeLayout mainLayout;
	private LinearLayout scrollLinLayout;
	private GestureDetector gestureDetector;
	private View viewTouched;
	View.OnTouchListener gestureListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_selector);
		
		mainLayout = (RelativeLayout) findViewById(R.id.playSelectorLayout);
		scrollLinLayout = (LinearLayout) findViewById(R.id.scrollViewLinLayout);
		
		gestureDetector = new GestureDetector(PlaySelector.this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
            	viewTouched = v;
                return gestureDetector.onTouchEvent(event);
            }
        };
        
        Button addNewPlayButton = (Button) findViewById(R.id.add_new_play);
        addNewPlayButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final Button newPlay = new Button(PlaySelector.this);
				newPlay.setBackgroundColor(0xfffaebd7);
				newPlay.setOnClickListener(PlaySelector.this);
				newPlay.setOnTouchListener(gestureListener);
				
				final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
				layoutParams.setMargins(0, 10, 0, 0);
				
			    AlertDialog.Builder alert = new AlertDialog.Builder(PlaySelector.this);
			    final EditText input = new EditText(PlaySelector.this);
			    input.setHint("Play Name");
			    alert.setView(input);
			    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            String value = input.getText().toString().trim();
			            newPlay.setText(value);
			            scrollLinLayout.addView(newPlay, layoutParams);
			            // Launch the RecordEdit or the ChunkSelector activity
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

                if (e2.getX() - e1.getX() > SWIPE_MIN_DIST && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                	/** Adding a Alert Dialog to ask the user if he/she really wants to delete the play **/
    			    AlertDialog.Builder alert = new AlertDialog.Builder(PlaySelector.this);
    			    alert.setMessage("Permanently delete play?");
    			    
    			    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    			    	public void onClick(DialogInterface dialog, int whichButton) {
    	                	final LinearLayout layout = (LinearLayout) findViewById(R.id.scrollViewLinLayout);
    	                	final Animation animation = AnimationUtils.loadAnimation(PlaySelector.this, android.R.anim.slide_out_right); 
    	                    viewTouched.startAnimation(animation);
    	                    Handler handle = new Handler();
    	                    handle.postDelayed(new Runnable() {
    	               
    	                      @Override
    	                        public void run() {
    	                    	  	if (viewTouched != null) {
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
    }

	@Override
	public void onClick(View v) {
		System.out.println("TEST");
	}
}