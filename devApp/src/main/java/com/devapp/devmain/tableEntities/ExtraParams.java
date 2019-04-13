package com.devapp.devmain.tableEntities;

/**
 * Created by u_pendra on 23/1/18.
 */

public interface ExtraParams {

    String TABLE_EXTRA_PARAMS = "tableExtraParams";
    String COLUMN_ID = "_id";
    String KEY = "key";
    String VALUE = "value";
    String REF_SEQ_NUM = "refSeqNum";
    String COLL_TYPE = "collType";
    String TIME = "time";
    String COMMITTED = "committed";
    String SENT_STATUS = "sentStatus";

    String COLLECTION_TIME = "collectionTime";
    String SMS_SENT_STATUS = "smsSentStatus";
    String POST_DATE = "postDate";
    String POST_SHIFT = "postShift";
    String SEQUENCE_NUMBER = "seqNum";

    /**
     * Query to create ExtraParams Table
     *
     * @return
     */
    String CREATE_EXTRA_PARAMS =
            "CREATE TABLE IF NOT EXISTS " + TABLE_EXTRA_PARAMS + " ( "
                    + ExtraParams.COLUMN_ID + " INTEGER PRIMARY KEY,"
                    + ExtraParams.KEY + " TEXT,"
                    + ExtraParams.VALUE + " TEXT,"
                    + ExtraParams.REF_SEQ_NUM + " INTEGER,"
                    + ExtraParams.COLL_TYPE + " TEXT,"
                    + ExtraParams.TIME + " INTEGER,"
                    + ExtraParams.COMMITTED + " INTEGER,"
                    + ExtraParams.SENT_STATUS + " INTEGER,"
                    + COLLECTION_TIME + " INTEGER, "
                    + SMS_SENT_STATUS + " INTEGER, "
                    + POST_DATE + " VARCHAR(20), "
                    + POST_SHIFT + " VARCHAR(20), "
                    + SEQUENCE_NUMBER + " INTEGER "
                    + ")";

}