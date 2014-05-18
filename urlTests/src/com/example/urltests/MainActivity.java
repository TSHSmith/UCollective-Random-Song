package com.example.urltests;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.PorterDuff.Mode;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	private volatile MediaPlayer mp;
	private ProgressBar progressBar;
	private SeekBar seekbar;
	private Boolean musicPaused = false;
	Button button;
	private Boolean stop = false;
	private Boolean update = true;
	private Rect rect;
	private Boolean hasBeenPressed = false;
	private MainActivity mA = this;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		button = (Button) findViewById(R.id.playButton);
		button.setBackgroundResource(R.drawable.play);
		button.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mp != null){
					switch(event.getAction()){
					case MotionEvent.ACTION_CANCEL:
						break;
					case MotionEvent.ACTION_DOWN:
						if(!mp.isPlaying()){
							button.setBackgroundResource(R.drawable.playpressed);
						}else{
							button.setBackgroundResource(R.drawable.pausepressed);
						}
						break;
					case MotionEvent.ACTION_UP:
						if (!mp.isPlaying()){
							button.setBackgroundResource(R.drawable.pause);
							mp.start();
						} else {
							button.setBackgroundResource(R.drawable.play);
							mp.pause();
						}
						break;
					}
					
				}
				
				if(!hasBeenPressed){
					switch(event.getAction()){
					case MotionEvent.ACTION_DOWN:
						button.setBackgroundResource(R.drawable.playpressed);
						new Asynctask(mA).execute();
						new check().execute();
					case MotionEvent.ACTION_UP:
						button.setBackgroundResource(R.drawable.playpressed);
						hasBeenPressed = true;
						break;
					}
					
				}
				return false;
			}
		});
		
		this.seekbar = (SeekBar) findViewById(R.id.songProgress);
		seekbar.getProgressDrawable().setColorFilter(Color.argb(255, 255, 102, 0), Mode.SRC_IN);
		seekbar.getThumb().setColorFilter(Color.argb(255, 255, 102, 0), Mode.SRC_IN);
		seekbar.setEnabled(false);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

		    @Override       
		    public void onStopTrackingTouch(SeekBar seekBar) {
		    	if(mp != null)
		    		mp.seekTo(seekbar.getProgress());
		    	
		    	seekbar.getThumb().setColorFilter(Color.argb(255, 255, 102, 0), Mode.SRC_IN);
		        update = true;     
		    }       

		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar) {
		    	seekbar.getThumb().setColorFilter(Color.argb(255, 48, 144, 192), Mode.SRC_IN);
		        update = false;  
		    }

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}     
		});
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}
	
	/**
	 * Gets a song using the Asynctask, starts the update of the seekbar using check and sets up the onchangeListneer for the seekbar.
	 * @param view
	 */
	public void getSong(View view){
		if(mp != null){
			if(mp.isPlaying()){
				mp.stop();
			}
			stop = true;
		}
		
		
		new Asynctask(this).execute();
		new check().execute();
	}
	
	/**
	 * An Async task that upates the current status of the progress bar using the current position of the MediaPlayer: mP.
	 * @author Thomas
	 *
	 */
	class check extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			stop = false;
			while(true){
				if (mp != null){
					seekbar.setMax(mp.getDuration());
					while(seekbar.getMax() != mp.getCurrentPosition()){
						if(update)
							seekbar.setProgress(mp.getCurrentPosition());
						
						if(stop)
							break;
					}
				}
				if(stop)
					break;
			}
			return null;
		}

	}
	
	/**
	 * An Async task that gets the json for a random song and starts playing the music using the local variable mP.
	 * 
	 * @author Thomas
	 *
	 */
	class Asynctask extends AsyncTask<Void, Void, Void>{

		 ProgressDialog pd;
	     Context co;
	     MainActivity ma;


	     public Asynctask (MainActivity ma){
	         this.ma= ma;
	         this.co = ma;
	         pd= new ProgressDialog(co);

	     }
	     
		@Override
	    protected void onPreExecute() {
	        this.pd.setMessage("Getting your sick tunes.");
			this.pd.show();
	        //super.onPreExecute();
	    }
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				
				//gets a random json from url and saves it as a string;
				getJSON gj = new getJSON();
				String text = gj.startThread();
				JSONObject obj = new JSONObject(text);
				mp = new MediaPlayer();
				mp.setOnCompletionListener(new OnCompletionListener(){
					@Override
					public void onCompletion(MediaPlayer mp) {
						if(mp != null){
							if(mp.isPlaying()){
								mp.stop();
							}
							stop = true;
						}
						//seekbar = (SeekBar) findViewById(R.id.songProgress);
						new Asynctask(ma).execute();
						new check().execute();
					}
				});
				//sets the source of the data to that in the JSON object under the name "file"
				mp.setDataSource(obj.getString("file"));
				//prepares the mediaplayer
				mp.prepare();
				//starts streaming the audio
				mp.start();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result){
			this.pd.dismiss();
			seekbar.setEnabled(true);
			button.setBackgroundResource(R.drawable.pause);
		}
		
	}


}



