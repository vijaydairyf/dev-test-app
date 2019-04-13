package com.devapp.devmain.tableEntities;


public interface ShiftStatusTable {

    String TABLE_NAME = "ShiftStatusTable";

    String COLUMN_ID = "_id";
    String DATE = "date";
    String SHIFT = "shift";
    String START_TIME = "startTime";
    String END_TIME = "endTime";
    String END_SHIFT_STATUS = "endShiftStatus";


    String CREATE_QUERY = "CREATE TABLE " + TABLE_NAME + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + DATE + " VARCHAR(30),"
            + SHIFT + " VARCHAR(30),"
            + START_TIME + " TEXT,"
            + END_TIME + " VARCHAR(30),"
            + END_SHIFT_STATUS + " VARCHAR(30)" + ")";

}
