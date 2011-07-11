package com.graviola.nojamforme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Main extends GraviolaAndroidProject {
	/** Constants to determine what activity is being referred to, and treat their response. */
	public static final int LOGIN_ACTIVITY = 1;
	public static final int REGISTER_ACTIVITY = 2;
	public static final int TRANSPORT_ACTIVITY = 3;
	
	/** Receiver for status of network connectivity. */
	private BroadcastReceiver broadcastReceiver; 
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** Default behavior is to load the application main screen of unauthenticated user. */
        setContentView(R.layout.main_unauthenticated);
                
        /** Defines which screen and what controls should be loaded. */
        showMain();
        /** Adds a listener for the status of network connection. */
        addConnectivityListener();
	}
	
	/** Called when the activity is destroyed. */
	@Override
    public void onDestroy() {
        super.onDestroy();
        
        /** Remove the receiver of status of network connectivity. */
        removeConnectivityListener();
	}
	
	/** Called when the option menu is created. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
        MenuInflater inflater = getMenuInflater();
        /** Defines custom option menu of the main screens. */
        inflater.inflate(R.menu.option_menu_main, menu);        
        return true;
    }

    /** Called before the option menu is shown. */ 
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		/** Check whether a user is logged in. */
		boolean authenticated = isAuthenticated(); 

		/** Shows only the appropriate options for each case. */
		menu.findItem(R.id.option_login_button).setVisible(!authenticated);
		menu.findItem(R.id.option_logout_button).setVisible(authenticated);
		menu.findItem(R.id.option_profile_button).setVisible(authenticated);
		
		return true;
    }
    
	/** Handle option menu item selection. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Intent myIntent;

        switch (item.getItemId()) {
        case R.id.option_profile_button:
            /** Create an intent to perform an operation, this case call an Activity. */
        	myIntent = new Intent(this.getBaseContext(), Profile.class);
        	/** Start an activity and wait for a result. */
            startActivityForResult(myIntent, 0);
            /** Transition of the screens. */
            overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out); 
            
            return true;
        case R.id.option_settings_button:
        	/** TODO create settings for the application. */
        	Toast.makeText(Main.this, "Access the Settings of the app!", Toast.LENGTH_SHORT).show();
        	
            return true;
        case R.id.option_login_button:
            /** Create an intent to perform an operation, this case call an Activity. */
        	myIntent = new Intent(this.getBaseContext(), Login.class);
        	/** Start an activity and wait for a result. */
            startActivityForResult(myIntent, LOGIN_ACTIVITY);
            /** Transition of the screens. */
            overridePendingTransition(R.anim.pull_in_down,R.anim.push_up_out); 
            
            return true;
        case R.id.option_logout_button:
        	Context context = this.getApplicationContext();
        	/** Create, if not exists, the preference GraviolaMOB. */
            SharedPreferences settings = context.getSharedPreferences("GraviolaMOB", MODE_PRIVATE);
            /** Create an editor to set the preference isAuthenticated to false. */
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("isAuthenticated", false);
            editor.commit();
            /** Defines which screen and what controls should be loaded. */
            showMain();
            
            return true;
        default:
        	return super.onOptionsItemSelected(item);
        }
    }

    /** Called when other activity returns a value. */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	/** Check from wich activity is this result. */
        if (requestCode == LOGIN_ACTIVITY) {
            if (resultCode == RESULT_OK) 
            	/** Defines which screen and what controls should be loaded. */
                showMain();
        }
    }

    /** Adds a listener for the status of network connection. */
    private void addConnectivityListener() {
        if (broadcastReceiver == null) {
            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    Bundle extras = intent.getExtras();
                    NetworkInfo info = (NetworkInfo) extras
                            .getParcelable("networkInfo");

                    NetworkInfo.State state = info.getState();
                    
                    if (state == NetworkInfo.State.CONNECTED) {
                    	setConnectivity(true);
                    } else {
                    	setConnectivity(false);
                    }
                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }
    
    /** Remove the listener for the status of network connection. */
    private void removeConnectivityListener() {
    	unregisterReceiver(broadcastReceiver);
    }
    
    /** Defines the connectivity preference. */
    public void setConnectivity(boolean status) {
    	Context context = this.getApplicationContext();
    	/** Create, if not exists, the preference GraviolaMOB. */
        SharedPreferences settings = context.getSharedPreferences("GraviolaMOB", MODE_PRIVATE);
        /** Create an editor to set the preference connectivity. */
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("connectivity", status);
        editor.commit();
    }
    
    /** Checks whether there is an authenticated user. */
    public boolean isAuthenticated() {
    	Context context = this.getApplicationContext();
    	/** Create, if not exists, the preference GraviolaMOB. */
        SharedPreferences settings = context.getSharedPreferences("GraviolaMOB", MODE_PRIVATE);
        /** Check the preference isAuthenticated and return false if is not set. */
        return settings.getBoolean("isAuthenticated", false);
    }
    
    /** Defines which screen and what controls should be loaded. */
    private void showMain() {
    	
    	/** Setting values of dynamic items of the title bar. */
        this.title.setText("Main");        
        this.icon.setImageResource(R.drawable.icon);
        
    	if (this.isAuthenticated())
    	{
        	/** Setting values of dynamic items of the title bar. */
        	this.header.setImageResource(R.drawable.title_logo_white);
        	/** Show screen of authenticated user*/
        	showAuthenticatedMain();
    	}
        else {
        	/** Setting values of dynamic items of the title bar. */
        	this.header.setImageResource(R.drawable.icon);
        	/** Show screen of unauthenticated user*/
        	showUnauthenticatedMain();
        }        	

        /** Setting join button, and your behavior. */
    	Button transport_button = (Button) findViewById(R.id.transport_button);
        transport_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { 
                /** Create an intent to perform an operation, this case call an Activity. */
            	Intent myIntent = new Intent(view.getContext(), Transport.class);
            	/** Start an activity and wait for a result. */
                startActivityForResult(myIntent, TRANSPORT_ACTIVITY);
                /** Transition of the screens. */
                overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out); 
            }
        });
    }
    
    /** Show screen of authenticated user. */
    private void showAuthenticatedMain() {
        setContentView(R.layout.main_authenticated);
    }

   /** Show screen of unauthenticated user. */
   private void showUnauthenticatedMain() {
        setContentView(R.layout.main_unauthenticated);

        /** Setting join button, and your behavior. */
        Button join_button = (Button) findViewById(R.id.join_button);
        join_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { 
                /** Create an intent to perform an operation, this case call an Activity. */
            	Intent myIntent = new Intent(view.getContext(), Register.class);
            	/** Start an activity and wait for a result. */
                startActivityForResult(myIntent, REGISTER_ACTIVITY);
                /** Transition of the screens. */
                overridePendingTransition(R.anim.pull_in_down,R.anim.push_up_out); 
            }
        });
    }
}