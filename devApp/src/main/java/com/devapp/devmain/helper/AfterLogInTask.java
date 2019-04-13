package com.devapp.devmain.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import com.devapp.devmain.ConsolidationPost.ConsolidatedRecordsSynchronizer;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.httptasks.BackgroundTasksMgr;
import com.devapp.devmain.httptasks.ConfigurationPush;
import com.devapp.devmain.httptasks.PostFarmerRecords;
import com.devapp.devmain.main.LoginActivity;
import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.milkline.service.EndShiftReceiver;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.DeleteExcelService;
import com.devapp.devmain.services.DeleteSentSMS;
import com.devapp.devmain.services.PurgeData;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.util.Calendar;

/**
 * Created by u_pendra on 28/2/17.
 */

public class AfterLogInTask {

    public static int staticCount = 0;
    ConsolidatedRecordsSynchronizer consolidatedRecordsSynchronizer;
    private Context mContext;
    private AmcuConfig amcuConfig;
    private DatabaseHandler databaseHandler;
    private SessionManager sessionManager;
    private int MAX_UNSENT_LIMIT = 0;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    public AfterLogInTask(Context context) {
        this.mContext = context;
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(mContext);
        consolidatedRecordsSynchronizer =
                ConsolidatedRecordsSynchronizer.getInstance(context);
    }

    public static void startPurgeDataService(Context mContext) {
        Intent dataPurgeIntent = new Intent(mContext,
                PurgeData.class);
        mContext.startService(dataPurgeIntent);
    }

    public boolean checkForUnsentAlert() {

        if (!Util.isNetworkAvailable(mContext)) {
            return false;
        } else {
            return checkForUnSentRecords();
        }
    }

    public boolean checkForUnSentRecords() {

        int count = consolidatedRecordsSynchronizer.getAllUnsentCount();

        if (count > 0) {
            return true;
        } else {
            Util.displayErrorToast("No unsent collection records available!", mContext);
            return false;
        }
    }

    public boolean unsentCleaningRecordsAvailable() {
        boolean retValue = false;
//        if (!SmartCCConstants.unsentCleaningData.equals(""))
        if (!amcuConfig.getUnsentCleaningData().equals(""))
            retValue = true;
        return retValue;
    }

    public void createAndSendUnsentRecords() {

        if (Util.checkForPendrive()) {
            Util.displayErrorToast("Please wait....", mContext);
            ReportHelper reportHelper = new ReportHelper(mContext);
            reportHelper.createUnsentRecords();
            Util.displayErrorToast("Data saved in pendrive successfully!", mContext);

        } else {
            Util.displayErrorToast("Please connect the pendrive and try again!", mContext);
        }
    }

    public void sendUnsentCleaningRecords() {

        if (Util.checkForPendrive()) {
            Util.displayErrorToast("Please wait....", mContext);
            ReportHelper reportHelper = new ReportHelper(mContext);
            reportHelper.createUnsentCleaningRecords();
            Util.displayErrorToast("Data saved in pendrive successfully!", mContext);

        } else {
            Util.displayErrorToast("Please connect the pendrive and try again!", mContext);
        }
    }


    public void doAfterLogInTasks() {
        initializeFieldAndParameters();
        deleteObsoleteSMS();
        deleteOldExcelFiles();
        processIncompleteRecords();
        startFarmerPushService();
        startBackgroundTask();
        resetSequenceNumber();
        setMA1Config();
        startConfigurationTask();


        if (!amcuConfig.getEndShiftSuccess()
                && amcuConfig.getCollectionEndShift()) {
            registerEndShiftAlarm(-1);
        } else if (amcuConfig.getKeyEnableCollectionConstraints()) {
            registerEndShiftAlarm(0);
        }

        databaseHandler.createTheHistoryUserIfNotThere();
    }

    public void deleteObsoleteSMS() {
        mContext.startService(new Intent(mContext, DeleteSentSMS.class));
    }

    private void setMA1Config() {
        if (!amcuConfig.getMA().equalsIgnoreCase(amcuConfig.getMa1Name())) {
            amcuConfig.saveMA1Details(amcuConfig.getMA(), amcuConfig.getMABaudRate());
            amcuConfig.setMa1parityStopAndDataBits(amcuConfig.getMaParity(),
                    amcuConfig.getMaStopBits(), amcuConfig.getMaDataBits());
        }
    }

    private void startFarmerPushService() {
        mContext.startService(new Intent(mContext, PostFarmerRecords.class));
    }

    private void startBackgroundTask() {
        // Trigger Bckground tasks to start the services periodically
        BackgroundTasksMgr bgTaskMgr = BackgroundTasksMgr.getInstance(mContext.getApplicationContext());
    }

    private void initializeFieldAndParameters() {
        sessionManager.setKeyIsMenuEnable(false);
        sessionManager.setComingFrom(null);
        if (amcuConfig.getAllowEquipmentBasedCollection()) {
            LoginActivity.clientName = "MILMA";
        } else {
            LoginActivity.clientName = "Generic";
        }

        SplashActivity.deviceRefreshActivityCount = 0;
        SplashActivity.isClearActivity = false;
        //for smartMooprinter
        amcuConfig.setIsSmartMoofontReduced(true);
        //For making defalut as collection
        amcuConfig.setSalesOrCollection("Collection");
    }

    public void deleteOldExcelFiles() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //To add sample records
                    // Util.addSampleRecords(LoginActivity.this,saveSession.getFarmerIdDigit());
                    //To delete more than month old created sheet
                    DeleteExcelService des = new DeleteExcelService();
                    des.deleteExcel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void resetSequenceNumber() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        dbh.isCollectionStarted();
    }

    //to start timer to update the flag as end shift
    public void registerEndShiftAlarm(long setTime) {

        ComponentName receiver = new ComponentName(mContext, EndShiftReceiver.class);
        PackageManager pm = mContext.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Calendar calendar = Calendar.getInstance();

        alarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(mContext, EndShiftReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, intent, 0);

        if (setTime == 0) {
            if (Util.getCurrentShift().equalsIgnoreCase(AppConstants.Shift.MORNING)) {
                calendar.setTimeInMillis(new SmartCCUtil(mContext).getTimeinLong(
                        amcuConfig.getKeyCollEndMrnShift()));
            } else {
                calendar.setTimeInMillis(new SmartCCUtil(mContext).getTimeinLong(
                        amcuConfig.getKeyCollEndEvnShift()));
            }
        } else if (setTime == -1) {

        } else {
            calendar.setTimeInMillis(setTime);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 30 * 2, pendingIntent);

    }

    public void startConfigurationTask() {
        OnStartTask onStartTask = new OnStartTask(mContext.getApplicationContext());
        onStartTask.doConfigurationTask();
        mContext.startService(new Intent(mContext.getApplicationContext(), ConfigurationPush.class));

    }

    public void processIncompleteRecords() {
       /* DatabaseManager dbm = new DatabaseManager(mContext);
        dbm.previousRecordChangeInCompleteToComplete();*/
    }


}
