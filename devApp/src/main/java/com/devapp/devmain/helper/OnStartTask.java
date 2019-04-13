package com.devapp.devmain.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.deviceinfo.GetDeviceStatusNew;
import com.devapp.devmain.devicemanager.ConfigurationManager;
import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.ConfigurationEntity;
import com.devapp.devmain.httptasks.ConfigurationPush;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.ArrayList;

/**
 * Created by u_pendra on 23/2/17.
 */

public class OnStartTask {

    DatabaseHandler databaseHandler;
    AmcuConfig amcuConfig;
    SessionManager session;
    private Context mContext;

    public OnStartTask(Context context) {
        this.mContext = context;
        this.databaseHandler = DatabaseHandler.getDatabaseInstance();
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mContext);
        //To initialize the SMS footers
        Util.getSMSFooter(mContext);
        Util.getFooterUrl(mContext);
    }

    public static ConfigurationEntity getNewConfigurationItems(ConfigurationEntity configurationEntity, Context context) {

        SharedPreferences pref = context.getSharedPreferences(ConfigurationConstants.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences sessionPref = context.getSharedPreferences(ConfigurationConstants.SESSION_PREF_NAME, Context.MODE_PRIVATE);

        configurationEntity.devicePwd = pref.getString(ConfigurationConstants.DEVICE_PWD, "");
        configurationEntity.setAmcuServer = pref.getString(AmcuConfig.KEY_SET_SERVER, AppConstants.GENRIC_SERVER);
        configurationEntity.whiteListURL = pref.getString(ConfigurationConstants.WHITE_LIST_URL, "NA");
        configurationEntity.deviceId = pref.getString(ConfigurationConstants.DEVICE_ID, "");
        configurationEntity.calibrationDate = String.valueOf(pref.getLong(ConfigurationConstants.CALIBRATION_DATE, 12));
        configurationEntity.createEdit = String.valueOf(pref.getBoolean(ConfigurationConstants.CREATE_EDIT, false));
        configurationEntity.allowFarmerEdit = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_ALLOW_FARMER_EDIT, true));
        configurationEntity.allowFarmerCreate = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_ALLOW_FARMER_CREATE, true));
        configurationEntity.farmIdDigit = String.valueOf(pref.getInt(ConfigurationConstants.FARM_ID_DIGIT, 4));
        configurationEntity.maParity = pref.getString(ConfigurationConstants.MA_PARITY, "PARITY_NONE");
        configurationEntity.mastopbits = pref.getString(ConfigurationConstants.MA_STOPBITS, "STOP_BITS_1");
        configurationEntity.maDatabits = pref.getString(ConfigurationConstants.MA_DATABITS, "DATA_BITS_8");
        configurationEntity.periodicDataSent = String.valueOf(pref.getInt(ConfigurationConstants.PERIODIC_DATA_SENT, 3));
        configurationEntity.startTimeDataSent = String.valueOf(pref.getInt(ConfigurationConstants.START_TIME_DATA_SENT, 5));
        configurationEntity.periodicDeviceDataSend = String.valueOf(3);

        configurationEntity.licenceJson = pref.getString(ConfigurationConstants.LICENSE_JSON, null);

        configurationEntity.allowOperatorToReportEditing = String.valueOf(pref.getBoolean(
                ConfigurationConstants.ALLOW_EDIT_REPORT, false));
        configurationEntity.numberOfEditableShift = String.valueOf(
                pref.getInt(ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT, 0));
        configurationEntity.updateRateMiddleSession = String.valueOf(pref.getBoolean(
                ConfigurationConstants.UPDATE_RATE_MIDDLE_SESSION, false
        ));
        configurationEntity.mCCorABC = String.valueOf(pref.getString(ConfigurationConstants.MCC_OR_ABC,
                UserType.AMCU));
        configurationEntity.tripCodeStartIndex = String.valueOf(pref.getInt(ConfigurationConstants.TRIP_CODE_START_INDEX,
                501));
        configurationEntity.startTankerIndex = String.valueOf(
                pref.getInt(ConfigurationConstants.KEY_START_TANKER_ID, 900));
        configurationEntity.maxTrip = String.valueOf(pref.getInt(ConfigurationConstants.MAX_TRIP, 20));
        configurationEntity.perDayTankerLimit = String.valueOf(
                pref.getInt(ConfigurationConstants.KEY_PER_TANKER_LIMIT, 1));
        configurationEntity.isTareOnStart = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_TARE_ON_START_COLLECTION
                , false));
        configurationEntity.milkType = (pref.getString(ConfigurationConstants.KEY_MILKTYPE
                , ""));
        configurationEntity.showUnsentAlert = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_SHOW_UNSENT_ALERT
                , false));
        configurationEntity.defalutMilkTypeBoth = pref.getString(ConfigurationConstants.KEY_DEFAULT_MILK_TYPE_BOTH
                , "COW");
        configurationEntity.ma1Parity = pref.getString(ConfigurationConstants.KEY_MA1_PARITY
                , "PARITY_NONE");
        configurationEntity.ma1stopbits = pref.getString(ConfigurationConstants.KEY_MA1_STOP_BITS
                , "DATA_BITS_8");
        configurationEntity.ma1Databits = pref.getString(ConfigurationConstants.KEY_MA1_DATA_BITS
                , "STOP_BITS_1");
        configurationEntity.ma2Parity = pref.getString(ConfigurationConstants.KEY_MA2_PARITY
                , "PARITY_NONE");
        configurationEntity.ma2stopbits = pref.getString(ConfigurationConstants.KEY_MA2_STOP_BITS
                , "DATA_BITS_8");
        configurationEntity.ma2Databits = pref.getString(ConfigurationConstants.KEY_MA2_DATA_BITS
                , "STOP_BITS_1");
        configurationEntity.isMandatoryRatechart = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_MANDATORY_RATE_CHART
                , true));
        configurationEntity.keyMinFatRejectCow = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MIN_FAT_REJECT_COW
                , 0.0f));
        configurationEntity.keyMinSnfRejectCow = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MIN_SNF_REJECT_COW
                , 0.0f));
        configurationEntity.keyMaxFatRejectCow = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_FAT_REJECT_COW
                , 14.0f));
        configurationEntity.keyMaxSnfRejectCow = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_SNF_REJECT_COW
                , 14.0f));
        configurationEntity.keyMaxLactoseRejectCow = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_COW
                , 6.0f));
        configurationEntity.enablehpPrinter = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_PRINT_ENABLE_A4
                , false));
        configurationEntity.sendEmaililConf = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_SEND_EMAIL_CONFIGUREIDS
                , false));
        configurationEntity.rateExcelImport = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_RATE_CHART_EXCEL_IMPORT
                , false));
        configurationEntity.keyMinFatRejectMix = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MIN_FAT_REJECT_MIX
                , 0.0f));
        configurationEntity.keyMinSnfRejectMix = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MIN_SNF_REJECT_MIX
                , 0.0f));
        configurationEntity.keyMaxFatRejectMix = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_FAT_REJECT_MIX
                , 14.0f));
        configurationEntity.keyMaxSnfRejectMix = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_SNF_REJECT_MIX
                , 14.0f));
        configurationEntity.keyMaxLactoseRejectMix = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_MIX
                , 6.0f));
        configurationEntity.keyMinFatRejectBuff = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MIN_FAT_REJECT_BUFF
                , 0.0f));
        configurationEntity.keyMinSnfRejectBuff = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MIN_SNF_REJECT_BUFF
                , 0.0f));
        configurationEntity.keyMaxFatRejectBuff = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_FAT_REJECT_BUFF
                , 14.0f));
        configurationEntity.keyMaxSnfRejectBuff = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_SNF_REJECT_BUFF
                , 14.0f));
        configurationEntity.keyMaxLactoseRejectBuff = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_BUFF
                , 6.0f));

        configurationEntity.maMilkType1 = pref.getString(ConfigurationConstants.KEY_MA1_MILKTYPE
                , "NONE");
        configurationEntity.maMilkType2 = pref.getString(ConfigurationConstants.KEY_MA2_MILKTYPE
                , "NONE");
        configurationEntity.ma1Name = pref.getString(ConfigurationConstants.KEY_MA1_NAME
                , "LACTOSCAN");
        configurationEntity.ma1Baudrate = String.valueOf(pref.getInt(ConfigurationConstants.KEY_MA1_BAUDRATE
                , 9600));
        configurationEntity.ma2Name = pref.getString(ConfigurationConstants.KEY_MA2_NAME
                , "LACTOSCAN");
        configurationEntity.ma2Baudrate = String.valueOf(pref.getInt(ConfigurationConstants.KEY_MA2_BAUDRATE
                , 9600));
        configurationEntity.smartCCFeature = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_SMARTCC_FEATURE
                , false));
        configurationEntity.keyAllowFormerIncrement = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_ALLOW_FAMER_INCREMENT
                , false));
        configurationEntity.lactoscanPrams = String.valueOf(pref.getBoolean(ConfigurationConstants.LACTOSCAN_PRAMS
                , false));
        configurationEntity.sampleAsCollection = String.valueOf(pref.getBoolean(ConfigurationConstants.SAMPLE_AS_COLLECTION
                , false));
        configurationEntity.mergedHMBEnable = String.valueOf(pref.getBoolean(ConfigurationConstants.MERGED_HMB_ENABLE
                , false));
        configurationEntity.minValidWeightForCollection = String.valueOf(pref.getFloat(ConfigurationConstants.KEY_MIN_VALID_WEIGHT
                , 0.100f));
        configurationEntity.deleteCollRecordShift = String.valueOf(pref.getInt(ConfigurationConstants.KEY_DELETE_COLL_SHIFT_RECORD
                , 120));
        configurationEntity.deleteFilesAfterDay = String.valueOf(pref.getInt(ConfigurationConstants.KEY_DELETE_FILES_DAY
                , 30));
        configurationEntity.allowAgentSplit =
                String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_ALLOW_AGENT_SPLIT
                        , false));
        configurationEntity.allowEditCollection =
                String.valueOf(pref.getBoolean(ConfigurationConstants.ALLOW_EDIT_COLLECTION
                        , false));

        configurationEntity.allowNameInReport =
                String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_ALLOW_DISPLAY_NAME_IN_REPORT
                        , false));
        configurationEntity.enableLactoseBasedRejection =
                String.valueOf(pref.getBoolean(ConfigurationConstants.ENABLE_LACTOSE_BASED_REJECTION
                        , false));
        configurationEntity.enableDynamicRateChart =
                String.valueOf(pref.getBoolean(ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART
                        , false));
        configurationEntity.displayDairyname =
                pref.getString(ConfigurationConstants.KEY_DISPLAY_DAIRY_NAME
                        , "");
        configurationEntity.rduPassword = pref.getString(ConfigurationConstants.KEY_RDU_PASSWORD, "1111");
        configurationEntity.hotspotSsid = pref.getString(ConfigurationConstants.KEY_HOTSPOT_SSID, SmartCCConstants.ssidList[0]);
        configurationEntity.enableHotspot = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_ENABLE_HOTSPOT, false));

        configurationEntity.wsIgnoreThreshold =
                String.valueOf(pref.getInt(ConfigurationConstants.KEY_WS_IGNORE_THRESHOLD
                        , 50));
        configurationEntity.wsLitreCommand =
                String.valueOf(pref.getString(ConfigurationConstants.KEY_WS_LITRE_COMMAND
                        , "L"));
        configurationEntity.wsKgCommand =
                pref.getString(ConfigurationConstants.KEY_WS_KG_COMMAND
                        , "K");
        configurationEntity.allowApkUpgradeFromFileSystem =
                String.valueOf(pref.getBoolean(ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM
                        , false));


        // Util.displayErrorToast(configurationEntity.wsIgnoreThreshold,context);

//        configurationEntity.incentiveRateChartName = pref.getString(ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME , "");
//        configurationEntity.allowProteinValue = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_ALLOW_PROTEIN_VALUE , false));
//        configurationEntity.allowTwoDecimal = String.valueOf(pref.getBoolean(ConfigurationConstants.KEY_ALLOW_TWO_DECIMAL , false));

        return configurationEntity;
    }

    public static ArrayList<ConfigurationElement> getAllTheMissingConfigItems(Context context) {

        ConfigurationEntity configurationEntity = getNewConfigurationItems(new ConfigurationEntity(), context);

        ArrayList<ConfigurationElement> allConfigPushList = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager(context);

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.DEVICE_PWD, configurationEntity.devicePwd
                , ""));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.DEVICE_ID, configurationEntity.deviceId
                , null));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.SET_AMCU_SERVER, configurationEntity.setAmcuServer
                , null));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.WHITE_LIST_URL, configurationEntity.whiteListURL
                , "NA"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.CREATE_EDIT, configurationEntity.createEdit
                , String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.CALIBRATION_DATE, configurationEntity.calibrationDate
                , String.valueOf(12)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.FARM_ID_DIGIT, configurationEntity.farmIdDigit
                , String.valueOf(4)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.MA_PARITY, configurationEntity.maParity
                , "PARITY_NONE"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.MA_STOPBITS, configurationEntity.mastopbits
                , "STOP_BITS_1"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.MA_DATABITS, configurationEntity.maDatabits
                , "DATA_BITS_8"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.PERIODIC_DATA_SENT, configurationEntity.periodicDataSent
                , String.valueOf(3)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.START_TIME_DATA_SENT, configurationEntity.startTimeDataSent
                , String.valueOf(5)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.PERIODIC_DEVICE_DATA_SEND, configurationEntity.periodicDeviceDataSend,
                String.valueOf(3)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.ALLOW_EDIT_REPORT,
                configurationEntity.allowOperatorToReportEditing, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT,
                configurationEntity.numberOfEditableShift, String.valueOf(0)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.UPDATE_RATE_MIDDLE_SESSION,
                configurationEntity.updateRateMiddleSession, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.MCC_OR_ABC,
                configurationEntity.mCCorABC, UserType.AMCU));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.TRIP_CODE_START_INDEX,
                configurationEntity.tripCodeStartIndex, String.valueOf(501)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.MAX_TRIP,
                configurationEntity.maxTrip, String.valueOf(20)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.KEY_START_TANKER_ID,
                configurationEntity.startTankerIndex, String.valueOf(900)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.KEY_PER_TANKER_LIMIT,
                configurationEntity.perDayTankerLimit, String.valueOf(1)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(ConfigurationConstants.KEY_TARE_ON_START_COLLECTION,
                configurationEntity.isTareOnStart, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.LICENSE_JSON,
                configurationEntity.licenceJson, null));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.MILK_TYPE,
                configurationEntity.milkType, null));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_SHOW_UNSENT_ALERT,
                configurationEntity.showUnsentAlert, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_DEFAULT_MILK_TYPE_BOTH,
                configurationEntity.defalutMilkTypeBoth, "COW"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA1_PARITY,
                configurationEntity.ma1Parity, "PARITY_NONE"));


        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA1_STOP_BITS,
                configurationEntity.ma1stopbits, "STOP_BITS_1"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA1_DATA_BITS,
                configurationEntity.ma1Databits, "DATA_BITS_8"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA2_PARITY,
                configurationEntity.ma2Parity, "PARITY_NONE"));


        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA2_STOP_BITS,
                configurationEntity.ma2stopbits, "STOP_BITS_1"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA2_DATA_BITS,
                configurationEntity.ma2Databits, "DATA_BITS_8"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MANDATORY_RATE_CHART,
                configurationEntity.isMandatoryRatechart, String.valueOf(true)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MIN_FAT_REJECT_COW,
                configurationEntity.keyMinFatRejectCow, String.valueOf(0.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MIN_SNF_REJECT_COW,
                configurationEntity.keyMinSnfRejectCow, String.valueOf(0.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_FAT_REJECT_COW,
                configurationEntity.keyMaxFatRejectCow, String.valueOf(14.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_SNF_REJECT_COW,
                configurationEntity.keyMaxSnfRejectCow, String.valueOf(14.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_COW,
                configurationEntity.keyMaxLactoseRejectCow, String.valueOf(6.0f)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_PRINT_ENABLE_A4,
                configurationEntity.enablehpPrinter, String.valueOf(false)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_SEND_EMAIL_CONFIGUREIDS,
                configurationEntity.sendEmaililConf, String.valueOf(false)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_RATE_CHART_EXCEL_IMPORT,
                configurationEntity.rateExcelImport, String.valueOf(false)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MIN_FAT_REJECT_BUFF,
                configurationEntity.keyMinFatRejectBuff, String.valueOf(0.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MIN_SNF_REJECT_BUFF,
                configurationEntity.keyMinSnfRejectBuff, String.valueOf(0.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_FAT_REJECT_BUFF,
                configurationEntity.keyMaxFatRejectBuff, String.valueOf(14.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_SNF_REJECT_BUFF,
                configurationEntity.keyMaxSnfRejectBuff, String.valueOf(14.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_BUFF,
                configurationEntity.keyMaxLactoseRejectBuff, String.valueOf(6.0f)));


        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MIN_FAT_REJECT_MIX,
                configurationEntity.keyMinFatRejectMix, String.valueOf(0.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MIN_SNF_REJECT_MIX,
                configurationEntity.keyMinSnfRejectMix, String.valueOf(0.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_FAT_REJECT_MIX,
                configurationEntity.keyMaxFatRejectMix, String.valueOf(14.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_SNF_REJECT_MIX,
                configurationEntity.keyMaxSnfRejectMix, String.valueOf(14.0f)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_MIX,
                configurationEntity.keyMaxLactoseRejectMix, String.valueOf(6.0f)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA1_MILKTYPE,
                configurationEntity.maMilkType1, "NONE"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA2_MILKTYPE,
                configurationEntity.maMilkType2, "NONE"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA1_BAUDRATE,
                configurationEntity.ma1Baudrate, "9600"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA1_NAME,
                configurationEntity.ma1Name, "LACTOSCAN"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA2_BAUDRATE,
                configurationEntity.ma2Baudrate, "9600"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MA2_NAME,
                configurationEntity.ma2Name, "LACTOSCAN"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_SMARTCC_FEATURE,
                configurationEntity.smartCCFeature, String.valueOf(false)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_FAMER_INCREMENT,
                configurationEntity.keyAllowFormerIncrement, String.valueOf(false)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.LACTOSCAN_PRAMS,
                configurationEntity.lactoscanPrams, String.valueOf(false)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.SAMPLE_AS_COLLECTION,
                configurationEntity.sampleAsCollection, String.valueOf(false)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.MERGED_HMB_ENABLE,
                configurationEntity.mergedHMBEnable, String.valueOf(false)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MIN_VALID_WEIGHT,
                configurationEntity.minValidWeightForCollection, String.valueOf(0.100f)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_DELETE_COLL_SHIFT_RECORD,
                configurationEntity.deleteCollRecordShift, String.valueOf(120)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_DELETE_FILES_DAY,
                configurationEntity.deleteFilesAfterDay, String.valueOf(30)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MORNING_START,
                configurationEntity.morningSessionStartTime, "00:00"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_EVENING_START,
                configurationEntity.eveningSessionStartTime, "15:00"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_MORNING_END_TIME,
                configurationEntity.morningSessionEndTime, "14:00"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_EVENING_END_TIME,
                configurationEntity.eveningSessionEndTime, "02:00"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_AGENT_SPLIT,
                configurationEntity.allowAgentSplit, "false"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.ALLOW_EDIT_COLLECTION,
                configurationEntity.allowEditCollection, "false"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME,
                configurationEntity.incentiveRateChartName, null));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_TWO_DECIMAL,
                configurationEntity.allowTwoDecimal, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_PROTEIN_VALUE,
                configurationEntity.allowProteinValue, String.valueOf(false)));

        //FAT,SNF CLR parameters

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_DISPLAY_FAT_CONFIGURATION,
                configurationEntity.displayFATConfiguration, "1"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_DISPLAY_SNF_CONFIGURATION,
                configurationEntity.displaySNFConfiguration, "1"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_DISPLAY_CLR_CONFIGURATION,
                configurationEntity.displayCLRConfiguration, "1"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_DISPLAY_PROTEIN_CONFIGURATION,
                configurationEntity.displayProteinConfiguration, "2"));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_RATE_FAT_CONFIGURATION,
                configurationEntity.rateFATConfiguration, "1"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_RATE_SNF_CONFIGURATION,
                configurationEntity.rateSNFConfiguration, "1"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_RATE_PROTEIN_CONFIGURATION,
                configurationEntity.rateProteinConfiguration, "2"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_RATE_CLR_CONFIGURATION,
                configurationEntity.rateCLRConfiguration, "1"));


        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ROUND_FAT_CONFIGURATION,
                configurationEntity.roundFATConfiguration, AppConstants.ROUND_HALF_UP));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ROUND_SNF_CONFIGURATION,
                configurationEntity.roundSNFConfiguration, AppConstants.ROUND_HALF_UP));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ROUND_CLR_CONFIGURATION,
                configurationEntity.roundCLRConfiguration, AppConstants.ROUND_HALF_UP));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ROUND_PROTEIN_CONFIGURATION,
                configurationEntity.roundProteinConfiguration, AppConstants.ROUND_HALF_UP));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_RDU_PASSWORD,
                configurationEntity.rduPassword, "1111"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_HOTSPOT_SSID,
                configurationEntity.hotspotSsid, SmartCCConstants.ssidList[0]));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ENABLE_HOTSPOT,
                configurationEntity.enableHotspot, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.ENABLE_LACTOSE_BASED_REJECTION,
                configurationEntity.enableLactoseBasedRejection, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART,
                configurationEntity.enableDynamicRateChart, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_DISPLAY_RATE,
                configurationEntity.allowDisplayRate, String.valueOf(true)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_KG_FAT,
                configurationEntity.kgFat, "0.0"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_KG_SNF,
                configurationEntity.kgSnf, "0.0"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_KG_CLR,
                configurationEntity.kgClr, "0.0"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_FARMER_EDIT,
                configurationEntity.allowFarmerEdit, String.valueOf(true)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_FARMER_CREATE,
                configurationEntity.allowFarmerCreate, String.valueOf(true)));

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_WS_IGNORE_THRESHOLD,
                configurationEntity.wsIgnoreThreshold, "100"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_WS_KG_COMMAND,
                configurationEntity.wsKgCommand, "K"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_WS_LITRE_COMMAND,
                configurationEntity.wsLitreCommand, "L"));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_DISPLAY_NAME_IN_REPORT,
                configurationEntity.allowNameInReport, String.valueOf(false)));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.KEY_DISPLAY_DAIRY_NAME,
                configurationEntity.displayDairyname, ""));
        allConfigPushList.add(configurationManager.getConfigurationPushList(
                ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM,
                configurationEntity.allowApkUpgradeFromFileSystem, String.valueOf(false)));

        //  Util.displayErrorToast(configurationEntity.wsIgnoreThreshold,context);
        return allConfigPushList;

    }

    public static ArrayList<ConfigurationElement> getAllTheMissingConfigTest98(Context context) {

        ConfigurationEntity configurationEntity = getNewConfigurationItems(new ConfigurationEntity(), context);

        ArrayList<ConfigurationElement> allConfigPushList = new ArrayList<>();
        ConfigurationManager configurationManager = new ConfigurationManager(context);

        allConfigPushList.add(configurationManager.getConfigurationPushList(
                "test189", "test189"
                , "test189"));

        return allConfigPushList;

    }

    public void doConfigurationTask() {
        databaseHandler.createTheConfigTableIfNotThere();
        int count = databaseHandler.isConfigurationTableContainAnyValue();
        ConfigurationManager configurationManager = new ConfigurationManager(mContext);

        if (count < 100) {
            configurationManager.addConfigurationPushList();
        }

        ConfigurationElement cpl = configurationManager.getConfigurationPushList(
                ConfigurationConstants.rduQuantityRoundOff, String.valueOf(amcuConfig.getRDUWeightRoundOff())
                , null);
        try {
            databaseHandler.insertOrReplaceConfigurationEntity(cpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        cpl = configurationManager.getConfigurationPushList(
                ConfigurationConstants.snfOrClrForTanker, String.valueOf(amcuConfig.getSNFOrCLRFromTanker())
                , null);
        try {
            databaseHandler.insertOrReplaceConfigurationEntity(cpl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        cpl = configurationManager.getConfigurationPushList(
                ConfigurationConstants.weightRecordLength, String.valueOf(
                        amcuConfig.getWeightRecordLenght())
                , null);
        try {
            databaseHandler.insertOrReplaceConfigurationEntity(cpl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConfigurationElement cplCloudSupport = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_IS_CLOUD_SUPPORT, String.valueOf(amcuConfig.getKeyCloudSupport())
                , null);
        ConfigurationElement cplUnsentLimit = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_UNSENT_ALERT_LIMIT, String.valueOf(amcuConfig.getUnsentAlertLimit())
                , null);
        ConfigurationElement cplShowUnsentAllert = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_SHOW_UNSENT_ALERT, String.valueOf(amcuConfig.getShowUnsentAlert())
                , null);
        ConfigurationElement cplPeriodicSent = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_PERIODIC_DATA_SENT, String.valueOf(amcuConfig.getKeyPeriodicDataSent())
                , null);
        ConfigurationElement cplStartDelay = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_DATA_SENT_START_TIME, String.valueOf(amcuConfig.getKeyDataSentStartTime())
                , null);

        ConfigurationElement cplDefaultForBoth = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_DEFAULT_MILK_TYPE_BOTH, String.valueOf(amcuConfig.getDefaultMilkTypeForBoth())
                , null);

        ConfigurationElement cplMinValidLimit = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_MIN_VALID_WEIGHT, String.valueOf(amcuConfig.getKeyMinValidWeight())
                , null);

        ConfigurationElement cplnumberOfShiftForDelete = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_DELETE_COLL_SHIFT_RECORD, String.valueOf(amcuConfig.getDeleteCollRecordAfterShift())
                , null);

        ConfigurationElement cplNumberOfDayToDelete = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_DELETE_FILES_DAY, String.valueOf(amcuConfig.getDeleteFilesAfterDays())
                , null);

        ConfigurationElement cplEscapeEnable = configurationManager.getConfigurationPushList(
                AmcuConfig.KEY_ESCAPE_ENABLE_COLLECTION, String.valueOf(amcuConfig.getKeyEscapeEnableCollection())
                , null);

        try {
            databaseHandler.insertOrReplaceConfigurationEntity(cplCloudSupport);
            databaseHandler.insertOrReplaceConfigurationEntity(cplUnsentLimit);
            databaseHandler.insertOrReplaceConfigurationEntity(cplShowUnsentAllert);
            databaseHandler.insertOrReplaceConfigurationEntity(cplPeriodicSent);
            databaseHandler.insertOrReplaceConfigurationEntity(cplStartDelay);
            databaseHandler.insertOrReplaceConfigurationEntity(cplDefaultForBoth);
            databaseHandler.insertOrReplaceConfigurationEntity(cplMinValidLimit);
            databaseHandler.insertOrReplaceConfigurationEntity(cplnumberOfShiftForDelete);
            databaseHandler.insertOrReplaceConfigurationEntity(cplNumberOfDayToDelete);
            databaseHandler.insertOrReplaceConfigurationEntity(cplEscapeEnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mContext.startService(new Intent(mContext.getApplicationContext(), ConfigurationPush.class));
    }

    /**
     * Disable the endshift register once task is done
     */

    public void disableRegister() {
//        if(saveSession.getEndShiftSuccess()) {
//            ComponentName receiver = new ComponentName(mContext, EndShiftReceiver.class);
//            PackageManager pm = mContext.getPackageManager();
//            pm.setComponentEnabledSetting(receiver,
//                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
//                    PackageManager.DONT_KILL_APP);
//            Util.displayErrorToast("Unresigter receiver",mContext);
//        }
    }

    public void startGetDeviceStatusService() {
        Thread serviceThread = new Thread() {
            public void run() {
                mContext.startService(new Intent(mContext.getApplicationContext(), GetDeviceStatusNew.class));

            }
        };
        serviceThread.start();
    }


}
