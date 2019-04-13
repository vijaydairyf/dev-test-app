package com.devapp.devmain.macollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.SalesRecordDao;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SalesRecordEntity;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.FarmerScannerHelper;
import com.devapp.devmain.helper.UserType;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.rdu.RduFactory;
import com.devapp.devmain.rdu.RduManager;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.FormatHindiLocal;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.CursorEndOnFocusChangeListener;
import com.devapp.devmain.util.FocusForwardKeyListener;
import com.devapp.devmain.util.RegexInputFilter;
import com.devapp.devmain.util.RegexTextWatcher;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.devmain.ws.WsFactory;
import com.devapp.devmain.ws.WsManager;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class SalesActivity extends Activity implements OnClickListener {

    private final String TAG = SalesActivity.class.getSimpleName();
    public String message;
    public String milkAnanalyserTime, startTime, weighTime;
    public boolean visible = false;
    public boolean isEnable;
    public String RDUMsg;
    public String currentShift;
    public String dbError = null;
    public boolean isNameEnable, isRateEnable, isFatEnable, isSnfEnable;
    String weightRecord = "0.0", amountRecord = "0.0", bonusAmount = "0.0";
    String isAutoManual = "Manual", addedWater = "0.0", isMaAutoManual = "Auto", isWsManual = "Auto";
    boolean isBtnEnable;
    boolean waitingForMAData = true;
    boolean ignoreInitialWSData = true;
    Button btnReject, btnWeight, btnAutoManual, btnBack;
    StringBuilder sbMessage = new StringBuilder();
    EditText etFat, etSnf, etMilkWeight, etFarmerName, etFarmerId, etRate,
            etAmount;
    double clr = 0, temp = 84.0;
    TextView tvFatAuto, tvSnfAuto, tvRate, tvMilkWeight;
    TextView tvHeader;
    String msg, rate;
    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatWeight = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatRate = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    double Rate = 0;
    String Totrate, Msg;
    String strRate;
    TableRow trFat, trSnf, trFarmId,
            trWeight, trRate, trAmount;
    double prevRecord = 0;
    long quantityTime, qualityTime;
    // TODO:
    // The following flag has been introduced to prevent user from pressing
    // PRINT multiple times and
    // end up with multiple records for the same farmerID.
    // Refer redmine issue #105. Possibly the highest priority issue that a
    // customer will notice
    boolean printEnabled = true;
    boolean isFatSnfPressed;
    TextView tvFarmerId;
    String rateStr = "0.0";
    String DATE, TIME;
    int countTare = 0;
    boolean isReject = false;
    boolean isCleaningFailed = false;
    Runnable timeOutRunnable;
    Handler timeOutHandler = new Handler();
    boolean timeOut = false;
    long time2 = System.currentTimeMillis() + 300000L;
    double collectionQuantity = 0;

    boolean isResetDone;
    private Context context = this;
    private FarmerEntity farmerEntity;
    private AlertDialog alertDialog;
    //Utilities
    private MaManager maManager;
    private WsManager wsManager;
    private ValidationHelper validationHelper;
    private UsbManager mUsbManager;
    private SalesRecordDao salesRecordDao;
    private SalesRecordEntity salesRecordEntity;
    private AmcuConfig amcuConfig;
    private PrinterManager printManager;
    private SessionManager session;
    private Calendar calendar;
    private SmartCCUtil mSmartCCUtil;
    private MilkAnalyserEntity milkAnalyserEntity;


    @Override
    public void onStart() {

        getCollectionQuantity();
        initializeUtility();
        initializeVariable();
        initializeSalesReportEntity();

        startTime = Util.getTodayDateAndTime(3, SalesActivity.this, true);
        setDecimalRoundOff();
        currentShift = Util.getCurrentShift();
        sbMessage = new StringBuilder();
        enableName();

        if (maManager != null)
            maManager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            milkAnalyserEntity = maEntity;
                            waitingForMAData = false;
                            closeMAConnection();
                            etFat.setText(decimalFormatFS.format(maEntity.fat));
                            etSnf.setText(decimalFormatFS.format(maEntity.snf));
                            afterGettingMaData();
                        }
                    });
                }

                @Override
                public void onOtherMessage(final String message) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!session.getIsSample()) {
                                onFinish();
                            } else {
                                enableReject(message);
                            }
                        }
                    });
                }
            });
        if (wsManager != null)
            wsManager.setOnNewDataListener(new WsManager.OnNewDataListener() {
                @Override
                public void onNewData(final double data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            double weight = 0;
                            if (data >= 0) {
                                try {
                                    weight = data;
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                if (weight != 0 && weight > 0) {
                                    double weight_Record = data;
                                    weight_Record = weight_Record / amcuConfig.getWeighingDivisionFactor();
                                    setWeightAndAmount(String.valueOf(weight_Record));
                                }
                            }
                        }
                    });
                }
            });

        waitingForMAData = true;
        ignoreInitialWSData = true;


        setMaEnable();
        setConnection();
        setVisibilityForId();
        enableOrDisableFS();

        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.sales_activity_landscape);
        } else {
            setContentView(R.layout.sales_actvity_layout);
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        initializeUtility();
        initializeView();
        initializeVariable();
        setDisableInitilization();
        setTextWatcher();
        setHeader();
    }


    private void initializeView() {
        etFarmerId = (EditText) findViewById(R.id.etFarmerId);
        tvFarmerId = (TextView) findViewById(R.id.tvFarmerId);
        etFarmerName = (EditText) findViewById(R.id.etFarmerName);
        etRate = (EditText) findViewById(R.id.etRate);
        etAmount = (EditText) findViewById(R.id.etAmount);
        etSnf = (EditText) findViewById(R.id.etSnf);
        etMilkWeight = (EditText) findViewById(R.id.etMilkweight);
        etFat = (EditText) findViewById(R.id.etFat);

        btnReject = (Button) findViewById(R.id.btnReject);
        btnWeight = (Button) findViewById(R.id.btnNext);
        btnAutoManual = (Button) findViewById(R.id.btnAutoManual);
        btnBack = (Button) findViewById(R.id.btnBack);

        tvFatAuto = (TextView) findViewById(R.id.tvFatAuto);
        tvSnfAuto = (TextView) findViewById(R.id.tvSnfAuto);
        tvHeader = (TextView) findViewById(R.id.tvheader);
        tvMilkWeight = (TextView) findViewById(R.id.tvMilkWeight);
        btnAutoManual.setVisibility(View.GONE);
        btnReject.setOnClickListener(this);
        btnWeight.setOnClickListener(this);
        btnAutoManual.setOnClickListener(this);
        btnBack.setOnClickListener(this);


        trFat = (TableRow) findViewById(R.id.trFat);
        trSnf = (TableRow) findViewById(R.id.trSnf);
        trFarmId = (TableRow) findViewById(R.id.trFarmId);
        trWeight = (TableRow) findViewById(R.id.trMilkWeight);
        trRate = (TableRow) findViewById(R.id.trRate);
        trAmount = (TableRow) findViewById(R.id.trAmount);

        tvRate = (TextView) findViewById(R.id.tvRate);
        etFarmerId.setFilters(new InputFilter[]{new RegexInputFilter(AppConstants.Regex.NUMBER)});
        etFarmerId.setOnKeyListener(new FocusForwardKeyListener(etFat));
        etFarmerId.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etFat.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etSnf.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());

        // etMilkWeight.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etMilkWeight.setFilters(new InputFilter[]{new RegexInputFilter("[0-9\\.]*")});
        etFat.addTextChangedListener(new RegexTextWatcher(etFat, AppConstants.Regex.NUMBER_DECIMAL));
        etSnf.addTextChangedListener(new RegexTextWatcher(etSnf, AppConstants.Regex.NUMBER_DECIMAL));
        if (!amcuConfig.getSalesFSEnable()) {
            etFarmerName.requestFocus();
            etFarmerName.setOnKeyListener(new FocusForwardKeyListener(etMilkWeight));
        }
    }

    private void initializeUtility() {
        printManager = new PrinterManager(SalesActivity.this);
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(SalesActivity.this);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        salesRecordEntity = new SalesRecordEntity();
        salesRecordDao = (SalesRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_SALES);
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        validationHelper = new ValidationHelper();
        maManager = MAFactory.getMA(DeviceName.MA1, SalesActivity.this);
        wsManager = WsFactory.getWs(DeviceName.WS, SalesActivity.this);
        mSmartCCUtil = new SmartCCUtil(SalesActivity.this);
    }

    private void initializeVariable() {
        try {
            isAutoManual = getIntent().getStringExtra("isAutoOrManual");
        } catch (Exception e) {
            isAutoManual = "Manual";
            e.printStackTrace();
        }
        session.setMilkReject("NO");
        btnReject.setText("Print");
        etFarmerId.setText(session.getFarmerID());
        etFarmerName.setText(session.getFarmerName());
        // This has been added to disallow user from pressing PRINT
        // multiple times in the typical Milk Accepted scenario.
        printEnabled = true;
        btnReject.setEnabled(true);
    }


    public void setTextWatcher() {
        etFarmerId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (validFarmerId(String.valueOf(s))) {
                    if (farmerEntity != null && farmerEntity.farmer_name != null)
                        etFarmerName.setText(farmerEntity.farmer_name);
                } else
                    etFarmerName.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etFarmerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Util.alphabetValidation(etFarmerName, Util.ONLY_ALPHABET, context, 0);
                isNameEnable = true;
                isFatEnable = false;
                isSnfEnable = false;
                isRateEnable = false;

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private boolean validFarmerId(String id) {
        boolean value = false;
        if (id.length() == 0)
            value = true;
        else {
            FarmerScannerHelper farmerScannerHelper = new FarmerScannerHelper(context);
            int entryType = farmerScannerHelper.isBarocodeOrId(id);
            String userType = farmerScannerHelper.getUserType(id, entryType);

            if (userType.equals(UserType.FARMER)) {
                farmerEntity = (FarmerEntity) farmerScannerHelper.getUserDetailsFromUserType(
                        etFarmerId.getText().toString().trim(), userType);
                value = true;
            }
        }
        return value;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnNext: {

            }
            break;
            case R.id.btnReject: {
                onClickButtonReject();

            }
            break;

            case R.id.btnBack: {

                closeMAConnection();
                onFinish();

            }
            break;

            case R.id.btnAutoManual:

            {

            }
            break;
            default:
                break;
        }
    }

    public void onClickButtonReject() {

        if ((amcuConfig.getSalesFSEnable() &&
                validFarmerId(etFarmerId.getText().toString()))
                || !amcuConfig.getSalesFSEnable()) {
            if (btnReject.getText().toString().equalsIgnoreCase("Read weight")) {

                if (checkForValidData(false)) {
                    setDisable();
                    //for f8 and smaple
                    btnReject.setEnabled(false);

                    waitingForMAData = false;
                    evaluateAcceptOrReject();
                    setWsEnable();
                } else {
                    Toast.makeText(SalesActivity.this, "Enter valid fat and snf values!", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (checkForValidData(true)) {

                    if (Util.salesWeightValidation(String.valueOf(collectionQuantity)
                            , etMilkWeight.getText().toString())) {
                        if (Double.parseDouble(etMilkWeight.getText().toString()) > amcuConfig.getMaxlimitOfWeight())
                            showAlertForManualWeight();
                        else
                            onPrintOrReject();
                    } else {
                        etMilkWeight.requestFocus();
                        Toast.makeText(SalesActivity.this,
                                "Sales quantity should be less than, remaining collection " + decimalFormatWeight.format(collectionQuantity) + "",
                                Toast.LENGTH_LONG).show();
                    }


                } else {
                    Toast.makeText(SalesActivity.this, "Enter valid data in all fields!", Toast.LENGTH_SHORT).show();
                }
            }
        } else
            Util.displayErrorToast("Invalid farmer Id", context);
    }


    public void onPrintOrReject() {
        btnReject.setEnabled(false);
        btnReject.setFocusable(false);

        weighTime = Util.getTodayDateAndTime(6, SalesActivity.this, true);
        startTime = Util.getTodayDateAndTime(3, SalesActivity.this, true);
        quantityTime = calendar.getTimeInMillis();
        if (milkAnalyserEntity == null || milkAnalyserEntity.clr < 1) {
            clr = Util.getCLR(Double.valueOf(etFat.getText().toString()),
                    Double.valueOf(etSnf.getText().toString()));
        } else {
            clr = milkAnalyserEntity.clr;
        }
        if (timeOut) {
            onFinish();
        }

        if (true == printEnabled) {
            printEnabled = false;
            getFullMsg();
            onAcceptMilk();
        }

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        String snf, fat, reject, qty, rate, amt;
        snf = etSnf.getText().toString();
        fat = etFat.getText().toString();
        reject = btnReject.getText().toString();
        qty = etMilkWeight.getText().toString();
        rate = etRate.getText().toString();
        amt = etAmount.getText().toString();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.sales_activity_landscape);
        } else {
            setContentView(R.layout.sales_actvity_layout);
        }

        etFarmerId = (EditText) findViewById(R.id.etFarmerId);
        tvFarmerId = (TextView) findViewById(R.id.tvFarmerId);
        etFarmerName = (EditText) findViewById(R.id.etFarmerName);
        etRate = (EditText) findViewById(R.id.etRate);
        etAmount = (EditText) findViewById(R.id.etAmount);
        etSnf = (EditText) findViewById(R.id.etSnf);
        etMilkWeight = (EditText) findViewById(R.id.etMilkweight);
        etFat = (EditText) findViewById(R.id.etFat);
        tvHeader = (TextView) findViewById(R.id.tvheader);
        btnReject = (Button) findViewById(R.id.btnReject);
        btnWeight = (Button) findViewById(R.id.btnNext);

        etFarmerId.setText(session.getFarmerID());
        etFarmerName.setText(session.getFarmerName());
        setHeader();
        etRate.setText(decimalFormatRate.format(Double.parseDouble(rate)));
        etAmount.setText(amt);
        etSnf.setText(snf);
        etFat.setText(fat);
        etMilkWeight.setText(qty);
        btnReject.setText(reject);
        btnReject.setEnabled(isBtnEnable);

        trFat = (TableRow) findViewById(R.id.trFat);
        trSnf = (TableRow) findViewById(R.id.trSnf);
        trFarmId = (TableRow) findViewById(R.id.trFarmId);
        trWeight = (TableRow) findViewById(R.id.trMilkWeight);
        trRate = (TableRow) findViewById(R.id.trRate);
        trAmount = (TableRow) findViewById(R.id.trAmount);


        if (isBtnEnable) {
            btnReject.requestFocus();
        }

        setDisable();
        setVisibilityForId();
        enableName();
        enableOrDisableFS();
        // onSetDataOnScreen();
    }

    public void setDisable() {
        etFat.setEnabled(false);
        etSnf.setEnabled(false);
        etAmount.setEnabled(false);
        etFarmerId.setEnabled(true);
        if (amcuConfig.getSalesFSEnable())
            etFarmerName.setEnabled(false);
        else {
            etFarmerName.setEnabled(true);
            etFarmerId.requestFocus();
        }
        etRate.setEnabled(false);
        if (amcuConfig.getDisableManualForDispatchValue())
            etMilkWeight.setEnabled(false);
    }

    public void setDisableInitilization() {

        etAmount.setEnabled(false);
        if (amcuConfig.getSalesFSEnable())
            etFarmerName.setEnabled(false);
        else {
            etFarmerName.setEnabled(true);
            etFarmerId.requestFocus();
        }
        etRate.setEnabled(false);
        if (amcuConfig.getDisableManualForDispatchValue())
            etMilkWeight.setEnabled(false);

    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        DATE = Util.getDateAndTimeRDU(1);
        TIME = Util.getDateAndTimeRDU(0);
        if (etFat.getText().toString().length() < 1) {
            setMaEnable();
        }
    }

    public void setWeightAndAmountManually(String record, String rate) {

        double amt, bonusAmt;
        try {
            Rate = Double.parseDouble(rate);
        } catch (NumberFormatException e) {
            Rate = 0;
            e.printStackTrace();
        }

        try {

            double weight_Record = Double.parseDouble(record);
//            weight_Record = weight_Record / amcuConfig.getWeighingDivisionFactor();
            record = decimalFormatWeight.format(weight_Record);

            Rate = Double.parseDouble(decimalFormatRate.format(Rate));
            amt = Rate * Double.parseDouble(record);
            amountRecord = decimalFormatAmount.format(amt);
            etAmount.setText(amountRecord);
            weightRecord = record;
            if (!btnReject.isEnabled()) {
                isBtnEnable = true;
                btnReject.setEnabled(true);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public void setWeightAndAmount(String record) {
        double amt, bonusAmt;
        //if conversion factor is enabled, it will consider milk as in liter format
        try {
            if (!amcuConfig.getAllowInKgformat() && amcuConfig.getMyRateChartEnable()) {
                record = String.valueOf(Double.parseDouble(record) *
                        Double.parseDouble(amcuConfig.getConversionFactor()));
            } else if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {
                record = String.valueOf(Double.parseDouble(record) /
                        Double.parseDouble(amcuConfig.getConversionFactor()));

            } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
                record = String.valueOf(Double.parseDouble(record) *
                        Double.parseDouble(amcuConfig.getConversionFactor()));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        try {
            etMilkWeight.setText(decimalFormatWeight.format(Double
                    .parseDouble(record)));
            record = decimalFormatWeight.format(Double.parseDouble(record));
            Rate = Double.parseDouble(decimalFormatRate.format(Rate));
            amt = Rate * Double.parseDouble(record);
            amountRecord = decimalFormatAmount.format(amt);
            etAmount.setText(amountRecord);
            weightRecord = record;


            if (!btnReject.isEnabled()) {
                isBtnEnable = true;
                btnReject.setEnabled(true);
            }
            if (amcuConfig.getEnableFilledOrEmptyCans() && session.getIsChillingCenter()) {
                btnWeight.setEnabled(true);
            } else {
                btnReject.requestFocus();
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStop() {
        try {
            closeMAConnection();
            closeWSConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            closeMAConnection();
            closeWSConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }


    private void initializeSalesReportEntity() {
        salesRecordEntity = new SalesRecordEntity();

        salesRecordEntity.postDate = mSmartCCUtil.getReportFormatDate();
        salesRecordEntity.postShift = SmartCCUtil.getShiftInPostFormat(
                SalesActivity.this);
    }


    public void getFullMsg() {
        salesRecordEntity.temperature = temp;
        if (etFat.getText().toString().replace(" ", "").length() > 0) {
            salesRecordEntity.fat = Double.valueOf(decimalFormatFS.format(Double.valueOf(
                    decimalFormatFS.format(Double.parseDouble(etFat.getText().toString())))));
            salesRecordEntity.snf = Double.valueOf(decimalFormatFS.format(Double.valueOf(
                    decimalFormatFS.format(Double.parseDouble(etSnf.getText().toString())))));
        }
        salesRecordEntity.setAwm(Double.valueOf(
                decimalFormatFS.format(Double.parseDouble(addedWater))));
        setMilkType();
        try {
            salesRecordEntity.amount = Double.valueOf(decimalFormatAmount.format(Double.valueOf(amountRecord)));
            salesRecordEntity.Quantity = Double.valueOf(decimalFormatWeight.format(Double.valueOf(weightRecord)));
            salesRecordEntity.setRate(Double.valueOf(decimalFormatRate.format(Double.parseDouble(etRate
                    .getText().toString()))));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public void saveReportsOnSdCard() throws IOException {

        String stringReport;
        String date = Util.getTodayDateAndTime(1, SalesActivity.this, true);
        String status;

        if (isReject) {
            status = "Reject";
        } else {
            status = "Accept";
        }
        if (isAutoManual == null) {
            isAutoManual = "Manual";
        }
        stringReport = session.getCollectionID() + "," + session.getFarmerID() + "," + session.getFarmerName() + ","
                + session.getFarmerBarcode() + ","
                + Util.getTodayDateAndTime(7, SalesActivity.this, true) + "," + Util.getTodayDateAndTime(3, SalesActivity.this, true) + ","
                + salesRecordEntity.getQuantity() + "," +
                salesRecordEntity.getFat() + "," +
                salesRecordEntity.getSnf()
                + "," + salesRecordEntity.getRate() + "," +
                salesRecordEntity.amount + ","
                + salesRecordEntity.awm + "," + "," + isAutoManual
                + "," + status + "," + isMaAutoManual + "," + isWsManual + "\n";

        Util.generateNoteOnSD(date + currentShift + "_salesReportReports",
                stringReport, SalesActivity.this, "smartAmcuReports");

    }

    public void createSMSText() {

        Totrate = etAmount.getText().toString();
        if (Totrate.replace(" ", "").equalsIgnoreCase(".00")) {
            Totrate = "0.0";
        }

        strRate = String.valueOf(decimalFormatRate.format(Rate));
        if (strRate.replace(" ", "").equalsIgnoreCase(".00")) {
            strRate = "0.0";
        }

        message = "\n" + "\n" + Util.getTodayDateAndTime(1, SalesActivity.this, true) + "/"
                + currentShift + "\n" + session.getFarmerName() + "\n"
                + "QTY :  " + etMilkWeight.getText().toString() + "L" + "\n"
                + "FAT :  " + etFat.getText().toString() + "\n" + "SNF :  "
                + etSnf.getText().toString() + "\n" + "RATE :  " + strRate
                + " Rs" + "\n" + "Amount :  " + Totrate + " Rs" + "\n" + "\n";
    }

    public void onAcceptMilk() {

        try {
            getSalesReportEntity();
        } catch (Exception e) {
            e.printStackTrace();
        }

        dbError = addToDatabase(salesRecordEntity);
        tareWSOverSerialManager();
        createSMSText();
        if (dbError != null) {
            Util.displayErrorToast("Db error occurred", SalesActivity.this);
            setReportData(false);
            afterDbError();
        }
        writeOnSDCard();
        afterAddingDatabaseTable();
        onFinish();

    }

    public void afterAddingDatabaseTable() {

        try {
            startRDU();
        } catch (Exception e) {
            e.printStackTrace();
        }

        printReceipt();
        tareWeighingScale();
        closeWSConnection();

    }

    public void setConnection() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                if (maManager != null) {
                    maManager.startReading();
                }
            }
        }).start();

        if (session.getIsSample()
                && (Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            checkTimeout();
        }

    }

    public void startRDU() {

        RduManager rduManager = RduFactory.getRdu(amcuConfig.getRDU(), SalesActivity.this);
        if (rduManager != null) {
            rduManager.openConnection();
            ReportEntity repEntity = Util.getReportEntityFromSalesRecord(salesRecordEntity);
            rduManager.displayReport(repEntity, amcuConfig.getEnableIncentiveRDU());
        } else {
            Toast.makeText(SalesActivity.this,
                    "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
        }
    }


    public void showAlertForManualWeight() {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        alertDialog = builder.create();

        builder.setCancelable(false);
        builder.setTitle("Exceeding quantity alert!");
        builder.setMessage("Entered quantity should be more than min limit " + amcuConfig.getKeyMinValidWeight() +
                " and less than max limit " + amcuConfig.getMaxlimitOfWeight()
                + ".");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                alertDialog.cancel();


            }
        });

        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                alertDialog.cancel();

            }
        });

        if (alertDialog == null || !alertDialog.isShowing()) {
            alertDialog = builder.create();
            alertDialog.show();
        }


    }

    public void setMilkType() {

        if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || session.getIsSample()) {
            salesRecordEntity.setMilkType(CattleType.TEST);
        } else if (session.getMilkType() == null) {

            double fat1 = 0;
            double fat2 = 0;
            try {
                fat1 = Double.parseDouble(etFat.getText().toString());
                fat2 = Double.parseDouble(amcuConfig.getChangeFat());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (fat1 >= fat2 && fat2 != 0) {
                salesRecordEntity.setMilkType(CattleType.BUFFALO);
            } else {
                salesRecordEntity.setMilkType(CattleType.COW);
            }
        } else {
            double fat1 = 0;
            double fat2 = 0;
            try {
                fat1 = Double.parseDouble(etFat.getText().toString());
                fat2 = Double.parseDouble(amcuConfig.getChangeFat());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (fat1 >= fat2 && fat2 != 0) {
                salesRecordEntity.setMilkType(CattleType.BUFFALO);
            } else {
                salesRecordEntity.setMilkType(session.getMilkType());
            }
        }
    }

    public void readWeightData() throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (wsManager != null) {
                    wsManager.openConnection();
                }
            }
        }).start();

        // ReadData();
    }

    public void tareWeighingScale() {
        if (wsManager != null) {
            countTare = countTare + 1;
            wsManager.tare();
        }

    }

    public void writeOnSDCard() {
        try {
            saveReportsOnSdCard();
        } catch (Exception e) {
            afterAddingDatabaseTable();
            Util.restartTab(SalesActivity.this);
            e.printStackTrace();
        }
    }

    public void afterDbError() {
        if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit()) || !session.getIsSample()) {
            try {
                printReceipt();
                printReceipt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(SalesActivity.this, "Tab restart is required.", Toast.LENGTH_LONG).show();
        Util.restartTab(SalesActivity.this);
        //   alertForDatabaseFailure();

    }

    private void getSalesReportEntity() {
        int txNumber = new SessionManager(SalesActivity.this)
                .getTXNumber() + 1;

        session.setTXNumber(txNumber);
        String date = Util.getTodayDateAndTime(1, SalesActivity.this, true);
        amcuConfig.setLastShift(currentShift);
        amcuConfig.setLastShiftDate(date);
        amcuConfig.setCollectionEndShift(false);
        salesRecordEntity.user = session.getUserId();
        salesRecordEntity.salesId = Util.paddingFarmerId(etFarmerId.getText().toString().trim(),
                amcuConfig.getFarmerIdDigit());
        salesRecordEntity.name = etFarmerName.getText().toString();
        salesRecordEntity.socId = String.valueOf(session.getSocietyColumnId());


        salesRecordEntity.temperature = temp;
        salesRecordEntity.fat = Double.valueOf(
                decimalFormatFS.format(
                        validationHelper.getDoubleFromString(
                                etFat.getText().toString().trim(), 0)));
        salesRecordEntity.snf = Double.valueOf(
                decimalFormatFS.format(
                        validationHelper.getDoubleFromString(
                                etSnf.getText().toString().trim(), 0)));
        salesRecordEntity.rate = Double.valueOf(
                decimalFormatRate.format(
                        validationHelper.getDoubleFromString(
                                etRate.getText().toString().trim(), 0)));

        salesRecordEntity.Quantity = Double.valueOf(
                decimalFormatWeight.format(
                        validationHelper.getDoubleFromString(
                                etMilkWeight.getText().toString().trim(), 0)));

        salesRecordEntity.amount = Double.valueOf(
                decimalFormatAmount.format(
                        validationHelper.getDoubleFromString(
                                etAmount.getText().toString().trim(), 0)));

        salesRecordEntity.postDate = date;
        salesRecordEntity.postShift = currentShift;
        salesRecordEntity.time = startTime;
        salesRecordEntity.txnNumber = txNumber;
        salesRecordEntity.collectionTime = calendar.getTimeInMillis();
        salesRecordEntity.awm = validationHelper.getDoubleFromString(addedWater, 0);

        if (session.getIsSample()
                || Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {

            if (isCleaningFailed) {
                salesRecordEntity.status = "Failure";
            } else {
                salesRecordEntity.status = "Success";
            }

        } else if (isReject) {
            salesRecordEntity.status = "Reject";
        } else {
            salesRecordEntity.status = "Accept";
        }

        Util.setCollectionStartedWithMilkType(salesRecordEntity.milkType, SalesActivity.this);


        if (isAutoManual != null) {
            salesRecordEntity.manual = isAutoManual;
            salesRecordEntity.weightManual = isWsManual;
            salesRecordEntity.milkoManual = isMaAutoManual;
        } else {
            salesRecordEntity.manual = "Manual";
            salesRecordEntity.weightManual = isWsManual;
            salesRecordEntity.milkoManual = isMaAutoManual;
        }
        salesRecordEntity.txnType = Util.SALES_TXN_TYPE_SALES;
        salesRecordEntity.txnSubType = Util.SALES_TXN_SUBTYPE_NORMAL;
        salesRecordEntity.clr = clr;

        salesRecordEntity.sentStatus = CollectionConstants.UNSENT;
        salesRecordEntity.sentSmsStatus = CollectionConstants.UNSENT;

    }

    public void evaluateAcceptOrReject() {
        setMaEntity();
        btnReject.setFocusable(true);
        milkAnanalyserTime = Util.getTodayDateAndTime(6, SalesActivity.this, true);
        qualityTime = calendar.getTimeInMillis();
        isReject = false;
        btnReject.setText("Print");
        try {
            sbMessage = new StringBuilder();
            readWeightData();
            setWsEnable();
        } catch (IOException e) {
            e.printStackTrace();
        }

        btnBack.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // No Operation to suppress back button
        // super.onBackPressed();
    }

    public void tareWSOverSerialManager() {
        if (wsManager != null) {
            wsManager.tare();
        }
    }

    public void enableReject(String str2) {

        if (str2 == null) {
            btnReject.setText("OK");
        } else if (str2.equalsIgnoreCase("CLN")
                && (Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            btnReject.setText("Cleaning Done");

        } else if (str2.equalsIgnoreCase("CLN") &&
                (!Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            btnReject.setText("Cleaning Failed, Please Try Again!");
            isCleaningFailed = true;

        } else if (str2.equalsIgnoreCase("RIN")
                && Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            btnReject.setText("Rinsing Done");
        } else if (str2.equalsIgnoreCase("RIN")
                && (!Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            btnReject.setText("Rinsing Failed, Please Try Again!");
            isCleaningFailed = true;
        } else if (str2.equalsIgnoreCase("CLFL")) {
            btnReject.setText("Cleaning Failed, Please Try Again!");
            isCleaningFailed = true;

        } else if (str2.equalsIgnoreCase("Timeout")) {
            timeOut = true;
            btnReject.setText("Time Out, Please Try Again!");

        }
        btnReject.requestFocus();
        isBtnEnable = true;
        btnReject.setFocusable(true);
        btnReject.setEnabled(true);

    }

    public void afterGettingMaData() {

        if (!etFat.isEnabled()) {
            sbMessage = new StringBuilder();
            evaluateAcceptOrReject();
            setWsEnable();
        } else {
            btnReject.setEnabled(true);
            btnReject.setText("Read weight");
            waitingForMAData = false;
            btnReject.requestFocus();
        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN) {
            btnReject.requestFocus();
            return true;

        } else {

            return false;
        }

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnReject.requestFocus();
                return true;
            }

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(SalesActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:
                Util.restartTab(SalesActivity.this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(SalesActivity.this, null);
                return true;

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
            case KeyEvent.KEYCODE_NUMPAD_ENTER: {
                if (btnBack.getVisibility() == View.VISIBLE && btnBack.hasFocus()) {
                    closeMAConnection();
                    onFinish();
                } else if (!isFatSnfPressed && checkForValidData(false) && btnReject.isEnabled()) {
                    onClickButtonReject();
                } else {
                    onClickButtonReject();
                }


                isFatSnfPressed = false;
                return true;
            }


            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    public String receiptFormat() {

        ReportEntity reportEntity = new ReportEntity();
        reportEntity.farmerId = salesRecordEntity.salesId;
        reportEntity.farmerName = salesRecordEntity.name;
        reportEntity.fat = salesRecordEntity.fat;
        reportEntity.snf = salesRecordEntity.snf;
        reportEntity.rate = salesRecordEntity.rate;
        reportEntity.amount = salesRecordEntity.amount;
        reportEntity.collectionType = Util.REPORT_TYPE_SALES;
        reportEntity.milkType = salesRecordEntity.milkType;
        reportEntity.quantity = salesRecordEntity.Quantity;
        reportEntity.postDate = salesRecordEntity.postDate;
        reportEntity.postShift = salesRecordEntity.postShift;
        reportEntity.time = salesRecordEntity.time;
        if (amcuConfig.getPrinter().equalsIgnoreCase(com.devapp.devmain.util.AppConstants.PRINTER_HINDI)) {


            FormatHindiLocal formatHindiLocal = new FormatHindiLocal();

            message = "\n" + formatHindiLocal.receiptFormat(SalesActivity.this, reportEntity);

        } else if (amcuConfig.getPrinter().equalsIgnoreCase(com.devapp.devmain.util.AppConstants.PRINTER_TAMIL)) {
            FormatHindiLocal formatHindiLocal = new FormatHindiLocal();

            message = "\n" + formatHindiLocal.receiptFormatTamil(SalesActivity.this, reportEntity);
        } else {
            if (amcuConfig.getSalesFSEnable())
                message = "\n" + "Id: " + etFarmerId.getText().toString()
                        + printDataWithNameFatRate();
            else
                message = printDataWithNameRate();
        }


        String printData = "\n" + "Date: "
                + mSmartCCUtil.changeDateFormat(
                salesRecordEntity.postDate, "yyyy-MM-dd",
                "dd-MMM-yyyy") + "/" +
                SmartCCUtil.getAlternateShift(salesRecordEntity.postShift)
                + " Time: " + TIME + message + "\n";
        Log.v("Sales", printData);
        return printData;
    }

    private String printDataWithNameFatRate() {
        return printDataWithNameQty() +
                printDataWithFatSnf() +
                printDataWithRateAmount();
    }

    private String printDataWithNameRate() {
        return printDataWithNameQty() +
                printDataWithRateAmount();
    }

    private String printDataWithNameQty() {
        return "\n" + "Name: " + etFarmerName.getText().toString() + "\n"
                + "Cattle Type: " + salesRecordEntity.getMilkType() + "\n"
                + "QTY: " + weightRecord + getTheUnit() + "\n";
    }

    private String printDataWithRateAmount() {
        return "RATE: " + strRate + " Rs" + "\n"
                + "Amount: " + amountRecord + " Rs" + "\n";
    }

    private String printDataWithFatSnf() {
        return "FAT: " + decimalFormatFS.format(Double.parseDouble(etFat.getText().toString())) + "\n" +
                "SNF: " + decimalFormatFS.format(Double.parseDouble(etSnf.getText().toString())) + "\n";
    }

    public void setDecimalRoundOff() {
        if (amcuConfig.getDecimalRoundOffAmount() == 0) {
            decimalFormatAmount = new DecimalFormat("#0");

        } else if (amcuConfig.getDecimalRoundOffAmount() == 1) {
            decimalFormatAmount = new DecimalFormat("#0.0");

        } else if (amcuConfig.getDecimalRoundOffAmount() == 2) {
            decimalFormatAmount = new DecimalFormat("#0.00");

        } else if (amcuConfig.getDecimalRoundOffAmount() == 3) {
            decimalFormatAmount = new DecimalFormat("#0.000");
        }

        if (amcuConfig.getDecimalRoundOffWeigh() == 0) {
            decimalFormatWeight = new DecimalFormat("#0");
        } else if (amcuConfig.getDecimalRoundOffWeigh() == 1) {

            decimalFormatWeight = new DecimalFormat("#0.0");

        } else if (amcuConfig.getDecimalRoundOffWeigh() == 2) {
            decimalFormatWeight = new DecimalFormat("#0.00");
        } else if (amcuConfig.getDecimalRoundOffWeigh() == 3) {
            decimalFormatWeight = new DecimalFormat("#0.000");
        }

        if (amcuConfig.getDecimalRoundOffRate() == 0) {
            decimalFormatRate = new DecimalFormat("#0");
        } else if (amcuConfig.getDecimalRoundOffRate() == 1) {
            decimalFormatRate = new DecimalFormat("#0.0");
        } else if (amcuConfig.getDecimalRoundOffRate() == 2) {
            decimalFormatRate = new DecimalFormat("#0.00");
        } else if (amcuConfig.getDecimalRoundOffRate() == 3) {
            decimalFormatRate = new DecimalFormat("#0.000");
        }

        if (amcuConfig.getDecimalRoundOffAmountCheck()) {
            decimalFormatAmount.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormatAmount.setRoundingMode(RoundingMode.FLOOR);
        }

        if (amcuConfig.getDecimalRoundOffRateCheck()) {
            decimalFormatRate.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormatRate.setRoundingMode(RoundingMode.FLOOR);
        }

        if (amcuConfig.getDecimalRoundOffWeightCheck()) {
            decimalFormatWeight.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormatWeight.setRoundingMode(RoundingMode.FLOOR);
        }

    }

    public void checkTimeout() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                if (checkForTimeOut(System.currentTimeMillis(), time2)) {
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

                enableReject("Timeout");
                closeMAConnection();

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

    public void setHeader() {


        tvHeader.setText(session.getSocietyName());
    }

    public void setReportData(boolean isFunctionKeyPressed) {
        String status = "Accept";
        String milkType = session.getMilkType();
        String ratec = "0.00";
        try {
            ratec = decimalFormatRate.format(Double.parseDouble(etRate
                    .getText().toString()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        status = "Accept";
        String date = Util.getTodayDateAndTime(1, SalesActivity.this, true);
        String records = null;

        if (isFunctionKeyPressed) {
            records = session.getFarmerID() + "=" + session.getFarmerName() + "=" + "barcode" + "=" + session.getCollectionID() + "="
                    + etFat.getText().toString() + "=" + etSnf.getText().toString() + "=" + ratec + "=" + "0.00" + "=" + "0.00" + "=" + "84.0"
                    + "=" + "0.0" + "=" + date + "=" + currentShift + "=" + startTime + "=" + milkType + "=" + Util.getDateInLongFormat(date) + "="
                    + "0000" + "=" + String.valueOf(calendar.getTimeInMillis()) + "=" +
                    String.valueOf(calendar.getTimeInMillis()) + "="
                    + String.valueOf(calendar.getTimeInMillis()) + "=" + "0.0" + "=" + status + "=" + isAutoManual + "=" + "0.00" + "=" + isMaAutoManual + "=" + isWsManual;
        } else {
            records = session.getFarmerID() + "=" + session.getFarmerName() + "=" + "barcode" + "=" + session.getCollectionID() + "="
                    + etFat.getText().toString() + "=" + etSnf.getText().toString() + "=" + ratec + "=" + weightRecord + "=" + amountRecord + "=" + "84.0"
                    + "=" + "0.0" + "=" + date + "=" + currentShift + "=" + startTime + "=" + milkType + "=" + Util.getDateInLongFormat(date) + "="
                    + "0000" + "=" + String.valueOf(calendar.getTimeInMillis()) + "=" +
                    String.valueOf(calendar.getTimeInMillis()) + "="
                    + String.valueOf(calendar.getTimeInMillis()) + "=" + "0.0" + "=" + status + "=" + isAutoManual + "=" + "0.00" + "=" + isMaAutoManual + "=" + isWsManual;

        }
        session.setReportData(records);
    }

    public void onFinish() {

        Intent intent = new Intent(SalesActivity.this, FarmerScannerActivity.class);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

    public void closeMAConnection() {
        try {
            if (maManager != null) {
                maManager.stopReading();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeWSConnection() {
        if (wsManager != null) {
            wsManager.closeConnection();
        }
    }

    public void setMaEnable() {
        if (amcuConfig.getSalesFSEnable()) {
            etFat.setSelection(0);
            ifMaManual();
        } else {
            waitingForMAData = false;
            closeMAConnection();
            sbMessage = new StringBuilder();
        }
    }

    public void setWsEnable() {
        if (!amcuConfig.getDisableManualForDispatchValue()) {
            etMilkWeight.setEnabled(true);
        }
        etMilkWeight.setSelection(0);
        etMilkWeight.requestFocus();
        textChangeFunction();


    }

    public void textChangeFunction() {
        etMilkWeight.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)
                        || (event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    if (btnReject.isEnabled()) {
                        onClickButtonReject();
                    }
                    return true;
                }
                return false;
            }
        });

        etMilkWeight.addTextChangedListener(new TextWatcher() {
            String original = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                original = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isRateEnable = false;
                isNameEnable = false;
                isSnfEnable = false;
                isFatEnable = false;

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (amcuConfig.getDisableManualForDispatchValue()) {
                    /*if (!s.toString().matches(AppConstants.Regex.NUMBER_DECIMAL)) {
                        etMilkWeight.setText(original);
                        return;
                    }*/
                }
                if (checkForValidData(true)
                        || !amcuConfig.getSalesFSEnable()) {
                    String weight = etMilkWeight.getText().toString().replace(" ", "");
                    rateStr = etRate.getText().toString().replace(" ", "");
                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(weight, rateStr);
                } else {
                    setWeightAndAmountManually("0", "0");
                    btnReject.setEnabled(false);
                }

            }
        });

        etRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                isNameEnable = false;
                isFatEnable = false;
                isSnfEnable = false;
                isRateEnable = true;

                if (checkForValidData(true)) {
                    String weight = etMilkWeight.getText().toString().replace(" ", "");

                    rateStr = etRate.getText().toString().replace(" ", "");

                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(weight, rateStr);
                } else {
                    setWeightAndAmountManually("0", "0");
                    btnReject.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        etMilkWeight.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    setCursorOnText(etRate);

                    if (!etRate.isEnabled() && btnReject.isEnabled()) {
                        onClickButtonReject();
                    }

                    return true;
                }
                return false;
            }
        });

        etRate.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    if (btnReject.isEnabled()) {
                        onClickButtonReject();
                    }
                    if ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {

                    }
                    return true;
                }
                return false;
            }
        });


    }

    public void checkForManualMAorWS(int keyValue) {

        if ((etFat.hasFocus() || etSnf.hasFocus() || etMilkWeight.hasFocus())) {


            if ((!isRateEnable && !isNameEnable) &&
                    ((btnReject.getText().toString().equalsIgnoreCase("Read weight"))
                            || waitingForMAData)) {
                // Util.alphabetValidation(etFat, Util.ONLY_DECIMAL, SalesActivity.this, 6);
                // Util.alphabetValidation(etSnf, Util.ONLY_DECIMAL, SalesActivity.this, 6);
                isMaAutoManual = "Manual";
                waitingForMAData = false;
                closeMAConnection();
                btnReject.setEnabled(true);
                btnReject.setText("Read weight");


                if (etFat.getText().toString().length() < 1 && etSnf.getText().toString().length() < 1) {
                    Toast.makeText(SalesActivity.this, "Enter fat/snf manually!", Toast.LENGTH_SHORT).show();
//                etFat.requestFocus();
                    if (!etFarmerId.hasFocus()) {
                        etFat.setText(String.valueOf(keyValue));
                        etFat.setCursorVisible(true);
                        int position = etFat.length();
                        etFat.setSelection(position);
                    }
                }

            } else if ((!isFatEnable && !isSnfEnable && !isNameEnable && !isRateEnable)
                    && !waitingForMAData && !btnReject.getText().toString().equalsIgnoreCase("Read weight") && !isReject) {

                if (checkForValidData(false) && !isWsManual.equalsIgnoreCase("Manual")) {
                    closeWSConnection();
                    // Util.alphabetValidation(etMilkWeight, Util.ONLY_DECIMAL, SalesActivity.this, 8);
                    textChangeFunction();
                    etMilkWeight.requestFocus();
                    etMilkWeight.setText(String.valueOf(keyValue));
                    etFat.setCursorVisible(true);
                    int pos = etMilkWeight.length();
                    etMilkWeight.setSelection(pos);
                    isWsManual = "Manual";
                    Toast.makeText(SalesActivity.this, "Enter weight manually!", Toast.LENGTH_SHORT).show();
                }
            } else if (isNameEnable) {
                enableName();
            }
        }
    }

    public boolean checkForValidData(boolean allValid) {

        double dfat = -1, dSnf = -1, dQuantity = -1, dRate = -1;
        String snf = etSnf.getText().toString().replace(" ", "");
        String fat = etFat.getText().toString().replace(" ", "");
        String weight = etMilkWeight.getText().toString().replace(" ", "");
        String rate = etRate.getText().toString().replace(" ", "");

        try {
            dfat = Double.parseDouble(fat);
            dSnf = Double.parseDouble(snf);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            dQuantity = Double.parseDouble(weight);
            dRate = Double.parseDouble(rate);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (!allValid) {
            if (dfat > -1 && dSnf > -1 && isFatSnfValid(dfat, dSnf)) {
                return true;
            } else {
                return false;
            }
        } else {
            if (dfat > -1 && dSnf > -1 && dQuantity > 0 && dRate > 0) {
                return true;
            } else {
                if (!amcuConfig.getSalesFSEnable() && dRate > 0 && dQuantity > 0) {
                    return true;
                } else {
                    return false;
                }

            }
        }
    }

    private boolean isFatSnfValid(double fat, double snf) {
        if (session.getMilkType().equalsIgnoreCase("Cow")) {
            if (fat > amcuConfig.getKeyMaxFatRejectCow() || snf > amcuConfig.getKeyMaxSnfRejectCow())
                return false;
        } else if (session.getMilkType().equalsIgnoreCase("Buffalo")) {
            if (fat > amcuConfig.getKeyMaxFatRejectBuff() || snf > amcuConfig.getKeyMaxSnfRejectBuff())
                return false;
        } else if (session.getMilkType().equalsIgnoreCase("Mixed"))
            if (fat > amcuConfig.getKeyMaxFatRejectBuff() || snf > amcuConfig.getKeyMaxSnfRejectBuff())
                return false;
        return true;
    }

    public void ifMaManual() {
        etFat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isRateEnable = false;
                isNameEnable = false;
                isFatEnable = true;
                isSnfEnable = false;

            }

            @Override
            public void afterTextChanged(Editable s) {


                if (checkForValidData(false)) {
                    isMaAutoManual = "Manual";
                    waitingForMAData = false;
                    closeMAConnection();
                    btnReject.setEnabled(true);

                    btnReject.setText("Read weight");

                }
            }
        });

        etSnf.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isRateEnable = false;
                isNameEnable = false;
                isFatEnable = false;
                isSnfEnable = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

                if (checkForValidData(false)) {
                    isMaAutoManual = "Manual";
                    waitingForMAData = false;
                    closeMAConnection();
                    btnReject.setEnabled(true);

                    btnReject.setText("Read weight");

                }

            }
        });

        etFat.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_UP &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    setCursorOnText(etSnf);
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
                        (event.getAction() == KeyEvent.ACTION_UP &&
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
    }


    //Alert dialog if mulitiple cans

    public String getTheUnit() {

        if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {

            return " L";

        } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            return " Kg";
        } else if (amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            return " Kg";
        } else {
            return " L";
        }

    }

    public void setVisibilityForId() {
        if (!amcuConfig.getSalesFSEnable()) {
//            trFarmId.setVisibility(View.GONE);
            tvFarmerId.setVisibility(View.GONE);
            etFarmerId.setVisibility(View.GONE);
            etFarmerName.requestFocus();
        }

    }

    public void enableName() {

//        Util.alphabetValidation(etFarmerName, Util.ONLY_ALPHABET, SalesActivity.this, 30);


    }

    public void enableOrDisableFS() {

        if (!amcuConfig.getSalesFSEnable()) {
            etFat.setText("0.00");
            etSnf.setText("0.00");
            trSnf.setVisibility(View.GONE);
            if (trFat != null) {
                trFat.setVisibility(View.GONE);
            }
            etMilkWeight.setEnabled(true);
            etMilkWeight.setFocusable(true);
            textChangeFunction();
        } else {
            if (amcuConfig.getDisableManualForDispatchValue()) {
                etFat.setEnabled(false);
                etSnf.setEnabled(false);
                etMilkWeight.setEnabled(false);
            } else {
                etFat.setEnabled(true);
                etSnf.setEnabled(true);
            }
            etMilkWeight.setEnabled(false);
            trSnf.setVisibility(View.VISIBLE);
            if (trFat != null) {
                trFat.setVisibility(View.VISIBLE);
            }
        }
        setRateAsPerCattleType();
    }

    public void setRateAsPerCattleType() {
        if (session.getMilkType().equalsIgnoreCase("Cow")) {
            etRate.setText(decimalFormatRate.format(Double.parseDouble(amcuConfig.getSalesCowRate())));
        } else if (session.getMilkType().equalsIgnoreCase("Buffalo")) {
            etRate.setText(decimalFormatRate.format(Double.parseDouble(amcuConfig.getSalesBuffRate())));
        } else if (session.getMilkType().equalsIgnoreCase("Mixed")) {
            etRate.setText(decimalFormatRate.format(Double.parseDouble(amcuConfig.getSalesMixedRate())));
        }
    }

    public void getCollectionQuantity() {

        collectionQuantity = Util.enableSales(SalesActivity.this,
                session.getMilkType(), null, null);

    }


    public String addToDatabase(SalesRecordEntity entity) {


        String dbError = null;

        if (entity != null) {
            Util.setDailyDateOrShift(
                    Util.getTodayDateAndTime(7, SalesActivity.this,
                            true),
                    entity.postShift);
            try {
                long columnId = salesRecordDao.saveOrUpdate(entity);
                entity.setPrimaryKeyId(columnId);

            } catch (Exception e) {
                e.printStackTrace();
                dbError = "Error while creating sales record.";
            }

        } else {
            dbError = "Error while creating sales record.";
        }
        return dbError;
    }

    private void printReceipt() {
        printManager.print(receiptFormat(), PrinterManager.salesReceipt, null, null, null);

    }

    private void setMaEntity() {
        if (milkAnalyserEntity == null &&
                etFat.getText().toString().trim().length() > 0)

        {
            milkAnalyserEntity = new MilkAnalyserEntity();
            milkAnalyserEntity.fat =
                    validationHelper.getDoubleFromString(
                            etFat.getText().toString().trim(), 0);
            milkAnalyserEntity.snf =
                    validationHelper.getDoubleFromString(
                            etSnf.getText().toString().trim(), 0);
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


}

