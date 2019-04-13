package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.helper.DatabaseEntity;

/**
 * Created by x on 6/2/18.
 */

public class ConfigurationDao extends ProfileDao {

    public ConfigurationDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, DatabaseEntity.TABLE_CONFIGURATION);
    }

    @Override
    ContentValues getContentValues(Entity entity) {
        ConfigurationElement configurationElement = null;
        if (entity instanceof ConfigurationElement) {

            configurationElement = (ConfigurationElement) entity;
        } else {
            return null;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseEntity.CONFIGURATION_KEY, configurationElement.key);
        contentValues.put(DatabaseEntity.CONFIGURATION_VALUE, configurationElement.value);
        contentValues.put(DatabaseEntity.CONFIGURATION_LAST_VALUE, configurationElement.lastValue);
        contentValues.put(DatabaseEntity.CONFIGURATION_MODIFIED_TIME, configurationElement.modifiedDate);
        contentValues.put(DatabaseEntity.CONFIGURATION_USER_NAME, configurationElement.userName);
        contentValues.put(DatabaseEntity.CONFIGURATION_STATUS, configurationElement.status);
        return contentValues;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        ConfigurationElement configurationElement = new ConfigurationElement();

        configurationElement.key =
                cursor.getString(cursor.getColumnIndex(DatabaseEntity.CONFIGURATION_KEY));
        configurationElement.value =
                cursor.getString(cursor.getColumnIndex(DatabaseEntity.CONFIGURATION_VALUE));
        configurationElement.lastValue =
                cursor.getString(cursor.getColumnIndex(DatabaseEntity.CONFIGURATION_LAST_VALUE));
        configurationElement.modifiedDate =
                cursor.getLong(cursor.getColumnIndex(DatabaseEntity.CONFIGURATION_MODIFIED_TIME));
        configurationElement.userName =
                cursor.getString(cursor.getColumnIndex(DatabaseEntity.CONFIGURATION_USER_NAME));
        configurationElement.status =
                cursor.getInt(cursor.getColumnIndex(DatabaseEntity.CONFIGURATION_STATUS));
        return configurationElement;
    }

    @Override
    String getEntityIdColumnName() {
        return DatabaseEntity.CONFIGURATION_KEY;
    }
}
