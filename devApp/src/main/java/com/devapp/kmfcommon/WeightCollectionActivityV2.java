package com.devapp.kmfcommon;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.WeightLimit;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.usb.DeviceEntity;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.user.WifiUtility;
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
import java.util.TimeZone;

import static com.devapp.devmain.user.Util.ping;


public class WeightCollectionActivityV2 extends AppCompatActivity implements View.OnClickListener, WeightLimit {

    public static WeightCollectionActivityV2 mWeightCollectionActivity;
    private static WeightCollectionActivityV2 mActivityInstance = null;
    public Context context = this;
    AmcuConfig amcuConfig;
    SessionManager mSession;
    EditText etCenterName, etCenterCode, etQuantity, etNumberOfCans, etSId;
    Button btnDone, btnNextCan;
    String TAG = "WeightCollectionActivity";
    Calendar calendar;
    boolean ignoreInitialWSData;
    ValidationHelper validationHelper;
    boolean isTareDone;
    TextView tvPreviousData, tvNewData, tvTotal, textNote;
    String radioStatus;
    boolean sidChanged = false;
    CollectionHelper collectionHelper;
    TextView tvQuantity;
    boolean isFilledOrEmptyCanStarted;
    AlertDialog alertForFilledAndEmptyCan;
    AlertDialog alertForRemoveCanAndTare;
    android.support.v7.app.AlertDialog alertForZeroDialog;
    DecimalFormat decimalFormat2Digit = new DecimalFormat("#0.00");
    int lastSid;
    boolean wsPing;
    private SmartCCUtil mSmartCCUtil;
    private WifiUtility mWifiUtility;
    private LinearLayout statusLayout;
    private Handler mPingHandler;
    private Runnable pingRunnable;
    private CheckBox cbWs;
    private TextView etZeroAlertText;
    private TextView tvNoc;
    private boolean initialWeightDataReceived;
    private WsManager wsManager;
    private CollectionRecordDao collectionRecordDao;
    private ReportEntity mReportEntity;
    private double tempKgWeight, tempLiterWeight;
    private QuantityEntity quantityEntity = new QuantityEntity();
    private ChooseDecimalFormat mChooseDecimalFormat;
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {

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
                wsPing = intent.getBooleanExtra(SmartCCConstants.WS_PING, true);
                displayDeviceStatus();
            }
        }
    };
    private String beforeChage = null, afterChange = null;

    public static WeightCollectionActivityV2 getInstance() {
        return mActivityInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_collection);
        mActivityInstance = WeightCollectionActivityV2.this;
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
        cbWs.setChecked(wsPing);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initializeUtils();
        getLastSID();
        initializeReportEntity();
        setUpViews();

        setStatusLayout();
        startPingService();
        registerWsListener();
        openWsConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeWsConnection();
    }

    private void initializeReportEntity() {
        this.mReportEntity = collectionHelper.getDefaultReportEntityForQuantity();
        mReportEntity.miliTime = calendar.getTimeInMillis();
        mReportEntity.setOverallCollectionStatus(0, false);
        try {
            mReportEntity.setManual(getIntent().getStringExtra("isAutoOrManual"));
        } catch (Exception e) {
            mReportEntity.setManual(SmartCCConstants.MANUAL);
            e.printStackTrace();
        }
    }

    private void initializeViews() {
        etCenterName = (EditText) findViewById(R.id.etCenterName);
        etCenterCode = (EditText) findViewById(R.id.etCenterId);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etNumberOfCans = (EditText) findViewById(R.id.etNOC);


        tvNoc = (TextView) findViewById(R.id.tvNOC);
        tvQuantity = (TextView) findViewById(R.id.tvQuantity);

        // SID
        etSId = (EditText) findViewById(R.id.etSId);
        etSId.setFocusable(true);

        Util.alphabetValidation(etSId, Util.ONLY_NUMERIC, WeightCollectionActivityV2.this, 0);

        //iSID = saveSession.getChillingWeightColletionSID() + 1;

        etSId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                sidChanged = true;
            }
        });
        etQuantity.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (((event.getAction() == KeyEvent.ACTION_UP) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) ||
                        (event.getAction() == KeyEvent.ACTION_DOWN &&
                                (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {

                    return false;
                } else if (Util.isDigitKey(keyCode, event)) {
                    if (amcuConfig.getWsManual()) {
                        addWatcherForQuantity();
                    }


                    return false;
                } else {
                    return false;
                }
            }
        });
        btnDone = (Button) findViewById(R.id.btnDone);
        btnNextCan = (Button) findViewById(R.id.btnNext);
        cbWs = (CheckBox) findViewById(R.id.cb_ws);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);
        mPingHandler = new Handler();


        btnDone.setOnClickListener(this);
        btnNextCan.setOnClickListener(this);
        // Util.alphabetValidation(etQuantity, Util.ONLY_DECIMAL, WeightCollectionActivity.this, 8);
        Util.alphabetValidation(etNumberOfCans, Util.ONLY_NUMERIC, WeightCollectionActivityV2.this, 3);


        etNumberOfCans.setEnabled(false);

    }

    private void getLastSID() {
        lastSid = collectionHelper.getCurrentSID();
        etSId.setText(String.valueOf(lastSid));
        //  etSId.setSelection(etSId.length());
    }

    private void initializeUtils() {
        mWeightCollectionActivity = WeightCollectionActivityV2.this;
        mSession = new SessionManager(WeightCollectionActivityV2.this);
        amcuConfig = AmcuConfig.getInstance();
        collectionHelper = new CollectionHelper(WeightCollectionActivityV2.this);
        mChooseDecimalFormat = new ChooseDecimalFormat();

        if (amcuConfig.getHotspotValue()) {
            mWifiUtility = new WifiUtility();
            mPingHandler = new Handler();
        }

        mSmartCCUtil = new SmartCCUtil(WeightCollectionActivityV2.this);
        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);

        validationHelper = new ValidationHelper();
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();

        decimalFormat2Digit = chooseDecimalFormat.get2DigitFormatWeight();

        wsManager = WsFactory.getWs(DeviceName.WS, WeightCollectionActivityV2.this);
    }

    private void setUpViews() {
        if (amcuConfig.getKeyRateChartInKg() || amcuConfig.getMyRateChartEnable()) {
            tvQuantity.setText("Qty(kgs)");
        } else {
            tvQuantity.setText("Qty(ltrs)");
        }
        if (!amcuConfig.getEnableFilledOrEmptyCans()) {
            etNumberOfCans.setVisibility(View.GONE);
            btnNextCan.setVisibility(View.GONE);
            tvNoc.setVisibility(View.GONE);
            btnDone.setText("Done");
        } else {
            btnDone.setText("Quantity Done");
        }

        if (!amcuConfig.getWsManual()) {
            etQuantity.setEnabled(false);
        } /*else {
            addWatcherForQuantity();
        }
*/
        etCenterName.setText(mSession.getFarmerName());
        etCenterCode.setText(mSession.getFarmerID());
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
                    case DeviceName.WS:
                        cbWs.setVisibility(View.VISIBLE);
                        break;

                }
            }
        }
        cbWs.setChecked(amcuConfig.getWsPingValue());
    }

    private void startPingService() {
//        pingIntent = new Intent(MilkCollectionActivity.this, PingIntentService.class);
//        startService(pingIntent);
        for (DeviceEntity deviceEntity : SmartCCConstants.devicesList) {
            if (deviceEntity.deviceName == DeviceName.WS && deviceEntity.deviceType == SmartCCConstants.WIFI) {
                pingRunnable = new Runnable() {
                    @Override
                    public void run() {

                        try {
                            new WeightCollectionActivityV2.PingTask().execute();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mPingHandler.postDelayed(this, 10000);
                    }
                };
                mPingHandler.post(pingRunnable);
            }
        }
    }

    private void registerWsListener() {
        if (wsManager != null) {
            wsManager.setOnNewDataListener(new WsManager.OnNewDataListener() {
                @Override
                public void onNewData(final double quantity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mReportEntity.getTippingStartTime() == 0) {
                                mReportEntity.setTippingStartTime(System.currentTimeMillis());
                            }
                            if (quantity > 0 && mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.AUTO)) {
                                onWsData(quantity);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * If reading quantity is already less than
     * configured deviation quantity app with automatically finish
     * the activity
     *
     * @param weight
     */
    public void updateUnloadCanAlert(String weight) {

        if (etZeroAlertText != null) {

            etZeroAlertText.setText(weight);
            weight = validationHelper.getDoubleFromString(new DecimalFormat("##.00"),
                    etZeroAlertText.getText().toString());

            if (Double.valueOf(weight) <= amcuConfig.getDeviationWeight()) {
                onFinish();
            }

        }


    }

    /**
     * After getting WS data update the appropriate
     * Quantity field
     *
     * @param quantity
     */
    private void onWsData(double quantity) {

        quantityEntity = collectionHelper.getQuantityItems(quantity);
        if (alertForRemoveCanAndTare != null && alertForRemoveCanAndTare.isShowing()) {
            updateUnloadCanAlert(String.valueOf(quantityEntity.displayQuantity));
        } else if (alertForFilledAndEmptyCan != null && alertForFilledAndEmptyCan.isShowing()) {
            //In this case read the weight , and display only on filled and empty can alert screen
            updateCurrentCanAlert(quantityEntity.displayQuantity);
        } else if (isFilledOrEmptyCanStarted) {
            //In this case just read the weight but not write
//            prevRecord = weightRecord;
        } else if (!mReportEntity.isQuantityDone()) {
            //Write the weight on main screen
            mReportEntity.setQuantity(quantityEntity.displayQuantity);
            mReportEntity.setKgWeight(quantityEntity.kgQuantity);
            mReportEntity.setLtrsWeight(quantityEntity.ltrQuanity);
            displayQuantity();
        }

        if (!btnDone.isEnabled()) {
            btnDone.setEnabled(true);
        }

//        if (!amcuConfig.getEnableFilledOrEmptyCans()) {
//            mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
//        }

        mReportEntity.setTippingEndTime(System.currentTimeMillis());
    }

    /**
     * This is update data on Alert with filled and empty can
     * After getting WS data from
     * If user select filled can this will add current and previous
     * else in case of empty can this will display previous - current weight
     *
     * @param quantity
     */
    private void updateCurrentCanAlert(double quantity) {
        tvPreviousData.setText(String.valueOf(mReportEntity.getQuantity()));
        double minLimit, maxLimit;
        mReportEntity.setNewCanRecord(quantityEntity.displayQuantity);
        mReportEntity.setNewCanKgData(quantityEntity.kgQuantity);
        mReportEntity.setNewCanLiterData(quantityEntity.ltrQuanity);
        if (radioStatus.equalsIgnoreCase("filled")) {
            tvNewData.setText("(+) " + mReportEntity.getNewCanRecord());
            tvTotal.setText(mChooseDecimalFormat.getWeightDecimalFormat().format(
                    mReportEntity.getDisplayQuantity()
                            + mReportEntity.getNewCanRecord()));


            tempKgWeight = Double.valueOf(
                    mChooseDecimalFormat.getWeightDecimalFormat().format(
                            mReportEntity.getKgWeight()
                                    + mReportEntity.getNewCanKgData()));
            tempLiterWeight = Double.valueOf(
                    mChooseDecimalFormat.getWeightDecimalFormat().format(
                            mReportEntity.getLtrsWeight() + mReportEntity.getNewCanLiterData()));


            if (amcuConfig.getAllowVisiblityForCanToggle() && amcuConfig.getCansToggling()) {
                minLimit = Double.parseDouble(String.valueOf(amcuConfig.getMinLimitFilledCans()));
                if (quantity < minLimit)
                    textNote.setVisibility(View.VISIBLE);
                else
                    textNote.setVisibility(View.GONE);
            }
        } else {
            tvNewData.setText("(-) " + quantity);
            if (Double.parseDouble(tvPreviousData.getText().toString()) <= quantity) {
                textNote.setVisibility(View.VISIBLE);
            } else {
                tvTotal.setText(decimalFormat2Digit.format(Double.parseDouble(tvPreviousData.getText().toString())
                        - quantity));

                tempKgWeight = Double.valueOf(
                        mChooseDecimalFormat.getWeightDecimalFormat().format(
                                mReportEntity.getKgWeight()
                                        - mReportEntity.getNewCanKgData()));
                tempLiterWeight = Double.valueOf(
                        mChooseDecimalFormat.getWeightDecimalFormat().format(
                                mReportEntity.getLtrsWeight() -
                                        mReportEntity.getNewCanLiterData()));


                if (amcuConfig.getAllowVisiblityForCanToggle() && amcuConfig.getCansToggling()) {
                    maxLimit = Double.parseDouble(String.valueOf(amcuConfig.getMaxLimitEmptyCans()));
                    if (quantity > maxLimit)
                        textNote.setVisibility(View.VISIBLE);
                    else
                        textNote.setVisibility(View.GONE);
                }
            }
        }

    }

    /**
     * Display the quantity on the views and
     * Enable the submit button
     */
    private void displayQuantity() {
        etQuantity.setText(String.valueOf(mReportEntity.getDisplayQuantity()));

        if (!btnDone.isEnabled()) {
            btnDone.setEnabled(true);
        }
        if (!initialWeightDataReceived) {
            initialWeightDataReceived = true;
            //  btnDone.setBackgroundColor(getResources().getColor(R.color.focused));
            btnDone.requestFocus();
        }
    }

    private void openWsConnection() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (wsManager != null) {
                    wsManager.openConnection();
                }
            }
        }).start();
    }

    private void closeWsConnection() {
        if (wsManager != null) {
            wsManager.closeConnection();
            wsManager = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnNext: {

                onSubmitNextCan(mReportEntity.getQuantity());
                break;
            }
            case R.id.btnDone: {

                if (btnDone.getText().toString().equalsIgnoreCase("Done")
                        && isValidWeight() && validateSID() && !isFilledOrEmptyCanStarted &&
                        !amcuConfig.getEnableFilledOrEmptyCans()) {
                    if (checkAlertForMaxWeight()) {
                        collectionHelper.showAlertForManualWeight(CollectionHelper.FROM_WEIGHTCOLLECTION);
                    } else {
                        tareAndAddToDatabase();
                    }
                } else if ((!mReportEntity.isQuantityDone()) && isValidWeight()) {

                    if (checkAlertForMaxWeight()) {
                        collectionHelper.showAlertForManualWeight(CollectionHelper.FROM_WEIGHTCOLLECTION);
                    } else {
                        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
                        onSubmitActions();
                    }

                } else if (mReportEntity.isQuantityDone() && isValidWeight() && validateSID()) {
                    tareAndAddToDatabase();
                } else if (!isValidWeight()) {
                    Toast.makeText(WeightCollectionActivityV2.this,
                            "Please enter valid weight", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }


    private void tareAndAddToDatabase() {
        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
        tareWs();
        try {
            addToDatabase();
            mSmartCCUtil.setCollectionStartData(mReportEntity);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            mSmartCCUtil.saveReportsOnSdCard(mReportEntity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // set dipping weight feature in enable
        if (amcuConfig.getDeviationWeightAlert()
                && !amcuConfig.getTareEnable() &&
                mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.AUTO)) {
            showAlertToTare();
        } else {
            onFinish();
        }

    }


    private void onSubmitActions() {
        if (mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.AUTO) &&
                !validationHelper.validMilkWeight(mReportEntity.getQuantity(), getApplicationContext())
                && !isFilledOrEmptyCanStarted) {
            collectionHelper.showAlertForManualWeight(CollectionHelper.FROM_WEIGHTCOLLECTION);
        } else {
            afterQuantityDone();
        }
    }

    public void afterQuantityDone() {
        tareWSOverSerialManagerAfterQuantityDone();
        etNumberOfCans.setEnabled(true);
        etNumberOfCans.requestFocus();
        etQuantity.setEnabled(false);
        btnDone.setText("Done");
        btnDone.setBackground(getResources().getDrawable(R.drawable.btn_selector_positive));
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        btnNextCan.setVisibility(View.GONE);
        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
    }

    public void tareWSOverSerialManagerAfterQuantityDone() {

        if (amcuConfig.getTareEnable()) {
            tareWs();
        }

    }

    private void onSubmitNextCan(double weight) {
        if (mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            Util.displayErrorToast("Enter next can weight manually!", WeightCollectionActivityV2.this);
            return;
        }
        if (!isFilledOrEmptyCanStarted
                && validationHelper.validMilkWeight(weight, WeightCollectionActivityV2.this)) {
            //If multiple can collection not yet started
            alertForRemoveCan();
        } else if (isFilledOrEmptyCanStarted && weight > 0) {
            alertForRemoveCan();
        } else {
            Toast.makeText(WeightCollectionActivityV2.this,
                    "No valid weight, or enter weight manually", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * ON click of next can this will ask operator to remove the
     * existing can, and start next can collection
     */
    private void alertForRemoveCan() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                WeightCollectionActivityV2.this);
        // set title

        alertDialogBuilder.setTitle("Reset weighing scale!");
        // set dialog message
        alertDialogBuilder
                .setMessage("Please remove the can and press OK to tare the weighing scale")
                .setCancelable(false);


        alertDialogBuilder.setPositiveButton("Cancel",

                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        alertDialogBuilder.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        ignoreInitialWSData = true;
                        try {
                            tareWs();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isFilledOrEmptyCanStarted = true;
                        alertForNextCan(String.valueOf(mReportEntity.getQuantity()));
                        dialog.dismiss();
                    }
                });
        alertForRemoveCanAndTare = alertDialogBuilder.create();
        alertForRemoveCanAndTare.show();
    }

    /**
     * This alert to display the quantity for
     * Previous can, filled or empty can and total quantity
     *
     * @param actualData
     */
    public void alertForNextCan(String actualData) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(WeightCollectionActivityV2.this);
        LayoutInflater inflater = WeightCollectionActivityV2.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.milkstate_dialogbox, null);


        RadioButton radioButtonEmpty = (RadioButton) dialogView.findViewById(R.id.radioBtnempty);
        RadioButton radioButtonFilled = (RadioButton) dialogView.findViewById(R.id.radioBtnfilled);
        final Button btnAdd = (Button) dialogView.findViewById(R.id.btnAdd);
        final Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);

        tvPreviousData = (TextView) dialogView.findViewById(R.id.tvprevious_data);
        tvNewData = (TextView) dialogView.findViewById(R.id.tvnew_vol_data);
        tvTotal = (TextView) dialogView.findViewById(R.id.tvtotal_vol_data);
        textNote = (TextView) dialogView.findViewById(R.id.textNote);

        tvPreviousData.setText(actualData);
        tvTotal.setText(actualData);

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
                        break;
                    case R.id.radioBtnempty:
                        tvNewData.setText("(-) 00.00");
                        radioStatus = "empty";
                        break;

                }
            }
        });

        btnAdd.requestFocus();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mReportEntity.setNewCanRecord(0.00);
                mReportEntity.setNewCanLiterData(0.00);
                mReportEntity.setNewCanKgData(0.00);
                alertForFilledAndEmptyCan.dismiss();

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioStatus.equalsIgnoreCase("empty") && (
                        mReportEntity.getQuantity() < mReportEntity.getNewCanRecord())) {
                    Util.displayErrorToast("Invalid can weight", WeightCollectionActivityV2.this);
                } else if (validationHelper.validMilkWeight(
                        mReportEntity.getNewCanRecord(), WeightCollectionActivityV2.this)) {

                    mReportEntity.setQuantity(validationHelper.getDoubleFromString(
                            tvTotal.getText().toString().trim(), mReportEntity.getQuantity()));
                    etQuantity.setText(String.valueOf(mReportEntity.getDisplayQuantity()));
                    mReportEntity.setKgWeight(tempKgWeight);
                    mReportEntity.setLtrsWeight(tempLiterWeight);
                    if (radioStatus.equalsIgnoreCase("filled")) {
                        amcuConfig.setLastAlertToggleState("empty");
                    } else {
                        amcuConfig.setLastAlertToggleState("filled");
                    }
                    mReportEntity.setNewCanRecord(0);
                    mReportEntity.setNewCanKgData(0);
                    mReportEntity.setNewCanLiterData(0);
                    alertForFilledAndEmptyCan.dismiss();
                }
            }
        });

        builder.setCancelable(false);
        builder.setView(dialogView);
        alertForFilledAndEmptyCan = builder.create();
        alertForFilledAndEmptyCan.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertForFilledAndEmptyCan.show();
        Window window = alertForFilledAndEmptyCan.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    /**
     * After pressing the submit button
     * If tare is disable and stop deviation weight feature is enable
     * This alert will pop and read the data from the Weighing scale, and allow user
     * to continue with new quantity or tare it for next collection
     */

    private void showAlertToTare() {
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        final Button btnCancel, btnTareAndCancel;

        LayoutInflater inflater = WeightCollectionActivityV2.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_weighing_text, null);
        alertBuilder.setView(dialogView);

        etZeroAlertText = (TextView) dialogView.findViewById(R.id.etZeroAlertText);
        btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        btnTareAndCancel = (Button) dialogView.findViewById(R.id.btnTareAndCancel);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinish();
            }
        });

        btnTareAndCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tareWs();
                onFinish();
            }
        });

        alertForZeroDialog = alertBuilder.create();
        alertForZeroDialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertForZeroDialog.getWindow().getAttributes());
        lp.width = 450;
        lp.height = 300;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertForZeroDialog.getWindow().setAttributes(lp);

    }

    private void onFinish() {
        startActivity(new Intent(WeightCollectionActivityV2.this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

    /**
     * Add the missing and required parameter in report entity and update in DB
     *
     * @throws SQLException
     */
    private void addToDatabase() throws SQLException {
        mReportEntity.setSampleNumber(validationHelper.getIntegerFromString(etSId.getText()
                .toString().trim(), 0));
        mReportEntity.setNumberOfCans(validationHelper.getIntegerFromString(etNumberOfCans.getText()
                .toString().trim(), 1));

        mReportEntity.weighingTime = System.currentTimeMillis();

        if (mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            QuantityEntity quantityEntity =
                    collectionHelper.getQuantityItems(mReportEntity.getRateCalculationQuanity());
            mReportEntity.kgWeight = quantityEntity.kgQuantity;
            mReportEntity.ltrsWeight = quantityEntity.ltrQuanity;
        }

        collectionRecordDao.saveOrUpdate(mReportEntity);
    }

    public void tareWs() {
        if (amcuConfig.getTareEnable()) {
            if (wsManager != null) {
                wsManager.tare();
                isTareDone = true;
            }
        }
    }

    public boolean isValidWeight() {

        boolean isValid = true;
        if (mReportEntity.getQuantity() > amcuConfig.getKeyMinValidWeight()) {
            if (mReportEntity.getQuantityMode().equalsIgnoreCase("Auto") && !isFilledOrEmptyCanStarted
                    && mReportEntity.getQuantity() > amcuConfig.getMaxlimitOfWeight()) {
                Util.displayErrorToast("Weight should be less than " + amcuConfig.getMaxlimitOfWeight()
                        , WeightCollectionActivityV2.this);
                isValid = false;
            } else if (isFilledOrEmptyCanStarted ||
                    mReportEntity.quantityMode.equalsIgnoreCase(SmartCCConstants.MANUAL)) {
                isValid = true;
            }
        } else {
            Util.displayErrorToast("Invalid quantity "
                    , WeightCollectionActivityV2.this);
            isValid = false;
        }

        return isValid;
    }

    public boolean validateSID() {

        int sid = 0;
        try {
            sid = Integer.parseInt(etSId.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sid <= 0) {
            Toast.makeText(this, "Please Enter Valid Serial Id", Toast.LENGTH_SHORT).show();
            resetEtTextSID();
            return false;
        }

        if ((etSId.getText().toString().trim().length() > 7) || (Integer.valueOf(etSId.getText().toString().trim()) > Util.LAST_SEQ_NUM)) {
            resetEtTextSID();
            Toast.makeText(this, "SID should be less than " + Util.LAST_SEQ_NUM, Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etSId.getText().toString().trim().equalsIgnoreCase("0")) {
            resetEtTextSID();
            Toast.makeText(this, "Serial Id can't be 0", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etSId.getText().toString().trim().equalsIgnoreCase("")) {
            resetEtTextSID();
            Toast.makeText(this, "Please Enter Valid Serial Id", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (Integer.valueOf(etSId.getText().toString().trim()) < lastSid) {


            resetEtTextSID();
            Toast.makeText(this, "SID should be greater than or same as " + lastSid, Toast.LENGTH_SHORT).show();
            return false;
        }


        return true;
    }

    public void resetEtTextSID() {
        etSId.setFocusable(true);
    }

    private void addWatcherForQuantity() {


        etQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeChage = etQuantity.getText().toString();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                afterChange = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (wsManager != null) {
                    Log.d("Close connection: " + "WS", beforeChage + " " + afterChange);
                    if (!amcuConfig.getEnableFilledOrEmptyCans()) {
                        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
                    }
                    closeWsConnection();
                    Util.displayErrorToast("Closing WS watcher", WeightCollectionActivityV2.this);
                    if (beforeChage != null && !beforeChage.equalsIgnoreCase("")) {
                        String out = getChangedText(beforeChage, afterChange);
                        if (out != null) {
                            etQuantity.setText(out);
                        } else {
                            etQuantity.getText().clear();
                            etQuantity.setText("");
                        }
                        etQuantity.setSelection(etQuantity.getText().toString().length());
                    } else {
                    }

                }

                if (mReportEntity.getTippingStartTime() == 0) {
                    mReportEntity.setTippingStartTime(System.currentTimeMillis());
                }
                if (mReportEntity.getQuantityMode().equalsIgnoreCase(SmartCCConstants.AUTO)) {
                    mReportEntity.setQuantityMode(SmartCCConstants.MANUAL);
                }

                mReportEntity.setQuantity(validationHelper.getDoubleFromString(
                        etQuantity.getText().toString().trim(), 0));

                mReportEntity.setTippingEndTime(System.currentTimeMillis());

            }
        });

    }

    @Override
    public void onBackPressed() {

        onFinish();

    }

    @Override
    public boolean exceedWeightLimit() {
        etQuantity.requestFocus();
        etQuantity.setSelection(etQuantity.getText().toString().length());
        return false;
    }

    @Override
    public boolean proceedWithExceedWeightLimit() {
        onSubmitActions();
        return false;
    }

    public boolean checkAlertForMaxWeight() {
        if (((mReportEntity.getQuantityMode().equalsIgnoreCase("Manual")
                && mReportEntity.getQuantity() >= amcuConfig.getMaxlimitOfWeight()) &&
                !isFilledOrEmptyCanStarted)) {
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

    class PingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            wsPing = ping(SmartCCConstants.wsIp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            cbWs.setChecked(wsPing);
            mWifiUtility.checkWisensConnectivity(context, DeviceName.WS, wsPing);
        }
    }

}
