package com.devapp.devmain.macollection;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.eevoskos.robotoviews.widget.RobotoButton;
import com.eevoskos.robotoviews.widget.RobotoEditText;
import com.eevoskos.robotoviews.widget.RobotoTextView;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.WeightLimit;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
import com.devapp.devmain.main.BaseActivity;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.rdu.RduFactory;
import com.devapp.devmain.rdu.RduManager;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.user.WifiUtility;
import com.devapp.devmain.util.UIValidation;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.devmain.util.logger.Log;
import com.devapp.devmain.ws.WsFactory;
import com.devapp.devmain.ws.WsManager;
import com.devapp.smartcc.entityandconstants.QuantityEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;


/**
 * Created by u_pendra on 22/6/18.
 */

public class SampleCollectionActivity extends BaseActivity implements View.OnClickListener, WeightLimit {

    private static SampleCollectionActivity mActivityInstance = null;
    private final int SAMPLE_TYPE_WATER = 0;
    private final int SAMPLE_TYPE_CLEANING = 1;
    private final int SAMPLE_TYPE_RINSING = 2;
    private final int SAMPLE_TYPE_SAMPLE = 3;
    private final int SAMPLE_TYPE_RATE_CHECK = 4;
    Runnable timeOutRunnable;
    Handler timeOutHandler = new Handler();
    long timeoutLimit = System.currentTimeMillis() + 300000L;
    private Context context = this;
    private String TAG = "SAMPLE_COLLECTION";
    private RelativeLayout header;
    private RobotoTextView tvheader, tvFarmerid, txtSID, tvName,
            tvFatAuto, tvSnfAuto, txtCLR, tvProtein, tvProteinRate, tvMilkWeight, tvRate, tvAmount, tvQualityOfMilk;
    private RobotoButton btnAutoManual, btnNext, btnReject;
    private LinearLayout statusLayout;
    private CheckBox cbMa, cbWs, cbRdu, cbPrinter;
    private TableLayout tableLayout1;
    private TableRow trFarmId, trFat, trSnf, trProtein, trMilkWeight, trAmount, trqualityOfMilk;
    private RobotoEditText etFarmerId, etSId, etFarmerName, etFat, etSnf, etClr, etProtein, etProteinRate, etMilkweight, etRate, etAmount;
    private Spinner spinnerQualityOfMilk;
    private View separator;
    private DatabaseHandler mDatabaseHandler;
    private UsbManager mUsbManager;
    private AmcuConfig amcuConfig;
    private SessionManager mSession;
    private WifiUtility mWifiUtility;
    private MaManager maManager;
    private WsManager wsManager;
    private PrinterManager mPrinterManager;
    private Handler mPingHandler;
    private UIValidation mUIValidation;
    private Calendar mCalendar;
    private DriverConfiguration mDriverConfiguration;
    private CollectionRecordDao mCollectionRecordDao;
    private SmartCCUtil mSmartCCUtil;
    private ValidationHelper mValidationHelper;
    private ReportEntity mReportEntity;
    private CollectionHelper mCollectionHelper;
    private MilkAnalyserEntity milkAnalyserEntity;
    View.OnKeyListener qualityFieldOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {


            if (((event.getAction() == KeyEvent.ACTION_UP) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                    (event.getAction() == KeyEvent.ACTION_DOWN &&
                            (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                if (v == etFat) {
                    if (etSnf.getVisibility() == View.VISIBLE) {
                        setCursorOnText(etSnf);

                    } else if (etClr.getVisibility() == View.VISIBLE) {
                        setCursorOnText(etClr);
                    }
                    return false;
                } else if (v == etSnf) {
                    if (etClr.getVisibility() == View.VISIBLE) {
                        setCursorOnText(etClr);
                    } else if (etProtein.getVisibility() == View.VISIBLE) {
                        setCursorOnText(etProtein);
                    } else {
                        onSubmit();
                    }
                } else if (v == etClr) {
                    onSubmit();
                } else if (v == etProtein) {
                    onSubmit();
                }

                return false;
            }


            if (Util.isDigitKey(keyCode, event)) {
                addQualityWatcher(v);
                return false;
            } else {
                return false;
            }
        }
    };
    private boolean maPing, wsPing, rduPing, printerPing;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case SmartCCConstants.MA_CONNECTED:
                    startMaReading();
                    break;
                case SmartCCConstants.WS_CONNECTED:
                    openWsConnection();
                    break;
                case SmartCCConstants.RDU_CONNECTED:
                    break;
                case SmartCCConstants.PRINTER_CONNECTED:
                    break;
            }
        }
    };

    private BroadcastReceiver pingStatusReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SmartCCConstants.PING_STATUS)) {
                maPing = intent.getBooleanExtra(SmartCCConstants.MA_PING, true);
                wsPing = intent.getBooleanExtra(SmartCCConstants.WS_PING, true);
                rduPing = intent.getBooleanExtra(SmartCCConstants.RDU_PING, true);
                printerPing = intent.getBooleanExtra(SmartCCConstants.PRINTER_PING, true);
                displayDeviceStatus();
            }
        }
    };
    private String beforeChage = null, afterChange = null;

    public static SampleCollectionActivity getInstance() {
        return mActivityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.allusbdevice_landscape);
        mActivityInstance = SampleCollectionActivity.this;
        initializeViews();
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

    private void displayDeviceStatus() {

        cbMa.setChecked(maPing);
        cbWs.setChecked(wsPing);
        cbRdu.setChecked(rduPing);
        cbPrinter.setChecked(printerPing);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initializeUtility();
        initializeReportEntity();
        displayUI();
        setUserDetails();
        registerMaListener();
        registerWsListener();
        setHeader();
        disableViews();
        setStatusLayout();
        setQuantity();

        if (!mReportEntity.isUncommittedRecordSaved()) {
            startMaReading();
            setQualityManual(amcuConfig.getMaManual());
           /* if (mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE
                    || mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK) {
                openWsConnection();
            }*/
        } else {
            setQuantityEnable();
            displayQualityItem();
            displayRate();
            // displayQuantity(mReportEntity.getQuantity());

            setQualityManual(false);
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.SAVED_UNCOMMITTED_RECORD, true);
            afterGettingMaData();
             /*  if (mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE
                    || mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK) {
                openWsConnection();
            }*/
        }
        setViewForSnfAndClr();
    }

    public void initializeViews() {

        header = (RelativeLayout) findViewById(R.id.header);
        tvheader = (RobotoTextView) findViewById(R.id.tvheader);
        btnAutoManual = (RobotoButton) findViewById(R.id.btnAutoManual);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        cbMa = (CheckBox) findViewById(R.id.cb_ma);
        cbWs = (CheckBox) findViewById(R.id.cb_ws);
        cbRdu = (CheckBox) findViewById(R.id.cb_rdu);
        cbPrinter = (CheckBox) findViewById(R.id.cb_printer);
        tableLayout1 = (TableLayout) findViewById(R.id.tableLayout1);
        trFarmId = (TableRow) findViewById(R.id.trFarmId);
        tvFarmerid = (RobotoTextView) findViewById(R.id.tvFarmerid);
        etFarmerId = (RobotoEditText) findViewById(R.id.etFarmerId);
        txtSID = (RobotoTextView) findViewById(R.id.txtSID);
        etSId = (RobotoEditText) findViewById(R.id.etSId);
        tvName = (RobotoTextView) findViewById(R.id.tvName);
        etFarmerName = (RobotoEditText) findViewById(R.id.etFarmerName);
        trFat = (TableRow) findViewById(R.id.trFat);
        trSnf = (TableRow) findViewById(R.id.trSnf);
        tvFatAuto = (RobotoTextView) findViewById(R.id.tvFatAuto);
        etFat = (RobotoEditText) findViewById(R.id.etFat);
        tvSnfAuto = (RobotoTextView) findViewById(R.id.tvSnfAuto);
        etSnf = (RobotoEditText) findViewById(R.id.etSnf);
        txtCLR = (RobotoTextView) findViewById(R.id.txtCLR);
        etClr = (RobotoEditText) findViewById(R.id.etClr);
        trProtein = (TableRow) findViewById(R.id.trProtein);
        tvProtein = (RobotoTextView) findViewById(R.id.tvProtein);
        etProtein = (RobotoEditText) findViewById(R.id.etProtein);
        tvProteinRate = (RobotoTextView) findViewById(R.id.tvProteinRate);
        etProteinRate = (RobotoEditText) findViewById(R.id.etProteinRate);
        trMilkWeight = (TableRow) findViewById(R.id.trMilkWeight);
        tvMilkWeight = (RobotoTextView) findViewById(R.id.tvMilkWeight);
        etMilkweight = (RobotoEditText) findViewById(R.id.etMilkweight);
        tvRate = (RobotoTextView) findViewById(R.id.tvRate);
        etRate = (RobotoEditText) findViewById(R.id.etRate);
        etProtein = (RobotoEditText) findViewById(R.id.etProtein);
        etProteinRate = (RobotoEditText) findViewById(R.id.etProteinRate);
        trAmount = (TableRow) findViewById(R.id.trAmount);
        tvAmount = (RobotoTextView) findViewById(R.id.tvAmount);
        etAmount = (RobotoEditText) findViewById(R.id.etAmount);
        trqualityOfMilk = (TableRow) findViewById(R.id.trqualityOfMilk);
        tvQualityOfMilk = (RobotoTextView) findViewById(R.id.tvQualityOfMilk);
        spinnerQualityOfMilk = (Spinner) findViewById(R.id.spinnerQualityOfMilk);
        btnNext = (RobotoButton) findViewById(R.id.btnNext);
        btnReject = (RobotoButton) findViewById(R.id.btnReject);


        btnAutoManual.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnReject.setOnClickListener(this);

        btnReject.setText(getResources().getString(R.string.submit));
        txtSID.setVisibility(View.GONE);
        etSId.setVisibility(View.GONE);
        etMilkweight.setEnabled(false);

    }

    private void initializeUtility() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        amcuConfig = AmcuConfig.getInstance();
        //TODO rename if required
        mSession = new SessionManager(mActivityInstance);
        //TODO initialize only if required

        if (amcuConfig.getHotspotValue()) {
            mWifiUtility = new WifiUtility();
            mPingHandler = new Handler();
        }

        //TODO initialize only if required
        mPrinterManager = new PrinterManager(mActivityInstance);

        //TODO check if we make it as singleton
        mUIValidation = UIValidation.getInstance();
        mCollectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        //TODO initialize only if required
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        mSmartCCUtil = new SmartCCUtil(SampleCollectionActivity.this);
        mDriverConfiguration = new DriverConfiguration();


        //TODO rename the validation class
        mValidationHelper = new ValidationHelper();

        maManager = MAFactory.getMA(DeviceName.MILK_ANALYSER, SampleCollectionActivity.this);
        wsManager = WsFactory.getWs(DeviceName.WS, SampleCollectionActivity.this);

        mCollectionHelper = new CollectionHelper(SampleCollectionActivity.this);


    }

    public void initializeReportEntity() {


        this.mReportEntity = mCollectionRecordDao.findByDateShiftAndCommitStatus(
                SmartCCUtil.getDateInPostFormat(), mSmartCCUtil.getFullShift(
                        Util.getCurrentShift()),
                CollectionConstants.UNSENT);
        if (this.mReportEntity == null) {

            this.mReportEntity = new ReportEntity();
            mReportEntity.user = mSession.getUserId();
            mReportEntity.farmerId = mSession.getFarmerID();
            mReportEntity.farmerName = mSession.getFarmerName();
//            mReportEntity.agentId = mSmartCCUtil.getAgentId();
            mReportEntity.socId = mSession.getCollectionID();
            mReportEntity.postDate = mSmartCCUtil.getReportFormatDate();
            mReportEntity.postShift = SmartCCUtil.getShiftInPostFormat(SampleCollectionActivity.this);
            mReportEntity.centerRoute = Util.getRouteFromChillingCenter(SampleCollectionActivity.this, mSession.getFarmerID());
            mReportEntity.rateChartName = amcuConfig.getRateChartName();
            mReportEntity.setMilkType(CattleType.TEST);
            //TODO check the usage
            mReportEntity.lDate = Util.getDateInLongFormat(mReportEntity.postDate);
            mReportEntity.time = Util.getTodayDateAndTime(3, SampleCollectionActivity.this, true);

            mReportEntity.miliTime = mCalendar.getTimeInMillis();
            //TODO remove txnumber
            mReportEntity.txnNumber = mSession.getTXNumber() + 1;

            mReportEntity.collectionType = Util.REPORT_TYPE_SAMPLE;

            mReportEntity.quantityMode = SmartCCConstants.AUTO;
            mReportEntity.qualityMode = SmartCCConstants.AUTO;
            mReportEntity.rateMode = SmartCCConstants.AUTO;
            mReportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;
            mReportEntity.milkStatusCode = mSmartCCUtil.getMilkStatusCode("GOOD");
            if (amcuConfig.getRateCalculatedFromDevice()) {
                mReportEntity.rateCalculation = SmartCCConstants.RATE_FROM_DEVICE;
            } else {
                mReportEntity.rateCalculation = SmartCCConstants.RATE_FROM_CLOUD;
            }
            mReportEntity.sampleNumber = 0;
            mReportEntity.serialMa = 1;
            mReportEntity.maName = amcuConfig.getMA();
            mSession.setTXNumber(mSession.getTXNumber() + 1);
            mReportEntity.milkQuality = AppConstants.MILK_QUALITY_NA;

            mReportEntity.setOverallCollectionStatus(0, false);

            try {
                mReportEntity.setManual(getIntent().getStringExtra("isAutoOrManual"));
            } catch (Exception e) {
                mReportEntity.setManual(SmartCCConstants.MANUAL);
                e.printStackTrace();
            }
        } else {
            mReportEntity.initialize();
            mReportEntity.setCollectionStatus(1);
            mReportEntity.setOverallCollectionStatus(0, false);
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUALITY_DONE, true);
            milkAnalyserEntity = mReportEntity.getQualityParameters();
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.SAVED_UNCOMMITTED_RECORD, true);
        }

        if (Integer.parseInt(mReportEntity.farmerId) == 991) {
            mReportEntity.setSampleType(SAMPLE_TYPE_WATER);
        } else if (Integer.parseInt(mReportEntity.farmerId) == 999) {
            mReportEntity.setSampleType(SAMPLE_TYPE_SAMPLE);
        } else if (Integer.parseInt(mReportEntity.farmerId) == 9997) {
            mReportEntity.setSampleType(SAMPLE_TYPE_CLEANING);
        } else if (Integer.parseInt(mReportEntity.farmerId) == 9998) {
            mReportEntity.setSampleType(SAMPLE_TYPE_RINSING);
        } else if (Integer.parseInt(mReportEntity.farmerId) == 9999) {
            mReportEntity.setSampleType(SAMPLE_TYPE_RATE_CHECK);
        }

        if (mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE) {
            try {
                String cattleType = getIntent().getExtras().getString("FarmerCattleType");
                mReportEntity.setMilkType(cattleType);
            } catch (Exception e) {

            }

        }

    }

    private void displayUI() {
        btnReject.setText(getResources().getString(R.string.submit));
        if (mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE) {
            displayUIForSample();
        } else if (mReportEntity.getSampleType() == SAMPLE_TYPE_WATER) {
            displayUIForWaterTest();
        } else if (mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING) {
            displayUIForCleaning();
        } else if (mReportEntity.getSampleType() == SAMPLE_TYPE_RINSING) {
            displayUIForRinsing();
        } else if (mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK) {
            displayUIForCollectionCheck();
        }

    }

    public void startMaReading() {
        if (maManager != null) {
            maManager.startReading();
        }
        if (mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING) {
            checkTimeout();
        }
    }

    public void checkTimeout() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                if (checkForTimeOut(System.currentTimeMillis(), timeoutLimit)) {
                    timeOutHandler.post(timeOutRunnable);
                } else {
                    try {
                        Thread.sleep(20000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    checkTimeout();
                }

            }
        }).start();

        timeOutRunnable = new Runnable() {

            @Override
            public void run() {

                setMAData("Timeout");
                stopMaReading();

            }
        };
    }

    public boolean checkForTimeOut(long t1, long t2) {

        if (t1 > t2) {
            return true;
        } else {
            return false;
        }

    }

    private void openWsConnection() {
        if (wsManager != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    wsManager.openConnection();
                }
            }).start();
        }
    }

    /**
     * Display all quality parameters
     */
    private void displayUIForWaterTest() {

        trMilkWeight.setVisibility(View.GONE);
        trAmount.setVisibility(View.GONE);
        tvRate.setVisibility(View.GONE);
        etRate.setVisibility(View.GONE);
    }

    private void displayUIForCleaning() {
        trSnf.setVisibility(View.GONE);
        trFat.setVisibility(View.GONE);
        trAmount.setVisibility(View.GONE);
        tvRate.setVisibility(View.GONE);
        etRate.setVisibility(View.GONE);
        trMilkWeight.setVisibility(View.GONE);
        etMilkweight.setVisibility(View.GONE);
        tvMilkWeight.setVisibility(View.GONE);
        btnReject.setText("Cleaning....");


    }

    /**
     * Display only rinsing status
     */
    private void displayUIForRinsing() {

        trSnf.setVisibility(View.GONE);
        trFat.setVisibility(View.GONE);
        trAmount.setVisibility(View.GONE);
        tvRate.setVisibility(View.GONE);
        etRate.setVisibility(View.GONE);
        trMilkWeight.setVisibility(View.GONE);
        tvMilkWeight.setVisibility(View.GONE);
        btnReject.setText("Rinsing.....");
    }

    /**
     * Display quality parameters and quantity
     */
    private void displayUIForSample() {
        trMilkWeight.setVisibility(View.VISIBLE);
        trAmount.setVisibility(View.GONE);
        tvRate.setVisibility(View.GONE);
        etRate.setVisibility(View.GONE);
        etMilkweight.setVisibility(View.VISIBLE);
        tvMilkWeight.setVisibility(View.VISIBLE);
        btnReject.setText("OK");
    }

    /**
     * Display quality,quantity and rate parameters
     */
    private void displayUIForCollectionCheck() {
        btnReject.setText("Back");
        btnReject.setEnabled(true);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnReject:
                onSubmit();
                break;
            default:
                break;
        }

    }

    private void registerMaListener() {
        if (maManager != null)
            maManager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopMaReading();
                            if (maEntity != null) {
                                milkAnalyserEntity = maEntity;
                                if (!mSmartCCUtil.validateQualityParameters(maEntity)) {
                                    Util.displayErrorToast("Invalid data, Press F10 and reset the MA", SampleCollectionActivity.this);
                                    onFinish();
                                    return;
                                }
                                mReportEntity.setQualityParameters(maEntity);
                                displayQualityItem();
                                afterGettingMaData();

                            } else {
                                Util.displayErrorToast("Invalid data, Press F10 and reset the MA", SampleCollectionActivity.this);
                            }
                        }
                    });
                }

                @Override
                public void onOtherMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            stopMaReading();

                            if (mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK
                                    || mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE) {
                                onFinish();
                            } else {
                                if (!mReportEntity.isQualityDone()) {
                                    mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUALITY_DONE, true);
                                }
                                setMAData(message);
                            }


                        }
                    });
                }
            });
    }

    private void registerWsListener() {

        if (wsManager != null)
            wsManager.setOnNewDataListener(new WsManager.OnNewDataListener() {
                @Override
                public void onNewData(final double data) {
                    //added opening of WS connection in reject scenario so that it will be read and ignored which will avoid displaying
                    // old data in next collection. This is done because wisens module stores the old data in its buffer.
                    if (mReportEntity.status != null && mReportEntity.status.equalsIgnoreCase("ACCEPT")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                double weight = 0;
                                try {
                                    weight = data;

                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                if (weight > 0 && mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.AUTO)) {
                                    if (mReportEntity.getTippingStartTime() == 0) {
                                        mReportEntity.setTippingStartTime(System.currentTimeMillis());
                                    }
                                    displayQuantity(data);
                                }
                            }
                        });
                    } else {
                        Log.v(PROBER, "Milk is rejected, hence ignoring fetched WS data");
                    }
                }
            });
    }

    public void stopMaReading() {
        if (maManager != null) {
            maManager.stopReading();
        }
    }

    public void closeWSConnection() {
        if (wsManager != null) {
            wsManager.closeConnection();
            wsManager = null;
        }
    }

    public void onFinish() {

        startActivity(new Intent(SampleCollectionActivity.this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

    private void displayQualityItem() {
        etFat.setText(String.valueOf(mReportEntity.getDisplayFat()));
        etSnf.setText(String.valueOf(mReportEntity.getDisplaySnf()));
        etClr.setText(String.valueOf(mReportEntity.getDisplayClr()));
        etProtein.setText(String.valueOf(mReportEntity.getDisplayProtein()));

    }

    public void afterGettingMaData() {

        if (mReportEntity.getQualityMode().equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            milkAnalyserEntity = new MilkAnalyserEntity(etFat, etSnf, etClr, etProtein, null, null);
            mReportEntity.setQualityParameters(milkAnalyserEntity);
        }

        setQualityManual(false);
        if (!(mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING ||
                mReportEntity.getSampleType() == SAMPLE_TYPE_RINSING)) {
            saveIncompleteRecord();
        }

        //Allow collection without quantity also
        if (mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK) {
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
        }

        if (mReportEntity.getSampleType() == SAMPLE_TYPE_WATER ||
                mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING ||
                mReportEntity.getSampleType() == SAMPLE_TYPE_RINSING) {
            mReportEntity.setStatus("SUCCESS");
            btnReject.requestFocus();
        } else {
            evaluateAcceptOrReject();
            if (mReportEntity.getStatus().equalsIgnoreCase("Reject")
                    && amcuConfig.getEditableRate() && !btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
                alertReject();
            } else if (mReportEntity.getStatus().equalsIgnoreCase("Reject")) {
                onMilkReject();
                disableViews();
                btnReject.requestFocus();
            } else {
                setQuantityEnable();
                onMilkAccept();
                if (etMilkweight.getVisibility() != View.VISIBLE) {
                    btnReject.requestFocus();
                }

                try {
                    openWsConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }


    }

    private void onMilkReject() {
        //added opening of WS connection in reject scenario so that it will be read and ignored which will avoid displaying
        // old data in next collection. This is done because wisens module stores the old data in its buffer.
        openWsConnection();
        Toast.makeText(SampleCollectionActivity.this,
                "Milk rejected for this fat and snf!",
                Toast.LENGTH_LONG).show();
        btnReject.setText("Reject");
        btnReject.setEnabled(true);
        btnReject.requestFocus();
        onRejectMilk();
    }

    private void onRejectMilk() {
        etRate.setText("0.00");
        etMilkweight.setText("0.00");
        etAmount.setText("0.00");
        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
    }

    private void onMilkAccept() {


        if (!btnReject.getText().toString().equalsIgnoreCase("Enter rate") &&
                (mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK ||
                        mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE)) {
            mReportEntity = mSmartCCUtil.getRateFromRateChart(mReportEntity);
            displayRate();
        }
        btnReject.setText("Print");

    }

    private void displayRate() {

        etRate.setText(String.valueOf(mReportEntity.getDisplayRate()));
        etProteinRate.setText(String.valueOf(mReportEntity.getIncentiveRate()));

    }

    public void setQuantityEnable() {
        if (amcuConfig.getWsManual() &&
                (mReportEntity.getStatus() != null &&
                        !mReportEntity.getStatus().equalsIgnoreCase(SmartCCConstants.REJECT))) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (amcuConfig.getWsManual()) {
                etMilkweight.setFocusable(true);
                etMilkweight.setEnabled(true);
                setCursorOnText(etMilkweight);
            } else {
                if (!btnReject.hasFocus()) {
                    btnReject.requestFocus();
                }
            }


            Util.alphabetValidation(etMilkweight, Util.ONLY_DECIMAL, SampleCollectionActivity.this, 8);
            etMilkweight.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    if (((event.getAction() == KeyEvent.ACTION_UP) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                            (event.getAction() == KeyEvent.ACTION_UP &&
                                    (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {

                        onSubmit();
                        return false;
                    } else if (Util.isDigitKey(keyCode, event)) {
                        addQuantityWatcher();
                        return false;
                    } else {
                        return false;
                    }
                }
            });


        } else if (etMilkweight.isEnabled()) {
            etMilkweight.setFocusable(false);
            etMilkweight.setEnabled(false);
        }
    }

    private void evaluateAcceptOrReject() {
        if (btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
            setQuantityEnable();
            return;
        }
        if (mReportEntity.collectionStatus == 0 && amcuConfig.getAllowMaxLimitFromRateChart()) {
            mReportEntity = mSmartCCUtil.setMaxFatAndSnf(mReportEntity);
        }
        if (mReportEntity.getFat() > Util.MAX_FAT_LIMIT ||
                mReportEntity.getSnf() > Util.MAX_SNF_LIMIT
                || mReportEntity.getFat() < Util.MIN_FAT_LIMIT
                || mReportEntity.getSnf() < Util.MIN_SNF_LIMIT) {
            Toast.makeText(SampleCollectionActivity.this,
                    "Invalid milk analyzer data", Toast.LENGTH_SHORT).show();
            onFinish();

        } else {
            mReportEntity = mSmartCCUtil.setAcceptOrRejectStatus(mReportEntity);

        }
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
        if (!amcuConfig.getHotspotValue()) {
            statusLayout.setVisibility(View.GONE);
        } else {
            for (String deviceName : configuredDevices) {
                switch (deviceName) {
                    case DeviceName.MILK_ANALYSER:
                        cbMa.setVisibility(View.VISIBLE);
                        break;
                    case DeviceName.WS:
                        cbWs.setVisibility(View.VISIBLE);
                        break;
                    case DeviceName.RDU:
                        cbRdu.setVisibility(View.VISIBLE);
                        break;
                    case DeviceName.PRINTER:
                        cbPrinter.setVisibility(View.VISIBLE);
                        break;

                }
            }
        }
        cbMa.setChecked(amcuConfig.getMaPingValue());
        cbWs.setChecked(amcuConfig.getWsPingValue());
        cbRdu.setChecked(amcuConfig.getRduPingValue());
        cbPrinter.setChecked(amcuConfig.getPrinterPingValue());
    }

    public void setMAData(String maData) {

        if (maData == null) {
            btnReject.setText("OK");
            mReportEntity.setStatus("FAILURE");
        } else if (maData.equalsIgnoreCase("CLN")
                && mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING) {
            btnReject.setText("Cleaning Done");
            mReportEntity.setStatus("SUCCESS");

        } else if (maData.equalsIgnoreCase("CLN")
                && mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING) {
            btnReject.setText("Cleaning Failed, Please Try Again!");
            mReportEntity.setStatus("FAILURE");

        } else if (maData.equalsIgnoreCase("RIN")
                && mReportEntity.getSampleType() == SAMPLE_TYPE_RINSING) {
            btnReject.setText("Rinsing Done");
            mReportEntity.setStatus("SUCCESS");
        } else if (maData.equalsIgnoreCase("RIN")
                && mReportEntity.getSampleType() == SAMPLE_TYPE_RINSING) {
            btnReject.setText("Rinsing Failed, Please Try Again!");
            mReportEntity.setStatus("SUCCESS");
        } else if (maData.equalsIgnoreCase("CLFL")) {
            btnReject.setText("Cleaning Failed, Please Try Again!");
            mReportEntity.setStatus("FAILURE");

        } else if (maData.equalsIgnoreCase("Timeout")) {
            btnReject.setText("Time Out, Please Try Again!");
            mReportEntity.setStatus("FAILURE");

        }
        btnReject.requestFocus();
        btnReject.setFocusable(true);
        btnReject.setEnabled(true);

    }

    private void onSubmit() {

        if (etFat.getText().toString().trim().length() < 1 &&
                !mReportEntity.isQualityDone() &&
                mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK) {
            onFinish();
        } else if (mReportEntity.isQualityDone() && (
                mReportEntity.getSampleType() == SAMPLE_TYPE_RINSING
                        || mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING)) {
            milkAnalyserEntity = new MilkAnalyserEntity();
            onSubmitActions();
        } else if (((!mReportEntity.isQuantityDone() &&
                mReportEntity.isQualityDone() &&
                milkAnalyserEntity.isValid() &&
                validateQualityFields() && mReportEntity.status == null)
                || (btnReject.getText().toString().equalsIgnoreCase("Enter rate")))
                && validateQualityFields()) {
            afterGettingMaData();
            setQualityManual(false);
            btnReject.setText(getResources().getString(R.string.submit));
        } else if (onSubmitValidation() ||
                (mReportEntity.status != null &&
                        mReportEntity.status.equalsIgnoreCase(SmartCCConstants.REJECT))) {
            if (checkAlertForMaxWeight()) {
                mCollectionHelper.showAlertForManualWeight(CollectionHelper.FROM_SAMPLE_ACTIIVITY);
            } else {
                onSubmitActions();
            }
        }
    }

    private void onSubmitActions() {
        updateReportEntityOnQuantitySubmit();

        try {
            addToDatabase();
            mSmartCCUtil.setCollectionStartData(mReportEntity);
        } catch (SQLException e) {
            e.printStackTrace();
            onDBError();
            Toast.makeText(SampleCollectionActivity.this, "Tab restart is required.",
                    Toast.LENGTH_LONG).show();
            Util.restartTab(SampleCollectionActivity.this);
        }

        printAndDisplay();

        tareWs();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeWSConnection();

        try {
            writeOnSDCard();
        } catch (IOException e) {
            e.printStackTrace();
            Util.restartTab(SampleCollectionActivity.this);
        }
        onFinish();

    }

    private void setQualityManual(boolean isEnable) {
        etFat.setEnabled(isEnable);
        etSnf.setEnabled(isEnable);
        etClr.setEnabled(isEnable);
        etProtein.setEnabled(isEnable);

        if (isEnable) {
            etFat.setOnKeyListener(qualityFieldOnKeyListener);
            etSnf.setOnKeyListener(qualityFieldOnKeyListener);
            etClr.setOnKeyListener(qualityFieldOnKeyListener);
            etProtein.setOnKeyListener(qualityFieldOnKeyListener);
            etFat.requestFocus();

//            onEnterQualityView();
            btnReject.setEnabled(true);
        }
    }

    private void updateReportEntityOnQuantitySubmit() {
        mReportEntity.weighingTime = System.currentTimeMillis();
        mReportEntity.tippingEndTime = System.currentTimeMillis();
        QuantityEntity quantityEntity =
                mCollectionHelper.getQuantityItems(mReportEntity.getRateCalculationQuanity());
        mReportEntity.kgWeight = quantityEntity.kgQuantity;
        mReportEntity.ltrsWeight = quantityEntity.ltrQuanity;

        mReportEntity.fatKg = Util.convertPercentageToKg(mReportEntity.kgWeight, mReportEntity.fat);
        mReportEntity.snfKg = Util.convertPercentageToKg(mReportEntity.kgWeight, mReportEntity.snf);
        mReportEntity.recordCommited = Util.REPORT_COMMITED;
        mReportEntity.resetSentMarkers();
    }

    private void addToDatabase() throws SQLException {
        mReportEntity.setMilkType(new CollectionHelper(SampleCollectionActivity.this).
                getMilkTypeFromConfiguration(mReportEntity));
        mCollectionRecordDao.saveOrUpdate(mReportEntity);
    }

    //TODO return the valid exception, to print and restart the tab
    public void writeOnSDCard() throws IOException {
        mSmartCCUtil.saveReportsOnSdCard(mReportEntity);
    }

    public void printAndDisplay() {

        try {
            displayOnRDU();
        } catch (Exception e) {
            e.printStackTrace();
        }

        printReceipt();


    }

    public void displayOnRDU() {

        RduManager rduManager = RduFactory.getRdu(amcuConfig.getRDU(), SampleCollectionActivity.this);
        if (rduManager != null) {
            rduManager.openConnection();
            rduManager.displayReport(mReportEntity, amcuConfig.getEnableIncentiveRDU());
            rduManager.closePort();
        } else {
            Toast.makeText(SampleCollectionActivity.this,
                    "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
        }

    }

    //This will restart the tab
    public void onDBError() {
        try {
            printReceipt();
            printReceipt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printReceipt() {
        if (mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK ||
                mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE)
            mPrinterManager.print(receiptFormat(), PrinterManager.printReciept, null, null, null);
    }

    public void tareWs() {

        if (wsManager != null) {
            wsManager.tare();
        }
    }

    public String receiptFormat() {
        FormatPrintRecords formatPrintRecord = new FormatPrintRecords(SampleCollectionActivity.this);
        String printData = formatPrintRecord.receiptFormat(SampleCollectionActivity.this, mReportEntity);
        return printData;
    }

    public boolean checkQualityValidation(MilkAnalyserEntity milkAnalyserEntity) {
        if (milkAnalyserEntity == null) {
            return false;
        }
        boolean isValidQuality = mValidationHelper.isValidQualityForSample(milkAnalyserEntity, SampleCollectionActivity.this);
        return isValidQuality;
    }

    public boolean checkValidation(MilkAnalyserEntity milkAnalyserEntity) {
        boolean isValidQuantity = checkQualityValidation(milkAnalyserEntity);

        if (isValidQuantity) {
            if (mReportEntity.getQuantity() >= amcuConfig.getKeyMinValidWeight()) {
                isValidQuantity = true;
            }
        }

        return isValidQuantity;
    }

    private void displayQuantity(double record) {

        mReportEntity.setQuantity(record);
        etMilkweight.setText(String.valueOf(mReportEntity.getDisplayQuantity()));
        displayAmount(mReportEntity.getRateCalculationQuanity());

        if (!mReportEntity.isQuantityDone()) {
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
        }


    }

    private void displayAmount(double record) {

        if (mReportEntity.getSampleType() != SAMPLE_TYPE_RATE_CHECK
                && mReportEntity.getSampleType() != SAMPLE_TYPE_SAMPLE) {
            return;
        }
        mReportEntity.setAmount(mReportEntity.getDisplayRate() * record);
        //Calculating bonus amount

        mReportEntity.setBonus(mReportEntity.getBonusRate() * record);
        mReportEntity.setIncentiveAmount(mReportEntity.getIncentiveRate() * record);

        if (amcuConfig.getKeyAllowProteinValue()) {
            etAmount.setText(String.valueOf(mReportEntity.getTotalAmount()));
        } else {
            etAmount.setText(String.valueOf(Util.getAmount(SampleCollectionActivity.this,
                    mReportEntity.getTotalAmount(), mReportEntity.getBonus())));
        }
    }

    private void addQuantityWatcher() {
        etMilkweight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeChage = etMilkweight.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                afterChange = charSequence.toString();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (wsManager != null) {
                    mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
                    closeWSConnection();
                    if (beforeChage != null && !beforeChage.equalsIgnoreCase("")) {
                        String out = getChangedText(beforeChage, afterChange);
                        if (out != null) {
                            etMilkweight.setText(out);
                        } else {
                            etMilkweight.getText().clear();
                            etMilkweight.setText("");
                        }
                        etMilkweight.setSelection(etMilkweight.getText().toString().length());
                    } else {
                    }

                    btnReject.setText(getResources().getString(R.string.submit));

                }
                if (mReportEntity.getTippingStartTime() == 0) {
                    mReportEntity.setTippingStartTime(System.currentTimeMillis());
                }
                if (mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.AUTO)) {
                    mReportEntity.setQuantityMode(SmartCCConstants.MANUAL);
                }
                mReportEntity.setQuantity(mValidationHelper.getDoubleFromString(etMilkweight.getText().toString().trim(), 0));
                mReportEntity.setQuantity(mReportEntity.getQuantity());
                displayAmount(mValidationHelper.getDoubleFromString(etMilkweight.getText().toString().trim(), 0));

            }
        });
    }

    private void addQualityWatcher(View v) {
        EditText etInput = (EditText) v;
        if (!mReportEntity.qualityMode.equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            mReportEntity.qualityMode = SmartCCConstants.MANUAL;
            stopMaReading();
            btnReject.setText(getResources().getString(R.string.read_quantity));
        }

        if (!mReportEntity.isQualityDone()) {
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUALITY_DONE, true);
        }
        if (milkAnalyserEntity == null) {
            milkAnalyserEntity = new MilkAnalyserEntity();
        }

        if (etInput == etRate) {
            double rate = mValidationHelper.getDoubleFromString(etRate.getText().toString().trim(), 0);
            mReportEntity.setRate(rate);
            mReportEntity.setRate(mReportEntity.getDisplayRate());
            if (mReportEntity.getRateMode().equalsIgnoreCase(SmartCCConstants.AUTO)) {
                mReportEntity.setRateMode(SmartCCConstants.MANUAL);
            }
            return;
        }


        if (etInput == etFat) {
            milkAnalyserEntity.fat =
                    mValidationHelper.getDoubleFromString(etFat.getText().toString().trim(), -1);
        }
        if (etInput == etClr) {
            milkAnalyserEntity.clr =
                    mValidationHelper.getDoubleFromString(etClr.getText().toString().trim(), -1);

        }
        if (etInput == etSnf) {
            milkAnalyserEntity.snf = mValidationHelper.getDoubleFromString(
                    etSnf.getText().toString().trim(), -1);
        }

        if (etSnf.getVisibility() == View.GONE || !etSnf.isEnabled()) {
            milkAnalyserEntity.snf = Util.getSNF(milkAnalyserEntity.fat, milkAnalyserEntity.clr);
            etSnf.setText(String.valueOf(
                    Util.getSNF(milkAnalyserEntity.fat, milkAnalyserEntity.clr)));
        }

        if (etClr.getVisibility() == View.GONE || !etClr.isEnabled()) {
            milkAnalyserEntity.clr = Util.getCLR(milkAnalyserEntity.fat, milkAnalyserEntity.snf);
            etClr.setText(String.valueOf(
                    Util.getCLR(milkAnalyserEntity.fat, milkAnalyserEntity.snf)));
        }


    }

    private void setUserDetails() {
        etFarmerId.setText(mSession.getFarmerID());
        etFarmerName.setText(mSession.getFarmerName());
        etSId.setText("");
        etFarmerName.setEnabled(false);
        etFarmerId.setEnabled(false);
        etSId.setEnabled(false);

    }

    private boolean onSubmitValidation() {
        boolean isValid = false;
        if (mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE) {
            if (mReportEntity.isQuantityDone() && checkValidation(milkAnalyserEntity)) {
                isValid = true;
            }
        } else if (mReportEntity.getSampleType() == SAMPLE_TYPE_RINSING ||
                mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING) {
            if (mReportEntity.isQualityDone()) {
                isValid = true;
            }
        } else if (mReportEntity.getSampleType() == SAMPLE_TYPE_WATER) {
            if (checkQualityValidation(milkAnalyserEntity)) {
                isValid = true;
            }
        } else if (mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK) {
            if (checkQualityValidation(milkAnalyserEntity) && mReportEntity.isQuantityDone()) {
                isValid = true;
            }
        }

        return isValid;
    }


    /**
     * Based on configuration show visibility for FAT/SNF
     */
    private void setViewForSnfAndClr() {


        String chilingCenterFSC = amcuConfig.getChillingFATSNFCLR();
        String collectionCenterFSC = amcuConfig.getCollectionFATSNFCLR();

        boolean allView = amcuConfig.getFATSNFCLR();
        if (allView) {
            tvSnfAuto.setVisibility(View.VISIBLE);
            txtCLR.setVisibility(View.VISIBLE);
            etSnf.setVisibility(View.VISIBLE);
            etClr.setVisibility(View.VISIBLE);
            if ((chilingCenterFSC.equalsIgnoreCase("FS")
                    && mSession.getIsChillingCenter()) ||
                    (collectionCenterFSC.equalsIgnoreCase("FS")
                            && !mSession.getIsChillingCenter())) {
                etClr.setEnabled(false);
            } else {
                etSnf.setEnabled(false);
            }
        } else {
            if (mSession.getIsChillingCenter()) {

                if (chilingCenterFSC.equalsIgnoreCase("FS")) {
                    showFatSnf();
                } else {
                    showFatClr();
                }
            } else {

                if (collectionCenterFSC.equalsIgnoreCase("FS")) {
                    showFatSnf();
                } else {
                    showFatClr();
                }
            }
        }


        if (amcuConfig.getKeyAllowProteinValue() && !mSession.getIsChillingCenter()) {
            trProtein.setVisibility(View.VISIBLE);
        } else {
            trProtein.setVisibility(View.GONE);
            etProtein.setVisibility(View.GONE);
        }
        txtSID.setVisibility(View.GONE);
        etSId.setVisibility(View.GONE);
    }


    public void showFatSnf() {
        tvSnfAuto.setVisibility(View.VISIBLE);
        etSnf.setVisibility(View.VISIBLE);
        etClr.setVisibility(View.GONE);
        txtCLR.setVisibility(View.GONE);
    }

    public void showFatClr() {
        tvSnfAuto.setVisibility(View.GONE);
        txtCLR.setVisibility(View.VISIBLE);
        etSnf.setVisibility(View.GONE);
        etClr.setVisibility(View.VISIBLE);
    }

    public void setHeader() {
        tvheader.setText(mSession.getSocietyName());
    }

    @Override
    public void onBackPressed() {
        if (amcuConfig.getKeyEscapeEnableCollection() ||
                mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK) {
            DatabaseHandler.getDatabaseInstance().deleteFromDb();
            onFinish();
        }
    }

    /**
     * Disable quantity, rate amount view as default
     */
    private void disableViews() {
        etMilkweight.setEnabled(false);
        etRate.setEnabled(false);
        etAmount.setEnabled(false);
    }

    public void saveIncompleteRecord() {
        //TODO populate report entity

        if (milkAnalyserEntity == null) {
            return;
        }

        if (mReportEntity.isQualityDone() && !milkAnalyserEntity.isValid()
                && ((mReportEntity.getSampleType() == SAMPLE_TYPE_RATE_CHECK ||
                mReportEntity.getSampleType() == SAMPLE_TYPE_SAMPLE))) {
            Util.displayErrorToast("Invalid data, please retry again!", SampleCollectionActivity.this);
            onFinish();
            return;
        }

        if (mReportEntity.isUncommittedRecordSaved()) {
            return;
        }
        try {
            long columnId = mCollectionRecordDao.saveOrUpdate(mReportEntity);
            mReportEntity.setPrimaryKeyId(columnId);
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.SAVED_UNCOMMITTED_RECORD, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validateQualityFields() {
        boolean returnValue = true;

        if (mReportEntity.getSampleType() == SAMPLE_TYPE_RINSING
                || mReportEntity.getSampleType() == SAMPLE_TYPE_CLEANING) {
            return returnValue;
        }

        if (TextUtils.isEmpty(etFat.getText().toString().trim())) {

            Util.displayErrorToast("Please enter valid Fat value!", SampleCollectionActivity.this);
            returnValue = false;
        } else if (TextUtils.isEmpty(etSnf.getText().toString().trim())
                && etSnf.getVisibility() == View.VISIBLE) {

            Util.displayErrorToast("Please enter valid SNF value!", SampleCollectionActivity.this);
            returnValue = false;
        } else if (TextUtils.isEmpty(etClr.getText().toString().trim())
                && etClr.getVisibility() == View.VISIBLE) {

            Util.displayErrorToast("Please enter valid CLR value!", SampleCollectionActivity.this);
            returnValue = false;
        }
        return returnValue;

    }

    @Override
    public boolean exceedWeightLimit() {
        etMilkweight.requestFocus();
        etMilkweight.setSelection(etMilkweight.getText().toString().length());
        return false;
    }

    @Override
    public boolean proceedWithExceedWeightLimit() {
        onSubmitActions();
        return false;
    }


    public boolean checkAlertForMaxWeight() {
        if (mReportEntity.getQuantityMode().equalsIgnoreCase("Manual")
                && mReportEntity.getQuantity() >= amcuConfig.getMaxlimitOfWeight()) {
            return true;
        }
        return false;
    }

    public void alertReject() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                SampleCollectionActivity.this);

        alertDialogBuilder.setTitle("Milk rejected alert!");

        alertDialogBuilder
                .setMessage("Press 'Reject' to reject the milk sample OR press" +
                        " 'Accept' to edit the rate and proceed with collection.")
                .setCancelable(false);


        alertDialogBuilder.setPositiveButton("Accept",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Allow edit the rate and set milk as accepted milk
                        etRate.setEnabled(true);
                        etRate.setFocusable(true);
                        etRate.requestFocus();
                        etRate.setSelection(etRate.getText().toString().length());
                        mReportEntity.setStatus(SmartCCConstants.ACCEPT);
                        etRate.setOnKeyListener(qualityFieldOnKeyListener);
                        btnReject.setText("Enter rate");
                        btnReject.setEnabled(true);
                        dialog.dismiss();


                    }
                });
        alertDialogBuilder.setNegativeButton("Reject",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Proceed with milk rejection.
                        btnReject.setText("Reject");
                        mReportEntity.setStatus("Reject");
                        onSubmit();
                        dialog.dismiss();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void setQuantity() {
        if (amcuConfig.getKeyRateChartInKg() || amcuConfig.getMyRateChartEnable()) {
            tvMilkWeight.setText("Qty(kgs)");
        } else {
            tvMilkWeight.setText("Qty(ltrs)");
        }

    }

    private void setCursorOnText(final EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {

                editText.setCursorVisible(true);
                editText.setSelection(editText.length());
                editText.requestFocus();
            }
        });
    }

    private String getChangedText(String beforeChange, String afterChange) {

        char[] beforeChangeArray = beforeChange.toCharArray();
        char[] afterChangeArray = afterChange.toCharArray();

        if (afterChangeArray.length <= beforeChange.length()) {
            return null;
        } else {
            for (int j = 0; j < beforeChangeArray.length; j++) {
                if (afterChangeArray[j] != beforeChangeArray[j]) {
                    return String.valueOf(afterChangeArray[j]);
                }
            }
            return String.valueOf(afterChangeArray[afterChangeArray.length - 1]);
        }

    }

    @Override
    protected void onStop() {
        stopAllConnections();
        super.onStop();
    }


    private void stopAllConnections() {
        stopMaReading();
        closeWSConnection();
        storeLastPingValues();
    }

    private void storeLastPingValues() {
        amcuConfig.setMaPingValue(maPing);
        amcuConfig.setWsPingValue(wsPing);
        amcuConfig.setRduPingValue(rduPing);
        amcuConfig.setPrinterPingValue(printerPing);
    }

}
