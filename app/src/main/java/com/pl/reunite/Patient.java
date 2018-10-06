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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//import org.kobjects.base64.Base64;

public class Patient {
	private static final String METHOD_NAME_SEARCH_COMPLETE = "searchComplete";

    public static final String SENT = "Sent";
	public static final String OUTBOX = "Outbox";
	public static final String DRAFT = "Draft";
    public static final String SAVED = "Saved";
    public static final String SAVED_FROM_SEARCH = "Saved from Search";
	public static final String CACHE = "Cache";
	public static final String TEST = "Test";
	
	public static final String MALE = "Male";
	public static final String FEMALE = "Female";
	public static final String UNKNOWN = "Unknown";
	public static final String COMPLEX = "Complex";

	public static final String MALE_SHORT = "mal";
	public static final String FEMALE_SHORT = "fml";
	public static final String UNKNOWN_SHORT = "unk";
	public static final String COMPLEX_SHORT = "cpx";

	public static CharSequence[] genderItemsShortForm;

	public static final String MISSING = "Missing";
	public static final String ALIVE_AND_WELL = "Alive and well";
	public static final String INJURED = "Injured";
	public static final String DECEASED = "Deceased";
	public static final String FOUND = "Found";

	public static final String MIS = "mis";
	public static final String ALI = "ali";
	public static final String INJ = "inj";
	public static final String DEC = "dec";
	public static final String UNK = "unk";
	public static final String FND = "fnd";

	// add pet
	public static final int PERSON = 0;
	public static final int ANIMAL = 1;
	public static final int BOTH = 2;

	public static CharSequence[] statusItemsShortForm;

	public static final int MAX_AGE = 150;

	public static int PHOTO_SIZE = 600;
	public static int PHOTO_WIDTH = 500;
	public static int PHOTO_HEIGHT = 500;

    String returnString;
	String errorCode = "";
	String errorMessage = "";

    private String personXml;
    public void setPersonXml(String personXml){this.personXml = personXml;}
    public String getPersonXml(){return personXml;}

	public Patient(String webServer) {
		serialId = 0;
		moreDetails = "";
		dateTimeSent = "";
		latitude = "";
		longitude = "";
		street1 = "";
		street2 = "";
		city = "";
		state = "";
		zip = "";
		country = "";
		eventName = "";
		eventShortName = "";
		patientUuid = "";
		encodedUuid = "";
		fullName = "";
		givengName = "";
		familyName = "";
		optStatus = "";
		imageUrl = "";
		imageUrlForFatch = "";
        imageToDeleteArr = new ArrayList<String>();
//		imageUrlFull = "";
//		imageUrlFullForFatch = "";
		imageWidth = "";
		imageHeight = "";
        images.clear();
		yearsOld = "";
		minAge = "";
		maxAge = "";
		id = "";
		statusSahanaUpdated = "";
		statusTriage = "";
		statusReport = "";
		peds = "";
		orgName = "";
		lastSeen = "";
		comments = "";
		gender = "";
		hospitalIcon = "";
		massCasualtyId = "";
		photoData = "";
		photo = null;
		statusPhotoDownload = false;
		
		this.webServer = webServer;

        personXml = "";

		genderItemsShortForm = new CharSequence[] {MALE_SHORT, FEMALE_SHORT, UNKNOWN_SHORT, COMPLEX_SHORT};
		statusItemsShortForm = new CharSequence[] {MIS, ALI, INJ, DEC, UNK, FND};

		// add pat
		pa = PERSON;
		buddy = "";
	}
	
	Patient(Patient p, String webServer) {
		this.serialId = p.getSerialId();
		this.moreDetails = p.getMoreDetails();
		this.dateTimeSent = p.getDateTimeSent();
		this.latitude = p.getLatitude();
		this.longitude = p.getLongitude();
		this.street1 = p.getStreet1();
		this.street2 = p.getStreet2();
		this.city = p.getCity();
		this.state = p.getState();
		this.zip = p.getZip();
		this.country = p.getCountry();
		this.eventName = p.getEventName();
		this.eventShortName = p.getEventShortName();
		this.patientUuid = p.getPatientUuid();
		this.encodedUuid = p.getEncodedUuid();
		this.fullName = p.getFullName();
		this.givengName = p.getGivengName();
		this.familyName = p.getFamilyName();
		this.optStatus = p.getOptStatus();
		this.setImageUrl(p.getImageUrl(), webServer);
//		this.setImageUrlFull(p.getImageUrlFull());
        this.setImageToDeleteArr(p.getImageToDeleteArr());
		this.imageWidth = p.getImageWidth();
		this.imageHeight = p.getImageHeight();
        this.images = p.getImages();
		this.yearsOld = p.getYearsOld();
		this.minAge = p.getMinAge();
		this.maxAge = p.getMaxAge();
		this.id = p.getId();
		this.statusSahanaUpdated = p.getStatusSahanaUpdated();
		this.statusTriage = p.getStatusTriage();
		this.statusReport = p.getStatusReport();
		this.peds = p.getPeds();
		this.orgName = p.getOrgName();
		this.lastSeen = p.getLastSeen();;
		this.comments = p.getComments();
		this.gender = p.getGender();
		this.hospitalIcon = p.getHospitalIcon();
		this.massCasualtyId = p.getMassCasualtyId();
		this.photoData = p.getPhotoData();
		this.photo = p.getPhoto();
		this.statusPhotoDownload = p.getStatusPhotoDownload();

		this.webServer = webServer;

        this.personXml = p.getPersonXml();

		genderItemsShortForm = new CharSequence[] {MALE_SHORT, FEMALE_SHORT, UNKNOWN_SHORT, COMPLEX_SHORT};
		statusItemsShortForm = new CharSequence[] {MIS, ALI, INJ, DEC, UNK, FND};

		// add pat
		this.pa = p.getPa();
		this.buddy = p.getBuddy();
	}
	
	String webServer = "";
	public String getWebServer() {
		return webServer;
	}
	public void setWebServer(String webServer) {
		this.webServer = webServer;
	}
	
	public void decodePhoto(){
		if (photo != null){
			return;
		}
		if (photoData.length() <= 0){
			return;
		}
//        byte[] data = Base64.decode(photoData);
        byte[] data = Base64.decode(photoData, Base64.DEFAULT);
		if (data.length > 0) {
			photo = BitmapFactory.decodeByteArray(data, 0, data.length);
		}
	}

    public Bitmap decodePhoto(String photoData){
//        byte[] data = Base64.decode(photoData);
        byte[] data = Base64.decode(photoData, Base64.DEFAULT);
        if (data.length > 0) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        return null;
    }
	
	public void encodePhoto(){
		ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();  
		photo.compress(CompressFormat.PNG, Image.COMPRESS_RATE, byteArryPhoto);
		byte[] b = byteArryPhoto.toByteArray();
//        this.photoData = Base64.encode(b);
		this.photoData = Base64.encodeToString(b, Base64.DEFAULT);

    }
	
	private String moreDetails;
	public String getMoreDetails() {
		return moreDetails;
	}
	public void setMoreDetails(String moreDetails) {
		this.moreDetails = moreDetails;
	}
	
	private String dateTimeSent;
	public String getDateTimeSent() {
		return dateTimeSent;
	}
	public void setDateTimeSent(String dateTimeSent) {
		this.dateTimeSent = dateTimeSent;
	}

	private String latitude;
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	private String longitude;
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	private String street1;
	public String getStreet1() {
		return street1;
	}
	public void setStreet1(String street1) {
		this.street1 = street1;
	}

	private String street2;
	public String getStreet2() {
		return street2;
	}
	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	private String city;
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}

	private String state;
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}

	private String zip;
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}

	private String country;
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	
	private String eventName;
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	private String eventShortName;
	public String getEventShortName() {
		return eventShortName;
	}
	public void setEventShortName(String eventShortName) {
		this.eventShortName = eventShortName;
	}

	private String patientUuid;
	public String getPatientUuid() {
		return patientUuid;
	}
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	
	private String encodedUuid;
	public String getEncodedUuid() {
		return encodedUuid;
	}
	public void setEncodedUuid(String encodedUuid) {
		this.encodedUuid = encodedUuid;
	}
	
	private String fullName;
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	private String givengName;
	public String getGivengName() {
		return givengName;
	}
	public void setGivengName(String givengName) {
		this.givengName = givengName;
	}

	private String familyName;
	public String getFamilyName() {
		return familyName;
	}
	public void setFamilyName(String familyName) {
		this.familyName = familyName;
	}

	private String optStatus;
	public String getOptStatus() {
		return optStatus;
	}
	public void setOptStatus(String optStatus) {
		this.optStatus = optStatus;
	}

	// put together: imageUrl and ImageUrlForFatch
	private String imageUrl;
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl, String webServer) {
		this.imageUrl = imageUrl;
        if (this.imageUrl.isEmpty()){
            this.imageUrlForFatch = "";
            return;
        }
        if (webServer.equalsIgnoreCase(WebServer.PL_NAME) == true){
            this.imageUrlForFatch = WebServer.WEB_SERVER_PL + this.imageUrl;
        }
        else if (webServer.equalsIgnoreCase(WebServer.PL_NAME_STAGE) == true){
			this.imageUrlForFatch = WebServer.WEB_SERVER_PL_STAGE + this.imageUrl;
		}
        else {
            return;
        }
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
        if (this.imageUrl.isEmpty()){
            this.imageUrlForFatch = "";
            return;
        }
        if (webServer.equalsIgnoreCase(WebServer.PL_NAME) == true){
            this.imageUrlForFatch = WebServer.WEB_SERVER_PL + this.imageUrl;
        }
        else if (webServer.equalsIgnoreCase(WebServer.PL_NAME_STAGE) == true){
            this.imageUrlForFatch = WebServer.WEB_SERVER_PL_STAGE + this.imageUrl;
        }
        else {
            return;
        }
	}

	private String imageUrlForFatch;
	public String getImageUrlForFatch() {
		return imageUrlForFatch;
	}
	public void setImageUrlForFatch(String imageUrlForFatch) {
		this.imageUrlForFatch = imageUrlForFatch;
	}

    private ArrayList<String>imageToDeleteArr;
    public void setImageToDeleteArr (ArrayList<String> imageToDeleteArr){this.imageToDeleteArr = imageToDeleteArr;}
    public ArrayList<String> getImageToDeleteArr(){return imageToDeleteArr;}
	
	private String imageWidth;
	public String getImageWidth() {
		return imageWidth;
	}
	public void setImageWidth(String imageWidth) {
		this.imageWidth = imageWidth;
	}
	
	private String imageHeight;
	public String getImageHeight() {
		return imageHeight;
	}
	public void setImageHeight(String imageHeight) {
		this.imageHeight = imageHeight;
	}

    private List<Image> images = new ArrayList<Image>();
    public void setImages (List<Image> images){this.images = images;}
    public List<Image> getImages(){return images;}

    private List<String> deleteImages = new ArrayList<String>();
    public void setDeleteImages(List<String> deleteImages){
        this.deleteImages = deleteImages;
    }
    public List<String> getDeleteImages(){return deleteImages;}

    private String yearsOld;
	public String getYearsOld() {
		return yearsOld;
	}
	public void setYearsOld(String yearsOld) {
		this.yearsOld = yearsOld;
	}

	private String minAge;
	public String getMinAge() {
		return minAge;
	}
	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}

	private String maxAge;
	public String getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(String maxAge) {
		this.maxAge = maxAge;
	}

	private long serialId;
	public long getSerialId() {
		return serialId;
	}
	public void setSerialId(long serialId) {
		this.serialId = serialId;
	}
	
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	private String statusSahanaUpdated;
	public String getStatusSahanaUpdated() {
		return statusSahanaUpdated;
	}
	public void setStatusSahanaUpdated(String statusSahanaUpdated) {
		this.statusSahanaUpdated = statusSahanaUpdated;
	}

	
	private String statusTriage;
	public String getStatusTriage() {
		return statusTriage;
	}
	public void setStatusTriage(String statusTriage) {
		this.statusTriage = statusTriage;
	}
	
	private String statusReport;
	public String getStatusReport() {
		return statusReport;
	}
	public void setStatusReport(String statusReport) {
		this.statusReport = statusReport;
	}
	
	private String peds;
	public String getPeds() {
		return peds;
	}
	public void setPeds(String peds) {
		this.peds = peds;
	}
	
	private String orgName;
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	
	private String lastSeen;
	public String getLastSeen() {
		return lastSeen;
	}
	public void setLastSeen(String lastSeen) {
		this.lastSeen = lastSeen;
	}

	private String comments;
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}

	private String gender;
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	private String hospitalIcon;
	public String getHospitalIcon() {
		return hospitalIcon;
	}
	public void setHospitalIcon(String hospitalIcon) {
		this.hospitalIcon = hospitalIcon;
	}

	private String massCasualtyId;
	public String getMassCasualtyId() {
		return massCasualtyId;
	}
	public void setMassCasualtyId(String massCasualtyId) {
		this.massCasualtyId = massCasualtyId;
	}	
	
	private String photoData;
	public String getPhotoData(){
		return photoData;
	}
	public void setPhotoData(String photoData){
		this.photoData = photoData;
	}
	
	private Bitmap photo;
	public Bitmap getPhoto() {
		return photo;
	}
	public void setPhoto(Bitmap photo){
		this.photo = photo;
	}
	public void downloadPatientPhoto(){
        if (imageUrlForFatch.endsWith("null") == true){
            photo = null;
            return;
        }
        if (imageUrlForFatch.isEmpty()){
            photo = null;
            return;
        }
		getImageFromUrlInAsyncWay();
	}

	private boolean statusPhotoDownload;
	public boolean getStatusPhotoDownload(){
		return statusPhotoDownload;
	}
	public void setStatusPhotoDownload(boolean statusPhotoDownload){
		this.statusPhotoDownload = statusPhotoDownload;
	}

	/**
	 * add animal in
	 * version 7.3.6
	 */
	// start
	private int pa;
	public int getPa() {
		return pa;
	}
	public void setPa(int pa) {this.pa = pa;}

	private String buddy;
	public String getBuddy(){return buddy;}
	public void setBuddy(String buddy){this.buddy = buddy;}
	// end

	// Convert photoData to Sha1 string
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
    	MessageDigest md = MessageDigest.getInstance("SHA-1");        
    	md.update(text.getBytes("iso-8859-1"), 0, text.length());
    	byte[] sha1hash = md.digest();
    	return convertToHex(sha1hash);
    } 
	
	// Convert photoData to Sha1 string
    public static String PhotoDataToSha1(String pData) throws NoSuchAlgorithmException, UnsupportedEncodingException  { 
    	MessageDigest md = MessageDigest.getInstance("SHA-1");
//        byte[] b = Base64.decode(pData);
        byte[] b = Base64.decode(pData, Base64.DEFAULT);
//    	md.update(b, 0, b.length);
    	md.update(b);
    	byte[] sha1hash = md.digest();
    	return convertToHex(sha1hash);
    } 

    private static String convertToHex(byte[] data) { 
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)) 
                    buf.append((char) ('0' + halfbyte));
                else 
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        } 
        return buf.toString();
    } 

    /**
     * input: image url
     * output: bitmap
     */
    public void getImageFromUrlInAsyncWay(){
		Bitmap bitmap = null;
		// Better to use the threads.
	    //limit the number of actual threads
	    int poolSize = 1;
	    ExecutorService service = Executors.newFixedThreadPool(poolSize);
	    List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

	    for (int n = 0; n < poolSize; n++)
	    {
	    	Future f = service.submit(new Runnable() {
	    		public void run(){

                    getBitmapFromURL(imageUrlForFatch);
	    		}

				private void getBitmapFromURL(String src) {
			        try {
			            // if it is null 
			            if (src.endsWith("null") == true){
			            	photo = null;
			            	return;
			            }

			            Log.e("src",src);
			            URL url = new URL(src);
			
			            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			            connection.setDoInput(true);
			            connection.setDoOutput(true);
			            connection.setReadTimeout(WebServer.WAITING_TIME);
			            connection.connect();
			            InputStream input = connection.getInputStream();
//			            Bitmap myBitmap = new Bitmap;
			            photo = BitmapFactory.decodeStream(input);
			            Log.e("Bitmap","returned");
			            return;
//			            return photo;
//			            return myBitmap;
			        } catch (IOException e) {
			            e.printStackTrace();
                        Log.e("Exception", e.getMessage());
			            photo = null;
			            return;
//			            return null;
			        }
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
			}
		    //shut down the executor service so that this thread can exit
		    service.shutdownNow();
	    }
	    // End of the thread
//	    return bitmap;
	}

	 // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return String.valueOf(serialId) + " " + givengName + " " + familyName + " " + eventName + " " + statusReport;
	  }

	  // if it is good, return null string
	  // if it is bad, return error message
	private String JSONParserForAddress(String string, String recordsFoundString) {
  		String url = "";
		String toParseString = "{" + "\"" + string + "\":" + recordsFoundString + "}";
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(toParseString);
			JSONArray  jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array

			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject o = jsonArray.getJSONObject(i); // create a single event jsonObject
				
				String p_uuid = o.getString("p_uuid");
				String location = o.getString("location");
				
				if (location.isEmpty() == false){ // To parser images
					// take off "[" and "]".
					location = "{" + "\"" + "location" + "\":[" + location + "]}";

					JSONObject jsonObjImages;
					try {
						jsonObjImages = new JSONObject(location);
						JSONArray  jsonArrayImages = jsonObjImages.getJSONArray("location"); // get all events as json objects from Events array
						for (int j = 0; j < jsonArrayImages.length(); j++){
							JSONObject oj = jsonArrayImages.getJSONObject(j); // create a single event jsonObject
							String street1 = oj.getString("street1");
							if (street1.equalsIgnoreCase("null") == true){
								street1 = "";
							}
							street1 = street1.replace("\n", "").replace("\r", "");
							String street2 = oj.getString("street2");
							if (street2.equalsIgnoreCase("null") == true){
								street2 = "";
							}
							String city = oj.getString("city");
							if (city.equalsIgnoreCase("null") == true){
								city = "";
							}
							String region = oj.getString("region");
							if (region.equalsIgnoreCase("null") == true){
								region = "";
							}
							String postal_code = oj.getString("postal_code");
							if (postal_code.equalsIgnoreCase("null") == true){
								postal_code = "";
							}
							String country = oj.getString("country");
							if (country.equalsIgnoreCase("null") == true){
								country = "";
							}

							this.setStreet1(street1);
							this.setStreet2(street2);
							this.setCity(city);
							this.setState(region);
							this.setZip(postal_code);
							this.setCountry(country);
						}
					}
					catch(JSONException e){
						e.printStackTrace();						
					}
					return url;
				}
			}
		} 
		catch (JSONException e) {

			e.printStackTrace();
		}     // create a json object from a string
		return url;
	}

	// don't need it anymore
  	public static String JSONParserForImageUrl(String string, String recordsFoundString) {
  		String url = "";
		String toParseString = "{" + "\"" + string + "\":" + recordsFoundString + "}";
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(toParseString);
			JSONArray  jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array

			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject o = jsonArray.getJSONObject(i); // create a single event jsonObject
				
				String p_uuid = o.getString("p_uuid");
				String images = o.getString("images");
				
				if (images.isEmpty() == false){ // To parser images
					// take off "[" and "]".
					images = "{" + "\"" + "images" + "\":" + images + "}";

					JSONObject jsonObjImages;
					try {
						jsonObjImages = new JSONObject(images);
						JSONArray  jsonArrayImages = jsonObjImages.getJSONArray("images"); // get all events as json objects from Events array
						for (int j = 0; j < jsonArrayImages.length(); j++){
							JSONObject oj = jsonArrayImages.getJSONObject(j); // create a single event jsonObject
							url = oj.getString("url");
						}
					}
					catch(JSONException e){
						e.printStackTrace();						
					}
					return url;
				}
			}
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}     // create a json object from a string
		return url;
	}

	public void toXML(String utcDataTimeAsString){
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
            this.setDateTimeSent(utcDataTimeAsString);
            dateTimeSent.setTextContent(this.getDateTimeSent());
            person.appendChild(dateTimeSent);

            // Node dateTimeSent
            Node expiryDate = doc.createElement("expiryDate");
            expiryDate.setTextContent("");
            person.appendChild(expiryDate);

            // Node eventShortname
            Node eventShortname = doc.createElement("eventShortname");
            eventShortname.setTextContent(this.getEventShortName());
            person.appendChild(eventShortname);

            // Node givenName
            Node givenName = doc.createElement("givenName");
            givenName.setTextContent(this.getGivengName());
            person.appendChild(givenName);

            // Node familyName
            Node familyName = doc.createElement("familyName");
            familyName.setTextContent(this.getFamilyName());
            person.appendChild(familyName);

            // Node gender
            Node gender = doc.createElement("gender");
            gender.setTextContent(this.getGender());
            person.appendChild(gender);

            // Node minAge
            Node minAge = doc.createElement("minAge");
            minAge.setTextContent(this.getMinAge());
            person.appendChild(minAge);

            // Node maxAge
            Node maxAge = doc.createElement("maxAge");
            maxAge.setTextContent(this.getMaxAge());
            person.appendChild(maxAge);

            // Node estimatedAge
            Node estimatedAge = doc.createElement("estimatedAge");
            estimatedAge.setTextContent(this.getYearsOld());
            person.appendChild(estimatedAge);

            // Node status
            Node status = doc.createElement("status");
            status.setTextContent(this.getOptStatus());
            person.appendChild(status);

            // Node location
            Node location = doc.createElement("location");

            // Node street1
            Node street1 = doc.createElement("street1");
            street1.setTextContent(this.getStreet1());
            location.appendChild(street1);

            // Node street2
            Node street2 = doc.createElement("street2");
            street2.setTextContent(this.getStreet2());
            location.appendChild(street2);

            // Node neighborhood
            Node neighborhood = doc.createElement("neighborhood");
            neighborhood.setTextContent("");
            location.appendChild(neighborhood);

            // Node city
            Node city = doc.createElement("city");
            city.setTextContent(this.getCity());
            location.appendChild(city);

            // Node region
            Node region = doc.createElement("region");
            region.setTextContent(this.getState());
            location.appendChild(region);

            // Node postalCode
            Node postalCode = doc.createElement("postalCode");
            postalCode.setTextContent(this.getZip());
            location.appendChild(postalCode);

            // Node country
            Node country = doc.createElement("country");
            country.setTextContent(this.getCountry());
            location.appendChild(country);

            // Node gps
            Node gps = doc.createElement("gps");

            // Node lat
            Node lat = doc.createElement("lat");
            lat.setTextContent(this.getLatitude());
            gps.appendChild(lat);

            // Node lon
            Node lon = doc.createElement("lon");
            lon.setTextContent(this.getLongitude());
            gps.appendChild(lon);

            // Colose gps
            location.appendChild(gps);

            // Close location
            person.appendChild(location);

            // Node commnet. Added version 3.2 build 2
            // Using "note" instead of "comment"!!!
            Node note = doc.createElement("note");
            note.setTextContent(this.getComments());
            person.appendChild(note);

            // Node delete photo
            Node deletePhotos = doc.createElement("deletePhotos");
            deletePhotos = buildDeletePhotoNodes(doc, deletePhotos, deleteImages);
            person.appendChild(deletePhotos);

            // Node photo
            Node photos = doc.createElement("photos");
            photos = buildPhotoNodes(doc, photos, images);
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

	public JSONObject buildReportJSON(final String token){
		JSONObject json = new JSONObject();
		try {
			json.put("token", token);
			json.put("call", "report");// version 7.3.6
			json.put("format", "PA1");
//			json.put("pa", "0"); // person = 0; animal = 1;
			json.put("pa", String.valueOf(this.getPa())); // person = 0; animal = 1;
			json.put("stat", this.getOptStatus()); //
			json.put("sex", this.getGender());
			json.put("age", this.getYearsOld());

            json.put("name", this.getFullName());

			json.put("latitude", this.getLatitude());
			json.put("longitude", this.getLongitude());
			json.put("lki", this.buildUnknownLocation());
//			json.put("buddy", "");
			json.put("buddy", this.getBuddy()); // version 7.3.6
			json.put("short", this.getEventShortName());

			// add photos, current we just send one photo.
			if (!this.getImages().isEmpty()) {
				Image img = this.getImages().get(0);
				json.put("photo", img.getPhotoData());
			}
		}
		catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
			return null;
		}
		return json;
	}

	public JSONObject buildReviseJSON(final String token){
		JSONObject json = new JSONObject();
		try {
			json.put("token", token);
			json.put("call", "revise");
			json.put("uuid", this.getPatientUuid());
			json.put("format", "PA1");
			json.put("pa", String.valueOf(this.getPa())); // person = 0; animal = 1;
			json.put("stat", this.getOptStatus()); //
			json.put("sex", this.getGender());
			json.put("age", this.getYearsOld());

			json.put("name", this.getFullName());

			json.put("latitude", this.getLatitude());
			json.put("longitude", this.getLongitude());
			json.put("lki", this.buildUnknownLocation());
			json.put("buddy", this.getBuddy());
			json.put("short", this.getEventShortName());

			// add photos, current we just send one photo.
			if (!this.getImages().isEmpty()) {
				Image img = this.getImages().get(0);
				json.put("photo", img.getPhotoData());
			}
		}
		catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
			return null;
		}
		return json;
	}

	public String buildUnknownLocation(){
		String address = "";
		if (!this.getStreet1().isEmpty()){
			address += this.getStreet1();
		}
		if (!this.getStreet2().isEmpty()){
			address += ", " + this.getStreet2();
		}
		if (!this.getCity().isEmpty()){
			address += ", " + this.getCity();
		}
		if (!this.getState().isEmpty()){
			address += ", " + this.getState();
		}
		if (!this.getZip().isEmpty()){
			address += ", " + this.getZip();
		}
		if (!this.getCountry().isEmpty()){
			address += ", " + this.getCountry();
		}
		return address;
	}

	public Node buildDeletePhotoNodes(Document doc, Node deletePhotos, List<String> deleteImages) {
        for (int i = 0; i < deleteImages.size(); i++){
            String deleteImg = deleteImages.get(i);
            if (!deleteImg.isEmpty()){
                Node photo = doc.createElement("photo");
                photo.setTextContent(deleteImg);
                deletePhotos.appendChild(photo);
            }
        }
        return deletePhotos;
    }

    // created for multiple images
    public Node buildPhotoNodes(Document doc, Node photos, List<Image> images){
        for (int i = 0; i < images.size(); i++){
            Image img = images.get(i);

            Node photo = doc.createElement("photo");
            // Node primary
            Node primary = doc.createElement("primary");
            if (i == 0){
                primary.setTextContent("1");
            }
            else{
                primary.setTextContent("0");
            }
            photo.appendChild(primary);

            // Node primary
            Node data = doc.createElement("data");
            data.setTextContent(img.getPhotoData());
            photo.appendChild(data);

            // Node Tags
            Node tags = doc.createElement("tags");

            // Node tag
            Node tag = doc.createElement("tag");

			if (img.isFaceDetected() == true){
				// Node x
				Node x = doc.createElement("x");
				x.setTextContent(String.valueOf(img.getRectX()));
				tag.appendChild(x);

				// Node y
				Node y = doc.createElement("y");
				y.setTextContent(String.valueOf(img.getRectY()));
				tag.appendChild(y);

				// Node w
				Node w = doc.createElement("w");
				w.setTextContent(String.valueOf(img.getRectW()));
				tag.appendChild(w);

				// Node h
				Node h = doc.createElement("h");
				h.setTextContent(String.valueOf(img.getRectH()));
				tag.appendChild(h);
			}
			else {
				// Node x
				Node x = doc.createElement("x");
				x.setTextContent("0");
				tag.appendChild(x);

				// Node y
				Node y = doc.createElement("y");
				y.setTextContent("0");
				tag.appendChild(y);

				// Node w
				Node w = doc.createElement("w");
				w.setTextContent(String.valueOf(Patient.PHOTO_WIDTH - 1));
				tag.appendChild(w);

				// Node h
				Node h = doc.createElement("h");
				h.setTextContent(String.valueOf(Patient.PHOTO_HEIGHT - 1));
				tag.appendChild(h);
			}

            // Node text
            Node text = doc.createElement("text");
            text.setTextContent(img.getCaption().toString());
            tag.appendChild(text);

            tags.appendChild(tag);

            photo.appendChild(tags);
            photos.appendChild(photo);
        }

        return photos;
    }

    // added version 9.0.0
    public static String encodeBitmapToString(Bitmap bm){
        String str;
        ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, Image.COMPRESS_RATE, byteArryPhoto);
        byte[] b = byteArryPhoto.toByteArray();
        str = Base64.encodeToString(b, Base64.DEFAULT);
        return str;
    }

	/**
	 * Multiple languages
	 * Gender
     */
	public static int getIndexFromGenderArray(String shortForm){
		int index = 3;
		CharSequence[] genderShortFormArray = new CharSequence[]{MALE_SHORT, FEMALE_SHORT, UNKNOWN_SHORT, COMPLEX_SHORT};
		for (int i = 0; i < genderShortFormArray.length; i++){
			if (shortForm.compareToIgnoreCase(genderShortFormArray[i].toString()) == 0){
				index = i;
				break;
			}
		}
		return index;
	}

	public static String convertGenderToShortForm(Context c, String gender){
		String shortForm = "";
		// Save to object, convert from long to short format.
		if (gender.equalsIgnoreCase(c.getResources().getString(R.string.male)) == true) {
			shortForm = Patient.MALE_SHORT;
		}
		else if (gender.equalsIgnoreCase(c.getResources().getString(R.string.female)) == true){
			shortForm = Patient.FEMALE_SHORT;
		}
		else if (gender.equalsIgnoreCase(c.getResources().getString(R.string.complex)) == true){
			shortForm = Patient.COMPLEX_SHORT;
		}
		else {
			shortForm = Patient.UNKNOWN_SHORT;
		}
		return shortForm;
	}

	public static String convertGenderToLongForm(Context c, String shortForm){
		String gender = "";
		// To display, convert from short to long form
		if (shortForm.equalsIgnoreCase(Patient.MALE_SHORT) == true) {
			gender = c.getResources().getString(R.string.male);
		}
		else if (shortForm.equalsIgnoreCase(Patient.FEMALE_SHORT) == true){
			gender = c.getResources().getString(R.string.female);
		}
		else if (shortForm.equalsIgnoreCase(Patient.COMPLEX_SHORT) == true){
			gender = c.getResources().getString(R.string.complex);
		}
		else {
			gender = c.getResources().getString(R.string.unknown);
		}
		return gender;
	}
	/**
	 * Multiple languages
	 * Status
	 */
	public static int getIndexFromStatusShortArray(String shortForm){
		int index = 4;
		CharSequence[] statusShortFormArray = new CharSequence[]{MIS, ALI, INJ, DEC, UNK, FND};
		for (int i = 0; i < statusShortFormArray.length; i++){
			if (shortForm.compareToIgnoreCase(statusShortFormArray[i].toString()) == 0){
				index = i;
				break;
			}
		}
		return index;
	}

	public static String convertStatusToShortForm(Context c, String status){
		String shortForm = "";
		// Save to object, convert from long to short format.
		if (status.equalsIgnoreCase(c.getResources().getString(R.string.missing)) == true) {
			shortForm = Patient.MIS;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.alive_and_well)) == true){
			shortForm = Patient.ALI;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.injured)) == true){
			shortForm = Patient.INJ;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.deceased)) == true){
			shortForm = Patient.DEC;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.found)) == true){
			shortForm = Patient.FND;
		}
		else {
			shortForm = Patient.UNK;
		}
		return shortForm;
	}

	public static String convertStatusToLongForm(Context c, String shortForm){
		String status = "";
		// To display, convert from short to long form
		if (shortForm.equalsIgnoreCase(Patient.MIS) == true) {
			status = c.getResources().getString(R.string.missing);
		}
		else if (shortForm.equalsIgnoreCase(Patient.ALI) == true){
			status = c.getResources().getString(R.string.alive_and_well);
		}
		else if (shortForm.equalsIgnoreCase(Patient.INJ) == true){
			status = c.getResources().getString(R.string.injured);
		}
		else if (shortForm.equalsIgnoreCase(Patient.DEC) == true){
			status = c.getResources().getString(R.string.deceased);
		}
		else if (shortForm.equalsIgnoreCase(Patient.FND) == true){
			status = c.getResources().getString(R.string.found);
		}
		else {
			status = c.getResources().getString(R.string.unknown);
		}
		return status;
	}

	public static int getColorFromStatusArray(Context c, String status){
		return Color.BLACK;
		/*
		int color = 0;
		if (status.equalsIgnoreCase(UNK) == true){
			color = Color.WHITE;
		}
		else if (status.equalsIgnoreCase("?") == true){
			color = Color.WHITE;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.unknown)) == true){
			color = Color.WHITE;
		}
		else if (status.equalsIgnoreCase(INJ) == true){
			color = Color.YELLOW;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.injured)) == true){
			color = Color.YELLOW;
		}
		else if (status.equalsIgnoreCase(ALI) == true){
			color = Color.GREEN;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.alive_and_well)) == true){
			color = Color.GREEN;
		}
		else if (status.equalsIgnoreCase(MIS) == true){
			color = Color.CYAN;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.missing)) == true){
			color = Color.CYAN;
		}
		else if (status.equalsIgnoreCase(DEC) == true){
			color = Color.LTGRAY;
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.deceased)) == true){
			color = Color.LTGRAY;
		}
		else if (status.equalsIgnoreCase(FND) == true){
			color = Color.rgb(255, 160, 122);
		}
		else if (status.equalsIgnoreCase(c.getResources().getString(R.string.found)) == true){
			color = Color.rgb(255, 160, 122);
		}
		else {
			color = Color.WHITE;
		}
		return color;
		*/
	}

}
