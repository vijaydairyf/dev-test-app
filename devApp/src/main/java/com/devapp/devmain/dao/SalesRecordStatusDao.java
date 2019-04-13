package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.SalesRecordStatusEntity;
import com.devapp.devmain.tableEntities.SalesRecordStatusTable;

/**
 * Created by u_pendra on 5/2/18.
 */

public class SalesRecordStatusDao extends Dao {

    public SalesRecordStatusDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, SalesRecordStatusTable.TABLE_COLL_SALES_STATUS);
    }


    @Override
    ContentValues getContentValues(Entity entity) {
        SalesRecordStatusEntity srse = null;

        if (entity instanceof SalesRecordStatusEntity) {
            srse = (SalesRecordStatusEntity) entity;

        } else {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(SalesRecordStatusTable.KEY_COL_SALES_COLUMN_ID, srse.getId());
        contentValues.put(SalesRecordStatusTable.KEY_COL_SALES_FARMER_SMS_STATUS, srse.getSalesSMSStatus());
        contentValues.put(SalesRecordStatusTable.KEY_COL_SALES_REFERENCE_IDX, srse.getSalesTableIdx());
        contentValues.put(SalesRecordStatusTable.KEY_COL_SALES_REPORT_SEQNUM, srse.getSalesSeqNum());
        contentValues.put(SalesRecordStatusTable.KEY_COL_SALES_SEND_STATUS, srse.getNwSendStatus());
        contentValues.put(SalesRecordStatusTable.KEY_COL_SALES_SHIFT_DATA_IDX, srse.getShiftDataIdx());
        return contentValues;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        SalesRecordStatusEntity srse = new SalesRecordStatusEntity();


        srse.setId(cursor.getLong(0));
        srse.setShiftDataIdx(cursor.getInt(
                cursor.getColumnIndex(SalesRecordStatusTable.KEY_COL_SALES_SHIFT_DATA_IDX)));
        srse.setNwSendStatus(cursor.getInt(
                cursor.getColumnIndex(SalesRecordStatusTable.KEY_COL_SALES_SEND_STATUS)));
        srse.setSalesSeqNum(cursor.getLong(
                cursor.getColumnIndex(SalesRecordStatusTable.KEY_COL_SALES_REPORT_SEQNUM)));
        srse.setSalesSMSStatus(cursor.getInt(
                cursor.getColumnIndex(SalesRecordStatusTable.KEY_COL_SALES_FARMER_SMS_STATUS)));
        srse.setSalesTableIdx(cursor.getInt(
                cursor.getColumnIndex(SalesRecordStatusTable.KEY_COL_SALES_REFERENCE_IDX)));

        return srse;
    }

    @Override
    String getEntityIdColumnName() {
        return SalesRecordStatusTable.KEY_COL_SALES_COLUMN_ID;
    }

    public SalesRecordStatusEntity findBySalesReferenceIndex(long index) {
        SalesRecordStatusEntity srse = null;
        String query = "SELECT * FROM " + SalesRecordStatusTable.TABLE_COLL_SALES_STATUS + " WHERE " +
                SalesRecordStatusTable.KEY_COL_SALES_REFERENCE_IDX + " = " + index;

        return (SalesRecordStatusEntity) findOneByQuery(query);
    }
}
