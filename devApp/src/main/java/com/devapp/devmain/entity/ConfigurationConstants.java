package com.devapp.devmain.entity;

import android.content.Context;
import android.content.SharedPreferences;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.helper.UserType;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.UserDetails;

/**
 * Created by Upendra on 8/26/2015.
 */
public class ConfigurationConstants {

    public static final String PREF_NAME = "permaPref";

    // Sharedpref file name
    public static final String SESSION_PREF_NAME = "LoginPref";
    //Saving this data in session Manager
//    public static final String morningSessionStartTime = "morningSessionStartTime";
//    public static final String eveningSessionStartTime = "eveningSessionStartTime";
//    public static final String morningSessionEndTime = "morningSessionEndTime";
//    public static final String eveningSessionEndTime = "eveningSessionEndTime";
    public static final String perDaySMSLimit = "perDaySMSLimit";
    public static final String SECOND_MILKANALYSER = "secondMAType";
    public static final String SECOND_MILKANALYSER_BAUDRATE = "secondMABaudrate";
    public static final String KEY_WEIGHING_BAUDRATE = "WeighingBaudrate";
    public static final String TEMP_SIM_PIN = "simlockPassword";
    public static final String ENABLE_REJECT_RATECHART = "enableRejectToDevice";
    public static final String SALES_ENABLE_FS = "enableSalesFS";
    public static final String SECOND_MA_BAUDRATE = "secondMABaudrate";
    //Saving this data in session Manager
//    public static final String MORNING_SESSION_START_TIME = "morningSessionStartTime";
//    public static final String EVENING_SESSION_START_TIME = "eveningSessionStartTime";
//    public static final String MORNING_SESSION_END_TIME = "morningSessionEndTime";
//    public static final String EVENING_SESSION_END_TIME = "eveningSessionEndTime";
    public static final String PER_DAY_SMS_LIMIT = "perDaySMSLimit";
    public static final String MILK_TYPE = "milkType";
    public static final String RATE_CHART = "rateChart";
    public static final String SECOND_MA_TYPE = "secondMAType";
    public static final String WHITE_LIST_URL = "whiteListURL";
    public static final String DEVICE_ID = "deviceId";
    public static final String PERIODIC_DATA_SENT = "periodicDataSent";
    public static final String START_TIME_DATA_SENT = "startTimeDataSent";
    public static final String MA_PARITY = "maParity";
    public static final String MA_STOPBITS = "mastopbits";
    public static final String MA_DATABITS = "maDatabits";
    public static final String KEY_RATECHART_NAME = "rateChart";
    public static final String TEMPORARY_SERVER = "tempServer";
    // Clr round off
    public static final String CLR_ROUND_OFF_UPTO = "clrRoundOffUpto";
    // overruling the config setting
    public static final String ALLOW_FSC_IN_SMS = "allowFSCinSMS";
    public static final String ALLOW_FSC_IN_PRINT = "allowFSCinPrint";
    public static final String MULTIPLE_MA = "multipleMA";
    public static final String CUSTOM_MY_RATE_CHART = "customMyRateChart";
    public static final String ALLOW_MAX_LIMIT_FROM_RC = "allowMaxLimitFromRC";
    public static final String ALLOW_ROUND_OFF_FAT_AND_SNF = "allowRoundOffFatAndSnf";
    public static final String EKO_MILK_CLR_POSITION = "ekoMilkClrPosition";
    public static final String EKO_MILK_ADDED_WATER_POSITION = "ekoMilkAddedWaterPosition";
    public static final String SMART_CC_FEATURE = "smartCCFeature";
    public static final String ALLOW_WS_TARE = "allowWSTare";
    public static final String CREATE_MULTIPLE_USERS = "createMultipleUsers";
    public static final String MATERIAL_CODE = "materialCode";
    public static final String TARE_DEVIATION_WEIGHT = "tareDeviationWeight";
    public static final String DEVIATION_ALERT_FOR_WEIGHT = "deviationAlertForWeight";
    public static final String IS_RATE_CALCULATED_FROM_DEVICE = "isRateCalculatedFromDevice";
    //Milk lane phase 2 requirement
    //Milk lane phase 2 requirement
    public static final String IGNORE_ZERO_FAT_SNF = "ignoreZeroFatSNF";
    public static final String ENABLE_COLLECTION_CONTRAINTS = "enableCollectionContraints";
    public static final String COLLECTION_START_MORNINGSHIFT = "collectionStartMorningshift";
    public static final String COLLECTION_END_MORNING_SHIFT = "collectionEndMorningShift";
    public static final String COLLECTION_START_EVENING_SHIFT = "collectionStartEveningShift";
    public static final String COLLECTION_END_EVENING_SHIFT = "collectionEndEveningShift";
    public static final String IS_DISPLAY_AMOUNT = "isDisplayAmount";
    public static final String IS_DISPLAY_QUANTITY = "isDisplayQuantity";
    public static final String IS_DISPLAY_RATE = "isDisplayRate";
    public static final String USER_CONFIG = "userConfig";
    public static final String WEIGHING_PARITY = "weighingParity";
    public static final String WEIGHING_DATBITS = "weighingDatbits";
    public static final String WEIGHING_STOPBITS = "weighingStopbits";
    public static final String ENABLE_OR_DISABLE_IP_TABLE = "enableOrDisableIpTable";
    public static final String MANAGER_PASSWORD = "managerPassword";
    public static final String MANAGER_MOBILE_NUMBER = "managerMobileNumber";
    public static final String MANAGER_EMAIL_ID = "managerEmailID";
    public static final String OPERATOR_PASSWORD = "operatorPassword";
    public static final String OPERATOR_MOBILE_NUMBER = "operatorMobileNumber";
    public static final String CENTER_NAME = "centerName";
    public static final String CENTER_CODE = "centerCode";
    public static final String CENTER_LOCATION = "centerLocation";
    public static final String CENTER_ROUTE = "centerRoute";
    public static final String CENTER_BMCID = "centerBMCID";
    public static final String CENTER_ADDRESS = "centerAddress";
    public static final String CENTER_CONTACT_PERSON_1 = "centerContactPerson1";
    public static final String CENTER_CONTACT_PERSON_2 = "centerContactPerson2";
    public static final String CENTER_CONTACT_EMAIL_1 = "centerContactEmail1";
    public static final String CENTER_CONTACT_EMAIL_2 = "centerContactEmail2";
    public static final String CENTER_CONTACT_MOBILE_1 = "centerContactMobile1";
    public static final String CENTER_CONTACT_MOBILE_2 = "centerContactMobile2";
    public static final String MAX_WEIGHING_SCALE_LIMIT = "maxWeighingScaleLimit";
    public static final String WEIGHING_BAUDRATE = "WeighingBaudrate";
    public static final String RDU_QUANTITY_ROUND_OFF = "rduQuantityRoundOff";
    public static final String SNF_OR_CLR_FOR_TANKER = "snfOrClrForTanker";
    public static final String WEIGHT_RECORD_LENGTH = "weightRecordLength";
    public static final String SAMPLE_AS_COLLECTION = "sampleAsCollection";
    public static final String IS_CLOUD_SUPPORT = "isCloudSupport";
    public static final String MERGED_HMB_ENABLE = "mergedHMBEnable";
    public static final String LACTOSCAN_PRAMS = "lactoscanPrams";
    public static final String UNSENT_ALERT_LIMIT = "unsentAlertLimit";
    //For sales
    public static final String ENABLE_INCENTIVE_IN_REPORT = "enableIncentiveInReport";
    public static final String ENABLE_SEQUENCE_NUMBER_REPORT = "enableSequenceNumberReport";
    public static final String INCENTIVE_PERCENTAGE = "incentivePercentage";
    public static final String ENABLE_FILLED_OR_EMPTY_CANS = "enableFilledOrEmptyCans";
    public static final String ENABLE_CLR_IN_REPORT = "enableClrInReport";
    public static final String ENABLE_TRUCK_ENTRY = "enableTruckEntry";
    public static final String ENABLE_CENTER_COLLECTION = "enableCenterCollection";
    public static final String ENABLE_DAIRY_REPORT = "enableDairyReport";
    public static final String TARE_COMMAND = "tareCommand";
    public static final String SELECT_RATE_CHART_TYPE = "selectRateChartType";
    public static final String ENABLE_BONUS_FOR_PRINT = "enableBonusForPrint";
    //New entries needs to be add in database
    public static final String DEVICE_PWD = "devicePwd";
    public static final String SET_AMCU_SERVER = "setAmcuServer";
    //To set buffalo ratechart
    public static final String CHANGE_MILKTYPE_BUFFALO = "changeMilktypeBuffalo";
    public static final String CHANGE_SNF_MILK_TYPE_BUFFALO = "changeSnfMilkTypeBuffalo";
    //for roundoff
    public static final String WEIGHT_ROUND_OFF = "weightRoundOff";
    public static final String AMOUNT_ROUND_OFF = "amountRoundOff";
    public static final String RATE_ROUND_OFF = "rateRoundOff";
    public static final String WEIGHT_ROUND_OFF_CHECK = "weightRoundOffCheck";
    public static final String AMOUNT_ROUND_OFF_CHECK = "amountRoundOffCheck";
    public static final String RATE_ROUND_OFF_CHECK = "rateRoundOffCheck";
    //For essae rdu
    public static final String IS_ADDED_WATER = "isAddedWater";
    public static final String IS_RATE_EDIT_CHECK = "isRateEditCheck";
    //Enable manual to device
    public static final String ENABLE_MANUAL_TO_DEVICE = "enableManualToDevice";
    public static final String ENABLE_SIMLOCK_TO_DEVICE = "enableSimlockToDevice";
    public static final String ENABLE_BONUS_TO_DEVICE = "enableBonusToDevice";
    public static final String ENABLE_MULTIPLE_COLLECTION = "enableMultipleCollection";
    public static final String ENABLE_CONFIGURABLE_FARMER_ID_SIZE = "enableConfigurableFarmerIdSize";
    //Save session
    public static final String MILK_ANALYZER_NAME = "milkAnalyzerName";
    public static final String PRINTER_NAME = "printerName";
    public static final String milkType = "milkType";
    public static final String rateChart = "rateChart";
    public static final String milkAnalyzerName = "milkAnalyzerName";
    public static final String printerName = "printerName";
    public static final String rduName = "rduName";
    public static final String weighingScale = "weighingScale";
    public static final String secondMAType = "secondMAType";
    public static final String WEIGHING_SCALE = "weighingScale";
    public static final String RDU_NAME = "rduName";
    //To set baudrate
    public static final String RDU_BAUDRATE = "rduBaudrate";
    public static final String PRINTER_BAUDRATE = "printerBaudrate";
    public static final String MILK_ANALYZER_BAUDRATE = "milkAnalyzerBaudrate";
    public static final String milkAnalyzerBaudrate = "milkAnalyzerBaudrate";
    public static final String printerBaudrate = "printerBaudrate";
    public static final String rduBaudrate = "rduBaudrate";
    public static final String secondMABaudrate = "secondMABaudrate";
    public static final String FAT_CONSTANT_FOR_CLR = "fatConstantForClr";
    public static final String SNF_CONSTANT_FOR_CLR = "snfConstantForClr";
    //for clr calculation
    public static final String CONSTANT_FOR_CLR = "constantForClr";
    public static final String CREATE_EDIT = "createEdit";
    public static final String fatConstantForClr = "fatConstantForClr";
    public static final String snfConstantForClr = "snfConstantForClr";
    //for clr calculation
    public static final String constantForClr = "constantForClr";
    //for tab shutdown delay
    public static final String SHUT_DOWN_DELAY = "shutDownDelay";
    public static final String IS_MA_MANUAL = "isMaManual";
    public static final String IS_WS_MANUAL = "isWsManual";
    //For bonus
    public static final String BONUS_START_DATE = "bonusStartDate";
    public static final String BONUS_END_DATE = "bonusEndDate";
    public static final String BONUS = "bonus";
    public static final String IS_BONUS_ENABLE = "isBonusEnable";
    //Regarding mail and SMS
    public static final String SEND_SHIFT_MAILS = "sendShiftMails";
    public static final String ENABLE_SMS = "enableSMS";
    public static final String IS_RATE_IMPORT_ACCESS_OPERATOR = "isRateImportAccessOperator";
    public static final String IS_IMPORT_EXCEL_SHEET_ACCESS = "isImportExcelSheetAccess";
    public static final String SIMLOCK_PASSWORD = "simlockPassword";
    public static final String ALLOW_SIMLOCK = "allowSimlock";
    //Enable or disable the reject
    public static final String ENABLE_REJECT_TO_DEVICE = "enableRejectToDevice";
    public static final String ACCEPT_MILK_IN_KG_FORMAT = "acceptMilkInKgFormat";
    public static final String ENABLE_CONVERSION_FACTOR = "enableConversionFactor";
    public static final String CONVERSION_FACTOR = "conversionFactor";
    public static final String WEIGHING_SCALE_PREFIX = "weighingScalePrefix";
    public static final String WEIGHING_SCALE_SUFFIX = "weighingScaleSuffix";
    public static final String WEIGHING_SCALE_SEPARATOR = "weighingScaleSeparator";
    public static final String DISPLAY_KG_TO_DEVICE = "displayKgToDevice";
    public static final String IS_RATE_CHART_IN_KG = "isRateChartInKg";
    public static final String WEIGHING_DIVISION_FACTOR = "weighingDivisionFactor";
    public static final String ENABLE_SALES_FS = "enableSalesFS";
    public static final String SALES_BUFF_RATE = "salesBuffRate";
    public static final String SALES_COW_RATE = "salesCowRate";
    public static final String ENABLE_SALES = "enableSales";
    public static final String ENABLE_BONUS_DISPLAY_RDU = "enableBonusDisplayRDU";
    public static final String ENABLE_INCENTIVE_ON_RDU = "enableIncentiveOnRdu";
    public static final String ENABLE_IP_TABLE_RULE = "enableIpTableRule";
    public static final String ENABLE_SALES_FARMER_ID = "enableSalesFarmerId";
    public static final String URL_HEADER = "urlHeader";
    //Credentials
    public static final String SMART_AMCU_EMAIL = "smartAmcuEmail";
    public static final String SMART_AMCU_PASSWORD = "smartAmcuPassword";
    public static final String SMS_FOOTER = "smsFooter";
    public static final String COLLECTION_SMS_FOOTER = "collectionSMSFooter";
    //Allow editable rate
    public static final String ENABLE_EDITABLE_RATE = "enableEditableRate";
    //To allow farmer export mail
    public static final String ENABLE_FARMER_EXPORT_MAIL = "enableFarmerExportMail";
    //To allow the milkquality
    public static final String ENABLE_MILK_QUALITY = "enableMilkQuality";
    // To allow equipmentbasedcollection
    public static final String ENABLE_EQUIPMENT_BASED_COLLECTION = "enableEquipmentBasedCollection";
    public static final String UPDATE_RATE_MIDDLE_SESSION = "updateRateMiddleSession";
    public static final String MCC_OR_ABC = "mCCorABC";
    public static final String LICENSE_JSON = "licenceJson";
    public static final String SERVER = "server";
    public static final String TRIP_CODE_START_INDEX = "tripCodeStartIndex";
    public static final String MAX_TRIP = "maxTrip";
    public static final String KEY_START_TANKER_ID = "startTankerIndex";
    public static final String KEY_PER_TANKER_LIMIT = "perDayTankerLimit";
    public static final String KEY_TARE_ON_START_COLLECTION = "isTareOnStart";
    //Allow route and number of cans in report
    public static final String ALLOW_NUMBER_OF_CANS_IN_REPORT = "allowNumberOfCansInReport";
    public static final String ALLOW_COLLECTION_ROUTE_IN_REPORT = "allowCollectionRouteInReport";
    public static final String ENABLE_FAT_SNF_VIEW_FOR_COLLECTION = "enableFatSnfViewForCollection";
    public static final String ENABLE_FAT_CLR_VIEW_FOR_CHILLING = "enableFatClrViewForChilling";
    public static final String ENABLE_ALL_FAT_SNF_CLR_VIEW = "enableAllFatSnfClrView";
    public static final String VISIBLITY_REPORT_EDITING = "visiblityReportEditing";
    public static final String PERIODIC_DEVICE_DATA_SEND = "periodicDeviceDataSend";
    public static final String ALLOW_EDIT_REPORT = "allowOperatorToReportEditing";
    public static final String NUMBER_OF_EDITABLE_SHIFT = "numberOfEditableShift";
    public static final String ADDITIONAL_PARAMS = "additionalParams";
    public static final String VISIBILITY_CAN_TOGGLING = "visibilityCanToggling";
    public static final String ALLOW_CAN_TOGGLING = "allowCanToggling";
    public static final String MAX_LIMIT_EMPTY_CAN = "maxLimitEmptyCan";
    public static final String MIN_LIMIT_FILLED_CAN = "minLimitFilledCan";

    public static final String ALLOW_EDIT_COLLECTION = "allowEditCollection";

    //for roundoff
    public static final String weightRoundOff = "weightRoundOff";
    public static final String amountRoundOff = "amountRoundOff";
    public static final String rateRoundOff = "rateRoundOff";
    public static final String weightRoundOffCheck = "weightRoundOffCheck";
    public static final String amountRoundOffCheck = "amountRoundOffCheck";
    public static final String rateRoundOffCheck = "rateRoundOffCheck";
    //For essae rdu
    public static final String isAddedWater = "isAddedWater";
    public static final String isRateEditCheck = "isRateEditCheck";
    //To set buffalo ratechart
    public static final String changeMilktypeBuffalo = "changeMilktypeBuffalo";
    public static final String changeSnfMilkTypeBuffalo = "changeSnfMilkTypeBuffalo";
    //for tab shutdown delay
    public static final String shutDownDelay = "shutDownDelay";
    public static final String isMaManual = "isMaManual";
    public static final String isWsManual = "isWsManual";
    //For bonus
    public static final String bonusStartDate = "bonusStartDate";
    public static final String bonusEndDate = "bonusEndDate";
    public static final String bonus = "bonus";
    public static final String isBonusEnable = "isBonusEnable";
    //Regarding mail and SMS
    public static final String sendShiftMails = "sendShiftMails";
    public static final String enableSMS = "enableSMS";
    public static final String isRateImportAccessOperator = "isRateImportAccessOperator";
    public static final String isImportExcelSheetAccess = "isImportExcelSheetAccess";
    public static final String simlockPassword = "simlockPassword";
    public static final String allowSimlock = "allowSimlock";
    //Credentials
    public static final String smartAmcuEmail = "smartAmcuEmail";
    public static final String smartAmcuPassword = "smartAmcuPassword";
    public static final String smsFooter = "smsFooter";
    public static final String collectionSMSFooter = "collectionSMSFooter";
    //Enable manual to device
    public static final String enableManualToDevice = "enableManualToDevice";
    public static final String enableSimlockToDevice = "enableSimlockToDevice";
    public static final String enableBonusToDevice = "enableBonusToDevice";
    public static final String enableMultipleCollection = "enableMultipleCollection";
    public static final String enableConfigurableFarmerIdSize = "enableConfigurableFarmerIdSize";
    //Enable or disable the reject
    public static final String enableRejectToDevice = "enableRejectToDevice";
    public static final String acceptMilkInKgFormat = "acceptMilkInKgFormat";
    public static final String enableConversionFactor = "enableConversionFactor";
    public static final String conversionFactor = "conversionFactor";
    public static final String weighingScalePrefix = "weighingScalePrefix";
    public static final String weighingScaleSuffix = "weighingScaleSuffix";
    public static final String weighingScaleSeparator = "weighingScaleSeparator";
    public static final String displayKgToDevice = "displayKgToDevice";
    public static final String isRateChartInKg = "isRateChartInKg";
    public static final String weighingDivisionFactor = "weighingDivisionFactor";
    public static final String enableSalesFS = "enableSalesFS";
    public static final String salesBuffRate = "salesBuffRate";
    public static final String salesCowRate = "salesCowRate";
    public static final String enableSales = "enableSales";
    public static final String enableBonusDisplayRDU = "enableBonusDisplayRDU";
    public static final String enableIncentiveOnRdu = "enableIncentiveOnRdu";
    public static final String enableIpTableRule = "enableIpTableRule";
    public static final String enableSalesFarmerId = "enableSalesFarmerId";
    public static final String urlHeader = "urlHeader";

    //For sales
    public static final String enableIncentiveInReport = "enableIncentiveInReport";
    public static final String enableSequenceNumberReport = "enableSequenceNumberReport";
    public static final String incentivePercentage = "incentivePercentage";
    public static final String enableFilledOrEmptyCans = "enableFilledOrEmptyCans";
    public static final String enableClrInReport = "enableClrInReport";
    public static final String enableTruckEntry = "enableTruckEntry";
    public static final String enableCenterCollection = "enableCenterCollection";
    public static final String enableDairyReport = "enableDairyReport";
    public static final String tareCommand = "tareCommand";
    public static final String selectRateChartType = "selectRateChartType";
    public static final String enableBonusForPrint = "enableBonusForPrint";
    //Allow editable rate
    public static final String enableEditableRate = "enableEditableRate";
    //To allow farmer export mail
    public static final String enableFarmerExportMail = "enableFarmerExportMail";
    //To allow the milkquality
    public static final String enableMilkQuality = "enableMilkQuality";
    // To allow equipmentbasedcollection
    public static final String enableEquipmentBasedCollection = "enableEquipmentBasedCollection";
    public static final String setAmcuServer = "setAmcuServer";
    //Allow route and number of cans in report
    public static final String allowNumberOfCansInReport = "allowNumberOfCansInReport";
    public static final String allowCollectionRouteInReport = "allowCollectionRouteInReport";
    public static final String enableFatSnfViewForCollection = "enableFatSnfViewForCollection";
    public static final String enableFatClrViewForChilling = "enableFatClrViewForChilling";
    public static final String enableAllFatSnfClrView = "enableAllFatSnfClrView";
    public static final String visiblityReportEditing = "visiblityReportEditing";
    public static final String allowOperatorToReportEditing = "allowOperatorToReportEditing";
    public static final String numberOfEditableShift = "numberOfEditableShift";
    public static final String visibilityCanToggling = "visibilityCanToggling";
    public static final String allowCanToggling = "allowCanToggling";
    public static final String maxLimitEmptyCan = "maxLimitEmptyCan";
    public static final String minLimitFilledCan = "minLimitFilledCan";
    // Clr round off
    public static final String clrRoundOffUpto = "clrRoundOffUpto";
    // overruling the config setting
    public static final String allowFSCinSMS = "allowFSCinSMS";
    public static final String allowFSCinPrint = "allowFSCinPrint";
    public static final String multipleMA = "multipleMA";
    public static final String customMyRateChart = "customMyRateChart";
    public static final String allowMaxLimitFromRC = "allowMaxLimitFromRC";
    public static final String allowRoundOffFatAndSnf = "allowRoundOffFatAndSnf";
    public static final String ekoMilkClrPosition = "ekoMilkClrPosition";
    public static final String ekoMilkAddedWaterPosition = "ekoMilkAddedWaterPosition";
    public static final String smartCCFeature = "smartCCFeature";
    public static final String allowWSTare = "allowWSTare";
    public static final String createMultipleUsers = "createMultipleUsers";
    public static final String materialCode = "materialCode";
    public static final String tareDeviationWeight = "tareDeviationWeight";
    public static final String deviationAlertForWeight = "deviationAlertForWeight";
    public static final String isRateCalculatedFromDevice = "isRateCalculatedFromDevice";
    //Milk lane phase 2 requirement
    public static final String ignoreZeroFatSNF = "ignoreZeroFatSNF";
    public static final String enableCollectionContraints = "enableCollectionContraints";
    public static final String collectionStartMorningshift = "collectionStartMorningshift";
    public static final String collectionEndMorningShift = "collectionEndMorningShift";
    public static final String collectionStartEveningShift = "collectionStartEveningShift";
    public static final String collectionEndEveningShift = "collectionEndEveningShift";
    public static final String CALIBRATION_DATE = "calibrationDate";
    public static final String isDisplayAmount = "isDisplayAmount";
    public static final String isDisplayQuantity = "isDisplayQuantity";
    public static final String isDisplayRate = "isDisplayRate";
    public static final String userConfig = "userConfig";
    public static final String weighingParity = "weighingParity";
    public static final String weighingDatbits = "weighingDatbits";
    public static final String weighingStopbits = "weighingStopbits";
    public static final String enableOrDisableIpTable = "enableOrDisableIpTable";
    public static final String managerPassword = "managerPassword";
    public static final String managerMobileNumber = "managerMobileNumber";
    public static final String managerEmailID = "managerEmailID";
    public static final String operatorPassword = "operatorPassword";
    public static final String operatorMobileNumber = "operatorMobileNumber";
    public static final String centerName = "centerName";
    public static final String centerCode = "centerCode";
    public static final String centerLocation = "centerLocation";
    public static final String centerRoute = "centerRoute";
    public static final String centerBMCID = "centerBMCID";
    public static final String centerAddress = "centerAddress";
    public static final String centerContactPerson1 = "centerContactPerson1";
    public static final String centerContactPerson2 = "centerContactPerson2";
    public static final String centerContactEmail1 = "centerContactEmail1";
    public static final String centerContactEmail2 = "centerContactEmail2";
    public static final String centerContactMobile1 = "centerContactMobile1";
    public static final String centerContactMobile2 = "centerContactMobile2";
    public static final String maxWeighingScaleLimit = "maxWeighingScaleLimit";
    public static final String WeighingBaudrate = "WeighingBaudrate";
    public static final String rduQuantityRoundOff = "rduQuantityRoundOff";
    public static final String snfOrClrForTanker = "snfOrClrForTanker";
    public static final String weightRecordLength = "weightRecordLength";
    public static final String escapeEnableForCollection = "escapeEnableForCollection";
    public static final String KEY_MILKTYPE = "milkType";
    public static final String KEY_SHOW_UNSENT_ALERT = "showUnsentAlert";
    public static final String KEY_DEFAULT_MILK_TYPE_BOTH = "defalutMilkTypeBoth";
    public static final String KEY_MA1_PARITY = "ma1Parity";
    public static final String KEY_MA1_STOP_BITS = "ma1stopbits";
    public static final String KEY_MA1_DATA_BITS = "ma1Databits";
    public static final String KEY_MA2_PARITY = "ma2Parity";
    public static final String KEY_MA2_STOP_BITS = "ma2stopbits";
    public static final String KEY_MA2_DATA_BITS = "ma2Databits";
    public final static String KEY_ESCAPE_ENABLE_COLLECTION = "escapeEnableForCollection";
    public final static String KEY_COLL_START_MRN_SHIFT = "collectionStartMorningshift";
    public final static String KEY_COLL_END_MRN_SHIFT = "collectionEndMorningShift";
    public final static String KEY_COLL_START_EVN_SHIFT = "collectionStartEveningShift";
    public final static String KEY_COLL_END_EVN_SHIFT = "collectionEndEveningShift";
    public static final String KEY_SNF_OR_CLR_TANKER = "snfOrClrForTanker";
    public final static String KEY_MANDATORY_RATE_CHART = "rateChartMandatory";
    public final static String KEY_MIN_FAT_REJECT_COW = "keyMinFatRejectCow";
    public final static String KEY_MIN_SNF_REJECT_COW = "keyMinSnfRejectCow";
    public final static String KEY_MAX_FAT_REJECT_COW = "keyMaxFatRejectCow";
    public final static String KEY_MAX_SNF_REJECT_COW = "keyMaxSnfRejectCow";
    public static final String KEY_PRINT_ENABLE_A4 = "enablehpPrinter";
    public static final String KEY_SEND_EMAIL_CONFIGUREIDS = "sendEmaililConf";
    public static final String KEY_RATE_CHART_EXCEL_IMPORT = "rateExcelImport";
    public final static String KEY_MANAGER_PASSWORD = "managerPassword";
    public final static String KEY_OPERATOR_PASSWORD = "operatorPassword";
    public static final String ON_CREATE_CALL_LIC = "onCreateCallLic";
    public static final String ON_CREATEON_UPGRADE = "onCreateonUpgrade";
    public final static String KEY_MIN_FAT_REJECT_BUFF = "keyMinFatRejectBuff";
    public final static String KEY_MIN_SNF_REJECT_BUFF = "keyMinSnfRejectBuff";
    public final static String KEY_MAX_FAT_REJECT_BUFF = "keyMaxFatRejectBuff";
    public final static String KEY_MAX_SNF_REJECT_BUFF = "keyMaxSnfRejectBuff";
    public final static String KEY_MIN_FAT_REJECT_MIX = "keyMinFatRejectMix";
    public final static String KEY_MIN_SNF_REJECT_MIX = "keyMinSnfRejectMix";
    public final static String KEY_MAX_FAT_REJECT_MIX = "keyMaxFatRejectMix";
    public final static String KEY_MAX_SNF_REJECT_MIX = "keyMaxSnfRejectMix";

    //Shared preference this is saved and 'smartCCfeature' and Database it saved as 'smartCCFeature'
    public static final String KEY_SMARTCC_FEATURE = "smartCCFeature";
    public static final String KEY_MA1_MILKTYPE = "maMilkType1";
    public static final String KEY_MA2_MILKTYPE = "maMilkType2";
    public static final String KEY_MA1_BAUDRATE = "ma1Baudrate";
    public static final String KEY_MA2_BAUDRATE = "ma2Baudrate";
    public static final String KEY_MA1_NAME = "ma1Name";
    public static final String KEY_MA2_NAME = "ma2Name";
    public static final String KEY_ALLOW_FAMER_INCREMENT = "keyAllowFormerIncrement";
    public static final String KEY_DELETE_COLL_SHIFT_RECORD = "deleteCollRecordShift";
    public static final String KEY_DELETE_FILES_DAY = "deleteFilesAfterDay";
    public static final String KEY_MIN_VALID_WEIGHT = "minValidWeightForCollection";

    //Added parameters 11.6.1

    public static final String KEY_MORNING_START = "morningSessionStartTime";
    public static final String KEY_EVENING_START = "eveningSessionStartTime";

    public static final String KEY_MORNING_END_TIME = "morningSessionEndTime";
    public static final String KEY_EVENING_END_TIME = "eveningSessionEndTime";

    public static final String KEY_ALLOW_AGENT_SPLIT = "allowAgentSplit";

    public static final String KEY_AFTER_DECIMAL_DIGIT_LENGHT = "keyAfterDecimalDigitLength";

    public static final String KEY_ALLOW_PROTEIN_VALUE = "KeyAllowProtein";

    public static final String KEY_ALLOW_INCENTIVE_RATE_CHART_NAME = "incentiveRateChartName";

    public static final String KEY_ALLOW_TWO_DECIMAL = "allowTwoDecimal";


    public static final String KEY_DISPLAY_FAT_CONFIGURATION = "displayFATConfiguration";
    public static final String KEY_DISPLAY_SNF_CONFIGURATION = "displaySNFConfiguration";
    public static final String KEY_DISPLAY_CLR_CONFIGURATION = "displayCLRConfiguration";
    public static final String KEY_DISPLAY_PROTEIN_CONFIGURATION = "displayProteinConfiguration";

    public static final String KEY_RATE_FAT_CONFIGURATION = "rateFATConfiguration";
    public static final String KEY_RATE_SNF_CONFIGURATION = "rateSNFConfiguration";
    public static final String KEY_RATE_CLR_CONFIGURATION = "rateCLRConfiguration";
    public static final String KEY_RATE_PROTEIN_CONFIGURATION = "rateProteinConfiguration";


    public static final String KEY_ROUND_FAT_CONFIGURATION = "roundFATConfiguration";
    public static final String KEY_ROUND_SNF_CONFIGURATION = "roundSNFConfiguration";
    public static final String KEY_ROUND_CLR_CONFIGURATION = "roundCLRConfiguration";
    public static final String KEY_ROUND_PROTEIN_CONFIGURATION = "roundProteinConfiguration";
    public static final String KEY_ALLOW_DISPLAY_RATE = "allowDisplayRate";
    public static final String FARM_ID_DIGIT = "farmIdDigit";
    public static final String KEY_ALLOW_FARMER_CREATE = "allowFamerCreate";
    public static final String KEY_ALLOW_FARMER_EDIT = "allowFamerEdit";
    public static final String KEY_ALLOW_DISPLAY_NAME_IN_REPORT = "allowDisplayNameInReport";
    public static final String KEY_DISPLAY_DAIRY_NAME = "displayDairyName";

    public static final String KEY_RDU_PASSWORD = "rduPassword";

    public static final String KEY_WS_IGNORE_THRESHOLD = "wsIgnoreThreshold";
    public static final String KEY_WS_LITRE_COMMAND = "wsLitreCommand";
    public static final String KEY_WS_KG_COMMAND = "wsKgCommand";

    public static final String KEY_KG_FAT = "kgFat";
    public static final String KEY_KG_SNF = "kgSnf";
    public static final String KEY_KG_CLR = "kgClr";
    public static final String KEY_HOTSPOT_SSID = "hotspotSsid";
    public static final String KEY_ENABLE_HOTSPOT = "enableHotspot";
    public static final String KEY_ENABLE_WIFI = "enableWifi";
    public static final String KEY_ENABLE_DISPATCH = "enableDispatchReport";
    public static final String KEY_DISABLE_MANUAL_DISPATCH = "disableManualForDispatch";
    public static final String KEY_SALES_MIXED_RATE = "salesMixedRate";
    public final static String ENABLE_LACTOSE_BASED_REJECTION = "enableLactoseBasedRejection";
    public final static String KEY_MAX_LACTOSE_REJECT_COW = "keyMaxLactoseRejectCow";
    public final static String KEY_MAX_LACTOSE_REJECT_BUFF = "keyMaxLactoseRejectBuff";
    public final static String KEY_MAX_LACTOSE_REJECT_MIX = "keyMaxLactoseRejectMix";
    public static final String KEY_SET_DYNAMIC_RATECHART_COW = "setRateCowDynamic";
    public static final String KEY_SET_DYNAMIC_RATECHART_BUFFALO = "setRateBuffaloDynamic";
    public static final String KEY_SET_DYNAMIC_RATECHART_MIXED = "setRateMixedDynamic";
    public final static String ENABLE_DYNAMIC_RATE_CHART = "enableDynamicRateChart";
    public final static String ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM = "allowApkUpgradeFromFileSystem";
    Context mContext;

    public ConfigurationConstants(Context ctx) {
        this.mContext = ctx;
    }

    public static SharedPreferences returnSharedPref(Context context) {
        SharedPreferences pref = context.getSharedPreferences(
                ConfigurationConstants.PREF_NAME, Context.MODE_PRIVATE);
        return pref;
    }

    public static String getStringConfigurationValue(SharedPreferences pref, String key, String defaultValue) {

        String returnValue = defaultValue;
        try {
            returnValue = pref.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public static String getBooleanConfigurationValue(SharedPreferences pref, String key, boolean defaultValue) {

        boolean returnValue = defaultValue;
        try {
            returnValue = pref.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(returnValue);
    }


    public static String getIntegerConfigurationValue(SharedPreferences pref, String key, int defaultValue) {

        int returnValue = defaultValue;
        try {
            returnValue = pref.getInt(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(returnValue);
    }


    public static String getFloatConfigurationValue(SharedPreferences pref, String key, float defaultValue) {

        float returnValue = defaultValue;
        try {
            returnValue = pref.getFloat(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(returnValue);
    }

    public static String getLongConfigurationValue(SharedPreferences pref, String key, long defaultValue) {

        long returnValue = defaultValue;
        try {
            returnValue = pref.getLong(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(returnValue);
    }

    /**
     * This method reads the confiuration from sharedPref and saves it in db.
     *
     * @param context
     * @return
     */

    public static ConfigurationEntity getDefaultConfiguration(Context context) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        SessionManager session = new SessionManager(context);
        SharedPreferences sharedPreferences = returnSharedPref(context);
        ConfigurationEntity configurationEntity = new ConfigurationEntity();

        configurationEntity.acceptMilkInKgFormat =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_IN_KG_FORMAT, false);
        configurationEntity.allowSimlock =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_CHECK_ALLOW_SIMLOCK, false);

        configurationEntity.amountRoundOff =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_AMOUNT_ROUNDOFF, 2);

        configurationEntity.allowRoundOffFatAndSnf =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_ROUND_OFF_FS, true);

        configurationEntity.allowWSTare =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_SMARTCC_ENABLE_TARE, true);

        configurationEntity.amountRoundOffCheck =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_AMOUNT_ROUNDOFFCHECK, true);

        configurationEntity.bonus =
                getFloatConfigurationValue(sharedPreferences, AmcuConfig.KEY_BONUS_RATE, 0.0f);

        configurationEntity.bonusEndDate =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_BONUS_END_DATE, null);

        configurationEntity.bonusStartDate =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_BONUS_START_DATE, null);
        configurationEntity.ekoMilkAddedWaterPosition =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_ADDED_WATER_POSITION, 4);

        configurationEntity.changeMilktypeBuffalo =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_CHANGE_FAT, String.valueOf(0.0));

        configurationEntity.changeSnfMilkTypeBuffalo =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_CHANGE_SNF, String.valueOf(0.0));

        configurationEntity.clrRoundOffUpto =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_CLR_ROUND_OFF, 1);

        configurationEntity.collectionSMSFooter =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_COLLECTION_SMS_FOOTER, null);

        configurationEntity.constantForClr =
                getFloatConfigurationValue(sharedPreferences, AmcuConfig.KEY_CLR_CONSTANT, 1.44f);

        configurationEntity.conversionFactor =
                getFloatConfigurationValue(sharedPreferences, AmcuConfig.KEY_CONVERSION_FACTOR, 1.027f);

        configurationEntity.createMultipleUsers =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_CREATE_MULITIPLE_USER, false);

        configurationEntity.deviationAlertForWeight =
                getFloatConfigurationValue(sharedPreferences, AmcuConfig.KEY_DEVIATION_ALERT_WEIGHT, 0.1f);

        configurationEntity.displayKgToDevice =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_DISPLAY_FORMAT_IN_KG, false);

        configurationEntity.ekoMilkClrPosition =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_CLR_POSITION, 3);

        configurationEntity.enableBonusDisplayRDU =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_BONUS_RDU, false);

        configurationEntity.enableBonusForPrint =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_BONUS_TO_PRINT, false);

        configurationEntity.enableBonusToDevice =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_BONUS_ENABLE, false);

        configurationEntity.enableClrInReport =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_CLR_REPORT, false);

        configurationEntity.enableConfigurableFarmerIdSize =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_FARMER_LENGTH_CONFIGURATION, false);

        configurationEntity.enableConversionFactor =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_CONVERSION_FACTOR, false);

        configurationEntity.enableIncentiveInReport =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_INCENTIVE_REPORT, false);

        configurationEntity.enableIpTableRule =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_IPTABLE_RULE, true);

        configurationEntity.enableManualToDevice =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_MANUAL_DISPLAY, false);

        configurationEntity.enableMultipleCollection =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_MULTIPLE_COLLECTION_DISPLAY, false);

        configurationEntity.enableRejectToDevice =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_REJECT_RATECHART, true);

        configurationEntity.enableSales =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_SALES, false);

        configurationEntity.enableSMS =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_CHECK_ALLOW_SMS, false);

        configurationEntity.enableSalesFarmerId =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_FARMERID_SALES, false);

        configurationEntity.enableSimlockToDevice =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_SIMLOCK_DISPLAY, false);

        configurationEntity.enableSequenceNumberReport =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_SEQUENCE_NUMBER_REPORT, true);

        configurationEntity.fatConstantForClr =
                getFloatConfigurationValue(sharedPreferences, AmcuConfig.KEY_FAT_CONSTANT, .84f);

        configurationEntity.incentivePercentage =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_INCENTIVE_PERCENTAGE, 40);

        configurationEntity.isAddedWater =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_AMOUNT_CHECKED, false);

        configurationEntity.isBonusEnable =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_BONUS_ENABLE, false);

        configurationEntity.isImportExcelSheetAccess =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATE_CHART_EXCEL_IMPORT, false);

        configurationEntity.isMaManual =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_CHECK_MA_MANUAL, false);

        configurationEntity.isRateCalculatedFromDevice =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATE_CALCULATED_DEVICE, true);

        configurationEntity.isRateChartInKg =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATECHART_IN_KG, false);

        configurationEntity.isRateImportAccessOperator =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATECHART_ACCESS_OPERATOR, false);

        configurationEntity.isWsManual =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_CHECK_WS_MANUAL, false);

        configurationEntity.milkAnalyzerBaudrate =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_MA_BAUDRATE, 9600);

        configurationEntity.milkAnalyzerName = getStringConfigurationValue(sharedPreferences,
                AmcuConfig.KEY_MILKANALYSER, String.valueOf("LACTOSCAN"));
// configurationEntity.milkType = saveSession.getMilkType();


        configurationEntity.perDaySMSLimit =
                String.valueOf(session.getPerDayMessageLimit());

        configurationEntity.printerBaudrate =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_PRINTER_BAUDRATE, 9600);

        configurationEntity.printerName =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_PRINTER, String.valueOf("SMARTMOO"));

        configurationEntity.rateChart =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATECHART_NAME, null);

        configurationEntity.rateRoundOff =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATE_ROUNDOFF, 2);

        configurationEntity.rateRoundOffCheck =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATE_ROUNDOFFCHECK, true);

        configurationEntity.rduBaudrate =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_RDU_BAUDRATE, 9600);

        configurationEntity.rduName =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_RDU, String.valueOf("ESSAE"));

        configurationEntity.secondMABaudrate =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_SECOND_MILKANALYSER_BAUDRATE,
                        9600);

        configurationEntity.secondMAType =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SECOND_MILKANALYSER, String.valueOf("LACTOSCAN"));

        configurationEntity.sendShiftMails =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_SEND_EMAIL_CONFIGUREIDS, false);

        configurationEntity.setAmcuServer =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_SERVER, null);

        configurationEntity.shutDownDelay =
                getLongConfigurationValue(sharedPreferences, AmcuConfig.KEY_SHUTDOWN_DELAY, 0);

        configurationEntity.simlockPassword =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SIM_UNLOCK_PASSWORD, null);

        configurationEntity.smartAmcuEmail =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_CONFIGURED_CLIENT_EMAIL, String.valueOf("smartamcu.smartmoo@stellapps.com"));

        configurationEntity.smartAmcuPassword =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_CONFIGURED_CLIENT_PASSWORD, String.valueOf("C9275B9B24B75C9488A3B3E35000AFED"));

        configurationEntity.smsFooter =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_BUILD_FOOTER, null);

        configurationEntity.snfConstantForClr =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_SNF_CONSTANT, 4);

        configurationEntity.tareCommand =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_TARE_COMMAND, String.valueOf("T"));

        configurationEntity.tareDeviationWeight =
                getFloatConfigurationValue(sharedPreferences, AmcuConfig.KEY_DEVIATION_WEIGHT, 0.1f);


        configurationEntity.urlHeader =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_URL_HEADER, String.valueOf("https://"));

        configurationEntity.weighingDatbits =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_WS_DATA_BITS, String.valueOf("DATA_BITS_8"));

        configurationEntity.weighingDivisionFactor =
                getFloatConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHING_DIVISION_FACTOR, 1f);

        configurationEntity.weighingParity =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_WS_PARITY, String.valueOf("PARITY_NONE"));

        configurationEntity.weighingScale =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHING_NAME, String.valueOf("ESSAE"));

        configurationEntity.weighingScalePrefix =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHING_PREFIX, String.valueOf("N"));

        configurationEntity.weighingScaleSeparator =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHING_SEPERATOR, String.valueOf("CRLF"));

        configurationEntity.weighingScaleSuffix =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHING_PREFIX, String.valueOf("=lt"));

        configurationEntity.weighingStopbits =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_WS_STOP_BITS, String.valueOf("STOP_BITS_1"));

        configurationEntity.weightRoundOff =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHING_ROUNDOFF, 2);

        configurationEntity.weightRoundOffCheck =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHING_ROUNDCHECK, true);


        configurationEntity.maxWeighingScaleLimit =
                getFloatConfigurationValue(sharedPreferences, AmcuConfig.KEY_MAX_LIMIT_WEIGHT, 200.0f);

        configurationEntity.WeighingBaudrate =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHING_BAUDRATE, 9600);

        configurationEntity.enableOrDisableIpTable =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_MOBILE_DATA_ENABLE, true);

        configurationEntity.rduQuantityRoundOff =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_RDU_WEIGHT_ROUND_OFF, true);

        configurationEntity.weightRecordLength =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_WEIGHT_RECORD_LENGTH, 0);

        configurationEntity.sampleAsCollection =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_SAMPLE_AS_COLLECTION, true);


        configurationEntity.mergedHMBEnable =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_MERGED_HMB_ENABLE, false);

//        configurationEntity.lactoscanPrams =
//                getStringConfigurationValue(sharedPreferences,SaveSession.KEY_LACTOSCAN_PARAM,String.valueOf(
//                        new AdvanceUtil(context).getLactoScanParams()
//                ));

        configurationEntity.isCloudSupport =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_IS_CLOUD_SUPPORT, true);

        configurationEntity.unsentAlertLimit =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_UNSENT_ALERT_LIMIT, 100);

        configurationEntity.devicePwd =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_DEVICE_Password, String.valueOf(""));

        configurationEntity.whiteListURL =
                getStringConfigurationValue(sharedPreferences, "whiteListURL", String.valueOf("NA"));

        configurationEntity.deviceId =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_DEVICE_ID, String.valueOf(""));

        configurationEntity.calibrationDate =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_CALIBRATION_DATE, String.valueOf("12"));

        configurationEntity.createEdit =
                getBooleanConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATECHART_CREATEOREDIT, false);

        configurationEntity.farmIdDigit =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_FARMER_ID_DIGIT, 4);

        configurationEntity.maParity =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_MA_PARITY, String.valueOf("PARITY_NONE"));

        configurationEntity.maDatabits =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_MA_DATA_BITS, String.valueOf("DATA_BITS_8"));

        configurationEntity.mastopbits =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_MA_STOP_BITS, String.valueOf("STOP_BITS_1"));

        configurationEntity.periodicDataSent =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_PERIODIC_DATA_SENT, 3);

        configurationEntity.startTimeDataSent =
                getIntegerConfigurationValue(sharedPreferences, AmcuConfig.KEY_DATA_SENT_START_TIME, 5);

        configurationEntity.allowOperatorToReportEditing =
                getBooleanConfigurationValue(sharedPreferences, ALLOW_EDIT_REPORT, false);

        configurationEntity.numberOfEditableShift =
                getIntegerConfigurationValue(sharedPreferences, NUMBER_OF_EDITABLE_SHIFT, 0);

        configurationEntity.milkType =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_MILKTYPE, String.valueOf(""));


        configurationEntity.allowCollectionRouteInReport =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_ROUTE_IN_REPORT, String.valueOf(false));

        configurationEntity.allowFSCinPrint =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_FSC_IN_PRINT, String.valueOf(false));


        configurationEntity.allowFSCinSMS =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_FSC_IN_SMS, String.valueOf(false));

        configurationEntity.allowMaxLimitFromRC =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_MAX_LIMIT_RC, String.valueOf(false));

        configurationEntity.allowNumberOfCansInReport =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_NOC_IN_REPORT, String.valueOf(false));

        configurationEntity.customMyRateChart =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_MY_RATE_CHART, String.valueOf(false));

        configurationEntity.enableCenterCollection =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_CENTER_COLLECTION, String.valueOf(false));

        configurationEntity.enableEditableRate =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_EDITABLE_RATE, String.valueOf(false));

        configurationEntity.enableEquipmentBasedCollection =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_EQUIPMENT_BASED_COLLECTION, String.valueOf(false));

        configurationEntity.enableFarmerExportMail =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_FARMER_EXPORTED_MAIL, String.valueOf(false));
        configurationEntity.enableIncentiveOnRdu =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_INCENTIVE_RDU, String.valueOf(false));
        configurationEntity.isDisplayAmount =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_DISPLAY_AMOUNT, String.valueOf(true));
        configurationEntity.isDisplayQuantity =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_DISPLAY_QUANTITY, String.valueOf(true));
        configurationEntity.isDisplayRate =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_DISPLAY_RATE, String.valueOf(true));
        configurationEntity.isRateEditCheck =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_EDITABLE_RATE, String.valueOf(false));
        configurationEntity.materialCode =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SMARTCC_MATERIAL_CODE, String.valueOf(2600004));
        configurationEntity.multipleMA =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_MULTIPLE_MA, String.valueOf(false));
        configurationEntity.selectRateChartType =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SELECT_RATECHART_TYPE, "FATSNF");
        configurationEntity.visiblityReportEditing =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_VISIBILITY_REPORT_EDITING, String.valueOf(false));
        configurationEntity.visibilityCanToggling =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_VISIBILITY_FOR_CANS_TOGGLE, String.valueOf(false));
        configurationEntity.smartCCFeature =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SMARTCC_FEATURE, String.valueOf(false));
        configurationEntity.enableFilledOrEmptyCans =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_FILLED_OR_EMPTY_CANS, String.valueOf(false));
        configurationEntity.snfOrClrForTanker =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SNF_OR_CLR_TANKER, "CLR");
        configurationEntity.enableMilkQuality =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ALLOW_MILK_QUALITY, String.valueOf(true));
        configurationEntity.isRateCalculatedFromDevice =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_RATE_CALCULATED_DEVICE, String.valueOf(true));
        configurationEntity.enableRejectToDevice =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_REJECT_RATECHART, String.valueOf(true));
        configurationEntity.enableEditableRate =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_ENABLE_EDITABLE_RATE, String.valueOf(false));
        configurationEntity.collectionStartMorningshift =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_COLL_START_MRN_SHIFT, "05:00");
        configurationEntity.collectionEndMorningShift =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_COLL_END_MRN_SHIFT, "11:00");
        configurationEntity.collectionStartEveningShift =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_COLL_START_EVN_SHIFT, "16:00");
        configurationEntity.collectionEndEveningShift =
                getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_COLL_END_EVN_SHIFT, String.valueOf("22:00"));

        configurationEntity.managerEmailID = "";
        configurationEntity.managerMobileNumber = "";
        configurationEntity.managerPassword = getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_MANAGER_PASSWORD, "MANAGER");


        configurationEntity.operatorMobileNumber = "";
        configurationEntity.operatorPassword = getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_OPERATOR_PASSWORD, "operator.smartamcu");


        configurationEntity.centerAddress = "";
        configurationEntity.centerCode = "SOC123";
        configurationEntity.centerBMCID = "";
        configurationEntity.centerContactEmail1 = "";
        configurationEntity.centerContactEmail2 = "";
        configurationEntity.centerContactMobile1 = "";
        configurationEntity.centerContactMobile2 = "";
        configurationEntity.centerContactPerson1 = "";
        configurationEntity.centerContactPerson2 = "";
        configurationEntity.centerRoute = "";
        configurationEntity.centerLocation = "";
        configurationEntity.centerName = "Devapp Testing";

        configurationEntity.periodicDeviceDataSend = String.valueOf(3);

//Need to add code for ABC

        configurationEntity.allowOperatorToReportEditing = String.valueOf(false);
        configurationEntity.numberOfEditableShift = String.valueOf(0);
        configurationEntity.updateRateMiddleSession = String.valueOf(false);
        configurationEntity.tripCodeStartIndex = String.valueOf(501);
        configurationEntity.startTankerIndex = String.valueOf(900);
        configurationEntity.maxTrip = String.valueOf(20);
        configurationEntity.perDayTankerLimit = String.valueOf(1);
        configurationEntity.isTareOnStart = String.valueOf(false);
        configurationEntity.licenceJson = null;


        configurationEntity.devicePwd = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.DEVICE_PWD, String.valueOf(""));


        configurationEntity.setAmcuServer = getStringConfigurationValue(sharedPreferences, AmcuConfig.KEY_SET_SERVER, AppConstants.GENRIC_SERVER);
        configurationEntity.whiteListURL = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.WHITE_LIST_URL, "NA");
        configurationEntity.deviceId = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.DEVICE_ID, " ");

        configurationEntity.calibrationDate = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.CALIBRATION_DATE,
                "12");
        configurationEntity.maParity = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.MA_PARITY,
                "PARITY_NONE");

        configurationEntity.mastopbits = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.MA_STOPBITS,
                "STOP_BITS_1");

        configurationEntity.maDatabits = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.MA_DATABITS,
                "DATA_BITS_8");
        configurationEntity.licenceJson = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.LICENSE_JSON,
                null);


        configurationEntity.ma1Parity = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA1_PARITY,
                "PARITY_NONE");

        configurationEntity.ma1stopbits = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA1_STOP_BITS,
                "STOP_BITS_1");

        configurationEntity.ma1Databits = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA1_DATA_BITS,
                "DATA_BITS_8");

        configurationEntity.ma2Parity = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA2_PARITY,
                "PARITY_NONE");

        configurationEntity.ma2stopbits = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA2_STOP_BITS,
                "STOP_BITS_1");

        configurationEntity.ma2Databits = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA2_DATA_BITS,
                "DATA_BITS_8");


        configurationEntity.defalutMilkTypeBoth = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_DEFAULT_MILK_TYPE_BOTH,
                "COW");


        configurationEntity.mCCorABC = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.MCC_OR_ABC,
                UserType.AMCU);

        configurationEntity.milkType = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MILKTYPE,
                "");

//        configurationEntity.farmIdDigit =
//                getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.FARM_ID_DIGIT, 4);

        configurationEntity.periodicDataSent =
                getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.PERIODIC_DATA_SENT, 3);
        configurationEntity.startTimeDataSent =
                getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.START_TIME_DATA_SENT, 5);

        configurationEntity.numberOfEditableShift =
                getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.NUMBER_OF_EDITABLE_SHIFT, 0);

        configurationEntity.tripCodeStartIndex =
                getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.TRIP_CODE_START_INDEX, 501);
        configurationEntity.startTankerIndex =
                getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_START_TANKER_ID, 900);

        configurationEntity.maxTrip =
                getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.MAX_TRIP, 20);
        configurationEntity.perDayTankerLimit =
                getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_PER_TANKER_LIMIT, 1);

        configurationEntity.periodicDeviceDataSend = String.valueOf(3);

        configurationEntity.createEdit =
                getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.CREATE_EDIT, false);

        configurationEntity.allowOperatorToReportEditing =
                getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ALLOW_EDIT_REPORT, false);
        configurationEntity.updateRateMiddleSession =
                getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.UPDATE_RATE_MIDDLE_SESSION, false);
        configurationEntity.isTareOnStart =
                getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_TARE_ON_START_COLLECTION, false);

        configurationEntity.showUnsentAlert =
                getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_SHOW_UNSENT_ALERT, false);

        configurationEntity.escapeEnableForCollection =
                getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_ESCAPE_ENABLE_COLLECTION, false);

        configurationEntity.allowCanToggling = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ALLOW_CAN_TOGGLING, false);
        configurationEntity.enableAllFatSnfClrView = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_ALL_FAT_SNF_CLR_VIEW, false);
        configurationEntity.enableCollectionContraints = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_COLLECTION_CONTRAINTS, false);
        configurationEntity.enableFatClrViewForChilling = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_FAT_CLR_VIEW_FOR_CHILLING, "FS");
        configurationEntity.enableFatSnfViewForCollection = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_FAT_SNF_VIEW_FOR_COLLECTION, "FS");
        configurationEntity.enableSalesFS = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_SALES_FS, false);


        configurationEntity.maxLimitEmptyCan = getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.MAX_LIMIT_EMPTY_CAN, 25);
        configurationEntity.minLimitFilledCan = getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.MIN_LIMIT_FILLED_CAN, 10);
        configurationEntity.salesBuffRate = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.SALES_BUFF_RATE, "0");
        configurationEntity.salesCowRate = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.SALES_COW_RATE, "0");

        configurationEntity.ignoreZeroFatSNF = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.IGNORE_ZERO_FAT_SNF, false);
        configurationEntity.enableDairyReport = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_DAIRY_REPORT, false);
        configurationEntity.enableTruckEntry = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_TRUCK_ENTRY, false);
        configurationEntity.isMandatoryRatechart = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MANDATORY_RATE_CHART, true);

        configurationEntity.keyMinFatRejectCow = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MIN_FAT_REJECT_COW, 0.0f);
        configurationEntity.keyMinSnfRejectCow = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MIN_SNF_REJECT_COW, 0.0f);
        configurationEntity.keyMaxFatRejectCow = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_FAT_REJECT_COW, 14.0f);
        configurationEntity.keyMaxSnfRejectCow = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_SNF_REJECT_COW, 14.0f);
        configurationEntity.keyMaxLactoseRejectCow = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_COW, 6.0f);

        configurationEntity.enablehpPrinter = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_PRINT_ENABLE_A4, false);

        configurationEntity.sendEmaililConf = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_SEND_EMAIL_CONFIGUREIDS, false);
        configurationEntity.rateExcelImport = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_RATE_CHART_EXCEL_IMPORT, false);


        configurationEntity.keyMinFatRejectBuff = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MIN_FAT_REJECT_BUFF, 0.0f);
        configurationEntity.keyMinSnfRejectBuff = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MIN_SNF_REJECT_BUFF, 0.0f);
        configurationEntity.keyMaxFatRejectBuff = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_FAT_REJECT_BUFF, 14.0f);
        configurationEntity.keyMaxSnfRejectBuff = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_SNF_REJECT_BUFF, 14.0f);
        configurationEntity.keyMaxLactoseRejectBuff = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_BUFF, 6.0f);

        configurationEntity.keyMinFatRejectMix = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MIN_FAT_REJECT_MIX, 0.0f);
        configurationEntity.keyMinSnfRejectMix = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MIN_SNF_REJECT_MIX, 0.0f);
        configurationEntity.keyMaxFatRejectMix = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_FAT_REJECT_MIX, 14.0f);
        configurationEntity.keyMaxSnfRejectMix = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_SNF_REJECT_MIX, 14.0f);
        configurationEntity.keyMaxLactoseRejectMix = getFloatConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MAX_LACTOSE_REJECT_MIX, 6.0f);
        configurationEntity.enableLactoseBasedRejection = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_LACTOSE_BASED_REJECTION, false);
        configurationEntity.enableDynamicRateChart = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART, false);
        configurationEntity.allowApkUpgradeFromFileSystem = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM, false);


        configurationEntity.maMilkType1 = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA1_MILKTYPE, "NONE");
        configurationEntity.maMilkType2 = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA2_MILKTYPE, "NONE");


        configurationEntity.ma1Baudrate = getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA1_BAUDRATE, 9600);
        configurationEntity.ma2Baudrate = getIntegerConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA2_BAUDRATE, 9600);


        configurationEntity.ma1Name = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA1_NAME, "LACTOSCAN");
        configurationEntity.ma2Name = getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MA2_NAME, "LACTOSCAN");

        configurationEntity.keyAllowFormerIncrement = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_ALLOW_FAMER_INCREMENT,
                false);


        configurationEntity.morningSessionEndTime =
                getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MORNING_END_TIME,
                        "14:00");
        configurationEntity.morningSessionStartTime =
                getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_MORNING_START,
                        "00:00");
        configurationEntity.eveningSessionEndTime =
                getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_EVENING_END_TIME,
                        "02:00");
        configurationEntity.eveningSessionStartTime =
                getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_EVENING_START,
                        "15:00");

        configurationEntity.allowAgentSplit =
                getStringConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_ALLOW_AGENT_SPLIT, "false");

        configurationEntity.allowEditCollection = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.ALLOW_EDIT_COLLECTION, false);

        configurationEntity.allowProteinValue = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_ALLOW_PROTEIN_VALUE, false);

        configurationEntity.allowTwoDecimal = getBooleanConfigurationValue(sharedPreferences, ConfigurationConstants.KEY_ALLOW_TWO_DECIMAL, false);

        configurationEntity.allowFarmerCreate = getBooleanConfigurationValue(sharedPreferences,
                ConfigurationConstants.KEY_ALLOW_FARMER_CREATE, true);
        configurationEntity.allowFarmerEdit = getBooleanConfigurationValue(sharedPreferences,
                ConfigurationConstants.KEY_ALLOW_FARMER_EDIT, true);
        configurationEntity.allowApkUpgradeFromFileSystem = getBooleanConfigurationValue(sharedPreferences,
                ConfigurationConstants.ALLOW_APK_UPGRADE_FROM_FILE_SYSTEM, false);
        configurationEntity.enableDynamicRateChart = getBooleanConfigurationValue(sharedPreferences,
                ConfigurationConstants.ENABLE_DYNAMIC_RATE_CHART, false);

        configurationEntity.displayFATConfiguration = "1";
        configurationEntity.displaySNFConfiguration = "1";
        configurationEntity.displayCLRConfiguration = "1";
        configurationEntity.displayProteinConfiguration = "2";

        configurationEntity.rateFATConfiguration = "1";
        configurationEntity.rateSNFConfiguration = "1";
        configurationEntity.rateCLRConfiguration = "1";
        configurationEntity.rateProteinConfiguration = "2";

        configurationEntity.roundFATConfiguration = AppConstants.ROUND_HALF_UP;
        configurationEntity.roundSNFConfiguration = AppConstants.ROUND_HALF_UP;
        configurationEntity.roundCLRConfiguration = AppConstants.ROUND_HALF_UP;
        configurationEntity.roundProteinConfiguration = AppConstants.ROUND_HALF_UP;
        configurationEntity.allowDisplayRate = "true";


        return configurationEntity;


    }

    // TODO this mehtod can be replaced with getConfig() in SplashActivity class
    public static ConfigurationEntity getLatestConfiguration(Context context) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        SessionManager session = new SessionManager(context);
        ConfigurationEntity configurationEntity = new ConfigurationEntity();
        configurationEntity.acceptMilkInKgFormat = String.valueOf(amcuConfig.getAllowInKgformat());
        configurationEntity.allowSimlock = String.valueOf(amcuConfig.getAllowSimlockPassword());
        configurationEntity.amountRoundOff = String.valueOf(amcuConfig.getDecimalRoundOffAmount());
        configurationEntity.allowRoundOffFatAndSnf = String.valueOf(amcuConfig.getRoundOffCheckFatAndSnf());
        configurationEntity.allowWSTare = String.valueOf(amcuConfig.getTareEnable());
        configurationEntity.amountRoundOffCheck = String.valueOf(amcuConfig.getDecimalRoundOffAmountCheck());
        configurationEntity.bonus = String.valueOf(amcuConfig.getBonusRate());
        configurationEntity.bonusEndDate = String.valueOf(amcuConfig.getBonusEndDate());
        configurationEntity.bonusStartDate = String.valueOf(amcuConfig.getBonusEndDate());

        configurationEntity.changeMilktypeBuffalo = String.valueOf(amcuConfig.getChangeFat());
        configurationEntity.changeSnfMilkTypeBuffalo = String.valueOf(amcuConfig.getChangeSnf());

        configurationEntity.clrRoundOffUpto = String.valueOf(amcuConfig.getClrRoundOffUpto());

        configurationEntity.collectionSMSFooter = amcuConfig.getCollectionSMSFooter();

        configurationEntity.constantForClr = String.valueOf(amcuConfig.getConstant());
        configurationEntity.conversionFactor = String.valueOf(amcuConfig.getConversionFactor());
        configurationEntity.createMultipleUsers = String.valueOf(amcuConfig.getMultipleUser());

        configurationEntity.deviationAlertForWeight = String.valueOf(amcuConfig.getDeviationWeightAlert());
        configurationEntity.displayKgToDevice = String.valueOf(amcuConfig.getDisplayInKg());
        configurationEntity.ekoMilkAddedWaterPosition = String.valueOf(amcuConfig.getAddedWaterPos());
        configurationEntity.ekoMilkClrPosition = String.valueOf(amcuConfig.getClrPosition());
        configurationEntity.enableBonusDisplayRDU = String.valueOf(amcuConfig.getEnableBonusRDU());
        configurationEntity.enableBonusForPrint = String.valueOf(amcuConfig.getBonusEnableForPrint());
        configurationEntity.enableBonusToDevice = String.valueOf(amcuConfig.getBonusEnable());
        configurationEntity.enableClrInReport = String.valueOf(amcuConfig.getEnableClrInReport());
        configurationEntity.enableConfigurableFarmerIdSize = String.valueOf(amcuConfig.getEnableFarmerLenghtConfiguration());
        configurationEntity.enableConversionFactor = String.valueOf(amcuConfig.getEnableConversionFactorDisplay());
        configurationEntity.enableIncentiveInReport = String.valueOf(amcuConfig.getEnableIncentiveInReport());
        configurationEntity.enableIpTableRule = String.valueOf(amcuConfig.getIpTableRule());
        configurationEntity.enableManualToDevice = String.valueOf(amcuConfig.getManualDisplay());
        configurationEntity.enableMultipleCollection = String.valueOf(amcuConfig.getAllowMultipeCollection());
        configurationEntity.enableRejectToDevice = String.valueOf(amcuConfig.getEnableRejectForRateChart());
        configurationEntity.enableSales = String.valueOf(amcuConfig.getEnableSales());
        configurationEntity.enableSMS = String.valueOf(amcuConfig.getAllowSMS());
        configurationEntity.enableSalesFarmerId = String.valueOf(amcuConfig.getEnableFarmerIdForSales());
        configurationEntity.enableSimlockToDevice = String.valueOf(amcuConfig.getSimlockDisplay());
        configurationEntity.enableSequenceNumberReport = String.valueOf(amcuConfig.getEnableSequenceNumberInReport());


        configurationEntity.fatConstantForClr = String.valueOf(amcuConfig.getFatCons());
        configurationEntity.incentivePercentage = String.valueOf(amcuConfig.getIncentivePercentage());
        configurationEntity.isAddedWater = String.valueOf(amcuConfig.getIsAddedWater());
        configurationEntity.isBonusEnable = String.valueOf(amcuConfig.getBonusEnable());

        configurationEntity.isImportExcelSheetAccess = String.valueOf(amcuConfig.getImportExcelRateEnable());
        configurationEntity.isMaManual = String.valueOf(amcuConfig.getMaManual());
        configurationEntity.isRateCalculatedFromDevice = String.valueOf(amcuConfig.getRateCalculatedFromDevice());
        configurationEntity.isRateChartInKg = String.valueOf(amcuConfig.getKeyRateChartInKg());
        configurationEntity.isRateImportAccessOperator = String.valueOf(amcuConfig.getOperatorRateAccess());
        configurationEntity.isWsManual = String.valueOf(amcuConfig.getWsManual());

        configurationEntity.milkAnalyzerBaudrate = String.valueOf(amcuConfig.getMABaudRate());
        configurationEntity.milkAnalyzerName = String.valueOf(amcuConfig.getMA());
        // configurationEntity.milkType = saveSession.getMilkType();


        configurationEntity.perDaySMSLimit = String.valueOf(session.getPerDayMessageLimit());
        configurationEntity.printerBaudrate = String.valueOf(amcuConfig.getPrinterBaudRate());
        configurationEntity.printerName = String.valueOf(amcuConfig.getPrinter());
        configurationEntity.rateChart = String.valueOf(amcuConfig.getRateChartName());
        configurationEntity.rateRoundOff = String.valueOf(amcuConfig.getDecimalRoundOffRate());
        configurationEntity.rateRoundOffCheck = String.valueOf(amcuConfig.getDecimalRoundOffRateCheck());
        configurationEntity.rduBaudrate = String.valueOf(amcuConfig.getRdubaudrate());
        configurationEntity.rduName = String.valueOf(amcuConfig.getRDU());

        configurationEntity.secondMABaudrate = String.valueOf(amcuConfig.getSecondMilkBaud());
        configurationEntity.secondMAType = String.valueOf(amcuConfig.getSecondMilkAnalyser());
        configurationEntity.sendShiftMails = String.valueOf(amcuConfig.getSendEmailToConfigureIDs());
        configurationEntity.setAmcuServer = String.valueOf(amcuConfig.getServer());
        configurationEntity.shutDownDelay = String.valueOf(amcuConfig.getShutDownDelay());
        configurationEntity.simlockPassword = String.valueOf(amcuConfig.getSimUnlockPassword());
        configurationEntity.smartAmcuEmail = String.valueOf(amcuConfig.getClientConfiguredEmail());
        configurationEntity.smartAmcuPassword = String.valueOf(amcuConfig.getClientConfiguredPassword());
        configurationEntity.smsFooter = String.valueOf(amcuConfig.getSMSFooter());
        configurationEntity.snfConstantForClr = String.valueOf(amcuConfig.getSnfCons());

        configurationEntity.tareCommand = String.valueOf(amcuConfig.getTareCommand());
        configurationEntity.tareDeviationWeight = String.valueOf(amcuConfig.getDeviationWeight());

        configurationEntity.urlHeader = String.valueOf(amcuConfig.getURLHeader());

        configurationEntity.weighingDatbits = String.valueOf(amcuConfig.getWSDataBits());
        configurationEntity.weighingDivisionFactor = String.valueOf(amcuConfig.getWeighingDivisionFactor());
        configurationEntity.weighingParity = String.valueOf(amcuConfig.getWSParity());
        configurationEntity.weighingScale = String.valueOf(amcuConfig.getWeighingScale());
        configurationEntity.weighingScalePrefix = String.valueOf(amcuConfig.getWeighingPrefix());
        configurationEntity.weighingScaleSeparator = String.valueOf(amcuConfig.getWeighingSeperator());
        configurationEntity.weighingScaleSuffix = String.valueOf(amcuConfig.getWeighingSuffix());
        configurationEntity.weighingStopbits = String.valueOf(amcuConfig.getWSStopBits());
        configurationEntity.weightRoundOff = String.valueOf(amcuConfig.getDecimalRoundOffWeigh());
        configurationEntity.weightRoundOffCheck = String.valueOf(amcuConfig.getDecimalRoundOffRateCheck());

        configurationEntity.maxWeighingScaleLimit = String.valueOf(amcuConfig.getMaxlimitOfWeight());
        configurationEntity.WeighingBaudrate = String.valueOf(amcuConfig.getWeighingbaudrate());

        configurationEntity.numberOfEditableShift = String.valueOf(amcuConfig.getNumberShiftCanBeEdited());
        configurationEntity.allowOperatorToReportEditing = String.valueOf(amcuConfig.getAllowReportEdit());

        configurationEntity.allowCollectionRouteInReport = String.valueOf(amcuConfig.getAllowRouteInReport());

        configurationEntity.allowFSCinPrint = String.valueOf(amcuConfig.getAllowFSCInPrint());

        configurationEntity.allowFSCinSMS = String.valueOf(amcuConfig.getAllowFSCInSMS());
        configurationEntity.allowMaxLimitFromRC = String.valueOf(amcuConfig.getAllowMaxLimitFromRateChart());
        configurationEntity.allowNumberOfCansInReport = String.valueOf(amcuConfig.getAllowNumberOfCans());
        configurationEntity.customMyRateChart = String.valueOf(amcuConfig.getMyRateChartEnable());
        configurationEntity.enableCenterCollection = String.valueOf(amcuConfig.getEnableCenterCollection());
        configurationEntity.enableEditableRate = String.valueOf(amcuConfig.getEditableRate());
        configurationEntity.enableEquipmentBasedCollection = String.valueOf(amcuConfig.getAllowEquipmentBasedCollection());
        configurationEntity.enableFarmerExportMail = String.valueOf(amcuConfig.getAllowMailExportedFarmer());
        configurationEntity.enableIncentiveOnRdu = String.valueOf(amcuConfig.getEnableIncentiveRDU());
        configurationEntity.isDisplayAmount = String.valueOf(amcuConfig.getKeyDisplayAmount());
        configurationEntity.isDisplayQuantity = String.valueOf(amcuConfig.getKeyDisplayQuantity());
        configurationEntity.isDisplayRate = String.valueOf(amcuConfig.getKeyDisplayRate());
        configurationEntity.isRateEditCheck = String.valueOf(amcuConfig.getEditableRate());
        configurationEntity.materialCode = String.valueOf(amcuConfig.getMaterialCode());
        configurationEntity.multipleMA = String.valueOf(amcuConfig.getMultipleMA());
        configurationEntity.selectRateChartType = String.valueOf(amcuConfig.getRateChartType());
        configurationEntity.visiblityReportEditing = String.valueOf(amcuConfig.getAllowVisibilityReportEditing());
        configurationEntity.visibilityCanToggling = String.valueOf(amcuConfig.getAllowVisiblityForCanToggle());
        configurationEntity.smartCCFeature = String.valueOf(amcuConfig.getSmartCCFeature());
        configurationEntity.enableFilledOrEmptyCans = String.valueOf(amcuConfig.getEnableFilledOrEmptyCans());

        configurationEntity.snfOrClrForTanker = String.valueOf(amcuConfig.getSNFOrCLRFromTanker());

        configurationEntity.enableMilkQuality = String.valueOf(amcuConfig.getAllowMilkquality());

        configurationEntity.isRateCalculatedFromDevice = String.valueOf(amcuConfig.getRateCalculatedFromDevice());

        configurationEntity.enableRejectToDevice = String.valueOf(amcuConfig.getEnableRejectForRateChart());

        configurationEntity.enableEditableRate = String.valueOf(amcuConfig.getEditableRate());

        configurationEntity.collectionStartMorningshift = String.valueOf(amcuConfig.getKeyCollStartMrnShift());

        configurationEntity.collectionEndMorningShift = String.valueOf(amcuConfig.getKeyCollEndMrnShift());

        configurationEntity.collectionStartEveningShift = String.valueOf(amcuConfig.getKeyCollStartEvnShift());

        configurationEntity.collectionEndEveningShift = String.valueOf(amcuConfig.getKeyCollEndEvnShift());


        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        //Add manager details
        UserEntity userEnt = dbh.getUserByUserId(UserDetails.MANAGER);

        configurationEntity.managerEmailID = userEnt.emailId;
        configurationEntity.managerMobileNumber = userEnt.mobile;
        configurationEntity.managerPassword = userEnt.password;

        //Add operator details
        userEnt = dbh.getUserByUserId("SMARTOPERATOR");

        configurationEntity.operatorMobileNumber = userEnt.mobile;
        configurationEntity.operatorPassword = userEnt.password;


        //Add center details
        SocietyEntity centerEntity = dbh.getSocietyDetails(0);

        configurationEntity.centerAddress = centerEntity.address;
        configurationEntity.centerCode = centerEntity.socCode;
        configurationEntity.centerBMCID = centerEntity.bmcId;
        configurationEntity.centerContactEmail1 = centerEntity.socEmail1;
        configurationEntity.centerContactEmail2 = centerEntity.socEmail2;
        configurationEntity.centerContactMobile1 = centerEntity.contactNum1;
        configurationEntity.centerContactMobile2 = centerEntity.contactNum2;
        configurationEntity.centerContactPerson1 = centerEntity.conPerson1;
        configurationEntity.centerContactPerson2 = centerEntity.conPerson2;
        configurationEntity.centerRoute = centerEntity.route;
        configurationEntity.centerLocation = centerEntity.location;
        configurationEntity.centerName = centerEntity.name;

        configurationEntity.enableOrDisableIpTable = String.valueOf(amcuConfig.getMobileData());
        configurationEntity.rduQuantityRoundOff = String.valueOf(amcuConfig.getRDUWeightRoundOff());
        configurationEntity.weightRecordLength = String.valueOf(amcuConfig.getWeightRecordLenght());
        configurationEntity.sampleAsCollection = String.valueOf(amcuConfig.getSampleAsCollection());

        configurationEntity.mergedHMBEnable = String.valueOf(amcuConfig.getMergedHMBEnable());
        //configurationEntity.lactoscanPrams = saveSession.getKeyLactoscanParam();
        configurationEntity.isCloudSupport = String.valueOf(amcuConfig.getKeyCloudSupport());
        configurationEntity.unsentAlertLimit = String.valueOf(amcuConfig.getUnsentAlertLimit());


        configurationEntity.devicePwd = String.valueOf(amcuConfig.getDevicePassword());
        configurationEntity.whiteListURL = String.valueOf(amcuConfig.getWhiteListURL());
        configurationEntity.deviceId = String.valueOf(amcuConfig.getDeviceID());
        configurationEntity.calibrationDate = String.valueOf(amcuConfig.getCalibrationDate());
        configurationEntity.createEdit = String.valueOf(amcuConfig.getCreateAndEdit());
        configurationEntity.farmIdDigit = String.valueOf(amcuConfig.getFarmerIdDigit());
        configurationEntity.allowFarmerEdit = String.valueOf(amcuConfig.getKeyAllowFarmerEdit());
        configurationEntity.allowFarmerCreate = String.valueOf(amcuConfig.getKeyAllowFarmerCreate());
        configurationEntity.maParity = String.valueOf(amcuConfig.getMaParity());
        configurationEntity.maDatabits = String.valueOf(amcuConfig.getMaDataBits());
        configurationEntity.mastopbits = String.valueOf(amcuConfig.getMaStopBits());
        configurationEntity.periodicDataSent = String.valueOf(amcuConfig.getKeyPeriodicDataSent());
        configurationEntity.startTimeDataSent = String.valueOf(amcuConfig.getKeyDataSentStartTime());

        configurationEntity.allowOperatorToReportEditing = String.valueOf(amcuConfig.getAllowReportEdit());
        configurationEntity.numberOfEditableShift = String.valueOf(amcuConfig.getNumberShiftCanBeEdited());
        configurationEntity.licenceJson = String.valueOf(amcuConfig.getLicenceJson());
        configurationEntity.keyMinFatRejectCow = String.valueOf(amcuConfig.getKeyMinFatRejectCow());
        configurationEntity.keyMinSnfRejectCow = String.valueOf(amcuConfig.getKeyMinSnfRejectCow());
        configurationEntity.keyMaxFatRejectCow = String.valueOf(amcuConfig.getKeyMaxFatRejectCow());
        configurationEntity.keyMaxSnfRejectCow = String.valueOf(amcuConfig.getKeyMaxSnfRejectCow());
        configurationEntity.keyMaxLactoseRejectCow = String.valueOf(amcuConfig.getKeyMaxLactoseRejectCow());
        configurationEntity.enablehpPrinter = String.valueOf(amcuConfig.getEnableA4Printer());
        configurationEntity.sendEmaililConf = String.valueOf(amcuConfig.getSendEmailToConfigureIDs());
        configurationEntity.rateExcelImport = String.valueOf(amcuConfig.getImportExcelRateEnable());


        configurationEntity.keyMinFatRejectBuff = String.valueOf(amcuConfig.getKeyMinFatRejectBuff());
        configurationEntity.keyMinSnfRejectBuff = String.valueOf(amcuConfig.getKeyMinSnfRejectBuff());
        configurationEntity.keyMaxFatRejectBuff = String.valueOf(amcuConfig.getKeyMaxFatRejectBuff());
        configurationEntity.keyMaxSnfRejectBuff = String.valueOf(amcuConfig.getKeyMaxSnfRejectBuff());
        configurationEntity.keyMaxLactoseRejectBuff = String.valueOf(amcuConfig.getKeyMaxLactoseRejectBuff());


        configurationEntity.keyMinFatRejectMix = String.valueOf(amcuConfig.getKeyMinFatRejectMix());
        configurationEntity.keyMinSnfRejectMix = String.valueOf(amcuConfig.getKeyMinSnfRejectMix());
        configurationEntity.keyMaxFatRejectMix = String.valueOf(amcuConfig.getKeyMaxFatRejectMix());
        configurationEntity.keyMaxSnfRejectMix = String.valueOf(amcuConfig.getKeyMaxSnfRejectMix());
        configurationEntity.keyMaxLactoseRejectMix = String.valueOf(amcuConfig.getKeyMaxLactoseRejectMix());
        configurationEntity.enableLactoseBasedRejection = String.valueOf(amcuConfig.getLactoseBasedRejection());
        configurationEntity.allowApkUpgradeFromFileSystem = String.valueOf(amcuConfig.getApkFromFileSystem());
        configurationEntity.enableDynamicRateChart = amcuConfig.getDynamicRateChartValue();

        configurationEntity.maMilkType1 = amcuConfig.getMA1MilkType();
        configurationEntity.maMilkType2 = amcuConfig.getMA2MilkType();

        configurationEntity.ma1Baudrate = String.valueOf(amcuConfig.getMa1Baudrate());
        configurationEntity.ma2Baudrate = String.valueOf(amcuConfig.getMa2Baudrate());
        configurationEntity.ma1Name = amcuConfig.getMa1Name();
        configurationEntity.ma2Name = amcuConfig.getMa2Name();
        configurationEntity.keyAllowFormerIncrement = String.valueOf(amcuConfig.getKeyAllowFarmerIncrement());

        configurationEntity.morningSessionEndTime = amcuConfig.getMorningEndTime();
        configurationEntity.morningSessionStartTime = amcuConfig.getMorningStart();
        configurationEntity.eveningSessionEndTime = amcuConfig.getEveningEndTime();
        configurationEntity.eveningSessionStartTime = amcuConfig.getEveningStart();

        configurationEntity.allowAgentSplit = String.valueOf(amcuConfig.getKeyAllowAgentFarmerCollection());
        configurationEntity.allowEditCollection = String.valueOf(amcuConfig.getKeyAllowEditCollection());
        configurationEntity.allowProteinValue = String.valueOf(amcuConfig.getKeyAllowProteinValue());

        configurationEntity.minLimitFilledCan = String.valueOf(amcuConfig.getMinLimitFilledCans());
        configurationEntity.ma1Databits = String.valueOf(amcuConfig.getMa1DataBits());
        configurationEntity.ma1stopbits = String.valueOf(amcuConfig.getMaStopBits());

        return configurationEntity;

    }


}
