package com.devapp.devmain.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.devapp.devmain.additionalRecords.AdditionalParamsEntity;
import com.devapp.devmain.agentfarmersplit.AgentSplitEntity;
import com.devapp.devmain.dao.AdditionalParamsDao;
import com.devapp.devmain.dao.AgentSplitDao;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.CollectionStatusDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.EditRecordDao;
import com.devapp.devmain.dao.EditRecordStatusDao;
import com.devapp.devmain.dao.SalesRecordDao;
import com.devapp.devmain.dao.SalesRecordStatusDao;
import com.devapp.devmain.dao.TankerCollectionDao;
import com.devapp.devmain.entity.CollectionRecordStatusEntity;
import com.devapp.devmain.entity.EditRecordStatusEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.entity.SalesRecordStatusEntity;
import com.devapp.devmain.milkline.entities.TankerCollectionEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.tableEntities.EditRecordCollectionTable;
import com.devapp.devmain.tableEntities.ExtraParams;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.tableEntities.RateChartNameTable;
import com.devapp.devmain.tableEntities.RateTable;
import com.devapp.devmain.tableEntities.RouteCollectionTable;
import com.devapp.devmain.tableEntities.SalesCollectionTable;
import com.devapp.devmain.tableEntities.SplitRecordTable;
import com.devapp.devmain.tableEntities.TankerCollectionTable;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.database.TruckCCDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.devapp.devmain.user.Util.padding;

/**
 * Created by u_pendra on 31/1/18.
 */

public class UpdateDatabaseHelper {


    DatabaseHandler databaseHandler;
    SQLiteDatabase sqLiteDatabase;

    CollectionStatusDao collectionStatusDao;
    private int seqNum;

    public UpdateDatabaseHelper(SQLiteDatabase sqLiteDatabase) {

        this.sqLiteDatabase = sqLiteDatabase;
        collectionStatusDao = (CollectionStatusDao) DaoFactory.getDao(CollectionConstants.COLLECTION_RECORD_STATUS, false);
        seqNum = collectionStatusDao.getLastTransactionSequenceNumber();

    }

    public static void setSequenceNumTrigger(String tableName, SQLiteDatabase sqLiteDb) {
        String trigger = "CREATE TRIGGER " + tableName + "UpdateSeqNum AFTER INSERT ON " + tableName +
                " BEGIN" +
                " UPDATE  " + DatabaseHandler.TABLE_COLL_REC_SEQ_NUM + " " +
                " SET " + DatabaseHandler.KEY_COLL_REC_SEQNUM
                + " = (" + DatabaseHandler.KEY_COLL_REC_SEQNUM + " % " + DatabaseHandler.MAX_SEQ_NUM + " )+ 1;" +
                " UPDATE " + tableName + " " +
                " SET " + FarmerCollectionTable.SEQUENCE_NUMBER + " = (" +
                " SELECT " + DatabaseHandler.KEY_COLL_REC_SEQNUM + " " +
                " FROM " + DatabaseHandler.TABLE_COLL_REC_SEQ_NUM + ") " +
                " WHERE _id = new._id; " +
                "END;";
        Log.d("myDebug", trigger);
        sqLiteDb.execSQL(trigger);
    }

    public static void triggerForPreUpdate(SQLiteDatabase sqLiteDb) {

        String trigger = "CREATE TRIGGER IF NOT " +
                "EXISTS ReportsTablePreUpdate BEFORE UPDATE ON ReportsTable " +
                "WHEN " + "NEW.reportSentStatus = " + 1 +
                " BEGIN " +
                "SELECT CASE " +
                " WHEN (NEW.lastModified < OLD.lastModified)" +
                " THEN RAISE (ABORT, " + "'Record was modified while posting'" + ")"
                + " END; " +
                "END;";
        sqLiteDb.execSQL(trigger);


    }

    public static void triggerForPostUpdate(SQLiteDatabase sqLiteDb) {

        String trigger =
                "CREATE TRIGGER IF NOT EXISTS " +
                        " ReportsTablePostUpdate AFTER UPDATE ON ReportsTable BEGIN UPDATE " +
                        " ReportsTable SET lastModified = " +
                        "CAST((julianday('now') - 2440587.5)*86400000 AS INTEGER) WHERE _id = NEW._id; END;";
        sqLiteDb.execSQL(trigger);
    }

    public static void triggerOnReportTableInsert(SQLiteDatabase sqLiteDb) {
        String trigger = "CREATE TRIGGER IF NOT EXISTS ReportsTableOnInsert " +
                "AFTER INSERT ON ReportsTable " +
                "BEGIN UPDATE ReportsTable " +
                "SET lastModified =  CAST((julianday('now') - 2440587.5)*86400000 AS INTEGER) " +
                "WHERE _id = NEW._id;END;";
        sqLiteDb.execSQL(trigger);
    }

    public static void addCollectionDateIndexOnReportsTable(SQLiteDatabase sqLiteDb) {
        String index = "CREATE INDEX collectionDateIndex ON " + DatabaseHandler.TABLE_REPORTS + " (" + DatabaseHandler.KEY_REPORT_LDATE + ")";
        sqLiteDb.execSQL(index);
    }

    public static void addCollectionTimeIndexOnReportsTable(SQLiteDatabase sqLiteDb) {
        String index = "CREATE INDEX collectionTimeIndex ON " + DatabaseHandler.TABLE_REPORTS + " (" + DatabaseHandler.KEY_REPORT_TIME_MILLI + ")";
        sqLiteDb.execSQL(index);
    }

    public static void addDateShiftSentStatusIndexOnReportsTable(SQLiteDatabase sqLiteDb) {
        String index = "CREATE INDEX reportsTableIdx ON ReportsTable (postDate, postShift, reportType, reportSentStatus , recordCommited)";
        sqLiteDb.execSQL(index);
    }

    public void version40DaoUpgrade() {


        ArrayList<String> queries = new ArrayList<>();

        queries.addAll(alterRouteDetailsTable());
        queries.addAll(alterAgentSplitTable());
        queries.addAll(alterAdditionalParamsTable());
        queries.addAll(alterTankerCollectionTable());
        queries.addAll(alterCollectionRecordsTable());
        queries.addAll(alterEditRecordTable());
        queries.addAll(alterSalesTable());

        for (String query : queries) {
            try {
                sqLiteDatabase.execSQL(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        migrateAdditionalParamsRecords();
        migrateAgentSplitData();
//        migrateRouteRecords();

        //Below three migration not required for 14.2 series apk, as
        //we are handling in new upgrade
//        migrateEditRecords();
//        migrateCollectionRecordsTable();
//        migrateSalesData();
        migrateTankerCollectionTable();
        migrateRatechartNameTable();

        //Below three migration not required for 14.2 series apk, as
        //we are handling in new upgrade
        // dropTables();

        collectionStatusDao.setLastTransactionSequenceNumber(seqNum);

        setSequenceNumTrigger(FarmerCollectionTable.TABLE_REPORTS, sqLiteDatabase);
        setSequenceNumTrigger(SalesCollectionTable.TABLE_SALES_REPORT, sqLiteDatabase);
        setSequenceNumTrigger(ExtraParams.TABLE_EXTRA_PARAMS, sqLiteDatabase);
        setSequenceNumTrigger(SplitRecordTable.TABLE, sqLiteDatabase);
        setSequenceNumTrigger(TankerCollectionTable.TABLE_TANKER, sqLiteDatabase);
        setSequenceNumTrigger(EditRecordCollectionTable.TABLE_EXTENDED_REPORT, sqLiteDatabase);
    }

    public void updateTo43(int oldVersion) {


        migrateReportTableFor43();
        migrateSalesReportTableFor43();
        migrateExtendedReportTableFor43();

        //for 11.6.9
        if (oldVersion < 40) {
            migrateEditRecords();
            migrateCollectionRecordsTable();
            migrateSalesData();
        }

        setSequenceNumTrigger(FarmerCollectionTable.TABLE_REPORTS, sqLiteDatabase);
        setSequenceNumTrigger(SalesCollectionTable.TABLE_SALES_REPORT, sqLiteDatabase);
        setSequenceNumTrigger(EditRecordCollectionTable.TABLE_EXTENDED_REPORT, sqLiteDatabase);


        updateConfigurationTable("allowFamerCreate", String.valueOf(false));
        updateConfigurationTable("allowFamerEdit", String.valueOf(false));
        updateConfigurationTable("allowDisplayNameInReport", String.valueOf(false));

        //  sqLiteDatabase.execSQL(ConfigSyncStatusTable.CREATE_QUERY);
    }

    public void dropTables() {
        ArrayList<String> queries = new ArrayList<>();

        String[] tableArray = {DatabaseHandler.TABLE_COL_EX_REC_STATUS,
                DatabaseHandler.TABLE_COL_REC_STATUS,
                DatabaseHandler.TABLE_COLL_EX_REC_SEQ_NUM,
                DatabaseHandler.TABLE_COLL_SALES_SEQ_NUMBER,
                DatabaseHandler.TABLE_COLL_SALES_STATUS,
                DatabaseHandler.TABLE_SHIFT_EX_DATA,
                DatabaseHandler.TABLE_SHIFT_SALES_DATA,
                DatabaseHandler.TABLE_SHIFT_CHILL_DATA,
                DatabaseHandler.TABLE_PRINTER,
                DatabaseHandler.TABLE_WEIGHING_SCALE,
                DatabaseHandler.TABLE_RDU,
                DatabaseHandler.TABLE_MILKOTESTER,
                DatabaseHandler.TABLE_CHILLING_REPORT,
                DatabaseHandler.TABLE_SETTING};


        for (String table : tableArray) {
            queries.add("DROP TABLE IF EXISTS " + table);

        }

        for (String query : queries) {
            sqLiteDatabase.execSQL(query);
        }
    }

    public ArrayList<String> alterRouteDetailsTable() {
        ArrayList<String> allQueries = new ArrayList<>();

        allQueries.add("Alter table " + RouteCollectionTable.TABLE_TRUCK_DETAILS + " ADD COLUMN " +
                RouteCollectionTable.SMS_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        allQueries.add("Alter table " + RouteCollectionTable.TABLE_TRUCK_DETAILS + " ADD COLUMN " +
                RouteCollectionTable.POST_SHIFT + " TEXT " + " DEFAULT " + null);
        allQueries.add("Alter table " + RouteCollectionTable.TABLE_TRUCK_DETAILS + " ADD COLUMN " +
                RouteCollectionTable.POST_DATE + " TEXT " + " DEFAULT " + null);
        allQueries.add("Alter table " + RouteCollectionTable.TABLE_TRUCK_DETAILS + " ADD COLUMN " +
                RouteCollectionTable.SEQUENCE_NUMBER + " INTEGER " + " DEFAULT " + 0);

        return allQueries;
    }

    public ArrayList<String> alterAgentSplitTable() {
        ArrayList<String> queries = new ArrayList<>();

        queries.add("Alter table " + SplitRecordTable.TABLE + " ADD COLUMN " +
                SplitRecordTable.SMS_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        queries.add("Alter table " + SplitRecordTable.TABLE + " ADD COLUMN " +
                SplitRecordTable.POST_SHIFT + " TEXT " + " DEFAULT " + null);
        queries.add("Alter table " + SplitRecordTable.TABLE + " ADD COLUMN " +
                SplitRecordTable.POST_DATE + " TEXT " + " DEFAULT " + null);
        queries.add("Alter table " + SplitRecordTable.TABLE + " ADD COLUMN " +
                SplitRecordTable.SEQUENCE_NUMBER + " INTEGER " + " DEFAULT " + 0);

        return queries;
    }

    public ArrayList<String> alterAdditionalParamsTable() {
        ArrayList<String> queries = new ArrayList<>();

        queries.add("Alter table " + ExtraParams.TABLE_EXTRA_PARAMS + " ADD COLUMN " +
                ExtraParams.COLLECTION_TIME + " INTEGER " + " DEFAULT " + 0);
        queries.add("Alter table " + ExtraParams.TABLE_EXTRA_PARAMS + " ADD COLUMN " +
                ExtraParams.SMS_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        queries.add("Alter table " + ExtraParams.TABLE_EXTRA_PARAMS + " ADD COLUMN " +
                ExtraParams.POST_SHIFT + " TEXT " + " DEFAULT " + null);
        queries.add("Alter table " + ExtraParams.TABLE_EXTRA_PARAMS + " ADD COLUMN " +
                ExtraParams.POST_DATE + " TEXT " + " DEFAULT " + null);
        queries.add("Alter table " + ExtraParams.TABLE_EXTRA_PARAMS + " ADD COLUMN " +
                ExtraParams.SEQUENCE_NUMBER + " INTEGER " + " DEFAULT " + 0);


        return queries;
    }

    public ArrayList<String> alterTankerCollectionTable() {

        ArrayList<String> queries = new ArrayList<>();
        queries.add("Alter table " + TankerCollectionTable.TABLE_TANKER + " ADD COLUMN " +
                TankerCollectionTable.SMS_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        queries.add("Alter table " + TankerCollectionTable.TABLE_TANKER + " ADD COLUMN " +
                TankerCollectionTable.POST_SHIFT + " TEXT " + " DEFAULT " + null);
        queries.add("Alter table " + TankerCollectionTable.TABLE_TANKER + " ADD COLUMN " +
                TankerCollectionTable.POST_DATE + " TEXT " + " DEFAULT " + null);
        queries.add("Alter table " + TankerCollectionTable.TABLE_TANKER + " ADD COLUMN " +
                TankerCollectionTable.SEQUENCE_NUMBER + " INTEGER " + " DEFAULT " + 0);


        return queries;


    }

    public ArrayList<String> alterCollectionRecordsTable() {

        ArrayList<String> allQueries = new ArrayList<>();

        allQueries.add("Alter table " + FarmerCollectionTable.TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.KEY_REPORT_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        allQueries.add("Alter table " + FarmerCollectionTable.TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.SMS_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        allQueries.add("Alter table " + FarmerCollectionTable.TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.POST_SHIFT + " TEXT " + " DEFAULT " + null);
        allQueries.add("Alter table " + FarmerCollectionTable.TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.POST_DATE + " TEXT " + " DEFAULT " + null);
        allQueries.add("Alter table " + FarmerCollectionTable.TABLE_REPORTS + " ADD COLUMN " +
                FarmerCollectionTable.SEQUENCE_NUMBER + " INTEGER " + " DEFAULT " + 0);


        return allQueries;


    }

    public ArrayList<String> alterSalesTable() {
        ArrayList<String> queries = new ArrayList<>();

        queries.add("Alter table " + SalesCollectionTable.TABLE_SALES_REPORT + " ADD COLUMN " +
                SalesCollectionTable.KEY_SALES_SEND_STATUS + " INTEGER " + " DEFAULT " + 0);
        queries.add("Alter table " + SalesCollectionTable.TABLE_SALES_REPORT + " ADD COLUMN " +
                SalesCollectionTable.SMS_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        queries.add("Alter table " + SalesCollectionTable.TABLE_SALES_REPORT + " ADD COLUMN " +
                SalesCollectionTable.POST_SHIFT + " TEXT " + " DEFAULT " + null);
        queries.add("Alter table " + SalesCollectionTable.TABLE_SALES_REPORT + " ADD COLUMN " +
                SalesCollectionTable.POST_DATE + " TEXT " + " DEFAULT " + null);

        queries.add("Alter table " + SalesCollectionTable.TABLE_SALES_REPORT + " ADD COLUMN " +
                SalesCollectionTable.SEQUENCE_NUMBER + " INTEGER " + " DEFAULT " + 0);


        return queries;
    }

//Need to get all the records from statustable and update

    public ArrayList<String> alterEditRecordTable() {
        ArrayList<String> queries = new ArrayList<>();

        queries.add("Alter table " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                EditRecordCollectionTable.KEY_REPORT_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        queries.add("Alter table " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                EditRecordCollectionTable.SMS_SENT_STATUS + " INTEGER " + " DEFAULT " + 0);
        queries.add("Alter table " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                EditRecordCollectionTable.POST_SHIFT + " TEXT " + " DEFAULT " + null);
        queries.add("Alter table " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                EditRecordCollectionTable.POST_DATE + " TEXT " + " DEFAULT " + null);

        queries.add("Alter table " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT + " ADD COLUMN " +
                EditRecordCollectionTable.SEQUENCE_NUMBER + " INTEGER " + " DEFAULT " + 0);


        return queries;
    }

    public String changeDateFormat(String dateString, String oldFormat, String newFormat) {
        String result = dateString;
        SimpleDateFormat oSDF = new SimpleDateFormat(oldFormat);
        SimpleDateFormat nSDF = new SimpleDateFormat(newFormat);
        try {
            Date date = oSDF.parse(dateString);
            result = nSDF.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String getDateFormatFromMillis(String dateFormat, long milliSeconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    public String getShift(Long longTime) {

        String shift = "MORNING";

        final Calendar c = Calendar.getInstance();
        c.setTimeInMillis(longTime);


        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        if ((Integer.parseInt(padding(hour) + padding(min)) >= Integer
                .parseInt("300")
                && (Integer.parseInt(padding(hour) + padding(min)) < Integer
                .parseInt("1500")))) {
            shift = "MORNING";
        } else {
            shift = "EVENING";
        }

        return shift;
    }

    public void migrateCollectionRecordsTable() {

        CollectionRecordDao collectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION, false);

        ArrayList<ReportEntity> reportEntities =
                (ArrayList<ReportEntity>) collectionRecordDao.findAll();

        for (ReportEntity reportEntity : reportEntities) {
            reportEntity.setPostShift(getShift(reportEntity.getMiliTime()));
            reportEntity.setPostDate(getDateFormatFromMillis("yyyy-MM-dd", reportEntity.getMiliTime()));


            CollectionRecordStatusEntity crse = collectionStatusDao.findByReportTableIndexAndType(reportEntity.getColumnId(),
                    CollectionConstants.REPORT_TYPE_COLLECTION);
            if (crse != null) {
                if (crse.getNwSendStatus() == CollectionConstants.SENT) {
                    reportEntity.setSentStatus(crse.getNwSendStatus());
                    reportEntity.setSequenceNum((int) crse.getSeqNum());
                    reportEntity.setSentSmsStatus(crse.getFarmerSMSStatus());
                } else {
                    reportEntity.setSentStatus(CollectionConstants.UNSENT);
//                    seqNum = DatabaseHandler.increment(seqNum);
//                    reportEntity.setSequenceNum(seqNum);
                    reportEntity.setSequenceNum((int) crse.getSeqNum());
                    reportEntity.setSentSmsStatus(crse.getFarmerSMSStatus());
                }

            } else {
                reportEntity.setSentStatus(CollectionConstants.UNSENT);
                reportEntity.setSentSmsStatus(CollectionConstants.UNSENT);
                seqNum = DatabaseHandler.increment(seqNum);
                reportEntity.setSequenceNum(seqNum);
            }


            if (reportEntity.getMilkType().equalsIgnoreCase(CattleType.TEST) ||
                    Util.checkIfRegisteredCode(reportEntity.farmerId)) {
                reportEntity.setCollectionType(CollectionConstants.REPORT_TYPE_SAMPLE);
                //  reportEntity.setMilkType(CattleType.COW);
                // Util.displayErrorToast(reportEntity.collectionType, DevAppApplication.getAmcuContext());
            }


            collectionRecordDao.update(reportEntity);
        }

    }

    public void migrateSalesData() {

        SalesRecordDao salesRecordDao =
                (SalesRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_SALES, false);
        SalesRecordStatusDao salesRecordStatusDao = (SalesRecordStatusDao) DaoFactory.getDao(CollectionConstants.SALES_RECORD_STATUS, false);

        ArrayList<SalesRecordEntity> salesRecordEntities =
                (ArrayList<SalesRecordEntity>) salesRecordDao.findAll();

        for (SalesRecordEntity salesRecordEntity : salesRecordEntities) {
            salesRecordEntity.setPostShift(getShift(salesRecordEntity.getCollectionTime()));
            salesRecordEntity.setPostDate(getDateFormatFromMillis("yyyy-MM-dd", salesRecordEntity.getCollectionTime()));

            SalesRecordStatusEntity srse = salesRecordStatusDao.findBySalesReferenceIndex(salesRecordEntity.getColumnId());
            if (srse != null) {
                salesRecordEntity.setSentStatus(srse.getNwSendStatus());
                if (srse.getNwSendStatus() == CollectionConstants.UNSENT) {
                    seqNum = DatabaseHandler.increment(seqNum);
                    salesRecordEntity.setSequenceNumber(seqNum);
                } else {
                    salesRecordEntity.setSequenceNumber(srse.getSalesSeqNum());
                }
                salesRecordEntity.setSentSmsStatus(srse.getSalesSMSStatus());
            } else {
                salesRecordEntity.setSentStatus(CollectionConstants.UNSENT);
                salesRecordEntity.setSentSmsStatus(CollectionConstants.UNSENT);
                seqNum = DatabaseHandler.increment(seqNum);
                salesRecordEntity.setSequenceNumber(seqNum);
            }

            salesRecordDao.update(salesRecordEntity);
        }

    }

    public void migrateTankerCollectionTable() {

        TankerCollectionDao tankerCollectionDao =
                (TankerCollectionDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_TANKER, false);

        ArrayList<TankerCollectionEntity> tankerCollectionEntities =
                (ArrayList<TankerCollectionEntity>) tankerCollectionDao.findAll();

        for (TankerCollectionEntity tankerCollectionEntity : tankerCollectionEntities) {
            tankerCollectionEntity.setPostShift(getShift(tankerCollectionEntity.getCollectionTime()));
            tankerCollectionEntity.setPostDate(getDateFormatFromMillis("yyyy-MM-dd", tankerCollectionEntity.getCollectionTime()));
            if (tankerCollectionEntity.getSent() == CollectionConstants.UNSENT) {
                seqNum = DatabaseHandler.increment(seqNum);
                tankerCollectionEntity.setSequenceNum(seqNum);
            }

            tankerCollectionDao.update(tankerCollectionEntity);
        }

    }

    public void migrateEditRecords() {

        EditRecordDao editRecordDao =
                (EditRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_EDITED, false);
        EditRecordStatusDao editRecordStatusDao = (EditRecordStatusDao) DaoFactory.getDao(CollectionConstants.EDIT_RECORD_STATUS, false);
        ArrayList<ReportEntity> editRecordEntities =
                (ArrayList<ReportEntity>) editRecordDao.findAll();

        for (ReportEntity reportEntity : editRecordEntities) {
            reportEntity.setPostShift(getShift(reportEntity.getMiliTime()));
            reportEntity.setPostDate(getDateFormatFromMillis("yyyy-MM-dd", reportEntity.getMiliTime()));
            EditRecordStatusEntity erse = editRecordStatusDao.findByEditReferenceIndex(reportEntity.getColumnId());
//            EditRecordStatusEntity extends SalesRecordEntity, and hence getters are not intuitive
            if (erse != null) {
                reportEntity.setSentStatus(erse.getNwSendStatus());
                if (erse.getNwSendStatus() == CollectionConstants.UNSENT) {
                    seqNum = DatabaseHandler.increment(seqNum);
                    reportEntity.setSequenceNum(seqNum);
                } else {
                    reportEntity.setSequenceNum((int) erse.getSalesSeqNum());
                }
                reportEntity.setSentSmsStatus(erse.getSalesSMSStatus());
            } else {
                reportEntity.setSentStatus(CollectionConstants.UNSENT);
                reportEntity.setSentSmsStatus(CollectionConstants.UNSENT);
                seqNum = DatabaseHandler.increment(seqNum);
                reportEntity.setSequenceNum(seqNum);
            }

            editRecordDao.update(reportEntity);
        }


    }

    public void migrateRouteRecords() {

        ArrayList<Long> allUnsentId = getAllUnsentColumnId();
        for (Long colId : allUnsentId) {
            databaseHandler.addSequenceNumAndRefNumber(sqLiteDatabase,
                    colId, CollectionConstants.REPORT_TYPE_EDITED);
            String updateQuery = "update " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT
                    + " set " + EditRecordCollectionTable.KEY_REPORT_SENT_STATUS +
                    " = " + CollectionConstants.UNSENT
                    + " Where " + EditRecordCollectionTable.KEY_REPORT_COLUMNID +
                    " = " + colId;
            sqLiteDatabase.execSQL(updateQuery);
        }

        boolean isRouteAvailable = isRouteAvailable();

        if (isRouteAvailable) {

            sqLiteDatabase.rawQuery("update " + RouteCollectionTable.TABLE_TRUCK_DETAILS
                    + " set " + RouteCollectionTable.POST_SHIFT + " = " +
                    getShift(Long.valueOf(RouteCollectionTable.KEY_TRUCK_SECURITY_TIME)), null);
            sqLiteDatabase.rawQuery("update " + RouteCollectionTable.TABLE_TRUCK_DETAILS
                    + " set " + RouteCollectionTable.POST_DATE + " = " +
                    getDateFormatFromMillis("yyyy-MM-dd", Long.valueOf(RouteCollectionTable.KEY_TRUCK_SECURITY_TIME)), null);
        }
    }

    public void migrateAdditionalParamsRecords() {
        AdditionalParamsDao additionalParamsDao =
                (AdditionalParamsDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_EDITED_ADDITIONAL, false);

        ArrayList<AdditionalParamsEntity> additionalParamsEntities =
                (ArrayList<AdditionalParamsEntity>) additionalParamsDao.findAll();

        for (AdditionalParamsEntity entity : additionalParamsEntities) {
            entity.setPostShift(getShift(entity.getUpdatedTime()));
            entity.setPostDate(getDateFormatFromMillis("yyyy-MM-dd", entity.getUpdatedTime()));
            entity.setCollectionTime(entity.getUpdatedTime());

            CollectionRecordStatusEntity crse = collectionStatusDao.findByReportTableIndexAndType(entity.getColumnId(),
                    CollectionConstants.REPORT_TYPE_EDITED_ADDITIONAL);
            if (crse != null) {
                entity.setSentStatus(crse.getNwSendStatus());
                if (crse.getNwSendStatus() == CollectionConstants.UNSENT) {
                    seqNum = DatabaseHandler.increment(seqNum);
                    entity.setSeqNum(seqNum);
                } else {
                    entity.setSeqNum(crse.getSeqNum());
                }
                entity.setSmsSentStatus(crse.getFarmerSMSStatus());

            } else {
                entity.setSentStatus(CollectionConstants.SENT);
                entity.setSmsSentStatus(CollectionConstants.SENT);
            }

            additionalParamsDao.update(entity);
        }
    }

    public void migrateAgentSplitData() {

        AgentSplitDao agentSplitDao =
                (AgentSplitDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_AGENT_SPLIT, false);

        ArrayList<AgentSplitEntity> agentSplitEntities =
                (ArrayList<AgentSplitEntity>) agentSplitDao.findAll();

        for (AgentSplitEntity agentSplitEntity : agentSplitEntities) {
//            Util.displayErrorToast("Collection time: "+agentSplitEntity.getCollectionTime(),
//                    DevAppApplication.getAmcuContext());

            agentSplitEntity.setPostShift(getShift(agentSplitEntity.getCollectionTime()));
            agentSplitEntity.setPostDate(getDateFormatFromMillis("yyyy-MM-dd", agentSplitEntity.getCollectionTime()));

            CollectionRecordStatusEntity crse = collectionStatusDao.findByReportTableIndexAndType(agentSplitEntity.getColumnId(),
                    CollectionConstants.REPORT_TYPE_AGENT_SPLIT);
            if (crse != null) {
                agentSplitEntity.setSent(crse.getNwSendStatus());
                if (crse.getNwSendStatus() == CollectionConstants.UNSENT) {
                    seqNum = DatabaseHandler.increment(seqNum);
                    agentSplitEntity.setSequenceNumber(seqNum);
                    agentSplitEntity.setSeqNum(seqNum);
                } else {
                    agentSplitEntity.setSequenceNumber(crse.getSeqNum());
                    agentSplitEntity.setSeqNum((int) crse.getSeqNum());
                }
                agentSplitEntity.setSmsSent(crse.getFarmerSMSStatus());
            } else {
                agentSplitEntity.setSent(CollectionConstants.UNSENT);
                agentSplitEntity.setSmsSent(CollectionConstants.UNSENT);
                seqNum = DatabaseHandler.increment(seqNum);
                agentSplitEntity.setSequenceNumber(seqNum);
                agentSplitEntity.setSeqNum(seqNum);
            }

            agentSplitDao.update(agentSplitEntity);
        }
    }

    public boolean isReportDataAvailabe() {
        boolean returnValue = false;
        String query = "Select count(*) " + " from " + FarmerCollectionTable.TABLE_REPORTS;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            returnValue = true;
        }

        return returnValue;
    }

    public boolean isTankerDataAvailabe() {
        boolean returnValue = false;
        String query = "Select count(*) " + " from " + TankerCollectionTable.TABLE_TANKER;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            returnValue = true;
        }

        return returnValue;
    }

    public boolean isRouteAvailable() {
        boolean returnValue = false;
        String query = "Select count(*) " + " from " + RouteCollectionTable.TABLE_TRUCK_DETAILS;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            returnValue = true;
        }

        return returnValue;
    }

    public boolean isEditAvailable() {
        boolean returnValue = false;
        String query = "Select count(*) " + " from " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean isSalesAvailable() {
        boolean returnValue = false;
        String query = "Select count(*) " + " from " + SalesCollectionTable.TABLE_SALES_REPORT;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            returnValue = true;
        }
        return returnValue;
    }

    public boolean isAgentSplitAvailable() {
        boolean returnValue = false;
        String query = "Select count(*) " + " from " + SplitRecordTable.TABLE;
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            returnValue = true;
        }
        return returnValue;
    }


    public ArrayList<Long> getAllUnsentColumnId() {

        ArrayList<Long> allUnsentColId = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + RouteCollectionTable.TABLE_TRUCK_DETAILS +
                " Where " + RouteCollectionTable.KEY_TRUCK_SENT_STATUS
                + " = " + TruckCCDatabase.SERVER_UNSENT_STATUS;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        if (cursor != null && cursor.moveToFirst())

            do {
                allUnsentColId.add(cursor.getLong(0));

            } while (cursor.moveToNext());

        return allUnsentColId;

    }

    private void migrateRatechartNameTable() {
        ArrayList<RatechartDetailsEnt> rateChartdetEnt = new ArrayList<RatechartDetailsEnt>();
        String selectQuery = "SELECT * FROM " + DatabaseHandler.TABLE_RATECHART_NAME;
        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                RatechartDetailsEnt ratechEnt = new RatechartDetailsEnt();
                ratechEnt.rateChartName = cursor.getString(1);
                ratechEnt.rateMilkType = cursor.getString(2);
                ratechEnt.rateOther1 = cursor.getString(3);
                ratechEnt.rateOther2 = cursor.getString(4);
                ratechEnt.rateValidityFrom = cursor.getString(5);
                ratechEnt.rateValidityTo = cursor.getString(6);
                ratechEnt.rateSocId = cursor.getString(7);
                ratechEnt.rateLvalidityFrom = cursor.getLong(8);
                ratechEnt.rateLvalidityTo = cursor.getLong(9);
                ratechEnt.isActive = cursor.getString(10);
                ratechEnt.ratechartType = cursor.getString(11);

                rateChartdetEnt.add(ratechEnt);

            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        SimpleDateFormat sDF = new SimpleDateFormat("dd-MM-yyyy");
        for (RatechartDetailsEnt rDE : rateChartdetEnt) {
            try {
                rDE.rateLvalidityFrom = sDF.parse(rDE.rateValidityFrom).getTime();
                rDE.rateLvalidityTo = sDF.parse(rDE.rateValidityTo).getTime();
                ContentValues values = new ContentValues();
                values.put(RateChartNameTable.LVALIDITYFROM, rDE.rateLvalidityFrom);
                values.put(RateChartNameTable.LVALIDITYTO, rDE.rateLvalidityTo);
                sqLiteDatabase.update(DatabaseHandler.TABLE_RATECHART_NAME,
                        values, RateChartNameTable.NAME + " = ?", new String[]{rDE.rateChartName});
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void migrateReportTableFor43() {


        ArrayList<String> allQuery = new ArrayList<>();


        String query = "Update " + FarmerCollectionTable.TABLE_REPORTS + " set "
                + FarmerCollectionTable.KEY_REPORT_FAT + " =" + 0 + " where " +
                FarmerCollectionTable.KEY_REPORT_FAT + " ='NA'";
        allQuery.add(query);

        query = "Update " + FarmerCollectionTable.TABLE_REPORTS + " set "
                + FarmerCollectionTable.KEY_REPORT_SNF + " =" + 0 + " where " +
                FarmerCollectionTable.KEY_REPORT_SNF + " ='NA'";
        allQuery.add(query);

        query = "Update " + FarmerCollectionTable.TABLE_REPORTS + " set "
                + FarmerCollectionTable.KEY_REPORT_CLR + " =" + 0 + " where " +
                FarmerCollectionTable.KEY_REPORT_CLR + " ='NA'";
        allQuery.add(query);

        query = "Update " + FarmerCollectionTable.TABLE_REPORTS + " set "
                + FarmerCollectionTable.KEY_REPORT_RATE + " =" + 0 + " where " +
                FarmerCollectionTable.KEY_REPORT_RATE + " ='NA'";
        allQuery.add(query);

        query = "Update " + FarmerCollectionTable.TABLE_REPORTS + " set "
                + FarmerCollectionTable.KEY_REPORT_AMOUNT + " =" + 0 + " where " +
                FarmerCollectionTable.KEY_REPORT_AMOUNT + " ='NA'";
        allQuery.add(query);

        query = "Update " + FarmerCollectionTable.TABLE_REPORTS + " set "
                + FarmerCollectionTable.OLD_KEY_TEMP + " =" + 0 + " where " +
                FarmerCollectionTable.OLD_KEY_TEMP + " ='NA'";
        allQuery.add(query);

        allQuery.add(FarmerCollectionTable.CREATE_TEMP_REPORT_TABLE);


        query = insertIntoTempReportTable(FarmerCollectionTable.TABLE_TEMP_REPORT,
                FarmerCollectionTable.TABLE_REPORTS);
        allQuery.add(query);

        allQuery.add("DROP TABLE " + FarmerCollectionTable.TABLE_REPORTS);


        allQuery.add(FarmerCollectionTable.CREATE_REPORT_TABLE);
        query = insertIntoReportTable(FarmerCollectionTable.TABLE_REPORTS,
                FarmerCollectionTable.TABLE_TEMP_REPORT);
        allQuery.add(query);

        allQuery.add("DROP TABLE " + FarmerCollectionTable.TABLE_TEMP_REPORT);

        for (String individualQuery : allQuery) {
            sqLiteDatabase.execSQL(individualQuery);
        }

    }

    private String insertIntoTempReportTable(String toTable, String fromTable) {
        String query = "Insert into " + toTable +
                "(" + FarmerCollectionTable.KEY_REPORT_COLUMNID + ", "
                + FarmerCollectionTable.KEY_REPORT_FARMERID + "," + FarmerCollectionTable.KEY_REPORT_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_SNF + "," + FarmerCollectionTable.KEY_REPORT_FAT + ","
                + FarmerCollectionTable.KEY_REPORT_USER + "," + FarmerCollectionTable.KEY_REPORT_MANUAL + "," + FarmerCollectionTable.KEY_REPORT_AMOUNT + ","
                + FarmerCollectionTable.KEY_REPORT_QUANTITY + ","
                + FarmerCollectionTable.KEY_REPORT_TAXNUM + "," + FarmerCollectionTable.KEY_REPORT_MILKTYPE + ","
                + FarmerCollectionTable.KEY_REPORT_RATE + "," + FarmerCollectionTable.KEY_REPORT_LDATE + ","
                + FarmerCollectionTable.KEY_REPORT_SOCIETYID + "," + FarmerCollectionTable.KEY_REPORT_BONUS + ","
                + FarmerCollectionTable.KEY_REPORT_TIME + "," + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                + "," + FarmerCollectionTable.KEY_REPORT_AWM + "," + FarmerCollectionTable.KEY_REPORT_CLR
                + "," + FarmerCollectionTable.KEY_REPORT_STATUS + ","
                + FarmerCollectionTable.KEY_REPORT_WEIGHTMANUAL + "," + FarmerCollectionTable.KEY_REPORT_MILKOMANUAL
                + "," + FarmerCollectionTable.KEY_REPORT_MILKANALYSERTIME + ","
                + FarmerCollectionTable.KEY_REPORT_WEIGHINGTIME + "," + FarmerCollectionTable.KEY_REPORT_TEMP
                + "," + FarmerCollectionTable.KEY_REPORT_COMMITED + "," + FarmerCollectionTable.KEY_REPORT_TYPE + ","
                + FarmerCollectionTable.KEY_MILKQUALITY + "," + FarmerCollectionTable.KEY_REPORT_RATEMODE + "," + FarmerCollectionTable.KEY_REPORT_NUMBER_OF_CANS + ","
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "," + FarmerCollectionTable.KEY_REPORT_ROUTE + ","
                + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + ","
                + FarmerCollectionTable.KEY_REPORT_RATECHART_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_KG_QTY + "," + FarmerCollectionTable.KEY_REPORT_LTR_QTY + "," + FarmerCollectionTable.KEY_TIPPING_START_TIME
                + ", " + FarmerCollectionTable.KEY_TIPPING_END_TIME + ", " + FarmerCollectionTable.KEY_REPORT_AGENT_ID
                + ", " + FarmerCollectionTable.KEY_REPORT_MILKSTATUS_CODE + "," + FarmerCollectionTable.KEY_REPORT_RATE_CALC_DEVICE
                + ","
                + FarmerCollectionTable.KEY_REPORT_MA_SERIAL + ","
                + FarmerCollectionTable.KEY_REPORT_MA_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_SALT + ","
                + FarmerCollectionTable.KEY_REPORT_FREEZING_POINT + ","
                + FarmerCollectionTable.KEY_REPORT_PH + ","
                + FarmerCollectionTable.KEY_REPORT_PROTEIN + ","
                + FarmerCollectionTable.KEY_REPORT_CONDUTIVITY + ","
                + FarmerCollectionTable.KEY_REPORT_LACTOSE + ", "
                + FarmerCollectionTable.KEY_REPORT_DENSITY + ", "
                + FarmerCollectionTable.KEY_REPORT_CALIBRATION + ", "
                + FarmerCollectionTable.KEY_REPORT_INCENTIVE_RATE + ", "
                + FarmerCollectionTable.KEY_REPORT_INCENTIVE_AMOUNT + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_FAT + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_SNF + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_CLR + ", "
                + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + ", "
                + FarmerCollectionTable.SMS_SENT_STATUS + ", "
                + FarmerCollectionTable.POST_DATE + ", "
                + FarmerCollectionTable.POST_SHIFT + ", "
                + FarmerCollectionTable.SEQUENCE_NUMBER
                + ")"
                + " Select " +
                FarmerCollectionTable.KEY_REPORT_COLUMNID + ", "
                + FarmerCollectionTable.KEY_REPORT_FARMERID + "," + FarmerCollectionTable.KEY_REPORT_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_SNF + "," + FarmerCollectionTable.KEY_REPORT_FAT + ","
                + FarmerCollectionTable.KEY_REPORT_USER + "," + FarmerCollectionTable.KEY_REPORT_MANUAL + "," + FarmerCollectionTable.KEY_REPORT_AMOUNT + ","
                + FarmerCollectionTable.KEY_REPORT_QUANTITY + ","
                + FarmerCollectionTable.OLD_KEY_TXN_NUMBER + "," + FarmerCollectionTable.KEY_REPORT_MILKTYPE + ","
                + FarmerCollectionTable.KEY_REPORT_RATE + "," + FarmerCollectionTable.KEY_REPORT_LDATE + ","
                + FarmerCollectionTable.KEY_REPORT_SOCIETYID + "," + FarmerCollectionTable.OLD_KEY_BONUS + ","
                + FarmerCollectionTable.KEY_REPORT_TIME + "," + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                + "," + FarmerCollectionTable.KEY_REPORT_AWM + "," + FarmerCollectionTable.KEY_REPORT_CLR
                + "," + FarmerCollectionTable.KEY_REPORT_STATUS + ","
                + FarmerCollectionTable.KEY_REPORT_WEIGHTMANUAL + "," + FarmerCollectionTable.KEY_REPORT_MILKOMANUAL
                + "," + FarmerCollectionTable.OLD_KEY_QUALITY_TIME + ","
                + FarmerCollectionTable.OLD_KEY_QUANTITY_TIME + "," + FarmerCollectionTable.OLD_KEY_TEMP
                + "," + FarmerCollectionTable.KEY_REPORT_COMMITED + "," + FarmerCollectionTable.KEY_REPORT_TYPE + ","
                + FarmerCollectionTable.KEY_MILKQUALITY + "," + FarmerCollectionTable.KEY_REPORT_RATEMODE + "," + FarmerCollectionTable.KEY_REPORT_NUMBER_OF_CANS + ","
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "," + FarmerCollectionTable.KEY_REPORT_ROUTE + ","
                + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + ","
                + FarmerCollectionTable.KEY_REPORT_RATECHART_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_KG_QTY + "," + FarmerCollectionTable.KEY_REPORT_LTR_QTY + "," + FarmerCollectionTable.KEY_TIPPING_START_TIME
                + ", " + FarmerCollectionTable.KEY_TIPPING_END_TIME + ", " + FarmerCollectionTable.KEY_REPORT_AGENT_ID
                + ", " + FarmerCollectionTable.KEY_REPORT_MILKSTATUS_CODE + "," + FarmerCollectionTable.KEY_REPORT_RATE_CALC_DEVICE
                + ","
                + FarmerCollectionTable.KEY_REPORT_MA_SERIAL + ","
                + FarmerCollectionTable.KEY_REPORT_MA_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_SALT + ","
                + FarmerCollectionTable.KEY_REPORT_FREEZING_POINT + ","
                + FarmerCollectionTable.KEY_REPORT_PH + ","
                + FarmerCollectionTable.KEY_REPORT_PROTEIN + ","
                + FarmerCollectionTable.KEY_REPORT_CONDUTIVITY + ","
                + FarmerCollectionTable.KEY_REPORT_LACTOSE + ", "
                + FarmerCollectionTable.KEY_REPORT_DENSITY + ", "
                + FarmerCollectionTable.KEY_REPORT_CALIBRATION + ", "
                + FarmerCollectionTable.KEY_REPORT_INCENTIVE_RATE + ", "
                + FarmerCollectionTable.KEY_REPORT_INCENTIVE_AMOUNT + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_FAT + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_SNF + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_CLR + ", "
                + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + ", "
                + FarmerCollectionTable.SMS_SENT_STATUS + ", "
                + FarmerCollectionTable.POST_DATE + ", "
                + FarmerCollectionTable.POST_SHIFT + ", "
                + FarmerCollectionTable.SEQUENCE_NUMBER
                + " from " + fromTable;

        return query;

    }

    private String insertIntoReportTable(String toTable, String fromTable) {
        String query = "Insert into " + toTable +
                "(" + FarmerCollectionTable.KEY_REPORT_COLUMNID + ", "
                + FarmerCollectionTable.KEY_REPORT_FARMERID + "," + FarmerCollectionTable.KEY_REPORT_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_SNF + "," + FarmerCollectionTable.KEY_REPORT_FAT + ","
                + FarmerCollectionTable.KEY_REPORT_USER + "," + FarmerCollectionTable.KEY_REPORT_MANUAL + "," + FarmerCollectionTable.KEY_REPORT_AMOUNT + ","
                + FarmerCollectionTable.KEY_REPORT_QUANTITY + ","
                + FarmerCollectionTable.KEY_REPORT_TAXNUM + "," + FarmerCollectionTable.KEY_REPORT_MILKTYPE + ","
                + FarmerCollectionTable.KEY_REPORT_RATE + "," + FarmerCollectionTable.KEY_REPORT_LDATE + ","
                + FarmerCollectionTable.KEY_REPORT_SOCIETYID + "," + FarmerCollectionTable.KEY_REPORT_BONUS + ","
                + FarmerCollectionTable.KEY_REPORT_TIME + "," + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                + "," + FarmerCollectionTable.KEY_REPORT_AWM + "," + FarmerCollectionTable.KEY_REPORT_CLR
                + "," + FarmerCollectionTable.KEY_REPORT_STATUS + ","
                + FarmerCollectionTable.KEY_REPORT_WEIGHTMANUAL + "," + FarmerCollectionTable.KEY_REPORT_MILKOMANUAL
                + "," + FarmerCollectionTable.KEY_REPORT_MILKANALYSERTIME + ","
                + FarmerCollectionTable.KEY_REPORT_WEIGHINGTIME + "," + FarmerCollectionTable.KEY_REPORT_TEMP
                + "," + FarmerCollectionTable.KEY_REPORT_COMMITED + "," + FarmerCollectionTable.KEY_REPORT_TYPE + ","
                + FarmerCollectionTable.KEY_MILKQUALITY + "," + FarmerCollectionTable.KEY_REPORT_RATEMODE + "," + FarmerCollectionTable.KEY_REPORT_NUMBER_OF_CANS + ","
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "," + FarmerCollectionTable.KEY_REPORT_ROUTE + ","
                + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + ","
                + FarmerCollectionTable.KEY_REPORT_RATECHART_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_KG_QTY + "," + FarmerCollectionTable.KEY_REPORT_LTR_QTY + "," + FarmerCollectionTable.KEY_TIPPING_START_TIME
                + ", " + FarmerCollectionTable.KEY_TIPPING_END_TIME + ", " + FarmerCollectionTable.KEY_REPORT_AGENT_ID
                + ", " + FarmerCollectionTable.KEY_REPORT_MILKSTATUS_CODE + "," + FarmerCollectionTable.KEY_REPORT_RATE_CALC_DEVICE
                + ","
                + FarmerCollectionTable.KEY_REPORT_MA_SERIAL + ","
                + FarmerCollectionTable.KEY_REPORT_MA_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_SALT + ","
                + FarmerCollectionTable.KEY_REPORT_FREEZING_POINT + ","
                + FarmerCollectionTable.KEY_REPORT_PH + ","
                + FarmerCollectionTable.KEY_REPORT_PROTEIN + ","
                + FarmerCollectionTable.KEY_REPORT_CONDUTIVITY + ","
                + FarmerCollectionTable.KEY_REPORT_LACTOSE + ", "
                + FarmerCollectionTable.KEY_REPORT_DENSITY + ", "
                + FarmerCollectionTable.KEY_REPORT_CALIBRATION + ", "
                + FarmerCollectionTable.KEY_REPORT_INCENTIVE_RATE + ", "
                + FarmerCollectionTable.KEY_REPORT_INCENTIVE_AMOUNT + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_FAT + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_SNF + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_CLR + ", "
                + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + ", "
                + FarmerCollectionTable.SMS_SENT_STATUS + ", "
                + FarmerCollectionTable.POST_DATE + ", "
                + FarmerCollectionTable.POST_SHIFT + ", "
                + FarmerCollectionTable.SEQUENCE_NUMBER
                + ")"
                + " Select " +
                FarmerCollectionTable.KEY_REPORT_COLUMNID + ", "
                + FarmerCollectionTable.KEY_REPORT_FARMERID + "," + FarmerCollectionTable.KEY_REPORT_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_SNF + "," + FarmerCollectionTable.KEY_REPORT_FAT + ","
                + FarmerCollectionTable.KEY_REPORT_USER + "," + FarmerCollectionTable.KEY_REPORT_MANUAL + "," + FarmerCollectionTable.KEY_REPORT_AMOUNT + ","
                + FarmerCollectionTable.KEY_REPORT_QUANTITY + ","
                + FarmerCollectionTable.KEY_REPORT_TAXNUM + "," + FarmerCollectionTable.KEY_REPORT_MILKTYPE + ","
                + FarmerCollectionTable.KEY_REPORT_RATE + "," + FarmerCollectionTable.KEY_REPORT_LDATE + ","
                + FarmerCollectionTable.KEY_REPORT_SOCIETYID + "," + FarmerCollectionTable.KEY_REPORT_BONUS + ","
                + FarmerCollectionTable.KEY_REPORT_TIME + "," + FarmerCollectionTable.KEY_REPORT_TIME_MILLI
                + "," + FarmerCollectionTable.KEY_REPORT_AWM + "," + FarmerCollectionTable.KEY_REPORT_CLR
                + "," + FarmerCollectionTable.KEY_REPORT_STATUS + ","
                + FarmerCollectionTable.KEY_REPORT_WEIGHTMANUAL + "," + FarmerCollectionTable.KEY_REPORT_MILKOMANUAL
                + "," + FarmerCollectionTable.KEY_REPORT_MILKANALYSERTIME + ","
                + FarmerCollectionTable.KEY_REPORT_WEIGHINGTIME + "," + FarmerCollectionTable.KEY_REPORT_TEMP
                + "," + FarmerCollectionTable.KEY_REPORT_COMMITED + "," + FarmerCollectionTable.KEY_REPORT_TYPE + ","
                + FarmerCollectionTable.KEY_MILKQUALITY + "," + FarmerCollectionTable.KEY_REPORT_RATEMODE + "," + FarmerCollectionTable.KEY_REPORT_NUMBER_OF_CANS + ","
                + FarmerCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + "," + FarmerCollectionTable.KEY_REPORT_ROUTE + ","
                + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + ","
                + FarmerCollectionTable.KEY_REPORT_RATECHART_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_KG_QTY + "," + FarmerCollectionTable.KEY_REPORT_LTR_QTY + "," + FarmerCollectionTable.KEY_TIPPING_START_TIME
                + ", " + FarmerCollectionTable.KEY_TIPPING_END_TIME + ", " + FarmerCollectionTable.KEY_REPORT_AGENT_ID
                + ", " + FarmerCollectionTable.KEY_REPORT_MILKSTATUS_CODE + "," + FarmerCollectionTable.KEY_REPORT_RATE_CALC_DEVICE
                + ","
                + FarmerCollectionTable.KEY_REPORT_MA_SERIAL + ","
                + FarmerCollectionTable.KEY_REPORT_MA_NAME + ","
                + FarmerCollectionTable.KEY_REPORT_SALT + ","
                + FarmerCollectionTable.KEY_REPORT_FREEZING_POINT + ","
                + FarmerCollectionTable.KEY_REPORT_PH + ","
                + FarmerCollectionTable.KEY_REPORT_PROTEIN + ","
                + FarmerCollectionTable.KEY_REPORT_CONDUTIVITY + ","
                + FarmerCollectionTable.KEY_REPORT_LACTOSE + ", "
                + FarmerCollectionTable.KEY_REPORT_DENSITY + ", "
                + FarmerCollectionTable.KEY_REPORT_CALIBRATION + ", "
                + FarmerCollectionTable.KEY_REPORT_INCENTIVE_RATE + ", "
                + FarmerCollectionTable.KEY_REPORT_INCENTIVE_AMOUNT + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_FAT + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_SNF + ", "
                + FarmerCollectionTable.KEY_REPORT_KG_CLR + ", "
                + FarmerCollectionTable.KEY_REPORT_SENT_STATUS + ", "
                + FarmerCollectionTable.SMS_SENT_STATUS + ", "
                + FarmerCollectionTable.POST_DATE + ", "
                + FarmerCollectionTable.POST_SHIFT + ", "
                + FarmerCollectionTable.SEQUENCE_NUMBER
                + " from " + fromTable;

        return query;

    }

    private void migrateSalesReportTableFor43() {

        ArrayList<String> allQuery = new ArrayList<>();
        allQuery.add(SalesCollectionTable.CREATE_TEMP_SALES_REPORT);
        allQuery.add(insertIntoTempSalesReportTable(SalesCollectionTable.TABLE_TEMP_SALES_REPORT,
                SalesCollectionTable.TABLE_SALES_REPORT));
        allQuery.add("DROP TABLE " + SalesCollectionTable.TABLE_SALES_REPORT);
        allQuery.add(SalesCollectionTable.CREATE_SALES_REPORT);
        allQuery.add(insertIntoSalesReportTable(SalesCollectionTable.TABLE_SALES_REPORT,
                SalesCollectionTable.TABLE_TEMP_SALES_REPORT));
        allQuery.add("DROP TABLE " + SalesCollectionTable.TABLE_TEMP_SALES_REPORT);

        for (String query : allQuery) {
            sqLiteDatabase.execSQL(query);
        }
    }

    private String insertIntoTempSalesReportTable(String toTable, String fromTable) {
        String query = "INSERT INTO " + toTable +
                "(" + SalesCollectionTable.KEY_SALES_COLUMNID + ", "
                + SalesCollectionTable.KEY_SALES_ID + ", "
                + SalesCollectionTable.KEY_SALES_NAME + ", "
                + SalesCollectionTable.KEY_SALES_SNF + ", "
                + SalesCollectionTable.KEY_SALES_FAT + ", "
                + SalesCollectionTable.KEY_SALES_USER + ", "
                + SalesCollectionTable.KEY_SALES_MANUAL + ", "
                + SalesCollectionTable.KEY_SALES_AMOUNT + ", "
                + SalesCollectionTable.KEY_SALES_QUANTITY + ", "
                + SalesCollectionTable.KEY_SALES_TXNNUM + ", "
                + SalesCollectionTable.KEY_SALES_MILKTYPE + ", "
                + SalesCollectionTable.KEY_SALES_RATE + ", "
                + SalesCollectionTable.KEY_SALES_SOCIETYID + ", "
                + SalesCollectionTable.KEY_SALES_TIME + ", "
                + SalesCollectionTable.KEY_SALES_AWM + ", "
                + SalesCollectionTable.KEY_SALES_CLR + ", "
                + SalesCollectionTable.KEY_SALES_STATUS + ", "
                + SalesCollectionTable.KEY_SALES_WEIGHTMANUAL + ", "
                + SalesCollectionTable.KEY_SALES_MILKOMANUAL + ", "
                + SalesCollectionTable.KEY_SALES_TIME_MILLI + ", "
                + SalesCollectionTable.KEY_SALES_TEMP + ", "
                + SalesCollectionTable.KEY_SALES_TXN_TYPE + ", "
                + SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE + ", "
                + SalesCollectionTable.KEY_SALES_SEND_STATUS + ", "
                + SalesCollectionTable.SMS_SENT_STATUS + ", "
                + SalesCollectionTable.POST_DATE + ", "
                + SalesCollectionTable.POST_SHIFT + ", "
                + SalesCollectionTable.SEQUENCE_NUMBER + ")"
                + " SELECT "
                + SalesCollectionTable.KEY_SALES_COLUMNID + ", "
                + SalesCollectionTable.KEY_SALES_ID + ", "
                + SalesCollectionTable.KEY_SALES_NAME + ", "
                + SalesCollectionTable.KEY_SALES_SNF + ", "
                + SalesCollectionTable.KEY_SALES_FAT + ", "
                + SalesCollectionTable.KEY_SALES_USER + ", "
                + SalesCollectionTable.KEY_SALES_MANUAL + ", "
                + SalesCollectionTable.KEY_SALES_AMOUNT + ", "
                + SalesCollectionTable.KEY_SALES_QUANTITY + ", "
                + SalesCollectionTable.OLD_TXN_NUMBER + ", "
                + SalesCollectionTable.KEY_SALES_MILKTYPE + ", "
                + SalesCollectionTable.KEY_SALES_RATE + ", "
                + SalesCollectionTable.KEY_SALES_SOCIETYID + ", "
                + SalesCollectionTable.KEY_SALES_TIME + ", "
                + SalesCollectionTable.KEY_SALES_AWM + ", "
                + SalesCollectionTable.KEY_SALES_CLR + ", "
                + SalesCollectionTable.KEY_SALES_STATUS + ", "
                + SalesCollectionTable.KEY_SALES_WEIGHTMANUAL + ", "
                + SalesCollectionTable.KEY_SALES_MILKOMANUAL + ", "
                + SalesCollectionTable.KEY_SALES_TIME_MILLI + ", "
                + SalesCollectionTable.KEY_SALES_TEMP + ", "
                + SalesCollectionTable.KEY_SALES_TXN_TYPE + ", "
                + SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE + ", "
                + SalesCollectionTable.KEY_SALES_SEND_STATUS + ", "
                + SalesCollectionTable.SMS_SENT_STATUS + ", "
                + SalesCollectionTable.POST_DATE + ", "
                + SalesCollectionTable.POST_SHIFT + ", "
                + SalesCollectionTable.SEQUENCE_NUMBER
                + " FROM " + fromTable;
        return query;
    }

    private String insertIntoSalesReportTable(String toTable, String fromTable) {
        String query = "INSERT INTO " + toTable +
                "(" + SalesCollectionTable.KEY_SALES_COLUMNID + ", "
                + SalesCollectionTable.KEY_SALES_ID + ", "
                + SalesCollectionTable.KEY_SALES_NAME + ", "
                + SalesCollectionTable.KEY_SALES_SNF + ", "
                + SalesCollectionTable.KEY_SALES_FAT + ", "
                + SalesCollectionTable.KEY_SALES_USER + ", "
                + SalesCollectionTable.KEY_SALES_MANUAL + ", "
                + SalesCollectionTable.KEY_SALES_AMOUNT + ", "
                + SalesCollectionTable.KEY_SALES_QUANTITY + ", "
                + SalesCollectionTable.KEY_SALES_TXNNUM + ", "
                + SalesCollectionTable.KEY_SALES_MILKTYPE + ", "
                + SalesCollectionTable.KEY_SALES_RATE + ", "
                + SalesCollectionTable.KEY_SALES_SOCIETYID + ", "
                + SalesCollectionTable.KEY_SALES_TIME + ", "
                + SalesCollectionTable.KEY_SALES_AWM + ", "
                + SalesCollectionTable.KEY_SALES_CLR + ", "
                + SalesCollectionTable.KEY_SALES_STATUS + ", "
                + SalesCollectionTable.KEY_SALES_WEIGHTMANUAL + ", "
                + SalesCollectionTable.KEY_SALES_MILKOMANUAL + ", "
                + SalesCollectionTable.KEY_SALES_TIME_MILLI + ", "
                + SalesCollectionTable.KEY_SALES_TEMP + ", "
                + SalesCollectionTable.KEY_SALES_TXN_TYPE + ", "
                + SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE + ", "
                + SalesCollectionTable.KEY_SALES_SEND_STATUS + ", "
                + SalesCollectionTable.SMS_SENT_STATUS + ", "
                + SalesCollectionTable.POST_DATE + ", "
                + SalesCollectionTable.POST_SHIFT + ", "
                + SalesCollectionTable.SEQUENCE_NUMBER + ")"
                + " SELECT "
                + SalesCollectionTable.KEY_SALES_COLUMNID + ", "
                + SalesCollectionTable.KEY_SALES_ID + ", "
                + SalesCollectionTable.KEY_SALES_NAME + ", "
                + SalesCollectionTable.KEY_SALES_SNF + ", "
                + SalesCollectionTable.KEY_SALES_FAT + ", "
                + SalesCollectionTable.KEY_SALES_USER + ", "
                + SalesCollectionTable.KEY_SALES_MANUAL + ", "
                + SalesCollectionTable.KEY_SALES_AMOUNT + ", "
                + SalesCollectionTable.KEY_SALES_QUANTITY + ", "
                + SalesCollectionTable.KEY_SALES_TXNNUM + ", "
                + SalesCollectionTable.KEY_SALES_MILKTYPE + ", "
                + SalesCollectionTable.KEY_SALES_RATE + ", "
                + SalesCollectionTable.KEY_SALES_SOCIETYID + ", "
                + SalesCollectionTable.KEY_SALES_TIME + ", "
                + SalesCollectionTable.KEY_SALES_AWM + ", "
                + SalesCollectionTable.KEY_SALES_CLR + ", "
                + SalesCollectionTable.KEY_SALES_STATUS + ", "
                + SalesCollectionTable.KEY_SALES_WEIGHTMANUAL + ", "
                + SalesCollectionTable.KEY_SALES_MILKOMANUAL + ", "
                + SalesCollectionTable.KEY_SALES_TIME_MILLI + ", "
                + SalesCollectionTable.KEY_SALES_TEMP + ", "
                + SalesCollectionTable.KEY_SALES_TXN_TYPE + ", "
                + SalesCollectionTable.KEY_SALES_TXN_SUB_TYPE + ", "
                + SalesCollectionTable.KEY_SALES_SEND_STATUS + ", "
                + SalesCollectionTable.SMS_SENT_STATUS + ", "
                + SalesCollectionTable.POST_DATE + ", "
                + SalesCollectionTable.POST_SHIFT + ", "
                + SalesCollectionTable.SEQUENCE_NUMBER
                + " FROM " + fromTable;
        return query;
    }

    private void migrateExtendedReportTableFor43() {
        ArrayList<String> allQuery = new ArrayList<>();
        allQuery.add(EditRecordCollectionTable.CREATE_TEMP_EXTENDED_REPORT_TABLE);
        allQuery.add(insertIntoTempExtendedReportTable(EditRecordCollectionTable.TABLE_TEMP_EXTENDED_REPORT,
                EditRecordCollectionTable.TABLE_EXTENDED_REPORT));
        allQuery.add("DROP TABLE " + EditRecordCollectionTable.TABLE_EXTENDED_REPORT);
        allQuery.add(EditRecordCollectionTable.CREATE_EXTENDED_REPORT_TABLE);
        allQuery.add(insertIntoExtendedReportTable(EditRecordCollectionTable.TABLE_EXTENDED_REPORT,
                EditRecordCollectionTable.TABLE_TEMP_EXTENDED_REPORT));
        allQuery.add("DROP TABLE " + EditRecordCollectionTable.TABLE_TEMP_EXTENDED_REPORT);
        for (String query : allQuery) {
            sqLiteDatabase.execSQL(query);
        }
    }

    private String insertIntoTempExtendedReportTable(String toTable, String fromTable) {
        String query = "INSERT INTO " + toTable +
                "(" + EditRecordCollectionTable.KEY_REPORT_COLUMNID + ", "
                + EditRecordCollectionTable.KEY_REPORT_FARMERID + ", " + EditRecordCollectionTable.KEY_REPORT_NAME + ", "
                + EditRecordCollectionTable.KEY_REPORT_SNF + ", " + EditRecordCollectionTable.KEY_REPORT_FAT + ", "
                + EditRecordCollectionTable.KEY_REPORT_USER + ", " + EditRecordCollectionTable.KEY_REPORT_MANUAL + ", "
                + FarmerCollectionTable.POST_SHIFT + ", " + EditRecordCollectionTable.KEY_REPORT_AMOUNT + ", "
                + EditRecordCollectionTable.KEY_REPORT_QUANTITY + ", " + FarmerCollectionTable.POST_DATE + ", "
                + EditRecordCollectionTable.KEY_REPORT_TAXNUM + ", " + EditRecordCollectionTable.KEY_REPORT_MILKTYPE + ", "
                + EditRecordCollectionTable.KEY_REPORT_RATE + ", " + EditRecordCollectionTable.KEY_REPORT_LDATE + ", "
                + EditRecordCollectionTable.KEY_REPORT_SOCIETYID + ", " + EditRecordCollectionTable.KEY_REPORT_BONUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_TIME + ", " + EditRecordCollectionTable.KEY_REPORT_TIME_MILLI + ", "
                + EditRecordCollectionTable.KEY_REPORT_AWM + ", " + EditRecordCollectionTable.KEY_REPORT_CLR + ", "
                + EditRecordCollectionTable.KEY_REPORT_STATUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_WEIGHTMANUAL + ", " + EditRecordCollectionTable.KEY_REPORT_MILKOMANUAL + ", "
                + EditRecordCollectionTable.KEY_REPORT_MILKANALYSERTIME + ", "
                + EditRecordCollectionTable.KEY_REPORT_WEIGHINGTIME + ", " + EditRecordCollectionTable.KEY_REPORT_TEMP + ", "
                + EditRecordCollectionTable.KEY_REPORT_COMMITED + ", " + EditRecordCollectionTable.KEY_REPORT_TYPE + ", "
                + EditRecordCollectionTable.KEY_MILKQUALITY + ", " + EditRecordCollectionTable.KEY_REPORT_RATEMODE + ", " + EditRecordCollectionTable.KEY_REPORT_NUMBER_OF_CANS + ", "
                + EditRecordCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + ", " + EditRecordCollectionTable.KEY_REPORT_ROUTE + ", " + EditRecordCollectionTable.KEY_REPORT_RECORD_STATUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_FOREIGN_SEQUENCE_ID + ", " + EditRecordCollectionTable.KEY_REPORT_EDITED_TIME + ", "
                + EditRecordCollectionTable.KEY_REPORT_OLD_OR_NEW_FLAG + ", " + EditRecordCollectionTable.KEY_TIPPING_START_TIME + ", "
                + EditRecordCollectionTable.KEY_TIPPING_END_TIME + ", " + EditRecordCollectionTable.KEY_REPORT_AGENT_ID + ", "
                + EditRecordCollectionTable.KEY_REPORT_MILKSTATUS_CODE + ", " + EditRecordCollectionTable.KEY_REPORT_RATE_CALC_DEVICE + ", "
                + EditRecordCollectionTable.KEY_REPORT_MA_SERIAL + ", " + EditRecordCollectionTable.KEY_REPORT_MA_NAME + ", "
                + EditRecordCollectionTable.KEY_REPORT_KG_QTY + ", " + EditRecordCollectionTable.KEY_REPORT_LTR_QTY
                + " ," + EditRecordCollectionTable.SEQUENCE_NUMBER + "," + EditRecordCollectionTable.SMS_SENT_STATUS
                + " ," + EditRecordCollectionTable.KEY_REPORT_SENT_STATUS + ")"
                + " SELECT "
                + EditRecordCollectionTable.KEY_REPORT_COLUMNID + ", "
                + EditRecordCollectionTable.KEY_REPORT_FARMERID + ", " + EditRecordCollectionTable.KEY_REPORT_NAME + ", "
                + EditRecordCollectionTable.KEY_REPORT_SNF + ", " + EditRecordCollectionTable.KEY_REPORT_FAT + ", "
                + EditRecordCollectionTable.KEY_REPORT_USER + ", " + EditRecordCollectionTable.KEY_REPORT_MANUAL + ", "
                + FarmerCollectionTable.POST_SHIFT + ", " + EditRecordCollectionTable.KEY_REPORT_AMOUNT + ", "
                + EditRecordCollectionTable.KEY_REPORT_QUANTITY + ", " + FarmerCollectionTable.POST_DATE + ", "
                + EditRecordCollectionTable.OLD_KEY_TXN_NUMBER + ", " + EditRecordCollectionTable.KEY_REPORT_MILKTYPE + ", "
                + EditRecordCollectionTable.KEY_REPORT_RATE + ", " + EditRecordCollectionTable.KEY_REPORT_LDATE + ", "
                + EditRecordCollectionTable.KEY_REPORT_SOCIETYID + ", " + EditRecordCollectionTable.OLD_KEY_BONUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_TIME + ", " + EditRecordCollectionTable.KEY_REPORT_TIME_MILLI + ", "
                + EditRecordCollectionTable.KEY_REPORT_AWM + ", " + EditRecordCollectionTable.KEY_REPORT_CLR + ", "
                + EditRecordCollectionTable.KEY_REPORT_STATUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_WEIGHTMANUAL + ", " + EditRecordCollectionTable.KEY_REPORT_MILKOMANUAL + ", "
                + EditRecordCollectionTable.OLD_KEY_QUALITY_TIME + ", "
                + EditRecordCollectionTable.OLD_KEY_QUANTITY_TIME + ", " + EditRecordCollectionTable.OLD_KEY_TEMP + ", "
                + EditRecordCollectionTable.KEY_REPORT_COMMITED + ", " + EditRecordCollectionTable.KEY_REPORT_TYPE + ", "
                + EditRecordCollectionTable.KEY_MILKQUALITY + ", " + EditRecordCollectionTable.KEY_REPORT_RATEMODE + ", " + EditRecordCollectionTable.KEY_REPORT_NUMBER_OF_CANS + ", "
                + EditRecordCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + ", " + EditRecordCollectionTable.KEY_REPORT_ROUTE + ", " + EditRecordCollectionTable.KEY_REPORT_RECORD_STATUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_FOREIGN_SEQUENCE_ID + ", " + EditRecordCollectionTable.KEY_REPORT_EDITED_TIME + ", "
                + EditRecordCollectionTable.KEY_REPORT_OLD_OR_NEW_FLAG + ", " + EditRecordCollectionTable.KEY_TIPPING_START_TIME + ", "
                + EditRecordCollectionTable.KEY_TIPPING_END_TIME + ", " + EditRecordCollectionTable.KEY_REPORT_AGENT_ID + ", "
                + EditRecordCollectionTable.KEY_REPORT_MILKSTATUS_CODE + ", " + EditRecordCollectionTable.KEY_REPORT_RATE_CALC_DEVICE + ", "
                + EditRecordCollectionTable.KEY_REPORT_MA_SERIAL + ", " + EditRecordCollectionTable.KEY_REPORT_MA_NAME + ", "
                + EditRecordCollectionTable.KEY_REPORT_KG_QTY + ", " + EditRecordCollectionTable.KEY_REPORT_LTR_QTY
                + ", " + EditRecordCollectionTable.SEQUENCE_NUMBER + ", " + EditRecordCollectionTable.SMS_SENT_STATUS
                + ", " + EditRecordCollectionTable.KEY_REPORT_SENT_STATUS
                + " FROM " + fromTable;
        return query;
    }

    private String insertIntoExtendedReportTable(String toTable, String fromTable) {
        String query = "INSERT INTO " + toTable +
                "(" + EditRecordCollectionTable.KEY_REPORT_COLUMNID + ", "
                + EditRecordCollectionTable.KEY_REPORT_FARMERID + ", " + EditRecordCollectionTable.KEY_REPORT_NAME + ", "
                + EditRecordCollectionTable.KEY_REPORT_SNF + ", " + EditRecordCollectionTable.KEY_REPORT_FAT + ", "
                + EditRecordCollectionTable.KEY_REPORT_USER + ", " + EditRecordCollectionTable.KEY_REPORT_MANUAL + ", "
                + FarmerCollectionTable.POST_SHIFT + ", " + EditRecordCollectionTable.KEY_REPORT_AMOUNT + ", "
                + EditRecordCollectionTable.KEY_REPORT_QUANTITY + ", " + FarmerCollectionTable.POST_DATE + ", "
                + EditRecordCollectionTable.KEY_REPORT_TAXNUM + ", " + EditRecordCollectionTable.KEY_REPORT_MILKTYPE + ", "
                + EditRecordCollectionTable.KEY_REPORT_RATE + ", " + EditRecordCollectionTable.KEY_REPORT_LDATE + ", "
                + EditRecordCollectionTable.KEY_REPORT_SOCIETYID + ", " + EditRecordCollectionTable.KEY_REPORT_BONUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_TIME + ", " + EditRecordCollectionTable.KEY_REPORT_TIME_MILLI + ", "
                + EditRecordCollectionTable.KEY_REPORT_AWM + ", " + EditRecordCollectionTable.KEY_REPORT_CLR + ", "
                + EditRecordCollectionTable.KEY_REPORT_STATUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_WEIGHTMANUAL + ", " + EditRecordCollectionTable.KEY_REPORT_MILKOMANUAL + ", "
                + EditRecordCollectionTable.KEY_REPORT_MILKANALYSERTIME + ", "
                + EditRecordCollectionTable.KEY_REPORT_WEIGHINGTIME + ", " + EditRecordCollectionTable.KEY_REPORT_TEMP + ", "
                + EditRecordCollectionTable.KEY_REPORT_COMMITED + ", " + EditRecordCollectionTable.KEY_REPORT_TYPE + ", "
                + EditRecordCollectionTable.KEY_MILKQUALITY + ", " + EditRecordCollectionTable.KEY_REPORT_RATEMODE + ", " + EditRecordCollectionTable.KEY_REPORT_NUMBER_OF_CANS + ", "
                + EditRecordCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + ", " + EditRecordCollectionTable.KEY_REPORT_ROUTE + ", " + EditRecordCollectionTable.KEY_REPORT_RECORD_STATUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_FOREIGN_SEQUENCE_ID + ", " + EditRecordCollectionTable.KEY_REPORT_EDITED_TIME + ", "
                + EditRecordCollectionTable.KEY_REPORT_OLD_OR_NEW_FLAG + ", " + EditRecordCollectionTable.KEY_TIPPING_START_TIME + ", "
                + EditRecordCollectionTable.KEY_TIPPING_END_TIME + ", " + EditRecordCollectionTable.KEY_REPORT_AGENT_ID + ", "
                + EditRecordCollectionTable.KEY_REPORT_MILKSTATUS_CODE + ", " + EditRecordCollectionTable.KEY_REPORT_RATE_CALC_DEVICE + ", "
                + EditRecordCollectionTable.KEY_REPORT_MA_SERIAL + ", " + EditRecordCollectionTable.KEY_REPORT_MA_NAME + ", "
                + EditRecordCollectionTable.KEY_REPORT_KG_QTY + ", " + EditRecordCollectionTable.KEY_REPORT_LTR_QTY +
                " ," + EditRecordCollectionTable.SEQUENCE_NUMBER + "," + EditRecordCollectionTable.SMS_SENT_STATUS
                + " ," + EditRecordCollectionTable.KEY_REPORT_SENT_STATUS +
                ")"
                + " SELECT "
                + EditRecordCollectionTable.KEY_REPORT_COLUMNID + ", "
                + EditRecordCollectionTable.KEY_REPORT_FARMERID + ", " + EditRecordCollectionTable.KEY_REPORT_NAME + ", "
                + EditRecordCollectionTable.KEY_REPORT_SNF + ", " + EditRecordCollectionTable.KEY_REPORT_FAT + ", "
                + EditRecordCollectionTable.KEY_REPORT_USER + ", " + EditRecordCollectionTable.KEY_REPORT_MANUAL + ", "
                + FarmerCollectionTable.POST_SHIFT + ", " + EditRecordCollectionTable.KEY_REPORT_AMOUNT + ", "
                + EditRecordCollectionTable.KEY_REPORT_QUANTITY + ", " + FarmerCollectionTable.POST_DATE + ", "
                + EditRecordCollectionTable.KEY_REPORT_TAXNUM + ", " + EditRecordCollectionTable.KEY_REPORT_MILKTYPE + ", "
                + EditRecordCollectionTable.KEY_REPORT_RATE + ", " + EditRecordCollectionTable.KEY_REPORT_LDATE + ", "
                + EditRecordCollectionTable.KEY_REPORT_SOCIETYID + ", " + EditRecordCollectionTable.KEY_REPORT_BONUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_TIME + ", " + EditRecordCollectionTable.KEY_REPORT_TIME_MILLI + ", "
                + EditRecordCollectionTable.KEY_REPORT_AWM + ", " + EditRecordCollectionTable.KEY_REPORT_CLR + ", "
                + EditRecordCollectionTable.KEY_REPORT_STATUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_WEIGHTMANUAL + ", " + EditRecordCollectionTable.KEY_REPORT_MILKOMANUAL + ", "
                + EditRecordCollectionTable.KEY_REPORT_MILKANALYSERTIME + ", "
                + EditRecordCollectionTable.KEY_REPORT_WEIGHINGTIME + ", " + EditRecordCollectionTable.KEY_REPORT_TEMP + ", "
                + EditRecordCollectionTable.KEY_REPORT_COMMITED + ", " + EditRecordCollectionTable.KEY_REPORT_TYPE + ", "
                + EditRecordCollectionTable.KEY_MILKQUALITY + ", " + EditRecordCollectionTable.KEY_REPORT_RATEMODE + ", " + EditRecordCollectionTable.KEY_REPORT_NUMBER_OF_CANS + ", "
                + EditRecordCollectionTable.KEY_REPORT_SEQUENCE_NUMBER + ", " + EditRecordCollectionTable.KEY_REPORT_ROUTE + ", " + EditRecordCollectionTable.KEY_REPORT_RECORD_STATUS + ", "
                + EditRecordCollectionTable.KEY_REPORT_FOREIGN_SEQUENCE_ID + ", " + EditRecordCollectionTable.KEY_REPORT_EDITED_TIME + ", "
                + EditRecordCollectionTable.KEY_REPORT_OLD_OR_NEW_FLAG + ", " + EditRecordCollectionTable.KEY_TIPPING_START_TIME + ", "
                + EditRecordCollectionTable.KEY_TIPPING_END_TIME + ", " + EditRecordCollectionTable.KEY_REPORT_AGENT_ID + ", "
                + EditRecordCollectionTable.KEY_REPORT_MILKSTATUS_CODE + ", " + EditRecordCollectionTable.KEY_REPORT_RATE_CALC_DEVICE + ", "
                + EditRecordCollectionTable.KEY_REPORT_MA_SERIAL + ", " + EditRecordCollectionTable.KEY_REPORT_MA_NAME + ", "
                + EditRecordCollectionTable.KEY_REPORT_KG_QTY + ", " + EditRecordCollectionTable.KEY_REPORT_LTR_QTY
                + " ," + EditRecordCollectionTable.SEQUENCE_NUMBER + "," + EditRecordCollectionTable.SMS_SENT_STATUS
                + " ," + EditRecordCollectionTable.KEY_REPORT_SENT_STATUS
                + " FROM " + fromTable;
        return query;
    }

    public void alterReportTableWithModifiedTime() {

        String query = "ALTER " + "TABLE " + FarmerCollectionTable.TABLE_REPORTS + " ADD " + "COLUMN "
                + FarmerCollectionTable.LAST_MODIFIED + " INTEGER " + " DEFAULT " + 0;
        sqLiteDatabase.execSQL(query);

    }


    public void updateRateTables() {
        String tempRateTable = "tempRateTable";
        ArrayList<String> queries = new ArrayList<>();
        queries.add(" ALTER TABLE " + RateTable.TABLE_NAME + " RENAME TO " + tempRateTable);
        queries.add(RateTable.CREATE_QUERY);
        queries.add(RateTable.INSERT_QUERY_WITH_REF_ID);
        queries.add(" DROP TABLE " + tempRateTable);
//
//        queries.add(RateChartNameTable.ALTER_QUERY_FOR_RATE_SHIFT);


        for (String query : queries) {
            sqLiteDatabase.execSQL(query);
        }

        if (!toCheckTheColumnInTable(sqLiteDatabase,
                RateChartNameTable.TABLE_NAME, RateChartNameTable.MODIFIED_TIME)) {
            sqLiteDatabase.execSQL(RateChartNameTable.ALTER_QUERY_FOR_MODIFIED_TIME);
        }
    }

    public void alterRateChartNameTableWithShift() {
        ArrayList<String> queries = new ArrayList<>();
        queries.add(RateChartNameTable.ALTER_QUERY_FOR_RATE_SHIFT);
        try {
            for (String query : queries) {
                sqLiteDatabase.execSQL(query);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private boolean toCheckTheColumnInTable(SQLiteDatabase database,
                                            String table, String column) {
        boolean result = false;
        String query = "Pragma " + "table_info " + "('" + table + "')";

        Cursor cursor = database.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                if (cursor.getString(cursor.getColumnIndexOrThrow("name"))
                        .equalsIgnoreCase(column)) {
                    result = true;
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null)
            cursor.close();
        return result;
    }


    private void updateConfigurationTable(String key, String value) {

        try {
            String query =
                    "UPDATE CONFIGURATION_TABLE SET value ='" + value
                            + "' WHERE value IS NULL And key = '" + key + "'";
            sqLiteDatabase.execSQL(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
