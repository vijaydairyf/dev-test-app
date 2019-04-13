package com.devapp.devmain.ConsolidationPost;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteException;

import com.devapp.devmain.additionalRecords.Database.AddtionalDatabase;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.FarmerSplitCollectionEntity;
import com.devapp.devmain.postentities.FarmerSplitSubRecords;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.DispatchCollectionTable;
import com.devapp.devmain.tableEntities.EditRecordCollectionTable;
import com.devapp.devmain.tableEntities.ExtraParams;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.tableEntities.RouteCollectionTable;
import com.devapp.devmain.tableEntities.SalesCollectionTable;
import com.devapp.devmain.tableEntities.SplitRecordTable;
import com.devapp.devmain.tableEntities.TankerCollectionTable;
import com.devapp.devmain.user.Util;

import java.util.ArrayList;

/**
 * Created by u_pendra on 17/1/18.
 */

public abstract class RecordSet {


    public static final int FARMER_TYPE = 1;
    public static final int MCC_TYPE = 2;
    public static final int SAMPLE_TYPE = 3;
    public static final int TANKER_TYPE = 4;
    public static final int ROUTE_TYPE = 5;
    public static final int EDITED_FARMER_TYPE = 6;
    public static final int EDITED_MCC_TYPE = 7;
    public static final int ADDITIONAL_TYPE = 8;
    public static final int FARMER_SPLIT = 9;
    public static final int SALES_TYPE = 10;
    public static final int INCOMPLETE_FARMER = 11;
    public static final int INCOMPLETE_MCC = 12;
    public static final int DISPATCH_TYPE = 13;

    abstract String getRecordType();

    abstract ArrayList<DateShiftEntry> getUnsentDatesAndShiftsList();

    abstract int getUnsentCount();

    abstract ArrayList<? extends SynchronizableElement> getUnsentRecords(DateShiftEntry dateShiftEntry);


    public String getQueryForUnsentDatesAndShifts(int type) {
        String query = null;
        if (type == FARMER_TYPE) {
            query =
                    "Select distinct " + FarmerCollectionTable.POST_DATE
                            + "," + FarmerCollectionTable.POST_SHIFT + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                            + " order by " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                            + " ASC , " + FarmerCollectionTable.POST_SHIFT + " DESC";
        } else if (type == MCC_TYPE) {
            query =
                    "Select distinct " + FarmerCollectionTable.POST_DATE
                            + "," + FarmerCollectionTable.POST_SHIFT + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                            + " order by " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                            + " ASC , " + FarmerCollectionTable.POST_SHIFT + " DESC";
        } else if (type == SAMPLE_TYPE) {
            query =
                    "Select distinct " + FarmerCollectionTable.POST_DATE
                            + "," + FarmerCollectionTable.POST_SHIFT + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_SAMPLE + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                            + " order by " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                            + " ASC , " + FarmerCollectionTable.POST_SHIFT + " DESC";
        } else if (type == TANKER_TYPE) {
            query = " Select distinct " + TankerCollectionTable.POST_DATE
                    + "," + TankerCollectionTable.POST_SHIFT + " from "
                    + TankerCollectionTable.TABLE_TANKER
                    + " Where " + TankerCollectionTable.KEY_SENT + " = " + 0 +
                    " order by " + TankerCollectionTable.KEY_COLLECTION_TIME
                    + " ASC , " + TankerCollectionTable.POST_SHIFT + " DESC";
        } else if (type == DISPATCH_TYPE) {
            query = " Select distinct " + DispatchCollectionTable.KEY_POST_DATE
                    + "," + DispatchCollectionTable.KEY_POST_SHIFT + " from "
                    + DispatchCollectionTable.TABLE_DISPATCH_REPORT
                    + " Where " + DispatchCollectionTable.KEY_SEND_STATUS + " = " + 0 +
                    " order by " + DispatchCollectionTable.KEY_TIME_MILLIS
                    + " ASC , " + DispatchCollectionTable.KEY_POST_SHIFT + " DESC";
        } else if (type == ROUTE_TYPE) {
            query = " Select distinct " + RouteCollectionTable.POST_DATE
                    + "," + RouteCollectionTable.POST_SHIFT + " from "
                    + RouteCollectionTable.TABLE_TRUCK_DETAILS
                    + " Where " + RouteCollectionTable.KEY_TRUCK_SENT_STATUS + " = " + 0
                    + " order by " + RouteCollectionTable.KEY_TRUCK_SECURITY_TIME
                    + " ASC , " + RouteCollectionTable.POST_SHIFT + " DESC";

        } else if (type == EDITED_FARMER_TYPE) {
            query =
                    " Select distinct " + EditRecordCollectionTable.POST_DATE
                            + "," + EditRecordCollectionTable.POST_SHIFT + " from "
                            + EditRecordCollectionTable.TABLE_EXTENDED_REPORT
                            + " Where " + DatabaseHandler.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                            + " order by " + EditRecordCollectionTable.KEY_REPORT_TIME + " , "
                            + EditRecordCollectionTable.KEY_REPORT_TIME_MILLI
                            + " ASC , " + EditRecordCollectionTable.POST_SHIFT + " DESC";
        } else if (type == EDITED_MCC_TYPE) {
            query =
                    " Select distinct " + EditRecordCollectionTable.POST_DATE
                            + "," + EditRecordCollectionTable.POST_SHIFT + " from "
                            + EditRecordCollectionTable.TABLE_EXTENDED_REPORT
                            + " Where " + DatabaseHandler.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " order by " + EditRecordCollectionTable.KEY_REPORT_TIME + " , "
                            + EditRecordCollectionTable.KEY_REPORT_TIME_MILLI
                            + " ASC , " + EditRecordCollectionTable.POST_SHIFT + " DESC";
        } else if (type == FARMER_SPLIT) {
            query =
                    " Select distinct " + SplitRecordTable.POST_DATE
                            + "," + SplitRecordTable.POST_SHIFT + " from "
                            + SplitRecordTable.TABLE
                            + " Where " + SplitRecordTable.SENT + " = " + 0
                            + " order by " + SplitRecordTable.COLLECTION_TIME
                            + " ASC , " + SplitRecordTable.POST_SHIFT + " DESC";


        } else if (type == SALES_TYPE) {
            query =
                    " Select distinct " + SalesCollectionTable.POST_DATE
                            + "," + SalesCollectionTable.POST_SHIFT + " from "
                            + SalesCollectionTable.TABLE_SALES_REPORT
                            + " Where " + SalesCollectionTable.KEY_SALES_SEND_STATUS + " = " + 0
                            + " order by " + SalesCollectionTable.KEY_SALES_TIME_MILLI
                            + " ASC , " + SalesCollectionTable.POST_SHIFT + " DESC";

        } else if (type == INCOMPLETE_FARMER) {
            query =
                    "Select distinct " + FarmerCollectionTable.POST_DATE
                            + "," + FarmerCollectionTable.POST_SHIFT + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " ='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " order by " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                            + " ASC , " + FarmerCollectionTable.POST_SHIFT + " DESC";
        } else if (type == INCOMPLETE_MCC) {
            query =
                    "Select distinct " + FarmerCollectionTable.POST_DATE
                            + "," + FarmerCollectionTable.POST_SHIFT + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " ='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " order by " + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                            + " ASC , " + FarmerCollectionTable.POST_SHIFT + " DESC";
        } else if (type == ADDITIONAL_TYPE) {
            query =
                    "Select distinct " + ExtraParams.POST_DATE
                            + "," + ExtraParams.POST_SHIFT + " from " + ExtraParams.TABLE_EXTRA_PARAMS
                            + " Where " + ExtraParams.SENT_STATUS + " = " + 0
                            + " order by " + ExtraParams.POST_DATE
                            + " ASC , " + ExtraParams.POST_SHIFT + " DESC";
        }


        return query;

    }

    public String getQueryForUnsentRecords(DateShiftEntry dateShiftEntry, int type) {
        String query = null;
        if (type == FARMER_TYPE) {
            query =
                    "Select * from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " !='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                            + " AND " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                            + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == TANKER_TYPE) {
            query = "Select * from " + TankerCollectionTable.TABLE_TANKER
                    + " Where " + TankerCollectionTable.KEY_SENT + " = " + 0 +
                    " And " + TankerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'" +
                    " And " + TankerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'" +
                    " order by " + TankerCollectionTable.KEY_TANKER_NUMBER + " , "
                    + TankerCollectionTable.KEY_COLLECTION_TIME;
        } else if (type == ROUTE_TYPE) {
            query = " Select * from " + RouteCollectionTable.TABLE_TRUCK_DETAILS
                    + " Where " + RouteCollectionTable.KEY_TRUCK_SENT_STATUS + " = " + 0
                    + " And " + RouteCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + RouteCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'"
                    + " order by " + RouteCollectionTable.KEY_TRUCK_SECURITY_TIME + " , "
                    + RouteCollectionTable.KEY_TRUCK_SECURITY_TIME;

        } else if (type == FARMER_SPLIT) {
            query = " Select * from " + SplitRecordTable.TABLE
                    + " Where " + SplitRecordTable.SENT + " = " + 0 +
                    " And " + SplitRecordTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + SplitRecordTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'" +
                    " order by " + SplitRecordTable.AGENT_ID + " , "
                    + SplitRecordTable.COLLECTION_TIME;
        } else if (type == EDITED_FARMER_TYPE) {
            query =
                    "Select * from " + DatabaseHandler.TABLE_EXTENDED_REPORT
                            + " Where " + DatabaseHandler.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_COLLECTION + "'" +
                            " And " + EditRecordCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'" +
                            " And " + EditRecordCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";

        } else if (type == EDITED_MCC_TYPE) {
            query =
                    "Select * from " + DatabaseHandler.TABLE_EXTENDED_REPORT
                            + " Where " + DatabaseHandler.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_CHILLING + "'" +
                            " And " + EditRecordCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'" +
                            " And " + EditRecordCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";

        } else if (type == ADDITIONAL_TYPE) {
            query = "SELECT * FROM " +
                    ExtraParams.TABLE_EXTRA_PARAMS + " WHERE "
                    + ExtraParams.SENT_STATUS + " = " +
                    AddtionalDatabase.SERVER_UNSENT_STATUS
                    + " And " + ExtraParams.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + ExtraParams.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == MCC_TYPE) {
            query =
                    "Select * from " + DatabaseHandler.TABLE_REPORTS
                            + " Where " + DatabaseHandler.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " !='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                            + " And " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                            + " And " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";

        } else if (type == SAMPLE_TYPE) {
            query = "Select * from " + FarmerCollectionTable.TABLE_REPORTS
                    + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                    + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                    " ='" + CollectionConstants.REPORT_TYPE_SAMPLE + "'"
                    + " And " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                    + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                    " !='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                    + " And " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == SALES_TYPE) {
            query = "Select * from " + SalesCollectionTable.TABLE_SALES_REPORT
                    + " Where " + SalesCollectionTable.KEY_SALES_SEND_STATUS + " = " + 0
                    + " And " + SalesCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + SalesCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == DISPATCH_TYPE) {
            query = "Select * from " + DispatchCollectionTable.TABLE_DISPATCH_REPORT
                    + " Where " + DispatchCollectionTable.KEY_SEND_STATUS + " = " + 0
                    + " And " + DispatchCollectionTable.KEY_POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + DispatchCollectionTable.KEY_POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == INCOMPLETE_FARMER) {
            query =
                    "Select * from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " ='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " AND " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                            + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == INCOMPLETE_MCC) {
            query =
                    "Select * from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " ='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " AND " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                            + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        }
        return query;
    }


    public String getQueryForUnsentCount(int type) {
        String query = null;
        if (type == FARMER_TYPE) {
            query =
                    "Select count(*) " + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED;
        } else if (type == MCC_TYPE) {
            query =
                    "Select count(*) " + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED;
        } else if (type == SAMPLE_TYPE) {
            query =
                    "Select count(*) " + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_SAMPLE + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED;
        } else if (type == TANKER_TYPE) {
            query = "Select count(*) " + " from "
                    + TankerCollectionTable.TABLE_TANKER
                    + " Where " + TankerCollectionTable.KEY_SENT + " = " + 0;
        } else if (type == ROUTE_TYPE) {
            query = "Select count(*) " + " from "
                    + RouteCollectionTable.TABLE_TRUCK_DETAILS
                    + " Where " + RouteCollectionTable.KEY_TRUCK_SENT_STATUS + " = " + 0;

        } else if (type == EDITED_FARMER_TYPE) {
            query = "Select count(*) " + " from "
                    + EditRecordCollectionTable.TABLE_EXTENDED_REPORT
                    + " Where " + DatabaseHandler.KEY_REPORT_SENT_STATUS + " = " + 0
                    + " And " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                    + CollectionConstants.REPORT_TYPE_COLLECTION + "'";
        } else if (type == EDITED_MCC_TYPE) {
            query =
                    "Select count(*) " + " from "
                            + EditRecordCollectionTable.TABLE_EXTENDED_REPORT
                            + " Where " + DatabaseHandler.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_CHILLING + "'";
        } else if (type == FARMER_SPLIT) {
            query =
                    "Select count(*) " + " from "
                            + SplitRecordTable.TABLE
                            + " Where " + SplitRecordTable.SENT + " = " + 0;


        } else if (type == SALES_TYPE) {
            query =
                    "Select count(*) " + " from "
                            + SalesCollectionTable.TABLE_SALES_REPORT
                            + " Where " + SalesCollectionTable.KEY_SALES_SEND_STATUS + " = " + 0;

        } else if (type == DISPATCH_TYPE) {
            query =
                    "Select count(*) " + " from "
                            + DispatchCollectionTable.TABLE_DISPATCH_REPORT
                            + " Where " + DispatchCollectionTable.KEY_SEND_STATUS + " = " + 0;

        } else if (type == INCOMPLETE_FARMER) {
            query = "Select count(*) " + " from " + FarmerCollectionTable.TABLE_REPORTS
                    + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                    + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                    " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                    + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                    " ='" + Util.RECORD_STATUS_INCOMPLETE + "'";
        } else if (type == INCOMPLETE_MCC) {
            query =
                    "Select count(*) " + " from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " = " + 0
                            + " And " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " ='" + Util.RECORD_STATUS_INCOMPLETE + "'";
        } else if (type == ADDITIONAL_TYPE) {
            query =
                    "Select count(*) " + " from " + ExtraParams.TABLE_EXTRA_PARAMS
                            + " Where " + ExtraParams.SENT_STATUS + " = " + 0;
        }


        return query;

    }

    public String getQueryForRecords(DateShiftEntry dateShiftEntry, int type) {
        String query = null;
        if (type == FARMER_TYPE) {
            query =
                    "Select * from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " !='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                            + " AND " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                            + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == TANKER_TYPE) {
            query = "Select * from " + TankerCollectionTable.TABLE_TANKER
                    + " Where " + TankerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'" +
                    " And " + TankerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'" +
                    " order by " + TankerCollectionTable.KEY_TANKER_NUMBER + " , "
                    + TankerCollectionTable.KEY_COLLECTION_TIME;
        } else if (type == ROUTE_TYPE) {
            query = " Select * from " + RouteCollectionTable.TABLE_TRUCK_DETAILS
                    + " Where " + RouteCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + RouteCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'"
                    + " order by " + RouteCollectionTable.KEY_TRUCK_SECURITY_TIME + " , "
                    + RouteCollectionTable.KEY_TRUCK_SECURITY_TIME;

        } else if (type == FARMER_SPLIT) {
            query = " Select * from " + SplitRecordTable.TABLE
                    + " Where " + SplitRecordTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + SplitRecordTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'" +
                    " order by " + SplitRecordTable.AGENT_ID + " , "
                    + SplitRecordTable.COLLECTION_TIME;
        } else if (type == EDITED_FARMER_TYPE) {
            query =
                    "Select * from " + DatabaseHandler.TABLE_EXTENDED_REPORT
                            + " Where " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_COLLECTION + "'" +
                            " And " + EditRecordCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'" +
                            " And " + EditRecordCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";

        } else if (type == EDITED_MCC_TYPE) {
            query =
                    "Select * from " + DatabaseHandler.TABLE_EXTENDED_REPORT
                            + " Where " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_CHILLING + "'" +
                            " And " + EditRecordCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'" +
                            " And " + EditRecordCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";

        } else if (type == ADDITIONAL_TYPE) {
            query = "SELECT * FROM " +
                    ExtraParams.TABLE_EXTRA_PARAMS + " WHERE "
                    + ExtraParams.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + ExtraParams.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == MCC_TYPE) {
            query =
                    "Select * from " + DatabaseHandler.TABLE_REPORTS
                            + " Where " + DatabaseHandler.KEY_REPORT_TYPE + " ='"
                            + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " !='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " AND " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                            + " And " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                            + " And " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";

        } else if (type == SAMPLE_TYPE) {
            query = "Select * from " + FarmerCollectionTable.TABLE_REPORTS
                    + " Where " + FarmerCollectionTable.KEY_REPORT_TYPE +
                    " ='" + CollectionConstants.REPORT_TYPE_SAMPLE + "'"
                    + " And " + FarmerCollectionTable.KEY_REPORT_COMMITED + " = " + Util.REPORT_COMMITED
                    + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                    " !='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                    + " And " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == SALES_TYPE) {
            query = "Select * from " + SalesCollectionTable.TABLE_SALES_REPORT
                    + " Where " + SalesCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + SalesCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == DISPATCH_TYPE) {
            query = "Select * from " + DispatchCollectionTable.TABLE_DISPATCH_REPORT
                    + " Where " + DispatchCollectionTable.KEY_POST_DATE + " ='" + dateShiftEntry.date + "'"
                    + " And " + DispatchCollectionTable.KEY_POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == INCOMPLETE_FARMER) {
            query =
                    "Select * from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " ='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " AND " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                            + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        } else if (type == INCOMPLETE_MCC) {
            query =
                    "Select * from " + FarmerCollectionTable.TABLE_REPORTS
                            + " Where " + FarmerCollectionTable.KEY_REPORT_TYPE +
                            " ='" + CollectionConstants.REPORT_TYPE_CHILLING + "'"
                            + " And " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS +
                            " ='" + Util.RECORD_STATUS_INCOMPLETE + "'"
                            + " AND " + FarmerCollectionTable.POST_DATE + " ='" + dateShiftEntry.date + "'"
                            + " AND " + FarmerCollectionTable.POST_SHIFT + " ='" + dateShiftEntry.shift + "'";
        }
        return query;
    }

    void setUpdateStatus(ArrayList<? extends SynchronizableElement> allUnsentRecords,
                         int status) {

        for (SynchronizableElement se : allUnsentRecords) {
            if (se instanceof FarmerSplitCollectionEntity) {
                FarmerSplitCollectionEntity fsc = (FarmerSplitCollectionEntity) se;
                for (FarmerSplitSubRecords sub : fsc.farmerSplitEntries) {
                    fsc.columnId = sub.columnId;
                    try {
                        fsc.setSentStatus(status);
                    } catch (SQLiteConstraintException e1) {

                    } catch (SQLiteException e) {

                    }

                }
            } else {
                try {
                    se.setSentStatus(status);
                } catch (SQLiteConstraintException e1) {

                } catch (SQLiteException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    public static void closeCursor(Cursor cursor) {
        if (cursor != null) {
            cursor.close();
        }
    }


}
