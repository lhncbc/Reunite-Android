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
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pl.reunite.Result.PingEchoResult;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RestfulActivity extends Activity implements View.OnClickListener{
    ReUnite app;

    ProgressDialog progress;
    TextView textViewContent;
    Button buttonGet;
    Button buttonSearch;
    Button buttonReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restful);

        app = ((ReUnite)this.getApplication());
        app.detectMobileDevice(this);
        app.setScreenOrientation(this);

        Initialize();
    }

    private void Initialize() {
        textViewContent = (TextView) findViewById(R.id.textViewContent);

        buttonGet = (Button) findViewById(R.id.buttonGet);
        buttonGet.setOnClickListener(this);

        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);

        buttonReport = (Button) findViewById(R.id.buttonReport);
        buttonReport.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonGet:
//                restGet();
                System.out.println("Testing 1 - Send Http GET request");
                try {
                    new SendGetAsyncTask(this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.buttonSearch:
                System.out.println("\nSearch");
                try {
                    new SendPostAsyncTask(this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.buttonReport:
                System.out.println("\nReport");
                try {
                    new ReportRestCallAsyncTask(this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;

        }
    }

    private void writeStream(OutputStream out) {
    }

    private void readStream(InputStream in) {
        int data = 0;
        try {
            data = in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(data != -1){
            System.out.print((char) data);
            try {
                data = in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String readStreamToStr(InputStream in){
        Scanner s = null;
        try {
            s = new Scanner(in).useDelimiter("\\A");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (s == null) {
            return "";
        }
        String result = s.hasNext() ? s.next() : "";
        return result;
    }

    @Override
    public void onResume()
    {
        super.onResume();
//        registerReceiver(receiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK));
    }

    @Override
    public void onPause()
    {
        super.onPause();
//        unregisterReceiver(receiver);
    }

    /**
     * Our Broadcast Receiver. We get notified that the data is ready this way.
     */
    /*
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            // clear the progress indicator
            if (progress != null) {
                progress.dismiss();
            }
            String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
            textViewContent.setText(response);
            Log.i(TAG, "RESPONSE = " + response);
            //
            // my old json code was here. this is where you will parse it.
            //
        }
    };
    */

    // HTTP GET request
    public String sendGet() throws Exception {
        String returnStr = "";
        URL url = new URL("http://www.android.com/");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        try {
            InputStream in = new BufferedInputStream(con.getInputStream());
            returnStr = readStreamToStr(in);
        }
        catch (Exception e){
            System.out.print(e.getMessage());
        }
        finally {
            con.disconnect();
        }
        return returnStr;
    }

    //To use the AsyncTask, it must be subclassed
    private class SendGetAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        PingEchoResult pingEchoResult;
        String returnStr = "";
        ProgressDialog progressDialog;

        private int func;
        private Context c;

        SendGetAsyncTask(Context c){
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
        protected Void doInBackground(Void... params)
        {
            //Get the current thread's token
            synchronized (this)
            {
                try {
                    returnStr = sendGet();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            progressDialog.dismiss();
            textViewContent.setText(returnStr);
        }
    }

    // HTTP POST request
    public String sendPost() throws Exception {
        String returnStr = "";

        System.out.println("\nTesting 2 - Send Http POST request");

        WebServer ws = new WebServer(app.getWebServer());
        String url = ws.getRestEndpoint();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
//        con.setRequestProperty("User-Agent", MK_USER_AGENT);
//        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        /*
        con.setRequestProperty("call", "search");
        con.setRequestProperty("token", "8f8ec4b135ed9932d423766d69b91c318695de0e50f1cf325cbdc1f1c1e7de985c54caaf05fa8573d670849e4741c7f3e146fbc0a43facf60bf1b5758512cd55");
        con.setRequestProperty("since", "1970-01-01T01:23:45Z");
        con.setRequestProperty("pageStart", "0");
        con.setRequestProperty("perPage", "50");
        */

        String urlParameters = "{\n";
        urlParameters = urlParameters + "  \"token\": \"";
//        urlParameters = urlParameters + "8f8ec4b135ed9932d423766d69b91c318695de0e50f1cf325cbdc1f1c1e7de985c54caaf05fa8573d670849e4741c7f3e146fbc0a43facf60bf1b5758512cd55";
        urlParameters = urlParameters + app.getToken();
        urlParameters = urlParameters + "\",\n" +
                "  \"call\": \"search\",\n" +
                "  \"short\": \"global\",\n" +
                "  \"query\": \"\",\n" +
                "  \"photo\": \"\",\n" +
                "  \"sexMale\": true,\n" +
                "  \"sexFemale\": true,\n" +
                "  \"sexOther\": true,\n" +
                "  \"sexUnknown\": true,\n" +
                "  \"ageChild\": true,\n" +
                "  \"ageAdult\": true,\n" +
                "  \"ageUnknown\": true,\n" +
                "  \"statusMissing\": true,\n" +
                "  \"statusAlive\": true,\n" +
                "  \"statusInjured\": true,\n" +
                "  \"statusDeceased\": true,\n" +
                "  \"statusUnknown\": true,\n" +
                "  \"statusFound\": true,\n" +
                "  \"hasImage\": false,\n" +
                "  \"since\": \"1970-01-01T01:23:45Z\",\n" +
                "  \"pageStart\": 0,\n" +
                "  \"perPage\": 50,\n" +
                "  \"sortBy\": \"\"\n" +
                "}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        JSONObject jo = new JSONObject(response.toString());
        String err = jo.get("error").toString();
        if (err.equalsIgnoreCase("0")){
            String results = jo.get("results").toString();
        }

        System.out.println(response.toString());
        returnStr = response.toString();
        return  returnStr;
    }

    //To use the AsyncTask, it must be subclassed
    private class SendPostAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        String returnStr = "";
        ProgressDialog progressDialog;

        private int func;
        private Context c;

        SendPostAsyncTask(Context c){
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
        protected Void doInBackground(Void... params)
        {
            //Get the current thread's token
            synchronized (this)
            {
                try {
                    returnStr = sendPost();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            progressDialog.dismiss();
            textViewContent.setText(returnStr);
        }
    }

    // HTTP POST request
    public String reportRestCall() throws Exception {
        String returnStr = "";

        WebServer ws = new WebServer(app.getWebServer());
        String url = ws.getRestEndpoint();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        String urlParameters = "{\n";
        urlParameters = urlParameters + "  \"token\": \"";
        urlParameters = urlParameters + app.getToken();
        urlParameters = urlParameters + "\",\n" +
                "  \"call\": \"report\",\n" +
                "  \"short\": \"test\",\n" +
                "  \"name\": \"吴名字\",\n" +
                "  \"age\": \"66\",\n" +
                "  \"format\": \"PA1\",\n" +
                "  \"pa\": \"0\",\n" +
                "  \"sortBy\": \"\"\n" +
                "}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        String[] strArry = new String[0];
        int i = 0;
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
            strArry[i++] = inputLine.toString();
        }
        in.close();

        //print result
        System.out.println(response.toString());
        returnStr = response.toString();
        return  returnStr;

    }

    //To use the AsyncTask, it must be subclassed
    private class ReportRestCallAsyncTask extends AsyncTask<Void, Integer, Void>
    {
        PingEchoResult pingEchoResult;
        String returnStr = "";
        ProgressDialog progressDialog;

        private int func;
        private Context c;

        ReportRestCallAsyncTask(Context c){
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
        protected Void doInBackground(Void... params)
        {
            //Get the current thread's token
            synchronized (this)
            {
                try {
                    returnStr = reportRestCall();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        //Update the progress
        @Override
        protected void onProgressUpdate(Integer... values)
        {
        }

        //after executing the code in the thread
        @Override
        protected void onPostExecute(Void result)
        {
            progressDialog.dismiss();
            textViewContent.setText(returnStr);
        }
    }


}
