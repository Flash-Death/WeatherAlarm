/*
 * Erik Wenkel
 * Weather Alarm App
 * 10/8/2013
 * 
 * Main Panel - Displayed upon started and used to set an alarm.
 */

package edu.vt.ece4564.weatheralarm;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;

public class MainActivity extends Activity {
	// Global Variables
	TextView currentAlarmText;
	TextView setAlarmText;
	TextView currentLocationText;
	TextView setLocationText;
	Button currentAlarmButton;
	long secondsDelayed = -1;
	String location;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			
			// GUI elements
			currentAlarmText = (TextView) this.findViewById(R.id.textView1);
			setAlarmText = (TextView) this.findViewById(R.id.textView2);
			currentLocationText = (TextView) this.findViewById(R.id.textView3);
			setLocationText = (TextView) this.findViewById(R.id.textView4);
			currentAlarmButton = (Button) this.findViewById(R.id.button1);
			
			// Listener for button to set alarm
			currentAlarmButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Switch to TimeSet Panel
				    startActivity(new Intent(getApplicationContext(), TimeSet.class));
				}
			});
			
			// Async Thread uses information from TimeSet to set the alarm time and labels
			new SetLabels().execute("");	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class SetLabels extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			try {
				// Receive data from TimeSet
				Intent i = getIntent();
				location = i.getStringExtra("location");
				String hour = i.getStringExtra("hour");
				String minute = i.getStringExtra("minute");
				
				// Set ZIP Code Label
				if(location != null)
					setLocationText.setText(location);
				
				// Calculate the time until the alarm should go off
				if(hour != null && minute != null) {
					setAlarmText.setText(hour + ":" + minute);
					Time time = new Time();
					time.setToNow();
					long currentHour = (long) time.hour;
					long currentMinute = (long) time.minute;
					long currentSecond = (long) time.second;
					long currentTime = (((currentHour*60)+currentMinute)*60)+currentSecond;
					secondsDelayed = ((Long.valueOf(hour)*60)+Long.valueOf(minute))*60;
					
					if(currentTime > secondsDelayed)
						secondsDelayed = ((24*60*60*1000)-(currentTime*1000))+(secondsDelayed*1000);
					else
						secondsDelayed = (secondsDelayed*1000)-(currentTime*1000);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String NULL) {
			try {
				if(secondsDelayed != -1) {
					// Timer that waits until the alarms should go off
			        new Handler().postDelayed(new Runnable() {
			            public void run() {
			            	// Switches to the OutputWeather Panel
			            	Intent intent = new Intent(getApplicationContext(), OutputWeather.class);
			            	
			            	// Sends the ZIP Code to the Output Weather Panel 
						    intent.putExtra("location", location);
						    startActivity(intent);
			            }
			        }, secondsDelayed); 
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
}