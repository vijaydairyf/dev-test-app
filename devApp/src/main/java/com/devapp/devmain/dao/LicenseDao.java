package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.LicenseEntity;
import com.devapp.devmain.tableEntities.LicenseTable;

public class LicenseDao extends Dao {

    LicenseDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, LicenseTable.TABLE_LICENSE);
    }

    @Override
    ContentValues getContentValues(Entity entity) {
        LicenseEntity licenseEntity = (LicenseEntity) entity;

        ContentValues contentValues = new ContentValues();
        contentValues.put(LicenseTable.KEY_LICENSE_END_DATE, licenseEntity.endDate);
        contentValues.put(LicenseTable.KEY_LICENSE_MESSAGE, licenseEntity.message);
        contentValues.put(LicenseTable.KEY_LICENSE_REQUEST_DATE, licenseEntity.requestTime);
        contentValues.put(LicenseTable.KEY_LICENSE_START_DATE, licenseEntity.startDate);
        contentValues.put(LicenseTable.KEY_LICENSE_STATUS_CODE, licenseEntity.responseStatusCode);
        contentValues.put(LicenseTable.KEY_LICENSE_VALID, licenseEntity.isValid);


        return contentValues;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        LicenseEntity licenseEntity = new LicenseEntity();

        licenseEntity.columnId = cursor.getLong(
                cursor.getColumnIndex(LicenseTable.KEY_LICENSE_COLUMNID));
        licenseEntity.endDate = cursor.getLong(
                cursor.getColumnIndex(LicenseTable.KEY_LICENSE_END_DATE));
        licenseEntity.startDate = cursor.getLong(
                cursor.getColumnIndex(LicenseTable.KEY_LICENSE_START_DATE));
        licenseEntity.requestTime = cursor.getLong(
                cursor.getColumnIndex(LicenseTable.KEY_LICENSE_REQUEST_DATE));
        licenseEntity.isValid = cursor.getString(
                cursor.getColumnIndex(LicenseTable.KEY_LICENSE_VALID));
        licenseEntity.message = cursor.getString(
                cursor.getColumnIndex(LicenseTable.KEY_LICENSE_MESSAGE));
        licenseEntity.responseStatusCode = cursor.getString(
                cursor.getColumnIndex(LicenseTable.KEY_LICENSE_STATUS_CODE));


        return licenseEntity;
    }

    @Override
    String getEntityIdColumnName() {
        return LicenseTable.KEY_LICENSE_COLUMNID;
    }
}
