package com.devapp.devmain.ConsolidationPost;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.devapp.devmain.postentities.CanInOut;
import com.devapp.devmain.postentities.RoutePostEntity;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.RouteCollectionTable;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/1/18.
 */

public class RouteCollectionRecordSet extends RecordSet {


    private static RouteCollectionRecordSet routeMilkCollectionRecord;
    private static Context mContext;
    ConsolidatedHelperMethods consolidatedHelperMethods;
    private DatabaseHandler databaseHandler;
    private SQLiteDatabase sqliteDatabase;

    private RouteCollectionRecordSet() {
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        sqliteDatabase = databaseHandler.getPrimaryDatabase();
        consolidatedHelperMethods = ConsolidatedHelperMethods.getInstance(mContext);

    }

    public static RouteCollectionRecordSet getInstance(Context context) {

        if (routeMilkCollectionRecord == null) {
            mContext = context;
            routeMilkCollectionRecord = new RouteCollectionRecordSet();
        }

        return routeMilkCollectionRecord;
    }


    @Override
    String getRecordType() {
        return null;
    }

    @Override
    ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList() {

        String query = routeMilkCollectionRecord.getQueryForUnsentDatesAndShifts(
                RecordSet.ROUTE_TYPE);
        ArrayList<DateShiftEntry> allDatEntries = databaseHandler.getDayAndEntryList(query);

        return allDatEntries;

    }

    @Override
    ArrayList getUnsentRecords(DateShiftEntry dateShiftEntry) {

        String query = routeMilkCollectionRecord.getQueryForUnsentRecords(dateShiftEntry,
                RecordSet.ROUTE_TYPE);
        ArrayList<RoutePostEntity> allRoutePostRecords = getCollectionList(query);
        return allRoutePostRecords;
    }

    @Override
    int getUnsentCount() {
        String query = getQueryForUnsentCount(
                RecordSet.ROUTE_TYPE);
        int count = databaseHandler.getUnsentCount(query);

        return count;
    }


    public ArrayList<RoutePostEntity> getCollectionList(String query) {

        ArrayList<RoutePostEntity> allRouteRecords = new ArrayList<>();

        Cursor cursor = sqliteDatabase.rawQuery(query, null);


        if (cursor != null && cursor.moveToFirst()) {

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    RoutePostEntity rpe = new RoutePostEntity();

                    rpe.columnId = cursor.getLong(
                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_COLUMNID));
                    rpe.seqNum =
                            cursor.getLong(cursor.getColumnIndex(RouteCollectionTable.SEQUENCE_NUMBER));

                    CanInOut canInOut = new CanInOut();

                    canInOut.in =
                            cursor.getInt(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_CANS_IN));
                    canInOut.out =
                            cursor.getInt(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_CANS_OUT));
//            rpe.collectionType =
//                    cursor.getLong(cursor.getColumnIndex(RouteCollectionTable.KEY_SALES_TY));
                    rpe.columnId =
                            cursor.getLong(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_COLUMN_ID));

//                rpe.date =
//                        cursor.getLong(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_DATE));
                    //     rpe.shift = cursor.getInt(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_SHIFT));

//                rpe.deviceId =
//                        cursor.getString(cursor.getColumnIndex(RouteCollectionTable.KEY_));
                    rpe.material =
                            cursor.getString(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_MATERIAL));
                    rpe.recoveryAmount =
                            cursor.getDouble(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_RECOVERY_AMOUNT));
                    rpe.remarks =
                            cursor.getString(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_REMARKS));
                    rpe.routeId =
                            cursor.getString(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_ID));
                    rpe.securityTime = ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                            cursor.getLong(cursor.getColumnIndex(
                                    RouteCollectionTable.KEY_TRUCK_SECURITY_TIME)));
                    rpe.sentStatus = cursor.getInt(cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_SENT_STATUS));


                    rpe.date = cursor.getString(
                            cursor.getColumnIndex(RouteCollectionTable.POST_DATE));
                    rpe.shift = cursor.getString(
                            cursor.getColumnIndex(RouteCollectionTable.POST_SHIFT));
                    rpe.sentStatus = cursor.getInt(
                            cursor.getColumnIndex(RouteCollectionTable.KEY_TRUCK_SENT_STATUS));
                    rpe.smsSentStatus = cursor.getInt(
                            cursor.getColumnIndex(RouteCollectionTable.SMS_SENT_STATUS));

                    rpe.calculateMin(rpe.securityTime.getTime());
                    rpe.calculateMax(rpe.securityTime.getTime());
                    allRouteRecords.add(rpe);


                } while (cursor.moveToNext());
            }
        }

        RecordSet.closeCursor(cursor);


        return allRouteRecords;
    }
}



