package com.graviola.nojamforme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Toast;
import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Transport extends GraviolaAndroidProject {
	/** Constants to determine what dialog is being referred to. */
	static final int PROGRESS_DIALOG = 0;
	
	static final int MENU_TRANSPORT_TYPES = 1;
	static final int MENU_ROUTES = 2;
	
	private static final String MAP_URL = "http://felipenv.dyndns.org:8098/graviola/web/api/maps";

	private Handler mHandler;

	private String transportFilterJSON, transportTypeFilterJSON, transportRouteFilterJSON;
	
	private Boolean reloadMenu;

	private WebView webView;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transport);

    	/** Setting values of dynamic items of the title bar. */
        this.title.setText("Transport");
        this.icon.setImageResource(R.drawable.icon);
        
        reloadMenu = false; 
        
        if (isConnected()) {
            mHandler = new Handler();
            ApiTransportFilter.start();
        	setupWebView();
        } else {
        	Toast.makeText(Transport.this, "You are not connected. The map can't be downloaded!", Toast.LENGTH_SHORT).show();
        }
    }
    
    /** Called when an dialog is created.*/
    protected Dialog onCreateDialog(int id) {
        ProgressDialog progressDialog;

        switch(id) {
        /** Define an dialog for PROGRESS_DIALOG dialog. */
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(Transport.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.loading_map));
            return progressDialog;
        default:
            return null;
        }
    }

    /** Called before the option menu is shown. */ 
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		
		if(reloadMenu) {
			menu.clear();
			JSONArray jsonArray;

			try {
				// TODO correct to consider transport types
				/**if (transportTypeFilterJSON.length() > 0 ) {
					SubMenu transportType = menu.addSubMenu(0, 1, 0, "Transport types");

					jsonArray = new JSONArray(transportTypeFilterJSON);
					for (int i = 0; i < jsonArray.length(); i++) {
						 JSONObject jsonObject = jsonArray.getJSONObject(i);
						 transportType.add(0, jsonObject.getInt("id"), i, jsonObject.getString("description"));
					}
					transportType.setGroupCheckable(0, true, true);
				}**/
				if (transportRouteFilterJSON.length() > 0 ) {
					SubMenu transportRoute = menu.addSubMenu(0, MENU_ROUTES, 1, "Routes");
					
					jsonArray  = new JSONArray(transportRouteFilterJSON);
					for (int i = 0; i < jsonArray.length(); i++) {
						 JSONObject jsonObject = jsonArray.getJSONObject(i);
						 transportRoute.add(MENU_ROUTES, jsonObject.getInt("id"), i, jsonObject.getString("description"));
					}
					transportRoute.setGroupCheckable(MENU_ROUTES, true, true);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
			}
			reloadMenu = false;
		}
		
		return true;
    }

	/** Handle option menu item selection. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getGroupId()) {
      case MENU_ROUTES:
    	  if (!item.isChecked())
    		  webView.loadUrl("javascript:loadRoute(" + item.getItemId() + ")");
      default:
          if (item.isChecked()) item.setChecked(false);
          else {
              item.setChecked(true);
          }
          return super.onOptionsItemSelected(item);
      }
    }
    
    /** Checks if the mobile is connected to the internet to show gooogle maps. */
    public boolean isConnected() {
    	Context context = this.getApplicationContext();
    	/** Create, if not exists, the preference GraviolaMOB. */
        SharedPreferences settings = context.getSharedPreferences("GraviolaMOB", MODE_PRIVATE);
        /** Check the preference connectivity and return false if is not set. */
        return settings.getBoolean("connectivity", false);
    }

    /** Sets up the WebView object and loads the URL of the page **/
    private void setupWebView(){
   	
		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		/** Create an WebViewClient to add and remove an progress dialog*/
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				showDialog(PROGRESS_DIALOG);
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				removeDialog(PROGRESS_DIALOG); 
			}
		});
		webView.loadUrl(MAP_URL);
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
    
    private Thread ApiTransportFilter = new Thread() {
        // Create a new HttpClient 
		HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://felipenv.dyndns.org:8098/graviola/web/api/transport-filter.json");

        public void run() {
        	transportFilterJSON = "";

            try {
                // Execute HTTP Get Request
                HttpResponse response = httpclient.execute(httpGet);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
    			if (statusCode == 200) {
    				transportFilterJSON = inputStreamToString(response.getEntity().getContent()).toString();
    			} else {
    				// TODO write log
    			}
                mHandler.post(transportFilterExecute);
            } catch (Exception e) {
            	// TODO Auto-generated catch block
            }
        }
    };
 
    private Runnable transportFilterExecute = new Runnable(){
        public void run(){
        	if (transportFilterJSON.length() > 0 ) {
        		
	            // TODO Save data of user in the database. 
	            try {
	    			JSONObject jsonObject = new JSONObject(transportFilterJSON);

	    			transportTypeFilterJSON = jsonObject.getString("vehicle_types");
	    			transportRouteFilterJSON = jsonObject.getString("routes");
	    			// TODO prepare filters to menu

	    			reloadMenu = true;
	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    		}
        	}
        	else {
	            Toast.makeText(Transport.this, "Unable to load the filters.", Toast.LENGTH_LONG).show();
        	}
        }
    };
}