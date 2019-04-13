package com.devapp.devmain.user;

import android.content.Context;

import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.DispatchRecordDao;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.CenterRecordEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.util.AppConstants;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.report.ReportHintConstant;
import com.devApp.BuildConfig;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormatPrintRecords {
    public static final int TYPE_SUMMARY = 0;
    AmcuConfig amcuConfig;
    SessionManager session;
    String strBuild;
    ArrayList<ReportEntity> allShiftReportEnt, cowShiftReportEnt,
            buffShiftReportEnt, mixShiftReportEnt, sampleTestRecords, allAssCenterRecord, allFarmersAndCenters;

    ArrayList<CenterRecordEntity> allCenterRecords, allCowCenterRecords, allBuffCenterRecords, allMixCenterRecords;

    ArrayList<SalesRecordEntity> allSalesRecords, cowSalesRecords,
            buffSalesRecords, mixSalesRecords;

    AverageReportDetail averageChillingCenterReport;
    SmartCCUtil smartCCUtil;
    DispatchRecordDao dispatchRecordDao;
    private Context mContext;
    private PrinterManager mPrinterManager;


    public FormatPrintRecords(Context ctx) {
        mContext = ctx;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(mContext);
        smartCCUtil = new SmartCCUtil(mContext);
        this.mPrinterManager = new PrinterManager(mContext);
        dispatchRecordDao = (DispatchRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_DISPATCH);
        strBuild = "";

    }


    public String onPrintShiftReport(int checkEndshift, String shift, long lDate) {

        if (checkEndshift == Util.sendEndShiftReport) {

            allFarmersAndCenters = Util.getShiftReport(mContext, null, 8,
                    shift, lDate);

            allShiftReportEnt = Util.getShiftReport(mContext, null, checkEndshift,
                    shift, lDate);
            cowShiftReportEnt = Util.getShiftReport(mContext, "COW", checkEndshift,
                    shift, lDate);
            buffShiftReportEnt = Util.getShiftReport(mContext, "BUFFALO",
                    checkEndshift, shift, lDate);
            mixShiftReportEnt = Util.getShiftReport(mContext, "MIXED",
                    checkEndshift, shift, lDate);
            sampleTestRecords = Util.getShiftReport(mContext, "TEST",
                    checkEndshift, shift, lDate);
            allAssCenterRecord = Util.getShiftReport(mContext, null,
                    5, shift, lDate);


        } else {

            allFarmersAndCenters = Util.getShiftReport(mContext, null, 8,
                    shift, lDate);

            allShiftReportEnt = Util.getShiftReport(mContext, null, 2,
                    shift, lDate);
            cowShiftReportEnt = Util.getShiftReport(mContext, "COW", 2,
                    shift, lDate);
            buffShiftReportEnt = Util.getShiftReport(mContext, "BUFFALO",
                    2, shift, lDate);
            mixShiftReportEnt = Util.getShiftReport(mContext, "MIXED",
                    2, shift, lDate);
            sampleTestRecords = Util.getShiftReport(mContext, "TEST",
                    2, shift, lDate);
            allAssCenterRecord = Util.getShiftReport(mContext, null,
                    4, shift, lDate);
        }
        averageChillingCenterReport = new SmartCCUtil(mContext).getAverageOfReport(allAssCenterRecord, false);
        getShiftReport();
        return strBuild;

    }

    public String onPrintShiftDispatchReport(long lDate) {
        strBuild = "";
        ArrayList<DispatchPostEntity> entityArrayList = Util.getDispatchReportForCurrentShift(mContext, lDate);
        strBuild = strBuild + "\n" + formatDispatchHeader() + "\n";
        setDataForDispatchReports(entityArrayList);
        return strBuild;
    }

    public String getDispatchReport(long startDate, long endDate) {


        ArrayList<DispatchPostEntity> entityArrayList =
                (ArrayList<DispatchPostEntity>) dispatchRecordDao.getDispatchEntities(startDate, endDate);

        strBuild = strBuild + "\n" + formatDispatchHeader() + "\n";
        setDataForDispatchReports(entityArrayList);
        return strBuild;
    }


    public String formatSalesRecord(SalesRecordEntity repEnt, int serialNum) {

        // String time = repEnt.time;
        String salesId = repEnt.salesId;
        if (salesId == null || salesId.equalsIgnoreCase("NULL") || salesId.equals(""))
            salesId = "0000";
        String code = Util.paddingFarmerId(salesId, 4);

        String qty = String.valueOf(repEnt.Quantity);
        String fat = String.valueOf(repEnt.fat);
        String snf = String.valueOf(repEnt.snf);
        String rate = String.valueOf(repEnt.rate);

        String amount = String.valueOf(repEnt.amount);
        String time = repEnt.time;
        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        DecimalFormat decimalFormatAmt = new DecimalFormat("#0.00");

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces

            try {
                rate = decimalFormatFS.format(Double.parseDouble(rate));
                amount = decimalFormatFS.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 7 spaces
            try {
                fat = decimalFormatFS.format(Double.parseDouble(fat));
                snf = decimalFormatFS.format(Double.parseDouble(snf));
                rate = decimalFormatAmt.format(Double.parseDouble(rate));
                amount = decimalFormatAmt.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }
        String collectionRecord = null;
        if (code.length() > 4) {
            collectionRecord = (fixedLength8 + code).substring(time.length())
                    + (fixedLength8 + qty).substring(qty.length())
                    + (fixedLength4 + fat).substring(fat.length())
                    + (fixedLength4 + snf).substring(snf.length())
                    + (fixedLength7 + rate).substring(rate.length())
                    + (fixedLength8 + amount).substring(amount.length());
        } else {
            collectionRecord = (fixedLength6 + time).substring(code.length())
                    + (fixedLength5 + code).substring(time.length())
                    + (fixedLength5 + qty).substring(qty.length())
                    + (fixedLength4 + fat).substring(fat.length())
                    + (fixedLength4 + snf).substring(snf.length())
                    + (fixedLength7 + rate).substring(rate.length())
                    + (fixedLength8 + amount).substring(amount.length());
        }

        return collectionRecord;
    }

    public String paddCenterId(String farmId) {

        if (farmId.length() == 1) {
            return "   " + farmId;
        } else if (farmId.length() == 2) {
            return "  " + farmId;
        } else if (farmId.length() == 3) {
            return " " + farmId;
        } else if (farmId.length() > 4) {
            return farmId.substring((farmId.length() - 4), farmId.length());
        } else {
            return farmId;
        }

    }


    //to set chilling center record

    public String formatRecord(ReportEntity repEnt) {


        // String time = repEnt.time;
        String code = "0000";
        String collectionRecord = null;
        if (repEnt != null && repEnt.collectionType != null
                && repEnt.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            code = paddCenterId(repEnt.farmerId);
            repEnt.quantity = repEnt.getPrintAndReportQuantity();
            repEnt.ltrsWeight = repEnt.getPrintAndReportLtQuantity();
            repEnt.kgWeight = repEnt.getPrintAndReportKgQuantity();
        } else {
            code = repEnt.farmerId;
        }
        if (code.equalsIgnoreCase("")) {
            code = "    ";
        }
        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        DecimalFormat decimalFormatAmt = new DecimalFormat("#0.00");
        DecimalFormat decimalFormatWholeNum = new DecimalFormat("#0");

        String qty = String.valueOf(repEnt.getPrintAndReportQuantity());

        ValidationHelper validationHelper = new ValidationHelper();

        String fat = validationHelper.getValidString(String.valueOf(repEnt.getPrinterFat()), decimalFormatFS);
        String snf = validationHelper.getValidString(String.valueOf(repEnt.getPrinterSnf()), decimalFormatFS);
        String fatKg = validationHelper.getValidString(String.valueOf(repEnt.getFatKg()), decimalFormatAmt);
        String snfKg = validationHelper.getValidString(String.valueOf(repEnt.getSnfKg()), decimalFormatAmt);

        //To display clr

        if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
            snf = String.valueOf(repEnt.clr);
        }

        String rate = String.valueOf(repEnt.rate);

        String amount = String.valueOf(Util.getAmount(mContext, repEnt.getAmountWithBonus(), repEnt.bonus));
        String time = repEnt.time;

        try {
            double dQty = Double.parseDouble(qty);
            if (dQty > 100) {
                qty = decimalFormatWholeNum.format(Double.parseDouble(qty));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces

            try {
                rate = decimalFormatFS.format(Double.parseDouble(rate));
                amount = decimalFormatFS.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 7 spaces
            try {
                fat = decimalFormatFS.format(Double.parseDouble(fat));
                snf = decimalFormatFS.format(Double.parseDouble(snf));
                rate = decimalFormatAmt.format(Double.parseDouble(rate));
                amount = decimalFormatAmt.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if ((amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)
        )) {
            fixedLength5 = "         "; // 9 spaces
            fixedLength4 = "        "; // 8 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "        "; // 8 spaces
            fixedLength8 = "            "; // 12 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(
                AppConstants.PRINTER_TAMIL)) {

            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(System.nanoTime() + time, getSpace(2));
            map.put(System.nanoTime() + code, getSpace(3));
            map.put(System.nanoTime() + new DecimalFormat("0000.00").format(
                    Double.valueOf(qty)), getSpace(2));
            map.put(System.nanoTime() + new DecimalFormat("00.00").format(
                    Double.valueOf(fat)), getSpace(4));
            map.put(System.nanoTime() + new DecimalFormat("00.00").format(
                    Double.valueOf(snf)), getSpace(3));

            if (amcuConfig.getKeyAllowDisplayRate()) {
                map.put(System.nanoTime() + new DecimalFormat("00.00").format(
                        Double.valueOf(rate)), getSpace(5));
                map.put(System.nanoTime() + new DecimalFormat("00000.00").format(
                        Double.valueOf(amount)), getSpace(12));
            } else {

                map.put(System.nanoTime() + fatKg, getSpace(12));
                map.put(System.nanoTime() + snfKg, getSpace(12));

            }
            collectionRecord = getPrintDataFromMap(map);

            return collectionRecord;
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces


        }

        if (amcuConfig.getFarmerIdDigit() > 4) {
            if (amcuConfig.getKeyAllowDisplayRate()) {

                collectionRecord = (fixedLength8 + code).substring(time.length())
                        + (fixedLength8 + qty).substring(qty.length())
                        + (fixedLength4 + fat).substring(fat.length())
                        + (fixedLength4 + snf).substring(snf.length())
                        + (fixedLength7 + rate).substring(rate.length())
                        + (fixedLength8 + amount).substring(amount.length());
            } else {
                collectionRecord = (fixedLength8 + code).substring(time.length())
                        + (fixedLength8 + qty).substring(qty.length())
                        + (fixedLength4 + fat).substring(fat.length())
                        + (fixedLength4 + snf).substring(snf.length())
                        + (fixedLength7 + fatKg).substring(fatKg.length())
                        + (fixedLength8 + snfKg).substring(snfKg.length());

            }
        } else {
            if (repEnt != null && repEnt.collectionType != null
                    && repEnt.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {

                if (amcuConfig.getKeyAllowDisplayRate()) {
                    collectionRecord = (fixedLength6 + time)
                            .substring(code.length())
                            + (fixedLength5 + code).substring(time.length())
                            + (fixedLength5 + qty).substring(qty.length())
                            + (fixedLength4 + fat).substring(fat.length())
                            + (fixedLength4 + snf).substring(snf.length())
                            + (fixedLength7 + rate).substring(rate.length())
                            + (fixedLength8 + amount).substring(amount.length());
                } else {
                    collectionRecord = (fixedLength6 + time)
                            .substring(code.length())
                            + (fixedLength5 + code).substring(time.length())
                            + (fixedLength5 + qty).substring(qty.length())
                            + (fixedLength4 + fat).substring(fat.length())
                            + (fixedLength4 + snf).substring(snf.length())
                            + (fixedLength7 + fatKg).substring(fatKg.length())
                            + (fixedLength8 + snfKg).substring(snfKg.length());

                }
            } else {

                if (amcuConfig.getKeyAllowDisplayRate()) {
                    collectionRecord = (fixedLength6 + time)
                            .substring(code.length())
                            + (fixedLength5 + code).substring(time.length())
                            + (fixedLength5 + qty).substring(qty.length())
                            + (fixedLength4 + fat).substring(fat.length())
                            + (fixedLength4 + snf).substring(snf.length())
                            + (fixedLength7 + rate).substring(rate.length())
                            + (fixedLength8 + amount).substring(amount.length());
                } else {
                    collectionRecord = (fixedLength6 + time)
                            .substring(code.length())
                            + (fixedLength5 + code).substring(time.length())
                            + (fixedLength5 + qty).substring(qty.length())
                            + (fixedLength4 + fat).substring(fat.length())
                            + (fixedLength4 + snf).substring(snf.length())
                            + (fixedLength7 + fatKg).substring(fatKg.length())
                            + (fixedLength8 + snfKg).substring(snfKg.length());

                }

            }

        }

        return collectionRecord;
    }

    public String formatDispatchRecord(DispatchPostEntity repEnt) {

        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");

        ValidationHelper validationHelper = new ValidationHelper();
        String time = repEnt.time;
//        String shift = repEnt.shift;
        String type = Util.getMilkTypeInitials(repEnt.milkType);
        String fat = validationHelper.getValidString(
                String.valueOf(repEnt.qualityParams.milkAnalyser.qualityReadingData.fat), decimalFormatFS);
        String snf = validationHelper.getValidString(
                String.valueOf(repEnt.qualityParams.milkAnalyser.qualityReadingData.snf), decimalFormatFS);

        String qty = String.valueOf(repEnt.quantityParams.weighingScaleData.measuredValue);

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 7 spaces
            try {
                fat = decimalFormatFS.format(Double.parseDouble(fat));
                snf = decimalFormatFS.format(Double.parseDouble(snf));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {

            type = addSuffixSpaces(repEnt.milkType, 10);


            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(System.nanoTime() + time, getSpace(3));
            map.put(System.nanoTime() + type, getSpace(2));
            map.put(System.nanoTime() + new DecimalFormat("00.00").format(Double.valueOf(fat))
                    , getSpace(5));
            map.put(System.nanoTime() + new DecimalFormat("00.00").format(Double.valueOf(snf))
                    , getSpace(3));
            map.put(System.nanoTime() + new DecimalFormat("0000.00").format(Double.valueOf(qty))
                    , getSpace(0));

            return getPrintDataFromMap(map);


        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }
        String collectionRecord = null;


        collectionRecord = (fixedLength5 + time).substring(time.length())
                + (fixedLength5 + type).substring(type.length())
                + (fixedLength5 + fat).substring(fat.length())
                + (fixedLength5 + snf).substring(snf.length())
                + (fixedLength7 + qty).substring(qty.length());

        return collectionRecord;
    }

    public String incentiveFormatRecord(ReportEntity repEnt) {

        // String time = repEnt.time;
        String code = "0000";
        if (repEnt != null && repEnt.collectionType != null
                && repEnt.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            code = paddCenterId(repEnt.farmerId);
            repEnt.quantity = repEnt.getPrintAndReportQuantity();
            repEnt.ltrsWeight = repEnt.getPrintAndReportLtQuantity();
            repEnt.kgWeight = repEnt.getPrintAndReportKgQuantity();

        } else {
            code = repEnt.farmerId;
        }
        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        DecimalFormat decimalFormatAmt = new DecimalFormat("#0.00");
        DecimalFormat decimalFormatWholeNum = new DecimalFormat("#0");
        String totalAmount = "NA";
        String qty = String.valueOf(repEnt.getDisplayQuantity());
        String protein = String.valueOf(repEnt.getDisplayProtein());
        String incentive = String.valueOf(repEnt.incentiveAmount);
//        TODO Fetch the appropriate amount
        totalAmount = decimalFormatAmt.format(repEnt.getTotalAmount());
        //To display clr
        if (session.getMCCStatus()) {
            if (!session.getRecordStatusComplete()) {
                protein = "NA";
                incentive = "NA";

            }
        }

        String rate = String.valueOf(repEnt.rate);
//        TODO fetch the appropriate amount
        String amount = String.valueOf(Util.getAmount(mContext, repEnt.getTotalAmount(), repEnt.bonus));
        String time = repEnt.time;

        try {
            double dQty = Double.parseDouble(qty);
            if (dQty > 100) {
                qty = decimalFormatWholeNum.format(Double.parseDouble(qty));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces

            try {
                rate = decimalFormatFS.format(Double.parseDouble(rate));
                totalAmount = decimalFormatFS.format(Double.parseDouble(totalAmount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 7 spaces
            try {
                protein = decimalFormatFS.format(Double.parseDouble(protein));
                incentive = decimalFormatAmt.format(Double.parseDouble(incentive));
                totalAmount = decimalFormatAmt.format(Double.parseDouble(totalAmount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }
        String collectionRecord = null;
        if (amcuConfig.getFarmerIdDigit() > 4) {
            collectionRecord = (fixedLength8 + code).substring(time.length())
                    + (fixedLength8 + qty).substring(qty.length())
                    + (fixedLength8 + protein).substring(protein.length())
                    + (fixedLength7 + incentive).substring(incentive.length())
                    + (fixedLength8 + totalAmount).substring(totalAmount.length());
        } else {
            if (repEnt != null && repEnt.collectionType != null
                    && repEnt.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
                collectionRecord = (fixedLength6 + time)
                        .substring(code.length())
                        + (fixedLength5 + code).substring(time.length())
                        + (fixedLength5 + qty).substring(qty.length())
                        + (fixedLength8 + protein).substring(protein.length())
                        + (fixedLength7 + incentive).substring(incentive.length())
                        + (fixedLength8 + totalAmount).substring(totalAmount.length());
            } else {

                collectionRecord = (fixedLength6 + time)
                        .substring(code.length())
                        + (fixedLength5 + code).substring(time.length())
                        + (fixedLength5 + qty).substring(qty.length())
                        + (fixedLength8 + protein).substring(protein.length())
                        + (fixedLength7 + incentive).substring(incentive.length())
                        + (fixedLength8 + totalAmount).substring(totalAmount.length());

            }

        }

        return collectionRecord;
    }


    public String formatChillingRecord(CenterRecordEntity repEnt) {

        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        DecimalFormat decimalFormatAmt = new DecimalFormat("#0.00");

        // String time = repEnt.time;
        String code = repEnt.centerId;
        String qty = decimalFormatAmt.format(repEnt.quantity);
        String fat = decimalFormatFS.format(repEnt.fat);
        String snf = decimalFormatFS.format(repEnt.snf);
        String rate = decimalFormatAmt.format(repEnt.rate);

        String amount = decimalFormatAmt.format(repEnt.amount);
        String time = repEnt.time;


        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces

            try {
                rate = decimalFormatFS.format(Double.parseDouble(rate));
                amount = decimalFormatFS.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 7 spaces
            try {
                fat = decimalFormatFS.format(Double.parseDouble(fat));
                snf = decimalFormatFS.format(Double.parseDouble(snf));
                rate = decimalFormatAmt.format(Double.parseDouble(rate));
                amount = decimalFormatAmt.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }
        String collectionRecord = null;
        if (code.length() > 4) {
            collectionRecord = (fixedLength8 + code).substring(time.length())
                    + (fixedLength8 + qty).substring(qty.length())
                    + (fixedLength4 + fat).substring(fat.length())
                    + (fixedLength4 + snf).substring(snf.length())
                    + (fixedLength7 + rate).substring(rate.length())
                    + (fixedLength8 + amount).substring(amount.length());
        } else {
            collectionRecord = (fixedLength6 + time)
                    .substring(code.length())
                    + (fixedLength5 + code).substring(time.length())
                    + (fixedLength5 + qty).substring(qty.length())
                    + (fixedLength4 + fat).substring(fat.length())
                    + (fixedLength4 + snf).substring(snf.length())
                    + (fixedLength7 + rate).substring(rate.length())
                    + (fixedLength8 + amount).substring(amount.length());
        }

        return collectionRecord;
    }

    String getSignatureLength(int length) {
        String signature = "_";
        for (int i = 0; i < length; i++) {
            signature = signature + "_";
        }

        return signature;
    }

    public String formatTotalPeriodicRecord(ReportEntity repEnt) {

        // String time = repEnt.time;
        String code = repEnt.farmerId;
        String qty = String.valueOf(repEnt.getPrinterQuantity());
        String rate = String.valueOf(repEnt.getPrinterRate());
//        TODO Fetch the appropriate amount
        String amount = String.valueOf(Util.getAmount(mContext, repEnt.getTotalAmount(), repEnt.bonus));
        String signature = getSignatureLength(4);
        String name = repEnt.farmerName;
        if (name != null && name.length() > 15) {
            name = repEnt.farmerName.substring(0, 15);
        }


        DecimalFormat decimalFormatAmt = new DecimalFormat("#0.00");

        String fixedLength10, fixedLength7, fixedLength5 = "", fixedLength6, fixedLength4, fixedLength8, fixedLength17 = "", fixedLength3 = "";

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
            fixedLength8 = "     "; // 5 spaces
            fixedLength10 = "      ";
            fixedLength7 = "    ";
            fixedLength6 = "   "; // 4 spaces


            try {
                rate = decimalFormatAmt.format(Double.parseDouble(rate));
                amount = decimalFormatAmt.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength10 = "         ";
            fixedLength7 = "       ";
            fixedLength6 = "      "; // 4 spaces
            fixedLength17 = "                ";
            fixedLength5 = "     ";

            try {
                rate = decimalFormatAmt.format(Double.parseDouble(rate));
                amount = decimalFormatAmt.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {
            fixedLength6 = "     "; // 6 spaces
            fixedLength10 = "         ";
            fixedLength7 = "       ";
            fixedLength8 = "        "; // 8 spaces
            fixedLength17 = "                ";

        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {

            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(System.nanoTime() + code, getSpace(3));

            if (amcuConfig.getDisplayNameInReport()) {
                map.put(System.nanoTime() + name, getSpace(3));
                map.put(System.nanoTime() + qty, getSpace(3));
                map.put(System.nanoTime() + rate, getSpace(3));
                map.put(System.nanoTime() + amount, getSpace(3));
            } else {
                map.put(System.nanoTime() + new DecimalFormat("0000.00").format(
                        Double.valueOf(qty)), getSpace(7));
                map.put(System.nanoTime() + new DecimalFormat("00.00").format(
                        Double.valueOf(rate)), getSpace(10));
                map.put(System.nanoTime() + new DecimalFormat("00000.00").format(
                        Double.valueOf(amount)), getSpace(4));
                map.put(System.nanoTime() + signature, getSpace(0));

            }
            return getPrintDataFromMap(map);
        } else {
            fixedLength6 = "       "; // 8  spaces
            fixedLength10 = "         ";
            fixedLength7 = "       ";
            fixedLength8 = "        "; // 8 spaces
            fixedLength17 = "                ";
        }
        String collectionRecord = null;
        if (amcuConfig.getDisplayNameInReport()) {

            fixedLength7 = "       ";
            //For displaying space right side
            for (int i = name.length(); i <= 15; i++) {
                fixedLength7 = fixedLength7 + " ";
            }
            collectionRecord = (fixedLength6 + code).substring(code.length())
                    + " "
                    + (fixedLength17 + name).substring(name.length()).trim()
                    + (fixedLength7 + qty).substring(qty.length())
                    + (fixedLength10 + amount).substring(amount.length());

        } else {
            collectionRecord = (fixedLength6 + code).substring(code.length())
                    + (fixedLength8 + qty).substring(qty.length())
                    + (fixedLength7 + rate).substring(rate.length())
                    + (fixedLength10 + amount).substring(amount.length())
                    + (fixedLength8 + signature).substring(signature.length());


        }

        return collectionRecord;
    }


    //header for chilling collection

    public String formatHeader() {

        String codeHeader = "Code";
        String qtyHeader = "Qty";
        String fatHeader = "Fat";
        String snfHeader = "Snf";
        String fatKgHeader = "KG-Fat";
        String snfKgHeader = "kG-SNF";

        //To display clr
        if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
            snfHeader = "Clr";
        }

        String rateHeader = "Rate";
        String amountHeader = "Amt";
        String timeHeader = "Time";
        String crHead = null;

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {

            LinkedHashMap<String, String> map = new LinkedHashMap<>();

            codeHeader = mContext.getResources().getString(R.string.tm_id);
            qtyHeader = mContext.getResources().getString(R.string.tm_qty);
            fatHeader = mContext.getResources().getString(R.string.tm_fat);
            snfHeader = mContext.getResources().getString(R.string.tm_snf);
            fatKgHeader = mContext.getResources().getString(R.string.tm_total_kg_fat);
            snfKgHeader = mContext.getResources().getString(R.string.tm_total_kg_snf);

            //To display clr
            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
                snfHeader = mContext.getResources().getString(R.string.tm_clr);
            }

            rateHeader = mContext.getResources().getString(R.string.tm_rate);
            amountHeader = mContext.getResources().getString(R.string.tm_amt);
            timeHeader = mContext.getResources().getString(R.string.tm_time);
            map.put(System.nanoTime() + timeHeader, getSpace(3));
            map.put(System.nanoTime() + codeHeader, getSpace(3));

            map.put(System.nanoTime() + qtyHeader, getSpace(4));
            map.put(System.nanoTime() + fatHeader, getSpace(2));
            map.put(System.nanoTime() + snfHeader, getSpace(4));
                if (amcuConfig.getKeyAllowDisplayRate()) {
                    map.put(System.nanoTime() + rateHeader, getSpace(5));
                    map.put(System.nanoTime() + amountHeader, getSpace(0));
                } else {

                    map.put(System.nanoTime() + fatKgHeader, getSpace(3));
                    map.put(System.nanoTime() + snfKgHeader, getSpace(0));

                }
            return getPrintDataFromMap(map);
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            codeHeader = mContext.getResources().getString(R.string.h_member_id);
            qtyHeader = mContext.getResources().getString(R.string.h_qty);
            fatHeader = mContext.getResources().getString(R.string.h_fat);
            snfHeader = mContext.getResources().getString(R.string.h_snf);
            fatKgHeader = mContext.getResources().getString(R.string.h_total_kg_fat);
            snfKgHeader = mContext.getResources().getString(R.string.h_total_kg_snf);

            //To display clr
            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
                snfHeader = mContext.getResources().getString(R.string.h_clr);
            }

            rateHeader = mContext.getResources().getString(R.string.h_rate);
            amountHeader = mContext.getResources().getString(R.string.h_amt);
            timeHeader = mContext.getResources().getString(R.string.h_time);

            String fixedLength9;

            fixedLength5 = "         "; // 9 spaces
            fixedLength4 = "        "; // 8 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "        "; // 8 spaces
            fixedLength8 = "            "; // 12 spaces
            fixedLength9 = "               ";// 15 spaces
            fixedLength10 = "                      ";//22 spaces
            if (amcuConfig.getFarmerIdDigit() > 4) {
                if (amcuConfig.getKeyAllowDisplayRate()) {
                    crHead = (fixedLength8 + codeHeader).substring(timeHeader.length())
                            + (fixedLength8 + qtyHeader).substring(qtyHeader.length())
                            + (fixedLength4 + fatHeader).substring(fatHeader.length())
                            + (fixedLength4 + snfHeader).substring(snfHeader.length())
                            + (fixedLength7 + rateHeader).substring(rateHeader.length())
                            + (fixedLength8 + amountHeader)
                            .substring(amountHeader.length());
                } else {
                    crHead = (fixedLength8 + codeHeader).substring(timeHeader.length())
                            + (fixedLength8 + qtyHeader).substring(qtyHeader.length())
                            + (fixedLength4 + fatHeader).substring(fatHeader.length())
                            + (fixedLength4 + snfHeader).substring(snfHeader.length())
                            + (fixedLength8 + fatKgHeader).substring(fatKgHeader.length())
                            + (fixedLength8 + snfKgHeader)
                            .substring(snfKgHeader.length());

                }
            } else {
                if (amcuConfig.getKeyAllowDisplayRate()) {
                    crHead = (fixedLength6 + timeHeader).substring(4)
                            + (fixedLength5 + codeHeader).substring(5)
                            + (fixedLength5 + qtyHeader).substring(6)
                            + (fixedLength8 + fatHeader).substring(5)
                            + (fixedLength4 + snfHeader).substring(5)
                            + (fixedLength7 + rateHeader).substring(5)
                            + (fixedLength9 + amountHeader)
                            .substring(6);
                } else {
                    crHead = (fixedLength6 + timeHeader).substring(codeHeader
                            .length())
                            + (fixedLength5 + codeHeader).substring(timeHeader.length())
                            + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                            + (fixedLength4 + fatHeader).substring(fatHeader.length())
                            + (fixedLength4 + snfHeader).substring(snfHeader.length())
                            + (fixedLength8 + fatKgHeader).substring(fatKgHeader.length())
                            + (fixedLength8 + snfKgHeader)
                            .substring(snfKgHeader.length());

                }
            }
            return crHead;
        }

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {

            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 6 spaces
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }

        if (amcuConfig.getFarmerIdDigit() > 4) {
            if (amcuConfig.getKeyAllowDisplayRate()) {
                crHead = (fixedLength8 + codeHeader).substring(timeHeader.length())
                        + (fixedLength8 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength4 + fatHeader).substring(fatHeader.length())
                        + (fixedLength4 + snfHeader).substring(snfHeader.length())
                        + (fixedLength7 + rateHeader).substring(rateHeader.length())
                        + (fixedLength8 + amountHeader)
                        .substring(amountHeader.length());
            } else {
                crHead = (fixedLength8 + codeHeader).substring(timeHeader.length())
                        + (fixedLength8 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength4 + fatHeader).substring(fatHeader.length())
                        + (fixedLength4 + snfHeader).substring(snfHeader.length())
                        + (fixedLength8 + fatKgHeader).substring(fatKgHeader.length())
                        + (fixedLength8 + snfKgHeader)
                        .substring(snfKgHeader.length());

            }
        } else {
            if (amcuConfig.getKeyAllowDisplayRate()) {
                crHead = (fixedLength6 + timeHeader).substring(codeHeader
                        .length())
                        + (fixedLength5 + codeHeader).substring(timeHeader.length())
                        + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength4 + fatHeader).substring(fatHeader.length())
                        + (fixedLength4 + snfHeader).substring(snfHeader.length())
                        + (fixedLength7 + rateHeader).substring(rateHeader.length())
                        + (fixedLength8 + amountHeader)
                        .substring(amountHeader.length());
            } else {
                crHead = (fixedLength6 + timeHeader).substring(codeHeader
                        .length())
                        + (fixedLength5 + codeHeader).substring(timeHeader.length())
                        + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength4 + fatHeader).substring(fatHeader.length())
                        + (fixedLength4 + snfHeader).substring(snfHeader.length())
                        + (fixedLength8 + fatKgHeader).substring(fatKgHeader.length())
                        + (fixedLength8 + snfKgHeader)
                        .substring(snfKgHeader.length());

            }
        }
        return crHead;

    }


    public String formatDispatchHeader() {
        String crHead = null;

        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {

            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_time), getSpace(3));
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_milk_type), getSpace(3));
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_fat), getSpace(3));
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_snf), getSpace(5));
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_qty), getSpace(0));

            crHead = getPrintDataFromMap(map);
            return crHead;
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            String timeHeader = mContext.getResources().getString(R.string.h_time);
            String typeHeader = mContext.getResources().getString(R.string.h_cattle_type);
            String fatHeader = mContext.getResources().getString(R.string.h_fat);
            String snfheader = mContext.getResources().getString(R.string.h_snf);
            String measuredQtyHeader = mContext.getResources().getString(R.string.h_qty);

            String fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;


            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
            crHead = (fixedLength6 + timeHeader).substring(timeHeader.length())
                    + (fixedLength7 + typeHeader).substring(4)
                    + (fixedLength5 + fatHeader).substring(fatHeader.length())
                    + (fixedLength8 + snfheader).substring(snfheader.length())
                    + (fixedLength4 + measuredQtyHeader);
            return crHead;
        }

        String timeHeader = "Time";
        String typeHeader = "Type";
        String fatHeader = "Fat";
        String snfheader = "SNF";
        String measuredQtyHeader = "Qty";

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {

            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "   "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength6 = "     "; // 5 spaces
            fixedLength4 = "   "; // 3 spaces
            fixedLength7 = "       "; // 6 spaces
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "   "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }


//        if (amcuConfig.getKeyAllowDisplayRate()) {
        crHead = (fixedLength5 + timeHeader).substring(timeHeader.length())
                + (fixedLength5 + typeHeader).substring(typeHeader.length())
                + (fixedLength5 + fatHeader).substring(fatHeader.length())
                + (fixedLength5 + snfheader).substring(snfheader.length())
                + (fixedLength4 + measuredQtyHeader);

        /*} else {
            crHead = (fixedLength5 + fatHeader).substring(fatHeader.length())
                    + (fixedLength5 + snfheader).substring(snfheader.length())
                    + (fixedLength4 + soldQtyHeader).substring(soldQtyHeader.length())
                    + (fixedLength4 + measuredQtyHeader).substring(measuredQtyHeader.length())
                    + (fixedLength8 + amountHeader).substring(amountHeader.length());

        }*/

        return crHead;

    }

    public String incentiveHeader() {
        String crHead = null;

        String incentiveHeader = "Inc";
        String proteinHeader = "Protein";
        String totalAmount = "T.Amt";
        String codeHeader = "Code";
        String qtyHeader = "Qty";
        String timeHeader = "Time";


        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {

            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 6 spaces
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }

        crHead = (fixedLength6 + timeHeader).substring(timeHeader
                .length())
                + (fixedLength5 + codeHeader).substring(codeHeader.length())
                + (fixedLength7 + qtyHeader).substring(qtyHeader.length())
                + (fixedLength8 + proteinHeader).substring(proteinHeader.length())
                + (fixedLength5 + incentiveHeader).substring(incentiveHeader.length())
                + (fixedLength8 + totalAmount).substring(totalAmount.length());
        return crHead;
    }

    // periodic record receipt header

    public String chillingFormatHeader() {

        String codeHeader = "Code";
        String qtyHeader = "Qty";
        String fatHeader = "Fat";
        String snfHeader = "Snf";
        String rateHeader = "Rate";
        String amountHeader = "Amt";
        String timeHeader = "Time";
        String crHead = null;

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            codeHeader = mContext.getResources().getString(R.string.tm_id);
            qtyHeader = mContext.getResources().getString(R.string.tm_qty);
            fatHeader = mContext.getResources().getString(R.string.tm_fat);
            snfHeader = mContext.getResources().getString(R.string.tm_snf);
            rateHeader = mContext.getResources().getString(R.string.tm_rate);
            amountHeader = mContext.getResources().getString(R.string.tm_amt);
            timeHeader = mContext.getResources().getString(R.string.tm_time);


            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces

            if (amcuConfig.getFarmerIdDigit() > 4) {
                crHead = (fixedLength8 + codeHeader).substring(timeHeader.length())
                        + (fixedLength8 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength4 + fatHeader).substring(fatHeader.length())
                        + (fixedLength4 + snfHeader).substring(snfHeader.length())
                        + (fixedLength7 + rateHeader).substring(rateHeader.length())
                        + (fixedLength8 + amountHeader)
                        .substring(amountHeader.length());
            } else {
                crHead = (fixedLength6 + timeHeader).substring(codeHeader
                        .length())
                        + (fixedLength5 + codeHeader).substring(timeHeader.length())
                        + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength4 + fatHeader).substring(fatHeader.length())
                        + (fixedLength4 + snfHeader).substring(snfHeader.length())
                        + (fixedLength7 + rateHeader).substring(rateHeader.length())
                        + (fixedLength8 + amountHeader)
                        .substring(amountHeader.length());
            }
            return crHead;
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            codeHeader = mContext.getResources().getString(R.string.h_center_id);
            qtyHeader = mContext.getResources().getString(R.string.h_qty);
            fatHeader = mContext.getResources().getString(R.string.h_fat);
            snfHeader = mContext.getResources().getString(R.string.h_snf);
            rateHeader = mContext.getResources().getString(R.string.h_rate);
            amountHeader = mContext.getResources().getString(R.string.h_amt);
            timeHeader = mContext.getResources().getString(R.string.h_time);


            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces

            if (amcuConfig.getFarmerIdDigit() > 4) {
                crHead = (fixedLength8 + codeHeader).substring(timeHeader.length())
                        + (fixedLength8 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength4 + fatHeader).substring(fatHeader.length())
                        + (fixedLength4 + snfHeader).substring(snfHeader.length())
                        + (fixedLength7 + rateHeader).substring(rateHeader.length())
                        + (fixedLength8 + amountHeader)
                        .substring(amountHeader.length());
            } else {
                crHead = (fixedLength6 + timeHeader).substring(codeHeader
                        .length())
                        + (fixedLength5 + codeHeader).substring(timeHeader.length())
                        + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength4 + fatHeader).substring(fatHeader.length())
                        + (fixedLength4 + snfHeader).substring(snfHeader.length())
                        + (fixedLength7 + rateHeader).substring(rateHeader.length())
                        + (fixedLength8 + amountHeader)
                        .substring(amountHeader.length());
            }
            return crHead;
        }

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {

            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 6 spaces
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }
        if (amcuConfig.getFarmerIdDigit() > 4) {
            crHead = (fixedLength8 + codeHeader).substring(timeHeader.length())
                    + (fixedLength8 + qtyHeader).substring(qtyHeader.length())
                    + (fixedLength4 + fatHeader).substring(fatHeader.length())
                    + (fixedLength4 + snfHeader).substring(snfHeader.length())
                    + (fixedLength7 + rateHeader).substring(rateHeader.length())
                    + (fixedLength8 + amountHeader)
                    .substring(amountHeader.length());
        } else {
            crHead = (fixedLength6 + timeHeader).substring(codeHeader
                    .length())
                    + (fixedLength5 + codeHeader).substring(timeHeader.length())
                    + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                    + (fixedLength4 + fatHeader).substring(fatHeader.length())
                    + (fixedLength4 + snfHeader).substring(snfHeader.length())
                    + (fixedLength7 + rateHeader).substring(rateHeader.length())
                    + (fixedLength8 + amountHeader)
                    .substring(amountHeader.length());
        }
        return crHead;
    }

    public String formatTotalPeriodicHeader() {

        String codeHeader = "Code";
        String qtyHeader = "T.Qty";
        String rateHeader = "A.Rate";
        String amountHeader = "T.Amt";
        String sigHeader = "Sign";
        String sigNameHeader = "name";
        String crHead = null;


        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8, fixedLength17 = "";
        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            codeHeader = mContext.getResources().getString(R.string.tm_code);
            qtyHeader = mContext.getResources().getString(R.string.tm_total_quantity);
            rateHeader = mContext.getResources().getString(R.string.tm_average_rate);
            amountHeader = mContext.getResources().getString(R.string.tm_amt);
            sigHeader = mContext.getResources().getString(R.string.tm_signature);
            sigNameHeader = mContext.getResources().getString(R.string.tm_name);

            LinkedHashMap<String, String> map = new LinkedHashMap<>();

            map.put(System.nanoTime() + codeHeader, getSpace(3));


            if (amcuConfig.getDisplayNameInReport()) {

                map.put(System.nanoTime() + sigNameHeader, getSpace(3));
                map.put(System.nanoTime() + qtyHeader, getSpace(3));
                map.put(System.nanoTime() + amountHeader, getSpace(3));



            } else {
                map.put(System.nanoTime() + qtyHeader, getSpace(3));
                map.put(System.nanoTime() + rateHeader, getSpace(3));
                map.put(System.nanoTime() + amountHeader, getSpace(4));
                map.put(System.nanoTime() + sigHeader, getSpace(0));


            }
            return getPrintDataFromMap(map);
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            codeHeader = mContext.getResources().getString(R.string.h_code);
            qtyHeader = mContext.getResources().getString(R.string.h_qty);
            rateHeader = mContext.getResources().getString(R.string.h_rate);
            amountHeader = mContext.getResources().getString(R.string.h_amt);
            sigHeader = mContext.getResources().getString(R.string.h_signature);
            sigNameHeader = mContext.getResources().getString(R.string.h_name);


            fixedLength6 = "       "; // 8  spaces
            fixedLength10 = "         ";
            fixedLength7 = "       ";
            fixedLength8 = "        "; // 8 spaces
            fixedLength17 = "           ";


            if (amcuConfig.getDisplayNameInReport()) {
                crHead = (fixedLength6 + codeHeader).substring(codeHeader.length())
                        + (fixedLength7 + sigNameHeader).substring(sigNameHeader.length())
                        + (fixedLength17 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength10 + amountHeader).substring(amountHeader.length());

            } else {
                crHead = (fixedLength6 + codeHeader).substring(codeHeader.length())
                        + (fixedLength17 + qtyHeader).substring(qtyHeader.length())
                        + (fixedLength7 + rateHeader).substring(rateHeader.length())
                        + (fixedLength10 + amountHeader).substring(amountHeader.length())
                        + (fixedLength8 + sigHeader).substring(4);

            }
            return crHead;
        }
        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {

            fixedLength8 = "     "; // 5 spaces
            fixedLength7 = "    ";
            fixedLength10 = "      ";
            fixedLength6 = "    ";
            fixedLength17 = "                ";
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength10 = "         ";
            fixedLength6 = "      "; // 4 spaces
            fixedLength7 = "       "; // 6 spaces
            fixedLength17 = "                ";

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {
            fixedLength6 = "     "; // 6 spaces
            fixedLength10 = "         ";
            fixedLength7 = "       ";
            fixedLength8 = "        "; // 8 spaces
            fixedLength17 = "                ";

        } else {
            fixedLength6 = "     "; // 6 spaces
            fixedLength10 = "         ";
            fixedLength7 = "       ";
            fixedLength8 = "        "; // 8 spaces
            fixedLength17 = "                ";

        }


        if (amcuConfig.getDisplayNameInReport()) {
            crHead = (fixedLength6 + codeHeader).substring(codeHeader.length())
                    + (fixedLength7 + sigNameHeader).substring(sigNameHeader.length())
                    + (fixedLength17 + qtyHeader).substring(qtyHeader.length())
                    + (fixedLength10 + amountHeader).substring(amountHeader.length());

        } else {
            crHead = (fixedLength6 + codeHeader).substring(codeHeader.length())
                    + (fixedLength8 + qtyHeader).substring(qtyHeader.length())
                    + (fixedLength7 + rateHeader).substring(rateHeader.length())
                    + (fixedLength10 + amountHeader).substring(amountHeader.length())
                    + (fixedLength8 + sigHeader).substring(sigHeader.length());

        }

        return crHead;
    }

    public String rightPad(String text, int length) {
        return text = String.format("%1$-" + length + 's', text);
    }

    public String getTotalPeriodicReport(ArrayList<ReportEntity> allTotalPeriodicReport) {


        if (allTotalPeriodicReport == null || allTotalPeriodicReport.size() < 1) {
            strBuild = strBuild + "\n" + formatTotalPeriodicHeader() + "\n";
        }

        if (allTotalPeriodicReport != null && allTotalPeriodicReport.size() > 0) {

            strBuild = strBuild + "\n" + createSeparatorThermal('-') + formatTotalPeriodicHeader() + "\n";
            setDataForTotalPeriodicReports(allTotalPeriodicReport);
        }

        if (allTotalPeriodicReport != null && allTotalPeriodicReport.size() > 1) {
            strBuild = strBuild + "\n" + "  Grand Total: " + "\n"
                    + createSeparatorThermal('-');
            setDataForTotalPeriodicReports(null);
        }
        return strBuild;

    }

    public void getSalesReport() {

        if (allSalesRecords != null && allSalesRecords.size() < 1) {
            strBuild = strBuild + "\n" + formatHeader() + "\n";
        }

        if (allSalesRecords == null) {
            strBuild = strBuild + "\n" + formatHeader() + "\n";
        }

        if (cowSalesRecords != null && cowSalesRecords.size() > 0) {

            strBuild = strBuild + "\n" + "  Milk Type:  COW" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            setDataForSalesPeriodicReports(cowSalesRecords, "COW");
        }
        if (buffSalesRecords != null && buffSalesRecords.size() > 0) {

            strBuild = strBuild + "\n" + "  Milk Type:  BUFFALO" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            setDataForSalesPeriodicReports(buffSalesRecords, "BUFFALO");
        }
        if (mixSalesRecords != null && mixSalesRecords.size() > 0) {

            strBuild = strBuild + "\n" + "  Milk Type:  MIXED" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            setDataForSalesPeriodicReports(mixSalesRecords, "MIXED");
        }
    }

    public void getPeriodicReport() {

        if ((allShiftReportEnt != null && allShiftReportEnt.size() < 2)
                && (allAssCenterRecord != null && allAssCenterRecord.size() < 1)) {
            strBuild = strBuild + "\n" + formatHeader() + "\n";
        }

        if (allShiftReportEnt == null && allAssCenterRecord != null) {
            strBuild = strBuild + "\n" + formatHeader() + "\n";
        }

        if (cowShiftReportEnt != null && cowShiftReportEnt.size() > 0) {

            strBuild = strBuild + "\n" + "  Milk Type:  COW" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            if (amcuConfig.getKeyAllowProteinValue()) {
                setIncentiveDataForPeriodicReports(cowShiftReportEnt, "COW", 1);
            } else {
                setDataForPeriodicReports(cowShiftReportEnt, "COW", 1);
            }

        }
        if (buffShiftReportEnt != null && buffShiftReportEnt.size() > 0) {
            strBuild = strBuild + "\n" + "  Milk Type:  BUFFALO" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            // setDataForPeriodicReports(buffShiftReportEnt, "BUFFALO", 1);
            if (amcuConfig.getKeyAllowProteinValue()) {
                setIncentiveDataForPeriodicReports(buffShiftReportEnt, "BUFFALO", 1);
            } else {
                setDataForPeriodicReports(buffShiftReportEnt, "BUFFALO", 1);
            }

        }
        if (mixShiftReportEnt != null && mixShiftReportEnt.size() > 0) {

            strBuild = strBuild + "\n" + "  Milk Type:  MIXED" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            // setDataForPeriodicReports(mixShiftReportEnt, "MIXED", 1);
            if (amcuConfig.getKeyAllowProteinValue()) {
                setIncentiveDataForPeriodicReports(mixShiftReportEnt, "MIXED", 1);
            } else {
                setDataForPeriodicReports(mixShiftReportEnt, "MIXED", 1);
            }

        }

        if (sampleTestRecords != null && sampleTestRecords.size() > 0) {

            strBuild = strBuild + "\n" + "  Sample records" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            // setDataForPeriodicReports(sampleTestRecords, "TEST", 1);
            if (amcuConfig.getKeyAllowProteinValue()) {
                setIncentiveDataForPeriodicReports(sampleTestRecords, "TEST", 1);
            } else {
                setDataForPeriodicReports(sampleTestRecords, "TEST", 1);
            }

        }

        if (!BuildConfig.FLAVOR.equalsIgnoreCase("hatsun") && checkForTotal()) {
            if (allShiftReportEnt != null && allShiftReportEnt.size() > 1) {
                strBuild = strBuild + "\n" + "  Total Members" + "\n"
                        + createSeparatorThermal('-') + "\n";
                // setDataForPeriodicReports(allShiftReportEnt, null, TYPE_SUMMARY);
                if (amcuConfig.getKeyAllowProteinValue()) {
                    setIncentiveDataForPeriodicReports(allShiftReportEnt, "null", TYPE_SUMMARY);
                } else {
                    setDataForPeriodicReports(allShiftReportEnt, "null", TYPE_SUMMARY);
                }

            }
        }

        if (allAssCenterRecord != null && allAssCenterRecord.size() > 0) {

            strBuild = strBuild + "\n" + "  Associated center reports" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            associateDataForPeriodicReports(allAssCenterRecord);
        }
        if (((cowShiftReportEnt != null && cowShiftReportEnt.size() > 0) || (buffShiftReportEnt != null && buffShiftReportEnt.size() > 0)) && allAssCenterRecord != null && allAssCenterRecord.size() > 0) {
            strBuild = strBuild + "\n" + "Total Summary" + "\n"
                    + createSeparatorThermal('-') + "\n";

            setTotalFarmerAndCenterSummaryReport(allFarmersAndCenters);
        }


    }

    public void getShiftReport() {

        int typeOfReports = 0;

        if (allShiftReportEnt != null && allShiftReportEnt.size() < 1 && allAssCenterRecord == null) {
            strBuild = strBuild + "\n" + formatHeader() + "\n";
        }
        if (allShiftReportEnt == null && allAssCenterRecord == null) {
            strBuild = strBuild + "\n" + formatHeader() + "\n";
        }
        if (cowShiftReportEnt != null && cowShiftReportEnt.size() > 0) {
            typeOfReports = typeOfReports + 1;
            strBuild = strBuild + "\n" + "  Milk Type:  COW" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";

            if (amcuConfig.getKeyAllowProteinValue()) {
                addIncentiveDataInReports(cowShiftReportEnt, "COW", 1);
            } else {
                setDataForReports(cowShiftReportEnt, "COW", 1);
            }


        }
        if (buffShiftReportEnt != null && buffShiftReportEnt.size() > 0) {
            typeOfReports = typeOfReports + 1;
            strBuild = strBuild + "\n" + "  Milk Type:  BUFFALO" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            // setDataForReports(buffShiftReportEnt, "BUFFALO", 1);
            if (amcuConfig.getKeyAllowProteinValue()) {
                addIncentiveDataInReports(buffShiftReportEnt, "BUFFALO", 1);
            } else {
                setDataForReports(buffShiftReportEnt, "BUFFALO", 1);
            }

        }
        if (mixShiftReportEnt != null && mixShiftReportEnt.size() > 0) {
            typeOfReports = typeOfReports + 1;
            strBuild = strBuild + "\n" + "  Milk Type:  MIXED" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n";
            //  setDataForReports(mixShiftReportEnt, "MIXED", 1);
            if (amcuConfig.getKeyAllowProteinValue()) {
                addIncentiveDataInReports(mixShiftReportEnt, "MIXED", 1);
            } else {
                setDataForReports(mixShiftReportEnt, "MIXED", 1);
            }

        }

        if (sampleTestRecords != null && sampleTestRecords.size() > 0) {
            strBuild = strBuild + "\n" + "  Sample records" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n" + "\n";
            // Protein feature was not applicable for Sample records;
        /*    if (amcuConfig.getKeyAllowProteinValue()) {
                addIncentiveDataInReports(sampleTestRecords, "TEST", 1);
            } else {*/
            setDataForReports(sampleTestRecords, "TEST", 1);
            // }

        }

        if (checkForTotal()) {
            if (allShiftReportEnt != null && allShiftReportEnt.size() > 1) {
                strBuild = strBuild + "\n" + "  Total Members" + "\n"
                        + createSeparatorThermal('-') + "\n";
                //    setDataForReports(allShiftReportEnt, null, TYPE_SUMMARY);
                if (amcuConfig.getKeyAllowProteinValue()) {
                    addIncentiveDataInReports(allShiftReportEnt, null, TYPE_SUMMARY);
                } else {
                    setDataForReports(allShiftReportEnt, null, TYPE_SUMMARY);
                }

            }
        }


        if (allAssCenterRecord != null && allAssCenterRecord.size() > 0) {
            typeOfReports = typeOfReports + 1;
            strBuild = strBuild + "\n" + "  Associated center records" + "\n"
                    + createSeparatorThermal('-') + formatHeader() + "\n" + "\n";
            associateSetDataForReports(allAssCenterRecord, null);
        }

        if (typeOfReports > 1) {
            strBuild = strBuild + "\n" + "Total Summary" + "\n"
                    + createSeparatorThermal('-') + "\n";
            setTotalFarmerAndCenterSummaryReport(allFarmersAndCenters);
        }
    }

    public void
    setTotalFarmerAndCenterSummaryReport(ArrayList<ReportEntity> allReportEntity) {

        AverageReportDetail avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(allReportEntity, false);
        strBuild = strBuild + "\n";

        if (avgReportdetail != null) {
            if (amcuConfig.getKeyAllowDisplayRate()) {
                strBuild = strBuild + rightPad("Total Records", 25)
                        + avgReportdetail.totalEntries + "\n"
                        + rightPad("Total Accepted", 25)
                        + avgReportdetail.totalAcceptedEntries + "\n"
                        + rightPad("Total Rejected", 25)
                        + avgReportdetail.totalRejectedEntries + "\n"
                        + rightPad("Average Amount", 25)
                        + avgReportdetail.avgAmount + "\n"
                        + rightPad(getTheUnit(), 25)
                        + avgReportdetail.avgQuantity + "\n"
                        + rightPad("Average Rate", 25)
                        + avgReportdetail.avgRate + "\n"
                        + rightPad("Average Fat", 25)
                        + avgReportdetail.avgFat + "\n"
                        + rightPad("Average SNF", 25)
                        + avgReportdetail.avgSnf + "\n"
                        + rightPad("Total Qty", 25)
                        + avgReportdetail.totalQuantity + "\n"
                        + rightPad("Total Amount", 25)
                        + avgReportdetail.totalAmount + "\n" + "\n";
            } else {
                strBuild = strBuild + rightPad("Total Records", 25)
                        + avgReportdetail.totalMember + "\n"
                        + getReportDetails(avgReportdetail);

            }
        }
        strBuild = strBuild + createSeparatorThermal('-');
    }


    //Set data for chilling reports

    public void getCenterShiftReport() {

        if (allCenterRecords != null && allCenterRecords.size() < 1) {
            strBuild = strBuild + "\n" + chillingFormatHeader() + "\n";
        }

        if (allCenterRecords == null) {
            strBuild = strBuild + "\n" + chillingFormatHeader() + "\n";

        }

        if (allCowCenterRecords != null && allCowCenterRecords.size() > 0) {

            strBuild = strBuild + "\n" + "  Milk Type:  COW" + "\n"
                    + createSeparatorThermal('-') + chillingFormatHeader() + "\n";
            setDataForChillingReports(allCowCenterRecords, "COW");


        }
        if (allBuffCenterRecords != null && allBuffCenterRecords.size() > 0) {

            strBuild = strBuild + "\n" + "  Milk Type:  BUFFALO" + "\n"
                    + createSeparatorThermal('-') + chillingFormatHeader() + "\n";
            setDataForChillingReports(allBuffCenterRecords, "BUFFALO");
        }
        if (allMixCenterRecords != null && allMixCenterRecords.size() > 0) {

            strBuild = strBuild + "\n" + "  Milk Type:  MIXED" + "\n"
                    + createSeparatorThermal('-') + chillingFormatHeader() + "\n";
            setDataForChillingReports(allMixCenterRecords, "MIXED");
        }


        if ((allCowCenterRecords != null && allCowCenterRecords.size() > 0) || (allBuffCenterRecords != null && allBuffCenterRecords.size() > 0) && allAssCenterRecord != null && allAssCenterRecord.size() > 0) {
            strBuild = strBuild + "\n" + "Total Summary" + "\n"
                    + createSeparatorThermal('-') + "\n";


            setTotalFarmerAndCenterSummaryReport(allFarmersAndCenters);
        }


    }

    //All associate center records

    public void setDataForChillingReports(ArrayList<CenterRecordEntity> arrReportsEnt,
                                          String milkType) {


        for (int i = 0; i < arrReportsEnt.size(); i++) {

            strBuild = strBuild + formatChillingRecord(arrReportsEnt.get(i)) + "\n";

            if (i == arrReportsEnt.size() - 1) {

                strBuild = strBuild + createSeparatorThermal('-');

            }

        }

    }

    public void associateSetDataForReports(ArrayList<ReportEntity> arrReportsEnt,
                                           String milkType) {


        for (int i = 0; i < arrReportsEnt.size(); i++) {

            strBuild = strBuild + formatRecord(arrReportsEnt.get(i)) + "\n";
            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n";
                strBuild = strBuild + createSeparatorThermal('-');
                AverageReportDetail averageReportDetail =
                        new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                strBuild = strBuild + "\n";

                if (averageReportDetail != null) {

                    if (amcuConfig.getKeyAllowDisplayRate()) {
                        strBuild = strBuild + rightPad("Total Centers", 25)
                                + averageReportDetail.totalCenter + "\n"
                                + rightPad("Total Accepted", 25)
                                + averageReportDetail.totalAcceptedEntries + "\n"
                                + rightPad("Total Rejected", 25)
                                + averageReportDetail.totalRejectedEntries + "\n"
                                + rightPad("Average Amount", 25)
                                + averageReportDetail.avgAmount + "\n"
                                + rightPad(getTheUnit(), 25)
                                + averageReportDetail.avgQuantity + "\n"
                                + rightPad("Average Rate", 25)
                                + averageReportDetail.avgRate + "\n"
                                + rightPad("Average Fat", 25)
                                + averageReportDetail.avgFat + "\n"
                                + rightPad("Average SNF", 25)
                                + averageReportDetail.avgSnf + "\n"
                                + rightPad("Total Qty", 25)
                                + averageReportDetail.totalQuantity + "\n"
                                + rightPad("Total Amount", 25)
                                + averageReportDetail.totalAmount + "\n" + "\n";

                    } else {
                        strBuild = strBuild + rightPad("Total Centers", 25)
                                + averageReportDetail.totalCenter + "\n"
                                + getReportDetails(averageReportDetail);

                    }
                    strBuild = strBuild + createSeparatorThermal('-');
                }
            }

//			if(i==(arrReportsEnt.size()-1))
//			{
//				strBuild = strBuild + "\n";
//				strBuild = strBuild + createSeparatorThermal('-')+"\n";
//			}
        }

    }

    public String setDataForReports(ArrayList<ReportEntity> arrReportsEnt,
                                    String milkType, int typeSummary) {

        if ((arrReportsEnt == null && milkType == null) || typeSummary == TYPE_SUMMARY) {
            AverageReportDetail avgReportdetail = null;
            if (typeSummary == TYPE_SUMMARY) {
                if (arrReportsEnt != null) {
                    avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                }
            } else {
//                FIXME: Check which avgReportDetail should be used
                avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
            }

            strBuild = strBuild + "\n";

            if (avgReportdetail != null) {
                if (amcuConfig.getKeyAllowDisplayRate()) {
                    strBuild = strBuild + rightPad("Total Records", 25)
                            + avgReportdetail.totalMember + "\n"
                            + rightPad("Total Accepted", 25)
                            + avgReportdetail.totalAcceptedEntries + "\n"
                            + rightPad("Total Rejected", 25)
                            + avgReportdetail.totalRejectedEntries + "\n"
                            + rightPad("Average Amount", 25)
                            + avgReportdetail.avgAmount + "\n"
                            + rightPad(getTheUnit(), 25)
                            + avgReportdetail.avgQuantity + "\n"
                            + rightPad("Average Rate", 25)
                            + avgReportdetail.avgRate + "\n"
                            + rightPad("Average Fat", 25)
                            + avgReportdetail.avgFat + "\n"
                            + rightPad("Average SNF", 25)
                            + avgReportdetail.avgSnf + "\n"
                            + rightPad("Total Qty", 25)
                            + avgReportdetail.totalQuantity + "\n"
                            + rightPad("Total Amount", 25)
                            + avgReportdetail.totalAmount + "\n" + "\n";
                } else {
                    strBuild = strBuild + rightPad("Total Records", 25)
                            + avgReportdetail.totalMember + "\n"
                            + getReportDetails(avgReportdetail);

                }
            }

            strBuild = strBuild + createSeparatorThermal('-');

            return strBuild;
        }

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            strBuild = strBuild + formatRecord(arrReportsEnt.get(i)) + "\n";

            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n";

                AverageReportDetail avgReportdetail = null;

                if (!milkType.equalsIgnoreCase("TEST") &&
                        !arrReportsEnt.get(i).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
                    avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                }

                strBuild = strBuild + "\n";

                if (avgReportdetail != null) {

                    if (amcuConfig.getKeyAllowDisplayRate()) {
                        strBuild = strBuild + rightPad("Total Farmers", 25)
                                + avgReportdetail.totalMember + "\n"
                                + rightPad("Total Accepted", 25)
                                + avgReportdetail.totalAcceptedEntries + "\n"
                                + rightPad("Total Rejected", 25)
                                + avgReportdetail.totalRejectedEntries + "\n"
                                + rightPad("Average Amount", 25)
                                + avgReportdetail.avgAmount + "\n"
                                + rightPad(getTheUnit(), 25)
                                + avgReportdetail.avgQuantity + "\n"
                                + rightPad("Average Rate", 25)
                                + avgReportdetail.avgRate + "\n"
                                + rightPad("Average Fat", 25)
                                + avgReportdetail.avgFat + "\n"
                                + rightPad("Average SNF", 25)
                                + avgReportdetail.avgSnf + "\n"
                                + rightPad("Total Qty", 25)
                                + avgReportdetail.totalQuantity + "\n"
                                + rightPad("Total Amount", 25)
                                + avgReportdetail.totalAmount + "\n" + "\n";
                    } else {
                        strBuild = strBuild + rightPad("Total Farmers", 25)
                                + avgReportdetail.totalMember + "\n"
                                + getReportDetails(avgReportdetail);
                    }
                }

                strBuild = strBuild + createSeparatorThermal('-');

            }

        }

        return strBuild;
    }


    public String addIncentiveDataInReports(ArrayList<ReportEntity> arrReportsEnt,
                                            String milkType, int typeSummary) {
        String inCentiveBuild = "";

        if ((arrReportsEnt == null && milkType == null) || typeSummary == TYPE_SUMMARY) {
            AverageReportDetail avgReportdetail = null;
            if (typeSummary == TYPE_SUMMARY) {
                if (arrReportsEnt != null) {
                    avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                }
            } else {
                avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
            }

            strBuild = strBuild + "\n";

            if (avgReportdetail != null) {
                strBuild = strBuild + rightPad("Total Records", 25)
                        + avgReportdetail.totalMember + "\n"
                        + rightPad("Total Accepted", 25)
                        + avgReportdetail.totalAcceptedEntries + "\n"
                        + rightPad("Total Rejected", 25)
                        + avgReportdetail.totalRejectedEntries + "\n"
                        + rightPad("Average Amount", 25)
                        + avgReportdetail.avgAmount + "\n"
                        + rightPad(getTheUnit(), 25)
                        + avgReportdetail.avgQuantity + "\n"
                        + rightPad("Average Rate", 25)
                        + avgReportdetail.avgRate + "\n"
                        + rightPad("Average Fat", 25)
                        + avgReportdetail.avgFat + "\n"
                        + rightPad("Average SNF", 25)
                        + avgReportdetail.avgSnf + "\n"
                        + rightPad("Total Qty", 25)
                        + avgReportdetail.totalQuantity + "\n"
                        + rightPad("Total Amount", 25)
                        + avgReportdetail.amountWithIncentive + "\n" + "\n";
            }

            strBuild = strBuild + createSeparatorThermal('-');

            return strBuild;
        }

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            strBuild = strBuild + formatRecord(arrReportsEnt.get(i)) + "\n";
            inCentiveBuild = inCentiveBuild + incentiveFormatRecord(arrReportsEnt.get(i)) + "\n";
            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n";
                strBuild = strBuild + incentiveHeader() + "\n";
                strBuild = strBuild + createSeparatorThermal('-');

                strBuild = strBuild + inCentiveBuild + "\n";

                AverageReportDetail avgReportdetail = null;

                if (!milkType.equalsIgnoreCase("TEST") &&
                        !arrReportsEnt.get(i).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
                    avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                }

                strBuild = strBuild + "\n";

                if (avgReportdetail != null) {
                    strBuild = strBuild + rightPad("Total Farmers", 25)
                            + avgReportdetail.totalMember + "\n"
                            + rightPad("Total Accepted", 25)
                            + avgReportdetail.totalAcceptedEntries + "\n"
                            + rightPad("Total Rejected", 25)
                            + avgReportdetail.totalRejectedEntries + "\n"
                            + rightPad("Average Amount", 25)
                            + avgReportdetail.avgAmount + "\n"
                            + rightPad(getTheUnit(), 25)
                            + avgReportdetail.avgQuantity + "\n"
                            + rightPad("Average Rate", 25)
                            + avgReportdetail.avgRate + "\n"
                            + rightPad("Average Fat", 25)
                            + avgReportdetail.avgFat + "\n"
                            + rightPad("Average SNF", 25)
                            + avgReportdetail.avgSnf + "\n"
                            + rightPad("Total Qty", 25)
                            + avgReportdetail.totalQuantity + "\n"
                            + rightPad("Total Amount", 25)
                            + avgReportdetail.totalAmount + "\n" + "\n";
                }

                strBuild = strBuild + createSeparatorThermal('-');

            }

        }

        return strBuild;
    }


    //Set data for total periodic reports

    public void setDataForTotalPeriodicReports(ArrayList<ReportEntity> arrReportsEnt) {

        if (arrReportsEnt == null) {
//FIXME
//            AverageReportDetail avgReportdetail = Util.avgTotalPeriodicDetails;
            AverageReportDetail avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);

            if (avgReportdetail != null) {
                strBuild = strBuild + rightPad("Total Farmers", 25)
                        + avgReportdetail.totalMember + "\n"
                        + rightPad("Average Rate", 25)
                        + avgReportdetail.avgRate + "\n"
                        + rightPad("Total Qty", 25)
                        + avgReportdetail.totalQuantity + "\n"
                        + rightPad("Total Amount", 25)
                        + avgReportdetail.totalAmount + "\n" + "\n";
            }

            strBuild = strBuild + createSeparatorThermal('-') + "\n";
            return;
        }

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            strBuild = strBuild + formatTotalPeriodicRecord(arrReportsEnt.get(i)) + "\n";
        }

        strBuild = strBuild + createSeparatorThermal('-');
    }


    public void setDataForSalesPeriodicReports(ArrayList<SalesRecordEntity> arrReportsEnt,
                                               String milkType) {

        String date = null;

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            if (date == null || !date.equalsIgnoreCase(arrReportsEnt.get(i).postDate)) {
                strBuild = strBuild + "\n" + SmartCCUtil.changeDateFormat(
                        arrReportsEnt.get(i).postDate, "yyyy-MM-dd",
                        "dd-mm-yyyy") + "\n";
                date = arrReportsEnt.get(i).postDate;
            }

            strBuild = strBuild + formatSalesRecord(arrReportsEnt.get(i), i) + "\n";

            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n";
//TODO Write a method in SmartCCUtil to get AverageReportDetail for list of SalesRecordEntity and use it here

                AverageReportDetail avgReportdetail = new AverageReportDetail();

                if (milkType.equalsIgnoreCase("COW")) {
                    avgReportdetail = Util.avgSalesCowReport;
                } else if (milkType.equalsIgnoreCase("BUFFALO")) {
                    avgReportdetail = Util.avgSalesBuffalo;

                } else if (milkType.equalsIgnoreCase("MIXED")) {
                    avgReportdetail = Util.avgSalesMixed;
                }

                strBuild = strBuild + "\n";
                if (avgReportdetail != null) {

                    String firstLine = "";

                    firstLine = rightPad("Total Sales", 25);
                    strBuild = strBuild + firstLine
                            + avgReportdetail.totalMember + "\n"
                            + rightPad("Average Amount", 25)
                            + avgReportdetail.avgAmount + "\n"
                            + rightPad(getTheUnit(), 25)
                            + avgReportdetail.avgQuantity + "\n"
                            + rightPad("Average Rate", 25)
                            + avgReportdetail.avgRate + "\n"
                            + rightPad("Average Fat", 25)
                            + avgReportdetail.avgFat + "\n"
                            + rightPad("Average SNF", 25)
                            + avgReportdetail.avgSnf + "\n"
                            + rightPad("Total Qty", 25)
                            + avgReportdetail.totalQuantity + "\n"
                            + rightPad("Total Amount", 25)
                            + avgReportdetail.totalAmount + "\n" + "\n";
                }

                strBuild = strBuild + createSeparatorThermal('-');

            }

        }
    }


    public void setDataForDispatchReports(ArrayList<DispatchPostEntity> arrReportsEnt) {
        String date = null;
        if (arrReportsEnt == null) {
            strBuild = strBuild + "\n";
            strBuild = strBuild + createSeparatorThermal('-');
            return;
        }
        double qty = 0;
        for (int i = 0; i < arrReportsEnt.size(); i++) {
            if (date == null ||
                    !date.equalsIgnoreCase(arrReportsEnt.get(i).date)) {
                strBuild = strBuild + "\n" + " " + SmartCCUtil.changeDateFormat(arrReportsEnt.get(i).date,
                        "yyyy-MM-dd", "dd-MM-yyyy") + "\n";
                date = arrReportsEnt.get(i).date;
            }
            qty = qty + arrReportsEnt.get(i).quantityParams.weighingScaleData.measuredValue;
            strBuild = strBuild + formatDispatchRecord(arrReportsEnt.get(i)) + "\n";

            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + createSeparatorThermal('-');

            }

        }
    }

    public void setDataForPeriodicReports(ArrayList<ReportEntity> arrReportsEnt,
                                          String milkType, int typeSummary) {

        String date = null;
        if ((arrReportsEnt == null && milkType == null) || typeSummary == TYPE_SUMMARY) {
            AverageReportDetail avgReportdetail = null;
            if (typeSummary == TYPE_SUMMARY) {
                if (arrReportsEnt != null) {
                    avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                }
            } else {
                avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
            }

            strBuild = strBuild + "\n";

            if (avgReportdetail != null) {
                if (amcuConfig.getKeyAllowDisplayRate()) {
                    strBuild = strBuild + rightPad("Total Records", 25)
                            + avgReportdetail.totalMember + "\n"
                            + rightPad("Total Accepted", 25)
                            + avgReportdetail.totalAcceptedEntries + "\n"
                            + rightPad("Total Rejected", 25)
                            + avgReportdetail.totalRejectedEntries + "\n"
                            + rightPad("Average Amount", 25)
                            + avgReportdetail.avgAmount + "\n"
                            + rightPad(getTheUnit(), 25)
                            + avgReportdetail.avgQuantity + "\n"
                            + rightPad("Average Rate", 25)
                            + avgReportdetail.avgRate + "\n"
                            + rightPad("Average Fat", 25)
                            + avgReportdetail.avgFat + "\n"
                            + rightPad("Average SNF", 25)
                            + avgReportdetail.avgSnf + "\n"
                            + rightPad("Total Qty", 25)
                            + avgReportdetail.totalQuantity + "\n"
                            + rightPad("Total Amount", 25)
                            + avgReportdetail.totalAmount + "\n" + "\n";
                } else {
                    strBuild = strBuild + rightPad("Total Records", 25)
                            + avgReportdetail.totalMember + "\n"
                            + getReportDetails(avgReportdetail);
                }
            }

            strBuild = strBuild + createSeparatorThermal('-');
            return;
        }

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            if (date == null || !date.equalsIgnoreCase(arrReportsEnt.get(i).postDate)) {
                strBuild = strBuild + "\n" +
                        SmartCCUtil.changeDateFormat(
                                arrReportsEnt.get(i).postDate, "yyyy-MM-dd",
                                "dd-mm-yyyy") + "\n";
                date = arrReportsEnt.get(i).postDate;
            }

            strBuild = strBuild + formatRecord(arrReportsEnt.get(i)) + "\n";

            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n";

                AverageReportDetail avgReportdetail = null;

                if (!milkType.equalsIgnoreCase("TEST") &&
                        !arrReportsEnt.get(i).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
                    avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                }

                strBuild = strBuild + "\n";
                if (avgReportdetail != null) {

                    String firstLine = "";
                    if (session.getReportType() == Util.PERIODICREPORTINDIVIDUAL) {
                        firstLine = rightPad("Total Records", 25);
                    } else {
                        firstLine = rightPad("Total Farmers", 25);
                    }

                    if (amcuConfig.getKeyAllowDisplayRate()) {
                        strBuild = strBuild + firstLine
                                + avgReportdetail.totalMember + "\n"
                                + rightPad("Total Accepted", 25)
                                + avgReportdetail.totalAcceptedEntries + "\n"
                                + rightPad("Total Rejected", 25)
                                + avgReportdetail.totalRejectedEntries + "\n"
                                + rightPad("Average Amount", 25)
                                + avgReportdetail.avgAmount + "\n"
                                + rightPad(getTheUnit(), 25)
                                + avgReportdetail.avgQuantity + "\n"
                                + rightPad("Average Rate", 25)
                                + avgReportdetail.avgRate + "\n"
                                + rightPad("Average Fat", 25)
                                + avgReportdetail.avgFat + "\n"
                                + rightPad("Average SNF", 25)
                                + avgReportdetail.avgSnf + "\n"
                                + rightPad("Total Qty", 25)
                                + avgReportdetail.totalQuantity + "\n"
                                + rightPad("Total Amount", 25)
                                + avgReportdetail.totalAmount + "\n" + "\n";
                    } else {

                        strBuild = strBuild + firstLine + avgReportdetail.totalMember + "\n"
                                + getReportDetails(avgReportdetail);


                    }
                }

                strBuild = strBuild + createSeparatorThermal('-');

            }

        }
    }


    public void setIncentiveDataForPeriodicReports(ArrayList<ReportEntity> arrReportsEnt,
                                                   String milkType, int typeSummary) {
        String date = null;
        String inCentiveBuild = "";
        if ((arrReportsEnt == null && milkType == null) || typeSummary == TYPE_SUMMARY) {
            AverageReportDetail avgReportdetail = null;
            if (typeSummary == TYPE_SUMMARY) {
                if (arrReportsEnt != null) {
                    DatabaseEntity databaseEntity = new DatabaseEntity(mContext);
                    avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                }
            } else {
                avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
            }

            strBuild = strBuild + "\n";

            if (avgReportdetail != null) {
                strBuild = strBuild + rightPad("Total Records", 25)
                        + avgReportdetail.totalMember + "\n"
                        + rightPad("Total Accepted", 25)
                        + avgReportdetail.totalAcceptedEntries + "\n"
                        + rightPad("Total Rejected", 25)
                        + avgReportdetail.totalRejectedEntries + "\n"
                        + rightPad("Average Amount", 25)
                        + avgReportdetail.avgAmount + "\n"
                        + rightPad(getTheUnit(), 25)
                        + avgReportdetail.avgQuantity + "\n"
                        + rightPad("Average Rate", 25)
                        + avgReportdetail.avgRate + "\n"
                        + rightPad("Average Fat", 25)
                        + avgReportdetail.avgFat + "\n"
                        + rightPad("Average SNF", 25)
                        + avgReportdetail.avgSnf + "\n"
                        + rightPad("Total Qty", 25)
                        + avgReportdetail.totalQuantity + "\n"
                        + rightPad("Total Amount", 25)
                        + avgReportdetail.amountWithIncentive + "\n" + "\n";
            }

            strBuild = strBuild + createSeparatorThermal('-');
            return;
        }

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            if (date == null || !date.equalsIgnoreCase(arrReportsEnt.get(i).postDate)) {
                strBuild = strBuild + "\n" + SmartCCUtil.changeDateFormat(
                        arrReportsEnt.get(i).postDate, "yyyy-MM-dd",
                        "dd-mm-yyyy"
                ) + "\n";
                date = arrReportsEnt.get(i).postDate;
                inCentiveBuild = inCentiveBuild + "\n" + arrReportsEnt.get(i).postDate + "\n";
            }

            strBuild = strBuild + formatRecord(arrReportsEnt.get(i)) + "\n";
            inCentiveBuild = inCentiveBuild + incentiveFormatRecord(arrReportsEnt.get(i)) + "\n";

            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n";

                strBuild = strBuild + incentiveHeader() + "\n";
                strBuild = strBuild + createSeparatorThermal('-');

                strBuild = strBuild + inCentiveBuild + "\n";

                AverageReportDetail avgReportdetail = null;

                if (!milkType.equalsIgnoreCase("TEST")
                        && !arrReportsEnt.get(i).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
                    avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
                }

                strBuild = strBuild + "\n";
                if (avgReportdetail != null) {

                    String firstLine = "";
                    if (session.getReportType() == Util.PERIODICREPORTINDIVIDUAL) {
                        firstLine = rightPad("Total Records", 25);
                    } else {
                        firstLine = rightPad("Total Farmers", 25);
                    }
                    strBuild = strBuild + firstLine
                            + avgReportdetail.totalMember + "\n"
                            + rightPad("Total Accepted", 25)
                            + avgReportdetail.totalAcceptedEntries + "\n"
                            + rightPad("Total Rejected", 25)
                            + avgReportdetail.totalRejectedEntries + "\n"

                            + rightPad("Average Amount", 25)
                            + avgReportdetail.avgAmount + "\n"
                            + rightPad(getTheUnit(), 25)
                            + avgReportdetail.avgQuantity + "\n"
                            + rightPad("Average Rate", 25)
                            + avgReportdetail.avgRate + "\n"
                            + rightPad("Average Fat", 25)
                            + avgReportdetail.avgFat + "\n"
                            + rightPad("Average SNF", 25)
                            + avgReportdetail.avgSnf + "\n"
                            + rightPad("Total Qty", 25)
                            + avgReportdetail.totalQuantity + "\n"
                            + rightPad("Total Amount", 25)
                            + avgReportdetail.totalAmount + "\n" + "\n";
                }

                strBuild = strBuild + createSeparatorThermal('-');

            }

        }
    }


//Associate cnetr periodic reports


    public void associateDataForPeriodicReports(ArrayList<ReportEntity> arrReportsEnt) {

        String date = null;

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            if (date == null || !date.equalsIgnoreCase(arrReportsEnt.get(i).postDate)) {
                strBuild = strBuild + "\n" + SmartCCUtil.changeDateFormat(
                        arrReportsEnt.get(i).postDate, "yyyy-MM-dd",
                        "dd-mm-yyyy") + "\n";
                date = arrReportsEnt.get(i).postDate;
            }

            strBuild = strBuild + formatRecord(arrReportsEnt.get(i)) + "\n";

            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n";
                AverageReportDetail avgReportdetail = averageChillingCenterReport;
                strBuild = strBuild + createSeparatorThermal('-');
                strBuild = strBuild + "\n";
                if (avgReportdetail != null) {
                    String firstLine = "";
                    firstLine = rightPad("Total Centers", 25);
                    if (amcuConfig.getKeyAllowDisplayRate()) {
                        strBuild = strBuild + firstLine
                                + avgReportdetail.totalCenter + "\n"
                                + rightPad("Total Accepted", 25)
                                + avgReportdetail.totalAcceptedEntries + "\n"
                                + rightPad("Total Rejected", 25)
                                + avgReportdetail.totalRejectedEntries + "\n"
                                + rightPad("Average Amount", 25)
                                + avgReportdetail.avgAmount + "\n"
                                + rightPad(getTheUnit(), 25)
                                + avgReportdetail.avgQuantity + "\n"
                                + rightPad("Average Rate", 25)
                                + avgReportdetail.avgRate + "\n"
                                + rightPad("Average Fat", 25)
                                + avgReportdetail.avgFat + "\n"
                                + rightPad("Average SNF", 25)
                                + avgReportdetail.avgSnf + "\n"
                                + rightPad("Total Qty", 25)
                                + avgReportdetail.totalQuantity + "\n"
                                + rightPad("Total Amount", 25)
                                + avgReportdetail.totalAmount + "\n" + "\n";
                        strBuild = strBuild + createSeparatorThermal('-');
                    } else {
                        strBuild = strBuild + firstLine + avgReportdetail.totalMember + "\n"
                                + getReportDetails(avgReportdetail);

                    }
                }
            }

        }
    }


    public String createSeparatorThermal(char c) {
        if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            return String.format("%40s", "").replace(' ', c) + "\n";
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {
            return String.format("%40s", "").replace(' ', c) + "\n";

        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)
                || amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            return String.format("%70s", "").replace(' ', c) + "\n";
        } else {
            return String.format("%32s", "").replace(' ', c) + "\n";
        }

    }

    private String centerAlignWithPaddingThermal(String text) {
        int spaceCount = 0;

        if (text != null && text.length() < 36) {
            spaceCount = (38 - text.length()) / 2;
        }

        text = String.format("%1$-" + (text.length() + spaceCount) + 's', text);
        text = String.format("%1$" + 38 + 's', text);

        return text;
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

    public boolean checkForTotal() {

        if ((cowShiftReportEnt != null && cowShiftReportEnt.size() > 0) &&
                (buffShiftReportEnt != null && buffShiftReportEnt.size() > 0)) {
            return true;
        } else if ((cowShiftReportEnt != null && cowShiftReportEnt.size() > 0) &&
                (mixShiftReportEnt != null && mixShiftReportEnt.size() > 0)) {
            return true;
        } else if ((buffShiftReportEnt != null && buffShiftReportEnt.size() > 0) &&
                (mixShiftReportEnt != null && mixShiftReportEnt.size() > 0)) {
            return true;
        } else {
            return false;
        }
    }

    public String dispatchReceiptFormat(DispatchPostEntity entity) {
        String printData = "", message;
        HashMap<String, String> map = new LinkedHashMap<>();
        HashMap<String, String> tamilValuesMap = new LinkedHashMap<>();

        tamilValuesMap.put(mContext.getResources().getString(R.string.tm_milk_type),
                entity.milkType);
        map.put(mContext.getResources().getString(R.string.h_cattle_type),
                entity.milkType);
        if (entity.qualityParams.milkAnalyser.qualityReadingData.fat == 0.0 &&
                entity.qualityParams.milkAnalyser.qualityReadingData.snf == 0.0) {

            message = "\n" + "Milk Type: " + entity.milkType
                    + "\n" + "No. of Cans: " + entity.numberOfCans
                    + "\n" + "Collected Qty: " + entity.quantityParams.collectedQuantity
                    + "\n" + "Sold Qty: " + entity.quantityParams.soldQuantity
                    + "\n" + "Available Qty: " + entity.quantityParams.availableQuantity
                    + "\n" + "Measured Qty: " + entity.quantityParams.weighingScaleData.measuredValue;
        } else {

            tamilValuesMap.put(mContext.getResources().getString(R.string.tm_fat),
                    String.valueOf(entity.qualityParams.milkAnalyser.qualityReadingData.fat));
            tamilValuesMap.put(mContext.getResources().getString(R.string.tm_snf),
                    String.valueOf(entity.qualityParams.milkAnalyser.qualityReadingData.snf));

            map.put(mContext.getResources().getString(R.string.h_fat),
                    String.valueOf(entity.qualityParams.milkAnalyser.qualityReadingData.fat));
            map.put(mContext.getResources().getString(R.string.h_snf),
                    String.valueOf(entity.qualityParams.milkAnalyser.qualityReadingData.snf));

            message = "\n" + "Milk Type: " + entity.milkType
                    + "\n" + "FAT: " + entity.qualityParams.milkAnalyser.qualityReadingData.fat
                    + "\n" + "SNF: " + entity.qualityParams.milkAnalyser.qualityReadingData.snf
                    + "\n" + "No. of Cans: " + entity.numberOfCans
                    + "\n" + "Collected Qty: " + entity.quantityParams.collectedQuantity
                    + "\n" + "Sold Qty: " + entity.quantityParams.soldQuantity
                    + "\n" + "Available Qty: " + entity.quantityParams.availableQuantity
                    + "\n" + "Measured Qty: " + entity.quantityParams.weighingScaleData.measuredValue;
        }
        map.put(mContext.getResources().getString(R.string.h_num_of_can),
                String.valueOf(entity.numberOfCans));
        map.put(mContext.getResources().getString(R.string.h_collected_qty),
                String.valueOf(entity.quantityParams.collectedQuantity));
        map.put(mContext.getResources().getString(R.string.h_sold_qty),
                String.valueOf(entity.quantityParams.soldQuantity));
        map.put(mContext.getResources().getString(R.string.h_available_qty),
                String.valueOf(entity.quantityParams.availableQuantity));
        map.put(mContext.getResources().getString(R.string.h_measured_qty),
                String.valueOf(entity.quantityParams.weighingScaleData.measuredValue));


        tamilValuesMap.put(mContext.getResources().getString(R.string.tm_num_of_can),
                String.valueOf(entity.numberOfCans));
        tamilValuesMap.put(mContext.getResources().getString(R.string.tm_collected_qty),
                String.valueOf(entity.quantityParams.collectedQuantity));
        tamilValuesMap.put(mContext.getResources().getString(R.string.tm_sold_qty),
                String.valueOf(entity.quantityParams.soldQuantity));
        tamilValuesMap.put(mContext.getResources().getString(R.string.tm_available_qty),
                String.valueOf(entity.quantityParams.availableQuantity));
        tamilValuesMap.put(mContext.getResources().getString(R.string.tm_measured_qty),
                String.valueOf(entity.quantityParams.weighingScaleData.measuredValue));

        String space = "";
        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            space = "    ";
            message = new FormatHindiLocal().formatPrintData(map);
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            space = "    ";
            message = "\n" + new FormatHindiLocal().formatTamilPrintData(tamilValuesMap);
        }

        String time = entity.time;
        time = time.substring(0, time.length() - 3);
        printData = "\n" + space + "Date: " +
                smartCCUtil.changeDateFormat(
                        entity.date, "yyyy-MM-dd",
                        "dd-MMM-yyyy") +
                "/" + SmartCCUtil.getAlternateShift(entity.shift)
                + " Time: " + time + message + "\n";
        return printData;
    }


    public String receiptFormat(Context ctx, ReportEntity reportEntity) {

        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            FormatHindiLocal formatHindiLocal = new FormatHindiLocal();
            return formatHindiLocal.receiptFormat(ctx, reportEntity);
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            FormatHindiLocal formatHindiLocal = new FormatHindiLocal();
            return formatHindiLocal.receiptFormatTamil(ctx, reportEntity);
        }

        String printData;
        String farmerIdText = null;
        String message;

        if (reportEntity.getCollectionType().equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
            farmerIdText = "Sample ID: " + reportEntity.getFarmerId();
        } else if (reportEntity.getCollectionType().equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            farmerIdText = "Center ID: " + reportEntity.getFarmerId();
        } else {
            farmerIdText = "Farmer ID: " + reportEntity.getFarmerId();
        }
        String route = "";
        if (amcuConfig.getAllowRouteInReport() && session.getIsChillingCenter()) {
            String centerRoute = Util.getRouteFromChillingCenter(ctx, session.getFarmerID());
            if (null != centerRoute && !centerRoute.equalsIgnoreCase("") && !centerRoute.equalsIgnoreCase("null")) {
                route = "Route: " + centerRoute + "\n";
            } else {
                route = "";
            }
        }

        String serialNumber = "";


        if (amcuConfig.getAllowSequenceNumberInPrintAndReport() && session.getIsChillingCenter()) {
            serialNumber = "Serial number: " + reportEntity.sampleNumber + "\n";
        }

        String header = "\n" + "name: " + session.getFarmerName() + "\n"
                + farmerIdText + "\n"
                + serialNumber
                + "Cattle Type: " + reportEntity.milkType + "\n"
                + route
                + "QTY: " + reportEntity.getPrintAndReportQuantity() + getTheQuantityUnit() + "\n"
                + "FAT: " + reportEntity.getPrinterFat() + "\n"
                + Util.getSnfOrClrForPrint(reportEntity, ctx);

        //To display amount with protine and incentive
        if (amcuConfig.getKeyAllowProteinValue() && (reportEntity.collectionType != null
                && reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_FARMER))) {
            message = header
                    + "Protein:" + reportEntity.getDisplayProtein() + "\n"
                    + "RATE: " + reportEntity.getPrinterRate() + "\n"
//                        +"Protein Rate:" + reportEntity.incentiveRate+ " Rs" + "\n"
                    + "Amount: " + reportEntity.getAmount() + " Rs" + "\n"
                    + "Incentive:" + reportEntity.incentiveAmount + " Rs" + "\n"
                    + "Total Amount:" + reportEntity.getTotalAmount() + " Rs";
        } else if (!amcuConfig.getBonusEnable()) {
            if (amcuConfig.getKeyAllowDisplayRate()) {
                message = header
                        + "RATE: " + reportEntity.getPrinterRate()
                        + " Rs" + "\n" + "Amount: " + reportEntity.getAmount() + " Rs" + "\n";
            } else {
                message = header
                        + "Total KG-Fat: " + reportEntity.fatKg
                        + "\n" + "Total KG-SNF: " + reportEntity.snfKg + "\n";

            }

        } else if (Double.valueOf(reportEntity.bonus) > 0 &&
                (amcuConfig.getBonusEnableForPrint() && amcuConfig.getBonusEnable())) {
            message = header
                    + "RATE: " + reportEntity.getPrinterRate()
                    + " Rs" + "\n" + "Bonus:  " + reportEntity.bonus + " Rs" + "\n" +
                    "Amount: " + reportEntity.getTotalAmount() + " Rs" + "\n";
        } else {
            String temp_amount = String.valueOf(Util.getAmount(mContext,
                    reportEntity.getTotalAmount(), reportEntity.bonus));
            message = header
                    + "RATE: " + reportEntity.rate
                    + " Rs" + "\n" + "Amount: " + temp_amount + " Rs" + "\n";
        }
        printData = "\n" + "Date: "
                + smartCCUtil.changeDateFormat(
                reportEntity.postDate, "yyyy-MM-dd",
                "dd-MMM-yyyy") + "/"
                + SmartCCUtil.getAlternateShift(reportEntity.postShift)
                + " Time: " + reportEntity.time + message + "\n";

        return printData;

    }


    public String getTheQuantityUnit() {

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


    public String padDate(String date) {
        String[] dateArr = Util.getDateDDMMYY(date, 0).split("-");
        return dateArr[0] + "/" + dateArr[1];
    }


    /**
     * To get the format record for periodic shift
     *
     * @param repEnt
     * @return
     */
    public String periodicFormatRecord(ReportEntity repEnt) {

        // String time = repEnt.time;
        String date = "DD/MM";
        date = padDate(repEnt.postDate);
        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        DecimalFormat decimalFormatAmt = new DecimalFormat("#0.00");
        DecimalFormat decimalFormatWholeNum = new DecimalFormat("#0");

        String qty = String.valueOf(repEnt.getPrinterQuantity());
        String fat = String.valueOf(repEnt.getPrinterFat());
        String snf = String.valueOf(repEnt.getPrinterSnf());
        String fatKg = decimalFormatAmt.format(repEnt.fatKg);
        String snfKg = decimalFormatAmt.format(repEnt.snfKg);

        //To display clr

        if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
            snf = String.valueOf(repEnt.getPrinterSnf());
        }
        String rate = String.valueOf(repEnt.getPrinterRate());
        String amount = String.valueOf(Util.getAmount(mContext, repEnt.getAmountWithBonus(), repEnt.bonus));
        String time = repEnt.time;

        try {
            double dQty = Double.parseDouble(qty);
            if (dQty > 100) {
                qty = decimalFormatWholeNum.format(Double.parseDouble(qty));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "      "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces

            try {
                rate = decimalFormatFS.format(Double.parseDouble(rate));
                amount = decimalFormatFS.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 7 spaces
            try {
                fat = decimalFormatFS.format(Double.parseDouble(fat));
                snf = decimalFormatFS.format(Double.parseDouble(snf));
                rate = decimalFormatAmt.format(Double.parseDouble(rate));
                amount = decimalFormatAmt.format(Double.parseDouble(amount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "      "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();

            map.put(System.nanoTime() + date, getSpace(5));
            map.put(System.nanoTime() + time, getSpace(2));
            map.put(System.nanoTime() + new DecimalFormat("0000.00").format(Double.valueOf(qty)), getSpace(3));
            map.put(System.nanoTime() + new DecimalFormat("00.00").format(Double.valueOf(fat)), getSpace(5));
            map.put(System.nanoTime() + new DecimalFormat("00.00").format(Double.valueOf(snf)), getSpace(3));

            if (amcuConfig.getKeyAllowDisplayRate()) {

                map.put(System.nanoTime() + new DecimalFormat("000.00").format(Double.valueOf(rate)), getSpace(3));
                map.put(System.nanoTime() + new DecimalFormat("00000.00").format(Double.valueOf(amount)), getSpace(0));

            } else {

                map.put(System.nanoTime() + new DecimalFormat("000.00").format(Double.valueOf(fatKg)), getSpace(3));
                map.put(System.nanoTime() + new DecimalFormat("00000.00").format(Double.valueOf(snfKg)), getSpace(0));

            }

            return getPrintDataFromMap(map);
        } else {


            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }
        String collectionRecord = null;

        if (amcuConfig.getKeyAllowDisplayRate()) {

            collectionRecord = (fixedLength4 + date)
                    .substring(date.length())
                    + (fixedLength5 + time).substring(time.length())
                    + (fixedLength5 + qty).substring(qty.length())
                    + (fixedLength4 + fat).substring(fat.length())
                    + (fixedLength4 + snf).substring(snf.length())
                    + (fixedLength7 + rate).substring(rate.length())
                    + (fixedLength8 + amount).substring(amount.length());
        } else {
            collectionRecord = (fixedLength4 + date)
                    .substring(date.length())
                    + (fixedLength5 + time).substring(time.length())
                    + (fixedLength5 + qty).substring(qty.length())
                    + (fixedLength4 + fat).substring(fat.length())
                    + (fixedLength4 + snf).substring(snf.length())
                    + (fixedLength7 + fatKg).substring(fatKg.length())
                    + (fixedLength8 + snfKg).substring(snfKg.length());

        }

        return collectionRecord;
    }

    public String periodicFormatHeader() {

        String codeHeader = "DD/MM";
        String qtyHeader = "Qty";
        String fatHeader = "Fat";
        String snfHeader = "Snf";
        String shiftHeader = "Shift";
        String fatKgHeader = "KG-Fat";
        String snfKgHeader = "KG-SNF";
        //To display clr
        if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
            snfHeader = "Clr";
        }

        String rateHeader = "Rate";
        String amountHeader = "Amt";
        String timeHeader = "Time";

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {

            fixedLength8 = "    "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "   "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 6 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "      "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {

            LinkedHashMap<String, String> map = new LinkedHashMap<>();

            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_dd_mm), getSpace(2));
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_time), getSpace(2));
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_qty), getSpace(5));
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_fat), getSpace(3));
            map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_snf), getSpace(4));

            if (amcuConfig.getKeyAllowDisplayRate()) {
                map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_rate), getSpace(4));
                map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_amt), getSpace(0));
            } else {
                map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_kg_fat), getSpace(1));
                map.put(System.nanoTime() + mContext.getResources().getString(R.string.tm_kg_snf), getSpace(0));

            }

            return getPrintDataFromMap(map);
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "      "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "       "; // 8 spaces
        }
        String crHead = null;


        if (amcuConfig.getKeyAllowDisplayRate()) {
            crHead = (fixedLength4 + codeHeader).substring(codeHeader
                    .length())
                    + (fixedLength5 + timeHeader).substring(shiftHeader.length())
                    + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                    + (fixedLength4 + fatHeader).substring(fatHeader.length())
                    + (fixedLength4 + snfHeader).substring(snfHeader.length())
                    + (fixedLength7 + rateHeader).substring(rateHeader.length())
                    + (fixedLength8 + amountHeader)
                    .substring(amountHeader.length());
        } else {
            crHead = (fixedLength4 + codeHeader).substring(codeHeader
                    .length())
                    + (fixedLength5 + timeHeader).substring(shiftHeader.length())
                    + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                    + (fixedLength4 + fatHeader).substring(fatHeader.length())
                    + (fixedLength4 + snfHeader).substring(snfHeader.length())
                    + (fixedLength7 + snfKgHeader).substring(snfKgHeader.length())
                    + (fixedLength8 + fatKgHeader)
                    .substring(fatKgHeader.length());

        }

        return crHead;
    }


    public String getPeriodicAverageReport(ArrayList<ReportEntity> allReports, boolean allowSample) {
        AverageReportDetail averageReportDetail =
                new SmartCCUtil(mContext).getAverageOfReport(allReports, allowSample);
        String str = null;
        if (amcuConfig.getKeyAllowDisplayRate()) {
            str = "Average Fat/SNF/Rate: " + averageReportDetail.avgFat + "/"
                    + averageReportDetail.avgSnf + "/" + averageReportDetail.avgRate + "\n";
            str = str + "Total Amount: " + averageReportDetail.totalAmount + "\n";
            str = str + "Total Quantity: " + averageReportDetail.totalQuantity + "\n";
        } else {
            str = "Average Fat/SNF/Rate: " + averageReportDetail.avgFat + "/"
                    + averageReportDetail.avgSnf + "/" + averageReportDetail.avgRate + "\n";
            str = str + "Total KG-Fat: " + averageReportDetail.totalFatKg + "\n";
            str = str + "Total KG-SNF: " + averageReportDetail.totalSnfKg + "\n";

        }

        return str;

    }


    //TODO All type of print
    public String getReportFormat(int repType, String header,
                                  String centerId, String dateHeader, ArrayList<ReportEntity> allReportEnt) {
        String strBuild = "";

        if (repType == ReportHintConstant.MEMBER_BILL_SUMM) {
            strBuild = header + "\n"
                    + centerId + "\n" + " "
                    + dateHeader + "\n";
            strBuild = strBuild + " MEM " + "  QTY  " + "  Rate  "
                    + "  Amount " + "  Sign  " + "\n";
            for (int i = 0; i < allReportEnt.size(); i++) {
                strBuild = strBuild + allReportEnt.get(i).farmerId + "  "
                        + allReportEnt.get(i).getPrinterQuantity() + " "
                        + allReportEnt.get(i).getPrinterRate() + " "
                        + allReportEnt.get(i).getPrinterAmount() + " " + "        "
                        + "\n";

            }
        } else if (repType == ReportHintConstant.MEMBER_BILL_REG) {
            strBuild = centerAlignWithPaddingThermal(header) + "\n"
                    + mPrinterManager.getCenterDetails() + "\n" + " "
                    + mPrinterManager.centerAlignWithPaddingThermal(dateHeader) + "\n";

            strBuild = strBuild + "    Date    " + " M " + " FAT " + " SNF "
                    + " QTY " + " Rate " + " Amt " + "\n";

            for (int i = 0; i < allReportEnt.size(); i++) {
                strBuild = strBuild + "  " + allReportEnt.get(i).postDate + " "
                        + allReportEnt.get(i).milkType.charAt(0) + " "
                        + allReportEnt.get(i).getPrinterFat() + " "
                        + allReportEnt.get(i).getPrinterSnf() + " "
                        + allReportEnt.get(i).getPrinterQuantity() + " "
                        + allReportEnt.get(i).getPrinterRate() + " "
                        + allReportEnt.get(i).getPrinterAmount() + "\n";

                if (i == allReportEnt.size() - 1) {
                    strBuild = strBuild + "\n" + "\n" + "\n" + "\n" + "\n"
                            + "\n";

                }

            }
        } else if (repType == ReportHintConstant.DAILY_SHIFT) {

        } else if (repType == ReportHintConstant.DAIRY_REPORT) {

        } else if (repType == ReportHintConstant.PERIODIC) {

        } else if (repType == ReportHintConstant.PERIODIC_SHIFT) {

        } else if (repType == ReportHintConstant.PERIODIC_TOTAL) {
            strBuild = getTotalPeriodicReport(allReportEnt);
        }


        return strBuild;

    }

    public String incentivePeriodicFormatHeader() {

        String codeHeader = "DD/MM";
        String qtyHeader = "Qty";
        String shiftHeader = "Shift";
        String proteinHeader = "Protein";
        String incentiveHeader = "Inc";
        String totalAmount = "T.Amt";

        String timeHeader = "Time";

        String fixedLength10, fixedLength7, fixedLength5, fixedLength6, fixedLength4, fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {

            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 6 spaces
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {
            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "      "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        } else {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }
        String crHead = null;

        crHead = (fixedLength4 + codeHeader).substring(codeHeader
                .length())
                + (fixedLength5 + timeHeader).substring(shiftHeader.length())
                + (fixedLength5 + qtyHeader).substring(qtyHeader.length())
                + (fixedLength8 + proteinHeader).substring(proteinHeader.length())
                + (fixedLength4 + incentiveHeader).substring(incentiveHeader.length())
                + (fixedLength8 + totalAmount)
                .substring(totalAmount.length());


        return crHead;
    }

    public String incentivePeriodicFormatRecord(ReportEntity repEnt) {

        // String time = repEnt.time;
        String date = "DD/MM";
        date = padDate(repEnt.postDate);

        String qty = String.valueOf(repEnt.getPrinterQuantity());
        String protein = String.valueOf(repEnt.protein);
        String inCentiveAmount = String.valueOf(repEnt.incentiveAmount);
        String totalAmount = String.valueOf(repEnt.getTotalAmount());
        //To display clr
        if (session.getMCCStatus()) {
            if (!session.getRecordStatusComplete()) {
                protein = "NA";
                inCentiveAmount = "NA";

            }
        }

        String rate = String.valueOf(repEnt.getPrinterRate());
        String time = repEnt.time;
        DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
        DecimalFormat decimalFormatAmt = new DecimalFormat("#0.00");
        DecimalFormat decimalFormatWholeNum = new DecimalFormat("#0");

        try {
            double dQty = Double.parseDouble(qty);
            if (dQty > 100) {
                qty = decimalFormatWholeNum.format(Double.parseDouble(qty));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String fixedLength10, fixedLength7, fixedLength5 = "", fixedLength6, fixedLength4 = "", fixedLength8;

        if (amcuConfig.getPrinter().equalsIgnoreCase("THERMAL CHADDHA")) {
            fixedLength8 = "     "; // 5 spaces
            fixedLength5 = "     "; // 5 spaces
            fixedLength4 = "    "; // 4 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "     "; // 5 spaces

            try {
                rate = decimalFormatFS.format(Double.parseDouble(rate));
                inCentiveAmount = decimalFormatAmt.format(Double.parseDouble(inCentiveAmount));
                totalAmount = decimalFormatAmt.format(Double.parseDouble(totalAmount));

            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (amcuConfig.getPrinter().equalsIgnoreCase("SMARTMOO")) {
            fixedLength8 = "        "; // 8 spaces
            fixedLength5 = "      "; // 6 spaces
            fixedLength4 = "     "; // 5 spaces
            fixedLength6 = "    "; // 4 spaces
            fixedLength7 = "       "; // 7 spaces
            try {
                inCentiveAmount = decimalFormatAmt.format(Double.parseDouble(inCentiveAmount));
                totalAmount = decimalFormatAmt.format(Double.parseDouble(totalAmount));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {
            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "      "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        } else

        {

            fixedLength5 = "      "; // 5 spaces
            fixedLength4 = "    "; // 5 spaces
            fixedLength6 = "      "; // 6 spaces
            fixedLength7 = "      "; // 6 spaces
            fixedLength8 = "        "; // 8 spaces
        }
        String collectionRecord = null;

        collectionRecord = (fixedLength4 + date)
                .substring(date.length())
                + (fixedLength5 + time).substring(time.length())
                + (fixedLength5 + qty).substring(qty.length())
                + (fixedLength7 + protein).substring(protein.length())
                + (fixedLength7 + inCentiveAmount).substring(inCentiveAmount.length())
                + (fixedLength8 + totalAmount).substring(totalAmount.length());


        return collectionRecord;
    }

    public String getReportDetails(AverageReportDetail avgReportdetail) {
        strBuild = rightPad("Total Accepted", 25)
                + avgReportdetail.totalAcceptedEntries + "\n"
                + rightPad("Total Rejected", 25)
                + avgReportdetail.totalRejectedEntries + "\n"
                + rightPad(getTheUnit(), 25)
                + avgReportdetail.avgQuantity + "\n"
                + rightPad("Average Fat", 25)
                + avgReportdetail.avgFat + "\n"
                + rightPad("Average SNF", 25)
                + avgReportdetail.avgSnf + "\n"
                + rightPad("Total KG-Fat", 25)
                + avgReportdetail.totalFatKg + "\n"
                + rightPad("Total KG-SNF", 25)
                + avgReportdetail.totalSnfKg + "\n"
                + rightPad("Total Qty", 25)
                + avgReportdetail.totalQuantity + "\n\n";

        return strBuild;
    }


    private String getSpace(int size) {
        StringBuilder space = new StringBuilder("");
        for (int i = 0; i < size; i++) {
            space.append(" ");
        }

        return space.toString();
    }

    private String getPrintDataFromMap(LinkedHashMap<String, String> map) {
        StringBuilder printData = new StringBuilder("");
        int count = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey().substring(String.valueOf(System.nanoTime()).length(), entry.getKey().length());
            if (count == 0) {
                printData.append(getSpace(1) + key + entry.getValue());

            } else {
                printData.append(key + entry.getValue());

            }
            count = count + 1;
        }
        return printData.toString();

    }


    private String addSuffixSpaces(String name, int length) {
        StringBuilder returnName = new StringBuilder(name);


        for (int i = name.length(); i < length; i++) {
            returnName = returnName.append(" ");
        }

        return returnName.toString();
    }
}

