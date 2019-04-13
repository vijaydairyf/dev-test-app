package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 5/2/18.
 */

public interface CollectionRecordStatusTable {


    String TABLE_COL_REC_STATUS = "collRecordStatusTable";

    public static final String KEY_COL_REC_COLUMN_ID = "_id";
    public static final String KEY_COL_REC_REPORT_SEQNUM = "seqNum";
    public static final String KEY_COL_REC_REFERENCE_IDX = "reportsTableIdx";
    public static final String KEY_COL_REC_SEND_STATUS = "nwSendStatus";
    public static final String KEY_COL_REC_FARMER_SMS_STATUS = "farmerSMSStatus";
    public static final String KEY_COL_REC_SHIFT_DATA_IDX = "shiftDataIdx";
    public static final String KEY_COL_REC_SHIFT_DATA_COLL_TYPE = "collectionType";


    String createQuery =
            "CREATE TABLE " + TABLE_COL_REC_STATUS + "("
                    + KEY_COL_REC_COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + KEY_COL_REC_REFERENCE_IDX + " INTEGER NOT NULL, "
                    + KEY_COL_REC_REPORT_SEQNUM + " INTEGER, "        // Check if a range of 0-999 can be enabled for this
                    + KEY_COL_REC_SEND_STATUS + " INTEGER, "
                    + KEY_COL_REC_FARMER_SMS_STATUS + " INTEGER,"
                    + KEY_COL_REC_SHIFT_DATA_IDX + " INTEGER,"
                    + KEY_COL_REC_SHIFT_DATA_COLL_TYPE + " TEXT, " +
                    " UNIQUE (" + KEY_COL_REC_REFERENCE_IDX + "," + KEY_COL_REC_SHIFT_DATA_COLL_TYPE
                    + " )" + " )";


}
