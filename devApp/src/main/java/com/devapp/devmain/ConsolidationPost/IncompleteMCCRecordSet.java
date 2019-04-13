package com.devapp.devmain.ConsolidationPost;

/**
 * Created by u_pendra on 20/1/18.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.IncompleteCenterCollection;
import com.devapp.devmain.postentities.MCCCollectionId;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;


public class IncompleteMCCRecordSet extends RecordSet {


    private static IncompleteMCCRecordSet incompleteMCCRecordSet;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    ValidationHelper validationHelper;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;

    private IncompleteMCCRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);
        validationHelper = new ValidationHelper();

    }

    public static IncompleteMCCRecordSet getInstance(Context context) {

        if (incompleteMCCRecordSet == null) {
            mContext = context;
            incompleteMCCRecordSet = new IncompleteMCCRecordSet();
        }

        return incompleteMCCRecordSet;
    }


    @Override
    String getRecordType() {
        return CollectionConstants.INCOMPLETE_MCC_RECORDS;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = getQueryForUnsentDatesAndShifts(
                RecordSet.INCOMPLETE_MCC);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    ArrayList<? extends SynchronizableElement> getUnsentRecords(DateShiftEntry dateShiftEntry) {
        String query = getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.INCOMPLETE_MCC);
        ArrayList<IncompleteCenterCollection> allFarmerPostRecords = getCollectionList(query);
        return allFarmerPostRecords;
    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.INCOMPLETE_MCC);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }


    public ArrayList<IncompleteCenterCollection> getCollectionList(String query) {

        ArrayList<IncompleteCenterCollection> allPostFarmerRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {

            do {
                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);
                IncompleteCenterCollection farmerPostEntity = getIncompleteReportFromReportEntity(reportEntity);
                allPostFarmerRecords.add(farmerPostEntity);

            } while (cursor.moveToNext());
        }
        RecordSet.closeCursor(cursor);
        return allPostFarmerRecords;

    }


    public IncompleteCenterCollection getIncompleteReportFromReportEntity(ReportEntity reportEntity) {

        IncompleteCenterCollection incompleteMCCEntity = new IncompleteCenterCollection();


        incompleteMCCEntity.columnId = reportEntity.columnId;
        incompleteMCCEntity.sequenceNumber = reportEntity.sequenceNum;
        incompleteMCCEntity.mode = reportEntity.manual;
        incompleteMCCEntity.milkType = reportEntity.milkType;
        incompleteMCCEntity.collectionTime = SmartCCUtil.getCollectionDateFromLongTime(
                reportEntity.miliTime);
        incompleteMCCEntity.status = reportEntity.status;
        incompleteMCCEntity.numberOfCans = reportEntity.numberOfCans;
        incompleteMCCEntity.collectionRoute = reportEntity.centerRoute;
        incompleteMCCEntity.sampleNumber = reportEntity.sampleNumber;
        incompleteMCCEntity.lastModified = reportEntity.lastModified;
        incompleteMCCEntity.qualityParams = reportEntity.getQualityParams();
        incompleteMCCEntity.quantityParams = reportEntity.getQuantityParams();
        incompleteMCCEntity.rateParams = reportEntity.getRateParams();
        MCCCollectionId farmerCollectionId = new MCCCollectionId();

        farmerCollectionId.agent = reportEntity.agentId;
        farmerCollectionId.user = reportEntity.user;
        farmerCollectionId.producer = reportEntity.farmerId;

        incompleteMCCEntity.collectionId = farmerCollectionId;
        incompleteMCCEntity.date = reportEntity.postDate;
        incompleteMCCEntity.shift = reportEntity.postShift;
        incompleteMCCEntity.sentStatus = reportEntity.sentStatus;
        incompleteMCCEntity.smsSentStatus = reportEntity.sentSmsStatus;

        incompleteMCCEntity.calculateMin(incompleteMCCEntity.collectionTime.getTime());
        incompleteMCCEntity.calculateMax(incompleteMCCEntity.collectionTime.getTime());

        return incompleteMCCEntity;
    }

}

