package com.devapp.devmain.user;


import android.content.Context;

import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FormatHindiLocal {


    private AmcuConfig amcuConfig;
    private SessionManager sessionManager;
    private SmartCCUtil smartCCUtil;
    private FormatPrintRecords formatPrintRecords;
    private Context mContext;

    private String dateShift = "";


    public FormatHindiLocal() {
        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(DevAppApplication.getAmcuContext());
        smartCCUtil = new SmartCCUtil(DevAppApplication.getAmcuContext());
        formatPrintRecords = new FormatPrintRecords(DevAppApplication.getAmcuContext());
        mContext = DevAppApplication.getAmcuContext();

    }


    public String receiptFormatTamil(Context ctx, ReportEntity reportEntity) {
        String printData;
        String farmerIdText = null;
        String message;
        HashMap<String, String> map = new LinkedHashMap<>();

        if (reportEntity.getCollectionType().equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
            farmerIdText = ": " + reportEntity.getFarmerId();
        } else if (reportEntity.getCollectionType().equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            farmerIdText = "Center ID: " + reportEntity.getFarmerId();
            map.put(mContext.getResources().getString(R.string.tm_center_id)
                    , reportEntity.getFarmerId());
        } else {
            farmerIdText = "Farmer ID: " + reportEntity.getFarmerId();
            map.put(mContext.getResources().getString(R.string.tm_member_id)
                    , reportEntity.getFarmerId());

            map.put("CollectionType", reportEntity.getCollectionType());
        }
        String route = "";
        if (amcuConfig.getAllowRouteInReport() && sessionManager.getIsChillingCenter()) {
            String centerRoute = Util.getRouteFromChillingCenter(ctx, sessionManager.getFarmerID());
            if (null != centerRoute && !centerRoute.equalsIgnoreCase("") && !centerRoute.equalsIgnoreCase("null")) {
                route = "Route: " + centerRoute + "\n";

            } else {
                route = "";
            }

            map.put(mContext.getResources().getString(R.string.tm_route)
                    , reportEntity.getFarmerId());
        }

        String serialNumber = "";

        if (amcuConfig.getAllowSequenceNumberInPrintAndReport() && sessionManager.getIsChillingCenter()) {
            serialNumber = "Serial number: " + reportEntity.sampleNumber + "\n";
            map.put(mContext.getResources().getString(R.string.tm_serial_number),
                    String.valueOf(reportEntity.sampleNumber));
        }
        map.put(mContext.getResources().getString(R.string.tm_name),
                sessionManager.getFarmerName());
        map.put(mContext.getResources().getString(R.string.tm_milk_type),
                reportEntity.milkType);
        map.put(mContext.getResources().getString(R.string.tm_qty),
                String.valueOf(reportEntity.getPrinterQuantity()));
        map.put(mContext.getResources().getString(R.string.tm_fat),
                String.valueOf(reportEntity.getPrinterFat()));
        map.put(mContext.getResources().getString(R.string.tm_snf),
                String.valueOf(reportEntity.getPrinterSnf()));

//        map.put(mContext.getResources().getString(R.string.h_protein),
//                reportEntity.protein);
        map.put(mContext.getResources().getString(R.string.tm_rate),
                reportEntity.rate + " " +
                        mContext.getResources().getString(R.string.tm_rupee_unit));
        map.put(mContext.getResources().getString(R.string.tm_amt),
                reportEntity.getPrinterAmount() + " " +
                        mContext.getResources().getString(R.string.tm_rupee_unit));

        if (amcuConfig.getKeyAllowProteinValue() && (reportEntity.collectionType != null
                && reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_FARMER))) {

            map.put(mContext.getResources().getString(R.string.tm_incentive),
                    reportEntity.getIncentiveAmount() + " " +
                            mContext.getResources().getString(R.string.tm_rupee_unit));
            map.put(mContext.getResources().getString(R.string.tm_total_amount),
                    reportEntity.amount + " " +
                            mContext.getResources().getString(R.string.tm_rupee_unit));
        }
        dateShift = "\n" + "Date: "
                + smartCCUtil.changeDateFormat(
                reportEntity.postDate, "yyyy-MM-dd",
                "dd-MMM-yyyy") + "/"
                + SmartCCUtil.getAlternateShift(reportEntity.postShift)
                + " Time: " + reportEntity.time + "\n";

        return formatTamilPrintData(map);

    }


    public String receiptFormat(Context ctx, ReportEntity reportEntity) {
        String printData;
        String farmerIdText = null;
        String message;
        HashMap<String, String> map = new LinkedHashMap<>();

        if (reportEntity.getCollectionType().equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
            farmerIdText = ": " + reportEntity.getFarmerId();
        } else if (reportEntity.getCollectionType().equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            farmerIdText = "Center ID: " + reportEntity.getFarmerId();
            map.put(mContext.getResources().getString(R.string.h_center_id)
                    , reportEntity.getFarmerId());
        } else {
            farmerIdText = "Farmer ID: " + reportEntity.getFarmerId();
            map.put(mContext.getResources().getString(R.string.h_member_id)
                    , reportEntity.getFarmerId());

            map.put("CollectionType", reportEntity.getCollectionType());
        }
        String route = "";
        if (amcuConfig.getAllowRouteInReport() && sessionManager.getIsChillingCenter()) {
            String centerRoute = Util.getRouteFromChillingCenter(ctx, sessionManager.getFarmerID());
            if (null != centerRoute && !centerRoute.equalsIgnoreCase("") && !centerRoute.equalsIgnoreCase("null")) {
                route = "Route: " + centerRoute + "\n";

            } else {
                route = "";
            }

            map.put(mContext.getResources().getString(R.string.h_route)
                    , reportEntity.getFarmerId());
        }

        String serialNumber = "";

        if (amcuConfig.getAllowSequenceNumberInPrintAndReport() && sessionManager.getIsChillingCenter()) {
            serialNumber = "Serial number: " + reportEntity.sampleNumber + "\n";
            map.put(mContext.getResources().getString(R.string.h_serial_number),
                    String.valueOf(reportEntity.sampleNumber));
        }
        map.put(mContext.getResources().getString(R.string.h_name),
                sessionManager.getFarmerName());
        map.put(mContext.getResources().getString(R.string.h_milk_type),
                reportEntity.milkType);
        map.put(mContext.getResources().getString(R.string.h_qty),
                String.valueOf(reportEntity.getPrinterQuantity()));
        map.put(mContext.getResources().getString(R.string.h_fat),
                String.valueOf(reportEntity.getPrinterFat()));
        map.put(mContext.getResources().getString(R.string.h_snf),
                String.valueOf(reportEntity.getPrinterSnf()));

        String header = "\n" + "name: " + sessionManager.getFarmerName() + "\n"
                + farmerIdText + "\n"
                + serialNumber
                + "Cattle Type: " + reportEntity.milkType + "\n"
                + route
                + "QTY: " + reportEntity.getQuantity() + formatPrintRecords.getTheQuantityUnit() + "\n"
                + "FAT: " + reportEntity.getFat() + "\n"
                + Util.getSnfOrClrForPrint(reportEntity, ctx);

//        map.put(mContext.getResources().getString(R.string.h_protein),
//                reportEntity.protein);
        map.put(mContext.getResources().getString(R.string.h_rate),
                reportEntity.getPrinterRate() + " " +
                        mContext.getResources().getString(R.string.h_rate));
        map.put(mContext.getResources().getString(R.string.h_amt),
                reportEntity.getPrinterAmount() + " " +
                        mContext.getResources().getString(R.string.h_rate));

        if (amcuConfig.getKeyAllowProteinValue() && (reportEntity.collectionType != null
                && reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_FARMER))) {
            message = header
                    + "Protein:" + reportEntity.getProtein() + "\n"
                    + "RATE: " + reportEntity.getRate() + "\n"
//                        +"Protein Rate:" + reportEntity.incentiveRate+ " Rs" + "\n"
                    + "Amount: " + reportEntity.getAmount() + " Rs" + "\n"
                    + "Incentive:" + reportEntity.incentiveAmount + " Rs" + "\n"
                    + "Total Amount:" + reportEntity.getTotalAmount() + " Rs";
            map.put(mContext.getResources().getString(R.string.h_incentive),
                    reportEntity.incentiveAmount + " " +
                            mContext.getResources().getString(R.string.h_rate));
            map.put(mContext.getResources().getString(R.string.h_total_amount),
                    reportEntity.amount + " " +
                            mContext.getResources().getString(R.string.h_rate));
        } else if (!amcuConfig.getBonusEnable()) {
            if (amcuConfig.getKeyAllowDisplayRate()) {
                message = header
                        + "RATE: " + reportEntity.getRate()
                        + " Rs" + "\n" + "Amount: " + reportEntity.getAmount() + " Rs" + "\n";
            } else {
                message = header
                        + "Total KG-Fat: " + reportEntity.fatKg
                        + "\n" + "Total KG-SNF: " + reportEntity.snfKg + "\n";

            }

        } else if (Double.valueOf(reportEntity.bonus) > 0 &&
                (amcuConfig.getBonusEnableForPrint() && amcuConfig.getBonusEnable())) {
            message = header
                    + "RATE: " + reportEntity.getRate()
                    + " Rs" + "\n" + "Bonus:  " + reportEntity.bonus + " Rs" + "\n" +
                    "Amount: " + reportEntity.getTotalAmount() + " Rs" + "\n";
        } else {
            String temp_amount = String.valueOf(Util.getAmount(DevAppApplication.getAmcuContext(),
                    reportEntity.getTotalAmount(), reportEntity.bonus));
            message = header
                    + "RATE: " + reportEntity.rate
                    + " Rs" + "\n" + "Amount: " + temp_amount + " Rs" + "\n";
        }
        dateShift = "\n" + "Date: "
                + smartCCUtil.changeDateFormat(
                reportEntity.postDate, "yyyy-MM-dd",
                "dd-MMM-yyyy") + "/"
                + SmartCCUtil.getAlternateShift(reportEntity.postShift)
                + " Time: " + reportEntity.time + "\n";

        return formatPrintData(map);

    }


    public String formatPrintData(Map<String, String> map) {

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> mapEntry : map.entrySet()) {

            if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_cattle_type)) ||
                    mapEntry.getKey().equalsIgnoreCase(
                            mContext.getResources().getString(R.string.h_milk_type))
                    ) {
                if (mapEntry.getValue().equalsIgnoreCase("COW")) {
                    mapEntry.setValue(mContext.getResources().getString(R.string.h_cow));
                } else {
                    mapEntry.setValue(mContext.getResources().getString(R.string.h_buffalo));
                }
            }


            if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_member_id))) {
                stringBuilder.append(String.format("%4s %-14s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_name))) {
                stringBuilder.append(String.format("%4s %-13s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_cattle_type))) {
                stringBuilder.append(String.format("%4s %-15s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_qty))) {
                stringBuilder.append(String.format("%4s %-16s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_milk_type))) {
                stringBuilder.append(String.format("%4s %-16s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_fat))) {
                stringBuilder.append(String.format("%4s %-13s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_snf))) {
                stringBuilder.append(String.format("%4s %-12s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_protein))) {
                stringBuilder.append(String.format("%4s %-16s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_rate))) {
                stringBuilder.append(String.format("%4s %-13s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_amt))) {
                stringBuilder.append(String.format("%4s %-13s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_available_qty))) {
                stringBuilder.append(String.format("%4s %-17s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_sold_qty))) {
                stringBuilder.append(String.format("%4s %-17s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_collected_qty))) {
                stringBuilder.append(String.format("%4s %-17s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.h_num_of_can))) {
                stringBuilder.append(String.format("%4s %-16s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase("CollectionType")) {

            } else {
                stringBuilder.append(String.format("%4s %-13s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            }

        }
        String value = stringBuilder.toString();
        return value;
    }


    public String formatTamilPrintData(Map<String, String> map) {

        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> mapEntry : map.entrySet()) {

            if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_milk_type)) ||
                    mapEntry.getKey().equalsIgnoreCase(
                            mContext.getResources().getString(R.string.tm_cattle_type))
                    ) {
                if (mapEntry.getValue().equalsIgnoreCase("COW")) {
                    mapEntry.setValue(mContext.getResources().getString(R.string.tm_cow));
                } else if (mapEntry.getValue().equalsIgnoreCase("BUFFALO")) {
                    mapEntry.setValue(mContext.getResources().getString(R.string.tm_buffalo));
                } else if (mapEntry.getValue().equalsIgnoreCase("MIXED")) {
                    mapEntry.setValue(mContext.getResources().getString(R.string.tm_mixed));
                }
            }


            if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_member_id))) {
                stringBuilder.append(String.format("%4s %-15s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_name))) {
                stringBuilder.append(String.format("%4s %-13s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_milk_type))) {
                stringBuilder.append(String.format("%4s %-12s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_qty))) {
                stringBuilder.append(String.format("%4s %-12s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_milk_type))) {
                stringBuilder.append(String.format("%4s %-14s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_fat))) {
                stringBuilder.append(String.format("%4s %-14s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_snf))) {
                stringBuilder.append(String.format("%4s %-12s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_protein))) {
                stringBuilder.append(String.format("%4s %-16s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_rate))) {
                stringBuilder.append(String.format("%4s %-13s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_amt))) {
                stringBuilder.append(String.format("%4s %-12s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_available_qty))) {
                stringBuilder.append(String.format("%4s %-17s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_sold_qty))) {
                stringBuilder.append(String.format("%4s %-17s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_collected_qty))) {
                stringBuilder.append(String.format("%4s %-17s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase(
                    mContext.getResources().getString(R.string.tm_num_of_can))) {
                stringBuilder.append(String.format("%4s %-16s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            } else if (mapEntry.getKey().equalsIgnoreCase("CollectionType")) {

            } else {
                stringBuilder.append(String.format("%4s %-13s : %s\n", "", mapEntry.getKey(), mapEntry.getValue()));

            }

        }
        String value = stringBuilder.toString();
        return value;
    }


}
