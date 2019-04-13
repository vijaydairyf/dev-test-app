package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 5/2/18.
 */

public interface EditRecordStatusTable {
    public static final String TABLE_COL_EX_REC_STATUS = "collEXRecordStatusTable";
    public static final String KEY_COL_EX_REC_COLUMN_ID = "_id";
    public static final String KEY_COL_EX_REC_REPORT_SEQNUM = "seqNum";
    public static final String KEY_COL_EX_REC_REFERENCE_IDX = "reportsTableIdx";
    public static final String KEY_COL_EX_REC_SEND_STATUS = "nwSendStatus";
    public static final String KEY_COL_EX_REC_FARMER_SMS_STATUS = "farmerSMSStatus";
    public static final String KEY_COL_EX_REC_SHIFT_DATA_IDX = "shiftDataIdx";

    String CREATE_COL_EX_RECORD_STATUS = "CREATE TABLE " + TABLE_COL_EX_REC_STATUS + "("
            + KEY_COL_EX_REC_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + KEY_COL_EX_REC_REFERENCE_IDX + " INTEGER, "
            + KEY_COL_EX_REC_REPORT_SEQNUM + " INTEGER, "
            + KEY_COL_EX_REC_SEND_STATUS + " INTEGER, "
            + KEY_COL_EX_REC_FARMER_SMS_STATUS + " INTEGER,"
            + KEY_COL_EX_REC_SHIFT_DATA_IDX + " INTEGER)";
}
