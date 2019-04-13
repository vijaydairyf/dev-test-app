package com.devapp.devmain.server;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.MAParam;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.config.AppConfig;
import com.devapp.devmain.util.config.DefaultConfigurationHandler;

import java.util.Properties;

public class AmcuConfig {


    public static final String KEY_EMPTY_OR_FILLED_CAN = "filloremptyCan";
    // Sharedpref file name
    public static final String PREF_NAME = "permaPref";
    // All Shared Preferences Keys
    public static final String KEY_RATECHART_NAME = "rateChartName";
    public static final String KEY_CLEANING_ALERT = "cleaningAlert";
    public static final String KEY_CLEANING_DATA = "cleaningData";
    // SocId address
    public static final String KEY_SOCIETYID = "sesocityId";
    public static final String KEY_VALID_TO = "rateValidTo";
    public static final String KEY_VALID_FROM = "rateValidFrom";
    public static final String KEY_SETRATECHART_COW = "setRateCow";
    public static final String KEY_SETRATECHART_BUFFALO = "setRateBuffalo";
    public static final String KEY_SETRATECHART_MIXED = "setRateMixed";
    public static final String KEY_MILKTYPE = "milkType";
    public static final String KEY_POST_DATA = "postData";
    public static final String KEY_MILKANALYSER = "milkAnalyser";
    public static final String KEY_MA_BAUDRATE = "milkbaudrate";
    public static final String KEY_RDU = "rdu";
    public static final String KEY_RDU_BAUDRATE = "rduBaudrate";
    public static final String KEY_WEIGHING_BAUDRATE = "weighingBaud";
    public static final String KEY_WEIGHING_NAME = "weighingNAME";
    public static final String KEY_PRINTER = "printer";
    public static final String KEY_PRINTER_BAUDRATE = "printerBaudrate";
    public static final String KEY_DEVICE_ID = "deviceId";
    public static final String KEY_DEVICE_Password = "devicePwd";
    public static final String KEY_DEVICE_UPDATE_URI = "updateUri";
    public static final String KEY_SET_CALIBRATION_DATE = "calibrationDate";
    public static final String KEY_SET_SERVER = "server";
    public static final String KEY_SET_SENDING_MSG = "sengingMsg";
    public static final String KEY_SET_CHANGE_SNF = "changeSnf";
    public static final String KEY_SET_CHANGE_FAT = "changeFat";
    public static final String KEY_SET_ENDED_SHIFT = "endedShift";
    public static final String KEY_SET_LAST_SHIFT = "lastShift";
    public static final String KEY_SET_CURRENT_SHIFT = "startShift";
    public static final String KEY_SET_LAST_SHIFT_DATE = "lastShiftDate";
    public static final String KEY_SET_CURRENT_SHIFT_DATE = "startShiftDate";
    public static final String KEY_SET_PREVIOUS_SHIFT_DATE = "prevShiftDate";
    public static final String KEY_SET_COLLECTION_ENDSHIFT = "collEndShift";
    public static final String KEY_WEIGHING_ROUNDOFF = "weighRoundOff";
    public static final String KEY_RATE_ROUNDOFF = "rateRoundOff";
    public static final String KEY_AMOUNT_ROUNDOFF = "amountRoundOff";
    public static final String KEY_AMOUNT_CHECKED = "isAmountChecked";
    public static final String KEY_WEIGHING_ROUNDCHECK = "weighRoundOffchk";
    public static final String KEY_RATE_ROUNDOFFCHECK = "rateRoundOffchk";
    public static final String KEY_AMOUNT_ROUNDOFFCHECK = "amountRoundOffchk";
    public static final String KEY_MILKANALYSER_PREVDATA = "prevMilkData";
    public static final String KEY_USBHUB = "usbHUB";
    public static final String KEY_MILKANALYZERID = "milkAnalyserID";
    public static final String KEY_PRINTERID = "printerID";
    public static final String KEY_WEIGHINGID = "weighingID";
    public static final String KEY_RDUID = "rduID";
    public static final String KEY_SNF_CONSTANT = "snfCons";
    public static final String KEY_FAT_CONSTANT = "fatCons";
    public static final String KEY_CLR_CONSTANT = "clrCons";
    public static final String KEY_RATECHART_CREATEOREDIT = "createEdit";
    public static final String KEY_ENCRYPTED_FILE = "encryptedFile";
    public static final String KEY_REPORTNOTE_FILE = "reportNoteFile";
    public static final String KEY_DOWNLOAD_ID = "downloadId";
    public static final String KEY_SECOND_MILKANALYSER = "secMilkAnalyser";
    public static final String KEY_SECOND_MILKANALYSER_BAUDRATE = "secMilkBaudrate";
    public static final String KEY_LOGIN_FOR = "logInfor";
    public static final String KEY_MA1_MILKTYPE = "maMilkType1";
    public static final String KEY_MA2_MILKTYPE = "maMilkType2";
    public static final String KEY_SET_DOWNLOADED_APK_PATH = "downloadedAPKPath";
    public static final String KEY_SEND_EMAIL_CONFIGUREIDS = "sendEmaililConf";
    public static final String KEY_RATE_CHART_EXCEL_IMPORT = "rateExcelImport";
    public static final String KEY_SHUTDOWN_DELAY = "shutDownDelay";
    public static final String KEY_BONUS_START_DATE = "bonusStartDate";
    public static final String KEY_BONUS_END_DATE = "bonusEndDate";
    public static final String KEY_BONUS_RATE = "bonusRate";
    public static final String KEY_BONUS_ENABLE = "bonusEnable";
    public static final String KEY_RATECHART_ACCESS_OPERATOR = "rateAccess";
    public static final String KEY_CHECK_MA_MANUAL = "maManual";
    public static final String KEY_CHECK_WS_MANUAL = "wsManual";
    public static final String KEY_CHECK_ALLOW_SMS = "allowSMS";
    public static final String KEY_CHECK_ALLOW_SIMLOCK = "allowSIMLock";
    public static final String KEY_SIM_UNLOCK_PASSWORD = "simUnlockPassword";
    public static final String KEY_CURRENT_SIM_ID = "currentSimId";
    public static final String KEY_TEMP_SIM_PIN = "tempSimId";
    public static final String KEY_SET_BUILD = "projectBuild";
    public static final String KEY_SET_UNSENT_SEQUENCE_NUMBER = "unsentSequenceNumber";
    public static final String KEY_GET_CONFIG_UPDATE_URI = "configUpdate";
    public static final String KEY_GET_FARMER_UPDATE_URI = "farmerUpdate";
    public static final String KEY_GET_APK_UPDATE_URI = "apkUri";
    public static final String KEY_CONVERSION_FACTOR = "conversionFactorL";
    public static final String KEY_CONFIGURED_CLIENT_EMAIL = "configClientEmail";
    public static final String KEY_CONFIGURED_CLIENT_PASSWORD = "configClientPass";
    public static final String KEY_COLLECTIOIN_STARTED_COW = "collectionStartedCow";
    public static final String KEY_COLLECTION_STARTED_BUFFALO = "collectionStarteBuff";
    public static final String KEY_COLLECTION_STARTED_MIXED = "collectionStarteMix";
    public static final String KEY_FARMER_ID_DIGIT = "farmIdDigit";
    public static final String KEY_BUILD_FOOTER = "buildFooter";
    public static final String KEY_SMRTMOO_FONT_REDUCED = "isSmarMooprinterfont";
    public static final String KEY_COLLECTION_SMS_FOOTER = "collectionSMSFooter";
    public static final String KEY_ENABLE_REJECT_RATECHART = "enableRejectTodevice";
    public static final String KEY_ENABLE_MANUAL_DISPLAY = "enableManualTodevice";
    public static final String KEY_ENABLE_SIMLOCK_DISPLAY = "enableSimlockToDevice";
    public static final String KEY_ENABLE_BONUS_DISPLAY = "enableBonusDevice";
    public static final String KEY_ENABLE_MULTIPLE_COLLECTION_DISPLAY = "enableMultipleCollection";
    public static final String KEY_ENABLE_CONVERSION_FACTOR = "enableConversiondisplay";
    public static final String KEY_ENABLE_FARMER_LENGTH_CONFIGURATION = "enableFarmerLengthconfig";
    public static final String KEY_ALLOW_IN_KG_FORMAT = "allowInKgformat";
    public static final String KEY_WEIGHING_PREFIX = "weighingPrefix";
    public static final String KEY_WEIGHING_SUFFIX = "weighingSuffix";
    public static final String KEY_WEIGHING_SEPERATOR = "weighingSeprator";
    public static final String KEY_RATECHART_IN_KG = "ratechartInKg";
    public static final String KEY_DISPLAY_FORMAT_IN_KG = "displayFormatInKg";
    public static final String KEY_WEIGHING_DIVISION_FACTOR = "multiplicationFactorws";
    public static final String KEY_URL_HEADER = "urlHeader";
    public static final String KEY_ENABLE_SALES = "enableSales";
    public static final String KEY_ENABLE_FARMERID_SALES = "enableFarmIdSales";
    public static final String KEY_SALES_OR_COLLECTION = "salesOrCollection";
    public static final String KEY_SALES_RATE_COW = "salesRateCow";
    public static final String KEY_SALES_RATE_BUFF = "salesRateBuff";
    public static final String KEY_SALES_ENABLE_FS = "salesEnableFS";
    public static final String KEY_PRINT_ENABLE_A4 = "enablehpPrinter";
    public static final String KEY_ENABLE_BONUS_RDU = "enableBonusRDU";
    public static final String KEY_ENABLE_INCENTIVE_RDU = "enableIncentiveRDU";

    //For sales
    public static final String KEY_INCENTIVE_PERCENTAGE = "incentivePercentage";
    public static final String KEY_ENABLE_INCENTIVE_REPORT = "incentiveReport";
    public static final String KEY_ENABLE_SEQUENCE_NUMBER_REPORT = "enableSequenceNumber";
    public static final String KEY_ENABLE_CLR_REPORT = "enableCLRReport";
    public static final String KEY_ENABLE_TRUCK_ENTRY = "enableTruckEntry";
    public static final String KEY_ENABLE_CENTER_COLLECTION = "enableCenterCollection";
    public static final String KEY_ENABLE_DAIRY_REPORT = "enableDairyReport";
    public static final String KEY_ENABLE_FILLED_OR_EMPTY_CANS = "enableFilledOrEmptyCans";
    //Added after hatsun version 66
    public static final String KEY_TARE_COMMAND = "tareCommand";
    public static final String KEY_ENABLE_IPTABLE_RULE = "enableIpTableRule";
    public static final String KEY_INDIFOSS_TIMESTAMP = "indifossTimeStamp";
    public static final String KEY_SELECT_RATECHART_TYPE = "selRateChartType";
    public static final String KEY_SET_BONUS_RATECHART_COW = "setRateCowBonus";
    public static final String KEY_SET_BONUS_RATECHART_BUFFALO = "setRateBuffaloBonus";
    public static final String KEY_SET_BONUS_RATECHART_MIXED = "setRateMixedBonus";
    public static final String KEY_SET_DYNAMIC_RATECHART_COW = "setRateCowDynamic";
    public static final String KEY_SET_DYNAMIC_RATECHART_BUFFALO = "setRateBuffaloDynamic";
    public static final String KEY_SET_DYNAMIC_RATECHART_MIXED = "setRateMixedDynamic";
    public static final String KEY_ENABLE_BONUS_TO_PRINT = "enableBonusToPrint";
    //To editable rate for reject milk
    public static final String KEY_ENABLE_EDITABLE_RATE = "enableEditableRate";
    public static final String KEY_ALLOW_FARMER_EXPORTED_MAIL = "allowFarmerExportedMail";

    //Bonus rate chart
    public static final String KEY_ALLOW_MILK_QUALITY = "allowMilkQuality";
    public static final String KEY_ALLOW_EQUIPMENT_BASED_COLLECTION = "equipmentBasedCollection";
    public static final String KEY_ALLOW_ROUTE_IN_REPORT = "allowRouteinreport";
    public static final String KEY_ALLOW_NOC_IN_REPORT = "allowNocinReport";
    public static final String KEY_SET_BOOT_COMPLETED = "setBooteCompleted";

    //To enable of disable farmer exported mail to smartAmcu
    public static final String KEY_ATTEMPT_FOR_SIM_UNLOCK = "attemptSimUnlock";
    public static final String KEY_IS_FIRST_CONNECTION = "isFirstConnection";
    public static final String KEY_FOR_SHUTDOWN_ALERT_FLAG = "shutDownAlertFlag";
    public static final String KEY_FOR_TEMPORARY_SERVER = "tempServer";
    //Keys for editing the report
    public static final String KEY_ALLOW_VISIBILITY_REPORT_EDITING = "visiblityReportEditing";

    //As per china new Tab requirement
    public static final String KEY_ALLOW_REPORT_EDITING = "allowReportediting";
    public static final String KEY_ALLOW_OPERATOR_TO_EDIT_REPORT = "allowOperatorToReportEditing";
    public static final String KEY_ALLOW_NUMBER_OF_CAN_BE_EDIT = "numberOfEditableShift";
    /////////
    public static final String KEY_SET_MULTIPLE_MA = "multipleMA";
    public static final String KEY_SET_MY_RATE_CHART = "myRateChart";
    public static final String KEY_DB_EXCEPTION_COUNTER = "dbexcounter";
    //MA parameter visibility value
    public final static String KEY_FAT_VISIBILITY = "maFatVisibility";
    public final static String KEY_SNF_VISIBILITY = "maSnfVisiblity";
    public final static String KEY_DEN_VISIBILITY = "maDenVisibility";
    public final static String KEY_LAC_VISIBILITY = "maLacVisibility";
    public final static String KEY_TEMP_VISIBLITY = "MaTempVisiblity";
    public final static String KEY_CAL_VISIBLITY = "maCalVisibility";
    public final static String KEY_sALT_VISIBILITY = "maSaltVisiblity";
    public final static String KEY_ADW_VISIBILITY = "maADWVisibility";
    public final static String KEY_SN_VISIBILITY = "maSNVisibility";
    public final static String KEY_PRO_VISIBILITY = "maProVisibility";
    public final static String KEY_FRP_VISIBILITY = "maFrpVisibility";

    //Keys for toggle bw
    public final static String KEY_PH_VISIBILITY = "maPhVisibility";
    public final static String KEY_COM_VISIBILITY = "maComVisibility";
    public final static String KEY_MANAGER_PASSWORD = "managerPassword";
    public final static String KEY_OPERATOR_PASSWORD = "operatorPassword";
    public static final String KEY_MOBILE_DATA_ENABLE = "enableMobileData";
    public static final String KEY_SMARTCC_FEATURE = "smartCCfeature";
    public static final String KEY_SMARTCC_AGENT_URL = "agentUpdateUrl";
    public static final String KEY_SMARTCC_TRUCK_URL = "truckUpdateUrl";
    public static final String KEY_SMARTCC_ENABLE_TARE = "enableTare";
    public static final String KEY_TRUCK_RECORDS_COUNT = "truckRecordsCount";
    public static final String KEY_SMARTCC_SOUR_RATE_COW = "sourRateCow";
    public static final String KEY_SMARTCC_SOUR_RATE_BUFFALO = "sourRateBuffalo";
    public static final String KEY_SMARTCC_SOUR_RATE_MIXED = "sourRateMixed";
    public static final String KEY_CREATE_MULITIPLE_USER = "createMultipleUser";
    public static final String KEY_SMARTCC_MATERIAL_CODE = "smartCCmaterialCode";
    public static final String KEY_DEVIATION_WEIGHT = "deviationWeight";
    public static final String KEY_DEVIATION_ALERT_WEIGHT = "deviationAlertForWeight";
    public static final String KEY_RATE_CALCULATED_DEVICE = "isRateCalculatedFromDevice";
    public final static String KEY_COLL_START_MRN_SHIFT = "collectionStartMorningshift";
    public final static String KEY_COLL_END_MRN_SHIFT = "collectionEndMorningShift";
    public final static String KEY_COLL_START_EVN_SHIFT = "collectionStartEveningShift";
    public final static String KEY_COLL_END_EVN_SHIFT = "collectionEndEveningShift";
    public final static String KEY_IGNORE_ZERO_FAT_SNF = "ignoreZeroFatSNF";
    public final static String KEY_ENABLE_COLLECTION_CONSTRAINTS = "enableCollectionContraints";
    public final static String KEY_MANDATORY_RATE_CHART = "isMandatoryRatechart";
    public final static String KEY_MIN_FAT_REJECT_COW = "keyMinFatRejectCow";
    public final static String KEY_MIN_SNF_REJECT_COW = "keyMinSnfRejectCow";
    public final static String KEY_MAX_FAT_REJECT_COW = "keyMaxFatRejectCow";
    public final static String KEY_MAX_SNF_REJECT_COW = "keyMaxSnfRejectCow";
    public final static String KEY_MIN_FAT_REJECT_BUFF = "keyMinFatRejectBuff";
    public final static String KEY_MIN_SNF_REJECT_BUFF = "keyMinSnfRejectBuff";
    public final static String KEY_MAX_FAT_REJECT_BUFF = "keyMaxFatRejectBuff";
    public final static String KEY_MAX_SNF_REJECT_BUFF = "keyMaxSnfRejectBuff";
    public final static String KEY_MIN_FAT_REJECT_MIX = "keyMinFatRejectMix";
    public final static String KEY_MIN_SNF_REJECT_MIX = "keyMinSnfRejectMix";
    public final static String KEY_MAX_FAT_REJECT_MIX = "keyMaxFatRejectMix";
    public final static String KEY_MAX_SNF_REJECT_MIX = "keyMaxSnfRejectMix";
    public static final String KEY_DEVICE_PORT1 = "port1Device";
    public static final String KEY_DEVICE_PORT2 = "port2Device";
    public static final String KEY_DEVICE_PORT3 = "port3Device";
    public static final String KEY_DEVICE_PORT4 = "port4Device";
    public static final String KEY_DEVICE_PORT5 = "port5Device";
    public static final String KEY_MA1_PARITY = "ma1Parity";
    public static final String KEY_MA1_STOP_BITS = "ma1stopbits";
    public static final String KEY_MA1_DATA_BITS = "ma1Databits";
    public static final String KEY_MA2_PARITY = "ma2Parity";
    public static final String KEY_MA2_STOP_BITS = "ma2stopbits";
    public static final String KEY_MA2_DATA_BITS = "ma2Databits";
    public static final String KEY_WS_PARITY = "wsParity";
    public static final String KEY_WS_STOP_BITS = "wsstopbits";
    public static final String KEY_WS_DATA_BITS = "wsDatabits";
    //Rdu and printer configuration
    public static final String KEY_RDU_PARITY = "rduParity";
    public static final String KEY_RDU_STOP_BITS = "rduStopbits";
    public static final String KEY_RDU_DATA_BITS = "rduDatabits";
    public static final String KEY_PRINTER_PARITY = "printerParity";
    public static final String KEY_PRINTER_STOP_BITS = "printerStopbits";
    public static final String KEY_PRINTER_DATA_BITS = "printerDatabits";
    public static final String KEY_SET_RDU_WEIGHT_ROUND_OFF = "rduQuantityRoundOff";
    public static final String KEY_SNF_OR_CLR_TANKER = "snfOrClrForTanker";
    public static final String KEY_WEIGHT_RECORD_LENGTH = "weightRecordLength";
    public static final String KEY_PERIODIC_DATA_SENT = "periodicDataSent";
    public static final String KEY_DATA_SENT_START_TIME = "startTimeDataSent";
    public static final String KEY_IS_CLOUD_SUPPORT = "isCloudSupport";
    public static final String KEY_UNSENT_ALERT_LIMIT = "unsentAlertLimit";
    public static final String KEY_SHOW_UNSENT_ALERT = "showUnsentAlert";
    //Added in 11.5.7G
    public static final String KEY_DEFAULT_MILK_TYPE_BOTH = "defalutMilkTypeBoth";
    public static final String KEY_MIN_VALID_WEIGHT = "minValidWeightForCollection";
    public static final String KEY_LICENCE_JSON = "licenceJson";
    public static final String KEY_DELETE_COLL_SHIFT_RECORD = "deleteCollRecordShift";
    public static final String KEY_DELETE_FILES_DAY = "deleteFilesAfterDay";
    public final static String KEY_ESCAPE_ENABLE_COLLECTION = "escapeEnableForCollection";
    public static final String KEY_MA_PARITY = "maParity";
    public static final String KEY_MA_STOP_BITS = "mastopbits";
    public static final String KEY_MA_DATA_BITS = "maDatabits";
    public static final String KEY_SAMPLE_AS_COLLECTION = "sampleAsCollection";
    public static final String KEY_MAX_LIMIT_WEIGHT = "maxlimitWeight";
    public static final String KEY_MERGED_HMB_ENABLE = "mergedHMBEnable";
    public static final String KEY_ALLOW_FAMER_INCREMENT = "keyAllowFormerIncrement";
    public static final String KEY_INCENTIVE_RATE_URI = "incentiveRateUri";
    public static final String KEY_SET_INCETIVE_RATECHART_COW = "setIncentiveRateCow";
    public static final String KEY_SET_INCETIVE_RATECHART_BUFFALO = "setIncentiveRateBuffalo";
    public static final String KEY_SET_INCETIVE_RATECHART_MIXED = "setIncentiveRateMixed";

    public static final String MA_PING_VALUE = "maPingValue";
    public static final String WS_PING_VALUE = "wsPingValue";
    public static final String RDU_PING_VALUE = "rduPingValue";
    public static final String PRINTER_PING_VALUE = "printerPingValue";
    private static final String KEY_MORNING_START = "mrnStart";
    private static final String KEY_EVENING_START = "evnStart";
    private static final String KEY_SELECTED_LANGUAGE = "selLanguage";
    private static final String KEY_WEEKLY_SENT = "weekSent";
    private static final String KEY_MONTHLY_SENT = "monthSent";
    public static DatabaseHandler databaseHandler;
    public static String KEY_ALLOW_VISIBILITY_FOR_CANS_TOGGLE = "visibilityCanToggling";
    public static String KEY_ALLOW_CAN_TOGGLING = "allowCanToggling";
    public static String KEY_MAX_LIMIT_EMPTY_CAN = "maxLimitEmptyCan";
    public static String KEY_MIN_LIMIT_FILLED_CAN = "minLimitFilledCan";
    public static String KEY_CLR_ROUND_OFF = "clrRoundOff";
    public static String KEY_ALLOW_FSC_IN_SMS = "allowAllValueSms";
    public static String KEY_ALLOW_FSC_IN_PRINT = "allowAllValuePrint";
    public static String KEY_ALLOW_MAX_LIMIT_RC = "allowMaxLimitRc";
    public static String KEY_ALLOW_ROUND_OFF_FS = "allowRoundOffFatAndSnf";
    public static String KEY_SET_CLR_POSITION = "clrPosition";
    public static String KEY_SET_ADDED_WATER_POSITION = "waterPosition";
    public static String CONFIG_TABLE = DatabaseEntity.TABLE_CONFIGURATION;

    public static String KEY_USER_URI = "useruri";
    public static String KEY_USER_FAIL_COUNT = "userFailCount";
    public static String KEY_DISPLAY_QUANTITY = "isDisplayQuantity";
    public static String KEY_DISPLAY_AMOUNT = "isDisplayAmount";
    public static String KEY_DISPLAY_RATE = "isDisplayRate";
    public static String KEY_SENT_UPDATED_RECORDS = "sentNullRecords";
    private static AmcuConfig amcuConfig;
    public final String KEY_GET_CHILLING_URI = "chillingUri";
    public final String KEY_ALLOW_SEQUENCE_NUMBER_PRINT_REPORT = "allowSeqNumPrint";
    public final String KEY_END_SHIFT_SUCCESS = "endShiftSuccess";
    final String KEY_IS_ONCREATE_ONUPGRADE_DB = "onCreateonUpgrade";
    // Shared Preferences
    SharedPreferences pref;
    // Editor for Shared preferences
    Editor editor;
    // Context
    Context _context;
    // Shared pref mode
    int PRIVATE_MODE = 0;
    // milk Quality FAT SNF and CLR
    String KEY_FOR_COLLECTION_FATSNFCLR = "colFATSNFCLR";
    String KEY_FOR_CHILLING_FATSNFCLR = "chilFATSNFCLR";
    String KEY_FOR_FATSNFCLR = "allFATSNFCLR";
    String KEY_FOR_MORNING_SID = "MSID";
    String KEY_FOR_EVENING_SID = "ESID";
    String KEY_FOR_LAST_SID = "LSID";
    String KEY_MA1_BAUDRATE = "ma1Baudrate";
    String KEY_MA2_BAUDRATE = "ma2Baudrate";
    String KEY_MA1_NAME = "ma1Name";
    String KEY_MA2_NAME = "ma2Name";
    String KEY_MA1_PREV_RECORD = "ma1prevRecord";
    String KEY_MA2_PREV_RECORD = "ma2PrevRecord";
    String KEY_FIRST_MA_SERIAL_PORT = "fserialPort";
    String KEY_SECOND_MA_SERIAL_PORT = "sserialPort";
    String KEY_SET_CHECKBOX_MULTIPLE_MA = "multipleMACheckBox";
    String KEY_GET_IP_FOR_SERVER = "ipForServer";
    String KEY_SUPPORT_SINGLE_OR_MULTIPLE = "singleOrMultiple";
    String KEY_DBONCREATE_LIC = "onCreateCallLic";
    String KEY_LICENSE = "firstTimeLicense";
    String KEY_SUN_LICENSE = "sunCheck";
    String KEY_FARMER_FAIL_COUNT = "farmerFailCount";
    String KEY_AGENT_FAIL_COUNT = "agetnFailCount";
    String KEY_TRUCK_FAIL_COUNT = "truckFailCount";
    String KEY_CENTER_FAIL_COUNT = "centerFailCount";
    private Properties configProperties;

    // Constructor
    private AmcuConfig(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        configProperties = DefaultConfigurationHandler.getInstance().getConfig();
    }

    public static synchronized AmcuConfig getInstance() {
        if (amcuConfig == null) {
            amcuConfig = new AmcuConfig(DevAppApplication.getAmcuContext());
        }
        return amcuConfig;
    }

    public String getUnsentCleaningData() {
        return pref.getString(KEY_CLEANING_DATA, "");
    }

    public void setUnsentCleaningData(String data) {
        editor.putString(KEY_CLEANING_DATA, data);
        editor.commit();
    }

    public boolean getCleaningAlertToBeDisplayed() {
        return pref.getBoolean(KEY_CLEANING_ALERT, true);
    }

    public void setCleaningAlertToBeDisplayed(boolean value) {
        editor.putBoolean(KEY_CLEANING_ALERT, value);
        editor.commit();
    }

    public String getWhiteListURL() {
        // return pref.getString("whiteListURL", "NA");

        return configProperties.getProperty(AppConfig.Key.whiteListURL.getName());

       /* String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.WHITE_LIST_URL, "NA");
        return value; */
    }

    public void setWhiteListURL(String urls) {
        // editor.putString("whiteListURL", urls);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.whiteListURL.getName(), urls, getWhiteListURL());
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public String getRateChartName() {
        return pref.getString(KEY_RATECHART_NAME, null);
        //  String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_RATECHART_NAME, "NA");
        // return value;

    }

    public void setRateChartName(String name) {
        //ConfigurationElement configurationPushList = getConfigurationPushList(ConfigurationConstants.KEY_RATECHART_NAME, name, getRateChartName());
        // databaseHandler.updateConfigEntity(configurationPushList, 0);

        editor.putString(KEY_RATECHART_NAME, name);
        editor.commit();
    }

    public void setRateChartSocId(String name) {
        editor.putString(KEY_SOCIETYID, name);
        editor.commit();
    }

    public String getSocId() {
        return pref.getString(KEY_SOCIETYID, null);
    }

    public String getRateChartValidFrom() {
        return pref.getString(KEY_VALID_FROM, null);
    }

    public void setRateChartValidFrom(String name) {
        editor.putString(KEY_VALID_FROM, name);
        editor.commit();
    }

    public String getRateChartValidTO() {
        return pref.getString(KEY_VALID_TO, null);
    }

    public void setRateChartValidTO(String name) {
        editor.putString(KEY_VALID_TO, name);
        editor.commit();
    }

    public String getRateChartForCow() {
        return pref.getString(KEY_SETRATECHART_COW, null);
    }

    public void setRateChartForCow(String name) {
        editor.putString(KEY_SETRATECHART_COW, name);
        editor.commit();
    }

    public String getRateChartForBuffalo() {
        return pref.getString(KEY_SETRATECHART_BUFFALO, null);
    }

    public void setRateChartForBuffalo(String name) {
        editor.putString(KEY_SETRATECHART_BUFFALO, name);
        editor.commit();
    }

    public String getRateChartForMixed() {
        return pref.getString(KEY_SETRATECHART_MIXED, null);
    }

    public void setRateChartForMixed(String name) {
        editor.putString(KEY_SETRATECHART_MIXED, name);
        editor.commit();
    }

    public String getMilkType() {

        return configProperties.getProperty(AppConfig.Key.milkType.getName());

        //String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.MILK_TYPE, null);
        //        return value;  return pref.getString(KEY_MILKTYPE, null);
        //return value;
    }

    public void setMilkType(String name) {
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.milkType.getName(), name, getMilkType());
        databaseHandler.updateConfigEntity(configurationElement, 0);
        //editor.putString(KEY_MILKTYPE, name);
        // editor.commit();
    }

    public int getPostData() {
        return pref.getInt(KEY_POST_DATA, 0);
    }

    public void setPostData(int postData) {
        editor.putInt(KEY_POST_DATA, postData);
        editor.commit();
    }

    public int getMABaudRate() {

        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.milkAnalyzerBaudrate.getName()));
        //int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.MILK_ANALYZER_BAUDRATE, String.valueOf(9600)));
        //return value;
        //  return pref.getInt(KEY_MA_BAUDRATE, 9600);

    }

    public void setMABaudRate(int baudRate) {
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.milkAnalyzerBaudrate.getName(), String.valueOf(baudRate), String.valueOf(getMABaudRate()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
        // editor.putInt(KEY_MA_BAUDRATE, baudRate);
        // editor.commit();
    }

    public String getMA() {

        return configProperties.getProperty(AppConfig.Key.milkAnalyzerName.getName());

//        String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.MILK_ANALYZER_NAME, "LACTOSCAN");                                                             //
//        return value;  //return pref.getString(KEY_MILKANALYSER, "LACTOSCAN");

    }

    public void setMA(String MA) {
        // editor.putString(KEY_MILKANALYSER, MA);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.milkAnalyzerName.getName(), MA, getMA());
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public int getPrinterBaudRate() {
//        int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.PRINTER_BAUDRATE, String.valueOf(9600)));
//        return value;

        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.printerBaudrate.getName()));
        //return pref.getInt(KEY_PRINTER_BAUDRATE, 9600);

    }

    public void setPrinterBaudRate(int baudRate) {

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.printerBaudrate.getName(), String.valueOf(baudRate), String.valueOf(getPrinterBaudRate()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
        // editor.putInt(KEY_PRINTER_BAUDRATE, baudRate);
        //editor.commit();
    }

    public String getPrinter() {

        //  return pref.getString(KEY_PRINTER, "SMARTMOO");
        return configProperties.getProperty(AppConfig.Key.printerName.getName());

//        String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.PRINTER_NAME, "SMARTMOO");
//        return value;
    }

    public void setPrinter(String MA) {

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.printerName.getName(), MA, getPrinter());
        databaseHandler.updateConfigEntity(configurationElement, 0);
        //editor.putString(KEY_PRINTER, MA);
        //editor.commit();
    }

    public int getRdubaudrate() {

        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.rduBaudrate.getName()));
//        int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.RDU_BAUDRATE, String.valueOf(9600)));
//        return value;
        //retu        return value;rn pref.getInt(KEY_RDU_BAUDRATE, 9600);

    }

    public void setRdubaudrate(int baudRate) {


        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.rduBaudrate.getName(), String.valueOf(baudRate), String.valueOf(getRdubaudrate()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
        // editor.putInt(KEY_RDU_BAUDRATE, baudRate);
        //editor.commit();
    }

    public String getWeighingScale() {

        return configProperties.getProperty(AppConfig.Key.weighingScale.getName());//
        //
        //return pref.getString(KEY_WEIGHING_NAME, "ESSAE");
        //  String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.WEIGHING_SCALE, "ESSAE");
        // return value;
    }

    public void setWeighingScale(String baudRate) {
        //   editor.putString(KEY_WEIGHING_NAME, baudRate);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.weighingScale.getName(), baudRate, getWeighingScale());
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public int getWeighingbaudrate() {
        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.WeighingBaudrate.getName()));
        //return pref.getInt(KEY_WEIGHING_BAUDRATE, 9600);
//
        //  int value = Integer.parseInt(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_WEIGHING_BAUDRATE, String.valueOf(9600)));
        //  return value;


    }

    public void setWeighingbaudrate(int baudRate) {
        // editor.putInt(KEY_WEIGHING_BAUDRATE, baudRate);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.WeighingBaudrate.getName(), String.valueOf(baudRate), String.valueOf(getWeighingbaudrate()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public String getRDU() {

        return configProperties.getProperty(AppConfig.Key.rduName.getName());
        //
        //return pref.getString(KEY_RDU, "ESSAE");
//        String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.RDU_NAME, "ESSAE");
//        return value;
    }

    public void setRDU(String rdu) {
        // editor.putString(KEY_RDU, rdu);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.rduName.getName(), rdu, getRDU());
        databaseHandler.updateConfigEntity(configurationElement, 0);
    }

    public void setDeviceId(String deviceId) {
        editor.putString(KEY_DEVICE_ID, deviceId);
        editor.commit();
    }

    public String getDeviceID() {

        String deviceId = pref.getString(KEY_DEVICE_ID, "");

        if (deviceId == null) {
            TelephonyManager tm = (TelephonyManager)
                    _context.getSystemService(Context.TELEPHONY_SERVICE);
            String device_id = tm.getDeviceId();
            if (device_id != null && device_id.length() > 5) {
                amcuConfig.setDeviceId(device_id);
            }
        }

        return deviceId;
    }

    public String getDevicePassword() {

        //return pref.getString(KEY_DEVICE_Password, "");
     /*   String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.DEVICE_PWD, "");
        return value;*/

        return configProperties.getProperty(AppConfig.Key.devicePwd.getName());
    }

    public void setDevicePassword(String devicePwd) {
        // editor.putString(KEY_DEVICE_Password, devicePwd);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.devicePwd.getName(), devicePwd, getDevicePassword());
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public void setUpdateUri(String uri) {
        editor.putString(KEY_DEVICE_UPDATE_URI, uri);
        editor.commit();
    }

    public String getUpdateURI() {
        return pref.getString(KEY_DEVICE_UPDATE_URI, null);
    }


    public void setIncentiveUpdateUri(String uri) {
        editor.putString(KEY_INCENTIVE_RATE_URI, uri);
        editor.commit();
    }

    public String getIncentiveUpdateURI() {
        return pref.getString(KEY_INCENTIVE_RATE_URI, null);
    }


    public long getCalibrationDate() {

      /*  long value = Long.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.CALIBRATION_DATE, String.valueOf(12)));
        return value;*/

        return Long.valueOf(configProperties.getProperty(AppConfig.Key.calibrationDate.getName()));
        // return pref.getLong(KEY_SET_CALIBRATION_DATE, 12);
    }

    public void setCalibrationDate(long calibDate) {

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.calibrationDate.getName(), String.valueOf(calibDate), String.valueOf(getCalibrationDate()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
        //editor.putLong(KEY_SET_CALIBRATION_DATE, calibDate);
        //editor.commit();
    }

    public void setSimlockPassword(String simLock) {
        //  editor.putString(KEY_SIM_UNLOCK_PASSWORD, simLock);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.simlockPassword.getName(), simLock, getSimUnlockPassword());
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public String getServer() {
        //return pref.getString(KEY_SET_SERVER, null);


    /*    String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SET_AMCU_SERVER, null);
        return value;*/

        return configProperties.getProperty(AppConfig.Key.setAmcuServer.getName());

    }

    public void setServer(String server) {

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.setAmcuServer.getName(), server, getServer());
        databaseHandler.updateConfigEntity(configurationElement, 0);

        // editor.putString(KEY_SET_SERVER, server);
        //editor.commit();
    }

    public boolean getMaPingValue() {
        return pref.getBoolean(MA_PING_VALUE, true);
    }

    public void setMaPingValue(boolean value) {
        editor.putBoolean(MA_PING_VALUE, value).commit();
    }

    public boolean getWsPingValue() {
        return pref.getBoolean(WS_PING_VALUE, true);
    }

    public void setWsPingValue(boolean value) {
        editor.putBoolean(WS_PING_VALUE, value).commit();
    }

    public boolean getRduPingValue() {
        return pref.getBoolean(RDU_PING_VALUE, true);
    }

    public void setRduPingValue(boolean value) {
        editor.putBoolean(RDU_PING_VALUE, value).commit();
    }

    public boolean getPrinterPingValue() {
        return pref.getBoolean(PRINTER_PING_VALUE, true);
    }

    public void setPrinterPingValue(boolean value) {
        editor.putBoolean(PRINTER_PING_VALUE, value).commit();
    }

    public String getSendingMsg() {
        return pref.getString(KEY_SET_SENDING_MSG, "Sending mail...");
    }

    public void setSendingMsg(String smg) {
        editor.putString(KEY_SET_SENDING_MSG, smg);
        editor.commit();
    }

    public String getChangeFat() {
        // return pref.getString(KEY_SET_CHANGE_FAT, "0.0");
        return configProperties.getProperty(AppConfig.Key.changeMilktypeBuffalo.getName());

//        String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.CHANGE_MILKTYPE_BUFFALO, "0.0");
//        return value;

    }


    public void setChangeFat(String smg) {

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.changeMilktypeBuffalo.getName(), smg, getChangeFat());
        databaseHandler.updateConfigEntity(configurationElement, 0);

        //editor.putString(KEY_SET_CHANGE_FAT, smg);
        //editor.commit();
    }

    public String getChangeSnf() {
        // return pref.getString(KEY_SET_CHANGE_SNF, "0.0");

        return configProperties.getProperty(AppConfig.Key.changeSnfMilkTypeBuffalo.getName());
//        String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.CHANGE_SNF_MILK_TYPE_BUFFALO, "0.0");
//        return value;
    }

    public void setChangeSnf(String smg) {

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.changeSnfMilkTypeBuffalo.getName(), smg, getChangeSnf());
        databaseHandler.updateConfigEntity(configurationElement, 0);

        //  editor.putString(KEY_SET_CHANGE_SNF, smg);
        // editor.commit();
    }

    public String getEndedShift() {
        return pref.getString(KEY_SET_ENDED_SHIFT, "false");
    }

    public void setEndedShift(String smg) {
        editor.putString(KEY_SET_ENDED_SHIFT, smg);
        editor.commit();
    }

    public String getLastShift() {
        return pref.getString(KEY_SET_LAST_SHIFT, "M");
    }

    public void setLastShift(String smg) {
        editor.putString(KEY_SET_LAST_SHIFT, smg);
        editor.commit();
    }

    public String getCurrentShift() {
        return pref.getString(KEY_SET_CURRENT_SHIFT, "M");
    }

    public void setCurrentShift(String smg) {
        editor.putString(KEY_SET_CURRENT_SHIFT, smg);
        editor.commit();
    }

    public String getLastShiftDate() {
        return pref.getString(KEY_SET_LAST_SHIFT_DATE, "M");
    }

    public void setLastShiftDate(String smg) {
        editor.putString(KEY_SET_LAST_SHIFT_DATE, smg);
        editor.commit();
    }

    public void setCurrentAndPreviousShiftDate(String smg, String previous) {
        editor.putString(KEY_SET_CURRENT_SHIFT_DATE, smg);
        editor.putString(KEY_SET_PREVIOUS_SHIFT_DATE, previous);
        editor.commit();
    }

    public String getPreviousShiftDate() {
        return pref.getString(KEY_SET_PREVIOUS_SHIFT_DATE, null);
    }

    public String getCurrentShiftDate() {
        return pref.getString(KEY_SET_CURRENT_SHIFT_DATE, "20-Apr-2011M");
    }

    public boolean getCollectionEndShift() {
        return pref.getBoolean(KEY_SET_COLLECTION_ENDSHIFT, false);
    }

    public void setCollectionEndShift(boolean smg) {
        editor.putBoolean(KEY_SET_COLLECTION_ENDSHIFT, smg);
        editor.commit();
    }

    public int getDecimalRoundOffWeigh() {
        //return pref.getInt(KEY_WEIGHING_ROUNDOFF, 2);
        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.weightRoundOff.getName()));
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.WEIGHT_ROUND_OFF, String.valueOf(2)));
//        return value;


    }

    public void setDecimalRoundOffWeigh(int weigh) {


        ConfigurationElement configurationElement = getConfigurationPushList(
                AppConfig.Key.weightRoundOff.getName(), String.valueOf(weigh),
                String.valueOf(getDecimalRoundOffWeigh()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

        // editor.putInt(KEY_WEIGHING_ROUNDOFF, weigh);
        //editor.commit();
    }

    public int getDecimalRoundOffRate() {
        //  return pref.getInt(KEY_RATE_ROUNDOFF, 2);
       /* int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.RATE_ROUND_OFF, String.valueOf(2)));
        return value;*/

        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.RATE_ROUND_OFF_PRECISION.getName()));
    }

    public void setDecimalRoundOffRate(int rate) {
        //  editor.putInt(KEY_RATE_ROUNDOFF, rate);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.RATE_ROUND_OFF_PRECISION.getName(),
                String.valueOf(rate), String.valueOf(getDecimalRoundOffRate()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
    }

    //
    public int getDecimalRoundOffAmount() {
        //return pref.getInt(KEY_AMOUNT_ROUNDOFF, 2);

//        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.amo.getName()));
       /* int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.AMOUNT_ROUND_OFF, String.valueOf(2)));
        return value;*/

        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.AMOUNT_ROUND_OFF_PRECISION.getName()));
    }

    public void setDecimalRoundOffAmount(int roundValue) {

        ConfigurationElement configurationElement = getConfigurationPushList(
                AppConfig.Key.AMOUNT_ROUND_OFF_PRECISION.getName(), String.valueOf(roundValue), String.valueOf(getDecimalRoundOffAmount()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
        //     editor.putInt(KEY_AMOUNT_ROUNDOFF, smg);
        // editor.commit();
    }

    public boolean getDecimalRoundOffAmountCheck() {
        //return pref.getBoolean(KEY_AMOUNT_ROUNDOFFCHECK, true);
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.amountRoundOffCheck.getName()));

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.AMOUNT_ROUND_OFF_CHECK, String.valueOf(true)));
//        return value;
    }

    public void setDecimalRoundOffAmountCheck(boolean chk) {
        //editor.putBoolean(KEY_AMOUNT_ROUNDOFFCHECK, chk);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.amountRoundOffCheck.getName(),
                String.valueOf(chk), String.valueOf(getDecimalRoundOffAmountCheck()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
    }

    public boolean getDecimalRoundOffWeightCheck() {
        //return pref.getBoolean(KEY_WEIGHING_ROUNDCHECK, true);
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.weightRoundOffCheck.getName()));
//
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.WEIGHT_ROUND_OFF_CHECK, String.valueOf(true)));
//        return value;

    }

    public void setDecimalRoundOffWeightCheck(boolean chk) {
        // editor.putBoolean(KEY_WEIGHING_ROUNDCHECK, chk);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.weightRoundOffCheck.getName(), String.valueOf(chk), String.valueOf(getDecimalRoundOffWeightCheck()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public boolean getDecimalRoundOffRateCheck() {
        //return pref.getBoolean(KEY_RATE_ROUNDOFFCHECK, true);
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.IS_RATE_ROUND_OFF_ENABLED.getName()));
     /*   boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.RATE_ROUND_OFF_CHECK, String.valueOf(true)));*/
//        return value;

    }

    public void setDecimalRoundOffRateCheck(boolean chk) {
        //  editor.putBoolean(KEY_RATE_ROUNDOFFCHECK, chk);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.IS_RATE_ROUND_OFF_ENABLED.getName(),
                String.valueOf(chk), String.valueOf(getDecimalRoundOffRateCheck()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
    }


    public String getMilkAnalyserPrvData(String device) {
        return pref.getString(device + "_" + KEY_MILKANALYSER_PREVDATA, null);
    }

    public void setMilkAnalyserPrvData(String data, String device) {
        editor.putString(device + "_" + KEY_MILKANALYSER_PREVDATA, data);
        editor.commit();
    }

    public String getMilkAnalyserPrvData() {
        return pref.getString(KEY_MILKANALYSER_PREVDATA, null);
    }

    public void setMilkAnalyserPrvData(String data) {
        editor.putString(KEY_MILKANALYSER_PREVDATA, data);
        editor.commit();
    }

    //Changes for generic apk

    public String getUSBHUB() {
        return pref.getString(KEY_USBHUB, "HUB");
    }

    public void setUSBHUB(String hub) {
        editor.putString(KEY_USBHUB, hub);
        editor.commit();
    }

    public boolean getIsAddedWater() {
        //  return pref.getBoolean(KEY_AMOUNT_CHECKED, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.IS_ADDED_WATER, String.valueOf(false)));
        return value;
*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.isAddedWater.getName()));

    }

    public void setIsAddedWater(boolean isAmt) {
        //editor.putBoolean(KEY_AMOUNT_CHECKED, isAmt);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.isAddedWater.getName(), String.valueOf(isAmt), String.valueOf(getIsAddedWater()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public String getMilkAnalyzerID() {
        return pref.getString(KEY_MILKANALYZERID, null);
    }

    public void setMilkAnalyzerID(String isAmt) {
        editor.putString(KEY_MILKANALYZERID, isAmt);
        editor.commit();
    }

    public String getWeighingID() {
        return pref.getString(KEY_WEIGHINGID, null);
    }

    public void setWeighingID(String isAmt) {
        editor.putString(KEY_WEIGHINGID, isAmt);
        editor.commit();
    }

    public String getPrinterID() {
        return pref.getString(KEY_PRINTERID, null);
    }

    public void setPrinterID(String isAmt) {
        editor.putString(KEY_PRINTERID, isAmt);
        editor.commit();
    }

    public String getRDUID() {
        return pref.getString(KEY_RDUID, null);
    }

    public void setRDUID(String isAmt) {
        editor.putString(KEY_RDUID, isAmt);
        editor.commit();
    }

    public float getSnfCons() {
        //return pref.getFloat(KEY_SNF_CONSTANT, 4);

        //   float value = Float.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SNF_CONSTANT_FOR_CLR, String.valueOf(4)));

        return Float.valueOf(configProperties.getProperty(AppConfig.Key.snfConstantForClr.getName()));
    }

    public void setSnfCons(double type) {
        //   editor.putFloat(KEY_SNF_CONSTANT, (float) type);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.snfConstantForClr.getName(), String.valueOf(type), String.valueOf(getSnfCons()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getFatCons() {
        // return pref.getFloat(KEY_FAT_CONSTANT, .84f);
       /* float value = Float.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.FAT_CONSTANT_FOR_CLR, String.valueOf(.84f)));
        return value;*/

        return Float.valueOf(configProperties.getProperty(AppConfig.Key.fatConstantForClr.getName()));
    }

    public void setFatCons(double type) {
        //  editor.putFloat(KEY_FAT_CONSTANT, (float) type);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.fatConstantForClr.getName(), String.valueOf(type), String.valueOf(getFatCons()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getConstant() {
        // return pref.getFloat(KEY_CLR_CONSTANT, 1.44f);
     /*   float value = Float.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.CONSTANT_FOR_CLR, String.valueOf(1.44f)));
        return value;*/

        return Float.valueOf(configProperties.getProperty(AppConfig.Key.CLR_CONSTANT.getName()));

    }

    public void setConstant(double type) {
        //editor.putFloat(KEY_CLR_CONSTANT, (float) type);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.CLR_CONSTANT.getName(), String.valueOf(type), String.valueOf(getConstant()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getCreateAndEdit() {
      /*  // return pref.getBoolean(KEY_RATECHART_CREATEOREDIT, false);
        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.CREATE_EDIT, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.createEdit.getName()));

    }

    public void setCreateAndEdit(boolean chk) {
        //editor.putBoolean(KEY_RATECHART_CREATEOREDIT, chk);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.createEdit.getName(), String.valueOf(chk), String.valueOf(getCreateAndEdit()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public String getEncryptedReportFile() {
        return pref.getString(KEY_ENCRYPTED_FILE, null);
    }

    public void setEncryptedReportFile(String enCrypted) {
        editor.putString(KEY_ENCRYPTED_FILE, enCrypted);
        editor.commit();
    }

    public String getReportNotefile() {
        return pref.getString(KEY_REPORTNOTE_FILE, null);
    }

    public void setReportNotefile(String enCrypted) {
        editor.putString(KEY_REPORTNOTE_FILE, enCrypted);
        editor.commit();
    }

    public long getDownloadId() {
        return pref.getLong(KEY_DOWNLOAD_ID, 0);
    }

    public void setDownloadId(long Id) {
        editor.putLong(KEY_DOWNLOAD_ID, Id);
        editor.commit();
    }

    public void setSecondMilkAnalyser(String name, int baudRate) {

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.secondMAType.getName(), name, getSecondMilkAnalyser());
        databaseHandler.updateConfigEntity(configurationElement, 0);

        ConfigurationElement configurationbaudRate = getConfigurationPushList(AppConfig.Key.secondMABaudrate.getName(), String.valueOf(baudRate), String.valueOf(getSecondMilkBaud()));
        databaseHandler.updateConfigEntity(configurationbaudRate, 0);

        // editor.putString(KEY_SECOND_MILKANALYSER, name);
        //editor.putInt(KEY_SECOND_MILKANALYSER_BAUDRATE, baudRate);
        //editor.commit();
    }

    public String getSecondMilkAnalyser() {
        // return pref.getString(KEY_SECOND_MILKANALYSER, "LACTOSCAN");

        return configProperties.getProperty(AppConfig.Key.secondMAType.getName());


    }

    public int getSecondMilkBaud() {
        // return pref.getInt(KEY_SECOND_MILKANALYSER_BAUDRATE, 9600);
       /* int value = Integer.parseInt(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SECOND_MILKANALYSER_BAUDRATE, String.valueOf(9600)));
        return value;*/

        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.secondMABaudrate.getName()));

    }

    public int getLogInFor() {
        return pref.getInt(KEY_LOGIN_FOR, 0);
    }

    public void setLogInFor(int logIn) {
        editor.putInt(KEY_LOGIN_FOR, logIn);
        editor.commit();
    }

    public void setMilkAnalyzersMilkType(String Ma1, String Ma2) {
        //editor.putString(KEY_MA1_MILKTYPE, Ma1);
        //editor.putString(KEY_MA2_MILKTYPE, Ma2);
        //  editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.maMilkType1.getName(),
                Ma1, String.valueOf(getMA1MilkType()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


        ConfigurationElement configurationElementMA2 = getConfigurationPushList(AppConfig.Key.maMilkType2.getName(),
                Ma2, String.valueOf(getMA2MilkType()));
        databaseHandler.updateConfigEntity(configurationElementMA2, 0);


    }

    public String getMA1MilkType() {
        // return pref.getString(KEY_MA1_MILKTYPE, "NONE");
        return configProperties.getProperty(AppConfig.Key.maMilkType1.getName());
       /* String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MA1_MILKTYPE, "NONE");
        return value;*/

    }

    public String getMA2MilkType() {
        // return pref.getString(KEY_MA2_MILKTYPE, "NONE");
        return configProperties.getProperty(AppConfig.Key.maMilkType2.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MA2_MILKTYPE, "NONE");
        return value;*/

    }

    public void setKeySetDownloadedApkPath(String path) {
        editor.putString(KEY_SET_DOWNLOADED_APK_PATH, path);
        editor.commit();
    }

    public String getKEYDownloadeAPKPath() {
        return pref.getString(KEY_SET_DOWNLOADED_APK_PATH, null);
    }

    public void setSendMailToConfigureEmailIDs(boolean mailSend, boolean rateImport) {

        //   editor.putBoolean(KEY_RATE_CHART_EXCEL_IMPORT, rateImport);
        //   editor.putBoolean(KEY_SEND_EMAIL_CONFIGUREIDS, mailSend);
        //   editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.sendShiftMails.getName(), String.valueOf(mailSend),
                String.valueOf(getSendEmailToConfigureIDs()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

        ConfigurationElement configurationElementImport = getConfigurationPushList(AppConfig.Key.rateExcelImport.getName(), String.valueOf(rateImport),
                String.valueOf(getImportExcelRateEnable()));
        databaseHandler.updateConfigEntity(configurationElementImport, 0);


    }

    public boolean getSendEmailToConfigureIDs() {

        // return pref.getBoolean(KEY_SEND_EMAIL_CONFIGUREIDS, false);
      /*  boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_SEND_EMAIL_CONFIGUREIDS, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.sendShiftMails.getName()));
    }

    public boolean getImportExcelRateEnable() {
        //    return pref.getBoolean(KEY_RATE_CHART_EXCEL_IMPORT, false);
      /*  boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_RATE_CHART_EXCEL_IMPORT, String.valueOf(false)));
        return value;
*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.rateExcelImport.getName()));

    }

    public long getShutDownDelay() {
        //  return pref.getLong(KEY_SHUTDOWN_DELAY, 0);
      /*  long value = Long.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SHUT_DOWN_DELAY, String.valueOf(0)));
        return value;
*/
        return Long.valueOf(configProperties.getProperty(AppConfig.Key.shutDownDelay.getName()));

    }

    public void setShutDownDelay(long lnDelay) {
        // editor.putLong(KEY_SHUTDOWN_DELAY, lnDelay);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.shutDownDelay.getName(), String.valueOf(lnDelay), String.valueOf(getShutDownDelay()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public void setBonusDetails(String bStartDate, String bEndDate, float rate, boolean isEnable, boolean rateAccess) {
        //editor.putString(KEY_BONUS_START_DATE, bStartDate);
        //editor.putString(KEY_BONUS_END_DATE, bEndDate);
        //editor.putFloat(KEY_BONUS_RATE, rate);
        //editor.putBoolean(KEY_BONUS_ENABLE, isEnable);
        //editor.putBoolean(KEY_RATECHART_ACCESS_OPERATOR, rateAccess);
        //editor.commit();

        ConfigurationElement cplSD = getConfigurationPushList(AppConfig.Key.bonusStartDate.getName(), bStartDate, getBonusStartDate());
        databaseHandler.updateConfigEntity(cplSD, 0);
        ConfigurationElement cplED = getConfigurationPushList(AppConfig.Key.bonusEndDate.getName(), bEndDate, getBonusEndDate());
        databaseHandler.updateConfigEntity(cplED, 0);
        ConfigurationElement cplBR = getConfigurationPushList(AppConfig.Key.BONUS_AMOUNT.getName(), String.valueOf(rate), String.valueOf(getBonusRate()));
        databaseHandler.updateConfigEntity(cplBR, 0);
        ConfigurationElement cplBE = getConfigurationPushList(AppConfig.Key.IS_BONUS_COMPUTE_ENABLED.getName(), String.valueOf(isEnable), String.valueOf(getBonusEnable()));
        databaseHandler.updateConfigEntity(cplBE, 0);
        ConfigurationElement cplRA = getConfigurationPushList(AppConfig.Key.isRateImportAccessOperator.getName(), String.valueOf(rateAccess), String.valueOf(getOperatorRateAccess()));
        databaseHandler.updateConfigEntity(cplRA, 0);


    }

    public String getBonusStartDate() {
        //editor.putString(KEY_BONUS_START_DATE, bStartDate);

        // return pref.getString(KEY_BONUS_START_DATE, null);

        return configProperties.getProperty(AppConfig.Key.bonusStartDate.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.BONUS_START_DATE, null);
        return value;*/
    }

    public String getBonusEndDate() {
        // return pref.getString(KEY_BONUS_END_DATE, null);
        return configProperties.getProperty(AppConfig.Key.bonusEndDate.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.BONUS_END_DATE, null);
        return value;*/

    }

    public float getBonusRate() {
        //return pref.getFloat(KEY_BONUS_RATE, 0.0f);
     /*   float value = Float.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.BONUS, String.valueOf(0.0f)));
        return value;
*/
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.BONUS_AMOUNT.getName()));
    }

    public boolean getBonusEnable() {
        //  return pref.getBoolean(KEY_BONUS_ENABLE, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.IS_BONUS_ENABLE, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.IS_BONUS_COMPUTE_ENABLED.getName()));
    }

    public boolean getOperatorRateAccess() {
        // return pref.getBoolean(KEY_RATECHART_ACCESS_OPERATOR, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.IS_RATE_IMPORT_ACCESS_OPERATOR, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.isRateImportAccessOperator.getName()));
    }

    public void setMaAndWsManualAndAllowSMS(boolean maManual, boolean wsManual, boolean allowSMS)

    {
//        editor.putBoolean(KEY_CHECK_MA_MANUAL, maManual);
//        editor.putBoolean(KEY_CHECK_WS_MANUAL, wsManual);
//        editor.putBoolean(KEY_CHECK_ALLOW_SMS, allowSMS);
//        editor.commit();

        ConfigurationElement cplMM = getConfigurationPushList(AppConfig.Key.isMaManual.getName(), String.valueOf(maManual), String.valueOf(getMaManual()));
        databaseHandler.updateConfigEntity(cplMM, 0);


        ConfigurationElement cplWM = getConfigurationPushList(AppConfig.Key.IS_WS_MANUAL.getName(),
                String.valueOf(wsManual), String.valueOf(getWsManual()));
        databaseHandler.updateConfigEntity(cplWM, 0);

        ConfigurationElement cplAS = getConfigurationPushList(AppConfig.Key.enableSMS.getName(),
                String.valueOf(allowSMS), String.valueOf(getAllowSMS()));
        databaseHandler.updateConfigEntity(cplAS, 0);


    }

    public void setAllowSimLockPassword(boolean isSimlock) {
        //editor.putBoolean(KEY_CHECK_ALLOW_SIMLOCK, isSimlock);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.IS_SIM_LOCK_ENABLED.getName(), String.valueOf(isSimlock), String.valueOf(getAllowSimlockPassword()));
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public boolean getAllowSimlockPassword() {
        // return pref.getBoolean(KEY_CHECK_ALLOW_SIMLOCK, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ALLOW_SIMLOCK, String.valueOf(false)));
        return value;
*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.IS_SIM_LOCK_ENABLED.getName()));
    }

    public boolean getMaManual() {
        //  return pref.getBoolean(KEY_CHECK_MA_MANUAL, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.IS_MA_MANUAL, String.valueOf(false)));
        return value;
*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.isMaManual.getName()));

    }

    public boolean getWsManual()

    {
        // return pref.getBoolean(KEY_CHECK_WS_MANUAL, false);
      /*  boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.IS_WS_MANUAL, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.IS_WS_MANUAL.getName()));
    }

    public boolean getAllowSMS() {
        //  return pref.getBoolean(KEY_CHECK_ALLOW_SMS, false);
       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_SMS, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableSMS.getName()));

    }

    public String getSimUnlockPassword() {
        // return pref.getString(KEY_SIM_UNLOCK_PASSWORD, null);

        return configProperties.getProperty(AppConfig.Key.simlockPassword.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SIMLOCK_PASSWORD, null);
        return value;*/
    }

    public String getBuildName() {
        return pref.getString(KEY_SET_BUILD, null);
    }

    public void setBuildName(String build) {
        editor.putString(KEY_SET_BUILD, build);
        editor.commit();
    }

    public String getUnsentSequenceNumber() {
        return pref.getString(KEY_SET_UNSENT_SEQUENCE_NUMBER, null);
    }

    public void setUnsentSequenceNumber(String sequenceNumber) {
        editor.putString(KEY_SET_UNSENT_SEQUENCE_NUMBER, sequenceNumber);
        editor.commit();
    }

    public String getConfigurationURI() {
        return pref.getString(KEY_GET_CONFIG_UPDATE_URI, null);
    }

    public void setConfigurationURI(String configUri) {
        editor.putString(KEY_GET_CONFIG_UPDATE_URI, configUri);
        editor.commit();
    }

    public String getFarmerURI() {
        return pref.getString(KEY_GET_FARMER_UPDATE_URI, null);
    }

    public void setFarmerURI(String farmerUri) {
        editor.putString(KEY_GET_FARMER_UPDATE_URI, farmerUri);
        editor.commit();
    }

    public String getAPKUri() {
        return pref.getString(KEY_GET_APK_UPDATE_URI, null);
    }

    public void setAPKUri(String apkUri) {
        editor.putString(KEY_GET_APK_UPDATE_URI, apkUri);
        editor.commit();
    }

    public void setCurrentSimId(String simId) {
        editor.putString(KEY_CURRENT_SIM_ID, simId);
        editor.commit();

    }

    public String getSimId() {
        return pref.getString(KEY_CURRENT_SIM_ID, null);
    }

    public String getTempSimPin() {
        // return pref.getString(KEY_TEMP_SIM_PIN, null);

        return configProperties.getProperty(AppConfig.Key.simlockPassword.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.TEMP_SIM_PIN, null);
        return value;*/

    }

    public void setTempSimPin(String simPin) {
        // editor.putString(KEY_TEMP_SIM_PIN, simPin);
        //editor.commit();


        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.simlockPassword.getName(), simPin, getTempSimPin());
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public String getConversionFactor() {

        return configProperties.getProperty(AppConfig.Key.KG_TO_LTR_CONVERSION_FACTOR.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.CONVERSION_FACTOR, "1.027");
        return value;*/

        //  return pref.getString(KEY_CONVERSION_FACTOR, "1.027");
    }

    public void setConversionFactor(String factor) {
        // editor.putString(KEY_CONVERSION_FACTOR, factor);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.KG_TO_LTR_CONVERSION_FACTOR.getName(), factor, getConversionFactor());
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public void setConfiguredClient(String email, String password) {
        if (email == null) {

            ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.smartAmcuPassword.getName(), password, getClientConfiguredPassword());
            databaseHandler.updateConfigEntity(cplP, 0);


            // editor.putString(KEY_CONFIGURED_CLIENT_PASSWORD, password);
        } else if (password == null) {
            // editor.putString(KEY_CONFIGURED_CLIENT_EMAIL, email);
            ConfigurationElement cplE = getConfigurationPushList(AppConfig.Key.smartAmcuEmail.getName(), email, getClientConfiguredEmail());
            databaseHandler.updateConfigEntity(cplE, 0);

        } else {
            // editor.putString(KEY_CONFIGURED_CLIENT_EMAIL, email);
            //editor.putString(KEY_CONFIGURED_CLIENT_PASSWORD, password);

            ConfigurationElement cplE = getConfigurationPushList(AppConfig.Key.smartAmcuEmail.getName(), email, getClientConfiguredEmail());
            databaseHandler.updateConfigEntity(cplE, 0);
            ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.smartAmcuPassword.getName(), password, getClientConfiguredPassword());
            databaseHandler.updateConfigEntity(cplP, 0);
        }
        // editor.commit();
    }

    public String getClientConfiguredEmail() {

        return configProperties.getProperty(AppConfig.Key.smartAmcuEmail.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SMART_AMCU_EMAIL, "smartamcu.smartmoo@stellapps.com");
        return value;*/

        // return pref.getString(KEY_CONFIGURED_CLIENT_EMAIL, "smartamcu.smartmoo@stellapps.com");
    }

    public String getClientConfiguredPassword() {


        return configProperties.getProperty(AppConfig.Key.devicePwd.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SMART_AMCU_PASSWORD, "C9275B9B24B75C9488A3B3E35000AFED");
        return value;*/
        //
        //return pref.getString(KEY_CONFIGURED_CLIENT_PASSWORD, "C9275B9B24B75C9488A3B3E35000AFED");
    }

    public void setCurrentSeesionStartedWithCow(boolean forCow) {
        editor.putBoolean(KEY_COLLECTIOIN_STARTED_COW, forCow);
        editor.commit();
    }

    public boolean getCurrentSessionStartedWithCow() {
        return pref.getBoolean(KEY_COLLECTIOIN_STARTED_COW, false);
    }

    public boolean getCurrentSessionStartedWithBuff() {
        return pref.getBoolean(KEY_COLLECTION_STARTED_BUFFALO, false);
    }

    public void setCurrentSessionStartedWithBuff(boolean forBuff) {
        editor.putBoolean(KEY_COLLECTION_STARTED_BUFFALO, forBuff);
        editor.commit();
    }

    public boolean getCurrentSessionStartedWithMix() {
        return pref.getBoolean(KEY_COLLECTION_STARTED_MIXED, false);
    }

    public void setCurrentSessionStartedWithMix(boolean forMix) {
        editor.putBoolean(KEY_COLLECTION_STARTED_MIXED, forMix);
        editor.commit();
    }

    public int getFarmerIdDigit() {
        //  return pref.getInt(KEY_FARMER_ID_DIGIT, 4);
/*
        int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(
                CONFIG_TABLE, ConfigurationConstants.FARM_ID_DIGIT, String.valueOf(4)));
        return value;*/

        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.farmIdDigit.getName()));
    }

    public void setFarmerIdDigit(int Id) {
        //editor.putInt(KEY_FARMER_ID_DIGIT, Id);
        //editor.commit();
        ConfigurationElement cpl = getConfigurationPushList(
                AppConfig.Key.farmIdDigit.getName(), String.valueOf(Id), String.valueOf(getFarmerIdDigit()));
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public String getSMSFooter() {
        //   return pref.getString(KEY_BUILD_FOOTER, null);
        return configProperties.getProperty(AppConfig.Key.smsFooter.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SMS_FOOTER, null);
        return value;*/

    }

    public void setSMSFooter(String footer) {
        // editor.putString(KEY_BUILD_FOOTER, footer);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.smsFooter.getName(), footer, getSMSFooter());
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public boolean getIsSmartMoofontReduced() {
        return pref.getBoolean(KEY_SMRTMOO_FONT_REDUCED, true);
    }

    public void setIsSmartMoofontReduced(boolean smartMoo)

    {
        editor.putBoolean(KEY_SMRTMOO_FONT_REDUCED, smartMoo);
        editor.commit();
    }

    public String getCollectionSMSFooter() {
        // return pref.getString(KEY_COLLECTION_SMS_FOOTER, null);
        return configProperties.getProperty(AppConfig.Key.SMS_FOOTER_TEXT.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.COLLECTION_SMS_FOOTER, null);

        return value;*/
    }

    public void setCollectionSMSFooter(String smsFooter) {
        // editor.putString(KEY_COLLECTION_SMS_FOOTER, smsFooter);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.SMS_FOOTER_TEXT.getName(), smsFooter, getCollectionSMSFooter());
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getManualDisplay() {
        //   return pref.getBoolean(KEY_ENABLE_MANUAL_DISPLAY, false);

        /*boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_MANUAL_TO_DEVICE, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableManualToDevice.getName()));

    }

    public void setManualDisplay(boolean value) {
        //  editor.putBoolean(KEY_ENABLE_MANUAL_DISPLAY, value);
        // editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableManualToDevice.getName(), String.valueOf(value), String.valueOf(getManualDisplay()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getBonusDisplay() {
        //    return pref.getBoolean(KEY_ENABLE_BONUS_DISPLAY, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_BONUS_TO_DEVICE, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableBonusToDevice.getName()));
    }

//Below three getter and setter as per new china tab changes

    public void setBonusDisplay(boolean value) {
        // editor.putBoolean(KEY_ENABLE_BONUS_DISPLAY, value);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableBonusToDevice.getName(), String.valueOf(value), String.valueOf(getBonusDisplay()));
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public void setAllowMultipleCollection(boolean value) {
        // editor.putBoolean(KEY_ENABLE_MULTIPLE_COLLECTION_DISPLAY, value);
        //editor.commit();
        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableMultipleCollection.getName(), String.valueOf(value), String.valueOf(getAllowMultipeCollection()));
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public boolean getAllowMultipeCollection() {
        // return pref.getBoolean(KEY_ENABLE_MULTIPLE_COLLECTION_DISPLAY, false);
       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_MULTIPLE_COLLECTION, String.valueOf(false)));
        return value;*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableMultipleCollection.getName()));
    }

    public boolean getSimlockDisplay() {
        //return pref.getBoolean(KEY_ENABLE_SIMLOCK_DISPLAY, false);
       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_SIMLOCK_TO_DEVICE, String.valueOf(false)));
        return value;*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableSimlockToDevice.getName()));

    }

    public void setSimlockDisplay(boolean value) {
        // editor.putBoolean(KEY_ENABLE_SIMLOCK_DISPLAY, value);
        // editor.commit();
        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableSimlockToDevice.getName(), String.valueOf(value), String.valueOf(getSimlockDisplay()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getEnableConversionFactorDisplay() {
        //  return pref.getBoolean(KEY_ENABLE_CONVERSION_FACTOR, false);

      /*  boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_CONVERSION_FACTOR, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.IS_CONVERSiON_FACTOR_ENABLED.getName()));

    }

    public void setEnableConversionFactorDisplay(boolean value) {
        //editor.putBoolean(KEY_ENABLE_CONVERSION_FACTOR, value);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.IS_CONVERSiON_FACTOR_ENABLED.getName(),
                String.valueOf(value), String.valueOf(getEnableConversionFactorDisplay()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public void setEnableFarmerLenghtDisplay(boolean value) {
        // editor.putBoolean(KEY_ENABLE_FARMER_LENGTH_CONFIGURATION, value);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableConfigurableFarmerIdSize.getName(), String.valueOf(value), String.valueOf(getEnableFarmerLenghtConfiguration()));
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public boolean getEnableFarmerLenghtConfiguration() {
        // return pref.getBoolean(KEY_ENABLE_FARMER_LENGTH_CONFIGURATION, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_CONFIGURABLE_FARMER_ID_SIZE, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableConfigurableFarmerIdSize.getName()));

    }

    public void setAllowInKgFormat(boolean value) {
        //    editor.putBoolean(KEY_ALLOW_IN_KG_FORMAT, value);
        //  editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.ACCEPT_MILK_IN_KG_FORMAT.getName(), String.valueOf(value), String.valueOf(getAllowInKgformat()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getAllowInKgformat() {

        //   return pref.getBoolean(KEY_ALLOW_IN_KG_FORMAT, false);
        String value = configProperties.getProperty(AppConfig.Key.ACCEPT_MILK_IN_KG_FORMAT.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ACCEPT_MILK_IN_KG_FORMAT, String.valueOf(false));*/
        if (value != null && value.equalsIgnoreCase("Kg")) {

            return true;
        } else if (value == null) {
            return false;
        } else {
            return Boolean.parseBoolean(value);
        }


    }

    public String getWeighingPrefix() {
        //   return pref.getString(KEY_WEIGHING_PREFIX, "N");

        return configProperties.getProperty(AppConfig.Key.weighingScalePrefix.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.WEIGHING_SCALE_PREFIX, "N");
        return value;*/


    }

    public void setWeighingPrefix(String str) {
        // editor.putString(KEY_WEIGHING_PREFIX, str);
        //editor.commit();


        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.weighingScalePrefix.getName(),
                str, getWeighingPrefix());
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public String getWeighingSuffix() {
        //  return pref.getString(KEY_WEIGHING_SUFFIX, "=lt");

        return configProperties.getProperty(AppConfig.Key.weighingScaleSuffix.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.WEIGHING_SCALE_SUFFIX, "=lt");
        return value;*/

    }

    public void setWeighingSuffix(String str) {
        // editor.putString(KEY_WEIGHING_SUFFIX, str);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.weighingScaleSuffix.getName(),
                str, getWeighingSuffix());
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public void setWeighingSeparator(String str) {
        // editor.putString(KEY_WEIGHING_SEPERATOR, str);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.weighingScaleSeparator.getName(),
                str, getWeighingSeperator());
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public String getWeighingSeperator() {
        //    return pref.getString(KEY_WEIGHING_SEPERATOR, "CRLF");


        return configProperties.getProperty(AppConfig.Key.weighingScaleSeparator.getName());
       /* String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.WEIGHING_SCALE_SEPARATOR, "CRLF");
        return value;*/
    }

    public boolean getDisplayInKg() {
        //  return pref.getBoolean(KEY_DISPLAY_FORMAT_IN_KG, false);
      /*  boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.DISPLAY_KG_TO_DEVICE, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.displayKgToDevice.getName()));

    }

//    public void setMorningSID(int sid) {
//        editor.putInt(KEY_FOR_MORNING_SID, sid);
//        editor.commit();
//    }
//
//    public int getMorningSID() {
//        return pref.getInt(KEY_FOR_MORNING_SID, 1);
//    }
//    public void setEveningSID(int sid) {
//        editor.putInt(KEY_FOR_EVENING_SID, sid);
//        editor.commit();
//    }
//    public int getEveningSID() {
//        return pref.getInt(KEY_FOR_EVENING_SID, 1);
//    }

    public void setDisplayInKg(boolean dsp) {                                                                 //
//        editor.putBoolean(KEY_DISPLAY_FORMAT_IN_KG, dsp);                                                     //
        //      editor.commit();


        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.displayKgToDevice.getName(), String.valueOf(dsp), String.valueOf(getDisplayInKg()));
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public void setKeyRatechartInKg(boolean rcg) {
        // editor.putBoolean(KEY_RATECHART_IN_KG, rcg);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.isRateChartInKg.getName(), String.valueOf(rcg), String.valueOf(getKeyRateChartInKg()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    // Edit the previous data from record history

    public boolean getKeyRateChartInKg() {

        //return pref.getBoolean(KEY_RATECHART_IN_KG, false);

        String value = configProperties.getProperty(AppConfig.Key.isRateChartInKg.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.IS_RATE_CHART_IN_KG, String.valueOf(false));*/

        if (value != null && value.equalsIgnoreCase("Kg")) {
            return true;
        } else if (value == null) {
            return false;
        } else {
            return Boolean.parseBoolean(value);
        }


    }

    public String getURLHeader() {

        String value = configProperties.getProperty(AppConfig.Key.urlHeader.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.URL_HEADER, "https://");*/
        if (value != null && value.endsWith(Util.URL_APPEND)) {
            return value;
        } else {
            return value + Util.URL_APPEND;
        }


        //return pref.getString(KEY_URL_HEADER, "https://");
    }

    public void setURLHeader(String urlHeader) {
        // editor.putString(KEY_URL_HEADER, urlHeader);
        // editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.urlHeader.getName(),
                urlHeader, getURLHeader());
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public double getWeighingDivisionFactor() {
        //return pref.getFloat(KEY_WEIGHING_DIVISION_FACTOR, 1);

       /* double value = Double.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.WEIGHING_DIVISION_FACTOR, String.valueOf(1)));
        return value;*/

        return Double.valueOf(configProperties.getProperty(AppConfig.Key.weighingDivisionFactor.getName()));
    }

    public void setWeighingDivisionFactor(float factor) {
        //  editor.putFloat(KEY_WEIGHING_DIVISION_FACTOR, factor);
        // editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.weighingDivisionFactor.getName(), String.valueOf(factor), String.valueOf(getWeighingDivisionFactor()));
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public boolean getEnableSales() {
        //  return pref.getBoolean(KEY_ENABLE_SALES, false);
        /*boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_SALES, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableSales.getName()));

    }

    public void setEnableSales(boolean sales) {
        //  editor.putBoolean(KEY_ENABLE_SALES, sales);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableSales.getName(), String.valueOf(sales), String.valueOf(getEnableSales()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getEnableFarmerIdForSales() {
        // return pref.getBoolean(KEY_ENABLE_FARMERID_SALES, false);

        /*boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_SALES_FARMER_ID, String.valueOf(false)));
        return value;
*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableSalesFarmerId.getName()));
    }

    public void setEnableFarmerIdForSales(boolean enableId) {
        //editor.putBoolean(KEY_ENABLE_FARMERID_SALES, enableId);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableSalesFarmerId.getName(), String.valueOf(enableId), String.valueOf(getEnableFarmerIdForSales()));
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public String getSalesOrCollection() {
        return pref.getString(KEY_SALES_OR_COLLECTION, "Collection");
    }

    public void setSalesOrCollection(String sales) {
        editor.putString(KEY_SALES_OR_COLLECTION, sales);
        editor.commit();
    }

    public String getIndifossTimeStamp() {
        return pref.getString(KEY_INDIFOSS_TIMESTAMP, "NULL");
    }

    public void setIndifossTimeStamp(String timeStamp) {
        editor.putString(KEY_INDIFOSS_TIMESTAMP, timeStamp);
        editor.commit();
    }

    public void setSalesData(String cowRate, String buffRate, boolean isEnableFatSnf) {


        ConfigurationElement configureCowRate = getConfigurationPushList(AppConfig.Key.salesCowRate.getName(),
                cowRate, getSalesCowRate());
        databaseHandler.updateConfigEntity(configureCowRate, 0);

        ConfigurationElement configureBuffRate = getConfigurationPushList(AppConfig.Key.salesBuffRate.getName(),
                buffRate, getSalesBuffRate());
        databaseHandler.updateConfigEntity(configureBuffRate, 0);

        ConfigurationElement configureIsEnableFatSnf = getConfigurationPushList(AppConfig.Key.enableSalesFS.getName(),
                String.valueOf(isEnableFatSnf), String.valueOf(getSalesFSEnable()));
        databaseHandler.updateConfigEntity(configureIsEnableFatSnf, 0);


    }

    public String getSalesMixedRate() {

        return configProperties.getProperty(AppConfig.Key.salesMixedRate.getName());


    }

    public void setSalesMixedRate(String salesRate) {

        ConfigurationElement cofigureMixRate = getConfigurationPushList(AppConfig.Key.salesMixedRate.getName(),
                salesRate, getSalesMixedRate());
        databaseHandler.updateConfigEntity(cofigureMixRate, 0);

    }

    public String getSalesCowRate() {

        return configProperties.getProperty(AppConfig.Key.salesCowRate.getName());


    }

    public String getSalesBuffRate()

    {
        return configProperties.getProperty(AppConfig.Key.salesBuffRate.getName());
    }

    //As per govind dairy requirement CLR round off configuration,

    public boolean getSalesFSEnable() {
        //  return pref.getBoolean(KEY_SALES_ENABLE_FS, false);
        /*boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SALES_ENABLE_FS, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableSalesFS.getName()));

    }

    public boolean getEnableA4Printer() {
        // return pref.getBoolean(KEY_PRINT_ENABLE_A4, false);
      /*  boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_PRINT_ENABLE_A4, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enablehpPrinter.getName()));
    }

    public void setEnableA4Printer(boolean enablePrint) {
        //   editor.putBoolean(KEY_PRINT_ENABLE_A4, enablePrint);
        //  editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enablehpPrinter.getName(),
                String.valueOf(enablePrint), String.valueOf(getEnableA4Printer()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public boolean getEnableBonusRDU() {
        //  return pref.getBoolean(KEY_ENABLE_BONUS_RDU, false);
     /*   boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_BONUS_DISPLAY_RDU, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableBonusDisplayRDU.getName()));

    }

    public void setEnableBonusRDU(boolean enableBonus) {

        // editor.putBoolean(KEY_ENABLE_BONUS_RDU, enableBonus);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.enableBonusDisplayRDU.getName(), String.valueOf(enableBonus), String.valueOf(getEnableBonusRDU()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public boolean getEnableIncentiveRDU() {
        /*// return pref.getBoolean(KEY_ENABLE_INCENTIVE_RDU, false);
        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_INCENTIVE_ON_RDU, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableIncentiveOnRdu.getName()));

    }

    public void setEnableIncentiveRDU(boolean incentive) {
        //editor.putBoolean(KEY_ENABLE_INCENTIVE_RDU, incentive);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.enableIncentiveOnRdu.getName(), String.valueOf(incentive), String.valueOf(getEnableIncentiveRDU()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public void setIptableRule(boolean ipTable) {
        //  editor.putBoolean(KEY_ENABLE_IPTABLE_RULE, ipTable);
        // editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableIpTableRule.getName(),
                String.valueOf(ipTable), String.valueOf(getIpTableRule()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getIpTableRule() {
        //  return pref.getBoolean(KEY_ENABLE_IPTABLE_RULE, true);
        /*boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_IP_TABLE_RULE, String.valueOf(true)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableIpTableRule.getName()));
    }

    public float getIncentivePercentage() {
        /*float value = Float.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.INCENTIVE_PERCENTAGE, String.valueOf(40)));
        return value;
*/
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.incentivePercentage.getName()));
        //   return pref.getFloat(KEY_INCENTIVE_PERCENTAGE, 40);
    }

    public void setIncentivePercentage(float incentivePercentage) {
        // editor.putFloat(KEY_INCENTIVE_PERCENTAGE, incentivePercentage);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.incentivePercentage.getName(), String.valueOf(incentivePercentage), String.valueOf(getIncentivePercentage()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getEnableIncentiveInReport() {
        //  return pref.getBoolean(KEY_ENABLE_INCENTIVE_REPORT, false);
/*

        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_INCENTIVE_IN_REPORT, String.valueOf(false)));
        return value;
*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableIncentiveInReport.getName()));

    }

    public void setEnableIncentiveInReport(boolean enableReport) {
        //  editor.putBoolean(KEY_ENABLE_INCENTIVE_REPORT, enableReport);
        // editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.enableIncentiveInReport.getName(), String.valueOf(enableReport), String.valueOf(getEnableIncentiveInReport()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public boolean getEnableSequenceNumberInReport() {
        // return pref.getBoolean(KEY_ENABLE_SEQUENCE_NUMBER_REPORT, true);
       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_SEQUENCE_NUMBER_REPORT, String.valueOf(true)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableSequenceNumberReport.getName()));

    }

    public void setEnableSequenceNumberInReport(boolean enableSeq) {
        // editor.putBoolean(KEY_ENABLE_SEQUENCE_NUMBER_REPORT, enableSeq);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.enableSequenceNumberReport.getName(), String.valueOf(enableSeq), String.valueOf(getEnableSequenceNumberInReport()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public boolean getEnableClrInReport() {
        //   return pref.getBoolean(KEY_ENABLE_CLR_REPORT, false);

      /*  boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_CLR_IN_REPORT, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.INCLUDE_CLR_IN_REPORT.getName()));


    }

    public void setEnableClrInReport(boolean enableClr) {
        /// editor.putBoolean(KEY_ENABLE_CLR_REPORT, enableClr);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.INCLUDE_CLR_IN_REPORT.getName(), String.valueOf(enableClr), String.valueOf(getEnableClrInReport()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public boolean getEnableTruckEntry() {
        // return pref.getBoolean(KEY_ENABLE_TRUCK_ENTRY, false);

        /*boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_TRUCK_ENTRY, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableTruckEntry.getName()));

    }

    public void setEnableTruckEntry(boolean enableTruck) {
        //  editor.putBoolean(KEY_ENABLE_TRUCK_ENTRY, enableTruck);
        //editor.commit();

        ConfigurationElement configurationEnableTruckEntry = getConfigurationPushList(AppConfig.Key.enableTruckEntry.getName(),
                String.valueOf(enableTruck), String.valueOf(getEnableTruckEntry()));
        databaseHandler.updateConfigEntity(configurationEnableTruckEntry, 0);
    }

    public boolean getEnableCenterCollection() {
        //return pref.getBoolean(KEY_ENABLE_CENTER_COLLECTION, false);
       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_CENTER_COLLECTION, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableCenterCollection.getName()));
    }

    public void setEnableCenterCollection(boolean enableCenterEntry) {
        //  editor.putBoolean(KEY_ENABLE_CENTER_COLLECTION, enableCenterEntry);
        //editor.commit();

        ConfigurationElement configurationEnableCenterCollection = getConfigurationPushList(AppConfig.Key.enableCenterCollection.getName(),

                String.valueOf(enableCenterEntry), String.valueOf(getEnableCenterCollection()));
        databaseHandler.updateConfigEntity(configurationEnableCenterCollection, 0);


    }

    public boolean getEnableDairyReport() {
        // return pref.getBoolean(KEY_ENABLE_DAIRY_REPORT, false);
       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_DAIRY_REPORT, String.valueOf(false)));
        return value;
*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.IS_DAIRY_REPORT_ENABLED.getName()));

    }

    public void setEnableDairyReport(boolean enableDairy) {
        //// editor.putBoolean(KEY_ENABLE_DAIRY_REPORT, enableDairy);
        // editor.commit();

        ConfigurationElement configurationEnableDaityReport = getConfigurationPushList(AppConfig.Key.IS_DAIRY_REPORT_ENABLED.getName(),

                String.valueOf(enableDairy), String.valueOf(getEnableDairyReport()));
        databaseHandler.updateConfigEntity(configurationEnableDaityReport, 0);


    }

    public void setChillingCenterUri(String chillingUri) {
        editor.putString(KEY_GET_CHILLING_URI, chillingUri);
        editor.commit();
    }

    public String getChillingUri() {
        return pref.getString(KEY_GET_CHILLING_URI, null);
    }

    public boolean getEnableFilledOrEmptyCans() {
        // return pref.getBoolean(KEY_ENABLE_FILLED_OR_EMPTY_CANS, false);

        /*boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_FILLED_OR_EMPTY_CANS, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableFilledOrEmptyCans.getName()));

    }

    public void setEnableFilledOrEmptyCans(boolean enableCans) {
        //editor.putBoolean(KEY_ENABLE_FILLED_OR_EMPTY_CANS, enableCans);
        //editor.commit();

        ConfigurationElement configurationEnableDaityReport = getConfigurationPushList(AppConfig.Key.enableFilledOrEmptyCans.getName(),


                String.valueOf(enableCans), String.valueOf(getEnableFilledOrEmptyCans()));
        databaseHandler.updateConfigEntity(configurationEnableDaityReport, 0);


    }

    public String getTareCommand() {
        // return pref.getString(KEY_TARE_COMMAND, "T");

        return configProperties.getProperty(AppConfig.Key.tareCommand.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.TARE_COMMAND, "T");
        return value;*/


    }

    public void setTareCommand(String tare) {
        //  editor.putString(KEY_TARE_COMMAND, tare);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.tareCommand.getName(), tare, getTareCommand());
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public String getRateChartType() {
        //  return pref.getString(KEY_SELECT_RATECHART_TYPE, "FATSNF");
        return configProperties.getProperty(AppConfig.Key.selectRateChartType.getName());
        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.SELECT_RATE_CHART_TYPE, "FATSNF");
        return value;*/


    }

    public void setRateChartType(String rateCharttype) {
        //  editor.putString(KEY_SELECT_RATECHART_TYPE, rateCharttype);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.selectRateChartType.getName(), rateCharttype,
                getRateChartType());
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public String getBonusRateChartCow() {
        return pref.getString(KEY_SET_BONUS_RATECHART_COW, null);
    }

    public void setBonusRateChartCow(String rateC) {
        editor.putString(KEY_SET_BONUS_RATECHART_COW, rateC);
        editor.commit();
    }

    public String getBonusRateChartBuffalo() {
        return pref.getString(KEY_SET_BONUS_RATECHART_BUFFALO, null);
    }

    public void setBonusRateChartBuffalo(String rateC) {
        editor.putString(KEY_SET_BONUS_RATECHART_BUFFALO, rateC);
        editor.commit();
    }

    public String getBonusRateChartMixed() {
        return pref.getString(KEY_SET_BONUS_RATECHART_MIXED, null);
    }

    public void setBonusRateChartMixed(String rateC) {
        editor.putString(KEY_SET_BONUS_RATECHART_MIXED, rateC);
        editor.commit();
    }

    public void setEnableBonusToPrint(boolean bonusToPrint) {
        //  editor.putBoolean(KEY_ENABLE_BONUS_TO_PRINT, bonusToPrint);
        // editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.IS_PRINT_BONUS_ENABLED.getName(), String.valueOf(bonusToPrint), String.valueOf(getBonusEnableForPrint()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getBonusEnableForPrint()

    {
        // return pref.getBoolean(KEY_ENABLE_BONUS_TO_PRINT, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_BONUS_FOR_PRINT, String.valueOf(false)));
        return value;*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.IS_PRINT_BONUS_ENABLED.getName()));
    }

    public boolean getEditableRate() {
        //  return pref.getBoolean(KEY_ENABLE_EDITABLE_RATE, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_EDITABLE_RATE, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableEditableRate.getName()));

    }

    public void setEditableRate(boolean editableRate) {
        // editor.putBoolean(KEY_ENABLE_EDITABLE_RATE, editableRate);
        //editor.commit();

        ConfigurationElement cEditableRate = getConfigurationPushList(AppConfig.Key.enableEditableRate.getName(),
                String.valueOf(editableRate), String.valueOf(getEditableRate()));
        databaseHandler.updateConfigEntity(cEditableRate, 0);
    }

    public boolean getAllowMailExportedFarmer() {
        //  return pref.getBoolean(KEY_ALLOW_FARMER_EXPORTED_MAIL, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ENABLE_FARMER_EXPORT_MAIL, String.valueOf(false)));
//        return value;
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableFarmerExportMail.getName()));


    }

    public void setAllowMailExportedFarmer(boolean mailEnable) {
        //  editor.putBoolean(KEY_ALLOW_FARMER_EXPORTED_MAIL, mailEnable);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enableFarmerExportMail.getName(),
                String.valueOf(mailEnable), String.valueOf(getAllowMailExportedFarmer()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public void setAllowMilkQuality(boolean mailEnable) {
        // editor.putBoolean(KEY_ALLOW_MILK_QUALITY, mailEnable);
        //editor.commit();


        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enableMilkQuality.getName(),
                String.valueOf(mailEnable), String.valueOf(getAllowMilkquality()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getAllowMilkquality() {
        // return pref.getBoolean(KEY_ALLOW_MILK_QUALITY, true);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ENABLE_MILK_QUALITY, String.valueOf(true)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableMilkQuality.getName()));


    }

    public void setAllowEquipMentBasedCollection(boolean enable) {
        // editor.putBoolean(KEY_ALLOW_EQUIPMENT_BASED_COLLECTION, enable);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enableEquipmentBasedCollection.getName(),
                String.valueOf(enable), String.valueOf(getAllowEquipmentBasedCollection()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getAllowEquipmentBasedCollection() {
        // return pref.getBoolean(KEY_ALLOW_EQUIPMENT_BASED_COLLECTION, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ENABLE_EQUIPMENT_BASED_COLLECTION, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableEquipmentBasedCollection.getName()));
    }

    public void setAllowNumberOfCansInReport(boolean allow) {
        //  editor.putBoolean(KEY_ALLOW_NOC_IN_REPORT, allow);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.INCLUDE_CAN_COUNT_IN_REPORT.getName(),
                String.valueOf(allow), String.valueOf(getAllowNumberOfCans()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getAllowNumberOfCans() {
        //  return pref.getBoolean(KEY_ALLOW_NOC_IN_REPORT, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ALLOW_NUMBER_OF_CANS_IN_REPORT, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.INCLUDE_CAN_COUNT_IN_REPORT.getName()));


    }

    public boolean getAllowRouteInReport() {
        //return pref.getBoolean(KEY_ALLOW_ROUTE_IN_REPORT, false);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ALLOW_COLLECTION_ROUTE_IN_REPORT, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.ALLOW_COLLECTION_ROUTE_IN_REPORT.getName()));

    }

    public void setAllowRouteInReport(boolean allow) {
        //   editor.putBoolean(KEY_ALLOW_ROUTE_IN_REPORT, allow);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.ALLOW_COLLECTION_ROUTE_IN_REPORT.getName(),
                String.valueOf(allow), String.valueOf(getAllowRouteInReport()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getBootCompleted() {
        return pref.getBoolean(KEY_SET_BOOT_COMPLETED, false);
    }

    public void setBootCompleted(boolean isBootCompleted) {
        editor.putBoolean(KEY_SET_BOOT_COMPLETED, isBootCompleted);
        editor.commit();
    }

    public void setAttemptForSimlock(boolean attempt) {
        editor.putBoolean(KEY_ATTEMPT_FOR_SIM_UNLOCK, attempt);
        editor.commit();
    }

    public boolean getAttemptFortSimlock() {
        return pref.getBoolean(KEY_ATTEMPT_FOR_SIM_UNLOCK, false);
    }

    public void setFirstConnection(boolean attempt) {
        editor.putBoolean(KEY_IS_FIRST_CONNECTION, attempt);
        editor.commit();
    }

    public boolean getFirstTimeConnection() {
        return pref.getBoolean(KEY_IS_FIRST_CONNECTION, true);
    }

    public boolean getShutDownAlertFlag() {
        return pref.getBoolean(KEY_FOR_SHUTDOWN_ALERT_FLAG, false);
    }

    public void setShutDownAlertFlag(boolean flag) {
        editor.putBoolean(KEY_FOR_SHUTDOWN_ALERT_FLAG, flag);
        editor.commit();
    }

    /// for MILK Quality type FAT SNF and CLR
    public String getCollectionFATSNFCLR() {
        // return pref.getString(KEY_FOR_COLLECTION_FATSNFCLR, "FS");

        return configProperties.getProperty(AppConfig.Key.enableFatSnfViewForCollection.getName());
//        String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ENABLE_FAT_SNF_VIEW_FOR_COLLECTION, "FS");
//        return value;
    }

    public void setCollectionFATSNFCLR(String flag) {
        //FS for fat and snf, FC for fat and clr
        // editor.putString(KEY_FOR_COLLECTION_FATSNFCLR, flag);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enableFatSnfViewForCollection.getName(),
                flag, getCollectionFATSNFCLR());
        databaseHandler.updateConfigEntity(configurationElement, 0);
    }

    public String getChillingFATSNFCLR() {

        //  return configProperties.getProperty(AppConfig.Key.en.getName());
       /* String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ENABLE_FAT_CLR_VIEW_FOR_CHILLING, "FS");
        return value;*/

        return configProperties.getProperty(AppConfig.Key.enableFatClrViewForChilling.getName());
    }

    public void setChillingFATSNFCLR(String flag) {
        //FS for fat and snf, FC for fat and clr
        //editor.putString(KEY_FOR_CHILLING_FATSNFCLR, flag);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enableFatClrViewForChilling.getName(),
                flag, getChillingFATSNFCLR());
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getFATSNFCLR() {
        //  return pref.getBoolean(KEY_FOR_FATSNFCLR, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ENABLE_ALL_FAT_SNF_CLR_VIEW, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableAllFatSnfClrView.getName()));

    }

    public void setFATSNFCLR(boolean flag) {
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enableAllFatSnfClrView.getName(),
                String.valueOf(flag), String.valueOf(getFATSNFCLR()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
        //true for all the fat flsnaf and clr displayF
        // editor.putBoolean(KEY_FOR_FATSNFCLR, flag);
        //editor.commit();
    }

    //FIXME is this required? "tempServer" property doesnt exist in the configProperties table. it will always return null.
    public String getTemporaryServer() {
        ///   return pref.getString(KEY_FOR_TEMPORARY_SERVER, null);

        /*String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.TEMPORARY_SERVER, null);
        return value;*/

        return configProperties.getProperty(ConfigurationConstants.TEMPORARY_SERVER, null);

    }

    public void setTemporaryServer(String tempServer) {
        // editor.putString(KEY_FOR_TEMPORARY_SERVER, tempServer);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(ConfigurationConstants.TEMPORARY_SERVER,
                tempServer, String.valueOf(getTemporaryServer()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getAllowSequenceNumberInPrintAndReport() {
        return pref.getBoolean(KEY_ALLOW_SEQUENCE_NUMBER_PRINT_REPORT, false);
    }

    public void setAllowSequenceNumberInPrintAndReport(boolean allowSequenceNum) {
        editor.putBoolean(KEY_ALLOW_SEQUENCE_NUMBER_PRINT_REPORT, allowSequenceNum);
        editor.commit();
    }

    public int getLastSaveSID() {
        return pref.getInt(KEY_FOR_LAST_SID, 1);
    }

    public void setLastSaveSID(int sid) {
        editor.putInt(KEY_FOR_LAST_SID, sid);
        editor.commit();
    }

    public void setAllowVisiblilityReportEditing(boolean value) {
        //editor.putBoolean(KEY_ALLOW_VISIBILITY_REPORT_EDITING, value);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.visiblityReportEditing.getName(),
                String.valueOf(value), String.valueOf(getAllowVisibilityReportEditing()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public boolean getAllowVisibilityReportEditing() {
        // return pref.getBoolean(KEY_ALLOW_VISIBILITY_REPORT_EDITING, false);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.VISIBLITY_REPORT_EDITING, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.visiblityReportEditing.getName()));


    }

    public void setPermissionEditReport(boolean value) {
        //  editor.putBoolean(KEY_ALLOW_OPERATOR_TO_EDIT_REPORT, value);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.PERMIT_OPERATOR_TO_EDIT_REPORT.getName(),
                String.valueOf(value), String.valueOf(getPermissionToEditReport()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public boolean getPermissionToEditReport() {
        //  return pref.getBoolean(KEY_ALLOW_OPERATOR_TO_EDIT_REPORT, false);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ALLOW_EDIT_REPORT, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.PERMIT_OPERATOR_TO_EDIT_REPORT.getName()));


    }

    public int getNumberShiftCanBeEdited() {
        //return pref.getInt(KEY_ALLOW_NUMBER_OF_CAN_BE_EDIT, 0);
//        int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT,
//                "0"));
//        return value;

        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.numberOfEditableShift.getName()));


    }

    public void setNumberShiftCanBeEdited(int value) {
        // editor.putInt(KEY_ALLOW_NUMBER_OF_CAN_BE_EDIT, value);
        //editor.commit();

        ConfigurationElement configurationElement =
                getConfigurationPushList(AppConfig.Key.numberOfEditableShift.getName(),
                        String.valueOf(value), String.valueOf(getNumberShiftCanBeEdited()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public boolean getAllowVisiblityForCanToggle() {
        // return pref.getBoolean(KEY_ALLOW_VISIBILITY_FOR_CANS_TOGGLE, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.VISIBILITY_CAN_TOGGLING, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.visibilityCanToggling.getName()));

    }

    //Changes for SmartCC

    // toggle b/n fill and empty can
    public void setAllowVisiblityForCanToggle(boolean value) {
        // editor.putBoolean(KEY_ALLOW_VISIBILITY_FOR_CANS_TOGGLE, value);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.visibilityCanToggling.getName(),
                String.valueOf(value), String.valueOf(getAllowVisiblityForCanToggle()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public boolean getCansToggling() {

        // return pref.getBoolean(KEY_ALLOW_CAN_TOGGLING, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ALLOW_CAN_TOGGLING, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.ALLOW_MILK_CAN_TOGGLING.getName()));


    }

    public void setCansToggling(boolean value) {
        //  editor.putBoolean(KEY_ALLOW_CAN_TOGGLING, value);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.ALLOW_MILK_CAN_TOGGLING.getName(),
                String.valueOf(value), String.valueOf(getCansToggling()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public int getMaxLimitEmptyCans() {
        //  return pref.getInt(KEY_MAX_LIMIT_EMPTY_CAN, 25);

//        int value = Integer.parseInt(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.MAX_LIMIT_EMPTY_CAN, String.valueOf(25)));
//        return value;

        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.maxLimitEmptyCan.getName()));


    }

    public void setMaxLimitEmptyCans(int value) {
        // editor.putInt(KEY_MAX_LIMIT_EMPTY_CAN, value);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.maxLimitEmptyCan.getName(),
                String.valueOf(value), String.valueOf(getMaxLimitEmptyCans()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public int getMinLimitFilledCans() {
        // return pref.getInt(KEY_MIN_LIMIT_FILLED_CAN, 10);

        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.minLimitFilledCan.getName()));
    }

    public void setMinLimitFilledCans(int value) {
        // editor.putInt(KEY_MIN_LIMIT_FILLED_CAN, value);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.minLimitFilledCan.getName(),
                String.valueOf(value), String.valueOf(getMinLimitFilledCans()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public String getLastAlertToggleState() {
        return pref.getString(KEY_EMPTY_OR_FILLED_CAN, "empty");
    }

    //Milk can toggle state
    public void setLastAlertToggleState(String filloremptycan) {
        editor.putString(KEY_EMPTY_OR_FILLED_CAN, filloremptycan);
        editor.commit();
    }

    public int getClrRoundOffUpto() {
        //return pref.getInt(KEY_CLR_ROUND_OFF, 1);

//        return Integer.parseInt(configProperties.getProperty(AppConfig.Key..getName()));    return Boolean.parseBoolean(configProperties.getProperty(AppConfig.Key..getName()));       boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.CLR_ROUND_OFF_UPTO, String.valueOf(1)));
//        return value;
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.CLR_ROUND_OFF_PRECISION.getName()));

    }

    public void setClrRoundOffUpto(int uptoDigit) {
        // editor.putInt(KEY_CLR_ROUND_OFF, uptoDigit);
        //editor.commit();


        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.CLR_ROUND_OFF_PRECISION.getName(),
                String.valueOf(uptoDigit), String.valueOf(getClrRoundOffUpto()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    // avoide peroidic httpcall for no data in update status
    public void setUpdatedRecordsStatus(boolean isSent) {
        editor.putBoolean(KEY_SENT_UPDATED_RECORDS, isSent);
        editor.commit();
    }

    public boolean getUpdatedRecordStatus() {
        return pref.getBoolean(KEY_SENT_UPDATED_RECORDS, false);
    }

    // for display data even though the configProperties screen setting is different
    public void setAllowFSCInSms(boolean allow) {
        //editor.putBoolean(KEY_ALLOW_FSC_IN_SMS, allow);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.INCLUDE_FSC_IN_SMS.getName(),
                String.valueOf(allow), String.valueOf(getAllowFSCInSMS()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public boolean getAllowFSCInSMS() {
        // return pref.getBoolean(KEY_ALLOW_FSC_IN_SMS, false);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ALLOW_FSC_IN_SMS, String.valueOf(false)));
//        return value;

        return Boolean.parseBoolean(configProperties.getProperty(AppConfig.Key.INCLUDE_FSC_IN_SMS.getName()));

    }

    public boolean getAllowFSCInPrint() {
        // return pref.getBoolean(KEY_ALLOW_FSC_IN_PRINT, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ALLOW_FSC_IN_PRINT, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.INCLUDE_FSC_IN_PRINT.getName()));

    }

    // for display data even though the configProperties screen setting is different
    public void setAllowFSCInPrint(boolean allow) {
//        editor.putBoolean(KEY_ALLOW_FSC_IN_PRINT, allow);
//        editor.commit();
//

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.INCLUDE_FSC_IN_PRINT.getName(),
                String.valueOf(allow), String.valueOf(getAllowFSCInPrint()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public void saveMA1Details(String name, int baudrate) {
        // editor.putString(KEY_MA1_NAME, name);
        //editor.putInt(KEY_MA1_BAUDRATE, baudrate);
        //editor.commit();
        ConfigurationElement configurationElementName = getConfigurationPushList(AppConfig.Key.ma1Name.getName(),
                String.valueOf(name), String.valueOf(getMa1Name()));
        databaseHandler.updateConfigEntity(configurationElementName, 0);

        ConfigurationElement configurationElementBaud = getConfigurationPushList(AppConfig.Key.ma1Baudrate.getName(),
                String.valueOf(baudrate), String.valueOf(getMa1Baudrate()));
        databaseHandler.updateConfigEntity(configurationElementBaud, 0);


    }

    public void saveMA2Details(String name, int baudrate) {
        //  editor.putString(KEY_MA2_NAME, name);
        // editor.putInt(KEY_MA2_BAUDRATE, baudrate);
        //editor.commit();

        ConfigurationElement configurationElementName = getConfigurationPushList(AppConfig.Key.ma2Name.getName(),
                String.valueOf(name), String.valueOf(getMa2Name()));
        databaseHandler.updateConfigEntity(configurationElementName, 0);

        ConfigurationElement configurationElementBaud = getConfigurationPushList(AppConfig.Key.ma2Baudrate.getName(),
                String.valueOf(baudrate), String.valueOf(getMa2Baudrate()));
        databaseHandler.updateConfigEntity(configurationElementBaud, 0);


    }

    public String getMa1Name() {
        //return pref.getString(KEY_MA1_NAME, "LACTOSCAN");
        return configProperties.getProperty(AppConfig.Key.ma1Name.getName());
    }

    public String getMa2Name() {
        // return pref.getString(KEY_MA2_NAME, "LACTOSCAN");
        return configProperties.getProperty(AppConfig.Key.ma2Name.getName());


    }

    public int getMa1Baudrate() {
        // return pref.getInt(KEY_MA1_BAUDRATE, 9600);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.ma1Baudrate.getName()));

    }

    public int getMa2Baudrate() {
        //  return pref.getInt(KEY_MA2_BAUDRATE, 9600);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.ma2Baudrate.getName()));
    }

    public boolean getMultipleMA() {

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.multipleMA.getName()));

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.MULTIPLE_MA, String.valueOf(false)));
//        return value;

        // return pref.getBoolean(KEY_SET_MULTIPLE_MA, false);
    }

    public void setMultipleMA(boolean isMultipleMa) {
        //  editor.putBoolean(KEY_SET_MULTIPLE_MA, isMultipleMa);
        // editor.commit();
        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.multipleMA.getName(),
                String.valueOf(isMultipleMa), String.valueOf(getMultipleMA()));
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public int getFirstMaSerialPort() {
        return pref.getInt(KEY_FIRST_MA_SERIAL_PORT, 1);
    }

    public void setFirstMaSerialPort(int fSport) {
        editor.putInt(KEY_FIRST_MA_SERIAL_PORT, fSport);
        editor.commit();

    }

    public int getSecondMASerialPort() {
        return pref.getInt(KEY_SECOND_MA_SERIAL_PORT, 1);
    }

    public void setSecondMASerialPort(int sSport) {
        editor.putInt(KEY_SECOND_MA_SERIAL_PORT, sSport);
        editor.commit();
    }

    public String getMA1PrevRecord() {
        return pref.getString(KEY_MA1_PREV_RECORD, null);
    }

    public void setMA1PrevRecord(String ma1Record) {
        editor.putString(KEY_MA1_PREV_RECORD, ma1Record);
        editor.commit();
    }

    public String getMA2PrevRecord() {
        return pref.getString(KEY_MA2_PREV_RECORD, null);
    }

    public void setMA2PrevRecord(String ma2Record) {
        editor.putString(KEY_MA2_PREV_RECORD, ma2Record);
        editor.commit();
    }

    public boolean getCheckboxMultipleMA() {
        return pref.getBoolean(KEY_SET_CHECKBOX_MULTIPLE_MA, false);
    }

    public void setCheckboxMultipleMA(boolean isMultipleMa) {
        editor.putBoolean(KEY_SET_CHECKBOX_MULTIPLE_MA, isMultipleMa);
        editor.commit();
    }

    public boolean getMyRateChartEnable() {
        // return pref.getBoolean(KEY_SET_MY_RATE_CHART, false);
       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.CUSTOM_MY_RATE_CHART, String.valueOf(false)));
        return value;*/

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.customMyRateChart.getName()));

    }

    public void setMyRateChartEnable(boolean isEnable) {
        // editor.putBoolean(KEY_SET_MY_RATE_CHART, isEnable);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.customMyRateChart.getName(),
                String.valueOf(isEnable), String.valueOf(getMyRateChartEnable()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public int getDbExceptionCounter() {
        return pref.getInt(KEY_DB_EXCEPTION_COUNTER, 0);
    }

    public void setDbExceptionCounter(int counter) {
        editor.putInt(KEY_DB_EXCEPTION_COUNTER, counter);
        editor.commit();
    }

    public boolean getAllowMaxLimitFromRateChart() {
        // return pref.getBoolean(KEY_ALLOW_MAX_LIMIT_RC, false);

       /* boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
                ConfigurationConstants.ALLOW_MAX_LIMIT_FROM_RC, String.valueOf(false)));
        return value;*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.ALLOW_MAX_LIMIT_FROM_RC.getName()));
    }

    public void setAllowMaxLimitFromRateChart(boolean value) {
        // editor.putBoolean(KEY_ALLOW_MAX_LIMIT_RC, value);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.ALLOW_MAX_LIMIT_FROM_RC.getName(),
                String.valueOf(value), String.valueOf(getAllowMaxLimitFromRateChart()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public long getLicRequestTime() {
        return pref.getLong(DatabaseHandler.KEY_LICENSE_REQUEST_DATE, System.currentTimeMillis());
    }

    // License
    public void setLicRequestTime(long longRequestTime) {
        editor.putLong(DatabaseHandler.KEY_LICENSE_REQUEST_DATE, longRequestTime);
        editor.commit();
    }

    public int getLicStatusCode() {
        return pref.getInt(DatabaseHandler.KEY_LICENSE_STATUS_CODE, 200);
    }

    public void setLicStatusCode(int statusCode) {
        editor.putInt(DatabaseHandler.KEY_LICENSE_STATUS_CODE, statusCode);
        editor.commit();
    }

    public long getLicStartDate() {
        return pref.getLong(DatabaseHandler.KEY_LICENSE_START_DATE, System.currentTimeMillis());
    }

    public void setLicStartDate(long longStartTime) {
        editor.putLong(DatabaseHandler.KEY_LICENSE_START_DATE, longStartTime);
        editor.commit();
    }

    public long getLicEndDate() {
        return pref.getLong(DatabaseHandler.KEY_LICENSE_END_DATE, System.currentTimeMillis());
    }

    public void setLicEndDate(long longEndTime) {
        editor.putLong(DatabaseHandler.KEY_LICENSE_END_DATE, longEndTime);
        editor.commit();
    }

    public void setLicVaidityStatus(String isValid) {
        editor.putString(DatabaseHandler.KEY_LICENSE_VALID, isValid);
        editor.commit();
    }

    public String getLicValidityStatus() {
        return pref.getString(DatabaseHandler.KEY_LICENSE_VALID, "YES");
    }

    public String getLicMessage() {
        return pref.getString(DatabaseHandler.KEY_LICENSE_MESSAGE, "Valid");
    }

    public void setLicMessage(String message) {
        editor.putString(DatabaseHandler.KEY_LICENSE_MESSAGE, message);
        editor.commit();
    }

    public boolean getFirstTimeLicenseEntry() {
        return pref.getBoolean(KEY_LICENSE, true);
    }

    public void setFirstTimeLicenseEntry(boolean status) {
        editor.putBoolean(KEY_LICENSE, status);
        editor.commit();
    }

    public boolean getSundayLicenseCheck() {
        return pref.getBoolean(KEY_SUN_LICENSE, false);
    }

    public void setSundayLicenseCheck(boolean status) {
        editor.putBoolean(KEY_SUN_LICENSE, status);
        editor.commit();
    }

    public void setMaparity(String parity) {
        // editor.putString(KEY_MA_PARITY, parity);
        //editor.commit();
        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.maParity.getName(), parity, getMaParity());
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public String getMaParity() {
        //  return pref.getString(KEY_MA_PARITY, "PARITY_NONE");
        return configProperties.getProperty(AppConfig.Key.maParity.getName());
        //
    }

    public String getMaDataBits() {
        // return pref.getString(KEY_MA_DATA_BITS, "DATA_BITS_8");
        return configProperties.getProperty(AppConfig.Key.maDatabits.getName());


    }

    public void setMaDataBits(String dataBits) {
        // editor.putString(KEY_MA_DATA_BITS, dataBits);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.maDatabits.getName(),
                dataBits, getMaDataBits());
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public String getMaStopBits() {
        //  return pref.getString(KEY_MA_STOP_BITS, "STOP_BITS_1");
        return configProperties.getProperty(AppConfig.Key.mastopbits.getName());
    }

    public void setMaStopBits(String stopBits) {
        // editor.putString(KEY_MA_STOP_BITS, stopBits);
        //editor.commit();
        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.mastopbits.getName(),
                stopBits, getMaStopBits());
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public void setRoundCheckFatAndSnf(boolean roundOffCheck) {
        //  editor.putBoolean(KEY_ALLOW_ROUND_OFF_FS, roundOffCheck);
        // editor.commit();
        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.IS_FAT_SNF_ROUND_OFF_ENABLED.getName(),
                String.valueOf(roundOffCheck), String.valueOf(getRoundOffCheckFatAndSnf()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getRoundOffCheckFatAndSnf() {
        // return pref.getBoolean(KEY_ALLOW_ROUND_OFF_FS, true);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ALLOW_ROUND_OFF_FAT_AND_SNF, String.valueOf(true)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.IS_FAT_SNF_ROUND_OFF_ENABLED.getName()));

    }

    public void setIpForServer(String IPAddress) {
        editor.putString(KEY_GET_IP_FOR_SERVER, IPAddress);
        editor.commit();
    }

    public String getIpAddress() {
        return pref.getString(KEY_GET_IP_FOR_SERVER, null);
    }

    public String getCriteriaSingleOrMultiple() {
        return pref.getString(KEY_SUPPORT_SINGLE_OR_MULTIPLE, "SINGLE");
    }

    public void setCriteriaSingleOrMultiple(String isMultiple) {
        editor.putString(KEY_SUPPORT_SINGLE_OR_MULTIPLE, isMultiple);
        editor.commit();
    }

    public int getClrPosition() {
        // return pref.getInt(KEY_SET_CLR_POSITION, 3);

        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.ekoMilkClrPosition.getName()));

    }

    public void setClrPosition(int clrPosition) {
        //editor.putInt(KEY_SET_CLR_POSITION, clrPosition);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.ekoMilkClrPosition.getName(),
                String.valueOf(clrPosition), String.valueOf(getClrPosition()));
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public int getAddedWaterPos() {
        // return pref.getInt(KEY_SET_ADDED_WATER_POSITION, 4);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.ekoMilkAddedWaterPosition.getName()));
    }

    public void setAddedWaterPos(int clrPosition) {
        //editor.putInt(KEY_SET_ADDED_WATER_POSITION, clrPosition);
        //editor.commit();
        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.ekoMilkAddedWaterPosition.getName(), String.valueOf(clrPosition), String.valueOf(getAddedWaterPos()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public boolean getonCreateDbCall() {
        return pref.getBoolean(KEY_DBONCREATE_LIC, false);
    }

    public void setonCreateDBCall(boolean status) {
        editor.putBoolean(KEY_DBONCREATE_LIC, status);
        editor.commit();
    }

    public void setMaxLimitOfWeight(double limit) {
        // editor.putFloat(KEY_MAX_LIMIT_WEIGHT, (float) limit);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.maxWeighingScaleLimit.getName(),
                String.valueOf(limit), String.valueOf(getMaxlimitOfWeight()));
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public double getMaxlimitOfWeight() {
        //  return pref.getFloat(KEY_MAX_LIMIT_WEIGHT, 200.0f);

//        double value = Double.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.MAX_WEIGHING_SCALE_LIMIT, String.valueOf(200.0f)));
//        return value;
        //
        return Double.valueOf(configProperties.getProperty(AppConfig.Key.maxWeighingScaleLimit.getName()));

    }

    public void setMobileDataEnable(boolean value) {
        //  editor.putBoolean(KEY_MOBILE_DATA_ENABLE, value);
        //editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.enableOrDisableIpTable.getName(), String.valueOf(value), String.valueOf(getMobileData()));
        databaseHandler.updateConfigEntity(cpl, 0);
    }

    public boolean getMobileData() {
        //   return pref.getBoolean(KEY_MOBILE_DATA_ENABLE, true);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ENABLE_OR_DISABLE_IP_TABLE, String.valueOf(true)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableOrDisableIpTable.getName()));

    }

    public boolean getOnUpgradeOrOnCreate() {
        return pref.getBoolean(KEY_IS_ONCREATE_ONUPGRADE_DB, false);
    }

    public void setOnUpgradeOrOnCreate(boolean onUponCr) {
        editor.putBoolean(KEY_IS_ONCREATE_ONUPGRADE_DB, onUponCr);
        editor.commit();
    }

    public void setCalledForSuday(boolean sundayCall) {
        editor.putBoolean("callForLICSunday", sundayCall);
        editor.commit();
    }

    public boolean getCallForSunday() {
        return pref.getBoolean("callForLICSunday", false);
    }

    public boolean getSmartCCFeature() {

        // return pref.getBoolean(KEY_SMARTCC_FEATURE, false);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.KEY_SMARTCC_FEATURE, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.smartCCFeature.getName()));

    }

    public void setSmartCCFeature(boolean smartCcfeature) {
        // editor.putBoolean(KEY_SMARTCC_FEATURE, smartCcfeature);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.smartCCFeature.getName(),
                String.valueOf(smartCcfeature),
                String.valueOf(getSmartCCFeature()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public boolean getMultipleUser() {
        //   return pref.getBoolean(KEY_CREATE_MULITIPLE_USER, false);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.CREATE_MULTIPLE_USERS, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.createMultipleUsers.getName()));

    }

    public void setMultipleUser(boolean createMultiple) {
        // editor.putBoolean(KEY_CREATE_MULITIPLE_USER, createMultiple);
        //editor.commit();
        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.createMultipleUsers.getName(), String.valueOf(createMultiple), String.valueOf(getMultipleUser()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public String getAgentUpdateUrl() {
        return pref.getString(KEY_SMARTCC_AGENT_URL, null);
    }

    public void setAgentUpdateUrl(String url) {
        editor.putString(KEY_SMARTCC_AGENT_URL, url);
        editor.commit();
    }

    public String getTruckUpdateUrl() {
        return pref.getString(KEY_SMARTCC_TRUCK_URL, null);
    }

    public void setTruckUpdateUrl(String url) {
        editor.putString(KEY_SMARTCC_TRUCK_URL, url);
        editor.commit();
    }

    public boolean getTareEnable() {


        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.ALLOW_WS_TARE.getName()));

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ALLOW_WS_TARE, String.valueOf(true)));
//        return value;

        //  return
        //  pref.getBoolean(KEY_SMARTCC_ENABLE_TARE, true);
    }

    public void setTareEnable(boolean tareEnable) {
        //   editor.putBoolean(KEY_SMARTCC_ENABLE_TARE, tareEnable);
        //  editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.ALLOW_WS_TARE.getName(), String.valueOf(tareEnable), String.valueOf(getTareEnable()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public long getTruckRecordsCount() {
        return pref.getLong(KEY_TRUCK_RECORDS_COUNT, 0);
    }

    public void setTruckRecordsCount(long count) {
        editor.putLong(KEY_TRUCK_RECORDS_COUNT, count);
        editor.commit();
    }

    public String getSourRateChartCow() {
        return pref.getString(KEY_SMARTCC_SOUR_RATE_COW, null);
    }

    public void setSourRateChartCow(String rateC) {
        editor.putString(KEY_SMARTCC_SOUR_RATE_COW, rateC);
        editor.commit();
    }

    public String getSourRateChartBuffalo() {
        return pref.getString(KEY_SMARTCC_SOUR_RATE_BUFFALO, null);
    }

    public void setSourRateChartBuffalo(String rateC) {
        editor.putString(KEY_SMARTCC_SOUR_RATE_BUFFALO, rateC);
        editor.commit();
    }

    public String getSourRateChartMixed() {
        return pref.getString(KEY_SMARTCC_SOUR_RATE_MIXED, null);
    }

    public void setSourRateChartMixed(String rateC) {
        editor.putString(KEY_SMARTCC_SOUR_RATE_MIXED, rateC);
        editor.commit();
    }

    public String getMaterialCode() {
        // return pref.getString(KEY_SMARTCC_MATERIAL_CODE, "2600004");

        return configProperties.getProperty(AppConfig.Key.materialCode.getName());

    }

    public void setMaterialCode(String materialCode) {
        // editor.putString(KEY_SMARTCC_MATERIAL_CODE, materialCode);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.materialCode.getName(),
                materialCode, String.valueOf(getMaterialCode()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public int getFarmerFailCount() {
        return pref.getInt(KEY_FARMER_FAIL_COUNT, 0);
    }

    public void setFarmerFailCount(int count) {
        editor.putInt(KEY_FARMER_FAIL_COUNT, count);
        editor.commit();

    }

    public int getAgentFailCount() {
        return pref.getInt(KEY_AGENT_FAIL_COUNT, 0);
    }

    public void setAgentFailCount(int count) {
        editor.putInt(KEY_AGENT_FAIL_COUNT, count);
        editor.commit();

    }

    public int getCenterFailCount() {
        return pref.getInt(KEY_CENTER_FAIL_COUNT, 0);
    }

    public void setCenterFailCount(int count) {
        editor.putInt(KEY_CENTER_FAIL_COUNT, count);
        editor.commit();

    }

    public int getTruckFailCount() {
        return pref.getInt(KEY_TRUCK_FAIL_COUNT, 0);
    }

    public void setTruckFailCount(int count) {
        editor.putInt(KEY_TRUCK_FAIL_COUNT, count);
        editor.commit();

    }

    public double getDeviationWeight() {
        //  return pref.getFloat(KEY_DEVIATION_WEIGHT, 0.100f);

//        double value = Double.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.TARE_DEVIATION_WEIGHT, String.valueOf(0.100f)));
//        return value;
        return Double.valueOf(configProperties.getProperty(AppConfig.Key.tareDeviationWeight.getName()));

    }

    public void setDeviationWeight(float deviationWeight) {
        //  editor.putFloat(KEY_DEVIATION_WEIGHT, deviationWeight);
        // editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.tareDeviationWeight.getName(), String.valueOf(deviationWeight), String.valueOf(getDeviationWeight()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public boolean getDeviationWeightAlert() {
        // return pref.getBoolean(KEY_DEVIATION_ALERT_WEIGHT, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.DEVIATION_ALERT_FOR_WEIGHT, String.valueOf(false)));
//        return value;
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.deviationAlertForWeight.getName()));


    }

    public void setDeviationWeightAlert(boolean isAlert) {
        // editor.putBoolean(KEY_DEVIATION_ALERT_WEIGHT, isAlert);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.deviationAlertForWeight.getName(), String.valueOf(isAlert), String.valueOf(getDeviationWeightAlert()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public boolean getRateCalculatedFromDevice() {
        // return pref.getBoolean(KEY_RATE_CALCULATED_DEVICE, true);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.IS_RATE_CALCULATED_FROM_DEVICE, String.valueOf(true)));
//        return value;
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.isRateCalculatedFromDevice.getName()));
    }

    public void setRateCalculatedFromDevice(boolean isRateCalculatedFromdevice) {
        // editor.putBoolean(KEY_RATE_CALCULATED_DEVICE, isRateCalculatedFromdevice);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.isRateCalculatedFromDevice.getName(), String.valueOf(isRateCalculatedFromdevice), String.valueOf(getRateCalculatedFromDevice()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public boolean getKeyEnableCollectionConstraints() {
        // return pref.getBoolean(KEY_ENABLE_COLLECTION_CONSTRAINTS, false);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.ENABLE_COLLECTION_CONTRAINTS, String.valueOf(false)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableCollectionContraints.getName()));


    }

    public void setKeyEnableCollectionConstraints(boolean value) {
        // editor.putBoolean(KEY_ENABLE_COLLECTION_CONSTRAINTS, value);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enableCollectionContraints.getName(),
                String.valueOf(value), String.valueOf(getKeyEnableCollectionConstraints()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public String getKeyCollEndEvnShift() {
        // return pref.getString(KEY_COLL_END_EVN_SHIFT, "22:00");

        return configProperties.getProperty(AppConfig.Key.COLLECTION_CENTER_EVENING_SHIFT_END_TIME.getName());

    }

    public void setKeyCollEndEvnShift(String time) {
        //  editor.putString(KEY_COLL_END_EVN_SHIFT, time);
        // editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.COLLECTION_CENTER_EVENING_SHIFT_END_TIME.getName(),

                time, getKeyCollEndEvnShift());
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public String getKeyCollStartEvnShift() {
        // return pref.getString(KEY_COLL_START_EVN_SHIFT, "16:00");

        return configProperties.getProperty(AppConfig.Key.COLLECTION_CENTER_EVENING_SHIFT_START_TIME.getName());

    }

    public void setKeyCollStartEvnShift(String time) {
        // editor.putString(KEY_COLL_START_EVN_SHIFT, time);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.COLLECTION_CENTER_EVENING_SHIFT_START_TIME.getName(),
                time, getKeyCollStartEvnShift());
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public String getKeyCollEndMrnShift() {
        // return pref.getString(KEY_COLL_END_MRN_SHIFT, "11:00");
        return configProperties.getProperty(AppConfig.Key.COLLECTION_CENTER_MORNING_SHIFT_END_TIME.getName());

    }

    public void setKeyCollEndMrnShift(String time) {
        // editor.putString(KEY_COLL_END_MRN_SHIFT, time);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.COLLECTION_CENTER_MORNING_SHIFT_END_TIME.getName(),
                time, getKeyCollEndMrnShift());
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public String getKeyCollStartMrnShift() {
        // return pref.getString(KEY_COLL_START_MRN_SHIFT, "05:00");
        return configProperties.getProperty(AppConfig.Key.COLLECTION_CENTER_MORNING_SHIFT_START_TIME.getName());
    }

    public void setKeyCollStartMrnShift(String time) {
        // editor.putString(KEY_COLL_START_MRN_SHIFT, time);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.COLLECTION_CENTER_MORNING_SHIFT_START_TIME.getName(),
                time, getKeyCollStartMrnShift());
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getKeyIgnoreZeroFatSnf() {
        //  return pref.getBoolean(KEY_IGNORE_ZERO_FAT_SNF, false);

//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.IGNORE_ZERO_FAT_SNF, String.valueOf(false)));
//        return value;
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.ignoreZeroFatSNF.getName()));

    }

    public void setKeyIgnoreZeroFatSnf(boolean value) {
        // editor.putBoolean(KEY_IGNORE_ZERO_FAT_SNF, value);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.ignoreZeroFatSNF.getName(),
                String.valueOf(value), String.valueOf(getKeyIgnoreZeroFatSnf()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public String getUserUri() {

        return pref.getString(KEY_USER_URI, null);
    }

    public void setUserUri(String userUri) {
        editor.putString(KEY_USER_URI, userUri);
        editor.commit();
    }

    public int getUserFailCount() {
        return pref.getInt(KEY_USER_FAIL_COUNT, 0);
    }

    public void setUserFailCount(int count) {
        editor.putInt(KEY_USER_FAIL_COUNT, count);
        editor.commit();

    }

    public boolean getKeyDisplayQuantity() {
        // return pref.getBoolean(KEY_DISPLAY_QUANTITY, true);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.IS_DISPLAY_QUANTITY, String.valueOf(true)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.isDisplayQuantity.getName()));

    }

    public void setKeyDisplayQuantity(boolean keyDisplayQuantity) {

        //editor.putBoolean(KEY_DISPLAY_QUANTITY, keyDisplayQuantity);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.isDisplayQuantity.getName(),
                String.valueOf(keyDisplayQuantity), String.valueOf(getKeyDisplayQuantity()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public boolean getKeyDisplayAmount() {
        // return pref.getBoolean(KEY_DISPLAY_AMOUNT, true);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.IS_DISPLAY_AMOUNT, String.valueOf(true)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.isDisplayAmount.getName()));

    }

    public void setKeyDisplayAmount(boolean keyDisplayAmount) {
        //  editor.putBoolean(KEY_DISPLAY_AMOUNT, keyDisplayAmount);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.isDisplayAmount.getName(),
                String.valueOf(keyDisplayAmount), String.valueOf(getKeyDisplayAmount()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public boolean getKeyDisplayRate() {
        //  return pref.getBoolean(KEY_DISPLAY_RATE, true);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.IS_DISPLAY_RATE, String.valueOf(true)));
//        return value;

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.isDisplayRate.getName()));

    }

    public void setKeyDisplayRate(boolean keyDisplayRate) {
        //  editor.putBoolean(KEY_DISPLAY_RATE, keyDisplayRate);
        //  editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.isDisplayRate.getName(),
                String.valueOf(keyDisplayRate), String.valueOf(getKeyDisplayRate()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    /**
     * If this is the case user don't want reject the milk, zero rate also milk will be accept,
     * And milk will be get reject based on the fat and snf
     *
     * @param value
     */

    public void setEnableRejectForRatechart(boolean value) {
        // editor.putBoolean(KEY_ENABLE_REJECT_RATECHART, value);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.enableRejectToDevice.getName(), String.valueOf(value), String.valueOf(getEnableRejectForRateChart()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }


    //For MA1 and MA2 configuration

    public boolean getEnableRejectForRateChart() {
        // return pref.getBoolean(KEY_ENABLE_REJECT_RATECHART, true);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.ENABLE_REJECT_RATECHART, String.valueOf(true)));
//        return value;
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableRejectToDevice.getName()));


    }

    public boolean getIsRateChartMandatory() {
        //return pref.getBoolean(KEY_MANDATORY_RATE_CHART, true);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MANDATORY_RATE_CHART, String.valueOf(true)));
//        return value;
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.rateChartMandatory.getName()));

    }

    public void setIsRateChartMandatory(boolean isRateChartMandatory) {
        //  editor.putBoolean(KEY_MANDATORY_RATE_CHART, isRateChartMandatory);
        // editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.rateChartMandatory.getName(), String.valueOf(isRateChartMandatory),
                String.valueOf(getIsRateChartMandatory()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMinFatRejectCow() {
        //return pref.getFloat(KEY_MIN_FAT_REJECT_COW, 0.0f);
//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MIN_FAT_REJECT_COW,
//                String.valueOf("0.0f")));
//        return value;

        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMinFatRejectCow.getName()));

    }

    public void setKeyMinFatRejectCow(float minFat) {
        // editor.putFloat(KEY_MIN_FAT_REJECT_COW, minFat);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMinFatRejectCow.getName(), String.valueOf(minFat),
                String.valueOf(getKeyMinFatRejectCow()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMinSnfRejectCow() {
        //  return pref.getFloat(KEY_MIN_SNF_REJECT_COW, 0.0f);

//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MIN_SNF_REJECT_COW,
//                String.valueOf("0.0f")));
//        return value;

        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMinSnfRejectCow.getName()));


    }

    public void setKeyMinSnfRejectCow(float minSnf) {
        // editor.putFloat(KEY_MIN_SNF_REJECT_COW, minSnf);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMinSnfRejectCow.getName(), String.valueOf(minSnf),
                String.valueOf(getKeyMinSnfRejectCow()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public float getKeyMaxLactoseRejectCow() {
        /*float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_COW,
                String.valueOf("6.0f")));
        return value;*/
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxLactoseRejectCow.getName()));
    }

    public float getKeyMaxFatRejectCow() {
        //  return pref.getFloat(KEY_MAX_FAT_REJECT_COW, 14.0f);
//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MAX_FAT_REJECT_COW,
//                String.valueOf("14.0f")));
//        return value;
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxFatRejectCow.getName()));

    }

    public void setKeyMaxFatRejectCow(float maxFat) {
        //editor.putFloat(KEY_MAX_FAT_REJECT_COW, maxFat);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMaxFatRejectCow.getName(), String.valueOf(maxFat),
                String.valueOf(getKeyMaxFatRejectCow()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public float getKeyMaxSnfRejectCow() {
        // return pref.getFloat(KEY_MAX_SNF_REJECT_COW, 14.0f);
//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MAX_SNF_REJECT_COW,
//                String.valueOf("14.0f")));
//        return value;

        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxSnfRejectCow.getName()));


    }

    public void setKeyMaxSnfRejectCow(float maxSnf) {
        // editor.putFloat(KEY_MAX_SNF_REJECT_COW, maxSnf);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMaxSnfRejectCow.getName(), String.valueOf(maxSnf),
                String.valueOf(getKeyMaxSnfRejectCow()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMinFatRejectBuff() {

        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMinFatRejectBuff.getName()));

        // return pref.getFloat(KEY_MIN_FAT_REJECT_BUFF, 0.0f);
//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MIN_FAT_REJECT_BUFF,
//                String.valueOf("0.0f")));
//        return value;

    }

    public void setKeyMinFatRejectBuff(float minFat) {
        // editor.putFloat(KEY_MIN_FAT_REJECT_BUFF, minFat);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMinFatRejectBuff.getName(), String.valueOf(minFat),
                String.valueOf(getKeyMinFatRejectBuff()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMinSnfRejectBuff() {
        // return pref.getFloat(KEY_MIN_SNF_REJECT_BUFF, 0.0f);
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMinSnfRejectBuff.getName()));

//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MIN_SNF_REJECT_BUFF,
//                String.valueOf("0.0f")));
//        return value;
    }

    public void setKeyMinSnfRejectBuff(float minSnf) {
        // editor.putFloat(KEY_MIN_SNF_REJECT_BUFF, minSnf);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMinSnfRejectBuff.getName(), String.valueOf(minSnf),
                String.valueOf(getKeyMinSnfRejectBuff()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMaxFatRejectBuff() {
        //  return pref.getFloat(KEY_MAX_FAT_REJECT_BUFF, 14.0f);
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxFatRejectBuff.getName()));

//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MAX_FAT_REJECT_BUFF,
//                String.valueOf("14.0f")));
//        return value;

    }

    public void setKeyMaxFatRejectBuff(float maxFat) {
        //  editor.putFloat(KEY_MAX_FAT_REJECT_BUFF, maxFat);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMaxFatRejectBuff.getName(), String.valueOf(maxFat),
                String.valueOf(getKeyMaxFatRejectBuff()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public float getKeyMaxSnfRejectBuff() {
        // return pref.getFloat(KEY_MAX_SNF_REJECT_BUFF, 14.0f);
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxSnfRejectBuff.getName()));

//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MAX_SNF_REJECT_BUFF,
//                String.valueOf("14.0f")));
//        return value;

    }

    public void setKeyMaxSnfRejectBuff(float maxSnf) {
        //editor.putFloat(KEY_MAX_SNF_REJECT_BUFF, maxSnf);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMaxSnfRejectBuff.getName(), String.valueOf(maxSnf),
                String.valueOf(getKeyMaxSnfRejectBuff()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMaxLactoseRejectBuff() {
        /*float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_BUFF,
                String.valueOf("6.0f")));
        return value;*/
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxLactoseRejectBuff.getName()));

    }

    public void setKeyMaxLactoseRejectBuff(float maxLactose) {
        ConfigurationElement configurationElement = getConfigurationPushList(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_BUFF, String.valueOf(maxLactose),
                String.valueOf(getKeyMaxLactoseRejectBuff()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMinFatRejectMix() {
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMinFatRejectMix.getName()));
        //  return pref.getFloat(KEY_MIN_FAT_REJECT_MIX, 0.0f);
//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MIN_FAT_REJECT_MIX,
//                String.valueOf("0.0f")));
//        return value;

    }

    public void setKeyMinFatRejectMix(float minFat) {
        // editor.putFloat(KEY_MIN_FAT_REJECT_MIX, minFat);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMinFatRejectMix.getName(), String.valueOf(minFat),
                String.valueOf(getKeyMinFatRejectMix()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMinSnfRejectMix() {
        // return pref.getFloat(KEY_MIN_SNF_REJECT_MIX, 0.0f);

        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMinSnfRejectMix.getName()));
//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MIN_SNF_REJECT_MIX,
//                String.valueOf("0.0f")));
//        return value;

    }

    public void setKeyMinSnfRejectMix(float minSnf) {
        //  editor.putFloat(KEY_MIN_SNF_REJECT_MIX, minSnf);
        // editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMinSnfRejectMix.getName(), String.valueOf(minSnf),
                String.valueOf(getKeyMinSnfRejectMix()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMaxFatRejectMix() {
        //  return pref.getFloat(KEY_MAX_FAT_REJECT_MIX, 14.0f);
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxFatRejectMix.getName()));

//        float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MAX_FAT_REJECT_MIX,
//                String.valueOf("14.0f")));
//        return value;

    }

    public void setKeyMaxFatRejectMix(float maxFat) {
        //  editor.putFloat(KEY_MAX_FAT_REJECT_MIX, maxFat);
        // editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMaxFatRejectMix.getName(), String.valueOf(maxFat),
                String.valueOf(getKeyMaxFatRejectMix()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMaxSnfRejectMix() {
        // return pref.getFloat(KEY_MAX_SNF_REJECT_MIX, 14.0f);
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxSnfRejectMix.getName()));
    }

    public void setKeyMaxSnfRejectMix(float maxSnf) {
        // editor.putFloat(KEY_MAX_SNF_REJECT_MIX, maxSnf);
        //editor.commit();
        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.keyMaxSnfRejectMix.getName(), String.valueOf(maxSnf),
                String.valueOf(getKeyMaxSnfRejectMix()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public float getKeyMaxLactoseRejectMix() {
        /*float value = Float.parseFloat(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_MIX,
                String.valueOf("6.0f")));
        return value;*/
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.keyMaxLactoseRejectMix.getName()));

    }

    public void setKeyMaxLactoseRejectMix(float maxLactose) {
        ConfigurationElement configurationElement = getConfigurationPushList(ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_MIX, String.valueOf(maxLactose),
                String.valueOf(getKeyMaxLactoseRejectMix()));
        databaseHandler.updateConfigEntity(configurationElement, 0);

    }

    public String getKeyDevicePort1() {
        return pref.getString(KEY_DEVICE_PORT1, DeviceName.MA1);
    }

    public void setKeyDevicePort1(String device) {
        editor.putString(KEY_DEVICE_PORT1, device);
        editor.commit();
    }

    public String getKeyDevicePort2() {
        return pref.getString(KEY_DEVICE_PORT2, DeviceName.NO_CONNECTION);
    }

    public void setKeyDevicePort2(String device) {
        editor.putString(KEY_DEVICE_PORT2, device);
        editor.commit();
    }

    public String getKeyDevicePort3() {
        return pref.getString(KEY_DEVICE_PORT3, DeviceName.RDU);
    }

    public void setKeyDevicePort3(String device) {
        editor.putString(KEY_DEVICE_PORT3, device);
        editor.commit();
    }

    public String getKeyDevicePort4() {
        return pref.getString(KEY_DEVICE_PORT4, DeviceName.WS);
    }

    public void setKeyDevicePort4(String device) {
        editor.putString(KEY_DEVICE_PORT4, device);
        editor.commit();
    }

    public String getKeyDevicePort5() {
        return pref.getString(KEY_DEVICE_PORT5, DeviceName.PRINTER);
    }

    public void setKeyDevicePort5(String device) {
        editor.putString(KEY_DEVICE_PORT5, device);
        editor.commit();
    }

    public void setMa1parityStopAndDataBits(String parity,
                                            String stopbits, String databits) {
//        editor.putString(KEY_MA1_PARITY, parity);
//        editor.putString(KEY_MA1_STOP_BITS, stopbits);
//        editor.putString(KEY_MA1_DATA_BITS, databits);
//        editor.commit();

        ConfigurationElement configurationPushListparity = getConfigurationPushList(AppConfig.Key.ma1Parity.getName(),
                parity, String.valueOf(getMa1Parity()));
        databaseHandler.updateConfigEntity(configurationPushListparity, 0);

        ConfigurationElement configurationPushListstopbits = getConfigurationPushList(
                AppConfig.Key.ma1stopbits.getName(),

                stopbits, String.valueOf(getMa1StopBits()));

        databaseHandler.updateConfigEntity(configurationPushListstopbits, 0);

        ConfigurationElement configurationPushListdatabits = getConfigurationPushList(AppConfig.Key.ma1Databits.getName(),
                databits, String.valueOf(getMa1DataBits()));
        databaseHandler.updateConfigEntity(configurationPushListdatabits, 0);


    }

    public String getMa1Parity() {
        // return pref.getString(KEY_MA1_PARITY, "PARITY_NONE");

        return configProperties.getProperty(AppConfig.Key.ma1Parity.getName());
//
    }

    public String getMa1DataBits() {

        //  return pref.getString(KEY_MA1_DATA_BITS, "DATA_BITS_8");

        return configProperties.getProperty(AppConfig.Key.ma1Databits.getName());
//

    }

    public String getMa1StopBits() {
        // return pref.getString(KEY_MA1_STOP_BITS, "STOP_BITS_1");
        return configProperties.getProperty(AppConfig.Key.ma1stopbits.getName());

    }

    public void setMa2parityStopAndDataBits(String parity,
                                            String stopbits, String databits) {
        //     editor.putString(KEY_MA2_PARITY, parity);
        //   editor.putString(KEY_MA2_STOP_BITS, stopbits);
        //  editor.putString(KEY_MA2_DATA_BITS, databits);
        // editor.commit();

        ConfigurationElement configurationPushListparity = getConfigurationPushList(AppConfig.Key.ma2Parity.getName(),
                parity, String.valueOf(getMa1Parity()));
        databaseHandler.updateConfigEntity(configurationPushListparity, 0);

        ConfigurationElement configurationPushListstopbits = getConfigurationPushList(AppConfig.Key.ma2stopbits.getName(),

                stopbits, String.valueOf(getMa1DataBits()));

        databaseHandler.updateConfigEntity(configurationPushListstopbits, 0);

        ConfigurationElement configurationPushListdatabits = getConfigurationPushList(AppConfig.Key.ma2Databits.getName(),
                databits, String.valueOf(getMa1StopBits()));
        databaseHandler.updateConfigEntity(configurationPushListdatabits, 0);

    }

    public String getMa2Parity() {
        //  return pref.getString(KEY_MA2_PARITY, "PARITY_NONE");
        return configProperties.getProperty(AppConfig.Key.ma2Parity.getName());

    }

    public String getMa2DataBits() {

        // return pref.getString(KEY_MA2_DATA_BITS, "DATA_BITS_8");
        return configProperties.getProperty(AppConfig.Key.ma2Databits.getName());
        // return value;


    }

    public String getMa2StopBits() {
        // return pref.getString(KEY_MA2_STOP_BITS, "STOP_BITS_1");
        return configProperties.getProperty(AppConfig.Key.ma2stopbits.getName());
    }

    public void setWSparityStopAndDataBits(String parity,
                                           String stopbits, String databits) {
        // editor.putString(KEY_WS_PARITY, parity);
        //editor.putString(KEY_WS_STOP_BITS, stopbits);
        //editor.putString(KEY_WS_DATA_BITS, databits);
        //editor.commit();
        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.weighingParity.getName(), parity, getWSParity());
        databaseHandler.updateConfigEntity(cplP, 0);
        ConfigurationElement cplS = getConfigurationPushList(AppConfig.Key.weighingStopbits.getName(), stopbits, getWSStopBits());
        databaseHandler.updateConfigEntity(cplS, 0);
        ConfigurationElement cplD = getConfigurationPushList(AppConfig.Key.weighingDatabits.getName(), databits, getWSDataBits());
        databaseHandler.updateConfigEntity(cplD, 0);
    }

    public String getWSParity() {
        //    return pref.getString(KEY_WS_PARITY, "PARITY_NONE");

        return configProperties.getProperty(AppConfig.Key.weighingParity.getName());


    }

    //Periodic Time to send data to the sever, time will be in second

    public String getWSDataBits() {

        //  return pref.getString(KEY_WS_DATA_BITS, "DATA_BITS_8");

//        return configProperties.getProperty(AppConfig.Key..getName());
       /* String value = databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.WEIGHING_DATBITS, "DATA_BITS_8");
        return value;*/
        return configProperties.getProperty(AppConfig.Key.weighingDatabits.getName());

    }

    public String getWSStopBits() {

        return configProperties.getProperty(AppConfig.Key.weighingStopbits.getName());

        // return pref.getString(KEY_WS_STOP_BITS, "STOP_BITS_1");
    }

    public void setPrinterParityStopAndDataBits(String parity,
                                                String stopbits, String databits) {
        editor.putString(KEY_PRINTER_PARITY, parity);
        editor.putString(KEY_PRINTER_STOP_BITS, stopbits);
        editor.putString(KEY_PRINTER_DATA_BITS, databits);
        editor.commit();
    }

    public String getPrinterParity() {
        return pref.getString(KEY_PRINTER_PARITY, "PARITY_NONE");
    }

    public String getPrinterDataBits() {
        return pref.getString(KEY_PRINTER_DATA_BITS, "DATA_BITS_8");
    }

    public String getPrinterStopBits() {
        return pref.getString(KEY_PRINTER_STOP_BITS, "STOP_BITS_1");
    }


    //Cloud support

    public void setRDUparityStopAndDataBits(String parity,
                                            String stopbits, String databits) {
        editor.putString(KEY_RDU_PARITY, parity);
        editor.putString(KEY_RDU_STOP_BITS, stopbits);
        editor.putString(KEY_RDU_DATA_BITS, databits);
        editor.commit();
    }

    public String getRDUParity() {
        return pref.getString(KEY_RDU_PARITY, "PARITY_NONE");
    }

    public String getRDUDataBits() {
        return pref.getString(KEY_RDU_DATA_BITS, "DATA_BITS_8");
    }

    public String getRDUStopBits() {
        return pref.getString(KEY_RDU_STOP_BITS, "STOP_BITS_1");
    }

    public boolean getEndShiftSuccess() {
        return pref.getBoolean(KEY_END_SHIFT_SUCCESS, false);
    }

    public void setEndShiftSuccess(boolean success) {
        editor.putBoolean(KEY_END_SHIFT_SUCCESS, success);
        editor.commit();
    }

    public boolean getRDUWeightRoundOff() {
        //  return pref.getBoolean(KEY_SET_RDU_WEIGHT_ROUND_OFF, true);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.RDU_QUANTITY_ROUND_OFF, String.valueOf(true)));
//        return value;
        return Boolean.parseBoolean(configProperties.getProperty(AppConfig.Key.rduQuantityRoundOff.getName()));
    }

    public void setRDUWeightRoundOff(boolean isRoundOff) {
        //   editor.putBoolean(KEY_SET_RDU_WEIGHT_ROUND_OFF, isRoundOff);
        // editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.rduQuantityRoundOff.getName(),
                String.valueOf(isRoundOff), String.valueOf(getRDUWeightRoundOff()));
        databaseHandler.updateConfigEntity(cpl, 0);

    }

    public void setSNFORCLRforTanker(String snfOrClr) {
        //editor.putString(KEY_SNF_OR_CLR_TANKER, snfOrClr);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.snfOrClrForTanker.getName(),
                snfOrClr, String.valueOf(getSNFOrCLRFromTanker()));
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public String getSNFOrCLRFromTanker() {

        // return pref.getString(KEY_SNF_OR_CLR_TANKER, "CLR");
        return configProperties.getProperty(AppConfig.Key.snfOrClrForTanker.getName());
    }

    public int getWeightRecordLenght() {
        //  return pref.getInt(KEY_WEIGHT_RECORD_LENGTH, 0);

        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.weightRecordLength.getName()));

    }

    public void setWeightRecordLenght(int length) {
        //  editor.putInt(KEY_WEIGHT_RECORD_LENGTH, length);
        // editor.commit();

        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.weightRecordLength.getName(), String.valueOf(length), String.valueOf(getWeightRecordLenght()));
        databaseHandler.updateConfigEntity(cpl, 0);


    }

    public int getKeyPeriodicDataSent() {
        //  return pref.getInt(KEY_PERIODIC_DATA_SENT, 3);

        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.periodicDataSent.getName()));

    }

    public void setKeyPeriodicDataSent(int time) {
        // editor.putInt(KEY_PERIODIC_DATA_SENT, time);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.periodicDataSent.getName(), String.valueOf(time), String.valueOf(getKeyPeriodicDataSent()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public int getKeyDataSentStartTime() {
        //  return pref.getInt(KEY_DATA_SENT_START_TIME, 5);

        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.startTimeDataSent.getName()));
    }

    public void setKeyDataSentStartTime(int time) {
        // editor.putInt(KEY_DATA_SENT_START_TIME, time);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.startTimeDataSent.getName(), String.valueOf(time), String.valueOf(getKeyDataSentStartTime()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public boolean getKeyCloudSupport() {

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.isCloudSupport.getName()));
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.IS_CLOUD_SUPPORT, String.valueOf(true)));
//        return value;
        ////     return pref.getBoolean(KEY_IS_CLOUD_SUPPORT, true);
    }

    public void setKeyCloudSupport(boolean isEnable) {
        //  editor.putBoolean(KEY_IS_CLOUD_SUPPORT, isEnable);
        // editor.commit();


        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.isCloudSupport.getName(), String.valueOf(isEnable), String.valueOf(getKeyCloudSupport()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public int getUnsentAlertLimit()

    {
        //  return pref.getInt(KEY_UNSENT_ALERT_LIMIT, 100);

        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.unsentAlertLimit.getName()));
    }

    public void setUnsentAlertLimit(int value) {
        //  editor.putInt(KEY_UNSENT_ALERT_LIMIT, value);
        // editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.unsentAlertLimit.getName(), String.valueOf(value), String.valueOf(getUnsentAlertLimit()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public boolean getShowUnsentAlert()

    {

        return pref.getBoolean(KEY_SHOW_UNSENT_ALERT, false);
    }

    public void setShowUnsentAlert(boolean value) {
        editor.putBoolean(KEY_SHOW_UNSENT_ALERT, value);
        editor.commit();
    }

//    public float getKeyMinValidWeight() {
//
//        return pref.getFloat(KEY_MIN_VALID_WEIGHT, 0.100f);
//    }


    public float getKeyMinValidWeight() {
        return Float.valueOf(configProperties.getProperty(AppConfig.Key.minValidWeightForCollection.getName()));
//        float value = Float.valueOf(databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_MIN_VALID_WEIGHT,
//                String.valueOf(0.100f)));
//        return value;
// return pref.getFloat(KEY_MIN_VALID_WEIGHT, 0.100f);
    }


    public void setKeyMinValidWeight(double value) {
        editor.putFloat(KEY_MIN_VALID_WEIGHT, (float) value);
    }

    public String getDefaultMilkTypeForBoth() {
        // return pref.getString(KEY_DEFAULT_MILK_TYPE_BOTH, CattleType.COW);
        return configProperties.getProperty(AppConfig.Key.defalutMilkTypeBoth.getName());
        // return value;

    }

    public void setDefaultMilkTypeForBoth(String milkType) {
        // editor.putString(KEY_DEFAULT_MILK_TYPE_BOTH, milkType);
        //editor.commit();

        ConfigurationElement configurationElement = getConfigurationPushList(AppConfig.Key.defalutMilkTypeBoth.getName(), milkType, getDefaultMilkTypeForBoth());
        databaseHandler.updateConfigEntity(configurationElement, 0);


    }

    public String getLicenceJson() {
        //  return pref.getString(KEY_LICENCE_JSON, null);

//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.LICENSE_JSON, null);
//        return value;
        return configProperties.getProperty(AppConfig.Key.licenceJson.getName());

    }

    public void setLicenceJson(String str) {
        //   editor.putString(KEY_LICENCE_JSON, str);
        // editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.licenceJson.getName(),
                String.valueOf(str), String.valueOf(getLicenceJson()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public int getDeleteCollRecordAfterShift() {
        return pref.getInt(KEY_DELETE_COLL_SHIFT_RECORD, 120);
    }

    public void setDeleteCollRecordAfterShift(int shifts) {
        editor.putInt(KEY_DELETE_COLL_SHIFT_RECORD, shifts);
        editor.commit();
    }

    public int getDeleteFilesAfterDays() {

        return pref.getInt(KEY_DELETE_FILES_DAY, 30);
    }

    public void setDeleteFilesAfterDays(int days) {
        editor.putInt(KEY_DELETE_FILES_DAY, days);
        editor.commit();
    }

    public boolean getKeyEscapeEnableCollection() {
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.escapeEnableForCollection.getName()));

        //return pref.getBoolean(KEY_ESCAPE_ENABLE_COLLECTION, false);
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.KEY_ESCAPE_ENABLE_COLLECTION,
//                String.valueOf(false)));
//        return value;
//
    }

    public void setKeyEscapeEnableCollection(boolean isEscapleEnable) {

        // editor.putBoolean(KEY_ESCAPE_ENABLE_COLLECTION, isEscapleEnable);
        //editor.commit();
        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.escapeEnableForCollection.getName(),
                String.valueOf(isEscapleEnable), String.valueOf(getKeyEscapeEnableCollection()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }


    public boolean getMergedHMBEnable() {

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.mergedHMBEnable.getName()));
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.MERGED_HMB_ENABLE, String.valueOf(false)));
//        return value;
//        return  pref.getBoolean(KEY_MERGED_HMB_ENABLE,false);
    }

    public void setMergedHMBEnable(boolean isEnable) {
        ConfigurationElement cplP = getConfigurationPushList(AppConfig.Key.mergedHMBEnable.getName(), String.valueOf(isEnable), String.valueOf(getMergedHMBEnable()));
        databaseHandler.updateConfigEntity(cplP, 0);
        /*editor.putBoolean(KEY_MERGED_HMB_ENABLE,isEnable);
        editor.commit();*/
    }

    public void setMAVisiblityParams(MAParam maparam) {

        editor.putBoolean(KEY_FAT_VISIBILITY, maparam.fat);
        editor.putBoolean(KEY_SNF_VISIBILITY, maparam.snf);
        editor.putBoolean(KEY_DEN_VISIBILITY, maparam.den);
        editor.putBoolean(KEY_LAC_VISIBILITY, maparam.lac);
        editor.putBoolean(KEY_TEMP_VISIBLITY, maparam.temp);
        editor.putBoolean(KEY_CAL_VISIBLITY, maparam.cal);
        editor.putBoolean(KEY_sALT_VISIBILITY, maparam.salt);
        editor.putBoolean(KEY_ADW_VISIBILITY, maparam.adw);
        editor.putBoolean(KEY_SN_VISIBILITY, maparam.sn);
        editor.putBoolean(KEY_FRP_VISIBILITY, maparam.frp);
        editor.putBoolean(KEY_PH_VISIBILITY, maparam.ph);
        editor.putBoolean(KEY_COM_VISIBILITY, maparam.com);
        editor.putBoolean(KEY_PRO_VISIBILITY, maparam.pro);

        editor.commit();
    }

    public boolean getFatVisiblity() {
        return pref.getBoolean(KEY_FAT_VISIBILITY, true);
    }

    public boolean getSnfVisiblity() {
        return pref.getBoolean(KEY_SNF_VISIBILITY, true);
    }

    public boolean getDenVisiblity() {
        return pref.getBoolean(KEY_DEN_VISIBILITY, true);
    }

    public boolean getLACVisiblity() {
        return pref.getBoolean(KEY_LAC_VISIBILITY, true);
    }

    public boolean getSaltVisiblity() {
        return pref.getBoolean(KEY_sALT_VISIBILITY, true);
    }

    public boolean getPROVisiblity() {
        return pref.getBoolean(KEY_PRO_VISIBILITY, true);
    }

    public boolean getTempVisiblity() {
        return pref.getBoolean(KEY_TEMP_VISIBLITY, true);
    }

    public boolean getAdwVisiblity() {
        return pref.getBoolean(KEY_ADW_VISIBILITY, true);
    }

    public boolean getFrpVisiblity() {
        return pref.getBoolean(KEY_FRP_VISIBILITY, true);
    }

    public boolean getCALVisiblity() {
        return pref.getBoolean(KEY_CAL_VISIBLITY, true);
    }

    public boolean getSNVisiblity() {
        return pref.getBoolean(KEY_SN_VISIBILITY, true);
    }

    public boolean getPHVisiblity() {
        return pref.getBoolean(KEY_PH_VISIBILITY, true);
    }

    public boolean getCoMVisiblity() {
        return pref.getBoolean(KEY_COM_VISIBILITY, true);
    }

    public boolean getSampleAsCollection() {

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.sampleAsCollection.getName()));
//        boolean value = Boolean.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE,
//                ConfigurationConstants.SAMPLE_AS_COLLECTION, String.valueOf(true)));
//        return value;
//        return pref.getBoolean(KEY_SAMPLE_AS_COLLECTION, true);
    }

    public void setSampleAsCollection(boolean sample) {
        ConfigurationElement cpl = getConfigurationPushList(AppConfig.Key.sampleAsCollection.getName(), String.valueOf(sample), String.valueOf(getSampleAsCollection()));
        databaseHandler.updateConfigEntity(cpl, 0);
        /*editor.putBoolean(KEY_SAMPLE_AS_COLLECTION, sample);
        editor.commit();*/

    }

/*    public int getNumberOfShiftEdited() {
        int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(CONFIG_TABLE, ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT, String.valueOf(0)));
        return value;
    }

    public void setNumberOfShiftEdited(int numberShift) {
        ConfigurationElement cplP = getConfigurationPushList(
                ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT,
                String.valueOf(numberShift), String.valueOf(getNumberOfShiftEdited()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }*/

    public void setAllowEditReport(boolean editReport) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.PERMIT_OPERATOR_TO_EDIT_REPORT.getName(),
                String.valueOf(editReport), String.valueOf(getAllowReportEdit()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getAllowReportEdit() {
//        boolean value = Boolean.parseBoolean(databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.ALLOW_EDIT_REPORT, String.valueOf(false)));
//        return value;
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.PERMIT_OPERATOR_TO_EDIT_REPORT.getName()));
    }

    public void setKeyManagerPassword(String managerPassword) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.managerPassword.getName(),
                managerPassword, String.valueOf(getManagerPassword()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public void setKeyOperatorPassword(String operatorPassword) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.operatorPassword.getName(),
                operatorPassword, String.valueOf(getOperatorPassword()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getManagerPassword() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_MANAGER_PASSWORD, "MANAGER");
//        return value;
        return configProperties.getProperty(AppConfig.Key.managerPassword.getName());
    }

    public String getOperatorPassword() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_OPERATOR_PASSWORD, "operator.smartamcu");
//        return value;
        return configProperties.getProperty(AppConfig.Key.operatorPassword.getName());
    }

    public void setKeyAllowFormerIncrement(boolean isEnableConsolidateReport) {
        // editor.putBoolean(KEY_ALLOW_FAMER_INCREMENT,isEnableConsolidateReport);
        //editor.commit();
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.keyAllowFormerIncrement.getName(),
                String.valueOf(isEnableConsolidateReport), String.valueOf(getKeyAllowFarmerIncrement()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public boolean getKeyAllowFarmerIncrement() {
        // return pref.getBoolean(KEY_ALLOW_FAMER_INCREMENT,false);

//        boolean value = Boolean.parseBoolean(databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ALLOW_FAMER_INCREMENT, String.valueOf(false)));
//        return value;
        return Boolean.parseBoolean(configProperties.getProperty(AppConfig.Key.keyAllowFormerIncrement.getName()));

    }

    public String getMorningStart() {
        // return pref.getString(KEY_MORNING_START, "00:00");

//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_MORNING_START, "00:00");
//        return value;

        return configProperties.getProperty(AppConfig.Key.morningSessionStartTime.getName());

    }

    public void setMorningStart(String type) {
        // editor.putString(KEY_MORNING_START, type);
        //editor.commit();

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.morningSessionStartTime.getName(),
                type, String.valueOf(getMorningStart()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public String getEveningStart() {

        return configProperties.getProperty(AppConfig.Key.eveningSessionStartTime.getName());

        // return pref.getString(KEY_EVENING_START, "15:00");
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_EVENING_START, "15:00");
//        return value;

    }

    public void setEveningStart(String type) {
        //  editor.putString(KEY_EVENING_START, type);
        // editor.commit();
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.eveningSessionStartTime.getName(),
                type, String.valueOf(getEveningStart()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }


    //Newly added parameters

    public String getMorningEndTime() {

//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_MORNING_END_TIME, "14:00");
//        return value;
        return configProperties.getProperty(AppConfig.Key.morningSessionEndTime.getName());


    }

    public void setMorningEndTime(String endTime) {

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.morningSessionEndTime.getName(),
                endTime, String.valueOf(getMorningEndTime()));
        databaseHandler.updateConfigEntity(cplP, 0);


    }

    public String getEveningEndTime() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_EVENING_END_TIME, "02:00");
//        return value;

        return configProperties.getProperty(AppConfig.Key.eveningSessionEndTime.getName());

    }

    public void setEveningEndTime(String endTime) {

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.eveningSessionEndTime.getName(),
                endTime, String.valueOf(getEveningEndTime()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public boolean getKeyAllowAgentFarmerCollection() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ALLOW_AGENT_SPLIT, "false");
//        return Boolean.parseBoolean(value);
        return Boolean.parseBoolean(configProperties.getProperty(AppConfig.Key.allowAgentSplit.getName()));

    }

    public void setKeyAllowAgentFarmerCollection(boolean allowAgent) {

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.allowAgentSplit.getName(),
                String.valueOf(allowAgent), String.valueOf(getKeyAllowAgentFarmerCollection()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public boolean getKeyAllowEditCollection() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.ALLOW_EDIT_COLLECTION, "false");
//        return Boolean.parseBoolean(value);
        return Boolean.parseBoolean(configProperties.getProperty(AppConfig.Key.allowEditCollection.getName()));

    }

    public void setKeyAllowEditCollection(boolean value) {


        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.allowEditCollection.getName(), String.valueOf(value),
                String.valueOf(getKeyAllowEditCollection()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }


//
//    public void setKeyAfterDecimalDigitLength(int digitLength)
//    {
//        ConfigurationElement cplP = getConfigurationPushList(
//                ConfigurationConstants.KEY_AFTER_DECIMAL_DIGIT_LENGHT,
//                String.valueOf(digitLength), String.valueOf(getKeyAfterDecimalDigitLength()));
//        databaseHandler.updateConfigEntity(cplP, 0);
//
//    }
//
//    public int getKeyAfterDecimalDigitLength()
//    {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_AFTER_DECIMAL_DIGIT_LENGHT, "1");
//        return Integer.parseInt(value);
//
//    }

    public boolean getKeyAllowProteinValue() {

       /* String value = databaseHandler.genericGetStringFromDatabase(
                CONFIG_TABLE, ConfigurationConstants.KEY_ALLOW_PROTEIN_VALUE, "false");
        return Boolean.parseBoolean(value);
*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.KeyAllowProtein.getName()));

    }

    public void setKeyAllowProteinValue(boolean allowProteinValue) {

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.KeyAllowProtein.getName(),
                String.valueOf(allowProteinValue), String.valueOf(getKeyAllowProteinValue()));
        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public String getInCentiveRateChartname() {
        //     String value = databaseHandler.genericGetStringFromDatabase(
        //           CONFIG_TABLE, ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME, null);
        return pref.getString(ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME, null);

    }

    public void setInCentiveRateChartname(String incentiveRateChartName) {

        editor.putString(ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME, incentiveRateChartName);
        editor.commit();

//        ConfigurationElement cplP = getConfigurationPushList(
//                ConfigurationConstants.KEY_ALLOW_INCENTIVE_RATE_CHART_NAME,
//                incentiveRateChartName, String.valueOf(getInCentiveRateChartname()));
//        databaseHandler.updateConfigEntity(cplP, 0);

    }

    public boolean getKeyAllowTwoDeciaml() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ALLOW_TWO_DECIMAL, "false");
//        return Boolean.parseBoolean(value);
        return Boolean.parseBoolean(configProperties.getProperty(AppConfig.Key.allowTwoDecimal.getName()));


    }

    public void setKeyAllowTwoDeciaml(boolean allowProteinValue) {

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.allowTwoDecimal.getName(),
                String.valueOf(allowProteinValue), String.valueOf(getKeyAllowTwoDeciaml()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }


//All quality params display parameters, rate params, and Rounding up/down

    //Display two digit

    //Display configuration (Display the same value on TAB/Receipt/Collection/SMS/Report/RDU(if possible)
    //Rate chart Configuration( To pick the rate from the rate chart)/
    //Rounding UP/DOWN

    public int getDisplayFATConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_DISPLAY_FAT_CONFIGURATION, "1");
//        return Integer.parseInt(value);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.displayFATConfiguration.getName()));


    }

    public void setDisplayFATConfiguration(int displayFATConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.displayFATConfiguration.getName(), String.valueOf(displayFATConfiguration),
                String.valueOf(getDisplayFATConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public int getDisplaySNFConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_DISPLAY_SNF_CONFIGURATION, "1");
//        return Integer.parseInt(value);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.displaySNFConfiguration.getName()));
    }

    public void setDisplaySNFConfiguration(int displaySNFConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.displaySNFConfiguration.getName(), String.valueOf(displaySNFConfiguration),
                String.valueOf(getDisplaySNFConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public int getDisplayCLRConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_DISPLAY_CLR_CONFIGURATION, "1");
//        return Integer.parseInt(value);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.displayCLRConfiguration.getName()));
    }

    public void setDisplayCLRConfiguration(int displayCLRConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.displayCLRConfiguration.getName(), String.valueOf(displayCLRConfiguration),
                String.valueOf(getDisplayCLRConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public int getDisplayProteinConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_DISPLAY_PROTEIN_CONFIGURATION, "2");
//        return Integer.parseInt(value);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.displayProteinConfiguration.getName()));
    }

    public void setDisplayProteinConfiguration(int displayProteinConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.displayProteinConfiguration.getName(), String.valueOf(displayProteinConfiguration),
                String.valueOf(getDisplayProteinConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public int getRateFATConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_RATE_FAT_CONFIGURATION, "1");
//        return Integer.parseInt(value);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.rateFATConfiguration.getName()));
    }

    public void setRateFATConfiguration(int rateFATConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.rateFATConfiguration.getName(), String.valueOf(rateFATConfiguration),
                String.valueOf(getRateFATConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public int getRateSNFConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_RATE_SNF_CONFIGURATION, "1");
//        return Integer.parseInt(value);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.rateSNFConfiguration.getName()));
    }

    public void setRateSNFConfiguration(int rateSNFConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.rateSNFConfiguration.getName(), String.valueOf(rateSNFConfiguration),
                String.valueOf(getRateSNFConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public int getRateCLRConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_RATE_CLR_CONFIGURATION, "1");
//        return Integer.parseInt(value);
        return Integer.parseInt(configProperties.getProperty(AppConfig.Key.rateCLRConfiguration.getName()));
    }

    public void setRateCLRConfiguration(String rateCLRConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.rateCLRConfiguration.getName(), String.valueOf(rateCLRConfiguration),
                String.valueOf(getRateCLRConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public int getRateProteinConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_RATE_PROTEIN_CONFIGURATION, "2");
//        return Integer.parseInt(value);
        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.rateProteinConfiguration.getName()));
    }

    public void setRateProteinConfiguration(String rateProteinConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.rateProteinConfiguration.getName(),
                String.valueOf(rateProteinConfiguration),
                String.valueOf(getRateProteinConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getRoundFATConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ROUND_FAT_CONFIGURATION, "HALF_UP");
//        return value;
        return configProperties.getProperty(AppConfig.Key.roundFATConfiguration.getName());
    }

    public void setRoundFATConfiguration(String roundFATConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.roundFATConfiguration.getName(), String.valueOf(roundFATConfiguration),
                String.valueOf(getRoundFATConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getRoundSNFConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ROUND_SNF_CONFIGURATION, "HALF_UP");
//        return value;
        return configProperties.getProperty(AppConfig.Key.roundSNFConfiguration.getName());
    }

    public void setRoundSNFConfiguration(String roundSNFConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.roundSNFConfiguration.getName(), String.valueOf(roundSNFConfiguration),
                String.valueOf(getRoundSNFConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getRoundCLRConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ROUND_CLR_CONFIGURATION, "HALF_UP");
//        return value;
        return configProperties.getProperty(AppConfig.Key.roundCLRConfiguration.getName());

    }

    public void setRoundCLRConfiguration(String roundCLRConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.roundCLRConfiguration.getName(), String.valueOf(roundCLRConfiguration),
                String.valueOf(getRoundCLRConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getRoundProteinConfiguration() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ROUND_PROTEIN_CONFIGURATION, "HALF_UP");
//        return value;
        return configProperties.getProperty(AppConfig.Key.roundProteinConfiguration.getName());

    }

    public void setRoundProteinConfiguration(String roundProteinConfiguration) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.roundProteinConfiguration.getName(), String.valueOf(roundProteinConfiguration),
                String.valueOf(getRoundProteinConfiguration()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getRduPassword() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_RDU_PASSWORD, "1111");
//        return value;
        return configProperties.getProperty(AppConfig.Key.rduPassword.getName());

    }

    public void setRduPassword(String rduPassword) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.rduPassword.getName(), String.valueOf(rduPassword),
                String.valueOf(getRduPassword()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getHotspotSsid() {
        return configProperties.getProperty(AppConfig.Key.hotspotSsid.getName());
    }

    public void setHotspotSsid(String ssid) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.hotspotSsid.getName(), String.valueOf(ssid),
                String.valueOf(getHotspotSsid()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getPreviousHotspotSsid() {
        return configProperties.getProperty(AppConfig.Key.previousHotspotSsid.getName());
    }

    public void setPreviousHotspotSsid(String ssid) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.previousHotspotSsid.getName(), ssid,
                String.valueOf(getPreviousHotspotSsid()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getHotspotValue() {
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableHotspot.getName()));
    }

    public void setHotspotValue(boolean value) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.enableHotspot.getName(), String.valueOf(value),
                String.valueOf(getHotspotValue()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getWifiValue() {
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableWifi.getName()));
    }

    public void setWifiValue(boolean value) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.enableWifi.getName(), String.valueOf(value),
                String.valueOf(getWifiValue()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getDisableManualForDispatchValue() {
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.disableManualForDispatch.getName()));
    }

    public void setDisableManualForDispatchValue(boolean value) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.disableManualForDispatch.getName(), String.valueOf(value),
                String.valueOf(getDisableManualForDispatchValue()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getDispatchValue() {
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableDispatchReport.getName()));
    }

    public void setDispatchValue(boolean value) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.enableDispatchReport.getName(), String.valueOf(value),
                String.valueOf(getDispatchValue()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public int getWsIgnoreThreshold() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_WS_IGNORE_THRESHOLD, "100");
//        return Integer.valueOf(value);
        try {
            return Integer.valueOf(configProperties.getProperty(AppConfig.Key.wsIgnoreThreshold.getName()));
        } catch (Exception e) {
            return 50;
        }

    }

    public void setWsIgnoreThreshold(String wsIgnoreThreshold) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.wsIgnoreThreshold.getName(), String.valueOf(wsIgnoreThreshold),
                String.valueOf(getWsIgnoreThreshold()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getLactoseBasedRejection() {
        /*String value = databaseHandler.genericGetStringFromDatabase(
                CONFIG_TABLE, ConfigurationConstants.ENABLE_LACTOSE_BASED_REJECTION, String.valueOf(false));
        return value;*/
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.enableLactoseBasedRejection.getName()));
    }

    public void setLactoseBasedRejection(boolean value) {
        ConfigurationElement cplP = getConfigurationPushList(
                ConfigurationConstants.ENABLE_LACTOSE_BASED_REJECTION, String.valueOf(value),
                String.valueOf(getLactoseBasedRejection()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getApkFromFileSystem() {
        String value = databaseHandler.genericGetStringFromDatabase(
                CONFIG_TABLE, ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM, String.valueOf(false));
        return Boolean.parseBoolean(value);
    }

    public void setApkFromFileSystem(boolean value) {
        ConfigurationElement cplP = getConfigurationPushList(
                ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM, String.valueOf(value),
                String.valueOf(getApkFromFileSystem()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getWsKgCommand() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_WS_KG_COMMAND, "K");
//        return value;

        return configProperties.getProperty(AppConfig.Key.rduBaudrate.getName());

    }

    public void setWsKgCommand(String wsKgCommand) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.rduBaudrate.getName(), String.valueOf(wsKgCommand),
                String.valueOf(getWsKgCommand()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }


    public String getWsLitreCommand() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_WS_LITRE_COMMAND, "L");
//        return value;
        return configProperties.getProperty(AppConfig.Key.wsLitreCommand.getName());

    }

    public void setWsLitreCommand(String wsLtrCommand) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.wsLitreCommand.getName(), String.valueOf(wsLtrCommand),
                String.valueOf(getWsLitreCommand()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getIncentiveRateChartForCow() {
        return pref.getString(KEY_SET_INCETIVE_RATECHART_COW, null);
    }

    public void setIncentiveRateChartForCow(String name) {
        editor.putString(KEY_SET_INCETIVE_RATECHART_COW, name);
        editor.commit();
    }

    public String getIncentiveRateChartForBuffalo() {
        return pref.getString(KEY_SET_INCETIVE_RATECHART_BUFFALO, null);
    }

    public void setIncentiveRateChartForBuffalo(String name) {
        editor.putString(KEY_SET_INCETIVE_RATECHART_BUFFALO, name);
        editor.commit();
    }

    public String getIncentiveRateChartForMixed() {
        return pref.getString(KEY_SET_INCETIVE_RATECHART_MIXED, null);
    }

    public void setRateIncentiveChartForMixed(String name) {
        editor.putString(KEY_SET_INCETIVE_RATECHART_MIXED, name);
        editor.commit();
    }


    public int getPeriodicDeviceDataSend() {
        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.periodicDeviceDataSend.getName()));

//        int value = Integer.valueOf(databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.PERIODIC_DEVICE_DATA_SEND, String.valueOf(3)));
//        return value;
    }

    public void setPeriodicDeviceDataSend(int PERIODIC_DEVICE_DATA_SEND) {
        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.periodicDeviceDataSend.getName(), String.valueOf(
                        PERIODIC_DEVICE_DATA_SEND), String.valueOf(getPeriodicDeviceDataSend()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getKeyAllowDisplayRate() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ALLOW_DISPLAY_RATE, "true");
//        return Boolean.parseBoolean(value);
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.allowDisplayRate.getName()));


    }

    public void setKeyAllowDisplayRate(boolean allowDisplayRate) {

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.allowDisplayRate.getName(),
                String.valueOf(allowDisplayRate), String.valueOf(getKeyAllowDisplayRate()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getKeyAllowFarmerCreate() {
//        String value = databaseHandler.genericGetStringFromDatabase(
//                CONFIG_TABLE, ConfigurationConstants.KEY_ALLOW_FARMER_CREATE, "true");
//        return Boolean.parseBoolean(value);

        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.allowFamerCreate.getName()));
    }

    public void setKeyAllowFarmerCreate(boolean allowFarmerCreate) {

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.allowFamerCreate.getName(),
                String.valueOf(allowFarmerCreate), String.valueOf(getKeyAllowFarmerCreate()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getKeyAllowFarmerEdit() {
        return Boolean.valueOf(configProperties.getProperty(AppConfig.Key.allowFamerEdit.getName()));
    }

    public void setKeyAllowFarmerEdit(boolean allowFarmerEdit) {

        ConfigurationElement cplP = getConfigurationPushList(
                AppConfig.Key.allowFamerEdit.getName(),
                String.valueOf(allowFarmerEdit), String.valueOf(getKeyAllowFarmerEdit()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public boolean getDisplayNameInReport() {
        String value = databaseHandler.genericGetStringFromDatabase(
                CONFIG_TABLE, ConfigurationConstants.KEY_ALLOW_DISPLAY_NAME_IN_REPORT, "false");
        return Boolean.parseBoolean(value);

    }

    public void setDisplayNameInReport(boolean allowFarmerCreate) {

        ConfigurationElement cplP = getConfigurationPushList(
                ConfigurationConstants.KEY_ALLOW_DISPLAY_NAME_IN_REPORT,
                String.valueOf(allowFarmerCreate), String.valueOf(getDisplayNameInReport()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getDisplayDairyName() {
        String value = databaseHandler.genericGetStringFromDatabase(
                CONFIG_TABLE, ConfigurationConstants.KEY_DISPLAY_DAIRY_NAME, "");
        return value;

    }

    public void setDisplayDairyName(String dairyName) {

        ConfigurationElement cplP = getConfigurationPushList(
                ConfigurationConstants.KEY_DISPLAY_DAIRY_NAME,
                dairyName, String.valueOf(getDisplayDairyName()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public ConfigurationElement getConfigurationPushList(String key, String value, String oldValue) {
        ConfigurationElement cpl = new ConfigurationElement();
        cpl.key = key;
        cpl.value = value;
        cpl.status = DatabaseEntity.FARMER_UNSENT_CODE;
        cpl.userName = new SessionManager(_context).getUserId();
        if (oldValue == null) {
            cpl.lastValue = value;
        } else {
            cpl.lastValue = oldValue;
        }
        cpl.modifiedDate = java.util.Calendar.getInstance().getTimeInMillis();
        configProperties.put(key, value);
        return cpl;

    }


    public String getDynamicRateChartValue() {
        String value = databaseHandler.genericGetStringFromDatabase(
                CONFIG_TABLE, ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART, String.valueOf(false));
        return value;
    }

    public void setDynamicRateChartValue(boolean value) {
        ConfigurationElement cplP = getConfigurationPushList(
                ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART, String.valueOf(value),
                String.valueOf(getDynamicRateChartValue()));
        databaseHandler.updateConfigEntity(cplP, 0);
    }

    public String getDynamicRateChartCow() {
        return pref.getString(ConfigurationConstants.KEY_SET_DYNAMIC_RATECHART_COW, null);
    }

    public void setDynamicRateChartCow(String rateC) {
        editor.putString(ConfigurationConstants.KEY_SET_DYNAMIC_RATECHART_COW, rateC);
        editor.commit();
    }

    public String getDynamicRateChartBuffalo() {
        return pref.getString(ConfigurationConstants.KEY_SET_DYNAMIC_RATECHART_BUFFALO, null);
    }

    public void setDynamicRateChartBuffalo(String rateC) {
        editor.putString(ConfigurationConstants.KEY_SET_DYNAMIC_RATECHART_BUFFALO, rateC);
        editor.commit();
    }

    public String getDynamicRateChartMixed() {
        return pref.getString(ConfigurationConstants.KEY_SET_DYNAMIC_RATECHART_MIXED, null);
    }

    public void setDynamicRateChartMixed(String rateC) {
        editor.putString(ConfigurationConstants.KEY_SET_DYNAMIC_RATECHART_MIXED, rateC);
        editor.commit();
    }

    public int getWeightReadRoundOff() {
        return Integer.valueOf(configProperties.getProperty(AppConfig.Key.weightReadRoundOff.getName()));

    }

    public void setWeightReadRoundOff(int roundOff) {
        ConfigurationElement configurationElement = getConfigurationPushList(
                AppConfig.Key.weightReadRoundOff.getName(), String.valueOf(roundOff),
                String.valueOf(getDecimalRoundOffWeigh()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
    }

    public String getWeightReadRoundingMode() {
        return String.valueOf(configProperties.getProperty(AppConfig.Key.weightReadRoundingMode.getName()));

    }

    public void setWeightReadRoundingMode(String mode) {
        ConfigurationElement configurationElement = getConfigurationPushList(
                AppConfig.Key.weightReadRoundingMode.getName(), String.valueOf(mode),
                String.valueOf(getDecimalRoundOffWeigh()));
        databaseHandler.updateConfigEntity(configurationElement, 0);
    }


}

