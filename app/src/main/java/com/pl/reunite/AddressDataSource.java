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

public class AddressDataSource {
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { 
			  MySQLiteHelper.COLUMN_SERIALID,
			  MySQLiteHelper.COLUMN_STREET1,
			  MySQLiteHelper.COLUMN_STREET2,
			  MySQLiteHelper.COLUMN_CITY,
			  MySQLiteHelper.COLUMN_STATE,
			  MySQLiteHelper.COLUMN_ZIP,
			  MySQLiteHelper.COLUMN_COUNTRY
	  };
	  
	  private PatientAddress patientAddress;
	  public PatientAddress getPatientAddress(){
		  return patientAddress;
	  }
	  public void setPatientAddress(PatientAddress patientAddress){
		  this.patientAddress = patientAddress;
	  }

	  public AddressDataSource(Context context) {
	    dbHelper = new MySQLiteHelper(context);
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public PatientAddress createAddress(String street1, String street2, String city, String state, String zip, String country) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_STREET1, street1);
	    values.put(MySQLiteHelper.COLUMN_STREET2, street2);
	    values.put(MySQLiteHelper.COLUMN_CITY, city);
	    values.put(MySQLiteHelper.COLUMN_STATE, state);
	    values.put(MySQLiteHelper.COLUMN_ZIP, zip);
	    values.put(MySQLiteHelper.COLUMN_COUNTRY, country);
	    	    
	    long insertId = database.insert(MySQLiteHelper.TABLE_NAME_ADDRESS, null, values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_ADDRESS,
	        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    PatientAddress newP = cursorToAddress(cursor);
	    cursor.close();
	    return newP;
	  }

	  public PatientAddress createAddress(PatientAddress p) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_STREET1, p.getStreet1());
		    values.put(MySQLiteHelper.COLUMN_STREET2, p.getStreet2());
		    values.put(MySQLiteHelper.COLUMN_CITY, p.getCity());
		    values.put(MySQLiteHelper.COLUMN_STATE, p.getState());
		    values.put(MySQLiteHelper.COLUMN_ZIP, p.getZip());
		    values.put(MySQLiteHelper.COLUMN_COUNTRY, p.getCountry());

		    long insertId = database.insert(MySQLiteHelper.TABLE_NAME_ADDRESS, null, values);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_ADDRESS,
		        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + insertId, null,
		        null, null, null);
		    cursor.moveToFirst();
		    PatientAddress newP = cursorToAddress(cursor);
		    cursor.close();
		    return newP;
		  }

	  public PatientAddress getAddress(long serialId) {
		    System.out.println("Address is searched with serialId: " + serialId);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_ADDRESS,
			        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + String.valueOf(serialId), null,
			        null, null, null);
		    cursor.moveToFirst();
		    PatientAddress p = cursorToAddress(cursor);
		    cursor.close();
		    return p;
		  }

	  public void deleteAddress(PatientAddress p) {
		    long serialId = p.getSerialId();
		    System.out.println("Address is deleted with serialId: " + serialId);
		    database.delete(MySQLiteHelper.TABLE_NAME_ADDRESS, MySQLiteHelper.COLUMN_SERIALID
		        + " = " + serialId, null);
		  }

	  public void deleteAllAddresses() {
		    System.out.println("Delete all address files");
		    database.delete(MySQLiteHelper.TABLE_NAME_ADDRESS, MySQLiteHelper.COLUMN_SERIALID
			        + " > " + -1, null);
		  }

	  public List<PatientAddress> getAllAddress() {
	    List<PatientAddress> list = new ArrayList<PatientAddress>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_ADDRESS,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	PatientAddress patientAddress = cursorToAddress(cursor);
	      list.add(patientAddress);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return list;
	  }

	  public List<PatientAddress> getAllAddressDesc() {
		    List<PatientAddress> list = new ArrayList<PatientAddress>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_ADDRESS,
		        allColumns, null, null, null, null, MySQLiteHelper.COLUMN_SERIALID + " DESC");

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	PatientAddress patientAddress = cursorToAddress(cursor);
		      list.add(patientAddress);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return list;
		  }

	  public int countAllRecords() {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_ADDRESS, null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countAllRecordsStreet1(String street1) {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_ADDRESS + " WHERE " + MySQLiteHelper.COLUMN_STREET1 + " = \'" + street1 + "\'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countAllRecordsStreet2(String street2) {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_ADDRESS + " WHERE " + MySQLiteHelper.COLUMN_STREET2 + " = \'" + street2 + "\'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countAllRecordsCity(String city) {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_ADDRESS + " WHERE " + MySQLiteHelper.COLUMN_CITY + " = \'" + city + "\'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countAllRecordsState(String state) {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_ADDRESS + " WHERE " + MySQLiteHelper.COLUMN_STATE + " = \'" + state + "\'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countAllRecordsZip(String zip) {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_ADDRESS + " WHERE " + MySQLiteHelper.COLUMN_ZIP + " = \'" + zip + "\'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  public int countAllRecordsCountry(String country) {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_ADDRESS + " WHERE " + MySQLiteHelper.COLUMN_COUNTRY + " = \'" + country + "\'", null);
		  total = cursor.getCount();	
		  return total;
	  }

	  private PatientAddress cursorToAddress(Cursor cursor) {
		PatientAddress p = new PatientAddress();
	    p.setSerialId(cursor.getLong(0));
	    p.setStreet1(cursor.getString(1));
	    p.setStreet2(cursor.getString(2));
	    p.setCity(cursor.getString(3));
	    p.setState(cursor.getString(4));
	    p.setZip(cursor.getString(5));
	    p.setCountry(cursor.getString(6));
	    
	    return p;
	  }
}
