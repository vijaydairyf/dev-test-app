package com.devapp.devmain.user;

import android.content.Context;

import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.util.AppConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

import static org.apache.commons.lang3.StringUtils.rightPad;

/**
 * Created by u_pendra on 4/8/17.
 */

public class FormatPrintHelper {

    FormatPrintRecords formatPrintRecords;
    String strBuild = "";
    private Context mContext;
    private AmcuConfig amcuConfig;
    private SessionManager sessionManager;
    private OnFormatCompleteListener mFormatCompleteListener;
    private Queue<String> printerQueue;

    public FormatPrintHelper(Context context) {
        this.mContext = context;
        this.amcuConfig = AmcuConfig.getInstance();
        this.formatPrintRecords = new FormatPrintRecords(mContext);
        sessionManager = new SessionManager(mContext);
    }

    public FormatPrintHelper(Context context, Queue<String> queue) {
        this.mContext = context;
        this.amcuConfig = AmcuConfig.getInstance();
        this.formatPrintRecords = new FormatPrintRecords(mContext);
        sessionManager = new SessionManager(mContext);
        this.printerQueue = queue;
    }


    public String getShiftReport(ArrayList<ReportEntity> allReportEntities) {
        strBuild = "";
        String strSample = "";
        Map<String, ArrayList<ReportEntity>> hashList = new ConcurrentHashMap<>();

        if (allReportEntities == null || allReportEntities.size() == 0) {
            return strBuild;

        }

        for (ReportEntity repEnt : allReportEntities) {

            if (hashList.keySet().contains(repEnt.milkType)) {

                for (Map.Entry<String, ArrayList<ReportEntity>> entry : hashList.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(repEnt.milkType)) {
                        entry.getValue().add(repEnt);
                        hashList.put(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                ArrayList<ReportEntity> subList = new ArrayList<>();
                subList.add(repEnt);
                hashList.put(repEnt.milkType, subList);
            }
        }


        if (hashList.size() == 1) {

            if (hashList.keySet().contains(CattleType.TEST)) {
                strBuild = strBuild + "\n" + "  Sample records" + "\n";

            } else {
                strBuild = strBuild + "\n" + "  Milk Type: " + allReportEntities.get(0).milkType + "\n";
            }
            strBuild = strBuild
                    + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
            if (amcuConfig.getKeyAllowProteinValue() &&
                    (!allReportEntities.get(0).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SALES)
                            && !allReportEntities.get(0).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)
                            && !allReportEntities.get(0).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)
                            && !allReportEntities.get(0).collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC))) {
                strBuild = strBuild + setIncentiveDataForReports(allReportEntities,
                        allReportEntities.get(0).milkType);
            } else {
                strBuild = strBuild + setDataForReports(allReportEntities,
                        allReportEntities.get(0).milkType);

            }

        } else if (hashList.size() == 2 && hashList.keySet().contains(CattleType.TEST)) {
            Map.Entry<String, ArrayList<ReportEntity>> entryTest = null;

            for (Map.Entry<String, ArrayList<ReportEntity>> entry : hashList.entrySet()) {
                if (!entry.getKey().equalsIgnoreCase(CattleType.TEST)) {
                    strBuild = strBuild + "\n" + "  Milk Type: " + entry.getKey() + "\n";
                    strBuild = strBuild
                            + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
                    if (amcuConfig.getKeyAllowProteinValue()) {
                        strBuild = strBuild + setIncentiveDataForReports(entry.getValue(), entry.getKey());

                    } else {
                        strBuild = strBuild + setDataForReports(entry.getValue(), entry.getKey());

                    }
                } else {
                    entryTest = entry;
                }
            }

            strBuild = strBuild + "\n" + "  Sample records" + "\n";
            strBuild = strBuild
                    + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
            //       strBuild = strBuild + setDataForReports(entryTest.getValue(), entryTest.getKey());
            if (amcuConfig.getKeyAllowProteinValue()) {
                strBuild = strBuild + setIncentiveDataForReports(entryTest.getValue(), entryTest.getKey());

            } else {
                strBuild = strBuild + setDataForReports(entryTest.getValue(), entryTest.getKey());

            }

        } else {
            for (Map.Entry<String, ArrayList<ReportEntity>> entry : hashList.entrySet()) {

                if (entry.getKey().equalsIgnoreCase(CattleType.COW)) {
                    strBuild = strBuild + "\n" + "  Milk Type: " + entry.getKey() + "\n";
                    strBuild = strBuild
                            + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
                    if (amcuConfig.getKeyAllowProteinValue()) {
                        strBuild = strBuild + setIncentiveDataForReports(entry.getValue(), entry.getKey());

                    } else {
                        strBuild = strBuild + setDataForReports(entry.getValue(), entry.getKey());

                    }
                    //  strBuild = strBuild + setDataForReports(entry.getValue(), entry.getKey());
                } else if (entry.getKey().equalsIgnoreCase(CattleType.BUFFALO)) {
                    strBuild = strBuild + "\n" + "  Milk Type: " + entry.getKey() + "\n";
                    strBuild = strBuild
                            + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
                    if (amcuConfig.getKeyAllowProteinValue()) {
                        strBuild = strBuild + setIncentiveDataForReports(entry.getValue(), entry.getKey());

                    } else {
                        strBuild = strBuild + setDataForReports(entry.getValue(), entry.getKey());

                    }
                    //   strBuild = strBuild + setDataForReports(entry.getValue(), entry.getKey());
                } else if (entry.getKey().equalsIgnoreCase(CattleType.MIXED)) {
                    strBuild = strBuild + "\n" + "  Milk Type: " + entry.getKey() + "\n";
                    strBuild = strBuild
                            + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
                    if (amcuConfig.getKeyAllowProteinValue()) {
                        strBuild = strBuild + setIncentiveDataForReports(entry.getValue(), entry.getKey());

                    } else {
                        strBuild = strBuild + setDataForReports(entry.getValue(), entry.getKey());

                    }
                    //   strBuild = strBuild + setDataForReports(entry.getValue(), entry.getKey());
                } else if (entry.getKey().equalsIgnoreCase(CattleType.TEST)) {
                    strSample = strSample + "\n" + "  Sample records" + "\n";
                    strSample = strSample
                            + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
                    if (amcuConfig.getKeyAllowProteinValue()) {
                        strSample = strSample + setIncentiveDataForReports(entry.getValue(), entry.getKey());

                    } else {
                        strSample = strSample + setDataForReports(entry.getValue(), entry.getKey());

                    }
                    // strSample = strSample + setDataForReports(entry.getValue(), entry.getKey());
                }


            }

            strBuild = strBuild + strSample;
            strBuild = strBuild + "\n" + "  Total Summary" + "\n";

            strBuild = strBuild
                    + formatPrintRecords.createSeparatorThermal('-') + "\n";

            if (amcuConfig.getKeyAllowProteinValue()) {
                strBuild = strBuild + setIncentiveDataForReports(allReportEntities, null);

            } else {
                strBuild = strBuild + setDataForReports(allReportEntities, null);

            }
            // strBuild = strBuild + setDataForReports(allReportEntities, null);
        }


        return strBuild;
    }


    public String setDataForReports(ArrayList<ReportEntity> arrReportsEnt,
                                    String milkType) {
        String strBuild = "";
        AverageReportDetail avgReportdetail =
                new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
        if (milkType != null && milkType.equalsIgnoreCase(CattleType.TEST)) {
            avgReportdetail = null;
        }
        if (arrReportsEnt == null || milkType == null) {
            strBuild = strBuild + "\n";
            if (avgReportdetail != null) {
                if (amcuConfig.getKeyAllowDisplayRate()) {
                    strBuild = strBuild + rightPad("Total Records", 25)
                            + avgReportdetail.totalEntries + "\n"
                            + getAverageReportWithRate(avgReportdetail);
                } else {
                    strBuild = strBuild
                            + rightPad("Total Records", 25)
                            + avgReportdetail.totalEntries + "\n"
                            + getReportWithoutRate(avgReportdetail);
                }
            }
            strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');
            return strBuild;
        }

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            strBuild = strBuild + formatPrintRecords.formatRecord(arrReportsEnt.get(i)) + "\n";

            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n" + "\n";
                if (avgReportdetail != null) {
                    if (AmcuConfig.getInstance().getKeyAllowDisplayRate()) {
                        strBuild = strBuild + rightPad("Total Records", 25)
                                + avgReportdetail.totalEntries + "\n"
                                + getAverageReportWithRate(avgReportdetail);
                    } else {
                        strBuild = strBuild + rightPad("Total Records", 25)
                                + avgReportdetail.totalEntries + "\n"
                                + getReportWithoutRate(avgReportdetail);
                    }
                }
                strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');
            }
        }
        return strBuild;
    }


    public String setIncentiveDataForReports(ArrayList<ReportEntity> arrReportsEnt,
                                             String milkType) {
        String strBuild = "";
        String inCentiveBuild = "";

        AverageReportDetail avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(
                arrReportsEnt, false);
        if (milkType != null && milkType.equalsIgnoreCase(CattleType.TEST)) {
            avgReportdetail = null;
        }

        if (arrReportsEnt == null || milkType == null) {
            strBuild = strBuild + "\n";
            if (avgReportdetail != null) {
                strBuild = strBuild + rightPad("Total Records", 25)
                        + avgReportdetail.totalEntries + "\n"
                        + getAverageReportWithRate(avgReportdetail);
            }
            strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');
            return strBuild;
        }

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            strBuild = strBuild + formatPrintRecords.formatRecord(arrReportsEnt.get(i)) + "\n";
            inCentiveBuild = inCentiveBuild + formatPrintRecords.incentiveFormatRecord(arrReportsEnt.get(i)) + "\n";

            if (i == arrReportsEnt.size() - 1) {
                strBuild = strBuild + "\n";
                strBuild = strBuild + "\n";
                strBuild = strBuild + formatPrintRecords.incentiveHeader() + "\n";
                strBuild = strBuild + inCentiveBuild;
                strBuild = strBuild + "\n\n";

                if (avgReportdetail != null) {
                    strBuild = strBuild + rightPad("Total Records", 25)
                            + avgReportdetail.totalEntries + "\n"
                            + getAverageReportWithRate(avgReportdetail);
                }
                strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');
            }

        }

        return strBuild;
    }


    public String setDataForPeriodicReports(ArrayList<ReportEntity> arrReportsEnt,
                                            String milkType) {

        String date = null;
        String strBuild = "";
        AverageReportDetail avgReportdetail = new SmartCCUtil(mContext).getAverageOfReport(arrReportsEnt, false);
        if (arrReportsEnt == null || milkType == null) {
            strBuild = strBuild + "\n";
            if (avgReportdetail != null) {
                strBuild = strBuild + rightPad("Total Records", 25)
                        + avgReportdetail.totalEntries + "\n"
                        + getAverageReportWithRate(avgReportdetail);
            }
            strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');
            printerQueue.add(strBuild);
            return strBuild;
        }

        int j = 0;
        strBuild = "";
        for (int i = 0; i < arrReportsEnt.size(); i++) {
            if (date == null || !date.equalsIgnoreCase(arrReportsEnt.get(i).postDate)) {
                strBuild = strBuild + "\n" + SmartCCUtil.changeDateFormat(
                        arrReportsEnt.get(i).postDate, "yyyy-mm-dd", "dd-mm-yyyy"
                ) + "\n\n";
                date = arrReportsEnt.get(i).postDate;
            }
            strBuild = strBuild + formatPrintRecords.formatRecord(arrReportsEnt.get(i)) + "\n";

            j++;
            if (j % 10 == 0) {
                printerQueue.add(new String(strBuild));
                strBuild = "";
            }
            if (i == arrReportsEnt.size() - 1) {
                printerQueue.add(new String(strBuild));
                strBuild = "";
                strBuild = strBuild + "\n";
                strBuild = strBuild + "\n";
                if (avgReportdetail != null && !milkType.equalsIgnoreCase(CattleType.TEST)) {

                    String firstLine = "";
                    //  if (sessionManager.getReportType() == Util.PERIODICREPORTINDIVIDUAL) {
                    firstLine = rightPad("Total Records", 25);
                  /*  } else {
                        firstLine = rightPad("Total Farmers", 25);
                    }*/
                    strBuild = strBuild + firstLine
                            + avgReportdetail.totalEntries + "\n"
                            + getAverageReportWithRate(avgReportdetail);
                }
                strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');
                printerQueue.add(new String(strBuild));
            }
        }


        return strBuild;
    }

    public String getMemberBillSummaryReport(ArrayList<ReportEntity> reportList) {

        SmartCCUtil smartCCUtil = new SmartCCUtil(mContext);
        Collections.sort(reportList, new Comparator<ReportEntity>() {
            @Override
            public int compare(ReportEntity o1, ReportEntity o2) {
                return o1.farmerId.compareTo(o2.farmerId);
            }
        });
        ArrayList<ReportEntity> consolidatedList = new ArrayList<>();
        Map<String, List<ReportEntity>> map = new LinkedHashMap<>();
        for (ReportEntity reportEntity : reportList) {
            if (map.get(reportEntity.farmerId) == null) {
                ArrayList<ReportEntity> list = new ArrayList<>();
                list.add(reportEntity);
                map.put(reportEntity.farmerId, list);
            } else {
                map.get(reportEntity.farmerId).add(reportEntity);
            }
        }
        for (Map.Entry<String, List<ReportEntity>> entry : map.entrySet()) {
            AverageReportDetail aRD = smartCCUtil.getAverageOfReport((ArrayList<ReportEntity>) entry.getValue(), false);
            ReportEntity re = new ReportEntity();
            ArrayList<ReportEntity> reportEntities = (ArrayList<ReportEntity>) entry.getValue();
            ReportEntity reportEntity = reportEntities.get(0);

            re.setFarmerName(reportEntity.farmerName);
            re.setFarmerId(entry.getKey());
            re.setQuantity(aRD.totalQuantity);
            re.setRate(aRD.avgRate);
            re.setAmount(aRD.totalAmount);
            consolidatedList.add(re);
        }

        String strBuild = "";
        strBuild = strBuild + "\n" + formatPrintRecords.createSeparatorThermal('-') +
                formatPrintRecords.formatTotalPeriodicHeader() + "\n";

        printerQueue.add(strBuild);
        strBuild = "";

        for (int i = 0; i < consolidatedList.size(); i++) {

            strBuild = strBuild + formatPrintRecords.formatTotalPeriodicRecord(consolidatedList.get(i)) + "\n";

            if (i % 10 == 0) {
                printerQueue.add(strBuild);
                strBuild = "";
            }


        }

        printerQueue.add(strBuild);
        strBuild = "";

        strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');

        strBuild = strBuild + "\n" + "  Grand Total: " + "\n"
                + formatPrintRecords.createSeparatorThermal('-');

        printerQueue.add(strBuild);
        strBuild = "";

        AverageReportDetail avgReportdetail = smartCCUtil.getAverageOfReport(reportList, false);
        //  AverageReportDetail avgReportdetail = new DatabaseEntity(mContext).getAverageReportDetails(null, arrReportsEnt);
        if (avgReportdetail != null) {
            strBuild = strBuild + rightPad("Total Records", 25)
                    + consolidatedList.size() + "\n"
                    + rightPad("Average Rate", 25)
                    + avgReportdetail.avgRate + "\n"
                    + rightPad("Total Qty", 25)
                    + avgReportdetail.totalQuantity + "\n"
                    + rightPad("Total Amount", 25)
                    + avgReportdetail.totalAmount + "\n" + "\n";
        }

        strBuild = strBuild + formatPrintRecords.createSeparatorThermal('-');

        printerQueue.add(strBuild);
        strBuild = "";

        mFormatCompleteListener.onFormatComplete();

        return strBuild;

    }


    public String getPeriodicFromMapData(String key, ArrayList<ReportEntity> allReportEnt) {
        String strBuild = "";


        if (key.equalsIgnoreCase(CattleType.TEST)) {
            strBuild = strBuild + "\n" + "  Sample records" + "\n"
                    + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
            strBuild = strBuild + setDataForPeriodicReports(allReportEnt, key);
        } else {
            strBuild = strBuild + "\n" + "  Milk Type: " + key + "\n"
                    + formatPrintRecords.createSeparatorThermal('-') + formatPrintRecords.formatHeader() + "\n";
            if (amcuConfig.getKeyAllowProteinValue()) {
                strBuild = strBuild + setIncentiveDataForReports(allReportEnt, key);
            } else {

                printerQueue.add(strBuild);
                strBuild = strBuild
                        + setDataForPeriodicReports(allReportEnt, key);
            }

        }
        return strBuild;
    }


    public String getPeriodicReport(ArrayList<ReportEntity> allReportEnt) {
        String strBuild = "";
        String strSample = "";
        Collections.sort(allReportEnt, new Comparator<ReportEntity>() {
                    @Override
                    public int compare(ReportEntity o1, ReportEntity o2) {
                        return o1.postDate.compareTo(o2.postDate);
                    }
                }
        );
        Map<String, ArrayList<ReportEntity>> hashList = new ConcurrentHashMap<>();

        if (allReportEnt == null || allReportEnt.size() == 0) {
            mFormatCompleteListener.onFormatComplete();
            return strBuild;
        }

        for (ReportEntity repEnt : allReportEnt) {
            if (hashList.keySet().contains(repEnt.milkType)) {
                for (Map.Entry<String, ArrayList<ReportEntity>> entry : hashList.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase(repEnt.milkType)) {
                        entry.getValue().add(repEnt);
                        hashList.put(entry.getKey(), entry.getValue());
                    }
                }
            } else {
                ArrayList<ReportEntity> subList = new ArrayList<>();
                subList.add(repEnt);
                hashList.put(repEnt.milkType, subList);
            }
        }

        for (Map.Entry<String, ArrayList<ReportEntity>> entry : hashList.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(CattleType.TEST)) {

                strSample = getPeriodicFromMapData(entry.getKey(), entry.getValue());
            } else {

                strBuild = strBuild + getPeriodicFromMapData(entry.getKey(), entry.getValue());

            }
        }

        strBuild = strBuild + strSample;
        if (hashList != null && hashList.size() > 1) {
            strBuild = strBuild + "\n" + "  Total Summary" + "\n"
                    + formatPrintRecords.createSeparatorThermal('-') + "\n";
            strBuild = strBuild + setDataForPeriodicReports(allReportEnt, null);
        }
        mFormatCompleteListener.onFormatComplete();
        return strBuild;
    }

    public String getReportWithoutRate(AverageReportDetail avgReportdetail) {
        strBuild = rightPad("Total Accepted", 25)
                + avgReportdetail.totalAcceptedEntries + "\n"
                + rightPad("Total Rejected", 25)
                + avgReportdetail.totalRejectedEntries + "\n"
                + rightPad(formatPrintRecords.getTheUnit(), 25)
                + avgReportdetail.avgQuantity + "\n"
                + rightPad("Average Fat", 25)
                + avgReportdetail.avgFat + "\n"
                + rightPad("Average SNF", 25)
                + avgReportdetail.avgSnf + "\n"
                + rightPad("Total Qty", 25)
                + avgReportdetail.totalQuantity + "\n"
                + rightPad("Total KG-Fat", 25)
                + avgReportdetail.totalFatKg + "\n"
                + rightPad("Total KG-SNF", 25)
                + avgReportdetail.totalSnfKg + "\n" + "\n";
        return strBuild;
    }


    public String getAverageReportWithRate(AverageReportDetail avgReportdetail) {
        return rightPad("Total Accepted", 25)
                + avgReportdetail.totalAcceptedEntries + "\n"
                + rightPad("Total Rejected", 25)
                + avgReportdetail.totalRejectedEntries + "\n"
                + rightPad("Average Amount", 25)
                + avgReportdetail.avgAmount + "\n"
                + rightPad(formatPrintRecords.getTheUnit(), 25)
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


    /**
     * Set periodic custom report as per Banas format
     *
     * @param arrReportsEnt
     * @return
     */
    public String setCustomPeriodicReport(ArrayList<ReportEntity> arrReportsEnt, boolean allowSample) {

        StringBuilder strReportData = new StringBuilder();
        String header = formatPrintRecords.periodicFormatHeader();

        String milkType = null;
        String farmerId = null;
        ArrayList<ReportEntity> allAverageReport = new ArrayList<>();
        String milkTypeText = "";


        for (int i = 0; i < arrReportsEnt.size(); i++) {

            if (milkType == null) {
                milkType = arrReportsEnt.get(i).milkType;
                milkTypeText = "\n" + " Type: " + milkType;

            }

            if (milkType.equalsIgnoreCase(arrReportsEnt.get(i).milkType)) {

                if (farmerId == null) {
                    allAverageReport = new ArrayList<>();
                    farmerId = arrReportsEnt.get(i).farmerId;
                    strReportData.append(PrinterManager.title + "\n");
                    strReportData.append(milkTypeText + "      Id: " + farmerId);
                    strReportData.append("\n" + header);

                    printerQueue.add(strReportData.toString());
                    strReportData = new StringBuilder();
                }

                if (farmerId.equalsIgnoreCase(arrReportsEnt.get(i).farmerId)) {
                } else {
                    //To set farmer Id

                    strReportData.append("\n" + formatPrintRecords.createSeparatorThermal('-'));
                    strReportData.append(formatPrintRecords.getPeriodicAverageReport(
                            allAverageReport, allowSample));
                    strReportData.append(formatPrintRecords.createSeparatorThermal('-'));
                    printerQueue.add(strReportData.toString());
                    strReportData = new StringBuilder();
                    allAverageReport = new ArrayList<>();
                    farmerId = arrReportsEnt.get(i).farmerId;
                    strReportData.append("\n" + PrinterManager.title + "\n");
                    strReportData.append(milkTypeText + "      Id: " + farmerId);
                    strReportData.append("\n" + header);
                    printerQueue.add(strReportData.toString());
                    strReportData = new StringBuilder();
                }


            } else {
                //To set milk type

                strReportData.append("\n" + formatPrintRecords.createSeparatorThermal('-'));
                strReportData.append(formatPrintRecords.getPeriodicAverageReport(allAverageReport,
                        allowSample));
                strReportData.append(formatPrintRecords.createSeparatorThermal('-'));

                printerQueue.add(strReportData.toString());
                strReportData = new StringBuilder();

                milkType = arrReportsEnt.get(i).milkType;
                milkTypeText = "\n" + " Type: " + milkType;

                allAverageReport = new ArrayList<>();
                farmerId = arrReportsEnt.get(i).farmerId;
                strReportData.append("\n" + PrinterManager.title + "\n");
                strReportData.append(milkTypeText + "      Id: " + farmerId);
                strReportData.append("\n" + header);
                printerQueue.add(strReportData.toString());
                strReportData = new StringBuilder();
            }

            allAverageReport.add(arrReportsEnt.get(i));


            strReportData.append("\n" +
                    formatPrintRecords.periodicFormatRecord(arrReportsEnt.get(i)));

            printerQueue.add(strReportData.toString());
            strReportData = new StringBuilder();


            if (i == (arrReportsEnt.size() - 1)) {
                printerQueue.add(strReportData.toString());
                strReportData = new StringBuilder();
                strReportData.append("\n" + formatPrintRecords.createSeparatorThermal('-'));
                strReportData.append(formatPrintRecords.getPeriodicAverageReport(
                        allAverageReport, allowSample));
                strReportData.append(formatPrintRecords.createSeparatorThermal('-'));

                printerQueue.add(strReportData.toString());
                strReportData = new StringBuilder();
            }
        }
        strReportData.append("\n" + "\n");
        printerQueue.add(strReportData.toString());

        mFormatCompleteListener.onFormatComplete();

        return strReportData.toString();
    }


    public String setIncentivePeriodicRecords(ArrayList<ReportEntity> arrReportsEnt) {

        String strReportData = "";
        String incentiveData = "";

        String header = formatPrintRecords.periodicFormatHeader();

        String milkType = null;
        String farmerId = null;
        ArrayList<ReportEntity> allAverageReport = new ArrayList<>();
        String milkTypeText = "";

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            if (milkType == null) {
                milkType = arrReportsEnt.get(i).milkType;
                milkTypeText = "\n" + " Type: " + milkType;

            }

            if (milkType.equalsIgnoreCase(arrReportsEnt.get(i).milkType)) {

                if (farmerId == null) {
                    allAverageReport = new ArrayList<>();
                    farmerId = arrReportsEnt.get(i).farmerId;
                    strReportData = strReportData + PrinterManager.title + "\n";
                    strReportData = strReportData + milkTypeText + "      Id: " + farmerId;
                    strReportData = strReportData + "\n" + header;

                    printerQueue.add(strReportData);
                    strReportData = "";
                }

                if (farmerId.equalsIgnoreCase(arrReportsEnt.get(i).farmerId)) {
                } else {
                    //To set farmer Id

                    strReportData = strReportData + "\n\n" + formatPrintRecords.incentivePeriodicFormatHeader();
                    strReportData = strReportData + incentiveData;
                    incentiveData = "";
                    printerQueue.add(strReportData);
                    strReportData = "";
                    strReportData = strReportData + "\n" + formatPrintRecords.createSeparatorThermal('-');
                    strReportData = strReportData + formatPrintRecords.getPeriodicAverageReport(allAverageReport, false);
                    strReportData = strReportData + formatPrintRecords.createSeparatorThermal('-');
                    printerQueue.add(strReportData);
                    strReportData = "";

                    allAverageReport = new ArrayList<>();
                    farmerId = arrReportsEnt.get(i).farmerId;
                    strReportData = strReportData + "\n" + PrinterManager.title + "\n";
                    strReportData = strReportData + milkTypeText + "      Id: " + farmerId;
                    strReportData = strReportData + "\n" + header;
                    printerQueue.add(strReportData);
                    strReportData = "";
                }


            } else {
                //To set milk type
                strReportData = strReportData + "\n\n" + formatPrintRecords.incentivePeriodicFormatHeader();
                strReportData = strReportData + incentiveData;
                incentiveData = "";
                printerQueue.add(strReportData);
                strReportData = "";


                strReportData = strReportData + "\n" + formatPrintRecords.createSeparatorThermal('-');
                strReportData = strReportData + formatPrintRecords.getPeriodicAverageReport(allAverageReport, false);
                strReportData = strReportData + formatPrintRecords.createSeparatorThermal('-');
                printerQueue.add(strReportData);
                strReportData = "";

                milkType = arrReportsEnt.get(i).milkType;
                milkTypeText = "\n" + " Type: " + milkType;

                allAverageReport = new ArrayList<>();
                farmerId = arrReportsEnt.get(i).farmerId;
                strReportData = strReportData + "\n" + PrinterManager.title + "\n";
                strReportData = strReportData + milkTypeText + "      Id: " + farmerId;
                strReportData = strReportData + "\n" + header;
                printerQueue.add(strReportData);
                strReportData = "";
            }

            allAverageReport.add(arrReportsEnt.get(i));


            strReportData = strReportData + "\n" + formatPrintRecords.periodicFormatRecord(arrReportsEnt.get(i));

            printerQueue.add(strReportData);
            strReportData = "";

        }

        printerQueue.add(strReportData);
        strReportData = "";

        for (int i = 0; i < arrReportsEnt.size(); i++) {

            incentiveData = incentiveData + "\n" + formatPrintRecords.incentivePeriodicFormatRecord(arrReportsEnt.get(i));

            printerQueue.add(incentiveData);
            incentiveData = "";


            if (i == (arrReportsEnt.size() - 1)) {
                strReportData = strReportData + "\n\n" + formatPrintRecords.incentivePeriodicFormatHeader();
                strReportData = strReportData + incentiveData;

                printerQueue.add(strReportData);
                strReportData = "";
                incentiveData = "";

                strReportData = strReportData + "\n" + formatPrintRecords.createSeparatorThermal('-');
                strReportData = strReportData + formatPrintRecords.getPeriodicAverageReport(allAverageReport, false);
                strReportData = strReportData + formatPrintRecords.createSeparatorThermal('-');
            }
        }
        strReportData = strReportData + "\n" + "\n";
        printerQueue.add(strReportData);
        strReportData = "";
        incentiveData = "";

        mFormatCompleteListener.onFormatComplete();

        return strReportData;
    }


    public void setOnFormatCompleteListener(OnFormatCompleteListener listener) {
        mFormatCompleteListener = listener;
    }

    private String milkTypeHeader(String milkType) {

        String header = "Milk Type: " + milkType;

        if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_TAMIL)) {
            header = mContext.getResources().getString(R.string.tm_milk_type) + ": "
                    + getMilkTypeTamil(milkType);
        } else if (amcuConfig.getPrinter().equalsIgnoreCase(AppConstants.PRINTER_HINDI)) {
            header = mContext.getResources().getString(R.string.tm_milk_type) + ": "
                    + getMilkTypeHindi(milkType);
        }

        return header;
    }

    private String getMilkTypeTamil(String milkType) {

        if (milkType.equalsIgnoreCase("COW")) {
            milkType = mContext.getResources().getString(R.string.tm_cow);
        } else if (milkType.equalsIgnoreCase("BUFFALO")) {
            milkType = mContext.getResources().getString(R.string.tm_buffalo);
        } else if (milkType.equalsIgnoreCase("MIXED")) {
            milkType = mContext.getResources().getString(R.string.tm_mixed);
        }

        return milkType;

    }

    private String getMilkTypeHindi(String milkType) {

        if (milkType.equalsIgnoreCase("COW")) {
            milkType = mContext.getResources().getString(R.string.h_cow);
        } else if (milkType.equalsIgnoreCase("BUFFALO")) {
            milkType = mContext.getResources().getString(R.string.h_buffalo);
        } else if (milkType.equalsIgnoreCase("MIXED")) {
            milkType = mContext.getResources().getString(R.string.h_buffalo);
        }

        return milkType;

    }

    public interface OnFormatCompleteListener {
        void onFormatComplete();
    }


}
