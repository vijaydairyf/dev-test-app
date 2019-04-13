package com.devapp.devmain.entity;

import com.devapp.devmain.postentities.CollectionConstants;

import java.io.Serializable;

/**
 * Created by Upendra on 11/16/2015.
 */

@TransactionalEntity
public class SalesRecordEntity implements Serializable, Entity {

    public long columnId;
    public String salesId;
    public String name;
    public double snf;
    public double fat;
    public double rate;
    public String user;
    public String manual;
    public double amount;
    public double Quantity;
    public int txnNumber;
    public String milkType;
    public String socId;
    public String time;
    public double awm;
    public String status;
    public double clr;
    public String weightManual;
    public String milkoManual;
    public String txnType;
    public String txnSubType;
    public double temperature;
    public long collectionTime;
    public int sentStatus;
    public int sentSmsStatus;
    public String postDate;
    public String postShift;
    public long sequenceNumber;

    public SalesRecordEntity() {
        this.sentStatus = CollectionConstants.UNSENT;
        this.sentSmsStatus = CollectionConstants.UNSENT;
    }

    public long getColumnId() {
        return columnId;
    }

    public void setColumnId(long columnId) {
        this.columnId = columnId;
    }

    public String getSalesId() {
        return salesId;
    }

    public void setSalesId(String salesId) {
        this.salesId = salesId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        Quantity = quantity;
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

    public String getSocId() {
        return socId;
    }

    public void setSocId(String socId) {
        this.socId = socId;
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

    public String getWeightManual() {
        return weightManual;
    }

    public void setWeightManual(String weightManual) {
        this.weightManual = weightManual;
    }

    public String getMilkoManual() {
        return milkoManual;
    }

    public void setMilkoManual(String milkoManual) {
        this.milkoManual = milkoManual;
    }

    public String getTxnType() {
        return txnType;
    }

    public void setTxnType(String txnType) {
        this.txnType = txnType;
    }

    public String getTxnSubType() {
        return txnSubType;
    }

    public void setTxnSubType(String txnSubType) {
        this.txnSubType = txnSubType;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public long getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(long collectionTime) {
        this.collectionTime = collectionTime;
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

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public Object getPrimaryKeyId() {
        return columnId;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.columnId = (long) id;
    }
}
