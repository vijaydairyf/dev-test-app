package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.agentfarmersplit.QualityParams;
import com.devapp.devmain.agentfarmersplit.QuantityParams;
import com.devapp.devmain.postentities.TankerMetadata;
import com.devapp.devmain.postentities.TankerPostEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.TankerCollectionTable;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/1/18.
 */

public class TankerCollectionRecordSet extends RecordSet {


    private static TankerCollectionRecordSet tankerMilkCollectionRecord;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;

    private TankerCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static TankerCollectionRecordSet getInstance(Context context) {

        if (tankerMilkCollectionRecord == null) {
            mContext = context;
            tankerMilkCollectionRecord = new TankerCollectionRecordSet();
        }

        return tankerMilkCollectionRecord;
    }


    @Override
    String getRecordType() {
        return null;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = getQueryForUnsentDatesAndShifts(
                RecordSet.TANKER_TYPE);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    ArrayList getUnsentRecords(DateShiftEntry dateShiftEntry) {

        String query = tankerMilkCollectionRecord.getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.TANKER_TYPE);
        ArrayList<TankerPostEntity> allTankerPostRecords = getCollectionList(query);
        return allTankerPostRecords;
    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.TANKER_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }


    public ArrayList<TankerPostEntity> getCollectionList(String query) {

        ArrayList<TankerPostEntity> allTankerPostRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {
            do {
                TankerPostEntity tpe = new TankerPostEntity();

                tpe.columnId = cursor.getLong(
                        cursor.getColumnIndex(TankerCollectionTable.KEY_COLUMN_ID));
                tpe.sequenceNumber =
                        cursor.getLong(cursor.getColumnIndex(TankerCollectionTable.SEQUENCE_NUMBER));

                tpe.collectionTime =
                        ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                                cursor.getLong(cursor.getColumnIndex(
                                        TankerCollectionTable.KEY_COLLECTION_TIME)));
//            tpe.collectionType =
//                    cursor.getLong(cursor.getColumnIndex(TankerCollectionTable.KEY_SALES_TY));
                tpe.columnId =
                        cursor.getLong(cursor.getColumnIndex(TankerCollectionTable.KEY_COLUMN_ID));
//                tpe.milkType =
//                        cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_T));
//                tpe.mode =
//                        cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_));


                TankerMetadata tankerMetadata = new TankerMetadata();

                tankerMetadata.number =
                        cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_TANKER_NUMBER));
                tankerMetadata.routeNumber =
                        cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_ROUTE_NUMER));
                tankerMetadata.supervisorCode =
                        cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_SUPERVISOR_CODE));


                tpe.comments = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_COMMENTS));
//                tpe.compartmentNumbers = new ArrayList<>().add(
//                        cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_COMPARTMENT_NUMBERS));

                tpe.status = cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_STATUS));

                QualityParams qualityParams = new QualityParams();
//                tpe.qualityParams.qualityMode = cursor.getString(
//                        cursor.getColumnIndex(TankerCollectionTable.KEY_));
                qualityParams.fat = cursor.getDouble(cursor.getColumnIndex(
                        TankerCollectionTable.KEY_FAT
                ));
                qualityParams.snf = cursor.getDouble(cursor.getColumnIndex(
                        TankerCollectionTable.KEY_SNF
                ));
                qualityParams.alcohol = cursor.getDouble(cursor.getColumnIndex(
                        TankerCollectionTable.KEY_ALCHOLOL
                ));
                qualityParams.temperature = cursor.getDouble(
                        cursor.getColumnIndex(TankerCollectionTable.KEY_TEMP)
                );
                qualityParams.clr = cursor.getDouble(
                        cursor.getColumnIndex(TankerCollectionTable.KEY_CLR)
                );


                QuantityParams quantityParams = new QuantityParams();
//                tpe.quantityParams.quantityMode =
//                        cursor.getString(
//                                cursor.getColumnIndex(TankerCollectionTable.KEY_Q)
//                        );
                quantityParams.weighingQuantity =
                        cursor.getDouble(cursor.getColumnIndex(TankerCollectionTable.KEY_QUANTITY));
                quantityParams.measurementUnit =
                        cursor.getString(cursor.getColumnIndex(TankerCollectionTable.KEY_QUANTITY_UNIT));

                tpe.qualityParamsPost = consolidatedHelperMethods.fromQualityParams(qualityParams);
                tpe.quantityParamspost = consolidatedHelperMethods.fromQuantityParams(quantityParams);


                tpe.date = cursor.getString(
                        cursor.getColumnIndex(TankerCollectionTable.POST_DATE));
                tpe.shift = cursor.getString(
                        cursor.getColumnIndex(TankerCollectionTable.POST_SHIFT));
                tpe.sent = cursor.getInt(
                        cursor.getColumnIndex(TankerCollectionTable.SMS_SENT_STATUS));
                tpe.smsSentStatus = cursor.getInt(
                        cursor.getColumnIndex(TankerCollectionTable.SMS_SENT_STATUS));

                tpe.calculateMin(tpe.collectionTime.getTime());
                tpe.calculateMax(tpe.collectionTime.getTime());
                tpe.tankerMetadata = tankerMetadata;
                allTankerPostRecords.add(tpe);


            } while (cursor.moveToNext());
        }
        return allTankerPostRecords;
    }
}


