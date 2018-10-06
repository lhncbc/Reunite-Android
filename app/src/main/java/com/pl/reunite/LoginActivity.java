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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.pl.reunite.Result.RestUserResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends Activity implements View.OnClickListener {
    ReUnite app;
    String webServer = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";

    String currentUsername = "Guest";
	String currentPassword = "";

	String errorCode;
	String errorMessage;
	String returnString = "";
	
	Button buEnter;
    Button buForgotUsername;
	Button buRegister;
	CheckBox ckShow;
	
	EditText etUsername;
	EditText etPassword;

    ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        webServer = app.getWebServer();
        /*
		if (webServer.equalsIgnoreCase(ReUnite.SPRINT_MOBIL_WEBSERVER) == true){
		    nameSpace = NAMESPACE_SPRINT_MOBILE;
		    url = URL_SPRINT_MOBILE;
		    soapAction = SOAP_ACTION_SPRINT_MOBILE;
		}
		else {
		    nameSpace = NAMESPACE_PL;
		    url = URL_PL;
		    soapAction = SOAP_ACTION_PL;
		}
		*/
        
        Initialise();
	}

	private void Initialise() {

		buEnter = (Button)findViewById(R.id.buEnter);
        buForgotUsername = (Button)findViewById(R.id.buForgotUsername);
		buRegister = (Button)findViewById(R.id.buRegister);
		ckShow = (CheckBox)findViewById(R.id.ckShow);
		
		etUsername = (EditText)findViewById(R.id.editUsername); 
		etPassword = (EditText)findViewById(R.id.editPassword); 
/*
		if (webServer.equalsIgnoreCase(ReUnite.SPRINT_MOBIL_WEBSERVER) == true){
			etUsername.setText("********");
			etPassword.setText("********");
		}
		else {
//			etUsername.setText("********");
//			etPassword.setText("********");
		}
*/
		buEnter.setOnClickListener(this);
        buForgotUsername.setOnClickListener(this);
		buRegister.setOnClickListener(this);
		ckShow.setOnClickListener(this);
		
		currentUsername = app.getUsername();
		currentPassword = app.getPassword();
	}

	public void onClick(View v) {

		switch(v.getId()){
            case R.id.buEnter:
            	// verify entry first
				currentUsername = etUsername.getText().toString();
				currentPassword = etPassword.getText().toString();
				String errMsg = verifyEntry();
				if (errMsg.isEmpty() == false){
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(errMsg)
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setCancelable(true)
							.setTitle(getResources().getString(R.string.error))
							.setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									return;
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
				else {
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(etUsername.getWindowToken(), 0);
					imm.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
//                new EnterAsyncTask().execute();
					new RestUserAsyncTask(this).execute();
				}
                break;
            case R.id.ckShow:
                ShowPassword();
                break;
            case R.id.buForgotUsername:
                ForgotUsername();
                break;
            case R.id.buRegister:
                Register();
                break;
            default:
                // It won't.
                break;
        }
	}

	private void ForgotUsername() {
        Intent i = new Intent("com.pl.reunite.FORGOTUSERNAMEACTIVITY");
		startActivity(i);
	}

	private void Register() {
		// SOAP
		Intent i = new Intent("com.pl.reunite.REGISTERACTIVITY");
		startActivity(i);
	}

	private void ShowPassword() {
		if (ckShow.isChecked() == true){
			etPassword.setTransformationMethod(null);
		}
		else {
			etPassword.setTransformationMethod(new PasswordTransformationMethod());			
		}
	}

	public RestUserResult userRestCall() throws Exception {
		RestUserResult rest = new RestUserResult(LoginActivity.this);

		WebServer ws = new WebServer(app.getWebServer());
		String url = ws.getRestEndpoint();
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");

		JSONObject jo = buildUserJson(this);

		// Send post request
		con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(jo.toString());

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		bw.write(jo.toString());
		bw.flush();
		bw.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + jo.toString());
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		String jsonStr = "";
		while ((inputLine = in.readLine()) != null) {
			jsonStr = inputLine;
			if (jsonStr.contains("error")){
				jsonStr = inputLine;
			}
			else {
				jsonStr = "";
			}
		}
		in.close();

		JSONObject jsonResult = null;
		if (!jsonStr.isEmpty()) {
			jsonResult = new JSONObject(jsonStr);
			rest.setErrorCode(jsonResult.get("error").toString());
			if (rest.getErrorCode().equalsIgnoreCase("0") == true){
				rest = extractUserJson(LoginActivity.this, jsonResult, rest);
			}
			else {
				rest.searchErrorMessage();
			}
		}
		return rest;
	}

	public JSONObject buildUserJson(Context c) {
		JSONObject json = new JSONObject();
		try {
			json.put("call", "user");
			json.put("user", currentUsername.toString());
			json.put("pass", currentPassword.toString());
		}
		catch (Exception ex){
			Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
			return null;
		}
		return json;
	}
	
	public RestUserResult extractUserJson(Context c, JSONObject j, RestUserResult rest) throws JSONException {
		try {
			rest.setToken(j.get("token").toString());
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
		}
		try {
			rest.setGid(j.get("gid").toString());
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
		}
		return rest;
	}

	// to replace EnterAsyncTask
	private class RestUserAsyncTask extends AsyncTask<Void, Integer, Void> {
		private RestUserResult rest;
		private String errorCode;
		private String errorMessage;

		String returnStr = "";
		ProgressDialog progressDialog;

		private int func;
		private Context c;

		RestUserAsyncTask(Context c){
			this.func = func;
			this.c = c;
		}
		//Before running code in separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(c);
			progressDialog.setMessage("Register starting...");
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}

		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params)
		{
			//Get the current thread's token
			synchronized (this)
			{
				try {
					rest = userRestCall();
				} catch (InterruptedException e) {
					e.printStackTrace();
					returnStr = e.getMessage();
				} catch (Exception e) {
					e.printStackTrace();
					returnStr = e.getMessage();
				}
			}
			return null;
		}

		//Update the progress
		@Override
		protected void onProgressUpdate(Integer... values)
		{
		}

		//after executing the code in the thread
		@Override
		protected void onPostExecute(Void result)
		{
			progressDialog.dismiss();
			if (rest.getErrorCode().equalsIgnoreCase("0")){
				app.setAuthStatus(true);
				app.setUsername(currentUsername);
				app.setPassword(currentPassword);
				app.setToken(rest.getToken());
				app.setTokenStatus(ReUnite.TOKEN_AUTH);
				app.setGroupId(rest.getGid());
				app.setGroupNameWithGroupId();
				String msg = "Welcome \"" + app.getUsername() + "\"!";
				Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();

				Credential d = new Credential();
				d.setUsernamePreferences(currentUsername);
				d.setPasswordPreferences(currentPassword);
				d.setGroupNamePreferences(app.getGroupName());
				CleanEntry();

				Intent backData = new Intent();
				backData.putExtra("username", currentUsername);
				backData.putExtra("password", currentPassword);

				// Save to cridential
				SaveToCredentialData(currentUsername, currentPassword);

				setResult(LoginActivity.this.RESULT_CANCELED, backData);
				LoginActivity.this.finish();
			}
			else {
					AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
					builder.setMessage(rest.getErrorMessage())
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setCancelable(true)
							.setTitle(getResources().getString(R.string.error))
							.setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
			}
		}
	}

	protected void SaveToCredentialData(String currentUsername, String currentPassword) {
		Credential credential = new Credential();
		credential.setUsernamePreferences(currentUsername);
		credential.setPasswordPreferences(currentPassword);
        app.setUsername(currentUsername);
        app.setPassword(currentUsername);
	}

	public String verifyEntry() {
		String errMsg = "";
		if (currentUsername.isEmpty()) {
			errMsg = getResources().getString(R.string.email_is_empty);
			return errMsg;
		}
		if (currentPassword.isEmpty()) {
			errMsg = getResources().getString(R.string.password_is_empty);
			return errMsg;
		}
		return errMsg;
	}

	private void CleanEntry() {

		etUsername.setText("");
		etPassword.setText("");		
	}

    public class Credential {
        public static final String GUEST = "Guest";

        private boolean bAuthStatus;
		private String username;
		private String password;
        private String groupName;
		private String webServer;
		private boolean quickStart;
		
		Credential(){
			reset();// initial
			getUsernamePreferences();
			getPasswordPreferences();
            getGroupNamePreferences();
			getAuthStatus();
			getWebServer();
			getQuickStartPreferences();
		}
		
		public String getUsername(){
			return username;
		}
		
		public String getPassword(){
			return password;
		}

        public String getGroupName(){
            return groupName;
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
		
		public boolean getQuickStart(){
			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true){
				quickStart = false;
			}
			else {
				quickStart = true;				
			}
			return quickStart;			
		}
		
		public String getWebServer(){
			return webServer;
		}

		public void reset() {
			bAuthStatus = false;
			username = GUEST;
			password = "";
            groupName = "";
//			saveUserPreferences(username, password);
            webServer = WebServer.PL_NAME;
//			saveWebServerPreferences(webServer);
			quickStart = false;
		}
		
		private void saveQuickStartPreferences(boolean quickStart) {
			this.quickStart = quickStart;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("quickStart", this.quickStart);
			editor.commit();
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

			String encrapedUsername;
			if (this.username.isEmpty() == true){
				encrapedUsername = null;
			}
			else {
				encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
		    }

			String encrapedPassword;
			if (this.password.isEmpty() == true){
				encrapedPassword = null;
			}
			else {
				encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
		    }
			
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", encrapedUsername);
			editor.putString("password", encrapedPassword);
			editor.commit();
		}
		
		private void setUsernamePreferences(String username){
			this.username = username;
			
			String encrapedUsername;
			if (this.username.isEmpty() == true){
				encrapedUsername = null;
			}
			else {
				encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
		    }
			
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", encrapedUsername);
			editor.commit();
		}
		  
		private void setPasswordPreferences(String password){
			this.password = password;

			String encrapedPassword;
			if (this.password.isEmpty() == true){
				encrapedPassword = "";
			}
			else {
				encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
		    }
						
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("password", encrapedPassword);
			editor.commit();
		}

        private void setGroupNamePreferences(String groupName){
            this.groupName = groupName;

            String encrapedGroupName;
            if (this.groupName.isEmpty() == true){
                encrapedGroupName = "";
            }
            else {
                encrapedGroupName = Base64.encodeToString(this.groupName.getBytes(), Base64.DEFAULT );
            }

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("groupName", encrapedGroupName);
            editor.commit();
        }

		private void setWebServerPreferences(String webServer){
			this.webServer = webServer;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("webServer", this.webServer);
			editor.commit();
		}

		String getUsernamePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			String encryptedUsername = sharedPreferences.getString("username", "guest");
			if (encryptedUsername.equalsIgnoreCase("Guest") == true){
				this.username = new String("Guest");
			}
			else {
				this.username = new String(Base64.decode(encryptedUsername, Base64.DEFAULT ) );
			}
			return this.username;
		}
	
		String getPasswordPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			String encryptedPassword = sharedPreferences.getString("password", "");
			if (encryptedPassword.isEmpty() == true){
				this.password = new String("");
			}
			else {
				this.password = new String(Base64.decode(encryptedPassword, Base64.DEFAULT ) );				
			}
			return this.password;
		}

        String getGroupNamePreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            String encryptedGroupName = sharedPreferences.getString("groupName", "");
            if (encryptedGroupName.isEmpty() == true){
                this.groupName = new String("");
            }
            else {
                this.groupName = new String(Base64.decode(encryptedGroupName, Base64.DEFAULT ) );
            }
            return this.groupName;
        }

		private String getWebServerPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.webServer = sharedPreferences.getString("webServer", WebServer.PL_NAME);
			return this.webServer;
		}
		
		private boolean getQuickStartPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.quickStart = sharedPreferences.getBoolean("quickStart", ReUnite.QUICK_START_DEFAULT);
			return this.quickStart;
		}
    }

	@Override
	protected void onResume() {
		super.onResume();

		// tracker
		app.tracker().setScreenName(app.TRACK_LOGIN);
		app.tracker().send(new HitBuilders.ScreenViewBuilder().build());
	}
}
