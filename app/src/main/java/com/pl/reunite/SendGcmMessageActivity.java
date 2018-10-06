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
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

public class SendGcmMessageActivity extends Activity implements View.OnClickListener{
//    public static String SERVER_API_KEY = "AIzaSyCxJS35ULBhO_go-NwkYn6YUVLRedbEYWQ";
//    public static String SENDER_ID = "430336049044";

    ReUnite app;
    private String gcmToken;
    private String sendId;
    private String serverApiKey;
    private String msg;
    private String errMsg;

    private TextView textViewGoogleProjectNumber;
    private TextView textViewApiKey;
    private TextView textViewRegistrationToken;
    private TextView textViewErrorMessage;
    private EditText editTextGCMMessage;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_gcm_message);

        app = ((ReUnite)this.getApplication());
        gcmToken = app.getGcmToken();
        sendId = getString(R.string.gcm_lpf_messaging_sender_id);
        sendId.trim();
        serverApiKey = getString(R.string.gcm_lpf_messaging_api_key);
        serverApiKey.trim();

        textViewGoogleProjectNumber = (TextView) findViewById(R.id.textViewGoogleProjectNumber);
        textViewGoogleProjectNumber.setText(sendId);

        textViewApiKey = (TextView) findViewById(R.id.textViewApiKey);
        textViewApiKey.setText(serverApiKey);

        textViewRegistrationToken = (TextView) findViewById(R.id.textViewRegistrationToken);
        textViewRegistrationToken.setText(gcmToken);

        textViewErrorMessage = (TextView) findViewById(R.id.textViewErrorMessage);
        textViewErrorMessage.setText("");

        editTextGCMMessage = (EditText) findViewById(R.id.editTextGCMMessage);
        editTextGCMMessage.setText("This is my message.");

        buttonSend = (Button) findViewById(R.id.buttonSend);
        buttonSend.setOnClickListener(this);
    }

    public  String send(String msg, String gcmToken) {
        if (msg == null || msg.isEmpty() || gcmToken == null || gcmToken.isEmpty()) {
            errMsg = "Message or GCM token is not defined.";
            return errMsg;
        }
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.
            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            try {
                jData.put("message", msg.trim());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // Where to send GCM message.
            if (!gcmToken.isEmpty()) {
                try {
                    jGcmData.put("to", gcmToken.trim());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    jGcmData.put("to", "/topics/global");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            // What to send in GCM message.
            try {
                jGcmData.put("data", jData);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + serverApiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            errMsg = resp + "Check your device/emulator for notification or logcat for " + "confirmation of the receipt of the GCM message.";
        } catch (IOException e) {
            errMsg = "Unable to send GCM message." + "Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).";
        }
        return errMsg;
    }

    public String callSend(final String msg, final String gcmToken) {

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    errMsg = send(msg, gcmToken);
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures) {
            try {
                f.get(10000, TimeUnit.SECONDS);
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

        return errMsg;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonSend:
                String str = editTextGCMMessage.getText().toString();
                str.trim();
                errMsg = callSend(str, gcmToken);
                textViewErrorMessage.setText(errMsg);
                break;
            default:
                break;

        }
    }
}
