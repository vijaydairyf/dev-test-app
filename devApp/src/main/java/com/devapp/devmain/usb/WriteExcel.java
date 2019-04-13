package com.devapp.devmain.usb;

import android.content.Context;

import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.CenterRecordEntity;
import com.devapp.devmain.entity.ExcelConstants;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.RateChartEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class WriteExcel {

    private final ArrayList<String> arrRate = new ArrayList<>();
    private final int UNIT_HEADER = 0;
    private final int UNIT_BODY = 1;
    private ArrayList<ReportEntity> allReportEntity;
    private ArrayList<RateChartEntity> allRateChartEnt;
    private ArrayList<AverageReportDetail> allAverageReportDetail;
    private ArrayList<FarmerEntity> allFarmerEntity;
    private ArrayList<String> arrFat = new ArrayList<>();
    private ArrayList<String> arrSnf = new ArrayList<>();
    private DatabaseHandler dbh;
    private AmcuConfig amcuConfig;
    private SessionManager session;
    private WritableCellFormat timesBoldUnderline;
    private WritableCellFormat times;
    private String inputFile;
    private Context context;
    private int checkReport;
    private RateDao rateDao;
    private RateChartNameDao rateChartNameDao;
    private ArrayList<ReportEntity> allDetReportEntity = new ArrayList<>();
    private ArrayList<SalesRecordEntity> allSalesRecordEnt = new ArrayList<>();
    private ArrayList<CenterRecordEntity> allCenterShiftReport = new ArrayList<>();
    private CollectionRecordDao collectionRecordDao;
    private OnExcelWriteCompleteListener onExcelWriteCompleteListener;

    public static int checkNumberOfRecord(AverageReportDetail avgReportdetail) {
        int numOfRecord = 0;
        if (avgReportdetail != null) {
            try {
                numOfRecord = avgReportdetail.totalMember;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        if (numOfRecord > 0) {
            return numOfRecord;
        } else {
            if (avgReportdetail != null) {
                try {
                    numOfRecord = avgReportdetail.totalTestEntries;
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return numOfRecord;
    }

    public void setOutputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public void write(Context context, int chkreport, ArrayList<?> detRepEntity)
            throws IOException, WriteException {
        File file = new File(inputFile);
        this.context = context;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(context);
        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);

        dbh = DatabaseHandler.getDatabaseInstance();
        rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);

        if (chkreport == ExcelConstants.TYPE_RATE_CHART) {
            this.allRateChartEnt = (ArrayList<RateChartEntity>) detRepEntity;
            getFatSnf();
        } else if (chkreport == ExcelConstants.TYPE_AVERAGE_REPORT) {
            this.allAverageReportDetail = (ArrayList<AverageReportDetail>) detRepEntity;
        }
        // For daily shift report
        else if (chkreport == ExcelConstants.TYPE_SHIFT_REPORT) {
            this.allDetReportEntity = (ArrayList<ReportEntity>) detRepEntity;
        } else if (chkreport == ExcelConstants.TYPE_PRINT_REPORT) {
            this.allDetReportEntity = (ArrayList<ReportEntity>) detRepEntity;
        } else if (chkreport == ExcelConstants.TYPE_FARMER) {
            this.allFarmerEntity = (ArrayList<FarmerEntity>) detRepEntity;
        } else if (chkreport == ExcelConstants.TYPE_PERIODIC_REPORT) {
            //This is for periodic report
            this.allDetReportEntity = (ArrayList<ReportEntity>) detRepEntity;
        } else if (chkreport == ExcelConstants.TYPE_SALES_RECORD) {
            //for sales report
            this.allSalesRecordEnt = (ArrayList<SalesRecordEntity>) detRepEntity;
        } else if (chkreport == ExcelConstants.TYPE_DAIRY_REPORT
                || checkReport == ExcelConstants.TYPE_MEMBER_BILL_REPORT) {
            //For Dairy report as per goa requirement
            this.allDetReportEntity = (ArrayList<ReportEntity>) detRepEntity;
        } else if (chkreport == ExcelConstants.TYPE_CHILLING_RECORD) {
            //Chilling center report
            this.allCenterShiftReport = (ArrayList<CenterRecordEntity>) detRepEntity;
        }

        this.checkReport = chkreport;
        if (chkreport != ExcelConstants.TYPE_FARMER && allDetReportEntity.size() < 1) {
            getAllReport();
        }

        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));
        wbSettings.setCharacterSet(0);
        wbSettings.setEncoding("utf-8");


        FileOutputStream fOS = new FileOutputStream(file);

        WritableWorkbook workbook = Workbook.createWorkbook(fOS, wbSettings);


        workbook.createSheet("Report", 0);
        WritableSheet excelSheet = workbook.getSheet(0);
        createLabel(excelSheet);
        createContent(excelSheet);
        workbook.write();
        workbook.close();


        //To sync to resolve pendrive writing issue
        fOS.getFD().sync();
        fOS.close();

        try {
            onExcelWriteCompleteListener.onWriteComplete();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


    }

    private void createLabel(WritableSheet sheet) throws WriteException {
        // Lets create a times font
        WritableFont times10pt = new WritableFont(WritableFont.TIMES, 10);
        // Define the cell format
        times = new WritableCellFormat(times10pt);

        // Lets automatically wrap the cells
        times.setWrap(true);

        // create create a bold font with unterlines
        WritableFont times10ptBoldUnderline = new WritableFont(
                WritableFont.TIMES, 10, WritableFont.BOLD, false,
                UnderlineStyle.SINGLE);
        timesBoldUnderline = new WritableCellFormat(times10ptBoldUnderline);
        // Lets automatically wrap the cells
        timesBoldUnderline.setWrap(true);


        times.setBorder(Border.ALL, BorderLineStyle.THIN);
        timesBoldUnderline.setBorder(Border.ALL, BorderLineStyle.THIN);

        CellView cv = new CellView();
        cv.setFormat(times);
        cv.setFormat(timesBoldUnderline);
        cv.setAutosize(true);


        // Write a few headers
        if (checkReport == 0) {
            addCaption(sheet, 0, 0, "Farmer_ID", ExcelConstants.Member_ID);
            addCaption(sheet, 1, 0, "Farmer_Name", ExcelConstants.Member);
            addCaption(sheet, 2, 0, "SNF", ExcelConstants.Snf);
            addCaption(sheet, 3, 0, "Fat", ExcelConstants.Fat);
            addCaption(sheet, 4, 0, "Rate", ExcelConstants.Rate);
            addCaption(sheet, 5, 0, "Mode", ExcelConstants.Mode);
        } else if (checkReport == ExcelConstants.TYPE_SHIFT_REPORT) {
            // 1 for end shift and 8 for daily shift report

            int row = 0;
            if (amcuConfig.getEnableSequenceNumberInReport()) {
                addCaption(sheet, row++, 0, "Sequence_Number", ExcelConstants.Sequence_Number);
                addCaption(sheet, row++, 0, "Society_ID", ExcelConstants.Society_ID);
            } else {
                addCaption(sheet, row++, 0, "Society_ID", ExcelConstants.Society_ID);
            }


            addCaption(sheet, row++, 0, "Member_ID", ExcelConstants.Member_ID);

            if (amcuConfig.getAllowSequenceNumberInPrintAndReport()) {
                addCaption(sheet, row++, 0, "Serial_number", ExcelConstants.Serial_number);
            }

            addCaption(sheet, row++, 0, "Member", ExcelConstants.Member);

            addCaption(sheet, row++, 0, "Date", ExcelConstants.Date);
            addCaption(sheet, row++, 0, "Time", ExcelConstants.Time);
            addCaption(sheet, row++, 0, "Shift", ExcelConstants.Shift);
            addCaption(sheet, row++, 0, "Milk_Type", ExcelConstants.Milk_Type);
            addCaption(sheet, row++, 0, "Fat", ExcelConstants.Fat);
            addCaption(sheet, row++, 0, "Snf", ExcelConstants.Snf);
            if (amcuConfig.getEnableClrInReport() || amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") ||
                    amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
                addCaption(sheet, row++, 0, "Clr", ExcelConstants.Clr);
            }
            addCaption(sheet, row++, 0, "AWM", ExcelConstants.AWM);
            addCaption(sheet, row++, 0, "temperature", ExcelConstants.Temp);

            addCaption(sheet, row++, 0, getTheUnit(context, UNIT_HEADER), ExcelConstants.Quantity_L);
            if (amcuConfig.getAllowMilkquality()) {
                addCaption(sheet, row++, 0, "Milk_Quality", ExcelConstants.Milk_Quality);
            }

            if (amcuConfig.getAllowNumberOfCans()) {
                addCaption(sheet, row++, 0, "Number_Of_Cans", ExcelConstants.Number_Of_Cans);
            }
            if (amcuConfig.getAllowRouteInReport()) {
                addCaption(sheet, row++, 0, "Route", ExcelConstants.Route);
            }
            addCaption(sheet, row++, 0, "Rate", ExcelConstants.Rate);


            if ((amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint())
                    && !(amcuConfig.getMaManual()
                    || amcuConfig.getWsManual())) {
                addCaption(sheet, row++, 0, "Bonus", ExcelConstants.Bonus);
                addCaption(sheet, row++, 0, "Total_Amount", ExcelConstants.Total_Amount);
                if (amcuConfig.getEnableIncentiveInReport()) {
                    addCaption(sheet, row++, 0, "Incentive", ExcelConstants.Incentive);
                } else {
                    addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
                }

                addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);

            } else if ((amcuConfig.getMaManual() || amcuConfig.getWsManual())
                    && (amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint())) {
                addCaption(sheet, row++, 0, "Bonus", ExcelConstants.Bonus);
                addCaption(sheet, row++, 0, "Total_Amount", ExcelConstants.Total_Amount);
                if (amcuConfig.getEnableIncentiveInReport()) {
                    addCaption(sheet, row++, 0, "Incentive", ExcelConstants.Incentive);
                } else {
                    addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
                }
                addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);

                addCaption(sheet, row++, 0, "Quality_Mode", ExcelConstants.Quality_Mode);
                addCaption(sheet, row++, 0, "Quantity_Mode", ExcelConstants.Quantity_Mode);

                if (amcuConfig.getEditableRate()) {
                    addCaption(sheet, row++, 0, "Rate_Mode", ExcelConstants.Rate_Mode);
                }

            } else if ((amcuConfig.getMaManual() || amcuConfig.getWsManual())) {
                addCaption(sheet, row++, 0, "Amount", ExcelConstants.Amount);
                if (amcuConfig.getEnableIncentiveInReport()) {
                    addCaption(sheet, row++, 0, "Incentive", ExcelConstants.Incentive);
                } else {
                    addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
                }
                addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);
                addCaption(sheet, row++, 0, "Quality_Mode", ExcelConstants.Quality_Mode);
                addCaption(sheet, row++, 0, "Quantity_Mode", ExcelConstants.Quantity_Mode);

                if (amcuConfig.getEditableRate()) {
                    addCaption(sheet, row++, 0, "Rate_Mode", ExcelConstants.Rate_Mode);
                }
            } else {
                addCaption(sheet, row++, 0, "Amount", ExcelConstants.Amount);
                if (amcuConfig.getEnableIncentiveInReport()) {
                    addCaption(sheet, row++, 0, "Incentive", ExcelConstants.Incentive);
                } else {
                    addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
                }
                addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);
                if (amcuConfig.getEditableRate()) {
                    addCaption(sheet, row++, 0, "Rate_Mode", ExcelConstants.Rate_Mode);
                }

            }

            if (amcuConfig.getEnableCenterCollection()) {
                addCaption(sheet, row++, 0, "CollectionType", ExcelConstants.CollectionType);
            }

        } else if (checkReport == ExcelConstants.TYPE_PRINT_REPORT) {

            addCaption(sheet, 0, 0, "TXN", ExcelConstants.Amount);
            addCaption(sheet, 1, 0, "Date", ExcelConstants.Date);
            addCaption(sheet, 2, 0, "Milk_Type", ExcelConstants.Milk_Type);
            addCaption(sheet, 3, 0, "Fat", ExcelConstants.Fat);
            addCaption(sheet, 4, 0, "Snf", ExcelConstants.Snf);
            addCaption(sheet, 5, 0, getTheUnit(context, UNIT_HEADER), ExcelConstants.Quantity_L);
            addCaption(sheet, 6, 0, "Rate", ExcelConstants.Rate);
            addCaption(sheet, 7, 0, "Amount", ExcelConstants.Amount);

        } else if (checkReport == ExcelConstants.TYPE_RATE_CHART) {
            addCaption(sheet, 0, 0, "Snf/fat", ExcelConstants.Amount);

            // addCaption(sheet, 1, 0, "Fat");
            // addCaption(sheet, 2, 0, "Rate");
        } else if (checkReport == ExcelConstants.TYPE_MEMBER_BILL_REPORT) {
            addCaption(sheet, 0, 0, "Member", ExcelConstants.Member);
            addCaption(sheet, 1, 0, "Quantity", ExcelConstants.Amount);
            addCaption(sheet, 2, 0, "Rate", ExcelConstants.Amount);
            addCaption(sheet, 3, 0, "Amount", ExcelConstants.Amount);
            addCaption(sheet, 4, 0, "Sign", ExcelConstants.Amount);

        } else if (checkReport == ExcelConstants.TYPE_AVERAGE_REPORT) {

            addCaption(sheet, 1, 0, "Sunday", ExcelConstants.Amount);
            addCaption(sheet, 2, 0, "Monday", ExcelConstants.Amount);
            addCaption(sheet, 3, 0, "Tuesday", ExcelConstants.Amount);
            addCaption(sheet, 4, 0, "Wednesday", ExcelConstants.Amount);
            addCaption(sheet, 5, 0, "Thursday", ExcelConstants.Amount);
            addCaption(sheet, 6, 0, "Friday", ExcelConstants.Amount);
            addCaption(sheet, 7, 0, "Saturday", ExcelConstants.Amount);

            addCaption(sheet, 0, 1, "No of Farmers", ExcelConstants.Amount);
            addCaption(sheet, 0, 2, "Quantity", ExcelConstants.Amount);
            addCaption(sheet, 0, 3, "Kg FAT", ExcelConstants.Amount);
            addCaption(sheet, 0, 4, "Kg SNF", ExcelConstants.Amount);
            addCaption(sheet, 0, 5, "Agg. FAT%", ExcelConstants.Amount);
            addCaption(sheet, 0, 6, "Agg. SNF%", ExcelConstants.Amount);
            addCaption(sheet, 0, 7, "Amount", ExcelConstants.Amount);

        } else if (checkReport == ExcelConstants.TYPE_AVERAGE_REPORT) {

            addCaption(sheet, 0, 0, "Date", ExcelConstants.Amount);
            addCaption(sheet, 1, 0, "No Of Farmers", ExcelConstants.Amount);
            addCaption(sheet, 2, 0, "Quantity", ExcelConstants.Amount);
            addCaption(sheet, 3, 0, "Kg FAT", ExcelConstants.Amount);
            addCaption(sheet, 4, 0, "Kg SNF", ExcelConstants.Amount);
            addCaption(sheet, 5, 0, "Agg. FAT%", ExcelConstants.Amount);
            addCaption(sheet, 6, 0, "Agg. SNF%", ExcelConstants.Amount);
            addCaption(sheet, 7, 0, "Amount", ExcelConstants.Amount);

        } else if (checkReport == ExcelConstants.TYPE_FARMER) {

            //This is for farmer

            addCaption(sheet, 0, 0, "Member_Id", ExcelConstants.Member_ID);
            addCaption(sheet, 1, 0, "name", ExcelConstants.Member);
            addCaption(sheet, 2, 0, "Barcode", ExcelConstants.Barcode);
            addCaption(sheet, 3, 0, "mobile", ExcelConstants.Mobile);
            addCaption(sheet, 4, 0, "CattleType", ExcelConstants.Milk_Type);
            addCaption(sheet, 5, 0, "Num_Of_Cans", ExcelConstants.Number_Of_Cans);

        } else if (checkReport == ExcelConstants.TYPE_PERIODIC_REPORT) {
            //This is for periodic report

            int row = 0;
            if (amcuConfig.getEnableSequenceNumberInReport()) {
                addCaption(sheet, row++, 0, "Sequence_Number", ExcelConstants.Sequence_Number);
                addCaption(sheet, row++, 0, "Society_ID", ExcelConstants.Society_ID);
            } else {
                addCaption(sheet, row++, 0, "Society_ID", ExcelConstants.Society_ID);
            }
            addCaption(sheet, row++, 0, "Member_ID", ExcelConstants.Member_ID);
            if (amcuConfig.getAllowSequenceNumberInPrintAndReport()) {
                addCaption(sheet, row++, 0, "Serial_number", ExcelConstants.Serial_number);
            }
            addCaption(sheet, row++, 0, "Member", ExcelConstants.Member);
            addCaption(sheet, row++, 0, "Date", ExcelConstants.Date);
            addCaption(sheet, row++, 0, "Time", ExcelConstants.Time);
            addCaption(sheet, row++, 0, "Shift", ExcelConstants.Shift);
            addCaption(sheet, row++, 0, "Milk_Type", ExcelConstants.Milk_Type);
            addCaption(sheet, row++, 0, "Fat", ExcelConstants.Fat);
            addCaption(sheet, row++, 0, "Snf", ExcelConstants.Snf);
            if (amcuConfig.getEnableClrInReport() || amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") ||
                    amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
                addCaption(sheet, row++, 0, "Clr", ExcelConstants.Clr);

            }
            addCaption(sheet, row++, 0, "AWM", ExcelConstants.AWM);
            addCaption(sheet, row++, 0, "temperature", ExcelConstants.Temp);

            addCaption(sheet, row++, 0, getTheUnit(context, UNIT_HEADER), ExcelConstants.Quantity_L);
            if (amcuConfig.getAllowMilkquality()) {
                addCaption(sheet, row++, 0, "Milk_Quality", ExcelConstants.Milk_Quality);
            }
            if (amcuConfig.getAllowNumberOfCans()) {
                addCaption(sheet, row++, 0, "Number_Of_Cans", ExcelConstants.Number_Of_Cans);
            }
            if (amcuConfig.getAllowRouteInReport()) {
                addCaption(sheet, row++, 0, "Route", ExcelConstants.Route);
            }
            addCaption(sheet, row++, 0, "Rate", ExcelConstants.Rate);

            if ((amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint())
                    && !(amcuConfig.getMaManual() || amcuConfig.getWsManual())) {
                addCaption(sheet, row++, 0, "Bonus", ExcelConstants.Bonus);
                addCaption(sheet, row++, 0, "Total_Amount", ExcelConstants.Total_Amount);

                if (amcuConfig.getEnableIncentiveInReport()) {
                    addCaption(sheet, row++, 0, "Incentive", ExcelConstants.Incentive);
                } else {
                    addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
                }

                addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);

            } else if ((amcuConfig.getMaManual() || amcuConfig.getWsManual())
                    && (amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint())) {
                addCaption(sheet, row++, 0, "Bonus", ExcelConstants.Bonus);
                addCaption(sheet, row++, 0, "Total_Amount", ExcelConstants.Total_Amount);
                if (amcuConfig.getEnableIncentiveInReport()) {
                    addCaption(sheet, row++, 0, "Incentive", ExcelConstants.Incentive);
                } else {
                    addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
                }
                addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);
                addCaption(sheet, row++, 0, "Quality_Mode", ExcelConstants.Quality_Mode);
                addCaption(sheet, row++, 0, "Quantity_Mode", ExcelConstants.Quality_Mode);

                if (amcuConfig.getEditableRate()) {
                    addCaption(sheet, row++, 0, "Rate_Mode", ExcelConstants.Rate_Mode);
                }
            }
//No bonus
            else if ((amcuConfig.getMaManual() || amcuConfig.getWsManual())) {
                addCaption(sheet, row++, 0, "Amount", ExcelConstants.Amount);
                if (amcuConfig.getEnableIncentiveInReport()) {
                    addCaption(sheet, row++, 0, "Incentive", ExcelConstants.Incentive);
                } else {
                    addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
                }
                addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);
                addCaption(sheet, row++, 0, "Quality_Mode", ExcelConstants.Quality_Mode);
                addCaption(sheet, row++, 0, "Quantity_Mode", ExcelConstants.Quantity_Mode);

                if (amcuConfig.getEditableRate()) {
                    addCaption(sheet, row++, 0, "Rate_Mode", ExcelConstants.Rate_Mode);
                }
            } else {
                addCaption(sheet, row++, 0, "Amount", ExcelConstants.Amount);
                if (amcuConfig.getEnableIncentiveInReport()) {
                    addCaption(sheet, row++, 0, "Incentive", ExcelConstants.Incentive);
                } else {
                    addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
                }
                addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);

                if (amcuConfig.getEditableRate()) {
                    addCaption(sheet, row++, 0, "Rate_Mode", ExcelConstants.Rate_Mode);
                }
            }

            if (amcuConfig.getEnableCenterCollection()) {
                addCaption(sheet, row++, 0, "CollectionType", ExcelConstants.CollectionType);
            }
        } else if (checkReport == ExcelConstants.TYPE_SALES_RECORD) {
            //This is for sales report
            int row = 0;
            addCaption(sheet, row++, 0, "Sequence_Number", ExcelConstants.Sequence_Number);
            addCaption(sheet, row++, 0, "Society_ID", ExcelConstants.Society_ID);
            addCaption(sheet, row++, 0, "Member", ExcelConstants.Member);
            addCaption(sheet, row++, 0, "Date", ExcelConstants.Date);
            addCaption(sheet, row++, 0, "Time", ExcelConstants.Time);
            addCaption(sheet, row++, 0, "Shift", ExcelConstants.Shift);
            addCaption(sheet, row++, 0, "Milk_Type", ExcelConstants.Milk_Type);
            addCaption(sheet, row++, 0, "Fat", ExcelConstants.Fat);
            addCaption(sheet, row++, 0, "Snf", ExcelConstants.Snf);
            addCaption(sheet, row++, 0, "AWM", ExcelConstants.AWM);
            addCaption(sheet, row++, 0, "temperature", ExcelConstants.Temp);
            addCaption(sheet, row++, 0, getTheUnit(context, UNIT_HEADER), ExcelConstants.Quantity_L);
            addCaption(sheet, row++, 0, "Rate", ExcelConstants.Rate);
            addCaption(sheet, row++, 0, "Amount", ExcelConstants.Amount);
            addCaption(sheet, row++, 0, "Mode", ExcelConstants.Mode);
            addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);
            addCaption(sheet, row++, 0, "Quality_Mode", ExcelConstants.Quality_Mode);
            addCaption(sheet, row++, 0, "Quantity_Mode", ExcelConstants.Quantity_Mode);


        } else if (checkReport == ExcelConstants.TYPE_DAIRY_REPORT) {
            //Dairy report header as per goa requirement
            addCaption(sheet, 0, 0, "Date", ExcelConstants.Date);
            addCaption(sheet, 1, 0, "Shift", ExcelConstants.Shift);
            addCaption(sheet, 2, 0, "Cattle_type", ExcelConstants.CattleType);
            addCaption(sheet, 3, 0, "Qty_milk_purchased", ExcelConstants.Member_ID);
            addCaption(sheet, 4, 0, "Amt_milk_purchased", ExcelConstants.Member_ID);
            addCaption(sheet, 5, 0, "Qty_milk_sold", ExcelConstants.Member_ID);
            addCaption(sheet, 6, 0, "Amt_milk_sold", ExcelConstants.Member_ID);
            addCaption(sheet, 7, 0, "Milk_to_dairy", ExcelConstants.Member_ID);


        } else if (checkReport == 13) {
            //Total milk bill summary per farmer
            addCaption(sheet, 0, 0, "Code", ExcelConstants.Member_ID);
            addCaption(sheet, 1, 0, "Total_Qty", ExcelConstants.Total_Amount);
            addCaption(sheet, 2, 0, "Avg_Rate", ExcelConstants.Member_ID);
            addCaption(sheet, 3, 0, "Total_Amount", ExcelConstants.Member_ID);
            addCaption(sheet, 4, 0, "Sign", ExcelConstants.Member_ID);

        } else if (checkReport == ExcelConstants.TYPE_CHILLING_RECORD) {
            // 1 for end shift and 8 for daily shift report

            int row = 0;
            if (amcuConfig.getEnableSequenceNumberInReport()) {
                addCaption(sheet, row++, 0, "Sequence_Number", ExcelConstants.Sequence_Number);
                addCaption(sheet, row++, 0, "ChillingId", ExcelConstants.Member_ID);
            } else {
                addCaption(sheet, row++, 0, "ChillingId", ExcelConstants.Member_ID);
            }


            addCaption(sheet, row++, 0, "CenterId", ExcelConstants.Member_ID);
            addCaption(sheet, row++, 0, "CenterName", ExcelConstants.Member);
            addCaption(sheet, row++, 0, "Date", ExcelConstants.Date);
            addCaption(sheet, row++, 0, "Time", ExcelConstants.Time);
            addCaption(sheet, row++, 0, "Shift", ExcelConstants.Shift);
            addCaption(sheet, row++, 0, "Milk_Type", ExcelConstants.Milk_Type);
            addCaption(sheet, row++, 0, "Fat", ExcelConstants.Fat);
            addCaption(sheet, row++, 0, "Snf", ExcelConstants.Snf);

            addCaption(sheet, row++, 0, getTheUnit(context, UNIT_HEADER), ExcelConstants.Quantity_L);
            addCaption(sheet, row++, 0, "Rate", ExcelConstants.Rate);
            addCaption(sheet, row++, 0, "Amount", ExcelConstants.Amount);
            addCaption(sheet, row++, 0, "Status", ExcelConstants.Status);

            addCaption(sheet, row++, 0, "QualityMode", ExcelConstants.Quality_Mode);
            addCaption(sheet, row++, 0, "QuantityMode", ExcelConstants.Quantity_Mode);
        }
    }

    private void addCaption(WritableSheet sheet, int column, int row, String s, int length)
            throws RowsExceededException, WriteException {

        Label label;
        label = new Label(column, row, s, timesBoldUnderline);
        sheet.addCell(label);

        sheet.setColumnView(column, length);
    }


    private void addLabel(WritableSheet sheet, int column, int row, String s)
            throws WriteException, RowsExceededException {
        Label label;
        label = new Label(column, row, s, times);
        sheet.addCell(label);
    }

    private void getAllReport() {
        allReportEntity = new ArrayList<>();
        try {
            allReportEntity = collectionRecordDao.findAllByCollectionType(Util.REPORT_TYPE_FARMER);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getFatSnf() {

        final DatabaseHandler db = DatabaseHandler.getDatabaseInstance();

        new Thread(new Runnable() {

            @Override
            public void run() {
                arrFat = db.getMinandMaxFat(
                );
                arrSnf = db.getMinandMaxSNF(
                );
            }
        }).start();
    }

    public void getRate() {
        for (int i = 0; i < allRateChartEnt.size(); i++) {
            arrRate.add(String.valueOf(allRateChartEnt.get(i).rate));
        }
    }


    private String getTheUnit(Context ctx, int check) {

        AmcuConfig amcuConfig = AmcuConfig.getInstance();
        if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {

            if (check == UNIT_HEADER) {
                return "Quantity_L";
            } else {
                return "Average_Ltrs";
            }


        } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            if (check == UNIT_HEADER) {
                return "Quantity_Kg";
            } else {
                return "Average_Kgs";
            }
        } else if (amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            if (check == UNIT_HEADER) {
                return "Quantity_Kg";
            } else {
                return "Avgrate_Kgs";
            }
        } else {
            if (check == UNIT_HEADER) {
                return "Quantity_L";
            } else {
                return "Average_Ltrs";
            }
        }
    }

    private String getIncentive(String amount) {
        DecimalFormat decimalFormatRate = new DecimalFormat("#0.00");
        double amt = 0.00, incentiveAmt = 0.00, inCentivePer = 0;
        try {
            amt = Double.parseDouble(amount);
            inCentivePer = amcuConfig.getIncentivePercentage();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            amt = 0;
        }
        incentiveAmt = (amt * inCentivePer) / 100;
        return decimalFormatRate.format(incentiveAmt);
    }


    private void setCenterSummaryDetails(WritableSheet sheet, int count) throws WriteException {

        AverageReportDetail avgReportdetail = Util.avgChillingDetails;
        if (avgReportdetail != null) {
            count = count + 2;

            addLabel(sheet, 0, count + 2, "Total_Centers");
            addLabel(sheet, 0, count + 3, "Total_Accepted");
            addLabel(sheet, 0, count + 4, "Total_Rejected");

            addLabel(sheet, 0, count + 5, "Average_Amount");
            addLabel(sheet, 0, count + 6, getTheUnit(context, UNIT_BODY));
            addLabel(sheet, 0, count + 7, "Average_Rate");

            addLabel(sheet, 0, count + 8, "Average_Fat");
            addLabel(sheet, 0, count + 9, "Average_Snf");
            addLabel(sheet, 0, count + 10, "Total_Qty");
            addLabel(sheet, 0, count + 11, "Total_Amount");

            addLabel(sheet, 1, count + 2, String.valueOf(avgReportdetail.totalMember));
            addLabel(sheet, 1, count + 3,
                    String.valueOf(avgReportdetail.totalAcceptedEntries));
            addLabel(sheet, 1, count + 4,
                    String.valueOf(avgReportdetail.totalRejectedEntries));
            addLabel(sheet, 1, count + 5, String.valueOf(avgReportdetail.avgAmount));
            addLabel(sheet, 1, count + 6, String.valueOf(avgReportdetail.avgQuantity));
            addLabel(sheet, 1, count + 7, String.valueOf(avgReportdetail.avgRate));
            addLabel(sheet, 1, count + 8, String.valueOf(avgReportdetail.avgFat));
            addLabel(sheet, 1, count + 9, String.valueOf(avgReportdetail.avgSnf));
            addLabel(sheet, 1, count + 10, String.valueOf(avgReportdetail.totalQuantity));
            addLabel(sheet, 1, count + 11, String.valueOf(avgReportdetail.totalAmount));
        }
    }

    private void createContent(WritableSheet sheet) throws WriteException,
            RowsExceededException {

        if (checkReport == 0) {
            // now a bit of text
            for (int i = 0; i < allReportEntity.size(); i++) {
                // First column
                addLabel(sheet, 0, i, allReportEntity.get(i).farmerId);
                addLabel(sheet, 1, i, allReportEntity.get(i).farmerName);
                addLabel(sheet, 2, i, String.valueOf(allReportEntity.get(i).snf));
                addLabel(sheet, 3, i, String.valueOf(allReportEntity.get(i).fat));
                addLabel(sheet, 4, i, String.valueOf(allReportEntity.get(i).rate));
                addLabel(sheet, 5, i, allReportEntity.get(i).manual);
            }
        } else if (checkReport == ExcelConstants.TYPE_PERIODIC_REPORT &&
                allDetReportEntity != null && allDetReportEntity.size() > 0) {
            //This is for periodic report
            int count = 0;

            for (int i = 1; i < allDetReportEntity.size(); i++) {
                // First column
                int row = 0;

                if (amcuConfig.getEnableSequenceNumberInReport()) {
                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).sequenceNum));
                    addLabel(sheet, row++, i,
                            session.getCollectionID());
                } else {
                    addLabel(sheet, row++, i,
                            session.getCollectionID());
                }


                addLabel(sheet, row++, i,
                        allDetReportEntity.get(i).farmerId);
                if (amcuConfig.getAllowSequenceNumberInPrintAndReport()) {
                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).sampleNumber));
                }
                addLabel(sheet, row++, i,
                        allDetReportEntity.get(i).farmerName);
                addLabel(sheet, row++, i, allDetReportEntity.get(i).postDate);
                addLabel(sheet, row++, i, allDetReportEntity.get(i).time);
                addLabel(sheet, row++, i, allDetReportEntity.get(i).postShift);
                addLabel(sheet, row++, i, allDetReportEntity.get(i).milkType);
                addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).fat));
                addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).snf));

                if (amcuConfig.getEnableClrInReport() || amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") ||
                        amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).clr));
                }

                addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).awm));
                addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).temp));

                addLabel(sheet, row++, i,
                        String.valueOf(allDetReportEntity.get(i).quantity));


                if (amcuConfig.getAllowMilkquality()) {
                    addLabel(sheet, row++, i, allDetReportEntity.get(i).milkQuality);
                }

                if (amcuConfig.getAllowNumberOfCans()) {
                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).numberOfCans));
                }
                if (amcuConfig.getAllowRouteInReport()) {
                    //get Data of route from TABLE_CHILLING_CENTER by farmerid

                    if (null != allDetReportEntity.get(i).centerRoute && !allDetReportEntity.get(i).centerRoute.equalsIgnoreCase("")
                            && !allDetReportEntity.get(i).centerRoute.equalsIgnoreCase("null")) {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i).centerRoute);
                    } else {
                        addLabel(sheet, row++, i, "NA");
                    }
                }
                addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).rate));


                if ((amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint())
                        && !(amcuConfig.getMaManual() || amcuConfig.getWsManual())) {
                    try {

                        addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).bonus));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        addLabel(sheet, row++, i, "0.00");
                    }
                    addLabel(sheet, row++, i, String.valueOf(Util.getAmount(this.context, allDetReportEntity.get(i).getTotalAmount(), allDetReportEntity.get(i).bonus)));
                    if (amcuConfig.getEnableIncentiveInReport()) {
                        addLabel(sheet, row++, i, getIncentive(
                                String.valueOf(Util.getAmount(this.context, allDetReportEntity.get(i).getTotalAmount(),
                                        allDetReportEntity.get(i).bonus))));
                    } else {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i).manual);
                    }

                    addLabel(sheet, row++, i, allDetReportEntity.get(i).status);


                } else if ((amcuConfig.getMaManual() || amcuConfig.getWsManual()) &&
                        (amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint())) {
                    try {

                        addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i).bonus));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        addLabel(sheet, row++, i, "0.00");
                    }
                    addLabel(sheet, row++, i,
                            String.valueOf(
                                    Util.getAmount(this.context, allDetReportEntity.get(i).getTotalAmount(),
                                            allDetReportEntity.get(i).bonus)));
                    if (amcuConfig.getEnableIncentiveInReport()) {
                        addLabel(sheet, row++, i, getIncentive(
                                String.valueOf(Util.getAmount(this.context, allDetReportEntity.get(i).getTotalAmount(), allDetReportEntity.get(i).bonus))));
                    } else {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i).manual);
                    }

                    addLabel(sheet, row++, i, allDetReportEntity.get(i).status);


                    addLabel(sheet, row++, i, allDetReportEntity.get(i).qualityMode);
                    addLabel(sheet, row++, i, allDetReportEntity.get(i).quantityMode);

                    if (amcuConfig.getEditableRate()) {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i).rateMode);

                    }


                } else if ((amcuConfig.getMaManual() || amcuConfig.getWsManual())) {
                    addLabel(sheet, row++, i, String.valueOf(Util.getAmount(this.context,
                            allDetReportEntity.get(i).getTotalAmount(),
                            allDetReportEntity.get(i).bonus)));

                    if (amcuConfig.getEnableIncentiveInReport()) {
                        addLabel(sheet, row++, i, getIncentive(
                                String.valueOf(Util.getAmount(this.context, allDetReportEntity.get(i).getTotalAmount(),
                                        allDetReportEntity.get(i).bonus))));
                    } else {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i).manual);
                    }


                    addLabel(sheet, row++, i, allDetReportEntity.get(i).status);

                    addLabel(sheet, row++, i, allDetReportEntity.get(i).qualityMode);
                    addLabel(sheet, row++, i, allDetReportEntity.get(i).quantityMode);
                    if (amcuConfig.getEditableRate()) {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i).rateMode);

                    }


                } else {
                    addLabel(sheet, row++, i, String.valueOf(Util.getAmount(this.context,
                            allDetReportEntity.get(i).getTotalAmount(), allDetReportEntity.get(i).bonus)));
                    if (amcuConfig.getEnableIncentiveInReport()) {
                        addLabel(sheet, row++, i, getIncentive(String.valueOf(
                                Util.getAmount(this.context, allDetReportEntity.get(i).getTotalAmount(), allDetReportEntity.get(i).bonus))));
                    } else {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i).manual);
                    }

                    addLabel(sheet, row++, i, allDetReportEntity.get(i).status);
                    if (amcuConfig.getEditableRate()) {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i).rateMode);

                    }

                }

                if (amcuConfig.getEnableCenterCollection()) {
                    addLabel(sheet, row++, i, allDetReportEntity.get(i).collectionType);
                }

                count = count + 1;
            }
            AverageReportDetail avgReportdetail = Util.getAverageData();

            if (avgReportdetail != null && checkNumberOfRecord(avgReportdetail) > 0) {
                count = count + 2;
                if (session.getReportType() == Util.PERIODICREPORTINDIVIDUAL) {
                    addLabel(sheet, 0, count + 2, "Total_Records");
                } else {
                    addLabel(sheet, 0, count + 2, "Total_Farmers");
                }
                addLabel(sheet, 0, count + 3, "Total_Accepted");
                addLabel(sheet, 0, count + 4, "Total_Rejected");
                addLabel(sheet, 0, count + 5, "Total_Test");

                addLabel(sheet, 0, count + 6, "Average_Amount");
                addLabel(sheet, 0, count + 7, getTheUnit(context, UNIT_BODY));
                addLabel(sheet, 0, count + 8, "Average_Rate");

                addLabel(sheet, 0, count + 9, "Average_Fat");
                addLabel(sheet, 0, count + 10, "Average_Snf");
                addLabel(sheet, 0, count + 11, "Total_Qty");
                addLabel(sheet, 0, count + 12, "Total_Amount");

                addLabel(sheet, 1, count + 2, String.valueOf(avgReportdetail.totalMember));
                addLabel(sheet, 1, count + 3,
                        String.valueOf(avgReportdetail.totalAcceptedEntries));
                addLabel(sheet, 1, count + 4,
                        String.valueOf(avgReportdetail.totalRejectedEntries));
                addLabel(sheet, 1, count + 5, String.valueOf(String.valueOf(avgReportdetail.totalTestEntries)));

                addLabel(sheet, 1, count + 6, String.valueOf(avgReportdetail.avgAmount));
                addLabel(sheet, 1, count + 7, String.valueOf(avgReportdetail.avgQuantity));
                addLabel(sheet, 1, count + 8, String.valueOf(avgReportdetail.avgRate));

                addLabel(sheet, 1, count + 9, String.valueOf(avgReportdetail.avgFat));
                addLabel(sheet, 1, count + 10, String.valueOf(avgReportdetail.avgSnf));
                addLabel(sheet, 1, count + 11, String.valueOf(avgReportdetail.totalQuantity));
                addLabel(sheet, 1, count + 12, String.valueOf(avgReportdetail.totalAmount));
                count = count + 12;
            }

            if (amcuConfig.getEnableCenterCollection() &&
                    Util.avgChillingDetails != null && checkNumberOfRecord(Util.avgChillingDetails) > 0) {
                setCenterSummaryDetails(sheet, count);
            }
        }


        //For sales

        else if (checkReport == ExcelConstants.TYPE_SALES_RECORD && allSalesRecordEnt != null && allSalesRecordEnt.size() > 0) {
            int count = 0;

            for (int i = 0; i < allSalesRecordEnt.size(); i++) {

                // First column
                int row = 0;
                addLabel(sheet, row++, i + 1, String.valueOf(allSalesRecordEnt.get(i).sequenceNumber));
                addLabel(sheet, row++, i + 1,
                        session.getCollectionID());

                addLabel(sheet, row++, i + 1,
                        allSalesRecordEnt.get(i).name);
                addLabel(sheet, row++, i + 1, allSalesRecordEnt.get(i).postDate);
                addLabel(sheet, row++, i + 1, allSalesRecordEnt.get(i).time);
                addLabel(sheet, row++, i + 1, allSalesRecordEnt.get(i).postShift);
                addLabel(sheet, row++, i + 1, allSalesRecordEnt.get(i).milkType);
                addLabel(sheet, row++, i + 1, String.valueOf(allSalesRecordEnt.get(i).fat));
                addLabel(sheet, row++, i + 1, String.valueOf(allSalesRecordEnt.get(i).snf));
                addLabel(sheet, row++, i + 1, String.valueOf(allSalesRecordEnt.get(i).awm));
                addLabel(sheet, row++, i + 1, String.valueOf(allSalesRecordEnt.get(i).temperature));
                addLabel(sheet, row++, i + 1,
                        String.valueOf(allSalesRecordEnt.get(i).Quantity));
                addLabel(sheet, row++, i + 1, String.valueOf(allSalesRecordEnt.get(i).rate));

                addLabel(sheet, row++, i + 1, String.valueOf(allSalesRecordEnt.get(i).amount));
                addLabel(sheet, row++, i + 1, allSalesRecordEnt.get(i).manual);
                addLabel(sheet, row++, i + 1, allSalesRecordEnt.get(i).status);

                addLabel(sheet, row++, i + 1, allSalesRecordEnt.get(i).milkoManual);
                addLabel(sheet, row++, i + 1, allSalesRecordEnt.get(i).weightManual);

                count = count + 1;
            }
            AverageReportDetail avgReportdetail = Util.getSalesAverageData();

            if (avgReportdetail != null) {
                count = count + 2;

                if (session.getReportType() == Util.PERIODICREPORTINDIVIDUAL) {
                    addLabel(sheet, 0, count + 2, "Total_Sales");
                } else {
                    addLabel(sheet, 0, count + 2, "Total_Sales");
                }


                addLabel(sheet, 0, count + 3, "Average_Amount");
                addLabel(sheet, 0, count + 4, getTheUnit(context, UNIT_BODY));
                addLabel(sheet, 0, count + 5, "Average_Rate");

                addLabel(sheet, 0, count + 6, "Average_Fat");
                addLabel(sheet, 0, count + 7, "Average_Snf");
                addLabel(sheet, 0, count + 8, "Total_Qty");
                addLabel(sheet, 0, count + 9, "Total_Amount");
                addLabel(sheet, 1, count + 2, String.valueOf(avgReportdetail.totalMember));
                addLabel(sheet, 1, count + 3, String.valueOf(avgReportdetail.avgAmount));
                addLabel(sheet, 1, count + 4, String.valueOf(avgReportdetail.avgQuantity));
                addLabel(sheet, 1, count + 5, String.valueOf(avgReportdetail.avgRate));

                addLabel(sheet, 1, count + 6, String.valueOf(avgReportdetail.avgFat));
                addLabel(sheet, 1, count + 7, String.valueOf(avgReportdetail.avgSnf));
                addLabel(sheet, 1, count + 8, String.valueOf(avgReportdetail.totalQuantity));
                addLabel(sheet, 1, count + 9, String.valueOf(avgReportdetail.totalAmount));

            }
        } else if (checkReport == ExcelConstants.TYPE_SHIFT_REPORT) {
            // this is for daily report and endshift report
            int count = 0;

            if (checkReport == ExcelConstants.TYPE_SHIFT_REPORT) {
                for (int i = 1; i <= allDetReportEntity.size(); i++) {
                    // First column

                    int row = 0;

                    if (amcuConfig.getEnableSequenceNumberInReport()) {
                        addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).sequenceNum));
                        addLabel(sheet, row++, i,
                                session.getCollectionID());
                    } else {
                        addLabel(sheet, row++, i,
                                session.getCollectionID());
                    }


                    addLabel(sheet, row++, i,
                            allDetReportEntity.get(i - 1).farmerId);
                    if (amcuConfig.getAllowSequenceNumberInPrintAndReport()) {
                        addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).sampleNumber));
                    }

                    addLabel(sheet, row++, i,
                            allDetReportEntity.get(i - 1).farmerName);
                    addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).postDate);
                    addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).time);
                    addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).postShift);
                    addLabel(sheet, row++, i,
                            allDetReportEntity.get(i - 1).milkType);
                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).fat));
                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).snf));

                    if (amcuConfig.getEnableClrInReport() || amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") ||
                            amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
                        addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).clr));
                    }

                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).awm));

                    // addLabel(sheet, 7, i, allDetReportEntity.get(i - 1).clr);
                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).temp));

                    addLabel(sheet, row++, i,
                            String.valueOf(allDetReportEntity.get(i - 1).quantity));


                    if (amcuConfig.getAllowMilkquality()) {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).milkQuality);
                    }

                    if (amcuConfig.getAllowNumberOfCans()) {
                        addLabel(sheet, row++, i, String.valueOf(
                                allDetReportEntity.get(i - 1).numberOfCans));
                    }
                    if (amcuConfig.getAllowRouteInReport()) {
                        //get Data of route from TABLE_CHILLING_CENTER by farmerid

                        if (null != allDetReportEntity.get(i - 1).centerRoute &&
                                !allDetReportEntity.get(i - 1).centerRoute.equalsIgnoreCase("") && !allDetReportEntity.get(i - 1).centerRoute.equalsIgnoreCase("null")) {
                            addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).centerRoute);
                        } else {
                            addLabel(sheet, row++, i, "NA");
                        }
                    }

                    addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).rate));

                    if ((amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint()) &&
                            !(amcuConfig.getMaManual() || amcuConfig.getWsManual())) {
                        try {

                            addLabel(sheet, row++, i, String.valueOf(
                                    allDetReportEntity.get(i - 1).bonus));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            addLabel(sheet, row++, i, "0.00");
                        }
                        addLabel(sheet, row++, i,
                                String.valueOf(Util.getAmount(this.context, allDetReportEntity.get(i - 1).getTotalAmount(),
                                        allDetReportEntity.get(i - 1).bonus)));

                        if (amcuConfig.getEnableIncentiveInReport()) {
                            addLabel(sheet, row++, i, getIncentive(
                                    String.valueOf(Util.getAmount(this.context,
                                            allDetReportEntity.get(i - 1).getTotalAmount(), allDetReportEntity.get(i - 1).bonus))));
                        } else {
                            addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).manual);
                        }
                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).status);

                    } else if ((amcuConfig.getMaManual() || amcuConfig.getWsManual()) &&
                            (amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint())) {
                        try {

                            addLabel(sheet, row++, i, String.valueOf(allDetReportEntity.get(i - 1).bonus));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            addLabel(sheet, row++, i, "0.00");
                        }
                        addLabel(sheet, row++, i, String.valueOf(
                                Util.getAmount(this.context, allDetReportEntity.get(i - 1).getTotalAmount(),
                                        allDetReportEntity.get(i - 1).bonus)));
                        if (amcuConfig.getEnableIncentiveInReport()) {
                            addLabel(sheet, row++, i, getIncentive(
                                    String.valueOf(Util.getAmount(this.context,
                                            allDetReportEntity.get(i - 1).getTotalAmount(),
                                            allDetReportEntity.get(i - 1).bonus))));
                        } else {
                            addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).manual);
                        }

                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).status);

                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).qualityMode);
                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).quantityMode);

                        if (amcuConfig.getEditableRate()) {
                            addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).rateMode);

                        }


                    } else if ((amcuConfig.getMaManual() || amcuConfig.getWsManual())) {
                        addLabel(sheet, row++, i,
                                String.valueOf(Util.getAmount(this.context, allDetReportEntity.get(i - 1).getTotalAmount(), allDetReportEntity.get(i - 1).bonus)));

                        if (amcuConfig.getEnableIncentiveInReport()) {
                            addLabel(sheet, row++, i, getIncentive(
                                    String.valueOf(Util.getAmount(this.context, allDetReportEntity.get(i - 1).getTotalAmount(),
                                            allDetReportEntity.get(i - 1).bonus))));
                        } else {
                            addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).manual);
                        }

                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).status);

                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).qualityMode);
                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).quantityMode);

                        if (amcuConfig.getEditableRate()) {
                            addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).rateMode);

                        }


                    } else {
                        addLabel(sheet, row++, i, String.valueOf(
                                Util.getAmount(this.context, allDetReportEntity.get(i - 1).getTotalAmount(),
                                        allDetReportEntity.get(i - 1).bonus)));
                        if (amcuConfig.getEnableIncentiveInReport()) {
                            addLabel(sheet, row++, i, getIncentive(
                                    String.valueOf(Util.getAmount(this.context,
                                            allDetReportEntity.get(i - 1).getTotalAmount(),
                                            allDetReportEntity.get(i - 1).bonus))));
                        } else {
                            addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).manual);
                        }
                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).status);

                        if (amcuConfig.getEditableRate()) {
                            addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).rateMode);

                        }

                    }

                    if (amcuConfig.getEnableCenterCollection()) {
                        addLabel(sheet, row++, i, allDetReportEntity.get(i - 1).collectionType);
                    }

                    count = count + 1;
                }
            }

            //Check chilling center records

            else {
                for (int i = 1; i < allDetReportEntity.size(); i++) {
                    // First column
                    addLabel(sheet, 0, i, allDetReportEntity.get(i).farmerId);
                    addLabel(sheet, 1, i, allDetReportEntity.get(i).farmerName);
                    addLabel(sheet, 2, i, allDetReportEntity.get(i).time);
                    addLabel(sheet, 3, i, allDetReportEntity.get(i).milkType);
                    addLabel(sheet, 4, i, String.valueOf(allDetReportEntity.get(i).fat));
                    addLabel(sheet, 5, i, String.valueOf(allDetReportEntity.get(i).snf));
                    addLabel(sheet, 6, i, String.valueOf(allDetReportEntity.get(i).awm));

                    addLabel(sheet, 7, i, String.valueOf(allDetReportEntity.get(i).quantity));
                    addLabel(sheet, 8, i, String.valueOf(allDetReportEntity.get(i).rate));
                    addLabel(sheet, 9, i, String.valueOf(Util.getAmount(this.context,
                            allDetReportEntity.get(i).getTotalAmount(), allDetReportEntity.get(i).bonus)));
                    addLabel(sheet, 10, i, allDetReportEntity.get(i).manual);

                    addLabel(sheet, 11, i, allDetReportEntity.get(i - 1).status);
                    count = count + 1;
                }
            }

            AverageReportDetail avgReportdetail = Util.getAverageData();

            if (avgReportdetail != null && checkNumberOfRecord(avgReportdetail) > 0) {
                count = count + 2;

                addLabel(sheet, 0, count + 2, "Total_Farmers");
                addLabel(sheet, 0, count + 3, "Total_Accepted");
                addLabel(sheet, 0, count + 4, "Total_Rejected");
                addLabel(sheet, 0, count + 5, "Total_Test");

                addLabel(sheet, 0, count + 6, "Average_Amount");
                addLabel(sheet, 0, count + 7, getTheUnit(context, UNIT_BODY));
                addLabel(sheet, 0, count + 8, "Average_Rate");

                addLabel(sheet, 0, count + 9, "Average_Fat");
                addLabel(sheet, 0, count + 10, "Average_Snf");
                addLabel(sheet, 0, count + 11, "Total_Qty");
                addLabel(sheet, 0, count + 12, "Total_Amount");

                addLabel(sheet, 1, count + 2, String.valueOf(avgReportdetail.totalMember));
                addLabel(sheet, 1, count + 3,
                        String.valueOf(avgReportdetail.totalAcceptedEntries));
                addLabel(sheet, 1, count + 4,
                        String.valueOf(avgReportdetail.totalRejectedEntries));
                addLabel(sheet, 1, count + 5, String.valueOf(avgReportdetail.totalTestEntries));

                addLabel(sheet, 1, count + 6, String.valueOf(avgReportdetail.avgAmount));
                addLabel(sheet, 1, count + 7, String.valueOf(avgReportdetail.avgQuantity));
                addLabel(sheet, 1, count + 8, String.valueOf(avgReportdetail.avgRate));

                addLabel(sheet, 1, count + 9, String.valueOf(avgReportdetail.avgFat));
                addLabel(sheet, 1, count + 10, String.valueOf(avgReportdetail.avgSnf));
                addLabel(sheet, 1, count + 11, String.valueOf(avgReportdetail.totalQuantity));
                addLabel(sheet, 1, count + 12, String.valueOf(avgReportdetail.totalAmount));

                count = count + 12;
            }

            if (amcuConfig.getEnableCenterCollection() &&
                    Util.avgChillingDetails != null && checkNumberOfRecord(Util.avgChillingDetails) > 0) {
                setCenterSummaryDetails(sheet, count);
            }

        } else if (checkReport == ExcelConstants.TYPE_CHILLING_RECORD) {
            // this is for daily report and endshift report
            int count = 0;

            if (checkReport == ExcelConstants.TYPE_CHILLING_RECORD) {
                for (int i = 1; i < allCenterShiftReport.size(); i++) {
                    // First column

                    int row = 0;

                    if (amcuConfig.getEnableSequenceNumberInReport()) {
                        addLabel(sheet, row++, i, allCenterShiftReport.get(i - 1).centerId);
                        addLabel(sheet, row++, i,
                                allCenterShiftReport.get(i - 1).chillingCenterId);
                    } else {
                        addLabel(sheet, row++, i,
                                allCenterShiftReport.get(i - 1).chillingCenterId);
                    }


                    addLabel(sheet, row++, i,
                            allCenterShiftReport.get(i - 1).centerId);
                    addLabel(sheet, row++, i,
                            allCenterShiftReport.get(i - 1).centerName);
                    addLabel(sheet, row++, i, allCenterShiftReport.get(i - 1).date);
                    addLabel(sheet, row++, i, allCenterShiftReport.get(i - 1).time);
                    addLabel(sheet, row++, i, allCenterShiftReport.get(i - 1).shift);
                    addLabel(sheet, row++, i,
                            allCenterShiftReport.get(i - 1).milkType);
                    addLabel(sheet, row++, i, String.valueOf(allCenterShiftReport.get(i - 1).fat));
                    addLabel(sheet, row++, i, String.valueOf(allCenterShiftReport.get(i - 1).snf));


                    addLabel(sheet, row++, i,
                            String.valueOf(allCenterShiftReport.get(i - 1).quantity));
                    addLabel(sheet, row++, i, String.valueOf(allCenterShiftReport.get(i - 1).rate));
                    addLabel(sheet, row++, i, String.valueOf(allCenterShiftReport.get(i - 1).amount));
                    addLabel(sheet, row++, i, allCenterShiftReport.get(i - 1).status);

                    addLabel(sheet, row++, i, allCenterShiftReport.get(i - 1).qualityMode);
                    addLabel(sheet, row++, i, allCenterShiftReport.get(i - 1).quantityMode);
                    count = count + 1;
                }
            } else {
                for (int i = 1; i < allDetReportEntity.size(); i++) {
                    // First column
                    addLabel(sheet, 0, i, allDetReportEntity.get(i).farmerId);
                    addLabel(sheet, 1, i, allDetReportEntity.get(i).farmerName);
                    addLabel(sheet, 2, i, allDetReportEntity.get(i).time);
                    addLabel(sheet, 3, i, allDetReportEntity.get(i).milkType);
                    addLabel(sheet, 4, i, String.valueOf(allDetReportEntity.get(i).fat));
                    addLabel(sheet, 5, i, String.valueOf(allDetReportEntity.get(i).snf));
                    addLabel(sheet, 6, i, String.valueOf(allDetReportEntity.get(i).awm));

                    addLabel(sheet, 7, i, String.valueOf(allDetReportEntity.get(i).quantity));
                    addLabel(sheet, 8, i, String.valueOf(allDetReportEntity.get(i).rate));
                    addLabel(sheet, 9, i, String.valueOf(allDetReportEntity.get(i).amount));
                    addLabel(sheet, 10, i, allDetReportEntity.get(i).manual);

                    addLabel(sheet, 11, i, allDetReportEntity.get(i - 1).status);
                    count = count + 1;
                }
            }


        } else if (checkReport == ExcelConstants.TYPE_PRINT_REPORT) {
            // now a bit of text
            for (int i = 1; i < allDetReportEntity.size(); i++) {
                // First column
                addLabel(sheet, 0, i, String.valueOf(allDetReportEntity.get(i).txnNumber));
                addLabel(sheet, 1, i,
                        allDetReportEntity.get(i).postDate.replace("-", "_"));
                addLabel(sheet, 2, i, String.valueOf(allDetReportEntity.get(i).milkType));
                addLabel(sheet, 3, i, String.valueOf(allDetReportEntity.get(i).fat));
                addLabel(sheet, 4, i, String.valueOf(allDetReportEntity.get(i).snf));
                addLabel(sheet, 5, i, String.valueOf(allDetReportEntity.get(i).quantity));
                addLabel(sheet, 6, i, String.valueOf(allDetReportEntity.get(i).rate));
                addLabel(sheet, 7, i, String.valueOf(allDetReportEntity.get(i).amount));
            }
        }
//To export the farmer list from device
        else if (checkReport == ExcelConstants.TYPE_FARMER) {
//
            try {

                for (int i = 1; i <= allFarmerEntity.size(); i++) {
                    // First column
                    addLabel(sheet, 0, i, allFarmerEntity.get(i - 1).farmer_id);
                    addLabel(sheet, 1, i,
                            allFarmerEntity.get(i - 1).farmer_name);
                    addLabel(sheet, 2, i,
                            allFarmerEntity.get(i - 1).farmer_barcode);

                    addLabel(sheet, 3, i,
                            allFarmerEntity.get(i - 1).farm_mob);
                    addLabel(sheet, 4, i,
                            allFarmerEntity.get(i - 1).farmer_cattle);
                    addLabel(sheet, 5, i, allFarmerEntity.get(i - 1).farmer_cans);
                }

            } catch (Exception e) {
                // TODO: handle exception
            }
        } else if (checkReport == ExcelConstants.TYPE_MEMBER_BILL_REPORT) {

            int count = 0;
            for (int i = 1; i < allDetReportEntity.size(); i++) {
                // First column
                addLabel(sheet, 0, i, allDetReportEntity.get(i).farmerId);
                addLabel(sheet, 1, i, String.valueOf(allDetReportEntity.get(i).quantity));
                addLabel(sheet, 2, i, String.valueOf(allDetReportEntity.get(i).rate));
                addLabel(sheet, 3, i, String.valueOf(allDetReportEntity.get(i).amount));
                addLabel(sheet, 4, i, "  ");
                count = count + 1;
            }

            AverageReportDetail avgReportdetail = Util.getAverageData();

            if (avgReportdetail != null) {
                count = count + 2;
                addLabel(sheet, 0, count + 2, "Number_of_Farmers");
                addLabel(sheet, 0, count + 3, "Average_Fat");
                addLabel(sheet, 0, count + 4, "Average_Snf");
                addLabel(sheet, 0, count + 5, "Total_Collection");
                addLabel(sheet, 0, count + 6, "Total_Amount");
                addLabel(sheet, 1, count + 2, String.valueOf(avgReportdetail.totalMember));
                addLabel(sheet, 1, count + 3, String.valueOf(avgReportdetail.avgFat));
                addLabel(sheet, 1, count + 4, String.valueOf(avgReportdetail.avgSnf));
                addLabel(sheet, 1, count + 5, String.valueOf(avgReportdetail.totalQuantity));
                addLabel(sheet, 1, count + 6, String.valueOf(avgReportdetail.totalAmount));
            }

        } else if (checkReport == ExcelConstants.TYPE_AVERAGE_REPORT) {
            for (int i = 0; i < allAverageReportDetail.size(); i++) {

                addLabel(sheet, i + 1, 1,
                        String.valueOf(allAverageReportDetail.get(i).totalMember));
                addLabel(sheet, i + 1, 2,
                        String.valueOf(allAverageReportDetail.get(i).totalQuantity));
                addLabel(sheet, i + 1, 3, String.valueOf(allAverageReportDetail.get(i).avgFat));
                addLabel(sheet, i + 1, 4, String.valueOf(allAverageReportDetail.get(i).avgSnf));

                addLabel(sheet, i + 1, 5, String.valueOf(allAverageReportDetail.get(i).aggFat));
                addLabel(sheet, i + 1, 6, String.valueOf(allAverageReportDetail.get(i).aggSnf));

                addLabel(sheet, i + 1, 7,
                        String.valueOf(allAverageReportDetail.get(i).totalAmount));

            }
        } else if (checkReport == ExcelConstants.TYPE_AVERAGE_REPORT) {
            for (int i = 0; i < allAverageReportDetail.size(); i++) {
                addLabel(sheet, 0, i + 1, allAverageReportDetail.get(i).date);

                addLabel(sheet, 1, i + 1,
                        String.valueOf(allAverageReportDetail.get(i).totalMember));
                addLabel(sheet, 2, i + 1,
                        String.valueOf(allAverageReportDetail.get(i).totalQuantity));
                addLabel(sheet, 3, i + 1, String.valueOf(allAverageReportDetail.get(i).avgFat));
                addLabel(sheet, 4, i + 1, String.valueOf(allAverageReportDetail.get(i).avgSnf));

                addLabel(sheet, 5, i + 1, String.valueOf(allAverageReportDetail.get(i).aggFat));
                addLabel(sheet, 6, i + 1, String.valueOf(allAverageReportDetail.get(i).aggSnf));

                addLabel(sheet, 7, i + 1,
                        String.valueOf(allAverageReportDetail.get(i).totalAmount));

            }
        } else if (checkReport == ExcelConstants.TYPE_RATE_CHART) {
            for (int l = 0; l < arrSnf.size(); l++) {
                addLabel(sheet, 0, l + 1, arrSnf.get(l));
            }

            for (int l = 0; l < arrFat.size(); l++) {
                addLabel(sheet, l + 1, 0, arrFat.get(l));
            }

            for (int i = 0; i < arrFat.size(); i++) {

                for (int j = 0; j < arrSnf.size(); j++) {

                    long refId = rateChartNameDao.findRateRefIdFromName(amcuConfig.getRateChartName());
                    String rate = rateDao.findRateFromInput(arrFat.get(i), arrSnf.get(j), null, refId);
                    addLabel(sheet, i + 1, j + 1, rate);

                }
            }
        } else if (checkReport == ExcelConstants.TYPE_DAIRY_REPORT) {
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
//
            try {

                for (int i = 1; i <= allDetReportEntity.size(); i++) {
                    // First column
                    addLabel(sheet, 0, i, allDetReportEntity.get(i - 1).postDate);
                    addLabel(sheet, 1, i,
                            allDetReportEntity.get(i - 1).postShift);
                    addLabel(sheet, 2, i,
                            allDetReportEntity.get(i - 1).milkType);

                    addLabel(sheet, 3, i,
                            decimalFormat.format(allDetReportEntity.get(i - 1).quantity));
                    addLabel(sheet, 4, i,
                            decimalFormat.format(allDetReportEntity.get(i - 1).amount));
//TODO handle the below fields
                 /*   addLabel(sheet, 5, i,
                            decimalFormat.format(allDairyReportEnt.get(i - 1).qtySold));
                    addLabel(sheet, 6, i,
                            decimalFormat.format(allDairyReportEnt.get(i - 1).amtSold));

                    addLabel(sheet, 7, i,
                            decimalFormat.format(allDairyReportEnt.get(i - 1).milkTodairy));*/
                }


            } catch (Exception e) {
                // TODO: handle exception
            }
        } else if (checkReport == 13) {

            try {

                for (int i = 1; i <= allDetReportEntity.size(); i++) {
                    // First column
                    addLabel(sheet, 0, i, allDetReportEntity.get(i - 1).farmerId);
                    addLabel(sheet, 1, i,
                            String.valueOf(allDetReportEntity.get(i - 1).quantity));
                    addLabel(sheet, 2, i,
                            String.valueOf(allDetReportEntity.get(i - 1).rate));
                    addLabel(sheet, 3, i, String.valueOf(allDetReportEntity.get(i - 1).amount));
                    addLabel(sheet, 4, i, "");

                }

            } catch (Exception e) {
                // TODO: handle exception
            }
        }

//		try {
//			times.setLocked(true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
    }

    public void setWriteCompleteListener(OnExcelWriteCompleteListener
                                                 onExcelWriteCompleteListener) {
        this.onExcelWriteCompleteListener = onExcelWriteCompleteListener;
    }


    public interface OnExcelWriteCompleteListener {
        void onWriteComplete();
    }
}