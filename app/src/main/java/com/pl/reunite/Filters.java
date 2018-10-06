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


import org.json.JSONObject;

/**
 * class for filters which is to narrow the search on specialized defined.
 */
public class Filters {
	final static int SEARCH_PERSON = 0;
	final static int SEARCH_ANIMATION = 1;
	final static int SEARCH_BOTH = 2;

	private boolean male;
	private boolean female;
	private boolean complex;
	private boolean genderUnknown;	
	private boolean adult;
	private boolean child;
	private boolean ageUnknown;
	private boolean missing;
	private boolean aliveAndWell;
	private boolean injured;
	private boolean deceased;
	private boolean statusUnknown;
	private boolean found;

	// animal search
	private boolean selSearchPerson;
	private boolean selSearchAnimal;
	private boolean selSearchBoth;
	
	Filters() {
		male = true;
		female = true;
		complex = true;
		genderUnknown = true;
		adult = true;
		child = true;
		ageUnknown = true;
		missing = true;
		aliveAndWell = true;
		injured = true;
		deceased = true;
		statusUnknown = true;
		found = true;

		selSearchPerson = false;
		selSearchAnimal = false;
		selSearchBoth = true;
	}

	Filters(Filters o) {
		this.male = o.getMale();
		this.female = o.getFemale();
		this.complex = o.getComplex();
		this.genderUnknown = o.getGenderUnknown();
		this.adult = o.getAdult();
		this.child = o.getChild();
		this.ageUnknown = o.getAgeUnknown();
		this.missing = o.getMissing();
		this.aliveAndWell = o.getAliveAndWell();
		this.injured = o.getInjured();
		this.deceased = o.getDeceased();
		this.statusUnknown = o.getStatusUnknown();
		this.found = o.getFound();

		selSearchPerson = o.isSelSearchPerson();
		selSearchAnimal = o.isSelSearchAnimal();
		selSearchBoth = o.isSelSearchBoth();
	}
	
	public void setDefaults() {
		male = true;
		female = true;
		complex = true;
		genderUnknown = true;
		adult = true;
		child = true;
		ageUnknown = true;
		missing = true;
		aliveAndWell = true;
		injured = true;
		deceased = true;
		statusUnknown = true;
		found = true;

		selSearchPerson = false;
		selSearchAnimal = false;
		selSearchBoth = true;
	}
	
	public boolean getMale(){
		return male;
	}
	public void setMale(boolean male){
		this.male = male;
	}
	
	public boolean getFemale(){
		return female;
	}
	public void setFemale(boolean female){
		this.female = female;
	}
	
	public boolean getComplex(){
		return complex;
	}
	public void setComplex(boolean complex){
		this.complex = complex;
	}

	public boolean getGenderUnknown(){
		return genderUnknown;
	}
	public void setGenderUnknown(boolean genderUnknown){
		this.genderUnknown = genderUnknown;
	}
	
	public boolean getAdult(){
		return adult;
	}
	public void setAdult(boolean adult){
		this.adult = adult;
	}

	public boolean getChild(){
		return child;
	}
	public void setChild(boolean child){
		this.child = child;
	}

	public boolean getAgeUnknown(){
		return ageUnknown;
	}
	public void setAgeUnknown(boolean ageUnknown){
		this.ageUnknown = ageUnknown;
	}

	public boolean getMissing(){
		return missing;
	}
	public void setMissing(boolean missing){
		this.missing = missing;
	}

	public boolean getAliveAndWell(){
		return aliveAndWell;
	}
	public void setAliveAndWell(boolean aliveAndWell){
		this.aliveAndWell = aliveAndWell;
	}

	public boolean getInjured(){
		return injured;
	}
	public void setInjured(boolean injured){
		this.injured = injured;
	}

	public boolean getDeceased(){
		return deceased;
	}
	public void setDeceased(boolean deceased){
		this.deceased = deceased;
	}

	public boolean getStatusUnknown(){
		return statusUnknown;
	}
	public void setStatusUnknown(boolean statusUnknown){
		this.statusUnknown = statusUnknown;
	}

	public boolean getFound(){
		return found;
	}
	public void setFound(boolean found){
		this.found = found;
	}

	// search animal
	public boolean isSelSearchBoth() {
		return selSearchBoth;
	}
	public void setSelSearchBoth(boolean selSearchBoth) {
		this.selSearchBoth = selSearchBoth;
	}

	public boolean isSelSearchAnimal() {
		return selSearchAnimal;
	}
	public void setSelSearchAnimal(boolean selSearchAnimal) {
		this.selSearchAnimal = selSearchAnimal;
	}

	public boolean isSelSearchPerson() {
		return selSearchPerson;
	}
	public void setSelSearchPerson(boolean selSearchPerson) {
		this.selSearchPerson = selSearchPerson;
	}

	// JSONPATIENT1 FORMAT - added in version 9.0.0
    public static JSONObject toJSON(Filters filters, ViewSettings viewSettings){
        JSONObject json = new JSONObject();
        try {
        	if (filters.isSelSearchPerson() == true){
				json.put("personAnimal", SEARCH_PERSON);
			}
			else if (filters.isSelSearchAnimal() == true){
				json.put("personAnimal", SEARCH_ANIMATION);
			}
			else {
				json.put("personAnimal", SEARCH_BOTH);
			}

            json.put("statusMissing", filters.getMissing());
            json.put("statusAlive", filters.getAliveAndWell());
            json.put("statusInjured", filters.getInjured());
            json.put("statusDeceased", filters.getDeceased());
            json.put("statusUnknown", filters.getStatusUnknown());
            json.put("statusFound", filters.getFound());

            json.put("genderMale", filters.getMale());
            json.put("genderFemale", filters.getFemale());
            json.put("genderComplex", filters.getComplex());
            json.put("genderUnknown", filters.getGenderUnknown());

            json.put("ageChild", filters.getChild());
            json.put("ageAdult", filters.getAdult());
            json.put("ageUnknown", filters.getAgeUnknown());

            if (viewSettings.getPhotoSel() == ViewSettings.PHOTO_ONLY){
                json.put("hasImage", true);
            }
            else if (viewSettings.getPhotoSel() == ViewSettings.BOTH){
                json.put("hasImage", false);
            }
            else {
                 json.put("hasImage", false);
            }
        }
        catch (Exception ex){
			return null;
        }
        return json;
    }
}
