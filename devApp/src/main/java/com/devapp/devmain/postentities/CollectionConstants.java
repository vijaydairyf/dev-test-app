package com.devapp.devmain.postentities;

/**
 * Created by u_pendra on 6/1/18.
 */

public interface CollectionConstants {


    String REPORT_TYPE_COLLECTION = "Collection";
    String REPORT_TYPE_SALES = "Sales";
    String REPORT_TYPE_CHILLING = "AggregateCollection";
    String REPORT_TYPE_AGENT_SPLIT = "agentSplit";
    String REPORT_TYPE_TANKER = "Tanker";
    String REPORT_TYPE_DISPATCH = "Dispatch";
    String REPORT_TYPE_ROUTE = "Route";
    String REPORT_TYPE_SAMPLE = "Sample";
    String REPORT_TYPE_EDITED = "update";
    String REPORT_TYPE_EDITED_ADDITIONAL = "Edited";
    String COLLECTION_RECORD_STATUS = "CollectionRecordStatus";
    String SALES_RECORD_STATUS = "SalesRecordStatus";
    String EDIT_RECORD_STATUS = "EditRecordStatus";
    String FARMER = "Farmer";
    String COLLECTION_CENTER = "CollectionCenter";
    String TRUCK = "Truck";
    String AGENT = "Agent";
    String CONFIGURATION = "Configuration";
    String ATTRIBUTEVALUE = "AttributeValue";
    String CHILLING_CENTER = "ChillingCenter";
    String USER = "User";
    String Sample = "Sample";
    String LICENSE = "License";


    int SENT = 1;
    int UNSENT = 0;
    int SMS_NOT_ENABLE = 2;
    int SMS_INVALID_NUMBER = 3;
    int SMS_GENERIC_FAILURE = 4;

    String FARMER_MILK_COLLECTION = "farmerMilkCollections";
    String TANKER_MILK_COLLECTION = "tankerMilkCollections";
    String SALES_COLLECTION = "salesCollections";
    String DISPATCH_COLLECTION = "dispatchCollections";
    String FARMER_SPLIT_COLLECTION = "farmerSplitCollections";
    String EXTRA_PARAM_DETAILS = "extraParametersDetails";
    String MCC_MILK_COLLECTION = "collectionCenterMilkCollections";
    String SAMPLE_RECORDS = "testRecords";
    String EDITED_FARMER_COLLECTION = "updateFarmerMilkCollections";
    String EDITED_MCC_COLLECTION = "updateCollectionCenterMilkCollections";
    String INCOMPLETE_FARMER_RECORDS = "incompleteFarmerCollections";
    String INCOMPLETE_MCC_RECORDS = "incompleteCenterCollections";
    String ROUTE_COLLECTION = "routeCollections";

    String BASIC_PROFILE = "basicProfile";
    String RATECHART_NAME = "rateChartName";
    String RATES = "rates";
    String INCENTIVE_RATES = "incentiveRates";
    String LOGS = "logs";
    String SHIFT_STATUS = "shiftStatus";

    int READ_WRITE = 0;
    int WRITE_ONLY = 2;
    int READ_ONLY = 1;

    String FARMER_SYNC = "farmersync";
    String RATE_SYNC = "rateSync";
    String CONFIG_SYNC = "configSync";
    String MCC_SYNC = "mccSync";
    String SYNC_ALL = "syncAll";


}
