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
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TutorialHomeScreenActivity extends Activity {
    private static final int NUM_PAGES = 7;
    
    ReUnite app;
    String webServer = "";
    
    /** Called when the activity is first created. */
    ViewPager myPager;
    TextView where;
    Button button;
    Button next;
    Button prev;
    String totalPages = Integer.toString(NUM_PAGES);

    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.tutorial_homescreen);
            
    		app = ((ReUnite)this.getApplication());
            app.detectMobileDevice(this);
            app.setScreenOrientation(this);

    		webServer = app.getWebServer();
            
            where = (TextView) findViewById(R.id.textViewWhere);
    		where.setText(Integer.toString(1) + "/" + totalPages); // Initial
            
            next = (Button) findViewById(R.id.buttonNext);
            next.setOnClickListener(new View.OnClickListener() {
            	public void onClick(View v) {
            		int n = myPager.getCurrentItem();
            		if (n < NUM_PAGES - 1){
            			n++;
            			myPager.setCurrentItem(n, true);
            		}
            		
            		where.setText(Integer.toString(n+1) + "/" + totalPages);
            		
            		if (n == 0){
            			prev.setEnabled(false);
            		}
            		else {
            			prev.setEnabled(true);
            		}
            		
            		if (n == NUM_PAGES - 1){
            			next.setEnabled(false);
            		}
            		else {
            			next.setEnabled(true);
            		}
            	}
            });
            prev = (Button) findViewById(R.id.buttonPrev);
            prev.setOnClickListener(new View.OnClickListener() {
            	public void onClick(View v) {
            		int n = myPager.getCurrentItem();
            		if (n > 0){
            			n--;
            			myPager.setCurrentItem(n, true);
            		}
            		
            		where.setText(Integer.toString(n+1) + "/" + totalPages);

            		if (n == 0){
            			prev.setEnabled(false);
            		}
            		else {
            			prev.setEnabled(true);
            		}
            		
            		if (n == NUM_PAGES - 1){
            			next.setEnabled(false);
            		}
            		else {
            			next.setEnabled(true);
            		}
            	}
            });
            
            MyPagerAdapter adapter = new MyPagerAdapter();
            myPager = (ViewPager) findViewById(R.id.threepageviewer);
            myPager.setAdapter(adapter);
            myPager.setCurrentItem(0);
            myPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {            
                public void onPageSelected(int position) {
            		where.setText(Integer.toString(position+1) + "/" + totalPages);
                }

                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                public void onPageScrollStateChanged(int state) {

                }
            });        
            }
    
	private class MyPagerAdapter extends PagerAdapter {

            public int getCount() {
                    return NUM_PAGES;
            }

            public Object instantiateItem(View collection, int position) {

                    LayoutInflater inflater = (LayoutInflater) collection.getContext()
                                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    int resId = 0;
                    switch (position) {
                    case 0:
                        resId = R.layout.home_screen_001;
                        break;
                    case 1:
                        resId = R.layout.home_screen_002;
                        break;
                    case 2:
                        resId = R.layout.home_screen_003;
                        break;
                    case 3:
                        resId = R.layout.home_screen_004;
                        break;
                    case 4:
                        resId = R.layout.home_screen_005;
                        break;
                    case 5:
                        resId = R.layout.home_screen_006;
                        break;
                    case 6:
                        resId = R.layout.home_screen_007;
                        break;
                    }

                    View view = inflater.inflate(resId, null);

                    ((ViewPager) collection).addView(view, 0);
                    
                    return view;
            }

            @Override
            public void destroyItem(View arg0, int arg1, Object arg2) {
                    ((ViewPager) arg0).removeView((View) arg2);

            }

            @Override
            public void finishUpdate(View arg0) {

            }

            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == ((View) arg1);

            }

            @Override
            public void restoreState(Parcelable arg0, ClassLoader arg1) {

            }

            @Override
            public Parcelable saveState() {
                    return null;
            }

            @Override
            public void startUpdate(View arg0) {

            }

    }
    // Menu sections
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.tutorial_menu, menu);
        return true;
	}	
	
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.itemMainPage:
        	GoBackToMainPage();
        	break;
        case R.id.itemLatency:
        	Latency2();
        	break;
        case R.id.itemContactUs:
			new EmailUs(this).start();
        	break;
        }
        return true;
	}

	private void Latency2() {
		Intent i = new Intent(TutorialHomeScreenActivity.this, LatencyActivity.class);
		i.putExtra("webServer", webServer);    			
		startActivity(i);	
	}
    
	private void GoBackToMainPage() {
		Intent i = new Intent(TutorialHomeScreenActivity.this, ReUniteActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP); 
		startActivity(i);
		finish();
	}    
}
