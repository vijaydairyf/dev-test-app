package com.devapp.devmain.tableEntities;

/**
 * Created by xx on 9/9/18.
 */

public interface DispatchCollectionTable {


    String TABLE_DISPATCH_REPORT = "dispatchReportTable";


    String KEY_COLUMN_ID = "_id";
    String KEY_SUPERVISOR_ID = "supervisorId";
    String KEY_DISPATCH_SNF = "snf";
    String KEY_DISPATCH_FAT = "fat";
    String NO_OF_CANS = "numberOfCans";
    String KEY_DISPATCH_MODE = "mode";

    String KEY_TIME_MILLIS = "timeMillis";
    String KEY_POST_DATE = "postDate";
    String KEY_POST_SHIFT = "postShift";
    String KEY_DISPATCH_COLLECTED_QUANTITY = "collectedQuantity";
    String KEY_DISPATCH_SOLD_QUANTITY = "soldQuantity";
    String KEY_DISPATCH_AVAILABLE_QUANTITY = "availableQuantity";
    String KEY_DISPATCH_AVAILABLE_QUANTITY_LTR = "availableLtrQuantity";
    String KEY_DISPATCH_AVAILABLE_QUANTITY_KG = "availableKgQuantity";
    String KEY_SEND_STATUS = "sentStatus";
    String KEY_MILKTYPE = "milktype";
    String SEQUENCE_NUMBER = "seqNum";
    String LAST_MODIFIED = "lastModified";


    String CREATE_DISPATCH_REPORT = "CREATE TABLE " + TABLE_DISPATCH_REPORT + "("
            + KEY_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + KEY_SUPERVISOR_ID + " TEXT,"
            + KEY_DISPATCH_FAT + " INTEGER,"
            + KEY_DISPATCH_SNF + " INTEGER,"
            + KEY_DISPATCH_MODE + " TEXT,"
            + KEY_POST_DATE + " TEXT,"
            + KEY_POST_SHIFT + " TEXT,"
            + KEY_MILKTYPE + " TEXT,"
            + NO_OF_CANS + " INTEGER,"
            + KEY_TIME_MILLIS + " INTEGER UNIQUE,"
            + KEY_DISPATCH_COLLECTED_QUANTITY + " INTEGER,"
            + KEY_DISPATCH_SOLD_QUANTITY + " INTEGER, "
            + KEY_SEND_STATUS + " INTEGER,"
            + KEY_DISPATCH_AVAILABLE_QUANTITY + " INTEGER, "
            + KEY_DISPATCH_AVAILABLE_QUANTITY_LTR + " INTEGER, "
            + KEY_DISPATCH_AVAILABLE_QUANTITY_KG + " INTEGER, "
            + SEQUENCE_NUMBER + " INTEGER, "
            + LAST_MODIFIED + " INTEGER "
            + ")";


}
