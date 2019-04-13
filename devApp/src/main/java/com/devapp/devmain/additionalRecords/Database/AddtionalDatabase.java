package com.devapp.devmain.additionalRecords.Database;

/**
 * Created by u_pendra on 29/5/17.
 * This class
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.devapp.devmain.additionalRecords.AdditionalParamsEntity;
import com.devapp.devmain.additionalRecords.CustomFieldEntity;
import com.devapp.devmain.dbbackup.DatabaseSync;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.ExtraParams;
import com.devapp.devmain.util.AdvanceUtil;

import java.util.ArrayList;
import java.util.Arrays;

import static com.devapp.devmain.tableEntities.ExtraParams.TABLE_EXTRA_PARAMS;

public class AddtionalDatabase {

    public static final String TABLE_CUSTOM_FIELDS = "tableCustomFields";


    public static final int SERVER_SENT_STATUS = 1;
    public static final int SERVER_UNSENT_STATUS = 0;
    SQLiteDatabase primaryDatabase;
    DatabaseSync databaseSync;
    Context mContext;
    SQLiteDatabase secondaryDatabase;
    AmcuConfig amcuConfig;
    DatabaseHandler databaseHandler;

    public AddtionalDatabase(Context context, SQLiteDatabase sqliteDatabase, DatabaseSync
            databaseSync) {
        this.mContext = context;
        this.primaryDatabase = sqliteDatabase;
        this.databaseSync = databaseSync;
        amcuConfig = AmcuConfig.getInstance();
        this.secondaryDatabase = databaseSync.getSqliteDatabase();
        databaseHandler = DatabaseHandler.getDatabaseInstance();
    }

    /**
     * Query to create CustomFields Table
     *
     * @return
     */
    public static String getCreateQueryForCustomFields() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOM_FIELDS + " ( "
                + CustomFields.COLUMN_ID + " INTEGER PRIMARY KEY,"
                + CustomFields.NAME + " TEXT UNIQUE,"
                + CustomFields.DISPLAY_NAME + " TEXT,"
                + CustomFields.TYPE + " TEXT,"
                + CustomFields.EDITABLE + " INTEGER,"
                + CustomFields.HINT + " TEXT,"
                + CustomFields.APPLICABLE_FOR + " TEXT,"
                + CustomFields.VALIDATION + " TEXT )";
    }

    public static String queryForCustomFieldInsert(String name) {
        String query = "SELECT * FROM " + TABLE_CUSTOM_FIELDS + " WHERE " +
                CustomFields.NAME + " = '" + name + "'";
        return query;
    }

    public static String getAllCustomFieldQuery() {
        String query = "SELECT * FROM " + TABLE_CUSTOM_FIELDS;
        return query;
    }


    public void insertOrUpdateCustomFields(ArrayList<CustomFieldEntity> entityList) {

        primaryDatabase.beginTransaction();
        for (int i = 0; i < entityList.size(); i++) {
            long colId = -1;
            Cursor cursor = primaryDatabase.rawQuery(queryForCustomFieldInsert(entityList.get(i).name), null);
            if (cursor != null && cursor.moveToFirst()) {
                colId = cursor.getLong(0);
            }

            ContentValues contentValues = getContentValuesForCustomFields(entityList.get(i));

            try {
                if (colId == -1) {
                    colId = databaseSync.syncInsert(primaryDatabase, TABLE_CUSTOM_FIELDS, contentValues);
                } else {
                    databaseSync.syncUpdate(primaryDatabase, TABLE_CUSTOM_FIELDS, contentValues,
                            CustomFields.COLUMN_ID + " =?",
                            new String[]{Long.toString(cursor.getLong(0))});
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        primaryDatabase.setTransactionSuccessful();
        primaryDatabase.endTransaction();


    }

    public void deleteAllCustomFields() {
        primaryDatabase.delete(TABLE_CUSTOM_FIELDS, null, null);
        if (AdvanceUtil.isSecondaryDatabaseExist(mContext)) {
            secondaryDatabase.delete(TABLE_CUSTOM_FIELDS, null, null);
        }
    }

    public ArrayList<CustomFieldEntity> getCustomFieldsFromQuery(String query) {
        ArrayList<CustomFieldEntity> entities = new ArrayList<>();
        Cursor cursor = primaryDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CustomFieldEntity cFE = getCustomFieldFromCursor(cursor);
                entities.add(cFE);
            } while (cursor.moveToNext());
        }

        return entities;
    }

    private CustomFieldEntity getCustomFieldFromCursor(Cursor cursor) {
        CustomFieldEntity cFE = new CustomFieldEntity();
        cFE.name = cursor.getString(cursor.getColumnIndex(CustomFields.NAME));
        cFE.displayName = cursor.getString(cursor.getColumnIndex(CustomFields.DISPLAY_NAME));
        cFE.type = cursor.getString(cursor.getColumnIndex(CustomFields.TYPE));
        cFE.editable = (cursor.getInt(cursor.getColumnIndex(CustomFields.EDITABLE)) == 0) ? false : true;
        cFE.applicableFor = new ArrayList<>(Arrays.asList(cursor.getString(cursor.getColumnIndex(CustomFields.APPLICABLE_FOR)).split(",")));
        cFE.hint = cursor.getString(cursor.getColumnIndex(CustomFields.HINT));
        cFE.validation = cursor.getString(cursor.getColumnIndex(CustomFields.VALIDATION));

        return cFE;
    }

    private ContentValues getContentValuesForCustomFields(CustomFieldEntity entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CustomFields.NAME, entity.name);
        contentValues.put(CustomFields.DISPLAY_NAME, entity.displayName);
        contentValues.put(CustomFields.TYPE, entity.type);
        contentValues.put(CustomFields.EDITABLE, entity.editable ? 1 : 0);
        contentValues.put(CustomFields.APPLICABLE_FOR, TextUtils.join(",", entity.applicableFor));
        contentValues.put(CustomFields.HINT, entity.hint);
        contentValues.put(CustomFields.VALIDATION, entity.validation);
        return contentValues;
    }

//    public void insertExtraParams(ArrayList<ExtraParamEntity> entityList){
//        primaryDatabase.beginTransaction();
//        for (int i = 0; i<entityList.size(); i++){
//            insertExtraParams(entityList.get(i));
//        }
//        primaryDatabase.setTransactionSuccessful();
//        primaryDatabase.endTransaction();
//    }


    public long updateUnsentExtraParams(ArrayList<AdditionalParamsEntity> allExtraParam) {
        long collID = -1;
        if (allExtraParam == null) {
            return collID;
        }
        for (int i = 0; i < allExtraParam.size(); i++) {
            allExtraParam.get(i).sentStatus = SERVER_SENT_STATUS;
            ContentValues contentValues = getContentValueForExtraParam(allExtraParam.get(i));
            String filter = ExtraParams.COLUMN_ID + " =?";
            try {
                collID = databaseSync.syncUpdateDB(primaryDatabase, TABLE_EXTRA_PARAMS, contentValues
                        , filter, new String[]{Long.toString(allExtraParam.get(i).columnId)});
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (AdvanceUtil.isSecondaryDatabaseExist(mContext)) {
                try {
                    collID = databaseSync.syncUpdateDB(secondaryDatabase, TABLE_EXTRA_PARAMS, contentValues
                            , filter, new String[]{Long.toString(allExtraParam.get(i).columnId)});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return collID;

    }


    public ArrayList<AdditionalParamsEntity> getExtraParamListByQuery(String query) {
        ArrayList<AdditionalParamsEntity> entities = new ArrayList<>();
        Cursor cursor = primaryDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                AdditionalParamsEntity ePE = getExtraParamFromCursor(cursor);

                entities.add(ePE);
            } while (cursor.moveToNext());
        }

        return entities;
    }

    public AdditionalParamsEntity getExtraParamByQuery(String query) {
        AdditionalParamsEntity entity = null;
        Cursor cursor = primaryDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            entity = getExtraParamFromCursor(cursor);
        }
        return entity;
    }

    public long getExtraParamUnsentCount() {
        String query = "SELECT COUNT(*) FROM " + TABLE_EXTRA_PARAMS + " WHERE "
                + ExtraParams.SENT_STATUS + " = " + SERVER_UNSENT_STATUS;
        Cursor cursor = primaryDatabase.rawQuery(query, null);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }


    private AdditionalParamsEntity getExtraParamFromCursor(Cursor cursor) {
        AdditionalParamsEntity ePE = new AdditionalParamsEntity();
        // ePE.columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));

        Long columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));
        ePE.columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));
        ePE.key = cursor.getString(cursor.getColumnIndex(ExtraParams.KEY));
        ePE.extraParameters = cursor.getString(cursor.getColumnIndex(ExtraParams.VALUE));
        ePE.refSeqNum = cursor.getLong(cursor.getColumnIndex(ExtraParams.REF_SEQ_NUM));
        // ePE.collectionType = cursor.getString(cursor.getColumnIndex(ExtraParams.COLL_TYPE));
        ePE.updatedTime = cursor.getLong(cursor.getColumnIndex(ExtraParams.TIME));
        //ePE.committed = cursor.getInt(cursor.getColumnIndex(ExtraParams.COMMITTED));
        ePE.sentStatus = cursor.getInt(cursor.getColumnIndex(ExtraParams.SENT_STATUS));

        long collId = DatabaseHandler.getDatabaseInstance().getCollIdSequenceNumber(
                ePE.refSeqNum);

        ReportEntity reportEntity = databaseHandler.getReportEntity(
                DatabaseEntity.getReportFromColId(collId));

        ePE.collectionTime = reportEntity.miliTime;
        ePE.milkType = reportEntity.milkType;
        ePE.date = reportEntity.postDate;
        if (reportEntity.postShift.equalsIgnoreCase("Morning")) {
            ePE.shift = "MORNING";
        } else {
            ePE.shift = "EVENING";
        }
        ePE.farmerId = reportEntity.farmerId;
        ePE.seqNum = cursor.getLong(cursor.getColumnIndex(ExtraParams.SEQUENCE_NUMBER));

        return ePE;
    }

    private ContentValues getContentValueForExtraParam(AdditionalParamsEntity entity) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ExtraParams.KEY, entity.key);
        contentValues.put(ExtraParams.VALUE, entity.extraParameters);
        contentValues.put(ExtraParams.REF_SEQ_NUM, entity.refSeqNum);
        //   contentValues.put(ExtraParams.COLL_TYPE, entity.collectionType);
        contentValues.put(ExtraParams.TIME, entity.updatedTime);
        // contentValues.put(ExtraParams.COMMITTED, entity.committed);
        contentValues.put(ExtraParams.SENT_STATUS, entity.sentStatus);

        contentValues.put(ExtraParams.COLLECTION_TIME, entity.collectionTime);
        contentValues.put(ExtraParams.POST_DATE, entity.date);
        contentValues.put(ExtraParams.POST_SHIFT, entity.shift);
        contentValues.put(ExtraParams.SMS_SENT_STATUS, entity.smsSentStatus);

        contentValues.put(ExtraParams.POST_SHIFT, entity.postShift);
        contentValues.put(ExtraParams.POST_DATE, entity.postDate);
        contentValues.put(ExtraParams.SEQUENCE_NUMBER, entity.seqNum);


        return contentValues;
    }


/*    public ArrayList<PostEndShift> createPostEndShiftForExtraPrams(ArrayList<AdditionalParamsEntity>
                                                                           allExtraParams) {

        ArrayList<PostEndShift> allPostEndShift = new ArrayList<>();

        if (allExtraParams == null || allExtraParams.size() == 0) {
            return allPostEndShift;
        }

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        AdvanceUtil advanceUtil = new AdvanceUtil(mContext);

        String shift = null;
        long collectionTime = 0;
        String date = null;
        PostEndShift postEndShift = new PostEndShift();
        postEndShift.extraParametersUpdatedFarmerCollectionEntryList = new ArrayList<>();
        SessionManager sessionManager = new SessionManager(mContext);
        for (int i = 0; i < allExtraParams.size(); i++) {
            AdditionalParamsEntity additionalParamsEntity = allExtraParams.get(i);
            long collId = DatabaseHandler.getDatabaseInstance().getCollIdSequenceNumber(
                    additionalParamsEntity.refSeqNum);
            ReportEntity reportEntity = dbh.getReportEntity(
                    DatabaseEntity.getReportFromColId(collId));
//            canCollectionEntry.shift = AdvanceUtil.getFullShift(canCollectionEntry.shift);
            String mDate = reportEntity.date;

            if ((collectionTime == 0 && shift == null) ||
                    (!mDate.equalsIgnoreCase(date)) ||
                    (!shift.equalsIgnoreCase(reportEntity.shift))) {
                postEndShift = new PostEndShift();

                postEndShift.extraParametersUpdatedFarmerCollectionEntryList = new ArrayList<>();
                postEndShift.shift = AdvanceUtil.getFullShift(reportEntity.shift);
                postEndShift.collectionDate = new
                        AdvanceUtil(mContext).changeDateFormat(reportEntity.date,
                        "dd-MMM-yyy", "yyyy-MM-dd");

                postEndShift.startTime = reportEntity.miliTime;
                postEndShift.endTime = reportEntity.miliTime;
                postEndShift.societyId = sessionManager.getCollectionID();
                postEndShift.deviceId = amcuConfig.getDeviceID();
                postEndShift.timeZone =
                        new SmartCCUtil(mContext).
                                getCalendarInstance(0).getTimeZone().getID();

                shift = reportEntity.shift;
                date = reportEntity.date;
                collectionTime = reportEntity.miliTime;

            } else {

                postEndShift.endTime = reportEntity.miliTime;

            }


            if (allPostEndShift != null && allPostEndShift.size() > 0
                    && allPostEndShift.contains(postEndShift)) {
                for (int j = 0; j < allPostEndShift.size(); j++) {
                    if (postEndShift.collectionDate.equalsIgnoreCase(
                            allPostEndShift.get(j).collectionDate)
                            && postEndShift.shift.equalsIgnoreCase(
                            allPostEndShift.get(j).shift)) {

                        allPostEndShift.get(j).extraParametersUpdatedFarmerCollectionEntryList.add(additionalParamsEntity);
                        allPostEndShift.get(j).endTime = reportEntity.miliTime;

                        break;
                    }
                }
            } else {
                postEndShift.extraParametersUpdatedFarmerCollectionEntryList.add(additionalParamsEntity);
                postEndShift.farmerCollectionEntryList = new ArrayList<>();
                postEndShift.shiftConcluded = advanceUtil.gtConcludedDateAndShift();
                postEndShift.deviceId = amcuConfig.getDeviceID();
                postEndShift.timeZone =
                        new SmartCCUtil(mContext).
                                getCalendarInstance(0).getTimeZone().getID();
                allPostEndShift.add(postEndShift);
            }


        }

        return allPostEndShift;
    }*/


/*    private SQLiteDatabase getSqliteDatabase(String isPrimaryOrSecondary) {
        SQLiteDatabase sqLiteDatabase = null;

        if (isPrimaryOrSecondary.equalsIgnoreCase(DatabaseHandler.isPrimary)) {
            sqLiteDatabase = primaryDatabase;
        } else if (isPrimaryOrSecondary.equalsIgnoreCase(DatabaseHandler.isSecondary) &&
                AdvanceUtil.isSecondaryDatabaseExist(mContext) && secondaryDatabase != null) {
            sqLiteDatabase = secondaryDatabase;
        }
        return sqLiteDatabase;
    }*/


    public interface CustomFields {
        String COLUMN_ID = "_id";
        String NAME = "name";
        String DISPLAY_NAME = "displayName";
        String TYPE = "type";
        String EDITABLE = "editable";
        String APPLICABLE_FOR = "applicableFor";
        String HINT = "hint";
        String VALIDATION = "validation";
    }


}
