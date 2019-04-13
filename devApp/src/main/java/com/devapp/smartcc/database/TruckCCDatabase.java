package com.devapp.smartcc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.dbbackup.DatabaseSync;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.RouteCollectionTable;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entities.TrucKInfo;
import com.devapp.smartcc.entities.TruckDetailsEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Upendra on 10/3/2016.
 */
public class TruckCCDatabase {

    public static final String TABLE_TRUCK_DETAILS = "tableTruckDetails";
    public static final String TABLE_TRUCK_INFO = "tableTruckInfo";


    public static final String KEY_TRUCK_COLUMN_ID = "_id";
    public static final String KEY_TRUCK_CENTER_ID = "truckCenterId";
    public static final String KEY_TRUCK_ID = "truckId";
    public static final String KEY_TRUCK_DATE = "truckDate";
    public static final String KEY_TRUCK_SECURITY_TIME = "truckSecurityTime";
    public static final String KEY_TRUCK_REMARKS = "truckRemarks";
    public static final String KEY_TRUCK_RECOVERY_AMOUNT = "recoveryAmount";
    public static final String KEY_TRUCK_MATERIAL = "material";
    public static final String KEY_TRUCK_CANS_IN = "truckCansIn";
    public static final String KEY_TRUCK_CANS_OUT = "truckCansOut";
    public static final String KEY_TRUCK_SHIFT = "truckShift";
    public static final String KEY_TRUCK_SENT_STATUS = "sentStatus";

    public static final int SERVER_SENT_STATUS = 1;
    public static final int SERVER_UNSENT_STATUS = 0;


    public static final String KEY_TRUCK_NUMBER = "truckNumber";
    public static final String KEY_TRUCK_NAME = "truckName";
    public static final String KEY_TRUCK_MODEL = "truckModel";
    public static final String KEY_TRUCK_MAKE = "truckMake";
    public static final String KEY_TRUCK_TYPE = "truckType";
    public static final String KEY_TRUCK_MAXCAPACITY = "maxCapacity";
    public static final String KEY_TRUCK_NUM_OF_CANS = "numOfCans";
    public static final String KEY_TRUCK_LAST_SERVICE_DATE = "lastServiceDate";
    public static final String KEY_TRUCK_CONTRACT_START_DATE = "contractStartDate";
    public static final String KEY_TRUCK_OWNER_NAME = "ownerName";
    public static final String KEY_TRUCK_OWNER_CONTACT_NUM = "contactNumer";
    public static final String KEY_TRUCK_DRIVER_CONTACT_NUMBER = "driverContactNum";
    public static final String KEY_TRUCK_DRIVER_NAME = "driverName";
    public static final String KEY_TRUCK_ROUTE = "routes";


    SQLiteDatabase primaryDatabase;
    DatabaseSync databaseSync;
    Context mContext;

    AmcuConfig amcuConfig;
    DatabaseHandler databaseHandler;

    public TruckCCDatabase(Context context, SQLiteDatabase sqliteDatabase, DatabaseSync
            databaseSync) {
        this.mContext = context;
        this.primaryDatabase = sqliteDatabase;
        this.databaseSync = databaseSync;

        amcuConfig = AmcuConfig.getInstance();
        databaseHandler = DatabaseHandler.getDatabaseInstance();
    }

    public static String getCreateTruckInfoQuery() {
        return "CREATE TABLE " + TABLE_TRUCK_INFO + " ( "
                + KEY_TRUCK_ID + " TEXT, "
                + KEY_TRUCK_NUMBER + " TEXT " + ", "
                + KEY_TRUCK_NAME + " TEXT, "
                + KEY_TRUCK_MAKE + " TEXT, "
                + KEY_TRUCK_MODEL + " TEXT, "
                + KEY_TRUCK_TYPE + " TEXT, "
                + KEY_TRUCK_MAXCAPACITY + " DOUBLE, "
                + KEY_TRUCK_NUM_OF_CANS + " INTEGER, "
                + KEY_TRUCK_CONTRACT_START_DATE + " INTEGER, "
                + KEY_TRUCK_LAST_SERVICE_DATE + " INTEGER, "
                + KEY_TRUCK_OWNER_NAME + " TEXT,"
                + KEY_TRUCK_OWNER_CONTACT_NUM + " TEXT,"
                + KEY_TRUCK_DRIVER_NAME + " TEXT,"
                + KEY_TRUCK_DRIVER_CONTACT_NUMBER + " TEXT,"
                + KEY_TRUCK_ROUTE + " TEXT )";

    }

    public static String getCreateQuery() {
        return RouteCollectionTable.CREATE_ROUTE_QUERY;

    }

    public ArrayList<TruckDetailsEntity> getAllTruckDetails() {

        ArrayList<TruckDetailsEntity> allTruckEntity = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRUCK_DETAILS;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst())

            do {
                TruckDetailsEntity truckDetailsEntity;
                int cansIn = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_CANS_IN));
                int cansOut = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_CANS_OUT));
                String centerId = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_CENTER_ID));
                String truckId = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_ID));
                long date = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_DATE));
                long securityTime = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_SECURITY_TIME));
                String material = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_MATERIAL));
                double recoveryAmount = cursor.getDouble(cursor.getColumnIndex(KEY_TRUCK_RECOVERY_AMOUNT));
                int shift = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_SHIFT));

                long sequenceNumber = cursor.getLong(cursor.getColumnIndex("_id"));
                int sentStatus = cursor.getInt(cursor.getColumnIndex(KEY_TRUCK_SENT_STATUS));
                String remarks = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_REMARKS));

                String postDate = cursor.getString(cursor.getColumnIndex(RouteCollectionTable.POST_DATE));
                String postShift = cursor.getString(cursor.getColumnIndex(RouteCollectionTable.POST_SHIFT));
                int smsSentStatus = cursor.getInt(cursor.getColumnIndex(RouteCollectionTable.SMS_SENT_STATUS));

                truckDetailsEntity = new TruckDetailsEntity(sequenceNumber, centerId, date
                        , shift, truckId, securityTime
                        , recoveryAmount, material, cansIn, cansOut,
                        amcuConfig.getDeviceID(), sentStatus, remarks, smsSentStatus, postDate, postShift);

                allTruckEntity.add(truckDetailsEntity);

            } while (cursor.moveToNext());

        return allTruckEntity;

    }

    public TruckDetailsEntity getTruckDetailsEntity(String truckId) {
        TruckDetailsEntity truckDetailsEntity = null;
        String selectQuery = "SELECT * FROM " + TABLE_TRUCK_DETAILS + " WHERE " + KEY_TRUCK_ID + " ='" + truckId + "'";
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {

            int cansIn = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_CANS_IN));
            int cansOut = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_CANS_OUT));
            String centerId = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_CENTER_ID));

            long date = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_DATE));
            long securityTime = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_SECURITY_TIME));
            String material = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_MATERIAL));
            double recoveryAmount = cursor.getDouble(cursor.getColumnIndex(KEY_TRUCK_RECOVERY_AMOUNT));
            int shift = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_SHIFT));

            long sequenceNumber = cursor.getLong(cursor.getColumnIndex("_id"));
            int sentStatus = cursor.getInt(cursor.getColumnIndex(KEY_TRUCK_SENT_STATUS));
            String remarks = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_REMARKS));

            String postDate = cursor.getString(cursor.getColumnIndex(RouteCollectionTable.POST_DATE));
            String postShift = cursor.getString(cursor.getColumnIndex(RouteCollectionTable.POST_SHIFT));
            int smsSentStatus = cursor.getInt(cursor.getColumnIndex(RouteCollectionTable.SMS_SENT_STATUS));


            truckDetailsEntity = new TruckDetailsEntity(sequenceNumber, centerId, date
                    , shift, truckId, securityTime
                    , recoveryAmount, material, cansIn, cansOut, amcuConfig.getDeviceID(),
                    sentStatus, remarks, smsSentStatus, postDate, postShift);
        }

        return truckDetailsEntity;

    }

    public void insertAllTruckDetailsEntity(ArrayList<TruckDetailsEntity> allTruckEnt,
                                            String primaryOrSecondary) {
        SQLiteDatabase database;
        if (primaryOrSecondary.equalsIgnoreCase(DatabaseHandler.isPrimary)) {
            database = primaryDatabase;
        } else {
            database = databaseSync.getSqliteDatabase();
        }
        database.beginTransaction();
        try {
            for (int pos = 0; pos < allTruckEnt.size(); pos++) {
                TruckDetailsEntity truckDetailsEntity = allTruckEnt.get(pos);
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_TRUCK_CANS_IN, truckDetailsEntity.cansIn);
                contentValues.put(KEY_TRUCK_CANS_OUT, truckDetailsEntity.cansOut);
                contentValues.put(KEY_TRUCK_CENTER_ID, truckDetailsEntity.centerId);
                contentValues.put(KEY_TRUCK_ID, truckDetailsEntity.truckId);
                contentValues.put(KEY_TRUCK_DATE, truckDetailsEntity.date);
                contentValues.put(KEY_TRUCK_MATERIAL, truckDetailsEntity.material);
                contentValues.put(KEY_TRUCK_SECURITY_TIME, truckDetailsEntity.securityTime);
                contentValues.put(KEY_TRUCK_SHIFT, truckDetailsEntity.shift);
                contentValues.put(KEY_TRUCK_SENT_STATUS, SERVER_SENT_STATUS);

                databaseSync.syncInsert(primaryDatabase, TABLE_TRUCK_DETAILS, contentValues);

            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            SmartCCUtil.todoDbFailure(mContext, primaryOrSecondary);
        } finally {
            database.endTransaction();
        }
    }

    public long updateTruckDetails(TruckDetailsEntity truckDetailsEntity, int sentStatus, boolean fromCollection) throws Exception {

        long colId = -1;

        String query = "Select * from " + TABLE_TRUCK_DETAILS + " where " + KEY_TRUCK_ID
                + " ='" + truckDetailsEntity.truckId + "'" + " AND " + KEY_TRUCK_DATE
                + " =" + truckDetailsEntity.date + " AND " +
                KEY_TRUCK_SHIFT + " = " + truckDetailsEntity.shift;

        Cursor cursor = primaryDatabase.rawQuery(query, null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TRUCK_CANS_IN, truckDetailsEntity.cansIn);
        contentValues.put(KEY_TRUCK_CANS_OUT, truckDetailsEntity.cansOut);
        contentValues.put(KEY_TRUCK_CENTER_ID, truckDetailsEntity.centerId);
        contentValues.put(KEY_TRUCK_ID, truckDetailsEntity.truckId);
        contentValues.put(KEY_TRUCK_DATE, truckDetailsEntity.date);
        contentValues.put(KEY_TRUCK_MATERIAL, truckDetailsEntity.material);
        contentValues.put(KEY_TRUCK_SECURITY_TIME, truckDetailsEntity.securityTime);
        contentValues.put(KEY_TRUCK_SHIFT, truckDetailsEntity.shift);
        contentValues.put(KEY_TRUCK_REMARKS, truckDetailsEntity.remarks);
        contentValues.put(KEY_TRUCK_RECOVERY_AMOUNT, truckDetailsEntity.recoveryAmount);
        contentValues.put(KEY_TRUCK_SENT_STATUS, sentStatus);
        if (cursor != null && cursor.moveToFirst()) {
            if (cursor.getInt(cursor.getColumnIndex(KEY_TRUCK_SENT_STATUS)) == 1 && fromCollection &&
                    truckDetailsEntity.cansIn == cursor.getInt(cursor.getColumnIndex(KEY_TRUCK_CANS_IN))) {
                return 0;
            }
            colId = databaseSync.syncUpdate(primaryDatabase, TABLE_TRUCK_DETAILS, contentValues
                    , KEY_TRUCK_COLUMN_ID + " =?",
                    new String[]{Long.toString(cursor.getLong(0))});
        } else {
            contentValues.put(KEY_TRUCK_SENT_STATUS, CollectionConstants.UNSENT);
            contentValues.put(RouteCollectionTable.SMS_SENT_STATUS, CollectionConstants.UNSENT);
            contentValues.put(RouteCollectionTable.POST_DATE, SmartCCUtil.getDateInPostFormat());
            contentValues.put(RouteCollectionTable.POST_SHIFT, SmartCCUtil.getShiftInPostFormat(mContext));

            colId = databaseSync.syncInsert(primaryDatabase, TABLE_TRUCK_DETAILS, contentValues);

        }


        return colId;

    }

    public ArrayList<TruckDetailsEntity> getAllUnsentTruckDetails() {

        ArrayList<TruckDetailsEntity> allTruckEntity = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRUCK_DETAILS + " Where " + KEY_TRUCK_SENT_STATUS
                + " = " + SERVER_UNSENT_STATUS;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst())

            do {
                TruckDetailsEntity truckDetailsEntity;
                int cansIn = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_CANS_IN));
                int cansOut = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_CANS_OUT));
                String centerId = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_CENTER_ID));
                String truckId = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_ID));
                long date = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_DATE));
                long securityTime = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_SECURITY_TIME));
                String material = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_MATERIAL));
                double recoveryAmount = cursor.getDouble(cursor.getColumnIndex(KEY_TRUCK_RECOVERY_AMOUNT));
                int shift = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_SHIFT));

                long sequenceNumber = 0;
                try {
                    sequenceNumber = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_COLUMN_ID));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                int sentStatus = cursor.getInt(cursor.getColumnIndex(KEY_TRUCK_SENT_STATUS));
                String remarks = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_REMARKS));

                String postDate = cursor.getString(cursor.getColumnIndex(RouteCollectionTable.POST_DATE));
                String postShift = cursor.getString(cursor.getColumnIndex(RouteCollectionTable.POST_SHIFT));
                int smsSentStatus = cursor.getInt(cursor.getColumnIndex(RouteCollectionTable.SMS_SENT_STATUS));


                truckDetailsEntity = new TruckDetailsEntity(sequenceNumber, centerId, date
                        , shift, truckId, securityTime
                        , recoveryAmount, material, cansIn, cansOut, amcuConfig.getDeviceID(), sentStatus, remarks, smsSentStatus,
                        postDate, postShift);

                allTruckEntity.add(truckDetailsEntity);

            } while (cursor.moveToNext());

        return allTruckEntity;

    }


    public ArrayList<Long> getAllUnsentColumnId() {

        ArrayList<Long> allUnsentColId = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRUCK_DETAILS + " Where " + KEY_TRUCK_SENT_STATUS
                + " = " + SERVER_UNSENT_STATUS;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst())

            do {
                allUnsentColId.add(cursor.getLong(0));

            } while (cursor.moveToNext());

        return allUnsentColId;

    }

    public void updateUnsentRecords(ArrayList<TruckDetailsEntity> allTruckDetailsEnt) {
        primaryDatabase.beginTransaction();
        for (int i = 0; i < allTruckDetailsEnt.size(); i++) {

            TruckDetailsEntity truckDetailsEnt = allTruckDetailsEnt.get(i);
            try {
                updateTruckDetails(truckDetailsEnt, SERVER_SENT_STATUS, false);
            } catch (Exception e) {
                primaryDatabase.endTransaction();
                e.printStackTrace();
            }
        }

        primaryDatabase.setTransactionSuccessful();
        primaryDatabase.endTransaction();

    }

    public void insertAllTruckInfoDetails(ArrayList<TrucKInfo> allTruckEnt,
                                          SQLiteDatabase primaryDatabase) {

        try {
            for (int pos = 0; pos < allTruckEnt.size(); pos++) {

                TrucKInfo truckInfo = allTruckEnt.get(pos);
                ContentValues contentValues = new ContentValues();
                contentValues.put(KEY_TRUCK_ID, truckInfo.truckId);
                contentValues.put(KEY_TRUCK_NUMBER, truckInfo.truckNumber);
                contentValues.put(KEY_TRUCK_NAME, truckInfo.truckName);
                contentValues.put(KEY_TRUCK_MODEL, truckInfo.model);
                contentValues.put(KEY_TRUCK_MAKE, truckInfo.make);
                contentValues.put(KEY_TRUCK_TYPE, truckInfo.type);
                contentValues.put(KEY_TRUCK_MAXCAPACITY, truckInfo.maxCapacity);
                contentValues.put(KEY_TRUCK_NUM_OF_CANS, truckInfo.numOfCans);
                contentValues.put(KEY_TRUCK_LAST_SERVICE_DATE, truckInfo.lastServiceDate);
                contentValues.put(KEY_TRUCK_CONTRACT_START_DATE, truckInfo.contractStartDate);
                contentValues.put(KEY_TRUCK_OWNER_NAME, truckInfo.ownerName);
                contentValues.put(KEY_TRUCK_OWNER_CONTACT_NUM, truckInfo.ownerContactNumber);
                contentValues.put(KEY_TRUCK_DRIVER_NAME, truckInfo.driverName);
                contentValues.put(KEY_TRUCK_DRIVER_CONTACT_NUMBER, truckInfo.driverContactNumber);

                String routes = "";

                if (truckInfo.routes != null && truckInfo.routes.size() > 0) {
                    for (int i = 0; i < truckInfo.routes.size(); i++) {
                        routes = routes + SmartCCConstants.LIST_SEPARATOR;
                    }
                }
                contentValues.put(KEY_TRUCK_ROUTE, routes);
                databaseSync.syncInsert(primaryDatabase, TABLE_TRUCK_INFO, contentValues);
            }
            primaryDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<TrucKInfo> getAllTruckInfoEntity() {

        ArrayList<TrucKInfo> allTruckEntity = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_TRUCK_INFO;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst())

            do {
                TrucKInfo truckInfor = new TrucKInfo();
                truckInfor.truckId = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_ID));
                truckInfor.numOfCans = cursor.getShort(cursor.getColumnIndex(KEY_TRUCK_NUM_OF_CANS));
                truckInfor.truckNumber = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_NUMBER));
                truckInfor.truckName = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_NAME));
                truckInfor.model = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_MODEL));
                truckInfor.make = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_MAKE));
                truckInfor.type = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_TYPE));
                truckInfor.maxCapacity = cursor.getDouble(cursor.getColumnIndex(KEY_TRUCK_MAXCAPACITY));
                truckInfor.lastServiceDate = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_LAST_SERVICE_DATE));
                truckInfor.contractStartDate = cursor.getLong(cursor.getColumnIndex(KEY_TRUCK_CONTRACT_START_DATE));
                truckInfor.ownerName = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_OWNER_NAME));
                truckInfor.ownerContactNumber = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_DRIVER_CONTACT_NUMBER));
                truckInfor.driverName = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_DRIVER_NAME));
                truckInfor.driverContactNumber = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_DRIVER_CONTACT_NUMBER));

                String routes = cursor.getString(cursor.getColumnIndex(KEY_TRUCK_ROUTE));
                if (routes != null) {
                    String[] allRoutes = routes.split(SmartCCConstants.LIST_SEPARATOR);
                    List<String> listRoutes = new ArrayList<>();
                    for (int i = 0; i < allRoutes.length; i++) {
                        listRoutes.add(allRoutes[i]);
                    }
                    truckInfor.routes = listRoutes;
                }


                allTruckEntity.add(truckInfor);

            } while (cursor.moveToNext());

        return allTruckEntity;

    }

    public long getSequenceNumber() {

        String query = "Select * from " + TABLE_TRUCK_DETAILS + " ORDER BY " + "_id" + " DESC";
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getLong(0);
        } else {
            return -1;
        }
    }


    public void getAndDeleteTruckTrans(String truckId, SQLiteDatabase primaryDatabase) throws Exception {

        long colId = -1l;
        String selectQuery = "SELECT  * FROM " + TABLE_TRUCK_INFO + " WHERE "
                + KEY_TRUCK_ID + "='" + truckId + "'";
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                colId = cursor.getLong(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (colId != -1) {
            String strFilter = KEY_TRUCK_ID + " = ?";
            databaseSync.syncDeleteByDB(primaryDatabase, TABLE_TRUCK_INFO, strFilter, new String[]{truckId});
            cursor.close();
            Util.displayErrorToast("Truck Id: " + truckId + " has updated!", mContext);
        }
    }

    //To add complete Truck Info details

    public void addCompleteTruckDetails(ArrayList<TrucKInfo> allTruckInfor, String primaryOrSecondary) {
        SQLiteDatabase database;
        if (primaryOrSecondary.equalsIgnoreCase(DatabaseHandler.isPrimary)) {
            database = primaryDatabase;
        } else {
            database = databaseSync.getSqliteDatabase();
        }

        try {
            database.beginTransaction();
            for (int i = 0; i < allTruckInfor.size(); i++) {
                try {

                    getAndDeleteTruckTrans(allTruckInfor.get(i).truckId, database);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            insertAllTruckInfoDetails(allTruckInfor, database);
        } catch (Exception e) {
            e.printStackTrace();
            Util.restartApp(mContext);
        } finally {
            endTransaction(database);
        }


    }


    public ArrayList<String> getUncommitedTruckIds(long date, int shift)

    {
        String sqlQuery = "Select " + KEY_TRUCK_ID + " from " + TABLE_TRUCK_INFO
                + " where " + KEY_TRUCK_ID + " !='" +
                "(SELECT " + KEY_TRUCK_ID + " from " + TABLE_TRUCK_DETAILS + " WHERE " +
                KEY_TRUCK_DATE + " =" + date + " AND " + KEY_TRUCK_SHIFT
                + " =" + shift + ")'" + " ORDER BY " + KEY_TRUCK_ID + " ASC";
        ArrayList<String> allTruckIds = null;
        Cursor cursor = primaryDatabase.rawQuery(sqlQuery, null);
        if (cursor != null && cursor.moveToFirst()) {
            allTruckIds = new ArrayList<>();
            do {
                allTruckIds.add(cursor.getString(cursor.getColumnIndex(KEY_TRUCK_ID)));

            } while (cursor.moveToNext());
        }
        return allTruckIds;

    }


    public void endTransaction(SQLiteDatabase db) {
        try {
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
