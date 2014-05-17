package com.example.urltests;


import java.io.IOException;

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
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity {
	Boolean running = false;
	musicMng mM = new musicMng();
	Asynctask as;
	MediaPlayer mp;
	
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
		new Asynctask(this).execute();
	}
	
	public void pauseMusic(View view){
		this.mp.pause();
		
	}
	
	public void playMusic(View view){
		this.mp.start();
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
	        super.onPreExecute();
	    }
		
		@Override
		protected Void doInBackground(Void... params) {
			try {
				if(mp != null){
					if(mp.isPlaying())
						mp.stop();
				}
				//gets a random json from url and saves it as a string;
				getJSON gj = new getJSON();
				String text = gj.startThread();
				JSONObject obj = new JSONObject(text);
				mp = new MediaPlayer();
				mp.setOnCompletionListener(new OnCompletionListener(){
					@Override
					public void onCompletion(MediaPlayer mp) {
						new Asynctask(ma).execute();
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



