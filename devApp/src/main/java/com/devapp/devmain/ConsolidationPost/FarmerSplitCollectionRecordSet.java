package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.FarmerSplitCollectionEntity;
import com.devapp.devmain.postentities.FarmerSplitSubRecords;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.SplitRecordTable;
import com.devapp.smartcc.database.AgentDatabase;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/1/18.
 */

public class FarmerSplitCollectionRecordSet extends RecordSet {


    private static FarmerSplitCollectionRecordSet farmerSplitCollectionRecordSet;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    AgentDatabase agentDatabase;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;

    private FarmerSplitCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        agentDatabase = databaseHandler.getAgentDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static FarmerSplitCollectionRecordSet getInstance(Context context) {

        if (farmerSplitCollectionRecordSet == null) {
            mContext = context;
            farmerSplitCollectionRecordSet = new FarmerSplitCollectionRecordSet();
        }

        return farmerSplitCollectionRecordSet;
    }


    @Override
    String getRecordType() {
        return CollectionConstants.FARMER_SPLIT_COLLECTION;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = farmerSplitCollectionRecordSet.getQueryForUnsentDatesAndShifts(
                RecordSet.FARMER_SPLIT);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    ArrayList getUnsentRecords(DateShiftEntry dateShiftEntry) {

        String query = farmerSplitCollectionRecordSet.getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.FARMER_SPLIT);
        ArrayList<FarmerSplitCollectionEntity> allSamplePostRecords = getCollectionList(query);
        return allSamplePostRecords;
    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.FARMER_SPLIT);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }


    public ArrayList<FarmerSplitCollectionEntity> getCollectionList(String query)

    {
        Cursor cursor = sqliteDatabase.rawQuery(query, null);
        ArrayList<FarmerSplitCollectionEntity> allSplitCollection = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {

            do {


                FarmerSplitCollectionEntity splitCollectionEntity
                        = new FarmerSplitCollectionEntity();
                splitCollectionEntity.parentSeqNum = cursor.getInt(cursor.getColumnIndex(
                        SplitRecordTable.PARENT_SEQ_NUM));


                splitCollectionEntity.columnId = cursor.getLong(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_COLUMNID));
                splitCollectionEntity.aggregateFarmerId = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.AGENT_ID));
//                splitCollectionEntity.parentSeqNum = databaseHandler.getSampleNumber(
//                        splitCollectionEntity.columnId,CollectionConstants.REPORT_TYPE_AGENT_SPLIT);
                splitCollectionEntity.agentId = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.AGENT_ID));
                splitCollectionEntity.centerId = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.SOCIETYID));
                splitCollectionEntity.date = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.DATE));
                splitCollectionEntity.collectionType = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.TYPE));

                splitCollectionEntity.sent = cursor.getInt(
                        cursor.getColumnIndex(SplitRecordTable.SENT));
                splitCollectionEntity.smsSentStatus = cursor.getInt(
                        cursor.getColumnIndex(SplitRecordTable.SMS_SENT_STATUS));
                splitCollectionEntity.sent = cursor.getInt(cursor.getColumnIndex(SplitRecordTable.SENT));
                splitCollectionEntity.shift = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.POST_SHIFT));
                splitCollectionEntity.date = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.POST_DATE));
                splitCollectionEntity.commited = cursor.getInt(
                        cursor.getColumnIndex(SplitRecordTable.COMMITED));


                splitCollectionEntity.farmerSplitEntries = getAllSplitSubRecords(splitCollectionEntity.parentSeqNum,
                        splitCollectionEntity.date, splitCollectionEntity.shift);

//                splitCollectionEntity.calculateMin(splitCollectionEntity.da.getTime());
//                splitCollectionEntity.calculateMax(splitCollectionEntity.collectionTime.getTime());

                if (allSplitCollection.contains(splitCollectionEntity)) {
                    continue;
                } else {
                    allSplitCollection.add(splitCollectionEntity);
                }
            } while (cursor.moveToNext());

        }

        RecordSet.closeCursor(cursor);
      /*  if (allSplitCollection.size() > 0) {
            Set<FarmerSplitCollectionEntity> setEntries =
                    new LinkedHashSet<>(allSplitCollection);

            allSplitCollection = new ArrayList<>(setEntries);
        }*/

        return allSplitCollection;
    }

    public ArrayList<FarmerSplitSubRecords> getAllSplitSubRecords(long parentSequenceNum,
                                                                  String date, String shift) {
        String query = "Select * from " + SplitRecordTable.TABLE + " Where "
                + SplitRecordTable.PARENT_SEQ_NUM + " = " + parentSequenceNum + " AND "
                + SplitRecordTable.POST_DATE + " ='" + date + "' " + " AND " +
                SplitRecordTable.POST_SHIFT + " ='" + shift + "'" + " AND " + SplitRecordTable.SENT + " = " + 0;
        ArrayList<FarmerSplitSubRecords> allFarmSubRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                FarmerSplitSubRecords farmerSplitSubRecords = new FarmerSplitSubRecords();

                farmerSplitSubRecords.columnId = cursor.getLong(
                        cursor.getColumnIndex(SplitRecordTable.COLUMNID));

                farmerSplitSubRecords.milkType = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.MILKTYPE));


                farmerSplitSubRecords.collectionTime = ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                        cursor.getLong(
                                cursor.getColumnIndex(SplitRecordTable.COLLECTION_TIME)));

                farmerSplitSubRecords.producerId = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.FARMERID));

                farmerSplitSubRecords.status = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.RECORD_STATUS));
                farmerSplitSubRecords.mode = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.ENTRY_MODE));


                farmerSplitSubRecords.userId = cursor.getString(
                        cursor.getColumnIndex(SplitRecordTable.USER));

                farmerSplitSubRecords.qualityParams = consolidatedHelperMethods.fromQualityParams(
                        agentDatabase.getQualityParamsFromCursor(cursor));
                farmerSplitSubRecords.quantityParams = consolidatedHelperMethods.fromQuantityParams(
                        agentDatabase.getQuantityParamsFromCursor(cursor));
                farmerSplitSubRecords.rateParams = consolidatedHelperMethods.fromRateParams(
                        agentDatabase.getRateParamsFromCursor(cursor));
                farmerSplitSubRecords.sequenceNumber =
                        cursor.getLong(cursor.getColumnIndex(SplitRecordTable.SEQUENCE_NUMBER));

                allFarmSubRecords.add(farmerSplitSubRecords);
            } while (cursor.moveToNext());
        }

        return allFarmSubRecords;

    }

}


