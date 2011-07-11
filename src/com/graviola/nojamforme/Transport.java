package com.graviola.nojamforme;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.Bitmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

public class Transport extends GraviolaAndroidProject {
	/** Constants to determine what dialog is being referred to. */
	static final int PROGRESS_DIALOG = 0;
	TextView teste;
	
	private static final String MAP_URL = "http://gmaps-samples.googlecode.com/svn/trunk/articles-android-webmap/simple-android-map.html";
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transport);

        TextView teste =  (TextView) findViewById(R.id.teste);

    	/** Setting values of dynamic items of the title bar. */
        this.title.setText("Transport");
        this.icon.setImageResource(R.drawable.icon);
        
        String readTwitterFeed = readTwitterFeed();
		
        try {
			JSONObject jsonObject = new JSONObject(readTwitterFeed);
			teste.append(jsonObject.getString("markers"));
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        if (isConnected()) {
        	setupWebView();
        }
        else 
        {
        	Toast.makeText(Transport.this, "You are not connected. The map can not be downloaded!", Toast.LENGTH_SHORT).show();
        }
    }
    
	/** Called when the option menu is created. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	
        MenuInflater inflater = getMenuInflater();
        /** Defines custom option menu of the transport screens. */
        inflater.inflate(R.menu.option_menu_transport, menu);        
        return true;
    }

	/** Handle option menu item selection. */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()) {
      case R.id.option_transport_type_bus_checkbox:
    	  /** TODO */
      case R.id.option_transport_type_metro_checkbox:
    	  /** TODO */
      case R.id.option_transport_type_train_checkbox:
    	  /** TODO */
      default:
          if (item.isChecked()) item.setChecked(false);
          else item.setChecked(true);
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

    /** Parse JSON. */
    public String readTwitterFeed() {

		StringBuilder builder = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet("http://felipenv.dyndns.org:8098/graviola/web/routes/route.json?id=5");
		try {
			HttpResponse response = client.execute(httpGet);
			StatusLine statusLine = response.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
			} else {
				teste.setText("Failed to download file");
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}
    
    /** Sets up the WebView object and loads the URL of the page **/
    private void setupWebView(){
    	WebView webView;

		webView = (WebView) findViewById(R.id.webview);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(MAP_URL);
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
}