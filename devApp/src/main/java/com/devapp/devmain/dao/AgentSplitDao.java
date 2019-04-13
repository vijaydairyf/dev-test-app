package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.agentfarmersplit.AgentSplitEntity;
import com.devapp.devmain.agentfarmersplit.QualityParams;
import com.devapp.devmain.agentfarmersplit.QuantityParams;
import com.devapp.devmain.agentfarmersplit.RateParams;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.tableEntities.SplitRecordTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.devapp.smartcc.database.AgentDatabase.getRateCalculated;

/**
 * Created by u_pendra on 4/2/18.
 */

public class AgentSplitDao extends Dao {


    AgentSplitDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, SplitRecordTable.TABLE);
    }


    @Override
    String getEntityIdColumnName() {

        return SplitRecordTable.COLUMNID;
    }


    public ArrayList<AgentSplitEntity> findByDateAndShift(Date date, String shift) {
        return null;
    }

    public ArrayList<AgentSplitEntity> findByProducerIdDateAndShift(String producerId, Date date, String shift) {
        return null;
    }


    //TODO Convert the date format to yyyy-mm-dd format

    public AgentSplitEntity findByProducerIdDateShiftAndCollTime(
            String producerId, Date date, String shift, long collectionTime) {

        return findByProducerIdDateShiftAndCollTime(producerId, date.toString(), shift, collectionTime);


    }


    private AgentSplitEntity findByProducerIdDateShiftAndCollTime(
            String producerId, String date, String shift, long collectionTime) {

        String selectQuery = "SELECT * FROM " + SplitRecordTable.TABLE + " WHERE " +
                SplitRecordTable.FARMERID + " ='" + producerId + "' AND " +
                SplitRecordTable.POST_DATE + " ='" + date + "'" + " AND " +
                SplitRecordTable.POST_SHIFT + " ='" + shift + "'" + " AND " +
                SplitRecordTable.COLLECTION_TIME + " = " + collectionTime;
        AgentSplitEntity fse = null;

        ArrayList<AgentSplitEntity> farmerSplitEntries =
                (ArrayList<AgentSplitEntity>) findByQuery(selectQuery);

        if (farmerSplitEntries.size() > 0) {
            fse = farmerSplitEntries.get(0);
        }

        return fse;
    }


    @Override
    Entity getEntityFromCursor(Cursor cursor) {


        AgentSplitEntity splitCollectionEntity = new AgentSplitEntity();

        splitCollectionEntity.columnId = cursor.getInt(
                cursor.getColumnIndex(SplitRecordTable.COLUMNID));
        splitCollectionEntity.agentId = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.AGENT_ID));
        splitCollectionEntity.milkType = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.MILKTYPE));
        splitCollectionEntity.centerId = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.SOCIETYID));
        splitCollectionEntity.collectionTime = cursor.getLong(
                cursor.getColumnIndex(SplitRecordTable.COLLECTION_TIME));
        splitCollectionEntity.date = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.DATE));
        splitCollectionEntity.producerId = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.FARMERID));
        splitCollectionEntity.sent = cursor.getInt(cursor.getColumnIndex(SplitRecordTable.SENT));
        splitCollectionEntity.shift = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.SHIFT));
        splitCollectionEntity.status = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.RECORD_STATUS));
        splitCollectionEntity.collectionType = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.TYPE));
        splitCollectionEntity.mode = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.ENTRY_MODE));
        splitCollectionEntity.parentSeqNum = cursor.getInt(cursor.getColumnIndex(
                SplitRecordTable.PARENT_SEQ_NUM));


        splitCollectionEntity.qualityParams = getQualityParamsFromCursor(cursor);
        splitCollectionEntity.quantityParams = getQuantityParamsFromCursor(cursor);
        splitCollectionEntity.rateParams = getRateParamsFromCursor(cursor);

          /*  splitCollectionEntity.seqNum = Util.getSampleNumber(mContext,
                    cursor.getInt(0), Util.REPORT_TYPE_AGENT_SPLIT);*/

        splitCollectionEntity.commited = cursor.getInt(
                cursor.getColumnIndex(SplitRecordTable.COMMITED));
        splitCollectionEntity.userId = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.USER));

        splitCollectionEntity.postDate = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.POST_DATE));
        splitCollectionEntity.postShift = cursor.getString(
                cursor.getColumnIndex(SplitRecordTable.POST_SHIFT));
        splitCollectionEntity.smsSent = cursor.getInt(
                cursor.getColumnIndex(SplitRecordTable.SMS_SENT_STATUS));

        splitCollectionEntity.sequenceNumber = cursor.getLong(
                cursor.getColumnIndex(SplitRecordTable.SEQUENCE_NUMBER));
        splitCollectionEntity.seqNum = cursor.getInt(
                cursor.getColumnIndex(SplitRecordTable.SEQUENCE_NUMBER));


        return splitCollectionEntity;
    }


    @Override
    ContentValues getContentValues(Entity entity) {

        AgentSplitEntity agentSplitEntity = null;

        if (entity instanceof AgentSplitEntity) {

            agentSplitEntity = (AgentSplitEntity) entity;
        } else {
            return null;
        }

        ContentValues contentValues = new ContentValues();

        contentValues = getSplitMainContentValue(contentValues, agentSplitEntity);
        contentValues = getMAContentValue(contentValues, agentSplitEntity.qualityParams);
        contentValues = getWSContentValue(contentValues, agentSplitEntity.quantityParams);
        contentValues = getRateContentValue(contentValues, agentSplitEntity.rateParams);


        return contentValues;
    }

    /**
     * total 17 items
     *
     * @param contentValues
     * @param splitCollectionEntity
     * @return
     */

    private ContentValues getSplitMainContentValue(ContentValues contentValues,
                                                   AgentSplitEntity splitCollectionEntity) {

        contentValues.put(SplitRecordTable.AGENT_ID, splitCollectionEntity.agentId);
        contentValues.put(SplitRecordTable.COLLECTION_TIME, splitCollectionEntity.collectionTime);
        contentValues.put(SplitRecordTable.COMMITED, splitCollectionEntity.commited);
        contentValues.put(SplitRecordTable.ENTRY_MODE, splitCollectionEntity.mode);
        contentValues.put(SplitRecordTable.FARMERID, splitCollectionEntity.producerId);
        contentValues.put(SplitRecordTable.MANUAL, splitCollectionEntity.mode);
//        contentValues.put(SplitRecordTable.MILKQUALITY,splitCollectionEntity.milkQuality);
//        contentValues.put(SplitRecordTable.MILKSTATUS_CODE,splitCollectionEntity.milkStatusCode);
        contentValues.put(SplitRecordTable.MILKTYPE, splitCollectionEntity.milkType);
        contentValues.put(SplitRecordTable.NUMBER_OF_CANS, splitCollectionEntity.numberOfCans);
        contentValues.put(SplitRecordTable.RECORD_STATUS, splitCollectionEntity.status);

        contentValues.put(SplitRecordTable.SHIFT, splitCollectionEntity.shift);
        contentValues.put(SplitRecordTable.SOCIETYID, splitCollectionEntity.centerId);
        contentValues.put(SplitRecordTable.TYPE, splitCollectionEntity.collectionType);
        contentValues.put(SplitRecordTable.USER, splitCollectionEntity.userId);
        contentValues.put(SplitRecordTable.SENT, splitCollectionEntity.sent);
        contentValues.put(SplitRecordTable.DATE, splitCollectionEntity.date);
        contentValues.put(SplitRecordTable.PARENT_SEQ_NUM, splitCollectionEntity.parentSeqNum);

        contentValues.put(SplitRecordTable.SMS_SENT_STATUS, splitCollectionEntity.smsSent);
        contentValues.put(SplitRecordTable.POST_DATE, splitCollectionEntity.postDate);
        contentValues.put(SplitRecordTable.POST_SHIFT, splitCollectionEntity.postShift);

        contentValues.put(SplitRecordTable.SEQUENCE_NUMBER, splitCollectionEntity.seqNum);


        return contentValues;

    }

    /**
     * total 23 items
     *
     * @param contentValues
     * @param qualityParams
     * @return
     */

    private ContentValues getMAContentValue(ContentValues contentValues, QualityParams qualityParams) {

        contentValues.put(DatabaseEntity.QualityParams.LACTOSE, qualityParams.lactose);
        contentValues.put(DatabaseEntity.QualityParams.ADDED_WATER, qualityParams.awm);
        contentValues.put(DatabaseEntity.QualityParams.ALCOHOL, qualityParams.alcohol);
        contentValues.put(DatabaseEntity.QualityParams.CALIBRATION, qualityParams.calibration);
        contentValues.put(DatabaseEntity.QualityParams.CLR, qualityParams.clr);
        contentValues.put(DatabaseEntity.QualityParams.COM, qualityParams.com);
        contentValues.put(DatabaseEntity.QualityParams.DENSITY, qualityParams.density);
        contentValues.put(DatabaseEntity.QualityParams.FAT, qualityParams.fat);
        contentValues.put(DatabaseEntity.QualityParams.FREEZING_POINT, qualityParams.freezingPoint);
        contentValues.put(DatabaseEntity.QualityParams.SALT, qualityParams.salt);
        contentValues.put(DatabaseEntity.QualityParams.SNF, qualityParams.snf);
        contentValues.put(DatabaseEntity.QualityParams.MA_SERIAL_NUM, qualityParams.maSerialNumber);
        contentValues.put(DatabaseEntity.QualityParams.TEMP, qualityParams.temperature);
        contentValues.put(DatabaseEntity.QualityParams.QUALITY_MODE, qualityParams.qualityMode);
        contentValues.put(DatabaseEntity.QualityParams.QUALITY_TIME, qualityParams.qualityTime);
        contentValues.put(DatabaseEntity.QualityParams.QUALITY_START_TIME, qualityParams.qualityStartTime);
        contentValues.put(DatabaseEntity.QualityParams.QUALITY_END_TIME, qualityParams.qualityEndTime);
        contentValues.put(DatabaseEntity.QualityParams.PH, qualityParams.pH);
        contentValues.put(DatabaseEntity.QualityParams.PROTEIN, qualityParams.protein);
        contentValues.put(DatabaseEntity.QualityParams.MILKSTATUS_CODE, qualityParams.milkStatusCode);
        contentValues.put(DatabaseEntity.QualityParams.MILKQUALITY, qualityParams.milkQuality);

        contentValues.put(DatabaseEntity.QualityParams.MA_NUMBER, qualityParams.maNumber);
        contentValues.put(DatabaseEntity.QualityParams.MA_NAME, qualityParams.maName);

        return contentValues;
    }

    /**
     * total 8 items
     *
     * @param contentValues
     * @param quantityParams
     * @return
     */

    private ContentValues getWSContentValue(ContentValues contentValues, QuantityParams quantityParams) {

        contentValues.put(DatabaseEntity.QuantityParams.KG_QUANTITY, quantityParams.kgQuantity);
        contentValues.put(DatabaseEntity.QuantityParams.LTR_QUANTITY, quantityParams.ltrQuantity);
        contentValues.put(DatabaseEntity.QuantityParams.MEASUREMENT_UNIT, quantityParams.measurementUnit);
        contentValues.put(DatabaseEntity.QuantityParams.QUANTITY_MODE, quantityParams.quantityMode);
        contentValues.put(DatabaseEntity.QuantityParams.QUANTIY_TIME, quantityParams.quantityTime);
        contentValues.put(DatabaseEntity.QuantityParams.TIPPING_START_TIME, quantityParams.tippingStartTime);
        contentValues.put(DatabaseEntity.QuantityParams.TIPPING_END_TIME, quantityParams.tippingEndTime);
        contentValues.put(DatabaseEntity.QuantityParams.WEIGHING_QUANTITY, quantityParams.weighingQuantity);

        return contentValues;
    }

    /**
     * total 8 items
     *
     * @param contentValues
     * @param rateParams
     * @return
     */
    private ContentValues getRateContentValue(ContentValues contentValues, RateParams rateParams) {


        contentValues.put(DatabaseEntity.RateParams.RATE_MODE, rateParams.rateMode);
        contentValues.put(DatabaseEntity.RateParams.AMOUNT, rateParams.amount);
        contentValues.put(DatabaseEntity.RateParams.BONUS, rateParams.bonus);
        contentValues.put(DatabaseEntity.RateParams.INCENTIVE, rateParams.incentive);
        contentValues.put(DatabaseEntity.RateParams.RATE, rateParams.rate);
        contentValues.put(DatabaseEntity.RateParams.RATE_CAL_FROM_DEVICE,
                getRateCalculated(rateParams.isRateCalculated));
        contentValues.put(DatabaseEntity.RateParams.RATE_CHART_NAME, rateParams.rateChartName);
        contentValues.put(DatabaseEntity.RateParams.RATE_UNIT, rateParams.rateUnit);
        return contentValues;
    }


    public QualityParams getQualityParamsFromCursor(Cursor cursor) {

        //MA parameters

        QualityParams qualityParams = new QualityParams();
        qualityParams.pH = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.PH));
        qualityParams.salt = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.SALT));
        qualityParams.fat = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.FAT));
        qualityParams.snf = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.SNF));
        qualityParams.clr = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.CLR));
        qualityParams.awm = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.ADDED_WATER));
        qualityParams.alcohol = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.ALCOHOL));

        qualityParams.density = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.DENSITY));
        qualityParams.salt = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.SALT));
        qualityParams.lactose = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.LACTOSE));
        qualityParams.freezingPoint = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.FREEZING_POINT));

        qualityParams.temperature = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.TEMP));
        qualityParams.protein = cursor.getDouble(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.PROTEIN));

        qualityParams.qualityMode = cursor.getString(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.QUALITY_MODE));
        qualityParams.com = cursor.getString(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.COM));

        qualityParams.calibration = cursor.getString(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.CALIBRATION));
        qualityParams.qualityEndTime = cursor.getLong(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.QUALITY_END_TIME));
        qualityParams.qualityStartTime = cursor.getLong(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.QUALITY_START_TIME));
        qualityParams.qualityTime = cursor.getLong(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.QUALITY_TIME));

        qualityParams.maSerialNumber = cursor.getString(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.MA_SERIAL_NUM));

        qualityParams.milkStatusCode = cursor.getInt(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.MILKSTATUS_CODE));
        qualityParams.milkQuality = cursor.getString(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.MILKQUALITY));
        qualityParams.maName = cursor.getString(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.MA_NAME));
        qualityParams.maNumber = cursor.getString(
                cursor.getColumnIndex(DatabaseEntity.QualityParams.MA_NUMBER));


        return qualityParams;

    }

    public QuantityParams getQuantityParamsFromCursor(Cursor cursor) {
        //WS entries

        QuantityParams quantityParams = new QuantityParams();

        quantityParams.weighingQuantity = cursor.getDouble(cursor.getColumnIndex(
                DatabaseEntity.QuantityParams.WEIGHING_QUANTITY));
        quantityParams.kgQuantity = cursor.getDouble(cursor.getColumnIndex(
                DatabaseEntity.QuantityParams.KG_QUANTITY));
        quantityParams.ltrQuantity = cursor.getDouble(cursor.getColumnIndex(
                DatabaseEntity.QuantityParams.LTR_QUANTITY));
        quantityParams.quantityMode = cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.QuantityParams.QUANTITY_MODE));
        quantityParams.tippingStartTime = cursor.getLong(cursor.getColumnIndex(
                DatabaseEntity.QuantityParams.TIPPING_START_TIME));
        quantityParams.tippingEndTime = cursor.getLong(cursor.getColumnIndex(
                DatabaseEntity.QuantityParams.TIPPING_END_TIME));
        quantityParams.measurementUnit = cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.QuantityParams.MEASUREMENT_UNIT));
        quantityParams.quantityTime = cursor.getLong(cursor.getColumnIndex(
                DatabaseEntity.QuantityParams.QUANTIY_TIME));

        return quantityParams;

    }

    public RateParams getRateParamsFromCursor(Cursor cursor) {

        RateParams rateParams = new RateParams();

        rateParams.amount = cursor.getLong(cursor.getColumnIndex(
                DatabaseEntity.RateParams.AMOUNT));
        rateParams.rate = cursor.getLong(cursor.getColumnIndex(
                DatabaseEntity.RateParams.RATE));
        rateParams.rateUnit = cursor.getString(cursor.getColumnIndex(
                DatabaseEntity.RateParams.RATE_UNIT));
        rateParams.bonus = cursor.getDouble(cursor.getColumnIndex(
                DatabaseEntity.RateParams.BONUS));
        rateParams.incentive = cursor.getDouble(cursor.getColumnIndex(
                DatabaseEntity.RateParams.INCENTIVE));

        return rateParams;
    }

    public List<AgentSplitEntity> findAllUnsent() {
        Map<String, String> map = new HashMap<>();
        map.put(SplitRecordTable.SENT, String.valueOf(CollectionConstants.UNSENT));
        return (List<AgentSplitEntity>) findByKey(map);

    }

    public ArrayList<AgentSplitEntity> findAllForReports(String[] mcc, String dateFrom, String dateTo,
                                                         String cattleType, String shift,
                                                         String recordCommitStatus) {
        Map<Key, Object> map = new LinkedHashMap<>();
        map.put(new Key(SplitRecordTable.FARMERID, FilterType.IN), mcc);
        map.put(new Key(SplitRecordTable.COLLECTION_TIME, FilterType.BETWEEN), new String[]{dateFrom, dateTo});
        map.put(new Key(SplitRecordTable.MILKTYPE, FilterType.EQUALS), cattleType);
        map.put(new Key(SplitRecordTable.SHIFT, FilterType.EQUALS), shift);
        map.put(new Key(SplitRecordTable.COMMITED, FilterType.EQUALS), recordCommitStatus);
        map.put(new Key(SplitRecordTable.MILKTYPE, FilterType.ORDER_BY), FilterType.Order.ASC);
        map.put(new Key(SplitRecordTable.FARMERID, FilterType.ORDER_BY), FilterType.Order.DESC);

        return (ArrayList<AgentSplitEntity>) findByFilter(map);
    }

}
