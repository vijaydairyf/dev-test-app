package com.devapp.devmain.devicemanager;


import android.content.Context;
import android.util.Log;

import com.devapp.devmain.entity.CenterEntity;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Upendra on 13/1/2015.
 */


public class ChillingCenterManager {

    private static final String TAG = "CENTER_MANAGER";
    static JSONParser jParser;
    public String responseBody;
    JSONArray jsonArray;
    SessionManager session;
    AmcuConfig amcuConfig;
    String url;
    String server;
    ServerResponse serverResponse;
    private Context mContext;

    public ChillingCenterManager(Context context) {
        mContext = context;
        session = new SessionManager(mContext);
        jParser = new JSONParser();
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();

    }

    public void parseUrlToGetChillingInfo() {
        if (Util.CHILLING_CENTER_URI != null && Util.CHILLING_CENTER_URI.contains("amcu")) {
            amcuConfig.setChillingCenterUri(Util.CHILLING_CENTER_URI);
            Util.CHILLING_CENTER_URI = null;
        }
        if (amcuConfig.getChillingUri() != null) {
            getChillingCenterData();
        }
    }

    public void afterGettingJSONArray() {
        ArrayList<CenterEntity> allCenterEntity = null;
        try {
            jsonArray = new JSONArray(responseBody);
            allCenterEntity = getChillingEntityFromJSON(jsonArray);
        } catch (NullPointerException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (amcuConfig.getCenterFailCount() >= 3) {
            amcuConfig.setCenterFailCount(amcuConfig.getCenterFailCount() + 1);
            sendUpdationStatus();
        } else if (allCenterEntity != null && allCenterEntity.size() > 0) {
            addCenterToDatabase(allCenterEntity);
        }
        sendUpdationStatus();
    }

    public void getChillingCenterData() {
        url = amcuConfig.getChillingUri();
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
                    //         response = cs.doGet(saveSession.getChillingUri());
                    response = cs.doGet(url + "?version=" + Util.getVersionCode(mContext));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IncompatibleProtocolException e) {
                    e.printStackTrace();
                }

                if (response == null ||
                        (response.getResponseCode() >= 400 && response.getResponseCode() < 500)) {

                    sendUpdationStatus();
                    Util.displayErrorToast("Invalid response please try again ", mContext);
                } else if (response != null && response.getResponseBody() != null) {
                    responseBody = response.getResponseBody();
                    afterGettingJSONArray();
                    Log.d(TAG, "Farmer");
                    Log.d(TAG, response.getResponseBody());
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
                    amcuConfig.setCenterFailCount(0);
                    amcuConfig.setChillingCenterUri(null);
                }

            }
        }).start();
    }


    public ServerResponse putData(String url, Object obj) {

        ServerResponse sr = new ServerResponse();
        System.out.println("URL for put farmer update: " + url);
        String putBody = JSONParser.getAllJsonBmcId(obj);
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
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

        if (response == null) {
            Util.displayErrorToast("Invalid Chilling center response!", mContext);
            sr.setSuccess(false);
            sr.setErrorMessage("Null response");
        } else if (response.getResponseCode() == 200) {
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

        if (response != null) {
            System.out.println("JSON parser---Put response: "
                    + String.valueOf(response.getResponseCode())
                    + "  Response Body: "
                    + response.getResponseBody());
        }
        return sr;
    }


    public ArrayList<CenterEntity> getChillingEntityFromJSON(JSONArray jsonArray) throws JSONException {
        ArrayList<CenterEntity> allFarmerEntities = new ArrayList<CenterEntity>();

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonData = jsonArray.getJSONObject(i).getJSONObject("producerProfile");

            String centerId = jsonData.getString("id");
            String centerBarcode = jsonData.getString("barcode");

            if (centerBarcode != null) {
                centerBarcode = centerBarcode.toUpperCase(Locale.ENGLISH);
            }

            if (centerId != null) {

                centerId = centerId.toUpperCase(Locale.ENGLISH);
            }

            try {
                String duplicateMessage = null;

                duplicateMessage = Util.checkForRegisteredCode(centerId,
                        AmcuConfig.getInstance().getFarmerIdDigit(), true);
                if (duplicateMessage == null) {
                    duplicateMessage = Util.getDuplicateIdOrBarCode(centerId, centerBarcode, mContext);
                }

                if (centerId != null &&
                        !centerId.equalsIgnoreCase("null") && (duplicateMessage == null
                        || (duplicateMessage != null && duplicateMessage.contains("Center")))) {

                    CenterEntity centerEntity = new CenterEntity();

                    if ((duplicateMessage != null && duplicateMessage.contains("Center"))) {
                        centerEntity.centerId = centerId.toUpperCase(Locale.ENGLISH);
                        int id = -1;
                        try {
                            id = Integer.parseInt(centerId);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        if (id != -1) {
                            DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();

                            String centerCode = databaseHandler.getDuplicateCenterId(id);

                            if (centerCode != null) {
                                centerId = centerCode;
                                centerEntity.centerId = centerId.toUpperCase(Locale.ENGLISH);
                            }

                        }
                    } else {
                        centerEntity.centerId = centerId.toUpperCase(Locale.ENGLISH);
                    }

                    if (jsonData.getString("mobileNumber") == null) {
                        centerEntity.contactNumber = "";
                    } else if (jsonData.getString("mobileNumber").equalsIgnoreCase("null")) {
                        centerEntity.contactNumber = "";
                    } else {
                        centerEntity.contactNumber = jsonData.getString("mobileNumber");

                    }

                    if (jsonData.getString("email") != null &&
                            !jsonData.getString("email").equalsIgnoreCase("null")) {
                        centerEntity.contactEmailId = jsonData.getString("email");
                    }


                    centerEntity.chillingCenterId = session.getCollectionID();
                    if (centerBarcode != null && !centerBarcode.equalsIgnoreCase("null")) {
                        centerEntity.centerBarcode = centerBarcode.toUpperCase(Locale.ENGLISH);
                    }

                    if (jsonData.getString("contactPersonName") == null) {
                        centerEntity.contactPerson = "";
                    } else if (jsonData.getString("contactPersonName").equalsIgnoreCase("null")) {
                        centerEntity.contactPerson = "";
                    } else {
                        centerEntity.contactPerson = jsonData.getString("contactPersonName");
                    }

                    try {
                        centerEntity.registerDate = jsonData.getLong("lastModified");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (jsonData.getString("name") == null) {
                        centerEntity.centerName = "";
                    } else if (jsonData.getString("name").equalsIgnoreCase("null")) {
                        centerEntity.centerName = "";
                    } else {
                        centerEntity.centerName = jsonData.getString("name").toUpperCase(Locale.ENGLISH);

                    }
                    JSONObject jsonDataRoute = jsonArray.getJSONObject(i);
                    //adding route
                    if (jsonDataRoute.getString("centerRoute") == null) {
                        centerEntity.centerRoute = "NA";
                    } else if (jsonDataRoute.getString("centerRoute").equalsIgnoreCase("null")) {
                        centerEntity.centerRoute = "NA";
                    } else {
                        centerEntity.centerRoute = jsonDataRoute.getString("centerRoute");
                    }

                    if (amcuConfig.getSmartCCFeature() && centerEntity.centerRoute.equalsIgnoreCase("NA")) {
                        Util.displayErrorToast("Enter the valid route for " + centerEntity.centerId, mContext);
                        continue;
                    }

                    //adding chilling center multiple or single
                    if (jsonData.getString("isMultipleCans") != null && jsonData.getBoolean("isMultipleCans") != true) {
                        centerEntity.singleOrMultiple = Util.SINGLE;
                    } else {
                        centerEntity.singleOrMultiple = Util.MULTIPLE;
                    }

                    if (jsonData.getString("milkType") == null ||
                            jsonData.getString("milkType").equalsIgnoreCase("null")) {
                        centerEntity.cattleType = "COW";
                    } else {

                        centerEntity.cattleType = jsonData.getString("milkType").toUpperCase(Locale.ENGLISH);
                    }
                    if (jsonData.has("isActive")) {
                        if (jsonData.getBoolean("isActive")) {
                            centerEntity.activeStatus = "1";
                        } else {
                            centerEntity.activeStatus = "0";
                        }
                    }
                    allFarmerEntities.add(centerEntity);
                } else {
                    Util.displayErrorToast(duplicateMessage, mContext);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return allFarmerEntities;
    }

    public void addCenterToDatabase(ArrayList<CenterEntity> allCenterEnt) {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        dbh.addCompleteChillingCenter(allCenterEnt, DatabaseHandler.isPrimary);
        dbh.addCompleteChillingCenter(allCenterEnt, DatabaseHandler.isSecondary);
        Util.displayErrorToast("Center list updated successfully", mContext);
        //Removed database close;
    }
}


