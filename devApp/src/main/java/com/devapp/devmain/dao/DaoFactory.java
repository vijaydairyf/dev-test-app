package com.devapp.devmain.dao;

import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;

/**
 * Created by u_pendra on 3/2/18.
 */

public class DaoFactory {


    public static Dao getDao(String type) {
        return getDao(type, true);
    }


    //TODO maintain a hashmap of daotype and instance to make dao as singletons
    public static Dao getDao(String type, boolean shouldChain) {


        Dao primaryDao = null;
        Dao secondaryDao = null;
        try {
            if (type.equalsIgnoreCase(CollectionConstants.REPORT_TYPE_COLLECTION)) {
                primaryDao =
                        new CollectionRecordDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new CollectionRecordDao(DatabaseHandler.getSecondaryDatabase());

            } else if (type.equalsIgnoreCase(CollectionConstants.REPORT_TYPE_SALES)) {
                primaryDao =
                        new SalesRecordDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new SalesRecordDao(DatabaseHandler.getSecondaryDatabase());


            } else if (type.equalsIgnoreCase(CollectionConstants.REPORT_TYPE_EDITED_ADDITIONAL)) {
                primaryDao =
                        new AdditionalParamsDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new AdditionalParamsDao(DatabaseHandler.getSecondaryDatabase());


            } else if (type.equalsIgnoreCase(CollectionConstants.REPORT_TYPE_TANKER)) {
                primaryDao =
                        new TankerCollectionDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new TankerCollectionDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.REPORT_TYPE_DISPATCH)) {
                primaryDao =
                        new DispatchRecordDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new DispatchRecordDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.REPORT_TYPE_EDITED)) {
                primaryDao =
                        new EditRecordDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new EditRecordDao(DatabaseHandler.getSecondaryDatabase());

            } else if (type.equalsIgnoreCase(CollectionConstants.REPORT_TYPE_AGENT_SPLIT)) {
                primaryDao =
                        new AgentSplitDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new AgentSplitDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.COLLECTION_RECORD_STATUS)) {
                primaryDao =
                        new CollectionStatusDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new CollectionStatusDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.FARMER)) {
                primaryDao =
                        new FarmerDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new FarmerDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.COLLECTION_CENTER)) {
                primaryDao = new CollectionCenterDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new CollectionCenterDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.TRUCK)) {
                primaryDao = new TruckDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new TruckDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.AGENT)) {
                primaryDao = new AgentDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new AgentDao((DatabaseHandler.getSecondaryDatabase()));
            } else if (type.equalsIgnoreCase(CollectionConstants.CONFIGURATION)) {
                primaryDao = new ConfigurationDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new ConfigurationDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.ATTRIBUTEVALUE)) {
                primaryDao = new ConfigurationDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new ConfigurationDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.EDIT_RECORD_STATUS)) {
                primaryDao = new EditRecordStatusDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new EditRecordStatusDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.SALES_RECORD_STATUS)) {
                primaryDao = new SalesRecordStatusDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new SalesRecordStatusDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.RATECHART_NAME)) {
                primaryDao = new RateChartNameDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new RateChartNameDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.RATES)) {
                primaryDao = new RateDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new RateDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.INCENTIVE_RATES)) {
                primaryDao = new IncentiveRateDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new IncentiveRateDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.LOGS)) {
                primaryDao = new LogEntityDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new LogEntityDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.SHIFT_STATUS)) {
                primaryDao = new ShiftStatusDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new ShiftStatusDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.REPORT_TYPE_DISPATCH)) {
                primaryDao = new DispatchRecordDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new DispatchRecordDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.CHILLING_CENTER)) {
                primaryDao = new ChillingCenterDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new ChillingCenterDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.USER)) {
                primaryDao = new UserDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new UserDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.Sample)) {
                primaryDao = new SampleDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new SampleDao(DatabaseHandler.getSecondaryDatabase());
            } else if (type.equalsIgnoreCase(CollectionConstants.LICENSE)) {
                primaryDao = new LicenseDao(DatabaseHandler.getPrimaryDatabase());
                secondaryDao = new LicenseDao(DatabaseHandler.getSecondaryDatabase());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (shouldChain) {
            if (primaryDao != null && secondaryDao != null)
                primaryDao.setSecondaryDao(secondaryDao);
        }

        return primaryDao;
    }
}
