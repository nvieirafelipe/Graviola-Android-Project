package com.graviola.nojamforme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class Register extends GraviolaAndroidProject {

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

    	/** Setting values of dynamic items of the title bar. */
        this.title.setText("Register");
        this.icon.setImageResource(R.drawable.icon);
        
        /** Setting ok & cancel buttons, and your behavior. */
        Button ok_button = (Button) findViewById(R.id.ok_button);
        Button cancel_button = (Button) findViewById(R.id.cancel_button);
        
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { 
            	/** TODO validate the data entered and confirm the registration.*/
            	Toast.makeText(Register.this, "Welcome to NoJam, your registration has been completed!", Toast.LENGTH_SHORT).show();
            	Intent myIntent = new Intent(view.getContext(), Main.class);
                startActivityForResult(myIntent, 0);
                overridePendingTransition(R.anim.push_left_in,R.anim.push_right_out); 
            }
        });

        cancel_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) { 
        		/** Confirm if user wants to cancel the registration. */
            	AlertDialog.Builder alert_builder = setAlertBuilder("Registration", R.drawable.icon_exclamation, "Are you sure you want to cancel? all data will be lost!");
            	alert_builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
 	               public void onClick(DialogInterface dialog, int id) {
		               	Intent intent = new Intent();
		                setResult(RESULT_CANCELED, intent);
		                finish();
		                overridePendingTransition(R.anim.pull_in_down,R.anim.push_up_out); 
	               }  
            	})
            				 .setNegativeButton("No", new DialogInterface.OnClickListener() {
					         	public void onClick(DialogInterface dialog, int id) {
					            	dialog.dismiss();
					            } 
				             });

            	AlertDialog alert = alert_builder.create();
        	    alert.show();
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
}
