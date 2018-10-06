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
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.pl.reunite.Result.RestReportResult;
import com.pl.reunite.Result.Result;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.kobjects.base64.Base64;

 /**
 * Report activity, which is the tools provided for patient data.
 * Version 9.2.10:
 * remove face detection,
 * remove annotation
 * full name replace family and given names
 * one photo only, remove functions that support multiple images.
 * remove face detection
 * remove photo navigation functions: move left move right
 * remove capitation for multiple image description.
 */
public class ReportProceedActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "ReportProceedActivity";

    static final int PICK_EVENT_REQUEST = 1;
	static final int TAKE_PICTURE_REQUEST = 2;
	static final int ADDRESS_REQUEST = 3;
	static final int QUERY_MEDIA = 4;
	static final int PICK_PHOTO_REQUEST = 5;

	private static final int GPS_PERMISSION_REQUEST_CODE = 1;
	private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
	public static final int RUN_GPS = 100;
	public static final int RUN_MAP = 101;
	public static final int RUN_CAMERA = 102;

	ReUnite app;
    String webServer = "";
    String userName = "";
    String passWord = "";
    String nameSpace = "";
    String url = "";
	
	private CharSequence[] genderItems; // = {"Male", "Female", "Unknown", "Complex"};
	private CharSequence[] statusItems; // = {"Missing", "Alive and well", "Injured", "Deceased", "Unknown", "Found"};
	
	// define to select image source
	final String camera = "Camera";

	private CharSequence[] ImageSourceItems; // = {"camera", "gallery", "primary", "delete"};
    private List<Image> images;
    private List<String> deleteImages;
    private int curSelImage;

	final int selectCamera = 0;
	final int selectGallery = 1;
	private int curSelImageSource = selectCamera;
	private String strCurSelImageSource = camera;

	double currentLatitude = 0.0;
	double currentLongitude = 0.0;
	
	private String strGenderLong = Patient.UNKNOWN;
	private String strGenderShort = Patient.UNKNOWN_SHORT;
	private int nGenderItem = 2;
	private int nCurSelGenderItem = nGenderItem;
	private String strCurSelGender = strGenderLong;

	private String strStatusLong = Patient.UNKNOWN;
	private String strStatusShort = Patient.UNK;
	private int nStatusItem = 4;
	private int nCurSelStatusItem = nStatusItem;
	private String strCurSelStatus = strStatusLong;

	private String returnUrl = "";
	private String returnErrorCode = "";
	private String returnErrorMessage = "";

	private PatientsDataSource datasource;
	Patient patient;

	private AddressDataSource addressDatasource;
	PatientAddress patientAddress;

    private ImageDataSource imageDatasource;

    Button buCamera;
    Button buGallery;

	ImageView ivPatientPhoto;
//    TextView tvPrimarySecondary;
//    TextView tvImageIndex;
//    Button btPrimarySecondary;
//    Button btMoveLeft;
//    Button btMoveRight;

    Bitmap bmpPhoto;
	String patientPhotoString;
	String personXml;
	File filePatientPhoto;
		
//	EditText etGivenName;
//	EditText etFamilyName;
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
	TextView tvUuid;
	Button buDelete;
	Button buSaveAsDraft;
	Button buUpload;

	 /**
	  * animal, version 7.3.6
	  */
	 static int currentReport = Patient.PERSON;
	 LinearLayout linearLayoutBuddy;
	 EditText etBuddy;
	 LinearLayout llPerson;
	 LinearLayout llAnimal;

	 // place picker
	 // version 7.2.10
	 // start
	 int PLACE_PICKER_REQUEST = 1000;
	 private GoogleApiClient mGoogleApiClient;
	 // end

	 ProgressDialog progressDialog;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();
		userName = app.getUsername();
		passWord = app.getPassword();

		setContentView(R.layout.report_proceed);

        Initialize();
	}

	private void Initialize() {
		/**
		 * multiple languages
		 */
		setGenderItems();
		setStatusItems();
		setImageSourceItems();
		strCurSelImageSource = ImageSourceItems[0].toString();

		progressDialog = new ProgressDialog(ReportProceedActivity.this);

        buCamera = (Button) findViewById(R.id.buttonCamera);
        buGallery = (Button) findViewById(R.id.buttonGallery);

        ivPatientPhoto = (ImageView)findViewById(R.id.ivPatientPhoto);
        ivPatientPhoto.setImageResource(R.drawable.questionmark);

        images = new ArrayList<Image>();
        deleteImages = new ArrayList<String>();
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
		buSelectStatus = (Button) findViewById(R.id.buttonSelectStatus);
		tvSelectStatus = (TextView) findViewById(R.id.tvSelectStatus);
		
		buSelectEvent = (Button) findViewById(R.id.buttonSelectEvent);
		tvSelectEvent = (TextView) findViewById(R.id.tvEvent);
		
		buSelectLastSeenLocation = (Button) findViewById(R.id.buttonSelectLastSeenLocation);
		tvSelectLastSeenLocation = (TextView) findViewById(R.id.tvLastSeenLocation);

		// Google Map View
				// place picker
				// added in version 7.2.10
		buttonPlacePicker = (Button) findViewById(R.id.buttonPlacePicker);
        buGoogleMapView = (Button) findViewById(R.id.buttonGoogleMapView);
        tvLocationUrl = (TextView) findViewById(R.id.tvLocationUrl);

		buAddressBook = (Button) findViewById(R.id.buttonAddressBook);

		etAddress1 = (EditText)findViewById(R.id.etAddress1);
		etAddress2 = (EditText)findViewById(R.id.etAddress2);
		etCity = (EditText)findViewById(R.id.etCity);
		etState = (EditText)findViewById(R.id.etState);
		etZip = (EditText)findViewById(R.id.etZip);
		etCountry = (EditText) findViewById(R.id.etCountry);

		/**
		 * animal. version 7.3.6
		 */
		linearLayoutBuddy = (LinearLayout) findViewById(R.id.linearLayoutBuddy);
		etBuddy = (EditText) findViewById(R.id.etBuddy);
		llPerson = (LinearLayout)findViewById(R.id.linearLayoutPerson);
		llAnimal = (LinearLayout)findViewById(R.id.linearLayoutAnimal);

		etMoreDetails = (EditText) findViewById(R.id.editTextMoreDetails);
		
		tvMessage = (TextView)findViewById(R.id.tvMessage);
		buDelete = (Button) findViewById(R.id.buDelete);
		buSaveAsDraft = (Button) findViewById(R.id.buSaveAsDraft);
		buUpload = (Button) findViewById(R.id.buUpload);
		
		tvUuid = (TextView)findViewById(R.id.tvUuid);

		buCamera.setOnClickListener(this);
        buGallery.setOnClickListener(this);
		ivPatientPhoto.setOnClickListener(this);
		
		buSelectGender.setOnClickListener(this);
		buSelectStatus.setOnClickListener(this);
		
		buSelectEvent.setOnClickListener(this);

		buttonPlacePicker.setOnClickListener(this); // place picker, added in version 7.2.10
		buSelectLastSeenLocation.setOnClickListener(this);
        buGoogleMapView.setOnClickListener(this); // Google Map View

		buAddressBook.setOnClickListener(this);

		buDelete.setOnClickListener(this);
		buSaveAsDraft.setOnClickListener(this);
		buUpload.setOnClickListener(this);

//		registerForContextMenu(tvAgeDisplay);
		
		patient = new Patient(webServer);
		patientAddress = new PatientAddress();
		
		// Get serialId from previous intent
	    Intent sender = getIntent();
	    patient.setSerialId(Long.parseLong(sender.getExtras().getString("serialId")));
		
	    // Build datasource
        datasource = new PatientsDataSource(this);
        datasource.open(); 
        
        addressDatasource = new AddressDataSource(this);
        addressDatasource.open();

        imageDatasource = new ImageDataSource(this);
        imageDatasource.open();

        // Read the patient record
        patient = datasource.getPatient(patient.getSerialId());

        // get images
        images = imageDatasource.getAllImages(patient.getSerialId(), webServer);
        patient.setImages(images);
        if (!images.isEmpty()){
            // add bitmap
            for (int i = 0; i < images.size(); i++){
                Image img = images.get(i);
                Patient p = new Patient(webServer);
                p.setPhotoData(img.getPhotoData());
                p.decodePhoto();
                img.setPhotoBitmap(p.getPhoto());

                deleteImages.add(img.getImageUrl());
            }

            patient.setImages(images);
            patient.setDeleteImages(deleteImages);

            if (images.size()>0) {
                Image img = images.get(0);
                patient.setPhoto(img.getPhotoBitmap());
                patient.setPhotoData(img.getPhotoData());
            }
            curSelImage = 0;
        }

        if (patient.getPatientUuid().isEmpty() == true){// in case puuid is not saved.
        	patient.setStatusReport(Patient.DRAFT);
        }

		// Get all patient data from database
		if (patient.getPa() == Patient.PERSON){
			llPerson.setVisibility(View.VISIBLE);
			llAnimal.setVisibility(View.GONE);
		}
		else {
			llPerson.setVisibility(View.GONE);
			llAnimal.setVisibility(View.VISIBLE);
		}

        // Get all patient data from database
        LoadPatientRecord();

		setMyImage();
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

	private void LoadPatientRecord() {
		// Take photo. 
		if (patient.getPhoto() != null){
			ivPatientPhoto.setImageBitmap(patient.getPhoto());			
		}
		else {
			patient.decodePhoto();
			if (patient.getPhoto() != null) {
				ivPatientPhoto.setImageBitmap(patient.getPhoto());
			}
		}
		 
//		etGivenName.setText(patient.getGivengName());
//		etFamilyName.setText(patient.getFamilyName());
		String fullName = patient.getFullName();
		if (fullName.isEmpty() || fullName.equalsIgnoreCase("unk")){
			fullName = getResources().getString(R.string.unknown);
		}
		etFullName.setText(fullName);

		/**
		 * 	change to use seek bar for age input
		 */
		// start
		loadAge();
		// end

	    // more describe-able on gender
		strGenderShort = patient.getGender();
		strGenderLong = Patient.convertGenderToLongForm(this, strGenderShort);
		tvSelectGender.setText(strGenderLong);
		
	    // more describe-able on status
		strStatusShort = patient.getOptStatus();
		strStatusLong = Patient.convertStatusToLongForm(this, strStatusShort);
		int color = Patient.getColorFromStatusArray(this, strStatusShort);
		tvSelectStatus.setText(strStatusLong);
		tvSelectStatus.setTextColor(color);
		
		tvSelectEvent.setText(patient.getEventName());
		
		etAddress1.setText(patient.getStreet1());
		etAddress2.setText(patient.getStreet2());
		etCity.setText(patient.getCity());
		etState.setText(patient.getState());
		etZip.setText(patient.getZip());
		etCountry.setText(patient.getCountry());
		
		etMoreDetails.setText(patient.getComments());// version 3.2 build 3
		
		tvMessage.setText(getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()));
		
		// Added in build 3.
		String uuid = patient.getPatientUuid();
		if (uuid.isEmpty() == false){
			tvUuid.setText(getResources().getString(R.string.uuid) + ": " + uuid);
		}

		/**
		 * animal. version 7.3.6
		 */
		// this section is added for pet
		if (patient.getPa() == Patient.PERSON){
			etBuddy.setText("");
			linearLayoutBuddy.setVisibility(View.INVISIBLE);
		}
		else {
			etBuddy.setText(patient.getBuddy());
			linearLayoutBuddy.setVisibility(View.VISIBLE);
		}
	}

	private void loadAge() {
		String yearsOld = "";

		yearsOld = patient.getYearsOld();
		if (yearsOld.contentEquals("-1")){
			textViewAge.setText(getResources().getText(R.string.unknown));
		}
		else {
			textViewAge.setText(yearsOld);
		}
		int pos = Integer.parseInt(yearsOld);
		pos++;
		seekBarAge.setProgress(pos);
	}

	public void onClick(View v) {

		switch (v.getId()){
		case R.id.ivPatientPhoto:
			WhereToGetPhoto();
			break;
		case R.id.buttonCamera:
			new checkPermissionAsyncTask(RUN_CAMERA, this).execute();
//			takePicture();
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
//			GetLocationFromGps();
			break;
			case R.id.buttonPlacePicker:	// place picker, added in version 7.2.10
				placePicker();
				break;
			case R.id.buttonGoogleMapView: // Google Map View
			new checkPermissionAsyncTask(RUN_MAP, this).execute();
			// startGoogleMap();
            break;
		case R.id.buUpload:
			Upload();
			break;
		case R.id.buSaveAsDraft:
			BuildPatientData();
            deleteCurPatientRecord(patient);
			saveAsDraft();
            finish();
			break;
		case R.id.buDelete:
			DeletePatientData();
			break;
		case R.id.buttonAddressBook:
			OpenAddressBook();
			break;
		default:
            break;
        }
	}

	 private void Upload() {
		 if (WebServer.isOnline(this)) {
			 app.setOffline(false);

			 if (patient.getPatientUuid().isEmpty()) {
				 new RestReportPersonAsyncTask().execute();
			 } else {
				 new RestUploadAsyncTask().execute();
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


	/*
    public void setPrimary() {
        if (images.size() <= 0){
            Toast.makeText(this, getResources().getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
            return;
        }
        if (curSelImage == 0){
            return;
        }

        selectCurrentPhotoAsPrimary();
        Toast.makeText(this, getResources().getString(R.string.done), Toast.LENGTH_SHORT).show();
    }

    protected void selectCurrentPhotoAsPrimary() {
        Image imgCurSel = images.get(curSelImage);
        Image imgCurPri = images.get(0);
        images.set(0, imgCurSel);
        images.set(curSelImage, imgCurPri);
        patient.setImages(images);

        curSelImage = 0;
        setMyImage();
    }
    */

    public void setMyImage() {
        if (images.size() > 0) {
            Image i = images.get(curSelImage);

            Bitmap tmp = Bitmap.createBitmap(i.getPhotoBitmap(), 1, 1, i.getPhotoBitmap().getWidth()-1, i.getPhotoBitmap().getHeight()-1);

            ivPatientPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
        }
        // no image
        else {
            ivPatientPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionmark));
        }
    }

    private void OpenAddressBook() {
		// Check address box is empty.
		if (addressDatasource.countAllRecords() <= 0) {
			AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
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
		}
		else {
			// Start address.
			Intent i = new Intent("com.pl.reunite.ADDRESSLISTACTIVITY");
			startActivityForResult(i, ADDRESS_REQUEST);
		}
	}

	private void DeletePatientData() {
		AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
		builder.setMessage(getResources().getString(R.string.you_are_about_to_delete_this_file))
			   .setTitle(getResources().getString(R.string.are_you_sure_q))
		       .setCancelable(false)
		       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                DeleteCurrentPatientFile();
		                Toast.makeText(ReportProceedActivity.this, getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();
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

	protected void DeleteCurrentPatientFile() {
		if (patient.getSerialId() < 0) {
			return;
		}
		datasource.deletePatient(patient);
	}

    // changed for multiple images
	private void saveAsDraft() {
        patient.setStatusReport(Patient.DRAFT);
        if (images.size() > 0){
            patient.setPhotoData(images.get(0).getPhotoData());
        }
        else {
            patient.setPhotoData("");
        }

        patient = datasource.createPatient(patient);
        if (patient != null){
            for (int i = 0; i < images.size(); i++){
                Image img = images.get(i);
                img.setPatientSerialId(patient.getSerialId());
                img.setSequence(i);
                img = imageDatasource.createImage(img, webServer);
                if (img != null){
                    images.set(i, img);
                }
            }
        }
        Toast.makeText(ReportProceedActivity.this, getResources().getString(R.string.file_is_saved), Toast.LENGTH_SHORT).show();
    }

    // re-written in version 4.0.0
	public boolean GetPersonPermissions(){
        if (app.getGroupName().equalsIgnoreCase(ReUnite.ADMINISTRATOR)){
            return true;
        }
        if (app.getGroupName().equalsIgnoreCase(ReUnite.USER)){
            return true;
        }
		return false;
	}

	private void BuildPatientData() {
		// animal
		if (patient.getPa() == Patient.ANIMAL){
			patient.setBuddy(etBuddy.getText().toString());
		}
		else {
			patient.setBuddy("");
		}

//		patient.setGivengName(etGivenName.getText().toString().trim());
//		patient.setFamilyName(etFamilyName.getText().toString().trim());
		String fullName = etFullName.getText().toString().trim();
		if (fullName.isEmpty() || fullName.equalsIgnoreCase(getResources().getString(R.string.unknown).toString())){
			fullName = "unk";
		}
		patient.setFullName(fullName);

		/**
		 * rewrite age
		 */
		buildAge();

		strGenderLong = tvSelectGender.getText().toString();
		strGenderShort = Patient.convertGenderToShortForm(this, strGenderLong);
		patient.setGender(strGenderShort);
		
		// To save, convert from long to short form
		strStatusLong = tvSelectStatus.getText().toString();
		strStatusShort = Patient.convertStatusToShortForm(this, strStatusLong);
		patient.setOptStatus(strStatusShort);
		
//		patient.setEventShortName(strEventShortName);
		patient.setEventName(tvSelectEvent.getText().toString());

		patient.setLastSeen(tvSelectLastSeenLocation.getText().toString());

		// add the following 2 lines in version 7.2.8 code 7020800
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
		
		if (patient.getEventName().equalsIgnoreCase("Unknown") == true){
			MyMessageBox(getResources().getString(R.string.error), getResources().getString(R.string.you_need_to_select_the_event), getResources().getString(R.string.ok));
			return;
		}	
	}

	private void WritePersonXml() {
	    // build request object
        try {
            //Creating an personXML Document
            //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //Creating the XML tree            
            //create the root element and add it to the document
            Node person = doc.createElement("person");
            doc.appendChild(person);

            // Node xmlFormat
            Node xmlFormat = doc.createElement("xmlFormat");
            xmlFormat.setTextContent("REUNITE4");
            person.appendChild(xmlFormat);

            // Node dateTimeSent
            Node dateTimeSent = doc.createElement("dateTimeSent");
            patient.setDateTimeSent(GetUTCdatetimeAsString());
            dateTimeSent.setTextContent(patient.getDateTimeSent());
            person.appendChild(dateTimeSent);
            
            // Node dateTimeSent
            Node expiryDate = doc.createElement("expiryDate");
            expiryDate.setTextContent("");
            person.appendChild(expiryDate);
      
            // Node eventShortname
            Node eventShortname = doc.createElement("eventShortname");
            eventShortname.setTextContent(patient.getEventShortName());
            person.appendChild(eventShortname);

            // Node givenName
            Node givenName = doc.createElement("givenName");
            givenName.setTextContent(patient.getGivengName());
            person.appendChild(givenName);

            // Node familyName
            Node familyName = doc.createElement("familyName");
            familyName.setTextContent(patient.getFamilyName());
            person.appendChild(familyName);

            // Node gender
            Node gender = doc.createElement("gender");
            gender.setTextContent(patient.getGender());
            person.appendChild(gender);
            
            // Node minAge
            Node minAge = doc.createElement("minAge");
            minAge.setTextContent(patient.getMinAge());
            person.appendChild(minAge);

            // Node maxAge
            Node maxAge = doc.createElement("maxAge");
            maxAge.setTextContent(patient.getMaxAge());
            person.appendChild(maxAge);

            // Node estimatedAge
            Node estimatedAge = doc.createElement("estimatedAge");
            estimatedAge.setTextContent(patient.getYearsOld());
            person.appendChild(estimatedAge);

            // Node status
            Node status = doc.createElement("status");
            status.setTextContent(patient.getOptStatus());
            person.appendChild(status);

            // Node location
            Node location = doc.createElement("location");
            
            // Node street1
            Node street1 = doc.createElement("street1");
            street1.setTextContent(patient.getStreet1());
            location.appendChild(street1);
            
            // Node street2
            Node street2 = doc.createElement("street2");
            street2.setTextContent(patient.getStreet2());
            location.appendChild(street2);
            
            // Node neighborhood
            Node neighborhood = doc.createElement("neighborhood");
            neighborhood.setTextContent("");
            location.appendChild(neighborhood);
            
            // Node city
            Node city = doc.createElement("city");
            city.setTextContent(patient.getCity());
            location.appendChild(city);
            
            // Node region
            Node region = doc.createElement("region");
            region.setTextContent(patient.getState());
            location.appendChild(region);

            // Node postalCode
            Node postalCode = doc.createElement("postalCode");
            postalCode.setTextContent(patient.getZip());
            location.appendChild(postalCode);

            // Node country
            Node country = doc.createElement("country");
            country.setTextContent(patient.getCountry());
            location.appendChild(country);

            // Node gps
            Node gps = doc.createElement("gps");
            
            // Node lat
            Node lat = doc.createElement("lat");
            lat.setTextContent(patient.getLatitude());
            gps.appendChild(lat);

            // Node lon
            Node lon = doc.createElement("lon");
            lon.setTextContent(patient.getLongitude());
            gps.appendChild(lon);

            // Colose gps
            location.appendChild(gps);
            
            // Close location
            person.appendChild(location);
            
            // Node commnet. Added version 3.2 build 2
            // Using "note" instead of "comment"!!!
            Node note = doc.createElement("note");
            note.setTextContent(patient.getComments());
            person.appendChild(note);
                                    
            // Node photo
            Node photos = doc.createElement("photos");
            
            Node photo = doc.createElement("photo");
            // Node primary
            Node primary = doc.createElement("primary");
            primary.setTextContent("1");
            photo.appendChild(primary);

            // Node primary
            Node data = doc.createElement("data");
            data.setTextContent(patient.getPhotoData());          
            photo.appendChild(data);
            
            // Node Tags
            Node tags = doc.createElement("tags");
            
            // Node tag
            Node tag = doc.createElement("tag");
            
            // Node x
            Node x = doc.createElement("x");
            x.setTextContent("4");
            tag.appendChild(x);
            
            // Node y
            Node y = doc.createElement("y");
            y.setTextContent("4");
            tag.appendChild(y);

            // Node w
            Node w = doc.createElement("w");
            w.setTextContent("4");
            tag.appendChild(w);

            // Node h
            Node h = doc.createElement("h");
            h.setTextContent("4");
            tag.appendChild(h);

            // Node text
            Node text = doc.createElement("text");
            text.setTextContent("photo 2");
            tag.appendChild(text);

            tags.appendChild(tag);
            
            // Node tag
            tag = doc.createElement("tag");
            
            // Node x
            x = doc.createElement("x");
            x.setTextContent("0");
            tag.appendChild(x);
            
            // Node y
            y = doc.createElement("y");
            y.setTextContent("0");
            tag.appendChild(y);

            // Node w
            w = doc.createElement("w");
            w.setTextContent("32");
            tag.appendChild(w);

            // Node h
            h = doc.createElement("h");
            h.setTextContent("32");
            tag.appendChild(h);

            // Node text
            text = doc.createElement("text");
            text.setTextContent("photo 2");
            tag.appendChild(text);

            tags.appendChild(tag);

//            photo.appendChild(tags); // Take off tags
            
            photos.appendChild(photo);
            
            // Close photo
            person.appendChild(photos);
            // End of person node
            
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            personXml = sw.toString();

        } catch (Exception e) {
            System.out.println(e);
        }
	}

	private void ReWritePersonXml(boolean deleteImage) {
	    // build request object
        try {
            //Creating an personXML Document
            //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //Creating the XML tree            
            //create the root element and add it to the document
            Node person = doc.createElement("person");
            doc.appendChild(person);

            // Node xmlFormat
            Node xmlFormat = doc.createElement("xmlFormat");
            xmlFormat.setTextContent("REUNITE4");
            person.appendChild(xmlFormat);

            // Node dateTimeSent
            Node dateTimeSent = doc.createElement("dateTimeSent");
            patient.setDateTimeSent(GetUTCdatetimeAsString());
            dateTimeSent.setTextContent(patient.getDateTimeSent());
            person.appendChild(dateTimeSent);
            
            // Node dateTimeSent
            Node expiryDate = doc.createElement("expiryDate");
            expiryDate.setTextContent("");
            person.appendChild(expiryDate);
      
            // Node eventShortname
            Node eventShortname = doc.createElement("eventShortname");
            eventShortname.setTextContent(patient.getEventShortName());
            person.appendChild(eventShortname);

            // Node givenName
            Node givenName = doc.createElement("givenName");
            givenName.setTextContent(patient.getGivengName());
            person.appendChild(givenName);

            // Node familyName
            Node familyName = doc.createElement("familyName");
            familyName.setTextContent(patient.getFamilyName());
            person.appendChild(familyName);

            // Node gender
            Node gender = doc.createElement("gender");
            gender.setTextContent(patient.getGender());
            person.appendChild(gender);
            
            // Node minAge
            Node minAge = doc.createElement("minAge");
            minAge.setTextContent(patient.getMinAge());
            person.appendChild(minAge);

            // Node maxAge
            Node maxAge = doc.createElement("maxAge");
            maxAge.setTextContent(patient.getMaxAge());
            person.appendChild(maxAge);

            // Node estimatedAge
            Node estimatedAge = doc.createElement("estimatedAge");
            estimatedAge.setTextContent(patient.getYearsOld());
            person.appendChild(estimatedAge);

            // Node status
            Node status = doc.createElement("status");
            status.setTextContent(patient.getOptStatus());
            person.appendChild(status);

            // Node location
            Node location = doc.createElement("location");
            
            // Node street1
            Node street1 = doc.createElement("street1");
            street1.setTextContent(patient.getStreet1());
            location.appendChild(street1);
            
            // Node street2
            Node street2 = doc.createElement("street2");
            street2.setTextContent(patient.getStreet2());
            location.appendChild(street2);
            
            // Node neighborhood
            Node neighborhood = doc.createElement("neighborhood");
            neighborhood.setTextContent("");
            location.appendChild(neighborhood);
            
            // Node city
            Node city = doc.createElement("city");
            city.setTextContent(patient.getCity());
            location.appendChild(city);
            
            // Node region
            Node region = doc.createElement("region");
            region.setTextContent(patient.getState());
            location.appendChild(region);

            // Node postalCode
            Node postalCode = doc.createElement("postalCode");
            postalCode.setTextContent(patient.getZip());
            location.appendChild(postalCode);

            // Node country
            Node country = doc.createElement("country");
            country.setTextContent(patient.getCountry());
            location.appendChild(country);

            // Node gps
            Node gps = doc.createElement("gps");
            
            // Node lat
            Node lat = doc.createElement("lat");
            lat.setTextContent(patient.getLatitude());
            gps.appendChild(lat);

            // Node lon
            Node lon = doc.createElement("lon");
            lon.setTextContent(patient.getLongitude());
            gps.appendChild(lon);

            // Colose gps
            location.appendChild(gps);
            
            // Close location
            person.appendChild(location);

            // Node comment. Added version 3.2 build 2
            // Using "note" instead of "comment"!!!
            Node note = doc.createElement("note");
            note.setTextContent(patient.getComments());
            person.appendChild(note);
                                    
            // Delete image 
            if (deleteImage == true){
            	if (patient.getImageUrl().isEmpty() == false){
            		Node deletePhotos = doc.createElement("deletePhotos");
            		Node photo4Delete = doc.createElement("photo");
            		photo4Delete.setTextContent(patient.getImageUrl());
            		deletePhotos.appendChild(photo4Delete);
            		person.appendChild(deletePhotos);
            	}
            }
            else {
            	// Node photo
            	Node photos = doc.createElement("photos");
            	
            	Node photo = doc.createElement("photo");
            	// Node primary
            	Node primary = doc.createElement("primary");
            	primary.setTextContent("1"); 
            	photo.appendChild(primary);

            	// Node primary
            	Node data = doc.createElement("data");
            	data.setTextContent(patient.getPhotoData());         
            	photo.appendChild(data);
            
            	// Node Tags
            	Node tags = doc.createElement("tags");
            
            	// Node tag
            	Node tag = doc.createElement("tag");
            
            	// Node x
            	Node x = doc.createElement("x");
            	x.setTextContent("4");
            	tag.appendChild(x);
            
            	// Node y
            	Node y = doc.createElement("y");
            	y.setTextContent("4");
            	tag.appendChild(y);

            	// Node w
            	Node w = doc.createElement("w");
            	w.setTextContent("4");
            	tag.appendChild(w);

            	// Node h
            	Node h = doc.createElement("h");
            	h.setTextContent("4");
            	tag.appendChild(h);

            	// Node text
            	Node text = doc.createElement("text");
            	text.setTextContent("photo 2");
            	tag.appendChild(text);

            	tags.appendChild(tag);
            
            	// Node tag
            	tag = doc.createElement("tag");
            
            	// Node x
            	x = doc.createElement("x");
            	x.setTextContent("0");
            	tag.appendChild(x);
            
            	// Node y
            	y = doc.createElement("y");
            	y.setTextContent("0");
            	tag.appendChild(y);

            	// Node w
            	w = doc.createElement("w");
            	w.setTextContent("32");
            	tag.appendChild(w);

            	// Node h
            	h = doc.createElement("h");
            	h.setTextContent("32");
            	tag.appendChild(h);

            	// Node text
            	text = doc.createElement("text");
            	text.setTextContent("photo 2");
            	tag.appendChild(text);

            	tags.appendChild(tag);

//            	photo.appendChild(tags);	// Take off tags
            
            	photos.appendChild(photo);
            
            	// Close photo
            	person.appendChild(photos);
            }            
            // End of person node
            
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            personXml = sw.toString();

        } catch (Exception e) {
            System.out.println(e);
        }
	}

	private void WritePersonXmlDeletePhoto() {
	    // build request object
        try {
            //Creating an personXML Document
            //We need a Document
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            //Creating the XML tree            
            //create the root element and add it to the document
            Node person = doc.createElement("person");
            doc.appendChild(person);

            // Node xmlFormat
            Node xmlFormat = doc.createElement("xmlFormat");
            xmlFormat.setTextContent("REUNITE4");
            person.appendChild(xmlFormat);

            // Node dateTimeSent
            Node dateTimeSent = doc.createElement("dateTimeSent");
            patient.setDateTimeSent(GetUTCdatetimeAsString());
            dateTimeSent.setTextContent(patient.getDateTimeSent());
            person.appendChild(dateTimeSent);
            
            // Node dateTimeSent
            Node expiryDate = doc.createElement("expiryDate");
            expiryDate.setTextContent("");
            person.appendChild(expiryDate);
      
            // Node eventShortname
            Node eventShortname = doc.createElement("eventShortname");
            eventShortname.setTextContent(patient.getEventShortName());
            person.appendChild(eventShortname);

            // Node givenName
            Node givenName = doc.createElement("givenName");
            givenName.setTextContent(patient.getGivengName());
            person.appendChild(givenName);

            // Node familyName
            Node familyName = doc.createElement("familyName");
            familyName.setTextContent(patient.getFamilyName());
            person.appendChild(familyName);

            // Node gender
            Node gender = doc.createElement("gender");
            gender.setTextContent(patient.getGender());
            person.appendChild(gender);
            
            // Node minAge
            Node minAge = doc.createElement("minAge");
            minAge.setTextContent(patient.getMinAge());
            person.appendChild(minAge);

            // Node maxAge
            Node maxAge = doc.createElement("maxAge");
            maxAge.setTextContent(patient.getMaxAge());
            person.appendChild(maxAge);

            // Node estimatedAge
            Node estimatedAge = doc.createElement("estimatedAge");
            estimatedAge.setTextContent(patient.getYearsOld());
            person.appendChild(estimatedAge);

            // Node status
            Node status = doc.createElement("status");
            status.setTextContent(patient.getOptStatus());
            person.appendChild(status);

            // changes are made in version 34
            // Delete image
            ArrayList<String> imgToDeleteArr = new ArrayList<String>();
            imgToDeleteArr = patient.getImageToDeleteArr();
            if (!imgToDeleteArr.isEmpty()){
                for (int i = 0; i < imgToDeleteArr.size(); i++) {
                    Node deletePhotos = doc.createElement("deletePhotos");
                    Node photo4Delete = doc.createElement("photo");
                    photo4Delete.setTextContent(imgToDeleteArr.get(i));
                    deletePhotos.appendChild(photo4Delete);
                    person.appendChild(deletePhotos);
                }
            }
            // End of person node
            
            //set up a transformer
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");

            //create string from xml tree
            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource source = new DOMSource(doc);
            trans.transform(source, result);
            personXml = sw.toString();

        } catch (Exception e) {
            System.out.println(e);
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
		if (strEvent.isEmpty() == true || strEvent.equalsIgnoreCase("Unknown")){
			returnStr = getResources().getString(R.string.error_in_event);
			return returnStr;
		}
		
		// Rule 7. gender cannot not be empty.
		String strSelGender = new String(tvSelectGender.getText().toString().trim());
		if (strSelGender.isEmpty() == true){
			returnStr = getResources().getString(R.string.error_in_gender);
			return returnStr;
		}

		// Rule 8. gender cannot not be empty.
		String strStatus = new String(tvSelectStatus.getText().toString().trim());
		if (strStatus.isEmpty() == true){
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
		AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
		builder.setTitle(getResources().getString(R.string.select));
		builder.setCancelable(false);
		
		builder.setSingleChoiceItems(ImageSourceItems, curSelImageSource, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item){
				curSelImageSource = item;
				strCurSelImageSource = (String) ImageSourceItems[curSelImageSource];
			}				
		});
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				strCurSelImageSource = (String) ImageSourceItems[curSelImageSource];
                if (curSelImageSource == 0){
					new checkPermissionAsyncTask(RUN_CAMERA, ReportProceedActivity.this).execute();
//					takePicture();
                }
                else if (curSelImageSource == 1){
                    pickPhoto();
                }
                else if (curSelImageSource == 2){
                    deleteCurrentImage();
                }
//				Toast.makeText(ReportActivity.this, strCurSelImageSource, Toast.LENGTH_SHORT).show();
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

    public void deleteCurrentImage(){
        // Need to add confirm dialogue box
        if (images.size() <= 0){
            Toast.makeText(ReportProceedActivity.this, getResources().getString(R.string.no_photo), Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
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
                            if (curSelImage > 0){
                                curSelImage = curSelImage - 1;
                            }
                            if (images.isEmpty()){
                                patient.setPhotoData("");
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


	protected void pickPhoto() {
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, PICK_PHOTO_REQUEST);
	}

	 // rewrite in version 9.3.0
	 private void GetLocationFromGps() {
		 currentLatitude = app.getLatitude();
		 currentLongitude = app.getLongitude();

		 String str1 = Double.toString(app.getLatitude());
		 String str2 = Double.toString(app.getLongitude());

		 tvSelectLastSeenLocation.setText(str1 + "," + str2);

		 if (str1.isEmpty() == false && str2.isEmpty() == false){
			 GetAddressFromGpsLocation(currentLatitude, currentLongitude);
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
    private void startGoogleMap(){
		currentLatitude = app.getLatitude();
		currentLongitude = app.getLongitude();

		String str1 = Double.toString(app.getLatitude());
		String str2 = Double.toString(app.getLongitude());

		String url = String.format(Locale.ENGLISH, "geo:%f,%f", currentLatitude, currentLongitude);
        tvLocationUrl.setText(url);

        if (str1.isEmpty() == false && str2.isEmpty() == false){
            GetAddressFromGpsLocation(currentLatitude, currentLongitude);
            showGoogleMap(currentLatitude, currentLongitude);
        }
    }

    // Google Map View
    private void showGoogleMap(double latitude, double longitude){
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        this.startActivity(intent);
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == PICK_EVENT_REQUEST){
			String eventname = data.getExtras().getString("eventname").trim();
			String eventshortname = data.getExtras().getString("eventshortname").trim();
			if (eventname.isEmpty() == false && eventshortname.isEmpty() == false){
				patient.setEventName(eventname);
				patient.setEventShortName(eventshortname);
				tvSelectEvent.setText(patient.getEventName());
			}
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

			Image img = new Image();
			img.setPhotoData(patient.getPhotoData());
			img.setPhotoBitmap(bmpPhoto);

			curSelImage = images.size();
			images.add(curSelImage, img);
			patient.setImages(images);
			setMyImage();
		}
		else if (requestCode == ADDRESS_REQUEST){
			String serialId = data.getExtras().getString("serialId");
			if (serialId.equalsIgnoreCase("0") == true){
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
		}
		else if (requestCode == QUERY_MEDIA){
			if (resultCode == 0) {// Cancel
				return;
			}
			Bundle extras = data.getExtras();
			String fileFullName = (String)extras.get("fileFullName");
		      
	        Bitmap bmpPhoto = Shrinkmethod(fileFullName, Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT);
			ivPatientPhoto.setImageBitmap(bmpPhoto);
			
			// Convert to data byte
			ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();  
			bmpPhoto.compress(CompressFormat.PNG, Image.COMPRESS_RATE, byteArryPhoto);
			byte[] photoData = byteArryPhoto.toByteArray(); 
			
			patient.setPhotoData(Base64.encodeToString(photoData, Base64.DEFAULT));

			// multiple images - start
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
		else if (requestCode == PICK_PHOTO_REQUEST){
			if (resultCode == Activity.RESULT_CANCELED){
				Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
				return;
			}
			Uri uri = data.getData();
			Bitmap bmpPhoto = Image.resizedBitmap(uri.toString(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false); // 9.0.

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
		else{
			MyMessageBox(getResources().getString(R.string.error), getResources().getString(R.string.no_data_returned), getResources().getString(R.string.ok));
		}
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
	
	private void SelectEvent() {

		Intent i = new Intent("com.pl.reunite.EVENTLISTFORREPORTACTIVITY");
		startActivityForResult(i, PICK_EVENT_REQUEST);	
	}

	private void SelectStatus() {
        SelectStatusListBox();
	}

	private void SelectStatusListBox() {
		nStatusItem = Patient.getIndexFromStatusShortArray(strStatusShort);
		AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
		builder.setTitle(getResources().getString(R.string.status));
		builder.setCancelable(false);
		builder.setSingleChoiceItems(statusItems, nStatusItem, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item){
				nCurSelStatusItem = item;
				strCurSelStatus = (String) statusItems[nCurSelStatusItem];
			}
		});
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				nStatusItem = nCurSelStatusItem;
				strStatusLong = strCurSelStatus;
				strStatusShort = Patient.convertStatusToShortForm(ReportProceedActivity.this, strStatusLong);
				int color = Patient.getColorFromStatusArray(ReportProceedActivity.this, strStatusShort);
				tvSelectStatus.setText(strStatusLong);
				tvSelectStatus.setTextColor(color);
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
		nGenderItem = Patient.getIndexFromGenderArray(strGenderShort);
		AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
		builder.setTitle(getResources().getString(R.string.select));
		builder.setCancelable(false);
		builder.setSingleChoiceItems(genderItems, nGenderItem, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item){
				nCurSelGenderItem = item;
				strCurSelGender = (String) genderItems[nCurSelGenderItem];
			}				
		});
		builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				nGenderItem = nCurSelGenderItem;
				strGenderLong = strCurSelGender;
				strGenderShort = Patient.convertGenderToShortForm(ReportProceedActivity.this, strGenderLong);
				tvSelectGender.setText(strGenderLong);
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
	public static Date GetUTCdatetimeAsDate()
	{
	    //note: doesn't check for null
	    return StringDateToDate(GetUTCdatetimeAsString());
	}

	public static String GetUTCdatetimeAsString()
	{
	    final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
	    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	    final String utcTime = sdf.format(new java.util.Date()) + " UTC";
	    return utcTime;
	}

	public static Date StringDateToDate(String StrDate)
	{
	    Date dateToReturn = null;
	    SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
	    try
	    {
	    	dateToReturn = (Date)dateFormat.parse(StrDate);
	    }
	    catch (java.text.ParseException e)
	    {
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
		// bug fixed here.
		// version 7.3.4.00
		if (app.getCurSelPatient() != null) {
			app.tracker().setScreenName(app.TRACK_UPDATE + " (" + app.getCurSelPatient().getEventShortName() + ")");
		}
		else if (app.getLastEvent() != null){
			app.tracker().setScreenName(app.TRACK_UPDATE + " (" + app.getLastEvent().getShortName() + ")");
		}
		else {
			app.tracker().setScreenName(app.TRACK_UPDATE + " (" + getResources().getString(R.string.unknown) + ")");
		}
		app.tracker().send(new HitBuilders.ScreenViewBuilder().build());
	}

    // Menu sections
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
	    	super.onCreateOptionsMenu(menu);
	        MenuInflater inflater = getMenuInflater();
            if (app.getDeveloper() == false){
                inflater.inflate(R.menu.report_menu, menu);
            }
            else {
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
//					sendNotification(this);
					break;
            }
            return true;
    	}

	// added in version 8.0.5
    private void getRandomMaleName() {
        PopularNames pn = new PopularNames();
        String firstName = pn.getRandomBoyName();
        String lastName = pn.getRandomLastName();
		String fullName = firstName + " " + lastName;

//        etFamilyName.setText(lastName);
//		etGivenName.setText(firstName);
		etFullName.setText(fullName);

        nCurSelGenderItem = 0;
        strCurSelGender = (String) genderItems[nCurSelGenderItem];
        tvSelectGender.setText(strCurSelGender.toString());
    }

    // added in version 8.0.5
    private void getRandomFemaleName() {
        PopularNames pn = new PopularNames();
        String firstName = pn.getRandomGirlName();
        String lastName = pn.getRandomLastName();
		String fullName = firstName + " " + lastName;

//        etFamilyName.setText(lastName);
//        etGivenName.setText(firstName);
		etFullName.setText(fullName);

        nCurSelGenderItem = 1;
        strCurSelGender = (String) genderItems[nCurSelGenderItem];
        tvSelectGender.setText(strCurSelGender.toString());
    }

		private void Tutorials() {
    		Intent i = new Intent(ReportProceedActivity.this, TutorialListActivity.class);
    		startActivity(i);	
		}

		private void Latency2() {
			Intent i = new Intent(ReportProceedActivity.this, LatencyActivity.class);
			i.putExtra("webServer", webServer);    			
			startActivity(i);	
		}
	    
		private void GoBackToMainPage() {
			Intent i = new Intent(ReportProceedActivity.this, ReUniteActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(i);
			finish();
		}


    public void saveDataAfterReportPersonSuccseed(){
        // To prevent duplicate, add the following codes.
        // If the patient data is existed already, remove it
        // Is the date can be found in database?
        // If yes, remove it.
        if (patient.getPatientUuid().isEmpty()){
            Log.i(TAG, "There is a record existed already: " + patient.getPatientUuid().toString());
        }
        else {
            Log.i(TAG, "Current record is a new record.");
        }
        // Other wise go ahead.
        patient.setPatientUuid(returnUrl);// record uuid
        patient.setStatusReport(Patient.SENT); // Update the report status

        // Save the new comment to the database
        if (images.size() > 0){
            patient.setPhotoData(images.get(0).getPhotoData());
        }
        else {
            patient.setPhotoData("");
        }
//        patient = datasource.createPatient(patient);
        datasource.updatePatient(patient);
        if (patient != null){
            for (int i = 0; i < images.size(); i++){
                Image img = images.get(i);
                img.setPatientSerialId(patient.getSerialId());
                img.setSequence(i);
                img = imageDatasource.createImage(img, webServer);
                if (img != null){
                    images.set(i, img);
                }
            }
        }
        tvMessage.setText(getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()) + ", Sent");

        // Create address
        SaveToAddressBook();
        GoBackToMainPage();
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

    public void saveDataAfterReportPersonFailed(){
        patient.setStatusReport(Patient.OUTBOX);

        // Save the new comment to the database
        if (images.size() > 0){
            patient.setPhotoData(images.get(0).getPhotoData());
        }
        else {
            patient.setPhotoData("");
        }

        patient = datasource.createPatient(patient);
        if (patient != null){
            for (int i = 0; i < images.size(); i++){
                Image img = images.get(i);
                img.setPatientSerialId(patient.getSerialId());
                img.setSequence(i);
                img = imageDatasource.createImage(img, webServer);
                if (img != null){
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

    public void saveDataToOutbox(){
        patient.setStatusReport(Patient.OUTBOX);

        // Save the new comment to the database
        if (images.size() > 0){
            patient.setPhotoData(images.get(0).getPhotoData());
        }
        else {
            patient.setPhotoData("");
        }

        datasource.updatePatient(patient);

        imageDatasource.deleteAllImages(patient.getSerialId());
        if (patient != null){
            for (int i = 0; i < images.size(); i++){
                Image img = images.get(i);
                img.setPatientSerialId(patient.getSerialId());
                img.setSequence(i);
                img = imageDatasource.createImage(img, webServer);
                if (img != null){
                    images.set(i, img);
                }
            }
        }
        tvMessage.setText(getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()) + " " + getResources().getString(R.string.saved));
    }

	public RestReportResult restReportPerson() throws Exception{
		RestReportResult restResult = new RestReportResult(ReportProceedActivity.this);

		WebServer ws = new WebServer(app.getWebServer());
		String url = ws.getRestEndpoint();
		URL obj = new URL(url);
		HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

		con.setRequestMethod("POST");
		con.setDoOutput(true);
		con.setRequestProperty("content-type", "application/json;  charset=utf-8");

		JSONObject jo = patient.buildReportJSON(app.getToken());
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

	private class RestReportPersonAsyncTask extends AsyncTask<Void, Integer, Void>
	{
		private RestReportResult reportResult;
		private String errorCode;
		private String errorMessage;

		private String BuildPatientData() {
			// animal
			if (patient.getPa() == Patient.ANIMAL){
				patient.setBuddy(etBuddy.getText().toString());
			}
			else {
				patient.setBuddy("");
			}

//			patient.setGivengName(etGivenName.getText().toString().trim());
//			patient.setFamilyName(etFamilyName.getText().toString().trim());

			String fullName = etFullName.getText().toString().trim();
			if (fullName.isEmpty() || fullName.equalsIgnoreCase("unk")){
				fullName = getResources().getString(R.string.unknown);
			}
			patient.setFamilyName(fullName);

			/**
			 * rewrite the age
			 */
			buildAge();

			strGenderLong = tvSelectGender.getText().toString();
			strGenderShort = Patient.convertGenderToShortForm(ReportProceedActivity.this, strGenderLong);
			patient.setGender(strGenderShort);

			// reverse back
			// To save, convert from long to short form
			strStatusLong = tvSelectStatus.getText().toString();
			strStatusShort = Patient.convertStatusToShortForm(ReportProceedActivity.this, strStatusLong);
			patient.setOptStatus(strStatusShort);

			patient.setEventName(tvSelectEvent.getText().toString());

			patient.setLastSeen(tvSelectLastSeenLocation.getText().toString());

			// add the following 2 lines in version 7.2.8 code 7020800
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

			if (patient.getEventName().equalsIgnoreCase("Unknown") == true){
				errorMessage = getResources().getString(R.string.you_need_to_select_the_event);
				errorCode = "-1";
			}

			return errorMessage;
		}

		//Before running code in separate thread
		@Override
		protected void onPreExecute()
		{
			reportResult = new RestReportResult(ReportProceedActivity.this);
			progressDialog = new ProgressDialog(ReportProceedActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getResources().getString(R.string.reporting_missing_person_please_wait));
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
				errorMessage = VerifyEntries();
				if (errorMessage.isEmpty()){
					errorCode = "0";
					errorMessage = BuildPatientData();
					if (errorMessage.isEmpty()) {
						errorCode = "0";
//						patient.toXML(GetUTCdatetimeAsString());
						try {
							reportResult = restReportPerson();
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(ReportProceedActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					}
					else {
						reportResult.setErrorMessage(errorMessage);
						reportResult.setErrorCode(Result.MY_ERROR_CODE);
					}
				}
				else {
					reportResult.setErrorMessage(errorMessage);
					reportResult.setErrorCode(Result.MY_ERROR_CODE);
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
			if (reportResult.getErrorCode().equalsIgnoreCase("0")){
				deleteCurPatientRecord(patient);
				saveAsSent(patient);

				tvMessage.setText(getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()) + " " + getResources().getString(R.string.is_sent));
				Toast.makeText(ReportProceedActivity.this, getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()) + " " + getResources().getString(R.string.is_sent), Toast.LENGTH_SHORT).show();
				ReportProceedActivity.this.finish();
				GoBackToMainPage();
			}
			else if (reportResult.getErrorCode().equalsIgnoreCase(Result.MY_ERROR_CODE)){
				AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
				builder.setMessage(reportResult.getErrorMessage())
						.setCancelable(false)
						.setTitle(getResources().getString(R.string.error))
						.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
				builder.setMessage(reportResult.getErrorMessage())
						.setCancelable(false)
						.setTitle(getResources().getString(R.string.error))
						.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								// Delete the old file in DB
								deleteCurPatientRecord(patient);
								saveDataAfterReportPersonFailed();
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}
	}

	private class RestUploadAsyncTask extends AsyncTask<Void, Integer, Void>{
		ProgressDialog progressDialog;
		private String errorCode;
		private String errorMessage;

		public RestUploadAsyncTask() {
			super();
			errorCode = "";
			errorMessage = "";
		}

		public void restReReportPerson(boolean bDeleteImage) throws Exception{
			RestReportResult restResult = new RestReportResult(ReportProceedActivity.this);

			WebServer ws = new WebServer(app.getWebServer());
			String url = ws.getRestEndpoint();
			URL obj = new URL(url);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setRequestProperty("content-type", "application/json;  charset=utf-8");

			JSONObject jo = patient.buildReviseJSON(app.getToken());
			String json = jo.toString();

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
			bw.write(json);
			bw.flush();
			bw.close();

			int responseCode = con.getResponseCode();
			System.out.println("calling revise");
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
					restResult.setErrorMessage("");
				}
				else {
					restResult.searchErrorMessage();
				}
			}
			errorCode = restResult.getErrorCode();
			errorMessage = restResult.getErrorMessage();
		}

		private String BuildPatientData() {
			// animal
			if (patient.getPa() == Patient.ANIMAL){
				patient.setBuddy(etBuddy.getText().toString());
			}
			else {
				patient.setBuddy("");
			}

//			patient.setGivengName(etGivenName.getText().toString().trim());
//			patient.setFamilyName(etFamilyName.getText().toString().trim());

			String fullName = etFullName.getText().toString().trim();
			if (fullName.isEmpty() || fullName.equalsIgnoreCase("unk")){
				fullName = getResources().getString(R.string.unknown);
			}
			patient.setFamilyName(fullName);

			/**
			 * rewrite age section
			 */
			buildAge();

			strGenderLong = tvSelectGender.getText().toString();
			strGenderShort = Patient.convertGenderToShortForm(ReportProceedActivity.this, strGenderLong);
			patient.setGender(strGenderShort);

			// reverse back
			// To save, convert from long to short form
			strStatusLong = tvSelectStatus.getText().toString();
			strStatusShort = Patient.convertStatusToShortForm(ReportProceedActivity.this, strStatusLong);
			patient.setOptStatus(strStatusShort);

			patient.setEventName(tvSelectEvent.getText().toString());

			patient.setLastSeen(tvSelectLastSeenLocation.getText().toString());

			// add the following 2 lines in version 7.2.8 code 7020800
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

			if (patient.getEventName().equalsIgnoreCase("Unknown") == true){
				errorMessage = getResources().getString(R.string.you_need_to_select_the_event);
				errorCode = "-1";
			}

			return errorMessage;
		}


		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			progressDialog = new ProgressDialog(ReportProceedActivity.this);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage(getResources().getString(R.string.updating_the_persons_information_please_wait_dots));
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			progressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			synchronized (this)
			{
				errorMessage = VerifyEntries();
				if (errorMessage.isEmpty()){
					errorCode = "0";
					errorMessage = BuildPatientData();
					if (errorMessage.isEmpty()){
						errorCode = "0";

						WebServer ws = new WebServer(app.getWebServer()); // Add the argument in. Modified in version 7.1.3
						ws.setTokenStatus(app.getTokenStatus());
						ws.setTokenAnonymous(app.getTokenAnonymous());
						ws.setToken(app.getToken());
						ws.setSearchCountOnly(false);
						ws.setCurPageStart(0);
						ws.setQuery("");
//						patient.toXML(GetUTCdatetimeAsString());
						try {
							restReReportPerson(false);
						} catch (Exception e) {
							e.printStackTrace();
							Toast.makeText(ReportProceedActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
						}
					}
				}
				else {
					errorCode = "-1";
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);

			progressDialog.dismiss();

			if (errorCode.equalsIgnoreCase("0") == true){
				deleteCurPatientRecord(patient);
				saveAsSent(patient);

				tvMessage.setText(getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()) + " " + getResources().getString(R.string.is_sent));
				Toast.makeText(ReportProceedActivity.this, getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()) + " " + getResources().getString(R.string.is_sent), Toast.LENGTH_SHORT).show();;
				ReportProceedActivity.this.finish();
				GoBackToMainPage();
			}
			else {
				AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
				builder.setMessage(errorMessage)
						.setCancelable(false)
						.setTitle(getResources().getString(R.string.error))
						.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								// Delete the old file in DB
								Patient p = new Patient(patient, webServer);
								datasource.deletePatient(p);

								patient.setStatusReport(Patient.OUTBOX);

								// Save the new comment to the database
								patient = datasource.createPatient(patient);

								tvMessage.setText(getResources().getString(R.string.local_file_id) + " " + String.valueOf(patient.getSerialId()) + ". is not sent");
							}
						});
				AlertDialog alert = builder.create();
				alert.show();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected void onCancelled(Void aVoid) {
			super.onCancelled(aVoid);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}
	}

	private void buildAge() {
		/**
		 * get estimated age
		 */
		String strYearsOld = textViewAge.getText().toString();
		if (strYearsOld.contentEquals(getResources().getString(R.string.unknown))){
			strYearsOld = "-1";
		}
		patient.setYearsOld(strYearsOld);
		// end

	}

	private void deleteCurPatientRecord(Patient p){
        imageDatasource.deleteAllImages(p.getSerialId());
        datasource.deletePatient(p);
    }

    private void saveAsSent(Patient p) {
        patient.setStatusReport(Patient.SENT);
        if (images.size() > 0){
            patient.setPhotoData(images.get(0).getPhotoData());
        }
        else {
            patient.setPhotoData("");
        }

        patient = datasource.createPatient(patient);
        if (patient != null){
            for (int i = 0; i < images.size(); i++){
                Image img = images.get(i);
                img.setPatientSerialId(patient.getSerialId());
                img.setSequence(i);
                img = imageDatasource.createImage(img, webServer);
                if (img != null){
                    images.set(i, img);
                }
            }
        }
        Toast.makeText(ReportProceedActivity.this, getResources().getString(R.string.is_saved), Toast.LENGTH_SHORT).show();
    }

	/**
	 * added for multiple languages
	 */
	// start
	public void setImageSourceItems(){
		ImageSourceItems = new CharSequence[3];
		for (int i = 0; i < 3; i++) {
			ImageSourceItems[i] = getImageSourceItems(i);
		}
	}

	public String getImageSourceItems(int index){
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

	public void setStatusItems(){
		statusItems = new CharSequence[6];
		for (int i = 0; i < 6; i++) {
			statusItems[i] = Patient.convertStatusToLongForm(this, Patient.statusItemsShortForm[i].toString());
		}
	}

	public void setGenderItems(){
		genderItems = new CharSequence[4];
		for (int i = 0; i < 4; i++) {
			genderItems[i] = Patient.convertGenderToLongForm(this, Patient.genderItemsShortForm[i].toString());
		}
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
					AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
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
					AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
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
					AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
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
					AlertDialog.Builder builder = new AlertDialog.Builder(ReportProceedActivity.this);
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
