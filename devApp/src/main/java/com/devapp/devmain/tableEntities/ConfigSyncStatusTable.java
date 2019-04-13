package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 11/7/18.
 */

public interface ConfigSyncStatusTable {

    String TABLE_NAME = "ConfigSyncTable";

    String COLUMN_ID = "_id";
    String CONFIG_TYPE = "configType";
    String CONFIG_STATUS = "configStatus";
    String MODIFIED_TIME = "modifiedTime";


    String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER, " +
            CONFIG_TYPE + " VARCHAR(100) PRIMARY KEY, " +
            CONFIG_STATUS + " INTEGER, " +
            MODIFIED_TIME + " INTEGER )";


}
