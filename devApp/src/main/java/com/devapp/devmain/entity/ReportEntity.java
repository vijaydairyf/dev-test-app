package com.devapp.devmain.entity;

import com.devapp.devmain.ConsolidationPost.ConsolidatedHelperMethods;
import com.devapp.devmain.agentfarmersplit.AppConstants;
import com.devapp.devmain.helper.DatabaseEntity;
import com.devapp.devmain.multipleequipments.ChooseDecimalFormat;
import com.devapp.devmain.postentities.CollectionConstants;
import com.devapp.devmain.postentities.MilkAnalyser;
import com.devapp.devmain.postentities.QualityMetaData;
import com.devapp.devmain.postentities.QualityParamsPost;
import com.devapp.devmain.postentities.QualityReadingData;
import com.devapp.devmain.postentities.QuantityParamspost;
import com.devapp.devmain.postentities.RateParamsPost;
import com.devapp.devmain.postentities.TemperatureData;
import com.devapp.devmain.postentities.WeighingScaleData;
import com.devapp.devmain.server.AmcuConfig;
import com.devapp.devmain.user.Util;
import com.devapp.smartcc.entityandconstants.SmartCCConstants;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

//TODO Fix all JSON keys
@TransactionalEntity
public class ReportEntity implements Entity, Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public long columnId;
    public String farmerId;
    public String farmerName;
    public double snf;
    public double fat;
    public double rate;
    public String user;
    public String manual;
    public double amount;
    public double quantity;
    public int txnNumber;
    public String milkType;
    public long lDate;
    public String socId;
    public double bonus;
    public String time;
    public double awm;
    public String status;
    public double clr;
    public String quantityMode;
    public String qualityMode;
    public long milkAnalyserTime;
    public long weighingTime;
    public long miliTime;
    //this will be 0 if not cmmited if print or reject button are not pressed else 1
    public int recordCommited;
    public String collectionType;
    public String milkQuality;

    //Rate mode
    public String rateMode;
    //Number of cans
    public int numberOfCans;
    public String centerRoute;
    public int sampleNumber;

    //Adding route and sequence number as per DB upgrade 17
    //This entity created to handle uncommited records from previous shift
    public String recordStatus;
    public long editedTime;
    public String oldOrNewFlag;

    //Entity for edited report
    public long foreignSequenceNum;

    public double kgWeight;
    public double ltrsWeight;
    public String rateChartName;

    //To support smartCc report

    public long tippingStartTime;
    public long tippingEndTime;
    public String agentId;
    public int milkStatusCode;

    //rate calculation from server for device side
    @JsonProperty("isRateCalculated")
    public int rateCalculation;

    public String maName;
    public int serialMa;
    public double lactose;
    public double salt;
    public double protein;
    public double freezingPoint;
    public double pH;
    public double conductivity;
    public double density;

    public double temp;
    public String reportType;


    public String calibration;
    public String maSerialNumber;
    public double incentiveRate;
    public double incentiveAmount;

    public double fatKg;
    public double snfKg;
    public int sentStatus;
    public int sentSmsStatus;
    public String postDate;
    public String postShift;
    public long sequenceNum;


    @JsonIgnore
    public int collectionStatus;
    public transient Map<Integer, Boolean> mapCollectionStatus;
    public double newCanRecord;
    @JsonIgnore
    public long lastModified;
    @JsonIgnore
    public int sampleType;
    @JsonIgnore
    public double bonusRate;
    @JsonIgnore
    public double dynamicRate;
    @JsonIgnore

    private transient ChooseDecimalFormat chooseDecimalFormat = new ChooseDecimalFormat();
    @JsonIgnore
    private transient AmcuConfig amcuConfig = AmcuConfig.getInstance();
    @JsonIgnore
    private boolean isSelected;
    @JsonIgnore
    private double newCanLiterData;
    @JsonIgnore
    private double newCanKgData;

    public ReportEntity(ReportEntity entity) {
        ReportEntity reportEntity = new ReportEntity();
        this.columnId = entity.getColumnId();
        this.farmerId = entity.getFarmerId();
        this.farmerName = entity.getFarmerName();
        this.snf = entity.getSnf();
        this.fat = entity.getFat();
        this.rate = entity.getRate();
        this.user = entity.getUser();
        this.manual = entity.getManual();
        this.amount = entity.getAmount();
        this.quantity = entity.getQuantity();
        this.txnNumber = entity.getTxnNumber();
        this.milkType = entity.getMilkType();
        this.lDate = entity.getlDate();
        this.socId = entity.getSocId();
        this.bonus = entity.getBonus();
        this.time = entity.getTime();
        this.awm = entity.getAwm();
        this.status = entity.getStatus();
        this.clr = entity.getClr();
        this.quantityMode = entity.getQuantityMode();
        this.qualityMode = entity.getQualityMode();
        this.milkAnalyserTime = entity.getMilkAnalyserTime();
        this.weighingTime = entity.getWeighingTime();
        this.temp = entity.getTemp();
        this.miliTime = entity.getMiliTime();
        this.recordCommited = entity.getRecordCommited();
        this.collectionType = entity.getCollectionType();
        this.milkQuality = entity.getMilkQuality();
        this.rateMode = entity.getRateMode();
        this.numberOfCans = entity.getNumberOfCans();
        this.centerRoute = entity.getCenterRoute();
        this.sampleNumber = entity.getSampleNumber();
        this.recordStatus = entity.getRecordStatus();
        this.editedTime = entity.getEditedTime();
        this.oldOrNewFlag = entity.getOldOrNewFlag();
        this.foreignSequenceNum = entity.getForeignSequenceNum();
        this.kgWeight = entity.getKgWeight();
        this.ltrsWeight = entity.getLtrsWeight();
        this.rateChartName = entity.getRateChartName();
        this.tippingStartTime = entity.getTippingStartTime();
        this.tippingEndTime = entity.getTippingEndTime();
        this.agentId = entity.getAgentId();
        this.milkStatusCode = entity.getMilkStatusCode();
        this.rateCalculation = entity.getRateCalculation();
        this.maName = entity.getMaName();
        this.serialMa = entity.getSerialMa();
        this.lactose = entity.getLactose();
        this.salt = entity.getSalt();
        this.protein = entity.getProtein();
        this.freezingPoint = entity.getFreezingPoint();
        this.pH = entity.getpH();
        this.conductivity = entity.getConductivity();
        this.density = entity.getDensity();
        this.reportType = entity.getReportType();
        this.calibration = entity.getCalibration();
        this.maSerialNumber = entity.getMaSerialNumber();
        this.incentiveRate = entity.getIncentiveRate();
        this.incentiveAmount = entity.getIncentiveAmount();

        this.fatKg = entity.getFatKg();
        this.snfKg = entity.getSnfKg();
        this.sentStatus = entity.getSentStatus();
        this.sentSmsStatus = entity.getSentSmsStatus();
        this.postDate = entity.getPostDate();
        this.postShift = entity.getPostShift();
        this.sequenceNum = entity.getSequenceNum();
        this.lastModified = entity.getLastModified();
    }


    public ReportEntity() {
        this.sentSmsStatus = CollectionConstants.UNSENT;
        this.sentStatus = CollectionConstants.UNSENT;
        this.recordCommited = Util.REPORT_NOT_COMMITED;
        initialize();
    }

    public ReportEntity(int sentStatus, int sentSmsStatus) {
        this.sentStatus = sentStatus;
        this.sentSmsStatus = sentSmsStatus;
        initialize();
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void initialize() {
        chooseDecimalFormat = new ChooseDecimalFormat();
        amcuConfig = AmcuConfig.getInstance();
    }

    public double getIncentiveAmount() {
        return incentiveAmount;
    }

    public void setIncentiveAmount(double incentiveAmount) {
        this.incentiveAmount =
                Double.parseDouble(chooseDecimalFormat.getAmountDecimalFormat().format(incentiveAmount));
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String farmerId) {
        this.farmerId = farmerId;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String farmerName) {
        this.farmerName = farmerName;
    }

    public double getSnf() {
        return snf;
    }

    public void setSnf(double snf) {
        this.snf = snf;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getManual() {
        return manual;
    }

    public void setManual(String manual) {
        this.manual = manual;
    }

    public double getAmount() {
        return Double.parseDouble(chooseDecimalFormat.getAmountDecimalFormat().format(
                this.amount));
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getQuantity() {

        return Double.parseDouble(chooseDecimalFormat.getWeightDecimalFormat().format(
                this.quantity));
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getTxnNumber() {
        return txnNumber;
    }

    public void setTxnNumber(int txnNumber) {
        this.txnNumber = txnNumber;
    }

    public String getMilkType() {
        return milkType;
    }

    public void setMilkType(String milkType) {
        this.milkType = milkType;
    }

    public long getlDate() {
        return lDate;
    }

    public void setlDate(long lDate) {
        this.lDate = lDate;
    }

    public String getSocId() {
        return socId;
    }

    public void setSocId(String socId) {
        this.socId = socId;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = Double.parseDouble(chooseDecimalFormat.getAmountDecimalFormat().format(bonus));
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getAwm() {
        return awm;
    }

    public void setAwm(double awm) {
        this.awm = awm;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getClr() {
        return clr;
    }

    public void setClr(double clr) {
        this.clr = clr;
    }

    public String getQuantityMode() {
        return quantityMode;
    }

    public void setQuantityMode(String quantityMode) {
        this.quantityMode = quantityMode;
    }

    public String getQualityMode() {
        return qualityMode;
    }

    public void setQualityMode(String qualityMode) {
        this.qualityMode = qualityMode;
    }

    public long getMilkAnalyserTime() {
        return milkAnalyserTime;
    }

    public void setMilkAnalyserTime(long milkAnalyserTime) {
        this.milkAnalyserTime = milkAnalyserTime;
    }

    public long getWeighingTime() {
        return weighingTime;
    }

    public void setWeighingTime(long weighingTime) {
        this.weighingTime = weighingTime;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getCalibration() {
        return calibration;
    }

    public void setCalibration(String calibration) {
        this.calibration = calibration;
    }

    public String getMaSerialNumber() {
        return maSerialNumber;
    }

    public void setMaSerialNumber(String maSerialNumber) {
        this.maSerialNumber = maSerialNumber;
    }

    public double getIncentiveRate() {
        return incentiveRate;
    }

    public void setIncentiveRate(double incentiveRate) {
        this.incentiveRate =
                Double.parseDouble(chooseDecimalFormat.getAmountDecimalFormat().format(incentiveRate));
    }

    public double getFatKg() {
        return fatKg;
    }

    public void setFatKg(double fatKg) {
        this.fatKg = fatKg;
    }

    public double getSnfKg() {
        return snfKg;
    }

    public void setSnfKg(double snfKg) {
        this.snfKg = snfKg;
    }

    public int getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(int sentStatus) {
        this.sentStatus = sentStatus;
    }

    public int getSentSmsStatus() {
        return sentSmsStatus;
    }

    public void setSentSmsStatus(int sentSmsStatus) {
        this.sentSmsStatus = sentSmsStatus;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getPostShift() {
        return postShift;
    }

    public void setPostShift(String postShift) {
        this.postShift = postShift;
    }

    public long getSequenceNum() {
        return sequenceNum;
    }

    public void setSequenceNum(long sequenceNum) {
        this.sequenceNum = sequenceNum;
    }

    public long getMiliTime() {
        return miliTime;
    }

    public void setMiliTime(long miliTime) {
        this.miliTime = miliTime;
    }

    public int getRecordCommited() {
        return recordCommited;
    }

    public void setRecordCommited(int recordCommited) {
        this.recordCommited = recordCommited;
    }

    public String getCollectionType() {
        return this.collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    public String getMilkQuality() {
        return this.milkQuality;
    }

    public void setMilkQuality(String milkQuality) {
        this.milkQuality = milkQuality;
    }

    public String getRateMode() {
        return rateMode;
    }

    public void setRateMode(String rateMode) {
        this.rateMode = rateMode;
    }

    public int getNumberOfCans() {
        return numberOfCans;
    }

    public void setNumberOfCans(int numberOfCans) {
        this.numberOfCans = numberOfCans;
    }

    public String getCenterRoute() {
        return centerRoute;
    }

    public void setCenterRoute(String centerRoute) {
        this.centerRoute = centerRoute;
    }

    public int getSampleNumber() {
        return sampleNumber;
    }

    public void setSampleNumber(int sampleNumber) {
        this.sampleNumber = sampleNumber;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }

    public long getEditedTime() {
        return editedTime;
    }

    public void setEditedTime(long editedTime) {
        this.editedTime = editedTime;
    }

    public String getOldOrNewFlag() {
        return oldOrNewFlag;
    }

    public void setOldOrNewFlag(String oldOrNewFlag) {
        this.oldOrNewFlag = oldOrNewFlag;
    }

    public long getForeignSequenceNum() {
        return foreignSequenceNum;
    }

    public void setForeignSequenceNum(long foreignSequenceNum) {
        this.foreignSequenceNum = foreignSequenceNum;
    }

    public double getKgWeight() {
        return kgWeight;
    }

    public void setKgWeight(double kgWeight) {
        this.kgWeight = kgWeight;
    }

    public double getLtrsWeight() {
        return ltrsWeight;
    }

    public void setLtrsWeight(double ltrsWeight) {
        this.ltrsWeight = ltrsWeight;
    }

    public String getRateChartName() {
        return rateChartName;
    }

    public void setRateChartName(String rateChartName) {
        this.rateChartName = rateChartName;
    }

    public long getTippingStartTime() {
        return tippingStartTime;
    }

    public void setTippingStartTime(long tippingStartTime) {
        this.tippingStartTime = tippingStartTime;
    }

    public long getTippingEndTime() {
        return tippingEndTime;
    }

    public void setTippingEndTime(long tippingEndTime) {
        this.tippingEndTime = tippingEndTime;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public int getMilkStatusCode() {
        return milkStatusCode;
    }

    public void setMilkStatusCode(int milkStatusCode) {
        this.milkStatusCode = milkStatusCode;
    }

    public int getRateCalculation() {
        return rateCalculation;
    }

    public void setRateCalculation(int rateCalculation) {
        this.rateCalculation = rateCalculation;
    }

    public String getMaName() {
        return maName;
    }

    public void setMaName(String maName) {
        this.maName = maName;
    }

    public int getSerialMa() {
        return serialMa;
    }

    public void setSerialMa(int serialMa) {
        this.serialMa = serialMa;
    }

    public double getLactose() {
        return lactose;
    }

    public void setLactose(double lactose) {
        this.lactose = lactose;
    }

    public double getSalt() {
        return salt;
    }

    public void setSalt(double salt) {
        this.salt = salt;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFreezingPoint() {
        return freezingPoint;
    }

    public void setFreezingPoint(double freezingPoint) {
        this.freezingPoint = freezingPoint;
    }

    public double getpH() {
        return pH;
    }

    public void setpH(double pH) {
        this.pH = pH;
    }

    public double getConductivity() {
        return conductivity;
    }

    public void setConductivity(double conductivity) {
        this.conductivity = conductivity;
    }

    public double getDensity() {
        return density;
    }

    public void setDensity(double density) {
        this.density = density;
    }

    public int getCollectionStatus() {
        return collectionStatus;
    }

    public void setCollectionStatus(int collectionStatus) {
        this.collectionStatus = collectionStatus;
    }

    @Override
    public Object getPrimaryKeyId() {
        return columnId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.columnId = (long) id;
    }

    public void resetSentMarkers() {
        this.sentStatus = CollectionConstants.UNSENT;
        if (AmcuConfig.getInstance().getAllowSMS()) {
            this.sentSmsStatus = CollectionConstants.UNSENT;
        } else {
            this.sentSmsStatus = CollectionConstants.SMS_NOT_ENABLE;
        }

    }

    public void resetSmsMarkers() {
        if (AmcuConfig.getInstance().getAllowSMS()) {
            this.sentSmsStatus = CollectionConstants.UNSENT;
        } else {
            this.sentSmsStatus = CollectionConstants.SMS_NOT_ENABLE;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public double getDisplayFat() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        return Double.valueOf(decimalFormat.format(this.fat));
    }

    public double getDisplaySnf() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        return Double.valueOf(decimalFormat.format(this.snf));
    }

    public double getDisplayClr() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);
        return Double.valueOf(decimalFormat.format(this.clr));
    }

    public double getDisplayProtein() {
        DecimalFormat decimalFormat =
                chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.PROTEIN);
        return Double.valueOf(decimalFormat.format(this.protein));
    }

    public double getDisplayRate() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getRateDecimalFormat();
        return Double.valueOf(decimalFormat.format(this.rate));
    }

    public double getDisplayQuantity() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getWeightDecimalFormat();
        return Double.valueOf(decimalFormat.format(this.quantity));
    }

    public double getDisplayAmount() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getAmountDecimalFormat();
        return Double.valueOf(decimalFormat.format(this.amount));
    }

    public double getRDUFat() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        return Double.valueOf(decimalFormat.format(this.fat));
    }

    public double getRDUSnf() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        return Double.valueOf(decimalFormat.format(this.snf));
    }

    public double getRDUClr() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);
        return Double.valueOf(decimalFormat.format(this.clr));
    }

    public double getRDUAwm() {
        return this.awm;
    }

    public double getRDURate() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getRateDecimalFormat();
        return Double.valueOf(decimalFormat.format(this.rate));
    }

    public double getRDUQuantity() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getWeightDecimalFormat();
        return Double.valueOf(decimalFormat.format(this.quantity));
    }

    public double getRDUAmount() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getAmountDecimalFormat();
        return Double.valueOf(decimalFormat.format(this.amount));
    }

    public double getPrinterFat() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.FAT);
        return Double.valueOf(decimalFormat.format(this.fat));
    }

    public double getPrinterSnf() {
        DecimalFormat decimalFormat =
                chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.SNF);
        return Double.valueOf(decimalFormat.format(this.snf));
    }

    public double getPrinterCLR() {
        DecimalFormat decimalFormat =
                chooseDecimalFormat.getDecimalFormatTypeForDisplay(AppConstants.CLR);
        return Double.valueOf(decimalFormat.format(this.snf));
    }

    public double getPrinterRate() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getRateDecimalFormat();
        return Double.valueOf(decimalFormat.format(this.rate));
    }

    public double getPrinterQuantity() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getWeightDecimalFormat();
        return Double.valueOf(decimalFormat.format(this.quantity));
    }

    public double getPrinterAmount() {
        double printAmount = this.amount;
        if (amcuConfig.getKeyAllowProteinValue()) {
            printAmount = getTotalAmount();
        } else if (amcuConfig.getBonusEnable() && amcuConfig.getBonusEnableForPrint()) {
            printAmount = getAmountWithBonus();
        }
        DecimalFormat decimalFormat = chooseDecimalFormat.getAmountDecimalFormat();
        return Double.valueOf(decimalFormat.format(printAmount));
    }

    public double getRateCalculationFat() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.FAT);
        return Double.valueOf(decimalFormat.format(this.fat));
    }

    public double getRateCalculationSnf() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.SNF);
        return Double.valueOf(decimalFormat.format(this.snf));
    }

    public double getRateCalculationClr() {
        DecimalFormat decimalFormat = chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.CLR);
        return Double.valueOf(decimalFormat.format(this.clr));
    }

    public double getRateCalculationProtein() {
        DecimalFormat decimalFormat =
                chooseDecimalFormat.getDecimalFormatTypeForRateChart(AppConstants.PROTEIN);
        return Double.valueOf(decimalFormat.format(this.protein));
    }

    public double getRateCalculationQuanity() {
        return Double.parseDouble(chooseDecimalFormat.getWeightDecimalFormat().format(
                this.quantity));
    }

    public double getBaseAmount() {
        return Double.parseDouble(chooseDecimalFormat.getAmountDecimalFormat().format(amount));
    }

    public double getAmountWithBonus() {
        return getBaseAmount() + getBonus();
    }

    public double getAmountWithIncentive() {
        return getBaseAmount() + getIncentiveAmount();
    }

    public double getTotalAmount() {
        return Double.parseDouble(chooseDecimalFormat.getAmountDecimalFormat().format(
                getBaseAmount() + getBonus() + getIncentiveAmount()));
    }

    public double getCalculatedClr() {

        double calculatedClr = (((amcuConfig.getSnfCons() * Double.parseDouble(
                chooseDecimalFormat.getFatAndSnfFormat().format(Double.valueOf(getSnf()))))
                - ((amcuConfig
                .getFatCons() * Double.parseDouble(
                chooseDecimalFormat.getFatAndSnfFormat().format(Double.valueOf(getFat()))))) -
                (amcuConfig
                        .getConstant())));

        return calculatedClr;
    }

    public double getCalculatedSnf() {
        double calculatedSnf = ((Double.valueOf(
                chooseDecimalFormat.getFatAndSnfFormat().format(Double.valueOf(getClr()))) +
                (amcuConfig.getFatCons() * Double.valueOf(
                        chooseDecimalFormat.getFatAndSnfFormat().format(Double.valueOf(getFat())))
                        + amcuConfig.getConstant()))
                / amcuConfig.getSnfCons());

        return calculatedSnf;

    }


    public MilkAnalyserEntity getQualityParameters() {
        MilkAnalyserEntity maEntity = new MilkAnalyserEntity();
        maEntity.fat = this.fat;
        maEntity.snf = this.snf;
        maEntity.clr = this.clr;
        maEntity.temp = this.temp;
        maEntity.protein = this.protein;
        maEntity.addedWater = this.awm;
        maEntity.lactose = this.lactose;
        maEntity.salt = this.salt;
        maEntity.freezingPoint = this.freezingPoint;
        maEntity.pH = this.pH;
        maEntity.conductivity = this.conductivity;
        this.density = maEntity.density;
//TODO: Add solids in ReportEntity
        maEntity.calibration = this.calibration;
        maEntity.serialNum = this.maSerialNumber;
//TODO: Add maMessage in ReportEntity

        return maEntity;
    }

    public void setQualityParameters(MilkAnalyserEntity maEntity) {
        this.fat = maEntity.fat;
        this.snf = maEntity.snf;
        this.clr = maEntity.clr;
        this.temp = maEntity.temp;
        this.protein = maEntity.protein;
        this.awm = maEntity.addedWater;
        this.lactose = maEntity.lactose;
        this.salt = maEntity.salt;
        this.freezingPoint = maEntity.freezingPoint;
        this.pH = maEntity.pH;
        this.conductivity = maEntity.conductivity;
        this.density = maEntity.density;
//TODO: Add solids in ReportEntity
        this.calibration = maEntity.calibration;
        this.maSerialNumber = maEntity.serialNum;
//TODO: Add maMessage in ReportEntity

        this.milkAnalyserTime = System.currentTimeMillis();
    }

    public boolean checkCurdQuality() {
        if (this.fat == 0 && this.snf == 0 && this.clr == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void setOverallCollectionStatus(int key, boolean value) {
        if (mapCollectionStatus == null) {
            mapCollectionStatus = new HashMap<>();

            mapCollectionStatus.put(SmartCCConstants.COLLECTION_STARTED, false);
            mapCollectionStatus.put(SmartCCConstants.QUALITY_DONE, false);
            mapCollectionStatus.put(SmartCCConstants.QUANTITY_DONE, false);
            mapCollectionStatus.put(SmartCCConstants.NEXT_CAN_STARTED, false);
            mapCollectionStatus.put(SmartCCConstants.PRINTER_DONE, false);
            mapCollectionStatus.put(SmartCCConstants.RDU_DONE, false);
            mapCollectionStatus.put(SmartCCConstants.SAVED_IN_DB, false);
            mapCollectionStatus.put(SmartCCConstants.SAVED_UNCOMMITTED_RECORD, false);
        }

        mapCollectionStatus.put(key, value);
    }

    public boolean isQualityDone() {
        for (Map.Entry<Integer, Boolean> entry : mapCollectionStatus.entrySet()) {
            if (entry.getKey() == SmartCCConstants.QUALITY_DONE) {
                return entry.getValue();
            }
        }

        return false;
    }

    public boolean isQuantityDone() {
        for (Map.Entry<Integer, Boolean> entry : mapCollectionStatus.entrySet()) {
            if (entry.getKey() == SmartCCConstants.QUANTITY_DONE) {
                return entry.getValue();
            }
        }

        return false;
    }

    public boolean isPrinterDone() {
        for (Map.Entry<Integer, Boolean> entry : mapCollectionStatus.entrySet()) {
            if (entry.getKey() == SmartCCConstants.PRINTER_DONE) {
                return entry.getValue();
            }
        }

        return false;
    }

    public boolean isRDUdone() {
        for (Map.Entry<Integer, Boolean> entry : mapCollectionStatus.entrySet()) {
            if (entry.getKey() == SmartCCConstants.RDU_DONE) {
                return entry.getValue();
            }
        }

        return false;
    }

    public boolean isSavedToDB() {
        for (Map.Entry<Integer, Boolean> entry : mapCollectionStatus.entrySet()) {
            if (entry.getKey() == SmartCCConstants.SAVED_IN_DB) {
                return entry.getValue();
            }
        }

        return false;
    }

    public boolean isMultipleCanStarted() {
        for (Map.Entry<Integer, Boolean> entry : mapCollectionStatus.entrySet()) {
            if (entry.getKey() == SmartCCConstants.NEXT_CAN_STARTED) {
                return entry.getValue();
            }
        }

        return false;
    }

    public boolean isUncommittedRecordSaved() {
        /*for (Map.Entry<Integer, Boolean> entry : mapCollectionStatus.entrySet()) {
            if (entry.getKey() == SmartCCConstants.SAVED_UNCOMMITTED_RECORD) {
                return entry.getValue();
            }
        }*/
        return mapCollectionStatus.get(SmartCCConstants.SAVED_UNCOMMITTED_RECORD);
    }

    public double getNewCanRecord() {
        return newCanRecord;
    }

    public void setNewCanRecord(double canWeight) {
        this.newCanRecord = canWeight;
    }


    public double getNewCanLiterData() {
        return newCanLiterData;
    }

    public void setNewCanLiterData(double canWeight) {
        this.newCanLiterData = canWeight;
    }

    public double getNewCanKgData() {
        return newCanKgData;
    }

    public void setNewCanKgData(double canWeight) {
        this.newCanKgData = canWeight;
    }

    public boolean isCLRViewEnableForUI() {
        if (amcuConfig.getChillingFATSNFCLR().equalsIgnoreCase("FC")
                || amcuConfig.getCollectionFATSNFCLR().equalsIgnoreCase("FC")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isChecked) {
        this.isSelected = isChecked;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public int getSampleType() {
        return sampleType;
    }

    public void setSampleType(int sampleType) {
        this.sampleType = sampleType;
    }

    public double getBonusRate() {
        return this.bonusRate;
    }

    public void setBonusRate(double bonusRate) {
        this.bonusRate = Double.parseDouble(chooseDecimalFormat.getRateDecimalFormat().format(
                bonusRate));
    }

    public double getDynamicRate() {
        return dynamicRate;
    }

    public void setDynamicRate(double dynamicRate) {
        this.dynamicRate = Double.parseDouble(chooseDecimalFormat.getRateDecimalFormat().format(
                dynamicRate));
    }

    public double getPrintAndReportQuantity() {
        try {
            if (!this.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC) &&
                    this.status.equalsIgnoreCase(SmartCCConstants.REJECT)
                    ) {
                return 0.00;
            } else {
                return getDisplayQuantity();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getDisplayQuantity();
        }

    }

    public double getPrintAndReportLtQuantity() {
        try {
            if (!this.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC) &&
                    this.status.equalsIgnoreCase(SmartCCConstants.REJECT)
                    ) {
                return 0.00;
            } else {
                return getLtrsWeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getLtrsWeight();
        }


    }

    public double getPrintAndReportKgQuantity() {
        try {
            if (!this.collectionType.equalsIgnoreCase(Util.REPORT_TYPE_MCC) &&
                    this.status.equalsIgnoreCase(SmartCCConstants.REJECT)
                    ) {
                return 0.00;
            } else {
                return getKgWeight();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return getKgWeight();
        }

    }


    public QualityParamsPost getQualityParams() {
        QualityParamsPost qualityParamsPost = new QualityParamsPost();

        MilkAnalyser milkAnalyser = new MilkAnalyser();
        QualityReadingData qualityReadingData = new QualityReadingData();


        qualityReadingData.fat = this.getDisplayFat();
        qualityReadingData.snf = this.getDisplaySnf();
        qualityReadingData.awm = this.getAwm();
        qualityReadingData.clr = this.getDisplayClr();
        //  qualityReadingData.com = this.co;
        qualityReadingData.density = this.getDensity();
        qualityReadingData.freezingPoint = this.getFreezingPoint();
        qualityReadingData.lactose = this.getLactose();
        qualityReadingData.pH = this.getpH();
        qualityReadingData.protein = this.getProtein();
        qualityReadingData.salt = this.getSalt();

        TemperatureData temperatureData = new TemperatureData();
        temperatureData.temperature = this.getTemp();
        temperatureData.unit = "F";

        qualityReadingData.temperature = temperatureData;

        QualityMetaData qualityMetaData = new QualityMetaData();

        qualityMetaData.calibration = this.getCalibration();
        //qualityMetaData.maData =this.getma;
        qualityMetaData.maMake = this.getMaName();
        // qualityMetaData.maNumber = this.getMi;
        qualityMetaData.serialNumber = this.getMaSerialNumber();


        milkAnalyser.qualityMetaData = qualityMetaData;
        milkAnalyser.qualityReadingData = qualityReadingData;

        qualityParamsPost.milkAnalyser = milkAnalyser;
        qualityParamsPost.measuredTime =
                ConsolidatedHelperMethods.getCollectionDateFromLongTime(this.getMilkAnalyserTime());
        qualityParamsPost.qualityTime =
                ConsolidatedHelperMethods.getCollectionDateFromLongTime(this.getMilkAnalyserTime());

        qualityParamsPost.mode = this.getQualityMode();
        qualityParamsPost.milkStatusCode = this.getMilkStatusCode();
        qualityParamsPost.milkQuality = this.getMilkQuality();

        return qualityParamsPost;
    }

    public QuantityParamspost getQuantityParams() {

        QuantityParamspost quantityParamspost = new QuantityParamspost();

        WeighingScaleData weighingScaleData = new WeighingScaleData();
        weighingScaleData.inKg = this.getKgWeight();
        weighingScaleData.inLtr = this.getLtrsWeight();
        weighingScaleData.measuredValue = this.getRateCalculationQuanity();
        //  weighingScaleData.measurementUnit = this.get;

        quantityParamspost.weighingScaleData = weighingScaleData;

        quantityParamspost.measurementTime = ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                this.getWeighingTime());
        quantityParamspost.tippingEndTime = ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                this.getTippingEndTime());
        quantityParamspost.tippingStartTime = ConsolidatedHelperMethods.getCollectionDateFromLongTime(
                this.getTippingStartTime());

        quantityParamspost.mode = this.getQuantityMode();

        return quantityParamspost;


    }

    public RateParamsPost getRateParams() {

        RateParamsPost rateParamsPost = new RateParamsPost();


        rateParamsPost.amountToBePaid = this.getTotalAmount();
        rateParamsPost.bonus = this.getBonus();
        rateParamsPost.incentive = this.getIncentiveAmount();
        rateParamsPost.isRateCalculated = DatabaseEntity.getRateCalculated(this.getRateCalculation());
        rateParamsPost.mode = this.getRateMode();
        rateParamsPost.rate = this.getRate();
        rateParamsPost.rateChartName = this.getRateChartName();
        //  rateParamsPost.unit = this.getratem;
        rateParamsPost.incentiveRate = this.getIncentiveRate();
        if (rateParamsPost.amountToBePaid == 0) {
            rateParamsPost.amountToBePaid = this.getBaseAmount();
        }


        return rateParamsPost;
    }


}