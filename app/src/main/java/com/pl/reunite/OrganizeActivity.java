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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class OrganizeActivity extends Activity implements View.OnClickListener {
	private static final int YES_ID = Menu.FIRST + 3;
	private static final int NO_ID = Menu.FIRST + 4;
	
    ReUnite app;
    String webServer = "";

	String currentSelectedRecord = "";
	List<Record> recordList = new ArrayList<Record>();
	ListView lv;
	ItemRecordListBaseAdapter adapter;	
	
	private PatientsDataSource datasource;

	private Record allRecord = new Record();
	private Record sentRecord = new Record();
	private Record savedRecord = new Record();
	private Record draftRecord = new Record();
	private Record outboxRecord = new Record();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.organize);

		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();
		
		Initialize();
	}

	private void Initialize() {

    	datasource = new PatientsDataSource(this);
        datasource.open();

        lv = (ListView) findViewById(R.id.listViewRecords);
		
		SetRecordList();
		DisplayRecordList();	
		
		registerForContextMenu(lv);// added in version 3.2 build 5
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
        	if (info.position == 0){
        		// Send
        		datasource.deleteAllSentPatients();
        	    Toast.makeText(this, getResources().getString(R.string.sent_box_is_empty), Toast.LENGTH_SHORT).show();
        	}
        	else if (info.position == 1) { 
        		// Outbox
        		datasource.deleteAllOutboxPatients();
        	    Toast.makeText(this, getResources().getString(R.string.out_box_is_empty), Toast.LENGTH_SHORT).show();
        	}
        	else if (info.position == 2) {
        		// Draft
        		datasource.deleteAllDraftPatients();
        	    Toast.makeText(this, getResources().getString(R.string.draft_box_is_empty), Toast.LENGTH_SHORT).show();
        	}
        	else if (info.position == 3) {
        		// Saved
        		datasource.deleteAllSavedPatients();
        	    Toast.makeText(this, getResources().getString(R.string.saved_box_is_empty), Toast.LENGTH_SHORT).show();
        	}
        	else if (info.position == 4) {
        		// All
        		DeleteAllRecords();
        	    Toast.makeText(this, getResources().getString(R.string.all_boxes_are_empty), Toast.LENGTH_SHORT).show();
        	}
            UpdateAllRecords();
            UpdateListView();
    	    break;
        case NO_ID:
    	    Toast.makeText(this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show();
    	    break;
        }

		return super.onContextItemSelected(item);
	}

	private void SetRecordList() {
		if (recordList.isEmpty() == false){
			recordList.clear();
		}

//		sentRecord.setCat(Patient.SENT);
		sentRecord.setCat(getResources().getString(R.string.sent));
		sentRecord.setCount(CountSentRecords());
		sentRecord.setDis(String.valueOf(sentRecord.getCount()) + " " + getResources().getString(R.string.files_found));
		recordList.add(sentRecord);

//		outboxRecord.setCat(Patient.OUTBOX);
		outboxRecord.setCat(getResources().getString(R.string.outbox));
		outboxRecord.setCount(CountOutboxRecords());
		outboxRecord.setDis(String.valueOf(outboxRecord.getCount()) + " " + getResources().getString(R.string.files_found));
		recordList.add(outboxRecord);

//		draftRecord.setCat(Patient.DRAFT);
		draftRecord.setCat(getResources().getString(R.string.draft));
		draftRecord.setCount(CountDraftRecords());
		draftRecord.setDis(String.valueOf(draftRecord.getCount()) + " " + getResources().getString(R.string.files_found));
		recordList.add(draftRecord);

//		savedRecord.setCat(Patient.SAVED_FROM_SEARCH);
		savedRecord.setCat(getResources().getString(R.string.saved_from_search));
		savedRecord.setCount(CountSavedRecords());
		savedRecord.setDis(String.valueOf(savedRecord.getCount()) + " " + getResources().getString(R.string.files_found));
		recordList.add(savedRecord);

		allRecord.setCount(savedRecord.getCount() + sentRecord.getCount() + outboxRecord.getCount() + draftRecord.getCount());
		allRecord.setCat(getResources().getString(R.string.delete_all));
		allRecord.setDis(String.valueOf(allRecord.getCount()) + " " + getResources().getString(R.string.files_found));
		recordList.add(allRecord);	
	}

	private int CountSentRecords() {

		return datasource.countSentRecords();
	}

	private int CountDraftRecords() {

		return datasource.countDraftRecords();
	}

	private int CountOutboxRecords() {

		return datasource.countOutboxRecords();
	}

	private int CountSavedRecords() {

		return datasource.countSavedRecords();
	}

	private void DisplayRecordList() {

        ArrayList<ItemRecordView> image_details = GetRecordResults();	
        lv = (ListView) findViewById(R.id.listViewRecords);
        adapter = new ItemRecordListBaseAdapter(OrganizeActivity.this, image_details);
        lv.setAdapter(adapter);
        
		lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lv.getItemAtPosition(position);
        		ItemRecordView obj_itemDetails = (ItemRecordView)o;
        		currentSelectedRecord = new String(obj_itemDetails.getCat());
//				if (currentSelectedRecord.equalsIgnoreCase(Patient.SAVED_FROM_SEARCH) == true) {
				if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.saved_from_search)) == true) {
        			if (savedRecord.getCount() > 0){
                        Intent i = new Intent(OrganizeActivity.this, PatientSavedListActivity.class);
                        final int result = 0;
                        startActivityForResult(i, result);
        			}
        			else {
            			Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.no_file_is_found), Toast.LENGTH_SHORT).show();
        			}	
        		}
//				else if (currentSelectedRecord.equalsIgnoreCase(Patient.SENT) == true) {
				else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.sent)) == true) {
        			if (sentRecord.getCount() > 0){
                        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH) {
                            Intent i = new Intent(OrganizeActivity.this, PatientSentListActivity.class);
                            final int result = 0;
                            startActivityForResult(i, result);
                        }
                        else {
                                Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.need_to_login), Toast.LENGTH_SHORT).show();
                        }
        			}
        			else {
            			Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.no_file_is_found), Toast.LENGTH_SHORT).show();
        			}	
        		}
//				else if (currentSelectedRecord.equalsIgnoreCase(Patient.DRAFT) == true) {
				else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.draft)) == true) {
        			if (draftRecord.getCount() > 0){
                        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH) {
                            Intent i = new Intent(OrganizeActivity.this, PatientDraftListActivity.class);
                            final int result = 0;
                            startActivityForResult(i, result);
                        }
                        else {
                            Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.need_to_login), Toast.LENGTH_SHORT).show();
                        }
        			}
        			else {
            			Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.no_file_is_found), Toast.LENGTH_SHORT).show();
        			}	
        		}
//				else if (currentSelectedRecord.equalsIgnoreCase(Patient.OUTBOX) == true) {
				else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.outbox)) == true) {
        			if (outboxRecord.getCount() > 0){
                        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH) {
                            Intent i = new Intent(OrganizeActivity.this, PatientOutboxListActivity.class);
                            final int result = 0;
                            startActivityForResult(i, result);
                        }
                        else {
                            Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.need_to_login), Toast.LENGTH_SHORT).show();
                        }
        			}
        			else {
            			Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.no_file_is_found), Toast.LENGTH_SHORT).show();
        			}	
        		}
        		else if (currentSelectedRecord.equalsIgnoreCase(Patient.TEST) == true){
           			Intent i = new Intent("com.pl.reunite.SQLITEDBACTIVITY");
        			startActivity(i);																					
        		}
				else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.delete_all)) == true){
        			if (allRecord.getCount() > 0){
                        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH) {
                            AreYouSureToExit();
                        }
                        else {
                            Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.need_to_login), Toast.LENGTH_SHORT).show();
                        }
        			}
        			else {
            			Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.no_file_is_found), Toast.LENGTH_SHORT).show();
        			}	
        		}
        		else {        		
        			Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.select) + " " + currentSelectedRecord, Toast.LENGTH_SHORT).show();
        		}
        	}

			private Record searchRecordFromList(String currentSelectedRecord) {

				for (int i = 0; i < recordList.size(); i++){
					Record r = recordList.get(i);
					if (currentSelectedRecord.equalsIgnoreCase(r.getCat()) == true){
						return r;
					}
				}
				return null;
			}
		});
	}

    private ArrayList<ItemRecordView> GetRecordResults() {

	   	ArrayList<ItemRecordView> results = new ArrayList<ItemRecordView>();
    	
	   	ItemRecordView item_details;    	
    	for (int i = 0; i < recordList.size(); i++)
    	{
    		Record r = recordList.get(i);
    		item_details = new ItemRecordView();
    		item_details.setCat(r.getCat());
    		item_details.setDis(r.getDis());
    		results.add(item_details);
    	}
    	return results;
	}

	public void onClick(View v) {
		switch (v.getId()){
		case R.id.listViewRecords:
			Toast.makeText(OrganizeActivity.this, "List view is clicked", Toast.LENGTH_SHORT);
			break;
		}
	}	
	
	private void AreYouSureToExit() {

		AlertDialog.Builder builder = new AlertDialog.Builder(OrganizeActivity.this);
		builder.setMessage(getResources().getString(R.string.you_are_about_to_delete_all_files))
			   .setTitle(getResources().getString(R.string.are_you_sure_q))
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                DeleteAllRecords();
		                UpdateAllRecords();
		                UpdateListView();
		                Toast.makeText(OrganizeActivity.this, getResources().getString(R.string.all_files_are_deleted), Toast.LENGTH_SHORT).show();
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
	
	private void DeleteAllRecords() {
		datasource.deleteAllDraftPatients();
		datasource.deleteAllOutboxPatients();
		datasource.deleteAllSentPatients();
		datasource.deleteAllSavedPatients();
	}
	
	public void UpdateListView() {

		// Update the display
		for (int i = 0; i < lv.getCount(); i++){
			Object o = lv.getItemAtPosition(i);	
			ItemRecordView obj_itemDetails = (ItemRecordView)o;
			currentSelectedRecord = new String(obj_itemDetails.getCat());
//			if (currentSelectedRecord.equalsIgnoreCase(Patient.SAVED) == true) {
			if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.saved_from_search)) == true) {
				obj_itemDetails.setDis(String.valueOf(savedRecord.getCount()) + " " + getResources().getString(R.string.files_found));
    		}
//			else if (currentSelectedRecord.equalsIgnoreCase(Patient.SENT) == true) {
			else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.sent)) == true) {
				obj_itemDetails.setDis(String.valueOf(sentRecord.getCount()) + " " + getResources().getString(R.string.files_found));
    		}
//			else if (currentSelectedRecord.equalsIgnoreCase(Patient.DRAFT) == true) {
			else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.draft)) == true) {
				obj_itemDetails.setDis(String.valueOf(draftRecord.getCount()) + " " + getResources().getString(R.string.files_found));
    		}
//			else if (currentSelectedRecord.equalsIgnoreCase(Patient.OUTBOX) == true) {
			else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.outbox)) == true) {
				obj_itemDetails.setDis(String.valueOf(outboxRecord.getCount()) + " " + getResources().getString(R.string.files_found));
    		}
//			else if (currentSelectedRecord.equalsIgnoreCase("delete all") == true){
			else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.delete_all)) == true){
				obj_itemDetails.setDis(String.valueOf(allRecord.getCount()) + " " + getResources().getString(R.string.files_found));
    		}
		}
		adapter.notifyDataSetChanged();
	}

	public void UpdateAllRecords() {

		// update the record
		savedRecord.setCount(CountSavedRecords());					
		sentRecord.setCount(CountSentRecords());					
		outboxRecord.setCount(CountOutboxRecords());					
		draftRecord.setCount(CountDraftRecords());					
		allRecord.setCount(savedRecord.getCount() + sentRecord.getCount() + outboxRecord.getCount() + draftRecord.getCount());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		
		datasource.open();
        UpdateAllRecords();
        UpdateListView();
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
		Intent i = new Intent(OrganizeActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

	private void Latency2() {
		Intent i = new Intent(OrganizeActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}
    
	private void GoBackToMainPage() {

		Intent i = new Intent(OrganizeActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}    
}
