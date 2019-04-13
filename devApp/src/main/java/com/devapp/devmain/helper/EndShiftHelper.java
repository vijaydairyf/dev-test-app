package com.devapp.devmain.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.cloud.APKManager;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.LogInService;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.DeleteOldRecordService;
import com.devapp.devmain.services.PurgeData;
import com.devapp.devmain.services.SendDailyShiftReport;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

/**
 * Created by u_pendra on 19/1/17.
 */

public class EndShiftHelper {

    public long alertTimeCheck;
    AlertDialog alertDialog;
    Button btnResend;
    TextView tvMailHeader;
    RelativeLayout ProgressL;

    Runnable runnableAlert;
    boolean sentStatus;
    Handler mailHandler = new Handler();
    AmcuConfig amcuConfig;
    SessionManager session;
    SmartCCUtil smartCCUtil;
    String currentShift, currentDate;
    DatabaseHandler databaseHandler;
    private Context mContext;

    public EndShiftHelper(Context context) {
        this.mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mContext);
        smartCCUtil = new SmartCCUtil(mContext);
        currentShift = Util.getCurrentShift();
        currentDate = smartCCUtil.getReportFormatDate();
        databaseHandler = DatabaseHandler.getDatabaseInstance();
    }


    public void doEndShift() {

        if (!amcuConfig.getEnableCenterCollection()
                && !amcuConfig.getEnableSales()) {
            amcuConfig.setCollectionEndShift(true);
        }

        getEndShiftDetails();
        if (Util.isNetworkAvailable(mContext)) {
            try {

                amcuConfig.setSendingMsg("sending");
                session.setReportType(Util.sendEndShiftReport);
                session.setSendReport(true);
                Intent intent = new Intent(mContext.getApplicationContext(),
                        SendDailyShiftReport.class);
                mContext.startService(intent);
                if (amcuConfig.getSendEmailToConfigureIDs()) {
                    sendingMailAlert();
                } else {
                    new SmartCCUtil(mContext).alertForProgressBar();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            updateAPK();
            // Purge DB records if required. Current purging policy is hardcoded to purge records
            // older than 60 sessions from the current one.
            //Commented as per JK road issue data was deleting unpredictibly
            Intent dataPurgeIntent = new Intent(mContext,
                    PurgeData.class);
            mContext.startService(dataPurgeIntent);
            Intent deleteData = new Intent(mContext,
                    DeleteOldRecordService.class);
            mContext.startService(deleteData);
        } else {
            Toast.makeText(mContext, "Please check the network connectivity!", Toast.LENGTH_LONG).show();
        }
        //Starting receiver if any case end shift failed
        long delayTime = System.currentTimeMillis() + 20 * 1000;
        AfterLogInTask alt = new AfterLogInTask(mContext);
        alt.registerEndShiftAlarm(delayTime);

    }


    public void sendingMailAlert() {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                mContext);

        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_sendingmail, null);
        tvMailHeader = (TextView) view.findViewById(R.id.tvheader);
        ProgressL = (RelativeLayout) view.findViewById(R.id.progress_layout);
        btnResend = (Button) view.findViewById(R.id.btnResend);
        final Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnResend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (btnResend.getText().toString().equalsIgnoreCase("OK")) {
                    alertDialog.dismiss();
                } else {
                    alertDialog.dismiss();
                    doEndShift();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        ProgressL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        final long timeMil = System.currentTimeMillis();

        long ln = timeMil;
        ln = timeMil + 180002L;
        final long timeMilTot = ln;
        // create alert dialog
        alertDialog = alertBuilder.create();
        alertDialog.setView(view);
        // show it
        alertDialog.show();

        // To display the alert dialog in center

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 450;
        lp.height = 450;
        // lp.x = -170;
        // lp.y = 100;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);
        checkForSent(timeMil, timeMilTot);

    }

    public void checkForSent(final long timeMil, final long timeMilTot) {
        alertTimeCheck = timeMil;

        new Thread(new Runnable() {

            @Override
            public void run() {

                sentStatus = checkForTest(timeMil, timeMilTot);
                if (sentStatus) {
                    mailHandler.post(runnableAlert);
                } else {
                    if (timeMil < timeMilTot) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        alertTimeCheck = timeMil + 1000L;
                        checkForSent(alertTimeCheck, timeMilTot);
                    }
                }
            }
        }).start();
        runnableAlert = new Runnable() {
            @Override
            public void run() {
                checkMsgSent();
            }
        };
    }

    public boolean checkForTest(long t1, long t2) {
        boolean sent = false;
        String Sendmsg = AmcuConfig.getInstance()
                .getSendingMsg();
        if (Sendmsg.equalsIgnoreCase("Sent")) {
            sent = true;
        } else if (Sendmsg.equalsIgnoreCase("Failed")) {
            sent = true;
        } else if (Sendmsg.equalsIgnoreCase("No Data")) {
            sent = true;
        } else {
            sent = t1 >= t2;
        }
        return sent;
    }

    public void checkMsgSent() {
        String Sendmsg = AmcuConfig.getInstance()
                .getSendingMsg();
        if (Sendmsg.equalsIgnoreCase("Sent")) {
            tvMailHeader.setText("Email sent successfully.");
            btnResend.setText("OK");
            ProgressL.setVisibility(View.GONE);
            return;
        } else if (Sendmsg.equalsIgnoreCase("Failed")) {
            tvMailHeader.setText("Sendig failed! Try again");
            ProgressL.setVisibility(View.GONE);
            return;
        } else if (Sendmsg.equalsIgnoreCase("No Data")) {
            tvMailHeader.setText("No data to send");
            ProgressL.setVisibility(View.GONE);
            btnResend.setText("OK");
            return;
        } else {
            tvMailHeader.setText("Email was not sent");
            ProgressL.setVisibility(View.GONE);
            return;
        }
    }

    public void getEndShiftDetails() {

        long lnDate = Util.getDateInLongFormat(currentDate);
        try {
            databaseHandler.getDailyShiftReport(lnDate, currentShift, "All farmers",
                    Util.getallSampleDataList(mContext));
            databaseHandler.getChillingCenterReport("All farmers", currentShift, lnDate, 0, 0, false, false);
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            //Removed database close;
        }

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public synchronized void updateAPK() {

        try {
            boolean isDuplicateAPK = Util.checkForDuplicateDownload(mContext);
            if (!isDuplicateAPK && !APKManager.apkDownloadInprogress) {
                APKManager.apkDownloadInprogress = true;
                if (session.getUserRole().equalsIgnoreCase("Manager")) {
                    Toast.makeText(mContext, "Please wait...",
                            Toast.LENGTH_SHORT).show();
                }
                amcuConfig.setLogInFor(Util.LOGINFORAPK);
                mContext.startService(new Intent(mContext,
                        LogInService.class));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void printShiftReport(String currentDate, String currentShift) {
        FormatPrintRecords formatPrintRecords = new FormatPrintRecords(
                mContext);

        long lnDate = Util.getDateInLongFormat(currentDate);

        String strBuild = formatPrintRecords.onPrintShiftReport(Util.sendEndShiftReport, currentShift, lnDate);

        sendToPrinter(strBuild, currentShift, currentDate, PrinterManager.printShiftReport);
        //adding delay to avoid packet loss
       /* try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        //if enableDispatchReport
        /*if (amcuConfig.getDispatchValue()) {
            String dispatchReport = "\n" + formatPrintRecords.onPrintShiftDispatchReport(lnDate);
            sendToPrinter(dispatchReport, currentShift, currentDate, PrinterManager.shiftDispatchReport);
        }*/
    }

    private void sendToPrinter(String data, String currentShift, String currentDate, int printType) {
        if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {
            printOnTvsprinter(data, currentShift, currentDate, printType);
        } else {
            printOnThermalPrinter(data, currentShift, currentDate, printType);
        }

    }


    public void printOnTvsprinter(String dataToPrint, String shift, String date, int printType) {
        PrinterManager printerManager = new PrinterManager(mContext);
        printerManager.startTVS(dataToPrint, printType, date, null, shift);
    }

    public void printOnThermalPrinter(String dataToPrint, String shift, String date, int printType) {
        String sendingShift = shift;
        String sendingDate = date;
        PrinterManager printerManager = new PrinterManager(mContext);

        printerManager.print(dataToPrint, printType, sendingDate, null
                , sendingShift);
    }


}
