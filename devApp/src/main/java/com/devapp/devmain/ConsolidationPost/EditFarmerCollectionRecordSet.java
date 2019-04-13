package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.EditedFarmerPostEntity;
import com.devapp.devmain.postentities.FarmerCollectionId;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.EditRecordCollectionTable;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.ArrayList;

/**
 * Created by u_pendra on 20/1/18.
 */


public class EditFarmerCollectionRecordSet extends RecordSet {


    private static EditFarmerCollectionRecordSet editFarmerCollectionRecordSet;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;


    private EditFarmerCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static EditFarmerCollectionRecordSet getInstance(Context context) {

        if (editFarmerCollectionRecordSet == null) {
            mContext = context;
            editFarmerCollectionRecordSet = new EditFarmerCollectionRecordSet();
        }

        return editFarmerCollectionRecordSet;
    }


    @Override
    String getRecordType() {
        return CollectionConstants.EDITED_FARMER_COLLECTION;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = getQueryForUnsentDatesAndShifts(
                RecordSet.EDITED_FARMER_TYPE);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.EDITED_FARMER_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }

    @Override
    ArrayList<? extends SynchronizableElement> getUnsentRecords(DateShiftEntry dateShiftEntry) {
        String query = getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.EDITED_FARMER_TYPE);
        ArrayList<EditedFarmerPostEntity> allFarmerPostRecords = getCollectionList(query);
        return allFarmerPostRecords;
    }


    public ArrayList<EditedFarmerPostEntity> getCollectionList(String query) {

        ArrayList<EditedFarmerPostEntity> allPostFarmerEditRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {

            do {
                EditedFarmerPostEntity editedFarmerPostEntity = new EditedFarmerPostEntity();
                ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);


                editedFarmerPostEntity.columnId = cursor.getLong(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_COLUMNID));
                editedFarmerPostEntity.sequenceNumber =
                        cursor.getLong(cursor.getColumnIndex(EditRecordCollectionTable.SEQUENCE_NUMBER));

                editedFarmerPostEntity.mode = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MANUAL));
                editedFarmerPostEntity.milkType = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MILKTYPE));
                editedFarmerPostEntity.collectionTime = SmartCCUtil.getCollectionDateFromLongTime(
                        cursor.getLong(
                                cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_TIME_MILLI)));

                editedFarmerPostEntity.status = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_STATUS));
                editedFarmerPostEntity.numberOfCans = Integer.parseInt(
                        cursor.getString(
                                cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_NUMBER_OF_CANS))
                );
                editedFarmerPostEntity.collectionRoute = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_ROUTE));
                editedFarmerPostEntity.sampleNumber = cursor.getInt(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_SEQUENCE_NUMBER));

                editedFarmerPostEntity.milkType = cursor.getString(cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_MILKTYPE));


                editedFarmerPostEntity.qualityParams = reportEntity.getQualityParams();
                editedFarmerPostEntity.quantityParams = reportEntity.getQuantityParams();
                editedFarmerPostEntity.rateParams = reportEntity.getRateParams();


                FarmerCollectionId farmerCollectionId = new FarmerCollectionId();

                farmerCollectionId.aggregateFarmer = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_AGENT_ID));
                farmerCollectionId.user = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_USER));
                farmerCollectionId.producer = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_FARMERID));

                editedFarmerPostEntity.collectionId = farmerCollectionId;


                editedFarmerPostEntity.date = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.POST_DATE));
                editedFarmerPostEntity.shift = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.POST_SHIFT));
                editedFarmerPostEntity.sentStatus = cursor.getInt(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_SENT_STATUS));
                editedFarmerPostEntity.smsSentStatus = cursor.getInt(
                        cursor.getColumnIndex(EditRecordCollectionTable.SMS_SENT_STATUS));

//                editedFarmerPostEntity.status = cursor.getString(
//                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_RECORD_STATUS));
                editedFarmerPostEntity.recordType = cursor.getString(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_OLD_OR_NEW_FLAG));
                editedFarmerPostEntity.updatedTime = SmartCCUtil.getCollectionDateFromLongTime(
                        cursor.getLong(
                                cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_EDITED_TIME)));


                editedFarmerPostEntity.calculateMin(editedFarmerPostEntity.collectionTime.getTime());
                editedFarmerPostEntity.calculateMax(editedFarmerPostEntity.collectionTime.getTime());

                editedFarmerPostEntity.recordType = editedFarmerPostEntity.recordType.toUpperCase();

                editedFarmerPostEntity.parentSequenceNumber = cursor.getLong(
                        cursor.getColumnIndex(EditRecordCollectionTable.KEY_REPORT_FOREIGN_SEQUENCE_ID));

                allPostFarmerEditRecords.add(editedFarmerPostEntity);

            } while (cursor.moveToNext());
        }
        RecordSet.closeCursor(cursor);
        return allPostFarmerEditRecords;

    }
}

