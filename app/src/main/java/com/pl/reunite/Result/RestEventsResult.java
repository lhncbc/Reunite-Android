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

import com.pl.reunite.Event;
import com.pl.reunite.Event2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 7/28/2017.
 */

public class RestEventsResult extends RestResult {
    private List<Event> eventList = new ArrayList<Event>();
    private List<Event2> eventList2 = new ArrayList<Event2>();

    public List<Event> getEventList() {
        return eventList;
    }

    public void setEventList(List<Event> eventList) {
        this.eventList = eventList;
    }

    public List<Event2> getEventList2() {
        return eventList2;
    }

    public void setEventList2(List<Event2> eventList2) {
        this.eventList2 = eventList2;
    }

    public RestEventsResult(Context c) {
        super(c);
        eventList2.clear();
    }

    public RestEventsResult(RestEventsResult r, Context c) {
        super(r.getErrorCode(), r.getErrorMessage(), r.getTimeElapsed(), c);
        this.eventList2 = r.getEventList2();
    }

    public void toEventList(){
        eventList.clear();
        for (int i = 0; i < eventList2.size(); i++){
            Event2 e2 = eventList2.get(i);
            Event e = e2.toEvent();
            eventList.add(e);
        }
    }
}
