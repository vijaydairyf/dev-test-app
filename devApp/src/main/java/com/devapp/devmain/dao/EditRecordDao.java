package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.tableEntities.EditRecordCollectionTable;

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

public class EditRecordDao extends Dao {


    EditRecordDao(SQLiteDatabase sqLiteDatabase) {

        super(sqLiteDatabase, EditRecordCollectionTable.TABLE_EXTENDED_REPORT);
    }

    @Override
    String getEntityIdColumnName() {
        return EditRecordCollectionTable.KEY_REPORT_COLUMNID;
    }


    public ArrayList<ReportEntity> findByDateAndShift(Date date, String shift) {
        return null;
    }

    public ArrayList<ReportEntity> findByProducerIdDateAndShift(String producerId, Date date, String shift) {
        return null;
    }


    //TODO Convert the date format to yyyy-mm-dd format

//    public ReportEntity findByProducerIdDateShiftAndCollTime(
//            String producerId, Date date, String shift, long collectionTime) {
//
//        return findByProducerIdDateShiftAndCollTime(producerId, date.toString(), shift, collectionTime);
//
//
//    }


    private ReportEntity findByProducerIdDateShiftAndEditedTime(
            String producerId, String date, String shift, long editedTime) {

        String selectQuery = "SELECT * FROM " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT + " WHERE " +
                EditRecordCollectionTable.KEY_REPORT_FARMERID + " ='" + producerId + "' AND " +
                EditRecordCollectionTable.POST_DATE + " ='" + date + "'" + " AND " +
                EditRecordCollectionTable.POST_SHIFT + " ='" + shift + "'" + " AND " +
                EditRecordCollectionTable.KEY_REPORT_EDITED_TIME + " = " + editedTime;
        ReportEntity reportEntity = null;

        ArrayList<ReportEntity> reportsEntities = (ArrayList<ReportEntity>) findByQuery(selectQuery);

        if (reportsEntities.size() > 0) {
            reportEntity = reportsEntities.get(0);
        }

        return reportEntity;
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

        values.put(EditRecordCollectionTable.KEY_REPORT_FARMERID, reportEntity.farmerId.toUpperCase(Locale.ENGLISH));
        values.put(EditRecordCollectionTable.KEY_REPORT_FAT, reportEntity.fat);
        values.put(EditRecordCollectionTable.KEY_REPORT_SNF, reportEntity.snf);
        values.put(EditRecordCollectionTable.KEY_REPORT_NAME, reportEntity.farmerName);
        values.put(EditRecordCollectionTable.KEY_REPORT_RATE, reportEntity.rate);
        values.put(EditRecordCollectionTable.KEY_REPORT_MANUAL, reportEntity.manual);
        values.put(EditRecordCollectionTable.KEY_REPORT_AMOUNT, reportEntity.amount);
     /*   values.put(EditRecordCollectionTable.KEY_REPORT_DATE, reportEntity.date);
        values.put(EditRecordCollectionTable.KEY_REPORT_SHIFT, reportEntity.shift);*/
        values.put(EditRecordCollectionTable.KEY_REPORT_QUANTITY, reportEntity.quantity);

        values.put(EditRecordCollectionTable.KEY_REPORT_KG_QTY, reportEntity.kgWeight);
        values.put(EditRecordCollectionTable.KEY_REPORT_LTR_QTY, reportEntity.ltrsWeight);

        values.put(EditRecordCollectionTable.KEY_REPORT_MILKTYPE,
                reportEntity.milkType.toUpperCase(Locale.ENGLISH));
        values.put(EditRecordCollectionTable.KEY_REPORT_TAXNUM, reportEntity.txnNumber);
        values.put(EditRecordCollectionTable.KEY_REPORT_LDATE, reportEntity.lDate);
        values.put(EditRecordCollectionTable.KEY_REPORT_SOCIETYID,
                reportEntity.socId);
        values.put(EditRecordCollectionTable.KEY_REPORT_BONUS, reportEntity.bonus);
        values.put(EditRecordCollectionTable.KEY_REPORT_USER, reportEntity.user);
        values.put(EditRecordCollectionTable.KEY_REPORT_TIME, reportEntity.time);
        values.put(EditRecordCollectionTable.KEY_REPORT_TIME_MILLI, reportEntity.miliTime);
        values.put(EditRecordCollectionTable.KEY_REPORT_AWM, reportEntity.awm);
        values.put(EditRecordCollectionTable.KEY_REPORT_CLR, reportEntity.clr);
        values.put(EditRecordCollectionTable.KEY_REPORT_STATUS, reportEntity.status);
        values.put(EditRecordCollectionTable.KEY_REPORT_WEIGHTMANUAL, reportEntity.quantityMode);
        values.put(EditRecordCollectionTable.KEY_REPORT_MILKOMANUAL, reportEntity.qualityMode);
        values.put(EditRecordCollectionTable.KEY_REPORT_MILKANALYSERTIME, reportEntity.milkAnalyserTime);
        values.put(EditRecordCollectionTable.KEY_REPORT_WEIGHINGTIME, reportEntity.weighingTime);
        values.put(EditRecordCollectionTable.KEY_REPORT_TEMP, reportEntity.temp);
        values.put(EditRecordCollectionTable.KEY_REPORT_COMMITED, reportEntity.recordCommited);
        values.put(EditRecordCollectionTable.KEY_REPORT_TYPE, reportEntity.collectionType);
        values.put(EditRecordCollectionTable.KEY_MILKQUALITY, reportEntity.milkQuality);
        values.put(EditRecordCollectionTable.KEY_REPORT_RATEMODE, reportEntity.rateMode);
        values.put(EditRecordCollectionTable.KEY_REPORT_NUMBER_OF_CANS, reportEntity.numberOfCans);

        values.put(EditRecordCollectionTable.KEY_REPORT_SEQUENCE_NUMBER, reportEntity.sampleNumber);
        values.put(EditRecordCollectionTable.KEY_REPORT_ROUTE, reportEntity.centerRoute);
        values.put(EditRecordCollectionTable.KEY_REPORT_RECORD_STATUS, reportEntity.recordStatus);

        values.put(EditRecordCollectionTable.KEY_REPORT_FOREIGN_SEQUENCE_ID, reportEntity.foreignSequenceNum);
        values.put(EditRecordCollectionTable.KEY_REPORT_OLD_OR_NEW_FLAG, reportEntity.oldOrNewFlag);
        values.put(EditRecordCollectionTable.KEY_REPORT_EDITED_TIME, reportEntity.editedTime);

        values.put(EditRecordCollectionTable.KEY_TIPPING_START_TIME, reportEntity.tippingStartTime);
        values.put(EditRecordCollectionTable.KEY_TIPPING_END_TIME, reportEntity.tippingEndTime);
        values.put(EditRecordCollectionTable.KEY_REPORT_AGENT_ID, reportEntity.agentId);
        values.put(EditRecordCollectionTable.KEY_REPORT_MILKSTATUS_CODE, reportEntity.milkStatusCode);
        values.put(EditRecordCollectionTable.KEY_REPORT_RATE_CALC_DEVICE, reportEntity.rateCalculation);

        values.put(EditRecordCollectionTable.KEY_REPORT_MA_SERIAL, reportEntity.serialMa);
        values.put(EditRecordCollectionTable.KEY_REPORT_MA_NAME, reportEntity.maName);
        values.put(EditRecordCollectionTable.KEY_REPORT_SENT_STATUS, reportEntity.sentStatus);
        values.put(EditRecordCollectionTable.SMS_SENT_STATUS, reportEntity.sentSmsStatus);
        values.put(EditRecordCollectionTable.POST_DATE, reportEntity.postDate);
        values.put(EditRecordCollectionTable.POST_SHIFT, reportEntity.postShift);

        if (reportEntity.sequenceNum != 0) {
            values.put(EditRecordCollectionTable.SEQUENCE_NUMBER, reportEntity.sequenceNum);
        }

        return values;

    }


    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        ReportEntity reportEntity = new ReportEntity();
        try {
            reportEntity.columnId = cursor.getLong(0);
            reportEntity.farmerId = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_FARMERID));

            reportEntity.farmerName = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_NAME));
            reportEntity.snf = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_SNF));
            reportEntity.fat = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_FAT));
            reportEntity.user = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_USER));
            reportEntity.manual = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MANUAL));
         /*   reportEntity.shift = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_SHIFT));*/
            reportEntity.amount = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_AMOUNT));
            reportEntity.quantity = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_QUANTITY));
         /*   reportEntity.date = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_DATE));*/
            reportEntity.txnNumber = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_TAXNUM));
            reportEntity.milkType = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MILKTYPE));
            reportEntity.rate = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_RATE));
            reportEntity.lDate = cursor.getLong(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_LDATE));
            reportEntity.socId = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_SOCIETYID));


            reportEntity.bonus =
                    cursor.getDouble(cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_BONUS));

            reportEntity.time = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_TIME));
            reportEntity.miliTime = cursor.getLong(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_TIME_MILLI));
            reportEntity.awm = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_AWM));
            reportEntity.clr = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_CLR));
            reportEntity.status = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_STATUS));
            reportEntity.quantityMode = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_WEIGHTMANUAL));
            reportEntity.qualityMode = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MILKOMANUAL));
            reportEntity.milkAnalyserTime = cursor.getLong(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MILKANALYSERTIME));
            reportEntity.weighingTime = cursor.getLong(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_WEIGHINGTIME));
            reportEntity.temp = cursor.getLong(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_TEMP));
            reportEntity.recordCommited = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_COMMITED));
            reportEntity.collectionType = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_TYPE));
            reportEntity.milkQuality = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_MILKQUALITY));
            reportEntity.rateMode = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_RATEMODE));
            reportEntity.numberOfCans = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_NUMBER_OF_CANS));
            reportEntity.sampleNumber = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_SEQUENCE_NUMBER));
            reportEntity.centerRoute = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_ROUTE));
            reportEntity.recordStatus = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_RECORD_STATUS));

            reportEntity.kgWeight = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_KG_QTY));
            reportEntity.ltrsWeight = cursor.getDouble(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_LTR_QTY));

            reportEntity.tippingStartTime = cursor.getLong(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_TIPPING_START_TIME));
            reportEntity.tippingEndTime = cursor.getLong(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_TIPPING_END_TIME));
            reportEntity.user = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_USER));
            reportEntity.agentId = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_AGENT_ID));
            reportEntity.milkStatusCode = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MILKSTATUS_CODE));

            reportEntity.rateCalculation = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_RATE_CALC_DEVICE));

            reportEntity.serialMa = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MA_SERIAL));
            reportEntity.maName = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MA_NAME));

            reportEntity.oldOrNewFlag = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_OLD_OR_NEW_FLAG));
            reportEntity.foreignSequenceNum = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_FOREIGN_SEQUENCE_ID));
            reportEntity.editedTime = cursor.getInt(
                    cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_EDITED_TIME));

            reportEntity.postDate = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.POST_DATE));
            reportEntity.postShift = cursor.getString(
                    cursor.getColumnIndex(EditRecordCollectionTable.POST_SHIFT));
            reportEntity.sequenceNum = cursor.getLong(
                    cursor.getColumnIndex(EditRecordCollectionTable.SEQUENCE_NUMBER));


        } catch (Exception e) {
            e.printStackTrace();
        }
        return reportEntity;

    }

    @Override
    public Entity findByEntity(Entity entity) {
        ReportEntity re = (ReportEntity) entity;
        Entity e = findByProducerIdDateShiftAndEditedTime(
                re.farmerId, re.postDate, re.postShift, re.editedTime);
        return e;
    }

    public List<ReportEntity> findAllUnsentByCollectionType(String type) {
        Map<String, String> map = new HashMap<>();
        map.put(EditRecordCollectionTable.KEY_REPORT_TYPE, type);
        map.put(EditRecordCollectionTable.KEY_REPORT_SENT_STATUS, String.valueOf(CollectionConstants.UNSENT));
        return (List<ReportEntity>) findByKey(map);
    }

    public ArrayList<ReportEntity> findAllForReports(String[] mcc, long dateFrom, long dateTo,
                                                     String cattleType, String shift) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(EditRecordCollectionTable.KEY_REPORT_FARMERID, FilterType.IN), mcc);
        map.put(new Key(EditRecordCollectionTable.KEY_REPORT_LDATE, FilterType.BETWEEN), new String[]{String.valueOf(dateFrom), String.valueOf(dateTo)});
        map.put(new Key(EditRecordCollectionTable.KEY_REPORT_MILKTYPE, FilterType.EQUALS), cattleType);
        map.put(new Key(EditRecordCollectionTable.POST_SHIFT, FilterType.EQUALS), shift);
        map.put(new Key(EditRecordCollectionTable.KEY_REPORT_OLD_OR_NEW_FLAG, FilterType.EQUALS), "new");
        return (ArrayList<ReportEntity>) findByFilter(map);
    }

}
