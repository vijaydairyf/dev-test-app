package com.devapp.devmain.macollection;

/**
 * Created by u_pendra on 5/6/18.
 */

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.eevoskos.robotoviews.widget.RobotoButton;
import com.eevoskos.robotoviews.widget.RobotoEditText;
import com.eevoskos.robotoviews.widget.RobotoTextView;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.WeightLimit;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
import com.devapp.devmain.main.BaseActivity;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.rdu.RduFactory;
import com.devapp.devmain.rdu.RduManager;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.user.WifiUtility;
import com.devapp.devmain.util.RegexTextWatcher;
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;


public class CollectionActivity extends BaseActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener, WeightLimit {

    private static CollectionActivity mActivityInstance = null;
    AlertDialog alertForFilledAndEmptyCan;
    AlertDialog alertForRemoveCanAndTare;
    String radioStatus;
    TextView tvPreviousData, tvNewData, tvTotal, textNote;

    LinkedHashMap<String, String> spinnerHashMapData;
    String spinnerItem;
    QuantityEntity quantityEntity = new QuantityEntity();
    private String beforeChage = null, afterChange = null;
    private Context context = this;
    private String TAG = "MILK_COLLECTION";
    private RelativeLayout header;
    private RobotoTextView tvheader, tvFarmerid, txtSID, tvName,
            tvFatAuto, tvSnfAuto, txtCLR, tvProtein, tvProteinRate, tvMilkWeight, tvRate, tvAmount, tvQualityOfMilk;
    private RobotoButton btnAutoManual, btnNext, btnReject, btnBack;
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
    private CollectionRecordDao mCollectionRecordDao;
    private SmartCCUtil mSmartCCUtil;
    private ValidationHelper mValidationHelper;
    private ReportEntity mReportEntity;
    private boolean
            isFilledOrEmptyCanStarted = false;
    private MilkAnalyserEntity milkAnalyserEntity;
    private double tempLiterData, tempKgData;
    private ChooseDecimalFormat mChooseDecimalFormat;
    private boolean maPing, wsPing, rduPing, printerPing;
    private CollectionHelper mCollectionHelper;
    View.OnKeyListener qualityFieldOnKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (((event.getAction() == KeyEvent.ACTION_UP) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                    (event.getAction() == KeyEvent.ACTION_DOWN &&
                            (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {

                if (v == etFat) {
                    if (etSnf.getVisibility() == View.VISIBLE && etSnf.isEnabled()) {
                        setCursorOnText(etSnf);

                    } else if (etClr.getVisibility() == View.VISIBLE) {
                        setCursorOnText(etClr);
                    }
                    return false;
                } else if (v == etSnf) {
                    if (etClr.getVisibility() == View.VISIBLE && etClr.isEnabled()) {
                        setCursorOnText(etClr);
                    } else if (etProtein.getVisibility() == View.VISIBLE && etProtein.isEnabled()) {
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


            //For any numeric entry
            if (Util.isDigitKey(keyCode, event)) {
                addQualityWatcher(v);
                return false;
            } else {
                return false;
            }
        }

    };
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

    public static CollectionActivity getInstance() {

        return mActivityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityInstance = CollectionActivity.this;
        setContentView(R.layout.allusbdevice_landscape);
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
        registerMaListener();
        registerWsListener();
        setStatusLayout();
        setUserDetails();
        setHeader();
        disableViews();
        setQuantity();
        if (!mReportEntity.isUncommittedRecordSaved()) {
            startMaReading();
            setQualityManual(amcuConfig.getMaManual());
        } else {
            setQuantityEnable();
            displayQualityItem();
            displayRate();
            // displayQuantity(mReportEntity.getQuantity());
            // openWsConnection();
            setQualityManual(false);
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.SAVED_UNCOMMITTED_RECORD, true);
            afterGettingMaData();
        }
        setViewForSnfAndClr();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mSession.getIsChillingCenter()) {
            spinnerQualityOfMilk.setVisibility(View.VISIBLE);
            tvQualityOfMilk.setVisibility(View.VISIBLE);
        }
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
        btnBack = (RobotoButton) findViewById(R.id.btnBack);


        etFat.addTextChangedListener(new RegexTextWatcher(etFat, AppConstants.Regex.NUMBER_DECIMAL));
        etSnf.addTextChangedListener(new RegexTextWatcher(etSnf, AppConstants.Regex.NUMBER_DECIMAL));
        etClr.addTextChangedListener(new RegexTextWatcher(etClr, AppConstants.Regex.NUMBER_DECIMAL));
        btnAutoManual.setOnClickListener(CollectionActivity.this);
        btnNext.setOnClickListener(this);
        btnReject.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnBack.setVisibility(View.VISIBLE);
        spinnerQualityOfMilk.setOnItemSelectedListener(this);

        btnReject.setText(getResources().getString(R.string.submit));
        addingSpinnerData(-1);
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
        mUIValidation = UIValidation.getInstance();
        mCollectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        //TODO initialize only if required
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        mSmartCCUtil = new SmartCCUtil(CollectionActivity.this);
        mValidationHelper = new ValidationHelper();

        maManager = MAFactory.getMA(DeviceName.MILK_ANALYSER, CollectionActivity.this);
        wsManager = WsFactory.getWs(DeviceName.WS, CollectionActivity.this);

        mCollectionHelper = new CollectionHelper(CollectionActivity.this);
        mChooseDecimalFormat = new ChooseDecimalFormat();

    }

    public void initializeReportEntity() {

        this.mReportEntity = mCollectionRecordDao.findByDateShiftAndCommitStatus(
                SmartCCUtil.getDateInPostFormat(), mSmartCCUtil.getFullShift(Util.getCurrentShift()),
                CollectionConstants.UNSENT);
        if (this.mReportEntity == null) {
            this.mReportEntity = new ReportEntity();
            mReportEntity.user = mSession.getUserId();
            mReportEntity.farmerId = mSession.getFarmerID();
            mReportEntity.farmerName = mSession.getFarmerName();
//            mReportEntity.agentId = mSmartCCUtil.getAgentId();
            mReportEntity.socId = mSession.getCollectionID();
            mReportEntity.postDate = mSmartCCUtil.getReportFormatDate();
            mReportEntity.postShift = SmartCCUtil.getShiftInPostFormat(CollectionActivity.this);
            mReportEntity.centerRoute = Util.getRouteFromChillingCenter(CollectionActivity.this, mSession.getFarmerID());
            mReportEntity.rateChartName = amcuConfig.getRateChartName();

            //TODO check the usage
            mReportEntity.lDate = Util.getDateInLongFormat(mReportEntity.postDate);
            mReportEntity.time = Util.getTodayDateAndTime(3, CollectionActivity.this, true);

            mReportEntity.miliTime = mCalendar.getTimeInMillis();
            //TODO remove txnumber
            mReportEntity.txnNumber = mSession.getTXNumber() + 1;

            if (mSession.getIsChillingCenter()) {
                mReportEntity.collectionType = Util.REPORT_TYPE_MCC;
            } else {
                mReportEntity.collectionType = Util.REPORT_TYPE_FARMER;
            }
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
            mSession.setTXNumber(mReportEntity.txnNumber + 1);

            mReportEntity.setOverallCollectionStatus(0, false);
            mReportEntity.setMilkType(mSession.getMilkType());

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

    }

    public void startMaReading() {
        if (maManager != null)
            maManager.startReading();

    }

    private void openWsConnection() {
        if (wsManager != null)
            wsManager.openConnection();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnNext: {
                onSubmitNextCan(mReportEntity.getQuantity());
                break;
            }
            case R.id.btnReject: {
                onSubmit();
                break;
            }


            case R.id.btnBack: {
                stopMaReading();
                onFinish();

                break;
            }
            default: {
            }
        }
    }

    private void onSubmit() {
        if (!validateQualityFields()) {

        } else if (!mReportEntity.isQuantityDone() && mReportEntity.getRateMode().equalsIgnoreCase("Manual")
                && mReportEntity.getDisplayRate() > 0) {
            btnReject.setText("Submit");
            afterGettingMaData();
        } else if ((etFat.isEnabled() ||
                mReportEntity.qualityMode.equalsIgnoreCase("Manual"))
                && !mReportEntity.isQualityDone() && (milkAnalyserEntity != null && milkAnalyserEntity.isValid())) {
            btnReject.setText("Submit");
            afterGettingMaData();
        } else if (((!mReportEntity.isQuantityDone() &&
                mReportEntity.isQualityDone() &&
                milkAnalyserEntity.isValid()
                && mReportEntity.rateMode.equalsIgnoreCase(SmartCCConstants.AUTO)) ||
                (btnReject.getText().toString().equalsIgnoreCase("Enter rate"))) && validateQualityFields()) {
            btnReject.setText(getResources().getString(R.string.submit));
            afterGettingMaData();
            setQualityManual(false);

        } else if ((mReportEntity.isQuantityDone() &&
                checkQuantityValidation(milkAnalyserEntity)) ||
                (mReportEntity.status != null
                        && mReportEntity.status.equalsIgnoreCase(SmartCCConstants.REJECT))) {
            if (checkAlertForMaxWeight()) {
                mCollectionHelper.showAlertForManualWeight(CollectionHelper.FROM_MILKCOLLECTION);
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
            Toast.makeText(CollectionActivity.this, "Tab restart is required.",
                    Toast.LENGTH_LONG).show();
            Util.restartTab(CollectionActivity.this);
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
            Util.restartTab(CollectionActivity.this);
        }
        onFinish();

    }

    public void onSubmitNextCan(double dWeightRecord) {
        if (mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            return;
        }
        if (!mReportEntity.isMultipleCanStarted()
                && mValidationHelper.validMilkWeight(dWeightRecord, CollectionActivity.this)) {
            //If multiple can collection not yet started
            alertForRemoveCan();
        } else if (mReportEntity.isMultipleCanStarted() && dWeightRecord > 0) {
            alertForRemoveCan();
        } else {
            Toast.makeText(CollectionActivity.this,
                    "No valid weight, or enter weight manually", Toast.LENGTH_SHORT).show();
        }
    }

    /*
      Alert if next can feature in enable
     */
    public void alertForRemoveCan() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CollectionActivity.this);
        // set title
        alertDialogBuilder.setTitle("Reset weighing scale!");
        // set dialog message
        alertDialogBuilder
                .setMessage("Please remove the can and press OK to tare the weighing scale")
                .setCancelable(false);


        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isFilledOrEmptyCanStarted = true;

                        try {
                            tareWs();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mReportEntity.setOverallCollectionStatus(SmartCCConstants.NEXT_CAN_STARTED, true);
                        alertForNextCan(mReportEntity.getQuantity());
                        dialog.dismiss();
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });


        // create alert dialog
        alertForRemoveCanAndTare = alertDialogBuilder.create();
        alertForRemoveCanAndTare.setCancelable(false);
        // show it
        alertForRemoveCanAndTare.show();
    }

    public void alertForNextCan(double actualData) {

        AlertDialog.Builder builder = new AlertDialog.Builder(CollectionActivity.this);
        LayoutInflater inflater = CollectionActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.milkstate_dialogbox, null);
        builder.setView(dialogView);

        RadioButton radioButtonEmpty = (RadioButton) dialogView.findViewById(R.id.radioBtnempty);
        RadioButton radioButtonFilled = (RadioButton) dialogView.findViewById(R.id.radioBtnfilled);
        final Button btnAdd = (Button) dialogView.findViewById(R.id.btnAdd);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

        tvPreviousData = (TextView) dialogView.findViewById(R.id.tvprevious_data);
        tvNewData = (TextView) dialogView.findViewById(R.id.tvnew_vol_data);
        tvTotal = (TextView) dialogView.findViewById(R.id.tvtotal_vol_data);
        textNote = (TextView) dialogView.findViewById(R.id.textNote);

        tvPreviousData.setText(String.valueOf(actualData));
        tvTotal.setText(String.valueOf(actualData));


        if (amcuConfig.getAllowVisiblityForCanToggle() && amcuConfig.getCansToggling()) {
            radioStatus = amcuConfig.getLastAlertToggleState();
        } else {
            radioStatus = "filled";
        }


        RadioGroup radioMilkStateGroup = (RadioGroup) dialogView.findViewById(R.id.radio_milk_state);
        if (radioStatus.equalsIgnoreCase("filled")) {
            radioButtonFilled.setChecked(true);
            tvNewData.setText("(+) 00.00");
        } else {
            radioButtonEmpty.setChecked(true);
            tvNewData.setText("(-) 00.00");
        }

        radioMilkStateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioBtnfilled:
                        tvNewData.setText("(+) 00.00");
                        radioStatus = "filled";
                        //  saveSession.setLastAlertToggleState(radioStatus);
                        break;
                    case R.id.radioBtnempty:
                        tvNewData.setText("(-) 00.00");
                        radioStatus = "empty";
                        //  saveSession.setLastAlertToggleState(radioStatus);
                        break;

                }
            }
        });


        btnAdd.requestFocus();

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioStatus.equalsIgnoreCase("empty") && (
                        mReportEntity.getQuantity() < mReportEntity.getNewCanRecord())) {
                    Util.displayErrorToast("Invalid can weight", CollectionActivity.this);
                } else if (mValidationHelper.validMilkWeight(mReportEntity.getNewCanRecord(), CollectionActivity.this)) {
                    etMilkweight.setText(String.valueOf(tvTotal.getText().toString()));
                    mReportEntity.setQuantity(Double.parseDouble(etMilkweight.getText().toString().trim()));
                    mReportEntity.setLtrsWeight(tempLiterData);
                    mReportEntity.setKgWeight(tempKgData);
                    displayAmount(mReportEntity.getRateCalculationQuanity());
                    mReportEntity.setNewCanRecord(0);
                    mReportEntity.setNewCanKgData(0);
                    mReportEntity.setNewCanLiterData(0);
                    if (radioStatus.equalsIgnoreCase("filled")) {
                        amcuConfig.setLastAlertToggleState("empty");
                    } else {
                        amcuConfig.setLastAlertToggleState("filled");
                    }
                    alertForFilledAndEmptyCan.dismiss();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mReportEntity.setNewCanRecord(0);
                mReportEntity.setNewCanLiterData(0);
                mReportEntity.setNewCanKgData(0);
                alertForFilledAndEmptyCan.dismiss();

            }
        });

        builder.setInverseBackgroundForced(true);
        builder.setCancelable(false);
        alertForFilledAndEmptyCan = builder.create();
        alertForFilledAndEmptyCan.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertForFilledAndEmptyCan.show();
        Window window = alertForFilledAndEmptyCan.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void addToDatabase() throws SQLException {
        //TODO need to remove this dependency
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

        RduManager rduManager = RduFactory.getRdu(amcuConfig.getRDU(), CollectionActivity.this);
        if (rduManager != null) {
            rduManager.openConnection();
            rduManager.displayReport(mReportEntity, amcuConfig.getEnableIncentiveRDU());
            rduManager.closePort();
        } else {
            Toast.makeText(CollectionActivity.this,
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
        mPrinterManager.print(receiptFormat(), PrinterManager.printReciept, null, null, null);
    }

    public void tareWs() {

        if (wsManager != null) {
            wsManager.tare();
        }
    }

    public String receiptFormat() {
        FormatPrintRecords formatPrintRecord = new FormatPrintRecords(CollectionActivity.this);
        String printData = formatPrintRecord.receiptFormat(CollectionActivity.this, mReportEntity);
        return printData;
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
                                    Util.displayErrorToast("Invalid data, Press F10 and reset the MA", CollectionActivity.this);
                                    onFinish();
                                    return;
                                }
                                mReportEntity.setQualityParameters(maEntity);
                                displayQualityItem();
                                if (!etFat.isEnabled()) {
                                    afterGettingMaData();
                                } else {
                                    btnReject.setText("Submit quality");
                                    btnReject.requestFocus();
                                }

                            } else {
                                Util.displayErrorToast("Invalid data, Press F10 and reset the MA", CollectionActivity.this);
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

                            if (!mSession.getIsSample()) {
                                onFinish();
                            } else {
                                //TODO Handle this condition
                                // enableReject(message);
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
                    if (mReportEntity.status.equalsIgnoreCase("Accept")) {
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

    private void displayQuantity(double record) {

        quantityEntity = mCollectionHelper.getQuantityItems(record);
        if (alertForRemoveCanAndTare != null && alertForRemoveCanAndTare.isShowing()) {
            //In this case just read the weight but not write
//            setAlertText(record);
        } else if (alertForFilledAndEmptyCan != null && alertForFilledAndEmptyCan.isShowing()) {
            //In this case read the weight , and display only on filled and empty can alert screen
            updateCurrentCanAlert(quantityEntity.displayQuantity);

        } else if (isFilledOrEmptyCanStarted) {
            //In this case just read the weight but not writeetZeroAlertText
            // prevRecord = record;
        } else {
            //Write the weight on main screen
            mReportEntity.setQuantity(quantityEntity.displayQuantity);
            mReportEntity.setKgWeight(quantityEntity.kgQuantity);
            mReportEntity.setLtrsWeight(quantityEntity.ltrQuanity);
            etMilkweight.setText(String.valueOf(mReportEntity.getDisplayQuantity()));
            displayAmount(mReportEntity.getRateCalculationQuanity());
            if (!btnReject.hasFocus() && !btnNext.hasFocus()
                    && !spinnerQualityOfMilk.hasFocus()
                    && !mReportEntity.isQuantityDone()) {
                btnReject.requestFocus();
            }
            if (!mReportEntity.isQuantityDone()) {
                mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
            }


        }

    }

    private void displayAmount(double record) {

        mReportEntity.setAmount(mReportEntity.getDisplayRate() * record);
        //Calculating bonus amount

        mReportEntity.setBonus(mReportEntity.getBonusRate() * record);
        mReportEntity.setIncentiveAmount(mReportEntity.getIncentiveRate() * record);

        if (amcuConfig.getKeyAllowProteinValue()) {
            etAmount.setText(String.valueOf(mReportEntity.getTotalAmount()));
        } else {
            etAmount.setText(String.valueOf(Util.getAmount(CollectionActivity.this,
                    mReportEntity.getTotalAmount(), mReportEntity.getBonus())));
        }
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

        startActivity(new Intent(CollectionActivity.this, FarmerScannerActivity.class));
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

    private void onMilkAccept() {

        if (!btnReject.getText().toString().equalsIgnoreCase("Enter rate")
                && mReportEntity.getRateMode().equalsIgnoreCase("Auto")) {
            mReportEntity = mSmartCCUtil.getRateFromRateChart(mReportEntity);
            displayRate();
        } else {
            etRate.setEnabled(false);
        }
        setMilkWeighingState();
        btnReject.setText("Print");

    }

    private void displayRate() {

        etRate.setText(String.valueOf(mReportEntity.getDisplayRate()));
        etProteinRate.setText(String.valueOf(mReportEntity.getIncentiveRate()));

    }

    public void setQuantityEnable() {
        if (amcuConfig.getWsManual()
                && !mReportEntity.getStatus().equalsIgnoreCase(SmartCCConstants.REJECT)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            etMilkweight.setFocusable(true);
            etMilkweight.setEnabled(true);
            setCursorOnText(etMilkweight);

            Util.alphabetValidation(etMilkweight, Util.ONLY_DECIMAL, CollectionActivity.this, 8);
            etMilkweight.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {


                    if (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER) {

                        if (event.getAction() == KeyEvent.ACTION_UP) {
                            etMilkweight.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_UP, KeyEvent.KEYCODE_ENTER, 0));
                        } else {
                            etMilkweight.dispatchKeyEvent(new KeyEvent(0, 0,
                                    KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER, 0));
                        }
                        return true;
                    }


                    if (((event.getAction() == KeyEvent.ACTION_UP) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER))) {

                        onSubmit();
                        return false;
                    } else if (Util.isDigitKey(keyCode, event)) {
                        //  closeWSConnection();
                        addQuantityWatcher(keyCode);
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

    public void setMilkWeighingState() {
        if (amcuConfig.getEnableFilledOrEmptyCans() && mSession.getIsChillingCenter()) {
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setText("Next weight");
        }
    }

    private void onMilkReject() {
        //added opening of WS connection in reject scenario so that it will be read and ignored which will avoid displaying
        // old data in next collection. This is done because wisens module stores the old data in its buffer.
        openWsConnection();
        Toast.makeText(CollectionActivity.this,
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
        etProteinRate.setText("0.00");
        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
    }


    private void evaluateAcceptOrReject() {

        if (mReportEntity.getRateMode().equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            mReportEntity.setStatus(SmartCCConstants.ACCEPT);
            return;
        }
        if (mReportEntity.collectionStatus == 0 && amcuConfig.getAllowMaxLimitFromRateChart()) {
            mReportEntity = mSmartCCUtil.setMaxFatAndSnf(mReportEntity);
        }
        if (mReportEntity.getFat() > Util.MAX_FAT_LIMIT ||
                mReportEntity.getSnf() > Util.MAX_SNF_LIMIT
                || mReportEntity.getFat() < Util.MIN_FAT_LIMIT
                || mReportEntity.getSnf() < Util.MIN_SNF_LIMIT) {
            Toast.makeText(CollectionActivity.this, "Invalid milk analyzer data", Toast.LENGTH_SHORT).show();
            onFinish();

        } else {
            mReportEntity = mSmartCCUtil.setAcceptOrRejectStatus(mReportEntity);

        }

    }

    public void saveIncompleteRecord() {
        //TODO populate report entity

        if (milkAnalyserEntity == null) {
            return;
        }

        if (mReportEntity.isQualityDone() && !milkAnalyserEntity.isValid()) {
            Util.displayErrorToast("Invalid data, please retry again!", CollectionActivity.this);
            onFinish();
            return;
        }

        if (mReportEntity.isUncommittedRecordSaved()) {
            return;
        }
        mReportEntity.recordCommited = Util.REPORT_NOT_COMMITED;
        if (mSession.getIsChillingCenter()) {
            mReportEntity.milkQuality = spinnerItem;
        } else {
            mReportEntity.milkQuality = "NA";
        }
        try {
            long columnId = mCollectionRecordDao.saveOrUpdate(mReportEntity);
            mReportEntity.setPrimaryKeyId(columnId);
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.SAVED_UNCOMMITTED_RECORD, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean checkQualityValidation(MilkAnalyserEntity milkAnalyserEntity) {
        boolean isValidQuality = mValidationHelper.isValidQuality(milkAnalyserEntity, CollectionActivity.this);
        return isValidQuality;
    }

    public boolean checkQuantityValidation(MilkAnalyserEntity milkAnalyserEntity) {

        boolean isValidData = checkQualityValidation(milkAnalyserEntity);

        if (isValidData) {
            if (mReportEntity.getQuantity() > amcuConfig.getKeyMinValidWeight()) {
                if (mReportEntity.quantityMode.equalsIgnoreCase(SmartCCConstants.AUTO)
                        && !isFilledOrEmptyCanStarted && mReportEntity.getQuantity() >
                        amcuConfig.getMaxlimitOfWeight()) {
                    isValidData = false;
                    Util.displayErrorToast("Weight should be less than " + amcuConfig.getMaxlimitOfWeight()
                            , CollectionActivity.this);
                } else if (isFilledOrEmptyCanStarted || mReportEntity.quantityMode.equalsIgnoreCase(SmartCCConstants.MANUAL)) {
                    isValidData = true;
                }
            } else {
                if (amcuConfig.getWsManual()) {
                    etMilkweight.requestFocus();
                }
                Util.displayErrorToast("Invalid weight "
                        , CollectionActivity.this);
                return false;
            }
        }
        return isValidData;
    }

    public void updateCurrentCanAlert(double record) {
        tvPreviousData.setText(String.valueOf(mReportEntity.getQuantity()));
        double minLimit, currentRecord, maxLimit;
        //TODO: Record should be actual record based on the calculation
        /* record = mReportEntity.getQuantity();*/
        currentRecord = record;
        mReportEntity.setNewCanRecord(record);
        mReportEntity.setNewCanLiterData(quantityEntity.kgQuantity);
        mReportEntity.setNewCanKgData(quantityEntity.ltrQuanity);

        if (radioStatus.equalsIgnoreCase("filled")) {
            tvNewData.setText("(+) " + record);
            tvTotal.setText(mChooseDecimalFormat.getWeightDecimalFormat().
                    format(Double.parseDouble(tvPreviousData.getText().toString())
                            + record));
            tempKgData = Double.valueOf(mChooseDecimalFormat.getWeightDecimalFormat()
                    .format(quantityEntity.kgQuantity + mReportEntity.kgWeight));
            tempLiterData = Double.valueOf(mChooseDecimalFormat.getWeightDecimalFormat()
                    .format(quantityEntity.ltrQuanity + mReportEntity.ltrsWeight));

            if (amcuConfig.getAllowVisiblityForCanToggle() && amcuConfig.getCansToggling()) {
                minLimit = Double.parseDouble(String.valueOf(amcuConfig.getMinLimitFilledCans()));
                if (currentRecord < minLimit)
                    textNote.setVisibility(View.VISIBLE);
                else
                    textNote.setVisibility(View.GONE);
            }
        } else {
            tvNewData.setText("(-) " + record);
            if (Double.parseDouble(tvPreviousData.getText().toString()) <= currentRecord) {
                textNote.setVisibility(View.VISIBLE);
            } else {
                tvTotal.setText(mChooseDecimalFormat.
                        getWeightDecimalFormat().format(Double.parseDouble(tvPreviousData.getText().toString())
                        - record));
                tempKgData = Double.valueOf(mChooseDecimalFormat.getWeightDecimalFormat()
                        .format(mReportEntity.kgWeight - quantityEntity.kgQuantity));
                tempLiterData = Double.valueOf(mChooseDecimalFormat.getWeightDecimalFormat()
                        .format(mReportEntity.getLtrsWeight() - quantityEntity.ltrQuanity));
                if (amcuConfig.getAllowVisiblityForCanToggle() && amcuConfig.getCansToggling()) {
                    maxLimit = Double.parseDouble(String.valueOf(amcuConfig.getMaxLimitEmptyCans()));
                    if (currentRecord > maxLimit)
                        textNote.setVisibility(View.VISIBLE);
                    else
                        textNote.setVisibility(View.GONE);
                }
            }
        }
    }


    private void updateReportEntityOnQuantitySubmit() {

        Util.setCollectionStartedWithMilkType(mReportEntity.milkType, CollectionActivity.this);
        mReportEntity.weighingTime = System.currentTimeMillis();
        mReportEntity.tippingEndTime = System.currentTimeMillis();

        if (mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            QuantityEntity quantityEntity =
                    mCollectionHelper.getQuantityItems(
                            mReportEntity.getRateCalculationQuanity());
            mReportEntity.kgWeight = quantityEntity.kgQuantity;
            mReportEntity.ltrsWeight = quantityEntity.ltrQuanity;
        }


        mReportEntity.fatKg = Util.convertPercentageToKg(mReportEntity.kgWeight, mReportEntity.fat);
        mReportEntity.snfKg = Util.convertPercentageToKg(mReportEntity.kgWeight, mReportEntity.snf);
        mReportEntity.recordCommited = Util.REPORT_COMMITED;

        if (mSession.getIsChillingCenter()) {
            mReportEntity.setNumberOfCans(1);
        } else {
            mReportEntity.setNumberOfCans(0);
        }

        mReportEntity.setMilkType(mCollectionHelper.getMilkTypeFromConfiguration(mReportEntity));
        mReportEntity.resetSentMarkers();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        spinnerItem = parent.getItemAtPosition(position).toString();
        Iterator myVeryOwnIterator = spinnerHashMapData.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            if (spinnerHashMapData.get(key).toString().equals(spinnerItem)) {
                spinnerItem = "" + key;
            }
        }
        // Toast.makeText(parent.getContext(), "Key::" + position + "Selected: " + spinnerItem, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void alertReject() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                CollectionActivity.this);

        alertDialogBuilder.setTitle("Milk reject alert!");

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
                        etRate.setOnKeyListener(qualityFieldOnKeyListener);
                        btnReject.setText("Enter rate");
                        btnReject.setEnabled(true);
                        mReportEntity.setStatus(SmartCCConstants.ACCEPT);
                        mReportEntity.setRateMode(SmartCCConstants.MANUAL);
                        addRateWatcher();
                        dialog.dismiss();


                    }
                });
        alertDialogBuilder.setNegativeButton("Reject",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Proceed with milk rejection.
                        btnReject.setText("Reject");
                        mReportEntity.setStatus(SmartCCConstants.REJECT);
                        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
                        onSubmit();
                        dialog.dismiss();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    /**
     * For customer milma, for FAT 0 allow user to select milk quality type
     *
     * @param fatValue
     */
    public void addingSpinnerData(double fatValue) {
        spinnerHashMapData = new LinkedHashMap<String, String>();
        if (fatValue == 0 && mSession.getIsChillingCenter()) {
            spinnerHashMapData.put("CS", "Curdled by society (CS)");
            spinnerHashMapData.put("CT", "Curdled by Transporter (CT)");
            spinnerHashMapData.put("SS", "Sub standard (SS)");
        } else {
            spinnerHashMapData.put("G", "Good quality (G)");
        }
        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();

        Iterator myVeryOwnIterator = spinnerHashMapData.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            categories.add(spinnerHashMapData.get(key));
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQualityOfMilk.setAdapter(dataAdapter);
    }

    private void addQualityWatcher(View v) {
        EditText etInput = (EditText) v;
        if (!mReportEntity.qualityMode.equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            mReportEntity.qualityMode = SmartCCConstants.MANUAL;
            stopMaReading();
            btnReject.setText(getResources().getString(R.string.read_quantity));
        }

        /*if (!mReportEntity.isQualityDone()) {
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUALITY_DONE, true);
        }*/
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
            addingSpinnerData(milkAnalyserEntity.fat);
        }
        if (etInput == etClr) {
            milkAnalyserEntity.clr =
                    mValidationHelper.getDoubleFromString(etClr.getText().toString().trim(), -1);

        }
        if (etInput == etProtein) {
            milkAnalyserEntity.protein = mValidationHelper.getDoubleFromString(
                    etProtein.getText().toString().trim(), -1);
            return;
        }
        if (etInput == etSnf) {
            milkAnalyserEntity.snf = mValidationHelper.getDoubleFromString(
                    etSnf.getText().toString().trim(), -1);
        }

        if (etSnf.getVisibility() == View.GONE || (!etSnf.isEnabled())) {
            milkAnalyserEntity.snf = Util.getSNF(milkAnalyserEntity.fat, milkAnalyserEntity.clr);
            etSnf.setText(String.valueOf(
                    Util.getSNF(milkAnalyserEntity.fat, milkAnalyserEntity.clr)));
        }

        if (etClr.getVisibility() == View.GONE || (!etClr.isEnabled())) {
            milkAnalyserEntity.clr = Util.getCLR(milkAnalyserEntity.fat, milkAnalyserEntity.snf);
            etClr.setText(String.valueOf(
                    Util.getCLR(milkAnalyserEntity.fat, milkAnalyserEntity.snf)));
        }


    }


    public void afterGettingMaData() {

        if (mReportEntity.getQualityMode().equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            milkAnalyserEntity = new MilkAnalyserEntity(etFat, etSnf, etClr, etProtein, null, null);
            mReportEntity.setQualityParameters(milkAnalyserEntity);
        }
        if (!mReportEntity.isQualityDone()) {
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUALITY_DONE, true);
        }
        setQualityManual(false);
        evaluateAcceptOrReject();
        saveIncompleteRecord();
        setQuantityEnable();

        if ((mReportEntity.getStatus().equalsIgnoreCase("Reject")
                && amcuConfig.getEditableRate()) &&
                !btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
            alertReject();
        } else if (mReportEntity.getStatus().equalsIgnoreCase("Reject")) {
            onMilkReject();
            disableViews();
            btnReject.requestFocus();
        } else {
            onMilkAccept();
            try {
                openWsConnection();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnBack.setVisibility(View.GONE);
    }

    private void addQuantityWatcher(final int keyCode) {

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
                    Log.d("Close connection: " + "WS", beforeChage + " " + afterChange);
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
                    Util.displayErrorToast("Enter quantity manually", CollectionActivity.this);

                }

                if (mReportEntity.getTippingStartTime() == 0) {
                    mReportEntity.setTippingStartTime(System.currentTimeMillis());
                }
                if (mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.AUTO)) {
                    mReportEntity.setQuantityMode(SmartCCConstants.MANUAL);
                }
                mReportEntity.setQuantity(mValidationHelper.getDoubleFromString(etMilkweight.getText().toString().trim(), 0));
                mReportEntity.setQuantity(mReportEntity.getQuantity());
                displayAmount(mReportEntity.getRateCalculationQuanity());

            }
        });
    }

    private void addRateWatcher() {
       /* etRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (mReportEntity.getRateMode().equalsIgnoreCase(SmartCCConstants.AUTO)) {
                    mReportEntity.setRateMode(SmartCCConstants.MANUAL);
                    btnReject.setText(getResources().getString(R.string.submit));
                }

                mReportEntity.setRate(mValidationHelper.getDoubleFromString(etRate.getText().toString().trim(), 0));

            }
        });*/
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


    private void setRateMode(boolean isEnable) {
        etRate.setEnabled(isEnable);
        if (isEnable) {
            addRateWatcher();
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

    /**
     * Based on configuration show visibility for FAT/SNF
     */

    private void setViewForSnfAndClr() {

        btnReject.setText(getResources().getString(R.string.submit));
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

    private void showFatSnf() {
        tvSnfAuto.setVisibility(View.VISIBLE);
        etSnf.setVisibility(View.VISIBLE);
        etClr.setVisibility(View.GONE);
        txtCLR.setVisibility(View.GONE);
    }

    private void showFatClr() {
        tvSnfAuto.setVisibility(View.GONE);
        txtCLR.setVisibility(View.VISIBLE);
        etSnf.setVisibility(View.GONE);
        etClr.setVisibility(View.VISIBLE);
    }

    private void setHeader() {
        tvheader.setText(mSession.getSocietyName());
    }

    /**
     * Disable quantity, rate amount view as default
     */
    private void disableViews() {
        etMilkweight.setEnabled(false);
        etRate.setEnabled(false);
        etAmount.setEnabled(false);
        etProteinRate.setEnabled(false);
    }

    private boolean validateQualityFields() {
        boolean returnValue = true;
        if (TextUtils.isEmpty(etFat.getText().toString().trim())) {

            Util.displayErrorToast("Please enter valid Fat value!", CollectionActivity.this);
            returnValue = false;
        } else if (TextUtils.isEmpty(etSnf.getText().toString().trim()) &&
                etSnf.getVisibility() == View.VISIBLE) {

            Util.displayErrorToast("Please enter valid SNF value!", CollectionActivity.this);
            returnValue = false;
        } else if (TextUtils.isEmpty(etClr.getText().toString().trim())
                && etClr.getVisibility() == View.VISIBLE) {

            Util.displayErrorToast("Please enter valid CLR value!", CollectionActivity.this);
            returnValue = false;
        } else if (etProtein.getVisibility() == View.VISIBLE &&
                TextUtils.isEmpty(etProtein.getText().toString().trim())) {
            Util.displayErrorToast("Please enter valid Protein value!", CollectionActivity.this);
            returnValue = false;
        } else if (btnReject.getText().toString().equalsIgnoreCase("Enter rate") && mReportEntity.rate <= 0) {
            Util.displayErrorToast("Please enter valid Rate!", CollectionActivity.this);
            etRate.setEnabled(true);
            etRate.requestFocus();
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
                && mReportEntity.getQuantity() >= amcuConfig.getMaxlimitOfWeight() && !isFilledOrEmptyCanStarted) {
            return true;
        }
        return false;
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
//            case KeyEvent.KEYCODE_DEL: {
//                if (AmcuConfig.getInstance().getKeyEscapeEnableCollection()
//                        && !new SessionManager(this).getIsChillingCenter()) {
//                    DatabaseHandler.getDatabaseInstance().deleteFromDb();
//                    onFinish();
//                }
//                return true;
//            }

            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnReject.requestFocus();
                return true;
            }

        }

        return super.onKeyUp(keyCode, event);
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