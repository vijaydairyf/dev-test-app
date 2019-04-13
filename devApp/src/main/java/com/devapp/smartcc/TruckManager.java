package com.devapp.smartcc;

import android.content.Context;
import android.util.Log;

import com.devapp.devmain.entity.UpdateRatechartEntity;
import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.LogInService;
import com.devapp.devmain.server.ServerResponse;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.database.TruckCCDatabase;
import com.devapp.smartcc.entities.TrucKInfo;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class TruckManager {

    private static final String TAG = "TRUCK_MANAGER";
    static JSONParser jParser;
    public String responseBody;
    JSONArray jsonArray;
    SessionManager session;
    AmcuConfig amcuConfig;
    String url;
    String server;
    ServerResponse serverResponse;
    private Context mContext;

    public TruckManager(Context context) {
        mContext = context;
        session = new SessionManager(mContext);
        jParser = new JSONParser();
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();

    }

    public void parseUrlToTruckInfo() {
        if (SmartCCConstants.TRUCK_UPDATE_URI != null) {
            amcuConfig.setTruckUpdateUrl(SmartCCConstants.TRUCK_UPDATE_URI);
            String test = amcuConfig.getTruckUpdateUrl();
            SmartCCConstants.TRUCK_UPDATE_URI = null;
        }
        if (amcuConfig.getTruckUpdateUrl() != null) {
            getTruckData();
        }
    }

    public void afterGettingJSONArray() {

        ArrayList<TrucKInfo> allTruckDetailsEnt = null;
        try {
            jsonArray = new JSONArray(responseBody);
            allTruckDetailsEnt = getTruckDetailsFromJson(jsonArray);
        } catch (NullPointerException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (amcuConfig.getTruckFailCount() >= 3) {
            sendUpdationStatus();
        } else if (allTruckDetailsEnt != null && allTruckDetailsEnt.size() > 0) {
            amcuConfig.setTruckFailCount(amcuConfig.getTruckFailCount() + 1);
            addTruckToDatabase(allTruckDetailsEnt);

        }

        sendUpdationStatus();


    }

    public void getTruckData() {
        url = amcuConfig.getTruckUpdateUrl();
        new Thread(new Runnable() {

            @Override
            public void run() {

                AuthenticationParameters authParams =
                        new AuthenticationParameters(null, amcuConfig.getDeviceID(), amcuConfig.getDevicePassword());

                CommunicationService cs = null;
                try {
                    cs = new CommunicationService(amcuConfig.getURLHeader() +
                            amcuConfig.getServer(), authParams);
                } catch (IOException e) {
                    Log.d(TAG, e.getMessage());
                } catch (SSLContextCreationException e) {
                    Log.d(TAG, e.getMessage());
                }

                HttpResponse response = null;
                try {
                    response = cs.doGet(amcuConfig.getTruckUpdateUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IncompatibleProtocolException e) {
                    e.printStackTrace();
                }

                if (response != null) {
                    if (response.getResponseCode() >= 400 && response.getResponseCode() < 500) {
                        Util.displayErrorToast("Invalid response " + response.getResponseCode(), mContext);
                        sendUpdationStatus();
                    } else if (response != null && response.getResponseBody() != null) {
                        responseBody = response.getResponseBody();
                        afterGettingJSONArray();
                        Log.d(TAG, "Farmer");
                        Log.d(TAG, response.getResponseBody());
                    }
                }

            }
        }).start();
    }

    public void sendUpdationStatus() {

        final UpdateRatechartEntity urc = new UpdateRatechartEntity();
        urc.status = 1;
        urc.imeiNumber = amcuConfig.getDeviceID();
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverResponse = putData(url, urc);
                if (serverResponse.getSuccess()) {
                    amcuConfig.setTruckFailCount(0);
                    amcuConfig.setTruckUpdateUrl(null);
                }
            }
        }).start();
    }


    public ServerResponse putData(String url, Object obj) {

        ServerResponse sr = new ServerResponse();
        System.out.println("URL for put farmer update: " + url);
        String putBody = JSONParser.getAllJsonBmcId(obj);
        AmcuConfig amcuConfig = amcuConfig = AmcuConfig.getInstance();

        AuthenticationParameters authParams =
                new AuthenticationParameters(null, amcuConfig.getDeviceID(), amcuConfig.getDevicePassword());

        CommunicationService cs = null;

        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(), authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }

        com.devapp.devmain.httptasks.HttpResponse response = null;
        try {
            response = cs.doPut(url, putBody);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (IncompatibleProtocolException ipe) {
            ipe.printStackTrace();
        }

        String responseBody = null;
        if (response != null) {
            responseBody = response.getResponseBody();
        }
        if (response.getResponseCode() == 200) {
            sr.setSuccess(true);
            sr.setResponseString(response.toString());
        } else if (response.getResponseCode() == 201) {
            sr.setSuccess(true);
            sr.setResponseString(response.getResponseBody());

        } else {
            sr.setSuccess(false);
            sr.setErrorMessage(response.toString());
            LogInService.isAuthenticated = false;
        }

        if (response != null)
            System.out.println("JSON parser---Put response: "
                    + String.valueOf(response.getResponseCode())
                    + "  Response Body: "
                    + response.getResponseBody());

        return sr;
    }


    public ArrayList<TrucKInfo> getTruckDetailsFromJson(JSONArray jsonArray) throws JSONException {
        ArrayList<TrucKInfo> allTruckDetailsEnt = new ArrayList<TrucKInfo>();
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonData = jsonArray.getJSONObject(i);
            String truckId = jsonData.getString("truckId");

            if (truckId != null) {

                truckId = truckId.toUpperCase(Locale.ENGLISH);
            }

            try {


                if (truckId != null &&
                        !truckId.equalsIgnoreCase("null")) {

                    TrucKInfo truckInfor = new TrucKInfo();

                    if (jsonData.getString("driverContactNumber") != null &&
                            !jsonData.getString("driverContactNumber").equalsIgnoreCase("null"))
                        truckInfor.driverContactNumber = jsonData.getString("driverContactNumber");
                    if (jsonData.getString("driverName") != null &&
                            !jsonData.getString("driverName").equalsIgnoreCase("null"))
                        truckInfor.driverName = jsonData.getString("driverName");
                    if (jsonData.getString("contractStartDate") != null &&
                            !jsonData.getString("contractStartDate").equalsIgnoreCase("null"))
                        truckInfor.contractStartDate = jsonData.getLong("contractStartDate");

                    if (jsonData.getString("lastServiceDate") != null &&
                            !jsonData.getString("lastServiceDate").equalsIgnoreCase("null"))
                        truckInfor.lastServiceDate = jsonData.getLong("lastServiceDate");

                    if (jsonData.getString("make") != null &&
                            !jsonData.getString("make").equalsIgnoreCase("null"))
                        truckInfor.make = jsonData.getString("make");

                    if (jsonData.getString("maxCapacity") != null &&
                            !jsonData.getString("maxCapacity").equalsIgnoreCase("null"))
                        truckInfor.maxCapacity = jsonData.getDouble("maxCapacity");

                    if (jsonData.getString("model") != null &&
                            !jsonData.getString("model").equalsIgnoreCase("null"))
                        truckInfor.model = jsonData.getString("model");

                    if (jsonData.getString("numOfCans") != null &&
                            !jsonData.getString("numOfCans").equalsIgnoreCase("null"))
                        truckInfor.numOfCans = jsonData.getInt("numOfCans");

                    if (jsonData.getString("ownerContactNumber") != null &&
                            !jsonData.getString("ownerContactNumber").equalsIgnoreCase("null"))
                        truckInfor.ownerContactNumber = jsonData.getString("ownerContactNumber");

                    if (jsonData.getString("ownerName") != null &&
                            !jsonData.getString("ownerName").equalsIgnoreCase("null"))
                        truckInfor.ownerName = jsonData.getString("ownerName");

                    if (jsonData.getString("truckId") != null &&
                            !jsonData.getString("truckId").equalsIgnoreCase("null"))
                        truckInfor.truckId = jsonData.getString("truckId");

                    if (jsonData.getString("truckName") != null &&
                            !jsonData.getString("truckName").equalsIgnoreCase("null"))
                        truckInfor.truckName = jsonData.getString("truckName");

                    if (jsonData.getString("truckNumber") != null &&
                            !jsonData.getString("truckNumber").equalsIgnoreCase("null"))
                        truckInfor.truckNumber = jsonData.getString("truckNumber");

                    if (jsonData.getString("type") != null &&
                            !jsonData.getString("type").equalsIgnoreCase("null"))
                        truckInfor.type = jsonData.getString("type");

                    JSONArray jArrayRoutes = jsonData.getJSONArray("routes");
                    ArrayList<String> allroutes = new ArrayList<>();
                    if (jArrayRoutes != null && jArrayRoutes.length() > 0) {
                        for (int j = 0; j < jArrayRoutes.length(); j++) {
                            allroutes.add(jArrayRoutes.getString(j));
                        }
                    }
                    truckInfor.truckName = jsonData.getString("truckName");
                    truckInfor.routes = allroutes;


                    allTruckDetailsEnt.add(truckInfor);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return allTruckDetailsEnt;
    }

    public void addTruckToDatabase(ArrayList<TrucKInfo> allTruckDetailsEnt) {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        TruckCCDatabase truckDatabase = dbh.getTruckCCDatabase();
        truckDatabase.addCompleteTruckDetails(allTruckDetailsEnt, DatabaseHandler.isPrimary);
        truckDatabase.addCompleteTruckDetails(allTruckDetailsEnt, DatabaseHandler.isSecondary);
        Util.displayErrorToast("Truck list updated successfully", mContext);
        //Removed database close;
    }


}


