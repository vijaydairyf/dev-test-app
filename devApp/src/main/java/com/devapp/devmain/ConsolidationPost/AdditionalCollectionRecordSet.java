package com.devapp.devmain.ConsolidationPost;

/**
 * Created by u_pendra on 23/1/18.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.postentities.AdditionFieldEditEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.ExtraParams;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;


public class AdditionalCollectionRecordSet extends RecordSet {


    private static AdditionalCollectionRecordSet additionalCollectionRecordSet;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;


    private AdditionalCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static AdditionalCollectionRecordSet getInstance(Context context) {

        if (additionalCollectionRecordSet == null) {
            mContext = context;
            additionalCollectionRecordSet = new AdditionalCollectionRecordSet();
        }

        return additionalCollectionRecordSet;
    }


    @Override
    String getRecordType() {
        return CollectionConstants.REPORT_TYPE_EDITED_ADDITIONAL;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = getQueryForUnsentDatesAndShifts(
                RecordSet.ADDITIONAL_TYPE);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.ADDITIONAL_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }

    @Override
    ArrayList<? extends SynchronizableElement> getUnsentRecords(DateShiftEntry dateShiftEntry) {
        String query = getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.ADDITIONAL_TYPE);
        ArrayList<AdditionFieldEditEntity> allFarmerPostRecords = getCollectionList(query);
        return allFarmerPostRecords;
    }


    public ArrayList<AdditionFieldEditEntity> getCollectionList(String query) {

        ArrayList<AdditionFieldEditEntity> allPostFarmerEditRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {

            do {
                AdditionFieldEditEntity ePE = new AdditionFieldEditEntity();

                ePE.columnId = cursor.getLong(cursor.getColumnIndex(ExtraParams.COLUMN_ID));
                ePE.key = cursor.getString(cursor.getColumnIndex(ExtraParams.KEY));
                ePE.extraParameters = cursor.getString(cursor.getColumnIndex(ExtraParams.VALUE));
                ePE.refSeqNum = cursor.getLong(cursor.getColumnIndex(ExtraParams.REF_SEQ_NUM));
                // ePE.collectionType = cursor.getString(cursor.getColumnIndex(ExtraParams.COLL_TYPE));

                ePE.updatedTime = SmartCCUtil.getCollectionDateFromLongTime(
                        cursor.getLong(cursor.getColumnIndex(ExtraParams.TIME)));

                //ePE.committed = cursor.getInt(cursor.getColumnIndex(ExtraParams.COMMITTED));
                ePE.sentStatus = cursor.getInt(cursor.getColumnIndex(ExtraParams.SENT_STATUS));

                long collId = DatabaseHandler.getDatabaseInstance().getCollIdSequenceNumber(
                        ePE.refSeqNum);

                ReportEntity reportEntity = databaseHandler.getReportEntity(
                        DatabaseEntity.getReportFromColId(collId));

                if (reportEntity != null) {
                    ePE.farmerId = reportEntity.farmerId;
                    ePE.milkType = reportEntity.milkType;
                }

                ePE.collectionTime = SmartCCUtil.getCollectionDateFromLongTime(
                        cursor.getLong(cursor.getColumnIndex(ExtraParams.TIME)));
                ePE.date = cursor.getString(cursor.getColumnIndex(ExtraParams.POST_DATE));
                ePE.shift = cursor.getString(cursor.getColumnIndex(ExtraParams.POST_SHIFT));
                ePE.seqNum = cursor.getLong(cursor.getColumnIndex(FarmerCollectionTable.SEQUENCE_NUMBER));
                allPostFarmerEditRecords.add(ePE);

            } while (cursor.moveToNext());
        }
        return allPostFarmerEditRecords;

    }


}

