package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.MCCCollectionId;
import com.devapp.devmain.postentities.MCCPostEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/1/18.
 */

public class MCCMilkCollectionRecordSet extends RecordSet {


    private static MCCMilkCollectionRecordSet mccMilkCollectionRecordSet;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;

    private MCCMilkCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static MCCMilkCollectionRecordSet getInstance(Context context) {

        if (mccMilkCollectionRecordSet == null) {
            mContext = context;
            mccMilkCollectionRecordSet = new MCCMilkCollectionRecordSet();
        }

        return mccMilkCollectionRecordSet;
    }


    @Override
    String getRecordType() {
        return CollectionConstants.MCC_MILK_COLLECTION;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = mccMilkCollectionRecordSet.getQueryForUnsentDatesAndShifts(
                RecordSet.MCC_TYPE);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.MCC_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }

    @Override
    ArrayList getUnsentRecords(DateShiftEntry dateShiftEntry) {

        String query = mccMilkCollectionRecordSet.getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.MCC_TYPE);
        ArrayList<MCCPostEntity> allMccPostRecords = getCollectionList(query);
        return allMccPostRecords;
    }


    public ArrayList<MCCPostEntity> getCollectionList(String query) {

        ArrayList<MCCPostEntity> allPostFarmerRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {

            do {

                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);
                MCCPostEntity mccPostEntity = getMCCpostEntityFromReportEntity(reportEntity);
                allPostFarmerRecords.add(mccPostEntity);

            } while (cursor.moveToNext());
        }

        RecordSet.closeCursor(cursor);

        return allPostFarmerRecords;

    }

    public MCCPostEntity getMCCpostEntityFromReportEntity(ReportEntity reportEntity) {
        MCCPostEntity mccPostEntity = new MCCPostEntity();

        mccPostEntity.columnId = reportEntity.columnId;
        mccPostEntity.sequenceNumber =
                reportEntity.sequenceNum;
        mccPostEntity.mode = reportEntity.manual;
        mccPostEntity.milkType = reportEntity.milkType;
        mccPostEntity.collectionTime = SmartCCUtil.getCollectionDateFromLongTime(
                reportEntity.miliTime);

        mccPostEntity.status = reportEntity.status;
        mccPostEntity.numberOfCans = reportEntity.numberOfCans;
        mccPostEntity.collectionRoute = reportEntity.centerRoute;
        mccPostEntity.sampleNumber = reportEntity.sampleNumber;
        mccPostEntity.lastModified = reportEntity.lastModified;

        mccPostEntity.qualityParams = reportEntity.getQualityParams();
        mccPostEntity.quantityParams = reportEntity.getQuantityParams();
        mccPostEntity.rateParams = reportEntity.getRateParams();

        MCCCollectionId farmerCollectionId = new MCCCollectionId();

        farmerCollectionId.agent = reportEntity.agentId;
        farmerCollectionId.user = reportEntity.user;
        farmerCollectionId.producer = reportEntity.farmerId;

        mccPostEntity.date = reportEntity.postDate;
        mccPostEntity.shift = reportEntity.postShift;
        mccPostEntity.sentStatus = reportEntity.sentStatus;
        mccPostEntity.smsSentStatus = reportEntity.sentSmsStatus;

        mccPostEntity.collectionId = farmerCollectionId;
        mccPostEntity.recordStatus = reportEntity.recordStatus;
        mccPostEntity.calculateMin(mccPostEntity.collectionTime.getTime());
        mccPostEntity.calculateMax(mccPostEntity.collectionTime.getTime());
        return mccPostEntity;
    }

}

