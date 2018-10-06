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
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.pl.reunite.Result.FollowRecordResult;
import com.pl.reunite.Result.ForgotUsernameResult;
import com.pl.reunite.Result.RegisterUserResult;
import com.pl.reunite.Result.ReportResult;
import com.pl.reunite.Result.RequestAnonTokenResult;
import com.pl.reunite.Result.RequestUserTokenResult;
import com.pl.reunite.Result.ResetUserPasswordResult;
import com.pl.reunite.Result.RestCommentResult;
import com.pl.reunite.Result.RestEventsResult;
import com.pl.reunite.Result.RestFollowRecordResult;
import com.pl.reunite.Result.RestFollowResult;
import com.pl.reunite.Result.RestForgotUsernameResult;
import com.pl.reunite.Result.RestGcmResult;
import com.pl.reunite.Result.RestMessageResult;
import com.pl.reunite.Result.RestRequestAnonTokenResult;
import com.pl.reunite.Result.RestResult;
import com.pl.reunite.Result.RestSearchResult;
import com.pl.reunite.Result.Result;
import com.pl.reunite.Result.SearchResult;
import com.pl.reunite.Result.UpdateReportResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.net.ssl.HttpsURLConnection;

/**
 * Upgraded to v34 by zli
 * 10/24/2014
 * I intend to put all web service calls here.
 */
public class WebServer {
    public static final String TIME_OUT = "Time Out!";
    public static final int WAITING_TIME = 10000; // added in version 7.1.5

    /**
     * Web Server Switch
     * Note: after the change, you will need uninstall existed app first.
     */
    // PL definitions - start
    public static final String PL_NAME = "PeopleLocator";
    public static final String PL_SHORT_NAME = "PL";
    public static final String PL_WEB_SERVICE = "www.your_domain.com/";
    public static final String PL_NAMESPACE = "https://www.your_domain.com/plusWebServices/";
    public static final String PL_URL = "https://www.your_domain.com/plus1";
    public static final String WEB_SERVER_PL = "https://www.your_domain.com/";

    public static final String REST_ENDPOINT_PL = "https://www.your_domain.com/rest_endpoint";
    // PL definitions - end

    // PLSTAGE definitions - start
    public static final String PL_NAME_STAGE = "PeopleLocatorStage";
    public static final String PL_SHORT_NAME_STAGE = "PS";
    public static final String PL_WEB_SERVICE_STAGE = "www.your_stage_domain.com/";
    public static final String PL_NAMESPACE_STAGE = "https://www.your_stage_domain.com/plusWebServices/";
    public static final String PL_URL_STAGE = "https://www.your_stage_domain.com/plus1"; // 6.1.8
    public static final String WEB_SERVER_PL_STAGE = "https://www.your_stage_domain.com/";

    public static final String REST_ENDPOINT_STAGE = "https://www.your_stage_domain.com/rest_endpoint";
    // PLSTAGE definitions - end

    public static final String END_POINT = "plus1#"; // 6.1.8
    public static final String NAMESPACE_PLUS = "plusWebServices/";

    private long id;
    private String name;
    private String shortName;
    private String webService;
    private String url;
    private String nameSpace;

    private String token = "";
    private String tokenAnonymous = "";
    private int tokenStatus = ReUnite.TOKEN_UNKNOWN;

    private SharedPreferences settings;// use to get webservice and crednetials
    private String errorCode = "";
    private String errorMessage;
    private double timeElapsed;

    private boolean searchCountOnly;

    public void setSearchCountOnly(boolean searchCountOnly) {
        this.searchCountOnly = searchCountOnly;
    }

    public boolean getSearchCountOnly() {
        return this.searchCountOnly;
    }

    private String searchSortBy;

    public void setSearchSortBy(String searchSortBy) {
        this.searchSortBy = searchSortBy;
    }

    public String getSearchSortBy() {
        return this.searchSortBy;
    }

    private int curPageStart;

    public void setCurPageStart(int curPageStart) {
        this.curPageStart = curPageStart;
    }

    public int getCurPageStart() {
        return curPageStart;
    }

    private RestSearchResult restSearchResult;

    public RestSearchResult getRestSearchResult() {
        return restSearchResult;
    }

    public void setRestSearchResult(RestSearchResult restSearchResult) {
        this.restSearchResult = restSearchResult;
    }

    private SearchResult searchResult;

    public SearchResult getSearchResult() {
        return searchResult;
    }

    public void setSearchResult(SearchResult searchResult) {
        this.searchResult = searchResult;
    }

    private RequestAnonTokenResult requestAnonTokenResult;

    public RequestAnonTokenResult getRequestAnonTokenResult() {
        return requestAnonTokenResult;
    }

    public void setRequestAnonTokenResult(RequestAnonTokenResult requestAnonTokenResult) {
        this.requestAnonTokenResult = requestAnonTokenResult;
    }

    RestForgotUsernameResult restForgotUsernameResult;
    public RestForgotUsernameResult getRestForgotUsernameResult() {
        return restForgotUsernameResult;
    }
    public void setRestForgotUsernameResult(RestForgotUsernameResult restForgotUsernameResult) {
        this.restForgotUsernameResult = restForgotUsernameResult;
    }

    private ReportResult reportResult;

    public ReportResult getReportResult() {
        return reportResult;
    }

    public void setReportResult(ReportResult reportResult) {
        this.reportResult = reportResult;
    }

    private RestGcmResult restGcmResult;

    private UpdateReportResult updateReportResult;

    public UpdateReportResult getUpdateReportResult() {
        return updateReportResult;
    }

    public void setUpdateReportResult(UpdateReportResult updateReportResult) {
        this.updateReportResult = updateReportResult;
    }

    private ResetUserPasswordResult resetUserPasswordResult;

    public ResetUserPasswordResult getResetUserPasswordResult() {
        return resetUserPasswordResult;
    }

    public void setResetUserPasswordResult(ResetUserPasswordResult resetUserPasswordResult) {
        this.resetUserPasswordResult = resetUserPasswordResult;
    }

    private ForgotUsernameResult forgotUsernameResult;

    public ForgotUsernameResult getForgotUsernameResult() {
        return forgotUsernameResult;
    }

    public void setForgotUsernameResult(ForgotUsernameResult forgotUsernameResult) {
        this.forgotUsernameResult = forgotUsernameResult;
    }

    private RegisterUserResult registerUserResult;

    public RegisterUserResult getRegisterUserResult() {
        return registerUserResult;
    }

    public void setRegisterUserResult(RegisterUserResult registerUserResult) {
        this.registerUserResult = registerUserResult;
    }

    private RequestUserTokenResult requestUserTokenResult;
    public RequestUserTokenResult getRequestUserTokenResult() {
        return requestUserTokenResult;
    }
    public void setRequestUserTokenResult(RequestUserTokenResult requestUserTokenResult) {
        this.requestUserTokenResult = requestUserTokenResult;
    }

    private FollowRecordResult followRecordResult;
    public FollowRecordResult getFollowRecordResult() {
        return followRecordResult;
    }
    public void setFollowRecordResult(FollowRecordResult followRecordResult) {
        this.followRecordResult = followRecordResult;
    }

    RestFollowRecordResult restFollowRecordResult;
    public RestFollowRecordResult getRestFollowRecordResult() {
        return restFollowRecordResult;
    }
    public void setRestFollowRecordResult(RestFollowRecordResult restFollowRecordResult) {
        this.restFollowRecordResult = restFollowRecordResult;
    }

    public RestFollowResult getRestFollowResult() {
        return restFollowResult;
    }
    public void setRestFollowResult(RestFollowResult restFollowResult) {
        this.restFollowResult = restFollowResult;
    }
    private RestFollowResult restFollowResult;

    // message
    public RestMessageResult getRestMessageResult() {
        return restMessageResult;
    }
    public void setRestMessageResult(RestMessageResult restMessageResult) {
        this.restMessageResult = restMessageResult;
    }
    private RestMessageResult restMessageResult;

    public RestCommentResult getRestCommentResult() {
        return restCommentResult;
    }

    public void setRestCommentResult(RestCommentResult restCommentResult) {
        this.restCommentResult = restCommentResult;
    }

    private RestCommentResult restCommentResult;

    private String query;

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
    }

    public Biscuit biscuit;

    // restful calls
    private String restEndpoint;
    public void setRestEndpoint(String restEndpoint){
        this.restEndpoint = restEndpoint;
    }
    public String getRestEndpoint(){
        return this.restEndpoint;
    }

    // test for server - start
    public WebServer() {
        if (this.name.isEmpty()) {
            this.name = this.PL_NAME;
            this.shortName = this.PL_SHORT_NAME;
            this.webService = this.PL_WEB_SERVICE;
            this.url = this.PL_URL;
            this.nameSpace = this.PL_NAMESPACE;
            this.restEndpoint = this.REST_ENDPOINT_PL;
        }
        else if (this.name.equalsIgnoreCase(this.PL_NAME)) {
            this.shortName = this.PL_SHORT_NAME;
            this.webService = this.PL_WEB_SERVICE;
            this.url = this.PL_URL;
            this.nameSpace = this.PL_NAMESPACE;
            this.restEndpoint = this.REST_ENDPOINT_PL;
        } else {
            this.name = this.PL_NAME_STAGE;
            this.shortName = this.PL_SHORT_NAME_STAGE;
            this.webService = this.PL_WEB_SERVICE_STAGE;
            this.url = this.PL_URL_STAGE;
            this.nameSpace = this.PL_NAMESPACE_STAGE;
            this.restEndpoint = this.REST_ENDPOINT_STAGE;
        }

        this.token = "";
        this.tokenAnonymous = "";
        this.tokenStatus = ReUnite.TOKEN_UNKNOWN;
        this.searchCountOnly = true; // count for default;
        this.searchSortBy = ""; // default to empty
        this.query = "";
    }
    // end of test for server

    public WebServer(WebServer w) {
        this.id = w.getId();
        this.name = w.getName();
        this.shortName = w.getShortName();
        this.webService = w.getWebService();
        this.url = w.getUrl();
        this.nameSpace = w.getNameSpace();
        this.token = w.getToken();
        this.tokenAnonymous = w.getTokenAnonymous();
        this.tokenStatus = w.getTokenStatus();
        this.searchCountOnly = w.getSearchCountOnly();
        this.searchSortBy = w.getSearchSortBy();
        this.query = w.getQuery();
        this.restEndpoint = w.getRestEndpoint();
    }

    public WebServer(String webService) {
        // change server step 3.
        if (webService.equalsIgnoreCase(WebServer.PL_NAME)) {
            this.name = this.PL_NAME;
            this.shortName = this.PL_SHORT_NAME;
            this.webService = this.PL_WEB_SERVICE;
            this.url = this.PL_URL;
            this.nameSpace = this.PL_NAMESPACE;
            this.restEndpoint = this.REST_ENDPOINT_PL;
        }
        else {
            this.name = this.PL_NAME_STAGE;
            this.shortName = this.PL_SHORT_NAME_STAGE;
            this.webService = this.PL_WEB_SERVICE_STAGE;
            this.url = this.PL_URL_STAGE;
            this.nameSpace = this.PL_NAMESPACE_STAGE;
            this.restEndpoint = this.REST_ENDPOINT_STAGE;
        }

        this.timeElapsed = 0;
        this.errorMessage = "";
        this.errorCode = "";

        this.searchCountOnly = true; // count for default;
        this.searchSortBy = ""; // default to empty
        this.query = "";
    }

    public WebServer(String name, String shortName, String webService, String nameSpace, String url) {
        this.name = name;
        this.shortName = shortName;
        this.webService = webService;
        this.nameSpace = nameSpace;
        this.url = url;
        this.timeElapsed = 0;
        this.errorMessage = "";
        this.errorCode = "";

        this.searchCountOnly = true; // count for default;
        this.searchSortBy = ""; // default to empty
        this.query = "";

        if (this.shortName.equalsIgnoreCase("PL") == true){
            this.restEndpoint = REST_ENDPOINT_PL;
        }
        else {
            this.restEndpoint = REST_ENDPOINT_STAGE;
        }
    }

    public String getTokenAnonymous() {
        return tokenAnonymous;
    }

    public void setTokenAnonymous(String tokenAnonymous) {
        this.tokenAnonymous = tokenAnonymous;
    }

    public int getTokenStatus() {
        return tokenStatus;
    }

    public void setTokenStatus(int tokenStatus) {
        this.tokenStatus = tokenStatus;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public double getTimeElapsed() {
        return this.timeElapsed;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }


    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return this.shortName;
    }

    public void setWebService(String webService) {
        this.webService = webService;
    }

    public String getWebService() {
        return this.webService;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getNameSpace() {
        return this.nameSpace;
    }

    public RequestAnonTokenResult restCallRequestAnonToken(final Context c){
        final RequestAnonTokenResult requestAnonTokenResult = new RequestAnonTokenResult();
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    try {
                        RestRequestAnonTokenResult result = restAnonToken(c);
                        requestAnonTokenResult.setErrorCode(result.getErrorCode());
                        requestAnonTokenResult.setErrorMessage(result.getErrorMessage());
                        if (requestAnonTokenResult.getErrorCode().equalsIgnoreCase("0")) {
                            requestAnonTokenResult.setTokenAnonymous(result.getTor());
                            tokenStatus = ReUnite.TOKEN_ANONYMOUS;
                        } else {
                            requestAnonTokenResult.setTokenAnonymous("");
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        requestAnonTokenResult.setErrorCode("-1");
                        requestAnonTokenResult.setErrorMessage(e.getMessage());
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
                restSearchResult.setErrorCode(Result.MY_ERROR_CODE);
                restSearchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                restSearchResult.setErrorCode(Result.MY_ERROR_CODE);
                restSearchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            } catch (TimeoutException e) {
                e.printStackTrace();
                restSearchResult.setErrorCode(Result.MY_ERROR_CODE);
                restSearchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread
        return requestAnonTokenResult;
    }

    public JSONObject buildAnonJson(Context c) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "anon");
        }
        catch (Exception ex){
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public RestRequestAnonTokenResult restAnonToken(Context c)throws Exception {
        RestRequestAnonTokenResult rest = new RestRequestAnonTokenResult(c);
        rest.toDefault();

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildAnonJson(c);
        if (jo == null) {
            rest.setErrorCode("-1");
            rest.setErrorMessage(c.getResources().getString(R.string.unknown));
            return rest;
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
        }
        in.close();

        JSONObject ResultJson = new JSONObject(jsonStr);
        rest = extractAnonResult(c, ResultJson);

        return rest;
    }

    private RestRequestAnonTokenResult extractAnonResult(Context c, JSONObject j) throws JSONException {
        RestRequestAnonTokenResult rest = new RestRequestAnonTokenResult(c);
        String errorCode = "", results = "", total = "";
        try {
            errorCode = j.get("error").toString();
            if (!errorCode.isEmpty()){
                rest.setErrorCode(errorCode);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            rest.setErrorCode("-1");
            rest.setErrorMessage(e.getMessage().toString());
            return rest;
        }

        try {
            results = j.get("token").toString();
            if (!results.isEmpty()){
                rest.setTor(results);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            rest.setErrorCode("-1");
            rest.setErrorMessage(e.getMessage().toString());
            return rest;
        }

        return rest;
    }

    public RestForgotUsernameResult restForgotUsername(final Context c, final String token, final String email) {
        restForgotUsernameResult = new RestForgotUsernameResult(c);

        if (email.isEmpty()) {
                restForgotUsernameResult.setErrorCode(RestResult.MY_ERROR_CODE);
                restForgotUsernameResult.setErrorMessage(c.getResources().getString(R.string.email_is_empty));
            return restForgotUsernameResult;
        }

        if (token.isEmpty()) {
            restForgotUsernameResult.setErrorCode(RestResult.MY_ERROR_CODE);
            restForgotUsernameResult.setErrorMessage(c.getResources().getString(R.string.no_valid_token));
            return restForgotUsernameResult;
        }

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    try {
                        restForgotUsernameResult = restForgot(c, token, email);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        restForgotUsernameResult.setErrorCode("-1");
                        restForgotUsernameResult.setErrorMessage(e.getMessage());
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
                restForgotUsernameResult.setErrorCode(RestResult.MY_ERROR_CODE);
                restForgotUsernameResult.setErrorMessage(e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                restForgotUsernameResult.setErrorCode(RestResult.MY_ERROR_CODE);
                restForgotUsernameResult.setErrorMessage(e.getMessage());
            } catch (TimeoutException e) {
                e.printStackTrace();
                restForgotUsernameResult.setErrorCode(RestResult.MY_ERROR_CODE);
                restForgotUsernameResult.setErrorMessage(e.getMessage());
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return restForgotUsernameResult;
    }

    public RestForgotUsernameResult restForgot(Context c, final String token, final String email)throws Exception {
        RestForgotUsernameResult restResult = new RestForgotUsernameResult(c);
        restResult.toDefault();

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildForgotJson(c, token, email);
        if (jo == null){
            restResult.setErrorCode("-1");
            restResult.setErrorMessage(c.getResources().getString(R.string.unknown));
            return restResult;
        }

        // Send post request
        con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(jo.toString());

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
            restResult.setErrorCode(jsonResult.get("error").toString());
            if (restResult.getErrorCode().equalsIgnoreCase("0")){
                return restResult;
            }
            else {
                restResult.searchErrorMessage();
            }
        }
        return restResult;
    }

    /**
     * rest call search
     * {"call":"forgot","token":"123","email":"bob@aol.com"}

     */
    public JSONObject buildForgotJson(Context c, final String token, final String email) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "forgot");
            json.put("token", token);
            json.put("email", email);
        }
        catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    // this function is added in api 34
    public RestSearchResult restCallSearchCountV34(final Context c, final Filters filters, final ViewSettings viewSettings, final String eventShort) {
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    try {
                        String result = restSearchCountV34(c, filters, viewSettings, eventShort);
                        JSONObject searchCountResultJson = new JSONObject(result);
                        restSearchResult = extractSearchResult(c, searchCountResultJson);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        restSearchResult.setErrorCode("-1");
                        restSearchResult.setErrorMessage(e.getMessage());
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
                restSearchResult.setErrorCode(Result.MY_ERROR_CODE);
                restSearchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                restSearchResult.setErrorCode(Result.MY_ERROR_CODE);
                restSearchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            } catch (TimeoutException e) {
                e.printStackTrace();
                restSearchResult.setErrorCode(Result.MY_ERROR_CODE);
                restSearchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return restSearchResult;
    }

    private RestSearchResult extractSearchResult(Context c, JSONObject j) throws JSONException {
        RestSearchResult rest = new RestSearchResult(c);
        String errorCode = "", results = "", total = "";
        try {
            errorCode = j.get("error").toString();
            if (!errorCode.equalsIgnoreCase("0")){
                rest.setErrorCode(errorCode);
                rest.searchErrorMessage();
                return rest;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            rest.setErrorCode("-1");
            rest.setErrorMessage(e.getMessage().toString());
            return rest;
        }

        try {
            results = j.get("results").toString();
            if (!results.isEmpty()){
                rest.setPatientList(extractPersonJSON(results));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            rest.setErrorCode("-1");
            rest.setErrorMessage(e.getMessage().toString());
            return rest;
        }

        try {
            total = j.get("total").toString();
            rest.setErrorCode(errorCode);
            rest.setCount(total);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            rest.setErrorCode("-1");
            rest.setErrorMessage(e.getMessage().toString());
            return rest;
        }
        return rest;
    }

    public List<Patient> extractPersonJSON(String results) throws JSONException {
        List<Patient> pList = new ArrayList<Patient>();

        JSONArray jsonArr = new JSONArray(results);
        for (int i = 0; i < jsonArr.length(); i++){
            Patient p = new Patient(getWebService());
            JSONObject o = jsonArr.getJSONObject(i);

            // what we get
            /*
            p.setPatientUuid(jo.getString("uuid"));
            String name = jo.getString("name");
            String stat = jo.getString("stat");
            String sex = jo.getString("sex");
            String age = jo.getString("age");
            String lki = jo.getString("lki");
            String latitude = jo.getString("latitude");
            String longitude = jo.getString("longitude");
            String editable = jo.getString("editable");
            String following = jo.getString("following");
            String animal = jo.getString("animal");
            String updated = jo.getString("updated");
            String created = jo.getString("created");
            String expires = jo.getString("expires");
            String image_url = jo.getString("image_url");
            String thumb_url = jo.getString("thumb_url");
            String comments = jo.getString("comments"); */

            // what we used to have
            p.setPatientUuid(o.getString("uuid"));
            String fullN = o.getString("name");
            if (fullN.equalsIgnoreCase("null") == true){
                fullN = "unk";
            }
            else if (fullN.equalsIgnoreCase("-1")){
                fullN = "unk";
            }
            p.setFullName(fullN);
            p.setOptStatus(o.getString("stat"));

            /**
             * animal. version 7.3.6
             */
            String animal = o.getString("animal");
            if (animal.equalsIgnoreCase("null") == true){
                p.setPa(Patient.PERSON);
            }
            else {
                p.setPa(Patient.ANIMAL);
                JSONObject oAnimal = new JSONObject(animal);
                String buddy = oAnimal.getString("buddy");
                if (buddy.equalsIgnoreCase("null")){
                    p.setBuddy("");
                }
                else if (buddy.isEmpty() == true) {
                    p.setBuddy("");
                }
                else if (buddy.equalsIgnoreCase("unk") == true) {
                    p.setBuddy("");
                }
                else {
                    p.setBuddy(buddy);
                }
            }

            p.setGender(o.getString("sex"));
            p.setYearsOld(o.getString("age"));

            p.setLastSeen(o.getString("lki")); // is last_seen changed to lki?
            // String lki = o.getString("lki");

//            String latitude = o.getString("latitude");// not used temporary.
//            String longitude = o.getString("longitude");// not used temporary.
//            String editable = o.getString("editable"); // not used temporary.
//            String following = o.getString("following");// not used temporary.
//            String animal = o.getString("animal"); // not used temporary.
            p.setStatusSahanaUpdated(o.getString("updated"));
//            String updated = jo.getString("updated");
//            String created = jo.getString("created");
//            String expires = jo.getString("expires");
//            String image_url = jo.getString("image_url");
            p.setImageUrl(o.getString("thumb_url"));
//            String thumb_url = jo.getString("thumb_url");
            /*
            String com = o.getString("comments");
            if (com.equalsIgnoreCase("null") == true){
                com = "";
            }
            p.setComments(com);
            */

            String comStr = o.getString("comments");
            if (comStr.equalsIgnoreCase("null") == true){
                comStr = "";
                p.setComments(comStr);
            }
            else if (comStr.isEmpty() == true){
                p.setComments(comStr);
            }
            else {
                p.setComments(extractCommentJSON(comStr));
            }


//            String comments = jo.getString("comments");
//				p.setImageUrlFull(o.getString("imageUrlFull"));
//            p.setImageWidth(o.getString("imageWidth"));
//            p.setImageHeight(o.getString("imageHeight"));

//            p.setId(o.getString("id"));
//            p.setStatusTriage(o.getString("statusTriage"));
//           p.setPeds(o.getString("peds"));
//            p.setOrgName(o.getString("orgName"));

//            p.setHospitalIcon(o.getString("hospitalIcon"));
//            p.setMassCasualtyId(o.getString("mass_casualty_id"));

            // Generate photo
            p.setStatusPhotoDownload(false);
            pList.add(p);
        }
        return pList;
    }

    public String extractBuddyJSON(String buddyStr) throws JSONException {
        List<String> list = new ArrayList<String>();
        String str = "";

        JSONArray jsonArr = new JSONArray(buddyStr);
        int len = jsonArr.length();
        for (int i = 0; i < jsonArr.length(); i++){
            JSONObject o = jsonArr.getJSONObject(i);
            str = o.getString("buddy");
        }
        return str;
    }

    public String extractCommentJSON(String comStr) throws JSONException {
        List<String> list = new ArrayList<String>();
        String str = "";

        JSONArray jsonArr = new JSONArray(comStr);
        int len = jsonArr.length();
        for (int i = 0; i < jsonArr.length(); i++){
            JSONObject o = jsonArr.getJSONObject(i);
            String comment = o.getString("comment");
            String when = o.getString("when");
            list.add(comment + "\n" + when);
        }

        for (int i = 0; i < list.size(); i++){
            str += list.get(i) + "\n";
        }
        return str;
    }

    // HTTP POST request

    /**
     * searchRestCall()
     * to searchCountV34()
     * change SOAP call to REST call
     * changes made in version 9.3.0
     * @param c
     * @param filters
     * @param viewSettings
     * @param eventShort
     * @return
     * @throws Exception
     */
    public String restSearchCountV34(Context c, final Filters filters, final ViewSettings viewSettings, final String eventShort) throws Exception {
        String returnStr = "";

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildSearchJson(filters, viewSettings, eventShort);
        if (jo == null){
            return returnStr = c.getResources().getString(R.string.error) + ": " + "buildJson()";
        }

        // Send post request
        con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(jo.toString());

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
        returnStr = jsonStr;
        return  returnStr;
    }

    /**
     * rest call search
     */
    public JSONObject buildSearchJson(final Filters filters, final ViewSettings viewSettings, final String eventShort) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "search");

            String tor;
            if (getTokenStatus() == ReUnite.TOKEN_AUTH) {
                json.put("token", getToken());
            }
            else {
                json.put("token", getTokenAnonymous());
            }

            json.put("short", eventShort);
            json.put("query", this.query.toString());
            json.put("photo", "");

            // animal
            if (filters.isSelSearchPerson() == true){
                json.put("personAnimal", Filters.SEARCH_PERSON);
            }
            else if (filters.isSelSearchAnimal() == true){
                json.put("personAnimal", Filters.SEARCH_ANIMATION);
            }
            else {
                json.put("personAnimal", Filters.SEARCH_BOTH);
            }

            json.put("sexMale", filters.getMale());
            json.put("sexFemale", filters.getFemale());
            json.put("sexOther", filters.getComplex());
            json.put("sexUnknown", filters.getGenderUnknown());
            json.put("ageChild", filters.getChild());
            json.put("ageAdult", filters.getAdult());
            json.put("ageUnknown", filters.getAgeUnknown());
            json.put("statusMissing", filters.getMissing());
            json.put("statusAlive", filters.getAliveAndWell());
            json.put("statusInjured", filters.getInjured());
            json.put("statusDeceased", filters.getDeceased());
            json.put("statusUnknown", filters.getStatusUnknown());
            json.put("statusFound", filters.getFound());

            if (viewSettings.getPhotoSel() == ViewSettings.PHOTO_ONLY){
                json.put("hasImage", true);
            }
            else {
                json.put("hasImage", false);
            }

            json.put("since", "1970-01-01T01:23:45Z");
            json.put("pageStart", this.curPageStart);
            json.put("perPage", viewSettings.getPageSize());
            json.put("sortBy", this.searchSortBy);
        }
        catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    // this function is added in api 34
    public ReportResult sendGcmToken(final String gcmToken, final Context c){
        final String TAG = "sendGcmToken";

        ReportResult reportResult = new ReportResult();
        reportResult.toDefault();

        if (getToken().isEmpty() && getTokenAnonymous().isEmpty()) {
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(c.getResources().getString(R.string.no_valid_token));
            Log.e(TAG, "token is empty.");
            return reportResult;
        }

        if (this.nameSpace.isEmpty()) {
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(c.getResources().getString(R.string.error_in_status));
            Log.e(TAG, "nameSpace is not defined.");
            return reportResult;
        }

        if (this.url.isEmpty()) {
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + "URL");
            Log.e(TAG, "url is not defined.");
            return reportResult;
        }

        if (BuildConfig.VERSION_NAME.isEmpty()) {
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + c.getResources().getString(R.string.version_history));
            Log.e(TAG, "Version number is not found.");
            return reportResult;
        }

        String METHOD_NAME = "registerGCM";
        String SOAP_ACTION = this.nameSpace + WebServer.END_POINT + METHOD_NAME;
        SoapObject request = new SoapObject(this.nameSpace, METHOD_NAME);

        if (getTokenStatus() == ReUnite.TOKEN_AUTH) {
            request.addProperty("token", getToken());
        }
        else {
            request.addProperty("token", getTokenAnonymous());
        }

        request.addProperty("registrationID", gcmToken.toString());
        String strDis = Build.MANUFACTURER + "," + Build.MODEL + "," + Build.USER + " " + Build.VERSION.RELEASE + "," + c.getResources().getString(R.string.app_name) + " " + BuildConfig.VERSION_NAME.toString() + ",(" + Locale.getDefault().toString() + ")(" + TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT) + ")";
        request.addProperty("versionString", strDis);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        HttpTransportSE ht = new HttpTransportSE(this.url);
        ht.setXmlVersionTag("<?xml version = \"1.0\" encoding = \"utf-8\"?>");
//        SoapPrimitive result = null;  // Only SoapPrimitive can get the data back. SoapObject doesn't.
        try {
            ht.debug = true;
            ht.call(SOAP_ACTION, envelope);
            Log.i(TAG, ht.requestDump);
            Log.i(TAG, ht.responseDump);
//                    result = (SoapPrimitive)envelope.getResponse();
        } catch (Exception e2) {
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e2.getMessage().toString());
        }

        final Vector response;
        String errorMessage = "";
        String content = "";
        String count = "";
        reportResult.toDefault();
        try {
            response = (Vector) envelope.getResponse();
            reportResult.setErrorCode(response.get(0).toString());
            reportResult.setErrorMessage(response.get(1).toString());
        } catch (SoapFault soapFault) {
            soapFault.printStackTrace();
            Log.e(TAG, soapFault.getMessage());
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + soapFault.getMessage().toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            reportResult.setErrorCode(Result.MY_ERROR_CODE);
            reportResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage().toString());
        }
        return reportResult;
    }

    public RestGcmResult restSendGcmToken( final Context c, final String gcmToken, final boolean sub)throws Exception {
        RestGcmResult rest = new RestGcmResult(c);

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildGcmTokenJson(c, gcmToken, sub);
        if (jo == null){
            rest.setErrorCode("-1");
            rest.setErrorMessage(c.getResources().getString(R.string.unknown));
            return rest;
        }

        // Send post request
        con.setDoOutput(true);
//        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//        wr.writeBytes(jo.toString());

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

        JSONObject j = new JSONObject(jsonStr);
        rest.setErrorCode(j.getString("error"));
        if (!rest.getErrorCode().equalsIgnoreCase("0")){
            rest.searchErrorMessage();
        }
        return rest;
    }

    public JSONObject buildGcmTokenJson(final Context c, final String gcmToken, final boolean sub) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "push");
            if (getTokenStatus() == ReUnite.TOKEN_AUTH) {
                json.put("token", getToken());
            }
            else {
                json.put("token", getTokenAnonymous());
            }
            json.put("rid", gcmToken);
            json.put("about", "ReUnite Android");
            json.put("sub", sub);
        }
        catch (Exception ex){
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public RestGcmResult restCallSendGcmToken(final Context c, final String gcmToken, final boolean sub){
        restGcmResult = new RestGcmResult(c);

        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    try {
                        restGcmResult = restSendGcmToken(c, gcmToken, sub);
                    } catch (Exception e) {
                        e.printStackTrace();
                        restGcmResult.setErrorCode("-1");
                        restGcmResult.setErrorMessage(e.getMessage());
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
                searchResult.setErrorCode(Result.MY_ERROR_CODE);
                searchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            } catch (ExecutionException e) {
                e.printStackTrace();
                searchResult.setErrorCode(Result.MY_ERROR_CODE);
                searchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            } catch (TimeoutException e) {
                e.printStackTrace();
                searchResult.setErrorCode(Result.MY_ERROR_CODE);
                searchResult.setErrorMessage(c.getResources().getString(R.string.error) + ": " + e.getMessage());
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        // End of the thread

        return restGcmResult;
    }

    public JSONObject buildFollowJson(Context c, final String token, final String uuid, final boolean sub) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "follow");
            json.put("token", token);
            json.put("uuid", uuid);
            json.put("sub", sub);
        }
        catch (Exception ex){
//            Toast.makeText(c, "Exception error in JSON: " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public RestFollowResult restFollow(Context c, final String token, final String uuid, final boolean sub)throws Exception {
        restFollowResult = new RestFollowResult(c);
        restFollowResult.toDefault();

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildFollowJson(c, token, uuid, sub);
        if (jo == null){
            restFollowResult.setErrorCode("-1");
            restFollowResult.setErrorMessage(c.getResources().getString(R.string.unknown));
            return restFollowResult;
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
            restFollowResult.setErrorCode(jsonResult.get("error").toString());
            if (restFollowResult.getErrorCode().equalsIgnoreCase("0")){
                return restFollowResult;
            }
            else {
                restFollowResult.searchErrorMessage();
            }
        }
        return restFollowResult;
    }


    public RestFollowResult restCallFollowRecord(final Context c, final String token, final String uuid, final boolean sub) {
        restFollowResult = new RestFollowResult(c);
        restFollowResult.toDefault();

        try {
            restFollowResult = restFollow(c, token, uuid, sub);
        }
        catch (Exception e) {
            e.printStackTrace();
            restFollowResult.setErrorCode("-1");
            restFollowResult.setErrorMessage(e.getMessage());
        }

        return restFollowResult;
    }

    public RestFollowRecordResult restCallFollowList(final Context c, final String token, final String locale) throws Exception {
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    try {
                        restFollowRecordResult = restFollowList(c, token, locale);
                    } catch (Exception e) {
                        e.printStackTrace();
                        restFollowRecordResult.setErrorCode("-1");
                        restFollowRecordResult.setErrorMessage(e.getMessage());
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
                restFollowRecordResult.setErrorCode("-1");
                restFollowRecordResult.setErrorMessage(e.getMessage());
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }

        return restFollowRecordResult;
    }

    public JSONObject buildFollowingJson(Context c, final String token, final String locale) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "following");
            json.put("token", token);
            json.put("locale", locale);
        }
        catch (Exception ex){
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public RestFollowRecordResult restFollowList(Context c, final String token, final String locale)throws Exception {
        restFollowRecordResult = new RestFollowRecordResult(c);
        restFollowRecordResult.toDefault();

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildFollowingJson(c, token, locale);
        if (jo == null) {
            restFollowRecordResult.setErrorCode("-1");
            restFollowRecordResult.setErrorMessage(c.getResources().getString(R.string.unknown));
            return restFollowRecordResult;
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
        }
        in.close();

        restFollowRecordResult.setErrorCode("0");
        restFollowRecordResult.setRecords(jsonStr);
        restFollowRecordResult.setErrorMessage("");
        return restFollowRecordResult;
    }

    public RestCommentResult restCallComment(final Context c, final String token, final String uuid, final String text) throws Exception {
        //limit the number of actual threads
        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    try {
                        restCommentResult = restComment(c, token, uuid, text);
                    } catch (Exception e) {
                        e.printStackTrace();
                        restCommentResult.setErrorCode("-1");
                        restCommentResult.setErrorMessage(e.getMessage());
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
                restCommentResult.setErrorCode("-1");
                restCommentResult.setErrorMessage(e.getMessage());
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }
        return restCommentResult;
    }

    public RestCommentResult restComment(Context c, final String token, final String uuid, final String text)throws Exception {
        RestCommentResult result = new RestCommentResult(c);
        result.toDefault();

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildCommentJson(c, token, uuid, text);
        if (jo == null) {
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

    public JSONObject buildCommentJson(Context c, final String token, final String uuid, final String text) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "comment");
            json.put("token", token);
            json.put("uuid", uuid);
            json.put("text", text);
            /*
            json.put("stat", locale);
            json.put("latitude", locale);
            json.put("longitude", locale);
            json.put("photo", locale);
            */
        }
        catch (Exception ex){
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public RestEventsResult restSearchEvents(Context c)throws Exception{
        RestEventsResult rest = new RestEventsResult(c);
        rest.toDefault();

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildSearchEventsJson(c);
        if (jo == null) {
            rest.setErrorCode("-1");
            rest.setErrorMessage(c.getResources().getString(R.string.unknown));
            return rest;
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
        }
        in.close();

        if (jsonStr.isEmpty()){
            rest.setErrorCode("-1");
            rest.setErrorMessage(c.getResources().getString(R.string.unknown));
            return rest;
        }

        rest = extractEventsResult(c, jsonStr);
        return rest;
    }

    private RestEventsResult extractEventsResult(Context c, String results) throws JSONException {
        RestEventsResult rest = new RestEventsResult(c);
        rest.toDefault();

        List<Event2> list = new ArrayList<Event2>();
        JSONArray jsonArr = new JSONArray(results);
        for (int i = 0; i < jsonArr.length(); i++){
            Event2 e = new Event2(getWebService());
            JSONObject o = jsonArr.getJSONObject(i);
            e.setArchived2(o.getBoolean("archived"));
            String articlesJson = o.getString("articles");
            if (!articlesJson.isEmpty()){
                e.setArticles2(extractArticle(c, articlesJson));
            }
            String captionsJson = o.getString("captions");
            if (!captionsJson.isEmpty()){
                e.setCaptions2(extractCaption(c, captionsJson));
            }
            e.setClosed2(o.getBoolean("closed"));
            e.setDate2(o.getString("date"));
            e.setDefault2(o.getBoolean("default"));
            e.setExpired2(o.getInt("expired"));
            e.setGroup2(o.getInt("group"));
            e.setId2(o.getInt("id"));
            e.setImage2(o.getString("image"));
            e.setLatitude2(o.getDouble("latitude"));
            e.setLongitude2(o.getDouble("longitude"));
            String namesJson = o.getString("names");
            if (!namesJson.isEmpty()) {
                e.setNames2(extractName(c, namesJson));
            }
            e.setOriginate2(o.getInt("originated"));
            e.setShort2(o.getString("short"));
            String tagsJson = o.getString("tags");
            if (!tagsJson.isEmpty()){
                e.setTags2(extractTag(c, tagsJson));
            }
            e.setType2(o.getString("type"));
            e.setUnexpired2(o.getInt("unexpired"));
            e.setUnlisted2(o.getBoolean("unlisted"));
            e.setUpdated2(o.getString("updated"));
            list.add(e);
        }
        rest.setEventList2(list);
        return rest;
    }

    private String[] extractTag(Context c, String tagsJson)  throws JSONException{
        String [] tags = {"","","","",""};
        JSONObject o = new JSONObject(tagsJson);
        try {
            tags[0] = o.getString("en");
        }catch (Exception e){
            tags[0] = "";
        }
        try {
            tags[1] = o.getString("es");
        }catch (Exception e){
            tags[1] = "";
        }
        try {
            tags[2] = o.getString("zh_CN");
        }catch (Exception e){
            tags[2] = "";
        }
        try {
            tags[3] = o.getString("zh_TW");
        }catch (Exception e){
            tags[3] = "";
        }
        try {
            tags[4] = o.getString("vi");
        }catch (Exception e){
            tags[4] = "";
        }
        return tags;
    }

    private String[] extractName(Context c, String namesJson) throws JSONException{
        String [] names = {"","","","",""};
        JSONObject o = new JSONObject(namesJson);
        try {
            names[0] = o.getString("en");
        }catch (Exception e){
            names[0] = "";
        }
        try {
            names[1] = o.getString("es");
        }catch (Exception e){
            names[1] = "";
        }
        try {
            names[2] = o.getString("zh_CN");
        }catch (Exception e){
            names[2] = "";
        }
        try {
            names[3] = o.getString("zh_TW");
        }catch (Exception e){
            names[3] = "";
        }
        try {
            names[4] = o.getString("vi");
        }catch (Exception e){
            names[4] = "";
        }
        return names;
    }

    private String[] extractArticle(Context c, String articleJson) throws JSONException {
        String [] articles = {"","","","",""};
        JSONObject o = new JSONObject(articleJson);
        try {
            articles[0] = o.getString("en");
        }catch (Exception e){
            articles[0] = "";
        }
        try {
            articles[1] = o.getString("es");
        }catch (Exception e){
            articles[1] = "";
        }
        try {
            articles[2] = o.getString("zh_CN");
        }catch (Exception e){
            articles[2] = "";
        }
        try {
            articles[3] = o.getString("zh_TW");
        }catch (Exception e){
            articles[3] = "";
        }
        try {
            articles[4] = o.getString("vi");
        }catch (Exception e){
            articles[4] = "";
        }
        return articles;
    }

    private String[] extractCaption(Context c, String captionJson) throws JSONException {
        String [] captions = {"","","","",""};
        JSONObject o = new JSONObject(captionJson);
        try {
            captions[0] = o.getString("en");
        }catch (Exception e){
            captions[0] = "";
        }
        try {
            captions[1] = o.getString("es");
        }catch (Exception e){
            captions[1] = "";
        }
        try {
            captions[2] = o.getString("zh_CN");
        }catch (Exception e){
            captions[2] = "";
        }
        try {
            captions[3] = o.getString("zh_TW");
        }catch (Exception e){
            captions[3] = "";
        }
        try {
            captions[4] = o.getString("vi");
        }catch (Exception e){
            captions[4] = "";
        }
        return captions;
    }

    // {"call":"events","token":"123"}
    private JSONObject buildSearchEventsJson(Context c) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "events");
            if (getTokenStatus() == ReUnite.TOKEN_AUTH) {
                json.put("token", getToken());
            }
            else {
                json.put("token", getTokenAnonymous());
            }
        }
        catch (Exception ex){
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }

    public static boolean isOnline(Context c){
        ConnectivityManager connMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        else {
            return false;
        }
    }

    // message
    public RestMessageResult restCallMessage(Context c, final String token, final boolean getOrSet, final RestMessageResult result){
        restMessageResult = result;

        int poolSize = 1;
        ExecutorService service = Executors.newFixedThreadPool(poolSize);
        List<Future<Runnable>> futures = new ArrayList<Future<Runnable>>();

        for (int n = 0; n < poolSize; n++) {
            Future f = service.submit(new Runnable() {
                public void run() {
                    try {
                        restMessageResult = restMessage(c, token, getOrSet, result.isEventEmail(), result.isEventPush(), result.isRecordEmail(), result.isRecordPush(), result.isAdminEmail(), result.isAdminPush());
                        if (restMessageResult.getErrorCode().contains("0") == true){
//                            JSONObject json = new JSONObject(restMessageResult.getErrorMessage());
                            restMessageResult = extractMessage(c, restMessageResult.getErrorMessage());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        restMessageResult.setErrorCode("-1");
                        restMessageResult.setErrorMessage(e.getMessage());
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
                restMessageResult.setErrorCode("-1");
                restMessageResult.setErrorMessage(e.getMessage());
            }
            //shut down the executor service so that this thread can exit
            service.shutdownNow();
        }

        return restMessageResult;
    }

    private RestMessageResult extractMessage(Context c, String json) throws JSONException{
        JSONObject o = new JSONObject(json);

        try {
            if (o.getString("event_email").contains("true") == true){
                restMessageResult.setEventEmail(true);
            }
            else {
                restMessageResult.setEventEmail(false);
            }
        }catch (Exception e){
            restMessageResult.setErrorCode("-1");
            restMessageResult.setErrorMessage(e.getMessage());
        }

        try {
            if (o.getString("event_push").contains("true") == true){
                restMessageResult.setEventPush(true);
            }
            else {
                restMessageResult.setEventPush(false);
            }
        }catch (Exception e){
            restMessageResult.setErrorCode("-1");
            restMessageResult.setErrorMessage(e.getMessage());
        }

        try {
            if (o.getString("event_push").contains("true") == true){
                restMessageResult.setEventPush(true);
            }
            else {
                restMessageResult.setEventPush(false);
            }
        }catch (Exception e){
            restMessageResult.setErrorCode("-1");
            restMessageResult.setErrorMessage(e.getMessage());
        }

        try {
            if (o.getString("record_email").contains("true") == true){
                restMessageResult.setRecordEmail(true);
            }
            else {
                restMessageResult.setRecordEmail(false);
            }
        }catch (Exception e){
            restMessageResult.setErrorCode("-1");
            restMessageResult.setErrorMessage(e.getMessage());
        }

        try {
            if (o.getString("record_push").contains("true") == true){
                restMessageResult.setRecordPush(true);
            }
            else {
                restMessageResult.setRecordPush(false);
            }
        }catch (Exception e){
            restMessageResult.setErrorCode("-1");
            restMessageResult.setErrorMessage(e.getMessage());
        }

        try {
            if (o.getString("admin_email").contains("true") == true){
                restMessageResult.setAdminEmail(true);
            }
            else {
                restMessageResult.setAdminEmail(false);
            }
        }catch (Exception e){
            restMessageResult.setErrorCode("-1");
            restMessageResult.setErrorMessage(e.getMessage());
        }

        try {
            if (o.getString("admin_push").contains("true") == true){
                restMessageResult.setAdminPush(true);
            }
            else {
                restMessageResult.setAdminPush(false);
            }
        }catch (Exception e){
            restMessageResult.setErrorCode("-1");
            restMessageResult.setErrorMessage(e.getMessage());
        }

        return restMessageResult;
    }

    public RestMessageResult restMessage(
            Context c,
            final String token,
            final boolean getOrSet,
            final boolean eventEmail,
            final boolean eventPush,
            final boolean recordEmail,
            final boolean recordPush,
            final boolean adminEmail,
            final boolean adminPush)throws Exception {
        restMessageResult = new RestMessageResult(c);
        restMessageResult.toDefault();

        URL obj = new URL(getRestEndpoint());
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");

        JSONObject jo = buildMessageJson(c, token, getOrSet, eventEmail, eventPush, recordEmail, recordPush, adminEmail, adminPush);
        if (jo == null){
            restMessageResult.setErrorCode("-1");
            restMessageResult.setErrorMessage(c.getResources().getString(R.string.unknown));
            return restMessageResult;
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
            restMessageResult.setErrorCode(jsonResult.get("error").toString());
            if (restMessageResult.getErrorCode().equalsIgnoreCase("0")){
                restMessageResult.setErrorMessage(jsonStr);
                return restMessageResult;
            }
            else {
                restMessageResult.searchErrorMessage();
            }
        }
        return restMessageResult;
    }

    public JSONObject buildMessageJson(
            Context c,
            final String token,
            final boolean getOrSet,
            final boolean eventEmail,
            final boolean eventPush,
            final boolean recordEmail,
            final boolean recordPush,
            final boolean adminEmail,
            final boolean adminPush) {
        JSONObject json = new JSONObject();
        try {
            json.put("call", "messages");
            json.put("token", token);
            if (getOrSet == true){
                /*
                json.put("event_email", eventEmail);
                json.put("event_push", eventPush);
                json.put("record_email", recordEmail);
                json.put("record_push", recordPush);
                json.put("admin_email", adminEmail);
                json.put("admin_push", adminPush);
                */
            }
            else {
                json.put("event_email", eventEmail);
                json.put("event_push", eventPush);
                json.put("record_email", recordEmail);
                json.put("record_push", recordPush);
                json.put("admin_email", adminEmail);
                json.put("admin_push", adminPush);
            }
        }
        catch (Exception ex){
            Toast.makeText(c, c.getResources().getString(R.string.error) + ": " + ex.getMessage().toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return json;
    }
}