package com.devapp.devmain.server;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.devapp.devmain.Connectivity.NetworkTest;
import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class ServerAPI {

    private static final String TAG = "ServerAPI";

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

    // AUTHENTICATE USER
    public static boolean authenticateUser(Context context, String userId, String password,
                                           int updateCheck, String server) {


        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        AuthenticationParameters authParams = new AuthenticationParameters(null, amcuConfig.getDeviceID(), amcuConfig.getDevicePassword());

        CommunicationService cs = null;

        Log.d(TAG, "-------------- Trying to authenticate -----------");
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(), authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }

        HttpResponse response = null;
        try {
            response = cs.login();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IncompatibleProtocolException e) {
            e.printStackTrace();
        }

        String responseBody = null;
        if (response != null) {
            responseBody = response.getResponseBody();
        }

        /*
        String urL = server + "/amcu/j_spring_security_check?j_username="
                + userId + "&j_password=" + password;

        Log.v("Authorization URL", urL);
        JSONParser jParser = new JSONParser();
        String response = jParser.parseForLogIn(urL);
        Log.v("LogInresponse", "Response " + response);

        */


        return setLogInResponse(responseBody == null ? "" : responseBody);

    }

    public static void notifyLoggedIn(HttpResponse response) {

        String responseBody = null;
        if (response != null) {
            responseBody = response.getResponseBody();
            setLogInResponse(responseBody == null ? "" : responseBody);
        }
    }

    // Logout user
    public static boolean LogoutUser(String server) {
        String urL = server + "/amcu/logout";
        Log.v("Logout URL", urL);
        JSONParser jParser = new JSONParser();
        JSONObject json = jParser.getJSONFromUrl(urL);
        return true;
    }

    public static boolean setLogInResponse(String response) {

        System.out.println("response: " + response);
        if (response.equalsIgnoreCase("Login failed")) {
            return false;
        } else if (response.contains("pushStatus")) {
            getJsonupload(response);
            return true;
        } else if (response.contains("[")) {
            return true;
        } else {
            return false;
        }
    }

    public static void getJsonupload(String jsonData) {
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(jsonData);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        if (jsonArray != null) {
            try {
                setAppropriateDPN(jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setAppropriateDPN(JSONArray jsonArray) throws JSONException {
        JSONObject jobj;
        for (int i = 0; i < jsonArray.length(); i++) {
            jobj = jsonArray.getJSONObject(i);
            jobj = jobj.getJSONObject("payload");
            String updateType = jobj.getString("updateType");
            if (updateType.equalsIgnoreCase("RATECHART_ADD") || updateType.equalsIgnoreCase("RATECHART_UPDATE")) {
                Util.RATECHART_URI = "/amcu" + jobj.getString("uri");
            } else if (updateType.contains("CONFIG")) {
                Util.CONFIGURATION_URI = "/amcu" + jobj.getString("uri");
            } else if (updateType.contains("FARMER")) {
                Util.FARMER_URI = "/amcu" + jobj.getString("uri");
            } else if (updateType.contains("COLLECTION_CENTER")) {
                Util.CHILLING_CENTER_URI = "/amcu" + jobj.getString("uri");
            } else if (updateType.contains("APK")) {
                try {
                    Util.APK_URI = "/amcu" + jobj.getString("uri");
                    String version = jobj.getString("version");
                    Util.APK_DOWNLODED_VERSION = Integer.parseInt(version);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (updateType.contains("AGENT")) {
                SmartCCConstants.AGENT_UPDATE_URI = "/amcu" + jobj.getString("uri");
            } else if (updateType.contains("TRUCK")) {
                SmartCCConstants.TRUCK_UPDATE_URI = "/amcu" + jobj.getString("uri");
            } else if (updateType.equalsIgnoreCase("INCENTIVE_RATECHART_ADD") || updateType.equalsIgnoreCase("INCENTIVE_RATECHART_UPDATE")) {
                Util.INCENTIVE_RATECHART_URI = "/amcu" + jobj.getString("uri");
            }

        }

    }


    // AUTHENTICATE USER
    public static String userConnectivity(Context context) {


        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        AuthenticationParameters authParams = new AuthenticationParameters(null, amcuConfig.getDeviceID(), amcuConfig.getDevicePassword());

        CommunicationService cs = null;

        Log.d(TAG, "-------------- Trying to authenticate -----------");
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(), authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }

        HttpResponse response = null;
        try {
            response = cs.login();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IncompatibleProtocolException e) {
            e.printStackTrace();
        }

        int responseCode = 0;
        if (response != null) {
            responseCode = response.getResponseCode();
        }
        NetworkTest networkTest = new NetworkTest(context);
        return networkTest.getTextFromResponseCode(responseCode);
    }

}
