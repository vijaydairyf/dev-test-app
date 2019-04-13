package com.devapp.devmain.helper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.devapp.devmain.ConsolidationPost.ConsolidatedRecordsSynchronizer;
import com.devapp.devmain.ConsolidationPost.DateShiftEntry;
import com.devapp.devmain.ConsolidationPost.SynchronizableElement;
import com.devapp.devmain.additionalRecords.Database.AddtionalDatabase;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.encryption.Csv;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.postentities.ConsolidatedPostData;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.usb.WriteExcel;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.AdvanceUtil;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.database.AgentDatabase;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.report.ReportHintConstant;
import com.devapp.syncapp.json.send.UnSentRecordDetails;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import jxl.write.WriteException;

import static android.os.Looper.getMainLooper;

/**
 * Created by u_pendra on 28/9/17.
 */

public class ReportHelper {

    DatabaseHandler databaseHandler;
    AgentDatabase agentDatabase;
    SmartCCUtil smartCCUtil;
    AdvanceUtil advanceUtil;
    AddtionalDatabase addtionalDatabase;
    ConsolidatedRecordsSynchronizer consolidatedRecordsSynchronizer;
    Runnable uiRunnable;
    boolean isWrittenToFile = false;
    //  CollectionRecordsHandler collectionRecordsHandler;
    private Context mContext;
    private AmcuConfig amcuConfig;


    public ReportHelper(Context context) {
        this.mContext = context;
        this.amcuConfig = AmcuConfig.getInstance();
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        agentDatabase = databaseHandler.getAgentDatabase();
        smartCCUtil = new SmartCCUtil(mContext);
        advanceUtil = new AdvanceUtil(mContext);

        addtionalDatabase = databaseHandler.getAdditionallDatabase();
        consolidatedRecordsSynchronizer = ConsolidatedRecordsSynchronizer.getInstance(mContext);
    }

    public void createUnsentCleaningRecords() {

//        String unsentData = SmartCCConstants.unsentCleaningData;
        String unsentData = amcuConfig.getUnsentCleaningData();
        String fileName = getCleaningDataFileName();
        copyCleaningFile(fileName, unsentData);

    }

    public void createUnsentRecords() {

        //TreeSet<DateShiftEntry> allDateShiftEntrySet = consolidatedRecordsSynchronizer.getUnsentDayShiftList();
        final Handler handler = new Handler(getMainLooper());


        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<ConsolidatedPostData> combinedPostEndShift = consolidatedRecordsSynchronizer.getAllUnsentRecords();
                String fileName = getFileName(combinedPostEndShift);
                copyFile(fileName, combinedPostEndShift);
                handler.post(uiRunnable);

            }
        }).start();

        uiRunnable = new Runnable() {
            @Override
            public void run() {
                Util.displayErrorToast("File has copied in pendrive successfully!", mContext);
                handler.removeCallbacks(uiRunnable);

            }
        };


    }

    public UnSentRecordDetails createUnsentRecordsS() {
        UnSentRecordDetails unSentRecordDetails = new UnSentRecordDetails();
        unSentRecordDetails.setEncData("");
        try {
            ArrayList<ConsolidatedPostData> combinedPostEndShift = consolidatedRecordsSynchronizer.getAllUnsentRecords();

            if (combinedPostEndShift != null && combinedPostEndShift.size() > 0) {
                int recordsCount = 0;
                for (ConsolidatedPostData cpd : combinedPostEndShift) {
                    Hashtable<String, ArrayList<? extends SynchronizableElement>> recordEntries = cpd.recordEntries;
                    Set<String> keySet = recordEntries.keySet();
                    Iterator<String> iterator = keySet.iterator();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        recordsCount += recordEntries.get(key).size();
                    }

                }
                unSentRecordDetails.setFileName(getFileName(combinedPostEndShift));
                unSentRecordDetails.setEncData(copyFile4Sync(combinedPostEndShift));
                unSentRecordDetails.setTotalRecords(recordsCount);
                return unSentRecordDetails;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return unSentRecordDetails;

    }

    private String copyFile4Sync(ArrayList<ConsolidatedPostData> combinedShifRecords) {
        Csv csv = new Csv(mContext, 0);
        return csv.generateCSVForConsolidatedReportS(combinedShifRecords);
    }

    private void copyFile(final String fileName, final ArrayList<ConsolidatedPostData> combinedShifRecords) {

        Csv csv = new Csv(mContext, 0);
        csv.generateCSVForConsolidatedReport(combinedShifRecords, fileName);

    }

    private void copyCleaningFile(final String fileName, final String cleaningData) {
        final Handler handler = new Handler(getMainLooper());


        new Thread(new Runnable() {
            @Override
            public void run() {
                Csv csv = new Csv(mContext, 0);
                csv.generateCSVForCleaningReport(cleaningData, fileName);
                handler.post(uiRunnable);
            }
        }).start();

        uiRunnable = new Runnable() {
            @Override
            public void run() {
                Util.displayErrorToast("File has copied in pendrive successfully!", mContext);
                handler.removeCallbacks(uiRunnable);

            }
        };
    }

    public String getFileName(String name) {

        final File fileSmartAmcu = new File(Util.rootFileName, "smartAmcuReports");
        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }
        File gpxfile = new File(fileSmartAmcu, new SessionManager(mContext)
                .getSocietyName().replace(" ", "") + "_" + name);
        return gpxfile.toString();
    }

    /**
     * This is to support list from ReportActivity
     *
     * @param allReportEntity
     * @param fileName
     */
    public void createEncryptedFileFromReport(ArrayList<ReportEntity> allReportEntity,
                                              String fileName) {
        ArrayList<ConsolidatedPostData> combinedPostEndShift = consolidatedRecordsSynchronizer.getAllConsolidatedRecords(
                getDateAndShiftFromList(allReportEntity));
        copyFile(fileName, combinedPostEndShift);
    }

    public String getFileName(ArrayList<ConsolidatedPostData> allReportEntity) {
        String fileName;
        SessionManager sessionManager = new SessionManager(mContext);

        if ((allReportEntity.size() > 2) &&
                (!SmartCCUtil.getpostDateFromTheDateFormat(
                        allReportEntity.get(0).consolidatedMetadata.
                                consolidatedDate.collectionDate)
                        .equalsIgnoreCase(SmartCCUtil.getpostDateFromTheDateFormat(
                                allReportEntity.get(allReportEntity.size() - 1).consolidatedMetadata
                                        .consolidatedDate.collectionDate)))) {
            fileName = sessionManager.getCollectionID() + "_records_from_" +
                    SmartCCUtil.getpostDateFromTheDateFormat(
                            allReportEntity.get(0).consolidatedMetadata.consolidatedDate.collectionDate)
                    + "_to_" +
                    SmartCCUtil.getpostDateFromTheDateFormat(
                            allReportEntity.get(allReportEntity.size() - 1).consolidatedMetadata
                                    .consolidatedDate.collectionDate);

        } else {
            fileName = sessionManager.getCollectionID() + "_records_ON_" +
                    SmartCCUtil.getpostDateFromTheDateFormat(
                            allReportEntity.get(0).consolidatedMetadata
                                    .consolidatedDate.collectionDate);
        }

        fileName = fileName + "." + AppConstants.CONSOLIDATED_EXTENSION;


        final File fileSmartAmcu = new File(Util.rootFileName, "smartAmcuReports");
        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }
        File gpxfile = new File(fileSmartAmcu, fileName);
        return gpxfile.toString();
    }

    public String getCleaningDataFileName() {
        SessionManager sessionManager = new SessionManager(mContext);
        String fileName = sessionManager.getCollectionID() + "_unsent_cleaning_records_ON_" +
                Util.getCurrentDateTime() + "." + AppConstants.CONSOLIDATED_EXTENSION;

        final File fileSmartAmcu = new File(Util.rootFileName, "smartAmcuReports");
        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }

        File gpxfile = new File(fileSmartAmcu, fileName);

        return gpxfile.toString();
    }

    private void displayToast(String message) {
        Util.displayErrorToast(message, mContext);
    }

    private TreeSet<DateShiftEntry> getDateAndShiftFromList(ArrayList<ReportEntity> allReportEnt) {
        TreeSet<DateShiftEntry> allTreeSet = new TreeSet<>();
        for (ReportEntity reportEntity : allReportEnt) {
            DateShiftEntry dateShiftEntry = new DateShiftEntry();
            dateShiftEntry.setShift(reportEntity.postShift);
            dateShiftEntry.setDate(reportEntity.postDate);

            allTreeSet.add(dateShiftEntry);
        }

        return allTreeSet;
    }

    public AverageReportDetail getAverageSalesReport(ArrayList<SalesRecordEntity> allSalesReportEnt) {
        AverageReportDetail averageReportDetail = new AverageReportDetail();
        ValidationHelper validationHelper = new ValidationHelper();
        double avgFat = 0, avgSnf = 0;

        for (SalesRecordEntity salesRecordEntity : allSalesReportEnt) {
            averageReportDetail.totalFarmer = averageReportDetail.totalFarmer + 1;
            averageReportDetail.totalAmount =
                    averageReportDetail.totalAmount +
                            salesRecordEntity.getAmount();
            averageReportDetail.totalQuantity = averageReportDetail.totalQuantity +
                    salesRecordEntity.getQuantity();

            if (salesRecordEntity.sentStatus == DatabaseHandler.COL_REC_NW_SENT) {
                averageReportDetail.totalSent = averageReportDetail.totalSent + 1;
            } else {
                averageReportDetail.totalUnsent = averageReportDetail.totalUnsent + 1;
            }

            avgFat = averageReportDetail.totalQuantity
                    + ((salesRecordEntity.getFat() * salesRecordEntity.getQuantity()) / 100);
            avgSnf = averageReportDetail.totalQuantity
                    + ((salesRecordEntity.getSnf() * salesRecordEntity.getQuantity()) / 100);
        }

        if (averageReportDetail != null) {
            averageReportDetail.avgQuantity =
                    averageReportDetail.totalQuantity / averageReportDetail.totalFarmer;
            averageReportDetail.avgAmount =
                    averageReportDetail.totalAmount / averageReportDetail.totalFarmer;
            averageReportDetail.avgRate =
                    averageReportDetail.totalAmount / Double.valueOf(averageReportDetail.totalQuantity);
            averageReportDetail.avgFat = (avgFat * 100) / Double.valueOf(averageReportDetail.totalQuantity);
            averageReportDetail.avgSnf = (avgSnf * 100) / Double.valueOf(averageReportDetail.totalQuantity);
        }

        return averageReportDetail;
    }

    public ArrayList<ReportEntity> sortByConfiguration(ArrayList<ReportEntity> reportEntities,
                                                       final String type, final String order) {

        ArrayList<ReportEntity> tempList = new ArrayList<>(reportEntities);
        Collections.sort(tempList, new Comparator<ReportEntity>() {
            @Override
            public int compare(ReportEntity o1, ReportEntity o2) {
                switch (type) {
                    case ReportHintConstant.MEMBER_ID: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return o1.farmerId.compareTo(o2.farmerId);
                        } else {
                            return o2.farmerId.compareTo(o1.farmerId);
                        }
                    }
                    case ReportHintConstant.DATE: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return o1.postDate.compareTo(o2.postDate);
                        } else {
                            return o2.postDate.compareTo(o1.postDate);
                        }
                    }
                    case ReportHintConstant.COLL_TIME: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return Long.valueOf(o1.miliTime).compareTo(Long.valueOf(o2.miliTime));
                        } else {
                            return Long.valueOf(o2.miliTime).compareTo(Long.valueOf(o1.miliTime));
                        }
                    }
                    case ReportHintConstant.SHIFT: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return o1.getPostShift().compareTo(o2.getPostShift());

                        } else {
                            return o2.getPostShift().compareTo(o1.getPostShift());
                        }

                    }
                    case ReportHintConstant.CATTLE_TYPE: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return o1.getMilkType().compareTo(o2.getMilkType());
                        } else {
                            return o2.getMilkType().compareTo(o1.getMilkType());
                        }

                    }
                    case ReportHintConstant.FAT: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return Double.valueOf(o1.fat).compareTo(Double.valueOf(o2.fat));
                        } else {
                            return Double.valueOf(o2.fat).compareTo(Double.valueOf(o1.fat));
                        }
                    }
                    case ReportHintConstant.SNF: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return Double.valueOf(o1.snf).compareTo(Double.valueOf(o2.snf));
                        } else {
                            return Double.valueOf(o2.snf).compareTo(Double.valueOf(o1.snf));
                        }
                    }
                    case ReportHintConstant.CLR: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return Double.valueOf(o1.clr).compareTo(Double.valueOf(o2.clr));
                        } else {
                            return Double.valueOf(o2.clr).compareTo(Double.valueOf(o1.clr));
                        }
                    }
                    case ReportHintConstant.QTY: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return Double.valueOf(o1.quantity).compareTo(Double.valueOf(o2.quantity));
                        } else {
                            return Double.valueOf(o2.quantity).compareTo(Double.valueOf(o1.quantity));
                        }
                    }
                    case ReportHintConstant.RATE_H: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return Double.valueOf(o1.rate).compareTo(Double.valueOf(o2.rate));
                        } else {
                            return Double.valueOf(o2.rate).compareTo(Double.valueOf(o1.rate));
                        }
                    }


                    case ReportHintConstant.AMOUNT_H: {
                        if (order.equalsIgnoreCase("ASC")) {
                            return Double.valueOf(o1.amount).compareTo(Double.valueOf(o2.amount));
                        } else {
                            return Double.valueOf(o2.amount).compareTo(Double.valueOf(o1.amount));
                        }
                    }

                }
                return 0;
            }
        });

        return tempList;

    }

    public void createEncryptedRecords(final TreeSet<DateShiftEntry> dateShiftEntries, final ProgressBar progressBar) {

        final Handler handler = new Handler(getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {

                ArrayList<ConsolidatedPostData> combinedPostEndShift =
                        consolidatedRecordsSynchronizer.findAllRecords(dateShiftEntries);
                String fileName = getFileName(combinedPostEndShift);
                copyFile(fileName, combinedPostEndShift);
                handler.post(uiRunnable);

            }
        }).start();

        uiRunnable = new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Util.displayErrorToast("File exported to pendrive successfully!", mContext);
                handler.removeCallbacks(uiRunnable);

            }
        };
    }

    public File createFileForPrint(File fileName, int type, ArrayList<ReportEntity>
            reportEntities) {

        final WriteExcel writeExcel = new WriteExcel();

        writeExcel.setWriteCompleteListener(new WriteExcel.OnExcelWriteCompleteListener() {
            @Override
            public void onWriteComplete() {
                isWrittenToFile = true;
            }
        });

        final File fileSmartAmcu = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath(), "smartAmcuReports");

        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }

        fileName = getTheExcelFileName(fileSmartAmcu, "Title", "Type");

        try {
            if (fileName != null) {
                writeExcel.setOutputFile(fileName.toString());
                writeExcel.write(mContext, type, reportEntities);
            }
        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if (isWrittenToFile) {
            Util.displayErrorToast("Write completed on excel sheet!", mContext);
            return fileName;
        } else {
            return null;
        }

    }

    //TODO Required for A4 printer

    /**
     * @param path
     */
    public void printViaShareIntent(String path) {

        Util.displayErrorToast(new File(path).toString(), mContext);

        try {
            Intent printIntent = new Intent(Intent.ACTION_VIEW);
            printIntent.setPackage("cn.wps.moffice_eng");
            printIntent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.ms-excel");
            printIntent.putExtra("return", true);
            mContext.startActivity(printIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Util.displayErrorToast("No application found to open excel sheet!", mContext);
        }
    }


    public File getTheExcelFileName(File fileSmartAmcu, String title, String type) {


        // socName_date_shift_Shift_Report.xls" for shift report
        //socName_date_shift_accumulative_shift_Report.xls" for shift report
        //socName_Member_Bill_Report.xls
        //socName_Bill_Summary.xls
        //socName_startDate_to_endDate_Periodic_Report.xls
        //socName_startDate_to_endDate_Sales_Report.xls
        //socName_startDate_to_endDate_Dairy_Report.xls
        //socName_startDate_to_endDate_Total_bill_summary.xls

        File xcelFile = new File(fileSmartAmcu.toString(), title);
        return xcelFile;


    }


}
