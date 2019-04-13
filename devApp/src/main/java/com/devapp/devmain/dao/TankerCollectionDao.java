package com.devapp.devmain.dao;

/**
 * Created by u_pendra on 4/2/18.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.Entity;
import com.devapp.devmain.milkline.entities.TankerCollectionEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.tableEntities.TankerCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by u_pendra on 4/2/18.
 */


public class TankerCollectionDao extends Dao {


    TankerCollectionDao(SQLiteDatabase sqLiteDatabase) {
        super(sqLiteDatabase, TankerCollectionTable.TABLE_TANKER);
    }


    @Override
    String getEntityIdColumnName() {
        return TankerCollectionTable.KEY_COLUMN_ID;
    }


    @Override
    Entity getEntityFromCursor(Cursor cursor) {

        TankerCollectionEntity tankerCollectionEntity = new TankerCollectionEntity();

        String compNumbers = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_COMPARTMENT_NUMBERS));
        String[] compNumber = compNumbers.split(SmartCCConstants.LIST_SEPARATOR);
        tankerCollectionEntity.compartmentNumbers = new ArrayList<>();

        for (int i = 0; i < compNumber.length; i++) {
            tankerCollectionEntity.compartmentNumbers.add(compNumber[i]);

        }
        tankerCollectionEntity.colId = cursor.getLong(cursor.getColumnIndex(TankerCollectionTable.KEY_COLUMN_ID));
        tankerCollectionEntity.collectionTime = cursor.getLong(cursor.getColumnIndex(TankerCollectionTable.KEY_COLLECTION_TIME));
        tankerCollectionEntity.alcohol = cursor.getDouble(cursor.getColumnIndex(TankerCollectionTable.KEY_ALCHOLOL));
        tankerCollectionEntity.centerId = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_BULK_COLLER_CODE));
        tankerCollectionEntity.fat = cursor.getDouble(cursor.getColumnIndex(TankerCollectionTable.KEY_FAT));
        tankerCollectionEntity.snf = cursor.getDouble(cursor.getColumnIndex(TankerCollectionTable.KEY_SNF));
        tankerCollectionEntity.quantity = cursor.getDouble(cursor.getColumnIndex(TankerCollectionTable.KEY_QUANTITY));
        tankerCollectionEntity.routeNumber = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_ROUTE_NUMER));
        tankerCollectionEntity.supervisorCode = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_SUPERVISOR_CODE));
        tankerCollectionEntity.routeNumber = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_ROUTE_NUMER));
        tankerCollectionEntity.tankerNumber = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_TANKER_NUMBER));
        tankerCollectionEntity.status = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_STATUS));
        tankerCollectionEntity.comments = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_COMMENTS));

        tankerCollectionEntity.quantityUnit = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_QUANTITY_UNIT));
        tankerCollectionEntity.sent = cursor.getInt(cursor.getColumnIndex(TankerCollectionTable.KEY_SENT));
        tankerCollectionEntity.temperature = cursor.getDouble(cursor.getColumnIndex(TankerCollectionTable.KEY_TEMP));
        tankerCollectionEntity.clr = cursor.getDouble(cursor.getColumnIndex(TankerCollectionTable.KEY_CLR));

        tankerCollectionEntity.seqNum = cursor.getLong(cursor.getColumnIndex(TankerCollectionTable.SEQUENCE_NUMBER));
        tankerCollectionEntity.postDate = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.POST_DATE));
        tankerCollectionEntity.postShift = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.POST_SHIFT));

        return tankerCollectionEntity;
    }

    @Override
    ContentValues getContentValues(Entity entity) {

        TankerCollectionEntity tankerCollectionEntity = null;

        if (entity instanceof TankerCollectionEntity) {

            tankerCollectionEntity = (TankerCollectionEntity) entity;
        } else {
            return null;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(TankerCollectionTable.KEY_ALCHOLOL, tankerCollectionEntity.alcohol);
        contentValues.put(TankerCollectionTable.KEY_BULK_COLLER_CODE, tankerCollectionEntity.centerId);
        contentValues.put(TankerCollectionTable.KEY_COLLECTION_TIME, tankerCollectionEntity.collectionTime);

        String compNumbers = "";
        if (tankerCollectionEntity.compartmentNumbers != null && tankerCollectionEntity.compartmentNumbers.size() > 0) {
            for (int i = 0; i < tankerCollectionEntity.compartmentNumbers.size(); i++) {
                compNumbers = compNumbers + tankerCollectionEntity.compartmentNumbers.get(i) +
                        SmartCCConstants.LIST_SEPARATOR;
            }
        }
        contentValues.put(TankerCollectionTable.KEY_COMPARTMENT_NUMBERS, compNumbers);
        contentValues.put(TankerCollectionTable.KEY_FAT, tankerCollectionEntity.fat);
        contentValues.put(TankerCollectionTable.KEY_SNF, tankerCollectionEntity.snf);
        contentValues.put(TankerCollectionTable.KEY_QUANTITY, tankerCollectionEntity.quantity);
        contentValues.put(TankerCollectionTable.KEY_ROUTE_NUMER, tankerCollectionEntity.routeNumber);
        contentValues.put(TankerCollectionTable.KEY_SENT, CollectionConstants.UNSENT);
        contentValues.put(TankerCollectionTable.KEY_SUPERVISOR_CODE, tankerCollectionEntity.supervisorCode);
        contentValues.put(TankerCollectionTable.KEY_TANKER_NUMBER, tankerCollectionEntity.tankerNumber);
        contentValues.put(TankerCollectionTable.KEY_STATUS, tankerCollectionEntity.status);
        contentValues.put(TankerCollectionTable.KEY_COMMENTS, tankerCollectionEntity.comments);
        contentValues.put(TankerCollectionTable.KEY_QUANTITY_UNIT, tankerCollectionEntity.quantityUnit);
        contentValues.put(TankerCollectionTable.KEY_TEMP, tankerCollectionEntity.temperature);
        contentValues.put(TankerCollectionTable.KEY_CLR, tankerCollectionEntity.clr);


        contentValues.put(TankerCollectionTable.KEY_SENT, CollectionConstants.UNSENT);
        contentValues.put(TankerCollectionTable.SMS_SENT_STATUS, CollectionConstants.UNSENT);
        contentValues.put(TankerCollectionTable.POST_DATE, tankerCollectionEntity.postDate);
        contentValues.put(TankerCollectionTable.POST_SHIFT, tankerCollectionEntity.postShift);
        contentValues.put(TankerCollectionTable.SEQUENCE_NUMBER, tankerCollectionEntity.sequenceNum);

        return contentValues;


    }


    public ArrayList<TankerCollectionEntity> findByDateAndShift(Date date, String shift) {
        return null;
    }

    public ArrayList<TankerCollectionEntity> findByProducerIdDateAndShift(String producerId, Date date, String shift) {
        return null;
    }


    //TODO Convert the date format to yyyy-mm-dd format

    public TankerCollectionEntity findByProducerIdDateShiftAndCollTime(
            String producerId, Date date, String shift, long collectionTime) {

        return findByProducerIdDateShiftAndCollTime(producerId, date.toString(), shift, collectionTime);


    }


    private TankerCollectionEntity findByProducerIdDateShiftAndCollTime(
            String producerId, String date, String shift, long collectionTime) {

        String selectQuery = "SELECT * FROM " + TankerCollectionTable.TABLE_TANKER + " WHERE " +
                TankerCollectionTable.KEY_TANKER_NUMBER + " ='" + producerId + "' AND " +
                TankerCollectionTable.POST_DATE + " ='" + date + "'" + " AND " +
                TankerCollectionTable.POST_SHIFT + " ='" + shift + "'" + " AND " +
                TankerCollectionTable.KEY_COLLECTION_TIME + " = " + collectionTime;
        TankerCollectionEntity tankerCollectionEntity = null;

        ArrayList<TankerCollectionEntity> reportsEntities = (ArrayList<TankerCollectionEntity>) findByQuery(selectQuery);

        if (reportsEntities.size() > 0) {
            tankerCollectionEntity = reportsEntities.get(0);
        }

        return tankerCollectionEntity;
    }

    public List<TankerCollectionEntity> findAllUnsent() {
        Map<String, String> map = new HashMap<>();
        map.put(TankerCollectionTable.KEY_SENT, String.valueOf(CollectionConstants.UNSENT));
        return (List<TankerCollectionEntity>) findByKey(map);
    }


}
