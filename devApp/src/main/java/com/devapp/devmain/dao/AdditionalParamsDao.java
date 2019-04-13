package com.devapp.devmain.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.additionalRecords.AdditionalParamsEntity;
import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.tableEntities.ExtraParams;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by u_pendra on 4/2/18.
 */

public class AdditionalParamsDao extends Dao {


    AdditionalParamsDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, ExtraParams.TABLE_EXTRA_PARAMS);
    }

    @Override
    Entity getEntityFromCursor(Cursor cursor) {
        AdditionalParamsEntity ape = new AdditionalParamsEntity();
        // ape.columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));

        Long columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));
        ape.columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));
        ape.key = cursor.getString(cursor.getColumnIndex(ExtraParams.KEY));
        ape.extraParameters = cursor.getString(cursor.getColumnIndex(ExtraParams.VALUE));
        ape.refSeqNum = cursor.getLong(cursor.getColumnIndex(ExtraParams.REF_SEQ_NUM));
        // ape.collectionType = cursor.getString(cursor.getColumnIndex(ExtraParams.COLL_TYPE));
        ape.updatedTime = cursor.getLong(cursor.getColumnIndex(ExtraParams.TIME));
        //ape.committed = cursor.getInt(cursor.getColumnIndex(ExtraParams.COMMITTED));
        ape.sentStatus = cursor.getInt(cursor.getColumnIndex(ExtraParams.SENT_STATUS));
        ape.collectionTime = cursor.getInt(cursor.getColumnIndex(ExtraParams.COLLECTION_TIME));
        ape.smsSentStatus = cursor.getInt(cursor.getColumnIndex(ExtraParams.SMS_SENT_STATUS));
        ape.seqNum = cursor.getLong(cursor.getColumnIndex(ExtraParams.SEQUENCE_NUMBER));
        ape.postDate = cursor.getString(cursor.getColumnIndex(ExtraParams.POST_DATE));
        ape.postShift = cursor.getString(cursor.getColumnIndex(ExtraParams.POST_SHIFT));

        return ape;
    }

    @Override
    String getEntityIdColumnName() {
        return ExtraParams.COLUMN_ID;
    }


    public ArrayList<AdditionalParamsEntity> findByDateAndShift(Date date, String shift) {
        return null;
    }

    public ArrayList<AdditionalParamsEntity> findByProducerIdDateAndShift(String producerId, Date date, String shift) {
        return null;
    }


    //TODO Convert the date format to yyyy-mm-dd format

    public AdditionalParamsEntity findByProducerIdDateShiftAndCollTime(
            String producerId, Date date, String shift, long collectionTime) {

        return findByProducerIdDateShiftAndCollTime(producerId, date.toString(), shift, collectionTime);


    }


    private AdditionalParamsEntity findByProducerIdDateShiftAndCollTime(
            String producerId, String date, String shift, long collectionTime) {

        String selectQuery = "SELECT * FROM " + ExtraParams.TABLE_EXTRA_PARAMS + " WHERE " +
                ExtraParams.KEY + " ='" + producerId + "' AND " +
                ExtraParams.POST_DATE + " ='" + date + "'" + " AND " +
                ExtraParams.POST_SHIFT + " ='" + shift + "'" + " AND " +
                ExtraParams.COLLECTION_TIME + " = " + collectionTime;
        AdditionalParamsEntity ape = null;

        ArrayList<AdditionalParamsEntity> extraParamEntities = (ArrayList<AdditionalParamsEntity>) findByQuery(selectQuery);

        if (extraParamEntities.size() > 0) {
            ape = extraParamEntities.get(0);
        }

        return ape;
    }


    public AdditionalParamsEntity findByReferenceSequenceNumber(long seqNum) {
        String query = "SELECT * FROM " + ExtraParams.TABLE_EXTRA_PARAMS + " WHERE "
                + ExtraParams.REF_SEQ_NUM + " = " + seqNum
                + " ORDER BY " + ExtraParams.TIME + " DESC "
                + " LIMIT 1";
        ArrayList<AdditionalParamsEntity> additionalParamsEntities =
                (ArrayList<AdditionalParamsEntity>) findByQuery(query);

        AdditionalParamsEntity ape = null;
        if (additionalParamsEntities.size() > 0) {
            ape = additionalParamsEntities.get(0);

        }

        return ape;
    }


    @Override
    ContentValues getContentValues(Entity entity) {

        AdditionalParamsEntity additionalParamsEntity = null;

        if (entity instanceof AdditionalParamsEntity) {

            additionalParamsEntity = (AdditionalParamsEntity) entity;
        } else {
            return null;
        }


        ContentValues contentValues = new ContentValues();
        contentValues.put(ExtraParams.KEY, additionalParamsEntity.key);
        contentValues.put(ExtraParams.COLLECTION_TIME, additionalParamsEntity.collectionTime);
        contentValues.put(ExtraParams.POST_DATE, additionalParamsEntity.date);
        contentValues.put(ExtraParams.POST_SHIFT, additionalParamsEntity.shift);
        contentValues.put(ExtraParams.VALUE, additionalParamsEntity.extraParameters);
        contentValues.put(ExtraParams.REF_SEQ_NUM, additionalParamsEntity.refSeqNum);
        //   contentValues.put(ExtraParams.COLL_TYPE, additionalParamsEntity.collectionType);
        contentValues.put(ExtraParams.TIME, additionalParamsEntity.updatedTime);
        // contentValues.put(ExtraParams.COMMITTED, additionalParamsEntity.committed);
        contentValues.put(ExtraParams.SENT_STATUS, additionalParamsEntity.sentStatus);

        contentValues.put(ExtraParams.SMS_SENT_STATUS, additionalParamsEntity.smsSentStatus);
        contentValues.put(ExtraParams.POST_SHIFT, additionalParamsEntity.postShift);
        contentValues.put(ExtraParams.POST_DATE, additionalParamsEntity.postDate);
        contentValues.put(ExtraParams.SEQUENCE_NUMBER, additionalParamsEntity.seqNum);
        return contentValues;
    }


    //TODO collectionTime,milkType,date,shift

    private AdditionalParamsEntity getExtraParamFromCursor(Cursor cursor) {
        AdditionalParamsEntity ape = new AdditionalParamsEntity();
        // ape.columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));

        Long columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));
        ape.columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));
        ape.key = cursor.getString(cursor.getColumnIndex(ExtraParams.KEY));
        ape.extraParameters = cursor.getString(cursor.getColumnIndex(ExtraParams.VALUE));
        ape.refSeqNum = cursor.getLong(cursor.getColumnIndex(ExtraParams.REF_SEQ_NUM));
        // ape.collectionType = cursor.getString(cursor.getColumnIndex(ExtraParams.COLL_TYPE));
        ape.updatedTime = cursor.getLong(cursor.getColumnIndex(ExtraParams.TIME));
        //ape.committed = cursor.getInt(cursor.getColumnIndex(ExtraParams.COMMITTED));
        ape.sentStatus = cursor.getInt(cursor.getColumnIndex(ExtraParams.SENT_STATUS));


        ape.collectionTime = cursor.getInt(cursor.getColumnIndex(ExtraParams.COLLECTION_TIME));
        ape.smsSentStatus = cursor.getInt(cursor.getColumnIndex(ExtraParams.SMS_SENT_STATUS));
        ape.seqNum = cursor.getLong(cursor.getColumnIndex(ExtraParams.SEQUENCE_NUMBER));
        ape.postDate = cursor.getString(cursor.getColumnIndex(ExtraParams.POST_DATE));
        ape.postShift = cursor.getString(cursor.getColumnIndex(ExtraParams.POST_SHIFT));

        return ape;
    }

    public List<AdditionalParamsEntity> findAllUnsent() {
        Map<String, String> map = new HashMap<>();
        map.put(ExtraParams.SENT_STATUS, String.valueOf(CollectionConstants.UNSENT));
        return (List<AdditionalParamsEntity>) findByKey(map);
    }

}
