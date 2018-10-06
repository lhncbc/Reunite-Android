package com.pl.reunite;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.pl.reunite.Result.RequestAnonTokenResult;
import com.pl.reunite.Result.RestPingResult;
import com.pl.reunite.Result.RestSearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Activity to display lost person in list.
 * The lost person list comes from:
 * 1. for the first time, the data come from search from web server.
 * 2. for the second time and after, check the data from local database first, if available. Otherwise, get the data from web server.
 * 3. table the re-flash, it will clean the data in location database and get the updated list from web server
 * 4. filter is used to narrow down the search.
 */

/**
 *  1. replace SearchCountForEventsAsyncTask() by SearchRestCallAsyncTask()
 */

public class PatientListExActivity extends ActionBarListActivity {
    private static final String METHOD_SEARCH = "search";
	private static final String METHOD_GET_EVENT_LIST = "getEventData"; // changed from getEventList. version 7.2.8

	// return id
	static final int PICK_EVENT_REQUEST = 1;
	static final int DEFINE_FILTERS = 2;
	static final int VIEW_SETTINGS = 3;
    static final int SEARCH_BY_PHOTO = 4;
	
	private PatientsDataSource datasource;
    private ImageDataSource dataSourceImage;

    ReUnite app;
    String webServer = "";
    String nameSpace = "";
    String url = "";
    String soapAction = "";
    String soapActionGetEventList = "";

    MyPingEcho myPingEcho;
//    PingEchoResult pingEchoResult;
    RestPingResult restPingResult;

    ViewSettings viewSettings;

    String returnString;
    Event currentEvent;
	List<Patient> patientList = new ArrayList<Patient>();

	// Search events
	List<Event> eventList = new ArrayList<Event>();
	
	// LoadMore button
	int totalPages = 0;
	int totalRecords = 0;
	int pageSize = 0;
	int currentPage = 0;
	int currentPageStart = 0;
	
	private ProgressDialog m_ProgressDialog = null; 
    private ArrayList<Patient> m_patients = null;
    private PatientAdapter m_adapter;
    private Runnable viewPatients;
    
    Button eventsButton;
    Button filtersButton;

    Button nextPageButton;
    Button nameSearchButton;
    EditText nameSearchEditText;
    RadioButton radioButtonByName;
    RadioButton radioButtonByPhoto;

    TextView info1;
    TextView info2;
    TextView info3;
    TextView info4;
    TextView info5;
    
    private String searchTerm = "";

    //A ProgressDialog object  
    private ProgressDialog progressDialog;  
    
    // Defauls
    Defaults defaults;

    ListView listView;    
    
    Filters filters;
    
    @Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

   		// Start activity and pass the data 
   		StartPatientInforActivity(position);
   	}

	private void StartPatientInforActivity(int position) {
		Intent i;

        /**
         * multiple images
         * 1. anonymous
         * 2. authenticated users
         */
        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH){
            i = new Intent(PatientListExActivity.this, PatientInfoForLoggedInUserActivity.class);
        }
        else {
            i = new Intent(PatientListExActivity.this, PatientInfoActivity.class);
        }

		Patient p = new Patient(patientList.get(position), webServer);

        // try the simple way - start
        p.setEventName(currentEvent.getName());
        p.setEventShortName(currentEvent.getShortName());
        app.setCurSelPatient(p);
        // try the simple way - end

		final int result=15;
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

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_ex_list);

        // Initial with globe variables.
        app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        datasource = new PatientsDataSource(this);
        dataSourceImage = new ImageDataSource(this);

        webServer = app.getWebServer();
        url = WebServer.PL_URL;
        nameSpace = WebServer.PL_NAMESPACE;
        soapAction = WebServer.PL_URL + "#" + METHOD_SEARCH;
        soapActionGetEventList = WebServer.PL_URL + "#" + METHOD_GET_EVENT_LIST;

        if (webServer.equalsIgnoreCase(WebServer.PL_NAME) == true){
            url = WebServer.PL_URL;
            nameSpace = WebServer.PL_NAMESPACE;
            soapAction = url + "#" + METHOD_SEARCH;
            soapActionGetEventList = WebServer.PL_URL + "#" + METHOD_GET_EVENT_LIST;
        }
        else if (webServer.equalsIgnoreCase(WebServer.PL_NAME_STAGE) == true){
            url = WebServer.PL_URL_STAGE;
            nameSpace = WebServer.PL_NAMESPACE_STAGE;
            soapAction = url + "#" + METHOD_SEARCH;
            soapActionGetEventList = WebServer.PL_URL + "#" + METHOD_GET_EVENT_LIST;
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.web_server_is_not_found) + ": " + webServer.toString(), Toast.LENGTH_LONG).show();
            return;
        }

        myPingEcho = new MyPingEcho(app.getWebServer()); // add argument. changed in version 7.1.3
        restPingResult = new RestPingResult(this);

        Initialize();

        /**
         * no need the progress dialogue, which cause the crash when rotating the screen.
         * This line cause the crash in screen rotation in latest SDK:
         * new SearchCountForEventsOnCreateAsyncTask().execute();
         * Took it out and rewrote.
         */
        searchAndDisplay();

        // added in version 7.2.6
        // clear messages for the user.
        if (totalRecords == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(PatientListExActivity.this);
            builder.setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(getResources().getString(R.string.no_record_is_found))
                    .setCancelable(false)
                    .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void searchAndDisplay() {
        currentEvent.setViewSettings(viewSettings);

        HideInfoTextView();
        currentPage = 0;
        currentPageStart = 0;
        totalRecords = Integer.parseInt(currentEvent.getNumberOfRecords().toString());
        ShowNextPage();

        // 1
        m_adapter.notifyDataSetChanged();
        for (int i = 0; i < patientList.size(); i ++){
            Patient p = new Patient(patientList.get(i), webServer);
            if (p != null){
                m_adapter.add(p);
            }
        }
        m_adapter.notifyDataSetChanged();

        // 2
        viewPatients = new Runnable(){
            public void run() {
                getOrders();
            }
        };

        // test 3
//        if (nextPageButton != null){
            nextPageButton.setEnabled(false);
//        }

        Thread thread =  new Thread(null, viewPatients, "Background");
        thread.start();
    }

    private void Initialize() {
        defaults = new Defaults();

        String eventName = app.getLastEvent().getName();
        String eventShortName = app.getLastEvent().getShortName();

        if (eventName.isEmpty() || eventShortName.isEmpty()){
            eventName = defaults.getEventNamePreferences().toString();
            eventShortName = defaults.getEventShortNamePreferences().toString();
            if (eventName.isEmpty() == true || eventShortName.isEmpty() == true){
                Event e = GetLatestEvent();
                if (e == null){
                    e = new Event(webServer);
                    e.toDefault();
                }
                eventName = e.getName();
                eventShortName = e.getShortName();

                app.setLastEvent(e);
            }
        }

		filters = new Filters();

		// Defaults
	    currentEvent = new Event(webServer);

	    currentEvent.setName(eventName);	
	    currentEvent.setShortName(eventShortName);

	    // animal
        currentEvent.setFilterSelSearchPerson(false);
        currentEvent.setFilterSelSearchAnimal(false);
        currentEvent.setFilterSelSearchBoth(true);
	    
	    currentEvent.setFilterGenderMale(true);
	    currentEvent.setFilterGenderFemale(true);
	    currentEvent.setFilterGenderComplex(true);
	    currentEvent.setFilterGenderUnknown(true);

	    currentEvent.setFilterAgeAdult(true);
	    currentEvent.setFilterAgeChild(true);
	    currentEvent.setFilterAgeUnknown(true);
	    
	    currentEvent.setFilterStatusMissing(true);
	    currentEvent.setFilterStatusAliveAndWell(true);
	    currentEvent.setFilterStatusInjured(true);
	    currentEvent.setFilterStatusDeceased(true);
	    currentEvent.setFilterStatusUnknown(true);
	    currentEvent.setFilterStatusFound(true);
	    
	    currentEvent.setNumberOfRecords("100");
	    	    
	    currentEvent.setSearchTerm("");
	    
	    viewSettings = new ViewSettings();
	    viewSettings.setPhotoSel(defaults.getViewPhotoSelPreferences());
	    viewSettings.setPageSize(defaults.getViewPageSizePreferences());

	    // pages 
	    totalRecords = Integer.parseInt(currentEvent.getNumberOfRecords());
	    pageSize = viewSettings.getPageSize();
	    totalPages = (int) Math.ceil(totalRecords / pageSize) + 1;
	    currentPage = 1;
	    currentPageStart = 0;
	    
	    // define events button and launch the events list
	    eventsButton = (Button) findViewById(R.id.buttonEvents);
	    eventsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SelectEvent();
            }
        });	 
	    
	    filtersButton = (Button) findViewById(R.id.buttonFilters);
	    filtersButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	SelectFilters();
            }
        });	 
	    
//	    viewButton = (Button) findViewById(R.id.buttonView);
//	    viewButton.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//            	ViewSettings();
//            }
//        });
        radioButtonByName = (RadioButton) findViewById(R.id.radioButtonByName);
        radioButtonByName.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (radioButtonByName.isChecked() == true){
                    viewSettings.setIsImageSearch(0);
                }
                else {
                    viewSettings.setIsImageSearch(ViewSettings.IMAGE_SEARCH);
                    nameSearchEditText.setText("");
                    TurnOffSoftKeyBoard();
                }
            }
        });
        radioButtonByName.setChecked(true);
        radioButtonByPhoto = (RadioButton) findViewById(R.id.radioButtonByPhoto);
        radioButtonByPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (radioButtonByPhoto.isChecked() == true){
                    viewSettings.setIsImageSearch(ViewSettings.IMAGE_SEARCH);
                    nameSearchEditText.setText("");
                    TurnOffSoftKeyBoard();
                }
                else {
                    viewSettings.setIsImageSearch(0);
                }
            }
        });
        radioButtonByPhoto.setChecked(false);
        viewSettings.setIsImageSearch(0);

        nameSearchButton = (Button) findViewById(R.id.buttonNameSearch);
	    nameSearchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (radioButtonByName.isChecked() == true){
                    TurnOffSoftKeyBoard();
                    ClearPatientList();
                    GetSearchTerm();
//            	SearchCountForEvents();
                    viewSettings.setIsImageSearch(0);
                    viewSettings.setEncodedImage("");
//                    new SearchCountForEventsAsyncTask().execute();
                    checkConnectionAndRunSearch(PatientListExActivity.this);
                }
                else {
                    nameSearchEditText.setText("");
                    Intent i = new Intent(PatientListExActivity.this, SearchByPhotoActivity.class);
                    startActivityForResult(i, SEARCH_BY_PHOTO);
                }
            }
        });	    		
	    nameSearchEditText = (EditText) findViewById(R.id.editTextNameSearch);
	    nameSearchEditText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	DisplayInfoTextView();
            }
        });

        // add key listener to catch return key
        // added in version 6.1.3
        nameSearchEditText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_ENTER:
                            TurnOffSoftKeyBoard();
                            ClearPatientList();
                            GetSearchTerm();
//                            new SearchCountForEventsAsyncTask().execute();
                            checkConnectionAndRunSearch(PatientListExActivity.this);
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        // Define adapter and footer.
        m_patients = new ArrayList<Patient>();
        this.m_adapter = new PatientAdapter(this, R.layout.item_patient_simple_view, m_patients);
    	listView = (ListView)findViewById(android.R.id.list);

    	if (totalRecords > pageSize){
        	nextPageButton = new Button(PatientListExActivity.this);
        	nextPageButton.setText(getResources().getString(R.string.more) + "...");

            /**
             * Will work on all Android OS.
             * Changes made in version 7.1.3
             */
            // start to change
            Drawable drawable;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                drawable = getResources().getDrawable(R.drawable.more_button, this.getTheme());
            } else {
                drawable = getResources().getDrawable(R.drawable.more_button);
            }
            // set background
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                nextPageButton.setBackground(drawable);
            } else {
                nextPageButton.setBackgroundDrawable(drawable);
            }
            // end of changes

            nextPageButton.setOnClickListener(new View.OnClickListener() {
    			public void onClick(View v) {
                    if (WebServer.isOnline(PatientListExActivity.this) == true) {
                        app.setOffline(false);
                        ShowNextPage();
                        int index = listView.getFirstVisiblePosition();
                        listView.setSelectionFromTop(index, 0);
                        Toast.makeText(PatientListExActivity.this, getResources().getString(R.string.page) + " " + String.valueOf(currentPage), Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PatientListExActivity.this);
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
    			}
    		});
        	listView.addFooterView(nextPageButton);
        }

        setListAdapter(this.m_adapter);
	    
	    info1 = (TextView) findViewById(R.id.textViewInfo1);
	    info2 = (TextView) findViewById(R.id.textViewInfo2);
	    info3 = (TextView) findViewById(R.id.textViewInfo3);
	    info4 = (TextView) findViewById(R.id.textViewInfo4);
	    info5 = (TextView) findViewById(R.id.textViewInfo5);
	    DisplayInfoTextView();
	}
	
	private Event GetLatestEvent() {
		SearchForEventList();
		if (eventList.isEmpty() == true){
			return null;
		}
		return eventList.get(0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PICK_EVENT_REQUEST){
			String eventname = data.getExtras().getString("eventname").trim();
			String eventshortname = data.getExtras().getString("eventshortname").trim();
			if (eventname.isEmpty() == false && eventshortname.isEmpty() == false){
				defaults.saveEventNamePreferences(eventname);
				defaults.saveEventShortNamePreferences(eventshortname);
				currentEvent.setName(eventname);
				currentEvent.setShortName(eventshortname);

                app.setLastEvent(currentEvent);
				
				// Start search
				DisplayInfoTextView();
            	ClearPatientList();
            	ResetSearchTerm();
//            	SearchCountForEvents();
//        	    new SearchCountForEventsAsyncTask().execute();
                checkConnectionAndRunSearch(PatientListExActivity.this);
			}
		}
		else if (requestCode == DEFINE_FILTERS){
			// get old event first
			Event oldEvent = new Event(currentEvent, webServer);
			
		    currentEvent.setName(data.getExtras().getString("name"));	
		    currentEvent.setShortName(data.getExtras().getString("shortname"));

            // animal
            currentEvent.setFilterSelSearchPerson(data.getExtras().getBoolean("filterSelSearchPerson"));
            currentEvent.setFilterSelSearchAnimal(data.getExtras().getBoolean("filterSelSearchAnimal"));
            currentEvent.setFilterSelSearchBoth(data.getExtras().getBoolean("filterSelSearchBoth"));

            currentEvent.setFilterGenderMale(data.getExtras().getBoolean("filterMale"));
		    currentEvent.setFilterGenderFemale(data.getExtras().getBoolean("filterFemale"));
		    currentEvent.setFilterGenderComplex(data.getExtras().getBoolean("filterComplex"));
		    currentEvent.setFilterGenderUnknown(data.getExtras().getBoolean("filterUnknown"));

		    currentEvent.setFilterAgeAdult(data.getExtras().getBoolean("filterAgeAdult"));
		    currentEvent.setFilterAgeChild(data.getExtras().getBoolean("filterAgeChild"));
		    currentEvent.setFilterAgeUnknown(data.getExtras().getBoolean("filterAgeUnknown"));

		    currentEvent.setFilterStatusMissing(data.getExtras().getBoolean("filterStatusMissing"));
		    currentEvent.setFilterStatusAliveAndWell(data.getExtras().getBoolean("filterStatusAliveAndWell"));
		    currentEvent.setFilterStatusInjured(data.getExtras().getBoolean("filterStatusInjured"));
		    currentEvent.setFilterStatusDeceased(data.getExtras().getBoolean("filterStatusDeceased"));
		    currentEvent.setFilterStatusUnknown(data.getExtras().getBoolean("filterStatusUnknown"));
		    currentEvent.setFilterStatusFound(data.getExtras().getBoolean("filterStatusFound"));

		    currentEvent.setNumberOfRecords(data.getExtras().getString("numberOfRecords"));

			currentEvent.setSearchTerm(data.getExtras().getString("searchTerm"));

            // photo section - start
            ViewSettings old = new ViewSettings(viewSettings);
            viewSettings.setPhotoSel(data.getExtras().getInt("photoSel"));
            viewSettings.setPageSize(data.getExtras().getInt("pageSize"));
            pageSize = viewSettings.getPageSize();
            // photo section - end
			
			if (isFilterChanged(currentEvent, oldEvent) == true || viewSettings.isDifferent(old) == true){
                // photo section - start
                defaults.saveViewPhotoSelPreferences(viewSettings.getPhotoSel());
                defaults.saveViewPageSizePreferences(viewSettings.getPageSize());
                // photo section - end
				DisplayInfoTextView();
				ClearPatientList();
//				new SearchCountForEventsAsyncTask().execute();
                checkConnectionAndRunSearch(PatientListExActivity.this);
			}
		}
		else if (requestCode == VIEW_SETTINGS){
			ViewSettings old = new ViewSettings(viewSettings);
			
			// Receive the data.
			viewSettings.setPhotoSel(data.getExtras().getInt("photoSel"));	
			viewSettings.setPageSize(data.getExtras().getInt("pageSize"));
            pageSize = viewSettings.getPageSize();
			
			if (viewSettings.isDifferent(old) == true){
				defaults.saveViewPhotoSelPreferences(viewSettings.getPhotoSel());
				defaults.saveViewPageSizePreferences(viewSettings.getPageSize());
				DisplayInfoTextView();
				ClearPatientList();
//				new SearchCountForEventsAsyncTask().execute();
                checkConnectionAndRunSearch(PatientListExActivity.this);
			}
		}
        else if (requestCode == SEARCH_BY_PHOTO){
            String returnStr = data.getExtras().getString("ENCODED_PHOTO").trim();
            if (returnStr.isEmpty()){
                Toast.makeText(this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show();
                return;
            }

            String encodedPhoto = app.getCurEncodedImage();
            app.setCurEncodedImage("");

            if (encodedPhoto.isEmpty()){
                Toast.makeText(this, getResources().getString(R.string.no_photo_is_found), Toast.LENGTH_SHORT).show();
                return;
            }
            ClearPatientList();
            GetSearchTerm();

            viewSettings.setIsImageSearch(ViewSettings.IMAGE_SEARCH);
            viewSettings.setEncodedImage(encodedPhoto);
            viewSettings.setSortBy(ViewSettings.SORT_SIMILARITY_DESC);

//            new SearchCountForEventsAsyncTask().execute();
            checkConnectionAndRunSearch(PatientListExActivity.this);
        }
	}
	
	private boolean isFilterChanged(Event cur, Event old) {
		// no change return false
		// changed return true
	    if (cur.getName().equalsIgnoreCase(old.getName()) == false){
	    	return true;
	    }
	    if (cur.getShortName().equalsIgnoreCase(old.getShortName()) == false){
	    	return true;
	    }

	    // animal - start
        if (cur.isFilterSelSearchPerson() != old.isFilterSelSearchPerson()){
            return true;
        }
        if (cur.isFilterSelSearchAnimal() != old.isFilterSelSearchAnimal()){
            return true;
        }
        if (cur.isFilterSelSearchBoth() != old.isFilterSelSearchBoth()){
            return true;
        }
        // animal - end

	    if (cur.getFilterGenderMale() != old.getFilterGenderMale()){
	    	return true;
	    }
	    if (cur.getFilterGenderFemale() != old.getFilterGenderFemale()){
	    	return true;
	    }
	    if (cur.getFilterGenderComplex() != old.getFilterGenderComplex()){
	    	return true;
	    }
	    if (cur.getFilterGenderUnknown() != old.getFilterGenderUnknown()){
	    	return true;
	    }
	    
	    if (cur.getFilterAgeAdult() != old.getFilterAgeAdult()){
	    	return true;
	    }
	    if (cur.getFilterAgeChild() != old.getFilterAgeChild()){
	    	return true;
	    }
	    if (cur.getFilterAgeUnknown() != old.getFilterAgeUnknown()){
	    	return true;
	    }

	    if (cur.getFilterStatusMissing() != old.getFilterStatusMissing()){
	    	return true;
	    }
	    if (cur.getFilterStatusAliveAndWell() != old.getFilterStatusAliveAndWell()){
	    	return true;
	    }
	    if (cur.getFilterStatusInjured() != old.getFilterStatusInjured()){
	    	return true;
	    }
	    if (cur.getFilterStatusDeceased() != old.getFilterStatusDeceased()){
	    	return true;
	    }
	    if (cur.getFilterStatusUnknown() != old.getFilterStatusUnknown()){
	    	return true;
	    }
	    if (cur.getFilterStatusFound() != old.getFilterStatusFound()){
	    	return true;
	    }
	    
	    if (cur.getNumberOfRecords().equalsIgnoreCase(old.getNumberOfRecords()) == false){
	    	return true;
	    }
	    
	    if (cur.getSearchTerm().equalsIgnoreCase(old.getSearchTerm()) == false){
	    	return true;
	    }

		return false;
	}

	@Override
	protected void onResume() {
		super.onResume();

        // tracker
        app.tracker().setScreenName(app.TRACK_SEARCH + " (" + currentEvent.getShortName() + ")");
        app.tracker().send(new HitBuilders.ScreenViewBuilder().build());

        // check internet
        new CheckInternetConnectionAsyncTask().execute();
	}
	
	private void ResetSearchTerm() {
		nameSearchEditText.setText("");
		if (searchTerm.isEmpty() == false){
			currentEvent.setSearchTerm("");
		}
	}

	protected void ViewSettings() {
		Intent i = new Intent(PatientListExActivity.this, ViewActivity.class);
		i.putExtra("photoSel", viewSettings.getPhotoSel());    			
		i.putExtra("pageSize", viewSettings.getPageSize()); 
		startActivityForResult(i, VIEW_SETTINGS);	
	}

	private void SelectEvent() {
		Intent i = new Intent(PatientListExActivity.this, EventListForReportActivity.class);
		startActivityForResult(i, PICK_EVENT_REQUEST);
	}
	
	protected void SelectFilters() {
		Intent i = new Intent(PatientListExActivity.this, FiltersActivity.class);
		
		i.putExtra("name", currentEvent.getName());    			
		i.putExtra("shortname", currentEvent.getShortName());

		// animal - start
        i.putExtra("filterSelSearchPerson", currentEvent.isFilterSelSearchPerson());
        i.putExtra("filterSelSearchAnimal", currentEvent.isFilterSelSearchAnimal());
        i.putExtra("filterSelSearchBoth", currentEvent.isFilterSelSearchBoth());
        // animal - end

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

        final int result=19;
		startActivityForResult(i, DEFINE_FILTERS);	
	}

	protected void ClearPatientList() {
		m_adapter.clear();
        m_adapter.notifyDataSetChanged();
        patientList.clear();
	}

	private void GetSearchTerm() {
		searchTerm = nameSearchEditText.getText().toString().trim();
		if (searchTerm.isEmpty() == false){
			currentEvent.setSearchTerm(searchTerm);
		}
        else {
            currentEvent.setSearchTerm("");
        }
	}
	
	protected void DisplayInfoTextView() {
		info1.setVisibility(View.VISIBLE);
		info2.setVisibility(View.VISIBLE);
		info3.setVisibility(View.VISIBLE);
		info4.setVisibility(View.VISIBLE);
		info5.setVisibility(View.VISIBLE);

        if (app.isTablet() == false){
            listView.setVisibility(View.GONE);
        }
        else {
            listView.setVisibility(View.VISIBLE);
        }
	}

	protected void HideInfoTextView() {
		listView.setVisibility(View.VISIBLE);

        if (app.isTablet() == false) {
            info1.setVisibility(View.GONE);
            info2.setVisibility(View.GONE);
            info3.setVisibility(View.GONE);
            info4.setVisibility(View.GONE);
            info5.setVisibility(View.GONE);
        }
        else {
            info1.setVisibility(View.VISIBLE);
            info2.setVisibility(View.VISIBLE);
            info3.setVisibility(View.VISIBLE);
            info4.setVisibility(View.VISIBLE);
            info5.setVisibility(View.VISIBLE);
        }
	}

	private void TurnOffSoftKeyBoard() {
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.hideSoftInputFromWindow(nameSearchEditText.getWindowToken(), 0);
	}

	private void ShowNextPage() {
		currentPage++;
		currentPageStart = (currentPage - 1) * pageSize;
		
		if (currentPageStart > totalRecords){
		    Toast.makeText(PatientListExActivity.this, getResources().getString(R.string.completed), Toast.LENGTH_SHORT).show();
			return;
		}

        /**
         *  Search patients together with images
          */
        SearchPatientsThreadMethod();

//        new SearchPatientsAsyncTask().execute();
        m_adapter.notifyDataSetChanged();
        for (int i = currentPageStart; i < patientList.size(); i++){
        	Patient p = new Patient(patientList.get(i), webServer);
        	if (p != null){
        		m_adapter.add(p);
        	}
        }
        
        m_adapter.notifyDataSetChanged();

        // test
//        if (nextPageButton != null){
        	nextPageButton.setEnabled(false);
//        }
        
        // move to the top
    	int first = listView.getFirstVisiblePosition();
    	int last = listView.getLastVisiblePosition();
    	listView.smoothScrollToPosition(last + last - first - 1);
    	Toast.makeText(PatientListExActivity.this, getResources().getString(R.string.page) + " " + String.valueOf(currentPage), Toast.LENGTH_SHORT).show();
        
        Thread thread =  new Thread(null, viewPatients, "MagentoBackground");
        thread.start();

        // track the report
        app.tracker().send(new HitBuilders.EventBuilder()
                .setCategory(currentEvent.getShortName())
                .setAction(app.TRACK_SEARCH)
                .setLabel(app.SUCCEED)
                .build());
	}

	public void ShowAllInOnePage(RestSearchResult rest){
        currentPage++;
        currentPageStart = (currentPage - 1) * pageSize;

        if (currentPageStart > totalRecords){
            Toast.makeText(PatientListExActivity.this, getResources().getString(R.string.completed), Toast.LENGTH_SHORT).show();
            return;
        }

        patientList.clear();
        patientList.addAll(rest.getPatientList());

        m_adapter.notifyDataSetChanged();
        for (int i = currentPageStart; i < patientList.size(); i++){
            Patient p = new Patient(patientList.get(i), webServer);
            if (p != null){
                m_adapter.add(p);
            }
        }

        m_adapter.notifyDataSetChanged();

        // test
//        if (nextPageButton != null){
            nextPageButton.setEnabled(false);
//        }

        // move to the top
        int first = listView.getFirstVisiblePosition();
        int last = listView.getLastVisiblePosition();
        listView.smoothScrollToPosition(last + last - first - 1);
        Toast.makeText(PatientListExActivity.this, getResources().getString(R.string.page) + " " + String.valueOf(currentPage), Toast.LENGTH_SHORT).show();

        Thread thread =  new Thread(null, viewPatients, "MagentoBackground");
        thread.start();
    }

    // Modified for api version v34
	private void SearchPatientsThreadMethod() {
        WebServer ws = new WebServer(app.getWebServer()); // version 7.1.3
        ws.setTokenStatus(app.getTokenStatus());
        ws.setTokenAnonymous(app.getTokenAnonymous());
        ws.setToken(app.getToken());
        ws.setSearchCountOnly(false);
        ws.setCurPageStart(currentPageStart);
        ws.setQuery(searchTerm);
        filters = currentEvent.generateFilters();
        RestSearchResult sr = ws.restCallSearchCountV34(this, filters, viewSettings, currentEvent.getShortName());
        if (sr.getErrorCode().equals("0")){
            patientList.addAll(sr.getPatientList());
        }
	}

	private Runnable returnRes = new Runnable() {
        public void run() {
            if(m_patients != null && m_patients.size() > 0){
                m_adapter.clear();
                for(int i=0;i<m_patients.size();i++){
                	m_adapter.add(m_patients.get(i));
                }
            }

            if (Integer.parseInt(currentEvent.getNumberOfRecords()) <= patientList.size()){
            	nextPageButton.setEnabled(false); 
            }
            else {
            	nextPageButton.setEnabled(true);
            }
        }
    };

    /**
     *  Save patients information into database.
     *  Add multiple images, we need to add images to database as well.
     */
    private void getOrders(){
          try{
       		  m_patients = new ArrayList<Patient>();
        	  int currentSize = patientList.size();
			  datasource.open();
              dataSourceImage.open();
              for (int i = 0; i < currentSize; i ++){
            	  Patient p = new Patient(patientList.get(i), webServer);
            	  if (p.getStatusPhotoDownload() == false){
            		  if (p.getImageUrl().equalsIgnoreCase("null") == true){
            			  p.setPhoto(null);
            		  }
                      else if (p.getImageUrl().isEmpty()) {
                          p.setPhoto(null);
                      }
                      else {
                          /**
                           * 1. get from database
                           * 2. if not found in database, download through url,
                           * 3. if found in database, retrieve it.
                           * 4. resize image, encoded image, save to database.
                           */
            			  Patient pData = datasource.getPatient(p.getImageUrl(), webServer);
            			  if (pData == null){
            				  p.downloadPatientPhoto();

                              /**
                               * Simple is better.
                               * improve listview image display
                               * version 7.1.5
                               * start
                               */
                              int outWidth;
                              int outHeight;
                              int inWidth = p.getPhoto().getWidth();
                              int inHeight = p.getPhoto().getHeight();
                              if(inWidth > inHeight){
                                  outWidth = Image.MAX_SIZE_ICON;
                                  outHeight = (inHeight * Image.MAX_SIZE_ICON) / inWidth;
                              } else {
                                  outHeight = Image.MAX_SIZE_ICON;
                                  outWidth = (inWidth * Image.MAX_SIZE_ICON) / inHeight;
                              }
                              Bitmap resize = Bitmap.createScaledBitmap(p.getPhoto(), outWidth, outHeight, false);
                              // end

                      		  p.setPhoto(resize);
            				  p.encodePhoto();
                  			  p.setStatusReport(Patient.CACHE);
                  			  datasource.createPatient(p);
            			  }
            			  else {
            				  if (pData.getPhoto() != null){

                                  /**
                                   * Simple is better.
                                   * improve listview image display
                                   * version 7.1.5
                                   * start
                                   */
                                  int outWidth;
                                  int outHeight;
                                  int inWidth = pData.getPhoto().getWidth();
                                  int inHeight = pData.getPhoto().getHeight();
                                  if(inWidth > inHeight){
                                      outWidth = Image.MAX_SIZE_ICON;
                                      outHeight = (inHeight * Image.MAX_SIZE_ICON) / inWidth;
                                  } else {
                                      outHeight = Image.MAX_SIZE_ICON;
                                      outWidth = (inWidth * Image.MAX_SIZE_ICON) / inHeight;
                                  }
                                  Bitmap resize = Bitmap.createScaledBitmap(pData.getPhoto(), outWidth, outHeight, false);
                                  // end

            					  pData.setPhoto(resize);
            					  pData.decodePhoto();
            				  }
            				  p.setPhotoData(pData.getPhotoData());
            				  p.decodePhoto();
            			  }
            		  }
            		  p.setStatusPhotoDownload(true);


            		  patientList.set(i, p);
            	  }
           		  m_patients.add(p);
              }
			  datasource.close();

			  Log.i("ARRAY", ""+ m_patients.size());
            } catch (Exception e) { 
              Log.e("BACKGROUND_PROC", e.getMessage());
            }
            runOnUiThread(returnRes);            
        }
    
    private class PatientAdapter extends ArrayAdapter<Patient> {

        private ArrayList<Patient> items;

        public PatientAdapter(Context context, int textViewResourceId, ArrayList<Patient> items) {
                super(context, textViewResourceId, items);
                this.items = items;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.item_patient_simple_view, null);
                }
                Patient p = items.get(position);
                if (p != null) {
                	ProgressBar progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
                	ImageView imageView = (ImageView) v.findViewById(R.id.imageViewPatientSmall);
                	TextView itemName = (TextView) v.findViewById(R.id.textPatientName);
                	TextView itemOptStatus = (TextView) v.findViewById(R.id.textOptStatus);
                	TextView itemStatusSahanaUpdated = (TextView) v.findViewById(R.id.textStatusSahanaUpdated);
                	TextView itemAge = (TextView) v.findViewById(R.id.textAge);
                	TextView itemGender = (TextView) v.findViewById(R.id.textGender);
                	TextView itemNumber = (TextView) v.findViewById(R.id.textNumber);
                    TextView itemEvent = (TextView) v.findViewById(R.id.textEvent);
                    TextView itemPersonAnimal = (TextView) v.findViewById(R.id.textViewPersonAnimal);

                	// use full name only
                    // modified in version 9.2.9
                    String name = p.getFullName();
                    if (name.isEmpty() || name.equalsIgnoreCase("unk")){
                        name =  getResources().getString(R.string.unknown);
                    }
            		itemName.setText(name);
            		
                    // replace age section
                    // built in versionCode 7020601, version 7.2.6-beta
                    // start
                    String strAge1 = getResources().getString(R.string.approximate_age) + ":";
                    String strAge2 = p.getYearsOld();
                    if (strAge2.equalsIgnoreCase("null")){
                        strAge2 = getResources().getString(R.string.unknown);// In listview, displays '?'
                    }
                    else if (strAge2.equalsIgnoreCase("-1") == true || strAge2.isEmpty() == true) {
                        strAge2 = getResources().getString(R.string.unknown);// In listview, displays '?'
                    }
                    itemAge.setText(strAge1 + strAge2);
                    // end

                    // Gender
                    String strG1 = getResources().getString(R.string.gender) + ":";
                    String strG2 = p.getGender();
            		if (strG2.equalsIgnoreCase("mal") == true || strG2.equalsIgnoreCase("male") == true) {
            			strG2 = getResources().getString(R.string.male);
            		}
            		else if (strG2.equalsIgnoreCase("fml") == true || strG2.equalsIgnoreCase("female") == true){
            			strG2 = getResources().getString(R.string.female);
            		}
            		else if (strG2.equalsIgnoreCase("cpx") == true || strG2.equalsIgnoreCase("complex") == true){
            			strG2 = getResources().getString(R.string.complex);
            		}
            		else if (strG2.equalsIgnoreCase("unk") == true || strG2.equalsIgnoreCase("unknown") == true){
            			strG2 = getResources().getString(R.string.unknown); // In listview, displays '?'
            		}
            		else {
            			// Do nothing. Take it as it is.
            		}
            		itemGender.setText(strG1 + strG2);
            		
            		// Status unknown
            		String strS1 = "";
            		String strS2 = p.getOptStatus();
            		int color = 0;
            		
            		// Here is display, convert from short to long form
            		if (strS2.equalsIgnoreCase(Patient.UNK) == true){
//                        strS2 = Patient.UNKNOWN;
                        strS2 = getResources().getString(R.string.unknown);
//            			color = Color.GRAY;
            		}
            		else if (strS2.equalsIgnoreCase(Patient.INJ) == true){
//            			strS2 = Patient.INJURED;
                        strS2 = getResources().getString(R.string.injured);
//            			color = Color.YELLOW;
            		}
            		else if (strS2.equalsIgnoreCase(Patient.ALI) == true){
//                        strS2 = Patient.ALIVE_AND_WELL;
                        strS2 = getResources().getString(R.string.alive_and_well);;
//            			color = Color.GREEN;
            		}
            		else if (strS2.equalsIgnoreCase(Patient.ALIVE_AND_WELL) == true){
//                        strS2 = Patient.ALIVE_AND_WELL;
                        strS2 = getResources().getString(R.string.alive_and_well);
//            			color = Color.GREEN;
            		}
            		else if (strS2.equalsIgnoreCase(Patient.MIS) == true){
//                        strS2 = Patient.MISSING;
                        strS2 = getResources().getString(R.string.missing);
//            			color = Color.CYAN;
            		}
            		else if (strS2.equalsIgnoreCase(Patient.DEC) == true){
//            			strS2 = Patient.DECEASED;
                        strS2 = getResources().getString(R.string.deceased);
//                        color = Color.LTGRAY;
            		}
            		else if (strS2.equalsIgnoreCase(Patient.FND) == true){
//                        strS2 = Patient.FOUND;
                        strS2 = getResources().getString(R.string.found);
//            			color = Color.rgb(255, 160, 122);
            		}
            		itemOptStatus.setText(strS1 + strS2);
            		itemOptStatus.setTextColor(Color.BLACK);
            		
            		strS1 = p.getStatusSahanaUpdated();
            		itemStatusSahanaUpdated.setText(strS1);
            			
                   	if (p.getStatusPhotoDownload() == false){
                		progressBar.setVisibility(View.VISIBLE);
                		imageView.setVisibility(View.GONE);
                	}
                	else{
                		progressBar.setVisibility(View.GONE);
                		imageView.setVisibility(View.VISIBLE);
               			Bitmap bitmap = p.getPhoto();
            			if (bitmap != null) {
            				imageView.setImageBitmap(bitmap);
            			}
            			else {
            				imageView.setImageResource(R.drawable.questionmark);			
            			}			
               		                		
                	}             	
  
            		// Number
            		String strN = String.valueOf(position + 1) + "/" + String.valueOf(totalRecords);
            		itemNumber.setText(strN);

            		// Event
            		String strE1 = "";
            		String strE2 = currentEvent.getName();
            		itemEvent.setText(strE1 + strE2);

            		if (p.getPa() == Patient.PERSON){
            		    itemPersonAnimal.setText("Person");
                    }
                    else {
                        itemPersonAnimal.setText("Animal");
                    }
                }
                return v;
        }
}
    
    public class Order {
        
        private String orderName;
        private String orderStatus;
        private boolean real;
        
        public Order(){
        	real = false;
        	orderName = "Unknown";
        	orderStatus = "Unknown";
        }
        
        public boolean getReal(){
        	return real;
        }
        public void setReal(boolean real){
        	this.real = real;
        }
        
        public String getOrderName() {
            return orderName;
        }
        public void setOrderName(String orderName) {
            this.orderName = orderName;
        }
        public String getOrderStatus() {
            return orderStatus;
        }
        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }
    } 
    
    public class Defaults {
		private String eventName;
		private String eventShortName;
		private String strDate;
		private String strStreet;

		// filters
		private String searchTerm = "";
	    private boolean filterGenderComplex;

	    // animal
        private boolean filterSelSearchPerson;
        private boolean filterSelSearchAnimal;
        private boolean filterSelSearchBoth;

        private boolean filterGenderMale;
	    private boolean filterGenderFemale;
	    private boolean filterGenderUnknown;
	    private boolean filterAgeChild;
	    private boolean filterAgeAdult;
	    private boolean filterAgeUnknown;
	    
	    private int viewPhotoSel;
	    private int viewPageSize;
        private ViewSettings viewSettings = new ViewSettings();
		
		Defaults(){
			getEventNamePreferences();
			getEventShortNamePreferences();
			getDatePreferences();
			getStreetPreferences();
			
			getSearchTermPreferences();
			getFilterGenderComplexPreferences();

			// animal - start
            getFilterSelSearchPersonPreferences();
            getFilterSelSearchAnimalPreferences();
            getFilterSelSearchBothPreferences();
            // animal - end

			getFilterGenderMalePreferences();
			getFilterGenderFemalePreferences();
			getFilterGenderUnknownPreferences();
			getFilterAgeChildPreferences();
			getFilterAgeAdultPreferences();
			getFilterAgeUnknownPreferences();
			
			getViewPhotoSelPreferences();
			getViewPageSizePreferences();
		}
		
		public String getEventName(){
			return eventName;
		}
		public String getEventSortName(){
			return eventShortName;
		}
		public String getDate() {
			return strDate;
		}
		public String getStreet() {
			return strStreet;
		}

		public String getSearchTerm(){
			return searchTerm;
		}
		public boolean getFilterGenderComplex(){
			return filterGenderComplex;
		}

		// animal
        public boolean isFilterSelSearchPerson() {return filterSelSearchPerson;}
        public boolean isFilterSelSearchAnimal() {return filterSelSearchAnimal;}
        public boolean isFilterSelSearchBoth() {return filterSelSearchBoth;}

        // animal - start
        public boolean isFilterSelSearchPersonPreferences(){
            return filterSelSearchPerson;
        }
        public boolean isFilterSelSearchAnimalPreferences(){
            return filterSelSearchAnimal;
        }
        public boolean isFilterSelSearchBothPreferences(){
            return filterSelSearchBoth;
        }
        // animal - end

		public boolean getFilterGenderMale(){
			return filterGenderMale;
		}
		public boolean getFilterGenderFemale(){
			return filterGenderFemale;
		}
		public boolean getFilterGenderUnknown(){
			return filterGenderUnknown;
		}
		public boolean getFilterAgeChild(){
			return filterAgeChild;
		}
		public boolean getFilterAgeAdult(){
			return filterAgeAdult;
		}
		public boolean getFilterAgeUnknown(){
			return filterAgeUnknown;
		}
		
		public int getViewPhotoSel(){
			return viewPhotoSel;
		}
		public int getViewPageSize(){
			return viewPageSize;
		}

		public void reset() {
			eventName = "";
			eventShortName = "";
			strDate = "";
			strStreet = "";
			searchTerm = "";
		    filterGenderComplex = true;

		    // animal
            filterSelSearchPerson = false;
            filterSelSearchAnimal = false;
            filterSelSearchBoth = true;

		    filterGenderMale = true;
		    filterGenderFemale = true;
		    filterGenderUnknown = true;
		    filterAgeChild = true;
		    filterAgeAdult = true;
		    filterAgeUnknown = true;
		    
		    viewPhotoSel = ViewSettings.BOTH;
		    
			if (isLargeScreen() == true){
				viewPageSize = ViewSettings.PAGE_SIZE_15;
			}
			else {
				viewPageSize = ViewSettings.PAGE_SIZE_10;
			}			
			saveEventNamePreferences(eventName);
			saveEventShortNamePreferences(eventShortName);
			saveDatePreferences(strDate);
			saveStreetPreferences(strStreet);
			saveSearchTermPreferences(searchTerm);
			savefilterGenderComplexPreferences(filterGenderComplex);

			// animal
            saveFilterSelSearchPersonPreferences(filterSelSearchPerson);
            saveFilterSelSearchAnimalPreferences(filterSelSearchAnimal);
            saveFilterSelSearchBothPreferences(filterSelSearchBoth);

			saveFilterGenderMalePreferences(filterGenderMale);
			saveFilterGenderFemalePreferences(filterGenderFemale);
			saveFilterGenderUnknownPreferences(filterGenderUnknown);
			saveFilterAgeChildPreferences(filterAgeChild);
			saveFilterAgeAdultPreferences(filterAgeAdult);
			saveFilterAgeUnknownPreferences(filterAgeUnknown);
			saveViewPhotoSelPreferences(viewPhotoSel);
			saveViewPageSizePreferences(viewPageSize);
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

		private void saveEventNamePreferences(String eventName) {
			this.eventName = eventName;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("eventName", this.eventName);
			editor.commit();
		}

		private void saveEventShortNamePreferences(String eventShortName) {
			this.eventShortName = eventShortName;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("eventShortName", this.eventShortName);
			editor.commit();
		}
		
		private void saveDatePreferences(String strDate) {
			this.strDate = strDate;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("strDate", this.strDate);
			editor.commit();
		}

		private void saveStreetPreferences(String strStreet) {
			this.strStreet = strStreet;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("strStreet", this.strStreet);
			editor.commit();
		}

		private void saveSearchTermPreferences(String searchTerm) {
			this.searchTerm = searchTerm;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("searchTerm", this.searchTerm);
			editor.commit();
		}
				
		private void savefilterGenderComplexPreferences(boolean filterGenderComplex) {
			this.filterGenderComplex = filterGenderComplex;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("filterGenderComplex", this.filterGenderComplex);
			editor.commit();
		}

		// animal - start
        private void saveFilterSelSearchPersonPreferences(boolean filterSelSearchPerson) {
            this.filterGenderMale = filterGenderMale;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("filterGenderMale", this.filterGenderMale);
            editor.commit();
        }
        private void saveFilterSelSearchAnimalPreferences(boolean filterSelSearchAnimal) {
            this.filterGenderMale = filterGenderMale;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("filterGenderMale", this.filterGenderMale);
            editor.commit();
        }
        private void saveFilterSelSearchBothPreferences(boolean filterSelSearchBoth) {
            this.filterGenderMale = filterGenderMale;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("filterGenderMale", this.filterGenderMale);
            editor.commit();
        }
        // animal - end

		private void saveFilterGenderMalePreferences(boolean filterGenderMale) {
			this.filterGenderMale = filterGenderMale;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("filterGenderMale", this.filterGenderMale);
			editor.commit();
		}
		
		private void saveFilterGenderFemalePreferences(boolean filterGenderFemale) {
			this.filterGenderFemale = filterGenderFemale;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("filterGenderFemale", this.filterGenderFemale);
			editor.commit();
		}
		
		private void saveFilterGenderUnknownPreferences(boolean filterGenderUnknown) {
			this.filterGenderUnknown = filterGenderUnknown;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("filterGenderUnknown", this.filterGenderUnknown);
			editor.commit();
		}
		
		private void saveFilterAgeChildPreferences(boolean filterAgeChild) {
			this.filterAgeChild = filterAgeChild;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("filterAgeChild", this.filterAgeChild);
			editor.commit();
		}
		
		private void saveFilterAgeAdultPreferences(boolean filterAgeAdult) {
			this.filterAgeAdult = filterAgeAdult;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("filterAgeAdult", this.filterAgeAdult);
			editor.commit();
		}
		
		private void saveFilterAgeUnknownPreferences(boolean filterAgeUnknown) {
			this.filterAgeUnknown = filterAgeUnknown;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("filterAgeUnknown", this.filterAgeUnknown);
			editor.commit();
		}
		
		private void saveViewPhotoSelPreferences(int viewPhotoSel) {
			this.viewPhotoSel = viewPhotoSel;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt("photoSel", this.viewPhotoSel);
			editor.commit();
		}

		private void saveViewPageSizePreferences(int viewPageSize) {
			this.viewPageSize = viewPageSize;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putInt("pageSize", this.viewPageSize);
			editor.commit();
		}
		
		private String getEventNamePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.eventName = sharedPreferences.getString("eventName", "");
			return this.eventName;
		}

		private String getEventShortNamePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.eventShortName = sharedPreferences.getString("eventShortName", "");
			return this.eventShortName;
		}
	
		private String getDatePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.strDate = sharedPreferences.getString("strDate", "");
			return this.strDate;
		}

		private String getStreetPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.strStreet = sharedPreferences.getString("strStreet", "");
			return this.strStreet;
		}

		private String getSearchTermPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.searchTerm = sharedPreferences.getString("searchTerm", "");
			return this.searchTerm;
		}

		private boolean getFilterGenderComplexPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.filterGenderComplex = sharedPreferences.getBoolean("filterGenderComplex", true);
			return this.filterGenderComplex;
		}

		// animal - start
        private boolean getFilterSelSearchPersonPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.filterSelSearchPerson = sharedPreferences.getBoolean("filterSelSearchPerson", true);
            return this.filterSelSearchPerson;
        }
        private boolean getFilterSelSearchAnimalPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.filterSelSearchAnimal = sharedPreferences.getBoolean("filterSelSearchAnimal", true);
            return this.filterSelSearchAnimal;
        }
        private boolean getFilterSelSearchBothPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.filterSelSearchBoth = sharedPreferences.getBoolean("filterSelSearchBoth", true);
            return this.filterSelSearchBoth;
        }
        // animal - end

		private boolean getFilterGenderMalePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.filterGenderMale = sharedPreferences.getBoolean("filterGenderMale", true);
			return this.filterGenderMale;
		}

		private boolean getFilterGenderFemalePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.filterGenderFemale = sharedPreferences.getBoolean("filterGenderFemale", true);
			return this.filterGenderFemale;
		}

		private boolean getFilterGenderUnknownPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.filterGenderUnknown = sharedPreferences.getBoolean("filterGenderUnknown", true);
			return this.filterGenderUnknown;
		}
		
		private boolean getFilterAgeChildPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.filterAgeChild = sharedPreferences.getBoolean("filterAgeChild", true);
			return this.filterAgeChild;
		}

		private boolean getFilterAgeAdultPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.filterAgeAdult = sharedPreferences.getBoolean("filterAgeAdult", true);
			return this.filterAgeAdult;
		}

		private boolean getFilterAgeUnknownPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.filterAgeUnknown = sharedPreferences.getBoolean("filterAgeUnknown", true);
			return this.filterAgeUnknown;
		}

		private int getViewPhotoSelPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.viewPhotoSel = sharedPreferences.getInt("photoSel", ViewSettings.BOTH);
			return this.viewPhotoSel;
		}

		private int getViewPageSizePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			if (isLargeScreen() == true){
				this.viewPageSize = sharedPreferences.getInt("pageSize", ViewSettings.PAGE_SIZE_15);
			}
			else {
				this.viewPageSize = sharedPreferences.getInt("pageSize", ViewSettings.PAGE_SIZE_10);
			}			
			return this.viewPageSize;
		}
    }
    
	private void SearchForEventList() {
		returnString = "";

		// Better to use the threads. 
	    //limit the number of actual threads
	    int poolSize = 1;
	    ExecutorService service = Executors.newFixedThreadPool(poolSize);
	    List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

	    for (int n = 0; n < poolSize; n++)
	    {
	    	Future f = service.submit(new Runnable() {
	    		public void run(){
	    			// Search events and get event list
	    			SearchEvents();
	    			// Search patient and get patient list
	    			if (eventList.isEmpty() == true){
	    				return;
	    			}
	    			// Order the event by its date
	    			OrderEventsByDate();
	    		}

				private void OrderEventsByDate() {
					for (int i = 0; i < eventList.size(); i++){
						Event eI = eventList.get(i);
						for (int j = i + 1; j < eventList.size(); j++){
							Event eJ = eventList.get(j);
							if (CompareDate(eI, eJ) < 0){
						        Collections.swap(eventList, i, j);
						        eI = eventList.get(i);
							}
						}
					}
				}

				private int CompareDate(Event pI, Event pJ) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					try {
						Date dateI = sdf.parse(pI.getDate());
						Date dateJ = sdf.parse(pJ.getDate());
						return dateI.compareTo(dateJ);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return 0;
				}
	       });
	       futures.add(f);
	    }

	    // wait for all tasks to complete before continuing
	    for (Future<Runnable> f : futures)
	    {
	    	try {
				f.get(WebServer.WAITING_TIME, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				e.printStackTrace();
				returnString = "Time out!";
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread
	}
    
	private void SearchEvents() {
	   	SoapObject request = new SoapObject(nameSpace, METHOD_GET_EVENT_LIST);

        // add this for version 34
        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH) {
            request.addProperty("token", app.getToken());
        }
        else {
            request.addProperty("token", app.getTokenAnonymous());
        }
        // end

    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.dotNet = false;
    	envelope.setOutputSoapObject(request);
    	
    	HttpTransportSE aht = new HttpTransportSE(url);
        aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
    	try
    	{
			aht.debug = true;
    		aht.call(soapActionGetEventList, envelope);
			Log.e("Soap request ", aht.requestDump);
			Log.e("Soap response ", aht.responseDump);
			result = (SoapPrimitive)envelope.getResponse();  
    	} catch (Exception e) {
    		result = null;
    	}
    	
       	SoapObject resultRequestSOAP  = (SoapObject)envelope.bodyIn;
    	String evenListString = resultRequestSOAP.getPropertyAsString("events");
    	String errorCodeString = resultRequestSOAP.getPropertyAsString("errorCode");
    	String errorMessageString = resultRequestSOAP.getPropertyAsString("errorMessage");
    	
    	returnString = "Even List: \r\n" + evenListString + "\r\nError Code: : \r\n" + errorCodeString + "\r\nError Message: \r\n" + errorMessageString;    	

    	if (errorCodeString.equalsIgnoreCase("0") == true) {
    		JSONParserForEvent("events", evenListString);
    	}
    	
	}

    /**
     * getEventData replace getEventList
     * following function is rewritten.
     * version 7.2.8, version code 7020801
     */
	private void JSONParserForEvent(String string, String hospitalListString) {
        while (!eventList.isEmpty()){
            eventList.remove(0);
        }
		String toParseString = "{" + "\"" + string + "\":" + hospitalListString + "}";
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(toParseString);
			JSONArray  jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array
			for(int i = 0; i < jsonArray.length(); i++){
                Event e = new Event(webServer);
                e.JsonObjectToEvent(jsonArray.getJSONObject(i));
                eventList.add(e);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}     // create a json object from a string         
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.patient_list_ex_menu, menu);
        return true;
	}	
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            getActionBarHelper().setRefreshActionItemState(true);
            
            getWindow().getDecorView().postDelayed(
                    new Runnable() {
                        public void run() {
                            getActionBarHelper().setRefreshActionItemState(false);
                    		AlertDialog.Builder builder = new AlertDialog.Builder(PatientListExActivity.this);
                            builder.setMessage(getResources().getString(R.string.you_are_about_to_delete_all_the_photos_saved_in_cache))
                                   .setTitle(getResources().getString(R.string.are_you_sure_q))
                    		       .setCancelable(false)
                    		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    		           public void onClick(DialogInterface dialog, int id) {
                    		        	   datasource.open();
                    		        	   datasource.deleteAllCachePatients();
                    		        	   datasource.close();
                    		        	   
                           				DisplayInfoTextView();
                                    	ClearPatientList();
                                    	ResetSearchTerm();
//                                    	SearchCountForEvents();
//                                	    new SearchCountForEventsAsyncTask().execute();
                                           checkConnectionAndRunSearch(PatientListExActivity.this);
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
                    }, 1000);
        	break;
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
		Intent i = new Intent(PatientListExActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

    private void CleanPhotos() {
		AlertDialog.Builder builder = new AlertDialog.Builder(PatientListExActivity.this);
		builder.setMessage(getResources().getString(R.string.you_are_about_to_delete_all_the_photos_saved_in_cache))
			   .setTitle(getResources().getString(R.string.are_you_sure_q))
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   datasource.open();
		        	   datasource.deleteAllCachePatients();
		        	   datasource.close();
		        	   
		               Toast.makeText(PatientListExActivity.this, getResources().getString(R.string.all_photos_are_deleted), Toast.LENGTH_SHORT).show();
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

	private void Latency2() {
		Intent i = new Intent(PatientListExActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}
    
	private void GoBackToMainPage() {
		Intent i = new Intent(PatientListExActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}

    public String getAnonymousToken(){
        String errorCode = "";
        // get the anonymous token
        WebServer ws = new WebServer(app.getWebServer()); // version 7.1.3
        RequestAnonTokenResult requestAnonTokenResult = ws.restCallRequestAnonToken(this);
        String anonymousToken = requestAnonTokenResult.getTokenAnonymous();
        errorCode = requestAnonTokenResult.getErrorCode();
        String errorMsg = requestAnonTokenResult.getErrorMessage();
        int tokenStatus = ws.getTokenStatus();
        if (errorCode.equalsIgnoreCase("0") == true){
            app.setTokenAnonymous(anonymousToken);
            app.setTokenStatus(ReUnite.TOKEN_ANONYMOUS);
            app.recordTimeWhenGotAnonymousToken();
            return errorCode;
        }
        else {
            returnString = "";
        }
        return returnString;
    }

    /*
    // modified for version 4.0.0.
    // 1. check if there is connection.
    // 2. check the token, if not available get the anonymous token.
    // 3. ping
    public PingEchoResult CheckInternetConnection() {
        // replaced by the following codes
        // version 4.0.0.
        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH){
            myPingEcho.setToken(app.getToken());
            pingEchoResult = myPingEcho.Call();
        }
        else if (app.getTokenStatus() == ReUnite.TOKEN_UNKNOWN){
            getAnonymousToken();
            app.recordTimeWhenGotAnonymousToken();
            myPingEcho.setToken(app.getTokenAnonymous());
            pingEchoResult = myPingEcho.Call();
        }
        else if (app.getTokenStatus() == ReUnite.TOKEN_ANONYMOUS) {
            if (app.isAnonymousTokenExpired()){
                getAnonymousToken();
                app.recordTimeWhenGotAnonymousToken();
            }
            myPingEcho.setToken(app.getTokenAnonymous());
            pingEchoResult = myPingEcho.Call();
        }
        else {
            pingEchoResult.toDefault();
            pingEchoResult.setErrorCode("-1");
            pingEchoResult.setErrorMessage("No valid token.");
        }
        return pingEchoResult;
    }
    */

    // modified for version 4.0.0.
    // 1. check if there is connection.
    // 2. check the token, if not available get the anonymous token.
    // 3. ping
    public RestPingResult CheckInternetConnection() {
        RestPingResult result = new RestPingResult(this);
        // replaced by the following codes
        // version 4.0.0.
        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH){
            myPingEcho.setToken(app.getToken());
//			pingEchoResult = myPingEcho.Call();
            result = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }
        else if (app.getTokenStatus() == ReUnite.TOKEN_UNKNOWN){
            getAnonymousToken();
            app.recordTimeWhenGotAnonymousToken();
            myPingEcho.setToken(app.getTokenAnonymous());
//			pingEchoResult = myPingEcho.Call();
            result = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }
        else if (app.getTokenStatus() == ReUnite.TOKEN_ANONYMOUS) {
            if (app.isAnonymousTokenExpired()){
                getAnonymousToken();
                app.recordTimeWhenGotAnonymousToken();
            }
            myPingEcho.setToken(app.getTokenAnonymous());
//			pingEchoResult = myPingEcho.Call();
            result = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }
        else {
            result.toDefault();
            result.setErrorCode("-1");
            result.setErrorMessage(getResources().getString(R.string.no_valid_token));
        }
        return result;
    }


    private class CheckInternetConnectionAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        //Before running code in separate thread
        @Override
        protected void onPreExecute()
        {
            // added in version 4.0.0
            if (app.getTokenStatus() == ReUnite.TOKEN_UNKNOWN) {
                getAnonymousToken();
                app.recordTimeWhenGotAnonymousToken();
            }
            restPingResult.toDefault();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params)
        {
            //Get the current thread's token
            synchronized (this)
            {
                restPingResult = CheckInternetConnection();
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
            if (restPingResult.getErrorCode().equalsIgnoreCase("-1")){
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientListExActivity.this);
                builder.setMessage(myPingEcho.getErrorMessage(PatientListExActivity.this, MyPingEcho.NETWORK_ERR))
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.latency))
                        .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    /**
     * rest call search
     */
    public JSONObject buildSearchJson(Context c) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "search");

            if (app.getTokenStatus() == ReUnite.TOKEN_AUTH){
                json.put("token", app.getToken());
            }
            else {
                json.put("token", app.getTokenAnonymous());
            }

            json.put("short", currentEvent.getShortName());
            json.put("query", currentEvent.getSearchTerm());
            json.put("photo", viewSettings.getEncodedImage());

            // animal
            if (currentEvent.isFilterSelSearchPerson() == true){
                json.put("personAnimal", Filters.SEARCH_PERSON);
            }
            else if (currentEvent.isFilterSelSearchAnimal() == true){
                json.put("personAnimal", Filters.SEARCH_ANIMATION);
            }
            else {
                json.put("personAnimal", Filters.SEARCH_BOTH);
            }

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
            json.put("perPage", 1000);
            json.put("sortBy", "");
        }
        catch (Exception ex){
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }
/*
    public JSONObject buildSearchJson(final Filters filters, final ViewSettings viewSettings, final String eventShort) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "search");

            String tor;
            if (getTokenStatus() == ReUnite.TOKEN_AUTH) {
                json.put("token", getToken());
            }
            else {
                json.put("token", getTokenAnonymous());
            }

            json.put("short", eventShort);
            json.put("query", this.query.toString());
            json.put("photo", "");
            json.put("sexMale", filters.getMale());
            json.put("sexFemale", filters.getFemale());
            json.put("sexOther", filters.getComplex());
            json.put("sexUnknown", filters.getGenderUnknown());
            json.put("ageChild", filters.getChild());
            json.put("ageAdult", filters.getAdult());
            json.put("ageUnknown", filters.getAgeUnknown());
            json.put("statusMissing", filters.getMissing());
            json.put("statusAlive", filters.getAliveAndWell());
            json.put("statusInjured", filters.getInjured());
            json.put("statusDeceased", filters.getDeceased());
            json.put("statusUnknown", filters.getStatusUnknown());
            json.put("statusFound", filters.getFound());

            if (viewSettings.getPhotoSel() == ViewSettings.PHOTO_ONLY){
                json.put("hasImage", true);
            }
            else {
                json.put("hasImage", false);
            }

            json.put("since", "1970-01-01T01:23:45Z");
            json.put("pageStart", this.curPageStart);
            json.put("perPage", viewSettings.getPageSize());
            json.put("sortBy", this.searchSortBy);
        }
        catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }
    */

    // HTTP POST request
    public String searchRestCall(Context c) throws Exception {
        String returnStr = "";

        WebServer ws = new WebServer(app.getWebServer());
        String url = ws.getRestEndpoint();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildSearchJson(c);
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

    private RestSearchResult extractSearchResult(Context c, JSONObject j) throws JSONException {
        RestSearchResult rest = new RestSearchResult(this);
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
            return rest;
        }

        try {
            results = j.get("results").toString();
            if (!results.isEmpty()){
                rest.setPatientList(extractPersonJSON(results));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            rest.setErrorCode("-1");
            rest.setErrorMessage(e.getMessage().toString());
            return rest;
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
            return rest;
        }
        return rest;
    }

    public List<Patient> extractPersonJSON(String results) throws JSONException {
        List<Patient> pList = new ArrayList<Patient>();

        JSONArray jsonArr = new JSONArray(results);
        int len = jsonArr.length();
        for (int i = 0; i < jsonArr.length(); i++){
            Patient p = new Patient(app.getWebServer());
            JSONObject o = jsonArr.getJSONObject(i);

            // what we get
            /*
            p.setPatientUuid(jo.getString("uuid"));
            String name = jo.getString("name");
            String stat = jo.getString("stat");
            String sex = jo.getString("sex");
            String age = jo.getString("age");
            String lki = jo.getString("lki");
            String latitude = jo.getString("latitude");
            String longitude = jo.getString("longitude");
            String editable = jo.getString("editable");
            String following = jo.getString("following");
            String animal = jo.getString("animal");
            String updated = jo.getString("updated");
            String created = jo.getString("created");
            String expires = jo.getString("expires");
            String image_url = jo.getString("image_url");
            String thumb_url = jo.getString("thumb_url");
            String comments = jo.getString("comments"); */

            // what we used to have
            p.setPatientUuid(o.getString("uuid"));
            String fullN = o.getString("name");
            if (fullN.equalsIgnoreCase("null") == true){
                fullN = "unk";
            }
            else if (fullN.equalsIgnoreCase("-1")){
                fullN = "unk";
            }
            p.setFullName(fullN);
            p.setOptStatus(o.getString("stat"));

            /**
             * animal. version 7.3.6
             */
            String animal = o.getString("animal");
            if (animal.equalsIgnoreCase("null") == true){
                p.setPa(Patient.PERSON);
            }
            else {
                p.setPa(Patient.ANIMAL);
                JSONObject oAnimal = new JSONObject(animal);
                String buddy = oAnimal.getString("buddy");
                if (buddy.equalsIgnoreCase("null")){
                    p.setBuddy("");
                }
                else if (buddy.isEmpty() == true) {
                    p.setBuddy("");
                }
                else if (buddy.equalsIgnoreCase("unk") == true) {
                    p.setBuddy("");
                }
                else {
                    p.setBuddy(buddy);
                }
            }

            p.setGender(o.getString("sex"));
            p.setYearsOld(o.getString("age"));

            p.setLastSeen(o.getString("lki")); // is last_seen changed to lki?
            // String lki = o.getString("lki");

//            String latitude = o.getString("latitude");// not used temporary.
//            String longitude = o.getString("longitude");// not used temporary.
//            String editable = o.getString("editable"); // not used temporary.
//            String following = o.getString("following");// not used temporary.
//            String animal = o.getString("animal"); // not used temporary.
            p.setStatusSahanaUpdated(o.getString("updated"));
//            String updated = jo.getString("updated");
//            String created = jo.getString("created");
//            String expires = jo.getString("expires");
//            String image_url = jo.getString("image_url");
            p.setImageUrl(o.getString("thumb_url"));
//            String thumb_url = jo.getString("thumb_url");
            String com = o.getString("comments");
            if (com.equalsIgnoreCase("null") == true){
                com = "";
            }
            p.setComments(com);
//            String comments = jo.getString("comments");
//				p.setImageUrlFull(o.getString("imageUrlFull"));
//            p.setImageWidth(o.getString("imageWidth"));
//            p.setImageHeight(o.getString("imageHeight"));

//            p.setId(o.getString("id"));
//            p.setStatusTriage(o.getString("statusTriage"));
//           p.setPeds(o.getString("peds"));
//            p.setOrgName(o.getString("orgName"));

//            p.setHospitalIcon(o.getString("hospitalIcon"));
//            p.setMassCasualtyId(o.getString("mass_casualty_id"));

            // Generate photo
            p.setStatusPhotoDownload(false);
            pList.add(p);
        }
        return pList;
    }

    //To use the AsyncTask, it must be subclassed
    private class SearchRestCallAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        RestSearchResult rest;
        String returnStr = "";

        private int func;
        private Context c;

        SearchRestCallAsyncTask(Context c){
            this.func = func;
            this.c = c;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(PatientListExActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.retrieving_information_on_selected_event_please_wait));
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
                // search for results
                currentEvent.setViewSettings(viewSettings);
                try {
                    returnStr = searchRestCall(c);
                    JSONObject searchCountResultJson = new JSONObject(returnStr);
                    rest = extractSearchResult(c, searchCountResultJson);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    returnStr = e.getMessage();
                    rest.setErrorCode("-1");
                    rest.setErrorMessage(e.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                    rest.setErrorCode("-1");
                    rest.setErrorMessage(e.getMessage());
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
            HideInfoTextView();
            currentPage = 0;
            currentPageStart = 0;
            totalRecords = Integer.parseInt(rest.getCount().toString());
            if (viewSettings.getEncodedImage().isEmpty()) {
                ShowNextPage();
            }
            else {
                ShowAllInOnePage(rest);
            }

            // added in version 7.2.6
            // clear messages for the user.
            if (totalRecords == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientListExActivity.this);
                builder.setIcon(android.R.drawable.ic_dialog_alert)
                        .setMessage(getResources().getString(R.string.no_record_is_found))
                        .setCancelable(false)
                        .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            // track the report
            app.tracker().send(new HitBuilders.EventBuilder()
                    .setCategory(currentEvent.getShortName())
                    .setAction(app.TRACK_SEARCH)
                    .setLabel(app.SUCCEED)
                    .build());

        }
    }

    public void checkConnectionAndRunSearch(Context c){
        if (WebServer.isOnline(c)) {
            app.setOffline(false);
            new SearchRestCallAsyncTask(c).execute();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(PatientListExActivity.this);
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
    }
}

