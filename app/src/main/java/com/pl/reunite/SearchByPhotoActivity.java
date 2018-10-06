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
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

/**
 * Created  on 5/20/2015.
 * Version 9.10: remove face detect, remove annotation
 *
 */
public class SearchByPhotoActivity extends Activity implements View.OnClickListener{
    static final int TAKE_PICTURE_REQUEST = 2;
    static final int QUERY_MEDIA = 4;
    static final int PICK_PHOTO_REQUEST = 5;
    private static final int SEARCH_BY_PHOTO = 9;

    private static final int GPS_PERMISSION_REQUEST_CODE = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    public static final int RUN_GPS = 100;
    public static final int RUN_MAP = 101;
    public static final int RUN_CAMERA = 102;

    ReUnite app;

    Button buttonCamera;
    Button buttonGallery;
    Button buttonDeleteImage;
    Button buttonStartSearch;

    ImageView imageViewPhoto;

    private Bitmap curBitmapPhoto = null;
    private String curEncodePhoto = "";

    //A ProgressDialog object
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_by_photo);

        app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        Initialize();
    }

    private void Initialize() {
        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(this);

        buttonGallery = (Button) findViewById(R.id.buttonGallery);
        buttonGallery.setOnClickListener(this);

        buttonDeleteImage = (Button) findViewById(R.id.buttonDeleteImage);
        buttonDeleteImage.setOnClickListener(this);

        buttonStartSearch = (Button) findViewById(R.id.buttonStartSearch);
        buttonStartSearch.setOnClickListener(this);

        imageViewPhoto = (ImageView) findViewById(R.id.imageViewPhoto);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonCamera:
                new checkPermissionAsyncTask(RUN_CAMERA, this).execute();
//                takePicture();
                break;
            case R.id.buttonGallery:
                pickPhoto();
                break;
            case R.id.buttonDeleteImage:
                deleteImage();
                break;
            case R.id.buttonStartSearch:
                startToSearch();
                break;
            default:
                break;

        }
    }

    private void startToSearch() {
        if (curEncodePhoto.isEmpty() == true){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.no_photo_is_selected))
                    .setCancelable(true)
                    .setTitle(getResources().getString(R.string.warning))
                    .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.a_photo_is_selected), Toast.LENGTH_SHORT).show();

            String encodedPhoto = "ENCODED_PHOTO";
            app.setCurEncodedImage(curEncodePhoto);
            Intent i = new Intent();
            i.putExtra("ENCODED_PHOTO", encodedPhoto);
            setResult(SEARCH_BY_PHOTO, i);
            this.finish();
        }
    }

    private void deleteImage() {
        if (curEncodePhoto.isEmpty()){
            Toast.makeText(this, getResources().getString(R.string.no_photo_is_found), Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_menu_crop)
                .setTitle(getResources().getString(R.string.warning))
                .setMessage(getResources().getString(R.string.are_you_sure_you_want_to_delete_the_selected_photo))
                .setCancelable(true)
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })
                .setNeutralButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        curBitmapPhoto = null;
                        curEncodePhoto = "";
                        imageViewPhoto.setImageDrawable(getResources().getDrawable(R.drawable.questionmark));
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void takePicture() {
        Intent i = startCamera();
        startActivityForResult(i, TAKE_PICTURE_REQUEST);
        app.setCameraIntent(i);
    }

    public Intent startCamera() {
        // Get a unique file name for uri.
        // Prepare information for uri.
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.ImageColumns.IS_PRIVATE, 1);
        values.put(MediaStore.MediaColumns.TITLE, System.currentTimeMillis());
        values.put(MediaStore.Images.ImageColumns.DESCRIPTION, "Image capture by camera");
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        // Prepare intent.
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.e("Get image from camera", uri.toString());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        return intent;
    }

    protected void pickPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_PHOTO_REQUEST);
    }

    @Override
    public void onBackPressed() {
        if (curEncodePhoto.isEmpty() == true){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.no_photo_is_selected_do_you_really_want_to_quit))
                    .setCancelable(true)
                    .setTitle(getResources().getString(R.string.warning))
                    .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();

                            String encodedPhoto = "";
                            app.setCurEncodedImage(encodedPhoto);
                            Intent i = new Intent();
                            i.putExtra("ENCODED_PHOTO", encodedPhoto);
                            setResult(SEARCH_BY_PHOTO, i);

                            SearchByPhotoActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            return;
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {
            Toast.makeText(this, getResources().getString(R.string.a_photo_is_selected), Toast.LENGTH_SHORT).show();

            String encodedPhoto = "ENCODED_PHOTO";
            app.setCurEncodedImage(curEncodePhoto);
            Intent i = new Intent();
            i.putExtra("ENCODED_PHOTO", encodedPhoto);
            setResult(SEARCH_BY_PHOTO, i);

            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TAKE_PICTURE_REQUEST){

            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent i = app.getCameraIntent();
            Uri uri = (Uri) i.getExtras().get(MediaStore.EXTRA_OUTPUT);
            curBitmapPhoto = Image.resizedBitmap(uri.toString(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false); // 9.0.

            // Convert to data byte
            ByteArrayOutputStream byteArrayPhoto = new ByteArrayOutputStream();
            curBitmapPhoto.compress(Bitmap.CompressFormat.PNG, Image.COMPRESS_RATE, byteArrayPhoto);
            byte[] photoData = byteArrayPhoto.toByteArray();
            curEncodePhoto = Base64.encodeToString(photoData, Base64.DEFAULT);

            /**
             * face detection
             */
            Image img = new Image();
            img.setPhotoData(curEncodePhoto);
            img.setPhotoBitmap(curBitmapPhoto);

            // no face dectection no draw
            img.DetectFaces(); // detect faces

            // display
            setMyImage(img);
        }
        else if (requestCode == QUERY_MEDIA){
            if (resultCode == 0) {// Cancel
                return;
            }
            Bundle extras = data.getExtras();
            String fileFullName = (String)extras.get("fileFullName");

            curBitmapPhoto = Shrinkmethod(fileFullName, Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT);

            // Convert to data byte
            ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
            curBitmapPhoto.compress(Bitmap.CompressFormat.PNG, Image.COMPRESS_RATE, byteArryPhoto);
            byte[] photoData = byteArryPhoto.toByteArray();
            curEncodePhoto = Base64.encodeToString(photoData, Base64.DEFAULT);

            imageViewPhoto.setImageBitmap(curBitmapPhoto);
        }
        else if (requestCode == PICK_PHOTO_REQUEST){
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Canceled.", Toast.LENGTH_SHORT).show();
                return;
            }
            Uri uri = data.getData();
            curBitmapPhoto = Image.resizedBitmap(uri.toString(), Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, this, false); // 9.0.

            // Convert to data byte
            ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
            curBitmapPhoto.compress(Bitmap.CompressFormat.PNG, Image.COMPRESS_RATE, byteArryPhoto);
            byte[] photoData = byteArryPhoto.toByteArray();
            curEncodePhoto = Base64.encodeToString(photoData, Base64.DEFAULT);

            /**
             * face detection
             */
            Image img = new Image();
            img.setPhotoData(curEncodePhoto);
            img.setPhotoBitmap(curBitmapPhoto);

            // no image detect and draw
//          img.DetectFaces(); // detect faces
            // display
            setMyImage(img);
        }
    }

    public void setMyImage(Image i) {
        Bitmap tmp = Bitmap.createBitmap(i.getPhotoBitmap(), 1, 1, i.getPhotoBitmap().getWidth() - 1, i.getPhotoBitmap().getHeight() - 1);
        /*
        Canvas canvas = new Canvas(tmp);
        canvas.drawBitmap(tmp, 0, 0, null);

        if (i.getNumberOfFacesDetected() >= 1){
            Paint facePaint = new Paint();
            facePaint.setColor(Color.GREEN);
            facePaint.setStyle(Paint.Style.STROKE);
            facePaint.setStrokeWidth(1); // changed from 3 to 1 in version 9.0.0

            Rect r = i.getRect();
            canvas.drawRect(
                    r.getX(),
                    r.getY(),
                    r.getX() + r.getW(),
                    r.getY() + r.getH(),
                    facePaint
            );
        }
        */
        imageViewPhoto.setImageDrawable(new BitmapDrawable(getResources(), tmp));
    }

    Bitmap Shrinkmethod(String file,int width,int height){
        BitmapFactory.Options bitopt=new BitmapFactory.Options();
        bitopt.inJustDecodeBounds=true;
        Bitmap bit=BitmapFactory.decodeFile(file, bitopt);

        int h=(int) Math.ceil(bitopt.outHeight/(float)height);
        int w=(int) Math.ceil(bitopt.outWidth/(float)width);

        if(h>1 || w>1){
            if(h>w){
                bitopt.inSampleSize=h;

            }else{
                bitopt.inSampleSize=w;
            }
        }
        bitopt.inJustDecodeBounds=false;
        bit=BitmapFactory.decodeFile(file, bitopt);
        return bit;
    }

    /**
     * Permissions
     * starting from SDK 23, need to enable permission in running time.
     * two permissions are needed:
     * fine location
     * external
     *
     * @return
     */

    private boolean checkPermission(int func, Context c) {
        int result;
        if (func == RUN_CAMERA){
            result = c.checkCallingOrSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        else {
            result = c.checkCallingOrSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private class checkPermissionAsyncTask extends AsyncTask<Void, Integer, Void> {
        private boolean permission = false;
        private int func;
        private Context c;

        checkPermissionAsyncTask(int func, Context c){
            this.func = func;
            this.c = c;
        }
        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage(getResources().getString(R.string.message_checking_permission_));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            //Get the current thread's token
            synchronized (this) {
                permission = checkPermission(func, c);
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            //close the progress dialog
            if (permission == false){
                new requestPermissionAsyncTask(func, c).execute();
            }
            else { // enabled
                if (func == RUN_CAMERA){
                    takePicture();
                }
                /*
                else if (func == RUN_GPS){
                    GetLocationFromGps();
                }
                else {
                    startGoogleMap();
                }
                */
            }
        }
    }

    private void requestPermission(int func) {
        if (func == RUN_CAMERA){
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, CAMERA_PERMISSION_REQUEST_CODE);
        }
        else { // RUN_GPS, RUN_MAP
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION_REQUEST_CODE);
        }
    }

    private class requestPermissionAsyncTask extends AsyncTask<Void, Integer, Void> {
        private int func;
        private Context c;

        requestPermissionAsyncTask(int func, Context c){
            this.func = func;
            this.c = c;
        }

        //Before running code in separate thread
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(c);
            progressDialog.setMessage(getResources().getString(R.string.message_requesting_permission_));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        //The code to be executed in a background thread.
        @Override
        protected Void doInBackground(Void... params) {
            //Get the current thread's token
            synchronized (this) {
                requestPermission(func);
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values) {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GPS_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.permission_is_granted_))
                            .setCancelable(false)
                            .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.permission_is_denied_))
                            .setCancelable(false)
                            .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
            case CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.permission_is_granted_))
                            .setCancelable(false)
                            .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getResources().getString(R.string.permission_is_denied_))
                            .setCancelable(false)
                            .setNegativeButton(getResources().getString(R.string.continue_word), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                break;
            default:
                break;
        }
    }
}
