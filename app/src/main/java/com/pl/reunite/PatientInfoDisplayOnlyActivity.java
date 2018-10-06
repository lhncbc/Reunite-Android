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
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12/5/2014.
 * This Activity is for display only. It does not provide tool to
 * change the contents. Jus for the all details of a lost person.
 * Version 9.2.10: remove
 */
public class PatientInfoDisplayOnlyActivity extends Activity implements View.OnClickListener {
//	String currentSelectedEvent = "";

    ReUnite app;
    String webServer = "";

    ImageView ivPatientPhoto;
//    TextView tvPrimarySecondary;
//    TextView tvImageIndex;
//    Button btMoveLeft;
//    Button btMoveRight;
    private List<Image> images;
    private int curSelImage;

//    TextView tvFamilyName;
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

    TextView textViewPersonOrAnimal;
    TextView textViewId;
    TextView textViewBuddy;

//    Button buPuuid;
//    Button buSave;

    Patient p;

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

        setContentView(R.layout.patient_info_display_only);

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
        app.tracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void Initialize() {
        // photo section - start
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        ivPatientPhoto = (ImageView)findViewById(R.id.ivPatientPhoto);
        ivPatientPhoto.setImageResource(R.drawable.questionmark);

        images = new ArrayList<Image>();
        curSelImage = 0;
        // photo section - end

//        tvFamilyName = (TextView)findViewById(R.id.tvFamilyName);
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

        textViewPersonOrAnimal = (TextView)findViewById(R.id.textViewPersonOrAnimal);
        textViewBuddy = (TextView)findViewById(R.id.textViewBuddy);

        textViewPersonOrAnimal = (TextView)findViewById(R.id.textViewPersonOrAnimal);
        textViewId = (TextView)findViewById(R.id.textViewId);
        textViewBuddy = (TextView)findViewById(R.id.textViewBuddy);

//        buPuuid = (Button)findViewById(R.id.buttonPuuid);
//        buPuuid.setOnClickListener(this);

        /**
         * change to the simple
         */
        // start to use the simple way
        p = app.getCurSelPatient();
        images = p.getImages();
        // use the simple way - end

//        tvFamilyName.setText(p.getFamilyName());
//        tvGivenName.setText(p.getGivengName());
        String fullName = p.getFullName();
        if (fullName.isEmpty() || fullName.equalsIgnoreCase("unk")){
            fullName = getResources().getString(R.string.unknown);
        }
        tvFullName.setText(fullName);

        /**
         * rewrite the age section
         * version 7.2.7-beta, versionCode 70207000
         */
        // start
        String strAge = p.getYearsOld();
        if (strAge.equalsIgnoreCase("null")){
            strAge = getResources().getString(R.string.unknown);
        }
        else if (strAge.equalsIgnoreCase("-1") == true){
            strAge = getResources().getString(R.string.unknown);
        }
        tvAge.setText(strAge);
        // end

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

        // just use existing photo.
        // 9.3.0
 //       new LoadViewTask().execute();
        progressBar.setVisibility(View.GONE);
        ivPatientPhoto.setVisibility(View.VISIBLE);
        if (p.getPhoto() != null){
            ivPatientPhoto.setImageBitmap(p.getPhoto());
        }

        if (p.getPa() == Patient.PERSON){
            textViewPersonOrAnimal.setText("Person");
        }
        else {
            textViewPersonOrAnimal.setText("Animal");
            if (p.getBuddy().isEmpty() == false){
                textViewBuddy.setText(p.getBuddy());
            }
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


    public void onClick(View v) {
        switch(v.getId()){
            /*
            case R.id.buttonPuuid:
                GoLpWebForPuuid(tvPuuid.getText().toString());
                break;
                */
            case R.id.ivPatientPhoto:
                Toast.makeText(PatientInfoDisplayOnlyActivity.this, "Photo is clicked.", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }

    }

    public void setMyImage() {
        if (images.size() > 0) {
            Image i = new Image(images.get(curSelImage));

            Bitmap tmp = Bitmap.createBitmap(i.getPhotoBitmap(), 1, 1, i.getPhotoBitmap().getWidth()-1, i.getPhotoBitmap().getHeight()-1);
            /*
            Canvas canvas = new Canvas(tmp);
            canvas.drawBitmap(tmp, 0,0, null);

            if (i.getNumberOfFacesDetected() >= 1){
                Paint facePaint = new Paint();
                facePaint.setColor(Color.GREEN);
                facePaint.setStyle(Paint.Style.STROKE);
                facePaint.setStrokeWidth(1); // changed from 3 to 1 in version 9.0.0

                Rect r = i.getRect();
                canvas.drawRect(
                        r.getX(),
                        r.getY(),
                        r.getX() + r.getW(),
                        r.getY() + r.getH(),
                        facePaint
                );
            }

            // Draw frame to tell primary or secondary photo
            Paint primaryPaint = new Paint();
            primaryPaint.setColor(Color.WHITE);
            primaryPaint.setStyle(Paint.Style.STROKE);
            primaryPaint.setStrokeWidth(1);

            int size = 24;
            primaryPaint.setTextSize(size);
            String tx;
            if (curSelImage == 0){
//                tx = Image.PRIMARY_IMAGE;
                tx = getResources().getString(R.string.primary_image);
            }
            else {
//                tx = Image.SECONDARY_IMAGE;
                tx = getResources().getString(R.string.secondary_image);
            }
            canvas.drawText(tx, 0, size, primaryPaint);
            */
            ivPatientPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
        }
        // no image
        else {
            ivPatientPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionmark));
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

        PatientsDataSource ds = new PatientsDataSource(this);
        ds.open();
        p = ds.createPatient(p);
        if (p.getSerialId() == -1){
            Toast.makeText(PatientInfoDisplayOnlyActivity.this, getResources().getString(R.string.file_is_saved), Toast.LENGTH_SHORT).show();
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

    private class SaveToLocalDbAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(PatientInfoDisplayOnlyActivity.this);
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

    // Menu sections
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
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
            case R.id.itemContactUs:
                new EmailUs(this).start();
                break;
        }
        return true;
    }

    private void Tutorials() {
        Intent i = new Intent(PatientInfoDisplayOnlyActivity.this, TutorialListActivity.class);
        startActivity(i);
    }

    private void Latency2() {
        Intent i = new Intent(PatientInfoDisplayOnlyActivity.this, LatencyActivity.class);
        i.putExtra("webServer", webServer);
        startActivity(i);
    }

    private void GoBackToMainPage() {
        Intent i = new Intent(PatientInfoDisplayOnlyActivity.this, ReUniteActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }
}

