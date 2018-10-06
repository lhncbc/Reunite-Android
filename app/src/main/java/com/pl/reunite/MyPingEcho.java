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

import android.content.Context;
import android.os.Build;

import com.pl.reunite.Result.RestPingResult;
import com.pl.reunite.Result.Result;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

import static com.pl.reunite.WebServer.WAITING_TIME;

/**
 * Created on 4/3/14.
 * v34 starts use pinEcho. I over write it.
 * Now it is converted to the REST calls.
 * version 9.3.0
 */
public class MyPingEcho {
    private static final String METHOD_NAME = "pingEcho";
    private static final String METHOD_NAME_SEARCH_COMPLETE = "searchComplete";

    public static final int NETWORK_ERR = 1;
    public static final int TIME_OUT_ERR = 2;
    public String getErrorMessage(Context c, int errorCode){
        String errMsg = "";
        if (errorCode == NETWORK_ERR){
            errMsg = c.getResources().getString(R.string.you_do_not_have_internet_connection);
        }
        else if (errorCode == TIME_OUT_ERR){
            errMsg =  c.getResources().getString(R.string.time_out);
        }
        else {
            errMsg = c.getResources().getString(R.string.unknown);
        }
        return errMsg;
    }

    private static final String GUEST = "Guest";

    ReUnite app;
    private long timeStart = 0;
    private long timeEnd = 0;
    private long timeElapsed = 0;

    private String soapAction = "";
    private String methodName = "";
    private String nameSpace = "";
    private String url = "";

    private String username = "";
    private String token = "";
    private String pingString = ""; //machine name;device id;app name;app version;operating system;device username;pl username
    private String latency = "";

    private String webServer;
    private Context c;
    private String restEndPoint;

    public RestPingResult getResult() {
        return result;
    }

    public void setResult(RestPingResult result) {
        this.result = result;
    }

    private RestPingResult result;;

    MyPingEcho(String webServer){
        if (webServer.isEmpty()){
            soapAction = WebServer.PL_NAMESPACE + WebServer.END_POINT + METHOD_NAME;
            methodName = METHOD_NAME;
            nameSpace = WebServer.PL_NAMESPACE;
            url = WebServer.PL_URL;
            this.webServer = webServer;
            restEndPoint = WebServer.REST_ENDPOINT_PL;
        }
        else if (webServer.equalsIgnoreCase(WebServer.PL_NAME)){
            soapAction = WebServer.PL_NAMESPACE + WebServer.END_POINT + METHOD_NAME;
            methodName = METHOD_NAME;
            nameSpace = WebServer.PL_NAMESPACE;
            url = WebServer.PL_URL;
            restEndPoint = WebServer.REST_ENDPOINT_PL;
        }
        else if (webServer.equalsIgnoreCase(WebServer.PL_NAME_STAGE)){
            soapAction = WebServer.PL_NAMESPACE_STAGE + WebServer.END_POINT + METHOD_NAME;
            methodName = METHOD_NAME;
            nameSpace = WebServer.PL_NAMESPACE_STAGE;
            url = WebServer.PL_URL_STAGE;
            restEndPoint = WebServer.REST_ENDPOINT_STAGE;
        }
        token = "";

        result = new RestPingResult(c);
    }

    MyPingEcho(Context c, String token, String webServer){
        if (webServer.isEmpty()){
            soapAction = WebServer.PL_NAMESPACE + WebServer.END_POINT + METHOD_NAME;
            methodName = METHOD_NAME;
            nameSpace = WebServer.PL_NAMESPACE;
            url = WebServer.PL_URL;
            restEndPoint = WebServer.REST_ENDPOINT_PL;
        }
        else if (webServer.equalsIgnoreCase(WebServer.PL_NAME)){
            soapAction = WebServer.PL_NAMESPACE + WebServer.END_POINT + METHOD_NAME;
            methodName = METHOD_NAME;
            nameSpace = WebServer.PL_NAMESPACE;
            url = WebServer.PL_URL;
            restEndPoint = WebServer.REST_ENDPOINT_PL;
        }
        else if (webServer.equalsIgnoreCase(WebServer.PL_NAME_STAGE)){
            soapAction = WebServer.PL_NAMESPACE_STAGE + WebServer.END_POINT + METHOD_NAME;
            methodName = METHOD_NAME;
            nameSpace = WebServer.PL_NAMESPACE_STAGE;
            url = WebServer.PL_URL_STAGE;
            restEndPoint = WebServer.REST_ENDPOINT_STAGE;
        }
        this.token = token;

        result = new RestPingResult(c);
    }

    public String getManufacturer() {
        String manufacturer = Build.MANUFACTURER;
        manufacturer = manufacturer.toUpperCase();
        String model = Build.MODEL;
        model = model.toString();
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + " " + model;
        }
    }

    public String getUrl(){
        return this.url;
    }
    public void setUrl(String url){
        this.url = url;
    }

    public String getNameSpace(){
        return this.nameSpace;
    }
    public void setNameSpace(String nameSpace){
        this.nameSpace = nameSpace;
    }

    public String getSoapAction(){
        return this.soapAction;
    }
    public void setSoapAction(String soapAction){
        this.soapAction = soapAction;
    }

    public String getLatency(){
        return this.latency;
    }
    public void setLatency(String latency){
        this.latency = latency;
    }

    public String getUsername(){
        return this.username;
    }
    public void setUsername(String username){this.username = username;}
    public String getToken(){return this.token;}
    public void setToken(String token){
        this.token = token;
    }

    public String getLatencyTime(){
        return latency;
    }

    // REST calls
    /*
    {"call":"ping","token":"123","latitude":41.891855,"longitude":-87.598861}
    */

    public RestPingResult restCall(final Context c, final double latitude, final double longitude) {
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    try {
                        // timing - beginning
                        timeStart = System.currentTimeMillis();

                        // call
                        result = restPing(c, latitude, longitude);

                        // timing - end
                        timeEnd = System.currentTimeMillis();
                        timeElapsed = timeEnd - timeStart;
                        latency = String.valueOf(timeElapsed);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        result.setErrorCode("-1");
                        result.setErrorMessage(c.getResources().getString(R.string.you_do_not_have_internet_connection));
                    }
                }
            });
            futures.add(f);
        }

        // wait for all tasks to complete before continuing
        for (Future<Runnable> f : futures) {
            try {
                f.get(WAITING_TIME, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
                result.setErrorCode(Result.MY_ERROR_CODE);
                result.setErrorMessage(e.getMessage());
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return result;
    }
    public RestPingResult restPing(Context c, final double latitude, final double longitude) throws Exception {
        RestPingResult result = new RestPingResult(c);

        URL obj = new URL(restEndPoint);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildPingJson(c,latitude, longitude);
        if (jo == null){
            result.setErrorCode("-1");
            result.setErrorMessage(c.getResources().getString(R.string.unknown));
            return result;
        }

        // Send post request
        con.setDoOutput(true);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(con.getOutputStream(), "UTF-8"));
        bw.write(jo.toString());
        bw.flush();
        bw.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + jo.toString());
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        String jsonStr = "";
        while ((inputLine = in.readLine()) != null) {
            jsonStr = inputLine;
            if (jsonStr.contains("error")){
                jsonStr = inputLine;
            }
            else {
                jsonStr = "";
            }
        }
        in.close();

        JSONObject jsonResult = null;
        if (!jsonStr.isEmpty()) {
            jsonResult = new JSONObject(jsonStr);
            result.setErrorCode(jsonResult.get("error").toString());
            if (result.getErrorCode().equalsIgnoreCase("0")){
                return result;
            }
            else {
                result.searchErrorMessage();
            }
        }
        return result;
    }

    /**
     * rest call search
     */
    public JSONObject buildPingJson(Context c, final double latitude, final double longitude) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "ping");
            json.put("token", getToken());
            json.put("latitude", latitude);
            json.put("longitude", longitude);
        }
        catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }
}
