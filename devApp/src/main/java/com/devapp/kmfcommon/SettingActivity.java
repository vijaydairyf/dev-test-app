package com.devapp.kmfcommon;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.dao.RateDao;
import com.devapp.devmain.devicemanager.ConfigurationManager;
import com.devapp.devmain.entity.ConfigurationConstants;
import com.devapp.devmain.entity.ConfigurationElement;
import com.devapp.devmain.entity.ConfigurationEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.httptasks.ConfigurationPush;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.UpdateFarmerService;
import com.devapp.devmain.usb.ReadExcel;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.RegexTextWatcher;
import com.devapp.devmain.util.ValidationHelper;
import com.devApp.R;

import java.io.File;
import java.io.FilenameFilter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SettingActivity extends Activity implements OnClickListener {

    private static final String FTYPE = ".xls";
    private static final int DIALOG_LOAD_FILE = 1000;
    private static int year;
    private static int month;
    private static int day;
    private final Calendar mcurrentTime = Calendar.getInstance();
    private final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
    private final int minute = mcurrentTime.get(Calendar.MINUTE);
    private final Handler myHandler = new Handler();
    private final DecimalFormat decimalFormatFS = new DecimalFormat("#0.0");
    private final DecimalFormat decimalFormat2Digit = new DecimalFormat("#0.00");
    private final DecimalFormat decimalFormatConversion = new DecimalFormat("#0.0000");
    private final int MORNING_START_TIME = 0;
    private final int MORNING_END_TIME = 1;
    private final int EVENING_START_TIME = 2;
    private final int EVENING_END_TIME = 3;
    private final int BONUS_START_DATE = 4;
    private final int BONUS_END_DATE = 5;
    private ArrayList<RatechartDetailsEnt> allRateChartDetails;
    private int setDateId = 0;
    private Map<String, String> treemap;
    private EditText etMorningEnd;
    private EditText etEveningEnd;
    private EditText etMinFat;
    private EditText etMinSnf;
    private EditText etMinClr;
    private EditText etMessageLimit;
    private boolean isRateSnf;
    private boolean isRateFat;
    private boolean isRateClr;
    private boolean isEKOMILK;
    private boolean isMultipleMA;
    private EditText etFatCons;
    private EditText etSnfCons;
    private EditText etCons;
    private EditText etMorningStart;
    private EditText etEveningStart;
    private EditText etMaxFat;
    private EditText etMaxSnf;
    private EditText etSnfBuff;
    private EditText etFatBuff;
    private EditText etBonusStartDate;
    private EditText etBonusEndDate;
    private final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth + 1;
            day = selectedDay;
            String _day = String.valueOf(day);
            String _month = String.valueOf(month);
            if (day < 10)
                _day = "0" + String.valueOf(day);
            if (month < 10)
                _month = "0" + String.valueOf(month);

            // set selected date into textview

            if (setDateId == BONUS_START_DATE) {
                etBonusStartDate.setText(new StringBuilder()
                        // Month is 0 based, just add 1
                        .append(_day).append("-").append(_month).append("-")
                        .append(year));

            } else if (setDateId == BONUS_END_DATE) {

                etBonusEndDate.setText(new StringBuilder()
                        // Month is 0 based, just add 1
                        .append(_day).append("-").append(_month).append("-")
                        .append(year));
            }
        }
    };
    private EditText etBonusRate;
    private EditText etConversion;
    private EditText etSID;
    private boolean isSnfChecked;
    private boolean isFatChecked;
    private boolean isClrChecked;
    private boolean isKeyboard;
    private boolean isUnregister;
    private boolean isRateEditCheck;
    private boolean isRateImportViaExcel;
    private boolean isRateAccess;
    private boolean isMaManual;
    private boolean isWsManual;
    private boolean isSMSEnable;
    private boolean isSimlockEnable;
    private CheckBox chkFat;
    private CheckBox chkSnf;
    private CheckBox chkClr;
    private CheckBox chkLM2;
    private CheckBox chkEM;
    private CheckBox chkSupport_unreg;
    private CheckBox chkSupport_keyPad;
    private CheckBox chkMaxFat;
    private CheckBox chkMaxSnf;
    private CheckBox chkRateRoundOff;
    private CheckBox chkWeightRoundOff;
    private CheckBox chkAmountRoundOff;
    private CheckBox checkAmount;
    private CheckBox checkAddedWater;
    private CheckBox checkMailToConfID;
    private CheckBox checkEditRatechart;
    private CheckBox checkRateImportViaExcel;
    private CheckBox checkBonusEnable;
    private CheckBox checkRateAccess;
    private CheckBox checkWsManual;
    private CheckBox checkMaManual;
    private CheckBox checkEnableSMS;
    private CheckBox checkSimLock;
    private CheckBox checkFATSNFCLR;
    private Button btnAlertSave;
    private Button btnAlertCancel;
    private RelativeLayout rlUnregistered;
    private RelativeLayout rlManualEntry;
    private RelativeLayout rlMaManual;
    private RelativeLayout rlWsMaual;
    private RelativeLayout rlSimlock;
    private RelativeLayout rlAllowSimlock;
    private RelativeLayout rlConversion;
    private RelativeLayout rlFarmerDigit;
    private LinearLayout lnSelectAmounAddedWater;
    private LinearLayout lnUnregistered;
    private LinearLayout lnConversion;
    private LinearLayout lnBonusDate;
    private LinearLayout lnBonusInfo;
    private LinearLayout lnFarmerDigit;
    private SessionManager session;
    private Runnable updateRunnable;
    private Spinner spMilkType;
    private Spinner spRatchart;
    private Spinner spMA;
    private Spinner spMAbaudrate;
    private Spinner spRDU;
    private Spinner spRDUbaudrate;
    private Spinner spWeighingScale;
    private Spinner spWeighingBaud;
    private Spinner spPrinterBAud;
    private Spinner spPrinter;
    private Spinner spSecondMik;
    private Spinner spSecondMilkBaud;
    private Spinner spRoundWeight;
    private Spinner spRoundRate;
    private Spinner spRoundAmount;
    private Spinner spMA1MilkType;
    private Spinner spMA2MilkType;
    private Spinner spUSBGATEWAY;
    private Spinner spChooseDelay;
    private Spinner spNumberOfDigit;
    private Spinner spMAParity;
    private Spinner spMADataBits;
    private Spinner spMAStopBits;
    private EditText etValidFrom;
    private EditText etValidTill;
    private EditText etMinWeightFill;
    private EditText etMaxWeightEmpty;
    private String milkType = "COW";
    private String rateChart = "";
    private AmcuConfig amcuConfig;
    private int maBaudrate;
    private int rduBaudrate;
    private int printerBaud;
    private int weighingroundOff;
    private int rateRoundOff;
    private int amountRoundOff;
    private int WeighingScaleBaud;
    private int maSecBaudRate;
    private String MAtype = "EKOMILK";
    private String RDUtype = "SMART";
    private String PrinterType = "TVS";
    private String WeighingScaleType = "HATSUN";
    private String GateWayType = "HUB";
    private String maSecType
            = "EKOMILK";
    private String ma1MilkType;
    private String ma2MilkType;
    private String shutDownDealy = "0";
    private String farmDigitConstraints = "4";
    private RelativeLayout rlSelectOption;
    private RelativeLayout rlBonusHeading;
    private boolean isRateRoundCheck;
    private boolean isAmountRoundCheck;
    private boolean isWeightRoundCheck;
    private boolean isAddedWater;
    private boolean isMailSend;
    private boolean isBonusEnable;
    private RelativeLayout rlSales;
    private LinearLayout lnCowRate;
    private LinearLayout lnBuffRate, lnSalesMixRate;
    private LinearLayout lnDisplayFS;
    private EditText etCowRate, etMixedRate;
    private EditText etBuffRate;
    private EditText etWeightLimit;
    private CheckBox checkDisplayFS, checkEnableOrDisableSales, checkEnableOrDisableManual;
    private boolean isDisplayFS;
    //for A4 print
    private CheckBox checkA4;
    private boolean enableA4Print;
    private RadioButton radio_fatsnf;
    private RadioButton radio_fatclr;
    private RadioButton radio_chilling_fatsnf;
    private RadioButton radio_chillinng_fatclr;
    private ConfigurationManager configurationManager;
    private ConfigurationEntity oldConfiguationEntity;
    private String maParity;
    private String maDataBits;
    private String maStopBits;
    private RateChartNameDao rateChartNameDao;
    private String[] mFileList;
    private File mPath;
    private String mChosenFile, fileName;
    private String chillingFatSnfClr = "FS";
    private String collectionFatSnfClr = "FS";
    private boolean setToggelCan = false, setOperatorPermission = false;
    private EditText etPreffix;
    private EditText etSuffix;
    private EditText etSeparator;
    private RateDao rateDao;
    private DatabaseHandler databaseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_settiings);
        //To make the screen awake
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        rateDao = (RateDao) DaoFactory.getDao(CollectionConstants.RATES);
        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        session = new SessionManager(SettingActivity.this);
        amcuConfig = AmcuConfig.getInstance();
        databaseHandler = DatabaseHandler.getDatabaseInstance();

        // edit and upadte old record
        RelativeLayout rlUpdateRecord = (RelativeLayout) findViewById(R.id.rlUpdateRecord);
        LinearLayout lnUpdateRecord = (LinearLayout) findViewById(R.id.lnUpdateRecord);
        RelativeLayout rlSID = (RelativeLayout) findViewById(R.id.rlSID);
        LinearLayout lnSID = (LinearLayout) findViewById(R.id.lnSID);
        if (!amcuConfig.getAllowEquipmentBasedCollection()) {
            rlSID.setVisibility(View.GONE);
            lnSID.setVisibility(View.GONE);
        }
        CheckBox checkUpdateRecordOperatorPremission = (CheckBox) findViewById(R.id.checkUpdateRecordOperatorPremission);
        EditText etupdaterecordNumberofShift = (EditText) findViewById(R.id.etupdaterecordNumberofShift);
        if (amcuConfig.getAllowVisibilityReportEditing()) {
            setOperatorPermission = amcuConfig.getPermissionToEditReport();
            rlUpdateRecord.setVisibility(View.VISIBLE);
            lnUpdateRecord.setVisibility(View.VISIBLE);
            if (Util.isOperator(this)) {
                if (amcuConfig.getPermissionToEditReport())
                    checkUpdateRecordOperatorPremission.setChecked(true);
                checkUpdateRecordOperatorPremission.setEnabled(false);
            } else {
                if (amcuConfig.getPermissionToEditReport())
                    checkUpdateRecordOperatorPremission.setChecked(true);
                checkUpdateRecordOperatorPremission.setEnabled(true);
            }

            etupdaterecordNumberofShift.setText("" + amcuConfig.getNumberShiftCanBeEdited());
        }

        checkUpdateRecordOperatorPremission.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                //  Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
                setOperatorPermission = arg1;
            }
        });
        // toggle can
        RelativeLayout rlToggle = (RelativeLayout) findViewById(R.id.rlToggle);
        LinearLayout lnToggle = (LinearLayout) findViewById(R.id.lnToggle);
        CheckBox checkTogglePermission = (CheckBox) findViewById(R.id.checkTogglePermission);
        etMinWeightFill = (EditText) findViewById(R.id.etMinWeightFill);
        etMaxWeightEmpty = (EditText) findViewById(R.id.etMaxWeightEmpty);

        checkTogglePermission.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                // TODO Auto-generated method stub
                //  Toast.makeText(getApplicationContext(), "Check box "+arg0.getText().toString()+" is "+String.valueOf(arg1) , Toast.LENGTH_LONG).show();
                setToggelCan = arg1;
            }
        });
        if (amcuConfig.getAllowVisiblityForCanToggle()) {

            boolean toggelStatus = amcuConfig.getCansToggling();
            if (toggelStatus) {
                checkTogglePermission.setChecked(true);
                setToggelCan = true;
            }
            etMinWeightFill.setText(String.valueOf(amcuConfig.getMinLimitFilledCans()));
            etMaxWeightEmpty.setText(String.valueOf(amcuConfig.getMaxLimitEmptyCans()));
            Util.alphabetValidation(etMinWeightFill, Util.ONLY_NUMERIC, SettingActivity.this, 0);
            Util.alphabetValidation(etMaxWeightEmpty, Util.ONLY_NUMERIC, SettingActivity.this, 0);
            if (Util.isOperator(this)) {
                etMinWeightFill.setEnabled(false);
                etMaxWeightEmpty.setEnabled(false);
                checkTogglePermission.setEnabled(false);
            }

        } else {
            rlToggle.setVisibility(View.GONE);
            lnToggle.setVisibility(View.GONE);
        }
        //SID
        etSID = (EditText) findViewById(R.id.etAlertSID);
        etSID.setText("" + amcuConfig.getLastSaveSID());
        if (Util.isOperator(this)) {
            etSID.setFocusable(false);
            etSID.setEnabled(false);
        } else {
            if (databaseHandler.isCollectionStartedForCurrentShift()) {
                etSID.setFocusable(false);
                etSID.setEnabled(false);
            } else {
                etSID.setFocusable(true);
                etSID.setEnabled(true);
            }
        }

        /// sID
        //saveSID();
        // for Milk Quality fat,snf & clr
        checkFATSNFCLR = (CheckBox) findViewById(R.id.checkSelectAllMilkQuality);
        RadioGroup radioCollectionCenter = (RadioGroup) findViewById(R.id.radio_collection);
        RadioGroup radioChillingCenter = (RadioGroup) findViewById(R.id.radio_chilling_collection);
        radio_fatsnf = (RadioButton) findViewById(R.id.radio_fatsnf);
        radio_fatclr = (RadioButton) findViewById(R.id.radio_fatclr);
        radio_chilling_fatsnf = (RadioButton) findViewById(R.id.radio_chilling_fatsnf);
        radio_chillinng_fatclr = (RadioButton) findViewById(R.id.radio_chillinng_fatclr);
        setRadioSelectView();
        boolean isFatSnfClr = amcuConfig.getFATSNFCLR();
        if (isFatSnfClr) {
            checkFATSNFCLR.setChecked(true);
        } else {
            checkFATSNFCLR.setChecked(false);
        }
        selectRadioCenter(radioCollectionCenter);
        selectRadioCenter(radioChillingCenter);
        checkAll(checkFATSNFCLR);

        //
        etEveningEnd = (EditText) findViewById(R.id.etEvening);
        etMorningEnd = (EditText) findViewById(R.id.etMorning);
        etMessageLimit = (EditText) findViewById(R.id.etMessageLimit);

        etMinClr = (EditText) findViewById(R.id.etClr);
        etMinFat = (EditText) findViewById(R.id.etMinFat);
        etMinSnf = (EditText) findViewById(R.id.etMinSnf);

        etMaxFat = (EditText) findViewById(R.id.etMaxFat);
        etMaxSnf = (EditText) findViewById(R.id.etMaxSnf);

        etFatCons = (EditText) findViewById(R.id.etFatConstant);
        etSnfCons = (EditText) findViewById(R.id.etSnfConstant);
        etCons = (EditText) findViewById(R.id.etConstant);
        etMorningStart = (EditText) findViewById(R.id.etMorningStart);
        etEveningStart = (EditText) findViewById(R.id.etEveningStart);
        etConversion = (EditText) findViewById(R.id.etConversionFactor);
        etWeightLimit = (EditText) findViewById(R.id.etWeighingLimit);

        etMorningStart.setFocusable(false);
        etMorningStart.setEnabled(false);

        etFatBuff = (EditText) findViewById(R.id.etBuffFat);
        etSnfBuff = (EditText) findViewById(R.id.etBuffSnf);

        chkClr = (CheckBox) findViewById(R.id.CheckClr);
        chkFat = (CheckBox) findViewById(R.id.CheckMinFat);
        chkSnf = (CheckBox) findViewById(R.id.CheckMinSnf);

        chkMaxFat = (CheckBox) findViewById(R.id.CheckMaxFat);
        chkMaxSnf = (CheckBox) findViewById(R.id.CheckMaxSnf);
        chkLM2 = (CheckBox) findViewById(R.id.checkLM);
        chkEM = (CheckBox) findViewById(R.id.checkIsEcomilk);
        chkSupport_keyPad = (CheckBox) findViewById(R.id.checkSupportKey);
        chkSupport_unreg = (CheckBox) findViewById(R.id.checkUnregUser);
        chkAmountRoundOff = (CheckBox) findViewById(R.id.CheckAmountRound);
        chkRateRoundOff = (CheckBox) findViewById(R.id.CheckRateRound);
        chkWeightRoundOff = (CheckBox) findViewById(R.id.CheckWeightRound);
        checkAmount = (CheckBox) findViewById(R.id.checkAmount);
        checkAddedWater = (CheckBox) findViewById(R.id.checkAddedWater);
        checkEditRatechart = (CheckBox) findViewById(R.id.checkCreateAndEdit);
        checkMailToConfID = (CheckBox) findViewById(R.id.CheckSendEmail);
        checkRateImportViaExcel = (CheckBox) findViewById(R.id.CheckImportRatechart);
        checkBonusEnable = (CheckBox) findViewById(R.id.checkEnableBonus);
        checkRateAccess = (CheckBox) findViewById(R.id.checkRateAccess);



        checkMaManual = (CheckBox) findViewById(R.id.checkMaManual);
        checkWsManual = (CheckBox) findViewById(R.id.checkWsManual);

        checkEnableSMS = (CheckBox) findViewById(R.id.checkEnableSMS);
        checkSimLock = (CheckBox) findViewById(R.id.checkAllowSimlock);

        CheckBox isSnfCheck = (CheckBox) findViewById(R.id.CheckIsSnf);
        CheckBox isFatCheck = (CheckBox) findViewById(R.id.CheckIsFat);
        CheckBox isClrCheck = (CheckBox) findViewById(R.id.CheckIsClr);

        etValidFrom = (EditText) findViewById(R.id.etValidFrom);
        etValidTill = (EditText) findViewById(R.id.etValidTill);
        etBonusEndDate = (EditText) findViewById(R.id.etBonusEnd);
        etBonusStartDate = (EditText) findViewById(R.id.etBonusStart);
        etBonusRate = (EditText) findViewById(R.id.etBonusperLiter);

        rlUnregistered = (RelativeLayout) findViewById(R.id.rlUnregUser);
        lnUnregistered = (LinearLayout) findViewById(R.id.lnUnregistered);
        rlConversion = (RelativeLayout) findViewById(R.id.rlConversion);
        lnConversion = (LinearLayout) findViewById(R.id.lnConversion);
        lnBonusDate = (LinearLayout) findViewById(R.id.lnBounusDate);
        lnBonusInfo = (LinearLayout) findViewById(R.id.lnBonusInfo);
        lnFarmerDigit = (LinearLayout) findViewById(R.id.lnFarmerDigit);


        spMilkType = (Spinner) findViewById(R.id.spMilkType);
        spRatchart = (Spinner) findViewById(R.id.spRatechart);
        spMA = (Spinner) findViewById(R.id.spMilkAnaly);
        spMAbaudrate = (Spinner) findViewById(R.id.spMilkBaud);
        spRDU = (Spinner) findViewById(R.id.spRDU);
        spRDUbaudrate = (Spinner) findViewById(R.id.spRdubaud);
        spRoundAmount = (Spinner) findViewById(R.id.spAmountRound);
        spRoundRate = (Spinner) findViewById(R.id.spRateRound);
        spRoundWeight = (Spinner) findViewById(R.id.spWeighingRound);
        spWeighingBaud = (Spinner) findViewById(R.id.spWeigingbaud);
        spWeighingScale = (Spinner) findViewById(R.id.spWeighing);
        spUSBGATEWAY = (Spinner) findViewById(R.id.spUsbGateWay);
        spSecondMik = (Spinner) findViewById(R.id.spMilkAnalySec);
        spSecondMilkBaud = (Spinner) findViewById(R.id.spMilkBaudSec);
        spMA1MilkType = (Spinner) findViewById(R.id.spMA1MilkType);
        spMA2MilkType = (Spinner) findViewById(R.id.spMA2MilkType);
        spChooseDelay = (Spinner) findViewById(R.id.spShutDownDelay);
        spNumberOfDigit = (Spinner) findViewById(R.id.spFarmerDigit);

        rlSelectOption = (RelativeLayout) findViewById(R.id.rlSelectOption);

        rlAllowSimlock = (RelativeLayout) findViewById(R.id.rlAllowSimlock);
        rlSimlock = (RelativeLayout) findViewById(R.id.rlSimlock);
        RelativeLayout rlCreateAndEdit = (RelativeLayout) findViewById(R.id.rlCreateAndEditRateChart);
        rlBonusHeading = (RelativeLayout) findViewById(R.id.rlEnableBonus);
        rlFarmerDigit = (RelativeLayout) findViewById(R.id.rlFarmDigit);

        rlCreateAndEdit.setVisibility(View.GONE);
        rlCreateAndEdit.setVisibility(View.GONE);

        rlManualEntry = (RelativeLayout) findViewById(R.id.rlManuallEntry);
        rlMaManual = (RelativeLayout) findViewById(R.id.rlMaManual);
        rlWsMaual = (RelativeLayout) findViewById(R.id.rlWSManual);

        RelativeLayout rlsetMultipleMA = (RelativeLayout) findViewById(R.id.rlsetMultipleMA);
        LinearLayout lnMultipleMA = (LinearLayout) findViewById(R.id.lnMultipleMA);
        CheckBox checkMultipleMA = (CheckBox) findViewById(R.id.checkMultipleMA);

        etupdaterecordNumberofShift.addTextChangedListener(new RegexTextWatcher(etupdaterecordNumberofShift, AppConstants.Regex.NUMBER_DECIMAL));

        if (amcuConfig.getMultipleMA()) {
            rlsetMultipleMA.setVisibility(View.VISIBLE);
            lnMultipleMA.setVisibility(View.VISIBLE);
        } else {
            lnMultipleMA.setVisibility(View.GONE);
            rlsetMultipleMA.setVisibility(View.GONE);
        }
        isMultipleMA = amcuConfig.getCheckboxMultipleMA();
        if (amcuConfig.getCheckboxMultipleMA()) {
            checkMultipleMA.setChecked(true);

        } else {
            checkMultipleMA.setChecked(false);
        }
        lnSelectAmounAddedWater = (LinearLayout) findViewById(R.id.lnSelectaddedWater);

        spPrinter = (Spinner) findViewById(R.id.spPrinter);
        spPrinterBAud = (Spinner) findViewById(R.id.spPrinterBaud);

        btnAlertSave = (Button) findViewById(R.id.btnSave);
        btnAlertCancel = (Button) findViewById(R.id.btnCancel);


        TextView tvWsDetails = (TextView) findViewById(R.id.tvWeighingDetails);
        tvWsDetails.setVisibility(View.VISIBLE);
        tvWsDetails.setText(Util.getAllConfiguration(SettingActivity.this));

        //For sales
        rlSales = (RelativeLayout) findViewById(R.id.rlSales);
        lnCowRate = (LinearLayout) findViewById(R.id.lnCowRateSales);
        lnBuffRate = (LinearLayout) findViewById(R.id.lnBuffRateSales);
        lnSalesMixRate = (LinearLayout) findViewById(R.id.lnSalesMixRate);
        lnDisplayFS = (LinearLayout) findViewById(R.id.lnSalesDisplayFS);

        etCowRate = (EditText) findViewById(R.id.etCowRate);
        etBuffRate = (EditText) findViewById(R.id.etBuffRate);
        etMixedRate = (EditText) findViewById(R.id.etMixedRate);

        checkDisplayFS = (CheckBox) findViewById(R.id.checkDisplayFS);
        checkEnableOrDisableSales = (CheckBox) findViewById(R.id.checkEnableDisableSales);
        checkEnableOrDisableManual = (CheckBox) findViewById(R.id.checkEnableOrDisableManual);
        checkA4 = (CheckBox) findViewById(R.id.checkEnableA4);
        etPreffix = (EditText) findViewById(R.id.etPreffix);
        etSuffix = (EditText) findViewById(R.id.etSuffix);
        etSeparator = (EditText) findViewById(R.id.etSeparator);

        LinearLayout layoutWSConfiguration = (LinearLayout) findViewById(R.id.layoutWSSetting);
        //MA configuration parameters

        if (Util.isOperator(SettingActivity.this)) {
            layoutWSConfiguration.setVisibility(View.GONE);
        } else {
            layoutWSConfiguration.setVisibility(View.VISIBLE);

        }
        updateWSPrefixAndSuffix();

        // ///////////////////////////////////
        RelativeLayout alertProgressLayout = (RelativeLayout) findViewById(R.id.progress_layout);
        alertProgressLayout.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        spWeighingScale.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {
                WeighingScaleType = spWeighingScale.getItemAtPosition(pos)
                        .toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spUSBGATEWAY.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                GateWayType = spUSBGATEWAY.getItemAtPosition(pos).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spPrinter.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                PrinterType = spPrinter.getItemAtPosition(pos).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spRoundWeight.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                weighingroundOff = Integer.parseInt(spRoundWeight
                        .getItemAtPosition(pos).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spMA1MilkType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {
                ma1MilkType = spMA1MilkType.getItemAtPosition(pos).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spMA2MilkType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {
                ma2MilkType = spMA2MilkType.getItemAtPosition(pos).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        spChooseDelay.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shutDownDealy = spChooseDelay.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spNumberOfDigit.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                farmDigitConstraints = spNumberOfDigit.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spRoundRate.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                rateRoundOff = Integer.parseInt(spRoundRate.getItemAtPosition(
                        pos).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spRoundAmount.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                amountRoundOff = Integer.parseInt(spRoundAmount
                        .getItemAtPosition(pos).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spWeighingBaud.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                WeighingScaleBaud = Integer.parseInt(spWeighingBaud
                        .getItemAtPosition(pos).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spPrinterBAud.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                printerBaud = Integer.parseInt(spPrinterBAud.getItemAtPosition(
                        pos).toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spMilkType.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                milkType = spMilkType.getItemAtPosition(pos).toString();

                getRateCahrtDetailsForMilktype(milkType);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spRatchart.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                rateChart = spRatchart.getItemAtPosition(arg2).toString();
                setDate(arg2);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spRDUbaudrate.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                rduBaudrate = Integer.parseInt(spRDUbaudrate.getItemAtPosition(
                        arg2).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spRDU.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                RDUtype = spRDU.getItemAtPosition(arg2).toString();
                if (RDUtype.equalsIgnoreCase("ESSAE")) {
                    lnSelectAmounAddedWater.setVisibility(View.VISIBLE);
                    rlSelectOption.setVisibility(View.VISIBLE);
                } else {
                    lnSelectAmounAddedWater.setVisibility(View.GONE);
                    rlSelectOption.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spMAbaudrate.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                maBaudrate = Integer.parseInt(spMAbaudrate.getItemAtPosition(
                        arg2).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spMA.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                MAtype = spMA.getItemAtPosition(arg2).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spSecondMik.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                maSecType = spSecondMik.getItemAtPosition(arg2).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spSecondMilkBaud
                .setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> arg0, View arg1,
                                               int pos, long arg3) {
                        maSecBaudRate = Integer.parseInt(spSecondMilkBaud
                                .getItemAtPosition(pos).toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                    }
                });


        alertProgressLayout.setVisibility(View.GONE);

        /*etFatCons.setKeyListener(DigitsKeyListener.getInstance(true, true));
        etSnfCons.setKeyListener(DigitsKeyListener.getInstance(true, true));
        etCons.setKeyListener(DigitsKeyListener.getInstance(true, true));*/

        /*etFatCons
                .addTextChangedListener(new CustomTextWatcher(etFatCons, 1, 2));
        etSnfCons
                .addTextChangedListener(new CustomTextWatcher(etSnfCons, 1, 1));
        etCons.addTextChangedListener(new CustomTextWatcher(etCons, 1, 2));*/

        isClrCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    isRateClr = true;
                } else {
                    isRateClr = false;
                }

            }
        });

        checkAmount.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isAddedWater = false;
                    checkAddedWater.setChecked(false);
                } else {
                    isAddedWater = true;
                    checkAddedWater.setChecked(true);
                }

            }
        });


        checkMaManual.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                isMaManual = isChecked;

            }
        });

        checkA4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                enableA4Print = isChecked;

            }
        });

        checkDisplayFS.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isDisplayFS = isChecked;
            }
        });

        checkEnableOrDisableSales.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });


        checkEnableSMS.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                isSMSEnable = isChecked;

            }
        });

        checkSimLock.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                isSimlockEnable = isChecked;

            }
        });
        checkWsManual.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                isWsManual = isChecked;

            }
        });

        checkRateImportViaExcel.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                isRateImportViaExcel = isChecked;

            }
        });

        checkRateAccess.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                isRateAccess = isChecked;

            }
        });

        checkBonusEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isBonusEnable = isChecked;

            }
        });


        checkMailToConfID.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                isMailSend = isChecked;

            }
        });

        checkAddedWater
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            isAddedWater = true;
                            checkAmount.setChecked(false);
                        } else {
                            isAddedWater = false;
                            checkAmount.setChecked(true);
                        }

                    }
                });

        isFatCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isRateFat = true;
                } else {
                    isRateFat = false;
                }
            }
        });

        isSnfCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isRateSnf = true;
                } else {
                    isRateSnf = false;
                }

            }
        });

        chkClr.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isClrChecked = true;
                } else {
                    isClrChecked = false;
                }
            }
        });

        chkRateRoundOff
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        isRateRoundCheck = isChecked;
                    }
                });

        chkAmountRoundOff
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        isAmountRoundCheck = isChecked;
                    }
                });

        chkWeightRoundOff
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        isWeightRoundCheck = isChecked;
                    }
                });

        chkSnf.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isSnfChecked = true;
                } else {
                    isSnfChecked = false;
                }
            }
        });

        chkFat.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isFatChecked = true;
                } else {
                    isFatChecked = false;
                }
            }
        });

        checkEditRatechart
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        isRateEditCheck = isChecked;

                    }
                });

        chkEM.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isEKOMILK = true;
                    chkLM2.setChecked(false);
                } else {

                    isEKOMILK = false;
                    chkLM2.setChecked(true);
                }

            }
        });

        chkLM2.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isEKOMILK = false;
                    chkEM.setChecked(false);
                } else {

                    isEKOMILK = true;
                    chkEM.setChecked(true);
                }

            }
        });


        checkMultipleMA.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                isMultipleMA = isChecked;

            }
        });

        etMorningStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                timePicker(MORNING_START_TIME);

            }
        });

        etEveningStart.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                timePicker(EVENING_START_TIME);

            }
        });

        etMorningEnd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                timePicker(MORNING_END_TIME);
            }
        });

        etEveningEnd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                timePicker(EVENING_END_TIME);
            }

        });

        chkSupport_unreg
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        isUnregister = isChecked;
                    }
                });

        chkSupport_keyPad
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        isKeyboard = isChecked;
                    }
                });


        btnAlertCancel.setOnClickListener(this);
        btnAlertSave.setOnClickListener(this);
        etBonusEndDate.setOnClickListener(this);
        etBonusStartDate.setOnClickListener(this);
        Util.alphabetValidation(etBonusRate, Util.ONLY_DECIMAL, SettingActivity.this, 5);
        Util.alphabetValidation(etConversion, Util.ONLY_DECIMAL, SettingActivity.this, 6);

        //For sales
        enableSalesVariable();

        Util.alphabetValidation(etCowRate, Util.ONLY_DECIMAL, SettingActivity.this, 5);
        Util.alphabetValidation(etBuffRate, Util.ONLY_DECIMAL, SettingActivity.this, 5);
        Util.alphabetValidation(etMixedRate, Util.ONLY_DECIMAL, SettingActivity.this, 5);

        initiliazeSpinner();
        setMaConfiguration();
        etFatCons.addTextChangedListener(new RegexTextWatcher(etFatCons, AppConstants.Regex.NUMBER_DECIMAL));
        etSnfCons.addTextChangedListener(new RegexTextWatcher(etSnfCons, AppConstants.Regex.NUMBER_DECIMAL));
        etCons.addTextChangedListener(new RegexTextWatcher(etCons, AppConstants.Regex.NUMBER_DECIMAL));
        etWeightLimit.addTextChangedListener(new RegexTextWatcher(etWeightLimit, AppConstants.Regex.NUMBER_DECIMAL));
        etFatBuff.addTextChangedListener(new RegexTextWatcher(etFatBuff, AppConstants.Regex.NUMBER_DECIMAL));
        etCowRate.addTextChangedListener(new RegexTextWatcher(etCowRate, AppConstants.Regex.NUMBER_DECIMAL));
        etBuffRate.addTextChangedListener(new RegexTextWatcher(etBuffRate, AppConstants.Regex.NUMBER_DECIMAL));
        etMixedRate.addTextChangedListener(new RegexTextWatcher(etMixedRate, AppConstants.Regex.NUMBER_DECIMAL));

    }

    private void setRadioSelectView() {
        String tempChilling = amcuConfig.getChillingFATSNFCLR();
        String tempCollection = amcuConfig.getCollectionFATSNFCLR();
        RadioButton radio_chilling_fatsnf, radio_chilling_fatclr, radio_fatsnf, radio_fatclr;

        if (tempChilling != null && tempChilling.equalsIgnoreCase("FS")) {
            radio_chilling_fatsnf = (RadioButton) findViewById(R.id.radio_chilling_fatsnf);
            radio_chilling_fatsnf.setChecked(true);
        } else

        {
            radio_chilling_fatclr = (RadioButton) findViewById(R.id.radio_chillinng_fatclr);
            radio_chilling_fatclr.setChecked(true);
        }
        if (tempCollection != null && tempCollection.equalsIgnoreCase("FS")) {
            radio_fatsnf = (RadioButton) findViewById(R.id.radio_fatsnf);
            radio_fatsnf.setChecked(true);
        } else {
            radio_fatclr = (RadioButton) findViewById(R.id.radio_fatclr);
            radio_fatclr.setChecked(true);
        }
    }

    private void selectRadioCenter(View view) {

        RadioGroup group = (RadioGroup) view;
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_chilling_fatsnf:
                        chillingFatSnfClr = "FS";
                        amcuConfig.setChillingFATSNFCLR(chillingFatSnfClr);
                        break;
                    case R.id.radio_chillinng_fatclr:
                        chillingFatSnfClr = "FC";
                        amcuConfig.setChillingFATSNFCLR(chillingFatSnfClr);
                        break;
                    case R.id.radio_fatclr:
                        collectionFatSnfClr = "FC";
                        amcuConfig.setCollectionFATSNFCLR(collectionFatSnfClr);
                        break;
                    case R.id.radio_fatsnf:
                        collectionFatSnfClr = "FS";
                        amcuConfig.setCollectionFATSNFCLR(collectionFatSnfClr);
                        break;
                }
            }
        });
    }

    private void checkAll(View view) {
        checkFATSNFCLR = (CheckBox) view;
        checkFATSNFCLR.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    amcuConfig.setFATSNFCLR(true);
                    checkFATSNFCLR.setChecked(true);
                } else {
                    amcuConfig.setFATSNFCLR(false);
                    checkFATSNFCLR.setChecked(false);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataFromSessionManager();
        buildBasedChange();
        getSettingEntity();
        configurationManager = new ConfigurationManager(SettingActivity.this);
        oldConfiguationEntity = ConfigurationConstants.getLatestConfiguration(SettingActivity.this);

        if (session.getUserRole().equalsIgnoreCase("Operator")) {
            setDisable(false);
        } else {
            setUneditableFields();
        }

    }

    private String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);

    }

    private void OnSaveData() {


        ValidationHelper validationHelper = new ValidationHelper();



        session.setEKOMILK(isEKOMILK);
        amcuConfig.setMorningEndTime(etMorningEnd.getText().toString()
                .replaceAll(" ", ""));
        amcuConfig.setEveningEndTime(etEveningEnd.getText().toString()
                .replace(" ", ""));
        amcuConfig.setMorningStart(etMorningStart.getText().toString()
                .replaceAll(" ", ""));
        amcuConfig.setEveningStart(etEveningStart.getText().toString()
                .replaceAll(" ", ""));


        if (etMessageLimit.getText().toString().replace(" ", "").length() > 0) {
            session.setPerDayMessageLimit(Integer.parseInt(etMessageLimit
                    .getText().toString()));
        }

        if (etMinClr.getText().toString().replace(" ", "").length() > 0) {

        }
        if (etMinSnf.getText().toString().replace(" ", "").length() > 0) {

        }
        if (etMinFat.getText().toString().replace(" ", "").length() > 0) {

        }


        if (milkType.equalsIgnoreCase("COW")
                && !rateChart.equalsIgnoreCase("No RateChart")) {
            amcuConfig.setRateChartForCow(rateChart);
        } else if (milkType.equalsIgnoreCase("BUFFALO")
                && !rateChart.equalsIgnoreCase("No RateChart")) {
            amcuConfig.setRateChartForBuffalo(rateChart);
        } else if (milkType.equalsIgnoreCase("MIXED")
                && !rateChart.equalsIgnoreCase("No RateChart")) {
            amcuConfig.setRateChartForMixed(rateChart);
        }

        amcuConfig.setCleaningAlertToBeDisplayed(true);
        amcuConfig.setFatCons(Double.parseDouble(etFatCons.getText()
                .toString()));
        amcuConfig.setSnfCons(Double.parseDouble(etSnfCons.getText()
                .toString()));
        amcuConfig
                .setConstant(Double.parseDouble(etCons.getText().toString()));
        amcuConfig.setRateChartName(rateChart);
        amcuConfig.setMA(MAtype);
        amcuConfig.setMABaudRate(maBaudrate);
        amcuConfig.setSecondMilkAnalyser(maSecType, maSecBaudRate);
        amcuConfig.setRDU(RDUtype);
        amcuConfig.setRdubaudrate(rduBaudrate);

        amcuConfig.setPrinter(PrinterType);
        amcuConfig.setPrinterBaudRate(printerBaud);
        amcuConfig.setDecimalRoundOffAmount(amountRoundOff);
        amcuConfig.setDecimalRoundOffRate(rateRoundOff);
        amcuConfig.setDecimalRoundOffWeigh(weighingroundOff);
        amcuConfig.setUSBHUB(GateWayType);
        amcuConfig.setMilkAnalyzersMilkType(ma1MilkType, ma2MilkType);

        amcuConfig.setDecimalRoundOffAmountCheck(isAmountRoundCheck);
        amcuConfig.setDecimalRoundOffRateCheck(isRateRoundCheck);
        amcuConfig.setDecimalRoundOffWeightCheck(isWeightRoundCheck);

        amcuConfig.setWeighingbaudrate(WeighingScaleBaud);
        amcuConfig.setWeighingScale(WeighingScaleType);
        amcuConfig.setIsAddedWater(isAddedWater);
        amcuConfig.setCreateAndEdit(isRateEditCheck);
        amcuConfig.setSendMailToConfigureEmailIDs(isMailSend, isRateImportViaExcel);
        //multiple ma
        amcuConfig.setCheckboxMultipleMA(isMultipleMA);

        if (!etFatBuff.getText().toString().equalsIgnoreCase("")) {

            String value = Util.convertStringtoDecimal(etFatBuff.getText().toString(), decimalFormatFS, "0");
            amcuConfig.setChangeFat(value);
        }
        if (!etConversion.getText().toString().equalsIgnoreCase("")) {
            String value = Util.convertStringtoDecimal(etConversion.getText().toString(), decimalFormatConversion, "1");
            amcuConfig.setConversionFactor(value);
        }

        amcuConfig.setChangeSnf(decimalFormatFS.format(Double
                .parseDouble(etSnfBuff.getText().toString())));

        amcuConfig.setShutDownDelay(Util.getLongFromTime(shutDownDealy));

        amcuConfig.setMaAndWsManualAndAllowSMS(checkMaManual.isChecked(),
                checkWsManual.isChecked(), isSMSEnable);
        amcuConfig.setAllowSimLockPassword(isSimlockEnable);
        amcuConfig.setBonusDetails(etBonusStartDate.getText().toString().replace(" ", ""),
                etBonusEndDate.getText().toString().replace(" ", ""), getFloatRate(), isBonusEnable, isRateAccess);

        String cowRate = validationHelper.getDoubleFromString(decimalFormat2Digit,
                etCowRate.getText().toString().trim());
        String buffRate = validationHelper.getDoubleFromString(decimalFormat2Digit,
                etBuffRate.getText().toString().trim());
        String mixRate = validationHelper.getDoubleFromString(decimalFormat2Digit,
                etMixedRate.getText().toString().trim());
        amcuConfig.setSalesData(cowRate
                , buffRate, isDisplayFS);
        amcuConfig.setSalesMixedRate(mixRate);

        amcuConfig.setSendMailToConfigureEmailIDs(amcuConfig.getSendEmailToConfigureIDs(),
                checkRateImportViaExcel.isChecked());

        amcuConfig.setEnableA4Printer(enableA4Print);
        amcuConfig.setEnableSales(checkEnableOrDisableSales.isChecked());
        amcuConfig.setDisableManualForDispatchValue(checkEnableOrDisableManual.isChecked());
        DecimalFormat decimalFormatConversion = new DecimalFormat("#0.0000");
        String value = Util.convertStringtoDecimal(etWeightLimit.getText().toString().trim(), decimalFormatConversion, "0");
        amcuConfig.setMaxLimitOfWeight(Double.valueOf(value));

        try {

            ArrayList<ConfigurationElement> allConfigPushList = configurationManager.getAllConfigPushList(
                    ConfigurationConstants.getLatestConfiguration(SettingActivity.this), oldConfiguationEntity);
            configurationManager.updateConfigurationPushList(allConfigPushList);

            // configurationManager.getSavedConfigurationList(configurationEntity);
            startService(new Intent(SettingActivity.this, ConfigurationPush.class));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getSettingEntity() {

        setSettingData();
    }

    private void setSettingData() {

        etFatBuff.setText(amcuConfig.getChangeFat());
        etSnfBuff.setText(amcuConfig.getChangeSnf());

        ArrayAdapter myAdap = (ArrayAdapter) spMA.getAdapter();
        int spinnerPosition = myAdap.getPosition(MAtype);

        spMA.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spMAbaudrate.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(maBaudrate));
        spMAbaudrate.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spSecondMik.getAdapter();
        spinnerPosition = myAdap.getPosition(maSecType);
        spSecondMik.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spSecondMilkBaud.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(maSecBaudRate));
        spSecondMilkBaud.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spRDU.getAdapter();
        spinnerPosition = myAdap.getPosition(RDUtype);
        spRDU.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spRDUbaudrate.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(rduBaudrate));
        spRDUbaudrate.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spWeighingScale.getAdapter();
        spinnerPosition = myAdap.getPosition(WeighingScaleType);
        spWeighingScale.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spWeighingBaud.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(WeighingScaleBaud));
        spWeighingBaud.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spPrinter.getAdapter();
        spinnerPosition = myAdap.getPosition(PrinterType);
        spPrinter.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spUSBGATEWAY.getAdapter();
        spinnerPosition = myAdap.getPosition(GateWayType);
        spUSBGATEWAY.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spPrinterBAud.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(printerBaud));
        spPrinterBAud.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spRoundWeight.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(weighingroundOff));
        spRoundWeight.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spRoundRate.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(rateRoundOff));
        spRoundRate.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spRoundAmount.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(amountRoundOff));
        spRoundAmount.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spMA1MilkType.getAdapter();
        spinnerPosition = myAdap.getPosition(ma1MilkType);
        spMA1MilkType.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spMA2MilkType.getAdapter();
        spinnerPosition = myAdap.getPosition(ma2MilkType);
        spMA2MilkType.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spChooseDelay.getAdapter();
        spinnerPosition = myAdap.getPosition(shutDownDealy);
        spChooseDelay.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spNumberOfDigit.getAdapter();
        spinnerPosition = myAdap.getPosition(farmDigitConstraints);
        spNumberOfDigit.setSelection(spinnerPosition);
        etMessageLimit.setText(String.valueOf(session.getMessageCount()));

            etEveningEnd.setText(amcuConfig.getEveningEndTime());
            etMorningEnd.setText(amcuConfig.getMorningEndTime());

            etMorningStart.setText(amcuConfig.getMorningStart());
            etEveningStart.setText(amcuConfig.getEveningStart());

            etFatCons.setText(String.valueOf(amcuConfig.getFatCons()));
            etSnfCons.setText(String.valueOf(amcuConfig.getSnfCons()));
            etCons.setText(String.valueOf(amcuConfig.getConstant()));
            etMessageLimit.setText(String.valueOf(session
                    .getPerDayMessageLimit()));

            if (session.getEKOMILK()) {
                isEKOMILK = true;
                chkEM.setChecked(true);
                chkLM2.setChecked(false);
            } else {
                isEKOMILK = false;
                chkEM.setChecked(false);
                chkLM2.setChecked(true);
            }

            if (session.getSupportUnregisterUser()) {
                isUnregister = true;
                chkSupport_unreg.setChecked(true);
            } else {
                isUnregister = false;
                chkSupport_unreg.setChecked(false);
            }

            if (session.getSupportKeyBoard()) {
                isKeyboard = true;
                chkSupport_keyPad.setChecked(true);
            } else {
                isKeyboard = false;
                chkSupport_keyPad.setChecked(false);
            }

        chkAmountRoundOff.setChecked(isAmountRoundCheck);
        chkRateRoundOff.setChecked(isRateRoundCheck);
        chkWeightRoundOff.setChecked(isWeightRoundCheck);
        checkAddedWater.setChecked(isAddedWater);
        checkEditRatechart.setChecked(amcuConfig.getCreateAndEdit());
        checkRateImportViaExcel.setChecked(amcuConfig.getImportExcelRateEnable());
        checkRateAccess.setChecked(amcuConfig.getOperatorRateAccess());
        checkMailToConfID.setChecked(isMailSend);
        checkWsManual.setChecked(amcuConfig.getWsManual());
        checkMaManual.setChecked(amcuConfig.getMaManual());
        checkEnableSMS.setChecked(amcuConfig.getAllowSMS());
        checkSimLock.setChecked(amcuConfig.getAllowSimlockPassword());
        checkA4.setChecked(amcuConfig.getEnableA4Printer());

        etBonusStartDate.setText(amcuConfig.getBonusStartDate());
        etBonusEndDate.setText(amcuConfig.getBonusEndDate());
        etBonusRate.setText(String.valueOf(amcuConfig.getBonusRate()));
        checkBonusEnable.setChecked(amcuConfig.getBonusEnable());
        isBonusEnable = amcuConfig.getBonusEnable();


        etBuffRate.setText(amcuConfig.getSalesBuffRate());
        etCowRate.setText(amcuConfig.getSalesCowRate());
        etMixedRate.setText(amcuConfig.getSalesMixedRate());
        checkDisplayFS.setChecked(amcuConfig.getSalesFSEnable());
        checkEnableOrDisableSales.setChecked(amcuConfig.getEnableSales());
        checkEnableOrDisableManual.setChecked(amcuConfig.getDisableManualForDispatchValue());

        etSnfCons.setText(String.valueOf(amcuConfig.getSnfCons()));
        etFatCons.setText(String.valueOf(amcuConfig.getFatCons()));
        etCons.setText(String.valueOf(amcuConfig.getConstant()));


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnSave:
                boolean valid = true;
                int sid = 0;
                try {
                    sid = Integer.parseInt(etSID.getText().toString().trim());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (sid > 0) {

                } else {
                    valid = false;
                    Toast.makeText(SettingActivity.this, "Please enter valid SID", Toast.LENGTH_SHORT).show();
                }
                if ((etMinWeightFill.getText().toString().trim().equalsIgnoreCase("")) && amcuConfig.getAllowVisiblityForCanToggle()) {
                    Toast.makeText(SettingActivity.this, "Please enter valid Empty Weight", Toast.LENGTH_SHORT).show();
                    valid = false;
                }
                if ((etMaxWeightEmpty.getText().toString().trim().equalsIgnoreCase("")) && amcuConfig.getAllowVisiblityForCanToggle()) {
                    Toast.makeText(SettingActivity.this, "Please enter valid Fill Weight", Toast.LENGTH_SHORT).show();
                    valid = false;
                }

                if (valid) {
                    if (validateTime() &&
                            Util.validateConversion(etConversion.getText().toString(), SettingActivity.this)
                            && (Integer.parseInt(farmDigitConstraints) == amcuConfig.getFarmerIdDigit())) {
                        onSave();
                        saveSID();
                        saveWeightToggle();
                        saveEditAndUpdateRecord();
                        saveMaConfiguration();
                        saveWSConfiguration();
                    } else if (Integer.parseInt(farmDigitConstraints) != amcuConfig.getFarmerIdDigit()) {
                        alertToChangeFarmerFormat();
                    }
                }
                break;
            case R.id.btnCancel:

                //For test
                //   importExcelConfiguration();
                finish();
                break;
            case R.id.etBonusEnd:
                setDateId = BONUS_END_DATE;
                showDialog(setDateId);
                break;
            case R.id.etBonusStart:
                setDateId = BONUS_START_DATE;
                showDialog(setDateId);
                break;

            default:
                break;
        }
    }

    private void onSave() {

        ValidationHelper validationHelper = new ValidationHelper();

        if ((validateBonusDate()) && validationHelper.validateMaxWeight(etWeightLimit.getText().toString()
                , SettingActivity.this)) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    OnSaveData();
                    myHandler.post(updateRunnable);
                }
            }).start();

            updateRunnable = new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(SettingActivity.this, "Setting saved.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            };
        } else if (!validateBonusDate()) {
            Toast.makeText(SettingActivity.this, "Please enter valid bonus date.", Toast.LENGTH_LONG).show();
        }
    }

    private void getRateCahrtDetailsForMilktype(String MilkType) {

        ArrayList<String> allRateChartName = new ArrayList<String>();
        try {
            if (amcuConfig.getMyRateChartEnable()) {
                allRateChartDetails = databaseHandler.getAllMyRateChartMilkTypeList(MilkType);
            } else {

                allRateChartDetails =
                        rateChartNameDao.findRateChartFromInputs(null, milkType, Util.RATECHART_TYPE_COLLECTION);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (allRateChartDetails != null && allRateChartDetails.size() > 0) {
            for (int pos = 0; pos < allRateChartDetails.size(); pos++) {
                allRateChartName
                        .add(allRateChartDetails.get(pos).rateChartName);
            }

            rateChart = allRateChartDetails.get(0).rateChartName;

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    SettingActivity.this, android.R.layout.simple_spinner_item,
                    allRateChartName);
            dataAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRatchart.setAdapter(dataAdapter);

            setSelection(MilkType);

        } else {
            allRateChartName = new ArrayList<String>();
            allRateChartName.add("No RateChart");
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
                    SettingActivity.this, android.R.layout.simple_spinner_item,
                    allRateChartName);
            dataAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spRatchart.setAdapter(dataAdapter);

            etValidFrom.setVisibility(View.GONE);
            etValidTill.setVisibility(View.GONE);
        }

    }

    private void saveEditAndUpdateRecord() {
        amcuConfig.setPermissionEditReport(setOperatorPermission);
    }

    private void saveWeightToggle() {
        amcuConfig.setMinLimitFilledCans(Integer.parseInt(etMinWeightFill.getText().toString().trim()));
        amcuConfig.setMaxLimitEmptyCans(Integer.parseInt(etMaxWeightEmpty.getText().toString().trim()));
        amcuConfig.setCansToggling(setToggelCan);
    }

    private void setDate(int pos) {

        if (allRateChartDetails != null
                && allRateChartDetails.size() > 0
                && allRateChartDetails.get(pos).rateChartName
                .equalsIgnoreCase(rateChart)
                && !rateChart.equalsIgnoreCase("No RateChart")) {
            etValidFrom.setVisibility(View.VISIBLE);
            etValidTill.setVisibility(View.VISIBLE);
            etValidFrom.setText(allRateChartDetails.get(pos).rateValidityFrom);
            etValidTill.setText(allRateChartDetails.get(pos).rateValidityTo);
            setRejFatAndSnf(rateChart);
        } else {
            setRejFatAndSnf(rateChart);
        }

    }

    private void setSelection(String milkType) {
        ArrayAdapter myAdap = (ArrayAdapter) spRatchart.getAdapter();
        int spinnerPosition = 0;
        if (milkType.equalsIgnoreCase("COW")) {

            spinnerPosition = myAdap.getPosition(amcuConfig
                    .getRateChartForCow());
            spRatchart.setSelection(spinnerPosition);

        } else if (milkType.equalsIgnoreCase("BUFFALO")) {

            myAdap = (ArrayAdapter) spRatchart.getAdapter();
            spinnerPosition = myAdap.getPosition(amcuConfig
                    .getRateChartForBuffalo());

        } else if (milkType.equalsIgnoreCase("MIXED")) {

            myAdap = (ArrayAdapter) spRatchart.getAdapter();

        }
    }

    private void setRejFatAndSnf(String rateChartName) {
        String minFat = "0.0", minSnf = "0.0";

        long refId = rateChartNameDao.findRateRefIdFromName(rateChartName);


        try {
            if (amcuConfig.getMyRateChartEnable()) {
                minFat = databaseHandler.getMyRateChartMinFATandSNF(1, rateChartName);
            } else {
              /*  minFat = dbh.getMinFATandSNFNew(
                        AppConstants.IS_FAT,
                        refId);*/
                minFat = rateDao.findMininumFat(null, refId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            if (amcuConfig.getMyRateChartEnable()) {
                minSnf = databaseHandler.getMyRateChartMinFATandSNF(0, rateChartName);
            } else {
               /* minSnf = dbh.getMinFATandSNFNew(
                        AppConstants.IS_SNF,
                        refId);*/
                minSnf = rateDao.findMinimumSNF(null, refId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close;

        minSnf = new ValidationHelper().getDoubleFromString(decimalFormatFS, minSnf, 0);
        minFat = new ValidationHelper().getDoubleFromString(decimalFormatFS, minFat, 0);

        String maxFat = "14.0", maxSnf = "14.0";
        refId = rateChartNameDao.findRateRefIdFromName(rateChartName);

        try {
            if (amcuConfig.getMyRateChartEnable()) {
                maxFat = databaseHandler.getMyRatechartMaxFATandSNF(1,
                        rateChartName);
            } else {
                // maxFat = dbh.getMaxFATandSNFNew(AppConstants.IS_FAT, refId);
                maxFat = rateDao.findMaximumFat(null, refId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (amcuConfig.getMyRateChartEnable()) {
                maxSnf = databaseHandler.getMyRatechartMaxFATandSNF(0,
                        rateChartName);
            } else {
               /* maxSnf = dbh.getMaxFATandSNFNew(AppConstants.IS_SNF,
                        refId);*/
                maxSnf = rateDao.findMaximumSNF(null, refId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        maxFat = new ValidationHelper().getDoubleFromString(decimalFormatFS, maxFat, 14);
        maxSnf = new ValidationHelper().getDoubleFromString(decimalFormatFS, maxSnf, 14);

        etMinFat.setText(minFat);
        etMinSnf.setText(minSnf);
        etMaxFat.setText(maxFat);
        etMaxSnf.setText(maxSnf);

    }

    private void setDisable(boolean isEnable) {
        etCons.setEnabled(isEnable);
        etEveningEnd.setEnabled(isEnable);
        etEveningStart.setEnabled(isEnable);
        etFatCons.setEnabled(isEnable);
        etMaxFat.setEnabled(isEnable);
        etMaxSnf.setEnabled(isEnable);
        etMessageLimit.setEnabled(isEnable);
        etMinClr.setEnabled(isEnable);
        etMinFat.setEnabled(isEnable);
        etMinSnf.setEnabled(isEnable);
        etMorningEnd.setEnabled(isEnable);
        etMorningStart.setEnabled(isEnable);
        etSnfCons.setEnabled(isEnable);
        etValidFrom.setEnabled(isEnable);
        etValidTill.setEnabled(isEnable);

        spMA.setEnabled(isEnable);
        spMAbaudrate.setEnabled(isEnable);
        spMilkType.setEnabled(isEnable);
        spRatchart.setEnabled(isEnable);
        spRDU.setEnabled(isEnable);
        spRDUbaudrate.setEnabled(isEnable);
        spPrinter.setEnabled(isEnable);
        spUSBGATEWAY.setEnabled(isEnable);
        spPrinterBAud.setEnabled(isEnable);
        spRoundAmount.setEnabled(isEnable);
        spRoundWeight.setEnabled(isEnable);
        spRoundRate.setEnabled(isEnable);
        spWeighingBaud.setEnabled(isEnable);
        spWeighingScale.setEnabled(isEnable);
        spMA1MilkType.setEnabled(isEnable);
        spMA2MilkType.setEnabled(isEnable);
        spSecondMik.setEnabled(isEnable);
        spSecondMilkBaud.setEnabled(isEnable);

        chkClr.setEnabled(isEnable);
        chkEM.setEnabled(isEnable);
        chkFat.setEnabled(isEnable);
        chkLM2.setEnabled(isEnable);
        chkMaxFat.setEnabled(isEnable);
        chkMaxSnf.setEnabled(isEnable);
        chkSnf.setEnabled(isEnable);
        chkSupport_keyPad.setEnabled(isEnable);
        chkSupport_unreg.setEnabled(isEnable);
        chkAmountRoundOff.setEnabled(isEnable);
        chkRateRoundOff.setEnabled(isEnable);
        chkWeightRoundOff.setEnabled(isEnable);
        checkMailToConfID.setEnabled(isEnable);
        checkRateImportViaExcel.setEnabled(isEnable);
        checkRateAccess.setEnabled(isEnable);
        checkAddedWater.setEnabled(isEnable);
        checkAmount.setEnabled(isEnable);
        checkMaManual.setEnabled(isEnable);
        checkWsManual.setEnabled(isEnable);
        checkEnableSMS.setEnabled(isEnable);
        checkSimLock.setEnabled(isEnable);

        checkA4.setEnabled(isEnable);


        btnAlertSave.setEnabled(isEnable);
        etFatBuff.setEnabled(isEnable);
        etSnfBuff.setEnabled(isEnable);
        spNumberOfDigit.setEnabled(isEnable);
        spChooseDelay.setEnabled(true);
        btnAlertCancel.setEnabled(true);
        btnAlertSave.setEnabled(true);

        etBonusEndDate.setEnabled(isEnable);
        etBonusStartDate.setEnabled(isEnable);
        etBonusRate.setEnabled(isEnable);
        checkBonusEnable.setEnabled(isEnable);
        etConversion.setFocusable(false);
        etConversion.setEnabled(false);

        etCowRate.setFocusable(isEnable);
        etCowRate.setEnabled(isEnable);
        etBuffRate.setFocusable(isEnable);
        etBuffRate.setEnabled(isEnable);
        etMixedRate.setFocusable(isEnable);
        etMixedRate.setEnabled(isEnable);
        checkDisplayFS.setEnabled(isEnable);
        checkDisplayFS.setFocusable(isEnable);
        checkFATSNFCLR.setEnabled(isEnable);
        checkEnableOrDisableSales.setEnabled(isEnable);
        checkEnableOrDisableSales.setFocusable(isEnable);
        checkEnableOrDisableManual.setEnabled(isEnable);
        checkEnableOrDisableManual.setEnabled(isEnable);


        radio_fatsnf.setEnabled(isEnable);
        radio_fatclr.setEnabled(isEnable);
        radio_chilling_fatsnf.setEnabled(isEnable);
        radio_chillinng_fatclr.setEnabled(isEnable);

        spMAParity.setEnabled(false);
        spMAStopBits.setEnabled(false);
        spMADataBits.setEnabled(false);
        etWeightLimit.setEnabled(false);

    }

    private void timePicker(final int time) {
        TimePickerDialog mTimePicker = new TimePickerDialog(SettingActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker,
                                          int selectedHour, int selectedMinute) {

                        String sH, sM;

                        sH = pad(selectedHour);
                        sM = pad(selectedMinute);

                        if (time == MORNING_START_TIME) {
                            etMorningStart.setText(sH + ":" + sM);
                        } else if (time == MORNING_END_TIME) {
                            etMorningEnd.setText(sH + ":" + sM);
                        } else if (time == EVENING_START_TIME) {
                            etEveningStart.setText(sH + ":" + sM);
                        } else {
                            etEveningEnd.setText(sH + ":" + sM);
                        }

                    }
                }, hour, minute, true);
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

        mTimePicker.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

    }

    private boolean validateTime() {
        int evnStart, mrnStart;

        evnStart = Integer.parseInt(etEveningStart.getText().toString()
                .replace(":", ""));
        mrnStart = Integer.parseInt(etMorningStart.getText().toString()
                .replace(":", ""));

        if (mrnStart >= evnStart) {
            Toast.makeText(SettingActivity.this,
                    "Morning start time should be less than Evening Start time!",
                    Toast.LENGTH_LONG).show();
            return false;

        } else {
            return true;
        }

    }

    private void getDataFromSessionManager() {
        maBaudrate = amcuConfig.getMABaudRate();
        MAtype = amcuConfig.getMA();

        maSecBaudRate = amcuConfig.getSecondMilkBaud();
        maSecType = amcuConfig.getSecondMilkAnalyser();

        isWeightRoundCheck = amcuConfig.getDecimalRoundOffWeightCheck();
        isAmountRoundCheck = amcuConfig.getDecimalRoundOffAmountCheck();
        isRateRoundCheck = amcuConfig.getDecimalRoundOffRateCheck();

        rduBaudrate = amcuConfig.getRdubaudrate();
        WeighingScaleBaud = amcuConfig.getWeighingbaudrate();
        WeighingScaleType = amcuConfig.getWeighingScale();
        RDUtype = amcuConfig.getRDU();

        printerBaud = amcuConfig.getPrinterBaudRate();
        PrinterType = amcuConfig.getPrinter();
        GateWayType = amcuConfig.getUSBHUB();
        isAddedWater = amcuConfig.getIsAddedWater();

        shutDownDealy = Util.getStringFromTime(amcuConfig.getShutDownDelay());

        farmDigitConstraints = String.valueOf(amcuConfig.getFarmerIdDigit());
        weighingroundOff = amcuConfig.getDecimalRoundOffWeigh();
        amountRoundOff = amcuConfig.getDecimalRoundOffAmount();
        rateRoundOff = amcuConfig.getDecimalRoundOffRate();
        ma1MilkType = "NONE";
        ma2MilkType = "NONE";
        isMailSend = amcuConfig.getSendEmailToConfigureIDs();
        isRateImportViaExcel = amcuConfig.getImportExcelRateEnable();
        isRateAccess = amcuConfig.getOperatorRateAccess();
        isWsManual = amcuConfig.getWsManual();
        isMaManual = amcuConfig.getMaManual();
        isSMSEnable = amcuConfig.getAllowSMS();
        isSimlockEnable = amcuConfig.getAllowSimlockPassword();

        etEveningEnd.setText(amcuConfig.getEveningEndTime());
        etMorningEnd.setText(amcuConfig.getMorningEndTime());

        etMorningStart.setText(amcuConfig.getMorningStart());
        etEveningStart.setText(amcuConfig.getEveningStart());
        etConversion.setText(String.valueOf(amcuConfig.getConversionFactor()));
        chkFat.setVisibility(View.GONE);
        chkSnf.setVisibility(View.GONE);
        chkMaxFat.setVisibility(View.GONE);
        chkMaxSnf.setVisibility(View.GONE);
        etWeightLimit.setText(String.valueOf(amcuConfig.getMaxlimitOfWeight()));

    }

    @Override
    protected Dialog onCreateDialog(int id) {

        Dialog dialog = null;
        if (id == DIALOG_LOAD_FILE) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Choose Farmer List");
            if (mFileList == null) {

                dialog = builder.create();
            }
            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mChosenFile = mFileList[which];

                    if (mChosenFile != null && mChosenFile.length() > 1) {
                        if (mChosenFile.contains(".xls")) {
                            fileName = fileName + "/" + mChosenFile;

                            Toast.makeText(SettingActivity.this,
                                    "Fetching farmers from " + mChosenFile,
                                    Toast.LENGTH_SHORT).show();
                            ReadExcelFile(fileName);
                            dialog.dismiss();

                        } else {
                            mPath = new File(Util.rootFileName + "/"
                                    + mChosenFile);
                            fileName = fileName + "/" + mChosenFile;
                            mFileList = new String[100];
                            loadFileList();
                            dialog.dismiss();
                        }

                    } else {
                        mFileList = new String[100];
                    }

                }
            });

            dialog = builder.show();
            return dialog;

        } else {
            return new DatePickerDialog(this, datePickerListener, year, month, day);
        }


    }

    private boolean validateBonusDate() {

        if (!isBonusEnable) {
            return true;
        } else {
//

            return true;
        }
    }

    private float getFloatRate() {
        try {
            float f = Float.parseFloat(etBonusRate.getText().toString());
            return f;
        } catch (NumberFormatException e) {

            e.printStackTrace();
            return 0.0f;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0f;
        }
    }

    private void buildBasedChange() {

        rlUnregistered.setVisibility(View.GONE);
        lnUnregistered.setVisibility(View.GONE);

        if (amcuConfig.getManualDisplay()) {
            rlMaManual.setVisibility(View.VISIBLE);
            rlManualEntry.setVisibility(View.VISIBLE);
            rlWsMaual.setVisibility(View.VISIBLE);
        } else {
            rlMaManual.setVisibility(View.GONE);
            rlManualEntry.setVisibility(View.GONE);
            rlWsMaual.setVisibility(View.GONE);
        }


        if (amcuConfig.getSimlockDisplay()) {
            rlSimlock.setVisibility(View.VISIBLE);
            rlAllowSimlock.setVisibility(View.VISIBLE);
        } else {
            rlSimlock.setVisibility(View.GONE);
            rlAllowSimlock.setVisibility(View.GONE);
        }


        if (amcuConfig.getEnableConversionFactorDisplay()) {
            rlConversion.setVisibility(View.VISIBLE);
            lnConversion.setVisibility(View.VISIBLE);
        } else {
            rlConversion.setVisibility(View.GONE);
            lnConversion.setVisibility(View.GONE);
        }

        if (amcuConfig.getBonusDisplay()) {
            rlBonusHeading.setVisibility(View.VISIBLE);
            //New bonus requirement this is not required
            lnBonusDate.setVisibility(View.GONE);
            lnBonusInfo.setVisibility(View.VISIBLE);
        } else {
            rlBonusHeading.setVisibility(View.GONE);
            lnBonusDate.setVisibility(View.GONE);
            lnBonusInfo.setVisibility(View.GONE);
        }

        if (amcuConfig.getEnableFarmerLenghtConfiguration()) {
            rlFarmerDigit.setVisibility(View.VISIBLE);
            lnFarmerDigit.setVisibility(View.VISIBLE);
        } else {
            rlFarmerDigit.setVisibility(View.GONE);
            lnFarmerDigit.setVisibility(View.GONE);
        }

    }

    private void alertToChangeFarmerFormat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);

        builder.setTitle("Farmer Id format alert!");

        if (amcuConfig.getCollectionEndShift() &&
                (Integer.parseInt(farmDigitConstraints) > amcuConfig.getFarmerIdDigit())) {
            builder.setMessage("Increasing the farmer Id format length from "
                    + amcuConfig.getFarmerIdDigit() + " to " + farmDigitConstraints + " press OK to delete all existing farmer and sample" +
                    " entities and apply the new farmer ID format!" + " Press cancel continue with existing format.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    Intent intent = new Intent(SettingActivity.this, UpdateFarmerService.class);
                    intent.putExtra("DigitLength", amcuConfig.getFarmerIdDigit());
                    amcuConfig.setFarmerIdDigit(Integer.parseInt(farmDigitConstraints));
                    onSave();
                    startService(intent);

                    dialog.dismiss();

                }
            });
        } else if (amcuConfig.getCollectionEndShift() &&
                (Integer.parseInt(farmDigitConstraints) < amcuConfig.getFarmerIdDigit())) {
            builder.setMessage("Reducing the farmer Id format length from " + amcuConfig.getFarmerIdDigit() + " to "
                    + farmDigitConstraints + " press OK to delete existing farmer and sample entities! " +
                    "Press cancel to continue with existing format.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {


                    Intent intent = new Intent(SettingActivity.this, UpdateFarmerService.class);
                    intent.putExtra("DigitLength", amcuConfig.getFarmerIdDigit());
                    amcuConfig.setFarmerIdDigit(Integer.parseInt(farmDigitConstraints));
                    onSave();
                    startService(intent);

                    dialog.dismiss();

                }
            });
        } else {
            builder.setMessage("Changed the farmer digit length can not be done in middle of the session." +
                    " Please do the end shift.");
        }
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = builder.create();

        // show it
        alertDialog.show();
    }

    private void setUneditableFields() {
        etBonusEndDate.setEnabled(true);
        etBonusEndDate.setFocusable(true);
        etBonusRate.setEnabled(true);
        etBonusRate.setFocusable(true);
        etBonusStartDate.setEnabled(true);
        etBonusEndDate.setEnabled(true);

        etConversion.setEnabled(false);
        etConversion.setFocusable(false);

        checkSimLock.setEnabled(false);
        checkSimLock.setFocusable(false);

        checkBonusEnable.setEnabled(true);
        checkBonusEnable.setFocusable(true);

//        checkMaManual.setEnabled(false);
//        checkMaManual.setFocusable(false);

//        checkWsManual.setEnabled(false);
//        checkWsManual.setFocusable(false);
    }

    private void loadFileList() {
        try {
            mPath.mkdirs();
        } catch (SecurityException e) {
        }
        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);

                    return filename.contains(FTYPE) || sel.isDirectory();
                }
            };
            mFileList = mPath.list(filter);
        } else {
            mFileList = new String[0];
        }

        onCreateDialog(DIALOG_LOAD_FILE);
    }

    private void ReadExcelFile(String file) {


        final ReadExcel test = new ReadExcel();
        File root = new File(Util.rootFileName);
        final File gpxfile = new File(root, file);

        test.setInputFile(gpxfile.toString(), 1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    treemap = test.readConfiguration(SettingActivity.this);
                    myHandler.post(updateRunnable);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        updateRunnable = new Runnable() {

            @Override
            public void run() {

                ConfigurationManager configMan = new ConfigurationManager(SettingActivity.this);


                if (treemap != null) {
                    Set set = treemap.entrySet();
                    Iterator iterator = set.iterator();

                    do {

                        Map.Entry mapEntry = (Map.Entry) iterator.next();
                        configMan.setDataInSaveSession(mapEntry.getKey().toString(), mapEntry.getValue().toString());
                    } while (iterator.hasNext());
                }

            }
        };
    }

    private void enableSalesVariable() {
        if (amcuConfig.getEnableSales()) {
            rlSales.setVisibility(View.VISIBLE);

            lnCowRate.setVisibility(View.VISIBLE);
            lnBuffRate.setVisibility(View.VISIBLE);
            lnDisplayFS.setVisibility(View.VISIBLE);
            lnSalesMixRate.setVisibility(View.VISIBLE);
        } else {
            rlSales.setVisibility(View.VISIBLE);

            lnCowRate.setVisibility(View.GONE);
            lnBuffRate.setVisibility(View.GONE);
            lnDisplayFS.setVisibility(View.GONE);
            lnSalesMixRate.setVisibility(View.GONE);
        }

    }

    //MA configuration changes

    private void saveSID() {
        //  etSID = (EditText)findViewById(R.id.etAlertSID);
        //  etSID.setText(""+saveSession.getLastSaveSID());
        int sid = 0;
        try {
            sid = Integer.parseInt(etSID.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (sid > 0) {
            if (amcuConfig.getLastSaveSID() == Integer.parseInt(etSID.getText().toString().trim())) {

            } else {
                amcuConfig.setLastSaveSID(Integer.parseInt(etSID.getText().toString().trim()));

            }
        } else {
            etSID.setText("1");
        }

    }

    private void initiliazeSpinner() {

        spMAParity = (Spinner) findViewById(R.id.spMaParity);
        spMADataBits = (Spinner) findViewById(R.id.spMaDatabits);
        spMAStopBits = (Spinner) findViewById(R.id.spMaStopbits);

        ArrayAdapter myAdap = (ArrayAdapter) spMAParity.getAdapter();
        int spinnerPosition = myAdap.getPosition(String.valueOf(amcuConfig.getMaParity()));
        spMAParity.setSelection(spinnerPosition);


        myAdap = (ArrayAdapter) spMADataBits.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(amcuConfig.getMaDataBits()));
        spMADataBits.setSelection(spinnerPosition);

        myAdap = (ArrayAdapter) spMAStopBits.getAdapter();
        spinnerPosition = myAdap.getPosition(String.valueOf(amcuConfig.getMaStopBits()));
        spMAStopBits.setSelection(spinnerPosition);
    }

    private void setMaConfiguration() {

        spMAParity.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                maParity = spMAParity.getItemAtPosition(
                        pos).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spMADataBits.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                maDataBits = spMADataBits.getItemAtPosition(
                        pos).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        spMAStopBits.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int pos,
                                       long arg3) {

                maStopBits = spMAStopBits.getItemAtPosition(
                        pos).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void saveMaConfiguration() {
        amcuConfig.setMaparity(maParity);
        amcuConfig.setMaDataBits(maDataBits);
        amcuConfig.setMaStopBits(maStopBits);

        amcuConfig.saveMA1Details(MAtype, maBaudrate);
        amcuConfig.setMa1parityStopAndDataBits(maParity, maStopBits, maDataBits);

    }

    private void updateWSPrefixAndSuffix() {
        etSuffix.setText(amcuConfig.getWeighingSuffix());
        etPreffix.setText(amcuConfig.getWeighingPrefix());
        etSeparator.setText(amcuConfig.getWeighingSeperator());
    }

    private void saveWSConfiguration() {
        amcuConfig.setWeighingPrefix(etPreffix.getText().toString());
        amcuConfig.setWeighingSuffix(etSuffix.getText().toString());
        amcuConfig.setWeighingSeparator(etSeparator.getText().toString());
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_PAGE_DOWN: {
                btnAlertSave.requestFocus();
            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
