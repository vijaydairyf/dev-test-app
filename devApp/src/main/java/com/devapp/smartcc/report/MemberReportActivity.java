package com.devapp.smartcc.report;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Environment;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AgentSplitEntity;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.AgentSplitDao;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.DispatchRecordDao;
import com.devapp.devmain.dao.EditRecordDao;
import com.devapp.devmain.dao.SalesRecordDao;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.AverageReportDetail;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.helper.ReportHelper;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.SendEmail;
import com.devapp.devmain.services.WriteRecordReceiver;
import com.devapp.devmain.usb.WriteExcel;
import com.devapp.devmain.user.FormatPrintHelper;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.adapters.ReportAdapter;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * To show the member report based on incoming Intent from Member filter activity
 * Here user can view,sort and print the report
 * Created by u_pendra on 8/5/17.
 */

public class MemberReportActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int TIME = 0;
    public static final int QUANTITY = 1;
    public static final int AMOUNT = 2;
    public static final int RATE = 3;
    public static final int ID = 4;
    static final int UP_ARROW = android.R.drawable.arrow_up_float;
    static final int DOWN_ARROW = android.R.drawable.arrow_down_float;
    private static final String ASC = "ASC";
    private static final String DESC = "DESC";
    public static int TIME_STATE = 0;
    public static int QUANTITY_STATE = 0;
    public static int AMOUNT_STATE = 0;
    public static int RATE_STATE = 0;
    public static int ID_STATE = 0;
    private final long THRESHOLD_SIZE = 1000;
    public boolean isWrittenToFile, isWrittentoEncFile;
    public WriteRecordReceiver receiverForWrite;
    public boolean isEnter = false;
    RecyclerView recyclerView;
    ReportAdapter reportAdapter;
    boolean allowHeader;
    Button btnBack, btnPrint, btnSort, btnAverage;
    DatabaseEntity dbe;
    PrinterManager printerManager;
    String query;
    AlertDialog dialog;
    AlertDialog printDialog;
    ArrayList<ReportEntity> allDisplayList = null;
    ArrayList<ReportEntity> tempList = new ArrayList<>();
    boolean returnValue = false;
    int detailsCheck;
    File excelFileName;
    Runnable updateRunnable, NewUpdateRunnable;
    Handler myHandler = new Handler();
    String reportType;
    AmcuConfig amcuConfig;
    SalesRecordDao salesRecordDao;
    DispatchRecordDao dispatchRecordDao;
    Queue<String> queue = new LinkedBlockingQueue<>();
    String dairyName = "";
    long count = 0;
    boolean shouldPrint = false;
    android.app.AlertDialog alertDialogPrint;
    private EditRecordDao editRecordDao;
    private String memberId, mcc, fromDate, toDate, shift, cattleType;
    private UsbManager mUsbManager;
    private String reportQuery;
    private DatabaseHandler dbh;
    private int ORDER_TYPE = ID;
    private String LAST_ORDER = ASC;
    private boolean isAggerareFarmer;
    private TextView tvHMemberId;
    private TextView tvHDate;
    private TextView tvHTime;
    private TextView tvHShift;
    private TextView tvHType;
    private TextView tvHFat;
    private TextView tvHSnf;
    private TextView tvHQty;
    private TextView tvRate;
    private TextView tvProtein;
    private TextView tvIncentive;
    private TextView tvFatKg;
    private TextView tvSNFKg;
    private TextView tvHClr;
    private TextView tvAmount;
    private SessionManager sessionManager;
    private Button btnExport;
    private ReportHelper reportHelper;
    private Button btnSummery;
    private String mCollectionType;
    private CollectionRecordDao collectionRecordDao;
    private AgentSplitDao agentSplitDao;
    private boolean isAscending = true;
    private int sortId;
    private SmartCCUtil smartCCUtil;
    private long listSize = 0;
    private String threadNamePrint = "Print";
    private ProgressBar progressBar;
    private Handler adapterHandler = new Handler();
    private Runnable adapterRunnable;
    private String threadNameReport = "Report";
    private boolean isPause = false;
    private Handler printHandler = new Handler();
    private Runnable printRunnable;
    private String printData;
    private AverageReportDetail averageReportDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_member_layout);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) findViewById(R.id.recylerList);
        btnPrint = (Button) findViewById(R.id.btnPrintReport);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnSort = (Button) findViewById(R.id.btnSort);
        btnAverage = (Button) findViewById(R.id.btnAverage);


        tvHMemberId = (TextView) findViewById(R.id.tvHMemberId);
        tvHDate = (TextView) findViewById(R.id.tvHDate);
        tvHTime = (TextView) findViewById(R.id.tvHTime);
        tvHShift = (TextView) findViewById(R.id.tvHShift);
        tvHType = (TextView) findViewById(R.id.tvHType);
        tvHFat = (TextView) findViewById(R.id.tvHFat);
        tvHSnf = (TextView) findViewById(R.id.tvHSnf);
        tvHClr = (TextView) findViewById(R.id.tvHClr);
        tvHQty = (TextView) findViewById(R.id.tvHQty);

        tvRate = (TextView) findViewById(R.id.tvHRate);
        tvProtein = (TextView) findViewById(R.id.tvProtein);
        tvIncentive = (TextView) findViewById(R.id.tvIncentive);
        tvAmount = (TextView) findViewById(R.id.tvHAmount);
        tvFatKg = (TextView) findViewById(R.id.tvHFatkg);
        tvSNFKg = (TextView) findViewById(R.id.tvHSnfkg);
        btnExport = (Button) findViewById(R.id.btnExport);
        btnSummery = (Button) findViewById(R.id.btnToggle);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sort(view);
            }
        };

        tvHMemberId.setOnClickListener(onClickListener);
        tvHDate.setOnClickListener(onClickListener);
        tvHTime.setOnClickListener(onClickListener);
        tvHShift.setOnClickListener(onClickListener);
        tvHType.setOnClickListener(onClickListener);
        tvHFat.setOnClickListener(onClickListener);
        tvHSnf.setOnClickListener(onClickListener);
        tvHClr.setOnClickListener(onClickListener);
        tvHQty.setOnClickListener(onClickListener);

        tvRate.setOnClickListener(onClickListener);
        tvProtein.setOnClickListener(onClickListener);
        tvIncentive.setOnClickListener(onClickListener);
        tvAmount.setOnClickListener(onClickListener);
        tvFatKg.setOnClickListener(onClickListener);
        tvSNFKg.setOnClickListener(onClickListener);

        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        agentSplitDao = (AgentSplitDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_AGENT_SPLIT);
        salesRecordDao = (SalesRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_SALES);
        dispatchRecordDao = (DispatchRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_DISPATCH);
        editRecordDao = (EditRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_EDITED);

        sessionManager = new SessionManager(MemberReportActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(MemberReportActivity.this,
                OrientationHelper.VERTICAL, false));

        recyclerView.requestFocus();

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

        amcuConfig = AmcuConfig.getInstance();
        if (AmcuConfig.getInstance().getKeyAllowProteinValue()) {
            tvIncentive.setVisibility(View.VISIBLE);
            tvProtein.setVisibility(View.VISIBLE);
        } else {
            tvIncentive.setVisibility(View.GONE);
            tvProtein.setVisibility(View.GONE);
        }
        if (AmcuConfig.getInstance().getKeyAllowDisplayRate()) {
            tvFatKg.setVisibility(View.GONE);
            tvSNFKg.setVisibility(View.GONE);
            tvRate.setVisibility(View.VISIBLE);
            tvAmount.setVisibility(View.VISIBLE);

        } else {
            tvFatKg.setVisibility(View.VISIBLE);
            tvSNFKg.setVisibility(View.VISIBLE);
            tvRate.setVisibility(View.GONE);
            tvAmount.setVisibility(View.GONE);
        }
        if (AmcuConfig.getInstance().getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
            tvHSnf.setVisibility(View.GONE);
            tvHClr.setVisibility(View.VISIBLE);
        }
        getReport();
        setAdapter();

        btnPrint.requestFocus();
        btnAverage.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        btnAverage.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnPrint.setOnClickListener(this);

        if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)) {
            tvRate.setText("CLR");
        }


        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // isMailSend = false;
                checkForPenDrive(false);

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        dbe = new DatabaseEntity(MemberReportActivity.this);
        printerManager = new PrinterManager(MemberReportActivity.this);
        reportHelper = new ReportHelper(MemberReportActivity.this);
        smartCCUtil = new SmartCCUtil(MemberReportActivity.this);

        setupServiceReceiver();
        updateVisibileExportOption();


        if (sessionManager.getKeySetLastMemberSortingOrder().equalsIgnoreCase("ASC")) {
            isAscending = true;
        } else {
            isAscending = false;
        }


    }

    private void sort(final View view) {
        resetRightDrawable(view);
        tempList = new ArrayList<>(allDisplayList);
        Collections.sort(tempList, new Comparator<ReportEntity>() {
            @Override
            public int compare(ReportEntity o1, ReportEntity o2) {
                switch (view.getId()) {
                    case R.id.tvHMemberId: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.MEMBER_ID);
                        if (isAscending) {
                            return o1.farmerId.compareTo(o2.farmerId);
                        } else {
                            return o2.farmerId.compareTo(o1.farmerId);
                        }
                    }
                    case R.id.tvHDate: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.DATE);

                        if (isAscending) {
                            return o1.postDate.compareTo(o2.postDate);
                        } else {
                            return o2.postDate.compareTo(o1.postDate);
                        }
                    }
                    case R.id.tvHTime: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.COLL_TIME);

                        if (isAscending) {
                            return Long.valueOf(o1.miliTime).compareTo(Long.valueOf(o2.miliTime));
                        } else {
                            return Long.valueOf(o2.miliTime).compareTo(Long.valueOf(o1.miliTime));
                        }
                    }
                    case R.id.tvHShift: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.SHIFT);

                        if (isAscending) {
                            return o1.getPostShift().compareTo(o2.getPostShift());

                        } else {
                            return o2.getPostShift().compareTo(o1.getPostShift());
                        }

                    }
                    case R.id.tvHType: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.CATTLE_TYPE);

                        if (isAscending) {
                            return o1.getMilkType().compareTo(o2.getMilkType());
                        } else {
                            return o2.getMilkType().compareTo(o1.getMilkType());
                        }

                    }
                    case R.id.tvHFat: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.FAT);

                        if (isAscending) {
                            return Double.valueOf(o1.fat).compareTo(Double.valueOf(o2.fat));
                        } else {
                            return Double.valueOf(o2.fat).compareTo(Double.valueOf(o1.fat));
                        }
                    }
                    case R.id.tvHSnf: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.SNF);

                        if (isAscending) {
                            return Double.valueOf(o1.snf).compareTo(Double.valueOf(o2.snf));
                        } else {
                            return Double.valueOf(o2.snf).compareTo(Double.valueOf(o1.snf));
                        }
                    }
                    case R.id.tvHClr: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.CLR);

                        if (isAscending) {
                            return Double.valueOf(o1.clr).compareTo(Double.valueOf(o2.clr));
                        } else {
                            return Double.valueOf(o2.clr).compareTo(Double.valueOf(o1.clr));
                        }
                    }
                    case R.id.tvHQty: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.QTY);

                        if (isAscending) {
                            return Double.valueOf(o1.quantity).compareTo(Double.valueOf(o2.quantity));
                        } else {
                            return Double.valueOf(o2.quantity).compareTo(Double.valueOf(o1.quantity));
                        }
                    }
                    case R.id.tvHRate: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.RATE_H);

                        if (isAscending) {
                            return Double.valueOf(o1.rate).compareTo(Double.valueOf(o2.rate));
                        } else {
                            return Double.valueOf(o2.rate).compareTo(Double.valueOf(o1.rate));
                        }
                    }
                    case R.id.tvProtein: {
                        if (isAscending) {
                            return Double.valueOf(o1.protein).compareTo(Double.valueOf(o2.protein));
                        } else {
                            return Double.valueOf(o2.protein).compareTo(Double.valueOf(o1.protein));
                        }
                    }
                    case R.id.tvIncentive: {
                        if (isAscending) {
                            return Double.valueOf(o1.incentiveAmount).compareTo(Double.valueOf(o2.incentiveAmount));
                        } else {
                            return Double.valueOf(o2.incentiveAmount).compareTo(Double.valueOf(o1.incentiveAmount));
                        }
                    }
                    case R.id.tvHAmount: {
                        sessionManager.setKeySetLastMemberSorting(ReportHintConstant.AMOUNT_H);

                        if (isAscending) {
                            return Double.valueOf(o1.amount).compareTo(Double.valueOf(o2.amount));
                        } else {
                            return Double.valueOf(o2.amount).compareTo(Double.valueOf(o1.amount));
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
                }
                return 0;
            }
        });

        if (isAscending) {
            sessionManager.setKeySetLastMemberSortingOrder("ASC");

        } else {
            sessionManager.setKeySetLastMemberSortingOrder("DESC");

        }

        reportAdapter.updateList(tempList);
        reportAdapter.notifyDataSetChanged();
    }

    private void resetRightDrawable(View view) {
        tvHMemberId.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHDate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHShift.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHType.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHFat.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHSnf.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHClr.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvHQty.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        tvRate.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvProtein.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvIncentive.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvAmount.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvFatKg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        tvSNFKg.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

        isAscending = (sortId == view.getId()) ? !isAscending : true;

        if (isAscending) {
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, UP_ARROW, 0);
        } else {
            ((TextView) view).setCompoundDrawablesWithIntrinsicBounds(0, 0, DOWN_ARROW, 0);
        }
        sortId = view.getId();
    }

    private void queryForCollectionRecords() {
        int recordCommitedStatus = 1;

        if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)) {
            AppConstants.IS_SELECTED_AGGERATE_FARMER = true;
            long startTime = 0, endTime = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
            try {
                startTime = sdf.parse(fromDate + " 00:00:00.000").getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                endTime = sdf.parse(toDate + " 23:59:59.999").getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            allDisplayList = new ArrayList<>();
            ArrayList<AgentSplitEntity> list = agentSplitDao.findAllForReports((memberId.equalsIgnoreCase("ALL") || memberId.equalsIgnoreCase("")) ? null : memberId.split(","), (startTime == 0) ? null : String.valueOf(startTime),
                    (endTime == 0) ? null : String.valueOf(endTime), (cattleType.equalsIgnoreCase("BOTH") || cattleType.equalsIgnoreCase("")) ? null : cattleType,
                    (shift.equalsIgnoreCase("ALL") || shift.equals("")) ? null : SmartCCUtil.getFullShift(shift),
                    String.valueOf(Util.REPORT_COMMITED));
            for (AgentSplitEntity ase : list) {
                allDisplayList.add(Util.getReportEntityFromSplitRecord(ase));
            }
            tempList = new ArrayList<>(allDisplayList);

        } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_SALES)) {

            AppConstants.IS_SELECTED_AGGERATE_FARMER = false;
            //TODO Calculate startTime & endTime
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
            long startTime = 0, endTime = 0;
            try {
                startTime = sdf.parse(fromDate + " 00:00:00.000").getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                endTime = sdf.parse(toDate + " 23:59:59.999").getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ArrayList<SalesRecordEntity> allSalesRecordList = (ArrayList<SalesRecordEntity>) salesRecordDao.findAllForReport(
                    (startTime == 0) ? null : String.valueOf(startTime),
                    (endTime == 0) ? null : String.valueOf(endTime), (shift.equalsIgnoreCase("ALL") || shift.equals("")) ? null : SmartCCUtil.getFullShift(shift),
                    (cattleType.equalsIgnoreCase("BOTH") || cattleType.equalsIgnoreCase("")) ? null : cattleType);
            allDisplayList = new ArrayList<>();

            for (SalesRecordEntity salesRecordEntity : allSalesRecordList) {
                allDisplayList.add(Util.getReportEntityFromSalesRecord(salesRecordEntity));
            }
            tempList = new ArrayList<>(allDisplayList);

        } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_DISPATCH)) {

            AppConstants.IS_SELECTED_AGGERATE_FARMER = false;
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS");
            long startTime = 0, endTime = 0;
            try {
                startTime = sdf.parse(fromDate + " 00:00:00.000").getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                endTime = sdf.parse(toDate + " 23:59:59.999").getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            ArrayList<DispatchPostEntity> allDispatchCollectionList = (ArrayList<DispatchPostEntity>) dispatchRecordDao.findAllForReport(
                    (startTime == 0) ? null : String.valueOf(startTime),
                    (endTime == 0) ? null : String.valueOf(endTime), (shift.equalsIgnoreCase("ALL") || shift.equals("")) ? null : SmartCCUtil.getFullShift(shift),
                    (cattleType.equalsIgnoreCase("BOTH") || cattleType.equalsIgnoreCase("")) ? null : cattleType);
            allDisplayList = new ArrayList<>();

            for (DispatchPostEntity dispatchPostEntity : allDispatchCollectionList) {
                allDisplayList.add(Util.getReportEntityFromDispatchRecord(dispatchPostEntity));
            }
            tempList = new ArrayList<>(allDisplayList);

        } else if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_FARMER_EDITED)) {
            AppConstants.IS_SELECTED_AGGERATE_FARMER = false;

            allDisplayList = editRecordDao.findAllForReports((memberId.equalsIgnoreCase("ALL") || memberId.equalsIgnoreCase("") ? null : memberId.split(",")),
                    Util.getDateInLongFormat(fromDate),
                    Util.getDateInLongFormat(toDate),
                    (cattleType.equalsIgnoreCase("BOTH") || cattleType.equalsIgnoreCase("")) ? null : cattleType,
                    (shift.equalsIgnoreCase("ALL") || shift.equals("")) ? null : SmartCCUtil.getFullShift(shift));
            tempList = new ArrayList<>(allDisplayList);
        } else {
            recordCommitedStatus = getRecordCommittedStatus();

            AppConstants.IS_SELECTED_AGGERATE_FARMER = false;
            query = dbe.getQueryForReport(
                    "ALL", memberId, fromDate,
                    toDate, cattleType, shift, reportType, recordCommitedStatus);
            reportQuery = query + " ORDER BY " + DatabaseHandler.KEY_REPORT_MILKTYPE
                    + " , " + DatabaseHandler.KEY_REPORT_FARMERID;
//            allDisplayList = dbh.getReportFromQuery(query);

            allDisplayList = collectionRecordDao.findAllForReports(null,
                    (memberId.equalsIgnoreCase("ALL") || memberId.equalsIgnoreCase("")) ? null : memberId.split(","),
                    Util.getDateInLongFormat(fromDate),
                    Util.getDateInLongFormat(toDate),
                    (cattleType.equalsIgnoreCase("BOTH") || cattleType.equalsIgnoreCase("")) ? null : cattleType,
                    (shift.equalsIgnoreCase("ALL") || shift.equals("")) ? null : SmartCCUtil.getFullShift(shift),
                    mCollectionType,
                    String.valueOf(recordCommitedStatus),
                    null);
            tempList = new ArrayList<>(allDisplayList);
        }


    }

    public void getReport() {
        memberId = getIntent().getStringExtra(ReportHintConstant.MEMBER_ID);
        mcc = getIntent().getStringExtra(ReportHintConstant.MCC);
        fromDate = getIntent().getStringExtra(ReportHintConstant.DATE_FROM);
        toDate = getIntent().getStringExtra(ReportHintConstant.DATE_TO);
        cattleType = getIntent().getStringExtra(ReportHintConstant.CATTLE_TYPE);
        shift = getIntent().getStringExtra(ReportHintConstant.SHIFT);
        allowHeader = getIntent().getBooleanExtra(ReportHintConstant.ALLOW_HEADER, false);
        isAggerareFarmer = getIntent().getBooleanExtra(ReportHintConstant.IS_AGGERATE_FARMER, false);
        mCollectionType = getIntent().getStringExtra(ReportHintConstant.COLLECTION_TYPE);

    }

    public Intent setIntent() {
        Intent intent = new Intent(MemberReportActivity.this, FilterMemberReportActivity.class);
        intent.putExtra(ReportHintConstant.CATTLE_TYPE, cattleType);
        intent.putExtra(ReportHintConstant.DATE_FROM, fromDate);
        intent.putExtra(ReportHintConstant.DATE_TO, toDate);
        intent.putExtra(ReportHintConstant.SHIFT, shift);
        intent.putExtra(ReportHintConstant.ROUTE, memberId);
        intent.putExtra(ReportHintConstant.COLLECTION_TYPE, mCollectionType);

        return intent;
    }

    public void printFormatData(String printData) {
        if (printData == null) {
            Util.displayErrorToast("No data available!", MemberReportActivity.this);
            return;
        }
        Util.displayErrorToast("Please wait for print!", MemberReportActivity.this);
        byte[] byteArray = printData.getBytes(Charset.forName("UTF-8"));

        String str = new String(byteArray);
        PrinterManager parsingPrinterData = new PrinterManager(MemberReportActivity.this);
        PrinterManager.title = parsingPrinterData.getPeriodicTitle(fromDate,
                toDate, allowHeader, mCollectionType);
        printerManager.print(printData, PrinterManager.printCustomReport,
                fromDate, toDate, null);
        enablePrintButton();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(setIntent());
    }

    //To display the Huge records of data this needs to be do in different Thread
    private void setAdapter() {
        dbe = new DatabaseEntity(MemberReportActivity.this);
        dbh = DatabaseHandler.getDatabaseInstance();
        reportType = Util.REPORT_TYPE_FARMER;
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

                progressBar.setVisibility(View.GONE);
                if (allDisplayList == null) {
                    allDisplayList = new ArrayList<>();
                }
                reportAdapter = new ReportAdapter(
                        tempList, ReportAdapter.MEMBER_REPORT_VIEW, MemberReportActivity.this);
                recyclerView.setAdapter(reportAdapter);


//                recyclerView.setItemViewCacheSize(20);
//                recyclerView.setDrawingCacheEnabled(true);
//                recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

                listSize = allDisplayList.size();

                tempList = reportHelper.sortByConfiguration(
                        tempList, sessionManager.getKeySetLastMemberSorting(),
                        sessionManager.getKeySetLastMemberSortingOrder());
                reportAdapter.updateList(tempList);
                reportAdapter.notifyDataSetChanged();
                adapterHandler.removeCallbacks(adapterRunnable);
            }
        };


    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_P: {
                printAlert();
                break;
            }
            case KeyEvent.KEYCODE_A: {
               /* DatabaseEntity dbe = new DatabaseEntity(MccReportActivity.this);
                String query = dbe.getQueryForReport(
                        route, mcc, fromDate,
                        toDate, cattleType, shift, Util.REPORT_TYPE_MCC, 1);
                DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();*/
                AverageReportDetail averageReportDetail = smartCCUtil.getAverageOfReport(allDisplayList, false);
                smartCCUtil.alertForAverageReport(averageReportDetail);

                break;
            }

//            case KeyEvent.KEYCODE_DEL: {
//                Util.restartTab(MemberReportActivity.this);
//                return true;
//            }

            case KeyEvent.KEYCODE_PAGE_UP: {
                btnAverage.requestFocus();
                break;
            }


            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnBack.requestFocus();
                return false;

            }


            default: {
                break;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isPause = true;
    }


    public String printExistingFormat() {
        printData = null;
        if (tempList == null || tempList.size() < 1) {
            Util.displayErrorToast("No data available!", MemberReportActivity.this);
            return null;
        }
        progressBar.setVisibility(View.VISIBLE);

        final FormatPrintHelper fph = new FormatPrintHelper(MemberReportActivity.this, queue);
        fph.setOnFormatCompleteListener(new FormatPrintHelper.OnFormatCompleteListener() {
            @Override
            public void onFormatComplete() {
                shouldPrint = false;
            }
        });
        printerManager.openConnection();
        shouldPrint = true;
        String stringBuild = "";
        final PrinterManager printerManager = new PrinterManager(MemberReportActivity.this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPause && (shouldPrint || !queue.isEmpty())) {

                    if (!isEnter) {
                        isEnter = true;
                        Util.displayErrorToast("Please wait", MemberReportActivity.this);
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
                PrinterManager.title = printerManager.getPeriodicTitle(fromDate, toDate, allowHeader, mCollectionType);
                if (AmcuConfig.getInstance().getKeyAllowProteinValue()
                        && mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_FARMER)) {
                    printData = fph.setIncentivePeriodicRecords(tempList);

                } else {

                    printData = fph.setCustomPeriodicReport(tempList, mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE) ? true : false);
                }

            }
        }).start();

//

//                enableButton(btnPrint,true);

        return printData;

    }

    @Override
    public void onClick(final View view) {

        switch (view.getId()) {
            case R.id.btnAverage: {

                progressBar.setVisibility(View.VISIBLE);
                btnAverage.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SmartCCUtil smartCCUtil = new SmartCCUtil(MemberReportActivity.this);
                        averageReportDetail = smartCCUtil.getAverageOfReport(allDisplayList, false);

                        adapterHandler.post(adapterRunnable);
                    }
                }, threadNameReport).start();

                adapterRunnable = new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        smartCCUtil.alertForAverageReport(averageReportDetail);
                        btnAverage.setEnabled(true);
                    }
                };
                break;
            }
            case R.id.btnSort: {
                sortAlert();
                break;
            }
            case R.id.btnPrintReport: {
                printAlert();
                break;
            }
            case R.id.btnA4Print: {
                btnPrint.setVisibility(View.GONE);
                printAlert();
                break;
            }
            case R.id.btnBack: {
                startActivity(setIntent());
                finish();
                break;
            }
            default:
                break;
        }

    }

    private void sortAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.smartDialog);

        LayoutInflater layoutInflater = LayoutInflater.from(MemberReportActivity.this);
        View view = layoutInflater.inflate(R.layout.menu_sort, null);
        builder.setView(view);

        final TextView tvSortId, tvSortQty, tvSortAmt, tvSortRate, tvSortTime;
        RelativeLayout relativeLayout;

        relativeLayout = (RelativeLayout) view.findViewById(R.id.rlId);

        relativeLayout.requestFocus();


        tvSortAmt = (TextView) view.findViewById(R.id.tvSortAmount);
        tvSortId = (TextView) view.findViewById(R.id.tvSortId);
        tvSortQty = (TextView) view.findViewById(R.id.tvSortQuantity);
        tvSortRate = (TextView) view.findViewById(R.id.tvSortRate);
        tvSortTime = (TextView) view.findViewById(R.id.tvSortTime);

        setDrawable(tvSortAmt, AMOUNT_STATE, AMOUNT);
        setDrawable(tvSortRate, RATE_STATE, RATE);
        setDrawable(tvSortId, ID_STATE, ID);
        setDrawable(tvSortQty, QUANTITY_STATE, QUANTITY);
        setDrawable(tvSortTime, TIME_STATE, TIME);

        tvSortAmt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (AMOUNT_STATE == 0) {
                    getASCOrDESCQuery(query, AMOUNT, ASC);
                    AMOUNT_STATE = 1;
                } else {
                    getASCOrDESCQuery(query, AMOUNT, DESC);
                    AMOUNT_STATE = 0;
                }

                dialog.dismiss();

            }
        });

        tvSortRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (RATE_STATE == 0) {
                    getASCOrDESCQuery(query, RATE, ASC);
                    RATE_STATE = 1;
                } else {
                    getASCOrDESCQuery(query, RATE, DESC);
                    RATE_STATE = 0;
                }
                dialog.dismiss();

            }
        });

        tvSortId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ID_STATE == 0) {
                    getASCOrDESCQuery(query, ID, ASC);
                    ID_STATE = 1;
                } else {
                    getASCOrDESCQuery(query, ID, DESC);
                    ID_STATE = 0;
                }
                dialog.dismiss();
            }
        });
        tvSortQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (QUANTITY_STATE == 0) {
                    getASCOrDESCQuery(query, QUANTITY, ASC);
                    QUANTITY_STATE = 1;
                } else {
                    getASCOrDESCQuery(query, QUANTITY, DESC);
                    QUANTITY_STATE = 0;
                }
                dialog.dismiss();
            }
        });
        tvSortTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TIME_STATE == 0) {
                    getASCOrDESCQuery(query, TIME, ASC);
                    TIME_STATE = 1;
                } else {
                    getASCOrDESCQuery(query, TIME, DESC);
                    TIME_STATE = 0;
                }
                dialog.dismiss();

            }
        });
        dialog = builder.create();
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams WMLP = dialog.getWindow().getAttributes();
        WMLP.gravity = Gravity.CENTER;

//        WMLP.x = 0;   //x position
//        WMLP.y = 50;   //y position

        dialog.getWindow().setAttributes(WMLP);

        dialog.show();

    }

    private void setDrawable(TextView textView, int state, int type) {


        if (type != ORDER_TYPE) {
            textView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.btn_star, 0, 0, 0);
            return;
        }

        if (state == 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.arrow_up_float, 0, 0, 0);

        } else {
            textView.setCompoundDrawablesWithIntrinsicBounds(android.R.drawable.arrow_down_float, 0, 0, 0);


        }
    }

    private String getASCOrDESCQuery(String query, int type, String order) {
        ORDER_TYPE = type;
        LAST_ORDER = order;

        String updatedQuery = query;
        if (order.equalsIgnoreCase(ASC)) {
            updatedQuery = DatabaseEntity.getQueryForSort(query, type, DESC);
        } else {
            updatedQuery = DatabaseEntity.getQueryForSort(query, type, ASC);
        }

        reportQuery = updatedQuery;
        if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT)) {
            allDisplayList = dbh.getShiftSplitReportFromQuery(updatedQuery);

        } else {
            allDisplayList = dbh.getReportFromQuery(updatedQuery);
        }

        if (allDisplayList != null && allDisplayList.size() > 0) {
            reportAdapter = new ReportAdapter(
                    allDisplayList, ReportAdapter.MEMBER_REPORT_VIEW, MemberReportActivity.this);
            recyclerView.setAdapter(reportAdapter);
        }
        return reportQuery;
    }

    public void printMBRFormat() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                FormatPrintRecords formatPrintRecords = new FormatPrintRecords(MemberReportActivity.this);
                printData = formatPrintRecords.getReportFormat(
                        ReportHintConstant.MEMBER_BILL_REG, ReportHintConstant.MEMBER_BILL_REG_HEADING,
                        new SessionManager(MemberReportActivity.this).getCollectionID(),
                        getDateHeader(), tempList);

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

    public void printMBSFormat() {

        queue = new LinkedBlockingQueue<>();
        final FormatPrintHelper fph = new FormatPrintHelper(MemberReportActivity.this, queue);
        fph.setOnFormatCompleteListener(new FormatPrintHelper.OnFormatCompleteListener() {
            @Override
            public void onFormatComplete() {
                shouldPrint = false;
            }
        });

        //Display Dairy name
        if (amcuConfig.getDisplayDairyName() != null && !amcuConfig.getDisplayDairyName().toUpperCase().equalsIgnoreCase("NULL") && amcuConfig.getDisplayDairyName().trim().length() > 0) {

            if (amcuConfig.getDisplayDairyName().length() > 30) {
                dairyName = amcuConfig.getDisplayDairyName().substring(0, 30);
            } else {
                dairyName = amcuConfig.getDisplayDairyName();

            }

        }

        progressBar.setVisibility(View.VISIBLE);

        printerManager.openConnection();
        shouldPrint = true;
        String stringBuild = "";

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isPause && (shouldPrint || !queue.isEmpty())) {

                    if (!isEnter) {
                        isEnter = true;
                        Util.displayErrorToast("Please wait", MemberReportActivity.this);
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
                printData = new PrinterManager(MemberReportActivity.this).getMemberBillSummaryTitle(
                        fromDate, toDate);
                if (dairyName.length() > 0) {
                    printData = new PrinterManager(MemberReportActivity.this).centerAlignWithPaddingThermal(dairyName) + "\n" +
                            printData + "\n";
                }
                queue.add(printData);
                fph.getMemberBillSummaryReport(tempList);
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
        FormatPrintHelper fph = new FormatPrintHelper(MemberReportActivity.this);
        String strBuild = printerManager.getShiftSubTitle(fromDate, shift, mCollectionType);
        strBuild = strBuild + fph.getShiftReport(allDisplayList);
        return strBuild;
    }

    private void printAlert() {

        disablePrintButton();

        /*if (listSize > THRESHOLD_SIZE) {

            Toast.makeText(MemberReportActivity.this, "Too huge data for print, please select" +
                    "appropriate data!", Toast.LENGTH_LONG).show();
            return;
        }*/


        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = LayoutInflater.from(MemberReportActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_print, null);
        builder.setView(view);

        final Button btnCustomPrint, btnMBR, btnMBS, btnPrintShift, btnPrintPeriodic;

        btnCustomPrint = (Button) view.findViewById(R.id.printExisting);
        btnMBR = (Button) view.findViewById(R.id.printMBR);
        btnMBS = (Button) view.findViewById(R.id.printMBS);
        btnPrintPeriodic = (Button) view.findViewById(R.id.printPeriodic);
        btnPrintShift = (Button) view.findViewById(R.id.printShift);

        btnCustomPrint.setText("Custom print");

        btnMBR.setVisibility(View.GONE);
        btnPrintPeriodic.setVisibility(View.GONE);

        if (!showShiftOption()) {
            btnPrintShift.setVisibility(View.GONE);
            btnPrintPeriodic.setVisibility(View.VISIBLE);
        }

        if (mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_AGENT_SPLIT) ||
                mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_SALES)
                || mCollectionType.equalsIgnoreCase(Util.REPORT_TYPE_SAMPLE)) {
            btnMBR.setVisibility(View.GONE);
            btnMBS.setVisibility(View.GONE);
        }
        btnCustomPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MemberReportActivity.this, "Please wait!", Toast.LENGTH_LONG).show();
                printDialog.dismiss();
                printExistingFormat();


            }
        });

        btnMBR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printDialog.dismiss();
                printMBRFormat();


            }
        });

        btnPrintShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printDialog.dismiss();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        printData = getShiftFormat();

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
        });

        btnPrintPeriodic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                printDialog.dismiss();
                getPeriodicFormat();
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        printData = getPeriodicFormat();
                        printHandler.post(printRunnable);
                    }
                }, threadName).start();

                printRunnable = new Runnable() {
                    @Override
                    public void run() {
                        printFormatData(printData);

                    }
                };*/


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


        final Dialog finalDialog = printDialog;
        finalDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                    enableButton(btnCustomPrint, true);
                    finalDialog.dismiss();
                    return true;
                }
                return false;
            }
        });

    }

    private String getPeriodicFormat() {
        final FormatPrintHelper fph = new FormatPrintHelper(MemberReportActivity.this,
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
                        Util.displayErrorToast("Please wait", MemberReportActivity.this);
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
                queue.add(printerManager.getPeriodicSubTitle(fromDate, toDate, mCollectionType));
                fph.getPeriodicReport(tempList);
            }
        }).start();


        return "";
    }

    public boolean showShiftOption() {
        returnValue = false;
        if (shift != null && !shift.trim().isEmpty()
                && !shift.equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            if (!fromDate.equalsIgnoreCase(SmartCCConstants.SELECT_ALL) &&
                    (fromDate.equalsIgnoreCase(toDate))) {
                returnValue = true;
            }
        }
        return returnValue;
    }

    public void checkForPenDrive(boolean mailSend) {

        if (Util.checkForPendrive()) {
            Toast.makeText(MemberReportActivity.this, "Copying the file please wait..", Toast.LENGTH_SHORT).show();
            exportRecordsAndSendMail(mailSend);

        } else {

            usbAlert();
        }
    }

    public void usbAlert() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(
                MemberReportActivity.this);
        // set title
        alertDialogBuilder.setTitle("Alert!");

        // set dialog message
        alertDialogBuilder
                .setMessage(getResources().getString(R.string.pendrive_alert))
                .setCancelable(false)
                .setPositiveButton("Continue",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Toast.makeText(MemberReportActivity.this, "Copying the file please wait..", Toast.LENGTH_SHORT).show();
                                exportRecordsAndSendMail(false);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                dialog.cancel();
                            }
                        });
        // create alert dialog
        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    public void exportRecordsAndSendMail(final boolean mailSend) {

        isWrittentoEncFile = true;
        final WriteExcel writeExcel = new WriteExcel();
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
                writeExcel.setOutputFile(excelFileName.toString());
                try {
                    String file = excelFileName.toString().replace(".xls", "." + com.devapp.devmain.agentfarmersplit.AppConstants.CONSOLIDATED_EXTENSION);
                    checkFileValidation(allDisplayList);
                    if (isWrittentoEncFile) {
                        //  Util.toSendEncryptedRecords(ReportsActivity.this, allDetailRep, file, 8);
                        reportHelper.createEncryptedFileFromReport(allDisplayList, file);
                    }
                    isWrittenToFile = true;
                } catch (Exception e) {
                    isWrittenToFile = false;
                    e.printStackTrace();
                }
                myHandler.post(updateRunnable);

            }
        }).start();

        updateRunnable = new Runnable() {

            @Override
            public void run() {
                if (mailSend && isWrittenToFile) {
                    if (Util.isNetworkAvailable(MemberReportActivity.this)) {
                        Intent intentService = new Intent(MemberReportActivity.this,
                                SendEmail.class);
                        startService(intentService);


                        if (amcuConfig.getSendEmailToConfigureIDs()) {
                            Toast.makeText(MemberReportActivity.this, "Sending email...",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MemberReportActivity.this, "Please enable send email option from Configuration...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        if (amcuConfig.getSendEmailToConfigureIDs()) {
                            Toast.makeText(MemberReportActivity.this, "Please check the network connectivity!",
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

    public void setThefileName(File fileSmartAmcu) {
        String fDateTime = fromDate;
        String tDate = toDate;
        fDateTime = fromDate.replace("-", "_");
        tDate = toDate.replace("-", "_");
        String Shift = (shift.equalsIgnoreCase("M") ? "Morning" : "Evening");
        excelFileName = new File(fileSmartAmcu, new SessionManager(MemberReportActivity.this)
                .getSocietyName().replace(" ", "")
                + "_"
                + fDateTime
                + "T"
                + tDate
                + "_"
                + Shift + "_MCC_Shift_Report.xls");

    }

    public void checkFileValidation(ArrayList<ReportEntity> allReportsEnt) {
        if ((allReportsEnt == null)) {
            //For shift report
            Util.displayErrorToast("No data to write!", MemberReportActivity.this);
            isWrittentoEncFile = false;
        } else if (detailsCheck == Util.sendPeriodicReport && allReportsEnt.size() < 1) {
            //for periodic report
            Util.displayErrorToast("No data to write!", MemberReportActivity.this);
            isWrittentoEncFile = false;
        } else {
            isWrittentoEncFile = true;
        }
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
                        Toast.makeText(MemberReportActivity.this, "File has been successfully written in Pendrive.",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MemberReportActivity.this, "File has been successfully written in Internal storage.",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (isWrittentoEncFile) {
                        Toast.makeText(MemberReportActivity.this, "Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void updateVisibileExportOption() {
        if (sessionManager.getMCCStatus()) {

            btnExport.setVisibility(View.GONE);


            btnPrint.setVisibility(View.GONE);

        }
        btnSort.setVisibility(View.GONE);
        btnSummery.setVisibility(View.GONE);


    }

    private int getRecordCommittedStatus() {
        int status = 0;
        if (sessionManager.getMCCStatus()) {
            reportType = Util.REPORT_TYPE_MCC;

            if (!sessionManager.getRecordStatusComplete()) {
                status = 0;
            } else {
                status = 1;
            }
        } else {
            status = 1;
        }

        return status;
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

    private void disablePrintButton() {


        btnPrint.setEnabled(false);
    }

    private void enablePrintButton() {
        //  btnPrint.setEnabled(true);
        progressBar.setVisibility(View.GONE);
    }

    private void enableButton(final Button button, final boolean value) {
        button.setEnabled(true);
    }

    public long getDelayConstant(long count) {
        count = (count * 2) / 100;

        if (count <= 0) {
            return 1;
        }
        return count;


    }


    //Based on configuration allow option for
    //A4 or normal print (In A4 print first app will create the excel sheet
    // with the given report and then try to print with third party HP printer application)

    private void allowA4SizePrint() {

        if (amcuConfig.getEnableA4Printer()) {

        }

    }

    private void onClickA4SizePrint() {
        File fileSmartAmcu = new File(Environment
                .getExternalStorageDirectory().getAbsolutePath(), "smartAmcuReports");

        if (!fileSmartAmcu.exists()) {
            fileSmartAmcu.mkdirs();
        }

        setThefileName(fileSmartAmcu);
        excelFileName = reportHelper.createFileForPrint(fileSmartAmcu,
                0, tempList);

        if (excelFileName != null) {
            reportHelper.printViaShareIntent(excelFileName.toString());
            alertDialogPrint.dismiss();
        } else {
            Util.displayErrorToast("No report found, please try again!",
                    MemberReportActivity.this);
            alertDialogPrint.dismiss();
        }

    }


}