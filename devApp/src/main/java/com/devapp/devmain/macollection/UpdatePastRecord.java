package com.devapp.devmain.macollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.EditRecordDao;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.WeightLimit;
import com.devapp.devmain.httptasks.PostCollectionRecordsService;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.CursorEndOnFocusChangeListener;
import com.devapp.devmain.util.FocusForwardKeyListener;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.QuantityEntity;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import java.io.IOException;
import java.text.DecimalFormat;

public class UpdatePastRecord extends Activity implements WeightLimit {

    private static UpdatePastRecord mUpdatePastRecord;
    EditText etCId, etFat, etSnf, etClr, etQuantity, etRate, etAmount;
    Button btnUpdate, btnCancel;
    DatabaseHandler databaseHandler;
    SmartCCUtil smartCCUtil;

    String iD;
    CenterEntity centerEntity;
    FarmerEntity farmerEntity;
    TextView clrText, snfText;
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    double changedRate = 0;
    DatabaseManager databaseManager;
    AmcuConfig amcuConfig;
    SessionManager sessionManager;
    DecimalFormat decimalFormatRate, decimalFormatAmount, decimalFormatQuantity;
    ValidationHelper validationHelper;
    boolean isRateManual;
    CollectionHelper collectionHelper;
    DecimalFormat decimalFormatClr;
    DecimalFormat decimalFormatFat;
    DecimalFormat decimalFormatSNF;
    DecimalFormat decimalFormatRateFat;
    DecimalFormat decimalFormatRateSNF;
    DecimalFormat decimalFormatRateCLR;
    private ReportEntity reportEntity, reportEntityUpdateVal;
    private boolean updateValue = false;
    private double Rate = 0, changedAmount;

    public TextWatcher commonWatcher = new TextWatcher() {
        String original = "";
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            original = s.toString();
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (etQuantity.getText().hashCode() == s.hashCode()) {
                if (!s.toString().matches("[0-9]*[.]?[0-9]*")) {
                    etQuantity.setText(original);
                    return;
                }
                setAmount();
            } else if (etRate.getText().hashCode() == s.hashCode()) {
//                if (!s.toString().matches("[0-9]*[.]?[0-9]*")) {
//                    etRate.setText(original);
//                    return;
//                }
//                isRateManual = true;
                setAmount();
            } else if (etFat.getText().hashCode() == s.hashCode()) {
                if (!s.toString().matches("[0-9]*[.]?[0-9]*")) {
                    etFat.setText(original);
                    return;
                } else {
                    setRate(etFat);
                }

            } else if (etSnf.getText().hashCode() == s.hashCode()) {
                if (etSnf.getVisibility() == View.VISIBLE) {
                    if (!s.toString().matches("[0-9]*[.]?[0-9]*")) {
                        etSnf.setText(original);
                        return;
                    }

                    setRate(etSnf);
                }
            } else if (etClr.getText().hashCode() == s.hashCode()) {
                if (etClr.getVisibility() == View.VISIBLE) {
                    if (!s.toString().matches("[0-9]*[.]?[0-9]*")) {
                        etClr.setText(original);
                        return;
                    }
                    setRate(etClr);
                }
            }
        }
    };
    private EditRecordDao editRecordDao;
    private CollectionRecordDao collectionRecordDao;
    private FarmerDao farmerDao;

    public static UpdatePastRecord getInstance() {
        return mUpdatePastRecord;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_past_record);

        mUpdatePastRecord = UpdatePastRecord.this;
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        databaseManager = new DatabaseManager(this);
        smartCCUtil = new SmartCCUtil(UpdatePastRecord.this);
        reportEntity = new ReportEntity();
        reportEntityUpdateVal = new ReportEntity();
        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(UpdatePastRecord.this);
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);

        editRecordDao =
                (EditRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_EDITED);
        collectionRecordDao =
                (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);

        iD = getIntent().getStringExtra("ID");

        clrText = (TextView) findViewById(R.id.clrText);
        snfText = (TextView) findViewById(R.id.snfText);

        etCId = (EditText) findViewById(R.id.etCId);
        etFat = (EditText) findViewById(R.id.etFat);
        etSnf = (EditText) findViewById(R.id.etSnf);
        etClr = (EditText) findViewById(R.id.etClr);


        etQuantity = (EditText) findViewById(R.id.etQuantity);
        etRate = (EditText) findViewById(R.id.etRate);
        etAmount = (EditText) findViewById(R.id.etAmount);
        btnUpdate = (Button) findViewById(R.id.btnUpdate);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        decimalFormatFat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        decimalFormatSNF = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        decimalFormatClr = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);

        decimalFormatRateFat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.FAT);
        decimalFormatRateSNF = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.SNF);
        decimalFormatRateCLR = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.CLR);

        setDecimalFormat();
        reportEntity = (ReportEntity) collectionRecordDao.findById(Long.valueOf(iD));
        //Initialize updated value
        setCurrentRecord(reportEntity);
        setLayoutData(reportEntity);
        markEntityAsOld(reportEntity);

        etCId.setOnKeyListener(new FocusForwardKeyListener(etFat));
        etCId.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etFat.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        if ((amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") &&
                !reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) ||
                (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") &&
                        reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC))) {
            etFat.setOnKeyListener(new FocusForwardKeyListener(etClr));
            etClr.setOnKeyListener(new FocusForwardKeyListener(etQuantity));
            etClr.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
            etSnf.setVisibility(View.GONE);
            snfText.setVisibility(View.GONE);
        } else {
            etFat.setOnKeyListener(new FocusForwardKeyListener(etSnf));
            etSnf.setOnKeyListener(new FocusForwardKeyListener(etQuantity));
            etSnf.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
            etClr.setVisibility(View.GONE);
            clrText.setVisibility(View.GONE);
        }
        etQuantity.setOnKeyListener(new FocusForwardKeyListener(btnUpdate));
        //  etRate.setOnKeyListener(new FocusForwardKeyListener(btnUpdate));
        etQuantity.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());
        etRate.setOnFocusChangeListener(new CursorEndOnFocusChangeListener());

        etRate.addTextChangedListener(commonWatcher);
        etQuantity.addTextChangedListener(commonWatcher);
        etFat.addTextChangedListener(commonWatcher);
        etClr.addTextChangedListener(commonWatcher);
        etSnf.addTextChangedListener(commonWatcher);
        etRate.setEnabled(false);
        etRate.setFocusable(false);
    }


    public void setDecimalFormat() {

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        decimalFormatAmount = chooseDecimalFormat.getAmountDecimalFormat();
        decimalFormatRate = chooseDecimalFormat.getRateDecimalFormat();
        decimalFormatQuantity = chooseDecimalFormat.getWeightDecimalFormat();
    }


    @Override
    protected void onStart() {
        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        decimalFormatFS = chooseDecimalFormat.getFatAndSnfFormat();
        validationHelper = new ValidationHelper();
        collectionHelper = new CollectionHelper(UpdatePastRecord.this);


        super.onStart();


    }

    public void onClickUpdate(View view) {
        boolean state = false;
        String errorMsg = "Please enter valid data";
        double dFat = 0, dSnf = 0;

        if (etFat.getText().toString().trim().length() > 0
                && etSnf.getText().toString().trim().length() > 0
                && etCId.getText().toString().trim().length() > 0
                && etQuantity.getText().toString().trim().length() > 0) {
            try {
                dFat = Double.parseDouble(etFat.getText().toString().trim());
                dSnf = Double.parseDouble(etSnf.getText().toString().trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }


            if (etCId.getText().toString().trim().length() > 0 &&
                    etQuantity.getText().toString().trim().length() > 0 &&
                    etRate.getText().toString().trim().length() > 0 &&
                    etClr.getText().toString().trim().length() > 0 && validationHelper.isValidFatAndSnf(dFat, dSnf,
                    getApplicationContext())) {


                if (reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
                    // get data from chilling center
                    if (databaseHandler.isCollectionIdValid(etCId.getText().toString().trim())) {
                        centerEntity = databaseHandler.getCenterEntity(etCId.getText().toString().trim(), Util.CHECK_DUPLICATE_CENTERCODE);
                        state = true;
                    } else {
                        errorMsg = "Please enter valid center id";
                    }
                } else {
                    // get data from farmer table
                    String paddingFarmerId = Util.paddingFarmerId(etCId.getText().toString().replace(" ", ""), amcuConfig.getFarmerIdDigit());
                    if (farmerDao.findById(paddingFarmerId) != null) {
                        farmerEntity = (FarmerEntity) farmerDao.findById(paddingFarmerId);
                        state = true;
                    } else {
                        errorMsg = "Please enter valid farmer id";
                    }
                }
            }
            if (state) {

                if (isMilkReject()) {
                    onSuccessFullSubmit();
                } else {
                    double weight = Double.parseDouble(etQuantity.getText().toString().trim());

                    if (weight <= 0 && !isMilkReject()) {
                        Util.displayErrorToast("Please enter valid weight!", UpdatePastRecord.this);
                        etQuantity.requestFocus();
                        etQuantity.setSelection(etQuantity.getText().toString().length());
                    } else if (!validationHelper.validMilkWeight(weight, UpdatePastRecord.this)) {
                        collectionHelper.showAlertForManualWeight(CollectionHelper.FROM_UPDATE_PAST_RECORD);
                    } else {
                        onSuccessFullSubmit();
                    }
                }


            } else
                Toast.makeText(UpdatePastRecord.this, errorMsg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(UpdatePastRecord.this, "Enter valid data", Toast.LENGTH_SHORT).show();
        }


    }


    public void onClickCancel(View view) {
        //databaseHandler.deleteOlderRecords(1);
        finish();
    }

    public void setRate(EditText etText) {

        etText.post(new Runnable() {
            @Override
            public void run() {

                if ((etFat.isCursorVisible() || etClr.isCursorVisible()) &&
                        (amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") &&
                                !reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) ||
                        (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") &&
                                reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC))) {
                    String snf = String.valueOf(calculateSNF(validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0),
                            validationHelper.getDoubleFromString(etClr.getText().toString().trim(), 0)));
                    if (!snf.equalsIgnoreCase(etSnf.getText().toString()))
                        etSnf.setText(snf);
                } else if ((etFat.isCursorVisible() || etSnf.isCursorVisible())) {
                    String clr = calculateCLR(etFat.getText().toString().trim(), etSnf.getText().toString().trim());
                    if (!clr.equalsIgnoreCase(etClr.getText().toString()))
                        etClr.setText(clr);
                }

                String milktype = reportEntity.milkType;

                String fat = etFat.getText().toString().trim();
                String snf = etSnf.getText().toString().trim();
                setRateChart(milktype);
                if (fat != null && snf != null && fat.length() > 0 && snf.length() > 0) {
                    try {
                        MilkAnalyserEntity maEntity = getMaEntity();
                        String rate = smartCCUtil.getRateFromRateChart(maEntity, amcuConfig.getRateChartName());


                        etRate.setText(rate);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                setAmount();
            }
        });


    }

    public void setRateChart(String milkType) {

        if (milkType.equalsIgnoreCase("Buffalo")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForBuffalo());
        } else if (milkType.equalsIgnoreCase("Mixed")) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForMixed());

        } else {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForCow());
        }
    }

    public void setAmount() {
        try {
            changedRate = Double.parseDouble(decimalFormatRate.format(Double.parseDouble(
                    etRate.getText().toString().trim())));
            double qty = Double.parseDouble(decimalFormatQuantity.format(Double.parseDouble(
                    etQuantity.getText().toString().trim())));
            changedAmount = Double.parseDouble
                    (decimalFormatAmount.format(changedRate * qty));
            etAmount.setText(String.valueOf(changedAmount));

        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    public String getRateFromRateChart(double snf, double fat) {
        String rate = "0.00";


        MilkAnalyserEntity maEntity = getMaEntity();
        rate = smartCCUtil.getRateFromRateChart(maEntity, amcuConfig.getRateChartName());
        if (rate == null) {
            rate = "0.0";
        }
        Rate = Double.parseDouble(decimalFormatRate.format(Double
                .parseDouble(rate)));
        etRate.setText(String.valueOf(Rate));
        return String.valueOf(Rate);

    }

    public void setLayoutData(ReportEntity reportEntity) {
        if (null != reportEntity) {


            etCId.setText(reportEntity.farmerId);
            etFat.setText(String.valueOf(reportEntity.getDisplayFat()));
            etSnf.setText(String.valueOf(reportEntity.getDisplaySnf()));
            etClr.setText(String.valueOf(reportEntity.getDisplayClr()));
            etQuantity.setText(String.valueOf(reportEntity.getDisplayQuantity()));
            etRate.setText(String.valueOf(reportEntity.getDisplayRate()));
            etAmount.setText(String.valueOf(Util.getAmount(getApplicationContext(),
                    reportEntity.getTotalAmount(), reportEntity.bonus)));


        }
    }

    public void markEntityAsOld(ReportEntity entity) {

        if (null != reportEntity) {
            reportEntity.oldOrNewFlag = "old";
            reportEntity.foreignSequenceNum = entity.sequenceNum;
            setRateChart(reportEntity.milkType);

        } else {
            finish();
        }

    }

    public void addUpdateDataFromCursor() {


        reportEntityUpdateVal.columnId = reportEntity.columnId;
        reportEntityUpdateVal.milkType = reportEntity.milkType;
        if (farmerEntity != null) {
            reportEntityUpdateVal.farmerId = Util.paddingFarmerId(etCId.getText().toString().trim(), amcuConfig.getFarmerIdDigit());
        } else {
            reportEntityUpdateVal.farmerId = etCId.getText().toString().trim();
        }

        if (reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            reportEntityUpdateVal.farmerName = centerEntity.centerName;
        } else {
            reportEntityUpdateVal.farmerName = farmerEntity.farmer_name;
        }


        boolean isReject = isMilkReject();

        if (!isReject) {
            reportEntityUpdateVal.status = "Accept";
        } else {
            reportEntityUpdateVal.status = "Reject";
            etQuantity.setText("0.00");
            etQuantity.setEnabled(false);
            Toast.makeText(UpdatePastRecord.this, "This milk got rejected", Toast.LENGTH_SHORT).show();
        }

        if (reportEntityUpdateVal.status.equalsIgnoreCase("Reject")) {

            reportEntityUpdateVal.quantity = 0;
            reportEntityUpdateVal.rate = 0;
            reportEntityUpdateVal.bonus = 0;
            reportEntityUpdateVal.amount = 0;

            reportEntityUpdateVal.kgWeight = 0;
            reportEntityUpdateVal.ltrsWeight = 0;
        } else {

            CollectionHelper collectionHelper = new CollectionHelper(UpdatePastRecord.this);

            QuantityEntity quantityEntity = collectionHelper.getQuantityItems(Double.parseDouble
                    (validationHelper.getDoubleFromString(decimalFormatQuantity,
                            etQuantity.getText().toString().trim())));

            reportEntityUpdateVal.quantity = quantityEntity.displayQuantity;
            reportEntityUpdateVal.ltrsWeight = quantityEntity.ltrQuanity;
            reportEntityUpdateVal.kgWeight = quantityEntity.kgQuantity;
            reportEntityUpdateVal.amount = validationHelper.getDoubleFromString(
                    etAmount.getText().toString().trim(), 0);
            reportEntityUpdateVal.rate = validationHelper.getDoubleFromString(
                    etRate.getText().toString().trim(), 0);
            reportEntityUpdateVal = getBonusAmount(reportEntityUpdateVal);

        }

        if ((amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC") &&
                !reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) ||
                (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC") &&
                        reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC))) {
            //reportEntity.snf = cursor.getString(3);
            reportEntityUpdateVal.snf = calculateSNF(validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0),
                    validationHelper.getDoubleFromString(etClr.getText().toString().trim(), 0));
        } else {
            reportEntityUpdateVal.snf = validationHelper.getDoubleFromString(etSnf.getText().toString().trim(), 0);
        }
        reportEntityUpdateVal.fat = validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0);

        if ((amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FS") && !reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) ||
                (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FS") && reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC))) {
            reportEntityUpdateVal.clr = validationHelper.getDoubleFromString(
                    calculateCLR(etFat.getText().toString().trim(), etSnf.getText().toString().trim()), 0);
        } else {
            reportEntityUpdateVal.clr = validationHelper.getDoubleFromString(etClr.getText().toString().toString(), 0);
        }

        reportEntityUpdateVal.user = reportEntity.user;

        reportEntityUpdateVal.txnNumber = reportEntity.txnNumber;
        reportEntityUpdateVal.lDate = reportEntity.lDate;
        reportEntityUpdateVal.socId = reportEntity.socId;
        reportEntityUpdateVal.time = reportEntity.time;
        reportEntityUpdateVal.miliTime = reportEntity.miliTime;

        reportEntityUpdateVal.awm = reportEntity.awm;

        reportEntityUpdateVal.manual = reportEntity.manual;
        reportEntityUpdateVal.quantityMode = reportEntity.quantityMode;
        reportEntityUpdateVal.qualityMode = reportEntity.qualityMode;
        reportEntityUpdateVal.rateMode = reportEntity.rateMode;
        reportEntityUpdateVal.milkAnalyserTime = reportEntity.milkAnalyserTime;
        reportEntityUpdateVal.weighingTime = reportEntity.weighingTime;

        reportEntityUpdateVal.temp = reportEntity.temp;
        reportEntityUpdateVal.recordCommited = reportEntity.recordCommited;
        reportEntityUpdateVal.collectionType = reportEntity.collectionType;

        reportEntityUpdateVal.milkQuality = reportEntity.milkQuality;

        reportEntityUpdateVal.numberOfCans = reportEntity.numberOfCans;

        reportEntityUpdateVal.sampleNumber = reportEntity.sampleNumber;
        if (reportEntity.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC)) {
            reportEntityUpdateVal.centerRoute = centerEntity.centerRoute;
        } else {
            reportEntityUpdateVal.centerRoute = reportEntity.centerRoute;
        }
        reportEntityUpdateVal.recordStatus = reportEntity.recordStatus;

        reportEntityUpdateVal.editedTime = System.currentTimeMillis();

        reportEntityUpdateVal.tippingStartTime = reportEntity.tippingStartTime;
        reportEntityUpdateVal.tippingEndTime = reportEntity.tippingEndTime;
        reportEntityUpdateVal.milkStatusCode = reportEntity.milkStatusCode;
        reportEntityUpdateVal.agentId = reportEntity.agentId;
        reportEntityUpdateVal.user = reportEntity.user;

        reportEntityUpdateVal.oldOrNewFlag = "new";
        reportEntityUpdateVal.foreignSequenceNum = reportEntity.foreignSequenceNum;

        reportEntityUpdateVal.rateCalculation = reportEntity.rateCalculation;
        reportEntityUpdateVal.rateChartName = amcuConfig.getRateChartName();

        reportEntityUpdateVal.postDate = reportEntity.postDate;
        reportEntityUpdateVal.postShift = reportEntity.postShift;

        if (reportEntityUpdateVal.fat == reportEntity.fat &&
                reportEntity.snf == reportEntityUpdateVal.snf)
            reportEntityUpdateVal.maName = reportEntity.maName;
        else
            reportEntityUpdateVal.maName = "NA";

        //In this case amount and total amount field will remain same
//        reportEntityUpdateVal.totalAmount = reportEntityUpdateVal.amount;

    }


    public double calculateSNF(double fat, double clr) {
        double snf = 0;
        try {
            snf = Util.getSNF(fat, clr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        etSnf.setText(decimalFormatSNF.format(snf));
        return snf;
    }

    public String calculateCLR(String fat, String SNF) {
        String clr = "25.0";
        try {
            clr = String.valueOf(Util.getCLR(
                    Double.valueOf(fat),
                    Double.valueOf(SNF)));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        etClr.setText(clr);
        return clr;
    }

    public ReportEntity getBonusAmount(ReportEntity repEnt) {
        double bonus = 0;
        MilkAnalyserEntity maEntity = getMaEntity();
        CollectionHelper collectionHelper = new CollectionHelper(UpdatePastRecord.this);
        bonus = collectionHelper.getBonusAmount(repEnt.farmerId, repEnt.milkType, maEntity);

        bonus = bonus * repEnt.quantity;
        String bonusAmount = decimalFormatAmount.format(bonus);
        double amount = Double.valueOf(repEnt.amount) + Double.valueOf(bonusAmount);

        repEnt.amount = amount;
        repEnt.bonus = bonus;

        return repEnt;
    }

    public void onSuccessFullSubmit() {
        addUpdateDataFromCursor();
        reportEntity.editedTime = reportEntityUpdateVal.editedTime;
        reportEntity.resetSentMarkers();
        reportEntity.sequenceNum = 0;
//        reportEntity.oldOrNewFlag = "old";
        editRecordDao.save(reportEntity);
//        try {
//            new SmartCCUtil(UpdatePastRecord.this).saveReportsOnSdCard(reportEntityUpdateVal);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        saveInSDCard();

        reportEntityUpdateVal.resetSmsMarkers();
        collectionRecordDao.update(reportEntityUpdateVal);
        reportEntityUpdateVal.resetSentMarkers();
        reportEntityUpdateVal.sequenceNum = 0;
        editRecordDao.save(reportEntityUpdateVal);
        startService(new Intent(getApplicationContext(), PostCollectionRecordsService.class));
        finish();
    }

    public boolean isMilkReject() {

        MilkAnalyserEntity maEntity = getMaEntity();

        boolean isReject = smartCCUtil.isMilkRejected(maEntity);
        return isReject;
    }


    private MilkAnalyserEntity getMaEntity() {
        SmartCCUtil smartCCUtil = new SmartCCUtil(UpdatePastRecord.this);
        sessionManager.setMilkType(reportEntity.milkType);

        double fat = validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0);
        double snf = validationHelper.getDoubleFromString(etSnf.getText().toString().trim(), 0);
        double clr = validationHelper.getDoubleFromString(etClr.getText().toString().trim(), 0);


        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        DecimalFormat fatFormat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(
                AppConstants.FAT);
        DecimalFormat snfFormat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(
                AppConstants.SNF);

        DecimalFormat clrFormat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(
                AppConstants.CLR);

        fat = Double.valueOf(fatFormat.format(fat));
        snf = Double.valueOf(snfFormat.format(snf));
        clr = Double.valueOf(clrFormat.format(clr));

        MilkAnalyserEntity maEntity = smartCCUtil.getMAEntity(fat, snf, clr);

        return maEntity;


    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_UP: {
                etCId.requestFocus();
                break;
            }
            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnUpdate.requestFocus();
                break;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean exceedWeightLimit() {
        etQuantity.requestFocus();
        etQuantity.setSelection(etQuantity.getText().toString().length());
        return false;
    }

    @Override
    public boolean proceedWithExceedWeightLimit() {

        onSuccessFullSubmit();

        return false;
    }

    private void setCurrentRecord(final ReportEntity reportEntity) {

        try {
            reportEntityUpdateVal = (ReportEntity) reportEntity.clone();
        } catch (Exception e) {
            reportEntityUpdateVal = new ReportEntity(reportEntity);
        } finally {

        }
    }

    private void saveInSDCard() {
        {

            try {
                String stringReport;
                String date = reportEntityUpdateVal.postDate;
                String shiftReport = reportEntityUpdateVal.postShift;

                SessionManager sessionManager = new SessionManager(UpdatePastRecord.this);

                stringReport = sessionManager.getCollectionID() + ","
                        + reportEntityUpdateVal.farmerId + ","
                        + reportEntityUpdateVal.farmerName + ","
//                        + sessionManager.getFarmerBarcode() + ","
                        + reportEntityUpdateVal.postDate + ","
                        + reportEntityUpdateVal.postShift + ","
                        + reportEntityUpdateVal.milkType + ","
                        + reportEntityUpdateVal.fat + ","
                        + reportEntityUpdateVal.snf + ","
                        + reportEntityUpdateVal.rate + ","
                        + reportEntityUpdateVal.quantity + ","
                        + reportEntityUpdateVal.amount + ","
                        + reportEntityUpdateVal.bonus + ","
                        + reportEntityUpdateVal.manual + ","
                        + reportEntityUpdateVal.status + ","
                        + reportEntityUpdateVal.qualityMode + ","
                        + reportEntityUpdateVal.quantityMode + ","
                        + reportEntityUpdateVal.kgWeight + ","
                        + reportEntityUpdateVal.ltrsWeight + ","
                        + sessionManager.getUserId() + "," +
                        reportEntityUpdateVal.tippingStartTime + "," +
                        reportEntityUpdateVal.tippingEndTime + "\n";
                Util.generateNoteOnSD(date + shiftReport + "_editCollectionReports",
                        stringReport, UpdatePastRecord.this, "smartAmcuReports");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
