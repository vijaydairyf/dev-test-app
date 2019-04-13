package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.FarmerCollectionId;
import com.devapp.devmain.postentities.FarmerPostEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/1/18.
 */

public class FarmerMilkCollectionRecordSet extends RecordSet {


    private static FarmerMilkCollectionRecordSet farmerMilkCollectionRecordSet;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;


    private FarmerMilkCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static FarmerMilkCollectionRecordSet getInstance(Context context) {

        if (farmerMilkCollectionRecordSet == null) {
            mContext = context;
            farmerMilkCollectionRecordSet = new FarmerMilkCollectionRecordSet();
        }

        return farmerMilkCollectionRecordSet;
    }


    @Override
    String getRecordType() {
        return "FARMER";
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = getQueryForUnsentDatesAndShifts(
                RecordSet.FARMER_TYPE);
        ArrayList<DateShiftEntry> dataEntryList = databaseHandler.getDayAndEntryList(query);

        return dataEntryList;

    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.FARMER_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }


    @Override
    ArrayList<? extends SynchronizableElement> getUnsentRecords(DateShiftEntry dateShiftEntry) {
        String query = getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.FARMER_TYPE);
        ArrayList<FarmerPostEntity> allFarmerPostRecords = getCollectionList(query);
        return allFarmerPostRecords;
    }


    public ArrayList<FarmerPostEntity> getCollectionList(String query) {

        ArrayList<FarmerPostEntity> allPostFarmerRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {

            do {
                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);
                FarmerPostEntity farmerPostEntity = getFarmerPostEntityFromReportEntity(reportEntity);
                allPostFarmerRecords.add(farmerPostEntity);
            } while (cursor.moveToNext());
        }
        RecordSet.closeCursor(cursor);
        return allPostFarmerRecords;
    }


    public FarmerPostEntity getFarmerPostEntityFromReportEntity(ReportEntity reportEntity) {
        FarmerPostEntity farmerPostEntity = new FarmerPostEntity();
        farmerPostEntity.columnId = reportEntity.columnId;
        farmerPostEntity.sequenceNumber = reportEntity.sequenceNum;
        farmerPostEntity.mode = reportEntity.manual;
        farmerPostEntity.milkType = reportEntity.milkType;
        farmerPostEntity.collectionTime = SmartCCUtil.getCollectionDateFromLongTime(
                reportEntity.miliTime);
        farmerPostEntity.status = reportEntity.status;
        farmerPostEntity.numberOfCans = reportEntity.numberOfCans;
        farmerPostEntity.collectionRoute = reportEntity.centerRoute;
        farmerPostEntity.sampleNumber = reportEntity.sampleNumber;
        farmerPostEntity.lastModified = reportEntity.lastModified;

        farmerPostEntity.qualityParams = reportEntity.getQualityParams();
        farmerPostEntity.quantityParams = reportEntity.getQuantityParams();
        farmerPostEntity.rateParams = reportEntity.getRateParams();

        FarmerCollectionId farmerCollectionId = new FarmerCollectionId();
        farmerCollectionId.aggregateFarmer = reportEntity.agentId;
        farmerCollectionId.user = reportEntity.user;
        farmerCollectionId.producer = reportEntity.farmerId;

        farmerPostEntity.collectionId = farmerCollectionId;
        farmerPostEntity.date = reportEntity.postDate;
        farmerPostEntity.shift = reportEntity.postShift;
        farmerPostEntity.sentStatus = reportEntity.sentStatus;
        farmerPostEntity.smsSentStatus = reportEntity.sentSmsStatus;
        farmerPostEntity.recordStatus = reportEntity.recordStatus;
        farmerPostEntity.calculateMin(farmerPostEntity.collectionTime.getTime());
        farmerPostEntity.calculateMax(farmerPostEntity.collectionTime.getTime());

        return farmerPostEntity;
    }


}
