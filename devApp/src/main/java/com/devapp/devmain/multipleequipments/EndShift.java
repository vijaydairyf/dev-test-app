package com.devapp.devmain.multipleequipments;

import android.content.Context;
import android.content.Intent;

import com.devapp.devmain.cloud.APKManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.LogInService;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.DeleteOldRecordService;
import com.devapp.devmain.services.PurgeData;
import com.devapp.devmain.services.SendDailyShiftReport;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;

/**
 * Created by Upendra on 6/8/2016.
 */
public class EndShift {

    Context mContext;
    AmcuConfig amcuConfig;
    SessionManager session;

    String Lshift;
    long lnDate;

    DatabaseHandler dbHandler;
    FormatPrintRecords formatPrintRecords;

    public EndShift(Context context) {
        this.mContext = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mContext);

        Lshift = Util.getCurrentShift();
        lnDate = Util.getDateInLongFormat(Util.getTodayDateAndTime(1, mContext, true));

        dbHandler = DatabaseHandler.getDatabaseInstance();
        formatPrintRecords = new FormatPrintRecords(mContext);
    }

    public void doEndShift() {

        if (!amcuConfig.getEnableCenterCollection() && !amcuConfig.getEnableSales()) {
            amcuConfig.setCollectionEndShift(true);
        }

        session.setAllSessionFarmer(null);
        getEndShiftDetails();
        if (Util.isNetworkAvailable(mContext)) {
            try {

                amcuConfig.setSendingMsg("sending");
                session.setReportType(Util.sendEndShiftReport);
                session.setSendReport(true);
                Intent intent = new Intent(mContext,
                        SendDailyShiftReport.class);
                mContext.startService(intent);


            } catch (Exception e) {
                e.printStackTrace();
            }

            updateAPK();

            Intent dataPurgeIntent = new Intent(mContext,
                    PurgeData.class);
            mContext.startService(dataPurgeIntent);
            Intent deleteData = new Intent(mContext,
                    DeleteOldRecordService.class);
            mContext.startService(deleteData);
        } else {
            Util.displayErrorToast("Please check the network connectivity!", mContext);
        }


    }

  /*  public void printEndShiftReport() {
        FormatPrintRecords formatPrintRecords = new FormatPrintRecords(
                mContext);

        String strBuild = formatPrintRecords.onPrintShiftReport(Util.sendEndShiftReport, Lshift, lnDate);
        if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {
            //Functionality for TVS printer
        } else {
            TherMalPrinter(strBuild);
        }
    }

    public void TherMalPrinter(String strPrint) {
        ArrayList<DeviceEntry> allDeviceData = DeviceListActivity.allDeviceEntry;
        PrinterManager printerManager = new PrinterManager(mContext);
        UsbManager mUsbManager = (UsbManager) mContext.getSystemService(Context.USB_SERVICE);

        String sendingShift = Util.getShift(mContext);
        String sendingDate = Util.getTodayDateAndTime(1, mContext, true);
        printerManager.startThermalPrinter(allDeviceData, mUsbManager);
        printerManager.printDataThermal(strPrint, 1, sendingShift,
                sendingDate);
    }*/

    public void getEndShiftDetails() {


        try {
            dbHandler.getDailyShiftReport(lnDate, Lshift, "All farmers",
                    Util.getallSampleDataList(mContext));
            dbHandler.getChillingCenterReport("All farmers", Lshift, lnDate, 0, 0, false, false);
        } catch (Exception e1) {
            e1.printStackTrace();
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

                    Util.displayErrorToast("Please wait...", mContext);
                }
                amcuConfig.setLogInFor(Util.LOGINFORAPK);
                mContext.startService(new Intent(mContext,
                        LogInService.class));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String getCenterAveragedetails() {
        int totalLength = 22;
        String stringAverage;
        AverageReportDetail averageChillingCenterReport = null;
        try {
            averageChillingCenterReport = dbHandler.getChillingCenterReport(null, Lshift, lnDate, 0, 0, false, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        stringAverage = getSpace(totalLength, "Total Centers")
                + getTail(10, String.valueOf(averageChillingCenterReport.totalMember)) + "\n"
                + getSpace(totalLength, "Total Accepted")
                + getTail(7, String.valueOf(averageChillingCenterReport.totalAcceptedEntries)) + "\n"
                + getSpace(totalLength, "Total Rejected")
                + getTail(8, String.valueOf(averageChillingCenterReport.totalRejectedEntries)) + "\n"
                + getSpace(totalLength, "Total Qty")
                + getTail(18, String.valueOf(averageChillingCenterReport.totalQuantity) + " " + Util.getTheUnit(mContext, Util.UNIT_FOR_QUANTITY)) + "\n"
                + getSpace(totalLength, "Total Amount")
                + getTail(10, String.valueOf(averageChillingCenterReport.totalAmount) + " Rs") + "\n" + "\n";

        stringAverage = stringAverage + formatPrintRecords.createSeparatorThermal('-');
        return stringAverage;
    }


    public String getSpace(int totalLength, String str) {

        String retString = new String(new char[4]).replace('\0', ' ') +
                str;

        return retString;
    }

    public String getTail(int length, String str) {

        String retString = new String(new char[length]).replace('\0', ' ') + str;

        return retString;
    }


}
