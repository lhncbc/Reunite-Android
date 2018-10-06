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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.pl.reunite.Result.RestRegisterResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Activity for new user to register.
 */

public class RegisterActivity extends Activity implements View.OnClickListener {
	ReUnite app;

	String webServer = "";
	String soapAction = "";
	String nameSpace = "";
	String url = "";

	Button buRegister;
//	EditText etFirstName;
//	EditText etLastName;
	EditText etEmail;
//	EditText etUsername;
	EditText etPassword1;
//	EditText etPassword2;
	CheckBox ckShow;

	// returned message
	private String registered;
	private String errorCode;
	private String errorMessage;

	ProgressDialog progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		app = ((ReUnite)this.getApplication());
		app.detectMobileDevice(this);
		app.setScreenOrientation(this);

		webServer = app.getWebServer();

		Initialize();
	}

	private void Initialize() {
		buRegister = (Button)findViewById(R.id.buttonRegister);
//		etFirstName = (EditText)findViewById(R.id.editTexFirstName);
//		etLastName = (EditText)findViewById(R.id.editTexLastName);
		etEmail = (EditText)findViewById(R.id.editTextEmail);
//		etUsername = (EditText)findViewById(R.id.editTextUsername);
		etPassword1 = (EditText)findViewById(R.id.editTextPassword1);
//		etPassword2 = (EditText)findViewById(R.id.editTextPassword2);
		ckShow = (CheckBox)findViewById(R.id.checkBoxShow);

		buRegister.setOnClickListener(this);
		ckShow.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch(v.getId()){
			case R.id.checkBoxShow:
				ShowPassword();
				break;
			case R.id.buttonRegister:
//				Register();
				new RegisterRestCallAsyncTask(this).execute();
				break;
			default:
				// It won't.
				break;
		}
	}

	private void ShowPassword() {
		if (ckShow.isChecked() == true){
			etPassword1.setTransformationMethod(null);
//			etPassword2.setTransformationMethod(null);
		}
		else {

			etPassword1.setTransformationMethod(new PasswordTransformationMethod());
//			etPassword2.setTransformationMethod(new PasswordTransformationMethod());
		}
	}

	private void Register() {
		// Verify the rules
		if (VerifyPasswords() == true){
//			WebServer ws = new WebServer(app.getWebServer()); // modified in version 7.1.3
//			RegisterUserResult registerUserResult = ws.RegisterUser(app.getTokenAnonymous(), etEmail.getText().toString(), etUsername.getText().toString(), etPassword1.getText().toString(), etFirstName.getText().toString(), etLastName.getText().toString());
//			if (registerUserResult.getErrorCode().equalsIgnoreCase("0") == true){
				/**
				 * Return to home screen automatically.
				 * Added in version 6.1.3
				 */
//				CleanEntry();
//				AlertDialog.Builder builder = new AlertDialog.Builder(this);
//				builder.setMessage(getResources().getString(R.string.registration_is_successful_))
//						.setCancelable(false)
//						.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								dialog.cancel();
//								GoBackToMainPage();
//							}
//						});
//				AlertDialog alert = builder.create();
//				alert.show();
//			}
//			else {
				// supporting multiple languages - start
//				ErrorMessage em = new ErrorMessage(this);
				// end

//				MyErrorMessageBox(errorMessage, getResources().getString(R.string.retry));
//			}
		}
	}

	private void GoBackToMainPage() {
		Intent i = new Intent(this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	private void CleanEntry() {
//		etFirstName.setText("");
//		etLastName.setText("");
		etEmail.setText("");
//		etUsername.setText("");
		etPassword1.setText("");
//		etPassword2.setText("");
	}

	private boolean VerifyPasswords() {
//		if (etFirstName.getText().length() == 0) {
//			MyErrorMessageBox(getResources().getString(R.string.first_name_is_empty), getResources().getString(R.string.retry));
//			return false;
//		}
//		if (etLastName.getText().length() == 0) {
//			MyErrorMessageBox(getResources().getString(R.string.last_name_is_empty), getResources().getString(R.string.retry));
//			return false;
//		}
		if (etEmail.getText().length() == 0) {
			MyErrorMessageBox(getResources().getString(R.string.email_is_empty), getResources().getString(R.string.retry));
			return false;
		}
//		if (etUsername.getText().length() == 0) {
//			MyErrorMessageBox(getResources().getString(R.string.username_is_empty), getResources().getString(R.string.retry));
//			return false;
//		}
		if (etPassword1.getText().length() == 0) {
			MyErrorMessageBox(getResources().getString(R.string.password_is_empty), getResources().getString(R.string.retry));
			return false;
		}
//		if (etPassword2.getText().length() == 0) {
//			MyErrorMessageBox(getResources().getString(R.string.confirmation_password_is_empty), getResources().getString(R.string.retry));
//			return false;
//		}

		// Verify password
		// Identical
		String strP1 = etPassword1.getText().toString();
//		String strP2 = etPassword2.getText().toString();

//		if (strP1.equals(strP2) == false) {
			// Display the error and return;
//			MyErrorMessageBox(getResources().getString(R.string.two_passwords_are_not_identical), getResources().getString(R.string.retry));
//			return false;
//		}

		// length of passwords
		if (strP1.length() < 8) {
			MyErrorMessageBox(getResources().getString(R.string.password_requires_at_least_8_characters), getResources().getString(R.string.retry));
			return false;
		}
		if (strP1.length() > 16) {
			MyErrorMessageBox(getResources().getString(R.string.password_cannot_have_more_than_16_characters), getResources().getString(R.string.retry));
			return false;
		}

		// At least one char is uppercase
		boolean found = false;
		for (int i = 0; i < strP1.length(); i++){
			if (strP1.charAt(i) >= 'A' && strP1.charAt(i) <= 'Z'){
				found = true;
				break;
			}
		}
		if (found == false){
			MyErrorMessageBox(getResources().getString(R.string.password_must_have_at_least_one_uppercase_character), getResources().getString(R.string.retry));
			return false;
		}

		// Must have at least one lowercase character
		found = false;
		for (int i = 0; i < strP1.length(); i++){
			if (strP1.charAt(i) >= 'a' && strP1.charAt(i) <= 'z'){
				found = true;
				break;
			}
		}
		if (found == false){
			MyErrorMessageBox(getResources().getString(R.string.password_must_have_at_least_one_lowercase_character), getResources().getString(R.string.retry));
			return false;
		}

		// Must have at least one numeral (0-9)
		found = false;
		for (int i = 0; i < strP1.length(); i++){
			if (strP1.charAt(i) >= '0' && strP1.charAt(i) <= '9'){
				found = true;
				break;
			}
		}
		if (found == false){
			MyErrorMessageBox(getResources().getString(R.string.password_must_have_at_least_one_numeral_), getResources().getString(R.string.retry));
			return false;
		}

		// Password cannot contain your username
//		String strUsername = etUsername.getText().toString();
//		if (strUsername.indexOf(etUsername.toString(), 0) >= 0){
//			MyErrorMessageBox(getResources().getString(R.string.password_cannot_contain_your_username), getResources().getString(R.string.retry));
//			return false;
//		}
		return true;
	}

	private void MyErrorMessageBox(String message, String buttonText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setNegativeButton(buttonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	public RestRegisterResult registerRestCall() throws Exception {
		RestRegisterResult rest = new RestRegisterResult(RegisterActivity.this);

		WebServer ws = new WebServer(app.getWebServer());
		String url = ws.getRestEndpoint();
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");

		JSONObject jo = buildRegisterJson(this);

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
//				rest.setToken2(jsonResult.get("token2").toString());
//				app.setToken2(rest.getToken2());
			}
			else {

			}
		}
		return rest;
	}

	public JSONObject buildRegisterJson(Context c) {
		JSONObject json = new JSONObject();
		try {
			json.put("call", "register");
			json.put("token", app.getTokenAnonymous());
			json.put("email", etEmail.getText().toString());
			json.put("pass", etPassword1.getText().toString());
			json.put("captcha", "1234567890");
			json.put("inactive", true);
		}
		catch (Exception ex){
			Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
			return null;
		}
		return json;
	}

	//To use the AsyncTask, it must be subclassed
	private class RegisterRestCallAsyncTask extends AsyncTask<Void, Integer, Void>
	{
		private RestRegisterResult rest;
		private String errorCode;
		private String errorMessage;

		String returnStr = "";
		ProgressDialog progressDialog;

		private int func;
		private Context c;

		RegisterRestCallAsyncTask(Context c){
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
					rest = registerRestCall();
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
			String msg = "registration was successful. You will receive an email from us shortly. Please open the email and click the link to complete the registration.";
			if (rest.getErrorCode().equalsIgnoreCase("0")){
				AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
				builder.setMessage(msg)
						.setIcon(android.R.drawable.ic_dialog_info)
						.setCancelable(true)
						.setTitle(getResources().getString(R.string.succeed))
						.setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								app.setUsername(etEmail.getText().toString());
								Intent i = new Intent(RegisterActivity.this, ReUniteActivity.class);
								startActivity(i);
								finish();

							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
			else {
				RestRegisterResult r = new RestRegisterResult(RegisterActivity.this);
				r.setErrorCode(r.getErrorCode());
				AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
				builder.setMessage(r.searchErrorMessage())
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

	@Override
	protected void onResume() {
		super.onResume();

		// tracker
		app.tracker().setScreenName(app.TRACK_REGISTER);
		app.tracker().send(new HitBuilders.ScreenViewBuilder().build());

	}
}
