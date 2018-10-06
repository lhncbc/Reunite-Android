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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created on 4/9/14.
 * This file is to record the changing history.
 */
public class FutureDevelopmentActivity extends Activity {
    ReUnite app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.future_development);

        app = (ReUnite)getApplication();
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        final FutureDevelopmentDialog futureDevelopment = new FutureDevelopmentDialog(this);
        final WebView webView = (WebView) findViewById(R.id.webView1);
        webView.loadDataWithBaseURL(null, futureDevelopment.getHTML(), "text/html", "utf-8", null);
    }

    // write a html file
    public void writeHTHL(String htmlString){
        if (!htmlString.isEmpty()){
            return;
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(this.openFileOutput("future_development.html", Context.MODE_PRIVATE));
            outputStreamWriter.write(htmlString);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    // Menu sections
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear(); // Clear the menu first
        /* Add the menu items */
        MenuInflater inflater = getMenuInflater();
        if (app.getDeveloper() == true){
            inflater.inflate(R.menu.future_development_menu, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemSaveHtml:
                generateHtml();
                break;
            default:
                break;
        }
        return true;
    }

    public void generateHtml(){
        FutureDevelopmentDialog futureDevelopment = new FutureDevelopmentDialog(this);
        String html = futureDevelopment.getHTML();
        if (html.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.html_file_is_empty), Toast.LENGTH_SHORT).show();
            return;
        }
        emailHTHL(html);
    }

    // Email to us
    private void emailHTHL(String html) {
        // Send email.
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setType("plain/text");
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"removed@gmail.com".toString()});
        email.putExtra(Intent.EXTRA_SUBJECT, "Future Developments");
        email.putExtra(Intent.EXTRA_TEXT, html);
        try {
            startActivity(Intent.createChooser(email, getResources().getString(R.string.sending_email)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, getResources().getString(R.string.there_is_no_email_client_installed), Toast.LENGTH_SHORT).show();
        }
    }
}
