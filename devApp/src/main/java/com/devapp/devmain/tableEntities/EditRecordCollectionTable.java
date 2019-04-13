package com.devapp.devmain.tableEntities;


/**
 * Created by u_pendra on 6/1/18.
 */

public interface EditRecordCollectionTable {


    String TABLE_EXTENDED_REPORT = "extendedReport";
    String TABLE_TEMP_EXTENDED_REPORT = "tempExtendedReport";

    String KEY_REPORT_COLUMNID = "_id";
    String KEY_REPORT_FARMERID = "farmerId";
    String KEY_REPORT_NAME = "name";
    String KEY_REPORT_SNF = "snf";
    String KEY_REPORT_FAT = "fat";
    String KEY_REPORT_RATE = "rate";
    String KEY_REPORT_USER = "user";

    String KEY_REPORT_MANUAL = "manual";

    String KEY_REPORT_AMOUNT = "amount";

    String KEY_REPORT_QUANTITY = "quantity";
    String KEY_REPORT_TAXNUM = "txnNumber";
    String KEY_REPORT_MILKTYPE = "milkType";
    String KEY_REPORT_LDATE = "lDAte";
    String KEY_REPORT_SOCIETYID = "socID";
    String KEY_REPORT_BONUS = "bonus";
    String KEY_REPORT_TIME = "time";
    String KEY_REPORT_TIME_MILLI = "timeMillis";
    String KEY_REPORT_MODE = "repMode";
    String KEY_REPORT_STATUS = "repStatus";
    String KEY_REPORT_CLR = "repCLR";
    String KEY_REPORT_AWM = "repAWM";
    String KEY_REPORT_WEIGHTMANUAL = "repWeightManual";
    String KEY_REPORT_MILKOMANUAL = "repMilkoManual";
    String KEY_REPORT_MILKANALYSERTIME = "qualityTime";
    String KEY_REPORT_WEIGHINGTIME = "quantityTime";
    String KEY_REPORT_TEMP = "temperature";
    String KEY_REPORT_COMMITED = "recordCommited";
    String KEY_REPORT_TYPE = "reportType";
    String KEY_MILKQUALITY = "milkQuality";
    String KEY_REPORT_RATEMODE = "rateMode";
    String KEY_REPORT_NUMBER_OF_CANS = "numberOfCans";

    String KEY_REPORT_SEQUENCE_NUMBER = "repSequenceNumber";
    String KEY_REPORT_ROUTE = "reportRoute";
    String KEY_REPORT_RECORD_STATUS = "recordStatus";
    String KEY_REPORT_RATECHART_NAME = "rateChartName";
    String KEY_REPORT_FOREIGN_SEQUENCE_ID = "forSeqId";
    String KEY_REPORT_EDITED_TIME = "editedTime";
    String KEY_REPORT_OLD_OR_NEW_FLAG = "oldOrNew";
    String KEY_REPORT_KG_QTY = "kgQty";
    String KEY_REPORT_LTR_QTY = "ltrsQty";
    String KEY_TIPPING_START_TIME = "tippingStartTime";
    String KEY_TIPPING_END_TIME = "tippingEndTime";
    String KEY_REPORT_AGENT_ID = "repAgentId";
    String KEY_REPORT_MILKSTATUS_CODE = "milkStatusCode";
    String KEY_REPORT_RATE_CALC_DEVICE = "isRateChartCalculationFromDevice";
    String KEY_REPORT_MA_SERIAL = "maSerialNumber";
    String KEY_REPORT_MA_NAME = "maName";
//    String KEY_REPORT_LACTOSE = "repLactose";
//    String KEY_REPORT_PROTEIN = "repProtein";
//    String KEY_REPORT_PH = "repPH";
//    String KEY_REPORT_SALT = "repSalt";
//    String KEY_REPORT_FREEZING_POINT = "freezingPoint";
//    String KEY_REPORT_CONDUTIVITY = "repCondutivity";
//    String KEY_REPORT_CALIBRATION = "maCalibration";
//    String KEY_REPORT_DENSITY = "density";

    //   String KEY_REPORT_TOTAl_AMOUNT = "totalAmount";
    String KEY_REPORT_SENT_STATUS = "reportSentStatus";
    String SMS_SENT_STATUS = "smsSentStatus";
    String POST_DATE = "postDate";
    String POST_SHIFT = "postShift";
    String SEQUENCE_NUMBER = "seqNum";


    String OLD_KEY_TXN_NUMBER = "taxNumber";
    String OLD_KEY_BONUS = "repDay";
    String OLD_KEY_QUALITY_TIME = "repFurtherInfo1";
    String OLD_KEY_QUANTITY_TIME = "repFurtherInfo2";
    String OLD_KEY_TEMP = "repFurtherInfo3";


    String CREATE_EXTENDED_REPORT_TABLE = "CREATE TABLE " + TABLE_EXTENDED_REPORT + "("
            + KEY_REPORT_COLUMNID + " INTEGER PRIMARY KEY, "
            + KEY_REPORT_FARMERID + " TEXT," + KEY_REPORT_NAME + " TEXT,"
            + KEY_REPORT_SNF + " DOUBLE," + KEY_REPORT_FAT + " DOUBLE,"
            + KEY_REPORT_USER + " TEXT," + KEY_REPORT_MANUAL + " TEXT,"
            + KEY_REPORT_AMOUNT + " DOUBLE,"
            + KEY_REPORT_QUANTITY + " DOUBLE,"
            + KEY_REPORT_TAXNUM + " INTEGER," + KEY_REPORT_MILKTYPE + " TEXT,"
            + KEY_REPORT_RATE + " DOUBLE," + KEY_REPORT_LDATE + " INTEGER,"
            + KEY_REPORT_SOCIETYID + " TEXT," + KEY_REPORT_BONUS + " DOUBLE,"
            + KEY_REPORT_TIME + " TEXT," + KEY_REPORT_TIME_MILLI
            + " INTEGER," + KEY_REPORT_AWM + " DOUBLE," + KEY_REPORT_CLR
            + " DOUBLE," + KEY_REPORT_STATUS + " TEXT,"
            + KEY_REPORT_WEIGHTMANUAL + " TEXT," + KEY_REPORT_MILKOMANUAL
            + " TEXT," + KEY_REPORT_MILKANALYSERTIME + " INTEGER,"
            + KEY_REPORT_WEIGHINGTIME + " INTEGER," + KEY_REPORT_TEMP
            + " DOUBLE," + KEY_REPORT_COMMITED + " INTEGER," + KEY_REPORT_TYPE + " TEXT,"
            + KEY_MILKQUALITY + " TEXT," + KEY_REPORT_RATEMODE + " TEXT," + KEY_REPORT_NUMBER_OF_CANS + " INTEGER,"
            + KEY_REPORT_SEQUENCE_NUMBER + " INTEGER," + KEY_REPORT_ROUTE + " TEXT," + KEY_REPORT_RECORD_STATUS + " TEXT," +
            KEY_REPORT_FOREIGN_SEQUENCE_ID + " INTEGER," + KEY_REPORT_EDITED_TIME + " INTEGER," +
            KEY_REPORT_OLD_OR_NEW_FLAG +
            " TEXT,"
            + KEY_TIPPING_START_TIME
            + " INTEGER, " + KEY_TIPPING_END_TIME + " INTEGER, " + KEY_REPORT_AGENT_ID
            + " TEXT, " + KEY_REPORT_MILKSTATUS_CODE + " INTEGER, " + KEY_REPORT_RATE_CALC_DEVICE
            + " INTEGER,"
            + KEY_REPORT_MA_SERIAL + " INTEGER,"
            + KEY_REPORT_MA_NAME + " TEXT,"
            + KEY_REPORT_KG_QTY + " DOUBLE," + KEY_REPORT_LTR_QTY + " DOUBLE,"
            + KEY_REPORT_SENT_STATUS + " INTEGER, "
            + SMS_SENT_STATUS + " INTEGER, "
            + POST_DATE + " VARCHAR(20), "
            + POST_SHIFT + " VARCHAR(20), "
            + SEQUENCE_NUMBER + " INTEGER "
            + ")";

    String CREATE_TEMP_EXTENDED_REPORT_TABLE = "CREATE TABLE " + TABLE_TEMP_EXTENDED_REPORT + "("
            + KEY_REPORT_COLUMNID + " INTEGER PRIMARY KEY, "
            + KEY_REPORT_FARMERID + " TEXT," + KEY_REPORT_NAME + " TEXT,"
            + KEY_REPORT_SNF + " TEXT," + KEY_REPORT_FAT + " TEXT,"
            + KEY_REPORT_USER + " TEXT," + KEY_REPORT_MANUAL + " TEXT,"
            + FarmerCollectionTable.POST_SHIFT + " TEXT," + KEY_REPORT_AMOUNT + " TEXT,"
            + KEY_REPORT_QUANTITY + " TEXT," + FarmerCollectionTable.POST_DATE + " TEXT,"
            + KEY_REPORT_TAXNUM + " TEXT," + KEY_REPORT_MILKTYPE + " TEXT,"
            + KEY_REPORT_RATE + " TEXT," + KEY_REPORT_LDATE + " INTEGER,"
            + KEY_REPORT_SOCIETYID + " TEXT," + KEY_REPORT_BONUS + " TEXT,"
            + KEY_REPORT_TIME + " TEXT," + KEY_REPORT_TIME_MILLI
            + " INTEGER," + KEY_REPORT_AWM + " TEXT," + KEY_REPORT_CLR
            + " TEXT," + KEY_REPORT_STATUS + " TEXT,"
            + KEY_REPORT_WEIGHTMANUAL + " TEXT," + KEY_REPORT_MILKOMANUAL
            + " TEXT," + KEY_REPORT_MILKANALYSERTIME + " TEXT,"
            + KEY_REPORT_WEIGHINGTIME + " TEXT," + KEY_REPORT_TEMP
            + " TEXT," + KEY_REPORT_COMMITED + " INTEGER," + KEY_REPORT_TYPE + " TEXT,"
            + KEY_MILKQUALITY + " TEXT," + KEY_REPORT_RATEMODE + " TEXT," + KEY_REPORT_NUMBER_OF_CANS + " TEXT,"
            + KEY_REPORT_SEQUENCE_NUMBER + " INTEGER," + KEY_REPORT_ROUTE + " TEXT," + KEY_REPORT_RECORD_STATUS + " TEXT," +
            KEY_REPORT_FOREIGN_SEQUENCE_ID + " INTEGER," + KEY_REPORT_EDITED_TIME + " INTEGER," +
            KEY_REPORT_OLD_OR_NEW_FLAG +
            " TEXT,"
            + KEY_TIPPING_START_TIME
            + " INTEGER, " + KEY_TIPPING_END_TIME + " INTEGER, " + KEY_REPORT_AGENT_ID
            + " TEXT, " + KEY_REPORT_MILKSTATUS_CODE + " INTEGER, " + KEY_REPORT_RATE_CALC_DEVICE
            + " INTEGER,"
            + KEY_REPORT_MA_SERIAL + " INTEGER,"
            + KEY_REPORT_MA_NAME + " TEXT,"
            + KEY_REPORT_KG_QTY + " TEXT," + KEY_REPORT_LTR_QTY + " TEXT,"
            + KEY_REPORT_SENT_STATUS + " INTEGER, "
            + SMS_SENT_STATUS + " INTEGER, "
            + SEQUENCE_NUMBER + " INTEGER "
            + ")";
}
