package com.devapp.devmain.macollection;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.multipleequipments.EndShift;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Upendra on 5/23/2016.
 */
public class MultipleMilkAnalyser extends AppCompatActivity {


    private final String TAG = MultipleMilkAnalyser.class.getName();
    public EditText etSnf1, etFat1, etSnf2, etFat2, etClr1, etClr2, etQuantity1,
            etQuantity2, etRate1, etRate2, etAmount1, etAmount2, etSampleId1, etSampleId2,
            etCenterId1, etCenterId2;
    public String nextSequenceNumber = null;
    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
    Handler handler = new Handler();
    UsbManager mUsbManager;
    AmcuConfig amcuConfig;
    SessionManager session;
    LinearLayout linearLayout1;
    LinearLayout linearLayout2;
    ArrayList<ReportEntity> allReportEntity = new ArrayList<>();
    ReportEntity reportEntityMA1, reportEntityMA2;
    Button btnCancelMa1, btnCancelMa2;
    //If Cancel is Enable, to maintained last sequence number
    String lastSequenceNumber;
    boolean isCollectionStarted = false;
    SmartCCUtil smartCCUtil;
    DriverConfiguration driverConfiguration;
    String bonusAmount = "0.00";
    AlertDialog.Builder alertBuilder;
    //Device connection segment
    Button btnEndShift, btnCancel;
    AlertDialog alertDialog;
    MaManager ma1Manager, ma2Manager;
    private LinearLayout statusLayout;
    private CheckBox cbMa, cbWs, cbRdu, cbPrinter;
    private Context context = this;
    private boolean maPing;
    private CollectionRecordDao collectionRecordDao;
    private CollectionHelper collectionHelper;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case SmartCCConstants.MA_CONNECTED:
                    startMaConnection();
                    break;
            }
        }
    };

    private BroadcastReceiver pingStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmartCCConstants.PING_STATUS)) {
                maPing = intent.getBooleanExtra(SmartCCConstants.MA_PING, true);
                displayDeviceStatus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_milk_analyser);
        initUtils();
        initViews();
        registerBroadcastReceivers();
    }

    private void registerBroadcastReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmartCCConstants.MA_CONNECTED);
        intentFilter.addAction(SmartCCConstants.WS_CONNECTED);
        intentFilter.addAction(SmartCCConstants.RDU_CONNECTED);
        intentFilter.addAction(SmartCCConstants.PRINTER_CONNECTED);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, intentFilter);
        LocalBroadcastManager.getInstance(context).registerReceiver(pingStatusReceiver,
                new IntentFilter(SmartCCConstants.PING_STATUS));
    }

    @Override
    protected void onStart() {
        super.onStart();
        setUpViews();
        setStatusLayout();
        registerMaListeners();
        startMaConnection();
        getAllreportEntity(nextSequenceNumber, null);
    }

    private void displayDeviceStatus() {

        cbMa.setChecked(maPing);
    }

    private void initViews() {
        //First milk analyser
        etAmount1 = (EditText) findViewById(R.id.etMA1Amount);
        etRate1 = (EditText) findViewById(R.id.etMA1Rate);
        etQuantity1 = (EditText) findViewById(R.id.etMA1QTY);
        etCenterId1 = (EditText) findViewById(R.id.etMA1Centerid);
        etClr1 = (EditText) findViewById(R.id.etMA1CLR);
        etSnf1 = (EditText) findViewById(R.id.etMA1SNF);
        etSampleId1 = (EditText) findViewById(R.id.etMA1Sampleid);
        etFat1 = (EditText) findViewById(R.id.etMA1FAT);

        linearLayout1 = (LinearLayout) findViewById(R.id.linearma1);
        btnCancelMa1 = (Button) findViewById(R.id.cancelMa1);

//Second milk analyser
        etAmount2 = (EditText) findViewById(R.id.etMA2Amount);
        etRate2 = (EditText) findViewById(R.id.etMA2Rate);
        etQuantity2 = (EditText) findViewById(R.id.etMA2QTY);
        etCenterId2 = (EditText) findViewById(R.id.etMA2Centerid);
        etClr2 = (EditText) findViewById(R.id.etMA2CLR);
        etSnf2 = (EditText) findViewById(R.id.etMA2SNF);
        etSampleId2 = (EditText) findViewById(R.id.etMA2Sampleid);
        etFat2 = (EditText) findViewById(R.id.etMA2FAT);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearma2);
        cbMa = (CheckBox) findViewById(R.id.cb_ma);
        cbWs = (CheckBox) findViewById(R.id.cb_ws);
        cbRdu = (CheckBox) findViewById(R.id.cb_rdu);
        cbPrinter = (CheckBox) findViewById(R.id.cb_printer);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        cbWs.setEnabled(false);
        cbRdu.setEnabled(false);
        cbPrinter.setEnabled(false);


        btnCancelMa2 = (Button) findViewById(R.id.cancelMa2);
        btnCancelMa1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayout2.getVisibility() == View.GONE) {
                    onFinish();
                } else {
                    linearLayout1.setVisibility(View.GONE);
                    onStopMa1();
                    if (etSampleId1.getText().toString().trim().length() > 0) {
                        lastSequenceNumber = etSampleId1.getText().toString().trim();
                    }
                }
            }
        });

        btnCancelMa2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (linearLayout1.getVisibility() == View.GONE) {
                    onFinish();
                } else {
                    onStopMa2();
                    linearLayout2.setVisibility(View.GONE);
                    if (etSampleId2.getText().toString().trim().length() > 0) {
                        lastSequenceNumber = etSampleId2.getText().toString().trim();
                    }
                }
            }
        });

        setStatusLayout();
    }

    private void setStatusLayout() {
        String[] strDevices = new String[5];
        strDevices[0] = amcuConfig.getKeyDevicePort1();
        strDevices[1] = amcuConfig.getKeyDevicePort2();
        strDevices[2] = amcuConfig.getKeyDevicePort3();
        strDevices[3] = amcuConfig.getKeyDevicePort4();
        strDevices[4] = amcuConfig.getKeyDevicePort5();
        ArrayList<String> configuredDevices = new ArrayList<String>();
        for (String device : strDevices) {
            if (!device.equals(DeviceName.NO_CONNECTION))
                configuredDevices.add(device);
        }

        if (!amcuConfig.getHotspotValue() &&
                !amcuConfig.getWifiValue()) {
            statusLayout.setVisibility(View.GONE);
        } else {

            for (String deviceName : configuredDevices) {
                switch (deviceName) {
                    case DeviceName.MILK_ANALYSER:
                        cbMa.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
        cbMa.setChecked(amcuConfig.getMaPingValue());
    }

    /**
     * to get the Uncommited records for current date and shift
     *
     * @param ma1OrMa2
     * @param lastSeqNumber
     */
    public void getAllreportEntity(String ma1OrMa2, String lastSeqNumber) {

        String currentDate = smartCCUtil.getReportFormatDate();
        String shift = SmartCCUtil.getShiftInPostFormat(MultipleMilkAnalyser.this);
        if (ma1OrMa2 == null) {
            allReportEntity = collectionRecordDao.getRecordsForTwoMa(null, null, currentDate, shift);
        }
//        } else if (SmartCCUtil.isMA1(ma1OrMa2)) {
//            allReportEntity = dbHandler.getUncommitedWSRecordsEntities(nextSequenceNumber
//                    , etSampleId2.getText().toString().trim(), lastSeqNumber);
//        }
//
        else {
            ValidationHelper validationHelper = new ValidationHelper();
            String sampleNo1 = String.valueOf(validationHelper.getIntegerFromString(etSampleId1.getText().toString().trim(), -1));
            String sampleNo2 = String.valueOf(validationHelper.getIntegerFromString(etSampleId2.getText().toString().trim(), -1));
            allReportEntity = collectionRecordDao.getRecordsForTwoMa(sampleNo1, sampleNo2, currentDate, shift);
        }

        //If no record found finish the activity with toast


        if (nextSequenceNumber == null) {
            if (allReportEntity != null && allReportEntity.size() > 1) {
                reportEntityMA1 = allReportEntity.get(0);
                reportEntityMA2 = allReportEntity.get(1);
                resetMA1Edit(reportEntityMA1);
                resetMA2Edit(reportEntityMA2);
            } else if (allReportEntity != null && allReportEntity.size() > 0) {
                reportEntityMA1 = allReportEntity.get(0);
                resetMA1Edit(reportEntityMA1);
            } else {

            }
        } else if (SmartCCUtil.isMA1(ma1OrMa2) && allReportEntity.size() > 0) {
            reportEntityMA1 = allReportEntity.get(0);
            resetMA1Edit(reportEntityMA1);
        } else if (ma1OrMa2.equalsIgnoreCase(DeviceName.MA2) && allReportEntity.size() > 0) {
            reportEntityMA2 = allReportEntity.get(0);
            resetMA2Edit(reportEntityMA2);
        } else {
            if (SmartCCUtil.isMA1(ma1OrMa2)
                    && (allReportEntity == null || allReportEntity.size() == 0)) {
                resetMA1Edit(null);

            } else if (ma1OrMa2.equalsIgnoreCase(DeviceName.MA2)
                    && (allReportEntity == null || allReportEntity.size() == 0)) {
                resetMA2Edit(null);
            }

        }

        if (!(etSampleId1.getText().toString().length() > 0)
                && !(etSampleId2.getText().toString().length() > 0)) {
            if (isCollectionStarted) {
                alertForEndShift();
            } else {
                Toast.makeText(MultipleMilkAnalyser.this, "No records found!", Toast.LENGTH_LONG).show();
                onFinish();
            }

        }
    }

    private void initUtils() {
        amcuConfig = AmcuConfig.getInstance();

        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        driverConfiguration = new DriverConfiguration();

        smartCCUtil = new SmartCCUtil(MultipleMilkAnalyser.this);
        collectionHelper = new CollectionHelper(MultipleMilkAnalyser.this);

        session = new SessionManager(MultipleMilkAnalyser.this);


        ma1Manager = MAFactory.getMA(DeviceName.MILK_ANALYSER, MultipleMilkAnalyser.this);
        ma2Manager = MAFactory.getMA(DeviceName.MA2, MultipleMilkAnalyser.this);
    }

    private void registerMaListeners() {
        if (ma1Manager != null)
            ma1Manager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (etSampleId1.getText().toString().trim().length() == 0) {
                                return;
                            }
                            if (maEntity != null) {

                                if (!smartCCUtil.validateFatAndSnfForZeroValues(maEntity.fat,
                                        maEntity.snf)) {
                                    onFinish();
                                } else {
                                    reportEntityMA1.setQualityParameters(maEntity);
                                    setMilkAnalyserData(reportEntityMA1, DeviceName.MILK_ANALYSER);
                                    resetMa1(maEntity);
                                    btnCancelMa1.setEnabled(false);
                                }
                            }
                        }
                    });

                }

                @Override
                public void onOtherMessage(String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            });
        if (ma2Manager != null)
            ma2Manager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (etSampleId2.getText().toString().trim().length() == 0) {
                                return;
                            }
                            if (maEntity != null) {
                                if (!smartCCUtil.validateFatAndSnfForZeroValues(maEntity.fat, maEntity.snf)) {
                                    onFinish();
                                } else {
                                    reportEntityMA2.setQualityParameters(maEntity);
                                    setMilkAnalyserData(reportEntityMA2, DeviceName.MA2);
                                    btnCancelMa2.setEnabled(false);
                                    resetMa2(maEntity);
                                }

                            }
                        }
                    });
                }

                @Override
                public void onOtherMessage(String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });
                }
            });
    }

    public void startMaConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ma1Manager != null) {
                    ma1Manager.startReading();
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ma2Manager != null) {
                    ma2Manager.startReading();
                }
            }
        }).start();
    }

    public void resetMa1(final MilkAnalyserEntity maEntity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayout1.setBackgroundColor(getResources().getColor(R.color.milkAnalyserOne));

                if (!fatAndSnfValidation(Double.valueOf(maEntity.fat)
                        , Double.valueOf(maEntity.snf))) {
                    Util.displayErrorToast("Invalid Fat or SNF values", MultipleMilkAnalyser.this);
                    return;
                }

                if (reportEntityMA1 != null) {
                    //  deleteExisting(reportEntityMA1.columnId);
                    ReportEntity reportEntity = getReportEntity(reportEntityMA1, maEntity, DeviceName.MILK_ANALYSER,
                            smartCCUtil.getCurrentRateChartForCattle(reportEntityMA1.milkType));
                    try {
                        smartCCUtil.saveReportsOnSdCard(reportEntity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addToDatabase(reportEntity);
                }
                nextSequenceNumber = etSampleId1.getText().toString().trim();
                getAllreportEntity(DeviceName.MILK_ANALYSER, lastSequenceNumber);
                lastSequenceNumber = null;


            }
        }, 8000);

    }

    public void resetMa2(final MilkAnalyserEntity maEntity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                linearLayout2.setBackgroundColor(getResources().getColor(R.color.milkAnalyserTwo));

                if (!fatAndSnfValidation(Double.valueOf(maEntity.fat)
                        , Double.valueOf(maEntity.snf))) {
                    Util.displayErrorToast("Invalid Fat or SNF values", MultipleMilkAnalyser.this);
                    return;
                }

                if (reportEntityMA2 != null) {
                    //    deleteExisting(reportEntityMA2.columnId);
                    ReportEntity reportEntity = getReportEntity(reportEntityMA2, maEntity, DeviceName.MA2
                            , smartCCUtil.getCurrentRateChartForCattle(reportEntityMA2.milkType));
                    try {
                        smartCCUtil.saveReportsOnSdCard(reportEntity);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    addToDatabase(reportEntity);
                }
                nextSequenceNumber = etSampleId2.getText().toString().trim();
                getAllreportEntity(DeviceName.MA2, lastSequenceNumber);
                lastSequenceNumber = null;
            }
        }, 5000);

    }

    public void resetMA1Edit(ReportEntity reportEnt) {

        if (reportEnt == null || reportEnt.farmerId == null || reportEnt.farmerId.length() < 1) {
            etCenterId1.getText().clear();
            etSampleId1.getText().clear();
            etQuantity1.getText().clear();
            onStopMa1();
        } else {
            etCenterId1.setText(reportEnt.farmerId);
            etSampleId1.setText(String.valueOf(reportEnt.sampleNumber));
            etQuantity1.setText(String.valueOf(reportEnt.quantity));
        }

        etFat1.getText().clear();
        etSnf1.getText().clear();
        etClr1.getText().clear();
        etRate1.getText().clear();
        etAmount1.getText().clear();
        btnCancelMa1.setEnabled(true);


    }

    public void resetMA2Edit(ReportEntity reportEnt) {

        if (reportEnt == null || reportEnt.farmerId == null || reportEnt.farmerId.length() < 1) {
            etCenterId2.getText().clear();
            etSampleId2.getText().clear();
            etQuantity2.getText().clear();
            onStopMa2();
        } else {
            etCenterId2.setText(reportEnt.farmerId);
            etSampleId2.setText(String.valueOf(reportEnt.sampleNumber));
            etQuantity2.setText(String.valueOf(reportEnt.quantity));
        }

        etFat2.getText().clear();
        etSnf2.getText().clear();
        etClr2.getText().clear();
        etRate2.getText().clear();
        etAmount2.getText().clear();
        btnCancelMa2.setEnabled(true);
    }

    public void setMilkAnalyserData(ReportEntity reportEntity, String isMa1OrMa2) {

        reportEntity = collectionHelper.getCommittedReportEntity(reportEntity, isMa1OrMa2);

        if (SmartCCUtil.isMA1(isMa1OrMa2)) {
            etFat1.setText(String.valueOf(reportEntity.getDisplayFat()));
            etSnf1.setText(String.valueOf(reportEntity.getDisplaySnf()));
            etClr1.setText(String.valueOf(reportEntity.getDisplayClr()));
            etRate1.setText(String.valueOf(reportEntity.getDisplayRate()));
            etAmount1.setText(String.valueOf(reportEntity.getDisplayAmount()));
            linearLayout1.setBackgroundColor(getResources().getColor(R.color.milkAnalayserGreen));

        } else {
            etFat2.setText(String.valueOf(reportEntity.getDisplayFat()));
            etSnf2.setText(String.valueOf(reportEntity.getDisplaySnf()));
            etClr2.setText(String.valueOf(reportEntity.getDisplayClr()));
            etRate2.setText(String.valueOf(reportEntity.getDisplayRate()));
            etAmount2.setText(String.valueOf(reportEntity.getDisplayAmount()));
            linearLayout2.setBackgroundColor(getResources().getColor(R.color.milkAnalayserGreen));
        }
    }


    public ReportEntity getReportEntity(ReportEntity reportEnt,
                                        MilkAnalyserEntity maEntity, String isMa1OrMa2, String rateChartName) {
        int txNumber = new SessionManager(MultipleMilkAnalyser.this)
                .getTXNumber() + 1;

        String date = smartCCUtil.getReportFormatDate();
        ReportEntity reportEntity = new ReportEntity();

        reportEntity.temp = maEntity.temp;


        reportEntity.fat = maEntity.fat;
        reportEntity.snf = maEntity.snf;

        reportEntity.user = reportEnt.user;
        reportEntity.farmerId = reportEnt.farmerId;
        reportEntity.farmerName = reportEnt.farmerName;
        reportEntity.socId = String.valueOf(session.getSocietyColumnId());

        reportEntity.quantity = reportEnt.quantity;

        reportEntity.time = Util.getTodayDateAndTime(3, MultipleMilkAnalyser.this, false);
        reportEntity.milkType = reportEnt.milkType;
        reportEntity.lDate = Util.getDateInLongFormat(date);
        reportEntity.txnNumber = Integer.parseInt(Util.getTxnNumber(txNumber));

        reportEntity.miliTime = reportEnt.miliTime;
        reportEntity.milkAnalyserTime = Util.getCurrentTimeInMili();

        reportEntity.weighingTime = reportEnt.weighingTime;
        reportEntity.awm = 0.00;
        Util.setCollectionStartedWithMilkType(session.getMilkType(), MultipleMilkAnalyser.this);

        reportEntity.manual = "Manual";
        reportEntity.qualityMode = "Auto";

        reportEntity.quantityMode = reportEnt.quantityMode;

        reportEntity.clr = maEntity.clr;
        reportEntity.bonus = Double.parseDouble(bonusAmount);
        reportEntity.recordCommited = Util.REPORT_COMMITED;
        reportEntity.collectionType = Util.REPORT_TYPE_MCC;

        // Adding milkquality

        reportEntity.milkQuality = "GOOD";
        reportEntity.rateMode = "Auto";
        reportEntity.numberOfCans = reportEnt.numberOfCans;

        reportEntity.centerRoute = Util.getRouteFromChillingCenter(MultipleMilkAnalyser.this, reportEnt.farmerId);
        reportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;

        String rate = "0.00";

        if (SmartCCUtil.isMA1(isMa1OrMa2)) {
            try {
                reportEntity.sampleNumber = Integer.valueOf(etSampleId1.getText().toString().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
            rate = etRate1.getText().toString().trim();
            reportEntity.rate = Double.parseDouble(etRate1.getText().toString().trim());
            reportEntity.amount = Double.parseDouble(etAmount1.getText().toString().trim());

            reportEntity.serialMa = 1;
            reportEntity.maName = amcuConfig.getMa1Name();
        } else {

            try {
                reportEntity.sampleNumber = Integer.valueOf(etSampleId2.getText().toString().trim());
            } catch (NumberFormatException e) {

                e.printStackTrace();
                return null;
            }
            rate = etRate2.getText().toString().trim();
            reportEntity.rate = Double.valueOf(etRate2.getText().toString().trim());
            reportEntity.amount = Double.valueOf(etAmount2.getText().toString().trim());

            reportEntity.serialMa = 2;
            reportEntity.maName = amcuConfig.getMa2Name();
        }

        boolean isReject = smartCCUtil.isMilkRejected(maEntity);

        if (!isReject) {
            reportEntity.status = "Accept";
        } else {
            reportEntity.status = "Reject";
        }
        reportEntity.rateChartName = rateChartName;
        reportEntity.kgWeight = reportEnt.kgWeight;
        reportEntity.ltrsWeight = reportEnt.ltrsWeight;
        //Added tipping details
        reportEntity.tippingStartTime = reportEnt.tippingStartTime;
        reportEntity.tippingEndTime = reportEnt.tippingEndTime;
        reportEntity.agentId = reportEnt.farmerId;
        reportEntity.milkStatusCode = smartCCUtil.getMilkStatusCode("GOOD");
        if (amcuConfig.getRateCalculatedFromDevice()) {
            reportEntity.rateCalculation = 1;
        } else {
            reportEntity.rateCalculation = 0;
        }

        reportEntity.fatKg = Double.valueOf(decimalFormatAmount.format(
                Util.convertPercentageToKg(reportEntity.kgWeight, reportEntity.fat)));
        reportEntity.snfKg = Double.valueOf(decimalFormatAmount.format(
                Util.convertPercentageToKg(reportEntity.kgWeight, reportEntity.snf)));

        reportEntity.protein = maEntity.protein;
        reportEntity.lactose = maEntity.lactose;
        reportEntity.conductivity = maEntity.conductivity;
        reportEntity.postDate = SmartCCUtil.getDateInPostFormat();
        reportEntity.postShift = SmartCCUtil.getShiftInPostFormat(MultipleMilkAnalyser.this);

        smartCCUtil.setCollectionStartData(reportEntity);
        return reportEntity;
    }

    public String addToDatabase(ReportEntity reportEntity) {

        String dbError = null;
        reportEntity.resetSentMarkers();

        Util.setDailyDateOrShift(Util.getTodayDateAndTime(7, MultipleMilkAnalyser.this, false),
                reportEntity.postShift);
        try {
            collectionRecordDao.saveOrUpdate(reportEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbError;
    }

    public void deleteExisting(long colId) {
        DatabaseManager dbm = new DatabaseManager(MultipleMilkAnalyser.this);

        dbm.deleteSelectedColumnIds(String.valueOf(colId));
    }

    public void onStopMa2() {
        if (ma2Manager != null) {
            ma2Manager.stopReading();
        }
    }

    public void onStopMa1() {
        if (ma1Manager != null) {
            ma1Manager.stopReading();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        onStopMa1();
        onStopMa2();
    }

    public double getBonusAmount(MilkAnalyserEntity maEntity) {
        double bonus = 0;
        CollectionHelper collectionHelper = new CollectionHelper(MultipleMilkAnalyser.this);
        bonus = collectionHelper.getBonusAmount(session.getFarmerID(), session.getMilkType(), maEntity);
        return bonus;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F1:

                if (amcuConfig.getSmartCCFeature()) {
                    Intent intent = new Intent(getApplicationContext(), FarmerScannerActivity.class);
                    intent.putExtra("USER_SELECTION", "WS");
                    startActivity(intent);
                    finish();
                }
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(MultipleMilkAnalyser.this);
                return true;

            case KeyEvent.KEYCODE_F11:
                Util.restartTab(MultipleMilkAnalyser.this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(MultipleMilkAnalyser.this, null);
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public void alertForEndShift() {
        alertBuilder = new AlertDialog.Builder(MultipleMilkAnalyser.this);
        final EndShift endShift = new EndShift(MultipleMilkAnalyser.this);
        String strDetails = endShift.getCenterAveragedetails();

        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.alert_sendingmail, null);

        TextView tvHeader = (TextView) view.findViewById(R.id.tvheader);
        TextView tvAlert = (TextView) view.findViewById(R.id.tvAlert);

        ImageView ivLogo = (ImageView) view.findViewById(R.id.ivsplash);
        ivLogo.setVisibility(View.GONE);
        RelativeLayout ProgressL = (RelativeLayout) view
                .findViewById(R.id.progress_layout);
        ProgressL.setVisibility(View.GONE);
        tvAlert.setVisibility(View.VISIBLE);

        tvAlert.setText(strDetails);
        tvHeader.setText("Shift details");

        btnEndShift = (Button) view.findViewById(R.id.btnResend);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);

        btnEndShift.setText("End shift");
        btnCancel.setText("Cancel");

        btnEndShift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endShift.doEndShift();
                alertDialog.cancel();
                onFinish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                onFinish();

            }
        });
        // create alert dialog
        alertDialog = alertBuilder.create();
        alertDialog.setView(view);
        // show it
        alertDialog.show();

        // To display the alert dialog in center

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();


        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 500;
        lp.height = 500;
        // lp.x = -170;
        // lp.y = 100;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);
    }


    private void setUpViews() {
        if (amcuConfig.getSmartCCFeature()) {
            etClr1.setVisibility(View.GONE);
            etClr2.setVisibility(View.GONE);
            etRate1.setVisibility(View.GONE);

            etRate2.setVisibility(View.GONE);
            etQuantity1.setVisibility(View.GONE);
            etQuantity2.setVisibility(View.GONE);

            etAmount1.setVisibility(View.GONE);
            etAmount2.setVisibility(View.GONE);

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onFinish();
    }

    public void onFinish() {
        startActivity(new Intent(MultipleMilkAnalyser.this, FarmerScannerActivity.class));
        finish();

    }


    public boolean fatAndSnfValidation(double fat, double snf) {
        if (fat < 0 || snf < 0 || fat >= Util.MAX_FAT_LIMIT || snf >= Util.MAX_SNF_LIMIT) {
            return false;
        } else {
            return true;
        }

    }


}
