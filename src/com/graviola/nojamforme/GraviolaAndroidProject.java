package com.graviola.nojamforme;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class GraviolaAndroidProject extends Activity {
	/** Dynamic items of the title bar */
    protected TextView title;
    protected ImageView icon;
    protected ImageView header;
  
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main_unauthenticated);
       
        /** Setting custom title bar*/
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        
        /** Setting dynamic items of the title bar*/ 
        title = (TextView) findViewById(R.id.title);
        icon  = (ImageView) findViewById(R.id.icon);
        header = (ImageView) findViewById(R.id.header);
    }
}