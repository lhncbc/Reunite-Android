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
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This report handles the camera and also provides static messages for
 * obtaining image bitmapss, sizes, and sha1hash digests
 * 
 * @author bonifantmc
 * 
 */
public class ReportPatientImageHandler {
	// identifier for luanching camera and getting result.
	public static int IMAGE_CAPTURE = 1313;
	public static int IMAGE_FIND_EX = 1414;
	public static int IMAGE_PRIV_GAL = 1515;
	private Uri imageUri;
	private Context c;
	
	public Uri getImageUri(){
		return this.imageUri;
	}
	public void setImageUri(Uri imageUri){
		this.imageUri = imageUri;
	}

	ReportPatientImageHandler(Context c) {
		this.c = c;
	}

	/**
	 * prepare an inten to start the camera
	 * 
	 * @return
	 */
	public Intent startCamera() {
		// Get a unique file name for uri.
		// Prepare information for uri.
		ContentValues values = new ContentValues();
        values.put(ImageColumns.IS_PRIVATE, 1);
		values.put(MediaColumns.TITLE, System.currentTimeMillis());
		values.put(ImageColumns.DESCRIPTION, "Image capture by camera");
        values.put(MediaColumns.MIME_TYPE, "image/jpeg");
		this.imageUri = c.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

		// Prepare intent.
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		Log.e("Get image from camera", this.imageUri.toString());
		intent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
		return intent;
	}
	
	/**
	 * Takes the onActivityResult parameters of the ReportPatient class and uses
	 * them to resolve the either selected or captured image. Images are first
	 * resized to an appropriate size. After resizing the images, if the image
	 * was captured from the camera any instances of the image are removed from
	 * the sd card. Then the image is stored privately on the app's memory.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @return Image image uri, size and digest
	 */
	protected Image onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode == Activity.RESULT_CANCELED){
			Toast.makeText(c, "Add Photo cancelled.", Toast.LENGTH_LONG).show();
			return null;
		}
		
		if (requestCode == IMAGE_FIND_EX || requestCode == IMAGE_PRIV_GAL){
			this.imageUri = data.getData();
		}
		
		if (requestCode == IMAGE_CAPTURE){
//            this.imageUri = (Uri) data.getExtras().get(MediaStore.EXTRA_OUTPUT);
            this.imageUri = (Uri) data.getExtras().get(MediaStore.EXTRA_OUTPUT);
		}

		String uri = this.imageUri.toString();
		Log.e("Get image back", this.imageUri.toString());
		String size = getSize(uri, c);
		String digest = getDigest(uri, c);

        // here is where get the uri, so bitmap and encoded as well.
        // rewrite the followings
        // changed in version 9.0.0
        Bitmap bitmap = resizedBitmap(uri, Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, c, false); // 9.0.0
        String encoded = Patient.encodeBitmapToString(bitmap);
//        String encoded = getEncoded(uri, c); // 9.0.0

		Image img = new Image();
        img.setPhotoBitmap(bitmap); // 9.0.0

///		img.setUriReal(this.imageUri);
//		return new Image(uri, digest, size, null);
		return img;
	}

    // added in 9.0.0
    private String getEncoded(String uri, Context c) {
        Bitmap picture = resizedBitmap(uri, Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, c, false);
        return Image.getEncodedFromBitmap(picture);
    }

    /**
	 * obtain the image digest
	 * 
	 * @param uri
	 * @param c
	 * @return
	 */
    // input image path
    // output encode string
	protected static String getDigest(String uri, Context c) {
		Bitmap picture = resizedBitmap(uri, Patient.PHOTO_WIDTH, Patient.PHOTO_HEIGHT, c, false);

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		picture.compress(Bitmap.CompressFormat.PNG, Image.COMPRESS_RATE, stream);
		byte[] arr = stream.toByteArray();
		picture.recycle();

		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA1");
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
		}
		byte[] res = md.digest(arr);

		StringBuilder sb = new StringBuilder();
		for (byte b : res)
			sb.append(String.format("%02X", b));
		System.out.println(sb);
		return sb.toString();
	}

	/**
	 * calculate the size of the image
	 * 
	 * @param path
	 * @param c
	 * @return
	 */
    // input image absolute path
    // output image size with * height
	protected static String getSize(String path, Context c) {
		InputStream in = null;
		try {
			if (path.contains("content"))
				in = c.getContentResolver().openInputStream(Uri.parse(path));
			else
				in = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		/* Decode image size */
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, o);
		int size = o.outWidth * o.outHeight;
		return String.valueOf(size);
	}

	/**
	 * This takes an image's bitmap and returns its absolute path as a string.
	 * 
	 * @throws java.io.IOException
	 */
    // input purposed image path + path
    // output a jpq file (absolute path)
	public String save(String uri) {
		File dir = c.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_MULTI_PROCESS);
		File internalFile = new File(dir, System.currentTimeMillis() + ".jpg");

		InputStream in;
		try {
			in = c.getContentResolver().openInputStream(Uri.parse(uri));
			OutputStream out = new FileOutputStream(internalFile);
			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1)
				out.write(buffer, 0, read);
			in.close();
			out.flush();
			out.close();
			c.getContentResolver().delete(Uri.parse(uri), null, null);
			return internalFile.getAbsolutePath();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return uri;
	}

	/**
	 * calculate an images scale size. If for some reason I enter higher than
	 * 500, enfore the max size as 500*500 pixels since few devices will ever
	 * need more than that for this
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
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

	/**
	 * parses the given string into a uri and then decodes the image at this
	 * uri. If the image is to be used for face detection, the image is created
	 * with the limit Config.RGB_565 per requisite of android FaceDetector
	 * class.
	 * 
	 * @param path
	 *            String: location in memory of image
	 * @param reqWidth
	 *            int: max width of image
	 * @param reqHeight
	 *            int: max height of image
	 * @param c
	 *            context: to access images
	 * @param face
	 *            boolean: image is for detecting faces
	 * @return the desired bitmap
	 */

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
				options.inPreferredConfig = Config.RGB_565;
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
}
