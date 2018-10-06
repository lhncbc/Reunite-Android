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

import com.pl.reunite.Image;
import com.pl.reunite.Patient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/8/2014.
 */

/**
 * Change history
 * 12/7/2014 Add caption.
 */
public class SearchResult extends Result {
    private String recordsFound;
    private String resultSet;

    private List<Patient> pList = new ArrayList<Patient>();

    public List<Patient> getPatientList() {
        return pList;
    }

    public void setPatientList(List<Patient> pList) {
        this.pList = pList;
    }

    public SearchResult() {
        super();
        recordsFound = "";
        resultSet = "";
    }

    public void toDefault() {
        super.toDefault();
        recordsFound = "";
        resultSet = "";
    }

    public String getResultSet() {
        return resultSet;
    }

    public void setResultSet(String resultSet) {
        this.resultSet = resultSet;
    }

    public String getRecordsFound() {
        return recordsFound;
    }

    public int getRecordsFoundAsInt() {
        return Integer.parseInt(recordsFound);
    }

    public void setRecordsFound(String recordsFound) {
        this.recordsFound = recordsFound;
    }

    public void JSONParserForPatient(String webServer) {
        String string = "eventList";
        String toParseString = "{" + "\"" + string + "\":" + resultSet + "}";
//        String toParseString = resultSet;
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(toParseString);
            JSONArray jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i); // create a single event jsonObject

                Patient p = new Patient(webServer);
                p.setPatientUuid(o.getString("p_uuid"));
//                p.setEncodedUuid(o.getString("encodedUUID")); // no longer has it

                String fullN = o.getString("full_name");
                if (fullN.equalsIgnoreCase("null") == true) {
                    fullN = "unk";
                }
                p.setFullName(fullN);

                /**
                 * remove given name and family name
                 */
                /*
                String gn = o.getString("given_name");
                if (gn.equalsIgnoreCase("null") == true) {
                    gn = "";
                }
                p.setGivengName(gn);
                String fn = o.getString("family_name");
                if (fn.equalsIgnoreCase("null") == true) {
                    fn = "";
                }
                p.setFamilyName(fn);
                */

                // age
                p.setYearsOld(o.getString("years_old"));
                if (p.getYearsOld().equalsIgnoreCase("null")) {
                    p.setYearsOld("-1");
                }
                p.setMinAge(o.getString("min_age"));
                if (p.getMinAge().equalsIgnoreCase("null")) {
                    p.setMinAge("-1");
                }
                p.setMaxAge(o.getString("max_age"));
                if (p.getMaxAge().equalsIgnoreCase("null")) {
                    p.setMaxAge("-1");
                }

                /**
                 * pre-process the old format of age that define minAge and maxAge
                 * version 7.2.6-beta, versionCode 7020601
                 */
                // start
                if (p.getYearsOld().contentEquals("-1") == true) {
                    if ((p.getMinAge().contentEquals("-1") == false) && (p.getMaxAge().contentEquals("-1") == false)) {
                        int min = Integer.parseInt(p.getMinAge());
                        int max = Integer.parseInt(p.getMaxAge());
                        int ave = (min + max) / 2;
                        p.setYearsOld(String.valueOf(ave));
                        p.setMinAge("-1");
                        p.setMaxAge("-1");
                    }
                }
                // end

                // gender
                String gen = o.getString("opt_gender");
                if (gen.equalsIgnoreCase("null")) {
                    p.setGender(Patient.UNKNOWN_SHORT);
                } else if (gen.isEmpty()) {
                    p.setGender(Patient.UNKNOWN_SHORT);
                } else {
                    p.setGender(gen);
                }

                p.setOptStatus(o.getString("opt_status"));
//				p.setImageUrlFull(o.getString("imageUrlFull"));

                String com = o.getString("other_comments");
                if (com.equalsIgnoreCase("null") == true) {
                    com = "";
                }
                p.setComments(com);

                p.setStatusSahanaUpdated(o.getString("last_updated"));

                // address
                p.setLastSeen(o.getString("last_seen")); // last seen location
                /*
                JSONArray locationArr = o.getJSONArray("location");
                JSONObject address = locationArr.getJSONObject(0);
                */
                JSONObject address = (JSONObject) o.get("location");

                String street1 = address.getString("street1");
                if (street1.equalsIgnoreCase("null")) {
                    street1 = "";
                }
                p.setStreet1(street1);

                String street2 = address.getString("street2");
                if (street2.equalsIgnoreCase("null")) {
                    street2 = "";
                }
                p.setStreet2(street2);

                String city = address.getString("city");
                if (city.equalsIgnoreCase("null")) {
                    city = "";
                }
                p.setStreet2(city);

                String region = address.getString("region");
                if (region.equalsIgnoreCase("null")) {
                    region = "";
                }
                p.setState(region);

                String zip = address.getString("postal_code");
                if (zip.equalsIgnoreCase("null")) {
                    zip = "";
                }
                p.setZip(zip);

                String country = address.getString("country");
                if (country.equalsIgnoreCase("null")) {
                    country = "";
                }
                p.setCountry(country);

                // image
                JSONArray jArrImg = new JSONArray();
                List<Image> images = new ArrayList<Image>();
                jArrImg = o.getJSONArray("images");
//                for (int j = 0; j < jArrImg.length(); j++){
                for (int j = 0; j < jArrImg.length(); j++) {
                    JSONObject oImg = jArrImg.getJSONObject(j);
                    Image img = new Image();
                    if (j == 0) {
//                    "note_record_id"
                        p.setId(oImg.getString("image_id"));
//                    "image_type"
                        p.setImageWidth(oImg.getString("image_width"));
                        img.setImageWidth(oImg.getString("image_width"));
                        p.setImageHeight(oImg.getString("image_height"));
                        img.setImageHeight(oImg.getString("image_height"));
//                    "created"
                        p.setImageUrl(oImg.getString("url"));
                        img.setImageUrl(oImg.getString("url"), webServer);
//                    "url_thumb"
//                    "original_filename"
//                    "principal"
//                    "sha1original"
//                    "color_channels"
//                    "note_id"
                        JSONArray jArrTag = new JSONArray();
                        jArrTag = oImg.getJSONArray("tags");
                        for (int k = 0; k < jArrTag.length(); k++) {
                            JSONObject oTag = jArrTag.getJSONObject(k);
                            /**
                             * avoid the null string
                             * version 7.2.6-beta, versionCode 7020601
                             */
                            // start
                            String cap = oTag.getString("tag_text");
                            if (cap == "null") {
                                cap = "";
                            }
                            img.setCaption(cap);
                            // end
                        }
                    } else {
//                    "note_record_id"
//                        p.setId(oImg.getString("image_id"));
//                    "image_type"
//                        p.setImageWidth(oImg.getString("image_width"));
                        img.setImageWidth(oImg.getString("image_width"));
//                        p.setImageHeight(oImg.getString("image_height"));
                        img.setImageHeight(oImg.getString("image_height"));
//                    "created"
//                        p.setImageUrl(oImg.getString("url"));
                        img.setImageUrl(oImg.getString("url"), webServer);
//                    "url_thumb"
//                    "original_filename"
//                    "principal"
//                    "sha1original"
//                    "color_channels"
//                    "note_id"
                        JSONArray jArrTag = new JSONArray();
                        jArrTag = oImg.getJSONArray("tags");
                        for (int k = 0; k < jArrTag.length(); k++) {
                            JSONObject oTag = jArrTag.getJSONObject(k);
                            /**
                             * avoid the null string
                             * version 7.2.6-beta, versionCode 7020601
                             */
                            // start
                            String cap = oTag.getString("tag_text");
                            if (cap == "null") {
                                cap = "";
                            }
                            img.setCaption(cap);
                            // end
                        }
                    }
                    images.add(img);
                }
                p.setImages(images);

                this.pList.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }     // create a json object from a string
    }

    public void REST_JSONParserForPatient(String webServer) {
        String string = "eventList";
        String toParseString = "{" + "\"" + string + "\":" + resultSet + "}";
//        String toParseString = resultSet;
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(toParseString);
            JSONArray jsonArray = jsonObj.getJSONArray(string); // get all events as json objects from Events array

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject o = jsonArray.getJSONObject(i); // create a single event jsonObject

                Patient p = new Patient(webServer);
                p.setPatientUuid(o.getString("p_uuid"));
//                p.setEncodedUuid(o.getString("encodedUUID")); // no longer has it

                String fullN = o.getString("full_name");
                if (fullN.equalsIgnoreCase("null") == true) {
                    fullN = "unk";
                }
                p.setFullName(fullN);

                /**
                 * remove given name and family name
                 */
                /*
                String gn = o.getString("given_name");
                if (gn.equalsIgnoreCase("null") == true) {
                    gn = "";
                }
                p.setGivengName(gn);
                String fn = o.getString("family_name");
                if (fn.equalsIgnoreCase("null") == true) {
                    fn = "";
                }
                p.setFamilyName(fn);
                */

                // age
                p.setYearsOld(o.getString("years_old"));
                if (p.getYearsOld().equalsIgnoreCase("null")) {
                    p.setYearsOld("-1");
                }
                p.setMinAge(o.getString("min_age"));
                if (p.getMinAge().equalsIgnoreCase("null")) {
                    p.setMinAge("-1");
                }
                p.setMaxAge(o.getString("max_age"));
                if (p.getMaxAge().equalsIgnoreCase("null")) {
                    p.setMaxAge("-1");
                }

                /**
                 * pre-process the old format of age that define minAge and maxAge
                 * version 7.2.6-beta, versionCode 7020601
                 */
                // start
                if (p.getYearsOld().contentEquals("-1") == true) {
                    if ((p.getMinAge().contentEquals("-1") == false) && (p.getMaxAge().contentEquals("-1") == false)) {
                        int min = Integer.parseInt(p.getMinAge());
                        int max = Integer.parseInt(p.getMaxAge());
                        int ave = (min + max) / 2;
                        p.setYearsOld(String.valueOf(ave));
                        p.setMinAge("-1");
                        p.setMaxAge("-1");
                    }
                }
                // end

                // gender
                String gen = o.getString("opt_gender");
                if (gen.equalsIgnoreCase("null")) {
                    p.setGender(Patient.UNKNOWN_SHORT);
                } else if (gen.isEmpty()) {
                    p.setGender(Patient.UNKNOWN_SHORT);
                } else {
                    p.setGender(gen);
                }

                p.setOptStatus(o.getString("opt_status"));

                String com = o.getString("other_comments");
                if (com.equalsIgnoreCase("null") == true) {
                    com = "";
                }
                p.setComments(com);

                p.setStatusSahanaUpdated(o.getString("last_updated"));

                // address
                p.setLastSeen(o.getString("last_seen")); // last seen location
                JSONObject address = (JSONObject) o.get("location");

                String street1 = address.getString("street1");
                if (street1.equalsIgnoreCase("null")) {
                    street1 = "";
                }
                p.setStreet1(street1);

                String street2 = address.getString("street2");
                if (street2.equalsIgnoreCase("null")) {
                    street2 = "";
                }
                p.setStreet2(street2);

                String city = address.getString("city");
                if (city.equalsIgnoreCase("null")) {
                    city = "";
                }
                p.setStreet2(city);

                String region = address.getString("region");
                if (region.equalsIgnoreCase("null")) {
                    region = "";
                }
                p.setState(region);

                String zip = address.getString("postal_code");
                if (zip.equalsIgnoreCase("null")) {
                    zip = "";
                }
                p.setZip(zip);

                String country = address.getString("country");
                if (country.equalsIgnoreCase("null")) {
                    country = "";
                }
                p.setCountry(country);

                // image
                JSONArray jArrImg = new JSONArray();
                List<Image> images = new ArrayList<Image>();
                jArrImg = o.getJSONArray("images");
//                for (int j = 0; j < jArrImg.length(); j++){
                for (int j = 0; j < jArrImg.length(); j++) {
                    JSONObject oImg = jArrImg.getJSONObject(j);
                    Image img = new Image();
                    if (j == 0) {
//                    "note_record_id"
                        p.setId(oImg.getString("image_id"));
//                    "image_type"
                        p.setImageWidth(oImg.getString("image_width"));
                        img.setImageWidth(oImg.getString("image_width"));
                        p.setImageHeight(oImg.getString("image_height"));
                        img.setImageHeight(oImg.getString("image_height"));
//                    "created"
                        p.setImageUrl(oImg.getString("url"));
                        img.setImageUrl(oImg.getString("url"), webServer);
//                    "url_thumb"
//                    "original_filename"
//                    "principal"
//                    "sha1original"
//                    "color_channels"
//                    "note_id"
                        JSONArray jArrTag = new JSONArray();
                        jArrTag = oImg.getJSONArray("tags");
                        for (int k = 0; k < jArrTag.length(); k++) {
                            JSONObject oTag = jArrTag.getJSONObject(k);
                            /**
                             * avoid the null string
                             * version 7.2.6-beta, versionCode 7020601
                             */
                            // start
                            String cap = oTag.getString("tag_text");
                            if (cap == "null") {
                                cap = "";
                            }
                            img.setCaption(cap);
                            // end
                        }
                    } else {
//                    "note_record_id"
//                        p.setId(oImg.getString("image_id"));
//                    "image_type"
//                        p.setImageWidth(oImg.getString("image_width"));
                        img.setImageWidth(oImg.getString("image_width"));
//                        p.setImageHeight(oImg.getString("image_height"));
                        img.setImageHeight(oImg.getString("image_height"));
//                    "created"
//                        p.setImageUrl(oImg.getString("url"));
                        img.setImageUrl(oImg.getString("url"), webServer);
//                    "url_thumb"
//                    "original_filename"
//                    "principal"
//                    "sha1original"
//                    "color_channels"
//                    "note_id"
                        JSONArray jArrTag = new JSONArray();
                        jArrTag = oImg.getJSONArray("tags");
                        for (int k = 0; k < jArrTag.length(); k++) {
                            JSONObject oTag = jArrTag.getJSONObject(k);
                            /**
                             * avoid the null string
                             * version 7.2.6-beta, versionCode 7020601
                             */
                            // start
                            String cap = oTag.getString("tag_text");
                            if (cap == "null") {
                                cap = "";
                            }
                            img.setCaption(cap);
                            // end
                        }
                    }
                    images.add(img);
                }
                p.setImages(images);

                this.pList.add(p);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }     // create a json object from a string
    }
}
