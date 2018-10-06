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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemPatientSimpleListBaseAdapter extends BaseAdapter{
	private static ArrayList<ItemPatientSimpleView> itemDetailsrrayList1;
	Drawable drawable = null;
	Bitmap bitmap = null;
	boolean firstTime = true;

	private LayoutInflater l_Inflater;

	public ItemPatientSimpleListBaseAdapter(Context context, ArrayList<ItemPatientSimpleView> results) {
		itemDetailsrrayList1 = results;
		l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return itemDetailsrrayList1.size();
	}

	public Object getItem(int position) {
		return itemDetailsrrayList1.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.item_patient_simple_view, null);
			holder = new ViewHolder();
			holder.itemProgressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
			holder.itemImage = (ImageView) convertView.findViewById(R.id.imageViewPatientSmall);
			holder.txt_itemName = (TextView) convertView.findViewById(R.id.textPatientName);
			holder.txt_itemOptStatus = (TextView) convertView.findViewById(R.id.textOptStatus);
			holder.txt_itemStatusSahanaUpdated = (TextView) convertView.findViewById(R.id.textStatusSahanaUpdated);
			holder.txt_itemAge = (TextView) convertView.findViewById(R.id.textAge);
			holder.txt_itemGender = (TextView) convertView.findViewById(R.id.textGender);
			holder.txt_itemNumber = (TextView) convertView.findViewById(R.id.textNumber);
			holder.txt_itemEvent = (TextView) convertView.findViewById(R.id.textEvent);
			holder.txt_itemPersonAnimal = (TextView) convertView.findViewById(R.id.textViewPersonAnimal);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		/**
		 * use full name only
		 * version 9.2.9
		 */
		String name = itemDetailsrrayList1.get(position).getName().toString();
		if (name.isEmpty() || name.equalsIgnoreCase("unk")){
			name = holder.txt_itemAge.getResources().getString(R.string.unknown);;
		}
		holder.txt_itemName.setText(name);

		// replace age section
		// built in versionCode 7020601, version 7.2.6-beta
		// start
		String strAge1 = holder.txt_itemAge.getResources().getString(R.string.approximate_age) + ":";
		String strAge2 = itemDetailsrrayList1.get(position).getAge().toString();
		if (strAge2.equalsIgnoreCase("null")){
			strAge2 = holder.txt_itemAge.getResources().getString(R.string.unknown);
		}
		else if (strAge2.equalsIgnoreCase("-1") == true || strAge2.isEmpty() == true) {
			strAge2 = holder.txt_itemAge.getResources().getString(R.string.unknown);
		}
		holder.txt_itemAge.setText(strAge1 + strAge2);
		// end
		
		// Gender
		String strG1 = holder.txt_itemGender.getResources().getString(R.string.gender) + ":";
		String strG2 = itemDetailsrrayList1.get(position).getGender().toString();
		if (strG2.equalsIgnoreCase("mal") == true || strG2.equalsIgnoreCase("male") == true) {
			strG2 = holder.txt_itemGender.getResources().getString(R.string.male);
		}
		else if (strG2.equalsIgnoreCase("fml") == true || strG2.equalsIgnoreCase("female") == true){
			strG2 = holder.txt_itemGender.getResources().getString(R.string.female);
		}
		else if (strG2.equalsIgnoreCase("cpx") == true || strG2.equalsIgnoreCase("complex") == true){
			strG2 = holder.txt_itemGender.getResources().getString(R.string.complex);
		}
		else if (strG2.equalsIgnoreCase("unk") == true || strG2.equalsIgnoreCase("unknown") == true){
			strG2 = holder.txt_itemGender.getResources().getString(R.string.unknown); // In listview, displays '?'
		}
		else {
            strG2 = holder.txt_itemGender.getResources().getString(R.string.unknown); // In listview, displays '?'
		}
		holder.txt_itemGender.setText(strG1 + strG2);
		
		// Status unknown
		String strS1 = "";
		String strS2 = itemDetailsrrayList1.get(position).getOptStatus().toString();
		int color = 0;
		
		// Here is display, convert from short to long form
		if (strS2.equalsIgnoreCase(Patient.UNK) == true){
			strS2 = holder.txt_itemOptStatus.getResources().getString(R.string.unknown);
//			color = Color.GRAY;
		}
		else if (strS2.equalsIgnoreCase(Patient.INJ) == true){
			strS2 = holder.txt_itemOptStatus.getResources().getString(R.string.injured);
//			color = Color.YELLOW;
		}
		else if (strS2.equalsIgnoreCase(Patient.ALI) == true){
			strS2 = holder.txt_itemOptStatus.getResources().getString(R.string.alive_and_well);
//			color = Color.GREEN;
		}
		else if (strS2.equalsIgnoreCase(Patient.ALIVE_AND_WELL) == true){
			strS2 = holder.txt_itemOptStatus.getResources().getString(R.string.alive_and_well);
//			color = Color.GREEN;
		}
		else if (strS2.equalsIgnoreCase(Patient.MIS) == true){
			strS2 = holder.txt_itemOptStatus.getResources().getString(R.string.missing);
//			color = Color.CYAN;
		}
		else if (strS2.equalsIgnoreCase(Patient.DEC) == true){
			strS2 = holder.txt_itemOptStatus.getResources().getString(R.string.deceased);
//			color = Color.LTGRAY;
		}
		else if (strS2.equalsIgnoreCase(Patient.FND) == true){
			strS2 = holder.txt_itemOptStatus.getResources().getString(R.string.found);
//			color = Color.rgb(255, 160, 122);
		}
		holder.txt_itemOptStatus.setText(strS1 + strS2);
		holder.txt_itemOptStatus.setTextColor(Color.BLACK);
		
		strS1 = itemDetailsrrayList1.get(position).getStatusSahanaUpdated().toString();
		holder.txt_itemStatusSahanaUpdated.setText(strS1);
			
//		boolean statusPhotoDownload = itemDetailsrrayList1.get(position).getStatusPhotoDownload();
//		if (statusPhotoDownload == false){
//			holder.itemProgressBar.setVisibility(View.VISIBLE);
//			holder.itemImage.setVisibility(View.GONE);			
//		}
//		else {
			Bitmap bitmap = itemDetailsrrayList1.get(position).getPhoto();
			holder.itemProgressBar.setVisibility(View.GONE);
			holder.itemImage.setVisibility(View.VISIBLE);
			if (bitmap != null) {
				holder.itemImage.setImageBitmap(bitmap);
			}
			else {
				holder.itemImage.setImageResource(R.drawable.questionmark);			
			}			
//		}
		
		// Number
		String strN = itemDetailsrrayList1.get(position).getNumber().toString();
		holder.txt_itemNumber.setText(strN);

		// Event
		String strE1 = "";
		String strE2 = itemDetailsrrayList1.get(position).getEvent().toString();
		holder.txt_itemEvent.setText(strE1 + strE2);

		if (itemDetailsrrayList1.get(position).getPersonAnimal() == Patient.PERSON){
			holder.txt_itemPersonAnimal.setText("Person");
		}
		else {
			holder.txt_itemPersonAnimal.setText("Animal");
		}

		return convertView;
	}

	static class ViewHolder {
		ProgressBar itemProgressBar;
		ImageView itemImage;
		TextView txt_itemName;
		TextView txt_itemOptStatus;
		TextView txt_itemStatusSahanaUpdated;
		TextView txt_itemAge;
		TextView txt_itemGender;
		TextView txt_itemNumber;
		TextView txt_itemEvent;
		TextView txt_itemPersonAnimal;
	}
}
