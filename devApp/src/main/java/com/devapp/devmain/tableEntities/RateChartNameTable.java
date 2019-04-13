package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 31/7/18.
 */

public interface RateChartNameTable {

    String TABLE_NAME = "RateChartName";

    String COLUMNID = "_id";
    String VALIDFROM = "rateValidFrom";
    String VALIDTO = "rateValidto";

    String MILKTYPE = "rateMilkType";
    String OTHER1 = "rateOther1";
    String OTHER2 = "rateOther2";
    String SOCID = "ratechsocId";
    String LVALIDITYFROM = "rateValFrom";
    String LVALIDITYTO = "rateValTo";
    String ISACTIVE = "isActive";
    String TYPE = "rateChartType";
    String NAME = "rateName";
    String MODIFIED_TIME = "modifiedTime";
    String RATE_SHIFT = "rateShift";


    String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMNID + " INTEGER PRIMARY KEY, "
            + NAME + " TEXT," + MILKTYPE
            + " TEXT," + OTHER1 + " TEXT,"
            + OTHER2 + " TEXT," + VALIDFROM
            + " TEXT," + VALIDTO + " TEXT,"
            + SOCID + " TEXT," + LVALIDITYFROM
            + " INTEGER," + LVALIDITYTO + " INTEGER,"
            + ISACTIVE + " TEXT," + TYPE + " TEXT," +
            RATE_SHIFT + " TEXT," +
            MODIFIED_TIME + " INTEGER " + ")";

    String ALTER_QUERY_FOR_MODIFIED_TIME =
            "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + MODIFIED_TIME + " INTEGER DEFAULT " + 0;
    String ALTER_QUERY_FOR_RATE_SHIFT =
            "ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + RATE_SHIFT + " TEXT";


}
