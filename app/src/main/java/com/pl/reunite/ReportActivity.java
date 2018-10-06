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

/**
 * ReportActivity
 * Report missing person
 *
 * Rewrite codes to use restful calls.
 * May 2017
 * Version 9.2.10.
 */
package com.pl.reunite;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pl.reunite.Result.RestReportResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;

/**
 * Version 9.2.10: remove face detection, remove annotation
 */
//import org.kobjects.base64.Base64;

public class ReportActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "ReportActivity";

	static final int PICK_EVENT_REQUEST = 1;
	static final int TAKE_PICTURE_REQUEST = 2;
	static final int ADDRESS_REQUEST = 3;
	static final int PICK_PHOTO_REQUEST = 5;

	private static final int LOGIN_REQUEST = 1;

	private static final int GPS_PERMISSION_REQUEST_CODE = 1;
	private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
	public static final int RUN_GPS = 100;
	public static final int RUN_MAP = 101;
	public static final int RUN_CAMERA = 102;

	// place picker
	// version 7.2.10
	// start
	int PLACE_PICKER_REQUEST = 1000;
	private GoogleApiClient mGoogleApiClient;
	// end

	private Context context;
	private Activity activity;
	private View view;

	ReUnite app;
	String webServer = "";
	String nameSpace = "";
	String url = "";
	String soapAction = "";

	private CharSequence[] items; // = {"Male", "Female", "Unknown", "Complex"};
	private CharSequence[] statusItems; // = {"Missing", "Alive and well", "Injured", "Deceased", "Unknown", "Found"};

	// define to select image source
	final String camera = "Camera";
	private CharSequence[] ImageSourceItems; // = {"camera", "gallery", "primary", "delete"};
	private ArrayList<Image> images;
	private int curSelImage;

	final int selectCamera = 0;
	final int selectGallery = 1;
	private int curSelImageSource = selectCamera;
	private String strCurSelImageSource;

	double currentLatitude = 0.0;
	double currentLongitude = 0.0;

	private String strGender = Patient.UNKNOWN;
	private int nGenderItem = 2;
	private int nCurSelGenderItem = nGenderItem;
	private String strCurSelGender = strGender;

	private String strStatus = Patient.UNKNOWN;
	private int nStatusItem = 4;
	private int nCurSelStatusItem = nStatusItem;
	private String strCurSelStatus = strStatus;

	private String returnUrl = "";
	private String returnErrorCode = "";
	private String returnErrorMessage = "";

	private PatientsDataSource datasource;
	Patient patient;

	private AddressDataSource addressDatasource;
	PatientAddress patientAddress;

	private ImageDataSource imageDatasource;

	// Defauls
	Defaults defaults;

	Button buCamera;
	Button buGallery;

	ImageView ivPatientPhoto;

	EditText etFullName;

	/**
	 * replace edit box by seek bar
	 */
	// seekbar for age input
	SeekBar seekBarAge;
	TextView textViewAge;
	int progressAge = 0;
	// end

	Button buSelectGender;
	//	EditText etSelectGender;
	TextView tvSelectGender;

	Button buSelectStatus;
	TextView tvSelectStatus;

	Button buSelectEvent;
	TextView tvSelectEvent;

	Button buSelectLastSeenLocation;
	TextView tvSelectLastSeenLocation;

	// place picker
	// added in version 7.2.10
	Button buttonPlacePicker;

	// Google Map View
	Button buGoogleMapView;
	TextView tvLocationUrl;

	Button buAddressBook;

	EditText etAddress1;
	EditText etAddress2;
	EditText etCity;
	EditText etState;
	EditText etZip;
	EditText etCountry;

	EditText etMoreDetails;

	TextView tvMessage;
	Button buSaveAsDraft;
	Button buUpload;

	/**
	 * animal, version 7.3.6
	 */
	RadioButton radioButtonPerson;
	RadioButton radioButtonAnimal;
	static int currentReport = Patient.PERSON;
	LinearLayout linearLayoutBuddy;
	EditText etBuddy;

	private boolean result;

	//A ProgressDialog object  
	private ProgressDialog progressDialog;
	private GoogleApiClient client;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.report);

		app = ((ReUnite) this.getApplication());
		app.detectMobileDevice(this);
		app.setScreenOrientation(this);

		webServer = app.getWebServer();

		Initialize();
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	private void Initialize() {
		setItem();
		setStatusItems();
		setImageSourceItems();
		strCurSelImageSource = ImageSourceItems[0].toString();

		defaults = new Defaults();

		buCamera = (Button) findViewById(R.id.buttonCamera);
		buGallery = (Button) findViewById(R.id.buttonGallery);

		// photo section - start
		ivPatientPhoto = (ImageView) findViewById(R.id.ivPatientPhoto);
		ivPatientPhoto.setImageResource(R.drawable.questionmark);

		images = new ArrayList<Image>();
		curSelImage = 0;
		// photo section - end

//		etGivenName = (EditText) findViewById(R.id.etGivenName);
//		etFamilyName = (EditText) findViewById(R.id.etFamilyName);
		etFullName = (EditText) findViewById(R.id.etFullName);

		// change to use seek bar for age input
		// start
		InitializeAge();
		// end

		buSelectGender = (Button) findViewById(R.id.buttonSelectGender);
		tvSelectGender = (TextView) findViewById(R.id.tvSelectGender);
//		etSelectGender.setText("Unknown");
		buSelectStatus = (Button) findViewById(R.id.buttonSelectStatus);
		tvSelectStatus = (TextView) findViewById(R.id.tvSelectStatus);

		buSelectEvent = (Button) findViewById(R.id.buttonSelectEvent);
		tvSelectEvent = (TextView) findViewById(R.id.tvEvent);

		buSelectLastSeenLocation = (Button) findViewById(R.id.buttonSelectLastSeenLocation);
		tvSelectLastSeenLocation = (TextView) findViewById(R.id.tvLastSeenLocation);

		// place picker
		// added in version 7.2.10
		buttonPlacePicker = (Button) findViewById(R.id.buttonPlacePicker);
		buGoogleMapView = (Button) findViewById(R.id.buttonGoogleMapView);
		tvLocationUrl = (TextView) findViewById(R.id.tvLocationUrl);

		buAddressBook = (Button) findViewById(R.id.buttonAddressBook);

		etAddress1 = (EditText) findViewById(R.id.etAddress1);
		etAddress2 = (EditText) findViewById(R.id.etAddress2);
		etCity = (EditText) findViewById(R.id.etCity);
		etState = (EditText) findViewById(R.id.etState);
		etZip = (EditText) findViewById(R.id.etZip);
		etCountry = (EditText) findViewById(R.id.etCountry);

		etMoreDetails = (EditText) findViewById(R.id.editTextMoreDetails);

		tvMessage = (TextView) findViewById(R.id.tvMessage);
		buSaveAsDraft = (Button) findViewById(R.id.buSaveAsDraft);
		buUpload = (Button) findViewById(R.id.buUpload);

		buCamera.setOnClickListener(this);
		buGallery.setOnClickListener(this);
		ivPatientPhoto.setOnClickListener(this);

//		etAgeMax.setOnClickListener(this);

		buSelectGender.setOnClickListener(this);
		buSelectStatus.setOnClickListener(this);

		buSelectEvent.setOnClickListener(this);

		buSelectLastSeenLocation.setOnClickListener(this);
		buttonPlacePicker.setOnClickListener(this); // place picker, added in version 7.2.10
		buGoogleMapView.setOnClickListener(this); // Google Map View

		buAddressBook.setOnClickListener(this);

		buSaveAsDraft.setOnClickListener(this);
		buUpload.setOnClickListener(this);

		patient = new Patient(webServer);
		patientAddress = new PatientAddress();

		datasource = new PatientsDataSource(this);
		datasource.open();

		addressDatasource = new AddressDataSource(this);
		addressDatasource.open();

		imageDatasource = new ImageDataSource(this);
		imageDatasource.open();

		app = (ReUnite) this.getApplicationContext();

		tvSelectEvent.setText(defaults.getEventName());
		etAddress1.setText(defaults.getStreet1Preferences());
		etAddress2.setText(defaults.getStreet2Preferences());
		etCity.setText(defaults.getCityPreferences());
		etState.setText(defaults.getStatePreferences());
		etZip.setText(defaults.getZipPreferences());
		etCountry.setText(defaults.getCountryPreferences());

		// this section is added for pet
		radioButtonPerson = (RadioButton)findViewById(R.id.radioButtonPerson);
		radioButtonAnimal = (RadioButton)findViewById(R.id.radioButtonAnimal);

		radioButtonPerson.setOnClickListener(this);
		radioButtonAnimal.setOnClickListener(this);
		linearLayoutBuddy = (LinearLayout) findViewById(R.id.linearLayoutBuddy);

		if (currentReport == Patient.PERSON){
			radioButtonPerson.setChecked(true);
			radioButtonAnimal.setChecked(false);
			linearLayoutBuddy.setVisibility(View.INVISIBLE);
		}
		else {
			radioButtonPerson.setChecked(false);
			radioButtonAnimal.setChecked(true);
			linearLayoutBuddy.setVisibility(View.VISIBLE);
		}

		etBuddy = (EditText) findViewById(R.id.etBuddy);

		/**
		 * Set default data
		 * version 7.2.7-beta, version code 70207000
		 */
		loadDefaultRecords();

		//
		mGoogleApiClient = new GoogleApiClient
				.Builder(this)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.build();
	}

	private void loadDefaultRecords() {
		loadDefaultAge();
		loadDefaultGender();
		loadDefaultStatus();
	}

	private void loadDefaultStatus() {
		int color = Patient.getColorFromStatusArray(this,  Patient.UNKNOWN_SHORT);
		tvSelectStatus.setText(getResources().getString(R.string.unknown));
		tvSelectStatus.setTextColor(color);
	}

	private void loadDefaultGender() {
		tvSelectGender.setText(getResources().getString(R.string.unknown));
	}

	private void loadDefaultAge() {
		textViewAge.setText(getResources().getText(R.string.unknown));
		seekBarAge.setProgress(0);
	}

	private void InitializeAge() {
		seekBarAge = (SeekBar) findViewById(R.id.seekBarAge);
		seekBarAge.setMax(121);
		seekBarAge.setProgress(0);
		seekBarAge.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
				progressAge = progress;
				String dis;
				if (progressAge == 0){
					dis = getResources().getString(R.string.unknown);
				}
				else {
					dis = String.valueOf(progressAge - 1);
				}
				textViewAge.setText(dis);
			}

			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});
		textViewAge = (TextView) findViewById(R.id.textViewAge);
	}

	public void onClick(View v) {
		view = v;
		switch (v.getId()) {
			case R.id.ivPatientPhoto:
				WhereToGetPhoto();
				break;
			case R.id.buttonCamera:
				new checkPermissionAsyncTask(RUN_CAMERA, this).execute();
//				takePicture();
				break;
			case R.id.buttonGallery:
				pickPhoto();
				break;
			case R.id.buttonSelectGender:
				SelectGender();
				break;
			case R.id.buttonSelectStatus:
				SelectStatus();
				break;
			case R.id.buttonSelectEvent:
				SelectEvent();
				break;
			case R.id.buttonSelectLastSeenLocation:
				new checkPermissionAsyncTask(RUN_GPS, this).execute();
//				GetLocationFromGps();
				break;
			case R.id.buttonPlacePicker:	// place picker, added in version 7.2.10
				placePicker();
				break;
			case R.id.buttonGoogleMapView: // Google Map View
				new checkPermissionAsyncTask(RUN_MAP, this).execute();
				break;
			case R.id.buUpload:
				// add warning message for anonymous users.
				if (app.getTokenStatus() == ReUnite.TOKEN_ANONYMOUS) {
					Toast.makeText(this, "Anonymous user has the limited function.", Toast.LENGTH_SHORT).show();
				}
				Upload();
				break;
			case R.id.buSaveAsDraft:
				BuildPatientData();
				deleteCurPatientRecord(patient);
				saveAsDraft();
				finish();
				break;
			case R.id.buttonAddressBook:
				OpenAddressBook();
				break;
			case R.id.radioButtonPerson:
			case R.id.radioButtonAnimal:
				if (radioButtonPerson.isChecked() == true){
					currentReport = Patient.PERSON;
					linearLayoutBuddy.setVisibility(View.INVISIBLE);
				}
				else {
					currentReport = Patient.ANIMAL;
					linearLayoutBuddy.setVisibility(View.VISIBLE);
				}
				break;
			default:
				break;
		}
	}

	/**
	 * Google Place Picker
	 * added version 7.2.10
	 */
	private void placePicker() {
		PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
		try {
			startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
		} catch (GooglePlayServicesRepairableException e) {
			e.printStackTrace();
		} catch (GooglePlayServicesNotAvailableException e) {
			e.printStackTrace();
		}
	}

	private void takePicture() {
		Intent i = startCamera();
		startActivityForResult(i, TAKE_PICTURE_REQUEST);
		app.setCameraIntent(i);
	}

	public Intent startCamera() {
		// Get a unique file name for uri.
		// Prepare information for uri.
		ContentValues values = new ContentValues();
		values.put(MediaStore.Images.ImageColumns.IS_PRIVATE, 1);
		values.put(MediaStore.MediaColumns.TITLE, System.currentTimeMillis());
		values.put(MediaStore.Images.ImageColumns.DESCRIPTION, "Image capture by camera");
		values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
		Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		// Prepare intent.
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Log.e("Get image from camera", uri.toString());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		return intent;
	}

	private void buildAlertMessageNoGps() {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
						dialog.cancel();
					}
				});
		final AlertDialog alert = builder.create();
		alert.show();
	}

	private void deleteCurPatientRecord(Patient p) {
		imageDatasource.deleteAllImages(p.getSerialId());
		datasource.deletePatient(p);
	}

	public void setMyImage() {
		if (images.size() > 0) {
			Image i = images.get(curSelImage);
			Bitmap tmp = Bitmap.createBitmap(i.getPhotoBitmap(), 1, 1, i.getPhotoBitmap().getWidth() - 1, i.getPhotoBitmap().getHeight() - 1);
			/*
			Canvas canvas = new Canvas(tmp);
			canvas.drawBitmap(tmp, 0, 0, null);

			if (i.getNumberOfFacesDetected() >= 1) {
				Paint facePaint = new Paint();
				facePaint.setColor(Color.GREEN);
				facePaint.setStyle(Paint.Style.STROKE);
				facePaint.setStrokeWidth(1); // changed from 3 to 1 in version 9.0.0

				Rect r = i.getRect();
				canvas.drawRect(
						r.getX(),
						r.getY(),
						r.getX() + r.getW(),
						r.getY() + r.getH(),
						facePaint
				);
			}

			// Draw frame to tell primary or secondary photo
			Paint primaryPaint = new Paint();
			primaryPaint.setColor(Color.WHITE);
			primaryPaint.setStyle(Paint.Style.STROKE);
			primaryPaint.setStrokeWidth(1);

			int size = 24;
			primaryPaint.setTextSize(size);
			String tx;
			if (curSelImage == 0) {
//				tx = Image.PRIMARY_IMAGE;
				tx = getResources().getString(R.string.primary_image);
			} else {
//				tx = Image.SECONDARY_IMAGE;
				tx = getResources().getString(R.string.secondary_image);
			}
			canvas.drawText(tx, 0, size, primaryPaint);
			*/
			ivPatientPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
		}
		// no image
		else {
			ivPatientPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionmark));
		}
	}

	private void Upload() {
		if (WebServer.isOnline(this)) {
			app.setOffline(false);

			String returnStr = "";
			returnStr = VerifyEntries();
			if (returnStr.isEmpty() == true) {
				BuildPatientData();
				new RestReportPersonAsyncTask().execute();
			} else {
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(returnStr)
						.setCancelable(false)
						.setTitle(getResources().getString(R.string.error))
						.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								return;
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
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

	private void OpenAddressBook() {
		// Check address box is empty.
		if (addressDatasource.countAllRecords() <= 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(getResources().getString(R.string.warning));
			builder.setMessage(getResources().getString(R.string.address_book_is_empty));
			builder.setCancelable(false);
			builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					dialog.dismiss();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		} else {
			// Start address.
			Intent i = new Intent("com.pl.reunite.ADDRESSLISTACTIVITY");
			startActivityForResult(i, ADDRESS_REQUEST);
		}
	}

	private void saveAsDraft() {
		patient.setStatusReport(Patient.DRAFT);
		if (images.size() > 0) {
			patient.setPhotoData(images.get(0).getPhotoData());
		} else {
			patient.setPhotoData("");
		}

		patient = datasource.createPatient(patient);
		if (patient != null) {
			for (int i = 0; i < images.size(); i++) {
				Image img = images.get(i);
				img.setPatientSerialId(patient.getSerialId());
				img.setSequence(i);
				img = imageDatasource.createImage(img, webServer);
				if (img != null) {
					images.set(i, img);
				}
			}
		}
		Toast.makeText(this, getResources().getString(R.string.file_is_saved), Toast.LENGTH_SHORT).show();
	}

	public RestReportResult restReportPerson() throws Exception{
		RestReportResult restResult = new RestReportResult(ReportActivity.this);

		WebServer ws = new WebServer(app.getWebServer());
		String url = ws.getRestEndpoint();
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("content-type", "application/json;  charset=utf-8");

		String tor = "";
		if (app.getAuthStatus() == true){
			tor = app.getToken();
		}
		else {
			tor = app.getTokenAnonymous();
		}
		JSONObject jo = patient.buildReportJSON(tor);
		String json = jo.toString();

		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
		bw.write(json);
		bw.flush();
		bw.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'POST' request to URL : " + url);
		System.out.println("Post json : " + json);
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
			restResult.setErrorCode(jsonResult.get("error").toString());
			if (restResult.getErrorCode().equalsIgnoreCase("0")){
				restResult.setUrl(jsonResult.get("uuid").toString());
			}
		}
		return restResult;
	}

	public void saveDataAfterReportPersonSuccseed() {
		patient.setPatientUuid(returnUrl);// record uuid
		patient.setStatusReport(Patient.SENT); // Update the report status

		// Save the new comment to the database
		if (images.size() > 0) {
			patient.setPhotoData(images.get(0).getPhotoData());
		} else {
			patient.setPhotoData("");
		}

		patient = datasource.createPatient(patient);
		if (patient != null) {
			for (int i = 0; i < images.size(); i++) {
				Image img = images.get(i);
				img.setPatientSerialId(patient.getSerialId());
				img.setSequence(i);
				img = imageDatasource.createImage(img, webServer);
				if (img != null) {
					images.set(i, img);
				}
			}
		}
		tvMessage.setText(getResources().getString(R.string.local_file_id) + " ID " + String.valueOf(patient.getSerialId()) + ", Sent");

		// Create address
		SaveToAddressBook();
		SaveToDefaults();
	}

	public void saveDataAfterReportPersonFailed() {
		patient.setStatusReport(Patient.OUTBOX);

		// Save the new comment to the database
		if (images.size() > 0) {
			patient.setPhotoData(images.get(0).getPhotoData());
		} else {
			patient.setPhotoData("");
		}

		patient = datasource.createPatient(patient);
		if (patient != null) {
			for (int i = 0; i < images.size(); i++) {
				Image img = images.get(i);
				img.setPatientSerialId(patient.getSerialId());
				img.setSequence(i);
				img = imageDatasource.createImage(img, webServer);
				if (img != null) {
					images.set(i, img);
				}
			}
		}
		/**
		 * modified on 3/7/2017
		 * version 7.2.10-beta
		 */
		String msg = getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()) + ", " + getResources().getString(R.string.is_not_sent);
		msg = msg + "\n" + getResources().getString(R.string.error) + ": " +  returnErrorMessage;
		tvMessage.setText(msg);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg)
				.setCancelable(false)
				.setTitle(getResources().getString(R.string.error))
				.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						return;
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void SaveToAddressBook() {
		// if is empty, no save
		if (patient.getStreet1().isEmpty() == true &&
				patient.getStreet2().isEmpty() == true &&
				patient.getCity().isEmpty() == true &&
				patient.getState().isEmpty() == true &&
				patient.getZip().isEmpty() == true &&
				patient.getCountry().isEmpty() == true) {
			return;
		}

		// Sign the value
		patientAddress.setStreet1(patient.getStreet1());
		patientAddress.setStreet2(patient.getStreet2());
		patientAddress.setCity(patient.getCity());
		patientAddress.setState(patient.getState());
		patientAddress.setZip(patient.getZip());
		patientAddress.setCountry(patient.getCountry());

		// If there is identical one
		if (addressDatasource.countAllRecords() <= 0) {
			patientAddress = addressDatasource.createAddress(patientAddress);
		} else if (patient.getCountry().isEmpty() == false && addressDatasource.countAllRecordsCountry(patientAddress.getCountry()) <= 0) {
			patientAddress = addressDatasource.createAddress(patientAddress);
		} else if (patient.getZip().isEmpty() == false && addressDatasource.countAllRecordsZip(patientAddress.getZip()) <= 0) {
			patientAddress = addressDatasource.createAddress(patientAddress);
		} else if (patient.getState().isEmpty() == false && addressDatasource.countAllRecordsState(patientAddress.getState()) <= 0) {
			patientAddress = addressDatasource.createAddress(patientAddress);
		} else if (patient.getCity().isEmpty() == false && addressDatasource.countAllRecordsCity(patientAddress.getCity()) <= 0) {
			patientAddress = addressDatasource.createAddress(patientAddress);
		} else if (patient.getStreet2().isEmpty() == false && addressDatasource.countAllRecordsStreet2(patientAddress.getStreet2()) <= 0) {
			patientAddress = addressDatasource.createAddress(patientAddress);
		} else if (patient.getStreet1().isEmpty() == false && addressDatasource.countAllRecordsStreet1(patientAddress.getStreet1()) <= 0) {
			patientAddress = addressDatasource.createAddress(patientAddress);
		}
	}

	protected void SaveToDefaults() {
		defaults.saveEventNamePreferences(patient.getEventName());
		defaults.saveEventShortNamePreferences(patient.getEventShortName());
		defaults.saveStreet1Preferences(etAddress1.getText().toString());
		defaults.saveStreet2Preferences(etAddress2.getText().toString());
		defaults.saveCityPreferences(etCity.getText().toString());
		defaults.saveStatePreferences(etState.getText().toString());
		defaults.saveZipPreferences(etZip.getText().toString());
		defaults.saveCountryPreferences(etCountry.getText().toString());
	}

	private void BuildPatientData() {
		// animal
		if (radioButtonAnimal.isChecked() == true){
			patient.setPa(Patient.ANIMAL);
			patient.setBuddy(etBuddy.getText().toString());
		}
		else {
			patient.setPa(Patient.PERSON);
			patient.setBuddy("");
		}

		if (images.size() > 0) {
			patient.setImages(images); // add images. multiple images
		}

//		patient.setGivengName(etGivenName.getText().toString().trim());
//		patient.setFamilyName(etFamilyName.getText().toString().trim());
		String fullName = etFullName.getText().toString().trim();
		if (fullName.isEmpty()){
			fullName = "unk";
		}
		patient.setFullName(fullName);

		String strYearsOld = textViewAge.getText().toString();
		if (strYearsOld.contentEquals(getResources().getString(R.string.unknown))){
			strYearsOld = "-1";
		}
		patient.setYearsOld(strYearsOld);
		// end

		String strGenderLong = tvSelectGender.getText().toString();
		String strGenderShort = Patient.convertGenderToShortForm(this, strGenderLong);
		patient.setGender(strGenderShort);

		// reverse back
		// To save, convert from long to short form
		String strStatusLong = tvSelectStatus.getText().toString();
		String strStatusShort = Patient.convertStatusToShortForm(this, strStatusLong);
		patient.setOptStatus(strStatusShort);

//		patient.setEventShortName(strEventShortName);
		patient.setEventName(tvSelectEvent.getText().toString());
		if (tvSelectEvent.getText().toString().equalsIgnoreCase(defaults.getEventNamePreferences()) == true) {
			patient.setEventShortName(defaults.getEventShortNamePreferences());
		}

		patient.setLastSeen(tvSelectLastSeenLocation.getText().toString());

		patient.setLatitude(String.valueOf(currentLatitude));
		patient.setLongitude(String.valueOf(currentLongitude));

		patient.setStreet1(etAddress1.getText().toString().trim());
		patient.setStreet2(etAddress2.getText().toString().trim());
		patient.setCity(etCity.getText().toString().trim());
		patient.setState(etState.getText().toString().trim());
		patient.setZip(etZip.getText().toString().trim());
		patient.setCountry(etCountry.getText().toString().trim());
		patient.setMoreDetails(etMoreDetails.getText().toString().trim());
		patient.setComments(patient.getMoreDetails()); // Version 32. Build 2

		if (patient.getEventName().equalsIgnoreCase("Unknown") == true) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getResources().getString(R.string.you_need_to_select_the_event))
					.setCancelable(false)
					.setTitle(getResources().getString(R.string.error))
					.setNegativeButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							return;
						}
					});
			AlertDialog alert = builder.create();
			alert.show();
		}
	}

	private String VerifyEntries() {
		String returnStr = "";

		/**
		 * rewrite age
		 */
		returnStr = verifyAge();
		if (returnStr.isEmpty() == false){
			return returnStr;
		}

		// Rule 6. Event cannot be unknown or empty.
		String strEvent = tvSelectEvent.getText().toString().trim();
		if (strEvent.isEmpty() == true || strEvent.equalsIgnoreCase("Unknown")) {
			returnStr = getResources().getString(R.string.error_in_event);
			return returnStr;
		}

		// Rule 7. gender cannot not be empty.
		String strSelGender = new String(tvSelectGender.getText().toString().trim());
		if (strSelGender.isEmpty() == true) {
			returnStr = getResources().getString(R.string.error_in_gender);
			return returnStr;
		}

		// Rule 8. gender cannot not be empty.
		String strStatus = new String(tvSelectStatus.getText().toString().trim());
		if (strStatus.isEmpty() == true) {
			returnStr = getResources().getString(R.string.error_in_status);
			return returnStr;
		}

		return returnStr;
	}

	public String verifyAge() {
		String strYearsOld = textViewAge.getText().toString();
		String returnStr = "";
		if (strYearsOld == null){
			returnStr = getResources().getString(R.string.age_is_missing);
			return returnStr;
		}
		if (strYearsOld.isEmpty() == true){
			returnStr = getResources().getString(R.string.age_is_missing);
			return returnStr;
		}
		return returnStr;
	}

	private void WhereToGetPhoto() {
		// Two ways to get the images: 1. table a picture by camera; 2. browser images from local
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.select));
		builder.setCancelable(false);

		builder.setSingleChoiceItems(ImageSourceItems, curSelImageSource, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				curSelImageSource = item;
				strCurSelImageSource = (String) ImageSourceItems[curSelImageSource];
			}
		});
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				strCurSelImageSource = (String) ImageSourceItems[curSelImageSource];
				if (curSelImageSource == 0) {
					new checkPermissionAsyncTask(RUN_CAMERA, ReportActivity.this).execute();
//					takePicture();
				} else if (curSelImageSource == 1) {
					pickPhoto();
				} else if (curSelImageSource == 2) {
					deleteCurrentImage();
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

	public void deleteCurrentImage() {
		// Need to add confirm dialogue box
		if (images.size() <= 0) {
			Toast.makeText(this, getResources().getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.do_you_want_to_delete_this_image))
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setCancelable(true)
				.setTitle(getResources().getString(R.string.warning))
				.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				})
				.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (images.size() > 0) {
							images.remove(curSelImage);
							if (curSelImage > 0) {
								curSelImage = curSelImage - 1;
							}
							patient.setImages(images);
							setMyImage();
						}
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	protected void pickPhoto() {
		Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, PICK_PHOTO_REQUEST);
	}

	// rewrite in version 9.3.0
	private void GetLocationFromGps() {
		currentLatitude = app.getLatitude();
		currentLongitude = app.getLongitude();

		String str1 = Double.toString(app.getLatitude());
		String str2 = Double.toString(app.getLongitude());

		if (str1.isEmpty() == false && str2.isEmpty() == false){
			tvSelectLastSeenLocation.setText(str1 + "," + str2);
			GetAddressFromGpsLocation(currentLatitude, currentLongitude);
		}
		else {
			tvSelectLastSeenLocation.setText("Undetected");
		}
	}

	// rewrotton as the class was Geocoder changed.
	// version 7.3.2. version code 7030200
	private void GetAddressFromGpsLocation(double latitude, double longitude) {
		Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
		try {
			List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
			if (addresses != null && !addresses.isEmpty()) {
				android.location.Address returnedAddress = addresses.get(0);

				StringBuilder strReturnedAddress = new StringBuilder("");

//				for (int i = 0; i < returnedAddress.getMaxAddressLineIndex() - 1; i++) {
				for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
					strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("\n");
				}
				if (!strReturnedAddress.toString().isEmpty()){
					String str = strReturnedAddress.toString();
					int endPoint = str.indexOf(",");
					if (endPoint > 0){
						str = str.substring(0, endPoint);
						etAddress1.setText(str);
					}
				}
				else {
					etAddress1.setText("");
				}
				etAddress2.setText("");
				etCity.setText(returnedAddress.getLocality());
				etZip.setText(returnedAddress.getPostalCode());
				etState.setText(returnedAddress.getAdminArea());
				etCountry.setText(returnedAddress.getCountryName());
			} else {
				etMoreDetails.setText(getResources().getString(R.string.no_record_is_found));
				Toast.makeText(this, etMoreDetails.toString(), Toast.LENGTH_SHORT).show();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, getResources().getString(R.string.let_gps_fill_your_address), Toast.LENGTH_SHORT).show();
		}
	}

	// Google Map View
	private void startGoogleMap() {
		currentLatitude = app.getLatitude();
		currentLongitude = app.getLongitude();

		String str1 = Double.toString(app.getLatitude());
		String str2 = Double.toString(app.getLongitude());

		String url = String.format(Locale.ENGLISH, "geo:%f,%f", currentLatitude, currentLongitude);
		tvLocationUrl.setText(url);

		if (str1.isEmpty() == false && str2.isEmpty() == false) {
			GetAddressFromGpsLocation(currentLatitude, currentLongitude);
			showGoogleMap(currentLatitude, currentLongitude);
		}
	}

	// Google Map View
	private void showGoogleMap(double latitude, double longitude) {
		String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		this.startActivity(intent);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == PICK_EVENT_REQUEST) {
			String eventname = data.getExtras().getString("eventname").trim();
			String eventshortname = data.getExtras().getString("eventshortname").trim();
			if (eventname.isEmpty() == false && eventshortname.isEmpty() == false) {
				patient.setEventName(eventname);
				patient.setEventShortName(eventshortname);
				tvSelectEvent.setText(patient.getEventName());
			}
		} else if (requestCode == ADDRESS_REQUEST) {
			String serialId = data.getExtras().getString("serialId");
			if (serialId.equalsIgnoreCase("0") == true) {
				return;
			}
			etAddress1.setText(data.getExtras().getString("street1"));
			etAddress2.setText(data.getExtras().getString("street2"));
			etCity.setText(data.getExtras().getString("city"));
			etState.setText(data.getExtras().getString("state"));
			etZip.setText(data.getExtras().getString("zip"));
			etCountry.setText(data.getExtras().getString("country"));
			tvSelectLastSeenLocation.setText(getResources().getString(R.string.let_gps_fill_your_address));
			Toast.makeText(this, getResources().getString(R.string.updated), Toast.LENGTH_SHORT).show();
		} else if (requestCode == PICK_PHOTO_REQUEST) {
			if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
				return;
			}

			Uri uri = data.getData();
			Bitmap bmpPhoto = Image.resizedBitmap(uri.toString(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false); // 9.0.

			//?img.DetectFaces()

			// Convert to data byte
			ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
			bmpPhoto.compress(CompressFormat.PNG, Image.COMPRESS_RATE, byteArryPhoto);
			byte[] photoData = byteArryPhoto.toByteArray();
			patient.setPhotoData(Base64.encodeToString(photoData, Base64.DEFAULT));

			Image img = new Image();
			img.setPhotoData(patient.getPhotoData());
			img.setPhotoBitmap(bmpPhoto);

			/**
			 * change to one image only
			 * version 9.2.10
			 */
			if (images.isEmpty()){
				images.add(img);
			}
			else {
				images.set(0, img);
			}
			patient.setImages(images);
			setMyImage();
		}
		else if (requestCode == TAKE_PICTURE_REQUEST) {
			if (resultCode == Activity.RESULT_CANCELED) {
				Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
				return;
			}

			Intent i = app.getCameraIntent();
			Uri uri = (Uri) i.getExtras().get(MediaStore.EXTRA_OUTPUT);
			Bitmap bmpPhoto = Image.resizedBitmap(uri.toString(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false); // 9.0.

			// Convert to data byte
			ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
			bmpPhoto.compress(CompressFormat.PNG, Image.COMPRESS_RATE, byteArryPhoto);
			byte[] photoData = byteArryPhoto.toByteArray();
			patient.setPhotoData(Base64.encodeToString(photoData, Base64.DEFAULT));

			/*
			 multiple images - start
			  */
			Image img = new Image();
			img.setPhotoData(patient.getPhotoData());
			img.setPhotoBitmap(bmpPhoto);

//			img.DetectFaces(); // detect faces

			/**
			 * change to one image only
			 * version 9.2.10
			 */
			if (images.isEmpty()){
				images.add(img);
			}
			else {
				images.set(0, img);
			}
			patient.setImages(images);
			setMyImage();
		}
		// place picker
		// added in version 7.2.10
		// start
		else if (requestCode == PLACE_PICKER_REQUEST) {
			if (resultCode == RESULT_OK) {
				Place place = PlacePicker.getPlace(data, this);
				currentLatitude = place.getLatLng().latitude;
				currentLongitude = place.getLatLng().longitude;
				GetAddressFromGpsLocation(currentLatitude, currentLongitude);
			}
		}
		// end
		else {
			MyMessageBox(getResources().getString(R.string.error), getResources().getString(R.string.not_succeed), getResources().getString(R.string.ok));
		}
	}

	private void SelectEvent() {
		Intent i = new Intent("com.pl.reunite.EVENTLISTFORREPORTACTIVITY");
		startActivityForResult(i, PICK_EVENT_REQUEST);
	}

	private void SelectStatus() {
		SelectStutsListBox();
	}

	private void SelectStutsListBox() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.status));
		builder.setCancelable(false);
		builder.setSingleChoiceItems(statusItems, nStatusItem, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				nCurSelStatusItem = item;
				strCurSelStatus = (String) statusItems[nCurSelStatusItem];
			}
		});
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				nStatusItem = nCurSelStatusItem;
				strStatus = strCurSelStatus;

				int color = Color.BLACK;

				/*
				int color = 0;
				switch (nCurSelStatusItem) {
					case 0: // missing
						strStatus = Patient.MISSING;
						color = Color.CYAN;
						break;
					case 1: // alive and well
						strStatus = Patient.ALIVE_AND_WELL;
						color = Color.GREEN;
						break;
					case 2: // Injured
						strStatus = Patient.INJURED;
						color = Color.YELLOW;
						break;
					case 3: // deceased
						strStatus = Patient.DECEASED;
						color = Color.LTGRAY;
						break;
					case 4: // Unknown
						strStatus = Patient.UNKNOWN;
						color = Color.WHITE;
						break;
					case 5: // found
						strStatus = Patient.FOUND;
						color = Color.rgb(255, 160, 122);
						break;
					default: // unknown
						strStatus = Patient.UNKNOWN;
						color = Color.WHITE;
						break;
				}*/


				tvSelectStatus.setTextColor(color);
				tvSelectStatus.setText(statusItems[nCurSelStatusItem]);

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

	private void SelectGender() {
		SelectGenderListBox();
	}

	private void SelectGenderListBox() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
		builder.setTitle(getResources().getString(R.string.select));
		builder.setCancelable(false);
		builder.setSingleChoiceItems(items, nGenderItem, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				nCurSelGenderItem = item;
				strCurSelGender = (String) items[nCurSelGenderItem];
			}
		});
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				nGenderItem = nCurSelGenderItem;
				strGender = strCurSelGender;
				tvSelectGender.setText(strGender);
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

	private void MyMessageBox(String title, String message, String buttonText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setCancelable(false)
				.setTitle(title)
				.setNegativeButton(buttonText, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	// Start a set of date functions.
	static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Date GetUTCdatetimeAsDate() {
		//note: doesn't check for null
		return StringDateToDate(GetUTCdatetimeAsString());
	}

	public static String GetUTCdatetimeAsString() {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		final String utcTime = sdf.format(new java.util.Date()) + " UTC";
		return utcTime;
	}

	public static Date StringDateToDate(String StrDate) {
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateToReturn;
	}

	@Override
	protected void onPause() {
		datasource.close();
		addressDatasource.close();
		imageDatasource.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		datasource.open();
		addressDatasource.open();
		imageDatasource.open();

		super.onResume();

		// tracker
		app.tracker().setScreenName(app.TRACK_REPORT + " (" + app.getLastEvent().getShortName() + ")");
		app.tracker().send(new HitBuilders.ScreenViewBuilder().build());
	}

	@Override
	public void onStart() {
		super.onStart();

		// place picker
		// added in version 7.2.10
		// start
		mGoogleApiClient.connect();
		// end

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW,
				"Report Page",
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				Uri.parse("android-app://com.pl.reunite/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		// place picker
		// added in version 7.2.10
		// start
		mGoogleApiClient.disconnect();
		//
		//

		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW,
				"Report Page",
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				Uri.parse("android-app://com.pl.reunite/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}

	private class RestReportPersonAsyncTask extends AsyncTask<Void, Integer, Void> {
		private RestReportResult restReportResult;

		//Before running code in separate thread
		@Override
		protected void onPreExecute() {
			restReportResult = new RestReportResult(ReportActivity.this);
			progressDialog = new ProgressDialog(ReportActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getResources().getString(R.string.reporting_missing_person_please_wait));
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}

		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			//Get the current thread's token
			synchronized (this) {
				try {
					restReportResult = restReportPerson();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		//Update the progress
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		//after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			//close the progress dialog
			progressDialog.dismiss();
			if (restReportResult.getErrorCode().equalsIgnoreCase("0")) {
				deleteCurPatientRecord(patient);
				saveDataAfterReportPersonSuccseed();

				// track the login event
				app.tracker().send(new HitBuilders.EventBuilder()
						.setCategory(patient.getEventShortName())
						.setAction(app.TRACK_REPORT)
						.setLabel(app.SUCCEED)
						.build());

				/**
				 * NCMED
				 * animal should not have warning. took out in version 7.3.6.0.1
				 */
				if (patient.getPa() == Patient.PERSON && progressAge > 0 && progressAge < 18 && app.getNcmecClaim() == ReUnite.NCMEC_CLAIM_SHOW){
					/**
					 * Display NCMEC information
					 */
					View ncmrcView = View.inflate(ReportActivity.this, R.layout.dialog_display_ncmec, null);
					CheckBox do_not_show = (CheckBox) ncmrcView.findViewById(R.id.checkBoxNotShowAgain);
					do_not_show.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
							if (isChecked == true){
								app.setNcmecClaim(ReUnite.NCMEC_CLAIM_NOT_SHOW);
							}
							else {
								app.setNcmecClaim(ReUnite.NCMEC_CLAIM_SHOW);
							}
						}
					});
					AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
					builder.setTitle(getResources().getString(R.string.warning))
							.setIcon(android.R.drawable.ic_dialog_alert)
							.setCancelable(true)
							.setView(ncmrcView)
							.setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
									GoBackToMainPage();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
				else {
					GoBackToMainPage();				}
			}
			else {
				deleteCurPatientRecord(patient);
				saveDataAfterReportPersonFailed();

				// track the report
				app.tracker().send(new HitBuilders.EventBuilder()
						.setCategory(patient.getEventShortName())
						.setAction(app.TRACK_REPORT)
						.setLabel(app.FAILED)
						.build());

				/**
				 * display error message
				 */
				AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
				String errorMsg = "";
				if (restReportResult.getErrorMessage().equalsIgnoreCase(getResources().getString(R.string.unknown))){
					errorMsg = restReportResult.getErrorCode() + ": " + getResources().getString(R.string.unknown);
				}
				else {
					errorMsg = restReportResult.getErrorCode();
				}
				builder.setMessage(errorMsg)
						.setIcon(android.R.drawable.ic_dialog_alert)
						.setCancelable(true)
						.setTitle(getResources().getString(R.string.error))
						.setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}

	// Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		if (app.getDeveloper() == false) {
			inflater.inflate(R.menu.report_menu, menu);
		} else {
			inflater.inflate(R.menu.report_menu_developer, menu);
		}
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
			case R.id.itemRandomMaleName:    // added in version 8.0.5
				getRandomMaleName();
				break;
			case R.id.itemRandomFemaleName:    // added in version 8.0.5
				getRandomFemaleName();
				break;
			case R.id.itemNotification:
				// sendNotification(this);
				break;
			case R.id.itemError:
				errorMessage();
				break;
		}
		return true;
	}

	private void errorMessage() {
		ErrorMessage er = new ErrorMessage(this);
		er.searchErrorMessage("6");
		Toast.makeText(this, er.searchErrorMessage("6"), Toast.LENGTH_SHORT).show();
	}

	// added in version 8.0.5
	private void getRandomMaleName() {
		PopularNames pn = new PopularNames(Locale.getDefault().getLanguage());
		String firstName = pn.getRandomBoyName();
		String lastName = pn.getRandomLastName();
		String fullName = firstName + " " + lastName;

//		etFamilyName.setText(lastName);
//		etGivenName.setText(firstName);
		etFullName.setText(fullName);

		nCurSelGenderItem = 0;
		strCurSelGender = (String) items[nCurSelGenderItem];
		tvSelectGender.setText(strCurSelGender.toString());
	}

	// added in version 8.0.5
	private void getRandomFemaleName() {
		PopularNames pn = new PopularNames(Locale.getDefault().getLanguage());
		String firstName = pn.getRandomGirlName();
		String lastName = pn.getRandomLastName();
		String fullName = firstName + " " + lastName;

//		etFamilyName.setText(lastName);
//		etGivenName.setText(firstName);
		etFullName.setText(fullName);

		nCurSelGenderItem = 1;
		strCurSelGender = (String) items[nCurSelGenderItem];
		tvSelectGender.setText(strCurSelGender.toString());
	}

	private void Tutorials() {
		Intent i = new Intent(ReportActivity.this, TutorialListActivity.class);
		startActivity(i);
	}

	private void Latency2() {
		Intent i = new Intent(ReportActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);
		startActivity(i);
	}

	private void GoBackToMainPage() {
		Intent i = new Intent(ReportActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		finish();
	}

	public class Defaults {
		private String eventName;
		private String eventShortName;
		private String street1;
		private String street2;
		private String city;
		private String state;
		private String zip;
		private String country;

		Defaults() {
			getEventNamePreferences();
			getEventShortNamePreferences();

			getStreet1Preferences();
			getStreet2Preferences();
			getCityPreferences();
			getStatePreferences();
			getZipPreferences();
			getCountryPreferences();
		}

		public String getEventName() {
			return eventName;
		}

		public String getEventSortName() {
			return eventShortName;
		}

		public String getStreet1() {
			return street1;
		}

		public String getStreet2() {
			return street2;
		}

		public String getCity() {
			return city;
		}

		public String getState() {
			return state;
		}

		public String getZip() {
			return zip;
		}

		public String getCountry() {
			return country;
		}

		public void reset() {
			eventName = "";
			eventShortName = "";
			saveEventNamePreferences(eventName);
			saveEventShortNamePreferences(eventShortName);

			street1 = "";
			street2 = "";
			city = "";
			state = "";
			zip = "";
			country = "";

			saveStreet1Preferences(street1);
			saveStreet2Preferences(street2);
			saveCityPreferences(city);
			saveStatePreferences(state);
			saveZipPreferences(zip);
			saveCountryPreferences(country);
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

		private void saveStreet1Preferences(String street1) {
			this.street1 = street1;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("street1", this.street1);
			editor.commit();
		}

		private void saveStreet2Preferences(String street2) {
			this.street2 = street2;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("street2", this.street2);
			editor.commit();
		}

		private void saveCityPreferences(String city) {
			this.city = city;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("city", this.city);
			editor.commit();
		}

		private void saveStatePreferences(String state) {
			this.state = state;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("state", this.state);
			editor.commit();
		}

		private void saveZipPreferences(String zip) {
			this.zip = zip;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("zip", this.zip);
			editor.commit();
		}

		private void saveCountryPreferences(String country) {
			this.country = country;
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("country", this.country);
			editor.commit();
		}

		private String getEventNamePreferences() {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.eventName = sharedPreferences.getString("eventName", "");
			return this.eventName;
		}

		private String getEventShortNamePreferences() {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.eventShortName = sharedPreferences.getString("eventShortName", "");
			return this.eventShortName;
		}

		private String getStreet1Preferences() {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.street1 = sharedPreferences.getString("street1", "");
			return this.street1;
		}

		private String getStreet2Preferences() {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.street2 = sharedPreferences.getString("street2", "");
			return this.street2;
		}

		private String getCityPreferences() {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.city = sharedPreferences.getString("city", "");
			return this.city;
		}

		private String getStatePreferences() {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.state = sharedPreferences.getString("state", "");
			return this.state;
		}

		private String getZipPreferences() {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.zip = sharedPreferences.getString("zip", "");
			return this.zip;
		}

		private String getCountryPreferences() {
			SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
			this.country = sharedPreferences.getString("country", "");
			return this.country;
		}
	}

	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getResources().getString(R.string.are_you_sure_you_want_to_quite_current_work_q));
		builder.setCancelable(false);
		builder.setTitle(getResources().getString(R.string.warning));
		builder.setIcon(android.R.drawable.ic_dialog_alert);
		builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ReportActivity.super.onBackPressed();
			}
		});
		builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	/**
	 * added for multiple languages
	 */
	// start
	public void setImageSourceItems() {
		ImageSourceItems = new CharSequence[3];
		for (int i = 0; i < 3; i++) {
			ImageSourceItems[i] = getImageSourceItems(i);
		}
	}

	public String getImageSourceItems(int index) {
		String result = "";
		switch (index) {
			case 0:
				result = getResources().getString(R.string.camera);
				break;
			case 1:
				result = getResources().getString(R.string.gallery);
				break;
			case 2:
				result = getResources().getString(R.string.delete);
				break;
		}
		return result;
	}

	public void setStatusItems() {
		statusItems = new CharSequence[6];
		for (int i = 0; i < 6; i++) {
			statusItems[i] = getStatusItems(i);
		}
	}

	public String getStatusItems(int index) {
		String result = "";
		switch (index) {
			case 0:
				result = getResources().getString(R.string.missing);
				break;
			case 1:
				result = getResources().getString(R.string.alive_and_well);
				break;
			case 2:
				result = getResources().getString(R.string.injured);
				break;
			case 3:
				result = getResources().getString(R.string.deceased);
				break;
			case 4:
				result = getResources().getString(R.string.unknown);
				break;
			case 5:
				result = getResources().getString(R.string.found);
				break;
			default:
				result = getResources().getString(R.string.unknown);
				break;
		}
		return result;
	}

	public void setItem() {
		items = new CharSequence[4];
		for (int i = 0; i < 4; i++) {
			items[i] = getItem(i);
		}
	}

	public String getItem(int index) {
		String result = "";
		switch (index) {
			case 0:
				result = getResources().getString(R.string.male);
				break;
			case 1:
				result = getResources().getString(R.string.female);
				break;
			case 2:
				result = getResources().getString(R.string.unknown);
				break;
			case 3:
				result = getResources().getString(R.string.complex);
				break;
			default:
				result = getResources().getString(R.string.unknown);
				break;
		}
		return result;
	}
	// multiple languages - end

	/**
	 * Permissions
	 * starting from SDK 23, need to enable permission in running time.
	 * two permissions are needed:
	 * fine location
	 * external
	 *
	 * @return
	 */

	private boolean checkPermission(int func, Context c) {
		int result;
		if (func == RUN_CAMERA){
			result = c.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		}
		else {
			result = c.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
		}
		if (result == PackageManager.PERMISSION_GRANTED) {
			return true;
		} else {
			return false;
		}
	}

	private class checkPermissionAsyncTask extends AsyncTask<Void, Integer, Void> {
		private boolean permission = false;
		private int func;
		private Context c;

		checkPermissionAsyncTask(int func, Context c){
			this.func = func;
			this.c = c;
		}
		//Before running code in separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(c);
			progressDialog.setMessage(getResources().getString(R.string.message_checking_permission_));
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}

		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			//Get the current thread's token
			synchronized (this) {
				permission = checkPermission(func, c);
			}
			return null;
		}

		//Update the progress
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		//after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			//close the progress dialog
			if (permission == false){
				new requestPermissionAsyncTask(func, c).execute();
			}
			else { // enabled
				if (func == RUN_CAMERA){
					takePicture();
				}
				else if (func == RUN_GPS){
					GetLocationFromGps();
				}
				else {
					startGoogleMap();
				}
			}
		}
	}

	private void requestPermission(int func) {
		if (func == RUN_CAMERA){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
			}
		}
		else { // RUN_GPS, RUN_MAP
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
				requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_REQUEST_CODE);
			}
		}
	}

	private class requestPermissionAsyncTask extends AsyncTask<Void, Integer, Void> {
		private int func;
		private Context c;

		requestPermissionAsyncTask(int func, Context c){
			this.func = func;
			this.c = c;
		}

		//Before running code in separate thread
		@Override
		protected void onPreExecute() {
			progressDialog = new ProgressDialog(c);
			progressDialog.setMessage(getResources().getString(R.string.message_requesting_permission_));
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}

		//The code to be executed in a background thread.
		@Override
		protected Void doInBackground(Void... params) {
			//Get the current thread's token
			synchronized (this) {
				requestPermission(func);
			}
			return null;
		}

		//Update the progress
		@Override
		protected void onProgressUpdate(Integer... values) {
		}

		//after executing the code in the thread
		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
		switch (requestCode) {
			case GPS_PERMISSION_REQUEST_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(getResources().getString(R.string.permission_is_granted_))
							.setCancelable(false)
							.setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(getResources().getString(R.string.permission_is_denied_))
							.setCancelable(false)
							.setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
				break;
			case CAMERA_PERMISSION_REQUEST_CODE:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(getResources().getString(R.string.permission_is_granted_))
							.setCancelable(false)
							.setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
					builder.setMessage(getResources().getString(R.string.permission_is_denied_))
							.setCancelable(false)
							.setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});
					AlertDialog alert = builder.create();
					alert.show();
				}
				break;
			default:
				break;
		}
	}
}