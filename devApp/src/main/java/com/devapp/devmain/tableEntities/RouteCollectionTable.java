package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 6/1/18.
 */

public interface RouteCollectionTable {


    String TABLE_TRUCK_DETAILS = "tableTruckDetails";
    String KEY_TRUCK_COLUMN_ID = "_id";
    String KEY_TRUCK_CENTER_ID = "truckCenterId";
    String KEY_TRUCK_ID = "truckId";
    String KEY_TRUCK_DATE = "truckDate";
    String KEY_TRUCK_SECURITY_TIME = "truckSecurityTime";
    String KEY_TRUCK_REMARKS = "truckRemarks";
    String KEY_TRUCK_RECOVERY_AMOUNT = "recoveryAmount";
    String KEY_TRUCK_MATERIAL = "material";
    String KEY_TRUCK_CANS_IN = "truckCansIn";
    String KEY_TRUCK_CANS_OUT = "truckCansOut";
    String KEY_TRUCK_SHIFT = "truckShift";
    String KEY_TRUCK_SENT_STATUS = "sentStatus";
    String SMS_SENT_STATUS = "smsSentStatus";
    String POST_DATE = "postDate";
    String POST_SHIFT = "postShift";
    String SEQUENCE_NUMBER = "seqNum";


    String CREATE_ROUTE_QUERY = "CREATE TABLE " + TABLE_TRUCK_DETAILS + " ( "
            + KEY_TRUCK_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TRUCK_ID + " TEXT NOT NULL, "
            + KEY_TRUCK_CENTER_ID + " TEXT NOT NULL, "
            + KEY_TRUCK_DATE + " INTEGER, "
            + KEY_TRUCK_SECURITY_TIME + " INTEGER, "
            + KEY_TRUCK_SHIFT + " INTEGER, "
            + KEY_TRUCK_RECOVERY_AMOUNT + " DOUBLE, "
            + KEY_TRUCK_MATERIAL + " TEXT, "
            + KEY_TRUCK_CANS_IN + " INTEGER, "
            + KEY_TRUCK_CANS_OUT + " INTEGER, "
            + KEY_TRUCK_SENT_STATUS + " INTEGER, "
            + KEY_TRUCK_REMARKS + " TEXT, "
            + SMS_SENT_STATUS + " INTEGER, "
            + POST_DATE + " VARCHAR(20), "
            + POST_SHIFT + " VARCHAR(20), "
            + SEQUENCE_NUMBER + " INTEGER "
            + ")";
}
