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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.pl.reunite.Result.RestConfirmationResult;
import com.pl.reunite.Result.RestResetResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ConfirmationActivity extends Activity implements View.OnClickListener {
    final static int CALL_REGISTER = 0;
    final static int CALL_FORGOT = 1;

    ReUnite app;
    String webServer = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";

    String confirmationCode;
    String currentUsername = "Guest";
    String currentPassword = "";

    String errorCode;
    String errorMessage;
    String returnString = "";

    Button buttonConfirm;

    int currentCall;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        app = ((ReUnite) this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        webServer = app.getWebServer();
        Initialize();
    }

    private void Initialize() {
        buttonConfirm = (Button) findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(this);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        String str = data.toString();
        if (str.indexOf("reset") > 0){
            currentCall = CALL_FORGOT;
        }
        else if (str.indexOf("confirm") > 0){
            currentCall = CALL_REGISTER;
        }
        else {
            // do nothing, until find a solution
        }
        int start = str.indexOf("=");
        int end = str.length();
        if (end > start) {
            confirmationCode = str.substring(start + 1);
        }
        else {
            confirmationCode = "";
        }
    }

    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.buttonConfirm:
                if (currentCall == CALL_REGISTER) {
                    new ConfirmationRestCallAsyncTask(ConfirmationActivity.this, confirmationCode).execute();
                }
                else if (currentCall == CALL_FORGOT){
                    new ResetRestCallAsyncTask(ConfirmationActivity.this, confirmationCode).execute();
                }
                else {

                }
            break;
            default:
                // It won't.
                break;
        }
    }

    public RestConfirmationResult confirmationRestCall() throws Exception {
        RestConfirmationResult rest = new RestConfirmationResult(ConfirmationActivity.this);

        WebServer ws = new WebServer(app.getWebServer());
        String url = ws.getRestEndpoint();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildConfirmationJson(this);

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

    public JSONObject buildConfirmationJson(Context c) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "confirm");
            json.put("token", app.getTokenAnonymous());
            json.put("confirmation", confirmationCode);
        }
        catch (Exception ex){
            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    //To use the AsyncTask, it must be subclassed
    private class ConfirmationRestCallAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        private RestConfirmationResult rest;
        private String errorCode;
        private String errorMessage;
        private String confirmationCode;

        String returnStr = "";
        ProgressDialog progressDialog;

        private int func;
        private Context c;

        ConfirmationRestCallAsyncTask(Context c, String confirmationCode){
            this.func = func;
            this.c = c;
            this.confirmationCode = confirmationCode;
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
                    rest = confirmationRestCall();
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

                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmationActivity.this);
                builder.setMessage(getResources().getString(R.string.confirmation_was_successful))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.succeed))
                        .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                Intent i = new Intent(ConfirmationActivity.this, ReUniteActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(i);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmationActivity.this);
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

    public RestResetResult resetRestCall() throws Exception {
        RestResetResult rest = new RestResetResult(ConfirmationActivity.this);

        WebServer ws = new WebServer(app.getWebServer());
        String url = ws.getRestEndpoint();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildResetJson(this);

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

//    {"call":"reset","token":"123","confirmation":"e71df6d876e3aacac907s07295983b9d9406f9ff2556ba9cca4792ac5c323e971"}
    public JSONObject buildResetJson(Context c) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "reset");
            json.put("token", app.getTokenAnonymous());
            json.put("confirmation", confirmationCode);
        }
        catch (Exception ex){
            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    private class ResetRestCallAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        private RestResetResult rest;
        private String errorCode;
        private String errorMessage;
        private String confirmationCode;

        String returnStr = "";
        ProgressDialog progressDialog;

        private int func;
        private Context c;

        ResetRestCallAsyncTask(Context c, String confirmationCode){
            this.func = func;
            this.c = c;
            this.confirmationCode = confirmationCode;
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
                    rest = resetRestCall();
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

                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmationActivity.this);
                builder.setMessage(getResources().getString(R.string.confirmation_was_successful))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.succeed))
                        .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                Intent i = new Intent(ConfirmationActivity.this, ReUniteActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                startActivity(i);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfirmationActivity.this);
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
