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
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PatientSentListActivity extends Activity implements View.OnClickListener{
	private static final int YES_ID = Menu.FIRST + 3;
	private static final int NO_ID = Menu.FIRST + 4;

	ReUnite app;
	String webServer = "";

	private PatientsDataSource datasource;
	
    ListView lvPatientSimple;
    String returnString;
    Event currentEvent;
    
	List<Patient> patientList = new ArrayList<Patient>();

	// LoadMore button
	int totalPages = 0;
	int totalRecords = 0;
	int pageSize = 0;
	int currentPage = 0;
	int currentPageStart = 0;
	
	Button btnDelete;
	Button btnPreviousPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.patient_draft_list);
		
		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();

		Initialize();
	}

	private void Initialize() {
    	datasource = new PatientsDataSource(this);
        datasource.open();

        lvPatientSimple = (ListView) findViewById(R.id.lvPatientSimple);
        
		// Load more button
        btnDelete = new Button(PatientSentListActivity.this);
        btnDelete.setText(getResources().getString(R.string.delete_all));

		// start to change
		Drawable drawable;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			drawable = getResources().getDrawable(R.drawable.more_button, this.getTheme());
		} else {
			drawable = getResources().getDrawable(R.drawable.more_button);
		}
		// set background
		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
			btnDelete.setBackground(drawable);
		} else {
			btnDelete.setBackgroundDrawable(drawable);
		}
		// end of changes

        btnDelete.setOnClickListener(new View.OnClickListener() {
    		public void onClick(View v) {
				// Starting a new async task
				DeleteAllSentPatients();
			}
		});
		lvPatientSimple.addFooterView(btnDelete);

		// Read the saved list.
        patientList = datasource.getAllSentPatients();

		if (patientList.isEmpty() == false) {
			DisplayPatientList();
		}
		
		registerForContextMenu(lvPatientSimple);// added in version 3.2 build 5				
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
			
        menu.setHeaderTitle(getResources().getString(R.string.delete_q));
	    menu.add(Menu.NONE, YES_ID, Menu.NONE, getResources().getString(R.string.yes));
        menu.add(Menu.NONE, NO_ID, Menu.NONE, getResources().getString(R.string.no));
    }
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    
		switch (item.getItemId()) {
        case YES_ID:
    		Object o = lvPatientSimple.getItemAtPosition(info.position);
    		ItemPatientSimpleView obj_itemDetails = (ItemPatientSimpleView)o;  
    	    Patient p = new Patient(webServer);
    		p.setSerialId(Long.parseLong(obj_itemDetails.getSerialId()));
    		if (p.getSerialId() >= 0){
    			datasource.deletePatient(p);    		
        	    Toast.makeText(this, getResources().getString(R.string.all_files_are_deleted), Toast.LENGTH_SHORT).show();

    			// Read the saved list.
    	        patientList = datasource.getAllSentPatients();
    			if (patientList.isEmpty() == false) {
    				DisplayPatientList();
    			}
    			else {
	        	   finish();
    			}
            }            	
    	    break;
        case NO_ID:
    	    Toast.makeText(this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show();
    	    break;
        }

		return super.onContextItemSelected(item);
	}
	
	protected void DeleteAllSentPatients() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PatientSentListActivity.this);
		builder.setMessage(getResources().getString(R.string.you_are_about_to_delete_all_files))
			   .setTitle(getResources().getString(R.string.are_you_sure_q))
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   datasource.deleteAllSentPatients();
		        	   Toast.makeText(PatientSentListActivity.this, getResources().getString(R.string.all_files_are_deleted), Toast.LENGTH_SHORT).show();
		               finish();
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

	private void DisplayPatientList() {
		if (patientList.isEmpty() == true){
			return;
		}
		
		// get listview current position - used to maintain scroll
		// position
		int currentPosition = lvPatientSimple.getFirstVisiblePosition();
		
        ArrayList<ItemPatientSimpleView> image_details = GetPatientResults();	
		lvPatientSimple.setAdapter(new ItemPatientSimpleListBaseAdapter(PatientSentListActivity.this, image_details));

		// Setting new scroll position
		lvPatientSimple.setSelectionFromTop(currentPosition + 1, 0);
		
		lvPatientSimple.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvPatientSimple.getItemAtPosition(position);
        		ItemPatientSimpleView obj_itemDetails = (ItemPatientSimpleView)o;

        		// Start activity and pass the data 
        		StartPatientInforActivity(obj_itemDetails);
        	}

			private void StartPatientInforActivity(ItemPatientSimpleView item) {
    			Intent i = new Intent(PatientSentListActivity.this, ReportProceedActivity.class);
    			i.putExtra("serialId", item.getSerialId());    			
    			final int result=1;
    			startActivityForResult(i, result);
			}

			private Patient searchPatientFromList(String strPersonUUID) {
				Patient p = null;
				for (int i = 0; i < patientList.size(); i++){
					p = new Patient(patientList.get(i), webServer);
					String strCurId = p.getPatientUuid();
					if (strPersonUUID.equalsIgnoreCase(strCurId) == true){
						return p;
					}
				}
				return null;
			}
        	;
		});	
	}

	private ArrayList<ItemPatientSimpleView> GetPatientResults() {
	   	ArrayList<ItemPatientSimpleView> results = new ArrayList<ItemPatientSimpleView>();
    	
    	ItemPatientSimpleView item_details;    	
    	for (int i = 0; i < patientList.size(); i++)
    	{
    		Patient p = patientList.get(i);
    		item_details = new ItemPatientSimpleView();
    		item_details.setPatientUuid(p.getPatientUuid().toString());
    		p.decodePhoto();
    		item_details.setPhoto(p.getPhoto());
    		item_details.setName(p.getFullName().toString());

			// replace age section
			// built in versionCode 7020601, version 7.2.6-beta
			// start
//			item_details.setMinAge(p.getMinAge().toString());
//			item_details.setMaxAge(p.getMaxAge().toString());
			item_details.setAge(p.getYearsOld().toString());
			item_details.setMinAge("-1");
			item_details.setMaxAge("-1");
			// end

    		item_details.setGender(p.getGender().toString());
    		item_details.setOptStatus(p.getOptStatus().toString());
    		item_details.setStatusSahanaUpdated(p.getStatusSahanaUpdated().toString());
    		item_details.setNumber(String.valueOf(i+1) + "/" + String.valueOf(patientList.size()));
    		item_details.setEvent(p.getEventName().toString());
    		item_details.setSerialId(String.valueOf(p.getSerialId()));

			item_details.setPersonAnimal(p.getPa());

			results.add(item_details);
    	}
    	return results;
	}

	public void onClick(View v) {

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// to save enger, just return to the organize activity
		finish();
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
    		Intent i = new Intent(PatientSentListActivity.this, TutorialListActivity.class);
    		startActivity(i);	
		}

		private void Latency2() {
			Intent i = new Intent(PatientSentListActivity.this, LatencyActivity.class);
			i.putExtra("webServer", webServer);    			
			startActivity(i);	
		}
	    
		private void GoBackToMainPage() {
			Intent i = new Intent(PatientSentListActivity.this, ReUniteActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(i);
			finish();
		}    	

}
