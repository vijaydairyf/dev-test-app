package com.devapp.devmain.user;

// Don't Copy this file


import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.devapp.kmfcommon.AllReportsActivity;
import com.devapp.devmain.agentfarmersplit.AgentSplitEntity;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.agentfarmersplit.QualityParams;
import com.devapp.devmain.agentfarmersplit.QuantityParams;
import com.devapp.devmain.agentfarmersplit.RateParams;
import com.devapp.devmain.cloud.APKManager;
import com.devapp.devmain.cloud.UpdateAPK;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.DispatchRecordDao;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.dao.SalesRecordDao;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.CollectionEntry;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.MessageEntity;
import com.devapp.devmain.entity.PostEndShift;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.RateChartPostEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.RejectionEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.entity.SequenceCollectionRecord;
import com.devapp.devmain.entity.SocietyEntity;
import com.devapp.devmain.entity.UserEntity;
import com.devapp.devmain.entity.WhiteListUrl;
import com.devapp.devmain.entitymanager.FarmerManager;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.main.DeviceListActivity;
import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.MilkAnalyser;
import com.devapp.devmain.postentities.QualityParamsPost;
import com.devapp.devmain.postentities.QualityReadingData;
import com.devapp.devmain.postentities.QuantityParamspost;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.TabAlertSwitchOffService;
import com.devapp.devmain.services.WriteRateChartLogs;
import com.devapp.devmain.util.DateAndTime;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.BuildConfig;
import com.devApp.R;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TimeZone;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.os.Looper.getMainLooper;
import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

//This class contains all necessary entities values and function which
//required frequently !
//This class contains all necessary entities values and function which
//required frequently !
public class Util {

    public final static double MIN_WEIGHT_LIMIT = 1;
    public final static double MAX_WEIGHT_LIMIT = 1000000;
    public final static int sendDailyReport = 1;
    public final static int sendMemberBillRegister = 2;
    public final static int sendMemberBillSummary = 3;
    public final static int sendWeeklyReport = 5;
    public final static int sendMonthlyReport = 6;
    public final static int sendEndShiftReport = 7;
    public final static int sendFarmerList = 10;
    public final static int sendPeriodicReport = 8;
    public final static int GETRATECHART = 11;
    public final static int GETREPORT = 12;
    public final static int LOGINRATECHART = 13;
    public final static int LOGINFORCLOUD = 14;
    public final static int LOGINFORAPK = 15;
    public final static int PERIODICREPORT = 16;
    public final static int PERIODICREPORTINDIVIDUAL = 17;
    public final static int UPDATE_FARMERLIST = 18;
    public final static int SALESREPORT = 19;
    public final static int REPORT_COMMITED = 1;
    public final static int REPORT_NOT_COMMITED = 0;
    public final static int GET_USER = 0;
    public final static int UPDATE_USER = 1;
    public final static int GET_SETTING = 2;
    public final static int UPDATE_SETTING = 3;
    public final static int GET_SOCIETY = 4;
    public final static int UPDATE_SOCIETY = 5;
    public final static String TEST_URL_STELLAPPS = "/smartamcu/images/SmartMoo.png";
    public final static String URL_APPEND = "://";
    public static final int ONLY_ALPHABET = 0;
    public static final int ONLY_NUMERIC = 1;
    public static final int ONLY_DECIMAL = 2;
    public static final int ONLY_ALPHANUMERIC = 3;
    public static final int ALPHANUMERIC_SPACE = 4;
    public static final String REPORT_TYPE_FARMER = "Collection";
    public static final String REPORT_TYPE_FARMER_EDITED = "farmerEdited";
    public static final String REPORT_TYPE_SALES = "Sales";
    public static final String REPORT_TYPE_MCC = "AggregateCollection";
    public static final String REPORT_TYPE_AGENT_SPLIT = "agentSplit";
    public static final String REPORT_TYPE_DISPATCH = "Dispatch";
    public static final String REPORT_TYPE_TOTAL_PERIODIC = "totalPeriodic";
    public static final String RATECHART_TYPE_COLLECTION = "COLLECTION";
    public static final String RATECHART_TYPE_DYNAMIC = "DYNAMIC";
    public static final String RATECHART_TYPE_PROTEIN = "PROTEIN";

    public static final String RATECHART_TYPE_BONUS = "BONUS";
    public static final String SALES_TXN_TYPE_SALES = "SALES";
    public static final String SALES_TXN_TYPE_TRUCK = "COOPERATIVE";
    public static final String SALES_TXN_SUBTYPE_NORMAL = "NORMAL";
    public static final String SALES_TXN_SUBTYPE_COB = "COB";
    public static final String RATE_MODE_MANUAL = "Manual";
    public static final String RATE_MODE_AUTO = "Auto";
    // for Database use to send uncommited record to server
    public final static String RECORD_STATUS_COMPLETE = "COMPLETE";
    public final static String RECORD_STATUS_INCOMPLETE = "INCOMPLETE";
    public final static String USB = "USB";
    public final static String CLOUD = "CLOUD";
    public final static String DEVICE = "DEVICE";
    public final static String ADD = "ADD";
    public final static String DELETE = "DELETE";
    public final static String INVALID = "INVALID";
    public final static String MANUAL = "MANUAL";
    public final static int MAX_MYRATECHAT_LIMIT = 30;
    public static final int UNIT_FOR_QUANTITY = 0;
    public final static String SINGLE = "SINGLE";
    public final static String MULTIPLE = "MULTIPLE";
    public static final double MAX_FAT_LIMIT = 20;
    public static final double MAX_SNF_LIMIT = 20;
    public static final double MIN_FAT_LIMIT = 0;
    public static final double MIN_SNF_LIMIT = 0;
    //For collection or sales
    public static final int CHECK_DUPLICATE_SAMPLEID = 102;
    public static final int CHECK_DUPLICATE_SAMPLEBARCODE = 103;
    public static final int CHECK_DUPLICATE_CENTERCODE = 104;
    public static final int CHECK_DUPLICATE_CENTERBARCODE = 105;
    public static final String REPORT_TYPE_SAMPLE = "Sample";
    public static final int INCENTIVE_RATE_CHART = 50;
    private static final String LICENSE_SERVER = "amculm.smartmoo.com";
    /* reports.crashlytics.com
    settings.crashlytics.com
     api.crashlytics.com
     www.crashlytics.com
     realtime.crashlytics.com﻿
     e.crashlytics.com*/
    private static final String DEVELOPMENT_SERVER = "planner.stellapps.com";
    private static final String FABRIC_SERVER = "fabric.io";
    private static final String CRASHYLITICS_SETTING = "settings.crashlytics.com";
    private static final String CRASHYLITICS_REALTIME = "realtime.crashlytics.com";
    private static final String CRASHYLITICS = "www.crashlytics.com";
    private static final String CRASHYLITICS_REPORTS = "reports.crashlytics.com";
    private static final String CRASHYLITICS_API = "api.crashlytics.com";
    private static final String CRASHYLITICS_E = "e.crashlytics.com";
    private static final String ALL_FABRIC_SERVER = "*.fabric.io";
    private static final String ALL_CRASHYLITICS_SERVER = "*.crashlytics.com";
    private static final String[] Data_list = {"socdata", "ratemap"};
    // Write ratechart entN
    private static final int UNIT_FOR_RATE = 1;
    public static int DEVICE_LOGIN = 0;
    public static int CHECK_RATECHART = 1;
    public static int LAST_SEQ_NUM = 999999;
    public static String PERIODIC_SUBJECT;
    public static String SALES_SUBJECT;
    public static String rootFileName = Environment
            .getExternalStorageDirectory().getAbsolutePath();
    public static AverageReportDetail avgReportDetail;
    public static AverageReportDetail avgSalesCowReport;
    public static AverageReportDetail avgSalesBuffalo;
    public static AverageReportDetail avgSalesMixed;
    public static AverageReportDetail avgChillingDetails;
    public static PostEndShift poEndShift = new PostEndShift();
    public static CollectionEntry poPerFarmer = new CollectionEntry();
    public static String RATECHART_URI;
    public static String CONFIGURATION_URI;
    public static String FARMER_URI;
    public static String APK_URI;
    public static String CHILLING_CENTER_URI;
    public static int APK_DOWNLODED_VERSION;
    public static String downLoadFilePath;
    public static boolean isShutDown;
    public static String LICENSE_URL_V1 = "https://" + LICENSE_SERVER + "/license/v1/validate/";
    public static String LICENSE_URL = "https://" + LICENSE_SERVER + "/license/v2/validate/";
    public static boolean WRITE_RATE_ADD_LOG = true;
    public static boolean isCheckForUpdate;
    public static String INCENTIVE_RATECHART_URI;
    private static AverageReportDetail avgSalesReportDetails;
    private static String Date;
    private static String shift;
    private static Runnable runnable;
    private static InputFilter filter;
    // Read rate chart entity
    private static RejectionEntity rejEntity = new RejectionEntity();

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus()
                .getWindowToken(), 0);
    }

    //getting Amount excluding bonus display
    public static double
    getAmount(Context ctx, double totalAmount, double repBonus) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        double amount;
        amount = totalAmount;
        DecimalFormat decimalFormat = new ChooseDecimalFormat().getAmountDecimalFormat();

        if (amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint()) {
            //This will be amount+bonus
            amount = totalAmount;
        } else {
            double bonus = repBonus;
            double dblbonus = 0;
            double dblAmt = 0;
            dblAmt = amount;
            dblbonus = bonus;

            dblAmt = dblAmt - dblbonus;
            amount = dblAmt;

        }

        return Double.parseDouble(decimalFormat.format(amount));

    }


    public static void WriteRateChartName(Context ctx, ArrayList<String> data,
                                          int type) {

        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        String userID = (new SessionManager(ctx)).getUserId();
        String filename = ctx.getFilesDir().toString() + "" + userID
                + ".ratechart";

        try {
            fOut = new FileOutputStream(new File(filename));
            osw = new ObjectOutputStream(fOut);

            osw.writeObject(data);

            osw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> ReadRateChartNAME(Context ctx, int type) {

        FileInputStream fIn = null;
        ObjectInputStream isr = null;

        ArrayList<String> _list = null;
        String userID = (new SessionManager(ctx)).getUserId();
        String filename = ctx.getFilesDir().toString() + "" + userID
                + ".ratechart";
        File f = new File(filename);
        if (!f.exists())
            return null;

        try {
            fIn = new FileInputStream(filename);
            isr = new ObjectInputStream(fIn);

            _list = (ArrayList<String>) isr.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return _list;

    }

    public static void WriteRateChartEnt(Context ctx,
                                         ArrayList<RateChartEntity> data, int type) {

        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        String userID = (new SessionManager(ctx)).getUserId();
        String filename = ctx.getFilesDir().toString() + "" + userID
                + ".ratechartent";

        try {
            fOut = new FileOutputStream(new File(filename));
            osw = new ObjectOutputStream(fOut);

            osw.writeObject(data);

            osw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<RateChartEntity> ReadRateChartEntity(Context ctx,
                                                                 int type) {

        FileInputStream fIn = null;
        ObjectInputStream isr = null;

        ArrayList<RateChartEntity> _list = null;
        String userID = (new SessionManager(ctx)).getUserId();
        String filename = ctx.getFilesDir().toString() + "" + userID
                + ".ratechartent";
        File f = new File(filename);
        if (!f.exists())
            return null;

        try {
            fIn = new FileInputStream(filename);
            isr = new ObjectInputStream(fIn);

            _list = (ArrayList<RateChartEntity>) isr.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return _list;

    }

    // Write settings
    public static void WriteHashMap(Context ctx,
                                    Map<String, ArrayList<FarmerEntity>> map,
                                    ArrayList<FarmerEntity> data, int type) {

        String fName = Data_list[type];

        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        String userID = (new SessionManager(ctx)).getUserId();
        String filename = ctx.getFilesDir().toString() + fName + userID
                + ".socfarmer";

        try {
            fOut = new FileOutputStream(new File(filename));
            osw = new ObjectOutputStream(fOut);

            osw.writeObject(map);

            osw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static Map<String, ArrayList<FarmerEntity>> ReadSettingsMap(
            Context ctx, int type) {

        String fName = Data_list[type];

        FileInputStream fIn = null;
        ObjectInputStream isr = null;

        Map<String, ArrayList<FarmerEntity>> _list = null;
        String userID = (new SessionManager(ctx)).getUserId();
        String filename = ctx.getFilesDir().toString() + fName + userID
                + ".socfarmer";
        File f = new File(filename);
        if (!f.exists())
            return null;

        try {
            fIn = new FileInputStream(filename);
            isr = new ObjectInputStream(fIn);

            _list = (Map<String, ArrayList<FarmerEntity>>) isr.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return _list;

    }

    //get current session
    public static String getCurrentShift() {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);

        if (amcuConfig.getMorningStart().equalsIgnoreCase("10:00")
                && amcuConfig.getMorningEndTime().equalsIgnoreCase("11:30")) {

            if (hour >= 15 || hour <= 2) {
                shift = "EVENING";
            } else {
                shift = "MORNING";
            }
        } else {

            if ((Integer.parseInt(padding(hour) + padding(min)) >= Integer
                    .parseInt(amcuConfig.getMorningStart().replace(":", "")))
                    && (Integer.parseInt(padding(hour) + padding(min)) < Integer
                    .parseInt(amcuConfig.getEveningStart()
                            .replace(":", "")))) {
                shift = "MORNING";
            } else {
                shift = "EVENING";
            }
        }
        return shift;
    }

    public static String getOperationDate(int i, int chkLong) {
        final Calendar c = Calendar.getInstance();

        String[] strMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec"};

        Date date = c.getTime();
        String strDate = String.valueOf(date);
        String DAY, MONTH, YEAR;

        c.add(Calendar.DATE, i);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DATE);
        int month1 = c.get(Calendar.MONTH);
        int month = 0;

        month = month1 + 1;

        if (day > 9) {
            DAY = String.valueOf(day);
        } else {
            DAY = "0" + String.valueOf(day);
        }

        if (month > 9) {
            MONTH = String.valueOf(month);
        } else {
            MONTH = "0" + String.valueOf(month);
        }

        if (chkLong == 1) {
            return String.valueOf(getDateInLongFormat(DAY + "-" + MONTH + "-"
                    + String.valueOf(year)));
        } else if (chkLong == 2) {
            return DAY + "-" + strMonth[month1] + "-" + String.valueOf(year);
        } else {
            return DAY + "-" + MONTH + "-" + String.valueOf(year);
        }

    }

    // ReadHamshMap
    public static String getTodayDateAndTime(int dTime, Context mContext, boolean report) {
        Calendar c = Calendar.getInstance();
        if (report) {
            SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
            if (smartCCUtil.isExtendedEveningShift()) {
                c = smartCCUtil.getCalendarInstance(-1);
            }
        }

        Date date = c.getTime();

        String strDate = String.valueOf(date);
        String DAY, MONTH;
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int sec = c.get(Calendar.SECOND);

        if (day > 9) {
            DAY = String.valueOf(day);
        } else {
            DAY = "0" + String.valueOf(day);
        }

        month = month + 1;

        if (month > 9) {
            MONTH = String.valueOf(month);
        } else {

            MONTH = "0" + String.valueOf(month);

        }

        String time = padding(hour) + padding(min);
        String time1 = padding(hour) + ":" + padding(min);
        String time2 = padding(hour) + "_" + padding(min);

        if (dTime == 0) {
            return time;
        } else if (dTime == 1) {
            //To return new format yyyy-MM-dd instead of dd-mmm-yyyy
           /* return String.valueOf(year) + "-" + strDate.substring(4, 7) + "-"
                    + DAY;*/
            return String.valueOf(year) + "-" + MONTH + "-"
                    + DAY;

        } else if (dTime == 2) {
            return DAY + "-" + strDate.substring(4, 7) + "-"
                    + String.valueOf(year) + "Time:" + time;
        } else if (dTime == 3) {
            return time1;

        } else if ((dTime == 4) || (dTime == 7)) {
            return DAY + "-" + MONTH + "-" + String.valueOf(year);
        } else if (dTime == 5) {
            return DAY + "_" + strDate.substring(4, 7) + "_"
                    + String.valueOf(year) + "T" + time2;
        } else if (dTime == 6) {
            return time1 + ":" + padding(sec);
        }
        //for dd-mm-yyyy format
       /* else if (dTime == 7) {
            month = month + 1;
            return DAY + "-" + padding(month) + "-" + String.valueOf(year);
        }*/
        else if (dTime == 8) {
            //ONly year
            return String.valueOf(year);
        } else {
            return DAY + "-" + strDate.substring(4, 7) + "-"
                    + String.valueOf(year);
        }

    }

    //    TODO Check if all the invocations are sending date in the correct format
    // To convert string date to long
    public static long getDateInLongFormat(String date) {
        long lDate = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date mDate = null;
        try {
            sdf.setLenient(false);
            mDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            sdf = new SimpleDateFormat("dd-MM-yyyy");
            sdf.setLenient(false);
            try {
                mDate = sdf.parse(date);
            } catch (ParseException e1) {
                e1.printStackTrace();
                sdf = new SimpleDateFormat("dd-MMM-yyyy");
                sdf.setLenient(false);
                try {
                    mDate = sdf.parse(date);
                } catch (ParseException e2) {
                    e2.printStackTrace();
                }
            }
            date = new SimpleDateFormat("yyyy-MM-dd").format(mDate);
        }


        date = date.replace("-", "");
        lDate = Long.valueOf(date);

        return lDate;

    }

    // Get transaction number
    public static String getTxnNumber(int txn) {

        if (txn < 10) {
            return "000" + txn;
        } else if (txn < 100) {
            return "00" + txn;
        } else if (txn < 1000) {
            return "0" + txn;
        } else if (txn > 9999) {
            return "0001";
        } else {
            return String.valueOf(txn);
        }
    }

    public static void setSociety(Context context, String UserId) {

        SessionManager session = new SessionManager(context);
        String socId = null;
        String socName = null;
        SocietyEntity socEnt = new SocietyEntity();
        DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
        try {
            socId = db
                    .getSociety(UserId.toString().toUpperCase(Locale.ENGLISH));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (socId != null) {
            session.setSocietyColumnId(Long.valueOf(socId));
        }

        //DB close removed;

        try {
            socEnt = db.getSocietyDetails(Long.valueOf(socId));
            if (socEnt != null) {
                socName = socEnt.name;
                session.setSocietyName(socName);
                session.setSocietyColumnId(socEnt.colId);
                session.setCollectionID(socEnt.socCode);
                session.setSocManagerEmail(socEnt.socEmail1);
                session.setCollectionCenterRoute(socEnt.route);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //DB close removed;
    }

    public static boolean isAirplaneModeOn(Context context) {

        boolean mode = Settings.System.getInt(context.getContentResolver(),
                Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        return mode;

    }

    public static void disableOrEnableAirplaneMode(boolean isEnable) {
        ArrayList<String> cmds = new ArrayList<String>();

        if (!isEnable) {
            cmds.add("settings put global airplane_mode_on 0");
            cmds.add("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
        } else {
            cmds.add("settings put global airplane_mode_on 1");
            cmds.add("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true");
        }


        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());

            for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd + "\n");
            }
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            int i = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

    }


    public static void createMsgTable(Context context, String socId) {
        MessageEntity msgEntity = new MessageEntity();
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        msgEntity = dbh.getAllMsgData(socId);

        if (msgEntity != null && msgEntity.date != null) {

            msgEntity.date = getTodayDateAndTime(1, context, false);
            msgEntity.socId = socId;
            msgEntity.msgLimit = "2";
            msgEntity.msgCount = "0";

            try {
                dbh.insertMsgDetails(msgEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    // Write settings
    public static void WriteHashMapGroup(Context ctx,
                                         Map<String, ArrayList<RateChartEntity>> map, String fname) {

        String fName = fname;

        FileOutputStream fOut = null;
        ObjectOutputStream osw = null;

        String filename = ctx.getFilesDir().toString() + fName + ".ratemap";

        if (!new File(filename).exists()) {
            try {
                fOut = new FileOutputStream(new File(filename));
                osw = new ObjectOutputStream(fOut);

                osw.writeObject(map);
                osw.flush();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fOut != null) {
                        fOut.close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @SuppressWarnings("unchecked")
    public static Map<String, ArrayList<RateChartEntity>> ReadSettingsMapGroup(
            Context ctx, String fname) {

        String fName = fname;

        FileInputStream fIn = null;
        ObjectInputStream isr = null;

        Map<String, ArrayList<RateChartEntity>> _list = null;
        String filename = ctx.getFilesDir().toString() + fName + ".ratemap";
        File f = new File(filename);
        if (!f.exists())
            return null;

        try {
            fIn = new FileInputStream(filename);
            isr = new ObjectInputStream(fIn);

            _list = (Map<String, ArrayList<RateChartEntity>>) isr.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return _list;

    }


    public static boolean EmailValidation(EditText etEmail, Context ctx) {

        boolean valid = true;
        if (etEmail.getText().toString() != null
                && etEmail.getText().toString().length() > 0) {
            if (!etEmail.getText().toString().contains("@")
                    && !etEmail.getText().toString().contains(".")) {

                etEmail.requestFocus();
                Toast.makeText(ctx, "Invalid email id !!", Toast.LENGTH_SHORT)
                        .show();
                valid = false;

            }

        }

        return valid;
    }


    public static void setSalesAverageData(AverageReportDetail avgReporDetail,
                                           String milkType) {
        if (milkType == null) {
            avgSalesReportDetails = avgReporDetail;
        } else if (milkType.equalsIgnoreCase("Cow")) {
            avgSalesCowReport = avgReporDetail;
        } else if (milkType.equalsIgnoreCase("Buffalo")) {
            avgSalesBuffalo = avgReporDetail;
        } else if (milkType.equalsIgnoreCase("Mixed")) {
            avgSalesMixed = avgReporDetail;
        }

    }


    public static AverageReportDetail getAverageData() {
        return avgReportDetail;
    }

    public static AverageReportDetail getSalesAverageData() {
        return avgSalesReportDetails;
    }

    public static void alphabetValidation(final EditText edit, final int check,
                                          final Context ctx, final int lengthOfEditText) {
        filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {

                if (lengthOfEditText != 0) {
                    edit.setFilters(new InputFilter[]{filter,
                            new InputFilter.LengthFilter(lengthOfEditText)});
                }

                if (check == ONLY_ALPHABET) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetter(source.charAt(i))
                                && !Character.toString(source.charAt(i))
                                .equals(" ")
                                && !Character.toString(source.charAt(i))
                                .equals(".")) {
                            Toast.makeText(ctx, "Only alphabets allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";

                        }
                    }
                } else if (check == ONLY_ALPHANUMERIC) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetterOrDigit(source.charAt(i))
                                && !Character.toString(source.charAt(i))
                                .equals("_")) {
                            Toast.makeText(ctx, "Only alphanumerics allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";
                        }
                    }
                } else if (check == ALPHANUMERIC_SPACE) {
                    // for alpha numeric with space
                    for (int i = start; i < end; i++) {
                        if (!Character.isLetterOrDigit(source.charAt(i))
                                && !Character.toString(source.charAt(i))
                                .equals("_") && !Character.toString(source.charAt(i))
                                .equals(" ")) {
                            Toast.makeText(ctx, "Only alphanumerics allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";
                        }
                    }
                } else if (check == ONLY_NUMERIC) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isDigit(source.charAt(i))) {
                            Toast.makeText(ctx, "Only numerics allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";
                        }

                    }
                } else if (check == ONLY_DECIMAL) {
                    for (int i = start; i < end; i++) {
                        if (!Character.isDigit(source.charAt(i))
                                && !Character.toString(source.charAt(i))
                                .equals(".")) {
                            Toast.makeText(ctx,
                                    "Only decimal numbers allowed!",
                                    Toast.LENGTH_SHORT).show();
                            edit.setText("");
                            return "";
                        }

                    }
                }


                return null;
            }
        };
        edit.setFilters(new InputFilter[]{filter});
    }

    public static void sendMessage(final Context ctx, String mobile,
                                   String msg, boolean check) {

        final SessionManager session = new SessionManager(ctx);
        final AmcuConfig amcuConfig = AmcuConfig.getInstance();

        //If sending sms is disable
        if (!amcuConfig.getAllowSMS()) {
            return;
        }

        msg = msg + "\n" + "\n"
                + getFooterUrl(ctx);

//        PendingIntent sentPI = PendingIntent.getBroadcast(ctx, 0, new Intent(
//                SENT), 0);
//
//        PendingIntent deliveredPI = PendingIntent.getBroadcast(ctx, 0,
//                new Intent(DELIVERED), 0);
//
//        // ---when the SMS has been sent---
//        ctx.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//
//                        session.setMessageCount(session.getMessageCount() + 1);
//                        Toast.makeText(ctx, "SMS sent", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
//                        Toast.makeText(ctx, "Generic failure", Toast.LENGTH_SHORT)
//                                .show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NO_SERVICE:
//                        Toast.makeText(ctx, "No service", Toast.LENGTH_SHORT)
//                                .show();
//                        break;
//                    case SmsManager.RESULT_ERROR_NULL_PDU:
//                        Toast.makeText(ctx, "Null PDU", Toast.LENGTH_SHORT).show();
//                        break;
//                    case SmsManager.RESULT_ERROR_RADIO_OFF:
//                        Toast.makeText(ctx, "Radio off", Toast.LENGTH_SHORT).show();
//                        break;
//                }
//            }
//        }, new IntentFilter(SENT));
//
//        // ---when the SMS has been delivered---
//        ctx.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context arg0, Intent arg1) {
//                switch (getResultCode()) {
//                    case Activity.RESULT_OK:
//                        Toast.makeText(ctx, "SMS delivered", Toast.LENGTH_SHORT)
//                                .show();
//                        break;
//                    case Activity.RESULT_CANCELED:
//                        Toast.makeText(ctx, "SMS not delivered", Toast.LENGTH_SHORT)
//                                .show();
//                        break;
//                }
//            }
//        }, new IntentFilter(DELIVERED));
//
//        SmsManager sms = SmsManager.getDefault();
//


        try {
            SmsManager smsManager = SmsManager.getDefault();
            session.setMessageCount(session.getMessageCount() + 1);
            smsManager.sendTextMessage(mobile, null, msg, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static String rateChartVersion(int i) {
        if (i <= 9) {
            return "00" + i;
        } else if (i > 9 && i <= 99) {
            return "0" + i;
        } else {
            return String.valueOf(i);
        }
    }

    public static boolean phoneNumberValidation(String number) {
        long num = Long.parseLong(number);
        long num1 = 7000000000L;
        long num2 = 9999999999L;

        if (num > num1 && num <= num2) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean validateMobileNumber(Context ctx, String number) {
        long mobNumber = 0;

        try {
            mobNumber = Long.parseLong(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (mobNumber > 5000000000l && mobNumber < 10000000000l) {
            return true;
        } else {
            MobileToast(ctx);
            return false;
        }
    }

    public static void MobileToast(Context ctx) {
        displayErrorToast("Please enter valid mobile number!",
                ctx);
    }


    public static void setManagerDetails(Context ctx) {
        UserEntity userEnt = new UserEntity();
        SessionManager session = new SessionManager(ctx);
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        userEnt = dbh.getManagerDetails("Manager",
                String.valueOf(session.getSocietyColumnId()));

        if (userEnt != null && userEnt.emailId != null) {
            session.setManagerEmailID(userEnt.emailId);
            session.setManagerName(userEnt.name);
            session.setManagerMobile(userEnt.mobile);
        }

    }

    public static int getMonthORYear(int monthY) {

        Calendar c = Calendar.getInstance();
        int k = 0;
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);

        if (monthY == 0) {
            k = month + 1;
            return k;
        } else {
            return year;
        }

    }


    public static String getDateAndTimeRDU(int dTime) {
        final Calendar c = Calendar.getInstance();
        Date date = c.getTime();

        String strDate = String.valueOf(date);
        String DAY;

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DATE);

        if (day > 9) {
            DAY = String.valueOf(day);
        } else {
            DAY = "0" + String.valueOf(day);
        }

        String time = padding(hour) + ":" + padding(min);

        if (dTime == 0) {
            return time;
        } else {
            return DAY + "/" + strDate.substring(4, 7) + "/"
                    + String.valueOf(year).substring(1, 3);
        }
    }

    public static String padding(int str) {
        String strRet = null;
        if (str < 10) {
            strRet = "0" + str;
        } else {
            strRet = String.valueOf(str);
        }
        return strRet;
    }

    public static void setDailyDateOrShift(String date, String Shift) {
        Date = date;
        shift = Shift;
    }

    public static String getDailyDateOrShift(int i) {
        if (i == 0) {
            return Date;
        } else {
            return shift;
        }
    }

    public static ArrayList<SampleDataEntity> getallSampleDataList(
            final Context ctx) {

        ArrayList<SampleDataEntity> allSampleListEnt = null;

        final DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();

        try {
            allSampleListEnt = dbHandler.getSampleData();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Removed database close;
        return allSampleListEnt;

    }


    public static String getDateAndTimeFromMilli(Long Ldate, int i) {

        Date date = new Date(Ldate);

        int month = date.getMonth();
        int day = date.getDate();
        int year = date.getYear();

        int hour = date.getHours();
        int min = date.getMinutes();
        int sec = date.getSeconds();
        month = month + 1;

        year = Integer.parseInt(date.toString().substring(
                date.toString().length() - 4, date.toString().length()));

        if (i == 0) {
            return padding(day) + "-" + padding(month) + "-" + padding(year);
        } else if (i == 10) {
            return padding(hour) + ":" + padding(min);
        } else {
            return padding(hour) + ":" + padding(min) + ":" + padding(sec);
        }

    }

    public static long getLong(String dayMonthYear) {
        String[] strArry = dayMonthYear.split("-");
        return Long.parseLong(strArry[2] + strArry[1] + strArry[0]);
    }

    public static long getLongTime(long dateLongformat) {
        final Calendar c = Calendar.getInstance();


        return Long.parseLong(String.valueOf(dateLongformat) + padding(22) + padding(22) + padding(22));
    }

    public static long getLongDateAndTime() {
        final Calendar c = Calendar.getInstance();

        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        month = month + 1;

        int hour = c.get(Calendar.HOUR_OF_DAY);
        //hour start from 0 EX 2.12PM will get as 13.12PM
        hour = hour + 1;
        int minutes = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);

        return Long.parseLong(String.valueOf(year) + padding(month) + padding(day) + padding(hour) + padding(minutes) + padding(sec));
    }

    public static ArrayList<ReportEntity> getShiftReport(Context ctx,
                                                         String milkType,
                                                         int endShift, String shift, long longDate) {

        CollectionRecordDao collectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        ArrayList<ReportEntity> allDetailRep = new ArrayList<ReportEntity>();

        if (endShift == sendEndShiftReport) {
            //To send end shift
            String currentShift = getCurrentShift();
            long lnDate = getDateInLongFormat(getTodayDateAndTime(1, ctx, true));

            try {
                if (milkType.equals("TEST")) {
                    allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(lnDate,
                            currentShift, null, REPORT_TYPE_SAMPLE);
                } else {
                    allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(lnDate,
                            currentShift, milkType, REPORT_TYPE_FARMER);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            //Removed database close statement
        } else if (endShift == 2) {
            // For daily shift report
            try {
                allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(longDate,
                        shift, milkType, REPORT_TYPE_FARMER);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Removed database close statement
        } else if (endShift == 3) {
            // For daily shift print report
            try {
                allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(longDate,
                        shift, milkType, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (endShift == 4) {
            // chilling shift report
            try {
                allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(longDate,
                        shift, milkType, REPORT_TYPE_MCC);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (endShift == 5) {
            // chilling shift report for end shift
            String Lshift = getCurrentShift();
            long lnDate = getDateInLongFormat(getTodayDateAndTime(1, ctx, true));

            try {
                allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(lnDate,
                        Lshift, milkType, REPORT_TYPE_MCC);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Removed database close statement
        }
        //
        else if (endShift == 6) {
            //To send end shift and creating excel sheet
            String Lshift = getCurrentShift();
            long lnDate = getDateInLongFormat(getTodayDateAndTime(1, ctx, true));

            try {
                allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(lnDate,
                        Lshift, milkType, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //Total Summary
        } else if (endShift == 8) {

            try {
                allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(longDate,
                        shift, milkType, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {

            try {
                allDetailRep = collectionRecordDao.findAllByDateShiftMilkTypeAndReportType(longDate,
                        shift, null, REPORT_TYPE_FARMER);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return allDetailRep;
    }

    public static void restartTab(Context mContext) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        try {
            amcuConfig.setBootCompleted(false);
            amcuConfig.setFirstConnection(true);

            Thread.sleep(100);
            // displayErrorToast(String.valueOf(saveSession.getFirstTimeConnection()), mContext);
            PowerManager powerManager = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
            powerManager.reboot(null);

        } catch (Exception e) {
            try {
                amcuConfig.setBootCompleted(false);
                Process proc = Runtime.getRuntime().exec(
                        new String[]{"su", "-c", "reboot"});
                proc.waitFor();

            } catch (Exception ex) {
                Log.i("Reboot", "Could not reboot", ex);
                mContext.startActivity(new Intent(mContext, SplashActivity.class));
            }
            e.printStackTrace();
        }


    }

    private static void showAlertForCharging(final Context context) {
        context.startService(new Intent(context, TabAlertSwitchOffService.class));
        AlertDialog.Builder chargingAlert = new AlertDialog.Builder(context);

        chargingAlert
                .setMessage("Please Turn Off Power Supply and press OK")
                .setCancelable(false);

        chargingAlert.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        boolean isCharging = isConnected();
                        if (isCharging)
                            showAlertForCharging(context);
                        else
                            commadsForShutDown(context);
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = chargingAlert.create();
        // avoids displaying the dialog if the parent activity has been destroyed thus avoiding BadTokenException
        if (!((Activity) context).isFinishing())
            alertDialog.show();
    }

    //25-feb-2016@yyy for geting delay from configuration
    public static String getDelayTime(Context mContext) {
        String delay = getStringFromTime(AmcuConfig.getInstance().getShutDownDelay());
        return delay;
    }

    public static void shutDownTab(Context mContext, SmartCCUtil.EndShiftListener endShiftListener) {

        String delay = getStringFromTime(AmcuConfig.getInstance().getShutDownDelay());

        if (!SmartCCUtil.FORCE_SHUTDOWN &&
                !AmcuConfig.getInstance().getEndShiftSuccess()) {
            new SmartCCUtil(mContext).alertForEndShift(endShiftListener);
        } else if (delay.equalsIgnoreCase("0"))

        {
            boolean isCharging = isConnected();
            if (isCharging) {
                showAlertForCharging(mContext);
            } else {
                commadsForShutDown(mContext);
            }
        } else {
            Intent intent = new Intent(mContext, ShutDownAlertActivity.class);
            intent.putExtra("isShutDown", false);
            mContext.startActivity(intent);
        }
    }

    // 25-feb-2016@yyy : used for reading the power_supply for capturing Ac current is on or off ,using kernal command.
    public static boolean isConnected() {

        //if status true = 1 then its ON , if status false = 0 then its OFF
        boolean status = true;
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"su", "-c", "cat /sys/class/power_supply/ac/online"});
            DataOutputStream stdin = new DataOutputStream(p.getOutputStream());
            stdin.writeBytes("ls /data\n"); // \n executes the command
            InputStream stdout = p.getInputStream();
            byte[] buffer = new byte[1024];
            int read;
            String out = new String();
            while (true) {
                read = stdout.read(buffer);
                out += new String(buffer, 0, read);

                if (read < 1024) {
                    break;
                }
            }
            if (out.contains("0")) {
                status = false;
            } else if (out.contains("1")) {
                status = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }

    public static void commadsForShutDown(Context mContext) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        try {
            amcuConfig.setBootCompleted(false);
            amcuConfig.setFirstConnection(true);


            Thread.sleep(100);
            // displayErrorToast(String.valueOf(saveSession.getFirstTimeConnection()), mContext);
            mContext.startActivity(new Intent("android.intent.action.ACTION_REQUEST_SHUTDOWN"));
        } catch (Exception e) {

            e.printStackTrace();
            try {
                //su-c "cat /sys/class/power_supply/ac/online" => 1(powersupply),0 (diconned)
                amcuConfig.setBootCompleted(false);
                Process proc = Runtime.getRuntime().exec(
                        new String[]{"su", "-c", "reboot -p"});
                proc.waitFor();

            } catch (Exception ex) {
                Log.i("Shutdown", "Could not shutdown", ex);
            }
        }
    }

    public static void restartApp(Context ctx) {

        displayErrorToast("Application restart please wait..", ctx);

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent mStartActivity = ctx.getPackageManager().getLaunchIntentForPackage(ctx.getPackageName());
        // Intent mStartActivity = new Intent(ctx, DeviceListActivity.class);
        //mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        // int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(ctx,
                0, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);


        AlarmManager mgr = (AlarmManager) ctx
                .getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10,
                mPendingIntent);
        System.exit(0);
    }

    public static void Logout(Context ctx) {

        displayErrorToast("Application logging out..", ctx);
        SessionManager session = new SessionManager(ctx);
        session.logoutUser();
        closePrinterAndRDUConnection();
        SplashActivity.deviceRefreshActivityCount = 0;
//        DeviceListActivity.onCreate = 0;
        Intent mStartActivity = new Intent(ctx, DeviceListActivity.class);
        mStartActivity.putExtra("fromSplashActivity", true);
        mStartActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mStartActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent mPendingIntent = PendingIntent.getActivity(ctx,
                0, mStartActivity,
                PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) ctx
                .getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, 0,
                mPendingIntent);

        System.exit(0);

    }

    private static void closePrinterAndRDUConnection() {
        // try {
        // if (SplashActivity.usbSerialPortPRINTER != null) {
        // SplashActivity.usbSerialPortPRINTER.close();
        // SplashActivity.usbSerialPortPRINTER = null;
        // }
        //
        // if (SplashActivity.usbSerialPortRDU != null) {
        // SplashActivity.usbSerialPortRDU.close();
        // SplashActivity.usbSerialPortRDU = null;
        // }
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
    }

    public static void saveReportsOnSdCard(Context ctx, ReportEntity reportEntity) throws IOException {

        String stringReport;
        String date = Util.getTodayDateAndTime(1, ctx, true);
        String shiftReport = getCurrentShift();

        if (reportEntity.postShift.equalsIgnoreCase("Morning")) {
            shiftReport = "Morning";
        } else {
            shiftReport = "Evening";
        }

        stringReport = reportEntity.socId + "," + reportEntity.farmerId + "," + reportEntity.farmerName + ","
                + "," + Util.getTodayDateAndTime(7, ctx, true) + "," + Util.getTodayDateAndTime(3, ctx, true) + ","
                + reportEntity.milkType + "," + reportEntity.fat + "," + reportEntity.snf
                + "," + reportEntity.awm + "," + reportEntity.quantity + ","
                + reportEntity.rate + "," + reportEntity.bonus + "," + reportEntity.amount + "," + reportEntity.manual
                + "," + reportEntity.status + "," + reportEntity.qualityMode + "," + reportEntity.quantityMode +
                "," + reportEntity.kgWeight + "," + reportEntity.ltrsWeight + "," +
                reportEntity.user + "," + reportEntity.tippingStartTime + "," + reportEntity.tippingEndTime + "\n";


        Util.generateNoteOnSD(date + shiftReport + "_shiftReports",
                stringReport, ctx, "smartAmcuReports");

    }

    // Changes for 11th version of APK

    public static void generateNoteOnSD(String sFileName, String sBody,
                                        Context ctx, String folderName) throws IOException {

        File root = new File(Util.getSDCardPath(),
                folderName);
        FileOutputStream fos = null;
        if (!root.exists()) {
            root.mkdirs();
        }
        // File gpxfile = new File(root, sFileName);
        final File myFile = new File(root, sFileName + ".txt");
        if (!myFile.exists()) {
            myFile.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(myFile, true);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(sBody);
        bufferedWriter.flush();
        bufferedWriter.close();
        //fileWriter.close();
    }

    public static void addFarmersTodatBase(ArrayList<FarmerEntity> allFarmEnt,
                                           Context context) {
        if (allFarmEnt.size() > 0) {
            FarmerManager farmerManager = new FarmerManager(context);
            farmerManager.saveAll(allFarmEnt, false);
            /*DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
            db.addCompleteFarmers(allFarmEnt, DatabaseHandler.isPrimary);
            db.addCompleteFarmers(allFarmEnt, DatabaseHandler.isSecondary);*/
        }
    }

    public static String paddingFarmerId(String farmId, int length) {


        if (length > 5) {
            if (farmId.length() > 5) {
                return farmId;
            }
            if (farmId.length() > 4) {
                return "0" + farmId;
            } else if (farmId.length() > 3) {
                return "00" + farmId;
            } else if (farmId.length() > 2) {
                return "000" + farmId;
            } else if (farmId.length() > 1) {
                return "0000" + farmId;
            } else if (farmId.length() == 1) {
                return "00000" + farmId;
            } else {
                return "000000";
            }
        } else if (length > 4) {
            if (farmId.length() > 4) {
                return farmId;
            }
            if (farmId.length() > 3) {
                return "0" + farmId;
            } else if (farmId.length() > 2) {
                return "00" + farmId;
            } else if (farmId.length() > 1) {
                return "000" + farmId;
            } else if (farmId.length() == 1) {
                return "0000" + farmId;
            } else {
                return "00000";
            }
        } else if (length > 3) {
            if (farmId.length() > 3) {
                return farmId;
            } else if (farmId.length() > 2) {
                return "0" + farmId;
            } else if (farmId.length() > 1) {
                return "00" + farmId;
            } else if (farmId.length() == 1) {
                return "000" + farmId;
            } else {
                return "0000";
            }
        } else {
            if (farmId.length() > 2) {
                return farmId;
            } else if (farmId.length() > 1) {
                return "0" + farmId;
            } else if (farmId.length() == 1) {
                return "00" + farmId;
            } else {
                return "000";
            }
        }
    }

    public static boolean checkForPendrive() {
        File root = new File("/mnt/usbhost1/");
        if (root.exists() && root.canWrite()) {
            rootFileName = "/mnt/usbhost1/";
            return true;
        } else {
            rootFileName = Util.getSDCardPath();
            return AmcuConfig.getInstance().getApkFromFileSystem();
        }
    }

    public static boolean checkForDuplicateDownLoadNetwork(Context ctx) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (amcuConfig.getDownloadId() == 0) {
            return false;
        } else {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(amcuConfig.getDownloadId());
            DownloadManager mDownloadManager = (DownloadManager) ctx
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor c = mDownloadManager.query(query);

            if (c.moveToFirst()) {
                int columnIndex = c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS);
                if (DownloadManager.STATUS_PAUSED == c.getInt(columnIndex)) {
                    return true;
                } else if (DownloadManager.STATUS_PENDING == c.getInt(columnIndex)) {
                    return true;
                } else {
                    return false;
                }
            } else {

                return false;
            }
        }
    }

    public static boolean checkForDuplicateDownload(Context ctx) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (amcuConfig.getDownloadId() == 0) {
            APKManager.apkDownloadInprogress = false;
            return false;
        } else {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(amcuConfig.getDownloadId());
            DownloadManager mDownloadManager = (DownloadManager) ctx
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            Cursor c = mDownloadManager.query(query);

            if (c.moveToFirst()) {
                int columnIndex = c
                        .getColumnIndex(DownloadManager.COLUMN_STATUS);


                if (DownloadManager.STATUS_RUNNING == c.getInt(columnIndex)) {
                    displayErrorToast("SmartAmcu update is in progress", ctx);
                    return true;
                } else if (DownloadManager.STATUS_PAUSED == c.getInt(columnIndex)) {
                    displayErrorToast("SmartAmcu update is Paused", ctx);
                    return true;
                } else if (DownloadManager.STATUS_PENDING == c.getInt(columnIndex)) {
                    displayErrorToast("SmartAmcu update is Pending", ctx);
                    return true;
                } else {
                    int i = c.getInt(columnIndex);
                    mDownloadManager.remove(amcuConfig.getDownloadId());
                    amcuConfig.setDownloadId(0);
                    APKManager.apkDownloadInprogress = false;
                    return false;
                }
            } else {

                mDownloadManager.remove(amcuConfig.getDownloadId());
                amcuConfig.setDownloadId(0);
                APKManager.apkDownloadInprogress = false;
                return false;
            }
        }
    }

    public static void displayActivity(final Context mContext) {
        try {
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(mContext, SimLockActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void displayErrorToast(final String errMessage, final Context mContext) {
        APKManager.apkDownloadInprogress = false;
        try {
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(mContext, errMessage, Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean checkForSampleRecord(Context ctx, String sampleId, int digitLength) {
        DatabaseHandler db;
        boolean valid = false;
        db = DatabaseHandler.getDatabaseInstance();

        try {
            valid = db.checkForSampleId(sampleId);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DB close removed;
            if (!valid) {
                addSampleDataRecords(ctx, sampleId, digitLength);
                return true;
            } else {
                return false;
            }
        }
    }

    private static void addSampleDataRecords(Context ctx, String sampleId, int digitLength) {
        String[] strName = {"Cleaning", "Rinsing", "Sample Test", "Water Test"};
        String[] strID = new String[4];

        if (digitLength > 5) {
            strID[0] = "009997";
            strID[1] = "009998";
            strID[2] = "000999";
            strID[3] = "000991";
        } else if (digitLength > 4) {
            strID[0] = "09997";
            strID[1] = "09998";
            strID[2] = "00999";
            strID[3] = "00991";
        } else {
            strID[0] = "9997";
            strID[1] = "9998";
            strID[2] = "0999";
            strID[3] = "0991";
        }

        DatabaseHandler db;

        if ((digitLength > 5 &&
                sampleId.equalsIgnoreCase("000991")) || (digitLength > 4
                && sampleId.equalsIgnoreCase("00991")) || (digitLength <= 4 && sampleId.equalsIgnoreCase("0991"))) {
            db = DatabaseHandler.getDatabaseInstance();
            SampleDataEntity sample = new SampleDataEntity();

            sample.sampleSocId = String.valueOf(new SessionManager(ctx).getSocietyColumnId());
            ;
            sample.sampleId = strID[3];
            sample.sampleMode = strName[3];
            sample.sampleBarcode = "";
            sample.sampleIsFat = "false";
            sample.sampleIsWeigh = "false";

            try {
                db.insertSampleData(sample);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //DB close removed;
            }
        } else {
            for (int i = 0; i < strName.length; i++) {

                db = DatabaseHandler.getDatabaseInstance();
                SampleDataEntity sample = new SampleDataEntity();

                sample.sampleSocId = String.valueOf(new SessionManager(ctx).getSocietyColumnId());
                ;
                sample.sampleId = strID[i];
                sample.sampleMode = strName[i];
                sample.sampleBarcode = "";
                sample.sampleIsFat = "false";
                sample.sampleIsWeigh = "false";

                try {
                    db.insertSampleData(sample);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //DB close removed;
                }
            }
        }
    }

    public static void addUsers(Context ctx) {

        String[] strName = {"Operator", "Manager"};
        String[] strPassword = {"operator.smartamcu", "manager"};
        String[] strUserId = {UserDetails.OPERATOR, UserDetails.MANAGER};
        String[] strRole = {"Operator", "Manager"};
        String[] strMobile = {"", ""};

        for (int i = 0; i < strName.length; i++) {
            DatabaseHandler db = DatabaseHandler.getDatabaseInstance();
            UserEntity userEntity = new UserEntity();

            // userEntity.emailId = strEmailId[i];
            userEntity.mobile = strMobile[i];
            userEntity.name = strName[i];
            userEntity.password = strPassword[i];
            userEntity.role = strRole[i];
            userEntity.userId = strUserId[i].toUpperCase(Locale.ENGLISH);
            userEntity.centerId = String.valueOf(new SessionManager(ctx).getSocietyColumnId());

            try {
                db.insertUser(userEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //DB close removed;
        }

    }

    public static String getFooterUrl(Context ctx) {

        String url = "";
        if (ctx != null) {
            if (AmcuConfig.getInstance().getSMSFooter() == null && BuildConfig.FLAVOR.equals("hatsun")) {
                AmcuConfig.getInstance().setSMSFooter(ctx.getResources().getString(R.string.hatsunlink));
            } else if (AmcuConfig.getInstance().getSMSFooter() == null) {
                AmcuConfig.getInstance().setSMSFooter(ctx.getResources().getString(R.string.stellappslink));
            }
            url = AmcuConfig.getInstance().getSMSFooter();
        }
        return url;
    }

    public static boolean isOperator(Context ctx)

    {
        isCheckForUpdate = false;
        String User = new SessionManager(ctx).getUserRole();
        if (User.equalsIgnoreCase("Operator")) {
            return true;
        } else {
            return false;
        }
    }


    public static String getDuplicateIdOrBarCode(String id, String barCode, Context ctx) {
        FarmerDao farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        String duplicateMsg = "";
        boolean validation = false;
        String centerId = id;
        int intId = -1;

        try {
            intId = Integer.parseInt(id);

            String farmId = Util.validateFarmerId(id,
                    ctx, AmcuConfig.getInstance().getFarmerIdDigit(), true);
            if (farmId != null) {
                id = farmId;
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (intId != -1) {
            centerId = String.valueOf(intId);
        }


        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        // To get duplicate from the sample Id

        try {
            validation = dbh.checkDuplicateSampleIdOrBarcode(id, CHECK_DUPLICATE_SAMPLEID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (validation) {
            duplicateMsg = "Sample Id: " + id + "\n";
            return duplicateMsg;
        }

        //Removed database close;


        dbh = DatabaseHandler.getDatabaseInstance();
// To get duplicate from smaple barcode
        try {
            if (barCode != null && !barCode.equalsIgnoreCase("")) {
                validation = dbh.checkDuplicateSampleIdOrBarcode(barCode, CHECK_DUPLICATE_SAMPLEBARCODE);
            } else {
                validation = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (validation) {

            duplicateMsg = duplicateMsg + "Sample Barcode: " + barCode + "\n";
            return duplicateMsg;
        }
        //Removed database close;
        //To get duplicate from farmer Id

        dbh = DatabaseHandler.getDatabaseInstance();
        try {
            validation = farmerDao.findById(id) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (validation) {
            duplicateMsg = duplicateMsg + "Member Id: " + id + "\n";
            return duplicateMsg;
        }

        //Removed database close;

        //To get duplicate from farmer barcode

        dbh = DatabaseHandler.getDatabaseInstance();

        try {
            if (barCode != null && !barCode.equalsIgnoreCase("")) {
                validation = farmerDao.findByBarcode(barCode) != null;
            } else {
                validation = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (validation) {
            duplicateMsg = duplicateMsg + "Member Barcode: " + barCode + "\n";
            return duplicateMsg;
        }

        //Removed database close;


        //To center Id and barcode

        dbh = DatabaseHandler.getDatabaseInstance();

        try {
            if (barCode != null && !barCode.equalsIgnoreCase("")) {
                validation = dbh.checkDuplicateCenterIdOrBarcode(barCode, CHECK_DUPLICATE_CENTERBARCODE);
            } else {
                validation = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (validation) {
            duplicateMsg = duplicateMsg + "Center Barcode: " + barCode + "\n";
            return duplicateMsg;
        }

        //Removed database close;
        //Check for duplicate center ID
        dbh = DatabaseHandler.getDatabaseInstance();

        try {
            validation = dbh.checkDuplicateCenterIdOrBarcode(centerId, CHECK_DUPLICATE_CENTERCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (validation) {
            duplicateMsg = duplicateMsg + "Center Id: " + centerId + "\n";
            return duplicateMsg;
        }

        //Removed database close;


        if (duplicateMsg.equalsIgnoreCase("")) {
            duplicateMsg = null;
        }
        return duplicateMsg;
    }

    public static void setNonMarketAppDisable(Context ctx) {
        try {
            Settings.Global.putInt(ctx.getContentResolver(),
                    Settings.Global.INSTALL_NON_MARKET_APPS, 0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNonMarketAppEnable(Context ctx) {
        try {
            Settings.Global.putInt(ctx.getContentResolver(),
                    Settings.Global.INSTALL_NON_MARKET_APPS, 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTimeAndTimeZone(Context ctx) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                Settings.Global.putInt(ctx.getContentResolver(),
                        Settings.Global.AUTO_TIME, 1);

                Settings.Global.putInt(ctx.getContentResolver(),
                        Settings.Global.AUTO_TIME_ZONE, 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public static String checkForValidMilkType(String milkType) {
        if (milkType.equalsIgnoreCase("COW") || milkType.equalsIgnoreCase("BUFFALO")
                || milkType.equalsIgnoreCase("MIXED") || milkType.equalsIgnoreCase("GOAT") || milkType.equalsIgnoreCase("BOTH")) {
            return null;
        } else {
            return milkType;
        }
    }

    public static boolean checkIfRegisteredCode(String id) {
        boolean returnValue = false;

        int intId = -1;
        try {
            intId = Integer.parseInt(id);

            if ((intId == 999) || (intId == 9999)
                    || (intId == 9997) || (intId == 9998) || (intId == 991)) {
                returnValue = true;
            }
        } catch (NullPointerException e1) {

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return returnValue;
    }


    public static String checkForRegisteredCode(String farmId, int digitLength, boolean isCenter) {
        int id = -1;
        try {
            id = Integer.parseInt(farmId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (isCenter) {
            if (id != -1) {
                if ((id == 9999) || (id == 9998) || (id == 9997) || (id == 991) || (id == 999)) {
                    return id + " code reserved, Please enter other code.";
                } else {
                    return null;
                }
            } else {
                return null;
            }

        } else if (digitLength > 5) {
            if (farmId.equalsIgnoreCase("009999") || farmId.equalsIgnoreCase("09999") || farmId.equalsIgnoreCase("9999")) {
                return "9999 code reserved for Rate check, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("009997") || farmId.equalsIgnoreCase("09997") || farmId.equalsIgnoreCase("9997")) {
                return "9997 code reserved for Cleaning, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("009998") || farmId.equalsIgnoreCase("09998") || farmId.equalsIgnoreCase("9998")) {
                return "9998 code reserved for Rinsing, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("000999") || farmId.equalsIgnoreCase("00999") || farmId.equalsIgnoreCase("0999")) {
                return "999 code reserved for Sample Test, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("000991") || farmId.equalsIgnoreCase("00991") || farmId.equalsIgnoreCase("0991")) {
                return "991 code reserved for Water Test, Please enter other code.";
            } else {
                return null;
            }
        } else if (digitLength > 4 && digitLength < 6) {
            if (farmId.equalsIgnoreCase("09999") || farmId.equalsIgnoreCase("9999")) {
                return "9999 code reserved for Rate check, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("09997") || farmId.equalsIgnoreCase("9997")) {
                return "9997 code reserved for Cleaning, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("09998") || farmId.equalsIgnoreCase("9998")) {
                return "9998 code reserved for Rinsing, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("00999") || farmId.equalsIgnoreCase("0999")) {
                return "999 code reserved for Sample Test, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("00991") || farmId.equalsIgnoreCase("0991")) {
                return "991 code reserved for Water Test, Please enter other code.";
            } else {
                return null;
            }
        } else if (digitLength > 2 && digitLength < 4) {
            if (farmId.equalsIgnoreCase("9999")) {
                return "9999 code reserved for Rate check, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("9997")) {
                return "9997 code resered for Cleaning, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("9998") || farmId.equalsIgnoreCase("9998")) {
                return "9998 code reserved for Rinsing, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("0999") || farmId.equalsIgnoreCase("999")) {
                return "0999 code reserved for Sample Test, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("0991") || farmId.equalsIgnoreCase("991")) {
                return "0991 code reserved for Water Test, Please enter other code.";
            } else {
                return null;
            }
        } else {
            if (farmId.equalsIgnoreCase("9999")) {
                return "9999 code reserved for Rate check, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("9997")) {
                return "9997 code reserved for Cleaning, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("9998")) {
                return "9998 code reserved for Rinsing, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("0999")) {
                return "0999 code reserved for Sample Test, Please enter other code.";
            } else if (farmId.equalsIgnoreCase("0991")) {
                return "0991 code reserved for Water Test, Please enter other code.";
            } else {
                return null;
            }
        }


    }

    public static void setIpTableCommand(Context mContext) {

        try {
            Process proc = Runtime.getRuntime().exec(
                    new String[]{"iptables -I INPUT 1 -i lo -j ACCEPT", "cd " + Environment.getExternalStorageDirectory().getAbsolutePath()});

            proc.waitFor();
        } catch (Exception ex) {
            Log.i("Reboot", "Could not reboot", ex);
        }
    }

    public static void doCommandsForUpgradeAPK(Context ctx, String filePath, boolean fromPendrive) {


        //new SessionManager(ctx).setIsLastMACollection(false);
        ArrayList<String> cmds = new ArrayList<String>();
        //  cmds.add("mount -o remount, rw /system");
        //Changes as per latest Image from china

        cmds.add("mount -o remount,rw /dev/block/by-name/system /system");

        String strLocation = filePath.substring(0, 4);

        if (strLocation.equals("/mnt")) {
            strLocation = filePath.substring(4, filePath.length());
        } else {
            strLocation = filePath;
        }

        File apkfile = new File(filePath);

        if (apkfile != null && apkfile.exists()) {


            // cmds.add("cat /data/app/com.package.name-1.apk > /system/app/ApplicationName.apk");
            cmds.add("cat " + filePath + " > /system/priv-app/" + SmartCCUtil.PACKAGE_NAME + "-1.apk");
            cmds.add("chmod 644 /system/priv-app/" + SmartCCUtil.PACKAGE_NAME + "-1.apk");
            //for installing the apk
            //cmds.add("pm install -r /system/priv-app/com.stellapps.smartamcu.usb-1.apk");
            try {
                if (apkfile != null && apkfile.exists()) {
                    double bytes = apkfile.length();
                    double kilobytes = (bytes / 1024);
                    double megabytes = (kilobytes / 1024);
                    if (megabytes > 1) {
                        Process process = Runtime.getRuntime().exec("su");
                        DataOutputStream os = new DataOutputStream(process.getOutputStream());

                        for (String tmpCmd : cmds) {
                            os.writeBytes(tmpCmd + "\n");
                        }
                        os.writeBytes("exit\n");
                        os.flush();
                        os.close();
                        int i = process.waitFor();
                    } else {
                        displayErrorToast("No file found while installation!", ctx);
                    }

                } else {
                    displayErrorToast("No file found while installation!", ctx);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (apkfile != null && apkfile.exists()) {
                    try {
                        apkfile.delete();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            displayErrorToast("Invalid file please try again!", ctx);
        }
    }

    /**
     * Setting IP tables rule, for various URL
     *
     * @param ctx
     * @throws Exception
     */
    public synchronized static void doCmdsForIptables(Context ctx) throws Exception {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        SmartCCUtil smartCCUtil = new SmartCCUtil(ctx);
        String server = amcuConfig.getServer();
        String oldWhiteList = amcuConfig.getWhiteListURL();

        if (AmcuConfig.getInstance().getIpTableRule()) {
            if (server != null) {
                //  displayErrorToast(server, ctx);
                ArrayList<String> cmds = new ArrayList<String>();

                //flush the Ip tables rule
                cmds.add("iptables -F");
                //# Allow loopback

                cmds.add("iptables -I INPUT 1 -i lo -j ACCEPT");
                //# Allow DNS
                cmds.add("iptables -A OUTPUT -p udp --dport 53 -j ACCEPT");
                //# Now, allow connection to website serverfault.com on port 80

                String smptMail = "smtp.googlemail.com";

//        cmds.add("iptables -A OUTPUT -p tcp -d "+smptMail+" --dport 465 -j ACCEPT");
//        cmds.add("iptables -A INPUT -p tcp -d "+smptMail+" --sport 465 -m state --state ESTABLISHED -j ACCEPT");

                //allows wisens module connection over hotspot
//                cmds.add("iptables -A INPUT -i wlan0 -j ACCEPT");
//                cmds.add("iptables -A OUTPUT -o wlan0 -j ACCEPT");


                cmds.add("iptables -A OUTPUT -p tcp " + "-m iprange --src-range 192.168.43.1-192.168.43.255" + " --dport 5683 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp " + "-m iprange --src-range 192.168.43.1-192.168.43.255" + " --sport 5683 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p udp " + "-m iprange --src-range 192.168.43.1-192.168.43.255" + " --dport 5683 -j ACCEPT");
                cmds.add("iptables -A INPUT -p udp " + "-m iprange --src-range 192.168.43.1-192.168.43.255" + " --sport 5683 -j ACCEPT");


                cmds.add("iptables -A OUTPUT -p tcp --dport 465 -j ACCEPT");
                // cmds.add("iptables -A OUTPUT -p tcp -d " + server + " --dport 80 -j ACCEPT");
                // cmds.add("iptables -A OUTPUT -p tcp -d " + server+ " --dport 443 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp --sport 465 -m state --state ESTABLISHED -j ACCEPT");
                cmds.add("iptables -A INPUT -m conntrack --ctstate ESTABLISHED,RELATED -j ACCEPT");
                //To set the time zone

//        cmds.add("iptables -A INPUT -s 2.android.pool.ntp.org -j ACCEPT");
//        cmds.add("iptables -A OUTPUT -d 2.android.pool.ntp.org -j ACCEPT");

                //New commands

                cmds.add("iptables -A OUTPUT -p udp -d 2.android.pool.ntp.org --dport 123 -j ACCEPT");
                cmds.add("iptables -A INPUT -p udp  -s 2.android.pool.ntp.org --sport 123 -j ACCEPT");
                //TODO commented for testing purpose
                cmds.add("iptables -A OUTPUT -p tcp -d " + LICENSE_SERVER + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + LICENSE_SERVER + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + DEVELOPMENT_SERVER + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + DEVELOPMENT_SERVER + " --dport 443 -j ACCEPT");

                cmds.add("iptables -A INPUT -i " + ALL_CRASHYLITICS_SERVER + " -j ACCEPT");
                cmds.add("iptables -A OUTPUT -o " + ALL_CRASHYLITICS_SERVER + " -j ACCEPT");
                cmds.add("iptables -A INPUT -i " + ALL_FABRIC_SERVER + " -j ACCEPT");
                cmds.add("iptables -A OUTPUT -o " + ALL_FABRIC_SERVER + " -j ACCEPT");

                cmds.add("iptables -A OUTPUT -p tcp -d " + FABRIC_SERVER + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + FABRIC_SERVER + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_API + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_API + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_E + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_E + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_REPORTS + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_REPORTS + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_SETTING + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_SETTING + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_REALTIME + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + CRASHYLITICS_REALTIME + " --dport 443 -j ACCEPT");


                cmds.add("iptables -A OUTPUT -p tcp -d " + ALL_CRASHYLITICS_SERVER + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + ALL_FABRIC_SERVER + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + ALL_CRASHYLITICS_SERVER + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A OUTPUT -p tcp -d " + ALL_FABRIC_SERVER + " --dport 80 -j ACCEPT");

                cmds.add("iptables -A INPUT -p tcp -d " + FABRIC_SERVER + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + FABRIC_SERVER + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_API + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_API + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_E + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_E + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_REPORTS + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_REPORTS + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_SETTING + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_SETTING + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_REALTIME + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + CRASHYLITICS_REALTIME + " --dport 443 -j ACCEPT");


                cmds.add("iptables -A INPUT -p tcp -d " + ALL_FABRIC_SERVER + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + ALL_FABRIC_SERVER + " --dport 443 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + ALL_CRASHYLITICS_SERVER + " --dport 80 -j ACCEPT");
                cmds.add("iptables -A INPUT -p tcp -d " + ALL_CRASHYLITICS_SERVER + " --dport 443 -j ACCEPT");
                if (oldWhiteList != null && !oldWhiteList.equalsIgnoreCase("NA")) {
                    String wList = AmcuConfig.getInstance().getWhiteListURL();
                    int startIndex = wList.indexOf("[");
                    int lastIndex = wList.indexOf("]");
                    wList = wList.substring(startIndex + 1, lastIndex);
                    StringTokenizer token = new StringTokenizer(wList, ",");
                    while (token.hasMoreTokens()) {
                        String invString = token.nextElement().toString().trim();
                        invString = invString.substring(1, invString.length() - 1);
                        cmds.add("iptables -A OUTPUT -p tcp -d " + invString + " --dport 80 -j ACCEPT");
                        cmds.add("iptables -A OUTPUT -p tcp -d " + invString + " --dport 443 -j ACCEPT");
                    }
                } else {
                    ArrayList<WhiteListUrl> allWhiteList =
                            smartCCUtil.getEnhancedWhiteListUrl(amcuConfig.getLicenceJson());
                    // whitelist URL code

                    if (allWhiteList != null && allWhiteList.size() > 0) {
                        for (int i = 0; i < allWhiteList.size(); i++) {
                            WhiteListUrl wlu = new WhiteListUrl();
                            wlu.uri = allWhiteList.get(i).uri;
                            wlu.ports = allWhiteList.get(i).ports;
                            // Util.displayErrorToast(wlu.uri,ctx);

                            //For testing purpose
                            cmds.add("iptables -A OUTPUT -p tcp -d " + "my.stellapps.com" + " --dport "
                                    + 80 + " -j ACCEPT");
                            cmds.add("iptables -A OUTPUT -p tcp -d " + "my.stellapps.com" + " --dport "
                                    + 443 + " -j ACCEPT");

                            cmds.add("iptables -A OUTPUT -p tcp -d " + "planner.stellapps.com" + " --dport "
                                    + 80 + " -j ACCEPT");
                            cmds.add("iptables -A OUTPUT -p tcp -d " + "planner.stellapps.com" + " --dport "
                                    + 443 + " -j ACCEPT");


                            for (int j = 0; j < wlu.ports.size(); j++) {
                                cmds.add("iptables -A OUTPUT -p tcp -d " + wlu.uri + " --dport "
                                        + wlu.ports.get(j) + " -j ACCEPT");
                            }
                        }
                    }

                }

                //Drop everything
                cmds.add("iptables -P INPUT DROP");
                cmds.add("iptables -P OUTPUT DROP");

                //IV6 Rule

                cmds.add("ip6tables -P INPUT DROP");
                cmds.add("ip6tables -P OUTPUT DROP");
                cmds.add("ip6tables -P FORWARD DROP");

                Process process = null;
                try {
                    process = Runtime.getRuntime().exec("su");
                    DataOutputStream os = new DataOutputStream(process.getOutputStream());

                    for (String tmpCmd : cmds) {

                        os.writeBytes(tmpCmd + "\n");
                        os.flush();

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                    os.writeBytes("exit\n");
                    os.flush();
                    os.close();
                    //  displayErrorToast("Ip table setting Done", ctx);
                    int i = process.waitFor();
                } catch (IOException e) {
                    e.printStackTrace();
                    //  displayErrorToast("Ip table setting Exp2", ctx);

                } catch (InterruptedException e) {
                    //  displayErrorToast("Ip table setting Exp1", ctx);
                    e.printStackTrace();
                } finally {
                    if (process != null) {
                        process.destroy();
                    }
                }

            }
        }
    }

    public static void validateServerName(Context ctx) {
        String server = AmcuConfig.getInstance().getServer();
        if (server == null) {
            if (BuildConfig.FLAVOR.equals("stellapps") || BuildConfig.FLAVOR.equals("hatsun")) {
                AmcuConfig.getInstance().setServer("amcu-gw.smartmoo.com");

            } else {
                AmcuConfig.getInstance().setServer("amcugw2.smartmoo.com");

            }
        } else if (server.startsWith("http://")) {

            server = server.replace("http://", "");
            AmcuConfig.getInstance().setServer(server);

        } else if (server.startsWith("https://")) {
            server = server.replace("https://", "");
            AmcuConfig.getInstance().setServer(server);
        }
    }

    public static long getLongFromTime(String time) {
        long lTime = 0;
        try {
            lTime = Long.parseLong(time);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        lTime = lTime * 60 * 1000;
        return lTime;
    }

    public static String getStringFromTime(long time) {

        long lTime = time / (60 * 1000);

        return String.valueOf(lTime);
    }

    public static boolean dateCompare(String date1, String date2)

    {
        try {
            if (getLong(date1) <= getLong(date2)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void addMissingDatatoDB(Context ctx, String str) {

        ReportEntity repEntity = null;
        try {
            repEntity = getRepEntity(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (repEntity != null) {
            DatabaseManager dbManager = new DatabaseManager(ctx);
            long colRecStatusIndex = dbManager.addCollectionRecord(repEntity);
            if (colRecStatusIndex != -1) {
                dataForPost(repEntity, colRecStatusIndex, ctx);
                new SessionManager(ctx).setReportData(null);
            }
        }
    }

    private static ReportEntity getRepEntity(String reports) {
        ValidationHelper chkValidationHelper = new ValidationHelper();

        String[] strArray = reports.split("=");

        ReportEntity repEntity = new ReportEntity();
        repEntity.farmerId = strArray[0];
        repEntity.farmerName = strArray[1];
        repEntity.socId = strArray[3];
        repEntity.fat = chkValidationHelper.getDoubleFromString(strArray[4], 0);
        repEntity.snf = chkValidationHelper.getDoubleFromString(strArray[5], 0);
        repEntity.rate = chkValidationHelper.getDoubleFromString(strArray[6], 0);
        repEntity.quantity = chkValidationHelper.getDoubleFromString(strArray[7], 0);
        repEntity.amount = chkValidationHelper.getDoubleFromString(strArray[8], 0);
        repEntity.temp = chkValidationHelper.getDoubleFromString(strArray[9], 0);
        repEntity.clr = chkValidationHelper.getDoubleFromString(strArray[10], 0);
        repEntity.postDate = strArray[11];
        repEntity.postShift = strArray[12];
        repEntity.time = strArray[13];
        repEntity.milkType = strArray[14];
        repEntity.lDate = Long.parseLong(strArray[15]);
        repEntity.txnNumber = !strArray[16].equalsIgnoreCase("") ? Integer.getInteger(strArray[16]) : 0;
        repEntity.miliTime = Long.parseLong(strArray[17]);
        repEntity.milkAnalyserTime = chkValidationHelper.getLongFromString(strArray[18]);
        repEntity.weighingTime = chkValidationHelper.getLongFromString(strArray[19]);
        repEntity.awm = chkValidationHelper.getDoubleFromString(strArray[20], 0);
        repEntity.status = strArray[21];
        repEntity.manual = strArray[22];
        repEntity.quantityMode = strArray[22];
        repEntity.qualityMode = strArray[22];
        repEntity.bonus = chkValidationHelper.getDoubleFromString(strArray[23], 0);
        repEntity.collectionType = strArray[26];

        repEntity.recordCommited = 1;

        return repEntity;
    }

    private static void dataForPost(ReportEntity repEnt, long colRecStatusIndex, Context ctx) {

        DatabaseManager dbm = new DatabaseManager(ctx);

        PostEndShift poEndShift = dbm.getPostEndShift(repEnt);
        // Check if we need to insert this end shift data or update an already created entry
//        DatabaseHandler db = DatabaseHandler.getDatabaseInstance(ctx);
//        db.updateShiftDetails(poEndShift, colRecStatusIndex);
    }

    public static String getTheCopiedFile(String input) throws IOException {
        String dest = Environment.getExternalStorageDirectory()
                .getAbsolutePath() + "/smartAmcu.apk";

        FileInputStream inStream = new FileInputStream(input);
        FileOutputStream outStream = new FileOutputStream(dest);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
        return dest;

    }

    public static boolean checkForRootedTab() {

        Process process = null;
        try {
            process = Runtime.getRuntime().exec("su");
            return true;
        } catch (Exception e) {
            return false;
        } finally {
            if (process != null) {
                try {
                    process.destroy();
                } catch (Exception e) {
                }
            }
        }

    }


//    public static void disableAirplaneMode()
//    {
//        ArrayList<String> cmds=new ArrayList<String>();
//        cmds.add("settings put global airplane_mode_on 0");
//        cmds.add("am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false");
//
//        Process process=null;
//        try {
//            process = Runtime.getRuntime().exec("su");
//            DataOutputStream os = new DataOutputStream(process.getOutputStream());
//
//            for (String tmpCmd : cmds) {
//                os.writeBytes(tmpCmd+"\n");
//            }
//            os.writeBytes("exit\n");
//            os.flush();
//            os.close();
//            int i=  process.waitFor();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        finally
//        {
//            if(process!=null)
//            {
//                process.destroy();
//            }
//        }
//
//    }

    public static String createSMSFromReport(
            ReportEntity reportEntity, String farmName, Context ctx) {
        String smsTosend = null;
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (farmName != null && farmName.length() > 10) {
            farmName = farmName.substring(0, 10);
        }
        smsTosend = "name: " + farmName + "\n"
                + "Date/Shift: " + Util.getDateDDMMYY(reportEntity.postDate, 0) + "/"
                + SmartCCUtil.getAlternateShift(reportEntity.postShift) + "\n"
                + "Qty " + getTheUnit(ctx, UNIT_FOR_QUANTITY) + " :  " +
                reportEntity.getQuantity() + "\n"
                + "Fat % :  " + reportEntity.getDisplayFat() + "\n"
                + getSnfOrClrForSMS(reportEntity)
                + "Rate :  " + reportEntity.rate + "/" + getTheUnit(ctx, UNIT_FOR_RATE) + "\n";
        if (amcuConfig.getKeyAllowProteinValue()) {
            smsTosend = smsTosend
                    + "Protein :" + reportEntity.getDisplayProtein() + "\n"
                    + "Inc :" + reportEntity.getIncentiveAmount() + "\n"
                    + "Amt :  " + getAmount(ctx, reportEntity.getTotalAmount(),
                    reportEntity.bonus) + "\n"
                    + "\n" + getSMSFooter(ctx) + "\n";
        } else {
            smsTosend = smsTosend
                    + "Amt :  " + getAmount(ctx, reportEntity.getTotalAmount(), reportEntity.bonus) + "\n" +
                    "\n" + getSMSFooter(ctx) + "\n";
        }
        return smsTosend;
    }


    public static String getSMSFooter(Context ctx) {


        if (ctx != null) {
            if (AmcuConfig.getInstance().getCollectionSMSFooter() == null &&
                    BuildConfig.FLAVOR.equalsIgnoreCase("hatsun")) {
                AmcuConfig.getInstance().setCollectionSMSFooter("Hatsun");
                return "Hatsun";
            } else if (AmcuConfig.getInstance().getCollectionSMSFooter() == null) {
                AmcuConfig.getInstance().setCollectionSMSFooter("Powered by smartAmcu");
                return AmcuConfig.getInstance().getCollectionSMSFooter();
            } else {
                return AmcuConfig.getInstance().getCollectionSMSFooter();
            }

        } else {
            return "Powered by samrtAmcu";
        }

    }

    public synchronized static boolean lockorUnlockTheSimPin(Context ctx, String simPin) {
        boolean returnValue = false;
        TelephonyManager manager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        Integer state = manager.getSimState();
        if (state.equals(TelephonyManager.SIM_STATE_PIN_REQUIRED)) {

            try {

                @SuppressWarnings("rawtypes")
                Class clazz = Class.forName(manager.getClass().getName());
                Method m = clazz.getDeclaredMethod("getITelephony");
                m.setAccessible(true);
                ITelephony it = (ITelephony) m.invoke(manager);
                if (it.supplyPin(simPin)) {
                    displayErrorToast("SIM UnLocked", ctx);
                } else {
                    displayErrorToast("SIM UnLocked failed", ctx);
                }

            } catch (Exception e) {
                //
                e.printStackTrace();
            }

            AmcuConfig.getInstance().setAttemptForSimlock(true);


            returnValue = true;

        } else if (state.equals(TelephonyManager.SIM_STATE_PUK_REQUIRED)) {
            displayErrorToast("PUK required, contact administrator", ctx);
            returnValue = true;
        } else if (state.equals(TelephonyManager.SIM_STATE_READY)) {
            //if sim ready state
            returnValue = true;
        } else if (state.equals(TelephonyManager.SIM_STATE_ABSENT)) {
            //for sim absent

            returnValue = true;
        }

        return returnValue;
    }

    public static void changeSimLockPassword(String oldPin, String newPin, final Context ctx) {
        final Handler handler = new Handler();

        try {
            String ussdCode = "**04*" + oldPin + "*" + newPin + "*" + newPin + Uri.encode("#");
            ctx.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ussdCode)));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    displayErrorToast("Tab restart required for this change!", ctx);
                    handler.postDelayed(runnable, 10000);
                }
            }).start();

            runnable = new Runnable() {
                @Override
                public void run() {
                    restartTab(ctx);
                }
            };

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static UserEntity getAndUpdateTheUserInfo(Context ctx, String userName, UserEntity userEntity, int todo) {
        UserEntity userEnt = null;
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        if (todo == GET_USER) {
            try {
                userEnt = dbh.getPassword(userName.toUpperCase(Locale.ENGLISH));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (todo == UPDATE_USER) {
            try {
                dbh.insertUser(userEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //Removed database close;
        return userEnt;
    }

    public static SocietyEntity getAndUpdateCenterInfo(Context ctx, SocietyEntity socEnt, int todo) {
        SocietyEntity societyEnt = null;
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        if (todo == GET_SOCIETY) {
            try {
                societyEnt = dbh.getSocietyDetails(0);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (todo == UPDATE_SOCIETY) {
            try {
                dbh.insertSociety(socEnt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return societyEnt;
    }


    public static String validateFarmerId(String farmerId, Context ctx, int digitLength, boolean centerCheck) {
        int id = 0;
        try {
            id = Integer.parseInt(farmerId);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (id < 1) {
            if (!centerCheck)
                displayErrorToast("Farmer Id should be greator than 0.", ctx);
            return null;
        } else if ((digitLength > 5) && id > 999999) {
            if (!centerCheck)
                displayErrorToast("Farmer Id should be less than 999999.", ctx);
            return null;
        } else if ((digitLength > 4) && id > 99999) {
            if (!centerCheck)
                displayErrorToast("Farmer Id should be less than 99999.", ctx);
            return null;
        } else if ((digitLength == 4) && id > 9999) {
            if (!centerCheck)
                displayErrorToast("Farmer Id should be less than 9999.", ctx);
            return null;
        } else if ((digitLength == 3) && id > 999) {
            if (!centerCheck)
                displayErrorToast("Farmer Id should be less than 999.", ctx);
            return null;
        } else if ((digitLength == 2) && id > 99) {
            if (!centerCheck)
                displayErrorToast("Farmer Id should be less than 99.", ctx);
            return null;
        } else {
            String getErrMsg = Util.checkForRegisteredCode(farmerId, digitLength, false);
            if (getErrMsg != null) {
                displayErrorToast(getErrMsg, ctx);
                return null;
            } else {
                return paddingFarmerId(farmerId, AmcuConfig.getInstance().getFarmerIdDigit());
            }
        }
    }

    public static int getVersionCode(Context ctx)

    {
        int versionCode = 0;
        try {
            versionCode = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static void updateApkFromDPN(Context ctx) {

        if (checkForAPKValidation(APK_DOWNLODED_VERSION, ctx)) {
            try {
                boolean isDuplicateAPK = checkForDuplicateDownload(ctx);
                if (!isDuplicateAPK && !APKManager.apkDownloadInprogress) {
                    APKManager.apkDownloadInprogress = true;
                    UpdateAPK updateApk;
                    updateApk = new UpdateAPK();
                    updateApk.setContext(ctx, Util.APK_URI);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Util.APK_DOWNLODED_VERSION = 0;
            AmcuConfig.getInstance().setDownloadId(0);
            Util.APK_URI = null;
            AmcuConfig.getInstance().setAPKUri(null);

            try {
                DownloadManager mDownloadManager = (DownloadManager) ctx
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                mDownloadManager.remove(AmcuConfig.getInstance().getDownloadId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean checkForAPKValidation(int version, Context ctx) {
        int current_version = getVersionCode(ctx);
        if (version > current_version) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean validateConversion(String str, Context ctx) {
        double dStr = 0;
        try {
            dStr = Double.parseDouble(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (dStr > 0) {
            return true;
        } else {
            Util.displayErrorToast("Invalid conversion factor!", ctx);
            return false;
        }
    }

    public static String convertStringtoDecimal(String str, DecimalFormat decimalForamt, String defaultValue) {

        try {
            defaultValue = decimalForamt.format(Double.parseDouble(str));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return defaultValue;
    }

    public static boolean readyToDelete(long smsDate) {
        long currentDate = System.currentTimeMillis();
        smsDate = smsDate + (10 * 24 * 60 * 60 * 1000);

        if (currentDate > smsDate) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean isThisNewShift(Context ctx) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        String currentShift = getCurrentShift();
        String date = getTodayDateAndTime(1, ctx, true);

        currentShift = date + currentShift;
        if (amcuConfig.getCurrentShiftDate().equals(currentShift)) {
            return false;
        } else {
            if (amcuConfig.getPreviousShiftDate() == null) {
                amcuConfig.setCurrentAndPreviousShiftDate(currentShift, currentShift);
            } else {
                amcuConfig.setCurrentAndPreviousShiftDate(currentShift, amcuConfig.getCurrentShiftDate());
            }

            return true;
        }
    }

    public static void setDefaultRateChart(Context ctx) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (isThisNewShift(ctx)) {
            amcuConfig.setRateChartName(null);
            amcuConfig.setRateChartForBuffalo(null);
            amcuConfig.setRateChartForCow(null);
            amcuConfig.setRateChartForMixed(null);
            amcuConfig.setCurrentSessionStartedWithMix(false);
            amcuConfig.setCurrentSessionStartedWithBuff(false);
            amcuConfig.setCurrentSeesionStartedWithCow(false);
            amcuConfig.setInCentiveRateChartname(null);
            amcuConfig.setIncentiveRateChartForBuffalo(null);
            amcuConfig.setIncentiveRateChartForCow(null);
            amcuConfig.setRateIncentiveChartForMixed(null);

        }
    }

    public static void setCollectionStartedWithMilkType(String str, Context ctx) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();

        String date = getTodayDateAndTime(1, ctx, true);
        String currentShift = getCurrentShift();
        String dateAndShift = date + currentShift;
        //Added curred data and shift
        amcuConfig.setCurrentAndPreviousShiftDate(dateAndShift, dateAndShift);
        if (str.equalsIgnoreCase("Cow")) {
            amcuConfig.setCurrentSeesionStartedWithCow(true);
        } else if (str.equalsIgnoreCase("Buffalo")) {
            amcuConfig.setCurrentSessionStartedWithBuff(true);
        } else if (str.equalsIgnoreCase("Mixed")) {
            amcuConfig.setCurrentSessionStartedWithMix(true);
        }
    }

    public static boolean checkIfSampleCode(String id, int digitLength) {
        if (digitLength > 5 && (id.equalsIgnoreCase("0999") || id.equals("00999") || id.equals("000999"))) {
            return true;
        } else if ((digitLength > 4 && digitLength < 6) && (id.equalsIgnoreCase("0999") || id.equals("00999"))) {
            return true;
        } else if ((digitLength <= 4) && (id.equalsIgnoreCase("0999") || id.equalsIgnoreCase("999"))) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean checkIfWaterCode(String id, int digitLength) {
        if (digitLength > 5 && (id.equalsIgnoreCase("0991") || id.equals("00991") || id.equals("000991"))) {
            return true;
        } else if ((digitLength > 4 && digitLength < 6) && (id.equalsIgnoreCase("0991") || id.equals("00991"))) {
            return true;
        } else if ((digitLength <= 4) && (id.equalsIgnoreCase("0991") || id.equalsIgnoreCase("991"))) {
            return true;
        } else {
            return false;
        }
    }

    //To check water test

    public static boolean checkIfRateCheck(String id, int digitLength) {

        if (digitLength > 5 && (id.equalsIgnoreCase("009999") || id.equalsIgnoreCase("09999") || id.equals("9999"))) {
            return true;
        } else if ((digitLength > 4 && digitLength < 6) && (id.equalsIgnoreCase("09999") || id.equals("9999"))) {
            return true;
        } else if (digitLength <= 4 && id.equals("9999")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean checkIfRinsing(String id, int digitLength) {

        if (digitLength > 5 && (id.equalsIgnoreCase("9998") || id.equals("09998") || id.equals("009998"))) {
            return true;
        } else if ((digitLength > 4 && digitLength < 6) && (id.equalsIgnoreCase("9998") || id.equals("09998"))) {
            return true;
        } else if ((digitLength <= 4) && id.equals("9998")) {
            return true;
        } else {
            return false;
        }

    }

    public static boolean checkIfCleaning(String id, int digitLength) {

        if (digitLength > 5 && (id.equalsIgnoreCase("9997") || id.equals("09997") || id.equals("009997"))) {
            return true;
        } else if ((digitLength > 4 && digitLength < 6) && (id.equalsIgnoreCase("9997") || id.equals("09997"))) {
            return true;
        } else if ((digitLength <= 4) && (id.equals("9997"))) {
            return true;
        } else {
            return false;
        }

    }

    public static String getOnlyDecimalFromString(String record) {
        record = record.trim();
        char[] chars = record.toCharArray();
        String result = "";
        if (record.startsWith("-")) {
            result = result + "-";
        }
        for (char c : chars) {

            if (Character.isDigit(c) || Character.toString(c).equals(".")) {
                result = result + c;
            }
        }
        return result.trim();
    }


    public static String getJsonFromObject(Object obj) {


        String jsonString = null;

        try {
            ObjectMapper mapper = new ObjectMapper();
            jsonString = mapper.writeValueAsString(obj);

        } catch (JsonGenerationException ex) {
            ex.printStackTrace();
        } catch (JsonMappingException ex) {
            ex.printStackTrace();

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return jsonString;
    }

    public static String getTheUnit(Context ctx, int check) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {

            if (check == UNIT_FOR_QUANTITY) {
                return "Ltrs";
            } else {
                return "Lt";
            }


        } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            if (check == UNIT_FOR_QUANTITY) {
                return "Kgs";
            } else {
                return "Kg";
            }
        } else if (amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            if (check == UNIT_FOR_QUANTITY) {
                return "Kgs";
            } else {
                return "Kg";
            }
        } else {
            if (check == UNIT_FOR_QUANTITY) {
                return "Ltrs";
            } else {
                return "Lt";
            }
        }

    }

    public static String getAllConfiguration(Context ctx) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        String configString = "";

        configString = "\n" + " * WS current format details" + "\n" +
                " prefix: " + amcuConfig.getWeighingPrefix() + ","
                + " suffix: " + amcuConfig.getWeighingSuffix() + ","
                + " separator: " + amcuConfig.getWeighingSeperator() + "\n";
        return configString;

    }

    public static String toJson(Object obj) {


        String jsonString = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            jsonString = mapper.writeValueAsString(obj);

        } catch (JsonGenerationException ex) {
            ex.printStackTrace();
        } catch (JsonMappingException ex) {
            ex.printStackTrace();

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return jsonString;
    }

    public static boolean checkIfVaildFarmerId(String str) {
        int id = 0;

        try {
            id = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (id > 0) {
            return true;
        } else {
            return false;
        }

    }

    public static double getCLR(double fat, double snf) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        DecimalFormat decimalFormatClr =
                new ChooseDecimalFormat().getDecimalFormatTypeForDisplay(AppConstants.CLR);
        double mClr = 25.0;
        try {
            mClr = ((amcuConfig.getSnfCons() * snf)
                    - (amcuConfig
                    .getFatCons() * fat)) - (amcuConfig
                    .getConstant());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (mClr < 0) {
            mClr = 0;
        }
        return Double.valueOf(decimalFormatClr.format(mClr));
    }


    public static String getDateDDMMYY(String str, int check) {

        String[] strMonth = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul",
                "Aug", "Sep", "Oct", "Nov", "Dec"};

        int month = 0;

        String[] strArray = str.split("-");

        for (int i = 0; i < strMonth.length; i++) {
            if (strArray[1].equalsIgnoreCase(strMonth[i])) {
                month = i + 1;
            }
        }

        if (check == 0) {
            //dd-mm-yy
      /*      return padding(Integer.parseInt(strArray[0])) + "-" + padding(Integer.parseInt(String.valueOf(month)))
                    + "-" + padding(Integer.parseInt(strArray[2].substring(2, 4)));*/

            return padding(Integer.parseInt(strArray[2])) + "-" + padding(Integer.parseInt(strArray[1]))
                    + "-" + padding(Integer.parseInt(strArray[0].substring(2, 4)));
        } else if (check == 1) {
            //dd-mmm-yyyy
            return strArray[2] + "-" + padding(Integer.parseInt(String.valueOf(month)))
                    + "-" + padding(Integer.parseInt(strArray[0]));
        } else if (check == 11) {
            //dd-mm-yyyy
            return padding(Integer.parseInt(strArray[0])) + "-" + padding(Integer.parseInt(String.valueOf(month)))
                    + "-" + strArray[2];
        } else {
            return padding(Integer.parseInt(strArray[0])) + "-" + strArray[1];
        }

    }

    public static boolean salesWeightValidation(String colWeight, String salesWeight) {
        double dcolWeight = 0, dsalesWeight = 0;

        try {
            dcolWeight = Double.parseDouble(colWeight);
            dsalesWeight = Double.parseDouble(salesWeight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return false;
        }

        if (dcolWeight >= dsalesWeight) {
            return true;
        } else {
            return false;
        }

    }

    public static double enableSales(Context ctx, String milkType, String date, String shift) {
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        SalesRecordDao salesRecordDao = (SalesRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_SALES);
        double qty = 0, qtySold = 0;

        if (date == null) {
            date = getTodayDateAndTime(1, ctx, true);
            shift = getCurrentShift();
        }
        try {
            ArrayList<ReportEntity> reportEntities = collectionRecordDao.findAllByDateShiftAndMilkType(date, shift, milkType);
            for (int i = 0, len = reportEntities.size(); i < len; i++) {
                try {
                    qty = qty + Double.valueOf(reportEntities.get(i).getQuantity());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String quantity = salesRecordDao.getTotalSalesCurrentSession(date, shift, milkType);
            qtySold = Double.parseDouble(quantity.substring(0, quantity.indexOf("=")));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Double.valueOf(new ChooseDecimalFormat().getWeightDecimalFormat().format((qty - qtySold)));

    }

    public static boolean validPrefixForBarcode(String barcode, Context ctx) {
        if (!barcode.startsWith("STPL")
                && !barcode.startsWith("SIN")) {

            displayErrorToast("Invalid barcode", ctx);

            return false;

        } else {
            return true;
        }
    }

    public static String getDuplicateCenterIdOrBarCode(String id, String barCode, Context ctx) {
        String duplicateMsg = "";
        boolean validation = false;

        String centerId = id;
        int intId = -1;

        try {
            intId = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (intId != -1) {
            centerId = String.valueOf(intId);
        }
        //To center Id and barcode

        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        try {
            if (barCode != null && !barCode.equalsIgnoreCase("")) {
                validation = dbh.checkDuplicateCenterIdOrBarcode(barCode, CHECK_DUPLICATE_CENTERBARCODE);
            } else {
                validation = false;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (validation) {
            duplicateMsg = duplicateMsg + "Center Barcode: " + barCode + "\n";
            return duplicateMsg;
        }

        //Removed database close;
        //Check for duplicate center ID
        dbh = DatabaseHandler.getDatabaseInstance();

        try {
            validation = dbh.checkDuplicateCenterIdOrBarcode(centerId, CHECK_DUPLICATE_CENTERCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (validation) {
            duplicateMsg = duplicateMsg + "Center Id: " + id + "\n";
            return duplicateMsg;
        }

        //Removed database close;


        if (duplicateMsg.equalsIgnoreCase("")) {
            duplicateMsg = null;
        }
        return duplicateMsg;
    }

    public static String getSnfOrClrForPrint(ReportEntity repEntity, Context mContext) {
        String snfLine = null;
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (amcuConfig.getAllowFSCInPrint()) {
            snfLine = "CLR: " + repEntity.getDisplayClr() + "\n"
                    + "SNF: " + repEntity.getPrinterSnf() + "\n";
        } else if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")
                && repEntity.collectionType.equalsIgnoreCase(REPORT_TYPE_MCC))
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")
                && repEntity.collectionType.equalsIgnoreCase(REPORT_TYPE_FARMER))) {
            snfLine = "CLR: "
                    + repEntity.getDisplayClr() + "\n";
        } else {
            snfLine = "SNF: "
                    + repEntity.getPrinterSnf() + "\n";
        }
        return snfLine;

    }

    private static String getSnfOrClrForSMS(ReportEntity reportEntity) {
        String snfLine = null;
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (amcuConfig.getAllowFSCInSMS()) {
            snfLine = "Clr   :  " + reportEntity.getDisplayClr() + "\n"
                    + "Snf % :  " + reportEntity.getDisplaySnf() + "\n";
        } else if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") &&
                reportEntity.getCollectionType().equalsIgnoreCase(REPORT_TYPE_MCC))
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") &&
                reportEntity.getCollectionType().equalsIgnoreCase(REPORT_TYPE_FARMER))) {

            snfLine = "Clr   :  " + reportEntity.getDisplayClr() + "\n";
        } else {
            snfLine = "Snf % :  " + reportEntity.getDisplaySnf() + "\n";
        }
        return snfLine;
    }

    public static Dialog showSyncing(Context mContext) {
        try {
            Dialog dialog = new Dialog(mContext, android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.syncing_overlay);
            dialog.setCancelable(false);
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // dupilicate print
    public static String printReceiptMessage(ReportEntity repEntity, Context ctx) {
        String message = null;
        String farmerIdText = null;
        farmerIdText = "Center ID: " + repEntity.farmerId;
        String route = "";
        if (AmcuConfig.getInstance().getAllowRouteInReport()) {
           /* DatabaseManager databaseManager = new DatabaseManager(ctx);
            String temp_route = databaseManager.getRouteFromChillingCenterTable(repEntity.farmerId);*/
            if (null != repEntity.centerRoute && !repEntity.centerRoute.equalsIgnoreCase("") && !repEntity.centerRoute.equalsIgnoreCase("null")) {
                route = "Route: " + repEntity.centerRoute + "\n";
            } else {
                route = "";
            }
        }
        String serialNumber = "";
        if (AmcuConfig.getInstance().getAllowSequenceNumberInPrintAndReport()) {
            serialNumber = "Serial number: " + repEntity.sampleNumber + "\n";
        }
        message = "\n" + "name: " + repEntity.farmerName + "\n"
                + farmerIdText + "\n"
                + serialNumber
                + "Cattle Type: " + repEntity.milkType + "\n"
                + route
                + "QTY: "
                + repEntity.quantity + getTheUnit(ctx, 0) + "\n" + "FAT: "
                + repEntity.getPrinterFat() + "\n" + Util.getSnfOrClrForPrint(repEntity, ctx) +
                "RATE: " + repEntity.getPrinterRate()
                + " Rs" + "\n" +
                "Amount: " + repEntity.getPrinterAmount() + " Rs" + "\n";
        String printData = "\n" + "Date: "
                + Util.getTodayDateAndTime(1, ctx, true) + "\n"
                + "Date & Time: " + repEntity.time + "\n" + "Shift: " + repEntity.postShift + "\n" + message + "\n";
        return printData;

    }

    public static String getRouteFromChillingCenter(Context ctx, String centerId) {
        String route = "NA";
        DatabaseManager databaseManager = new DatabaseManager(ctx);
        route = databaseManager.getRouteFromChillingCenterTable(centerId);
        if (null != route && !route.equalsIgnoreCase("") && !route.equalsIgnoreCase("null")) {
            //Correct route
        } else {
            route = "NA";
        }

        return route;
    }

    //As saved in configuration get the formula, to calculate snf
    public static double getSNF(double fat, double clr) {
        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        DecimalFormat decimalFormat =
                chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        double dSnf = 0;
        try {
            dSnf = ((clr +
                    (amcuConfig.getFatCons() * fat
                            + amcuConfig.getConstant()))
                    / amcuConfig.getSnfCons());
            if (dSnf < 0) {
                dSnf = 0.0;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return Double.valueOf(decimalFormat.format(dSnf));
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    // SMS send for snf and clr

    private static ArrayList<String> getValidDatelistItem(Context ctx) {
        ArrayList<String> allPossibleDateShift = new ArrayList<>();

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        int MAX_SHIFT = amcuConfig.getNumberShiftCanBeEdited();

        String currentDate = getOperationDate(0, 3);
        String currentShift = Util.getCurrentShift();

        switch (MAX_SHIFT) {

            case 0: {
                break;
            }
            case 1: {
                allPossibleDateShift.add(currentDate + AllReportsActivity.dateToShiftSeparater + currentShift);
                break;
            }
            case 2: {
                if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {
                    allPossibleDateShift.add(currentDate + AllReportsActivity.dateToShiftSeparater + currentShift);
                    currentDate = Util.getOperationDate(-1, 3);
                    allPossibleDateShift.add(currentDate + AllReportsActivity.dateToShiftSeparater + AppConstants.Shift.EVENING);
                } else {
                    allPossibleDateShift.add(currentDate + AllReportsActivity.dateToShiftSeparater + currentShift);
                    allPossibleDateShift.add(currentDate + AllReportsActivity.dateToShiftSeparater + AppConstants.Shift.MORNING);

                }
                break;
            }

            default: {

                if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {

                    int shiftCheck = MAX_SHIFT - 1;
                    int reminderCheck = shiftCheck % 2;
                    shiftCheck = shiftCheck / 2;
                    int totalDayToCheck = 0;

                    allPossibleDateShift.add(currentDate + AllReportsActivity.dateToShiftSeparater + currentShift);

                    for (int i = 1; i <= shiftCheck; i++) {
                        String changeDate = Util.getOperationDate(-i, 3);
                        allPossibleDateShift.add(changeDate + AllReportsActivity.dateToShiftSeparater + AppConstants.Shift.EVENING);
                        allPossibleDateShift.add(changeDate + AllReportsActivity.dateToShiftSeparater +
                                AppConstants.Shift.MORNING);
                        totalDayToCheck = totalDayToCheck + 1;
                    }

                    if (reminderCheck == 1) {
                        String changeDate = Util.getOperationDate(-(totalDayToCheck + 1), 3);
                        allPossibleDateShift.add(changeDate + AllReportsActivity.dateToShiftSeparater + AppConstants.Shift.EVENING);
                    }

                } else {

                    int shiftCheck = MAX_SHIFT - 2;
                    int reminderCheck = shiftCheck % 2;
                    shiftCheck = shiftCheck / 2;
                    int totalDayToCheck = 0;


                    allPossibleDateShift.add(currentDate + AllReportsActivity.dateToShiftSeparater + currentShift);
                    allPossibleDateShift.add(currentDate + AllReportsActivity.dateToShiftSeparater + AppConstants.Shift.MORNING);

                    for (int i = 1; i <= shiftCheck; i++) {
                        String changeDate = Util.getOperationDate(-i, 3);
                        allPossibleDateShift.add(changeDate + AllReportsActivity.dateToShiftSeparater + AppConstants.Shift.EVENING);
                        allPossibleDateShift.add(changeDate + AllReportsActivity.dateToShiftSeparater + AppConstants.Shift.MORNING);
                        totalDayToCheck = totalDayToCheck + 1;
                    }

                    if (reminderCheck == 1) {
                        String changeDate = Util.getOperationDate(-(totalDayToCheck + 1), 3);
                        allPossibleDateShift.add(changeDate + AllReportsActivity.dateToShiftSeparater + AppConstants.Shift.EVENING);
                    }

                }

                break;
            }
        }

        return allPossibleDateShift;
    }

    public static long getCurrentTimeInMili() {
        Calendar calendar;
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return calendar.getTimeInMillis();
    }

    public static void writeRatechartLogs(RatechartDetailsEnt rateChartDetailsEnt,
                                          Context ctx, String addOrDelete, String usbOrCloud) {

        Intent intent = new Intent(ctx, WriteRateChartLogs.class);

        intent.putExtra("RATECHARTNAME", rateChartDetailsEnt.rateChartName);
        intent.putExtra("USBORCLOUDORMANUAL", usbOrCloud);
        intent.putExtra("MODIFIEDTIME", System.currentTimeMillis());
        intent.putExtra("VALIDITYFROM", rateChartDetailsEnt.rateValidityFrom);
        intent.putExtra("VALIDITYTO", rateChartDetailsEnt.rateValidityTo);
        intent.putExtra("ADDORDELETE", addOrDelete);
        intent.putExtra("S_SHIFT", "NA");
        intent.putExtra("E_SHIFT", "NA");

        ctx.startService(intent);

    }


    public static void writeRatechartLogs(RateChartPostEntity rateChart, Context ctx, String type, String usbOrCloud) {
        Intent intent = new Intent(ctx, WriteRateChartLogs.class);

        intent.putExtra("RATECHARTNAME", rateChart.rateChartName);
        intent.putExtra("USBORCLOUDORMANUAL", usbOrCloud);
        intent.putExtra("MODIFIEDTIME", System.currentTimeMillis());
        intent.putExtra("VALIDITYFROM", rateChart.startDate);
        intent.putExtra("VALIDITYTO", rateChart.endDate);
        intent.putExtra("ADDORDELETE", type);
        intent.putExtra("S_SHIFT", "NA");
        intent.putExtra("E_SHIFT", "NA");

        ctx.startService(intent);
    }


    public static SequenceCollectionRecord getCollectionReportFromReportEntity(ReportEntity reportEntity
            , Context context) {
        SequenceCollectionRecord seqCollRec = new SequenceCollectionRecord();
        try {
            int sequenceNumber = reportEntity.sampleNumber;
            seqCollRec.setSeqNum(sequenceNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Retrieve all the Collection Record Details
        seqCollRec.collectionTime = reportEntity.miliTime;
        seqCollRec.producerId = reportEntity.farmerId;
        seqCollRec.milkType = reportEntity.milkType;
        seqCollRec.status = reportEntity.status;
        seqCollRec.collectionType = reportEntity.collectionType;
        seqCollRec.mode = reportEntity.manual;


        int numberOfCans = 1;

        numberOfCans = reportEntity.numberOfCans;
        seqCollRec.numberOfCans = numberOfCans;
        seqCollRec.collectionRoute = reportEntity.centerRoute;
        seqCollRec.recordStatus = reportEntity.recordStatus;

        seqCollRec.userId = reportEntity.user;
        seqCollRec.agentId = reportEntity.agentId;


        seqCollRec.sampleNumber = reportEntity.sampleNumber;

        QualityParams qualityParams = new QualityParams();
        QuantityParams quantityParams = new QuantityParams();
        RateParams rateParams = new RateParams();

        ValidationHelper validationHelper = new ValidationHelper();


        qualityParams.snf = reportEntity.snf;
        qualityParams.fat = reportEntity.fat;
        qualityParams.clr = reportEntity.clr;
//        qualityParams.density = checkValidation.getDoubleFromString(
//                cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_)),0);
        qualityParams.awm = reportEntity.awm;
//        qualityParams.freezingPoint = checkValidation.getDoubleFromString(
//                cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_FR)),0);
        qualityParams.temperature = reportEntity.temp;


        qualityParams.qualityMode = reportEntity.qualityMode;
        qualityParams.qualityTime = reportEntity.milkAnalyserTime;


        qualityParams.maSerialNumber = String.valueOf(reportEntity.serialMa);
//        qualityParams.n = cursor.getString(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MA_NAME));


//        qualityParams.protein = checkValidation.getDoubleFromString(
//                cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_)),0);
//        qualityParams.lactose = checkValidation.getDoubleFromString(
//                cursor.getString(
//                        cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_L)),0);


        rateParams.rate = reportEntity.rate;
        rateParams.amount = reportEntity.amount;


        rateParams.bonus = reportEntity.bonus;
        rateParams.rateMode = reportEntity.rateMode;
        rateParams.isRateCalculated = DatabaseEntity.getRateCalculated(reportEntity.rateCalculation);
        rateParams.rateChartName = reportEntity.rateChartName;


        quantityParams.weighingQuantity = reportEntity.quantity;
        quantityParams.kgQuantity = reportEntity.kgWeight;
        quantityParams.ltrQuantity = reportEntity.ltrsWeight;
        quantityParams.quantityMode = reportEntity.quantityMode;
        quantityParams.tippingStartTime = reportEntity.tippingStartTime;
        quantityParams.tippingEndTime = reportEntity.tippingEndTime;

        quantityParams.quantityTime = reportEntity.weighingTime;


        seqCollRec.qualityParams = qualityParams;
        seqCollRec.quantityParams = quantityParams;
        seqCollRec.rateParams = rateParams;

        return seqCollRec;
    }


    public static String getTodayDateAndTime(int dTime) {
        final Calendar c = new DateAndTime().getCalendarInstance();
        Date date = c.getTime();

        String strDate = String.valueOf(date);
        String DAY, MONTH;

        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c.get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH);
        int sec = c.get(Calendar.SECOND);

        if (day > 9) {
            DAY = String.valueOf(day);
        } else {
            DAY = "0" + String.valueOf(day);
        }

        if (month > 9) {
            MONTH = String.valueOf(month);
        } else {

            MONTH = "0" + String.valueOf(month);

        }

        String time = padding(hour) + padding(min);
        String time1 = padding(hour) + ":" + padding(min);
        String time2 = padding(hour) + "_" + padding(min);

        if (dTime == 0) {
            return time;
        } else if (dTime == 1) {
            return DAY + "-" + strDate.substring(4, 7) + "-"
                    + String.valueOf(year);
        } else if (dTime == 2) {
            return DAY + "-" + strDate.substring(4, 7) + "-"
                    + String.valueOf(year) + "Time:" + time;
        } else if (dTime == 5) {
            return DAY + "_" + strDate.substring(4, 7) + "_"
                    + String.valueOf(year) + "T" + time2;
        } else if (dTime == 6) {
            return time1 + ":" + padding(sec);
        } else if (dTime == 3) {
            return time1;

        } else if (dTime == 7) {
            month = month + 1;
            return DAY + "-" + padding(month) + "-" + String.valueOf(year);
        } else if (dTime == 4) {
            return DAY + "-" + MONTH + "-" + String.valueOf(year);
        } else {
            return DAY + "-" + strDate.substring(4, 7) + "-"
                    + String.valueOf(year);
        }

    }


    public static ReportEntity getReportEntityFromSalesRecord(SalesRecordEntity salesRecordEntity) {

        ReportEntity reportEntity = new ReportEntity();

        reportEntity.farmerId = salesRecordEntity.getSalesId();
        reportEntity.farmerName = salesRecordEntity.getName();
        reportEntity.snf = salesRecordEntity.getSnf();
        reportEntity.fat = salesRecordEntity.getFat();
        reportEntity.user = salesRecordEntity.getUser();
        reportEntity.manual = salesRecordEntity.getManual();
        reportEntity.postShift = salesRecordEntity.getPostShift();
        reportEntity.amount = salesRecordEntity.getAmount();
        reportEntity.quantity = salesRecordEntity.getQuantity();
        reportEntity.postDate = salesRecordEntity.getPostDate();
        reportEntity.txnNumber = 0;
        reportEntity.milkType = salesRecordEntity.getMilkType();
        reportEntity.rate = salesRecordEntity.getRate();
//        reportEntity.lDate =cursor.getLong(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LDATE));
        reportEntity.socId = salesRecordEntity.getSocId();

        reportEntity.bonus = 0;

        reportEntity.miliTime = salesRecordEntity.getCollectionTime();
        reportEntity.time = getTimeFromMiliSecond(salesRecordEntity.collectionTime);
        reportEntity.awm = salesRecordEntity.getAwm();
        reportEntity.clr = salesRecordEntity.getClr();
        reportEntity.status = salesRecordEntity.getStatus();
        reportEntity.quantityMode = salesRecordEntity.getWeightManual();

        reportEntity.qualityMode = salesRecordEntity.getManual();
        reportEntity.milkAnalyserTime = salesRecordEntity.getCollectionTime();
        reportEntity.weighingTime = salesRecordEntity.getCollectionTime();
        reportEntity.temp = salesRecordEntity.getTemperature();
        reportEntity.recordCommited = 1;

        reportEntity.milkType = salesRecordEntity.getMilkType();
        reportEntity.user = salesRecordEntity.getUser();
        reportEntity.collectionType = Util.REPORT_TYPE_SALES;

        reportEntity.lactose = 0;
        reportEntity.conductivity = 0;
        reportEntity.density = 0;
        reportEntity.calibration = "NA";
        reportEntity.maSerialNumber = "NA";

        return reportEntity;

    }


    public static ReportEntity getReportEntityFromSplitRecord(AgentSplitEntity agentSplitEntity) {

        ReportEntity reportEntity = new ReportEntity();

        QuantityParams quantityParams = agentSplitEntity.quantityParams;
        QualityParams qualityParams = agentSplitEntity.qualityParams;
        RateParams rateParams = agentSplitEntity.rateParams;


        reportEntity.farmerId = agentSplitEntity.producerId;
        reportEntity.farmerName = "";
        reportEntity.snf = qualityParams.snf;
        reportEntity.fat = qualityParams.fat;
        reportEntity.user = agentSplitEntity.userId;
        reportEntity.manual = agentSplitEntity.mode;
        reportEntity.postShift = agentSplitEntity.shift;
        reportEntity.amount = rateParams.amount;
        reportEntity.quantity = quantityParams.weighingQuantity;
        reportEntity.postDate = agentSplitEntity.postDate;
        reportEntity.txnNumber = 0;
        reportEntity.milkType = agentSplitEntity.milkType;
        reportEntity.rate = rateParams.rate;
//        reportEntity.lDate =cursor.getLong(
//                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_LDATE));
        reportEntity.socId = agentSplitEntity.centerId;

        reportEntity.bonus = rateParams.amount;

        reportEntity.miliTime = agentSplitEntity.collectionTime;
        reportEntity.time = getTimeFromMiliSecond(agentSplitEntity.collectionTime);
        reportEntity.awm = qualityParams.awm;
        reportEntity.clr = qualityParams.clr;
        reportEntity.status = agentSplitEntity.status;
        reportEntity.quantityMode = quantityParams.quantityMode;

        reportEntity.qualityMode = qualityParams.qualityMode;
        reportEntity.milkAnalyserTime = qualityParams.qualityTime;
        reportEntity.weighingTime = quantityParams.quantityTime;
        reportEntity.temp = qualityParams.temperature;
        reportEntity.recordCommited = agentSplitEntity.commited;

        reportEntity.milkType = agentSplitEntity.milkType;
        reportEntity.user = agentSplitEntity.userId;
        reportEntity.collectionType = Util.REPORT_TYPE_AGENT_SPLIT;

        reportEntity.lactose = qualityParams.lactose;
        reportEntity.conductivity = qualityParams.conductivity;
        reportEntity.density = qualityParams.density;
        reportEntity.calibration = qualityParams.calibration;
        reportEntity.maSerialNumber = qualityParams.maSerialNumber;

        return reportEntity;

    }

    public static String getTimeFromMiliSecond(long milliSeconds) {

        String dateFormat = "kk:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        String date = simpleDateFormat.format(calendar.getTime());

        return date;


    }

    public static double convertPercentageToKg(double qty, double fatOrSnf) {
        double returnKgValue = 0;
        DecimalFormat decimalFormat = new DecimalFormat("#0.00");

        try {
            returnKgValue = (qty * fatOrSnf) / 100;
        } catch (Exception e) {

        }

        return Double.parseDouble(decimalFormat.format(returnKgValue));

    }

    public static boolean ping(String ip) {
        Runtime runtime = Runtime.getRuntime();
        try {
//            InetAddress inetAddress = InetAddress.getByName(ip);
//            boolean pingResult = inetAddress.isReachable(2000);
            Process mIpAddrProcess = runtime.exec("/system/bin/ping -c 1 " + ip);
            int pingResult = mIpAddrProcess.waitFor();
            Log.v(PROBER, " PING VALUE for " + ip + " : " + pingResult);
            return pingResult == 0;
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(" Exception:" + e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<byte[]> divideArray(byte[] data, int maxBuffer) {

        List<byte[]> result = new ArrayList<byte[]>();
        int start = 0;
        while (start < data.length) {
            int end = Math.min(data.length, start + maxBuffer);
            result.add(Arrays.copyOfRange(data, start, end));
            start += maxBuffer;
        }
        Log.v(SmartCCConstants.PROBER, "Packet list size: " + result.size());
        return result;
    }

    public static long getCurrentTimeInMillis() {
        Date currentDate = Calendar.getInstance().getTime();
        long timeInMillis = currentDate.getTime();
        Log.v(PROBER, "Fetched time in millis: " + timeInMillis);
        return timeInMillis;
    }

    public static String formatTimeWithTimeZone() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        String formattedTime = sdf.format(currentDate);
        Log.v(PROBER, "Fetched time: " + formattedTime);
        return formattedTime;
    }

    public static String getCurrentDateTime() {
        Date currentDate = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String formattedTime = sdf.format(currentDate);
        Log.v(PROBER, "current date time: " + formattedTime);
        return formattedTime;

    }

    public static String convertDate(String inputDate) {
        String outputDate = "";
        SimpleDateFormat inputSdf = new SimpleDateFormat("dd-MM-yy");
        SimpleDateFormat outputSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = inputSdf.parse(inputDate);
            outputDate = outputSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDate;
    }


    public static void displayMACleaningAlert(Context context, View.OnClickListener onClickListener, boolean mandatory) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_sendingmail, null);

        TextView tvMailHeader = (TextView) view.findViewById(R.id.tvheader);
        TextView tvAlertText = (TextView) view.findViewById(R.id.tvAlert);
        ImageView ivLogo = (ImageView) view.findViewById(R.id.ivsplash);
        ivLogo.setVisibility(View.GONE);
        RelativeLayout ProgressL = (RelativeLayout) view
                .findViewById(R.id.progress_layout);
        ProgressL.setVisibility(View.GONE);
        tvAlertText.setVisibility(View.VISIBLE);

        tvMailHeader.setText("MA Cleaning Required!");
        // TODO
        String alertText = "Please start MA cleaning process.";

        tvAlertText.setText(alertText);
        tvAlertText.setTextSize(23f);

        final Button btnResend = (Button) view.findViewById(R.id.btnResend);
        final Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        if (mandatory)
            btnCancel.setVisibility(View.GONE);
        btnCancel.setText("LATER");
        btnResend.setText("OK");

        btnResend.requestFocus();
        final AlertDialog alertDialog = alertBuilder.create();
        alertBuilder.setCancelable(false);

        alertDialog.setView(view);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        // avoids displaying the dialog if the parent activity has been destroyed thus avoiding BadTokenException
//        if (!((Activity) context).isFinishing())
        alertDialog.show();

        btnResend.setOnClickListener(onClickListener);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        // To display the alert dialog in center

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 450;
        lp.height = 450;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);

    }

    public static boolean checkIfOnlySampleCode(String id) {
        boolean returnValue = false;

        int intId = -1;
        try {
            intId = Integer.parseInt(id);

            if ((intId == 999)) {
                returnValue = true;
            }
        } catch (NullPointerException e1) {

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public static boolean isDigitKey(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_PERIOD:
            case KeyEvent.KEYCODE_MINUS:
            case KeyEvent.KEYCODE_NUMPAD_SUBTRACT:
            case KeyEvent.KEYCODE_NUMPAD_DOT:
            case KeyEvent.KEYCODE_NUMPAD_0:
            case KeyEvent.KEYCODE_NUMPAD_1:
            case KeyEvent.KEYCODE_NUMPAD_2:
            case KeyEvent.KEYCODE_NUMPAD_3:
            case KeyEvent.KEYCODE_NUMPAD_4:
            case KeyEvent.KEYCODE_NUMPAD_5:
            case KeyEvent.KEYCODE_NUMPAD_6:
            case KeyEvent.KEYCODE_NUMPAD_7:
            case KeyEvent.KEYCODE_NUMPAD_8:
            case KeyEvent.KEYCODE_NUMPAD_9:
            case KeyEvent.KEYCODE_DEL:
            case KeyEvent.KEYCODE_FORWARD_DEL:
                return true;
            default:
                return false;
        }
    }

    public static String getMilkTypeInitials(String type) {
        String initial;
        switch (type) {
            case "COW":
                initial = "C";
                break;
            case "BUFFALO":
                initial = "B";
                break;
            case "MIXED":
                initial = "M";
                break;
            default:
                initial = "M";
        }
        return initial;
    }

    public static ArrayList<DispatchPostEntity> getDispatchReportForCurrentShift(Context context, long longDate) {

        DispatchRecordDao dispatchRecordDao = (DispatchRecordDao) DaoFactory.getDao(Util.REPORT_TYPE_DISPATCH);

        String date = getDateFromLongDate(longDate);
        ArrayList<DispatchPostEntity> entityArrayList = (ArrayList<DispatchPostEntity>) dispatchRecordDao.getDailyShiftDispatchReport(
                date, getCurrentShift());
        return entityArrayList;
    }

    private static String getDateFromLongDate(Long lDate) {
        String sDate = String.valueOf(lDate);
        String finalDate = "";
        if (sDate.length() == 8) {
            int day = Integer.parseInt(sDate.substring(sDate.length() - 2));
            int month = Integer.parseInt(sDate.substring(sDate.length() - 4, sDate.length() - 2));
            int year = Integer.parseInt(sDate.substring(0, sDate.length() - 4));

            finalDate = padding(year) + "-" + padding(month) + "-" + padding(day);
        } else
            Log.v("Util", "Invalid long date");
        return finalDate;
    }

    public static ReportEntity getReportEntityFromDispatchRecord(
            DispatchPostEntity dispatchPostEntity) {

        ReportEntity reportEntity = new ReportEntity();

        reportEntity.farmerId = dispatchPostEntity.supervisorId;
        QualityParamsPost qualityParams = dispatchPostEntity.qualityParams;
        QuantityParamspost quantityParams = dispatchPostEntity.quantityParams;
        MilkAnalyser milkAnalyser = qualityParams.milkAnalyser;
        QualityReadingData reading = milkAnalyser.qualityReadingData;

        reportEntity.snf = reading.snf;
        reportEntity.fat = reading.fat;
        reportEntity.postShift = dispatchPostEntity.shift;
        reportEntity.quantity = quantityParams.soldQuantity;
        reportEntity.postDate = dispatchPostEntity.date;
        reportEntity.txnNumber = 0;
        reportEntity.milkType = dispatchPostEntity.milkType;


        reportEntity.bonus = 0;

        reportEntity.miliTime = dispatchPostEntity.timeInMillis;
        reportEntity.time = getTimeFromMiliSecond(dispatchPostEntity.timeInMillis);
        reportEntity.recordCommited = 1;

        reportEntity.collectionType = Util.REPORT_TYPE_DISPATCH;

        reportEntity.lactose = 0;
        reportEntity.conductivity = 0;
        reportEntity.density = 0;
        reportEntity.calibration = "NA";
        reportEntity.maSerialNumber = "NA";

        return reportEntity;

    }

    public static boolean mobileDataStatus(Context ctx) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
            // Util.displayErrorToast("mobile data status "+mobileDataEnabled,ctx);
        } catch (Exception e) {
            // Some problem accessible private API
            // TODO do whatever error handling you want here
        }

        return mobileDataEnabled;

    }

    public static void setMobileDataEnabled(boolean enabled, Context context) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod =
                connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);

        if (enabled) {
            // displayErrorToast("Turning on mobile data",context);
        } else {
            //  displayErrorToast("Turning off mobile data",context);
        }
    }

    public static String getSDCardPath() {
        if (directoryExists(SmartCCConstants.SDCARD_PATH))
            return SmartCCConstants.SDCARD_PATH;
        else
            return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private static boolean directoryExists(String path) {
        File root = new File(path);
        if (root.exists()) {
            String[] files = root.list();
            if (files != null && files.length > 0)
                return true;
            else
                return false;
        }
        return false;
    }

    public boolean checkMobileNetwork(Context ctx) {
        NetworkInfo info = getNetworkInfo(ctx);
        if (info != null)
            displayErrorToast("Internet " + info.getTypeName(), ctx);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }


}
