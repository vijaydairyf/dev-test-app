package com.devapp.devmain.macollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.devapp.kmfcommon.UserSelectionActivity;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.devicemanager.DeviceParameters;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.devicemanager.WeighingScaleManager;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.RejectionEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
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
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ChillingCollectionActivityV2 extends Activity implements AdapterView.OnItemSelectedListener, OnClickListener {

    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static int MILK_ANALYSER_DRIVER = 0;
    public static int WEIGHING_SCALE_DRIVER = 1;
    private final String TAG = ChillingCollectionActivityV2.class.getSimpleName();
    public Context context = this;
    public String milkAnanalyserTime, startTime, weighTime;
    public String ekoRduMsg;
    public boolean visible = false;
    public boolean isEnable;
    public String newRDUMSG;
    public int mabaudRate = 1200, rduBaudRate = 9600, weighingBaudrate = 9600;
    public String rduMsg;
    public String currentShift;
    public long tempReportColId = -1;
    public String dbError = null;
    public boolean isReject = false;
    public String comingFrom;
    public double totalWeightFromUserMCA;
    public int numberOfCans;
    public String rateMode = Util.RATE_MODE_AUTO;
    public String cattleType = "COW";
    RejectionEntity dataBaseRejectEnt;
    String spinnerItem;
    UsbManager mUsbManager;
    boolean waitingForMAData = true;
    boolean ignoreInitialWSData = true;
    boolean isManualSelected = false;
    Button btnReject, btnWeight, btnAutoManual;
    StringBuilder sbMessage = new StringBuilder();
    EditText etFat, etSnf, etMilkWeight, etFarmerName, etFarmerId, etRate,
            etAmount, etClr, etSID;
    String incentive = "00.00";
    double weightRecord, amountRecord, bonusAmount;
    TextView tvFatAuto, tvSnfAuto, tvRate, tvMilkWeight, tvQualityOfMilk, txtCLR, txtSID;
    TextView tvHeader;
    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatWeight = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatRate = new DecimalFormat("#0.00");
    //    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
//    DecimalFormat decimalFormatClr = new DecimalFormat("#0");
    DecimalFormat decimalFormatFS;
    DecimalFormat decimalFormatClr;
    DecimalFormat decimalFormatFat;
    DecimalFormat decimalFormatSNF;
    DecimalFormat decimalFormatProtein;
    DecimalFormat decimalFormatRateProtein;
    DecimalFormat decimalFormatRateFat;
    DecimalFormat decimalFormatRateSNF;
    DecimalFormat decimalFormatRateCLR;
    double doubleRate = 0;
    SessionManager session;
    String totRate, msg;
    String strRate;
    Handler myHandler = new Handler();
    Handler myHandlerReject = new Handler();
    Runnable updateRunnable;
    Runnable updateRunnableReject;
    String isAutoManual = "Manual", addedWater = "0.0", isMaAutoManual = "Auto", isWsManual = "Auto";
    boolean isEKOMILK, isBtnEnable, isCavinCare, isGoldTech, isRduSmart, isEKOMilkUltra,
            isTareDone;
    SampleDataEntity sampleDataEnt;
    AmcuConfig amcuConfig;
    PrinterManager printManager;
    double clr;
    double temp;
    TableRow trFat, trSnf, trWeight, trRate, trAmount, trSID;
    long quantityTime, qualityTime;
    Calendar calendar;
    // TODO:
    // The following flag has been introduced to prevent user from pressing
    // PRINT multiple times and
    // end up with multiple records for the same farmerID.
    // Refer redmine issue #105. Possibly the highest priority issue that a
    // customer will notice
    boolean printEnabled = true;
    boolean isFatSnfPressed;
    String DATE, TIME;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    int countTare = 0;
    boolean isCleaningFailed = false;
    Runnable timeOutRunnable;
    Handler timeOutHandler = new Handler();
    boolean timeOut = false;
    long time2 = System.currentTimeMillis() + 300000L;
    String newVolData, totalData;
    String radioStatus;
    TextView tvPreviousData, tvNewData, tvTotal;
    AlertDialog levelDialog;
    boolean isBuilderEnable;
    boolean isResetDone;
    String prevRecord = "0.00";
    Spinner spinner;
    LinkedHashMap<String, String> spinnerHashMapData;
    String selectedCursorIds;
    String weightManualStatus;
    ReportEntity unCommitedReportEntity;
    ValidationHelper chckValidationHelper = new ValidationHelper();
    TextView tvAmount;
    Future mFuture;
    double kgQuantity = 0, ltrQuantity = 0;
    int milkSID;
    boolean comingFromSID, comingFromNextSIDAlert;
    String maxFat = "0.0", maxSnf = "0.0";
    long tippingStartTime = 0;
    long tippingEndTime = 0;
    boolean isTempRecordAdded = false;
    long collectionTime = 0;
    MaManager maManager;
    DriverConfiguration driverConfiguration;
    boolean maPing, wsPing, rduPing, printerPing;
    private WifiUtility wifiUtility;
    private LinearLayout statusLayout;
    private Handler pingHandler;
    private Runnable pingRunnable;
    private CheckBox cbMa, cbWs, cbRdu, cbPrinter;
    private UsbSerialPort usbSerialPortMilkoTester = null;
    private UsbSerialPort usbSerialPortWeighingScale = null;
    private ExecutorService mExecutor;
    private SerialInputOutputManager mSerialIoManager;
    private WeighingScaleManager weighingScaleManager;
    private SmartCCUtil smartCCUtil;
    private ReportEntity mReportEntity;
    private DeviceParameters maParams;
    private CollectionRecordDao collectionRecordDao;
    private ReportEntity reportEntity;
    private String UMCA_SHIFT;
    private MilkAnalyserEntity mMaEntity;

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

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case SmartCCConstants.MA_CONNECTED:
                    startMaReading();
                    break;
                case SmartCCConstants.WS_CONNECTED:
                    break;
                case SmartCCConstants.RDU_CONNECTED:
                    break;
                case SmartCCConstants.PRINTER_CONNECTED:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getDataFromIntent();

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.allusbdevice_landscape);
        } else {
            setContentView(R.layout.allusbdevice);
        }
        // for SID
        etSID = (EditText) findViewById(R.id.etSId);
        txtSID = (TextView) findViewById(R.id.txtSID);
        etSID.setText("" + milkSID);
        if (comingFromSID) {
            if (milkSID == Util.LAST_SEQ_NUM) {
                etSID.setVisibility(View.GONE);
                txtSID.setVisibility(View.GONE);
            }
        } else if (comingFromNextSIDAlert) {

            etSID.setVisibility(View.VISIBLE);
            txtSID.setVisibility(View.VISIBLE);

        } else {
            etSID.setVisibility(View.GONE);
            txtSID.setVisibility(View.GONE);
        }
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(ChillingCollectionActivityV2.this);
        wifiUtility = new WifiUtility();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try {
            isAutoManual = getIntent().getStringExtra("isAutoOrManual");
        } catch (Exception e) {
            isAutoManual = "Manual";
            e.printStackTrace();
        }

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();

        decimalFormatFS = chooseDecimalFormat.getFatAndSnfFormat();
        decimalFormatFat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        decimalFormatSNF = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        decimalFormatClr = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);
        decimalFormatProtein = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.PROTEIN);
        decimalFormatRateProtein = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.PROTEIN);
        decimalFormatRateFat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.FAT);
        decimalFormatRateSNF = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.SNF);
        decimalFormatRateCLR = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.CLR);


        printManager = new PrinterManager(ChillingCollectionActivityV2.this);
        tvQualityOfMilk = (TextView) findViewById(R.id.tvQualityOfMilk);
        etFarmerId = (EditText) findViewById(R.id.etFarmerId);
        etFarmerName = (EditText) findViewById(R.id.etFarmerName);
        etRate = (EditText) findViewById(R.id.etRate);
        etAmount = (EditText) findViewById(R.id.etAmount);
        etSnf = (EditText) findViewById(R.id.etSnf);
        etMilkWeight = (EditText) findViewById(R.id.etMilkweight);
        etMilkWeight.setEnabled(false);
        etFat = (EditText) findViewById(R.id.etFat);
        btnReject = (Button) findViewById(R.id.btnReject);
        btnWeight = (Button) findViewById(R.id.btnNext);
        btnAutoManual = (Button) findViewById(R.id.btnAutoManual);

        tvFatAuto = (TextView) findViewById(R.id.tvFatAuto);
        tvSnfAuto = (TextView) findViewById(R.id.tvSnfAuto);
        tvHeader = (TextView) findViewById(R.id.tvheader);
        tvMilkWeight = (TextView) findViewById(R.id.tvMilkWeight);

        txtCLR = (TextView) findViewById(R.id.txtCLR);
        etClr = (EditText) findViewById(R.id.etClr);

        btnAutoManual.setVisibility(View.GONE);
        btnReject.setOnClickListener(this);
        btnWeight.setOnClickListener(this);
        btnAutoManual.setOnClickListener(this);
        setHeader();

        trFat = (TableRow) findViewById(R.id.trFat);
        trSnf = (TableRow) findViewById(R.id.trSnf);
        trWeight = (TableRow) findViewById(R.id.trMilkWeight);
        trRate = (TableRow) findViewById(R.id.trRate);
        trAmount = (TableRow) findViewById(R.id.trAmount);
        cbMa = (CheckBox) findViewById(R.id.cb_ma);
        cbWs = (CheckBox) findViewById(R.id.cb_ws);
        cbRdu = (CheckBox) findViewById(R.id.cb_rdu);
        cbPrinter = (CheckBox) findViewById(R.id.cb_printer);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        pingHandler = new Handler();

        tvRate = (TextView) findViewById(R.id.tvRate);
        //adding spinner data*/
        spinner = (Spinner) findViewById(R.id.spinnerQualityOfMilk);

        spinner.setVisibility(View.VISIBLE);
        tvQualityOfMilk.setVisibility(View.VISIBLE);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        addingSpinnerData("na");
        etFat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (0 != etFat.getText().length()) {
                    String spnId = etFat.getText().toString();
                    addingSpinnerData(spnId);
                } else {
                    addingSpinnerData("na");
                }
            }
        });


        session.setMilkReject("NO");
        btnReject.setText("Print");
        // dataBaseRejectEnt = Util.getRejectEnt(ChillingCollectionActivityV2.this);

        boolean isFarm = true;

        try {
            isFarm = getIntent().getBooleanExtra("isFarmer", isFarm);

            sampleDataEnt = (SampleDataEntity) getIntent()
                    .getSerializableExtra("SampleDataEnt");

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (comingFromNextSIDAlert) {
            session.setFarmerID(getIntent().getStringExtra("ALERT_CID"));
            session.setFarmerName(getIntent().getStringExtra("ALERT_CID_NAME"));
        }

        etFarmerId.setText(session.getFarmerID());
        etFarmerName.setText(session.getFarmerName());
        if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) {
            etMilkWeight.setText("" + totalWeightFromUserMCA);
        }
        // This has been added to disallow user from pressing PRINT
        // multiple times in the typical Milk Accepted scenario.
        printEnabled = true;
        btnReject.setEnabled(false);
        registerBroadcastReceivers();
        setDisableInitilization();
        onCreateView();
        getViewForFATSNFCLR(tvSnfAuto, txtCLR, etSnf, etClr);
        setStatusLayout();
    }


    private void displayDeviceStatus() {

        cbMa.setChecked(maPing);
        cbWs.setChecked(wsPing);
        cbRdu.setChecked(rduPing);
        cbPrinter.setChecked(printerPing);
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
                    case DeviceName.MA1:
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
    public void onStart() {

        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        reportEntity = new ReportEntity();

        if (amcuConfig.getSmartCCFeature()) {
            disableForThrimula();
        }

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        // decimalFormatFS = chooseDecimalFormat.getFatAndSnfFormat();

        weighingScaleManager = new WeighingScaleManager(ChillingCollectionActivityV2.this);
        smartCCUtil = new SmartCCUtil(ChillingCollectionActivityV2.this);

        rduBaudRate = amcuConfig.getRdubaudrate();
        weighingBaudrate = amcuConfig.getWeighingbaudrate();
        //creating calendar instance and setting indian time zone
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        setDecimalFormatForClr();

        startTime = Util.getTodayDateAndTime(3, ChillingCollectionActivityV2.this, true);
        setDecimalRoundOff();
        currentShift = Util.getCurrentShift();
        String ma = amcuConfig.getMA();
        mabaudRate = amcuConfig.getMABaudRate();
        String rdu = amcuConfig.getRDU();
        sbMessage = new StringBuilder();
        driverConfiguration = new DriverConfiguration();
        /*maParams = new DeviceParameters(saveSession.getMA(), saveSession.getMABaudRate(),
                driverConfiguration.getParity(saveSession.getMaParity()),
                driverConfiguration.getStopBits(saveSession.getMaStopBits()),
                driverConfiguration.getDataBits(saveSession.getMaDataBits()));*/
        maManager = MAFactory.getMA(DeviceName.MILK_ANALYSER, ChillingCollectionActivityV2.this);
        if (maManager != null)
            maManager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (maEntity != null) {
                                mMaEntity = maEntity;
                                waitingForMAData = false;
                                closeMAConnection();
                                if (!smartCCUtil.validateFatAndSnfForZeroValues(maEntity.fat, maEntity.snf)) {
                                    Util.displayErrorToast("Invalid data, Press F10 and reset the MA", ChillingCollectionActivityV2.this);

                                    onFinish();
                                } else {
                                    etFat.setText(String.valueOf(maEntity.fat));
                                    etSnf.setText(String.valueOf(maEntity.snf));
                                    etClr.setText(String.valueOf(maEntity.clr));
                                    afterGettingMaData(maEntity.fat, maEntity.snf);
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
                            Util.displayErrorToast("Invalid data, Press F10 and reset the MA", ChillingCollectionActivityV2.this);
                            onFinish();
                        }
                    });
                }
            });
        if (ma.equalsIgnoreCase("EKOMILK ULTRA PRO")) {
            isEKOMilkUltra = true;
        } else if (ma.equalsIgnoreCase("EKOMILK")) {
            isEKOMILK = true;

        }
        //for RDU
        if (rdu.equalsIgnoreCase("SMART")) {
            isRduSmart = true;
        } else if (rdu.equalsIgnoreCase("HATSUN")) {
            isRduSmart = false;
        }
        //for weighing scale

        if (amcuConfig.getWeighingScale().equalsIgnoreCase("EVEREST")) {
            isCavinCare = true;
        } else if (amcuConfig.getWeighingScale().equalsIgnoreCase("GOLDTECH")) {
            isGoldTech = true;
        } else {
            isCavinCare = false;
        }
        initializeReportEntity();
        if (null != unCommitedReportEntity && unCommitedReportEntity.reportType.equalsIgnoreCase(Util.RECORD_STATUS_INCOMPLETE)) {
            waitingForMAData = true;
            ignoreInitialWSData = true;
            setMaEnable();
            startMaReading();

        }
        //To check last temp record which was getting minimized
        else if (unCommitedReportEntity != null && unCommitedReportEntity.fat != 0 && unCommitedReportEntity.snf != 0) {
            etFat.setText(String.valueOf(unCommitedReportEntity.fat));
            etSnf.setText(String.valueOf(unCommitedReportEntity.snf));
            etClr.setText(String.valueOf(unCommitedReportEntity.clr));
            mMaEntity = new MilkAnalyserEntity(unCommitedReportEntity);
            etFat.setEnabled(false);
            etSnf.setEnabled(false);
            etClr.setEnabled(false);
            etSID = (EditText) findViewById(R.id.etSId);
            txtSID = (TextView) findViewById(R.id.txtSID);
            txtSID.setVisibility(View.VISIBLE);
            etSID.setVisibility(View.VISIBLE);
            etSID.setText("" + unCommitedReportEntity.sampleNumber);
            UMCA_SHIFT = unCommitedReportEntity.postShift;

            if (unCommitedReportEntity.sampleNumber == Util.LAST_SEQ_NUM) {
                txtSID.setVisibility(View.GONE);
                etSID.setVisibility(View.GONE);
            }
            // from intent
            weightManualStatus = unCommitedReportEntity.quantityMode;
            //weight
            isWsManual = unCommitedReportEntity.quantityMode;
            //barcode entry
            isAutoManual = unCommitedReportEntity.manual;
            //milk anly
            isMaAutoManual = unCommitedReportEntity.qualityMode;

            tippingEndTime = unCommitedReportEntity.tippingEndTime;
            tippingStartTime = unCommitedReportEntity.tippingStartTime;


            waitingForMAData = false;
            ignoreInitialWSData = true;
            if (unCommitedReportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
                session.setIsChillingCenter(true);
            }

            double weight = 0;
            numberOfCans = unCommitedReportEntity.numberOfCans;

            try {
                weight = Double.valueOf(unCommitedReportEntity.quantity);
                kgQuantity = Double.valueOf(unCommitedReportEntity.kgWeight);
                ltrQuantity = Double.valueOf(unCommitedReportEntity.ltrsWeight);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (weight > 0) {
                weightRecord = weight;
                etMilkWeight.setText(String.valueOf(weightRecord));
            }

            if (checkCurdMilkQuality()) {
                btnReject.setText("Enter Manual Rate");
                etRate.setText(String.valueOf(unCommitedReportEntity.rate));
             /*   etRate.requestFocus();
                etRate.setEnabled(true);
             */
                btnReject.setEnabled(true);
                textChangeFunction();


            } else {

                evaluateAcceptOrReject();
                if (amcuConfig.getWsManual() && !isReject) {
                    btnReject.setEnabled(false);
                    setWsEnable();
                } else if (isReject || session.getIsSample() ||
                        Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                    btnReject.setEnabled(true);
                }
            }


        } else {
            waitingForMAData = true;
            ignoreInitialWSData = true;
            setMaEnable();
            startMaReading();
        }

        setMaxFatAndSnf();
        setDisable();
        super.onStart();
    }

    public void onCreateView() {
        if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            btnReject.setText("Back");
            btnReject.setEnabled(true);
        } else if (!session.getIsSample()) {
            setMaEnable();
        } else if (session.getIsSample()
                && (Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            trAmount.setVisibility(View.GONE);
            tvRate.setVisibility(View.GONE);
            etRate.setVisibility(View.GONE);
            btnReject.setText("OK");
        } else if (session.getIsSample()
                && (Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {

            trSnf.setVisibility(View.GONE);
            trFat.setVisibility(View.GONE);
            trAmount.setVisibility(View.GONE);
            trWeight.setVisibility(View.GONE);
            tvRate.setVisibility(View.GONE);
            etRate.setVisibility(View.GONE);

            etMilkWeight.setVisibility(View.GONE);
            tvMilkWeight.setVisibility(View.GONE);
            if (Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                btnReject.setText("Cleaning....");
            } else {
                btnReject.setText("Rinsing.....");
            }

        } else if (session.getIsSample()) {
            trWeight.setVisibility(View.GONE);
            trAmount.setVisibility(View.GONE);
            tvRate.setVisibility(View.GONE);
            etRate.setVisibility(View.GONE);
            etMilkWeight.setVisibility(View.GONE);
            tvMilkWeight.setVisibility(View.GONE);
            btnReject.setText("OK");
        } else {
            Toast.makeText(ChillingCollectionActivityV2.this, "Invalid Code", Toast.LENGTH_SHORT).show();
            if (comingFromSID || comingFromNextSIDAlert) {
                gotoFarmerScannerActivity();
            } else {
                onFinish();
            }
        }
    }

    public void getViewForFATSNFCLR(View tsnf, View tclr, View esnf, View eclr) {
        tvSnfAuto = (TextView) tsnf;
        txtCLR = (TextView) tclr;
        etSnf = (EditText) esnf;
        etClr = (EditText) eclr;
        boolean allView = amcuConfig.getFATSNFCLR();
        if (allView) {
            tvSnfAuto.setVisibility(View.VISIBLE);
            txtCLR.setVisibility(View.VISIBLE);
            etSnf.setVisibility(View.VISIBLE);
            etClr.setVisibility(View.VISIBLE);
        } else {

            String chilingCenterFSC = amcuConfig.getChillingFATSNFCLR();
            if (chilingCenterFSC.equalsIgnoreCase("FS")) {
                visibilityCheckFS();
            } else {
                visibilityCheckFC();
            }

        }
    }

    public void visibilityCheckFS() {
        tvSnfAuto.setVisibility(View.VISIBLE);
        etSnf.setVisibility(View.VISIBLE);
        etClr.setVisibility(View.GONE);
        txtCLR.setVisibility(View.GONE);
    }

    public void visibilityCheckFC() {
        tvSnfAuto.setVisibility(View.GONE);
        txtCLR.setVisibility(View.VISIBLE);
        etSnf.setVisibility(View.GONE);
        etClr.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnNext: {
                //Not required for this Activity
                //  alertForRemoveCan();
            }
            break;
            case R.id.btnReject: {
                onClickButtonReject();

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
        if (checkCurdMilkQuality()
                && btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate")) {
            if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) {
                etFat.setText("0.0");
                etSnf.setText("0.0");

                if (amcuConfig.getClrRoundOffUpto() == 0) {
                    etClr.setText("0");
                } else {
                    etClr.setText("0.0");
                }

                etFat.setEnabled(false);
                etSnf.setEnabled(false);
                etClr.setEnabled(false);
                etRate.setEnabled(true);
                etRate.setFocusable(true);
                etRate.requestFocus();
                //setQuantityEnable();
                saveIncompleteRecord();

                textChangeFunction();
                setWeightAndAmountManually(weightRecord);

                btnReject.setText("Print");

                try {
                    readWeightData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (btnReject.getText().toString().equalsIgnoreCase("Enter rate")
                || btnReject.getText().toString().equalsIgnoreCase("Rate done")) {

            if (isReject) {
                alertReject();
            } else {
                //Disable rate to edit once again
                etRate.setEnabled(false);
                etRate.setFocusable(false);
                setMilkWeighingState();
                btnReject.setText("Print");

                //Open the weighingscale connection
                try {
                    sbMessage = new StringBuilder();
                    readWeightData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                rateMode = Util.RATE_MODE_MANUAL;
                textChangeFunction();

                setWsEnable();


            }
        } else if (btnReject.getText().toString().equalsIgnoreCase("Read weight")
                || (btnReject.getText().toString().equalsIgnoreCase("Read Rate"))) {

            if (checkForValidData(false)) {
                setDisable();
                //for f8 and smaple
                if (!session.getIsSample() && !Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                    btnReject.setEnabled(false);
                } //for sample milk also wait is rquired
                else if (Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                    btnReject.setEnabled(false);
                }
                waitingForMAData = false;

                evaluateAcceptOrReject();
                setWsEnable();
            } else {
                Toast.makeText(ChillingCollectionActivityV2.this, "Enter valid quality parameters!", Toast.LENGTH_SHORT).show();
            }
        } else if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            if (checkForValidData(false)) {
                onPrintOrReject();
            } else {
                if (comingFromSID || comingFromNextSIDAlert) {
                    gotoFarmerScannerActivity();
                } else {
                    onFinish();
                }
            }
        } else {
            if ((isReject && checkForValidData(false))
                    || (!isReject && checkForValidData(true)) ||
                    Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                    || Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                    || Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                onPrintOrReject();
            } else if ((checkForValidData(false) && session.getIsSample())) {
                onPrintOrReject();
            } else {
                Toast.makeText(ChillingCollectionActivityV2.this, "Enter all valid values!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void gotoNewSessionIDAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChillingCollectionActivityV2.this);
        LayoutInflater inflater = ChillingCollectionActivityV2.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sid_dialogbox, null);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //  Toast.makeText(ChillingCollectionActivityV2.this, "Ok NEXT SID Cliked", Toast.LENGTH_SHORT).show();
                DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
                // last sid return cursor data
                Cursor cursor = dbh.nextSequenceNumber(milkSID, smartCCUtil.getReportFormatDate(), Util.getCurrentShift());
                if (null != cursor && cursor.getCount() == 0) {
                    dialog.dismiss();
                    Toast.makeText(ChillingCollectionActivityV2.this, "No more data available.", Toast.LENGTH_SHORT).show();
                    gotoFarmerScannerActivity();
                } else {
                    if (null != cursor) {
                        cursor.moveToPosition(0);
                        Intent intent = new Intent(getApplicationContext(), ChillingCollectionActivityV2.class);

                        intent.putExtra("COMING_FROM_NEXT_SID", true);
                        intent.putExtra("ALERT_CID", cursor.getString(cursor.getColumnIndex("farmerId")));
                        intent.putExtra("ALERT_CID_NAME", cursor.getString(cursor.getColumnIndex("name")));
                        intent.putExtra("COMING_FROM", "UMCA");
                        intent.putExtra("SELECTED_CURSORID", String.valueOf(cursor.getString(cursor.getColumnIndex("_id"))));


                        // put DB cursor fetch SID for further transaction
                        /*intent.putExtra("MILK_SID", cursor.getInt(cursor.getColumnIndex("repSequenceNumber")));
                        intent.putExtra("TOTALMILK", Double.valueOf(decimalFormatWeight.format(Double.valueOf(cursor.getString(cursor.getColumnIndex("quantity"))))));
                        intent.putExtra("isAutoOrManual", "Manual");// barcode reader data for auto or manual
                        intent.putExtra("NO_OF_CANS", Integer.valueOf(cursor.getString(cursor.getColumnIndex("numberOfCans"))));
                        intent.putExtra("WEIGHT_MANUAL", cursor.getString(cursor.getColumnIndex("repWeightManual"))); // auto or manual entry from weighing scale
                        intent.putExtra("UMCA_SHIFT", cursor.getString(cursor.getColumnIndex("shift")));
                        intent.putExtra("TIPPING_START_TIME", cursor.getLong(cursor.getColumnIndex(
                                DatabaseHandler.KEY_TIPPING_START_TIME)));
                        intent.putExtra("TIPPING_END_TIME", cursor.getLong(cursor.getColumnIndex(
                                DatabaseHandler.KEY_TIPPING_END_TIME)));
                        double tKg = 0, tLt = 0;
                        try {
                            tKg = Double.parseDouble(cursor.getString(36));
                            tLt = Double.parseDouble(cursor.getString(37));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        intent.putExtra("KG_MILK", tKg);
                        intent.putExtra("LTR_MILK", tLt);
                        intent.putExtra("MILK_SID",
                                cursor.getInt(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_SEQUENCE_NUMBER)));
                        intent.putExtra("COLLECTION_TIME", cursor.getLong(
                                cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_TIME_MILLI)));*/

                        mReportEntity = SmartCCUtil.getReportFromCursor(cursor);
                        intent.putExtra("SELECTED_DATA", mReportEntity);

                        String selectMilkType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));
                        session.setMilkType(selectMilkType);
                        new SmartCCUtil(ChillingCollectionActivityV2.this).setRateChart();

                        startActivity(intent);
                        dialog.dismiss();
                    } else {
                        dialog.dismiss();
                        gotoFarmerScannerActivity();
                    }

                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), UserSelectionActivity.class);
                startActivity(intent);
                // finish();
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void onPrintOrReject() {
        btnReject.setEnabled(false);
        btnReject.setFocusable(false);

       /* if (isEKOMilkUltra && isMaAutoManual.equalsIgnoreCase("Auto")) {
            saveSession.setMilkAnalyserPrvData(MilkCollectionManager.ekomilkUltraMessage);
        }*/
        weighTime = Util.getTodayDateAndTime(6, ChillingCollectionActivityV2.this, true);
        startTime = Util.getTodayDateAndTime(3, ChillingCollectionActivityV2.this, true);
        quantityTime = calendar.getTimeInMillis();
        getClr(etFat.getText().toString(), etSnf.getText().toString());

        if (timeOut) {
            if (comingFromSID || comingFromNextSIDAlert) {
                gotoFarmerScannerActivity();
            } else {
                onFinish();
            }
        } else if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {

            if (etFat.getText().toString() != null
                    && etFat.getText().toString().replace(" ", "").length() > 0) {
                getFullMsg();
                onAcceptMilk();
            } else {
                tareWSOverSerialManager();
                stopIoManager(WEIGHING_SCALE_DRIVER);
                if (comingFromSID || comingFromNextSIDAlert) {
                    gotoFarmerScannerActivity();
                } else {
                    onFinish();
                }
            }
        } else if (isReject) {
            // Milk rejected scenario;
            onRejectMilk();
        } else if (session.getIsSample()) {
            if (session.getSampleWeigh()) {
                tareWSOverSerialManager();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopIoManager(WEIGHING_SCALE_DRIVER);

            } else {
                closeMAConnection();
            }
            getMsgForSampleTest();
            addToDatabase();
            if (comingFromSID || comingFromNextSIDAlert) {
                gotoFarmerScannerActivity();
            } else {
                onFinish();
            }
        } else {
            if (true == printEnabled) {
                printEnabled = false;
                getFullMsg();
                onAcceptMilk();
            }
        }
    }
//Writing in sdcards

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        String snf, fat, reject, qty, rate, amt, clr;
        snf = etSnf.getText().toString();
        fat = etFat.getText().toString();
        reject = btnReject.getText().toString();
        qty = etMilkWeight.getText().toString();
        rate = etRate.getText().toString();
        amt = etAmount.getText().toString();
        clr = etClr.getText().toString().trim();

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.allusbdevice_landscape);

        } else {
            setContentView(R.layout.allusbdevice);
        }

        etFarmerId = (EditText) findViewById(R.id.etFarmerId);
        etFarmerName = (EditText) findViewById(R.id.etFarmerName);
        etRate = (EditText) findViewById(R.id.etRate);
        etAmount = (EditText) findViewById(R.id.etAmount);
        etSnf = (EditText) findViewById(R.id.etSnf);
        etMilkWeight = (EditText) findViewById(R.id.etMilkweight);
        etFat = (EditText) findViewById(R.id.etFat);
        tvHeader = (TextView) findViewById(R.id.tvheader);
        btnReject = (Button) findViewById(R.id.btnReject);
        btnWeight = (Button) findViewById(R.id.btnNext);
        etClr = (EditText) findViewById(R.id.etClr);
        //spinner = (Spinner)findViewById(R.id.spinnerQualityOfMilk);
        etFarmerId.setText(session.getFarmerID());
        etFarmerName.setText(session.getFarmerName());
        tvQualityOfMilk = (TextView) findViewById(R.id.tvQualityOfMilk);
        setHeader();
        etRate.setText(rate);
        etAmount.setText(amt);
        etSnf.setText(snf);
        etFat.setText(fat);

        //etClr.setText( Util.getCLR(fat,snf,this));
        etClr.setText(clr);
        etMilkWeight.setText(qty);
        btnReject.setText(reject);
        btnReject.setEnabled(isBtnEnable);
        if (isBtnEnable) {
            btnReject.requestFocus();
        }
        if (amcuConfig.getMA().equalsIgnoreCase("EKOMILK ULTRA")) {
            isEKOMilkUltra = true;
            btnWeight.setVisibility(View.VISIBLE);
            btnWeight.setText("Read Weight");
        }
        setDisable();
        // Spinner element
        spinner = (Spinner) findViewById(R.id.spinnerQualityOfMilk);

        spinner.setVisibility(View.VISIBLE);
        tvQualityOfMilk.setVisibility(View.VISIBLE);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        addingSpinnerData("na");
        etFat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (0 != etFat.getText().length()) {
                    String spnId = etFat.getText().toString();
                    addingSpinnerData(spnId);
                } else {
                    addingSpinnerData("na");
                }
            }
        });


    }

    public void addingSpinnerData(String choice) {
        ///adding spinner hash map data
        spinnerHashMapData = new LinkedHashMap<String, String>();

        if (null != choice && choice.equalsIgnoreCase("na")) {
            spinnerHashMapData.put("G", "Good quality (G)");

        } else if (null != choice && (choice.equalsIgnoreCase("0") || choice.equalsIgnoreCase("0.0"))) {
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


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        // end spinner data
    }

    public void setDisable() {
        etFat.setEnabled(false);
        etSnf.setEnabled(false);
        etClr.setEnabled(false);
        etAmount.setEnabled(false);
        etFarmerId.setEnabled(false);
        etFarmerName.setEnabled(false);
        etRate.setEnabled(false);
        if (checkForValidData(false)) {
            setWsEnable();
        } else {
            etMilkWeight.setEnabled(false);
        }

    }

    public void setDisableInitilization() {

        if (!amcuConfig.getMaManual()) {
            etFat.setEnabled(false);
            etSnf.setEnabled(false);
            etClr.setEnabled(false);
        }
        etAmount.setEnabled(false);
        etFarmerId.setEnabled(false);
        etFarmerName.setEnabled(false);
        etRate.setEnabled(false);
        etMilkWeight.setEnabled(false);

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

        spinner.setVisibility(View.VISIBLE);
        tvQualityOfMilk.setVisibility(View.VISIBLE);
        if (null != etMilkWeight)
            etMilkWeight.setEnabled(false);
        DATE = Util.getDateAndTimeRDU(1);
        TIME = Util.getDateAndTimeRDU(0);
        if (etFat.getText().toString().length() < 1) {
            setMaEnable();
        }
    }

    public String getRateFromRateChart(double snf, double fat, double clr) {
        String rate = "0.00";
        MilkAnalyserEntity maEntity = smartCCUtil.getMAEntity(fat, snf, clr);
        rate = smartCCUtil.getRateFromRateChart(maEntity, amcuConfig.getRateChartName());
        //DB close removed;
        if (rate == null) {
            rate = "0.0";
        }

        doubleRate = Double.parseDouble(decimalFormatRate.format(Double
                .parseDouble(rate)));
        etRate.setText(String.valueOf(doubleRate));

        if (etMilkWeight.getText().toString().equalsIgnoreCase("")) {
            etAmount.setText(String.valueOf("0.00"));
        }
        return String.valueOf(doubleRate);

    }


    // startRDU function is for Open the RDU connection, get the proper RDU
    // message as per selected RDU as well as Write on the RDU

    public void setWeightAndAmountManually(double record) {
        double amt, bonusAmt;
        try {

            doubleRate = Double.parseDouble(decimalFormatRate.format(doubleRate));
            amt = doubleRate * record;
            //Calculating bonus amount
            bonusAmt = getBonusAmount() * record;
            bonusAmount = bonusAmt;
            amt = amt + bonusAmt;
            amountRecord = amt;
            //etAmount.setText(amountRecord);
            DecimalFormat decimalFormat = new DecimalFormat("#0.00");
            etAmount.setText(String.valueOf(Util.getAmount(getApplicationContext(), amountRecord, bonusAmount)));
            weightRecord = record;
            if (!btnReject.isEnabled()) {
                isBtnEnable = true;
                btnReject.setEnabled(true);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    private void stopIoManager(int driverType) {

        try {
            if (mSerialIoManager != null) {
                Log.i(TAG, "Stopping io manager ..");
                mSerialIoManager.stop();
                mSerialIoManager = null;
                mFuture.get();

                if (driverType == MILK_ANALYSER_DRIVER) {

                    try {
                        usbSerialPortMilkoTester.close();
                        usbSerialPortMilkoTester = null;
                        mExecutor.shutdownNow();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (driverType == WEIGHING_SCALE_DRIVER) {
                    try {
                        usbSerialPortWeighingScale.close();
                        usbSerialPortWeighingScale = null;
                        mExecutor.shutdownNow();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        /*try {
                stopMaReading();


        } catch (Exception e) {
            e.printStackTrace();
        }*/
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            closeMAConnection();
            storeLastPingValues();
            stopPingService();
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private void storeLastPingValues() {
        amcuConfig.setMaPingValue(maPing);
        amcuConfig.setWsPingValue(wsPing);
        amcuConfig.setRduPingValue(rduPing);
        amcuConfig.setPrinterPingValue(printerPing);
    }

    private void stopPingService() {
//        pingHandler.removeCallbacks(pingRunnable);
    }

    public void getFullMsg() {

        if (checkCurdMilkQuality()) {
            cattleType = setMilkType();
            double rate = 0;

            try {
                rate = Double.valueOf(etRate
                        .getText().toString().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

        } else if (!isReject) {
            cattleType = setMilkType();
        } else if (session.getIsSample()) {

            cattleType = setMilkType();
            doubleRate = 0;
            weightRecord = 0;
            amountRecord = 0;

        } else {
            cattleType = setMilkType();
            // fullMsg.add("Reject");
            doubleRate = 0;
            weightRecord = 0;
            amountRecord = 0;
        }


    }

    public void getMsgForSampleTest() {
        if (etFat.getText().toString().replace(" ", "").length() > 0
                && etSnf.getText().toString().replace(" ", "").length() > 0) {

        } else {
            etFat.setText("0.0");
            etSnf.setText("0.0");
            etClr.setText("0.0");
        }
        cattleType = "Test";
        if (Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            try {
                weightRecord = Double.parseDouble(etMilkWeight.getText().toString());

            } catch (NumberFormatException e) {
                weightRecord = 0;
                e.printStackTrace();
            }
        } else {
            weightRecord = 0;
        }
    }

    public void onAcceptMilk() {

        dbError = addToDatabase();
        tareWSOverSerialManager();

        if (dbError != null) {
            Util.displayErrorToast("Db error occurred", ChillingCollectionActivityV2.this);
            setReportData(false);
            afterDbError();
        }
        writeOnSDCard();
        AfterAddingDatabaseTable();
        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        // last sid return cursor data
        Cursor cursor = dbh.nextSequenceNumber(milkSID,
                Util.getTodayDateAndTime(1, ChillingCollectionActivityV2.this, true), Util.getCurrentShift());
        if (null != cursor && cursor.getCount() == 0) {
            Toast.makeText(ChillingCollectionActivityV2.this, "No more data available.", Toast.LENGTH_SHORT).show();
            gotoFarmerScannerActivity();
        } else if (comingFromSID || comingFromNextSIDAlert) {
            milkSID = mReportEntity.sampleNumber;
            if (milkSID != 0)
                gotoNewSessionIDAlert();
            else
                gotoFarmerScannerActivity();
        } else {
            onFinish();
        }


    }

    public void AfterAddingDatabaseTable() {

        try {
            startRDU();
        } catch (Exception e) {
            e.printStackTrace();
        }

        printReceipt();
        tareWeighingScale();
        stopIoManager(WEIGHING_SCALE_DRIVER);
    }

    public void startMaReading() {

        if (maManager != null) {
            maManager.startReading();
        }

        if (session.getIsSample()
                && (Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            checkTimeout();
        }

    }

    public void startRDU() {

        RduManager rduManager = RduFactory.getRdu(amcuConfig.getRDU(), ChillingCollectionActivityV2.this);
        if (rduManager != null) {
            rduManager.openConnection();
            ReportEntity reportEntity = getReportEntity(1);
            rduManager.displayReport(reportEntity, amcuConfig.getEnableIncentiveRDU());
        } else {
            Toast.makeText(ChillingCollectionActivityV2.this,
                    "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
        }
    }
//For lactoscan v2

    public String setMilkType() {
        String cattleType;

        String farmerCattleType = null;
        try {
            farmerCattleType = getIntent().getStringExtra("FarmerCattleType");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || session.getIsSample()) {
            cattleType = "TEST";
        } else if (farmerCattleType != null && farmerCattleType.equalsIgnoreCase("both")) {
            cattleType = session.getMilkType();
        } else if (session.getMilkType() == null) {

            double fat1 = 0;
            double fat2 = 0;
            try {
                fat1 = Double.parseDouble(etFat.getText().toString());
                double snf1 = Double.parseDouble(etSnf.getText().toString());

                fat2 = Double.parseDouble(amcuConfig.getChangeFat());
                double snf2 = Double.parseDouble(amcuConfig.getChangeSnf());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (fat1 >= fat2 && fat2 != 0) {
                cattleType = "BUFFALO";
            } else {
                cattleType = "COW";
            }

        } else {
            double fat1 = 0;
            double fat2 = 0;
            try {
                fat1 = Double.parseDouble(etFat.getText().toString());
                double snf1 = Double.parseDouble(etSnf.getText().toString());

                fat2 = Double.parseDouble(amcuConfig.getChangeFat());
                double snf2 = Double.parseDouble(amcuConfig.getChangeSnf());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (fat1 >= fat2 && fat2 != 0) {
                cattleType = "BUFFALO";
            } else {
                cattleType = session.getMilkType();
            }
        }
        return cattleType;
    }

    public void readWeightData() throws IOException {

       /* if ((null != comingFrom && comingFrom.equalsIgnoreCase("UMCA"))) {

        } else {
            try {
                if (allDeviceData.size() > 0) {
                    for (int i = 0; i < allDeviceData.size(); i++) {
                        if (allDeviceData.get(i).driverName
                                .equalsIgnoreCase(DeviceName.WS)) {
                            if (allDeviceData.get(i).device != null) {
                                openDeviceConnectionWeight(
                                        allDeviceData.get(i).driver,
                                        allDeviceData.get(i).productId);
                            }
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            CheckWriteWeight();
            // ReadData();
        }
*/

    }

    public void tareWeighingScale() {

        countTare = countTare + 1;

        if (usbSerialPortWeighingScale != null) {

            String Exception = "exp";

            String tareMsg = amcuConfig.getTareCommand();

            byte[] tempBytes = tareMsg.getBytes(Charset.forName("UTF-8"));
            try {
                usbSerialPortWeighingScale.write(tempBytes, 3000);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Exception = e.getMessage();
                e.printStackTrace();
            } finally {
                if (Exception.contains("java.io.IOException: Error writing")) {
                    Toast.makeText(ChillingCollectionActivityV2.this,
                            "Please restart the attached devices",
                            Toast.LENGTH_SHORT).show();

                }
            }
        }

    }

    public void writeOnSDCard() {
        try {
            ReportEntity reportEntity = getReportEntity(1);
            smartCCUtil.saveReportsOnSdCard(reportEntity);
        } catch (Exception e) {
            AfterAddingDatabaseTable();
            Util.restartTab(ChillingCollectionActivityV2.this);
            e.printStackTrace();
        }
    }

    public void gotoFarmerScannerActivity() {
        Intent intent = new Intent(getApplicationContext(), FarmerScannerActivity.class);
        if (comingFromSID) {
            intent.putExtra("USER_ID", "SID");
        } else {
            intent.putExtra("USER_ID", "CID");
        }
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

    public String addToDatabase() {


        String dbError = null;
        if (cattleType.replaceAll(" ", "").equalsIgnoreCase("")) {
            cattleType = "COW";
        }
        reportEntity = getReportEntity(Util.REPORT_COMMITED);
        reportEntity.resetSentMarkers();
        Util.setDailyDateOrShift(Util.getTodayDateAndTime(7, ChillingCollectionActivityV2.this, true),
                reportEntity.postShift);
        try {
            collectionRecordDao.saveOrUpdate(reportEntity);
            updateSelectedReportEntities(reportEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbError;
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
        Toast.makeText(ChillingCollectionActivityV2.this, "Tab restart is required.", Toast.LENGTH_LONG).show();
        Util.restartTab(ChillingCollectionActivityV2.this);
        //   alertForDatabaseFailure();

    }

    public ReportEntity getReportEntity(int commited) {
        int txNumber = new SessionManager(ChillingCollectionActivityV2.this)
                .getTXNumber() + 1;
        String date = smartCCUtil.getReportFormatDate();
        quantityTime = collectionTime;

        reportEntity.temp = temp;

        reportEntity.fat = Double.parseDouble(etFat.getText().toString().trim());
        reportEntity.snf = Double.parseDouble(etSnf.getText().toString().trim());

        reportEntity.user = session.getUserId();
        reportEntity.farmerId = session.getFarmerID();
        reportEntity.farmerName = session.getFarmerName();
        reportEntity.socId = session.getCollectionID();
        if (etRate.getText().toString().trim().length() > 0) {
            reportEntity.rate = doubleRate;
        } else {
            reportEntity.rate = 0;
        }

        if (null == comingFrom || !comingFrom.equalsIgnoreCase("UMCA")) {
            if (etMilkWeight.getText().toString().trim().length() > 0) {
                reportEntity.quantity = !etMilkWeight.getText().toString().trim().equalsIgnoreCase("") ? Double.parseDouble(etMilkWeight.getText().toString().trim()) : 0;
            } else {
                reportEntity.quantity = weightRecord;
            }

        } else {
            reportEntity.quantity = weightRecord;
        }


        if (etRate.getText().toString().trim().length() > 0) {
            reportEntity.amount = amountRecord;
        } else {
            reportEntity.amount = 0;
        }


        getIncentive(reportEntity.amount);
        reportEntity.postDate = date;

        if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) {
            reportEntity.postShift = UMCA_SHIFT;
        } else {
            reportEntity.postShift = currentShift;
        }

        reportEntity.time = startTime;
        reportEntity.milkType = cattleType;
        reportEntity.lDate = Util.getDateInLongFormat(date);
        reportEntity.txnNumber = txNumber;

        //   reportEntity.miliTime = collectionTime;

        //milkAnanalyserTime = Util.getTodayDateAndTime(6);
        //qualityTime = Calendar.getInstance().getTimeInMillis();

        reportEntity.milkAnalyserTime = qualityTime;

        //quantityTime = Calendar.getInstance().getTimeInMillis();
        reportEntity.weighingTime = quantityTime;

        reportEntity.awm = mMaEntity.addedWater;

        if (session.getIsSample()
                || Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {

            if (isCleaningFailed) {
                reportEntity.status = "Failure";
            } else {
                reportEntity.status = "Success";
            }

        } else if (isReject) {

            reportEntity.status = "Reject";
            Util.setCollectionStartedWithMilkType(cattleType, ChillingCollectionActivityV2.this);
        } else {
            reportEntity.status = "Accept";
            Util.setCollectionStartedWithMilkType(cattleType, ChillingCollectionActivityV2.this);
        }

        if (isAutoManual != null) {
            reportEntity.manual = isAutoManual;
            reportEntity.qualityMode = isMaAutoManual;
        } else {
            reportEntity.manual = "Manual";
            reportEntity.qualityMode = isMaAutoManual;
        }
        if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) {
            if (null != weightManualStatus)
                reportEntity.quantityMode = weightManualStatus;
            else
                reportEntity.quantityMode = isWsManual;
        } else {
            reportEntity.quantityMode = isWsManual;
        }

        if (etClr.getText().toString().trim().length() > 0) {
            reportEntity.clr = Double.parseDouble(etClr.getText().toString().trim());
        } else {
            reportEntity.clr = clr;
        }

        reportEntity.bonus = bonusAmount;
        reportEntity.recordCommited = commited;
        reportEntity.collectionType = Util.REPORT_TYPE_MCC;
        // Adding milkquality

        reportEntity.milkQuality = "GOOD";

        reportEntity.rateMode = rateMode;
        if (0 != numberOfCans) {
            reportEntity.numberOfCans = numberOfCans;
        } else {
            reportEntity.numberOfCans = 1;
        }

        reportEntity.centerRoute = Util.getRouteFromChillingCenter(ChillingCollectionActivityV2.this, session.getFarmerID());
//        if (commited == 0) {
//            reportEntity.recordStatus = Util.RECORD_STATUS_INCOMPLETE;
//        } else {
        reportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;
        //   }
        reportEntity.rateChartName = amcuConfig.getRateChartName();
        reportEntity.kgWeight = kgQuantity;
        reportEntity.ltrsWeight = ltrQuantity;

        //Added tipping details

        reportEntity.tippingStartTime = tippingStartTime;
        reportEntity.tippingEndTime = tippingEndTime;
        reportEntity.agentId = smartCCUtil.getAgentId();
        reportEntity.milkStatusCode = smartCCUtil.getMilkStatusCode("GOOD");

        if (unCommitedReportEntity != null) {
            reportEntity.rateCalculation = unCommitedReportEntity.rateCalculation;
        } else if (amcuConfig.getRateCalculatedFromDevice()) {
            reportEntity.rateCalculation = 1;
        } else {
            reportEntity.rateCalculation = 0;
        }
        //sequence number resemble sample number to server
        if (etSID.getText().toString().trim().length() > 0) {
            reportEntity.sampleNumber = Integer.valueOf(etSID.getText().toString().trim());
        } else {
            // selection uncomitted wiighing scale recore
            // comming from cid , since multiple selection is there so by defualt it will be 99999999, even for one checked selected record also.
            // default value is 99999999
            reportEntity.sampleNumber = Util.LAST_SEQ_NUM;
        }

        reportEntity.serialMa = 1;
        reportEntity.maName = amcuConfig.getMA();
        reportEntity.fatKg = Util.convertPercentageToKg(reportEntity.kgWeight, reportEntity.fat);
        reportEntity.snfKg = Util.convertPercentageToKg(reportEntity.kgWeight, reportEntity.snf);
        reportEntity.postDate = smartCCUtil.getReportFormatDate();
        reportEntity.postShift = Util.getCurrentShift();
        if (mMaEntity != null) {
            reportEntity.protein = mMaEntity.protein;
            reportEntity.lactose = mMaEntity.lactose;
            reportEntity.conductivity = mMaEntity.conductivity;
        }
        smartCCUtil.setCollectionStartData(reportEntity);

        return reportEntity;
    }

    // to evaluate accept or reject milk from ratechart
    public void evaluateAcceptOrReject() {
        btnReject.setFocusable(true);
        milkAnanalyserTime = Util.getTodayDateAndTime(6, ChillingCollectionActivityV2.this, true);
        qualityTime = calendar.getTimeInMillis();
        String fat, snf, clr;
        fat = etFat.getText().toString().replace(" ", "");
        snf = etSnf.getText().toString().replace(" ", "");
        clr = etClr.getText().toString().trim();
        double dsnf = chckValidationHelper.getDoubleFromString(snf, 0);
        if (snf.trim().length() < 1) {
            dsnf = Util.getSNF(chckValidationHelper.getDoubleFromString(fat, 0),
                    chckValidationHelper.getDoubleFromString(clr, 0));
        }

        if (unCommitedReportEntity == null && amcuConfig.getAllowMaxLimitFromRateChart()) {
            if (snf.trim().length() < 1) {
                dsnf = Util.getSNF(chckValidationHelper.getDoubleFromString(fat, 0),
                        chckValidationHelper.getDoubleFromString(clr, 0));
                etSnf.setText(String.valueOf(dsnf));
            }
            resetFatAndSnf(fat, snf);
            fat = etFat.getText().toString().replace(" ", "");
            snf = etSnf.getText().toString().replace(" ", "");
            clr = etClr.getText().toString().trim();
        }


        if (unCommitedReportEntity == null) {
            if (((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")
                    && !session.getIsChillingCenter())) && amcuConfig.getMaManual()) {
                snf = String.valueOf(Util.getSNF(Double.parseDouble(fat), Double.parseDouble(clr)));
                etSnf.setText(snf);
                resetFatAndSnf(fat, snf);
                snf = etSnf.getText().toString().replace(" ", "");

            } else if (((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FS"))
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FS")
                    && !session.getIsChillingCenter())) && amcuConfig.getMaManual()) {
                clr = String.valueOf(Util.getCLR(Double.valueOf(fat), Double.valueOf(snf)));
                etClr.setText(clr);
            }


            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")
                    || amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))
                    && etClr.getText().toString().trim().length() > 0) {
                clr = etClr.getText().toString().trim();
                etClr.setText(decimalFormatClr.format(Double.valueOf(clr)));
                if (etSnf.getText().toString().trim().length() > 0) {

                } else {

                    snf = String.valueOf(Util.getSNF(Double.parseDouble(fat), Double.parseDouble(clr)));
                    etSnf.setText(snf);

                }
                resetFatAndSnf(fat, snf);
            } else {
                clr = String.valueOf(Util.getCLR(Double.valueOf(fat), Double.valueOf(snf)));

                etClr.setText(clr);
            }

        }
        snf = etSnf.getText().toString().replace(" ", "");
        getRateFromRateChart(Double.parseDouble(snf), Double.parseDouble(fat),
                new ValidationHelper().getDoubleFromString(
                        etClr.getText().toString().trim(), 0));
        isReject = false;
        //To add the temp records

        if ((Double.parseDouble(fat) > Util.MAX_FAT_LIMIT || Double.parseDouble(snf) > Util.MAX_SNF_LIMIT)
                || (Double.parseDouble(fat) < Util.MIN_FAT_LIMIT
                || Double.parseDouble(snf) < Util.MIN_SNF_LIMIT)) {
            Toast.makeText(ChillingCollectionActivityV2.this, "Invalid milk analyzer data", Toast.LENGTH_SHORT).show();
            onFinish();

        } else if (Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit()) ||
                Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            //This is only for cleaning and rinsing
            btnReject.setText("OK");
            btnReject.setEnabled(true);
            btnReject.requestFocus();

        } else if (session.getIsSample()
                && !(Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            saveIncompleteRecord();
            btnReject.setText("OK");
            btnReject.setEnabled(true);
            btnReject.requestFocus();
        } else if (!session.getIsSample() ||
                Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            saveIncompleteRecord();
            isReject = smartCCUtil.isMilkRejected(
                    smartCCUtil.getMAEntity(Double.valueOf(fat), Double.valueOf(snf),
                            Double.valueOf(clr)));

            if (isReject && amcuConfig.getEditableRate() && (
                    !session.getIsSample() ||
                            Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
                alertReject();
            } else if (isReject) {
                Toast.makeText(ChillingCollectionActivityV2.this,
                        "Milk rejected for this fat and snf!",
                        Toast.LENGTH_LONG).show();
                isBtnEnable = true;
                btnReject.setText("Reject");
                btnReject.setEnabled(true);
                btnReject.requestFocus();
            } else {
                if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                        || Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                    btnReject.setText("OK");
                } else if (!Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                    setMilkWeighingState();
                    btnReject.setText("Print");
                } else {
                    setMilkWeighingState();
                    btnReject.setText("Print");
                }
                try {
                    sbMessage = new StringBuilder();
                    readWeightData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            enableReject(null);
        }

        setWsEnable();
        //Added in 11.1.3
        // stopMaReading();
    }

    public void setMilkWeighingState() {
        if (amcuConfig.getEnableFilledOrEmptyCans()) {
            if ((null == comingFrom || !comingFrom.equalsIgnoreCase("UMCA"))) {
                btnWeight.setVisibility(View.VISIBLE);
                btnWeight.setText("Next weight");
            }
        }
    }

    @Override
    public void onBackPressed() {

    }

    public void onRejectMilk() {

        new Thread(new Runnable() {

            @Override
            public void run() {
                getFullMsg();
                addToDatabase();
                myHandlerReject.post(updateRunnableReject);
            }
        }).start();

        // Additional functionality like printing can be handled
        // in this handler
        updateRunnableReject = new Runnable() {

            @Override
            public void run() {
                closeMAConnection();
                DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
                // last sid return cursor data
                Cursor cursor = dbh.nextSequenceNumber(milkSID, smartCCUtil.getReportFormatDate(), Util.getCurrentShift());
                if (null != cursor && cursor.getCount() == 0) {
                    Toast.makeText(ChillingCollectionActivityV2.this, "No more data available.", Toast.LENGTH_SHORT).show();
                    gotoFarmerScannerActivity();
                } else if (comingFromSID || comingFromNextSIDAlert) {
                    milkSID = mReportEntity.sampleNumber;
                    if (milkSID != 0)
                        gotoNewSessionIDAlert();
                    else
                        gotoFarmerScannerActivity();
                } else {
                    onFinish();
                }
                /*if(comingFromSID || comingFromNextSIDAlert){
                    gotoFarmerScannerActivity();
                }else {
                    onFinish();
                }*/
            }
        };
    }

    public void tareWSOverSerialManager() {
        byte[] tareMsg = amcuConfig.getTareCommand().getBytes(Charset.forName("UTF-8"));
        try {

            mSerialIoManager.writeAsync(tareMsg);
            isTareDone = true;

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        //Commented as Id is not required for Now
        //  getIDWSOverSerialManager();
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

    // After getting Milk Analyser data from Auto Mode like rejet calculation
    public void afterGettingMaData(double fat, double snf) {

        etClr.setText(String.valueOf(mMaEntity.clr));
       /* if (etClr.getText().toString().trim().length() > 0) {
            // auto clr from machine
        } else {
            // calculated clr
            etClr.setText(Util.getCLR(fat, snf, this));
        }*/
        if ((session.getIsSample() && !Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            sbMessage = new StringBuilder();
            evaluateAcceptOrReject();
            setWsEnable();
        } else {
            btnReject.setEnabled(true);

            btnReject.setText("Read Rate");
            waitingForMAData = false;
            btnReject.requestFocus();

        }


    }

    public void parseKamdhenuData() {

        int i;
        try {
            i = sbMessage.indexOf("temperature");
            String temp = sbMessage.substring(i + 6, i + 11);

            if (temp != null && i != -1) {
                temp = temp.replace(" ", "");
            } else {
                temp = "0.0";
            }

            i = sbMessage.indexOf("Fat");
            String fat = sbMessage.substring(i + 15, i + 20);
            fat = fat.replace("%", "");
            fat = fat.replaceAll(" ", "");
            etFat.setText(decimalFormatFat.format(Double.parseDouble(fat)));

            i = sbMessage.indexOf("SNF");
            String snf = sbMessage.substring(i + 15, i + 20);
            snf = snf.replace("%", "");
            snf = snf.replaceAll(" ", "");
            etSnf.setText(decimalFormatSNF.format(Double.parseDouble(snf)));

            i = sbMessage.indexOf("Added water");
            if (i != -1) {
                String addedWater = sbMessage.substring(i + 15, i + 20);
                addedWater = addedWater.replace("%", "");
                addedWater = addedWater.replace(" ", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        afterGettingMaData(mMaEntity.fat, mMaEntity.snf);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(ChillingCollectionActivityV2.this);
                return true;

            case KeyEvent.KEYCODE_F11:
                Util.restartTab(ChillingCollectionActivityV2.this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(ChillingCollectionActivityV2.this, null);
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
            case KeyEvent.KEYCODE_NUMPAD_ENTER:

                if ((Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                        || Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) && btnReject.isEnabled()) {
                    onClickButtonReject();
                } else if (!isFatSnfPressed && checkForValidData(false) && btnReject.isEnabled()) {
                    if (amcuConfig.getEnableFilledOrEmptyCans() && !isReject) {
                        btnWeight.requestFocus();
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

    public String receiptFormat() {

        FormatPrintRecords formatPrintRecord = new FormatPrintRecords(ChillingCollectionActivityV2.this);
        ReportEntity reportEntity = getReportEntity(1);
        String printData = formatPrintRecord.receiptFormat(ChillingCollectionActivityV2.this, reportEntity);
        return printData;

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

    public double getClr(String fat, String snf) {
        double mClr = 25.0;

        DecimalFormat decimalClr = new DecimalFormat("#0");
        if (!fat.equalsIgnoreCase("") && !snf.equalsIgnoreCase("")
                && fat.length() > 0 && snf.length() > 0) {
            try {
                snf = decimalFormatSNF.format(Double.parseDouble(snf));
                fat = decimalFormatFat.format(Double.parseDouble(fat));
                mClr = (((amcuConfig.getSnfCons() * Double.parseDouble(snf)) - ((amcuConfig
                        .getFatCons() * Double.parseDouble(fat))) - (amcuConfig
                        .getConstant())));
                clr = mClr;
            } catch (NumberFormatException e) {
                e.printStackTrace();
                clr = mClr;
            }

        }

        return clr;

    }

    public void setHeader() {
//        if (allDeviceData != null && allDeviceData.size() > 4) {
//            tvHeader.setText(session.getSocietyName() + "  "
//                    + session.getMAType() + "  " + session.getMilkType());
//        } else {
//            tvHeader.setText(session.getSocietyName());
//        }

        tvHeader.setText(session.getSocietyName());
    }

    public void alertForDatabaseFailure() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                ChillingCollectionActivityV2.this);
        // set title
        alertDialogBuilder.setTitle("Reboot alert!");
        // set dialog message

        alertDialogBuilder
                .setMessage("This data need to take manually!Press reboot for further collection!")
                .setCancelable(false);

        alertDialogBuilder.setPositiveButton("Reboot",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Util.restartTab(ChillingCollectionActivityV2.this);
                        dialog.cancel();
                        onFinish();

                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
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
        if (session.getIsSample()
                || Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            milkType = "Test";
            if (isCleaningFailed) {
                status = "Failure";
            } else {
                status = "Success";
            }

        } else if (isReject) {
            status = "Reject";
        } else {
            status = "Accept";
        }
        String date = smartCCUtil.getReportFormatDate();
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

    public void saveIncompleteRecord() {

        getFullMsg();
        ReportEntity reportEntity = getReportEntity(Util.REPORT_NOT_COMMITED);
        try {
            long columnId = collectionRecordDao.saveOrUpdate(reportEntity);
            // reportEntity.setPrimaryKeyId(columnId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        updateSelectedReportEntities(reportEntity);
    }

    public void onFinish() {
        startActivity(new Intent(ChillingCollectionActivityV2.this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

    public void initializeReportEntity() {
        if (mReportEntity != null) {
            this.reportEntity = mReportEntity;
            unCommitedReportEntity = null;
        } else {
            this.reportEntity = collectionRecordDao.findByDateShiftAndCommitStatus(
                    SmartCCUtil.getDateInPostFormat(), smartCCUtil.getFullShift(Util.getCurrentShift()),
                    CollectionConstants.UNSENT);
            if (this.reportEntity == null) {
                this.reportEntity = new ReportEntity();
                reportEntity.user = session.getUserId();
                reportEntity.farmerId = session.getFarmerID();
                reportEntity.farmerName = session.getFarmerName();
                reportEntity.socId = session.getCollectionID();
                reportEntity.postDate = smartCCUtil.getReportFormatDate();
                reportEntity.lDate = Util.getDateInLongFormat(reportEntity.postDate);
                reportEntity.time = Util.getTodayDateAndTime(3, ChillingCollectionActivityV2.this, true);
                reportEntity.miliTime = calendar.getTimeInMillis();
                reportEntity.txnNumber = session.getTXNumber() + 1;
            } else {
                unCommitedReportEntity = reportEntity;
            }
        }

    }

    public void closeMAConnection() {
        if (maManager != null) {
            maManager.stopReading();
        }
        /*try {
            if (usbSerialPortMilkoTester != null) {
                stopIoManager(MILK_ANALYSER_DRIVER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    //Alert dialog if mulitiple cans

    public void closeWSConnection() {
        try {
            if (usbSerialPortWeighingScale != null) {
                stopIoManager(WEIGHING_SCALE_DRIVER);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

            if (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")) {
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

    // focus changes to get weight
    public void setWsEnable() {
        etMilkWeight.setEnabled(false);
        setWeightAndAmountManually(chckValidationHelper.getDoubleFromString(etMilkWeight.getText().toString().trim(), 0));
    }

    public void textChangeFunction() {
        etMilkWeight.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (amcuConfig.getEnableFilledOrEmptyCans() && !isReject) {
                        btnWeight.requestFocus();
                    } else if (btnReject.isEnabled()) {
                        onClickButtonReject();
                    }
                    return true;
                }
                return false;
            }
        });

        etMilkWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (checkForValidData(true)) {
                    double weight = chckValidationHelper.getDoubleFromString(etMilkWeight.getText().toString().replace(" ", ""), 0);
                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(weight);

                } else if (checkCurdMilkQuality()) {
                    double weight = chckValidationHelper.getDoubleFromString(etMilkWeight.getText().toString().replace(" ", ""), 0);
                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(weight);
                } else {
                    setWeightAndAmountManually(0);
                    if (!session.getIsSample() && !Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                        btnReject.setEnabled(false);
                    } else if (Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
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
                if (checkCurdMilkQuality()) {
                    String rate = etRate.getText().toString().replace(" ", "");
                    try {
                        doubleRate = Double.parseDouble(rate);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    doubleRate = Double.parseDouble(decimalFormatRate.format(doubleRate));

                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(chckValidationHelper.getDoubleFromString(etMilkWeight.getText().toString(), 0));
                }
            }
        });

    }

    public void checkForManualMAorWS(int keyValue) {

        if ((null != comingFrom && comingFrom.equalsIgnoreCase("UMCA"))
                && !checkCurdMilkQuality()
                && btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate") && !etRate.isEnabled()) {
            btnReject.setText("Read Rate");
            closeMAConnection();
            waitingForMAData = false;


        } else if ((null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) && checkCurdMilkQuality()
                && !btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate") && !etRate.isEnabled()) {
            btnReject.setText("Enter Manual Rate");
            closeMAConnection();
            waitingForMAData = false;

        } else if (btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
            // no code for this block since we dont want to enter any data to weight text
            onRateChange();
        } else if (amcuConfig.getMaManual() && ((btnReject.getText().toString().equalsIgnoreCase("Read weight")) ||
                waitingForMAData)) {
            Util.alphabetValidation(etFat, Util.ONLY_DECIMAL, ChillingCollectionActivityV2.this, 6);
            Util.alphabetValidation(etSnf, Util.ONLY_DECIMAL, ChillingCollectionActivityV2.this, 6);
            // Util.alphabetValidation(etClr,Util.ONLY_NUMERIC,ChillingCollectionActivityV2.this,2);
            isMaAutoManual = "Manual";
            waitingForMAData = false;
            closeMAConnection();
            btnReject.setEnabled(true);
            if ((null != comingFrom && comingFrom.equalsIgnoreCase("UMCA"))) {
                btnReject.setText("Read Rate");
            } else if (!session.getIsSample() || Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                btnReject.setText("Read weight");
            } else {
                btnReject.setText("OK");
            }
            if (etFat.getText().toString().length() < 1 && etSnf.getText().toString().length() < 1
                    && etClr.getText().toString().length() < 1) {
                Toast.makeText(ChillingCollectionActivityV2.this, "Enter fat/snf manually!", Toast.LENGTH_SHORT).show();
                etFat.requestFocus();
                etFat.setText(String.valueOf(keyValue));
                etFat.setCursorVisible(true);
                int position = etFat.length();
                etFat.setSelection(position);
            }

        } else if (amcuConfig.getWsManual() && !waitingForMAData &&
                !btnReject.getText().toString().equalsIgnoreCase("Read weight")
                && !isReject && !btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate")
                && !btnReject.getText().toString().equalsIgnoreCase("Rate done") &&
                !btnReject.getText().toString().equalsIgnoreCase("Read Rate")
                && (null == comingFrom || !comingFrom.equalsIgnoreCase("UMCA"))) {

            if (checkForValidData(false) && !isWsManual.equalsIgnoreCase("Manual") &&
                    (!session.getIsSample() || Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
                closeWSConnection();
                Util.alphabetValidation(etMilkWeight, Util.ONLY_DECIMAL, ChillingCollectionActivityV2.this, 8);
                textChangeFunction();
                etMilkWeight.requestFocus();
                etMilkWeight.setText(String.valueOf(keyValue));
                etFat.setCursorVisible(true);
                int pos = etMilkWeight.length();
                etMilkWeight.setSelection(pos);
                isWsManual = "Manual";
                Toast.makeText(ChillingCollectionActivityV2.this, "Enter weight manually!", Toast.LENGTH_SHORT).show();
            }

        }

    }

    public boolean checkForValidData(boolean allValid) {

        double dfat = -1, dSnf = -1, dClr = -1, dQuantity = -1;
        String snf = etSnf.getText().toString().replace(" ", "");
        String fat = etFat.getText().toString().replace(" ", "");

        String clr = etClr.getText().toString().trim();

        if (((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC"))
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")
                && !session.getIsChillingCenter())) && amcuConfig.getMaManual()) {
            if (fat.length() > 0 && clr.length() > 0) {
                snf = String.valueOf(Util.getSNF(Double.parseDouble(fat), Double.parseDouble(clr)));
            }
        } else if (((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FS"))
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FS")
                && !session.getIsChillingCenter())) && amcuConfig.getMaManual()) {
            if (fat.length() > 0 && snf.length() > 0) {
                clr = String.valueOf(Util.getCLR(Double.valueOf(fat), Double.valueOf(snf)));

            }

        }
        String weight = etMilkWeight.getText().toString().replace(" ", "");

        try {
            dfat = Double.parseDouble(fat);
            dSnf = Double.parseDouble(snf);
            dClr = Double.parseDouble(clr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        try {
            dQuantity = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        ValidationHelper validationHelper = new ValidationHelper();
        return validationHelper.isValidFatAndSnf(dfat, dSnf, ChillingCollectionActivityV2.this);
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


                if (checkForValidData(false)) {
                    isMaAutoManual = "Manual";
                    waitingForMAData = false;
                    closeMAConnection();
                    btnReject.setEnabled(true);

                    if (!session.getIsSample() || Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
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

                if (checkForValidData(false)) {
                    isMaAutoManual = "Manual";
                    waitingForMAData = false;
                    closeMAConnection();
                    btnReject.setEnabled(true);

                    if (!session.getIsSample() || Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
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

                if (checkForValidData(false)) {
                    isMaAutoManual = "Manual";
                    waitingForMAData = false;
                    closeMAConnection();
                    btnReject.setEnabled(true);

                    if (!session.getIsSample() ||
                            Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
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
                    if (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")) {
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

    public double getBonusAmount() {

        double bonus = 0;
        MilkAnalyserEntity maEntity = smartCCUtil.getMAEntity(Double.valueOf(
                etFat.getText().toString().trim())
                , Double.valueOf(etSnf.getText().toString().trim()),
                Double.valueOf(etClr.getText().toString().trim()));
        CollectionHelper collectionHelper = new CollectionHelper(ChillingCollectionActivityV2.this);
        bonus = collectionHelper.getBonusAmount(session.getFarmerID(), session.getMilkType(), maEntity);
        return bonus;
    }

    public void getIncentive(double amount) {
        double amt = 0.00, incentiveAmt = 0.00, inCentivePer = 0;
        try {
            amt = amount;
            inCentivePer = amcuConfig.getIncentivePercentage();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            amt = 0;
        }
        incentiveAmt = (amt * inCentivePer) / 100;
        incentive = decimalFormatRate.format(incentiveAmt);

    }


//Reject alert

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
                ChillingCollectionActivityV2.this);

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
                    isReject = false;
                    doubleRate = dEtRate;
                    double wRecord = 0;

                    try {
                        wRecord = Double.valueOf(etMilkWeight.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (wRecord > 0) {
                        setWeightAndAmountManually(chckValidationHelper.getDoubleFromString(etMilkWeight.getText().toString().trim(), 0));
                    }
                    btnReject.setText("Rate done");

                } else {
                    isReject = true;

                    btnReject.setText("Enter rate");

                }

            }
        });
    }

    public boolean checkCurdMilkQuality() {

        double fat = -1;
        double snf = -1;
        double clr = -1;


        String temp_fat = etFat.getText().toString().trim();
        String temp_snf = etSnf.getText().toString().trim();
        String temp_clr = etClr.getText().toString().trim();
        try {
            if (null != temp_fat && !temp_fat.equalsIgnoreCase("") && temp_fat.length() > 0) {
                fat = Double.valueOf(temp_fat);
            }
            if (null != temp_snf && !temp_snf.equalsIgnoreCase("") && temp_fat.length() > 0) {
                snf = Double.valueOf(temp_snf);
            }
            if (null != temp_clr && !temp_clr.equalsIgnoreCase("") && temp_clr.length() > 0) {
                clr = Double.valueOf(temp_clr);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (fat == 0 && (snf == 0 || clr == 0)) {
            return true;
        } else {
            return false;
        }
    }

    public void setDecimalFormatForClr() {
//        if (saveSession.getClrRoundOffUpto() == 0) {
//            decimalFormatClr = new DecimalFormat("#0");
//        } else {
//            decimalFormatClr = new DecimalFormat("#0.0");
//        }
    }

    public void setMaxFatAndSnf() {
//        DatabaseHandler dbHandler = DatabaseHandler.getDatabaseInstance();
//        maxFat = dbHandler.getMaxFATOrSNF(1, amcuConfig.getRateChartName());
//        maxSnf = dbHandler.getMaxFATOrSNF(0, amcuConfig.getRateChartName());
    }

    public void resetFatAndSnf(String fat, String snf) {

        if (amcuConfig.getAllowMaxLimitFromRateChart()) {
            if ((Double.parseDouble(fat) > Double.parseDouble(maxFat))
                    && (Double.parseDouble(snf) > Double.parseDouble(maxSnf))) {
                etFat.setText(maxFat);
                etSnf.setText(maxSnf);
            } else if ((Double.parseDouble(fat) > Double.parseDouble(maxFat))) {
                etFat.setText(maxFat);
            } else if ((Double.parseDouble(snf) > Double.parseDouble(maxSnf))) {
                etSnf.setText(maxSnf);
            }
        }
    }

    public void disableForThrimula() {
        TableRow trQuality = (TableRow) findViewById(R.id.trqualityOfMilk);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvFarmerId = (TextView) findViewById(R.id.tvFarmerId);
        trAmount.setVisibility(View.GONE);
        trWeight.setVisibility(View.GONE);
        tvQualityOfMilk.setVisibility(View.GONE);
        tvName.setVisibility(View.GONE);
        trQuality.setVisibility(View.GONE);

        etAmount.setVisibility(View.GONE);
        etMilkWeight.setVisibility(View.GONE);
        etRate.setVisibility(View.GONE);
        // etFarmerId.setVisibility(View.GONE);
        etFarmerName.setVisibility(View.GONE);
        // btnReject.setText("Done");
    }

    public void getDataFromIntent() {

        comingFrom = getIntent().getStringExtra("COMING_FROM");
        mReportEntity = (ReportEntity) getIntent().getSerializableExtra("SELECTED_DATA");
        comingFromSID = getIntent().getBooleanExtra("COMING_FROM_SID", false);
        comingFromNextSIDAlert = getIntent().getBooleanExtra("COMING_FROM_NEXT_SID", false);
        selectedCursorIds = getIntent().getStringExtra("SELECTED_CURSORID");

        if (comingFromSID || comingFromNextSIDAlert) {
            if (mReportEntity == null) {
                return;
            }
        }

        int numberOfC = 0;
        if (mReportEntity != null) {
            totalWeightFromUserMCA = mReportEntity.quantity;
            numberOfC = mReportEntity.numberOfCans;
            weightManualStatus = mReportEntity.quantityMode;
            kgQuantity = mReportEntity.kgWeight;
            ltrQuantity = mReportEntity.ltrsWeight;
            tippingStartTime = mReportEntity.tippingStartTime;
            tippingEndTime = mReportEntity.tippingEndTime;
            UMCA_SHIFT = mReportEntity.postShift;
            collectionTime = mReportEntity.miliTime;
            weightRecord = mReportEntity.quantity;
            milkSID = mReportEntity.sampleNumber;
            numberOfC = mReportEntity.numberOfCans;
            collectionTime = mReportEntity.miliTime;
        }


//
//        tippingStartTime = getIntent().getLongExtra("TIPPING_START_TIME", System.currentTimeMillis());
//        tippingEndTime = getIntent().getLongExtra("TIPPING_END_TIME", System.currentTimeMillis());

        //SID

        //
        if (numberOfC != 0) {
            numberOfCans = numberOfC;
        }
//        UMCA_SHIFT = getIntent().getStringExtra("UMCA_SHIFT");
//        collectionTime = getIntent().getLongExtra("COLLECTION_TIME",
//               0);

        if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")
                && (totalWeightFromUserMCA > 0)) {
            weightRecord = totalWeightFromUserMCA;

        }

//        milkSID = getIntent().getIntExtra("MILK_SID", 0);
    }

    public void deleteSelectedIds() {
        DatabaseManager dbm = new DatabaseManager(ChillingCollectionActivityV2.this);
        if (selectedCursorIds != null) {
            dbm.deleteSelectedColumnIds(selectedCursorIds);
        }
    }

    //    TODO: Entity has to be modified with all relevant fields like rate, amount ..
    private void updateSelectedReportEntities(ReportEntity modifiedEntity) {
        if (selectedCursorIds == null || modifiedEntity == null) {
            return;
        }
        String[] columnIds = selectedCursorIds.split("=");

        for (String columnId : columnIds) {
            if (columnId.trim().equalsIgnoreCase("")) {
                continue;
            }
            ReportEntity entity = (ReportEntity) collectionRecordDao.findById(Long.parseLong(columnId));
            if (entity != null && entity.columnId != reportEntity.columnId) {
                entity.setFat(modifiedEntity.getFat());
                entity.setFatKg(modifiedEntity.getFatKg());
                entity.setSnf(modifiedEntity.getSnf());
                entity.setSnfKg(modifiedEntity.getSnfKg());
                entity.setClr(modifiedEntity.getClr());
                entity.resetSentMarkers();
                entity.setRecordStatus(Util.RECORD_STATUS_INCOMPLETE);
                entity.setRecordCommited(Util.REPORT_COMMITED);
                collectionRecordDao.update(entity);

            }

        }
    }

    private void printReceipt() {
        printManager.print(receiptFormat(), PrinterManager.printReciept, null, null, null);
    }


}
