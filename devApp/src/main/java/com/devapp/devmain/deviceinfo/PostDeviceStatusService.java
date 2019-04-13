package com.devapp.devmain.deviceinfo;

/**
 * Created by u_pendra on 22/12/17.
 */


import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class PostDeviceStatusService extends IntentService {
    String centerId;
    private String URI = "/amcu/devices/parameters/status/";
    private AmcuConfig amcuConfig;
    private DatabaseHandler dbHandler;

    public PostDeviceStatusService() {
        super("POST_DEVICE_STATUS");
    }

    public static String getTaskName() {
        return "POST_DEVICE_STATUS";
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.v("service", "Sending device status ");
        initializeClass();

        //  Util.displayErrorToast("Device status Post Called!",getApplicationContext());
        ArrayList<DeviceStatusEntity> deviceStatusList = getAllUnsentDeviceStatus();
        if (deviceStatusList == null || deviceStatusList.size() == 0) {
            return;
        }
        DeviceStatusPostEntity postEntity = new DeviceStatusPostEntity();
        postEntity.centerId = centerId;
        postEntity.deviceId = amcuConfig.getDeviceID();
        postEntity.deviceStatusList = deviceStatusList;
        try {
            AuthenticationParameters authParameters = new AuthenticationParameters(null,
                    amcuConfig.getDeviceID(),
                    amcuConfig.getDevicePassword());
            CommunicationService communicationService = new CommunicationService(amcuConfig.getURLHeader()
                    + amcuConfig.getServer(),
                    authParameters);
            String jsonString = Util.getJsonFromObject(postEntity);
            Log.d("Device Status", jsonString);
            HttpResponse httpResponse = communicationService.doPost(URI, jsonString);
            if (httpResponse.getResponseCode() == HttpURLConnection.HTTP_CREATED
                    || httpResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                TODO Update db with sent status
                for (DeviceStatusEntity entity : deviceStatusList) {
                    entity.sent = DatabaseHandler.COL_REC_NW_SENT;
                    dbHandler.deleteTheDeviceInfoTable(entity);
                }

            } else {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SSLContextCreationException e) {
            e.printStackTrace();
        } catch (IncompatibleProtocolException e) {
            e.printStackTrace();
        }

    }

    private ArrayList<DeviceStatusEntity> getAllUnsentDeviceStatus() {
//        Todo get unsent records from db
        return dbHandler.getAllUnsentDeviceStatus();


    }

    public void initializeClass() {
        dbHandler = DatabaseHandler.getDatabaseInstance();
        amcuConfig = AmcuConfig.getInstance();
        centerId = new SessionManager(getApplicationContext()).getCollectionID();
    }
}
