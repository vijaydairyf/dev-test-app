package com.devapp.devmain.macollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.RejectionEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.helper.AfterLogInTask;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.WeightLimit;
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
import com.devapp.devmain.usb.DeviceEntity;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import static com.devapp.devmain.user.Util.ping;
import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

public class MilkCollectionActivity extends Activity implements
        AdapterView.OnItemSelectedListener, OnClickListener, WeightLimit {

    public static long SLEEP_TIME = 100;
    private static MilkCollectionActivity mInstance;
    private final String TAG = MilkCollectionActivity.class.getSimpleName();
    public Context context = this;
    public String milkAnanalyserTime, startTime, weighTime;
    public String EcoRduMsg;
    public boolean visible = false;
    public ArrayList<DeviceEntity> allDeviceData = SmartCCConstants.devicesList;
    public String Message, EKOmilkUltraMessage;
    public boolean isEnable;
    public String NewRDUMSG;
    public int MAbaudRate = 1200, rduBaudRate = 9600, WeighingBaudrate = 9600;
    public String RDUMsg;
    public String currentShift;
    public long tempReportColId = -1;
    public String dbError = null;
    public boolean isReject = false;
    public String comingFrom;
    public double totalWeightFromUserMCA;
    public int numberOfCans;
    public String rateMode = Util.RATE_MODE_AUTO;
    public double newRcordFilledCan = 0;
    public double tempLt, tempKg;
    RejectionEntity dataBaseRejectEnt;
    String spinnerItem;
    UsbManager mUsbManager;
    boolean waitingForMAData = true;
    boolean ignoreInitialWSData = true;
    Button btnReject, btnWeight, btnAutoManual;
    StringBuilder sbMessage = new StringBuilder();
    EditText etFat, etSnf, etMilkWeight, etFarmerName, etFarmerId, etRate,
            etAmount, etClr, etSId, etProtein, etProteinRate;
    //    String weightRecord = "0.0", amountRecord = "0.0", bonusAmount = "00.00", incentive = "00.00";
    double weightRecord, amountRecord, bonusAmount, incentive;
    TextView tvFatAuto, tvSnfAuto, tvRate, tvMilkWeight, tvQualityOfMilk, txtCLR,
            txtSID, tvProtein, tvProteinRate;
    TextView tvHeader;
    /*DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatQuantity = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatRate = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS;
    DecimalFormat decimalFormatClr;
    DecimalFormat decimalFormatFat;
    DecimalFormat decimalFormatSNF;
    DecimalFormat decimalFormatProtein;
    DecimalFormat decimalFormatRateProtein;
    DecimalFormat decimalFormatRateFat;
    DecimalFormat decimalFormatRateSNF;
    DecimalFormat decimalFormatRateCLR;*/
    DecimalFormat decimalFormat2Digit = new DecimalFormat("#0.00");
    double Rate = 0;
    SessionManager session;
    String Totrate;
    String strRate;
    Handler myHandlerReject = new Handler();
    Runnable updateRunnableReject;
    String isAutoManual = AppConstants.MANUAL, isMaAutoManual = AppConstants.AUTO,
            isWsManual = "Auto";
    double addedWater;
    boolean isEKOMILK, isLM2, isEKOMilkUltra, isRduSmart, isBtnEnable, isAkashGanga, isIndifoss, isLactoScanV2,
            isLactoScan, isCavinCare, isKamDhenu, isNuline, isGoldTech, isKsheeraa, isEkoMilkEven, isEkomilkV2,
            isLaktan240;
    SampleDataEntity sampleDataEnt;
    AmcuConfig amcuConfig;
    PrinterManager printManager;
    double clr;
    double temp;
    TableRow trFat, trSnf, trWeight, trRate, trAmount;
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
    SmartCCUtil smartCCUtil;
    String DATE, TIME;
    int countTare = 0;
    boolean isCleaningFailed = false;
    Runnable timeOutRunnable;
    Handler timeOutHandler = new Handler();
    boolean timeOut = false;
    long time2 = System.currentTimeMillis() + 300000L;
    String radioStatus;
    TextView tvPreviousData, tvNewData, tvTotal;
    //    String prevRecord = "0.00";
    double prevRecord;
    Spinner spinner;
    LinkedHashMap<String, String> spinnerHashMapData;
    String selectedCursorIds;
    String weightManualStatus;
    // New class WeighingScaleManger to handle weighing related parsing stuff;
    boolean isFilledOrEmptyCanStarted;
    //boolean to confirm that taken weight from next can feature
    AlertDialog alertForFilledAndEmptyCan;
    AlertDialog alertForRemoveCanAndTare;
    DriverConfiguration driverConfiguration;
    CollectionHelper collectionHelper;
    //    String kgWeight = "0.00", ltrWeight = "0.00";
    double kgWeight, ltrWeight;
    String maxFat = "0.0", maxSnf = "0.0";
    long tippingStartTime = 0, tippingEndTime;
    DatabaseHandler databaseHandler;
    double inCentiveRate;
    double incentiveAmount = 0.0;
    String protein = "0.0";
    double totalAmount;
    ChooseDecimalFormat chooseDecimalFormat;
    UIValidation uiValidation;
    ValidationHelper validationHelper = new ValidationHelper();
    private WifiUtility wifiUtility;
    private LinearLayout statusLayout;
    private CheckBox cbMa, cbWs, cbRdu, cbPrinter;
    private MaManager maManager;
    private ReportEntity reportEntity, unCommitedReportEntity;
    private WsManager wsManager;
    private TableRow trProtein;
    private TextView tvAmount;
    private String UMCA_SHIFT;
    private TextView textNote;
    private MilkAnalyserEntity mMaEntity;
    private CollectionRecordDao collectionRecordDao;
    private double lactose;
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

    public static MilkCollectionActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHandler = DatabaseHandler.getDatabaseInstance();
        comingFrom = getIntent().getStringExtra("COMING_FROM");
        totalWeightFromUserMCA = getIntent().getDoubleExtra("TOTALMILK", 0);
        int numberOfC = getIntent().getIntExtra("NO_OF_CANS", 1);
        weightManualStatus = getIntent().getStringExtra("WEIGHT_MANUAL");
        mInstance = MilkCollectionActivity.this;
        registerBroadcastReceivers();

        numberOfCans = numberOfC;

        UMCA_SHIFT = getIntent().getStringExtra("UMCA_SHIFT");

        selectedCursorIds = getIntent().getStringExtra("SELECTED_CURSORID");

        if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA") && (totalWeightFromUserMCA > 0)) {
            //    weightRecord = totalWeightFromUserMCA;
            reportEntity.setQuantity(totalWeightFromUserMCA);
            reportEntity.setQuantity(totalWeightFromUserMCA);
        }

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.allusbdevice_landscape);
        } else {
            setContentView(R.layout.allusbdevice);
        }

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(MilkCollectionActivity.this);
        wifiUtility = new WifiUtility();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try {
            isAutoManual = getIntent().getStringExtra("isAutoOrManual");
        } catch (Exception e) {
            isAutoManual = "Manual";
            e.printStackTrace();
        }
        printManager = new PrinterManager(MilkCollectionActivity.this);
        tvQualityOfMilk = (TextView) findViewById(R.id.tvQualityOfMilk);
        txtSID = (TextView) findViewById(R.id.txtSID);
        etSId = (EditText) findViewById(R.id.etSId);

        txtSID.setVisibility(View.GONE);
        etSId.setVisibility(View.GONE);
        etFarmerId = (EditText) findViewById(R.id.etFarmerId);
        etFarmerName = (EditText) findViewById(R.id.etFarmerName);
        etRate = (EditText) findViewById(R.id.etRate);
        etAmount = (EditText) findViewById(R.id.etAmount);
        etSnf = (EditText) findViewById(R.id.etSnf);
        etMilkWeight = (EditText) findViewById(R.id.etMilkweight);
        etFat = (EditText) findViewById(R.id.etFat);
        btnReject = (Button) findViewById(R.id.btnReject);
        btnWeight = (Button) findViewById(R.id.btnNext);
        btnAutoManual = (Button) findViewById(R.id.btnAutoManual);

        tvFatAuto = (TextView) findViewById(R.id.tvFatAuto);
        tvSnfAuto = (TextView) findViewById(R.id.tvSnfAuto);
        tvHeader = (TextView) findViewById(R.id.tvheader);
        tvMilkWeight = (TextView) findViewById(R.id.tvMilkWeight);
        tvProtein = (TextView) findViewById(R.id.tvProtein);
        tvProteinRate = (TextView) findViewById(R.id.tvProteinRate);

        etProtein = (EditText) findViewById(R.id.etProtein);
        etProteinRate = (EditText) findViewById(R.id.etProteinRate);
        cbMa = (CheckBox) findViewById(R.id.cb_ma);
        cbWs = (CheckBox) findViewById(R.id.cb_ws);
        cbRdu = (CheckBox) findViewById(R.id.cb_rdu);
        cbPrinter = (CheckBox) findViewById(R.id.cb_printer);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
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
        trProtein = (TableRow) findViewById(R.id.trProtein);
        showProteinOption();

        tvRate = (TextView) findViewById(R.id.tvRate);
        //adding spinner data*/
        spinner = (Spinner) findViewById(R.id.spinnerQualityOfMilk);

        tvAmount = (TextView) findViewById(R.id.tvAmount);
        setStatusLayout();
        etProteinRate.setEnabled(false);

        if (session.getIsChillingCenter()) {
            spinner.setVisibility(View.VISIBLE);
            tvQualityOfMilk.setVisibility(View.VISIBLE);
        }
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


        //  dataBaseRejectEnt = Util.getRejectEnt(MilkCollectionActivity.this);

        if (session.getSelectedLanguage().equalsIgnoreCase("Kannada")) {
            setKannada();
        }
        boolean isFarm = true;

        try {
            isFarm = getIntent().getBooleanExtra("isFarmer", isFarm);

            sampleDataEnt = (SampleDataEntity) getIntent()
                    .getSerializableExtra("SampleDataEnt");

        } catch (Exception e) {
            e.printStackTrace();
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
        setDisableInitilization();
        onCreateView();
        getViewForFATSNFCLR(tvSnfAuto, txtCLR, etSnf, etClr);
//        startPingService();
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

    private void displayDeviceStatus() {

        cbMa.setChecked(maPing);
        cbWs.setChecked(wsPing);
        cbRdu.setChecked(rduPing);
        cbPrinter.setChecked(printerPing);
    }

    private void startPingService() {
//        pingIntent = new Intent(MilkCollectionActivity.this, PingService.class);
//        startService(pingIntent);
        /*pingRunnable = new Runnable() {
            @Override
            public void run() {

                try {
                    new PingTask().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pingHandler.postDelayed(this, 10000);
            }
        };
        pingHandler.post(pingRunnable);*/
    }

    @Override
    public void onStart() {


        uiValidation = UIValidation.getInstance();
        collectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        chooseDecimalFormat = new ChooseDecimalFormat();
        validationHelper = new ValidationHelper();
        smartCCUtil = new SmartCCUtil(MilkCollectionActivity.this);
        driverConfiguration = new DriverConfiguration();
        collectionHelper = new CollectionHelper(MilkCollectionActivity.this);
        initializeReportEntity();

       /* decimalFormatFS = chooseDecimalFormat.getFatAndSnfFormat();
        decimalFormatFat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        decimalFormatSNF = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        decimalFormatClr = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);

        decimalFormatProtein = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.PROTEIN);
        decimalFormatRateProtein = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.PROTEIN);
        decimalFormatRateFat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.FAT);
        decimalFormatRateSNF = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.SNF);
        decimalFormatRateCLR = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.CLR);*/


        rduBaudRate = amcuConfig.getRdubaudrate();
        WeighingBaudrate = amcuConfig.getWeighingbaudrate();

        // setDecimalRoundOff();
        currentShift = Util.getCurrentShift();
        String ma = amcuConfig.getMA();
        MAbaudRate = amcuConfig.getMABaudRate();
        String rdu = amcuConfig.getRDU();

        sbMessage = new StringBuilder();
        //   setDecimalFormatForClr();
        /*maParams = new DeviceParameters(saveSession.getMA(), saveSession.getMABaudRate(),
                driverConfiguration.getParity(saveSession.getMaParity()),
                driverConfiguration.getStopBits(saveSession.getMaStopBits()),
                driverConfiguration.getDataBits(saveSession.getMaDataBits()));*/
        maManager = MAFactory.getMA(DeviceName.MILK_ANALYSER, MilkCollectionActivity.this);
        wsManager = WsFactory.getWs(DeviceName.WS, MilkCollectionActivity.this);
        if (maManager != null)
            maManager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            waitingForMAData = false;
                            stopMaReading();
                            if (maEntity != null) {
                                mMaEntity = maEntity;
                                if (!smartCCUtil.validateFatAndSnfForZeroValues(maEntity.fat, maEntity.snf)) {
                                    Util.displayErrorToast("Invalid data, Press F10 and reset the MA", MilkCollectionActivity.this);
                                    onFinish();
                                    return;
                                }
                                setFatAndSnf(maEntity);
                                afterGettingMaData(etFat.getText().toString(), etSnf.getText().toString());
                            } else {
                                Util.displayErrorToast("Invalid data, Press F10 and reset the MA", MilkCollectionActivity.this);
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
                            waitingForMAData = false;
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
                    if (!isReject) {
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
                                    setTippingEndTime();
                                }
                            }
                        });
                    } else {
                        Log.v(PROBER, "Milk is rejected, hence ignoring fetched WS data");
                    }
                }
            });

        //For milkanalyaser
        if (ma.equalsIgnoreCase("LACTOSCAN")) {
            isLactoScan = true;
        } else if (ma.equalsIgnoreCase("EKOMILK ULTRA PRO")) {
            isEKOMilkUltra = true;
        } else if (ma.equalsIgnoreCase("EKOMILK")) {
            isEKOMILK = true;

        } else if (ma.equalsIgnoreCase("LM2") || ma.equalsIgnoreCase("LACTOPLUS")) {
            isLM2 = true;
        } else if (ma.equalsIgnoreCase("KAMDHENU")) {
            isKamDhenu = true;
        } else if (ma.equalsIgnoreCase("AKASHGANGA") && (MAbaudRate == 9600)) {
            isAkashGanga = true;
        } else if (ma.equalsIgnoreCase("AKASHGANGA") && (MAbaudRate == 2400)) {
            isEKOMilkUltra = true;
        } else if (ma.equalsIgnoreCase("INDIFOSS")) {
            isIndifoss = true;
        } else if (ma.equalsIgnoreCase("NULINE")
                || ma.equalsIgnoreCase("EKOBOND")) {
            isNuline = true;
        } else if (ma.equalsIgnoreCase("LACTOSCAN_V2")) {
            isLactoScanV2 = true;
        } else if (ma.equalsIgnoreCase("KSHEERAA")) {
            isKsheeraa = true;
        } else if (ma.equalsIgnoreCase("EKOMILK EVEN")) {
            isEkoMilkEven = true;
        } else if (ma.equalsIgnoreCase("EKOMILK_V2")) {
            isEkomilkV2 = true;
        } else if (ma.equalsIgnoreCase("LAKTAN_240")) {
            isLaktan240 = true;
            SLEEP_TIME = 100;
        } else {
            Toast.makeText(MilkCollectionActivity.this, ma + " is not configured.", Toast.LENGTH_LONG).show();
            onFinish();
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


        if (null != unCommitedReportEntity &&
                unCommitedReportEntity.collectionType.equalsIgnoreCase(Util.RECORD_STATUS_INCOMPLETE)) {
            waitingForMAData = true;
            ignoreInitialWSData = true;
            setMaEnable();
            startMaReading();
            setConnection();

        }
        //To check last temp record which was getting minimized
        else if (unCommitedReportEntity != null) {
            etFat.setText(String.valueOf(unCommitedReportEntity.fat));
            etSnf.setText(String.valueOf(unCommitedReportEntity.snf));
            etClr.setText(String.valueOf(unCommitedReportEntity.clr));
            etProtein.setText(String.valueOf(unCommitedReportEntity.protein));
            etProteinRate.setText(String.valueOf(unCommitedReportEntity.incentiveRate));

            etFat.setEnabled(false);
            etSnf.setEnabled(false);
            etClr.setEnabled(false);
            etProteinRate.setEnabled(false);
            etProtein.setEnabled(false);

            // from intent
            weightManualStatus = unCommitedReportEntity.quantityMode;
            //weight
            isWsManual = unCommitedReportEntity.quantityMode;
            //barcode entry
            isAutoManual = unCommitedReportEntity.manual;
            //milk anly
            isMaAutoManual = unCommitedReportEntity.qualityMode;

            mMaEntity = new MilkAnalyserEntity(unCommitedReportEntity);

            addedWater = unCommitedReportEntity.awm;
            temp = unCommitedReportEntity.temp;
            lactose = unCommitedReportEntity.lactose;

            waitingForMAData = false;
            ignoreInitialWSData = true;
            if (unCommitedReportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
                session.setIsChillingCenter(true);
            }

            double weight = 0;
            numberOfCans = unCommitedReportEntity.numberOfCans;

            try {
                weight = Double.valueOf(unCommitedReportEntity.quantity);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            if (weight > 0) {
                //    weightRecord = weight;
                reportEntity.setQuantity(weight);
                reportEntity.setQuantity(weight);
                kgWeight = unCommitedReportEntity.kgWeight;
                ltrWeight = unCommitedReportEntity.ltrsWeight;

                etMilkWeight.setText(String.valueOf(weightRecord));
            }

            if (checkCurdMilkQuality() &&
                    unCommitedReportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
                btnReject.setText("Enter Manual Rate");
                etRate.setText(String.valueOf(unCommitedReportEntity.rate));
                etRate.requestFocus();
                etRate.setEnabled(true);
                btnReject.setEnabled(true);
                //etMilkWeight.setEnabled(true);

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
            setConnection();

        }
        setMaxFatAndSnf();
        setQuantity();


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
            tvRate.setVisibility(View.GONE);
            etRate.setVisibility(View.GONE);
            trWeight.setVisibility(View.GONE);
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
            Toast.makeText(MilkCollectionActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
            onFinish();
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
            if (session.getIsChillingCenter()) {
                String chilingCenterFSC = amcuConfig.getChillingFATSNFCLR();
                if (chilingCenterFSC.equalsIgnoreCase("FS")) {
                    visibilityCheckFS();
                } else {
                    visibilityCheckFC();
                }
            } else {
                String collectionCenterFSC = amcuConfig.getCollectionFATSNFCLR();
                if (collectionCenterFSC.equalsIgnoreCase("FS")) {
                    visibilityCheckFS();
                } else {
                    visibilityCheckFC();
                }
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
                onSubmitNextCan(reportEntity.getQuantity());

            }
            break;
            case R.id.btnReject: {
                //Once clicked user should not able to click again

                // Toast.makeText(getBaseContext(), "click", Toast.LENGTH_LONG).show();

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
        //Moving this code
        if (!isWsManual.equalsIgnoreCase("Auto")) {
            if (amcuConfig.getKeyRateChartInKg()) {
                kgWeight = reportEntity.getQuantity();
                setLtrWeight(kgWeight);
            } else {
                ltrWeight = reportEntity.getQuantity();
                setKgWeight(ltrWeight);
            }
        }

        if (session.getIsChillingCenter() && checkCurdMilkQuality()
                && btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate")) {
            if ((unCommitedReportEntity != null &&
                    unCommitedReportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC))) {
                comingFrom = "UMCA";
                UMCA_SHIFT = unCommitedReportEntity.postShift;
                //setQuantityEnable();
                //if(saveSession.getWsManual()){
                etMilkWeight.setEnabled(true);
                Toast.makeText(MilkCollectionActivity.this, "Please Enter the weight manually", Toast.LENGTH_SHORT).show();
                //}
            }

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
                //setAmount(weightRecord);
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
                Toast.makeText(MilkCollectionActivity.this, "Enter valid quality parameters!", Toast.LENGTH_SHORT).show();
            }
        } else if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            if (checkForValidData(false)) {
                onPrintOrReject();
            } else {
                onFinish();
            }
        } else {

            if ((isReject && checkForValidData(false))
                    || (!isReject && checkForValidData(true)) ||
                    Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                    || Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                    || Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                if (!checkForManualWeight()) {
                    collectionHelper.showAlertForManualWeight(CollectionHelper.FROM_MILKCOLLECTION);
                } else {
                    onPrintOrReject();
                }

            } else if ((checkForValidData(false) && session.getIsSample())) {
                onPrintOrReject();
            } else {
                Toast.makeText(MilkCollectionActivity.this, "Enter all valid values!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onPrintOrReject() {
        btnReject.setEnabled(false);
        btnReject.setFocusable(false);

        if (isEKOMilkUltra && isMaAutoManual.equalsIgnoreCase("Auto")) {
            amcuConfig.setMilkAnalyserPrvData(EKOmilkUltraMessage);
        }
        weighTime = Util.getTodayDateAndTime(6, MilkCollectionActivity.this, true);
        quantityTime = calendar.getTimeInMillis();
        getClr(etFat.getText().toString(), etSnf.getText().toString());

        if (timeOut) {
            onFinish();
        } else if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {

            if (etFat.getText().toString() != null
                    && etFat.getText().toString().replace(" ", "").length() > 0) {
                onAcceptMilk();
            } else {
                tareWSOverSerialManager();
                closeWSConnection();
                onFinish();
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
                closeWSConnection();

            } else {
                stopMaReading();
            }
            addToDatabase();
            onFinish();
        } else {
            if (true == printEnabled) {
                printEnabled = false;
                onAcceptMilk();
            }
        }
    }

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

        if (session.getIsChillingCenter()) {
            spinner.setVisibility(View.VISIBLE);
            tvQualityOfMilk.setVisibility(View.VISIBLE);
        }
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


        txtSID = (TextView) findViewById(R.id.txtSID);
        etSId = (EditText) findViewById(R.id.etSId);

        txtSID.setVisibility(View.GONE);
        etSId.setVisibility(View.GONE);

    }
//Writing in sdcards

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
        etProtein.setEnabled(false);
        etProteinRate.setEnabled(false);
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
            etProteinRate.setEnabled(false);
            etProtein.setEnabled(false);
        }
        etAmount.setEnabled(false);
        etFarmerId.setEnabled(false);
        etFarmerName.setEnabled(false);
        etRate.setEnabled(false);
        etMilkWeight.setEnabled(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.getIsChillingCenter()) {
            spinner.setVisibility(View.VISIBLE);
            tvQualityOfMilk.setVisibility(View.VISIBLE);
        }
        DATE = Util.getDateAndTimeRDU(1);
        TIME = Util.getDateAndTimeRDU(0);
        if (etFat.getText().toString().length() < 1) {
            setMaEnable();
        }
    }

    public String getRateFromRateChart(double snf, double fat) {
        String rate = "0.00";

        MilkAnalyserEntity maEntity = smartCCUtil.getMAEntity(fat, snf,
                Double.parseDouble(etClr.getText().toString().trim()));
        rate = smartCCUtil.getRateFromRateChart(maEntity, amcuConfig.getRateChartName());
        if (rate == null) {
            rate = "0.0";
        }
      /*  Rate = Double.parseDouble(decimalFormatRate.format(Double
                .parseDouble(rate)));*/
        reportEntity.setRate(Double.valueOf(rate));
        etRate.setText(String.valueOf(reportEntity.getDisplayRate()));
        if (etMilkWeight.getText().toString().equalsIgnoreCase("")) {
            etAmount.setText(String.valueOf("0.00"));
        }
        return String.valueOf(reportEntity.getRate());
    }

    public void setWeightAndAmountManually(double record) {

        double amt;
        try {
            reportEntity.setQuantity(record);
//            if (!isWsManual.equalsIgnoreCase("Auto"))
//                record = weight_Record;
//            Rate = Double.parseDouble(decimalFormatRate.format(Rate));
            amt = reportEntity.getRate() * reportEntity.getRateCalculationQuanity();

            calculateBonusAndIncentive(amt, reportEntity.getRateCalculationQuanity());
            //Calculating bonus amount
//            bonusAmt = getBonus() * Double.parseDouble(record);
//            bonusAmount = decimalFormatAmount.format(bonusAmt);
//            incentiveAmount = Double.parseDouble(inCentiveRate) *  Double.parseDouble(record);
//
//            amt = amt + bonusAmt;
//
//            amountRecord = decimalFormatAmount.format(amt);
//            //etAmount.setText(amountRecord);
//
//                               if(saveSession.getKeyAllowProteinValue())
//                               {
//                                   totalAmount = amt +incentiveAmount;
//                                   etAmount.setText(String.valueOf(decimalFormatAmount.format(totalAmount)));
//                               }else
//                               {
//                                   totalAmount = amt;
//                                   etAmount.setText(Util.getAmount(getApplicationContext(), amountRecord, bonusAmount));
//                               }


            //   weightRecord = reportE;
            if (!btnReject.isEnabled()) {
                isBtnEnable = true;
                btnReject.setEnabled(true);
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

    }

    public void setWeightAndAmount(double record) {
        double amt;

        //if conversion factor is enabled, it will consider milk as in liter format
        try {

            double weight_Record = record;
            weight_Record = weight_Record / amcuConfig.getWeighingDivisionFactor();
            reportEntity.setQuantity(weight_Record);

           /* if (!saveSession.getAllowInKgformat() && saveSession.getMyRateChartEnable()) {
                ltrWeight = weight_Record;
                record = ltrWeight *
                        Double.parseDouble(saveSession.getConversionFactor());
                kgWeight = record;
            }

            if (saveSession.getAllowInKgformat()) {
                kgWeight = weight_Record;
                ltrWeight = kgWeight /
                        Double.parseDouble(saveSession.getConversionFactor());
            } else {
                ltrWeight = weight_Record;
                kgWeight = ltrWeight *
                        Double.parseDouble(saveSession.getConversionFactor());
            }

            if (saveSession.getKeyRateChartInKg()) {
                record = kgWeight;
            } else {
                record = ltrWeight;
            }*/

            etMilkWeight.setText(String.valueOf(reportEntity.getDisplayQuantity()));
            //  Rate = Double.parseDouble(decimalFormatRate.format(Rate));
            amt = reportEntity.getRate() * reportEntity.getRateCalculationQuanity();

            calculateBonusAndIncentive(amt, reportEntity.getRateCalculationQuanity());
            //  weightRecord = record;

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
        super.onStop();
    }

    private void stopPingService() {
//        pingHandler.removeCallbacks(pingRunnable);
    }

    @Override
    protected void onDestroy() {
        stopMaReading();
        closeWSConnection();
        storeLastPingValues();
//        stopPingService();
        super.onDestroy();
    }

    private void storeLastPingValues() {
        amcuConfig.setMaPingValue(maPing);
        amcuConfig.setWsPingValue(wsPing);
        amcuConfig.setRduPingValue(rduPing);
        amcuConfig.setPrinterPingValue(printerPing);
    }

    //    TODO Set quality params directly from MaEntity instead of picking from EditTexts in UI
    public void setReportFromUI() {
        reportEntity.fat = Double.parseDouble(etFat.getText().toString().trim());
        reportEntity.snf = Double.parseDouble(etSnf.getText().toString().trim());
        reportEntity.protein = Double.parseDouble(etProtein.getText().toString().trim());
        if (mMaEntity != null) {
            reportEntity.awm = mMaEntity.addedWater;
            reportEntity.lactose = mMaEntity.lactose;
            reportEntity.calibration = mMaEntity.calibration;
            reportEntity.conductivity = mMaEntity.conductivity;
            temp = mMaEntity.temp;
        }
        reportEntity.temp = temp;

        if (etClr.getText().toString().trim().length() > 0) {
            reportEntity.clr = validationHelper.getDoubleFromString(
                    etClr.getText().toString().trim(), 0);
        } else {
            reportEntity.clr = clr;
        }
        reportEntity = smartCCUtil.getFormattedReportEntity(reportEntity);
        reportEntity.milkType = setMilkType();
        reportEntity.rate = reportEntity.getRate();
        reportEntity.amount = amountRecord;

        if (amcuConfig.getAllowInKgformat() == amcuConfig.getKeyRateChartInKg()) {
           /* reportEntity.quantity = Double.parseDouble(checkValidation.getDoubleFromString(decimalFormatQuantity
                    , etMilkWeight.getText().toString().trim(), 0.0));*/
            reportEntity.quantity = reportEntity.getQuantity();
        } else {
            reportEntity.quantity = Double.parseDouble(validationHelper.getDoubleFromString(decimalFormat2Digit
                    , etMilkWeight.getText().toString().trim(), 0.0));
        }

        reportEntity.bonus = bonusAmount;

        if (isReject) {
            reportEntity.quantity = 0;
        }


    }

/*    // ---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message) {

        if (Util.settingEntity.msgLimit != null) {
            session.setPerDayMessageLimit(Integer
                    .parseInt(Util.settingEntity.msgLimit));
        }
        if (session.getMessageCount() > session.getPerDayMessageLimit()) {

            Toast.makeText(MilkCollectionActivity.this,
                    "Today's sms limit is over!", Toast.LENGTH_SHORT).show();

        } else {
            String SENT = "SMS_SENT";
            String DELIVERED = "SMS_DELIVERED";

            PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(SENT), 0);

            PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                    new Intent(DELIVERED), 0);

            message = message;

            // ---when the SMS has been sent---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            session.setMessageCount(session.getMessageCount() + 1);
                            Toast.makeText(getBaseContext(), "SMS sent",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                            Toast.makeText(getBaseContext(), "Generic failure",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NO_SERVICE:
                            Toast.makeText(getBaseContext(), "No service",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_NULL_PDU:
                            Toast.makeText(getBaseContext(), "Null PDU",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case SmsManager.RESULT_ERROR_RADIO_OFF:
                            Toast.makeText(getBaseContext(), "Radio off",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(SENT));

            // ---when the SMS has been delivered---
            registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context arg0, Intent arg1) {
                    switch (getResultCode()) {
                        case Activity.RESULT_OK:
                            Toast.makeText(getBaseContext(), "SMS delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        case Activity.RESULT_CANCELED:
                            Toast.makeText(getBaseContext(), "SMS not delivered",
                                    Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, new IntentFilter(DELIVERED));

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
        }
    }*/

    public void createSMSText() {

        Totrate = etAmount.getText().toString();
        if (Totrate.replace(" ", "").equalsIgnoreCase(".00")) {
            Totrate = "0.0";
        }
        strRate = String.valueOf(reportEntity.getRate());
        if (strRate.replace(" ", "").equalsIgnoreCase(".00")) {
            strRate = "0.0";
        }
        if (amcuConfig.getKeyAllowProteinValue()) {
            try {
                double amount = (Double.parseDouble(etMilkWeight.getText().toString()) * Double.parseDouble(strRate));
                Message = "\n" + "\n" + smartCCUtil.getReportFormatDate() + "/"
                        + currentShift + "\n" + session.getFarmerName() + "\n"
                        + "QTY :  " + etMilkWeight.getText().toString() + "L" + "\n"
                        + "FAT :  " + etFat.getText().toString() + "\n" + "SNF :  "
                        + etSnf.getText().toString() + "\n"
                        + "Protein : " + etProtein.getText().toString() + "\n"
                        + "RATE :  " + strRate
                        + " Rs" + "\n" + "Amount :  " + amount + " Rs" + "\n"
                        + "Incentive :" + incentiveAmount + "\n"
                        + "Total Amount :" + Totrate + "\n";
            } catch (Exception e) {

            }


        } else {
            Message = "\n" + "\n" + smartCCUtil.getReportFormatDate() + "/"
                    + currentShift + "\n" + session.getFarmerName() + "\n"
                    + "QTY :  " + etMilkWeight.getText().toString() + "L" + "\n"
                    + "FAT :  " + etFat.getText().toString() + "\n" + "SNF :  "
                    + etSnf.getText().toString() + "\n" + "RATE :  " + strRate
                    + " Rs" + "\n" + "Amount :  " + Totrate + " Rs" + "\n" + "\n";

        }

    }

    public void onAcceptMilk() {

        dbError = addToDatabase();
        tareWSOverSerialManager();
        createSMSText();
        if (dbError != null) {
            Util.displayErrorToast("Db error occurred", MilkCollectionActivity.this);
            setReportData();
            afterDbError();
        }
        writeOnSDCard();
        AfterAddingDatabaseTable();
        onFinish();

    }

    public void AfterAddingDatabaseTable() {

        try {
            startRDU();
        } catch (Exception e) {
            e.printStackTrace();
        }

        printReceipt();
        tareWeighingScale();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        closeWSConnection();
    }

    private void startMaReading() {
        if (maManager != null)
            maManager.startReading();
         /*new Thread(new Runnable() {
                @Override
                public void run() {
                    maManager.startReading();
                }
            }).start();*/
    }

    private void openWsConnection() {
        if (wsManager != null) {
            wsManager.openConnection();
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        wsManager.startReading();
                    }
                }).start();*/
        }
    }

    public void setConnection() {
        if (session.getIsSample()
                && (Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
            checkTimeout();
        }

    }

    public void setKannada() {
        tvFatAuto.setText(getResources().getString(R.string.fatk));
        tvSnfAuto.setText(getResources().getString(R.string.snfk));
        btnReject.setText(getResources().getString(R.string.rejectk));

    }

    public void startRDU() {

        RduManager rduManager = RduFactory.getRdu(amcuConfig.getRDU(), MilkCollectionActivity.this);
        if (rduManager != null) {
            rduManager.openConnection();
            ReportEntity reportEntity = setReportEntity(1);
            rduManager.displayReport(reportEntity, amcuConfig.getEnableIncentiveRDU());
        } else {
            Toast.makeText(MilkCollectionActivity.this,
                    "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
        }

    }

    public String setMilkType() {
        String cattleType;

        String farmerCattleType = null;
        try {
            farmerCattleType = getIntent().getStringExtra("FarmerCattleType");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit()) ||
                Util.checkIfWaterCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit()) ||
                Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            cattleType = "TEST";
        } else if (session.getIsSample() && farmerCattleType != null) {
            cattleType = farmerCattleType;
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
        reportEntity.milkType = cattleType;
        return reportEntity.milkType;
    }
//For lactoscan v2

    public void readWeightData() throws IOException {

        stopMaReading();

        if ((null != comingFrom && comingFrom.equalsIgnoreCase("UMCA"))) {

        } else {
            openWsConnection();
        }
    }

    public void tareWeighingScale() {
        if (wsManager != null) {
            countTare = countTare + 1;
            wsManager.tare();
        }

    }

    public void writeOnSDCard() {
        try {
            smartCCUtil.saveReportsOnSdCard(reportEntity);
            showToast("Write to SDCard ");
        } catch (Exception e) {
            AfterAddingDatabaseTable();
            Util.restartTab(MilkCollectionActivity.this);
            e.printStackTrace();
        }
    }

    public String addToDatabase() {

        showToast("Add to database ");
        String dbError = null;

        reportEntity = setReportEntity(1);
        reportEntity.resetSentMarkers();
        if (reportEntity != null) {
            Util.setDailyDateOrShift(Util.getTodayDateAndTime(7, MilkCollectionActivity.this, true),
                    reportEntity.postShift);
            collectionRecordDao.saveOrUpdate(reportEntity);
            session.setReportData(null);
            showToast("Added to database");
        } else {
            dbError = "Error while creating collection record.";
        }

        return dbError;
    }

    public void afterDbError() {
        if (Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                || !session.getIsSample()) {
            try {

                printReceipt();
                printReceipt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(MilkCollectionActivity.this, "Tab restart is required.",
                Toast.LENGTH_LONG).show();
        Util.restartTab(MilkCollectionActivity.this);
        //   alertForDatabaseFailure();
    }

    public ReportEntity setReportEntity(int committed) {

        setReportFromUI();


        getIncentive(reportEntity.amount);
        reportEntity.bonus = bonusAmount;
        if (null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) {
            reportEntity.postShift = UMCA_SHIFT;
        } else {
            reportEntity.postShift = currentShift;
        }

        reportEntity.milkAnalyserTime = qualityTime;
        reportEntity.weighingTime = quantityTime;

        if (session.getIsSample()
                || Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            if (isCleaningFailed) {
                reportEntity.status = "Failure";
            } else {
                reportEntity.status = "Success";
            }
        } else if (isReject) {
            reportEntity.status = "Reject";
            Util.setCollectionStartedWithMilkType(reportEntity.milkType, MilkCollectionActivity.this);
        } else {
            reportEntity.status = "Accept";
            Util.setCollectionStartedWithMilkType(reportEntity.milkType, MilkCollectionActivity.this);
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

        reportEntity.recordCommited = committed;

        if (session.getIsSample() ||
                Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            reportEntity.collectionType = Util.REPORT_TYPE_SAMPLE;
        } else if (session.getIsChillingCenter()) {
            reportEntity.collectionType = Util.REPORT_TYPE_MCC;
        } else {
            String type = databaseHandler.getFarmerType(reportEntity.farmerId);
            if (type != null && type.equalsIgnoreCase(AppConstants.FARMER_TYPE_FARMER)) {
                reportEntity.collectionType = Util.REPORT_TYPE_FARMER;
            } else {
                reportEntity.collectionType = Util.REPORT_TYPE_FARMER;
                //  reportEntity.collectionType = Util.REPORT_TYPE_AGENT;
            }
        }
        if (session.getIsChillingCenter()) {
            reportEntity.milkQuality = spinnerItem;
        } else {
            reportEntity.milkQuality = "NA";
        }
        reportEntity.rateMode = rateMode;
        reportEntity.numberOfCans = numberOfCans;
        reportEntity.centerRoute = Util.getRouteFromChillingCenter(MilkCollectionActivity.this, session.getFarmerID());
//        if (committed == 0) {
//            reportEntity.recordStatus = Util.RECORD_STATUS_INCOMPLETE;
//        } else {
        reportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;
        //   }

        reportEntity.rateChartName = amcuConfig.getRateChartName();
        CollectionHelper collectionHelper = new CollectionHelper(MilkCollectionActivity.this);
        QuantityEntity quantityEntity =
                collectionHelper.getQuantityItems(reportEntity.quantity);

        if (isWsManual.equalsIgnoreCase("Manual")) {
            kgWeight = quantityEntity.kgQuantity;
            ltrWeight = quantityEntity.ltrQuanity;
        }

        reportEntity.kgWeight = kgWeight;
        reportEntity.ltrsWeight = ltrWeight;

        if (committed == 1 && (reportEntity.quantityMode == null
                || reportEntity.quantityMode.equalsIgnoreCase("Manual")
                || tippingStartTime == 0)) {
            setTippingTimeForWSManual();
        } else {
            setTippingEndTime();
        }

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
        reportEntity.sampleNumber = 0;

        reportEntity.serialMa = 1;
        reportEntity.maName = amcuConfig.getMA();

        reportEntity.incentiveRate = inCentiveRate;
        reportEntity.incentiveAmount = incentiveAmount;
        reportEntity.fatKg = Util.convertPercentageToKg(kgWeight, reportEntity.fat);
        reportEntity.snfKg = Util.convertPercentageToKg(kgWeight, reportEntity.snf);
        reportEntity.postDate = new SmartCCUtil(MilkCollectionActivity.this).getReportFormatDate();
        reportEntity.postShift = Util.getCurrentShift();
        smartCCUtil.setCollectionStartData(reportEntity);

        return reportEntity;
    }


    // to evaluate accept or reject milk from ratechart
    public void evaluateAcceptOrReject() {
        btnReject.setFocusable(true);
        milkAnanalyserTime = Util.getTodayDateAndTime(6, MilkCollectionActivity.this, true);
        qualityTime = calendar.getTimeInMillis();
        String fat, snf, clr, protein;
        reportEntity.setFat(validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0));
        reportEntity.setSnf(validationHelper.getDoubleFromString(etSnf.getText().toString().trim(), -1));
        reportEntity.setFat(validationHelper.getDoubleFromString(etClr.getText().toString().trim(), 0));
        reportEntity.setSnf(validationHelper.getDoubleFromString(etProtein.getText().toString().trim(), 0));

    /*    fat = etFat.getText().toString().replace(" ", "");
        snf = etSnf.getText().toString().replace(" ", "");
        clr = etClr.getText().toString().trim();
        protein = etProtein.getText().toString().trim();*/
        if (reportEntity.getSnf() == -1) {
            reportEntity.setSnf(Util.getSNF(reportEntity.getFat(),
                    reportEntity.getClr()));
        }

        if (unCommitedReportEntity == null && amcuConfig.getAllowMaxLimitFromRateChart()) {
            if (reportEntity.getSnf() == -1) {
                reportEntity.setSnf(Util.getSNF(reportEntity.getFat(),
                        reportEntity.getClr()));
                etSnf.setText(String.valueOf(reportEntity.getDisplaySnf()));
            }
            resetFatAndSnf(String.valueOf(reportEntity.getDisplayFat()),
                    String.valueOf(reportEntity.getDisplaySnf()));
         /*   fat = etFat.getText().toString().replace(" ", "");
            snf = etSnf.getText().toString().replace(" ", "");
            clr = etClr.getText().toString().trim();*/
        }

        if (unCommitedReportEntity == null) {
            if (((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") && session.getIsChillingCenter())
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")
                    && !session.getIsChillingCenter())) && amcuConfig.getMaManual()) {
                reportEntity.setSnf(Util.getSNF(reportEntity.getFat(),
                        reportEntity.getClr()));
                etSnf.setText(String.valueOf(reportEntity.getSnf()));
                resetFatAndSnf(String.valueOf(reportEntity.getFat()), String.valueOf(reportEntity.getSnf()));
                /* snf = etSnf.getText().toString().replace(" ", "");*/

            } else if (((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FS") && session.getIsChillingCenter())
                    || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FS")
                    && !session.getIsChillingCenter())) && amcuConfig.getMaManual()) {
                reportEntity.setClr(Double.valueOf(
                        Util.getCLR(reportEntity.getFat(),
                                reportEntity.getSnf())
                ));
                etClr.setText(String.valueOf(reportEntity.getClr()));
            }

            if ((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")
                    || amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC"))
                    && etClr.getText().toString().trim().length() > 0) {
                // clr = etClr.getText().toString().trim();
                etClr.setText(String.valueOf(reportEntity.getClr()));
                if (reportEntity.getSnf() > -1) {

                } else {

                    reportEntity.setSnf(Util.getSNF(reportEntity.getFat(),
                            reportEntity.getClr()));
                    etSnf.setText(String.valueOf(reportEntity.getSnf()));

                }
                resetFatAndSnf(String.valueOf(reportEntity.getFat()),
                        String.valueOf(reportEntity.getSnf()));

            } else {
                clr = String.valueOf(Util.getCLR(reportEntity.getFat()
                        , reportEntity.getSnf()));
                etClr.setText(clr);
            }
        }
        snf = etSnf.getText().toString().replace(" ", "");
        getRateFromRateChart(reportEntity.getRateCalculationSnf(),
                reportEntity.getRateCalculationFat());
        if (amcuConfig.getKeyAllowProteinValue()) {
            getInCentiveRate(reportEntity.getRateCalculationProtein());
        }
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        maEntity.fat = Double.parseDouble(etFat.getText().toString().trim());
        maEntity.snf = Double.parseDouble(etSnf.getText().toString().trim());

        isReject = false;
        //To add the temp records
        if (reportEntity.getFat() > Util.MAX_FAT_LIMIT ||
                reportEntity.getSnf() > Util.MAX_SNF_LIMIT
                || reportEntity.getFat() < Util.MIN_FAT_LIMIT
                || reportEntity.getSnf() < Util.MIN_SNF_LIMIT) {
            Toast.makeText(MilkCollectionActivity.this, "Invalid milk analyzer data", Toast.LENGTH_SHORT).show();
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
            //disable reject for test
            isReject = smartCCUtil.isMilkRejected(maEntity);
            //Added to support reaccept

            if (isReject && amcuConfig.getEditableRate() && (
                    !session.getIsSample() ||
                            Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
                alertReject();
            } else if (isReject) {
                try {
                    //added opening of WS connection in reject scenario so that it will be read and ignored which will avoid displaying
                    // old data in next collection. This is done because wisens module stores the old data in its buffer.
                    readWeightData();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MilkCollectionActivity.this,
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
        if (amcuConfig.getEnableFilledOrEmptyCans() && session.getIsChillingCenter()) {
            if ((null == comingFrom || !comingFrom.equalsIgnoreCase("UMCA"))) {
                btnWeight.setVisibility(View.VISIBLE);
                btnWeight.setText("Next weight");
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (amcuConfig.getKeyEscapeEnableCollection()
                && !session.getIsChillingCenter()) {
            databaseHandler.deleteFromDb();
            onFinish();
        }
    }

    public void onRejectMilk() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                addToDatabase();
                myHandlerReject.post(updateRunnableReject);
            }
        }).start();
        // Additional functionality like printing can be handled
        // in this handler
        updateRunnableReject = new Runnable() {
            @Override
            public void run() {
                stopMaReading();
                onFinish();
            }
        };
    }

    public void tareWSOverSerialManager() {

        if (wsManager != null) {
            wsManager.tare();
        }
    }

    public void changeToLiterWSOverSerialManager() {
        if (wsManager != null)
            wsManager.setToLitreMode();
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

    public String addDecimalPoint(String str) {

        String value;
        String[] str1 = str.split("(?<=\\G.{2})");
        value = str1[0] + "." + str1[1];
        //  value = decimalFormatFS.format(Double.parseDouble(value));
        return value;

    }

    // After getting Milk Analyser data from Auto Mode like rejet calculation
    public void afterGettingMaData(String fat, String snf) {

        if (etClr.getText().toString().trim().length() > 0) {

        } else {
            etClr.setText(String.valueOf(Util.getCLR(Double.valueOf(fat), Double.valueOf(snf))));
        }
        if ((!snf.equalsIgnoreCase("") && !amcuConfig.getMaManual()) ||
                (session.getIsSample() &&
                        !Util.checkIfSampleCode(session.getFarmerID(),
                                amcuConfig.getFarmerIdDigit()))) {
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
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(MilkCollectionActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:
                Util.restartTab(MilkCollectionActivity.this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(MilkCollectionActivity.this, null);
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
                    if (amcuConfig.getEnableFilledOrEmptyCans() && session.getIsChillingCenter() && !isReject) {
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
        FormatPrintRecords formatPrintRecord = new FormatPrintRecords(MilkCollectionActivity.this);
        String printData = formatPrintRecord.receiptFormat(MilkCollectionActivity.this, reportEntity);
        return printData;
    }

/*    public void setDecimalRoundOff() {
        if (saveSession.getDecimalRoundOffAmount() == 0) {
            decimalFormatAmount = new DecimalFormat("#0");

        } else if (saveSession.getDecimalRoundOffAmount() == 1) {
            decimalFormatAmount = new DecimalFormat("#0.0");

        } else if (saveSession.getDecimalRoundOffAmount() == 2) {
            decimalFormatAmount = new DecimalFormat("#0.00");

        } else if (saveSession.getDecimalRoundOffAmount() == 3) {
            decimalFormatAmount = new DecimalFormat("#0.000");
        }

        if (saveSession.getDecimalRoundOffWeigh() == 0) {
            decimalFormatQuantity = new DecimalFormat("#0");
        } else if (saveSession.getDecimalRoundOffWeigh() == 1) {

            decimalFormatQuantity = new DecimalFormat("#0.0");

        } else if (saveSession.getDecimalRoundOffWeigh() == 2) {
            decimalFormatQuantity = new DecimalFormat("#0.00");
        } else if (saveSession.getDecimalRoundOffWeigh() == 3) {
            decimalFormatQuantity = new DecimalFormat("#0.000");
        }

        if (saveSession.getDecimalRoundOffRate() == 0) {
            decimalFormatRate = new DecimalFormat("#0");
        } else if (saveSession.getDecimalRoundOffRate() == 1) {
            decimalFormatRate = new DecimalFormat("#0.0");
        } else if (saveSession.getDecimalRoundOffRate() == 2) {
            decimalFormatRate = new DecimalFormat("#0.00");
        } else if (saveSession.getDecimalRoundOffRate() == 3) {
            decimalFormatRate = new DecimalFormat("#0.000");
        }

        if (saveSession.getDecimalRoundOffAmountCheck()) {
            decimalFormatAmount.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormatAmount.setRoundingMode(RoundingMode.FLOOR);
        }

        if (saveSession.getDecimalRoundOffRateCheck()) {
            decimalFormatRate.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormatRate.setRoundingMode(RoundingMode.FLOOR);
        }

        if (saveSession.getDecimalRoundOffWeightCheck()) {
            decimalFormatQuantity.setRoundingMode(RoundingMode.HALF_UP);
            decimalFormat2Digit.setRoundingMode(RoundingMode.HALF_UP);
        } else {
            decimalFormatQuantity.setRoundingMode(RoundingMode.FLOOR);
            decimalFormat2Digit.setRoundingMode(RoundingMode.FLOOR);
        }

    }*/

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

    public double getClr(String fat, String snf) {
        double mClr = 25.0;

        DecimalFormat decimalClr = new DecimalFormat("#0");
        if (!fat.equalsIgnoreCase("") && !snf.equalsIgnoreCase("")
                && fat.length() > 0 && snf.length() > 0) {
            try {
              /*  snf = decimalFormatSNF.format(Double.parseDouble(snf));
                fat = decimalFormatFat.format(Double.parseDouble(fat));*/
                mClr = (((amcuConfig.getSnfCons() * reportEntity.getSnf()) - ((amcuConfig
                        .getFatCons() * reportEntity.getFat())) - (amcuConfig
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
        tvHeader.setText(session.getSocietyName());
    }

    public void setReportData() {

        String records = session.getFarmerID() + "=" + session.getFarmerName() + "=" + "barcode" + "=" + session.getCollectionID() + "="
                + etFat.getText().toString() + "=" + etSnf.getText().toString() + "=" + reportEntity.rate + "=" + reportEntity.getQuantity() + "=" + amountRecord + "=" + "84.0"
                + "=" + "0.0" + "=" + reportEntity.postDate + "=" + currentShift + "=" + reportEntity.time + "=" + reportEntity.milkType + "="
                + "0000" + "=" + String.valueOf(calendar.getTimeInMillis()) + "=" +
                String.valueOf(calendar.getTimeInMillis()) + "="
                + String.valueOf(calendar.getTimeInMillis()) + "=" + "0.0" + "=" + reportEntity.status + "=" + isAutoManual + "=" + "0.00" + "=" + isMaAutoManual + "=" + isWsManual;


        session.setReportData(records);
    }

    public void saveIncompleteRecord() {
        DatabaseManager dbm = new DatabaseManager(MilkCollectionActivity.this);
        setReportEntity(Util.REPORT_NOT_COMMITED);
        try {
            long columnId = collectionRecordDao.saveOrUpdate(reportEntity);
            reportEntity.setPrimaryKeyId(columnId);
        } catch (Exception e) {
            e.printStackTrace();
        }
//DB close removed;
    }

    public void onFinish() {
        //If collection time is over then for sample records need to end shift

        boolean isShiftOver = smartCCUtil.isSessionTimeOver(Util.getCurrentShift());

        if (isShiftOver) {
            AfterLogInTask afterLogInTask = new AfterLogInTask(MilkCollectionActivity.this);
            afterLogInTask.registerEndShiftAlarm(System.currentTimeMillis());

        }

        startActivity(new Intent(MilkCollectionActivity.this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

    public void initializeReportEntity() {

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
            reportEntity.time = Util.getTodayDateAndTime(3, MilkCollectionActivity.this, true);
            reportEntity.miliTime = calendar.getTimeInMillis();
            reportEntity.txnNumber = session.getTXNumber() + 1;
        } else {
            unCommitedReportEntity = reportEntity;
        }

    }

    public void stopMaReading() {
        if (maManager != null) {
            maManager.stopReading();
        }
    }

    public void closeWSConnection() {
        if (wsManager != null)
            wsManager.closeConnection();
    }


    //Alert dialog if mulitiple cans

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

            if (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") && session.getIsChillingCenter()) {
                etSnf.setEnabled(false);
                etSnf.setFocusable(false);
                etClr.setEnabled(true);
                etClr.setFocusable(true);

            } else if (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") && !session.getIsChillingCenter()) {
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
        //session.setIsLastMACollection(false);

        if ((null != comingFrom && comingFrom.equalsIgnoreCase("UMCA"))) {
            etMilkWeight.setEnabled(false);
            setWeightAndAmountManually(Double.parseDouble(etMilkWeight.getText().toString().trim()));
        } else if (amcuConfig.getWsManual() && !isReject) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            etMilkWeight.setFocusable(true);
            etMilkWeight.setEnabled(true);
            etMilkWeight.setCursorVisible(true);
            etMilkWeight.setSelection(0);
            etMilkWeight.requestFocus();
            Util.alphabetValidation(etMilkWeight, Util.ONLY_DECIMAL, MilkCollectionActivity.this, 8);
            textChangeFunction();

        } else if (etMilkWeight.isEnabled()) {
            etMilkWeight.setFocusable(false);
            etMilkWeight.setEnabled(false);
        }
    }

    public void textChangeFunction() {
        etMilkWeight.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    if (amcuConfig.getEnableFilledOrEmptyCans() && session.getIsChillingCenter() && !isReject) {
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
                    double qty;
                    String weight = etMilkWeight.getText().toString().replace(" ", "");
                    qty = Double.parseDouble(weight);
                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(qty);

                } else if (session.getIsChillingCenter() && checkCurdMilkQuality()) {
                    String weight = etMilkWeight.getText().toString().replace(" ", "");
                    double qty = Double.parseDouble(weight);
                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(qty);
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
                if (session.getIsChillingCenter() && checkCurdMilkQuality()) {
                    String rate = etRate.getText().toString().replace(" ", "");
                    try {
                        reportEntity.setRate(Double.parseDouble(rate));
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }


                    btnReject.setEnabled(true);
                    setWeightAndAmountManually(Double.parseDouble(etMilkWeight.getText().toString()));
                }
            }
        });

    }

    public void checkForManualMAorWS(int keyValue) {

        if ((null != comingFrom && comingFrom.equalsIgnoreCase("UMCA"))
                && !checkCurdMilkQuality()
                && btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate") && !etRate.isEnabled()) {
            btnReject.setText("Read Rate");
            stopMaReading();
            waitingForMAData = false;


        } else if ((null != comingFrom && comingFrom.equalsIgnoreCase("UMCA")) && checkCurdMilkQuality()
                && !btnReject.getText().toString().equalsIgnoreCase("Enter Manual Rate") && !etRate.isEnabled()) {
            btnReject.setText("Enter Manual Rate");
            stopMaReading();
            waitingForMAData = false;


        } else if (btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
            // no code for this block since we dont want to enter any data to weight text
            onRateChange();
        } else if (amcuConfig.getMaManual() && ((btnReject.getText().toString().equalsIgnoreCase("Read weight")) ||
                waitingForMAData)) {
            Util.alphabetValidation(etFat, Util.ONLY_DECIMAL, MilkCollectionActivity.this, 6);
            Util.alphabetValidation(etSnf, Util.ONLY_DECIMAL, MilkCollectionActivity.this, 6);
            // Util.alphabetValidation(etClr,Util.ONLY_NUMERIC,MilkCollectionActivity.this,2);
            isMaAutoManual = "Manual";
            waitingForMAData = false;
            stopMaReading();
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
                Toast.makeText(MilkCollectionActivity.this, "Enter fat/snf manually!", Toast.LENGTH_SHORT).show();
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
                Util.alphabetValidation(etMilkWeight, Util.ONLY_DECIMAL, MilkCollectionActivity.this, 8);
                textChangeFunction();
                etMilkWeight.requestFocus();
                etMilkWeight.setText(String.valueOf(keyValue));
                etFat.setCursorVisible(true);
                int pos = etMilkWeight.length();
                etMilkWeight.setSelection(pos);
                isWsManual = "Manual";
                Toast.makeText(MilkCollectionActivity.this, "Enter weight manually!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public boolean checkForValidData(boolean allValid) {

        double dfat = -1, dSnf = -1, dClr = -1, dQuantity = -1;
        String snf = etSnf.getText().toString().replace(" ", "");
        String fat = etFat.getText().toString().replace(" ", "");
        String clr = etClr.getText().toString().trim();

        if (snf.length() > 0 && fat.length() > 0) {
            try {
                dfat = Double.parseDouble(fat);
                dSnf = Double.parseDouble(snf);
            } catch (NumberFormatException e) {
                e.printStackTrace();

                Util.displayErrorToast("Invalid quality parameters!", MilkCollectionActivity.this);
                return false;
            }
        }


        String protein = etProtein.getText().toString().replace(" ", "");
        if (((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") && session.getIsChillingCenter())
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")
                && !session.getIsChillingCenter())) && amcuConfig.getMaManual()) {
            if (fat.length() > 0 && clr.length() > 0) {
                snf = String.valueOf(Util.getSNF(validationHelper.getDoubleFromString(fat, 0), validationHelper.getDoubleFromString(clr, 0)));
                dSnf = Double.parseDouble(snf);
                dfat = Double.parseDouble(fat);
            }
        } else if (((amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FS") && session.getIsChillingCenter())
                || (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FS")
                && !session.getIsChillingCenter())) && amcuConfig.getMaManual()) {
            if (fat.length() > 0 && snf.length() > 0) {
                clr = String.valueOf(Util.getCLR(Double.valueOf(fat), Double.valueOf(snf)));
            }

        }


        String weight = etMilkWeight.getText().toString().replace(" ", "");
        try {
            dQuantity = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (!allValid && (etFat.getText().toString().length() > 0 &&
                (etSnf.getText().toString().length() > 0 ||
                        etClr.getText().toString().length() > 0))) {

            if (amcuConfig.getKeyAllowProteinValue() && !session.getIsChillingCenter()) {

                double proteinValue = validationHelper.getDoubleFromString(etProtein.getText().toString().trim(), -1);

                boolean proteinValidation = validationHelper.isValidProtein(proteinValue, MilkCollectionActivity.this);

                if (!proteinValidation) {
                    return false;
                }


            }


            return validationHelper.isValidFatAndSnf(dfat, dSnf, MilkCollectionActivity.this);
        } else if (allValid) {

            if (dfat >= 0 && dSnf >= 0 && dQuantity > 0) {
                if (isWsManual.equalsIgnoreCase("Auto") && !isFilledOrEmptyCanStarted) {
                    return validationHelper.validMilkWeight(dQuantity, getApplicationContext());
                } else if (isFilledOrEmptyCanStarted || isWsManual.equalsIgnoreCase("Manual")) {
                    return true;
                } else {
                    return validationHelper.validMilkWeight(dQuantity, getApplicationContext());
                }

            } else {
                if (Util.checkIfSampleCode(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
                    Util.displayErrorToast("Invalid weight "
                            , MilkCollectionActivity.this);
                }
                return false;
            }

        }
        return false;
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


                if (checkForValidData(false) && allDeviceData.size() < 2) {
                    isMaAutoManual = "Manual";
                    waitingForMAData = false;
                    stopMaReading();
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

                if (checkForValidData(false) && allDeviceData.size() < 2) {
                    isMaAutoManual = "Manual";
                    waitingForMAData = false;
                    stopMaReading();
                    btnReject.setEnabled(true);

                    if (!session.getIsSample() || Util.checkIfSampleCode(session.getFarmerID(),
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

                if (checkForValidData(false) && allDeviceData.size() < 2) {
                    isMaAutoManual = "Manual";
                    waitingForMAData = false;
                    stopMaReading();
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
                    if (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") && session.getIsChillingCenter()) {
                        etClr.requestFocus();
                        int position = etClr.length();
                        etClr.setCursorVisible(true);
                        etClr.setSelection(position);
                    } else if (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") && !session.getIsChillingCenter()) {
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

//        etSnf.setOnKeyListener(new View.OnKeyListener() {
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (((event.getAction() == KeyEvent.ACTION_UP) &&
//                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
//                        (event.getAction() == KeyEvent.ACTION_DOWN &&
//                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
//                    if (btnReject.isEnabled()) {
//                        onClickButtonReject();
//                    }
//                    if ((keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
//                        isFatSnfPressed = true;
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });


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

    public double getBonusAmount() {
        double bonus = 0;
        MilkAnalyserEntity maEntity = smartCCUtil.getMAEntity(validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0),
                validationHelper.getDoubleFromString(etSnf.getText().toString().trim(), 0),
                validationHelper.getDoubleFromString(etClr.getText().toString().trim(), 0));
        CollectionHelper collectionHelper = new CollectionHelper(MilkCollectionActivity.this);
        bonus = collectionHelper.getBonusAmount(session.getFarmerID(), session.getMilkType(), maEntity);
        return bonus;
    }

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
        incentive = incentiveAmt;

    }

    public void openMilkStateDialog(double actualData) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MilkCollectionActivity.this);
        LayoutInflater inflater = MilkCollectionActivity.this.getLayoutInflater();
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

        btnAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if (radioStatus.equalsIgnoreCase("empty") && (
                        weightRecord < newRcordFilledCan)) {
                    Util.displayErrorToast("Invalid can weight", MilkCollectionActivity.this);
                } else if (validationHelper.validMilkWeight(newRcordFilledCan, MilkCollectionActivity.this)) {
                    weightRecord = Double.parseDouble(tvTotal.getText().toString());
                    etMilkWeight.setText(String.valueOf(weightRecord));
                    ltrWeight = ltrWeight + tempLt;
                    kgWeight = kgWeight + tempKg;
                    setAmount(weightRecord);
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

        btnCancel.setOnClickListener(new OnClickListener() {
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
        tvPreviousData.setText(String.valueOf(weightRecord));
        double minLimit, currentRecord, maxLimit;
        record = getWeightRecordForAlert(Double.valueOf(record));
        currentRecord = record;
        newRcordFilledCan = currentRecord;

        if (radioStatus.equalsIgnoreCase("filled")) {
            tvNewData.setText("(+) " + record);
            tvTotal.setText(decimalFormat2Digit.format(Double.parseDouble(tvPreviousData.getText().toString())
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
                tvTotal.setText(decimalFormat2Digit.format(Double.parseDouble(tvPreviousData.getText().toString())
                        - record));
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
                MilkCollectionActivity.this);
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

                        openMilkStateDialog(weightRecord);
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
                MilkCollectionActivity.this);

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
                    Rate = dEtRate;
                    double wRecord = 0;

                    try {
                        wRecord = Double.valueOf(etMilkWeight.getText().toString().trim());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (wRecord > 0) {
                        setWeightAndAmountManually(Double.parseDouble(etMilkWeight.getText().toString().trim()));
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

   /* public void setDecimalFormatForClr() {


        decimalFormatClr = new ChooseDecimalFormat().
                getDecimalFormatTypeForDisplay(AppConstants.CLR
                );

    }
*/

    public void setFatAndSnf(MilkAnalyserEntity maEntity) {
        etFat.setText(String.valueOf(maEntity.fat));
        etSnf.setText(String.valueOf(maEntity.snf));
        etProtein.setText(String.valueOf(maEntity.protein));
        etClr.setText(String.valueOf(maEntity.clr));
    }

    public void setKgWeight(double record) {
        kgWeight = record *
                Double.parseDouble(amcuConfig.getConversionFactor());
    }

    public void setLtrWeight(double record) {
        try {
            ltrWeight = record /
                    Double.parseDouble(amcuConfig.getConversionFactor());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void setMaxFatAndSnf() {
//        maxFat = databaseHandler.getMaxFATOrSNF(1, amcuConfig.getRateChartName());
//        maxSnf = databaseHandler.getMaxFATOrSNF(0, amcuConfig.getRateChartName());

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

    public void setQuantity() {
        if (amcuConfig.getKeyRateChartInKg() || amcuConfig.getMyRateChartEnable()) {
            tvMilkWeight.setText("Qty(kgs)");
        } else {
            tvMilkWeight.setText("Qty(ltrs)");
        }

    }

    public double getWeightRecordForAlert(double weight_Record) {

        String record = "0.00", mLtRecord = "0.00", mKgRecord = "0.00";
        tempLt = 0;
        tempLt = 0;

        weight_Record = weight_Record / amcuConfig.getWeighingDivisionFactor();

        if ((!amcuConfig.getAllowInKgformat() && amcuConfig.getMyRateChartEnable())
                || (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg())) {
            reportEntity.setLtrsWeight(weight_Record);
            reportEntity.setQuantity(reportEntity.getLtrsWeight() *
                    Double.parseDouble(amcuConfig.getConversionFactor()));
            reportEntity.setKgWeight(reportEntity.getQuantity());

        } else if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {
            reportEntity.setKgWeight(weight_Record);
            reportEntity.setQuantity(Double.parseDouble(mKgRecord) /
                    Double.parseDouble(amcuConfig.getConversionFactor()));
            //mLtRecord = decimalFormatQuantity.format(Double.parseDouble(record));
            reportEntity.setLtrsWeight(reportEntity.getQuantity());


        } else {
         /*   mLtRecord = decimalFormatQuantity.format(weight_Record);
            record = decimalFormatQuantity.format(weight_Record);
            mKgRecord = decimalFormatQuantity.format(Double.parseDouble(mLtRecord) *
                    Double.parseDouble(saveSession.getConversionFactor()));*/

            reportEntity.setLtrsWeight(weight_Record);
            reportEntity.setQuantity(weight_Record);
            reportEntity.setKgWeight(Double.parseDouble(mLtRecord) *
                    Double.parseDouble(amcuConfig.getConversionFactor()));

        }

        tempKg = Double.valueOf(mKgRecord);
        tempLt = Double.valueOf(mLtRecord);
        return Double.parseDouble(record);
    }

    public void setAmount(double record) {
//        Rate = Double.parseDouble(decimalFormatRate.format(Rate));
        double amt = reportEntity.getRate() * record;
        reportEntity.setAmount(amt);
        //Calculating bonus amount
        double bonusAmt = getBonusAmount() * record;
        bonusAmount = bonusAmt;
        reportEntity.setBonus(bonusAmount);
        incentiveAmount = inCentiveRate * record;
        reportEntity.setIncentiveAmount(incentiveAmount);

        amt = amt + bonusAmt;
        amountRecord = amt;
        //etAmount.setText(amountRecord);

        if (amcuConfig.getKeyAllowProteinValue()) {
            totalAmount = amt + incentiveAmount;
            etAmount.setText(String.valueOf(reportEntity.getTotalAmount()));
        } else {
            totalAmount = amt;
//            TODO Use amount from getter in ReportEntity
            etAmount.setText(String.valueOf(Util.getAmount(getApplicationContext(), reportEntity.getAmount(),
                    reportEntity.getBonus())));
        }
    }

    public String addDecimalPointGeneric(String str, DecimalFormat decimalFormat) {
        String value;
        String[] str1 = str.split("(?<=\\G.{2})");
        value = str1[0] + "." + str1[1];
        value = decimalFormat.format(Double.parseDouble(value));
        return value;
    }

    public void setTippingStartTime() {
        if (tippingStartTime == 0)
            tippingStartTime = System.currentTimeMillis();
    }

    public void setTippingEndTime() {
        tippingEndTime = System.currentTimeMillis();
    }

    public void setTippingTimeForWSManual() {
        tippingStartTime = System.currentTimeMillis();
        tippingEndTime = System.currentTimeMillis();
    }

    public boolean checkForManualWeight() {
        if (!isWsManual.equalsIgnoreCase("Manual")) {
            return true;
        }
        double dQuantity = 0;
        String weight = etMilkWeight.getText().toString().trim();
        try {
            dQuantity = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return validationHelper.validMilkWeight(dQuantity, getApplicationContext());

    }

    @Override
    public boolean exceedWeightLimit() {
        etMilkWeight.requestFocus();
        etMilkWeight.setSelection(etMilkWeight.getText().toString().length());
        return false;
    }

    @Override
    public boolean proceedWithExceedWeightLimit() {
        onPrintOrReject();
        return false;
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

    public void onSubmitNextCan(double dWeightRecord) {
        if (isWsManual.equalsIgnoreCase("Manual")) {
            return;
        }
        if (!isFilledOrEmptyCanStarted
                && validationHelper.validMilkWeight(dWeightRecord, MilkCollectionActivity.this)) {
            //If multiple can collection not yet started
            alertForRemoveCan();
        } else if (isFilledOrEmptyCanStarted && dWeightRecord > 0) {
            alertForRemoveCan();
        } else {
            Toast.makeText(MilkCollectionActivity.this,
                    "No valid weight, or enter weight manually", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProteinOption() {
        if (amcuConfig.getKeyAllowProteinValue() && !session.getIsChillingCenter()) {
            trProtein.setVisibility(View.VISIBLE);
            if (session.getIsSample()
                    && (Util.checkIfCleaning(session.getFarmerID(), amcuConfig.getFarmerIdDigit())
                    || Util.checkIfRinsing(session.getFarmerID(), amcuConfig.getFarmerIdDigit()))) {
                trProtein.setVisibility(View.GONE);
            } else if (session.getIsSample()) {
                etProteinRate.setVisibility(View.INVISIBLE);
                tvProteinRate.setVisibility(View.INVISIBLE);
            }
        } else {
            trProtein.setVisibility(View.GONE);
        }
    }

    private void getInCentiveRate(double protein) {

        if (session.getIsChillingCenter()) {
            inCentiveRate = 0;
            etProteinRate.setText(String.valueOf(inCentiveRate));
            return;
        }
        SessionManager session = new SessionManager(MilkCollectionActivity.this);
        double rate;
        DatabaseHandler databaseHandler = DatabaseHandler.getDatabaseInstance();
        rate = Double.parseDouble(databaseHandler.getIncentiveRate(String.valueOf(protein),
                amcuConfig.getInCentiveRateChartname()));
        inCentiveRate = rate;
        etProteinRate.setText(String.valueOf(inCentiveRate));

    }

    private void showToast(String message) {
        // Util.displayErrorToast(message,MilkCollectionActivity.this);
    }

    private void calculateBonusAndIncentive(double amt, double record) {
        if (session.getIsSample()
                && !Util.checkIfRateCheck(session.getFarmerID(), amcuConfig.getFarmerIdDigit())) {
            return;
        }

//        Rate = Double.parseDouble(decimalFormatRate.format(Rate));
        amt = reportEntity.getRate() * record;
        //Calculating bonus amount
        double bonusAmt = getBonusAmount() * record;
        reportEntity.setBonus(bonusAmt);
        bonusAmount = bonusAmt;
        incentiveAmount = inCentiveRate * record;
        reportEntity.setIncentiveAmount(incentiveAmount);

        amt = amt + bonusAmt;

        amountRecord = amt;
        //etAmount.setText(amountRecord);

        if (amcuConfig.getKeyAllowProteinValue()) {
            totalAmount = amt + incentiveAmount;
            etAmount.setText(String.valueOf(reportEntity.getTotalAmount()));
        } else {
            totalAmount = amt;
            etAmount.setText(String.valueOf(Util.getAmount(MilkCollectionActivity.this,
                    reportEntity.getAmount(), reportEntity.getBonus())));
        }

    }

    private double convertPercentageToKg(double qty, double fatOrSnf) {
        double returnKgValue = 0;

        try {
            returnKgValue = (qty * fatOrSnf) / 100;
        } catch (Exception e) {

        }

        return returnKgValue;

    }

    private void printReceipt() {
        printManager.print(receiptFormat(), PrinterManager.printReciept, null, null, null);
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
                    wifiUtility.checkWisensConnectivity(context, DeviceName.MILK_ANALYSER, maPing);
                if (deviceEntity.deviceName == DeviceName.WS && deviceEntity.deviceType == SmartCCConstants.WIFI)
                    wifiUtility.checkWisensConnectivity(context, DeviceName.WS, wsPing);
                if (deviceEntity.deviceName == DeviceName.RDU && deviceEntity.deviceType == SmartCCConstants.WIFI)
                    wifiUtility.checkWisensConnectivity(context, DeviceName.RDU, rduPing);
                if (deviceEntity.deviceName == DeviceName.PRINTER && deviceEntity.deviceType == SmartCCConstants.WIFI)
                    wifiUtility.checkWisensConnectivity(context, DeviceName.PRINTER, printerPing);


            }
        }
    }

}
