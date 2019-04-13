package com.devapp.devmain.macollection;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.WeightLimit;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.EnterRejectDetails;
import com.devapp.smartcc.entityandconstants.QuantityEntity;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devApp.R;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by Upendra on 10/12/2016.
 */
public class MilkRejectActivity extends Activity implements View.OnClickListener, WeightLimit {


    private static MilkRejectActivity mMilkRejectActivity;
    EditText etFat, etSnf, etSID, etQuantity, etRate, etAmount, etCenterId;
    // Spinner spMilkType,spRejectReason;
    Button btnSubmit, btnCancel;
    SessionManager session;
    AmcuConfig amcuConfig;
    ValidationHelper validationHelper;
    DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatWeight = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatRate = new DecimalFormat("#0.00");
    DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    DecimalFormat decimalFormatClr = new DecimalFormat("#0");
    LinearLayout linearQuality, linearRate;
    SmartCCUtil smartCCUtil;
    long collectionTime;
    String shift, cattleType, rejectReason, date;
    CenterEntity centerEntity;
    double rate, amount, weight;

    private ReportEntity reportEntity;
    private CollectionRecordDao collectionRecordDao;

    public static MilkRejectActivity getInstance() {
        return mMilkRejectActivity;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reject_milk_details);
        mMilkRejectActivity = MilkRejectActivity.this;
        initializeView();
        setViewItem();
        setIntentData();
        setVisibility();

    }

    @Override
    protected void onStart() {
        super.onStart();
        session = new SessionManager(MilkRejectActivity.this);
        amcuConfig = AmcuConfig.getInstance();
        smartCCUtil = new SmartCCUtil(MilkRejectActivity.this);
        validationHelper = new ValidationHelper();
        reportEntity = new ReportEntity();
        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
    }

    public void initializeView() {
        etCenterId = (EditText) findViewById(R.id.etCenterId);
        etFat = (EditText) findViewById(R.id.etFat);
        etSnf = (EditText) findViewById(R.id.etSnf);
        etSID = (EditText) findViewById(R.id.etSID);
        etQuantity = (EditText) findViewById(R.id.etMilkWeight);
        etRate = (EditText) findViewById(R.id.etRate);
        etAmount = (EditText) findViewById(R.id.etAmount);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        linearQuality = (LinearLayout) findViewById(R.id.lnQuality);
    }

    public void setIntentData() {

        Intent intent = getIntent();

        String centerid = intent.getStringExtra("CENTERID");
        String sid = intent.getStringExtra("SID");

        etCenterId.setText(intent.getStringExtra("CENTERID"));
        etSID.setText(intent.getStringExtra("SID").toUpperCase(Locale.ENGLISH));
        shift = intent.getStringExtra("SHIFT");
        rejectReason = intent.getStringExtra("REJECT_REASON");
        cattleType = intent.getStringExtra("CATTLE_TYPE");
        date = intent.getStringExtra("DATE");
        collectionTime = System.currentTimeMillis();

    }

    public void setViewItem() {

        Util.alphabetValidation(etFat, Util.ONLY_DECIMAL, MilkRejectActivity.this, 5);
        Util.alphabetValidation(etSnf, Util.ONLY_DECIMAL, MilkRejectActivity.this, 5);
        Util.alphabetValidation(etQuantity, Util.ONLY_DECIMAL, MilkRejectActivity.this, 8);
        Util.alphabetValidation(etRate, Util.ONLY_DECIMAL, MilkRejectActivity.this, 6);
        Util.alphabetValidation(etAmount, Util.ONLY_DECIMAL, MilkRejectActivity.this, 8);

        btnCancel.setOnClickListener(MilkRejectActivity.this);
        btnSubmit.setOnClickListener(MilkRejectActivity.this);

    }

    public boolean ValidateViewItems() {
        boolean validateView = true;
        centerEntity = null;
        double quantity = Double.parseDouble(
                validationHelper.getDoubleFromString(new DecimalFormat("##.0"), etQuantity.getText().toString().trim()));
        double rate = Double.parseDouble(
                validationHelper.getDoubleFromString(new DecimalFormat("##.0"), etRate.getText().toString().trim()));
        double amount = Double.parseDouble(
                validationHelper.getDoubleFromString(new DecimalFormat("##.0"), etAmount.getText().toString().trim()));
        double snf = Double.parseDouble(
                validationHelper.getDoubleFromString(new DecimalFormat("##.0"), etSnf.getText().toString().trim()));

        double fat = Double.parseDouble(
                validationHelper.getDoubleFromString(new DecimalFormat("##.0"), etFat.getText().toString().trim()));
        String centerId = etCenterId.getText().toString().trim();

        centerEntity = DatabaseHandler.getDatabaseInstance(
        ).getCenterEntity(centerId, Util.CHECK_DUPLICATE_CENTERCODE);

        if (centerEntity == null) {
            Toast.makeText(MilkRejectActivity.this, "Invalid center Id ", Toast.LENGTH_SHORT).show();
            etCenterId.requestFocus();
            validateView = false;
        } else if (validationHelper.getIntegerFromString(etSID.getText().toString().trim()) <= 0) {
            Toast.makeText(MilkRejectActivity.this, "Invalid sample Id ", Toast.LENGTH_SHORT).show();
            etSID.requestFocus();
            validateView = false;
        } else if (rejectReason.equalsIgnoreCase("SOUR") &&
                (etFat.getText().toString().trim().length() < 1 ||
                        etSnf.getText().toString().trim().length() < 1)) {
            Toast.makeText(MilkRejectActivity.this, "Invalid fat or snf ", Toast.LENGTH_SHORT).show();
            validateView = false;

        } else if (!validationHelper.isValidFatAndSnf(fat, snf, MilkRejectActivity.this)) {
            etFat.requestFocus();
            validateView = false;
        } else if (rate < 0) {
            etRate.requestFocus();
            validateView = false;
        } else if (amount < 0) {
            etAmount.requestFocus();
            validateView = false;
        } else if (quantity <= 0) {
            etQuantity.requestFocus();
            validateView = false;
        }

        return validateView;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSubmit: {
                if (ValidateViewItems()) {
                    double quantity = Double.parseDouble(
                            validationHelper.getDoubleFromString(new DecimalFormat("##.0"),
                                    etQuantity.getText().toString().trim()));
                    getRateFromRateChart(etSnf.getText().toString().trim(), etFat.getText().toString().trim());

                    if (quantity > 0 && !validationHelper.validMilkWeight(quantity, MilkRejectActivity.this)) {
                        new CollectionHelper(MilkRejectActivity.this).showAlertForManualWeight(
                                CollectionHelper.FROM_MILK_REJECT_ACTIVITY);
                    } else {

                        addToDatabase();
                        onFinish();
                    }


                }
            }
            break;
            case R.id.btnCancel: {
                onFinish();
            }
            break;
            default:
                break;

        }

    }

    public ReportEntity getReportEntity() {
        setAmount();
        int txNumber = new SessionManager(MilkRejectActivity.this)
                .getTXNumber() + 1;

        session.setTXNumber(txNumber);


        reportEntity.temp = 0.0;
        reportEntity.fat = Double.valueOf(validationHelper.getDoubleFromString(
                decimalFormatFS, etFat.getText().toString().trim()));
        reportEntity.snf = Double.valueOf(validationHelper.getDoubleFromString(
                decimalFormatFS, etSnf.getText().toString().trim()));
        double dWeight = 0;
        try {
            dWeight = weight;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        CollectionHelper collectionHelper = new CollectionHelper(MilkRejectActivity.this);

        QuantityEntity quantityEntity = collectionHelper.getQuantityItems(dWeight);


        reportEntity.quantity = Double.valueOf(decimalFormatWeight.format(quantityEntity.displayQuantity));
        reportEntity.amount = amount;
        reportEntity.rate = rate;

        reportEntity.user = session.getUserId();
        reportEntity.farmerId = etCenterId.getText().toString().trim().toUpperCase();
        reportEntity.farmerName = centerEntity.centerName;
        reportEntity.socId = String.valueOf(session.getSocietyColumnId());

        reportEntity.milkType = cattleType;
        reportEntity.lDate = Util.getDateInLongFormat(date);
        reportEntity.milkQuality = rejectReason;
        reportEntity.sampleNumber = Integer.valueOf(etSID.getText().toString().trim());

        reportEntity.time = Util.getTodayDateAndTime(3, MilkRejectActivity.this, true);

        reportEntity.txnNumber = Integer.parseInt(Util.getTxnNumber(txNumber));
        reportEntity.miliTime = collectionTime;
        reportEntity.milkAnalyserTime = collectionTime;
        reportEntity.weighingTime = collectionTime;

        reportEntity.awm = 0.00;
        reportEntity.status = "Reject";
        reportEntity.manual = "Manual";
        reportEntity.qualityMode = "Manual";
        reportEntity.quantityMode = "Manual";
        reportEntity.clr = 0.00;
        reportEntity.bonus = 0.00;
        reportEntity.recordCommited = Util.REPORT_COMMITED;
        reportEntity.collectionType = Util.REPORT_TYPE_MCC;
        reportEntity.rateMode = "Manual";
        reportEntity.numberOfCans = 1;
        reportEntity.centerRoute = Util.getRouteFromChillingCenter(MilkRejectActivity.this, centerEntity.centerId);

        reportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;
        reportEntity.rateChartName = amcuConfig.getRateChartName();
        reportEntity.kgWeight = Double.valueOf(decimalFormatWeight.format(quantityEntity.kgQuantity));
        reportEntity.ltrsWeight = Double.valueOf(decimalFormatWeight.format(quantityEntity.ltrQuanity));
        reportEntity.tippingStartTime = collectionTime;
        reportEntity.tippingEndTime = collectionTime;


        reportEntity.agentId = smartCCUtil.getAgentId();
        reportEntity.milkStatusCode = smartCCUtil.getMilkStatusCode(rejectReason);
        if (amcuConfig.getRateCalculatedFromDevice()) {
            reportEntity.rateCalculation = 1;
        } else {
            reportEntity.rateCalculation = 0;
        }
        reportEntity.maName = amcuConfig.getMA();
        reportEntity.serialMa = 1;


        reportEntity.postDate = SmartCCUtil.getDateInPostFormat();
        reportEntity.postShift = SmartCCUtil.getShiftInPostFormat(MilkRejectActivity.this);


        smartCCUtil.setCollectionStartData(reportEntity);
        return reportEntity;
    }

    public String addToDatabase() {

        String dbError = null;

        reportEntity = getReportEntity();
        reportEntity.resetSentMarkers();
        Util.setDailyDateOrShift(Util.getTodayDateAndTime(7, MilkRejectActivity.this, true),
                reportEntity.postShift);
        try {
            collectionRecordDao.saveOrUpdate(reportEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbError;
    }

    public boolean isRateChartAvailableForSourMilk(String rejectType, String milkType) {
        if (rejectType.equalsIgnoreCase("Sour")) {
            if (milkType.equalsIgnoreCase("COW") && amcuConfig.getSourRateChartCow() == null) {
                displayToast("Sour rate chart is not available for cow");
                return false;
            } else if (milkType.equalsIgnoreCase("Buffalo") && amcuConfig.getSourRateChartBuffalo() == null) {
                displayToast("Sour rate chart is not available for buffalo");
                return false;
            } else if (milkType.equalsIgnoreCase("Mixed") && amcuConfig.getSourRateChartMixed() == null) {
                displayToast("Sour rate chart is not available for mixed");
                return false;
            }

        }

        return true;

    }

    public void displayToast(String str) {

        Util.displayErrorToast(str, MilkRejectActivity.this);
    }

    public String getRateFromRateChart(String snf, String fat) {

        getRateChart();

        if (amcuConfig.getRateChartName() == null) {
            Util.displayErrorToast("No rate chart available!", MilkRejectActivity.this);
            etRate.setText("0.00");
            etAmount.setText("0.00");


            return "0.00";
        }

        String rate = "0.00";
        snf = snf.replace(" ", "");
        fat = fat.replace(" ", "");

        if (!fat.equalsIgnoreCase("") && !snf.equalsIgnoreCase("")) {
            snf = decimalFormatFS.format(Double.parseDouble(snf));
            fat = decimalFormatFS.format(Double.parseDouble(fat));
            try {

                DatabaseManager dbManager = new DatabaseManager(MilkRejectActivity.this);
                rate =
                        dbManager.getRateForGivenParams(Double.valueOf(fat),
                                Double.valueOf(snf), Util.getCLR(Double.valueOf(fat),
                                        Double.valueOf(snf)),
                                amcuConfig.getRateChartName());

                etFat.setText(fat);
                etSnf.setText(snf);

            } catch (Exception e) {
                e.printStackTrace();
            }

            //DB close removed;
        } else {
            Toast.makeText(MilkRejectActivity.this,
                    "Fat and snf not valid!", Toast.LENGTH_SHORT).show();
        }
        if (rate == null) {
            rate = "0.0";
        }
        double doubleRate = Double.parseDouble(decimalFormatRate.format(Double
                .parseDouble(rate)));
        etRate.setText(String.valueOf(doubleRate));

        if (etQuantity.getText().toString().equalsIgnoreCase("")) {
            etAmount.setText(String.valueOf("0.00"));
        }
        return String.valueOf(doubleRate);

    }

    public String getRateChart() {
        amcuConfig.setRateChartName("null");
        if (rejectReason.equalsIgnoreCase("SOUR")) {
            if (cattleType.equalsIgnoreCase("COW")) {
                amcuConfig.setRateChartName(amcuConfig.getSourRateChartCow());
            } else if (cattleType.equalsIgnoreCase("Buffalo")) {
                amcuConfig.setRateChartName(amcuConfig.getSourRateChartBuffalo());
            } else if (cattleType.equalsIgnoreCase("Mixed")) {
                amcuConfig.setRateChartName(amcuConfig.getSourRateChartMixed());
            }
        } else if (rejectReason.equalsIgnoreCase("SOUR VEHICLE FAULT")) {
            if (cattleType.equalsIgnoreCase("COW")) {
                amcuConfig.setRateChartName(amcuConfig.getRateChartForCow());
            } else if (cattleType.equalsIgnoreCase("Buffalo")) {
                amcuConfig.setRateChartName(amcuConfig.getRateChartForBuffalo());
            } else if (cattleType.equalsIgnoreCase("Mixed")) {
                amcuConfig.setRateChartName(amcuConfig.getRateChartForMixed());
            }
        }
        return amcuConfig.getRateChartName();
    }

    public void setAmount() {
        ValidationHelper validationHelper = new ValidationHelper();

        rate = Double.valueOf(
                validationHelper.getDoubleFromString(decimalFormatRate, etRate.getText().toString()));
        weight = Double.valueOf(
                validationHelper.getDoubleFromString(decimalFormatWeight, etQuantity.getText().toString()));

        double damount = Double.valueOf(rate) * Double.valueOf(weight);

        etAmount.setText(String.valueOf(decimalFormatAmount.format(damount)));
        amount = Double.valueOf(
                validationHelper.getDoubleFromString(decimalFormatAmount, etAmount.getText().toString()));

    }

    public void setVisibility() {
        etAmount.setVisibility(View.GONE);
        etRate.setVisibility(View.GONE);

        if (rejectReason.equalsIgnoreCase("CURD")) {
            linearQuality.setVisibility(View.INVISIBLE);
            etFat.setText("0.0");
            etSnf.setText("0.0");
            etAmount.setText("0.00");
            etRate.setText("0.00");

        }
    }

    @Override
    public boolean exceedWeightLimit() {
        etQuantity.requestFocus();
        etQuantity.setSelection(etQuantity.getText().toString().length());
        return false;
    }

    @Override
    public boolean proceedWithExceedWeightLimit() {
        addToDatabase();
        onFinish();
        return false;
    }

    public void onFinish() {

        startActivity(new Intent(MilkRejectActivity.this, EnterRejectDetails.class));
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
        finish();
    }

}
