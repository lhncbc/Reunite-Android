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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 11/19/2014.
 * It is the bridge in between class Image and MySQL database.
 */
public class ImageDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {
            MySQLiteHelper.COLUMN_SERIALID,
            MySQLiteHelper.COLUMN_PATIENT_SERIAL_ID,
            MySQLiteHelper.COLUMN_SEQUENCE,
            MySQLiteHelper.COLUMN_IMAGE_URL,
            MySQLiteHelper.COLUMN_IMAGE_URL_FOR_FATCH,
            MySQLiteHelper.COLUMN_IMAGE_WIDTH,
            MySQLiteHelper.COLUMN_IMAGE_HEIGHT,
            MySQLiteHelper.COLUMN_PHOTO_DATA,
            MySQLiteHelper.COLUMN_FACE_DETECTED,
            MySQLiteHelper.COLUMN_RECT_X,
            MySQLiteHelper.COLUMN_RECT_Y,
            MySQLiteHelper.COLUMN_RECT_H,
            MySQLiteHelper.COLUMN_RECT_W,
            MySQLiteHelper.COLUMN_CAPTION,
            MySQLiteHelper.COLUMN_SIZE
    };

    public ImageDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Image createImage(Image img, String webServer) {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PATIENT_SERIAL_ID, img.getPatientSerialId());
        values.put(MySQLiteHelper.COLUMN_SEQUENCE, img.getSequence());
        values.put(MySQLiteHelper.COLUMN_IMAGE_URL, img.getImageUrl());
        values.put(MySQLiteHelper.COLUMN_IMAGE_URL_FOR_FATCH, img.getImageUrlForFetch());
        values.put(MySQLiteHelper.COLUMN_IMAGE_WIDTH, img.getImageWidth());
        values.put(MySQLiteHelper.COLUMN_IMAGE_HEIGHT, img.getImageHeight());
        values.put(MySQLiteHelper.COLUMN_PHOTO_DATA, img.getPhotoData());
        if (img.isFaceDetected() == true) {
            values.put(MySQLiteHelper.COLUMN_FACE_DETECTED, "true");
        }
        else {
            values.put(MySQLiteHelper.COLUMN_FACE_DETECTED, "false");
        }
        values.put(MySQLiteHelper.COLUMN_RECT_X, img.getRectX());
        values.put(MySQLiteHelper.COLUMN_RECT_Y, img.getRectY());
        values.put(MySQLiteHelper.COLUMN_RECT_H, img.getRectH());
        values.put(MySQLiteHelper.COLUMN_RECT_W, img.getRectW());
        values.put(MySQLiteHelper.COLUMN_CAPTION, img.getCaption());
        values.put(MySQLiteHelper.COLUMN_SIZE, img.getSize());

        long insertId = database.insert(MySQLiteHelper.TABLE_NAME_IMAGE, null, values);
        if (insertId == -1){
            return null;
        }
        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_IMAGE,
                allColumns, MySQLiteHelper.COLUMN_SERIALID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Image newImg = new Image();
        newImg = cursorToImage(cursor, webServer);
        cursor.close();
        return newImg;
    }

    public List<Image> getAllImages(long serialId, String webSever) {
        List<Image> list = new ArrayList<Image>();

        System.out.println("Image is searched with serialId: " + serialId);
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_IMAGE
                + " WHERE " + MySQLiteHelper.COLUMN_PATIENT_SERIAL_ID + " = " + String.valueOf(serialId), null);

        if (cursor == null){
            return null;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Image img = cursorToImage(cursor, webSever);
            list.add(img);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();

        return list;
    }




    public void deleteImage(Image img) {
        long serialId = img.getSerialId();
        System.out.println("Image is deleted with serialId: " + serialId);
        database.delete(MySQLiteHelper.TABLE_NAME_IMAGE, MySQLiteHelper.COLUMN_SERIALID
                + " = " + serialId, null);
    }

    public void deleteAllImages() {
        System.out.println("Delete all images");
        database.delete(MySQLiteHelper.TABLE_NAME_IMAGE, MySQLiteHelper.COLUMN_SERIALID
                + " > " + -1, null);
    }

    public void deleteAllImages(long patientSerialId) {
        System.out.println("Delete all images");
        database.delete(MySQLiteHelper.TABLE_NAME_IMAGE, MySQLiteHelper.COLUMN_SERIALID
                + " = " + String.valueOf(patientSerialId), null);
    }

    public List<Image> getAllImages(String webServer) {
        List<Image> list = new ArrayList<Image>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_NAME_IMAGE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Image img = cursorToImage(cursor, webServer);
            list.add(img);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return list;
    }

    public int countAllRecords() {

        int total = 0;
        Cursor cursor = database.rawQuery("SELECT * FROM " + MySQLiteHelper.TABLE_NAME_IMAGE, null);
        total = cursor.getCount();
        return total;
    }

    private Image cursorToImage(Cursor cursor, String webServer) {
        Image img = new Image();
        img.setSerialId(Long.valueOf(cursor.getString(0)));
        img.setPatientSerialId(Long.valueOf(cursor.getString(1)));
        img.setSequence(Integer.valueOf(cursor.getString(2)));
        img.setImageUrl(cursor.getString(3), webServer.toString());
        img.setImageUrlForFetch(cursor.getString(4));
        img.setImageWidth(cursor.getString(5));
        img.setImageHeight(cursor.getString(6));
        img.setPhotoData(cursor.getString(7));
        if (cursor.getString(8).compareToIgnoreCase("true") == 0){
            img.setFaceDetected(true);
            img.setNumberOfFacesDetected(1);
        }
        else {
            img.setFaceDetected(false);
            img.setNumberOfFacesDetected(0);
        }
//        img.setFaceDetected(Boolean.valueOf(cursor.getString(8))); // 8
        img.setRectX(Integer.valueOf(cursor.getString(9)));
        img.setRectY(Integer.valueOf(cursor.getString(10)));
        img.setRectH(Integer.valueOf(cursor.getString(11)));

        img.setRectW(Integer.valueOf(cursor.getString(12)));
        Rect rect = new Rect((float)img.getRectX(), (float)img.getRectY(), (float)img.getRectH(), (float)img.getRectW());
        img.setRect(rect);

        // to prevent from null
        // version 7.2.7-beta, version code 70207000
        String caption = cursor.getString(13);
        if (caption == null){
            caption = "";
        }
        img.setCaption(caption);

        img.setSize(Integer.valueOf(cursor.getString(14)));

        return img;
    }
}
