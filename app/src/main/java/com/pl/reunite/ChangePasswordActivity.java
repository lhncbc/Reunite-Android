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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pl.reunite.Result.RestChangePasswordResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ChangePasswordActivity extends Activity implements View.OnClickListener{
    EditText editTextOldPassword;
    EditText editTextNewPassword1;
    EditText editTextNewPassword2;
    Button buttonChangePassword;

    String oldP, newP1, newP2;

    ReUnite app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        app = ((ReUnite) getApplication());
        Initialize();
    }

    private void Initialize() {
        editTextOldPassword = (EditText) findViewById(R.id.editTextOldPassword);
        editTextNewPassword1 = (EditText) findViewById(R.id.editTextNewPassword1);
        editTextNewPassword2 = (EditText) findViewById(R.id.editTextNewPassword2);

        buttonChangePassword = (Button) findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonChangePassword:
                changePassword();
                break;
            default:
                break;
        }
    }

    private void changePassword() {
        oldP = editTextOldPassword.getText().toString();
        newP1 = editTextNewPassword1.getText().toString();
        newP2 = editTextNewPassword2.getText().toString();

        // if there empty
        if (oldP.isEmpty() || newP1.isEmpty() || newP2.isEmpty()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.implemented))
                    .setTitle(getResources().getString(R.string.error))
                    .setCancelable(false)
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else if (!newP1.contentEquals(newP2)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.two_passwords_are_not_identical))
                    .setTitle(getResources().getString(R.string.error))
                    .setCancelable(false)
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            new ChangePasswordRestCallAsyncTask(this, app.getToken(), app.getUsername(), oldP, newP1).execute();
        }
    }

    public RestChangePasswordResult changePasswordRestCall(Context c, final String token, final String email, final String oldPassword, final String newPassword) throws Exception {
        RestChangePasswordResult rest = new RestChangePasswordResult(ChangePasswordActivity.this);

        WebServer ws = new WebServer(app.getWebServer());
        String url = ws.getRestEndpoint();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildChangePasswordJson(c, token, email, oldPassword, newPassword);

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
            }
            else {
                rest.searchErrorMessage();
            }
        }
        return rest;
    }

    //    {"call":"change","token":"123","user":"bob@aol.com","old":"AwtfyRBz5sim","new":"ZwtFyRcz5BJZ"}
    public JSONObject buildChangePasswordJson(Context c, String token, String email, String oldP, String newP) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "change");
            json.put("token", token);
            json.put("user", email);
            json.put("old", oldP);
            json.put("new", newP);
        }
        catch (Exception ex){
            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    private class ChangePasswordRestCallAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        private RestChangePasswordResult rest;

        private Context c;
        private String token;
        private String email;
        private String oldPassword;
        private String newPassword;

        String returnStr = "";
        ProgressDialog progressDialog;


        ChangePasswordRestCallAsyncTask(Context c, final String token, final String email, final String oldPassword, final String newPassword){
            this.c = c;
            this.token = token;
            this.email = email;
            this.oldPassword = oldPassword;
            this.newPassword = newPassword;
        }
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage("Confirmation starting...");
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
                    rest = changePasswordRestCall(c, token, email, oldPassword, newPassword);
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
                // define the message

                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                builder.setMessage(getResources().getString(R.string.confirmation_was_successful))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.succeed))
                        .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                Intent i = new Intent(ChangePasswordActivity.this, ReUniteActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(i);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChangePasswordActivity.this);
                builder.setMessage(rest.searchErrorMessage())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.error))
                        .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

}
