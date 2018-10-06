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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ItemEventListBaseAdapter extends BaseAdapter {
	private static ArrayList<ItemEventView> itemDetailsrrayList;
		
	private LayoutInflater l_Inflater;

	public ItemEventListBaseAdapter(Context context, ArrayList<ItemEventView> results) {
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
			convertView = l_Inflater.inflate(R.layout.item_event_view, null);
			holder = new ViewHolder();
			holder.tvItemName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvItemDate = (TextView) convertView.findViewById(R.id.tvDate);
			holder.tvItemType = (TextView) convertView.findViewById(R.id.tvType);
			holder.tvItemStatus = (TextView) convertView.findViewById(R.id.tvStatus);
			holder.tvItemStreet = (TextView) convertView.findViewById(R.id.tvStreet);
			holder.shorName = "";

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvItemName.setText(itemDetailsrrayList.get(position).getName());
		holder.tvItemDate.setText(itemDetailsrrayList.get(position).getDate());
		holder.tvItemType.setText(itemDetailsrrayList.get(position).getType());
		if (itemDetailsrrayList.get(position).getType().contains("REAL") == true){
//			holder.tvItemName.setTextColor(Color.rgb(255, 165, 0)); // Orange color
//			holder.tvItemName.setTextColor(Color.rgb(135, 206, 250)); // light sky blue  color
//			holder.tvItemName.setTextColor(Color.WHITE);			
//			holder.tvItemType.setTextColor(Color.rgb(255, 165, 0)); // Orange color
		}
		else{
//			holder.tvItemName.setTextColor(Color.rgb(135, 206, 250)); // light sky blue  color
//			holder.tvItemName.setTextColor(Color.WHITE);			
//			holder.tvItemType.setTextColor(Color.rgb(135, 206, 250)); // light sky blue  color
		}

		if (itemDetailsrrayList.get(position).getStatus().contains("0") == true) {
			Context c = holder.tvItemStatus.getContext();
			holder.tvItemStatus.setText(c.getResources().getString(R.string.open_for_reporting));
//			holder.tvItemStatus.setTextColor(Color.GREEN); // Orange color
		}
		else {
			Context c = holder.tvItemStatus.getContext();
			holder.tvItemStatus.setText(c.getResources().getString(R.string.closed_reporting));
//			holder.tvItemStatus.setTextColor(Color.YELLOW); // light sky blue  color
		}
		holder.tvItemStatus.setTextColor(Color.BLACK);

		if (itemDetailsrrayList.get(position).getStreet().isEmpty() == true){
			holder.tvItemStreet.setVisibility(View.GONE);
		}
		else {
			holder.tvItemStreet.setText(itemDetailsrrayList.get(position).getStreet());
		}
		holder.shorName = itemDetailsrrayList.get(position).getShortName();

		return convertView;
	}

	static class ViewHolder {
		TextView tvItemName;
		TextView tvItemDate;
		TextView tvItemType;
		TextView tvItemStatus;
		TextView tvItemStreet;
		String shorName;
	}


}
