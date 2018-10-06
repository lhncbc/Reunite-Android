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
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ViewActivity extends Activity implements View.OnClickListener {
    ReUnite app;
    String webServer = "";
    
    ViewSettings viewSettings;
	
    RadioGroup radioGroupPhoto;
    RadioButton radioButtonSelPhotoOnly;
    RadioButton radioButtonSelNoPhoto;
    RadioButton radioButtonSelBoth;
    
//    SeekBar seekBarViewNumber;
//    TextView textViewNumber; 
//    CheckBox checkBoxDefaultViewNumber;
    RadioGroup radioGroupPageSettings;
	RadioButton radioButtonSel5;
	RadioButton radioButtonSel10;
	RadioButton radioButtonSel15;
	RadioButton radioButtonSel20;

   	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view);

		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();
		
		Initialize();
	}

	private void Initialize() {	
		
		radioGroupPhoto = (RadioGroup)findViewById(R.id.radioGroupPhoto);
		radioButtonSelPhotoOnly = (RadioButton)findViewById(R.id.radioButtonSelPhotoOnly);
		radioButtonSelNoPhoto = (RadioButton)findViewById(R.id.radioButtonSelNoPhoto);
		radioButtonSelBoth = (RadioButton)findViewById(R.id.radioButtonSelBoth);	
		
	    radioGroupPageSettings = (RadioGroup)findViewById(R.id.radioGroupPageSettings);
		radioButtonSel5 = (RadioButton)findViewById(R.id.radioButtonSel5);
		radioButtonSel10 = (RadioButton)findViewById(R.id.radioButtonSel10);
		radioButtonSel15 = (RadioButton)findViewById(R.id.radioButtonSel15);
		radioButtonSel20 = (RadioButton)findViewById(R.id.radioButtonSel20);

		if (isLargeScreen() == true){
			radioButtonSel10.setText("10 records");
			radioButtonSel15.setText("15 records - recommended");
		}
		else {
			radioButtonSel10.setText("10 records - recommended");
			radioButtonSel15.setText("15 records");			
		}
				
		radioButtonSelPhotoOnly.setOnClickListener(this);
		radioButtonSelNoPhoto.setOnClickListener(this);
		radioButtonSelBoth.setOnClickListener(this);

		radioButtonSel5.setOnClickListener(this);
		radioButtonSel10.setOnClickListener(this);
		radioButtonSel15.setOnClickListener(this);
		radioButtonSel20.setOnClickListener(this);

		// Receive the data.
	    Intent sender = getIntent();
		viewSettings = new ViewSettings();
		viewSettings.setPhotoSel(sender.getExtras().getInt("photoSel"));	
		viewSettings.setPageSize(sender.getExtras().getInt("pageSize"));
		
		// Display
		switch (viewSettings.getPhotoSel()){
		case ViewSettings.PHOTO_ONLY:
			radioButtonSelPhotoOnly.setChecked(true);
			break;
		case ViewSettings.NO_PHOTO:
			radioButtonSelNoPhoto.setChecked(true);
			break;
		case ViewSettings.BOTH:
			radioButtonSelBoth.setChecked(true);
			break;
		default:
			radioButtonSelBoth.setChecked(true);
			break;		
		}

		switch (viewSettings.getPageSize()){
		case ViewSettings.PAGE_SIZE_5:
			radioButtonSel5.setChecked(true);
			break;
		case ViewSettings.PAGE_SIZE_10:
			radioButtonSel10.setChecked(true);
			break;
		case ViewSettings.PAGE_SIZE_15:
			radioButtonSel15.setChecked(true);
			break;
		case ViewSettings.PAGE_SIZE_20:
			radioButtonSel20.setChecked(true);
			break;
		default:
			if (isLargeScreen() == true){
				radioButtonSel15.setChecked(true);
			}
			else {
				radioButtonSel10.setChecked(true);
			}
			break;
		}	
	}

	public boolean isLargeScreen() {
		boolean b = false;
		int screenSize = getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK;
	    if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {     
	    	b = true;
	    }
	    else if (screenSize == Configuration.SCREENLAYOUT_SIZE_NORMAL) {     
	    	b = false;
	    } 
	    else if (screenSize == Configuration.SCREENLAYOUT_SIZE_SMALL) {     
	    	b = false;
	    }
	    else if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {     
	    	b = true;
	    }
	    else {
	    	b = false;
	    }
		return b;
	}

	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.radioButtonSelPhotoOnly:
			viewSettings.setPhotoSel(ViewSettings.PHOTO_ONLY);
			break;
		case R.id.radioButtonSelNoPhoto:
			viewSettings.setPhotoSel(ViewSettings.NO_PHOTO);
			break;
		case R.id.radioButtonSelBoth:
			viewSettings.setPhotoSel(ViewSettings.BOTH);
			break;
		case R.id.radioButtonSel5:
			viewSettings.setPageSize(ViewSettings.PAGE_SIZE_5);
			break;
		case R.id.radioButtonSel10:
			viewSettings.setPageSize(ViewSettings.PAGE_SIZE_10);
			break;
		case R.id.radioButtonSel15:
			viewSettings.setPageSize(ViewSettings.PAGE_SIZE_15);
			break;
		case R.id.radioButtonSel20:
			viewSettings.setPageSize(ViewSettings.PAGE_SIZE_20);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		SaveAndReturn();		
		super.onBackPressed();		
	}
	
	private void SaveAndReturn() {
	    Intent i = new Intent();
		i.putExtra("photoSel", viewSettings.getPhotoSel());    			
		i.putExtra("pageSize", viewSettings.getPageSize()); 
		setResult(ViewActivity.this.RESULT_OK, i);
		ViewActivity.this.finish();
	}

	private void SetToDefault() {
		radioButtonSelBoth.setChecked(true);
		viewSettings.setPhotoSel(ViewSettings.BOTH);

		radioButtonSel10.setChecked(true);
		viewSettings.setPageSize(ViewSettings.PAGE_SIZE_10);
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
		Intent i = new Intent(ViewActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

	private void Latency2() {
		Intent i = new Intent(ViewActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}
    
	private void GoBackToMainPage() {
		Intent i = new Intent(ViewActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}   

}
