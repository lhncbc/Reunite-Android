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
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pl.reunite.Result.RestSearchCountResult;
import com.pl.reunite.Result.SearchResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class FiltersActivity extends Activity implements View.OnClickListener {
	static final int DEFINE_FILTERS = 2;

	Filters filters = new Filters();

    ReUnite app;
    String webServer = "";
    String nameSpace = "";
    String url = "";
    String soapAction = "";

    Event currentEvent;
	TextView tvEventName;
	SearchResult searchResult;
	
	ProgressBar pbTotalNumber;
	ProgressBar pbNewTotalNumber;

	TextView tvTotalNumber;
	TextView tvNewTotalNumber;
//	Button btNext;
	Button btNext2;
	
	CheckBox cbMale;
	CheckBox cbFemale;
	CheckBox cbComplex;
	CheckBox cbGenderUnknown;
	
	CheckBox cbAgeAdult;
	CheckBox cbAgeChild;

	CheckBox cbMissing;
	CheckBox cbAliveAndWell;
	CheckBox cbInjured;
	CheckBox cbDeceased;
	CheckBox cbStatusUnknown;
	CheckBox cbFound;
	
	int threadCounter = 0;

    ViewSettings viewSettings = new ViewSettings();

    // photo section - start
    RadioGroup radioGroupPhoto;
    RadioButton radioButtonSelPhotoOnly;
    RadioButton radioButtonSelNoPhoto;
    RadioButton radioButtonSelBoth;

    RadioGroup radioGroupPageSettings;
    RadioButton radioButtonSel5;
    RadioButton radioButtonSel10;
    RadioButton radioButtonSel15;
    RadioButton radioButtonSel20;

	/**
	 * add the animal search
	 */
	RadioButton radioButtonSelSearchPerson;
	RadioButton radioButtonSelSearchAnimal;
	RadioButton radioButtonSelSearchBoth;
    // photo section - end

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filters_and_view);

		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();

		Initialize();
	}
	
	private void Initialize() {
		pbNewTotalNumber = (ProgressBar) findViewById(R.id.progressBarNewTotalNumber);
		tvNewTotalNumber = (TextView) findViewById(R.id.tvNewTotalNumber);

		cbMale = (CheckBox) findViewById(R.id.checkBoxMale);
		cbFemale = (CheckBox) findViewById(R.id.checkBoxFemale);
		cbComplex = (CheckBox) findViewById(R.id.checkBoxComplex);
		cbGenderUnknown = (CheckBox) findViewById(R.id.checkBoxGenderUnknown);
				
		cbAgeAdult = (CheckBox)findViewById(R.id.checkBoxAgeAdult);
		cbAgeChild = (CheckBox)findViewById(R.id.checkBoxAgeChild);

		cbMissing = (CheckBox)findViewById(R.id.checkBoxMissing);
		cbAliveAndWell = (CheckBox)findViewById(R.id.checkBoxAliveAndWell);
		cbInjured = (CheckBox)findViewById(R.id.checkBoxInjured);
		cbDeceased = (CheckBox)findViewById(R.id.checkBoxDeceased);
		cbStatusUnknown = (CheckBox)findViewById(R.id.checkBoxStatusUnknown);
		cbFound = (CheckBox)findViewById(R.id.checkBoxFound);

		cbMale.setOnClickListener(this);
		cbFemale.setOnClickListener(this);
		cbComplex.setOnClickListener(this);
		cbGenderUnknown.setOnClickListener(this);

		cbAgeAdult.setOnClickListener(this);
	    cbAgeChild.setOnClickListener(this);

	    cbMissing.setOnClickListener(this);
	    cbAliveAndWell.setOnClickListener(this);
	    cbInjured.setOnClickListener(this);
	    cbDeceased.setOnClickListener(this);
	    cbStatusUnknown.setOnClickListener(this);
	    cbFound.setOnClickListener(this);	    

	    Intent sender = getIntent();
	    currentEvent = new Event(webServer);
	    currentEvent.setName(sender.getExtras().getString("name"));	
	    currentEvent.setShortName(sender.getExtras().getString("shortname"));	

	    // animal
		currentEvent.setFilterSelSearchPerson(sender.getExtras().getBoolean("filterSelSearchPerson"));
		currentEvent.setFilterSelSearchAnimal(sender.getExtras().getBoolean("filterSelSearchAnimal"));
		currentEvent.setFilterSelSearchBoth(sender.getExtras().getBoolean("filterSelSearchBoth"));

	    currentEvent.setFilterGenderMale(sender.getExtras().getBoolean("filterMale"));
	    currentEvent.setFilterGenderFemale(sender.getExtras().getBoolean("filterFemale"));
	    currentEvent.setFilterGenderComplex(sender.getExtras().getBoolean("filterComplex"));
	    currentEvent.setFilterGenderUnknown(sender.getExtras().getBoolean("filterUnknown"));

	    currentEvent.setFilterAgeAdult(sender.getExtras().getBoolean("filterAgeAdult"));
	    currentEvent.setFilterAgeChild(sender.getExtras().getBoolean("filterAgeChild"));
	    currentEvent.setFilterAgeUnknown(sender.getExtras().getBoolean("filterAgeUnknown"));

	    currentEvent.setFilterStatusMissing(sender.getExtras().getBoolean("filterStatusMissing"));
	    currentEvent.setFilterStatusAliveAndWell(sender.getExtras().getBoolean("filterStatusAliveAndWell"));
	    currentEvent.setFilterStatusInjured(sender.getExtras().getBoolean("filterStatusInjured"));
	    currentEvent.setFilterStatusDeceased(sender.getExtras().getBoolean("filterStatusDeceased"));
	    currentEvent.setFilterStatusUnknown(sender.getExtras().getBoolean("filterStatusUnknown"));
	    currentEvent.setFilterStatusFound(sender.getExtras().getBoolean("filterStatusFound"));

	    currentEvent.setNumberOfRecords(sender.getExtras().getString("numberOfRecords"));

		currentEvent.setSearchTerm(sender.getExtras().getString("searchTerm"));

		searchResult = new SearchResult();

		/**
		 * search animal
		 */
		radioButtonSelSearchPerson = (RadioButton)findViewById(R.id.radioButtonSelPerson);
		radioButtonSelSearchAnimal = (RadioButton)findViewById(R.id.radioButtonSelAnimal);
		radioButtonSelSearchBoth = (RadioButton)findViewById(R.id.radioButtonSelPersonAnimal);

		radioButtonSelSearchPerson.setOnClickListener(this);
		radioButtonSelSearchAnimal.setOnClickListener(this);
		radioButtonSelSearchBoth.setOnClickListener(this);

		if (currentEvent.isFilterSelSearchPerson() == true){
			radioButtonSelSearchPerson.setChecked(true);
		}
		else if (currentEvent.isFilterSelSearchAnimal() == true){
			radioButtonSelSearchAnimal.setChecked(true);
		}
		else {
			radioButtonSelSearchBoth.setChecked(true);
		}

		cbMale.setChecked(currentEvent.getFilterGenderMale());
    	cbFemale.setChecked(currentEvent.getFilterGenderFemale());
    	cbComplex.setChecked(currentEvent.getFilterGenderComplex());
    	cbGenderUnknown.setChecked(currentEvent.getFilterGenderUnknown());
    	
    	cbAgeAdult.setChecked(currentEvent.getFilterAgeAdult());
    	cbAgeChild.setChecked(currentEvent.getFilterAgeChild());

    	cbMissing.setChecked(currentEvent.getFilterStatusMissing());
    	cbAliveAndWell.setChecked(currentEvent.getFilterStatusAliveAndWell());
    	cbInjured.setChecked(currentEvent.getFilterStatusInjured());
    	cbDeceased.setChecked(currentEvent.getFilterStatusDeceased());
    	cbStatusUnknown.setChecked(currentEvent.getFilterStatusUnknown());
    	cbFound.setChecked(currentEvent.getFilterStatusFound());

        // photo section - start
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
            radioButtonSel10.setText(getResources().getString(R.string._10_seconds));
            radioButtonSel15.setText(getResources().getString(R.string._15_seconds_recommended));
        }
        else {
            radioButtonSel10.setText(getResources().getString(R.string._10_seconds_recommended));
            radioButtonSel15.setText(getResources().getString(R.string._15_seconds));
        }

        radioButtonSelPhotoOnly.setOnClickListener(this);
        radioButtonSelNoPhoto.setOnClickListener(this);
        radioButtonSelBoth.setOnClickListener(this);

        radioButtonSel5.setOnClickListener(this);
        radioButtonSel10.setOnClickListener(this);
        radioButtonSel15.setOnClickListener(this);
        radioButtonSel20.setOnClickListener(this);

		// Receive the data.
        viewSettings.setPhotoSel(sender.getExtras().getInt("photoSel"));
        viewSettings.setPageSize(sender.getExtras().getInt("pageSize"));

        // Display
        switch (viewSettings.getPhotoSel()){
            case ViewSettings.PHOTO_ONLY:
                radioButtonSelPhotoOnly.setChecked(true);
                break;
            case ViewSettings.NO_PHOTO:
//                radioButtonSelNoPhoto.setChecked(true);
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
        // photo section - end

//        new SearchCountForEventsAsyncTask().execute();
		new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
	}

	public void onClick(View v) {
		switch (v.getId()){
			case R.id.radioButtonSelPerson:
				currentEvent.setFilterSelSearchPerson(true);
				currentEvent.setFilterSelSearchAnimal(false);
				currentEvent.setFilterSelSearchBoth(false);
				new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
				break;
			case R.id.radioButtonSelAnimal:
				currentEvent.setFilterSelSearchPerson(false);
				currentEvent.setFilterSelSearchAnimal(true);
				currentEvent.setFilterSelSearchBoth(false);
				new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
				break;
			case R.id.radioButtonSelPersonAnimal:
				currentEvent.setFilterSelSearchPerson(false);
				currentEvent.setFilterSelSearchAnimal(false);
				currentEvent.setFilterSelSearchBoth(true);
				new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
				break;
		case R.id.checkBoxMale:
			currentEvent.setFilterGenderMale(cbMale.isChecked());
//			new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxFemale:
			currentEvent.setFilterGenderFemale(cbFemale.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxComplex:
			currentEvent.setFilterGenderComplex(cbComplex.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxGenderUnknown:
			currentEvent.setFilterGenderUnknown(cbGenderUnknown.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxAgeAdult:
			currentEvent.setFilterAgeAdult(cbAgeAdult.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxAgeChild:
			currentEvent.setFilterAgeChild(cbAgeChild.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxMissing:
			currentEvent.setFilterStatusMissing(cbMissing.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxAliveAndWell:
			currentEvent.setFilterStatusAliveAndWell(cbAliveAndWell.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxInjured:
			currentEvent.setFilterStatusInjured(cbInjured.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxDeceased:
			currentEvent.setFilterStatusDeceased(cbDeceased.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxStatusUnknown:
			currentEvent.setFilterStatusUnknown(cbStatusUnknown.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
		case R.id.checkBoxFound:
			currentEvent.setFilterStatusFound(cbFound.isChecked());
//		    new SearchCountForEventsAsyncTask().execute();
			new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
			break;
        // photo section - start
            case R.id.radioButtonSelPhotoOnly:
                viewSettings.setPhotoSel(ViewSettings.PHOTO_ONLY);
//                new SearchCountForEventsAsyncTask().execute();
				new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
                break;
            case R.id.radioButtonSelNoPhoto:
                viewSettings.setPhotoSel(ViewSettings.NO_PHOTO);
//                new SearchCountForEventsAsyncTask().execute();
				new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
                break;
            case R.id.radioButtonSelBoth:
                viewSettings.setPhotoSel(ViewSettings.BOTH);
//                new SearchCountForEventsAsyncTask().execute();
				new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
                break;
            case R.id.radioButtonSel5:
//                viewSettings.setPageSize(ViewSettings.PAGE_SIZE_5);
				new SearchCountRestCallAsyncTask(this, currentEvent.getShortName()).execute();
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
        // photo section - end

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		ReturnFilters();		
		super.onBackPressed();		
	}

	protected void ReturnFilters() {
	    Intent i = new Intent();
		
		i.putExtra("name", currentEvent.getName());    			
		i.putExtra("shortname", currentEvent.getShortName());

		i.putExtra("filterSelSearchPerson", currentEvent.isFilterSelSearchPerson());
		i.putExtra("filterSelSearchAnimal", currentEvent.isFilterSelSearchAnimal());
		i.putExtra("filterSelSearchBoth", currentEvent.isFilterSelSearchBoth());

		i.putExtra("filterMale", currentEvent.getFilterGenderMale());
		i.putExtra("filterFemale", currentEvent.getFilterGenderFemale());    			
		i.putExtra("filterComplex", currentEvent.getFilterGenderComplex());    			
		i.putExtra("filterUnknown", currentEvent.getFilterGenderUnknown());    			

		i.putExtra("filterAgeAdult", currentEvent.getFilterAgeAdult());    			
		i.putExtra("filterAgeChild", currentEvent.getFilterAgeChild());    			
		i.putExtra("filterAgeUnknown", currentEvent.getFilterAgeUnknown()); 
		
		i.putExtra("filterStatusMissing", currentEvent.getFilterStatusMissing()); 
		i.putExtra("filterStatusAliveAndWell", currentEvent.getFilterStatusAliveAndWell()); 
		i.putExtra("filterStatusInjured", currentEvent.getFilterStatusInjured()); 
		i.putExtra("filterStatusDeceased", currentEvent.getFilterStatusDeceased()); 
		i.putExtra("filterStatusUnknown", currentEvent.getFilterStatusUnknown()); 
		i.putExtra("filterStatusFound", currentEvent.getFilterStatusFound()); 

		i.putExtra("numberOfRecords", currentEvent.getNumberOfRecords());

		i.putExtra("searchTerm", currentEvent.getSearchTerm());

        // photo section - start
        i.putExtra("photoSel", viewSettings.getPhotoSel());
        i.putExtra("pageSize", viewSettings.getPageSize());
        // photo section - end

        setResult(FiltersActivity.this.RESULT_OK, i);
		FiltersActivity.this.finish();
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
		Intent i = new Intent(FiltersActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

	private void Latency2() {
		Intent i = new Intent(FiltersActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}


	private void GoBackToMainPage() {

		Intent i = new Intent(FiltersActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
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

	public JSONObject buildSearchCountJson(Context c, String eventShort) {
		final String call = "search";
		JSONObject json = new JSONObject();
		try {
			json.put("call", call);
			if (app.getAuthStatus() == true){
				json.put("token", app.getToken());
			}
			else {
				json.put("token", app.getTokenAnonymous());
			}
			json.put("short", currentEvent.getShortName());
			json.put("query", "");
			json.put("photo", "");

			// animal - start
			if (currentEvent.isFilterSelSearchPerson() == true){
				json.put("personAnimal", Filters.SEARCH_PERSON);
			}
			else if (currentEvent.isFilterSelSearchAnimal() == true){
				json.put("personAnimal", Filters.SEARCH_ANIMATION);
			}
			else {
				json.put("personAnimal", Filters.SEARCH_BOTH);
			}
			// animal - end

			json.put("sexMale", currentEvent.getFilterGenderMale());
			json.put("sexFemale", currentEvent.getFilterGenderFemale());
			json.put("sexOther", currentEvent.getFilterGenderComplex());
			json.put("sexUnknown", currentEvent.getFilterGenderUnknown());
			json.put("ageChild", currentEvent.getFilterAgeChild());
			json.put("ageAdult", currentEvent.getFilterAgeAdult());
			json.put("ageUnknown", currentEvent.getFilterAgeUnknown());
			json.put("statusMissing", currentEvent.getFilterStatusMissing());
			json.put("statusAlive", currentEvent.getFilterStatusAliveAndWell());
			json.put("statusInjured", currentEvent.getFilterStatusInjured());
			json.put("statusDeceased", currentEvent.getFilterStatusDeceased());
			json.put("statusUnknown", currentEvent.getFilterStatusUnknown());
			json.put("statusFound", currentEvent.getFilterStatusFound());

			if (viewSettings.getPhotoSel() == ViewSettings.PHOTO_ONLY){
				json.put("hasImage", true);
			}
			else {
				json.put("hasImage", false);
			}

			json.put("since", "1970-01-01T01:23:45Z");
			json.put("pageStart", 0);
			json.put("perPage", 1);
			json.put("sortBy", "");
		}
		catch (Exception ex){
			Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
			return null;
		}
		return json;
	}

	// HTTP POST request
	public String searchCountRestCall(Context c, String eventShort) throws Exception {
		String returnStr = "";

		WebServer ws = new WebServer(app.getWebServer());
		String url = ws.getRestEndpoint();
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		//add request header
		con.setRequestMethod("POST");

		JSONObject jo = buildSearchCountJson(c, eventShort);
		if (jo == null){
			return returnStr = c.getResources().getString(R.string.error) + ": " + "buildJson()";
		}

		// Send post request
		con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(jo.toString());

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		bw.write(jo.toString());
		bw.flush();
		bw.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post parameters : " + jo.toString());
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
		returnStr = jsonStr;
		return  returnStr;
	}

	private RestSearchCountResult extractSearchCountResult(Context c, JSONObject j) throws JSONException {
		RestSearchCountResult rest = new RestSearchCountResult(FiltersActivity.this);
		String errorCode = "", results = "", total = "";
		try {
			errorCode = j.get("error").toString();
			if (!errorCode.equalsIgnoreCase("0")){
				rest.setErrorCode(errorCode);
				rest.searchErrorMessage();
				return rest;
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
			rest.setErrorCode("-1");
			rest.setErrorMessage(e.getMessage().toString());
		}
		try {
			total = j.get("total").toString();
			rest.setErrorCode(errorCode);
			rest.setCount(total);
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
			rest.setErrorCode("-1");
			rest.setErrorMessage(e.getMessage().toString());
		}
		return rest;
	}


	//To use the AsyncTask, it must be subclassed
	private class SearchCountRestCallAsyncTask extends AsyncTask<Void, Integer, Void>
	{
		RestSearchCountResult rest;
		String returnStr = "";

		private int func;
		private Context c;
		private String eventShort;

		SearchCountRestCallAsyncTask(Context c, String eventShort){
			this.func = func;
			this.c = c;
			this.eventShort = eventShort;
		}

		//Before running code in separate thread
		@Override
		protected void onPreExecute() {
			tvNewTotalNumber.setVisibility(View.GONE);
			pbNewTotalNumber.setVisibility(View.VISIBLE);

			currentEvent.setViewSettings(viewSettings);
		}

		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params)
		{
			//Get the current thread's token
			synchronized (this)
			{
				try {
					returnStr = searchCountRestCall(c, eventShort);
					JSONObject searchCountResultJson = new JSONObject(returnStr);
					rest = extractSearchCountResult(c, searchCountResultJson);
					currentEvent.setNumberOfRecords(rest.getCount());
				} catch (InterruptedException e) {
					e.printStackTrace();
					returnStr = e.getMessage();
					rest.setErrorCode("-1");
					rest.setErrorMessage(e.getMessage());
					currentEvent.setNumberOfRecords("-1");
				} catch (Exception e) {
					e.printStackTrace();
					rest.setErrorCode("-1");
					rest.setErrorMessage(e.getMessage());
					currentEvent.setNumberOfRecords("-1");
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
			if (rest.getErrorCode().equalsIgnoreCase("0")){
				Toast.makeText(c, "Total: " + rest.getCount(), Toast.LENGTH_SHORT).show();
				pbNewTotalNumber.setVisibility(View.GONE);
				tvNewTotalNumber.setVisibility(View.VISIBLE);
				// make error message available
				tvNewTotalNumber.setText(rest.getCount());
			}
			else {
				tvNewTotalNumber.setVisibility(View.GONE);
				pbNewTotalNumber.setVisibility(View.VISIBLE);
				tvNewTotalNumber.setText(rest.getErrorMessage());
			}
		}
	}
}
