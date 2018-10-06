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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This file is a must to build database.
 * Almost it is a standard.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    // define patient table
    public static final String TABLE_NAME = "patient";
    public static final String TABLE_NAME_ADDRESS = "address";
    public static final String TABLE_NAME_EVENT = "event";
    public static final String TABLE_NAME_IMAGE = "image"; // image table

    public static final String COLUMN_SERIALID = "serialId";
    public static final String COLUMN_GIVENNAME = "givenName";
    public static final String COLUMN_FAMILYNAME = "familyName";
    public static final String COLUMN_PA = "pa";    // add animal, version 7.3.6
    public static final String COLUMN_BUDDY = "buddy";  // add buddy, version 7.3.6
    public static final String COLUMN_EVENT_NAME = "eventName";
    public static final String COLUMN_EVENT_SHORTNAME = "eventShortName";
    public static final String COLUMN_MOER_DETAILS = "moreDetails";
    public static final String COLUMN_DATE_TIME_SENT = "dateTimeSent";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_STREET1 = "street1";
    public static final String COLUMN_STREET2 = "street2";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_STATE = "state";
    public static final String COLUMN_ZIP = "zip";
    public static final String COLUMN_COUNTRY = "country";
    public static final String COLUMN_PATIENT_UUID = "patientUuid";
    public static final String COLUMN_ENCODE_UUID = "encodedUuid";
    public static final String COLUMN_FULLNAME = "fullName";
    public static final String COLUMN_OPT_STATUS = "optStatus";

    // image
//    public static final String COLUMN_SERIALID = "serialId"; // 0
    public static final String COLUMN_PATIENT_SERIAL_ID = "patientSerialId"; // 1
    public static final String COLUMN_SEQUENCE = "Sequence";	// 2
    public static final String COLUMN_IMAGE_URL = "imageUrl"; // 3
    public static final String COLUMN_IMAGE_URL_FOR_FATCH = "imageUrlForFatch"; // 4
    public static final String COLUMN_IMAGE_WIDTH = "imageWidth"; // 5
    public static final String COLUMN_IMAGE_HEIGHT = "imageHeight"; // 6
    public static final String COLUMN_PHOTO_DATA = "photoData"; // 7
    public static final String COLUMN_FACE_DETECTED = "FaceDetected";	// 8
    public static final String COLUMN_RECT_X = "Rect_X";	// 9
    public static final String COLUMN_RECT_Y = "Rect_Y";	// 10
    public static final String COLUMN_RECT_H = "Rect_H";	// 11
    public static final String COLUMN_RECT_W = "Rect_W";	// 12
    public static final String COLUMN_CAPTION = "Caption"; 	// 13
    public static final String COLUMN_SIZE = "Size"; 	// 14

    public static final String COLUMN_YEARS_OLD = "yearsOld";
    public static final String COLUMN_MIN_AGE = "minAge";
    public static final String COLUMN_MAX_AGE = "maxAge";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_STATUS_SAHANA_UPDATED = "statusSahanaUpdated";
    public static final String COLUMN_STATUS_TRIAGE = "statusTriage";
    public static final String COLUMN_STATUS_REPORT = "statusReport";
    public static final String COLUMN_PEDS = "peds";
    public static final String COLUMN_ORG_NAME = "orgName";
    public static final String COLUMN_LAST_SEEN = "lastSeen";
    public static final String COLUMN_COMMENTS = "comments";
    public static final String COLUMN_GENDER = "gender";
    public static final String COLUMN_HOSPITAL_ICON = "hospitalIcon";
    public static final String COLUMN_MASS_CASUALTY_ID = "massCasualtyId";

    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_STREET = "street";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_STATUS = "status";

    private static final String DATABASE_NAME = "patients.db";

    /**
     * Database version
     * 16 - add animal: PA and buddy, 4/10/2018
     */
    private static final int DATABASE_VERSION = 16;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NAME + "("
            + COLUMN_SERIALID + " integer primary key autoincrement, "
            + COLUMN_GIVENNAME + " text not null, "
            + COLUMN_FAMILYNAME + " text not null, "
            + COLUMN_PA + " text not null, "  // animal version 7.3.6
            + COLUMN_BUDDY + " text not null, "  // animal version 7.3.6
            + COLUMN_EVENT_NAME + " text not null, "
            + COLUMN_EVENT_SHORTNAME + " text not null, "
            + COLUMN_MOER_DETAILS + " text not null, "
            + COLUMN_DATE_TIME_SENT + " text not null, "
            + COLUMN_LATITUDE + " text not null, "
            + COLUMN_LONGITUDE + " text not null, "
            + COLUMN_STREET1 + " text not null, "
            + COLUMN_STREET2 + " text not null, "
            + COLUMN_CITY + " text not null, "
            + COLUMN_STATE + " text not null, "
            + COLUMN_ZIP + " text not null, "
            + COLUMN_COUNTRY + " text not null, "
            + COLUMN_PATIENT_UUID + " text not null, "
            + COLUMN_ENCODE_UUID + " text not null, "
            + COLUMN_FULLNAME + " text not null, "
            + COLUMN_OPT_STATUS + " text not null, "
            + COLUMN_IMAGE_URL + " text not null, "
            + COLUMN_IMAGE_URL_FOR_FATCH + " text not null, "
            + COLUMN_IMAGE_WIDTH + " text not null, "
            + COLUMN_IMAGE_HEIGHT + " text not null, "
            + COLUMN_YEARS_OLD + " text not null, "
            + COLUMN_MIN_AGE + " text not null, "
            + COLUMN_MAX_AGE + " text not null, "
            + COLUMN_ID + " text not null, "
            + COLUMN_STATUS_SAHANA_UPDATED + " text not null, "
            + COLUMN_STATUS_TRIAGE + " text not null, "
            + COLUMN_STATUS_REPORT + " text not null, "
            + COLUMN_PEDS + " text not null, "
            + COLUMN_ORG_NAME + " text not null, "
            + COLUMN_LAST_SEEN + " text not null, "
            + COLUMN_COMMENTS + " text not null, "
            + COLUMN_GENDER + " text not null, "
            + COLUMN_HOSPITAL_ICON + " text not null, "
            + COLUMN_MASS_CASUALTY_ID + " text not null, "
            + COLUMN_PHOTO_DATA + " text not null);";

    private static final String DATABASE_CREATE_ADDRESS = "create table "
            + TABLE_NAME_ADDRESS + "("
            + COLUMN_SERIALID + " integer primary key autoincrement, "
            + COLUMN_STREET1 + " text not null, "
            + COLUMN_STREET2 + " text not null, "
            + COLUMN_CITY + " text not null, "
            + COLUMN_STATE + " text not null, "
            + COLUMN_ZIP + " text not null, "
            + COLUMN_COUNTRY + " text not null);";

    private static final String DATABASE_CREATE_EVENT = "create table "
            + TABLE_NAME_EVENT + "("
            + COLUMN_SERIALID + " integer primary key autoincrement, "
            + COLUMN_EVENT_NAME + " text not null, "
            + COLUMN_EVENT_SHORTNAME + " text not null, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_STREET + " text not null, "
            + COLUMN_TYPE + " text not null, "
            + COLUMN_STATUS + " text not null);";

    private static final String DATABASE_CREATE_IMAGE = "create table "
            + TABLE_NAME_IMAGE + "("
            + COLUMN_SERIALID + " integer primary key autoincrement, " // 0
            + COLUMN_PATIENT_SERIAL_ID + " text not null, " // 1
            + COLUMN_SEQUENCE + " text not null, " // 2
            + COLUMN_IMAGE_URL + " text not null, " // 3
            + COLUMN_IMAGE_URL_FOR_FATCH + " text not null, " // 4
            + COLUMN_IMAGE_WIDTH + " text not null, " // 5
            + COLUMN_IMAGE_HEIGHT + " text not null, " // 6
            + COLUMN_PHOTO_DATA + " text not null, " // 7
            + COLUMN_FACE_DETECTED + " text not null, " // 8
            + COLUMN_RECT_X + " text not null, " // 9
            + COLUMN_RECT_Y + " text not null, " // 10
            + COLUMN_RECT_H + " text not null, " // 11
            + COLUMN_RECT_W + " text not null, " // 12
            + COLUMN_CAPTION + " text not null, " // 13
            + COLUMN_SIZE + " text not null);"; // 14

    public MySQLiteHelper(Context context) {
		    super(context, DATABASE_NAME, null, DATABASE_VERSION);
	  }

	  @Override
	  public void onCreate(SQLiteDatabase database) {
		  database.execSQL(DATABASE_CREATE);
		  database.execSQL(DATABASE_CREATE_ADDRESS);
          database.execSQL(DATABASE_CREATE_EVENT);
          database.execSQL(DATABASE_CREATE_IMAGE);
	  }

	  @Override
	  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    Log.w(MySQLiteHelper.class.getName(),
	        "Upgrading database from version " + oldVersion + " to "
	            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ADDRESS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EVENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_IMAGE);
	    onCreate(db);
	  }
	} 