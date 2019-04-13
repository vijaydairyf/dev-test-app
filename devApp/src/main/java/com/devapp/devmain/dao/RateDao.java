package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.RateTable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by x on 30/7/18.
 */

public class RateDao extends Dao {
    RateDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, DatabaseHandler.TABLE_RATE);
    }

    public synchronized void saveWithRefId(List<? extends Entity> entities, long refId) {
        for (Entity entity : entities) {
            RateChartEntity rce = (RateChartEntity) entity;
            rce.rateReferenceId = refId;
            save(rce);
        }

    }


    public synchronized void saveAll(List<? extends Entity> entities) {
        for (Entity entity : entities) {
            save(entity);
        }
    }

    @Override
    ContentValues getContentValues(Entity entity) {
        RateChartEntity rateEntity = null;
        if (entity instanceof RateChartEntity) {
            rateEntity = (RateChartEntity) entity;
        } else {
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(RateTable.FARMER, rateEntity.farmerId);
        values.put(RateTable.FAT, rateEntity.fat);
        values.put(RateTable.SNF, rateEntity.snf);
        values.put(RateTable.USER, rateEntity.managerID);
        values.put(RateTable.MILK_TYPE, rateEntity.milkType.toUpperCase(Locale.ENGLISH));
        values.put(RateTable.END_DATE, rateEntity.endDate);
        values.put(RateTable.START_DATE, rateEntity.startDate);
        values.put(RateTable.SOCID, rateEntity.societyId);
        values.put(RateTable.RATE, rateEntity.rate);
        values.put(RateTable.CLR, rateEntity.clr);
        values.put(RateTable.RATE, rateEntity.rate);
        values.put(RateTable.RATE_REF_ID, rateEntity.rateReferenceId);
        return values;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        RateChartEntity rateChartEntity = new RateChartEntity();

        rateChartEntity.columnId = (int) cursor.getLong(cursor.getColumnIndex(RateTable.COLUMN_ID));
        rateChartEntity.societyId = cursor.getString(cursor.getColumnIndex(RateTable.SOCID));
        rateChartEntity.farmerId = cursor.getString(cursor.getColumnIndex(RateTable.FARMER));
        rateChartEntity.fat = cursor.getDouble(cursor.getColumnIndex(RateTable.FAT));
        rateChartEntity.snf = cursor.getDouble(cursor.getColumnIndex(RateTable.SNF));
        rateChartEntity.rate = cursor.getDouble(cursor.getColumnIndex(RateTable.RATE));
        rateChartEntity.startDate = cursor.getString(cursor.getColumnIndex(RateTable.START_DATE));
        rateChartEntity.endDate = cursor.getString(cursor.getColumnIndex(RateTable.END_DATE));
        rateChartEntity.milkType = cursor.getString(cursor.getColumnIndex(RateTable.MILK_TYPE));
        rateChartEntity.managerID = cursor.getString(cursor.getColumnIndex(RateTable.USER));
        rateChartEntity.clr = cursor.getString(cursor.getColumnIndex(RateTable.CLR));
        rateChartEntity.rateReferenceId = cursor.getLong(cursor.getColumnIndex(RateTable.RATE_REF_ID));
        return rateChartEntity;
    }

    @Override
    String getEntityIdColumnName() {
        return RateTable.COLUMN_ID;
    }

    public ArrayList<RateChartEntity> findAllByName(long refId) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(RateTable.RATE_REF_ID, FilterType.EQUALS), String.valueOf(refId));
        List<RateChartEntity> rateChartEntities = (List<RateChartEntity>) findByFilter(map);
        return (ArrayList<RateChartEntity>) rateChartEntities;
    }


    public String findRateFromInput(String fat, String snf, String clr, long refId) {
        String rate = "0.00";
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(RateTable.FAT, FilterType.EQUALS), fat);
        map.put(new Key(RateTable.SNF, FilterType.EQUALS), snf);
        map.put(new Key(RateTable.CLR, FilterType.EQUALS), clr);
        map.put(new Key(RateTable.RATE_REF_ID, FilterType.EQUALS), String.valueOf(refId));

        List<RateChartEntity> rateChartEntities = (List<RateChartEntity>) findByFilter(map);

        if (rateChartEntities != null && rateChartEntities.size() > 0) {
            rate = String.valueOf(rateChartEntities.get(0).rate);
        }

        return rate;

    }

    public String findMininumFat(String milkType, long refId) {
        String fat = "0.00";
        String query = "Select MIN(fat) from RateTable where rateRefId =" + refId;
        if (milkType != null) {
            query = "Select MIN(fat) from RateTable where milkType ='" +
                    milkType + "' And rateRefId =" + refId;
        }
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            fat = cursor.getString(0);
            cursor.close();
        }

        return fat;
    }

    public String findMaximumFat(String milkType, long refId) {
        String fat = "14.00";

        String query = "Select MAX(fat) from RateTable where rateRefId =" + refId;
        if (milkType != null) {
            query = "Select MAX(fat) from RateTable where milkType ='" +
                    milkType + "' And rateRefId =" + refId;
        }
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            fat = cursor.getString(0);
            cursor.close();
        }

        return fat;
    }

    public String findMinimumSNF(String milkType, long refId) {
        String snf = "0.00";
        String query = "Select MIN(snf) from RateTable where rateRefId =" + refId;
        if (milkType != null) {
            query = "Select MIN(snf) from RateTable where milkType ='" +
                    milkType + "' And rateRefId =" + refId;
        }
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            snf = cursor.getString(0);
            cursor.close();
        }

        return snf;
    }

    public String findMaximumSNF(String milkType, long refId) {
        String snf = "14.00";

        String query = "Select MAX(snf) from RateTable where rateRefId =" + refId;
        if (milkType != null) {
            query = "Select MAX(snf) from RateTable where milkType ='" +
                    milkType + "' And rateRefId =" + refId;
        }
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            snf = cursor.getString(0);
            cursor.close();
        }

        return snf;
    }


}
