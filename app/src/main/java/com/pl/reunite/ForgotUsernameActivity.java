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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pl.reunite.Result.RestForgotUsernameResult;

/**
 * upgraded from api v24 to v34 by zli 10/24/2014
 * title says its all.
 */
public class ForgotUsernameActivity extends Activity implements View.OnClickListener{
    ReUnite app;
    String webServer = "";
    String soapAction = "";
    String nameSpace = "";
    String url = "";
    
	String errorCode = "";
    String errorMessage = "";
    EditText editEmailAddress;
	Button buttonSendUsername;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_username);
		
		app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

		webServer = app.getWebServer();

		Initialize();
	}

	private void Initialize() {
		editEmailAddress = (EditText) findViewById(R.id.editEmailAddress);
		buttonSendUsername = (Button) findViewById(R.id.buttonSendUsername);

		buttonSendUsername.setOnClickListener(this);		
	}

	public void onClick(View v) {
		switch (v.getId()) {
            case R.id.buttonSendUsername:
//                ForgotUsernameResult forgotUsernameResult = new ForgotUsernameResult();
                RestForgotUsernameResult restForgotUsernameResult = new RestForgotUsernameResult(ForgotUsernameActivity.this);
                WebServer ws = new WebServer(app.getWebServer()); // Add the argument in. Modified in version 7.1.3
                ws.setTokenStatus(app.getTokenStatus());
                ws.setToken(app.getToken());
                ws.setTokenAnonymous(app.getTokenAnonymous());
//                forgotUsernameResult = ws.forgotUsername(editEmailAddress.getText().toString());
                restForgotUsernameResult = ws.restForgotUsername(ForgotUsernameActivity.this, app.getTokenAnonymous(), editEmailAddress.getText().toString());
//			ResetUserPassword();
                if (restForgotUsernameResult.getErrorCode().equalsIgnoreCase("0") == true) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.email_address_is_sent))
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.succeed))
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    editEmailAddress.setText("");
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    // supporting multiple languages - start
                    ErrorMessage em = new ErrorMessage(this);
                    errorMessage = em.searchErrorMessage(restForgotUsernameResult.getErrorCode());
                    // end

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(errorMessage)
                            .setCancelable(false)
                            .setTitle(getResources().getString(R.string.error))
                            .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
        }
	}
}
