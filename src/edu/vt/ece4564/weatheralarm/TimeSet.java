/*
 * Erik Wenkel
 * Weather Alarm App
 * 10/8/2013
 * 
 * Time Set Panel - Displayed when selecting the time 
 * and setting the location
 */

package edu.vt.ece4564.weatheralarm;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.app.Activity;
import android.content.Intent;

public class TimeSet extends Activity {
	// Global Variables
	TextView enterAlarmText;
	TimePicker alarmPicker;
	EditText editLocationText;
	Button setAlarmButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.set_time);
			
			// GUI elements
			enterAlarmText = (TextView) this.findViewById(R.id.textView5);
			alarmPicker = (TimePicker) this.findViewById(R.id.timePicker1);
			editLocationText = (EditText) this.findViewById(R.id.editText1);
			setAlarmButton = (Button) this.findViewById(R.id.button2);
			
			// Listener to set the alarm
			setAlarmButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					// Store the ZIP code
				    final String location = editLocationText.getText().toString();
				    
				    // Setup the time string to be sent
				    int h = alarmPicker.getCurrentHour();
				    final String hour;
				    int min = alarmPicker.getCurrentMinute();
				    final String minute;
				    
				    if(min < 10)
				    	minute = "0" + alarmPicker.getCurrentMinute().toString();
				    else
				    	minute = alarmPicker.getCurrentMinute().toString();
				    
				    if(h < 10)
				    	hour = "0" + alarmPicker.getCurrentHour().toString();
				    else
				    	hour = alarmPicker.getCurrentHour().toString();
				    	
				    // Send the location, time and switch to the Main Panel
				    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
				    intent.putExtra("location", location);
				    intent.putExtra("hour", hour);
				    intent.putExtra("minute", minute);
				    startActivity(intent);
				}
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}