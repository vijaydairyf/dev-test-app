package com.devapp.devmain.milkline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.dbbackup.DatabaseSync;
import com.devapp.devmain.milkline.entities.TankerCollectionEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.TankerCollectionTable;
import com.devapp.smartcc.database.TruckCCDatabase;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/12/16.
 */

public class TankerDatabase {

    public static final int DAY_FOR_PURGE = 120;
    public static String TABLE_TANKER = "tankerTable";
    public static String KEY_TANKER_NUMBER = "tankerNumber";
    public static String KEY_COLUMN_ID = "_id";
    public static String KEY_SUPERVISOR_CODE = "supervisorCode";
    public static String KEY_ROUTE_NUMER = "routeNumber";
    public static String KEY_BULK_COLLER_CODE = "bulkcoolercode";
    public static String KEY_QUANTITY = "quantity";
    public static String KEY_FAT = "fat";
    public static String KEY_SNF = "snf";
    public static String KEY_COMPARTMENT_NUMBERS = "compartmentNumbers";
    public static String KEY_ALCHOLOL = "alchohol";
    public static String KEY_SENT = "sent";
    public static String KEY_COLLECTION_TIME = "collectionTime";
    public static String KEY_STATUS = "status";
    public static String KEY_COMMENTS = "comments";
    public static String KEY_QUANTITY_UNIT = "quantityUnit";
    public static String KEY_TEMP = "temp";
    public static String KEY_CLR = "clr";
    SQLiteDatabase primaryDatabase;
    DatabaseSync databaseSync;
    Context mContext;
    AmcuConfig amcuConfig;

    DatabaseHandler databaseHandler;


    public TankerDatabase(Context context, SQLiteDatabase sqliteDatabase, DatabaseSync
            databaseSync) {
        this.mContext = context;
        this.primaryDatabase = sqliteDatabase;
        this.databaseSync = databaseSync;

        amcuConfig = AmcuConfig.getInstance();
        databaseHandler = DatabaseHandler.getDatabaseInstance();
    }

    public TankerDatabase() {
    }


    public static String createQuery() {
        String createQuery = TankerCollectionTable.CREATE_TANKER_QUERY;
        return createQuery;
    }

    public static String getAlterQueryForConfigurationTable(String column,
                                                            double defaultValue) {
        return "ALTER " + "TABLE " + TABLE_TANKER
                + " ADD " + "COLUMN "
                + column + " DOUBLE" + " DEFAULT "
                + defaultValue;
    }

    public static TankerCollectionEntity getTankerEntityFromCrusorV2(Cursor cursor) {
        TankerCollectionEntity tankerCollectionEntity = new TankerCollectionEntity();

        String compNumbers = cursor.getString(cursor.getColumnIndex(KEY_COMPARTMENT_NUMBERS));
        String[] compNumber = compNumbers.split(SmartCCConstants.LIST_SEPARATOR);
        tankerCollectionEntity.compartmentNumbers = new ArrayList<>();

        for (int i = 0; i < compNumber.length; i++) {
            tankerCollectionEntity.compartmentNumbers.add(compNumber[i]);

        }
        tankerCollectionEntity.colId = cursor.getLong(cursor.getColumnIndex(KEY_COLUMN_ID));
        tankerCollectionEntity.collectionTime = cursor.getLong(cursor.getColumnIndex(KEY_COLLECTION_TIME));

        return tankerCollectionEntity;
    }

    public ArrayList<TankerCollectionEntity> getAllTankerEntities() {
        String query = "Select * from " + TABLE_TANKER +
                " ORDER BY " + KEY_COLLECTION_TIME + " DESC ";
        ArrayList<TankerCollectionEntity> allTankerCollectionEntity = new ArrayList<>();
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TankerCollectionEntity tankerCollectionEntity = getTankerEntityFromCrusor(cursor);
                allTankerCollectionEntity.add(tankerCollectionEntity);
            } while (cursor.moveToNext());
        }
        return allTankerCollectionEntity;

    }

    public ArrayList<TankerCollectionEntity> getAllTankerEntitiesBetweenDates(long startDate, long endDate) {
        String query = "Select * from " + TABLE_TANKER + " where " + KEY_COLLECTION_TIME + "<=" + endDate
                + " AND " + KEY_COLLECTION_TIME + ">" + startDate;

        ArrayList<TankerCollectionEntity> allTankerCollectionEntity = new ArrayList<>();
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TankerCollectionEntity tankerCollectionEntity = getTankerEntityFromCrusor(cursor);
                allTankerCollectionEntity.add(tankerCollectionEntity);
            } while (cursor.moveToNext());
        }
        return allTankerCollectionEntity;

    }

    public ArrayList<TankerCollectionEntity> getAllUnsentTankerEntities() {
        String query = "Select * from " + TABLE_TANKER + " where " + KEY_SENT + "= " + 0;

        ArrayList<TankerCollectionEntity> allTankerCollectionEntity = new ArrayList<>();
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                TankerCollectionEntity tankerCollectionEntity = getTankerEntityFromCrusor(cursor);
                allTankerCollectionEntity.add(tankerCollectionEntity);
            } while (cursor.moveToNext());
        }

        return allTankerCollectionEntity;

    }

    public TankerCollectionEntity getTankerEntityFromCrusor(Cursor cursor) {
        TankerCollectionEntity tankerCollectionEntity = new TankerCollectionEntity();

        String compNumbers = cursor.getString(cursor.getColumnIndex(KEY_COMPARTMENT_NUMBERS));
        String[] compNumber = compNumbers.split(SmartCCConstants.LIST_SEPARATOR);
        tankerCollectionEntity.compartmentNumbers = new ArrayList<>();

        for (int i = 0; i < compNumber.length; i++) {
            tankerCollectionEntity.compartmentNumbers.add(compNumber[i]);

        }
        tankerCollectionEntity.colId = cursor.getLong(cursor.getColumnIndex(KEY_COLUMN_ID));
        tankerCollectionEntity.collectionTime = cursor.getLong(cursor.getColumnIndex(KEY_COLLECTION_TIME));
        tankerCollectionEntity.alcohol = cursor.getDouble(cursor.getColumnIndex(KEY_ALCHOLOL));
        tankerCollectionEntity.centerId = cursor.getString(cursor.getColumnIndex(KEY_BULK_COLLER_CODE));
        tankerCollectionEntity.fat = cursor.getDouble(cursor.getColumnIndex(KEY_FAT));
        tankerCollectionEntity.snf = cursor.getDouble(cursor.getColumnIndex(KEY_SNF));
        tankerCollectionEntity.quantity = cursor.getDouble(cursor.getColumnIndex(KEY_QUANTITY));
        tankerCollectionEntity.routeNumber = cursor.getString(cursor.getColumnIndex(KEY_ROUTE_NUMER));
        tankerCollectionEntity.supervisorCode = cursor.getString(cursor.getColumnIndex(KEY_SUPERVISOR_CODE));
        tankerCollectionEntity.routeNumber = cursor.getString(cursor.getColumnIndex(KEY_ROUTE_NUMER));
        tankerCollectionEntity.tankerNumber = cursor.getString(cursor.getColumnIndex(KEY_TANKER_NUMBER));
        tankerCollectionEntity.status = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
        tankerCollectionEntity.comments = cursor.getString(cursor.getColumnIndex(KEY_COMMENTS));
        tankerCollectionEntity.seqNum = cursor.getLong(cursor.getColumnIndex(TankerCollectionTable.SEQUENCE_NUMBER));
        tankerCollectionEntity.quantityUnit = cursor.getString(cursor.getColumnIndex(KEY_QUANTITY_UNIT));
        tankerCollectionEntity.sent = cursor.getInt(cursor.getColumnIndex(KEY_SENT));
        tankerCollectionEntity.temperature = cursor.getDouble(cursor.getColumnIndex(KEY_TEMP));
        tankerCollectionEntity.clr = cursor.getDouble(cursor.getColumnIndex(KEY_CLR));

        return tankerCollectionEntity;
    }

    public long updateTankerDetails(TankerCollectionEntity tankerCollectionEntity, int sentStatus) throws Exception {

        long codId = -1;

        String query = "Select * from " + TABLE_TANKER + " where " + KEY_COLLECTION_TIME
                + " =" + tankerCollectionEntity.collectionTime;

        Cursor cursor = primaryDatabase.rawQuery(query, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_ALCHOLOL, tankerCollectionEntity.alcohol);
        contentValues.put(KEY_BULK_COLLER_CODE, tankerCollectionEntity.centerId);
        contentValues.put(KEY_COLLECTION_TIME, tankerCollectionEntity.collectionTime);

        String compNumbers = "";
        if (tankerCollectionEntity.compartmentNumbers != null && tankerCollectionEntity.compartmentNumbers.size() > 0) {
            for (int i = 0; i < tankerCollectionEntity.compartmentNumbers.size(); i++) {
                compNumbers = compNumbers + tankerCollectionEntity.compartmentNumbers.get(i) +
                        SmartCCConstants.LIST_SEPARATOR;
            }
        }
        contentValues.put(KEY_COMPARTMENT_NUMBERS, compNumbers);
        contentValues.put(KEY_FAT, tankerCollectionEntity.fat);
        contentValues.put(KEY_SNF, tankerCollectionEntity.snf);
        contentValues.put(KEY_QUANTITY, tankerCollectionEntity.quantity);
        contentValues.put(KEY_ROUTE_NUMER, tankerCollectionEntity.routeNumber);
        contentValues.put(KEY_SENT, sentStatus);
        contentValues.put(KEY_SUPERVISOR_CODE, tankerCollectionEntity.supervisorCode);
        contentValues.put(KEY_TANKER_NUMBER, tankerCollectionEntity.tankerNumber);
        contentValues.put(KEY_STATUS, tankerCollectionEntity.status);
        contentValues.put(KEY_COMMENTS, tankerCollectionEntity.comments);
        contentValues.put(KEY_QUANTITY_UNIT, tankerCollectionEntity.quantityUnit);
        contentValues.put(KEY_TEMP, tankerCollectionEntity.temperature);
        contentValues.put(KEY_CLR, tankerCollectionEntity.clr);


        if (cursor != null && cursor.moveToFirst()) {
            codId = databaseSync.syncUpdate(primaryDatabase, TABLE_TANKER, contentValues
                    , KEY_COLUMN_ID + " =?",
                    new String[]{Long.toString(cursor.getLong(0))});
        } else {
            contentValues.put(KEY_SENT, CollectionConstants.UNSENT);
            contentValues.put(TankerCollectionTable.SMS_SENT_STATUS, CollectionConstants.UNSENT);
            contentValues.put(TankerCollectionTable.POST_DATE, SmartCCUtil.getDateInPostFormat());
            contentValues.put(TankerCollectionTable.POST_SHIFT, SmartCCUtil.getShiftInPostFormat(mContext));
            codId = databaseSync.syncInsert(primaryDatabase, TABLE_TANKER, contentValues);

        }


        return codId;

    }

    public void updateUnsentRecords(ArrayList<TankerCollectionEntity> allTankerCollectionEntity) {
        primaryDatabase.beginTransaction();
        for (int i = 0; i < allTankerCollectionEntity.size(); i++) {

            TankerCollectionEntity tankerCollectionEntity = allTankerCollectionEntity.get(i);
            try {
                updateTankerDetails(tankerCollectionEntity, TruckCCDatabase.SERVER_SENT_STATUS);
            } catch (Exception e) {
                primaryDatabase.endTransaction();
                e.printStackTrace();
            }
        }

        primaryDatabase.setTransactionSuccessful();
        primaryDatabase.endTransaction();

    }

    public int getUnsentCount() {
        //select sent from tankerTable where sent = 0;
        int count = 0;
        String query = "select count(" + KEY_SENT + ") from " + TABLE_TANKER + " where " + KEY_SENT + " = " + 0;
        Cursor cursor = primaryDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        return count;
    }

    public void deleteOnYearOldTankerRecords() {
        long oneYearOldTime = new SmartCCUtil(mContext).getDateInMillis(-DAY_FOR_PURGE, false);
        String query = "Select * from " + TABLE_TANKER + " Where " + KEY_COLLECTION_TIME + "<" + oneYearOldTime;
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        try {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String strFilter = KEY_COLLECTION_TIME + " = ?";
                    databaseSync.syncDelete(primaryDatabase, TABLE_TANKER, strFilter,
                            new String[]{String.valueOf(cursor.getLong(cursor.getColumnIndex(KEY_COLLECTION_TIME)))});
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
