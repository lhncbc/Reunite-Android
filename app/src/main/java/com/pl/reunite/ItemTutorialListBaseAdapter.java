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

public class ItemTutorialListBaseAdapter extends BaseAdapter {
	private static ArrayList<ItemTutorialView> itemDetailsrrayList;
		
	private LayoutInflater l_Inflater;

	public ItemTutorialListBaseAdapter(Context context, ArrayList<ItemTutorialView> results) {
		itemDetailsrrayList = results;
		l_Inflater = LayoutInflater.from(context);
	}

	public int getCount() {
		int n= itemDetailsrrayList.size();
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
			convertView = l_Inflater.inflate(R.layout.item_tutorial_view, null);
			holder = new ViewHolder();
			holder.tvItemCat = (TextView) convertView.findViewById(R.id.textViewCat);
			holder.tvItemDis = (TextView) convertView.findViewById(R.id.textViewDis);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		holder.tvItemCat.setText(itemDetailsrrayList.get(position).getCat());
		holder.tvItemDis.setText(itemDetailsrrayList.get(position).getDis());

		return convertView;
	}

	static class ViewHolder {
		TextView tvItemCat;
		TextView tvItemDis;
	}
}
