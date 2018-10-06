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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemAddressListBaseAdapter extends BaseAdapter {
	private static ArrayList<ItemAddressView> itemDetailsrrayList;
	
	private LayoutInflater l_Inflater;

	public ItemAddressListBaseAdapter(Context context, ArrayList<ItemAddressView> results) {
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return itemDetailsrrayList.size();
	}

	public Object getItem(int position) {
		return itemDetailsrrayList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = l_Inflater.inflate(R.layout.item_address_view, null);
			holder = new ViewHolder();
			holder.tvItemSerialId = (TextView) convertView.findViewById(R.id.textSerialId);
			holder.tvItemStreet1 = (TextView) convertView.findViewById(R.id.textStreet1);
			holder.tvItemStreet2 = (TextView) convertView.findViewById(R.id.textStreet2);
			holder.tvItemCity = (TextView) convertView.findViewById(R.id.textCity);
			holder.tvItemState = (TextView) convertView.findViewById(R.id.textState);
			holder.tvItemZip = (TextView) convertView.findViewById(R.id.textZip);
			holder.tvItemCountry = (TextView) convertView.findViewById(R.id.textCountry);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvItemSerialId.setText(itemDetailsrrayList.get(position).getSerialId());
		if (itemDetailsrrayList.get(position).getStreet1().isEmpty() == true){
			holder.tvItemStreet1.setVisibility(View.GONE);			
		}
		else {
			holder.tvItemStreet1.setText(itemDetailsrrayList.get(position).getStreet1());
		}
		if (itemDetailsrrayList.get(position).getStreet2().isEmpty() == true){
			holder.tvItemStreet2.setVisibility(View.GONE);						
		}
		else {	
			holder.tvItemStreet2.setText(itemDetailsrrayList.get(position).getStreet2());
		}
		if (itemDetailsrrayList.get(position).getCity().isEmpty() == true){
			holder.tvItemCity.setVisibility(View.GONE);			
		}
		else {
			holder.tvItemCity.setText(itemDetailsrrayList.get(position).getCity());
		}
		if (itemDetailsrrayList.get(position).getState().isEmpty() == true){
			holder.tvItemState.setVisibility(View.GONE);			
		}
		else{
			holder.tvItemState.setText(itemDetailsrrayList.get(position).getState());
		}
		if (itemDetailsrrayList.get(position).getZip().isEmpty() == true){
			holder.tvItemZip.setVisibility(View.GONE);			
		}
		else{
			holder.tvItemZip.setText(itemDetailsrrayList.get(position).getZip());
		}
		if (itemDetailsrrayList.get(position).getCountry().isEmpty() == true){
			holder.tvItemCountry.setVisibility(View.GONE);				
		}
		else{
			holder.tvItemCountry.setText(itemDetailsrrayList.get(position).getCountry());			
		}

		return convertView;
	}

	static class ViewHolder {
		TextView tvItemSerialId;
		TextView tvItemStreet1;
		TextView tvItemStreet2;
		TextView tvItemCity;
		TextView tvItemState;
		TextView tvItemZip;
		TextView tvItemCountry;
	}


}
