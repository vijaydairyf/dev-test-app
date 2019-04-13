package com.devapp.smartcc.report;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.helper.ReportHelper;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.FormatPrintHelper;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.adapters.ReportAdapter;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import static com.devapp.smartcc.report.MemberReportActivity.AMOUNT_STATE;
import static com.devapp.smartcc.report.MemberReportActivity.ID_STATE;
import static com.devapp.smartcc.report.MemberReportActivity.QUANTITY_STATE;
import static com.devapp.smartcc.report.MemberReportActivity.RATE_STATE;
import static com.devapp.smartcc.report.MemberReportActivity.TIME_STATE;

/**
 * Created by u_pendra on 28/3/17.
 */

public class MccReportActivity extends AppCompatActivity implements View.OnClickListener {


    static final int UP_ARROW = android.R.drawable.arrow_up_float;
    static final int DOWN_ARROW = android.R.drawable.arrow_down_float;
    private static final String ASC_OR_DESC = "ASC";
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    private final String DETAILS = "DETAILS";
    private final String SUMMARY = "SUMMARY";
    RecyclerView recycleView;
    TextView tvNoDataFound;
    ReportAdapter reportAdapter;
    Button btnNext, btnCancel, btnFilter, btnToggle, btnSort, btnPrint;
    String query;
    DatabaseEntity dbe;
    AlertDialog dialog;
    ArrayList<ReportEntity> reportList = null;
    ArrayList<ReportEntity> tempReportList = new ArrayList<>();
    private TextView tvHMccId, tvHRouteId, tvHDate, tvHFat,
            tvHSnf, tvHFatKg, tvHSnfKg, tvHQty, tvHRate, tvHAmount, tvHClr;
    private String route, mcc, fromDate, toDate, shift, cattleType;
    private int CURRENT_VIEW = 0;
    private DatabaseHandler mDatabaseHandler;
    private SmartCCUtil smartCCUtil;
    private CollectionRecordDao collectionRecordDao;
    private SessionManager sessionManager;
    private boolean isAscending = true;
    private int sortId;
    private AlertDialog printDialog;
    private PrinterManager printerManager;
    private AmcuConfig amcuConfig;
    private boolean allowHeader = true;//FIXME Refer FilterMemberReportActivity
    private String recordCompleteStatus;
    private RelativeLayout rlListHeader;
    public boolean isEnter = false;
    boolean shouldPrint = false;
    Queue<String> queue = new LinkedBlockingQueue<>();
    long count = 0;
    private ProgressBar progressBar;
    private Handler adapterHandler = new Handler();
    private Runnable adapterRunnable;
    private String threadNameReport = "Report";
    private boolean isPause = false;
    private Handler printHandler = new Handler();
    private Runnable printRunnable;


    @Override
    protected void onStart() {
        super.onStart();
        CURRENT_VIEW = ReportAdapter.SHORT_VIEW;


        setAdapter();
        tempReportList = new ReportHelper(MccReportActivity.this).sortByConfiguration(
                tempReportList, sessionManager.getKeySetLastMccSorting(),
                sessionManager.getKeySetLastMccSortingOrder());
        reportAdapter.updateList(tempReportList);
        reportAdapter.notifyDataSetChanged();

        if (sessionManager.getKeySetLastMccSortingOrder().equalsIgnoreCase("ASC")) {
            isAscending = true;
        } else {
            isAscending = false;
        }
    }

    private void sort(final View view) {
        resetRightDrawable(view);
        tempReportList = new ArrayList<>(reportList);
        Collections.sort(tempReportList, new Comparator<ReportEntity>() {
            @Override
            public int compare(ReportEntity o1, ReportEntity o2) {
                switch (view.getId()) {
                    case R.id.tvHMccId: {
                        sessionManager.setKeySetLastMccSorting(ReportHintConstant.MEMBER_ID);
                        if (isAscending) {
                            return o1.farmerId.compareTo(o2.farmerId);
                        } else {
                            return o2.farmerId.compareTo(o1.farmerId);
                        }
                    }
                    case R.id.tvHRouteId: {
                        sessionManager.setKeySetLastMccSorting(ReportHintConstant.ROUTE);

                        if (isAscending) {
                            return o1.centerRoute.compareTo(o2.centerRoute);
                        } else {
                            return o2.centerRoute.compareTo(o1.centerRoute);
                        }
                    }
                    case R.id.tvHDate: {
                        sessionManager.setKeySetLastMccSorting(ReportHintConstant.DATE);

                        if (isAscending) {
                            return o1.postDate.compareTo(o2.postDate);
                        } else {
                            return o2.postDate.compareTo(o1.postDate);
                        }
                    }
                    case R.id.tvHFat: {
                        sessionManager.setKeySetLastMccSorting(ReportHintConstant.FAT);

                        if (isAscending) {
                            return Double.valueOf(o1.fat).compareTo(Double.valueOf(o2.fat));
                        } else {
                            return Double.valueOf(o2.fat).compareTo(Double.valueOf(o1.fat));
                        }
                    }
                    case R.id.tvHSnf: {
                        sessionManager.setKeySetLastMccSorting(ReportHintConstant.SNF);

                        if (isAscending) {
                            return Double.valueOf(o1.snf).compareTo(Double.valueOf(o2.snf));
                        } else {
                            return Double.valueOf(o2.snf).compareTo(Double.valueOf(o1.snf));
                        }
                    }
                    case R.id.tvHFatkg: {
                        if (isAscending) {
                            return Double.valueOf(o1.fatKg).compareTo(Double.valueOf(o2.fatKg));
                        } else {
                            return Double.valueOf(o2.fatKg).compareTo(Double.valueOf(o1.fatKg));
                        }
                    }
                    case R.id.tvHSnfkg: {
                        if (isAscending) {
                            return Double.valueOf(o1.snfKg).compareTo(Double.valueOf(o2.snfKg));
                        } else {
                            return Double.valueOf(o2.snfKg).compareTo(Double.valueOf(o1.snfKg));
                        }
                    }
                    case R.id.tvHQty: {
                        sessionManager.setKeySetLastMccSorting(ReportHintConstant.QTY);

                        if (isAscending) {
                            return Double.valueOf(o1.quantity).compareTo(Double.valueOf(o2.quantity));
                        } else {
                            return Double.valueOf(o2.quantity).compareTo(Double.valueOf(o1.quantity));
                        }
                    }
                    case R.id.tvHRate: {
                        sessionManager.setKeySetLastMccSorting(ReportHintConstant.RATE_H);

                        if (isAscending) {
                            return Double.valueOf(o1.rate).compareTo(Double.valueOf(o2.rate));
                        } else {
                            return Double.valueOf(o2.rate).compareTo(Double.valueOf(o1.rate));
                        }
                    }
                    case R.id.tvHAmount: {
                        sessionManager.setKeySetLastMccSorting(ReportHintConstant.AMOUNT_H);

                        if (isAscending) {
                            return Double.valueOf(o1.amount).compareTo(Double.valueOf(o2.amount));
                        } else {
                            return Double.valueOf(o2.amount).compareTo(Double.valueOf(o1.amount));
                        }
                    }
                }
                return 0;
            }
        });
        reportAdapter.updateList(tempReportList);
        reportAdapter.notifyDataSetChanged();

        if (isAscending) {
            sessionManager.setKeySetLastMccSortingOrder("ASC");
        } else {
            sessionManager.setKeySetLastMccSortingOrder("DESC");

        }

    }

    private void resetRightDrawable(View view) {
        tvHMccId.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHRouteId.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHFat.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHSnf.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHFatKg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHSnfKg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHQty.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHRate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        isAscending = (sortId == view.getId()) ? !isAscending : true;

        if (isAscending) {
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, UP_ARROW, 0);
        } else {
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, DOWN_ARROW, 0);
        }
        sortId = view.getId();
    }


    public void getReport() {
        route = getIntent().getStringExtra(ReportHintConstant.ROUTE);
        mcc = getIntent().getStringExtra(ReportHintConstant.MCC);
        fromDate = getIntent().getStringExtra(ReportHintConstant.DATE_FROM);
        toDate = getIntent().getStringExtra(ReportHintConstant.DATE_TO);
        cattleType = getIntent().getStringExtra(ReportHintConstant.CATTLE_TYPE);
        shift = getIntent().getStringExtra(ReportHintConstant.SHIFT);
        recordCompleteStatus = getIntent().getStringExtra(ReportHintConstant.IS_COMPLETE);
    }

    @Override
    public void onClick(View view) {

        if (view == btnCancel) {
            startActivity(setIntent());
            finish();
        } else if (view == btnSort) {
            sortAlert();
        } else if (view == btnNext) {

        } else if (view == btnFilter) {
            //For text

//            AverageReportDetail averageReportDetail = dbh.getAverageReportDataFromQuery(query);
            AverageReportDetail averageReportDetail = smartCCUtil.getAverageOfReport(reportList, false);
            smartCCUtil.alertForAverageReport(averageReportDetail);

        } else if (view == btnToggle) {
            if (btnToggle.getText().toString().trim().equalsIgnoreCase(SUMMARY)) {
                btnToggle.setText(DETAILS);
                CURRENT_VIEW = ReportAdapter.SHORT_VIEW;
                rlListHeader.setVisibility(View.VISIBLE);
//                reportAdapter.setViewType(ReportAdapter.SHORT_VIEW);
//                reportAdapter.notifyDataSetChanged();
                setAdapter();
            } else {
                btnToggle.setText(SUMMARY);
                CURRENT_VIEW = ReportAdapter.DETAIL_VIEW;
                rlListHeader.setVisibility(View.INVISIBLE);
//                reportAdapter.setViewType(ReportAdapter.DETAIL_VIEW);
//                reportAdapter.notifyDataSetChanged();
                setAdapter();
            }
        } else if (view == btnPrint) {
            printAlert();
        }
    }

    private String printData;
    private String threadNamePrint = "Print";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_report);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvHMccId = (TextView) findViewById(R.id.tvHMccId);
        tvHRouteId = (TextView) findViewById(R.id.tvHRouteId);
        tvHDate = (TextView) findViewById(R.id.tvHDate);
        tvHFat = (TextView) findViewById(R.id.tvHFat);
        tvHSnf = (TextView) findViewById(R.id.tvHSnf);
        tvHFatKg = (TextView) findViewById(R.id.tvHFatkg);
        tvHSnfKg = (TextView) findViewById(R.id.tvHSnfkg);
        tvHQty = (TextView) findViewById(R.id.tvHQty);
        tvHRate = (TextView) findViewById(R.id.tvHRate);
        tvHAmount = (TextView) findViewById(R.id.tvHAmount);
        tvHClr = (TextView) findViewById(R.id.tvHClr);

        recycleView = (RecyclerView) findViewById(R.id.rpRecycleView);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnFilter = (Button) findViewById(R.id.btnFilter);
        btnToggle = (Button) findViewById(R.id.btnToggle);
        btnSort = (Button) findViewById(R.id.btnSort);
        btnPrint = (Button) findViewById(R.id.btnPrintReport);
        rlListHeader = (RelativeLayout) findViewById(R.id.listHeader);

        btnSort.setVisibility(View.GONE);

        View.OnClickListener sortClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort(v);
            }
        };

        tvHMccId.setOnClickListener(sortClickListener);
        tvHRouteId.setOnClickListener(sortClickListener);
        tvHDate.setOnClickListener(sortClickListener);
        tvHFat.setOnClickListener(sortClickListener);
        tvHSnf.setOnClickListener(sortClickListener);
        tvHFatKg.setOnClickListener(sortClickListener);
        tvHSnfKg.setOnClickListener(sortClickListener);
        tvHQty.setOnClickListener(sortClickListener);
        tvHRate.setOnClickListener(sortClickListener);
        tvHAmount.setOnClickListener(sortClickListener);

        btnCancel.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnToggle.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        btnPrint.setOnClickListener(this);

        btnToggle.setText("DETAILS");


        recycleView.setLayoutManager(new LinearLayoutManager(MccReportActivity.this,
                OrientationHelper.VERTICAL, false));
        tvNoDataFound = (TextView) findViewById(R.id.tvNoRecords);

        mDatabaseHandler = DatabaseHandler.getDatabaseInstance();
        smartCCUtil = new SmartCCUtil(this);
        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        sessionManager = new SessionManager(MccReportActivity.this);
        printerManager = new PrinterManager(MccReportActivity.this);
        amcuConfig = AmcuConfig.getInstance();

        if (AmcuConfig.getInstance().getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
            tvHSnf.setVisibility(View.GONE);
            tvHClr.setVisibility(View.VISIBLE);
        }

        recycleView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();
                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        rv.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }


            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
        getReport();
    }

    //To display the Huge records of data this needs to be do in different Thread
    private void setAdapter() {
        dbe = new DatabaseEntity(MccReportActivity.this);
        mDatabaseHandler = DatabaseHandler.getDatabaseInstance();
        progressBar.setVisibility(View.VISIBLE);

        new Thread(new Runnable() {
            @Override
            public void run() {

                queryForCollectionRecords();

                adapterHandler.post(adapterRunnable);
            }
        }, threadNameReport).start();

        adapterRunnable = new Runnable() {
            @Override
            public void run() {


                if (reportList.size() > 0) {
                    tempReportList = new ArrayList<>();
                    for (ReportEntity reportEntity : reportList) {
                        reportEntity.quantity = reportEntity.getPrintAndReportQuantity();
                        reportEntity.kgWeight = reportEntity.getPrintAndReportKgQuantity();
                        reportEntity.ltrsWeight = reportEntity.getPrintAndReportLtQuantity();
                        tempReportList.add(reportEntity);
                    }

                    tvNoDataFound.setVisibility(View.GONE);
                } else {
                    tvNoDataFound.setVisibility(View.VISIBLE);
                }


                progressBar.setVisibility(View.GONE);

                reportAdapter = new ReportAdapter(
                        tempReportList, CURRENT_VIEW, MccReportActivity.this);
                recycleView.setAdapter(reportAdapter);

                //   reportAdapter.updateList(tempReportList);
                reportAdapter.notifyDataSetChanged();
                adapterHandler.removeCallbacks(adapterRunnable);
            }
        };


    }

    private void queryForCollectionRecords() {


        dbe = new DatabaseEntity(MccReportActivity.this);
        query = dbe.getQueryForReport(
                route, mcc, fromDate,
                toDate, cattleType, shift, Util.REPORT_TYPE_MCC, 1);

        String commitStatus = null;

        if (recordCompleteStatus.equalsIgnoreCase(Util.RECORD_STATUS_INCOMPLETE)) {
            commitStatus = String.valueOf(Util.REPORT_NOT_COMMITED);
        } else {
            commitStatus = String.valueOf(Util.REPORT_COMMITED);
        }
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();

        if (reportList == null || reportList.size() < 1) {
            reportList = collectionRecordDao.findAllForReports(
                    route.equalsIgnoreCase("all") || route.equalsIgnoreCase("") ? null : route.split(","),
                    mcc.equalsIgnoreCase("all") || mcc.equalsIgnoreCase("") ? null : mcc.split(","),
                    Util.getDateInLongFormat(fromDate), Util.getDateInLongFormat(toDate),
                    (cattleType.equalsIgnoreCase(SmartCCConstants.SELECT_ALL) || cattleType.equals("")) ? null : cattleType,
                    (shift.equalsIgnoreCase(SmartCCConstants.SELECT_ALL) || shift.equalsIgnoreCase("")) ? null : SmartCCUtil.getFullShift(shift),
                    Util.REPORT_TYPE_MCC,
                    commitStatus, recordCompleteStatus);
            tempReportList = new ArrayList<>(reportList);
        }


    }

    private void printAlert() {
        disablePrintButton();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = LayoutInflater.from(MccReportActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_print, null);
        builder.setView(view);

        final Button btnPrint, btnMBR, btnMBS, btnPrintShift, btnPrintPeriodic;

        btnPrint = (Button) view.findViewById(R.id.printExisting);
        btnMBR = (Button) view.findViewById(R.id.printMBR);
        btnMBS = (Button) view.findViewById(R.id.printMBS);
        btnMBS.setText("AGENT(S) BILL SUMMARY");
        btnPrintPeriodic = (Button) view.findViewById(R.id.printPeriodic);
        btnPrintShift = (Button) view.findViewById(R.id.printShift);

        btnPrint.setText("Custom print");

        btnMBR.setVisibility(View.GONE);
        btnPrintPeriodic.setVisibility(View.GONE);

        if (!showShiftOption()) {
            btnPrintShift.setVisibility(View.GONE);
            btnPrintPeriodic.setVisibility(View.VISIBLE);
        }
        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                printExistingFormat();

                printDialog.dismiss();
            }
        });

        btnMBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                printMBRFormat();
                printDialog.dismiss();

            }
        });

        btnPrintShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String printShift = getShiftFormat();

                printFormatData(printShift);
                printDialog.dismiss();
            }
        });

        btnPrintPeriodic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String periodic = getPeriodicFormat();
                printDialog.dismiss();
                getPeriodicFormat();

//                printFormatData(periodic);
//                printDialog.dismiss();

            }
        });
        btnMBS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printMBSFormat();
                printDialog.dismiss();
            }
        });

        printDialog = builder.create();
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams WMLP = printDialog.getWindow().getAttributes();
        WMLP.gravity = Gravity.CENTER;

//        WMLP.x = 0;   //x position
//        WMLP.y = 50;   //y position
        printDialog.getWindow().setAttributes(WMLP);
        printDialog.show();

    }

    public String printExistingFormat() {
        printData = null;
        if (tempReportList == null || tempReportList.size() < 1) {
            Util.displayErrorToast("No data available!", MccReportActivity.this);
            return null;
        }
        progressBar.setVisibility(View.VISIBLE);
        final FormatPrintHelper fph = new FormatPrintHelper(MccReportActivity.this, queue);
        fph.setOnFormatCompleteListener(new FormatPrintHelper.OnFormatCompleteListener() {
            @Override
            public void onFormatComplete() {
                shouldPrint = false;
            }
        });
        printerManager.openConnection();
        shouldPrint = true;
        final PrinterManager printerManager = new PrinterManager(MccReportActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPause && (shouldPrint || !queue.isEmpty())) {
                    if (!isEnter) {
                        isEnter = true;
                        Util.displayErrorToast("Please wait", MccReportActivity.this);
                    }
                    if (!queue.isEmpty()) {
                        String data = queue.poll();
                        System.out.println(data);
                        printerManager.write(data);
                        count++;
                        try {
                            Thread.sleep(data.getBytes().length * 5 * getDelayConstant(count));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                printerManager.closePrinter();
                count = 0;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });

            }
        }, threadNamePrint).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                PrinterManager.title = printerManager.getPeriodicTitle(fromDate, toDate, allowHeader, Util.REPORT_TYPE_MCC);
                if (AmcuConfig.getInstance().getKeyAllowProteinValue()) {
                    printData = fph.setIncentivePeriodicRecords(tempReportList);

                } else {

                    printData = fph.setCustomPeriodicReport(tempReportList, false);
                }

            }
        }).start();


        return printData;

    }

    public void printMBRFormat() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                FormatPrintRecords formatPrintRecords = new FormatPrintRecords(MccReportActivity.this);
                printData = formatPrintRecords.getReportFormat(
                        ReportHintConstant.MEMBER_BILL_REG, ReportHintConstant.MEMBER_BILL_REG_HEADING,
                        new SessionManager(MccReportActivity.this).getCollectionID(),
                        getDateHeader(), tempReportList);
                printHandler.post(printRunnable);
            }
        }, threadNamePrint).start();
        printRunnable = new Runnable() {
            @Override
            public void run() {
                printFormatData(printData);
            }
        };


    }

    public void printFormatData(String printData) {

        if (printData == null) {
            Util.displayErrorToast("No data available!", MccReportActivity.this);
            return;
        }
        PrinterManager parsingPrinterData = new PrinterManager(MccReportActivity.this);
        PrinterManager.title = parsingPrinterData.getPeriodicTitle(fromDate,
                toDate, allowHeader, Util.REPORT_TYPE_MCC);

        printerManager.print(printData, PrinterManager.printCustomReport,
                fromDate, toDate, null);


    }

    public void printMBSFormat() {
        queue = new LinkedBlockingQueue<>();
        final FormatPrintHelper fph = new FormatPrintHelper(MccReportActivity.this, queue);
        fph.setOnFormatCompleteListener(new FormatPrintHelper.OnFormatCompleteListener() {
            @Override
            public void onFormatComplete() {
                shouldPrint = false;
            }
        });
        progressBar.setVisibility(View.VISIBLE);
        printerManager.openConnection();
        shouldPrint = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPause && (shouldPrint || !queue.isEmpty())) {

                    if (!isEnter) {
                        isEnter = true;
                        Util.displayErrorToast("Please wait", MccReportActivity.this);
                    }
                    if (!queue.isEmpty() && isEnter) {
                        String data = queue.poll();
                        System.out.println(data);
                        printerManager.write(data);
                        count++;

                        try {
                            Thread.sleep(data.getBytes().length * 5 * getDelayConstant(count));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                printerManager.closePrinter();
                count = 0;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }, threadNamePrint).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                String dairyName = "";
                printData = new PrinterManager(MccReportActivity.this).getMemberBillSummaryTitle(
                        fromDate, toDate);
                if (dairyName.length() > 0) {
                    printData = new PrinterManager(MccReportActivity.this).centerAlignWithPaddingThermal(dairyName) + "\n" +
                            printData + "\n";
                }
                queue.add(printData);
                fph.getMemberBillSummaryReport(tempReportList);
            }
        }).start();


    }

    private String getDateHeader() {
        String returnString = "";
        if (!fromDate.equalsIgnoreCase(SmartCCConstants.SELECT_ALL) &&
                !toDate.equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            returnString = "From " + fromDate + " To " + toDate;
        }

        return returnString;

    }

    private String getShiftFormat() {
        FormatPrintHelper fph = new FormatPrintHelper(MccReportActivity.this);
        String strBuild = printerManager.getShiftSubTitle(fromDate, shift, Util.REPORT_TYPE_MCC);
        strBuild = strBuild + fph.getShiftReport(tempReportList);
        return strBuild;
    }

    private String getPeriodicFormat() {
        final FormatPrintHelper fph = new FormatPrintHelper(MccReportActivity.this,
                queue);
        fph.setOnFormatCompleteListener(new FormatPrintHelper.OnFormatCompleteListener() {
            @Override
            public void onFormatComplete() {
                shouldPrint = false;
            }
        });
        printerManager.openConnection();
        shouldPrint = true;
        progressBar.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPause && (shouldPrint || !queue.isEmpty())) {
                    if (!isEnter) {
                        isEnter = true;
                        Util.displayErrorToast("Please wait", MccReportActivity.this);
                    }
                    if (!queue.isEmpty() && isEnter) {
                        String data = queue.poll();
                        // System.out.println(data);
                        //  Log.v("PrintData: ",data);
                        printerManager.write(data);
                        count++;
                        try {
                            Thread.sleep(data.getBytes().length * (5 * getDelayConstant(count)));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                printerManager.closePrinter();
                count = 0;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                queue.add(printerManager.getPeriodicSubTitle(fromDate, toDate, Util.REPORT_TYPE_MCC));
                fph.getPeriodicReport(tempReportList);
            }
        }).start();
        return printData;
    }

    public boolean showShiftOption() {
        boolean returnValue = false;
        if (shift != null && !shift.trim().isEmpty()
                && !shift.equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            if (!fromDate.equalsIgnoreCase(SmartCCConstants.SELECT_ALL) &&
                    (fromDate.equalsIgnoreCase(toDate))) {
                returnValue = true;
            }
        }
        return returnValue;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_D: {
                btnToggle.setText(SUMMARY);
                CURRENT_VIEW = ReportAdapter.DETAIL_VIEW;
                setAdapter();
                break;
            }
            case KeyEvent.KEYCODE_S: {
                btnToggle.setText(DETAILS);
                CURRENT_VIEW = ReportAdapter.SHORT_VIEW;
                setAdapter();
                break;
            }
            case KeyEvent.KEYCODE_A: {
               /* DatabaseEntity dbe = new DatabaseEntity(MccReportActivity.this);
                String query = dbe.getQueryForReport(
                        route, mcc, fromDate,
                        toDate, cattleType, shift, Util.REPORT_TYPE_MCC, 1);
                DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();*/
                AverageReportDetail averageReportDetail = smartCCUtil.getAverageOfReport(reportList, false);
                smartCCUtil.alertForAverageReport(averageReportDetail);

                break;
            }


            default: {
                break;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    public Intent setIntent() {
        Intent intent = new Intent(MccReportActivity.this, FilterMccReportActivity.class);
        intent.putExtra(ReportHintConstant.CATTLE_TYPE, cattleType);
        intent.putExtra(ReportHintConstant.DATE_FROM, fromDate);
        intent.putExtra(ReportHintConstant.DATE_TO, toDate);
        intent.putExtra(ReportHintConstant.MCC, mcc);
        intent.putExtra(ReportHintConstant.SHIFT, shift);
        intent.putExtra(ReportHintConstant.ROUTE, route);
        intent.putExtra(ReportHintConstant.IS_COMPLETE, recordCompleteStatus);

        return intent;
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        startActivity(setIntent());
        finish();

    }

    private void sortAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = LayoutInflater.from(MccReportActivity.this);
        View view = layoutInflater.inflate(R.layout.menu_sort, null);
        builder.setView(view);

        final TextView tvSortId, tvSortQty, tvSortAmt, tvSortRate, tvSortTime;


        tvSortAmt = (TextView) view.findViewById(R.id.tvSortAmount);
        tvSortId = (TextView) view.findViewById(R.id.tvSortId);
        tvSortQty = (TextView) view.findViewById(R.id.tvSortQuantity);
        tvSortRate = (TextView) view.findViewById(R.id.tvSortRate);
        tvSortTime = (TextView) view.findViewById(R.id.tvSortTime);

        setDrawable(tvSortAmt, AMOUNT_STATE);
        setDrawable(tvSortRate, RATE_STATE);
        setDrawable(tvSortId, ID_STATE);
        setDrawable(tvSortQty, QUANTITY_STATE);
        setDrawable(tvSortTime, TIME_STATE);

        tvSortAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AMOUNT_STATE == 0) {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.AMOUNT, ASC, AMOUNT_STATE);
                    AMOUNT_STATE = 1;
                } else {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.AMOUNT, DESC, AMOUNT_STATE);
                    AMOUNT_STATE = 0;
                }


            }
        });

        tvSortRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RATE_STATE == 0) {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.RATE, ASC, RATE_STATE);
                    RATE_STATE = 1;
                } else {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.RATE, DESC, RATE_STATE);
                    RATE_STATE = 0;
                }


            }
        });

        tvSortId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ID_STATE == 0) {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.ID, ASC, ID_STATE);
                    ID_STATE = 1;
                } else {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.ID, DESC, ID_STATE);
                    ID_STATE = 0;
                }

            }
        });
        tvSortQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QUANTITY_STATE == 0) {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.QUANTITY, ASC, QUANTITY_STATE);
                    QUANTITY_STATE = 1;
                } else {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.QUANTITY, DESC, QUANTITY_STATE);
                    QUANTITY_STATE = 0;
                }
            }
        });
        tvSortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TIME_STATE == 0) {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.TIME, ASC, TIME_STATE);
                    TIME_STATE = 1;
                } else {
                    getASCOrDESCQuery(tvSortAmt, MemberReportActivity.TIME, DESC, TIME_STATE);
                    TIME_STATE = 0;
                }

            }
        });


        dialog = builder.create();
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
        WMLP.gravity = Gravity.TOP | Gravity.LEFT;

        WMLP.x = 0;   //x position
        WMLP.y = 50;   //y position

        dialog.getWindow().setAttributes(WMLP);

        dialog.show();

    }

    private void setDrawable(TextView textView, int state) {
        if (state == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.arrow_up_float, 0, 0, 0);

        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.arrow_down_float, 0, 0, 0);


        }
    }


    private void getASCOrDESCQuery(TextView textView, int type, String order, int state) {


        String updatedQuery = query;
        if (order.equalsIgnoreCase(ASC)) {
            updatedQuery = DatabaseEntity.getQueryForSort(query, type, DESC);
        } else {
            updatedQuery = DatabaseEntity.getQueryForSort(query, type, ASC);
        }

        dialog.dismiss();
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        ArrayList<ReportEntity> allReportEnt = dbh.getReportFromQuery(updatedQuery);
        if (allReportEnt.size() > 0) {
            tvNoDataFound.setVisibility(View.GONE);
        } else {
            tvNoDataFound.setVisibility(View.VISIBLE);
        }
        reportAdapter = new ReportAdapter(
                allReportEnt, CURRENT_VIEW, MccReportActivity.this);
        recycleView.setAdapter(reportAdapter);
    }

    public long getDelayConstant(long count) {
        count = (count * 2) / 100;
        if (count <= 0) {
            return 1;
        }
        return count;
    }

    private void disablePrintButton() {
        btnPrint.setEnabled(false);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }


    @Override
    protected void onStop() {
        super.onStop();
        printHandler.removeCallbacks(printRunnable);
        Thread[] threads = new Thread[Thread.activeCount()];
        Thread.enumerate(threads);
        for (Thread t : threads) {
            if (t.getName() != null && (
                    t.getName().equalsIgnoreCase(threadNameReport)
                            || t.getName().equalsIgnoreCase(threadNamePrint))) {
                t.interrupt();
            }
        }
    }
}
