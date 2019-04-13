package com.devapp.devmain.server;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.ConsolidationPost.DateShiftEntry;
import com.devapp.devmain.additionalRecords.Database.AddtionalDatabase;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.databaseTasks.DatabaseTask;
import com.devapp.devmain.dbbackup.Backup;
import com.devapp.devmain.dbbackup.DatabaseCorruptionHandler;
import com.devapp.devmain.dbbackup.DatabaseSync;
import com.devapp.devmain.deviceinfo.DeviceStatusEntity;
import com.devapp.devmain.entity.AssocSocietyData;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.BonusChartEntity;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.FactorEntity;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.MessageEntity;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.entity.SocietyEntity;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.helper.UpdateDatabaseHelper;
import com.devapp.devmain.helper.UserRole;
import com.devapp.devmain.main.CustomRateChartUtil;
import com.devapp.devmain.milkline.TankerDatabase;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.tableEntities.DispatchCollectionTable;
import com.devapp.devmain.tableEntities.EditRecordCollectionTable;
import com.devapp.devmain.tableEntities.ExtraParams;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.tableEntities.FarmerTable;
import com.devapp.devmain.tableEntities.LicenseTable;
import com.devapp.devmain.tableEntities.RateChartNameTable;
import com.devapp.devmain.tableEntities.RateTable;
import com.devapp.devmain.tableEntities.SalesCollectionTable;
import com.devapp.devmain.tableEntities.SampleRecordTable;
import com.devapp.devmain.tableEntities.SplitRecordTable;
import com.devapp.devmain.tableEntities.TankerCollectionTable;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.GetEntity;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.devmain.util.config.DefaultConfigurationHandler;
import com.devapp.smartcc.database.AgentDatabase;
import com.devapp.smartcc.database.TruckCCDatabase;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.BuildConfig;

import org.apache.commons.collections4.SetUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import static com.devapp.devmain.helper.DatabaseEntity.CONFIGURATION_KEY;
import static com.devapp.devmain.helper.DatabaseEntity.CONFIGURATION_VALUE;
import static com.devapp.devmain.helper.DatabaseEntity.TABLE_CONFIGURATION;
import static com.devapp.devmain.helper.DatabaseEntity.TABLE_ESSAE_DATA;
import static com.devapp.devmain.helper.DatabaseEntity.TABLE_INCENTIVE_RATECHART;
import static com.devapp.smartcc.entityandconstants.SmartCCConstants.ESSAE;


public class DatabaseHandler extends SQLiteOpenHelper {


    public static final String isPrimary = "isPrimary";
    public static final int DATABASE_VERSION = 47;
    // Introduced in Version 8
    //This is for record sent or unsent
    public static final int COL_REC_NW_UNSENT = 0;
    public static final int COL_REC_NW_SENT = 1;
    public static final int COL_REC_SMS_SENT = 1;
    public static final int COL_REC_SMS_INVALID_NUMBER = 3;
    public static final int COL_REC_SMS_GENERIC_FAILURE = 4;
    // Introduced in Version 4
    public static final int MAX_SEQ_NUM = 99999;
    // Database name
    public static final String DATABASE_NAME = "devappUsb";
    // Farmer table name
    public static final String TABLE_FARMER = "FarmerTable";
    public static final String TABLE_RATE = "RateTable";
    public static final String TABLE_REPORTS = "ReportsTable";
    public static final String TABLE_USER = "UserTable";
    public static final String TABLE_RATECHART_NAME = "RateChartName";
    public static final String TABLE_MILKOTESTER = "MilkoTester";
    public static final String TABLE_PRINTER = "Printer";
    public static final String TABLE_WEIGHING_SCALE = "WeighingScale";
    public static final String TABLE_RDU = "rdu";
    public static final String TABLE_SETTING = "tableSetting";
    public static final String TABLE_SAMPLE = "sampleTable";
    // New table in Version 4 for Collection Record Despatch status
    public static final String TABLE_COL_REC_STATUS = "collRecordStatusTable";
    public static final String TABLE_COLL_REC_SEQ_NUM = "seqNumTable";
    public static final String TABLE_CHILLING_CENTER = "chillingCenter";
    public static final String TABLE_CHILLING_REPORT = "chillingReportTable";
    ////////////// consolidation API Changes
    public static final String TABLE_COLL_SALES_STATUS = "collSalesRecordStatus";
    public static final String TABLE_SHIFT_SALES_DATA = "shiftSalesData";
    public static final String TABLE_COLL_SALES_SEQ_NUMBER = "salesCollSeqNum";
    public static final String TABLE_SHIFT_CHILL_DATA = "shiftChillData";
    public static final String TABLE_COL_EX_REC_STATUS = "collEXRecordStatusTable";
    public static final String TABLE_SHIFT_EX_DATA = "shiftEXDataTable";
    public static final String TABLE_COLL_EX_REC_SEQ_NUM = "seqNumEXTable";
    ////////////// consolidation API Changes
    public static final String TABLE_EXTENDED_REPORT = "extendedReport";
    // Farmer Table Columns names
    public static final String KEY_FARMER_NAME = "farmerName";
    public static final String KEY_FARMER_COLUMNID = "_id";
    public static final String DEFAULT_FARMER_ID = "9994";
    public static final String KEY_FARMER_ID = "farmerId";
    public static final String KEY_FARMER_BARCODE = "farmerBarcode";
    public static final String KEY_FARMER_CANS = "farmerCans";
    public static final String KEY_FARMER_CATTLE = "cattleType";
    public static final String KEY_FARMER_SOCCODE = "socCode";
    public static final String KEY_FARMER_MOBILE = "mobile";
    public static final String KEY_FARMER_EMAIL = "email";
    public static final String KEY_FARMER_COW = "numCow";
    public static final String KEY_FARMER_BUFFALO = "numBuff";
    public static final String KEY_FARMER_NO_CATTLE = "numCattle";
    public static final String KEY_FARMER_ASSIGN_RATECHART = "assignRatechart";
    public static final String KEY_FARMER_REGISTRATION_DATE = "regDate";
    public static final String KEY_ENABLE_MULTIPLECANS = "enableMultipleCans";
    public static final String KEY_FARMER_SENT = "sentFarmerStatus";
    public static final String KEY_AGENT_ID = "agentId";
    public static final String KEY_FARMER_TYPE = "farmerType";
    public static final String KEY_REPORT_COLUMNID = "_id";
    public static final String KEY_REPORT_FARMERID = "farmerId";
    public static final String KEY_REPORT_NAME = "name";
    public static final String KEY_REPORT_SNF = "snf";
    public static final String KEY_REPORT_FAT = "fat";
    public static final String KEY_REPORT_RATE = "rate";
    public static final String KEY_REPORT_USER = "user";
    // Reports Entity
    public static final String KEY_REPORT_MANUAL = "manual";
    public static final String KEY_REPORT_AMOUNT = "amount";
    public static final String KEY_REPORT_QUANTITY = "quantity";
    public static final String KEY_REPORT_TAXNUM = "txnNumber";
    public static final String KEY_REPORT_MILKTYPE = "milkType";
    public static final String KEY_REPORT_LDATE = "lDAte";
    public static final String KEY_REPORT_SOCIETYID = "socID";
    public static final String KEY_REPORT_BONUS = "bonus";
    public static final String KEY_REPORT_TIME = "time";
    public static final String KEY_REPORT_TIME_MILLI = "timeMillis";
    public static final String KEY_REPORT_STATUS = "repStatus";
    public static final String KEY_REPORT_CLR = "repCLR";
    public static final String KEY_REPORT_AWM = "repAWM";
    public static final String KEY_REPORT_WEIGHTMANUAL = "repWeightManual";
    public static final String KEY_REPORT_MILKOMANUAL = "repMilkoManual";
    public static final String KEY_REPORT_MILKANALYSERTIME = "qualityTime";
    public static final String KEY_REPORT_WEIGHINGTIME = "quantityTime";
    public static final String KEY_REPORT_TEMP = "temperature";
    public static final String KEY_REPORT_COMMITED = "recordCommited";
    public static final String KEY_REPORT_TYPE = "reportType";
    public static final String KEY_MILKQUALITY = "milkQuality";
    //Added for Db version 14
    public static final String KEY_REPORT_RATEMODE = "rateMode";
    public static final String KEY_REPORT_NUMBER_OF_CANS = "numberOfCans";
    //Below two field added in database version 17
    public static final String KEY_REPORT_SEQUENCE_NUMBER = "repSequenceNumber";
    public static final String KEY_REPORT_ROUTE = "reportRoute";
    //This field added for version 18th, to send previous shift uncommited record as Incomplete
    public static final String KEY_REPORT_RECORD_STATUS = "recordStatus";
    public static final String KEY_REPORT_RATECHART_NAME = "rateChartName";
    public static final String KEY_REPORT_FOREIGN_SEQUENCE_ID = "forSeqId";
    public static final String KEY_REPORT_EDITED_TIME = "editedTime";
    public static final String KEY_REPORT_OLD_OR_NEW_FLAG = "oldOrNew";
    public static final String KEY_REPORT_KG_QTY = "kgQty";
    public static final String KEY_REPORT_LTR_QTY = "ltrsQty";
    public static final String KEY_TIPPING_START_TIME = "tippingStartTime";
    public static final String KEY_TIPPING_END_TIME = "tippingEndTime";
    public static final String KEY_REPORT_SENT_STATUS = "reportSentStatus";
    //For Extended MilmaRequirement
    public static final String KEY_REPORT_AGENT_ID = "repAgentId";
    public static final String KEY_REPORT_MILKSTATUS_CODE = "milkStatusCode";
    public static final String KEY_REPORT_RATE_CALC_DEVICE = "isRateChartCalculationFromDevice";
    //For heritage
    public static final String KEY_REPORT_MA_SERIAL = "maSerialNumber";
    public static final String KEY_REPORT_MA_NAME = "maName";
    //This section for the for extended report
    public static final String KEY_REPORT_LACTOSE = "repLactose";
    public static final String KEY_REPORT_PROTEIN = "repProtein";
    public static final String KEY_REPORT_PH = "repPH";
    public static final String KEY_REPORT_SALT = "repSalt";
    public static final String KEY_REPORT_FREEZING_POINT = "freezingPoint";
    public static final String KEY_REPORT_CONDUTIVITY = "repCondutivity";
    //  public static final String KEY_REPORT_MA_SERIAL_NUMBER = "maSerialNumber";
    public static final String KEY_REPORT_CALIBRATION = "maCalibration";
    public static final String KEY_REPORT_INCENTIVE_RATE = "incentiveRate";
    public static final String KEY_REPORT_INCENTIVE_AMOUNT = "incentiveAmount";
    public static final String KEY_REPORT_KG_FAT = "kgFat";
    public static final String KEY_REPORT_KG_SNF = "kgSnf";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_USERID = "userId";
    public static final String KEY_USER_EMAIL = "email";
    public static final String KEY_USER_ROLE = "userRole";
    public static final String KEY_USER_PASSWORD = "userPassword";
    public static final String KEY_USER_MOBILE = "userMobile";
    public static final String KEY_USER_SOCIETYID = "socId";
    public static final String KEY_USER_REGDATE = "regDate";
    public static final String KEY_USER_WEEKDATE = "weekDate";
    public static final String KEY_USER_MONTHDATE = "monthDate";
    public static final String KEY_USER_SENT = "sentUserStatus";
    public static final String KEY_SAMPLE_ID = "sampleId";
    public static final String KEY_SAMPLE_SOC_ID = "sampleSocId";
    public static final String KEY_SAMPLE_MODE = "sampleMode";
    public static final String KEY_SAMPLE_IS_WEIGH = "sampleISWeighchk";
    public static final String KEY_SAMPLE_IS_FAT = "sampleIsFat";
    public static final String KEY_SAMPLE_IS_SNF = "sampleIsSnf";
    public static final String KEY_SAMPLE_FAT = "sampleFat";
    public static final String KEY_SAMPLE_SNF = "sampleSnf";

    // Reports Entity
    public static final String KEY_SAMPLE_BARCODE = "sampleOther1";
    public static final String KEY_SAMPLE_RATE = "sampleRate";
    public static final String KEY_SAMPLE_AMOUNT = "sampleAmount";
    public static final String KEY_SAMPLE_SENT = "sentSampleStatus";
    public static final String KEY_COLL_REC_SEQNUM = "collRecSeqNum";
    public static final String KEY_CHILLING_ID = "ccenterId";
    public static final String KEY_CHILLING_CENTER_ID = "centerId";
    public static final String KEY_CHILLING_CENTER_NAME = "centerName";
    public static final String KEY_CHILLING_ROUTE = "centerRoute";
    public static final String KEY_CHILLING_CONTACT_PERSON = "conPerson";
    public static final String KEY_CHILLING_CONTACT_NUMBER = "conNumber";
    public static final String KEY_CHILLING_CONTACT_EMAILID = "conEmailId";
    public static final String KEY_CHILLING_BARCODE = "centerBarcode";
    public static final String KEY_CHILLING_REG_DATE = "regDate";
    public static final String KEY_CHILLING_SINGLE_MULTIPLE = "singleOrmultiple";
    public static final String KEY_CHILLING_MILK_TYPE = "chillingMilkType";
    public static final String KEY_CHILLING_CENTER_SENT = "sentChillingStatus";
    public static final String KEY_CHILLING_IS_ACTIVE = "isActive";
    public static final String KEY_MY_RATECHART_NAME = "rateChartName";
    public static final String KEY_MY_RATECHART_MILIDATE = "rateChartMiliTime";
    public static final String KEY_MY_RATECHART_MILK_TYPE = "milkType";
    public static final String KEY_MY_RATECHART_KG_FAT_RATE = "kgFATRate";
    public static final String KEY_MY_RATECHART_START_FAT = "startFAT";
    public static final String KEY_MY_RATECHART_END_FAT = "endFAT";
    public static final String KEY_MY_RATECHART_KG_SNF_RATE = "kgSNFRate";
    public static final String KEY_MY_RATECHART_START_SNF = "startSNF";
    public static final String KEY_MY_RATECHART_END_SNF = "endSNF";
    public static final String KEY_MY_RATECHART_START_SHIFT = "startShift";
    public static final String KEY_MY_RATECHART_START_DATE = "startDate";
    public static final String KEY_MY_RATECHART_END_SHIFT = "endShift";
    public static final String KEY_MY_RATECHART_END_DATE = "endDate";
    public static final String KEY_LICENSE_REQUEST_DATE = "requestTime";
    public static final String KEY_LICENSE_STATUS_CODE = "responseStatusCode";
    public static final String KEY_LICENSE_START_DATE = "startDate";
    public static final String KEY_LICENSE_END_DATE = "endDate";
    public static final String KEY_LICENSE_VALID = "isValid";
    public static final String KEY_LICENSE_MESSAGE = "message";
    // Version 4 change
    private static final String TAG = "DataBaseHandler";
    //Version 5 changes added COMMIT column in reports table
    private static final int VERSION_FIRST = 1;
    private static final int VERSION_SECOND = 2;
    private static final int VERSION_THIRD = 3;
    private static final int VERSION_FOURTH = 4;
    private static final int VERSION_FIFTH = 5;
    private static final int VERSION_SIXTH = 6;
    private static final int VERSION_SEVENTH = 7;
    private static final int VERSION_EIGHTH = 8;
    private static final int VERSION_NINTH = 9;
    private static final int VERSION_TENTH = 10;
    private static final int VERSION_ELEVENTH = 11;
    //added fro the Milk Quality and Remarks in TABLE_REPORTS
    private static final int VERSION_TWELFTH = 12;
    private static final int VERSION_THIRTEENTH = 13;
    private static final int VERSION_FOURTEENTH = 14;
    private static final int VERSION_FIFTEEN = 15;
    private static final int VERSION_SIXTEENTH = 16;
    private static final int VERSION_SEVENTEENTH = 17;
    private static final int VERSION_EIGHTEENTH = 18;
    private static final int VERSION_NINETEENTH = 19;
    private static final int VERSION_TWENTY = 20;
    private static final int VERSION_TWENTY_ONE = 21;
    private static final int VERSION_TWENTY_TWO = 22;
    //Increasing version for to allow single or multiple collection
    private static final int VERSION_TWENTY_THREE = 23;
    private static final int VERSION_TWENTY_FOUR = 24;
    private static final int VERSION_TWENTY_FIVE = 25;
    private static final int VERSION_TWENTY_SIX = 26;
    private static final int VERSION_TWENTY_SEVEN = 27;
    private static final int VERSION_TWENTY_EIGHT = 28;
    private static final int VERSION_TWENTY_NINE = 29;
    private static final int VERSION_THIRTY = 30;
    private static final int VERSION_THIRTY_ONE = 31;
    private static final int VERSION_THIRY_TWO = 32;
    private static final int VERSION_THIRTY_THREE = 33;
    private static final int VERSION_THIRTY_FOUR = 34;
    private static final int VERSION_THIRTY_FIVE = 35;
    private static final int VERSION_THIRTY_SIX = 36;
    private static final int VERSION_THIRTY_SEVEN = 37;
    private static final int VERSION_THIRTY_EIGHT = 38;
    private static final int VERSION_THIRTY_NINE = 39;
    private static final int VERSION_FORTY = 40;
    private static final int VERSION_FORTY_ONE = 41;
    private static final int VERSION_FORTY_TWO = 42;
    private static final int VERSION_FORTY_THREE = 43;
    private static final int VERSION_FORTY_FOUR = 44;
    private static final int VERSION_FORTY_FIVE = 45;
    private static final int VERSION_FORTY_SIX = 46;
    //This is for sms sent or unsent
    private static final int COL_REC_SMS_UNSENT = 0;
    private static final int COL_REC_SMS_NOT_ENABLE = 2;
    private static final int MIN_SEQ_NUM = 1;
    private static final String TABLE_TEMP_COL_REC_STATUS = "tempcollRecordStatusTable";
    private static final String TABLE_FARMER_TEMP = "FarmerTabletemp";
    private static final String TABLE_EXCEL = "ExcelTable";
    private static final String TABLE_SOCIETY = "SocietyTable";
    private static final String TABLE_ASSOCSOC = "ASSOCSOC";
    private static final String TABLE_MSGDETAILS = "MSGDETAILS";
    private static final String TABLE_KGFACTOR = "KGFACTOR";
    private static final String TABLE_REJECTION_PARAM = "ReJParam";
    private static final String TABLE_VALIDATION = "validationTable";
    private static final String TABLE_COLLECTION = "collectionTable";
    private static final String TABLE_SHIFT_DATA = "shiftDataTable";
    //Added in version 11.1.9
    private static final String TABLE_FARMER_STATUS = "tableFarmerStatus";
    // New table in Version 4 for Collection Record Despatch status
    //For sales
    private static final String TABLE_SALES_REPORT = "salesReportTable";
    private static final String TABLE_MY_RATE_CHART = "myRateChartTable";
    private static final String DEFAULT_FARMER_NAME = "Society Own Milk";
    private static final String KEY_FARMER_WEEKLY = "weekReport";
    private static final String KEY_FARMER_MONTHLY = "monthReport";
    // Validation Table
    private static final String KEY_VALIDATION_COLUMN_ID = "_id";
    private static final String KEY_VALIDATION_FAT = "valFat";
    private static final String KEY_VALIDATION_SNF = "valSnf";
    private static final String KEY_VALIDATION_DUMMY = "dummy";
    // Collection records
    private static final String KEY_COLLECTION_COLUMN_ID = "_id";
    private static final String KEY_COLLECTION_DATE = "valFat";
    private static final String KEY_COLLECTION_SHIFT = "valSnf";
    private static final String KEY_COLLECTION_DUMMY1 = "dummy_one";
    private static final String KEY_COLLECTION_DUMMY2 = "dummy_two";
    private static final String KEY_COLLECTION_DUMMY3 = "dummy_three";
    //Keys for table TABLE_COL_SALES_STATUS
    private static final String KEY_COLL_SALES_SEQNUM = "collRecSeqNum";
    //For smartCC added tipping start and endTime
    private static final String KEY_SOCIETY_COLUMNID = "_id";
    private static final String KEY_SOCIETY_NAME = "societyName";
    private static final String KEY_SOCIETY_CODE = "societyCode";
    private static final String KEY_SOCIETY_ADDRESS = "societyAddress";
    private static final String KEY_SOCIETY_LOCATION = "societyLocation";
    private static final String KEY_SOCIETY_ROUTE = "societyRate";
    private static final String KEY_SOCIETY_BMCID = "bmcId";
    // Key society
    private static final String KEY_SOCIETY_EMAIL1 = "socEmail1";
    private static final String KEY_SOCIETY_CONTACT1 = "societyContact1";
    private static final String KEY_SOCIETY_CONPERSON1 = "conperson1";
    private static final String KEY_SOCIETY_EMAIL2 = "socEmail2";
    private static final String KEY_SOCIETY_CONTACT2 = "societyContact2";
    private static final String KEY_SOCIETY_CONPERSON2 = "conperson2";
    private static final String KEY_SOCIETY_NUMFARMER = "numFarmer";
    private static final String KEY_SOCIETY_REGISTRATION_DATE = "regDate";
    private static final String KEY_SOCIETY_WEEKLY = "weekReport";
    private static final String KEY_SOCIETY_MONTHLY = "monthReport";
    private static final String KEY_ASSOCS_COLUMNID = "_id";
    private static final String KEY_ASSOCS_SOCID = "socId";
    private static final String KEY_ASSOCS_ASSOCID = "assOCID";
    private static final String KEY_ASSOCS_ASSOCName = "assName";
    private static final String KEY_USER_COLUMNID = "_id";
    private static final String KEY_MSG_COLUMNID = "_id";
    private static final String KEY_MSG_SOCIETY = "msgSoc";
    private static final String KEY_MSG_SENT = "msgSent";
    private static final String KEY_MSG_LIMIT = "msgLimit";
    private static final String KEY_MSG_DATE = "msgDate";
    private static final String KEY_KG_COLUMNID = "_id";
    private static final String KEY_KG_SOCIETY = "kgSoc";
    private static final String KEY_KG_TYPE = "kgType";
    private static final String KEY_KG_FACTOR = "kgFactor";
    private static final String KEY_REJECT_COLUMNID = "_id";
    private static final String KEY_REJECT_SOCIETY = "socId";
    private static final String KEY_REJECT_FAT = "fat";
    private static final String KEY_REJECT_SNF = "snf";
    private static final String KEY_REJECT_CLR = "clr";
    private static final String KEY_REJECT_CLRChecked = "ClrChk";
    private static final String KEY_REJECT_FATChecked = "FatChk";
    // Rejection table param
    private static final String KEY_REJECT_SNFChecked = "SnfChk";
    private static final String KEY_REJECT_CLRRATE = "ClrRate";
    private static final String KEY_REJECT_FATRATE = "FatRate";
    private static final String KEY_REJECT_SNFRATE = "SnfRate";
    private static final String KEY_SETTING_COLUMNID = "_id";
    private static final String KEY_SAMPLE_COLUMN_ID = "_id";
    private static final String KEY_SAMPLE_WEIGH = "sampleWeigh";
    private static final String KEY_SAMPLE_OTHER2 = "sampleOther2";
    private static final String KEY_SAMPLE_OTHER3 = "sampleOther3";
    // Version 4 changes: Keys/Parms for Collection Record Status
    private static final String KEY_COL_REC_COLUMN_ID = "_id";
    private static final String KEY_COL_REC_REPORT_SEQNUM = "seqNum";
    private static final String KEY_COL_REC_REFERENCE_IDX = "reportsTableIdx";
    private static final String KEY_COL_REC_SEND_STATUS = "nwSendStatus";
    private static final String KEY_COL_REC_FARMER_SMS_STATUS = "farmerSMSStatus";
    private static final String KEY_COL_REC_SHIFT_DATA_IDX = "shiftDataIdx";
    private static final String KEY_COL_REC_SHIFT_DATA_COLL_TYPE = "collectionType";
    // Version 4 changes: Keys for SHift Description Data
    private static final String KEY_SHIFT_DATA_COLUMN_ID = "_id";
    private static final String KEY_SHIFT_DATA_COLLECTION_DATE = "shiftCollDate";
    private static final String KEY_SHIFT_DATA_SHIFT_TYPE = "shiftType";
    private static final String KEY_SHIFT_DATA_STARTTIME = "shiftStartTime";
    private static final String KEY_SHIFT_DATA_ENDTIME = "shiftEndTime";
    private static final String KEY_SHIFT_DATA_SOCIETYID = "shiftSocietyID";
    private static final String KEY_SHIFT_DATA_END_STATUS = "shiftEndStatus";
    private static final String KEY_FARMER_STATUS_ID = "_id";
    private static final String KEY_FARMER_STATUS_FARMER_COLID = "fcolId";
    private static final String KEY_FARMER_STATUS_FARMERID = "fFarmerID";
    private static final String KEY_FARMER_STATUS_SENT = "farmSent";
    //Keys for sales
    private static final String KEY_COL_SALES_COLUMN_ID = "_id";
    private static final String KEY_COL_SALES_REPORT_SEQNUM = "salesseqNum";
    private static final String KEY_COL_SALES_REFERENCE_IDX = "salesTableIdx";
    private static final String KEY_COL_SALES_SEND_STATUS = "nwSendStatus";
    private static final String KEY_COL_SALES_FARMER_SMS_STATUS = "salesSMSStatus";
    private static final String KEY_COL_SALES_SHIFT_DATA_IDX = "shiftDataIdx";
    private static final String KEY_SHIFT_SALES_DATA_COLUMN_ID = "_id";
    private static final String KEY_SHIFT_SALES_DATA_COLLECTION_DATE = "shiftCollDate";
    private static final String KEY_SHIFT_SALES_DATA_SHIFT_TYPE = "shiftType";
    private static final String KEY_SHIFT_SALES_DATA_STARTTIME = "shiftStartTime";
    private static final String KEY_SHIFT_SALES_DATA_ENDTIME = "shiftEndTime";
    private static final String KEY_SHIFT_SALES_DATA_SOCIETYID = "shiftSocietyID";
    private static final String KEY_SHIFT_SALES_DATA_END_STATUS = "shiftEndStatus";
    private static final String KEY_CHILLING_COLUMNID = "_id";
    // MY rate Chart
    private static final String KEY_MY_RATECHART_COLUMNID = "_id";
    private static final String KEY_MY_RATECHART_CLASS = "class";
    private static final String KEY_MY_RATECHART_CLASS_DEFAULT_VALUE = "GENERAL";
    private static final String TABLE_LICENSE = "permTable";
    // License table
    private static final String KEY_LICENSE_COLUMNID = "_id";
    private static final String TABLE_DEVICE_INFO = "tableDeviceInfo";
    private static final String PREF_VERSION_CODE_KEY = "app_version_code";
    private static final int DOESNT_EXIST = -1;
    private static final String KEY_DEVICE_BATTERY_PERCENTAGE = "devBatteryPer";
    private static final String KEY_DEVICE_BATTERT_CHARGING = "devCharging";
    private static final String KEY_DEVICE_CHARGING_TYPE = "chargingType";
    private static final String KEY_DEVICE_BATTERY_TEMP = "devBatteryTemp";
    private static final String KEY_DEVICE_BATTERY_HEALTH = "devBatteryHealth";
    private static final String KEY_DEVICE_BATTERY_VOLTAGE = "devBatteryVoltage";
    private static final String KEY_DEVICE_BATTERY_AMBIENT_TEMP = "devAmbTemp";
    private static final String KEY_DEVICE_NETWORK_ASU = "networkAsu";
    private static final String KEY_DEVICE_NETWORK_OPERATOR = "networkOperator";
    private static final String KEY_DEVICE_DATA_STATUS = "deviceDataStatus";
    private static final String KEY_DEVICE_NETWORK_TYPE = "networkType";
    private static final String KEY_DEVICE_SIM_SERIAL_NUM = "simSerialNum";
    private static final String KEY_DEVICE_CURRENT_TIME = "currentTime";
    private static final String KEY_DEVICE_SENT_STATUS = "sentStatus";
    private static final String KEY_DEVICE_LAC = "lac";
    private static final String KEY_DEVICE_CELL_ID = "cellId";
    public static String isSecondary = "isSecondary";
    public static String KEY_WEIGHINGSCALE_BAUDRATE = "weighBaud";
    public static String KEY_WEIGHINGSCALE_NAME = "weighName";
    private static DatabaseTask primaryTask;
    private static DatabaseTask secondaryTask;
    private static SessionManager session;

    private static DatabaseHandler databaseHandler = null;
    //for sales
    private static SQLiteDatabase sqliteDatabase = null;
    private static Context mContext;
    private static DatabaseSync dbSync;
    private static SharedPreferences pref;
    private static SharedPreferences.Editor prefEditor;
    private static DefaultConfigurationHandler configurationHandler;
    private final String defaultWeight = "0.00";

    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    private String CREATE_SAMPLE_TABLE;
    private String CREATE_ASSOC_TABLE;
    private String CREATE_EXCEL_TABLE;
    private String CREATE_TABLE_KGFACTOR;
    private String CREATE_TABLE_MSGDETAILS;
    private String CREATE_VALIDATION_TABLE;
    private String CREATE_SOCIETY_TABLE;
    private String CREATE_USER_TABLE;
    private String CREATE_REJECT_TABLE;
    private String CREATE_TABLE_COLLECTION;
    private String CREATE_COL_RECORD_STATUS;
    private String CREATE_SHIFT_DATA;
    private String CREATE_COLLREC_SEQNUM;
    private String CREATE_FARMER_STATUS;
    private String CREATE_SALES_STATUS;
    private String CREATE_SALES_SHIFT_DATA;
    private String CREATE_COLL_SALES_SEQNUM;
    private String CREATE_FARMER_TABLE_UNIQUE;
    private String CREATE_CHILLING_CENTER_TABLE;
    private String CREATE_TEMP_COL_RECORD_STATUS;
    private String CREATE_MY_RATECHART_TABLE;
    private boolean shouldBackup = false;

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, new DatabaseCorruptionHandler());

    }


    public static DatabaseHandler getDatabaseInstance() {
        synchronized (DatabaseHandler.class) {
            if (databaseHandler == null) {
                initParams(DevAppApplication.getAmcuContext());
                if (databaseHandler == null) {
                    Backup backup = new Backup();

                    boolean primaryExists = backup.checkPrimaryDbExists();
                    boolean secondaryExists = backup.checkSecondaryDbExists();
//                    boolean isPrimarySane = backup.checkPrimaryDbIntegrity();
                    boolean isPrimarySane = true;
//                    boolean isSecondarySane = backup.checkSecondaryDbIntegrity();
                    boolean isSecondarySane = true;
                    Log.d(TAG, "PrimaryExists " + primaryExists);
                    Log.d(TAG, "SecondaryExists " + secondaryExists);
                    Log.d(TAG, "PrimarySane " + isPrimarySane);
                    Log.d(TAG, "SecondarySane " + isSecondarySane);
                    if (primaryExists && isPrimarySane) {
                        if (!(secondaryExists && isSecondarySane)) {
                            Log.d(TAG, "Copying primary to secondary db");
                            backup.backUpDatabase(Backup.PRIMARY);
                        }
                    } else if (secondaryExists && isSecondarySane) {
                        if (DatabaseHandler.DATABASE_VERSION >= backup.getSecondaryDbVersion()) {
                            Log.d(TAG, "Copying secondary to primary db");
                            backup.backUpDatabase(Backup.SECONDARY);
                        }
                    }
                    databaseHandler = new DatabaseHandler(mContext);
                }


                if (sqliteDatabase == null || !sqliteDatabase.isOpen()) {
                    sqliteDatabase = databaseHandler.getWritableDatabase();
                }


            }

        }

        initiliazeTask();
        return databaseHandler;
    }

    private static void initiliazeTask() {
        dbSync = DatabaseSync.getInstance(mContext);
        primaryTask = new DatabaseTask(sqliteDatabase);
        secondaryTask = new DatabaseTask(dbSync.getSqliteDatabase());
        primaryTask.setChainTask(secondaryTask);
        secondaryTask.setChainTask(null);
    }


    private static void initParams(Context context) {
        mContext = context.getApplicationContext();
        session = new SessionManager(mContext);
        // amcuConfig = new AmcuConfig(mContext);
        pref = mContext.getSharedPreferences(ConfigurationConstants.PREF_NAME, Context.MODE_PRIVATE);
        prefEditor = pref.edit();
        configurationHandler = DefaultConfigurationHandler.getInstance();

    }

    // Method to handle sequence Number increments. Note that we currently handle from 1-999
    public static int increment(int seqNum) {

        // If sequence number is 0, it is quite likely that we do not have any records yet.
        if ((seqNum == MAX_SEQ_NUM) || (seqNum == 0)) {
            seqNum = MIN_SEQ_NUM;  // Start the sequence Numnbering
        } else {
            seqNum++;
        }

        return seqNum;
    }

    public static DatabaseTask getPrimaryTask() {
        return primaryTask;
    }

    public static SQLiteDatabase getSecondaryDatabase() {
        return dbSync.getSqliteDatabase();
    }

    //Need to check sqlite exceptions
    public static SQLiteDatabase getPrimaryDatabase() {
        return sqliteDatabase;
    }

    private static boolean isOpen() {
        if (sqliteDatabase != null && sqliteDatabase.isOpen()) {
            return true;
        } else {
            return false;
        }
    }

    public static void closeDatabase() {
        if (isOpen()) {
            sqliteDatabase.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase primaryDatabase) {
        shouldBackup = true;
        Log.i(TAG, "inside onCreate..going to install tables in db..");

        // primaryDatabase.execSQL(createExSeqNumTable());
        createAllQuery();
        primaryDatabase.execSQL(CREATE_MY_RATECHART_TABLE);
        primaryDatabase.execSQL(CREATE_SOCIETY_TABLE);
        primaryDatabase.execSQL(CREATE_USER_TABLE);
        primaryDatabase.execSQL(FarmerTable.CREATE_QUERY);
        primaryDatabase.execSQL(RateTable.CREATE_QUERY);
        primaryDatabase.execSQL(FarmerCollectionTable.CREATE_REPORT_TABLE);
        primaryDatabase.execSQL(CREATE_EXCEL_TABLE);
        primaryDatabase.execSQL(CREATE_ASSOC_TABLE);
        primaryDatabase.execSQL(CREATE_TABLE_MSGDETAILS);
        primaryDatabase.execSQL(CREATE_TABLE_KGFACTOR);
        primaryDatabase.execSQL(CREATE_REJECT_TABLE);
        primaryDatabase.execSQL(CREATE_VALIDATION_TABLE);
        primaryDatabase.execSQL(CREATE_SAMPLE_TABLE);
        primaryDatabase.execSQL(RateChartNameTable.CREATE_QUERY);

        primaryDatabase.execSQL(CREATE_TABLE_COLLECTION);

        // Version 4: Changes
        // Create table for farmer collection records despatch status
        primaryDatabase.execSQL(CREATE_COL_RECORD_STATUS);

        // Create table for Storing Shift Description Data
        primaryDatabase.execSQL(CREATE_SHIFT_DATA);
        // End Version 4 changes

        primaryDatabase.execSQL(CREATE_COLLREC_SEQNUM);
        primaryDatabase.execSQL(SalesCollectionTable.CREATE_SALES_REPORT);
        primaryDatabase.execSQL(DispatchCollectionTable.CREATE_DISPATCH_REPORT);
        primaryDatabase.execSQL(CREATE_SALES_SHIFT_DATA);
        primaryDatabase.execSQL(CREATE_SALES_STATUS);
        primaryDatabase.execSQL(CREATE_COLL_SALES_SEQNUM);
        primaryDatabase.execSQL(CREATE_CHILLING_CENTER_TABLE);
        primaryDatabase.execSQL(LicenseTable.CREATE_QUERY);
        decimalFormat = new DecimalFormat("#0.00");


        primaryDatabase.execSQL(EditRecordCollectionTable.CREATE_EXTENDED_REPORT_TABLE);
        //To initialize the table value with 0
        createSeqNum(primaryDatabase);
        //createExSeqNum(primaryDatabase);
        createSalesSeqNum(primaryDatabase);

        // backup data for colllentionrecord seq num
        //  primaryDatabase.execSQL(CREATE_TEMP_COL_RECORD_STATUS);
        //amcuConfig.setonCreateDBCall(true);
        //amcuConfig.setOnUpgradeOrOnCreate(true);
        primaryDatabase.execSQL(DatabaseEntity.getCreateQueryForConfigurationTable());
        primaryDatabase.execSQL(TruckCCDatabase.getCreateQuery());
        primaryDatabase.execSQL(AgentDatabase.getCreateQuery());
        primaryDatabase.execSQL(TruckCCDatabase.getCreateTruckInfoQuery());
        primaryDatabase.execSQL(TankerDatabase.createQuery());

        primaryDatabase.execSQL(DatabaseEntity.createSplitRecordTable());
        primaryDatabase.execSQL(AddtionalDatabase.getCreateQueryForCustomFields());
        primaryDatabase.execSQL(ExtraParams.CREATE_EXTRA_PARAMS);

        primaryDatabase.execSQL(DatabaseEntity.getCreateQueryForBonusTable());
        primaryDatabase.execSQL(getCreateQueryforDeviceInfo());

        UpdateDatabaseHelper.setSequenceNumTrigger(FarmerCollectionTable.TABLE_REPORTS, primaryDatabase);
        UpdateDatabaseHelper.setSequenceNumTrigger(SalesCollectionTable.TABLE_SALES_REPORT, primaryDatabase);
        UpdateDatabaseHelper.setSequenceNumTrigger(DispatchCollectionTable.TABLE_DISPATCH_REPORT, primaryDatabase);
        UpdateDatabaseHelper.setSequenceNumTrigger(ExtraParams.TABLE_EXTRA_PARAMS, primaryDatabase);

        UpdateDatabaseHelper.setSequenceNumTrigger(SplitRecordTable.TABLE, primaryDatabase);
        UpdateDatabaseHelper.setSequenceNumTrigger(TankerCollectionTable.TABLE_TANKER, primaryDatabase);
        UpdateDatabaseHelper.setSequenceNumTrigger(EditRecordCollectionTable.TABLE_EXTENDED_REPORT, primaryDatabase);

        UpdateDatabaseHelper.triggerOnReportTableInsert(primaryDatabase);
        UpdateDatabaseHelper.triggerForPreUpdate(primaryDatabase);
        UpdateDatabaseHelper.triggerForPostUpdate(primaryDatabase);
        UpdateDatabaseHelper.addCollectionDateIndexOnReportsTable(primaryDatabase);
        UpdateDatabaseHelper.addCollectionTimeIndexOnReportsTable(primaryDatabase);
        primaryDatabase.execSQL(DatabaseEntity.getCreateQueryForEssaeTable());
        //  primaryDatabase.execSQL(ConfigSyncStatusTable.CREATE_QUERY);

        initializeDatabase(primaryDatabase);
        addDefaultFarmer(primaryDatabase);
        prefEditor.putBoolean(ConfigurationConstants.ON_CREATE_CALL_LIC, true);
        prefEditor.apply();
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (shouldBackup) {
            Backup backup = new Backup();
            backup.backUpDatabase(Backup.PRIMARY);
            shouldBackup = false;
        }
        restartApplication();
    }
// changes starts here

    // chk if upgrade needs to be handled. If so, check for configuration changes on app upgrade.
    public void handleAppUpgrade(SQLiteDatabase primaryDatabase, int lastDb) {

        // Get current version code
        int currentVersionCode = BuildConfig.VERSION_CODE;

        // Get saved version code
        int savedVersionCode = pref.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {

            // TODO Either its a new installation (or the shared preferences is cleared)
            //check whether configuration table is empty or not.
            if (getConfigurationCount() == 0) {
                DefaultConfigurationHandler.initConfigTable(primaryDatabase);
                DefaultConfigurationHandler.loadAllConfigurations();
            }
            //To support 11.6.9Apk
            if (lastDb != 0 && lastDb < 40) {
                handleConfigChangeOnAppUpgrade();
            }
        } else if (currentVersionCode > savedVersionCode) {
            //check whether configuration table is empty or not.
            if (getConfigurationCount() == 0) {
                DefaultConfigurationHandler.initConfigTable(primaryDatabase);
                DefaultConfigurationHandler.loadAllConfigurations();
            }
            // TODO This is an upgrade.
            handleConfigChangeOnAppUpgrade();
        }

        // Update the shared preferences with the current version code
        pref.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).apply();
    }

    // check if any configurations needs to be modified
    private void handleConfigChangeOnAppUpgrade() {

        //read props from assets folder of newly upgraded app.
        Properties newConfigProps = DefaultConfigurationHandler.loadConfigProperties(
                configurationHandler.DEFAULT_CONFIG_FILENAME);

        //read existing props from db.
        Properties existingConfigInDB = getAllConfigurations();

        // get all keys defined in props from assets folder
        Set<String> newConfigKeys = newConfigProps.stringPropertyNames();
        // get all keys stored in props from db.
        Set<String> existingConfigKeys = existingConfigInDB.stringPropertyNames();

        // set of configs to be inserted in DB.
        SetUtils.SetView<String> configKeystoBeAdded = SetUtils.difference(newConfigKeys, existingConfigKeys);

        // set of configs present in existing config table but not in new props in assets
        SetUtils.SetView<String> configKeysToBeDeleted = SetUtils.difference(existingConfigKeys, newConfigKeys);

        // add new configs to db
        Iterator<String> it = configKeystoBeAdded.iterator();
        while (it.hasNext()) {
            String key = it.next();
            ConfigurationElement configElement = new ConfigurationElement();
            configElement.setKey(key);
            configElement.setValue(newConfigProps.getProperty(key));
            addConfiguration(configElement);
        }

        // remove configs from db
        it = configKeysToBeDeleted.iterator();
        while (it.hasNext()) {
            deleteConfiguration(it.next());
        }

    }


    // Load all configurations into Properties
    public Properties getAllConfigurations() {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONFIGURATION;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        Properties properties = new Properties();

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String key = cursor.getString(1);
                String value = cursor.getString(2);
                if (value != null)
                    properties.setProperty(key, value);
                else
                    properties.setProperty(key, "");

            } while (cursor.moveToNext());
        }
        cursor.close();

        return properties;
    }

    // Get count of configurations
    private int getConfigurationCount() {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String countQuery = "SELECT  * FROM " + TABLE_CONFIGURATION;
        Cursor cursor = primaryDatabase.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }


    // Adding new config
    private void addConfiguration(ConfigurationElement config) {
        ContentValues values = new ContentValues();
        values.put(CONFIGURATION_KEY, config.getKey());
        values.put(CONFIGURATION_VALUE, config.getValue());
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        // Inserting Row
        primaryDatabase.insert(TABLE_CONFIGURATION, null, values);

    }


    // Delete single config
    private void deleteConfiguration(String key) {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        primaryDatabase.delete(TABLE_CONFIGURATION, CONFIGURATION_KEY + " = ?",
                new String[]{key});

    }
    //change ends here


    private void createAllQuery() {

        // Space required before TEXT

        CREATE_ASSOC_TABLE = "CREATE TABLE " + TABLE_ASSOCSOC + "("
                + KEY_USER_COLUMNID + " INTEGER PRIMARY KEY, "
                + KEY_ASSOCS_ASSOCID + " TEXT," + KEY_ASSOCS_SOCID + " TEXT,"
                + KEY_ASSOCS_ASSOCName + " TEXT" + ")";

        CREATE_EXCEL_TABLE = "CREATE TABLE " + TABLE_EXCEL + "("
                + FarmerCollectionTable.KEY_REPORT_COLUMNID + " INTEGER PRIMARY KEY, "
                + FarmerCollectionTable.KEY_REPORT_FARMERID + " TEXT," + FarmerCollectionTable.KEY_REPORT_NAME + " TEXT,"
                + FarmerCollectionTable.KEY_REPORT_SNF + " TEXT," + FarmerCollectionTable.KEY_REPORT_FAT + " TEXT,"
                + FarmerCollectionTable.KEY_REPORT_USER + " TEXT," + FarmerCollectionTable.KEY_REPORT_RATE + " TEXT" + ")";

        CREATE_FARMER_TABLE_UNIQUE = "CREATE TABLE " + TABLE_FARMER_TEMP + "("
                + KEY_FARMER_COLUMNID + " INTEGER PRIMARY KEY, "
                + KEY_FARMER_BARCODE + " TEXT," + KEY_FARMER_NAME + " TEXT,"
                + KEY_FARMER_CATTLE + " TEXT," + KEY_FARMER_SOCCODE + " TEXT,"
                + KEY_FARMER_MOBILE + " TEXT," + KEY_FARMER_EMAIL + " TEXT,"
                + KEY_FARMER_ID + " TEXT NOT NULL UNIQUE," + KEY_FARMER_CANS + " TEXT,"
                + KEY_FARMER_COW + " TEXT," + KEY_FARMER_BUFFALO + " TEXT,"
                + KEY_FARMER_NO_CATTLE + " TEXT," + KEY_FARMER_ASSIGN_RATECHART
                + " TEXT," + KEY_FARMER_REGISTRATION_DATE + " INTEGER,"
                + KEY_FARMER_WEEKLY + " INTEGER," + KEY_FARMER_MONTHLY
                + " INTEGER," + KEY_ENABLE_MULTIPLECANS + " TEXT" + ")";

        CREATE_TABLE_KGFACTOR = "CREATE TABLE " + TABLE_KGFACTOR + "("
                + KEY_KG_COLUMNID + " INTEGER PRIMARY KEY, " + KEY_KG_SOCIETY
                + " TEXT," + KEY_KG_TYPE + " TEXT," + KEY_KG_FACTOR + " TEXT"
                + ")";
        CREATE_TABLE_MSGDETAILS = "CREATE TABLE " + TABLE_MSGDETAILS + "("
                + KEY_MSG_COLUMNID + " INTEGER PRIMARY KEY, " + KEY_MSG_SOCIETY
                + " TEXT," + KEY_MSG_DATE + " TEXT," + KEY_MSG_LIMIT + " TEXT,"
                + KEY_MSG_SENT + " TEXT" + ")";

        CREATE_TABLE_COLLECTION = "CREATE TABLE " + TABLE_COLLECTION + "("
                + KEY_COLLECTION_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + KEY_COLLECTION_DATE + " TEXT," + KEY_COLLECTION_SHIFT
                + " TEXT," + KEY_COLLECTION_DUMMY1 + " TEXT,"
                + KEY_COLLECTION_DUMMY2 + " TEXT," + KEY_COLLECTION_DUMMY3
                + " TEXT" + ")";

        CREATE_SOCIETY_TABLE = "CREATE TABLE " + TABLE_SOCIETY + "("
                + KEY_SOCIETY_COLUMNID + " INTEGER PRIMARY KEY, "
                + KEY_SOCIETY_ADDRESS + " TEXT," + KEY_SOCIETY_BMCID + " TEXT,"
                + KEY_SOCIETY_CODE + " TEXT," + KEY_SOCIETY_CONTACT1 + " TEXT,"
                + KEY_SOCIETY_LOCATION + " TEXT," + KEY_SOCIETY_ROUTE
                + " TEXT," + KEY_SOCIETY_EMAIL1 + " TEXT,"
                + KEY_SOCIETY_CONPERSON1 + " TEXT," + KEY_SOCIETY_NAME
                + " TEXT," + KEY_SOCIETY_EMAIL2 + " TEXT,"
                + KEY_SOCIETY_CONPERSON2 + " TEXT," + KEY_SOCIETY_CONTACT2
                + " TEXT," + KEY_SOCIETY_NUMFARMER + " TEXT,"
                + KEY_SOCIETY_REGISTRATION_DATE + " INTEGER,"
                + KEY_SOCIETY_WEEKLY + " INTEGER," + KEY_SOCIETY_MONTHLY
                + " INTEGER" + ")";

        CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_COLUMNID + " INTEGER PRIMARY KEY, " + KEY_USER_EMAIL
                + " TEXT," + KEY_USER_MOBILE + " TEXT," + KEY_USER_NAME
                + " TEXT," + KEY_USER_PASSWORD + " TEXT," + KEY_USER_ROLE
                + " TEXT," + KEY_USER_SOCIETYID + " TEXT," + KEY_USER_USERID
                + " TEXT," + KEY_USER_REGDATE + " INTEGER," + KEY_USER_WEEKDATE
                + " INTEGER," + KEY_USER_MONTHDATE + " INTEGER," +
                KEY_USER_SENT + " INTEGER" + ")";

        CREATE_REJECT_TABLE = "CREATE TABLE " + TABLE_REJECTION_PARAM + "("
                + KEY_REJECT_COLUMNID + " INTEGER PRIMARY KEY, "
                + KEY_REJECT_CLR + " TEXT," + KEY_REJECT_FAT + " TEXT,"
                + KEY_REJECT_SNF + " TEXT," + KEY_REJECT_SOCIETY + " TEXT,"
                + KEY_REJECT_CLRChecked + " TEXT," + KEY_REJECT_FATChecked
                + " TEXT," + KEY_REJECT_SNFChecked + " TEXT,"
                + KEY_REJECT_CLRRATE + " TEXT," + KEY_REJECT_FATRATE + " TEXT,"
                + KEY_REJECT_SNFRATE + " TEXT" + ")";

        CREATE_VALIDATION_TABLE = "CREATE TABLE " + TABLE_VALIDATION + "("
                + KEY_VALIDATION_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + KEY_VALIDATION_FAT + " TEXT," + KEY_VALIDATION_SNF + " TEXT,"
                + KEY_VALIDATION_DUMMY + " TEXT" + ")";


        CREATE_SAMPLE_TABLE = "CREATE TABLE " + TABLE_SAMPLE + "("
                + KEY_SAMPLE_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + KEY_SAMPLE_MODE + " TEXT," + KEY_SAMPLE_FAT + " TEXT,"
                + KEY_SAMPLE_IS_FAT + " TEXT," + KEY_SAMPLE_SNF + " TEXT,"
                + KEY_SAMPLE_IS_SNF + " TEXT," + KEY_SAMPLE_WEIGH + " TEXT,"
                + KEY_SAMPLE_IS_WEIGH + " TEXT," + KEY_SAMPLE_BARCODE
                + " TEXT," + KEY_SAMPLE_OTHER2 + " TEXT," + KEY_SAMPLE_OTHER3
                + " TEXT," + KEY_SAMPLE_RATE + " TEXT," + KEY_SAMPLE_AMOUNT
                + " TEXT," + KEY_SAMPLE_SOC_ID + " TEXT," + KEY_SAMPLE_ID
                + " TEXT," + KEY_SAMPLE_SENT + " INTEGER" + ")";


        CREATE_MY_RATECHART_TABLE = "CREATE TABLE " + TABLE_MY_RATE_CHART + "("
                + KEY_MY_RATECHART_COLUMNID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_MY_RATECHART_NAME + " TEXT UNIQUE NOT NULL, "
                + KEY_MY_RATECHART_MILIDATE + " INTEGER,"
                + KEY_MY_RATECHART_MILK_TYPE + " TEXT," + KEY_MY_RATECHART_KG_FAT_RATE + " TEXT,"
                + KEY_MY_RATECHART_START_FAT + " TEXT," + KEY_MY_RATECHART_END_FAT + " TEXT,"
                + KEY_MY_RATECHART_KG_SNF_RATE + " TEXT," + KEY_MY_RATECHART_START_SNF + " TEXT,"
                + KEY_MY_RATECHART_END_SNF + " TEXT," + KEY_MY_RATECHART_START_SHIFT + " TEXT,"
                + KEY_MY_RATECHART_CLASS + " TEXT DEFAULT " + KEY_MY_RATECHART_CLASS_DEFAULT_VALUE + ","
                + KEY_MY_RATECHART_START_DATE + " INTEGER," + KEY_MY_RATECHART_END_SHIFT + " TEXT,"
                + KEY_MY_RATECHART_END_DATE + " INTEGER)";

        // Version 4 changes
        // Introduced to add status for coll records sent
        createCollRecStatusTable();

        // Create End SHift Related Table
        createShiftData();
        createSeqNumTable();
        // End Version 4 changes
        createCollSalesStatusTable();
        createSalesShiftData();
        createSalesSeqNumTable();
        createChillingCenterTable();

    }

    private void createChillingCenterTable() {
        CREATE_CHILLING_CENTER_TABLE = "Create Table " + TABLE_CHILLING_CENTER + "(" + KEY_CHILLING_COLUMNID + " INTEGER PRIMARY KEY, "
                + KEY_CHILLING_ID + " TEXT NOT NULL, " + KEY_CHILLING_CENTER_ID + " TEXT UNIQUE NOT NULL, "
                + KEY_CHILLING_CENTER_NAME + " TEXT, "
                + KEY_CHILLING_CONTACT_PERSON + " TEXT, " + KEY_CHILLING_CONTACT_NUMBER + " TEXT, "
                + KEY_CHILLING_CONTACT_EMAILID + " TEXT, " + KEY_CHILLING_REG_DATE + " INTEGER, "
                + KEY_CHILLING_BARCODE + " TEXT UNIQUE, " + KEY_CHILLING_ROUTE + " TEXT, "
                + KEY_CHILLING_SINGLE_MULTIPLE + " TEXT," + KEY_CHILLING_MILK_TYPE + " TEXT,"
                + KEY_CHILLING_CENTER_SENT + " INTEGER, " + KEY_CHILLING_IS_ACTIVE + " TEXT " + ")";

    }

    private String createCollRecStatusTable() {
        // New Table introduced in DB Version 4

        CREATE_COL_RECORD_STATUS = "CREATE TABLE " + TABLE_COL_REC_STATUS + "("
                + KEY_COL_REC_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + KEY_COL_REC_REFERENCE_IDX + " INTEGER NOT NULL, "
                + KEY_COL_REC_REPORT_SEQNUM + " INTEGER, "        // Check if a range of 0-999 can be enabled for this
                + KEY_COL_REC_SEND_STATUS + " INTEGER, "
                + KEY_COL_REC_FARMER_SMS_STATUS + " INTEGER,"
                + KEY_COL_REC_SHIFT_DATA_IDX + " INTEGER,"
                + KEY_COL_REC_SHIFT_DATA_COLL_TYPE + " TEXT, " +
                " UNIQUE (" + KEY_COL_REC_REFERENCE_IDX + "," + KEY_COL_REC_SHIFT_DATA_COLL_TYPE
                + " )" + " )";

        return CREATE_COL_RECORD_STATUS;
    }

    //Add collection type in sequnce reference table
    private String alterRecRefTable() {
        return "ALTER " + "TABLE " + TABLE_COL_REC_STATUS + " ADD " +
                "COLUMN " + KEY_COL_REC_SHIFT_DATA_COLL_TYPE + " TEXT" + " DEFAULT '" + Util.REPORT_TYPE_FARMER + "'";
    }

    private void alterRecRefTableForUnique(SQLiteDatabase primaryDatabase) {
        String TEMP_TABLE = "temp_ref_status_table";
        String renameQuery = " ALTER TABLE " + TABLE_COL_REC_STATUS + " RENAME TO " + TEMP_TABLE;
        primaryDatabase.execSQL(renameQuery);
        primaryDatabase.execSQL(createCollRecStatusTable());
        String copyQuery = " INSERT INTO " + TABLE_COL_REC_STATUS + " SELECT " +
                " * FROM " + TEMP_TABLE;
        primaryDatabase.execSQL(copyQuery);
        primaryDatabase.execSQL("DROP TABLE " + TEMP_TABLE);
    }

    private void createTempCollRecStatusTable() {
        // New Table introduced in DB Version 4
        CREATE_TEMP_COL_RECORD_STATUS = "CREATE TABLE " + TABLE_TEMP_COL_REC_STATUS + "("
                + KEY_COL_REC_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + KEY_COL_REC_REFERENCE_IDX + " INTEGER UNIQUE NOT NULL, "
                + KEY_COL_REC_REPORT_SEQNUM + " INTEGER, "        // Check if a range of 0-999 can be enabled for this
                + KEY_COL_REC_SEND_STATUS + " INTEGER, "
                + KEY_COL_REC_FARMER_SMS_STATUS + " INTEGER,"
                + KEY_COL_REC_SHIFT_DATA_IDX + " INTEGER)";
    }

    /**
     * Create ShiftData Table
     */
    private void createShiftData() {
        // New Table introduced in DB Version 4
        CREATE_SHIFT_DATA = "CREATE TABLE " + TABLE_SHIFT_DATA + "("
                + KEY_SHIFT_DATA_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + KEY_SHIFT_DATA_COLLECTION_DATE + " TEXT,"
                + KEY_SHIFT_DATA_SHIFT_TYPE + " TEXT,"
                + KEY_SHIFT_DATA_STARTTIME + " TEXT,"
                + KEY_SHIFT_DATA_ENDTIME + " TEXT,"
                + KEY_SHIFT_DATA_SOCIETYID + " TEXT,"
                + KEY_SHIFT_DATA_END_STATUS + " INTEGER" + ")";
    }

    private void createSeqNumTable() {
        CREATE_COLLREC_SEQNUM = "CREATE TABLE " + TABLE_COLL_REC_SEQ_NUM + "("
                + KEY_COLL_REC_SEQNUM + " INTEGER PRIMARY KEY" + ")";
    }

    private void createFarmerStatus() {
        CREATE_FARMER_STATUS = "CREATE TABLE " + TABLE_FARMER_STATUS + "(" +
                KEY_FARMER_STATUS_ID + " INTEGER PRIMARY KEY," + KEY_FARMER_STATUS_FARMER_COLID
                + " INTEGER," + KEY_FARMER_STATUS_FARMERID + " TEXT," + KEY_FARMER_STATUS_SENT +
                " INTEGER" + ")";
    }

    private void createCollSalesStatusTable() {
        CREATE_SALES_STATUS = "CREATE TABLE " + TABLE_COLL_SALES_STATUS + "("
                + KEY_COL_SALES_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + KEY_COL_SALES_REFERENCE_IDX + " INTEGER, "
                + KEY_COL_SALES_REPORT_SEQNUM + " INTEGER, "        // Check if a range of 0-999 can be enabled for this
                + KEY_COL_SALES_SEND_STATUS + " INTEGER, "
                + KEY_COL_SALES_FARMER_SMS_STATUS + " INTEGER,"
                + KEY_COL_SALES_SHIFT_DATA_IDX + " INTEGER)";
    }

    /**
     * Create ShiftData Table
     */
    private void createSalesShiftData() {
        // New Table introduced in DB Version 4
        CREATE_SALES_SHIFT_DATA = "CREATE TABLE " + TABLE_SHIFT_SALES_DATA + "("
                + KEY_SHIFT_SALES_DATA_COLUMN_ID + " INTEGER PRIMARY KEY, "
                + KEY_SHIFT_SALES_DATA_COLLECTION_DATE + " TEXT,"
                + KEY_SHIFT_SALES_DATA_SHIFT_TYPE + " TEXT,"
                + KEY_SHIFT_SALES_DATA_STARTTIME + " TEXT,"
                + KEY_SHIFT_SALES_DATA_ENDTIME + " TEXT,"
                + KEY_SHIFT_SALES_DATA_SOCIETYID + " TEXT,"
                + KEY_SHIFT_SALES_DATA_END_STATUS + " INTEGER" + ")";
    }


    private void createSalesSeqNumTable() {
        CREATE_COLL_SALES_SEQNUM = "CREATE TABLE " + TABLE_COLL_SALES_SEQ_NUMBER + "("
                + KEY_COLL_SALES_SEQNUM + " INTEGER PRIMARY KEY" + ")";
    }

    private void createSeqNum(SQLiteDatabase primaryDatabase) {

        ContentValues values = new ContentValues();
        // Update the table with a sentinel value
        values.put(KEY_COLL_REC_SEQNUM, 0);
        primaryDatabase.insert(TABLE_COLL_REC_SEQ_NUM, null, values);
    }

    private void createSalesSeqNum(SQLiteDatabase primaryDatabase) {

        ContentValues values = new ContentValues();
        // Update the table with a sentinel value
        values.put(KEY_COLL_SALES_SEQNUM, 0);
        primaryDatabase.insert(TABLE_COLL_SALES_SEQ_NUMBER, null, values);
    }


    @Override
    public void onUpgrade(SQLiteDatabase primaryDatabase, int oldVersion, int newVersion) {
        sqliteDatabase = primaryDatabase;
        shouldBackup = true;
        createAllQuery();
        prefEditor.putBoolean(ConfigurationConstants.ON_CREATEON_UPGRADE, true);
        prefEditor.apply();
        UpdateDatabaseHelper updateDatabaseHelper = new UpdateDatabaseHelper(primaryDatabase);

        switch (oldVersion) {
            case VERSION_FIRST:

                primaryDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
                primaryDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCEL);
                primaryDatabase.execSQL(CREATE_TABLE_COLLECTION);

                break;

            case VERSION_SECOND:
                primaryDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
                primaryDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_EXCEL);
                primaryDatabase.execSQL(CREATE_TABLE_COLLECTION);

                primaryDatabase.execSQL(FarmerCollectionTable.CREATE_REPORT_TABLE);
                primaryDatabase.execSQL(CREATE_EXCEL_TABLE);

                // Create table for farmer collection records despatch status
                primaryDatabase.execSQL(CREATE_COL_RECORD_STATUS);
                // Create table for Storing Shift Description Data
                primaryDatabase.execSQL(CREATE_SHIFT_DATA);
                primaryDatabase.execSQL(CREATE_COLLREC_SEQNUM);
                createSeqNum(primaryDatabase);

                break;

            case VERSION_THIRD:
                // Create table for farmer collection records despatch status
                primaryDatabase.execSQL(CREATE_COL_RECORD_STATUS);
                // Create table for Storing Shift Description Data
                primaryDatabase.execSQL(CREATE_SHIFT_DATA);
                primaryDatabase.execSQL(CREATE_COLLREC_SEQNUM);
                createSeqNum(primaryDatabase);
                // Updated farmer table with entry allow multiple cans
                primaryDatabase.execSQL(alterFarmerTable());
                break;

            case VERSION_FOURTH:
                primaryDatabase.execSQL(alterReportTable());
                //This should support for both version 5 and 6
                primaryDatabase.execSQL(updateSMSStatusInCollection());

                //To support 7
                createFarmerStatus();
                primaryDatabase.execSQL(CREATE_FARMER_STATUS);
                primaryDatabase.execSQL(alterReportTableWithType());

                // Table create for sales
                primaryDatabase.execSQL(SalesCollectionTable.CREATE_SALES_REPORT);
                primaryDatabase.execSQL(CREATE_SALES_SHIFT_DATA);
                primaryDatabase.execSQL(CREATE_SALES_STATUS);
                primaryDatabase.execSQL(CREATE_COLL_SALES_SEQNUM);
                createSalesSeqNum(primaryDatabase);
                // Updated farmer table with entry allow multiple cans
                primaryDatabase.execSQL(alterFarmerTable());

                // for make the famer Id unique, and added rate chart type
                alterFarmerTableConstraints(primaryDatabase);
                primaryDatabase.execSQL(alterRateChartType());

                break;
            case VERSION_FIFTH:
                primaryDatabase.execSQL(updateSMSStatusInCollection());
                //To support 7
                createFarmerStatus();
                primaryDatabase.execSQL(CREATE_FARMER_STATUS);
                primaryDatabase.execSQL(alterReportTableWithType());

                // Table create for sales
                primaryDatabase.execSQL(SalesCollectionTable.CREATE_SALES_REPORT);
                primaryDatabase.execSQL(CREATE_SALES_SHIFT_DATA);
                primaryDatabase.execSQL(CREATE_SALES_STATUS);
                primaryDatabase.execSQL(CREATE_COLL_SALES_SEQNUM);
                createSalesSeqNum(primaryDatabase);
                // Updated farmer table with entry allow multiple cans
                primaryDatabase.execSQL(alterFarmerTable());

                // for make the famer Id unique, and added rate chart type
                alterFarmerTableConstraints(primaryDatabase);
                primaryDatabase.execSQL(alterRateChartType());

                break;

            case VERSION_SIXTH:
                createFarmerStatus();
                primaryDatabase.execSQL(CREATE_FARMER_STATUS);
                primaryDatabase.execSQL(alterReportTableWithType());

                // Table create for sales
                primaryDatabase.execSQL(SalesCollectionTable.CREATE_SALES_REPORT);
                primaryDatabase.execSQL(CREATE_SALES_SHIFT_DATA);
                primaryDatabase.execSQL(CREATE_SALES_STATUS);
                primaryDatabase.execSQL(CREATE_COLL_SALES_SEQNUM);
                createSalesSeqNum(primaryDatabase);
                // Updated farmer table with entry allow multiple cans
                primaryDatabase.execSQL(alterFarmerTable());

                // for make the famer Id unique, and added rate chart type
                alterFarmerTableConstraints(primaryDatabase);
                primaryDatabase.execSQL(alterRateChartType());

                primaryDatabase.execSQL(CREATE_CHILLING_CENTER_TABLE);

                break;

            case VERSION_SEVENTH:
                // Table create for sales
                primaryDatabase.execSQL(SalesCollectionTable.CREATE_SALES_REPORT);
                primaryDatabase.execSQL(CREATE_SALES_SHIFT_DATA);
                primaryDatabase.execSQL(CREATE_SALES_STATUS);
                primaryDatabase.execSQL(CREATE_COLL_SALES_SEQNUM);
                // Updated farmer table with entry allow multiple cans
                primaryDatabase.execSQL(alterFarmerTable());
                createSalesSeqNum(primaryDatabase);

                // for make the famer Id unique, and added rate chart type
                alterFarmerTableConstraints(primaryDatabase);
                primaryDatabase.execSQL(alterRateChartType());


                primaryDatabase.execSQL(CREATE_CHILLING_CENTER_TABLE);

                break;

            case VERSION_EIGHTH:
                // Updated farmer table with entry allow multiple cans
                primaryDatabase.execSQL(alterFarmerTable());


            case VERSION_NINTH:
                // for make the famer Id unique, and added rate chart type
                alterFarmerTableConstraints(primaryDatabase);
                primaryDatabase.execSQL(alterRateChartType());
                primaryDatabase.execSQL(alterSalesTable());
                primaryDatabase.execSQL(alterSalesTableColumn2());


            case VERSION_TENTH:

            case VERSION_ELEVENTH:

                primaryDatabase.execSQL(CREATE_CHILLING_CENTER_TABLE);

            case VERSION_TWELFTH:

                //changes in version 12
                alterReportTableWithRemarksConstraints(primaryDatabase);

            case VERSION_THIRTEENTH:

                //Changes version 13
                alterReportForRate(primaryDatabase);

            case VERSION_FOURTEENTH:

                //Change in version 14
                alterReportForNumberOfCans(primaryDatabase);

            case VERSION_FIFTEEN:

                //Change in version 15
                alterChillingCenterForRoute(primaryDatabase);

            case VERSION_SIXTEENTH:

                //Change in version 16
                alterReportForSequenceNumber(primaryDatabase);
                alterReportForRoute(primaryDatabase);

            case VERSION_SEVENTEENTH:

                //Change in version 17
                alterReportForRecordStatus(primaryDatabase);

            case VERSION_EIGHTEENTH:

                //change in version 18

            case VERSION_NINETEENTH:

                //Change in version 19

                // primaryDatabase.execSQL(createExSeqNumTable());
                primaryDatabase.execSQL(EditRecordCollectionTable.CREATE_EXTENDED_REPORT_TABLE);
                //createExSeqNum(primaryDatabase);

                altercolStatusTable(primaryDatabase);

            case VERSION_TWENTY:

                //Create query less than equals to version 20
                primaryDatabase.execSQL(CREATE_MY_RATECHART_TABLE);

            case VERSION_TWENTY_ONE:

                //Change in version 21
                alterReportTableForRateChartName(primaryDatabase);
                alterReportForKgWeight(primaryDatabase);
                alterReportForLtrsWeight(primaryDatabase);
            case VERSION_TWENTY_TWO:

                //Create query for less than version 23
                primaryDatabase.execSQL(LicenseTable.CREATE_QUERY);


            case VERSION_TWENTY_THREE:
                alterChillingCenterTableForSingleOrMultiple(primaryDatabase);

            case VERSION_TWENTY_FOUR:
                alterReportTableWithTippingStartTime(primaryDatabase);
                alterReportTableWithTippingEndTime(primaryDatabase);
                alterReportTableWithAgentId(primaryDatabase);
                alterReportTableWithMilkStatusCode(primaryDatabase);
                alterChillingCenterTableForMilkType(primaryDatabase);

                //Create query for less  than or equals to  version 24
                primaryDatabase.execSQL(TruckCCDatabase.getCreateQuery());
                primaryDatabase.execSQL(AgentDatabase.getCreateQuery());
                primaryDatabase.execSQL(TruckCCDatabase.getCreateTruckInfoQuery());

            case VERSION_TWENTY_FIVE:
                //Commented in 11.4.2
                // alterTruckTableConstraints(primaryDatabase);
            case VERSION_TWENTY_SIX:
                if (oldVersion > 19) {
                    alterEXTReportTableWithTippingStartTime(primaryDatabase);
                    alterEXTReportTableWithTippingEndTime(primaryDatabase);
                    alterEXTReportTableWithAgentId(primaryDatabase);
                    alterEXTReportTableWithMilkStatusCode(primaryDatabase);
                }


            case VERSION_TWENTY_SEVEN:
                alterReportTableWithRateCalculation(primaryDatabase);
                alterReportTableWithMASerialNum(primaryDatabase);
                alterReportTableWithMAName(primaryDatabase);

                if (oldVersion > 19) {
                    alterEXTReportTableWithRateCalculation(primaryDatabase);
                    alterEXTReportTableWithMASerialNum(primaryDatabase);
                    alterEXTReportTableWithMAName(primaryDatabase);
                }


            case VERSION_TWENTY_EIGHT:
                if (oldVersion > 19) {
                    alterExtendedReportForKgWeight(primaryDatabase);
                    alterExtendedReportForLtrsWeight(primaryDatabase);
                }


            case VERSION_TWENTY_NINE:
                //Create query for less than and equal to version 29
                primaryDatabase.execSQL(TankerDatabase.createQuery());

            case VERSION_THIRTY:
                alterFarmerWithSentStatus(primaryDatabase);
                alterUserSentStatus(primaryDatabase);
                alterChillingWithSentStatus(primaryDatabase);
                alterSampleSentStatus(primaryDatabase);

            case VERSION_THIRTY_ONE: {
                primaryDatabase.execSQL(DatabaseEntity.getCreateQueryForConfigurationTable());
                //  updateConfigurationTableWithMissingItems(primaryDatabase);
                /* TODO
                 * on app upgrade, (db verison = 31)
                 *  a) read shared preference values and save it  db config tbale.
                 */


            }
            case VERSION_THIRY_TWO:
                alterTankerTable(primaryDatabase, TankerDatabase.KEY_CLR);
                alterTankerTable(primaryDatabase, TankerDatabase.KEY_TEMP);


            case VERSION_THIRTY_THREE: {
                alterConfigurationTableWithSocietyId(primaryDatabase);

                if (checkIfTableHaveValue(DatabaseEntity.TABLE_CONFIGURATION, primaryDatabase) <= 1) {
                    // TODO load props into config table with default values
                    DefaultConfigurationHandler.initConfigTable(primaryDatabase);
                    DefaultConfigurationHandler.loadAllConfigurations();
                }
                if (!toCheckTheColumnInTable(primaryDatabase,
                        TABLE_COL_REC_STATUS, KEY_COL_REC_SHIFT_DATA_COLL_TYPE)) {
                    primaryDatabase.execSQL(alterRecRefTable());
                    alterRecRefTableForUnique(primaryDatabase);
                }

            }

            case VERSION_THIRTY_FOUR:

                if (!toCheckTheColumnInTable(primaryDatabase, TABLE_FARMER, KEY_AGENT_ID)) {
                    primaryDatabase.execSQL(alterFarmerTableAgentId());

                }
                primaryDatabase.execSQL(alterFarmerTableFarmerType());

                if (!toCheckTheColumnInTable(primaryDatabase,
                        TABLE_REPORTS, FarmerCollectionTable.KEY_REPORT_PH)) {
                    alterReportTableWithMAParams(primaryDatabase);
                }
                primaryDatabase.execSQL(DatabaseEntity.createSplitRecordTable());

                primaryDatabase.execSQL(AddtionalDatabase.getCreateQueryForCustomFields());
                primaryDatabase.execSQL(ExtraParams.CREATE_EXTRA_PARAMS);

            case VERSION_THIRTY_FIVE:
                if (!toCheckTheColumnInTable(primaryDatabase,
                        TABLE_REPORTS, FarmerCollectionTable.KEY_REPORT_INCENTIVE_RATE)) {
                    alterReportTableWithTOTalBonus(primaryDatabase);
                }
                //  updateConfigurationTableWithMissingItems(primaryDatabase);
                primaryDatabase.execSQL(DatabaseEntity.getCreateQueryForBonusTable());
                primaryDatabase.execSQL(getCreateQueryforDeviceInfo());


            case VERSION_THIRTY_SIX:
                //  updateConfigurationTableWithMissingItems(primaryDatabase);
                if (!toCheckTheColumnInTable(primaryDatabase,
                        TABLE_CHILLING_CENTER, KEY_CHILLING_IS_ACTIVE)) {

                    alterReportTableWithIsActive(primaryDatabase);
                }
            case VERSION_THIRTY_SEVEN:
                clearSampleTable(primaryDatabase);
                ArrayList<SampleDataEntity> allSampleDataEntity = new GetEntity(mContext, pref).getDefaultSample();
                createSampleTable(primaryDatabase, allSampleDataEntity);

            case VERSION_THIRTY_EIGHT:

            case VERSION_THIRTY_NINE:
//
//                updateDatabaseHelper.version40DaoUpgrade();

            case VERSION_FORTY:
                addDefaultFarmer(primaryDatabase);


            case VERSION_FORTY_ONE:
                updateDatabaseHelper.alterReportTableWithModifiedTime();
                UpdateDatabaseHelper.triggerOnReportTableInsert(primaryDatabase);
                UpdateDatabaseHelper.triggerForPreUpdate(primaryDatabase);
                UpdateDatabaseHelper.triggerForPostUpdate(primaryDatabase);
                primaryDatabase.execSQL(DatabaseEntity.getCreateQueryForEssaeTable());
                if (oldVersion < VERSION_FORTY)
                    updateDatabaseHelper.version40DaoUpgrade();
            case VERSION_FORTY_TWO:
            case VERSION_FORTY_THREE:
            case VERSION_FORTY_FOUR:
                primaryDatabase.execSQL(DispatchCollectionTable.CREATE_DISPATCH_REPORT);
                UpdateDatabaseHelper.setSequenceNumTrigger(DispatchCollectionTable.TABLE_DISPATCH_REPORT, primaryDatabase);

            case VERSION_FORTY_FIVE:
                updateDatabaseHelper.alterRateChartNameTableWithShift();
            case VERSION_FORTY_SIX:
                //Table missing issue noticed in crashlytics,
                //Tyring to create the table if not exist
                primaryDatabase.execSQL(getCreateQueryforDeviceInfo());
                updateDatabaseHelper.updateTo43(oldVersion);
                updateDatabaseHelper.updateRateTables();
                updateDatabaseHelper.dropTables();
                UpdateDatabaseHelper.addCollectionTimeIndexOnReportsTable(primaryDatabase);
                UpdateDatabaseHelper.addCollectionDateIndexOnReportsTable(primaryDatabase);

            default:
                // updateConfigurationTableWithMissingItems(primaryDatabase);
                //handle app launch on upgrade.
                Log.i(TAG, "upgrade is happening...");
                handleAppUpgrade(primaryDatabase, oldVersion);
                break;
        }
    }

    private void addDefaultFarmer(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(KEY_FARMER_ID, DEFAULT_FARMER_ID);
        values.put(KEY_FARMER_NAME, DEFAULT_FARMER_NAME);
        values.put(KEY_FARMER_CATTLE, "BOTH");
        values.put(KEY_FARMER_CANS, "1");
        values.put(KEY_FARMER_TYPE, "FARMER");
        values.put(KEY_ENABLE_MULTIPLECANS, "true");
        long res = db.insert(TABLE_FARMER, null, values);
        Log.v(ESSAE, "Inserting default farmer : " + res);
    }

    public void insertCleaningDataIntoEssaeTable(SQLiteDatabase db, long queriedTime, String date, String time,
                                                 String type, String value, int status) {
        db.beginTransaction();
        String newDate = Util.convertDate(date);
        ContentValues values = new ContentValues();
        values.put(DatabaseEntity.ESSAE_QUERIED_TIME, queriedTime);
        values.put(DatabaseEntity.ESSAE_DATE, newDate);
        values.put(DatabaseEntity.ESSAE_TIME, time);
        values.put(DatabaseEntity.ESSAE_TYPE, type);
        values.put(DatabaseEntity.ESSAE_VALUE, value);
        values.put(DatabaseEntity.ESSAE_STATUS, status);
        long res = db.insertWithOnConflict(DatabaseEntity.TABLE_ESSAE_DATA, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        Log.v(ESSAE, "DB insert result for " + type + " : " + res);
        db.setTransactionSuccessful();
        db.endTransaction();
    }

    public int updateCleaningDataIntoEssaeTable() {
        SQLiteDatabase db = sqliteDatabase;
        db.beginTransaction();
        ContentValues values = new ContentValues();
        values.put(DatabaseEntity.ESSAE_STATUS, 0);
        int res = db.update(DatabaseEntity.TABLE_ESSAE_DATA, values, DatabaseEntity.ESSAE_STATUS + "=" + 1, null);
        Log.v(ESSAE, "DB no of rows updated: " + res);
        db.setTransactionSuccessful();
        db.endTransaction();
        return res;
    }


    private void altercolStatusTable(SQLiteDatabase primaryDatabase) {
        createTempCollRecStatusTable();
        primaryDatabase.execSQL(CREATE_TEMP_COL_RECORD_STATUS);
        String insertInto = "INSERT INTO " + TABLE_TEMP_COL_REC_STATUS + " SELECT * FROM " + TABLE_COL_REC_STATUS;
        primaryDatabase.execSQL(insertInto);
        String sDrop = "DROP TABLE " + TABLE_COL_REC_STATUS;
        primaryDatabase.execSQL(sDrop);
        primaryDatabase.execSQL(CREATE_COL_RECORD_STATUS);
        String insertIntoF = "INSERT INTO " + TABLE_COL_REC_STATUS + " SELECT * FROM " + TABLE_TEMP_COL_REC_STATUS;
        primaryDatabase.execSQL(insertIntoF);
        String sDropF = "DROP TABLE " + TABLE_TEMP_COL_REC_STATUS;
        primaryDatabase.execSQL(sDropF);
    }

   /* private void updateConfigurationTableWithMissingItems(SQLiteDatabase primaryDatabase) {

        ArrayList<ConfigurationElement> allConfigPushList = OnStartTask
                .getAllTheMissingConfigItems(mContext);
        insertConfigurationEntity(allConfigPushList, primaryDatabase);
    }
*/

    private void alterFarmerTableConstraints(SQLiteDatabase primaryDatabase) {


        primaryDatabase.execSQL(CREATE_FARMER_TABLE_UNIQUE);
        String insertInto = "INSERT INTO " + TABLE_FARMER_TEMP + " SELECT * FROM " + TABLE_FARMER;
        primaryDatabase.execSQL(insertInto);

        String blank = "";
        String query = "Update " + TABLE_FARMER_TEMP + " set "
                + KEY_FARMER_BARCODE + " =" + null + " where " + KEY_FARMER_BARCODE + " ='" + blank + "'";
        primaryDatabase.execSQL(query);


        String sDrop = "DROP TABLE " + TABLE_FARMER;
        primaryDatabase.execSQL(sDrop);

        primaryDatabase.execSQL(FarmerTable.CREATE_QUERY);

        String insertIntoF = "INSERT INTO " + TABLE_FARMER + " SELECT * FROM " + TABLE_FARMER_TEMP;
        primaryDatabase.execSQL(insertIntoF);

        String sDropF = "DROP TABLE " + TABLE_FARMER_TEMP;
        primaryDatabase.execSQL(sDropF);
    }

    private void alterReportTableWithRemarksConstraints(SQLiteDatabase primaryDatabase) {
        String query = "ALTER TABLE " + TABLE_REPORTS + " ADD COLUMN " + KEY_MILKQUALITY + " TEXT";
        primaryDatabase.execSQL(query);
    }

    private void alterReportTableWithTippingStartTime(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER TABLE " + TABLE_REPORTS
                + " ADD COLUMN " + KEY_TIPPING_START_TIME + " INTEGER " + " DEFAULT " + 0);
    }

    private void alterReportTableWithTippingEndTime(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER TABLE " + TABLE_REPORTS
                + " ADD COLUMN " + KEY_TIPPING_END_TIME + " INTEGER " + " DEFAULT " + 0);
    }

    private void alterReportTableWithAgentId(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER TABLE " + TABLE_REPORTS + " ADD COLUMN "
                + FarmerCollectionTable.KEY_REPORT_AGENT_ID + " VARCHAR(100) " + " DEFAULT " + SmartCCConstants.DEFAULT_AGENT_ID);
    }

    private void alterReportTableWithMilkStatusCode(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_MILKSTATUS_CODE + " INTEGER " + " DEFAULT " + SmartCCConstants.DEFAULT_MILK_STATUS);
    }

    private void alterReportTableWithRateCalculation(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_RATE_CALC_DEVICE + " INTEGER " + " DEFAULT " + SmartCCConstants.DEFAULT_RATE_CALC_DEVICE);
    }

    //These changes After Android DB30 and version 11.4.5G159

    private void alterReportTableWithMASerialNum(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_MA_SERIAL + " INTEGER " + " DEFAULT " +
                1);
    }

    private void alterReportTableWithMAName(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_MA_NAME + " VARCHAR(100) " + " DEFAULT " +
                SmartCCConstants.DEFAULT_MA_NAME);
    }

    private void alterEXTReportTableWithTippingStartTime(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER TABLE " + TABLE_EXTENDED_REPORT
                + " ADD COLUMN " + KEY_TIPPING_START_TIME + " INTEGER " + " DEFAULT " + 0);
    }

    private void alterEXTReportTableWithTippingEndTime(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER TABLE " + TABLE_EXTENDED_REPORT
                + " ADD COLUMN " + KEY_TIPPING_END_TIME + " INTEGER " + " DEFAULT " + 0);
    }

    private void alterEXTReportTableWithAgentId(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER TABLE " + TABLE_EXTENDED_REPORT + " ADD COLUMN "
                + FarmerCollectionTable.KEY_REPORT_AGENT_ID + " VARCHAR(100) " + " DEFAULT " + SmartCCConstants.DEFAULT_AGENT_ID);
    }

    private void alterEXTReportTableWithMilkStatusCode(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_MILKSTATUS_CODE + " INTEGER " + " DEFAULT " + SmartCCConstants.DEFAULT_MILK_STATUS);
    }

    private void alterEXTReportTableWithRateCalculation(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_RATE_CALC_DEVICE + " INTEGER " + " DEFAULT " + SmartCCConstants.DEFAULT_RATE_CALC_DEVICE);
    }
    // Insert ratechart name and validity;

    private void alterEXTReportTableWithMASerialNum(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_MA_SERIAL + " INTEGER " + " DEFAULT " +
                1);
    }

    private void alterEXTReportTableWithMAName(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_MA_NAME + " VARCHAR(100) " + " DEFAULT " +
                SmartCCConstants.DEFAULT_MA_NAME);
    }


    //alter report table added number of cans in reportTable

    private void alterReportForRate(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_REPORTS + " ADD "
                + FarmerCollectionTable.KEY_REPORT_RATEMODE + " VARCHAR(100)" + " DEFAULT " + Util.RATE_MODE_AUTO;
        primaryDatabase.execSQL(sqlQuery);
    }


    //Insert farmer list from excel

    private String alterSalesTable() {
        return "ALTER " + "TABLE " + TABLE_SALES_REPORT + " ADD "
                + SalesCollectionTable.KEY_SALES_TXN_TYPE + " VARCHAR(100)" + " DEFAULT " + Util.SALES_TXN_TYPE_SALES;
    }

    private String alterSalesTableColumn2() {
        return "ALTER " + "TABLE " + TABLE_SALES_REPORT + " ADD "
                + SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE + " VARCHAR(100)" + " DEFAULT " + Util.SALES_TXN_SUBTYPE_NORMAL;
    }

    private String alterRateChartType() {
        return "ALTER " + "TABLE " + TABLE_RATECHART_NAME + " ADD " + "COLUMN "
                + RateChartNameTable.TYPE + " TEXT" + " DEFAULT " + Util.RATECHART_TYPE_COLLECTION;
    }

    private String alterReportTable() {
        //Added default value to get the old table records
        return "ALTER " + "TABLE " + TABLE_REPORTS + " ADD " + "COLUMN "
                + FarmerCollectionTable.KEY_REPORT_COMMITED + " INTEGER" + " DEFAULT " + "1";
    }

    private String alterReportTableWithType() {
        //Added default value to get the old table records
        return "ALTER " + "TABLE " + TABLE_REPORTS + " ADD " + "COLUMN "
                + FarmerCollectionTable.KEY_REPORT_TYPE + " TEXT" + " DEFAULT " + Util.REPORT_TYPE_FARMER;
    }

    private String alterFarmerTable() {
        //Added multiple cans default it will be false
        return "ALTER " + "TABLE " + TABLE_FARMER + " ADD " + "COLUMN "
                + KEY_ENABLE_MULTIPLECANS + " TEXT" + " DEFAULT " + "false";
    }

    private String alterFarmerTableAgentId() {

        return "ALTER " + "TABLE " + TABLE_FARMER + " ADD " + "COLUMN "
                + KEY_AGENT_ID + " TEXT";
    }


    //Insert center record

    private String alterFarmerTableFarmerType() {

        return "ALTER " + "TABLE " + TABLE_FARMER + " ADD " + "COLUMN "
                + KEY_FARMER_TYPE + " TEXT" + " DEFAULT " + AppConstants.FARMER_TYPE_FARMER;
    }

    private String updateSMSStatusInCollection() {
        //Added default value to get the old table records
        return "UPDATE " + TABLE_COL_REC_STATUS + " SET " + KEY_COL_REC_FARMER_SMS_STATUS +
                " = " + COL_REC_SMS_NOT_ENABLE;
    }


    //sales

    private void alterReportForNumberOfCans(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_REPORTS + " ADD "
                + FarmerCollectionTable.KEY_REPORT_NUMBER_OF_CANS + " VARCHAR(100)" + " DEFAULT " + "1";
        primaryDatabase.execSQL(sqlQuery);
    }

    //Insert center record status infor

    private void alterChillingCenterForRoute(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_CHILLING_CENTER + " ADD "
                + KEY_CHILLING_ROUTE + " VARCHAR(100)" + " DEFAULT " + "NA";
        primaryDatabase.execSQL(sqlQuery);
    }

    private void alterReportForSequenceNumber(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_REPORTS + " ADD "
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " INTEGER" + " DEFAULT " + 0;
        primaryDatabase.execSQL(sqlQuery);
    }

    private void alterReportForRoute(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_REPORTS + " ADD "
                + FarmerCollectionTable.KEY_REPORT_ROUTE + " VARCHAR(100)" + " DEFAULT " + "'NA'";
        primaryDatabase.execSQL(sqlQuery);
    }

    private void alterReportForRecordStatus(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_REPORTS + " ADD "
                + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + " VARCHAR(100)" + " DEFAULT " + Util.RECORD_STATUS_COMPLETE;
        primaryDatabase.execSQL(sqlQuery);
    }

    private void alterReportForKgWeight(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_REPORTS + " ADD "
                + FarmerCollectionTable.KEY_REPORT_KG_QTY + " TEXT" + " DEFAULT " + "'" + defaultWeight + "'";
        primaryDatabase.execSQL(sqlQuery);
    }


    //Insert sales shift details

    private void alterReportForLtrsWeight(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_REPORTS + " ADD "
                + FarmerCollectionTable.KEY_REPORT_LTR_QTY + " TEXT" + " DEFAULT " + "'" + defaultWeight + "'";
        primaryDatabase.execSQL(sqlQuery);
    }

    private void alterReportTableForRateChartName(SQLiteDatabase primaryDatabase) {

        String sqlQery = "ALTER " + "TABLE " + TABLE_REPORTS + " ADD " + "COLUMN "
                + FarmerCollectionTable.KEY_REPORT_RATECHART_NAME + " TEXT" + " DEFAULT " + "NA";
        primaryDatabase.execSQL(sqlQery);
    }


    //Insert chill record

    private void alterChillingCenterTableForSingleOrMultiple(SQLiteDatabase primaryDatabase) {
        String query = "ALTER " + " TABLE " + TABLE_CHILLING_CENTER + " ADD "
                + KEY_CHILLING_SINGLE_MULTIPLE + " TEXT " + "DEFAULT " + "'" + Util.MULTIPLE + "'";

        primaryDatabase.execSQL(query);

    }


    //Update sales shift details

    private void alterChillingCenterTableForMilkType(SQLiteDatabase primaryDatabase) {
        String query = "ALTER " + " TABLE " + TABLE_CHILLING_CENTER + " ADD "
                + KEY_CHILLING_MILK_TYPE + " TEXT " + "DEFAULT " + "'COW'";
        primaryDatabase.execSQL(query);
    }

    private void alterExtendedReportForKgWeight(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_EXTENDED_REPORT + " ADD " + "COLUMN "
                + FarmerCollectionTable.KEY_REPORT_KG_QTY + " TEXT" + " DEFAULT " + "'" + defaultWeight + "'";
        primaryDatabase.execSQL(sqlQuery);
    }

    private void alterExtendedReportForLtrsWeight(SQLiteDatabase primaryDatabase) {
        String sqlQuery = "ALTER " + "TABLE " + TABLE_EXTENDED_REPORT + " ADD " + "COLUMN "
                + FarmerCollectionTable.KEY_REPORT_LTR_QTY + " TEXT" + " DEFAULT " + "'" + defaultWeight + "'";
        primaryDatabase.execSQL(sqlQuery);
    }

    private void alterFarmerWithSentStatus(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL(
                "ALTER " + "TABLE " + TABLE_FARMER + " ADD " + "COLUMN "
                        + KEY_FARMER_SENT + " INTEGER" + " DEFAULT " + DatabaseEntity.FARMER_UNSENT_CODE);
    }


    //Update chillng center record to sent

    private void alterTankerTable(SQLiteDatabase primaryDatabase, String column) {
        if (!toCheckTheColumnInTable(primaryDatabase, TankerDatabase.TABLE_TANKER,
                column)) {
            primaryDatabase.execSQL(
                    TankerDatabase.getAlterQueryForConfigurationTable(column, 0));
        }

    }


    private void alterChillingWithSentStatus(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER " + "TABLE " + TABLE_CHILLING_CENTER
                + " ADD " + "COLUMN "
                + KEY_CHILLING_CENTER_SENT + " INTEGER" + " DEFAULT "
                + DatabaseEntity.FARMER_UNSENT_CODE);
    }

    private void alterUserSentStatus(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER " + "TABLE " + TABLE_USER
                + " ADD " + "COLUMN "
                + KEY_USER_SENT + " INTEGER" + " DEFAULT "
                + DatabaseEntity.FARMER_UNSENT_CODE);
    }

    private void alterSampleSentStatus(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("ALTER " + "TABLE " + TABLE_SAMPLE
                + " ADD " + "COLUMN "
                + KEY_SAMPLE_SENT + " INTEGER" + " DEFAULT "
                + DatabaseEntity.FARMER_UNSENT_CODE);


    }

    private void alterReportTableWithMAParams(SQLiteDatabase primaryDatabase)

    {
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_LACTOSE + " DOUBLE ");
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_PROTEIN + " DOUBLE ");
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_PH + " DOUBLE ");
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_SALT + " DOUBLE ");
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_FREEZING_POINT + " DOUBLE ");
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_CONDUTIVITY + " DOUBLE ");

        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_CALIBRATION + " TEXT ");
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_DENSITY + " DOUBLE ");


    }


    private void alterReportTableWithTOTalBonus(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_INCENTIVE_RATE + " DOUBLE " + " DEFAULT " + 0.0);
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_INCENTIVE_AMOUNT + " DOUBLE " + " DEFAULT " + 0.0);
        //   primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
        //         FarmerCollectionTable.KEY_REPORT_PROTEIN + " DOUBLE "+ " DEFAULT " + 0.0);
        primaryDatabase.execSQL("UPDATE " + TABLE_REPORTS + " SET " + FarmerCollectionTable.KEY_REPORT_PROTEIN + " = " + 0.0);

        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_KG_FAT + " DOUBLE " + " DEFAULT " + 0.0);
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_KG_SNF + " DOUBLE " + " DEFAULT " + 0.0);
        primaryDatabase.execSQL("Alter table " + TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_KG_CLR + " DOUBLE " + " DEFAULT " + 0.0);

    }


    private void alterReportTableWithIsActive(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("Alter table " + TABLE_CHILLING_CENTER + " ADD COLUMN " +
                KEY_CHILLING_IS_ACTIVE + " TEXT " + " DEFAULT " + 1);


    }

    public void insertSampleData
            (SampleDataEntity sampleEnt) {

        String barcode = null;
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String selectQuery = "SELECT  * FROM " + TABLE_SAMPLE + " WHERE "
                + KEY_SAMPLE_ID + "='" + sampleEnt.sampleId + "'";

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                barcode = cursor.getString(14);
            }


            ContentValues values = new ContentValues();

            values.put(KEY_SAMPLE_FAT, sampleEnt.sampleFat);
            values.put(KEY_SAMPLE_IS_FAT, sampleEnt.sampleIsFat);
            values.put(KEY_SAMPLE_IS_SNF, sampleEnt.sampleIsSnf);
            values.put(KEY_SAMPLE_IS_WEIGH, sampleEnt.sampleIsWeigh);
            values.put(KEY_SAMPLE_MODE, sampleEnt.sampleMode);
            values.put(KEY_SAMPLE_SOC_ID, String.valueOf(session.getSocietyColumnId()));
            values.put(KEY_SAMPLE_OTHER2, sampleEnt.sampleOther2);
            values.put(KEY_SAMPLE_OTHER3, sampleEnt.sampleOther3);

            values.put(KEY_SAMPLE_WEIGH, sampleEnt.sampleWeigh);

            values.put(KEY_SAMPLE_RATE, sampleEnt.sampleRate);
            values.put(KEY_SAMPLE_AMOUNT, sampleEnt.sampleAmount);
            values.put(KEY_SAMPLE_BARCODE, sampleEnt.sampleBarcode.toUpperCase(Locale.ENGLISH));

            // Inserting Row

            if (barcode != null && barcode.equalsIgnoreCase(sampleEnt.sampleId)) {
                // values.put(KEY_ID, bmcEntity.bmc_Id);

                String strFilter = KEY_SAMPLE_ID + " = ?";

                dbSync.syncUpdate(primaryDatabase, TABLE_SAMPLE, values, strFilter,
                        new String[]{sampleEnt.sampleId});
                Log.v("TABLE_SAMPLE", "Table sample updated successfully");

            } else {

                values.put(KEY_SAMPLE_ID, sampleEnt.sampleId);
                dbSync.syncInsert(primaryDatabase, TABLE_SAMPLE, values);
                Log.v("TABLE_SAMPLE", "Table sample created successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

        // Closing database connection
    }


    public void insertValidationTableR
            (ArrayList<RateChartEntity> allRateChartEnt, String primaryOrsecondary) {

        int rateId = 0;
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        if (primaryOrsecondary.equalsIgnoreCase(isPrimary)) {
            primaryDatabase = sqliteDatabase;
        } else {
            primaryDatabase = dbSync.getSqliteDatabase();
        }
        primaryDatabase.beginTransaction();
        ContentValues values = new ContentValues();
        try {
            for (rateId = 0; rateId < allRateChartEnt.size(); rateId++) {
                values.put(KEY_VALIDATION_DUMMY, "Dummy");
                values.put(KEY_VALIDATION_SNF, allRateChartEnt.get(rateId).snf);
                values.put(KEY_VALIDATION_FAT, allRateChartEnt.get(rateId).fat);
                dbSync.syncInsert(primaryDatabase, TABLE_VALIDATION, values);
            }
            primaryDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            // handle your sqlite errors
        } finally {
            primaryDatabase.endTransaction();
            SmartCCUtil.todoDbFailure(mContext, primaryOrsecondary);
        }
        // Closing database connection
    }

    public void insertRateChartFromExcel(ArrayList<RateChartEntity> allRateChartEnt, String primaryOrsecondary) {
        int rateId = 0;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        if (primaryOrsecondary.equalsIgnoreCase(isPrimary)) {
            primaryDatabase = sqliteDatabase;
        } else {
            primaryDatabase = dbSync.getSqliteDatabase();
        }


        try {
            primaryDatabase.beginTransaction();

            RatechartDetailsEnt rde = SmartCCUtil.getRateChartEntity(allRateChartEnt.get(0));
            ContentValues valuesName = new ContentValues();
            valuesName.put(RateChartNameTable.MILKTYPE, rde.rateMilkType);
            valuesName.put(RateChartNameTable.OTHER1, rde.rateOther1);
            valuesName.put(RateChartNameTable.OTHER2, rde.rateOther2);
            valuesName.put(RateChartNameTable.VALIDFROM, rde.rateValidityFrom);
            valuesName.put(RateChartNameTable.VALIDTO, rde.rateValidityTo);
            valuesName.put(RateChartNameTable.SOCID, rde.rateSocId);
            valuesName.put(RateChartNameTable.LVALIDITYFROM, rde.rateLvalidityFrom);
            valuesName.put(RateChartNameTable.LVALIDITYTO, rde.rateLvalidityTo);
            valuesName.put(RateChartNameTable.ISACTIVE, rde.isActive);
            valuesName.put(RateChartNameTable.TYPE, rde.ratechartType);
            valuesName.put(RateChartNameTable.NAME, rde.rateChartName);

            long id = dbSync.syncInsert(primaryDatabase, RateChartNameTable.TABLE_NAME, valuesName);


            for (rateId = 0; rateId < allRateChartEnt.size(); rateId++) {
                ContentValues values = new ContentValues();
                values.put(RateTable.FARMER,
                        allRateChartEnt.get(rateId).farmerId);
                values.put(RateTable.FAT, allRateChartEnt.get(rateId).fat);
                values.put(RateTable.SNF, allRateChartEnt.get(rateId).snf);
                values.put(RateTable.USER, allRateChartEnt.get(rateId).managerID);
                values.put(RateTable.MILK_TYPE, allRateChartEnt.get(rateId).milkType
                        .toUpperCase(Locale.ENGLISH));
                values.put(RateTable.END_DATE, allRateChartEnt.get(rateId).endDate);
                values.put(RateTable.START_DATE,
                        allRateChartEnt.get(rateId).startDate);
                values.put(RateTable.SOCID,
                        allRateChartEnt.get(rateId).societyId);
                values.put(RateTable.RATE, allRateChartEnt.get(rateId).rate);
                values.put(RateTable.CLR, allRateChartEnt.get(rateId).clr);

                values.put(RateTable.RATE_REF_ID, id);

                dbSync.syncInsert(primaryDatabase, RateTable.TABLE_NAME, values);
            }
            primaryDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            // handle your sqlite errors
        } finally {
            primaryDatabase.endTransaction();
            SmartCCUtil.todoDbFailure(mContext, primaryOrsecondary);
        }
        // Closing database connection
    }

    //Insert sample data from list
    public void insertSampleFromExcel(ArrayList<SampleDataEntity> allSampleEnt) {

        int id = 0;
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        primaryDatabase.beginTransaction();
        ContentValues values = new ContentValues();

        try {
            for (id = 0; id < allSampleEnt.size(); id++) {

                SampleDataEntity sampleEnt = allSampleEnt.get(id);
                values.put(KEY_SAMPLE_FAT, sampleEnt.sampleFat);
                values.put(KEY_SAMPLE_IS_FAT, sampleEnt.sampleIsFat);
                values.put(KEY_SAMPLE_IS_SNF, sampleEnt.sampleIsSnf);
                values.put(KEY_SAMPLE_IS_WEIGH, sampleEnt.sampleIsWeigh);
                values.put(KEY_SAMPLE_MODE, sampleEnt.sampleMode);
                values.put(KEY_SAMPLE_SOC_ID, String.valueOf(session.getSocietyColumnId()));
                values.put(KEY_SAMPLE_OTHER2, sampleEnt.sampleOther2);
                values.put(KEY_SAMPLE_OTHER3, sampleEnt.sampleOther3);
                values.put(KEY_SAMPLE_WEIGH, sampleEnt.sampleWeigh);
                values.put(KEY_SAMPLE_RATE, sampleEnt.sampleRate);
                values.put(KEY_SAMPLE_AMOUNT, sampleEnt.sampleAmount);
                values.put(KEY_SAMPLE_BARCODE, sampleEnt.sampleBarcode.toUpperCase(Locale.ENGLISH));
                values.put(KEY_SAMPLE_ID, sampleEnt.sampleId);

                dbSync.syncInsert(primaryDatabase, TABLE_SAMPLE, values);
            }
            primaryDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            // handle your sqlite errors
        } finally {
            primaryDatabase.endTransaction();
        }
        // Closing database connection
    }


    /**
     * to get the unsent collection records
     * getNWUnsentRecordsCount
     *
     * @return number of unsent count
     */
    public int getAllNWUnsentRecordsCount() {

        int count = 0;
        // Retrieve number of records which have not been sent to network yet
        String selectQuery = "SELECT  COUNT(*) FROM " + TABLE_REPORTS + " WHERE " +
                FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " +
                CollectionConstants.UNSENT;

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            //Log.d(TAG, "Getting Count from Unsent Records ---------- : " + cursor.getInt(0));
            count = cursor.getInt(0);
        }
        // return contact list
        if (cursor != null)
            cursor.close();
        return count;
    }


    //consolidation API Changes

    /**
     * To get the unsent sales records
     *
     * @return
     */

    public int getAllNWUnsentSalesCount() {

        int count = 0;
        // Retrieve number of records which have not been sent to network yet
        String selectQuery = "SELECT  COUNT(*) FROM " + TABLE_SALES_REPORT + " WHERE " +
                SalesCollectionTable.KEY_SALES_SEND_STATUS + " = " +
                CollectionConstants.UNSENT;

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            //Log.d(TAG, "Getting Count from Unsent Records ---------- : " + cursor.getInt(0));
            count = cursor.getInt(0);
        }
        // return contact list
        if (cursor != null)
            cursor.close();


        return count;
    }

    public void insertUser(UserEntity userEntity) {

        String userId = null;
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_USER_USERID + "='" + userEntity.userId + "'";

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                userId = cursor.getString(7);
            }

            ContentValues values = new ContentValues();

            values.put(KEY_USER_EMAIL, userEntity.emailId);
            values.put(KEY_USER_MOBILE, userEntity.mobile);
            values.put(KEY_USER_NAME, userEntity.name);
            values.put(KEY_USER_PASSWORD, userEntity.password);
            values.put(KEY_USER_SOCIETYID, userEntity.centerId);
            values.put(KEY_USER_ROLE, userEntity.role);
            values.put(KEY_USER_REGDATE, userEntity.regDate);
            values.put(KEY_USER_WEEKDATE, userEntity.weekDate);
            values.put(KEY_USER_MONTHDATE, userEntity.monthDate);

            if (userId != null && (userId.equalsIgnoreCase(userEntity.userId))) {
                // values.put(KEY_ID, bmcEntity.bmc_Id);

                String strFilter = KEY_USER_USERID + " = ?";

                dbSync.syncUpdate(primaryDatabase, TABLE_USER, values, strFilter,
                        new String[]{userEntity.userId});

                Log.v("TABLE_USER", "Table user updated successfully");

            } else {
                values.put(KEY_USER_USERID, userEntity.userId);
                dbSync.syncInsert(primaryDatabase, TABLE_USER, values);
                Log.v("TABLE_USER", "Table user created successfully");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public long insertSociety(SocietyEntity socEntity) {

        long colID = -1;
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String selectQuery = "SELECT  * FROM " + TABLE_SOCIETY + " WHERE "
                + KEY_SOCIETY_COLUMNID + "=" + socEntity.colId;

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                colID = cursor.getInt(0);
            }

            ContentValues values = new ContentValues();
            values.put(KEY_SOCIETY_ADDRESS, socEntity.address);
            values.put(KEY_SOCIETY_BMCID, socEntity.bmcId);
            values.put(KEY_SOCIETY_CONPERSON1, socEntity.conPerson1);
            values.put(KEY_SOCIETY_CONTACT1, socEntity.contactNum1);
            values.put(KEY_SOCIETY_LOCATION, socEntity.location);
            values.put(KEY_SOCIETY_ROUTE, socEntity.route);
            values.put(KEY_SOCIETY_EMAIL1, socEntity.socEmail1);
            values.put(KEY_SOCIETY_NAME, socEntity.name);

            values.put(KEY_SOCIETY_CONPERSON2, socEntity.conPerson2);
            values.put(KEY_SOCIETY_CONTACT2, socEntity.contactNum2);
            values.put(KEY_SOCIETY_EMAIL2, socEntity.socEmail2);
            values.put(KEY_SOCIETY_NUMFARMER, socEntity.numOfFarmer);

            values.put(KEY_SOCIETY_REGISTRATION_DATE, socEntity.society_regDate);
            values.put(KEY_SOCIETY_WEEKLY, socEntity.society_weekly);
            values.put(KEY_SOCIETY_MONTHLY, socEntity.society_monthly);
            values.put(KEY_SOCIETY_CODE, socEntity.socCode.toUpperCase(Locale.ENGLISH));
            if (colID != -1 && colID == socEntity.colId) {
                String strFilter = KEY_SOCIETY_COLUMNID + " = ?";
                dbSync.syncUpdate(primaryDatabase, TABLE_SOCIETY, values, strFilter,
                        new String[]{String.valueOf(socEntity.colId)});
                Log.v("TABLE_SOCIETY", "Table society updated successfully");

            } else {
                values.put(KEY_SOCIETY_COLUMNID, socEntity.colId);
                colID = dbSync.syncInsert(primaryDatabase, TABLE_SOCIETY, values);
                Log.v("TABLE_SOCIETY", "Table society created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return colID;
    }

    public UserEntity getUserByUserId(String userId) {
        UserEntity userEntity = new UserEntity();

        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_USER_USERID + " = '" + userId + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        DatabaseEntity dbe = new DatabaseEntity(mContext);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                userEntity = dbe.getUserEntity(cursor);

            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return userEntity;
    }


    //Get center shift report

    public UserEntity getManagerDetails(String role, String socId) {

        UserEntity userEntity = new UserEntity();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_USER_ROLE + "='" + UserRole.MANAGER + "'" + " AND "
                + KEY_USER_SOCIETYID + "='" + socId + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        DatabaseEntity dbe = new DatabaseEntity(mContext);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                userEntity = dbe.getUserEntity(cursor);

            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return userEntity;
    }

    public ArrayList<SocietyEntity> getSocietyEntity(int dateChk) {
        ArrayList<SocietyEntity> allSocieties = new ArrayList<SocietyEntity>();
        // Select All Query

        String selectQuery = null;

        if (dateChk == 1) {
            selectQuery = "SELECT  * FROM " + TABLE_SOCIETY + " WHERE "
                    + KEY_SOCIETY_WEEKLY + "=" + Util.getOperationDate(0, 1)
                    + " OR " + KEY_SOCIETY_MONTHLY + "="
                    + Util.getOperationDate(0, 1);
        } else {
            selectQuery = "SELECT  * FROM " + TABLE_SOCIETY;
        }

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                SocietyEntity societyEntity = new SocietyEntity();
                societyEntity.colId = cursor.getInt(0);
                societyEntity.address = cursor.getString(1);
                societyEntity.bmcId = cursor.getString(2);
                societyEntity.socCode = cursor.getString(3);
                societyEntity.contactNum1 = cursor.getString(4);
                societyEntity.location = cursor.getString(5);
                societyEntity.route = cursor.getString(6);
                societyEntity.socEmail1 = cursor.getString(7);
                societyEntity.conPerson1 = cursor.getString(8);
                societyEntity.name = cursor.getString(9);
                societyEntity.socEmail2 = cursor.getString(10);
                societyEntity.conPerson2 = cursor.getString(11);
                societyEntity.contactNum2 = cursor.getString(12);
                societyEntity.numOfFarmer = cursor.getString(13);
                societyEntity.society_regDate = cursor.getInt(14);
                societyEntity.society_weekly = cursor.getInt(15);
                societyEntity.society_monthly = cursor.getInt(16);

                allSocieties.add(societyEntity);
            } while (cursor.moveToNext());
        }
        // return contact list

        if (cursor != null)
            cursor.close();

        return allSocieties;
    }


    // Get all reports per milk type

    // Getting All RateChart
    public boolean checkVacantPosition(String snf, String fat) {

        boolean validation = false;

        String selectQuery = "SELECT  * FROM " + TABLE_VALIDATION + " WHERE "
                + KEY_VALIDATION_SNF + "='" + snf + "'" + " AND "
                + KEY_VALIDATION_FAT + "='" + fat + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            validation = true;

        } else {
            validation = false;
        }
        if (cursor != null)
            cursor.close();

        return validation;
    }

    // Getting All RateChart
    public ArrayList<RateChartEntity> getAllProteinRateData(String socId, String name,
                                                            int SocCheck) {
        ArrayList<RateChartEntity> allRateChart = new ArrayList<RateChartEntity>();
        // Select All Query
        String selectQuery = null;

        selectQuery = "SELECT  * FROM " + TABLE_INCENTIVE_RATECHART + " WHERE "
                + DatabaseEntity.InCentive.RATE_CHART_NAME + "='" + name + "'" + " ORDER BY "
                + DatabaseEntity.InCentive.VALUE + " ASC ";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                BonusChartEntity bonusChartEntity = new BonusChartEntity();
                RateChartEntity rateEntity = new RateChartEntity();

                bonusChartEntity.bonusRate = Double.parseDouble(cursor.getString(2));
                bonusChartEntity.point = Double.parseDouble(cursor.getString(1));

                rateEntity.rate = bonusChartEntity.bonusRate;
                rateEntity.fat = bonusChartEntity.point;
                allRateChart.add(rateEntity);
            } while (cursor.moveToNext());
        }

        // return contact list
        if (cursor != null)
            cursor.close();

        return allRateChart;
    }


    public Cursor getUnsentEssaeRecords() {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String query = "SELECT * FROM " + TABLE_ESSAE_DATA + " WHERE "
                + DatabaseEntity.ESSAE_STATUS + "=" + 1;
        return primaryDatabase.rawQuery(query, null);

    }

    public Cursor getAllData(long refId) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String buildSQL = "SELECT * FROM " + RateTable.TABLE_NAME + " WHERE "
                + RateTable.RATE_REF_ID
                + "= " + refId + " ORDER BY " + RateTable.FAT + " ASC ";
        return primaryDatabase.rawQuery(buildSQL, null);
    }

    public Cursor getRateChartDetailsCursor() {

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String buildSQL = "SELECT * FROM " + RateChartNameTable.TABLE_NAME + " ORDER BY "
                + RateChartNameTable.NAME + " ASC ";
        return primaryDatabase.rawQuery(buildSQL, null);
    }

    //get the center shift report

    public String getSociety(String UserId) {
        String socId = "socId";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_USER_USERID + "='" + UserId + "'";

        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);

        if (cursor != null && cursor.moveToFirst()) {
            socId = cursor.getString(6);
        }
        if (cursor != null)
            cursor.close();

        return socId;

    }

    public SocietyEntity getSocietyDetails(long socID) {
        SocietyEntity socEnt = null;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT  * FROM " + TABLE_SOCIETY + " WHERE "
                + KEY_SOCIETY_COLUMNID + "=" + socID;

        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);

        if (cursor != null && cursor.moveToFirst()) {
            socEnt = new SocietyEntity();
            socEnt.colId = cursor.getInt(0);
            socEnt.address = cursor.getString(1);
            socEnt.bmcId = cursor.getString(2);
            socEnt.socCode = cursor.getString(3);
            socEnt.contactNum1 = cursor.getString(4);
            socEnt.location = cursor.getString(5);
            socEnt.route = cursor.getString(6);
            socEnt.socEmail1 = cursor.getString(7);
            socEnt.conPerson1 = cursor.getString(8);
            socEnt.name = cursor.getString(9);
            socEnt.socEmail2 = cursor.getString(10);
            socEnt.conPerson2 = cursor.getString(11);
            socEnt.contactNum2 = cursor.getString(12);
            socEnt.numOfFarmer = cursor.getString(13);

        }

        if (cursor != null)
            cursor.close();

        return socEnt;

    }

    public boolean isDuplicateUsers(String userId) {
        boolean isDuplicate = false;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_USER_USERID + "='" + userId + "'";
        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);

        if (cursor != null && cursor.moveToFirst()) {
            isDuplicate = true;
        }

        if (cursor != null)
            cursor.close();
        return isDuplicate;
    }

    public Cursor getUsers(String socId) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_USER_SOCIETYID + "='" + socId + "'" + " ORDER BY "
                + KEY_USER_NAME;
        //Log.d("SQL Curser", "getAllData SQL: " + buildSQL);
        return primaryDatabase.rawQuery(buildSQL, null);
    }

    public Cursor getSamples(String socId) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT  * FROM " + TABLE_SAMPLE
                + " ORDER BY "
                + KEY_SAMPLE_ID + " ASC ";
        //Log.d("SQL Curser", "getAllSampleData SQL: " + buildSQL);
        return primaryDatabase.rawQuery(buildSQL, null);
    }

    //Get obsolete user

    public Cursor getSocieties() {

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT  * FROM " + TABLE_SOCIETY;
        return primaryDatabase.rawQuery(buildSQL, null);
    }


    public void updateRateChart(RateChartEntity rcEntity) {

        int rateId = 0;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String selectQuery = "SELECT  * FROM " + TABLE_RATE + " WHERE "
                + RateTable.COLUMN_ID + "=" + rcEntity.columnId;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                rateId = cursor.getInt(0);
            }
            ContentValues values = new ContentValues();
            values.put(RateTable.FARMER, rcEntity.farmerId);
            values.put(RateTable.FAT, rcEntity.fat);
            values.put(RateTable.SNF, rcEntity.snf);
            values.put(RateTable.USER, rcEntity.managerID);
            values.put(RateTable.MILK_TYPE, rcEntity.milkType.toUpperCase(Locale.ENGLISH));
            values.put(RateTable.SOCID, rcEntity.societyId);
            values.put(RateTable.RATE, rcEntity.rate);
            values.put(RateTable.CLR, rcEntity.clr);
            if (rateId != 0 && (rateId == rcEntity.columnId)) {
                String strFilter = RateTable.COLUMN_ID + " = ?";
                dbSync.syncUpdate(primaryDatabase, TABLE_RATE, values, strFilter,
                        new String[]{String.valueOf(rcEntity.columnId)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();

        }

    }

    public UserEntity getPassword(String userId) {
        UserEntity userEntity = new UserEntity();
        String mobNumber = null;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_USER_USERID + "='" + userId + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        DatabaseEntity dbe = new DatabaseEntity(mContext);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                userEntity = dbe.getUserEntity(cursor);
            } while (cursor.moveToNext());
        }
        // return contact list
        if (cursor != null)
            cursor.close();

        return userEntity;

    }


    // Getting All Users
    public ArrayList<UserEntity> getAllUsers(String soccode, int dateCheck) {
        ArrayList<UserEntity> allUsers = new ArrayList<>();
        String selectQuery = null;

        if (dateCheck == 1) {

            selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                    + KEY_USER_SOCIETYID + "='" + soccode + "'"
                    + " ORDER BY "
                    + KEY_USER_NAME;
        } else {

            selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                    + KEY_USER_SOCIETYID + "='" + soccode + "'"
                    + " ORDER BY " + KEY_USER_NAME;
        }

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        DatabaseEntity dbe = new DatabaseEntity(mContext);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                UserEntity userEntity = dbe.getUserEntity(cursor);
                allUsers.add(userEntity);
            } while (cursor.moveToNext());
        }
        // return contact list
        if (cursor != null)
            cursor.close();

        return allUsers;
    }

    //This method removed in 14.1.2
    public Cursor getDailyShiftReport(long date, String shift, String farmer,
                                      ArrayList<SampleDataEntity> allSampleEnt) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = null;
        if (farmer.equalsIgnoreCase("All farmers")) {
            if (shift.equalsIgnoreCase("")) {
                buildSQL = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                        + FarmerCollectionTable.KEY_REPORT_LDATE + "=" + date + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0;
            } else {

                buildSQL = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                        + FarmerCollectionTable.KEY_REPORT_LDATE + "=" + date + " AND "
                        + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0;
            }
        } else {
            if (shift.equalsIgnoreCase("")) {
                buildSQL = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                        + FarmerCollectionTable.KEY_REPORT_LDATE + "=" + date + " AND "
                        + FarmerCollectionTable.KEY_REPORT_FARMERID + "='" + farmer + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0;
            } else {
                buildSQL = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                        + FarmerCollectionTable.POST_DATE + "=" + date + " AND "
                        + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND "
                        + FarmerCollectionTable.KEY_REPORT_FARMERID + "='" + farmer + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0;
            }

        }
        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);
        return primaryDatabase.rawQuery(buildSQL, null);
    }

    public ArrayList<String> getMinandMaxFat() {
        ArrayList<String> arrfat = new ArrayList<>();

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT DISTINCT " + RateTable.FAT + " FROM "
                + TABLE_RATE + " ORDER BY " + RateTable.FAT + " ASC ";

        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                arrfat.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return arrfat;
    }

    public ArrayList<String> getMinandMaxSNF() {
        ArrayList<String> arrsnf = new ArrayList<>();

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT DISTINCT " + RateTable.SNF + " FROM "
                + TABLE_RATE + " ORDER BY " + RateTable.SNF + " ASC ";
        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                arrsnf.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();


        return arrsnf;
    }


    //All centers and chilling Collection

    public ArrayList<ReportEntity> getDailyShiftReportEntity(long date,
                                                             String shift, String reportType) {

        ArrayList<ReportEntity> allReportEntity = new ArrayList<>();
        ReportEntity re = new ReportEntity();
        allReportEntity.add(re);

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = null;
        if (shift.equalsIgnoreCase("") || shift.equalsIgnoreCase("all")) {
            buildSQL = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_LDATE + "=" + date + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0
                    + " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + reportType + "'";
        } else if (reportType == null) {

            buildSQL = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_LDATE + "=" + date + " AND "
                    + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0;

        } else {
            buildSQL = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_LDATE + "=" + date + " AND "
                    + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0
                    + " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + reportType + "'";
        }

        buildSQL = buildSQL + SmartCCUtil.queryWithCompleteRecords();

        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);
                allReportEntity.add(reportEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return allReportEntity;
    }


    public ArrayList<String> getAllSampleIdorBarcodes(String socID,
                                                      int checkFarm) {
        ArrayList<String> allFarmerIdorBarcodes = new ArrayList<>();
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = null;

        buildSQL = "SELECT  * FROM " + TABLE_SAMPLE;
        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (checkFarm == 0) {
                    // To get all farmer ID's
                    allFarmerIdorBarcodes.add(cursor.getString(14));
                } else {
                    // To get all bar codes
                    allFarmerIdorBarcodes.add(cursor.getString(8));
                }

            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return allFarmerIdorBarcodes;
    }

    public void InsertAssociateData(AssocSocietyData assSocData) {

        String bmcId = null;
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String selectQuery = "SELECT  * FROM " + TABLE_ASSOCSOC + " WHERE "
                + KEY_ASSOCS_SOCID + "='" + assSocData.socId + "'";

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                bmcId = cursor.getString(1);
            }

            ContentValues values = new ContentValues();

            values.put(KEY_ASSOCS_SOCID, assSocData.socId);
            values.put(KEY_ASSOCS_ASSOCName, assSocData.assoSocName);

            if (bmcId != null && bmcId.equalsIgnoreCase(assSocData.assocId)) {
                // values.put(KEY_ID, bmcEntity.bmc_Id);

                String strFilter = KEY_ASSOCS_COLUMNID + " = ?";

                dbSync.syncUpdate(primaryDatabase, TABLE_ASSOCSOC, values, strFilter,
                        new String[]{assSocData.assocId});
                Log.v("TABLE_ASSOCSOC", "Table Associated updated successfully");
            } else {
                values.put(KEY_ASSOCS_ASSOCID, assSocData.assocId);
                dbSync.syncInsert(primaryDatabase, TABLE_ASSOCSOC, values);
                Log.v("TABLE_ASSOCSOC", "Table farmer created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }

    }


    public ArrayList<SampleDataEntity> getSampleData() {
        ArrayList<SampleDataEntity> allSampleData = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_SAMPLE + " ORDER BY "
                + KEY_SAMPLE_ID + " ASC ";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                SampleDataEntity sampleData = new SampleDataEntity();

                sampleData.sampleMode = cursor.getString(1);
                sampleData.sampleFat = cursor.getString(2);
                sampleData.sampleIsFat = cursor.getString(3);
                sampleData.sampleSnf = cursor.getString(4);
                sampleData.sampleIsSnf = cursor.getString(5);
                sampleData.sampleWeigh = cursor.getString(6);

                sampleData.sampleIsWeigh = cursor.getString(7);
                sampleData.sampleBarcode = cursor.getString(8);
                sampleData.sampleOther2 = cursor.getString(9);
                sampleData.sampleOther3 = cursor.getString(10);
                sampleData.sampleRate = cursor.getString(11);
                sampleData.sampleAmount = cursor.getString(12);
                sampleData.sampleSocId = cursor.getString(13);
                sampleData.sampleId = cursor.getString(14);

                allSampleData.add(sampleData);

            } while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();

        return allSampleData;
    }


    //get periodic report milk type

    public SampleDataEntity getSampleDataEntity(String id, int i) {
        SampleDataEntity sampleEnt = null;

        String selectQuery = null;

        if (i == 0) {
            selectQuery = "SELECT  * FROM " + TABLE_SAMPLE + " WHERE "
                    + " cast (" + KEY_SAMPLE_ID + " as INTEGER )" + "= " + Integer.valueOf(id);
        } else if (i == 1) {

            selectQuery = "SELECT  * FROM " + TABLE_SAMPLE + " WHERE "
                    + KEY_SAMPLE_BARCODE + "='" + id + "'";
        }

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {

            sampleEnt = new SampleDataEntity();
            sampleEnt.sampleMode = cursor.getString(1);
            sampleEnt.sampleFat = cursor.getString(2);
            sampleEnt.sampleIsFat = cursor.getString(3);
            sampleEnt.sampleSnf = cursor.getString(4);
            sampleEnt.sampleIsSnf = cursor.getString(5);
            sampleEnt.sampleIsWeigh = cursor.getString(6);

            sampleEnt.sampleIsWeigh = cursor.getString(7);
            sampleEnt.sampleBarcode = cursor.getString(8);
            sampleEnt.sampleOther2 = cursor.getString(9);
            sampleEnt.sampleOther3 = cursor.getString(10);
            sampleEnt.sampleRate = cursor.getString(11);
            sampleEnt.sampleAmount = cursor.getString(12);
            sampleEnt.sampleSocId = cursor.getString(13);
            sampleEnt.sampleId = cursor.getString(14);

        }
        if (cursor != null)
            cursor.close();

        return sampleEnt;
    }


    //get periodic total farmer and centerCollection Report

    public ArrayList<AssocSocietyData> getAssocData(String socId) {
        ArrayList<AssocSocietyData> allAssocData = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_ASSOCSOC + " WHERE "
                + KEY_ASSOCS_SOCID + "='" + socId + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                AssocSocietyData assosocData = new AssocSocietyData();

                assosocData.assocId = cursor.getString(1);
                assosocData.socId = cursor.getString(2);
                assosocData.assoSocName = cursor.getString(3);

                allAssocData.add(assosocData);

            } while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();

        return allAssocData;
    }


    public void insertMsgDetails(MessageEntity msgEntity) {

        String Date = null;
        String query = "SELECT  * FROM " + TABLE_MSGDETAILS + " WHERE "
                + KEY_MSG_SOCIETY + "='" + msgEntity.socId + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                Date = cursor.getString(2);

            }
            ContentValues values = new ContentValues();

            values.put(KEY_MSG_LIMIT, msgEntity.msgLimit);
            values.put(KEY_MSG_SENT, msgEntity.msgCount);
            values.put(KEY_MSG_SOCIETY, msgEntity.socId);

            if (Date != null && Date.equalsIgnoreCase(msgEntity.date)) {
                // values.put(KEY_ID, bmcEntity.bmc_Id);

                String strFilter = KEY_MSG_DATE + " = ?";

                dbSync.syncUpdate(primaryDatabase, TABLE_MSGDETAILS, values, strFilter,
                        new String[]{msgEntity.date});

                Log.v("TABLE_MSGDETAILS",
                        "Table message details updated successfully");

            } else {

                values.put(KEY_MSG_DATE, msgEntity.date);
                dbSync.syncInsert(primaryDatabase, TABLE_MSGDETAILS, values);
                Log.v("TABLE_MSGDETAILS",
                        "Table message details created successfully");
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null)
                cursor.close();
        }

    }

    public MessageEntity getAllMsgData(String socId) {
        MessageEntity msgEntity = new MessageEntity();
        String selectQuery = "SELECT  * FROM " + TABLE_MSGDETAILS + " WHERE "
                + KEY_MSG_SOCIETY + "='" + socId + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            msgEntity.date = cursor.getString(2);
            msgEntity.msgCount = cursor.getString(4);
            msgEntity.msgLimit = cursor.getString(3);
            msgEntity.socId = cursor.getString(1);
        }
        if (cursor != null)
            cursor.close();
        //Removed database close statement

        return msgEntity;

    }

    public void insertKGFACOTR(FactorEntity factEnt) {

        String Factor = null;
        String query = "SELECT  * FROM " + TABLE_KGFACTOR + " WHERE "
                + KEY_KG_SOCIETY + "='" + factEnt.socId + "'" + " AND "
                + KEY_KG_TYPE + "='" + factEnt.cattleType + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                Factor = cursor.getString(3);

            }
            ContentValues values = new ContentValues();

            values.put(KEY_KG_SOCIETY, factEnt.socId);
            values.put(KEY_KG_TYPE, factEnt.cattleType);

            if (Factor != null && Factor.equalsIgnoreCase(factEnt.kgFactor)) {
                // values.put(KEY_ID, bmcEntity.bmc_Id);
                String strFilter = KEY_KG_COLUMNID + " = ?";
                dbSync.syncUpdate(primaryDatabase, TABLE_KGFACTOR, values, strFilter,
                        new String[]{factEnt.kgFactor});
                Log.v("TABLE KGFACTOR",
                        "Table message details updated successfully");

            } else {
                values.put(KEY_KG_FACTOR, factEnt.kgFactor);
                dbSync.syncInsert(primaryDatabase, TABLE_KGFACTOR, values);
                Log.v("TABLE_KGFACTOR",
                        "Table message details created successfully");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();

        }

    }


    public String getKGfactor(String socId, String type) {
        String factor = null;

        String query = "SELECT  * FROM " + TABLE_KGFACTOR + " WHERE "
                + KEY_KG_SOCIETY + "='" + socId + "'" + " AND " + KEY_KG_TYPE
                + "='" + type + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            factor = cursor.getString(3);

        }

        if (cursor != null)
            cursor.close();


        return factor;

    }


    public ArrayList<String> getAllCollectedFarmerList(String socId,
                                                       String date, String shift) {
        ArrayList<String> allFarmerList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                + FarmerCollectionTable.KEY_REPORT_SOCIETYID + "='" + socId + "'" + " AND "
                + FarmerCollectionTable.POST_DATE + "='" + date + "'" + " AND "
                + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {

                allFarmerList.add(cursor.getString(1));

            } while (cursor.moveToNext());
        }
        // return contact list
        if (cursor != null)
            cursor.close();

        return allFarmerList;
    }

    public double getTotalMilkCollectedQty(String shift, String date) {

        String query = "SELECT " + FarmerCollectionTable.KEY_REPORT_QUANTITY + " FROM " + TABLE_REPORTS + " WHERE " +
                SalesCollectionTable.POST_DATE + " = '" + date + "' AND " +
                SalesCollectionTable.POST_SHIFT + " = '" + shift + "'" + " AND "
                + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + Util.REPORT_TYPE_FARMER + "'" +
                " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(query, null);
        double qty = 0;
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    qty = qty + cursor.getDouble(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qty;
    }

    public double getTotalMilkSoldQty(String shift, String date) {

        String query = "SELECT " + SalesCollectionTable.KEY_SALES_QUANTITY + " FROM " + TABLE_SALES_REPORT + " WHERE " +
                SalesCollectionTable.POST_DATE + " = '" + date + "' AND " +
                SalesCollectionTable.POST_SHIFT + " = '" + shift + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(query, null);
        double qty = 0;
        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    qty = qty + cursor.getDouble(0);
                } while (cursor.moveToNext());
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qty;
    }
    //get periodic report milk type

    public String getAllFarmerAndTotalWeightOfSession(long date,
                                                      String shift, String reportType) {

        double totalWeight = 0;
        int totFarmer = 0;
        int totalTest = 0;
        int totFarmerAndTest = 0;

        TreeSet<String> treeSet = new TreeSet<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "

                + FarmerCollectionTable.KEY_REPORT_LDATE + "='" + date + "'" + " AND "
                + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND ("
                + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + reportType + "'" + " OR "
                + FarmerCollectionTable.KEY_REPORT_TYPE + "= '" + CollectionConstants.REPORT_TYPE_SAMPLE + "') " +
                " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0;

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String ct = cursor.getString(
                        cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_TYPE)).toUpperCase(Locale.ENGLISH);

                double weight = 0;

                if (!ct.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {

                    weight = cursor.getDouble(
                            cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_QUANTITY));
                    try {
                        totalWeight = totalWeight + weight;
                        treeSet.add(cursor.getString(
                                cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_FARMERID)));
                        totFarmer = totFarmer + 1;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    totalTest = totalTest + 1;
                }

            } while (cursor.moveToNext());
        }
        // return contact list
        if (cursor != null)
            cursor.close();


        totFarmerAndTest = totFarmer + totalTest;

        return totFarmer + "-" + String.valueOf(decimalFormat.format(totalWeight)) + "-" + String.valueOf(totalTest) + "-" + String.valueOf(totFarmerAndTest);

    }

    public String getAllCenterAndTotalWeightOfSession(long date,
                                                      String shift, String reportType) {

        double totalWeight = 0;
        int totFarmer = 0;
        int totalTest = 0;
        int totFarmerAndTest = 0;

        TreeSet<String> treeSet = new TreeSet<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                + FarmerCollectionTable.KEY_REPORT_LDATE + "='" + date + "'" + " AND "
                + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" +
                " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + reportType + "'"
                + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0 + " AND " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + " ='"
                + Util.RECORD_STATUS_COMPLETE + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String mt = cursor.getString(
                        cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_MILKTYPE));
                if (!mt.equalsIgnoreCase("TEST")) {
                    try {
                        totalWeight = totalWeight + cursor.getDouble(
                                cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_QUANTITY));
                        treeSet.add(cursor.getString(1));
                        totFarmer = totFarmer + 1;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    totalTest = totalTest + 1;
                }

            } while (cursor.moveToNext());
        }
        // return contact list
        if (cursor != null)
            cursor.close();
        totFarmerAndTest = totFarmer + totalTest;
        return totFarmer + "-" + String.valueOf(decimalFormat.format(totalWeight)) + "-" + String.valueOf(totalTest) + "-" + String.valueOf(totFarmerAndTest);

    }

    public String getAllSalesAndTotalWeightOfSession(String date,
                                                     String shift) {

        double totalWeight = 0;
        int totFarmer = 0;
        int totalTest = 0;
        int totFarmerAndTest = 0;

        TreeSet<String> treeSet = new TreeSet<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + SalesCollectionTable.TABLE_SALES_REPORT + " WHERE "
                + SalesCollectionTable.POST_DATE + "='" + date + "'" + " AND "
                + SalesCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND "
                + SalesCollectionTable.KEY_SALES_TXN_TYPE + "='" + Util.SALES_TXN_TYPE_SALES + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                String mt = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_MILKTYPE));

                if (!mt.equalsIgnoreCase("TEST")) {
                    try {
                        totalWeight = totalWeight +
                                cursor.getDouble(
                                        cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_QUANTITY));
                        treeSet.add(
                                cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_ID)));
                        totFarmer = totFarmer + 1;
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                } else {
                    totalTest = totalTest + 1;
                }

            } while (cursor.moveToNext());
        }
        // return contact list
        if (cursor != null)
            cursor.close();

        totFarmerAndTest = totFarmer + totalTest;
        return totFarmer + "-" + String.valueOf(decimalFormat.format(totalWeight)) + "-" +
                String.valueOf(totalTest) + "-" + String.valueOf(totFarmerAndTest);

    }


    public boolean checkForSampleId(String sampleId) {
        boolean validation = false;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String selectQuery = null;
        Cursor cursor = null;
        try {

            selectQuery = "SELECT  * FROM " + TABLE_SAMPLE + " WHERE "
                    + KEY_SAMPLE_ID + "='" + sampleId + "'";

            cursor = primaryDatabase.rawQuery(selectQuery, null);
            if (cursor != null && cursor.moveToFirst()) {

                validation = true;
            }
        } catch (Exception e) {

        } finally {
            if (cursor != null)
                cursor.close();

        }
        return validation;
    }
//to get all unsent sms

    public boolean checkDuplicateSampleIdOrBarcode(String idOrBarcode,
                                                   int checkIdOrBarcode) {
        boolean validation = false;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String selectQuery = null;
        Cursor cursor = null;
        try {
            //To check duplicate farmer Id
            if (checkIdOrBarcode == Util.CHECK_DUPLICATE_SAMPLEID) {
                selectQuery = "SELECT  * FROM " + TABLE_SAMPLE + " WHERE "
                        + KEY_SAMPLE_ID + "='" + idOrBarcode + "'";

                cursor = primaryDatabase.rawQuery(selectQuery, null);
                if (cursor != null && cursor.moveToFirst()) {

                    validation = true;
                }
                if (cursor != null)
                    cursor.close();
            }
//To check duplicate barCode
            else if (checkIdOrBarcode == Util.CHECK_DUPLICATE_SAMPLEBARCODE) {
                selectQuery = "SELECT  * FROM " + TABLE_SAMPLE + " WHERE "
                        + KEY_SAMPLE_BARCODE + "='" + idOrBarcode + "'";

                cursor = primaryDatabase.rawQuery(selectQuery, null);
                if (cursor != null && cursor.moveToFirst()) {

                    validation = true;
                }
            } else {
                //ToDo
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }

        return validation;
    }

    public String getDuplicateCenterId(int centerCode) {
        String centerId = null;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String sqliteQuery = "Select " + KEY_CHILLING_CENTER_ID + " from " + TABLE_CHILLING_CENTER;

        Cursor cursor = primaryDatabase.rawQuery(sqliteQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                int id = getCenterId(cursor.getString(0));
                if (id != -1) {
                    if (id == centerCode)
                        centerId = cursor.getString(0);
                }

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return centerId;

    }

    private boolean getDuplicateIntegerCenterId(int centerCode) {
        ArrayList<Integer> arrayListCenter = new ArrayList<>();
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String sqliteQuery = "Select " + KEY_CHILLING_CENTER_ID + " from " + TABLE_CHILLING_CENTER;

        Cursor cursor = primaryDatabase.rawQuery(sqliteQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                int id = getCenterId(cursor.getString(0));
                if (id != -1) {
                    arrayListCenter.add(id);
                }

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        if (arrayListCenter.size() > 0 && arrayListCenter.contains(centerCode)) {
            return true;
        } else {
            return false;
        }

    }

    private int getCenterId(String centerCode) {

        int centerId = -1;

        if (centerCode != null) {
            try {
                centerId = Integer.parseInt(centerCode);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        }

        return centerId;
    }

    public boolean checkDuplicateCenterIdOrBarcode(String idOrBarcode,
                                                   int checkIdOrBarcode) {

        boolean validation = false;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String selectQuery = null;
        Cursor cursor = null;

        if (checkIdOrBarcode == Util.CHECK_DUPLICATE_CENTERCODE) {
            int centerCode = getCenterId(idOrBarcode);
            if (centerCode != -1) {
                validation = getDuplicateIntegerCenterId(centerCode);
            }

        }


        try {
            //To check duplicate farmer Id
            if (checkIdOrBarcode == Util.CHECK_DUPLICATE_CENTERCODE && !validation) {
                selectQuery = "SELECT  * FROM " + TABLE_CHILLING_CENTER + " WHERE "
                        + KEY_CHILLING_CENTER_ID + "='" + idOrBarcode + "'";

                cursor = primaryDatabase.rawQuery(selectQuery, null);
                if (cursor != null && cursor.moveToFirst()) {

                    validation = true;
                }
                if (cursor != null)
                    cursor.close();
            }
//To check duplicate barCode
            else if (checkIdOrBarcode == Util.CHECK_DUPLICATE_CENTERBARCODE) {
                selectQuery = "SELECT  * FROM " + TABLE_CHILLING_CENTER + " WHERE "
                        + KEY_CHILLING_BARCODE + "='" + idOrBarcode + "'";

                cursor = primaryDatabase.rawQuery(selectQuery, null);
                if (cursor != null && cursor.moveToFirst()) {

                    validation = true;
                }
            } else {
                //ToDo
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }

        return validation;
    }

    public Cursor getFarmerDataSociety(String socId) {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT * FROM " + TABLE_FARMER + " ORDER BY "
                + KEY_FARMER_ID;

        // /
        //Log.d("SQL Curser", "getAllData SQL: " + buildSQL);
        return primaryDatabase.rawQuery(buildSQL, null);
    }

    //Insert data to table chilling center table

    public Cursor getTotalcenterFromChilling(String socId) {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT * FROM " + TABLE_CHILLING_CENTER + " ORDER BY "
                + KEY_CHILLING_CENTER_ID;
        return primaryDatabase.rawQuery(buildSQL, null);
    }

    public void insertChillingCenter(CenterEntity centerEntity) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String centerId = null;

        String selectQuery = "Select " + KEY_CHILLING_CENTER_ID + " From " + TABLE_CHILLING_CENTER +
                " Where " + KEY_CHILLING_CENTER_ID + "='" + centerEntity.centerId + "'";

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        DatabaseEntity dbe = new DatabaseEntity(mContext);

        try {

            if (cursor != null && cursor.moveToFirst()) {
                centerId = centerEntity.centerId;
            }

            ContentValues values = dbe.getContentValueForChillingCenter(centerEntity);

            if (centerId == null) {
                values.put(KEY_CHILLING_CENTER_ID,
                        centerEntity.centerId.toUpperCase(Locale.ENGLISH));
                values.put(KEY_CHILLING_BARCODE, centerEntity.centerBarcode);
                dbSync.syncInsert(primaryDatabase, TABLE_CHILLING_CENTER, values);
            } else {
                //To update if center Id is already esixt
                values.put(KEY_CHILLING_BARCODE, centerEntity.centerBarcode);
                dbSync.syncUpdate(primaryDatabase, TABLE_CHILLING_CENTER, values,
                        KEY_CHILLING_CENTER_ID + " =?", new String[]{centerEntity.centerId.toUpperCase(Locale.ENGLISH)});
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();
        }
    }

    public ArrayList<CenterEntity> getAllCenterEntity() {

        ArrayList<CenterEntity> allCenterEntity = null;

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String selectQuery = "Select * " + "From " + TABLE_CHILLING_CENTER + " ORDER BY "
                + KEY_CHILLING_CENTER_ID;

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        DatabaseEntity dbe = new DatabaseEntity(mContext);

        if (cursor != null && cursor.moveToFirst()) {
            allCenterEntity = new ArrayList<>();
            do {
                CenterEntity centerEntity = dbe.getCenterEntity(cursor);
                allCenterEntity.add(centerEntity);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return allCenterEntity;
    }

    public CenterEntity getCenterEntity(String centerId, int checkId) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        CenterEntity centerEntity = null;
        String selectQuery = null;

        if (checkId == Util.CHECK_DUPLICATE_CENTERCODE) {
            selectQuery = "SELECT * FROM " + TABLE_CHILLING_CENTER + " WHERE " + KEY_CHILLING_CENTER_ID + " ='"
                    + centerId.toUpperCase(Locale.ENGLISH) + "'";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_CHILLING_CENTER + " WHERE " + KEY_CHILLING_BARCODE + " ='"
                    + centerId + "'";
        }
        DatabaseEntity dbe = new DatabaseEntity(mContext);
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            centerEntity = dbe.getCenterEntity(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }
        return centerEntity;

    }


    public String getCenterMobileNumber(String centerId) {
        String mobileNumber = null;

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String buildQuery = "Select " + KEY_CHILLING_CONTACT_NUMBER + " From " + TABLE_CHILLING_CENTER
                + " Where " + KEY_CHILLING_CENTER_ID + "='" + centerId + "'";

        Cursor cursor = primaryDatabase.rawQuery(buildQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            mobileNumber = cursor.getString(5);
        }

        if (cursor != null)
            cursor.close();
        return mobileNumber;
    }

    //Extended report code end

    public Cursor getTruckReportDataFromSalesRecordTable() {
        String query = "SELECT  * FROM " + TABLE_SALES_REPORT + " WHERE " + SalesCollectionTable.KEY_SALES_TXN_TYPE + " ='" + Util.SALES_TXN_TYPE_TRUCK + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        return primaryDatabase.rawQuery(query, null);
    }

    public Cursor getTruckTotalCollection(String date_truck, String s_shift, String txnType) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String query = "SELECT  * FROM " + TABLE_SALES_REPORT + " WHERE " + SalesCollectionTable.POST_SHIFT + "='" + s_shift + "'" + " AND " + SalesCollectionTable.POST_DATE + "='" + date_truck + "'" +
                " AND " + SalesCollectionTable.KEY_SALES_TXN_TYPE + " ='" + txnType + "'";
        return primaryDatabase.rawQuery(query, null);
    }

    //Method to initialize society table and user

    // return cursor for ceind,shift and date used in Usermilckcollectionactivity list view
    public Cursor getUserMilkColectionWeingScale(String centerid, String shift, String sDate) {
        ArrayList<ReportEntity> allReports = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE " + FarmerCollectionTable.POST_DATE + "='" + sDate + "'" + " AND "
                + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND "
                + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 0 + " AND "
                + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + " ='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                + " AND " + FarmerCollectionTable.KEY_REPORT_FARMERID + " = '" + centerid + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        return primaryDatabase.rawQuery(selectQuery, null);
    }

    // return chilling center route
    public String getRouteFromChillingCenterTable(String centerid) {
        String centerID = "NA";
        String selectQuery = "SELECT  centerRoute FROM " + TABLE_CHILLING_CENTER + " WHERE "
                + KEY_CHILLING_CENTER_ID + "='" + centerid + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                centerID = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return centerID;
    }

    //This function will return all chilling center id's
    public Cursor getAllActiveCenterId() {

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String selectQuery = "Select " + KEY_CHILLING_CENTER_ID +
                " From " + TABLE_CHILLING_CENTER +
                " WHERE " + KEY_CHILLING_IS_ACTIVE + " = " + 1 + " ORDER BY "
                + KEY_CHILLING_CENTER_ID;

        return primaryDatabase.rawQuery(selectQuery, null);
    }

    public Cursor getChillingCenterRecords(String centerid, String shift, String date) {

        String selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 1
                + " AND " + FarmerCollectionTable.KEY_REPORT_FARMERID + "='" + centerid + "'" + " AND " + FarmerCollectionTable.POST_DATE + "='" + date + "'" + " AND "
                + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        return primaryDatabase.rawQuery(selectQuery, null);
    }

    // My rate chart

    public AverageReportDetail getChillingCenterReport(String centerId, String shift, long date, long startDate,
                                                       long endDate, boolean isPeriodic, boolean isIndividual) {

        AverageReportDetail avgReportDetail = null;
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String selectQuery = null;
        if (isIndividual) {
            selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 1
                    + " AND " + FarmerCollectionTable.KEY_REPORT_FARMERID + "='" + centerId + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_LDATE + "='" + date + "'" + " AND "
                    + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0;
        } else if (isPeriodic && (centerId != null && !centerId.equalsIgnoreCase("All farmers"))) {
            selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_LDATE + " BETWEEN " + startDate + " AND " + endDate + " AND "
                    + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0 + " AND " + FarmerCollectionTable.KEY_REPORT_FARMERID + "='" + centerId + "'"
                    + " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + Util.REPORT_TYPE_MCC + "'";
        } else if (isPeriodic) {
            selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_LDATE + " BETWEEN " + startDate + " AND " + endDate + " AND "
                    + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0
                    + " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + Util.REPORT_TYPE_MCC + "'";
        } else {

            selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 1
                    + " AND " + FarmerCollectionTable.KEY_REPORT_LDATE + "='" + date + "'" + " AND "
                    + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0 + " AND "
                    + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + Util.REPORT_TYPE_MCC + "'";

        }

        selectQuery = selectQuery + " AND " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + " ='" + Util.RECORD_STATUS_COMPLETE + "'";
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        avgReportDetail = new DatabaseEntity(mContext).getAverageReportDetails(cursor, null);
        Util.avgChillingDetails = avgReportDetail;
        return avgReportDetail;

    }


    public void deleteUncommittedWSData(String[] centerIds) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String table = TABLE_REPORTS;
        String key = FarmerCollectionTable.KEY_REPORT_COLUMNID;

        for (int i = 0; i < centerIds.length; i++) {
            try {
                dbSync.syncDelete(primaryDatabase, table, key + " = ?",
                        new String[]{centerIds[i]});
                if (dbSync.getSqliteDatabase() != null) {
                    dbSync.syncDelete(dbSync.getSqliteDatabase(), table, key + " = ?",
                            new String[]{centerIds[i]});
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // validate SID
    public boolean sequenceNumberValidation(int sequenceNumber, String date, String shift) {
        boolean isValidSeqNum = false;

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String sqlQuery = "SELECT *" + " FROM " + TABLE_REPORTS + " WHERE " +
                FarmerCollectionTable.POST_DATE + " ='" +
                date + "'" + " AND "
                + FarmerCollectionTable.POST_SHIFT + " ='" + shift + "' AND "
                + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 0 + " AND "
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " =" + sequenceNumber;

        Cursor cursor = primaryDatabase.rawQuery(sqlQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            isValidSeqNum = true;
        }

        if (cursor != null)
            cursor.close();
        return isValidSeqNum;
    }

    // get next sid
    public Cursor nextSequenceNumber(int sequenceNumber, String date, String shift) {
        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String sdate = smartCCUtil.getReportFormatDate();

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String sqlQuery = "SELECT *" + " FROM " + TABLE_REPORTS + " WHERE " + FarmerCollectionTable.POST_DATE + " ='" +
                sdate + "'" + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'"
                + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 0 + " AND "
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " >" + sequenceNumber + " ORDER BY "
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " ASC ";

        return primaryDatabase.rawQuery(sqlQuery, null);

    }

    // get next sid
    public Cursor getCurrentSequenceNumber(int sequenceNumber, String date, String shift) {
        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String sdate = smartCCUtil.getReportFormatDate();
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String sqlQuery = "SELECT *" + " FROM " + TABLE_REPORTS + " WHERE " + FarmerCollectionTable.POST_DATE + " ='" +
                sdate + "'" + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 0 + " AND "
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " =" + sequenceNumber + " ORDER BY "
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " ASC ";

        return primaryDatabase.rawQuery(sqlQuery, null);

    }


    public boolean isCollectionStartedForCurrentShift() {
        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String date = smartCCUtil.getReportFormatDate();
        String shift = Util.getCurrentShift();

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String select_Query = "SELECT * FROM " + TABLE_REPORTS + " WHERE " + FarmerCollectionTable.POST_DATE + "='" + date + "'"
                + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + shift + "' AND " + FarmerCollectionTable.KEY_REPORT_TYPE + " ='" + Util.REPORT_TYPE_MCC + "'";

        Cursor cursor = primaryDatabase.rawQuery(select_Query, null);

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            if (cursor != null)
                cursor.close();
            return false;
        }

    }

    public Cursor getEditableShiftRecord(String id, String date, String shift) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        String[] strArray = date.split("-");
        StringBuilder yyyymmddDate = new StringBuilder(strArray[0]).
                append(strArray[1]).append(strArray[2]);

        long lDate = Long.valueOf(yyyymmddDate.toString());

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String selectQuery;
        if (id == null) {
            selectQuery = "Select * From " + TABLE_REPORTS + " Where " +
                    FarmerCollectionTable.KEY_REPORT_LDATE + " = " + lDate + " AND " +
                    FarmerCollectionTable.POST_SHIFT + " ='" + shift + "' AND " +
                    FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + 1 +
                    " AND " + FarmerCollectionTable.KEY_REPORT_MILKTYPE + " !='TEST'";
        } else {
            selectQuery = "Select * From " + TABLE_REPORTS + " Where " +
                    FarmerCollectionTable.KEY_REPORT_LDATE + " = " + lDate + " AND " +
                    FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'AND " +
                    KEY_FARMER_ID + " ='" + id + "' AND " +
                    FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + 1 +
                    " AND " + FarmerCollectionTable.KEY_REPORT_MILKTYPE + " !='TEST'";
        }

        selectQuery = selectQuery + " AND " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + " ='" + Util.RECORD_STATUS_COMPLETE + "'";
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor == null || !cursor.moveToFirst()) {
            if (cursor != null)
                cursor.close();

            if (id != null) {
                selectQuery = "Select * From " + TABLE_REPORTS + " Where " +
                        FarmerCollectionTable.POST_DATE + " ='" + date + "' AND " +
                        FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'AND " +
                        KEY_FARMER_ID + " ='" + Util.paddingFarmerId(id, amcuConfig.getFarmerIdDigit()) + "' AND " +
                        FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + 1 +
                        " AND " + FarmerCollectionTable.KEY_REPORT_MILKTYPE + " !='TEST'";
            }

            cursor = primaryDatabase.rawQuery(selectQuery, null);
        }

        return cursor;
    }


    public Cursor getEditedReportCursor(long sequenceNumber) {
        //  long sequenceNumberID = getColIDFromSequenceNumber(sampleNumber);
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String type = "new";
        String selecetQuery = "SELECT * FROM " + TABLE_EXTENDED_REPORT + " Where " + EditRecordCollectionTable.KEY_REPORT_FOREIGN_SEQUENCE_ID + " =" + sequenceNumber;
        return primaryDatabase.rawQuery(selecetQuery, null);

    }

    // deleting of old record data by providing number o fdays
    public void deleteOlderRecords(int days) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -days);

        long backtime = cal.getTimeInMillis();
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String selecetQuery = "SELECT * FROM " + TABLE_EXTENDED_REPORT;
        Cursor cursor = primaryDatabase.rawQuery(selecetQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                long cursortime = cursor.getLong(cursor.getColumnIndex("timeMillis"));
                if (cursortime < backtime)
                    try {
                        dbSync.syncDelete(primaryDatabase, TABLE_EXTENDED_REPORT,
                                EditRecordCollectionTable.KEY_REPORT_TIME_MILLI + "=" + cursortime, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
    }


    public boolean isCollectionIdValid(String id) {
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String selecetQuery = "SELECT * FROM " + TABLE_CHILLING_CENTER + " WHERE " + KEY_CHILLING_CENTER_ID + " ='"
                + id.toUpperCase(Locale.ENGLISH) + "'";

        Cursor cursor = primaryDatabase.rawQuery(selecetQuery, null);
        boolean value = cursor.moveToFirst();

        if (cursor != null)
            cursor.close();
        return value;
    }

    public boolean isCollectionStarted() {
        boolean isValidSeqNum = false;

        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String date = smartCCUtil.getReportFormatDate();

        String shift = Util.getCurrentShift();

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String sqlQuery = "SELECT *" + " FROM " + TABLE_REPORTS + " WHERE " + FarmerCollectionTable.POST_DATE + " ='" +
                date + "'" + " AND " + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'";

        Cursor cursor = primaryDatabase.rawQuery(sqlQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            isValidSeqNum = true;
        } else {

        }

        if (cursor != null)
            cursor.close();
        return isValidSeqNum;
    }

    public ArrayList<ReportEntity> getUncommittedWSRecordsEntitiesParaller(
            String sampleId1,
            String sampleId2, String lastSequenceNumber) {

        ArrayList<ReportEntity> allReports = new ArrayList<>();
        String selectQuery = null;
        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String currentDate = smartCCUtil.getReportFormatDate();
        String shift = Util.getCurrentShift();
        // Select All Query

        if (lastSequenceNumber != null) {
            int lastseq = Integer.valueOf(lastSequenceNumber);
            selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 0
                    + " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + Util.REPORT_TYPE_MCC + "'"
                    + " AND " + FarmerCollectionTable.POST_DATE + "='" + currentDate + "'"
                    + " AND " + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'"
                    + " AND " + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "=" + lastseq
                    + " ORDER BY " + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " ASC ";
        } else if (sampleId1 == null || (sampleId2 == null || sampleId2.length() < 1)) {
            selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 0 + " AND "
                    + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + Util.REPORT_TYPE_MCC + "'"
                    + " AND " + FarmerCollectionTable.POST_DATE + "='" + currentDate + "'"
                    + " AND " + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'"
                    + " AND " + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "!=0" + " AND "
                    + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "!=" + Util.LAST_SEQ_NUM
                    + " ORDER BY " + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " ASC ";
        } else {
            int sequneceNumber1 = 0;
            try {
                sequneceNumber1 = Integer.parseInt(sampleId1);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            int sequenceNumber2 = 0;
            try {
                sequenceNumber2 = Integer.parseInt(sampleId2);
            } catch (NumberFormatException e) {
                sequenceNumber2 = sequneceNumber1;
                e.printStackTrace();
            }

            selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE "
                    + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 0 + " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + "='"
                    + Util.REPORT_TYPE_MCC + "'"
                    + " AND " + FarmerCollectionTable.POST_DATE + "='" + currentDate + "'"
                    + " AND " + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'"
                    + " AND " + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "!=" + sequneceNumber1 + " AND " +
                    FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "!=" + sequenceNumber2 + " AND " + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "!=0" + " AND "
                    + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "!=" + Util.LAST_SEQ_NUM +
                    " ORDER BY " + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " ASC ";
        }


        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);
                allReports.add(reportEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        // return contact list
        return allReports;

    }

    private void initializeDatabase(SQLiteDatabase primaryDatabase) {
        GetEntity getEntity = new GetEntity(mContext, pref);

        SocietyEntity socEntity = getEntity.getSocietyEntity();
        createSocietyTable(primaryDatabase, socEntity);
        ArrayList<UserEntity> allUserEntity = getEntity.getUserEntity();
        createUsers(primaryDatabase, allUserEntity);
        ArrayList<SampleDataEntity> allSampleDataEntity = getEntity.getDefaultSample();
        createSampleTable(primaryDatabase, allSampleDataEntity);

        configurationHandler = DefaultConfigurationHandler.getInstance();
        Log.i(TAG, "going read default logs and load it into config table in db");
        //TODO Read props and load it to Configuration table.
        // FIXME rename method name
        DefaultConfigurationHandler.initConfigTable(primaryDatabase);
        DefaultConfigurationHandler.loadAllConfigurations();
        // add current version code to shared preferences
        pref.edit().putInt(PREF_VERSION_CODE_KEY, BuildConfig.VERSION_CODE).apply();
    }


    private void clearSampleTable(SQLiteDatabase primaryDatabase) {
        primaryDatabase.execSQL("delete from " + TABLE_SAMPLE);
    }

    private void createSampleTable(SQLiteDatabase primaryDatabase, ArrayList<SampleDataEntity> allSampleEntity) {
        Log.v("DB", "Adding new sample code");

        ContentValues values = new ContentValues();

        for (int i = 0; i < allSampleEntity.size(); i++) {
            SampleDataEntity sampleEnt = allSampleEntity.get(i);
            values.put(KEY_SAMPLE_FAT, sampleEnt.sampleFat);
            values.put(KEY_SAMPLE_IS_FAT, sampleEnt.sampleIsFat);
            values.put(KEY_SAMPLE_IS_SNF, sampleEnt.sampleIsSnf);
            values.put(KEY_SAMPLE_IS_WEIGH, sampleEnt.sampleIsWeigh);
            values.put(KEY_SAMPLE_MODE, sampleEnt.sampleMode);
            values.put(KEY_SAMPLE_SOC_ID, String.valueOf(session.getSocietyColumnId()));
            values.put(KEY_SAMPLE_OTHER2, sampleEnt.sampleOther2);
            values.put(KEY_SAMPLE_OTHER3, sampleEnt.sampleOther3);
            values.put(KEY_SAMPLE_WEIGH, sampleEnt.sampleWeigh);
            values.put(KEY_SAMPLE_RATE, sampleEnt.sampleRate);
            values.put(KEY_SAMPLE_AMOUNT, sampleEnt.sampleAmount);
            values.put(KEY_SAMPLE_BARCODE, sampleEnt.sampleBarcode);
            values.put(KEY_SAMPLE_ID, sampleEnt.sampleId);
            primaryDatabase.insert(TABLE_SAMPLE, null, values);
        }

    }

    private void createSocietyTable(SQLiteDatabase primaryDatabase, SocietyEntity socEntity) {

        ContentValues values = new ContentValues();
        values.put(KEY_SOCIETY_ADDRESS, socEntity.address);
        values.put(KEY_SOCIETY_BMCID, socEntity.bmcId);
        values.put(KEY_SOCIETY_CONPERSON1, socEntity.conPerson1);
        values.put(KEY_SOCIETY_CONTACT1, socEntity.contactNum1);
        values.put(KEY_SOCIETY_LOCATION, socEntity.location);
        values.put(KEY_SOCIETY_ROUTE, socEntity.route);
        values.put(KEY_SOCIETY_EMAIL1, socEntity.socEmail1);
        values.put(KEY_SOCIETY_NAME, socEntity.name);

        values.put(KEY_SOCIETY_CONPERSON2, socEntity.conPerson2);
        values.put(KEY_SOCIETY_CONTACT2, socEntity.contactNum2);
        values.put(KEY_SOCIETY_EMAIL2, socEntity.socEmail2);
        values.put(KEY_SOCIETY_NUMFARMER, socEntity.numOfFarmer);

        values.put(KEY_SOCIETY_REGISTRATION_DATE, socEntity.society_regDate);
        values.put(KEY_SOCIETY_WEEKLY, socEntity.society_weekly);
        values.put(KEY_SOCIETY_MONTHLY, socEntity.society_monthly);
        values.put(KEY_SOCIETY_CODE, socEntity.socCode.toUpperCase(Locale.ENGLISH));

        values.put(KEY_SOCIETY_COLUMNID, socEntity.colId);
        long id = primaryDatabase.insert(TABLE_SOCIETY, null, values);
        session.setSocietyName(socEntity.name);
        session.setSocietyColumnId(id);
        Log.v("TABLE_SOCIETY", "Table society created successfully");

    }

    private void createUsers(SQLiteDatabase primaryDatabase, ArrayList<UserEntity> allUserEntity) {

        primaryDatabase.beginTransaction();
        ContentValues values = new ContentValues();

        for (int i = 0; i < allUserEntity.size(); i++) {
            UserEntity userEntity = allUserEntity.get(i);
            values.put(KEY_USER_EMAIL, userEntity.emailId);
            values.put(KEY_USER_MOBILE, userEntity.mobile);
            values.put(KEY_USER_NAME, userEntity.name);
            values.put(KEY_USER_PASSWORD, userEntity.password);
            values.put(KEY_USER_SOCIETYID, userEntity.centerId);
            values.put(KEY_USER_ROLE, userEntity.role);
            values.put(KEY_USER_REGDATE, userEntity.regDate);
            values.put(KEY_USER_WEEKDATE, userEntity.weekDate);
            values.put(KEY_USER_MONTHDATE, userEntity.monthDate);
            values.put(KEY_USER_USERID, userEntity.userId);
            primaryDatabase.insert(TABLE_USER, null, values);
        }
        primaryDatabase.setTransactionSuccessful();
        primaryDatabase.endTransaction();

    }

    public Cursor getAllMyRateChartList() {
        String selectQuery = "SELECT  * FROM " + TABLE_MY_RATE_CHART;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null) {
            int cou = cursor.getCount();
        }
        return cursor;
    }

    public String getSelectedMyRateChart(String milktype) {

        String currentShift = Util.getCurrentShift();
        String rateChartName = null;
        Date dte = new Date();
        long longenddate = 0;
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
        String dateText = DATE_FORMAT.format(dte);
        try {
            Date edate = DATE_FORMAT.parse(dateText);
            longenddate = edate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        String selectQuery;
        selectQuery = "SELECT  * FROM " + TABLE_MY_RATE_CHART + " WHERE " + KEY_MY_RATECHART_MILK_TYPE + " = '"
                + milktype + "' AND " + KEY_MY_RATECHART_START_DATE + " <= " + longenddate +
                " ORDER BY " + KEY_MY_RATECHART_START_DATE + " DESC ," + KEY_MY_RATECHART_START_SHIFT + " ASC LIMIT 2";


        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            int i = cursor.getCount();
            cursor.moveToPosition(0);
            rateChartName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME));
            long cursorZeroStartDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_DATE));
            String cursorZeroStartShift = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SHIFT));
            //longenddate = current date
            if (cursorZeroStartDate == longenddate) {

                if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING) &&
                        !currentShift.equalsIgnoreCase(cursorZeroStartShift)) {
                    if (cursor.getCount() > 1) {
                        cursor.moveToPosition(1);
                        rateChartName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME));
                    } else {
                        rateChartName = null;
                    }
                }
            }

            int j = cursor.getCount();

        }
        if (cursor != null)
            cursor.close();
        //  Util.displayErrorToast("RateChart name:: " + rateChartName + " MIlk Type:: " + milktype, mContext);
        return rateChartName;
    }

    public Cursor getSelectedMyRateChartList(String ratechartname) {
        String selectQuery = "SELECT  * FROM " + TABLE_MY_RATE_CHART + " WHERE " + KEY_MY_RATECHART_NAME + " = '" + ratechartname + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null) {
            int cou = cursor.getCount();
        }
        return cursor;
    }

    public void insertMyRateChart(String ratechartname, String cattletype, String kgfatrate, String startfat, String endfat, String kgsnfrate, String startsnf, String endsnf, String startshift, long startdate, String endshift, long enddate) {

        try {
            SQLiteDatabase primaryDatabase = sqliteDatabase;
            ContentValues values = new ContentValues();
            // Update the table with a sentinel value
            values.put(KEY_MY_RATECHART_NAME, ratechartname);
            values.put(KEY_MY_RATECHART_MILK_TYPE, cattletype);
            values.put(KEY_MY_RATECHART_MILIDATE, System.currentTimeMillis());
            values.put(KEY_MY_RATECHART_KG_FAT_RATE, kgfatrate);
            values.put(KEY_MY_RATECHART_START_FAT, startfat);
            values.put(KEY_MY_RATECHART_END_FAT, endfat);
            values.put(KEY_MY_RATECHART_KG_SNF_RATE, kgsnfrate);
            values.put(KEY_MY_RATECHART_START_SNF, startsnf);
            values.put(KEY_MY_RATECHART_END_SNF, endsnf);
            values.put(KEY_MY_RATECHART_START_SHIFT, startshift);
            values.put(KEY_MY_RATECHART_START_DATE, startdate);
            values.put(KEY_MY_RATECHART_END_SHIFT, endshift);
            values.put(KEY_MY_RATECHART_END_DATE, enddate);
            dbSync.syncInsert(primaryDatabase, TABLE_MY_RATE_CHART, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteMyExpireRateChart(String ratechartname) {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String deleteQuery = "DELETE FROM " + TABLE_MY_RATE_CHART + " WHERE " + KEY_MY_RATECHART_NAME + " = '" + ratechartname + "'";
        primaryDatabase.execSQL(deleteQuery);
    }

    public void deleteMyRateChartBeforeEndDate(String milktype, String classtype) {

        String endDate;
        Cursor alldata;
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Date dte = new Date();
        long currentdate = 0;
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
        String dateText = DATE_FORMAT.format(dte);
        try {
            Date edate = DATE_FORMAT.parse(dateText);
            currentdate = edate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        String selectQuery;
        selectQuery = "SELECT  * FROM " + TABLE_MY_RATE_CHART + " WHERE " + KEY_MY_RATECHART_MILK_TYPE + " = '"
                + milktype + "' AND " + KEY_MY_RATECHART_CLASS + "= '" + classtype + "' AND " + KEY_MY_RATECHART_START_DATE + " <= " + currentdate +
                " ORDER BY " + KEY_MY_RATECHART_START_DATE + " DESC ," + KEY_MY_RATECHART_START_SHIFT + " ASC";

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        alldata = cursor;
        int i = cursor.getCount();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                    long eDte = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_DATE));
                    long startDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_DATE));
                    Date tmpedate = new Date(eDte);
                    endDate = df.format(tmpedate);
                    if (currentdate == startDate) {
                        if (Util.getCurrentShift().equalsIgnoreCase(AppConstants.Shift.MORNING)
                                && cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SHIFT)).equalsIgnoreCase("e")) {

                        } else {
                            deleteBeforeEndDate(endDate, alldata);
                            break;
                        }
                    } else {
                        deleteBeforeEndDate(endDate, alldata);
                        break;
                    }


                } while (cursor.moveToNext());
            }
        }

        if (cursor != null)
            cursor.close();
    }

    private void deleteBeforeEndDate(String endDateOfLatestRateChart, Cursor alldatacursor) {
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String currentShift = Util.getCurrentShift();
        Date currentDate = new Date();
        String currentDateText = df.format(currentDate);
        try {
            currentDate = df.parse(currentDateText);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date dateendObjLatestRateChart = null, cdate = null;
        String endShift;
        try {
            dateendObjLatestRateChart = df.parse(endDateOfLatestRateChart);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        int count = 0;
        if (alldatacursor != null && alldatacursor.getCount() > 0) {
            if (alldatacursor.moveToFirst()) {
                do {
                    if (count == 0) {
                        count++;
                        if (currentDate.equals(dateendObjLatestRateChart)) {
                            endShift = alldatacursor.getString(alldatacursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_SHIFT));
                            if (currentShift.equalsIgnoreCase(AppConstants.Shift.EVENING)
                                    && endShift.equalsIgnoreCase("M")) {
                                //delete
                                deleteMyExpireRateChart(alldatacursor.getString(alldatacursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME)));
                                //  Toast.makeText(CustomRateChartActivity.this, "equal date ,delete shift wise", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        long eDte = alldatacursor.getLong(alldatacursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_DATE));
                        Date allCursorEdateOneByONe = new Date(eDte);

                        if (allCursorEdateOneByONe.before(dateendObjLatestRateChart)) {
                            deleteMyExpireRateChart(alldatacursor.getString(alldatacursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME)));
                            //  Toast.makeText(CustomRateChartActivity.this, "end date ", Toast.LENGTH_SHORT).show();
                        }
                        if (allCursorEdateOneByONe.equals(dateendObjLatestRateChart)) {
                            endShift = alldatacursor.getString(alldatacursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_SHIFT));
                            if (currentShift.equalsIgnoreCase(AppConstants.Shift.EVENING) && endShift.equalsIgnoreCase("M")) {
                                //delete
                                deleteMyExpireRateChart(alldatacursor.getString(alldatacursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME)));
                                //  Toast.makeText(CustomRateChartActivity.this, "equal date ,delete shift wise", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                } while (alldatacursor.moveToNext());
            }
        }
        if (alldatacursor != null)
            alldatacursor.close();

    }

    public void deleteExpireRateChartBeforeCurrentDate() {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        Date currentDate = new Date();
        Date cdate = null;

        try {
            String dateText = df.format(currentDate);
            cdate = df.parse(dateText);
            long longcurrentdate = cdate.getTime();
            getCursorDataBeforeEndDate();
            String deleteQuery = "DELETE FROM " + TABLE_MY_RATE_CHART + " WHERE " + KEY_MY_RATECHART_END_DATE + " < " + longcurrentdate;
            primaryDatabase.execSQL(deleteQuery);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //For farmer in transaction

    private void getCursorDataBeforeEndDate() {
        Date cdate = null;
        Cursor cursor = null;
        try {
            SQLiteDatabase primaryDatabase = sqliteDatabase;
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
            Date currentDate = new Date();
            String dateText = df.format(currentDate);
            cdate = df.parse(dateText);
            long longcurrentdate = cdate.getTime();
            String selectQuery = "Select * from " + TABLE_MY_RATE_CHART + " WHERE " + KEY_MY_RATECHART_END_DATE + " < " + longcurrentdate;
            cursor = primaryDatabase.rawQuery(selectQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        long eDte = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_DATE));
                        long startDate = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_DATE));
                        String createDate = String.valueOf(cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_MILIDATE)));
                        String rateChartName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME));
                        String startShift = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SHIFT));
                        String endShift = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_SHIFT));
                        CustomRateChartUtil.getInstance(mContext).writeMyRatechartLogs(rateChartName, startDate, eDte, startShift, endShift, mContext, "DELETE", System.currentTimeMillis());
                    } while (cursor.moveToNext());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null)
                cursor.close();
        }
    }

    public boolean isRateChartNameAvailable(String rateChartName) {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String selectQuery = "Select * from " + TABLE_MY_RATE_CHART + " WHERE " + KEY_MY_RATECHART_NAME + " ='" + rateChartName + "'";
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        boolean value;
        if (cursor.getCount() == 1) {
            value = false;
        } else {
            value = true;
        }


        if (cursor != null)
            cursor.close();
        return value;
    }

    public ArrayList<RatechartDetailsEnt> getAllMyRateChartMilkTypeList(String milktype) {
        ArrayList<RatechartDetailsEnt> rateChartdetEnt = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_MY_RATE_CHART + " WHERE " + KEY_MY_RATECHART_MILK_TYPE + " = '" + milktype + "' ORDER BY " + KEY_MY_RATECHART_START_DATE + " DESC";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            int cou = cursor.getCount();
            do {
                RatechartDetailsEnt ratechEnt = new RatechartDetailsEnt();
                ratechEnt.rateChartName = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_NAME));
                ratechEnt.rateMilkType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_MILK_TYPE));
                ratechEnt.rateOther1 = "NA";
                ratechEnt.rateOther2 = "NA";
                ratechEnt.rateValidityFrom = "NA";
                ratechEnt.rateValidityTo = "NA";
                ratechEnt.rateSocId = "NA";
                ratechEnt.rateLvalidityFrom = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_DATE));
                ratechEnt.rateLvalidityTo = cursor.getLong(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_DATE));
                ratechEnt.isActive = "NA";
                ratechEnt.ratechartType = "NA";

                rateChartdetEnt.add(ratechEnt);

            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return rateChartdetEnt;
    }


    public String getMyRateChartMinFATandSNF(int i, String rateChartName) {
        String minFatOrSnf = "0.0";

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = "SELECT  * FROM " + TABLE_MY_RATE_CHART + " WHERE "
                + KEY_MY_RATECHART_NAME + "='" + rateChartName + "'" + " ORDER BY "
                + KEY_MY_RATECHART_START_DATE + " DESC ";

        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);

        if (cursor != null && cursor.moveToFirst()) {
            if (i == 0) {
                minFatOrSnf = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SNF));
            } else {
                minFatOrSnf = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_FAT));
            }

        }
        if (cursor != null)
            cursor.close();


        if (minFatOrSnf == null) {
            minFatOrSnf = "0.0";
        }

        return minFatOrSnf;
    }

    public String getMyRatechartMaxFATandSNF(int i,
                                             String rateChartName) {
        String fatOrSnf = null;

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        String buildSQL = null;

        buildSQL = "SELECT  * FROM " + TABLE_MY_RATE_CHART + " WHERE "
                + KEY_MY_RATECHART_NAME + "='" + rateChartName + "'" + " ORDER BY "
                + KEY_MY_RATECHART_START_DATE + " DESC ";

        Cursor cursor = primaryDatabase.rawQuery(buildSQL, null);

        if (cursor != null && cursor.moveToFirst()) {

            if (i == 0) {
                fatOrSnf = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_SNF));
            } else {
                fatOrSnf = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_FAT));
            }

        }
        if (cursor != null)
            cursor.close();


        if (fatOrSnf == null) {
            fatOrSnf = "14.0";
        }

        return fatOrSnf;
    }


    public long insertLicenseData() {
        long insertedLong = 0;

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        try {
            if (amcuConfig != null && (amcuConfig.getLicStatusCode() == 200
                    || amcuConfig.getLicStatusCode() == 204)) {
                dbSync.syncDelete(primaryDatabase, TABLE_LICENSE, null, null);
                ContentValues values = new ContentValues();
                values.put(KEY_LICENSE_REQUEST_DATE, System.currentTimeMillis());
                values.put(KEY_LICENSE_STATUS_CODE, amcuConfig.getLicStatusCode());
                values.put(KEY_LICENSE_START_DATE, amcuConfig.getLicStartDate());
                values.put(KEY_LICENSE_END_DATE, amcuConfig.getLicEndDate());
                values.put(KEY_LICENSE_VALID, amcuConfig.getLicValidityStatus());
                values.put(KEY_LICENSE_MESSAGE, amcuConfig.getLicMessage());
                insertedLong = dbSync.syncInsert(primaryDatabase, TABLE_LICENSE, values);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return insertedLong;
    }

    public Cursor getLicenseData() {
        Cursor cursor = null;
        SQLiteDatabase database = sqliteDatabase;
        String query = "select * from " + TABLE_LICENSE;
        try {
            cursor = database.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {

            } else {
                insertLicenseData();
                getLicenseData();
            }
        } catch (Exception e) {
            insertLicenseData();
            getLicenseData();
        }
        return cursor;
    }


    //Transaction code for collection

    public boolean checkForDuplicateChillingRecord(String maOrWs, String centerid, String milkType) {

        String selectQuery = null;
        Cursor cursor = null;
        SQLiteDatabase readDatabase = sqliteDatabase;

        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String date = smartCCUtil.getReportFormatDate();

        String shift = Util.getCurrentShift();

        if (maOrWs.equalsIgnoreCase("MA")) {
            selectQuery = "Select * from " + TABLE_REPORTS + " WHERE " + FarmerCollectionTable.KEY_REPORT_FARMERID + " ='" + centerid + "'"
                    + " AND " + FarmerCollectionTable.POST_DATE + "='" + date + "'" + " AND "
                    + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'" + " And "
                    + FarmerCollectionTable.KEY_REPORT_MILKTYPE + " ='" + milkType + "'" + " AND "
                    + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + " ='" + Util.RECORD_STATUS_COMPLETE + "'";
        } else if (maOrWs.equalsIgnoreCase("WS")) {
            selectQuery = "Select * from " + TABLE_REPORTS + " WHERE " + FarmerCollectionTable.KEY_REPORT_FARMERID + " ='" + centerid + "'"
                    + " AND " + FarmerCollectionTable.POST_DATE + "='" + date + "'" + " AND "
                    + FarmerCollectionTable.KEY_REPORT_MILKTYPE + " ='" + milkType + "'" + " AND "
                    + FarmerCollectionTable.POST_SHIFT + "='" + shift + "'";
        }


        if (selectQuery != null) {
            cursor = readDatabase.rawQuery(selectQuery, null);
        }
        boolean value;
        if (cursor != null && cursor.moveToFirst()) {
            value = true;
        } else {
            value = false;
        }

        if (cursor != null)
            cursor.close();
        return value;
    }

    private void restartApplication() {
        //TODO  commented for testing purpose
    /*    Intent intent = new Intent(mContext, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);*/
    }

    public void addCompleteChillingCenter(ArrayList<CenterEntity> allCenterEnt, String primaryOrSecondary) {
        SQLiteDatabase database;
        if (primaryOrSecondary.equalsIgnoreCase(isPrimary)) {
            database = sqliteDatabase;
        } else {
            database = dbSync.getSqliteDatabase();
        }

        try {
            database.beginTransaction();
            for (int i = 0; i < allCenterEnt.size(); i++) {
                try {
                    if (allCenterEnt.get(i).centerBarcode != null && !allCenterEnt.get(i).centerBarcode.equalsIgnoreCase("") &&
                            !allCenterEnt.get(i).centerBarcode.equalsIgnoreCase("null")) {
                        getAndDeleteCenterTrans(allCenterEnt.get(i).centerBarcode, 1, database);
                    }
                    getAndDeleteCenterTrans(allCenterEnt.get(i).centerId, 0, database);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InsertChillingCenterTrans(allCenterEnt, database);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            SmartCCUtil.todoDbFailure(mContext, primaryOrSecondary);
        } finally {
            endTransaction(database);
        }


    }

    private void getAndDeleteCenterTrans(String farmIdOrBarcode,
                                         int farmOrBarcode,
                                         SQLiteDatabase primaryDatabase) throws Exception {
        long colId = -1;
        String farmerId = null;
        String selectQuery = null;

        if (farmOrBarcode == 0) {
            //for farm Id
            selectQuery = "SELECT  * FROM " + TABLE_CHILLING_CENTER + " WHERE "
                    + KEY_CHILLING_CENTER_ID + "='" + farmIdOrBarcode + "'";
        } else {
            //For barcode
            selectQuery = "SELECT  * FROM " + TABLE_CHILLING_CENTER + " WHERE "
                    + KEY_CHILLING_BARCODE + "='" + farmIdOrBarcode + "'";
        }

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                colId = cursor.getLong(0);
                farmerId = cursor.getString(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (colId != -1) {


            String strFilter = KEY_CHILLING_COLUMNID + " = ?";
            dbSync.syncDeleteByDB(primaryDatabase, TABLE_CHILLING_CENTER, strFilter, new String[]{String.valueOf(colId)});

            cursor.close();
            if (farmerId != null) {
                Util.displayErrorToast("Center Id: " + farmerId + " has updated!", mContext);
            }
        }
        if (cursor != null)
            cursor.close();
    }


    //Insert chilling center table as transaction

    private void InsertChillingCenterTrans(ArrayList<CenterEntity> allCenterEntity,
                                           SQLiteDatabase primaryDatabase) throws Exception {

        DatabaseEntity dbe = new DatabaseEntity(mContext);
        for (int i = 0; i < allCenterEntity.size(); i++) {
            ContentValues values = dbe.getContentValueForChillingCenter(allCenterEntity.get(i));
            dbSync.syncInsertExcelDatabase(primaryDatabase, TABLE_CHILLING_CENTER, values);
        }


    }


    private void endTransaction(SQLiteDatabase db) {
        try {
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getMilkTypeFromCenter(String centerId) {
        String milkType = "COW";
        String query = "Select " + KEY_CHILLING_MILK_TYPE + " From " + TABLE_CHILLING_CENTER +
                " WHERE " + KEY_CHILLING_CENTER_ID + "='" + centerId + "'";
        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            milkType = cursor.getString(cursor.getColumnIndex(KEY_CHILLING_MILK_TYPE));

        }
        if (cursor != null)
            cursor.close();
        return milkType;
    }


    /**
     * To check if any center exist the entered id
     *
     * @param id
     * @return
     */
    public boolean isCenterExistWithId(String id) {
        boolean isCenterExist = false;
        String selectQuery = "SELECT " + KEY_CHILLING_CENTER_ID + " FROM " + TABLE_CHILLING_CENTER + " WHERE "
                + KEY_CHILLING_CENTER_ID + "='" + id + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            isCenterExist = true;
        }
        if (cursor != null)
            cursor.close();
        return isCenterExist;
    }

    /**
     * To check if any center exist the entered id
     *
     * @param id
     * @return
     */
    public boolean isCenterExistWithBarcode(String id) {
        boolean isCenterExist = false;
        String selectQuery = "SELECT " + KEY_CHILLING_BARCODE + " FROM " + TABLE_CHILLING_CENTER + " WHERE "
                + KEY_CHILLING_BARCODE + "='" + id + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            isCenterExist = true;
        }
        if (cursor != null)
            cursor.close();
        return isCenterExist;
    }


    public boolean isSampleExistWithBarcode(String id) {
        boolean isSampleExist = false;
        String selectQuery = "SELECT " + KEY_SAMPLE_BARCODE + " FROM " + TABLE_SAMPLE + " WHERE "
                + KEY_SAMPLE_BARCODE + "='" + id + "'";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            isSampleExist = true;
        }
        if (cursor != null)
            cursor.close();
        return isSampleExist;
    }

    //Sample ID only support integer value
    public boolean isSampleExistWithId(String id) {

        boolean isSampleExist = false;

        try {
            int intId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();

            return isSampleExist;
        }

        String selectQuery = "SELECT " + KEY_SAMPLE_ID +
                " FROM " + TABLE_SAMPLE + " WHERE "
                + " cast( " + KEY_SAMPLE_ID + " as INTEGER)" + "= " + Integer.valueOf(id);
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            isSampleExist = true;
        }
        if (cursor != null)
            cursor.close();
        return isSampleExist;
    }


    //Add user in transaction

    public void addCompleteUser(ArrayList<UserEntity> alluserEntity, String primaryOrSecondary) {
        SQLiteDatabase database;
        if (primaryOrSecondary.equalsIgnoreCase(isPrimary)) {
            database = sqliteDatabase;
        } else {
            database = dbSync.getSqliteDatabase();
        }

        try {
            database.beginTransaction();
            for (int i = 0; i < alluserEntity.size(); i++) {
                try {

                    getAndDeleteUserTrans(alluserEntity.get(i).userId, database);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            insertUserInTrans(alluserEntity, database);
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            SmartCCUtil.todoDbFailure(mContext, primaryOrSecondary);
        } finally {
            endTransaction(database);
        }


    }


    private void getAndDeleteUserTrans(String userId, SQLiteDatabase database) throws Exception

    {
        long colId = -1;
        SQLiteDatabase primaryDatabase = database;
        String selectQuery = null;

        selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE "
                + KEY_USER_USERID + "='" + userId + "'";

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                colId = cursor.getLong(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (colId != -1) {

            String strFilter = KEY_USER_COLUMNID + " = ?";
            dbSync.syncDeleteByDB(primaryDatabase, TABLE_USER,
                    strFilter, new String[]{String.valueOf(colId)});
            if (userId != null) {
                Util.displayErrorToast("User : " + userId + " has updated!", mContext);
            }
            if (cursor != null)
                cursor.close();

        }
    }


    private void insertUserInTrans(ArrayList<UserEntity> allUserEntity,
                                   SQLiteDatabase database) throws Exception {
        int id = 0;
        SQLiteDatabase primaryDatabase = database;
        ContentValues values = new ContentValues();
        DatabaseEntity dbe = new DatabaseEntity(mContext);
        for (id = 0; id < allUserEntity.size(); id++) {
            values = dbe.getUserContentValues(allUserEntity.get(id));
            dbSync.syncInsertExcelDatabase(primaryDatabase, TABLE_USER, values);
        }
    }


    public AverageReportDetail getAverageReportData(String type, String milkType) {
        DatabaseEntity dbe = new DatabaseEntity(mContext);
        AverageReportDetail ard = new AverageReportDetail();
        String query = dbe.getShiftQueryForAverageReport(type, milkType);
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        ard = dbe.getAverageReportDetails(cursor, null);
        int totalUnsent = getAllNWUnsentRecordsCount();
        ard.totalUnsent = totalUnsent;
        if (cursor != null)
            cursor.close();
        return ard;

    }

    public ArrayList<String> getAllMemberList(String collectionType) {
        DatabaseEntity dbe = new DatabaseEntity(mContext);
        AverageReportDetail ard = new AverageReportDetail();
        String query = dbe.getQueryToGetMemberList(collectionType);
        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        ArrayList<String> allMemberlist = new ArrayList<>();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                allMemberlist.add(cursor.getString(
                        cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_FARMERID)));
            }
            while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return allMemberlist;

    }

    public ReportEntity getReportEntFromId(String farmerid) {
        DatabaseEntity dbe = new DatabaseEntity(mContext);

        String query = dbe.getQueryForMemberReport(farmerid);
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        ReportEntity reportEnt = null;
        if (cursor != null && cursor.moveToFirst()) {
            reportEnt = SmartCCUtil.getReportFromCursor(cursor);
        }
        if (cursor != null)
            cursor.close();
        return reportEnt;


    }

    public long updateFarmerEntity(FarmerEntity farmEntity, int status) {
        DatabaseEntity dbe = new DatabaseEntity(mContext);
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        ContentValues values = dbe.getFarmerContentValue(farmEntity);
        values.put(KEY_FARMER_SENT, status);
        values.put(KEY_FARMER_ID, farmEntity.farmer_id);
        String strFilter = KEY_FARMER_ID + " = ?";

        long id = -1;
        try {
            id = dbSync.syncUpdate(primaryDatabase, TABLE_FARMER, values, strFilter,
                    new String[]{farmEntity.farmer_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public boolean checkForSupervisor(String query) {
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        boolean value;
        if (cursor != null && cursor.moveToFirst()) {
            value = true;
        } else {
            value = false;
        }
        if (cursor != null)
            cursor.close();
        return value;
    }

    public long insertConfigurationEntity(ArrayList<ConfigurationElement> configurationElement,
                                          String primaryOrsecondary) {
        DatabaseEntity dbe = new DatabaseEntity(mContext);
        SQLiteDatabase primaryDatabase = sqliteDatabase;

        if (primaryOrsecondary.equalsIgnoreCase(isPrimary)) {
            primaryDatabase = sqliteDatabase;
        } else {
            primaryDatabase = dbSync.getSqliteDatabase();
        }

        primaryDatabase.beginTransaction();
        try {
            for (int i = 0; i < configurationElement.size(); i++) {
                ContentValues values = dbe.getConfigurationContentValue(configurationElement.get(i));
                dbSync.syncInsert(primaryDatabase, DatabaseEntity.TABLE_CONFIGURATION, values);
            }
            primaryDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            primaryDatabase.endTransaction();
            SmartCCUtil.todoDbFailure(mContext, primaryOrsecondary);
        }
        return 0;
    }


    public void insertOrReplaceConfigurationEntity(ConfigurationElement cpl) {
        dbSync.insertOrIgnoreConfiguration(sqliteDatabase, cpl);
    }

    public void insertOrReplaceUserHistory(String userId, String role) {
        dbSync.insertOrReplaceUserHistory(sqliteDatabase, userId, role, System.currentTimeMillis());
    }


    public long updateConfigEntity(ConfigurationElement configPushList, int status) {
        DatabaseEntity dbe = new DatabaseEntity(mContext);
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Properties config = DefaultConfigurationHandler.getInstance().getConfig();
        ContentValues values = dbe.getConfigurationContentValue(configPushList);
        if ((configPushList.value != null) && (configPushList.lastValue != null)
                && (configPushList.value.equals(configPushList.lastValue))) {
            status = 1;
        }
        values.put(DatabaseEntity.CONFIGURATION_STATUS, status);
        values.put(DatabaseEntity.CONFIGURATION_KEY, configPushList.key);
        String strFilter = DatabaseEntity.CONFIGURATION_KEY + " = ?";
        long id = -1;
        try {
            id = dbSync.syncUpdateBoth(primaryDatabase, DatabaseEntity.TABLE_CONFIGURATION, values, strFilter,
                    new String[]{configPushList.key});
            config.put(configPushList.key, configPushList.value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public ArrayList<ConfigurationElement> getAllUnsentConfigurationEntity() {
        ArrayList<ConfigurationElement> allConfigPushList = new ArrayList<>();
        DatabaseEntity dbe = new DatabaseEntity(mContext);

        String selectQuery = DatabaseEntity.QUERY_UNSENT_CONFIGURATION;

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ConfigurationElement configurationElement = dbe.getConfigurationlistEntity(cursor);
                allConfigPushList.add(configurationElement);
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return allConfigPushList;
    }


    public void updateSupervisorToTankerSuper(String query) {
        sqliteDatabase.execSQL(query);
    }


    /**
     * Generic method to get String from any table for given value
     *
     * @param table
     * @param key
     * @param defaultValue
     * @return
     */
    public String genericGetStringFromDatabase(String table, String key, String defaultValue) {
        String retValue = defaultValue;
        Cursor cursor = null;
        String query = " Select " + DatabaseEntity.CONFIGURATION_VALUE + " from " + table
                + " Where " + DatabaseEntity.CONFIGURATION_KEY + " ='" + key + "'";
        try {
            cursor = sqliteDatabase.rawQuery(query, null);

            if (cursor != null && cursor.moveToFirst()) {
                retValue = cursor.getString(0);
            }

            if (retValue == null) {
                retValue = defaultValue;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null)
                cursor.close();
        }

        return retValue;

    }


    public TruckCCDatabase getTruckCCDatabase() {
        return new TruckCCDatabase(mContext, sqliteDatabase, dbSync);
    }

    public AgentDatabase getAgentDatabase() {
        return new AgentDatabase(mContext, sqliteDatabase, dbSync);
    }

    public TankerDatabase getTankerDatabase() {
        return new TankerDatabase(mContext, sqliteDatabase, dbSync);
    }

    public void createTheConfigTableIfNotThere() {
        sqliteDatabase.execSQL(DatabaseEntity.getCreateQueryForConfigurationTable());
    }

    public void createTheHistoryUserIfNotThere() {
        sqliteDatabase.execSQL(DatabaseEntity.getCreateQueryForHistoryUser());
    }


    //Method to get the all reports based on the query

    public int isConfigurationTableContainAnyValue() {
        String query = "Select count(*) from " + DatabaseEntity.TABLE_CONFIGURATION;
        int count = 0;

        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (cursor != null)
            cursor.close();
        return count;
    }


    public int getAllUpdatedUnsentRecordsCount() {

        int count = 0;
        // Retrieve number of records which have not been sent to network yet
        String selectQuery = "SELECT  COUNT(*) FROM " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT + " WHERE " +
                EditRecordCollectionTable.KEY_REPORT_SENT_STATUS + "=" +
                String.valueOf(COL_REC_NW_UNSENT);

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            //Log.d(TAG, "Getting Count from Unsent Records ---------- : " + cursor.getInt(0));
            count = cursor.getInt(0);
        }
        // return contact list
        if (cursor != null)
            cursor.close();
        return count;
    }

    public ArrayList<String> getUserHistoryList(String role) {
        ArrayList<String> allUsers = new ArrayList<>();
        String query;

        if (role.equalsIgnoreCase(UserRole.ALL)) {
            query = "Select " + DatabaseEntity.USER_ID + " from " +
                    DatabaseEntity.TABLE_USER_HISTORY +
                    " order by " + DatabaseEntity.USER_LOG_IN_TIME + " DESC";
        } else {
            query = "Select " + DatabaseEntity.USER_ID + " from " +
                    DatabaseEntity.TABLE_USER_HISTORY + " Where " +
                    DatabaseEntity.USER_ROLE + " ='" + role + "'" +
                    " order by " + DatabaseEntity.USER_LOG_IN_TIME + " DESC";
        }


        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                allUsers.add(cursor.getString(
                        cursor.getColumnIndex(DatabaseEntity.USER_ID)));
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return allUsers;
    }

    private boolean toCheckTheColumnInTable(SQLiteDatabase database,
                                            String table, String column) {
        boolean result = false;
        String query = "Pragma " + "table_info " + "('" + table + "')";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndexOrThrow("name"))
                        .equalsIgnoreCase(column)) {
                    result = true;
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return result;
    }


    //Method to get the all reports based on the query

    public ArrayList<ReportEntity> getReportFromQuery(String query) {
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        ArrayList<ReportEntity> allReportEnt = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                ReportEntity repent = SmartCCUtil.getReportFromCursor(cursor);
                allReportEnt.add(repent);
            }
            while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();
        return allReportEnt;
    }


    public ArrayList<ReportEntity> getShiftSplitReportFromQuery(String query) {
        ArrayList<ReportEntity> allReportEnt = null;
        Cursor cursor = null;
        try {
            cursor = sqliteDatabase.rawQuery(query, null);
            allReportEnt = new ArrayList<>();

            AgentDatabase agentDatabase = getAgentDatabase();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    ReportEntity repent = Util.getReportEntityFromSplitRecord(agentDatabase.getSplitCollectionEntityFromCursor(cursor));
                    allReportEnt.add(repent);
                }
                while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null)
                cursor.close();

        }
        return allReportEnt;

    }


    public ArrayList<String> getCenterListFromQuery(String query) {
        ArrayList<String> allCenterList = new ArrayList<>();
        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                allCenterList.add(cursor.getString(cursor.getColumnIndex(KEY_CHILLING_CENTER_ID)));
            } while (cursor.moveToNext());

        }
        if (cursor != null)
            cursor.close();
        return allCenterList;

    }

    public boolean validateData(String table, String key, String queryData, int len) {
        String query = "Select count(*) from (select * from " + table +
                " where " + key + " in (" + queryData + ")" + ")";

        boolean isValidate = true;
        int size = 0;
        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            size = cursor.getInt(0);
        }

        if (size != 0 && size < len) {
            isValidate = false;
        }
        if (cursor != null)
            cursor.close();

        return isValidate;
    }

    public boolean validateDataByQuery(String query) {
        boolean isValidate = false;
        int size = 0;
        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            size = cursor.getInt(0);
        }

        if (size > 0) {
            isValidate = true;
        }
        if (cursor != null)
            cursor.close();
        return isValidate;
    }


    public ReportEntity getReportEntity(String query) {
        ReportEntity repEnt = null;
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            repEnt = SmartCCUtil.getReportFromCursor(cursor);
        }
        if (cursor != null) {
            cursor.close();
        }
        return repEnt;
    }


    /**
     * To get the last collection date
     *
     * @return
     */

    public long toGetLastCollectionTime() {
        String query = "Select MAX(" + FarmerCollectionTable.KEY_REPORT_TIME_MILLI + ") FROM "
                + TABLE_REPORTS + " Where " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + 1;
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        long milliTime = 0;
        if (cursor != null && cursor.moveToFirst() && cursor.getString(0) != null) {
            try {
                milliTime = cursor.getLong(0);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return milliTime;
    }

    public long toGetTabRegisteredDate() {
        String query = "Select " + KEY_LICENSE_REQUEST_DATE + " FROM "
                + TABLE_LICENSE;
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        long milliTime = 0;
        if (cursor != null && cursor.moveToFirst()) {
            try {
                milliTime = Long.parseLong(cursor.getString(0));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return milliTime;

    }


    private long getLastUnCommitedCollectionId() {
        long colId = -1;
        String selectQuery = "SELECT  * FROM " + TABLE_REPORTS + " WHERE " + FarmerCollectionTable.KEY_REPORT_FAT + "!=" + "'" + "NA" + "'" + " AND "
                + FarmerCollectionTable.POST_DATE + "='" + new SmartCCUtil(mContext).getReportFormatDate() + "'" + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "=" + 0 + " AND "
                + FarmerCollectionTable.POST_SHIFT + "='" + Util.getCurrentShift() + "'" + " order by " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI + " DESC";

        Cursor cursor = sqliteDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            colId = cursor.getLong(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_COLUMNID));
        }
        if (cursor != null) {
            cursor.close();
        }
        return colId;
    }

    public long deleteFromDb() {
        long id = getLastUnCommitedCollectionId();

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        String strFilter = FarmerCollectionTable.KEY_REPORT_COLUMNID + " = ?";

        try {
            dbSync.syncDelete(primaryDatabase, TABLE_REPORTS, strFilter,
                    new String[]{String.valueOf(id)});
            primaryDatabase = dbSync.getSqliteDatabase();
            dbSync.syncDelete(primaryDatabase, TABLE_REPORTS, strFilter,
                    new String[]{String.valueOf(id)});

        } catch (Exception e) {
            e.printStackTrace();
            if (primaryDatabase == sqliteDatabase) {
                Util.restartTab(mContext);
            }
        }

        return id;

    }


    public long getCollIdSequenceNumber(long seqNum) {
        long collId = 0;
        SQLiteDatabase primaryDatabase = this.getReadableDatabase();
        Cursor cursor;
        String selectQueryOfStatusTable = "SELECT  * FROM " + FarmerCollectionTable.TABLE_REPORTS + " WHERE "
                + FarmerCollectionTable.SEQUENCE_NUMBER + "= " + seqNum;
        cursor = primaryDatabase.rawQuery(selectQueryOfStatusTable, null);
        if (cursor != null && cursor.moveToFirst()) {
            collId = cursor.getLong(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_COLUMNID));
        }
        if (cursor != null) {
            cursor.close();
        }
        return collId;
    }


    public long addSequenceNumAndRefNumber(SQLiteDatabase database, long index, String type) {
        long newRowId = 0;
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        Cursor cursor = null;
        try {
            ContentValues values = new ContentValues();
            int seqNum = 0;
            String selectSeqNum = "SELECT " + KEY_COLL_REC_SEQNUM + " FROM "
                    + TABLE_COLL_REC_SEQ_NUM;
            cursor = database.rawQuery(selectSeqNum, null);
            if (cursor.moveToFirst()) {
                seqNum = cursor.getInt(0);
            }
            seqNum = increment(seqNum);
            values.put(KEY_COL_REC_SEND_STATUS, COL_REC_NW_UNSENT);
            if (amcuConfig.getAllowSMS()) {
                values.put(KEY_COL_REC_FARMER_SMS_STATUS, COL_REC_SMS_UNSENT);
            } else {
                values.put(KEY_COL_REC_FARMER_SMS_STATUS, COL_REC_SMS_NOT_ENABLE);
            }
            values.put(KEY_COL_REC_REFERENCE_IDX, index);
            values.put(KEY_COL_REC_REPORT_SEQNUM, seqNum);
            values.put(KEY_COL_REC_SHIFT_DATA_COLL_TYPE, type);

            newRowId = dbSync.syncInsertTransaction(database, TABLE_COL_REC_STATUS, values);
            ContentValues seqVal = new ContentValues();
            seqVal.put(KEY_COLL_REC_SEQNUM, seqNum);
            dbSync.syncUpdateTransaction(database, TABLE_COLL_REC_SEQ_NUM, seqVal, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            // DatabaseHandler.testWirteToFile(e.getMessage(),mContext);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return newRowId;
    }


    public boolean checkIdAggerateFarmerOrNot(String agentId) {
        String selectQuery = "SELECT " + KEY_FARMER_ID + " FROM " + TABLE_FARMER + " WHERE "
                + KEY_FARMER_ID + "='" + agentId + "'" + " AND " + KEY_FARMER_TYPE + "='" + AppConstants.FARMER_TYPE_AGENT + "'";
        Cursor cursor = sqliteDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            if (cursor != null) {
                cursor.close();
            }
            return false;
        }


    }


    public ArrayList<ReportEntity> getALLAggreateFarmerIDCursor(String query) {
        ArrayList<ReportEntity> allReportEntity = new ArrayList<>();
        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ReportEntity reportEntity = SmartCCUtil.getAggreateFarmerIDCursor(cursor);
                allReportEntity.add(reportEntity);

            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }

        return allReportEntity;
    }


    public String getFarmerType(String farmerId) {
        String returnType = AppConstants.FARMER_TYPE_FARMER;
        String query = "Select " + KEY_FARMER_TYPE + " From " + TABLE_FARMER
                + " where " + KEY_FARMER_ID + " ='" + farmerId + "' LIMIT 1";
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            returnType = cursor.getString(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        return returnType;
    }

    private void alterConfigurationTableWithSocietyId(SQLiteDatabase primaryDatabase) {
        String TEMP_TABLE = "temp_configuration_table";
        // primaryDatabase.beginTransaction();
        String renameQuery = " ALTER TABLE " + DatabaseEntity.TABLE_CONFIGURATION + " RENAME TO " + TEMP_TABLE;
        primaryDatabase.execSQL(renameQuery);
        primaryDatabase.execSQL(DatabaseEntity.getCreateQueryForConfigurationTable());
        String copyQuery = " INSERT INTO " + DatabaseEntity.TABLE_CONFIGURATION + " SELECT " +
                DatabaseEntity.CONFIGURATION_ID + " , " +
                DatabaseEntity.CONFIGURATION_KEY + " , " +
                DatabaseEntity.CONFIGURATION_VALUE + " ," +
                DatabaseEntity.CONFIGURATION_LAST_VALUE + " ," +
                DatabaseEntity.CONFIGURATION_MODIFIED_TIME + " ," +
                DatabaseEntity.CONFIGURATION_STATUS + " ," +
                DatabaseEntity.CONFIGURATION_USER_NAME + " FROM " + TEMP_TABLE;
        primaryDatabase.execSQL(copyQuery);
        primaryDatabase.execSQL("DROP TABLE " + TEMP_TABLE);
//        primaryDatabase.setTransactionSuccessful();
//        primaryDatabase.endTransaction();
    }

    private int checkIfTableHaveValue(String tableName, SQLiteDatabase primaryDatabase) {
        int count = -1;
        Cursor cursor = primaryDatabase.rawQuery("Select count(*) from " + tableName, null);

        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        return count;
    }


    public String getShiftAgentDetails() {
        String returnValue = "";
        ValidationHelper validationHelper = new ValidationHelper();

        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String date = smartCCUtil.getReportFormatDate();
        String shift = Util.getCurrentShift();
        String query = " Select * from " + TABLE_REPORTS + " inner join " + TABLE_FARMER + " on "
                + TABLE_FARMER + "." + KEY_FARMER_ID + " = " + TABLE_REPORTS + "." + KEY_FARMER_ID + " where " + TABLE_FARMER + "." + KEY_FARMER_TYPE + " = " + "'Aggregate Farmer'"
                + " and " + TABLE_REPORTS + "." + FarmerCollectionTable.POST_SHIFT + " = '" + shift + "'" + " and "
                + TABLE_REPORTS + "." + FarmerCollectionTable.POST_DATE + " = '" + date + "'";
        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        double totalQty = 0;

        if (cursor != null && cursor.moveToFirst()) {
            returnValue = cursor.getCount() + AppConstants.DB_SEPERATOR;
            do {

                totalQty = totalQty + validationHelper.getDoubleFromString(
                        cursor.getString(
                                cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_QUANTITY)), 0);
            } while (cursor.moveToNext());

            returnValue = returnValue + totalQty + AppConstants.DB_SEPERATOR;
        }
        if (cursor != null) {
            cursor.close();
        }


        return returnValue;
    }


    public String getShiftSplitFamrerDetails() {
        String returnValue = "";
        ValidationHelper validationHelper = new ValidationHelper();

        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String date = smartCCUtil.getReportFormatDate();
        String shift = Util.getCurrentShift();

        if (shift != null && shift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {
            shift = "MORNING";
        } else {
            shift = "EVENING";

        }

        String query = "Select * from " + SplitRecordTable.TABLE + " Where " + FarmerCollectionTable.KEY_REPORT_TYPE
                + " ='" + Util.REPORT_TYPE_AGENT_SPLIT + "'" + " And " + SplitRecordTable.POST_DATE + " ='" + date + "'"
                + " AND " + SplitRecordTable.POST_SHIFT + " ='" + shift + "'";

        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        double totalQty = 0;

        if (cursor != null && cursor.moveToFirst()) {
            returnValue = cursor.getCount() + AppConstants.DB_SEPERATOR;
            do {

                totalQty = totalQty + validationHelper.getDoubleFromString(
                        cursor.getString(
                                cursor.getColumnIndex(DatabaseEntity.QuantityParams.WEIGHING_QUANTITY)), 0);
            } while (cursor.moveToNext());

            returnValue = returnValue + totalQty + AppConstants.DB_SEPERATOR;
        }
        if (cursor != null) {
            cursor.close();
        }


        return returnValue;
    }


    /**
     * @param query
     * @return
     */

    public ArrayList<ReportEntity> getAllReportEntityFromQuery(String query) {
        ArrayList<ReportEntity> allReportEntity = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        ReportEntity reportEntity;

        if (cursor != null && cursor.moveToFirst()) {
            do {
                reportEntity = SmartCCUtil.getReportFromCursor(cursor);
                allReportEntity.add(reportEntity);
            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        return allReportEntity;
    }

    /**
     * set and retrive the can database
     *
     * @return
     */
    public AddtionalDatabase getAdditionallDatabase() {
        return new AddtionalDatabase(mContext, sqliteDatabase, dbSync);
    }


    public String getSocietyID() {
        // Select All Query
        String socityId = null;
        String selectQuery = null;

        selectQuery = "SELECT " + KEY_SOCIETY_CODE + " FROM " + TABLE_SOCIETY;

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            socityId = cursor.getString(0);
        }

        if (socityId == null) {
            socityId = "societyID";
        }

        if (cursor != null)
            cursor.close();

        return socityId;
    }

    public String[] getSocietyIdAndName() {
        // Select All Query
        String values[] = {"", ""};
        String selectQuery = "SELECT " + KEY_SOCIETY_CODE + "," + KEY_SOCIETY_NAME + " FROM " + TABLE_SOCIETY;

        SQLiteDatabase primaryDatabase = sqliteDatabase;

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            values[0] = cursor.getString(0);
            values[1] = cursor.getString(1);
        }

        if (cursor != null)
            cursor.close();

        return values;
    }

    public String getTotalSplitFarmerUnSentRecords() {
        String returnValue = "";
        // FIXME: confirm table name
        String query = "Select * from " + SplitRecordTable.TABLE + " Where " +
                SplitRecordTable.SENT
                + " ='" + Util.REPORT_NOT_COMMITED + "'";

        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null) {
            returnValue = String.valueOf(cursor.getCount());

        }
        if (cursor != null) {
            cursor.close();
        }

        return returnValue;
    }

    private// Adding new Bonus Ratechart from excel
    void insertBonusRateChartFromExcel(ArrayList<BonusChartEntity> allBonusRateChartEnt,
                                       SQLiteDatabase database) throws Exception {
        int rateId = 0;
        SQLiteDatabase primaryDatabase = database;
        ContentValues values = new ContentValues();
        for (rateId = 0; rateId < allBonusRateChartEnt.size(); rateId++) {
            values.put(DatabaseEntity.InCentive.VALUE,
                    allBonusRateChartEnt.get(rateId).point);
            values.put(DatabaseEntity.InCentive.BONUS, allBonusRateChartEnt.get(rateId).bonusRate);
            values.put(DatabaseEntity.InCentive.START_DATE, allBonusRateChartEnt.get(rateId).startDate);
            values.put(DatabaseEntity.InCentive.END_DATE, allBonusRateChartEnt.get(rateId).endDate);
            values.put(DatabaseEntity.InCentive.SOCIETY_ID, allBonusRateChartEnt.get(rateId).societyId);
            values.put(DatabaseEntity.InCentive.MILK_TYPE, allBonusRateChartEnt.get(rateId).milkType);
            values.put(DatabaseEntity.InCentive.RATE_CHART_NAME,
                    allBonusRateChartEnt.get(rateId).name);
            values.put(DatabaseEntity.InCentive.RATE_CHART_TYPE,
                    allBonusRateChartEnt.get(rateId).rateChartType);
            dbSync.syncInsertExcelDatabase(primaryDatabase, TABLE_INCENTIVE_RATECHART, values);
        }

    }

    public String getIncentiveRate(String protein, String rateChartName) {
        String rate = "0.00";
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        if (!protein.contains(".")) {
            protein = protein + ".0";
        }
        String selectQuery = "SELECT " + DatabaseEntity.InCentive.BONUS + " FROM " + TABLE_INCENTIVE_RATECHART + " WHERE "
                + DatabaseEntity.InCentive.VALUE + "=" + protein
                + " AND " + DatabaseEntity.InCentive.RATE_CHART_NAME + "='" + rateChartName + "'"
                + " AND " + DatabaseEntity.InCentive.MILK_TYPE + "='" + session.getMilkType() + "'";
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                rate = cursor.getString(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cursor != null) {
            cursor.close();
        }
        return rate;
    }

    private String getCreateQueryforDeviceInfo() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_DEVICE_INFO + "("
                + KEY_DEVICE_CURRENT_TIME + " INTEGER PRIMARY KEY, "
                + KEY_DEVICE_BATTERY_PERCENTAGE + " DOUBLE,"
                + KEY_DEVICE_BATTERT_CHARGING + " TEXT,"
                + KEY_DEVICE_CHARGING_TYPE + " TEXT,"
                + KEY_DEVICE_BATTERY_TEMP + " DOUBLE,"
                + KEY_DEVICE_BATTERY_HEALTH + " TEXT,"
                + KEY_DEVICE_BATTERY_VOLTAGE + " INTEGER,"
                + KEY_DEVICE_NETWORK_ASU + " INTEGER,"
                + KEY_DEVICE_NETWORK_OPERATOR + " TEXT,"
                + KEY_DEVICE_DATA_STATUS + " TEXT,"
                + KEY_DEVICE_NETWORK_TYPE + " TEXT,"
                + KEY_DEVICE_SIM_SERIAL_NUM + " TEXT,"
                + KEY_DEVICE_LAC + " INTEGER,"
                + KEY_DEVICE_CELL_ID + " INTEGER,"
                + KEY_DEVICE_SENT_STATUS + " INTEGER,"
                + KEY_DEVICE_BATTERY_AMBIENT_TEMP + " DOUBLE" + ")";

    }

    public long insertOrUpdateDeviceStatus(DeviceStatusEntity entity) {
        String selectQuery = "SELECT * FROM " + TABLE_DEVICE_INFO + " WHERE " + KEY_DEVICE_CURRENT_TIME
                + " = " + entity.time;
        Cursor cursor = sqliteDatabase.rawQuery(selectQuery, null);
        long id = -1;
        if (cursor != null && cursor.moveToFirst()) {
            id = cursor.getLong(cursor.getColumnIndex(KEY_DEVICE_CURRENT_TIME));
        }
        ContentValues contentValues = new ContentValues();

        contentValues.put(KEY_DEVICE_BATTERY_PERCENTAGE, entity.batteryPercent);
        contentValues.put(KEY_DEVICE_BATTERT_CHARGING, String.valueOf(entity.charging));
        contentValues.put(KEY_DEVICE_CHARGING_TYPE, entity.chargingType);
        contentValues.put(KEY_DEVICE_BATTERY_TEMP, entity.batteryTemperature);
        contentValues.put(KEY_DEVICE_BATTERY_HEALTH, entity.batteryHealth);
        contentValues.put(KEY_DEVICE_BATTERY_VOLTAGE, entity.batteryVoltage);
        contentValues.put(KEY_DEVICE_NETWORK_ASU, entity.networkAsu);
        contentValues.put(KEY_DEVICE_NETWORK_OPERATOR, entity.networkOperator);
        contentValues.put(KEY_DEVICE_DATA_STATUS, entity.dataStatus);
        contentValues.put(KEY_DEVICE_NETWORK_TYPE, entity.networkType);
        contentValues.put(KEY_DEVICE_SIM_SERIAL_NUM, entity.simSerialNumber);
        contentValues.put(KEY_DEVICE_SENT_STATUS, entity.sent);
        contentValues.put(KEY_DEVICE_BATTERY_AMBIENT_TEMP, entity.ambientTemperature);
        contentValues.put(KEY_DEVICE_LAC, entity.lac);
        contentValues.put(KEY_DEVICE_CELL_ID, entity.cellId);

        try {
            if (id == -1) {
                contentValues.put(KEY_DEVICE_CURRENT_TIME, entity.time);
                dbSync.syncInsert(sqliteDatabase, TABLE_DEVICE_INFO, contentValues);
            } else {
                dbSync.syncUpdate(sqliteDatabase, TABLE_DEVICE_INFO, contentValues, KEY_DEVICE_CURRENT_TIME + " = ?", new String[]{String.valueOf(entity.time)});
            }
        } catch (Exception e) {
            e.printStackTrace();
            restartApplication();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return id;
    }

    public ArrayList<DeviceStatusEntity> getAllUnsentDeviceStatus() {
        ArrayList<DeviceStatusEntity> dsl = null;
        String query = "SELECT * FROM " + TABLE_DEVICE_INFO + " WHERE " + KEY_DEVICE_SENT_STATUS + " = " + COL_REC_NW_UNSENT;
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            dsl = new ArrayList<>();
            do {
                DeviceStatusEntity entity = new DeviceStatusEntity();
                entity.time = cursor.getLong(cursor.getColumnIndex(KEY_DEVICE_CURRENT_TIME));
                entity.sent = cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_SENT_STATUS));
                entity.ambientTemperature = cursor.getFloat(cursor.getColumnIndex(KEY_DEVICE_BATTERY_AMBIENT_TEMP));
                entity.batteryHealth = cursor.getString(cursor.getColumnIndex(KEY_DEVICE_BATTERY_HEALTH));
                entity.batteryPercent = cursor.getFloat(cursor.getColumnIndex(KEY_DEVICE_BATTERY_PERCENTAGE));
                entity.batteryVoltage = cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_BATTERY_VOLTAGE));
                entity.batteryTemperature = cursor.getFloat(cursor.getColumnIndex(KEY_DEVICE_BATTERY_TEMP));
                entity.chargingType = cursor.getString(cursor.getColumnIndex(KEY_DEVICE_CHARGING_TYPE));
                entity.charging = Boolean.valueOf(cursor.getString(cursor.getColumnIndex(KEY_DEVICE_BATTERT_CHARGING)));
                entity.networkAsu = cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_NETWORK_ASU));
                entity.networkOperator = cursor.getString(cursor.getColumnIndex(KEY_DEVICE_NETWORK_OPERATOR));
                entity.dataStatus = cursor.getString(cursor.getColumnIndex(KEY_DEVICE_DATA_STATUS));
                entity.networkType = cursor.getString(cursor.getColumnIndex(KEY_DEVICE_NETWORK_TYPE));
                entity.simSerialNumber = cursor.getString(cursor.getColumnIndex(KEY_DEVICE_SIM_SERIAL_NUM));
                entity.lac = cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_LAC));
                entity.cellId = cursor.getInt(cursor.getColumnIndex(KEY_DEVICE_CELL_ID));
                dsl.add(entity);
            } while (cursor.moveToNext());

        }
        if (cursor != null) {
            cursor.close();
        }
        return dsl;
    }

    /**
     * @param dse
     */
    public void deleteTheDeviceInfoTable(DeviceStatusEntity dse) {
        String strFilter = KEY_DEVICE_CURRENT_TIME + " = ?";
        try {
            dbSync.syncDeleteBoth(sqliteDatabase, TABLE_DEVICE_INFO,
                    strFilter, new String[]{String.valueOf(dse.time)});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void purgeDeviceInfoPiorTheTime(long time, String primaryOrsecondary) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        if (primaryOrsecondary.equalsIgnoreCase(isPrimary)) {
            primaryDatabase = sqliteDatabase;
        } else {
            primaryDatabase = dbSync.getSqliteDatabase();
        }
        String deleteQuery = "DELETE FROM " + TABLE_DEVICE_INFO +
                " WHERE " + KEY_DEVICE_CURRENT_TIME + " < " + time;
        primaryDatabase.execSQL(deleteQuery);
    }

    public void purgeCleaningDataPriorTheTime(long time, String primaryOrsecondary) {

        SQLiteDatabase primaryDatabase = sqliteDatabase;
        if (primaryOrsecondary.equalsIgnoreCase(isPrimary)) {
            primaryDatabase = sqliteDatabase;
        } else {
            primaryDatabase = dbSync.getSqliteDatabase();
        }
        String deleteQuery = "DELETE FROM " + DatabaseEntity.TABLE_ESSAE_DATA +
                " WHERE " + DatabaseEntity.ESSAE_QUERIED_TIME + " < " + time;
        primaryDatabase.execSQL(deleteQuery);
    }

    public boolean isFarmerCollectionDone(String farmerID, String agentID, String shift, String milkType, String date) {
        SQLiteDatabase primaryDatabase = sqliteDatabase;
        boolean isValidFarmerId;
        //FIXME confirm table name
        String selecetQuery = "SELECT * FROM " + SplitRecordTable.TABLE + " Where " + SplitRecordTable.FARMERID + " ='" + farmerID + "'" + " And " + SplitRecordTable.AGENT_ID + " ='" + agentID + "'"
                + " And " + SplitRecordTable.MILKTYPE + " ='" + milkType + "'"
                + " And " + SplitRecordTable.SHIFT + " ='" + shift + "'"
                + " And " + SplitRecordTable.DATE + " ='" + date + "'";

        Cursor cursor = primaryDatabase.rawQuery(selecetQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            isValidFarmerId = true;
        } else {
            isValidFarmerId = false;
        }

        if (cursor != null)
            cursor.close();
        return isValidFarmerId;
    }

    //For consolidated API Upgrade

    public ArrayList<DateShiftEntry> getDayAndEntryList(String query) {
        ArrayList<DateShiftEntry> allDateShiftEntry = new ArrayList<>();
        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {
            do {
                DateShiftEntry dateShiftEntry = new DateShiftEntry();
                dateShiftEntry.setDate(
                        cursor.getString(cursor.getColumnIndex(
                                FarmerCollectionTable.POST_DATE)));

                dateShiftEntry.setShift(cursor.getString(
                        cursor.getColumnIndex(FarmerCollectionTable.POST_SHIFT)));

                allDateShiftEntry.add(dateShiftEntry);

            } while (cursor.moveToNext());
        }
        if (cursor != null) {
            cursor.close();
        }

        return allDateShiftEntry;
    }


    public int getUnsentCount(String query) {
        int count = 0;

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {

            count = cursor.getInt(0);

        }
        if (cursor != null) {
            cursor.close();
        }

        return count;
    }

    private void insertRateChartDetails(RatechartDetailsEnt rateChartDetEnt,
                                        SQLiteDatabase database)
            throws Exception {

        String name = null;
        SQLiteDatabase primaryDatabase = database;
        String selectQuery = "SELECT  * FROM " + RateChartNameTable.TABLE_NAME
                + " WHERE " + RateChartNameTable.NAME + "='"
                + rateChartDetEnt.rateChartName + "'";

        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(1);
        }

        ContentValues values = new ContentValues();

        values.put(RateChartNameTable.MILKTYPE, rateChartDetEnt.rateMilkType);
        values.put(RateChartNameTable.OTHER1, rateChartDetEnt.rateOther1);
        values.put(RateChartNameTable.OTHER2, rateChartDetEnt.rateOther2);
        values.put(RateChartNameTable.VALIDFROM, rateChartDetEnt.rateValidityFrom);
        values.put(RateChartNameTable.VALIDTO, rateChartDetEnt.rateValidityTo);
        values.put(RateChartNameTable.SOCID, rateChartDetEnt.rateSocId);
        values.put(RateChartNameTable.LVALIDITYFROM,
                rateChartDetEnt.rateLvalidityFrom);
        values.put(RateChartNameTable.LVALIDITYTO, rateChartDetEnt.rateLvalidityTo);
        values.put(RateChartNameTable.ISACTIVE, rateChartDetEnt.isActive);
        values.put(RateChartNameTable.TYPE, rateChartDetEnt.ratechartType);

        if (name != null
                && name.equalsIgnoreCase(rateChartDetEnt.rateChartName)) {
            String strFilter = RateChartNameTable.NAME + " = ?";
            dbSync.syncUpdateDB(primaryDatabase, RateChartNameTable.TABLE_NAME, values, strFilter,
                    new String[]{rateChartDetEnt.rateChartName});
        } else {
            values.put(RateChartNameTable.NAME, rateChartDetEnt.rateChartName);
            dbSync.syncInsertCollection(primaryDatabase, RateChartNameTable.TABLE_NAME, values);
        }
        if (cursor != null)
            cursor.close();
    }


    public ArrayList<String> getAllSampleIds() {
        ArrayList<String> allSampleIds = new ArrayList<>();

        String query = "Select " + SampleRecordTable.KEY_SAMPLE_ID + " FROM "
                + SampleRecordTable.TABLE_SAMPLE;

        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {

                allSampleIds.add(cursor.getString(0));

            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();

        return allSampleIds;
    }


}
