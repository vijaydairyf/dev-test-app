package com.devapp.devmain.agentfarmersplit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.devapp.devmain.dao.AgentSplitDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.dao.FarmerDao;
import com.devapp.devmain.devicemanager.DriverConfiguration;
import com.devapp.devmain.entity.FarmerEntity;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.helper.DeviceName;
import com.devapp.devmain.ma.MAFactory;
import com.devapp.devmain.ma.MaManager;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.devmain.ws.WsFactory;
import com.devapp.devmain.ws.WsManager;
import com.devapp.smartcc.entityandconstants.QuantityEntity;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devapp.smartcc.main.CollectionHelper;
import com.devapp.smartcc.report.ReportHintConstant;
import com.devApp.R;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;

/**
 * Created by Upendra on 24/10/17.
 */

public class AggregateFarmerCollection extends Activity implements View.OnClickListener {


    private static final String CATTLE_TYPE = "COW";
    public static String MA1 = DeviceName.MILK_ANALYSER;
    private final String TAG = "Agent Collection";
    AmcuConfig amcuConfig;
    DriverConfiguration driverConfiguration;
    long collectionTime, qualityTime, quantityTime,
            qualityStartTime, qualityEndTime;
    String QUALITY_MODE = AppConstants.AUTO;
    String QUANTITY_MODE = AppConstants.AUTO;
    SmartCCUtil smartCCUtil;
    MilkAnalyserEntity maEntity = null;
    // private ExecutorService mExecutorWS;

    //    private ExecutorService mExecutorMA1 = Executors.newSingleThreadExecutor();
//    private ExecutorService mExecutorMA2 = Executors.newSingleThreadExecutor();
    ByteArrayOutputStream baosMa = new ByteArrayOutputStream();
    QualityParams qualityParams = new QualityParams();
    ValidationHelper validationHelper;
    DecimalFormat decimalFormatQuality = new DecimalFormat("#0.0");
    StringBuilder messageWS = new StringBuilder();
    boolean isCavinCare, isGoldTech;
    boolean ignoreInitialWSData;
    CollectionHelper collectionHelper;
    long count = 0;
    long tippingStartTime = 0, tippingEndTime;
    QuantityEntity quantityEntity = new QuantityEntity();
    QuantityParams quantityParams = new QuantityParams();
    int MILK_STATUS_CODE = 1;
    int PARENT_ID = 1;
    private Activity mActivity;
    private EditText etAgentId;
    //Device connection segment
    private EditText etFarmerId;
    private EditText etFat;
    private EditText etSnf;
    private EditText etQty;
    private EditText etCLR;
    private TextView etAggregateFarmerName;

    private Button mbtnCancel;
    private Button mbtnSubMit;
    private MaManager maManager;
    private WsManager wsManager;
    private AgentSplitDao agentSplitDao;

    private TextView etName;
    //Open device connection
    private boolean isWsAdded;
    private FarmerDao farmerDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_collection);
        mActivity = AggregateFarmerCollection.this;
        amcuConfig = AmcuConfig.getInstance();
        farmerDao = (FarmerDao) DaoFactory.getDao(CollectionConstants.FARMER);
        driverConfiguration = new DriverConfiguration();

        initializeView();
    }

    private void initializeView() {

        etAgentId = (EditText) findViewById(R.id.etAgentId);
        etFarmerId = (EditText) findViewById(R.id.etFarmerId);
        etFat = (EditText) findViewById(R.id.etFat);
        etSnf = (EditText) findViewById(R.id.etSnf);
        etQty = (EditText) findViewById(R.id.etQty);
        etCLR = (EditText) findViewById(R.id.etCLR);
        etName = (TextView) findViewById(R.id.etFarmerName);
        etAggregateFarmerName = (TextView) findViewById(R.id.etAggregateName);

        mbtnCancel = (Button) findViewById(R.id.btnCancel);
        mbtnSubMit = (Button) findViewById(R.id.btnSubmit);

        mbtnCancel.setOnClickListener(this);
        mbtnSubMit.setOnClickListener(this);

        etFarmerId.setEnabled(false);
        etAgentId.setEnabled(false);
        etName.setEnabled(false);
        etAggregateFarmerName.setEnabled(false);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            etAgentId.setText(bundle.getString("agentId"));
            etFarmerId.setText(bundle.getString("farmerId"));
            PARENT_ID = bundle.getInt(com.devapp.devmain.agentfarmersplit.AgentFarmerActivity.PARENT_ID);

            String name = ((FarmerEntity) farmerDao.findById(bundle.getString("farmerId"))).farmer_name;
            if (name != null)
                etName.setText(name);

            String aggregateFarmerName = ((FarmerEntity) farmerDao.findById(bundle.getString("agentId"))).farmer_name;
            if (aggregateFarmerName != null)
                etAggregateFarmerName.setText(aggregateFarmerName);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        agentSplitDao = (AgentSplitDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_AGENT_SPLIT);
        collectionHelper = new CollectionHelper(AggregateFarmerCollection.this);
        smartCCUtil = new SmartCCUtil(AggregateFarmerCollection.this);
        collectionTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
        validationHelper = new ValidationHelper();
        setTextDrawable();

        checkForMAManual();
        maManager = MAFactory.getMA(DeviceName.MILK_ANALYSER, AggregateFarmerCollection.this);
        wsManager = WsFactory.getWs(DeviceName.WS, AggregateFarmerCollection.this);
        if (maManager != null)
            maManager.setOnNewDataListener(new MaManager.OnNewDataListener() {
                @Override
                public void onNewData(final MilkAnalyserEntity maEntity) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (qualityTime == 0) {
                                qualityTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
                                qualityStartTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
                            }
                            if (maEntity != null) {
                                if (!smartCCUtil.validateFatAndSnfForZeroValues(maEntity.fat, maEntity.snf)) {
                                    onFinish();
                                } else {
                                    qualityEndTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
                                    QUALITY_MODE = AppConstants.AUTO;
                                    setMAData(maEntity, etFat, etSnf, etCLR);
//                                mSerialIoManagerMA.resetSIOManager();
                                    afterGettingMAData();
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
        if (wsManager != null)
            wsManager.setOnNewDataListener(new WsManager.OnNewDataListener() {
                @Override
                public void onNewData(final double data) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            isWsAdded = true;
                            setTippingStartTime();
                            resetWSMessage(data);
                        }
                    });
                }
            });
        setMilkAnalyserConnection();
    }


    private void afterGettingMAData() {

        etFat.setEnabled(false);
        etSnf.setEnabled(false);
        checkWSManual();
        closeMaConnection();
        openWeightConnection();
    }

    private void setMAData(MilkAnalyserEntity maEntity, EditText tvFat, EditText tvSnf, EditText tvCLR) {

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
        if (maEntity == null) {
            maEntity = initializeMAEntity();
        }

        maEntity.fat = Double.valueOf(chooseDecimalFormat.getFatAndSnfFormat().format(maEntity.fat));
        maEntity.snf = Double.valueOf(chooseDecimalFormat.getFatAndSnfFormat().format(maEntity.snf));
        maEntity.clr = Double.valueOf(chooseDecimalFormat.getCLRFormat().format(maEntity.clr));

        tvFat.setText(chooseDecimalFormat.getFatAndSnfFormat().format(maEntity.fat));
        tvSnf.setText(chooseDecimalFormat.getFatAndSnfFormat().format(maEntity.snf));
        tvCLR.setText(chooseDecimalFormat.getFatAndSnfFormat().format(maEntity.clr));

        //  qualityParams.serialNumber =
        qualityParams.qualityEndTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
        //  qualityParams.qualityMode =
        qualityParams.awm = maEntity.addedWater;

        qualityParams.fat = maEntity.fat;
        qualityParams.snf = maEntity.snf;
//        qualityParams.alcohol = Double.parseDouble(
//                checkValidation.getDoubleFromString(decimalFormatQuality,maEntity.a)
//        );
        qualityParams.clr = maEntity.clr;
        qualityParams.lactose = maEntity.lactose;
        qualityParams.calibration = maEntity.calibration;
        qualityParams.conductivity = maEntity.conductivity;
        qualityParams.salt =
                maEntity.salt;
        qualityParams.density = maEntity.density;
        qualityParams.freezingPoint = maEntity.freezingPoint;

        qualityParams.pH = maEntity.pH;
        qualityParams.protein = maEntity.protein;
        qualityParams.temperature = maEntity.temp;
        qualityParams.qualityMode = QUALITY_MODE;

        qualityParams.maName = amcuConfig.getMA();

        qualityParams.qualityTime = qualityTime;
        qualityParams.qualityStartTime = qualityStartTime;
        qualityParams.qualityEndTime = qualityEndTime;
    }

    private void setWSData() {

        if (quantityEntity == null) {
            return;
        }
        quantityParams.weighingQuantity = quantityEntity.displayQuantity;
        quantityParams.kgQuantity = quantityEntity.kgQuantity;
        quantityParams.ltrQuantity = quantityEntity.ltrQuanity;

        quantityParams.quantityMode = QUANTITY_MODE;
        quantityParams.measurementUnit = quantityEntity.unit;

        // quantityParams.quantityTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
        quantityParams.quantityTime = quantityTime;
        quantityParams.tippingStartTime = tippingStartTime;
        quantityParams.tippingEndTime = tippingEndTime;

    }

    /**
     * Check for the milk analyzer and read the corresponding connection
     */
    private synchronized void setMilkAnalyserConnection() {


        new Thread(new Runnable() {

            @Override
            public void run() {

                if (maManager != null) {
                    maManager.startReading();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:

                if (qualityCLRValidation()) {

                    if (quantityValidation()) {
                        if (!qualityValidation()) {
                            showToastMessage("Invalid quality parameters!");
                            return;
                        }
//
//                         if (!quantityValidation()) {
//                             showToastMessage("Invalid quantity!");
//                             return;
//                         }
                        tareWeighingScale();
                        addToDatabase();
                        onFinish();
                    } else {
                        Util.displayErrorToast("Please enter valid Quantity Value", mActivity);

                    }
                } else {
                    Util.displayErrorToast("Invalid quality parameters!", mActivity);
                }
                break;
            case R.id.btnCancel:
                finish();
                break;

        }
    }


    public void resetWSMessage(double str1) {
        setWeight(str1);
//        setWeight(parallelFunction.parseWeighingScaleData(str1, true));
        setWSData();
        messageWS = new StringBuilder();
        setTippingEndTime();
    }

    public void setTippingEndTime() {
        tippingEndTime = System.currentTimeMillis();
    }

    public void setTippingStartTime() {
        if (tippingStartTime == 0) {
            tippingStartTime = System.currentTimeMillis();
        }
    }

    private void openWeightConnection() {

        if (!isWsAdded) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (wsManager != null) {
                        wsManager.openConnection();
                    }
                }
            }).start();

        }

    }

    public void setWeight(double record) {
        try {
            quantityEntity = collectionHelper.getQuantityItems(record);
            etQty.setText(String.valueOf(quantityEntity.displayQuantity));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void showToastMessage(String message) {
        Util.displayErrorToast(message, AggregateFarmerCollection.this);
    }

    public void onFinish() {
        startActivity(new Intent(AggregateFarmerCollection.this, com.devapp.devmain.agentfarmersplit.AgentFarmerActivity.class));
        finish();
    }

    public boolean checkValidation() {
        boolean isValidQuality =
                validationHelper.isValidFatAndSnf(
                        validationHelper.getDoubleFromString(
                                String.valueOf(maEntity.fat), -1),
                        validationHelper.getDoubleFromString(
                                String.valueOf(maEntity.snf), -1), AggregateFarmerCollection.this);
        return isValidQuality;
    }

    private AgentSplitEntity getSplitCollectionEntity(int commited) {

        //////////For testing
//        qualityParams.fat = Double.parseDouble(etFat.getText().toString());
//        qualityParams.snf = Double.parseDouble(etSnf.getText().toString());
//        qualityParams.qualityTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
//        qualityParams.awm = 0;
//        qualityParams.qualityMode = AppConstants.AUTO;
//
//        // qualityParams.maNumber;
//        qualityParams.maName = saveSession.getMa1Name();
//        qualityParams.milkStatusCode = MILK_STATUS_CODE;


        AgentSplitEntity splitCollectionEntity = new AgentSplitEntity();
        splitCollectionEntity.quantityParams = quantityParams;
        splitCollectionEntity.qualityParams = qualityParams;
        splitCollectionEntity.rateParams = new RateParams();
        splitCollectionEntity.shift = SmartCCUtil.getFullShift(Util.getCurrentShift());
        splitCollectionEntity.date = smartCCUtil.getReportFormatDate();
        splitCollectionEntity.sent = DatabaseEntity.FARMER_UNSENT_CODE;
        splitCollectionEntity.agentId = etAgentId.getText().toString().trim();
        splitCollectionEntity.centerId = new SessionManager(AggregateFarmerCollection.this).getCollectionID();
        splitCollectionEntity.producerId = etFarmerId.getText().toString().trim();
        splitCollectionEntity.status = ReportHintConstant.REPORT_ACCEPT_STATUS;
        splitCollectionEntity.milkType = ((FarmerEntity) farmerDao.findById(etFarmerId.getText().toString().trim())).farmer_cattle;
        splitCollectionEntity.mode = ReportHintConstant.REPORT_ENTRY_MANUAL;
        splitCollectionEntity.commited = commited;
        splitCollectionEntity.collectionTime = collectionTime;
        splitCollectionEntity.collectionType = Util.REPORT_TYPE_AGENT_SPLIT;
        splitCollectionEntity.userId = new SessionManager(AggregateFarmerCollection.this).getUserId();


        splitCollectionEntity.parentSeqNum = PARENT_ID;

        splitCollectionEntity.postDate = smartCCUtil.getDateInPostFormat();
        splitCollectionEntity.postShift = smartCCUtil.getShiftInPostFormat(AggregateFarmerCollection.this);
        splitCollectionEntity.smsSent = 0;

        return splitCollectionEntity;
    }

    private void addToDatabase() {
        setManualTime();
        setWeight(validationHelper.getDoubleFromString(etQty.getText().toString().trim(), 0));
        setWSData();
        setMAData(maEntity, etFat, etSnf, etCLR);
        AgentSplitEntity agentSplitEntity = getSplitCollectionEntity(Util.REPORT_COMMITED);
        agentSplitEntity.resetSentMarkers();
        agentSplitDao.saveOrUpdate(agentSplitEntity);

    }


    public void setManualTime() {
        if (quantityTime == 0) {
            quantityTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
            tippingEndTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
            tippingStartTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
        }

        if (qualityTime == 0) {
            qualityTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
            qualityStartTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
            qualityEndTime = smartCCUtil.getCalendarInstance(0).getTimeInMillis();
        }

    }


    private boolean qualityValidation() {
        double fat = validationHelper.getDoubleFromString(etFat.getText().toString().trim(), -1);
        double snf = validationHelper.getDoubleFromString(etSnf.getText().toString().trim(), -1);

        if (etFat.getText().toString().trim().equalsIgnoreCase("") &&
                etSnf.getText().toString().equalsIgnoreCase("")) {
            return true;
        } else return smartCCUtil.validateFatAndSnfForZeroValues(fat, snf);
    }

    private boolean qualityCLRValidation() {
        double CLR = validationHelper.getDoubleFromString(etCLR.getText().toString().trim(), 0);
        return CLR >= 0 && CLR < 100;

    }


    private boolean quantityValidation() {
        double quantity = validationHelper.getDoubleFromString(etQty.getText().toString().trim(), -1);

        return quantity > 0 && quantity <= AmcuConfig.getInstance().getMaxlimitOfWeight();
    }


    private MilkAnalyserEntity getManualMAEntity() {
        MilkAnalyserEntity milkAnalyserEntity = new MilkAnalyserEntity();

        milkAnalyserEntity.fat = validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0);
        milkAnalyserEntity.snf = validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0);

        milkAnalyserEntity.addedWater = 0;

        return getManualMAEntity();

    }


    private void setTextDrawable() {
        smartCCUtil.toSetDrawableOnText(etCLR, "CLR %:");

        smartCCUtil.toSetDrawableOnEdit(etFat, "FAT %: ");
        smartCCUtil.toSetDrawableOnText(etSnf, "SNF %: ");

        //  smartCCUtil.toSetDrawableOnText(etAgentId, "Aggregate farmer Id:");
        //  smartCCUtil.toSetDrawableOnText(etFarmerId, "Member Id: ");
        smartCCUtil.toSetDrawableOnText(etQty, "Quantity: ");
        //smartCCUtil.toSetDrawableOnEdit(etName, "Member Name:");
        //  smartCCUtil.toSetDrawableOnEdit(etAggregateFarmerName, "Aggregate Farmer Name:");


    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_F10:
                Util.restartApp(AggregateFarmerCollection.this);
                return true;

            case KeyEvent.KEYCODE_F11:
                Util.restartTab(AggregateFarmerCollection.this);
                return true;
            case KeyEvent.KEYCODE_F12:
                Util.shutDownTab(AggregateFarmerCollection.this, null);
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


                return true;
            default:
                return super.onKeyUp(keyCode, event);
        }
    }


    /**
     * on start any manual entry
     *
     * @param input
     */
    private void checkForManualMAorWS(int input) {
        if (etFat.hasFocus()) {
            if (QUALITY_MODE.equalsIgnoreCase("Auto")) {
                etFat.setText("");
                etFat.setText(String.valueOf(input));
                etFat.setSelection(etFat.getText().toString().length());
            }
            QUALITY_MODE = com.devapp.devmain.agentfarmersplit.AppConstants.MANUAL;
            closeMaConnection();

        } else if (etSnf.hasFocus()) {
            if (QUALITY_MODE.equalsIgnoreCase("Auto")) {
                etSnf.setText("");
                etSnf.setText(String.valueOf(input));
                etSnf.setSelection(etSnf.getText().toString().length());
            }
            QUALITY_MODE = com.devapp.devmain.agentfarmersplit.AppConstants.MANUAL;
            closeMaConnection();
        } else if (etQty.hasFocus()) {

            if (QUANTITY_MODE.equalsIgnoreCase("Auto")) {
                etQty.setText("");
                etQty.setText(String.valueOf(input));
                etQty.setSelection(etQty.getText().toString().length());
            }
            QUANTITY_MODE = com.devapp.devmain.agentfarmersplit.AppConstants.MANUAL;
            closeWsConnection();

        }
    }


    private void closeMaConnection() {
        if (maManager != null) {
            maManager.stopReading();
        }
    }

    private void closeWsConnection() {
        if (wsManager != null) {
            wsManager.closeConnection();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        closeMaConnection();
        closeWsConnection();
    }

    private void checkForMAManual() {
        etFat.requestFocus();
        if (amcuConfig.getMaManual()) {
            etFat.setEnabled(true);
            etSnf.setEnabled(true);

        }
    }

    private void checkWSManual() {
        if (true) {
            etQty.setEnabled(true);
            etQty.requestFocus();
        }
    }

    private MilkAnalyserEntity initializeMAEntity() {

        MilkAnalyserEntity milkAnalyserEntity = new MilkAnalyserEntity();
        milkAnalyserEntity.addedWater = 0;
        milkAnalyserEntity.fat = validationHelper.getDoubleFromString(etFat.getText().toString().trim(), 0);
        milkAnalyserEntity.snf = validationHelper.getDoubleFromString(etSnf.getText().toString().trim(), 0);

        if (etCLR.getText().toString().length() > 0) {
            milkAnalyserEntity.clr = validationHelper.getDoubleFromString(etCLR.getText().toString().trim(), 0);

        } else {
            milkAnalyserEntity.clr = Util.getCLR(milkAnalyserEntity.fat,
                    milkAnalyserEntity.snf);
        }
        milkAnalyserEntity.density = 0;
        milkAnalyserEntity.temp = 0;
        milkAnalyserEntity.freezingPoint = 0;
        milkAnalyserEntity.lactose = 0;
        milkAnalyserEntity.conductivity = 0;
        milkAnalyserEntity.protein = 0;
        milkAnalyserEntity.pH = 0;

        return milkAnalyserEntity;

    }

    public void tareWeighingScale() {

        if (wsManager != null) {
            wsManager.tare();
        }

    }


}
