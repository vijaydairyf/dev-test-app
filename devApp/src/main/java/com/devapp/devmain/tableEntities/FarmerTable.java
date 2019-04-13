package com.devapp.devmain.tableEntities;

/**
 * Created by x on 23/1/18.
 */

public interface
FarmerTable {
    String TABLE_FARMER = "FarmerTable";


    String KEY_FARMER_NAME = "farmerName";
    String KEY_FARMER_COLUMNID = "_id";
    String KEY_FARMER_ID = "farmerId";
    String KEY_FARMER_BARCODE = "farmerBarcode";
    String KEY_FARMER_CANS = "farmerCans";
    String KEY_FARMER_CATTLE = "cattleType";
    String KEY_FARMER_SOCCODE = "socCode";
    String KEY_FARMER_MOBILE = "mobile";
    String KEY_FARMER_EMAIL = "email";
    String KEY_FARMER_COW = "numCow";
    String KEY_FARMER_BUFFALO = "numBuff";
    String KEY_FARMER_NO_CATTLE = "numCattle";
    String KEY_FARMER_ASSIGN_RATECHART = "assignRatechart";
    String KEY_FARMER_REGISTRATION_DATE = "regDate";
    String KEY_FARMER_WEEKLY = "weekReport";
    String KEY_FARMER_MONTHLY = "monthReport";
    String KEY_ENABLE_MULTIPLECANS = "enableMultipleCans";
    String KEY_FARMER_SENT = "sentFarmerStatus";
    String KEY_AGENT_ID = "agentId";
    String KEY_FARMER_TYPE = "farmerType";

    String KEY_FARMER_FAMILY = "numOfFamily";
    String KEY_FARMER_COW_HERD_SIZE = "cowHerdSize";
    String KEY_FARMER_BUFFALO_HERD_SIZE = "buffaloHerdSize";
    String KEY_FARMER_DAIRY_SOURCE = "dairySource";
    String KEY_FARMER_PRIMARY_SOURCE = "primarySource";
    String KEY_FARMER_REGION = "region";
    String KEY_FARMER_LAT = "lat";
    String KEY_FARMER_LNG = "lng";
    String KEY_FARMER_GENDER = "gender";
    String KEY_FARMER_AGE = "age";
    String KEY_FARMER_EDUCATION = "education";
    String KEY_FARMER_PINCODE = "pincode";
    String KEY_FARMER_ADDRESS = "address";
    String KEY_FARMER_CAST_CATEGORY = "castCategory";
    String KEY_FARMER_ECS_CODE = "ecsCode";


    String CREATE_QUERY = "CREATE TABLE " + TABLE_FARMER + "("
            + KEY_FARMER_COLUMNID + " INTEGER PRIMARY KEY, "
            + KEY_FARMER_BARCODE + " TEXT UNIQUE," + KEY_FARMER_NAME + " TEXT,"
            + KEY_FARMER_CATTLE + " TEXT," + KEY_FARMER_SOCCODE + " TEXT,"
            + KEY_FARMER_MOBILE + " TEXT," + KEY_FARMER_EMAIL + " TEXT,"
            + KEY_FARMER_ID + " TEXT NOT NULL UNIQUE," + KEY_FARMER_CANS + " TEXT,"
            + KEY_FARMER_COW + " TEXT," + KEY_FARMER_BUFFALO + " TEXT,"
            + KEY_FARMER_NO_CATTLE + " TEXT," + KEY_FARMER_ASSIGN_RATECHART
            + " TEXT," + KEY_FARMER_REGISTRATION_DATE + " INTEGER,"
            + KEY_FARMER_WEEKLY + " INTEGER," + KEY_FARMER_MONTHLY
            + " INTEGER," + KEY_ENABLE_MULTIPLECANS + " TEXT,"
            + KEY_FARMER_SENT + " INTEGER, " + KEY_AGENT_ID + " TEXT, "
            + KEY_FARMER_TYPE + " TEXT, " + KEY_FARMER_FAMILY + " TEXT, "
            + KEY_FARMER_COW_HERD_SIZE + " TEXT, " + KEY_FARMER_BUFFALO_HERD_SIZE + " TEXT, "
            + KEY_FARMER_DAIRY_SOURCE + " TEXT, " + KEY_FARMER_PRIMARY_SOURCE + " TEXT, "
            + KEY_FARMER_REGION + " TEXT, "
            + KEY_FARMER_GENDER + " TEXT, "
            + KEY_FARMER_AGE + " TEXT, " + KEY_FARMER_EDUCATION + " TEXT,"
            + KEY_FARMER_PINCODE + " TEXT," + KEY_FARMER_ADDRESS + " TEXT,"
            + KEY_FARMER_CAST_CATEGORY + " TEXT," + KEY_FARMER_ECS_CODE + " TEXT"
            + ")";
}
