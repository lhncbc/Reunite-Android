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
 * this class take care of all the transactions in between Event and database.
 * such as create an event, update an event, etc.
 */
public class EventDataSource {
	  private SQLiteDatabase database;
	  private MySQLiteHelper dbHelper;
	  private String[] allColumns = { 
			  MySQLiteHelper.COLUMN_SERIALID,
			  MySQLiteHelper.COLUMN_EVENT_NAME,
			  MySQLiteHelper.COLUMN_EVENT_SHORTNAME,
			  MySQLiteHelper.COLUMN_DATE,
			  MySQLiteHelper.COLUMN_STREET,
			  MySQLiteHelper.COLUMN_TYPE,
			  MySQLiteHelper.COLUMN_STATUS
	  };
	  
	  private String webServer = "";
	  	  	  
	  private Event event;
	  public Event getEvent(){
		  return event;
	  }
	  public void setEvent(Event event){
		  this.event = event;
	  }

	  public EventDataSource(Context context, String webServer) {
	    dbHelper = new MySQLiteHelper(context);
	    this.webServer = webServer;
	  }

	  public void open() throws SQLException {
	    database = dbHelper.getWritableDatabase();
	  }

	  public void close() {
	    dbHelper.close();
	  }

	  public Event createEvent(String eventName, String eventShortName, String date, String street, String type, String status) {
	    ContentValues values = new ContentValues();
	    values.put(MySQLiteHelper.COLUMN_EVENT_NAME, eventName);
	    values.put(MySQLiteHelper.COLUMN_EVENT_SHORTNAME, eventShortName);
	    values.put(MySQLiteHelper.COLUMN_DATE, date);
	    values.put(MySQLiteHelper.COLUMN_STREET, street);
		  values.put(MySQLiteHelper.COLUMN_TYPE, type);
		  values.put(MySQLiteHelper.COLUMN_STATUS, status);

	    long insertId = database.insert(MySQLiteHelper.TABLE_NAME_EVENT, null, values);
	    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_EVENT,
	        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Event e = cursorToEvent(cursor);
	    cursor.close();
	    return e;
	  }

	  public Event createEvent(Event e) {
		    ContentValues values = new ContentValues();
		    values.put(MySQLiteHelper.COLUMN_EVENT_NAME, e.getName());
		    values.put(MySQLiteHelper.COLUMN_EVENT_SHORTNAME, e.getShortName());
		    values.put(MySQLiteHelper.COLUMN_DATE, e.getDate());
		    values.put(MySQLiteHelper.COLUMN_STREET, e.getStreet());
		  values.put(MySQLiteHelper.COLUMN_TYPE, e.getType());
		  values.put(MySQLiteHelper.COLUMN_STATUS, e.getClosed());

		    long insertId = database.insert(MySQLiteHelper.TABLE_NAME_EVENT, null, values);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_EVENT,
		        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + insertId, null,
		        null, null, null);
		    cursor.moveToFirst();
		    Event newE = cursorToEvent(cursor);
		    cursor.close();
		    return newE;
		  }

	  public Event getEvent(long serialId) {
		    System.out.println("Event is searched with serialId: " + serialId);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_EVENT,
			        allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + Long.toString(serialId), null,
			        null, null, null);
		    cursor.moveToFirst();
		    Event e = cursorToEvent(cursor);
		    cursor.close();
		    return e;
		  }

	  public Event getEvent(String eventName) {
		    System.out.println("Event is searched with name: " + eventName);
		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_EVENT,
			        allColumns, MySQLiteHelper.COLUMN_EVENT_NAME + " = " + eventName, null,
			        null, null, null);
		    cursor.moveToFirst();
		    Event e = cursorToEvent(cursor);
		    cursor.close();
		    return e;
		  }

	  public void deleteEvent(Event e) {
		    long serialId = e.getSerialId();
		    System.out.println("Event is deleted with serialId: " + serialId);
		    database.delete(MySQLiteHelper.TABLE_NAME_EVENT, MySQLiteHelper.COLUMN_SERIALID
		        + " = " + serialId, null);
		  }

	  public void deleteAllEvent() {
		    System.out.println("Delete all events");
		    database.delete(MySQLiteHelper.TABLE_NAME_EVENT, MySQLiteHelper.COLUMN_SERIALID
			        + " > " + -1, null);
		  }

	  public List<Event> getAllEvent() {
	    List<Event> list = new ArrayList<Event>();

	    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_EVENT,
	        allColumns, null, null, null, null, null);

	    cursor.moveToFirst();
	    while (!cursor.isAfterLast()) {
	    	Event event = cursorToEvent(cursor);
	      list.add(event);
	      cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return list;
	  }

	  public List<Event> getAllEventDesc() {
		    List<Event> list = new ArrayList<Event>();

		    Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_EVENT,
		        allColumns, null, null, null, null, MySQLiteHelper.COLUMN_SERIALID + " DESC");

		    cursor.moveToFirst();
		    while (!cursor.isAfterLast()) {
		    	Event event = cursorToEvent(cursor);
		      list.add(event);
		      cursor.moveToNext();
		    }
		    // Make sure to close the cursor
		    cursor.close();
		    return list;
		  }

	  public int countAllRecords() {
		  int total = 0;
		  Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_EVENT, null);
		  total = cursor.getCount();	
		  return total;
	  }

	  private Event cursorToEvent(Cursor cursor) {
		Event e = new Event(WebServer.PL_NAME);
	    e.setSerialId(cursor.getLong(0));
	    e.setName(cursor.getString(1));
	    e.setShortName(cursor.getString(2));
	    e.setDate(cursor.getString(3));
	    e.setStreet(cursor.getString(4));
		e.setType(cursor.getString(5));
		e.setClosed(cursor.getString(6));
	    return e;
	  }
}
