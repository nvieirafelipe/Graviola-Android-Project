package com.graviola.nojamforme;

import android.os.Bundle;

public class Profile extends GraviolaAndroidProject {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
		
    	/** Setting values of dynamic items of the title bar. */
		this.title.setText("Profile");
		this.icon.setImageResource(R.drawable.icon);
	}
}
