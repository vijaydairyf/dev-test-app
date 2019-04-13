package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.EditRecordStatusEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.tableEntities.EditRecordStatusTable;

/**
 * Created by u_pendra on 5/2/18.
 */

public class EditRecordStatusDao extends Dao {

    public EditRecordStatusDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, EditRecordStatusTable.TABLE_COL_EX_REC_STATUS);
    }

    @Override
    ContentValues getContentValues(Entity entity) {
        EditRecordStatusEntity erse = null;

        if (entity instanceof EditRecordStatusEntity) {
            erse = (EditRecordStatusEntity) entity;

        } else {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(EditRecordStatusTable.KEY_COL_EX_REC_COLUMN_ID, erse.getId());
        contentValues.put(EditRecordStatusTable.KEY_COL_EX_REC_FARMER_SMS_STATUS, erse.getSalesSMSStatus());
        contentValues.put(EditRecordStatusTable.KEY_COL_EX_REC_REFERENCE_IDX, erse.getSalesTableIdx());
        contentValues.put(EditRecordStatusTable.KEY_COL_EX_REC_REPORT_SEQNUM, erse.getSalesSeqNum());
        contentValues.put(EditRecordStatusTable.KEY_COL_EX_REC_SEND_STATUS, erse.getNwSendStatus());
        contentValues.put(EditRecordStatusTable.KEY_COL_EX_REC_SHIFT_DATA_IDX, erse.getShiftDataIdx());
        return contentValues;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        EditRecordStatusEntity erse = new EditRecordStatusEntity();


        erse.setId(cursor.getLong(0));
        erse.setShiftDataIdx(cursor.getInt(
                cursor.getColumnIndex(EditRecordStatusTable.KEY_COL_EX_REC_SHIFT_DATA_IDX)));
        erse.setNwSendStatus(cursor.getInt(
                cursor.getColumnIndex(EditRecordStatusTable.KEY_COL_EX_REC_SEND_STATUS)));
        erse.setSalesSeqNum(cursor.getLong(
                cursor.getColumnIndex(EditRecordStatusTable.KEY_COL_EX_REC_REPORT_SEQNUM)));
        erse.setSalesSMSStatus(cursor.getInt(
                cursor.getColumnIndex(EditRecordStatusTable.KEY_COL_EX_REC_FARMER_SMS_STATUS)));
        erse.setSalesTableIdx(cursor.getInt(
                cursor.getColumnIndex(EditRecordStatusTable.KEY_COL_EX_REC_REFERENCE_IDX)));

        return erse;
    }

    @Override
    String getEntityIdColumnName() {
        return EditRecordStatusTable.KEY_COL_EX_REC_COLUMN_ID;
    }

    public EditRecordStatusEntity findByEditReferenceIndex(long index) {
        EditRecordStatusEntity srse = null;
        String query = "SELECT * FROM " + EditRecordStatusTable.TABLE_COL_EX_REC_STATUS + " WHERE " +
                EditRecordStatusTable.KEY_COL_EX_REC_REFERENCE_IDX + " = " + index;

        return (EditRecordStatusEntity) findOneByQuery(query);
    }
}
