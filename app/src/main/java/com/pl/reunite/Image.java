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

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

//import org.kobjects.base64.Base64;

/**
 * Created on 11/12/2014.
 * class Image includes all information of an image in current project.
 *
 */
public class Image {
    public static final String PRIMARY_IMAGE = "Primary Image";
    public static final String SECONDARY_IMAGE = "Secondary Image";

    //  To the compressor, 0-100. 0 meaning compress for small size, 100 meaning compress for max quality.
    // Some formats, like PNG which is lossless, will ignore the quality setting
    public static final int COMPRESS_RATE = 100;
    public static final int MAX_SIZE_ICON = 256;

    private static int MAX_NUMBER_OF_FACES_TO_DETECT = 10; // face detection

    private long serialId;
    private long patientSerialId;
    private int sequence;
    private String imageUrl;
    private String imageUrlForFetch;
    private String imageWidth;
    private String imageHeight;
    private String photoData;
    private Bitmap photoBitmap;
    private boolean faceDetected;
    private int rectX;
    private int rectY;
    private int rectH;
    private int rectW;
    private String caption;
    private int size;

    // face detection
    private int numberOfFacesDetected;
    private FaceDetector.Face[] faces;
    private Rect rect;

    public Image() {
        this.serialId = -1;
        this.patientSerialId = -1;
        this.sequence = -1;
        this.imageUrl = "";
        this.imageUrlForFetch = "";
        this.imageWidth = "";
        this.imageHeight = "";
        this.photoData = "";
        this.photoBitmap = null;
        this.faceDetected = false;
        this.rectX = 0;
        this.rectY = 0;
        this.rectH = 0;
        this.rectW = 0;
        this.caption = "";
        this.size = 0;

        this.numberOfFacesDetected = 0;
        this.faces = null;
        this.rect = new Rect();
    }

    public Image(long serialId, int patientSerialId, int sequence, String imageUrl, String imageUrlForFetch, String imageWidth, String imageHeight, String photoData, Bitmap photoBitmap, boolean faceDetected, int rectX, int rectY, int rectH, int rectW, String caption, int size) {
        this.serialId = serialId;
        this.patientSerialId = patientSerialId;
        this.sequence = sequence;
        this.imageUrl = imageUrl;
        this.imageUrlForFetch = imageUrlForFetch;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.photoData = photoData;
        this.photoBitmap = photoBitmap;
        this.faceDetected = faceDetected;
        this.rectX = rectX;
        this.rectY = rectY;
        this.rectH = rectH;
        this.rectW = rectW;
        this.caption = caption;
        this.size = size;

        faces = new FaceDetector.Face[MAX_NUMBER_OF_FACES_TO_DETECT];
        this.numberOfFacesDetected = 0;
        this.rect = new Rect();
    }

    Image(Image o) {
        this.serialId = o.getSerialId();
        this.patientSerialId = o.getPatientSerialId();
        this.sequence = o.getSequence();
        this.imageUrl = o.getImageUrl();
        this.imageUrlForFetch = o.getImageUrlForFetch();
        this.imageWidth = o.getImageWidth();
        this.imageHeight = o.getImageHeight();
        this.photoData = o.getPhotoData();
        this.photoBitmap = o.getPhotoBitmap();
        this.faceDetected = o.isFaceDetected();
        this.rectX = o.getRectX();
        this.rectY = o.getRectY();
        this.rectH = o.getRectH();
        this.rectW = o.getRectW();
        this.caption = o.getCaption();
        this.size = o.getSize();

        this.numberOfFacesDetected = o.getNumberOfFacesDetected();
        for (int i = 0; i < this.numberOfFacesDetected; i++){
            this.faces[i] = o.getFaces()[i];
        }
        this.rect = new Rect();
        this.setRect(o.getRect());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Image)) return false;

        Image image = (Image) o;

        if (faceDetected != image.faceDetected) return false;
        if (patientSerialId != image.patientSerialId) return false;
        if (rectH != image.rectH) return false;
        if (rectW != image.rectW) return false;
        if (rectX != image.rectX) return false;
        if (rectY != image.rectY) return false;
        if (sequence != image.sequence) return false;
        if (serialId != image.serialId) return false;
        if (size != image.size) return false;
        if (!caption.equals(image.caption)) return false;
        if (!imageHeight.equals(image.imageHeight)) return false;
        if (!imageUrl.equals(image.imageUrl)) return false;
        if (!imageUrlForFetch.equals(image.imageUrlForFetch)) return false;
        if (!imageWidth.equals(image.imageWidth)) return false;
        if (!photoData.equals(image.photoData)) return false;
        if (!photoBitmap.equals(image.photoBitmap)) return false;
        if (numberOfFacesDetected != image.getNumberOfFacesDetected()) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int)serialId;
        result = 31 * result + (int)patientSerialId;
        result = 31 * result + sequence;
        result = 31 * result + imageUrl.hashCode();
        result = 31 * result + imageUrlForFetch.hashCode();
        result = 31 * result + imageWidth.hashCode();
        result = 31 * result + imageHeight.hashCode();
        result = 31 * result + photoData.hashCode();
        result = 31 * result + photoBitmap.hashCode();
        result = 31 * result + (faceDetected ? 1 : 0);
        result = 31 * result + rectX;
        result = 31 * result + rectY;
        result = 31 * result + rectH;
        result = 31 * result + rectW;
        result = 31 * result + caption.hashCode();
        result = 31 * result + size;
        return result;
    }

    public long getSerialId() {
        return serialId;
    }

    public void setSerialId(long serialId) {
        this.serialId = serialId;
    }

    public long getPatientSerialId() {
        return patientSerialId;
    }

    public void setPatientSerialId(long patientSerialId) {
        this.patientSerialId = patientSerialId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl, String webServer) {
        this.imageUrl = imageUrl;
        if (this.imageUrl.isEmpty()){
            this.imageUrlForFetch = "";
            return;
        }

        if (webServer.equalsIgnoreCase(WebServer.PL_NAME) == true){
            this.imageUrlForFetch = WebServer.WEB_SERVER_PL + this.imageUrl;
        }
        else if (webServer.equalsIgnoreCase(WebServer.PL_NAME_STAGE) == true){
            this.imageUrlForFetch = WebServer.WEB_SERVER_PL_STAGE + this.imageUrl;
        }
        else {
            return;
        }
    }

    public String getImageUrlForFetch() {
        return imageUrlForFetch;
    }

    public void setImageUrlForFetch(String imageUrlForFetch) {
        this.imageUrlForFetch = imageUrlForFetch;
    }

    public String getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(String imageWidth) {
        this.imageWidth = imageWidth;
    }

    public String getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(String imageHeight) {
        this.imageHeight = imageHeight;
    }

    public String getPhotoData() {
        return photoData;
    }

    public void setPhotoData(String photoData) {
        this.photoData = photoData;
    }
    public Bitmap getPhotoBitmap() {
        return photoBitmap;
    }

    public void setPhotoBitmap(Bitmap photoBitmap) {
        this.photoBitmap = photoBitmap;
    }

    public boolean isFaceDetected() {
        return faceDetected;
    }

    public void setFaceDetected(boolean faceDetected) {
        this.faceDetected = faceDetected;
    }

    public void setRect(Rect rect){
        this.rect = rect;
    }
    public Rect getRect(){
        return this.rect;
    }

    public int getRectX() {
        return rectX;
    }

    public void setRectX(int rectX) {
        this.rectX = rectX;
    }

    public int getRectY() {
        return rectY;
    }

    public void setRectY(int rectY) {
        this.rectY = rectY;
    }

    public int getRectH() {
        return rectH;
    }

    public void setRectH(int rectH) {
        this.rectH = rectH;
    }

    public int getRectW() {
        return rectW;
    }

    public void setRectW(int rectW) {
        this.rectW = rectW;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setNumberOfFacesDetected(int numberOfFacesDetected){
        this.numberOfFacesDetected = numberOfFacesDetected;
    }
    public int getNumberOfFacesDetected() {
        return this.numberOfFacesDetected;
    }

    public void setFaces(FaceDetector.Face[] faces){
        this.faces = faces;
    }
    public FaceDetector.Face[] getFaces(){
        return this.faces;
    }

    public void downloadPatientPhoto(){
        if (imageUrlForFetch.endsWith("null") == true){
            photoBitmap = null;
            return;
        }
        if (imageUrlForFetch.isEmpty()){
            photoBitmap = null;
            return;
        }
        getImageFromUrlInAsyncWay();
    }

    public static String getEncodedFromBitmap(Bitmap picture){
        String str;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if (picture.compress(Bitmap.CompressFormat.PNG, Image.COMPRESS_RATE, stream) == false){
            return "";
        }
        byte[] b = stream.toByteArray();
        str = Base64.encodeToString(b, Base64.DEFAULT);
        return str;
    }

    /**
     * input: image url
     * output: bitmap
     */
    public void getImageFromUrlInAsyncWay(){
        Bitmap bitmap = null;
        // Better to use the threads.
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++)
        {
            Future f = service.submit(new Runnable() {
                public void run(){
                    getBitmapFromURL(imageUrlForFetch);
                }

                private void getBitmapFromURL(String src) {
                    try {
                        // if it is null
                        if (src.endsWith("null") == true){
                            photoBitmap = null;
                            return;
                        }

                        Log.e("src", src);
                        URL url = new URL(src);

                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setDoInput(true);
                        connection.setDoOutput(true);
                        connection.setReadTimeout(10000);
                        connection.connect();
                        InputStream input = connection.getInputStream();
//			            Bitmap myBitmap = new Bitmap;
                        photoBitmap = BitmapFactory.decodeStream(input);
                        Log.e("Bitmap","returned");
                        return;
//			            return photo;
//			            return myBitmap;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("Exception", e.getMessage());
                        photoBitmap = null;
                        return;
//			            return null;
                    }
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures)
        {
            try {
                f.get(WebServer.WAITING_TIME, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread
//	    return bitmap;
    }

    public void encodePhoto(){
        ByteArrayOutputStream byteArryPhoto = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.PNG, Image.COMPRESS_RATE,byteArryPhoto);
        byte[] b = byteArryPhoto.toByteArray();
        this.photoData = Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void decodePhoto(){
        if (photoBitmap != null){
            return;
        }
        if (photoData.length() <= 0){
            return;
        }
        byte[] data = Base64.decode(photoData, Base64.DEFAULT);
        if (data.length > 0) {
            photoBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        }
    }

    // input path, width, height
    // output bitmap
    // ImageFileNameToBitmap
    public static Bitmap resizedBitmap(String path, int reqWidth, int reqHeight, Context c, boolean face) {
        face = true; // always true. This is to enable face detection. changed in version 9.0.0
        Uri uri;
        ContentResolver mContentResolver = c.getContentResolver();
        if (path.contains("content:")){
            uri = Uri.parse(path);
        }
        else{
            //  uri = Uri.parse("file://" + path);
            uri = Uri.fromFile(new File(path));
        }

        try {
            InputStream in;
            in = mContentResolver.openInputStream(uri);
            // First decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, options);
            in.close();

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            if (face) {
                options.inPreferredConfig = Bitmap.Config.RGB_565;
            }
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, options);
            if (face && b.getWidth() % 2 != 0) {
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth() - 1,
                        b.getHeight());
            }

            return b;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                if (reqHeight > Patient.PHOTO_HEIGHT)
                    reqHeight = Patient.PHOTO_HEIGHT;
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                if (reqWidth > Patient.PHOTO_WIDTH)
                    reqWidth = Patient.PHOTO_WIDTH;
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public void DetectFaces() {
        System.gc();
        faces = new FaceDetector.Face[MAX_NUMBER_OF_FACES_TO_DETECT];
        FaceDetector fd = new FaceDetector(photoBitmap.getWidth(), photoBitmap.getHeight(), MAX_NUMBER_OF_FACES_TO_DETECT);
        numberOfFacesDetected = fd.findFaces(photoBitmap, faces);
        Log.e("Detectd faces: ", String.valueOf(numberOfFacesDetected));

        if (numberOfFacesDetected <= 0){
            return;
        }

        if (numberOfFacesDetected == 1){
            rect = FaceToRect(faces[0]);
            rectX = (int)rect.getX();
            rectY = (int)rect.getY();
            rectH = (int)rect.getH();
            rectW = (int)rect.getW();
            faceDetected = true;
        }
    }

    public Rect FaceToRect(FaceDetector.Face f){
        Rect r = new Rect();
        PointF midPoint = new PointF();
        f.getMidPoint(midPoint);

        float a = (float) 2.3;
        float b = (float) 2.3;

        r.setW(a * f.eyesDistance());
        r.setH(b * f.eyesDistance());

        PointF startPoint = new PointF();
        r.setX((float) (midPoint.x - r.getW()/2.0));
        r.setY((float) (midPoint.y - r.getH()/3.0));

        return r;
    }

}


