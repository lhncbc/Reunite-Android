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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This is to
 * 1. clean event list in database
 * 2. search event list from web server
 * 3. save the update event list into database.
 */

public class CacheActivity extends Activity implements View.OnClickListener {
    ReUnite app;
    String webServer = "";
    
	private PatientsDataSource datasource;
	private Record cacheRecord = new Record();
 
    Button cleanButton;
    TextView tvNumber;
    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();
		
		setContentView(R.layout.cache);
		
		Initialize();
	}

	private void Initialize() {
		cleanButton = (Button) findViewById(R.id.buttonClean);
		tvNumber = (TextView) findViewById(R.id.textViewNumber);
		
		cleanButton.setOnClickListener(this);

    	datasource = new PatientsDataSource(this);
        datasource.open();
		cacheRecord.setCount(CountCacheRecords());
		tvNumber.setText(String.valueOf(cacheRecord.getCount()));
        datasource.close();
	}

	private int CountCacheRecords() {
		return datasource.countCacheRecords();
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.buttonClean:
			CleanPhotos();
			break;
		}
	}
	
    private void CleanPhotos() {
		AlertDialog.Builder builder = new AlertDialog.Builder(CacheActivity.this);
		builder.setMessage(getResources().getString(R.string.you_are_about_to_delete_all_the_photos_saved_in_cache))
			   .setTitle(getResources().getString(R.string.are_you_sure_q))
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   datasource.open();
		        	   datasource.deleteAllCachePatients();
		        	   cacheRecord.setCount(CountCacheRecords());
		        	   tvNumber.setText(String.valueOf(cacheRecord.getCount()));
		        	   datasource.close();
		        	   
		               Toast.makeText(CacheActivity.this, getResources().getString(R.string.all_photos_are_deleted), Toast.LENGTH_SHORT).show();
		           }
		       })
		       .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
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
		Intent i = new Intent(CacheActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

	private void Latency2() {
		Intent i = new Intent(CacheActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}

	private void GoBackToMainPage() {
		Intent i = new Intent(CacheActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}    
}
