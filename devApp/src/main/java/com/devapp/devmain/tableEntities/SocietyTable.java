package com.devapp.devmain.tableEntities;

public interface SocietyTable {


    String TABLE_NAME = "SocietyTable";


    String COLUMNID = "_id";
    String NAME = "societyName";
    String CODE = "societyCode";
    String ADDRESS = "societyAddress";
    String LOCATION = "societyLocation";
    String ROUTE = "societyRate";
    String BMCID = "bmcId";
    String EMAIL1 = "socEmail1";
    String CONTACT1 = "societyContact1";
    String CONPERSON1 = "conperson1";
    String EMAIL2 = "socEmail2";
    String CONTACT2 = "societyContact2";
    String CONPERSON2 = "conperson2";
    String NUMFARMER = "numFarmer";
    String REGISTRATION_DATE = "regDate";
    String WEEKLY = "weekReport";
    String MONTHLY = "monthReport";

    String CREATE_SOCIETY_TABLE = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMNID + " INTEGER PRIMARY KEY, "
            + ADDRESS + " TEXT," + BMCID + " TEXT,"
            + CODE + " TEXT," + CONTACT1 + " TEXT,"
            + LOCATION + " TEXT," + ROUTE
            + " TEXT," + EMAIL1 + " TEXT,"
            + CONPERSON1 + " TEXT," + NAME
            + " TEXT," + EMAIL2 + " TEXT,"
            + CONPERSON2 + " TEXT," + CONTACT2
            + " TEXT," + NUMFARMER + " TEXT,"
            + REGISTRATION_DATE + " INTEGER,"
            + WEEKLY + " INTEGER," + MONTHLY
            + " INTEGER" + ")";


}
