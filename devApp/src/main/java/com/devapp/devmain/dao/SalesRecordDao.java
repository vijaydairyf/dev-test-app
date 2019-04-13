package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.tableEntities.SalesCollectionTable;
import com.devapp.devmain.user.Util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by u_pendra on 4/2/18.
 */

public class SalesRecordDao extends Dao {

    SalesRecordDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, SalesCollectionTable.TABLE_SALES_REPORT);
    }


    @Override
    String getEntityIdColumnName() {
        return SalesCollectionTable.KEY_SALES_COLUMNID;
    }


    public ArrayList<SalesRecordEntity> findByDateAndShift(Date date, String shift) {
        return null;
    }

    public ArrayList<SalesRecordEntity> findByProducerIdDateAndShift(String producerId, Date date, String shift) {
        return null;
    }

   /* public ArrayList<SalesRecordEntity> findByQuery(String selectQuery) {
        ArrayList<SalesRecordEntity> salesRecordEntities =  findByQuery(selectQuery);
        return salesRecordEntities;
    }*/


    //TODO to get the sales record between stardate and enddate
    //Reference getSalesRecordPerMilkType() method in Database Handler
    public ArrayList<SalesRecordEntity> findSalesRecordPerMilkTypeAndTXNType(
            long sdate, long edate, String milkType
            , String txnType, String txnSubtype) {
        Map<String, String> map = new HashMap<>();
        map.put(SalesCollectionTable.KEY_SALES_TIME_MILLI, String.valueOf(sdate));
        map.put(SalesCollectionTable.KEY_SALES_TIME_MILLI, String.valueOf(edate));
        map.put(SalesCollectionTable.KEY_SALES_TXN_TYPE, txnType);
        map.put(SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE, txnSubtype);
        map.put(SalesCollectionTable.KEY_SALES_MILKTYPE, String.valueOf(milkType));
        return (ArrayList<SalesRecordEntity>) findByKey(map);

    }

    /*
    Reference method getAllShiftSalesReports(String date, String shift) in DatabaseHandler
     */
    public ArrayList<SalesRecordEntity> findSalesRecordsPerDayAndShift(
            String date, String shift) {
        Map<String, String> map = new HashMap<>();
        map.put(SalesCollectionTable.POST_DATE, date);
        map.put(SalesCollectionTable.POST_SHIFT, shift);
        return (ArrayList<SalesRecordEntity>) findByKey(map);
    }

    //TODO to get the sales record between stardate and enddate
    /*
       Reference method getAllPeriodicSalesReports(
       long sdate, long edate, String txnType, String txnSubType)
     */

    public ArrayList<SalesRecordEntity> findPeriodicSalesRecord(
            long sdate, long edate, String txnType, String txnSubType) {
        Map<String, String> map = new HashMap<>();
        map.put(SalesCollectionTable.KEY_SALES_TIME_MILLI, String.valueOf(sdate));
        map.put(SalesCollectionTable.KEY_SALES_TIME_MILLI, String.valueOf(edate));
        map.put(SalesCollectionTable.KEY_SALES_TXN_TYPE, txnType);
        map.put(SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE, txnSubType);
        return (ArrayList<SalesRecordEntity>) findByKey(map);
    }


    //TODO Convert the date format to yyyy-mm-dd format

    public SalesRecordEntity findByProducerIdDateShiftAndCollTime(
            String producerId, Date date, String shift, long collectionTime) {

        return findByProducerIdDateShiftAndCollTime(producerId, date.toString(), shift, collectionTime);


    }


    private SalesRecordEntity findByProducerIdDateShiftAndCollTime(
            String producerId, String date, String shift, long collectionTime) {

        String selectQuery = "SELECT * FROM " + SalesCollectionTable.TABLE_SALES_REPORT + " WHERE " +
                SalesCollectionTable.KEY_SALES_ID + " ='" + producerId + "' AND " +
                SalesCollectionTable.POST_DATE + " ='" + date + "'" + " AND " +
                SalesCollectionTable.POST_SHIFT + " ='" + shift + "'" + " AND " +
                SalesCollectionTable.KEY_SALES_TIME_MILLI + " = " + collectionTime;
        SalesRecordEntity salesRecordEntity = null;

        ArrayList<SalesRecordEntity> reportsEntities = (ArrayList<SalesRecordEntity>) findByQuery(selectQuery);

        if (reportsEntities.size() > 0) {
            salesRecordEntity = reportsEntities.get(0);
        }

        return salesRecordEntity;
    }


    @Override
    ContentValues getContentValues(Entity entity) {

        SalesRecordEntity salesRecordEntity = null;
        if (entity instanceof SalesRecordEntity) {

            salesRecordEntity = (SalesRecordEntity) entity;
        } else {
            return null;
        }
        ContentValues values = new ContentValues();

        values.put(SalesCollectionTable.KEY_SALES_ID, salesRecordEntity.salesId);
        values.put(SalesCollectionTable.KEY_SALES_FAT, salesRecordEntity.fat);
        values.put(SalesCollectionTable.KEY_SALES_SNF, salesRecordEntity.snf);
        values.put(SalesCollectionTable.KEY_SALES_NAME, salesRecordEntity.name);
        values.put(SalesCollectionTable.KEY_SALES_RATE, salesRecordEntity.rate);
        values.put(SalesCollectionTable.KEY_SALES_MANUAL, salesRecordEntity.manual);
        values.put(SalesCollectionTable.KEY_SALES_AMOUNT, salesRecordEntity.amount);
        values.put(SalesCollectionTable.KEY_SALES_QUANTITY, salesRecordEntity.Quantity);
        values.put(SalesCollectionTable.KEY_SALES_MILKTYPE,
                salesRecordEntity.milkType.toUpperCase(Locale.ENGLISH));
        values.put(SalesCollectionTable.KEY_SALES_TXNNUM, salesRecordEntity.txnNumber);
        values.put(SalesCollectionTable.KEY_SALES_SOCIETYID,
                salesRecordEntity.socId);
        values.put(SalesCollectionTable.KEY_SALES_USER, salesRecordEntity.user);
        values.put(SalesCollectionTable.KEY_SALES_TIME, salesRecordEntity.time);
        values.put(SalesCollectionTable.KEY_SALES_TIME_MILLI, salesRecordEntity.collectionTime);

        values.put(SalesCollectionTable.KEY_SALES_AWM, salesRecordEntity.awm);
        values.put(SalesCollectionTable.KEY_SALES_CLR, salesRecordEntity.clr);
        values.put(SalesCollectionTable.KEY_SALES_STATUS, salesRecordEntity.status);
        values.put(SalesCollectionTable.KEY_SALES_WEIGHTMANUAL, salesRecordEntity.weightManual);
        values.put(SalesCollectionTable.KEY_SALES_MILKOMANUAL, salesRecordEntity.milkoManual);
        values.put(SalesCollectionTable.KEY_SALES_TEMP, salesRecordEntity.temperature);

        values.put(SalesCollectionTable.KEY_SALES_TXN_TYPE, salesRecordEntity.txnType);
        values.put(SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE, salesRecordEntity.txnSubType);
        values.put(SalesCollectionTable.KEY_SALES_SEND_STATUS, salesRecordEntity.sentStatus);
        values.put(SalesCollectionTable.SMS_SENT_STATUS, salesRecordEntity.sentSmsStatus);

        values.put(SalesCollectionTable.POST_DATE, salesRecordEntity.postDate);
        values.put(SalesCollectionTable.POST_SHIFT, salesRecordEntity.postShift);

        if (salesRecordEntity.sequenceNumber != 0) {
            values.put(SalesCollectionTable.SEQUENCE_NUMBER, salesRecordEntity.sequenceNumber);
        }

        return values;
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        SalesRecordEntity salesRecordEntity = new SalesRecordEntity();
        salesRecordEntity.columnId = cursor.getInt(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_COLUMNID));
        salesRecordEntity.salesId = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_ID));
        salesRecordEntity.name = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_NAME));
        salesRecordEntity.snf = cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_SNF));
        salesRecordEntity.fat = cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_FAT));
        salesRecordEntity.user = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_USER));
        salesRecordEntity.manual = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_MANUAL));

        salesRecordEntity.amount = cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_AMOUNT));
        salesRecordEntity.Quantity = cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_QUANTITY));

        salesRecordEntity.txnNumber = cursor.getInt(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TXNNUM));
        salesRecordEntity.milkType = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_MILKTYPE)).toUpperCase(
                Locale.ENGLISH);
        salesRecordEntity.rate = cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_RATE));
        salesRecordEntity.time = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TIME));

        salesRecordEntity.collectionTime = cursor.getLong(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TIME_MILLI));

        salesRecordEntity.awm = cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_AWM));
        salesRecordEntity.clr = cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_CLR));
        salesRecordEntity.status = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_STATUS));
        salesRecordEntity.weightManual = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_WEIGHTMANUAL));
        salesRecordEntity.milkoManual = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_MILKOMANUAL));

        salesRecordEntity.temperature = cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TEMP));
        salesRecordEntity.txnType = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TXN_TYPE));
        salesRecordEntity.txnSubType = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE));
        salesRecordEntity.socId =
                cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_SOCIETYID));
        salesRecordEntity.sequenceNumber = cursor.getLong(cursor.getColumnIndex(SalesCollectionTable.SEQUENCE_NUMBER));
        salesRecordEntity.postDate = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.POST_DATE));
        salesRecordEntity.postShift = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.POST_SHIFT));

        return salesRecordEntity;

    }

    public List<SalesRecordEntity> findAllUnsent() {
        Map<String, String> map = new HashMap<>();
        map.put(SalesCollectionTable.KEY_SALES_SEND_STATUS, String.valueOf(CollectionConstants.UNSENT));
        return (List<SalesRecordEntity>) findByKey(map);
    }

    public List<SalesRecordEntity> findAllForReport(String startTime, String endTime, String shift, String milkType) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(SalesCollectionTable.KEY_SALES_TIME_MILLI, FilterType.BETWEEN), new String[]{startTime, endTime});
        map.put(new Key(SalesCollectionTable.POST_SHIFT, FilterType.EQUALS), shift);
        map.put(new Key(SalesCollectionTable.KEY_SALES_MILKTYPE, FilterType.EQUALS), milkType);
        return (List<SalesRecordEntity>) findByFilter(map);
    }


    public String getTotalSalesCurrentSession(String date, String shift, String milkType) {

        double qtySold = 0, amtSold = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        String selectQuery = "SELECT  * FROM " + tableName + " WHERE " +
                SalesCollectionTable.POST_DATE + "='" + date + "'" + " AND " +
                SalesCollectionTable.KEY_SALES_MILKTYPE + "='" + milkType + "'" + " AND "
                + SalesCollectionTable.KEY_SALES_TXN_TYPE + "='" + Util.SALES_TXN_TYPE_SALES
                + "'" + " AND " + SalesCollectionTable.POST_SHIFT + "='" + shift + "'";

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                qtySold = qtySold + cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_QUANTITY));
                amtSold = amtSold + cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_AMOUNT));
            } while (cursor.moveToNext());
        }
        String strReturn = decimalFormat.format(qtySold) + "=" + decimalFormat.format(amtSold);
        if (cursor != null)
            cursor.close();
        return strReturn;
    }


}
