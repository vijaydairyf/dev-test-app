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
import com.devapp.smartcc.database.AgentDatabase;
import com.devapp.smartcc.entities.AgentEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AgentManager {

    private static final String TAG = "AGENT_MANAGER";
    static JSONParser jParser;
    public String responseBody;
    JSONArray jsonArray;
    SessionManager session;
    AmcuConfig amcuConfig;
    String url;
    String server;
    ServerResponse serverResponse;
    String firstName, lastName, phoneNumber, mobileNuber, milkType, uniqueId1, uniqueId2, uniqueId3, shiftSupplyingTo, routeid, pickUpPoint;
    long registeredDate;
    int numberOfcans;
    double distanceToDileviry;
    private Context mContext;

    public AgentManager(Context context) {
        mContext = context;
        session = new SessionManager(mContext);
        jParser = new JSONParser();
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();

    }

    public void parseUrlToAgentInfo() {
        if (SmartCCConstants.AGENT_UPDATE_URI != null) {
            amcuConfig.setAgentUpdateUrl(SmartCCConstants.AGENT_UPDATE_URI);
            SmartCCConstants.AGENT_UPDATE_URI = null;
        }
        if (amcuConfig.getAgentUpdateUrl() != null) {
            getAgentData();
        }
    }

    public void afterGettingJSONArray() {
        ArrayList<AgentEntity> allAgentEntity = null;
        try {
            jsonArray = new JSONArray(responseBody);
            allAgentEntity = getAgentFromJson(jsonArray);
        } catch (NullPointerException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (amcuConfig.getAgentFailCount() >= 3) {
            sendUpdationStatus();
        } else if (allAgentEntity != null && allAgentEntity.size() > 0) {
            amcuConfig.setAgentFailCount(amcuConfig.getAgentFailCount() + 1);
            addAgentToDatabase(allAgentEntity);
        }

        sendUpdationStatus();

    }

    public void getAgentData() {
        url = amcuConfig.getAgentUpdateUrl();
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
                    response = cs.doGet(amcuConfig.getAgentUpdateUrl());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IncompatibleProtocolException e) {
                    e.printStackTrace();
                }

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
                    amcuConfig.setAgentFailCount(0);
                    amcuConfig.setAgentUpdateUrl(null);
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
            Util.displayErrorToast("Invalid Agent response!", mContext);
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

//        System.out.println("JSON parser---Put response: "
//                + String.valueOf(response.getResponseCode())
//                + "  Response Body: "
//                + response.getResponseBody());

        return sr;
    }

    public ArrayList<AgentEntity> getAgentFromJson(JSONArray jsonArray) throws JSONException {
        ArrayList<AgentEntity> allAgentEntity = new ArrayList<AgentEntity>();
        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonData = jsonArray.getJSONObject(i);
            String agentId = jsonData.getString("agentID");
            String agentBarcode = jsonData.getString("barcode");

            if (agentBarcode != null) {
                agentBarcode = agentBarcode.toUpperCase(Locale.ENGLISH);
            }

            if (agentId != null) {

                agentId = agentId.toUpperCase(Locale.ENGLISH);
            }

            try {

                if (agentId != null &&
                        !agentId.equalsIgnoreCase("null")) {
                    if (jsonData.getString("agentID") != null &&
                            !jsonData.getString("agentID").equalsIgnoreCase("null"))
                        agentId = jsonData.getString("agentID");
                    if (jsonData.getString("firstName") != null &&
                            !jsonData.getString("firstName").equalsIgnoreCase("null"))
                        firstName = jsonData.getString("firstName");
                    if (jsonData.getString("lastName") != null &&
                            !jsonData.getString("lastName").equalsIgnoreCase("null"))
                        lastName = jsonData.getString("lastName");
                    if (jsonData.getString("phoneNum") != null &&
                            !jsonData.getString("phoneNum").equalsIgnoreCase("null"))
                        phoneNumber = jsonData.getString("phoneNum");
                    if (jsonData.getString("mobileNum") != null &&
                            !jsonData.getString("mobileNum").equalsIgnoreCase("null"))
                        mobileNuber = jsonData.getString("mobileNum");
                    if (jsonData.getString("milkType") != null &&
                            !jsonData.getString("milkType").equalsIgnoreCase("null"))
                        milkType = jsonData.getString("milkType");

                    if (jsonData.getString("registeredDate") != null &&
                            !jsonData.getString("registeredDate").equalsIgnoreCase("null"))
                        registeredDate = jsonData.getLong("registeredDate");

                    if (jsonData.getString("uniqueID1") != null &&
                            !jsonData.getString("uniqueID1").equalsIgnoreCase("null"))
                        uniqueId1 = jsonData.getString("uniqueID1");
                    if (jsonData.getString("uniqueID2") != null &&
                            !jsonData.getString("uniqueID2").equalsIgnoreCase("null"))
                        uniqueId2 = jsonData.getString("uniqueID2");

                    if (jsonData.getString("uniqueID3") != null &&
                            !jsonData.getString("uniqueID3").equalsIgnoreCase("null"))
                        uniqueId3 = jsonData.getString("uniqueID3");

                    if (jsonData.getString("numberOfCans") != null &&
                            !jsonData.getString("numberOfCans").equalsIgnoreCase("null"))
                        numberOfcans = jsonData.getInt("numberOfCans");

                    if (jsonData.getString("shiftsSupplyingTo") != null &&
                            !jsonData.getString("shiftsSupplyingTo").equalsIgnoreCase("null"))
                        shiftSupplyingTo = jsonData.getString("shiftsSupplyingTo");

                    if (jsonData.getString("agentID") != null &&
                            !jsonData.getString("agentID").equalsIgnoreCase("null"))
                        routeid = jsonData.getString("agentID");

                    if (jsonData.getString("pickupPoint") != null &&
                            !jsonData.getString("pickupPoint").equalsIgnoreCase("null"))
                        pickUpPoint = jsonData.getString("pickupPoint");

                    if (jsonData.getString("distanceToDelivery") != null &&
                            !jsonData.getString("distanceToDelivery").equalsIgnoreCase("null"))
                        distanceToDileviry = jsonData.getDouble("distanceToDelivery");

                    JSONArray jCenterArray = jsonData.getJSONArray("centerIdList");

                    List<String> centerList = new ArrayList<String>();

                    for (int j = 0; j < jCenterArray.length(); j++) {
                        centerList.add(jCenterArray.getString(j));

                    }

                    AgentEntity agentEntity = new AgentEntity(agentId, agentBarcode, firstName
                            , lastName, phoneNumber, mobileNuber, milkType, registeredDate,
                            uniqueId1, uniqueId2, uniqueId3,
                            numberOfcans, shiftSupplyingTo, routeid, pickUpPoint
                            , distanceToDileviry, centerList);

                    allAgentEntity.add(agentEntity);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return allAgentEntity;
    }

    public void addAgentToDatabase(ArrayList<AgentEntity> allAgentEntity) {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        AgentDatabase agentDatabase = dbh.getAgentDatabase();
        agentDatabase.addCompleteAgentDetails(allAgentEntity, DatabaseHandler.isPrimary);
        agentDatabase.addCompleteAgentDetails(allAgentEntity, DatabaseHandler.isSecondary);
        Util.displayErrorToast("Agent list updated successfully", mContext);
        //Removed database close;
    }
}


