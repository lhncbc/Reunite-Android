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

import android.graphics.Bitmap;

public class ItemPatientSimpleView {
	private String patientUuid;
	private int idNumber;
	private String name;
	private String age;
	private String minAge;
	private String maxAge;
	private String gender;
	private String optStatus;
	private String statusSahanaUpdated;	
	private Bitmap photo;
	private boolean statusPhotoDownload;
	private String number;
	private String event;
	private String serialId;
	private int personAnimal;
	
	public String getPatientUuid() {
		return patientUuid;
	}
	public void setPatientUuid(String patientUuid) {
		this.patientUuid = patientUuid;
	}
	public Bitmap getPhoto() {
		return photo;
	}
	public void setPhoto(Bitmap photo){
		this.photo = photo;
	}
	
	public boolean getStatusPhotoDownload(){
		return statusPhotoDownload;
	}
	public void setStatusPhotoDownload(boolean statusPhotoDownload){
		this.statusPhotoDownload = statusPhotoDownload;
	}
	
	public int getIdNumber() {
		return idNumber;
	}
	public void setIdNumber(int idNumber) {
		this.idNumber = idNumber;
	}	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}	
	public String getMinAge() {
		return minAge;
	}
	public void setMinAge(String minAge) {
		this.minAge = minAge;
	}	
	public String getMaxAge() {
		return maxAge;
	}
	public void setMaxAge(String maxAge) {
		this.maxAge = maxAge;
	}	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}	
	
	public String getOptStatus() {
		return optStatus;
	}
	public void setOptStatus(String optStatus){
		this.optStatus = optStatus;
	}

	public String getStatusSahanaUpdated() {
		return statusSahanaUpdated;
	}
	public void setStatusSahanaUpdated(String statusSahanaUpdated){
		this.statusSahanaUpdated = statusSahanaUpdated;
	}
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}

	public String getEvent() {
		return event;
	}
	public void setEvent(String event) {
		this.event = event;
	}

	public String getSerialId() {
		return serialId;
	}
	public void setSerialId(String serialId) {
		this.serialId = serialId;
	}

	public int getPersonAnimal() {
		return personAnimal;
	}
	public void setPersonAnimal(int personAnimal) {
		this.personAnimal = personAnimal;
	}
}
