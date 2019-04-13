package com.devapp.devmain.milkline;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.eevoskos.robotoviews.widget.RobotoButton;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.DispatchRecordDao;
import com.devapp.devmain.devicemanager.PrinterManager;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.MilkAnalyser;
import com.devapp.devmain.postentities.QualityParamsPost;
import com.devapp.devmain.postentities.QualityReadingData;
import com.devapp.devmain.postentities.QuantityParamspost;
import com.devapp.devmain.postentities.WeighingScaleData;
import com.devapp.devmain.postentities.dispatchentities.DispatchPostEntity;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.FormatPrintRecords;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.CursorEndOnFocusChangeListener;
import com.devapp.devmain.util.FocusForwardKeyListener;
import com.devapp.devmain.util.InputFilterMinMax;
import com.devapp.devmain.util.RegexInputFilter;
import com.devapp.devmain.util.RegexTextWatcher;
import com.devapp.devmain.util.logger.Log;
import com.devapp.devmain.ws.WsFactory;
import com.devapp.devmain.ws.WsManager;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.io.IOException;
import java.text.DecimalFormat;

public class EnterDispatchDetailsActivity extends Activity implements View.OnKeyListener {

    private final String LTR = "LTR";
    private final String KG = "KG";
    private Context context = this;
    private EditText etFat, etSnf, etCollectedQty,
            etSoldQty, etAvailableQty, etMeasuredQty, etCans;
    private MaManager maManager;
    private WsManager wsManager;
    private SmartCCUtil smartCCUtil;
    private String currentDate;
    private PrinterManager printerManager;
    private DispatchPostEntity dispatchPostEntity;
    private SessionManager sessionManager;
    private AmcuConfig amcuConfig;
    private DatabaseHandler dbHandler;
    private DispatchRecordDao dispatchRecordDao;
    private String supervisorId;
    private DecimalFormat decimalFormatWeight, decimalFormat2Digit;
    private Spinner spUnit, spMilkType;
    private DecimalFormat decimalFormatFS, decimalFormatQty;
    private AlertDialog alertDialog;
    private boolean fatSnfAddedManually, weightAddedManually;

    private Button btnSubmit, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_dispatch_details);
        initializeView();
        initializeUtility();

        if (sessionManager.getUserId() != null) {
            supervisorId = sessionManager.getUserId();
            Log.v("Dispatch", "supervisor id:" + supervisorId);
        }
        if (maManager != null)
            maManager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            etFat.setText(String.valueOf(maEntity.fat));
                            etSnf.setText(String.valueOf(maEntity.snf));
//                            etFat.setEnabled(false);
//                            etSnf.setEnabled(false);
                            etCans.requestFocus();
                            stopMaReading();
                            openWsConnection();
                        }
                    });
                }

                @Override
                public void onOtherMessage(String message) {

                }
            });
        if (wsManager != null)
            wsManager.setOnNewDataListener(new WsManager.OnNewDataListener() {
                @Override
                public void onNewData(final double data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (data >= 0) {
                                double weight = 0;
                                String val = "";
                                try {
                                    weight = data;
                                    weight = weight / amcuConfig.getWeighingDivisionFactor();
                                    val = decimalFormatWeight.format(weight);
                                } catch (NumberFormatException e) {
                                    e.printStackTrace();
                                }
                                if (weight != 0 && weight > 0) {
                                    etMeasuredQty.setText(val);
                                }
                            }
                        }
                    });
                }
            });
        if (dbHandler != null) {
            currentDate = smartCCUtil.getReportFormatDate();
            double collectedQty = dbHandler.getTotalMilkCollectedQty(Util.getCurrentShift(),
                    currentDate);
            double soldQty = dbHandler.getTotalMilkSoldQty(Util.getCurrentShift(),
                    currentDate);
            double availableQty = collectedQty - soldQty;
            if (availableQty == 0) {
                finish();
                Util.displayErrorToast("Milk not available to dispatch", getApplicationContext());
            }
            etCollectedQty.setText(decimalFormatQty.format(collectedQty));
            etSoldQty.setText(decimalFormatQty.format(soldQty));
            etAvailableQty.setText(decimalFormatQty.format(availableQty));
        } else {
            Util.displayErrorToast("DB handler null", context);
        }
        etFat.setOnKeyListener(new FocusForwardKeyListener(etSnf));
        etSnf.setOnKeyListener(new FocusForwardKeyListener(etMeasuredQty));
        etMeasuredQty.setOnKeyListener(new FocusForwardKeyListener(btnSubmit));
        etCans.setOnKeyListener(new FocusForwardKeyListener(btnSubmit));
        etCans.setFilters(new InputFilter[]{new RegexInputFilter(AppConstants.Regex.NUMBER), new InputFilterMinMax("1", "999")});
        etFat.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etSnf.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etCans.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etMeasuredQty.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        addTextWatchers();
        checkAutoOrManual();
        startMaReading();
    }

    private void initializeView() {
        etFat = (EditText) findViewById(R.id.et_fat);
        etSnf = (EditText) findViewById(R.id.et_snf);
        etCollectedQty = (EditText) findViewById(R.id.et_collected_qty);
        etSoldQty = (EditText) findViewById(R.id.et_sold_qty);
        etAvailableQty = (EditText) findViewById(R.id.et_available_qty);
        etMeasuredQty = (EditText) findViewById(R.id.et_measured_qty);
        etCans = (EditText) findViewById(R.id.et_cans);
        spUnit = (Spinner) findViewById(R.id.spQuantityUnit);
        spMilkType = (Spinner) findViewById(R.id.sp_milktype);
        btnSubmit = (RobotoButton) findViewById(R.id.btn_submit);
        btnBack = (RobotoButton) findViewById(R.id.btnBack);


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submit();

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFinish();
            }
        });

    }


    private void initializeUtility() {
        maManager = MAFactory.getMA(DeviceName.MA1, context);
        wsManager = WsFactory.getWs(DeviceName.WS, context);
        printerManager = new PrinterManager(context);
        dbHandler = DatabaseHandler.getDatabaseInstance();
        sessionManager = new SessionManager(this);
        amcuConfig = AmcuConfig.getInstance();
        smartCCUtil = new SmartCCUtil(context);
        decimalFormatFS = new DecimalFormat("#0.0");
        decimalFormatQty = new DecimalFormat("#0.00");
        dispatchRecordDao = (DispatchRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_DISPATCH);
        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        decimalFormat2Digit = chooseDecimalFormat.get2DigitFormatWeight();
        decimalFormatWeight = chooseDecimalFormat.getWeightDecimalFormat();
        dispatchPostEntity = new DispatchPostEntity();
    }


    private void addTextWatchers() {
        etFat.addTextChangedListener(new TextWatcher() {
            String original = "";
            int startIndex;
            boolean isCorrecting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isCorrecting) {
                    startIndex = start;
                    isCorrecting = false;
                }
                original = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fatSnfAddedManually = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches(AppConstants.Regex.NUMBER_DECIMAL)) {
                    try {
                        isCorrecting = true;
                        etFat.setText(original);
                        etFat.setSelection(startIndex);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        etSnf.addTextChangedListener(new TextWatcher() {
            String original = "";
            int startIndex;
            boolean isCorrecting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isCorrecting) {
                    startIndex = start;
                    isCorrecting = false;
                }
                original = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                weightAddedManually = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().matches(AppConstants.Regex.NUMBER_DECIMAL)) {
                    try {
                        isCorrecting = true;
                        etSnf.setText(original);
                        etSnf.setSelection(startIndex);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        etMeasuredQty.addTextChangedListener(new RegexTextWatcher(etMeasuredQty, AppConstants.Regex.NUMBER_DECIMAL));
    }

    private void checkAutoOrManual() {
        if (amcuConfig.getDisableManualForDispatchValue()) {
            etFat.setEnabled(false);
            etSnf.setEnabled(false);
            etCans.requestFocus();
            etMeasuredQty.setEnabled(false);
        } else {
            etFat.setEnabled(true);
            etSnf.setEnabled(true);
            etMeasuredQty.setEnabled(true);
        }
    }

    private void startMaReading() {
        if (maManager != null)
            maManager.startReading();
    }

    private void openWsConnection() {
        if (wsManager != null) {
            wsManager.openConnection();
        }
    }

    private void stopMaReading() {
        if (maManager != null)
            maManager.stopReading();
    }


    public void closeWSConnection() {
        if (wsManager != null)
            wsManager.closeConnection();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopMaReading();
        closeWSConnection();
    }

    public void submit() {
        if (isDataValid()) {
            double qty = Double.parseDouble(etMeasuredQty.getText().toString());
            if (qty > 10000 || qty < amcuConfig.getKeyMinValidWeight())
                showAlertForManualWeight();
            else {
                saveDataAndPrint();
            }
        }
    }


    public void onFinish() {


        finish();

    }

    private void saveDataAndPrint() {
        createDispatchEntity();
        printReceipt();
        saveInSDCard();
        finish();
    }

    private boolean isDataValid() {
        if (TextUtils.isEmpty(etMeasuredQty.getText()) || TextUtils.isEmpty(etCans.getText())) {
            Util.displayErrorToast("Please enter all the fields", context);
            return false;
        }
        if (!TextUtils.isEmpty(etFat.getText()) && !TextUtils.isEmpty(etSnf.getText())) {
            double fat = Double.parseDouble(etFat.getText().toString());
            double snf = Double.parseDouble(etSnf.getText().toString());
            if (!isFatSnfValid(fat, snf)) {
                Util.displayErrorToast("Invalid fat/snf values", context);
                return false;
            }
        }
        return true;
    }

    private boolean isFatSnfValid(double fat, double snf) {
        if (sessionManager.getMilkType().equalsIgnoreCase("Cow")) {
            if (fat > amcuConfig.getKeyMaxFatRejectCow() || snf > amcuConfig.getKeyMaxSnfRejectCow())
                return false;
        } else if (sessionManager.getMilkType().equalsIgnoreCase("Buffalo")) {
            if (fat > amcuConfig.getKeyMaxFatRejectBuff() || snf > amcuConfig.getKeyMaxSnfRejectBuff())
                return false;
        } else if (sessionManager.getMilkType().equalsIgnoreCase("Mixed"))
            if (fat > amcuConfig.getKeyMaxFatRejectBuff() || snf > amcuConfig.getKeyMaxSnfRejectBuff())
                return false;
        return true;
    }


    private void createDispatchEntity() {
        dispatchPostEntity.supervisorId = supervisorId;
//        dispatchPostEntity.timeInMillis = System.currentTimeMillis();
        dispatchPostEntity.time = Util.getTodayDateAndTime(6, context, true);
//        dispatchPostEntity.date = SmartCCUtil.getDateInPostFormat();
        dispatchPostEntity.date = currentDate;
        dispatchPostEntity.timeInMillis = smartCCUtil.getDateInMilliSeconds(currentDate + " " +
                "" + dispatchPostEntity.time);
        dispatchPostEntity.shift = SmartCCUtil.getShiftInPostFormat(context);
        dispatchPostEntity.milkType = spMilkType.getSelectedItem().toString();
        dispatchPostEntity.numberOfCans = Integer.parseInt(etCans.getText().toString());
        QualityParamsPost qualityParams = new QualityParamsPost();
        qualityParams.mode = "Manual";
        MilkAnalyser milkAnalyzer = new MilkAnalyser();
        QualityReadingData reading = new QualityReadingData();
        if (!TextUtils.isEmpty(etFat.getText())) {
            reading.fat = Double.valueOf(decimalFormatQty.format(Double.parseDouble(etFat.getText().toString())));
        }
        if (!TextUtils.isEmpty(etSnf.getText()))
            reading.snf = Double.valueOf(decimalFormatQty.format(Double.parseDouble(etSnf.getText().toString())));

        milkAnalyzer.qualityReadingData = reading;
        qualityParams.milkAnalyser = milkAnalyzer;

        QuantityParamspost quantityParams = new QuantityParamspost();
        quantityParams.mode = "Manual";
        quantityParams.collectedQuantity = Double.valueOf(decimalFormatQty.format(Double.valueOf(etCollectedQty.getText().toString())));
        quantityParams.soldQuantity = Double.valueOf(decimalFormatQty.format(Double.valueOf(etSoldQty.getText().toString())));
        quantityParams.availableQuantity = Double.valueOf(decimalFormatQty.format(Double.valueOf(etAvailableQty.getText().toString())));
        WeighingScaleData weighingScale = new WeighingScaleData();
        weighingScale.measuredValue = Double.valueOf(decimalFormatQty.format(Double.valueOf(etMeasuredQty.getText().toString())));
        if (spUnit.getSelectedItem().toString().equalsIgnoreCase(LTR)) {
            weighingScale.inLtr = Double.valueOf(etMeasuredQty.getText().toString());
            weighingScale.inKg = Double.parseDouble(decimalFormatWeight.format(weighingScale.inLtr *
                    Double.parseDouble(AmcuConfig.getInstance().getConversionFactor())));
        } else {
            weighingScale.inKg = Double.valueOf(etMeasuredQty.getText().toString());
            weighingScale.inLtr = Double.parseDouble(decimalFormat2Digit.format(weighingScale.inKg /
                    Double.parseDouble(AmcuConfig.getInstance().getConversionFactor())));

        }
        quantityParams.weighingScaleData = weighingScale;
        dispatchPostEntity.qualityParams = qualityParams;
        dispatchPostEntity.quantityParams = quantityParams;
        dispatchPostEntity.sentStatus = CollectionConstants.UNSENT;

        long columnId = dispatchRecordDao.saveOrUpdate(dispatchPostEntity);
        dispatchPostEntity.setPrimaryKeyId(columnId);
    }

    private void printReceipt() {
        FormatPrintRecords formatPrintRecord = new FormatPrintRecords(context);
        String printData = formatPrintRecord.dispatchReceiptFormat(dispatchPostEntity);
        printerManager.print(printData, PrinterManager.dispatchReceipt, null, null, null);
    }

    public void showAlertForManualWeight() {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        alertDialog = builder.create();
        builder.setCancelable(false);
        builder.setTitle("Exceeding quantity alert!");
        builder.setMessage("Entered quantity should be more than min limit " + amcuConfig.getKeyMinValidWeight() +
                " and less than max limit 10000"
                + ", press Cancel to enter correct quantity or press OK to proceed.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveDataAndPrint();
                alertDialog.cancel();

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    private void saveInSDCard() {
        {

            try {
                String stringReport;
                String date = dispatchPostEntity.date;
                String shiftReport = dispatchPostEntity.shift;

                SessionManager sessionManager = new SessionManager(context);

                stringReport = sessionManager.getCollectionID() + ","
                        + dispatchPostEntity.supervisorId + ","
                        + sessionManager.getFarmerBarcode() + "," +
                        dispatchPostEntity.date + ","
                        + dispatchPostEntity.shift + ","
                        + dispatchPostEntity.milkType + "," +
                        dispatchPostEntity.qualityParams.milkAnalyser.qualityReadingData.fat + ","
                        + dispatchPostEntity.qualityParams.milkAnalyser.qualityReadingData.snf + ","
                        + dispatchPostEntity.quantityParams.weighingScaleData.measuredValue + ","
                        + dispatchPostEntity.quantityParams.weighingScaleData.inLtr + ","
                        + dispatchPostEntity.quantityParams.weighingScaleData.inKg + ","
                        + sessionManager.getUserId() + "," + "\n";
                Util.generateNoteOnSD(date + shiftReport + "_dispatchReports",
                        stringReport, context, "smartAmcuReports");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_PAGE_DOWN:
                btnSubmit.requestFocus();
                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        return false;
    }
}
