package com.devapp.devmain.devicemanager;

/**
 * Created by Upendra on 9/8/2015.
 */

import android.content.Context;
import android.util.Log;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.UpdateRatechartEntity;
import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.LogInService;
import com.devapp.devmain.server.ServerResponse;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


public class FarmerManager {

    private static final String TAG = "FARMER_MANAGER";
    static JSONParser jParser;
    public String responseBody;
    JSONArray jsonArray;
    SessionManager session;
    AmcuConfig amcuConfig;
    String url;
    String server;
    ServerResponse serverResponse;
    private Context mContext;
    private FarmerDao farmerDao;

    public FarmerManager(Context context) {
        mContext = context;
        session = new SessionManager(mContext);
        jParser = new JSONParser();
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);

    }

    public void parseUrlToGetConfiguration() {
        if (Util.FARMER_URI != null && Util.FARMER_URI.contains("amcu")) {
            amcuConfig.setFarmerURI(Util.FARMER_URI);
            Util.FARMER_URI = null;
        }
        if (amcuConfig.getFarmerURI() != null) {
            getFarmerData();
        }
    }

    public void afterGettingJSONArray() {
        ArrayList<FarmerEntity> allFarmerEntity = null;
        try {
            jsonArray = new JSONArray(responseBody);
            allFarmerEntity = getConfigurableEntityFromJSON(jsonArray);
        } catch (NullPointerException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (amcuConfig.getFarmerFailCount() >= 3) {
            sendUpdationStatus();
        } else if (allFarmerEntity != null && allFarmerEntity.size() > 0) {
            amcuConfig.setFarmerFailCount(amcuConfig.getFarmerFailCount() + 1);
            addFarmerToDatabase(allFarmerEntity);
        }
        sendUpdationStatus();

    }

    public void getFarmerData() {
        url = amcuConfig.getFarmerURI();
        new Thread(new Runnable() {

            @Override
            public void run() {

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

                HttpResponse response = null;
                try {
                    response = cs.doGet(amcuConfig.getFarmerURI());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IncompatibleProtocolException e) {
                    e.printStackTrace();
                }
                if (response == null) {
                    Util.displayErrorToast("Invalid response please try again ", mContext);
                    sendUpdationStatus();

                } else if (response.getResponseCode() >= 400 && response.getResponseCode() < 500) {
                    Util.displayErrorToast("Invalid response " + response.getResponseCode(), mContext);
                    sendUpdationStatus();
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
                    amcuConfig.setFarmerFailCount(0);
                    amcuConfig.setFarmerURI(null);
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
                new AuthenticationParameters(null, amcuConfig.getDeviceID(),
                        amcuConfig.getDevicePassword());

        CommunicationService cs = null;

        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(),
                    authParams);
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
            Util.displayErrorToast("Invalid farmer response!", mContext);
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

//        if (response != null)
//            System.out.println("JSON parser---Put response: "
//                    + String.valueOf(response.getResponseCode())
//                    + "  Response Body: "
//                    + response.getResponseBody());

        return sr;
    }


    public ArrayList<FarmerEntity> getConfigurableEntityFromJSON(JSONArray jsonArray) throws JSONException {
        ArrayList<FarmerEntity> allFarmerEntities = new ArrayList<FarmerEntity>();

        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonData = jsonArray.getJSONObject(i);
            String farmerId = Util.validateFarmerId(jsonData.getString("farmer_id"),
                    mContext, amcuConfig.getFarmerIdDigit(), false);
            if (!ValidationHelper.isValidFarmerId(farmerId, amcuConfig.getFarmerIdDigit())) {
                Util.displayErrorToast("Invalid farmer Id " + jsonData.getString("farmer_id"), mContext);
                continue;
            }
            String barcode = jsonData.getString("farmer_barcode");
            int l = barcode.length();

            if (barcode == null) {
                barcode = "";
            } else if (barcode.equalsIgnoreCase("null")) {
                barcode = "";
            } else if (barcode.length() < 1) {
                barcode = "";
            } else {
                barcode = barcode.toUpperCase(Locale.ENGLISH);
            }

            if (farmerId != null) {
                farmerId = farmerId.toUpperCase(Locale.ENGLISH);
            }

            try {
                String duplicateMessage = null;
                duplicateMessage = Util.getDuplicateIdOrBarCode(farmerId, barcode, mContext);
                if (farmerId != null && (jsonData.getString("farmer_name") != null &&
                        !jsonData.getString("farmer_name").equalsIgnoreCase("null")) && (duplicateMessage == null
                        || (duplicateMessage != null && duplicateMessage.contains("Member")))) {

                    FarmerEntity farmerEntity = new FarmerEntity();
                    if (jsonData.getString("farm_mob") == null) {
                        farmerEntity.farm_mob = "";
                    } else if (jsonData.getString("farm_mob").equalsIgnoreCase("null")) {
                        farmerEntity.farm_mob = "";
                    } else {
                        farmerEntity.farm_mob = jsonData.getString("farm_mob");

                    }

                    farmerEntity.farm_email = jsonData.getString("farm_email");
                    farmerEntity.farmer_id = ValidationHelper.paddingOnlyFarmerId(
                            Integer.parseInt(farmerId), amcuConfig.getFarmerIdDigit());

                    if (barcode == null || barcode.replace(" ", "").length() < 2) {
                        farmerEntity.farmer_barcode = null;
                    } else {
                        farmerEntity.farmer_barcode = barcode;
                    }

                    if (jsonData.getString("farmer_cans") != null
                            && jsonData.getString("farmer_cans").equalsIgnoreCase("null")) {
                        farmerEntity.farmer_cans = String.valueOf(1);
                    } else if (jsonData.getString("farmer_cans") == null) {
                        farmerEntity.farmer_cans = String.valueOf(1);
                    } else {
                        try {
                            farmerEntity.farmer_cans = String.valueOf(Integer.parseInt(jsonData.getString("farmer_cans")));
                        } catch (NumberFormatException e) {

                            e.printStackTrace();
                            farmerEntity.farmer_cans = String.valueOf(1);
                            Util.displayErrorToast("Invalid cans for farmer", mContext);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    farmerEntity.farmer_id = ValidationHelper.paddingOnlyFarmerId(Integer.parseInt(farmerId)
                            , amcuConfig.getFarmerIdDigit());

                    if (jsonData.getString("farmer_cattle") == null) {
                        farmerEntity.farmer_cattle = "COW";
                    } else if (jsonData.getString("farmer_cattle").equalsIgnoreCase("null")) {
                        farmerEntity.farmer_cattle = "COW";
                    } else {
                        farmerEntity.farmer_cattle = jsonData.getString("farmer_cattle");
                    }

                    farmerEntity.farmer_name = jsonData.getString("farmer_name");


                    try {
                        farmerEntity.farmer_regDate = jsonData.getLong("farmer_regDate");
                    } catch (JSONException e) {
                        farmerEntity.farmer_regDate = System.currentTimeMillis();
                        e.printStackTrace();
                    }
                    farmerEntity.soc_code = jsonData.getString("soc_code");

                    if (jsonData.getString("numBuff") == null) {
                        farmerEntity.numBuff = String.valueOf(Integer.parseInt("0"));
                    } else if (jsonData.getString("numBuff").equalsIgnoreCase("null")) {
                        farmerEntity.numBuff = String.valueOf(0);
                    } else {
                        try {
                            farmerEntity.numBuff = String.valueOf(Integer.parseInt(jsonData.getString("numBuff")));
                        } catch (NumberFormatException e) {
                            farmerEntity.numBuff = String.valueOf(0);
                            Util.displayErrorToast("Invalid number of Buffalo", mContext);
                            e.printStackTrace();
                        }
                    }

                    if (jsonData.getString("numCow") == null) {
                        farmerEntity.numCow = String.valueOf(Integer.parseInt("0"));
                    } else if (jsonData.getString("numCow").equalsIgnoreCase("null")) {
                        farmerEntity.numCow = String.valueOf(0);
                    } else {
                        try {
                            farmerEntity.numCow = String.valueOf(Integer.parseInt(jsonData.getString("numCow")));
                        } catch (NumberFormatException e) {
                            farmerEntity.numCow = String.valueOf(0);
                            Util.displayErrorToast("Invalid number of cow", mContext);
                            e.printStackTrace();
                        }
                    }

                    if (jsonData.getString("isMultipleCans") != null && !jsonData.getString("isMultipleCans").equalsIgnoreCase("null")) {
                        farmerEntity.isMultipleCans = Boolean.parseBoolean(jsonData.getString("isMultipleCans"));
                    } else {
                        farmerEntity.isMultipleCans = false;
                    }

                    farmerEntity.agentId = jsonData.optString("agentId", AppConstants.NA);
                    farmerEntity.farmerType = jsonData.optString("producerType", AppConstants.FARMER_TYPE_FARMER);

                    //                farmerEntity.assignRatechart=jsonData.getString("assignRatechart");
                    //                farmerEntity.numCattle=jsonData.getString("numCattle");


                    if (farmerEntity.agentId == null || farmerEntity.agentId.equalsIgnoreCase("null") ||
                            farmerEntity.agentId.equalsIgnoreCase(AppConstants.NA)) {
                        allFarmerEntities.add(farmerEntity);
                    } else if (farmerDao.findById(farmerEntity.agentId) != null) {
                        allFarmerEntities.add(farmerEntity);
                    } else {
                        Util.displayErrorToast("Agent id not exist! " + farmerEntity.agentId, mContext);
                        continue;
                    }


                } else {
                    Util.displayErrorToast(duplicateMessage, mContext);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return allFarmerEntities;
    }

    public void addFarmerToDatabase(ArrayList<FarmerEntity> allFarmEnt) {
       /* DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        dbh.addCompleteFarmers(allFarmEnt, DatabaseHandler.isPrimary);
        dbh.addCompleteFarmers(allFarmEnt, DatabaseHandler.isSecondary);
        Util.displayErrorToast("Farmer list updated successfully", mContext);*/
    }

}

