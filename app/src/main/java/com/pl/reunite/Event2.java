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

/**
 * Created on 7/28/2017.
 */

public class Event2 extends Event{
    public boolean isArchived2() {
        return archived2;
    }

    public void setArchived2(boolean archived2) {
        this.archived2 = archived2;
    }

    public String[] getArticles2() {
        return articles2;
    }

    @Override
    public void setViewSettings(ViewSettings viewSettings) {
        super.setViewSettings(viewSettings);
    }

    public void setArticles2(String[] articles2) {
        this.articles2 = articles2;
    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
    }


    public String getType2() {
        return type2;
    }

    public String getUpdated2() {
        return updated2;
    }

    public void setUpdated2(String updated2) {
        this.updated2 = updated2;
    }

    public int getUnexpired2() {

        return unexpired2;
    }

    public boolean isUnlisted2() {
        return unlisted2;
    }

    public void setUnlisted2(boolean unlisted2) {
        this.unlisted2 = unlisted2;
    }

    public void setUnexpired2(int unexpired2) {
        this.unexpired2 = unexpired2;
    }

    public void setType2(String type2) {

        this.type2 = type2;
    }

    public String[] getTags2() {
        return tags2;

    }

    public void setTags2(String[] tags2) {
        this.tags2 = tags2;
    }

    public String[] getCaptions2() {
        return captions2;

    }

    public String getShort2() {
        return short2;
    }

    public void setShort2(String short2) {
        this.short2 = short2;
    }

    public int getOriginate2() {

        return originate2;
    }

    public void setOriginate2(int originate2) {
        this.originate2 = originate2;
    }

    public double getLongitude2() {
        return longitude2;

    }

    public void setLongitude2(double longitude2) {
        this.longitude2 = longitude2;
    }

    public String[] getNames2() {
        return names2;
    }

    public void setNames2(String[] names2) {
        this.names2 = names2;
    }

    public boolean isClosed2() {

        return closed2;

    }

    public String getImage2() {
        return image2;
    }

    public double getLatitude2() {
        return latitude2;
    }

    public void setLatitude2(double latitude2) {
        this.latitude2 = latitude2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;

    }

    public int getExpired2() {
        return expired2;
    }

    public int getId2() {
        return id2;
    }

    public void setId2(int id2) {
        this.id2 = id2;
    }

    public void setExpired2(int expired2) {
        this.expired2 = expired2;
    }

    public boolean isDefault2() {
        return default2;
    }

    public void setDefault2(boolean default2) {
        this.default2 = default2;
    }

    public void setClosed2(boolean closed2) {
        this.closed2 = closed2;
    }
    public boolean getClosed2() {
        return closed2;
    }

    public void setCaptions2(String[] captions2) {
        this.captions2 = captions2;
    }

    public int getGroup2() {
        return group2;
    }
    public void setGroup2(int group2) {
        this.group2 = group2;
    }

    private boolean archived2;
    private boolean closed2;
    private String date2;
    private boolean default2;
    private int expired2;
    private int group2;
    private int id2;
    private String image2;
    private double latitude2;
    private double longitude2;
    private String[] names2 = {"", "", "", ""};
    private int originate2;
    private String short2;
    private String[] tags2 = {"", "", "", ""};
    private String type2;
    private int unexpired2;
    private boolean unlisted2;
    private String updated2;
    private String[] articles2 = {"", "", "", ""};
    private String[] captions2 = {"", "", "", ""};

    public Event2(String webServer) {
        super(webServer);
        archived2 = false;
        closed2 = false;
        date2 = "";
        default2 = true;
        expired2 = 0;
        group2 = 0;
        id2 = 0;
        image2 = "";
        latitude2 = 0.0;
        longitude2 = 0.0;
        // names2 = {"", "", "", ""};
        originate2 = 0;
        short2 = "";
        // tags2 = {"", "", "", ""};
        type2 = "";
        unexpired2 = 0;
        unlisted2 = false;
        updated2 = "";
        //articles2 = {"", "", "", ""};
        //captions2 = {"", "", "", ""};
    }
    public Event2(Event2 e, String webServer) {
        super(webServer);
        archived2 = e.isArchived2();
        closed2 = e.getClosed2();
        date2 = e.getDate2();
        default2 = e.isDefault2();
        expired2 = e.getExpired2();
        group2 = e.getGroup2();
        id2 = e.getId2();
        image2 = e.getImage2();
        latitude2 = e.getLatitude2();
        longitude2 = e.getLongitude2();
        names2 = e.getNames2();
        originate2 = e.getOriginate2();
        short2 = e.getShort2();
        tags2 = e.getTags2();
        type2 = e.getType2();
        unexpired2 = e.getUnexpired2();
        unlisted2 = e.isUnlisted2();
        updated2 = e.getUpdated2();
        articles2 = e.getArticles2();
        captions2 = e.getCaptions2();
    }

    public Event toEvent(){
        Event event = new Event(getWebServer());
        event.toDefault();
        event.setName(names2[0]);
        event.setShortName(short2);
        event.setDate(date2);
        event.setStreet("");

        event.setGroup(String.valueOf(group2));
        event.setIncidentId(String.valueOf(id2));
        event.setParentId("");
        event.setType(type2);
        if (closed2 == true){
            event.setClosed("1");
        }
        else {
            event.setClosed("0");
        }
        event.setLatitude(Double.toString(latitude2));
        event.setLongitude(Double.toString(longitude2));

        return event;
    }
}
