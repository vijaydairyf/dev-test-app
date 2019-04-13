package com.devapp.devmain.tableEntities;

public interface SampleRecordTable {


    String TABLE_SAMPLE = "sampleTable";

    String KEY_SAMPLE_COLUMN_ID = "_id";
    String KEY_SAMPLE_ID = "sampleId";
    String KEY_SAMPLE_SOC_ID = "sampleSocId";
    String KEY_SAMPLE_MODE = "sampleMode";
    String KEY_SAMPLE_IS_WEIGH = "sampleISWeighchk";
    String KEY_SAMPLE_IS_FAT = "sampleIsFat";
    String KEY_SAMPLE_IS_SNF = "sampleIsSnf";
    String KEY_SAMPLE_FAT = "sampleFat";
    String KEY_SAMPLE_SNF = "sampleSnf";
    String KEY_SAMPLE_WEIGH = "sampleWeigh";
    String KEY_SAMPLE_BARCODE = "sampleOther1";
    String KEY_SAMPLE_OTHER2 = "sampleOther2";
    String KEY_SAMPLE_OTHER3 = "sampleOther3";
    String KEY_SAMPLE_RATE = "sampleRate";
    String KEY_SAMPLE_AMOUNT = "sampleAmount";
    String KEY_SAMPLE_SENT = "sentSampleStatus";

    String CREATE_SAMPLE_TABLE = "CREATE TABLE " + TABLE_SAMPLE + "("
            + KEY_SAMPLE_COLUMN_ID + " INTEGER PRIMARY KEY, "
            + KEY_SAMPLE_MODE + " TEXT," + KEY_SAMPLE_FAT + " TEXT,"
            + KEY_SAMPLE_IS_FAT + " TEXT," + KEY_SAMPLE_SNF + " TEXT,"
            + KEY_SAMPLE_IS_SNF + " TEXT," + KEY_SAMPLE_WEIGH + " TEXT,"
            + KEY_SAMPLE_IS_WEIGH + " TEXT," + KEY_SAMPLE_BARCODE
            + " TEXT," + KEY_SAMPLE_OTHER2 + " TEXT," + KEY_SAMPLE_OTHER3
            + " TEXT," + KEY_SAMPLE_RATE + " TEXT," + KEY_SAMPLE_AMOUNT
            + " TEXT," + KEY_SAMPLE_SOC_ID + " TEXT," + KEY_SAMPLE_ID
            + " TEXT," + KEY_SAMPLE_SENT + " INTEGER" + ")";

}
