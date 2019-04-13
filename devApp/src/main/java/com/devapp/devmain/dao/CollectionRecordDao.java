package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.ConsolidationPost.DateShiftEntry;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by u_pendra on 3/2/18.
 */


public class CollectionRecordDao extends Dao {

    CollectionRecordDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, FarmerCollectionTable.TABLE_REPORTS);
    }

    @Override
    String getEntityIdColumnName() {
        return FarmerCollectionTable.KEY_REPORT_COLUMNID;
    }

    @Override
    Entity findByEntity(Entity entity) {
        ReportEntity re = (ReportEntity) entity;
        Entity e = findByProducerIdDateShiftAndCollTime(
                re.farmerId, re.postDate, re.postShift, re.miliTime);
        return e;
    }

    @Override
    ContentValues getContentValues(Entity entity) {

        ReportEntity reportEntity = null;

        if (entity instanceof ReportEntity) {

            reportEntity = (ReportEntity) entity;
        } else {
            return null;
        }


        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_REPORT_FARMERID,
                ValidationHelper.getValidText(reportEntity.farmerId.toUpperCase(Locale.ENGLISH), ""));
        values.put(DatabaseHandler.KEY_REPORT_FAT, reportEntity.fat);
        values.put(DatabaseHandler.KEY_REPORT_SNF, reportEntity.snf);
        values.put(DatabaseHandler.KEY_REPORT_NAME,
                ValidationHelper.getValidText(reportEntity.farmerName, ""));
        values.put(DatabaseHandler.KEY_REPORT_RATE, reportEntity.rate);
        values.put(DatabaseHandler.KEY_REPORT_MANUAL,
                ValidationHelper.getValidText(reportEntity.manual, "Manual"));
        values.put(DatabaseHandler.KEY_REPORT_AMOUNT, reportEntity.amount);
        values.put(DatabaseHandler.KEY_REPORT_QUANTITY, reportEntity.quantity);
        values.put(DatabaseHandler.KEY_REPORT_MILKTYPE,
                ValidationHelper.getValidText(reportEntity.milkType, "COW"));
        values.put(DatabaseHandler.KEY_REPORT_TAXNUM, reportEntity.txnNumber);
        values.put(DatabaseHandler.KEY_REPORT_LDATE,
                reportEntity.lDate);
        values.put(DatabaseHandler.KEY_REPORT_SOCIETYID,
                String.valueOf(reportEntity.socId));
        values.put(DatabaseHandler.KEY_REPORT_BONUS, reportEntity.bonus);
        values.put(DatabaseHandler.KEY_REPORT_USER,
                ValidationHelper.getValidText(reportEntity.user, ""));
        values.put(DatabaseHandler.KEY_REPORT_TIME,
                ValidationHelper.getValidText(reportEntity.time, ""));
        values.put(DatabaseHandler.KEY_REPORT_TIME_MILLI,
                reportEntity.miliTime);
        values.put(DatabaseHandler.KEY_REPORT_AWM, reportEntity.awm);
        values.put(DatabaseHandler.KEY_REPORT_CLR, reportEntity.clr);
        values.put(DatabaseHandler.KEY_REPORT_STATUS, reportEntity.status);
        values.put(DatabaseHandler.KEY_REPORT_WEIGHTMANUAL, reportEntity.quantityMode);
        values.put(DatabaseHandler.KEY_REPORT_MILKOMANUAL, reportEntity.qualityMode);
        values.put(DatabaseHandler.KEY_REPORT_MILKANALYSERTIME, reportEntity.milkAnalyserTime);
        values.put(DatabaseHandler.KEY_REPORT_WEIGHINGTIME, reportEntity.weighingTime);
        values.put(DatabaseHandler.KEY_REPORT_TEMP, reportEntity.temp);
        values.put(DatabaseHandler.KEY_REPORT_COMMITED, reportEntity.recordCommited);
        values.put(DatabaseHandler.KEY_REPORT_TYPE, reportEntity.collectionType);
        values.put(DatabaseHandler.KEY_MILKQUALITY, reportEntity.milkQuality);
        values.put(DatabaseHandler.KEY_REPORT_RATEMODE, reportEntity.rateMode);
        values.put(DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS, reportEntity.numberOfCans);

        values.put(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER, reportEntity.sampleNumber);
        values.put(DatabaseHandler.KEY_REPORT_ROUTE, reportEntity.centerRoute);
        values.put(DatabaseHandler.KEY_REPORT_RECORD_STATUS, reportEntity.recordStatus);

        values.put(DatabaseHandler.KEY_REPORT_KG_QTY, reportEntity.kgWeight);
        values.put(DatabaseHandler.KEY_REPORT_LTR_QTY, reportEntity.ltrsWeight);

        values.put(DatabaseHandler.KEY_REPORT_RATECHART_NAME,
                ValidationHelper.getValidText(reportEntity.rateChartName, ""));
        values.put(DatabaseHandler.KEY_TIPPING_START_TIME, reportEntity.tippingStartTime);
        values.put(DatabaseHandler.KEY_TIPPING_END_TIME, reportEntity.tippingEndTime);
        values.put(DatabaseHandler.KEY_REPORT_AGENT_ID,
                ValidationHelper.getValidText(reportEntity.agentId, ""));
        values.put(DatabaseHandler.KEY_REPORT_MILKSTATUS_CODE, reportEntity.milkStatusCode);
        values.put(DatabaseHandler.KEY_REPORT_RATE_CALC_DEVICE, reportEntity.rateCalculation);

        values.put(DatabaseHandler.KEY_REPORT_MA_SERIAL,
                ValidationHelper.getValidText(reportEntity.maSerialNumber, ""));
        values.put(DatabaseHandler.KEY_REPORT_MA_NAME,
                ValidationHelper.getValidText(reportEntity.maName, ""));
        values.put(DatabaseHandler.KEY_REPORT_PROTEIN, reportEntity.protein);
        values.put(DatabaseHandler.KEY_REPORT_LACTOSE, reportEntity.lactose);
        values.put(DatabaseHandler.KEY_REPORT_INCENTIVE_RATE, reportEntity.incentiveRate);
        values.put(DatabaseHandler.KEY_REPORT_INCENTIVE_AMOUNT, reportEntity.incentiveAmount);

        values.put(DatabaseHandler.KEY_REPORT_KG_FAT, reportEntity.fatKg);
        values.put(DatabaseHandler.KEY_REPORT_KG_SNF, reportEntity.snfKg);
//        values.put(DatabaseHandler.KEY_REPORT_KG_CLR,
//                CheckValidation.getValidText(reportEntitycll, "0.0"));

        values.put(FarmerCollectionTable.POST_SHIFT, reportEntity.postShift);
        values.put(FarmerCollectionTable.POST_DATE, reportEntity.postDate);
        values.put(FarmerCollectionTable.KEY_REPORT_SENT_STATUS, reportEntity.sentStatus);
        values.put(FarmerCollectionTable.SMS_SENT_STATUS, reportEntity.sentSmsStatus);

        values.put(FarmerCollectionTable.KEY_REPORT_CONDUTIVITY, reportEntity.conductivity);
        if (reportEntity.sequenceNum != 0) {
            values.put(FarmerCollectionTable.SEQUENCE_NUMBER, reportEntity.sequenceNum);

        }
        values.put(FarmerCollectionTable.LAST_MODIFIED, reportEntity.lastModified);
        return values;

    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        ReportEntity reportEntity = new ReportEntity();
        try {
            reportEntity.columnId = cursor.getLong(0);
            reportEntity.farmerId = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FARMERID));

            reportEntity.farmerName = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_NAME));
            reportEntity.snf = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF));
            reportEntity.fat = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT));
            reportEntity.user = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_USER));
            reportEntity.manual = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MANUAL));
            reportEntity.amount = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT));
            reportEntity.quantity = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY));
            reportEntity.txnNumber = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TAXNUM));
            reportEntity.milkType = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));
            reportEntity.rate = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE));
            reportEntity.lDate = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LDATE));
            reportEntity.socId = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SOCIETYID));
            reportEntity.bonus = cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_BONUS));
            reportEntity.time = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME));
            reportEntity.miliTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME_MILLI));
            reportEntity.awm = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AWM));
            reportEntity.clr = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CLR));
            reportEntity.status = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_STATUS));
            reportEntity.quantityMode = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_WEIGHTMANUAL));
            reportEntity.qualityMode = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKOMANUAL));
            reportEntity.milkAnalyserTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKANALYSERTIME));
            reportEntity.weighingTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_WEIGHINGTIME));
            reportEntity.temp = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TEMP));
            reportEntity.recordCommited = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_COMMITED));
            reportEntity.collectionType = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TYPE));
            reportEntity.milkQuality = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_MILKQUALITY));
            reportEntity.rateMode = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATEMODE));
            reportEntity.numberOfCans = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS));
            reportEntity.sampleNumber = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER));
            reportEntity.centerRoute = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_ROUTE));
            reportEntity.recordStatus = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RECORD_STATUS));

            reportEntity.kgWeight = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_KG_QTY));
            reportEntity.ltrsWeight = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LTR_QTY));

            reportEntity.tippingStartTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_TIPPING_START_TIME));
            reportEntity.tippingEndTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_TIPPING_END_TIME));
            reportEntity.user = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_USER));
            reportEntity.agentId = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AGENT_ID));
            reportEntity.milkStatusCode = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKSTATUS_CODE));
            reportEntity.milkType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));

            reportEntity.rateCalculation = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE_CALC_DEVICE));

            reportEntity.maSerialNumber = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_SERIAL));
            reportEntity.maName = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_NAME));
            reportEntity.protein = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_PROTEIN));
            reportEntity.incentiveRate = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_INCENTIVE_RATE));
            reportEntity.incentiveAmount = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_INCENTIVE_AMOUNT));
            reportEntity.snfKg = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_KG_SNF));
            reportEntity.fatKg = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_KG_FAT));
            reportEntity.lactose = cursor.getDouble(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LACTOSE));

            reportEntity.postDate = cursor.getString(
                    cursor.getColumnIndex(FarmerCollectionTable.POST_DATE));
            reportEntity.postShift = cursor.getString(
                    cursor.getColumnIndex(FarmerCollectionTable.POST_SHIFT));
            reportEntity.sequenceNum = cursor.getLong(
                    cursor.getColumnIndex(FarmerCollectionTable.SEQUENCE_NUMBER));

            reportEntity.sentSmsStatus = cursor.getInt(
                    cursor.getColumnIndex(FarmerCollectionTable.SMS_SENT_STATUS));
            reportEntity.sentStatus = cursor.getInt(
                    cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_SENT_STATUS));

            reportEntity.conductivity = cursor.getDouble(cursor.getColumnIndex(
                    FarmerCollectionTable.KEY_REPORT_CONDUTIVITY));

            if (cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_RATECHART_NAME) != -1) {
                reportEntity.rateChartName = cursor.getString(
                        cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_RATECHART_NAME));
            }

            reportEntity.lastModified = cursor.getLong(cursor.getColumnIndex(FarmerCollectionTable.LAST_MODIFIED));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return reportEntity;

    }


    //Custom methods


    public ArrayList<ReportEntity> findByDateAndShift(Date date, String shift) {
        return null;
    }

    public ArrayList<ReportEntity> findByProducerIdDateAndShift(String producerId, Date date, String shift) {
        return null;
    }


    //TODO Convert the date format to yyyy-mm-dd format

    public ReportEntity findByProducerIdDateShiftAndCollTime(
            String producerId, Date date, String shift, long collectionTime) {

        return findByProducerIdDateShiftAndCollTime(producerId, date.toString(), shift, collectionTime);


    }


    private ReportEntity findByProducerIdDateShiftAndCollTime(
            String producerId, String date, String shift, long collectionTime) {

        String selectQuery = "SELECT * FROM " + FarmerCollectionTable.TABLE_REPORTS + " WHERE " +
                FarmerCollectionTable.KEY_REPORT_FARMERID + " ='" + producerId + "' AND " +
                FarmerCollectionTable.POST_DATE + " ='" + date + "'" + " AND " +
                FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'" + " AND " +
                FarmerCollectionTable.KEY_REPORT_TIME_MILLI + " = " + collectionTime;
        ReportEntity reportEntity = null;

        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByQuery(selectQuery);

        if (reportEntities.size() > 0) {
            reportEntity = reportEntities.get(0);
        }

        return reportEntity;
    }

    public ReportEntity findByDateShiftAndCommitStatus(String date, String shift, int status) {
        ReportEntity reportEntity = null;
        String query = "SELECT * FROM " + FarmerCollectionTable.TABLE_REPORTS + " WHERE " +
                FarmerCollectionTable.POST_DATE + " ='" + date + "'" + " AND " +
                FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'" + " AND " +
                FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + " ='" + Util.RECORD_STATUS_COMPLETE + "'" + " AND " +
                FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + status;

        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByQuery(query);

        if (reportEntities.size() > 0) {
            reportEntity = reportEntities.get(0);
        }

        return reportEntity;

    }

    public ArrayList<ReportEntity> findAllByCollectionType(String collectionType) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.KEY_REPORT_TYPE, collectionType);
        map.put(FarmerCollectionTable.KEY_REPORT_COMMITED + "!", String.valueOf(Util.REPORT_NOT_COMMITED));
        return (ArrayList<ReportEntity>) findByKey(map);
    }

    public ReportEntity findUncommitedRecordByFarmerIdDateAndShift(String farmerId, String date, String shift) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.KEY_REPORT_FAT + "!", "NA");
        map.put(FarmerCollectionTable.KEY_REPORT_FARMERID, farmerId);
        map.put(FarmerCollectionTable.POST_DATE, date);
        map.put(FarmerCollectionTable.KEY_REPORT_COMMITED, String.valueOf(Util.REPORT_NOT_COMMITED));
        map.put(FarmerCollectionTable.POST_SHIFT, shift);
        return (ReportEntity) findOneByKey(map);
    }

    public ReportEntity findUncommitedRecordByDateAndShift(String lDate, String shift) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.KEY_REPORT_FAT + "!", "NA");
        map.put(FarmerCollectionTable.KEY_REPORT_LDATE, lDate);
        map.put(FarmerCollectionTable.KEY_REPORT_RECORD_STATUS, Util.RECORD_STATUS_COMPLETE);
        map.put(FarmerCollectionTable.KEY_REPORT_COMMITED, String.valueOf(Util.REPORT_NOT_COMMITED));
        map.put(FarmerCollectionTable.POST_SHIFT, shift);
        return (ReportEntity) findOneByKey(map);
    }

    public ReportEntity findByFarmerIdDateShiftAndMilkType(String farmerId, String date, String shift, String milkType) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.KEY_REPORT_TYPE + "!", Util.REPORT_TYPE_SAMPLE);
        map.put(FarmerCollectionTable.KEY_REPORT_FARMERID, farmerId);
        map.put(FarmerCollectionTable.POST_DATE, date);
        map.put(FarmerCollectionTable.POST_SHIFT, shift);
        map.put(FarmerCollectionTable.KEY_REPORT_MILKTYPE, milkType);
        return (ReportEntity) findOneByKey(map);
    }

    public ArrayList<ReportEntity> findByDateRangeFarmerIdAndMilkType(long sDate, long eDate, String farmerId, String milkType, String reportType) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT  * FROM " + tableName + " WHERE ");
        sb.append(FarmerCollectionTable.KEY_REPORT_LDATE + " BETWEEN " + sDate + " AND " + eDate);
        if (!farmerId.equalsIgnoreCase("all") && !farmerId.equalsIgnoreCase("All farmers")) {
            sb.append(" AND " + FarmerCollectionTable.KEY_REPORT_FARMERID + "=" + "'" + farmerId + "'");
        }
        if (milkType != null) {
            sb.append(" AND " + FarmerCollectionTable.KEY_REPORT_MILKTYPE + "=" + "'" + milkType + "'");
        }
        sb.append(" AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + "!=" + 0);
        if (reportType != null) {
            sb.append(" AND " + FarmerCollectionTable.KEY_REPORT_TYPE + "='" + reportType + "'");
        }
        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByQuery(sb.toString());
        return reportEntities;
    }

    public void deleteByDays(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -days);
        long time = calendar.getTimeInMillis();
        sqLiteDatabase.delete(tableName, FarmerCollectionTable.KEY_REPORT_TIME_MILLI + " < ? ", new String[]{String.valueOf(time)});
        if (secondaryDao != null) {
            ((CollectionRecordDao) secondaryDao).deleteByDays(days);
        }

    }

    public ArrayList<ReportEntity> findByUnsentSms() {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.SMS_SENT_STATUS, String.valueOf(CollectionConstants.UNSENT));
        map.put(FarmerCollectionTable.KEY_REPORT_TYPE, Util.REPORT_TYPE_FARMER);
        map.put(FarmerCollectionTable.KEY_REPORT_COMMITED, String.valueOf(Util.REPORT_COMMITED));
        map.put(FarmerCollectionTable.KEY_REPORT_RECORD_STATUS, Util.RECORD_STATUS_COMPLETE);
        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByKey(map);
        return reportEntities;
    }

    public long updateSmsStatus(int id, int status) {
        String filter = FarmerCollectionTable.KEY_REPORT_COLUMNID + " = ?";
        ContentValues values = new ContentValues();
        values.put(FarmerCollectionTable.SMS_SENT_STATUS, status);
        String[] args = new String[]{Integer.toString(id)};
        return update(values, filter, args);
    }

    public ArrayList<ReportEntity> findAllByDateShiftAndMilkType(String date, String shift, String milkType) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.POST_DATE, date);
        map.put(FarmerCollectionTable.KEY_REPORT_MILKTYPE, milkType);
        map.put(FarmerCollectionTable.POST_SHIFT, shift);
        map.put(FarmerCollectionTable.KEY_REPORT_COMMITED, String.valueOf(Util.REPORT_COMMITED));
        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByKey(map);
        return reportEntities;
    }

    public ArrayList<ReportEntity> findAllByDateShiftMilkTypeAndReportType(long date, String shift, String milkType, String reportType) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.KEY_REPORT_LDATE, String.valueOf(date));
        map.put(FarmerCollectionTable.POST_SHIFT, shift);
        map.put(FarmerCollectionTable.KEY_REPORT_MILKTYPE, milkType);
        map.put(FarmerCollectionTable.KEY_REPORT_TYPE, reportType);
        map.put(FarmerCollectionTable.KEY_REPORT_COMMITED, String.valueOf(Util.REPORT_COMMITED));
        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByKey(map);
        return reportEntities;

    }

    public ArrayList<ReportEntity> findAllByDateAndShift(String date, String shift) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.POST_DATE, date);
        map.put(FarmerCollectionTable.POST_SHIFT, shift);
        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByKey(map);
        return reportEntities;
    }

    public ArrayList<ReportEntity> findAllUnsentByCollectionTypeAndCompletionStatus(String type, String status) {
        Map<String, String> map = new HashMap<>();
        map.put(FarmerCollectionTable.KEY_REPORT_TYPE, type);
        if (status != null) {
            map.put(FarmerCollectionTable.KEY_REPORT_RECORD_STATUS, status);
        }
        map.put(FarmerCollectionTable.KEY_REPORT_SENT_STATUS, String.valueOf(CollectionConstants.UNSENT));
        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByKey(map);
        return reportEntities;
    }

    public int getUnsentCount() {
        String query = "Select count(*) from " + FarmerCollectionTable.TABLE_REPORTS
                + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS;

        return 0;
    }

    public ArrayList<ReportEntity> findAllForReports(String[] route, String[] mcc, long dateFrom, long dateTo,
                                                     String cattleType, String shift, String reportType,
                                                     String commitStatus, String recordCompleteStatus) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_ROUTE, FilterType.IN), route);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_FARMERID, FilterType.IN), mcc);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_LDATE, FilterType.BETWEEN), new String[]{String.valueOf(dateFrom), String.valueOf(dateTo)});
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_MILKTYPE, FilterType.EQUALS), cattleType);
        map.put(new Key(FarmerCollectionTable.POST_SHIFT, FilterType.EQUALS), shift);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_TYPE, FilterType.EQUALS), reportType);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_COMMITED, FilterType.EQUALS), String.valueOf(commitStatus));
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_RECORD_STATUS, FilterType.EQUALS), recordCompleteStatus);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_TIME_MILLI, FilterType.ORDER_BY), FilterType.Order.ASC);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_MILKTYPE, FilterType.ORDER_BY), FilterType.Order.ASC);
        return (ArrayList<ReportEntity>) findByFilter(map);
    }

    public ReportEntity findNextSampleBySampleNumber(int sampleNo, String date, String shift) {
        Map<Key, Object> map = new LinkedHashMap<>();
        ReportEntity reportEntity = null;
        map.put(new Key(FarmerCollectionTable.POST_DATE, FilterType.EQUALS), date);
        map.put(new Key(FarmerCollectionTable.POST_SHIFT, FilterType.EQUALS), shift);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_COMMITED, FilterType.EQUALS), String.valueOf(Util.REPORT_NOT_COMMITED));
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_RECORD_STATUS, FilterType.EQUALS), Util.RECORD_STATUS_INCOMPLETE);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER, FilterType.GREATER_THAN), String.valueOf(sampleNo));
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER, FilterType.ORDER_BY), FilterType.Order.ASC);
        List<ReportEntity> reportEntityList = (List<ReportEntity>) findByFilter(map);
        if (reportEntityList.size() > 0) {
            reportEntity = reportEntityList.get(0);
        }
        return reportEntity;
    }

    public int getNextSampleNumber(String date, String shift) {
        String query = "SELECT MAX(" + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + ") FROM "
                + tableName + " WHERE " + FarmerCollectionTable.POST_DATE + " ='" +
                date + "'" + " AND " + FarmerCollectionTable.POST_SHIFT + " = '" + shift + "'"
                + " AND " + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + " !=" + Util.LAST_SEQ_NUM;

        Cursor cursor = rawQuery(query, null);
        int value;
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(0) + 1;
        } else {
            value = 1;
        }
        if (cursor != null)
            cursor.close();
        return value;
    }

    public ArrayList<ReportEntity> getRecordsForTwoMa(String ma1CurrentSample, String ma2CurrentSample, String date, String shift) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_COMMITED, FilterType.EQUALS), String.valueOf(Util.REPORT_NOT_COMMITED));
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_TYPE, FilterType.EQUALS), Util.REPORT_TYPE_MCC);
        map.put(new Key(FarmerCollectionTable.POST_DATE, FilterType.EQUALS), date);
        map.put(new Key(FarmerCollectionTable.POST_SHIFT, FilterType.EQUALS), shift);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER, FilterType.NOT_EQUALS), ma1CurrentSample);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER, FilterType.NOT_EQUALS), ma2CurrentSample);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER, FilterType.NOT_EQUALS), String.valueOf(0));
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER, FilterType.NOT_EQUALS), String.valueOf(Util.LAST_SEQ_NUM));
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER, FilterType.ORDER_BY), FilterType.Order.ASC);
        return (ArrayList<ReportEntity>) findByFilter(map);
    }

    public ReportEntity findCollectionForDateShiftAndType(String date, String shift, String milkType) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_MILKTYPE, FilterType.EQUALS), milkType);
        map.put(new Key(FarmerCollectionTable.POST_DATE, FilterType.EQUALS), date);
        map.put(new Key(FarmerCollectionTable.POST_SHIFT, FilterType.EQUALS), shift);
        ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) findByFilter(map);
        ReportEntity reportEntity = null;
        if (reportEntities != null && reportEntities.size() > 0) {
            reportEntity = reportEntities.get(0);
        }
        return reportEntity;
    }


    public ArrayList<ReportEntity> findUnsentByStartDateEndDateAndShift(long startDate,
                                                                        long endDate,
                                                                        String shift,
                                                                        String type) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_TIME_MILLI, FilterType.BETWEEN),
                new String[]{String.valueOf(startDate), String.valueOf(endDate)});
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_COMMITED, FilterType.EQUALS), Util.REPORT_NOT_COMMITED);
        map.put(new Key(FarmerCollectionTable.POST_SHIFT, FilterType.EQUALS), shift);
        map.put(new Key(FarmerCollectionTable.KEY_REPORT_TYPE, FilterType.EQUALS), type);

        return (ArrayList<ReportEntity>) findByFilter(map);
    }

    public ArrayList<DateShiftEntry> getDateAndShiftEntity(long startDate,
                                                           long endDate,
                                                           String shift,
                                                           String type) {
        ArrayList<DateShiftEntry> allDateShiftEntries = new ArrayList<>();

        String query = "Select distinct " + FarmerCollectionTable.POST_DATE
                + "," + FarmerCollectionTable.POST_SHIFT + " from " + FarmerCollectionTable.TABLE_REPORTS
                + " Where " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI + " > " + startDate
                + " And " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI + " < " + endDate
                + " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + " ='" + type + "'"
                + " AND " + FarmerCollectionTable.KEY_REPORT_TYPE + " ='" + shift + "'"
                + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                + " order by " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                + " ASC , " + FarmerCollectionTable.POST_SHIFT + " DESC";

        Cursor cursor = sqLiteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                DateShiftEntry dateShiftEntry = new DateShiftEntry();
                dateShiftEntry.setDate(
                        cursor.getString(cursor.getColumnIndex(
                                FarmerCollectionTable.POST_DATE)));

                dateShiftEntry.setShift(cursor.getString(
                        cursor.getColumnIndex(FarmerCollectionTable.POST_SHIFT)));

                allDateShiftEntries.add(dateShiftEntry);

            } while (cursor.moveToNext());
        }

        return allDateShiftEntries;
    }


}
