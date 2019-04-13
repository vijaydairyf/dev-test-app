package com.devapp.devmain.tableEntities;


/**
 * Created by u_pendra on 31/7/18.
 */

public interface RateTable {


    String TABLE_NAME = "RateTable";
    String TEMP_TABLE = "tempRateTable";


    String COLUMN_ID = "_id";
    String USER = "userName";
    String FARMER = "farmerId";
    String SNF = "snf";
    String FAT = "fat";
    String RATE = "rate";
    String START_DATE = "startDate";
    String END_DATE = "endDate";
    String MILK_TYPE = "milkType";
    String SOCID = "socId";
    String CLR = "clr";
    String RATE_REF_ID = "rateRefId";

    String NAME = "rateName";

    String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, " + RATE
            + " REAL," + FARMER + " TEXT," + FAT
            + " REAL," + SNF + " REAL," + USER + " TEXT,"
            + END_DATE + " TEXT," + START_DATE + " TEXT,"
            + SOCID + " TEXT," + MILK_TYPE + " TEXT,"
            + CLR + " TEXT," + RATE_REF_ID + " INTEGER" + ")";

    String INSERT_QUERY_WITH_REF_ID =
            "INSERT INTO " + TABLE_NAME +
                    " select " + COLUMN_ID + "," +
                    RATE + "," + FARMER + "," + FAT
                    + "," + SNF + "," + USER + "," + END_DATE + "," +
                    START_DATE + "," + SOCID + "," +
                    MILK_TYPE + ", " + CLR + ",(select a._id from " + RateChartNameTable.TABLE_NAME + " AS a " +
                    " where a.rateName = " + " b.rateName " + ")" + " FROM "
                    + TEMP_TABLE + " AS b";


}
