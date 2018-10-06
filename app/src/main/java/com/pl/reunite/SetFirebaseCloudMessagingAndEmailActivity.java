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
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.reunite.Result.PingEchoResult;
import com.pl.reunite.Result.RestMessageResult;

public class SetFirebaseCloudMessagingAndEmailActivity extends Activity implements View.OnClickListener{

    RestMessageResult restMessageResult;

    CheckBox checkBoxRecordPush;
    CheckBox checkBoxRecordEmail;
    CheckBox checkBoxEventPush;
    CheckBox checkBoxEventEmail;
    CheckBox checkBoxAdminPush;
    CheckBox checkBoxAdminEmail;

    View viewAdmin;
    TextView textViewAdmin;

    private ProgressDialog progressDialog = null;

    ReUnite app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_firebase_cloud_messaging_and_email);

        app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        initialize();
    }

    private void initialize() {
        checkBoxRecordPush = (CheckBox) findViewById(R.id.checkBoxNotificationRecordUpdate);
        checkBoxRecordEmail = (CheckBox) findViewById(R.id.checkBoxEmailRecordUpdate);
        checkBoxEventPush = (CheckBox) findViewById(R.id.checkBoxNotificationNewEvent);
        checkBoxEventEmail = (CheckBox) findViewById(R.id.checkBoxEmailNewEvent);
        checkBoxAdminPush = (CheckBox) findViewById(R.id.checkBoxAdminPush);
        checkBoxAdminEmail = (CheckBox) findViewById(R.id.checkBoxAdminEmail);

        viewAdmin = (View) findViewById(R.id.viewAdmin);
        textViewAdmin = (TextView) findViewById(R.id.textViewAdmin);

        checkBoxRecordPush.setOnClickListener(this);
        checkBoxRecordEmail.setOnClickListener(this);
        checkBoxEventPush.setOnClickListener(this);
        checkBoxEventEmail.setOnClickListener(this);
        checkBoxAdminPush.setOnClickListener(this);
        checkBoxAdminEmail.setOnClickListener(this);

        // if is admin
        if (app.getGroupName().equalsIgnoreCase(ReUnite.USER)){
            viewAdmin.setVisibility(View.GONE);
            textViewAdmin.setVisibility(View.GONE);
            checkBoxAdminPush.setVisibility(View.GONE);
            checkBoxAdminEmail.setVisibility(View.GONE);
        }

        // retrieve the settings from web server
        restMessageResult = new RestMessageResult(this);
        restMessageResult = callMessage(app.getToken(), true, restMessageResult);
        if (restMessageResult.getErrorCode().contains("0")) {
            checkBoxRecordPush.setChecked(restMessageResult.isRecordPush());
            checkBoxRecordEmail.setChecked(restMessageResult.isRecordEmail());
            checkBoxEventPush.setChecked(restMessageResult.isEventPush());
            checkBoxEventEmail.setChecked(restMessageResult.isEventEmail());

            if (app.getGroupName().equalsIgnoreCase(ReUnite.ADMINISTRATOR)){
                checkBoxAdminPush.setChecked(restMessageResult.isAdminPush());
                checkBoxAdminEmail.setChecked(restMessageResult.isAdminEmail());
            }
        }
        else {
            Toast.makeText(this, "Error: " + restMessageResult.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkBoxNotificationRecordUpdate:
                restMessageResult.setRecordPush(checkBoxRecordPush.isChecked());
                break;
            case R.id.checkBoxEmailRecordUpdate:
                restMessageResult.setRecordEmail(checkBoxRecordEmail.isChecked());
                break;
            case R.id.checkBoxNotificationNewEvent:
                restMessageResult.setEventPush(checkBoxEventPush.isChecked());
                break;
            case R.id.checkBoxEmailNewEvent:
                restMessageResult.setEventEmail(checkBoxEventEmail.isChecked());
                break;
            case R.id.checkBoxAdminPush:
                restMessageResult.setAdminPush(checkBoxAdminPush.isChecked());
                break;
            case R.id.checkBoxAdminEmail:
                restMessageResult.setAdminEmail(checkBoxAdminEmail.isChecked());
                break;
            default:
                break;
        }

        // retrieve the settings from web server
        new CallMessageAsyncTask(this, app.getToken(), false, restMessageResult).execute();
    }

    /**
     * Message
     */
    private RestMessageResult callMessage(String fcmToken, boolean getOrSet, RestMessageResult result) {
        // Add custom implementation, as needed.
        ReUnite app = ((ReUnite)this.getApplication());
        /**
         *
         */
        // start
        WebServer ws = new WebServer(app.getWebServer()); // modified in version 7.1.3
//        com.pl.reunite.WebServer ws = new com.pl.reunite.WebServer();
        // end
        ws.setTokenStatus(app.getTokenStatus());
        ws.setTokenAnonymous(app.getTokenAnonymous());
        ws.setToken(app.getToken());
        //ReportResult reportResult = ws.callSendGcmToken(gsmToken, this);
        restMessageResult = ws.restCallMessage(this, fcmToken, getOrSet, result);
        return restMessageResult;
    }

    //To use the AsyncTask, it must be subclassed
    private class CallMessageAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        PingEchoResult pingEchoResult;
        String returnStr = "";
        ProgressDialog progressDialog;

        private int func;
        private Context c;
        private String fcmToken;
        private boolean getOrSet;
        private RestMessageResult result;

        CallMessageAsyncTask(Context c, String fcmToken, boolean getOrSet, RestMessageResult result){
            this.func = func;
            this.c = c;
            this.fcmToken = fcmToken;
            this.getOrSet = getOrSet;
            this.result = result;
        }
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage(getResources().getString(R.string.processing_please_wait));
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
                    restMessageResult = callMessage(fcmToken, getOrSet, result);
                } catch (Exception e) {
                    e.printStackTrace();
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
            if (restMessageResult.getErrorCode().contains("0")) {
                checkBoxRecordPush.setChecked(restMessageResult.isRecordPush());
                checkBoxRecordEmail.setChecked(restMessageResult.isRecordEmail());
                checkBoxEventPush.setChecked(restMessageResult.isEventPush());
                checkBoxEventEmail.setChecked(restMessageResult.isEventEmail());

                if (app.getGroupName().equalsIgnoreCase(ReUnite.ADMINISTRATOR)){
                    checkBoxAdminPush.setChecked(restMessageResult.isAdminPush());
                    checkBoxAdminEmail.setChecked(restMessageResult.isAdminEmail());
                }
            }
            else {
                Toast.makeText(c, "Error: " + restMessageResult.getErrorMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}
