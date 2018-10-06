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

import com.pl.reunite.R;

/**
 * Created on 5/16/2017.
 */

public class RestResult {
    public static final String MY_ERROR_CODE = "-1";
    private String errorMessage;
    private String timeElapsed;
    private String errorCode;
    private Context c;

    public void toDefault(){
        errorCode = "";
        errorMessage = "";
        timeElapsed = "";
    }

    public RestResult(Context c){
        this.c = c;
        errorCode = "";
        errorMessage = "";
        timeElapsed = "";
    }

    public RestResult(String errorCode, String errorMessage, String timeElapsed, Context c){
        this.c = c;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.timeElapsed = timeElapsed;
    }

    public void setContext(Context c){this.c = c;}
    public Context getContext() {return c;}

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCodeAsInt() {
        int res = Integer.parseInt(errorCode);
        return res;
    }

    public String getTimeElapsed() {
        return timeElapsed;
    }

    public double getTimeElapsedAsDouble(){
        return Double.parseDouble(timeElapsed.toString());
    }

    public void setTimeElapsed(String timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String searchErrorMessage(){
        this.errorMessage = "";
        int code = getErrorCodeAsInt();
        switch (code) {
            case 0:
                errorMessage = "";
                break;
            case 1:
                errorMessage = c.getResources().getString(R.string.plusError1);
                break;
            case 2:
                errorMessage = c.getResources().getString(R.string.exceeded_authentication_failure_limit_);
                break;
            case 3:
                errorMessage = c.getResources().getString(R.string.user_account_is_inactive_);
                break;
            case 2000:
                errorMessage = c.getResources().getString(R.string.a_user_already_exists_with_);
                break;
            case 2001:
                errorMessage = c.getResources().getString(R.string.invalid_email_address);
                break;
            case 2002:
                errorMessage = c.getResources().getString(R.string.your_password_must_contain_a_number_etc);
                break;
//            case 2003:
//                errorMessage = "invalid captcha value";
//                break;
            case 2100:
                errorMessage = c.getResources().getString(R.string.missing_person_uuid);
                break;
            case 2101:
                errorMessage = c.getResources().getString(R.string.unknown); // document message: invalid type
                break;
            case 2200:
                errorMessage = c.getResources().getString(R.string.invalid_confirmation_code);
//                errorMessage = c.getResources().getString(R.string.unknown); // document message: invalid type
                break;
            case 3000:
                errorMessage = c.getResources().getString(R.string.invalid_preference_value);
                break;
            case 8000:
                errorMessage = c.getResources().getString(R.string.error_in_event);
                break;
            case 9000:
                errorMessage = c.getResources().getString(R.string.no_valid_token);
                break;
            case 9001:
                errorMessage = c.getResources().getString(R.string.no_valid_token); // document message: token cannot access event
                break;
            default:
                errorMessage = c.getResources().getString(R.string.unknown);
                break;
        }
        return errorMessage;
    }
}
