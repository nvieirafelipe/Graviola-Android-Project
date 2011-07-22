package com.graviola.nojamforme;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class Login extends GraviolaAndroidProject {
	/** Constants to determine what dialog is being referred to. */
	static final int PROGRESS_DIALOG = 0;

	private Handler mHandler;
    
	private String username, password, userJSON;
	private EditText edit_username, edit_password;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

    	edit_username = (EditText) findViewById(R.id.username); 
    	edit_password = (EditText) findViewById(R.id.password);

    	/** Setting values of dynamic items of the title bar. */
        this.title.setText("Login");
        this.icon.setImageResource(R.drawable.icon);

        /** Setting ok & cancel buttons, and your behavior. */
        Button ok_button = (Button) findViewById(R.id.ok_button);
        Button cancel_button = (Button) findViewById(R.id.cancel_button);
        
        /** Validade username & password entered. */
        ok_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	username = edit_username.getText().toString();
                password = edit_password.getText().toString();
            	
            	/** TODO Validate username and password. */
            	if(username.length() > 0 && password.length() > 0) {
                    mHandler = new Handler();
        			showDialog(PROGRESS_DIALOG);
                    ApiLogin.start();
            	} else {
            		/** Provide feedback to the user that login and password you entered were not validated. */
            		AlertDialog.Builder alert_builder = setAlertBuilder("NoJam Login", R.drawable.icon_error, "Username and/or password incorrect.");
            		alert_builder.setCancelable(false)
            					 .setNeutralButton("OK", new DialogInterface.OnClickListener() {
            						 public void onClick(DialogInterface dialog, int id) {
						                Intent intent = new Intent();
						                setResult(RESULT_CANCELED, intent);
						                finish();
						                overridePendingTransition(R.anim.pull_in_down,R.anim.push_up_out); 
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
    
    /** Called when an dialog is created.*/
    protected Dialog onCreateDialog(int id) {
        ProgressDialog progressDialog;

        switch(id) {
        /** Define an dialog for PROGRESS_DIALOG dialog. */
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.connecting_to_the_server));
            return progressDialog;
        default:
            return null;
        }
    }

    /** Define Title, icon and message of a alert dialog.*/
    private AlertDialog.Builder setAlertBuilder(String title, int icon, String message) {
    	AlertDialog.Builder alert_builder = new AlertDialog.Builder(this);
        alert_builder.setTitle(title)
        		.setIcon(icon)
        		.setMessage(message);
        
        return alert_builder;
    }
    
    private StringBuilder inputStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        
        // Wrap a BufferedReader around the InputStream
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));

        // Read response until the end
		try {
			while ((line = rd.readLine()) != null) { 
			    total.append(line); 
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
        
        // Return full string
        return total;
    }

    private Thread ApiLogin = new Thread() {
        // Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://felipenv.dyndns.org:8098/graviola/web/api/login.json");

        public void run() {
        	userJSON = "";

            try {
                // Add your data
            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("username", username));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,HTTP.UTF_8));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
    			if (statusCode == 200) {
    				userJSON = inputStreamToString(response.getEntity().getContent()).toString();
    			} else {
    				// TODO write log
    			}
                mHandler.post(loginExecute);
            } catch (Exception e) {
            	// TODO Auto-generated catch block
            }
        }
    };

    private Runnable loginExecute = new Runnable(){
        public void run(){
        	if (userJSON.length() > 0 ) {
	        	/** Create, if not exists, the preference GraviolaMOB. */
	            SharedPreferences settings = Login.this.getSharedPreferences("GraviolaMOB", MODE_PRIVATE);
	            /** Create an editor to set the preference isAuthenticated to true. */
	            SharedPreferences.Editor editor = settings.edit();
	            editor.putBoolean("isAuthenticated", true);
	            editor.commit();
	            
	            // TODO Save data of user in the database. 
	            try {
	    			JSONObject jsonObject = new JSONObject(userJSON);
	    			/** Create, if not exists, the preference GraviolaMOB. */
		            SharedPreferences userCache = Login.this.getSharedPreferences("sfGuardUser", MODE_PRIVATE);
		            /** Create an editor to set the preference isAuthenticated to true. */
		            SharedPreferences.Editor userCacheEditor = userCache.edit();
		            userCacheEditor.putString("first_name", jsonObject.getString("first_name"));
		            userCacheEditor.putString("last_name", jsonObject.getString("last_name"));
		            userCacheEditor.putString("email_address", jsonObject.getString("email_address"));
		            userCacheEditor.putString("username", jsonObject.getString("username"));
		            userCacheEditor.commit();

		            Toast.makeText(Login.this, "Welcome to NoJam, "+jsonObject.getString("first_name")+"!", Toast.LENGTH_LONG).show();

	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    		}

	            /** Finish the activity. */
	            Intent intent = new Intent();
	            setResult(RESULT_OK, intent);
	            finish();
	            overridePendingTransition(R.anim.pull_in_down,R.anim.push_up_out);
        	}
        	else {
	    		/** Provide feedback to the user that login and password you entered were not validated. */
	    		AlertDialog.Builder alert_builder = setAlertBuilder("NoJam Login", R.drawable.icon_error, "Unable to login!");
	    		alert_builder.setCancelable(false)
	    					 .setNeutralButton("OK", new DialogInterface.OnClickListener() {
	    						 public void onClick(DialogInterface dialog, int id) {
						                Intent intent = new Intent();
						                setResult(RESULT_CANCELED, intent);
						                finish();
						                overridePendingTransition(R.anim.pull_in_down,R.anim.push_up_out); 
	    					 	 }
	    					 });
	    	    AlertDialog alert = alert_builder.create();
	    	    alert.show();
        	}
			removeDialog(PROGRESS_DIALOG); 

        }
    };
}
