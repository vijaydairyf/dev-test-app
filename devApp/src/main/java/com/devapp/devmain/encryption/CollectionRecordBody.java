package com.devapp.devmain.encryption;

/**
 * Created by Upendra on 4/17/2015.
 */

import java.util.HashMap;
import java.util.Map;

public class CollectionRecordBody implements SerializeableToCSV {

    public static final String CSV_VERSION = "1.2";
    public String farmerId;
    public long collectionTime;
    public String milkType;
    public double milkQuantity;
    public double fat;
    public double snf;
    public double amount;
    public double rate;
    public String mode;
    public double awm;
    public String status;
    public double bonus;
    public long tippingStartTime;
    public long tippingEndTime;
    public boolean isRateCalculated;
    private String collectionDate;
    private String farmerName;
    private String temp;
    private String societyId;
    private String shift;
    private int sequenceNumber;
    private String qualityMode;
    private String quantityMode;
    private String collectionType;

    //New fields in smartCC
    private String rateMode;
    private String milkQuality;
    private String kgQuantity;
    private String ltrQuantity;
    private int numberOfCans;
    private String collectionRoute;
    private String userId;
    private String agentId;
    private int milkStatusCode;
    private int sampleNumber;
    private int maSerialNum;
    private String maName;
    private String oldOrNew;
    private long editedTime;

    //Added params in 11.6.1
    private double clr;

    public CollectionRecordBody() {
    }

    public double getClr() {
        return clr;
    }

    public void setClr(double clr) {
        this.clr = clr;
    }

    private String getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(String id) {
        this.farmerId = id;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(long shift) {
        this.collectionTime = shift;
    }

    public double getBonus() {
        return bonus;
    }

    public void setBonus(double bonus) {
        this.bonus = bonus;
    }

    private int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int seqN) {
        this.sequenceNumber = seqN;
    }

    private String getMilkType() {
        return milkType;
    }

    public void setMilkType(String cDate) {
        this.milkType = cDate;
    }

    public double getMilkQuantity() {
        return milkQuantity;
    }

    public void setMilkQuantity(double milkQuan) {
        this.milkQuantity = milkQuan;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSnf() {
        return snf;
    }

    public void setSnf(double snf) {
        this.snf = snf;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amt) {
        this.amount = amt;
    }

    public void setCollectioinType(String cType) {
        this.collectionType = cType;
    }

    private String getCollectionType() {
        return collectionType;
    }

    private String getFarmerName() {
        return farmerName;
    }

    public void setFarmerName(String fName) {
        this.farmerName = fName;
    }

    private String getTemp() {
        return temp;
    }

    public void setTemp(String tempr) {

        this.temp = tempr;
    }

    private String getDate() {
        return collectionDate;
    }

    public void setDate(String date) {
        this.collectionDate = date;
    }

    public double getRate() {

        return rate;
    }

    public void setRate(double rat) {
        this.rate = rat;
    }

    private String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    private String getMode() {

        return mode;
    }

    public void setMode(String mod) {
        this.mode = mod;
    }

    public double getAwm() {
        return awm;
    }

    public void setAwm(double aw) {
        this.awm = aw;
    }

    private String getStatus() {
        return status;
    }

    public void setStatus(String sts) {
        this.status = sts;
    }

    private String getSocietyId() {
        return societyId;
    }

    public void setSocietyId(String socId) {
        this.societyId = socId;
    }

    public void setQualityAndQuantityMode(String qlMode, String qtMode) {
        this.qualityMode = qlMode;
        this.quantityMode = qtMode;
    }

    private String getQualityMode() {
        return qualityMode;
    }

    private String getQuantityMode() {
        return quantityMode;
    }

    private String getRateMode()

    {
        return rateMode;
    }

    public void setRateMode(String rMode) {
        this.rateMode = rMode;
    }

    private String getMilkQuality() {
        return milkQuality;
    }

    public void setMilkQuality(String mQuality) {
        this.milkQuality = mQuality;
    }

    private String getKgQuantity() {
        return kgQuantity;
    }

    public void setKgQuantity(String kgQ) {
        this.kgQuantity = kgQ;
    }

    private String getLtrQuantity() {
        return ltrQuantity;
    }

    public void setLtrQuantity(String kgQ) {
        this.ltrQuantity = kgQ;
    }

    public void setTippingTime(long startTime, long endTime) {
        this.tippingStartTime = startTime;
        this.tippingEndTime = endTime;
    }

    public void setUserAndAgent(String userId, String agentId) {
        this.userId = userId;
        this.agentId = agentId;
    }

    public void setRouteMilkStatusAndCans(String route, int status, int cans) {
        this.collectionRoute = route;
        this.milkStatusCode = status;
        this.numberOfCans = cans;

    }


    public long getTippingStartTime() {
        return this.tippingStartTime;
    }

    public long getTippingEndTime() {
        return this.tippingEndTime;
    }

    private String getUserId() {
        return this.userId;
    }

    private String getAgentId() {
        return this.agentId;
    }

    private int getNumberOfCans() {
        return this.numberOfCans;
    }

    private int getMilkStatusCode() {
        return this.milkStatusCode;
    }

    private String getCollectionRoute() {
        return this.collectionRoute;
    }

    private int getSampleNumber() {
        return this.sampleNumber;
    }

    public void setSampleNumber(int sampleNum) {
        this.sampleNumber = sampleNum;
    }

    public boolean getRateCalculatedFromDevice() {
        return this.isRateCalculated;
    }

    public void setRateCalculatedFromDevice(boolean isRateCalc) {
        this.isRateCalculated = isRateCalc;
    }

    public void setMaSerialNumber(int serialNum) {
        this.maSerialNum = serialNum;
    }

    private int getSerialNumber() {
        return maSerialNum;
    }

    private String getMilkAnalyserName() {
        return maName;
    }

    public void setMilkAnalyserName(String maName) {
        this.maName = maName;
    }

    public void setOldorNew(String flag) {
        this.oldOrNew = flag;
    }

    private String getOldOrNew() {
        return oldOrNew;
    }

    public long getEditedTime() {
        return editedTime;
    }

    public void setEditedTime(long editedTime) {
        this.editedTime = editedTime;
    }

    public String[] getColumnHeaders() {
        // TODO Auto-generated method stub
        return new String[]{"VersionNumber", "SequenceNumber", "SocietyID", "MemberId", "Member", "Date", "CollectionTime", "Shift", "MilkType",
                "Fat", "Snf", "Awm", "temperature", "MilkQuantity", "Rate", "Amount", "Mode",
                "Status", "Bonus", "QualityMode", "QuantityMode", "CollectionType", "RateMode",
                "MilkQuality", "KgQuantity", "LtrQuantity"
                , "UserId", "AgentId", "NumberOfCans", "CenterRoute", "MilkStatusCode"
                , "TippingStartTime", "TippingEndTime", "IsRateCalcFRomDevice", "SampleNumber"
                , "MASerial", "MAName", "OldOrNew", "EditedTime", "Clr"};
    }

    public String[] getValueList() {
        // TODO Auto-generated method stub
        return new String[]{CSV_VERSION, String.valueOf(getSequenceNumber()), getSocietyId(), getFarmerId(), getFarmerName(), getDate(),
                String.valueOf(getCollectionTime()), getShift(), getMilkType(),
                String.valueOf(getFat()), String.valueOf(getSnf()), String.valueOf(getAwm()), getTemp(),
                String.valueOf(getMilkQuantity()), String.valueOf(getRate()), String.valueOf(getAmount()), getMode(),
                getStatus(), String.valueOf(getBonus()), getQualityMode(), getQuantityMode(), getCollectionType(),
                getRateMode(), getMilkQuality(), getKgQuantity(), getLtrQuantity()
                , getUserId(), getAgentId(), String.valueOf(getNumberOfCans())
                , getCollectionRoute(), String.valueOf(getMilkStatusCode())
                , String.valueOf(getTippingStartTime()), String.valueOf(getTippingEndTime()),
                String.valueOf(getRateCalculatedFromDevice()), String.valueOf(getSampleNumber()),
                String.valueOf(getSerialNumber()), getMilkAnalyserName(), getOldOrNew(),
                String.valueOf(getEditedTime()), String.valueOf(getClr())
        };
    }

    public Map<String, String> getColumnMap() {

        Map<String, String> columnMap = new HashMap<String, String>();

        columnMap.put("VersionNumber", "versionNumber");
        columnMap.put("SequenceNumber", "sampleNumber");
        columnMap.put("centerId", "societyId");
        columnMap.put("FarmerId", "farmerId");
        columnMap.put("Member", "member");
        columnMap.put("CollectionDate", "collectionDate");
        columnMap.put("CollectionTime", "collectionTime");
        columnMap.put("Shift", "shift");
        columnMap.put("MilkType", "milkType");

        columnMap.put("Fat", "fat");
        columnMap.put("Snf", "snf");
        columnMap.put("Awm", "awm");
        columnMap.put("temperature", "temp");
        columnMap.put("MilkQuantity", "milkQuantity");
        columnMap.put("Rate", "rate");
        columnMap.put("Amount", "amount");
        columnMap.put("Mode", "mode");
        columnMap.put("Status", "status");
        columnMap.put("Bonus", "bonus");
        columnMap.put("QualityMode", "qualityMode");
        columnMap.put("QuantityMode", "quantityMode");
        columnMap.put("CollectionType", "collectionType");
        columnMap.put("RateMode", "rateMode");
        columnMap.put("MilkQuanlity", "milkQuality");
        columnMap.put("KgQuantity", "kgQuantity");
        columnMap.put("LtrQuantity", "ltrQuantity");

        columnMap.put("UserId", "userId");
        columnMap.put("AgentId", "agentId");
        columnMap.put("NumberOfCans", "numberOfCans");
        columnMap.put("CollectionRoute", "collectionRoute");
        columnMap.put("MilkStatusCode", "milkStatusCode");
        columnMap.put("TippingStartTime", "tippingStartTime");
        columnMap.put("TippingEndTime", "tippingEndTime");

        columnMap.put("IsRateCalcFRomDevice", "isRateCalculated");
        columnMap.put("SampleNumber", "sampleNumber");

        columnMap.put("MASerial", "maSerialNum");
        columnMap.put("MAName", "maName");
        columnMap.put("OldOrNew", "oldOrNew");
        columnMap.put("EditedTime", "editedTime");
        columnMap.put("Clr", "clr");

        return columnMap;
    }

    public String toString() {
        return "Collection record body";
    }

}
