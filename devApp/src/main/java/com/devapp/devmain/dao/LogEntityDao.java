package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.LogEntity;
import com.devapp.devmain.tableEntities.LogTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by u_pendra on 7/8/18.
 */

public class LogEntityDao extends Dao {

    LogEntityDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, LogTable.TABLE);
    }

    @Override
    ContentValues getContentValues(Entity entity) {

        LogEntity logEntity = (LogEntity) entity;

        ContentValues contentValues = new ContentValues();

        contentValues.put(LogTable.CENTER_ID, logEntity.id.collectionId);
        contentValues.put(LogTable.USER, logEntity.id.userId);
        contentValues.put(LogTable.RECEIVED_TIME, logEntity.receivedTime);
        contentValues.put(LogTable.SENT_STATUS, logEntity.sentStatus);
        contentValues.put(LogTable.MODE, logEntity.mode);
        contentValues.put(LogTable.META_DATA, logEntity.metaData);

        return contentValues;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        LogEntity logEntity = new LogEntity();

        logEntity.columnId = cursor.getLong(cursor.getColumnIndex(LogTable.COLUMN_ID));
        logEntity.id.userId = cursor.getString(cursor.getColumnIndex(LogTable.USER));
        logEntity.id.collectionId = cursor.getString(cursor.getColumnIndex(LogTable.CENTER_ID));
        logEntity.metaData = cursor.getString(cursor.getColumnIndex(LogTable.META_DATA));
        logEntity.sentStatus = cursor.getInt(cursor.getColumnIndex(LogTable.SENT_STATUS));
        logEntity.mode = cursor.getString(cursor.getColumnIndex(LogTable.MODE));
        logEntity.receivedTime = cursor.getLong(cursor.getColumnIndex(LogTable.RECEIVED_TIME));

        return logEntity;
    }

    @Override
    String getEntityIdColumnName() {
        return LogTable.COLUMN_ID;
    }

    public ArrayList<LogEntity> findByInputs(Integer sentStatus, String type) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(LogTable.SENT_STATUS, FilterType.EQUALS), String.valueOf(sentStatus));
        map.put(new Key(LogTable.TYPE, FilterType.EQUALS), type);
        ArrayList<LogEntity> logEntities = (ArrayList<LogEntity>) findByFilter(map);
        LogEntity logEntity = null;
        return logEntities;

    }

}
