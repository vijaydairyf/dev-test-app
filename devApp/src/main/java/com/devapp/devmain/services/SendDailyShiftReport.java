package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.devapp.devmain.entity.ExcelConstants;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.usb.WriteExcel;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import jxl.write.WriteException;

public class SendDailyShiftReport extends IntentService {

    ArrayList<ReportEntity> allDetailRep = new ArrayList<ReportEntity>();

    Runnable updateRunnable, NewUpdateRunnable;
    Handler myHandler = new Handler();
    File gpxfile;
    String shift;
    long lnDate;
    AmcuConfig amcuConfig;
    SessionManager session;
    SmartCCUtil smartCCUtil;

    public SendDailyShiftReport() {
        super("SMS");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        smartCCUtil = new SmartCCUtil(getApplicationContext());
        shift = Util.getCurrentShift();
        lnDate = Util.getDateInLongFormat(smartCCUtil.getReportFormatDate());
        allDetailRep = Util.getShiftReport(getApplicationContext(), null, 6,
                null, 0);
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(getApplicationContext());

        if (allDetailRep != null) {
            cteateExcelAndSendMail();
        } else {
            amcuConfig.setSendingMsg("No Data");
        }
    }

    public void cteateExcelAndSendMail() {
        final WriteExcel writeExcel = new WriteExcel();

        String root = Util.getSDCardPath();
        String dateTime = Util.getTodayDateAndTime(5, getApplicationContext(), true);

        final File fileSmartAmcu = new File(root, "smartAmcuReports");
        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }

        gpxfile = new File(fileSmartAmcu, new SessionManager(getApplicationContext())
                .getCollectionID().replace(" ", "") + "_"
                + dateTime
                + shift
                + "_Shift_Report.xls");

        writeExcel.setWriteCompleteListener(new WriteExcel.OnExcelWriteCompleteListener() {
            @Override
            public void onWriteComplete() {

            }
        });


        new Thread(new Runnable() {
            public void run() {

                writeExcel.setOutputFile(gpxfile.toString());
                try {
                    writeExcel.write(getApplicationContext(), ExcelConstants.TYPE_REPORT, allDetailRep);
                    new SessionManager(getApplicationContext())
                            .setFileName(gpxfile.toString());
                } catch (WriteException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("Please check the result file " + gpxfile);

                myHandler.post(updateRunnable);

            }
        }).start();
        updateRunnable = new Runnable() {

            @Override
            public void run() {

                Intent intentService = new Intent(getApplicationContext(),
                        SendEmail.class);
                startService(intentService);

                if (amcuConfig.getSendEmailToConfigureIDs() ||
                        session.getReportType() == Util.PERIODICREPORT || session.getReportType() == Util.PERIODICREPORTINDIVIDUAL) {
                    Toast.makeText(getApplicationContext(), "Sending email...",
                            Toast.LENGTH_SHORT).show();
                }


            }
        };
    }

}
