package com.devapp.devmain.util;

import android.app.AlarmManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dbbackup.SecondaryDBObject;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.SntpClient;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import org.apache.commons.lang3.StringEscapeUtils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

import static android.os.Looper.getMainLooper;

/**
 * Created by u_pendra on 20/3/17.
 */


/**
 * Created by Upendra on 10/5/2016.
 */
public class AdvanceUtil {

    public static boolean FORCE_SHUTDOWN = false;
    public static String PRIMARY_SHARED_PREF_PATH = "/data/data/" + SmartCCUtil.PACKAGE_NAME + "/shared_prefs";
    //    public static String SECONDARY_SHARED_PREF_PATH = Environment.getExternalStorageDirectory() + "/.devappdatabase/shared_prefs";
    public static String SECONDARY_SHARED_PREF_PATH = Util.getSDCardPath() + "/.devappdatabase/shared_prefs";
    public static long validTimeDiff = 24 * 60 * 60 * 1000;
    Context mContext;
    AmcuConfig amcuConfig;
    SessionManager sessionManager;
    TextToSpeech tts;
    Runnable timeRunnable;
    long ntpTime;
    long elapsedTime;
    long roundTripTime;

    public AdvanceUtil(Context context) {
        this.mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(context);
    }

    public static String getRuppeSymbol() {
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

    public static long getLongMilisFromDataAndShift(Context context) throws ParseException {

        AdvanceUtil advanceUtil = new AdvanceUtil(context);

        ArrayList<String> allDateAndShiftList = advanceUtil.getDateAndShiftList();
        String dateAndShift = allDateAndShiftList.get(allDateAndShiftList.size() - 1);

        String[] strArray = dateAndShift.split(AppConstants.DB_SEPERATOR);
        String date = strArray[0];
        String shift = strArray[1];

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");
        Date dateF = sdf.parse(date);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateF);

        if (shift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {
            calendar.set(Calendar.HOUR_OF_DAY, 4);

        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 15);
        }

        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();

    }

    public static String getFullShift(String shift) {
        if (shift.equalsIgnoreCase("M")) {
            return "MORNING";
        } else if (shift.equalsIgnoreCase("E")) {
            return "EVENING";
        } else {
            return shift;
        }
    }

    public static long getTodayLongMillis(int hour, int mins, int sec) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, mins);
        calendar.set(Calendar.SECOND, sec);

        return calendar.getTimeInMillis();

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

        if (milkType.equalsIgnoreCase("Buffalo")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForBuffalo());
        } else if (milkType.equalsIgnoreCase("Mixed")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForMixed());

        } else {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForCow());
        }


        return amcuConfig.getRateChartName();
    }

    public Calendar getCalendarInstance(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);

        return calendar;
    }

    /*
     * This will return dd-mmm-yyyy format of date
     * */
    public String getReportFormatDate() {
        Calendar c = getCalendarInstance(0);
        if (isExtendedEveningShift()) {
            c = getCalendarInstance(-1);
        }

        Date date = c.getTime();

        String strDate = String.valueOf(date);
        String DAY;
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DATE);

        if (day > 9) {
            DAY = String.valueOf(day);
        } else {
            DAY = "0" + String.valueOf(day);
        }
        return DAY + "-" + strDate.substring(4, 7) + "-"
                + String.valueOf(year);
    }

    /*public MilkAnalyserEntity getMAEntity(String fat, String Snf, String clr) {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        maEntity.fat = fat;
        maEntity.snf = Snf;
        maEntity.clr = clr;
        return maEntity;
    }*/

    public boolean isExtendedEveningShift() {
        boolean isExtendedEvnShift = false;
        if (Util.getCurrentShift().equalsIgnoreCase("E")) {
            SessionManager session = new SessionManager(mContext);
            final Calendar c = getCalendarInstance(0);
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);

            int time = (Integer.parseInt(Util.padding(hour) + Util.padding(min)));

            if (time >= 0 && time < Integer
                    .parseInt(session.getMorningStart().replace(":", ""))) {
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


    /*public void setCollectionStartData(ReportEntity reportEntity) {
        sessionManager.setTXNumber(Integer.parseInt(reportEntity.txnNumber));
        saveSession.setCollectionEndShift(false);
        saveSession.setLastShiftDate(reportEntity.date);
        saveSession.setLastShift(reportEntity.shift);
        saveSession.setEndShiftSuccess(false);

    }*/

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

    /**
     * Based on fat and snf decide whether milk should be accepted or rejected
     *
     * @param maEntity
     */
    public boolean isMilkRejected(MilkAnalyserEntity maEntity) {
        boolean isReject = false;
        DecimalFormat decimalFormat = new DecimalFormat("##.0");
        String rate = getRateFromRateChart(maEntity, amcuConfig.getRateChartName());

        if (Double.parseDouble(rate) <= 0) {
            isReject = true;
        }

        return isReject;
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

    /**
     * Method to calculate valid TAB time
     *
     * @return true if time is valid
     */
    public boolean isValidTabTime() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        long lastCollectionTime = dbh.toGetLastCollectionTime();

        if (System.currentTimeMillis() < lastCollectionTime) {
            return false;
        } else if ((System.currentTimeMillis() - lastCollectionTime) > validTimeDiff) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validateFatAndSnfForZeroValues(double fat, double snf) {

        if (fat <= 0 && snf <= 0) {
            Util.displayErrorToast("Invalid fat and snf, enter the Id once again ", mContext);
            return false;
        }

        return true;
    }

    /**
     * To pick the last collection and compare it with current time
     *
     * @return
     */
    public String timeDiffBwLastCollectionAndCurrentTime() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        long lastCollectionTime = dbh.toGetLastCollectionTime();
        long timeDiff = System.currentTimeMillis() - lastCollectionTime;

        return getTimeFromMiliSecond(timeDiff);
    }

    public void toSetTabTime(long milliTime) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(milliTime);
        //  c.set(2016, 1, 11, 12, 34, 56);
        AlarmManager am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        am.setTime(c.getTimeInMillis());
    }

    /**
     * to set txt drawable in edit text
     *
     * @param edit
     * @param code
     */
    public void toSetDrawableOnEdit(EditText edit, String code, float fontSize) {
        edit.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code, Color.BLACK, fontSize), null, null, null);
        edit.setCompoundDrawablePadding(code.length() * 10);
    }

    /**
     * To set txt drawable on TextView
     *
     * @param textView
     * @param code
     */

    public void toSetDrawableOnText(TextView textView, String code, float fontSize, int padding) {
        textView.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable(code, Color.BLACK, fontSize), null, null, null);
        textView.setCompoundDrawablePadding(padding);
    }

    public HashMap<Integer, String> setLactoScanfields() {
        //Default
        String[] lactoParams = {amcuConfig.KEY_FAT_VISIBILITY,
                amcuConfig.KEY_SNF_VISIBILITY,
                amcuConfig.KEY_DEN_VISIBILITY,
                amcuConfig.KEY_LAC_VISIBILITY,
                amcuConfig.KEY_sALT_VISIBILITY,
                amcuConfig.KEY_PRO_VISIBILITY,
                amcuConfig.KEY_TEMP_VISIBLITY,
                amcuConfig.KEY_ADW_VISIBILITY,
                amcuConfig.KEY_FRP_VISIBILITY,
                amcuConfig.KEY_CAL_VISIBLITY,
                amcuConfig.KEY_SN_VISIBILITY,
                amcuConfig.KEY_PH_VISIBILITY,
                amcuConfig.KEY_COM_VISIBILITY};

        HashMap<Integer, String> map = new HashMap<>();
        int count = 0;

        if (amcuConfig.getFatVisiblity()) {
            map.put(count, amcuConfig.KEY_FAT_VISIBILITY);
            count++;

        }

        if (amcuConfig.getSnfVisiblity()) {
            map.put(count, amcuConfig.KEY_SNF_VISIBILITY);
            count++;
        }

        if (amcuConfig.getDenVisiblity()) {
            map.put(count, amcuConfig.KEY_DEN_VISIBILITY);
            count++;
        }

        if (amcuConfig.getLACVisiblity()) {
            map.put(count, amcuConfig.KEY_LAC_VISIBILITY);
            count++;
        }

        if (amcuConfig.getSaltVisiblity()) {
            map.put(count, amcuConfig.KEY_sALT_VISIBILITY);
            count++;
        }
        if (amcuConfig.getPROVisiblity()) {
            map.put(count, amcuConfig.KEY_PRO_VISIBILITY);
            count++;
        }

        if (amcuConfig.getTempVisiblity()) {
            map.put(count, amcuConfig.KEY_TEMP_VISIBLITY);
            count++;
        }

        if (amcuConfig.getAdwVisiblity()) {
            map.put(count, amcuConfig.KEY_ADW_VISIBILITY);
            count++;
        }

        if (amcuConfig.getFrpVisiblity()) {
            map.put(count, amcuConfig.KEY_FRP_VISIBILITY);
            count++;
        }

        if (amcuConfig.getCALVisiblity()) {
            map.put(count, amcuConfig.KEY_CAL_VISIBLITY);
            count++;
        }

        if (amcuConfig.getSNVisiblity()) {
            map.put(count, amcuConfig.KEY_SN_VISIBILITY);
            count++;
        }

        if (amcuConfig.getPHVisiblity()) {
            map.put(count, amcuConfig.KEY_PH_VISIBILITY);
            count++;
        }

        if (amcuConfig.getCoMVisiblity()) {
            map.put(count, amcuConfig.KEY_COM_VISIBILITY);
            count++;
        }


        return map;

    }

    public String getMilkType(String id, String milkType) {
        String returnMilkType = "COW";
        if (milkType != null) {
            returnMilkType = milkType;
        }
        return returnMilkType;
    }

    /**
     * To return the appropriate type of collection
     * based on the Entered ID
     *
     * @param id
     * @return
     */
    public String getReportType(String id) {
        String returnRportType = Util.REPORT_TYPE_FARMER;
        if (Util.checkIfSampleCode(id, amcuConfig.getFarmerIdDigit())
                && amcuConfig.getSampleAsCollection()) {
            returnRportType = Util.REPORT_TYPE_SAMPLE;
        } else {
            returnRportType = Util.REPORT_TYPE_FARMER;
        }

        return returnRportType;
    }

    public String getTheUnit() {
        if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {
            return " L";

        } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            return " Kg";
        } else if (amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            return " Kg";
        } else {
            return " L";
        }
    }

    public void checkForNtpTime() {
        final Handler timeHandler = new Handler(getMainLooper());
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
                    toSetTabTime(ntpTime);
                } else {
                    Util.displayErrorToast("Invalid time try again!", mContext);
                }

            }
        };

    }

    public String getMilkType(String fat) {

        String cattleType;

        if ((Util.checkIfRateCheck(sessionManager.getFarmerID(),
                amcuConfig.getFarmerIdDigit())
                || sessionManager.getIsSample())
                && !(Util.checkIfSampleCode(sessionManager.getFarmerID(),
                amcuConfig.getFarmerIdDigit())
                && amcuConfig.getSampleAsCollection())) {
            cattleType = CattleType.TEST;
        } else {
            double fat1 = 0;
            double fat2 = 0;
            try {
                fat1 = Double.parseDouble(fat);
                fat2 = Double.parseDouble(amcuConfig.getChangeFat());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (fat1 >= fat2 && fat2 != 0) {
                cattleType = CattleType.BUFFALO;
            } else {
                cattleType = sessionManager.getMilkType();
            }
        }
        return cattleType;
    }

    /**
     * Get the setting done in Lactoscan
     *
     * @return
     */


    public String getLactoScanParams() {
        String returnData = "";
        if (amcuConfig.getFatVisiblity()) {
            returnData = returnData + AppConstants.FAT + AppConstants.DB_SEPERATOR;
        }

        if (amcuConfig.getSnfVisiblity()) {
            returnData = returnData + AppConstants.SNF + AppConstants.DB_SEPERATOR;
        }

        if (amcuConfig.getDenVisiblity()) {
            returnData = returnData + AppConstants.CLR + AppConstants.DB_SEPERATOR;
            returnData = returnData + AppConstants.DEN + AppConstants.DB_SEPERATOR;
        }

        if (amcuConfig.getLACVisiblity()) {
            returnData = returnData + AppConstants.LAC + AppConstants.DB_SEPERATOR;
        }

        if (amcuConfig.getSaltVisiblity()) {
            returnData = returnData + AppConstants.SALT + AppConstants.DB_SEPERATOR;
        }
        if (amcuConfig.getPROVisiblity()) {
            returnData = returnData + AppConstants.PROTEIN + AppConstants.DB_SEPERATOR;
        }
        if (amcuConfig.getTempVisiblity()) {
            returnData = returnData + AppConstants.TEMP + AppConstants.DB_SEPERATOR;
        }
        if (amcuConfig.getAdwVisiblity()) {
            returnData = returnData + AppConstants.AWM + AppConstants.DB_SEPERATOR;
        }
        if (amcuConfig.getFrpVisiblity()) {
            returnData = returnData + AppConstants.FRP + AppConstants.DB_SEPERATOR;
        }
        if (amcuConfig.getCALVisiblity()) {
            returnData = returnData + AppConstants.CAL + AppConstants.DB_SEPERATOR;
        }
        if (amcuConfig.getSNVisiblity()) {
            returnData = returnData + AppConstants.SN + AppConstants.DB_SEPERATOR;
        }
        if (amcuConfig.getPHVisiblity()) {
            returnData = returnData + AppConstants.PH + AppConstants.DB_SEPERATOR;
        }
        if (amcuConfig.getCoMVisiblity()) {
            returnData = returnData + AppConstants.CON + AppConstants.DB_SEPERATOR;
        }

        return returnData;
    }

    public ArrayList<String> getDateAndShiftList() {


        ArrayList<String> allPossibleDateShift = new ArrayList<>();
        int MAX_SHIFT = amcuConfig.getNumberShiftCanBeEdited();

        String currentDate = Util.getTodayDateAndTime(1);
        String currentShift = Util.getCurrentShift();

        switch (MAX_SHIFT) {

            case 0:

            {
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

    public ArrayList<String> getShiftConcludeDateAndShift() {
        Date date = null;

        ArrayList<String> allPossibleDateShift = new ArrayList<>();
        int MAX_SHIFT = amcuConfig.getNumberShiftCanBeEdited();
        String currentDate = Util.getTodayDateAndTime(1);
        String currentShift = Util.getCurrentShift();

        if (MAX_SHIFT == 0) {
            MAX_SHIFT = 1;
        }
        if (AmcuConfig.getInstance().getCollectionEndShift()) {
            if (amcuConfig.getNumberShiftCanBeEdited() == 0) {
                MAX_SHIFT = MAX_SHIFT - 1;

                allPossibleDateShift.add(currentDate + AppConstants.DB_SEPERATOR + currentShift);

                return allPossibleDateShift;
            }
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        try {
            date = simpleDateFormat.parse(currentDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < MAX_SHIFT; i++) {
            if (currentShift.equals(AppConstants.Shift.MORNING)) {
                currentShift = AppConstants.Shift.EVENING;
                date = new Date(date.getTime() - (24 * 3600 * 1000));
            } else {
                currentShift = AppConstants.Shift.MORNING;
            }
        }

        allPossibleDateShift.add(simpleDateFormat.format(date) + AppConstants.DB_SEPERATOR + currentShift);

        return allPossibleDateShift;
//
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

    public long getDateInMilliFromMilli(long milliTime, int hour, int min, int sec) {
        Calendar calendar = getCalendarInstance(0);

        calendar.setTimeInMillis(milliTime);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, sec);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();

    }


}





