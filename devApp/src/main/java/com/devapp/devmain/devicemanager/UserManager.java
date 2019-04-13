package com.devapp.devmain.devicemanager;

import android.content.Context;
import android.util.Log;

import com.devapp.devmain.entity.UpdateRatechartEntity;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.ServerResponse;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by u_pendra on 29/12/16.
 * To get userEntity from the JSON entity
 */

public class UserManager {


    private static final String TAG = "USER_MANAGER";
    static JSONParser jParser;
    public String responseBody;
    JSONArray jsonArray;
    SessionManager session;
    AmcuConfig amcuConfig;
    String url;
    String server;
    ServerResponse serverResponse;
    private Context mContext;

    public UserManager(Context context) {
        mContext = context;
        session = new SessionManager(mContext);
        jParser = new JSONParser();
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();

    }

    public void parseUrlToGetUsers() {
        if (SmartCCConstants.USER_UPDATE_URI != null
                && SmartCCConstants.USER_UPDATE_URI.contains("amcu")) {
            amcuConfig.setUserUri(SmartCCConstants.USER_UPDATE_URI);
            SmartCCConstants.USER_UPDATE_URI = null;
        }
        if (amcuConfig.getUserUri() != null) {
            getUserData();
        }
    }

    public void afterGettingJSONArray(String response) {
        ArrayList<UserEntity> allUserEntity = null;
        try {
            jsonArray = new JSONArray(response);
            allUserEntity = getUserEntityFromJSON(jsonArray);
        } catch (NullPointerException e) {

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (allUserEntity != null && allUserEntity.size() > 0) {
            //  amcuConfig.setFarmerFailCount(amcuConfig.getUserFailCount()+1);
            addUserEntityToDatabase(allUserEntity);
        }
    }

    public void getUserData() {
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

                } else if (response.getResponseCode() >= 400
                        && response.getResponseCode() < 500) {
                    Util.displayErrorToast("Invalid response "
                            + response.getResponseCode(), mContext);
                    sendUpdationStatus();
                } else if (response != null &&
                        response.getResponseBody() != null) {
                    responseBody = response.getResponseBody();
                    afterGettingJSONArray(responseBody);
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
                    amcuConfig.setUserFailCount(0);
                    amcuConfig.setUserUri(null);
                }
            }
        }).start();
    }


    public ServerResponse putData(String url, Object obj) {

        ServerResponse sr = new ServerResponse();
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


        if (response.getResponseCode() == 200) {
            sr.setSuccess(true);
            sr.setResponseString(response.toString());
        } else if (response.getResponseCode() == 201) {
            sr.setSuccess(true);
            sr.setResponseString(response.getResponseBody());

        } else {
            sr.setSuccess(false);
            sr.setErrorMessage(response.toString());
        }

        if (response != null)
            System.out.println("JSON parser---Put response: "
                    + String.valueOf(response.getResponseCode())
                    + "  Response Body: "
                    + response.getResponseBody());

        return sr;
    }


    public ArrayList<UserEntity> getUserEntityFromJSON(JSONArray jsonArray) throws JSONException {
        ArrayList<UserEntity> allUserEntity = new ArrayList<UserEntity>();

        for (int i = 0; i < jsonArray.length(); i++) {

            UserEntity userEntity = new UserEntity();
            JSONObject jsonData = jsonArray.getJSONObject(i);

            userEntity.role = jsonData.getString("role");
            userEntity.name = jsonData.getString("name");
            userEntity.userId = jsonData.getString("userId");
            userEntity.mobile = jsonData.getString("mobileNumber");
            userEntity.emailId = jsonData.getString("emailId");
            userEntity.password = jsonData.getString("password");
            userEntity.centerId = String.valueOf(session.getSocietyColumnId());
            userEntity.regDate = Calendar.getInstance().getTimeInMillis();

            if (jsonData.getString("emailId") == null ||
                    jsonData.getString("emailId").equalsIgnoreCase("null")) {
                userEntity.emailId = "";
            }

            allUserEntity.add(userEntity);
        }

        return allUserEntity;
    }

    public void addUserEntityToDatabase(ArrayList<UserEntity> allUsesrEntity) {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        dbh.addCompleteUser(allUsesrEntity, DatabaseHandler.isPrimary);
        dbh.addCompleteUser(allUsesrEntity, DatabaseHandler.isSecondary);
        Util.displayErrorToast("User list updated successfully", mContext);
    }
}
