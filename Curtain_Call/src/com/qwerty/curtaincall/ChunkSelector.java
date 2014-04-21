package com.qwerty.curtaincall;

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
import android.widget.TextView;
import android.widget.Toast;

public class ChunkSelector extends Activity implements OnClickListener {
	private RelativeLayout mainLayout;
	private LinearLayout scrollLinLayout;
	private GestureDetector gestureDetector;
	private View viewTouched;
	private EditText addNewChunk;
	private Button newPlay;
	private String playNameStr;
	View.OnTouchListener gestureListener;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chunk_selector);
		
		mainLayout = (RelativeLayout) findViewById(R.id.chunkSelectorLayout);
		scrollLinLayout = (LinearLayout) findViewById(R.id.chunkScrollViewLinLayout);
		
		Intent intent = getIntent();
		playNameStr = intent.getExtras().getString("play");
		getActionBar().setTitle(playNameStr);
		
		gestureDetector = new GestureDetector(ChunkSelector.this, new MyGestureDetector());
        gestureListener = new View.OnTouchListener() {
        	
            public boolean onTouch(View v, MotionEvent event) {
            	viewTouched = v;
                gestureDetector.onTouchEvent(event);
            	return false;
            }
        };
        
        addNewChunk = (EditText) findViewById(R.id.add_new_chunk);
        addNewChunk.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String playName = addNewChunk.getText().toString();
					
					if (playName.equals("")) {
						Toast.makeText(getApplicationContext(), "Please enter a chunk title first", Toast.LENGTH_SHORT).show();
					} else {
						addPlay(addNewChunk.getText().toString());
						addNewChunk.setText("");
					}
				}
				
				return false;
			}
        });
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
    			    AlertDialog.Builder alert = new AlertDialog.Builder(ChunkSelector.this);
    			    alert.setMessage("Permanently delete play?");
    			    
    			    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
    			    	public void onClick(DialogInterface dialog, int whichButton) {
    	                	final LinearLayout layout = (LinearLayout) findViewById(R.id.scrollViewLinLayout);
    	                	final Animation animation = AnimationUtils.loadAnimation(ChunkSelector.this, android.R.anim.slide_out_right); 
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
		//MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.play_selector, menu);
		return super.onCreateOptionsMenu(menu);
	}
		
	public void addPlay(String playName) {
		newPlay = new Button(ChunkSelector.this);
		newPlay.setBackgroundColor(0xff2975aa);
		newPlay.setTextColor(0xffffffff);
		newPlay.setText(playName);
		newPlay.setTag(playName);
		
		/** To go to the next corresponding screen to add/edit recordings **/
		newPlay.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent intent = new Intent(ChunkSelector.this, RecordEdit.class);
				intent.putExtra("play", playNameStr);
				intent.putExtra("chunk", (CharSequence) view.getTag());
				startActivity(intent);
			}
		});
		
		/** To rename the play name once it has been created **/
		newPlay.setOnLongClickListener(new Button.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
			    AlertDialog.Builder alert = new AlertDialog.Builder(ChunkSelector.this);
			    final EditText input = new EditText(ChunkSelector.this);
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

	@Override
	public void onClick(View v) {
		// Do nothing
	}
}
