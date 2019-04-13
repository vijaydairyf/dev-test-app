package com.devapp.devmain.macollection;

/**
 * Created by u_pendra on 6/2/17.
 */

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.devapp.kmfcommon.UserSelectionActivity;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionCenterDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.DeviceParameters;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.user.WifiUtility;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.devmain.ws.WsFactory;
import com.devapp.devmain.ws.WsManager;
import com.devapp.smartcc.adapters.AutoTextAdapter;
import com.devapp.smartcc.entityandconstants.QuantityEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ParallelActivity extends AppCompatActivity implements View.OnClickListener {


    public static String MA1 = DeviceName.MILK_ANALYSER;
    public static ArrayList<ReportEntity> allReportEntity;
    public static ReportEntity reportEntityMA1, reportEntityMA2;
    public static boolean isMultipleMA = false;
    private final String MA2 = DeviceName.MA2;
    private final String SINGLE_MA = DeviceName.MILK_ANALYSER;
    private final String WS = DeviceName.WS;
    private final String TAG = ParallelActivity.class.getName();
    public String SELECT_ROUTE = "Select Route";
    public String SELECT_CENTER = "Select MCC";
    public String lastCenter = null;
    public String nextSequenceNumber = null;

    public ArrayList<DeviceEntity> allDeviceData = SmartCCConstants.devicesList;
    // private ExecutorService mExecutorWS;
    Handler handler = new Handler();
    Runnable updateRunnable;
    Spinner spMilkType;

    AutoCompleteTextView tvSelectRoute, tvSelectCenter;
    int adapterLayoutId;
    AutoTextAdapter autoTextAdapter;
    //
    SessionManager session;
    DecimalFormat decimalFormatAmout, decimalFormatRate;
    SmartCCUtil smartCCUtil;
    DatabaseHandler databaseHandler;
    ValidationHelper validationHelper;
    CollectionHelper collectionHelper;
    String lastSequenceNumber;
    boolean isWeightEnable = false;
    // Get and update the data from milk analayser
    StringBuilder messageMa1 = new StringBuilder();
    StringBuilder messageMa2 = new StringBuilder();
    ByteArrayOutputStream baosMa1 = new ByteArrayOutputStream();
    ByteArrayOutputStream baosMa2 = new ByteArrayOutputStream();
    StringBuilder messageWS = new StringBuilder();
    boolean isCavinCare, isGoldTech;
    boolean ignoreInitialWSData;
    long count = 0;
    int thresHold = 0;
    String weightRecord = "0.00";
    int lastSid = 0;
    long quantityTime;
    long tippingStartTime = 0, tippingEndTime;
    QuantityEntity quantityEntity;
    DecimalFormat decimalFormatClr;
    DecimalFormat decimalFormatFat;
    DecimalFormat decimalFormatSNF;
    MaManager ma1Manager, ma2Manager;
    WsManager wsManager;
    DeviceParameters ma1Params, ma2Params;
    private LinearLayout statusLayout;
    private Handler pingHandler;
    private Runnable pingRunnable;
    private CheckBox cbMa, cbWs, cbRdu, cbPrinter;
    private Context context = this;
    private WifiUtility wifiUtility;

    private ExecutorService mExecutor = Executors.newCachedThreadPool();

    // To read the connection
    private Activity mActivity;
    private AmcuConfig amcuConfig;
    private TextView tvMa2Header, tvEnterQuantity, tvEnterCodeHeader, tvMa1Header, tvSID1, tvSID2, tvCID1, tvCID2,
            tvFat1, tvFat2, tvSnf1, tvSnf2;
    private EditText etNumberOfCans, etQuantity, etCenterId, etSmapleNumber, etEnterTruckNumber;
    private CardView EnterCenterCardView;
    private CardView EnterQuantityCardView, cardViewMa1, cardViewMA2;
    private android.support.design.widget.TextInputLayout tvTruckNumber;
    private RelativeLayout rlEnterQuantity, rlHeadingMA2, rlEnterCode, rlHeadingMA1, rlMA1, rlMA2;
    private LinearLayout linearMilkAnalyser, linearLayout2, lnFatSnf2, lnCenterAndSample1, lnFatSnf1, linearLayout, lnCenterAndSample2;
    private Button btnRetry2, btnSubmitQuantity, btnSubmit, btnRetry,
            btnQuantityCancel, btnSubmitCenter, btnCancel, btnSubmit2;
    private String lastRouteId = SmartCCConstants.SELECT_ALL, lastCenterId;
    private double minimumDippingWeight = 0;
    private boolean isWsAdded, isMA1Added, isMA2Added;
    private boolean isCenterEnable = true;

    private boolean enableMininumFlag = true;
    private boolean maPing, wsPing, rduPing, printerPing;

    private ReportEntity weightReportEntity;
    private CollectionCenterDao collectionCenterDao;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case SmartCCConstants.MA_CONNECTED:
                    setMilkAnalyserConnection();
                    break;
                case SmartCCConstants.WS_CONNECTED:
                    openWeightConnection();
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = ParallelActivity.this;
        initializeUtility();
        setContentView(R.layout.activity_parallel);
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        adapterLayoutId = getResources().getIdentifier("auto_text_item", "res", getPackageName());
        initializeView();
        onClickEvents();
        onSelectSpinner();
        autoTextAdapter();
        setStatusLayout();
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
    protected void onStart() {
        super.onStart();


        //This line for the enable or disable the Double milk Analyser functionality
        isMultipleMA = amcuConfig.getCheckboxMultipleMA() && amcuConfig.getMultipleMA();

        try {
            collectionHelper.getAllreportEntity(null, lastSequenceNumber, nextSequenceNumber,
                    tvCID1, tvCID2,
                    tvSID1, tvSID2, tvFat1, tvFat2, tvSnf1, tvSnf2);
        } catch (Exception e) {
            e.printStackTrace();
            onFinish();
        }


        if (amcuConfig.getWeighingScale().equalsIgnoreCase("EVEREST")) {
            isCavinCare = true;
        } else if (amcuConfig.getWeighingScale().equalsIgnoreCase("GOLDTECH")) {
            isGoldTech = true;
        } else {
            isCavinCare = false;
        }

        if (isMultipleMA) {
            MA1 = DeviceName.MILK_ANALYSER;
            tvMa1Header.setText("MA1 Sample");
            tvMa2Header.setText("MA2 Sample");
        } else {
            MA1 = SINGLE_MA;
            tvMa1Header.setText("MA Sample");
            cardViewMA2.setVisibility(View.GONE);
            amcuConfig.saveMA1Details(amcuConfig.getMA(), amcuConfig.getMABaudRate());
        }

        registerDevice();
        setMilkAnalyserConnection();

    }


    private void initializeUtility() {
        mActivity = ParallelActivity.this;
        amcuConfig = AmcuConfig.getInstance();

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();

        decimalFormatAmout = chooseDecimalFormat.getAmountDecimalFormat();
        decimalFormatRate = chooseDecimalFormat.getRateDecimalFormat();
        smartCCUtil = new SmartCCUtil(ParallelActivity.this);

        session = new SessionManager(ParallelActivity.this);
        validationHelper = new ValidationHelper();
        collectionHelper = new CollectionHelper(mActivity);
        collectionCenterDao = (CollectionCenterDao) DaoFactory.getDao(CollectionConstants.COLLECTION_CENTER);

        ma1Manager = MAFactory.getMA(DeviceName.MILK_ANALYSER, ParallelActivity.this);
        ma2Manager = MAFactory.getMA(DeviceName.MA2, ParallelActivity.this);
        wsManager = WsFactory.getWs(DeviceName.WS, ParallelActivity.this);
    }


    private void registerDevice() {
        if (ma1Manager != null) {
            ma1Manager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!tvFat1.getText().toString().contains("-") && tvFat1.getText().toString().length() > 1) {
                                ma1Manager.resetConnection(200);
                                return;
                            }
                            if (tvSID1.getText().toString().contains("-") ||
                                    tvSID1.getText().toString().trim().length() == 0) {

                                return;
                            }
                            if (maEntity != null) {
                                reportEntityMA1.setQualityParameters(maEntity);
                                if (!smartCCUtil.validateFatAndSnfForZeroValues(maEntity.fat
                                        , maEntity.snf)) {
                                    onFinish();
                                } else {
                                    setMAData(reportEntityMA1, tvFat1, tvSnf1);
                                    collectionHelper.setVisibilityOfMa(cardViewMa1);
//                                mSerialIoManagerMAFirst.resetSIOManager();
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
        if (ma2Manager != null) {
            ma2Manager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!tvFat2.getText().toString().contains("-") && tvFat2.getText().toString().length() > 1) {
                                ma2Manager.resetConnection(200);
                                return;
                            }
                            if (tvSID2.getText().toString().contains("-") ||
                                    tvSID2.getText().toString().trim().length() == 0) {

                                return;
                            }
                            if (maEntity != null) {
                                reportEntityMA2.setQualityParameters(maEntity);
                                messageMa2 = new StringBuilder();
                                if (!smartCCUtil.validateFatAndSnfForZeroValues(maEntity.fat, maEntity.snf)) {
                                    onFinish();
                                } else {
                                    setMAData(reportEntityMA2, tvFat2, tvSnf2);
                                    collectionHelper.setVisibilityOfMa(cardViewMA2);
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
        if (wsManager != null) {
            wsManager.setOnNewDataListener(new WsManager.OnNewDataListener() {
                @Override
                public void onNewData(final double data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            count = count + 1;
                            if (data != CollectionHelper.DEFAULT_DIPPING_VALUE) {
                                setMinimumDippingWeight(String.valueOf(data));
                            }
                            if (isCenterEnable) {
                                return;
                            }
                            isWsAdded = true;
                            enableMininumFlag = true;
                            setTippingStartTime();
                            setWeight(String.valueOf(data));
                            setTippingEndTime();
                        }
                    });

                }
            });
        }
    }


    private void openWeightConnection() {

        if (!isWsAdded) {

            if (wsManager != null) {
                wsManager.openConnection();
            }


        } else {
            // Util.displayErrorToast("WS already read!",ParallelActivity.this);
        }

    }

    /**
     * Check for the milk analyzer and read the corresponding connection
     */
    private synchronized void setMilkAnalyserConnection() {

        if (ma1Manager != null) {
            ma1Manager.startReading();
        }

        if (ma2Manager != null) {
            ma2Manager.startReading();
        }
    }


    public void updateUIWithWSReceivedData(byte[] data) {

        String s = new String(data);
        messageWS.append(s);
        String str1 = messageWS.toString();

        if (str1.length() > 10) {
            count = count + 1;
            String record = collectionHelper.parseWSForDippingWeight(str1);

            if (count % 5 == 0) {
                // Util.displayErrorToast("Record: "+record,ParallelActivity.this);
            }
            if (record != null) {
                setMinimumDippingWeight(record);
            }
        }

        if (isCenterEnable) {
            return;
        }

        isWsAdded = true;
        enableMininumFlag = true;
        setTippingStartTime();

        //This is for GoldTech weighing scale
        if (isGoldTech && str1.length() > 9) {
            if (!ignoreInitialWSData) {
                if (collectionHelper.parseWeighingScaleData(str1, true) != null)
                    resetWSMessage(str1);
            } else if (str1.length() > 10) { // Ignore first 10 messages
                ignoreInitialWSData = false;
                messageWS = new StringBuilder();
            }
        } else if (!isCavinCare
                && str1.length() > 12) {
            if (!ignoreInitialWSData) {
                if (collectionHelper.parseWeighingScaleData(str1, true) != null)
                    resetWSMessage(str1);
            } else if (str1.length() > 300) { // Ignore first 20 messages
                ignoreInitialWSData = false;
                messageWS = new StringBuilder();
            }
        }
        //This is for cavin care wiehging scale
        else if (isCavinCare && str1.length() > 9) {
            if (!ignoreInitialWSData) {
                if (collectionHelper.parseWeighingScaleData(str1, true) != null)
                    resetWSMessage(str1);
            } else if (str1.length() > 10) { // Ignore first 10 messages
                ignoreInitialWSData = false;
                messageWS = new StringBuilder();
            }
        }

    }

    public void resetWSMessage(String str1) {
        setWeight(collectionHelper.parseWeighingScaleData(str1, true));
        messageWS = new StringBuilder();
        setTippingEndTime();
    }

    public void showToastMessage(String message) {
        Util.displayErrorToast(message, ParallelActivity.this);
    }

    public void initializeView() {

        //CardView
        EnterQuantityCardView = (android.support.v7.widget.CardView) findViewById(R.id.EnterQuantityCardView);
        EnterCenterCardView = (android.support.v7.widget.CardView) findViewById(R.id.EnterCenterCardView);
        cardViewMa1 = (android.support.v7.widget.CardView) findViewById(R.id.cardViewMA1);
        cardViewMA2 = (android.support.v7.widget.CardView) findViewById(R.id.cardViewMA2);

        //Text view initialization
        tvMa2Header = (TextView) findViewById(R.id.tvMa2Header);
        tvEnterQuantity = (TextView) findViewById(R.id.tvEnterQuantity);
        tvEnterCodeHeader = (TextView) findViewById(R.id.tvEnterCodeHeader);
        tvMa1Header = (TextView) findViewById(R.id.tvMa1Header);
        tvSID1 = (TextView) findViewById(R.id.tvSID1);
        tvSID2 = (TextView) findViewById(R.id.tvSID2);
        tvCID1 = (TextView) findViewById(R.id.tvCID1);
        tvCID2 = (TextView) findViewById(R.id.tvCID2);
        tvFat1 = (TextView) findViewById(R.id.tvFat1);
        tvFat2 = (TextView) findViewById(R.id.tvFat2);
        tvSnf1 = (TextView) findViewById(R.id.tvSnf1);
        tvSnf2 = (TextView) findViewById(R.id.tvSNF2);

        //Edit text initialization
        etNumberOfCans = (EditText) findViewById(R.id.etNumberOfCans);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etCenterId = (EditText) findViewById(R.id.etCenterId);
        etSmapleNumber = (EditText) findViewById(R.id.etSmapleNumber);
        etEnterTruckNumber = (EditText) findViewById(R.id.etEnterTruckNumber);
        tvTruckNumber = (android.support.design.widget.TextInputLayout) findViewById(R.id.tvTruckNumber);

        //Relative layout initialization
        rlEnterQuantity = (RelativeLayout) findViewById(R.id.rlEnterQuantity);
        rlHeadingMA2 = (RelativeLayout) findViewById(R.id.rlHeadingMA2);
        rlEnterCode = (RelativeLayout) findViewById(R.id.rlEnterCode);
        rlHeadingMA1 = (RelativeLayout) findViewById(R.id.rlHeadingMA1);
        rlMA1 = (RelativeLayout) findViewById(R.id.rlMA1);
        rlMA2 = (RelativeLayout) findViewById(R.id.rlMA2);

        //Linear layout initialization
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        lnFatSnf2 = (LinearLayout) findViewById(R.id.lnFatSnf2);
        lnCenterAndSample1 = (LinearLayout) findViewById(R.id.lnCenterAndSample1);
        lnFatSnf1 = (LinearLayout) findViewById(R.id.lnFatSnf1);
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        lnCenterAndSample2 = (LinearLayout) findViewById(R.id.lnCenterAndSample2);
        linearMilkAnalyser = (LinearLayout) findViewById(R.id.linearMilkAnalyser);


        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();

        decimalFormatFat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        decimalFormatSNF = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        decimalFormatClr = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);

        //Button initialization
        btnSubmitQuantity = (Button) findViewById(R.id.btnSubmitQuantity);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        btnRetry = (Button) findViewById(R.id.btnRetry);
        btnQuantityCancel = (Button) findViewById(R.id.btnQuantityCancel);
        btnSubmitCenter = (Button) findViewById(R.id.btnSubmitCenter);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit2 = (Button) findViewById(R.id.btnSubmit2);
        btnRetry2 = (Button) findViewById(R.id.btnRetry2);

        cbMa = (CheckBox) findViewById(R.id.cb_ma);
        cbWs = (CheckBox) findViewById(R.id.cb_ws);
        cbRdu = (CheckBox) findViewById(R.id.cb_rdu);
        cbPrinter = (CheckBox) findViewById(R.id.cb_printer);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        pingHandler = new Handler();
        wifiUtility = new WifiUtility();

        //Spinner initialization
        spMilkType = (Spinner) findViewById(R.id.spMilkType);

        tvSelectRoute = (AutoCompleteTextView) findViewById(R.id.tvSelectRoute);
        tvSelectCenter = (AutoCompleteTextView) findViewById(R.id.tvSelectCenter);

        //disable visibility of retry
        btnRetry.setVisibility(View.GONE);
        btnRetry2.setVisibility(View.GONE);

        tvTruckNumber.setVisibility(View.GONE);
        EnterQuantityCardView.setVisibility(View.GONE);
        EnterCenterCardView.setVisibility(View.VISIBLE);
        setSpinnerFirstTime();
        Util.alphabetValidation(etNumberOfCans, Util.ONLY_NUMERIC, ParallelActivity.this, 4);
    }

    public void onClickEvents() {
        btnRetry.setOnClickListener(this);
        btnRetry2.setOnClickListener(this);

        btnSubmitQuantity.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        btnQuantityCancel.setOnClickListener(this);
        btnSubmitCenter.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnSubmit2.setOnClickListener(this);

        tvSelectCenter.setOnClickListener(this);
        tvSelectRoute.setOnClickListener(this);
    }

    public void setCenterList() {
        ArrayList<String> allCenter = collectionCenterDao.findActiveCenterByRoute(tvSelectRoute.getText().toString());

        autoTextAdapter = new AutoTextAdapter(ParallelActivity.this, adapterLayoutId, allCenter);

        tvSelectCenter.setThreshold(thresHold);
        tvSelectCenter.setAdapter(autoTextAdapter);

        if (!tvSelectCenter.getText().toString().equalsIgnoreCase("Select MCC")
                && allCenter != null && allCenter.size() > 0) {
            // tvSelectCenter.setText(allCenter.get(0));
            tvSelectCenter.setHint(SELECT_CENTER);
        }


    }

    /**
     * Set the milk type is spinner
     *
     * @param milkType
     */
    public void setMilkTypeInSpinner(String milkType) {
        if (milkType.equalsIgnoreCase(CattleType.BOTH)) {
            spMilkType.setEnabled(true);
            if (amcuConfig.getDefaultMilkTypeForBoth().equalsIgnoreCase(CattleType.BUFFALO)) {
                spMilkType.setSelection(1);
            } else {
                spMilkType.setSelection(0);
            }
        } else if (milkType.equalsIgnoreCase(CattleType.COW)) {
            spMilkType.setSelection(0);
            spMilkType.setEnabled(false);
        } else if (milkType.equalsIgnoreCase(CattleType.BUFFALO)) {
            spMilkType.setSelection(1);
            spMilkType.setEnabled(false);
        }
    }

    // Quantity related things

    public void setSpinnerFirstTime() {

        ArrayList<String> allRoute = collectionCenterDao.getAllActiveRoutes();
        autoTextAdapter = new AutoTextAdapter(ParallelActivity.this, adapterLayoutId, allRoute);

        tvSelectRoute.setThreshold(thresHold);
        tvSelectRoute.setAdapter(autoTextAdapter);
        if (SELECT_ROUTE.equalsIgnoreCase("Select Route") && allRoute != null && allRoute.size() > 0) {
            tvSelectRoute.setText(allRoute.get(0));
        } else {
            tvSelectRoute.setText(SELECT_ROUTE);
        }

        tvSelectRoute.setSelection(tvSelectRoute.getText().toString().length());
        initCenter();
        String[] allCattleType = getResources().getStringArray(R.array.Milk_type_both);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, allCattleType);
        spMilkType.setAdapter(arrayAdapter);

    }

    public void initCenter() {
        ArrayList<String> allCenter =
                collectionCenterDao.findActiveCenterByRoute(tvSelectRoute.getText().toString());
        autoTextAdapter = new AutoTextAdapter(ParallelActivity.this, adapterLayoutId, allCenter);
        if (allCenter != null && allCenter.size() > 0)
            //  tvSelectCenter.setText(allCenter.get(0));

            tvSelectCenter.setHint(SELECT_CENTER);
        tvSelectCenter.setThreshold(thresHold);
        tvSelectCenter.setAdapter(autoTextAdapter);

    }

    public void setEnterCenterIdView() {

        ArrayList<String> allRoute = collectionCenterDao.getAllRoutes();
        autoTextAdapter = new AutoTextAdapter(ParallelActivity.this, adapterLayoutId, allRoute);

        tvSelectRoute.setThreshold(thresHold);
        tvSelectRoute.setAdapter(autoTextAdapter);
        tvSelectRoute.setText(lastRouteId);

        ArrayList<String> allCenter =
                collectionCenterDao.findActiveCenterByRoute(tvSelectRoute.getText().toString());
        autoTextAdapter = new AutoTextAdapter(ParallelActivity.this, adapterLayoutId, allCenter);

        tvSelectCenter.setThreshold(thresHold);
        tvSelectCenter.setAdapter(autoTextAdapter);
        tvSelectCenter.requestFocus();
        //   tvSelectCenter.setSelection(tvSelectCenter.getText().toString().length());
        tvSelectCenter.setHint(SELECT_CENTER);
        tvSelectCenter.showDropDown();

        String[] allCattleType = getResources().getStringArray(R.array.Milk_type_both);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.custom_spinner_item, allCattleType);
        spMilkType.setAdapter(arrayAdapter);

    }

    void onSelectSpinner() {

        tvSelectRoute.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (tvSelectRoute.getText().toString().trim().equalsIgnoreCase("")) {
                    tvSelectRoute.getAdapter();
                    // setSpinnerFirstTime();

                } else if (!tvSelectRoute.getText().toString().equalsIgnoreCase("Select Route")) {
                    setCenterList();
                } else {
                    initCenter();
                }

            }
        });

        tvSelectCenter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (tvSelectCenter.getText().toString().trim().equalsIgnoreCase("")) {
                    //   tvSelectCenter.showDropDown();
                } else if (!tvSelectCenter.getText().toString().equalsIgnoreCase(SELECT_CENTER)) {
                    String milkType = collectionHelper.getMilkTypeFromCenter(
                            tvSelectCenter.getText().toString().trim().toUpperCase());
                    setMilkTypeInSpinner(milkType);
                }

            }
        });

        spMilkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //   session.setMilkType(spMilkType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvSelectRoute.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                tvSelectRoute.setText(adapterView.getItemAtPosition(pos).toString());
                setCenterList();
                tvSelectCenter.setText("");
                tvSelectCenter.setHint(SELECT_CENTER);
                tvSelectCenter.requestFocus();
                tvSelectCenter.setSelection(tvSelectCenter.getText().toString().length());

            }
        });

        tvSelectCenter.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                tvSelectCenter.setText(adapterView.getItemAtPosition(pos).toString());
                // setMilkTypeInSpinner(tvSelectCenter.getText().toString());

                onEnterOnCenter();

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnRetry2) {
        } else if (v == btnSubmitQuantity) {

            onSubmitQuantity();
        } else if (v == btnSubmit) {
            setMADataToDatabase(reportEntityMA1, MA1);
        } else if (v == btnRetry) {
        } else if (v == btnQuantityCancel) {
            resetDippingWeight();
            gotoSelectCenterScreen();
            tvSelectCenter.setText("");
            tvSelectCenter.setHint(SELECT_CENTER);
        } else if (v == btnSubmitCenter) {
            onSubmitCenter();
        } else if (v == btnCancel) {

            startActivity(new Intent(ParallelActivity.this, UserSelectionActivity.class));
            finish();

        } else if (v == btnSubmit2) {
            setMADataToDatabase(reportEntityMA2, MA2);
        } else if (v == tvSelectCenter) {
            tvSelectCenter.showDropDown();
        } else if (v == tvSelectRoute) {
            tvSelectRoute.showDropDown();
        }
    }

    public void setQuantityDetails() {

        ignoreInitialWSData = false;
        weightRecord = "0.00";
        lastSid = collectionHelper.getCurrentSID();

        weightReportEntity = collectionHelper.getDefaultReportEntityForQuantity();
        etCenterId.setText(tvSelectCenter.getText().toString());
        etCenterId.setFocusable(false);
        //  session.setMilkType(spMilkType.getSelectedItem().toString());
        etQuantity.setText(weightRecord);
        etQuantity.setFocusable(false);
        etSmapleNumber.setText(String.valueOf(lastSid));
        etNumberOfCans.setText("1");
        etNumberOfCans.setSelection(etNumberOfCans.getText().toString().length());
        etNumberOfCans.requestFocus();

    }

    public void setTippingStartTime() {
        if (weightReportEntity.tippingStartTime == 0) {
            weightReportEntity.tippingStartTime = System.currentTimeMillis();
        }
    }

    public void setTippingEndTime() {

        weightReportEntity.tippingEndTime = System.currentTimeMillis();
    }

    public void resetSID() {
        etSmapleNumber.requestFocus();
        etSmapleNumber.setSelection(etSmapleNumber.getText().toString().length());
    }

    public void setWeight(String record) {
//
        try {
            double dQty = validationHelper.getDoubleFromString(
                    record, 0);
            quantityEntity = collectionHelper.getQuantityItems(dQty);
            weightReportEntity.quantity = quantityEntity.displayQuantity;
            weightReportEntity.setLtrsWeight(quantityEntity.ltrQuanity);
            weightReportEntity.setKgWeight(quantityEntity.kgQuantity);
            etQuantity.setText(String.valueOf(weightReportEntity.getDisplayQuantity()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void onSubmitQuantity() {

        if (!collectionHelper.validateSID(etSmapleNumber, lastSid)) {
            resetSID();
            return;
        }

        //  quantityEntity = parallelFunction.getQuantityItems(Double.parseDouble(weightRecord));
        if (quantityEntity == null ||
                !collectionHelper.isValidWeight(etQuantity)) {
            Util.displayErrorToast("Enter valid weight ", mActivity);
            return;
        }

//         isWsAdded = false;
//        stopWS();

        if (!collectionHelper.validateNumberOfCans(etNumberOfCans)) {
            return;
        }
       /* ReportEntity reportEntity = collectionHelper.getQuantityReportEntity(Util.REPORT_NOT_COMMITED,
                etNumberOfCans.getText().toString(),
                quantityEntity, quantityTime,
                tippingStartTime, tippingEndTime, etSmapleNumber.getText().toString().trim());*/

        weightReportEntity.sampleNumber = Integer.parseInt(etSmapleNumber.getText().toString().trim());
        weightReportEntity.numberOfCans = Integer.parseInt(etNumberOfCans.getText().toString().trim());

        lastCenter = weightReportEntity.farmerId;

        try {
            smartCCUtil.saveReportsOnSdCard(weightReportEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        collectionHelper.addQuantityToDatabase(weightReportEntity);

        //    Util.displayErrorToast(" Milk type "+reportEntity.milkType,ParallelActivity.this);
        //Setting tipping start time for next collection
        /*  tippingStartTime = 0;*/
        isCenterEnable = true;
        gotoSelectCenterScreen();
        int sid = -1;
        try {
            sid = Integer.parseInt(tvSID1.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (sid < 1) {
            collectionHelper.getAllreportEntity(MA1, lastSequenceNumber, nextSequenceNumber,
                    tvCID1, tvCID2,
                    tvSID1, tvSID2, tvFat1, tvFat2, tvSnf1, tvSnf2);
        }
        sid = -1;
        try {
            sid = Integer.parseInt(tvSID2.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        if (sid < 1 && isMultipleMA) {
            collectionHelper.getAllreportEntity(MA2, lastSequenceNumber, nextSequenceNumber,
                    tvCID1, tvCID2,
                    tvSID1, tvSID2, tvFat1, tvFat2, tvSnf1, tvSnf2);
        }

        //this is for set empty center Id
        tvSelectCenter.setText("");
        tvSelectCenter.setHint(SELECT_CENTER);

    }

    public void gotoSelectCenterScreen() {
        EnterCenterCardView.setVisibility(View.VISIBLE);
        EnterQuantityCardView.setVisibility(View.GONE);
        setEnterCenterIdView();
        //  parallelFunction.tareWSOverSerialManager(mSerialIoManagerWS,usbSerialPortWeighingScale);
        setHeader();

    }

    public void onSubmitCenter() {
        boolean isValidRoute = collectionHelper.isValidRoute(
                tvSelectRoute.getText().toString().trim().toUpperCase());
        if (!isValidRoute) {
            Util.displayErrorToast(" Please enter the valid route Id", ParallelActivity.this);
            return;
        }

        CenterEntity centerEntity = collectionHelper.isValidCenter(
                tvSelectCenter.getText().toString().toUpperCase(), true);
        if (centerEntity == null) {
            Util.displayErrorToast(" Invalid center id", ParallelActivity.this);
            return;
        }

        CollectionCenterDao collectionCenterDao = (CollectionCenterDao) DaoFactory.getDao(CollectionConstants.COLLECTION_CENTER);
        CenterEntity entity = collectionCenterDao.findByCenterId(tvSelectCenter.getText().toString().toUpperCase());
        if (entity != null && !entity.activeStatus.equalsIgnoreCase("1")) {
            Util.displayErrorToast("Inactive center id", ParallelActivity.this);
            return;
        }


        boolean isValidCenter = collectionHelper.isValidCenterForRoute(
                tvSelectCenter.getText().toString().trim().toUpperCase(),
                tvSelectRoute.getText().toString().trim().toUpperCase());
        if (!isValidCenter) {
            Util.displayErrorToast(" Please enter the valid route Id or center Id", ParallelActivity.this);
            return;
        }

        if (minimumDippingWeight > amcuConfig.getKeyMinValidWeight() && enableMininumFlag) {
            Util.displayErrorToast("Remove the can from Load cell!" +
                            " or press F7 for refresh."
                    , ParallelActivity.this);
            return;
        }

        session.setMilkType(spMilkType.getSelectedItem().toString());
        session.setFarmerID(centerEntity.centerId);
        session.setFarmerName(centerEntity.centerName);

        //Util.displayErrorToast("Selected milk type : "+session.getMilkType(),ParallelActivity.this);

        tvSelectCenter.setText(tvSelectCenter.getText().toString().trim().toUpperCase());

        if (!centerEntity.singleOrMultiple.equalsIgnoreCase(Util.MULTIPLE) &&
                collectionHelper.checkForCenterDuplicate(centerEntity.centerId)) {
            Util.displayErrorToast(" Collection is already done for center "
                    + centerEntity.centerId, mActivity);
            return;
        }
        isCenterEnable = false;
        EnterCenterCardView.setVisibility(View.GONE);
        EnterQuantityCardView.setVisibility(View.VISIBLE);
        setQuantityDetails();
        openWeightConnection();
        lastRouteId = tvSelectRoute.getText().toString();
        lastCenterId = tvSelectCenter.getText().toString();

    }

    public void setMAData(ReportEntity reportEntity, TextView tvFat, TextView tvSnf) {
        tvFat.setText(decimalFormatFat.format(reportEntity.getDisplayFat()));
        tvSnf.setText(decimalFormatSNF.format(reportEntity.getDisplaySnf()));
    }

    /**
     * Setting un-committed records to commited record
     *
     * @param reportEntity existing un-committed record
     * @param ma1OrMa2     Output data from MA1 or MA2
     */
    public synchronized void setMADataToDatabase(
            final ReportEntity reportEntity, final String ma1OrMa2) {

        if (reportEntity == null) {
            Util.displayErrorToast("Invalid fat or SNF", mActivity);
            return;
        }

        if (ma1OrMa2.equalsIgnoreCase(MA1)) {
            if (tvFat1.getText().toString().trim().isEmpty() ||
                    tvSnf1.getText().toString().trim().isEmpty()) {
                Util.displayErrorToast("Invalid Fat or SNF values", mActivity);
                return;
            }

            rlMA1.setBackgroundColor(getResources().getColor(R.color.milkAnalyserOne));

        } else {
            if (tvFat2.getText().toString().trim().isEmpty() ||
                    tvSnf2.getText().toString().trim().isEmpty()) {
                Util.displayErrorToast("Invalid Fat or SNF values", mActivity);
                return;
            }
            rlMA2.setBackgroundColor(getResources().getColor(R.color.milkAnalyserOne));
        }


        if (!collectionHelper.fatAndSnfValidation(Double.valueOf(reportEntity.fat)
                , Double.valueOf(reportEntity.snf))) {
            Util.displayErrorToast("Invalid Fat or SNF values", mActivity);
            return;
        }

        if (reportEntity != null && ma1OrMa2.equalsIgnoreCase(MA1)) {
            //     parallelFunction.deleteExisting(reportEntity.columnId);
            ReportEntity repComEnt = collectionHelper.getCommittedReportEntity(
                    reportEntity, MA1);
            try {
                smartCCUtil.saveReportsOnSdCard(repComEnt);
            } catch (IOException e) {
                e.printStackTrace();
            }
            collectionHelper.addQualityToDatabase(repComEnt);
            nextSequenceNumber = tvSID1.getText().toString().trim();
            collectionHelper.getAllreportEntity(MA1, lastSequenceNumber, nextSequenceNumber,
                    tvCID1, tvCID2,
                    tvSID1, tvSID2, tvFat1, tvFat2, tvSnf1, tvSnf2);
            lastSequenceNumber = null;

            baosMa1.reset();
            messageMa1 = new StringBuilder();
        } else if (ma1OrMa2.equalsIgnoreCase(MA2)) {

            // parallelFunction.deleteExisting(reportEntity.columnId);

            ReportEntity repComEnt = collectionHelper.getCommittedReportEntity(
                    reportEntity, MA2);
            try {
                smartCCUtil.saveReportsOnSdCard(repComEnt);
            } catch (IOException e) {
                e.printStackTrace();
            }
            collectionHelper.addQualityToDatabase(repComEnt);
//            parallelFunction.getCommittedReportEntity(
//                    reportEntity,maEntity,MA2,
//                    smartCCUtil.getCurrentRateChartForCattle(reportEntity.milkType));
//            parallelFunction.addQualityToDatabase(reportEntity);
            nextSequenceNumber = tvSID2.getText().toString().trim();
            collectionHelper.getAllreportEntity(MA2, lastSequenceNumber, nextSequenceNumber,
                    tvCID1, tvCID2,
                    tvSID1, tvSID2, tvFat1, tvFat2, tvSnf1, tvSnf2);
            lastSequenceNumber = null;
            baosMa2.reset();
            messageMa2 = new StringBuilder();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_F1) {
            if (linearMilkAnalyser.getVisibility() == View.VISIBLE) {
                linearMilkAnalyser.setVisibility(View.GONE);
            } else {
                linearMilkAnalyser.setVisibility(View.VISIBLE);
            }
        } else if (keyCode == KeyEvent.KEYCODE_F7 && isCenterEnable) {
            resetDippingWeight();
        }
        return super.onKeyUp(keyCode, event);
    }

    public void stopWS() {

        if (wsManager != null) {
            wsManager.closeConnection();
        }

    }

    public void onStopMa1() {
        if (ma1Manager != null) {
            ma1Manager.stopReading();
        }

    }

    public void onStopMa2() {
        if (ma2Manager != null) {
            ma2Manager.stopReading();
        }


    }

    @Override
    protected void onPause() {
        Log.d("Parallel Activity: ", "On pause called");
        onStopExecutor();
        super.onPause();
    }

    @Override
    protected void onStop() {
        stopPingService();
        super.onStop();
    }

    private void stopPingService() {
        pingHandler.removeCallbacks(pingRunnable);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onFinish();

    }

//    public void displayToast(String msg)
//    {
//        Util.displayErrorToast(msg,mActivity);
//    }

    public void onFinish() {
        startActivity(new Intent(ParallelActivity.this, UserSelectionActivity.class));
        finish();
    }

    public void setHeader() {
        if (lastCenter != null) {
            tvEnterCodeHeader.setText("Select MCC " + "( Last MCC " + lastCenter + " )");
        }
    }

    public void autoTextAdapter() {

        spMilkType.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

//                if (((keyEvent.getAction() == KeyEvent.ACTION_UP)
//                        && (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)))
//                {
//
//                        tvSelectCenter.requestFocus();
//                        tvSelectCenter.setText(tvSelectCenter.getText().toString().trim());
//                        tvSelectCenter.setSelection(tvSelectCenter.getText().toString().trim().length());
//
//                }

                return false;
            }
        });

        tvSelectCenter.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (((keyEvent.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))) {
                    if (!tvSelectCenter.isPopupShowing()) {
                        onEnterOnCenter();
                        setRouteOrCenter(false);
                    }


                } else if ((keyEvent.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    if (!tvSelectCenter.isPopupShowing()) {
                        onEnterOnCenter();
                        setRouteOrCenter(false);
                    }
                } else if (((keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_DPAD_UP))) {
                    if (!tvSelectCenter.isPopupShowing()) {
                        tvSelectRoute.requestFocus();
                        tvSelectRoute.setText(tvSelectRoute.getText().toString().trim());
                        tvSelectRoute.setSelection(tvSelectRoute.getText().toString().trim().length());

                    }
                }
                return false;
            }
        });

        tvSelectRoute.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (((keyEvent.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))) {
                    setRouteOrCenter(true);
                    tvSelectRoute.setText(tvSelectRoute.getText().toString().trim());
                    tvSelectRoute.setSelection(tvSelectRoute.getText().toString().trim().length());
                } else if ((keyEvent.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
                    setRouteOrCenter(true);
                    tvSelectRoute.setText(tvSelectRoute.getText().toString().trim());
                    tvSelectRoute.setSelection(tvSelectRoute.getText().toString().trim().length());
                }

                return false;
            }
        });


    }

    public void setRouteOrCenter(boolean isRoute) {
        if (isRoute) {
            if (tvSelectRoute.getText().toString().trim().equalsIgnoreCase("")) {
                tvSelectRoute.showDropDown();
            }
        } else {
            if (tvSelectCenter.getText().toString().trim().equalsIgnoreCase("")) {
                if (tvSelectRoute.getText().toString().trim().length() < 1) {
                    setSpinnerFirstTime();
                } else {
                    tvSelectCenter.clearComposingText();
                    tvSelectCenter.setText(tvSelectCenter.getText().toString().trim());
                    tvSelectCenter.setSelection(tvSelectCenter.getText().toString().trim().length());
                    tvSelectCenter.showDropDown();
                }
            }
        }

    }

    public void onStopExecutor() {
        //  Util.displayErrorToast("Calling shutDown!",ParallelActivity.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    stopWS();
                    onStopMa1();
                    onStopMa2();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (mExecutor != null && !mExecutor.isShutdown()) {
                        mExecutor.awaitTermination(3000, TimeUnit.MILLISECONDS);

                    }
//                    if(mExecutorWS != null && !mExecutorWS.isShutdown())
//                    {
//                        mExecutorWS.shutdownNow();
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void setMinimumDippingWeight(String records) {
        //Once it come to the Quantity screen it should set the minimum dipping weight
        if (!enableMininumFlag) {
            return;
        }

        String weight = validationHelper.getDoubleFromString(
                decimalFormatAmout, records, CollectionHelper.DEFAULT_DIPPING_VALUE);
        minimumDippingWeight = Double.parseDouble(weight);
        if (count > 100000) {
            count = count - 10000;
        }

        if ((minimumDippingWeight != CollectionHelper.DEFAULT_DIPPING_VALUE) && (minimumDippingWeight
                <= amcuConfig.getKeyMinValidWeight())) {
            messageWS = new StringBuilder();
            enableMininumFlag = false;
//            Util.displayErrorToast("Set dipping weight "
//                    +enableMininumFlag,ParallelActivity.this);
        }
    }

    private void onEnterOnCenter() {
        CenterEntity centerEntity = collectionHelper.isValidCenter(
                tvSelectCenter.getText().toString().toUpperCase(), true);
        if (centerEntity != null) {
            String milkType = collectionHelper.getMilkTypeFromCenter(
                    tvSelectCenter.getText().toString().trim().toUpperCase());
            if (milkType.equalsIgnoreCase(CattleType.BOTH)) {
                spMilkType.requestFocus();
            } else {
                btnSubmitCenter.requestFocus();
            }
            tvSelectCenter.setText(centerEntity.centerId);
        } else {
            tvSelectCenter.setText(tvSelectCenter.getText().toString().trim());
            tvSelectCenter.setSelection(tvSelectCenter.getText().toString().length());
        }
    }

    private void resetDippingWeight() {
        setMinimumDippingWeight("0000");
        minimumDippingWeight = 0;
        enableMininumFlag = false;
        isCenterEnable = true;
        tvSelectCenter.setText("");
        tvSelectCenter.setHint(SELECT_CENTER);
        tvSelectCenter.requestFocus();
    }


}
