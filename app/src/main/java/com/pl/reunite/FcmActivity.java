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
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pl.reunite.Result.RequestAnonTokenResult;
import com.pl.reunite.Result.RestGcmResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class FcmActivity extends Activity implements View.OnClickListener{
/*	private static final String AUTH_KEY = "MIGTAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBHkwdwIBAQQgz7jz3yxazkrcXix4\n" +
			"4pGpRrnyLgjX5MN175IsIQMDwaygCgYIKoZIzj0DAQehRANCAAR39jxpy6YrH7ln\n" +
			"Wmc807lTjwVlmEej8ms/e8ChGmwduycG3DtHAu07aCWKLe6KoLuK04qe6mpa7wcF\n" +
			"896FxCQA";
			*/

    private static final String AUTH_KEY = "1:352368471875:android:6f621476e01725a0";
    private TextView mTextView;
    private EditText etAnonToken;
    private Button buttonGetAnonToken;
    private Button buttonGetFcmToken;
    private Button buttonSubscribe;
    private Button buttonUnsubscribe;

    String fcmToken;

    ReUnite app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcm);

        // Initial with globe variables.
        app = ((ReUnite) this.getApplication());

        mTextView = findViewById(R.id.txt);
        etAnonToken = findViewById(R.id.editTextAnonToken);

        buttonGetAnonToken = findViewById(R.id.buttonGetAnonToken);
        buttonGetAnonToken.setOnClickListener(this);

        buttonGetFcmToken = findViewById(R.id.buttonGetFcmToken);
        buttonGetFcmToken.setOnClickListener(this);

        buttonSubscribe = findViewById(R.id.buttonSubscribe);
        buttonSubscribe.setOnClickListener(this);

        buttonUnsubscribe = findViewById(R.id.buttonUnsubscribe);
        buttonUnsubscribe.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String tmp = "";
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                tmp += key + ": " + value + "\n\n";
            }
            mTextView.setText(tmp);
            fcmToken = tmp;
//			etAnonToken.setText(tmp);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonGetAnonToken:
                getAnonymousToken();
                break;
            case R.id.buttonGetFcmToken:
                showToken();
                break;
            case R.id.buttonSubscribe:
                subscribeFcm();
                break;
            case R.id.buttonUnsubscribe:
                unsubscribeFcm();
                break;
            default:
                break;
        }
    }

    public String getAnonymousToken(){
        String errorCode = "";
        // get the anonymous token
        app.setWebServer(WebServer.PL_NAME_STAGE);

        WebServer ws = new WebServer(app.getWebServer());
        RequestAnonTokenResult requestAnonTokenResult = ws.restCallRequestAnonToken(this);
        String anonymousToken = requestAnonTokenResult.getTokenAnonymous();
        errorCode = requestAnonTokenResult.getErrorCode();
        String errorMsg = requestAnonTokenResult.getErrorMessage();
        int tokenStatus = ws.getTokenStatus();
        if (errorCode.equalsIgnoreCase("0") == true){
            app.setTokenAnonymous(anonymousToken);
            app.setTokenStatus(ReUnite.TOKEN_ANONYMOUS);
            app.recordTimeWhenGotAnonymousToken();

            etAnonToken.setText(app.getTokenAnonymous());
            return errorCode;
        }
        else {
            errorCode = "";
        }
        return errorCode;
    }

    public void showToken() {
        mTextView.setText(FirebaseInstanceId.getInstance().getToken());
        Log.i("token", FirebaseInstanceId.getInstance().getToken());
        fcmToken = FirebaseInstanceId.getInstance().getToken();
    }

    public void subscribeFcm() {
        RestGcmResult result = sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), true);
        Toast.makeText(this, "Subscribe Error Code: " + result.getErrorCode().toString(), Toast.LENGTH_LONG).show();
        app.setGcmTokenUploadMsg(result.getErrorMessage());
    }

    public void unsubscribeFcm() {
        RestGcmResult result = sendRegistrationToServer(FirebaseInstanceId.getInstance().getToken(), false);
        Toast.makeText(this, "Unsubscribe Error Code: " + result.getErrorCode().toString(), Toast.LENGTH_LONG).show();
        app.setGcmTokenUploadMsg(result.getErrorMessage());
    }

    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     */
    private RestGcmResult sendRegistrationToServer(String fcmToken, boolean sub) {
        // Add custom implementation, as needed.
        ReUnite app = ((ReUnite)this.getApplication());
        /**
         *
         */
        // start
        WebServer ws = new WebServer(app.getWebServer()); // modified in version 7.1.3
//        com.pl.reunite.WebServer ws = new com.pl.reunite.WebServer();
        // end
        ws.setTokenStatus(app.getTokenStatus());
        ws.setTokenAnonymous(app.getTokenAnonymous());
        ws.setToken(app.getToken());
        //ReportResult reportResult = ws.callSendGcmToken(gsmToken, this);
        RestGcmResult restReportResult = ws.restCallSendGcmToken(this, fcmToken, sub);
        Toast.makeText(this, "Subscribe Error Code: " + restReportResult.getErrorCode().toString(), Toast.LENGTH_LONG).show();
        return restReportResult;
    }

    public void sendToken(View view) {
        sendWithOtherThread("token");
    }

    public void sendTokens(View view) {
        sendWithOtherThread("tokens");
    }

    public void sendTopic(View view) {
        sendWithOtherThread("topic");
    }

    private void sendWithOtherThread(final String type) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                pushNotification(type);
            }
        }).start();
    }

    private void pushNotification(String type) {
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        JSONObject jData = new JSONObject();
        try {
            jNotification.put("title", "Google I/O 2016");
            jNotification.put("body", "Firebase Cloud Messaging (App)");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jNotification.put("icon", "ic_notification");

            jData.put("picture", "http://opsbug.com/static/google-io.jpg");

            switch(type) {
                case "tokens":
                    JSONArray ja = new JSONArray();
                    ja.put("c5pBXXsuCN0:APA91bH8nLMt084KpzMrmSWRS2SnKZudyNjtFVxLRG7VFEFk_RgOm-Q5EQr_oOcLbVcCjFH6vIXIyWhST1jdhR8WMatujccY5uy1TE0hkppW_TSnSBiUsH_tRReutEgsmIMmq8fexTmL");
                    ja.put(FirebaseInstanceId.getInstance().getToken());
                    jPayload.put("registration_ids", ja);
                    break;
                case "topic":
                    jPayload.put("to", "/topics/news");
                    break;
                case "condition":
                    jPayload.put("condition", "'sport' in topics || 'news' in topics");
                    break;
                default:
                    jPayload.put("to", FirebaseInstanceId.getInstance().getToken());
            }

            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            jPayload.put("data", jData);

            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", AUTH_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Send FCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());

            // Read FCM response.
            InputStream inputStream = conn.getInputStream();
            final String resp = convertStreamToString(inputStream);

            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    mTextView.setText(resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

}