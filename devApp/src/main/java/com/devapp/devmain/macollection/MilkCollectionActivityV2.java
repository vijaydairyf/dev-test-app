package com.devapp.devmain.macollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DeviceName;
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
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.user.WifiUtility;
import com.devapp.devmain.util.UIValidation;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.devmain.util.logger.Log;
import com.devapp.devmain.ws.WsFactory;
import com.devapp.devmain.ws.WsManager;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static com.devapp.devmain.entity.ConfigurationConstants.isWsManual;
import static com.devapp.devmain.user.Util.ping;
import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;


public class MilkCollectionActivityV2 extends BaseActivity implements View.OnClickListener {

    public double newRcordFilledCan = 0, prevRecord;
    AlertDialog alertForFilledAndEmptyCan;
    AlertDialog alertForRemoveCanAndTare;
    String radioStatus;
    TextView tvPreviousData, tvNewData, tvTotal, textNote;
    boolean printEnabled = true;
    private String TAG = "MILK_COLLECTION";
    private Activity currentActivity = null;
    private ArrayList<DeviceEntity> allDeviceData = SmartCCConstants.devicesList;
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
    private CollectionRecordDao getmCollectionRecordDao;
    private Calendar mCalendar;
    private DriverConfiguration mDriverConfiguration;
    private CollectionHelper mParallerFunction;
    private CollectionRecordDao mCollectionRecordDao;
    private SmartCCUtil mSmartCCUtil;
    private ValidationHelper mValidationHelper;
    private ReportEntity mReportEntity, unCommittedRecord;
    private boolean waitingForMAData = true, ignoreInitialWSData = true,
            isFilledOrEmptyCanStarted = false, isFatSnfPressed = false;
    private MilkAnalyserEntity milkAnalyserEntity;
    private boolean maPing, wsPing, rduPing, printerPing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = MilkCollectionActivityV2.this;
        setContentView(R.layout.allusbdevice_landscape);
        findByViews();


    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUtility();
        initializeReportEntity();
        registerMaListener();
        registerWsListener();
        if (mReportEntity.getCollectionStatus() == 0) {
            startMAConnection();
        } else {
            displayQualityItem();
            startWsConnection();
        }
    }

    public void findByViews() {

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
        trProtein = (TableRow) findViewById(R.id.trProtein);
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

        btnAutoManual.setOnClickListener(MilkCollectionActivityV2.this);
        btnNext.setOnClickListener(this);
        btnReject.setOnClickListener(this);
    }

    private void initializeUtility() {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        amcuConfig = AmcuConfig.getInstance();
        mSession = new SessionManager(currentActivity);
        mWifiUtility = new WifiUtility();

        mPrinterManager = new PrinterManager(currentActivity);
        mPingHandler = new Handler();

        mUIValidation = UIValidation.getInstance();
        mCollectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));


        mSmartCCUtil = new SmartCCUtil(MilkCollectionActivityV2.this);
        mDriverConfiguration = new DriverConfiguration();
        mParallerFunction = new CollectionHelper(MilkCollectionActivityV2.this);

        maManager = MAFactory.getMA(DeviceName.MILK_ANALYSER, MilkCollectionActivityV2.this);
        wsManager = WsFactory.getWs(DeviceName.WS, MilkCollectionActivityV2.this);


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
            mReportEntity.socId = mSession.getCollectionID();
            mReportEntity.postDate = mSmartCCUtil.getReportFormatDate();
            mReportEntity.lDate = Util.getDateInLongFormat(mReportEntity.postDate);
            mReportEntity.time = Util.getTodayDateAndTime(3, MilkCollectionActivityV2.this, true);
            mReportEntity.miliTime = mCalendar.getTimeInMillis();
            mReportEntity.txnNumber = mSession.getTXNumber() + 1;
        } else {
            mReportEntity.setCollectionStatus(1);
        }

    }


    public void startMAConnection() {
        if (maManager != null)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    maManager.startReading();
                }
            }).start();
    }

    private void startWsConnection() {
        if (wsManager != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    wsManager.openConnection();
                }
            }).start();
        }
    }


    public void setMaEnable() {
        if (amcuConfig.getMaManual()) {
            etFat.setEnabled(true);
            etFat.setFocusable(true);
            etFat.setFocusableInTouchMode(true);
            etFat.requestFocus();
            etFat.requestFocusFromTouch();
            etFat.setCursorVisible(true);
            etFat.setSelection(0);
            etProtein.setEnabled(true);
            etProteinRate.setEnabled(false);

            if (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") &&
                    mSession.getIsChillingCenter()) {
                etSnf.setEnabled(false);
                etSnf.setFocusable(false);
                etClr.setEnabled(true);
                etClr.setFocusable(true);

            } else if (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") &&
                    !mSession.getIsChillingCenter()) {
                etSnf.setEnabled(false);
                etSnf.setFocusable(false);
                etClr.setEnabled(true);
                etClr.setFocusable(true);

            } else {
                etSnf.setEnabled(true);
                etSnf.setFocusable(true);
                etClr.setEnabled(false);
                etClr.setFocusable(false);
            }

            ifMaManual();
        }
    }


    public void ifMaManual() {
        etFat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {


                if (checkQualityValidation(milkAnalyserEntity) && allDeviceData.size() < 2) {
                    mReportEntity.qualityMode = "Manual";
                    waitingForMAData = false;
                    closeMAConnection();
                    btnReject.setEnabled(true);

                    if (!mSession.getIsSample() ||
                            Util.checkIfSampleCode(mSession.getFarmerID(),
                                    amcuConfig.getFarmerIdDigit())) {
                        btnReject.setText("Read weight");
                    } else {
                        btnReject.setText("OK");
                    }
                }
            }
        });

        etSnf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (checkQualityValidation(milkAnalyserEntity) && allDeviceData.size() < 2) {
                    mReportEntity.qualityMode = "Manual";
                    waitingForMAData = false;
                    closeMAConnection();
                    btnReject.setEnabled(true);

                    if (!mSession.getIsSample() || Util.checkIfSampleCode(mSession.getFarmerID(),
                            amcuConfig.getFarmerIdDigit())) {
                        btnReject.setText("Read weight");
                    } else {
                        btnReject.setText("OK");
                    }
                }

            }
        });

        etClr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (checkQualityValidation(milkAnalyserEntity) && allDeviceData.size() < 2) {
                    mReportEntity.qualityMode = "Manual";
                    waitingForMAData = false;
                    closeMAConnection();
                    btnReject.setEnabled(true);

                    if (!mSession.getIsSample() ||
                            Util.checkIfSampleCode(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                        btnReject.setText("Read weight");
                    } else {
                        btnReject.setText("OK");
                    }
                }

            }
        });


        etFat.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    if (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") &&
                            mSession.getIsChillingCenter()) {
                        etClr.requestFocus();
                        int position = etClr.length();
                        etClr.setCursorVisible(true);
                        etClr.setSelection(position);
                    } else if (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") &&
                            !mSession.getIsChillingCenter()) {
                        etClr.requestFocus();
                        int position = etClr.length();
                        etClr.setCursorVisible(true);
                        etClr.setSelection(position);

                    } else {
                        etSnf.requestFocus();
                        int position = etSnf.length();
                        etSnf.setCursorVisible(true);
                        etSnf.setSelection(position);
                    }

                    if ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                        isFatSnfPressed = true;
                    }
                    return true;
                }
                return false;
            }
        });


        etSnf.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    if (amcuConfig.getKeyAllowProteinValue()) {
                        etProtein.requestFocus();
                        int position = etProtein.length();
                        etProtein.setCursorVisible(true);
                        etProtein.setSelection(position);

                    } else {
                        if (btnReject.isEnabled()) {
                            onClickButtonReject();
                        }

                    }
                    if ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                        isFatSnfPressed = true;
                    }
                    return true;

                }
                return false;
            }
        });

        etProtein.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {

                    if (btnReject.isEnabled()) {
                        onClickButtonReject();
                    }
                    if ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                        isFatSnfPressed = true;
                    }
                    return true;
                }
                return false;
            }
        });


        etClr.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {

                    if (amcuConfig.getKeyAllowProteinValue()) {
                        etProtein.requestFocus();
                        int position = etProtein.length();
                        etProtein.setCursorVisible(true);
                        etProtein.setSelection(position);

                    } else {
                        if (btnReject.isEnabled()) {
                            onClickButtonReject();
                        }

                    }
                    if ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                        isFatSnfPressed = true;
                    }
                    return true;


                }
                return false;
            }
        });
    }


    // focus changes to get weight
    public void setWsEnable() {
        if (amcuConfig.getWsManual() && !mReportEntity.getStatus().equalsIgnoreCase("Reject")) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            etMilkweight.setFocusable(true);
            etMilkweight.setEnabled(true);
            etMilkweight.setCursorVisible(true);
            etMilkweight.setSelection(0);
            etMilkweight.requestFocus();
            Util.alphabetValidation(etMilkweight, Util.ONLY_DECIMAL, MilkCollectionActivityV2.this, 8);
            textChangeFunction();

        } else if (etMilkweight.isEnabled()) {
            etMilkweight.setFocusable(false);
            etMilkweight.setEnabled(false);
        }
    }


    public boolean checkQualityValidation(MilkAnalyserEntity milkAnalyserEntity) {
        boolean isValidQuality = mValidationHelper.isValidQuality(milkAnalyserEntity, MilkCollectionActivityV2.this);
        return isValidQuality;
    }

    public boolean checkQuantityValidation(MilkAnalyserEntity milkAnalyserEntity) {
        boolean isValidQuantity = checkQualityValidation(milkAnalyserEntity);

        if (isValidQuantity) {
            if (mReportEntity.getQuantity() > 0) {
                if (mReportEntity.quantityMode.equalsIgnoreCase("Auto") && !isFilledOrEmptyCanStarted) {
                    return mValidationHelper.validMilkWeight(mReportEntity.getQuantity(), getApplicationContext());
                } else if (isFilledOrEmptyCanStarted || mReportEntity.quantityMode.equalsIgnoreCase("Manual")) {
                    return true;
                } else {
                    return mValidationHelper.validMilkWeight(mReportEntity.getQuantity(), getApplicationContext());
                }

            } else {
                if (Util.checkIfSampleCode(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                    Util.displayErrorToast("Invalid weight "
                            , MilkCollectionActivityV2.this);
                }
                return false;
            }
        }

        return isValidQuantity;
    }


    public void textChangeFunction() {
        etMilkweight.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (amcuConfig.getEnableFilledOrEmptyCans() &&
                            mSession.getIsChillingCenter() && !mReportEntity.status.equalsIgnoreCase("Reject")) {
                        btnNext.requestFocus();
                    } else if (btnReject.isEnabled()) {
                        onClickButtonReject();
                    }
                    return true;
                }
                return false;
            }
        });

        etMilkweight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkQuantityValidation(milkAnalyserEntity) || mSession.getIsChillingCenter() && checkCurdMilkQuality()) {
                    double qty;
                    String weight = etMilkweight.getText().toString().replace(" ", "");
                    qty = Double.parseDouble(weight);
                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(qty);

                } else {
                    setWeightAndAmountManually(0);
                    if (Util.checkIfSampleCode(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                        btnReject.setEnabled(false);
                    }

                }

            }
        });

        etRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mSession.getIsChillingCenter() && checkCurdMilkQuality()) {
                    String rate = etRate.getText().toString().replace(" ", "");
                    try {
                        mReportEntity.setRate(Double.parseDouble(rate));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(Double.parseDouble(etMilkweight.getText().toString()));
                }
            }
        });

    }


    @Override
    public void onClick(View view) {

    }

    private void openMAConnection() {
        if (maManager != null)
            maManager.startReading();
    }

    public void closeMAConnection() {
        if (maManager != null) {
            maManager.stopReading();
        }
    }

    private boolean checkCurdMilkQuality() {


        if (mReportEntity.getFat() == 0 && (mReportEntity.getSnf() == 0
                || mReportEntity.getClr() == 0)) {
            return true;
        } else {
            return false;
        }
    }


    public void setWeightAndAmountManually(double record) {

    /*    double amt;
        try {
            mReportEntity.setQuantity(record);
            amt = mReportEntity.getRate() * mReportEntity.getRateCalculationQuanity();
            calculateBonusAndIncentive(amt, mReportEntity.getRateCalculationQuanity());
            if (!btnReject.isEnabled()) {
                btnReject.setEnabled(true);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        */
        setAmount(record);

    }


    private void calculateBonusAndIncentive(double amt, double record) {
        if (mSession.getIsSample()
                && !Util.checkIfRateCheck(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            return;
        }
        mReportEntity.setAmount(mReportEntity.getRate() * record);
        //Calculating bonus amount
        double bonusAmt = getBonus() * record;
        mReportEntity.setBonus(bonusAmt);
        mReportEntity.setIncentiveAmount(getInCentiveRate(mReportEntity.getRateCalculationProtein()) * record);

        if (amcuConfig.getKeyAllowProteinValue()) {
            etAmount.setText(String.valueOf(mReportEntity.getTotalAmount()));
        } else {
            etAmount.setText(String.valueOf(Util.getAmount(MilkCollectionActivityV2.this,
                    mReportEntity.getTotalAmount(), mReportEntity.getBonus())));
        }
    }

    public double getBonus() {
        double bonus = 0;
        MilkAnalyserEntity maEntity = mSmartCCUtil.getMAEntity(
                mValidationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0),
                mValidationHelper.getDoubleFromString(etSnf.getText().toString().trim(), 0),
                mValidationHelper.getDoubleFromString(etClr.getText().toString().trim(), 0));
        CollectionHelper collectionHelper = new CollectionHelper(MilkCollectionActivityV2.this);
        bonus = collectionHelper.getBonusAmount(mSession.getFarmerID(), mSession.getMilkType(), maEntity);
        mReportEntity.setBonus(bonus);
        return mReportEntity.getBonus();
    }

    private double getInCentiveRate(double protein) {

        if (mSession.getIsChillingCenter()) {
            etProteinRate.setText("0.00");
            return 0;
        }
        double rate = Double.parseDouble(mDatabaseHandler.getIncentiveRate(String.valueOf(protein),
                amcuConfig.getInCentiveRateChartname()));
        etProteinRate.setText(String.valueOf(rate));
        mReportEntity.setIncentiveRate(rate);
        return mReportEntity.getIncentiveRate();

    }

    public void saveIncompleteRecord() {
        setReportEntity(Util.REPORT_NOT_COMMITED);
        try {
            long columnId = mCollectionRecordDao.saveOrUpdate(mReportEntity);
            mReportEntity.setPrimaryKeyId(columnId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ReportEntity setReportEntity(int committed) {

        //This should be actual value from the milk analyzer
        mReportEntity.fat = milkAnalyserEntity.fat;
        mReportEntity.snf = milkAnalyserEntity.snf;
        mReportEntity.clr = milkAnalyserEntity.clr;
        mReportEntity.protein = milkAnalyserEntity.protein;
        mReportEntity.lactose = milkAnalyserEntity.lactose;
        mReportEntity.awm = milkAnalyserEntity.addedWater;


      /*  setReportFromUI();


        getIncentive(mReportEntity.amount);
        mReportEntity.bonus = bonusAmount;
        if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) {
            mReportEntity.postShift = UMCA_SHIFT;
        } else {
            mReportEntity.postShift = currentShift;
        }

        mReportEntity.milkAnalyserTime = qualityTime;
        mReportEntity.weighingTime = quantityTime;

        if (session.getIsSample()
                || Util.checkIfRateCheck(session.getFarmerID(), mSaveSession.getFarmerIdDigit())) {
            if (isCleaningFailed) {
                mReportEntity.status = "Failure";
            } else {
                mReportEntity.status = "Success";
            }
        } else if (isReject) {
            mReportEntity.status = "Reject";
            Util.setCollectionStartedWithMilkType(mReportEntity.milkType, MilkCollectionActivity.this);
        } else {
            mRemReportEntity.atus = "Accept";
            Util.setCollectionStartedWithMilkType(mReportEntity.milkType, MilkCollectionActivity.this);
        }
        if (isAutoManual != null) {
            mReportEntity.manual = isAutoManual;
            mReportEntity.qualityMode = isMaAutoManual;
        } else {
            mReportEntity.manual = "Manual";
            mReportEntity.qualityMode = isMaAutoManual;
        }
        if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) {
            if (null != weightManualStatus)
                mReportEntity.quantityMode = weightManualStatus;
            else
                mReportEntity.quantityMode = isWsManual;
        } else {
            mReportEntity.quantityMode = isWsManual;
        }

        mReportEntity.recordCommited = committed;

        if (session.getIsSample() ||
                Util.checkIfRateCheck(session.getFarmerID(), saveSession.getFarmerIdDigit())) {
            mReportEntity.collectionType = Util.REPORT_TYPE_SAMPLE;
        } else if (session.getIsChillingCenter()) {
            mReportEntity.collectionType = Util.REPORT_TYPE_MCC;mReportEntity.se {
            String type = databaseHandler.getFarmerType(mReportEntity.farmerId);
            if (type != null && type.equalsIgnoreCase(AppConstants.FARMER_TYPE_FARMER)) {
                mReportEntity.collectionType = Util.REPORT_TYPE_FARMER;
            } else {
                mReportEntity.collectionType = Util.REPORT_TYPE_FARMER;
                //  mReportEntity.collectionType = Util.REPORT_TYPE_AGENT;
            }
        }
        if (session.getIsChillingCenter()) {
            mReportEntity.milkQuality = spinnerItem;
        } else {
            mReportEntity.milkQuality = "NA";
        }
        mReportEntity.rateMode = rateMode;
        mReportEntity.numberOfCans = numberOfCans;
        mReportEntity.centerRoute = Util.getRouteFromChillingCenter(MilkCollectionActivity.this, session.getFarmerID());
//        if (committed == 0) {
//            mReportEntity.recordStatus = Util.RECORD_STATUS_INCOMPLETE;
//        } else {
        mReportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;
        //   }

        mReportEntity.rateChartName = saveSession.getRateChartName();
        ParallelFunction parallelFunction = new ParallelFunction(MilkCollectionActivity.this);
        QuantityEntity quantityEntity =
                parallelFunction.getQuantityFromRealWeight(mReportEntity.quantity);

        if (isWsManual.equalsIgnoreCase("Manual")) {
            kgWeight = quantityEntity.kgQuantity;
            ltrWeight = quantityEntity.ltrQuanity;
        }

        mReportEntity.kgWeight = kgWeight;
        mReportEntity.ltrsWeight = ltrWeight;

        if (committed == 1 && (mReportEntity.quantityMode == null
                || mReportEntity.quantityMode.equalsIgnoreCase("Manual")
                || tippingStartTime == 0)) {
            setTippingTimeForWSManual();
        } else {
            setTippingEndTime();
        }

        mReportEntity.tippingStartTime = tippingStartTime;
        mReportEntity.tippingEndTime = tippingEndTime;
        mReportEntity.agentId = smartCCUtil.getAgentId();
        mReportEntity.milkStatusCode = smartCCUtil.getMilkStatusCode("GOOD");

        if (unCommittedRecord != null) {
            mReportEntity.isRateCalculated = unCommittedRecord.isRateCalculated;
        } else if (mSaveSession.getRateCalculatedFromDevice()) {
            mReportEntity.isRateCalculated = 1;
        } else {
            mReportEntity.isRateCalculated = 0;
        }
        //sequence number resemble sample number to server
        mReportEntity.sampleNumber = 0;

        mReportEntity.serialMa = 1;
        mReportEntity.maName = mSaveSession.getMA();

        mReportEntity.incentiveRate = inCentiveRate;
        mReportEntity.incentiveAmount = incentiveAmount;
        mReportEntity.fatKg = Util.convertPercentageToKg(kgWeight, mReportEntity.fat);
        mReportEntity.snfKg = Util.convertPercentageToKg(kgWeight, mReportEntity.snf);
        mReportEntity.postDate = new SmartCCUtil(MilkCollectionActivityV2.this).getReportFormatDate();
        mReportEntity.postShift = Util.getShift(MilkCollectionActivityV2.this);
        mSmartCCUtil.setCollectionStartData(mReportEntity);*/

        return mReportEntity;
    }


    private void registerMaListener() {
        if (maManager != null)
            maManager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waitingForMAData = false;
                            closeMAConnection();
                            if (maEntity != null) {
                                milkAnalyserEntity = maEntity;
                                if (!mSmartCCUtil.validateQualityParameters(maEntity)) {
                                    Util.displayErrorToast("Invalid data, Press F10 and reset the MA", MilkCollectionActivityV2.this);
                                    onFinish();
                                    return;
                                }
                                mReportEntity.setQualityParameters(maEntity);
                                displayQualityItem();
                                afterGettingMaData();
                            } else {
                                Util.displayErrorToast("Invalid data, Press F10 and reset the MA", MilkCollectionActivityV2.this);
                            }
                        }
                    });
                }

                @Override
                public void onOtherMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeMAConnection();
                            waitingForMAData = false;
                            if (!mSession.getIsSample()) {
                                onFinish();
                            } else {
                                enableReject(message);
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
                                if (weight != 0 && weight > 0) {
                                    displayWeight(data);
                                }
                            }
                        });
                    } else {
                        Log.v(PROBER, "Milk is rejected, hence ignoring fetched WS data");
                    }
                }
            });
    }

    public void displayWeight(double weightRecord) {
        if (alertForRemoveCanAndTare != null && alertForRemoveCanAndTare.isShowing()) {
            //In this case just read the weight but not write
        } else if (alertForFilledAndEmptyCan != null && alertForFilledAndEmptyCan.isShowing()) {
            //In this case read the weight , and display only on filled and empty can alert screen
            setAlertText(weightRecord);

        } else if (isFilledOrEmptyCanStarted) {
            //In this case just read the weight but not write
            prevRecord = weightRecord;
        } else {
            //Write the weight on main screen
            setWeightAndAmount(weightRecord);
        }
    }

    // After getting Milk Analyser data from Auto Mode like reject calculation
    public void afterGettingMaData() {

        evaluateAcceptOrReject();
        setWsEnable();
    }


    private void displayQualityItem() {
        etFat.setText(String.valueOf(mReportEntity.getDisplayFat()));
        etSnf.setText(String.valueOf(mReportEntity.getDisplaySnf()));
        etClr.setText(String.valueOf(mReportEntity.getDisplayClr()));
        etProtein.setText(String.valueOf(mReportEntity.getDisplayProtein()));

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_1:
                checkForManualMAorWS(1);
                return true;
            case KeyEvent.KEYCODE_2:
                checkForManualMAorWS(2);
                return true;
            case KeyEvent.KEYCODE_3:
                checkForManualMAorWS(3);
                return true;
            case KeyEvent.KEYCODE_4:
                checkForManualMAorWS(4);
                return true;
            case KeyEvent.KEYCODE_5:
                checkForManualMAorWS(5);
                return true;
            case KeyEvent.KEYCODE_6:
                checkForManualMAorWS(6);
                return true;
            case KeyEvent.KEYCODE_7:
                checkForManualMAorWS(7);
                return true;
            case KeyEvent.KEYCODE_8:
                checkForManualMAorWS(8);
                return true;
            case KeyEvent.KEYCODE_9:
                checkForManualMAorWS(9);
                return true;
            case KeyEvent.KEYCODE_0:
                checkForManualMAorWS(0);
                return true;
            case KeyEvent.KEYCODE_PERIOD:
                checkForManualMAorWS(0);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DOT:
                checkForManualMAorWS(0);
                return true;

            case KeyEvent.KEYCODE_NUMPAD_0:
                checkForManualMAorWS(0);
                return true;

            case KeyEvent.KEYCODE_NUMPAD_1:
                checkForManualMAorWS(1);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
                checkForManualMAorWS(2);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
                checkForManualMAorWS(3);
                return true;

            case KeyEvent.KEYCODE_NUMPAD_4:
                checkForManualMAorWS(4);
                return true;

            case KeyEvent.KEYCODE_NUMPAD_5:
                checkForManualMAorWS(5);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
                checkForManualMAorWS(6);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
                checkForManualMAorWS(7);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
                checkForManualMAorWS(8);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
                checkForManualMAorWS(9);
                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:

                if ((Util.checkIfRinsing(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())
                        || Util.checkIfCleaning(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())) && btnReject.isEnabled()) {
                    onClickButtonReject();
                } else if (!isFatSnfPressed && checkQualityValidation(milkAnalyserEntity) && btnReject.isEnabled()) {
                    if (amcuConfig.getEnableFilledOrEmptyCans() && mSession.getIsChillingCenter()
                            && mReportEntity.status.equalsIgnoreCase("Accept")) {
                        btnNext.requestFocus();
                    } else {

                        onClickButtonReject();
                    }
                }

                isFatSnfPressed = false;
                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void evaluateAcceptOrReject() {


        if (mReportEntity.collectionStatus == 0 && amcuConfig.getAllowMaxLimitFromRateChart()) {
            mReportEntity = mSmartCCUtil.setMaxFatAndSnf(mReportEntity);
        }
        if (mReportEntity.getFat() > Util.MAX_FAT_LIMIT ||
                mReportEntity.getSnf() > Util.MAX_SNF_LIMIT
                || mReportEntity.getFat() < Util.MIN_FAT_LIMIT
                || mReportEntity.getSnf() < Util.MIN_SNF_LIMIT) {
            Toast.makeText(MilkCollectionActivityV2.this, "Invalid milk analyzer data", Toast.LENGTH_SHORT).show();
            onFinish();

        } else {
            mReportEntity = mSmartCCUtil.setAcceptOrRejectStatus(mReportEntity);
            saveIncompleteRecord();

            if (mReportEntity.status.equalsIgnoreCase("Reject") && amcuConfig.getEditableRate()) {
                alertReject();
            } else if (mReportEntity.status.equalsIgnoreCase("Reject")) {
                //added opening of WS connection in reject scenario so that it will be read and ignored which will avoid displaying
                // old data in next collection. This is done because wisens module stores the old data in its buffer.
                startWsConnection();
                Toast.makeText(MilkCollectionActivityV2.this,
                        "Milk rejected for this fat and snf!",
                        Toast.LENGTH_LONG).show();
                btnReject.setText("Reject");
                btnReject.setEnabled(true);
                btnReject.requestFocus();
            } else {

                if (amcuConfig.getKeyAllowProteinValue()) {
                    getInCentiveRate(mReportEntity.getRateCalculationProtein());
                }
                setMilkWeighingState();
                btnReject.setText("Print");
                try {
                    startWsConnection();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
         /*else {
            enableReject(null);
        }*/

            setWsEnable();
            //Added in 11.1.3
            // stopMaReading();
        }
        return;
    }

    public void setMilkWeighingState() {
        if (amcuConfig.getEnableFilledOrEmptyCans() && mSession.getIsChillingCenter()) {
            btnNext.setVisibility(View.VISIBLE);
            btnNext.setText("Next weight");
        }
    }

    public void onFinish() {

        startActivity(new Intent(MilkCollectionActivityV2.this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }


    public void alertReject() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MilkCollectionActivityV2.this);

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

                        btnReject.setText("Enter rate");
                        btnReject.setEnabled(true);
                        onRateChange();


                        dialog.dismiss();


                    }
                });
        alertDialogBuilder.setNegativeButton("Reject",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Proceed with milk rejection.
                        btnReject.setText("Reject");
                        onClickButtonReject();
                        dialog.dismiss();

                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void onRateChange() {
        etRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                double dEtRate = 0;
                try {
                    dEtRate = Double.parseDouble(etRate.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                if (dEtRate > 0) {

                    mReportEntity.setStatus("Accept");
                    mReportEntity.setRate(dEtRate);
                    double wRecord = 0;

                    try {
                        wRecord = Double.valueOf(etMilkweight.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (wRecord > 0) {
                        setWeightAndAmountManually(Double.parseDouble(etMilkweight.getText().toString().trim()));
                    }
                    btnReject.setText("Rate done");

                } else {
                    mReportEntity.setStatus("Reject");
                    mReportEntity.setRate(0);
                    btnReject.setText("Enter rate");
                }

            }
        });
    }


    private void printReceipt() {
        mPrinterManager.print(receiptFormat(), PrinterManager.printReciept, null, null, null);
    }

    public String receiptFormat() {
        FormatPrintRecords formatPrintRecord = new FormatPrintRecords(MilkCollectionActivityV2.this);
        String printData = formatPrintRecord.receiptFormat(MilkCollectionActivityV2.this, mReportEntity);
        return printData;
    }

    public void onClickButtonReject() {


        if (mSession.getIsChillingCenter() && checkCurdMilkQuality()
                && btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate")) {
            if ((mReportEntity.collectionStatus == 0 &&
                    mReportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC))) {
                etMilkweight.setEnabled(true);
                Toast.makeText(MilkCollectionActivityV2.this, "Please Enter the weight manually", Toast.LENGTH_SHORT).show();
                //}
            }


        } else if (btnReject.getText().toString().equalsIgnoreCase("Enter rate")
                || btnReject.getText().toString().equalsIgnoreCase("Rate done")) {

            if (mReportEntity.status.equalsIgnoreCase("Reject")) {
                alertReject();
            } else {
                //Disable rate to edit once again
                etRate.setEnabled(false);
                etRate.setFocusable(false);
                setMilkWeighingState();
                btnReject.setText("Print");

                //Open the weighingscale connection
                startWsConnection();
                mReportEntity.rateMode = Util.RATE_MODE_MANUAL;
                textChangeFunction();
                setWsEnable();
            }
        } else if (btnReject.getText().toString().equalsIgnoreCase("Read weight")
                || (btnReject.getText().toString().equalsIgnoreCase("Read Rate"))) {

//            TODO pass ReportEntity or MilkAnalyserEntity
            if (checkQualityValidation(null)) {
                setDisable();
                if (Util.checkIfSampleCode(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                    btnReject.setEnabled(false);
                }
                waitingForMAData = false;

                evaluateAcceptOrReject();

                setWsEnable();
            } else {
                Toast.makeText(MilkCollectionActivityV2.this, "Enter valid quality parameters!", Toast.LENGTH_SHORT).show();
            }
        } else {

            if ((mReportEntity.status.equalsIgnoreCase("reject") && checkQualityValidation(milkAnalyserEntity))
                    || (!mReportEntity.status.equalsIgnoreCase("reject") && checkQuantityValidation(milkAnalyserEntity))
                    || Util.checkIfSampleCode(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                if (!checkForManualWeight()) {
                    mParallerFunction.showAlertForManualWeight(CollectionHelper.FROM_MILKCOLLECTION);
                } else {
                    onPrintOrReject();
                }

            } else if ((checkQualityValidation(milkAnalyserEntity) && mSession.getIsSample())) {
                onPrintOrReject();
            } else {
                Toast.makeText(MilkCollectionActivityV2.this, "Enter all valid values!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean checkForManualWeight() {
        if (!mReportEntity.quantityMode.equalsIgnoreCase("Manual")) {
            return true;
        }
        double dQuantity = 0;
        String weight = etMilkweight.getText().toString().trim();
        try {
            dQuantity = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return mValidationHelper.validMilkWeight(dQuantity, getApplicationContext());

    }

    public void setDisable() {
        etFat.setEnabled(false);
        etSnf.setEnabled(false);
        etClr.setEnabled(false);
        etAmount.setEnabled(false);
        etFarmerId.setEnabled(false);
        etFarmerName.setEnabled(false);
        etRate.setEnabled(false);
        etProtein.setEnabled(false);
        etProteinRate.setEnabled(false);
        if (checkQualityValidation(milkAnalyserEntity)) {
            setWsEnable();
        } else {
            etMilkweight.setEnabled(false);
        }

    }

    public void onPrintOrReject() {
        btnReject.setEnabled(false);
        btnReject.setFocusable(false);

//        weighTime = Util.getTodayDateAndTime(6, MilkCollectionActivity.this, true);
        mReportEntity.weighingTime = Calendar.getInstance().getTimeInMillis();

       /*if (Util.checkIfRateCheck(mSession.getFarmerID(), mSaveSession.getFarmerIdDigit())) {

            if (etFat.getText().toString() != null
                    && etFat.getText().toString().replace(" ", "").length() > 0) {
                onAcceptMilk();
            } else {
                tareWSOverSerialManager();
                closeWSConnection();
                onFinish();
            }
        } else */
        if (mReportEntity.status.equalsIgnoreCase("reject")) {
            // Milk rejected scenario;
            onRejectMilk();
        } /*else if (mSession.getIsSample()) {
            if (mSession.getSampleWeigh()) {
                tareWSOverSerialManager();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                closeWSConnection();

            } else {
                stopMaReading();
            }
            addToDatabase();
            onFinish();
        } */ else {
            if (true == printEnabled) {
                printEnabled = false;
                onAcceptMilk();
            }
        }
    }

    public void onRejectMilk() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                addToDatabase();
                closeMAConnection();
                onFinish();
            }
        }).start();
        // Additional functionality like printing can be handled
        // in this handler
    }

    public void onAcceptMilk() {

        String dbError = addToDatabase();
        tareWSOverSerialManager();
        if (dbError != null) {
            Util.displayErrorToast("Db error occurred", MilkCollectionActivityV2.this);
            afterDbError();
        }
        writeOnSDCard();
        printAndDisplay();
        onFinish();

    }

    public void printAndDisplay() {

        try {
            startRDU();
        } catch (Exception e) {
            e.printStackTrace();
        }

        printReceipt();
        tareWSOverSerialManager();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeWSConnection();
    }

    public void startRDU() {

        RduManager rduManager = RduFactory.getRdu(amcuConfig.getRDU(), MilkCollectionActivityV2.this);
        if (rduManager != null) {
            rduManager.openConnection();
            ReportEntity reportEntity = setReportEntity(1);
            rduManager.displayReport(reportEntity, amcuConfig.getEnableIncentiveRDU());
        } else {
            Toast.makeText(MilkCollectionActivityV2.this,
                    "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
        }

    }

    public void closeWSConnection() {
        if (wsManager != null)
            wsManager.closeConnection();
    }

    public void writeOnSDCard() {
        try {
            mSmartCCUtil.saveReportsOnSdCard(mReportEntity);
            Util.displayErrorToast("Write to SDCard ", MilkCollectionActivityV2.this);
        } catch (Exception e) {
            printAndDisplay();
            Util.restartTab(MilkCollectionActivityV2.this);
            e.printStackTrace();
        }
    }

    public void afterDbError() {
        if (Util.checkIfRateCheck(mSession.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || !mSession.getIsSample()) {
            try {

                printReceipt();
                printReceipt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(MilkCollectionActivityV2.this, "Tab restart is required.",
                Toast.LENGTH_LONG).show();
        Util.restartTab(MilkCollectionActivityV2.this);
        //   alertForDatabaseFailure();

    }

    public void tareWSOverSerialManager() {

        if (wsManager != null) {
            wsManager.tare();
        }
    }

    public String addToDatabase() {

        Util.displayErrorToast("Add to database ", MilkCollectionActivityV2.this);
        String dbError = null;

        mReportEntity = setReportEntity(1);
        mReportEntity.resetSentMarkers();
        if (mReportEntity != null) {
            Util.setDailyDateOrShift(Util.getTodayDateAndTime(7, MilkCollectionActivityV2.this, true),
                    mReportEntity.postShift);
            mCollectionRecordDao.saveOrUpdate(mReportEntity);
            mSession.setReportData(null);
            Util.displayErrorToast("Added to database", MilkCollectionActivityV2.this);
        } else {
            dbError = "Error while creating collection record.";
        }

        return dbError;
    }

    public void checkForManualMAorWS(int keyValue) {

        if (checkCurdMilkQuality()
                && !btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate") && !etRate.isEnabled()) {
            btnReject.setText("Enter Manual Rate");
            closeMAConnection();
            waitingForMAData = false;
        } else if (btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
            // no code for this block since we dont want to enter any data to weight text
            onRateChange();
        } else if (amcuConfig.getMaManual() && ((btnReject.getText().toString().equalsIgnoreCase("Read weight")) ||
                waitingForMAData)) {
            Util.alphabetValidation(etFat, Util.ONLY_DECIMAL, MilkCollectionActivityV2.this, 6);
            Util.alphabetValidation(etSnf, Util.ONLY_DECIMAL, MilkCollectionActivityV2.this, 6);
            // Util.alphabetValidation(etClr,Util.ONLY_NUMERIC,MilkCollectionActivity.this,2);
            mReportEntity.qualityMode = "Manual";
            waitingForMAData = false;
            closeMAConnection();
            btnReject.setEnabled(true);
            btnReject.setText("Read weight");

            if (etFat.getText().toString().length() < 1 && etSnf.getText().toString().length() < 1
                    && etClr.getText().toString().length() < 1) {
                Toast.makeText(MilkCollectionActivityV2.this, "Enter fat/snf manually!", Toast.LENGTH_SHORT).show();
                etFat.requestFocus();
                etFat.setText(String.valueOf(keyValue));
                etFat.setCursorVisible(true);

                int position = etFat.length();
                etFat.setSelection(position);
            }

        } else if (amcuConfig.getWsManual() && !waitingForMAData &&
                !btnReject.getText().toString().equalsIgnoreCase("Read weight")
                && mReportEntity.status.equalsIgnoreCase("Accept") && !btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate")
                && !btnReject.getText().toString().equalsIgnoreCase("Rate done") &&
                !btnReject.getText().toString().equalsIgnoreCase("Read Rate")) {

            if (checkQualityValidation(milkAnalyserEntity) && !isWsManual.equalsIgnoreCase("Manual")) {
                closeWSConnection();
                Util.alphabetValidation(etMilkweight, Util.ONLY_DECIMAL, MilkCollectionActivityV2.this, 8);
                textChangeFunction();
                etMilkweight.requestFocus();
                etMilkweight.setText(String.valueOf(keyValue));
                etFat.setCursorVisible(true);
                int pos = etMilkweight.length();
                etMilkweight.setSelection(pos);
                mReportEntity.quantityMode = "Manual";
                Toast.makeText(MilkCollectionActivityV2.this, "Enter weight manually!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void openMilkStateDialog(double actualData) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MilkCollectionActivityV2.this);
        LayoutInflater inflater = MilkCollectionActivityV2.this.getLayoutInflater();
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
                        mReportEntity.getQuantity() < newRcordFilledCan)) {
                    Util.displayErrorToast("Invalid can weight", MilkCollectionActivityV2.this);
                } else if (mValidationHelper.validMilkWeight(newRcordFilledCan, MilkCollectionActivityV2.this)) {
                    mReportEntity.quantity = Double.parseDouble(tvTotal.getText().toString());
                    etMilkweight.setText(String.valueOf(mReportEntity.quantity));
                  /*  ltrWeight = ltrWeight + tempLt;
                    kgWeight = kgWeight + tempKg;*/
                    setAmount(mReportEntity.getQuantity());
                    newRcordFilledCan = 0.00;

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

                newRcordFilledCan = 0.00;
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

    public void setAlertText(double record) {
        tvPreviousData.setText(String.valueOf(mReportEntity.quantity));
        double minLimit, currentRecord, maxLimit;
        record = getWeightRecordForAlert(Double.valueOf(record));
        currentRecord = record;
        newRcordFilledCan = currentRecord;

        if (radioStatus.equalsIgnoreCase("filled")) {
            tvNewData.setText("(+) " + record);
            tvTotal.setText(String.valueOf(Double.parseDouble(tvPreviousData.getText().toString())
                    + record));
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
                tvTotal.setText(String.valueOf((Double.parseDouble(tvPreviousData.getText().toString())
                        - record)));
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

    public void alertForRemoveCan() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MilkCollectionActivityV2.this);
        // set title

        alertDialogBuilder.setTitle("Reset weighing scale!");
        // set dialog message
        alertDialogBuilder
                .setMessage("Please remove the can and press OK to tare the weighing scale")
                .setCancelable(false);


        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ignoreInitialWSData = true;
                        try {
                            tareWSOverSerialManager();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        isFilledOrEmptyCanStarted = true;

                        openMilkStateDialog(mReportEntity.quantity);
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

    public double getWeightRecordForAlert(double weight_Record) {

        weight_Record = weight_Record / amcuConfig.getWeighingDivisionFactor();

        if ((!amcuConfig.getAllowInKgformat() && amcuConfig.getMyRateChartEnable())
                || (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg())) {
            mReportEntity.setLtrsWeight(weight_Record);
            mReportEntity.setQuantity(mReportEntity.getLtrsWeight() *
                    Double.parseDouble(amcuConfig.getConversionFactor()));
            mReportEntity.setKgWeight(mReportEntity.getQuantity());

        } else if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {
            mReportEntity.setKgWeight(weight_Record);
            mReportEntity.setQuantity(mReportEntity.getKgWeight() /
                    Double.parseDouble(amcuConfig.getConversionFactor()));
            //mLtRecord = decimalFormatQuantity.format(Double.parseDouble(record));
            mReportEntity.setLtrsWeight(mReportEntity.getQuantity());


        } else {
            mReportEntity.setLtrsWeight(weight_Record);
            mReportEntity.setQuantity(weight_Record);
            mReportEntity.setKgWeight(mReportEntity.getLtrsWeight() *
                    Double.parseDouble(amcuConfig.getConversionFactor()));

        }

        return mReportEntity.getQuantity();
    }

    public void setWeightAndAmount(double record) {
        double amt;
        try {

            double weight_Record = record;
            weight_Record = weight_Record / amcuConfig.getWeighingDivisionFactor();

            etMilkweight.setText(String.valueOf(mReportEntity.getDisplayQuantity()));
            //  Rate = Double.parseDouble(decimalFormatRate.format(Rate));
            amt = mReportEntity.getRate() * mReportEntity.getRateCalculationQuanity();
            calculateBonusAndIncentive(amt, mReportEntity.getRateCalculationQuanity());
            if (!btnReject.isEnabled()) {
                btnReject.setEnabled(true);
            }
            if (amcuConfig.getEnableFilledOrEmptyCans() && mSession.getIsChillingCenter()) {
                btnNext.setEnabled(true);
            } else {
                btnReject.requestFocus();
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void enableReject(String str2) {
        if (str2 == null) {
            btnReject.setText("OK");
        }
        btnReject.requestFocus();
        btnReject.setFocusable(true);
        btnReject.setEnabled(true);
    }

    public void setAmount(double record) {
//        Rate = Double.parseDouble(decimalFormatRate.format(Rate));
        double amt = mReportEntity.getRate() * record;
        getBonus();
        getInCentiveRate(mReportEntity.getRateCalculationProtein());
        mReportEntity.setAmount(amt);
        double bonusAmt = mReportEntity.getBonus() * record;
        mReportEntity.setBonus(bonusAmt);
        mReportEntity.setIncentiveAmount(mReportEntity.getIncentiveRate() * record);

        if (amcuConfig.getKeyAllowProteinValue()) {
            etAmount.setText(String.valueOf(mReportEntity.getTotalAmount()));
        } else {
//            TODO Use amount from getter in ReportEntity
            etAmount.setText(String.valueOf(Util.getAmount(getApplicationContext(), mReportEntity.getTotalAmount(),
                    mReportEntity.getBonus())));
        }
    }

    class PingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            for (DeviceEntity deviceEntity : allDeviceData) {
                if (deviceEntity.deviceName == DeviceName.MILK_ANALYSER && deviceEntity.deviceType == SmartCCConstants.WIFI) {
                    maPing = ping(SmartCCConstants.maIp);
                }
                if (deviceEntity.deviceName == DeviceName.WS && deviceEntity.deviceType == SmartCCConstants.WIFI) {
                    wsPing = ping(SmartCCConstants.wsIp);
                }
                if (deviceEntity.deviceName == DeviceName.RDU && deviceEntity.deviceType == SmartCCConstants.WIFI) {
                    rduPing = ping(SmartCCConstants.rduIp);
                }
                if (deviceEntity.deviceName == DeviceName.PRINTER && deviceEntity.deviceType == SmartCCConstants.WIFI) {
                    printerPing = ping(SmartCCConstants.printerIp);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cbMa.setChecked(maPing);
            cbWs.setChecked(wsPing);
            cbRdu.setChecked(rduPing);
            cbPrinter.setChecked(printerPing);

            for (DeviceEntity deviceEntity : allDeviceData) {
                if (deviceEntity.deviceName == DeviceName.MILK_ANALYSER && deviceEntity.deviceType == SmartCCConstants.WIFI)
                    mWifiUtility.checkWisensConnectivity(MilkCollectionActivityV2.this, DeviceName.MILK_ANALYSER, maPing);
                if (deviceEntity.deviceName == DeviceName.WS && deviceEntity.deviceType == SmartCCConstants.WIFI)
                    mWifiUtility.checkWisensConnectivity(MilkCollectionActivityV2.this, DeviceName.WS, wsPing);
                if (deviceEntity.deviceName == DeviceName.RDU && deviceEntity.deviceType == SmartCCConstants.WIFI)
                    mWifiUtility.checkWisensConnectivity(MilkCollectionActivityV2.this, DeviceName.RDU, rduPing);
                if (deviceEntity.deviceName == DeviceName.PRINTER && deviceEntity.deviceType == SmartCCConstants.WIFI)
                    mWifiUtility.checkWisensConnectivity(MilkCollectionActivityV2.this, DeviceName.PRINTER, printerPing);


            }
        }
    }

}