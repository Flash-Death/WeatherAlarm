/*
 * Erik Wenkel
 * Weather Alarm App
 * 10/8/2013
 * 
 * Weather Output Panel - Displayed upon the alarm going off 
 * and also handles the networking
 */

package edu.vt.ece4564.weatheralarm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class OutputWeather extends Activity {
	// Global Variables
	TextView weatherText;
	Button turnOffAlarmButton;
	Button resetAlarmButton;
	String location;
	Vibrator vibration;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.weather_ouput);
			
			// GUI elements
			weatherText = (TextView) this.findViewById(R.id.textView7);
			turnOffAlarmButton = (Button) this.findViewById(R.id.button3);
			resetAlarmButton = (Button) this.findViewById(R.id.button4);
			
			// Set vibration for when alarm goes off
			vibration = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			long[] pattern = {0, 100, 1000};
			vibration.vibrate(pattern, 0);
			
			// Listener to turn off the alarm vibration
			turnOffAlarmButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v2) {
					// Cancel Vibration
					vibration.cancel();
				}
			});
			
			// Listener to set a new alarm
			resetAlarmButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v1) {
					// Switch to the TimeSet Panel
				    startActivity(new Intent(getApplicationContext(), TimeSet.class));
				}
			});
			
			// Receive Location Data
			Intent i = getIntent();
			location = i.getStringExtra("location");
			
			// Start AsyncTask
			if(location != "Set Location")
				new GetWeather().execute("");	
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// AsyncTask for networking
    private class GetWeather extends AsyncTask<String, Void, String> {
		// Networking variables
    	int startOfSpan;
    	String subHtml;
    	String line;
    	String html;
    	StringBuilder htmlBuilder = new StringBuilder();
    	CountDownLatch latch = new CountDownLatch(2);
    	String extractedData1 = "";
    	String extractedData2 = "";
    	
	    @Override
	    protected String doInBackground(String... params) {
	    	try {
		    	new Thread(new Runnable(){
					public void run(){
						try {
					    	// Setup connection to weather.com
					    	URL url1 = new URL("http://www.weather.com/weather/today/" + location);
					    	HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
					    	connection1.setRequestMethod("GET");
					    	BufferedReader readIn1 = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
					    	
					    	// Create HTML document
					    	while ((line = readIn1.readLine()) != null) {
					            htmlBuilder.append(line);
					        }
					    	
					    	html = htmlBuilder.toString();
					        readIn1.close();
					        
					        // Extract relevant data
					    	while((startOfSpan = html.indexOf("It's ")) != -1) {
					    		subHtml = html.substring(startOfSpan + ("It's ").length());
					    		extractedData1 = subHtml.substring(0, subHtml.indexOf("\">"));
					    		break;
					    	}
						} catch(Exception e) {
				    		e.printStackTrace();
				    	}
						latch.countDown();
					}
				}).start();
		    	
		    	new Thread(new Runnable(){
					public void run(){
						try {
					    	// Setup connection to weather.gov
					    	URL url2 = new URL("http://www.weather.gov/" + location);
					    	HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
					    	connection2.setRequestMethod("GET");
					    	BufferedReader readIn2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
					    	
					    	// Create HTML document
					    	while ((line = readIn2.readLine()) != null) {
					            htmlBuilder.append(line);
					        }
					    	
					    	html = htmlBuilder.toString();
					        readIn2.close();
					        
					        // Extract relevant data
					    	while((startOfSpan = html.indexOf("\"myforecast-current\">")) != -1) {
					    		subHtml = html.substring(startOfSpan + ("\"myforecast-current\">").length());
					    		extractedData2 = subHtml.substring(0, subHtml.indexOf("&deg"));
					    		break;
					    	}
					    	
				    	} catch(Exception e) {
				    		e.printStackTrace();
				    	}
						latch.countDown();
				    }
				}).start();
				latch.await();
			} catch (Exception e) {
				e.printStackTrace();
			}
    		return null;
    	}    
	    
		@Override
		protected void onPostExecute(String NULL) {
			try {
				// Extract only the weather and temperature
				String Temp1 = extractedData1.substring(0, extractedData1.indexOf("&")) + " deg F";
				String Cond1 = extractedData1.substring(extractedData1.indexOf(" ")+1);
				String Temp2 = extractedData2.substring(extractedData2.indexOf("\">")+2) + " deg F";
				String Cond2 = extractedData2.substring(0, extractedData2.indexOf("<"));
				
				extractedData1 = "\nwww.weather.com:\n" + Temp1 + ", " + Cond1 + "\n";
				extractedData2 = "\nwww.weather.gov:\n" + Temp2 + ", " + Cond2 + "\n";
				
				// Set the weather output text
				weatherText.setText(extractedData1 + extractedData2);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
    } 
}
