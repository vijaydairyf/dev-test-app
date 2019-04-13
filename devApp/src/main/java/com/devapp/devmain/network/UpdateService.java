package com.devapp.devmain.network;

import android.content.Context;

import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.ConfigSyncStatusUpdateEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.UpdateRatechartEntity;
import com.devapp.devmain.entity.UpdateStatus;
import com.devapp.devmain.entitymanager.AgentManager;
import com.devapp.devmain.entitymanager.ConfigEntityManager;
import com.devapp.devmain.entitymanager.FarmerManager;
import com.devapp.devmain.entitymanager.RateChartManager;
import com.devapp.devmain.httptasks.AuthenticationParameters;
import com.devapp.devmain.httptasks.CommunicationService;
import com.devapp.devmain.httptasks.HttpResponse;
import com.devapp.devmain.httptasks.IncompatibleProtocolException;
import com.devapp.devmain.httptasks.SSLContextCreationException;
import com.devapp.devmain.json.JSONParser;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.LogInService;
import com.devapp.devmain.server.ServerResponse;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.logger.Log;

import java.io.IOException;
import java.util.List;

/**
 * Created by x on 28/1/18.
 */

public class UpdateService {
    private static final String TAG = "UPDATE_SERVICE";
    static JSONParser jParser;
    public String responseBody;
    SessionManager session;
    AmcuConfig amcuConfig;
    String url;
    String server;
    ServerResponse serverResponse;
    EntityManager entityManager;
    private Context mContext;

    public UpdateService(Context context, EntityManager entityManager) {
        mContext = context;
        session = new SessionManager(mContext);
        jParser = new JSONParser();
        amcuConfig = AmcuConfig.getInstance();
        server = amcuConfig.getURLHeader() + amcuConfig.getServer();
        this.entityManager = entityManager;
    }

  /*  public void getDataFromConfigSync() {
        final String uri = "/amcu/configsync/";
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
                ConfigSyncPostEntity syncPostEntity = new ConfigSyncPostEntity();
                syncPostEntity.imeiNumber = amcuConfig.getDeviceID();
                syncPostEntity.centerId = session.getCollectionID();
                syncPostEntity.configurationType = entityManager.getType();
                String jsonBody = Util.getJsonFromObject(syncPostEntity);
                try {
                    response = cs.doPost(uri, jsonBody);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IncompatibleProtocolException e) {
                    e.printStackTrace();
                }
                if (response == null) {
                    Util.displayErrorToast("Invalid response please try again ", mContext);
//                    sendUpdateStatus();

                } else if (response.getResponseCode() >= 400 && response.getResponseCode() < 500) {
                    Util.displayErrorToast("Invalid response " + response.getResponseCode(), mContext);
//                    sendUpdateStatus();
                } else if (response != null && response.getResponseBody() != null) {
                    responseBody = response.getResponseBody();
                    String configBody = "";
                    ObjectMapper mapper = new ObjectMapper();
                    ConfigSyncEntity configSyncEntity = null;
                    try {
                        configBody = new JSONObject(responseBody).getJSONArray("configData").toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        configSyncEntity = mapper.readValue(responseBody, new TypeReference<ConfigSyncEntity>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        List<Entity> entityList = entityManager.getEntityFromJson(configBody, true);
                        entityManager.saveAll(entityList, true);

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InvalidUpdateException e) {
                        e.printStackTrace();
                    }
                    sendConfigSyncUpdateStatus(configSyncEntity.id, "SUCCESS");
                    Log.d(TAG, entityManager.getClass().toString());
                    Log.d(TAG, response.getResponseBody());
                }

            }
        }).start();
    }*/

    public void getDataFromDpn() {
        url = entityManager.getDpnUrl();
        if (url == null) {
            return;
        }
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
                    response = cs.doGet(url + "?version=" + Util.getVersionCode(mContext));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IncompatibleProtocolException e) {
                    e.printStackTrace();
                }
                if (response == null) {
                    Util.displayErrorToast("Invalid response please try again ", mContext);
                    sendUpdateStatus(UpdateStatus.FAILURE);

                } else if (response.getResponseCode() >= 400 && response.getResponseCode() < 500) {
                    Util.displayErrorToast("Invalid response " + response.getResponseCode(), mContext);
                    sendUpdateStatus(UpdateStatus.FAILURE);
                } else if (response != null && response.getResponseBody() != null) {
                    responseBody = response.getResponseBody();
                    try {
                        List<Entity> entityList = entityManager.getEntityFromJson(response.getResponseBody(), true);
                        entityManager.saveAll(entityList, true);

                        if (entityManager instanceof RateChartManager) {
                            DatabaseManager databaseManager = new DatabaseManager(mContext);
                            databaseManager.manageRateChart();
                        }

                        sendUpdateStatus(UpdateStatus.SUCCESS);
                        displayToast(entityManager);
                        Log.d(TAG, entityManager.getClass().toString());
                        Log.d(TAG, response.getResponseBody());

                    } catch (InvalidUpdateException e) {
                        e.printStackTrace();
                        Util.displayErrorToast(e.getMessage(), mContext);
                        sendUpdateStatus(e.getUpdateStatus());
                    } catch (Exception e) {
                        e.printStackTrace();
                        Util.displayErrorToast(e.getMessage(), mContext);
                        sendUpdateStatus(UpdateStatus.FAIL_TO_SAVE_DATA);
                    }

                }

            }
        }).start();
    }

    //TODO Add parameters
    public void sendUpdateStatus(int status) {

        final UpdateRatechartEntity urc = new UpdateRatechartEntity();
        urc.status = status;
        urc.imeiNumber = amcuConfig.getDeviceID();
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverResponse = putData(url, urc);
                if (serverResponse.getSuccess()) {
                    entityManager.resetDpnUrl();
                }
            }
        }).start();
    }

    private void displayToast(EntityManager entityManager) {
        if (entityManager instanceof RateChartManager) {
            Util.displayErrorToast(" Rate chart updated Successfully", mContext);
        } else if (entityManager instanceof FarmerManager) {
            Util.displayErrorToast(" Farmer(s) updated Successfully", mContext);

        } else if (entityManager instanceof AgentManager) {
            Util.displayErrorToast(" Agents(s) updated Successfully", mContext);

        } else if (entityManager instanceof ConfigEntityManager) {
            Util.displayErrorToast(" Configuration(s) updated Successfully", mContext);

        }

    }


    public void sendConfigSyncUpdateStatus(long id, String status) {
        final String uri = "/configsync/" + String.valueOf(id) + "/";
        final ConfigSyncStatusUpdateEntity body = new ConfigSyncStatusUpdateEntity();
        body.status = status;
        body.imeiNumber = amcuConfig.getDeviceID();
        body.id = id;
        new Thread(new Runnable() {
            @Override
            public void run() {
                ServerResponse serverResponse = putData(uri, body);
                if (serverResponse.getSuccess()) {
//                    TODO: Update status locally
                }

            }
        }).start();
    }

    public ServerResponse putData(String url, Object obj) {

        ServerResponse sr = new ServerResponse();
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
            Util.displayErrorToast("Invalid response!", mContext);
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

        return sr;
    }

}
