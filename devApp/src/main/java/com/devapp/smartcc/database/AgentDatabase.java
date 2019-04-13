package com.devapp.smartcc.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.agentfarmersplit.AgentSplitEntity;
import com.devapp.devmain.agentfarmersplit.QualityParams;
import com.devapp.devmain.agentfarmersplit.QuantityParams;
import com.devapp.devmain.agentfarmersplit.RateParams;
import com.devapp.devmain.agentfarmersplit.SplitCollectionEntity;
import com.devapp.devmain.dbbackup.DatabaseSync;
import com.devapp.devmain.entity.PostEndShift;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tableEntities.SplitRecordTable;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entities.AgentEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;

/**
 * Created by Upendra on 10/4/2016.
 */
public class AgentDatabase {

    public static final String TABLE_AGENT = "agentTable";
    public static final String KEY_AGENT_ID = "agentId";
    public static final String KEY_AGENT_BARCODE = "agentBarcode";
    public static final String KEY_AGENT_FIRST_NAME = "agentName";
    public static final String KEY_AGENT_LAST_NAME = "agentLastName";
    public static final String KEY_AGENT_MOBILE_NUM = "mobileNum";
    public static final String KEY_AGENT_PHONE_NUM = "phoneNum";
    public static final String KEY_UNIQUE_ID1 = "uniqueId1";
    public static final String KEY_UNIQUE_ID2 = "uniqueId2";
    public static final String KEY_UNIQUE_ID3 = "uniqueId3";
    public static final String KEY_AGENT_CATTLE_TYPE = "agentCattleType";
    public static final String KEY_AGENT_REG_DATE = "agentRegDate";
    public static final String KEY_AGENT_ROUTE = "agentRoute";
    public static final String KEY_SHIFT_SUPPLYING_TO = "shiftSupplying";
    public static final String KEY_PICKUP_POINT = "pickUpPoint";
    public static final String KEY_DISTANCE_TO_DELIVERY = "distanceDelivery";
    public static final String KEY_AGENT_ASSOCIATED_MCC = "associateCenters";
    public static final String KEY_AGENT_NUMBER_OF_CANS = "numOfCans";
    SQLiteDatabase primaryDatabase;
    DatabaseSync databaseSync;
    Context mContext;
    SQLiteDatabase secondaryDatabase;
    private AmcuConfig amcuConfig;
    private DatabaseHandler databaseHandler;

    public AgentDatabase(Context context, SQLiteDatabase sqliteDatabase, DatabaseSync
            databaseSync) {
        this.mContext = context;
        this.primaryDatabase = sqliteDatabase;
        this.databaseSync = databaseSync;
        this.databaseHandler = DatabaseHandler.getDatabaseInstance();
        this.secondaryDatabase = databaseHandler.getSecondaryDatabase();
        this.amcuConfig = AmcuConfig.getInstance();

    }

    public static String getCreateQuery() {
        return "CREATE TABLE " + TABLE_AGENT + "( " +
                KEY_AGENT_ID + " TEXT PRIMARY KEY UNIQUE NOT NULL, " +
                KEY_AGENT_BARCODE + " TEXT UNIQUE, " +
                KEY_AGENT_FIRST_NAME + " VARCHAR(100), " +
                KEY_AGENT_LAST_NAME + " VARCHAR(100), " +
                KEY_UNIQUE_ID1 + " TEXT UNIQUE, " +
                KEY_UNIQUE_ID2 + " TEXT UNIQUE, " +
                KEY_UNIQUE_ID3 + " TEXT UNIQUE, " +
                KEY_PICKUP_POINT + " TEXT, " +
                KEY_AGENT_REG_DATE + " INTEGER, " +
                KEY_AGENT_NUMBER_OF_CANS + " INTEGER, " +
                KEY_DISTANCE_TO_DELIVERY + " REAL, " +
                KEY_AGENT_ASSOCIATED_MCC + " TEXT, " +
                KEY_AGENT_MOBILE_NUM + " TEXT, " +
                KEY_AGENT_PHONE_NUM + " TEXT, " +
                KEY_AGENT_CATTLE_TYPE + " TEXT, " +
                KEY_AGENT_ROUTE + " TEXT, " +
                KEY_SHIFT_SUPPLYING_TO + " TEXT )";

    }

    public static String getSplitCollectionQuery(AgentSplitEntity splitCollectionEntity) {
        return "Select * from " + SplitRecordTable.TABLE + " Where " +
                SplitRecordTable.COLLECTION_TIME + " = " + splitCollectionEntity.collectionTime;
    }

    public static int getRateCalculated(boolean check) {
        int isRateCalculated = 0;
        if (check) {
            isRateCalculated = 1;
        } else {
            isRateCalculated = 0;
        }
        return isRateCalculated;
    }

    public void InsertAgentTrans(ArrayList<AgentEntity> allAgentEntity, SQLiteDatabase primaryDatabase) {


        try {
            for (int i = 0; i < allAgentEntity.size(); i++) {

                AgentEntity agentEntity = allAgentEntity.get(i);

                ContentValues values = new ContentValues();
                values.put(KEY_AGENT_ID, agentEntity.agentID);
                values.put(KEY_AGENT_BARCODE, agentEntity.barcode);
                values.put(KEY_AGENT_CATTLE_TYPE, agentEntity.milkType);
                values.put(KEY_AGENT_MOBILE_NUM, agentEntity.mobileNum);
                values.put(KEY_AGENT_PHONE_NUM, agentEntity.phoneNum);
                values.put(KEY_AGENT_FIRST_NAME, agentEntity.firstName);
                values.put(KEY_AGENT_LAST_NAME, agentEntity.lastName);
                values.put(KEY_AGENT_REG_DATE, agentEntity.registeredDate);
                values.put(KEY_AGENT_NUMBER_OF_CANS, agentEntity.numCans);
                values.put(KEY_DISTANCE_TO_DELIVERY, agentEntity.distanceToDelivery);
                values.put(KEY_PICKUP_POINT, agentEntity.pickupPoint);
                values.put(KEY_UNIQUE_ID1, agentEntity.uniqueID1);
                values.put(KEY_UNIQUE_ID2, agentEntity.uniqueID2);
                values.put(KEY_UNIQUE_ID3, agentEntity.uniqueID3);
                values.put(KEY_SHIFT_SUPPLYING_TO, agentEntity.shiftsSupplyingTo);

                String centerList = "";
                if (agentEntity.centerList != null && agentEntity.centerList.size() > 0) {
                    for (int j = 0; j < agentEntity.centerList.size(); j++) {
                        centerList = agentEntity.centerList.get(j) + SmartCCConstants.LIST_SEPARATOR;
                    }
                }
                values.put(KEY_AGENT_ASSOCIATED_MCC, centerList);

                databaseSync.syncInsertExcelDatabase(primaryDatabase, TABLE_AGENT, values);
            }
            primaryDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            primaryDatabase.endTransaction();
            e.printStackTrace();
        }


    }

    public ArrayList<AgentEntity> getAllAgentDetails() {

        ArrayList<AgentEntity> allAgentEntity = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_AGENT;
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst())

            do {
                String agentId = cursor.getString(cursor.getColumnIndex(KEY_AGENT_ID));
                String agentBarcode = cursor.getString(cursor.getColumnIndex(KEY_AGENT_BARCODE));
                String firstName = cursor.getString(cursor.getColumnIndex(KEY_AGENT_FIRST_NAME));
                String lastName = cursor.getString(cursor.getColumnIndex(KEY_AGENT_LAST_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(KEY_AGENT_PHONE_NUM));
                String mobileNuber = cursor.getString(cursor.getColumnIndex(KEY_AGENT_MOBILE_NUM));
                String milkType = cursor.getString(cursor.getColumnIndex(KEY_AGENT_CATTLE_TYPE));
                long registeredDate = cursor.getLong(cursor.getColumnIndex(KEY_AGENT_REG_DATE));
                String uniqueId1 = cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_ID1));
                String uniqueId2 = cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_ID2));
                String uniqueId3 = cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_ID3));
                int numberOfcans = cursor.getInt(cursor.getColumnIndex(KEY_AGENT_NUMBER_OF_CANS));
                String shiftSupplyingTo = cursor.getString(cursor.getColumnIndex(KEY_SHIFT_SUPPLYING_TO));
                String routeid = cursor.getString(cursor.getColumnIndex(KEY_AGENT_ID));
                String pickUpPoint = cursor.getString(cursor.getColumnIndex(KEY_PICKUP_POINT));
                double distanceToDileviry = cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TO_DELIVERY));

                String centerListStr = cursor.getString(cursor.getColumnIndex(KEY_AGENT_ASSOCIATED_MCC));
                ArrayList<String> allCenterList = new ArrayList<>();
                String[] strArray = centerListStr.split(SmartCCConstants.LIST_SEPARATOR);

                for (int j = 0; j < strArray.length; j++) {
                    allCenterList.add(strArray[j]);

                }

                AgentEntity agentEntity = new AgentEntity(agentId, agentBarcode, firstName
                        , lastName, phoneNumber, mobileNuber, milkType, registeredDate,
                        uniqueId1, uniqueId2, uniqueId3,
                        numberOfcans, shiftSupplyingTo, routeid, pickUpPoint
                        , distanceToDileviry, allCenterList);

                allAgentEntity.add(agentEntity);

            } while (cursor.moveToNext());

        return allAgentEntity;

    }

    public AgentEntity getAgentEntity(String agentId) {
        AgentEntity agentEntity = null;
        String selectQuery = "SELECT * FROM " + TABLE_AGENT + " WHERE " + KEY_AGENT_ID + " ='" + agentId + "'";
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst()) {

            String agentBarcode = cursor.getString(cursor.getColumnIndex(KEY_AGENT_BARCODE));
            String firstName = cursor.getString(cursor.getColumnIndex(KEY_AGENT_FIRST_NAME));
            String lastName = cursor.getString(cursor.getColumnIndex(KEY_AGENT_LAST_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(KEY_AGENT_PHONE_NUM));
            String mobileNuber = cursor.getString(cursor.getColumnIndex(KEY_AGENT_MOBILE_NUM));
            String milkType = cursor.getString(cursor.getColumnIndex(KEY_AGENT_CATTLE_TYPE));
            long registeredDate = cursor.getLong(cursor.getColumnIndex(KEY_AGENT_REG_DATE));
            String uniqueId1 = cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_ID1));
            String uniqueId2 = cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_ID2));
            String uniqueId3 = cursor.getString(cursor.getColumnIndex(KEY_UNIQUE_ID3));
            int numberOfcans = cursor.getInt(cursor.getColumnIndex(KEY_AGENT_NUMBER_OF_CANS));
            String shiftSupplyingTo = cursor.getString(cursor.getColumnIndex(KEY_SHIFT_SUPPLYING_TO));
            String routeid = cursor.getString(cursor.getColumnIndex(KEY_AGENT_ID));
            String pickUpPoint = cursor.getString(cursor.getColumnIndex(KEY_PICKUP_POINT));
            double distanceToDileviry = cursor.getDouble(cursor.getColumnIndex(KEY_DISTANCE_TO_DELIVERY));

            String centerListStr = cursor.getString(cursor.getColumnIndex(KEY_AGENT_ASSOCIATED_MCC));
            ArrayList<String> allCenterList = new ArrayList<>();
            String[] strArray = centerListStr.split(SmartCCConstants.LIST_SEPARATOR);

            for (int j = 0; j < strArray.length; j++) {
                allCenterList.add(strArray[j]);

            }

            agentEntity = new AgentEntity(agentId, agentBarcode, firstName
                    , lastName, phoneNumber, mobileNuber, milkType, registeredDate,
                    uniqueId1, uniqueId2, uniqueId3,
                    numberOfcans, shiftSupplyingTo, routeid, pickUpPoint
                    , distanceToDileviry, allCenterList);
        }

        return agentEntity;

    }

    public String getMilkType(String centerId) {
        String query = "Select " + KEY_AGENT_CATTLE_TYPE + " From " + TABLE_AGENT + " Where "
                + KEY_AGENT_ROUTE + " = " + centerId + "#" + " OR " + KEY_AGENT_ROUTE + " = " + "#" + centerId;

        Cursor cursor = primaryDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            return cursor.getString(0);
        }

        return "COW";

    }

    public void getAndDeleteAgentTrans(String agentId, SQLiteDatabase primaryDatabase) throws Exception {

        long colId = -1l;
        String selectQuery = "SELECT  * FROM " + TABLE_AGENT + " WHERE "
                + KEY_AGENT_ID + "='" + agentId + "'";
        Cursor cursor = primaryDatabase.rawQuery(selectQuery, null);
        try {
            if (cursor != null && cursor.moveToFirst()) {
                colId = cursor.getLong(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (colId != -1) {
            String strFilter = KEY_AGENT_ID + " = ?";
            databaseSync.syncDeleteByDB(primaryDatabase, TABLE_AGENT, strFilter, new String[]{agentId});
            cursor.close();
            Util.displayErrorToast("Agent Id: " + agentId + " has updated!", mContext);
        }
    }

    public void addCompleteAgentDetails(ArrayList<AgentEntity> allAgentEntity, String primaryOrSecondary) {
        SQLiteDatabase database;
        if (primaryOrSecondary.equalsIgnoreCase(DatabaseHandler.isPrimary)) {
            database = primaryDatabase;
        } else {
            database = databaseSync.getSqliteDatabase();
        }

        try {
            database.beginTransaction();
            for (int i = 0; i < allAgentEntity.size(); i++) {
                try {

                    getAndDeleteAgentTrans(allAgentEntity.get(i).agentID, database);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            InsertAgentTrans(allAgentEntity, database);
        } catch (Exception e) {
            e.printStackTrace();
            Util.restartApp(mContext);
        } finally {
            endTransaction(database);
        }


    }

    public void endTransaction(SQLiteDatabase db) {
        try {
            db.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUnsentQuerySplitRecords() {
        String query = " Select * from " + SplitRecordTable.TABLE
                + " Where " + SplitRecordTable.SENT + " = " + 0
                + " order by " + SplitRecordTable.AGENT_ID + " , "
                + SplitRecordTable.DATE + " , "
                + SplitRecordTable.SHIFT;
        return query;
    }


    public ArrayList<AgentSplitEntity> getAllSplitAgentCollection(String query)

    {
        ArrayList<AgentSplitEntity> allSplitCollection =
                new ArrayList<>();
        Cursor cursor = primaryDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                AgentSplitEntity splitCollectionEntity = new AgentSplitEntity();
                splitCollectionEntity = getSplitCollectionEntityFromCursor(cursor);
                allSplitCollection.add(splitCollectionEntity);
            }
            while (cursor.moveToNext());
        }
        return allSplitCollection;
    }

    private SQLiteDatabase getSqliteDatabase(String isPrimaryOrSecondary) {
        SQLiteDatabase sqLiteDatabase = null;

        if (isPrimaryOrSecondary.equalsIgnoreCase(DatabaseHandler.isPrimary)) {
            sqLiteDatabase = primaryDatabase;
        } else if (isPrimaryOrSecondary.equalsIgnoreCase(DatabaseHandler.isSecondary) &&
                SmartCCUtil.isSecondaryDatabaseExist(mContext) && secondaryDatabase != null) {
            sqLiteDatabase = secondaryDatabase;
        }
        return sqLiteDatabase;
    }

    public ArrayList<SplitCollectionEntity> getSplitEntityFromFarmerSplitEntity(
            ArrayList<AgentSplitEntity>
                    allFarmerSplitEntity) {
        ArrayList<SplitCollectionEntity> allSplitCollectionEntity = new ArrayList<>();
        SplitCollectionEntity msplitCollectionEntity = null;
        SplitCollectionEntity mTempsplitCollectionEntity = null;
        ArrayList<AgentSplitEntity> mallAgentSplitEntity = null;

        for (int i = 0; i < allFarmerSplitEntity.size(); i++) {
            AgentSplitEntity fse = allFarmerSplitEntity.get(i);
            if (mallAgentSplitEntity != null &&
                    mallAgentSplitEntity.contains(fse)) {
                msplitCollectionEntity.collectionTime = fse.collectionTime;
                mallAgentSplitEntity.add(fse);
                msplitCollectionEntity.agentSplitEntities = mallAgentSplitEntity;
                mTempsplitCollectionEntity = msplitCollectionEntity;

            } else {

                if (mTempsplitCollectionEntity != null) {
                    allSplitCollectionEntity.add(mTempsplitCollectionEntity);
                }

                msplitCollectionEntity = new SplitCollectionEntity();

                msplitCollectionEntity.collectionDate = fse.date;
                msplitCollectionEntity.collectionShift = fse.shift;
                msplitCollectionEntity.agentId = fse.agentId;
                msplitCollectionEntity.collectionTime = fse.collectionTime;
                msplitCollectionEntity.parentSeqNum = fse.parentSeqNum;

                mallAgentSplitEntity = new ArrayList<>();
                mallAgentSplitEntity.add(fse);
                msplitCollectionEntity.agentSplitEntities = mallAgentSplitEntity;

                mTempsplitCollectionEntity = msplitCollectionEntity;

            }
        }

        if (mTempsplitCollectionEntity != null) {
            allSplitCollectionEntity.add(mTempsplitCollectionEntity);
        }

        return allSplitCollectionEntity;

    }

    public ArrayList<PostEndShift> createPostEndShiftDataForSplitAgentFarmer(ArrayList<SplitCollectionEntity>
                                                                                     allSplitCollectionEntity) {
        ArrayList<PostEndShift> allPostCanShiftList = new ArrayList<>();

        if (allSplitCollectionEntity == null || allSplitCollectionEntity.size() == 0) {
            return allPostCanShiftList;
        }

        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        String shift = null;
        long collectionTime = 0;

        String date = null;
        PostEndShift postCanShiftEntity = new PostEndShift();
        postCanShiftEntity.farmerSplitCollectionEntryList = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(mContext);
        for (int i = 0; i < allSplitCollectionEntity.size(); i++) {


            SplitCollectionEntity splitCollectionEntity = allSplitCollectionEntity.get(i);

            if (splitCollectionEntity.agentSplitEntities == null
                    || splitCollectionEntity.agentSplitEntities.size() == 0) {
                continue;
            } else {
            }
//            canCollectionEntry.shift = AdvanceUtil.getFullShift(canCollectionEntry.shift);
            String mDate = smartCCUtil.changeDateFormat(splitCollectionEntity.collectionDate,
                    "dd-MMM-yyyy", "yyyy-MM-dd");
            if ((collectionTime == 0 && shift == null) ||
                    (!mDate.equalsIgnoreCase(date)) ||
                    (!shift.equalsIgnoreCase(splitCollectionEntity.collectionShift))) {
                postCanShiftEntity = new PostEndShift();
                postCanShiftEntity.farmerSplitCollectionEntryList = new ArrayList<>();

                postCanShiftEntity.shift = splitCollectionEntity.collectionShift;
                postCanShiftEntity.collectionDate = smartCCUtil.changeDateFormat(
                        splitCollectionEntity.collectionDate,
                        "dd-MMM-yyyy", "yyyy-MM-dd");

                postCanShiftEntity.startTime = splitCollectionEntity.collectionTime;
                postCanShiftEntity.endTime = splitCollectionEntity.collectionTime;
                postCanShiftEntity.societyId = sessionManager.getCollectionID();
                shift = splitCollectionEntity.collectionShift;
                collectionTime = splitCollectionEntity.collectionTime;
                date = postCanShiftEntity.collectionDate;
                //   canCollectionEntry.date = new AdvanceUtil(mContext).changeDateFormat(canCollectionEntry.date, "dd-MMM-yyyy", "yyyy-MM-dd");

            } else {
                //   canCollectionEntry.date = new AdvanceUtil(mContext).changeDateFormat(canCollectionEntry.date, "dd-MMM-yyyy", "yyyy-MM-dd");
                postCanShiftEntity.endTime = splitCollectionEntity.collectionTime;
            }


            if (allPostCanShiftList != null && allPostCanShiftList.size() > 0
                    && allPostCanShiftList.contains(postCanShiftEntity)) {
                for (int j = 0; j < allPostCanShiftList.size(); j++) {
                    if (postCanShiftEntity.collectionDate.equalsIgnoreCase(
                            allPostCanShiftList.get(j).collectionDate)
                            && postCanShiftEntity.shift.equalsIgnoreCase(
                            allPostCanShiftList.get(j).shift)) {

                        splitCollectionEntity.collectionDate = smartCCUtil.changeDateFormat(
                                splitCollectionEntity.collectionDate,
                                "dd-MMM-yyyy", "yyyy-MM-dd");
                        allPostCanShiftList.get(j).
                                farmerSplitCollectionEntryList.add(splitCollectionEntity);
                        allPostCanShiftList.get(j).
                                endTime = splitCollectionEntity.collectionTime;

                        break;
                    }
                }
            } else {
                splitCollectionEntity.collectionDate = smartCCUtil.changeDateFormat(
                        splitCollectionEntity.collectionDate,
                        "dd-MMM-yyyy", "yyyy-MM-dd");
                postCanShiftEntity.farmerSplitCollectionEntryList.add(splitCollectionEntity);
                postCanShiftEntity.farmerCollectionEntryList = new ArrayList<>();
                postCanShiftEntity.extraParametersUpdatedFarmerCollectionEntryList = new ArrayList<>();
                postCanShiftEntity.shiftConcluded = smartCCUtil.gtConcludedDateAndShift();
                postCanShiftEntity.deviceId = amcuConfig.getDeviceID();
                postCanShiftEntity.timeZone =
                        new SmartCCUtil(mContext).
                                getCalendarInstance(0).getTimeZone().getID();
                allPostCanShiftList.add(postCanShiftEntity);
            }

        }

        return allPostCanShiftList;

    }


    public ContentValues getContentValueOfSplitRecord(AgentSplitEntity splitCollectionEntity)


    {
        ContentValues contentValues = new ContentValues();

        contentValues = getSplitMainContentValue(contentValues, splitCollectionEntity);
        contentValues = getMAContentValue(contentValues, splitCollectionEntity.qualityParams);
        contentValues = getWSContentValue(contentValues, splitCollectionEntity.quantityParams);
        contentValues = getRateContentValue(contentValues, splitCollectionEntity.rateParams);


        return contentValues;
    }

    /**
     * total 17 items
     *
     * @param contentValues
     * @param splitCollectionEntity
     * @return
     */

    public ContentValues getSplitMainContentValue(ContentValues contentValues,
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
        contentValues.put(SplitRecordTable.SEQUENCE_NUMBER, splitCollectionEntity.sampleNumber);
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


        return contentValues;

    }

    /**
     * total 23 items
     *
     * @param contentValues
     * @param qualityParams
     * @return
     */

    public ContentValues getMAContentValue(ContentValues contentValues, QualityParams qualityParams) {

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

    public ContentValues getWSContentValue(ContentValues contentValues, QuantityParams quantityParams) {

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
    public ContentValues getRateContentValue(ContentValues contentValues, RateParams rateParams) {


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

    public AgentSplitEntity getSplitCollectionEntityFromCursor(Cursor cursor)

    {
        AgentSplitEntity splitCollectionEntity = new AgentSplitEntity();
        if (cursor != null) {

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

            splitCollectionEntity.seqNum = cursor.getInt(
                    cursor.getColumnIndex(SplitRecordTable.SEQUENCE_NUMBER));

            splitCollectionEntity.commited = cursor.getInt(
                    cursor.getColumnIndex(SplitRecordTable.COMMITED));
            splitCollectionEntity.userId = cursor.getString(
                    cursor.getColumnIndex(SplitRecordTable.USER));

        }
        return splitCollectionEntity;
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


}
