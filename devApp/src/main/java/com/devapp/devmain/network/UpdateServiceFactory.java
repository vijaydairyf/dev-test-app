package com.devapp.devmain.network;

import android.content.Context;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.entitymanager.AgentManager;
import com.devapp.devmain.entitymanager.CollectionCenterManager;
import com.devapp.devmain.entitymanager.ConfigEntityManager;
import com.devapp.devmain.entitymanager.FarmerManager;
import com.devapp.devmain.entitymanager.IncentiveRateChartManager;
import com.devapp.devmain.entitymanager.RateChartManager;
import com.devapp.devmain.entitymanager.TruckManager;

/**
 * Created by x on 28/1/18.
 */

public class UpdateServiceFactory {

    private final Context mContext;

    public UpdateServiceFactory(Context context) {
        mContext = context;
    }

    public UpdateService getUpdateService(String configType) {
        UpdateService updateService = null;
        switch (configType) {
            case AppConstants.ConfigurationTypes.AGENT: {
                updateService = new UpdateService(mContext, new AgentManager(mContext));
                break;
            }

            case AppConstants.ConfigurationTypes.COLLECTION_CENTER_LIST: {
                updateService = new UpdateService(mContext, new CollectionCenterManager(mContext));
                break;
            }
            case AppConstants.ConfigurationTypes.CONFIGURATION: {
                updateService = new UpdateService(mContext, new ConfigEntityManager(mContext));
                break;
            }
            case AppConstants.ConfigurationTypes.FARMER: {
                updateService = new UpdateService(mContext, new FarmerManager(mContext));
                break;
            }
            case AppConstants.ConfigurationTypes.INCENTIVE_RATE_CHART: {
                updateService = new UpdateService(mContext, new IncentiveRateChartManager(mContext));
                break;
            }
            case AppConstants.ConfigurationTypes.RATE_CHART: {
                updateService = new UpdateService(mContext, new RateChartManager(mContext));
                break;
            }
            case AppConstants.ConfigurationTypes.TRUCK: {
                updateService = new UpdateService(mContext, new TruckManager(mContext));
                break;
            }
        }
        return updateService;
    }
}
