package com.qwerty.curtaincall;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;

import com.qwerty.data.DataStorage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class PlaySelector extends Activity implements OnClickListener {
	private RelativeLayout mainLayout;
	private LinearLayout scrollLinLayout;
	private GestureDetector gestureDetector;
	private View viewTouched;
	private EditText addNewPlay;
	private Button newPlay;
	View.OnTouchListener gestureListener;
	
	public static final int FROM_INPUT = 1;
	public static final int FROM_STORAGE = 2;
		
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
                gestureDetector.onTouchEvent(event);
            	return false;
            }
        };
		        
        addNewPlay = (EditText) findViewById(R.id.add_new_play);
        addNewPlay.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String playName = addNewPlay.getText().toString().trim();
					addPlay(playName, PlaySelector.FROM_INPUT);
				}
				
				return false;
			}
        });
        
		/** Populate buttons for all plays currently stored on the phone **/
		ArrayList<String> playsList = DataStorage.getAllPlays();
		
		for (String playName : playsList) {
			addPlay(playName, PlaySelector.FROM_STORAGE);
		}
	}
	
	public void addPlay(String playName, int source) {		
		if (playName.equals("")) {
			Toast.makeText(getApplicationContext(), "Please enter a play title first", Toast.LENGTH_SHORT).show();
		} else {
			int returnValue = DataStorage.addPlay(playName);
			
			if ((returnValue == DataStorage.EXISTS) && (source == PlaySelector.FROM_INPUT)) {
				Toast.makeText(getApplicationContext(), "The play already exists.", Toast.LENGTH_SHORT).show();
			} else if (source == PlaySelector.FROM_INPUT) {
				addPlayView(addNewPlay.getText().toString());
				addNewPlay.setText("");
			} else if (source == PlaySelector.FROM_STORAGE) {
				addPlayView(playName);
			}
		}
	}
	
	/** Adds a button to the layout to represent the play **/
	public void addPlayView(String playName) {
		newPlay = new Button(PlaySelector.this);
		newPlay.setBackgroundColor(0xff2975aa);
		newPlay.setTextColor(0xffffffff);
		newPlay.setText(playName);
		newPlay.setTag(playName);
		
		/** To go to the next corresponding screen to add/edit recordings **/
		newPlay.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(PlaySelector.this, ChunkSelector.class);
				intent.putExtra("play", (CharSequence) view.getTag());
				startActivity(intent);
			}
		});
		
		/** To rename the play name once it has been created **/
		newPlay.setOnLongClickListener(new Button.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
			    AlertDialog.Builder alert = new AlertDialog.Builder(PlaySelector.this);
			    final EditText input = new EditText(PlaySelector.this);
			    input.setText((CharSequence) view.getTag());
			    alert.setView(input);
			    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			    	
			        public void onClick(DialogInterface dialog, int whichButton) {
			            String value = input.getText().toString().trim();
			            newPlay.setText(value);
			            newPlay.setTag(value);
			        }
			    });

			    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			    	
			        public void onClick(DialogInterface dialog, int whichButton) {
			            dialog.cancel();
			        }
			    });
			    
			    alert.show(); 
				return false;
			}
		});
		
		/** To detect gestures defined in the MyGestureDetector class **/
		newPlay.setOnTouchListener(gestureListener);
						
		final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		layoutParams.setMargins(0, 10, 0, 0);
		
		scrollLinLayout.addView(newPlay, layoutParams);		
	}
	
	public class MyGestureDetector extends SimpleOnGestureListener {
		private static final int SWIPE_MIN_DIST = 120;
		private static final int SWIPE_MAX_OFF_PATH = 250;
		private static final int SWIPE_THRESHOLD_VELOCITY = 200;
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
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onClick(View v) {
		// Do nothing
	}
}
