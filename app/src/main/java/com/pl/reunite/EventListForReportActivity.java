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

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pl.reunite.Result.PingEchoResult;
import com.pl.reunite.Result.RequestAnonTokenResult;
import com.pl.reunite.Result.RestEventsResult;
import com.pl.reunite.Result.RestPingResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

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

public class EventListForReportActivity extends ActionBarActivity implements View.OnClickListener {
    private static final String METHOD_NAME = "getEventData"; // changed from getEventList. version 7.2.8

    private static int timesOfCalled;

    ReUnite app;
    String webServer = "";
    String nameSpace = "";
    String url = "";
    String soapAction = "";

    String currentSelectedEvent = "";
     
    ListView lvEx;
    ListView lvPatientSimple;
    String returnString;

    String [] nameArray;
	ArrayAdapter<String> adapter; 

	List<Event> eventList = new ArrayList<Event>();
	Event event;
	List<Patient> patientList = new ArrayList<Patient>();
	List<Patient> smaillPatientList = new ArrayList<Patient>();

   //A ProgressDialog object  
    private ProgressDialog progressDialog;

    MyPingEcho myPingEcho;
//	PingEchoResult pingEchoResult;
	RestPingResult restPingResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_list);
		lvEx = (ListView) findViewById(R.id.lvEventsEx);

		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        /**
         * change to running time
         */
        // switch server step 4.
        webServer = app.getWebServer();
		if (webServer == WebServer.PL_NAME){
			nameSpace = WebServer.PL_NAMESPACE;
			url = WebServer.PL_URL;
		}
		else {
			nameSpace = WebServer.PL_NAMESPACE_STAGE;
			url = WebServer.PL_URL_STAGE;
		}
		soapAction = nameSpace + WebServer.END_POINT + METHOD_NAME;

		myPingEcho = new MyPingEcho(app.getWebServer()); // changed in version 7.1.3
		restPingResult = new RestPingResult(this);

		if (timesOfCalled == 0){
			if (isOnline()) {
				app.setOffline(false);
				new SetRequestForEventAndPatientListAsyncTask().execute();
			}
			else {
				app.setOffline(true);
			}
			EventDataSource eventDataSource = new EventDataSource(this, nameSpace);
			eventDataSource.open();
			eventList = eventDataSource.getAllEvent();
			eventDataSource.close();
			if (eventList.isEmpty() == false){
				DisplayEventList();
			}
			timesOfCalled++;
		}
        else {	
			EventDataSource eventDataSource = new EventDataSource(this, nameSpace);
			eventDataSource.open();
			eventList = eventDataSource.getAllEvent();
			eventDataSource.close();
			if (eventList.isEmpty() == false){
				DisplayEventList();
			}
			timesOfCalled++;
        }
	}

	private void Initialize() {

		event = new Event(webServer);
		if (eventList.isEmpty() == false){
			DisplayEventList();
		}				
	}
	
	private void DisplayEventList() {
		if (eventList.isEmpty() == true){
			return;
		}
        ArrayList<ItemEventView> image_details = GetEventResults();	
//        lvEx = (ListView) findViewById(R.id.lvEventsEx);
        lvEx.setAdapter(new ItemEventListBaseAdapter(EventListForReportActivity.this, image_details));
		
        lvEx.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvEx.getItemAtPosition(position);
        		ItemEventView obj_itemDetails = (ItemEventView)o;

        		Event e = new Event(webServer);
        		e.setName(obj_itemDetails.getName().toString());
				e.setShortName(obj_itemDetails.getShortName().toString());
				e.setDate(obj_itemDetails.getDate().toString());
				e.setStreet(obj_itemDetails.getStreet().toString());
        		currentSelectedEvent = e.getName();
				app.setLastEvent(e);

        		// Add codes to return the selected event.
//       			AreYouSureToExit();
        		String message = getResources().getString(R.string.you_have_selected) + ": \n" + e.getName();
        		Toast.makeText(EventListForReportActivity.this, message, Toast.LENGTH_LONG).show();
        		
        		// Return
    			Intent backData = new Intent();
    			backData.putExtra("eventname", e.getName());
    			backData.putExtra("eventshortname", e.getShortName());
				backData.putExtra("eventDate", e.getDate());
				backData.putExtra("eventStreet", e.getStreet());
    			setResult(EventListForReportActivity.this.RESULT_OK, backData);

				if (isOnline()) {
					new FinishAsyncTask(EventListForReportActivity.this).execute();
				}
        	}

			private void AreYouSureToExit() {
				AlertDialog.Builder builder = new AlertDialog.Builder(EventListForReportActivity.this);
				builder.setMessage(currentSelectedEvent)
					   .setTitle(getResources().getString(R.string.you_have_selected))
				       .setCancelable(false)
				       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
			        			Intent backData = new Intent();
			        			backData.putExtra("eventname", event.getName());
			        			backData.putExtra("eventshortname", event.getShortName());
  							    backData.putExtra("eventDate", event.getDate());
							    backData.putExtra("eventStreet", event.getStreet());
			        			setResult(EventListForReportActivity.this.RESULT_CANCELED, backData);
			        			EventListForReportActivity.this.finish();
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

			private Event searchEventFromList(String currentSelectedEvent) {
				for (int i = 0; i < eventList.size(); i++){
					Event e = eventList.get(i);
					if (currentSelectedEvent.equalsIgnoreCase(e.getName()) == true){
						return e;
					}
				}
				return null;
			}
		});
		
	}

	private ArrayList<ItemEventView> GetEventResults() {
	   	ArrayList<ItemEventView> results = new ArrayList<ItemEventView>();
    	
	   	ItemEventView item_details;    	
    	for (int i = 0; i < eventList.size(); i++)
    	{
    		Event e = eventList.get(i);
    		item_details = new ItemEventView();
    		item_details.setName(e.getName());
    		item_details.setShortName(e.getShortName());
    		item_details.setDate(e.getDate());
    		item_details.setStreet(e.getStreet().toString());
    		item_details.setType(e.getType() + " event");
			item_details.setStatus(e.getClosed());
    		results.add(item_details);
    	}
    	return results;
	}

	private void SetRequestForEventAndPatientList() {
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
                    // start to change SearchEvents()
                    /*
                    EventListResult eventListResult = new EventListResult();
                    WebServer ws = new WebServer();
                    if (app.getTokenStatus() == ReUnite.TOKEN_AUTH){
                        eventListResult = ws.getEventListV34(app.getToken());
                    }
                    else if (app.getTokenStatus() == ReUnite.TOKEN_ANONYMOUS) {
                        eventListResult = ws.getEventListV34(app.getTokenAnonymous());
                    }
                    else {
                        eventListResult.setErrorCode(Result.MY_ERROR_CODE);
                        eventListResult.setErrorMessage("Error: token is not defined.");
                    }
                    if (eventListResult.getErrorCode().equalsIgnoreCase("0")){
                        eventListResult.JSONParserForEvent(ws.getName().toString());
                        eventList = eventListResult.getEventList();
                    }
*/
// 	    			SearchEvents();

					// start
					WebServer ws = new WebServer(app.getWebServer()); // modified in version 7.1.3
					ws.setTokenStatus(app.getTokenStatus());
					ws.setTokenAnonymous(app.getTokenAnonymous());
					ws.setToken(app.getToken());
					RestEventsResult restEventsResult = new RestEventsResult(EventListForReportActivity.this);
					try {
						restEventsResult = ws.restSearchEvents(EventListForReportActivity.this);
						eventList.clear();
						if (restEventsResult.getErrorCode().equalsIgnoreCase("-1")){
							return;
						}
						if (restEventsResult.getEventList2().size() > 0){
							restEventsResult.toEventList();
							eventList = restEventsResult.getEventList();
						}

					} catch (Exception e) {
						e.printStackTrace();
						restEventsResult.setErrorCode("-1");
						restEventsResult.setErrorMessage(e.getMessage());
                        return;
					}

					// end
	    			// Search patient and get patient list
	    			if (eventList.isEmpty() == true){
	    				return;
	    			}
	    			// Order the event by its date
	    			OrderEventsByDate();
	    			
	    			// Search number of records
//	    			SearchCountForEvents();
	    			
	    			currentSelectedEvent = eventList.get(0).getName(); 
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
//					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
				returnString = getResources().getString(R.string.time_out);
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread
	}
	private void SearchEvents() {

	   	SoapObject request = new SoapObject(nameSpace, METHOD_NAME);

        // add this for version 34
        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH) {
            request.addProperty("token", app.getToken());
        }
        else {
			if (app.getTokenAnonymous().isEmpty()){
				getAnonymousToken();
			}
            request.addProperty("token", app.getTokenAnonymous());
        }
        // end

    	SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
    	envelope.dotNet = false;
    	envelope.setOutputSoapObject(request);
    	
//    	AndroidHttpTransport aht = new AndroidHttpTransport(URL);
    	HttpTransportSE aht = new HttpTransportSE(url); 
        aht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
    	try
    	{
			aht.debug = true;
    		aht.call(soapAction, envelope);
			Log.e("Soap request ", aht.requestDump);
			Log.e("Soap response ", aht.responseDump);
			result = (SoapPrimitive)envelope.getResponse();  
    	} catch (Exception e) {
    		result = null;
    	}
    	
       	SoapObject resultRequestSOAP  = (SoapObject)envelope.bodyIn;
		String evenListString = resultRequestSOAP.getPropertyAsString("events");
//		String evenListString = resultRequestSOAP.getPropertyAsString("eventList");

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

	public void onClick(View v) {

		switch(v.getId()){
		case R.id.lvEventsEx:
			finish();
//			SelectEventId();
//			Search();
			break;
		case R.id.tv:
		    Toast.makeText(EventListForReportActivity.this, "Not implemented.", Toast.LENGTH_SHORT).show();							
		default:
		    Toast.makeText(EventListForReportActivity.this, "Back key click???.", Toast.LENGTH_SHORT).show();							
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			/*
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("You have not selected an event.")
			       .setCancelable(false)
			       .setTitle("Are you sure?")
			       .setNegativeButton("NO", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			                Log.d(this.getClass().getName(), "back button pressed");
			           }
			       })
			       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			    	   public void onClick(DialogInterface dialog, int id) {
		        			Intent backData = new Intent();
		        			backData.putExtra("eventname", "");
		        			backData.putExtra("eventshortname", "");
		        			setResult(EventListForReportActivity.this.RESULT_CANCELED, backData);
		        			EventListForReportActivity.this.finish();
			    		   dialog.dismiss();
			    		   Log.d(this.getClass().getName(), "back button pressed");
			    	   }
			       });
			AlertDialog alert = builder.create();		
			alert.show();		
		    return false;
		    */
			Toast.makeText(EventListForReportActivity.this, getResources().getString(R.string.canceled), Toast.LENGTH_SHORT).show();
			Intent backData = new Intent();
			backData.putExtra("eventname", "");
			backData.putExtra("eventshortname", "");
			setResult(EventListForReportActivity.this.RESULT_CANCELED, backData);
			EventListForReportActivity.this.finish();
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
        new CheckInternetConnectionAsyncTask().execute();
    }

	private void Search() {

		
	}

	//To use the AsyncTask, it must be subclassed
    private class SetRequestForEventAndPatientListAsyncTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        	// spin on action bar
            getActionBarHelper().setRefreshActionItemState(true);
            
            //Create a new progress dialog  
            progressDialog = new ProgressDialog(EventListForReportActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage(getResources().getString(R.string.retrieving_events_please_wait));
            progressDialog.setCancelable(false);  
            progressDialog.setIndeterminate(false);  
            progressDialog.show();  

			while (!eventList.isEmpty()) {
				eventList.remove(0);
			}
        }
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {  
            //Get the current thread's token  
			synchronized (this)  
			{  
				SetRequestForEventAndPatientList();
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
    		SaveEventsToDatabase();

    		//close the progress dialog  
            progressDialog.dismiss();  
            
            // Set back the spin on action bar
            getActionBarHelper().setRefreshActionItemState(false);

            //initialize the View  
            Initialize();
      }

		private void SaveEventsToDatabase() {
			if (eventList.isEmpty() == true){
				return;
			}
			for (int i = 0; i < eventList.size(); i++){
				Event e = eventList.get(i);
				for (int j = i + 1; j < eventList.size(); j++){
					if (eventList.get(j).getName().equalsIgnoreCase(e.getName()) == true){
						eventList.remove(j);
					}
				}
			}
    		EventDataSource eventDataSource = new EventDataSource(EventListForReportActivity.this, nameSpace);
        	eventDataSource.open();
        	eventDataSource.deleteAllEvent();
        	for (int i = 0; i < eventList.size(); i++){
        		Event e = eventList.get(i);
       			eventDataSource.createEvent(e);
        	}
            eventDataSource.close();
		}  
    }  
    
    // Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_list_menu, menu);
        return true;
	}	
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_refresh:
            new SetRequestForEventAndPatientListAsyncTask().execute();         
            /*            getWindow().getDecorView().postDelayed(
                    new Runnable() {
                        public void run() {
                            getActionBarHelper().setRefreshActionItemState(false);
                        }
                    }, 2000);
                    */
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
			new EmailUs(EventListForReportActivity.this).start();
        	break;
        }
        return true;
	}

	private void GoBackToMainPage() {

		Intent i = new Intent(EventListForReportActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}   

	private void Latency2() {
		Intent i = new Intent(EventListForReportActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}
	
	private void Tutorials() {
		Intent i = new Intent(EventListForReportActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}


	//To use the AsyncTask, it must be subclassed
    private class FinishAsyncTask extends AsyncTask<Void, Integer, Void>  
    {
		PingEchoResult pingEchoResult;
		RestPingResult restPingResult;
		Context c;

		FinishAsyncTask(Context c){
			this.c = c;

		}
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {
			restPingResult = new RestPingResult(c);
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {  
            //Get the current thread's token  
			synchronized (this)  
			{  
				int sleepSeconds = 500;
				try {
					Thread.sleep(sleepSeconds);
				} catch (InterruptedException e) {

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
			EventListForReportActivity.this.finish();    			
        }
    }

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

    public String getAnonymousToken(){
        String errorCode = "";
        // get the anonymous token
        WebServer ws = new WebServer(app.getWebServer()); // modified in version 7.1.3
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
            returnString = "";
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
			// changes made in version 7.1.6.
			if (!restPingResult.getErrorCode().contentEquals("0")){
//				if (returnString.equalsIgnoreCase(MyPingEcho.TIME_OUT) == true || returnString.isEmpty() == true){
                AlertDialog.Builder builder = new AlertDialog.Builder(EventListForReportActivity.this);
                builder.setMessage(restPingResult.getErrorMessage())
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

	public boolean isOnline(){
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		else {
			return false;
		}
	}
}
