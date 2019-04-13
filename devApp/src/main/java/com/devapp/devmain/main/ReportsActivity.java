/*
package com.stellapps.smartamcu.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.stellapps.smartamcu.agentfarmersplit.AppConstants;
import com.stellapps.smartamcu.dao.CollectionRecordDao;
import com.stellapps.smartamcu.dao.DaoFactory;
import com.stellapps.smartamcu.dao.FarmerDao;
import com.stellapps.smartamcu.devicemanager.PrinterManager;
import com.stellapps.smartamcu.entity.AverageReportDetail;
import com.stellapps.smartamcu.entity.CenterRecordEntity;
import com.stellapps.smartamcu.entity.DairyReportEntity;
import com.stellapps.smartamcu.entity.FarmerEntity;
import com.stellapps.smartamcu.entity.MemberBillEntity;
import com.stellapps.smartamcu.entity.ReportEntity;
import com.stellapps.smartamcu.entity.SalesRecordEntity;
import com.stellapps.smartamcu.helper.ReportHelper;
import com.stellapps.smartamcu.postentities.CollectionConstants;
import com.stellapps.smartamcu.server.AmcuConfig;
import com.stellapps.smartamcu.server.DatabaseHandler;
import com.stellapps.smartamcu.server.SessionManager;
import com.stellapps.smartamcu.services.SendEmail;
import com.stellapps.smartamcu.services.WriteRecordReceiver;
import com.stellapps.smartamcu.usb.WriteExcel;
import com.stellapps.smartamcu.user.BillSummaryAdapter;
import com.stellapps.smartamcu.user.DairyPeriodicAdapter;
import com.stellapps.smartamcu.user.FormatPrintRecords;
import com.stellapps.smartamcu.user.RateAdapterNew;
import com.stellapps.smartamcu.user.TotalPeriodicAdapter;
import com.stellapps.smartamcu.user.TotalShiftAdapter;
import com.stellapps.smartamcu.user.Util;
import com.stellapps.smartamcu.util.ValidationHelper;
import com.devApp.BuildConfig;
import com.devApp.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import jxl.write.WriteException;

public class ReportsActivity extends Activity implements OnClickListener {

    static final int DATE_DIALOG_Start_ID = 999;
    static final int SEND_TIMEOUT = 10 * 1000;
    static final int SIZEWIDTH_MAX = 8;
    static final int SIZEHEIGHT_MAX = 8;
    static int year;
    static int month;
    static int day;
    public String space = " ";
    public boolean isWrittenToFile, isWrittentoEncFile;
    public WriteRecordReceiver receiverForWrite;
    UsbManager mUsbManager;
    UsbDeviceConnection usbDeviceConnection;
    PrinterManager parsingPrinterData;
    SpannableString stringSpannable;
    String[] strArrShift = new String[3];
    boolean starDate, endDate, dailyDate, salesStartDate, salesEndDate, pTstartDate, pTendDate;
    long StartDateIimeStamp, EndDateTimeStamp, etDailyTimestamp,
            salesEndTimeStamp, salesStartTimeStamp, tpStartTimeStamp,
            tpEndTimeStamp, purEndTimeStamp, purStartTimeStamp, totalShiftTimeStamp;
    EditText etDailyShiftDate, etStartMBR, etEndMBR, etFarmerIdstart,
            etFarmerIdEnd, etSalesStartDate, etSalesEndDate, etTpStartDate, etTpEndDate;
    Button btnDailyShift, btnMBR, btnMBS, btnMemberList, btnBack, btnSales, btnTotalPeriodic, btnPrintReport;
    AlertDialog alertDialog, alertDialogPrint;
    Spinner spShift, spFarmer;
    ProgressDialog progressbar;
    long lnDate, lnStartDate, lnEndDate;
    String farmer = "All farmers";
    String shift = "M";
    String farmerIdStart = "all";
    String farmerIdEnd = "all";
    boolean blDSR, blMBR, blMBS, blML, blPR, blSales, blTPR, blPUR, blTotS;
    Button btnPeriodicReports;
    ArrayList<String> allFarmerId = new ArrayList<String>();
    ArrayList<String> allFarmerAndCenterId = new ArrayList<String>();
    ArrayList<ReportEntity> allDetailRep = new ArrayList<ReportEntity>();
    Runnable updateRunnable, NewUpdateRunnable;
    Handler myHandler = new Handler();
    boolean msgSent;
    TextView tvalertHeader, tvalertSociety, tvalertShift, tvalertDate,
            tvalertShTime, tvalertD, tvDateFrom, tvDateTo, tvMemberName,
            tvMemC, tvFromC, tvToc, tvMemberPeriodic;
    RateAdapterNew rateAdapter;
    BillSummaryAdapter billSummaryAdapter;
    ArrayList<ReportEntity> allReportEntity = new ArrayList<ReportEntity>();
    ArrayList<SalesRecordEntity> allSalesRecordEntity = new ArrayList<SalesRecordEntity>();
    ArrayList<MemberBillEntity> allMemberBillEnt;
    String strBuild;
    ListView DailyShiftList;
    DatabaseHandler databaseHandler;
    File gpxfile, farmerFile;
    int detailsCheck;
    long longToday, longSet;
    LinearLayout lvDetails;
    boolean onCreate;
    SessionManager session;
    TextView tvNumOfFarm, tvAvgFat, tvAvgSnf, tvTotalCollection, tvAvgAmount;
    LinearLayout lnAvgFat, lnAvgSnf;
    boolean isAlert = false;
    AmcuConfig amcuConfig;
    LinearLayout lnSales, lnTPeriodic, lnTotalShift, lnPurchaseBody, lnPrintFarmer, lnTotalShiftHeader;
    RelativeLayout rlSales, rlTPeriodic, rlTotalShift, rlPurchase;
    Button btnTotalShift, btnPurchase, btnTrigger, btnPrintFarmer;
    EditText etPurchaseStart, etPurchaseEnd, etTotalShift;
    Spinner spTotalShift;

    //Center shift report
    boolean isPurStart, isPurEnd, isTotShift;
    ArrayList<AverageReportDetail> allAverageReport;
    LinearLayout lnCenterShiftHeader;
    RelativeLayout rlCenterShiftReport;
    Button btnCenterShift;
    EditText etCenterShiftDate;
    Spinner spCenterShift;
    String centerShift = "M";
    Boolean blCS, isCenterShift;
    long centerTimeStamp;
    ArrayList<CenterRecordEntity> allCenterReportEnt = new ArrayList<CenterRecordEntity>();
    TextView tvNumOfFarmText, tvAvgAmountText, tvFatText, tvAvgSnfText, tvTotalCollectionText;
    boolean isMailSend;
    long currentTimeMili = 0;
    boolean isSalesEnable;
    ArrayList<DairyReportEntity> allDairyReportEnt;

    private TextView tvProtein;
    private TextView tvIncentive;
    private TextView tvTotalAmount;
    private TextView tvKgFat;
    private TextView tvKgSnf;
    private LinearLayout llKgFat;
    private LinearLayout llKgSnf;
    private LinearLayout llAvgamount;
    private TextView tvTotalFatKg;
    private TextView tvTotalSnfKg;

    private ReportHelper reportHelper;
    private FarmerDao farmerDao;
    private CollectionRecordDao collectionRecordDao;


    private DatePickerDialog.OnDateSetListener datePickerListener =
            new DatePickerDialog.OnDateSetListener() {

                // when dialog box is closed, below method will be called.
                public void onDateSet(DatePicker view, int selectedYear,
                                      int selectedMonth, int selectedDay) {
                    onCreate = true;
                    //   long currentTime=System.currentTimeMillis();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));


                    year = selectedYear;
                    month = selectedMonth + 1;
                    day = selectedDay;

                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    String _day = String.valueOf(day);
                    String _month = String.valueOf(month);
                    if (day < 10)
                        _day = "0" + String.valueOf(day);
                    if (month < 10)
                        _month = "0" + String.valueOf(month);

                    // set selected date into textview

                    if (starDate) {
                        StartDateIimeStamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());


                        etStartMBR.setText(new StringBuilder().append(_day)
                                .append("-").append(_month).append("-")
                                .append(year));


                    } else if (endDate) {
                        EndDateTimeStamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());


                        etEndMBR.setText(new StringBuilder()
                                // Month is 0 based, just add 1
                                .append(_day).append("-").append(_month)
                                .append("-").append(year));


                    } else if (pTstartDate) {
                        tpStartTimeStamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());


                        etTpStartDate.setText(new StringBuilder().append(_day)
                                .append("-").append(_month).append("-")
                                .append(year));

                    } else if (pTendDate) {
                        tpEndTimeStamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());

                        etTpEndDate.setText(new StringBuilder()
                                // Month is 0 based, just add 1
                                .append(_day).append("-").append(_month)
                                .append("-").append(year));

                    } else if (dailyDate) {
                        etDailyTimestamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());


                        etDailyShiftDate.setText(new StringBuilder()
                                // Month is 0 based, just add 1
                                .append(_day).append("-").append(_month)
                                .append("-").append(year));


                    } else if (isTotShift) {
                        totalShiftTimeStamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());


                        etTotalShift.setText(new StringBuilder()
                                // Month is 0 based, just add 1
                                .append(_day).append("-").append(_month)
                                .append("-").append(year));

                    } else if (salesEndDate) {
                        calendar.set(selectedYear, selectedMonth, selectedDay, 23, 59, 59);
                        long calendarMiliend = calendar.getTimeInMillis();

                        salesEndTimeStamp = calendarMiliend;


                        etSalesEndDate.setText(new StringBuilder().append(_day)
                                .append("-").append(_month).append("-")
                                .append(year));

                    } else if (salesStartDate) {

                        calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
                        long calendarMiliStart = calendar.getTimeInMillis();
                        salesStartTimeStamp = calendarMiliStart;
                        etSalesStartDate.setText(new StringBuilder().append(_day)
                                .append("-").append(_month).append("-")
                                .append(year));
                    }

                    //This is for dairy report

                    else if (isPurEnd) {
                        calendar.set(selectedYear, selectedMonth, selectedDay, 23, 59, 59);
                        long calendarMiliend = calendar.getTimeInMillis();

                        purEndTimeStamp = calendarMiliend;


                        etPurchaseEnd.setText(new StringBuilder().append(_day)
                                .append("-").append(_month).append("-")
                                .append(year));

                    } else if (isPurStart) {

                        calendar.set(selectedYear, selectedMonth, selectedDay, 0, 0, 0);
                        long calendarMiliStart = calendar.getTimeInMillis();
                        purStartTimeStamp = calendarMiliStart;

                        etPurchaseStart.setText(new StringBuilder().append(_day)
                                .append("-").append(_month).append("-"));

                    } else if (isCenterShift) {
                        centerTimeStamp = Long.parseLong(new StringBuilder()
                                .append(year).append(_month).append(_day).toString());

                        etCenterShiftDate.setText(new StringBuilder()
                                .append(_day).append("-").append(_month)
                                .append("-").append(year));


                    }
                    initDatePicker(onCreate);

                }
            };


    @Override
    protected void onStart() {

        parsingPrinterData = new PrinterManager(ReportsActivity.this);
        reportHelper = new ReportHelper(ReportsActivity.this);

        setupServiceReceiver();
        checkifSalesEnable();
        checkVisibilityForA4Print();

        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reports_activity);
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(ReportsActivity.this);
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        etDailyShiftDate = (EditText) findViewById(R.id.etShiftDateDR);
        etStartMBR = (EditText) findViewById(R.id.etStartDateMBR);
        etEndMBR = (EditText) findViewById(R.id.etEndDateMBR);
        etFarmerIdstart = (EditText) findViewById(R.id.etFarmStartMBR);
        etFarmerIdEnd = (EditText) findViewById(R.id.etFarmEndMBR);

        etSalesEndDate = (EditText) findViewById(R.id.etEndSalesDate);
        etSalesStartDate = (EditText) findViewById(R.id.etStartSalesDate);
        etTotalShift = (EditText) findViewById(R.id.etTotalShift);

        etTpStartDate = (EditText) findViewById(R.id.etStartTPDate);
        etTpEndDate = (EditText) findViewById(R.id.etEndTPDate);

        etPurchaseStart = (EditText) findViewById(R.id.etStartPurchaseDate);
        etPurchaseEnd = (EditText) findViewById(R.id.etEndPurchaseDate);

        btnDailyShift = (Button) findViewById(R.id.btnDailyreport);
        btnPeriodicReports = (Button) findViewById(R.id.btnPeriodicShiftRecords);
        btnMBR = (Button) findViewById(R.id.btnMemberBill);
        btnMBS = (Button) findViewById(R.id.btnMemberSummary);
        btnSales = (Button) findViewById(R.id.btnSales);
        btnTotalPeriodic = (Button) findViewById(R.id.btnTotalPeriodic);
        btnPrintReport = (Button) findViewById(R.id.btnPrintA4);

        btnTrigger = (Button) findViewById(R.id.btnTrigger);
        btnTotalShift = (Button) findViewById(R.id.btnTotalShift);
        btnPurchase = (Button) findViewById(R.id.btnPurchase);

        btnMemberList = (Button) findViewById(R.id.btnMemberList);
        btnBack = (Button) findViewById(R.id.btnBack);
        spShift = (Spinner) findViewById(R.id.spSelectShift);
        spFarmer = (Spinner) findViewById(R.id.spFarmer);
        spTotalShift = (Spinner) findViewById(R.id.spTotalShift);


        strArrShift = getApplicationContext().getResources().getStringArray(
                R.array.Select_shift);

        lnSales = (LinearLayout) findViewById(R.id.lnperiodicSalesHeader);
        rlSales = (RelativeLayout) findViewById(R.id.rlpriodicSalesbody);

        rlTotalShift = (RelativeLayout) findViewById(R.id.rlTotalShiftBody);
        lnTotalShift = (LinearLayout) findViewById(R.id.lnTotalShift);
        lnTotalShiftHeader = (LinearLayout) findViewById(R.id.lnTotalShiftHeader);


//This is for dairy report
        rlPurchase = (RelativeLayout) findViewById(R.id.rlPurchaseBody);
        lnPurchaseBody = (LinearLayout) findViewById(R.id.lnPurchaseHeader);

        lnTPeriodic = (LinearLayout) findViewById(R.id.lnTPeriodicH);
        rlTPeriodic = (RelativeLayout) findViewById(R.id.rlTPeriodic);

        lnPrintFarmer = (LinearLayout) findViewById(R.id.lnPrintFarmer);
        btnPrintFarmer = (Button) findViewById(R.id.btnPrintFarmer);

        //This is for center shift report

        rlCenterShiftReport = (RelativeLayout) findViewById(R.id.rlCenterBody);
        lnCenterShiftHeader = (LinearLayout) findViewById(R.id.lnCenterHeader);
        etCenterShiftDate = (EditText) findViewById(R.id.etCenterShiftDate);
        btnCenterShift = (Button) findViewById(R.id.btnShiftCenter);
        spCenterShift = (Spinner) findViewById(R.id.spCenterSelectedShift);

        etCenterShiftDate.setOnClickListener(this);
        btnCenterShift.setOnClickListener(this);

        etDailyShiftDate.setOnClickListener(this);
        etStartMBR.setOnClickListener(this);
        etEndMBR.setOnClickListener(this);

        etSalesEndDate.setOnClickListener(this);
        etSalesStartDate.setOnClickListener(this);

        etTpEndDate.setOnClickListener(this);
        etTpStartDate.setOnClickListener(this);
        etPurchaseEnd.setOnClickListener(this);
        etPurchaseStart.setOnClickListener(this);

        session = new SessionManager(ReportsActivity.this);

        btnDailyShift.setOnClickListener(this);
        btnMBR.setOnClickListener(this);
        btnMBS.setOnClickListener(this);
        btnMemberList.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPeriodicReports.setOnClickListener(this);
        btnSales.setOnClickListener(this);
        btnTotalPeriodic.setOnClickListener(this);
        btnTotalShift.setOnClickListener(this);
        btnPrintReport.setOnClickListener(this);
        btnPrintFarmer.setOnClickListener(this);
        btnPurchase.setOnClickListener(this);

        farmer = "All farmers";
        getAllFarmerId();

        spShift.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                shift = spShift.getItemAtPosition(arg2).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
        //Center shift report
        spCenterShift.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                centerShift = spCenterShift.getItemAtPosition(arg2).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, allFarmerAndCenterId);
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFarmer.setAdapter(dataAdapter);

        spFarmer.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                farmer = spFarmer.getItemAtPosition(arg2).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        databaseHandler = DatabaseHandler.getDatabaseInstance();
        initDatePicker(onCreate);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnDailyreport: {

                setFarmer();

                if (etDailyShiftDate.getText().toString() != null
                        && etDailyShiftDate.getText().toString().length() > 4) {

                    lnDate = Util.getDateInLongFormat(etDailyShiftDate.getText()
                            .toString());

                    boolean validate = validateAndErrorToast(etDailyShiftDate.getText().toString().trim(), null, null);

                    if (!validate) {
                        return;
                    }

                    blDSR = true;
                    blMBR = false;
                    blMBS = false;
                    blML = false;
                    blSales = false;
                    blTPR = false;
                    blPUR = false;
                    isAlert = true;
                    blTotS = false;
                    blCS = false;
                    printDetails();
                } else {
                    Toast.makeText(ReportsActivity.this, "Please select the date",
                            Toast.LENGTH_SHORT).show();
                }

            }

            break;

            case R.id.btnPeriodicShiftRecords: {
                if (etStartMBR.getText().toString().length() > 4
                        && etEndMBR.getText().toString().length() > 4) {

                    lnStartDate = Util.getDateInLongFormat(etStartMBR.getText()
                            .toString());
                    lnEndDate = Util.getDateInLongFormat(etEndMBR.getText()
                            .toString());

                    if (etFarmerIdEnd.getText().toString().length() > 0) {
                        farmerIdEnd = etFarmerIdEnd.getText().toString();
                    }

                    if (etFarmerIdstart.getText().toString().length() > 0) {
                        farmerIdStart = etFarmerIdEnd.getText().toString();
                    }


                    boolean validate = validateAndErrorToast(null, etStartMBR.getText().toString().trim()
                            , etEndMBR.getText().toString().trim());

                    if (!validate) {
                        return;
                    }


                    blDSR = false;
                    blMBR = false;
                    blMBS = false;
                    blML = false;
                    isAlert = true;
                    blPR = true;
                    blSales = false;
                    blTPR = false;
                    blPUR = false;
                    blTotS = false;
                    blCS = false;
                    printDetails();

                } else {
                    Toast.makeText(ReportsActivity.this,
                            "Please enter start and end date!", Toast.LENGTH_SHORT)
                            .show();
                }

            }
            break;
            case R.id.btnSales: {
                if (etSalesStartDate.getText().toString().length() > 4
                        && etSalesEndDate.getText().toString().length() > 4) {

                    boolean validate = validateAndErrorToast(null, etSalesStartDate.getText().toString().trim()
                            , etSalesEndDate.getText().toString().trim());

                    if (!validate) {
                        return;
                    }


                    blDSR = false;
                    blMBR = false;
                    blMBS = false;
                    blML = false;
                    isAlert = true;
                    blPR = false;
                    blSales = true;
                    blTPR = false;
                    blPUR = false;
                    blTotS = false;
                    blCS = false;
                    printDetails();

                } else {
                    Toast.makeText(ReportsActivity.this,
                            "Please enter start and end date!", Toast.LENGTH_SHORT)
                            .show();
                }

            }
            break;

            case R.id.btnPurchase: {
                if (etPurchaseStart.getText().toString().length() > 4
                        && etPurchaseEnd.getText().toString().length() > 4) {


                    boolean validate = validateAndErrorToast(null, etPurchaseStart.getText().toString().trim()
                            , etPurchaseEnd.getText().toString().trim());

                    if (!validate) {
                        return;
                    }

                    Util.displayErrorToast("Please wait..", ReportsActivity.this);
                    getAllDairyEntity();
                    blDSR = false;
                    blMBR = false;
                    blMBS = false;
                    blML = false;
                    isAlert = true;
                    blPR = false;
                    blSales = false;
                    blTPR = false;
                    blPUR = true;
                    blTotS = false;
                    blCS = false;

                    printDetails();

                } else {
                    Toast.makeText(ReportsActivity.this,
                            "Please enter start and end date!", Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            }

            case R.id.btnTotalPeriodic: {

                if (etTpStartDate.getText().toString().length() > 4
                        && etTpEndDate.getText().toString().length() > 4) {

                    lnStartDate = Util.getDateInLongFormat(etTpStartDate.getText()
                            .toString());
                    lnEndDate = Util.getDateInLongFormat(etTpEndDate.getText()
                            .toString());

                    boolean validate = validateAndErrorToast(null, etTpStartDate.getText().toString().trim()
                            , etTpEndDate.getText().toString().trim());

                    if (!validate) {
                        return;
                    }

                    blDSR = false;
                    blMBR = false;
                    blMBS = false;
                    blML = false;
                    isAlert = true;
                    blPR = false;
                    blSales = false;
                    blTPR = true;
                    blPUR = false;
                    blTotS = false;
                    blCS = false;

                    // for Total periodic report
                    getAllreportsEnt();

                    printDetails();

                } else {
                    Toast.makeText(ReportsActivity.this,
                            "Please enter start and end date!", Toast.LENGTH_SHORT)
                            .show();
                }

            }
            break;
            case R.id.btnMemberBill: {
                setFarmer();

                if (etStartMBR.getText().toString().length() > 4
                        && etEndMBR.getText().toString().length() > 4) {

                    lnStartDate = Util.getDateInLongFormat(etStartMBR.getText()
                            .toString());
                    lnEndDate = Util.getDateInLongFormat(etEndMBR.getText()
                            .toString());

                    if (etFarmerIdEnd.getText().toString().length() > 0) {
                        farmerIdEnd = etFarmerIdEnd.getText().toString();
                    }

                    if (etFarmerIdstart.getText().toString().length() > 0) {
                        farmerIdStart = etFarmerIdEnd.getText().toString();
                    }


                    boolean validate = validateAndErrorToast(null, etStartMBR.getText().toString().trim()
                            , etEndMBR.getText().toString().trim());

                    if (!validate) {
                        return;
                    }

                    blDSR = false;
                    blMBR = true;
                    blMBS = false;
                    blML = false;
                    isAlert = true;
                    blPR = false;
                    blSales = false;
                    blTPR = false;
                    blPUR = false;
                    blTotS = false;
                    blCS = false;

                    printDetails();

                } else {
                    Toast.makeText(ReportsActivity.this,
                            "Please enter start and end date!", Toast.LENGTH_SHORT)
                            .show();
                }

            }

            break;

            case R.id.btnMemberSummary: {
                setFarmer();

                if (etStartMBR.getText().toString().length() > 4
                        && etEndMBR.getText().toString().length() > 4) {

                    lnStartDate = Util.getDateInLongFormat(etStartMBR.getText()
                            .toString());
                    lnEndDate = Util.getDateInLongFormat(etEndMBR.getText()
                            .toString());

                    if (etFarmerIdEnd.getText().toString().length() > 0) {
                        farmerIdEnd = etFarmerIdEnd.getText().toString();
                    }

                    if (etFarmerIdstart.getText().toString().length() > 0) {
                        farmerIdStart = etFarmerIdEnd.getText().toString();
                    }


                    boolean validate = validateAndErrorToast(null, etStartMBR.getText().toString().trim()
                            , etEndMBR.getText().toString().trim());

                    if (!validate) {
                        return;
                    }

                    blDSR = false;
                    blMBR = false;
                    blMBS = true;
                    blML = false;
                    isAlert = true;
                    blPR = false;
                    blSales = false;
                    blTPR = false;
                    blPUR = false;
                    blTotS = false;
                    blCS = false;
                    printDetails();

                } else {
                    Toast.makeText(ReportsActivity.this,
                            "Please enter start and end date!", Toast.LENGTH_SHORT)
                            .show();
                }

            }
            break;

            case R.id.btnMemberList: {
                setFarmer();

                blDSR = false;
                blMBR = false;
                blMBS = false;
                blML = true;
                isAlert = true;
                blTPR = false;
                blPR = false;
                blPUR = false;
                blTotS = false;
                blCS = false;

                printDetails();
            }
            break;

            case R.id.btnTotalShift: {


                blDSR = false;
                blMBR = false;
                blMBS = false;
                blML = false;
                isAlert = true;
                blTPR = false;
                blPR = false;
                blPUR = false;
                blTotS = true;
                blCS = false;

                allAverageReport = getTotalShiftReport();

                printDetails();
            }
            break;


            case R.id.btnShiftCenter: {


                blDSR = false;
                blMBR = false;
                blMBS = false;
                blML = false;
                isAlert = true;
                blTPR = false;
                blPR = false;
                blPUR = false;
                blTotS = false;
                blCS = true;

                printDetails();
            }
            break;

            case R.id.etShiftDateDR:
                dailyDate = true;
                starDate = false;
                endDate = false;

                salesEndDate = false;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = false;

                isPurStart = false;
                isPurEnd = false;
                isTotShift = false;

                isCenterShift = false;

                showDialog(DATE_DIALOG_Start_ID);

                break;

            case R.id.etCenterShiftDate:
                dailyDate = false;
                starDate = false;
                endDate = false;

                salesEndDate = false;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = false;

                isPurStart = false;
                isPurEnd = false;
                isTotShift = false;
                isCenterShift = true;

                showDialog(DATE_DIALOG_Start_ID);

                break;

            case R.id.etTotalShift:
                dailyDate = false;
                starDate = false;
                endDate = false;

                salesEndDate = false;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = false;

                isPurStart = false;
                isPurEnd = false;
                isTotShift = true;
                isCenterShift = false;

                showDialog(DATE_DIALOG_Start_ID);

                break;

            case R.id.etStartDateMBR:
                dailyDate = false;
                starDate = true;
                endDate = false;
                salesEndDate = false;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = false;
                isPurStart = false;
                isPurEnd = false;
                isTotShift = false;
                isCenterShift = false;

                showDialog(DATE_DIALOG_Start_ID);
                break;
            case R.id.etEndDateMBR:
                dailyDate = false;
                starDate = false;
                endDate = true;
                salesEndDate = false;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = false;
                isPurStart = false;
                isPurEnd = false;
                isTotShift = false;
                isCenterShift = false;

                showDialog(DATE_DIALOG_Start_ID);
                break;

            case R.id.etEndSalesDate: {
                dailyDate = false;
                starDate = false;
                endDate = false;

                salesEndDate = true;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = false;
                isPurStart = false;
                isPurEnd = false;
                isTotShift = false;
                isCenterShift = false;

                showDialog(DATE_DIALOG_Start_ID);

            }
            break;

            case R.id.etStartSalesDate: {
                dailyDate = false;
                starDate = false;
                endDate = false;

                salesEndDate = false;
                salesStartDate = true;
                pTendDate = false;
                pTstartDate = false;
                isPurStart = false;
                isPurEnd = false;
                isTotShift = false;
                isCenterShift = false;

                showDialog(DATE_DIALOG_Start_ID);
            }
            break;


            case R.id.etEndTPDate: {

                dailyDate = false;
                starDate = false;
                endDate = false;

                salesEndDate = false;
                salesStartDate = false;
                pTendDate = true;
                pTstartDate = false;
                isPurStart = false;
                isPurEnd = false;
                isTotShift = false;
                isCenterShift = false;

                showDialog(DATE_DIALOG_Start_ID);

            }
            break;
            case R.id.etStartTPDate: {
                dailyDate = false;
                starDate = false;
                endDate = false;

                salesEndDate = false;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = true;
                isPurStart = false;
                isPurEnd = false;
                isTotShift = false;
                isCenterShift = false;

                showDialog(DATE_DIALOG_Start_ID);
            }
            break;

            case R.id.etStartPurchaseDate: {
                dailyDate = false;
                starDate = false;
                endDate = false;

                salesEndDate = false;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = false;
                isPurStart = true;
                isPurEnd = false;
                isTotShift = false;
                isCenterShift = false;
                showDialog(DATE_DIALOG_Start_ID);
            }
            break;

            case R.id.etEndPurchaseDate: {
                dailyDate = false;
                starDate = false;
                endDate = false;

                salesEndDate = false;
                salesStartDate = false;
                pTendDate = false;
                pTstartDate = false;
                isPurStart = false;
                isPurEnd = true;
                isTotShift = false;
                isCenterShift = false;
                showDialog(DATE_DIALOG_Start_ID);
            }
            break;

            case R.id.btnBack:
                finish();
                break;

            case R.id.btnPrintA4:

                break;

            case R.id.btnTrigger:
                onClickTrigger();
                break;

            case R.id.btnPrintFarmer:
                boolean isWr = createExcelForPint("Farmer");
                if (isWr) {
                    printViaShareIntent(farmerFile.toString());
                    ;
                }

                break;

            default:
                break;
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        return new DatePickerDialog(this, datePickerListener, year, month, day);

    }

    public void printDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                ReportsActivity.this);
        detailsCheck = 0;
        isMailSend = false;

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = null;
        Button btnSend = null;
        Button btnPrint = null;
        Button btnExport = null;

        TextView tvSnf;

        if (blDSR) {
            //Daily shift report
            detailsCheck = Util.sendDailyReport;
            view = inflater.inflate(R.layout.daily_shift_report, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertShift = (TextView) view.findViewById(R.id.tvShift);
            tvalertShTime = (TextView) view.findViewById(R.id.tvShiftTime);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvalertD = (TextView) view.findViewById(R.id.tvD);
            tvalertDate = (TextView) view.findViewById(R.id.tvDate);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);
            btnExport = (Button) view.findViewById(R.id.btnExport);
            tvalertDate.setText(etDailyShiftDate.getText().toString());
            DailyShiftList = (ListView) view
                    .findViewById(R.id.lvDailyShiftList);
            tvSnf = (TextView) view.findViewById(R.id.tvSnf);

            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")))

            {
                tvSnf.setText("Clr");
            }
            tvIncentive = (TextView) view.findViewById(R.id.tvIncentive);
            tvProtein = (TextView) view.findViewById(R.id.tvProtein);
            tvTotalAmount = (TextView) view.findViewById(R.id.tvtotalAmount);
            tvKgFat = (TextView) view.findViewById(R.id.tvFatKg);
            tvKgSnf = (TextView) view.findViewById(R.id.tvSnfKg);
            llKgFat = (LinearLayout) view.findViewById(R.id.lnToalFat);
            llKgSnf = (LinearLayout) view.findViewById(R.id.lnToalSNF);
            llAvgamount = (LinearLayout) view.findViewById(R.id.lnAvgamount);
            tvTotalFatKg = (TextView) view.findViewById(R.id.tvTotalFatKg);
            tvTotalSnfKg = (TextView) view.findViewById(R.id.tvTotalSnfKg);

            TextView tvRate = (TextView) view.findViewById(R.id.tvRate);
            TextView tvAmount = (TextView) view.findViewById(R.id.tvAmount);

            if (amcuConfig.getKeyAllowProteinValue()) {
                tvIncentive.setVisibility(View.VISIBLE);
                tvTotalAmount.setVisibility(View.VISIBLE);
                tvProtein.setVisibility(View.VISIBLE);
            } else {
                tvIncentive.setVisibility(View.GONE);
                tvTotalAmount.setVisibility(View.GONE);
                tvProtein.setVisibility(View.GONE);

            }

            if (amcuConfig.getKeyAllowDisplayRate()) {
                llKgSnf.setVisibility(View.GONE);
                llKgFat.setVisibility(View.GONE);
                tvRate.setVisibility(View.VISIBLE);
                tvAmount.setVisibility(View.VISIBLE);
                llAvgamount.setVisibility(View.VISIBLE);
                tvKgSnf.setVisibility(View.GONE);
                tvKgFat.setVisibility(View.GONE);

            } else {
                llKgSnf.setVisibility(View.VISIBLE);
                llKgFat.setVisibility(View.VISIBLE);
                tvRate.setVisibility(View.GONE);
                tvAmount.setVisibility(View.GONE);
                llAvgamount.setVisibility(View.GONE);
                tvKgSnf.setVisibility(View.VISIBLE);
                tvKgFat.setVisibility(View.VISIBLE);

            }

            tvNumOfFarm = (TextView) view.findViewById(R.id.tvNumOfFarm);
            tvAvgAmount = (TextView) view.findViewById(R.id.tvAvgamount);
            tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
            tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
            tvTotalCollection = (TextView) view
                    .findViewById(R.id.tvTotalCollection);

            tvNumOfFarmText = (TextView) view.findViewById(R.id.tvNumOfFarmerText);
            tvAvgAmountText = (TextView) view.findViewById(R.id.tvAvgamountText);
            tvFatText = (TextView) view.findViewById(R.id.tvAvgFatText);
            tvTotalCollectionText = (TextView) view.findViewById(R.id.tvTotalCollectionText);
            tvAvgSnfText = (TextView) view.findViewById(R.id.tvAvgSnfText);


            // btnExport.setVisibility(View.VISIBLE);
            boolean allowVisibilty = Util.allowExportToPendrive(etDailyShiftDate.getText()
                    .toString(), shift, ReportsActivity.this);

            if (allowVisibilty) {
                btnExport.setVisibility(View.VISIBLE);
            } else {
                btnExport.setVisibility(View.GONE);
            }

            if (shift.equalsIgnoreCase("M")
                    || shift.equalsIgnoreCase("Morning")) {
                tvalertShTime.setText("Morning");
            } else {
                tvalertShTime.setText("Evening");
            }

            btnExport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    isMailSend = false;
                    checkForPenDrive(false);

                }
            });

        } else if (blMBR) {
            //For member bill register
            detailsCheck = Util.sendMemberBillRegister;
            view = inflater.inflate(R.layout.report_memberbill_register, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertHeader.setText("Member bill register");
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvMemberName = (TextView) view.findViewById(R.id.tvMemberDetail);
            tvDateFrom = (TextView) view.findViewById(R.id.tvDateFrom);
            tvDateTo = (TextView) view.findViewById(R.id.tvDateTo);

            tvFromC = (TextView) view.findViewById(R.id.tvFrom);
            tvToc = (TextView) view.findViewById(R.id.tvTo);
            tvMemC = (TextView) view.findViewById(R.id.tvMemC);

            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);
            tvDateFrom.setText(etStartMBR.getText().toString() + "   ");
            tvDateTo.setText(etEndMBR.getText().toString() + "   ");

            DailyShiftList = (ListView) view
                    .findViewById(R.id.lvDailyShiftList);

        } else if (blMBS) {
            //For member bill summary
            detailsCheck = Util.sendMemberBillSummary;
            view = inflater.inflate(R.layout.report_member_bill_summary, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertHeader.setText("Member bill summary");
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvMemberName = (TextView) view.findViewById(R.id.tvMemberDetail);
            tvNumOfFarm = (TextView) view.findViewById(R.id.tvNumOfFarm);
            tvDateFrom = (TextView) view.findViewById(R.id.tvDateFrom);
            tvDateTo = (TextView) view.findViewById(R.id.tvDateTo);
            tvFromC = (TextView) view.findViewById(R.id.tvFrom);
            tvToc = (TextView) view.findViewById(R.id.tvTo);
            tvMemC = (TextView) view.findViewById(R.id.tvMemC);

            tvDateFrom.setText(etStartMBR.getText().toString() + "   ");
            tvDateTo.setText(etEndMBR.getText().toString() + "   ");

            // for average values

            tvAvgAmount = (TextView) view.findViewById(R.id.tvAvgamount);
            tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
            tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
            tvTotalCollection = (TextView) view
                    .findViewById(R.id.tvTotalCollection);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);
            DailyShiftList = (ListView) view.findViewById(R.id.lvMemberbill);
        } else if (blML)

        {
            //For member list
            view = inflater.inflate(R.layout.report_member_list, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertHeader.setText("Member List");
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            DailyShiftList = (ListView) view.findViewById(R.id.lvMemberList);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);
        } else if (blPR) {
            //for periodic report
            detailsCheck = Util.sendPeriodicReport;
            view = inflater.inflate(R.layout.daily_shift_report, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertShift = (TextView) view.findViewById(R.id.tvShift);
            tvalertShTime = (TextView) view.findViewById(R.id.tvShiftTime);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvalertD = (TextView) view.findViewById(R.id.tvD);
            tvalertDate = (TextView) view.findViewById(R.id.tvDate);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);
            tvalertDate.setText(etDailyShiftDate.getText().toString());
            DailyShiftList = (ListView) view
                    .findViewById(R.id.lvDailyShiftList);
            // for average values
            tvNumOfFarm = (TextView) view.findViewById(R.id.tvNumOfFarm);
            tvAvgAmount = (TextView) view.findViewById(R.id.tvAvgamount);
            tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
            tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
            tvTotalCollection = (TextView) view
                    .findViewById(R.id.tvTotalCollection);

            tvNumOfFarmText = (TextView) view.findViewById(R.id.tvNumOfFarmerText);
            tvAvgAmountText = (TextView) view.findViewById(R.id.tvAvgamountText);
            tvFatText = (TextView) view.findViewById(R.id.tvAvgFatText);
            tvTotalCollectionText = (TextView) view.findViewById(R.id.tvTotalCollectionText);
            tvAvgSnfText = (TextView) view.findViewById(R.id.tvAvgSnfText);

            tvIncentive = (TextView) view.findViewById(R.id.tvIncentive);
            tvProtein = (TextView) view.findViewById(R.id.tvProtein);
            tvTotalAmount = (TextView) view.findViewById(R.id.tvtotalAmount);
            tvKgFat = (TextView) view.findViewById(R.id.tvFatKg);
            tvKgSnf = (TextView) view.findViewById(R.id.tvSnfKg);
            llKgFat = (LinearLayout) view.findViewById(R.id.lnToalFat);
            llKgSnf = (LinearLayout) view.findViewById(R.id.lnToalSNF);
            tvTotalFatKg = (TextView) view.findViewById(R.id.tvTotalFatKg);
            tvTotalSnfKg = (TextView) view.findViewById(R.id.tvTotalSnfKg);

            llAvgamount = (LinearLayout) view.findViewById(R.id.lnAvgamount);
            TextView tvRate = (TextView) view.findViewById(R.id.tvRate);
            TextView tvAmount = (TextView) view.findViewById(R.id.tvAmount);

            if (amcuConfig.getKeyAllowProteinValue()) {
                tvIncentive.setVisibility(View.VISIBLE);
                tvTotalAmount.setVisibility(View.VISIBLE);
                tvProtein.setVisibility(View.VISIBLE);
            } else {
                tvIncentive.setVisibility(View.GONE);
                tvTotalAmount.setVisibility(View.GONE);
                tvProtein.setVisibility(View.GONE);

            }
            if (amcuConfig.getKeyAllowDisplayRate()) {
                llKgSnf.setVisibility(View.GONE);
                llKgFat.setVisibility(View.GONE);
                tvRate.setVisibility(View.VISIBLE);
                tvAmount.setVisibility(View.VISIBLE);
                llAvgamount.setVisibility(View.VISIBLE);
                tvKgSnf.setVisibility(View.GONE);
                tvKgFat.setVisibility(View.GONE);
            } else {
                llKgSnf.setVisibility(View.VISIBLE);
                llKgFat.setVisibility(View.VISIBLE);
                tvRate.setVisibility(View.GONE);
                tvAmount.setVisibility(View.GONE);
                llAvgamount.setVisibility(View.GONE);
                tvKgSnf.setVisibility(View.VISIBLE);
                tvKgFat.setVisibility(View.VISIBLE);


            }
            tvMemberPeriodic = (TextView) view.findViewById(R.id.tvMember);
            final TextView tvSh = (TextView) view.findViewById(R.id.tvShift);
            tvSh.setVisibility(View.GONE);
            tvalertD.setVisibility(View.GONE);
            tvalertDate.setVisibility(View.GONE);
            tvalertShTime.setText("From " + etStartMBR.getText().toString()
                    + " To " + etEndMBR.getText().toString());
            if (!farmer.equalsIgnoreCase("All farmers")) {
                tvMemberPeriodic.setText("Date");
            }
            tvalertShTime.setGravity(Gravity.CENTER_HORIZONTAL);
            tvalertHeader.setText("Periodic Report");
            btnExport = (Button) view.findViewById(R.id.btnExport);
            boolean allowVisibilty = Util.allowExportToPendrive(etEndMBR.getText()
                    .toString(), null, ReportsActivity.this);

            if (allowVisibilty) {
                btnExport.setVisibility(View.VISIBLE);
            } else {
                btnExport.setVisibility(View.GONE);
            }
            //  btnExport.setVisibility(View.VISIBLE);

            tvSnf = (TextView) view.findViewById(R.id.tvSnf);

            //To display clr

            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))) {
                tvSnf.setText("Clr");
            }

            btnExport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    isMailSend = false;
                    checkForPenDrive(false);

                }
            });
        } else if (blSales) {
            //for sales report report
            detailsCheck = Util.sendSalesReport;
            view = inflater.inflate(R.layout.daily_shift_report, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertShift = (TextView) view.findViewById(R.id.tvShift);
            tvalertShTime = (TextView) view.findViewById(R.id.tvShiftTime);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvalertD = (TextView) view.findViewById(R.id.tvD);
            tvalertDate = (TextView) view.findViewById(R.id.tvDate);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);
            tvalertDate.setText(etDailyShiftDate.getText().toString());
            DailyShiftList = (ListView) view
                    .findViewById(R.id.lvDailyShiftList);
            // for average values
            tvNumOfFarm = (TextView) view.findViewById(R.id.tvNumOfFarm);
            tvAvgAmount = (TextView) view.findViewById(R.id.tvAvgamount);
            tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
            tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
            tvTotalFatKg = (TextView) view.findViewById(R.id.tvTotalFatKg);
            tvTotalSnfKg = (TextView) view.findViewById(R.id.tvTotalSnfKg);

            tvTotalCollection = (TextView) view
                    .findViewById(R.id.tvTotalCollection);
            tvMemberPeriodic = (TextView) view.findViewById(R.id.tvMember);
            final TextView tvSh = (TextView) view.findViewById(R.id.tvShift);
            tvSh.setVisibility(View.GONE);
            tvalertD.setVisibility(View.GONE);
            tvalertDate.setVisibility(View.GONE);
            tvalertShTime.setText("From " + etSalesStartDate.getText().toString()
                    + " To " + etSalesEndDate.getText().toString());
            if (!farmer.equalsIgnoreCase("All farmers")) {
                tvMemberPeriodic.setText("Date");
            }
            tvalertShTime.setGravity(Gravity.CENTER_HORIZONTAL);
            tvalertHeader.setText("Sales Report");
            btnExport = (Button) view.findViewById(R.id.btnExport);
            btnExport.setVisibility(View.GONE);
            btnExport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    isMailSend = false;
                    checkForPenDrive(false);

                }
            });
        }


        //this for dairy report goa

        else if (blPUR) {
            //for sales report report
            detailsCheck = Util.sendDairyReport;
            view = inflater.inflate(R.layout.dairy_shift_report, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertShift = (TextView) view.findViewById(R.id.tvShift);
            tvalertShTime = (TextView) view.findViewById(R.id.tvShiftTime);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvalertD = (TextView) view.findViewById(R.id.tvD);
            tvalertDate = (TextView) view.findViewById(R.id.tvDate);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);


            tvalertDate.setText(etDailyShiftDate.getText().toString());
            DailyShiftList = (ListView) view
                    .findViewById(R.id.lvDailyShiftList);
            // for average values
            tvNumOfFarm = (TextView) view.findViewById(R.id.tvNumOfFarm);
            tvAvgAmount = (TextView) view.findViewById(R.id.tvAvgamount);
            tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
            tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
            tvTotalCollection = (TextView) view
                    .findViewById(R.id.tvTotalCollection);
            tvMemberPeriodic = (TextView) view.findViewById(R.id.tvMember);
            final TextView tvSh = (TextView) view.findViewById(R.id.tvShift);
            tvSh.setVisibility(View.GONE);
            tvalertD.setVisibility(View.GONE);
            tvalertDate.setVisibility(View.GONE);
            tvalertShTime.setText("From " + etPurchaseStart.getText().toString()
                    + " To " + etPurchaseEnd.getText().toString());
            if (!farmer.equalsIgnoreCase("All farmers")) {
                tvMemberPeriodic.setText("Date");
            }

            tvalertShTime.setGravity(Gravity.CENTER_HORIZONTAL);
            tvalertHeader.setText("Dairy Report");
            btnExport = (Button) view.findViewById(R.id.btnExport);
            btnExport.setVisibility(View.GONE);
            btnSend.setVisibility(View.GONE);
            btnExport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    isMailSend = false;
                    checkForPenDrive(false);

                }
            });
        }

        //Total shift summary

        else if (blTotS) {
            //for sales report report
            detailsCheck = Util.sendTotalShift;
            view = inflater.inflate(R.layout.dairy_shift_report, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertShift = (TextView) view.findViewById(R.id.tvShift);
            tvalertShTime = (TextView) view.findViewById(R.id.tvShiftTime);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvalertD = (TextView) view.findViewById(R.id.tvD);
            tvalertDate = (TextView) view.findViewById(R.id.tvDate);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);


            tvalertDate.setText(etDailyShiftDate.getText().toString());
            DailyShiftList = (ListView) view
                    .findViewById(R.id.lvDailyShiftList);
            // for average values
            tvNumOfFarm = (TextView) view.findViewById(R.id.tvNumOfFarm);
            tvAvgAmount = (TextView) view.findViewById(R.id.tvAvgamount);
            tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
            tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
            tvTotalCollection = (TextView) view
                    .findViewById(R.id.tvTotalCollection);
            tvMemberPeriodic = (TextView) view.findViewById(R.id.tvMember);
            final TextView tvSh = (TextView) view.findViewById(R.id.tvShift);
            tvSh.setVisibility(View.GONE);
            tvalertD.setVisibility(View.GONE);
            tvalertDate.setVisibility(View.GONE);
            tvalertShTime.setText("From " + etPurchaseStart.getText().toString()
                    + " To " + etPurchaseEnd.getText().toString());
            if (!farmer.equalsIgnoreCase("All farmers")) {
                tvMemberPeriodic.setText("Date");
            }

            tvalertShTime.setGravity(Gravity.CENTER_HORIZONTAL);
            tvalertHeader.setText("Dairy Report");
            btnExport = (Button) view.findViewById(R.id.btnExport);
            btnExport.setVisibility(View.GONE);
            btnSend.setVisibility(View.GONE);
            btnExport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    isMailSend = false;
                    checkForPenDrive(false);

                }
            });
        }
        //BtnCenter shift report
        else if (blCS) {
            //Daily shift report
            detailsCheck = Util.CENTERSHIFTEPORT;
            view = inflater.inflate(R.layout.chilling_center_report, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertShift = (TextView) view.findViewById(R.id.tvShift);
            tvalertShTime = (TextView) view.findViewById(R.id.tvShiftTime);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvalertD = (TextView) view.findViewById(R.id.tvD);
            tvalertDate = (TextView) view.findViewById(R.id.tvDate);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);
            btnExport = (Button) view.findViewById(R.id.btnExport);
            tvalertDate.setText(etDailyShiftDate.getText().toString());
            DailyShiftList = (ListView) view
                    .findViewById(R.id.lvDailyShiftList);
            // for average values
            tvNumOfFarm = (TextView) view.findViewById(R.id.tvNumOfFarm);
            tvAvgAmount = (TextView) view.findViewById(R.id.tvAvgamount);
            tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
            tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
            tvTotalCollection = (TextView) view
                    .findViewById(R.id.tvTotalCollection);

            LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.lvDetails);
            linearLayout.setVisibility(View.GONE);
            btnExport.setVisibility(View.GONE);

            if (shift.equalsIgnoreCase("M")
                    || shift.equalsIgnoreCase("Morning")) {
                tvalertShTime.setText("Morning");
            } else {
                tvalertShTime.setText("Evening");
            }

            btnExport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    isMailSend = false;
                    checkForPenDrive(false);

                }
            });

        } else if (blTPR) {
            //for total periodic report report
            detailsCheck = Util.sendTotalPeriodicReport;
            view = inflater.inflate(R.layout.daily_shift_report, null);
            tvalertHeader = (TextView) view.findViewById(R.id.tvheader);
            tvalertSociety = (TextView) view.findViewById(R.id.tvSociety);
            tvalertShift = (TextView) view.findViewById(R.id.tvShift);
            tvalertShTime = (TextView) view.findViewById(R.id.tvShiftTime);
            tvalertSociety.setText(new SessionManager(ReportsActivity.this)
                    .getSocietyName());
            tvalertD = (TextView) view.findViewById(R.id.tvD);
            tvalertDate = (TextView) view.findViewById(R.id.tvDate);
            btnPrint = (Button) view.findViewById(R.id.btnPrint);
            btnSend = (Button) view.findViewById(R.id.btnSend);
            tvalertDate.setText(etDailyShiftDate.getText().toString());
            DailyShiftList = (ListView) view
                    .findViewById(R.id.lvDailyShiftList);
            // for average values
            tvNumOfFarm = (TextView) view.findViewById(R.id.tvNumOfFarm);
            tvAvgAmount = (TextView) view.findViewById(R.id.tvAvgamount);
            tvAvgFat = (TextView) view.findViewById(R.id.tvAvgFat);
            tvAvgSnf = (TextView) view.findViewById(R.id.tvAvgSnf);
            tvTotalCollection = (TextView) view
                    .findViewById(R.id.tvTotalCollection);
            tvMemberPeriodic = (TextView) view.findViewById(R.id.tvMember);

            lnAvgSnf = (LinearLayout) view.findViewById(R.id.lnAvgSnf);
            lnAvgFat = (LinearLayout) view.findViewById(R.id.lnAvgFat);
            tvTotalFatKg = (TextView) view.findViewById(R.id.tvTotalFatKg);
            tvTotalSnfKg = (TextView) view.findViewById(R.id.tvTotalSnfKg);

            final TextView tvSh = (TextView) view.findViewById(R.id.tvShift);
            tvSh.setVisibility(View.GONE);
            tvalertD.setVisibility(View.GONE);
            tvalertDate.setVisibility(View.GONE);
            tvalertShTime.setText("From " + etTpStartDate.getText().toString()
                    + " To " + etTpEndDate.getText().toString());
            if (!farmer.equalsIgnoreCase("All farmers")) {
                tvMemberPeriodic.setText("Date");
            }
            tvalertShTime.setGravity(Gravity.CENTER_HORIZONTAL);
            tvalertHeader.setText("Milk bill summary");
            TextView tvMember = (TextView) view.findViewById(R.id.tvMember);
            TextView tvTax = (TextView) view.findViewById(R.id.tvTax);
            TextView tvMilkType = (TextView) view.findViewById(R.id.tvMilkType);
            TextView tvFat = (TextView) view.findViewById(R.id.tvFat);
            tvSnf = (TextView) view.findViewById(R.id.tvSnf);
            TextView tvQuantity = (TextView) view.findViewById(R.id.tvQuality);
            TextView tvRate = (TextView) view.findViewById(R.id.tvRate);
            TextView tvAmount = (TextView) view.findViewById(R.id.tvAmount);
            TextView tvName = (TextView) view.findViewById(R.id.tvName);

            lnAvgSnf.setVisibility(View.GONE);
            lnAvgFat.setVisibility(View.GONE);

            tvMember.setText("Code");
            tvTax.setText("T.Qty");
            tvMilkType.setText("A.Rate");
            tvFat.setText("T.Amount");
            tvSnf.setText("Signature");

            tvQuantity.setVisibility(View.GONE);
            tvRate.setVisibility(View.GONE);
            tvAmount.setVisibility(View.GONE);


            if (amcuConfig.getDisplayNameInReport()) {
                tvName.setText("name");
                tvName.setVisibility(View.VISIBLE);
                tvSnf.setVisibility(View.GONE);
                tvMilkType.setVisibility(View.GONE);

            } else {
                tvSnf.setVisibility(View.VISIBLE);
                tvMilkType.setVisibility(View.VISIBLE);


            }


            btnExport = (Button) view.findViewById(R.id.btnExport);
            btnExport.setVisibility(View.GONE);
            btnSend.setVisibility(View.GONE);
            btnExport.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                    isMailSend = false;
                    checkForPenDrive(false);

                }
            });
        }

        btnPrint.requestFocus();

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                setAdapter();

            }
        });
        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (SplashActivity.sendCount == 0) {
                    isMailSend = true;
                    checkForPenDrive(true);
                }
            }
        });
        btnPrint.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (amcuConfig.getEnableA4Printer()) {
                    alertToSelectPrint();
                    isAlert = false;
                    alertDialog.dismiss();
                } else if (blPUR) {
                    alertToSelectPrint();
                    isAlert = false;
                    alertDialog.dismiss();
                } else {
                    DailyShiftReport();
                    isAlert = false;
                    alertDialog.dismiss();
                }


            }
        });

        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.show();
        alertDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        alertDialog.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    alertDialog.dismiss();
                    isAlert = false;
                } else if (keyCode == KeyEvent.KEYCODE_F2) {

                    if (SplashActivity.sendCount == 0) {
                        checkForPenDrive(true);
                    } else {
                        Toast.makeText(ReportsActivity.this, "Please wait!",
                                Toast.LENGTH_SHORT).show();
                    }

                } else if (keyCode == KeyEvent.KEYCODE_F3) {
                    // getAllRepEntity(detailsCheck);
                    // DailyShiftReport();
                    // isAlert = false;
                    // alertDialog.dismiss();
                } else if (keyCode == KeyEvent.KEYCODE_F4) {
                    DailyShiftReport();
                    isAlert = false;
                    alertDialog.dismiss();
                } else if (keyCode == KeyEvent.KEYCODE_ESCAPE) {
                    alertDialog.dismiss();
                    isAlert = false;
                }
                return false;
            }
        });
    }

    public void setAdapter() {
        if (blDSR) {
//For daily shift report
            rateAdapter = new RateAdapterNew(ReportsActivity.this,
                    databaseHandler.getDailyShiftReport(lnDate, shift,
                            "All farmers",
                            Util.getallSampleDataList(ReportsActivity.this)), 0);
            DailyShiftList.setAdapter(rateAdapter);
            getAllreportsEnt();
            databaseHandler.getChillingCenterReport("All farmers", shift, lnDate, 0, 0, false, false);
            displaySummary();

        } else if (blCS) {
//For daily shift report
            rateAdapter = new RateAdapterNew(ReportsActivity.this,
                    databaseHandler.getDailyCenterShiftReport(
                            Util.getDateDDMMYY(etCenterShiftDate.getText().toString(), 1),
                            centerShift), 7);
            DailyShiftList.setAdapter(rateAdapter);
            getAllreportsEnt();
            setAverageData(Util.REPORT_TYPE_FARMER);

        } else if (blPR) {
            // for periodic report
            if (farmer.equalsIgnoreCase("All farmers")) {
                rateAdapter = new RateAdapterNew(ReportsActivity.this,
                        databaseHandler.getPeriodicReports(lnStartDate, lnEndDate,
                                farmer, null), 0);
                databaseHandler.getChillingCenterReport("All farmers", shift, lnDate, lnStartDate, lnEndDate, true, false);
            } else {
                rateAdapter = new RateAdapterNew(ReportsActivity.this,
                        databaseHandler.getPeriodicReports(lnStartDate, lnEndDate,
                                farmer, null), 4);
                databaseHandler.getChillingCenterReport(farmer, shift, lnDate, lnStartDate, lnEndDate, true, false);
            }
            DailyShiftList.setAdapter(rateAdapter);
            getAllreportsEnt();
            displaySummary();
        } else if (blSales) {
            // for Sales report
            rateAdapter = new RateAdapterNew(ReportsActivity.this,
                    databaseHandler.getPeriodicSalesReports(salesStartTimeStamp,
                            salesEndTimeStamp, Util.SALES_TXN_TYPE_SALES, null), 5);
            DailyShiftList.setAdapter(rateAdapter);
            getAllreportsEnt();
            setAverageData(Util.REPORT_TYPE_SALES);
        } else if (blPUR) {

            //  setAverageData(Util.REPORT_TYPE_TOTAL_PERIODIC);
            DairyPeriodicAdapter dairyPeriodicAdapter = new DairyPeriodicAdapter(ReportsActivity.this, 0, 0, allDairyReportEnt);
            DailyShiftList.setAdapter(dairyPeriodicAdapter);
        } else if (blTotS) {
            //  setAverageData(Util.REPORT_TYPE_TOTAL_PERIODIC);
            TotalShiftAdapter totalShiftAdapter = new TotalShiftAdapter(ReportsActivity.this, 0, allAverageReport);
            DailyShiftList.setAdapter(totalShiftAdapter);
        } else if (blTPR) {
            setAverageData(Util.REPORT_TYPE_TOTAL_PERIODIC);
            TotalPeriodicAdapter tPeriodicAdapter = new TotalPeriodicAdapter(ReportsActivity.this, 0, 0, allReportEntity);
            DailyShiftList.setAdapter(tPeriodicAdapter);
        } else if (blMBR) {
// for member bill register
            rateAdapter = new RateAdapterNew(ReportsActivity.this,
                    databaseHandler.getMemberBillRegister(lnStartDate,
                            lnEndDate, farmerIdStart, farmerIdEnd, farmer, Util.REPORT_TYPE_FARMER), 1);
            DailyShiftList.setAdapter(rateAdapter);
            getAllreportsEnt();
        } else if (blMBS) {
// for member bill summary
            getMemberData();

            billSummaryAdapter = new BillSummaryAdapter(ReportsActivity.this,
                    0, allMemberBillEnt, 2);

            DailyShiftList.setAdapter(billSummaryAdapter);

            setAverageData(Util.REPORT_TYPE_FARMER);

        } else if (blML) {
            // for member list
            rateAdapter = new RateAdapterNew(ReportsActivity.this,
                    databaseHandler.getFarmerDataSociety(String
                            .valueOf(new SessionManager(ReportsActivity.this)
                                    .getCollectionID())), 3);
            DailyShiftList.setAdapter(rateAdapter);
        }
    }

    public void getAllreportsEnt() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                if (blDSR) {
                    allReportEntity = databaseHandler
                            .getDailyShiftReportEntity(lnDate, shift, null);

                } else if (blCS) {
                    allCenterReportEnt = databaseHandler
                            .getAllCenterShiftReport(Util.getDateDDMMYY(etCenterShiftDate.getText().toString(), 1), shift, null);
                } else if (blPR) {
                    allReportEntity = databaseHandler
                            .getPeriodicShiftReportEntity(lnStartDate,
                                    lnEndDate, farmer, null, Util.REPORT_TYPE_FARMER);
                } else if (blMBR) {
                    allReportEntity = databaseHandler
                            .getMemberBillRegisterReportEnt(lnStartDate,
                                    lnEndDate, farmerIdStart, farmerIdEnd, farmer, Util.REPORT_TYPE_FARMER);
                } else if (blSales) {
                    allSalesRecordEntity = databaseHandler.
                            getAllPeriodicSalesReports(salesStartTimeStamp,
                                    salesEndTimeStamp, Util.SALES_TXN_TYPE_SALES, null);
                } else if (blTPR) {
                    allReportEntity = databaseHandler.getTotalPeriodicReport(tpStartTimeStamp,
                            tpEndTimeStamp,
                            Util.REPORT_TYPE_FARMER);
                }
            }
        });
    }

    public void getMemberData() {
        allMemberBillEnt = databaseHandler.getMemberBillSummary(lnStartDate,
                lnEndDate, farmerIdStart, farmerIdEnd, allFarmerId, farmer, Util.REPORT_TYPE_FARMER);
    }

    public void DailyShiftReport() {
        if (blDSR) {
            FormatPrintRecords formatPrintRecords = new FormatPrintRecords(
                    ReportsActivity.this);
            strBuild = formatPrintRecords.onPrintShiftReport(3, shift, lnDate);
        } else if (blCS) {
            FormatPrintRecords formatPrintRecords = new FormatPrintRecords(
                    ReportsActivity.this);
            strBuild = formatPrintRecords.onPrintChillingShiftReport(Util.getDateDDMMYY(etCenterShiftDate.getText().toString(), 1), shift);
        } else if (blPR) {
            if (farmer.equalsIgnoreCase("All Farmers")) {
                session.setReportType(Util.PERIODICREPORT);
            } else {
                session.setReportType(Util.PERIODICREPORTINDIVIDUAL);
            }
            FormatPrintRecords formatPrintRecords = new FormatPrintRecords(
                    ReportsActivity.this);
            strBuild = formatPrintRecords.onPrintPeriodicRecords(lnStartDate, lnEndDate, farmer);
        } else if (blTPR) {

            FormatPrintRecords formatPrintRecords = new FormatPrintRecords(
                    ReportsActivity.this);
            strBuild = formatPrintRecords.onPrintTotalPeriodicRecords(lnStartDate, lnEndDate);
        } else if (blSales) {

            FormatPrintRecords formatPrintRecords = new FormatPrintRecords(
                    ReportsActivity.this);
            strBuild = formatPrintRecords.onPrintSalesRecord(salesStartTimeStamp, salesEndTimeStamp);
        } else if (blMBR) {
            strBuild = tvalertHeader.getText().toString() + "\n" + "\n"
                    + tvalertSociety.getText().toString() + "\n" + " "
                    + tvFromC.getText().toString()
                    + tvDateFrom.getText().toString() + " "
                    + tvToc.getText().toString() + " "
                    + tvDateTo.getText().toString() + "\n" + "\n";

            strBuild = strBuild + "    Date    " + " M " + " FAT " + " SNF "
                    + " QTY " + " Rate " + " Amt " + "\n";

            for (int i = 0; i < allReportEntity.size(); i++) {
                strBuild = strBuild + "  " + allReportEntity.get(i).postDate + " "
                        + allReportEntity.get(i).milkType + " "
                        + allReportEntity.get(i).fat + " "
                        + allReportEntity.get(i).snf + " "
                        + allReportEntity.get(i).quantity + " "
                        + allReportEntity.get(i).rate + " "
                        + allReportEntity.get(i).amount + "\n";

                if (i == allReportEntity.size() - 1) {
                    strBuild = strBuild + "\n" + "\n" + "\n" + "\n" + "\n"
                            + "\n";

                }

            }

        } else if (blMBS) {

            strBuild = tvalertHeader.getText().toString() + "\n" + "\n"
                    + tvalertSociety.getText().toString() + "\n" + " "
                    + tvFromC.getText().toString()
                    + tvDateFrom.getText().toString() + " "
                    + tvFromC.getText().toString() + " "
                    + tvDateTo.getText().toString() + "\n" + "\n";

            strBuild = strBuild + " MEM " + "  QTY  " + "  Rate  "
                    + "  Amount " + "  Sign  " + "\n";

            for (int i = 1; i < allMemberBillEnt.size(); i++) {
                strBuild = strBuild + allMemberBillEnt.get(i).code + "  "
                        + allMemberBillEnt.get(i).quantity + " "
                        + allMemberBillEnt.get(i).rate + " "
                        + allMemberBillEnt.get(i).amount + " " + "        "
                        + "\n";

            }

        }

        if (amcuConfig.getPrinter().equalsIgnoreCase("TVS")) {

            TVSPrinterR();

        } else {
            TherMalPrinterR();
        }

    }

    private void initDatePicker(boolean onCreat) {

        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        if (currentTimeMili == 0) {
            c.set(year, month, day, 23, 59, 59);
            currentTimeMili = c.getTimeInMillis();
        }

        String _month = String.valueOf(month + 1), _day = String.valueOf(day);

        if (month + 1 < 10)
            _month = "0" + String.valueOf(month + 1);

        if (day < 10)
            _day = "0" + String.valueOf(day);

        if (!onCreat) {

            etDailyShiftDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(_day).append("-").append(_month).append("-")
                    .append(year));

            etEndMBR.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(_day).append("-").append(_month).append("-")
                    .append(year));

            etTpEndDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(_day).append("-").append(_month).append("-")
                    .append(year));

            etSalesEndDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(_day).append("-").append(_month).append("-")
                    .append(year));

            etPurchaseEnd.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(_day).append("-").append(_month).append("-")
                    .append(year));

            etTotalShift.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(_day).append("-").append(_month).append("-")
                    .append(year));

            etCenterShiftDate.setText(new StringBuilder()
                    // Month is 0 based, just add 1
                    .append(_day).append("-").append(_month).append("-")
                    .append(year));
            EndDateTimeStamp = Long.parseLong(new StringBuilder().append(year)
                    .append(_month).append(_day).toString());
            tpEndTimeStamp = Long.parseLong(new StringBuilder().append(year)
                    .append(_month).append(_day).toString());

            salesEndTimeStamp = currentTimeMili;
            purEndTimeStamp = currentTimeMili;

            longToday = Util.getDateInLongFormat(Util.getTodayDateAndTime(1, ReportsActivity.this, false));
        }

    }

    public void sendEmail(final boolean mailSend) {

        isWrittentoEncFile = true;
        final WriteExcel test = new WriteExcel();
        //   File root = new File(Util.rootFileName);
        File fileSmartAmcu = new File(Util.rootFileName, "smartAmcuReports");

        if (mailSend) {
            fileSmartAmcu = new File(Environment
                    .getExternalStorageDirectory().getAbsolutePath(), "smartAmcuReports");
        }

        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }

        //set the file name
        setThefileName(fileSmartAmcu);
        new Thread(new Runnable() {
            public void run() {

                test.setOutputFile(gpxfile.toString());

                try {
                    if (detailsCheck == Util.sendMemberBillSummary && mailSend) {
                        test.write(ReportsActivity.this, 4, allMemberBillEnt);
                    } else if ((detailsCheck == Util.sendDailyReport) && mailSend) {
                        //for daily shift report
                        test.write(ReportsActivity.this, 8, allDetailRep);
                    }
//For chilling center report
                    else if ((detailsCheck == Util.CENTERSHIFTEPORT) && mailSend) {
                        //for daily shift report
                        test.write(ReportsActivity.this, 14, allDetailRep);
                    } else if (detailsCheck == Util.sendDailyReport) {
                        String file = gpxfile.toString().replace(".xls", "." + AppConstants.CONSOLIDATED_EXTENSION);
                        checkFileValidation(allDetailRep);
                        if (isWrittentoEncFile) {
                            //  Util.toSendEncryptedRecords(ReportsActivity.this, allDetailRep, file, 8);
                            reportHelper.createEncryptedFileFromReport(allDetailRep, file);
                        }

                    } else if ((detailsCheck == Util.sendMemberBillRegister) && mailSend) {
                        test.write(ReportsActivity.this, 2, allDetailRep);
                    } else if ((detailsCheck == Util.sendMemberBillRegister)) {

                        String file = gpxfile.toString().replace(".xls", "." + AppConstants.CONSOLIDATED_EXTENSION);

                        // Util.toSendEncryptedRecords(ReportsActivity.this, allDetailRep, file, 8);
                        reportHelper.createEncryptedFileFromReport(allDetailRep, file);
                    } else if (detailsCheck == Util.sendPeriodicReport && mailSend) {
                        //for periodic reports
                        test.write(ReportsActivity.this, 10, allDetailRep);

                    } else if ((detailsCheck == Util.sendPeriodicReport)) {
                        String file = gpxfile.toString().replace(".xls", "." + AppConstants.CONSOLIDATED_EXTENSION);
                        checkFileValidation(allDetailRep);
                        if (isWrittentoEncFile) {
                            // Util.toSendEncryptedRecords(ReportsActivity.this, allDetailRep, file, 10);
                            reportHelper.createEncryptedFileFromReport(allDetailRep, file);
                        }
                    } else if (detailsCheck == Util.sendSalesReport && mailSend) {
                        test.write(ReportsActivity.this, 11, allSalesRecordEntity);
                    } else if (detailsCheck == Util.sendDairyReport && mailSend) {
                        test.write(ReportsActivity.this, 12, allDairyReportEnt);
                    }
                    new SessionManager(ReportsActivity.this)
                            .setFileName(gpxfile.toString());
                    isWrittenToFile = true;
                } catch (WriteException e) {
                    isWrittenToFile = false;
                    e.printStackTrace();
                } catch (IOException e) {
                    isWrittenToFile = false;
                    e.printStackTrace();
                } catch (Exception e) {
                    isWrittenToFile = false;
                    e.printStackTrace();
                }
                System.out.println("Please check the result file " + gpxfile);

                myHandler.post(updateRunnable);

            }
        }).start();

        updateRunnable = new Runnable() {

            @Override
            public void run() {
                if (mailSend && isWrittenToFile) {
                    if (Util.isNetworkAvailable(ReportsActivity.this)) {
                        Intent intentService = new Intent(ReportsActivity.this,
                                SendEmail.class);
                        startService(intentService);

                       */
/* if (saveSession.getSendEmailToConfigureIDs() ||
                                session.getReportType() == Util.PERIODICREPORT ||
                                session.getReportType() == Util.PERIODICREPORTINDIVIDUAL)*//*


                        if (amcuConfig.getSendEmailToConfigureIDs()) {
                            Toast.makeText(ReportsActivity.this, "Sending email...",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ReportsActivity.this, "Please enable send email option from Configuration...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (amcuConfig.getSendEmailToConfigureIDs()) {
                            Toast.makeText(ReportsActivity.this, "Please check the network connectivity!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else if (isWrittenToFile) {

                    receiverForWrite.send(Activity.RESULT_OK, null);
                } else {
                    receiverForWrite.send(Activity.RESULT_CANCELED, null);
                }
            }
        };
    }

    public void getAllRepEntity(int i, boolean mailSend) {
        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
        try {
            if (i == Util.sendDailyReport) {
                Util.setDailyDateOrShift(etDailyShiftDate.getText().toString(),
                        shift);
                allDetailRep = Util.getShiftReport(getApplicationContext(),
                        null, 3, shift, lnDate);
                // allDetailRep = dbHandler.Util.(lnDate,
                // shift);
            } else if (i == Util.sendMemberBillRegister) {
                allDetailRep = dbHandler.getMemberBill(lnStartDate, lnEndDate,
                        farmerIdStart, farmerIdEnd, Util.REPORT_TYPE_FARMER);
            } else if (i == Util.sendPeriodicReport) {
                allDetailRep = dbHandler.getPeriodicShiftReportEntity(
                        lnStartDate, lnEndDate, farmer, null, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close;
        sendEmail(mailSend);
    }


    public void getAllFarmerId() {
        DatabaseHandler dbHand = DatabaseHandler.getDatabaseInstance();
        try {
            allFarmerId = farmerDao.getAllFarmerIds();
            allFarmerId.add(0, "All farmers");
            allFarmerAndCenterId = dbHand.getAllFarmerIdAndCenterId();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AverageReportDetail getAverageReportDetails(String type) {

        AverageReportDetail avgReportdetail;

        if (type.equalsIgnoreCase(Util.REPORT_TYPE_FARMER)) {
            avgReportdetail = Util.getAverageData();
        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_TOTAL_PERIODIC)) {
            avgReportdetail = Util.getTotalPeriodicDetail();
        } else if (type.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            avgReportdetail = Util.avgChillingDetails;
        } else {
            avgReportdetail = Util.getSalesAverageData();
        }

        return avgReportdetail;
    }

    public void setAverageData(String type) {
        AverageReportDetail avgReportdetail = getAverageReportDetails(type);


        tvTotalFatKg.setText(String.valueOf(avgReportdetail.totalFatKg));
        tvTotalSnfKg.setText(String.valueOf(avgReportdetail.totalSnfKg));
        tvAvgAmount.setText(String.valueOf(avgReportdetail.totalAmount));
        tvAvgFat.setText(String.valueOf(avgReportdetail.avgFat));
        tvAvgSnf.setText(String.valueOf(avgReportdetail.avgSnf));
        tvTotalCollection.setText(String.valueOf(avgReportdetail.totalQuantity));
        tvNumOfFarm.setText(String.valueOf(avgReportdetail.totalMember));
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    public void TherMalPrinterR() {


        if (blDSR) {

            parsingPrinterData.print(strBuild,
                    PrinterManager.printShiftReport, etDailyShiftDate
                            .getText().toString(), null, shift);
        } else if (blCS) {

            parsingPrinterData.print(strBuild,
                    PrinterManager.printChillingReport, etCenterShiftDate
                            .getText().toString(), null, shift);
        } else if (blPR) {

            parsingPrinterData.print(strBuild,
                    PrinterManager.printperiodicReort, etStartMBR.getText().toString(), etEndMBR.getText().toString(), null);

        } else if (blSales) {
            parsingPrinterData.print(strBuild,
                    PrinterManager.printsalesReport, etSalesStartDate.getText().toString(), etSalesEndDate.getText().toString(), null);
        } else if (blTPR) {
            parsingPrinterData.print(strBuild,
                    PrinterManager.printtotalPR, etTpStartDate.getText().toString(), etTpEndDate.getText().toString(), null);
        } else if (blMBS) {
            parsingPrinterData.print(strBuild,
                    PrinterManager.printtotalPR, etStartMBR.getText().toString(), etEndMBR.getText().toString(), null);
        } else {
            parsingPrinterData.print(strBuild,
                    PrinterManager.printtotalPR, etStartMBR.getText().toString(), etEndMBR.getText().toString(), null);
        }
    }

    public void TVSPrinterR() {

        if (blDSR) {
            parsingPrinterData.startTVS(strBuild,
                    PrinterManager.printShiftReport, etDailyShiftDate.getText().toString(),
                    null, shift);
        } else if (blPR) {
            parsingPrinterData.startTVS(strBuild,
                    PrinterManager.printperiodicReort, etStartMBR.getText().toString(),
                    etEndMBR.getText().toString(), shift);

        } else if (blCS) {
            parsingPrinterData.startTVS(strBuild,
                    PrinterManager.printChillingReport, etCenterShiftDate
                            .getText().toString(), null, shift);
        } else if (blPR) {
            parsingPrinterData.startTVS(strBuild,
                    PrinterManager.printperiodicReort, etStartMBR.getText().toString(),
                    etEndMBR.getText().toString(), shift);
        } else if (blSales) {
            parsingPrinterData.startTVS(strBuild,
                    PrinterManager.printsalesReport, etSalesStartDate.getText().toString(),
                    etSalesEndDate.getText().toString(), shift);
        } else if (blTPR) {
            parsingPrinterData.startTVS(strBuild,
                    PrinterManager.printtotalPR, etTpStartDate.getText().toString(),
                    etTpEndDate.getText().toString(), shift);
        } else if (blMBS) {
            parsingPrinterData.print(strBuild,
                    PrinterManager.printtotalPR, etStartMBR.getText().toString(), etEndMBR.getText().toString(), null);
        } else {
            parsingPrinterData.startTVS(strBuild,
                    PrinterManager.printReport, etStartMBR.getText().toString(),
                    etEndMBR.getText().toString(), shift);
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F9:
                Util.Logout(ReportsActivity.this);
                finish();
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(ReportsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(ReportsActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(ReportsActivity.this, null);
                return true;


            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void checkForPenDrive(boolean mailSend) {

        if (mailSend == true) {
            getAllRepEntity(detailsCheck, mailSend);
            isAlert = false;
            alertDialog.dismiss();
        } else if (Util.checkForPendrive()) {
            Toast.makeText(ReportsActivity.this, "Copying the file please wait..", Toast.LENGTH_SHORT).show();
            getAllRepEntity(detailsCheck, mailSend);
            isAlert = false;
            alertDialog.dismiss();
        } else {
//            Toast.makeText(ReportsActivity.this, "This part for testing", Toast.LENGTH_LONG).show();
//            getAllRepEntity(detailsCheck);
//            encryptionTech();

            usbAlert();
        }
    }

    public void usbAlert() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ReportsActivity.this);

        // set title
        alertDialogBuilder.setTitle("Alert!");

        // set dialog message
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.pendrive_alert))
                .setCancelable(false)
                .setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(ReportsActivity.this, "Copying the file please wait..", Toast.LENGTH_SHORT).show();
                                getAllRepEntity(detailsCheck, isMailSend);
                                isAlert = false;
                                dialog.cancel();
                                alertDialog.dismiss();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    // Setup the callback for when data is received from the service
    public void setupServiceReceiver() {
        receiverForWrite = new WriteRecordReceiver(new Handler());
        // This is where we specify what happens when data is received from the
        // service
        receiverForWrite.setReceiver(new WriteRecordReceiver.Receiver() {
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    if (!isWrittentoEncFile) {

                    } else if (Util.rootFileName.contains("usbhost")) {
                        Toast.makeText(ReportsActivity.this, "File has been successfully written in Pendrive.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(ReportsActivity.this, "File has been successfully written in Internal storage.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (isWrittentoEncFile) {
                        Toast.makeText(ReportsActivity.this, "Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void setFarmer() {
        farmer = "All farmers";
        ArrayAdapter myAdap = (ArrayAdapter) spFarmer.getAdapter();
        int spinnerPosition = myAdap.getPosition(farmer);
        spFarmer.setSelection(spinnerPosition);

    }

    public void checkFileValidation(ArrayList<ReportEntity> allReportsEnt) {
        if ((allReportsEnt == null)) {
            //For shift report
            Util.displayErrorToast("No data to write!", ReportsActivity.this);
            isWrittentoEncFile = false;
        } else if (detailsCheck == Util.sendPeriodicReport && allReportsEnt.size() < 1) {
            //for periodic report
            Util.displayErrorToast("No data to write!", ReportsActivity.this);
            isWrittentoEncFile = false;
        } else {
            isWrittentoEncFile = true;
        }
    }

    public void checkifSalesEnable() {

        if (amcuConfig.getEnableDairyReport()) {
            rlPurchase.setVisibility(View.VISIBLE);
            btnPurchase.setVisibility(View.VISIBLE);
            lnPurchaseBody.setVisibility(View.VISIBLE);
        }

        if (amcuConfig.getEnableCenterCollection()) {
            rlCenterShiftReport.setVisibility(View.GONE);
            lnCenterShiftHeader.setVisibility(View.GONE);
        } else {
            rlCenterShiftReport.setVisibility(View.GONE);
            lnCenterShiftHeader.setVisibility(View.GONE);
        }


        if (amcuConfig.getEnableSales()) {
            //  btnTrigger.setVisibility(View.VISIBLE);

            btnTrigger.setVisibility(View.GONE);

            rlSales.setVisibility(View.VISIBLE);
            lnSales.setVisibility(View.VISIBLE);

//            rlShiftSales.setVisibility(View.VISIBLE);
//            lnShiftSalesBody.setVisibility(View.VISIBLE);

            rlTotalShift.setVisibility(View.GONE);
            lnTotalShift.setVisibility(View.GONE);
            btnTotalShift.setVisibility(View.GONE);
            lnTotalShiftHeader.setVisibility(View.GONE);

            // btnSales.setVisibility(View.VISIBLE);
            //  btnSalesShift.setVisibility(View.VISIBLE);

        } else {
            btnTrigger.setVisibility(View.GONE);
            rlSales.setVisibility(View.GONE);
            lnSales.setVisibility(View.GONE);

            rlTotalShift.setVisibility(View.GONE);
            lnTotalShift.setVisibility(View.GONE);
            btnTotalShift.setVisibility(View.GONE);
            lnTotalShiftHeader.setVisibility(View.GONE);
            btnSales.setVisibility(View.GONE);

        }

        if (BuildConfig.FLAVOR.equalsIgnoreCase("hatsun")) {
            btnTrigger.setVisibility(View.GONE);
            btnTrigger.setVisibility(View.VISIBLE);

            rlSales.setVisibility(View.GONE);
            lnSales.setVisibility(View.GONE);
            rlTotalShift.setVisibility(View.GONE);
            lnTotalShift.setVisibility(View.GONE);
            rlPurchase.setVisibility(View.GONE);
            lnPurchaseBody.setVisibility(View.GONE);
            btnSales.setVisibility(View.GONE);
            btnTotalShift.setVisibility(View.GONE);
            btnPurchase.setVisibility(View.GONE);
        }
    }

    public void onClickTrigger() {
        if (!isSalesEnable) {
            isSalesEnable = true;
            rlSales.setVisibility(View.VISIBLE);
            lnSales.setVisibility(View.VISIBLE);
            rlTotalShift.setVisibility(View.VISIBLE);
            lnTotalShift.setVisibility(View.VISIBLE);
            rlPurchase.setVisibility(View.VISIBLE);
            lnPurchaseBody.setVisibility(View.VISIBLE);
            btnSales.setVisibility(View.VISIBLE);
            btnTotalShift.setVisibility(View.VISIBLE);
            btnPurchase.setVisibility(View.VISIBLE);
        } else {
            isSalesEnable = false;
            rlSales.setVisibility(View.GONE);
            lnSales.setVisibility(View.GONE);
            rlTotalShift.setVisibility(View.GONE);
            lnTotalShift.setVisibility(View.GONE);
            rlPurchase.setVisibility(View.GONE);
            lnPurchaseBody.setVisibility(View.GONE);
            btnSales.setVisibility(View.GONE);
            btnTotalShift.setVisibility(View.GONE);
            btnPurchase.setVisibility(View.GONE);
        }

    }

    public void checkVisibilityForA4Print() {

        if (amcuConfig.getEnableA4Printer()) {
            btnPrintReport.setVisibility(View.GONE);
            lnPrintFarmer.setVisibility(View.VISIBLE);
            btnPrintFarmer.setVisibility(View.VISIBLE);
        } else {
            btnPrintReport.setVisibility(View.GONE);
            lnPrintFarmer.setVisibility(View.GONE);
            btnPrintFarmer.setVisibility(View.GONE);

        }
    }

    public void getTempReports(int i) {

        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
        try {
            if (i == Util.sendDailyReport) {
                Util.setDailyDateOrShift(etDailyShiftDate.getText().toString(),
                        shift);
                allDetailRep = Util.getShiftReport(getApplicationContext(),
                        null, 0, shift, lnDate);
                // allDetailRep = dbHandler.Util.(lnDate,
                // shift);
            } else if (i == Util.sendMemberBillRegister) {
                allDetailRep = dbHandler.getMemberBill(lnStartDate, lnEndDate,
                        farmerIdStart, farmerIdEnd, Util.REPORT_TYPE_FARMER);
            } else if (i == Util.sendPeriodicReport) {
                allDetailRep = dbHandler.getPeriodicShiftReportEntity(
                        lnStartDate, lnEndDate, farmer, null, Util.REPORT_TYPE_FARMER);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close;
    }

    public void alertToSelectPrint() {

        getTempReports(detailsCheck);

        File fileSmartAmcu = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath(), "smartAmcuReports");

        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }

        setThefileName(fileSmartAmcu);
        final boolean isWr = createExcelForPint("report");

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                ReportsActivity.this);

        LayoutInflater inflater = (LayoutInflater) ReportsActivity.this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_selectprinter, null);

        Button tvA4Printer = (Button) view.findViewById(R.id.tvA4Printer);
        Button tvPosPrinter = (Button) view.findViewById(R.id.tvPosprinter);


        if (blPUR) {
            tvPosPrinter.setVisibility(View.GONE);
            tvA4Printer.requestFocus();
        } else {
            tvPosPrinter.requestFocus();
        }

        tvA4Printer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWr) {
                    printViaShareIntent(gpxfile.toString());
                    alertDialogPrint.dismiss();
                } else {
                    Util.displayErrorToast("No report found, please try again!", ReportsActivity.this);
                    alertDialogPrint.dismiss();
                }

            }
        });

        tvPosPrinter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                DailyShiftReport();
                isAlert = false;
                alertDialogPrint.dismiss();

            }
        });

        alertDialogPrint = alertBuilder.create();
        alertDialogPrint.setCanceledOnTouchOutside(false);
        alertDialogPrint.setView(view);
        alertDialogPrint.show();


    }

    public boolean createExcelForPint(String name) {

        isWrittenToFile = false;

        if (name.equalsIgnoreCase("farmer")) {
            Util.displayErrorToast("Please wait for excel sheet!", ReportsActivity.this);
        }


        ArrayList<FarmerEntity> allFarmList = getAllChillingData();

        final WriteExcel test = new WriteExcel();
        String date = Util.getTodayDateAndTime(1, ReportsActivity.this, false);
        date = date.replace("-", "");

        final File fileSmartAmcu = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath(), "smartAmcuReports");

        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }


        try {

            if (name.equalsIgnoreCase("farmer")) {
                farmerFile = new File(fileSmartAmcu, new SessionManager(ReportsActivity.this)
                        .getSocietyName().replace(" ", "") + "_" + date + "_farmerList.xls");

                test.setOutputFile(farmerFile.toString());
                if (allFarmList.size() > 0) {
                    test.write(ReportsActivity.this, 9, allFarmList);
                    new SessionManager(ReportsActivity.this).setFileName(farmerFile
                            .toString());
                    isWrittenToFile = true;

                } else {
                    Toast.makeText(ReportsActivity.this, "No farmer list to export!",
                            Toast.LENGTH_LONG).show();
                }
            } else if (gpxfile != null) {
                test.setOutputFile(gpxfile.toString());
                if (detailsCheck == Util.sendMemberBillSummary) {
                    test.write(ReportsActivity.this, 4, allMemberBillEnt);
                } else if ((detailsCheck == Util.sendDailyReport)) {
                    //for daily shift report
                    test.write(ReportsActivity.this, 8, allDetailRep);
                } else if ((detailsCheck == Util.sendMemberBillRegister)) {
                    test.write(ReportsActivity.this, 2, allDetailRep);
                } else if (detailsCheck == Util.sendPeriodicReport) {
                    //for periodic reports
                    test.write(ReportsActivity.this, 10, allDetailRep);

                } else if (detailsCheck == Util.sendSalesReport) {
                    test.write(ReportsActivity.this, 11, allSalesRecordEntity);
                } else if (detailsCheck == Util.sendDairyReport) {
                    test.write(ReportsActivity.this, 12, allDairyReportEnt);
                } else if (detailsCheck == Util.sendTotalPeriodicReport) {
                    test.write(ReportsActivity.this, 13, allReportEntity);
                }

                isWrittenToFile = true;

            }


        } catch (WriteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isWrittenToFile) {
            receiverForWrite.send(Activity.RESULT_OK, null);

        } else {
            receiverForWrite.send(Activity.RESULT_CANCELED, null);
        }

        return isWrittenToFile;
    }

    public void printViaShareIntent(String path) {

        Util.displayErrorToast(new File(path).toString(), ReportsActivity.this);

        try {
            Intent printIntent = new Intent(Intent.ACTION_VIEW);

            printIntent.setPackage("cn.wps.moffice_eng");
//        "text/html"
//        "text/plain"
            printIntent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.ms-excel");
            printIntent.putExtra("return", true);
            startActivity(printIntent);
        } catch (Exception e) {
            e.printStackTrace();

            Util.displayErrorToast("No application found to read excel sheet!", ReportsActivity.this);
        }
    }

    public ArrayList<FarmerEntity> getAllChillingData() {

        DatabaseHandler databaseHelper = DatabaseHandler.getDatabaseInstance();
        ArrayList<FarmerEntity> allFarmList = null;

        try {
            allFarmList = (ArrayList<FarmerEntity>) farmerDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close statement

        return allFarmList;
    }

    public void setThefileName(File fileSmartAmcu) {
        String dateTime = etDailyShiftDate.getText().toString();
        dateTime = dateTime.replace("-", "_");
        String Shift = (shift.equalsIgnoreCase("M") ? "Morning" : "Evening");
        if (detailsCheck == Util.sendDailyReport) {

            gpxfile = new File(fileSmartAmcu, new SessionManager(ReportsActivity.this)
                    .getSocietyName().replace(" ", "")
                    + "_"
                    + dateTime
                    + "_"
                    + Shift + "_Shift_Report.xls");
            session.setReportType(Util.sendDailyReport);
        } else if (detailsCheck == Util.CENTERSHIFTEPORT) {
            gpxfile = new File(fileSmartAmcu, new SessionManager(ReportsActivity.this)
                    .getSocietyName().replace(" ", "")
                    + "_"
                    + dateTime
                    + "_"
                    + Shift + "accumulative_shift_Report.xls");
            session.setReportType(Util.CENTERSHIFTEPORT);
        } else if (detailsCheck == Util.sendMemberBillRegister) {

            gpxfile = new File(fileSmartAmcu,
                    new SessionManager(ReportsActivity.this).getSocietyName()
                            + "_" + "Member_Bill_Report.xls");
            session.setReportType(Util.sendMemberBillRegister);
        } else if (detailsCheck == Util.sendMemberBillSummary) {
            gpxfile = new File(fileSmartAmcu,
                    new SessionManager(ReportsActivity.this).getSocietyName()
                            + "_" + "Bill_Summary.xls");
            session.setReportType(Util.sendMemberBillSummary);
        } else if (detailsCheck == Util.sendPeriodicReport) {
            String add = tvalertShTime.getText().toString().replace(" ", "");
            add = add.replace("-", "");
            add = add.replace("From", "");
            if (farmer.equalsIgnoreCase("All farmers")) {
                Util.PERIODIC_SUBJECT = session.getCollectionID() + " " + session.getSocietyName() + " Periodic reports from "
                        + etStartMBR.getText().toString() + " to "
                        + etEndMBR.getText().toString();
                session.setReportType(Util.PERIODICREPORT);
            } else {
                Util.PERIODIC_SUBJECT = session.getCollectionID() + " " + session.getSocietyName() + " Periodic reports from "
                        + etStartMBR.getText().toString() + " to "
                        + etEndMBR.getText().toString() + " for farmerID " + farmer;
                add = add + "FarmerId_" + farmer;
                add = add.replace(" ", "");
                session.setReportType(Util.PERIODICREPORTINDIVIDUAL);
            }

            gpxfile = new File(fileSmartAmcu, new SessionManager(ReportsActivity.this)
                    .getSocietyName().replace(" ", "") + "_" + add + "Periodic_Report.xls");
        } else if (detailsCheck == Util.sendSalesReport) {
            String add = tvalertShTime.getText().toString().replace(" ", "");
            add = add.replace("-", "");
            add = add.replace("From", "");

            Util.SALES_SUBJECT = session.getCollectionID() + " " + session.getSocietyName() + " Sales reports from "
                    + etSalesStartDate.getText().toString() + " to "
                    + etSalesEndDate.getText().toString();

            session.setReportType(Util.SALESREPORT);


            gpxfile = new File(fileSmartAmcu, new SessionManager(ReportsActivity.this)
                    .getSocietyName().replace(" ", "") + "_" + add + "Sales_periodic_Report.xls");

        } else if (detailsCheck == Util.sendDairyReport) {
            String add = tvalertShTime.getText().toString().replace(" ", "");
            add = add.replace("-", "");
            add = add.replace("From", "");

            Util.DAIRY_SUBJECT = session.getCollectionID() + " " + session.getSocietyName() + " Sales reports from "
                    + etPurchaseStart.getText().toString() + " to "
                    + etPurchaseEnd.getText().toString();

            session.setReportType(Util.DAIRYREPORT);
            gpxfile = new File(fileSmartAmcu, new SessionManager(ReportsActivity.this)
                    .getSocietyName().replace(" ", "") + "_" + add + "Dairy_periodic_Report.xls");
        } else if (detailsCheck == Util.sendTotalPeriodicReport) {
            String add = tvalertShTime.getText().toString().replace(" ", "");
            add = add.replace("-", "");
            add = add.replace("From", "");

            Util.DAIRY_SUBJECT = session.getCollectionID() + " " + session.getSocietyName() + " Sales reports from "
                    + etTpStartDate.getText().toString() + " to "
                    + etTpEndDate.getText().toString();

            gpxfile = new File(fileSmartAmcu, new SessionManager(ReportsActivity.this)
                    .getSocietyName().replace(" ", "") + "_" + add + "Total_bill_summary.xls");
        }

    }

    public void getAllDairyEntity() {
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        try {
            allDairyReportEnt = dbh.getAllDairyReport(purStartTimeStamp, purEndTimeStamp, Util.REPORT_TYPE_FARMER);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close;
    }


    public List getAllpackageList() {

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List pkgAppsList = getPackageManager().queryIntentActivities(mainIntent, 0);
        return pkgAppsList;
    }

    public ArrayList<AverageReportDetail> getTotalShiftReport() {

        ArrayList<AverageReportDetail> allAverageReportdetail = new ArrayList<>();

        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();


        */
/*AverageReportDetail colAverageRepDet = dbHandler.getTotalCollectionShiftReport(Util.getDateDDMMYY(etTotalShift.getText().toString(), 1),
                shift, null, null, null);*//*

        AverageReportDetail colAverageRepDet = Util.getAverageReportDetail(
                collectionRecordDao.findAllByDateAndShift(
                        Util.getDateDDMMYY(etTotalShift.getText().toString(), 1), shift), ReportsActivity.this);
        //Removed database close;

        if (colAverageRepDet != null) {

            dbHandler = DatabaseHandler.getDatabaseInstance();

            AverageReportDetail salesAvgReport = dbHandler.getTotalCollectionShiftReport(
                    Util.getDateDDMMYY(etTotalShift.getText().toString(), 1), shift,
                    Util.REPORT_TYPE_SALES, Util.SALES_TXN_TYPE_SALES, null);

            if (salesAvgReport != null) {
                allAverageReportdetail.add(salesAvgReport);
            }


            AverageReportDetail truckNormalReport = dbHandler.getTotalCollectionShiftReport(
                    Util.getDateDDMMYY(etTotalShift.getText().toString(), 1), shift,
                    Util.REPORT_TYPE_SALES, Util.SALES_TXN_TYPE_SALES, Util.SALES_TXN_SUBTYPE_NORMAL);

            if (truckNormalReport != null) {
                allAverageReportdetail.add(truckNormalReport);
            }

            AverageReportDetail truckCOBReport = dbHandler.getTotalCollectionShiftReport(
                    Util.getDateDDMMYY(etTotalShift.getText().toString(), 1), shift,
                    Util.REPORT_TYPE_SALES, Util.SALES_TXN_TYPE_SALES, Util.SALES_TXN_SUBTYPE_COB);
            if (truckCOBReport != null) {
                allAverageReportdetail.add(truckCOBReport);
            }


            //Removed database close;

            return allAverageReportdetail;
        } else {
            AverageReportDetail ard = new AverageReportDetail();

          */
/*  ard.avgRate = "Avg rate";
            ard.totalMember = "Total Mem";
            ard.totalQuantity = "Total Qty";
            ard.totalAmount = "Total Amt";*//*


            allAverageReportdetail.add(ard);

            return null;
        }

    }


    public void displaySummary() {

        AverageReportDetail avgReportDetailsFarmer = getAverageReportDetails(Util.REPORT_TYPE_FARMER);
        AverageReportDetail avgReportDetailsCenter = getAverageReportDetails(Util.REPORT_TYPE_MCC);

        ValidationHelper validationHelper = new ValidationHelper();

        if ((avgReportDetailsFarmer == null || avgReportDetailsFarmer.totalMember < 1) &&
                (avgReportDetailsCenter != null && avgReportDetailsCenter.totalMember > 0)) {
            tvNumOfFarmText.setText("Total centers");
            setAverageData(Util.REPORT_TYPE_MCC);
        } else {
            setAverageData(Util.REPORT_TYPE_FARMER);
        }

    }


    public boolean validateAndErrorToast(String date, String startDate, String endDate) {
        boolean returnValue = true;
        long lnTodayDate = Util.getDateInLongFormat(Util.getTodayDateAndTime(7, ReportsActivity.this, false));

        if (date != null) {
            long lnDate = Util.getDateInLongFormat(date);
            if (lnDate > lnTodayDate) {
                Toast.makeText(ReportsActivity.this, "Date should be less than today date", Toast.LENGTH_SHORT).show();
                returnValue = false;
            }

        } else {
            long lnStartDate = Util.getDateInLongFormat(startDate);
            long lnEndDate = Util.getDateInLongFormat(endDate);

            if (lnEndDate > lnTodayDate) {
                Toast.makeText(ReportsActivity.this, "End Date should be less than today date", Toast.LENGTH_SHORT).show();
                returnValue = false;
            } else if (lnEndDate < lnStartDate) {
                Toast.makeText(ReportsActivity.this, "End Date should be greater than start date", Toast.LENGTH_SHORT).show();
                returnValue = false;
            }

        }

        return returnValue;

    }


}

*/
