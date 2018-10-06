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
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class AddressListActivity extends Activity implements View.OnClickListener{
    ListView lvAddressSimple;
    PatientAddress currentPatientAddress;
    String currentSelectedSerialId;
	List<PatientAddress> patientAddressList = new ArrayList<PatientAddress>();

	private AddressDataSource datasource;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.address_list);
		
		Initialize();
	}
	
	private void Initialize() {
		lvAddressSimple = (ListView) findViewById(R.id.lvAddressSimple);
		
    	datasource = new AddressDataSource(this);
        datasource.open();
        
        patientAddressList = datasource.getAllAddressDesc();
        DisplayAddressList();
	}
	
	
	public void onClick(View v) {
	}
	
	private void DisplayAddressList() {
		if (patientAddressList.isEmpty() == true){
			return;
		}
        ArrayList<ItemAddressView> image_details = GetAddressResults();	
		lvAddressSimple = (ListView) findViewById(R.id.lvAddressSimple);
		lvAddressSimple.setAdapter(new ItemAddressListBaseAdapter(AddressListActivity.this, image_details));
		
		lvAddressSimple.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView<?> a, View v, int position, long id) { 
        		Object o = lvAddressSimple.getItemAtPosition(position);
        		ItemAddressView obj_itemDetails = (ItemAddressView)o;

        		// prepare data to return
        		Intent backData = new Intent();
				backData.putExtra("serialId", obj_itemDetails.getSerialId());
				backData.putExtra("street1", obj_itemDetails.getStreet1());
				backData.putExtra("street2", obj_itemDetails.getStreet2());
				backData.putExtra("city", obj_itemDetails.getCity());
				backData.putExtra("state", obj_itemDetails.getState());
				backData.putExtra("zip", obj_itemDetails.getZip());
				backData.putExtra("country", obj_itemDetails.getCountry());

				setResult(AddressListActivity.this.RESULT_CANCELED, backData);
				AddressListActivity.this.finish();
        	}
		});
	}

	private ArrayList<ItemAddressView> GetAddressResults() {
	   	ArrayList<ItemAddressView> results = new ArrayList<ItemAddressView>();
    	
    	ItemAddressView item_details;    	
    	for (int i = 0; i < patientAddressList.size(); i++)
    	{
    		PatientAddress p = patientAddressList.get(i);
    		item_details = new ItemAddressView();
    		item_details.setSerialId(String.valueOf(p.getSerialId()));
    		item_details.setStreet1(p.getStreet1().toString());
    		item_details.setStreet2(p.getStreet2().toString());
    		item_details.setCity(p.getCity().toString());
    		item_details.setState(p.getState().toString());
    		item_details.setZip(p.getZip().toString());
    		item_details.setCountry(p.getCountry().toString());
    		results.add(item_details);
    	}
    	return results;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getResources().getString(R.string.you_have_selected))
			       .setCancelable(true)
			       .setTitle(getResources().getString(R.string.are_you_sure_q))
			       .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       })
			       .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                dialog.dismiss();
			        		// prepare data to return
			        		Intent backData = new Intent();
							backData.putExtra("serialId", "0");
							backData.putExtra("street1", "");
							backData.putExtra("street2", "");
							backData.putExtra("city", "");
							backData.putExtra("state", "");
							backData.putExtra("zip", "");
							backData.putExtra("country", "");

							setResult(AddressListActivity.this.RESULT_CANCELED, backData);
							AddressListActivity.this.finish();
			           }
			       });
			AlertDialog alert = builder.create();		
			alert.show();		
		    return false;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	protected void onResume() {
		datasource.open();
		super.onResume();
	}   

}
