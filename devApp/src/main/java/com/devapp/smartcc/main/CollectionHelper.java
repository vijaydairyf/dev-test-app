package com.devapp.smartcc.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.devapp.kmfcommon.WeightCollectionActivityV2;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.dao.CollectionRecordDao;
import com.devapp.devmain.dao.DaoFactory;
import com.devapp.devmain.devicemanager.DatabaseManager;
import com.devapp.devmain.devicemanager.WeighingScaleManager;
import com.devapp.devmain.entity.CenterEntity;
import com.devapp.devmain.entity.MilkAnalyserEntity;
import com.devapp.devmain.entity.ReportEntity;
import com.devapp.devmain.helper.CattleType;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.macollection.CollectionActivity;
import com.devapp.devmain.macollection.MilkRejectActivity;
import com.devapp.devmain.macollection.ParallelActivity;
import com.devapp.devmain.macollection.SampleCollectionActivity;
import com.devapp.devmain.macollection.UpdatePastRecord;
import com.devapp.devmain.main.CalculateBonus;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.server.DatabaseHandler;
import com.devapp.devmain.server.SessionManager;
import com.devapp.devmain.user.Util;
import com.devapp.devmain.util.ValidationHelper;
import com.devapp.smartcc.entityandconstants.QuantityEntity;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;
import com.devapp.smartcc.entityandconstants.SmartCCUtil;
import com.devApp.R;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DecimalFormat;

/**
 * Created by Upendra on 11/22/2016.
 */
public class CollectionHelper {

    public static final double DEFAULT_DIPPING_VALUE = -100;
    public static int FROM_MILKCOLLECTION = 0;
    public static int FROM_WEIGHTCOLLECTION = 1;
    public static int FROM_UPDATE_PAST_RECORD = 2;
    public static int FROM_MILK_REJECT_ACTIVITY = 3;
    public static int FROM_SAMPLE_ACTIIVITY = 4;
    DatabaseHandler databaseHandler;
    AmcuConfig amcuConfig;
    SessionManager sessionManager;
    DecimalFormat decimalFormatWeight, decimalFormat2Digit;
    WeighingScaleManager wsManager;
    ValidationHelper validationHelper;
    SmartCCUtil smartCCUtil;
    DecimalFormat decimalFormatRate, decimalFormatAmout;
    AlertDialog alertDialog;
    AlertDialog.Builder alertBuilderBoth;
    Spinner spSelectMilkType;
    String selectMilkType = "COW";
    boolean isBoth = true;
    private Context mContext;
    private CollectionActivity collectionActivity;
    private WeightCollectionActivityV2 weightCollectionActivity;
    private UpdatePastRecord mUpdatePastRecord;
    private MilkRejectActivity mMilkRejectActivity;
    private SampleCollectionActivity mSampleCollectionActivity;

    private CollectionRecordDao collectionRecordDao;

    public CollectionHelper(Context ctx) {

        this.mContext = ctx;
        databaseHandler = DatabaseHandler.getDatabaseInstance();

        amcuConfig = AmcuConfig.getInstance();
        sessionManager = new SessionManager(mContext);
        collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);

        ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();

        decimalFormatWeight = chooseDecimalFormat.getWeightDecimalFormat();
        decimalFormat2Digit = chooseDecimalFormat.get2DigitFormatWeight();

        decimalFormatAmout = chooseDecimalFormat.getAmountDecimalFormat();
        decimalFormatRate = chooseDecimalFormat.getRateDecimalFormat();

        smartCCUtil = new SmartCCUtil(mContext);

        wsManager = new WeighingScaleManager(mContext);
        validationHelper = new ValidationHelper();
    }

    public String getMilkTypeFromCenter(String centerId) {
        return databaseHandler.getMilkTypeFromCenter(centerId);

    }

    /**
     * To check route is valid or not
     *
     * @param routeId
     * @return
     */
    public boolean isValidRoute(String routeId) {
        boolean isValidRoute = false;

        if (routeId == null || routeId.length() < 1) {
            isValidRoute = false;
        } else if (routeId.equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            isValidRoute = true;
        } else {
            DatabaseEntity databaseEntity = new DatabaseEntity(mContext);
            isValidRoute = databaseEntity.checkForIfValueExist(DatabaseHandler.TABLE_CHILLING_CENTER,
                    DatabaseHandler.KEY_CHILLING_ROUTE, routeId);
        }
        return isValidRoute;

    }

    /**
     * to check for entered center is valid or not, based on barcode or simple Id
     *
     * @param id
     * @param isCenterId
     * @return
     */
    public CenterEntity isValidCenter(String id, boolean isCenterId) {
        CenterEntity centerEntity = null;
        if (isCenterId) {
            try {
                centerEntity = databaseHandler.getCenterEntity(id
                        .replace(" ", ""), Util.CHECK_DUPLICATE_CENTERCODE);

            } catch (Exception e) {
            }

        } else {

            if (id.replace(" ", "").length() > 10) {
                try {
                    centerEntity = databaseHandler.getCenterEntity(id.replace(" ", ""),
                            Util.CHECK_DUPLICATE_CENTERBARCODE);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return centerEntity;
    }

    /**
     * @param centerId
     * @param routeId
     * @return
     */
    public boolean isValidCenterForRoute(String centerId, String routeId) {
        if (routeId == null || routeId.length() < 1) {
            return false;

        } else if (routeId.equalsIgnoreCase(SmartCCConstants.SELECT_ALL)) {
            return true;
        } else {
            String query = DatabaseEntity.validateDataFromOther(DatabaseHandler.TABLE_CHILLING_CENTER,
                    DatabaseHandler.KEY_CHILLING_CENTER_ID, DatabaseHandler.KEY_CHILLING_ROUTE,
                    centerId, routeId);
            boolean isValid = databaseHandler.validateDataByQuery(query);
            return isValid;

        }

    }

    public boolean isRateChartAvailableForCenterCattleType(String cattleType) {
        return false;
    }

    public boolean isMultiple(CenterEntity centerEntity) {
        String cattleType = centerEntity.cattleType;
        String singleOrMultiple = centerEntity.singleOrMultiple;

        if (centerEntity != null && cattleType.equalsIgnoreCase("BOTH")) {

            //This is check avoid duplicate collection
            if (singleOrMultiple.equalsIgnoreCase(Util.SINGLE)
                    && checkForCenterDuplicate(centerEntity.centerId)) {
                Util.displayErrorToast("Collection is already done for center " + centerEntity.centerId
                                + " and milkType " + sessionManager.getMilkType()
                        , mContext);
                return false;
            }
        }

        return true;
    }

    public boolean validateNumberOfCans(EditText etNumberOfCans) {
        int num = 0;
        try {
            num = Integer.parseInt(etNumberOfCans.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (num > SmartCCConstants.MIN_NUM_OF_CANS && num < SmartCCConstants.MAX_NUM_OF_CANS) {
            return true;
        } else {
            Util.displayErrorToast("Enter valid number of cans", mContext);
            return false;
        }
    }

    //Milk analyser related function

    public boolean checkForCenterDuplicate(String centerId) {
        boolean isDuplicate = false;
        isDuplicate = databaseHandler.checkForDuplicateChillingRecord("WS", centerId, sessionManager.getMilkType());
        return isDuplicate;
    }

    public int getCurrentSID() {
        CollectionRecordDao collectionRecordDao = (CollectionRecordDao) DaoFactory.getDao(CollectionConstants.REPORT_TYPE_COLLECTION);
        /*DatabaseHandler dbh = DatabaseHandler.getDatabaseInstance();
        int lastSid = dbh.getReportLastSequenceNumber(smartCCUtil.getReportFormatDate(),
                Util.getShift(mContext));*/
        int currentSID = collectionRecordDao.getNextSampleNumber(smartCCUtil.getReportFormatDate(),
                Util.getCurrentShift());

        return currentSID;
    }


    //Create report entity for milk analyser

    public QuantityEntity getQuantityItems(double weight_Record) {
        QuantityEntity quantityEntity = new QuantityEntity();
        quantityEntity.readQuantity = weight_Record;
        quantityEntity.unit = AppConstants.LTR_UNIT;
        if (!amcuConfig.getAllowInKgformat() && amcuConfig.getMyRateChartEnable()) {
            quantityEntity.ltrQuanity = Double.valueOf(decimalFormatWeight.format(weight_Record));
            quantityEntity.displayQuantity = Double.valueOf(
                    decimalFormatWeight.format(quantityEntity.ltrQuanity *
                            Double.parseDouble(amcuConfig.getConversionFactor())));
            quantityEntity.kgQuantity = quantityEntity.displayQuantity;
            quantityEntity.unit = AppConstants.KG_UNIT;
        } else if (amcuConfig.getAllowInKgformat() && !amcuConfig.getKeyRateChartInKg()) {
            quantityEntity.kgQuantity = Double.valueOf(decimalFormatWeight.format(weight_Record));
            quantityEntity.displayQuantity = Double.valueOf(
                    decimalFormatWeight.format(
                            quantityEntity.kgQuantity /
                                    Double.parseDouble(amcuConfig.getConversionFactor())));
            quantityEntity.ltrQuanity = quantityEntity.displayQuantity;

        } else if (!amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            quantityEntity.ltrQuanity = Double.valueOf(decimalFormatWeight.format(weight_Record));
            quantityEntity.displayQuantity = Double.valueOf(decimalFormatWeight.format(
                    quantityEntity.ltrQuanity *
                            Double.parseDouble(amcuConfig.getConversionFactor())));
            quantityEntity.kgQuantity = quantityEntity.displayQuantity;
            quantityEntity.unit = AppConstants.KG_UNIT;
        } else if (amcuConfig.getAllowInKgformat() && amcuConfig.getKeyRateChartInKg()) {
            quantityEntity.unit = AppConstants.KG_UNIT;
            quantityEntity.kgQuantity = Double.valueOf(decimalFormatWeight.format(weight_Record));
            quantityEntity.displayQuantity = quantityEntity.kgQuantity;
            quantityEntity.ltrQuanity = Double.valueOf(decimalFormatWeight.format(quantityEntity.kgQuantity /
                    Double.parseDouble(amcuConfig.getConversionFactor())));
        } else {
            quantityEntity.ltrQuanity = Double.valueOf(decimalFormatWeight.format(weight_Record));
            quantityEntity.displayQuantity = Double.valueOf(decimalFormatWeight.format(weight_Record));
            quantityEntity.kgQuantity = Double.valueOf(decimalFormatWeight.format(quantityEntity.ltrQuanity *
                    Double.parseDouble(amcuConfig.getConversionFactor())));
        }
        return quantityEntity;

    }


    public boolean validateSID(EditText etSId, int lastSid) {

        int sid = 0;
        try {
            sid = Integer.parseInt(etSId.getText().toString().trim());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (sid <= 0) {
            Util.displayErrorToast("Please Enter Valid Serial Id", mContext);
            return false;
        }

        if ((etSId.getText().toString().trim().length() > 7)
                || (Integer.valueOf(etSId.getText().toString().trim()) > Util.LAST_SEQ_NUM)) {
            Util.displayErrorToast("SID should be less than " + Util.LAST_SEQ_NUM, mContext);
            return false;
        }
        if (etSId.getText().toString().trim().equalsIgnoreCase("0")) {
            Util.displayErrorToast("Serial Id can't be 0", mContext);
            return false;
        }
        if (etSId.getText().toString().trim().equalsIgnoreCase("")) {
            Util.displayErrorToast("Please Enter Valid Serial Id", mContext);
            return false;
        }
        if (Integer.valueOf(etSId.getText().toString().trim()) < lastSid) {

            Util.displayErrorToast("SID should be greater than or same as " + lastSid, mContext);
            return false;
        }
        return true;
    }

    public String parseWSForDippingWeight(String str) {
        double weight = DEFAULT_DIPPING_VALUE;
        String record = null;
        String[] records = wsManager.splitWSDataViaSeparator(str);
        if (records.length > 4) {
            record = records[records.length - 2];
            if (wsManager.checkForPrefixAndSuffix(record,
                    amcuConfig.getWeighingPrefix(),
                    amcuConfig.getWeighingSuffix())) {
                record = wsManager.getCorrectWeight(record);
                if (!record.equalsIgnoreCase("")) {
                    try {
                        weight = Double.parseDouble(record);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    if (weight != DEFAULT_DIPPING_VALUE) {
                        return record;
                    }
                }
            }
        }
        return record;
    }

    public String parseWeighingScaleData(String str, boolean isFirst) {

        double weight = 0;
        String record = null;

        String[] records = wsManager.splitWSDataViaSeparator(str);

        if (isFirst) {
            if (records.length > 4) {
                record = records[records.length - 2];
                if (wsManager.checkForPrefixAndSuffix(record,
                        amcuConfig.getWeighingPrefix(),
                        amcuConfig.getWeighingSuffix())) {
                    record = wsManager.getCorrectWeight(record);
                    if (!record.equalsIgnoreCase("")) {
                        try {
                            weight = Double.parseDouble(record);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        if (weight != 0 && weight > 0) {
                            if (!validationHelper.validMilkWeight(weight, mContext)) {
                                return record;
                            }
                        }
                    }
                    return record;
                } else {
                    Util.displayErrorToast("Invalid weighingScale format " + record + "," +
                            " Please check the prefix and suffix", mContext);
                }

            }
        }
        return record;
    }

/*    public ReportEntity getQuantityReportEntity(int commited, String numberOfCans, QuantityEntity quantityEntity,
                                                long quantityTime, long tippingStartTime,
                                                long tippingEndTime, String sequenceNumber) {

        int txNumber = sessionManager
                .getTXNumber() + 1;
        String currentShift = Util.getShift(mContext);

        String date = smartCCUtil.getReportFormatDate();
        String startTime = Util.getTodayDateAndTime(3, mContext, true);


        ReportEntity repEntity = new ReportEntity();
        repEntity.temp = 0.00;
        repEntity.fat = 0.00;
        repEntity.snf = 0.00;
        repEntity.rate = 0.00;
        repEntity.amount = 0.00;
        repEntity.status = "NA";
        repEntity.rate = 0.00;
        repEntity.amount = 0.00;

        repEntity.user = sessionManager.getUserId();
        repEntity.farmerId = sessionManager.getFarmerID();
        repEntity.farmerName = sessionManager.getFarmerName();
        repEntity.socId = String.valueOf(sessionManager.getSocietyColumnId());

        repEntity.awm = 0.00;
        repEntity.manual = "Manual";
        repEntity.quantityMode = "Auto";
        repEntity.qualityMode = "Auto";
        repEntity.clr = 0.00;
        repEntity.bonus = 0.00;


        repEntity.quantity = quantityEntity.weighingData;

        repEntity.time = startTime;
        repEntity.milkType = sessionManager.getMilkType();
        repEntity.lDate = Util.getDateInLongFormat(date);
        repEntity.txnNumber = Integer.parseInt( Util.getTxnNumber(txNumber));
        repEntity.miliTime = quantityTime;
        repEntity.milkAnalyserTime = quantityTime;
        repEntity.weighingTime = quantityTime;

        repEntity.recordCommited = commited;
        repEntity.collectionType = Util.REPORT_TYPE_MCC;

        int cans = 1;
        try {
            cans = Integer.parseInt(numberOfCans);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        repEntity.numberOfCans = cans;

        repEntity.sampleNumber = Integer.parseInt(sequenceNumber);
        repEntity.milkQuality = "NA";

        repEntity.centerRoute = Util.getRouteFromChillingCenter(mContext, sessionManager.getFarmerID());
        repEntity.recordStatus = Util.RECORD_STATUS_INCOMPLETE;

        repEntity.kgWeight = quantityEntity.kgQuantity;
        repEntity.ltrsWeight = quantityEntity.ltrQuanity;
        repEntity.tippingStartTime = tippingStartTime;
        repEntity.tippingEndTime = tippingEndTime;
        repEntity.milkStatusCode = smartCCUtil.getMilkStatusCode("Good");
        repEntity.postDate = SmartCCUtil.getDateInPostFormat();
        repEntity.postShift = SmartCCUtil.getShiftInPostFormat(mContext);

        smartCCUtil.setCollectionStartData(repEntity);
        return repEntity;
    }*/

    public String addQuantityToDatabase(ReportEntity repEntity) {
        String dbError = null;
        //  Toast.makeText(MilkCollectionActivity.this, "Get Selected Item" + spinnerItem, Toast.LENGTH_SHORT).show();

        Util.setDailyDateOrShift(Util.getTodayDateAndTime(7, mContext, true),
                repEntity.postShift);
        try {
            collectionRecordDao.saveOrUpdate(repEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dbError;
    }

    public boolean isValidWeight(EditText etQuantity) {
        double dWeight = -1;

        try {
            dWeight = Double.parseDouble(etQuantity.getText().toString().trim());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (dWeight > 0) {
            return validationHelper.validMilkWeight(dWeight, mContext);
        } else {
            return false;
        }
    }

    public void tareWSOverSerialManager(SerialInputOutputManager mSerialIoManager, UsbSerialPort usbSerialPort) {

        byte[] tareMsg = smartCCUtil.getTareMessage().getBytes(Charset.forName("UTF-8"));
        if (amcuConfig.getTareEnable()) {
            try {
                mSerialIoManager.writeAsync(tareMsg);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (usbSerialPort != null) {
                try {
                    usbSerialPort.write(tareMsg, 3000);
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                } catch (IOException e) {
                }
            }
        }
    }

    public boolean fatAndSnfValidation(double fat, double snf) {
        if (fat < 0 || snf < 0 || fat >= Util.MAX_FAT_LIMIT || snf >= Util.MAX_SNF_LIMIT) {
            return false;
        } else {
            return true;
        }

    }

    /**
     * To delete un-committed record with colId
     *
     * @param colId
     */
    public void deleteExisting(long colId) {
        DatabaseManager dbm = new DatabaseManager(mContext);
        dbm.deleteSelectedColumnIds(String.valueOf(colId));
    }

    public ReportEntity getCommittedReportEntity(ReportEntity reportEntity,
                                                 String isMa1OrMa2) {
        DecimalFormat decimalFormatAmount = new DecimalFormat("#0.00");

        reportEntity = smartCCUtil.getReportEntity(reportEntity);
        Util.setCollectionStartedWithMilkType(reportEntity.milkType, mContext);
        reportEntity.manual = "Manual";
        reportEntity.recordCommited = Util.REPORT_COMMITED;
        reportEntity.recordStatus = Util.RECORD_STATUS_COMPLETE;
        reportEntity.milkQuality = "GOOD";
        reportEntity.rateMode = "Auto";
        if (isMa1OrMa2.equalsIgnoreCase("Ma1")) {

            reportEntity.serialMa = 1;
            reportEntity.maName = amcuConfig.getMa1Name();
        } else {
            reportEntity.serialMa = 2;
            reportEntity.maName = amcuConfig.getMa2Name();
        }
        reportEntity = smartCCUtil.setAcceptOrRejectStatus(reportEntity);
        reportEntity = smartCCUtil.getRateFromRateChart(reportEntity);
        reportEntity.setTxnNumber(new SessionManager(mContext)
                .getTXNumber() + 1);
        reportEntity.milkStatusCode = smartCCUtil.getMilkStatusCode("GOOD");
        if (amcuConfig.getRateCalculatedFromDevice()) {
            reportEntity.rateCalculation = 1;
        } else {
            reportEntity.rateCalculation = 0;
        }
        reportEntity.setAmount(reportEntity.getDisplayRate() *
                reportEntity.getDisplayQuantity());
        //Calculating bonus amount

        reportEntity.setBonus(reportEntity.getBonusRate() *
                reportEntity.getDisplayQuantity());
        reportEntity.setIncentiveAmount(reportEntity.getIncentiveRate()
                * reportEntity.getDisplayQuantity());
        reportEntity.fatKg = Double.valueOf(decimalFormatAmount.format(Util.convertPercentageToKg(reportEntity.kgWeight,
                reportEntity.fat)));
        reportEntity.snfKg = Double.valueOf(decimalFormatAmount.format(
                Util.convertPercentageToKg(reportEntity.kgWeight, reportEntity.snf)));

        sessionManager.setTXNumber(reportEntity.txnNumber);
/*
        reportEntity.postDate = SmartCCUtil.getDateInPostFormat();
        reportEntity.postShift = SmartCCUtil.getShiftInPostFormat(mContext);*/

        smartCCUtil.setCollectionStartData(reportEntity);
        return reportEntity;
    }

    public double getBonusAmount(String id, String milkType, MilkAnalyserEntity maEntity) {

        if (!amcuConfig.getIsRateChartMandatory()) {
            return 0.00;
        } else if (Util.checkIfRateCheck(id, amcuConfig.getFarmerIdDigit()) ||
                Util.checkIfSampleCode(id, amcuConfig.getFarmerIdDigit())) {
            return 0;
        } else if (amcuConfig.getBonusEnable()) {
            CalculateBonus calculateBonus = CalculateBonus.getInstance(mContext);
            double bonus = calculateBonus.getBonus(maEntity.fat, maEntity.snf,
                    maEntity.clr, milkType);
            return bonus;
        } else {
            return 0;
        }
    }


    public synchronized String addQualityToDatabase(ReportEntity reportEntity) {

        String dbError = null;
        reportEntity.resetSentMarkers();
        Util.setDailyDateOrShift(Util.getTodayDateAndTime(7, mContext, false),
                reportEntity.postShift);
        try {
            collectionRecordDao.saveOrUpdate(reportEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return dbError;
    }

    public void getAllreportEntity(String ma1OrMa2,
                                   String lastSeqNumber, String nextSequenceNumber
            , TextView tvCenter1, TextView tvCenter2
            , TextView tvSampleId1, TextView tvSampleId2
            , TextView tvFat1
            , TextView tvFat2, TextView tvSnf1
            , TextView tvSnf2) {

        if (ma1OrMa2 == null) {
            ParallelActivity.allReportEntity = databaseHandler.getUncommittedWSRecordsEntitiesParaller(null, null, null);
        } else if (ma1OrMa2.equalsIgnoreCase(ParallelActivity.MA1)) {
            ParallelActivity.allReportEntity = databaseHandler.getUncommittedWSRecordsEntitiesParaller(nextSequenceNumber
                    , tvSampleId2.getText().toString().trim(), lastSeqNumber);
        } else if (ParallelActivity.isMultipleMA) {
            ParallelActivity.allReportEntity = databaseHandler.getUncommittedWSRecordsEntitiesParaller(nextSequenceNumber
                    , tvSampleId1.getText().toString().trim(), lastSeqNumber);
        }

        if (nextSequenceNumber == null) {
            if (ParallelActivity.allReportEntity != null && ParallelActivity.allReportEntity.size() > 1) {
                ParallelActivity.reportEntityMA1 = ParallelActivity.allReportEntity.get(0);
                ParallelActivity.reportEntityMA2 = ParallelActivity.allReportEntity.get(1);
                resetMAEdit(ma1OrMa2, ParallelActivity.reportEntityMA1, tvFat1, tvSnf1, tvSampleId1, tvCenter1);

                if (ParallelActivity.isMultipleMA) {
                    resetMAEdit(ma1OrMa2, ParallelActivity.reportEntityMA2, tvFat2, tvSnf2, tvSampleId2, tvCenter2);
                }

            } else if (ParallelActivity.allReportEntity != null && ParallelActivity.allReportEntity.size() > 0) {
                ParallelActivity.reportEntityMA1 = ParallelActivity.allReportEntity.get(0);
                resetMAEdit(ma1OrMa2, ParallelActivity.reportEntityMA1, tvFat1, tvSnf1, tvSampleId1, tvCenter1);
            } else {

            }
        } else if (ma1OrMa2.equalsIgnoreCase(ParallelActivity.MA1) && ParallelActivity.allReportEntity.size() > 0) {
            ParallelActivity.reportEntityMA1 = ParallelActivity.allReportEntity.get(0);
            resetMAEdit(ma1OrMa2, ParallelActivity.reportEntityMA1, tvFat1, tvSnf1, tvSampleId1, tvCenter1);
        } else if (ma1OrMa2.equalsIgnoreCase("MA2") && ParallelActivity.allReportEntity.size() > 0) {
            ParallelActivity.reportEntityMA2 = ParallelActivity.allReportEntity.get(0);
            if (ParallelActivity.isMultipleMA) {
                resetMAEdit(ma1OrMa2, ParallelActivity.reportEntityMA2, tvFat2, tvSnf2, tvSampleId2, tvCenter2);
            }

        } else {
            if (ma1OrMa2.equalsIgnoreCase(ParallelActivity.MA1)
                    && (ParallelActivity.allReportEntity == null || ParallelActivity.allReportEntity.size() == 0)) {
                resetMAEdit(ma1OrMa2, null, tvFat1, tvSnf1, tvSampleId1, tvCenter1);
            } else if (ma1OrMa2.equalsIgnoreCase("MA2")
                    && (ParallelActivity.allReportEntity == null || ParallelActivity.allReportEntity.size() == 0)) {
                resetMAEdit(ma1OrMa2, null, tvFat2, tvSnf2, tvSampleId2, tvCenter2);
            }

        }

        if (!(tvSampleId1.getText().toString().length() > 0)
                && !(tvSampleId2.getText().toString().length() > 0)) {

            Util.displayErrorToast("No records found!", mContext);

        }
    }

    public void resetMAEdit(String ma1OrMa2, ReportEntity reportEnt, TextView tvFat, TextView tvSnf,
                            TextView tvSampleId, TextView tvCenterId) {


        if (reportEnt == null || reportEnt.farmerId == null || reportEnt.farmerId.length() < 1) {
            tvCenterId.setText("");
            tvSampleId.setText("");
        } else {
            tvCenterId.setText(reportEnt.farmerId);
            tvSampleId.setText(String.valueOf(reportEnt.sampleNumber));
        }
        tvFat.setText("");
        tvSnf.setText("");
    }

    public void setVisibilityOfMa(CardView cardView) {

        if (cardView.getVisibility() != View.VISIBLE) {
            Util.displayErrorToast("Press f1 and complete the sample collection", mContext);
        }
    }

    public void alertForMilkTypeBoth(final String strBarcode,
                                     final boolean isFarmer, final Intent intent) {
        final Button btnCancel, btnSubmita, btnCow, btnBuffalo;
        TextView tvHeader;
        if (alertBuilderBoth != null) {

            try {
                alertDialog.dismiss();
                alertDialog.cancel();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        alertBuilderBoth = new AlertDialog.Builder(
                mContext);
        LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext()
                .getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.alert_select_milktype, null);

        tvHeader = (TextView) view.findViewById(R.id.tvHeader);
        tvHeader.setText("Select cattle type");
        spSelectMilkType = (Spinner) view.findViewById(R.id.spSelectMilkType);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnSubmita = (Button) view.findViewById(R.id.btnSubmit);
        btnCow = (Button) view.findViewById(R.id.btnCow);
        btnBuffalo = (Button) view.findViewById(R.id.btnBuffalo);
        alertDialog = alertBuilderBoth.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setView(view);
        alertDialog.show();

        btnSubmita.requestFocus();


        if (!isFarmer) {
            selectMilkType = "COW";
            btnCow.requestFocus();
            btnBuffalo.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button));
            btnCow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_selected));
            btnCow.setTextColor(mContext.getResources().getColor(R.color.white));
        }

        btnCow.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT))) {
                    btnCow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button));
                    btnCow.setTextColor(mContext.getResources().getColor(R.color.black));
                    btnBuffalo.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_selected));
                    btnBuffalo.setTextColor(mContext.getResources().getColor(R.color.white));
                    btnBuffalo.requestFocus();
                    selectMilkType = "BUFFALO";
                    return true;
                } else if (selectMilkType != null && ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    isBoth = false;
                    selectMilkType = "COW";
                    //  etBarcode.setText(strBarcode);
                    onSubmitCenter();
                    return true;
                }
                return false;
            }
        });

        btnBuffalo.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DPAD_LEFT))) {
                    btnBuffalo.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button));
                    btnBuffalo.setTextColor(mContext.getResources().getColor(R.color.black));
                    btnCow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_selected));
                    btnCow.setTextColor(mContext.getResources().getColor(R.color.white));
                    btnCow.requestFocus();
                    selectMilkType = "COW";
                    return true;
                } else if (selectMilkType != null && ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER))
                        || ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))) {
                    isBoth = false;
                    selectMilkType = "BUFFALO";
                    onSubmitCenter();
                    return true;
                }
                return false;
            }
        });


        spSelectMilkType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectMilkType = spSelectMilkType.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnCow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBuffalo.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button));
                btnBuffalo.setTextColor(mContext.getResources().getColor(R.color.black));
                btnCow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_selected));
                btnCow.setTextColor(mContext.getResources().getColor(R.color.white));
                btnCow.requestFocus();
                selectMilkType = "COW";
            }
        });

        btnBuffalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button));
                btnCow.setTextColor(mContext.getResources().getColor(R.color.black));
                btnBuffalo.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.round_button_selected));
                btnBuffalo.setTextColor(mContext.getResources().getColor(R.color.white));
                btnBuffalo.requestFocus();
                selectMilkType = "BUFFALO";
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBoth = true;
                alertDialog.dismiss();
            }
        });

        btnSubmita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBoth = false;
                onSubmitCenter();
            }
        });


        // To display the alert dialog in center

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int densityDpi = (int) (metrics.density * 160f);
        lp.copyFrom(alertDialog.getWindow().getAttributes());

        int width = metrics.widthPixels;
        int height = metrics.heightPixels;


        LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(height, width / 2); // Width , height


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

    public void onSubmitCenter() {
    }


    /**
     * In case of Auto if quantity is more than the max limit
     * show the alert, to allow with exceeded weight or cancel
     *
     * @param comingFrom
     */

    public void showAlertForManualWeight(final int comingFrom) {

        if (comingFrom == FROM_MILKCOLLECTION) {
            collectionActivity = CollectionActivity.getInstance();
        } else if (comingFrom == FROM_WEIGHTCOLLECTION) {
            weightCollectionActivity = WeightCollectionActivityV2.getInstance();
        } else if (comingFrom == FROM_UPDATE_PAST_RECORD) {
            mUpdatePastRecord = UpdatePastRecord.getInstance();
        } else if (comingFrom == FROM_MILK_REJECT_ACTIVITY) {
            mMilkRejectActivity = MilkRejectActivity.getInstance();
        } else if (comingFrom == FROM_SAMPLE_ACTIIVITY) {
            mSampleCollectionActivity = SampleCollectionActivity.getInstance();
        } else {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setCancelable(false);
        builder.setTitle("Exceeding quantity alert!");
        builder.setMessage("Entered quantity should be more than min limit " + amcuConfig.getKeyMinValidWeight() +
                " and less than max limit " + amcuConfig.getMaxlimitOfWeight()
                + ", press Cancel to enter correct quantity or press OK to proceed.");
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (comingFrom == FROM_MILKCOLLECTION) {
                    collectionActivity.exceedWeightLimit();
                } else if (comingFrom == FROM_WEIGHTCOLLECTION) {
                    weightCollectionActivity.exceedWeightLimit();
                } else if (comingFrom == FROM_UPDATE_PAST_RECORD) {
                    mUpdatePastRecord.exceedWeightLimit();
                } else if (comingFrom == FROM_MILK_REJECT_ACTIVITY) {
                    mMilkRejectActivity.exceedWeightLimit();
                } else if (comingFrom == FROM_SAMPLE_ACTIIVITY) {
                    mSampleCollectionActivity.exceedWeightLimit();
                }
                alertDialog.cancel();


            }
        });

        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (comingFrom == FROM_MILKCOLLECTION) {
                    collectionActivity.proceedWithExceedWeightLimit();
                } else if (comingFrom == FROM_WEIGHTCOLLECTION) {
                    weightCollectionActivity.proceedWithExceedWeightLimit();
                } else if (comingFrom == FROM_UPDATE_PAST_RECORD) {
                    mUpdatePastRecord.proceedWithExceedWeightLimit();
                } else if (comingFrom == FROM_MILK_REJECT_ACTIVITY) {
                    mMilkRejectActivity.proceedWithExceedWeightLimit();
                } else if (comingFrom == FROM_SAMPLE_ACTIIVITY) {
                    mSampleCollectionActivity.proceedWithExceedWeightLimit();
                }
                alertDialog.cancel();

            }
        });

        if (alertDialog == null || !alertDialog.isShowing()) {
            alertDialog = builder.create();
            alertDialog.show();
        }


    }

    /**
     * Set all the default parameter in report entity
     *
     * @return
     */
    public ReportEntity getDefaultReportEntityForQuantity() {
        ReportEntity mReportEntity = new ReportEntity();
        mReportEntity.user = sessionManager.getUserId();
        mReportEntity.farmerId = sessionManager.getFarmerID();
        mReportEntity.farmerName = sessionManager.getFarmerName();
        mReportEntity.setMilkType(sessionManager.getMilkType());
        mReportEntity.socId = sessionManager.getCollectionID();
        mReportEntity.postDate = smartCCUtil.getReportFormatDate();
        mReportEntity.postShift = SmartCCUtil.getShiftInPostFormat(mContext);
        mReportEntity.centerRoute = Util.getRouteFromChillingCenter(mContext, sessionManager.getFarmerID());
        mReportEntity.rateChartName = amcuConfig.getRateChartName();
        mReportEntity.lDate = Util.getDateInLongFormat(mReportEntity.postDate);
        mReportEntity.time = Util.getTodayDateAndTime(3, mContext, true);
        mReportEntity.collectionType = Util.REPORT_TYPE_MCC;
        mReportEntity.quantityMode = SmartCCConstants.AUTO;
        mReportEntity.qualityMode = SmartCCConstants.AUTO;
        mReportEntity.rateMode = SmartCCConstants.AUTO;
        mReportEntity.milkStatusCode = smartCCUtil.getMilkStatusCode("GOOD");
        if (amcuConfig.getRateCalculatedFromDevice()) {
            mReportEntity.rateCalculation = SmartCCConstants.RATE_FROM_DEVICE;
        } else {
            mReportEntity.rateCalculation = SmartCCConstants.RATE_FROM_CLOUD;
        }

        mReportEntity.sampleNumber = 0;
        mReportEntity.serialMa = 1;
        mReportEntity.maName = amcuConfig.getMA();
        mReportEntity.setTxnNumber(mReportEntity.txnNumber + 1);

        mReportEntity.status = "NA";
        mReportEntity.milkQuality = "NA";

        mReportEntity.recordCommited = Util.REPORT_NOT_COMMITED;
        mReportEntity.recordStatus = Util.RECORD_STATUS_INCOMPLETE;


        mReportEntity.miliTime = System.currentTimeMillis();

        return mReportEntity;
    }

    public double getDynamicAmount(String milkType, MilkAnalyserEntity maEntity) {
        // removed check for sample Util.checkIfSampleCode(id, saveSession.getFarmerIdDigit())
        // as dynamic RC should also be picked up for sample checks
//        if (Util.checkIfRateCheck(id, saveSession.getFarmerIdDigit())) {
//            return 0;
//        } else {
        CalculateBonus calculateBonus = CalculateBonus.getInstance(mContext);

        double bonus = calculateBonus.getDynamicAmount(maEntity, milkType);
        return bonus;
//        }
    }


    public String getMilkTypeFromConfiguration(ReportEntity reportEntity) {
        String milkType = reportEntity.milkType;


        if (CattleType.TEST.equalsIgnoreCase(milkType)) {
            return milkType;
        }

        try {
            double configuredFat = Double.parseDouble(amcuConfig.getChangeFat());
            if (configuredFat > 0 && reportEntity.fat >= configuredFat) {
                milkType = CattleType.BUFFALO;
            }
        } catch (Exception e) {
        }

        return milkType;

    }


}
