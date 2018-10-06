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

public class ViewSettings {
	public static final int BOTH = 1;
	public static final int PHOTO_ONLY = 2;
	public static final int NO_PHOTO = 3;
	
	public static final int PAGE_SIZE_5 = 5;
	public static final int PAGE_SIZE_10 = 10;
	public static final int PAGE_SIZE_15 = 15;
	public static final int PAGE_SIZE_20 = 20;
	public static final int PAGE_SIZE_25 = 25;
	public static final int PAGE_SIZE_30 = 30;
	public static final int PAGE_SIZE_35 = 35;
    public static final int PAGE_SIZE_40 = 40;
    public static final int PAGE_SIZE_MAXIMUM = 100000000;

    public static final String SORT_SIMILARITY_DESC = "similarity desc";

    public static final int IMAGE_SEARCH = 1;

    ViewSettings(){
		photoSel = 0;
		pageSize = 0;
        isImageSearch = 0;
        encodedImage = "";
        sortBy = "";
	}
	
	ViewSettings(ViewSettings v){
		this.photoSel = v.photoSel;
		this.pageSize = v.pageSize;
        this.isImageSearch = v.getIsImageSearch();
        this.encodedImage = v.getEncodedImage();
        this.sortBy = v.getSortBy();
	}
	
	public void SetToDefault(){
		photoSel = BOTH;
		pageSize = PAGE_SIZE_10;
        isImageSearch = 0;
        encodedImage = "";
        sortBy = "";
	}
	
	public boolean isDifferent(ViewSettings v){
		if (this.photoSel != v.photoSel){
			return true;
		}
		if (this.pageSize != v.pageSize){
			return true;
		}
		return false;
	}
	
	private int photoSel;
	public int getPhotoSel() {
		return photoSel;
	}
	public void setPhotoSel(int photoSel){
		this.photoSel = photoSel;
	}
	
	private int pageSize;
	public int getPageSize(){
		return pageSize;
	}
	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}

    private int isImageSearch;
    public void setIsImageSearch(int isImageSearch){this.isImageSearch = isImageSearch;}
    public int getIsImageSearch(){return this.isImageSearch;}

    private String encodedImage;
    public void setEncodedImage(String encodedImage){this.encodedImage = encodedImage;}
    public String getEncodedImage(){return this.encodedImage;}

    private String sortBy;
    public String getSortBy() {
        return sortBy;
    }
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
}
