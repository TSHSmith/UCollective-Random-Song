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

public class getJSON extends Thread{

	String json = "";
	
	@Override
	public void run(){
		try {
			json = getJson("https://ucollective.org/api/?request=audio&scope=rand");
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String startThread(){
		try {
			this.start();
			this.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
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
