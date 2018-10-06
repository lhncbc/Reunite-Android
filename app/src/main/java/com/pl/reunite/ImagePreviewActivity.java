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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * preview the image to add to the missing people report.
 */

public class ImagePreviewActivity extends Activity implements View.OnClickListener{
	static final int IMAGE_PREVIEW = 1;
	
    ReUnite app;
    String webServer = "";

    ImageView imageViewlarge;
	Button buttonSelect;
    Button buttonCancel;
	String imageFullName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.image_preview);
		
		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();

		Initialize();
	}

	private void Initialize() {
		imageViewlarge = (ImageView) findViewById(R.id.imageViewlarge);
        buttonSelect = (Button) findViewById(R.id.buttonSelect);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        buttonSelect.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

	    Intent sender = getIntent();
	    imageFullName = sender.getExtras().getString("fileFullName").trim();
	    	    
        Bitmap bmpPhoto = Shrinkmethod(imageFullName, 600,600);
        imageViewlarge.setImageBitmap(bmpPhoto);
	}

    Bitmap Shrinkmethod(String file,int width,int height){
        BitmapFactory.Options bitopt=new BitmapFactory.Options();
        bitopt.inJustDecodeBounds=true;
        Bitmap bit=BitmapFactory.decodeFile(file, bitopt);

        int h=(int) Math.ceil(bitopt.outHeight/(float)height);
        int w=(int) Math.ceil(bitopt.outWidth/(float)width);

        if(h>1 || w>1){
            if(h>w){
                bitopt.inSampleSize=h;

            }else{
                bitopt.inSampleSize=w;
            }
        }
        bitopt.inJustDecodeBounds=false;
        bit=BitmapFactory.decodeFile(file, bitopt);
        return bit;
    }    

	public void onClick(View v) {
		switch (v.getId()){
		case R.id.buttonSelect:
			Select();
            break;
            case R.id.buttonCancel:
                CancelSelectedImage();
                break;
            default:
                break;


		}
	}

    private void CancelSelectedImage() {
        Intent i = new Intent();
//		i.putExtra("resultFromImageView", "");
        setResult(ImagePreviewActivity.this.RESULT_CANCELED, i);
        ImagePreviewActivity.this.finish();
    }

    @Override
	public void onBackPressed() {
		super.onBackPressed();
	    Intent i = new Intent();
//		i.putExtra("resultFromImageView", "");    			
		setResult(ImagePreviewActivity.this.RESULT_CANCELED, i);
		ImagePreviewActivity.this.finish();
	}

	private void Select() {
	    Intent i = new Intent();
//		i.putExtra("resultFromImageView", imageFullName);    			
		setResult(ImagePreviewActivity.this.RESULT_OK, i);
		ImagePreviewActivity.this.finish();
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
		Intent i = new Intent(ImagePreviewActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

	private void Latency2() {
		Intent i = new Intent(ImagePreviewActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}
    
	private void GoBackToMainPage() {
		Intent i = new Intent(ImagePreviewActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}    

	
}
