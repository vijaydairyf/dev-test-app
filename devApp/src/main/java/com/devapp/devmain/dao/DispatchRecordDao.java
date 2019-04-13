package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.ConsolidationPost.ConsolidatedHelperMethods;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.MilkAnalyser;
import com.devapp.devmain.postentities.QualityParamsPost;
import com.devapp.devmain.postentities.QualityReadingData;
import com.devapp.devmain.postentities.QuantityParamspost;
import com.devapp.devmain.postentities.WeighingScaleData;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;
import com.devapp.devmain.tableEntities.DispatchCollectionTable;
import com.devapp.devmain.user.Util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xx on 9/9/18.
 */

public class DispatchRecordDao extends Dao {

    DispatchRecordDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, DispatchCollectionTable.TABLE_DISPATCH_REPORT);
    }


    @Override
    String getEntityIdColumnName() {
        return DispatchCollectionTable.KEY_COLUMN_ID;
    }

    @Override
    ContentValues getContentValues(Entity entity) {

        DispatchPostEntity dispatchPostEntity = null;
        if (entity instanceof DispatchPostEntity) {

            dispatchPostEntity = (DispatchPostEntity) entity;
        } else {
            return null;
        }
        ContentValues values = new ContentValues();

        values.put(DispatchCollectionTable.KEY_SUPERVISOR_ID, dispatchPostEntity.supervisorId);
        //  values.put(DispatchCollectionTable.KEY_COLUMN_ID, dispatchPostEntity.columnId);
        values.put(DispatchCollectionTable.KEY_POST_DATE, dispatchPostEntity.date);
        values.put(DispatchCollectionTable.KEY_POST_SHIFT, dispatchPostEntity.shift);
        values.put(DispatchCollectionTable.SEQUENCE_NUMBER, dispatchPostEntity.sequenceNumber);
        values.put(DispatchCollectionTable.KEY_TIME_MILLIS, dispatchPostEntity.timeInMillis);
        values.put(DispatchCollectionTable.KEY_MILKTYPE, dispatchPostEntity.milkType);
        values.put(DispatchCollectionTable.NO_OF_CANS, dispatchPostEntity.numberOfCans);
        values.put(DispatchCollectionTable.KEY_DISPATCH_FAT, dispatchPostEntity.qualityParams.milkAnalyser.qualityReadingData.fat);
        values.put(DispatchCollectionTable.KEY_DISPATCH_SNF, dispatchPostEntity.qualityParams.milkAnalyser.qualityReadingData.snf);
        values.put(DispatchCollectionTable.KEY_DISPATCH_MODE, dispatchPostEntity.qualityParams.mode);
        values.put(DispatchCollectionTable.KEY_DISPATCH_COLLECTED_QUANTITY, dispatchPostEntity.quantityParams.collectedQuantity);
        values.put(DispatchCollectionTable.KEY_DISPATCH_SOLD_QUANTITY, dispatchPostEntity.quantityParams.soldQuantity);
        values.put(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY, dispatchPostEntity.quantityParams.weighingScaleData.measuredValue);
        values.put(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY_LTR, dispatchPostEntity.quantityParams.weighingScaleData.inLtr);
        values.put(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY_KG, dispatchPostEntity.quantityParams.weighingScaleData.inKg);
        values.put(DispatchCollectionTable.KEY_SEND_STATUS, CollectionConstants.UNSENT);

        return values;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        DispatchPostEntity dispatchPostEntity = new DispatchPostEntity();
        dispatchPostEntity.columnId = cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.KEY_COLUMN_ID));
        dispatchPostEntity.milkType = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_MILKTYPE));
        dispatchPostEntity.numberOfCans = cursor.getInt(cursor.getColumnIndex(DispatchCollectionTable.NO_OF_CANS));
        QualityParamsPost qualityParams = new QualityParamsPost();
        qualityParams.mode = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_MODE));
        MilkAnalyser milkAnalyzer = new MilkAnalyser();
        QualityReadingData reading = new QualityReadingData();
        reading.fat = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_FAT));
        reading.snf = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_SNF));
        milkAnalyzer.qualityReadingData = reading;
        qualityParams.milkAnalyser = milkAnalyzer;
        dispatchPostEntity.qualityParams = qualityParams;
        QuantityParamspost quantityParams = new QuantityParamspost();
        quantityParams.mode = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_MODE));
        quantityParams.collectedQuantity = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_COLLECTED_QUANTITY));
        quantityParams.soldQuantity = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_SOLD_QUANTITY));

        WeighingScaleData weighingScale = new WeighingScaleData();
        weighingScale.measuredValue = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY));
        weighingScale.inKg = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY_KG));
        weighingScale.inLtr = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY_LTR));
        quantityParams.weighingScaleData = weighingScale;
        dispatchPostEntity.quantityParams = quantityParams;
        dispatchPostEntity.supervisorId = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_SUPERVISOR_ID));
        dispatchPostEntity.lastModified = cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.LAST_MODIFIED));
        dispatchPostEntity.timeInMillis = cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.KEY_TIME_MILLIS));
        dispatchPostEntity.collectionTime = ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.KEY_TIME_MILLIS)));
        dispatchPostEntity.date = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_POST_DATE));
        dispatchPostEntity.shift = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_POST_SHIFT));
        dispatchPostEntity.sentStatus = cursor.getInt(cursor.getColumnIndex(DispatchCollectionTable.KEY_SEND_STATUS));
        dispatchPostEntity.sequenceNumber = cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.SEQUENCE_NUMBER));

        dispatchPostEntity.time = Util.getTimeFromMiliSecond(cursor.getLong(cursor.getColumnIndex(
                DispatchCollectionTable.KEY_TIME_MILLIS)));
        return dispatchPostEntity;

    }

    public List<DispatchPostEntity> findAllUnsent() {
        Map<String, String> map = new HashMap<>();
        map.put(DispatchCollectionTable.KEY_SEND_STATUS, String.valueOf(CollectionConstants.UNSENT));
        return (List<DispatchPostEntity>) findByKey(map);
    }

    public List<DispatchPostEntity> findAllForReport(String startTime, String endTime, String shift, String milkType) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(DispatchCollectionTable.KEY_TIME_MILLIS, FilterType.BETWEEN), new String[]{startTime, endTime});
        map.put(new Key(DispatchCollectionTable.KEY_POST_SHIFT, FilterType.EQUALS), shift);
        map.put(new Key(DispatchCollectionTable.KEY_MILKTYPE, FilterType.EQUALS), milkType);
        return (List<DispatchPostEntity>) findByFilter(map);
    }


    public List<DispatchPostEntity> getDispatchEntities(long startTime, long endTime) {

        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(DispatchCollectionTable.KEY_TIME_MILLIS, FilterType.BETWEEN),
                new String[]{String.valueOf(startTime), String.valueOf(endTime)});
        return (List<DispatchPostEntity>) findByFilter(map);
    }


    public List<DispatchPostEntity> getDailyShiftDispatchReport(String date, String shift) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(DispatchCollectionTable.KEY_POST_DATE, FilterType.EQUALS), date);
        map.put(new Key(DispatchCollectionTable.KEY_POST_SHIFT, FilterType.EQUALS), shift);

        return (List<DispatchPostEntity>) findByFilter(map);
    }

    public Cursor getDispatchReports(long sdate, long edate) {

        String buildSQL;
        buildSQL = "SELECT  * FROM " + DispatchCollectionTable.TABLE_DISPATCH_REPORT + " WHERE "
                + DispatchCollectionTable.KEY_TIME_MILLIS + " BETWEEN " + sdate + " AND " + edate;

        Cursor cursor = rawQuery(buildSQL, null);

        return cursor;
    }

}
