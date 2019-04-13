package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 6/1/18.
 */

public interface SalesCollectionTable {


    String TABLE_SALES_REPORT = "salesReportTable";
    String TABLE_TEMP_SALES_REPORT = "tempSalesReportTable";


    String KEY_SALES_COLUMNID = "_id";
    String KEY_SALES_ID = "salesId";
    String KEY_SALES_NAME = "name";
    String KEY_SALES_SNF = "snf";

    String KEY_SALES_FAT = "fat";
    String KEY_SALES_RATE = "rate";
    String KEY_SALES_USER = "user";
    String KEY_SALES_MANUAL = "manual";

    String KEY_SALES_AMOUNT = "amount";
    String KEY_SALES_QUANTITY = "quantity";
    String KEY_SALES_TXNNUM = "txnNumber";
    String KEY_SALES_MILKTYPE = "milkType";
    String KEY_SALES_SOCIETYID = "socID";
    String KEY_SALES_TIME = "time";
    //   String KEY_SALES_MODE = "repMode";
    String KEY_SALES_STATUS = "repStatus";
    String KEY_SALES_CLR = "repCLR";
    String KEY_SALES_AWM = "repAWM";
    String KEY_SALES_WEIGHTMANUAL = "repWeightManual";
    String KEY_SALES_MILKOMANUAL = "repMilkoManual";
    String KEY_SALES_TEMP = "repFurtherInfo3";
    String KEY_SALES_TIME_MILLI = "timeMillis";
    String KEY_SALES_TXN_TYPE = "salesTxnType";
    String KEY_SALES_TXN_SUB_TYPE = "salesTxnSubType";
    String KEY_SALES_SEND_STATUS = "salesSentStatus";
    String SMS_SENT_STATUS = "smsSentStatus";
    String POST_DATE = "postDate";
    String POST_SHIFT = "postShift";
    String SEQUENCE_NUMBER = "seqNum";


    String OLD_TXN_NUMBER = "taxNumber";


    String CREATE_SALES_REPORT = "CREATE TABLE " + TABLE_SALES_REPORT + "("
            + KEY_SALES_COLUMNID + " INTEGER PRIMARY KEY, "
            + KEY_SALES_ID + " TEXT," + KEY_SALES_NAME + " TEXT,"
            + KEY_SALES_SNF + " DOUBLE," + KEY_SALES_FAT + " DOUBLE,"
            + KEY_SALES_USER + " TEXT," + KEY_SALES_MANUAL + " TEXT,"
            + KEY_SALES_AMOUNT + " DOUBLE," + KEY_SALES_QUANTITY + " DOUBLE,"
            + KEY_SALES_TXNNUM + " TEXT," + KEY_SALES_MILKTYPE + " TEXT,"
            + KEY_SALES_RATE + " DOUBLE," + KEY_SALES_SOCIETYID + " TEXT,"
            + KEY_SALES_TIME + " TEXT," + KEY_SALES_AWM + " DOUBLE,"
            + KEY_SALES_CLR + " DOUBLE," + KEY_SALES_STATUS + " TEXT,"
            + KEY_SALES_WEIGHTMANUAL + " TEXT," + KEY_SALES_MILKOMANUAL + " TEXT,"
            + KEY_SALES_TIME_MILLI + " INTEGER," + KEY_SALES_TEMP + " DOUBLE,"
            + KEY_SALES_TXN_TYPE + " VARCHAR(100), " + KEY_SALES_TXN_SUB_TYPE + " VARCHAR(100), "
            + KEY_SALES_SEND_STATUS + " INTEGER, " + SMS_SENT_STATUS + " INTEGER, "
            + POST_DATE + " VARCHAR(20), " + POST_SHIFT + " VARCHAR(20), "
            + SEQUENCE_NUMBER + " INTEGER "
            + ")";


    String CREATE_TEMP_SALES_REPORT = "CREATE TABLE " + TABLE_TEMP_SALES_REPORT + "("
            + KEY_SALES_COLUMNID + " INTEGER PRIMARY KEY, "
            + KEY_SALES_ID + " TEXT," + KEY_SALES_NAME + " TEXT,"
            + KEY_SALES_SNF + " TEXT," + KEY_SALES_FAT + " TEXT,"
            + KEY_SALES_USER + " TEXT," + KEY_SALES_MANUAL + " TEXT,"
            + KEY_SALES_AMOUNT + " TEXT," + KEY_SALES_QUANTITY + " TEXT,"
            + KEY_SALES_TXNNUM + " TEXT," + KEY_SALES_MILKTYPE + " TEXT,"
            + KEY_SALES_RATE + " TEXT," + KEY_SALES_SOCIETYID + " TEXT,"
            + KEY_SALES_TIME + " TEXT," + KEY_SALES_AWM + " TEXT,"
            + KEY_SALES_CLR + " TEXT," + KEY_SALES_STATUS + " TEXT,"
            + KEY_SALES_WEIGHTMANUAL + " TEXT," + KEY_SALES_MILKOMANUAL + " TEXT,"
            + KEY_SALES_TIME_MILLI + " INTEGER," + KEY_SALES_TEMP + " TEXT,"
            + KEY_SALES_TXN_TYPE + " VARCHAR(100), " + KEY_SALES_TXN_SUB_TYPE + " VARCHAR(100), "
            + KEY_SALES_SEND_STATUS + " INTEGER, " + SMS_SENT_STATUS + " INTEGER, "
            + POST_DATE + " VARCHAR(20), " + POST_SHIFT + " VARCHAR(20), "
            + SEQUENCE_NUMBER + " INTEGER "
            + ")";

}
