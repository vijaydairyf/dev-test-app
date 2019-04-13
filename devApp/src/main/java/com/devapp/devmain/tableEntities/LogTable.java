package com.devapp.devmain.tableEntities;

import static com.devapp.devmain.tableEntities.RateChartNameTable.COLUMNID;

/**
 * Created by u_pendra on 7/8/18.
 */

public interface LogTable {

    String TABLE = "LOG_TABLE";

    String COLUMN_ID = "_id";
    String TYPE = "type";
    String RECEIVED_TIME = "receivedTime";
    String META_DATA = "metaData";
    String USER = "user";
    String CENTER_ID = "centerId";
    String SENT_STATUS = "sentStatus";
    String MODE = "mode";


    String CREATE_QUERY = "CREATE TABLE " + TABLE + "("
            + COLUMNID + " INTEGER PRIMARY KEY, "
            + TYPE + " VARCHAR(30),"
            + RECEIVED_TIME + " INTEGER,"
            + META_DATA + " TEXT,"
            + USER + " VARCHAR(30),"
            + MODE + " VARCHAR(30),"
            + CENTER_ID + " VARCHAR(30),"
            + SENT_STATUS + " INTEGER " + ")";

}
