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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pl.reunite.Result.FollowRecordResult;
import com.pl.reunite.Result.PingEchoResult;
import com.pl.reunite.Result.RequestAnonTokenResult;
import com.pl.reunite.Result.RestFollowRecordResult;
import com.pl.reunite.Result.RestFollowResult;
import com.pl.reunite.Result.RestGcmResult;
import com.pl.reunite.Result.RestPingResult;
import com.pl.reunite.Result.RestPrefResult;
import com.pl.reunite.Result.RestSearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

/**
 * This is main screen - home screen.
 * Every thing starts from here.
 */

public class ReUniteActivity extends Activity implements View.OnClickListener, SensorEventListener {
    private static final String TAG = "ReUniteActivity";

    private static final int SEL_WEB_SERVER_ID = Menu.FIRST + 1;

    final CharSequence[] WebServerArray = {WebServer.PL_NAME, WebServer.PL_NAME_STAGE};
    private int nCurSelWS;
    private int nPreSelWS;

    private static final String GUEST = "Guest";
    private static final int LOGIN_REQUEST = 1;
    private static final int SELECT_WEBSERVER = 2;
    private static final int ACCEPTANCE_REQUEST = 3;
    private static final int BURDEN_STATEMENT_SPLASH = 4;
    private static final int PICK_EVENT_REQUEST = 5;
    private static final int FIND_PEOPLE = 6;
    private static final int RETURN_FROM_REPORTACTIVITY = 7;
    private static final int RETURN_FROM_ANIMAL_REPORTACTIVITY = 8;

    private static final String GOOGLE_PLUS_URL = "https://plus.google.com/117085093991742083182/posts";
    private static final String FACEBOOK_URL = "https://www.facebook.com/NlmPeopleLocator/"; // updated in version 7.3.5.00
    private static final String TWITTER_URL = "https://twitter.com/NLM_PL";

    Tracker mTracker;

    /**
     * latency indicator
     */
    private static final int UNKNOWN = 0;
    private static final int DISCONNECTED = 1;
    private static final int POOR = 2;
    private static final int GOOD = 3;
    private static final int EXCELLENT = 4;

    private static final int CONNECTED = 2;

    private static final int[] ServerRatingImage = {
            R.drawable.status_unknown,
            R.drawable.status_disconnected,
            R.drawable.status_low,
            R.drawable.status_mid,
            R.drawable.status_high
    };
    private static final int[] ServerRatingColor = {
            Color.BLACK,
            Color.YELLOW,
            Color.BLACK
    };

    ReUnite app;
    String webServer = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";

    String username = "";
    String password = "";
    String token = "";
    String tokenAnonymous = "";
    int tokenStatus = ReUnite.TOKEN_UNKNOWN;

    Credential credential;

    boolean quickStart = false;
    private int nCurSelQuickStart = 1;
    final CharSequence[] items = {"1 second", "5 seconds"};

    static boolean wasBurdenCalled = false;

    static boolean fcmAccepted = false;
   /**
     * Google Analytics
     */
    ReUniteBroadcastService reUniteBroadcastService;

    /**
     * GCM
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    //    private ProgressBar mRegistrationProgressBar;
//    private TextView mInformationTextView;
    public String gcmMessage = "";
    public boolean gcmSentToken = false;

    // to receive the notifications
//    PlGcmListenerService gcmListener;
    // GCM end

    /**
     * App Indexing start
     */
    private GoogleApiClient client;
    //	static final Uri APP_URI = Uri.parse("android-app://com.example.android.recipes/http/recipe-app.com/recipes");
    static final Uri APP_URI = Uri.parse("ReUnite://play.google.com/store/apps/details?id=com.pl.reunite");

    //	static final Uri WEB_URI = Uri.parse("http://recipe-app.com/recipes/");
    static final Uri WEB_URI = Uri.parse("https://www.your_domain.com/");
    // App Indexing end

    // locale
    Locale curLocale;

    // 	Button loginButton;
    Button findPeopleButton;
    Button reportPersonButton;
    Button organizeButton;
    TextView displayText;
    TextView welcomeTextView;
    //    Button buLoginLogout;
    Button buttonLoginLogout;
    TextView whoTextView;
//    ImageView imageViewLogo;
    TextView youAreDeveloperNow;
//	Button aboutButton;

    TextView tvLatestEventName;
    TextView tvLatestEventStatus;
    TextView tvLatestEventDetails;
    TextView tvWebServer;

    Button buttonChange;

    //	RatingBar ratingBar;
//    ImageView imageViewServerRating;
    TextView textViewServerRating;
    Button buttonCheck;

    int ratings = 0;
    TextView tvServerLatency;

    String strDisplay;
    MyPingEcho myPingEcho;
    private PingEchoResult pingEchoResult;

    RadioButton radioButtonPerson;
    RadioButton radioButtonAnimal;

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    private static boolean asked = false;

    // shake
    private boolean mInitialized;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float lastX;
    private float lastY;
    private float lastZ;
    private final float NOISE = (float) 5.0;
    private final float SHAKE_THRESHOLD = (float) 800.00;
    private int shakeCount = 0;
    private int shakeLeftCount = 0;
    private int shakeRightCount = 0;
    private long lastUpdateTime = 0;
    private boolean developer;

    private int curSelFollowRecord = 0;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.main);

        // Initial with globe variables.
        app = ((ReUnite) this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        // Get the user auth
        credential = new Credential();
        // Add very here
        username = credential.getUsernamePreferences().toString();
        password = credential.getPasswordPreferences().toString();
        webServer = credential.getWebServerPreferences().toString();
        token = credential.getTokenPreferences().toString();
        tokenAnonymous = credential.getTokenAnonymous().toString();
        tokenStatus = credential.getTokenStatus();
        webServer = credential.getWebServerPreferences().toString();
        quickStart = credential.getQuickStartPreferences();
        developer = credential.getDeveloperPreferences();

        app.setUsername(username);
        app.setPassword(password);
        app.setToken(token);
        app.setTokenAnonymous(tokenAnonymous);
        app.setTokenStatus(tokenStatus);
        app.setWebServer(webServer);
        if (app.getNcmecClaim() == ReUnite.NEMEC_CLAIM_UNDECIDED) {
            app.setNcmecClaim(ReUnite.NCMEC_CLAIM_SHOW);
            credential.setNcmecClaim(ReUnite.NCMEC_CLAIM_SHOW);
        }
        app.setQuickStart(quickStart);
        app.setDeveloper(developer);

        // get location
//        getLocation();

        if (WebServer.isOnline(this) == true) {
            app.setOffline(false);
        } else {
            app.setOffline(true);
        }

        Initialize();

        if (app.isOffLine() == true) {
            return;
        }

        mTracker = app.tracker();

        // Google Analytics start
        reUniteBroadcastService = new ReUniteBroadcastService((ReUnite) app);
        // Google Analytics end

//        gcmListener = new PlGcmListenerService();

        // GCM start
//        gcmInitialize();
//        startGCM();
        // GCM end

        // App Indexing start
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        // App Indexing end

        /**
         * ping and give the location.
         */
        String tor;
        if (app.getAuthStatus() == true) {
            tor = app.getToken();
        } else {
            tor = app.getTokenAnonymous();
        }
        myPingEcho = new MyPingEcho(this, tor, app.getWebServer()); // add argument. changed in version 7.1.3

        // locale
        // it is only for user and admin, not for anonymous user
        if (app.getAuthStatus() == true) {
            curLocale = getResources().getConfiguration().locale;
            if (app.getLocale() == null || !app.getLocale().equals(curLocale)) {
                app.setLocale(curLocale);
                app.toLanguageCode();
                new RestPrefAsyncTask(this).execute();
            }
        }
    }

    // Google Map View
    /*
    private void getLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationManagerHelper lmh = new LocationManagerHelper();
        String mlocProvider;
        Criteria hdCrit = new Criteria();

        hdCrit.setAccuracy(Criteria.ACCURACY_COARSE);

        mlocProvider = locationManager.getBestProvider(hdCrit, true);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1000, lmh);
        Location currentLocation;

        try {
            currentLocation = new Location(locationManager.getLastKnownLocation(mlocProvider));
            locationManager.removeUpdates(lmh);
            app.setLatitude(currentLocation.getLatitude());
            app.setLongitude(currentLocation.getLongitude());
        } catch (Exception e) {
            app.setLatitude(0);
            app.setLongitude(0);
            Toast.makeText(this, "GPS cannot get the current location. " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    */

    /**
     * GCM
     */
    /*
    private void gcmInitialize() {
//        mRegistrationProgressBar = (ProgressBar) findViewById(R.id.registrationProgressBar);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//				mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                gcmSentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (gcmSentToken) {
                    gcmMessage = getString(R.string.gcm_send_message);
//					mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                    gcmMessage = getString(R.string.token_error_message);
//					mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
//        mInformationTextView = (TextView) findViewById(R.id.informationTextView);
    }
    */

    /*
    private void startGCM() {
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * GCM
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    /*
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    */

    /*
    public void startGcmListener() {
        // Start IntentService to register this application with GCM.
        Intent intent = new Intent(this, PlGcmListenerService.class);
        startService(intent);
    }
    */

    private void Acceptance() {
        Intent i = new Intent("com.pl.reunite.ACCEPTACTIVITY");
        startActivityForResult(i, ACCEPTANCE_REQUEST);
    }

    private void Initialize() {
        // shake
        mInitialized = false;
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        shakeCount = 0;
        shakeLeftCount = 0;
        shakeRightCount = 0;

        whoTextView = (TextView) findViewById(R.id.tvWho);
        buttonLoginLogout = (Button) findViewById(R.id.buttonLoginLogout);

//        imageViewLogo = (ImageView) findViewById(R.id.imageViewLogo);

        youAreDeveloperNow = (TextView) findViewById(R.id.textViewYouAreDeveloperNow);
        if (app.getDeveloper() == true) {
            youAreDeveloperNow.setVisibility(View.VISIBLE);
        } else {
            youAreDeveloperNow.setVisibility(View.INVISIBLE);
        }

        findPeopleButton = (Button) findViewById(R.id.buFindPeople);
        reportPersonButton = (Button) findViewById(R.id.buReportPerson);
        organizeButton = (Button) findViewById(R.id.buOrganize);
        displayText = (TextView) findViewById(R.id.tv);

        tvWebServer = (TextView) findViewById(R.id.textViewWebServer);
        tvWebServer.setText(webServer);

        // add color. modified in version 7.1.3
        if (webServer.equalsIgnoreCase(WebServer.PL_NAME)) {
            tvWebServer.setTextColor(getResources().getColor(R.color.black3));
        } else {
            tvWebServer.setTextColor(getResources().getColor(R.color.red0));
        }

//        imageViewServerRating = (ImageView) findViewById(R.id.imageViewServerRating);
        textViewServerRating = (TextView) findViewById(R.id.textViewServerRating);
        tvServerLatency = (TextView) findViewById(R.id.textViewServerLatency);

//        imageViewServerRating.setImageResource(ServerRatingImage[UNKNOWN]);
        textViewServerRating.setText(getServerRatingString(UNKNOWN));
        textViewServerRating.setTextColor(ServerRatingColor[UNKNOWN]);

        buttonCheck = (Button) findViewById(R.id.buttonCheck);

//        imageViewServerRating.setOnClickListener(this); // get more space for button. added in 7.1.6
        textViewServerRating.setOnClickListener(this); // get more space for button. added in 7.1.6
        buttonCheck.setOnClickListener(this);

        tvLatestEventName = (TextView) findViewById(R.id.textViewLatestEventName);
        tvLatestEventStatus = (TextView) findViewById(R.id.textViewLatestEventStatus);
        tvLatestEventDetails = (TextView) findViewById(R.id.textViewLatestEventDetails);

        buttonChange = (Button) findViewById(R.id.buttonChange);

        tvLatestEventName.setOnClickListener(this); // get more space for button. added in 7.1.6
        buttonChange.setOnClickListener(this);

        Event e = app.getLastEvent();
        if (e == null || e.getName().isEmpty()) {
            String eventName = credential.getEventNamePreferences();
            String eventShortName = credential.getEventShortNamePreferences();
            String eventDate = credential.getEventDatePreferences();
            String eventStreet = credential.getEventStreetPreferences();

            if (eventName.isEmpty()) {
                // Get the last event
                EventDataSource eventDataSource = new EventDataSource(this, nameSpace);
                eventDataSource.open();
                List<Event> eventList = new ArrayList<Event>();
                eventList = eventDataSource.getAllEvent();
                e = new Event(webServer);
                if (eventList.isEmpty() == false) {
                    e = eventList.get(0);
                } else {
                    e.toDefault();
                }
                eventDataSource.close();
            } else {
                e = new Event(webServer);
                e.toDefault();
                e.setName(eventName);
                e.setShortName(eventShortName);
                e.setDate(eventDate);
                e.setStreet(eventStreet);
            }
        }

        tvLatestEventName.setText(e.getName());
        tvLatestEventStatus.setText(e.getDate());
        tvLatestEventDetails.setText(e.getStreet());

        app.setLastEvent(e);

        findPeopleButton.setOnClickListener(this);
        reportPersonButton.setOnClickListener(this);
        organizeButton.setOnClickListener(this);

        // Get the user auth
        credential = new Credential();
        // Add veriy here
        username = credential.getUsernamePreferences().toString();
        password = credential.getPasswordPreferences().toString();
        app.setAuthStatus(credential.getAuthStatus());
        app.setGroupName(credential.getGroupNamePreferences().toString());

        if (username.equalsIgnoreCase(GUEST)) {
            whoTextView.setText(getResources().getString(R.string.guest));
            buttonLoginLogout.setText(getResources().getString(R.string.login));
        } else {
            whoTextView.setText(username);
            buttonLoginLogout.setText(getResources().getString(R.string.logout));
        }

        buttonLoginLogout.setOnClickListener(this);

//        registerForContextMenu(imageViewLogo);

        // Burden
        if (wasBurdenCalled == false && app.getAuthStatus() == false) {
            wasBurdenCalled = true;
            showOMB();
        }

        // FCM
        if (fcmAccepted == true) {
            subscribeFcm();
        }
    }

    // ask if accept fcm message
    public void askIfAcceptFcm(){
        /**
         * change to alert dialogue box
         */
        View ombView = View.inflate(this, R.layout.dialog_display_accept_fcm, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
        builder.setView(ombView);
        builder.setCancelable(false);
        builder.setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                subscribeFcm();
                fcmAccepted = true;
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * get the location
     * version 9.3.0
     */
    private void findMyLocation() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                app.setLatitude(location.getLatitude());
                app.setLongitude(location.getLongitude());
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };

        // Register the listener with the Location Manager to receive location updates
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    private void showOMB(){
        /**
         * change to alert dialogue box
         */
        View ombView = View.inflate(this, R.layout.dialog_display_omb, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
        builder.setView(ombView);
        builder.setCancelable(false);
        builder.setNeutralButton(getResources().getString(R.string.i_refuse), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Toast.makeText(ReUniteActivity.this, getResources().getString(R.string.good_bye), Toast.LENGTH_SHORT).show();
                dialog.cancel();
                finish();
                System.exit(0);
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.omb_sub), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

        // tracker
        app.tracker().setScreenName(app.TRACK_OMB);
        app.tracker().send(new HitBuilders.ScreenViewBuilder().build());

        app.tracker().setScreenName(app.TRACK_PRIVACY);
        app.tracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

/*
    private boolean AmIConnected() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;

	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	        if (ni.getTypeName().equalsIgnoreCase("WIFI"))
	            if (ni.isConnected())
	                haveConnectedWifi = true;
	        if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
	            if (ni.isConnected())
	                haveConnectedMobile = true;
	    }
	    return haveConnectedWifi || haveConnectedMobile;
	}
	*/

    @Override
    public void onStop() {
        super.onStop();
        if (app.isOffLine() == true){
            return;
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "ReUnite Page",
                Uri.parse("http://host/path"),
                Uri.parse("android-app://com.pl.reunite/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);

        // GCM end
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.disconnect();

        credential.saveNcmecClaimPreferences(app.getNcmecClaim());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (app.isOffLine() == true){
            return;
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();

        /**
         * Google
         */
        // Set screen name on tracker so that all subsequent hits will use this
        // value.
        mTracker.set("ReUniteActivity", "Home Screen");

        // Get the intent that started this Activity.
        Intent intent = this.getIntent();
        Uri uri = intent.getData();
        // GCM end

        Event e = app.getLastEvent();
        if (e == null) {
            e = new Event(webServer);
            e.toDefault();
            app.setLastEvent(e);
        }
        if (e.getName().equalsIgnoreCase(Event.DEFAULT_EVENT_NAME) == false) {
            tvLatestEventName.setText(e.getName());
            tvLatestEventStatus.setText(e.getDate());
            tvLatestEventDetails.setText(e.getStreet());
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "ReUnite Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://your_domain/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
	protected void onPause() {
        // shake
        mSensorManager.unregisterListener(this);

        // GCM start
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        // GCM end

		super.onPause();
	}

    @Override
	protected void                                                                                                                                      onResume() {
		super.onResume();
        if (app.isOffLine() == true){
            return;
        }

        // shake
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        if (pingEchoResult == null){
            pingEchoResult = new PingEchoResult();
        }
        else {
            pingEchoResult.toDefault();
        }

        // tracker
        app.tracker().setScreenName("Home");
        app.tracker().send(new HitBuilders.ScreenViewBuilder().build());

        // check internet
        new CheckInternetConnectionAsyncTask().execute();

        // GCM start
        if (fcmAccepted == true){
            if (app.getAuthStatus() == true){
                subscribeFcm();
            }
        }
//        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        // GCM end
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long curTime = System.currentTimeMillis();
        if ((curTime - lastUpdateTime) < 100) {
            return;
        }

        long diffTime = (curTime - lastUpdateTime);
        lastUpdateTime = curTime;

        // read the data
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

        if (speed > SHAKE_THRESHOLD) {
            shakeCount++;
            if (shakeCount >= 7){
                shakeCount = 0;
                Log.d("sensor", "shake detected w/ speed: " + speed);
                if (developer == true){
                    developer = false;
                }
                else {
                    developer = true;
                }
                app.setDeveloper(developer);
                credential.saveDeveloperPreferences(developer);
                if (app.getDeveloper() == true){
                    youAreDeveloperNow.setVisibility(View.VISIBLE);
                }
                else {
                    youAreDeveloperNow.setVisibility(View.INVISIBLE);
                }
            }
        }
        lastX = x;
        lastY = y;
        lastZ = z;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // can be safely ignored for this demo
    }

	@Override
	public void onBackPressed() {
		super.onBackPressed();		
	}
	

	public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonLoginLogout:
                if (credential.getAuthStatus() == false){
                    Login();
                }
                else {
                    Logout();
                    return;
                }
                break;
            case R.id.buFindPeople:
//			EventList();
                FindPeople();
                break;
            case R.id.buReportPerson:
                if (credential.getAuthStatus() == true){
                    if (app.getPersonOrAnimal() == Patient.PERSON) {
                        ReportPerson();
                    }
                    else {
                        reportAnimal();
                    }
                }
                else {
                    LoginFirst();
                }
                break;
            case R.id.buOrganize:
                if (credential.getAuthStatus() == true){
                    Organize();
                }
                else {
                    LoginFirst2();
                }
                break;
            case R.id.buttonChange:
            case R.id.textViewLatestEventName: // get more space for button. added in 7.1.6
                selectEvent();
                break;
            case R.id.buttonCheck:
//            case R.id.imageViewServerRating: // get more space for button. added in 7.1.6
            case R.id.textViewServerRating: // get more space for button. added in 7.1.6
                Latency2();
                break;
            default:
                break;
        }
    }

    private void showGCMToken() {
        String msg = "";
        String gcmToken = app.getGcmToken();
        if (gcmToken.isEmpty()){
            msg = "None";
        }
        else {
            msg = gcmToken;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
        builder.setMessage(msg.toString())
                .setTitle("Google Cloud Messing Token")
                .setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showGCMTokenUploadMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
        builder.setMessage(app.getGcmTokenUploadMsg().toString())
                .setTitle("GCM Token Upload Message")
                .setCancelable(false)
                .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void LoginFirst2() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
		builder.setMessage(getResources().getString(R.string.as_a_guest_you_may_have_))
			   .setTitle(getResources().getString(R.string.do_you_want_to_login))
               .setIcon(android.R.drawable.ic_dialog_alert)
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Login();
                   }
               })
                .setNeutralButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Organize();
                    }
                })
		       .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
                   }
               });
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void LoginFirst() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
		builder.setMessage(getResources().getString(R.string.as_a_guest_you_may_report_))
			   .setTitle(getResources().getString(R.string.do_you_want_to_login))
                .setIcon(android.R.drawable.ic_dialog_alert)
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.login), new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       Login();
                   }
               })
                .setNeutralButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ReportPerson();
                    }
                })
		       .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
                       dialog.cancel();
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}

    private void personOrAnimal() {
    }

    private void EventList() {
		Intent i = new Intent("com.pl.reunite.EVENTLIST");
		startActivity(i);																							
	}

	private void Organize() {
		Intent i = new Intent("com.pl.reunite.ORGANIZEACTIVITY");
		startActivity(i);																							
	}

    private void ReportPerson() {
        Intent i = new Intent("com.pl.reunite.REPORTACTIVITY");
        startActivityForResult(i, RETURN_FROM_REPORTACTIVITY);
    }

    private void reportAnimal() {
        Intent i = new Intent("com.pl.reunite.REPORTACTIVITY");
        startActivityForResult(i, RETURN_FROM_REPORTACTIVITY);
    }

	private void Login() {
        if (WebServer.isOnline(this)) {
            app.setOffline(false);
            Intent i = new Intent("com.pl.reunite.LOGINACTIVITY");
            startActivityForResult(i, LOGIN_REQUEST);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

	private void FindPeople() {
        if (WebServer.isOnline(this)) {
            app.setOffline(false);
            Intent i = new Intent("com.pl.reunite.PATIENTLISTEXACTIVITY");
            startActivityForResult(i, FIND_PEOPLE);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

	private void About() {
		Intent openAbout = new Intent("com.pl.reunite.ABOUT");
		startActivity(openAbout);												
	}

	private void Logout() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
		builder.setMessage(getResources().getString(R.string.logout))
			   .setTitle(getResources().getString(R.string.are_you_sure_q))
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		               // fcm first
                       unsubscribeFcm();

		       		credential.reset();
//					loginButton.setText(LOGIN);
					username = credential.getUsername();
					password = credential.getPassword();
                    app.setGroupName(credential.getGroupName());
		       		credential.saveUserPreferences(username, password);
                    credential.saveGroupIdPreferences(app.getGroupName());
		       		app.setUsername(username);
		       		app.setPassword(password);
                       if (username.equalsIgnoreCase(GUEST)){
                           whoTextView.setText(getResources().getString(R.string.guest));
                           buttonLoginLogout.setText(getResources().getString(R.string.login));
                       }
                       else {
                           whoTextView.setText(username);
                           buttonLoginLogout.setText(getResources().getString(R.string.logout));
                       }

                       app.setToken("");
                       app.setTokenAnonymous("");
                       app.setTimeWhenGotAnonymousToken(0);
                       app.setTokenStatus(ReUnite.TOKEN_UNKNOWN);
                       credential.saveTokenPreferences("");
                       credential.saveTokenAnonymousPreferences("");
                       credential.saveTimeWhenGotAnonymousTokenPreferences(0);
                       credential.saveTokenStatusPreferences(ReUnite.TOKEN_UNKNOWN);

                       /**
                        * delete all data in database
                        */
                       DeleteAllRecords();

                        // added in version 9.3.0-beta
                       finish();
                       System.exit(0);
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

    /**
     * delete all records after logout
     * added in version 7.1.6
     */
    private void DeleteAllRecords() {
        PatientsDataSource dataSource = new PatientsDataSource(this);
        dataSource.open();
        dataSource.deleteAllDraftPatients();
        dataSource.deleteAllOutboxPatients();
        dataSource.deleteAllSentPatients();
        dataSource.deleteAllSavedPatients();
        dataSource.close();
    }

    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == LOGIN_REQUEST){
			if (data == null){ // In case of doing nothing
				return;
			}
			Bundle extras = data.getExtras();
			username = (String)extras.get("username");			
			password = (String)extras.get("password");
			credential.setUsernamePreferences(username);
			credential.setPasswordPreferences(password);
            credential.setGroupNamePreferences(app.getGroupName());

            // version 4.0.0.
            credential.setTokenPreferences(app.getToken());
            credential.setTokenStatusPreferences(app.getTokenStatus());

            if (username.equalsIgnoreCase(GUEST)){
                whoTextView.setText(getResources().getString(R.string.guest));
                buttonLoginLogout.setText(getResources().getString(R.string.login));
            }
            else {
                whoTextView.setText(username);
                buttonLoginLogout.setText(getResources().getString(R.string.logout));
            }

            // process locale information
            if (app.getAuthStatus() == true) {
                curLocale = getResources().getConfiguration().locale;
                if (app.getLocale() == null || !app.getLocale().equals(curLocale)) {
                    app.setLocale(curLocale);
                    app.toLanguageCode();
                    new RestPrefAsyncTask(this).execute();
                }
            }

            // fcm
            if (app.getAuthStatus() == true){
                if (fcmAccepted == false) {
                    askIfAcceptFcm();
                }
            }
        }
		else if (requestCode == SELECT_WEBSERVER){
			webServer = app.getWebServer();
			credential.saveWebServerPreferences(webServer);
		}
		else if (requestCode == ACCEPTANCE_REQUEST){
			boolean answer = data.getExtras().getBoolean("answer");
			if (answer == false){
				new GoodByeAsyncTask().execute();
			}
		}
		else if (requestCode == BURDEN_STATEMENT_SPLASH){
			if (username.equalsIgnoreCase("Guest") == true) {
	  			if (asked == false){
	  				asked = true;
	  				Acceptance();
	  			}
			}
		}
        else if (requestCode == PICK_EVENT_REQUEST){
            if (resultCode == Activity.RESULT_CANCELED){
                return;
            }
            String eventname = data.getExtras().getString("eventname").trim();
            String eventshortname = data.getExtras().getString("eventshortname").trim();
            String eventDate = data.getExtras().getString("eventDate").trim();
            String eventStreet = data.getExtras().getString("eventStreet").trim();

            app.getLastEvent().setName(eventname);
            app.getLastEvent().setShortName(eventshortname);
            app.getLastEvent().setDate(eventDate);
            app.getLastEvent().setStreet(eventStreet);

            tvLatestEventName.setText(eventname);
            tvLatestEventStatus.setText(eventDate);
            tvLatestEventDetails.setText(eventStreet);

            credential.saveEventPreferences(eventname, eventshortname, eventDate, eventStreet);
        }
        else if (requestCode == RETURN_FROM_REPORTACTIVITY)
        {
            credential.saveNcmecClaimPreferences(app.getNcmecClaim());
        }
        else if (requestCode == FIND_PEOPLE){
            String eventname = credential.getEventNamePreferences();

            if (!app.getLastEvent().getName().equalsIgnoreCase(eventname)){
                tvLatestEventName.setText(app.getLastEvent().getName());
                tvLatestEventStatus.setText(app.getLastEvent().getDate());
                tvLatestEventDetails.setText(app.getLastEvent().getStreet());

                credential.saveEventPreferences(app.getLastEvent().getName(), app.getLastEvent().getShortName(), app.getLastEvent().getDate(), app.getLastEvent().getStreet());
            }
        }
	}

    public void setLocale(String lang) {
        Locale myLocale;
        if (lang.equals("zh_CN")) {
            myLocale = Locale.SIMPLIFIED_CHINESE;
        } else if (lang.equals("zh_TW")) {
            myLocale = Locale.TRADITIONAL_CHINESE;
        } else {
            myLocale = new Locale(lang);
        }

        Locale.setDefault(myLocale);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public class Credential {
		private boolean bAuthStatus;
		private String username;
		private String password;
		private String webServer;
		private boolean quickStart;
        private boolean developer;

        private String eventName;
        private String eventShortName;
        private String eventDate;
        private String eventStreet;

        // ncmec clam
        private int ncmecClaim;
        public int getNcmecClaim() {return ncmecClaim;}
        public void setNcmecClaim(int ncmecClaim){
            this.ncmecClaim = ncmecClaim;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        public String getEventShortName() {
            return eventShortName;
        }

        public void setEventShortName(String eventShortName) {
            this.eventShortName = eventShortName;
        }

        public String getEventDate() {
            return eventDate;
        }

        public void setEventDate(String eventDate) {
            this.eventDate = eventDate;
        }

        public String getEventStreet() {
            return eventStreet;
        }

        public void setEventStreet(String eventStreet) {
            this.eventStreet = eventStreet;
        }

        private String groupId;
        public String getGroupId() {
            return groupId;
        }
        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }
        private String groupName;
        public String getGroupName() {
            return groupName;
        }
        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        private String token;
        public String getToken(){return token;}
        public void setToken(String token){this.token = token;}

        private String tokenAnonymous;
        public String getTokenAnonymous(){return this.tokenAnonymous;}
        public void setTokenAnonymous(String tokenAnonymous){this.tokenAnonymous = tokenAnonymous;}

        private long timeWhenGotAnonymousToken;
        public void setTimeWhenGotAnonymousToken(long timeWhenGotAnonymousToken){this.timeWhenGotAnonymousToken = timeWhenGotAnonymousToken;}
        public long getTimeWhenGotAnonymousToken(){return timeWhenGotAnonymousToken;}

        private int tokenStatus;
        public void setTokenStatus(int tokenStatus) {this.tokenStatus = tokenStatus;}
        public int getTokenStatus(){return tokenStatus;}

        Credential(){
			reset();// initial
			getUsernamePreferences();
			getPasswordPreferences();
			getAuthStatus();
			getWebServer();
			getQuickStartPreferences();
            getDeveloperPreferences();
            getTokenPreferences();
            getTokenAnonymousPreferences();
            getTimeWhenGotAnonymousToken();
            getTokenStatusPreferences();
            getGroupId();
            getGroupName();
            getEventName();
            getEventShortName();
            getEventDate();
            getEventStreet();
		}
		
		public String getUsername(){
			return username;
		}
		
		public String getPassword(){
			return password;
		}
		
		public boolean getAuthStatus(){
			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true){
				bAuthStatus = false;
			}
			else {
				bAuthStatus = true;				
			}
			return bAuthStatus;
		}
		
		public boolean getQuickStart(){
			if (username.equalsIgnoreCase(GUEST) == true && password.isEmpty() == true){
				quickStart = false;
			}
			else {
				quickStart = true;				
			}
			return quickStart;			
		}

        public boolean getDeveloper(){
            return developer;
        }
        public void setDeveloper(boolean developer){
            this.developer = developer;
        }
		
		public String getWebServer(){
			return webServer;
		}

		public void reset() {
			bAuthStatus = false;
			username = GUEST;
			password = "";
            groupName = "";
//			saveUserPreferences(username, password);
			webServer = WebServer.PL_NAME;
//			saveWebServerPreferences(webServer);
			quickStart = false;
            developer = false;
            token = "";
            tokenAnonymous = "";
            tokenStatus = ReUnite.TOKEN_UNKNOWN;
            timeWhenGotAnonymousToken = 0;
            groupId = "";
            groupName = "";
            Event e = new Event(webServer);
            e.toDefault();
            eventName = e.getName();
            eventShortName = e.getShortName();
            eventDate = e.getDate();
            eventStreet = e.getStreet();

            ncmecClaim = ReUnite.NEMEC_CLAIM_UNDECIDED;
		}
		
		private void saveQuickStartPreferences(boolean quickStart) {
			this.quickStart = quickStart;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putBoolean("quickStart", this.quickStart);
			editor.commit();
		}

        private void saveDeveloperPreferences(boolean developer) {
            this.developer = developer;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("developer", this.developer);
            editor.commit();
        }

        private void saveWebServerPreferences(String webServer) {
            this.webServer = webServer;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("webServer", this.webServer);
            editor.commit();
        }

        private void saveNcmecClaimPreferences(int ncmecClaim) {
            this.ncmecClaim = ncmecClaim;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("ncmecClaim", this.ncmecClaim);
            editor.commit();
        }

		public void saveUserPreferences(String username, String password){
			this.username = username;
			this.password = password;

			String encrapedUsername;
			if (this.username.isEmpty() == true){
				encrapedUsername = null;
			}
			else {
				encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
		    }

			String encrapedPassword;
			if (this.password.isEmpty() == true){
				encrapedPassword = null;
			}
			else {
				encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
		    }
			
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", encrapedUsername);
			editor.putString("password", encrapedPassword);
            editor.putString("token", this.token);
            editor.putString("tokenAnonymous", this.tokenAnonymous);
            editor.putLong("timeWhenGotAnonymousToken", this.timeWhenGotAnonymousToken);
            editor.putInt("tokenStatus", this.tokenStatus);
            editor.putString("groupId", this.groupId);
            editor.putString("groupName", this.groupName);

			editor.commit();
		}

        private void saveTokenPreferences(String token){
            this.token = token;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("token", token);
            editor.commit();
        }

        private void saveTokenAnonymousPreferences(String tokenAnonymous){
            this.tokenAnonymous = tokenAnonymous;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tokenAnonymous", tokenAnonymous);
            editor.commit();
        }

        private void saveTimeWhenGotAnonymousTokenPreferences(long timeWhenGotAnonymousToken){
            this.timeWhenGotAnonymousToken = timeWhenGotAnonymousToken;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("timeWhenGotAnonymousToken", timeWhenGotAnonymousToken);
            editor.commit();
        }

        private void saveTokenStatusPreferences(int tokenStatus){
            this.tokenStatus = tokenStatus;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("tokenStatus", tokenStatus);
            editor.commit();
        }

        private void saveGroupIdPreferences(String tokenAnonymous){
            this.tokenAnonymous = tokenAnonymous;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("tokenAnonymous", tokenAnonymous);
            editor.commit();
        }

        private void setUsernamePreferences(String username){
			this.username = username;
			
			String encrapedUsername;
			if (this.username.isEmpty() == true){
				encrapedUsername = null;
			}
			else {
				encrapedUsername = Base64.encodeToString(this.username.getBytes(), Base64.DEFAULT );
		    }
			
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("username", encrapedUsername);
			editor.commit();
		}
		  
		private void setPasswordPreferences(String password){
			this.password = password;

			String encrapedPassword;
			if (this.password.isEmpty() == true){
				encrapedPassword = null;
			}
			else {
				encrapedPassword = Base64.encodeToString(this.password.getBytes(), Base64.DEFAULT );
		    }
						
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("password", encrapedPassword);
			editor.commit();
		}

        private void setGroupNamePreferences(String groupName){
            this.groupName = groupName;

            String encrapedGroupName;
            if (this.groupName.isEmpty() == true){
                encrapedGroupName = null;
            }
            else {
                encrapedGroupName = Base64.encodeToString(this.groupName.getBytes(), Base64.DEFAULT );
            }

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("groupName", encrapedGroupName);
            editor.commit();
        }

        private void saveEventPreferences(String eventName, String eventShortName, String eventDate, String eventStreet){
            this.eventName = eventName;
            this.eventShortName = eventShortName;
            this.eventDate = eventDate;
            this.eventStreet = eventStreet;

            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("eventName", this.eventName);
            editor.putString("eventShortName", this.eventShortName);
            editor.putString("eventDate", this.eventDate);
            editor.putString("eventStreet", this.eventStreet);
            editor.commit();
        }

        private void setTokenPreferences(String token){
            saveTokenPreferences(token);
        }
        private void setTokenAnonymousPreferences(String tokenAnonymous){saveTokenAnonymousPreferences(tokenAnonymous);}
        private void setTimeWhenGotAnonymousTokenPreferences(long timeWhenGotAnonymousToken){saveTimeWhenGotAnonymousTokenPreferences(timeWhenGotAnonymousToken);}
        private void setTokenStatusPreferences(int tokenStatus){saveTokenStatusPreferences(tokenStatus);}

        private void setEventPreferences(String eventName, String eventShortName, String eventDate, String eventStreet){saveEventPreferences(eventName, eventShortName, eventDate, eventStreet);}

        private void setWebServerPreferences(String webServer){
            this.webServer = webServer;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("webServer", this.webServer);
            editor.commit();
        }

        private void setNcmecClaimPreferences(int ncmecClaim){
            this.ncmecClaim = ncmecClaim;
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("ncmecClaim", this.ncmecClaim);
            editor.commit();
        }

        String getUsernamePreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			String encryptedUsername = sharedPreferences.getString("username", "guest");
			if (encryptedUsername.equalsIgnoreCase("Guest") == true){
				this.username = new String("Guest");
			}
			else {
				this.username = new String(Base64.decode(encryptedUsername, Base64.DEFAULT ) );
			}
			return this.username;
		}
	
		String getPasswordPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			String encryptedPassword = sharedPreferences.getString("password", "");
			if (encryptedPassword.isEmpty() == true){
				this.password = new String("");
			}
			else {
				this.password = new String(Base64.decode(encryptedPassword, Base64.DEFAULT ) );				
			}
			return this.password;
		}

        String getGroupNamePreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            String encryptedPassword = sharedPreferences.getString("groupName", "");
            if (encryptedPassword.isEmpty() == true){
                this.groupName = new String("");
            }
            else {
                this.groupName = new String(Base64.decode(encryptedPassword, Base64.DEFAULT ) );
            }
            return this.groupName;
        }

        private String getTokenPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            token = sharedPreferences.getString("token", "");
            return token;
        }

        private String getTokenAnonymousPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            tokenAnonymous = sharedPreferences.getString("tokenAnonymous", "");
            return tokenAnonymous;
        }

        private long getTimeWhenGotAnonymousTokenPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            timeWhenGotAnonymousToken = sharedPreferences.getLong("timeWhenGotAnonymousToken", 0);
            return timeWhenGotAnonymousToken;
        }

        private int getTokenStatusPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            tokenStatus = sharedPreferences.getInt("tokenStatus", ReUnite.TOKEN_UNKNOWN);
            return tokenStatus;
        }

        private String getWebServerPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.webServer = sharedPreferences.getString("webServer", WebServer.PL_NAME);
            return this.webServer;
        }

        private int getNcmecClaimPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.ncmecClaim = sharedPreferences.getInt("ncmecClaim", ReUnite.NCMEC_CLAIM_SHOW);
            return this.ncmecClaim;
        }

        private boolean getQuickStartPreferences(){
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.quickStart = sharedPreferences.getBoolean("quickStart", ReUnite.QUICK_START_DEFAULT);
			return this.quickStart;
		}

        private boolean getDeveloperPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.developer = sharedPreferences.getBoolean("developer", ReUnite.DEVELOPER_OPTION_DEFAULT);
            return this.developer;
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

        private String getEventDatePreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.eventDate = sharedPreferences.getString("eventDate", "");
            return this.eventDate;
        }
        private String getEventStreetPreferences(){
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            this.eventStreet = sharedPreferences.getString("eventStreet", "");
            return this.eventStreet;
        }
    }

    /**
     * Modified in version 7.1.1
     * Added future development menu
     */
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();

        app.setDeveloper(true);// test need to remove later.

        if (app.getDeveloper() == false){
            inflater.inflate(R.menu.main_menu, menu);
        }
        else {
            inflater.inflate(R.menu.main_menu, menu);
            inflater.inflate(R.menu.main_menu_developer, menu);
        }
        return true;
    }

    /**
     * Added in version 7.1.1
     * Added future development menu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear(); // Clear the menu first

        /* Add the menu items */
        MenuInflater inflater = getMenuInflater();

        /**
         * original
         */
        if (app.getDeveloper() == false){
            inflater.inflate(R.menu.main_menu, menu);
        }
        else {
            inflater.inflate(R.menu.main_menu, menu);
            inflater.inflate(R.menu.main_menu_developer, menu);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    boolean bLogin = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        	case R.id.itemAbout:
        		About();
        		break;
            case R.id.itemLatency:
            	Latency2();
            	break;
            case R.id.itemCache:
            	Cache();
            	break;
            case R.id.itemTutorials:
            	Tutorials();
            	break;
            case R.id.itemContactUs:
                new EmailUs(this).start();
            	break;
            case R.id.itemChangeLog:
                changeLog();
                break;
            case R.id.itemGooglePlus:
                startGooglePlus();
                break;
            case R.id.itemFacebook:
                startFacebook();
                break;
            case R.id.itemTwitter:
                startTwitter();
                break;
//            case R.id.itemGCMReceive:
//                startGcmListener();
//                break;
            case R.id.itemFutureDevelopment:
                futureDevelopment();
                break;
            case R.id.itemGCMToken:
                showGCMToken();
                break;
            case R.id.itemGCMTokenUploadMsg:
                showGCMTokenUploadMsg();
                break;
            case R.id.itemGCMSendMessage:
                sendGCMMessage();
                break;
            case R.id.itemEmailGCMToken:
                emailGCMToken();
                break;
            case R.id.itemSelectWebServer:
                selectWebServer();
                break;
            case R.id.itemShowOMB:
                showOMB();
                break;
            case R.id.itemFollowingRecords:
                callFollowingRecords();
                break;
            case R.id.itemTestInterrupt:
                testInterrupt();
                break;
            case R.id.itemRestful:
                startRestful();
                break;
            case R.id.itemEmailToken:
                emailToken();
                break;
            case R.id.itemDisplayToken:
                displayToken();
                break;
            case R.id.itemDisplayAnonymousToken:
                displayAnonymousToken();
                break;
            case R.id.itemChangePassword:
                changePassword();
                break;
            case R.id.itemNcmecClaimEnable:
                app.setNcmecClaim(ReUnite.NCMEC_CLAIM_SHOW);
                credential.saveNcmecClaimPreferences(ReUnite.NCMEC_CLAIM_SHOW);
                Toast.makeText(this, "Show NCMEC claim", Toast.LENGTH_SHORT).show();
                break;
            case R.id.itemNcmecClaimDisable:
                app.setNcmecClaim(ReUnite.NCMEC_CLAIM_NOT_SHOW);
                credential.saveNcmecClaimPreferences(ReUnite.NCMEC_CLAIM_NOT_SHOW);
                Toast.makeText(this, "No NCMEC claim", Toast.LENGTH_SHORT).show();
                break;
            case R.id.itemFcm:
                Intent i = new Intent(this, FcmActivity.class);
                startActivity(i);
                break;
            case R.id.itemSubcribeFcm:
                fcmAccepted = true;
                subscribeFcm();
                break;
            case R.id.itemUnsubcribeFcm:
                fcmAccepted = false;
                unsubscribeFcm();
                break;
            case R.id.itemSetFirebaseCloudMessagingAndEmail:
                if (credential.getAuthStatus() == true){
                    setFirebaseCloudMessagingAndEmail();
                }
                else {
                    LoginFirst();
                }
                break;
            default:
                break;
        }
        return true;
    }

    private void setFirebaseCloudMessagingAndEmail() {
        Intent i = new Intent(this, SetFirebaseCloudMessagingAndEmailActivity.class);
        startActivity(i);
    }

    private void changePassword() {
        Intent i = new Intent(this, ChangePasswordActivity.class);
        startActivity(i);
    }

    private void displayToken(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Token");
        builder.setCancelable(false);
        builder.setMessage(app.getToken());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Save to Clip Board", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                saveToClipBoard(app.getToken());
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void displayAnonymousToken(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Anonymous Token");
        builder.setCancelable(false);
        builder.setMessage(app.getTokenAnonymous());
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("Save to Clip Board", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                saveToClipBoard(app.getTokenAnonymous());
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void saveToClipBoard(String str){
        // Gets a handle to the clipboard service.
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", app.getTokenAnonymous());
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    private void emailToken() {
        String s = app.getToken();
        if (s.isEmpty()){
            Toast.makeText(this, "Token is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send email.
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        email.putExtra(Intent.EXTRA_EMAIL, "");
        email.putExtra(Intent.EXTRA_SUBJECT, "Google Authenticated Token");
        email.putExtra(Intent.EXTRA_TEXT, s);
        try {
            startActivity(Intent.createChooser(email, getResources().getString(R.string.sending_email)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(ReUniteActivity.this, getResources().getString(R.string.there_is_no_email_client_installed), Toast.LENGTH_SHORT).show();
        }
    }

    private void startRestful() {
        Intent i = new Intent(this, RestfulActivity.class);
        startActivity(i);
    }

    /*
    private void rateThisApp() {
        Intent i = new Intent(this, TestRateThisAppActivity.class);
        startActivity(i);
    }
    */

    private void testInterrupt() {
        new TestInterruptAsync(this).execute();

    }

    private void callFollowingRecords() {
        if (app.getAuthStatus() == true){
            if (WebServer.isOnline(this)) {
                app.setOffline(false);
                app.setFollowRecordList(followingRecords());
                if (!app.getFollowRecordList().isEmpty()) {
                    showFollowingRecords();
                }
                else { // no following record
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.follow_record));
                    builder.setCancelable(false);
                    builder.setMessage(getResources().getString(R.string.you_do_not_have_any_following_records));
                    builder.setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else { // Off line
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.offline));
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setCancelable(false);
                builder.setMessage(getResources().getString(R.string.you_do_not_have_internet_connection));
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        else { // not log in yet
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.warning));
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setCancelable(false);
            builder.setMessage(getResources().getString(R.string.need_to_login));
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.cancel();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void showFollowingRecords() {
        curSelFollowRecord = 0;
        String[] followingRecords = new String[app.getFollowRecordList().size()];
        for (int i = 0; i < app.getFollowRecordList().size(); i++){
            FollowRecord fr = app.getFollowRecordList().get(i);
            followingRecords[i] = fr.getName();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.follow_record));
        builder.setCancelable(false);
        builder.setSingleChoiceItems(followingRecords, curSelFollowRecord, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                curSelFollowRecord = item;
            }
        });
        builder.setNeutralButton(getResources().getString(R.string.no_follow), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id) {
                new unfollowAllRecordsAsyncTask().execute();
                dialog.cancel();
            }
        });
        builder.setPositiveButton(getResources().getString(R.string.select), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (curSelFollowRecord >= 0) {
                    FollowRecord fr = app.getFollowRecordList().get(curSelFollowRecord);
                    String query = "p_uuid:" + fr.getUuid();
                    new searchPatientsByQueryAsyncTask(query).execute();
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void searchPatientsByQuery(String query) {
        List<Patient> patientList = new ArrayList<Patient>();
        WebServer ws = new WebServer(app.getWebServer()); // version 7.1.3
        ws.setTokenStatus(app.getTokenStatus());
        ws.setTokenAnonymous(app.getTokenAnonymous());
        ws.setToken(app.getToken());
        ws.setSearchCountOnly(false);
        ws.setCurPageStart(0);
        ws.setQuery(query);
        Filters filters = new Filters();
        filters.setDefaults();
        ViewSettings viewSettings = new ViewSettings();
        viewSettings.SetToDefault();
        RestSearchResult sr = ws.restCallSearchCountV34(this, filters, viewSettings, app.getLastEvent().getShortName());
        if (sr.getErrorCode().equals("0")){
            patientList.addAll(sr.getPatientList());
            if (!patientList.isEmpty()){
                startPatientInfoForLoggedInUserActivity(patientList, 0);
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getResources().getString(R.string.follow_record));
                builder.setCancelable(false);
                builder.setMessage(getResources().getString(R.string.no_record_is_found));
                builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void startPatientInfoForLoggedInUserActivity(List<Patient> list, int position) {
        Intent i;

        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH){
            i = new Intent(this, PatientInfoForLoggedInUserActivity.class);
        }
        else {
            return;
        }

        Patient p = new Patient(list.get(position), webServer);
        p.setEventName(app.getLastEvent().getName());
        p.setEventShortName(app.getLastEvent().getShortName());

        // if there is no image data
        if (p.getStatusPhotoDownload() == false) {
            p.downloadPatientPhoto();
            p.setStatusPhotoDownload(true);
        }

        app.setCurSelPatient(p);
        startActivity(i);
    }

    private void followRecord(final String uuid, final int curFollowStatus) {
        WebServer ws = new WebServer(app.getWebServer()); // Add the argument in. Modified in version 7.1.6
        ws.setTokenStatus(app.getTokenStatus());
        ws.setToken(app.getToken());
//        ReportResult reportResult = ws.callFollowRecord(uuid, curFollowStatus, this);
        RestFollowResult restFollowResult = null;
        try {
//            restFollowRecordResult = ws.restCallFollowList(this, app.getToken(), app.getLocale().getLanguage());
            boolean action = false;
            if (curFollowStatus == FollowRecord.FOLLOW_OFF){
                action = false;
            }
            else {
                action = true;
            }
            ws.restFollow(this, app.getToken(), uuid, action);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unfollowAllRecords() {
        for (int i = 0; i < app.getFollowRecordList().size(); i++) {
            FollowRecord followRecord = app.getFollowRecordList().get(i);
            followRecord(followRecord.getUuid(), FollowRecord.FOLLOW_OFF);
        }
    }

    private List<FollowRecord> followingRecords() {
        List<FollowRecord> followRecordList = new ArrayList<FollowRecord>();
        WebServer ws = new WebServer(app.getWebServer()); // Add the argument in. Modified in version 7.1.6
        ws.setTokenStatus(app.getTokenStatus());
        ws.setToken(app.getToken());
//        FollowRecordResult followRecordResult = ws.callFollowList(this);
        RestFollowRecordResult restFollowRecordResult = null;
        try {
            restFollowRecordResult = ws.restCallFollowList(this, app.getToken(), app.getLanguageCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
        FollowRecordResult followRecordResult = new FollowRecordResult();
        followRecordResult.setErrorCode(restFollowRecordResult.getErrorCode());
        followRecordResult.setRecords(restFollowRecordResult.getRecords());
        followRecordResult.setErrorMessage(restFollowRecordResult.getErrorMessage());
        if (followRecordResult.getErrorCode().equalsIgnoreCase("0")){
            followRecordList = parsingFollowRecord(followRecordResult.getRecords());
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setMessage(followRecordResult.getErrorMessage());
            builder.setCancelable(false);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    return;
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        return followRecordList;
    }

    public List<FollowRecord> parsingFollowRecord(String inString){
        List<FollowRecord> followRecordList = new ArrayList<FollowRecord>();

        String string = "followRecord";
        String toParseString = "{" + "\"" + string + "\":" + inString + "}";
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(toParseString);
            JSONArray jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject o = jsonArray.getJSONObject(i); // create a single event jsonObject
                FollowRecord fr = new FollowRecord();
                String str = o.getString("name");
                if (str.contentEquals("unk")){
                    str = getResources().getString(R.string.unknown);
                }
                fr.setName(str);
                fr.setUuid(o.getString("uuid"));
                fr.setUrl(o.getString("url"));
                followRecordList.add(fr);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }     // create a json object from a string

        return followRecordList;
    }

    private void selectWebServer() {
        /**
         * if currrent user is login. logout first!
         */
        if (app.getUsername().equalsIgnoreCase("Guest") == false){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_info);
            builder.setTitle(getResources().getString(R.string.logout_first));
            builder.setCancelable(false);
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss();
                    return;
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(android.R.drawable.ic_dialog_alert);
            builder.setTitle(getResources().getString(R.string.select_web_server));
            builder.setCancelable(false);
            nPreSelWS = 0;
            nCurSelWS = 0;
            if (app.getWebServer().isEmpty()) {
                nPreSelWS = 0;
                nCurSelWS = 0;
            } else if (app.getWebServer().equalsIgnoreCase(WebServer.PL_NAME)) {
                nPreSelWS = 0;
                nCurSelWS = 0;
            } else {
                nPreSelWS = 1;
                nCurSelWS = 1;
            }
            builder.setSingleChoiceItems(WebServerArray, nPreSelWS, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    nCurSelWS = item;
                }
            });
            builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switchWebServer(item);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    switchWebServer(item);
                    dialog.dismiss();
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void switchWebServer(int item){
        if (item == -2){ // Cancel button, nothing is changed
            return;
        }
        else if (item == -1){ // OK button
            if (nCurSelWS == nPreSelWS){ // no change
                return;
            }
            else {
                prepareData();

                // add remind the user to restart. add in version 7.1.6
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.change_is_made_));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        dialog.dismiss();
                        System.exit(0);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void prepareData(){
        webServer = WebServerArray[nCurSelWS].toString();
        app.setWebServer(webServer);
        credential.setWebServerPreferences(app.getWebServer());

        app.setToken("");
        app.setTokenAnonymous("");
        app.setTimeWhenGotAnonymousToken(0);
        app.setTokenStatus(ReUnite.TOKEN_UNKNOWN);
        credential.saveTokenPreferences("");
        credential.saveTokenAnonymousPreferences("");
        credential.saveTimeWhenGotAnonymousTokenPreferences(0);
        credential.saveTokenStatusPreferences(ReUnite.TOKEN_UNKNOWN);

        credential.saveNcmecClaimPreferences(ReUnite.NCMEC_CLAIM_SHOW);
        app.setNcmecClaim(credential.getNcmecClaimPreferences());
    }

    private void emailGCMToken() {
        String s = app.getGcmToken();
        if (s.isEmpty()){
            Toast.makeText(this, "Google authenticated token is empty.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Send email.
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        email.putExtra(Intent.EXTRA_EMAIL, "");
        email.putExtra(Intent.EXTRA_SUBJECT, "Google Authenticated Token");
        email.putExtra(Intent.EXTRA_TEXT, s);
        try {
            startActivity(Intent.createChooser(email, getResources().getString(R.string.sending_email)));
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(ReUniteActivity.this, getResources().getString(R.string.there_is_no_email_client_installed), Toast.LENGTH_SHORT).show();
        }

        // tracker
        app.tracker().setScreenName(app.TRACK_CONTACT);
        app.tracker().send(new HitBuilders.ScreenViewBuilder().build());
    }

    private void sendGCMMessage() {
        Intent i = new Intent(this, SendGcmMessageActivity.class);
        startActivity(i);
    }

    private void futureDevelopment() {
        Intent i = new Intent(this, FutureDevelopmentActivity.class);
        startActivity(i);
    }

    private void startTwitter() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(TWITTER_URL));
        startActivity(i);
    }

    private void startGooglePlus() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(GOOGLE_PLUS_URL));
        startActivity(i);
    }

    private void startFacebook() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(FACEBOOK_URL));
        startActivity(i);
    }

    private void selectEvent() {
        if (WebServer.isOnline(this)) {
            app.setOffline(false);
            Intent i = new Intent(this, EventListForReportActivity.class);
            startActivityForResult(i, PICK_EVENT_REQUEST);
        }
        else {
            app.setOffline(true);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    public String getAnonymousToken(){
        String errorCode = "";
        // get the anonymous token
        if (app.getWebServer().isEmpty()){
            app.setWebServer(WebServer.PL_NAME);
        }
        WebServer ws = new WebServer(app.getWebServer());
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
            errorCode = "";
        }
        return errorCode;
    }

    private void changeLog() {
        Intent i = new Intent(this, ChangeLogActivity.class);
        startActivity(i);
    }

	private void QuickStart() {
		if (quickStart == true){
			nCurSelQuickStart = 0;
		}
		else {
			nCurSelQuickStart = 1;			
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
		builder.setTitle("Set splash screen to show");

		builder.setCancelable(false);
		
		builder.setSingleChoiceItems(items, nCurSelQuickStart, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                nCurSelQuickStart = item;
            }
        });
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (nCurSelQuickStart == 0) {
                    quickStart = true;
                } else {
                    quickStart = false;
                }
                app.setQuickStart(quickStart);
                credential.saveQuickStartPreferences(quickStart);
                dialog.dismiss();
            }
        });
		builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                dialog.cancel();
            }
        });
		AlertDialog alert = builder.create();		
		alert.show();		
	}

	private void Tutorials() {
		Intent i = new Intent(ReUniteActivity.this, TutorialListActivity.class);
		startActivity(i);	
	}

	private void Cache() {
		Intent i = new Intent(ReUniteActivity.this, CacheActivity.class);
		startActivity(i);	
	}

	private void Latency2() {
        if (WebServer.isOnline(this) == true) {
            app.setOffline(false);
            Intent i = new Intent(ReUniteActivity.this, LatencyActivity.class);
            i.putExtra("webServer", webServer);
            startActivity(i);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

	private void SelectWebServer() {

	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

        if (app.getDeveloper() == true){
            menu.setHeaderTitle("Advanced Settings");
            menu.add(Menu.NONE, SEL_WEB_SERVER_ID, Menu.NONE, "Select Web Server");
        }
    }	

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    
		switch (item.getItemId()) {
        case SEL_WEB_SERVER_ID:
        	SelectWebServer();
    	    break;
        }

		return super.onContextItemSelected(item);
	}
	
    // modified for version 4.0.0.
    // 1. check if there is connection.
    // 2. check the token, if not available get the anonymous token.
    // 3. ping
    public RestPingResult CheckInternetConnection() {
        RestPingResult result = new RestPingResult(ReUniteActivity.this);
        // replaced by the following codes
        // version 4.0.0.
        if (app.getTokenStatus() == ReUnite.TOKEN_AUTH){
            myPingEcho.setToken(app.getToken());
//            pingEchoResult = myPingEcho.Call();
            result = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }
        else if (app.getTokenStatus() == ReUnite.TOKEN_UNKNOWN){
            getAnonymousToken();
            app.recordTimeWhenGotAnonymousToken();
            myPingEcho.setToken(app.getTokenAnonymous());
//            pingEchoResult = myPingEcho.Call();
            result  = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }
        else if (app.getTokenStatus() == ReUnite.TOKEN_ANONYMOUS) {
            if (app.isAnonymousTokenExpired()){
                getAnonymousToken();
                app.recordTimeWhenGotAnonymousToken();
            }
            myPingEcho.setToken(app.getTokenAnonymous());
//            pingEchoResult = myPingEcho.Call();
            result  = myPingEcho.restCall(this, app.getLatitude(), app.getLongitude());
        }
        return result;
    }

    private class CheckInternetConnectionAsyncTask extends AsyncTask<Void, Integer, Void>
    {
//        PingEchoResult pingEchoResult;
        RestPingResult result;

        //Before running code in separate thread
        @Override  
        protected void onPreExecute()  
        {
            // added in version 4.0.0
            if (app.getTokenStatus() == ReUnite.TOKEN_UNKNOWN) {
                getAnonymousToken();
                app.recordTimeWhenGotAnonymousToken();
            }
//            pingEchoResult = new PingEchoResult();
//        	pingEchoResult.toDefault();
            result = new RestPingResult(ReUniteActivity.this);
			tvServerLatency.setText("Detecting...");
//			ratingBar.setRating(0);
//            imageViewServerRating.setImageResource(ServerRatingImage[UNKNOWN]);
            textViewServerRating.setText(getServerRatingString(UNKNOWN));
            textViewServerRating.setTextColor(ServerRatingColor[UNKNOWN]);
        }
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {  
            //Get the current thread's token  
			synchronized (this)  
			{
                result = CheckInternetConnection();
                if (result.getErrorCode().contentEquals("-1")){
                    pingEchoResult.setErrorCode(result.getErrorCode());
                    pingEchoResult.setErrorMessage(result.getErrorMessage());
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
            if (pingEchoResult.getErrorCode().equalsIgnoreCase("-1")){
                String errMsg = myPingEcho.getErrorMessage(ReUniteActivity.this, MyPingEcho.NETWORK_ERR);
    			AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
    			builder.setMessage(errMsg)
    			       .setCancelable(true)
                       .setIcon(android.R.drawable.ic_dialog_alert)
    			       .setTitle(getResources().getString(R.string.latency))
    			       .setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
    			           public void onClick(DialogInterface dialog, int id) {
    			                dialog.cancel();
//                               imageViewServerRating.setImageResource(ServerRatingImage[DISCONNECTED]);
                               textViewServerRating.setText(getServerRatingString(DISCONNECTED));
                               textViewServerRating.setTextColor(ServerRatingColor[DISCONNECTED]);
                               app.setOffline(true);
    			           }
    			       });
    			AlertDialog alert = builder.create();		
    			alert.show();
                tvServerLatency.setText(errMsg);
    		}
    		else {
                tvServerLatency.setText(myPingEcho.getLatencyTime() + "ms");
                int r = Integer.valueOf(myPingEcho.getLatencyTime());
                if (r < 500) {
//                    imageViewServerRating.setImageResource(ServerRatingImage[EXCELLENT]);
                    textViewServerRating.setText(getServerRatingString(EXCELLENT));
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                } else if (r < 750) {
//                    imageViewServerRating.setImageResource(ServerRatingImage[GOOD]);
                    textViewServerRating.setText(getServerRatingString(GOOD));
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                } else if (r < 1000) {
//                    imageViewServerRating.setImageResource(ServerRatingImage[POOR]);
                    textViewServerRating.setText(getServerRatingString(POOR));
                    textViewServerRating.setTextColor(ServerRatingColor[CONNECTED]);
                } else {
//    				ratingBar.setRating(0);
//                    imageViewServerRating.setImageResource(ServerRatingImage[DISCONNECTED]);
                    textViewServerRating.setText(getServerRatingString(DISCONNECTED));
                    textViewServerRating.setTextColor(ServerRatingColor[DISCONNECTED]);
                    app.setOffline(true);
                }
            }
		}
    }  
    
    private class GoodByeAsyncTask extends AsyncTask<Void, Integer, Void>  
    {  
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {
            progressDialog = new ProgressDialog(ReUniteActivity.this);  
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
            progressDialog.setMessage(getResources().getString(R.string.good_bye));
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
				try {
					Thread.sleep(1000);
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
            //close the progress dialog  
            progressDialog.dismiss();  
			finish();
			System.exit(0);
        }  
    }

    private class unfollowAllRecordsAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(ReUniteActivity.this);
    		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.un_flowing_all_the_records_please_wait));
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
                unfollowAllRecords();
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
            progressDialog.dismiss();
        }
    }

    private class searchPatientsByQueryAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        ProgressDialog progressDialog;
        private String query;

        public searchPatientsByQueryAsyncTask(String query){
            this.query = query;
        }

        @Override
        protected void onPreExecute()
        {
            progressDialog = new ProgressDialog(ReUniteActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(getResources().getString(R.string.searching_the_selected_record_));
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
                searchPatientsByQuery(query);
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
            progressDialog.dismiss();
        }
    }

    public String getServerRatingString(int index){
        String status = "";
        switch (index){
            case 0:
                status = getResources().getString(R.string.unknown);
                break;
            case 1:
                status = getResources().getString(R.string.disconnected);
                break;
            case 2:
                status = getResources().getString(R.string.poor);
                break;
            case 3:
                status = getResources().getString(R.string.good);
                break;
            case 4:
                status = getResources().getString(R.string.excellent);
                break;
            default:
                status = "";
                break;
        }
        return status;
    }

    public class TestInterruptAsync extends AsyncTask<Void, Void, Void> {

        private volatile boolean running = true;
        private final ProgressDialog progressDialog;

        public TestInterruptAsync(Context c) {
            progressDialog = new ProgressDialog(c);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Test interrupt...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    cancel(true);
                    dialog.dismiss();
                }
            });
            progressDialog.show();
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
        }

        @Override
        protected void onCancelled() {
            running = false;
        }

        @Override
        protected Void doInBackground(Void... params) {

            while (running) {
                // does the hard work
                long step = 1;
                for (int i = 0; i > 1000000; i++){
                    try {
                        Thread.sleep(step);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        // ...
    }

    public RestPrefResult prefRestCall() throws Exception {
        RestPrefResult rest = new RestPrefResult(this);

        WebServer ws = new WebServer(app.getWebServer());
        String url = ws.getRestEndpoint();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildPrefJson(this);
        if (jo == null){
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

        JSONObject jsonResult = null;
        if (!jsonStr.isEmpty()) {
            jsonResult = new JSONObject(jsonStr);
            rest.setErrorCode(jsonResult.get("error").toString());
            if (rest.getErrorCode().equalsIgnoreCase("0") == true){
//                rest = extractUserJson(this, jsonResult, rest);
            }
            else {
                rest.searchErrorMessage();
            }
        }
        return rest;
    }

    public JSONObject buildPrefJson(Context c) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "pref");
            if (app.getAuthStatus() == true) {
                json.put("token", app.getToken().toString());
            }
            else {
                json.put("token", app.getTokenAnonymous().toString());
            }
            json.put("locale", app.getLanguageCode());
        }
        catch (Exception ex){
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public RestPrefResult extractUserJson(Context c, JSONObject j, RestPrefResult rest) throws JSONException {
        try {
            rest.setToken2(j.get("token").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        try {
            rest.setGid(j.get("gid").toString());
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }
        return rest;
    }

    // to replace EnterAsyncTask
    private class RestPrefAsyncTask extends AsyncTask<Void, Integer, Void> {
        private RestPrefResult rest;
        private String errorCode;
        private String errorMessage;

        String returnStr = "";
        ProgressDialog progressDialog;

        private int func;
        private Context c;

        RestPrefAsyncTask(Context c){
            this.func = func;
            this.c = c;
        }
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage(c.getResources().getString(R.string.reporting_locale_dot));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
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
                try {
                    rest = prefRestCall();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    returnStr = e.getMessage();
                } catch (Exception e) {
                    e.printStackTrace();
                    returnStr = e.getMessage();
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
            progressDialog.dismiss();
            if (rest == null) {
                String msg = "system error";
                Toast.makeText(ReUniteActivity.this, msg, Toast.LENGTH_SHORT).show();
                return;
            }
            if (rest.getErrorCode().equalsIgnoreCase("0")){
                Toast.makeText(ReUniteActivity.this, app.getLocale().getCountry() + ", " + app.getLocale().getLanguage(), Toast.LENGTH_SHORT).show();
                return;
            }
            else {
                rest.setErrorCode(rest.getErrorCode());
                AlertDialog.Builder builder = new AlertDialog.Builder(ReUniteActivity.this);
                builder.setMessage(rest.searchErrorMessage())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(true)
                        .setTitle(getResources().getString(R.string.error))
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

    // fcm
    public void subscribeFcm() {
        RestGcmResult result = sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), true);
        if (result.getErrorCode().equalsIgnoreCase("0")){
            Toast.makeText(this, getResources().getString(R.string.pad3subStatusSubscribed), Toast.LENGTH_LONG).show();
        }
        app.setGcmTokenUploadMsg(result.getErrorMessage());
    }

    public void unsubscribeFcm() {
        RestGcmResult result = sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), false);
        if (result.getErrorCode().equalsIgnoreCase("0")){
            Toast.makeText(this, getResources().getString(R.string.pad3subStatusUnsubscribed), Toast.LENGTH_LONG).show();
        }
        app.setGcmTokenUploadMsg(result.getErrorMessage());
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     */
    private RestGcmResult sendRegistrationToServer(String fcmToken, boolean sub) {
        // Add custom implementation, as needed.
        ReUnite app = ((ReUnite)this.getApplication());
        /**
         *
         */
        // start
        WebServer ws = new WebServer(app.getWebServer()); // modified in version 7.1.3
//        com.pl.reunite.WebServer ws = new com.pl.reunite.WebServer();
        // end
        ws.setTokenStatus(app.getTokenStatus());
        ws.setTokenAnonymous(app.getTokenAnonymous());
        ws.setToken(app.getToken());
        //ReportResult reportResult = ws.callSendGcmToken(gsmToken, this);
        RestGcmResult restReportResult = ws.restCallSendGcmToken(this, fcmToken, sub);
        return restReportResult;
    }
}