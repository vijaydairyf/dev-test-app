package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.postentities.MilkAnalyser;
import com.devapp.devmain.postentities.QualityParamsPost;
import com.devapp.devmain.postentities.QualityReadingData;
import com.devapp.devmain.postentities.QuantityParamspost;
import com.devapp.devmain.postentities.WeighingScaleData;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.DispatchCollectionTable;

import java.util.ArrayList;

/**
 * Created by xx on 9/9/18.
 */

public class DispatchCollectionRecordSet extends RecordSet {

    private static DispatchCollectionRecordSet dispatchCollectionRecordSet;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;

    private DispatchCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static DispatchCollectionRecordSet getInstance(Context context) {

        if (dispatchCollectionRecordSet == null) {
            mContext = context;
            dispatchCollectionRecordSet = new DispatchCollectionRecordSet();
        }

        return dispatchCollectionRecordSet;
    }

    @Override
    String getRecordType() {
        return null;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {
        String query = dispatchCollectionRecordSet.getQueryForUnsentDatesAndShifts(
                RecordSet.DISPATCH_TYPE);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;
    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.DISPATCH_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }

    @Override
    ArrayList<? extends SynchronizableElement> getUnsentRecords(DateShiftEntry dateShiftEntry) {
        String query = dispatchCollectionRecordSet.getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.DISPATCH_TYPE);
        ArrayList<DispatchPostEntity> allDispatchPostRecords = getCollectionList(query);
        return allDispatchPostRecords;
    }


    public ArrayList<DispatchPostEntity> getCollectionList(String query) {

        ArrayList<DispatchPostEntity> allDispatchPostRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {
            do {
                DispatchPostEntity dpe = new DispatchPostEntity();

                dpe.columnId = cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.KEY_COLUMN_ID));
                dpe.supervisorId = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_SUPERVISOR_ID));
                dpe.sequenceNumber = cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.SEQUENCE_NUMBER));

                dpe.collectionTime = ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                        cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.KEY_TIME_MILLIS)));
                QualityParamsPost qualityParams = new QualityParamsPost();
                qualityParams.mode = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_MODE));
                MilkAnalyser milkAnalyzer = new MilkAnalyser();
                QualityReadingData reading = new QualityReadingData();
                reading.fat = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_FAT));
                reading.snf = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_SNF));
                milkAnalyzer.qualityReadingData = reading;
                qualityParams.milkAnalyser = milkAnalyzer;
                dpe.qualityParams = qualityParams;
                QuantityParamspost quantityParams = new QuantityParamspost();
                quantityParams.mode = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_MODE));
                quantityParams.soldQuantity = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_SOLD_QUANTITY));
                quantityParams.collectedQuantity = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_COLLECTED_QUANTITY));

                WeighingScaleData weighingScale = new WeighingScaleData();
                weighingScale.measuredValue = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY));
                weighingScale.inKg = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY_KG));
                weighingScale.inLtr = cursor.getDouble(cursor.getColumnIndex(DispatchCollectionTable.KEY_DISPATCH_AVAILABLE_QUANTITY_LTR));
                quantityParams.weighingScaleData = weighingScale;
                dpe.quantityParams = quantityParams;


                dpe.timeInMillis = cursor.getInt(cursor.getColumnIndex(DispatchCollectionTable.KEY_TIME_MILLIS));
                dpe.date = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_POST_DATE));
                dpe.shift = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_POST_SHIFT));
                dpe.sentStatus = cursor.getInt(cursor.getColumnIndex(DispatchCollectionTable.KEY_SEND_STATUS));
                dpe.lastModified = cursor.getLong(cursor.getColumnIndex(DispatchCollectionTable.LAST_MODIFIED));
                dpe.milkType = cursor.getString(cursor.getColumnIndex(DispatchCollectionTable.KEY_MILKTYPE));
                dpe.numberOfCans = cursor.getInt(cursor.getColumnIndex(DispatchCollectionTable.NO_OF_CANS));
                dpe.calculateMin(dpe.collectionTime.getTime());
                dpe.calculateMax(dpe.collectionTime.getTime());
                allDispatchPostRecords.add(dpe);

            } while (cursor.moveToNext());
        }

        RecordSet.closeCursor(cursor);

        return allDispatchPostRecords;
    }
}
