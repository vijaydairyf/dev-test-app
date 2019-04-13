package com.devapp.devmain.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import com.devapp.devmain.encryption.Csv;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.httptasks.PostCollectionRecordsService;
import com.devapp.devmain.main.SplashActivity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.Mail;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.usb.WriteExcel;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;

public class SendEmail extends IntentService {

    private static final String TAG = "SEND_EMAIL_SVC_INTENT";
    public static boolean isIllegalEmail;
    public static boolean isSendingMailQuotaOver;
    public String startShift = "";
    public String endShift = "";
    boolean msgSent = false;
    Runnable updateRunnable;
    Handler myHandler = new Handler();
    String file;
    SessionManager session;
    int month;
    AverageReportDetail avgReportdetail;
    ArrayList<ReportEntity> allReportEntity = null;
    String shift;
    String[] allMonths = {"January", "February", "March", "April", "May",
            "June", "July", "August", "September", "October", "November",
            "December"};
    AmcuConfig amcuConfig;
    SmartCCUtil smartCCUtil;

    public SendEmail() {
        super("SendEmail");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        smartCCUtil = new SmartCCUtil(getApplicationContext());
        month = Util.getMonthORYear(0);
        shift = Util.getCurrentShift();
        isIllegalEmail = false;
        isSendingMailQuotaOver = false;


        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (month == 1) {
            month = 11;
        } else {
            month = month - 2;
        }
        session = new SessionManager(getApplicationContext());
        amcuConfig = AmcuConfig.getInstance();

        getAllSessionFarmer();
        avgReportdetail = Util.getAverageData();
        sendShiftDataToCloud();
        file = session.getFileName();
        if (file != null
                && !file.equalsIgnoreCase("")) {
            if (amcuConfig.getSendEmailToConfigureIDs()) {
                msgsent();
            } else {
                Toast.makeText(getApplicationContext(), "Enable send email configuration for device!", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getApplicationContext(), "No file to send!", Toast.LENGTH_SHORT).show();
        }

    }

    public void msgsent() {
        new Thread(new Runnable() {

            @Override
            public void run() {

                Csv csv = new Csv(getApplicationContext(), 0);
                String password = csv.getDcryptedKEY(amcuConfig.getClientConfiguredPassword());
                String email = amcuConfig.getClientConfiguredEmail();
                if (email != null && password != null) {
                    //Setting need to do for account
                    //https://www.google.com/settings/security/lesssecureapps
                    Mail m = new Mail(email, password, getApplicationContext());
                    setMsgAndsendMail(file, m);
                }
                myHandler.post(updateRunnable);

            }
        }).start();

        updateRunnable = new Runnable() {

            @Override
            public void run() {

                if (isSendingMailQuotaOver) {
                    if (displayToast()) {
                        Util.displayErrorToast("Network busy,Please try after some time!", getApplicationContext());
                    }
                    amcuConfig.setSendingMsg("Failed");
                    SplashActivity.sendCount = 0;
                } else if (isIllegalEmail) {
                    if (displayToast()) {
                        Util.displayErrorToast("Invalid email Id for manager or Society contact person!", getApplicationContext());
                    }
                    amcuConfig.setSendingMsg("Failed");
                    SplashActivity.sendCount = 0;
                } else if (msgSent) {
                    amcuConfig.setSendingMsg("Sent");
                    if (displayToast()) {
                        Util.displayErrorToast("Email sent successfully", getApplicationContext());
                    }
                    session.setFileName("");
                    amcuConfig.setSendingMsg("Sent");
                    SplashActivity.sendCount = 0;
                    session.setMilkReject("NO");

                } else {
                    if (SplashActivity.sendCount < 2) {
                        try {
                            Thread.sleep(10000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        SplashActivity.sendCount = SplashActivity.sendCount + 1;
                        Intent intentService = new Intent(
                                getApplicationContext(), SendEmail.class);
                        startService(intentService);

                        if (displayToast()) {
                            Util.displayErrorToast("Re-attempting to send email, please wait...", getApplicationContext());
                        }
                    } else {
                        amcuConfig.setSendingMsg("Failed");
                        SplashActivity.sendCount = 0;
                        if (displayToast()) {
                            Util.displayErrorToast("Email was not sent! please check the connectivity", getApplicationContext());
                        }

                    }
                }
            }

        };
    }

    public void setMsgAndsendMail(String gpxfile, Mail m) {


        // m.getPasswordAuthentication();
        m.setFrom(amcuConfig.getClientConfiguredEmail());

        String[] toArr = null;
        String emailStr = "";

        if (amcuConfig.getSendEmailToConfigureIDs() || session.getReportType() == Util.PERIODICREPORT
                || session.getReportType() == Util.PERIODICREPORTINDIVIDUAL || amcuConfig.getAllowMailExportedFarmer()) {
            if (session.getManagerEmailID() != null && session.getManagerEmailID().length() > 2) {
                emailStr += session.getManagerEmailID();
            }
            if (session.getSocManagerEmail() != null && session.getSocManagerEmail().length() > 2) {
                if (emailStr.length() > 2) {
                    emailStr += ("," + session.getSocManagerEmail());
                } else {
                    emailStr += session.getSocManagerEmail();
                }

            }
        }

        try {
            String[] strArr = emailStr.split(",");
            toArr = new String[strArr.length];
            for (int i = 0; i < strArr.length; i++) {
                toArr[i] = strArr[i].replace(" ", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        m.setTo(toArr);


        m.setBody("Please find the attached file"
                + "\n"
                + "\n"
                + getApplicationContext().getResources().getString(
                R.string.stellappslink));
        if (session.getReportType() == Util.sendDailyReport) {
            // Selected date and shift report

            m.setSubject(session.getCollectionID() + " " + session.getSocietyName() + " "
                    + Util.getDailyDateOrShift(0) + " " + "-" + " "
                    + shift
                    + " " + "shift report");

            m.setBody("<html>"
                    + "Dear "
                    + "Sir/Madam"
                    + ","
                    + "<br>"
                    + "<br>"
                    + "<p>Following is the summary of the shift:</p>"
                    + "<p>"
                    + "Start time: "
                    + startShift
                    + "<br>"
                    + "End time: "
                    + endShift
                    + "</p>"
                    + createMailSummaryContent()
                    + "<br>"
                    + createContentForCenter(Util.avgChillingDetails)
                    + "<br>"
                    + " Please refer to the attached document for details."
                    + "<br>"
                    + "<br>"
                    + getApplicationContext().getResources().getString(
                    R.string.stellappslink) + "<br>" + "<br>"
                    + "Regards," + "<br>" + "smartAMCU" + "</html>");

        } else if (session.getReportType() == Util.sendMemberBillRegister) {
            m.setSubject("Member bill register");
        } else if (session.getReportType() == Util.sendMemberBillSummary) {
            m.setSubject("Member bill summary");
        } else if (session.getReportType() == Util.sendWeeklyReport) {
            m.setSubject("Weekly report from " + Util.getOperationDate(-7, 0)
                    + " to " + Util.getOperationDate(0, 0));

            m.setBody("Dear "
                    + session.getManagerName()
                    + ","
                    + "\n"
                    + "\n"
                    + "Please find the Weekly Consolidated Report for "
                    + session.getSocietyName()
                    + " attached with this mail."
                    + "\n"
                    + "\n"
                    + getApplicationContext().getResources().getString(
                    R.string.stellappslink) + "\n" + "\n" + "Regards,"
                    + "\n" + "smartAMCU");

        } else if (session.getReportType() == Util.sendMonthlyReport) {
            m.setSubject(session.getSocietyName()
                    + " Society Monthly report of " + allMonths[month]);

            m.setBody("Dear "
                    + session.getManagerName()
                    + ","
                    + "\n"
                    + "\n"
                    + "Please find the Monthly Consolidated Report for "
                    + session.getSocietyName()
                    + " attached with this mail."
                    + "\n"
                    + "\n"
                    + getApplicationContext().getResources().getString(
                    R.string.stellappslink) + "\n" + "\n" + "Regards,"
                    + "\n" + "smartAMCU");

        } else if (session.getReportType() == Util.sendEndShiftReport) {

            m.setSubject(session.getCollectionID() + " " + session.getSocietyName() + " "
                    + Util.getOperationDate(0, 0) + " " + "-" + " "
                    + (Util.getCurrentShift() == "M" ? "Morning" : "Evening") + " "
                    + "shift report");

            m.setBody("<html>"
                    + "Dear "
                    + "Sir/Madam"
                    + ","
                    + "<br>"
                    + "<br>"
                    + "<p>Following is the summary of the shift:</p>"
                    + "<p>"
                    + "Start time: "
                    + startShift
                    + "<br>"
                    + "End time: "
                    + endShift
                    + "</p>"
                    + createMailSummaryContent()
                    + "<br>"
                    + createContentForCenter(Util.avgChillingDetails)
                    + "<br>"
                    + " Please refer to the attached document for details."
                    + "<br>"
                    + "<br>"
                    + getApplicationContext().getResources().getString(
                    R.string.stellappslink) + "<br>" + "<br>"
                    + "Regards," + "<br>" + "smartAMCU" + "</html>");

        } else if (session.getReportType() == Util.sendFarmerList) {
//            toArr = new String[1];
//            toArr[0] = "smartamcu.care@stellapps.com";
            m.setTo(toArr);
            m.setSubject(session.getCollectionID() + " " + session.getSocietyName() + " Farmer list");

        } else if (session.getReportType() == Util.UPDATE_FARMERLIST) {
            toArr = new String[1];
            toArr[0] = "smartamcu.care@stellapps.com";
            m.setTo(toArr);
            m.setSubject(session.getCollectionID() + " " + session.getSocietyName() + " Farmer_Id configuration changed!");

        } else if (session.getReportType() == Util.PERIODICREPORT || session.getReportType() == Util.PERIODICREPORTINDIVIDUAL) {
            m.setSubject(Util.PERIODIC_SUBJECT);
            m.setBody("<html>"
                    + "Dear "
                    + "Sir/Madam"
                    + ","
                    + "<br>"
                    + " Please refer to the attached document for details."
                    + "<br>"
                    + "<br>"
                    + getApplicationContext().getResources().getString(
                    R.string.stellappslink) + "<br>" + "<br>"
                    + "Regards," + "<br>" + "smartAMCU" + "</html>");
        } else if (session.getReportType() == Util.SALESREPORT) {
            m.setSubject(Util.SALES_SUBJECT);
            m.setBody("<html>"
                    + "Dear "
                    + "Sir/Madam"
                    + ","
                    + "<br>"
                    + " Please refer to the attached document for details."
                    + "<br>"
                    + "<br>"
                    + getApplicationContext().getResources().getString(
                    R.string.stellappslink) + "<br>" + "<br>"
                    + "Regards," + "<br>" + "smartAMCU" + "</html>");
        } else {
            m.setSubject("Daily reports");
        }

        try {

            m.addAttachment(gpxfile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (m.send()) {
                msgSent = true;
            } else {
                msgSent = false;
            }
        } catch (Exception e) {
            msgSent = false;
            e.printStackTrace();
        }

    }


    public void getAllSessionFarmer() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        if (session.getReportType() == Util.sendDailyReport) {

            long lnDate = Util.getDateInLongFormat(Util.getDailyDateOrShift(0));

            try {
                allReportEntity = dbh.getDailyShiftReportEntity(lnDate, shift, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (allReportEntity != null && allReportEntity.size() > 1) {
                startShift = allReportEntity.get(1).time;
                endShift = allReportEntity.get(allReportEntity.size() - 1).time;
            }

        } else if (session.getReportType() == Util.sendEndShiftReport) {

            long lnDate = Util.getDateInLongFormat(smartCCUtil.getReportFormatDate());
            shift = Util.getCurrentShift();

            try {
                allReportEntity = dbh.getDailyShiftReportEntity(lnDate, shift, null);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (allReportEntity != null && allReportEntity.size() > 1) {
                startShift = allReportEntity.get(1).time;
                endShift = allReportEntity.get(allReportEntity.size() - 1).time;
            }

        }

    }

    private String rightPad(String text, int length) {
        return text = String.format("%1$-" + length + 's', text);
    }

    private String createMailSummaryContent() {

        String content = "";
        if (avgReportdetail != null)

        {
            content = "<table style='border-collapse: collapse;'>" + "<tr>"
                    + "<td>" + "Number of Farmers&nbsp;&nbsp;&nbsp;" + "</td>"
                    + "<td>"
                    + avgReportdetail.totalMember
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td>"
                    + "Total Qty"
                    + "</td>"
                    + "<td>"
                    + avgReportdetail.totalQuantity
                    + "</td>"
                    + "</tr>"
                    + "<tr>"
                    + "<td>"
                    + "Total Amount"
                    + "</td>"
                    + "<td>"
                    + avgReportdetail.totalAmount + "</td>" + "</tr>"

                    + "</table>";

        }
        return content;
    }

    public void dataForPost() {

        if (allReportEntity != null && allReportEntity.size() > 0) {

            amcuConfig.setPostData(1);
            amcuConfig.setLogInFor(Util.LOGINFORCLOUD);

            Intent i = new Intent(getApplicationContext(), PostCollectionRecordsService.class);
            i.putExtra(PostCollectionRecordsService.END_SHIFT, true);
            startService(i);

        }
    }


    public void sendShiftDataToCloud() {
        if (session.getReportType() == Util.sendEndShiftReport) {
            dataForPost();
            session.setDailyEmailSent(true);
            amcuConfig.setCollectionEndShift(true);
        }
    }

    public boolean displayToast() {
        if ((amcuConfig.getSendEmailToConfigureIDs() &&
                (session.getReportType() != Util.sendFarmerList) &&
                (session.getReportType() != Util.UPDATE_FARMERLIST)) ||
                (session.getReportType() == Util.PERIODICREPORT) ||
                (session.getReportType() == Util.PERIODICREPORTINDIVIDUAL)
                ) {
            return true;
        } else {
            return false;
        }
    }

    public String getTheUnit() {

        if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {

            return "Average Ltrs";

        } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            return "Average Kgs";
        } else if (amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            return "Average Kgs";
        } else {
            return "Average Ltrs";
        }

    }

    public String createContentForCenter(AverageReportDetail avgReportdetail) {
        String content = "";
        if (amcuConfig.getEnableCenterCollection()) {
            if (WriteExcel.checkNumberOfRecord(Util.avgChillingDetails) > 0)

            {
                content = "<html>"
                        + "<br>"
                        + "<br>"
                        + "Center collection summary "
                        + "<br>"
                        + "<br>"
                        + "</html>";
                content = content +
                        "<table style='border-collapse: collapse;'>" + "<tr>"
                        + "<td>" + "Number of Centers&nbsp;&nbsp;&nbsp;" + "</td>"
                        + "<td>"
                        + avgReportdetail.totalMember
                        + "</td>"
                        + "</tr>"

//                        + "<tr>"
//                        + "<td>"
//                        + "Total Accepted"
//                        + "</td>"
//                        + "<td>"
//                        + avgReportdetail.totalAcceptedEntries
//                        + "</td>"
//                        + "</tr>"

//                        + "<tr>"
//                        + "<td>"
//                        + "Total Rejected"
//                        + "</td>"
//                        + "<td>"
//                        + avgReportdetail.totalRejectedEntries
//                        + "</td>"
//                        + "</tr>"
//
//                        + "<tr>"
//                        + "<td>"
//                        + "Total Test"
//                        + "</td>"
//                        + "<td>"
//                        + avgReportdetail.totalTestEntries
//                        + "</td>"
//                        + "</tr>"
//
//                        + "<tr>"
//                        + "<td>"
//                        + "Average Amount"
//                        + "</td>"
//                        + "<td>"
//                        + avgReportdetail.avgAmount
//                        + "</td>"
//                        + "</tr>"

//                        + "<tr>"
//                        + "<td>"
//                        + getTheUnit()
//                        + "</td>"
//                        + "<td>"
//                        + avgReportdetail.avgQuantity
//                        + "</td>"
//                        + "</tr>"
//
//                        + "<tr>"
//                        + "<td>"
//                        + "Average Rate"
//                        + "</td>"
//                        + "<td>"
//                        + avgReportdetail.avgRate
//                        + "</td>"
//                        + "</tr>"
//
//                        + "<tr>"
//                        + "<td>"
//                        + "Average Fat"
//                        + "</td>"
//                        + "<td>"
//                        + avgReportdetail.avgFat
//                        + "</td>"
//                        + "</tr>"
//
//                        + "<tr>"
//                        + "<td>"
//                        + "Average SNF"
//                        + "</td>"
//                        + "<td>"
//                        + avgReportdetail.avgSnf
//                        + "</td>"
//                        + "</tr>"

                        + "<tr>"
                        + "<td>"
                        + "Total Qty"
                        + "</td>"
                        + "<td>"
                        + avgReportdetail.totalQuantity
                        + "</td>"
                        + "</tr>"

                        + "<tr>"
                        + "<td>"
                        + "Total Amount"
                        + "</td>"
                        + "<td>"
                        + avgReportdetail.totalAmount + "</td>" + "</tr>"

                        + "</table>";

            }
        }
        return content;
    }


}
