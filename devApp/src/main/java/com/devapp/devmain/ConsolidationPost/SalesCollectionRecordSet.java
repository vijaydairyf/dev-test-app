package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.agentfarmersplit.QualityParams;
import com.devapp.devmain.agentfarmersplit.QuantityParams;
import com.devapp.devmain.agentfarmersplit.RateParams;
import com.devapp.devmain.postentities.SalesPostEntity;
import com.devapp.devmain.postentities.SalesTransaction;
import com.devapp.devmain.postentities.salesEntities.Id;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.SalesCollectionTable;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/1/18.
 */

public class SalesCollectionRecordSet extends RecordSet {


    private static SalesCollectionRecordSet salesMilkCollectionRecord;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;

    private SalesCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static SalesCollectionRecordSet getInstance(Context context) {

        if (salesMilkCollectionRecord == null) {
            mContext = context;
            salesMilkCollectionRecord = new SalesCollectionRecordSet();
        }

        return salesMilkCollectionRecord;
    }


    @Override
    String getRecordType() {
        return null;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = salesMilkCollectionRecord.getQueryForUnsentDatesAndShifts(
                RecordSet.SALES_TYPE);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    ArrayList getUnsentRecords(DateShiftEntry dateShiftEntry) {

        String query = salesMilkCollectionRecord.getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.SALES_TYPE);
        ArrayList<SalesPostEntity> allSalesPostRecords = getCollectionList(query);
        return allSalesPostRecords;
    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.SALES_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }


    public ArrayList<SalesPostEntity> getCollectionList(String query) {

        ArrayList<SalesPostEntity> allSalesPostEntity = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {
            do {
                SalesPostEntity spe = new SalesPostEntity();

                spe.columnId = cursor.getLong(
                        cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_COLUMNID));
                spe.sequenceNumber = cursor.getLong(cursor.getColumnIndex(SalesCollectionTable.SEQUENCE_NUMBER));

                spe.salesTime =
                        ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                                cursor.getLong(
                                        cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TIME_MILLI)));
                spe.columnId =
                        cursor.getLong(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_COLUMNID));
                spe.milkType =
                        cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_MILKTYPE));

                spe.salesId =
                        cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_ID));
                Id id = new Id();
                id.producer = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_ID));
                id.user = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_NAME));
                spe.id = id;
                SalesTransaction salesTransaction = new SalesTransaction();

                salesTransaction.salesTransactionSubType =
                        cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE));
                salesTransaction.salesTransactionType =
                        cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TXN_TYPE));
                spe.date = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.POST_DATE));
                spe.shift = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.POST_SHIFT));
                spe.status = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_STATUS));
                spe.mode = cursor.getString(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_MANUAL));


                QualityParams qualityParams = new QualityParams();
                qualityParams.qualityMode = cursor.getString(
                        cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_MILKOMANUAL));
                qualityParams.fat = cursor.getDouble(cursor.getColumnIndex(
                        SalesCollectionTable.KEY_SALES_FAT
                ));
                qualityParams.snf = cursor.getDouble(cursor.getColumnIndex(
                        SalesCollectionTable.KEY_SALES_SNF
                ));
                qualityParams.awm = cursor.getDouble(cursor.getColumnIndex(
                        SalesCollectionTable.KEY_SALES_AWM
                ));
                qualityParams.temperature = cursor.getDouble(
                        cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_TEMP)
                );


                QuantityParams quantityParams = new QuantityParams();
                quantityParams.quantityMode =
                        cursor.getString(
                                cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_WEIGHTMANUAL)
                        );
                quantityParams.weighingQuantity =
                        cursor.getDouble(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_QUANTITY));


                RateParams rateParams = new RateParams();
                rateParams.rate = cursor.getDouble(
                        cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_RATE)
                );
                rateParams.amount = cursor.getDouble(
                        cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_AMOUNT)
                );

                spe.qualityParams = consolidatedHelperMethods.fromQualityParams(qualityParams);
                spe.quantityParams = consolidatedHelperMethods.fromQuantityParams(quantityParams);
                spe.rateParams = consolidatedHelperMethods.fromRateParams(rateParams);

                spe.calculateMin(spe.salesTime.getTime());
                spe.calculateMax(spe.salesTime.getTime());
                spe.salesTransaction = salesTransaction;
                allSalesPostEntity.add(spe);
            } while (cursor.moveToNext());
        }

        return allSalesPostEntity;
    }
}

