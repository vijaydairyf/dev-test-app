package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 6/1/18.
 */

public interface TankerCollectionTable {

    String TABLE_TANKER = "tankerTable";
    String KEY_TANKER_NUMBER = "tankerNumber";
    String KEY_COLUMN_ID = "_id";
    String KEY_SUPERVISOR_CODE = "supervisorCode";
    String KEY_ROUTE_NUMER = "routeNumber";
    String KEY_BULK_COLLER_CODE = "bulkcoolercode";
    String KEY_QUANTITY = "quantity";
    String KEY_FAT = "fat";
    String KEY_SNF = "snf";
    String KEY_COMPARTMENT_NUMBERS = "compartmentNumbers";
    String KEY_ALCHOLOL = "alchohol";
    String KEY_SENT = "sent";
    String KEY_COLLECTION_TIME = "collectionTime";
    String KEY_STATUS = "status";
    String KEY_COMMENTS = "comments";
    String KEY_QUANTITY_UNIT = "quantityUnit";
    String KEY_TEMP = "temp";
    String KEY_CLR = "clr";
    String SMS_SENT_STATUS = "smsSentStatus";
    String POST_DATE = "postDate";
    String POST_SHIFT = "postShift";
    String SEQUENCE_NUMBER = "seqNum";

    String CREATE_TANKER_QUERY = "CREATE TABLE IF NOT EXISTS " + TABLE_TANKER + " ( "
            + KEY_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_TANKER_NUMBER + " VARCHAR(100), "
            + KEY_BULK_COLLER_CODE + " VARCHAR(100), "
            + KEY_ROUTE_NUMER + " VARCHAR(100), "
            + KEY_SUPERVISOR_CODE + " VARCHAR(100), "
            + KEY_STATUS + " VARCHAR(100), "
            + KEY_COMMENTS + " VARCHAR(300), "
            + KEY_QUANTITY + " DOUBLE,  "
            + KEY_FAT + " DOUBLE, "
            + KEY_SNF + " DOUBLE, "
            + KEY_ALCHOLOL + " DOUBLE, "
            + KEY_SENT + " INTEGER,"
            + KEY_COLLECTION_TIME + " INTEGER UNIQUE, "
            + KEY_QUANTITY_UNIT + " VARCHAR(30), "
            + KEY_COMPARTMENT_NUMBERS + " TEXT,"
            + KEY_TEMP + " DOUBLE,"
            + KEY_CLR + " DOUBLE, "
            + SMS_SENT_STATUS + " INTEGER, "
            + POST_DATE + " VARCHAR(20), "
            + POST_SHIFT + " VARCHAR(20), "
            + SEQUENCE_NUMBER + " INTEGER "
            + ")";


}
