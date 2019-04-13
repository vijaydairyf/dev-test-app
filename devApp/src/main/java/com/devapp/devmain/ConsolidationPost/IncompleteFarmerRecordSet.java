package com.devapp.devmain.ConsolidationPost;

/**
 * Created by u_pendra on 20/1/18.
 */

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.FarmerCollectionId;
import com.devapp.devmain.postentities.IncompleteFarmerCollection;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;


public class IncompleteFarmerRecordSet extends RecordSet {


    private static IncompleteFarmerRecordSet incompleteFarmerRecordSet;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;


    private IncompleteFarmerRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static IncompleteFarmerRecordSet getInstance(Context context) {

        if (incompleteFarmerRecordSet == null) {
            mContext = context;
            incompleteFarmerRecordSet = new IncompleteFarmerRecordSet();
        }

        return incompleteFarmerRecordSet;
    }


    @Override
    String getRecordType() {
        return CollectionConstants.INCOMPLETE_FARMER_RECORDS;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = getQueryForUnsentDatesAndShifts(
                RecordSet.INCOMPLETE_FARMER);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.INCOMPLETE_FARMER);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }

    @Override
    ArrayList<? extends SynchronizableElement> getUnsentRecords(DateShiftEntry dateShiftEntry) {
        String query = getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.INCOMPLETE_FARMER);
        ArrayList<IncompleteFarmerCollection> allFarmerPostRecords = getCollectionList(query);
        return allFarmerPostRecords;
    }


    public ArrayList<IncompleteFarmerCollection> getCollectionList(String query) {

        ArrayList<IncompleteFarmerCollection> allPostFarmerRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {

            do {
                IncompleteFarmerCollection farmerPostEntity = new IncompleteFarmerCollection();

                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);

                farmerPostEntity.columnId = cursor.getLong(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_COLUMNID));
                farmerPostEntity.sequenceNumber =
                        cursor.getLong(cursor.getColumnIndex(FarmerCollectionTable.SEQUENCE_NUMBER));
                farmerPostEntity.mode = cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MANUAL));
                farmerPostEntity.milkType = cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));
                farmerPostEntity.collectionTime = SmartCCUtil.getCollectionDateFromLongTime(
                        cursor.getLong(
                                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME_MILLI)));

                farmerPostEntity.status = cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_STATUS));
                farmerPostEntity.numberOfCans = Integer.parseInt(
                        cursor.getString(
                                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS))
                );
                farmerPostEntity.collectionRoute = cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_ROUTE));
                farmerPostEntity.sampleNumber = cursor.getInt(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER));
                farmerPostEntity.milkType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));
                farmerPostEntity.lastModified = cursor.getLong(cursor.getColumnIndex(FarmerCollectionTable.LAST_MODIFIED));


                farmerPostEntity.qualityParams = reportEntity.getQualityParams();
                farmerPostEntity.quantityParams = reportEntity.getQuantityParams();
                farmerPostEntity.rateParams = reportEntity.getRateParams();


                FarmerCollectionId farmerCollectionId = new FarmerCollectionId();

                farmerCollectionId.aggregateFarmer = cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AGENT_ID));
                farmerCollectionId.user = cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_USER));
                farmerCollectionId.producer = cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FARMERID));

                farmerPostEntity.collectionId = farmerCollectionId;


                farmerPostEntity.date = cursor.getString(
                        cursor.getColumnIndex(FarmerCollectionTable.POST_DATE));
                farmerPostEntity.shift = cursor.getString(
                        cursor.getColumnIndex(FarmerCollectionTable.POST_SHIFT));
                farmerPostEntity.sentStatus = cursor.getInt(
                        cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_SENT_STATUS));
                farmerPostEntity.smsSentStatus = cursor.getInt(
                        cursor.getColumnIndex(FarmerCollectionTable.SMS_SENT_STATUS));

                farmerPostEntity.calculateMin(farmerPostEntity.collectionTime.getTime());
                farmerPostEntity.calculateMax(farmerPostEntity.collectionTime.getTime());


                allPostFarmerRecords.add(farmerPostEntity);

            } while (cursor.moveToNext());
        }
        RecordSet.closeCursor(cursor);
        return allPostFarmerRecords;

    }
}
