/*
 * Informational Notice:
 *
 * This software, the ”TBD,” was developed under contract funded by the National Library of Medicine, which is part of the National Institutes of Health, an agency of the Department of Health and Human Services, United States Government.
 *
 * The license of this software is an open-source BSD license.  It allows use in both commercial and non-commercial products.
 *
 * The license does not supersede any applicable United States law.
 *
 * The license does not indemnify you from any claims brought by third parties whose proprietary rights may be infringed by your usage of this software.
 *
 * Government usage rights for this software are established by Federal law, which includes, but may not be limited to, Federal Acquisition Regulation (FAR) 48 C.F.R. Part52.227-14, Rights in Data—General.
 * The license for this software is intended to be expansive, rather than restrictive, in encouraging the use of this software in both commercial and non-commercial products.
 *
 * LICENSE:
 *
 * Government Usage Rights Notice:  The U.S. Government retains unlimited, royalty-free usage rights to this software, but not ownership, as provided by Federal law.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * •	Redistributions of source code must retain the above Government Usage Rights Notice, this list of conditions and the following disclaimer.
 *
 * •	Redistributions in binary form must reproduce the above Government Usage Rights Notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * •	The names,trademarks, and service marks of the National Library of Medicine, the National Cancer Institute, the National Institutes of Health, and the names of any of the software developers shall not be used to endorse or promote products derived from this software without specific prior written permission.
 */

package com.pl.reunite;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pl.reunite.Result.PingEchoResult;
import com.pl.reunite.Result.RequestAnonTokenResult;
import com.pl.reunite.Result.RestPingResult;

public class LatencyActivity extends Activity  implements View.OnClickListener {
    private static final String LOGIN = "Login";
	private static final String LOGOUT = "Logout";
    private static final String GUEST = "Guest";
    private static final int LOGIN_REQUEST = 1;
    private static final int SELECT_WEBSERVER = 2;

	private ProgressDialog progressDialog = null; 
    
    ReUnite app;
    String webServer = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";
    String soapActionChechAuth = "";
    
    String username = "";
 	String password = "";
 	Credential credential;	
 	
	TextView tvWebServer;
//	RatingBar ratingBar;
	int ratings = 0;
	TextView tvServerLatency;

    ImageView imageViewServerRating;
    TextView textViewServerRating;

    private PingEchoResult pingEchoResult;
    MyPingEcho myPingEcho;

    /**
     * latency indicator
     */

    private static final int UNKNOWN = 0;
    private static final int DISCONNECTED = 1;
    private static final int POOR = 2;
    private static final int GOOD = 3;
    private static final int EXCELLENT = 4;

    private static final int CONNECTED = 2;

    private static final int[] ServerRatingImage = {
            R.drawable.status_unknown,
            R.drawable.status_disconnected,
            R.drawable.status_low,
            R.drawable.status_mid,
            R.drawable.status_high
    };
    private static final int[] ServerRatingColor = {
            Color.BLACK,
            Color.BLACK,
            Color.BLACK
    };

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.latency);

		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        myPingEcho = new MyPingEcho(app.getWebServer());

        Initialize();

		new CheckInternetConnectionAsyncTask().execute();
	}

	private void Initialize() {
		tvWebServer = (TextView)findViewById(R.id.textViewWebServer);

        /**
         * This let the rating bar as a indicator. It is non click able now.
         * version
         */
        /*
        ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar.setFocusable(false);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        */
        tvServerLatency = (TextView)findViewById(R.id.textViewServerLatency);

        imageViewServerRating = (ImageView) findViewById(R.id.imageViewServerRating);
        textViewServerRating = (TextView) findViewById(R.id.textViewServerRating);

        imageViewServerRating.setImageResource(ServerRatingImage[UNKNOWN]);
        textViewServerRating.setText(getServerRatingString(UNKNOWN));
        textViewServerRating.setTextColor(ServerRatingColor[UNKNOWN]);

	    Intent sender = getIntent();
	    webServer = sender.getExtras().getString("webServer");	
	    tvWebServer.setText(webServer);
	}

	public void onClick(View v) {
	}
	
	@Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.latency_menu, menu);
        return true;
    }
    
    boolean bLogin = false;

    @Override
 	public boolean onOptionsItemSelected(MenuItem item) {
         switch (item.getItemId()) {
         case R.id.menu_refresh:
     		new CheckInternetConnectionAsyncTask().execute();
         	break;
         case R.id.itemMainPage:
         	GoBackToMainPage();
         	break;
         case R.id.itemTutorials:
         	Tutorials();
         	break;
         case R.id.itemContactUs:
             new EmailUs(this).start();
         	break;
          }
         return true;
 	}


    private void Tutorials() {
		Intent i = new Intent(LatencyActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

	public class Credential {
		private boolean bAuthStatus;
		private String username;
		private String password;
		private String webServer;
		
		Credential(){
			getUsernamePreferences();
			getPasswordPreferences();
			getAuthStatus();
			getWebServer();
		}
		
		public String getUsername(){
			return username;
		}
		
		public String getPassword(){
			return password;
		}
		
		public boolean getAuthStatus(){
			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true){
				bAuthStatus = false;
			}
			else {
				bAuthStatus = true;				
			}
			return bAuthStatus;
		}
		
		public String getWebServer(){
			return webServer;
		}

		public void reset() {
			bAuthStatus = false;
			username = GUEST;
			password = "";
			saveUserPreferences(username, password);
            webServer = app.getWebServer();
            if (webServer.isEmpty()) {
                webServer = WebServer.PL_NAME;
            }
			saveWebServerPreferences(webServer);
		}
		
		private void saveWebServerPreferences(String webServer) {
			this.webServer = webServer;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("webServer", this.webServer);
			editor.commit();
		}

		public void saveUserPreferences(String username, String password){
			this.username = username;
			this.password = password;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", this.username);
			editor.putString("password", this.password);
			editor.commit();
		}
		
		private void setUsernamePreferences(String username){
			this.username = username;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", this.username);
			editor.commit();
		}
		  
		private void setPasswordPreferences(String password){
			this.password = password;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("password", this.password);
			editor.commit();
		}

		private void setWebServerPreferences(String webServer){
			this.webServer = webServer;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("webServer", this.webServer);
			editor.commit();
		}

		private String getUsernamePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.username = sharedPreferences.getString("username", "guest");
			return this.username;
		}
	
		private String getPasswordPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.password = sharedPreferences.getString("password", "");
			return this.password;
		}

		private String getWebServerPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.webServer = sharedPreferences.getString("webServer", "");
			return this.webServer;
		}
	}

	public RestPingResult CheckInternetConnection() {
        RestPingResult result = new RestPingResult(LatencyActivity.this);
        // replaced by the following codes
        // version 4.0.0.
        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH){
            myPingEcho.setToken(app.getToken());
//            pingEchoResult = myPingEcho.Call();
            result = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }
        else if (app.getTokenStatus() == ReUnite.TOKEN_UNKNOWN){
            getAnonymousToken();
            app.recordTimeWhenGotAnonymousToken();
            myPingEcho.setToken(app.getTokenAnonymous());
//            pingEchoResult = myPingEcho.Call();
            result = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }
        else if (app.getTokenStatus() == ReUnite.TOKEN_ANONYMOUS) {
            if (app.isAnonymousTokenExpired()){
                getAnonymousToken();
                app.recordTimeWhenGotAnonymousToken();
            }
            myPingEcho.setToken(app.getTokenAnonymous());
//            pingEchoResult = myPingEcho.Call();
            result = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }

        return result;

	}

    private class CheckInternetConnectionAsyncTask extends AsyncTask<Void, Integer, Void> {
//        PingEchoResult pingEchoResult;
        RestPingResult restResult;

        //Before running code in separate thread  
        @Override
        protected void onPreExecute() {
            // added in version 4.0.0
            if (app.getTokenStatus() == ReUnite.TOKEN_UNKNOWN) {
                getAnonymousToken();
                app.recordTimeWhenGotAnonymousToken();
            }

            restResult = new RestPingResult(LatencyActivity.this);
//            RestPingResult.toDefault();

            tvServerLatency.setText(getResources().getString(R.string.detecting_the_speed_please_wait));
//			ratingBar.setRating(0);
            imageViewServerRating.setImageResource(ServerRatingImage[DISCONNECTED]);
            textViewServerRating.setText(getServerRatingString(DISCONNECTED));
            textViewServerRating.setTextColor(ServerRatingColor[DISCONNECTED]);

            progressDialog = new ProgressDialog(LatencyActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.detecting_the_speed_please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.  
        @Override
        protected Void doInBackground(Void... params) {
            //Get the current thread's token  
            synchronized (this) {
                restResult = CheckInternetConnection();
            }
            return null;
        }

        //Update the progress  
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        //after executing the code in the thread  
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            if (restResult.getErrorCode().equalsIgnoreCase("0") == false) {
                String errMsg = restResult.getErrorMessage();
                AlertDialog.Builder builder = new AlertDialog.Builder(LatencyActivity.this);
                builder.setMessage(errMsg)
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.latency))
                        .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                tvServerLatency.setText(errMsg);
            } else {
                tvServerLatency.setText(myPingEcho.getLatencyTime() + "ms");
                int r = Integer.valueOf(myPingEcho.getLatencyTime());
                if (r < 500) {
                    imageViewServerRating.setImageResource(ServerRatingImage[EXCELLENT]);
                    textViewServerRating.setText(getServerRatingString(EXCELLENT));
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
                else if (r < 750) {
                    imageViewServerRating.setImageResource(ServerRatingImage[GOOD]);
                    textViewServerRating.setText(getServerRatingString(GOOD));
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
                else if (r < 1000) {
                    imageViewServerRating.setImageResource(ServerRatingImage[POOR]);
                    textViewServerRating.setText(getServerRatingString(POOR));
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                }
                else {
                    imageViewServerRating.setImageResource(ServerRatingImage[DISCONNECTED]);
                    textViewServerRating.setText(getServerRatingString(DISCONNECTED));
                    textViewServerRating.setTextColor(ServerRatingColor[DISCONNECTED]);
                }
            }
        }
    }

    public String getAnonymousToken(){
        String errMsg = "";
        // get the anonymous token
        WebServer ws = new WebServer(app.getWebServer()); // Modified in version 7.1.3
        RequestAnonTokenResult requestAnonTokenResult = ws.restCallRequestAnonToken(this);
        if (requestAnonTokenResult.getErrorCode().equalsIgnoreCase("0") == true){
            app.setTokenAnonymous(requestAnonTokenResult.getTokenAnonymous());
            app.setTokenStatus(ReUnite.TOKEN_ANONYMOUS);
            app.recordTimeWhenGotAnonymousToken();
            return requestAnonTokenResult.getErrorCode();
        }
        else {
            errMsg = "";
        }
        return errMsg;
    }

    private void GoBackToMainPage() {
		Intent i = new Intent(LatencyActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}

    public String getServerRatingString(int index){
        String status = "";
        switch (index){
            case 0:
                status = getResources().getString(R.string.unknown);
                break;
            case 1:
                status = getResources().getString(R.string.disconnected);
                break;
            case 2:
                status = getResources().getString(R.string.poor);
                break;
            case 3:
                status = getResources().getString(R.string.good);
                break;
            case 4:
                status = getResources().getString(R.string.excellent);
                break;
            default:
                status = getResources().getString(R.string.unknown);
                break;
        }
        return status;
    }
}
