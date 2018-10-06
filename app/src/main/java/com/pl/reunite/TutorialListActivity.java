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
import android.os.Bundle;
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

public class TutorialListActivity extends Activity implements View.OnClickListener {
    ReUnite app;
    String webServer = "";

    String currentSelectedRecord = "";
	List<Record> recordList = new ArrayList<Record>();
	ListView lv;
	ItemTutorialListBaseAdapter adapter;	
	
    private static final String HOMESCREEN = "Home Screen";
    private static final String LOGIN = "Login/Logout";
    private static final String FIND = "Find";
    private static final String REPORT = "Report";
    private static final String ORGANIZE = "Organize";
	
	private Record homeScreenTutorial = new Record();
	private Record loginTutorial = new Record();
	private Record findTutorial = new Record();
	private Record reportTutorial = new Record();
	private Record organizeTutorial = new Record();	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tutorial_list);
		
		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();

		Initialize();	
	}

	private void Initialize() {
        lv = (ListView) findViewById(R.id.listViewTutorials);
		
		SetRecordList();
		DisplayRecordList();
	}

	private void DisplayRecordList() {
        ArrayList<ItemTutorialView> image_details = GetRecordResults();	
        lv = (ListView) findViewById(R.id.listViewTutorials);
        adapter = new ItemTutorialListBaseAdapter(TutorialListActivity.this, image_details);
 
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lv.getItemAtPosition(position);
        		ItemTutorialView obj_itemDetails = (ItemTutorialView)o;
        		currentSelectedRecord = new String(obj_itemDetails.getCat());
        		
        		if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.home)) == true) {
        			Intent i = new Intent(TutorialListActivity.this, TutorialHomeScreenActivity.class);
        			startActivity(i);			
        		}
        		else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.login_or_logout)) == true) {
        			Intent i = new Intent(TutorialListActivity.this, TutorialLoginActivity.class);
        			startActivity(i);			
        		}
        		else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.find)) == true) {
        			Intent i = new Intent(TutorialListActivity.this, TutorialFindActivity.class);
        			startActivity(i);			
        		}
        		else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.report)) == true) {
        			Intent i = new Intent(TutorialListActivity.this, TutorialReportActivity.class);
        			startActivity(i);			
        		}
        		else if (currentSelectedRecord.equalsIgnoreCase(getResources().getString(R.string.organize)) == true){
        			Intent i = new Intent(TutorialListActivity.this, TutorialOrganizeActivity.class);
        			startActivity(i);			
        		}
        		else {        		
        			Toast.makeText(TutorialListActivity.this, getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
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

	private ArrayList<ItemTutorialView> GetRecordResults() {
	   	ArrayList<ItemTutorialView> results = new ArrayList<ItemTutorialView>();
    	
	   	ItemTutorialView item_details;    	
    	for (int i = 0; i < recordList.size(); i++)
    	{
    		Record r = recordList.get(i);
    		item_details = new ItemTutorialView();
    		item_details.setCat(r.getCat());
    		item_details.setDis(r.getDis());
    		results.add(item_details);
    	}
    	return results;
	}

	private void SetRecordList() {
		if (recordList.isEmpty() == false){
			recordList.clear();
		}
		homeScreenTutorial.setCat(getResources().getString(R.string.home));
		homeScreenTutorial.setDis(getResources().getString(R.string.tutorial_on_the_starting_page));
		recordList.add(homeScreenTutorial);
		
		loginTutorial.setCat(getResources().getString(R.string.login_or_logout));
		loginTutorial.setDis(getResources().getString(R.string.tutorial_on_rules_of_user_registration));
		recordList.add(loginTutorial);
		
		findTutorial.setCat(getResources().getString(R.string.find));
		findTutorial.setDis(getResources().getString(R.string.tutorial_on_finding_a_missing_person));
		recordList.add(findTutorial);
		
		reportTutorial.setCat(getResources().getString(R.string.report));
		reportTutorial.setDis(getResources().getString(R.string.tutorial_on_reporting_a_missing_person));
		recordList.add(reportTutorial);

		organizeTutorial.setCat(getResources().getString(R.string.organize));
		organizeTutorial.setDis(getResources().getString(R.string.tutorial_on_managing_files_in_sent_saved_draft_and_outbox));
		recordList.add(organizeTutorial);	
	}

	public void onClick(View v) {
	}
	
    // Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutorial_menu, menu);
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
        case R.id.itemContactUs:
			new EmailUs(this).start();
        	break;
        }
        return true;
	}

	private void Latency2() {
		Intent i = new Intent(TutorialListActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}
    
	private void GoBackToMainPage() {
		Intent i = new Intent(TutorialListActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}    
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

}
