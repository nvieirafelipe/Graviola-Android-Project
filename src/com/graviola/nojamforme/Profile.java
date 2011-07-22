package com.graviola.nojamforme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class Profile extends GraviolaAndroidProject {

	private EditText edit_first_name, edit_last_name, edit_email_address, edit_username;
	private TextView view_profile_name;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
		
    	edit_first_name = (EditText) findViewById(R.id.first_name); 
    	edit_last_name = (EditText) findViewById(R.id.last_name); 
    	edit_email_address = (EditText) findViewById(R.id.email_address); 
    	edit_username = (EditText) findViewById(R.id.username); 
    	view_profile_name = (TextView) findViewById(R.id.profile_name);

    	/** Setting values of dynamic items of the title bar. */
		this.title.setText("Profile");
		this.icon.setImageResource(R.drawable.icon);
		
    	Context context = this.getApplicationContext();
    	/** Create, if not exists, the preference GraviolaMOB. */
        SharedPreferences userCache = context.getSharedPreferences("sfGuardUser", MODE_PRIVATE);
        /** Check the preference connectivity and return false if is not set. */
        view_profile_name.setText(userCache.getString("first_name", "")+", "+userCache.getString("last_name", ""));
        edit_first_name.setText(userCache.getString("first_name", ""));
        edit_last_name.setText(userCache.getString("last_name", ""));
        edit_email_address.setText(userCache.getString("email_address", ""));
        edit_username.setText(userCache.getString("username", ""));
		
	}
}
