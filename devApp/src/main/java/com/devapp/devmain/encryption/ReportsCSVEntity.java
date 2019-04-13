package com.devapp.devmain.encryption;

import java.util.HashMap;
import java.util.Map;

public class ReportsCSVEntity implements SerializeableToCSV {

    public String farmerId;
    public String farmerName;
    public String snf;
    public String fat;
    public String rate;
    public String user;
    public String manual;
    public String date;
    public String shift;
    public String amount;
    public String Quantity;
    public String taxNumber;
    public String milkType;
    public long lDate;
    public String socId;
    public String day;
    public String time;
    public String awm;
    public String status;
    public String clr;
    public String weightManual;
    public String milkoManual;
    public String milkAnalyserTime;
    public String weighingTime;
    public String Temp;
    public long miliTime;

    public ReportsCSVEntity() {
    }

    public String getFarmerId() {
        return farmerId;
    }

    public void setFarmerID(String id) {
        this.farmerId = id;
    }

    public String getFarmerName() {
        return farmerName;
    }

    public void setName(String name) {
        this.farmerName = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String Time) {
        this.time = Time;
    }

    public String getMilkType() {
        return milkType;
    }

    public void setMilkType(String mType) {
        this.milkType = mType;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getSnf() {
        return snf;
    }

    public void setSnf(String snf) {
        this.snf = snf;
    }

    public String getAWM() {
        return awm;
    }

    public void setAWM(String awm) {
        this.awm = awm;
    }

    public String getTemp() {
        return Temp;
    }

    public void setTemp(String temp) {
        this.Temp = temp;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String qty) {
        this.Quantity = qty;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMode() {
        return manual;
    }

    public void setMode(String mode) {
        this.manual = mode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String[] getColumnHeaders() {
        // TODO Auto-generated method stub
        return new String[]{"Member_ID", "Member", "Time", "Milk_Type",
                "Fat", "Snf", "AWM", "temperature", "Quantity", "Rate", "Amount",
                "Mode", "Status"};
    }

    public String[] getValueList() {
        // TODO Auto-generated method stub
        return new String[]{getFarmerId(), getFarmerName(), getTime(),
                getMilkType(), getFat(), getSnf(), getAWM(), getTemp(),
                getQuantity(), getRate(), getAmount(), getMode(), getStatus()};
    }

    public Map<String, String> getColumnMap() {

        Map<String, String> columnMap = new HashMap<String, String>();
        columnMap.put("Member_ID", "farmerId");
        columnMap.put("Member", "farmerName");
        columnMap.put("Time", "time");
        columnMap.put("Milk_Type", "milkType");
        columnMap.put("Fat", "fat");
        columnMap.put("Snf", "snf");
        columnMap.put("AWM", "awm");
        columnMap.put("temperature", "temperature");
        columnMap.put("Quantity", "Quantity");
        columnMap.put("Rate", "rate");
        columnMap.put("Amount", "amount");
        columnMap.put("Mode", "manual");
        columnMap.put("Status", "status");

        return columnMap;
    }

    public String toString() {
        return this.getFarmerId() + "," + this.getFarmerName() + ","
                + this.getTime() + "," + this.getMilkType() + ","
                + this.getFat() + "," + this.getSnf() + "," + this.getAWM()
                + "," + this.getTemp() + "," + this.getQuantity() + ","
                + this.getRate() + "," + this.getAmount() + ","
                + this.getMode() + "," + this.getStatus();
    }

}
