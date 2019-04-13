package com.devapp.devmain.main;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.WriteRateChartLogs;
import com.devapp.devmain.user.Util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yyy on 14/6/16.
 */
public class CustomRateChartUtil {

    private static Context mContext;
    private static SessionManager session;
    private static AmcuConfig amcuConfig;
    private static DatabaseHandler databaseHandler;
    private static CustomRateChartUtil myRateChartUtil = null;

    private CustomRateChartUtil() {

    }

    public static CustomRateChartUtil getInstance(Context context) {
        if (myRateChartUtil == null) {
            synchronized (CustomRateChartUtil.class) {
                if (myRateChartUtil == null) {
                    myRateChartUtil = new CustomRateChartUtil();
                    mContext = context;
                    session = new SessionManager(context);
                    amcuConfig = AmcuConfig.getInstance();
                    databaseHandler = DatabaseHandler.getDatabaseInstance();
                }
            }

        }
        return myRateChartUtil;
    }

    public void setMyrateChart() {
        amcuConfig.setRateChartForCow(databaseHandler.getSelectedMyRateChart("COW"));
        amcuConfig.setRateChartForBuffalo(databaseHandler.getSelectedMyRateChart("BUFFALO"));
        amcuConfig.setRateChartForMixed(databaseHandler.getSelectedMyRateChart("MIXED"));

    }

    public void deleteExpireRateChart() {
        try {
            databaseHandler.deleteExpireRateChartBeforeCurrentDate();
            databaseHandler.deleteMyRateChartBeforeEndDate("COW", "GENERAL");
            databaseHandler.deleteMyRateChartBeforeEndDate("BUFFALO", "GENERAL");
            databaseHandler.deleteMyRateChartBeforeEndDate("MIXED", "GENERAL");
        } catch (Exception e) {
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            writeMyRatechartDatabaseLogs(errors.toString());
        }
    }

    public double getRateFromMyRateChart(double fat, double snf, Context context) {
        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        double kgfat = 0, kgsnf = 0, endFat = 0, endSnf = 0, startFat = 0, startSnf = 0;

        String rateChartName = amcuConfig.getRateChartName();
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();
        Cursor cursor = databaseHandler.getSelectedMyRateChartList(rateChartName);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    kgfat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_KG_FAT_RATE)));
                    kgsnf = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_KG_SNF_RATE)));
                    endFat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_FAT)));
                    endSnf = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_END_SNF)));
                    startFat = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_FAT)));
                    startSnf = Double.parseDouble(cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_MY_RATECHART_START_SNF)));
                } while (cursor.moveToNext());
            }
        }
        double rate = 0;
        if ((Double.valueOf(fat) >= startFat && Double.valueOf(fat) <= endFat) && (Double.valueOf(snf) >= startSnf && Double.valueOf(snf) <= endSnf)) {
            rate = (Double.valueOf(kgfat) * Double.valueOf(fat) + Double.valueOf(kgsnf) * Double.valueOf(snf)) / 100;
        } else {
            rate = 0;
        }

        return rate;
    }

    public void writeMyRatechartDatabaseLogs(String error) {

        try {
            Util.generateNoteOnSD("MyRatechartLogs", error, mContext, "smartAmcuReports");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void writeMyRatechartLogs(String rateChartName, long rateValidityFrom, long rateValidityTo, String startShift, String endShift, Context ctx, String addOrDelete, long createDate) {
        Date sdate = null, edate = null;
        long longstartdate = 0, longenddate = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        sdate = new Date(rateValidityFrom);
        edate = new Date(rateValidityTo);
        Intent intent = new Intent(ctx, WriteRateChartLogs.class);
        intent.putExtra("RATECHARTNAME", rateChartName);
        intent.putExtra("USBORCLOUDORMANUAL", Util.MANUAL);
        intent.putExtra("MODIFIEDTIME", createDate);
        intent.putExtra("VALIDITYFROM", simpleDateFormat.format(sdate));
        intent.putExtra("VALIDITYTO", simpleDateFormat.format(edate));
        intent.putExtra("ADDORDELETE", addOrDelete);
        intent.putExtra("S_SHIFT", startShift);
        intent.putExtra("E_SHIFT", endShift);
        ctx.startService(intent);

    }
}
