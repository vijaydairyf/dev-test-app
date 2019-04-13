package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 5/2/18.
 */

public interface SalesRecordStatusTable {

    public static final String TABLE_COLL_SALES_STATUS = "collSalesRecordStatus";

    public static final String KEY_COL_SALES_COLUMN_ID = "_id";
    public static final String KEY_COL_SALES_REPORT_SEQNUM = "salesseqNum";
    public static final String KEY_COL_SALES_REFERENCE_IDX = "salesTableIdx";
    public static final String KEY_COL_SALES_SEND_STATUS = "nwSendStatus";
    public static final String KEY_COL_SALES_FARMER_SMS_STATUS = "salesSMSStatus";
    public static final String KEY_COL_SALES_SHIFT_DATA_IDX = "shiftDataIdx";

    String CREATE =
            "CREATE TABLE " + TABLE_COLL_SALES_STATUS + "("
                    + KEY_COL_SALES_COLUMN_ID + " INTEGER PRIMARY KEY, "
                    + KEY_COL_SALES_REFERENCE_IDX + " INTEGER, "
                    + KEY_COL_SALES_REPORT_SEQNUM + " INTEGER, "
                    + KEY_COL_SALES_SEND_STATUS + " INTEGER, "
                    + KEY_COL_SALES_FARMER_SMS_STATUS + " INTEGER,"
                    + KEY_COL_SALES_SHIFT_DATA_IDX + " INTEGER)";

}
