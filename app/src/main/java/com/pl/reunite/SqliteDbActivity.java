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

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;
import java.util.Random;

public class SqliteDbActivity extends ListActivity implements View.OnClickListener{
    ReUnite app;
    String webServer = "";

    Button buCountSaved;
	Button buCountDraft;
	Button buCountSent;
	Button buCountOutbox;
	private PatientsDataSource datasource;
	  
	  
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.sqlite_db);

			app = ((ReUnite)this.getApplication());
            app.detectMobileDevice(this);
            app.setScreenOrientation(this);

			webServer = app.getWebServer();

			buCountSaved = (Button) findViewById(R.id.buttonCountSaved);
	        buCountDraft = (Button) findViewById(R.id.buttonCountDraft);
	        buCountSent = (Button) findViewById(R.id.buttonCountSent);
	        buCountOutbox = (Button) findViewById(R.id.buttonCountOutbox);
	        
	        buCountSaved.setOnClickListener(this);
	        buCountDraft.setOnClickListener(this);
	        buCountSent.setOnClickListener(this);
	        buCountOutbox.setOnClickListener(this);

	    	datasource = new PatientsDataSource(this);
	        datasource.open();

	        List<Patient> values = datasource.getAllPatients();
//	        List<Patient> values = datasource.getAllDraftPatients();
//	        List<Patient> values = datasource.getAllDraftPatients();
//	        List<Patient> values = datasource.getAllDraftPatients();
//	        List<Patient> values = datasource.getAllDraftPatients();

	        // Use the SimpleCursorAdapter to show the
	        // elements in a ListView
	        ArrayAdapter<Patient> adapter = new ArrayAdapter<Patient>(this,
	            android.R.layout.simple_list_item_1, values);
	        setListAdapter(adapter);    
	    }
	    
	    // Will be called via the onClick attribute
	    // of the buttons in main.xml
	    public void onClick(View view) {
	      @SuppressWarnings("unchecked")
	      ArrayAdapter<Patient> adapter = (ArrayAdapter<Patient>) getListAdapter();
	      Patient patient = null;
	      switch (view.getId()) {
	      case R.id.add:
	        String[] familyNames = new String[] { "Joe", "Tom", "Green", "David", "John", "Andy", "Mike", "White", "Black", "Golden"};
	        String[] lastNames = new String[] {"Zhao", "Qian", "Shen", "Li", "Zhang", "Wang", "Song", "Hu", "Shi", "Kong"};
	        int nextInt1 = new Random().nextInt(10);
	        int nextInt2 = new Random().nextInt(10);
	        // Save the new comment to the database
	        patient = new Patient(webServer);
	        patient.setGivengName(familyNames[nextInt1]);
	        patient.setFamilyName(lastNames[nextInt2]);
	        patient.setEventShortName("test");
	        patient.setEventName("Test Exercise");
	        patient.setStatusReport(Patient.TEST);
	        patient = datasource.createPatient(patient);
	        adapter.add(patient);
	        break;
	      case R.id.delete:
	        if (getListAdapter().getCount() > 0) {
	        	patient = (Patient) getListAdapter().getItem(0);
	        	datasource.deletePatient(patient);
	        	adapter.remove(patient);
	        }
	        break;
	      case R.id.buttonCountSaved:
	    	  CountSavedRecords();
	    	  break;
	      case R.id.buttonCountDraft:
	    	  CountDraftRecords();
	    	  break;
	      case R.id.buttonCountSent:
	    	  CountSentRecords();
	    	  break;
	      case R.id.buttonCountOutbox:
	    	  CountOutboxRecords();
	    	  break;
	      }
	      adapter.notifyDataSetChanged();
	    }

		private void CountOutboxRecords() {
			int r = datasource.countOutboxRecords();
			Toast.makeText(this, getListView().getResources().getString(R.string.outbox) + " " + String.valueOf(r), Toast.LENGTH_SHORT).show();
		}

		private void CountSentRecords() {
			int r = datasource.countSentRecords();
			Toast.makeText(this, getListView().getResources().getString(R.string.sent) + " " + String.valueOf(r), Toast.LENGTH_SHORT).show();
		}

		private void CountDraftRecords() {
			int r = datasource.countDraftRecords();
			Toast.makeText(this, getListView().getResources().getString(R.string.draft) + " " + String.valueOf(r), Toast.LENGTH_SHORT).show();
		}

		private void CountSavedRecords() {
			int r = datasource.countSavedRecords();
			Toast.makeText(this, getListView().getResources().getString(R.string.saved) + " " + String.valueOf(r), Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPause() {
			datasource.close();
			super.onPause();
		}

		@Override
		protected void onResume() {
			datasource.open();
			super.onResume();
		}   
	}
