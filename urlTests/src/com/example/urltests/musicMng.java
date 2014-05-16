package com.example.urltests;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class musicMng implements Runnable{
	
	private volatile MediaPlayer mp = null;
	private volatile Boolean isPlaying = false;
	private volatile MediaListener mL = new MediaListener(this);
	private volatile Boolean hasEnded = false;
	
	@Override
	public void run(){
		if (isPlaying){
			mp.stop();
			this.playSong();
		} else {
			this.playSong();
			isPlaying = true;
		}
			
	}
	
	public void playSong(){
		hasEnded = false;
		try {
			//gets a random json from url and saves it as a string
			getJSON gj = new getJSON();
			String text = gj.startThread();
			JSONObject obj = new JSONObject(text);
	
			mp = new MediaPlayer();	
			mp.setOnCompletionListener(new OnCompletionListener(){
				@Override
				public void onCompletion(MediaPlayer mp) {
					run();
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
	}
	
	public void pause(){
		if(mp != null){
			mp.pause();
			isPlaying = false;
		}
		Boolean test = mp.isPlaying();
	}
	
	public void play(){
		if(mp != null){
			mp.start();
			isPlaying = true;
		}
	}
	
	public Boolean getIsPlaying(){
		return this.isPlaying;
	}
	
	private String getJson(String url) throws ClientProtocolException, IOException{
		String responseString = "";
		
		HttpClient httpclient = new DefaultHttpClient();
	    HttpResponse response = httpclient.execute(new HttpGet(url));
	    StatusLine statusLine = response.getStatusLine();
	    if(statusLine.getStatusCode() == HttpStatus.SC_OK){
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        response.getEntity().writeTo(out);
	        out.close();
	        responseString = out.toString();
	        //..more logic
	    } else{
	        //Closes the connection.
	        response.getEntity().getContent().close();
	        throw new IOException(statusLine.getReasonPhrase());
	    }
	    
	    return responseString;
	}
	
	

}

class MediaListener implements OnCompletionListener{

	private musicMng mM = null;
	
	public MediaListener(musicMng mM){
		this.mM = mM;
	}
	
	@Override
	public void onCompletion(MediaPlayer mp) {
		mM.playSong();
	}
	
}
