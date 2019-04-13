package com.devapp.smartcc.entityandconstants;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.agentfarmersplit.ShiftConcluded;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.dbbackup.SecondaryDBObject;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SocietyEntity;
import com.devapp.devmain.entity.WhiteListUrl;
import com.devapp.devmain.helper.AfterLogInTask;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.SntpClient;
import com.devapp.devmain.helper.UserRole;
import com.devapp.devmain.httptasks.PostCollectionRecordsService;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.tableEntities.EditRecordCollectionTable;
import com.devapp.devmain.tableEntities.FarmerCollectionTable;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.TextDrawable;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.database.AgentDatabase;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * Created by Upendra on 10/5/2016.
 */
public class SmartCCUtil {

    public static final String PACKAGE_NAME = "com.devApp";
    public static String PRIMARY_SHARED_PREF_PATH = "/data/data/" + PACKAGE_NAME + "/shared_prefs";
    public static String SECONDARY_SHARED_PREF_PATH = Util.getSDCardPath() + "/.devappdatabase/shared_prefs";
    public static boolean FORCE_SHUTDOWN = false;
    public static Date MIN_DATE;
    public static Date MAX_DATE;
    private static float hintSize = TextDrawable.SIZE_16;
    Context mContext;
    AmcuConfig amcuConfig;
    SessionManager sessionManager;
    TextToSpeech tts;
    AlertDialog alertDialog;
    ObjectAnimator anim;
    int count = 0;
    View view;
    ProgressBar mprogressBar;
    ImageView ivStatus;
    Runnable runnable;
    android.os.Handler handler;
    android.os.Handler timeHandler;
    Runnable timeRunnable;
    long ntpTime;
    long elapsedTime;
    long roundTripTime;
    private int hintColor;


    public SmartCCUtil(Context context) {
        this.mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(context);
        hintColor = mContext.getResources().getColor(R.color.blue);
    }

    //
    public static ReportEntity getReportFromCursor(Cursor cursor) {

        ReportEntity reportEntity = new ReportEntity();
        try {
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

            reportEntity.bonus = cursor.getDouble(
                    cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_BONUS));
            reportEntity.time = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME));
            reportEntity.miliTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME_MILLI));
            reportEntity.awm = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AWM));
            reportEntity.clr = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_CLR));
            reportEntity.status = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_STATUS));
            reportEntity.quantityMode = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_WEIGHTMANUAL));
            reportEntity.qualityMode = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKOMANUAL));
            reportEntity.milkAnalyserTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKANALYSERTIME));
            reportEntity.weighingTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_WEIGHINGTIME));
            reportEntity.temp = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TEMP));
            reportEntity.recordCommited = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_COMMITED));
            reportEntity.collectionType = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TYPE));
            reportEntity.milkQuality = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_MILKQUALITY));
            reportEntity.rateMode = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATEMODE));
            reportEntity.numberOfCans = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS));
            reportEntity.sampleNumber = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER));
            reportEntity.centerRoute = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_ROUTE));
            reportEntity.recordStatus = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RECORD_STATUS));

            reportEntity.kgWeight = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_KG_QTY));
            reportEntity.ltrsWeight = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LTR_QTY));

            reportEntity.tippingStartTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_TIPPING_START_TIME));
            reportEntity.tippingEndTime = cursor.getLong(
                    cursor.getColumnIndex(DatabaseHandler.KEY_TIPPING_END_TIME));
            reportEntity.user = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_USER));
            reportEntity.agentId = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_AGENT_ID));
            reportEntity.milkStatusCode = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKSTATUS_CODE));
            reportEntity.milkType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));

            reportEntity.rateCalculation = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATE_CALC_DEVICE));

            reportEntity.serialMa = cursor.getInt(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_SERIAL));
            reportEntity.maName = cursor.getString(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_NAME));
            reportEntity.protein = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_PROTEIN));
            reportEntity.incentiveRate = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_INCENTIVE_RATE));
            reportEntity.incentiveAmount = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_INCENTIVE_AMOUNT));
            reportEntity.snfKg = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_KG_SNF));
            reportEntity.fatKg = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_KG_FAT));
            reportEntity.postDate = cursor.getString(
                    cursor.getColumnIndex(FarmerCollectionTable.POST_DATE));
            reportEntity.postShift = cursor.getString(
                    cursor.getColumnIndex(FarmerCollectionTable.POST_SHIFT));
            reportEntity.sequenceNum = cursor.getLong(
                    cursor.getColumnIndex(FarmerCollectionTable.SEQUENCE_NUMBER));
            reportEntity.sentStatus = cursor.getInt(
                    cursor.getColumnIndex(FarmerCollectionTable.KEY_REPORT_SENT_STATUS));
            reportEntity.sentSmsStatus = cursor.getInt(
                    cursor.getColumnIndex(FarmerCollectionTable.SMS_SENT_STATUS));
            reportEntity.lactose = cursor.getDouble(
                    cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LACTOSE));
            reportEntity.lastModified = cursor.getLong(cursor.getColumnIndex(FarmerCollectionTable.LAST_MODIFIED));

            if (cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATECHART_NAME) != -1)
                reportEntity.rateChartName = cursor.getString(
                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_RATECHART_NAME));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reportEntity;

    }


    public static ReportEntity getAggreateFarmerIDCursor(Cursor cursor) {

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.columnId = cursor.getLong(0);
        reportEntity.farmerId = cursor.getString(
                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_FARMERID));
        reportEntity.sampleNumber = cursor.getInt(
                cursor.getColumnIndex(FarmerCollectionTable.SEQUENCE_NUMBER));

        return reportEntity;

    }


    /**
     * To decide selected milk analyser is MA1 or not
     *
     * @param MA1
     * @return
     */
    public static boolean isMA1(String MA1) {
        if (MA1.equalsIgnoreCase(DeviceName.MILK_ANALYSER) ||
                MA1.equalsIgnoreCase(DeviceName.MILK_ANALYSER)) {
            return true;
        } else {
            return false;
        }

    }

    public static String getRupeeSymbol() {
        return "\u20B9";
    }

    public static String getDollorSymbol() {
        return "\u0024";
    }

    public static String getRupeeSign() {
        return "\u20A8";
    }

    public static String getEuroSymbol() {
        return "\u20AC";
    }

    public static String getDegreeCelsius() {
        return "\u2103";
    }

    public static int pixelToDp(Context context, int pixel) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return pixel < 0 ? pixel : Math.round(pixel / displayMetrics.density);
    }

    /**
     * Convert dp to pixel. Preserve the negative value as it's used for representing
     * MATCH_PARENT(-1) and WRAP_CONTENT(-2).
     *
     * @param context the context
     * @param dp      the value in dp
     * @return the converted value in pixel
     */
    public static int dpToPixel(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return dp < 0 ? dp : Math.round(dp * displayMetrics.density);
    }

    public static String tabInfor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return "Pixel information " + "\n"
                + "Density " + displayMetrics.density + "\n"
                + " Height in pixel " + displayMetrics.heightPixels + "\n"
                + " Width in pixel " + displayMetrics.widthPixels + "\n"
                + "Scale density " + displayMetrics.scaledDensity + "\n"
                + " xhdpi  " + displayMetrics.xdpi + "\n"
                + " yhdpi " + displayMetrics.ydpi + "\n";

    }

    public static void todoDbFailure(Context context, String isPrimary) {
        if (DatabaseHandler.isPrimary.equalsIgnoreCase(isPrimary)) {
            Util.displayErrorToast("Unable to access database!", context);
        }


    }

    public static String getFullShift(String shift) {
        if (shift.equalsIgnoreCase("M")) {
            return "MORNING";
        } else if (shift.equalsIgnoreCase("E")) {
            return "EVENING";
        }
        return shift;
    }

    public static boolean isSecondaryDatabaseExist(Context context) {
        boolean returnValue = false;
        SecondaryDBObject sdb = new SecondaryDBObject();
        if (sdb.isFileExist()) {
            DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
            if (dbh.getSecondaryDatabase() != null) {
                returnValue = true;
            }
        }

        return returnValue;

    }

    public static Date getCollectionDateFromLongTime(long time) {
        long milliSec = time;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kolkata"));
        calendar.setTimeInMillis(milliSec);
        System.out.println("GregorianCalendar -" + sdf.format(calendar.getTime()));

        Date date = null;
        try {
            date = sdf.parse(sdf.format(calendar.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    //Return today date in "YYYY-MM-dd" format
    public static String getDateInPostFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("Asia/Kolkata"));
        return sdf.format(calendar.getTime());
    }

    //Update all supervisor to tanker supervisor

    //Return today date in "YYYY-MM-dd" format
    public static String getShiftInPostFormat(Context context) {
        String shift = Util.getCurrentShift();

        return getFullShift(shift);
    }

    public static String getpostDateFromTheDateFormat(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date.getTime());
    }

    public static SocietyEntity getCenterEntity(Context context) {
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();
        SocietyEntity socEntity = databaseHandler.getSocietyDetails(0);

        return socEntity;

    }

    public static String changeDateFormat(String dateString, String oldFormat, String newFormat) {
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

    public static long getTodayTimeInMilli() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();

    }

    public static String queryWithCompleteRecords() {
        return " AND " + FarmerCollectionTable.KEY_REPORT_RECORD_STATUS + " ='" + Util.RECORD_STATUS_COMPLETE + "'";
    }

    public static String getAlternateShift(String shift) {
        switch (shift) {
            case "M": {
                return "MORNING";
            }
            case "E": {
                return "EVENING";
            }
            case "MORNING": {
                return "M";
            }
            case "EVENING": {
                return "E";
            }
            default: {
                return "MORNING";
            }
        }
    }

    //Update all supervisor to tanker supervisor

    public static String getAccumalativeReportQuery() {
        String query =
                " Select " +
                        FarmerCollectionTable.KEY_REPORT_COLUMNID + ", "
                        + FarmerCollectionTable.KEY_REPORT_FARMERID + "," + FarmerCollectionTable.KEY_REPORT_NAME + ","
                        + FarmerCollectionTable.KEY_REPORT_SNF + "," + FarmerCollectionTable.KEY_REPORT_FAT + ","
                        + FarmerCollectionTable.KEY_REPORT_USER + "," +
                        FarmerCollectionTable.KEY_REPORT_MANUAL + "," +
                        "sum(" + FarmerCollectionTable.KEY_REPORT_AMOUNT + ")" + ","
                        + "sum(" + FarmerCollectionTable.KEY_REPORT_QUANTITY + ")" + ","
                        + FarmerCollectionTable.KEY_REPORT_TAXNUM + "," +
                        FarmerCollectionTable.KEY_REPORT_MILKTYPE + ","
                        + "sum(" + FarmerCollectionTable.KEY_REPORT_AMOUNT + ")/"
                        + "sum(" + FarmerCollectionTable.KEY_REPORT_QUANTITY + ")"
                        + "," + FarmerCollectionTable.KEY_REPORT_LDATE + ","
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
                        + " from " + FarmerCollectionTable.TABLE_REPORTS;

        return query;
    }

    //TODO: Need to milli time from the date
    public static RatechartDetailsEnt getRateChartEntity(RateChartEntity rateChartEntity) {
        RatechartDetailsEnt rde = new RatechartDetailsEnt();

        rde.rateChartName = rateChartEntity.rateChartName;
        rde.ratechartType = rateChartEntity.milkType;
        rde.rateSocId = rateChartEntity.societyId;
        rde.isActive = String.valueOf(rateChartEntity.isActive);
        rde.rateValidityFrom = rateChartEntity.startDate;
        rde.rateValidityTo = rateChartEntity.endDate;

        rde.modifiedTime = System.currentTimeMillis();

        return rde;
    }

    public String getAgentId() {
        return sessionManager.getFarmerID();
    }

    public int getMilkStatusCode(String value) {
        if (value.equalsIgnoreCase("GOOD"))
            return 1;
        else if (value.equalsIgnoreCase("SOUR"))
            return 2;
        else if (value.equalsIgnoreCase("CURD"))
            return 3;
        else if (value.equalsIgnoreCase("SOUR VEHICLE FAULT"))
            return 4;
        else
            return 1;
    }

    public String getMilkTypeFromAgent(String centerId) {
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();
        AgentDatabase agentDatabase = databaseHandler.getAgentDatabase();
        return agentDatabase.getMilkType(centerId);
    }

    public void setRateChart() {
        if (sessionManager.getMilkType().equalsIgnoreCase("Buffalo")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForBuffalo());
        } else if (sessionManager.getMilkType().equalsIgnoreCase("Mixed")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForMixed());

        } else {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForCow());
        }
    }

    public long getDateInMillis(int i, boolean isReport) {
        Calendar calendar = null;
        if (isReport == true) {
            calendar = getReportCalendarInstance(i);
        } else {
            calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, i);
        }
        int year = calendar.get(Calendar.YEAR);
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);

        calendar.set(year, month, day);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long date = calendar.getTimeInMillis();
        return date;
    }

    public long getCalendarTime(int year, int day, int month, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        long date = calendar.getTimeInMillis();
        return date;

    }

    public String getCurrentRateChartForCattle(String milkType) {

        if (!amcuConfig.getIsRateChartMandatory()) {
            amcuConfig.setRateChartName(SmartCCConstants.NO_RATECHART);
        } else if (milkType.equalsIgnoreCase(CattleType.BUFFALO)) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForBuffalo());
        } else if (milkType.equalsIgnoreCase(CattleType.MIXED)) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForMixed());

        } else {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForCow());

        }
        return amcuConfig.getRateChartName();
    }

    public String getCurrentIncentiveRateChartForCattle(String milkType) {

        if (!amcuConfig.getIsRateChartMandatory()) {
            amcuConfig.setInCentiveRateChartname(SmartCCConstants.NO_RATECHART);
        } else if (milkType.equalsIgnoreCase(CattleType.BUFFALO)) {
            amcuConfig.setInCentiveRateChartname(amcuConfig.getIncentiveRateChartForBuffalo());

        } else if (milkType.equalsIgnoreCase(CattleType.MIXED)) {
            amcuConfig.setInCentiveRateChartname(amcuConfig.getIncentiveRateChartForMixed());

        } else {
            amcuConfig.setInCentiveRateChartname(amcuConfig.getIncentiveRateChartForCow());

        }

        return amcuConfig.getInCentiveRateChartname();
    }

    public Calendar getCalendarInstance(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        calendar.add(Calendar.DATE, days);
        return calendar;
    }

    /*
     * This will return yyyy-MM-dd format of date
     * */
    public String getReportFormatDate() {
        Calendar c = getCalendarInstance(0);
        if (isExtendedEveningShift()) {
            c = getCalendarInstance(-1);
        }

        Date date = c.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public String getReportFormatDate(int i) {
        Calendar c = getCalendarInstance(i);
        Date date = c.getTime();

        return new SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    public boolean isExtendedEveningShift() {
        boolean isExtendedEvnShift = false;
        if (Util.getCurrentShift().equalsIgnoreCase(AppConstants.Shift.EVENING)) {
            AmcuConfig amcuConfig = AmcuConfig.getInstance();
            final Calendar c = getCalendarInstance(0);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            int time = (Integer.parseInt(Util.padding(hour) + Util.padding(min)));
            if (time >= 0 && time < Integer
                    .parseInt(amcuConfig.getMorningStart().replace(":", ""))) {
                isExtendedEvnShift = true;
            }
        }

        return isExtendedEvnShift;
    }

    public Calendar getReportCalendarInstance(int day) {
        if (isExtendedEveningShift()) {
            return getCalendarInstance((day - 1));
        } else
            return getCalendarInstance(day);

    }

    public ContentValues getReportContentValues(ReportEntity reportEntity) {
        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_REPORT_FARMERID,
                ValidationHelper.getValidText(reportEntity.farmerId.toUpperCase(Locale.ENGLISH), ""));
        values.put(DatabaseHandler.KEY_REPORT_FAT, reportEntity.fat);
        values.put(DatabaseHandler.KEY_REPORT_SNF, reportEntity.snf);
        values.put(DatabaseHandler.KEY_REPORT_NAME,
                ValidationHelper.getValidText(reportEntity.farmerName, ""));
        values.put(DatabaseHandler.KEY_REPORT_RATE, reportEntity.rate);
        values.put(DatabaseHandler.KEY_REPORT_MANUAL,
                ValidationHelper.getValidText(reportEntity.manual, "Manual"));
        values.put(DatabaseHandler.KEY_REPORT_AMOUNT, reportEntity.amount);
        values.put(DatabaseHandler.KEY_REPORT_QUANTITY, reportEntity.quantity);
        values.put(DatabaseHandler.KEY_REPORT_MILKTYPE,
                ValidationHelper.getValidText(reportEntity.milkType, "COW"));
        values.put(DatabaseHandler.KEY_REPORT_TAXNUM, reportEntity.txnNumber);
        values.put(DatabaseHandler.KEY_REPORT_LDATE,
                reportEntity.lDate);
        values.put(DatabaseHandler.KEY_REPORT_SOCIETYID,
                reportEntity.socId);
        values.put(DatabaseHandler.KEY_REPORT_BONUS, reportEntity.bonus);
        values.put(DatabaseHandler.KEY_REPORT_USER,
                ValidationHelper.getValidText(reportEntity.user, ""));
        values.put(DatabaseHandler.KEY_REPORT_TIME,
                ValidationHelper.getValidText(reportEntity.time, ""));
        values.put(DatabaseHandler.KEY_REPORT_TIME_MILLI,
                reportEntity.miliTime);
        values.put(DatabaseHandler.KEY_REPORT_AWM, reportEntity.awm);
        values.put(DatabaseHandler.KEY_REPORT_CLR, reportEntity.clr);
        values.put(DatabaseHandler.KEY_REPORT_STATUS, reportEntity.status);
        values.put(DatabaseHandler.KEY_REPORT_WEIGHTMANUAL, reportEntity.quantityMode);
        values.put(DatabaseHandler.KEY_REPORT_MILKOMANUAL, reportEntity.qualityMode);
        values.put(DatabaseHandler.KEY_REPORT_MILKANALYSERTIME, reportEntity.milkAnalyserTime);
        values.put(DatabaseHandler.KEY_REPORT_WEIGHINGTIME, reportEntity.weighingTime);
        values.put(DatabaseHandler.KEY_REPORT_TEMP, reportEntity.temp);
        values.put(DatabaseHandler.KEY_REPORT_COMMITED, reportEntity.recordCommited);
        values.put(DatabaseHandler.KEY_REPORT_TYPE, reportEntity.collectionType);
        values.put(DatabaseHandler.KEY_MILKQUALITY, reportEntity.milkQuality);
        values.put(DatabaseHandler.KEY_REPORT_RATEMODE, reportEntity.rateMode);
        values.put(DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS, reportEntity.numberOfCans);

        values.put(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER, reportEntity.sampleNumber);
        values.put(DatabaseHandler.KEY_REPORT_ROUTE, reportEntity.centerRoute);
        values.put(DatabaseHandler.KEY_REPORT_RECORD_STATUS, reportEntity.recordStatus);

        values.put(DatabaseHandler.KEY_REPORT_KG_QTY, reportEntity.kgWeight);
        values.put(DatabaseHandler.KEY_REPORT_LTR_QTY, reportEntity.ltrsWeight);

        values.put(DatabaseHandler.KEY_REPORT_RATECHART_NAME,
                ValidationHelper.getValidText(reportEntity.rateChartName, ""));
        values.put(DatabaseHandler.KEY_TIPPING_START_TIME, reportEntity.tippingStartTime);
        values.put(DatabaseHandler.KEY_TIPPING_END_TIME, reportEntity.tippingEndTime);
        values.put(DatabaseHandler.KEY_REPORT_AGENT_ID,
                ValidationHelper.getValidText(reportEntity.agentId, ""));
        ;
        values.put(DatabaseHandler.KEY_REPORT_MILKSTATUS_CODE, reportEntity.milkStatusCode);
        values.put(DatabaseHandler.KEY_REPORT_RATE_CALC_DEVICE, reportEntity.rateCalculation);

        values.put(DatabaseHandler.KEY_REPORT_MA_SERIAL,
                ValidationHelper.getValidText(reportEntity.maSerialNumber, ""));
        values.put(DatabaseHandler.KEY_REPORT_MA_NAME,
                ValidationHelper.getValidText(reportEntity.maName, ""));
        values.put(DatabaseHandler.KEY_REPORT_PROTEIN, reportEntity.protein);
        values.put(DatabaseHandler.KEY_REPORT_INCENTIVE_RATE, reportEntity.incentiveRate);
        values.put(DatabaseHandler.KEY_REPORT_INCENTIVE_AMOUNT, reportEntity.incentiveAmount);
        values.put(DatabaseHandler.KEY_REPORT_KG_FAT, reportEntity.fatKg);
        values.put(DatabaseHandler.KEY_REPORT_KG_SNF, reportEntity.snfKg);
        values.put(DatabaseHandler.KEY_REPORT_LACTOSE, reportEntity.lactose);
        values.put(DatabaseHandler.KEY_REPORT_CONDUTIVITY, reportEntity.conductivity);
        values.put(DatabaseHandler.KEY_REPORT_SALT, reportEntity.salt);
        values.put(DatabaseHandler.KEY_REPORT_FREEZING_POINT, reportEntity.freezingPoint);
        values.put(DatabaseHandler.KEY_REPORT_PH, reportEntity.pH);
        values.put(DatabaseHandler.KEY_REPORT_CALIBRATION, reportEntity.calibration);
        values.put(DatabaseHandler.KEY_REPORT_TEMP, reportEntity.temp);

//        values.put(DatabaseHandler.KEY_REPORT_KG_CLR,
//                CheckValidation.getValidText(reportEntitycll, "0.0"));

        values.put(FarmerCollectionTable.KEY_REPORT_SENT_STATUS, reportEntity.sentStatus);
        values.put(FarmerCollectionTable.SMS_SENT_STATUS, reportEntity.sentSmsStatus);


        values.put(FarmerCollectionTable.POST_DATE, reportEntity.postDate);
        values.put(FarmerCollectionTable.POST_SHIFT, reportEntity.postShift);

        return values;

    }

    public ContentValues getExReportContentValues(ReportEntity reportEntity)


    {
        ContentValues values = new ContentValues();

        values.put(DatabaseHandler.KEY_REPORT_FARMERID, reportEntity.farmerId.toUpperCase(Locale.ENGLISH));
        values.put(DatabaseHandler.KEY_REPORT_FAT, reportEntity.fat);
        values.put(DatabaseHandler.KEY_REPORT_SNF, reportEntity.snf);
        values.put(DatabaseHandler.KEY_REPORT_NAME, reportEntity.farmerName);
        values.put(DatabaseHandler.KEY_REPORT_RATE, reportEntity.rate);
        values.put(DatabaseHandler.KEY_REPORT_MANUAL, reportEntity.manual);
        values.put(DatabaseHandler.KEY_REPORT_AMOUNT, reportEntity.amount);
        values.put(DatabaseHandler.KEY_REPORT_QUANTITY, reportEntity.quantity);

        values.put(DatabaseHandler.KEY_REPORT_KG_QTY, reportEntity.kgWeight);
        values.put(DatabaseHandler.KEY_REPORT_LTR_QTY, reportEntity.ltrsWeight);

        values.put(DatabaseHandler.KEY_REPORT_MILKTYPE,
                reportEntity.milkType.toUpperCase(Locale.ENGLISH));
        values.put(DatabaseHandler.KEY_REPORT_TAXNUM, reportEntity.txnNumber);
        values.put(DatabaseHandler.KEY_REPORT_LDATE, reportEntity.lDate);
        values.put(DatabaseHandler.KEY_REPORT_SOCIETYID,
                String.valueOf(sessionManager.getSocietyColumnId()));
        values.put(DatabaseHandler.KEY_REPORT_BONUS, reportEntity.bonus);
        values.put(DatabaseHandler.KEY_REPORT_USER, reportEntity.user);
        values.put(DatabaseHandler.KEY_REPORT_TIME, reportEntity.time);
        values.put(DatabaseHandler.KEY_REPORT_TIME_MILLI, reportEntity.miliTime);
        values.put(DatabaseHandler.KEY_REPORT_AWM, reportEntity.awm);
        values.put(DatabaseHandler.KEY_REPORT_CLR, reportEntity.clr);
        values.put(DatabaseHandler.KEY_REPORT_STATUS, reportEntity.status);
        values.put(DatabaseHandler.KEY_REPORT_WEIGHTMANUAL, reportEntity.quantityMode);
        values.put(DatabaseHandler.KEY_REPORT_MILKOMANUAL, reportEntity.qualityMode);
        values.put(DatabaseHandler.KEY_REPORT_MILKANALYSERTIME, reportEntity.milkAnalyserTime);
        values.put(DatabaseHandler.KEY_REPORT_WEIGHINGTIME, reportEntity.weighingTime);
        values.put(DatabaseHandler.KEY_REPORT_TEMP, reportEntity.temp);
        values.put(DatabaseHandler.KEY_REPORT_COMMITED, reportEntity.recordCommited);
        values.put(DatabaseHandler.KEY_REPORT_TYPE, reportEntity.collectionType);
        values.put(DatabaseHandler.KEY_MILKQUALITY, reportEntity.milkQuality);
        values.put(DatabaseHandler.KEY_REPORT_RATEMODE, reportEntity.rateMode);
        values.put(DatabaseHandler.KEY_REPORT_NUMBER_OF_CANS, reportEntity.numberOfCans);

        values.put(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER, reportEntity.sampleNumber);
        values.put(DatabaseHandler.KEY_REPORT_ROUTE, reportEntity.centerRoute);
        values.put(DatabaseHandler.KEY_REPORT_RECORD_STATUS, reportEntity.recordStatus);

        values.put(DatabaseHandler.KEY_REPORT_FOREIGN_SEQUENCE_ID, reportEntity.foreignSequenceNum);
        values.put(DatabaseHandler.KEY_REPORT_OLD_OR_NEW_FLAG, reportEntity.oldOrNewFlag);
        values.put(DatabaseHandler.KEY_REPORT_EDITED_TIME, reportEntity.editedTime);

        values.put(DatabaseHandler.KEY_TIPPING_START_TIME, reportEntity.tippingStartTime);
        values.put(DatabaseHandler.KEY_TIPPING_END_TIME, reportEntity.tippingEndTime);
        values.put(DatabaseHandler.KEY_REPORT_AGENT_ID, reportEntity.agentId);
        values.put(DatabaseHandler.KEY_REPORT_MILKSTATUS_CODE, reportEntity.milkStatusCode);
        values.put(DatabaseHandler.KEY_REPORT_RATE_CALC_DEVICE, reportEntity.rateCalculation);

        values.put(DatabaseHandler.KEY_REPORT_MA_SERIAL, reportEntity.serialMa);
        values.put(DatabaseHandler.KEY_REPORT_MA_NAME, reportEntity.maName);

        values.put(DatabaseHandler.KEY_REPORT_LACTOSE, reportEntity.lactose);
        values.put(DatabaseHandler.KEY_REPORT_CONDUTIVITY, reportEntity.conductivity);
        values.put(DatabaseHandler.KEY_REPORT_SALT, reportEntity.salt);
        values.put(DatabaseHandler.KEY_REPORT_FREEZING_POINT, reportEntity.freezingPoint);
        values.put(DatabaseHandler.KEY_REPORT_PH, reportEntity.pH);
        values.put(DatabaseHandler.KEY_REPORT_CALIBRATION, reportEntity.calibration);
        values.put(EditRecordCollectionTable.SMS_SENT_STATUS, reportEntity.sentSmsStatus);
        values.put(EditRecordCollectionTable.POST_DATE, reportEntity.postDate);
        values.put(EditRecordCollectionTable.POST_SHIFT, reportEntity.postShift);

        return values;

    }

    public int getShiftInInt(String shift) {
        if (shift.equalsIgnoreCase("Morning") || shift.equalsIgnoreCase("M")) {
            return 0;
        } else {
            return 1;
        }

    }

    public long getstartOrShiftTime(String time) {
        String[] strArray = time.split(":");
        Calendar calendar = getCalendarInstance(0);

        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(strArray[0]));
        calendar.set(Calendar.MINUTE, Integer.parseInt(strArray[1]));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        long date = calendar.getTimeInMillis();
        return date;


    }

    public String getDDMMMYYYYFormatToYYYYMMDD(String date) {
        String strDate = date;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            Date varDate = dateFormat.parse(strDate);
            dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            strDate = dateFormat.format(varDate);

        } catch (Exception e) {
            strDate = null;
            e.printStackTrace();
        }

        return strDate;

    }

    public String getReportDate(int day) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, day);
        return dateFormat.format(cal.getTime());
    }

    public boolean validateFatAndSnfForZeroValues(double fat, double snf) {

        if (fat >= Util.MAX_FAT_LIMIT || snf >= Util.MAX_SNF_LIMIT) {
            Util.displayErrorToast("Invalid fat or snf, enter the Id once again ", mContext);
            return false;
        } else if (!Util.checkIfWaterCode(sessionManager.getFarmerID(), amcuConfig.getFarmerIdDigit()) &&
                amcuConfig.getKeyIgnoreZeroFatSnf()) {
            if (fat <= 0 || snf <= 0) {
                Util.displayErrorToast("Invalid fat or snf, enter the Id once again ", mContext);
                return false;
            }

        }
        return true;
    }

    public String getDateAndTimeFromMiliSecond(long milliSeconds) {


        String dateFormat = "dd-MM-yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        System.out.println("DATE: " + simpleDateFormat.format(calendar.getTime()) + " : " + milliSeconds);
        return simpleDateFormat.format(calendar.getTime());


    }

    public long getTimeinLong(String time) {
        String[] strArray = time.split(":");
        int hh = Integer.parseInt(strArray[0]);
        int mm = Integer.parseInt(strArray[1]);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hh);
        calendar.set(Calendar.MINUTE, mm);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();

    }

    public String getTimeFromMiliSecond(long milliSeconds) {

        String dateFormat = "kk:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String date = simpleDateFormat.format(calendar.getTime());
        System.out.println("DATE: " + simpleDateFormat.format(calendar.getTime()) + " : " + milliSeconds);
        return date;


    }

    public Calendar calendarWithTimeZone() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return calendar;
    }

    public void speakMessage(final Locale loc, String message) {

        tts = new TextToSpeech(mContext, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                tts.setLanguage(loc);
                tts.setPitch(1.1f);

            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, "1");
        } else {
            tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
        }

    }

    public void animateView(View view, int animationType) {
        Animation animation = AnimationUtils.loadAnimation(mContext, animationType);
        animation.reset();
        view.clearAnimation();
        view.setAnimation(animation);

    }

    public String getSpace(int length, String str) {

        String retString = new String(new char[length]).replace('\0', ' ') +
                str;

        return retString;
    }

    public String getTail(int length, String str) {

        String retString = new String(new char[length]).replace('\0', ' ') + str;

        return retString;
    }

    public String getCenterAveragedetails(AverageReportDetail averageReport) {
        FormatPrintRecords formatPrintRecords = new FormatPrintRecords(mContext);

        int totalLength = 4;
        String stringAverage;


        stringAverage = getSpace(totalLength, "Total Centers")
                + getTail(10, String.valueOf(averageReport.totalMember)) + "\n"
                + getSpace(totalLength, "Total Accepted")
                + getTail(7, String.valueOf(averageReport.totalAcceptedEntries)) + "\n"
                + getSpace(totalLength, "Total Rejected")
                + getTail(8, String.valueOf(averageReport.totalRejectedEntries)) + "\n"
                + getSpace(totalLength, "Total Qty")
                + getTail(18, String.valueOf(averageReport.totalQuantity) + " " + Util.getTheUnit(mContext, Util.UNIT_FOR_QUANTITY)) + "\n"
                + getSpace(totalLength, "Total Amount")
                + getTail(10, String.valueOf(averageReport.totalAmount) + " Rs")
                + getSpace(totalLength, "Average Fat %")
                + getTail(10, String.valueOf(averageReport.avgFat))
                + getSpace(totalLength, "Average SNF %")
                + getTail(10, String.valueOf(averageReport.avgSnf))
                + getSpace(totalLength, "Average Rate")
                + getTail(10, String.valueOf(averageReport.avgRate))
                + "\n" + "\n";

        stringAverage = stringAverage + formatPrintRecords.createSeparatorThermal('-');
        return stringAverage;
    }

    public String getSummary(AverageReportDetail avgReportdetail) {

        String strBuild = "";
        FormatPrintRecords formatPrintRecords = new FormatPrintRecords(mContext);
        strBuild = strBuild + "\n";

        if (avgReportdetail != null) {
            strBuild = strBuild + formatPrintRecords.rightPad("Total Farmers", 25)
                    + avgReportdetail.totalMember + "\n"
                    + formatPrintRecords.rightPad("Total Accepted", 25)
                    + avgReportdetail.totalAcceptedEntries + "\n"
                    + formatPrintRecords.rightPad("Total Rejected", 25)
                    + avgReportdetail.totalRejectedEntries + "\n"
                    + formatPrintRecords.rightPad("Average Amount", 25)
                    + avgReportdetail.avgAmount + "\n"
                    + formatPrintRecords.rightPad(formatPrintRecords.getTheUnit(), 25)
                    + avgReportdetail.avgQuantity + "\n"
                    + formatPrintRecords.rightPad("Average Rate", 25)
                    + avgReportdetail.avgRate + "\n"
                    + formatPrintRecords.rightPad("Average Fat", 25)
                    + avgReportdetail.avgFat + "\n"
                    + formatPrintRecords.rightPad("Average SNF", 25)
                    + avgReportdetail.avgSnf + "\n"
                    + formatPrintRecords.rightPad("Total Qty", 25)
                    + avgReportdetail.totalQuantity + "\n"
                    + formatPrintRecords.rightPad("Total Amount", 25)
                    + avgReportdetail.totalAmount + "\n" + "\n";
        }

        strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');
        return strBuild;
    }

    public String receiptFormat(ReportEntity reportEntity) {
        FormatPrintRecords formatPrintRecord = new FormatPrintRecords(mContext);
        String printData = formatPrintRecord.receiptFormat(mContext, reportEntity);
        return printData;

    }

    public String getRateFromRateChart(MilkAnalyserEntity maEntity, String rateChartName) {
        String rate = "0.00";
        if (!amcuConfig.getIsRateChartMandatory()) {
            return "0.00";
        }
        try {
            DatabaseManager dbManager = new DatabaseManager(mContext);
            rate = dbManager.getRateForGivenParams(maEntity.fat, maEntity.snf, maEntity.clr, rateChartName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (rate == null) {
            rate = "0.00";
        }
        return rate;
    }

    public ReportEntity setAcceptOrRejectStatus(ReportEntity reportEntity) {
        MilkAnalyserEntity milkAnalyserEntity = new MilkAnalyserEntity();
        milkAnalyserEntity.fat = reportEntity.getRateCalculationFat();
        milkAnalyserEntity.snf = reportEntity.getRateCalculationSnf();
        milkAnalyserEntity.clr = reportEntity.getRateCalculationClr();
        milkAnalyserEntity.protein = reportEntity.getRateCalculationProtein();
        milkAnalyserEntity.lactose = reportEntity.getLactose();

        boolean isReject = isMilkRejected(milkAnalyserEntity);

        if (isReject) {
            reportEntity.status = "Reject";
        } else {
            reportEntity.status = "Accept";
        }

        return reportEntity;

    }


//    /**
//     * To set txt drawable on TextView
//     * @param textView
//     * @param code
//     */
//
//    public void toSetDrawableOnText(TextView textView,String code,float fontSize,int padding)
//    {
//        textView.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code,fontSize), null, null, null);
//        textView.setCompoundDrawablePadding(padding);
//    }

    /**
     * Based on fat and snf decide whether milk should be accepted or rejected
     *
     * @param maEntity
     */
    public boolean isMilkRejected(MilkAnalyserEntity maEntity) {
        DecimalFormat decimalFormatRateFat;
        DecimalFormat decimalFormatRateSNF;

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        decimalFormatRateFat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.FAT);
        decimalFormatRateSNF = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.SNF);

/*
        maEntity.fat = String.valueOf(decimalFormatRateFat.format(Double.parseDouble(maEntity.fat)));
        maEntity.snf = String.valueOf(decimalFormatRateSNF.format(Double.parseDouble(maEntity.snf)));*/

        boolean isReject = false;
        DecimalFormat decimalFormat = new DecimalFormat("##.0");

        // Fat and snf in min/max limit will be accepted
        if (amcuConfig.getSmartCCFeature()) {
            //No rejection if smartCC feature is enable
            return isReject;
        } else if (!amcuConfig.getIsRateChartMandatory()) {
            if (sessionManager.getMilkType().equalsIgnoreCase("COW")) {
                if (maEntity.fat >
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxFatRejectCow()))
                        || maEntity.fat <
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMinFatRejectCow()))) {
                    isReject = true;
                } else if (maEntity.snf >
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxSnfRejectCow()))
                        || maEntity.snf <
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMinSnfRejectCow()))) {
                    isReject = true;
                } else if (amcuConfig.getLactoseBasedRejection() &&
                        maEntity.lactose > Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxLactoseRejectCow()))) {
                    isReject = true;
                }
            } else if (sessionManager.getMilkType().equalsIgnoreCase("Buffalo")) {
                if (maEntity.fat >
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxFatRejectBuff()))
                        || maEntity.fat <
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMinFatRejectBuff()))) {
                    isReject = true;
                } else if (maEntity.snf >
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxSnfRejectBuff()))
                        || maEntity.snf <
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMinSnfRejectBuff()))) {
                    isReject = true;
                } else if (amcuConfig.getLactoseBasedRejection() &&
                        maEntity.lactose > Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxLactoseRejectBuff()))) {
                    isReject = true;
                }
            } else if (sessionManager.getMilkType().equalsIgnoreCase("Mixed")) {
                if (maEntity.fat >
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxFatRejectMix()))
                        || maEntity.fat <
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMinFatRejectMix()))) {
                    isReject = true;
                } else if (maEntity.snf >
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxSnfRejectMix()))
                        || maEntity.snf <
                        Double.valueOf(decimalFormat.format(amcuConfig.getKeyMinSnfRejectMix()))) {
                    isReject = true;
                } else if (amcuConfig.getLactoseBasedRejection() &&
                        maEntity.lactose > Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxLactoseRejectMix()))) {
                    isReject = true;
                }
            }


        }
        //All fat and snf value accepted
        else if (!amcuConfig.getEnableRejectForRateChart()) {

        }
        //Accepted only if rate greater than zero
        // for corresponding fat and snf
        else {
            String rate = getRateFromRateChart(maEntity, amcuConfig.getRateChartName());

            if (Double.parseDouble(rate) <= 0) {
                isReject = true;
            }
        }
        if (amcuConfig.getLactoseBasedRejection()) {
            if (sessionManager.getMilkType().equalsIgnoreCase("COW") &&
                    maEntity.lactose > Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxLactoseRejectCow())))
                isReject = true;
            else if (sessionManager.getMilkType().equalsIgnoreCase("Buffalo") &&
                    maEntity.lactose > Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxLactoseRejectBuff())))
                isReject = true;
            else if (sessionManager.getMilkType().equalsIgnoreCase("Mixed") &&
                    maEntity.lactose > Double.valueOf(decimalFormat.format(amcuConfig.getKeyMaxLactoseRejectMix())))
                isReject = true;
        }
        return isReject;
    }

    public MilkAnalyserEntity getMAEntity(double fat, double Snf, double clr) {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        maEntity.fat = fat;
        maEntity.snf = Snf;
        maEntity.clr = clr;
        return maEntity;
    }

    public boolean checkForSupervisor() {
        String query = "Select " + DatabaseHandler.KEY_USER_USERID
                + " From " + DatabaseHandler.TABLE_USER
                + " Where " + DatabaseHandler.KEY_USER_ROLE
                + " = '" + UserRole.SUPERVISOR + "'";

        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();
        boolean returnValue = databaseHandler.checkForSupervisor(query);
        return returnValue;
    }

    /**
     * Earlier apk user was exist as a role supervisor
     */
    public void updateSupervisorToTankerSupervisor() {

        int versionCode = -1;
        try {
            versionCode = mContext.getApplicationContext().getPackageManager().getPackageInfo(
                    mContext.getApplicationContext().getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String updateQuery = "Update " + DatabaseHandler.TABLE_USER
                + " set " + DatabaseHandler.KEY_USER_ROLE +
                " ='" + UserRole.TANKER_SUPERVISOR + "'" + " Where " +
                DatabaseHandler.KEY_USER_ROLE + "='" + UserRole.SUPERVISOR + "'";
        if (versionCode != -1 && versionCode <= 160) {
            DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();
            databaseHandler.updateSupervisorToTankerSuper(updateQuery);
        }


    }

    public void selectSpinnerPosition(Spinner spinner, String item) {
        ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
        int spinnerPosition = myAdap.getPosition(item);
        spinner.setSelection(spinnerPosition);
    }

    public double convertHexToDecimal(String hex) {
        hex = "0x" + hex;
        int intHex = Integer.decode(hex);
        float f = Float.intBitsToFloat(intHex);
        return Double.valueOf(f);
    }

    public int convertHexToInt(String hex) {
        hex = "0x" + hex;
        int intHex = Integer.decode(hex);
        return intHex;
    }

    public boolean checkPortValidation() {
        ArrayList<String> arrDevice = new ArrayList<>();

        if (!amcuConfig.getKeyDevicePort1().equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            arrDevice.add(amcuConfig.getKeyDevicePort1());
        }
        if (!amcuConfig.getKeyDevicePort2().equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            arrDevice.add(amcuConfig.getKeyDevicePort2());
        }
        if (!amcuConfig.getKeyDevicePort3().equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            arrDevice.add(amcuConfig.getKeyDevicePort3());
        }
        if (!amcuConfig.getKeyDevicePort4().equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            arrDevice.add(amcuConfig.getKeyDevicePort4());
        }
        if (!amcuConfig.getKeyDevicePort5().equalsIgnoreCase(DeviceName.NO_CONNECTION)) {
            arrDevice.add(amcuConfig.getKeyDevicePort5());
        }

        TreeSet<String> treeSet = new TreeSet<>();
        for (int i = 0; i < arrDevice.size(); i++) {
            treeSet.add(arrDevice.get(i));
        }

        if (arrDevice.contains(DeviceName.MA2) && !(arrDevice.contains(DeviceName.MILK_ANALYSER)
                || arrDevice.contains(DeviceName.MILK_ANALYSER))) {
            Util.displayErrorToast("MA1 port is missing! ", mContext);
            return false;

        } else if (arrDevice.size() == treeSet.size()) {
            return true;
        } else {

            Util.displayErrorToast("Same device selected for multiple port! ", mContext);
            return false;
        }


    }

    public void setCollectionStartData(ReportEntity reportEntity) {
        sessionManager.setTXNumber(reportEntity.txnNumber);
        amcuConfig.setCollectionEndShift(false);
        amcuConfig.setLastShiftDate(reportEntity.postDate);
        amcuConfig.setCollectionEndShift(false);
        amcuConfig.setLastShift(reportEntity.postShift);
        amcuConfig.setEndShiftSuccess(false);

    }

    public void endShiftFunction(Context context) {


        AmcuConfig.getInstance().setCollectionEndShift(true);
//Pass the intent
        Intent i = new Intent(context, PostCollectionRecordsService.class);
        i.putExtra(PostCollectionRecordsService.END_SHIFT, true);
        context.startService(i);

    }

    public boolean isSessionTimeOver(String currentShift) {
        if (!amcuConfig.getKeyEnableCollectionConstraints()) {
            return false;
        } else if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING) && (System.currentTimeMillis() >
                getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift()))) {

            //Morning shift is over
            return true;
        } else if (currentShift.equalsIgnoreCase(AppConstants.Shift.EVENING) &&
                (System.currentTimeMillis()
                        > getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift()))) {
            //Evening shift is over
            return true;
        } else {
            return false;
        }

    }

    public boolean checkForValidCollection(String currentShift) {

        if (currentShift.equalsIgnoreCase("MORNING")) {

            if (System.currentTimeMillis() <
                    getstartOrShiftTime(amcuConfig.getKeyCollStartMrnShift())) {
                long time = getstartOrShiftTime(amcuConfig.getKeyCollStartMrnShift())
                        - System.currentTimeMillis();
                Util.displayErrorToast("Morning session will start at " + amcuConfig.getKeyCollStartMrnShift(), mContext);
                return false;
            } else if (System.currentTimeMillis() > getstartOrShiftTime(amcuConfig.getKeyCollStartMrnShift())
                    &&
                    System.currentTimeMillis() < getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift())) {
                long seconds = (getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift())
                        - System.currentTimeMillis()) / 1000;
                int mins = (int) (seconds / 60);
                String time = String.format("%02d:%02d:%02d", mins / 60, mins % 60, seconds % 60);


                Util.displayErrorToast("Time remaining to complete the morning session " + time, mContext);
                return true;
            } else if (System.currentTimeMillis() > getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift())) {
                long time = System.currentTimeMillis()
                        - getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift());
                Util.displayErrorToast("Morning session has finished at " +
                        amcuConfig.getKeyCollEndMrnShift(), mContext);
                return false;
            }
        } else {
            if (System.currentTimeMillis() < getstartOrShiftTime(amcuConfig.getKeyCollStartEvnShift())) {
                long time = getstartOrShiftTime(amcuConfig.getKeyCollStartEvnShift()) - System.currentTimeMillis();

                Util.displayErrorToast("Evening session will start at " + amcuConfig.getKeyCollStartEvnShift(), mContext);
                return false;
            } else if (System.currentTimeMillis() > getstartOrShiftTime(amcuConfig.getKeyCollStartEvnShift())
                    && System.currentTimeMillis() < getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift())) {

                long seconds = (getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift())
                        - System.currentTimeMillis()) / 1000;
                int mins = (int) (seconds / 60);
                String time = String.format("%02d:%02d:%02d", mins / 60, mins % 60, seconds % 60);

                Util.displayErrorToast("Time remaining to complete the evening session " + time, mContext);
                return true;
            } else if (System.currentTimeMillis() > getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift())) {
                long time = System.currentTimeMillis()
                        - getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift());
                Util.displayErrorToast("Evening session has finished at " +
                        amcuConfig.getKeyCollEndEvnShift(), mContext);
                return false;
            }
        }

        return true;

    }

    public void alertForEndShift(final EndShiftListener endShiftListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        FORCE_SHUTDOWN = false;

        View view = layoutInflater.inflate(R.layout.alert_unsent_records, null);

        TextView tvEndShiftStatus, tvUnsentRecords, tvUnsentUpdatedRecords,
                tvTotalUnsent, tvHeader;
        LinearLayout lnTotalUnsent, lnUpdatedUnsent;

        Button btnSend, btnCancel;

        tvHeader = (TextView) view.findViewById(R.id.tvheader);
        tvEndShiftStatus = (TextView) view.findViewById(R.id.tvEndShift);
        tvTotalUnsent = (TextView) view.findViewById(R.id.tvTotalUnsent);
        tvUnsentRecords = (TextView) view.findViewById(R.id.tvCollectionUnsent);
        tvUnsentUpdatedRecords = (TextView) view.findViewById(R.id.tvUpdatedUnsent);
        lnTotalUnsent = (LinearLayout) view.findViewById(R.id.lnTotalUnsent);
        lnUpdatedUnsent = (LinearLayout) view.findViewById(R.id.lnUpdatedRecords);


        int collectionUnsent = dbh.getAllNWUnsentRecordsCount();
        int updatedUnsent = dbh.getAllUpdatedUnsentRecordsCount();
        if (updatedUnsent > 0) {
            updatedUnsent = updatedUnsent / 2;
        }
        int totalUnsent = collectionUnsent + updatedUnsent;

        btnSend = (Button) view.findViewById(R.id.btnSend);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setText("Shut down");
        btnSend.setText("End Shift");

        btnSend.requestFocus();

        tvEndShiftStatus.setText(String.valueOf(
                amcuConfig.getEndShiftSuccess()).toUpperCase());
        tvUnsentRecords.setText(String.valueOf(collectionUnsent));
        tvTotalUnsent.setText(String.valueOf(totalUnsent));
        tvUnsentUpdatedRecords.setText(String.valueOf(updatedUnsent));
        tvHeader.setText("End Shift Alert");

        if (!amcuConfig.getAllowVisibilityReportEditing()) {
            lnTotalUnsent.setVisibility(View.GONE);
            lnUpdatedUnsent.setVisibility(View.GONE);
        }

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.isNetworkAvailable(mContext)) {
                    AfterLogInTask alt = new AfterLogInTask(mContext);
                    alt.registerEndShiftAlarm(System.currentTimeMillis());
                    alertDialog.dismiss();
                    if (endShiftListener != null)
                        endShiftListener.onEndShiftStart();
                    alertForProgressBar();
                } else {
                    Util.displayErrorToast("Please check the network connectivity!", mContext);
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FORCE_SHUTDOWN = true;
                Util.shutDownTab(mContext, null);
                alertDialog.dismiss();
            }
        });

        builder.setView(view);


        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 650;
        lp.height = 450;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);

    }

    public void alertForAverageReport(AverageReportDetail averageReportDetail) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.layout_summary, null);

        TextView tvTotalCollection, tvTotalAccept, tvTotalReject, tvAvgFat, tvAvgSnf,
                tvAvgRate, tvTotalQty, tvtotalAmount, tvTotalNoc;
        tvTotalCollection = (TextView) view.findViewById(R.id.tvTotalCollection);
        tvTotalAccept = (TextView) view.findViewById(R.id.tvTotalAccept);
        tvTotalReject = (TextView) view.findViewById(R.id.tvTotalReject);
        tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
        tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
        tvAvgRate = (TextView) view.findViewById(R.id.tvAvgRate);
        tvTotalQty = (TextView) view.findViewById(R.id.tvTotalQty);
        tvtotalAmount = (TextView) view.findViewById(R.id.tvtotalAmount);
        tvTotalNoc = (TextView) view.findViewById(R.id.tvTotalCans);

        TextView tvtotalKGFat = (TextView) view.findViewById(R.id.tvTotalFatKg);
        TextView tvTotalKGSNF = (TextView) view.findViewById(R.id.tvtotalKgSnf);

        CardView cvTotalKGFat = (CardView) view.findViewById(R.id.cvTotalKGFat);
        CardView cvTotalKGSNF = (CardView) view.findViewById(R.id.cvTotalKGSnf);
        CardView cvTotalAmount = (CardView) view.findViewById(R.id.cvTotalAmount);
        CardView cvTotalAvgRate = (CardView) view.findViewById(R.id.cvTotalKGAvgRate);

        tvTotalAccept.setText(String.valueOf(averageReportDetail.totalAcceptedEntries));
        tvTotalCollection.setText(String.valueOf(averageReportDetail.totalEntries));
        tvTotalReject.setText(String.valueOf(averageReportDetail.totalRejectedEntries));
        tvAvgFat.setText(String.valueOf(averageReportDetail.avgFat));
        tvAvgSnf.setText(String.valueOf(averageReportDetail.avgSnf));
        tvAvgRate.setText(String.valueOf(averageReportDetail.avgRate));
        tvTotalQty.setText(String.valueOf(averageReportDetail.totalQuantity));
        tvtotalAmount.setText(String.valueOf(averageReportDetail.totalAmount));
        tvTotalNoc.setText(String.valueOf(averageReportDetail.totalCans));
        tvTotalKGSNF.setText(String.valueOf(averageReportDetail.totalSnfKg));
        tvtotalKGFat.setText(String.valueOf(averageReportDetail.totalFatKg));
        if (amcuConfig.getKeyAllowDisplayRate()) {
            cvTotalAmount.setVisibility(View.VISIBLE);
            cvTotalAvgRate.setVisibility(View.VISIBLE);
            cvTotalKGFat.setVisibility(View.GONE);
            cvTotalKGSNF.setVisibility(View.GONE);
        } else {
            cvTotalAmount.setVisibility(View.GONE);
            cvTotalAvgRate.setVisibility(View.GONE);
            cvTotalKGFat.setVisibility(View.VISIBLE);
            cvTotalKGSNF.setVisibility(View.VISIBLE);
        }

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    alertDialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 750;
        lp.height = 450;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);


    }

    public void lowBatteryAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        view = layoutInflater.inflate(R.layout.battery_alert, null);


        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 750;
        lp.height = 450;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);


        handler = new android.os.Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Util.shutDownTab(mContext, null);
            }
        }, 10000);

    }

    public void alertForProgressBar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        view = layoutInflater.inflate(R.layout.alert_progress_layout, null);

        ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
        final TextView textView = (TextView) view.findViewById(R.id.tvMessage);

        textView.setText("Attempting for end shift..");

        mprogressBar = (ProgressBar) view.findViewById(R.id.circular_progress_bar);
        anim = ObjectAnimator.ofInt(mprogressBar, "progress", 0, 70);
        anim.setDuration(15 * 1000);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
        anim.setRepeatMode(ValueAnimator.RESTART);

        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 450;
        lp.height = 450;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);


        runnable = new Runnable() {
            @Override
            public void run() {

                count = count + 1;


                if (amcuConfig.getEndShiftSuccess()) {
                    mprogressBar.setVisibility(View.GONE);
                    ivStatus.setVisibility(View.VISIBLE);
                    ivStatus.setBackgroundResource(R.drawable.success);
                    ivStatus.requestFocus();
                    textView.setText("Shift ended successfully!");
                } else if (count < 3) {
                    mprogressBar.clearAnimation();
                    anim.start();
                    handler = new android.os.Handler(Looper.getMainLooper());
                    handler.postDelayed(runnable, 10 * 1000);
                } else {
                    mprogressBar.setVisibility(View.GONE);
                    ivStatus.setVisibility(View.VISIBLE);
                    ivStatus.setBackgroundResource(R.drawable.failed);
                    ivStatus.requestFocus();
                    textView.setText("End shift failed, please try again!");
                }
            }
        };

        ivStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });


        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (((keyEvent.getAction() == KeyEvent.ACTION_UP)
                        && (keyCode == KeyEvent.KEYCODE_ENTER))) {
                    alertDialog.dismiss();
                } else if ((keyEvent.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    alertDialog.dismiss();
                }
                return true;
            }
        });

        final android.os.Handler handler = new android.os.Handler(Looper.getMainLooper());

        handler.postDelayed(runnable, 15 * 1000);
    }

    public String getTareMessage() {
        return StringEscapeUtils.unescapeJava(amcuConfig.getTareCommand());
    }

    public String getStringfromSpecialChars(String data) {
        String returnString = "";
        if (data.equalsIgnoreCase("CRLF")) {
            returnString = "\r\n";
        } else if (data.equalsIgnoreCase("CR")) {
            returnString = "\r";
        } else if (data.equalsIgnoreCase("LF")) {
            returnString = "\n";
        } else if (data.equalsIgnoreCase("TAB")) {
            returnString = "\t";
        } else if (data.equalsIgnoreCase("NUL")) {
            returnString = "\0";
        } else if (data.equalsIgnoreCase("BS")) {
            returnString = "\b";
        } else if (data.equalsIgnoreCase("HT")) {
            returnString = "\t";
        } else if (data.equalsIgnoreCase("FF")) {
            returnString = "\f";
        } else if (data.equalsIgnoreCase("ESC")) {
            returnString = "\u001B";
        }
//
        else if (data.equalsIgnoreCase("SOH")) {
            returnString = "\u0001";
        } else if (data.equalsIgnoreCase("STX")) {
            returnString = "\u0002";
        } else if (data.equalsIgnoreCase("ETX")) {
            returnString = "\u0003";
        } else if (data.equalsIgnoreCase("EOT")) {
            returnString = "\u0004";
        } else if (data.equalsIgnoreCase("ENQ")) {
            returnString = "\u0005";
        } else if (data.equalsIgnoreCase("ACK")) {
            returnString = "\u0006";
        } else if (data.equalsIgnoreCase("BEL")) {
            returnString = "\u0007";
        } else if (data.equalsIgnoreCase("VT")) {
            returnString = "\u000B";
        } else if (data.equalsIgnoreCase("SO")) {
            returnString = "\u000E";
        } else if (data.equalsIgnoreCase("SI")) {
            returnString = "\u000F";
        }


        return returnString;
    }

    public void setDrawableParams(int color, float size) {
        hintColor = color;
        hintSize = size;
    }

    /**
     * to set txt drawable in edit text
     *
     * @param edit
     * @param code
     */
    public void toSetDrawableOnEdit(EditText edit, String code) {
        edit.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code, hintColor, hintSize), null, null, null);
        edit.setCompoundDrawablePadding(code.length() * 10);
    }

    /**
     * To set txt drawable on TextView
     *
     * @param textView
     * @param code
     */

    public void toSetDrawableOnText(TextView textView, String code) {
        textView.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code, hintColor, hintSize), null, null, null);
        textView.setCompoundDrawablePadding(code.length() * 10);
    }

    public long getDateInMilli(int day, int month, int year) {
        Calendar calendar = getCalendarInstance(0);

        calendar.set(year, month, day);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    public long getDateInMilliSeconds(String inputDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        return millis;
    }

    public boolean dateValidator(String date, String format) {
        boolean returnValue = false;
        DateFormat formatter = new SimpleDateFormat(format);
        formatter.setLenient(false);
        try {
            Date testDate = formatter.parse(date);
            String formatDate = formatter.format(testDate);

            if (date.equals(formatter.format(testDate))) {
                returnValue = true;
            }


        } catch (ParseException e) {
            //If input date is in different format or invalid.
        }

        return returnValue;
    }

    public void checkForNtpTime() {
        timeHandler = new android.os.Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                SntpClient sntpClient = new SntpClient();
                sntpClient.requestTime("2.android.pool.ntp.org", 3000);

                ntpTime = sntpClient.getNtpTime();
                elapsedTime = sntpClient.getNtpTimeReference();
                roundTripTime = sntpClient.getRoundTripTime();

                timeHandler.post(timeRunnable);


            }
        }).start();

        timeRunnable = new Runnable() {
            @Override
            public void run() {

                if (ntpTime > 0) {
                    Util.displayErrorToast("NTP time +" + ntpTime, mContext);
                    toSetTabTime(ntpTime);
                } else {
                    Util.displayErrorToast("Invalid time try again!", mContext);
                }

            }
        };

    }

    public void toSetTabTime(long milliTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliTime);
        //  c.set(2016, 1, 11, 12, 34, 56);
        try {
            AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
            am.setTime(c.getTimeInMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Is the tab time is valid,
     * this should be greater than last collection time and registed time
     *
     * @return
     */
    public boolean isValidTabTime() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        long currentTime = System.currentTimeMillis();
        long registerdTime = dbh.toGetTabRegisteredDate();
        long lastCollectionTime = dbh.toGetLastCollectionTime();

        if (currentTime > registerdTime &&
                currentTime > lastCollectionTime) {
            return true;
        } else {
            //  Util.displayErrorToast("Please set the current time in the TAB",mContext);
            return true;
        }
    }

    /**
     * To get Enhanced white list url from the JSON string
     *
     * @param licenseJson
     * @return
     */

    public ArrayList<WhiteListUrl> getEnhancedWhiteListUrl(String licenseJson) {
        ArrayList<WhiteListUrl> allWhiteListUrl = new ArrayList<>();
        try {

            JSONObject reader = new JSONObject(licenseJson);
            JSONObject result = reader.getJSONObject("result");
            JSONArray jsonArray = result.getJSONArray("whiteListURLEnhanced");

            if (jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    WhiteListUrl whiteListUrl = new WhiteListUrl();
                    whiteListUrl.ports = new ArrayList<>();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    JSONArray jsonSubArray = jsonObject.getJSONArray("ports");

                    whiteListUrl.uri = jsonObject.getString("uri");
                    if (jsonSubArray != null && jsonSubArray.length() > 0) {
                        for (int j = 0; j < jsonSubArray.length(); j++) {
                            whiteListUrl.ports.add(jsonSubArray.getInt(j));
                        }
                    } else {
                        whiteListUrl.ports.add(SmartCCConstants.HTTP_PORT);
                        whiteListUrl.ports.add(SmartCCConstants.HTTPS_PORT);
                    }
                    allWhiteListUrl.add(whiteListUrl);
                }
            }
        } catch (NullPointerException e1) {

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allWhiteListUrl;
    }

    public String getDateFormatFromMillis(String dateFormat, long milliSeconds) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    public ArrayList<String> getDateAndShiftList() {


        ArrayList<String> allPossibleDateShift = new ArrayList<>();
        // int MAX_SHIFT=saveSession.getNumberOfShiftEdited();
        int MAX_SHIFT = 2;

        String currentDate = getReportDate(0);
        String currentShift = Util.getCurrentShift();

        switch (MAX_SHIFT) {

            case 0: {
                break;
            }
            case 1: {
                allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + currentShift);
                break;
            }
            case 2: {
                if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {

                    allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + currentShift);
                    currentDate = Util.getOperationDate(-1, 2);
                    allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.EVENING);
                } else {
                    allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + currentShift);
                    allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.MORNING);
                }
                break;
            }

            default: {

                if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {

                    int shiftCheck = MAX_SHIFT - 1;
                    int reminderCheck = shiftCheck % 2;
                    shiftCheck = shiftCheck / 2;
                    int totalDayToCheck = 0;

                    allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + currentShift);

                    for (int i = 1; i <= shiftCheck; i++) {
                        String changeDate = Util.getOperationDate(-i, 2);
                        allPossibleDateShift.add(changeDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.EVENING);
                        allPossibleDateShift.add(changeDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.MORNING);
                        totalDayToCheck = totalDayToCheck + 1;
                    }

                    if (reminderCheck == 1) {
                        String changeDate = Util.getOperationDate(-(totalDayToCheck + 1), 2);
                        allPossibleDateShift.add(changeDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.EVENING);
                    }

                } else {

                    int shiftCheck = MAX_SHIFT - 2;
                    int reminderCheck = shiftCheck % 2;
                    shiftCheck = shiftCheck / 2;
                    int totalDayToCheck = 0;


                    allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + currentShift);
                    allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.MORNING);

                    for (int i = 1; i <= shiftCheck; i++) {
                        String changeDate = Util.getOperationDate(-i, 2);
                        allPossibleDateShift.add(changeDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.EVENING);
                        allPossibleDateShift.add(changeDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.MORNING);
                        totalDayToCheck = totalDayToCheck + 1;
                    }

                    if (reminderCheck == 1) {
                        String changeDate = Util.getOperationDate(-(totalDayToCheck + 1), 2);
                        allPossibleDateShift.add(changeDate + AppConstants.DB_SEPERATOR + AppConstants.Shift.EVENING);
                    }
                }
                break;
            }
        }
        return allPossibleDateShift;
    }

    public ShiftConcluded gtConcludedDateAndShift() {
        String date, shift;
        ArrayList<String> allList = getDateAndShiftList();

        if (allList == null || allList.size() == 0) {
            if (AmcuConfig.getInstance().getCollectionEndShift()) {
                date = new SmartCCUtil(mContext).getReportDate(0);
                ;
                shift = Util.getCurrentShift();
            } else {
                shift = Util.getCurrentShift();
                if (shift.equalsIgnoreCase("E")) {
                    date = new SmartCCUtil(mContext).getReportDate(0);
                    shift = "M";
                } else {
                    date = new SmartCCUtil(mContext).getReportDate(-1);
                    shift = "E";

                }
            }

        } else {
            String[] strArray = allList.get(allList.size() - 1).split(AppConstants.DB_SEPERATOR);
            date = strArray[0];
            shift = strArray[1];
        }
        date = new SmartCCUtil(mContext).changeDateFormat(date,
                "dd-MMM-yyyy", "yyyy-MM-dd");
        ShiftConcluded shiftConcluded = new ShiftConcluded();
        if (shift.equalsIgnoreCase("E")) {
            shift = "EVENING";
        } else {
            shift = "MORNING";
        }
        shiftConcluded.date = date;
        shiftConcluded.shift = shift;
        return shiftConcluded;
    }

    public Date getDateFromFormat(String dateString, String dateFormat) {
        Date result = null;
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            result = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * For BACK UP Save report data to SD CARD
     *
     * @param reportEntity
     * @throws IOException
     */

    public void saveReportsOnSdCard(ReportEntity reportEntity) throws IOException {


        String stringReport;
        String date = reportEntity.postDate;
        String shiftReport = reportEntity.postShift;

        SessionManager sessionManager = new SessionManager(mContext);

        if (shiftReport.equalsIgnoreCase("Morning")) {
            shiftReport = "Morning";
        } else {
            shiftReport = "Evening";
        }

        stringReport = sessionManager.getCollectionID() + ","
                + reportEntity.farmerId + ","
                + reportEntity.farmerName + ","
                + sessionManager.getFarmerBarcode() + "," +
                reportEntity.postDate + ","
                + reportEntity.postShift + ","
                + reportEntity.milkType + "," +
                reportEntity.getDisplayFat() + ","
                + reportEntity.getDisplaySnf()
                + "," + reportEntity.rate + ","
                + reportEntity.getRateCalculationQuanity() + ","
                + reportEntity.getTotalAmount() + ","
                + reportEntity.bonus + "," +
                reportEntity.manual
                + "," + reportEntity.status + "," +
                reportEntity.qualityMode + "," +
                reportEntity.quantityMode + ","
                + reportEntity.kgWeight + "," +
                reportEntity.ltrsWeight + "," +
                sessionManager.getUserId() + "," +
                reportEntity.tippingStartTime + "," +
                reportEntity.tippingEndTime + "\n";
        Util.generateNoteOnSD(date + shiftReport + "_shiftReports",
                stringReport, mContext, "smartAmcuReports");

    }

    public ReportEntity getFormattedReportEntity(ReportEntity reportEntity) {
        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        ValidationHelper validationHelper = new ValidationHelper();

        DecimalFormat decimal2Digit, decimalFormatClr, decimalFormatFat, decimalFormatSNF, decimalFormatProtein;
        decimal2Digit = new DecimalFormat("#0.00");
        decimalFormatFat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        decimalFormatSNF = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        decimalFormatClr = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);
        decimalFormatProtein = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.PROTEIN);


   /*     reportEntity.fat = checkValidation.getDoubleFromString(decimalFormatFat
                , reportEntity.fat, 0.0);
        reportEntity.snf = checkValidation.getDoubleFromString(decimalFormatSNF
                , reportEntity.snf, 0.0);
        reportEntity.awm = checkValidation.getDoubleFromString(decimal2Digit
                , reportEntity.awm, 0.0);
        reportEntity.protein = checkValidation.getDoubleFromString(decimalFormatProtein
                , reportEntity.protein, 0.0);
        reportEntity.clr = decimalFormatClr.format(checkValidation.getDoubleFromString(
                reportEntity.clr, 0));*/

        return reportEntity;


    }

    public String readFileFromText(String fileName) throws Exception {
        String returnString = "";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            returnString = sb.toString();
        } finally {
            br.close();
        }

        return returnString;
    }

    public ReportEntity getReportEntity(ReportEntity repEntity) {

        repEntity.sentStatus = CollectionConstants.UNSENT;
        repEntity.sentSmsStatus = CollectionConstants.UNSENT;

        return repEntity;
    }


    public ReportEntity setMaxFatAndSnf(ReportEntity reportEntity) {

        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(
                CollectionConstants.RATECHART_NAME);
        RateDao rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
        long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());
        String maxFat = rateDao.findMaximumFat(null, refId);
        String maxSnf = rateDao.findMaximumSNF(null, refId);

        maxFat = new ValidationHelper().getDoubleFromString(new DecimalFormat("#0.00"), maxFat, 14);
        maxSnf = new ValidationHelper().getDoubleFromString(new DecimalFormat("#0.00"), maxSnf, 14);


        if ((reportEntity.fat > Double.parseDouble(maxFat))) {
            reportEntity.setFat(Double.parseDouble(maxFat));
        }

        if ((reportEntity.getSnf() > Double.parseDouble(maxSnf))) {
            reportEntity.setSnf(Double.parseDouble(maxSnf));
        }
        return reportEntity;
    }

    public ReportEntity getRateFromRateChart(ReportEntity reportEntity) {
        String rate = "0.00";

        MilkAnalyserEntity maEntity = getMAEntity(reportEntity.getRateCalculationFat(),
                reportEntity.getRateCalculationSnf(), reportEntity.getRateCalculationClr());
        rate = getRateFromRateChart(maEntity, amcuConfig.getRateChartName());
        if (rate == null) {
            rate = "0.0";
        }

      /*  Rate = Double.parseDouble(decimalFormatRate.format(Double
                .parseDouble(rate)));*/
        reportEntity.setRate(Double.valueOf(rate));

        if (reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            reportEntity.setIncentiveRate(0);
        } else {
            double incentiveRate = Double.parseDouble(DatabaseHandler.getDatabaseInstance().getIncentiveRate(
                    String.valueOf(reportEntity.getProtein()),
                    amcuConfig.getInCentiveRateChartname()));
            reportEntity.setIncentiveRate(incentiveRate);
        }
        double bonus = 0;

        CollectionHelper collectionHelper = new CollectionHelper(mContext);
        bonus = collectionHelper.getBonusAmount(reportEntity.farmerId, reportEntity.milkType, maEntity);
        reportEntity.setBonusRate(bonus);
        if (Boolean.parseBoolean(amcuConfig.getDynamicRateChartValue())
                && reportEntity.getCollectionType().equalsIgnoreCase(Util.REPORT_TYPE_FARMER)) {
            double dynamicAmt = collectionHelper.getDynamicAmount(sessionManager.getMilkType(), maEntity);
            reportEntity.setDynamicRate(dynamicAmt);
            reportEntity.setRate(reportEntity.getDisplayRate() + reportEntity.getDynamicRate());
        }

        return reportEntity;
    }

    public boolean validateQualityParameters(MilkAnalyserEntity maEntity) {
        if (maEntity.fat >= Util.MAX_FAT_LIMIT || maEntity.snf >= Util.MAX_SNF_LIMIT) {
            Util.displayErrorToast("Invalid fat or snf, enter the Id once again ", mContext);
            return false;
        } else if (!Util.checkIfWaterCode(sessionManager.getFarmerID(), amcuConfig.getFarmerIdDigit()) &&
                amcuConfig.getKeyIgnoreZeroFatSnf()) {
            if (maEntity.fat <= 0 || maEntity.snf <= 0) {
                Util.displayErrorToast("Invalid fat or snf, enter the Id once again ", mContext);
                return false;
            }
        }
        return true;
    }

    public AverageReportDetail getAverageOfReport(ArrayList<ReportEntity> reportEntities, boolean includeSample) {

        AverageReportDetail avgReportDetail = new AverageReportDetail();
        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        if ((reportEntities != null && reportEntities.size() > 0)) {
            int count = 0;
            do {
                avgReportDetail.totalEntries = avgReportDetail.totalEntries + 1;
                if (reportEntities.get(count).status.equalsIgnoreCase(SmartCCConstants.REJECT)) {
                    avgReportDetail.totalRejectedEntries = avgReportDetail.totalRejectedEntries + 1;
                } else if (reportEntities.get(count).status.equalsIgnoreCase(
                        SmartCCConstants.ACCEPT)) {
                    avgReportDetail.totalAcceptedEntries = avgReportDetail.totalAcceptedEntries + 1;
                }
                if (reportEntities.get(count).milkType.equalsIgnoreCase(CattleType.TEST)
                        || reportEntities.get(count).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
                    avgReportDetail.totalTestEntries = avgReportDetail.totalTestEntries + 1;
                } else if (reportEntities.get(count).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
                    avgReportDetail.totalCenter = avgReportDetail.totalCenter + 1;
                } else {
                    avgReportDetail.totalMember = avgReportDetail.totalMember + 1;
                }

                if (!reportEntities.get(count).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE) || includeSample) {
                    avgReportDetail.totalFatKg = avgReportDetail.totalFatKg + reportEntities.get(count).fatKg;
                    avgReportDetail.totalSnfKg = avgReportDetail.totalSnfKg + reportEntities.get(count).snfKg;
                }
                if ((!reportEntities.get(count).milkType.equalsIgnoreCase(
                        CattleType.TEST) && !reportEntities.get(count).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) || includeSample) {
                    avgReportDetail.totalAmount = avgReportDetail.totalAmount +
                            Double.valueOf(Util.getAmount(mContext, reportEntities.get(count).getTotalAmount(),
                                    reportEntities.get(count).bonus));
                    avgReportDetail.totalQuantity = avgReportDetail.totalQuantity
                            + reportEntities.get(count).getPrintAndReportQuantity();

                    avgReportDetail.amountWithIncentive = avgReportDetail.amountWithIncentive
                            + reportEntities.get(count).getAmountWithIncentive();

                    double fat = reportEntities.get(count).fat;
                    double snf = reportEntities.get(count).snf;
                    double quantity = reportEntities.get(count).getPrintAndReportQuantity();

                    double val1 = (fat * quantity) / 100;
                    double val2 = (quantity * snf) / 100;

                    avgReportDetail.avgFat = avgReportDetail.avgFat + val1;
                    avgReportDetail.avgSnf = avgReportDetail.avgSnf + val2;

                    int cans = reportEntities.get(count).numberOfCans;
                    if (cans <= 1000) {
                        avgReportDetail.totalCans = avgReportDetail.totalCans + cans;
                    }
                }
                count++;
            }
            while ((reportEntities != null && count < reportEntities.size()));
            //avgReportDetail.totalEntries = reportEntities.size();
        }
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
        try {

            if (avgReportDetail.totalQuantity > 0) {
                avgReportDetail.avgFat =
                        Double.valueOf(decimalFormat.format(
                                avgReportDetail.avgFat * 100 / avgReportDetail.totalQuantity));
                avgReportDetail.avgSnf = Double.valueOf(decimalFormat.format(
                        avgReportDetail.avgSnf * 100 / avgReportDetail.totalQuantity));
                avgReportDetail.avgRate = Double.valueOf(decimalFormat.format(
                        avgReportDetail.totalAmount / avgReportDetail.totalQuantity));
            }

            if (avgReportDetail.totalAcceptedEntries > 0) {
                avgReportDetail.avgAmount = avgReportDetail.totalAmount / (avgReportDetail.totalAcceptedEntries);

                avgReportDetail.avgQuantity = avgReportDetail.totalQuantity / avgReportDetail.totalAcceptedEntries;

                avgReportDetail.avgAmount = Double.parseDouble(decimalFormat.format(avgReportDetail.avgAmount));
                avgReportDetail.avgRate = Double.parseDouble(decimalFormat.format(avgReportDetail.avgRate));
                avgReportDetail.avgQuantity = Double.parseDouble(decimalFormat.format(avgReportDetail.avgQuantity));
            }
            avgReportDetail.totalAmount = Double.valueOf(chooseDecimalFormat.getAmountDecimalFormat().format(avgReportDetail.totalAmount));
            avgReportDetail.totalQuantity = Double.valueOf(chooseDecimalFormat.getWeightDecimalFormat().format(avgReportDetail.totalQuantity));
            avgReportDetail.amountWithIncentive =
                    Double.valueOf(chooseDecimalFormat.getAmountDecimalFormat().format(
                            avgReportDetail.amountWithIncentive));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return avgReportDetail;
    }


    public interface EndShiftListener {
        void onEndShiftStart();
    }


    public long getDateInMilliSecondsFromFormat(String inputDate, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            sdf.setLenient(false);
            date = sdf.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = date.getTime();
        return millis;
    }



}





