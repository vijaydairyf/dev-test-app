package com.devapp.devmain.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.entity.SequenceCollectionRecord;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tableEntities.ExtraParams;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.tableEntities.SalesCollectionTable;
import com.devapp.devmain.tableEntities.SplitRecordTable;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.AdvanceUtil;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.report.MemberReportActivity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_AMOUNT;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_COLUMNID;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_COMMITED;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_FARMERID;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_LDATE;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_MILKTYPE;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_QUANTITY;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_RATE;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_RECORD_STATUS;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_ROUTE;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_STATUS;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_TIME_MILLI;
import static com.devapp.devmain.server.DatabaseHandler.KEY_REPORT_TYPE;
import static com.devapp.devmain.server.DatabaseHandler.TABLE_REPORTS;

/**
 * Created by u_pendra on 29/12/16.
 * This class is for get the Entity and content value, This is kind helper class
 * for DatabaseHandler
 */

public class DatabaseEntity {

    public static final int FARMER_UNSENT_CODE = 0;
    public static final int FARMER_SENT_CODE = 1;
    public static final String TABLE_CONFIGURATION = "CONFIGURATION_TABLE";
    public static final String TABLE_ESSAE_DATA = "TABLE_ESSAE_DATA";
    public static final String CONFIGURATION_ID = "_id";
    public static final String CONFIGURATION_KEY = "key";

    public static final String ESSAE_ID = "_id";
    public static final String ESSAE_QUERIED_TIME = "queriedTime";
    public static final String ESSAE_DATE = "date";
    public static final String ESSAE_TIME = "time";
    public static final String ESSAE_TYPE = "type";
    public static final String ESSAE_VALUE = "value";
    public static final String ESSAE_STATUS = "status";

    //Configuration Entity
    public static final String CONFIGURATION_VALUE = "value";
    public static final String CONFIGURATION_LAST_VALUE = "lastValue";
    public static final String CONFIGURATION_MODIFIED_TIME = "modifiedTime";
    public static final String CONFIGURATION_USER_NAME = "userName";
    //  public static final String CONFIGURATION_SOCIETY_ID = "societyId";
    public static final String CONFIGURATION_STATUS = "configStatus";
    public static final String TABLE_USER_HISTORY = "USER_HISTORY";
    public static final String COLUMN_ID = "_id";
    public static final String USER_ID = "userId";


    //
    public static final String USER_ROLE = "userRole";
    public static final String USER_LOG_IN_TIME = "loginTime";
    public static final String QUERY_UNSENT_CONFIGURATION = " SELECT * FROM "
            + TABLE_CONFIGURATION + " WHERE " + CONFIGURATION_STATUS + " = " + FARMER_UNSENT_CODE;

    public static final String TABLE_INCENTIVE_RATECHART = "IncentiveRateChart";
    AmcuConfig amcuConfig;
    SessionManager sessionManager;
    Context mContext;


    /**
     * @param context
     */
    public DatabaseEntity(Context context) {
        amcuConfig = AmcuConfig.getInstance();

        sessionManager = new SessionManager(context);
        mContext = context;
    }

    public static String getCreateQueryForEssaeTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_ESSAE_DATA
                + "( " + ESSAE_ID + " INTEGER , " +
                ESSAE_QUERIED_TIME + " INTEGER" + "," +
                ESSAE_DATE + " TEXT NOT NULL" + "," +
                ESSAE_TIME + " TEXT NOT NULL" + "," +
                ESSAE_TYPE + " TEXT NOT NULL" + "," +
                ESSAE_VALUE + " TEXT " + "," +
                ESSAE_STATUS + " INTEGER ," + " PRIMARY KEY(" + ESSAE_DATE + "," + ESSAE_TIME + "," + ESSAE_TYPE + "))";

    }

    public static String getCreateQueryForConfigurationTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_CONFIGURATION
                + "( " + CONFIGURATION_ID + " INTEGER PRIMARY KEY, " +
                CONFIGURATION_KEY + " TEXT UNIQUE NOT NULL" + "," +
                CONFIGURATION_VALUE + " TEXT " + "," +
                CONFIGURATION_LAST_VALUE + " TEXT " + "," +
                CONFIGURATION_MODIFIED_TIME + " INTEGER " + "," +
                CONFIGURATION_STATUS + " INTEGER " + "," +
                CONFIGURATION_USER_NAME + " TEXT " + ")";

    }

    public static String getCreateQueryForHistoryUser() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_USER_HISTORY
                + "( " + COLUMN_ID + " INTEGER PRIMARY KEY, " +
                USER_ID + " TEXT UNIQUE NOT NULL ," +
                USER_ROLE + " TEXT, " +
                USER_LOG_IN_TIME + " INTEGER" + ")";
    }

    public static String QueryToCheckTableExist(String tableName) {
        return " SELECT name FROM sqlite_master Where type = 'table' AND name ='" + tableName + "'";
    }

    public static String getQueryForSort(String query, int type, String order) {

        if (type == MemberReportActivity.ID) {
            query = query + " ORDER BY " + KEY_REPORT_FARMERID + " " + order;
        } else if (type == MemberReportActivity.QUANTITY) {
            query = query + " ORDER BY "
                    + " cast (" + KEY_REPORT_QUANTITY + " as float)" + " " + order;
        } else if (type == MemberReportActivity.RATE) {
            query = query + " ORDER BY "
                    + " cast (" + KEY_REPORT_RATE + " as float)" + " " + order;
        } else if (type == MemberReportActivity.AMOUNT) {
            query = query + " ORDER BY "
                    + " cast (" + KEY_REPORT_AMOUNT + " as float)" + " " + order;
        } else if (type == MemberReportActivity.TIME) {
            query = query + " ORDER BY "
                    + " cast (" + KEY_REPORT_TIME_MILLI + " as int)" + " " + order;
        }

        return query;

    }

    public static String createQueryToGetCenterFromRoutes(String[] routeList) {
        String query = "Select " + DatabaseHandler.KEY_CHILLING_CENTER_ID + " From " +
                DatabaseHandler.TABLE_CHILLING_CENTER;
        if (routeList.length > 0) {
            String routes = "'" + routeList[0].trim() + "'";
            if (!routes.equalsIgnoreCase("All")) {
                for (int i = 1; i < routeList.length; i++) {
                    if (routeList[i].trim().length() > 0 && !routeList[i].trim().equalsIgnoreCase("")) {
                        routes = routes + ",'" + routeList[i].trim() + "'";
                    }
                }
                query = query + " where " + DatabaseHandler.KEY_CHILLING_ROUTE + " IN (" + routes + ")";
            }
        }
        return query;
    }

    public static String getValidString(String key) {
        if (key == null || key.trim().equalsIgnoreCase("")) {
            return SmartCCConstants.SELECT_ALL;
        } else {
            return key;
        }
    }

    /**
     * To create a query for get validate the data by using given data
     *
     * @param tableName
     * @param key1
     * @param key2
     * @param validateData
     * @param givenData
     * @return
     */
    public static String validateDataFromOther(String tableName, String key1, String key2,
                                               String validateData, String givenData) {
        String returnValue = "Select count(*) from " + tableName + " where " + key1 + " = '"
                + validateData + "' AND " + key2 + " = '" + givenData + "'";

        return returnValue;

    }

    public static String getQueryForLatestRecord() {
        return "Select *, MAX(" + DatabaseHandler.KEY_REPORT_TIME_MILLI + ")"
                + " From " + DatabaseHandler.TABLE_REPORTS;
    }

    public static String getQueryToGetAgentIdForDateAndShift(Context context) {
        SmartCCUtil smartCCUtil = new SmartCCUtil(context);
        String currentDate = smartCCUtil.getReportFormatDate();
        String shift = Util.getCurrentShift();
        String query = "Select * from " + DatabaseHandler.TABLE_REPORTS + " Where "
                + FarmerCollectionTable.POST_DATE + " ='" + currentDate + "' AND " +
                FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'" + " Order by "
                + KEY_REPORT_FARMERID + " ASC ";

        return query;

    }

    public static String getFarmerQueryForBarcode(String barcode) {
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_FARMER + " WHERE "
                + DatabaseHandler.KEY_FARMER_BARCODE + "='" + barcode + "'";
        return selectQuery;
    }

    public static String getFarmerQueryForId(String id) {
        String selectQuery = "SELECT  * FROM " + DatabaseHandler.TABLE_FARMER + " WHERE "
                + DatabaseHandler.KEY_FARMER_ID + "='" + id + "'";
        return selectQuery;
    }

    public static String queryToGetUnsentSplitRecord() {
        String selectQuery = "Select * from " + SplitRecordTable.TABLE + " Where "
                + SplitRecordTable.SENT + " = " + DatabaseEntity.FARMER_UNSENT_CODE;
        return selectQuery;
    }

    public static String createSplitRecordTable() {

        String CREATE_REPORT_TABLE = "CREATE TABLE " + SplitRecordTable.TABLE + "("
                + SplitRecordTable.COLUMNID + " INTEGER PRIMARY KEY, "
                + SplitRecordTable.AGENT_ID + " TEXT," +
                SplitRecordTable.COLLECTION_TIME + " INTEGER,"
                + SplitRecordTable.COMMITED + " INTEGER," +
                SplitRecordTable.ENTRY_MODE + " TEXT,"
                + SplitRecordTable.FARMERID + " TEXT," +
                SplitRecordTable.MANUAL + " TEXT,"
                + SplitRecordTable.MILKTYPE + " TEXT," +
                SplitRecordTable.NUMBER_OF_CANS + " INTEGER," +
                SplitRecordTable.RECORD_STATUS + " TEXT,"
                + SplitRecordTable.SEQUENCE_NUMBER + " INTEGER," +
                SplitRecordTable.SHIFT + " TEXT,"
                + SplitRecordTable.SOCIETYID + " TEXT," +
                SplitRecordTable.TYPE + " TEXT," +
                SplitRecordTable.USER + " TEXT,"
                + SplitRecordTable.SENT + " INTEGER, " +
                SplitRecordTable.DATE + " TEXT, " +
                SplitRecordTable.PARENT_SEQ_NUM + " INTEGER, " +

                QualityParams.LACTOSE + " DOUBLE," +
                QualityParams.ADDED_WATER + " DOUBLE,"
                + QualityParams.ALCOHOL + " DOUBLE," +
                QualityParams.CALIBRATION + " TEXT,"
                + QualityParams.CLR + " DOUBLE," +
                QualityParams.COM + " TEXT," +
                QualityParams.DENSITY + " DOUBLE," +
                QualityParams.FAT + " DOUBLE,"
                + QualityParams.FREEZING_POINT + " DOUBLE,"
                + QualityParams.SALT + " DOUBLE," +
                QualityParams.SNF + " DOUBLE,"
                + QualityParams.MA_SERIAL_NUM + " TEXT," +
                QualityParams.TEMP + " DOUBLE,"
                + QualityParams.QUALITY_MODE + " TEXT,"
                + QualityParams.QUALITY_TIME + " INTEGER,"
                + QualityParams.QUALITY_START_TIME + " INTEGER," +
                QualityParams.QUALITY_END_TIME + " INTEGER,"
                + QualityParams.PH + " DOUBLE, "
                + QualityParams.PROTEIN + " DOUBLE, "
                + QualityParams.MILKQUALITY + " TEXT," +
                QualityParams.MILKSTATUS_CODE + " TEXT,"
                + QualityParams.MA_NAME + " TEXT,"
                + QualityParams.MA_NUMBER + " TEXT,"

                + QuantityParams.KG_QUANTITY + " DOUBLE, "
                + QuantityParams.LTR_QUANTITY + " DOUBLE,"
                + QuantityParams.MEASUREMENT_UNIT + " TEXT, "
                + QuantityParams.QUANTITY_MODE + " TEXT,"
                + QuantityParams.QUANTIY_TIME + " INTEGER, " +
                QuantityParams.TIPPING_START_TIME + " INTEGER,"
                + QuantityParams.TIPPING_END_TIME + " INTEGER, " +
                QuantityParams.WEIGHING_QUANTITY + " DOUBLE, "

                + RateParams.RATE_MODE + " TEXT, "
                + RateParams.AMOUNT + " DOUBLE, " +
                RateParams.BONUS + " DOUBLE, "
                + RateParams.INCENTIVE + " DOUBLE, " +
                RateParams.RATE + " DOUBLE, "
                + RateParams.RATE_CAL_FROM_DEVICE + " TEXT," +
                RateParams.RATE_CHART_NAME + " TEXT,"
                + RateParams.RATE_UNIT + " TEXT, "
                + SplitRecordTable.SMS_SENT_STATUS + " INTEGER, "
                + SplitRecordTable.POST_DATE + " VARCHAR(20), "
                + SplitRecordTable.POST_SHIFT + " VARCHAR(20) "
                + ")";

        return CREATE_REPORT_TABLE;
    }

    public static boolean getRateCalculated(int check) {
        boolean isRateCalculated = false;
        if (check == 0) {
            isRateCalculated = false;
        } else {
            isRateCalculated = true;
        }
        return isRateCalculated;
    }

    public static String getQueryForEditedFarmerList(Context context) {
        long millis = 0;
        try {
            millis = AdvanceUtil.getLongMilisFromDataAndShift(context);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String query = "Select * from " + TABLE_REPORTS + " where " +
                "cast (" + KEY_REPORT_TIME_MILLI + " as int) > " + millis
                + " AND " + KEY_REPORT_MILKTYPE + " !='" + CattleType.TEST + "'"
                + " AND " + KEY_REPORT_TYPE + " ='" + CollectionConstants.REPORT_TYPE_COLLECTION + "'";

        return query;
    }

    public static String getReportFromColId(long colId) {
        String query = "Select * from " + TABLE_REPORTS + " where "
                + KEY_REPORT_COLUMNID + " = " + colId;
        return query;

    }


    public static String getQueryForCollectionRecord(ArrayList<String> collectionId, long startTime, long endTime) {

        String query = "Select *  from " + DatabaseHandler.TABLE_REPORTS + " WHERE " +
                "cast (" + DatabaseHandler.KEY_REPORT_TIME_MILLI + " as int) >= "
                + startTime
                + " AND " + "cast (" + DatabaseHandler.KEY_REPORT_TIME_MILLI + " as int)"
                + " <= " + endTime;
//             if(collectionId != null || collectionId.size()>0)
//             {
//                 query = query + " AND "+DatabaseHandler.KEY_FARMER_ID +" ='"+collectionId+"'";
//             }
        return query;
    }

    public static String getQueryForExtraParams(String extraParamId, long startTime, long endTime) {
        String query = "Select *  from " + ExtraParams.TABLE_EXTRA_PARAMS + " WHERE "
                + ExtraParams.TIME + " >= " + startTime
                + " AND " + ExtraParams.TIME + " <= " + endTime;
//        if(extraParamId != null)
//        {
//            query = query + " AND "+CanDatabase.ExtraParams. +" ='"+extraParamId+"'";
//        }
        return query;
    }

    public static String getCreateQueryForBonusTable() {
        return "CREATE TABLE IF NOT EXISTS " + TABLE_INCENTIVE_RATECHART
                + "( " + InCentive.ID + " INTEGER PRIMARY KEY, " +
                InCentive.VALUE + " TEXT " + "," +
                InCentive.BONUS + " TEXT " + "," +
                InCentive.START_DATE + " TEXT " + "," +
                InCentive.END_DATE + " TEXT " + "," +
                InCentive.SOCIETY_ID + " TEXT " + "," +
                InCentive.RATE_CHART_NAME + " TEXT " + "," +
                InCentive.RATE_CHART_TYPE + " TEXT " + "," +
                InCentive.MILK_TYPE + " TEXT " + ")";

    }

    public static SalesRecordEntity getSalesReportFromCursor(Cursor cursor) {
        SalesRecordEntity salesRecordEntity = new SalesRecordEntity();

        salesRecordEntity.milkType = cursor.getString(12).toUpperCase(
                Locale.ENGLISH);

        salesRecordEntity.columnId = cursor.getLong(0);
        salesRecordEntity.status = cursor.getString(18);

        salesRecordEntity.milkoManual = cursor.getString(20);
        salesRecordEntity.weightManual = cursor.getString(19);
        salesRecordEntity.manual = cursor.getString(6);


        salesRecordEntity.fat = cursor.getDouble(4);
        salesRecordEntity.snf = cursor.getDouble(3);
        salesRecordEntity.Quantity = cursor.getDouble(9);
        salesRecordEntity.amount = cursor.getDouble(8);

        salesRecordEntity.rate = cursor.getDouble(13);
        salesRecordEntity.awm = cursor.getDouble(16);

        salesRecordEntity.temperature = cursor.getDouble(22);
        salesRecordEntity.clr = cursor.getDouble(17);

        salesRecordEntity.txnType = cursor.getString(23);
        salesRecordEntity.txnSubType = cursor.getString(24);

        salesRecordEntity.sentSmsStatus =
                cursor.getInt(cursor.getColumnIndex(SalesCollectionTable.SMS_SENT_STATUS));
        salesRecordEntity.sentStatus =
                cursor.getInt(cursor.getColumnIndex(SalesCollectionTable.KEY_SALES_SEND_STATUS));
        salesRecordEntity.postDate =
                cursor.getString(cursor.getColumnIndex(SalesCollectionTable.POST_DATE));
        salesRecordEntity.postShift =
                cursor.getString(cursor.getColumnIndex(SalesCollectionTable.POST_SHIFT));

        return salesRecordEntity;
    }

 /*   public static ReportEntity getReportFromCursor(Cursor cursor) {

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.columnId = cursor.getLong(0);
        reportEntity.farmerId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FARMERID));
        reportEntity.farmerName = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_NAME));
        reportEntity.snf = cursor.getDouble(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF));
        reportEntity.fat = cursor.getDouble(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT));
        reportEntity.user = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_USER));
        reportEntity.manual = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MANUAL));
        reportEntity.amount = cursor.getDouble(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT));
        reportEntity.quantity = cursor.getDouble(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY));
        reportEntity.txnNumber = cursor.getInt(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TAXNUM));
        reportEntity.milkType = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));
        reportEntity.rate = cursor.getDouble(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE));
        reportEntity.lDate = cursor.getLong(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LDATE));
        reportEntity.socId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SOCIETYID));

            reportEntity.bonus = cursor.getDouble(16);
        reportEntity.time = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME));
        reportEntity.miliTime = cursor.getLong(
                cursor.getColumnIndex(KEY_REPORT_TIME_MILLI));
        reportEntity.awm = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AWM));
        reportEntity.clr = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CLR));
        reportEntity.status = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_STATUS));
        reportEntity.quantityMode = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_WEIGHTMANUAL));
        reportEntity.qualityMode = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKOMANUAL));
        reportEntity.milkAnalyserTime = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKANALYSERTIME));
        reportEntity.weighingTime = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_WEIGHINGTIME));
        reportEntity.temperature = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TEMP));
        reportEntity.recordCommited = cursor.getInt(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_COMMITED));
        if (cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATECHART_NAME) != -1)
            reportEntity.rateChartName = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATECHART_NAME));

        if (cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE)) != null) {
            reportEntity.milkType = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE)).toUpperCase(Locale.ENGLISH);
        } else {
            reportEntity.milkType = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));
        }

        reportEntity.user = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_USER));
        reportEntity.reportType = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TYPE));


//        reportEntity.milkQuality=cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_MILKQUALITY));
//        reportEntity.rateMode=cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATEMODE));
//        reportEntity.numberOfCans=cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS));
//        reportEntity.sequenceNumber=cursor.getInt(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER));
//        reportEntity.centerRoute=cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_ROUTE));
//        reportEntity.recordStatus=cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RECORD_STATUS));
//        reportEntity.kgWeight=cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_KG_QTY));
//        reportEntity.ltrsWeight=cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LTR_QTY));
//        reportEntity.tippingStartTime = cursor.getLong(
//                cursor.getColumnIndex(DatabaseHandler.KEY_TIPPING_START_TIME));
//        reportEntity.tippingEndTime = cursor.getLong(
//                cursor.getColumnIndex(DatabaseHandler.KEY_TIPPING_END_TIME));
//        reportEntity.agentId = cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AGENT_ID));
//        reportEntity.milkStatusCode = cursor.getInt(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKSTATUS_CODE));
//        reportEntity.isRateCalculated = cursor.getInt(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE_CALC_DEVICE));
//        reportEntity.serialMa = cursor.getInt(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_SERIAL));
//        reportEntity.maName = cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_NAME));


        reportEntity.lactose = cursor.getDouble(cursor.getColumnIndex(
                DatabaseHandler.KEY_REPORT_LACTOSE));
//        reportEntity.salt = cursor.getDouble(cursor.getColumnIndex(
//                DatabaseHandler.KEY_REPORT_SALT));
//        reportEntity.protein = cursor.getDouble(cursor.getColumnIndex(
//                DatabaseHandler.KEY_REPORT_PROTEIN));
//        reportEntity.pH = cursor.getDouble(cursor.getColumnIndex(
//                DatabaseHandler.KEY_REPORT_PH));
//        reportEntity.conductivity = cursor.getDouble(cursor.getColumnIndex(
//                DatabaseHandler.KEY_REPORT_CONDUTIVITY));
//        reportEntity.freezingPoint = cursor.getDouble(cursor.getColumnIndex(
        //     DatabaseHandler.KEY_REPORT_FREEZING_POINT));
        reportEntity.density = cursor.getDouble(cursor.getColumnIndex(
                DatabaseHandler.KEY_REPORT_DENSITY));
        reportEntity.calibration = cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_REPORT_CALIBRATION));
        reportEntity.maSerialNumber = cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_REPORT_MA_SERIAL_NUMBER));

        return reportEntity;

    }*/

    /**
     * @param cursor
     * @return
     */


    public FarmerEntity getFarmerEntity(Cursor cursor)

    {
        FarmerEntity farmerEntity = new FarmerEntity();

        farmerEntity.assignRatechart = cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_FARMER_ASSIGN_RATECHART));
        farmerEntity.farm_email = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_EMAIL));
        farmerEntity.farm_mob = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_MOBILE));
        farmerEntity.farmer_barcode = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_BARCODE));
        farmerEntity.farmer_cans = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_CANS));
        farmerEntity.farmer_cattle = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_CATTLE));
        farmerEntity.farmer_id = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_ID));
        farmerEntity.farmer_name = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_NAME));
        farmerEntity.farmer_regDate = cursor.getLong(cursor.getColumnIndex(
                DatabaseHandler.KEY_FARMER_REGISTRATION_DATE));
        farmerEntity.isActive = true;
        farmerEntity.isMultipleCans = Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_ENABLE_MULTIPLECANS)));
        farmerEntity.numBuff = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_BUFFALO));
        farmerEntity.numCattle = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_NO_CATTLE));
        farmerEntity.numCow = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_COW));
        farmerEntity.soc_code = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_SOCCODE));
        farmerEntity.sentStatus = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_SENT));
        farmerEntity.agentId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_AGENT_ID));
        farmerEntity.farmerType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_FARMER_TYPE));

        return farmerEntity;

    }

    /**
     * To get the center entity data from database cursor
     *
     * @param cursor
     * @return
     */

    public CenterEntity getCenterEntity(Cursor cursor)

    {

        CenterEntity centerEntity = new CenterEntity();
        centerEntity.centerName = cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_CHILLING_CENTER_NAME));
        centerEntity.centerRoute = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_ROUTE));
        centerEntity.cattleType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_MILK_TYPE));
        centerEntity.centerBarcode = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_BARCODE));
        centerEntity.centerId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CENTER_ID));
        centerEntity.chillingCenterId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_ID));
        centerEntity.contactEmailId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CONTACT_EMAILID));
        centerEntity.contactPerson = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CONTACT_PERSON));
        centerEntity.registerDate = cursor.getLong(cursor.getColumnIndex(
                DatabaseHandler.KEY_CHILLING_REG_DATE));
        centerEntity.contactNumber = cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_CHILLING_CONTACT_NUMBER));
        centerEntity.singleOrMultiple = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_SINGLE_MULTIPLE));
        centerEntity.sentStatus = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CENTER_SENT));
        centerEntity.activeStatus = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_IS_ACTIVE));
        return centerEntity;

    }

    /**
     * To get sample data entity from database cursor
     *
     * @param cursor
     * @return
     */

    public SampleDataEntity getSampleEntity(Cursor cursor)

    {
        SampleDataEntity sampleEntity = new SampleDataEntity();


        sampleEntity.sampleBarcode = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_BARCODE));

        sampleEntity.sampleId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_ID));
        sampleEntity.sampleIsSnf = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_IS_SNF));
        sampleEntity.sampleIsFat = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_IS_FAT));
        sampleEntity.sampleIsWeigh = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_IS_WEIGH));

        sampleEntity.sampleMode = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_MODE));
        sampleEntity.sampleFat = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_FAT));
        sampleEntity.sampleSnf = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_SNF));
        sampleEntity.sampleSocId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_SOC_ID));
        sampleEntity.sampleRate = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_RATE));
        sampleEntity.sampleAmount = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_AMOUNT));
        sampleEntity.sentStatus = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_SAMPLE_SENT));

        return sampleEntity;

    }

    /**
     * To get user entity data from database cursor
     *
     * @param cursor
     * @return
     */

    public UserEntity getUserEntity(Cursor cursor)

    {
        UserEntity userEntity = new UserEntity();


        userEntity.emailId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_EMAIL));
        userEntity.mobile = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_MOBILE));
        userEntity.monthDate = cursor.getLong(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_MONTHDATE));
        userEntity.name = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_NAME));
        userEntity.password = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_PASSWORD));

        userEntity.centerId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_SOCIETYID));
        userEntity.userId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_USERID));
        userEntity.regDate = cursor.getLong(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_REGDATE));
        userEntity.role = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_ROLE));
        userEntity.weekDate = cursor.getLong(
                cursor.getColumnIndex(DatabaseHandler.KEY_USER_WEEKDATE));
        userEntity.sentStatus = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_USER_SENT));
        return userEntity;

    }

    /**
     * To get content values from the farmer data entity
     *
     * @param farmEntity
     * @return
     */

    public ContentValues getFarmerContentValue(FarmerEntity farmEntity) {

        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_FARMER_NAME, farmEntity.farmer_name);
        values.put(DatabaseHandler.KEY_FARMER_CANS, farmEntity.farmer_cans);
        values.put(DatabaseHandler.KEY_FARMER_SOCCODE, String.valueOf(
                sessionManager.getSocietyColumnId()));
        values.put(DatabaseHandler.KEY_FARMER_MOBILE, farmEntity.farm_mob);
        values.put(DatabaseHandler.KEY_FARMER_EMAIL, farmEntity.farm_email);
        values.put(DatabaseHandler.KEY_FARMER_CATTLE, farmEntity.farmer_cattle.toUpperCase(Locale.ENGLISH));

        values.put(DatabaseHandler.KEY_FARMER_COW, farmEntity.numCow);
        values.put(DatabaseHandler.KEY_FARMER_BUFFALO, farmEntity.numBuff);
        values.put(DatabaseHandler.KEY_FARMER_NO_CATTLE, farmEntity.numCattle);
        values.put(DatabaseHandler.KEY_FARMER_ASSIGN_RATECHART, farmEntity.assignRatechart);

        values.put(DatabaseHandler.KEY_FARMER_REGISTRATION_DATE, farmEntity.farmer_regDate);
        values.put(DatabaseHandler.KEY_ENABLE_MULTIPLECANS, String.valueOf(farmEntity.isMultipleCans));

        values.put(DatabaseHandler.KEY_FARMER_BARCODE, farmEntity.farmer_barcode);
        values.put(DatabaseHandler.KEY_FARMER_ID, farmEntity.farmer_id);
        values.put(DatabaseHandler.KEY_FARMER_SENT, farmEntity.sentStatus);
        values.put(DatabaseHandler.KEY_AGENT_ID, farmEntity.agentId);
        values.put(DatabaseHandler.KEY_FARMER_TYPE, farmEntity.farmerType);

        return values;

    }

    /**
     * To get content values from Milk collection Center entity
     *
     * @param centerEntity
     * @return
     */

    public ContentValues getContentValueForChillingCenter(CenterEntity centerEntity) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_CHILLING_ID,
                String.valueOf(sessionManager.getSocietyColumnId()));
        values.put(DatabaseHandler.KEY_CHILLING_CONTACT_PERSON, centerEntity.contactPerson);
        if (centerEntity.centerName != null) {
            values.put(DatabaseHandler.KEY_CHILLING_CENTER_NAME,
                    centerEntity.centerName.toUpperCase(Locale.ENGLISH));
        } else {
            values.put(DatabaseHandler.KEY_CHILLING_CENTER_NAME, centerEntity.centerName);
        }
        values.put(DatabaseHandler.KEY_CHILLING_CONTACT_NUMBER, centerEntity.contactNumber);
        values.put(DatabaseHandler.KEY_CHILLING_CONTACT_EMAILID, centerEntity.contactEmailId);
        values.put(DatabaseHandler.KEY_CHILLING_REG_DATE, centerEntity.registerDate);
        if (centerEntity.centerRoute != null) {
            values.put(DatabaseHandler.KEY_CHILLING_ROUTE,
                    centerEntity.centerRoute.toUpperCase(Locale.ENGLISH));
        } else {
            values.put(DatabaseHandler.KEY_CHILLING_ROUTE, "NA");
        }

        values.put(DatabaseHandler.KEY_CHILLING_SINGLE_MULTIPLE, centerEntity.singleOrMultiple);
        values.put(DatabaseHandler.KEY_CHILLING_MILK_TYPE, centerEntity.cattleType);
        values.put(DatabaseHandler.KEY_CHILLING_BARCODE, centerEntity.centerBarcode);
        values.put(DatabaseHandler.KEY_CHILLING_CENTER_ID,
                centerEntity.centerId.toUpperCase(Locale.ENGLISH));
        values.put(DatabaseHandler.KEY_CHILLING_CENTER_SENT, centerEntity.sentStatus);
        values.put(DatabaseHandler.KEY_CHILLING_IS_ACTIVE, centerEntity.activeStatus);

        return values;

    }


//    public AverageReportDetail getDLDataForFarmer(Cursor cursor,String farmerId)
//    {
//
//        AverageReportDetail averageReportDetail = new AverageReportDetail();
//        CheckValidation checkValidation = new CheckValidation();
//        ArrayList<String> allFarmer = new ArrayList<>();
//        ArrayList<Integer> allPostion = new ArrayList<>();
//
//        averageReportDetail.minAmount =-1;
//        averageReportDetail.minFat =-1;
//        averageReportDetail.minSnf =-1;
//        averageReportDetail.minRate =-1;
//        averageReportDetail.minQuantity =-1;
//        averageReportDetail.minCom =-1;
//
//
//        do {
//
//            if ( averageReportDetail.minAmount==-1 ||(averageReportDetail.minAmount >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), "0.0")))) {
//                averageReportDetail.minAmount = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), "0.0"));
//            }
//
//            if (averageReportDetail.maxAmount <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), "0.0"))) {
//                averageReportDetail.maxAmount = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), "0.0"));
//            }
//
//            if (averageReportDetail.minFat ==-1 || (averageReportDetail.minFat >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), "0.0")))) {
//                averageReportDetail.minFat = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), "0.0"));
//            }
//
//            if (averageReportDetail.maxFat <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), "0.0"))) {
//                averageReportDetail.maxFat = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), "0.0"));
//            }
//
//            if (averageReportDetail.minSnf == -1 ||(averageReportDetail.minSnf >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), "0.0")))) {
//                averageReportDetail.minSnf = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), "0.0"));
//            }
//
//            if (averageReportDetail.maxSnf <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), "0.0"))) {
//                averageReportDetail.maxSnf = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), "0.0"));
//            }
//
//
//            if (averageReportDetail.minCom == -1 ||(averageReportDetail.minCom >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), "0.0")))) {
//                averageReportDetail.minCom = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), "0.0"));
//            }
//
//            if (averageReportDetail.maxCom <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), "0.0"))) {
//                averageReportDetail.minCom = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), "0.0"));
//            }
//
//
//
//            if (averageReportDetail.minRate ==-1 ||(averageReportDetail.minRate >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), "0.0")))) {
//                averageReportDetail.minRate = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), "0.0"));
//            }
//
//            if (averageReportDetail.maxRate <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), "0.0"))) {
//                averageReportDetail.maxRate = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), "0.0"));
//            }
//
//            if (averageReportDetail.minQuantity == -1 ||(averageReportDetail.minQuantity >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), "0.0")))) {
//                averageReportDetail.minQuantity = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), "0.0"));
//            }
//
//            if (averageReportDetail.maxQuanity <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), "0.0"))) {
//                averageReportDetail.maxQuanity = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), "0.0"));
//            }
//
//            if (getIntTime(averageReportDetail.minTime) >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME)), "0.0"))) {
//                averageReportDetail.minTime = cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME));
//            }
//
//            if (getIntTime(averageReportDetail.maxTime) <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME)), "0.0"))) {
//                averageReportDetail.maxTime = cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME));
//            }
//
//            Cursor localCursor;
//            localCursor = DatabaseHandler.getDatabaseInstance(mContext).getCursor(
//                    queryToNearestAheadFarmer(
//                            cursor.getLong(cursor.getColumnIndex
//                                    (DatabaseHandler.KEY_REPORT_TIME_MILLI)),
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_SHIFT))));
//
//            if(localCursor!=null && localCursor.moveToFirst())
//            {
//                allFarmer.add(localCursor.getString(0));
//            }
//
//
//            localCursor = DatabaseHandler.getDatabaseInstance(mContext).getCursor(
//                    queryTogetNearestLastFarmer(
//                            cursor.getLong(cursor.getColumnIndex
//                                    (DatabaseHandler.KEY_REPORT_TIME_MILLI)),
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_SHIFT))));
//
//            if(localCursor!=null && localCursor.moveToFirst())
//            {
//                allFarmer.add(localCursor.getString(0));
//            }
//
//            int startPos=0,farmerPos=0;
//
//            localCursor = DatabaseHandler.getDatabaseInstance(mContext).getCursor(
//                    queryToGetTheStartPosition(
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.KEY_REPORT_DATE)),
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_SHIFT))));
//
//            if(localCursor!=null && localCursor.moveToFirst())
//            {
//                startPos = localCursor.getInt(0);
//            }
//
//            localCursor = DatabaseHandler.getDatabaseInstance(mContext).getCursor(
//                    queryToGetThePositionFarmer(
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.KEY_REPORT_DATE)),
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_SHIFT)),
//                            cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FARMERID))));
//
//            if(localCursor!=null && localCursor.moveToFirst())
//            {
//                farmerPos = localCursor.getInt(0);
//            }
//
//            allPostion.add((farmerPos-startPos));
//            averageReportDetail.totalMember = averageReportDetail.totalMember+1;
//        }
//        while(cursor.moveToNext());
//
//
//        Cursor localCursor;
//
//        int activeCount=0;
//        int farmerCount=0;
//
//        localCursor = DatabaseHandler.getDatabaseInstance(
//                mContext).getCursor(queryForTotalActiveShift());
//
//        if(localCursor!=null && localCursor.moveToFirst())
//        {
//            activeCount = localCursor.getInt(0);
//        }
//
//        localCursor = DatabaseHandler.getDatabaseInstance(
//                mContext).getCursor(queryForTotalActiveShiftForFarmer(farmerId));
//
//        if(localCursor!=null && localCursor.moveToFirst())
//        {
//            farmerCount = localCursor.getInt(0);
//        }
//
//        averageReportDetail.numberOfTimeBunked = activeCount-farmerCount;
//        averageReportDetail.neighbourFarmer = allFarmer.get(0);
//        averageReportDetail.maxEntryRank = allPostion.get(0);
//        averageReportDetail.minEntryRank = allPostion.get(allPostion.size()-1);
//        return averageReportDetail;
//    }

    /**
     * To get the content values from the user entity
     *
     * @param userEntity
     * @return
     */

    public ContentValues getUserContentValues(UserEntity userEntity) {

        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_USER_EMAIL, userEntity.emailId);
        values.put(DatabaseHandler.KEY_USER_MOBILE, userEntity.mobile);
        values.put(DatabaseHandler.KEY_USER_NAME, userEntity.name);
        values.put(DatabaseHandler.KEY_USER_PASSWORD, userEntity.password);
        values.put(DatabaseHandler.KEY_USER_SOCIETYID, userEntity.centerId);
        values.put(DatabaseHandler.KEY_USER_ROLE, userEntity.role);
        values.put(DatabaseHandler.KEY_USER_REGDATE, userEntity.regDate);
        values.put(DatabaseHandler.KEY_USER_WEEKDATE, userEntity.weekDate);
        values.put(DatabaseHandler.KEY_USER_MONTHDATE, userEntity.monthDate);
        values.put(DatabaseHandler.KEY_USER_USERID, userEntity.userId);
        values.put(DatabaseHandler.KEY_USER_SENT, userEntity.sentStatus);

        return values;


    }

    public ContentValues getSampleContentValue(SampleDataEntity sampleEntity)

    {
        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_SAMPLE_BARCODE, sampleEntity.sampleBarcode);
        values.put(DatabaseHandler.KEY_SAMPLE_ID, sampleEntity.sampleId);
        values.put(DatabaseHandler.KEY_SAMPLE_IS_SNF, sampleEntity.sampleIsSnf);
        values.put(DatabaseHandler.KEY_SAMPLE_IS_FAT, sampleEntity.sampleIsFat);
        values.put(DatabaseHandler.KEY_SAMPLE_IS_WEIGH, sampleEntity.sampleIsWeigh);
        values.put(DatabaseHandler.KEY_SAMPLE_MODE, sampleEntity.sampleMode);
        values.put(DatabaseHandler.KEY_SAMPLE_FAT, sampleEntity.sampleFat);
        values.put(DatabaseHandler.KEY_SAMPLE_SNF, sampleEntity.sampleSnf);
        values.put(DatabaseHandler.KEY_SAMPLE_SOC_ID, sampleEntity.sampleSocId);
        values.put(DatabaseHandler.KEY_SAMPLE_RATE, sampleEntity.sampleRate);
        values.put(DatabaseHandler.KEY_SAMPLE_AMOUNT, sampleEntity.sampleAmount);
        values.put(DatabaseHandler.KEY_SAMPLE_SENT, sampleEntity.sentStatus);


        return values;

    }

    /**
     * @param type
     * @param milkType
     * @return
     */

    public String getShiftQueryForAverageReport(String type, String milkType) {
        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);

        String date = smartCCUtil.getReportDate(0);
        String shift = Util.getCurrentShift();

        String common = FarmerCollectionTable.POST_DATE + " ='" + date + "' AND "
                + FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'"
                + "AND " + KEY_REPORT_COMMITED + "=" + 1;

        if (type == null && milkType == null) {
            return "Select * from " + TABLE_REPORTS + " Where "
                    + common;
        } else if (type != null && milkType == null) {
            return "Select * from " + TABLE_REPORTS + " Where "
                    + KEY_REPORT_TYPE + " ='" + type + "' AND "
                    + common;
        } else if (milkType != null && type == null) {
            return "Select * from " + TABLE_REPORTS + " Where "
                    + KEY_REPORT_MILKTYPE + " ='" + milkType + "' AND "
                    + common;
        } else {
            return "Select * from " + TABLE_REPORTS + " Where "
                    + KEY_REPORT_TYPE + " ='" + type + "' AND "
                    + KEY_REPORT_MILKTYPE + " ='" + milkType + "' AND "
                    + common;

        }

    }

    public AverageReportDetail getAverageReportDetails(Cursor cursor, ArrayList<ReportEntity> allReport) {

        AverageReportDetail avgReportDetail = new AverageReportDetail();

        int totalCount = 0, totalAccept = 0, totalReject = 0, totalSent = 0, totalUnsent = 0,
                totalFarmer = 0, totalCenter = 0, totalSample = 0, totalCans = 0;
        double totalAmount = 0, totalMilk = 0, avgRate = 0, avgFat = 0, avgSnf = 0, avgMilk = 0, avgAmount = 0, totalKgFat = 0, totalKgSnf = 0, fatKg = 0.0, snfKg = 0.0;

        if ((cursor != null && cursor.moveToFirst()) || (
                allReport != null && allReport.size() > 0
        )) {
            int count = 0;
            do {
                boolean isReject = false;
                totalCount = totalCount + 1;

                if (cursor != null) {
                    if (cursor != null && cursor.getString(cursor.
                            getColumnIndex(KEY_REPORT_STATUS))
                            .equalsIgnoreCase("Reject")) {
                        totalReject = totalReject + 1;
                        isReject = true;
                    } else if (cursor.getString(cursor.
                            getColumnIndex(KEY_REPORT_STATUS)).equalsIgnoreCase("Accept")) {
                        totalAccept = totalAccept + 1;
                    }

                    boolean isSample = false;
                    if (cursor.getString(cursor.getColumnIndex(KEY_REPORT_MILKTYPE))
                            .equalsIgnoreCase("Test")) {
                        totalSample = totalSample + 1;
                        isSample = true;
                    } else if (cursor.getString(cursor.getColumnIndex(KEY_REPORT_TYPE))
                            .equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
                        totalCenter = totalCenter + 1;
                    } else {
                        totalFarmer = totalFarmer + 1;
                    }
                    if (cursor.getString(56) == null || cursor.getString(56).equalsIgnoreCase("null")
                            || cursor.getString(56).equalsIgnoreCase("")) {

                        fatKg = 0.0;
                    } else {
                        fatKg = Double.parseDouble(cursor.getString(56));
                    }

                    if (cursor.getString(57) == null || cursor.getString(57).equalsIgnoreCase("null")
                            || cursor.getString(57).equalsIgnoreCase("")) {

                        snfKg = 0.0;
                    } else {
                        snfKg = Double.parseDouble(cursor.getString(57));
                    }
                    totalKgFat = totalKgFat + fatKg;
                    totalKgSnf = totalKgSnf + snfKg;

                    // Do the average calculation only if milk status is Accepted
                    if (!isReject && !isSample) {
                        if (amcuConfig.getKeyAllowProteinValue()) {
                            totalAmount = totalAmount +
                                    Double.valueOf(Util.getAmount(mContext, cursor.getDouble(
                                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)),
                                            cursor.getDouble(
                                                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_BONUS))));

                        } else {
                            totalAmount = totalAmount +
                                    Double.valueOf(Util.getAmount(mContext, cursor.getDouble(
                                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)),
                                            cursor.getDouble(
                                                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_BONUS))));
//
//                                    Double.parseDouble(cursor.getString(
//                                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)));
                        }
                        totalMilk = totalMilk +
                                Double.parseDouble(cursor.getString(
                                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)));

                        double fat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(
                                DatabaseHandler.KEY_REPORT_FAT)));
                        double snf = Double.parseDouble(cursor.getString(cursor.getColumnIndex(
                                DatabaseHandler.KEY_REPORT_SNF)));
                        double quantity = Double.parseDouble(cursor.getString(
                                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)));

                        double val1 = (fat * quantity) / 100;
                        double val2 = (quantity * snf) / 100;

                        avgFat = avgFat + val1;
                        avgSnf = avgSnf + val2;

                        int cans = cursor.getInt(cursor.getColumnIndex(KEY_REPORT_NUMBER_OF_CANS));
                        if (cans <= 1000) {
                            totalCans = totalCans + cans;
                        }
                    }
                } else if (allReport != null) {
                    boolean isSample = false;

                    if (allReport.get(count).status.equalsIgnoreCase("Reject")) {
                        totalReject = totalReject + 1;
                        isReject = true;
                    } else if (allReport.get(count).status.equalsIgnoreCase("Accept")) {
                        totalAccept = totalAccept + 1;
                    }

                    if (allReport.get(count).milkType.equalsIgnoreCase("Test")) {
                        totalSample = totalSample + 1;
                        isSample = true;
                    } else if (allReport.get(count).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
                        totalCenter = totalCenter + 1;
                    } else {
                        totalFarmer = totalFarmer + 1;
                    }

                    if (!isSample) {
                        totalKgFat = totalKgFat + allReport.get(count).fatKg;
                        totalKgSnf = totalKgSnf + allReport.get(count).snfKg;
                    }
                    if (!isReject && !isSample) {
                        totalAmount = totalAmount +
                                Double.valueOf(Util.getAmount(mContext, allReport.get(count).getTotalAmount(),
                                        allReport.get(count).bonus));
                        totalMilk = totalMilk + allReport.get(count).quantity;
                        double fat = 0.0;
                        double snf = 0.0;
                        fat = allReport.get(count).fat;
                        snf = allReport.get(count).snf;
                        double quantity = allReport.get(count).quantity;

                        double val1 = (fat * quantity) / 100;
                        double val2 = (quantity * snf) / 100;

                        avgFat = avgFat + val1;
                        avgSnf = avgSnf + val2;

                        int cans = allReport.get(count).numberOfCans;
                        if (cans <= 1000) {
                            totalCans = totalCans + cans;
                        }
                    }

                    count++;
                }

            }
            while ((cursor != null && cursor.moveToNext())
                    || (allReport != null && count < allReport.size()));

        }

        DecimalFormat decimalFormat = null;
        decimalFormat = new DecimalFormat("#0.00");
        try {
            if (totalAccept > 0) {
                avgFat = avgFat * 100 / totalMilk;
                avgSnf = avgSnf * 100 / totalMilk;
                avgAmount = totalAmount / (totalAccept);
                avgRate = totalAmount / totalMilk;
                avgMilk = totalMilk / (totalAccept);
            } else {
                avgFat = 0.0;
                avgSnf = 0.0;
                avgAmount = 0.0;
                avgMilk = 0.0;
                avgRate = 0;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        avgReportDetail.avgFat = Double.valueOf(decimalFormat.format(avgFat));
        avgReportDetail.avgSnf = Double.valueOf(decimalFormat.format(avgSnf));
        avgReportDetail.totalAcceptedEntries = totalAccept;
        avgReportDetail.totalQuantity = Double.valueOf(decimalFormat.format(totalMilk));
        avgReportDetail.avgAmount = Double.valueOf(decimalFormat.format(avgAmount));
        avgReportDetail.totalAmount = Double.valueOf(decimalFormat.format(totalAmount));
        avgReportDetail.avgRate = Double.valueOf(decimalFormat.format(avgRate));
        avgReportDetail.avgQuantity = Double.valueOf(decimalFormat.format(avgMilk));
        avgReportDetail.totalRejectedEntries = totalReject;
        avgReportDetail.totalTestEntries = totalSample;
        avgReportDetail.totalMember = totalCount;
        avgReportDetail.totalFarmer = totalFarmer;
        avgReportDetail.totalCenter = totalCenter;
        avgReportDetail.totalCans = totalCans;
        avgReportDetail.totalSnfKg = Double.valueOf(decimalFormat.format(totalKgSnf));
        avgReportDetail.totalFatKg = Double.valueOf(decimalFormat.format(totalKgFat));

        return avgReportDetail;
    }

    public String getQueryToGetMemberList(String type) {
        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);

        String date = smartCCUtil.getReportDate(0);
        String shift = Util.getCurrentShift();

        String common = FarmerCollectionTable.POST_DATE + " ='" + date + "' AND "
                + FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'"
                + "AND " + KEY_REPORT_COMMITED + "=" + 1;

        if (type == null) {
            return "Select " + DatabaseHandler.KEY_REPORT_FARMERID + " from " + TABLE_REPORTS + " Where "
                    + common;
        } else {
            return "Select " + DatabaseHandler.KEY_REPORT_FARMERID + " from " + TABLE_REPORTS + " Where "
                    + KEY_REPORT_TYPE + " ='" + type + "' AND "
                    + common;
        }
    }

    public String getQueryForMemberReport(String id) {
        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);

        String date = smartCCUtil.getReportDate(0);
        String shift = Util.getCurrentShift();

        String common = FarmerCollectionTable.POST_DATE + " ='" + date + "' AND "
                + FarmerCollectionTable.POST_SHIFT + " ='" + shift + "'"
                + "AND " + KEY_REPORT_COMMITED + "=" + 1;

        return "Select * from " + TABLE_REPORTS + " Where "
                + common + " AND " + DatabaseHandler.KEY_REPORT_FARMERID + "='" + id + "'";

    }

    public String getQueryForFarmerWithRecordStatus(int status) {
        return "Select * from " + DatabaseHandler.TABLE_FARMER + " Where "
                + DatabaseHandler.KEY_FARMER_SENT + " = " + status;
    }


//    public AverageReportDetail getDLDataForFarmer(Cursor cursor,String farmerId)
//    {
//
//        AverageReportDetail averageReportDetail = new AverageReportDetail();
//        CheckValidation checkValidation = new CheckValidation();
//        ArrayList<String> allFarmer = new ArrayList<>();
//        ArrayList<Integer> allPostion = new ArrayList<>();
//
//        averageReportDetail.minAmount =-1;
//        averageReportDetail.minFat =-1;
//        averageReportDetail.minSnf =-1;
//        averageReportDetail.minRate =-1;
//        averageReportDetail.minQuantity =-1;
//        averageReportDetail.minCom =-1;
//
//
//        do {
//
//            if ( averageReportDetail.minAmount==-1 ||(averageReportDetail.minAmount >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), "0.0")))) {
//                averageReportDetail.minAmount = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), "0.0"));
//            }
//
//            if (averageReportDetail.maxAmount <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), "0.0"))) {
//                averageReportDetail.maxAmount = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), "0.0"));
//            }
//
//            if (averageReportDetail.minFat ==-1 || (averageReportDetail.minFat >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), "0.0")))) {
//                averageReportDetail.minFat = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), "0.0"));
//            }
//
//            if (averageReportDetail.maxFat <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), "0.0"))) {
//                averageReportDetail.maxFat = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), "0.0"));
//            }
//
//            if (averageReportDetail.minSnf == -1 ||(averageReportDetail.minSnf >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), "0.0")))) {
//                averageReportDetail.minSnf = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), "0.0"));
//            }
//
//            if (averageReportDetail.maxSnf <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), "0.0"))) {
//                averageReportDetail.maxSnf = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), "0.0"));
//            }
//
//
//            if (averageReportDetail.minCom == -1 ||(averageReportDetail.minCom >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), "0.0")))) {
//                averageReportDetail.minCom = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), "0.0"));
//            }
//
//            if (averageReportDetail.maxCom <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), "0.0"))) {
//                averageReportDetail.minCom = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), "0.0"));
//            }
//
//
//
//            if (averageReportDetail.minRate ==-1 ||(averageReportDetail.minRate >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), "0.0")))) {
//                averageReportDetail.minRate = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), "0.0"));
//            }
//
//            if (averageReportDetail.maxRate <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), "0.0"))) {
//                averageReportDetail.maxRate = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), "0.0"));
//            }
//
//            if (averageReportDetail.minQuantity == -1 ||(averageReportDetail.minQuantity >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), "0.0")))) {
//                averageReportDetail.minQuantity = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), "0.0"));
//            }
//
//            if (averageReportDetail.maxQuanity <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), "0.0"))) {
//                averageReportDetail.maxQuanity = Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), "0.0"));
//            }
//
//            if (getIntTime(averageReportDetail.minTime) >=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME)), "0.0"))) {
//                averageReportDetail.minTime = cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME));
//            }
//
//            if (getIntTime(averageReportDetail.maxTime) <=
//                    Double.parseDouble(checkValidation.getValidateDouble(null, cursor.getString(
//                            cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME)), "0.0"))) {
//                averageReportDetail.maxTime = cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME));
//            }
//
//            Cursor localCursor;
//            localCursor = DatabaseHandler.getDatabaseInstance(mContext).getCursor(
//                    queryToNearestAheadFarmer(
//                            cursor.getLong(cursor.getColumnIndex
//                                    (DatabaseHandler.KEY_REPORT_TIME_MILLI)),
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_SHIFT))));
//
//            if(localCursor!=null && localCursor.moveToFirst())
//            {
//                allFarmer.add(localCursor.getString(0));
//            }
//
//
//            localCursor = DatabaseHandler.getDatabaseInstance(mContext).getCursor(
//                    queryTogetNearestLastFarmer(
//                            cursor.getLong(cursor.getColumnIndex
//                                    (DatabaseHandler.KEY_REPORT_TIME_MILLI)),
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_SHIFT))));
//
//            if(localCursor!=null && localCursor.moveToFirst())
//            {
//                allFarmer.add(localCursor.getString(0));
//            }
//
//            int startPos=0,farmerPos=0;
//
//            localCursor = DatabaseHandler.getDatabaseInstance(mContext).getCursor(
//                    queryToGetTheStartPosition(
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_DATE)),
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_SHIFT))));
//
//            if(localCursor!=null && localCursor.moveToFirst())
//            {
//                startPos = localCursor.getInt(0);
//            }
//
//            localCursor = DatabaseHandler.getDatabaseInstance(mContext).getCursor(
//                    queryToGetThePositionFarmer(
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_DATE)),
//                            cursor.getString(cursor.getColumnIndex
//                                    (DatabaseHandler.FarmerCollectionTable.POST_SHIFT)),
//                            cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FARMERID))));
//
//            if(localCursor!=null && localCursor.moveToFirst())
//            {
//                farmerPos = localCursor.getInt(0);
//            }
//
//            allPostion.add((farmerPos-startPos));
//            averageReportDetail.totalMember = averageReportDetail.totalMember+1;
//        }
//        while(cursor.moveToNext());
//
//
//        Cursor localCursor;
//
//        int activeCount=0;
//        int farmerCount=0;
//
//        localCursor = DatabaseHandler.getDatabaseInstance(
//                mContext).getCursor(queryForTotalActiveShift());
//
//        if(localCursor!=null && localCursor.moveToFirst())
//        {
//            activeCount = localCursor.getInt(0);
//        }
//
//        localCursor = DatabaseHandler.getDatabaseInstance(
//                mContext).getCursor(queryForTotalActiveShiftForFarmer(farmerId));
//
//        if(localCursor!=null && localCursor.moveToFirst())
//        {
//            farmerCount = localCursor.getInt(0);
//        }
//
//        averageReportDetail.numberOfTimeBunked = activeCount-farmerCount;
//        averageReportDetail.neighbourFarmer = allFarmer.get(0);
//        averageReportDetail.maxEntryRank = allPostion.get(0);
//        averageReportDetail.minEntryRank = allPostion.get(allPostion.size()-1);
//        return averageReportDetail;
//    }

    public ConfigurationElement getConfigurationlistEntity(Cursor cursor) {
        ConfigurationElement configurationElement = new ConfigurationElement();

        configurationElement.key =
                cursor.getString(cursor.getColumnIndex(CONFIGURATION_KEY));
        configurationElement.value =
                cursor.getString(cursor.getColumnIndex(CONFIGURATION_VALUE));
        configurationElement.lastValue =
                cursor.getString(cursor.getColumnIndex(CONFIGURATION_LAST_VALUE));
        configurationElement.modifiedDate =
                cursor.getLong(cursor.getColumnIndex(CONFIGURATION_MODIFIED_TIME));
        configurationElement.userName =
                cursor.getString(cursor.getColumnIndex(CONFIGURATION_USER_NAME));
        configurationElement.status =
                cursor.getInt(cursor.getColumnIndex(CONFIGURATION_STATUS));
        return configurationElement;

    }

    public ContentValues getConfigurationContentValue(ConfigurationElement configurationElement) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(CONFIGURATION_KEY, configurationElement.key);
        contentValues.put(CONFIGURATION_VALUE, configurationElement.value);
        contentValues.put(CONFIGURATION_LAST_VALUE, configurationElement.lastValue);
        contentValues.put(CONFIGURATION_MODIFIED_TIME, configurationElement.modifiedDate);
        contentValues.put(CONFIGURATION_USER_NAME, configurationElement.userName);
        contentValues.put(CONFIGURATION_STATUS, configurationElement.status);
        return contentValues;
    }

    public String getQueryForReport(
            String route, String mcc, String
            dateFrom, String dateTo
            , String cattleType, String shift, String reportType, int recordComitedStatus) {
        String checkRoute = getValidString(route.replace(",", ""));
        if (!checkRoute.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            route = getStringFromTokenizer(route);
        } else {
            route = checkRoute;
        }
        String checkMcc = getValidString(mcc.replace(",", ""));
        if (!checkMcc.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            mcc = getStringFromTokenizer(mcc);
        } else {
            mcc = checkMcc;
        }

        dateFrom = getValidString(dateFrom);
        dateTo = getValidString(dateTo);
        cattleType = getValidString(cattleType);
        shift = getValidString(shift);


        String query = null;
        String reportTypequery = " AND " + KEY_REPORT_TYPE + " = '" + reportType + "'";
        if (sessionManager.getMCCStatus()) {
            String recordStatus = Util.RECORD_STATUS_INCOMPLETE;
            if (sessionManager.getRecordStatusComplete())
                recordStatus = Util.RECORD_STATUS_COMPLETE;

            query = "Select * from " + TABLE_REPORTS + " where "
                    + KEY_REPORT_RECORD_STATUS + " ='" + recordStatus + "'";

        } else {
            query = "Select * from " + TABLE_REPORTS + " where "
                    + KEY_REPORT_COMMITED + " = " + recordComitedStatus
                    + " AND " + KEY_REPORT_RECORD_STATUS + " ='" + Util.RECORD_STATUS_COMPLETE + "'";
        }

        if (!route.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + KEY_REPORT_ROUTE + " IN (" + route.trim() + ")";
        }
        if (!mcc.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + DatabaseHandler.KEY_REPORT_FARMERID + " IN (" + mcc.trim() + ")";
        }
        if (!shift.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + FarmerCollectionTable.POST_SHIFT + " = '" + shift + "'";
        }

        if (!cattleType.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + KEY_REPORT_MILKTYPE + " = '" + cattleType + "'";
        }
        if (!reportType.equalsIgnoreCase(Util.REPORT_TYPE_MCC) &&
                cattleType.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND (" + KEY_REPORT_TYPE + " = '" +
                    reportType + "'" + " OR " + KEY_REPORT_TYPE + " = '" + Util.REPORT_TYPE_SAMPLE + "') ";
        } else {
            query = query + reportTypequery;
        }

        if (!dateFrom.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)
                && !dateTo.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            long start_date = Util.getDateInLongFormat(dateFrom);
            long end_date = Util.getDateInLongFormat(dateTo);
            query = query + "AND " + KEY_REPORT_LDATE + " BETWEEN "
                    + start_date + " AND " + end_date;
        } else if (!dateFrom.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + KEY_REPORT_LDATE + " = " + Util.getDateInLongFormat(dateFrom);
        }
        return query;
    }


    public String getStringFromTokenizer(String value) {
        String[] valueList = value.split(",");
        String values = "";
        if (valueList.length > 0) {
            values = "'" + valueList[0] + "'";
            if (!values.equalsIgnoreCase("All")) {
                for (int i = 1; i < valueList.length; i++) {
                    if (valueList[i].trim().length() > 0 &&
                            !valueList[i].trim().equalsIgnoreCase("")) {
                        values = values + ",'" + valueList[i].trim() + "'";
                    }

                }
            }
        }

        if (values.trim().endsWith(",")) {

        }
        return values;
    }

    /**
     * Check if value exist with given value
     *
     * @param table
     * @param key
     * @param data
     * @return
     */
    public boolean checkForIfValueExist(String table, String key, String data) {

        String values = getStringFromTokenizer(data);
        String valueList[] = values.split(",");
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        boolean returnValue = dbh.validateData(table, key, values, valueList.length);
        return returnValue;

    }

    int getIntTime(String time) {
        String[] strArray = new String[0];
        try {
            strArray = time.split(":");
            return Integer.parseInt(Util.padding(Integer.parseInt(strArray[0]))
                    + Util.padding(Integer.parseInt(strArray[1])));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String queryToNearestAheadFarmer(long time, String shift) {

        return "Select " + DatabaseHandler.KEY_REPORT_NAME + " from " +
                TABLE_REPORTS + " where " +
                DatabaseHandler.KEY_REPORT_TIME_MILLI + " > " + time
                + " AND " + FarmerCollectionTable.POST_SHIFT + " = '" + shift + "'"
                + " order by " + DatabaseHandler.KEY_REPORT_TIME_MILLI
                + " ASC LIMIT " + 1;
    }

    public String queryTogetNearestLastFarmer(long time, String shift) {
        return "Select " + DatabaseHandler.KEY_REPORT_NAME + " from " +
                TABLE_REPORTS + " where " +
                DatabaseHandler.KEY_REPORT_TIME_MILLI + " < " + time
                + " AND " + FarmerCollectionTable.POST_SHIFT + " = '" + shift + "'"
                + " order by " + DatabaseHandler.KEY_REPORT_TIME_MILLI
                + " DESC LIMIT " + 1;
    }

    public String queryToGetTheStartPosition(String date, String shift) {
        String query = "Select _id " + " from " +
                TABLE_REPORTS + " where " +
                FarmerCollectionTable.POST_DATE + " = '" + date
                + "' AND " + FarmerCollectionTable.POST_SHIFT + " = '" + shift + "'"
                + " order by " + DatabaseHandler.KEY_REPORT_TIME_MILLI
                + " ASC LIMIT " + 1;
        return query;
    }

    public String queryToGetThePositionFarmer(String date, String shift, String id) {
        String query = "Select _id " + " from " +
                TABLE_REPORTS + " where " +
                FarmerCollectionTable.POST_DATE + " = '" + date
                + "' AND " + FarmerCollectionTable.POST_SHIFT + " = '" + shift + "'"
                + " AND " + DatabaseHandler.KEY_REPORT_FARMERID + " = '" + id + "'"
                + " order by " + DatabaseHandler.KEY_REPORT_TIME_MILLI
                + " ASC LIMIT " + 1;

        return query;
    }

    public String queryForTotalActiveShift() {
        return "Select count (*) from (select distinct date,shift from "
                + TABLE_REPORTS + " )";
    }

    public String queryForTotalActiveShiftForFarmer(String id) {
        return "Select count (*) from (select distinct date,shift from " + TABLE_REPORTS +
                " Where " +
                DatabaseHandler.KEY_REPORT_FARMERID + " = " + "'" + id + "')";
    }

    public CenterEntity getAllAgentList(Cursor cursor)

    {

        CenterEntity centerEntity = new CenterEntity();
        centerEntity.centerName = cursor.getString(cursor.getColumnIndex(
                DatabaseHandler.KEY_CHILLING_CENTER_NAME));
        centerEntity.centerId = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_CHILLING_CENTER_ID));

        return centerEntity;

    }

    public SequenceCollectionRecord getCollectionEntry(Cursor cursor) {

        SequenceCollectionRecord collEntry = new SequenceCollectionRecord();
        com.devapp.devmain.agentfarmersplit.QuantityParams quantityParams = new com.devapp.devmain.agentfarmersplit.QuantityParams();
        com.devapp.devmain.agentfarmersplit.QualityParams qualityParams = new com.devapp.devmain.agentfarmersplit.QualityParams();
        com.devapp.devmain.agentfarmersplit.RateParams rateParams = new com.devapp.devmain.agentfarmersplit.RateParams();

        ValidationHelper validationHelper = new ValidationHelper();

        collEntry.producerId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FARMERID));
        collEntry.userId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_USER));
        collEntry.mode = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MANUAL));
//        collEntry.s = cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.FarmerCollectionTable.POST_SHIFT));


//        collEntry.date=cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.FarmerCollectionTable.POST_DATE));

        collEntry.milkType = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));


//        collEntry.time = cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME));
        collEntry.collectionTime = cursor.getLong(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME_MILLI));

        collEntry.status = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_STATUS));
        collEntry.collectionType = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TYPE));
        collEntry.numberOfCans = Integer.parseInt(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS))
        );
        collEntry.collectionRoute = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_ROUTE));
        collEntry.sampleNumber = cursor.getInt(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER));
//        collEntry.status = cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RECORD_STATUS));
        collEntry.agentId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AGENT_ID));

//        collEntry.milkStatusCode = cursor.getInt(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKSTATUS_CODE));
        collEntry.milkType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));


        qualityParams.snf = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SNF)), 0);
        qualityParams.fat = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FAT)), 0);
        qualityParams.clr = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CLR)), 0);
//        qualityParams.density = checkValidation.getDoubleFromString(
//                cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_)),0);
        qualityParams.awm = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AWM)), 0);
//        qualityParams.freezingPoint = checkValidation.getDoubleFromString(
//                cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_FR)),0);
        qualityParams.temperature = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TEMP)), 0);


        qualityParams.qualityMode = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKOMANUAL));
        qualityParams.qualityTime = validationHelper.getLongFromString(cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKANALYSERTIME)));


        qualityParams.maSerialNumber = String.valueOf(cursor.getInt(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_SERIAL)));

        qualityParams.milkStatusCode = cursor.getInt(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKSTATUS_CODE));

        qualityParams.milkQuality = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_MILKQUALITY));


//        qualityParams.n = cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_NAME));

        qualityParams.protein = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_PROTEIN)), 0);
        qualityParams.lactose = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LACTOSE)), 0);
        qualityParams.conductivity = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CONDUTIVITY)), 0);
        qualityParams.calibration = cursor.getString
                (cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CALIBRATION));


        rateParams.rate = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)), 0);
//        rateParams.amount = checkValidation.getDoubleFromString(
//                cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), 0
//        );
        rateParams.bonus = cursor.getDouble(cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_BONUS));
        rateParams.rateMode = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATEMODE));
        rateParams.isRateCalculated = getRateCalculated(cursor.getInt(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE_CALC_DEVICE)));
        rateParams.rateChartName = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATECHART_NAME));


        rateParams.incentiveRate = Double.parseDouble(cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_INCENTIVE_RATE)));
        rateParams.incentiveAmount = Double.parseDouble(cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_INCENTIVE_AMOUNT)));
        rateParams.baseAmount = Double.parseDouble(cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)));
        rateParams.baseRate = Double.parseDouble(cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE)));
        rateParams.amount = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AMOUNT)), 0
        );

        quantityParams.weighingQuantity = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_QUANTITY)), 0
        );
        quantityParams.kgQuantity = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_KG_QTY)), 0
        );
        quantityParams.ltrQuantity = validationHelper.getDoubleFromString(
                cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LTR_QTY)), 0
        );
        quantityParams.quantityMode = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_WEIGHTMANUAL));
        quantityParams.tippingStartTime = cursor.getLong(
                cursor.getColumnIndex(DatabaseHandler.KEY_TIPPING_START_TIME));
        quantityParams.tippingEndTime = cursor.getLong(
                cursor.getColumnIndex(DatabaseHandler.KEY_TIPPING_END_TIME));

        quantityParams.quantityTime = validationHelper.getLongFromString(cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_WEIGHINGTIME)));


        collEntry.qualityParams = qualityParams;
        collEntry.quantityParams = quantityParams;
        collEntry.rateParams = rateParams;


        return collEntry;

    }

    public String getQueryDetailsCCReport(
            String route, String mcc, String
            dateFrom, String dateTo
            , String cattleType, String shift, String reportType, int recordComitedStatus) {
        String checkRoute = getValidString(route.replace(",", ""));
        if (!checkRoute.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            route = getStringFromTokenizer(route);
        } else {
            route = checkRoute;
        }
        String checkMcc = getValidString(mcc.replace(",", ""));
        if (!checkMcc.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            mcc = getStringFromTokenizer(mcc);
        } else {
            mcc = checkMcc;
        }

        dateFrom = getValidString(dateFrom);
        dateTo = getValidString(dateTo);
        cattleType = getValidString(cattleType);
        shift = getValidString(shift);


        String query = null;
        query = "Select * from " + TABLE_REPORTS + " where "
                + KEY_REPORT_TYPE + " = '" + reportType + "'";

        if (!route.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + KEY_REPORT_ROUTE + " IN (" + route.trim() + ")";
        }
        if (!mcc.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + DatabaseHandler.KEY_REPORT_FARMERID + " IN (" + mcc.trim() + ")";
        }
        if (!shift.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + FarmerCollectionTable.POST_SHIFT + " = '" + shift + "'";
        }
        if (!cattleType.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + KEY_REPORT_MILKTYPE + " = '" + cattleType + "'";
        }

        if (!dateFrom.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)
                && !dateTo.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            long start_date = Util.getDateInLongFormat(dateFrom);
            long end_date = Util.getDateInLongFormat(dateTo);
            query = query + "AND " + KEY_REPORT_LDATE + " BETWEEN "
                    + start_date + " AND " + end_date;
        } else if (!dateFrom.trim().equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            query = query + " AND " + KEY_REPORT_LDATE + " = " + Util.getDateInLongFormat(dateFrom);
        }
        return query;
    }

    /**
     * total 17 items
     */
    public interface SplitRecord {

        String TABLE = "SplitFarmerRecords";

        String COLUMNID = "_id";
        String AGENT_ID = "repAgentId";
        String COLLECTION_TIME = "collectionTime";
        String COMMITED = "recordCommited";
        String ENTRY_MODE = "entryMode";
        String FARMERID = "farmerId";
        String MANUAL = "manual";
        //        String MILKQUALITY = "milkQuality";
//        String MILKSTATUS_CODE = "milkStatusCode";
        String MILKTYPE = "milkType";
        String NUMBER_OF_CANS = "numberOfCans";
        String RECORD_STATUS = "recordStatus";
        String SEQUENCE_NUMBER = "repSequenceNumber";
        String SHIFT = "shift";
        String SOCIETYID = "socID";
        String TYPE = "reportType";
        String USER = "user";
        String SENT = "sent";
        String DATE = "date";
        String PARENT_SEQ_NUM = "parentSeqNum";

    }


    /**
     * total 21 items
     */
    public interface QualityParams {

        String LACTOSE = "lactose";
        String ADDED_WATER = "addedWater";
        String ALCOHOL = "alcohol";
        String CALIBRATION = "calibration";
        String CONDUCTIVITY = " conductivity";
        String CLR = "repCLR";
        String COM = "com";
        String DENSITY = "density";
        String FAT = "fat";
        String FREEZING_POINT = "freezingPoint";
        String SALT = "salt";
        String SNF = "snf";
        String MA_SERIAL_NUM = "maSerialNumber";
        String TEMP = "temp";
        String QUALITY_MODE = "qualityMode";
        String QUALITY_TIME = "qualityTime";
        String QUALITY_START_TIME = "qualityStartTime";
        String QUALITY_END_TIME = "qualityEndTime";
        String PH = "ph";
        String PROTEIN = "protein";

        String MILKQUALITY = "milkQuality";
        String MILKSTATUS_CODE = "milkStatusCode";
        String MA_NUMBER = "maNumber";
        String MA_NAME = "maName";
    }


    /**
     * total 8 items
     */
    public interface QuantityParams {

        String KG_QUANTITY = "kgQuantity";
        String LTR_QUANTITY = "literQuantity";
        String MEASUREMENT_UNIT = "measurementUnit";
        String QUANTITY_MODE = "quantityMode";
        String QUANTIY_TIME = "quantityTime";
        String TIPPING_START_TIME = "tippingStartTime";
        String TIPPING_END_TIME = "tippingEndTime";
        String WEIGHING_QUANTITY = "weighingQuantity";

    }


    /**
     * total 8 items
     */
    public interface RateParams {

        String RATE_MODE = "rateMode";
        String AMOUNT = "amount";
        String BONUS = "bonus";
        String INCENTIVE = "incentive";
        String RATE = "rate";
        String RATE_CAL_FROM_DEVICE = "rateCalcFromDevice";
        String RATE_CHART_NAME = "rateChartName";
        String RATE_UNIT = "rateUnit";
    }


    //Bonus Table
    public interface InCentive {
        String TABLE_NAME = "IncentiveRateChart";
        String ID = "_id";
        String VALUE = "point";
        String BONUS = "bonus";
        String START_DATE = "startDate";
        String END_DATE = "endDate";
        String SOCIETY_ID = "SCID";
        String MILK_TYPE = "milkType";
        String RATE_CHART_NAME = "rateName";
        String RATE_CHART_TYPE = "rateCharttype";

    }


}


