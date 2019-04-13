package com.devapp.devmain.macollection;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.eevoskos.robotoviews.widget.RobotoButton;
import com.eevoskos.robotoviews.widget.RobotoEditText;
import com.eevoskos.robotoviews.widget.RobotoTextView;
import com.devapp.kmfcommon.UserSelectionActivity;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
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
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.RegexTextWatcher;
import com.devapp.devmain.util.UIValidation;
import com.devapp.devmain.util.ValidationHelper;
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


/**
 * After completing the weighing collection, user can do the quality check via SID or MCCID
 * So this is Activity is to support the Quality  check and convert the Incomplete record to Complete record
 * Created by u_pendra on 12/6/18.
 */

public class MCCCollectionActivity extends BaseActivity implements
        AdapterView.OnItemSelectedListener, View.OnClickListener {


    public String comingFrom;
    LinkedHashMap<String, String> spinnerHashMapData;
    String spinnerItem;
    boolean comingFromSID, comingFromNextSIDAlert;
    private String TAG = MCCCollectionActivity.class.getName();
    private Activity currentActivity = null;
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
    private AmcuConfig amcuConfig;
    private SessionManager mSession;
    private MaManager maManager;
    private PrinterManager mPrinterManager;
    private UIValidation mUIValidation;
    private Calendar mCalendar;

    private CollectionRecordDao mCollectionRecordDao;
    private SmartCCUtil mSmartCCUtil;
    private ValidationHelper mValidationHelper;
    private ReportEntity mReportEntity;

    private MilkAnalyserEntity milkAnalyserEntity;
    private ArrayList<ReportEntity> mReportEntities;
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
    protected void onResume() {
        super.onResume();
        spinnerQualityOfMilk.setVisibility(View.VISIBLE);
        tvQualityOfMilk.setVisibility(View.VISIBLE);
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


        btnAutoManual.setOnClickListener(MCCCollectionActivity.this);
        btnNext.setOnClickListener(this);
        btnReject.setOnClickListener(this);
        spinnerQualityOfMilk.setOnItemSelectedListener(this);

        etFat.addTextChangedListener(new RegexTextWatcher(etFat, AppConstants.Regex.NUMBER_DECIMAL));
        etSnf.addTextChangedListener(new RegexTextWatcher(etSnf, AppConstants.Regex.NUMBER_DECIMAL));
        etClr.addTextChangedListener(new RegexTextWatcher(etClr, AppConstants.Regex.NUMBER_DECIMAL));


        addingSpinnerData(-1);


    }

    private void initializeUtility() {
        amcuConfig = AmcuConfig.getInstance();
        mSession = new SessionManager(currentActivity);
        mPrinterManager = new PrinterManager(currentActivity);
        mUIValidation = UIValidation.getInstance();
        mCollectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));

        mSmartCCUtil = new SmartCCUtil(MCCCollectionActivity.this);

        mValidationHelper = new ValidationHelper();

        maManager = MAFactory.getMA(DeviceName.MILK_ANALYSER, MCCCollectionActivity.this);
        if (amcuConfig.getSmartCCFeature()) {
            disableRateAndAmountDisplay();
        }


    }

    private void disableViews() {
        etMilkweight.setEnabled(false);
        etRate.setEnabled(false);
        etAmount.setEnabled(false);
    }

    public void getDataFromIntent() {

        comingFrom = getIntent().getStringExtra("COMING_FROM");
        //  Bundle bundle = getIntent().getBundleExtra("BUNDLE");
        mReportEntities = (ArrayList<ReportEntity>) getIntent().getSerializableExtra("SELECTED_DATA");
        comingFromSID = getIntent().getBooleanExtra("COMING_FROM_SID", false);
        comingFromNextSIDAlert = getIntent().getBooleanExtra("COMING_FROM_NEXT_SID", false);


        if (comingFromSID || comingFromNextSIDAlert) {
            if (mReportEntities == null) {
                return;
            }
        }


    }

    /**
     * Report entity can come via SID flow or Center ID flow
     * From center ID flow this will be,
     * From SID this will update the selected report entity
     * From center ID flow this will update the last selected Report entity,
     */

    private void initializeReportEntity() {
        if (mReportEntities != null && mReportEntities.size() > 0) {
            mReportEntity = mReportEntities.get(mReportEntities.size() - 1);
            mReportEntity.initialize();
            mReportEntity.setOverallCollectionStatus(0, false);
            for (int size = 0; size < mReportEntities.size() - 1; size++) {
                ReportEntity reportEntity = mReportEntities.get(size);
                reportEntity.initialize();
                mReportEntity.setQuantity(mReportEntity.getQuantity()
                        + reportEntity.getQuantity());
                mReportEntity.setKgWeight(mReportEntity.getKgWeight() + reportEntity.getKgWeight());
                mReportEntity.setLtrsWeight(mReportEntity.getLtrsWeight() + reportEntity.getLtrsWeight());

            }
        }

    }

    private void stopMaReading() {
        if (maManager != null) {
            maManager.stopReading();
            maManager = null;
        }
    }


    private void startMaReading() {
        if (maManager != null) {
            maManager.startReading();
        }
    }

    /**
     * For customer milma, for FAT 0 allow user to select milk quality type
     *
     * @param fatValue
     */
    public void addingSpinnerData(double fatValue) {
        spinnerHashMapData = new LinkedHashMap<String, String>();
        if (fatValue == 0 && !amcuConfig.getSmartCCFeature()) {
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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnReject: {
                onSubmit();
                break;
            }
            case R.id.btnNext: {

                break;
            }
            default: {

            }
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        spinnerItem = parent.getItemAtPosition(position).toString();
        Iterator myVeryOwnIterator = spinnerHashMapData.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            if (spinnerHashMapData.get(key).toString().equals(spinnerItem)) {
                spinnerItem = "" + key;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void disableRateAndAmountDisplay() {
        TableRow trQuality = (TableRow) findViewById(R.id.trqualityOfMilk);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvFarmerId = (TextView) findViewById(R.id.tvFarmerId);
        trAmount.setVisibility(View.GONE);
        trMilkWeight.setVisibility(View.GONE);
        tvQualityOfMilk.setVisibility(View.GONE);
        tvName.setVisibility(View.GONE);
        trQuality.setVisibility(View.GONE);

        etAmount.setVisibility(View.GONE);
        etMilkweight.setVisibility(View.GONE);
        etRate.setVisibility(View.GONE);
        etFarmerName.setVisibility(View.GONE);
    }

    private void setUIDetails() {
        etFarmerId.setText(mReportEntity.getFarmerId());
        etFarmerName.setText(mReportEntity.getFarmerName());
        etSId.setText(String.valueOf(mReportEntity.getSampleNumber()));
        etMilkweight.setText(String.valueOf(mReportEntity.getDisplayQuantity()));
        etFarmerName.setEnabled(false);
        etFarmerId.setEnabled(false);
        etSId.setEnabled(false);

        if (!comingFromSID && !comingFromNextSIDAlert) {
            txtSID.setVisibility(View.GONE);
            etSId.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentActivity = MCCCollectionActivity.this;
        setContentView(R.layout.allusbdevice_landscape);
        initializeViews();
        registerBroadcastReceivers();
    }

    /**
     * On accept milk calculate rate
     * based on milk quality and set the
     * Rate field in report entity
     */
    private void onMilkAccept() {

        if (!btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
            mReportEntity = mSmartCCUtil.getRateFromRateChart(mReportEntity);
            displayRate();
        } else {
            etRate.setEnabled(false);
        }
        btnReject.setText("Print");

    }

    private void displayRate() {

        etRate.setText(String.valueOf(mReportEntity.getDisplayRate()));
        etProteinRate.setText(String.valueOf(mReportEntity.getIncentiveRate()));

    }

    private void displayAmount(double quantity) {

        mReportEntity.setAmount(mReportEntity.getDisplayRate() * quantity);
        //Calculating bonus amount

        mReportEntity.setBonus(mReportEntity.getBonusRate() * quantity);
        mReportEntity.setIncentiveAmount(mReportEntity.getIncentiveRate() * quantity);

        if (amcuConfig.getKeyAllowProteinValue()) {
            etAmount.setText(String.valueOf(mReportEntity.getTotalAmount()));
        } else {
            etAmount.setText(String.valueOf(Util.getAmount(MCCCollectionActivity.this,
                    mReportEntity.getTotalAmount(), mReportEntity.getBonus())));
        }
    }

    private void onMilkReject() {

        Toast.makeText(MCCCollectionActivity.this,
                "Milk rejected for this fat and snf!",
                Toast.LENGTH_LONG).show();
        btnReject.setText("Reject");
        btnReject.setEnabled(true);
        btnReject.requestFocus();
    }

    private void evaluateAcceptOrReject() {
        if (btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
            return;
        }

        if (mReportEntity.collectionStatus == 0 && amcuConfig.getAllowMaxLimitFromRateChart()) {
            mReportEntity = mSmartCCUtil.setMaxFatAndSnf(mReportEntity);
        }
        if (mReportEntity.getFat() > Util.MAX_FAT_LIMIT ||
                mReportEntity.getSnf() > Util.MAX_SNF_LIMIT
                || mReportEntity.getFat() < Util.MIN_FAT_LIMIT
                || mReportEntity.getSnf() < Util.MIN_SNF_LIMIT) {
            Toast.makeText(MCCCollectionActivity.this, "Invalid milk analyzer data", Toast.LENGTH_SHORT).show();
            onFinish();

        } else {
            mReportEntity = mSmartCCUtil.setAcceptOrRejectStatus(mReportEntity);

        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromIntent();

        initializeUtility();
        initializeReportEntity();
        setUIDetails();
        setHeader();
        setStatusLayout();
        setQualityManual(amcuConfig.getMaManual());
        registerMaListener();
        startMaReading();
        disableViews();
        setQuantity();

        if (mReportEntity.recordCommited == Util.REPORT_NOT_COMMITED
                && mReportEntity.recordStatus.equalsIgnoreCase(Util.RECORD_STATUS_COMPLETE)) {
            displayQualityItem();
            displayRate();
            setParameters(mReportEntity.getQuantity());
            setQualityManual(false);
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.SAVED_UNCOMMITTED_RECORD, true);
            afterGettingMaData();
        }

        setViewForSnfAndClr();
    }


    public void alertReject() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MCCCollectionActivity.this);

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

    public void saveIncompleteRecord() {

        if (milkAnalyserEntity == null) {
            return;
        }

        if (mReportEntity.isQualityDone() && !milkAnalyserEntity.isValid()) {
            Util.displayErrorToast("Invalid data, please retry again!", MCCCollectionActivity.this);
            onFinish();
            return;
        }

        if (mReportEntity.isUncommittedRecordSaved()) {
            return;
        }
        mReportEntity.recordCommited = Util.REPORT_NOT_COMMITED;
        mReportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;
        if (amcuConfig.getSmartCCFeature()) {
            mReportEntity.milkQuality = AppConstants.MILK_QUALITY_GOOD;
        } else {
            mReportEntity.milkQuality = spinnerItem;
        }
        try {
            long columnId = mCollectionRecordDao.saveOrUpdate(mReportEntity);
            mReportEntity.setPrimaryKeyId(columnId);
            mReportEntity.setOverallCollectionStatus(SmartCCConstants.SAVED_UNCOMMITTED_RECORD, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * If user selected SID option for collection
     * User can go via next SID option for up coming sequence numbers
     * Once all sequence number over Activity will finish here
     */
    private void checkForNextSequenceNumber() {
        ReportEntity nextSample = mCollectionRecordDao.findNextSampleBySampleNumber(
                Integer.parseInt(etSId.getText().toString().trim()),
                mSmartCCUtil.getReportFormatDate(),
                Util.getCurrentShift());

        if (nextSample == null) {
            Toast.makeText(MCCCollectionActivity.this, "No more data available.", Toast.LENGTH_SHORT).show();
            gotoFarmerScannerActivity();
        } else if (comingFromSID || comingFromNextSIDAlert) {
            etSId.setText(String.valueOf(mReportEntity.sampleNumber));
            if (mReportEntity.sampleNumber != 0)
                gotoNextSampleIdAlert();
            else
                gotoFarmerScannerActivity();
        } else {
            onFinish();
        }

    }

    private void addToDatabase() throws SQLException {
        Util.setCollectionStartedWithMilkType(mReportEntity.milkType, MCCCollectionActivity.this);
        mReportEntity.setMilkType(new CollectionHelper(MCCCollectionActivity.this).
                getMilkTypeFromConfiguration(mReportEntity));
        mCollectionRecordDao.saveOrUpdate(mReportEntity);
        updateSelectedReportEntities();
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

        RduManager rduManager = RduFactory.getRdu(amcuConfig.getRDU(), MCCCollectionActivity.this);
        if (rduManager != null) {
            rduManager.openConnection();
            rduManager.displayReport(mReportEntity, amcuConfig.getEnableIncentiveRDU());
            rduManager.closePort();
        } else {
            Toast.makeText(MCCCollectionActivity.this,
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

    public String receiptFormat() {

        FormatPrintRecords formatPrintRecord = new FormatPrintRecords(MCCCollectionActivity.this);
        String printData = formatPrintRecord.receiptFormat(MCCCollectionActivity.this, mReportEntity);
        return printData;

    }


    /**
     * Report entities from the intent will be update with
     * the new quality parameters and status will remain INCOMPLETE
     * App will try send this report entity once again with quality params
     */
    private void updateSelectedReportEntities() {
        if (mReportEntities == null || mReportEntity == null) {
            return;
        }

        for (ReportEntity reportEntity : mReportEntities) {

            if (reportEntity.getColumnId() == mReportEntity.getColumnId()) {
                return;
            }

            reportEntity.setFat(mReportEntity.getFat());
            reportEntity.setFatKg(mReportEntity.getFatKg());
            reportEntity.setSnf(mReportEntity.getSnf());
            reportEntity.setSnfKg(mReportEntity.getSnfKg());
            reportEntity.setClr(mReportEntity.getClr());
            reportEntity.resetSentMarkers();
            reportEntity.setRecordStatus(Util.RECORD_STATUS_INCOMPLETE);
            reportEntity.setRecordCommited(Util.REPORT_COMMITED);
            mCollectionRecordDao.update(reportEntity);


        }
    }

    /**
     * Alert for to continue with Next SID or Skip and do collection via
     * normal flow
     */

    public void gotoNextSampleIdAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MCCCollectionActivity.this);
        LayoutInflater inflater = MCCCollectionActivity.this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sid_dialogbox, null);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReportEntity nextSample = mCollectionRecordDao.findNextSampleBySampleNumber(
                        mReportEntity.getSampleNumber(),
                        mSmartCCUtil.getReportFormatDate(),
                        Util.getCurrentShift());
                if (nextSample == null) {
                    dialog.dismiss();
                    Toast.makeText(MCCCollectionActivity.this, "No more data available.", Toast.LENGTH_SHORT).show();
                    gotoFarmerScannerActivity();
                } else {
                    mReportEntities = new ArrayList<>();
                    mReportEntities.add(nextSample);
                    Intent intent = new Intent(getApplicationContext(), MCCCollectionActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    intent.putExtra("COMING_FROM_NEXT_SID", true);
                    intent.putExtra("ALERT_CID", nextSample.getFarmerId());
                    intent.putExtra("ALERT_CID_NAME", nextSample.getFarmerName());
                    intent.putExtra("COMING_FROM", "UMCA");
                    intent.putExtra("SELECTED_CURSORID", String.valueOf(nextSample.getColumnId()));
                    intent.putExtra("SELECTED_DATA", mReportEntities);

                    String selectMilkType = nextSample.getMilkType();
                    mSession.setMilkType(selectMilkType);
                    mSmartCCUtil.setRateChart();

                    startActivity(intent);
                    dialog.dismiss();
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

    private void displayQualityItem() {
        etFat.setText(String.valueOf(mReportEntity.getDisplayFat()));
        etSnf.setText(String.valueOf(mReportEntity.getDisplaySnf()));
        etClr.setText(String.valueOf(mReportEntity.getDisplayClr()));
        etProtein.setText(String.valueOf(mReportEntity.getDisplayProtein()));

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

    public boolean checkQualityValidation(MilkAnalyserEntity milkAnalyserEntity) {
        boolean isValidQuality = mValidationHelper.isValidQuality(milkAnalyserEntity,
                MCCCollectionActivity.this);
        return isValidQuality;
    }

    public void onFinish() {
        startActivity(new Intent(MCCCollectionActivity.this, FarmerScannerActivity.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
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
//            onEnterQualityView();
            btnReject.setEnabled(true);
            etFat.requestFocus();
        }
    }

    /**
     * To register milk analyzer and set Report entity on New data
     * with quality parameters
     */

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
                                if (!mSmartCCUtil.validateQualityParameters(milkAnalyserEntity)) {
                                    Util.displayErrorToast(getResources().getString(R.string.invalid_ma_data), MCCCollectionActivity.this);
                                    onFinish();
                                    return;
                                }
                                mReportEntity.setQualityParameters(milkAnalyserEntity);
                                displayQualityItem();
                                if (!etFat.isEnabled()) {
                                    afterGettingMaData();
                                } else {
                                    btnReject.setText("Submit quality");
                                    btnReject.requestFocus();
                                }


                            } else {
                                Util.displayErrorToast(getResources().getString(R.string.invalid_ma_data),
                                        MCCCollectionActivity.this);
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
                        }
                    });
                }
            });
    }


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

    public void setHeader() {
        tvheader.setText(mSession.getSocietyName());
    }


    private boolean validateQualityFields() {
        boolean returnValue = true;
        if (TextUtils.isEmpty(etFat.getText().toString().trim())) {

            Util.displayErrorToast("Please enter valid Fat value!", MCCCollectionActivity.this);
            returnValue = false;
        } else if (TextUtils.isEmpty(etSnf.getText().toString().trim()) && etSnf.getVisibility() == View.VISIBLE) {

            Util.displayErrorToast("Please enter valid SNF value!", MCCCollectionActivity.this);
            returnValue = false;
        } else if (TextUtils.isEmpty(etClr.getText().toString().trim())
                && etClr.getVisibility() == View.VISIBLE) {

            Util.displayErrorToast("Please enter valid CLR value!", MCCCollectionActivity.this);
            returnValue = false;
        } else if (btnReject.getText().toString().equalsIgnoreCase("Enter rate") && mReportEntity.rate <= 0) {
            Util.displayErrorToast("Please enter valid Rate!", MCCCollectionActivity.this);
            etRate.setEnabled(true);
            etRate.requestFocus();
            returnValue = false;
        }
        return returnValue;

    }


    private void setParameters(double record) {

        mReportEntity.setQuantity(record);
        etMilkweight.setText(String.valueOf(mReportEntity.getDisplayQuantity()));
        displayAmount(mReportEntity.getRateCalculationQuanity());
        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUANTITY_DONE, true);
        mReportEntity.setOverallCollectionStatus(SmartCCConstants.QUALITY_DONE, true);
        milkAnalyserEntity = mReportEntity.getQualityParameters();

    }

    @Override
    public void onBackPressed() {
        if (amcuConfig.getKeyEscapeEnableCollection()
                && !mSession.getIsChillingCenter()) {
            DatabaseHandler.getDatabaseInstance().deleteFromDb();
            onFinish();
        }
    }

    /**
     * this is the trigger after getting the MA data either by AUTO OR
     * Manual mode, this performs  number of  actions
     * Check the quality status of milk
     * Perform action based of milk status
     * ave the Uncommitted record in database
     */
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
        if (mReportEntity.getStatus().equalsIgnoreCase("Reject")
                && amcuConfig.getEditableRate() && !btnReject.getText().toString().equalsIgnoreCase("Enter rate")) {
            alertReject();
        } else if (mReportEntity.getStatus().equalsIgnoreCase("Reject")) {
            onMilkReject();
            btnReject.requestFocus();
        } else {
            onMilkAccept();
        }
        displayAmount(mReportEntity.getRateCalculationQuanity());
        saveIncompleteRecord();

    }

    private void onSubmit() {

        if (!validateQualityFields()) {

        } else if ((etFat.isEnabled() || mReportEntity.qualityMode.equalsIgnoreCase("Manual"))
                && !mReportEntity.isQualityDone() && (milkAnalyserEntity != null && milkAnalyserEntity.isValid())) {
            btnReject.setText("Submit");
            afterGettingMaData();
        } else if (((!mReportEntity.isQualityDone()
                && milkAnalyserEntity.isValid()
                && mReportEntity.rateMode.equalsIgnoreCase(SmartCCConstants.AUTO)) ||
                (btnReject.getText().toString().equalsIgnoreCase("Enter rate"))) && validateQualityFields()) {
            afterGettingMaData();
            setQualityManual(false);
        } else if ((mReportEntity.isQualityDone() && milkAnalyserEntity.isValid()) && validateQualityFields()) {
            mReportEntity.resetSentMarkers();
            mReportEntity.setRecordCommited(Util.REPORT_COMMITED);
            mReportEntity.setRecordStatus(Util.RECORD_STATUS_COMPLETE);
            try {
                addToDatabase();
                mSmartCCUtil.setCollectionStartData(mReportEntity);
            } catch (SQLException e) {
                e.printStackTrace();
                onDBError();
                Toast.makeText(MCCCollectionActivity.this, "Tab restart is required.",
                        Toast.LENGTH_LONG).show();
                Util.restartTab(MCCCollectionActivity.this);
            }
            printAndDisplay();
            try {
                writeOnSDCard();
            } catch (IOException e) {
                e.printStackTrace();
                Util.restartTab(MCCCollectionActivity.this);
            }
            checkForNextSequenceNumber();
        }
    }

    private void addQualityWatcher(View v) {
        EditText etInput = (EditText) v;

        if (!mReportEntity.qualityMode.equalsIgnoreCase(SmartCCConstants.MANUAL)) {
            mReportEntity.qualityMode = SmartCCConstants.MANUAL;
            stopMaReading();
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
            addingSpinnerData(milkAnalyserEntity.fat);
        }
        if (etInput == etClr) {
            milkAnalyserEntity.clr = mValidationHelper.getDoubleFromString(etClr.getText().toString().trim(), -1);
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

    private void registerBroadcastReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmartCCConstants.MA_CONNECTED);
        intentFilter.addAction(SmartCCConstants.WS_CONNECTED);
        intentFilter.addAction(SmartCCConstants.RDU_CONNECTED);
        intentFilter.addAction(SmartCCConstants.PRINTER_CONNECTED);
        LocalBroadcastManager.getInstance(MCCCollectionActivity.this).registerReceiver(receiver, intentFilter);
        LocalBroadcastManager.getInstance(MCCCollectionActivity.this).registerReceiver(pingStatusReceiver,
                new IntentFilter(SmartCCConstants.PING_STATUS));
    }

    private void displayDeviceStatus() {

        cbMa.setChecked(maPing);
        cbWs.setChecked(wsPing);
        cbRdu.setChecked(rduPing);
        cbPrinter.setChecked(printerPing);
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
    protected void onStop() {
        stopAllConnections();
        super.onStop();
    }

    private void stopAllConnections() {
        stopMaReading();
        storeLastPingValues();
    }

    private void storeLastPingValues() {
        amcuConfig.setMaPingValue(maPing);
        amcuConfig.setWsPingValue(wsPing);
        amcuConfig.setRduPingValue(rduPing);
        amcuConfig.setPrinterPingValue(printerPing);
    }
}
