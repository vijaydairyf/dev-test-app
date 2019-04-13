package com.devapp.devmain.devicemanager;

import android.content.Context;
import android.util.Log;

import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.peripherals.factories.DeviceFactory;
import com.devapp.devmain.peripherals.interfaces.Device;
import com.devapp.devmain.peripherals.interfaces.WriteDataListener;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.AppConstants;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

public class PrinterManager implements WriteDataListener {

    public static int printReciept = 0;
    public static int printShiftReport = 1;
    public static int printDailyShiftReport = 3;
    public static int printReport = 2;
    public static int printperiodicReort = 4;
    public static int printsalesReport = 5;
    public static int printtotalPR = 6;
    public static int printChillingReport = 7;
    public static int printIndividualReport = 8;


    public static int printCustomReport = 9;
    public static int printDispatchReport = 10;
    public static int shiftDispatchReport = 11;
    public static int dispatchReceipt = 12;
    public static int salesReceipt = 13;
    public static int FARMER_EXPORT = 14;

    public static String title = "";
    Context mcontext;
    AmcuConfig amcuConfig;
    SessionManager session;
    DriverConfiguration driverConfiguration;
    Device mDevice;

    public PrinterManager(Context ctx) {
        this.mcontext = ctx;

        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mcontext);
        mDevice = DeviceFactory.getDevice(mcontext, DeviceName.PRINTER);
        driverConfiguration = new DriverConfiguration();
        if (mDevice != null)
            mDevice.registerListener(this);
//        openConnection();

    }

    private void sendToPrinter(String msg) {
        if (mDevice != null) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mDevice.writeAsync(msg.getBytes(Charset.forName("UTF-8")));
        }
    }


    public void openConnection() {
        if (mDevice != null)
            mDevice.read();
    }

    public void closePrinter() {
        if (mDevice != null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mDevice.closeConnection();
        }

    }

    public void print(String printData, int chkShift, String startDate, String endDate, String shift) {
        openConnection();
        if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            startTVS(printData, chkShift, startDate, endDate, shift);
        } else {
            printDataThermal(printData,
                    chkShift, startDate, endDate, shift);
        }
    }

    public void write(String data) {

        try {
            mDevice.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /*public void openPrinterConnection(){
        if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")
                || amcuConfig.getPrinter().equalsIgnoreCase("TVS 80-MM")) {
            startTVS(printData, chkShift, startDate, endDate, shift);
        } else {
            printDataThermal(printData,
                    chkShift, startDate, endDate, shift);
        }
    }*/

    public void startTVS(String printData, int chkShift, String startDate, String endDate, String shift) {

        openConnection();
        printOnTVS(printData, chkShift, startDate, endDate, shift);

    }

    public void printOnTVS(String printData,
                           int type, String startDate, String endDate, String shift) {
        try {
            String printerData = "";

            String[] header = constructHeaderForTVS(
                    type, startDate, endDate, shift).split("\n");

            if (type == printReciept || type == dispatchReceipt || type == salesReceipt) {

                for (int i = 0; i < header.length; i++) {
                    if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                            || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
                        printerData = printerData + header[i] + "\n";
                    } else {
                        printerData = printerData + header[i];
                    }
                }
                printerData = printerData + "\n" + printData;
            } else if (type == FARMER_EXPORT) {
                String[] strArr = printData.split("\n");
                for (int i = 0; i < strArr.length; i++) {
                    printerData = printerData + strArr[i] + "\n";
                }
            } else {
                for (int i = 0; i < header.length; i++) {
                    if (header[i] != null && header[i].trim().length() > 0) {
                        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
                            printerData = printerData + header[i] + "\n";
                        } else {
                            printerData = printerData + header[i];
                        }
                    }
                }
                printerData = printerData + "\n";
                String[] strArr = printData.split("\n");


                for (int i = 0; i < strArr.length; i++) {
                    printerData = printerData + strArr[i] + "\n";
                }


            }

            sendToPrinter(printerData);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void printDataThermal(String printData, int type, String startDate,
                                 String endDate, String shift) {

        ArrayList<String> headers = new ArrayList<>();

        if (type != printCustomReport) {
            headers = new ArrayList<>(Arrays.asList(constructHeader(type, startDate, endDate, shift).split("\n")));
        }

        String[] strArr = printData.split("\n");
        String printerData = "";

        if (type == printReciept || type == dispatchReceipt || type == salesReceipt) {
            if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
                reduceFontSize(false);
            }
        } else {
            if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
                reduceFontSize(true);
            }
        }


        if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
//
            for (int i = 0; i < headers.size(); i++) {
                if (type == printReciept || type == dispatchReceipt || type == salesReceipt) {
                    printerData = printerData + centerAlignWithPaddingThermalSmartMoo(headers.get(i)) + "\n";

                } else {
                    printerData = printerData + centerAlignWithPaddingThermalSmartMooReduced(headers.get(i)) + "\n";

                }
            }
            printerData = printerData + "\n";
//            writeDataOnThermalPrinter("\n");
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {

            for (int i = 0; i < headers.size(); i++) {

                printerData = printerData + centerAlignWithPaddingThermalSmartMoo(headers.get(i));
//                writeDataOnThermalPrinter(centerAlignWithPaddingThermalSmartMoo(headers[i]));
            }

            printerData = printerData + "\n";
//            writeDataOnThermalPrinter("\n");
        } else {

            for (int i = 0; i < headers.size(); i++) {
                printerData = printerData + centerAlignWithPaddingThermal(headers.get(i));
//                writeDataOnThermalPrinter(centerAlignWithPaddingThermal(headers[i]));
            }
            printerData = printerData + "\n";
//            writeDataOnThermalPrinter("\n");
        }

        for (int i = 0; i < strArr.length; i++) {
            if (i == strArr.length - 1) {
                printerData = printerData + strArr[i];
//                writeDataOnThermalPrinter(strArr[i]);
                if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO") ||
                        amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
                    printerData = printerData + "\n\n\n\n";
//                    writeDataOnThermalPrinter("\n\n");
//                    writeDataOnThermalPrinter("\n\n");
                } else {
                    printerData = printerData + "\n\n\n\n\n\n";
//                    writeDataOnThermalPrinter("\n\n");
//                    writeDataOnThermalPrinter("\n\n");
//                    writeDataOnThermalPrinter("\n\n");
                }
            } else {
                printerData = printerData + strArr[i] + "\n";
//                writeDataOnThermalPrinter(strArr[i] + "\n");

            }
        }

        sendToPrinter(printerData);

        /*if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            reduceFontSize(false);
        }*/
    }


    public void writeDataOnThermalPrinterHeader(byte[] printData) {
        if (mDevice != null)
            mDevice.write(printData);
    }


    public String centerAlignWithPaddingThermal(String text) {
        int spaceCount = 0;

        if (text != null && text.length() < 38) {
            spaceCount = (42 - text.length()) / 2;
        }

        text = String.format("%1$-" + (text.length() + spaceCount) + 's', text);
        text = String.format("%1$" + 42 + 's', text);

        return text;
    }


    private String centerAlignWithPaddingThermalSmartMooReduced(String text) {
        int spaceCount = 0;

        if (text != null && text.length() < 40) {
            spaceCount = (42 - text.length()) / 2;
        }

        text = String.format("%1$-" + (text.length() + spaceCount) + 's', text);
        text = String.format("%1$" + 42 + 's', text);

        return text;
    }

    public String centerAlignWithPaddingThermalSmartMoo(String text) {
        int spaceCount = 0;

        if (text != null && text.length() < 30) {
            spaceCount = (32 - text.length()) / 2;
        }

        text = String.format("%1$-" + (text.length() + spaceCount) + 's', text);
        text = text + " ";
        text = String.format("%1$" + 32 + 's', text);

        return text;
    }


    private String centerTVSAlignWithPadding(String text) {
        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            return centerTVS80MMAlignWithPadding(text);
        }
        int spaceCount = 0;

        if (text != null && text.length() < 40) {
            spaceCount = (50 - text.length()) / 2;
        }

        text = String.format("%1$-" + (text.length() + spaceCount) + 's', text);
        text = String.format("%1$" + 44 + 's', text);

        return text;
    }

    private String centerTVS80MMAlignWithPadding(String text) {
        int spaceCount = 0;

        if (text != null && text.length() < 80) {
            spaceCount = (100 - text.length()) / 2;
        }

        text = String.format("%1$-" + (text.length() + spaceCount) + 's', text);
        text = String.format("%1$" + 88 + 's', text);

        return text;
    }

    private String centerAlignWithPadding(String text) {

        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            return centerAlignWithPaddingTVS80MM(text);
        }
        int spaceCount = 0;

        if (text != null && text.length() < 40) {
            spaceCount = (44 - text.length()) / 2;
        }

        text = String.format("%1$-" + (text.length() + spaceCount) + 's', text);
        text = String.format("%1$" + 44 + 's', text);

        return text;
    }

    private String centerAlignWithPaddingTVS80MM(String text) {
        int spaceCount = 20;

        text = text.trim();
        text = text + "\n";

        if (text != null && text.length() < 50) {
            spaceCount = (50 - text.length()) / 2;
        }

        text = String.format("%1$" + (spaceCount + text.length()) + 's', text);
        //  text = String.format("%1$" + 80 + 's', text);


        return text;
    }

    private String rightPad(String text, int length) {
        return text = String.format("%1$-" + length + 's', text);
    }

    private String leftPad(String text, int length) {
        return text = String.format("%1$" + 44 + 's', text);
    }


    public void reduceFontSize(boolean isReduce) {

        Log.v(PROBER, "reduce font size: " + isReduce);
        if (isReduce) {
            byte[] data = new byte[]{(byte) 0x1B, (byte) 0x21, (byte) 0x01};
            writeDataOnThermalPrinterHeader(data);
            amcuConfig.setIsSmartMoofontReduced(true);
        } else {
            byte[] data = new byte[]{(byte) 0x1B, (byte) 0x21, (byte) 0x02};
            writeDataOnThermalPrinterHeader(data);
            amcuConfig.setIsSmartMoofontReduced(false);
        }
    }


    public String getPeriodicTitle(String startDate,
                                   String endDate,
                                   boolean allowHeader,
                                   String type) {
        String headline = null, socName = null, socCode = null;
        String header = "Provisional Periodic Summary";

        if (type == null) {

        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
            header = "Samples Periodic Summary";
        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_SALES)) {
            header = "Sales Periodic Summary";
        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)) {
            header = "Agents Periodic Summary";
        }


        if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {
            headline = centerAlignWithPadding(header);
            socName = centerAlignWithPadding("  "
                    + session.getSocietyName());
            socCode = centerAlignWithPadding("   "
                    + session.getCollectionID());

        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            headline = centerAlignWithPadding(header).replace("\n", "");
            socName = centerAlignWithPadding("  "
                    + session.getSocietyName()).replace("\n", "");
            socCode = centerAlignWithPadding("   "
                    + session.getCollectionID()).replace("\n", "");

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL HATSUN")) {
            headline = centerAlignWithPaddingThermal(header);
            socName = centerAlignWithPaddingThermal("  "
                    + session.getSocietyName());
            socCode = centerAlignWithPaddingThermal("   "
                    + session.getCollectionID());

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            headline = centerAlignWithPaddingThermalSmartMooReduced(header);
            socName = centerAlignWithPaddingThermalSmartMooReduced("  "
                    + session.getSocietyName());
            socCode = centerAlignWithPaddingThermalSmartMooReduced("   "
                    + session.getCollectionID());
        } else {

            headline = centerAlignWithPaddingThermalSmartMoo(header);
            socName = centerAlignWithPaddingThermalSmartMoo("  "
                    + session.getSocietyName());
            socCode = centerAlignWithPaddingThermalSmartMoo("   "
                    + session.getCollectionID());
        }

        String title = "";
        if (allowHeader) {
            title = headline + "\n" + socName + "\n" + socCode;
        }
        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            title = title + "\n" + centerAlignWithPadding(
                    "From " + startDate.trim() + "  To " + endDate.trim());

        } else {
            title = title + "\n" + "From " + startDate.trim() + "  To " + endDate.trim();

        }
        return title;
    }

/*
    public String getPeriodicTitle() {
        String headline = null, socName = null, socCode = null;
        String header = "Provisional Periodic Summary";
        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL HATSUN")) {
            headline = centerAlignWithPaddingThermal(header);
            socName = centerAlignWithPaddingThermal("  "
                    + session.getSocietyName());
            socCode = centerAlignWithPaddingThermal("   "
                    + session.getCollectionID());

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            headline = centerAlignWithPaddingThermalSmartMooReduced(header);
            socName = centerAlignWithPaddingThermalSmartMooReduced("  "
                    + session.getSocietyName());
            socCode = centerAlignWithPaddingThermalSmartMooReduced("   "
                    + session.getCollectionID());
        } else {

            headline = centerAlignWithPaddingThermalSmartMoo(header);
            socName = centerAlignWithPaddingThermalSmartMoo("  "
                    + session.getSocietyName());
            socCode = centerAlignWithPaddingThermalSmartMoo("   "
                    + session.getCollectionID());
        }
        return headline + "\n" + socName + "\n" + socCode;
    }*/


    public String getPeriodicSubTitle(String startDate, String endDate, String type) {

        String header = "Provisional Periodic Summary\"";
        if (type == null) {

        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
            header = "Samples Periodic Summary";
        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_SALES)) {
            header = "Sales Periodic Summary";
        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)) {
            header = "Agents Periodic Summary";
        }

        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            return centerAlignWithPaddingThermal(header) + "\n"
                    + getCenterDetails() + "\n"
                    + "\n" + "        From  " +
                    startDate + "  To " + endDate;
        } else {
            return centerAlignWithPaddingThermal(header) + "\n"
                    + getCenterDetails()
                    + centerAlignWithPadding("\n" + "From  " +
                    startDate + "  To " + endDate);
        }
    }

    public String getShiftSubTitle(String date, String shift, String type) {

        String header = "Provisional Shift Summary\"";
        if (type == null) {

        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
            header = "Samples Shift Summary";
        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_SALES)) {
            header = "Sales Shift Summary";
        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)) {
            header = "Agents Shift Summary";
        }


        return centerAlignWithPaddingThermal(header) + "\n" +
                getCenterDetails() +
                centerAlignWithPadding("\n" + "Date:  " + date + "  Shift: " + shift);
    }

    public String getMemberBillSummaryTitle(String startDate, String endDate) {
        return centerAlignWithPaddingThermal("Member's bill Summary") + "\n" +
                getCenterDetails()
                + centerAlignWithPadding("\n" + "From  " + startDate + "  To " + endDate);
    }

    public String getAgentBillSummaryTitle(String startDate, String endDate) {
        return centerAlignWithPaddingThermal("Agent's bill Summary") + "\n" +
                getCenterDetails()
                + centerAlignWithPadding("\n" + "From  " + startDate + "  To " + endDate);
    }

    public String getMemberBillRegisterTitle(String startDate, String endDate) {
        return centerAlignWithPaddingThermal("Member's bill Summary") + "\n" +
                getCenterDetails()
                + centerAlignWithPadding("\n" + "From  " + startDate + "  To " + endDate);
    }

    public String getCenterDetails() {
        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            return centerAlignWithPaddingThermal("  "
                    + session.getSocietyName()) + "\n" +
                    centerAlignWithPadding("   "
                            + session.getCollectionID());
        } else {
            return centerAlignWithPaddingThermal("  "
                    + session.getSocietyName()) + "\n" +
                    centerAlignWithPaddingThermal("   "
                            + session.getCollectionID());
        }
    }

    /**
     * To construct header for various type of
     * periodic and shift report
     *
     * @param type
     * @param startDate
     * @param endDate
     * @param shift
     * @return
     */

    public String constructHeader(int type, String startDate, String endDate, String shift) {

        StringBuilder header = new StringBuilder();

        if (type == printReciept || type == printtotalPR || type == printperiodicReort
                || type == dispatchReceipt || type == salesReceipt) {

            String dairyName = "";
            //Display Dairy name
            if (amcuConfig.getDisplayDairyName() != null && !amcuConfig.getDisplayDairyName().toUpperCase().equalsIgnoreCase("NULL") && amcuConfig.getDisplayDairyName().trim().length() > 0) {
                if (amcuConfig.getDisplayDairyName().length() > 30) {
                    dairyName = amcuConfig.getDisplayDairyName().substring(0, 30);
                } else {
                    dairyName = amcuConfig.getDisplayDairyName().toUpperCase();

                }

                header.append(dairyName + "\n");
            }
        }

        header.append(session.getSocietyName() + "\n");
        header.append(session.getCollectionID() + "\n");
        if (type == printReciept) {
            header.append("smartAMCU" + "\n");
            header.append("Automatic Milk Collection Unit" + "\n");
        } else if (type == dispatchReceipt) {
            header.append("Dispatch Note" + "\n");
            header.append("smartAMCU" + "\n");
            header.append("Automatic Milk Collection Unit" + "\n");
        } else if (type == salesReceipt) {
            header.append("Sales Receipt" + "\n");
            header.append("smartAMCU" + "\n");
            header.append("Automatic Milk Collection Unit" + "\n");
        } else if (type == printShiftReport) {
            header.append("Provisional Shift Summary" + "\n");
            header.append("Date: " + startDate + "   Shift: " + shift + "\n");
        } else if (type == printChillingReport) {
            header.append("Provisional center Summary" + "\n");
            header.append("Date: " + startDate + "   Shift: " + shift + "\n");
        } else if (type == printperiodicReort) {
            header.append("Provisional Periodic Summary" + "\n");
            header.append("From: " + startDate + "   To: " + endDate + "\n");

        } else if (type == printsalesReport) {
            header.append("Provisional Sales Summary" + "\n");
            header.append("From: " + startDate + "   To: " + endDate + "\n");
        } else if (type == printtotalPR) {
            header.append("Provisional Milk Bill Summary" + "\n");
            header.append("From: " + startDate + "   To: " + endDate + "\n");
        } else if (type == printIndividualReport) {
            header = new StringBuilder();
        } else if (type == printReport) {

        } else if (type == printDispatchReport) {
            header.append("Provisional Dispatch Summary" + "\n");
            header.append("From: " + startDate + "   To: " + endDate + "\n");

        } else if (type == shiftDispatchReport) {
            header.append("Provisional Dispatch Summary" + "\n");
            header.append("Date: " + startDate + "   Shift: " + shift + "\n");
        } else {
            header = new StringBuilder();
        }

        return header.toString();


    }


    public String constructHeaderForTVS(int type, String startDate, String endDate, String shift) {
        StringBuilder header = new StringBuilder();

        if (type == printReciept || type == printtotalPR || type == dispatchReceipt || type == salesReceipt) {
            String dairyName = "";
            //Display Dairy name
            if (amcuConfig.getDisplayDairyName() != null && !amcuConfig.getDisplayDairyName().toUpperCase().equalsIgnoreCase("NULL")
                    && amcuConfig.getDisplayDairyName().length() > 0) {
                if (amcuConfig.getDisplayDairyName().length() > 20) {
                    dairyName = amcuConfig.getDisplayDairyName().substring(0, 20).toUpperCase();
                } else {
                    dairyName = amcuConfig.getDisplayDairyName().toUpperCase().toUpperCase();

                }
                header.append(centerTVSAlignWithPadding(dairyName + "\n"));
            }
        }
        header.append(centerAlignWithPadding("" + session.getSocietyName() + "\n"));
        header.append(centerAlignWithPadding("    " + session.getCollectionID() + "\n"));

        if (type == printReciept) {
            if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                    || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
                header.append(centerAlignWithPadding("smartAMCU" + "\n"));
                header.append(centerAlignWithPadding("Automatic Milk Collection Unit" + "\n"));

            } else {
                header.append("   " + centerAlignWithPadding("smartAMCU") + "\n");
                header.append("          " + "Automatic Milk Collection Unit" + "\n");

            }
        } else if (type == dispatchReceipt) {
            if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                    || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
                header.append(centerAlignWithPadding("Dispatch Note" + "\n"));
                header.append(centerAlignWithPadding("smartAMCU" + "\n"));
                header.append(centerAlignWithPadding("Automatic Milk Collection Unit" + "\n"));
            } else {
                header.append("   " + centerAlignWithPadding("Dispatch Note") + "\n");
                header.append("   " + centerAlignWithPadding("smartAMCU") + "\n");
                header.append("          " + "Automatic Milk Collection Unit" + "\n");
            }
        } else if (type == salesReceipt) {
            if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI) ||
                    amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
                header.append(centerAlignWithPadding("Sales Receipt" + "\n"));
                header.append(centerAlignWithPadding("smartAMCU" + "\n"));
                header.append(centerAlignWithPadding("Automatic Milk Collection Unit" + "\n"));
            } else {
                header.append("   " + centerAlignWithPadding("Sales Receipt") + "\n");
                header.append("   " + centerAlignWithPadding("smartAMCU") + "\n");
                header.append("          " + "Automatic Milk Collection Unit" + "\n");
            }
        } else if (type == printShiftReport) {
            if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI) ||
                    amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {

                header.append(centerAlignWithPadding("Provisional Shift Summary" + "\n"));
                header.append(centerAlignWithPadding("Date: " + startDate + "   Shift: " + shift + "\n"));
            } else {
                header.append("  " + centerAlignWithPadding("Provisional Shift Summary") + "\n");
                header.append("  " + centerAlignWithPadding("Date: " + startDate + "   Shift: " + shift) + "\n");
            }
        } else if (type == printChillingReport) {
            header.append("  " + centerAlignWithPadding("Provisional center Summary") + "\n");
            header.append("  " + centerAlignWithPadding("Date: " + startDate + "   Shift: " + shift) + "\n");
        } else if (type == printperiodicReort) {
            if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
                    || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
                header.append(centerAlignWithPadding("Provisional Periodic Summary" + "\n"));
                header.append(centerAlignWithPadding("From: " + startDate + "   To: " + endDate + "\n"));
            } else {
                header.append("  " + centerAlignWithPadding("Provisional Periodic Summary") + "\n");
                header.append("  " + centerAlignWithPadding("From: " + startDate + "   To: " + endDate) + "\n");
            }

        } else if (type == printDispatchReport) {
            if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI) ||
                    amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {

                header.append(centerAlignWithPadding("Provisional Dispatch Summary" + "\n"));
                header.append(centerAlignWithPadding("From: " + startDate + "   To: " + endDate + "\n"));
            } else {
                header.append("  " + centerAlignWithPadding("Provisional Dispatch Summary") + "\n");
                header.append("  " + centerAlignWithPadding("From: " + startDate + "   To: " + endDate) + "\n");
            }

        } else if (type == printsalesReport) {
            header.append("  " + centerAlignWithPadding("Provisional Sales Summary") + "\n");
            header.append("  " + centerAlignWithPadding(" From: " + startDate + "   To: " + endDate) + "\n");
        } else if (type == printtotalPR) {
            header.append("  " + centerAlignWithPadding("Provisional Milk Bill Summary") + "\n");
            header.append("  " + centerAlignWithPadding("From: " + startDate + "   To: " + endDate) + "\n");
        } else if (type == printIndividualReport) {
            header = new StringBuilder();
        } else if (type == printReport) {

        } else {
            header = new StringBuilder();
        }
        return header.toString();
    }


    @Override
    public void onWriteComplete() {
        if (mDevice != null)
            mDevice.unregisterObserver();
    }
}
