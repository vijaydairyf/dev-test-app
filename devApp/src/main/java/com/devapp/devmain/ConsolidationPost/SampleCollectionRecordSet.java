package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.SamplePostEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/1/18.
 */

public class SampleCollectionRecordSet extends RecordSet {


    private static SampleCollectionRecordSet sampleMilkCollectionRecord;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;

    private SampleCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static SampleCollectionRecordSet getInstance(Context context) {

        if (sampleMilkCollectionRecord == null) {
            mContext = context;
            sampleMilkCollectionRecord = new SampleCollectionRecordSet();
        }

        return sampleMilkCollectionRecord;
    }


    @Override
    String getRecordType() {
        return null;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = getQueryForUnsentDatesAndShifts(
                RecordSet.SAMPLE_TYPE);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    ArrayList getUnsentRecords(DateShiftEntry dateShiftEntry) {

        String query = sampleMilkCollectionRecord.getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.SAMPLE_TYPE);
        ArrayList<SamplePostEntity> allSamplePostRecords = getCollectionList(query);
        return allSamplePostRecords;
    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.SAMPLE_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }


    public ArrayList<SamplePostEntity> getCollectionList(String query) {

        Cursor cursor = sqliteDatabase.rawQuery(query, null);

        ArrayList<SamplePostEntity> allSampleEnteties = new ArrayList<>();


        if (cursor != null && cursor.moveToFirst()) {

            do {

                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);
                SamplePostEntity samplePostEntity = getSamplePostEntityFromReportEntity(reportEntity);

                allSampleEnteties.add(samplePostEntity);

            } while (cursor.moveToNext());
        }
        return allSampleEnteties;

    }


    public SamplePostEntity getSamplePostEntityFromReportEntity(ReportEntity reportEntity) {

        SamplePostEntity samplePostEntity = new SamplePostEntity();

        samplePostEntity.columnId = reportEntity.columnId;
        samplePostEntity.sequenceNumber = reportEntity.sequenceNum;
        samplePostEntity.producerId = reportEntity.farmerId;
        samplePostEntity.collectionTime = ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                reportEntity.miliTime);
        samplePostEntity.status = reportEntity.status;
        samplePostEntity.milkType = reportEntity.milkType;
        samplePostEntity.qualityParams = reportEntity.getQualityParams();
        samplePostEntity.quantityParams = reportEntity.getQuantityParams();
        samplePostEntity.rateParams = reportEntity.getRateParams();
        samplePostEntity.lastModified = reportEntity.lastModified;
        samplePostEntity.date = reportEntity.postDate;
        samplePostEntity.shift = reportEntity.postShift;
        samplePostEntity.sentStatus = reportEntity.sentStatus;
        samplePostEntity.smsSentStatus = reportEntity.sentSmsStatus;
        samplePostEntity.calculateMin(samplePostEntity.collectionTime.getTime());
        samplePostEntity.calculateMax(samplePostEntity.collectionTime.getTime());

        return samplePostEntity;


    }

}

