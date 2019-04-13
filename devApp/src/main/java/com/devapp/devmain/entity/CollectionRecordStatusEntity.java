package com.devapp.devmain.entity;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by u_pendra on 5/2/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CollectionRecordStatusEntity implements Entity, Serializable {


    private long id;
    private long seqNum;
    private long reportsTableIdx;
    private int nwSendStatus;
    private int farmerSMSStatus;
    private int shiftDataIdx;
    private String collectionType;

    public long getReportsTableIdx() {
        return reportsTableIdx;
    }

    public void setReportsTableIdx(long reportsTableIdx) {
        this.reportsTableIdx = reportsTableIdx;
    }

    public long getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(long seqNum) {
        this.seqNum = seqNum;
    }

    public int getNwSendStatus() {
        return nwSendStatus;
    }

    public void setNwSendStatus(int nwSendStatus) {
        this.nwSendStatus = nwSendStatus;
    }

    public int getFarmerSMSStatus() {
        return farmerSMSStatus;
    }

    public void setFarmerSMSStatus(int farmerSMSStatus) {
        this.farmerSMSStatus = farmerSMSStatus;
    }

    public int getShiftDataIdx() {
        return shiftDataIdx;
    }

    public void setShiftDataIdx(int shiftDataIdx) {
        this.shiftDataIdx = shiftDataIdx;
    }

    public String getCollectionType() {
        return collectionType;
    }

    public void setCollectionType(String collectionType) {
        this.collectionType = collectionType;
    }

    @Override
    public Object getPrimaryKeyId() {
        return id;
    }

    @Override
    public void setPrimaryKeyId(Object id) {
        this.id = (long) id;
    }


}
