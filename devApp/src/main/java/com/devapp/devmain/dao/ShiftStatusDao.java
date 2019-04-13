package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.ShiftStatusEntity;
import com.devapp.devmain.tableEntities.ShiftStatusTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by u_pendra on 24/9/18.
 */

public class ShiftStatusDao extends ProfileDao {


    public ShiftStatusDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, ShiftStatusTable.TABLE_NAME);
    }


    @Override
    ContentValues getContentValues(Entity entity) {
        ShiftStatusEntity sse = null;

        if (entity instanceof ShiftStatusEntity) {
            sse = (ShiftStatusEntity) entity;

        } else {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(ShiftStatusTable.DATE, sse.date);
        contentValues.put(ShiftStatusTable.SHIFT, sse.shift);
        contentValues.put(ShiftStatusTable.START_TIME, sse.startTime);
        contentValues.put(ShiftStatusTable.END_TIME, sse.endTime);
        contentValues.put(ShiftStatusTable.END_SHIFT_STATUS, sse.sentStatus);
        return contentValues;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        ShiftStatusEntity sse = new ShiftStatusEntity();


        sse.setColumnId(cursor.getLong(0));
        sse.setStartTime(cursor.getLong(
                cursor.getColumnIndex(ShiftStatusTable.START_TIME)));

        sse.setEndTime(cursor.getLong(
                cursor.getColumnIndex(ShiftStatusTable.END_TIME)));
        sse.setDate(cursor.getString(
                cursor.getColumnIndex(ShiftStatusTable.DATE)));
        sse.setShift(cursor.getString(
                cursor.getColumnIndex(ShiftStatusTable.SHIFT)));
        sse.setSentStatus(cursor.getInt(
                cursor.getColumnIndex(ShiftStatusTable.END_SHIFT_STATUS)));

        return sse;
    }

    @Override
    String getEntityIdColumnName() {
        return ShiftStatusTable.COLUMN_ID;
    }

    public ShiftStatusEntity findEntityForDateShiftAndType(String date, String shift) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(ShiftStatusTable.DATE, FilterType.EQUALS), date);
        map.put(new Key(ShiftStatusTable.SHIFT, FilterType.EQUALS), shift);
        ArrayList<ShiftStatusEntity> shiftStatusEntities
                = (ArrayList<ShiftStatusEntity>) findByFilter(map);
        ShiftStatusEntity sse = null;
        if (shiftStatusEntities != null && shiftStatusEntities.size() > 0) {
            sse = shiftStatusEntities.get(0);
        }
        return sse;
    }

    public ShiftStatusEntity findEntityForStatus(int status) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(ShiftStatusTable.END_SHIFT_STATUS, FilterType.EQUALS),
                String.valueOf(status));
        ArrayList<ShiftStatusEntity> shiftStatusEntities
                = (ArrayList<ShiftStatusEntity>) findByFilter(map);
        ShiftStatusEntity sse = null;
        if (shiftStatusEntities != null && shiftStatusEntities.size() > 0) {
            sse = shiftStatusEntities.get(0);
        }
        return sse;
    }


}

