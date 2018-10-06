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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class for Event object.
 * All features of event are included.
 * Self contain web service calls.
 */

public class Event {
    public static final String DEFAULT_EVENT_NAME = "Test Exercise";
    public static final String DEFAULT_EVENT_SHORT_NAME = "test";
    public static final String DEFAULT_EVENT_DATE = "2008-08-15";
    public static final String DEFAULT_EVENT_LOCATION = "Bethesda, MD 20894, USA";

    // filters
	private String searchTerm = "";
    private boolean filterGenderComplex;
    private boolean filterGenderMale;
    private boolean filterGenderFemale;
    private boolean filterGenderUnknown;
    private boolean filterAgeChild;
    private boolean filterAgeAdult;
    private boolean filterAgeUnknown;
	private boolean filterStatusMissing;
	private boolean filterStatusAliveAndWell;
	private boolean filterStatusInjured;
	private boolean filterStatusDeceased;
	private boolean filterStatusUnknown;
	private boolean filterStatusFound;

	// animal
	private boolean filterSelSearchPerson;
	private boolean filterSelSearchAnimal;
	private boolean filterSelSearchBoth;

	// view settings
    private ViewSettings viewSettings;
    public void setViewSettings(ViewSettings viewSettings){this.viewSettings = viewSettings;}
    public ViewSettings getViewSettings(){return viewSettings;}

    public Filters generateFilters() {
        Filters filters = new Filters();

        filters.setMissing(filterStatusMissing);
        filters.setAliveAndWell(filterStatusAliveAndWell);
        filters.setInjured(filterStatusInjured);
        filters.setDeceased(filterStatusDeceased);
        filters.setStatusUnknown(filterStatusUnknown);
        filters.setFound(filterStatusFound);

        // animal
		filters.setSelSearchPerson(filterSelSearchPerson);
		filters.setSelSearchAnimal(filterSelSearchAnimal);
		filters.setSelSearchBoth(filterSelSearchBoth);

        filters.setComplex(filterGenderComplex);
        filters.setMale(filterGenderMale);
        filters.setFemale(filterGenderFemale);
        filters.setGenderUnknown(filterGenderUnknown);

        filters.setChild(filterAgeChild);
        filters.setAdult(filterAgeAdult);
        filters.setAgeUnknown(filterAgeUnknown);

        return filters;
    }

	private long serialId;
    private String webServer;
    
    public Event(String webServer){
    	searchTerm = "";
        filterGenderComplex = true;
        filterGenderMale = true;
        filterGenderFemale = true;
        filterGenderUnknown = true;
        filterAgeChild = true;
        filterAgeAdult = true;
        filterAgeUnknown = true;
    	filterStatusMissing = true;
    	filterStatusAliveAndWell = true;
    	filterStatusInjured = true;
    	filterStatusDeceased = true;
    	filterStatusUnknown = true;
    	filterStatusFound = true;

    	// search animal
		filterSelSearchPerson = false;
		filterSelSearchAnimal = false;
		filterSelSearchBoth = true;

    	strIncidentId ="";
    	strParentId ="";
    	strName ="";
    	strShortname ="";
    	strDate ="";
    	strType ="";
    	strLatitude ="";
    	strLongitude ="";
    	strStreet ="";
    	strGroup ="";
    	strClosed ="";
    	numberOfRecords ="";
    	
    	serialId = 0;
    	this.webServer = webServer;
    }
    
    public Event(Event e, String webServer){
    	this.searchTerm = e.getSearchTerm();
        this.filterGenderComplex = e.getFilterGenderComplex();

        // animal - start
		this.filterSelSearchPerson = e.isFilterSelSearchPerson();
		this.filterSelSearchAnimal = e.isFilterSelSearchAnimal();
		this.filterSelSearchBoth = e.isFilterSelSearchBoth();
		// animal - end

        this.filterGenderMale = e.getFilterGenderMale();
        this.filterGenderFemale = e.getFilterGenderFemale();
        this.filterGenderUnknown = e.getFilterGenderUnknown();
        this.filterAgeChild = e.getFilterAgeChild();
        this.filterAgeAdult = e.getFilterAgeAdult();
        this.filterAgeUnknown = e.getFilterAgeUnknown();
    	this.filterStatusMissing = e.getFilterStatusMissing();
    	this.filterStatusAliveAndWell = e.getFilterStatusAliveAndWell();
    	this.filterStatusInjured = e.getFilterStatusInjured();
    	this.filterStatusDeceased = e.getFilterStatusDeceased();
    	this.filterStatusUnknown = e.getFilterStatusUnknown();
    	this.filterStatusFound = e.getFilterStatusFound();

        this.strIncidentId = e.getIncidentId();
        this.strParentId = e.getParentId();
        this.strName = e.getName();
        this.strShortname = e.getShortName();
        this.strDate = e.getDate();
        this.strType = e.getType();
        this.strLatitude = e.getLatitude();
        this.strLongitude = e.getLongitude();
        this.strStreet = e.getStreet();
        this.strGroup = e.getGroup();
        this.strClosed = e.getClosed();
        this.numberOfRecords = e.getNumberOfRecords();
        
        this.serialId = e.getSerialId();
        this.webServer = webServer;
    }
    
	public long getSerialId() {
		return serialId;
	}
	public void setSerialId(long serialId) {
		this.serialId = serialId;
	}

	public String getSearchTerm() {
    	return searchTerm;
    }
    public void setSearchTerm(String searchTerm) {
    	this.searchTerm = searchTerm;
    }

    public String getWebServer() {
    	return webServer;
    }
    public void setWebServer(String webServer) {
    	this.webServer = webServer;
    }

    public boolean getFilterGenderComplex() {
    	return filterGenderComplex;
    }
    public void setFilterGenderComplex(boolean filterGenderComplex) {
    	this.filterGenderComplex = filterGenderComplex;
    }

    // animal - start
	public boolean isFilterSelSearchPerson() {
		return filterSelSearchPerson;
	}
	public void setFilterSelSearchPerson(boolean filterSelSearchPerson) {
		this.filterSelSearchPerson = filterSelSearchPerson;
	}
	public boolean isFilterSelSearchAnimal() {
		return filterSelSearchAnimal;
	}
	public void setFilterSelSearchAnimal(boolean filterSelSearchAnimal) {
		this.filterSelSearchAnimal = filterSelSearchAnimal;
	}
	public boolean isFilterSelSearchBoth() {
		return filterSelSearchBoth;
	}
	public void setFilterSelSearchBoth(boolean filterSelSearchBoth) {
		this.filterSelSearchBoth = filterSelSearchBoth;
	}
	// animal - end

    public boolean getFilterGenderMale() {
    	return filterGenderMale;
    }
    public void setFilterGenderMale(boolean filterGenderMale) {
    	this.filterGenderMale = filterGenderMale;
    }
    public boolean getFilterGenderFemale() {
    	return filterGenderFemale;
    }
    public void setFilterGenderFemale(boolean filterGenderFemale) {
    	this.filterGenderFemale = filterGenderFemale;
    }
    public boolean getFilterGenderUnknown() {
    	return filterGenderUnknown;
    }
    public void setFilterGenderUnknown(boolean filterGenderUnknown) {
    	this.filterGenderUnknown = filterGenderUnknown;
    }
    public boolean getFilterAgeChild() {
    	return filterAgeChild;
    }
    public void setFilterAgeChild(boolean filterAgeChild) {
    	this.filterAgeChild = filterAgeChild;
    }
    public boolean getFilterAgeAdult() {
    	return filterAgeAdult;
    }
    public void setFilterAgeAdult(boolean filterAgeAdult) {
    	this.filterAgeAdult = filterAgeAdult;
    }
    public boolean getFilterAgeUnknown() {
    	return filterAgeUnknown;
    }
    public void setFilterAgeUnknown(boolean filterAgeUnknown) {
    	this.filterAgeUnknown = filterAgeUnknown;
    }
    public boolean getFilterStatusMissing() {
    	return filterStatusMissing;
    }
    public void setFilterStatusMissing(boolean filterStatusMissing) {
    	this.filterStatusMissing = filterStatusMissing;
    }
    public boolean getFilterStatusAliveAndWell() {
    	return filterStatusAliveAndWell;
    }
    public void setFilterStatusAliveAndWell(boolean filterStatusAliveAndWell) {
    	this.filterStatusAliveAndWell = filterStatusAliveAndWell;
    }
    public boolean getFilterStatusInjured() {
    	return filterStatusInjured;
    }
    public void setFilterStatusInjured(boolean filterStatusInjured) {
    	this.filterStatusInjured = filterStatusInjured;
    }
    public boolean getFilterStatusDeceased() {
    	return filterStatusDeceased;
    }
    public void setFilterStatusDeceased(boolean filterStatusDeceased) {
    	this.filterStatusDeceased = filterStatusDeceased;
    }
    public boolean getFilterStatusUnknown() {
    	return filterStatusUnknown;
    }
    public void setFilterStatusUnknown(boolean filterStatusUnknown) {
    	this.filterStatusUnknown = filterStatusUnknown;
    }
    public boolean getFilterStatusFound() {
    	return filterStatusFound;
    }
    public void setFilterStatusFound(boolean filterStatusFound) {
    	this.filterStatusFound = filterStatusFound;
    }

	private String strIncidentId;
	public String getIncidentId() {
		return strIncidentId;
	}
	public void setIncidentId(String nIncidentId) {
		this.strIncidentId = strIncidentId;
	}
	
	private String strParentId;
	public String getParentId() {
		return strParentId;
	}
	public void setParentId(String strParentId) {
		this.strParentId = strParentId;
	}

	private String strName;
	public String getName() {
		return strName;
	}
	public void setName(String strName) {
		this.strName = strName;
	}
	
	private String strShortname;
	public String getShortName() {
		return strShortname;
	}
	public void setShortName(String strShortname) {
		this.strShortname = strShortname;
	}
	
	private String strDate;
	public String getDate() {
		return strDate;
	}
	public void setDate(String strDate) {
		this.strDate = strDate;
	}
	
	private String strType;
	public String getType() {
		return strType;
	}
	public void setType(String strType) {
		this.strType = strType;
	}

	private String strLatitude;
	public String getLatitude() {
		return strLatitude;
	}
	public void setLatitude(String strLatitude) {
		this.strLatitude = strLatitude;
	}
	
	private String strLongitude;
	public String getLongitude() {
		return strLongitude;
	}
	public void setLongitude(String strLongitude) {
		this.strLongitude = strLongitude;
	}

	private String strStreet;
	public String getStreet() {
		return strStreet;
	}
	public void setStreet(String strStreet) {
		this.strStreet = strStreet;
	}
	
	private String strGroup;
	public String getGroup() {
		return strGroup;
	}
	public void setGroup(String strGroup) {
		this.strGroup = strGroup;
	}
	
	private String strClosed;
	public String getClosed() {
		return strClosed;
	}
	public void setClosed(String strClosed) {
		this.strClosed = strClosed;
	}
	
	private String numberOfRecords;

	public String getNumberOfRecords() {
		return numberOfRecords;
	}
	public void setNumberOfRecords(String numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}

    /**
     * This is the only web service call from outside of class WebService, because the historical
     * reason.
     */

    public void toDefault(){
        setName(this.DEFAULT_EVENT_NAME);
        setShortName(this.DEFAULT_EVENT_SHORT_NAME);
        setDate(this.DEFAULT_EVENT_DATE);
        setStreet(this.DEFAULT_EVENT_LOCATION);
        setPersonAnimal();
    }

	private void setPersonAnimal() {
    	filterSelSearchPerson = false;
    	filterSelSearchAnimal = false;
    	filterSelSearchBoth = true;
	}

	public void JsonObjectToEvent(JSONObject o){
        try {
            setGroup(o.getString("group").toString());

            /**
             * getEventData to replace getEventList
             * incident id is removed
             * version 7.2.8 version code 7020801
             */
            // start
            setIncidentId("0");
//				e.setIncidentId(o.getString("incident_id").toString());
            // end

            // take parent_id out. version 7.1.6
//				e.setParentId(o.getString("parent_id").toString());
            setParentId("");
            setName(o.getString("name").toString());
            setShortName(o.getString("shortname").toString());
            setDate(o.getString("date").toString());
            setType(o.getString("type").toString());
            setClosed(o.getString("closed").toString());
            setLatitude(o.getString("latitude").toString());
            setLongitude(o.getString("longitude").toString());

            /**
             * getEventData to replace getEventList
             * incident id is removed
             * version 7.2.8 version code 7020801
             */
            // start
            setStreet("");
//				e.setStreet(o.getString("street").toString());
            // end
        }
        catch (JSONException e){
            e.getStackTrace();
        }
    }
}
