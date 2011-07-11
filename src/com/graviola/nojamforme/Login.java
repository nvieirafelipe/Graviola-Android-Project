package com.graviola.nojamforme;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends GraviolaAndroidProject {
	
	/** . */
	private String username, password;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

    	/** Setting values of dynamic items of the title bar. */
        this.title.setText("Login");
        this.icon.setImageResource(R.drawable.icon);

        /** Setting ok & cancel buttons, and your behavior. */
        Button ok_button = (Button) findViewById(R.id.ok_button);
        Button cancel_button = (Button) findViewById(R.id.cancel_button);
        
        /** Validade username & password entered. */
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	EditText edit_usename = (EditText) findViewById(R.id.username); 
            	EditText edit_password = (EditText) findViewById(R.id.password);
            			
            	username = edit_usename.getText().toString();
                password = edit_password.getText().toString();
            	
            	/** TODO Validate username and password. */
            	if(username.length() > 0 && password.length() > 0) {
                    Toast.makeText(Login.this, "Welcome, Username!", Toast.LENGTH_SHORT).show();
                    loginSuccess();
            	} else {
            		/** Provide feedback to the user that login and password you entered were not validated. */
            		AlertDialog.Builder alert_builder = setAlertBuilder("Authentication", R.drawable.icon_error, "Unable to login!");
            		alert_builder.setCancelable(false)
            					 .setNeutralButton("OK", new DialogInterface.OnClickListener() {
            						 public void onClick(DialogInterface dialog, int id) {
            							 /**TODO */
            					 	 }
            					 });
            	    AlertDialog alert = alert_builder.create();
            	    alert.show();
            	}
            }
        });
        
        /** Finish the activity returning canceled result. */
        cancel_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
                overridePendingTransition(R.anim.pull_in_down,R.anim.push_up_out); 
            }
        });
    }
    
    /** Define Title, icon and message of a alert dialog.*/
    private AlertDialog.Builder setAlertBuilder(String title, int icon, String message) {
    	AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setTitle(title)
        		.setIcon(icon)
        		.setMessage(message);
        
        return alert_builder;
    }
    
    private void loginSuccess() {
    	Context context = this.getApplicationContext();
    	/** Create, if not exists, the preference GraviolaMOB. */
        SharedPreferences settings = context.getSharedPreferences("GraviolaMOB", MODE_PRIVATE);
        /** Create an editor to set the preference isAuthenticated to true. */
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isAuthenticated", true);
        editor.commit();
        
        /** Finish the activity. */
        Intent intent = new Intent();
         setResult(RESULT_OK, intent);
         finish();
         overridePendingTransition(R.anim.pull_in_down,R.anim.push_up_out); 
    }
}
