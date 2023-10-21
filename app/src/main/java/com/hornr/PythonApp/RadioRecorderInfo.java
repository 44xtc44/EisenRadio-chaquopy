// Get app content for notification.
// Use localhost network for Flask server requests, without ForeGroundService ANR would kill us.
package com.hornr.PythonApp;

import android.util.Log;

import org.json.JSONObject;
import org.json.JSONException;

import java.net.URL;
import java.io.Reader;
import java.util.Iterator;
import java.io.IOException;
import java.io.BufferedReader;
import java.net.URLConnection;
import java.io.InputStreamReader;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;

public class RadioRecorderInfo {
    private static final String LOG_TAG = "[runEisen...AppInfo]";
    public String AppServerInfoGet() throws ExecutionException, InterruptedException, IOException {
        /*
        @methods UrlResponseStringJSONGet()
        @methods RefurbishJSON

        Needs Android Network access config
        ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        
        In Android Manifest, in tag <application add:
        
        android:usesCleartextTraffic="true"
        android:networkSecurityConfig="@xml/network_security_config"
        <uses-library android:name="org.apache.http.legacy" android:required="false"/>

        Create a resource folder type xml and add a file *network_security_config.xml* and write this:
        
        <?xml version="1.0" encoding="utf-8"?>
          <network-security-config>
            <base-config cleartextTrafficPermitted="true" />
          </network-security-config>
        For Certificate Authorities (CA) see https://developer.android.com/privacy-and-security/security-config

         */
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Callable is async and returns a value. Runnable runs default a thread without a return value.
        Callable<String> stringCallable = () -> {

            String urlDisplay = "http://localhost:5050/station_get";
            String urlStreams = "http://localhost:5050/streamer_get";
            String jsonTitle, jsonRecorderOn, retMsg;

            try {
                jsonTitle = "listen to: " +  UrlResponseStringJSONGet(urlDisplay);
                jsonRecorderOn = "\tRecorderOnline: " +  UrlResponseStringJSONGet(urlStreams);
                retMsg = jsonTitle + jsonRecorderOn;
            }catch (IOException e) {
                Log.i(LOG_TAG, e.toString());
                retMsg = "Await notification input ...";
            }
            return retMsg;
        };

        Future<String> callableFuture = executorService.submit(stringCallable);  // store return value
        executorService.shutdown();
        return callableFuture.get();
    }
    public String UrlResponseStringJSONGet(String url) throws IOException, JSONException {
        // {updateDisplay: {"1":"Mazurkas Op 17 No 4 in A Minor","3":"Sam Spade-501201 003 The Dog Bed Caper"}}
        URL request = new URL(url);
        URLConnection response = request.openConnection();

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (response.getInputStream(), StandardCharsets.UTF_8))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        String prettyJSON;
        JSONObject jsonJava;
        try {
            jsonJava = new JSONObject(String.valueOf(textBuilder));
        } catch (JSONException e) {
            Log.d(LOG_TAG, e.toString());
            return e.toString();
        }
        prettyJSON = RefurbishJSON(jsonJava);
        return prettyJSON;
    }
    public String RefurbishJSON(JSONObject jsonObject) throws JSONException {
        /*
         JSON JScript AJAX ......... {"data": {"payload1": val, "payload2": val}} unwrap "Iterator<String>"  
         JSON Java often uses a list { Key: [{"payload1": val, "payload2": val}]} unwrap "JSONArray"
         AJAX async fun calls read the header Key to confirm that the JSON response belongs to the own call
         */
        Log.i(LOG_TAG, jsonObject.toString());
        String payloadKeys = "";
        StringBuilder prettyJSON = new StringBuilder();
        JSONObject jsonDictionaries = new JSONObject(jsonObject.toString());

        Iterator<String> wrapDictKey = jsonDictionaries.keys();
        try {
            if (wrapDictKey.hasNext()) {
                payloadKeys = jsonObject.getString(wrapDictKey.next());
            }
            JSONObject payload = new JSONObject(payloadKeys);
            Iterator<String> keys = payload.keys();

            while (keys.hasNext()) {
                String key = keys.next();
                String val = payload.getString(key);
                prettyJSON.append(key).append(".").append(val).append(" :: ");
            }
        }catch (JSONException e) {
            Log.d(LOG_TAG, e.toString());
            }
        return prettyJSON.toString();
    }
}
