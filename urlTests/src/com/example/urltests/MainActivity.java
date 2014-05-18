package com.example.urltests;


import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {

	private volatile MediaPlayer mp;
	private ProgressBar progressBar;
	private SeekBar seekbar;
	
	private Boolean stop = false;
	private Boolean update = true;
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
	
	public void getString(View view){
		if(mp != null){
			if(mp.isPlaying()){
				mp.stop();
			}
			stop = true;
		}
		this.seekbar = (SeekBar) findViewById(R.id.songProgress);
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

		    @Override       
		    public void onStopTrackingTouch(SeekBar seekBar) {
		    	mp.seekTo(seekbar.getProgress());
		        update = true;     
		    }       

		    @Override       
		    public void onStartTrackingTouch(SeekBar seekBar) {     
		        update = false;  
		    }

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				
			}     
		});
		new Asynctask(this).execute();
		new check().execute();
	}
	
	public void pauseMusic(View view){
		this.mp.pause();
		
	}
	
	public void playMusic(View view){
		this.mp.start();
	}
	
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
		}
		
	}


}



