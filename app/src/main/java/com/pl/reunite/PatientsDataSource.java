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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapping class for patient data to the database.
 * All functions relayed to the transaction of patient data are included.
 */
public class PatientsDataSource {
	  // Database fields
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = {
			  MySQLiteHelper.COLUMN_SERIALID,
			  MySQLiteHelper.COLUMN_GIVENNAME,
			  MySQLiteHelper.COLUMN_FAMILYNAME,
			  MySQLiteHelper.COLUMN_PA,	// animal version 7.3.6
			  MySQLiteHelper.COLUMN_BUDDY, // animal version 7.3.6
			  MySQLiteHelper.COLUMN_EVENT_NAME,
			  MySQLiteHelper.COLUMN_EVENT_SHORTNAME,
			  MySQLiteHelper.COLUMN_MOER_DETAILS,
			  MySQLiteHelper.COLUMN_DATE_TIME_SENT,
			  MySQLiteHelper.COLUMN_LATITUDE,
			  MySQLiteHelper.COLUMN_LONGITUDE,
			  MySQLiteHelper.COLUMN_STREET1,
			  MySQLiteHelper.COLUMN_STREET2,
			  MySQLiteHelper.COLUMN_CITY,
			  MySQLiteHelper.COLUMN_STATE,
			  MySQLiteHelper.COLUMN_ZIP,
			  MySQLiteHelper.COLUMN_COUNTRY,
			  MySQLiteHelper.COLUMN_PATIENT_UUID,
			  MySQLiteHelper.COLUMN_ENCODE_UUID,
			  MySQLiteHelper.COLUMN_FULLNAME,
			  MySQLiteHelper.COLUMN_OPT_STATUS,
			  MySQLiteHelper.COLUMN_IMAGE_URL,
			  MySQLiteHelper.COLUMN_IMAGE_URL_FOR_FATCH,
			  MySQLiteHelper.COLUMN_IMAGE_WIDTH,
			  MySQLiteHelper.COLUMN_IMAGE_HEIGHT,
			  MySQLiteHelper.COLUMN_YEARS_OLD,
			  MySQLiteHelper.COLUMN_MIN_AGE,
			  MySQLiteHelper.COLUMN_MAX_AGE,
			  MySQLiteHelper.COLUMN_ID,
			  MySQLiteHelper.COLUMN_STATUS_SAHANA_UPDATED,
			  MySQLiteHelper.COLUMN_STATUS_TRIAGE,
			  MySQLiteHelper.COLUMN_STATUS_REPORT,
			  MySQLiteHelper.COLUMN_PEDS,
			  MySQLiteHelper.COLUMN_ORG_NAME,
			  MySQLiteHelper.COLUMN_LAST_SEEN,
			  MySQLiteHelper.COLUMN_COMMENTS,
			  MySQLiteHelper.COLUMN_GENDER,
			  MySQLiteHelper.COLUMN_HOSPITAL_ICON,
			  MySQLiteHelper.COLUMN_MASS_CASUALTY_ID,
			  MySQLiteHelper.COLUMN_PHOTO_DATA
	  };
	  
	  private Patient patient;
	  public Patient getPatient(){
		  return patient;
	  }
	  public void setPatient(Patient patient){
		  this.patient = patient;
	  }

	  public PatientsDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Patient createPatient(String givenName, String lastName) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_GIVENNAME, givenName);
	    values.put(MySQLiteHelper.COLUMN_FAMILYNAME, lastName);

	    long insertId = database.insert(MySQLiteHelper.TABLE_NAME, null,
	        values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
	        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Patient newP = cursorToPatient(cursor);
	    cursor.close();
	    return newP;
	  }

	  public Patient createPatient(Patient p) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_GIVENNAME, p.getGivengName());
		    values.put(MySQLiteHelper.COLUMN_FAMILYNAME, p.getFamilyName());
			values.put(MySQLiteHelper.COLUMN_PA, p.getPa());	// animal, version 7.3.6
			values.put(MySQLiteHelper.COLUMN_BUDDY, p.getBuddy()); // animal, version 7.3.6
			values.put(MySQLiteHelper.COLUMN_EVENT_NAME, p.getEventName());
		    values.put(MySQLiteHelper.COLUMN_EVENT_SHORTNAME, p.getEventShortName());
		    values.put(MySQLiteHelper.COLUMN_GIVENNAME, p.getGivengName());
		    values.put(MySQLiteHelper.COLUMN_FAMILYNAME, p.getFamilyName());
		    values.put(MySQLiteHelper.COLUMN_MOER_DETAILS, p.getMoreDetails());
		    values.put(MySQLiteHelper.COLUMN_DATE_TIME_SENT, p.getDateTimeSent());
		    values.put(MySQLiteHelper.COLUMN_LATITUDE, p.getLatitude());
		    values.put(MySQLiteHelper.COLUMN_LONGITUDE, p.getLongitude());
		    values.put(MySQLiteHelper.COLUMN_STREET1, p.getStreet1());
		    values.put(MySQLiteHelper.COLUMN_STREET2, p.getStreet2());
		    values.put(MySQLiteHelper.COLUMN_CITY, p.getCity());
		    values.put(MySQLiteHelper.COLUMN_STATE, p.getState());
		    values.put(MySQLiteHelper.COLUMN_ZIP, p.getZip());
		    values.put(MySQLiteHelper.COLUMN_COUNTRY, p.getCountry());
		    values.put(MySQLiteHelper.COLUMN_PATIENT_UUID, p.getPatientUuid());
		    values.put(MySQLiteHelper.COLUMN_ENCODE_UUID, p.getEncodedUuid());
		    values.put(MySQLiteHelper.COLUMN_FULLNAME, p.getFullName());
		    values.put(MySQLiteHelper.COLUMN_OPT_STATUS, p.getOptStatus());
		    values.put(MySQLiteHelper.COLUMN_IMAGE_URL, p.getImageUrl());
		    values.put(MySQLiteHelper.COLUMN_IMAGE_URL_FOR_FATCH, p.getImageUrlForFatch());
		    values.put(MySQLiteHelper.COLUMN_IMAGE_WIDTH, p.getImageWidth());
		    values.put(MySQLiteHelper.COLUMN_IMAGE_HEIGHT, p.getImageHeight());
		    values.put(MySQLiteHelper.COLUMN_YEARS_OLD, p.getYearsOld());
		    values.put(MySQLiteHelper.COLUMN_MIN_AGE, p.getMinAge());
		    values.put(MySQLiteHelper.COLUMN_MAX_AGE, p.getMaxAge());
		    values.put(MySQLiteHelper.COLUMN_ID, p.getId());
		    values.put(MySQLiteHelper.COLUMN_STATUS_SAHANA_UPDATED, p.getStatusSahanaUpdated());
		    values.put(MySQLiteHelper.COLUMN_STATUS_TRIAGE, p.getStatusTriage());
		    values.put(MySQLiteHelper.COLUMN_STATUS_REPORT, p.getStatusReport());
		    values.put(MySQLiteHelper.COLUMN_PEDS, p.getPeds());
		    values.put(MySQLiteHelper.COLUMN_ORG_NAME, p.getOrgName());
		    values.put(MySQLiteHelper.COLUMN_LAST_SEEN, p.getLastSeen());
		    values.put(MySQLiteHelper.COLUMN_COMMENTS, p.getComments());
		    values.put(MySQLiteHelper.COLUMN_GENDER, p.getGender());	    
		    values.put(MySQLiteHelper.COLUMN_HOSPITAL_ICON, p.getHospitalIcon());
		    values.put(MySQLiteHelper.COLUMN_MASS_CASUALTY_ID, p.getMassCasualtyId());
		    values.put(MySQLiteHelper.COLUMN_PHOTO_DATA, p.getPhotoData());

		    long insertId = database.insert(MySQLiteHelper.TABLE_NAME, null, values);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
		        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + insertId, null,
		        null, null, null);
		    cursor.moveToFirst();
		    Patient newP = cursorToPatient(cursor);
		    cursor.close();
		    return newP;
		  }

      public void updatePatient(Patient p){
          ContentValues values = new ContentValues();
          values.put(MySQLiteHelper.COLUMN_GIVENNAME, p.getGivengName());
          values.put(MySQLiteHelper.COLUMN_FAMILYNAME, p.getFamilyName());
		  values.put(MySQLiteHelper.COLUMN_PA, p.getPa());	// animal, version 7.3.6
		  values.put(MySQLiteHelper.COLUMN_BUDDY, p.getBuddy()); // animal, version 7.3.6
		  values.put(MySQLiteHelper.COLUMN_EVENT_NAME, p.getEventName());
          values.put(MySQLiteHelper.COLUMN_EVENT_SHORTNAME, p.getEventShortName());
          values.put(MySQLiteHelper.COLUMN_GIVENNAME, p.getGivengName());
          values.put(MySQLiteHelper.COLUMN_FAMILYNAME, p.getFamilyName());
          values.put(MySQLiteHelper.COLUMN_MOER_DETAILS, p.getMoreDetails());
          values.put(MySQLiteHelper.COLUMN_DATE_TIME_SENT, p.getDateTimeSent());
          values.put(MySQLiteHelper.COLUMN_LATITUDE, p.getLatitude());
          values.put(MySQLiteHelper.COLUMN_LONGITUDE, p.getLongitude());
          values.put(MySQLiteHelper.COLUMN_STREET1, p.getStreet1());
          values.put(MySQLiteHelper.COLUMN_STREET2, p.getStreet2());
          values.put(MySQLiteHelper.COLUMN_CITY, p.getCity());
          values.put(MySQLiteHelper.COLUMN_STATE, p.getState());
          values.put(MySQLiteHelper.COLUMN_ZIP, p.getZip());
          values.put(MySQLiteHelper.COLUMN_COUNTRY, p.getCountry());
          values.put(MySQLiteHelper.COLUMN_PATIENT_UUID, p.getPatientUuid());
          values.put(MySQLiteHelper.COLUMN_ENCODE_UUID, p.getEncodedUuid());
          values.put(MySQLiteHelper.COLUMN_FULLNAME, p.getFullName());
          values.put(MySQLiteHelper.COLUMN_OPT_STATUS, p.getOptStatus());
          values.put(MySQLiteHelper.COLUMN_IMAGE_URL, p.getImageUrl());
          values.put(MySQLiteHelper.COLUMN_IMAGE_URL_FOR_FATCH, p.getImageUrlForFatch());
          values.put(MySQLiteHelper.COLUMN_IMAGE_WIDTH, p.getImageWidth());
          values.put(MySQLiteHelper.COLUMN_IMAGE_HEIGHT, p.getImageHeight());
          values.put(MySQLiteHelper.COLUMN_YEARS_OLD, p.getYearsOld());
          values.put(MySQLiteHelper.COLUMN_MIN_AGE, p.getMinAge());
          values.put(MySQLiteHelper.COLUMN_MAX_AGE, p.getMaxAge());
          values.put(MySQLiteHelper.COLUMN_ID, p.getId());
          values.put(MySQLiteHelper.COLUMN_STATUS_SAHANA_UPDATED, p.getStatusSahanaUpdated());
          values.put(MySQLiteHelper.COLUMN_STATUS_TRIAGE, p.getStatusTriage());
          values.put(MySQLiteHelper.COLUMN_STATUS_REPORT, p.getStatusReport());
          values.put(MySQLiteHelper.COLUMN_PEDS, p.getPeds());
          values.put(MySQLiteHelper.COLUMN_ORG_NAME, p.getOrgName());
          values.put(MySQLiteHelper.COLUMN_LAST_SEEN, p.getLastSeen());
          values.put(MySQLiteHelper.COLUMN_COMMENTS, p.getComments());
          values.put(MySQLiteHelper.COLUMN_GENDER, p.getGender());
          values.put(MySQLiteHelper.COLUMN_HOSPITAL_ICON, p.getHospitalIcon());
          values.put(MySQLiteHelper.COLUMN_MASS_CASUALTY_ID, p.getMassCasualtyId());
          values.put(MySQLiteHelper.COLUMN_PHOTO_DATA, p.getPhotoData());

          database.update(MySQLiteHelper.TABLE_NAME, values, MySQLiteHelper.COLUMN_SERIALID + " = "
                  + p.getSerialId(), null);
      }

	  public Patient getPatient(long serialId) {
		    System.out.println("Lost person is searched with serialId: " + serialId);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
			        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + String.valueOf(serialId), null,
			        null, null, null);
		    cursor.moveToFirst();
		    Patient p = cursorToPatient(cursor);
		    cursor.close();
		    return p;
		  }

	  public Patient getPatient(String imageUrl, String webServer) {
		  Patient p = null;
		    Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
					  + " WHERE " + MySQLiteHelper.COLUMN_IMAGE_URL + " = '" + imageUrl + "'", null);
		    cursor.moveToFirst();
		    if (cursor.getCount() == 0){
		    	return null;
		    }
		    while (!cursor.isAfterLast()) {
			    p = new Patient(cursorToPatient(cursor), webServer);		
			    if (p.getPhotoData().isEmpty() == false){
			    	return p;
			    }
			    cursor.moveToNext();
		    }
		    cursor.close();
		    return p;
	  }

	  public void deletePatient(Patient p) {
		    long serialId = p.getSerialId();
		    System.out.println("Lost person is deleted with serialId: " + serialId);
		    database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_SERIALID
		        + " = " + serialId, null);
		  }

	  public void deleteAllPatients() {
		    System.out.println("Delete all patient files");
		    database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_SERIALID
			        + " > " + -1, null);
		  }

	  public void deleteAllDraftPatients() {
		    System.out.println("Delete all patient files in draft");
		    database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_STATUS_REPORT
			        + " = 'Draft'", null);
	  }

	  public void deleteAllCachePatients() {
		    System.out.println("Delete all patient files in cache");
		    database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_STATUS_REPORT
			        + " = 'Cache'", null);
	  }

	  public void deleteAllOutboxPatients() {
		    System.out.println("Delete all patient files in outbox");
		    database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_STATUS_REPORT
			        + " = 'Outbox'", null);
	  }

	  public void deleteAllSentPatients() {
		    System.out.println("Delete all patient files in sent box");
		    database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_STATUS_REPORT
			        + " = 'Sent'", null);
	  }

	  public void deleteAllSavedPatients() {
		    System.out.println("Delete all patient files in saved box");
		    database.delete(MySQLiteHelper.TABLE_NAME, MySQLiteHelper.COLUMN_STATUS_REPORT
			        + " = 'Saved'", null);
	  }

	  public List<Patient> getAllPatients() {
	    List<Patient> list = new ArrayList<Patient>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	      Patient patient = cursorToPatient(cursor);
	      list.add(patient);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return list;
	  }
	 

	  public List<Patient> getAllSavedPatients() {
		    List<Patient> list = new ArrayList<Patient>();

		    Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
					  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Saved'", null);
		    
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Patient patient = cursorToPatient(cursor);
		      list.add(patient);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return list;
	  }

	  public List<Patient> getAllDraftPatients() {
		    List<Patient> list = new ArrayList<Patient>();

		    Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
					  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Draft'", null);
		    
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Patient patient = cursorToPatient(cursor);
		      list.add(patient);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return list;
	  }

	  public List<Patient> getAllCachePatients() {
		    List<Patient> list = new ArrayList<Patient>();

		    Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
					  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Cache'", null);
		    
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Patient patient = cursorToPatient(cursor);
		      list.add(patient);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return list;
	  }

	  public List<Patient> getAllSentPatients() {
		    List<Patient> list = new ArrayList<Patient>();

		    Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
					  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Sent'", null);
		    
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Patient patient = cursorToPatient(cursor);
		      list.add(patient);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return list;
	  }

	  public List<Patient> getAllOutboxPatients() {
		    List<Patient> list = new ArrayList<Patient>();

		    Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
					  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Outbox'", null);
		    
		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		      Patient patient = cursorToPatient(cursor);
		      list.add(patient);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return list;
	  }
	  
	  public int countAllRecords() {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME, null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countSavedRecords(){
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
				  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Saved'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countSentRecords(){
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
				  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Sent'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countDraftRecords(){
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
				  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Draft'", null);
		  total = cursor.getCount();	
		  return total;
	  }
	  
	  public int countCacheRecords(){
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
				  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Cache'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countOutboxRecords(){
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME 
				  + " WHERE " + MySQLiteHelper.COLUMN_STATUS_REPORT + " = 'Outbox'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  private Patient cursorToPatient(Cursor cursor) {
	    Patient p = new Patient(WebServer.PL_NAME);
	    p.setSerialId(cursor.getLong(0));
	    p.setGivengName(cursor.getString(1));
	    p.setFamilyName(cursor.getString(2));
		p.setPa(cursor.getInt(3));
		p.setBuddy(cursor.getString(4));
    	p.setEventName(cursor.getString(5));
	    p.setEventShortName(cursor.getString(6));
	    p.setMoreDetails(cursor.getString(7));
	    p.setDateTimeSent(cursor.getString(8));
	    p.setLatitude(cursor.getString(9));
	    p.setLongitude(cursor.getString(10));
	    p.setStreet1(cursor.getString(11));
	    p.setStreet2(cursor.getString(12));
	    p.setCity(cursor.getString(13));
	    p.setState(cursor.getString(14));
	    p.setZip(cursor.getString(15));
	    p.setCountry(cursor.getString(16));
	    p.setPatientUuid(cursor.getString(17));
	    p.setEncodedUuid(cursor.getString(18));
	    p.setFullName(cursor.getString(19));
	    p.setOptStatus(cursor.getString(20));
	    p.setImageUrl(cursor.getString(21));
	    p.setImageUrlForFatch(cursor.getString(22));
	    p.setImageWidth(cursor.getString(23));
	    p.setImageHeight(cursor.getString(24));
	    p.setYearsOld(cursor.getString(25));
	    p.setMinAge(cursor.getString(26));
	    p.setMaxAge(cursor.getString(27));
	    p.setId(cursor.getString(28));
	    p.setStatusSahanaUpdated(cursor.getString(29));
	    p.setStatusTriage(cursor.getString(30));
	    p.setStatusReport(cursor.getString(31));
	    p.setPeds(cursor.getString(32));
	    p.setOrgName(cursor.getString(33));
	    p.setLastSeen(cursor.getString(34));
	    p.setComments(cursor.getString(35));
	    p.setGender(cursor.getString(36));
	    p.setHospitalIcon(cursor.getString(37));
	    p.setMassCasualtyId(cursor.getString(38));
	    p.setPhotoData(cursor.getString(39));

        // in case the full name is empty with space characters.
        String str = p.getFullName();
        str = str.replaceAll("\\s","");
        if (str.isEmpty()){
            p.setFullName(p.getGivengName() + " " + p.getFamilyName());
        }

	    return p;
	  }
}
