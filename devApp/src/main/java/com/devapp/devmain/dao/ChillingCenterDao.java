package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.server.DatabaseHandler;

public class ChillingCenterDao extends Dao {

    public ChillingCenterDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, DatabaseHandler.TABLE_CHILLING_CENTER);
    }


    @Override
    ContentValues getContentValues(Entity entity) {
        return null;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        return null;
    }

    @Override
    String getEntityIdColumnName() {
        return null;
    }
}
