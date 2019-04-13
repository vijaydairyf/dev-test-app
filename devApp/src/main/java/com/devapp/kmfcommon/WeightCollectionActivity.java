package com.devapp.kmfcommon;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.devicemanager.WeighingScaleManager;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.WeightLimit;
import com.devapp.devmain.main.FarmerScannerActivity;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.user.WifiUtility;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.devmain.ws.WsFactory;
import com.devapp.devmain.ws.WsManager;
import com.devapp.smartcc.entityandconstants.QuantityEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import static com.devapp.devmain.user.Util.ping;

public class WeightCollectionActivity extends AppCompatActivity implements View.OnClickListener
        , WeightLimit {

    public static WeightCollectionActivity mWeightCollectionActivity;
    public Context context = this;
    public double newRcordFilledCan = 0;
    public double tempLt, tempKg;
    boolean wsPing;
    AmcuConfig amcuConfig;
    SessionManager session;
    EditText etCenterName, etCenterCode, etQuantity, etNumberOfCans, etSId;
    Button btnDone, btnNextCan;
    StringBuilder sbMessage = new StringBuilder();
    String TAG = "WeightCollectionActivity";
    int WeighingBaudrate;
    Calendar calendar;
    boolean isCavinCare, isGoldTech;
    boolean ignoreInitialWSData;
    WeighingScaleManager weighingScaleManager;
    ValidationHelper validationHelper;
    boolean isTareDone;
    TextView tvPreviousData, tvNewData, tvTotal, textNote;
    double weightRecord;
    String prevRecord, radioStatus;
    boolean isBtnEnable;
    DecimalFormat decimalFormatWeight;
    double addedWater = 0;
    String isAutoManual = "Manual", isMaAutoManual = "Auto", isWsManual = "Auto";
    String startTime;
    UsbManager mUsbManager;
    boolean sidChanged = false;
    CollectionHelper collectionHelper;
    TextView tvQuantity;
    SmartCCUtil smartCCUtil;
    DriverConfiguration driverConfiguration;
    boolean isFilledOrEmptyCanStarted;
    AlertDialog alertForFilledAndEmptyCan;
    AlertDialog alertForRemoveCanAndTare;
    android.support.v7.app.AlertDialog alertForZeroDialog;
    double kgWeight = 0.00, ltrWeight = 0.00;
    DecimalFormat decimalFormat2Digit = new DecimalFormat("#0.00");
    QuantityEntity quantityEntity = null;
    long tippingStartTime = 0, tippingEndTime;
    int lastSid;
    private LinearLayout statusLayout;
    private CheckBox cbWs;
    private TextView etZeroAlertText;
    private boolean isZeroAlertDialogEnable, initialWeightDataReceived;
    private WsManager wsManager;
    private CollectionRecordDao collectionRecordDao;
    private WifiUtility wifiUtility;

    //To get data from weighing scale
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

    public static WeightCollectionActivity getInstance() {
        return mWeightCollectionActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWeightCollectionActivity = WeightCollectionActivity.this;
        session = new SessionManager(WeightCollectionActivity.this);
        amcuConfig = AmcuConfig.getInstance();
        collectionHelper = new CollectionHelper(WeightCollectionActivity.this);
        wifiUtility = new WifiUtility();

        setContentView(R.layout.activity_weight_collection);

        etCenterName = (EditText) findViewById(R.id.etCenterName);
        etCenterCode = (EditText) findViewById(R.id.etCenterId);
        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etNumberOfCans = (EditText) findViewById(R.id.etNOC);


        TextView tvNoc = (TextView) findViewById(R.id.tvNOC);
        tvQuantity = (TextView) findViewById(R.id.tvQuantity);

        // SID
        etSId = (EditText) findViewById(R.id.etSId);
        etSId.setFocusable(true);

        Util.alphabetValidation(etSId, Util.ONLY_NUMERIC, WeightCollectionActivity.this, 0);
        getLastSID();
        //iSID = saveSession.getChillingWeightColletionSID() + 1;
        etSId.setText("" + lastSid);
        etSId.setSelection(etSId.length());
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
        btnDone = (Button) findViewById(R.id.btnDone);
        btnNextCan = (Button) findViewById(R.id.btnNext);
        cbWs = (CheckBox) findViewById(R.id.cb_ws);
        statusLayout = (LinearLayout) findViewById(R.id.status_layout);

        etCenterName.setText(session.getFarmerName());
        etCenterCode.setText(session.getFarmerID());

        btnDone.setOnClickListener(this);
        btnNextCan.setOnClickListener(this);
        // Util.alphabetValidation(etQuantity, Util.ONLY_DECIMAL, WeightCollectionActivity.this, 8);
        Util.alphabetValidation(etNumberOfCans, Util.ONLY_NUMERIC, WeightCollectionActivity.this, 3);


        etNumberOfCans.setEnabled(false);

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
        }
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
        cbWs.setChecked(wsPing);
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
                    case DeviceName.WS:
                        cbWs.setVisibility(View.VISIBLE);
                        break;

                }
            }
        }
        cbWs.setChecked(amcuConfig.getWsPingValue());

    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {
        closeWsConnection();
        storeLastPingValues();
        super.onStop();
    }

    private void storeLastPingValues() {
        amcuConfig.setWsPingValue(wsPing);
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
        // etSId.setText("");
        etSId.setFocusable(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

        driverConfiguration = new DriverConfiguration();
        smartCCUtil = new SmartCCUtil(WeightCollectionActivity.this);
        weighingScaleManager = new WeighingScaleManager(WeightCollectionActivity.this);
        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        validationHelper = new ValidationHelper();
        WeighingBaudrate = amcuConfig.getWeighingbaudrate();
        calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();

        decimalFormatWeight = chooseDecimalFormat.getWeightDecimalFormat();
        decimalFormat2Digit = chooseDecimalFormat.get2DigitFormatWeight();

        wsManager = WsFactory.getWs(DeviceName.WS, WeightCollectionActivity.this);
        if (wsManager != null) {
            wsManager.setOnNewDataListener(new WsManager.OnNewDataListener() {
                @Override
                public void onNewData(final double data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            boolean retVal = false;
                            double weight = 0;
                            setTippingStartTime();
                            if (data != 0) {
                                try {
                                    weight = data;

                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }


                                if (weight != 0 && weight > 0) {

                                    if (isZeroAlertDialogEnable) {
                                        setWeightForZeroAlert(String.valueOf(data));
                                        return;
                                    }
                                    if (amcuConfig.getEnableFilledOrEmptyCans() &&
                                            btnDone.getText().toString().equalsIgnoreCase("Done")) {
                                        return;
                                    }

                                    if (!btnDone.isEnabled()) {
                                        btnDone.setEnabled(true);
                                    }
                                    displayWeight(String.valueOf(data));
                                    setTippingEndTime();
                                }
                            }
                        }
                    });
                }
            });
        }

        if (amcuConfig.getWeighingScale().equalsIgnoreCase("EVEREST")) {
            isCavinCare = true;
        } else if (amcuConfig.getWeighingScale().equalsIgnoreCase("GOLDTECH")) {
            isGoldTech = true;
        } else {
            isCavinCare = false;
        }
        openWsConnection();

        setQuantity();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(WeightCollectionActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:
                Util.restartTab(WeightCollectionActivity.this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(WeightCollectionActivity.this, null);
                return true;

            case KeyEvent.KEYCODE_1:

                enableKeyboardForQuanity("1");

                return true;
            case KeyEvent.KEYCODE_2:
                enableKeyboardForQuanity("2");
                return true;
            case KeyEvent.KEYCODE_3:
                enableKeyboardForQuanity("3");
                return true;
            case KeyEvent.KEYCODE_4:
                enableKeyboardForQuanity("4");
                return true;
            case KeyEvent.KEYCODE_5:
                enableKeyboardForQuanity("5");
                return true;
            case KeyEvent.KEYCODE_6:
                enableKeyboardForQuanity("6");
                return true;
            case KeyEvent.KEYCODE_7:
                enableKeyboardForQuanity("7");
                return true;
            case KeyEvent.KEYCODE_8:
                enableKeyboardForQuanity("8");
                return true;
            case KeyEvent.KEYCODE_9:
                enableKeyboardForQuanity("9");
                return true;
            case KeyEvent.KEYCODE_0:
                enableKeyboardForQuanity("0");
                return true;
            case KeyEvent.KEYCODE_PERIOD:
                enableKeyboardForQuanity("0");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_DOT:
                enableKeyboardForQuanity("0");
                return true;

            case KeyEvent.KEYCODE_NUMPAD_0:
                enableKeyboardForQuanity("0");
                return true;

            case KeyEvent.KEYCODE_NUMPAD_1:
                enableKeyboardForQuanity("1");

                return true;
            case KeyEvent.KEYCODE_NUMPAD_2:
                enableKeyboardForQuanity("2");
                return true;
            case KeyEvent.KEYCODE_NUMPAD_3:
                enableKeyboardForQuanity("3");
                return true;

            case KeyEvent.KEYCODE_NUMPAD_4:
                enableKeyboardForQuanity("4");

                return true;

            case KeyEvent.KEYCODE_NUMPAD_5:
                enableKeyboardForQuanity("5");

                return true;
            case KeyEvent.KEYCODE_NUMPAD_6:
                enableKeyboardForQuanity("6");

                return true;
            case KeyEvent.KEYCODE_NUMPAD_7:
                enableKeyboardForQuanity("7");

                return true;
            case KeyEvent.KEYCODE_NUMPAD_8:
                enableKeyboardForQuanity("8");

                return true;
            case KeyEvent.KEYCODE_NUMPAD_9:
                enableKeyboardForQuanity("9");

                return true;
            case KeyEvent.KEYCODE_NUMPAD_ENTER:


                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_UP:

                btnDone.setBackground(getResources().getDrawable(R.drawable.btn_selector_positive));
//                btnDone.setBackgroundColor(getResources().getColor(R.color.btnblueNormal));
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                btnDone.requestFocus();
//                btnDone.setBackgroundColor(getResources().getColor(R.color.focused));
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), FarmerScannerActivity.class);
        startActivity(intent);
        finish();
    }

    public void setWeight(String record) {
        //if conversion factor is enabled, it will consider milk as in liter format
        try {

            double weight_Record = Double.parseDouble(record);
            weight_Record = weight_Record / amcuConfig.getWeighingDivisionFactor();
            record = getWeightRecord(weight_Record);

        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        try {
            etQuantity.setText(record);
            weightRecord = Double.valueOf(record);

            if (!btnDone.isEnabled()) {
                isBtnEnable = true;
                btnDone.setEnabled(true);
            }
            btnNextCan.setEnabled(true);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        //to highlight Quantity button once WS data is received
        enableQuantityButton();
    }

    // alert for milk fill and empty
    public void openMilkStateDialog(String actualData) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(WeightCollectionActivity.this);
        LayoutInflater inflater = WeightCollectionActivity.this.getLayoutInflater();
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
                newRcordFilledCan = 0.00;
                alertForFilledAndEmptyCan.dismiss();

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioStatus.equalsIgnoreCase("empty") && (
                        weightRecord < newRcordFilledCan)) {
                    Util.displayErrorToast("Invalid can weight", WeightCollectionActivity.this);
                } else if (validationHelper.validMilkWeight(newRcordFilledCan, WeightCollectionActivity.this)) {
                    weightRecord = Double.valueOf(tvTotal.getText().toString());
                    etQuantity.setText(String.valueOf(weightRecord));
                    ltrWeight = ltrWeight + tempLt;
                    kgWeight = kgWeight + tempKg;
                    if (radioStatus.equalsIgnoreCase("filled")) {
                        amcuConfig.setLastAlertToggleState("empty");
                    } else {
                        amcuConfig.setLastAlertToggleState("filled");
                    }
                    newRcordFilledCan = 0.00;
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

    public void setAlertText(String record) {
        tvPreviousData.setText(String.valueOf(weightRecord));

        record = decimalFormatWeight.format(Double.parseDouble(record));
        record = getWeightRecordForAlert(Double.valueOf(record));
        double minLimit, currentRecord, maxLimit;
        currentRecord = Double.parseDouble(record);
        newRcordFilledCan = currentRecord;

        if (radioStatus.equalsIgnoreCase("filled")) {
            tvNewData.setText("(+) " + record);
            tvTotal.setText(decimalFormat2Digit.format(Double.parseDouble(tvPreviousData.getText().toString())
                    + Double.parseDouble(record)));
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
                        - Double.parseDouble(record)));
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
                WeightCollectionActivity.this);
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
                            tareWSOverSerialManager();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        isFilledOrEmptyCanStarted = true;
                        openMilkStateDialog(String.valueOf(weightRecord));
                        dialog.dismiss();
                    }
                });
        alertForRemoveCanAndTare = alertDialogBuilder.create();
        alertForRemoveCanAndTare.show();
    }

    public void tareWSOverSerialManager() {
        if (wsManager != null) {
            wsManager.tare();
            isTareDone = true;
        }
        /*byte[] tareMsg = smartCCUtil.getTareMessage().getBytes(Charset.forName("UTF-8"));
        try {
            mSerialIoManager.writeAsync(tareMsg);
            isTareDone = true;
        } catch (NullPointerException e) {
            e.printStackTrace();
        }*/
    }


    // this part related to Weighing scale connection

    public void tareWSOverSerialManagerAfterQuantityDone() {

        if (amcuConfig.getTareEnable()) {
            tareWSOverSerialManager();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnNext: {

                if (amcuConfig.getWsManual()) {
                    weightRecord = Double.valueOf(etQuantity.getText().toString().trim());
                }

                double dWeightRecord = 0;
                try {
                    dWeightRecord = weightRecord;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                onSubmitNextCan(dWeightRecord);

                break;
            }
            case R.id.btnDone: {
                if (btnDone.getText().toString().equalsIgnoreCase("Quantity Done") && isValidWeight()) {

                    if (!checkForManualWeight()) {
                        collectionHelper.showAlertForManualWeight(CollectionHelper.FROM_WEIGHTCOLLECTION);
                    } else {
                        afterQuantityDone();
                    }

                } else if (isValidWeight() && validateSID()) {
                    tareWSOverSerialManagerAfterQuantityDone();

                    if (!checkForManualWeight() && !amcuConfig.getEnableFilledOrEmptyCans()) {
                        collectionHelper.showAlertForManualWeight(CollectionHelper.FROM_WEIGHTCOLLECTION);
                    } else {
                        afterSuccessFulWeight();
                    }

                } else if (!isValidWeight()) {
                    Toast.makeText(WeightCollectionActivity.this,
                            "Please enter valid weight", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    public void afterSuccessFulWeight() {
        addToDatabase();
        if (amcuConfig.getDeviationWeightAlert()
                && !amcuConfig.getTareEnable() &&
                isWsManual.equalsIgnoreCase("Auto")) {
            alertDialogForWeight();
        } else {
            onFinish();
        }

    }

    public String addToDatabase() {

        String dbError = null;
        //  Toast.makeText(MilkCollectionActivity.this, "Get Selected Item" + spinnerItem, Toast.LENGTH_SHORT).show();

        ReportEntity repEntity = null;
        try {
            repEntity = getReportEntity(0);
            smartCCUtil.saveReportsOnSdCard(repEntity);

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (repEntity != null) {
            Util.setDailyDateOrShift(Util.getTodayDateAndTime(7, WeightCollectionActivity.this, true),
                    repEntity.postShift);
//            DatabaseManager dbManager = new DatabaseManager(WeightCollectionActivity.this);
//            try {
//                dbManager.addCollectionRecord(repEntity);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            repEntity.resetSentMarkers();
            repEntity.resetSmsMarkers();
            collectionRecordDao.saveOrUpdate(repEntity);

        } else {
            dbError = "Error while creating collection record.";
        }
        return dbError;
    }

    public ReportEntity getReportEntity(int commited) {

        QuantityEntity quantityEntity = new QuantityEntity();

        if (!isWsManual.equalsIgnoreCase("Auto") || weightRecord == 0) {
            weightRecord = Double.valueOf(etQuantity.getText().toString().trim());
            try {
                weightRecord = Double.valueOf(decimalFormatWeight.format(Double.valueOf(weightRecord)));
            } catch (Exception e) {
                weightRecord = 0.00;
                e.printStackTrace();
            }

            quantityEntity.displayQuantity = weightRecord;

            if (amcuConfig.getKeyRateChartInKg()) {
                kgWeight = weightRecord;
                setLtrWeight(kgWeight);
            } else {
                ltrWeight = weightRecord;
                setKgWeight(ltrWeight);
            }
            quantityEntity.kgQuantity = kgWeight;
            quantityEntity.ltrQuanity = ltrWeight;

        } else {
            quantityEntity = collectionHelper.getQuantityItems(
                    weightRecord);

        }


        int txNumber = new SessionManager(WeightCollectionActivity.this)
                .getTXNumber() + 1;

        String currentShift = Util.getCurrentShift();

        String date = smartCCUtil.getReportFormatDate();
        ReportEntity repEntity = new ReportEntity();
        startTime = Util.getTodayDateAndTime(3, WeightCollectionActivity.this, true);

        repEntity.user = session.getUserId();
        repEntity.farmerId = session.getFarmerID();
        repEntity.farmerName = session.getFarmerName();
        repEntity.socId = session.getCollectionID();
        repEntity.quantity = quantityEntity.displayQuantity;
        repEntity.time = startTime;
        repEntity.milkType = session.getMilkType();
        repEntity.lDate = Util.getDateInLongFormat(date);
        repEntity.txnNumber = Integer.parseInt(Util.getTxnNumber(txNumber));
        repEntity.miliTime = calendar.getTimeInMillis();

        //milkAnanalyserTime = Util.getTodayDateAndTime(6);
        //qualityTime = Calendar.getInstance().getTimeInMillis();

        repEntity.milkAnalyserTime = calendar.getTimeInMillis();

        //quantityTime = Calendar.getInstance().getTimeInMillis();
        repEntity.weighingTime = calendar.getTimeInMillis();
        repEntity.awm = addedWater;
        repEntity.temp = 0;
        repEntity.fat = 0;
        repEntity.snf = 0;

        repEntity.amount = 0;
        repEntity.rate = 0;
        repEntity.clr = 0;

        repEntity.status = "NA";
        repEntity.milkQuality = "NA";
        repEntity.recordStatus = Util.RECORD_STATUS_INCOMPLETE;
        repEntity.milkAnalyserTime = calendar.getTimeInMillis();

        if (isAutoManual != null) {
            repEntity.manual = isAutoManual;
            repEntity.quantityMode = isWsManual;
            repEntity.qualityMode = isMaAutoManual;
        } else {
            repEntity.manual = "Manual";
            repEntity.quantityMode = isWsManual;
            repEntity.qualityMode = isMaAutoManual;
        }

        repEntity.bonus = 0.00;
        repEntity.recordCommited = commited;
        if (session.getIsChillingCenter()) {
            repEntity.collectionType = Util.REPORT_TYPE_MCC;
        } else {
            repEntity.collectionType = Util.REPORT_TYPE_FARMER;
        }
        String numberOfCans = etNumberOfCans.getText().toString().replace(" ", "");
        int cans = 1;

        try {
            cans = Integer.parseInt(numberOfCans);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }


        repEntity.numberOfCans = cans;
        // Adding milkquality
        // Adding serial number

        int seqNum = 1;
        try {
            seqNum = Integer.parseInt(etSId.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        repEntity.sampleNumber = seqNum;


        repEntity.centerRoute = Util.getRouteFromChillingCenter(WeightCollectionActivity.this, session.getFarmerID());


        repEntity.kgWeight = quantityEntity.kgQuantity;
        repEntity.ltrsWeight = quantityEntity.ltrQuanity;

        if (repEntity.quantityMode == null
                || repEntity.quantityMode.equalsIgnoreCase("Manual") || tippingStartTime == 0) {
            setTippingTimeForWSManual();
        } else {
            setTippingEndTime();
        }

        repEntity.tippingStartTime = tippingStartTime;
        repEntity.tippingEndTime = tippingEndTime;
        repEntity.milkStatusCode = new SmartCCUtil(getApplicationContext()).getMilkStatusCode("Good");

        repEntity.fatKg = 0;
        repEntity.snfKg = 0;

        repEntity.postDate = SmartCCUtil.getDateInPostFormat();
        repEntity.postShift = SmartCCUtil.getShiftInPostFormat(WeightCollectionActivity.this);
        smartCCUtil.setCollectionStartData(repEntity);
        return repEntity;
    }

    public void onFinish() {
        startActivity(new Intent(WeightCollectionActivity.this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

    public boolean isValidWeight() {
        double dWeight = -1;

        try {
            dWeight = Double.parseDouble(etQuantity.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (dWeight > 0) {

            if (isWsManual.equalsIgnoreCase("Auto") && !isFilledOrEmptyCanStarted) {
                return validationHelper.validMilkWeight(dWeight, getApplicationContext());
            } else if (isFilledOrEmptyCanStarted || isWsManual.equalsIgnoreCase("Manual")) {
                return true;
            } else {
                return validationHelper.validMilkWeight(dWeight, getApplicationContext());
            }

        } else {
            return false;
        }
    }

    public void openWsConnection() {

        if (wsManager != null) {
            wsManager.openConnection();
        }
    }

    private void closeWsConnection() {
        if (wsManager != null) {
            wsManager.closeConnection();
        }
    }

    public void enableKeyboardForQuanity(String str) {
        if (etSId.hasFocus()) {

        } else if (!isWsManual.equalsIgnoreCase("Manual") && amcuConfig.getWsManual() &&
                (btnDone.getText().toString().equalsIgnoreCase("Quantity Done") ||
                        (!amcuConfig.getEnableFilledOrEmptyCans() &&
                                btnDone.getText().toString().equalsIgnoreCase("Done")))) {

            closeWsConnection();
            etQuantity.setText(str);
            etQuantity.setSelection(etQuantity.getText().toString().length());
            isWsManual = "Manual";
        }

        sidChanged = false;
    }

    public void setKgWeight(double record) {
        kgWeight = Double.valueOf(decimalFormat2Digit.format(record *
                Double.parseDouble(amcuConfig.getConversionFactor())));
    }

    public void setLtrWeight(double record) {
        ltrWeight = Double.valueOf(decimalFormat2Digit.format(record /
                Double.parseDouble(amcuConfig.getConversionFactor())));
    }

    public void setQuantity() {
        if (amcuConfig.getKeyRateChartInKg() || amcuConfig.getMyRateChartEnable()) {
            tvQuantity.setText("Qty(kgs)");
        } else {
            tvQuantity.setText("Qty(ltrs)");
        }

    }

    public String getWeightRecordForAlert(double weight_Record) {

        String record = "0.00", mLtRecord = "0.00", mKgRecord = "0.00";
        weight_Record = weight_Record / amcuConfig.getWeighingDivisionFactor();

        if (!amcuConfig.getAllowInKgformat() && amcuConfig.getMyRateChartEnable()) {
            mLtRecord = decimalFormatWeight.format(weight_Record);
            //use kg value to calculate the rate
            record = decimalFormat2Digit.format(Double.parseDouble(mLtRecord) *
                    Double.parseDouble(amcuConfig.getConversionFactor()));
            mKgRecord = decimalFormatWeight.format(Double.parseDouble(record));
        }


        if (amcuConfig.getAllowInKgformat()) {
            mKgRecord = decimalFormatWeight.format(weight_Record);
            mLtRecord = decimalFormat2Digit.format(Double.parseDouble(mKgRecord) /
                    Double.parseDouble(amcuConfig.getConversionFactor()));
        } else {
            mLtRecord = decimalFormatWeight.format(weight_Record);
            mKgRecord = decimalFormat2Digit.format(Double.parseDouble(mLtRecord) *
                    Double.parseDouble(amcuConfig.getConversionFactor()));
        }

        if (amcuConfig.getKeyRateChartInKg()) {
            record = decimalFormat2Digit.format(Double.parseDouble(mKgRecord));
        } else {
            record = decimalFormat2Digit.format(Double.parseDouble(mLtRecord));
        }


        tempKg = Double.valueOf(mKgRecord);
        tempLt = Double.valueOf(mLtRecord);
        return record;
    }

    public String getWeightRecord(double weight_Record) {
        String record = "0.00";
        if (!amcuConfig.getAllowInKgformat() && amcuConfig.getMyRateChartEnable()) {
            ltrWeight = Double.valueOf(decimalFormatWeight.format(weight_Record));
            record = decimalFormat2Digit.format(ltrWeight *
                    Double.parseDouble(amcuConfig.getConversionFactor()));
            kgWeight = Double.valueOf(record);
        } else if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {
            kgWeight = Double.valueOf(decimalFormatWeight.format(weight_Record));
            record = decimalFormat2Digit.format(kgWeight /
                    Double.parseDouble(amcuConfig.getConversionFactor()));
            ltrWeight = Double.valueOf(record);

        } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            ltrWeight = Double.valueOf(decimalFormatWeight.format(weight_Record));
            record = decimalFormat2Digit.format(ltrWeight *
                    Double.parseDouble(amcuConfig.getConversionFactor()));
            kgWeight = Double.valueOf(record);
        } else if (amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            kgWeight = Double.valueOf(decimalFormatWeight.format(weight_Record));
            record = String.valueOf(kgWeight);
            ltrWeight = Double.valueOf(decimalFormat2Digit.format(kgWeight /
                    Double.parseDouble(amcuConfig.getConversionFactor())));
        } else {
            ltrWeight = Double.valueOf(decimalFormatWeight.format(weight_Record));
            record = decimalFormatWeight.format(weight_Record);
            kgWeight = Double.valueOf(decimalFormat2Digit.format(ltrWeight *
                    Double.parseDouble(amcuConfig.getConversionFactor())));
        }
        return record;
    }

    public void setTippingStartTime() {
        if (tippingStartTime == 0) {
            tippingStartTime = System.currentTimeMillis();

        }

    }

    public void setTippingEndTime() {
        tippingEndTime = System.currentTimeMillis();
    }

    public void setTippingTimeForWSManual() {
        tippingStartTime = System.currentTimeMillis();
        tippingEndTime = System.currentTimeMillis();
    }

    public void alertDialogForWeight() {
        isZeroAlertDialogEnable = true;
        android.support.v7.app.AlertDialog.Builder alertBuilder = new android.support.v7.app.AlertDialog.Builder(this);
        final Button btnCancel, btnTareAndCancel;

        LayoutInflater inflater = WeightCollectionActivity.this.getLayoutInflater();
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
                tareWSOverSerialManager();
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

    public void setWeightForZeroAlert(String weight) {

        if (etZeroAlertText != null) {

            etZeroAlertText.setText(weight);
            weight = validationHelper.getDoubleFromString(new DecimalFormat("##.00"),
                    etZeroAlertText.getText().toString());

            if (Double.valueOf(weight) <= amcuConfig.getDeviationWeight()
                    && etZeroAlertText.getText().toString().trim().length() > 1) {
                onFinish();
            }

        }


    }

    public void getLastSID() {

        lastSid = collectionHelper.getCurrentSID();
        etSId.setText(String.valueOf(lastSid));
    }

    public boolean checkForManualWeight() {
        if (!isWsManual.equalsIgnoreCase("Manual")) {
            return true;
        }
        double dQuantity = 0;
        String weight = etQuantity.getText().toString().trim();
        try {
            dQuantity = Double.parseDouble(weight);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return validationHelper.validMilkWeight(dQuantity, getApplicationContext());

    }

    @Override
    public boolean exceedWeightLimit() {
        etQuantity.requestFocus();
        etQuantity.setSelection(etQuantity.getText().toString().length());
        return false;
    }

    @Override
    public boolean proceedWithExceedWeightLimit() {

        if (btnNextCan.getVisibility() == View.VISIBLE) {
            afterQuantityDone();
        } else {
            afterSuccessFulWeight();
        }

        return false;
    }

    public void displayWeight(String weightRecord) {
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
            setWeight(weightRecord);
        }
    }

    public void onSubmitNextCan(double dWeightRecord) {
        if (isWsManual.equalsIgnoreCase("Manual")) {
            Util.displayErrorToast("Enter next can weight manually!", WeightCollectionActivity.this);
            return;
        }
        if (!isFilledOrEmptyCanStarted
                && validationHelper.validMilkWeight(dWeightRecord, WeightCollectionActivity.this)) {
            //If multiple can collection not yet started
            alertForRemoveCan();
        } else if (isFilledOrEmptyCanStarted && dWeightRecord > 0) {
            alertForRemoveCan();
        } else {
            Toast.makeText(WeightCollectionActivity.this,
                    "No valid weight, or enter weight manually", Toast.LENGTH_SHORT).show();
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
    }

    public void enableQuantityButton() {
        if (!initialWeightDataReceived) {
            initialWeightDataReceived = true;
            btnDone.setBackgroundColor(getResources().getColor(R.color.focused));
            btnDone.requestFocus();
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
            wifiUtility.checkWisensConnectivity(context, DeviceName.WS, wsPing);
        }
    }


}
