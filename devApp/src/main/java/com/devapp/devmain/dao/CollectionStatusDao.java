package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.CollectionRecordStatusEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.tableEntities.CollectionRecordStatusTable;

/**
 * Created by u_pendra on 5/2/18.
 */

public class CollectionStatusDao extends Dao {


    public CollectionStatusDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, CollectionRecordStatusTable.TABLE_COL_REC_STATUS);
    }


    @Override
    ContentValues getContentValues(Entity entity) {

        CollectionRecordStatusEntity crse = null;

        if (entity instanceof CollectionRecordStatusEntity) {

            crse = (CollectionRecordStatusEntity) entity;
        } else {
            return null;
        }


        ContentValues values = new ContentValues();

        values.put(CollectionRecordStatusTable.KEY_COL_REC_SEND_STATUS,
                crse.getNwSendStatus());

        values.put(CollectionRecordStatusTable.KEY_COL_REC_FARMER_SMS_STATUS,
                crse.getFarmerSMSStatus());
        values.put(CollectionRecordStatusTable.KEY_COL_REC_REFERENCE_IDX,
                crse.getReportsTableIdx());
        values.put(CollectionRecordStatusTable.KEY_COL_REC_REPORT_SEQNUM,
                crse.getSeqNum());
        values.put(CollectionRecordStatusTable.KEY_COL_REC_SHIFT_DATA_COLL_TYPE,
                crse.getCollectionType());
        values.put(CollectionRecordStatusTable.KEY_COL_REC_SHIFT_DATA_IDX,
                crse.getShiftDataIdx());

        return values;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        CollectionRecordStatusEntity crse = new CollectionRecordStatusEntity();


        crse.setPrimaryKeyId(cursor.getLong(0));
        crse.setCollectionType(cursor.getString(
                cursor.getColumnIndex(CollectionRecordStatusTable.KEY_COL_REC_SHIFT_DATA_COLL_TYPE)));

        crse.setFarmerSMSStatus(cursor.getInt(
                cursor.getColumnIndex(CollectionRecordStatusTable.KEY_COL_REC_FARMER_SMS_STATUS)));

        crse.setNwSendStatus(cursor.getInt(
                cursor.getColumnIndex(CollectionRecordStatusTable.KEY_COL_REC_SEND_STATUS)));
        crse.setReportsTableIdx(cursor.getLong(
                cursor.getColumnIndex(CollectionRecordStatusTable.KEY_COL_REC_REFERENCE_IDX)));
        crse.setShiftDataIdx(cursor.getInt(
                cursor.getColumnIndex(CollectionRecordStatusTable.KEY_COL_REC_SHIFT_DATA_IDX)));
        crse.setSeqNum(cursor.getLong(
                cursor.getColumnIndex(CollectionRecordStatusTable.KEY_COL_REC_REPORT_SEQNUM)));


        return crse;
    }

    @Override
    String getEntityIdColumnName() {
        return CollectionRecordStatusTable.KEY_COL_REC_COLUMN_ID;
    }

    public CollectionRecordStatusEntity findByReportTableIndexAndType(long index, String type) {
        CollectionRecordStatusEntity crse = null;
        String query = "SELECT * FROM " + CollectionRecordStatusTable.TABLE_COL_REC_STATUS + " WHERE " +
                CollectionRecordStatusTable.KEY_COL_REC_REFERENCE_IDX + " = " + index + " And "
                + CollectionRecordStatusTable.KEY_COL_REC_SHIFT_DATA_COLL_TYPE + " ='" + type + "'";


        return (CollectionRecordStatusEntity) findOneByQuery(query);

    }
}
