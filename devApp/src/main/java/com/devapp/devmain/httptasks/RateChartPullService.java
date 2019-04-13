package com.devapp.devmain.httptasks;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.network.UpdateService;
import com.devapp.devmain.network.UpdateServiceFactory;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.ServerAPI;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.io.IOException;

/**
 * Created by Upendra on 6/19/2015.
 */
public class RateChartPullService extends IntentService {
    private static final String TAG = "RATE_CHART_PULL_SERVICE";
    private static final String PULL_RATE_CHART_TASK = "PULL_RATE_CHART_TASK"; // Task name
    private String DPN_API = "/amcu/checkfordpn";

    public RateChartPullService() {
        super(PULL_RATE_CHART_TASK);
    }

    public static String getTaskName() {
        return PULL_RATE_CHART_TASK;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        DPN_API = "/amcu/checkfordpn";
        DPN_API = DPN_API + "?version=" + Util.getVersionCode(getApplicationContext());

        System.out.println("DPN: " + DPN_API);

        boolean fromCheckUpdate = false;
        try {
            fromCheckUpdate = intent.getBooleanExtra("FromCheckUpdate", false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Util.RATECHART_URI == null && amcuConfig.getUpdateURI() == null) {
            try {
                getRateChartDPN();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Util.RATECHART_URI != null || amcuConfig.getUpdateURI() != null) {
            showUpdateToast(fromCheckUpdate, "Rate chart");
            /*RateChartFromJSON rateChartFromJSON = new RateChartFromJSON(getApplicationContext());
            rateChartFromJSON.parseUrlAndGetRateChart();*/
            UpdateService updateService = new UpdateServiceFactory(this).getUpdateService(AppConstants.ConfigurationTypes.RATE_CHART);
            updateService.getDataFromDpn();

        }
        //For configuration pull and farmer list pull
        if (Util.CONFIGURATION_URI != null || amcuConfig.getConfigurationURI() != null) {
            showUpdateToast(fromCheckUpdate, "Configuration");
            /*ConfigurationManager configurationManager = new ConfigurationManager(getApplicationContext());
            configurationManager.parseUrlToGetConfiguration();*/
            UpdateService updateService = new UpdateServiceFactory(this).getUpdateService(AppConstants.ConfigurationTypes.CONFIGURATION);
            updateService.getDataFromDpn();
        }
        if (Util.FARMER_URI != null || amcuConfig.getFarmerURI() != null) {
            showUpdateToast(fromCheckUpdate, "Farmers");
            /*FarmerService farmerService = new FarmerService(getApplicationContext());
            farmerService.getFarmerDataFromDpn();*/
            UpdateService updateService = new UpdateServiceFactory(this).getUpdateService(AppConstants.ConfigurationTypes.FARMER);
            updateService.getDataFromDpn();
            /*FarmerManager farmerManager = new FarmerManager(getApplicationContext());
            farmerManager.parseUrlToGetConfiguration();*/
        }

        if (Util.CHILLING_CENTER_URI != null || amcuConfig.getChillingUri() != null
                && amcuConfig.getEnableCenterCollection()) {
            showUpdateToast(fromCheckUpdate, "Chilling center");
            /*ChillingCenterManager chillingManager = new ChillingCenterManager(getApplicationContext());
            chillingManager.parseUrlToGetChillingInfo();*/
            UpdateService updateService = new UpdateServiceFactory(this).getUpdateService(AppConstants.ConfigurationTypes.COLLECTION_CENTER_LIST);
            updateService.getDataFromDpn();
        }

        if (Util.APK_URI != null || amcuConfig.getAPKUri() != null) {
            showUpdateToast(fromCheckUpdate, "smartAmcu APK");
            Util.updateApkFromDPN(getApplicationContext());
        }

        if (SmartCCConstants.AGENT_UPDATE_URI != null || amcuConfig.getAgentUpdateUrl() != null) {
            showUpdateToast(fromCheckUpdate, "Agent list");
            UpdateService updateService = new UpdateServiceFactory(this).getUpdateService(AppConstants.ConfigurationTypes.AGENT);
            updateService.getDataFromDpn();
            /*AgentManager agentManager = new AgentManager(getApplicationContext());
            agentManager.parseUrlToAgentInfo();*/
        }

        if (SmartCCConstants.TRUCK_UPDATE_URI != null || amcuConfig.getTruckUpdateUrl() != null) {
            showUpdateToast(fromCheckUpdate, "Truck list");
            /*TruckManager truckManager = new TruckManager(getApplicationContext());
            truckManager.parseUrlToTruckInfo();*/
            UpdateService updateService = new UpdateServiceFactory(this).getUpdateService(AppConstants.ConfigurationTypes.TRUCK);
            updateService.getDataFromDpn();
        }
        if (Util.INCENTIVE_RATECHART_URI != null || amcuConfig.getIncentiveUpdateURI() != null) {
            showUpdateToast(fromCheckUpdate, "Incentive Rate chart");
            /*IncentiveRateChartFromJSON incentiveRateChartFromJSON = new IncentiveRateChartFromJSON(getApplicationContext());
            incentiveRateChartFromJSON.parseUrlAndGetRateChart();*/
            UpdateService updateService = new UpdateServiceFactory(this).getUpdateService(AppConstants.ConfigurationTypes.INCENTIVE_RATE_CHART);
            updateService.getDataFromDpn();

        }

        if (fromCheckUpdate && ((amcuConfig.getAPKUri() == null) && (amcuConfig.getFarmerURI() == null)
                && amcuConfig.getChillingUri() == null
                && (amcuConfig.getConfigurationURI() == null) && (amcuConfig.getUpdateURI() == null)
                && (amcuConfig.getAgentUpdateUrl() == null) && (amcuConfig.getTruckUpdateUrl() == null) && (amcuConfig.getIncentiveUpdateURI() == null)
        )
                ) {
            Util.displayErrorToast("No update available!", getApplicationContext());
        }

    }

    public void getRateChartDPN() {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        AuthenticationParameters authParams = new AuthenticationParameters(null, amcuConfig.getDeviceID(),
                amcuConfig.getDevicePassword());
        CommunicationService cs = null;
        try {
            cs = new CommunicationService(amcuConfig.getURLHeader() + amcuConfig.getServer(), authParams);
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
        } catch (SSLContextCreationException e) {
            Log.d(TAG, e.getMessage());
        }

        try {
            HttpResponse response = cs.doGet(DPN_API);
            ServerAPI.notifyLoggedIn(response);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IncompatibleProtocolException e) {
            e.printStackTrace();
        }
    }

    public void showUpdateToast(boolean isShowUpdate, String type) {
        if (isShowUpdate) {
            FarmerScannerActivity.isUpdateAvailable = true;
            Util.displayErrorToast(type + " is getting updated", getApplicationContext());
        }
    }

}
