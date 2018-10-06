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
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReUnite extends Application{
    // Google Cloud Message
    /**
     * Google Cloud Message
     * Project Number: 173542997716
     */

    // longitude and longitude
    private double latitude;
    public double getLatitude() {
        return latitude;
    }
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
    private double longitude;
    public double getLongitude() {
        return longitude;
    }
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    private String gcmToken;
    public void setGcmToken(String gcmToken){this.gcmToken = gcmToken;}
    public String getGcmToken(){return gcmToken;}

    private String gcmTokenUploadMsg;
    public void setGcmTokenUploadMsg(String gcmTokenUploadMsg){this.gcmTokenUploadMsg = gcmTokenUploadMsg;}
    public String getGcmTokenUploadMsg(){return gcmTokenUploadMsg;}

    /**
     * FollowRecordList
     * version 7.1.6
     */
    private List<FollowRecord> followRecordList = new ArrayList<FollowRecord>();
    public void setFollowRecordList(List<FollowRecord> followRecordList){
        this.followRecordList = followRecordList;
    }
    public List<FollowRecord> getFollowRecordList(){
        return followRecordList;
    }

    private FollowRecord curSelFollowRecord;
    void setCurSelFollowRecord(FollowRecord curSelFollowRecord){
        this.curSelFollowRecord = curSelFollowRecord;
    }
    FollowRecord getCurSelFollowRecord() {
        return this.curSelFollowRecord;
    }

    /**
     * registerGCM
     * unregisterGCM
     */

    /**
     * Android
     * Service registerAndroidDevice
     */
    /*
    new web service:

            *registerAndroidDevice*
    token
            registrationID
    versionString
    */

    /**
     * Apple
     * Service registerGCM;
     */
//    <?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/" xmlns="soap/plusWebServices"><soap:Body><registerGCM><token>bad23bd967552165aba0ac4495684b8e4c6a06c34274421cff530864b0473c3ddceb1f8959603e7ecbd23d67204b44ebf1961b515c5b571d93e2232708188094</token><registrationID>msOpNUfpBCc:APA91bH1imm6MErxSd6RO-xrj3-sO0vAJAxF5wyiuLY6iiSRHHkk7G3JEkISq3-TQ4dZm5ynBH26NW3qDTZdL-UmDNLQVu76zkZ2Cg3Ij2Q6klBPmcF557MNV6cAQTrZWRHRn_rCig-j</registrationID><versionString>iPhone (iPhone6,1) (9.0.2) (en-US) (GMT-4)</versionString></registerGCM></soap:Body></soap:Envelope>


    // test web server - end
	public static final boolean QUICK_START_DEFAULT = false;
    public static final boolean DEVELOPER_OPTION_DEFAULT = false;

    public static final String ADMINISTRATOR = "administrator";
    public static final String USER = "user";
    public static final String ANONYMOUS_USER = "Anonymous User";
    public static final String RESEARCHERS = "researchers";

    // track
    /**
     * track list
     About
     Help
     Privacy
     Resources
     Follow Us
     Trademark
     OMB
     Contact Us
     Record View (event short name)
     Report (event short name)
     Update (event short name)
     Search
     Login
     Register
     */
    public static final String TRACK_ABOUT = "About";
    public static final String TRACK_LOGIN = "Login";
    public static final String TRACK_REGISTER = "Register";
    public static final String TRACK_SEARCH = "Search";
    public static final String TRACK_REPORT = "Report";
    public static final String TRACK_UPDATE = "Update";
    public static final String TRACK_RECORD_VIEW = "Record View";
    public static final String TRACK_OMB = "OMB";
    public static final String TRACK_PRIVACY = "Privacy";
    public static final String TRACK_CONTACT = "Contact Us";
//    public static final String TRACK_FOLLOW_US = "Follow Us";

    public static final String SUCCEED = "Succeed";
    public static final String FAILED = "Failed";

    public static final String GOOGLE_PLAY_URL="https://play.google.com/store/apps/details?id=com.pl.reunite&referrer=utm_source%3Dgoogle%26utm_medium%3Dcpc%26utm_term%3Dfind%252Breport%252Bmissing%252Bperson%252Bdisaster%252Bfamily%252Breunification%252Bpeople%252Blocator%26utm_content%3Dlogolink%26utm_campaign%3Dfind%2520and%2520report%2520missing%2520persons%26anid%3Dadmob";

    private boolean authStatus;
	private String username;
	private String password;
	private String webServer;	
	private boolean quickStart;
    private boolean developer;

    // version 4.0.0
    // token section - start
    // the token status is changed in REST calls:
    // gid = group id; 1:admin, 2:user; 3:anon
    public static final int TOKEN_AUTH = 2;     // when have token
    public static final int TOKEN_ANONYMOUS = 3;// where have anonymous token

//    public static final int TOKEN_AUTH = 1;     // when have token
//    public static final int TOKEN_ANONYMOUS = 2;// where have anonymous token

    public static final int TOKEN_UNKNOWN = -1; // when have neither

    public static final int NINE_HOURS = 32400000;

    public static final int NCMEC_CLAIM_NOT_SHOW = 0;
    public static final int NCMEC_CLAIM_SHOW = 1;
    public static final int NEMEC_CLAIM_UNDECIDED = -1;

    private boolean isTablet;
    public boolean getTablet() {
        return this.isTablet;
    }
    public void setTablet(boolean isTablet) {
        this.isTablet = isTablet;
    }

    private long authId;

    private String token;
    public void setToken(String token){this.token = token;}
    public String getToken(){return token;}

    private String tokenAnonymous;
    public void setTokenAnonymous(String tokenAnonymous){this.tokenAnonymous = tokenAnonymous;}
    public String getTokenAnonymous(){return tokenAnonymous;}

    private String token2;
    public void setToken2(String token2){this.token2 = token2;}
    public String getToken2(){return token2;}

    private long timeWhenGotAnonymousToken;
    public void setTimeWhenGotAnonymousToken(long timeWhenGotAnonymousToken){this.timeWhenGotAnonymousToken = timeWhenGotAnonymousToken;}
    public void recordTimeWhenGotAnonymousToken(){timeWhenGotAnonymousToken = System.currentTimeMillis();}
    public long getTimeWhenGotAnonymousToken(){return timeWhenGotAnonymousToken;}

    public boolean isAnonymousTokenExpired(){
        long timeNow = System.currentTimeMillis();
        if ((timeNow - timeWhenGotAnonymousToken) > NINE_HOURS) {// normal 10 hours. I defined as 9 hours.
            return true;
        }
        return false;
    }

    private int tokenStatus;
    public void setTokenStatus(int tokenStatus){this.tokenStatus = tokenStatus;}
    public int getTokenStatus(){return tokenStatus;}

    // ncmec clam
    private int ncmecClaim;
    public int getNcmecClaim() {return ncmecClaim;}
    public void setNcmecClaim(int ncmecClaim){
        this.ncmecClaim = ncmecClaim;
    }

    // token section end

    private boolean isAnonymous;
    private long webServerId;
    //	private String webServerName;
    private boolean offLineMode;
    private String seed;

    private String groupId;
    private String groupName;

    private Patient curSelPatient;
    void setCurSelPatient(Patient curSelPatient){
        this.curSelPatient = curSelPatient;
    }
    Patient getCurSelPatient() {
        return this.curSelPatient;
    }

    private Event lastEvent;
    public void setLastEvent(Event lastEvent){this.lastEvent = lastEvent;}
    public Event getLastEvent(){return lastEvent;}
    // version 4.0.0. - end

    private String curEncodedImage;
    public String getCurEncodedImage(){return this.curEncodedImage;}
    public void setCurEncodedImage(String curEncodedImage){this.curEncodedImage = curEncodedImage;}

    private Intent cameraIntent;
    public Intent getCameraIntent(){
        return this.cameraIntent;
    }
    public void setCameraIntent(Intent cameraIntent){
        this.cameraIntent = cameraIntent;
    }

    // locale
    private Locale locale;
    public void setLocale(Locale locale){this.locale = locale;}
    public Locale getLocale(){return locale;}

    private String languageCode;
    public void setLanguageCode(String languageCode){this.languageCode = languageCode;}
    public String getLanguageCode(){return languageCode;}
    // support en, es, ja, zh_CN and zh_TW
    public String toLanguageCode(){
        if (locale.getLanguage().equalsIgnoreCase("zh")){
            languageCode = locale.toString();
        }
        else {
            languageCode = locale.getLanguage();
        }
        return languageCode;
    }

    private boolean offLine;
    public boolean isOffLine(){return offLine;}
    public void setOffline(boolean offLine){this.offLine = offLine;}

    // add pet
    private int personOrAnimal;
    public void setPersonOrAnimal(int personOrAnimal) {this.personOrAnimal = personOrAnimal;}
    public int getPersonOrAnimal() {return personOrAnimal;}

    public ReUnite(String username, String password, String webServer, boolean quickStart, boolean developer, String token, String tokenAnonymous, String token2, int tokenStatus, String groupId, String groupName){
        super();
        if (password.isEmpty() == true){
            authStatus = false;
        }
        else if (username.equalsIgnoreCase("Guest") == true){
            authStatus = false;
        }
        else {
            authStatus = true;
        }

        this.username = username;
        this.password = password;
        this.webServer = webServer;
        this.quickStart = quickStart;
        this.developer = developer;
        this.token = token;
        this.tokenAnonymous = tokenAnonymous;
        this.token2 = token2;
        this.tokenStatus = tokenStatus;
        this.groupId = groupId;
        this.groupName = groupName;

        this.gcmToken = "";
        this.gcmTokenUploadMsg = "";

        this.ncmecClaim = NEMEC_CLAIM_UNDECIDED;
    }
	
	public ReUnite(){
		super();
        username = "Guest";
        password = "";
        webServer = WebServer.PL_NAME;
        quickStart = false;
        developer = false;
        token = "";
        groupId = "";
        groupName = "";
        tokenAnonymous = "";
        token2 = "";
        tokenStatus = ReUnite.TOKEN_UNKNOWN;
        groupId = "";
        groupName = "";

        this.gcmToken = "";
        this.gcmTokenUploadMsg = "";

        this.ncmecClaim = NEMEC_CLAIM_UNDECIDED;

        this.personOrAnimal = Patient.PERSON;
    }

    public String getGroupId() {
        return groupId;
    }
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
    public String getGroupName() {
        return groupName;
    }
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setGroupNameWithGroupId(){
        if (groupId.isEmpty() == false){
            if (groupId.equalsIgnoreCase("1")){
                groupName = ReUnite.ADMINISTRATOR;
            }
            else if (groupId.equalsIgnoreCase("2")){
                groupName = ReUnite.USER;
            }
            else if (groupId.equalsIgnoreCase("3")){
                groupName = ReUnite.ANONYMOUS_USER;
            }
            else {
                groupName = "";
            }
        }
    }

    public String getWebServer(){
		return webServer;
	}
	public void setWebServer(String webServer){
		this.webServer = webServer;
	}

	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username = username;
	}
	
	public String getPassword(){
		return password;
	}
	public void setPassword(String password){
		this.password = password;
	}

	public boolean getQuickStart(){
		return quickStart;
	}
	public void setQuickStart(boolean quickStart){
		this.quickStart = quickStart;
	}

    public boolean getDeveloper() {
        return developer;
    }

    public void setDeveloper(boolean developer) {
        this.developer = developer;
    }

    public boolean getAuthStatus(){
		return authStatus;
	}
	public void setAuthStatus(boolean authStatus){
		this.authStatus = authStatus;
	}

    public boolean detectMobileDevice(Context context){
        isTablet = false;
        if ((context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE) {
            isTablet = true;
        }
        return isTablet;
    }

    public void setIsTablet(final boolean b){
        isTablet = b;
    }

    public boolean isTablet(){
        return isTablet;
    }

    public void setScreenOrientation(Activity c){
        int currentOrientation = c.getResources().getConfiguration().orientation;
        // cell phone
        if (isTablet == false){
            if (currentOrientation == Configuration.ORIENTATION_UNDEFINED ||currentOrientation == Configuration.ORIENTATION_LANDSCAPE  ){
                c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
        // tablet
        else {
            if (currentOrientation == Configuration.ORIENTATION_UNDEFINED || currentOrientation == Configuration.ORIENTATION_PORTRAIT){
                // test
                // always portrait.
                c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }

        c.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED); // Added in version 6.1.3
    }

    /**
     * The Analytics singleton. The field is set in onCreate method override when the application
     * class is initially created.
     */
    private static GoogleAnalytics analytics;

    /**
     * The default app tracker. The field is from onCreate callback when the application is
     * initially created.
     */
    private static Tracker tracker;

    /**
     * Access to the global Analytics singleton. If this method returns null you forgot to either
     * set android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.analytics field in onCreate method override.
     */
    public static GoogleAnalytics analytics() {
        return analytics;
    }

    /**
     * The default app tracker. If this method returns null you forgot to either set
     * android:name="&lt;this.class.name&gt;" attribute on your application element in
     * AndroidManifest.xml or you are not setting this.tracker field in onCreate method override.
     */
    public static Tracker tracker() {
        return tracker;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Google Analytics
         * Added in version
         */

        analytics = GoogleAnalytics.getInstance(this);
        tracker = analytics.newTracker(getString(R.string.google_analytics_property_id));

        // Provide unhandled exceptions reports. Do that first after creating the tracker
        tracker.enableExceptionReporting(true);

        // Enable Remarketing, Demographics & Interests reports
        // https://developers.google.com/analytics/devguides/collection/android/display-features
        tracker.enableAdvertisingIdCollection(true);

        // Enable automatic activity tracking for your app
        // disable the automatically report screen name
//        tracker.enableAutoActivityTracking(true);

        tracker.send(new HitBuilders.ScreenViewBuilder().setCustomDimension(1, null).build());
    }
}
