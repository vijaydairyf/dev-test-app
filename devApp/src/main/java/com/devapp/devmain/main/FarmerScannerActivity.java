package com.devapp.devmain.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.devapp.kmfcommon.AllRateChartDetailsActivity;
import com.devapp.kmfcommon.AllReportsActivity;
import com.devapp.kmfcommon.PrintDuplicateActivity;
import com.devapp.kmfcommon.SelectCenterActivity;
import com.devapp.kmfcommon.SettingActivity;
import com.devapp.kmfcommon.TruckEntryActivity;
import com.devapp.kmfcommon.TruckEntryReportActivity;
import com.devapp.kmfcommon.UserMilkCollectionActivity;
import com.devapp.kmfcommon.UserSelectionActivity;
import com.devapp.kmfcommon.WeightCollectionActivityV2;
import com.devapp.devmain.DevAppApplication;
import com.devapp.devmain.Connectivity.ConnectivityCheck;
import com.devapp.devmain.additionalRecords.EditableRecordList;
import com.devapp.devmain.agentfarmersplit.AgentFarmerActivity;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.cloud.APKManager;
import com.devapp.devmain.cloud.UpdateAPK;
import com.devapp.devmain.dao.CollectionCenterDao;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.RateChartNameDao;
import com.devapp.devmain.deviceinfo.DeviceInfoActivity;
import com.devapp.devmain.encryption.EncryptedReportActivity;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.FactorEntity;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.RatechartDetailsEnt;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.entity.SampleDataEntity;
import com.devapp.devmain.entity.SocietyEntity;
import com.devapp.devmain.helper.AfterLogInTask;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.helper.EndShiftHelper;
import com.devapp.devmain.helper.FarmerScannerHelper;
import com.devapp.devmain.helper.UserType;
import com.devapp.devmain.httptasks.RateChartPullService;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
import com.devapp.devmain.macollection.CollectionActivity;
import com.devapp.devmain.macollection.MCCCollectionActivity;
import com.devapp.devmain.macollection.SalesActivity;
import com.devapp.devmain.macollection.SampleCollectionActivity;
import com.devapp.devmain.macollection.UpdatePastRecord;
import com.devapp.devmain.milkline.AdvanceConfiguration;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.rdu.RduFactory;
import com.devapp.devmain.rdu.RduManager;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.ServerAPI;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.services.PostEssaeCleaningDataService;
import com.devapp.devmain.user.AllChillingCenterDetails;
import com.devapp.devmain.user.AllFarmerData;
import com.devapp.devmain.user.AllSampleListActivity;
import com.devapp.devmain.user.AllUserActivity;
import com.devapp.devmain.user.CustomRateChartActivity;
import com.devapp.devmain.user.EnrollSocietyActivity;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.APIConstants;
import com.devapp.smartcc.AllAgentActivity;
import com.devapp.smartcc.EnterRejectDetails;
import com.devapp.smartcc.PortConfigurationActivity;
import com.devapp.smartcc.UpdateTruckDetails;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.report.FilterMccReportActivity;
import com.devapp.smartcc.report.FilterMemberReportActivity;
import com.devApp.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import static com.devapp.smartcc.entityandconstants.SmartCCConstants.ESSAE;
import static com.devapp.smartcc.entityandconstants.SmartCCConstants.PROBER;

public class FarmerScannerActivity extends Activity implements OnClickListener, SmartCCUtil.EndShiftListener {


    private static final int TIME = 1234;
    private static final String FTYPE_APK = ".apk";
    private static final int DIALOG_LOAD_APK = 1000;
    private static final int DIALOG_LOAD_XL = 999;
    private static final String FTYPE_XL = ".xls";
    public static boolean isResetEight;
    public static boolean backFromFarmer;
    public static boolean apkFromPendrive, isUpdateAvailable;
    public static Activity mActivity;
    private final int RESULT_CHANGE_SIMPIN = 101;
    private final Context context = DevAppApplication.getAmcuContext();
    String User;
    boolean isFarmer = false;
    String farmerAndCollection = null;
    int unsentCount = 0;
    private boolean isAuthenticated;
    private boolean farmOrBarcode = true, isRegistered = true;
    private boolean isDisable = true, isProcAlreadyDone = false;
    private String farmerName, farmerId, NumberOfCans, fileName;
    private boolean isConnected;
    private Runnable simRunnable;
    // For alert
    private TextView tvFarmerDetails, tvHeader, tvDeviceHeader;
    private EditText etBarcode;
    private Button btnSubmit, btnAutoOrManual, btnSalesOrCollection, btnRateChart, btnReports, btnAdvanceReport,
            btnConversionFact, btnSocietyInfo, btnUser, btnMyRate,
            btnSetting, btnEndShift,
            btnSampleTest, btnConfigPassword, btnCheckUpdate, btnPrintDuplicate, btnPrintShiftReport,
            btnUpdateValues, btnUpdateValuesHistory, btnSales, btnAdvanceConfig, btnCCReport;
    private AlertDialog alertDialog;
    private ArrayList<RatechartDetailsEnt> allDatabaseRateDetailsEnt;
    private LinearLayout lnButton, lnSocietyInfo, linearLayoutSales;
    private SessionManager session;
    private Handler myHandler = new Handler();
    private Runnable updateRunnable;
    private Button btnSociety, btnFarmer, btnMenu, btnDeviceInfo,
            btnInstallAPK, btnSimlockChange,
            btnSwitchDevice, btnCheckCon, btnDeviceSetting,
            btnAgentCollection, btnOfflineData, btnNddbShutDown, btnNddbLogout, btnnddbReboot, btnnddbDeviceList,
            btnNddbErp, btnNddbFileManager;
    private String MilkType, factor;
    private CheckBox chkUnreg;
    private LinearLayout lnButtonsSubmit;
    private RelativeLayout rlCheck, progressLayoutScan;
    private SocietyEntity societyEnt;
    private int min;
    private Calendar mcurrentTime = Calendar.getInstance();
    private int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
    private TextView tvUnregistered;
    private String isAutoOrManual;
    private View layoutView = null;
    //Added for sales
    private AmcuConfig amcuConfig;
    private RelativeLayout rlDisplayCollectionDetails;
    private TextView tvTotalFarmers, tvTotalRecords, tvTotalCollection, tvTotalUnsent;
    private RelativeLayout rlDisplayCenterDetails;
    private TextView tvCCCollections, tvCCNumbers;
    private ScrollView scrollView;
    private Spinner spCattleType;
    private boolean isSalesEnable;
    private LinearLayout lnCattleType;
    private TextView tvMemberH, tvCollectionH, tvTestRecordH;
    private Button btnTruckEntry, btnCenter, btnCenterCollection,
            btnTruckEntryReport, btnmultipleMA, btnDeviceStatus, btnUnsentRecords;
    private String oldServer;
    private int count = 0;
    private int installAttempt = 0;
    private Spinner spSelectMilkType;
    private String selectMilkType = "COW";
    private boolean isBoth = true;
    private Builder alertBuilderBoth;
    private String comingFrom, userID;
    private boolean isCenter;
    private TextToSpeech tts;
    private Button btnTruckList, btnAgentList, btnRejectEntry;
    private String currentShift, currentDate;
    private LinearLayout lnPendingTime;
    private TextView tvPendingTime;
    private TextView tvPendingTimeTitle;
    private Handler timehandler;
    private boolean collectionStatus = false;
    private SessionManager sessionManager;
    private Button btnEditCollection;
    private MaManager maManager;
    // In an Activity
    private String[] mFileList;
    private File mPath = new File(Util.getSDCardPath()
            + "");
    private String mChosenFile;
    private String TAG = "Farmer Scanner";
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour = hourOfDay;
            min = minute;
        }
    };
    private SmartCCUtil smartCCUtil;
    private final Runnable timeRunnable = new Runnable() {
        @Override
        public void run() {
            if (currentShift.equalsIgnoreCase(AppConstants.Shift.MORNING)) {

                if (System.currentTimeMillis() <
                        smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollStartMrnShift())) {
                    long time = smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollStartMrnShift())
                            - System.currentTimeMillis();
                    collectionStatus = false;
                    setMessageAndTime("Morning session will start at ", amcuConfig.getKeyCollStartMrnShift(), time);
                } else if (System.currentTimeMillis() > smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollStartMrnShift())
                        &&
                        System.currentTimeMillis() < smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift())) {
                    long seconds = (smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift())
                            - System.currentTimeMillis()) / 1000;
                    int mins = (int) (seconds / 60);
                    String time = String.format("%02d:%02d:%02d", mins / 60, mins % 60, seconds % 60);

                    collectionStatus = true;
                    setMessageAndTime("Time remaining to complete the morning session ", time, seconds);
                } else if (System.currentTimeMillis() > smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift())) {
                    long time = System.currentTimeMillis()
                            - smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollEndMrnShift());
                    triggerToSessionEnd("End of morning shift please wait!");
                    setMessageAndTime("Morning session has finished at ",
                            amcuConfig.getKeyCollEndMrnShift(), time);
                    collectionStatus = false;
                }
            } else {
                if (System.currentTimeMillis() <
                        smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollStartEvnShift())) {
                    long time = smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollStartEvnShift()) - System.currentTimeMillis();
                    collectionStatus = false;
                    setMessageAndTime("Evening session will start at ", amcuConfig.getKeyCollStartEvnShift(), time);
                } else if (System.currentTimeMillis() > smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollStartEvnShift())
                        &&
                        System.currentTimeMillis() < smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift())) {

                    long seconds = (smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift())
                            - System.currentTimeMillis()) / 1000;
                    int mins = (int) (seconds / 60);
                    String time = String.format("%02d:%02d:%02d", mins / 60, mins % 60, seconds % 60);
                    collectionStatus = true;
                    setMessageAndTime("Time remaining to complete the evening session ", time, seconds);
                } else if (System.currentTimeMillis() > smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift())) {
                    long time = System.currentTimeMillis()
                            - smartCCUtil.getstartOrShiftTime(amcuConfig.getKeyCollEndEvnShift());
                    triggerToSessionEnd("End of evening shift please wait!");

                    setMessageAndTime("Evening session has finished at ",
                            amcuConfig.getKeyCollEndEvnShift(), time);
                    collectionStatus = false;
                }
            }

            timehandler.postDelayed(timeRunnable, 1000L);
        }

    };
    private FarmerScannerHelper farmerScannerHelper;
    private String userType;
    private int entryType;
    private FarmerEntity farmerEntity;
    private CenterEntity mccEntity;
    private SampleDataEntity sampleDataEntity;
    private DatabaseHandler databaseHandler;
    private CollectionRecordDao collectionRecordDao;
    private boolean isCleaningInProgress = false;
    private Handler unsentHandler = new Handler();
    private Runnable unsentRunnable;

    private void initializeView(View view) {
        // Set view on initializing
        setContentView(view);
        // ///////////////////////////////////
        progressLayoutScan = (RelativeLayout) view
                .findViewById(R.id.progress_layout);
        progressLayoutScan.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        progressLayoutScan.setVisibility(View.GONE);
        // ///////////////////////////////////

        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnAutoOrManual = (Button) view.findViewById(R.id.btnEdit);
        btnRateChart = (Button) view.findViewById(R.id.btnLinear);
        etBarcode = (EditText) view.findViewById(R.id.etBarcode);
        tvFarmerDetails = (TextView) view.findViewById(R.id.tvFarmerDetails);
        tvUnregistered = (TextView) view.findViewById(R.id.tvRegistered);
        btnSalesOrCollection = (Button) view.findViewById(R.id.ivLogout);
        btnReports = (Button) view.findViewById(R.id.btnReports);
        btnAdvanceReport = (Button) view.findViewById(R.id.btnAdvanceReport);
        btnCCReport = (Button) view.findViewById(R.id.btnCCReport);
        btnSociety = (Button) view.findViewById(R.id.btnSociety);
        btnFarmer = (Button) view.findViewById(R.id.btnFarmer);
        btnMenu = (Button) view.findViewById(R.id.btnMenu);
        btnmultipleMA = (Button) view.findViewById(R.id.btnmultipleMA);
        btnCheckCon = (Button) view.findViewById(R.id.btnCheckCon);

        btnMyRate = (Button) findViewById(R.id.btnMyRate);
        btnSocietyInfo = (Button) view.findViewById(R.id.btnSocietyInfo);
        btnSales = (Button) view.findViewById(R.id.btnSales);
        btnUser = (Button) view.findViewById(R.id.btnUser);
        btnCenter = (Button) view.findViewById(R.id.btnCenter);
        btnCenterCollection = (Button) view.findViewById(R.id.btnCenterCollection);
        btnDeviceStatus = (Button) view.findViewById(R.id.btnDeviceStatus);
        btnUnsentRecords = (Button) view.findViewById(R.id.btnUnsentRecords);

        if (!Util.isOperator(this) && amcuConfig.getMyRateChartEnable()) {
            btnMyRate.setVisibility(view.VISIBLE);
        }

        btnSetting = (Button) view.findViewById(R.id.btnSetting);
        btnAdvanceConfig = (Button) view.findViewById(R.id.btnAdvanceConfig);
        btnDeviceSetting = (Button) view.findViewById(R.id.btnDeviceSetting);
        btnEndShift = (Button) view.findViewById(R.id.btnEndShift);
        btnTruckEntry = (Button) view.findViewById(R.id.btnTruck);
        btnTruckEntryReport = (Button) view.findViewById(R.id.btnTruckReport);
        btnCheckUpdate = (Button) view.findViewById(R.id.btnCheckForUpdate);
        btnPrintDuplicate = (Button) view.findViewById(R.id.btnPrintDuplicate);
        btnAgentCollection = (Button) view.findViewById(R.id.btnAgentCollection);
        btnEditCollection = (Button) view.findViewById(R.id.btnEditCollection);
        btnOfflineData = (Button) view.findViewById(R.id.btnOfflineData);
        btnConversionFact = (Button) view.findViewById(R.id.btnConversionFact);
        btnNddbLogout = (Button) view.findViewById(R.id.btnNDDBLogout);
        btnNddbShutDown = (Button) view.findViewById(R.id.btnNDDBShutDown);
        btnnddbReboot = (Button) view.findViewById(R.id.btnNDDBReboot);
        btnnddbDeviceList = (Button) view.findViewById(R.id.btnNDDBDeviceList);
        btnNddbErp = (Button) view.findViewById(R.id.btnNDDBErp);
        btnNddbFileManager = (Button) view.findViewById(R.id.btnNDDBFileManager);
        btnPrintShiftReport = (Button) view.findViewById(R.id.btnPrintShiftReport);
        //checking for chilling center record only
        if (amcuConfig.getEnableCenterCollection()) {
            btnPrintDuplicate.setVisibility(view.VISIBLE);

        } else {
            btnPrintDuplicate.setVisibility(view.GONE);
        }
        btnSampleTest = (Button) view.findViewById(R.id.btnSampleTest);
        btnDeviceInfo = (Button) view.findViewById(R.id.btnDeviceInfo);
        btnSimlockChange = (Button) view.findViewById(R.id.btnSimlock);
        btnConfigPassword = (Button) view
                .findViewById(R.id.btnConfigurePassword);
        btnInstallAPK = (Button) view.findViewById(R.id.btnInstallAPK);
        btnUpdateValues = (Button) view.findViewById(R.id.btnUpdateValues);
        btnUpdateValuesHistory = (Button) view.findViewById(R.id.btnUpdateValuesHistory);

        btnSwitchDevice = (Button) view.findViewById(R.id.btnSwitchDevice);

        if (!isOperator() || !amcuConfig.getAllowEquipmentBasedCollection()) {
            btnSwitchDevice.setVisibility(view.INVISIBLE);
        }
        if (null != session.getComingFrom())
            btnSwitchDevice.setText(" " + session.getComingFrom());


        lnButton = (LinearLayout) view.findViewById(R.id.lnButton);
        chkUnreg = (CheckBox) view.findViewById(R.id.checkregisterd);
        tvHeader = (TextView) view.findViewById(R.id.tvheader);
        lnButtonsSubmit = (LinearLayout) view.findViewById(R.id.linearLayout);
        lnSocietyInfo = (LinearLayout) view
                .findViewById(R.id.linearLayoutSociety);
        linearLayoutSales = (LinearLayout) view.findViewById(R.id.linearLayoutSales);
        lnPendingTime = (LinearLayout) view.findViewById(R.id.lnPendingTime);
        tvPendingTime = (TextView) view.findViewById(R.id.tvPendingTime);
        tvPendingTimeTitle = (TextView) view.findViewById(R.id.tvPendingHeader);


        rlCheck = (RelativeLayout) view.findViewById(R.id.rlUnreg);
        rlCheck.setVisibility(View.GONE);

        if (Util.isOperator(this)) {
            if (amcuConfig.getPermissionToEditReport() && amcuConfig.getAllowVisibilityReportEditing()) {
                btnUpdateValues.setVisibility(View.VISIBLE);
                btnUpdateValuesHistory.setVisibility(View.VISIBLE);
            }
        } else {
            if (amcuConfig.getAllowVisibilityReportEditing()) {

                btnUpdateValues.setVisibility(View.VISIBLE);
                btnUpdateValuesHistory.setVisibility(View.VISIBLE);
            }
        }
        btnUpdateValuesHistory.setVisibility(View.GONE);
        if (!Util.isOperator(this) && amcuConfig.getAllowVisibilityReportEditing()) {
            btnUpdateValues.setVisibility(View.VISIBLE);
        }
        //  btnUpdateValues.setVisibility(View.VISIBLE);
        spCattleType = (Spinner) view.findViewById(R.id.spCattleType);
        scrollView = (ScrollView) view.findViewById(R.id.scrollButton);
        lnCattleType = (LinearLayout) view.findViewById(R.id.lnMilkType);


        lnButton.setVisibility(View.GONE);
        tvFarmerDetails.setVisibility(View.GONE);

        btnSubmit.setText("Submit");
        btnAutoOrManual.setText("Auto");
        btnRateChart.setOnClickListener(this);
        session.setMilkReject("NO");

        btnUpdateValues.setOnClickListener(this);
        btnUpdateValuesHistory.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
        btnAutoOrManual.setOnClickListener(this);
        btnSalesOrCollection.setOnClickListener(this);
        btnReports.setOnClickListener(this);
        btnAdvanceReport.setOnClickListener(this);
        btnCCReport.setOnClickListener(this);
        btnSociety.setOnClickListener(this);
        btnFarmer.setOnClickListener(this);
        btnMenu.setOnClickListener(this);
        btnSocietyInfo.setOnClickListener(this);
        btnSales.setOnClickListener(this);
        btnSetting.setOnClickListener(this);
        btnAdvanceConfig.setOnClickListener(this);
        btnDeviceSetting.setOnClickListener(this);
        btnEndShift.setOnClickListener(this);

        btnSampleTest.setOnClickListener(this);
        btnDeviceInfo.setOnClickListener(this);
        btnSimlockChange.setOnClickListener(this);
        btnConfigPassword.setOnClickListener(this);
        btnInstallAPK.setOnClickListener(this);

        btnConversionFact.setOnClickListener(this);
        btnCheckUpdate.setOnClickListener(this);
        btnUser.setOnClickListener(this);
        btnCheckCon.setOnClickListener(this);

        btnTruckEntry.setOnClickListener(this);
        btnTruckEntryReport.setOnClickListener(this);

        btnCenter.setOnClickListener(this);
        btnCenterCollection.setOnClickListener(this);
        btnmultipleMA.setOnClickListener(this);
        btnMyRate.setOnClickListener(this);
        btnAgentCollection.setOnClickListener(this);
        btnEditCollection.setOnClickListener(this);
        btnDeviceStatus.setOnClickListener(this);
        btnUnsentRecords.setOnClickListener(this);
        btnOfflineData.setOnClickListener(this);
        btnNddbShutDown.setOnClickListener(this);
        btnNddbLogout.setOnClickListener(this);
        btnnddbReboot.setOnClickListener(this);
        btnnddbDeviceList.setOnClickListener(this);
        btnNddbErp.setOnClickListener(this);
        btnNddbFileManager.setOnClickListener(this);
        btnPrintShiftReport.setOnClickListener(this);
        showButton();
        if (amcuConfig.getEnableCenterCollection())
            btnPrintDuplicate.setOnClickListener(this);
        btnSwitchDevice.setOnClickListener(this);
        //For adding new details on window
        rlDisplayCollectionDetails = (RelativeLayout) view.findViewById(R.id.rlshiftDetailsInfor);
        rlDisplayCenterDetails = (RelativeLayout) view.findViewById(R.id.rlCenterShiftDetails);
        //Header
        tvCollectionH = (TextView) view.findViewById(R.id.tvCollection);
        tvMemberH = (TextView) view.findViewById(R.id.tvMember);
        tvTestRecordH = (TextView) view.findViewById(R.id.tvTestRecord);

        if (amcuConfig.getEnableCenterCollection() && isOperator()) {
            rlDisplayCenterDetails.setVisibility(View.VISIBLE);
        } else {
            rlDisplayCenterDetails.setVisibility(View.INVISIBLE);
        }
        tvMemberH.setText("Collections");
        tvCollectionH.setText("Quantity");

        tvTotalCollection = (TextView) view.findViewById(R.id.tvTotalCollections);
        tvTotalFarmers = (TextView) view.findViewById(R.id.tvTotalFarmers);
        tvTotalRecords = (TextView) view.findViewById(R.id.tvTotalRecords);
        tvTotalUnsent = (TextView) view.findViewById(R.id.tvTotalUnsent);
        tvCCNumbers = (TextView) view.findViewById(R.id.tvCenterCount);
        tvCCCollections = (TextView) view.findViewById(R.id.tvCenterCollectaion);


        etBarcode.requestFocus();

        etBarcode.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {

                    if (!checkValidSession())
                        return false;

                    onSubmit();
                    return true;
                }
                return false;
            }
        });

        etBarcode.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {


                if (etBarcode.getText().toString().trim().length() > 0 && !checkValidSession())
                    return;

                if (!farmOrBarcode) {
                    if (etBarcode.getText().toString().contains("STPL")) {
                        if (etBarcode.getText().toString().length() > 17) {
                            BarCodeAuthentication();
                        }
                    } else {
                        if (etBarcode.getText().toString().length() > 12) {
                            BarCodeAuthentication();
                        }
                    }

                }

            }
        });

        chkUnreg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    isRegistered = false;
                    etBarcode.setText("");
                    etBarcode.setHint("Press submit button to continue");
                    etBarcode.setEnabled(false);
                    btnAutoOrManual.setEnabled(false);
                } else {
                    isRegistered = true;
                    if (farmOrBarcode) {
                        if (session.getSelectedLanguage().equalsIgnoreCase(
                                "Kannada")) {
                            etBarcode.setHint(getResources().getString(
                                    R.string.enter_farmerk));
                        } else {
                            etBarcode.setHint("Enter id..");
                        }

                    } else {
                        if (session.getSelectedLanguage().equalsIgnoreCase(
                                "Kannada")) {
                            etBarcode.setHint(getResources().getString(
                                    R.string.enter_barcodek));
                        } else {
                            etBarcode.setHint("Enter  barcode..");
                        }
                    }
                    btnAutoOrManual.setEnabled(true);
                    etBarcode.setEnabled(true);
                }

            }
        });

        if (session.getSelectedLanguage().equalsIgnoreCase("Kannada")) {
            setKannada();
        }

        if (!session.getUserRole().equalsIgnoreCase("Operator")) {
            rlDisplayCollectionDetails.setVisibility(View.GONE);
            rlDisplayCollectionDetails.setVisibility(View.GONE);
            lnPendingTime.setVisibility(View.GONE);
        }
        tvHeader.setText(new SessionManager(FarmerScannerActivity.this)
                .getSocietyName());

        setSmartCCViews();

    }

    @Override
    protected void onStart() {
        super.onStart();
        smartCCUtil = new SmartCCUtil(FarmerScannerActivity.this);
        farmerScannerHelper = new FarmerScannerHelper(FarmerScannerActivity.this);

        initializeShiftAndDate();
        initializeTTS();

        isCenter = false;
        addMissingData();
        isUpdateAvailable = false;

        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(FarmerScannerActivity.this);

        if (lnPendingTime.getVisibility() == View.VISIBLE) {
            timehandler = new Handler();
            timehandler.postDelayed(timeRunnable, 1000L);
        }
        session.setIsChillingCenter(false);
        User = session.getUserRole();
        if (session.getSelectedLanguage().equalsIgnoreCase("Kannada")) {
            setKannada();
        }

        if (!LoginActivity.clientName.equalsIgnoreCase("MILMA")) {

            tvHeader.setText(new SessionManager(FarmerScannerActivity.this)
                    .getSocietyName());

            if (User.equalsIgnoreCase("Operator")) {
                forOperator();
            }
            addMsgData();
            visibilityForUser();
            updatePageInfo();

            Util.alphabetValidation(etBarcode, Util.ONLY_ALPHANUMERIC, FarmerScannerActivity.this, 0);
            btnSalesOrCollection.setVisibility(View.GONE);
        }
        setVisiblityForCenter();

        setVisiblityForAgentFarmerCollection();
        if (isEssae()) {
            initializeMAManager();
        }
    }

    private void initializeMAManager() {
        maManager = MAFactory.getMA(DeviceName.MILK_ANALYSER, this);
        if (maManager != null)
            maManager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(MilkAnalyserEntity maEntity) {

                }

                @Override
                public void onOtherMessage(String message) {
                    Log.v(PROBER, "MA Cleaning data in activity: " + message);
                    Intent i = new Intent(FarmerScannerActivity.this, PostEssaeCleaningDataService.class);
                    i.putExtra("data", message);
                    startService(i);
                    closeMAConnection();
                    isCleaningInProgress = false;
                }
            });
    }

    private void openMAConnection() {
        if (maManager != null)
            maManager.startReading();
    }

    private void closeMAConnection() {
        if (maManager != null) {
            maManager.stopReading();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!LoginActivity.clientName.equalsIgnoreCase("MILMA")) {
            tvFarmerDetails.setText("");
        }

        if (isCleaningInProgress) {
            closeMAConnection();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnMyRate:
                Intent myrateintent = new Intent(this, CustomRateChartActivity.class);
                startActivity(myrateintent);
                break;
            case R.id.btnCheckCon: {
                Intent intent = new Intent(FarmerScannerActivity.this, ConnectivityCheck.class);
                startActivity(intent);
            }
            break;
            case R.id.btnUpdateValues: {
                //if shift = 0 from sessionthen not to read the activity
                if (amcuConfig.getNumberShiftCanBeEdited() == 0) {
                    Toast.makeText(FarmerScannerActivity.this, "Permission denied,Please contact to Manager", Toast.LENGTH_SHORT).show();
                } else {
                    //  openChoiceAlertForCollectionorCenter();
                    Intent intent = new Intent(FarmerScannerActivity.this, AllReportsActivity.class);
                    //intent.putExtra("Choice", choice);
                    startActivity(intent);
                }
            }
            break;
            case R.id.btnUpdateValuesHistory: {
                Intent intent = new Intent(this, UpdatePastRecord.class);
                startActivity(intent);
                Toast.makeText(FarmerScannerActivity.this, "Update History", Toast.LENGTH_SHORT).show();
            }
            break;
            case R.id.btnSubmit: {
                onSubmit();
            }
            break;

            case R.id.btnFarmer: {
                Intent intentfarm = new Intent(FarmerScannerActivity.this,
                        AllFarmerData.class);
                startActivity(intentfarm);
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);


            }
            break;

            case R.id.btnUser: {

                if (session.getSocietyColumnId() != -1) {

                    Intent intent = new Intent(FarmerScannerActivity.this,
                            AllUserActivity.class);
                    intent.putExtra("SocietyCode",
                            String.valueOf(session.getSocietyColumnId()));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);

                } else {
                    Toast.makeText(FarmerScannerActivity.this,
                            "No society code found", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.btnSetting: {
                startActivity(new Intent(FarmerScannerActivity.this,
                        SettingActivity.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
            }
            break;

            case R.id.btnAdvanceConfig: {
                startActivity(new Intent(FarmerScannerActivity.this,
                        AdvanceConfiguration.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
            }
            break;

            case R.id.btnDeviceSetting: {
                startActivity(new Intent(FarmerScannerActivity.this,
                        PortConfigurationActivity.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
            }
            break;

            case R.id.btnSales: {
                Intent intent;
                if (isSalesEnable && isOperator()) {
                    session.setMilkType(MilkType);
                    session.setFarmerID("");
                    session.setFarmerName("");
                    double qty = Util.enableSales(FarmerScannerActivity.this, MilkType, null, null);

                    if (qty > 0) {
                        intent = new Intent(FarmerScannerActivity.this, SalesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                        finish();
                    } else {
                        Util.displayErrorToast("Currently no collection for " + MilkType,
                                FarmerScannerActivity.this);
                    }


                }
                break;
            }

            case R.id.btnSocietyInfo: {

                Intent intent;
                if (isSalesEnable && isOperator()) {
                    session.setMilkType(MilkType);
                    session.setFarmerID("");
                    session.setFarmerName("");
                    double qty = Util.enableSales(FarmerScannerActivity.this, MilkType, null, null);

                    if (qty > 0) {
                        intent = new Intent(FarmerScannerActivity.this, SalesActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_in_left,
                                R.anim.slide_out_left);
                        finish();
                    } else {
                        Util.displayErrorToast("Currently no collection for " + MilkType,
                                FarmerScannerActivity.this);
                    }


                } else {
                    intent = new Intent(FarmerScannerActivity.this,
                            EnrollSocietyActivity.class);
                    intent.putExtra("SocietyEntity", societyEnt);
                    intent.putExtra("isNew", false);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);
                    finish();
                }


            }
            break;
            case R.id.btnEdit: {
                ToggleButton();

            }
            break;

            case R.id.ivLogout:

                if (isOperator() && amcuConfig.getEnableSales()) {
                    salesFunction();
                } else {
                    Util.restartTab(FarmerScannerActivity.this);
                }


                break;

            case R.id.btnDeviceInfo:
                getDeviceInfo();
                break;

            case R.id.btnSimlock:
                alertSimlock();
                break;

            case R.id.btnConfigurePassword:

                setConfigPassword();
                break;

            case R.id.btnEndShift: {

                //This needs to be uncomment
                getCleaningDataFromMA();
                EndShiftHelper endShiftHelper = new EndShiftHelper(FarmerScannerActivity.this);
                endShiftHelper.doEndShift();

            }

            break;
            case R.id.btnLinear: {
                // To create ratechart
                startActivity(new Intent(FarmerScannerActivity.this,
                        AllRateChartDetailsActivity.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
                // alertRatechart();
            }
            break;
            case R.id.btnConversionFact: {
                alertFactor();
            }
            break;

            case R.id.btnReports: {
       /*         startActivity(new Intent(FarmerScannerActivity.this,
                        ReportsActivity.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);*/
                // Reports();
            }
            break;

            case R.id.btnAdvanceReport: {
                sessionManager.setMCCStatus(false);
                sessionManager.setRecordStatusComplete(true);

                startActivity(new Intent(FarmerScannerActivity.this,
                        FilterMemberReportActivity.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);
                // Reports();
            }
            break;

            case R.id.btnCCReport: {
                sessionManager.setMCCStatus(false);
                startActivity(new Intent(FarmerScannerActivity.this,
                        FilterMccReportActivity.class));
                overridePendingTransition(R.anim.slide_in_left,
                        R.anim.slide_out_left);

            }
            break;

            case R.id.btnCenter: {
                onStartAllCenter();
            }
            break;

            case R.id.btnCenterCollection: {
                openCenterAlert();
            }
            break;

            case R.id.btnDeviceStatus: {
                startActivity(new Intent(FarmerScannerActivity.this,
                        DeviceInfoActivity.class));

            }
            break;

            case R.id.btnUnsentRecords: {
                startActivity(new Intent(FarmerScannerActivity.this, UnsentRecordsActivity.class));
                break;
            }

            case R.id.btnCheckForUpdate: {

                Util.isCheckForUpdate = true;
                if (!isUpdateAvailable) {
                    checkForAnyUpdate();
                }

                ////////////////
                //  onPrintShiftReport()

            }
            break;

            case R.id.btnMenu: {
                // restartTab();
                onClickMenuButton();
                break;
            }

            case R.id.btnSampleTest:

            {

                if (session.getSocietyColumnId() != -1) {

                    Intent intent = new Intent(FarmerScannerActivity.this,
                            AllSampleListActivity.class);
                    intent.putExtra("SocietyCode",
                            String.valueOf(session.getSocietyColumnId()));
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_left,
                            R.anim.slide_out_left);

                } else {
                    Toast.makeText(FarmerScannerActivity.this,
                            "No society code found", Toast.LENGTH_SHORT).show();
                }

            }


            break;

            case R.id.btnInstallAPK: {
                installAPKFromPendrive();
            }
            break;

            case R.id.btnTruck: {
                goToTruckEntry();
                break;
            }
            case R.id.btnTruckReport: {
                gotoTruckEntryReport();
                break;
            }

            case R.id.btnSwitchDevice: {
                startActivity(new Intent(FarmerScannerActivity.this, UserSelectionActivity.class));
                session.setComingFrom(null);
                LoginActivity.clientName = "MILMA";
                finish();
            }
            break;

            case R.id.btnPrintDuplicate: {
                gotoPrintDuplicateActivity();
                break;
            }

            case R.id.btnmultipleMA: {
                //This activity is removed after Manual port configuration
                // startActivity(new Intent(FarmerScannerActivity.this, ConfigureMilkAnalyser.class));
            }
            break;

            case R.id.btnTruckList: {
                startActivity(new Intent(FarmerScannerActivity.this, UpdateTruckDetails.class));
            }
            break;

            case R.id.btnAgentList: {
                startActivity(new Intent(FarmerScannerActivity.this, AllAgentActivity.class));
            }
            break;

            case R.id.btnRejectEntry: {
                startActivity(new Intent(FarmerScannerActivity.this, EnterRejectDetails.class));
            }
            break;


            case R.id.btnAgentCollection:
                startActivity(new Intent(FarmerScannerActivity.this, AgentFarmerActivity.class));

                break;
            case R.id.btnOfflineData:
                startActivity(new Intent(FarmerScannerActivity.this,
                        EncryptedReportActivity.class));

                break;

            // navigate to Editcollection List
            case R.id.btnEditCollection: {
                if (amcuConfig.getNumberShiftCanBeEdited() > 0) {
                    startActivity(new Intent(FarmerScannerActivity.this, EditableRecordList.class));
                } else {
                    Util.displayErrorToast("Number of editable shift is not valid!", FarmerScannerActivity.this);
                }

                break;
            }

            case R.id.btnNDDBLogout: {
                Util.Logout(FarmerScannerActivity.this);
                break;
            }
            case R.id.btnNDDBShutDown: {
                Util.shutDownTab(FarmerScannerActivity.this,
                        FarmerScannerActivity.this);
                break;

            }
            case R.id.btnNDDBReboot: {
                Util.restartTab(FarmerScannerActivity.this);
                break;
            }
            case R.id.btnNDDBDeviceList: {
                Intent intent = new Intent(FarmerScannerActivity.this, DeviceListActivity.class);
                intent.putExtra("showDevices", true);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            }
            case R.id.btnNDDBErp: {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.stellapps.dairy");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                } else {
                    Util.displayErrorToast("Could not find ERP application", FarmerScannerActivity.this);
                }
                break;
            }
            case R.id.btnNDDBFileManager: {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.softwinner.explore");
                if (launchIntent != null) {
                    startActivity(launchIntent);//null pointer check in case package name was not found
                } else {
                    Util.displayErrorToast("Could not find FileManager", FarmerScannerActivity.this);
                }
                break;
            }
            case R.id.btnPrintShiftReport: {
                onPrintShiftReport();
                break;
            }

            default:
                break;
        }

    }

    private void onClickMenuButton() {

        if (isDisable) {
            sessionManager.setKeyIsMenuEnable(true);
            isDisable = false;
            lnButton.setVisibility(View.VISIBLE);
            rlDisplayCollectionDetails.setVisibility(View.GONE);
            lnPendingTime.setVisibility(View.GONE);

            if (amcuConfig.getEnableCenterCollection()) {
                rlDisplayCenterDetails.setVisibility(View.GONE);
            }

            etBarcode.setVisibility(View.GONE);
            btnAutoOrManual.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.GONE);
            btnCheckUpdate.setEnabled(true);
            if (amcuConfig.getEnableTruckEntry()) {
                // btnTruckEntry.setVisibility(View.VISIBLE);
            }
            if (amcuConfig.getEnableCenterCollection()) {
                btnCenter.setVisibility(View.VISIBLE);
                btnCenterCollection.setVisibility(View.GONE);
            }

            btnSalesOrCollection.setVisibility(View.GONE);


        } else {
            sessionManager.setKeyIsMenuEnable(false);
            isDisable = true;
            lnButton.setVisibility(View.GONE);

            if (amcuConfig.getEnableCenterCollection()) {
                rlDisplayCenterDetails.setVisibility(View.VISIBLE);
            }

            rlDisplayCollectionDetails.setVisibility(View.VISIBLE);
            if (amcuConfig.getKeyEnableCollectionConstraints()) {
                lnPendingTime.setVisibility(View.VISIBLE);
            }

            etBarcode.setVisibility(View.VISIBLE);
            btnAutoOrManual.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);

            btnCenterCollection.setVisibility(View.GONE);
            if (amcuConfig.getEnableSales()) {

                btnSalesOrCollection.setVisibility(View.VISIBLE);
            }

        }

    }

    private void setButton() {
        if (null != userID && userID.equals("SID")) {
            etBarcode.setHint("Enter Id..");
        } else {
            if (farmOrBarcode) {

                if (session.getSelectedLanguage().equalsIgnoreCase("Kannada")) {
                    btnAutoOrManual.setText(getResources().getString(
                            R.string.manualk));
                    etBarcode.setText("");
                    etBarcode.setHint(getResources().getString(
                            R.string.enter_farmerk));
                } else {
                    btnAutoOrManual.setText("Manual");
                    etBarcode.setText("");
                    etBarcode.setHint("Enter id..");
                    etBarcode.requestFocus();
                }

            } else {
                if (session.getSelectedLanguage().equalsIgnoreCase("Kannada")) {
                    btnAutoOrManual.setText(getResources()
                            .getString(R.string.autok));
                    etBarcode.setText("");
                    etBarcode.setHint(getResources().getString(
                            R.string.enter_barcodek));
                } else {
                    btnAutoOrManual.setText("Manual");
                    etBarcode.setText("");
                    etBarcode.setHint("Enter id..");
                }
            }

        }
        etBarcode.requestFocus();
    }

    private void ToggleButton() {
        if (!farmOrBarcode) {

            if (session.getSelectedLanguage().equalsIgnoreCase("Kannada")) {
                btnAutoOrManual.setText(getResources().getString(
                        R.string.manualk));
                etBarcode.setText("");
                etBarcode.setHint(getResources().getString(
                        R.string.enter_farmerk));
            } else {
                btnAutoOrManual.setText("Manual");
                etBarcode.setText("");
                etBarcode.setHint("Enter id..");
                etBarcode.requestFocus();
            }
            farmOrBarcode = true;

        } else {
            if (session.getSelectedLanguage().equalsIgnoreCase("Kannada")) {
                btnAutoOrManual.setText(getResources()
                        .getString(R.string.autok));
                etBarcode.setText("");
                etBarcode.setHint(getResources().getString(
                        R.string.enter_barcodek));
            } else {
                btnAutoOrManual.setText("Auto");
                etBarcode.setText("");
                etBarcode.setHint("Enter barcode..");
            }

            farmOrBarcode = false;
        }

        etBarcode.requestFocus();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        View view;
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            view = getLayoutInflater().inflate(R.layout.activity_scanner_land,
                    null);
        } else {
            view = getLayoutInflater().inflate(R.layout.activity_scanner, null);
        }

        initializeView(view);
        visibilityForUser();
        updatePageInfo();
        checkIfSalesEnable();
        if (amcuConfig.getSalesOrCollection().equalsIgnoreCase("Sales")) {
            salesFunction();
        }

    }

    private void getAllDatabaseRatechart() {
        RateChartNameDao rateChartNameDao = (RateChartNameDao) DaoFactory.getDao(CollectionConstants.RATECHART_NAME);
        allDatabaseRateDetailsEnt = new ArrayList<>();
        try {
            allDatabaseRateDetailsEnt = rateChartNameDao.findRateChartFromInputs(null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void BarCodeAuthentication() {

        if (etBarcode.getText().toString().contains("STPL")
                || etBarcode.getText().toString().contains("SIN")) {

            getFarmerDataFromDatabase();

        } else {

            etBarcode.setText("");
            etBarcode.requestFocus();
            etBarcode.setHint("Enter barcode..");
            Toast.makeText(FarmerScannerActivity.this, "Invalid barcode !!",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private void getFarmerDataFromDatabase() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                isProcAlreadyDone = false;
                entryType = farmerScannerHelper.isBarocodeOrId(etBarcode.getText().toString().trim());
                userType = farmerScannerHelper.getUserType(etBarcode.getText().toString().trim(), entryType);

                if (userType.equalsIgnoreCase(UserType.FARMER)) {
                    farmerEntity = (FarmerEntity) farmerScannerHelper.getUserDetailsFromUserType(
                            etBarcode.getText().toString().trim(), userType);
                } else if (userType.equalsIgnoreCase(UserType.CENTER)) {
                    mccEntity = (CenterEntity) farmerScannerHelper.getUserDetailsFromUserType(
                            etBarcode.getText().toString().trim(), userType);
                } else if (userType.equalsIgnoreCase(UserType.SAMPLE)) {
                    sampleDataEntity = (SampleDataEntity) farmerScannerHelper.getUserDetailsFromUserType(
                            etBarcode.getText().toString().trim(), userType);
                    sampleDataEntity.sampleMilkType = CattleType.COW;
                } else {
                    Util.displayErrorToast("Invalid entry ", FarmerScannerActivity.this);
                    return;
                }
                if (farmerEntity != null) {
                    isFarmer = true;
                    session.setIsChillingCenter(false);
                    if (!isMultipleCans()) {
                        try {
                            if (isBoth) {
                                isProcAlreadyDone = collectionRecordDao.findByFarmerIdDateShiftAndMilkType(
                                        farmerEntity.farmer_id, currentDate, currentShift, null) != null;
                            } else {
                                isProcAlreadyDone = collectionRecordDao.findByFarmerIdDateShiftAndMilkType(
                                        farmerEntity.farmer_id, currentDate, currentShift, selectMilkType) != null;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            //DB close removed;
                        }
                    }
                } else {
                    isFarmer = false;
                    isProcAlreadyDone = false;
                }
                myHandler.post(updateRunnable);

            }
        }).start();
        updateRunnable = new Runnable() {

            @Override
            public void run() {

                if (userType.equalsIgnoreCase(UserType.FARMER) &&
                        (isDefaultFarmer(etBarcode.getText().toString()) || isDefaultFarmer(farmerEntity.farmer_id))) {
                    farmerEntity.isReservedFarmer = true;
                    alertForMilkTypeBoth(etBarcode.getText().toString(), true, getIntentForCustomer(), true, false);
                } else if (userType.equalsIgnoreCase(UserType.FARMER) &&
                        farmerEntity.farmer_cattle.equalsIgnoreCase(CattleType.BOTH) && isBoth) {
                    alertForMilkTypeBoth(etBarcode
                            .getText().toString().replace(" ", ""), true, null, false, false);
                } else if (!isProcAlreadyDone && userType.equalsIgnoreCase(UserType.FARMER)) {
                    NextActivity();
                } else if (userType.equalsIgnoreCase(UserType.SAMPLE)) {
                    if (isSampleRecord(etBarcode.getText().toString()) || isSampleRecord(sampleDataEntity.sampleId))
                        alertForMilkTypeBoth(etBarcode.getText().toString(), true, getIntentForCustomer(), true, true);
                    else
                        ifSampleCode(sampleDataEntity);
                } else if (userType.equalsIgnoreCase(UserType.CENTER)) {
                    ifMccEntity(mccEntity);
                    session.setIsChillingCenter(true);
                } else {
                    etBarcode.requestFocus();
                    etBarcode.setText("");
                    Toast.makeText(
                            FarmerScannerActivity.this,
                            "For " + farmerEntity.farmer_id
                                    + " procurement is already done!",
                            Toast.LENGTH_SHORT).show();

                    if (!isBoth && alertDialog != null) {
                        isBoth = true;
                        alertDialog.dismiss();
                    }
                }
            }
        };
    }

    private boolean isSampleRecord(String id) {
        return (id.equals("999") || id.equals("0999"));
    }

    private boolean isDefaultFarmer(String id) {
        return (id.equals("9994") || id.equals("09994"));

    }

    private void NextActivity() {

        if (farmerEntity != null) {

            session.setUnregistered(false);

            farmerName = farmerEntity.farmer_name;
            farmerId = farmerEntity.farmer_id;
            NumberOfCans = farmerEntity.farmer_cans;
            String farmNumber = farmerEntity.farm_mob;

            session.setFarmerID(farmerId);
            session.setFarmerName(farmerName);
            session.setFarmerBarcode(farmerEntity.farmer_barcode);


            if (selectMilkType != null && farmerEntity.farmer_cattle != null &&
                    farmerEntity.farmer_cattle.equalsIgnoreCase(CattleType.BOTH)) {
                session.setMilkType(selectMilkType.toUpperCase(Locale.ENGLISH));
            } else if (farmerEntity.farmer_cattle != null
                    && !farmerEntity.farmer_cattle.equalsIgnoreCase("")) {
                session.setMilkType(farmerEntity.farmer_cattle
                        .toUpperCase(Locale.ENGLISH));
            } else {
                session.setMilkType(CattleType.COW);

            }
            setRateChart();
            if (farmNumber != null) {
                session.setFarmNumber("+91" + farmNumber);
            } else {
                session.setFarmNumber(farmNumber);
            }


            if (!farmOrBarcode) {
                isAutoOrManual = "Auto";
            } else {
                isAutoOrManual = "Manual";
            }

            if (amcuConfig.getRateChartName() != null) {
                checkForIntent();

            } else {
                etBarcode.requestFocus();
                Toast.makeText(FarmerScannerActivity.this,
                        "No rate chart available for milk type " + session.getMilkType() + "!", Toast.LENGTH_LONG).show();
            }
        } else if (sampleDataEntity != null) {

            session.setUnregistered(false);

            farmerId = sampleDataEntity.sampleId;
            farmerName = sampleDataEntity.sampleMode;

            session.setFarmerID(farmerId);
            session.setFarmerName(farmerName);
            session.setFarmerBarcode(sampleDataEntity.sampleBarcode);


            if (selectMilkType != null && sampleDataEntity.sampleMilkType != null &&
                    sampleDataEntity.sampleMilkType.equalsIgnoreCase(CattleType.BOTH)) {
                session.setMilkType(selectMilkType.toUpperCase(Locale.ENGLISH));
            } else if (sampleDataEntity.sampleMilkType != null
                    && !sampleDataEntity.sampleMilkType.equalsIgnoreCase("")) {
                session.setMilkType(sampleDataEntity.sampleMilkType
                        .toUpperCase(Locale.ENGLISH));
            } else {
                session.setMilkType(CattleType.COW);

            }
            setRateChart();
            ifSampleCode(sampleDataEntity);

            if (!farmOrBarcode) {
                isAutoOrManual = "Auto";
            } else {
                isAutoOrManual = "Manual";
            }

            if (amcuConfig.getRateChartName() != null) {
                checkForIntent();

            } else {
                etBarcode.requestFocus();
                Toast.makeText(FarmerScannerActivity.this,
                        "No rate chart available for milk type " + session.getMilkType() + "!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        APIConstants.AgentID = null;
        if (LoginActivity.clientName.equalsIgnoreCase("MILMA")
                && isOperator() && amcuConfig.getAllowEquipmentBasedCollection()) {

        } else {
            updatePageInfo();
            checkIfSalesEnable();
            if (amcuConfig.getSalesOrCollection().equalsIgnoreCase("Sales")) {
                salesFunction();
            }
        }
        if (amcuConfig.getCheckboxMultipleMA() && amcuConfig.getMultipleMA()) {
            // btnmultipleMA.setVisibility(View.VISIBLE);
        } else {
            btnmultipleMA.setVisibility(View.GONE);
        }
        initializeMAManager();

        if (sessionManager.getKeyIsMenuEnable()) {
            onClickMenuButton();
        }
    }

    @Override
    public void onEndShiftStart() {
        getCleaningDataFromMA();
    }

    private void visibilityForUser() {

        if (User.equalsIgnoreCase("Operator")) {

            btnMenu.setVisibility(View.VISIBLE);

            if (amcuConfig.getOperatorRateAccess()) {
                if (amcuConfig.getMyRateChartEnable()) {
                    btnRateChart.setVisibility(View.GONE);
                } else {
                    btnRateChart.setVisibility(View.VISIBLE);
                }
            } else {
                btnRateChart.setVisibility(View.GONE);

            }

            btnSociety.setVisibility(View.GONE);
            btnConversionFact.setVisibility(View.GONE);
            lnSocietyInfo.setVisibility(View.GONE);
            lnButtonsSubmit.setVisibility(View.VISIBLE);
            btnUser.setVisibility(View.GONE);
            btnDeviceInfo.setVisibility(View.GONE);

            btnSimlockChange.setVisibility(View.GONE);
            btnEndShift.setVisibility(View.VISIBLE);
            btnCheckUpdate.setVisibility(View.VISIBLE);
            btnSalesOrCollection.setVisibility(View.GONE);
            btnSampleTest.setVisibility(View.GONE);
            btnConfigPassword.setVisibility(View.GONE);
            btnInstallAPK.setVisibility(View.GONE);
            rlCheck.setVisibility(View.GONE);

            if (amcuConfig.getKeyEnableCollectionConstraints()) {
                lnPendingTime.setVisibility(View.VISIBLE);
            }


            if (amcuConfig.getEnableCenterCollection()) {
                rlDisplayCenterDetails.setVisibility(View.VISIBLE);
            }
            rlDisplayCenterDetails.setVisibility(View.GONE);

            if (amcuConfig.getEnableCenterCollection()) {
                btnCenterCollection.setVisibility(View.GONE);
            } else {
                btnCenterCollection.setVisibility(View.GONE);
            }
        } else if (User.equalsIgnoreCase("Manager")
                || User.equalsIgnoreCase("Admin")) {

            if (amcuConfig.getMyRateChartEnable()) {
                btnRateChart.setVisibility(View.GONE);
            } else {
                btnRateChart.setVisibility(View.VISIBLE);
            }
            btnMenu.setVisibility(View.GONE);

            if (new SessionManager(FarmerScannerActivity.this).getSocietyName() != null) {
                tvHeader.setText(new SessionManager(FarmerScannerActivity.this)
                        .getSocietyName());
            } else {
                tvHeader.setText("Welcome");
            }

            lnButton.setVisibility(View.VISIBLE);
            lnButtonsSubmit.setVisibility(View.GONE);
            lnSocietyInfo.setVisibility(View.VISIBLE);
            etBarcode.setVisibility(View.GONE);
            rlCheck.setVisibility(View.GONE);
            btnSociety.setVisibility(View.GONE);
            btnSalesOrCollection.setVisibility(View.GONE);
            btnEndShift.setVisibility(View.GONE);
            btnCheckUpdate.setVisibility(View.VISIBLE);
            if (amcuConfig.getSimlockDisplay() && amcuConfig.getAllowSimlockPassword()) {
                btnSimlockChange.setVisibility(View.VISIBLE);
            } else {
                btnSimlockChange.setVisibility(View.GONE);
            }

            //  lnButton.setGravity(1);
            rlDisplayCollectionDetails.setVisibility(View.GONE);
            rlDisplayCenterDetails.setVisibility(View.GONE);
            lnPendingTime.setVisibility(View.GONE);

        } else {
            btnMenu.setVisibility(View.VISIBLE);

            if (new SessionManager(FarmerScannerActivity.this).getSocietyName() != null) {
                tvHeader.setText(new SessionManager(FarmerScannerActivity.this)
                        .getSocietyName());
            } else {
                tvHeader.setText("Welcome");
            }

            lnButton.setVisibility(View.VISIBLE);
            btnRateChart.setVisibility(View.GONE);
            btnSociety.setVisibility(View.GONE);
            btnConversionFact.setVisibility(View.GONE);
            rlDisplayCollectionDetails.setVisibility(View.GONE);
            rlDisplayCenterDetails.setVisibility(View.GONE);
            lnPendingTime.setVisibility(View.GONE);

        }

    }

    private void ShowKeyboard() {
        if (getResources().getConfiguration().hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showInputMethodPicker();
        }
    }

    private void addMsgData() {
        new Handler().post(new Runnable() {

            @Override
            public void run() {
                Util.createMsgTable(FarmerScannerActivity.this,
                        String.valueOf(session.getSocietyColumnId()));

            }
        });
    }

    private void alertFactor() {

        Builder builder = new Builder(
                FarmerScannerActivity.this);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alert_conversion_factor, null);

        final Spinner spinner = (Spinner) view.findViewById(R.id.spMilk);
        EditText etLit = (EditText) view.findViewById(R.id.etLiters);
        final EditText etKg = (EditText) view.findViewById(R.id.etKG);

        TextView tvSoceName = (TextView) view.findViewById(R.id.tvSocietyName);

        tvSoceName.setText(session.getSocietyName());

        Button btnSave = (Button) view.findViewById(R.id.btnSubmit);
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        etLit.setEnabled(false);
        MilkType = "Cow";

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                MilkType = spinner.getItemAtPosition(arg2).toString();
                factor = getKgfactor(MilkType);
                if (factor != null) {
                    etKg.setText(factor + " Kg");
                } else {
                    etKg.setText("");
                    etKg.setHint("Equivalent Kg...");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                new Handler().post(new Runnable() {

                    @Override
                    public void run() {
                        DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance(
                        );
                        FactorEntity factEnt = new FactorEntity();
                        factEnt.cattleType = MilkType;
                        factEnt.socId = String.valueOf(session
                                .getSocietyColumnId());
                        factEnt.kgFactor = etKg.getText().toString();
                        try {
                            dbh.insertKGFACOTR(factEnt);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                Toast.makeText(FarmerScannerActivity.this,
                        "Conversion factor saved for the society!",
                        Toast.LENGTH_SHORT).show();
            }
        });
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });
        alertDialog = builder.create();
        alertDialog.setView(view);
        alertDialog.show();

    }

    private String getKgfactor(String milkType) {
        String factor = null;
        try {
            factor = databaseHandler.getKGfactor(
                    String.valueOf(session.getSocietyColumnId()), milkType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Removed database close;
        return factor;
    }

    private void loadFileList(final int reqCode) {
        try {
            mPath.mkdirs();
        } catch (SecurityException e) {
            Log.e(TAG, "unable to write on the sd card " + e.toString());
        }
        if (mPath.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);

                    if (reqCode == DIALOG_LOAD_APK) {
                        return filename.contains(FTYPE_APK) || sel.isDirectory();
                    } else if (reqCode == DIALOG_LOAD_XL) {
                        return filename.contains(FTYPE_XL) || sel.isDirectory();
                    } else {
                        return sel.isDirectory();
                    }


                }
            };
            mFileList = mPath.list(filter);
        } else {
            mFileList = new String[0];
        }

        onCreateDialog(reqCode);
    }

    @Override
    protected Dialog onCreateDialog(final int id) {

        if (id == DIALOG_LOAD_APK) {
            Dialog dialog;
            Builder builder = new Builder(this);
            builder.setTitle("Choose smartAmcu application");
            if (mFileList == null) {
                Log.e(TAG, "Showing file picker before loading the file list");
            }
            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mChosenFile = mFileList[which];

                    if (mChosenFile != null && mChosenFile.length() > 1) {
                        if (mChosenFile.contains(".apk")) {
                            fileName = Util.rootFileName + fileName + "/" + mChosenFile;
                            apkFromPendrive = true;
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        final String apkFile = Util.getTheCopiedFile(fileName);
                                        UpdateAPK.installDownload(apkFile, FarmerScannerActivity.this);

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    myHandler.post(updateRunnable);
                                }
                            }).start();

                            dialog.dismiss();
                            Toast.makeText(FarmerScannerActivity.this,
                                    "Application is being updated with " + mChosenFile,
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mPath = new File(Util.rootFileName
                                    + "/"
                                    + mChosenFile);
                            fileName = fileName + "/" + mChosenFile;
                            mFileList = new String[100];
                            loadFileList(id);
                            dialog.dismiss();
                        }

                    } else {
                        mFileList = new String[100];
                    }

                }
            });

            dialog = builder.show();
            final Dialog finalDialog = dialog;
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        finalDialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            return dialog;
        } else if (id == DIALOG_LOAD_XL) {

            Dialog dialog = null;
            Builder builder = new Builder(this);
            builder.setTitle("Choose file");
            if (mFileList == null) {
                dialog = builder.create();
            }
            builder.setItems(mFileList, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    mChosenFile = mFileList[which];

                    if (mChosenFile != null && mChosenFile.length() > 1) {
                        if (mChosenFile.contains(".xls") || mChosenFile.contains(".txt") || mChosenFile.contains(".pdf")) {
                            fileName = Util.rootFileName + fileName + "/" + mChosenFile;
                            printViaShareIntent(fileName);

                            dialog.dismiss();

                        } else {
                            mPath = new File(Util.rootFileName
                                    + "/"
                                    + mChosenFile);
                            fileName = fileName + "/" + mChosenFile;
                            mFileList = new String[100];
                            loadFileList(id);
                            dialog.dismiss();
                        }

                    } else {
                        mFileList = new String[100];
                    }

                }
            });

            dialog = builder.show();
            final Dialog finalDialog = dialog;
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_DEL && keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        finalDialog.dismiss();
                        return true;
                    }
                    return false;
                }
            });
            return dialog;

        } else if (id == TIME) {
            return new TimePickerDialog(this, timePickerListener, hour, min,
                    true);
        } else {
            return new TimePickerDialog(this, timePickerListener, hour, min,
                    true);
        }
    }

    @Override
    public void onBackPressed() {
        isBoth = true;
        if (!session.getUserRole().equalsIgnoreCase("operator")) {

            //  super.onBackPressed();
        } else {
            btnCheckUpdate.setEnabled(true);
            updatePageInfo();
            sessionManager.setKeyIsMenuEnable(false);
        }
    }

    private void setKannada() {
        btnMenu.setText(getResources().getString(R.string.menuk));
        etBarcode.setHint(getResources().getString(R.string.enter_barcodek));
        tvUnregistered.setText(getResources().getString(
                R.string.unregistered_userk));
        btnSubmit.setText(getResources().getString(R.string.submitk));
        btnAutoOrManual.setText(getResources().getString(R.string.autok));
        btnFarmer.setText(getResources().getString(R.string.all_farmersk));
        btnReports.setText(getResources().getString(R.string.reportsk));

    }

    private void checkRDU() {

        RduManager rduManager = RduFactory.getRdu(amcuConfig.getRDU(), FarmerScannerActivity.this);
        if (rduManager != null) {
            rduManager.openConnection();
            rduManager.resetRdu(isResetEight);
            rduManager.closePort();
            isResetEight = !isResetEight;
        } else {
            Toast.makeText(FarmerScannerActivity.this,
                    "Invalid RDU configured!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timehandler.removeCallbacks(timeRunnable);
    }

    private void onSubmit() {

        boolean isValidTime = smartCCUtil.isValidTabTime();
        if (!isValidTime) {

        } else if (!checkValidSession()) {
            return;
        } else if (null != userID && userID.equalsIgnoreCase("SID")) {

            if (etBarcode.getText().toString().trim().length() > 0) {
                boolean num = Util.isNumeric(etBarcode.getText().toString().trim());
                if (num) {
                    try {

                        boolean isSIDAvailable = databaseHandler.sequenceNumberValidation(
                                Integer.valueOf(etBarcode.getText().toString().trim()), currentDate, currentShift);
                        if (isSIDAvailable) {
                            session.setIsChillingCenter(true);
                           /* Intent intent = getIntentForCustomer();
                            onStartMilkCollectionActivity(intent);*/
                            goBySequenceId();
                        } else {
                            Toast.makeText(FarmerScannerActivity.this, "No SID record available.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(FarmerScannerActivity.this, "Please Enter Valid SID.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(FarmerScannerActivity.this, "Please Enter Numeric SID.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(FarmerScannerActivity.this, "Please Enter Valid SID.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // for otheres
            isCenter = false;
            if (!farmOrBarcode) {
                isAutoOrManual = "Auto";
            } else {
                isAutoOrManual = "Manual";
            }

            if ((etBarcode.getText().toString().equalsIgnoreCase("9999")
                    || etBarcode.getText().toString().equalsIgnoreCase("09999")
                    || etBarcode.getText().toString().equalsIgnoreCase("009999"))) {
                test_F8_forSampleMilk();
            } else if (etBarcode.getText().toString().equalsIgnoreCase("9999")) {
                test_F8_forSampleMilk();
            } else if (etBarcode.getText().toString().equalsIgnoreCase("9996")) {
                etBarcode.setText("");
                cleanNow();
            } else {
                if (isRegistered) {
                    if (farmOrBarcode || !isBoth) {
                        session.setUnregistered(false);
                        session.setIsSample(false);
                        // for checking valid farmer id and collection id or center id
                        getFarmerDataFromDatabase();
                    }

                } else {
                    // RESETZERO();
                    session.setUnregistered(true);
                    session.setFarmerID("");
                    session.setFarmerName("");
                    session.setFarmerBarcode("");
                    session.setFarmNumber("+91" + "9611849076");

                    if (allDatabaseRateDetailsEnt != null
                            && allDatabaseRateDetailsEnt.size() > 0) {

                        Intent intent = getIntentForCustomer();
                        onStartMilkCollectionActivity(intent);
                    } else {
                        Toast.makeText(FarmerScannerActivity.this,
                                "No rate chart available", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_F1:

                if (Util.checkForPendrive()) {
                    AfterLogInTask afterLogInTask = new AfterLogInTask(FarmerScannerActivity.this);
                    if (afterLogInTask.checkForUnSentRecords()) {
                        afterLogInTask.createAndSendUnsentRecords();
                    }
                    if (afterLogInTask.unsentCleaningRecordsAvailable()) {
                        afterLogInTask.sendUnsentCleaningRecords();
                    }
                } else {
                    Util.displayErrorToast("Please connect the pendrive to save the Unsent records!",
                            FarmerScannerActivity.this);
                }

                return true;
            case KeyEvent.KEYCODE_F2:
                if (isOperator()) {
                    getCleaningDataFromMA();
                    EndShiftHelper endShiftHelper = new EndShiftHelper(FarmerScannerActivity.this);
                    endShiftHelper.doEndShift();
                }

                // checkForPenDrive();
                return true;
            case KeyEvent.KEYCODE_F3:

                if (isOperator()) {

                    try {
                        onPrintShiftReport();
                        getCleaningDataFromMA();
                        EndShiftHelper endShiftHelper = new EndShiftHelper(FarmerScannerActivity.this);
                        endShiftHelper.doEndShift();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {


                    }
                }
                // checkForPenDrive();
                return true;

            case KeyEvent.KEYCODE_F4:

                if (isOperator()) {

                    onPrintShiftReport();

                }
                return true;

            case KeyEvent.KEYCODE_F5:
                if (isOperator() && (null == userID || (!userID.equals("SID")))) {
                    ToggleButton();
                }

                return true;
            case KeyEvent.KEYCODE_F6:

                if (amcuConfig.getEnableSales() && isOperator() && isDisable) {
                    salesFunction();
                }

                return true;

            case KeyEvent.KEYCODE_F7:
                if (isOperator()) {
                    checkRDU();
                }
                return true;

            case KeyEvent.KEYCODE_F8:
                if ((isOperator() &&
                        (null == userID || (!userID.equals("SID"))))) {
                    if (amcuConfig.getKeyEnableCollectionConstraints() && !collectionStatus) {
                        Util.displayErrorToast("Please check the collection time", FarmerScannerActivity.this);
                    } else {
                        test_F8_forSampleMilk();
                    }
                }

                return true;

            case KeyEvent.KEYCODE_F9:
                Util.Logout(FarmerScannerActivity.this);
                return true;

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(FarmerScannerActivity.this);
                return true;

            case KeyEvent.KEYCODE_F11:

                Util.restartTab(FarmerScannerActivity.this);
                return true;

            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(FarmerScannerActivity.this, FarmerScannerActivity.this);
                return true;
            case KeyEvent.KEYCODE_HOME:
                Toast.makeText(FarmerScannerActivity.this, "Home button pressed!", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_MENU:
                Toast.makeText(FarmerScannerActivity.this, "Menu button pressed!", Toast.LENGTH_SHORT).show();
                return true;

            case KeyEvent.KEYCODE_ALT_LEFT + KeyEvent.KEYCODE_C: {
                startActivity(new Intent(FarmerScannerActivity.this, UserSelectionActivity.class));
                session.setComingFrom(null);
                LoginActivity.clientName = "MILMA";
                finish();
            }
            return true;

            case KeyEvent.KEYCODE_DEL:

                if (!isDisable) {
                    onClickMenuButton();
                }
//               else if (!etBarcode.hasFocus()) {
//                    Util.Logout(FarmerScannerActivity.this);
//                }
                return true;

            case KeyEvent.KEYCODE_PAGE_UP: {
                btnMenu.requestFocus();
                return true;
            }
            case KeyEvent.KEYCODE_PAGE_DOWN: {
                if (isDisable) {
                    btnSubmit.requestFocus();
                }
                return true;
            }
            case KeyEvent.KEYCODE_S:


                return true;

            default:
                return super.onKeyUp(keyCode, event);
        }
    }

    private void displayMACleaningAlert(Context context, boolean mandatory) {

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_sendingmail, null);

        TextView tvMailHeader = (TextView) view.findViewById(R.id.tvheader);
        TextView tvAlertText = (TextView) view.findViewById(R.id.tvAlert);
        ImageView ivLogo = (ImageView) view.findViewById(R.id.ivsplash);
        ivLogo.setVisibility(View.GONE);
        RelativeLayout ProgressL = (RelativeLayout) view
                .findViewById(R.id.progress_layout);
        ProgressL.setVisibility(View.GONE);
        tvAlertText.setVisibility(View.VISIBLE);

        tvMailHeader.setText("MA Cleaning Required!");
        // TODO
        String alertText = "Press OK to start MA cleaning process.";

        tvAlertText.setText(alertText);
        tvAlertText.setTextSize(23f);

        final Button btnResend = (Button) view.findViewById(R.id.btnResend);
        final Button btnCancel = (Button) view.findViewById(R.id.btnCancel);

        if (mandatory) {
            amcuConfig.setCleaningAlertToBeDisplayed(true);
            btnCancel.setVisibility(View.GONE);
        }
        btnCancel.setText("LATER");
        btnResend.setText("OK");

        btnResend.requestFocus();
        final AlertDialog alertDialog = alertBuilder.create();
        alertBuilder.setCancelable(false);

        alertDialog.setView(view);
//        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        // avoids displaying the dialog if the parent activity has been destroyed thus avoiding BadTokenException
        if (!((Activity) context).isFinishing())
            alertDialog.show();

        btnResend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                amcuConfig.setCleaningAlertToBeDisplayed(false);
                cleanNow();
                alertDialog.dismiss();

            }
        });
        alertDialog.setCancelable(false);
        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                amcuConfig.setCleaningAlertToBeDisplayed(true);
                alertDialog.dismiss();
            }
        });

        // To display the alert dialog in center

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(alertDialog.getWindow().getAttributes());
        lp.width = 450;
        lp.height = 450;
        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);
    }

    private void cleanNow() {
        if (isEssae()) {
            if (maManager != null) {
                openMAConnection();
                isCleaningInProgress = true;
                Log.v(ESSAE, "cleaning now command");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                maManager.writeToMA(SmartCCConstants.CLEANRINSE);
            } else {
                Log.v(ESSAE, "MA manager null, reinitializing");
                initializeMAManager();
                cleanNow();
            }
        }
    }

    private void fetchCleaningHistory() {
        if (maManager != null) {
            try {
                openMAConnection();
                maManager.writeToMA(SmartCCConstants.CLEANHISTORYALL);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void fetchCalibrationHistory() {
        if (maManager != null) {
            openMAConnection();
            maManager.writeToMA(SmartCCConstants.CALIBHISTORYALL);
        }
    }

    private boolean isEssae() {
        return MAFactory.getMaModel(DeviceName.MA1, FarmerScannerActivity.this).equals(AppConstants.MA.ESSAE);
    }

    private void onPrintShiftReport() {
        EndShiftHelper endShiftHelper = new EndShiftHelper(FarmerScannerActivity.this);
        endShiftHelper.printShiftReport(currentDate, currentShift);
    }

    private void ifSampleCode(SampleDataEntity sampleDataEntity) {


        if (sampleDataEntity != null) {


            if (Util.checkIfOnlySampleCode(sampleDataEntity.sampleId) &&
                    amcuConfig.getKeyEnableCollectionConstraints() && !collectionStatus) {
                Util.displayErrorToast("Please check the collection time", FarmerScannerActivity.this);
                return;
            }

            session.setIsSample(true);
            Intent intent = getIntentForCustomer();
            intent.putExtra("SampleDataEnt", sampleDataEntity);
            intent.putExtra("isAutoOrManual", isAutoOrManual);


            session.setFarmerID(sampleDataEntity.sampleId);
            session.setFarmerName(sampleDataEntity.sampleMode);
            session.setFarmerBarcode(sampleDataEntity.sampleBarcode);

            if (sampleDataEntity.sampleIsFat.equalsIgnoreCase("true")) {
                session.setSampleMA(true);
            } else {
                session.setSampleMA(false);
            }

            if (sampleDataEntity.sampleIsWeigh.equalsIgnoreCase("true")) {
                session.setSampleWeigh(true);
            } else {
                session.setSampleWeigh(false);
            }

            setRateChart();

            if (amcuConfig.getRateChartName() == null) {
                setRateChartForSampleTest();
            }

            if (sampleDataEntity.sampleId.equalsIgnoreCase("9996")) {
                etBarcode.setText("");
                cleanNow();
            } else if (amcuConfig.getRateChartName() != null ||
                    (sampleDataEntity.sampleId.equalsIgnoreCase("9998") ||
                            sampleDataEntity.sampleId.equalsIgnoreCase("9997") ||
                            sampleDataEntity.sampleId.equalsIgnoreCase("991") ||
                            sampleDataEntity.sampleId.equalsIgnoreCase("0991"))) {
                intent.putExtra("isFarmer", isFarmer);
                onStartMilkCollectionActivity(intent);
            } else if (!isSampleRecord(sampleDataEntity.sampleId)) {
                etBarcode.requestFocus();
                Toast.makeText(FarmerScannerActivity.this,
                        "No rate chart available for milk type " + session.getMilkType() + "!", Toast.LENGTH_LONG).show();
            }
            return;
        }


    }

    private void ifMccEntity(CenterEntity centerEntity) {
        if (amcuConfig.getEnableCenterCollection()) {
            checkIfCenterCode(centerEntity);
        } else {
            ifNoRecordEntity();
        }

    }

    private void ifNoRecordEntity() {

        if (farmOrBarcode) {
            etBarcode.requestFocus();
            etBarcode.setText("");
            Toast.makeText(FarmerScannerActivity.this,
                    "Entered ID is not valid", Toast.LENGTH_SHORT)
                    .show();
        } else {
            etBarcode.requestFocus();
            etBarcode.setText("");
            Toast.makeText(FarmerScannerActivity.this,
                    "Entered barcode is not valid", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfCenterCode(CenterEntity centerEntity) {


        if (centerEntity != null) {

            //This is check avoid duplicate collection
            session.setMilkType(centerEntity.cattleType);
            if (centerEntity.singleOrMultiple.equalsIgnoreCase(Util.SINGLE)
                    && checkForCenterDuplicate(databaseHandler, centerEntity.centerId)) {
                Util.displayErrorToast("Collection is already done for center " + centerEntity.centerId
                        , FarmerScannerActivity.this);
                return;
            }
            CollectionCenterDao collectionCenterDao = (CollectionCenterDao) DaoFactory.getDao(CollectionConstants.COLLECTION_CENTER);
            CenterEntity entity = collectionCenterDao.findByCenterId(mccEntity.centerId);

            if (entity != null && (entity.activeStatus != null && !entity.activeStatus.equalsIgnoreCase("1"))) {
                Util.displayErrorToast(centerEntity.centerId + " is inactive"
                        , FarmerScannerActivity.this);
                return;
            }

            isCenter = true;
            session.setFarmerID(centerEntity.centerId);
            session.setFarmerName(centerEntity.centerName);
            session.setFarmerBarcode(centerEntity.centerBarcode);
            session.setIsChillingCenter(true);

            //SmartCC changes
            if (session.getComingFrom() == null || (session.getComingFrom() != null &&
                    !session.getComingFrom().equalsIgnoreCase("MA"))) {
                session.setIsChillingCenter(true);
                session.setMilkType(centerEntity.cattleType);
                selectMilkType = session.getMilkType();

            }

            Intent intent = getIntentForCustomer();
            intent.putExtra("isAutoOrManual", isAutoOrManual);
            setRateChart();

            if (amcuConfig.getRateChartName() == null) {
                Toast.makeText(FarmerScannerActivity.this,
                        "No rate chart available for milk type " + session.getMilkType(), Toast.LENGTH_SHORT).show();
            } else if (selectMilkType.equalsIgnoreCase(CattleType.BOTH)) {
                alertForMilkTypeBoth(centerEntity.centerId, false, intent, false, false);
            } else {
                onSubmitCenter(intent);
            }

            return;
        }
        ifNoRecordEntity();
    }

    private void getDeviceInfo() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String device_id = tm.getDeviceId();
        System.out.println("Device ID " + device_id);
        Builder alertDialogBuilder = new Builder(
                FarmerScannerActivity.this);
        // set title
        alertDialogBuilder.setTitle("Device Information");
        // set dialog message
        alertDialogBuilder
                .setMessage(
                        "Device ID " + device_id + "\n" + "Sim operator "
                                + tm.getSimOperator() + "\n")
                .setCancelable(true)
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                        alertDialog.dismiss();
                    }
                    return true;
                }
                return false;
            }
        });
        alertDialog.show();
    }

    private void setConfigPassword() {
        Builder alertBuilder = new Builder(
                FarmerScannerActivity.this);

        LayoutInflater inflater = (LayoutInflater) FarmerScannerActivity.this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_authentication, null);

        final Button btnSave = (Button) view.findViewById(R.id.btnSubmit);
        btnSave.setText("Save");
        final Button btnCancel = (Button) view.findViewById(R.id.btnCanc);
        btnCancel.setText("Test");

        EditText etDeviceName = (EditText) view.findViewById(R.id.editEmail);
        etDeviceName.setText(amcuConfig.getDeviceID());
        final EditText etPassword = (EditText) view.findViewById(R.id.editPwd);
        final EditText etSimUnlockPass = (EditText) view.findViewById(R.id.editSIMPassword);

        tvDeviceHeader = (TextView) view.findViewById(R.id.txtLogo);

        final EditText etServer = (EditText) view
                .findViewById(R.id.edithyperLink);
        etServer.setText(amcuConfig.getServer());
        etPassword.setText(amcuConfig.getDevicePassword());
        oldServer = amcuConfig.getServer();

        etSimUnlockPass.setVisibility(View.GONE);
        tvDeviceHeader.setText("Device LogIn Details");

        // etServer.setVisibility(View.GONE);
        etDeviceName.setEnabled(false);
        etDeviceName.setFocusable(false);
        etPassword.requestFocus();
        etPassword.setCursorVisible(true);
        etPassword.setSelection(etPassword.getText().toString().length());

        Util.alphabetValidation(etSimUnlockPass, Util.ONLY_NUMERIC, FarmerScannerActivity.this, 4);

        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (etServer.getText().toString().contains("http")) {
                    Toast.makeText(FarmerScannerActivity.this,
                            "Remove the http:// or https:// from url", Toast.LENGTH_LONG)
                            .show();
                } else if (etPassword.getText().toString().replace(" ", "").length() > 3
                        && etServer.getText().toString().length() > 3) {
                    amcuConfig.setDevicePassword(etPassword.getText()
                            .toString().replace(" ", ""));
                    amcuConfig.setServer(etServer.getText().toString().replace(" ", ""));

                    if (oldServer != null && !oldServer.equalsIgnoreCase(amcuConfig.getServer())) {
                        Util.restartApp(FarmerScannerActivity.this);
                    }
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(FarmerScannerActivity.this,
                            "Please enter valid details!", Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                tvDeviceHeader.setText("Device LogIn Details");

                try {
                    if (etPassword.getText().toString() == null || etPassword.getText().toString().length() < 2) {
                        Toast.makeText(FarmerScannerActivity.this, "Invalid password!", Toast.LENGTH_SHORT).show();
                    } else if (etPassword.getText().toString().equalsIgnoreCase(amcuConfig.getDevicePassword())) {
                        isAuthenticated = false;
                        tvDeviceHeader.setText("Connecting to server....");
                        LogIn();
                    } else {
                        Toast.makeText(FarmerScannerActivity.this, "Save the password and try again.", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void LogIn() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    logInUser();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
        updateRunnable = new Runnable() {

            @Override
            public void run() {
                if (isAuthenticated) {
                    tvDeviceHeader.setText("Successfully logged In.");
                } else {
                    if (count < 2) {
                        count = count + 1;
                        LogIn();
                    } else {
                        isAuthenticated = false;
                        if (!isConnected) {
                            tvDeviceHeader
                                    .setText("Please check the connectivity!");
                        } else {
                            tvDeviceHeader.setText("Log in failed!");
                        }
                    }
                }
            }
        };
    }

    private void logInUser() {

        if (ServerAPI.isNetworkAvailable(getApplicationContext())) {
            if (!isAuthenticated) {
                isConnected = true;
                isAuthenticated = ServerAPI.authenticateUser(
                        getApplicationContext(),
                        amcuConfig.getDeviceID(),
                        amcuConfig.getDevicePassword(), Util.DEVICE_LOGIN,
                        amcuConfig.getURLHeader() + amcuConfig.getServer());
                myHandler.post(updateRunnable);

            } else if (isAuthenticated) {

                myHandler.post(updateRunnable);
            } else {
                isAuthenticated = false;
                tvDeviceHeader.setText("Log In failed.");
                myHandler.post(updateRunnable);
            }
        } else {
            isConnected = false;
            tvDeviceHeader.setText("Check network connectvity!");
        }

    }

    private void test_F8_forSampleMilk() {

        if (!checkValidSession())
            return;
        session.setFarmerID(Util.paddingFarmerId("9999", amcuConfig.getFarmerIdDigit()));
        session.setFarmerName("Rate check");
        session.setFarmerBarcode("");

        setRateChart();
        session.setIsSample(true);
        Intent intent = getIntentForCustomer();
        intent.putExtra("isAutoOrManual", isAutoOrManual);
        onStartMilkCollectionActivity(intent);
    }

    private synchronized void checkForAnyUpdate() {

        try {
            if (Util.isNetworkAvailable(FarmerScannerActivity.this)) {
                Toast.makeText(FarmerScannerActivity.this, "Checking for updates please wait...",
                        Toast.LENGTH_SHORT).show();

                Intent intentUpdate = new Intent(FarmerScannerActivity.this,
                        RateChartPullService.class);
                intentUpdate.putExtra("FromCheckUpdate", true);
                startService(intentUpdate);
            } else {
                Toast.makeText(FarmerScannerActivity.this, "Please connect to network!",
                        Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * To display the Sales collection summary on dashboard
     */
    private void displaySalesSummary() {

        isFarmer = true;
        resetDashboardData();
        String farmerAndCollection = null;
        try {
            farmerAndCollection = databaseHandler
                    .getAllSalesAndTotalWeightOfSession(
                            currentDate, currentShift);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        if (farmerAndCollection != null) {
            String[] farmAndColl = farmerAndCollection.split("-");
            // tvFarmers.setText(farmAndColl[0]);
            tvTotalFarmers.setText(farmAndColl[0]);
            tvTotalCollection.setText(farmAndColl[1] + getTheUnit());
            tvTotalRecords.setText(farmAndColl[2]);

        }
        try {
            int unsentCount = databaseHandler.getAllNWUnsentSalesCount();
            tvTotalUnsent.setText(String.valueOf(unsentCount));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //DB close removed;
        }
    }

    private void resetDashboardData() {
        tvTotalFarmers.setText("");
        tvTotalCollection.setText("");
        tvTotalRecords.setText("");
        tvTotalUnsent.setText("");
    }

    private boolean shiftStarted() {
        boolean value = false;
        ArrayList<String> farmersList = databaseHandler.getAllCollectedFarmerList(session.getCollectionID(),
                currentDate, currentShift);
        if (farmersList.size() == 0)
            value = true;
        Log.v(PROBER, "Shift started: " + value);
        return value;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = FarmerScannerActivity.this;
        apkFromPendrive = false;
        amcuConfig = AmcuConfig.getInstance();
        session = new SessionManager(FarmerScannerActivity.this);
        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        initializeShiftAndDate();
        if (shiftStarted() && isOperator() && isEssae()) {
            displayMACleaningAlert(this, false);
        }

        try {
            societyEnt = (SocietyEntity) getIntent().getSerializableExtra(
                    "SocietyEntity");
        } catch (Exception e) {
            e.printStackTrace();
        }
        comingFrom = getIntent().getStringExtra("USER_SELECTION");
        userID = getIntent().getStringExtra("USER_ID");

        if (comingFrom != null) {
            session.setComingFrom(comingFrom);
        }

        if (null != comingFrom && comingFrom.equalsIgnoreCase("BOTH")) {
            LoginActivity.clientName = "BOTH";
        }
        if (null != comingFrom && comingFrom.equalsIgnoreCase("MA")) {

            LoginActivity.clientName = "BOTH";
        }
        if (null != comingFrom && comingFrom.equalsIgnoreCase("WS")) {
            LoginActivity.clientName = "BOTH";
        }
        if (LoginActivity.clientName.equalsIgnoreCase("MILMA")
                && amcuConfig.getAllowEquipmentBasedCollection() && isOperator()
                && amcuConfig.getEnableCenterCollection()) {
            Intent intent = new Intent(getApplicationContext(), UserSelectionActivity.class);
            intent.putExtra("CID", new SessionManager(FarmerScannerActivity.this).getSocietyName());
            startActivity(intent);
            finish();
        } else {
            User = new SessionManager(FarmerScannerActivity.this).getUserRole();

            try {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutView = getLayoutInflater().inflate(
                        R.layout.activity_scanner_land, null);
            } else {
                layoutView = getLayoutInflater().inflate(R.layout.activity_scanner,
                        null);
            }
            initializeView(layoutView);
            visibilityForUser();
            //  updatePageInfo();
            if (session.getComingFrom() != null && amcuConfig.getAllowEquipmentBasedCollection()) {
                btnSwitchDevice.setVisibility(View.VISIBLE);
                btnSwitchDevice.setText(" " + session.getComingFrom());
            } else {
                btnSwitchDevice.setVisibility(View.GONE);
            }
            boolean isUpdateAPK;
            try {
                isUpdateAPK = getIntent().getBooleanExtra("UpdateAPK", false);
                if (isUpdateAPK) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            amcuConfig = AmcuConfig.getInstance();
                            UpdateAPK.installDownloadForUnrootedTab(amcuConfig.getKEYDownloadeAPKPath(), FarmerScannerActivity.this);

                        }
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!LoginActivity.clientName.equalsIgnoreCase("MILMA")) {
            etBarcode.requestFocus();
            backFromFarmer = false;
            if (!isRegistered) {
                isRegistered = true;
                chkUnreg.setChecked(false);
            }
            mActivity = null;

        }

        unsentHandler.removeCallbacks(unsentRunnable);

    }

    /**
     * Display famrer and MCC summary on dashboard
     *
     * @param reportType
     */
    private void showFarmerAndMccSummary(final String reportType) {

        try {
            if (amcuConfig.getSalesOrCollection().equalsIgnoreCase("Sales")) {
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                long lnDate = Util.getDateInLongFormat(currentDate);
                isFarmer = true;

                try {
                    farmerAndCollection = databaseHandler
                            .getAllFarmerAndTotalWeightOfSession(
                                    lnDate, currentShift, reportType);
                    unsentCount = databaseHandler.getAllNWUnsentRecordsCount();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //DB close removed;
                }

                unsentHandler.post(unsentRunnable);
            }
        }).start();


        unsentRunnable = new Runnable() {
            @Override
            public void run() {


                if (farmerAndCollection != null) {
                    String[] farmAndColl = farmerAndCollection.split("-");
                    // tvFarmers.setText(farmAndColl[0]);
                    tvTotalFarmers.setText(farmAndColl[0]);
                    tvTotalCollection.setText(farmAndColl[1] + getTheUnit());
                    tvTotalRecords.setText(farmAndColl[2]);

                }


                tvTotalUnsent.setText(String.valueOf(unsentCount));


            }
        };


        if (amcuConfig.getEnableCenterCollection()) {
            long lnDate = Util.getDateInLongFormat(currentDate);
            try {
                farmerAndCollection = databaseHandler
                        .getAllCenterAndTotalWeightOfSession(
                                lnDate, currentShift, Util.REPORT_TYPE_MCC);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //DB close removed;
            }

            if (farmerAndCollection != null) {
                String[] farmAndColl = farmerAndCollection.split("-");
                // tvFarmers.setText(farmAndColl[0]);
                tvCCNumbers.setText(farmAndColl[0]);
                tvCCCollections.setText(farmAndColl[1] + getTheUnit());

            }
        }

    }


    private void checkForIntent() {

        Intent intentSubmit = getIntentForCustomer();
        intentSubmit.putExtra("FarmerName", farmerName);
        intentSubmit.putExtra("isAutoOrManual", isAutoOrManual);
        intentSubmit.putExtra("FarmerId", farmerId);
        intentSubmit.putExtra("isFarmer", isFarmer);

        intentSubmit.putExtra("NumberOfCan", NumberOfCans);
        if (farmerEntity != null && farmerEntity.farmer_cattle != null &&
                farmerEntity.farmer_cattle.equalsIgnoreCase(CattleType.BOTH)) {
            intentSubmit.putExtra("FarmerCattleType", farmerEntity.farmer_cattle);
        } else if (sampleDataEntity != null && sampleDataEntity.sampleMilkType != null)
            intentSubmit.putExtra("FarmerCattleType", sampleDataEntity.sampleMilkType);


        onStartMilkCollectionActivity(intentSubmit);

    }


    private void setRateChartForSampleTest() {
        if (session.getMilkType().equalsIgnoreCase(CattleType.COW) && amcuConfig.getRateChartForCow() != null && amcuConfig.getRateChartForCow().length() > 0) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForCow());
        } else if (session.getMilkType().equalsIgnoreCase(CattleType.BUFFALO) && amcuConfig.getRateChartForBuffalo() != null && amcuConfig.getRateChartForBuffalo().length() > 0) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForBuffalo());
        } else if (session.getMilkType().equalsIgnoreCase(CattleType.MIXED) && amcuConfig.getRateChartForMixed() != null && amcuConfig.getRateChartForMixed().length() > 0) {
            amcuConfig.setRateChartName(amcuConfig.getRateChartForMixed());
        }
    }

    private void setRateChart() {
        smartCCUtil.getCurrentRateChartForCattle(session.getMilkType());
        smartCCUtil.getCurrentIncentiveRateChartForCattle(session.getMilkType());
    }

    private void installAPKFromPendrive()

    {
        if (!Util.checkForPendrive()) {
            Toast.makeText(FarmerScannerActivity.this,
                    "Please attach the pendrive and try again!",
                    Toast.LENGTH_LONG).show();
        } else {
            fileName = "";
            mPath = new File(Util.rootFileName);
            loadFileList(DIALOG_LOAD_APK);
            if (alertDialog != null) {
                alertDialog.dismiss();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_CHANGE_SIMPIN) {
            if (resultCode == 0) {
                afterSuccessFullChangePin(amcuConfig.getTempSimPin());
            } else {
                Util.displayErrorToast("Sim pin changed unsuccessfull", FarmerScannerActivity.this);
            }
        } else if (resultCode == 1) {
            //  Util.doCommandsForUpgradeAPK(Util.downLoadFilePath);
            Toast.makeText(this, "User pressed 'Install' button", Toast.LENGTH_SHORT);
            DeleteAPKFile();

        } else {
            Toast.makeText(this, "User pressed 'Cancel' button", Toast.LENGTH_SHORT);
            installAttempt = installAttempt + 1;
            if (installAttempt < 3) {
                UpdateAPK.installDownloadForUnrootedTab(amcuConfig.getKEYDownloadeAPKPath(), FarmerScannerActivity.this);

            } else {

                DeleteAPKFile();
            }
        }
    }

    private void DeleteAPKFile() {

        APKManager.apkDownloadInprogress = false;
        try {
            DownloadManager mDownloadManager = (DownloadManager) FarmerScannerActivity.this
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            mDownloadManager.remove(amcuConfig.getDownloadId());
            amcuConfig.setDownloadId(0);

            File file = new File(amcuConfig.getKEYDownloadeAPKPath());
            file.delete();
            Util.setNonMarketAppDisable(FarmerScannerActivity.this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        amcuConfig.setKeySetDownloadedApkPath(null);
        installAttempt = 0;
    }

    private boolean isOperator()

    {
        User = session.getUserRole();
        return User.equalsIgnoreCase("Operator");
    }

    private void updatePageInfo() {
        User = new SessionManager(FarmerScannerActivity.this).getUserRole();
        if (User.equalsIgnoreCase("Operator")) {
            isDisable = true;
            lnButton.setVisibility(View.GONE);
            etBarcode.setVisibility(View.VISIBLE);
            // for sid disable auto button
            if (null != userID && userID.equalsIgnoreCase("SID")) {
                btnAutoOrManual.setVisibility(View.GONE);
            } else {
                btnAutoOrManual.setVisibility(View.VISIBLE);
            }
            btnSubmit.setVisibility(View.VISIBLE);
            lnButton.setVisibility(View.GONE);

            btnSocietyInfo.setVisibility(View.GONE);
            btnTruckEntry.setVisibility(View.GONE);
            etBarcode.requestFocus();
            etBarcode.setCursorVisible(true);
            etBarcode.setSelection(0);

            btnCenter.setVisibility(View.GONE);

            if (amcuConfig.getEnableCenterCollection()) {
                btnCenterCollection.setVisibility(View.GONE);
            } else {
                btnCenterCollection.setVisibility(View.GONE);
            }
            if (amcuConfig.getEnableSales()) {
                btnSalesOrCollection.setVisibility(View.VISIBLE);
            }
            if (isSalesEnable) {
                displaySalesSummary();
            } else {
                showFarmerAndMccSummary(Util.REPORT_TYPE_FARMER);
                rlDisplayCollectionDetails.setVisibility(View.VISIBLE);
                if (amcuConfig.getEnableCenterCollection()) {
                    rlDisplayCenterDetails.setVisibility(View.VISIBLE);
                }
                if (amcuConfig.getKeyEnableCollectionConstraints()) {
                    lnPendingTime.setVisibility(View.VISIBLE);
                }
            }


        } else {
            if (amcuConfig.getSimlockDisplay() && amcuConfig.getAllowSimlockPassword()) {
                btnSimlockChange.setVisibility(View.VISIBLE);
            } else {
                btnSimlockChange.setVisibility(View.GONE);
            }


            btnTruckEntry.setVisibility(View.GONE);
            btnEndShift.setVisibility(View.GONE);
            rlDisplayCenterDetails.setVisibility(View.GONE);
            rlDisplayCollectionDetails.setVisibility(View.GONE);
            lnPendingTime.setVisibility(View.GONE);

            btnCenterCollection.setVisibility(View.GONE);

            if (amcuConfig.getEnableCenterCollection()) {
                btnCenter.setVisibility(View.VISIBLE);
            } else {
                btnCenter.setVisibility(View.GONE);
            }
        }
    }

    // for switching between the customer screen ws,ma or both
    public Intent getIntentForCustomer() {

        Intent intent = new Intent(FarmerScannerActivity.this, CollectionActivity.class);

        if (session.getIsSample()) {
            intent = new Intent(FarmerScannerActivity.this, SampleCollectionActivity.class);
        } else if (session.getComingFrom() == null) {


            intent = new Intent(FarmerScannerActivity.this, CollectionActivity.class);
            //for sid
            intent.putExtra("COMING_FROM_SID", false);

        } else if (null != userID && userID.equalsIgnoreCase("SID")) {
            intent = new Intent(FarmerScannerActivity.this, MCCCollectionActivity.class);

            Cursor cursor = databaseHandler.getCurrentSequenceNumber(Integer.valueOf(etBarcode.getText().toString().trim()),
                    currentDate, currentShift);
            cursor.moveToPosition(0);
            intent.putExtra("COMING_FROM_SID", true);
            intent.putExtra("COMING_FROM", "UMCA");
            intent.putExtra("SELECTED_CURSORID", String.valueOf(cursor.getString(cursor.getColumnIndex("_id"))));
            ReportEntity reportEntity = SmartCCUtil.getReportFromCursor(cursor);
            session.setFarmerName(cursor.getString(cursor.getColumnIndex("name")));
            session.setFarmerID(cursor.getString(cursor.getColumnIndex("farmerId")));

            //SmartCC changes
            selectMilkType = cursor.getString(cursor.getColumnIndex(DatabaseHandler.KEY_REPORT_MILKTYPE));
            session.setMilkType(selectMilkType);

            ArrayList<ReportEntity> reportEntities = new ArrayList<>();
            reportEntities.add(reportEntity);
         /*     Bundle bundle = new Bundle();
            bundle.putSerializable("SELECTED_DATA",reportEntities);*/

            intent.putExtra("SELECTED_DATA", reportEntities);

        } else if (session.getComingFrom().equalsIgnoreCase("MA") && isCenter) {
            intent = new Intent(FarmerScannerActivity.this, UserMilkCollectionActivity.class);
            intent.putExtra("isAutoOrManual", isAutoOrManual);

        } else if (session.getComingFrom().equalsIgnoreCase("WS") && isCenter) {
            amcuConfig.setLastAlertToggleState("empty");
            intent = new Intent(FarmerScannerActivity.this, WeightCollectionActivityV2.class);
        }

        return intent;
    }

    private void alertSimlock() {

        Builder alertBuilder = new Builder(
                FarmerScannerActivity.this);

        LayoutInflater inflater = (LayoutInflater) FarmerScannerActivity.this
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alert_authentication, null);

        final Button btnSave = (Button) view.findViewById(R.id.btnSubmit);
        btnSave.setText("Save");
        final Button btnCancel = (Button) view.findViewById(R.id.btnCanc);
        btnCancel.setText("Cancel");

        EditText etDeviceName = (EditText) view.findViewById(R.id.editEmail);
        final EditText etServer = (EditText) view
                .findViewById(R.id.edithyperLink);

        etDeviceName.setVisibility(View.GONE);
        etServer.setVisibility(View.GONE);

        final EditText etSimOldPassword = (EditText) view.findViewById(R.id.editPwd);
        final EditText etSimNewPassword = (EditText) view.findViewById(R.id.editSIMPassword);

        if (amcuConfig.getSimUnlockPassword() == null) {
            etSimOldPassword.setHint("Enter current simlock pin");
            etSimOldPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
            etSimNewPassword.setVisibility(View.GONE);
        } else {
            etSimOldPassword.setHint("Enter current pin");
            etSimOldPassword.setText(amcuConfig.getSimUnlockPassword());
            etSimOldPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
            etSimNewPassword.requestFocus();
            etSimNewPassword.setHint("Enter new pin");
            etSimNewPassword.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        tvDeviceHeader = (TextView) view.findViewById(R.id.txtLogo);
        tvDeviceHeader.setText("Change SIM lock password");


        Util.alphabetValidation(etSimOldPassword, Util.ONLY_NUMERIC, FarmerScannerActivity.this, 8);
        Util.alphabetValidation(etSimNewPassword, Util.ONLY_NUMERIC, FarmerScannerActivity.this, 8);

        btnSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (amcuConfig.getSimUnlockPassword() == null) {
                    if (etSimOldPassword.getText().toString().length() > 3) {
                        amcuConfig.setSimlockPassword(etSimOldPassword.getText().toString()
                                .replace(" ", ""));
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(FarmerScannerActivity.this, "Invalid password!", Toast.LENGTH_SHORT).show();
                    }
                } else if (etSimOldPassword.getText().toString().length() > 3) {

                    if (etSimNewPassword.getText().toString().length() > 3) {
                        changeSimLockPassword(etSimOldPassword.getText().toString(), etSimNewPassword.getText().toString());
                        amcuConfig.setTempSimPin(etSimNewPassword.getText().toString().replace(" ", ""));
                        alertDialog.dismiss();
                    } else {
                        Toast.makeText(FarmerScannerActivity.this,
                                "Enter valid new password!", Toast.LENGTH_SHORT)
                                .show();
                        etSimOldPassword.requestFocus();
                        etSimNewPassword.setSelection(etSimNewPassword.getText().toString().length());
                    }
                } else {
                    Toast.makeText(FarmerScannerActivity.this,
                            "Enter valid old password!", Toast.LENGTH_SHORT)
                            .show();
                    etSimOldPassword.requestFocus();
                    etSimOldPassword.setSelection(etSimOldPassword.getText().toString().length());
                }

            }
        });

        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
            }
        });

        alertDialog = alertBuilder.create();
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void addMissingData() {

        if (new SessionManager(FarmerScannerActivity.this).getReportData() != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Util.addMissingDatatoDB(FarmerScannerActivity.this, new SessionManager(FarmerScannerActivity.this).getReportData());
                }
            }).start();
        }
    }

    private void onStartMilkCollectionActivity(Intent intent) {
        session.setFarmOrBarcode(farmOrBarcode);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
        finish();
    }

    private void forOperator() {
        isFarmer = false;
        farmOrBarcode = session.getFarmOrBarcode();
        setButton();
        session.setSampleMA(true);
        session.setSampleWeigh(true);
        session.setIsSample(false);
        if (allDatabaseRateDetailsEnt == null
                || allDatabaseRateDetailsEnt.size() < 1) {
            getAllDatabaseRatechart();
        }
//        if (Util.settingEntity.onlyManual != null
//                && Util.settingEntity.onlyManual.equalsIgnoreCase("false")) {
//            ShowKeyboard();
//        }

        etBarcode.setText("");
        etBarcode.requestFocus();
    }

    private void changeSimLockPassword(String oldPin, String newPin) {
        try {
            String ussdCode = "**04*" + oldPin + "*" + newPin + "*" + newPin + Uri.encode("#");
            startActivityForResult(new Intent(Intent.ACTION_CALL,
                    Uri.parse("tel:" + ussdCode)), RESULT_CHANGE_SIMPIN);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void afterSuccessFullChangePin(String newPin) {
        final Handler handler = new Handler();
        Util.displayErrorToast("pin changed", FarmerScannerActivity.this);
        amcuConfig.setSimlockPassword(newPin);
        amcuConfig.setTempSimPin(null);

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Util.displayErrorToast("Tab restart required for this change!", FarmerScannerActivity.this);
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(simRunnable);
            }
        }).start();

        simRunnable = new Runnable() {
            @Override
            public void run() {
                Util.restartTab(FarmerScannerActivity.this);
            }
        };
    }


    //For A4 size printing

    private String getTheUnit() {

        if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {

            return " Ltrs";

        } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            return " Kgs";
        } else if (amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            return " Kgs";
        } else {
            return " Ltrs";
        }
    }

    private void disableOrEnableCollectionPage() {
        if (isSalesEnable) {
            etBarcode.setVisibility(View.GONE);
            scrollView.setVisibility(View.GONE);
            lnButtonsSubmit.setVisibility(View.GONE);
            lnCattleType.setVisibility(View.VISIBLE);
            btnSales.setVisibility(View.VISIBLE);
            linearLayoutSales.setVisibility(View.VISIBLE);
            btnSales.setText("Sales entry");
            spCattleType.setEnabled(true);
            spCattleType.requestFocus();
            tvMemberH.setText("Sales");
            tvCollectionH.setText("Total Qty");
            tvTestRecordH.setVisibility(View.GONE);
            tvTotalRecords.setVisibility(View.GONE);
            btnMenu.setVisibility(View.GONE);
            enableMilkTypeSpinner();
            displaySalesSummary();
            rlDisplayCenterDetails.setVisibility(View.GONE);


        } else {
            etBarcode.setVisibility(View.VISIBLE);
            scrollView.setVisibility(View.VISIBLE);
            lnButtonsSubmit.setVisibility(View.VISIBLE);
            lnSocietyInfo.setVisibility(View.GONE);
            lnCattleType.setVisibility(View.GONE);
            spCattleType.setEnabled(false);
            btnSocietyInfo.setVisibility(View.GONE);
            btnMenu.setVisibility(View.VISIBLE);
            btnSocietyInfo.setText("Sales entry");

            etBarcode.requestFocus();

            tvMemberH.setText("Collections");
            tvCollectionH.setText("Quantity");
            tvTestRecordH.setVisibility(View.VISIBLE);
            tvTotalRecords.setVisibility(View.VISIBLE);
            showFarmerAndMccSummary(Util.REPORT_TYPE_FARMER);
            btnSales.setVisibility(View.GONE);
            linearLayoutSales.setVisibility(View.GONE);
            if (amcuConfig.getEnableCenterCollection())
                rlDisplayCenterDetails.setVisibility(View.VISIBLE);
        }

    }

    private void checkIfSalesEnable() {
        if (amcuConfig.getEnableSales() && isOperator()) {
            btnSalesOrCollection.setText("Sales");
            btnSalesOrCollection.setVisibility(View.VISIBLE);

        }

    }

    private void salesFunction() {
        etBarcode.setText("");
        if (isSalesEnable) {
            btnSalesOrCollection.setText("Sales");
            isSalesEnable = false;
            amcuConfig.setSalesOrCollection("Collection");

        } else {
            btnSalesOrCollection.setText("Collection");
            isSalesEnable = true;
            amcuConfig.setSalesOrCollection("Sales");
        }
        disableOrEnableCollectionPage();
    }

    private void enableMilkTypeSpinner() {
        spCattleType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                MilkType = spCattleType.getItemAtPosition(arg2).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    private void alertForMilkTypeBoth(final String strBarcode, final boolean isFarmer,
                                      final Intent intent, final boolean displayMixed,
                                      final boolean isSample) {


        final Button btnCancel, btnSubmita, btnCow, btnBuffalo, btnMixed;
        TextView tvHeader;


        if (alertBuilderBoth != null) {

            try {
                alertDialog.dismiss();
                alertDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        alertBuilderBoth = new Builder(
                FarmerScannerActivity.this);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alert_select_milktype, null);

        tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        tvHeader.setText("Select cattle type");
        spSelectMilkType = (Spinner) view.findViewById(R.id.spSelectMilkType);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnSubmita = (Button) view.findViewById(R.id.btnSubmit);
        btnCow = (Button) view.findViewById(R.id.btnCow);
        btnBuffalo = (Button) view.findViewById(R.id.btnBuffalo);
        btnMixed = (Button) view.findViewById(R.id.btnMixed);
        if (displayMixed)
            btnMixed.setVisibility(View.VISIBLE);
        alertDialog = alertBuilderBoth.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.show();
        btnSubmita.requestFocus();
        if (isFarmer && !isMultipleCans()) {
            selectMilkType = getMilkTypeFromBoth();

            if (selectMilkType != null) {

                if (selectMilkType.equalsIgnoreCase(CattleType.COW)) {
                    btnCow.requestFocus();
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnCow.setTextColor(getResources().getColor(R.color.white));
                } else if (selectMilkType.equalsIgnoreCase(CattleType.BUFFALO)) {
                    btnBuffalo.requestFocus();
                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.white));
                } else {
                    btnMixed.requestFocus();
                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnMixed.setTextColor(getResources().getColor(R.color.white));
                }
            } else {

                Util.displayErrorToast("Both collections done for id "
                        + etBarcode.getText().toString().trim() + "!", FarmerScannerActivity.this);
                isBoth = true;
                alertDialog.dismiss();
                alertDialog.cancel();
            }
        } else {
            selectMilkType = amcuConfig.getDefaultMilkTypeForBoth();
            if (selectMilkType.equalsIgnoreCase(CattleType.COW)) {
                btnCow.requestFocus();
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnBuffalo.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setTextColor(getResources().getColor(R.color.black));
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnCow.setTextColor(getResources().getColor(R.color.white));
            } else if (selectMilkType.equalsIgnoreCase(CattleType.BUFFALO)) {
                btnBuffalo.requestFocus();
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnCow.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setTextColor(getResources().getColor(R.color.black));
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnBuffalo.setTextColor(getResources().getColor(R.color.white));
            } else {
                btnMixed.requestFocus();
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnCow.setTextColor(getResources().getColor(R.color.black));
                btnBuffalo.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnMixed.setTextColor(getResources().getColor(R.color.white));
            }
        }
        btnMixed.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT))) {
                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnMixed.setTextColor(getResources().getColor(R.color.black));
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.white));
                    btnBuffalo.requestFocus();
                    selectMilkType = CattleType.BUFFALO;
                    if (farmerEntity != null && farmerEntity.isReservedFarmer)
                        farmerEntity.farmer_cattle = CattleType.BUFFALO;
                    if (sampleDataEntity != null)
                        sampleDataEntity.sampleMilkType = CattleType.BUFFALO;
                    return true;
                } else if (selectMilkType != null && ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    isBoth = false;
                    selectMilkType = CattleType.MIXED;
                    if (farmerEntity != null && farmerEntity.isReservedFarmer)
                        farmerEntity.farmer_cattle = CattleType.MIXED;
                    if (sampleDataEntity != null)
                        sampleDataEntity.sampleMilkType = CattleType.MIXED;
                    if (displayMixed)
                        NextActivity();
                    else
                        OnSubmitFarmerOrCenter(isFarmer, intent);
                    return true;
                }
                return false;
            }
        });
        btnCow.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))) {
                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnCow.setTextColor(getResources().getColor(R.color.black));
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.white));
                    btnBuffalo.requestFocus();
                    selectMilkType = CattleType.BUFFALO;
                    if (farmerEntity != null && farmerEntity.isReservedFarmer)
                        farmerEntity.farmer_cattle = CattleType.BUFFALO;
                    if (sampleDataEntity != null)
                        sampleDataEntity.sampleMilkType = CattleType.BUFFALO;
                    return true;
                } else if (selectMilkType != null && ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    isBoth = false;
                    selectMilkType = CattleType.COW;
                    if (farmerEntity != null && farmerEntity.isReservedFarmer)
                        farmerEntity.farmer_cattle = CattleType.COW;
                    if (sampleDataEntity != null)
                        sampleDataEntity.sampleMilkType = CattleType.COW;
                    if (displayMixed)
                        NextActivity();
                    else
                        OnSubmitFarmerOrCenter(isFarmer, intent);
                    return true;
                }
                return false;
            }
        });

        btnBuffalo.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)) {
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.black));
                    btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnCow.setTextColor(getResources().getColor(R.color.white));
                    btnCow.requestFocus();
                    selectMilkType = CattleType.COW;
                    if (farmerEntity != null && farmerEntity.isReservedFarmer)
                        farmerEntity.farmer_cattle = CattleType.COW;
                    if (sampleDataEntity != null)
                        sampleDataEntity.sampleMilkType = CattleType.COW;
                    return true;
                } else if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
                    btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setTextColor(getResources().getColor(R.color.black));
                    btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                    btnMixed.setTextColor(getResources().getColor(R.color.white));
                    btnMixed.requestFocus();
                    selectMilkType = CattleType.MIXED;
                    if (farmerEntity != null && farmerEntity.isReservedFarmer)
                        farmerEntity.farmer_cattle = CattleType.MIXED;
                    if (sampleDataEntity != null)
                        sampleDataEntity.sampleMilkType = CattleType.MIXED;
                    return true;
                } else if (selectMilkType != null && ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    isBoth = false;
                    selectMilkType = CattleType.BUFFALO;
//                    etBarcode.setText(strBarcode);
                    if (farmerEntity != null && farmerEntity.isReservedFarmer)
                        farmerEntity.farmer_cattle = CattleType.BUFFALO;
                    if (sampleDataEntity != null)
                        sampleDataEntity.sampleMilkType = CattleType.BUFFALO;
                    if (displayMixed)
                        NextActivity();
                    else
                        OnSubmitFarmerOrCenter(isFarmer, intent);
                    return true;
                }
                return false;
            }
        });


        spSelectMilkType.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMilkType = spSelectMilkType.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnBuffalo.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setTextColor(getResources().getColor(R.color.black));
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnCow.setTextColor(getResources().getColor(R.color.white));
                btnCow.requestFocus();
                selectMilkType = CattleType.COW;
                if (farmerEntity != null && farmerEntity.isReservedFarmer)
                    farmerEntity.farmer_cattle = CattleType.COW;
                if (sampleDataEntity != null)
                    sampleDataEntity.sampleMilkType = CattleType.COW;
            }
        });

        btnBuffalo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnCow.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setTextColor(getResources().getColor(R.color.black));
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnBuffalo.setTextColor(getResources().getColor(R.color.white));
                btnBuffalo.requestFocus();
                selectMilkType = CattleType.BUFFALO;
                if (farmerEntity != null && farmerEntity.isReservedFarmer)
                    farmerEntity.farmer_cattle = CattleType.BUFFALO;
                if (sampleDataEntity != null)
                    sampleDataEntity.sampleMilkType = CattleType.BUFFALO;
            }
        });
        btnMixed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCow.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnBuffalo.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button));
                btnCow.setTextColor(getResources().getColor(R.color.black));
                btnBuffalo.setTextColor(getResources().getColor(R.color.black));
                btnMixed.setBackgroundDrawable(getResources().getDrawable(R.drawable.round_button_selected));
                btnMixed.setTextColor(getResources().getColor(R.color.white));
                btnMixed.requestFocus();
                selectMilkType = CattleType.MIXED;
                if (farmerEntity != null && farmerEntity.isReservedFarmer)
                    farmerEntity.farmer_cattle = CattleType.MIXED;
                if (sampleDataEntity != null)
                    sampleDataEntity.sampleMilkType = CattleType.MIXED;
            }
        });


        btnCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isBoth = true;
                alertDialog.dismiss();
            }
        });

        btnSubmita.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                isBoth = false;
                etBarcode.setText(strBarcode);
                if (displayMixed)
                    NextActivity();
                else
                    OnSubmitFarmerOrCenter(isFarmer, intent);
            }
        });
        // To display the alert dialog in center
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);
        lp.copyFrom(alertDialog.getWindow().getAttributes());

        if (densityDpi < 150) {
            lp.width = 300;
            lp.height = 300;

        } else {
            lp.width = 450;
            lp.height = 450;
        }

        lp.gravity = Gravity.CENTER | Gravity.CENTER;
        alertDialog.getWindow().setAttributes(lp);
    }

    private String getMilkTypeFromBoth() {

        boolean isCollectionDoneCow, isCollectionDoneBuff;

        isCollectionDoneCow = collectionRecordDao.findByFarmerIdDateShiftAndMilkType(
                farmerEntity.farmer_id, currentDate, currentShift, CattleType.COW) != null;

        isCollectionDoneBuff = collectionRecordDao.findByFarmerIdDateShiftAndMilkType(
                farmerEntity.farmer_id, currentDate, currentShift, CattleType.BUFFALO) != null;


        if (isCollectionDoneBuff &&
                isCollectionDoneCow) {
            return null;
        } else if (!isCollectionDoneBuff && !isCollectionDoneCow) {
            if (amcuConfig.getDefaultMilkTypeForBoth().equalsIgnoreCase(CattleType.BUFFALO)) {
                return CattleType.BUFFALO;
            } else {
                return CattleType.COW;
            }
        } else if (isCollectionDoneBuff) {
            return CattleType.COW;
        } else if (isCollectionDoneCow) {
            return CattleType.BUFFALO;
        } else {
            if (amcuConfig.getDefaultMilkTypeForBoth().equalsIgnoreCase(CattleType.BUFFALO)) {
                return CattleType.BUFFALO;
            } else {
                return CattleType.COW;
            }
        }

    }

    private boolean isMultipleCans() {
        boolean isMultipleCans = false;
        if (farmerEntity != null)
            isMultipleCans = farmerEntity.isMultipleCans;
        else if (sampleDataEntity != null) {
            return true;
        } else {
            farmerEntity = (FarmerEntity) farmerScannerHelper.getUserDetailsFromUserType(
                    etBarcode.getText().toString().trim(), userType);
            isMultipleCans();
        }

        int cans = 0;

        try

        {
            cans = Integer.parseInt(farmerEntity.farmer_cans);
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

        return isMultipleCans || cans >= 2;
    }

    private void printViaShareIntent(String path) {

        Util.displayErrorToast(path, FarmerScannerActivity.this);

        Intent printIntent = new Intent(Intent.ACTION_VIEW);
        printIntent.setDataAndType(Uri.parse(path), "application/vnd.ms-excel");
        printIntent.putExtra("return", true);
        startActivity(printIntent);
    }

    private void gotoTruckEntryReport() {
        Intent intentTruck = new Intent(FarmerScannerActivity.this,
                TruckEntryReportActivity.class);
        startActivity(intentTruck);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
    }


    //For new Entry for SmartCC

    private void goToTruckEntry() {
        Intent intentTruck = new Intent(FarmerScannerActivity.this,
                TruckEntryActivity.class);
        startActivity(intentTruck);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
    }

    private void gotoPrintDuplicateActivity() {
        Intent intentPrint = new Intent(FarmerScannerActivity.this,
                PrintDuplicateActivity.class);
        intentPrint.putExtra("COMING_FROM", "FarmerScannerActivity");
        startActivity(intentPrint);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);
    }

    private void onStartAllCenter() {
        Intent intentTruck = new Intent(FarmerScannerActivity.this,
                AllChillingCenterDetails.class);
        startActivity(intentTruck);
        overridePendingTransition(R.anim.slide_in_left,
                R.anim.slide_out_left);

    }

    private void openCenterAlert() {
        startActivity(new Intent(FarmerScannerActivity.this, SelectCenterActivity.class));
    }

    private void OnSubmitFarmerOrCenter(boolean isFarmer, Intent intent) {
        session.setMilkType(selectMilkType);
        if (isFarmer) {
            onSubmit();
        } else {
            onSubmitCenter(intent);
        }

    }

    private void onSubmitCenter(Intent intent) {


        if (!checkMultipleForCenter()) {
            return;
        }

        if (session.getComingFrom() != null &&
                session.getComingFrom().equalsIgnoreCase("WS")) {

            session.setIsChillingCenter(true);
            //smartCC changes
            session.setMilkType(selectMilkType);
            intent.putExtra("isFarmer", isFarmer);
            onStartMilkCollectionActivity(intent);
        } else {
            session.setMilkType(selectMilkType);
            setRateChart();
            if (amcuConfig.getRateChartName() != null) {
                session.setIsChillingCenter(true);
                intent.putExtra("isFarmer", isFarmer);
                onStartMilkCollectionActivity(intent);
            } else {
                session.setIsChillingCenter(false);
                etBarcode.requestFocus();
                Toast.makeText(FarmerScannerActivity.this,
                        "No rate chart available for milk type " + session.getMilkType() + "!", Toast.LENGTH_LONG).show();
            }

            if (alertDialog != null) {
                alertDialog.dismiss();
            }
        }
    }

    private void goBySequenceId() {
        isCenter = true;
        Intent intent = getIntentForCustomer();
        intent.putExtra("isAutoOrManual", isAutoOrManual);
        session.setIsChillingCenter(true);
        //SmartCC changes
        selectMilkType = session.getMilkType();
        setRateChart();

        if (!selectMilkType.equalsIgnoreCase(CattleType.BOTH)
                && amcuConfig.getRateChartName() == null) {
            Toast.makeText(FarmerScannerActivity.this,
                    "No rate chart available for milk type " + session.getMilkType(), Toast.LENGTH_SHORT).show();
        } else if (selectMilkType.equalsIgnoreCase(CattleType.BOTH)) {
            alertForMilkTypeBoth(etBarcode.getText().toString().trim(), false, intent, false, false);
        } else {
            onSubmitCenter(intent);
        }

    }

    private boolean checkForCenterDuplicate(DatabaseHandler dbh, String centerId) {
        boolean isDuplicate;
        if (session.getComingFrom() != null &&
                session.getComingFrom().equalsIgnoreCase("WS")) {
            isDuplicate = dbh.checkForDuplicateChillingRecord("WS", centerId, session.getMilkType());
        } else {
            isDuplicate = dbh.checkForDuplicateChillingRecord("MA", centerId, session.getMilkType());
        }
        return isDuplicate;
    }

    private void setSmartCCViews() {
        btnTruckList = (Button) findViewById(R.id.btnTruckList);
        btnAgentList = (Button) findViewById(R.id.btnAgentList);
        btnRejectEntry = (Button) findViewById(R.id.btnRejectEntry);
        btnTruckList.setOnClickListener(this);
        btnAgentList.setOnClickListener(this);
        btnRejectEntry.setOnClickListener(this);
        if (AmcuConfig.getInstance().getSmartCCFeature()) {
            btnTruckList.setVisibility(View.VISIBLE);
            btnAgentList.setVisibility(View.VISIBLE);
            btnRejectEntry.setVisibility(View.VISIBLE);
        } else {
            btnTruckList.setVisibility(View.GONE);
            btnAgentList.setVisibility(View.GONE);
            btnRejectEntry.setVisibility(View.GONE);
        }
    }

    private boolean checkMultipleForCenter() {
        if ((null != comingFrom && comingFrom.equalsIgnoreCase("MA"))
                || (null != userID && userID.equalsIgnoreCase("SID"))) {
            return true;
        }
        String cattleType = mccEntity.cattleType;
        String singleOrMultiple = mccEntity.singleOrMultiple;

        if (mccEntity != null && cattleType.equalsIgnoreCase(CattleType.BOTH)) {

            //This is check avoid duplicate collection
            if (singleOrMultiple.equalsIgnoreCase(Util.SINGLE)
                    && checkForCenterDuplicate(databaseHandler, mccEntity.centerId)) {
                Util.displayErrorToast("Collection is already done for center " + mccEntity.centerId
                                + " and milkType " + session.getMilkType()
                        , FarmerScannerActivity.this);
                return false;
            }
        }

        return true;
    }

    private void initializeShiftAndDate() {
        smartCCUtil = new SmartCCUtil(FarmerScannerActivity.this);
        currentShift = Util.getCurrentShift();
        currentDate = smartCCUtil.getReportFormatDate();
        databaseHandler = DatabaseHandler.getDatabaseInstance();
        timehandler = new Handler();
        sessionManager = new SessionManager(FarmerScannerActivity.this);


    }

    private void setMessageAndTime(String message, String time, long millis) {
        tvPendingTimeTitle.setText(message);
        tvPendingTime.setText(time);

        if (collectionStatus) {
            int mins = (int) (millis / (60 * 1000));
            if ((mins <= 10 && mins % 2 == 0))

                tvPendingTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.green_circle, 0);
        } else {

            tvPendingTime.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.red_circle, 0);

        }

    }

    private boolean checkValidSession() {
        boolean returnValue = true;
        entryType = farmerScannerHelper.isBarocodeOrId(etBarcode.getText().toString().trim());
        userType = farmerScannerHelper.getUserType(etBarcode.getText().toString().trim(), entryType);


        if (userType.equalsIgnoreCase(UserType.SAMPLE) && sampleDataEntity != null
                && sampleDataEntity.sampleId != null &&
                !Util.checkIfOnlySampleCode(sampleDataEntity.sampleId)) {
            returnValue = true;
        } else if (userType.equalsIgnoreCase(UserType.SAMPLE) && !
                Util.checkIfRateCheck(etBarcode.getText().toString().trim(),
                        amcuConfig.getFarmerIdDigit()) && !
                Util.checkIfOnlySampleCode(etBarcode.getText().toString().trim())) {
            returnValue = true;
        } else if (etBarcode.getText().toString().trim().length() > 0 &&
                amcuConfig.getKeyEnableCollectionConstraints() && !collectionStatus) {
            Util.displayErrorToast("Please check the collection time", FarmerScannerActivity.this);
            returnValue = false;
        }

        return returnValue;
    }

    // If collection status change to finished during the collection
    private void triggerToSessionEnd(String message) {
        if (collectionStatus) {
            if (isOperator()) {
                tts.speak(message, TextToSpeech.QUEUE_FLUSH, null);
                try {
                    onPrintShiftReport();
                    getCleaningDataFromMA();
                    EndShiftHelper endShiftHelper = new EndShiftHelper(FarmerScannerActivity.this);
                    endShiftHelper.doEndShift();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            collectionStatus = false;
        }
    }

    private void getCleaningDataFromMA() {
        if (isEssae()) {
            fetchCalibrationHistory();
            fetchCleaningHistory();
            if (isOperator())
                displayMACleaningAlert(FarmerScannerActivity.this, true);
        }
    }

    private void initializeTTS() {

        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.UK);
                }
            }
        });
    }

    private void setVisiblityForCenter() {
        if (amcuConfig.getEnableCenterCollection()) {
            btnCCReport.setVisibility(View.VISIBLE);
        } else {
            btnCCReport.setVisibility(View.GONE);
        }
    }

    private void setVisiblityForAgentFarmerCollection() {

        if (amcuConfig.getKeyAllowAgentFarmerCollection()) {
            btnAgentCollection.setVisibility(View.VISIBLE);
        } else {
            btnAgentCollection.setVisibility(View.GONE);
        }
    }

    private void showButton() {
        if (amcuConfig.getKeyAllowEditCollection()) {
            btnEditCollection.setVisibility(View.VISIBLE);
        } else {
            btnEditCollection.setVisibility(View.GONE);

        }
    }


}
