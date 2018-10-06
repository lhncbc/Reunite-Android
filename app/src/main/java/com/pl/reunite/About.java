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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import java.util.ArrayList;

public class About extends Activity {
	private static final String ABOUT_REUNITE = "ReUnite®";
	private static final String BURDEN_AND_PRIVACY = "Burden and Privacy Statement";
	private static final String ABOUT_LPF = "Lost Person Finder";
	private static final String ABOUT_NLM = "National Library of Medicine";
	private static final String ABOUT_NIH = "National Institutes of Health";
	private static final String ABOUT_HHS = "U.S. Department of Health & Human Services";
	private static final String ABOUT_NCMEC = "National Center for Missing and Exploited Children";

	protected static final ArrayList<ItemAboutView> image_details = null;
	ListView lvAboutEx;
	String strAboutCompanies[];
	ItemAboutListBaseAdapter adapterEx;

	ReUnite app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);

		app = ((ReUnite) this.getApplication());

		InitializeEx();
	}

	private void InitializeEx() {
		strAboutCompanies = new String[] {
				ABOUT_REUNITE,
				BURDEN_AND_PRIVACY,
//				"Privacy Statement",
//				"Burden Statement",
				ABOUT_LPF,
				ABOUT_NLM,
				ABOUT_NIH,
				ABOUT_HHS,
				ABOUT_NCMEC
		};
		
		ArrayList<ItemAboutView> image_details = GetSearchResults();
        lvAboutEx = (ListView) findViewById(R.id.lvAboutEx);
        lvAboutEx.setAdapter(new ItemAboutListBaseAdapter(About.this, image_details));
        
        lvAboutEx.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvAboutEx.getItemAtPosition(position);
        		ItemAboutView obj_itemDetails = (ItemAboutView)o;
        		String strName = obj_itemDetails.getName();
        		
    			if (strName.equalsIgnoreCase(ABOUT_REUNITE) == true){
    				AboutReUnite();
    			}
				else if (strName.equalsIgnoreCase(BURDEN_AND_PRIVACY) == true){
					burdenAndPrivacyStatement();
				}
				/*
    			else if (strName.equalsIgnoreCase("Privacy Statement") == true){
    				PrivacyStatement();
    			}	
    			else if (strName.equalsIgnoreCase("Burden Statement") == true){
    				BurdenStatement();
    			}
    			*/
    			else if (strName.equalsIgnoreCase(ABOUT_LPF) == true){
    				AboutUs();
    			}	
    			else if (strName.equalsIgnoreCase(ABOUT_NLM) == true){
    				AboutNLM();
    			}	
    			else if (strName.equalsIgnoreCase(ABOUT_HHS) == true){
    				AboutHHS();
    			}	
    			else if (strName.equalsIgnoreCase(ABOUT_NIH) == true){
    				AboutNIH();
    			}
				else if (strName.equalsIgnoreCase("Contact Us") == true){
					new EmailUs(About.this).start();
				}
				else if (strName.equalsIgnoreCase(ABOUT_NCMEC) == true){
					AboutNcmec();
				}
    			else {
    			    Toast.makeText(About.this, getResources().getString(R.string.not_implemented), Toast.LENGTH_SHORT).show();
    			}
        	}

    		private void AboutNIH() {
    			Intent i = new Intent("com.pl.reunite.ABOUTNIH");
    			startActivity(i);																					
    		}

    		private void AboutHHS() {
    			Intent i = new Intent("com.pl.reunite.ABOUTHHS");
    			startActivity(i);																		
    			
    		}

    		private void AboutNLM() {
    			Intent i = new Intent("com.pl.reunite.ABOUTNLM");
    			startActivity(i);																		
    		}

    		private void AboutUs() {
    			Intent i = new Intent("com.pl.reunite.ABOUTLPF");
    			startActivity(i);															
    		}

    		private void BurdenStatement() {
    			Intent i = new Intent("com.pl.reunite.BURDENSTATEMENT");
    			startActivity(i);															
    		}

    		private void PrivacyStatement() {
    			Intent i = new Intent("com.pl.reunite.PRIVACYSTATEMENT");
    			startActivity(i);																		
    		}

			private void AboutReUnite() {
				Intent i = new Intent("com.pl.reunite.ABOUTREUNITE");
				startActivity(i);

			}

			private void AboutNcmec() {
				Intent i = new Intent("com.pl.reunite.AboutNcmecActivity");
				startActivity(i);
			}

			private void burdenAndPrivacyStatement() {
				View ombView = View.inflate(About.this, R.layout.dialog_display_omb, null);
				AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
				builder.setView(ombView);
				builder.setNegativeButton(getResources().getString(R.string.close), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();

                // tracker
                app.tracker().setScreenName(app.TRACK_OMB);
                app.tracker().send(new HitBuilders.ScreenViewBuilder().build());

                app.tracker().setScreenName(app.TRACK_PRIVACY);
                app.tracker().send(new HitBuilders.ScreenViewBuilder().build());
            }
		});
	}

	private ArrayList<ItemAboutView> GetSearchResults(){
    	ArrayList<ItemAboutView> results = new ArrayList<ItemAboutView>();
	    	
    	ItemAboutView item_details;    	
    	for (int i = 0; i < strAboutCompanies.length; i++)
    	{
    		item_details = new ItemAboutView();
    		item_details.setName(strAboutCompanies[i]);
    		item_details.setImageNumber(i + 1);
    		results.add(item_details);
    	}
    	return results;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// tracker
		app.tracker().setScreenName(app.TRACK_ABOUT);
		app.tracker().send(new HitBuilders.ScreenViewBuilder().build());
	}
}
