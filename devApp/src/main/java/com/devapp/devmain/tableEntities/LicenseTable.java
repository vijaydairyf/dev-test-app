package com.devapp.devmain.tableEntities;

public interface LicenseTable {


    String TABLE_LICENSE = "permTable";
    String KEY_LICENSE_REQUEST_DATE = "requestTime";
    String KEY_LICENSE_STATUS_CODE = "responseStatusCode";
    String KEY_LICENSE_START_DATE = "startDate";
    String KEY_LICENSE_END_DATE = "endDate";
    String KEY_LICENSE_VALID = "isValid";
    String KEY_LICENSE_MESSAGE = "message";

    String KEY_LICENSE_COLUMNID = "_id";
    String CREATE_QUERY = "CREATE TABLE " + TABLE_LICENSE + "("
            + KEY_LICENSE_COLUMNID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_LICENSE_REQUEST_DATE + " INTEGER, "
            + KEY_LICENSE_STATUS_CODE + " INTEGER,"
            + KEY_LICENSE_START_DATE + " INTEGER," + KEY_LICENSE_END_DATE + " INTEGER,"
            + KEY_LICENSE_VALID + " TEXT," + KEY_LICENSE_MESSAGE + " TEXT)";


}
