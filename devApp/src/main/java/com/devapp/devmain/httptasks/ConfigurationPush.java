package com.devapp.devmain.httptasks;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.ConfigurationPushEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;


/**
 * Created by u_pendra on 23/2/17.
 */

public class ConfigurationPush extends IntentService {


    public static final String TAG = "POST_CONFIGURATION";
    private final String SUFFIX = "/configuration";
    AmcuConfig amcuConfig;
    private String CONFIGURATION_URL = "/amcu/configurations/";
    private String CENTERID = "centerId";

    public ConfigurationPush() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        initializeClass();
        ArrayList<ConfigurationElement> allConfigPushList = getAllUnsentConfiguration();

        if (allConfigPushList == null || allConfigPushList.size() == 0) {
            return;
        }

        try {

            ConfigurationPushEntity configurationPushEntity = new ConfigurationPushEntity();

            configurationPushEntity.configurationList = allConfigPushList;
            configurationPushEntity.deviceId = amcuConfig.getDeviceID();
            configurationPushEntity.societyId = CENTERID;


            String jsonString = Util.getJsonFromObject(configurationPushEntity);
            Log.d("Configuration Push", jsonString);

            AuthenticationParameters authParameters = new AuthenticationParameters(null,
                    amcuConfig.getDeviceID(),
                    amcuConfig.getDevicePassword());
            CommunicationService communicationService = null;
            communicationService = new CommunicationService(amcuConfig.getURLHeader()
                    + amcuConfig.getServer(),
                    authParameters);

            try {
                HttpResponse httpResponse = communicationService.doPost(CONFIGURATION_URL, jsonString, true);
                if (httpResponse.getResponseCode() == HttpURLConnection.HTTP_CREATED
                        || httpResponse.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    for (int i = 0; i < allConfigPushList.size(); i++) {
                        DatabaseHandler.getDatabaseInstance().updateConfigEntity(allConfigPushList.get(i),
                                DatabaseEntity.FARMER_SENT_CODE);
                    }
                    Util.displayErrorToast("Configuration pushed successfully!", getApplicationContext());


                } else {
                    return; // Further not attempted to be sent
                }

            } catch (IncompatibleProtocolException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SSLContextCreationException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ConfigurationElement> getAllUnsentConfiguration() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        ArrayList<ConfigurationElement> allConfigPushList = new ArrayList<ConfigurationElement>();
        try {
            allConfigPushList = dbh.getAllUnsentConfigurationEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allConfigPushList;
    }

    public void initializeClass() {
        amcuConfig = AmcuConfig.getInstance();
        CENTERID = new SessionManager(getApplicationContext()).getCollectionID();
    }
}
