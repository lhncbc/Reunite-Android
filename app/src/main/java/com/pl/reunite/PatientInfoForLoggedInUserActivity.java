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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.reunite.Result.RestCommentResult;
import com.pl.reunite.Result.RestFollowRecordResult;
import com.pl.reunite.Result.RestFollowResult;
import com.pl.reunite.Result.RestReportAbuseResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * This is to re-report lost person records.
 * For these records can updated here. But Draft is disabled,
 * because it involve synchronisation problem.So I took Draft out here.
 * the latest changed made in version 6.1.0
 * Version 9.2.10:
 * remove move photo next and move photo back functions,
 * remove primary photo indication,
 * remove photo index.
 * full name to replace family and given names.
 */

public class PatientInfoForLoggedInUserActivity  extends Activity implements View.OnClickListener {
//	String currentSelectedEvent = "";
    private static final int LOGIN_REQUEST = 1;

    ReUnite app;
    String webServer = "";

    ImageView ivPatientPhoto;
//    TextView tvPrimarySecondary;
//    TextView tvImageIndex;
//    Button btMoveLeft;
//    Button btMoveRight;
    private List<Image> images;
    private int curSelImage;

//	TextView tvFamilyName;
//    TextView tvGivenName;
    TextView tvFullName;
	TextView tvAge;
	TextView tvGender;
	TextView tvStatus;
	TextView tvEvent;
	TextView tvPuuid;
	TextView tvLastSeenLocation;
	TextView tvStatusSaharaUpdated;
	TextView tvComments;

	Button buUpdate;
	Button butterComment;
//	Button buPuuid;
	Button buSave;
    Button buAbuse;

    TextView textViewPersonOrAnimal;
    TextView textViewId;
    TextView textViewBuddy;

    int curFollowStatus = FollowRecord.FOLLOW_OFF;
//    Button buttonFollowRecord;
    CheckBox checkBoxFollowRecord;
	
	Patient p;

    private int abuseSel = 0;

    ProgressBar progressBar;
	//A ProgressDialog object  
    private ProgressDialog progressDialog;  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();
		
		setContentView(R.layout.patient_info_for_logged_in_user);
		
		Initialize();

	}

	@Override
	protected void onResume() {
		super.onResume();

        // tracker
        // version 7.3.4.00
        if (app.getCurSelPatient() != null) {
            app.tracker().setScreenName(app.TRACK_RECORD_VIEW + " (" + app.getCurSelPatient().getEventShortName() + ")");
        }
        else if (app.getLastEvent() != null){
            app.tracker().setScreenName(app.TRACK_RECORD_VIEW + " (" + app.getLastEvent().getShortName() + ")");
        }
        else {
            app.tracker().setScreenName(app.TRACK_RECORD_VIEW + " (" + getResources().getString(R.string.unknown) + ")");
        }

    }
	
	private void Initialize() {
        // photo section - start
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        ivPatientPhoto = (ImageView)findViewById(R.id.ivPatientPhoto);
        ivPatientPhoto.setImageResource(R.drawable.questionmark);

        images = new ArrayList<Image>();
        curSelImage = 0;
        // photo section - end
		
//		tvFamilyName = (TextView)findViewById(R.id.tvFamilyName);
//        tvGivenName = (TextView)findViewById(R.id.tvGivenName);
        tvFullName = (TextView)findViewById(R.id.tvFullName);
		tvAge = (TextView)findViewById(R.id.tvAge);
		
		tvGender = (TextView)findViewById(R.id.tvGerder);
		tvStatus = (TextView)findViewById(R.id.tvStatus);
		tvEvent = (TextView)findViewById(R.id.tvEvent);
		tvPuuid = (TextView)findViewById(R.id.tvPuuid);
		tvLastSeenLocation = (TextView)findViewById(R.id.tvLastSeenLocation);	
		
		tvStatusSaharaUpdated = (TextView)findViewById(R.id.tvLastUpdatedDate);
		tvComments = (TextView)findViewById(R.id.tvAdditionalDetails);

		buUpdate = (Button)findViewById(R.id.buttonUpdate);
		butterComment = (Button)findViewById(R.id.buttonComment);
//		buPuuid = (Button)findViewById(R.id.buttonPuuid);
        buSave = (Button)findViewById(R.id.buttonSave);
        buAbuse = (Button)findViewById(R.id.buttonAbuse);

		buUpdate.setOnClickListener(this);
        butterComment.setOnClickListener(this);
//		buPuuid.setOnClickListener(this);
        buSave.setOnClickListener(this);
        buAbuse.setOnClickListener(this);

        textViewPersonOrAnimal = (TextView)findViewById(R.id.textViewPersonOrAnimal);
        textViewId = (TextView)findViewById(R.id.textViewId);
        textViewBuddy = (TextView)findViewById(R.id.textViewBuddy);

        /**
         * change to the simple
         */
        // start to use the simple way
        p = app.getCurSelPatient();
        images = p.getImages();
        // use the simple way - end

        String fullName = p.getFullName();
        if (fullName.isEmpty() || fullName.equalsIgnoreCase("unk")){
            fullName = getResources().getString(R.string.unknown);
        }
//	    tvFamilyName.setText(p.getFamilyName());
//        tvGivenName.setText(p.getGivengName());
        tvFullName.setText(fullName);

        /**
         * rewrite the age section
         * version 7.2.7-beta, versionCode 70207000
         */
        // more describe-able on age
	    String strAge = p.getYearsOld();
        if (strAge.equalsIgnoreCase("null")){
            strAge = getResources().getString(R.string.unknown);
        }
		else if (strAge.equalsIgnoreCase("-1") == true){
            strAge = getResources().getString(R.string.unknown);
        }
	    tvAge.setText(strAge);
	    
	    // more describe-able on gender
	    String strGender = p.getGender();
	    strGender = Patient.convertGenderToLongForm(this, strGender);
        tvGender.setText(strGender);
        
	    // more describe-able on status
        String strStatusShort = p.getOptStatus();
        String strStatusLong = Patient.convertStatusToLongForm(this, strStatusShort);
        int color = Patient.getColorFromStatusArray(this, strStatusShort);
        tvStatus.setText(strStatusLong);
        tvStatus.setTextColor(color);

		// add a line under the lastseen
		String str = p.getLastSeen();
		if (str.isEmpty() == true || str.equalsIgnoreCase("null") == true){
//            str = "None\n";
            str = getResources().getString(R.string.none);
			tvLastSeenLocation.setText(str);
		}
		else {
			tvLastSeenLocation.setText(p.getLastSeen());
		}
        tvEvent.setText(p.getEventName());
        tvPuuid.setText(p.getPatientUuid());
        tvStatusSaharaUpdated.setText(p.getStatusSahanaUpdated());
        
		String com = p.getComments();
		if (com.isEmpty() == true || com.equalsIgnoreCase("null") == true){
            com = getResources().getString(R.string.none);
			tvComments.setText(com);
		}
		else {
			tvComments.setText(p.getComments());
		}


        /**
         * follow records
         * added in version 7.1.6
         */
        // start
//        buttonFollowRecord = (Button)findViewById(R.id.buttonFollowRecord);
//        buttonFollowRecord.setOnClickListener(this);

        checkBoxFollowRecord = (CheckBox) findViewById(R.id.checkBoxFollowRecord);
        checkBoxFollowRecord.setOnClickListener(this);

        curFollowStatus = FollowRecord.FOLLOW_OFF;
        app.setFollowRecordList(followingRecords());
        if (!app.getFollowRecordList().isEmpty())
        {
            for (int i = 0; i < app.getFollowRecordList().size(); i++){
                FollowRecord f = app.getFollowRecordList().get(i);
                if (f.getUuid().equalsIgnoreCase(p.getPatientUuid())){
                    curFollowStatus = FollowRecord.FOLLOW_ON;
                    break;
                }
            }
        }
        if (curFollowStatus == FollowRecord.FOLLOW_ON)
        {
            checkBoxFollowRecord.setChecked(true);
        }
        else
        {
            checkBoxFollowRecord.setChecked(false);
        }
        // end of follow record

        // use the simple way - start
//        new LoadViewTask().execute();
        // use the simple way - end

        // just use existing photo.
        // 9.3.0
        progressBar.setVisibility(View.GONE);
        ivPatientPhoto.setVisibility(View.VISIBLE);
        if (p.getPhoto() != null){
            ivPatientPhoto.setImageBitmap(p.getPhoto());
        }

        if (p.getPa() == Patient.PERSON){
            textViewPersonOrAnimal.setText("Person");
            textViewId.setVisibility(View.INVISIBLE);
            textViewBuddy.setVisibility(View.INVISIBLE);
        }
        else {
            textViewPersonOrAnimal.setText("Animal");
            if (p.getBuddy().isEmpty() == false){
                textViewBuddy.setText(p.getBuddy());
            }
        }
    }

    public List<FollowRecord> parsingFollowRecord(String inString){
        List<FollowRecord> followRecordList = new ArrayList<FollowRecord>();

        String string = "followRecord";
        String toParseString = "{" + "\"" + string + "\":" + inString + "}";
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(toParseString);
            JSONArray jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject o = jsonArray.getJSONObject(i); // create a single event jsonObject
                FollowRecord fr = new FollowRecord();
                fr.setName(o.getString("name").toString());
                fr.setUuid(o.getString("uuid").toString());
                fr.setUrl(o.getString("url").toString());
                followRecordList.add(fr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }     // create a json object from a string

        return followRecordList;
    }

    private List<FollowRecord> followingRecords() {
        List<FollowRecord> followRecordList = new ArrayList<FollowRecord>();
        WebServer ws = new WebServer(app.getWebServer()); // Add the argument in. Modified in version 7.1.6
        ws.setTokenStatus(app.getTokenStatus());
        ws.setToken(app.getToken());
//        FollowRecordResult followRecordResult = ws.callFollowList(this);
        RestFollowRecordResult followRecordResult = null;
        try {
            followRecordResult = ws.restCallFollowList(this, app.getToken(), app.getLanguageCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (followRecordResult.getErrorCode().equalsIgnoreCase("0")){
            followRecordList = parsingFollowRecord(followRecordResult.getRecords());
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(followRecordResult.getErrorMessage());
            builder.setCancelable(false);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    return;
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return followRecordList;
    }

    public void onClick(View v) {
		switch(v.getId()){
            case R.id.buttonUpdate:
                new UpdateAsyncTask().execute();
                break;
            case R.id.buttonAbuse:
                restAbuse();
                break;
            case R.id.ivPatientPhoto:
                Toast.makeText(PatientInfoForLoggedInUserActivity.this, "Photo is clicked.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonSave:
                new SaveToLocalDbAsyncTask().execute();
                break;
            case R.id.checkBoxFollowRecord:
                if (curFollowStatus == FollowRecord.FOLLOW_ON) {
                    curFollowStatus = FollowRecord.FOLLOW_OFF;
                    checkBoxFollowRecord.setChecked(false);
                }
                else {
                    curFollowStatus = FollowRecord.FOLLOW_ON;
                    checkBoxFollowRecord.setChecked(true);
                }
                new followRecordAsyncTask(p.getPatientUuid(), curFollowStatus, PatientInfoForLoggedInUserActivity.this).execute();
                break;
            case R.id.buttonComment:
                addComment();
                break;
		default:
			break;
		}
	}

    private void followRecord(final String uuid, final int curFollowStatus, final Context c) {
        WebServer ws = new WebServer(app.getWebServer()); // Add the argument in. Modified in version 7.1.6
        ws.setTokenStatus(app.getTokenStatus());
        ws.setToken(app.getToken());
//        ReportResult reportResult = ws.callFollowRecord(uuid, curFollowStatus, c);
        RestFollowResult restFollowResult = ws.restCallFollowRecord(c, app.getToken(), uuid, checkBoxFollowRecord.isChecked());
//        return reportResult;
    }

    private void restAbuse() {
        // abuse
        final String [] abuseReasons = {
                getResources().getString(R.string.spam),
                getResources().getString(R.string.sexually_explicit),
                getResources().getString(R.string.hate_speech),
                getResources().getString(R.string.violence),
                getResources().getString(R.string.something_else)};

        // Creating and Building the Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.what_would_you_like_to_report));
        builder.setSingleChoiceItems(abuseReasons, abuseSel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                abuseSel = item;
            }});
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                new RestReportAbuseAsyncTask(PatientInfoForLoggedInUserActivity.this, p.getPatientUuid(), abuseSel).execute();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(PatientInfoForLoggedInUserActivity.this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show();
            }
        });
        AlertDialog d = builder.create();
        d.show();
    }

    public JSONObject buildReportAbuseJSON(final String token, final String uuid, final int type){
        JSONObject json = new JSONObject();
        try {
            json.put("token", token);
            json.put("call", "abuse");
            json.put("uuid", uuid);
            json.put("type", type);
        }
        catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public RestReportAbuseResult restReportAbuse(String uuid, int type) throws Exception{
        RestReportAbuseResult restResult = new RestReportAbuseResult(this);

        WebServer ws = new WebServer(app.getWebServer());
        String url = ws.getRestEndpoint();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("content-type", "application/json;  charset=utf-8");

        String tor = "";
        if (app.getAuthStatus() == true){
            tor = app.getToken();
        }
        else {
            tor = app.getTokenAnonymous();
        }
        JSONObject jo = buildReportAbuseJSON(tor, uuid, type);
        String json = jo.toString();

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        bw.write(json);
        bw.flush();
        bw.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post json : " + json);
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
            restResult.setErrorCode(jsonResult.get("error").toString());
            if (restResult.getErrorCode().equalsIgnoreCase("0")){
                return restResult;
            }
            else {
                restResult.searchErrorMessage();
            }
        }
        return restResult;
    }

    private class RestReportAbuseAsyncTask extends AsyncTask<Void, Integer, Void> {
        private RestReportAbuseResult restReportAbuseResult;
        private String uuid;
        private int type;
        Context c;

        RestReportAbuseAsyncTask(Context c, String uuid, int type){
            this.c = c;
            this.uuid = uuid;
            this.type = type;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            restReportAbuseResult = new RestReportAbuseResult(PatientInfoForLoggedInUserActivity.this);
            progressDialog = new ProgressDialog(PatientInfoForLoggedInUserActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.reporting_missing_person_please_wait));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            //Get the current thread's token
            synchronized (this) {
                try {
                    restReportAbuseResult = restReportAbuse(uuid, type);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
            //close the progress dialog
            progressDialog.dismiss();
            if (restReportAbuseResult.getErrorCode().equalsIgnoreCase("0")) {
                Toast.makeText(c, c.getResources().getString(R.string.abuse_thanks), Toast.LENGTH_SHORT).show();
            }
            else {
                /**
                 * display error message
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoForLoggedInUserActivity.this);
                String errorMsg = "";
                if (restReportAbuseResult.getErrorMessage().equalsIgnoreCase(getResources().getString(R.string.unknown))){
                    errorMsg = restReportAbuseResult.getErrorCode() + ": " + getResources().getString(R.string.unknown);
                }
                else {
                    errorMsg = restReportAbuseResult.getErrorMessage();
                }
                builder.setMessage(errorMsg)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.error))
                        .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private class UpdateAsyncTask extends AsyncTask<Void, Integer, Void>{
        private ProgressDialog progressDialog;
        private String errorCode;
        private String errorMessage;

        private String SaveToLocalDb() {
            // Convert to data byte
            // Save image
            if (p.getPhoto() != null && p.getPhotoData().isEmpty() == true){
                p.encodePhoto();
            }

            for (int i = 0; i < images.size(); i++){
                Image img = images.get(i);
                if (img.getPhotoBitmap() != null && img.getPhotoData().isEmpty() == true){
                    img.encodePhoto();
                }
            }

            p.setStatusReport(Patient.SAVED);

            PatientsDataSource datasource = new PatientsDataSource(PatientInfoForLoggedInUserActivity.this);
            datasource.open();
            p = datasource.createPatient(p);
            if (p.getSerialId() == -1){
                errorCode = "-1";
                errorMessage = "Person's file is not saved.";
            }
            else {
                errorCode = "0";
                errorMessage = "Person's file is saved successfully.";
            }

            ImageDataSource imageDataSource = new ImageDataSource(PatientInfoForLoggedInUserActivity.this);
            imageDataSource.open();
            for (int i = 0; i < images.size(); i++){
                Image img = images.get(i);
                img.setPatientSerialId(p.getSerialId());
                img.setSequence(i);
                img = imageDataSource.createImage(img, webServer);
                if (img != null){
                    images.set(i, img);
                }
            }
            return errorMessage;
        }

        public UpdateAsyncTask() {
            super();

            errorCode = "";
            errorMessage = "";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = new ProgressDialog(PatientInfoForLoggedInUserActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.saving_data_to_database_please_wait_dot));
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            synchronized (this)
            {
                errorMessage = SaveToLocalDb();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            progressDialog.dismiss();

            if (errorCode.equalsIgnoreCase("0")){
                // To reprocess
                Toast.makeText(PatientInfoForLoggedInUserActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(PatientInfoForLoggedInUserActivity.this, ReportProceedUpdateActivity.class);
                i.putExtra("serialId", String.valueOf(p.getSerialId()));
                final int result=1;
                startActivityForResult(i, result);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientInfoForLoggedInUserActivity.this);
                builder.setMessage(errorMessage)
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.error))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private void addComment() {
        if (WebServer.isOnline(this) == false) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.offline));
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(getResources().getString(R.string.you_do_not_have_internet_connection))
                    .setCancelable(true)
                    .setTitle(getResources().getString(R.string.warning))
                    .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            app.setOffline(true);
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_comment, null);
        final EditText editTextComment = (EditText) v.findViewById(R.id.comment);
//        showPasswordCheckBox.setText("Search All Images");        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(v)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(PatientInfoForLoggedInUserActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String comment = editTextComment.getText().toString();
                        Toast.makeText(PatientInfoForLoggedInUserActivity.this, comment, Toast.LENGTH_SHORT).show();
                        sendComment(PatientInfoForLoggedInUserActivity.this, app.getToken(), app.getCurSelPatient().getPatientUuid(), comment);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void sendComment(Context c, final String token, final String uuid, final String comment) {
        WebServer ws = new WebServer(app.getWebServer()); // version 7.1.3
        RestCommentResult sr = null;
        try {
            sr = ws.restCallComment(c, token, uuid, comment);
        } catch (Exception e) {
            e.printStackTrace();
            sr.setErrorCode("-1");
            sr.setErrorMessage(e.getMessage());
        }
        if (sr.getErrorCode().equals("0")){
            Toast.makeText(PatientInfoForLoggedInUserActivity.this, getResources().getString(R.string.sent), Toast.LENGTH_SHORT).show();
        }
    }

    private void SaveToLocalDb() {
		// Convert to data byte
		// Save image
		if (p.getPhoto() != null && p.getPhotoData().isEmpty() == true){
			p.encodePhoto();
		}

        for (int i = 0; i < images.size(); i++){
            Image img = images.get(i);
            if (img.getPhotoBitmap() != null && img.getPhotoData().isEmpty() == true){
                img.encodePhoto();
            }
        }
		
		p.setStatusReport(Patient.SAVED);

		PatientsDataSource datasource = new PatientsDataSource(this);
        datasource.open();
        p = datasource.createPatient(p);
        if (p.getSerialId() == -1){
            Toast.makeText(PatientInfoForLoggedInUserActivity.this, getResources().getString(R.string.file_is_saved), Toast.LENGTH_SHORT).show();
        }

        ImageDataSource imageDataSource = new ImageDataSource(this);
        imageDataSource.open();
        for (int i = 0; i < images.size(); i++){
            Image img = images.get(i);
            img.setPatientSerialId(p.getSerialId());
            img.setSequence(i);
            img = imageDataSource.createImage(img, webServer);
            if (img != null){
                images.set(i, img);
            }
        }
	}

    // Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.patient_info_for_logged_user_menu, menu);
        return true;
	}	
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.itemMainPage:
        	GoBackToMainPage();
        	break;
        case R.id.itemLatency:
        	Latency2();
        	break;
        case R.id.itemTutorials:
        	Tutorials();
        	break;
            case R.id.itemReportAbuse:
//                Abuse();
                restAbuse();
                break;
        }
        return true;
	}

    private void Tutorials() {
		Intent i = new Intent(PatientInfoForLoggedInUserActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

	private void Latency2() {
		Intent i = new Intent(PatientInfoForLoggedInUserActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}

	private void GoBackToMainPage() {
		Intent i = new Intent(PatientInfoForLoggedInUserActivity.this, ReUniteActivity.class);
		startActivity(i);																		
		finish();
	}

    private class SaveToLocalDbAsyncTask extends AsyncTask<Void, Integer, Void>
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {
            progressDialog = new ProgressDialog(PatientInfoForLoggedInUserActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage(getResources().getString(R.string.save_a_copy_to_local));
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
                    SaveToLocalDb();
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
            //close the progress dialog  
            progressDialog.dismiss();
			finish();
        }  
    }

    private class followRecordAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        private String uuid;
        private int status;
        private Context c;

        public followRecordAsyncTask(String uuid, int status, Context c){
            this.uuid = uuid;
            this.status = status;
            this.c = c;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(PatientInfoForLoggedInUserActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.updating_the_status_please_wait));
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
                followRecord(uuid, status, c);
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
            //close the progress dialog
            progressDialog.dismiss();
        }
    }

}
