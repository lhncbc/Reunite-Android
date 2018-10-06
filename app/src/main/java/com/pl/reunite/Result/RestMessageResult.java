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

package com.pl.reunite.Result;

import android.content.Context;

public class RestMessageResult extends RestResult {
    private String records;

    private boolean eventEmail;
    private boolean eventPush;
    private boolean recordEmail;
    private boolean recordPush;
    private boolean adminEmail;
    private boolean adminPush;

    public RestMessageResult(Context c){
        super(c);
        records = "";
        eventEmail = false;
        eventPush = false;
        recordEmail = false;
        recordPush = false;
        adminEmail = false;
        adminPush = false;
    }

    public RestMessageResult(Context c, RestMessageResult r) {
        super(c);
        this.setErrorCode(r.getErrorCode());
        this.setErrorMessage(r.getErrorMessage());
        this.setTimeElapsed(r.getTimeElapsed());
        this.records = r.getRecords();

        this.eventEmail = r.isEventEmail();
        this.eventPush = r.isEventPush();
        this.recordEmail = r.isRecordEmail();
        this.recordPush = r.isRecordPush();
        this.adminEmail = r.isAdminEmail();
        this.adminPush = r.isAdminPush();
    }

    public String getRecords() {
        return records;
    }

    public void setRecords(String records) {
        this.records = records;
    }

    public void toDefault(){
        super.toDefault();
        records = "";

        eventEmail = false;
        eventPush = false;
        recordEmail = false;
        recordPush = false;
        adminEmail = false;
        adminPush = false;
    }


    public boolean isEventEmail() {
        return eventEmail;
    }

    public void setEventEmail(boolean eventEmail) {
        this.eventEmail = eventEmail;
    }

    public boolean isEventPush() {
        return eventPush;
    }

    public void setEventPush(boolean eventPush) {
        this.eventPush = eventPush;
    }

    public boolean isRecordEmail() {
        return recordEmail;
    }

    public void setRecordEmail(boolean recordEmail) {
        this.recordEmail = recordEmail;
    }

    public boolean isRecordPush() {
        return recordPush;
    }

    public void setRecordPush(boolean recordPush) {
        this.recordPush = recordPush;
    }

    public boolean isAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(boolean adminEmail) {
        this.adminEmail = adminEmail;
    }

    public boolean isAdminPush() {
        return adminPush;
    }

    public void setAdminPush(boolean adminPush) {
        this.adminPush = adminPush;
    }
}
