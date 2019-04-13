package com.devapp.devmain.tableEntities;

public interface BasicProfileTable {

    String TABLE_NAME = "BasicProfileTable";


    String COLUMNID = "_id";
    String UUID = "uuid";
    String NAME = "name";
    String CODE = "code";
    String TYPE = "type";
    String BMCID = "bmcId";
    String MODIFIED_TIME = "modifiedTime";
    String CONTACT_NUMBER = "contactNumber";
    String CONTACT_NAME = "contactName";
    String LOCATION = "location";


    String CREATE_BASIC_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMNID + " INTEGER PRIMARY KEY, "
            + UUID + " INTEGER, "
            + BMCID + " VARCHAR(30),"
            + CODE + " VARCHAR(40),"
            + TYPE + " VARCHAR(40),"
            + LOCATION + " TEXT,"
            + CONTACT_NAME + " VARCHAR(150),"
            + NAME + " VARCHAR(150),"
            + CONTACT_NUMBER + " VARCHAR(20),"
            + MODIFIED_TIME + " INTEGER," + ")";


}
